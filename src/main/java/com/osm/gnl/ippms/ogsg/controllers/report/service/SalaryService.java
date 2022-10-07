package com.osm.gnl.ippms.ogsg.controllers.report.service;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.MassReassignDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.report.SalaryDifferenceBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Repository
@Transactional(readOnly = true)
@Slf4j
public class SalaryService {

    private final SessionFactory sessionFactory;

    public SalaryService(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<SalaryDifferenceBean> getEmployeesWithSalaryDiffByDates(int pStartRow, int pEndRow, String pSortOrder, String pSortCriterion,
                                                                        LocalDate pStartDate, LocalDate pEndDate,
                                                                        LocalDate pStartDate2, LocalDate pEndDate2, BusinessCertificate bc)
    {
        String sql = "select c.employeeId,c.lastName,c.firstName,c.initials,m.id,m.name,m.codeName,"
                + "sc.name,s.level,s.step,a.netPay,b.netPay,a.taxesPaid,b.taxesPaid,c.id,a.id "
                + "from "+ IppmsUtils.getPaycheckTableName(bc) +" a, "+IppmsUtils.getPaycheckTableName(bc)+" b, " +
                " "+IppmsUtils.getEmployeeTableName(bc)+" c, SalaryInfo s, SalaryType sc, MdaInfo m, MdaDeptMap mdm"
                + " where a.employee.id = b.employee.id and a.employee.id = c.id and b.employee.id = c.id and m.id = mdm.mdaInfo.id and c.mdaDeptMap.id = mdm.id"
                + " and c.salaryInfo.id = s.id and s.salaryType.id = sc.id and a.netPay <> b.netPay "
                + "and b.netPay > 0 and a.netPay > 0 and a.payPeriodStart = :pStart1 "
                + "and a.payPeriodEnd = :pEnd1 and b.payPeriodStart = :pStart2 and b.payPeriodEnd = :pEnd2 "
                + "order by c.id";

        Query query = this.sessionFactory.getCurrentSession().createQuery(sql);

        query.setParameter("pStart1", pStartDate);
        query.setParameter("pEnd1", pEndDate);
        query.setParameter("pStart2", pStartDate2);
        query.setParameter("pEnd2", pEndDate2);

        if (pStartRow > 0)
            query.setFirstResult(pStartRow);
        query.setMaxResults(pEndRow);

        List<Object[]> wRetList = query.list();
        List<SalaryDifferenceBean> wRetVal = new ArrayList<SalaryDifferenceBean>();

        if ((wRetList == null) || (wRetList.isEmpty())) {
            return wRetVal;
        }
        SalaryDifferenceBean s = null;
        int i =0;
        for (Object[] o : wRetList) {
            s = new SalaryDifferenceBean();
            s.setEmployeeId((String)o[i++]);

            s.setEmployeeName(PayrollHRUtils.createDisplayName((String)o[i++], (String)o[i++], (String)o[i++]));

            s.setMdaInfo(new MdaInfo((Long)o[i++], (String)o[i++], (String)o[i++]));


            s.setSalaryScale((String)o[i++]);
            SalaryInfo si = new SalaryInfo();
            si.setLevel(((Integer)o[i++]));
            si.setStep(((Integer)o[i++]));
            s.setLevelAndStep(si.getLevelAndStepAsStr());
            s.setCurrentSalary(((Double)o[i++]));
            s.setLastSalary(((Double)o[i++]));
            s.setCurrentTaxPaid(((Double)o[i++]));
            s.setLastTaxPaid(((Double)o[i++]));
            s.setEmployee(new Employee((Long)o[i++]));
            s.setCurrentPaycheckId(((Long)o[i++]));


            wRetVal.add(s);
            i = 0;
        }

        return wRetVal;
    }

    public List<SalaryDifferenceBean> getEmployeesWithSalaryDiffByDates(int pStartRow, int pEndRow, String pSortOrder, String pSortCriterion,
                                                                        LocalDate pStartDate, LocalDate pEndDate, LocalDate pStartDate1,
                                                                        LocalDate pEndDate1, double pStartAmount, double pEndAmount, BusinessCertificate bc)
    {
        String sql = "select c.employeeId,c.lastName,c.firstName,c.initials, sc.name,s.level,s.step,a.netPay," +
                "b.netPay,a.taxesPaid,b.taxesPaid,c.id,a.id from "+IppmsUtils.getPaycheckTableName(bc)+" a, "+IppmsUtils.getPaycheckTableName(bc)+" b, "+IppmsUtils.getEmployeeTableName(bc)+" c,SalaryInfo s,"
                + " SalaryType sc where a.employee.id = b.employee.id and a.employee.id = c.id and b.employee.id = c.id  "
                + "and c.salaryInfo.id = s.id and s.salaryType.id = sc.id and a.netPay <> b.netPay and b.netPay > 0 "
                + "and a.netPay > 0 and a.payPeriodStart = :pStart1 and a.payPeriodEnd = :pEnd1 and b.payPeriodStart = :pStart2 "
                + "and b.payPeriodEnd = :pEnd2 and (b.netPay - a.netPay >= :pStartAmount and b.netPay - a.netPay <= :pEndAmount) order by c.id";

        Query query = this.sessionFactory.getCurrentSession().createQuery(sql);
        query.setParameter("pStart1", pStartDate);
        query.setParameter("pEnd1", pEndDate);
        query.setParameter("pStart2", pStartDate1);
        query.setParameter("pEnd2", pEndDate1);
        query.setParameter("pStartAmount", pStartAmount);
        query.setParameter("pEndAmount", pEndAmount);

        if (pStartRow > 0)
            query.setFirstResult(pStartRow);
        query.setMaxResults(pEndRow);

        List<Object[]> wRetList = query.list();
        List<SalaryDifferenceBean> wRetVal = new ArrayList<SalaryDifferenceBean>();

        if ((wRetList == null) || (wRetList.isEmpty())) {
            return wRetVal;
        }
        SalaryDifferenceBean s = null;
        int i = 0;
        for (Object[] o : wRetList) {
            s = new SalaryDifferenceBean();
            s.setEmployeeId((String)o[i++]);

            s.setEmployeeName(PayrollHRUtils.createDisplayName((String)o[i++], (String)o[i++], (String)o[i++]));


            s.setSalaryScale((String)o[i++]);
            SalaryInfo si = new SalaryInfo();
            si.setLevel(((Integer)o[i++]));
            si.setStep(((Integer)o[i++]));
            s.setLevelAndStep(si.getLevelAndStepAsStr());
            s.setCurrentSalary(((Double)o[i++]));
            s.setLastSalary(((Double)o[i++]));
            s.setCurrentTaxPaid(((Double)o[i++]));
            s.setLastTaxPaid(((Double)o[i++]));
            s.setEmployee(new Employee((Long)o[i++]));
            s.setCurrentPaycheckId(((Long)o[i++]));

            wRetVal.add(s);
            i = 0;
        }

        return wRetVal;
    }


    public List<SalaryDifferenceBean> getEmployeesWithSalaryDiffByDates(LocalDate pCurrentMonthStart, LocalDate pCurrentMonthEnd, LocalDate pPrevMonthStart,
                                                                        LocalDate pPrevMonthEnd, BusinessCertificate bc)
    {
        String sql = "select c.lastName,c.firstName,c.initials,c.employeeId,"
                + "m.id,m.name, m.codeName, sc.name,"
                + "s.level,s.step,a.netPay,b.netPay,a.taxesPaid,b.taxesPaid,c.id,a.id "
                + "from "+IppmsUtils.getPaycheckTableName(bc)+" a, "+IppmsUtils.getPaycheckTableName(bc)+" b, "+IppmsUtils.getEmployeeTableName(bc)+" c,SalaryInfo s, SalaryType sc, MdaDeptMap mdm, MdaInfo m "
                + "where a.employee.id = b.employee.id and a.employee.id = c.id and b.employee.id = c.id and mdm.id = c.mdaDeptMap.id and mdm.mdaInfo.id = m.id  "
                + "and c.salaryInfo.id = s.id and s.salaryType.id = sc.id and a.netPay <> b.netPay and b.netPay > 0 "
                + "and a.netPay > 0 and a.payPeriodStart = :pStart1 and a.payPeriodEnd = :pEnd1 and b.payPeriodStart = :pStart2 "
                + "and b.payPeriodEnd = :pEnd2 order by c.id";

        Query query = this.sessionFactory.getCurrentSession().createQuery(sql);

        query.setParameter("pStart1", pCurrentMonthStart);
        query.setParameter("pEnd1", pCurrentMonthEnd);
        query.setParameter("pStart2", pPrevMonthStart);
        query.setParameter("pEnd2", pPrevMonthEnd);

        List<Object[]> wRetList = query.list();
        List<SalaryDifferenceBean> wRetVal = new ArrayList<SalaryDifferenceBean>();

        if ((wRetList == null) || (wRetList.isEmpty())) {
            return wRetVal;
        }
        SalaryDifferenceBean s = null;
        int i = 0;
        for (Object[] o : wRetList) {
            s = new SalaryDifferenceBean();

            s.setEmployeeName(PayrollHRUtils.createDisplayName((String)o[i++], (String)o[i++], StringUtils.trimToEmpty((String)o[i++])));
            s.setEmployeeId((String)o[i++]);
            s.setMdaInfo(new MdaInfo((Long)o[i++],(String)o[i++], (String)o[i++]));

            s.setSalaryScale((String)o[i++]);
            SalaryInfo si = new SalaryInfo();
            si.setLevel(((Integer)o[i++]).intValue());
            si.setStep(((Integer)o[i++]).intValue());
            s.setLevelAndStep(si.getLevelAndStepAsStr());
            s.setCurrentSalary(((Double)o[i++]));
            s.setLastSalary(((Double)o[i++]));
            s.setCurrentTaxPaid(((Double)o[i++]));
            s.setLastTaxPaid(((Double)o[i++]));
            s.setId((Long)o[i++]); //employee.id
            s.setCurrentPaycheckId((Long)o[i++]);

            wRetVal.add(s);
            i = 0;
        }

        return wRetVal;
    }

    public int getTotalNoOfWithSalaryDiffByDate(LocalDate pStartDate, LocalDate pEndDate, LocalDate pPrevMonthStart, LocalDate pPrevMonthEnd,
                                                BusinessCertificate bc)
    {
        int wRetVal = 0;
        String sql = "select count(c.id)from "+IppmsUtils.getPaycheckTableName(bc)+" a, "+IppmsUtils.getPaycheckTableName(bc)+" b," +
                " "+IppmsUtils.getEmployeeTableName(bc)+" c,SalaryInfo s, SalaryType sc where a.employee.id = b.employee.id and " +
                "a.employee.id = c.id and b.employee.id = c.id  and c.salaryInfo.id = s.id and s.salaryType.id = sc.id and" +
                " a.netPay <> b.netPay and b.netPay > 0 and a.netPay > 0 and a.payPeriodStart = :pStart1 and a.payPeriodEnd = :pEnd1" +
                " and b.payPeriodStart = :pStart2 and b.payPeriodEnd = :pEnd2";

        Query query = this.sessionFactory.getCurrentSession().createQuery(sql);

        query.setParameter("pStart1", pStartDate);
        query.setParameter("pEnd1", pEndDate);
        query.setParameter("pStart2", pPrevMonthStart);
        query.setParameter("pEnd2", pPrevMonthEnd);

        List list = query.list();

        if (list == null)
            return wRetVal;
        if (list.size() == 0)
            return wRetVal;
        if ((list.size() == 1) && (((Long)list.get(0)).intValue() == 0)) {
            return wRetVal;
        }
        wRetVal = ((Long)list.get(0)).intValue();

        return wRetVal;
    }

    public int getTotalNoOfWithSalaryDiffByDate(LocalDate pStartDate, LocalDate pEndDate, LocalDate pPrevMonthStart,
                                                LocalDate pPrevMonthEnd, double pStartAmount, double pEndAmount, BusinessCertificate bc)
    {
        int wRetVal = 0;
        String sql = "select count(c.id)from "+IppmsUtils.getPaycheckTableName(bc)+" a, "+IppmsUtils.getPaycheckTableName(bc)+" b," +
                " "+IppmsUtils.getEmployeeTableName(bc)+" c,SalaryInfo s, SalaryType sc " +
                "where a.employee.id = b.employee.id and a.employee.id = c.id and b.employee.id = c.id  " +
                "and c.salaryInfo.id = s.id and s.salaryType.id = sc.id and a.netPay <> b.netPay and b.netPay > 0 and a.netPay > 0 " +
                "and a.payPeriodStart = :pStart1 and a.payPeriodEnd = :pEnd1 and b.payPeriodStart = :pStart2 " +
                "and b.payPeriodEnd = :pEnd2 and (b.netPay - a.netPay >= :pStartAmount and b.netPay - a.netPay <= :pEndAmount)";

        Query query = this.sessionFactory.getCurrentSession().createQuery(sql);

        query.setParameter("pStart1", pStartDate);
        query.setParameter("pEnd1", pEndDate);
        query.setParameter("pStart2", pPrevMonthStart);
        query.setParameter("pEnd2", pPrevMonthEnd);
        query.setParameter("pStartAmount", pStartAmount);
        query.setParameter("pEndAmount", pEndAmount);

        List list = query.list();

        if (list == null)
            return wRetVal;
        if (list.size() == 0)
            return wRetVal;
        if ((list.size() == 1) && (((Long)list.get(0)).intValue() == 0)) {
            return wRetVal;
        }
        wRetVal = ((Long)list.get(0)).intValue();

        return wRetVal;
    }

    public List<SalaryType> getSalaryTypeByIdAndClientId(BusinessCertificate bc, String pOrder)
    {
        List<SalaryType> salaryTypeList = new ArrayList<>();
        String sql = "select sc.id, sc.name, sc.createdBy.firstName, sc.createdBy.lastName, sc.creationDate, sc.lastModBy.firstName, sc.lastModBy.lastName,sc.lastModTs, sc.consolidatedInd, sc.selectableInd,count(c.id) " +
                "from " +IppmsUtils.getEmployeeTableName(bc)+" c,SalaryInfo s, SalaryType sc " +
                "where  c.salaryInfo.id = s.id and s.salaryType.id = sc.id  and c.statusIndicator = 0 and c.businessClientId = sc.businessClientId and " +
                " sc.businessClientId = :pBizId " +
                "group by sc.id, sc.name, sc.createdBy.firstName, sc.createdBy.lastName, sc.creationDate,sc.lastModBy.firstName, sc.lastModBy.lastName, sc.lastModTs, sc.consolidatedInd, sc.selectableInd" +
                " order by sc."+pOrder  ;

        Query query = this.sessionFactory.getCurrentSession().createQuery(sql);



        query.setParameter("pBizId", bc.getBusinessClientInstId());

        List<Object[]> wRetList = query.list();

        if ((wRetList == null) || (wRetList.isEmpty())) {
            return salaryTypeList;
        }
        SalaryType s = null;
        int i = 0;
        for (Object[] o : wRetList) {
            s = new SalaryType();
            s.setId((Long)o[i++]);
            s.setName((String)o[i++]);
            s.setCreatedBy(new User((String)o[i++],(String)o[i++]));
            s.setCreationDate((Timestamp)o[++i]);
            s.setLastModBy(new User((String)o[i++],(String)o[i++]));
            s.setLastModTs((Timestamp)o[++i]);
            s.setConsolidatedInd((Integer)o[i++]);
            s.setSelectableInd((Integer)o[i++]);
            s.setNoOfYearsAtRetirement(((Long)o[i++]).intValue());
            salaryTypeList.add(s);
            i = 0;
        }
        return salaryTypeList;
    }

    public HashMap<String, Long> makeSalaryLevelStepAndIdMap(BusinessCertificate bc, Long pSalTypeId) {

        HashMap<String, Long> salaryTypeList = new HashMap<>();
        String sql = "select s.level,s.step,s.id " +
                "from SalaryInfo s, SalaryType sc " +
                "where s.salaryType.id = sc.id and sc.businessClientId = s.businessClientId and sc.businessClientId = :pBizId " +
                "and sc.id = :pSalTypeIdVar" ;

        Query query = this.sessionFactory.getCurrentSession().createQuery(sql);

        query.setParameter("pBizId", bc.getBusinessClientInstId());
        query.setParameter("pSalTypeIdVar", pSalTypeId);

        List<Object[]> wRetList = query.list();

        if ((wRetList == null) || (wRetList.isEmpty())) {
            return salaryTypeList;
        }

        int i = 0;
        for (Object[] o : wRetList) {
            String key = o[i++]+":"+o[i++];
            Long value = (Long)o[i++];
            salaryTypeList.put(key,value);
            i = 0;
        }
        return salaryTypeList;
    }

    public List<MassReassignDetailsBean> getEmpForPayGroupMove(BusinessCertificate bc, Long pSalTypeId) {
        List<MassReassignDetailsBean> employees = new ArrayList<>();
        String sql = "select e.id,e.lastName,e.firstName,e.initials,m.name,s.level,s.step ,s.id,e.employeeId " +
                "from SalaryInfo s, SalaryType sc , " +IppmsUtils.getEmployeeTableName(bc)+" e, MdaInfo m, MdaDeptMap mdm "+
                "where s.salaryType.id = sc.id and sc.businessClientId = s.businessClientId and sc.businessClientId = :pBizId " +
                "and sc.id = :pSalTypeIdVar and e.statusIndicator = 0 and e.salaryInfo.id = s.id and e.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = m.id" ;

        Query query = this.sessionFactory.getCurrentSession().createQuery(sql);

        query.setParameter("pBizId", bc.getBusinessClientInstId());
        query.setParameter("pSalTypeIdVar", pSalTypeId);

        List<Object[]> wRetList = query.list();

        if ((wRetList == null) || (wRetList.isEmpty())) {
            return employees;
        }

        int i = 0;
        long eId;
        String firstName;
        String lastName;
        String initials;
        MassReassignDetailsBean bean = null;
        for (Object[] o : wRetList) {
            bean = new MassReassignDetailsBean();
            eId = (Long) o[i++];
            lastName = (String) o[i++];
            firstName = (String) o[i++];
            Object obj = o[i++];
            if(obj != null)
                initials = (String) obj;
            else
                initials = "";
            bean.setStaffName((lastName+" "+firstName+ " "+initials).trim());
            bean.setMdaName((String) o[i++]);
            bean.setLevelAndStep(o[i++]+":"+o[i++]);
            bean.setFromSalaryInfo(new SalaryInfo((Long)o[i++]));
            bean.setStaffId((String)o[i++]);
            bean.setParentId(eId);

            employees.add(bean);
            i = 0;
        }
        return employees;
    }
}
