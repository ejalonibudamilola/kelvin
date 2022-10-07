/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.domain.beans.MaterialityDisplayBean;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import com.osm.gnl.ippms.ogsg.statistics.domain.MdaPayrollStatistics;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service("statisticsService")
@Repository
@Transactional(readOnly = true)
public class StatisticsService {

    private final GenericService genericService;
    private final SessionFactory sessionFactory;

    @Autowired
    public StatisticsService(GenericService genericService, SessionFactory sessionFactory) {
        this.genericService = genericService;
        this.sessionFactory = sessionFactory;
    }

    private Query makeQuery(String wHql) {
        return sessionFactory.getCurrentSession().createQuery(wHql);
    }

    public NamedEntityBean loadPaycheckSummaryInfoByMonthAndYear(BusinessCertificate pBusinessCertificate, int pRunMonth, int pRunYear) {

        String wHqlStr = "select coalesce(sum(p.netPay),0), coalesce(sum(p.totalPay),0),coalesce(sum(p.totalDeductions),0),count(p.employee.id) " +
                "from " + IppmsUtils.getPaycheckTableName(pBusinessCertificate) + " p   " +
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

    public MaterialityDisplayBean loadEmployeesNotPaidByMonthAndYear(BusinessCertificate pBusinessCertificate, int pRunMonth, int pRunYear, MaterialityDisplayBean pMDB) {

        String wHqlStr = "select  h.birthDate,h.hireDate,p.birthDateTerminatedInd,p.hireDateTerminatedInd,p.awaitingPenCalcInd  " +
                "from " + IppmsUtils.getPaycheckTableName(pBusinessCertificate) + " p, " + IppmsUtils.getEmployeeTableName(pBusinessCertificate) + " e, HiringInfo h   " +
                "where  p.employee.id = e.id and h." + pBusinessCertificate.getEmployeeIdJoinStr() + " = e.id and p.runMonth = :pRunMonth and p.runYear = :pRunYear " +
                "and p.netPay = 0 and p.suspendedInd = 0 and p.terminatedInd = 0 and p.contractIndicator = 0 and p.rejectedForPayrollingInd = 0 " +
                " ";

        Query wQuery = makeQuery(wHqlStr);

        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {
            for (Object[] o : wRetVal) {

                if ((Integer) o[2] == 1) {
                    pMDB.setEmployeesNotPaidByBirthDate(pMDB.getEmployeesNotPaidByBirthDate() + 1);
                } else if ((Integer) o[3] == 1) {
                    pMDB.setEmployeesNotPaidByHireDate(pMDB.getEmployeesNotPaidByHireDate() + 1);
                }else if((Integer) o[4] == 1){
                    pMDB.setPensionersNotPaidByPensionCalc(pMDB.getPensionersNotPaidByPensionCalc() + 1);
                }

            }

        }
        return pMDB;


    }


    public NamedEntityBean loadEmployeesPaidByContract(BusinessCertificate pBusinessCertificate, int pRunMonth, int pRunYear, boolean pExcludePayment) {
        String wHql = "select coalesce(sum(p.netPay),0), coalesce(sum(p.totalPay),0), count(p.id) " +
                "from " + IppmsUtils.getPaycheckTableName(pBusinessCertificate) + " p " +
                "where p.runMonth = :pRunMonthVar and p.runYear = :pRunYearVar " +
                " and p.contractIndicator = 1 ";

        if (pExcludePayment)
            wHql += " and p.netPay = 0";
        else
            wHql += " and p.netPay > 0 ";
        Query wQuery = this.makeQuery(wHql);
        wQuery.setParameter("pRunMonthVar", pRunMonth);
        wQuery.setParameter("pRunYearVar", pRunYear);


        NamedEntityBean wRetList = new NamedEntityBean();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {
            for (Object[] o : wRetVal) {

                wRetList.setNetPay((Double) o[0]);
                wRetList.setTotalPay((Double) o[1]);
                wRetList.setNoOfActiveEmployees(((Long) o[2]).intValue());

            }

        }

        return wRetList;

    }

    /**
     * Use with Caution. The Boolean Variables are Mutually Exclusive, i.e., only 1 should be true at a time.
     *
     * @param pBusinessCertificate
     * @param pRunMonth
     * @param pRunYear
     * @param suspension
     * @param termination
     * @param approvalIssues
     * @return
     */
    public int loadEmpNotPaidDueToReason(BusinessCertificate pBusinessCertificate, int pRunMonth, int pRunYear, boolean suspension, boolean termination, boolean approvalIssues) {

        String wHql = "select count(p.id) " +
                "from " + IppmsUtils.getPaycheckTableName(pBusinessCertificate) + " p  " +
                "where p.runMonth = :pRunMonthVar and p.runYear = :pRunYearVar ";
        if (suspension)
            wHql += " and p.terminatedInd = 0 and p.suspendedInd = 1 and p.netPay = 0 ";
        else if (termination)
            wHql += " and p.terminatedInd = 1 and p.netPay = 0 ";
        else if (approvalIssues)
            wHql += " and p.rejectedForPayrollingInd = 1 and p.netPay = 0 ";
        else
            return 0;

        Query wQuery = this.makeQuery(wHql);
        wQuery.setParameter("pRunMonthVar", pRunMonth);
        wQuery.setParameter("pRunYearVar", pRunYear);


        List list = wQuery.list();

        if (list == null)
            return 0;
        if (list.size() == 0)
            return 0;
        if ((list.size() == 1) && (((Long) list.get(0)).intValue() == 0)) {
            return 0;
        }
        return ((Long) list.get(0)).intValue();


    }

    public NamedEntityBean loadNoOfEmployeesPaidByInterdiction(BusinessCertificate pBusinessCertificate, int pRunMonth, int pRunYear) {
        String wHqlStr = "select coalesce(sum(p.netPay),0), coalesce(sum(p.totalPay),0), count(p.id) " +
                "from " + IppmsUtils.getPaycheckTableName(pBusinessCertificate) + " p    " +
                "where  p.runMonth = :pRunMonthVar and p.runYear = :pRunYearVar " +
                "and p.suspendedInd = 1 and p.netPay > 0 ";


        Query wQuery = this.makeQuery(wHqlStr);
        wQuery.setParameter("pRunMonthVar", pRunMonth);
        wQuery.setParameter("pRunYearVar", pRunYear);


        NamedEntityBean wRetList = new NamedEntityBean();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {
            for (Object[] o : wRetVal) {

                wRetList.setNetPay((Double) o[0]);
                wRetList.setTotalPay((Double) o[1]);
                wRetList.setNoOfActiveEmployees(((Long) o[2]).intValue());

            }

        }

        return wRetList;


    }

    public NamedEntityBean loadEmployeesWithNegativeNetPay(BusinessCertificate pBusinessCertificate, int pRunMonth, int pRunYear) {
        String wHqlStr = "select coalesce(sum(abs(p.netPay)),0),count(p.employee.id),coalesce(sum(abs(p.totalPay)),0) " +
                "from " + IppmsUtils.getPaycheckTableName(pBusinessCertificate) + " p   " +
                "where  p.runMonth = :pRunMonth and p.runYear = :pRunYear " +
                "and p.netPay < 0 ";

        Query wQuery = makeQuery(wHqlStr);

        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);


        NamedEntityBean wRetList = new NamedEntityBean();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {
            for (Object[] o : wRetVal) {

                wRetList.setNegativePay((Double) o[0]);
                wRetList.setNoOfEmployeesWithNegPay(((Long) o[1]).intValue());
                wRetList.setTotalPay((Double) o[2]);

            }

        }

        return wRetList;
    }

    public NamedEntityBean loadEmployeesPaidByDays(BusinessCertificate pBusinessCertificate, int pRunMonth, int pRunYear) {


        String wHqlStr = "select coalesce(sum(abs(p.netPay)),0),count(p.employee.id),coalesce(sum(abs(p.totalPay)),0) " +
                "from " + IppmsUtils.getPaycheckTableName(pBusinessCertificate) + " p   " +
                "where  p.runMonth = :pRunMonth and p.runYear = :pRunYear " +
                "and p.noOfDays > 0 ";

        Query wQuery = makeQuery(wHqlStr);

        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);


        NamedEntityBean wRetList = new NamedEntityBean();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {
            for (Object[] o : wRetVal) {

                wRetList.setNetPayByDays((Double) o[0]);
                wRetList.setNoOfEmployeesPaidByDays(((Long) o[1]).intValue());
                wRetList.setTotalPay((Double) o[2]);

            }

        }

        return wRetList;


    }

    public NamedEntityBean loadEmployeesPaidSpecialAllowancesByMonthAndYear(BusinessCertificate pBusinessCertificate, int pRunMonth, int pRunYear) {

        String wHqlStr = "select coalesce(sum(p.specialAllowance),0),count(p.employee.id), coalesce(sum(p.totalPay),0) " +
                "from " + IppmsUtils.getPaycheckTableName(pBusinessCertificate) + " p   " +
                "where  p.runMonth = :pRunMonth and p.runYear = :pRunYear " +
                "and p.specialAllowance > 0 ";

        Query wQuery = makeQuery(wHqlStr);

        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);


        NamedEntityBean wRetList = new NamedEntityBean();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {
            for (Object[] o : wRetVal) {

                wRetList.setNetPayByDays((Double) o[0]);
                wRetList.setNoOfEmployeesPaidByDays(((Long) o[1]).intValue());
                wRetList.setTotalPayStr(PayrollHRUtils.getDecimalFormat().format(o[2]));
            }

        }

        return wRetList;


    }

    public List<MdaPayrollStatistics> createMdaPayrollStatistics(BusinessCertificate bc, int pRunMonth, int pRunYear) {


        List<MdaPayrollStatistics> wRetList = new ArrayList<>();

        String wHql = "select sum(p.totalPay), sum(p.netPay),sum(p.taxesPaid), sum(p.totalAllowance)"
                + ", sum(p.specialAllowance), sum(p.totalGarnishments), sum(p.totalDeductions),p.masterBean.id,p.mdaDeptMap.mdaInfo.id, count(p.id)"
                + " from " + IppmsUtils.getPaycheckTableName(bc) + " p where p.runMonth = :pRunMonthVar and p.runYear = :pRunYearVar"
                + " group by p.mdaDeptMap.mdaInfo.id,p.masterBean.id";
        Query query = sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pRunMonthVar", pRunMonth);
        query.setParameter("pRunYearVar", pRunYear);

        List<Object[]> wRetVal = (ArrayList) query.list();

        if (wRetVal.size() > 0) {
            int i = 0;
            double netPay = 0.0D;
            double totalPay = 0.0D;
            double taxes = 0.0D;
            double totalAllow = 0.0D;
            double specAllow = 0.0D;
            double loans = 0.0D;
            double deductions = 0.0D;
            int noOfStaffs = 0;
            Long _noOfStaffs = null;
            for (Object[] o : wRetVal) {

                MdaPayrollStatistics p = new MdaPayrollStatistics();
                p.setBusinessClientId(bc.getBusinessClientInstId());
                p.setTotalPay((Double) o[i++]);
                p.setNetPay((Double) o[i++]);
                p.setTotalTaxes((Double) o[i++]);
                p.setTotalAllowance((Double) o[i++]);
                p.setTotalSpecialAllowance((Double) o[i++]);
                p.setTotalGarnishments((Double) o[i++]);
                p.setTotalDeductions((Double) o[i++]);
                p.setPayrollRunMasterBean(new PayrollRunMasterBean((Long) o[i++]));
                p.setMdaInfo(new MdaInfo((Long) o[i++]));
                _noOfStaffs = (Long) o[i++];
                if (_noOfStaffs != null)
                    p.setTotalStaff(_noOfStaffs.intValue());

                netPay += p.getNetPay();
                totalPay += p.getTotalPay();
                taxes += p.getTotalTaxes();
                totalAllow += p.getTotalAllowance();
                specAllow += p.getTotalSpecialAllowance();
                loans += p.getTotalGarnishments();
                deductions += p.getTotalDeductions();
                noOfStaffs += p.getTotalStaff();
                wRetList.add(p);
                i = 0;
            }
            //We need to Reiterate and set Percentile Values...
            BigDecimal wBD = null;
            for (MdaPayrollStatistics q : wRetList) {
                if (q.getTotalPay() > 0) {
                    if (totalPay > 0) {
                        wBD = new BigDecimal((q.getTotalPay() * 100) / totalPay).setScale(2, RoundingMode.HALF_EVEN);
                        q.setTotalPayPercentile(wBD.doubleValue());
                    }
                    if (netPay > 0) {
                        wBD = new BigDecimal((q.getNetPay() * 100) / netPay).setScale(2, RoundingMode.HALF_EVEN);
                        q.setNetPayPercentile(wBD.doubleValue());
                    }
                    if (taxes > 0) {
                        wBD = new BigDecimal((q.getTotalTaxes() * 100) / taxes).setScale(2, RoundingMode.HALF_EVEN);
                        q.setTotalTaxesPercentile(wBD.doubleValue());
                    }
                    if (totalAllow > 0) {
                        wBD = new BigDecimal((q.getTotalAllowance() * 100) / totalAllow).setScale(2, RoundingMode.HALF_EVEN);
                        q.setTotalAllowancePercentile(wBD.doubleValue());
                    }
                    if (specAllow > 0) {
                        wBD = new BigDecimal((q.getTotalSpecialAllowance() * 100) / specAllow).setScale(2, RoundingMode.HALF_EVEN);
                        q.setTotalSpecialAllowancePercentile(wBD.doubleValue());
                    }
                    if (loans > 0) {
                        wBD = new BigDecimal((q.getTotalGarnishments() * 100) / loans).setScale(2, RoundingMode.HALF_EVEN);
                        q.setTotalGarnishmentsPercentile(wBD.doubleValue());
                    }
                    if (deductions > 0) {
                        wBD = new BigDecimal((q.getTotalDeductions() * 100) / deductions).setScale(2, RoundingMode.HALF_EVEN);
                        q.setTotalDeductionsPercentile(wBD.doubleValue());
                    }
                    if (noOfStaffs > 0) {
                        wBD = new BigDecimal((new Double(String.valueOf(q.getTotalStaff())) * 100) / new Double(String.valueOf(noOfStaffs))).setScale(2, RoundingMode.HALF_EVEN);
                        q.setTotalStaffPercentile(wBD.doubleValue());
                    }
                }

            }
        }

        return wRetList;

    }
    public List<AbstractPaycheckEntity> loadPendingPaychecks(BusinessCertificate bc, int pStartRow, int pEndRow) {
        ArrayList<Object[]> wRetVal;
        List wRetList = new ArrayList();


        String hqlQuery = "select p.id, p.lastName,p.firstName,p.initials,p.ogNumber,p.totalPay,p.netPay,p.taxesPaid,st.name,s.level,s.step,p.runMonth,p.runYear,p.payPeriodStart," +
                "p.payPeriodEnd,p.payDate from " +
                IppmsUtils.getPaycheckTableName(bc) +" p, SalaryInfo s, SalaryType st " +
                "where p.salaryInfo.id = s.id and s.salaryType.id = st.id and p.status = 'P' and p.businessClientId = :pBusClientIdVar ";

        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);

        query.setParameter("pBusClientIdVar", bc.getBusinessClientInstId());

        if (pStartRow > 0)
            query.setFirstResult(pStartRow);
        query.setMaxResults(pEndRow);

        wRetVal = (ArrayList) query.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                int i = 0;
                AbstractPaycheckEntity p = new EmployeePayBean();
                p.setId((Long) o[i++]);
                p.setEmployeeName(PayrollHRUtils.createDisplayName( (String) o[i++], (String) o[i++], o[i++]));
                p.setOgNumber((String)o[i++]);
                p.setTotalPay(((Double) o[i++]));
                p.setNetPay(((Double) o[i++]));
                p.setTaxesPaid(((Double) o[i++]));
                p.setSalaryTypeName((String) o[i++]);
                p.setLevelAndStepStr(PayrollUtils.makeLevelAndStep((Integer)o[i++], (Integer)o[i++]));
                p.setRunMonth((Integer)o[i++]);
                p.setRunYear((Integer)o[i++]);
                p.setPayPeriodStart((LocalDate)o[i++]);
                p.setPayPeriodEnd((LocalDate)o[i++]);
                p.setPayDate((LocalDate)o[i++]);

                wRetList.add(p);

            }

        }

        return wRetList;

    }
    public int countPendingPaychecks(BusinessCertificate bc) {
        int wRetVal;
        List wRetList = new ArrayList();


        String hqlQuery = "select count(p.id)  from " +
                IppmsUtils.getPaycheckTableName(bc) +" p, SalaryInfo s, SalaryType st " +
                "where p.salaryInfo.id = s.id and s.salaryType.id = st.id and p.status = 'P' and p.businessClientId = :pBusClientIdVar ";

        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);

        query.setParameter("pBusClientIdVar", bc.getBusinessClientInstId());


        wRetList = query.list();

        wRetVal= Integer.parseInt(String.valueOf(wRetList.get(0)));

        return wRetVal;

    }
}
