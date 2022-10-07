package com.osm.gnl.ippms.ogsg.controllers.report.service;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckDeductionEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.beans.EmployeeTaxDeductionBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionCategory;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.report.DeductionScheduleByTSC;
import com.osm.gnl.ippms.ogsg.domain.report.GlobalDeduction;
import com.osm.gnl.ippms.ogsg.domain.tax.TaxDeductions;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckDeduction;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpDeductMiniBean;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
@Repository
@Transactional(readOnly = true)
@Slf4j
public class PaycheckDeductionService {

    private final SessionFactory sessionFactory;
    private final GenericService genericService;

    @Autowired
    public PaycheckDeductionService(SessionFactory sessionFactory, GenericService genericService) {
        this.sessionFactory = sessionFactory;
        this.genericService = genericService;
    }

    public List<AbstractPaycheckDeductionEntity> loadEmpDeductionsByParentIdAndPayPeriod(Long pDedTypeId, int pRunMonth, int pRunYear, BusinessCertificate bc) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<AbstractPaycheckDeductionEntity> list = new ArrayList<>();

        ArrayList<Object[]> wRetVal;

        String hqlQuery = "select emp.id, emp.firstName,emp.lastName,emp.employeeId,p.id,p.amount,edi.id, edt.id,"
                + " edt.name, edt.description,bb.bankInfo.name, coalesce(p.accountNumber,'-'), edc.id, edc.apportionedInd, mda.name,e.schoolInfo.id from " + IppmsUtils.getPaycheckDeductionTableName(bc) + " p, " + IppmsUtils.getPaycheckTableName(bc) + " e, "
                + "" + IppmsUtils.getEmployeeTableName(bc) + " emp, " + IppmsUtils.getDeductionInfoTableName(bc) + " edi,EmpDeductionType edt, EmpDeductionCategory edc, BankBranch bb, MdaDeptMap mdm, MdaInfo mda "
                + "where p.employeePayBean.id = e.id and e.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = mda.id "
                + "and p.employee.id = e.employee.id and e.employee.id = emp.id and edi.id = p.empDedInfo.id "
                + "and bb.branchSortCode = p.sortCode "
                + "and edi.empDeductionType.id = edt.id and edt.empDeductionCategory.id = edc.id and e.netPay > 0 and p.runMonth = :pRunMonth "
                + "and p.runYear = :pRunYear";

        if (IppmsUtils.isNotNullAndGreaterThanZero(pDedTypeId)) {
            hqlQuery += " and edt.id = :pDedTypeIdVal";
        }
        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);

        query.setParameter("pRunMonth", pRunMonth);
        query.setParameter("pRunYear", pRunYear);
        if (IppmsUtils.isNotNullAndGreaterThanZero(pDedTypeId)) {
            query.setParameter("pDedTypeIdVal", pDedTypeId);

        }

        wRetVal = (ArrayList) query.list();

        if (wRetVal.size() > 0) {
            HashMap<Long, SchoolInfo> schoolMap = this.genericService.loadObjectAsMapWithConditions(SchoolInfo.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())),"id");

            Employee e;
            AbstractPaycheckDeductionEntity p;
            AbstractDeductionEntity edi;
            EmpDeductionType edt;
            EmpDeductionCategory edc;
            for (Object[] o : wRetVal) {
                e = new Employee((Long) o[0], (String) o[1], (String) o[2]);
                e.setEmployeeId((String) o[3]);
                p = IppmsUtils.makePaycheckDeductionObject(bc);
                p.setId((Long) o[4]);

                p.setAmount(((Double) o[5]));
                edi = IppmsUtils.makeDeductionInfoObject(bc);
                edi.setId((Long) o[6]);
                edt = new EmpDeductionType((Long) o[7]);
                edt.setName((String) o[8]);
                edt.setDescription((String) o[9]);
                edt.setBankName((String) o[10]);
                edt.setAccountNumber((String) o[11]);

                edc = new EmpDeductionCategory((Long) o[12]);
                edc.setApportionedInd((Integer) o[13]);
                p.setName((String)o[14]);

                edi.setEmpDeductionType(edt);
                p.setEmpDedInfo(edi);
                p.setAbstractEmployeeEntity(e);
                p.setEmpDeductionCategory(edc);
                if(IppmsUtils.isNotNullAndGreaterThanZero((Number) o[15]))
                    p.setSchoolName(schoolMap.get(o[15]).getName());
                list.add(p);
            }

        }

        return list;
    }

    public List<PaycheckDeduction> loadEmpDeductionsByPeriod(int pRunMonth, int pRunYear, BusinessCertificate bc) {
        List<PaycheckDeduction> list = new ArrayList<>();

        ArrayList<Object[]> wRetVal;

        String hqlQuery = "select emp.id, emp.firstName,emp.lastName,emp.employeeId,p.id,p.amount,edi.id, edt.id,"
                + " edt.name, edt.description,bb.bankInfo.name, coalesce(p.accountNumber,'-'), e.id, e.monthlyBasic, e.arrears, e.otherArrears from " + IppmsUtils.getPaycheckDeductionTableName(bc) + " p, " + IppmsUtils.getPaycheckTableName(bc) + " e, "
                + "" + IppmsUtils.getEmployeeTableName(bc) + " emp, " + IppmsUtils.getDeductionInfoTableName(bc) + " edi,EmpDeductionType edt,BankBranch bb "
                + "where p.employeePayBean.id = e.id "
                + "and p.employee.id = e.employee.id and e.employee.id = emp.id and edi.id = p.empDedInfo.id "
                + "and bb.branchSortCode = p.sortCode "
                + "and edi.empDeductionType.id = edt.id and e.netPay > 0 and p.runMonth = :pRunMonth "
                + "and p.runYear = :pRunYear";

        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);

        query.setParameter("pRunMonth", pRunMonth);
        query.setParameter("pRunYear", pRunYear);


        wRetVal = (ArrayList) query.list();

        if (wRetVal.size() > 0) {
            AbstractDeductionEntity edi;
            for (Object[] o : wRetVal) {
                Employee e = new Employee((Long) o[0], (String) o[1], (String) o[2]);
                e.setEmployeeId((String) o[3]);
                PaycheckDeduction p = new PaycheckDeduction((Long) o[4]);
                p.setAmount(((Double) o[5]));
                EmployeePayBean ep = new EmployeePayBean((Long) o[12]);
                ep.setMonthlyBasic((Double) o[13]);
                ep.setArrears((Double) o[14]);
                ep.setOtherArrears((Double) o[15]);
                edi = IppmsUtils.makeDeductionInfoObject(bc);
                edi.setId((Long) o[6]);

                EmpDeductionType edt = new EmpDeductionType((Long) o[7]);
                edt.setName((String) o[8]);
                edt.setDescription((String) o[9]);
                edt.setName((String) o[10]);
                edt.setAccountNumber((String) o[11]);

                edi.setEmpDeductionType(edt);
                p.setEmpDedInfo(edi);
                p.setEmployeePayBean(ep);
                p.setEmployee(e);
                list.add(p);
            }

        }

        return list;
    }

    public double getTotalDeductions(Long pDedTypeId, int pRunMonth, int pRunYear, BusinessCertificate bc) {
        String wHql = "select coalesce(sum(p.amount),0)  " +
                "from " + IppmsUtils.getPaycheckDeductionTableName(bc) + " p," + IppmsUtils.getDeductionInfoTableName(bc) + " edi, EmpDeductionType edt" +
                " where p.empDedInfo.id = edi.id and edi.empDeductionType.id = edt.id" +
                " and p.runMonth = :pRunMonthVal and p.runYear = :pRunYearVal";


        if (pDedTypeId != null && pDedTypeId > 0) {
            wHql += " and edt.id = :pDedTypeIdVal";
        }

        Query wQuery = sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonthVal", pRunMonth);
        wQuery.setParameter("pRunYearVal", pRunYear);
        if (pDedTypeId != null && pDedTypeId > 0) {
            wQuery.setParameter("pDedTypeIdVal", pDedTypeId);
        }


        List list = wQuery.list();

        if (list != null && !list.isEmpty() && list.get(0) != null) {
            return (Double) list.get(0);
        } else {
            return 0.0D;
        }
    }

    public int getNoOfEmployeeWithDeductions(Long pDeductionId,
                                             int pRunMonth, int pRunYear, BusinessCertificate bc) {
        int wRetVal = 0;
        String wHql = "select count(distinct p.employee.id)  " +
                "from " + IppmsUtils.getPaycheckDeductionTableName(bc) + " p, " + IppmsUtils.getPaycheckTableName(bc) + " emp," + IppmsUtils.getDeductionInfoTableName(bc) + " edi,EmpDeductionType edt" +
                " where emp.runMonth = :pRunMonthVal and emp.runYear = :pRunYearVal" +
                " and emp.id = p.employeePayBean.id and p.empDedInfo.id = edi.id and edi.empDeductionType.id = edt.id";

        if (pDeductionId != null && pDeductionId > 0) {
            wHql += " and edt.id = :pDedTypeIdVal";
        }
        Query wQuery = sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonthVal", pRunMonth);
        wQuery.setParameter("pRunYearVal", pRunYear);
        if (pDeductionId != null && pDeductionId > 0) {
            wQuery.setParameter("pDedTypeIdVal", pDeductionId);
        }

        List list = wQuery.list();

        if (list != null) {
            wRetVal = Integer.parseInt(String.valueOf(list.get(0)));

        }
        return wRetVal;
    }

    public List<EmpDeductMiniBean> loadEmpDeductMiniBeanByFromDateToDate(LocalDate pFromDate, BusinessCertificate bc) {
        String wHql = "select p.amount,e.lastName,e.firstName, e.initials,e.employeeId, mda.name"
                + " ,edt.description from " + IppmsUtils.getPaycheckDeductionTableName(bc) + " p, " + IppmsUtils.getEmployeeTableName(bc) + " e, " + IppmsUtils.getPaycheckTableName(bc) + " emp," +
                "" + IppmsUtils.getDeductionInfoTableName(bc) + " edi, EmpDeductionType edt, MdaDeptMap mdm, MdaInfo mda where p.employeePayBean.id = emp.id and " +
                "emp.employee.id = e.id and edi.empDeductionType.id = edt.id and edi.id = p.empDedInfo.id " +
                "and p.runMonth = :pRunMonth and p.runYear = :pRunYear and emp.mdaDeptMap.id = mdm.id and mda.id = mdm.mdaInfo.id";

        Query wQuery = sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonth", pFromDate.getMonthValue());
        wQuery.setParameter("pRunYear", pFromDate.getYear());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if (wRetVal.size() > 0) {
            List wRetList = new ArrayList();
            int i;
            EmpDeductMiniBean p;
            for (Object[] o : wRetVal) {
                i = 0;
                p = new EmpDeductMiniBean();
                p.setCurrentDeduction(((Double) o[i++]));

                p.setName(PayrollHRUtils.createDisplayName((String) o[i++], (String) o[i++], StringUtils.trimToEmpty((String) o[i++])));
                p.setEmployeeId((String) o[i++]);
                p.setMdaName((String)o[i++]);
                p.setDeductionName((String) o[i++]);
                wRetList.add(p);
            }

            return wRetList;
        }
        return new ArrayList();
    }

    public List<EmpDeductMiniBean> loadSingleEmpDeductMiniBeanByFromDateToDate(LocalDate pFromDate, BusinessCertificate bc, Long dedTypeId) {
        String wHql = "select p.amount,e.lastName,e.firstName, e.initials,e.employeeId, emp.mdaDeptMap.id,mda.id, mda.name,mda.codeName"
                + " ,edt.description from " + IppmsUtils.getPaycheckDeductionTableName(bc) + " p, " + IppmsUtils.getEmployeeTableName(bc) + " e, " + IppmsUtils.getPaycheckTableName(bc) + " emp," +
                "" + IppmsUtils.getDeductionInfoTableName(bc) + " edi, EmpDeductionType edt, MdaDeptMap mdm, MdaInfo mda where p.employeePayBean.id = emp.id and " +
                "emp.employee.id = e.id and edi.empDeductionType.id = edt.id and edi.id = p.empDedInfo.id " +
                "and p.runMonth = :pRunMonth and p.runYear = :pRunYear and emp.mdaDeptMap.id = mdm.id and mda.id = mdm.mdaInfo.id and edt.id = :pId";

        Query wQuery = sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonth", pFromDate.getMonthValue());
        wQuery.setParameter("pRunYear", pFromDate.getYear());
        wQuery.setParameter("pId", dedTypeId);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if (wRetVal.size() > 0) {
            List wRetList = new ArrayList();
            int i;
            EmpDeductMiniBean p;
            for (Object[] o : wRetVal) {
                i = 0;
                p = new EmpDeductMiniBean();
                p.setCurrentDeduction(((Double) o[0]));

                p.setName(PayrollHRUtils.createDisplayName((String) o[1], (String) o[2], StringUtils.trimToEmpty((String) o[3])));
                p.setEmployeeId((String) o[4]);
                p.setMdaDeptMap(new MdaDeptMap((Long) o[5]));
                p.getMdaDeptMap().setMdaInfo(new MdaInfo((Long) o[6], (String) o[7], (String) o[8]));
                p.setDeductionName((String) o[9]);
                p.setMdaName((String) o[7]);
                wRetList.add(p);
            }

            return wRetList;
        }
        return new ArrayList();
    }



    public List<EmpDeductMiniBean> loadEmpDeductMiniBeanByPayPeriod(
            int pRunMonth, int pRunYear, BusinessCertificate bc) {

        String wHql = "select sum(p.amount),coalesce(p.accountNumber,'-'),b.name,bb.name,edt.description from " + IppmsUtils.getPaycheckDeductionTableName(bc) + " p, " +
                " " + IppmsUtils.getDeductionInfoTableName(bc) + " edi, EmpDeductionType edt, BankInfo b, BankBranch bb where " +
                "edi.empDeductionType.id = edt.id and edi.id = p.empDedInfo.id " +
                "and p.sortCode = bb.branchSortCode and bb.bankInfo.id = b.id and p.runMonth = :pRunMonth and p.runYear = :pRunYear" +
                " group by p.accountNumber,b.name,bb.name,edt.description";

        Query wQuery = sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if (wRetVal.size() > 0) {
            List wRetList = new ArrayList();

            EmpDeductMiniBean e;
            for (Object[] o : wRetVal) {

                e = new EmpDeductMiniBean();
                e.setDeductionAmount(((Double) o[0]));
                e.setAccountNumber((String) o[1]);
                e.setBankName((String) o[2]);
                e.setBankBranchName((String) o[3]);
                e.setDeductionName((String) o[4]);

                wRetList.add(e);
            }

            return wRetList;
        }

        return new ArrayList<>();

    }

    public List<EmpDeductMiniBean> loadTaxDeductionByRunMonthAndYear(int pStartRow, int pEndRow, String pSortOrder, String pSortCriterion,
                                                                     int pRunMonth, int pRunYear, int pToMonth, int pToYear, Long pMdaId, Long pEmpId, BusinessCertificate bc) {

        //Determine whether to sum
        boolean pMustSum = pRunMonth != pToMonth || pRunYear != pToYear;
        boolean pUseMda = IppmsUtils.isNotNullAndGreaterThanZero(pMdaId);
        boolean pUseEmpId = pEmpId > 0;

        Object[] wMapIdList = null;

        String wSumHeader = "select e.firstName,e.lastName, e.initials,e.employeeId, p.mdaDeptMap.id, m.id, m.name,m.codeName" +
                ", sum(p.taxesPaid),e.id " +
                "from " + IppmsUtils.getEmployeeTableName(bc) + " e, " + IppmsUtils.getPaycheckTableName(bc) + " p, MdaInfo m, MdaDeptMap mdm ";

        String wDoNoSumHeader = "select e.firstName,e.lastName, e.initials,e.employeeId, p.mdaDeptMap.id, " +
                "m.id, m.name,m.codeName, p.taxesPaid,e.id,p.payDate " +
                "from " + IppmsUtils.getEmployeeTableName(bc) + " e, " + IppmsUtils.getPaycheckTableName(bc) + " p, MdaInfo m, MdaDeptMap mdm";

        String wOrderByCondition = " order by e.lastName, e.firstName, e.initials";

        String wEmpOrderByCondition = " order by p.payDate";

        String wWhereClauseSum = " where ((p.runMonth >= " + pRunMonth
                + " and p.runYear = " + pRunYear + ")"
                + " or (p.runMonth <= " + pToMonth + " and p.runYear = "
                + pToYear + " ) " + "or (p.runMonth >= 0 and p.runYear > "
                + pRunYear + " and p.runYear < " + pToYear + " )) and e.mdaDeptMap.id = mdm.id and m.id = mdm.mdaInfo.id";

        String wWhereClauseSameYear = " where p.runMonth >= " + pRunMonth
                + " and p.runMonth <= " + pToMonth + " and p.runYear = "
                + pRunYear + " and e.mdaDeptMap.id = mdm.id and m.id = mdm.mdaInfo.id";

        String wWhereClauseDoNotSum = " where p.runMonth = " + pRunMonth
                + " and p.runYear = " + pRunYear + " and e.mdaDeptMap.id = mdm.id and m.id = mdm.mdaInfo.id";

        String wEndWhere = " and p.employee.id = e.id ";

        String wWhereClauseMda = "";
        String wWhereClauseEmployee = "";
        if (pUseMda) {
            wWhereClauseMda = " and m.id = :pMdaId ";
        }
        if (pUseEmpId)
            wWhereClauseEmployee = " and e.id = " + pEmpId;

        String sql = "";
        boolean wSameYear = pToYear - pRunYear == 0;
        if (pMustSum) {
            String wGroupByCondition = " group by e.firstName,e.lastName, e.initials,e.employeeId, p.mdaDeptMap.id,m.id, m.name,m.codeName,e.id";

            if (!pUseEmpId)
                sql = wSumHeader;
            else
                sql = wDoNoSumHeader;

            if (wSameYear) {
                sql += wWhereClauseSameYear;
            } else {
                sql += wWhereClauseSum;
            }
            if (pUseMda)
                sql += wWhereClauseMda;

            if (pUseEmpId)
                sql += wWhereClauseEmployee;
            sql += wEndWhere;

            if (!pUseEmpId) {
                sql += wGroupByCondition;
                sql += wOrderByCondition;
            } else {
                sql += wEmpOrderByCondition;
            }

        } else {
            sql = wDoNoSumHeader;
            sql += wWhereClauseDoNotSum;
            if (pUseMda)
                sql += wWhereClauseMda;

            if (pUseEmpId) {
                sql += wWhereClauseEmployee;
                sql += wEmpOrderByCondition;

            }
            sql += wEndWhere;

        }


        Query wQuery = sessionFactory.getCurrentSession().createQuery(sql);
        if (pUseMda)
            wQuery.setParameter("pMdaId", pMdaId);
//        if (pStartRow > 0)
//            wQuery.setFirstResult(pStartRow);
//        wQuery.setMaxResults(pEndRow);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if (wRetVal.size() > 0) {
            List wRetList = new ArrayList();
            for (Object[] o : wRetVal) {
                EmpDeductMiniBean p = new EmpDeductMiniBean();


                String pFirstName = (String) o[0];
                String pLastName = (String) o[1];
                String pInitials = null;
                if (o[2] != null) {
                    pInitials = (String) o[2];
                }

                p.setName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials));

                p.setEmployeeId((String) o[3]);

                p.setMdaDeptMap(new MdaDeptMap((Long) o[4]));
                p.getMdaDeptMap().setMdaInfo(new MdaInfo((Long) o[5], (String) o[6], (String) o[7]));
                p.setMdaName(p.getMdaDeptMap().getMdaInfo().getName());
                p.setCurrentDeduction(((Double) o[8]));
                p.setId((Long) o[9]);
                p.setEmpInstId(String.valueOf(o[9]));
                if (pUseEmpId)
                    p.setPayPeriodStr(PayrollHRUtils.getMonthYearDateFormat().format((LocalDate) o[10]));
                wRetList.add(p);
            }

            return wRetList;
        }
        return new ArrayList();
    }

    public int getTotalNoOfEmpTaxDeductionByDates(int pRunMonth,
                                                  int pRunYear, int pToMonth, int pToYear, Long pMapId, Long pEmpId, BusinessCertificate bc) {

        boolean pMustSum = pRunMonth != pToMonth && pRunYear != pToYear;
        boolean pUseMda = IppmsUtils.isNotNullAndGreaterThanZero(pMapId);
        boolean pUseEmpId = pEmpId > 0;


        String sql = "select count(p.id) " +
                "from " + IppmsUtils.getPaycheckTableName(bc) + " p, MdaDeptMap mdm, MdaInfo m ";

        String wWhereClauseSum = " where ((p.runMonth >= " + pRunMonth
                + " and p.runYear = " + pRunYear + ")"
                + " or (p.runMonth <= " + pToMonth + " and p.runYear = "
                + pToYear + " ) " + "or (p.runMonth >= 0 and p.runYear > "
                + pRunYear + " and p.runYear < " + pToYear + " ))  and mdm.id = p.mdaDeptMap.id and mdm.mdaInfo.id = m.id";

        String wWhereClauseSameYear = " where p.runMonth >= " + pRunMonth
                + " and p.runMonth <= " + pToMonth + " and p.runYear = "
                + pRunYear + "  and mdm.id = p.mdaDeptMap.id and mdm.mdaInfo.id = m.id";

        String wWhereClauseDoNotSum = " where p.runMonth = " + pRunMonth
                + " and p.runYear = " + pRunYear + "  and mdm.id = p.mdaDeptMap.id and mdm.mdaInfo.id = m.id";

        String wWhereClauseUseMda = "";
        String wWhereClauseUseEmpId = "";

        if (pUseMda) {
            wWhereClauseUseMda = " and mdm.id = p.mdaDeptMap.id and mdm.mdaInfo.id = m.id and m.id = :pMdaId ";
        }

        if (pUseEmpId)
            wWhereClauseUseEmpId = " and mdm.id = p.mdaDeptMap.id and mdm.mdaInfo.id = m.id and p.employee.id = " + pEmpId;

        boolean wSameYear = pToYear - pRunYear == 0;
        if (pMustSum) {

            if (wSameYear) {
                sql += wWhereClauseSameYear;
            } else {
                sql += wWhereClauseSum;
            }

        } else {
            sql += wWhereClauseDoNotSum;

        }
        if (pUseEmpId)
            sql += wWhereClauseUseEmpId;

        if (pUseMda)
            sql += wWhereClauseUseMda;


        Query wQuery = sessionFactory.getCurrentSession().createQuery(sql);

        if (pUseMda)
            wQuery.setParameter("pMdaId", pMapId);
        List list = wQuery.list();

        if (list == null) {
            return 0;
        }
        return ((Long) list.get(0)).intValue();
    }


    public List<EmpDeductMiniBean> loadEmpTaxDeductionsByRunMonthAndYear(
            LocalDate pFromDate, LocalDate pToDate, Long pMdaId, Long pEmpId, BusinessCertificate bc) {

        int pRunMonth = pFromDate.getMonthValue();
        int pRunYear = pFromDate.getYear();
        int pToMonth = pToDate.getMonthValue();
        int pToYear = pToDate.getYear();

        //Determine whether to sum
        boolean pMustSum = pRunMonth != pToMonth || pRunYear != pToYear;
        boolean pUseMda = IppmsUtils.isNotNullAndGreaterThanZero(pMdaId);
        boolean pUseEmpId = IppmsUtils.isNotNullAndGreaterThanZero(pEmpId);


        String wSumHeader = "select e.firstName,e.lastName, e.initials,e.employeeId, p.mdaDeptMap.id,m.name " +
                ", sum(p.taxesPaid),e.id " +
                "from " + IppmsUtils.getEmployeeTableName(bc) + " e, " + IppmsUtils.getPaycheckTableName(bc) + " p, MdaInfo m, MdaDeptMap mdm ";

        String wDoNoSumHeader = "select e.firstName,e.lastName, e.initials,e.employeeId, p.mdaDeptMap.id,m.name, " +
                " p.taxesPaid,e.id,p.runMonth,p.runYear  " +
                "from " + IppmsUtils.getEmployeeTableName(bc) + " e, " + IppmsUtils.getPaycheckTableName(bc) + " p, MdaInfo m, MdaDeptMap mdm  ";

        String wOrderByConditionNoSum = " order by e.lastName, e.firstName, e.initials,p.runMonth,p.runYear ";

        String wOrderByConditionSum = " order by e.lastName,e.firstName,e.initials ";

        String wWhereClauseSum = " where ((p.runMonth >= " + pRunMonth
                + " and p.runYear = " + pRunYear + ")"
                + " or (p.runMonth <= " + pToMonth + " and p.runYear = "
                + pToYear + " ) " + "or (p.runMonth >= 0 and p.runYear > "
                + pRunYear + " and p.runYear < " + pToYear + " ))  and e.mdaDeptMap.id = mdm.id and m.id = mdm.mdaInfo.id ";

        String wWhereClauseSameYear = " where p.runMonth >= " + pRunMonth
                + " and p.runMonth <= " + pToMonth + " and p.runYear = "
                + pRunYear + "  and e.mdaDeptMap.id = mdm.id and m.id = mdm.mdaInfo.id";

        String wWhereClauseDoNotSum = " where p.runMonth = " + pRunMonth
                + " and p.runYear = " + pRunYear + " and e.mdaDeptMap.id = mdm.id and m.id = mdm.mdaInfo.id";

        String wEndWhere = " and p.employee.id = e.id and mdm.id = p.mdaDeptMap.id and mdm.mdaInfo.id = m.id ";

        String wWhereClauseMda = "";
        String wWhereClauseEmployee = "";
        if (pUseMda) {
            wWhereClauseMda = " and m.id = :pMdaIdVar ";
        }
        if (pUseEmpId)
            wWhereClauseEmployee = " and e.id = " + pEmpId;

        String sql = "";
        boolean wSameYear = pToYear - pRunYear == 0;
        if (pMustSum) {
            String wGroupByCondition = " group by e.firstName,e.lastName, e.initials,e.employeeId, p.mdaDeptMap.id,m.name,e.id ";

            if (!pUseEmpId)
                sql = wSumHeader;
            else
                sql = wDoNoSumHeader;

            if (wSameYear) {
                sql += wWhereClauseSameYear;
            } else {
                sql += wWhereClauseSum;
            }
            if (pUseMda)
                sql += wWhereClauseMda;
            if (pUseEmpId)
                sql += wWhereClauseEmployee;

            sql += wEndWhere;
            if (!pUseEmpId)
                sql += wGroupByCondition;
            if (pUseEmpId)
                sql += wOrderByConditionNoSum;
            else
                sql += wOrderByConditionSum;
        } else {
            sql = wDoNoSumHeader;
            sql += wWhereClauseDoNotSum;
            if (pUseMda)
                sql += wWhereClauseMda;

            if (pUseEmpId)
                sql += wWhereClauseEmployee;
            sql += wEndWhere;
            sql += wOrderByConditionNoSum;

        }
        Query wQuery = sessionFactory.getCurrentSession().createQuery(sql);

        if (pUseMda)
            wQuery.setParameter("pMdaIdVar", pMdaId);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if (wRetVal.size() > 0) {
            List wRetList = new ArrayList();
            for (Object[] o : wRetVal) {
                EmpDeductMiniBean p = new EmpDeductMiniBean();


                String pFirstName = (String) o[0];
                String pLastName = (String) o[1];
                String pInitials = null;
                if (o[2] != null) {
                    pInitials = (String) o[2];
                }

                p.setName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials));

                p.setEmployeeId((String) o[3]);
                p.setMdaDeptMap(new MdaDeptMap((Long) o[4]));
                p.setMdaName((String) o[5]);
                p.setCurrentDeduction(((Double) o[6]));
                p.setId((Long) o[7]);
                p.setEmpInstId(String.valueOf(o[7]));
                if (pUseEmpId) {
                    p.setRunMonth((Integer) o[8]);
                    p.setRunYear((Integer) o[9]);
                }
                wRetList.add(p);
            }

            return wRetList;
        }
        return new ArrayList();
    }

    public List<EmployeePayBean> loadEmployeeTaxImplications(int pRunYear, BusinessCertificate bc) {

        String wSql = "select sum(p.taxesPaid), sum(p.monthlyReliefAmount), sum(p.taxableIncome), sum(p.totalPay)," +
                "sum(p.totalDeductions), p.employee.id, e.employeeId ,e.firstName, e.lastName, e.initials, ma.name" +
                " from " + IppmsUtils.getPaycheckTableName(bc) + " p, " + IppmsUtils.getEmployeeTableName(bc) + " e, MdaDeptMap m, MdaInfo ma" +
                " where p.runYear = :pRunYearVar and e.id = p.employee.id and e.mdaDeptMap.id = m.id and m.mdaInfo.id = ma.id " +
                " group by p.employee.id, e.employeeId, e.firstName, e.lastName, e.initials, ma.name" +
                " order by e.lastName,e.firstName,e.initials";


        Query wQuery = sessionFactory.getCurrentSession().createQuery(wSql);

        wQuery.setParameter("pRunYearVar", pRunYear);


        ArrayList<Object[]> wRetVal = (ArrayList) wQuery.list();
        List<EmployeePayBean> wRetList = new ArrayList<>();


        for (Object[] o : wRetVal) {
            Long wEmpId = (Long) o[5];
            EmployeePayBean p = new EmployeePayBean();
            p.setEmpInstId(wEmpId);
            p.setTaxesPaid((Double) o[0]);
            p.setReliefAmount((Double) o[1]);
            p.setTaxableIncomeYTD((Double) o[2]);
            p.setTotalPay((Double) o[3]);
            p.setTotalDeductions((Double) o[4]);
            p.setEmployeeId((String) o[6]);
            Employee wEmp = new Employee();
            wEmp.setId(wEmpId);
            wEmp.setFirstName((String) o[7]);
            wEmp.setLastName((String) o[8]);
            Object wInit = o[9];
            p.setMda((String) o[10]);
            if (wInit != null)
                wEmp.setInitials((String) wInit);
            else
                wEmp.setInitials("");

            p.setEmployee(wEmp);

            wRetList.add(p);
        }
        return wRetList;
    }

    public List<GlobalDeduction> globalDeduction(int wRunMonth, int wRunYear, BusinessCertificate bc) {


        GlobalDeduction wGD;


        String wSql1 = "select e.employeeId,e.firstName,e.lastName, d.amount,d.runMonth, i.description, g.name, d.payPeriodEnd, e.initials"
                + " from " + IppmsUtils.getPaycheckTableName(bc) + " p, " + IppmsUtils.getPaycheckDeductionTableName(bc) + " d, " + IppmsUtils.getEmployeeTableName(bc) + " e, " + IppmsUtils.getDeductionInfoTableName(bc) + " i, MdaInfo g, MdaDeptMap a"
                + " where p.runMonth = "
                + wRunMonth
                + " and p.runYear = "
                + wRunYear
                + ""
                + " and p.id = d.employeePayBean.id"
                + " and p.mdaDeptMap.id = a.id "
                + " and a.mdaInfo.id = g.id"
                + " and p.employee.id = e.id and p.netPay > 0"
                + " and d.empDedInfo.id = i.id"
                + " order by i.description, g.name, e.lastName,e.firstName";

        Query wQuery = sessionFactory.getCurrentSession().createQuery(wSql1);
        ArrayList<Object[]> wRetVal = (ArrayList) wQuery.list();
        List<GlobalDeduction> wCol = new ArrayList<>();

        for (Object[] o : wRetVal) {
            String eId = (String) o[0];
            String firstName = (String) o[1];
            String lastName = (String) o[2];
            Double amount = (Double) o[3];
            String deductionName = (String) o[5];
            String agency = (String) o[6];
            LocalDate period = (LocalDate) o[7];
            String initials = PayrollHRUtils.treatNull((String) o[8]);
            String sName = PayrollHRUtils.createDisplayName(lastName,
                    firstName, initials);
            wGD = new GlobalDeduction(sName, eId, amount, deductionName,
                    agency, period, " ");
            wCol.add(wGD);
        }
        return wCol;
    }

    public List<DeductionScheduleByTSC> deductionByTsc(int wRunMonth, int wRunYear, BusinessCertificate bc)
            throws Exception {

        DeductionScheduleByTSC wDBA;
        String wSql1 = "select e.employeeId,e.firstName,e.lastName, d.amount,d.runMonth, i.description, g.name,"
                + " d.payPeriodEnd, e.initials,s.name"
                + " from " + IppmsUtils.getPaycheckTableName(bc) + " p, " + IppmsUtils.getPaycheckDeductionTableName(bc) + " d, " + IppmsUtils.getEmployeeTableName(bc) + " e, "
                + "" + IppmsUtils.getDeductionInfoTableName(bc) + " i, MdaInfo g, MdaDeptMap a, SchoolInfo s"
                + " where p.runMonth = "
                + wRunMonth
                + " and p.runYear = "
                + wRunYear
                + ""
                + " and p.schoolInfo.id is not null"
                + " and p.id = d.employeePayBean.id"
                + " and p.mdaDeptMap.id = a.id "
                + " and a.mdaInfo.id = g.id"
                + " and p.schoolInfo.id = s.id"
                + " and d.employee.id = e.id"
                + " and d.empDedInfo.id = i.id and p.netPay > 0"
                + " order by s.name, i.description, e.lastName, e.firstName";
        Query wQuery = sessionFactory.getCurrentSession().createQuery(wSql1);
        ArrayList<Object[]> wRetVal = (ArrayList) wQuery.list();
        List<DeductionScheduleByTSC> wCol = new ArrayList<>();
        for (Object[] o : wRetVal) {
            String eID = (String) o[0];
            String firstName = (String) o[1];
            String lastName = (String) o[2];

            Double amount = (Double) o[3];
            String desc = (String) o[5];
            String agency = (String) o[6];
            LocalDate period = (LocalDate) o[7];
            String initials = PayrollHRUtils.treatNull((String) o[8]);
            String schoolName = PayrollHRUtils.treatNull((String) o[9]);
            wDBA = new DeductionScheduleByTSC(agency, schoolName, desc,
                    PayrollHRUtils.createDisplayName(lastName, firstName,
                            initials), eID, amount, period);
            wCol.add(wDBA);
        }


        return wCol;
    }


    public List<DeductionScheduleByTSC> singleDeductionByTsc(int wRunMonth, int wRunYear, BusinessCertificate bc, Long dedTypeId)
            throws Exception {

        DeductionScheduleByTSC wDBA;
        String wSql1 = "select e.employeeId,e.firstName,e.lastName, d.amount,d.runMonth, i.description, g.name,"
                + " d.payPeriodEnd, e.initials,s.name"
                + " from " + IppmsUtils.getPaycheckTableName(bc) + " p, " + IppmsUtils.getPaycheckDeductionTableName(bc) + " d, " + IppmsUtils.getEmployeeTableName(bc) + " e, "
                + "" + IppmsUtils.getDeductionInfoTableName(bc) + " i, EmpDeductionType edt, MdaInfo g, MdaDeptMap a, SchoolInfo s"
                + " where p.runMonth = "
                + wRunMonth
                + " and p.runYear = "
                + wRunYear
                + ""
                + " and p.schoolInfo.id is not null"
                + " and p.id = d.employeePayBean.id"
                + " and p.mdaDeptMap.id = a.id "
                + " and a.mdaInfo.id = g.id"
                + " and p.schoolInfo.id = s.id"
                + " and d.employee.id = e.id"
                + " and d.empDedInfo.id = i.id and p.netPay > 0 and i.empDeductionType.id = edt.id and edt.id = "+dedTypeId+""
                + " order by s.name, i.description, e.lastName, e.firstName";
        Query wQuery = sessionFactory.getCurrentSession().createQuery(wSql1);
        ArrayList<Object[]> wRetVal = (ArrayList) wQuery.list();
        List<DeductionScheduleByTSC> wCol = new ArrayList<>();
        for (Object[] o : wRetVal) {
            String eID = (String) o[0];
            String firstName = (String) o[1];
            String lastName = (String) o[2];

            Double amount = (Double) o[3];
            String desc = (String) o[5];
            String agency = (String) o[6];
            LocalDate period = (LocalDate) o[7];
            String initials = PayrollHRUtils.treatNull((String) o[8]);
            String schoolName = PayrollHRUtils.treatNull((String) o[9]);
            wDBA = new DeductionScheduleByTSC(agency, schoolName, desc,
                    PayrollHRUtils.createDisplayName(lastName, firstName,
                            initials), eID, amount, period);
            wCol.add(wDBA);
        }


        return wCol;
    }




    public List<TaxDeductions> loadEmployeeTaxDeductionByRunMonthAndYear(
            int pStartRow, int pEndRow, String pSortOrder, String pSortCriterion,
            int pRunMonth, int pRunYear, int pToMonth, int pToYear, Long pMapId, Long pEmpId, BusinessCertificate bc) {


        //Determine whether to sum
        //boolean pMustSum = pRunMonth != pToMonth && pRunYear != pToYear;
        boolean pUseMda = IppmsUtils.isNotNullAndGreaterThanZero(pMapId);
        boolean pUseEmpId = IppmsUtils.isNotNullAndGreaterThanZero(pEmpId);


        String wSumHeader = "select p.runMonth,p.runYear,p.payDate, m.id," +
                "p.taxesPaid " +
                "from " + IppmsUtils.getPaycheckTableName(bc) + " p, MdaInfo m, MdaDeptMap mdm where p.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = m.id ";


        String wOrderByCondition = " order by p.runMonth,p.runYear";

        String wWhereClauseSum = " and ((p.runMonth >= " + pRunMonth
                + " and p.runYear = " + pRunYear + ")"
                + " or (p.runMonth <= " + pToMonth + " and p.runYear = "
                + pToYear + " ) " + "or (p.runMonth >= 0 and p.runYear > "
                + pRunYear + " and p.runYear < " + pToYear + " )) ";

        String wWhereClauseSameYear = " and p.runMonth >= " + pRunMonth
                + " and p.runMonth <= " + pToMonth + " and p.runYear = "
                + pRunYear;


        String wWhereClauseMda = "";
        String wWhereClauseEmployee = "";
        if (pUseMda) {
            wWhereClauseMda = " and m.id = :pMapppedIds ";
        }
        if (pUseEmpId)
            wWhereClauseEmployee = " and p.employee.id = " + pEmpId;

        String sql = "";
        boolean wSameYear = pToYear - pRunYear == 0;

        sql = wSumHeader;

        if (wSameYear) {
            sql += wWhereClauseSameYear;
        } else {
            sql += wWhereClauseSum;
        }
        if (pUseMda)
            sql += wWhereClauseMda;

        if (pUseEmpId)
            sql += wWhereClauseEmployee;

        sql += wOrderByCondition;

        Query wQuery = sessionFactory.getCurrentSession().createQuery(sql);
        if (pUseMda)
            wQuery.setParameter("pMapppedIds", pMapId);
        if (pStartRow > 0)
            wQuery.setFirstResult(pStartRow);
        wQuery.setMaxResults(pEndRow);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if (wRetVal.size() > 0) {
            List wRetList = new ArrayList();
            for (Object[] o : wRetVal) {
                TaxDeductions p = new TaxDeductions();

                p.setRunMonth((Integer) o[0]);
                p.setRunYear((Integer) o[1]);
                p.setPayPeriodEnd((LocalDate) o[2]);
                p.setMdaInstId((Long) o[3]);
                p.setAmount((Double) o[4]);

                wRetList.add(p);
            }

            return wRetList;
        }
        return new ArrayList();

    }

    public List<EmployeeTaxDeductionBean> taxDeduction(String pPayPeriod, String empId, BusinessCertificate bc)
            throws Exception {
        // int wRunYear = Integer.parseInt(pPayPeriod);
        LocalDate payPeriod = PayrollBeanUtils.getLocalDateFromString(pPayPeriod);
        int wRunYear = payPeriod.getYear();

        Long wEmpId = Long.parseLong(empId);

        List<EmployeeTaxDeductionBean> wCol = new ArrayList<>();

        String wSql1 = "select e.firstName, e.lastName, e.initials , e.employeeId, a.name, p.taxesPaid, p.payDate"
                + " from " + IppmsUtils.getEmployeeTableName(bc) + " e, " + IppmsUtils.getPaycheckTableName(bc) + " p, MdaInfo a, MdaDeptMap d"
                + " where e.id = p.employee.id" + " and p.runYear = " + wRunYear + ""
                + " and e.id = " + wEmpId + "" + " and p.mdaDeptMap.id = d.id"
                + " and d.mdaInfo.id = a.id" + " order by p.payDate";


        Query wQuery = sessionFactory.getCurrentSession().createQuery(wSql1);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if (wRetVal.size() > 0) {
            List wRetList = new ArrayList();
            for (Object[] o : wRetVal) {
                EmployeeTaxDeductionBean wETB = new EmployeeTaxDeductionBean();
                String name = PayrollHRUtils.createDisplayName((String) o[1], (String) o[0], (String) o[2]);
                String eid = (String) o[3];
                String agency = (String) o[4];
                Double taxes = (Double) o[5];
                LocalDate period = (LocalDate) o[6];

                wETB = new EmployeeTaxDeductionBean();
                wETB.setAgency(agency);
                wETB.setAmount(taxes);
                wETB.setEmployeeId(eid);
                wETB.setEmployeeName(name);
                wETB.setPayPeriod(period);
                wCol.add(wETB);
            }

            return wCol;
        }
        return new ArrayList<>();
    }


}
