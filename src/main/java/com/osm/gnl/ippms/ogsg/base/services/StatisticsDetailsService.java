/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service("statsDetailsService")
@Repository
@Transactional(readOnly = true)
public class StatisticsDetailsService {
    @Autowired
    private GenericService genericService;
    @Autowired
    private SessionFactory sessionFactory;

    private Query makeQuery(String wHql) {
        return sessionFactory.getCurrentSession().createQuery(wHql);
    }

    public NamedEntityBean loadPaycheckSummaryInfoByMonthAndYear(BusinessCertificate businessCertificate, int pRunMonth, int pRunYear) {
        String wHqlStr = "select coalesce(sum(p.netPay),0), coalesce(sum(p.totalPay),0),coalesce(sum(p.totalDeductions),0),count(p.employee.id) " +
                "from " + IppmsUtils.getPaycheckTableName(businessCertificate) + " p   " +
                "where  p.runMonth = :pRunMonth and p.runYear = :pRunYear " +
                "and p.netPay > 0 ";

        Query wQuery = makeQuery(wHqlStr);

        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);


        NamedEntityBean wRetList = new NamedEntityBean();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {
            for (Object[] o : wRetVal) {

                wRetList.setNetPay((Double) o[0]);
                wRetList.setTotalPay((Double) o[1]);
                wRetList.setTotalDeductions((Double) o[2]);
                wRetList.setNoOfActiveEmployees(((Long) o[3]).intValue());

            }

        }

        return wRetList;
    }

    public List<NamedEntityBean> loadEmployeesPaid(BusinessCertificate businessCertificate, int pRunMonth, int pRunYear) {


        String wHqlStr = "select  e.id,p.lastName,p.firstName,p.initials,s.id,s.level,s.step,p.netPay,p.totalPay,e.employeeId, p.totalDeductions," +
                "mdm.id, m.id,m.name,m.codeName,s.salaryType.name,e.rank.name "
                + "from " + IppmsUtils.getPaycheckTableName(businessCertificate) + " p, " + IppmsUtils.getEmployeeTableName(businessCertificate) + " e, SalaryInfo s, MdaDeptMap mdm, MdaInfo m   " +
                "where  p.employee.id = e.id and p.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = m.id " +
                "and p.salaryInfo.id = s.id and p.runMonth = :pRunMonth and p.runYear = :pRunYear " +
                "and p.netPay > 0 order by p.lastName,p.firstName,p.initials";

        Query wQuery = makeQuery(wHqlStr);

        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);


        List<NamedEntityBean> wRetList = new ArrayList<>();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {


            for (Object[] o : wRetVal) {

                NamedEntityBean wBean = new NamedEntityBean();
                wBean.setId((Long) o[0]);
                String initials = null;
                if (o[3] != null)
                    initials = (String) o[3];
                wBean.setName(PayrollHRUtils.createDisplayName((String) o[1], (String) o[2], initials));
                wBean.setSalaryInfo(new SalaryInfo((Long) o[4], ((Integer) o[5]), ((Integer) o[6])));
                wBean.setNetPayStr(PayrollHRUtils.getDecimalFormat().format(o[7]));
                wBean.setNetPay((Double) o[7]);
                wBean.setTotalPayStr(PayrollHRUtils.getDecimalFormat().format(o[8]));
                wBean.setTotalPay((Double) o[8]);
                wBean.setMode((String) o[9]);
                wBean.setTotalDeductionsStr(PayrollHRUtils.getDecimalFormat().format(o[10]));
                wBean.setTotalDeductions((Double) o[10]);
                wRetList.add(wBean);

            }
        }
        return wRetList;


    }

    public List<NamedEntityBean> loadEmployeesNotPaidByBirthOrHireDate(BusinessCertificate businessCertificate, int pRunMonth, int pRunYear, int pCondition) {
        String wOrderByClause = " order by e.lastName,e.firstName, e.initials";

        String wHqlStr = "select  h.birthDate,p.lastName,p.firstName,p.initials,s.id,s.level,s.step, " +
                "h.expectedDateOfRetirement, e.employeeId,e.id,h.hireDate " +
                "from " + IppmsUtils.getPaycheckTableName(businessCertificate) + " p, " + IppmsUtils.getEmployeeTableName(businessCertificate) + " e, HiringInfo h,SalaryInfo s   " +
                "where  p.employee.id = e.id and h." + businessCertificate.getEmployeeIdJoinStr() + " = e.id " +
                "and p.salaryInfo.id = s.id and p.runMonth = :pRunMonth and p.runYear = :pRunYear " +
                "and p.netPay = 0 and p.suspendedInd = 0 and p.terminatedInd = 0 and p.contractIndicator = 0";

        //Ideally they should be mutually exclusive...
        if (pCondition == 1) {
            wHqlStr += " and p.birthDateTerminatedInd = 1 and p.hireDateTerminatedInd = 0";
        } else {
            wHqlStr += " and p.hireDateTerminatedInd = 1 and p.birthDateTerminatedInd = 0";

        }
        wHqlStr += wOrderByClause;
        Query wQuery = makeQuery(wHqlStr);

        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);


        List<NamedEntityBean> wRetList = new ArrayList<>();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {

            for (Object[] o : wRetVal) {

                NamedEntityBean wBean = new NamedEntityBean();
                wBean.setBirthDate((LocalDate) o[0]);

                // wBean.setId(new Long(++wSerialNo));
                String initials = null;
                if (o[3] != null)
                    initials = (String) o[3];
                wBean.setName(PayrollHRUtils.createDisplayName((String) o[1], (String) o[2], initials));
                wBean.setSalaryInfo(new SalaryInfo((Long) o[4], ((Integer) o[5]), ((Integer) o[6])));
                wBean.setExpectedDateOfRetirement((LocalDate) o[7]);
                wBean.setMode((String) o[8]);
                wBean.setId((Long) o[9]);
                wBean.setHireDate((LocalDate) o[10]);
                wRetList.add(wBean);


            }

        }
        return wRetList;


    }

    public List<NamedEntityBean> loadEmployeesNotPaidByContractDate(BusinessCertificate businessCertificate, int pRunMonth, int pRunYear, boolean pExcludePayment) {
        String wHqlStr = "select  p.lastName,p.firstName,p.initials,s.id,s.level,s.step,p.netPay,p.totalPay,p.totalDeductions,e.employeeId,e.id," +
                "h.contractStartDate,h.contractEndDate " +
                "from " + IppmsUtils.getPaycheckTableName(businessCertificate) + " p, SalaryInfo s, " + IppmsUtils.getEmployeeTableName(businessCertificate) + " e,HiringInfo h  " +
                "where  s.id = p.salaryInfo.id and e.id = p.employee.id and h." + businessCertificate.getEmployeeIdJoinStr() + " = e.id " +
                "and p.runMonth = :pRunMonthVar and p.runYear = :pRunYearVar " +
                "and p.contractIndicator = 1 ";

        if (pExcludePayment)
            wHqlStr += " and p.netPay = 0 ";
        else
            wHqlStr += " and p.netPay > 0 ";

        wHqlStr += "order by p.lastName,p.firstName,p.initials";

        Query wQuery = makeQuery(wHqlStr);

        wQuery.setParameter("pRunMonthVar", pRunMonth);
        wQuery.setParameter("pRunYearVar", pRunYear);


        List<NamedEntityBean> wRetList = new ArrayList<>();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {
            for (Object[] o : wRetVal) {
                NamedEntityBean wBean = new NamedEntityBean();
                wBean.setId((Long) o[10]);
                wBean.setMode((String) o[9]);
                String initials = null;
                if (o[2] != null)
                    initials = (String) o[2];
                wBean.setName(PayrollHRUtils.createDisplayName((String) o[0], (String) o[1], initials));
                wBean.setSalaryInfo(new SalaryInfo((Long) o[3], ((Integer) o[4]), ((Integer) o[5])));
                wBean.setNetPayStr(PayrollHRUtils.getDecimalFormat().format(o[6]));
                wBean.setTotalPay((Double) o[7]);
                wBean.setNetPay((Double) o[6]);
                wBean.setTotalDeductions((Double) o[8]);
                wBean.setTotalPayStr(PayrollHRUtils.getDecimalFormat().format(o[7]));
                wBean.setTotalDeductionsStr(PayrollHRUtils.getDecimalFormat().format(o[8]));
                if (o[11] != null)
                    wBean.setAllowanceStartDateStr(PayrollHRUtils.getDisplayDateFormat().format((LocalDate) o[11]));
                else
                    wBean.setAllowanceStartDateStr("");
                if (o[12] != null)
                    wBean.setAllowanceEndDateStr(PayrollHRUtils.getDisplayDateFormat().format((LocalDate) o[12]));
                else
                    wBean.setAllowanceEndDateStr("");

                wRetList.add(wBean);

            }

        }
        return wRetList;


    }

    public List<NamedEntityBean> loadNotPaidBySuspension(BusinessCertificate businessCertificate, int pRunMonth, int pRunYear) {

        String wHqlStr = "select  p.lastName,p.firstName,p.initials,s.id,s.level,s.step,p.netPay,p.totalPay,e.employeeId, e.id " +
                "from " + IppmsUtils.getPaycheckTableName(businessCertificate) + " p, " + IppmsUtils.getEmployeeTableName(businessCertificate) + " e, SalaryInfo s   " +
                "where  p.employee.id = e.id " +
                "and p.salaryInfo.id = s.id and p.runMonth = :pRunMonth and p.runYear = :pRunYear " +
                "and p.suspendedInd = 1 and p.netPay = 0 order by p.lastName,p.firstName,p.initials";

        Query wQuery = makeQuery(wHqlStr);

        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);


        List<NamedEntityBean> wRetList = new ArrayList<>();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {
            for (Object[] o : wRetVal) {

                //That means this Employee was suspended during this period.
                NamedEntityBean wBean = new NamedEntityBean();

                String initials = null;
                if (o[2] != null)
                    initials = (String) o[2];
                wBean.setName(PayrollHRUtils.createDisplayName((String) o[0], (String) o[1], initials));
                wBean.setSalaryInfo(new SalaryInfo((Long) o[3], ((Integer) o[4]), ((Integer) o[5])));
                wBean.setNetPay((Double) o[6]);
                wBean.setTotalPay((Double) o[7]);
                wBean.setMode((String) o[8]);
                wBean.setId((Long) o[9]);
                wRetList.add(wBean);

            }
        }
        return wRetList;


    }

    public List<NamedEntityBean> loadEmployeesWithNegativeNetPayDetails(BusinessCertificate businessCertificate, int pRunMonth, int pRunYear) {

        String wHqlStr = "select  p.lastName,p.firstName,p.initials,s.id,s.level,s.step,p.netPay,abs(p.totalPay), abs(p.totalDeductions), e.employeeId,e.id " +
                "from " + IppmsUtils.getPaycheckTableName(businessCertificate) + " p, " + IppmsUtils.getEmployeeTableName(businessCertificate) + " e, SalaryInfo s   " +
                "where  p.employee.id = e.id " +
                "and p.salaryInfo.id = s.id and p.runMonth = :pRunMonth and p.runYear = :pRunYear " +
                "and p.netPay < 0 order by p.lastName,p.firstName,p.initials";

        Query wQuery = makeQuery(wHqlStr);

        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);


        List<NamedEntityBean> wRetList = new ArrayList<>();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {

            for (Object[] o : wRetVal) {

                NamedEntityBean wBean = new NamedEntityBean();

                String initials = null;
                if (o[2] != null)
                    initials = (String) o[2];
                wBean.setName(PayrollHRUtils.createDisplayName((String) o[0], (String) o[1], initials));
                wBean.setSalaryInfo(new SalaryInfo((Long) o[3], ((Integer) o[4]), ((Integer) o[5])));
                wBean.setNetPayStr(PayrollHRUtils.getDecimalFormat().format(o[6]));
                wBean.setNetPay((Double) o[6]);
                wBean.setTotalPayStr(PayrollHRUtils.getDecimalFormat().format(o[7]));
                wBean.setTotalPay((Double) o[7]);
                wBean.setTotalDeductionsStr(PayrollHRUtils.getDecimalFormat().format(o[8]));
                wBean.setTotalDeductions((Double) o[8]);
                wBean.setMode((String) o[9]);
                wBean.setId((Long) o[10]);
                wRetList.add(wBean);

            }
        }
        return wRetList;


    }

    public List<NamedEntityBean> loadEmployeesPaidByDaysDetails(BusinessCertificate businessCertificate, int pRunMonth, int pRunYear) {
        String wHqlStr = "select  p.lastName,p.firstName,p.initials,s.id,s.level,s.step,p.netPay,p.totalPay,p.totalDeductions, e.employeeId,e.id " +
                "from " + IppmsUtils.getPaycheckTableName(businessCertificate) + " p, " + IppmsUtils.getEmployeeTableName(businessCertificate) + " e, SalaryInfo s   " +
                "where  p.employee.id = e.id " +
                "and p.salaryInfo.id = s.id and p.runMonth = :pRunMonth and p.runYear = :pRunYear " +
                "and p.noOfDays > 0 order by p.lastName,p.firstName,p.initials";

        Query wQuery = makeQuery(wHqlStr);

        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);


        List<NamedEntityBean> wRetList = new ArrayList<>();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {

            for (Object[] o : wRetVal) {

                NamedEntityBean wBean = new NamedEntityBean();

                String initials = null;
                if (o[2] != null)
                    initials = (String) o[2];
                wBean.setName(PayrollHRUtils.createDisplayName((String) o[0],
                        (String) o[1], initials));
                wBean.setSalaryInfo(new SalaryInfo((Long) o[3],
                        ((Integer) o[4]), ((Integer) o[5])));
                wBean.setNetPayStr(PayrollHRUtils.getDecimalFormat().format(o[6]));
                wBean.setNetPay((Double) o[6]);
                wBean.setTotalPayStr(PayrollHRUtils.getDecimalFormat().format(o[7]));
                wBean.setTotalPay((Double) o[7]);
                wBean.setTotalDeductionsStr(PayrollHRUtils.getDecimalFormat().format(o[8]));
                wBean.setTotalDeductions((Double) o[8]);
                wBean.setMode((String) o[9]);
                wBean.setId((Long) o[10]);
                wRetList.add(wBean);

            }
        }
        return wRetList;

    }

    public List<NamedEntityBean> loadEmployeesPaidSpecAllowDetails(BusinessCertificate businessCertificate, int pRunMonth, int pRunYear) {

        String wHqlStr = "select  p.lastName,p.firstName,p.initials,s.id,s.level,s.step,p.netPay,p.totalPay,p.totalDeductions,e.employeeId,e.id,p.specialAllowance " +
                "from " + IppmsUtils.getPaycheckTableName(businessCertificate) + " p, " + IppmsUtils.getEmployeeTableName(businessCertificate) + " e, SalaryInfo s   " +
                "where  p.employee.id = e.id " +
                "and p.salaryInfo.id = s.id and p.runMonth = :pRunMonth and p.runYear = :pRunYear " +
                "and p.specialAllowance > 0 order by p.lastName,p.firstName,p.initials";

        Query wQuery = makeQuery(wHqlStr);

        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);
 
        List<NamedEntityBean> wRetList = new ArrayList<>();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {

            for (Object[] o : wRetVal) {

                NamedEntityBean wBean = new NamedEntityBean();

                String initials = null;
                if (o[2] != null)
                    initials = (String) o[2];
                wBean.setName(PayrollHRUtils.createDisplayName((String) o[0], (String) o[1], initials));
                wBean.setSalaryInfo(new SalaryInfo((Long) o[3], ((Integer) o[4]), ((Integer) o[5])));
                wBean.setNetPayStr(PayrollHRUtils.getDecimalFormat().format(o[6]));
                wBean.setNetPay((Double) o[6]);
                wBean.setTotalPayStr(PayrollHRUtils.getDecimalFormat().format(o[7]));
                wBean.setTotalPay((Double) o[7]);
                wBean.setTotalDeductionsStr(PayrollHRUtils.getDecimalFormat().format(o[8]));
                wBean.setTotalDeductions((Double) o[8]);
                wBean.setMode((String) o[9]);
                wBean.setId((Long) o[10]);
                wBean.setSpecAllowStr(PayrollHRUtils.getDecimalFormat().format(o[11]));
                wBean.setSpecAllow((Double) o[11]);
                wRetList.add(wBean);

            }
        }
        return wRetList;
 
    }

    public List<NamedEntityBean> loadEmployeesPaidByInterdiction(BusinessCertificate businessCertificate, int pRunMonth, int pRunYear) {

      String wHqlStr = "select  p.lastName,p.firstName,p.initials,s.id,s.level,s.step,p.netPay,p.totalPay,p.totalDeductions,e.employeeId,e.id "+
                "from "+IppmsUtils.getPaycheckTableName(businessCertificate)+" p, "+IppmsUtils.getEmployeeTableName(businessCertificate)+" e, SalaryInfo s   "+
                "where  p.employee.id = e.id " +
                "and p.salaryInfo.id = s.id and p.runMonth = :pRunMonth and p.runYear = :pRunYear "+
                "and p.payPercentage > 0 and p.netPay > 0 order by p.lastName,p.firstName,p.initials";

        Query wQuery = makeQuery(wHqlStr);

        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);


        List<NamedEntityBean> wRetList = new ArrayList<>();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0))
        {

            for (Object[] o : wRetVal) {

                NamedEntityBean wBean = new NamedEntityBean();

                String initials = null;
                if(o[2] != null)
                    initials = (String)o[2];
                wBean.setName(PayrollHRUtils.createDisplayName((String)o[0], (String)o[1], initials));
                wBean.setSalaryInfo(new SalaryInfo((Long)o[3],((Integer)o[4]),((Integer)o[5])));
                wBean.setNetPayStr(PayrollHRUtils.getDecimalFormat().format(o[6]));
                wBean.setNetPay((Double) o[6]);
                wBean.setTotalPayStr(PayrollHRUtils.getDecimalFormat().format(o[7]));
                wBean.setTotalPay((Double) o[7]);
                wBean.setTotalDeductionsStr(PayrollHRUtils.getDecimalFormat().format(o[8]));
                wBean.setTotalDeductions((Double) o[8]);
                wBean.setMode((String)o[9]);
                wBean.setId((Long)o[10]);

                wRetList.add(wBean);

            }
        }
        return wRetList;


    }

    public List<NamedEntityBean> loadEmployeesNotPaidByTermination(BusinessCertificate businessCertificate, int pRunMonth, int pRunYear) {

        String wHqlStr = "select  h.hireDate,p.lastName,p.firstName,p.initials,s.id,s.level,s.step,h.expectedDateOfRetirement, e.employeeId,e.id, h.birthDate,h.terminateDate "+
                "from "+IppmsUtils.getPaycheckTableName(businessCertificate)+" p, "+IppmsUtils.getEmployeeTableName(businessCertificate)+" e, HiringInfo h,SalaryInfo s   "+
                "where  p.employee.id = e.id and h."+businessCertificate.getEmployeeIdJoinStr()+" = e.id " +
                "and p.salaryInfo.id = s.id and p.runMonth = :pRunMonth and p.runYear = :pRunYear "+
                "and p.netPay = 0 and p.terminatedInd = 1 order by p.lastName,p.firstName,p.initials";

        Query wQuery = makeQuery(wHqlStr);

        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);


        ArrayList<NamedEntityBean> wRetList = new ArrayList<>();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0))
        {


            int wSerialNo = 0;
            for (Object[] o : wRetVal) {

                LocalDate birthDate = null;
                if(o[10] != null){
                    birthDate = (LocalDate)o[10];
                }
                LocalDate hireDate = null;
                if(o[0] != null){
                    hireDate = (LocalDate)o[0];
                }


                NamedEntityBean wBean = new NamedEntityBean();
                wBean.setHireDate(hireDate);
                wBean.setBirthDate(birthDate);
                wBean.setId(new Long(++wSerialNo));
                String initials = null;
                if(o[3] != null)
                    initials = (String)o[3];
                wBean.setName(PayrollHRUtils.createDisplayName((String)o[1], (String)o[2], initials));
                wBean.setSalaryInfo(new SalaryInfo((Long)o[4],((Integer)o[5]),((Integer)o[6])));
                wBean.setExpectedDateOfRetirement((LocalDate)o[7]);
                wBean.setMode((String)o[8]);
                wBean.setId((Long)o[9]);
                wBean.setTerminationDate((LocalDate)o[11]);

                wRetList.add(wBean);

            }


        }

        Collections.sort(wRetList, Comparator.comparing(NamedEntityBean::getName));
        return wRetList;



    }

    public List<NamedEntityBean> loadEmployeesNotPaidByApproval(BusinessCertificate businessCertificate, int pRunMonth, int pRunYear) {
       String wHqlStr = "select  h.hireDate,p.lastName,p.firstName,p.initials,s.id,s.level,s.step,h.expectedDateOfRetirement, e.employeeId,e.id, h.birthDate,h.terminateDate "+
                "from "+IppmsUtils.getPaycheckTableName(businessCertificate)+" p, "+IppmsUtils.getEmployeeTableName(businessCertificate)+" e, HiringInfo h,SalaryInfo s   "+
                "where  p.employee.id = e.id and h."+businessCertificate.getEmployeeIdJoinStr()+" = e.id " +
                "and p.salaryInfo.id = s.id and p.runMonth = :pRunMonth and p.runYear = :pRunYear "+
                "and p.netPay = 0 and p.rejectedForPayrollingInd = 1 order by p.lastName,p.firstName,p.initials";

        Query wQuery = makeQuery(wHqlStr);

        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);


        List<NamedEntityBean> wRetList = new ArrayList<NamedEntityBean>();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0))
        {


            int wSerialNo = 0;
            for (Object[] o : wRetVal) {

                LocalDate birthDate = null;
                if(o[10] != null){
                    birthDate = (LocalDate)o[10];
                }
                LocalDate hireDate = null;
                if(o[0] != null){
                    hireDate = (LocalDate)o[0];
                }
                NamedEntityBean wBean = new NamedEntityBean();
                wBean.setHireDate(hireDate);
                wBean.setBirthDate(birthDate);
                wBean.setId(new Long(++wSerialNo));
                String initials = null;
                if(o[3] != null)
                    initials = (String)o[3];
                wBean.setName(PayrollHRUtils.createDisplayName((String)o[1], (String)o[2], initials));
                wBean.setSalaryInfo(new SalaryInfo((Long)o[4],((Integer)o[5]),((Integer)o[6])));
                wBean.setExpectedDateOfRetirement((LocalDate)o[7]);
                wBean.setMode((String)o[8]);
                wBean.setId((Long)o[9]);
                wBean.setTerminationDate((LocalDate)o[11]);

                wRetList.add(wBean);


            }

        }

        Collections.sort(wRetList,Comparator.comparing(NamedEntityBean::getName));
        return wRetList;

    }

    public List<NamedEntityBean> loadPensionersAwaitingCalc(BusinessCertificate businessCertificate, int pRunMonth, int pRunYear, boolean b) {

        String wHqlStr = "select  h.pensionStartDate,p.lastName,p.firstName,p.initials,s.id,s.level,s.step,h.amAliveDate, e.employeeId,e.id, h.monthlyPensionAmount,h.yearlyPensionAmount "+
                "from "+IppmsUtils.getPaycheckTableName(businessCertificate)+" p, "+IppmsUtils.getEmployeeTableName(businessCertificate)+" e, HiringInfo h,SalaryInfo s   "+
                "where  p.employee.id = e.id and h."+businessCertificate.getEmployeeIdJoinStr()+" = e.id " +
                "and p.salaryInfo.id = s.id and p.runMonth = :pRunMonth and p.runYear = :pRunYear "+
                "and p.netPay = 0 and p.awaitingPenCalcInd = 1 order by p.lastName,p.firstName,p.initials";

        Query wQuery = makeQuery(wHqlStr);

        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);


        List<NamedEntityBean> wRetList = new ArrayList<NamedEntityBean>();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0))
        {

            NamedEntityBean wBean;
            String initials;
          
            int wSerialNo = 0;
            for (Object[] o : wRetVal) {

                wBean = new NamedEntityBean();
           
                wBean.setHireDate((LocalDate)o[0]);
                wBean.setId(new Long(++wSerialNo));
                  initials = null;
                if(o[3] != null)
                    initials = (String)o[3];
                wBean.setName(PayrollHRUtils.createDisplayName((String)o[1], (String)o[2], initials));
                wBean.setSalaryInfo(new SalaryInfo((Long)o[4],((Integer)o[5]),((Integer)o[6])));
                wBean.setExpectedDateOfRetirement((LocalDate)o[7]);
                wBean.setMode((String)o[8]);
                wBean.setId((Long)o[9]);
                wBean.setMonthlyPension((Double)o[10]);
                wBean.setAnnualPension((Double)o[11]);
                wBean.setMonthlyPensionStr(PayrollHRUtils.getDecimalFormat().format(wBean.getMonthlyPension()));
                wBean.setAnnualPensionStr(PayrollHRUtils.getDecimalFormat().format(wBean.getAnnualPension()));
                wRetList.add(wBean);


            }

        }

        Collections.sort(wRetList,Comparator.comparing(NamedEntityBean::getName));
        return wRetList;

    }
}
