package com.osm.gnl.ippms.ogsg.controllers.report.service;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import com.osm.gnl.ippms.ogsg.domain.report.LoanSchedule;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpDeductMiniBean;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpGarnMiniBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.*;

@Service
@Repository
@Transactional(readOnly = true)
@Slf4j
public class GarnishmentService {

    /**
     * Kasumu Taiwo
     * 12-2020
     */


    private final SessionFactory sessionFactory;
    private final GenericService genericService;
    @Autowired
    public GarnishmentService(SessionFactory sessionFactory, GenericService genericService) {
        this.sessionFactory = sessionFactory;
        this.genericService = genericService;
    }

    public List<AbstractPaycheckGarnishmentEntity> loadEmpGarnishmentsByParentIdAndPayPeriod(Long pGarnTypeId, int pRunMonth, int pRunYear,
                                                                                             BusinessCertificate bc) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<AbstractPaycheckGarnishmentEntity> l = new ArrayList<>();
        ArrayList<Object[]> wRetVal;

        String hqlQuery = "select emp.id, emp.firstName,emp.lastName,emp.employeeId,p.id," +
                "p.amount,edi.id, edt.id, edt.name, edt.description,edi.owedAmount,bb.bankInfo.name, coalesce(p.accountNumber,'-'), p.interestPaid, mda.name,e.schoolInfo.id " +
                "from " + IppmsUtils.getPaycheckGarnishmentTableName(bc) + " p, " + IppmsUtils.getPaycheckTableName(bc) + " e, " + IppmsUtils.getEmployeeTableName(bc) + " emp, BankBranch bb, " +
                ""+IppmsUtils.getGarnishmentInfoTableName(bc)+" edi,EmpGarnishmentType edt, MdaDeptMap mdm, MdaInfo mda  " +
                "where emp.id = e.employee.id and e.id = p.employeePayBean.id and e.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = mda.id "
                + "and p.sortCode = bb.branchSortCode " +
                "and edi.id = p.empGarnInfo.id " +
                "and edi.empGarnishmentType.id = edt.id and e.netPay > 0 " +
                "and p.runMonth = :pRunMonth and p.runYear = :pRunYear ";

        if (pGarnTypeId != null && pGarnTypeId > 0) {
            hqlQuery += "and edt.id = :pGarnTypeIdVal";
        }

        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);

        query.setParameter("pRunMonth", pRunMonth);
        query.setParameter("pRunYear", pRunYear);
        if (pGarnTypeId != null && pGarnTypeId > 0) {
            query.setParameter("pGarnTypeIdVal", pGarnTypeId);
        }

        wRetVal = (ArrayList) query.list();
        Employee e;AbstractGarnishmentEntity edi;
        EmpGarnishmentType edt;
        AbstractPaycheckGarnishmentEntity p;

        if (wRetVal.size() > 0) {
            HashMap<Long, SchoolInfo> schoolMap = this.genericService.loadObjectAsMapWithConditions(SchoolInfo.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())),"id");
            for (Object[] o : wRetVal) {
                 e = new Employee((Long) o[0], (String) o[1], (String) o[2]);
                e.setEmployeeId((String) o[3]);
                p = IppmsUtils.makePaycheckGarnishmentObject(bc);
                p.setEmployee(e);
                p.setId((Long)o[4]);
                p.setAmount(((Double) o[5]));
                edi = IppmsUtils.makeGarnishmentInfoObject(bc);
                edi.setId((Long) o[6]);
                edt = new EmpGarnishmentType((Long) o[7]);
                edt.setName((String) o[8]);
                edt.setDescription((String) o[9]);
                edi.setOwedAmount(((Double) o[10]));
                edt.setDisplayName((String) o[11]); //stores Bank or Branch Name
                edt.setAccountNumber((String) o[12]);
                edi.setEmpGarnishmentType(edt);
                p.setEmpGarnInfo(edi);
                p.setAccountNumber((String) o[12]);
                p.setInterestPaid((Double) o[13]);
                p.setMdaName((String)o[14]);
               if(IppmsUtils.isNotNullAndGreaterThanZero((Number) o[15]))
                  p.setSchoolName(schoolMap.get(o[15]).getName());
                l.add(p);
            }
        }
       if(!l.isEmpty())
           Collections.sort(l, Comparator.comparing(AbstractPaycheckGarnishmentEntity::getEmployeeName));
        return l;
    }

    public int getNoOfEmployeesWithLoanDeductions(Long pGarnTypeId, int pRunMonth, int pRunYear, BusinessCertificate bc) {
        int wRetVal = 0;
        String wHql = "select count(distinct p.employee.id)  " +
                "from " + IppmsUtils.getPaycheckGarnishmentTableName(bc) + " p, " + IppmsUtils.getPaycheckTableName(bc) + " emp,"+IppmsUtils.getGarnishmentInfoTableName(bc)+" edi,EmpGarnishmentType edt" +
                " where emp.runMonth = :pRunMonthVal and emp.runYear = :pRunYearVal" +
                " and emp.id = p.employeePayBean.id and p.empGarnInfo.id = edi.id and edi.empGarnishmentType.id = edt.id";

        if (pGarnTypeId != null && pGarnTypeId > 0) {
            wHql += " and edt.id = :pGarnTypeIdVal";
        }
        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonthVal", pRunMonth);
        wQuery.setParameter("pRunYearVal", pRunYear);
        if (pGarnTypeId != null && pGarnTypeId > 0) {
            wQuery.setParameter("pGarnTypeIdVal", pGarnTypeId);
        }

        List list = wQuery.list();

        if (list != null) {
            wRetVal = Integer.parseInt(String.valueOf(list.get(0)));

        }
        return wRetVal;
    }

    public double getTotalGarnishments(Long pGarnTypeId, int pRunMonth, int pRunYear, BusinessCertificate bc) {
        String wHql = "select coalesce(sum(p.amount),0)  " +
                "from " + IppmsUtils.getPaycheckGarnishmentTableName(bc) + " p, " + IppmsUtils.getPaycheckTableName(bc) + " emp,"+IppmsUtils.getGarnishmentInfoTableName(bc)+" edi,EmpGarnishmentType edt " +
                "  where emp.runMonth = :pRunMonthVal and emp.runYear = :pRunYearVal" +
                " and emp.id = p.employeePayBean.id and p.empGarnInfo.id = edi.id" +
                " and edi.empGarnishmentType.id = edt.id ";

        if (pGarnTypeId != null && pGarnTypeId > 0) {
            wHql += " and edt.id = :pGarnTypeIdVal";
        }

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonthVal", pRunMonth);
        wQuery.setParameter("pRunYearVal", pRunYear);
        if (IppmsUtils.isNotNullAndGreaterThanZero(pGarnTypeId)) {
            wQuery.setParameter("pGarnTypeIdVal", pGarnTypeId);
        }


        List list = wQuery.list();

        if (list != null && !list.isEmpty() && list.get(0) != null) {
            return (Double) list.get(0);
        } else {
            return 0.0D;
        }
    }

    public List<EmpGarnishmentType> findEmpGarnishmentsByBusinessClient(BusinessCertificate bc) {
        List wRetList = new ArrayList();
        String hqlQuery = "select gt.id,gt.name, gt.description from "+IppmsUtils.getGarnishmentInfoTableName(bc)+" g, EmpGarnishmentType gt, BusinessClient b "
                + "where g.empGarnishmentType.id = gt.id and gt.businessClientId = b.id  group by gt.id,gt.name,gt.description";

        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                EmpGarnishmentType e = new EmpGarnishmentType();
                e.setId((Long) o[0]);
                e.setName((String) o[1]);
                e.setDescription((String) o[2]);
                wRetList.add(e);
            }
        }

        return wRetList;
    }

    public List<EmpGarnMiniBean> loadEmpGarnMiniBeanByFromDateToDateAndType(
            LocalDate pFromDate, Long pGarnTypeId, BusinessCertificate bc) {
        String wHql = "select p.amount,e.firstName,e.lastName, e.initials,e.employeeId, emp.mdaDeptMap.id, mda.id,mda.name, mda.codeName,edt.id " +
                "from " + IppmsUtils.getPaycheckGarnishmentTableName(bc) + " p, " + IppmsUtils.getEmployeeTableName(bc) + " e, " + IppmsUtils.getPaycheckTableName(bc) + " emp, MdaDeptMap mdm, MdaInfo mda," +
                ""+IppmsUtils.getGarnishmentInfoTableName(bc)+" edi, EmpGarnishmentType edt where p.employeePayBean.id = emp.id and " +
                "emp.employee.id = e.id and edi.empGarnishmentType.id = edt.id and edi.id = p.empGarnInfo.id " +
                "and p.runMonth = :pRunMonth and p.runYear = :pRunYear and edt.id = :pLoanType "
                + " and mdm.id = emp.mdaDeptMap.id and mdm.mdaInfo.id = mda.id ";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonth", pFromDate.getMonthValue());
        wQuery.setParameter("pRunYear", pFromDate.getYear());
        wQuery.setParameter("pLoanType", pGarnTypeId);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if (wRetVal.size() > 0) {
            List<EmpGarnMiniBean> wRetList = new ArrayList<>();
            for (Object[] o : wRetVal) {
                EmpGarnMiniBean p = new EmpGarnMiniBean();
                p.setCurrentGarnishment(((Double) o[0]));

                String pFirstName = (String) o[1];
                String pLastName = (String) o[2];
                String pInitials = null;
                if (o[3] != null) {
                    pInitials = (String) o[3];
                }

                p.setName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials));
                p.setEmployeeId((String) o[4]);
                p.setMdaDeptMap(new MdaDeptMap((Long) o[5]));
                p.getMdaDeptMap().setMdaInfo(new MdaInfo((Long) o[6], (String) o[7], (String) o[8]));

                p.setId((Long) o[9]);
                wRetList.add(p);
            }

            return wRetList;
        }
        return new ArrayList();
    }

    public List<EmpGarnMiniBean> loadEmpGarnMiniBeanByFromDateToDate(LocalDate pPayPeriodStart, BusinessCertificate bc) {
        String wHql = "select p.amount,e.firstName,e.lastName, e.initials,e.employeeId, "
                + "mda.id, mda.name,edt.id, edi.owedAmount,edi.startDate,edi.loanTerm,edi.endDate, edt.name "
                + "from " + IppmsUtils.getPaycheckGarnishmentTableName(bc) + " p, " + IppmsUtils.getEmployeeTableName(bc) + " e, " + IppmsUtils.getPaycheckTableName(bc) + " emp,"+IppmsUtils.getGarnishmentInfoTableName(bc)+" edi, MdaInfo mda, MdaDeptMap mdm, "
                + "EmpGarnishmentType edt where p.employeePayBean.id = emp.id and emp.employee.id = e.id "
                + "and edi.empGarnishmentType.id = edt.id and edi.id = p.empGarnInfo.id and emp.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = mda.id "
                + "and p.runMonth = :pRunMonth and p.runYear = :pRunYear";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonth", pPayPeriodStart.getMonthValue());
        wQuery.setParameter("pRunYear", pPayPeriodStart.getYear());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if (wRetVal.size() > 0) {
            List wRetList = new ArrayList();
            for (Object[] o : wRetVal) {
                EmpGarnMiniBean p = new EmpGarnMiniBean();
                p.setCurrentGarnishment(((Double) o[0]));

                String pFirstName = (String) o[1];
                String pLastName = (String) o[2];
                String pInitials = StringUtils.trimToEmpty((String) o[3]);


                p.setName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials));
                p.setEmployeeId((String) o[4]);
                p.setMdaInstId((Long) o[5]);
                p.setMdaName((String) o[6]);
                p.setId((Long) o[7]);
                p.setGarnishmentName((String) o[12]);
                p.setYearToDate(((Double) o[8]));
                p.setStartDate((LocalDate) o[9]);
                p.setLoanTerm(((Integer) o[10]).intValue());
                if (o[11] != null)
                    p.setEndDate((LocalDate) o[11]);
                else {
                    p.setEndDate(null);
                }

                if (p.getEndDate() == null) {
                    p.setEndDate(PayrollUtils.makeEndDate(p.getStartDate(), p.getLoanTerm()));
                }
                wRetList.add(p);
            }

            return wRetList;
        }
        return new ArrayList();
    }

    public List<LoanSchedule> loanScheduleTSC(int wRunMonth, int wRunYear, BusinessCertificate bc)
            throws Exception {
        LoanSchedule wLTSC;
        List<LoanSchedule> wCol = new ArrayList<>();

        String wSql = "select e.employeeId, e.firstName,e.lastName, d.amount,i.owedAmount, d.runMonth,d.payPeriodEnd, i.description,s.name, p.name,e.initials"
                + " from " + IppmsUtils.getPaycheckTableName(bc) + " pi, " + IppmsUtils.getPaycheckGarnishmentTableName(bc) + " d, " + IppmsUtils.getEmployeeTableName(bc) + " e, "+IppmsUtils.getGarnishmentInfoTableName(bc)+" i,MdaDeptMap pdm, "
                + "MdaInfo p, SchoolInfo s"
                + " where pi.schoolInfo.id = s.id and pi.schoolInfo.id is not null"
                + " and pi.runMonth = " + wRunMonth + " and pi.runYear = " + wRunYear + ""
                + " and pi.id = d.employeePayBean.id" + " and pi.mdaDeptMap.id = pdm.id "
                + " and pdm.mdaInfo.id = p.id" + " and pi.employee.id = e.id"
                + " and d.empGarnInfo.id = i.id and pi.netPay > 0"
                + " order by s.name,i.description, e.lastName,e.firstName";


        Query query = this.sessionFactory.getCurrentSession().createQuery(wSql);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                String eID = ((String) o[0]);
                String firstName = ((String) o[1]);
                String lastName = ((String) o[2]);
                String amount = (String.valueOf(o[3]));
                String balance = (String.valueOf(o[4]));
                LocalDate period = ((LocalDate) o[6]);
                String description = ((String) o[7]);
                String school = ((String) o[8]);
                String agency = ((String) o[9]);
                String initials = PayrollHRUtils.treatNull((String) o[10]);
                String sName = PayrollHRUtils.createDisplayName(lastName, firstName, initials);
                wLTSC = new LoanSchedule(agency, school, description, sName, eID, amount, balance, period);
                wCol.add(wLTSC);
            }
        }
        return wCol;
    }

    public List<EmpDeductMiniBean> loadEmpDeductMiniBeanForLoanByPayPeriodList(
            int pRunMonth, int pRunYear, BusinessCertificate bc) {

        List<EmpDeductMiniBean> wRetList = new ArrayList<>();
        EmpDeductMiniBean wEDMB;

        String wHql = "select sum(p.amount),edt.id,b.defaultInd, coalesce(p.accountNumber,'-') "
                + ",b.id,b.name,bb.id, bb.name,edt.name,edt.description, p.payPeriodStart from " + IppmsUtils.getPaycheckGarnishmentTableName(bc) + " p, "
                + ""+IppmsUtils.getGarnishmentInfoTableName(bc)+" edi, EmpGarnishmentType edt, BankInfo b, BankBranch bb where "
                + "edi.empGarnishmentType.id = edt.id and edi.id = p.empGarnInfo.id "
                + "and p.sortCode = bb.branchSortCode and bb.bankInfo.id = b.id and p.runMonth = :pRunMonth and p.runYear = :pRunYear"
                + " group by edt.id,b.defaultInd,p.accountNumber,b.id,b.name,bb.id,bb.name,edt.name,edt.description, p.payPeriodStart";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                wEDMB = new EmpDeductMiniBean();
                wEDMB.setAccountNumber((String) o[3]);
                wEDMB.setDeductionName((String) o[9]);
                wEDMB.setDeductionAmount((Double) o[0]);
                if (((Integer) o[2]).intValue() == IConstants.ON) {
                    wEDMB.setBankBranchName("No Bank Branch");
                    wEDMB.setBankName("No Bank");
                } else {
                    wEDMB.setBankBranchName((String) o[7]);
                    wEDMB.setBankName((String) o[5]);
                }
                wEDMB.setPeriod((LocalDate) o[10]);

                wRetList.add(wEDMB);
            }
        }

        return wRetList;
    }

}
