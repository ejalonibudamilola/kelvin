package com.osm.gnl.ippms.ogsg.controllers.report.service;

import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.control.entities.EmployeeType;
import com.osm.gnl.ippms.ogsg.domain.beans.BankPVSummaryBean;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.domain.report.BankScheduleDetailed;
import com.osm.gnl.ippms.ogsg.domain.report.BankScheduleSummary;
import com.osm.gnl.ippms.ogsg.domain.report.GrossSummary;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/*
Taiwo Kasumu
12-09-2020
 */
@Service
@Repository
@Transactional(readOnly = true)
@Slf4j
public class BankService {

    @Autowired
    PaycheckService paycheckService;

    /**
     * Kasumu Taiwo
     * 12-2020
     */

    private final SessionFactory sessionFactory;

    public BankService(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<BankScheduleSummary> bankScheduleSummary(BusinessCertificate bc, int wRunMonth, int wRunYear,
                                                         int fromLevel, int toLevel, String bank) {
        BankScheduleSummary wBSS;
        List<BankScheduleSummary> wCol = new ArrayList<>();

        String wSql = "select sum(i.netPay), i.payPeriodEnd, b.name, k.name,b.id"
                + " from " + IppmsUtils.getPaycheckTableName(bc) + " i, BankBranch b,  BankInfo k, SalaryInfo s "
                + " where i.runMonth = " + wRunMonth + " and i.runYear = " + wRunYear + ""
                + " and i.bankBranch.id = b.id and i.salaryInfo.id = s.id"
                + " and b.bankInfo.id = k.id and i.netPay > 0 ";

        if (IppmsUtils.isNotNullOrEmpty(bank))
            wSql += "and k.name = " + bank + " ";

        if (fromLevel > 0 && toLevel > 0)
            wSql += "and s.level >= " + fromLevel + " and s.level <= " + toLevel + " ";

        wSql += " group by i.payPeriodEnd, b.name, k.name,b.id  order by k.name";
        Query query = this.sessionFactory.getCurrentSession().createQuery(wSql);
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        String bankName, bankBranch;
        Double amount;
        LocalDate period;
        if (wRetVal.size() > 0)
            for (Object[] o : wRetVal) {
            bankName = ((String) o[3]);
            bankBranch = ((String) o[2]);
            amount = ((Double) o[0]);
            period = (LocalDate) o[1];
            wBSS = new BankScheduleSummary(bankName, bankBranch, amount, period);
            wBSS.setBankBranchId((Long)o[4]);
            wCol.add(wBSS);
            }
        return wCol;
    }

    public List<BankScheduleSummary> bankSummaryByBanks(BusinessCertificate bc, int wRunMonth, int wRunYear) {
        BankScheduleSummary wBSS;
        List<BankScheduleSummary> wCol = new ArrayList<>();

        String wSql = "select sum(i.netPay), k.name, count(i.id)"
                + " from " + IppmsUtils.getPaycheckTableName(bc) + " i, " + IppmsUtils.getEmployeeTableName(bc) + " e, BankBranch b, BankInfo k"
                + " where i.runMonth = " + wRunMonth + " and i.runYear = " + wRunYear + ""
                + " and i.bankBranch.id = b.id and i.employee.id = e.id "
                + " and b.bankInfo.id = k.id and i.netPay > 0"
                + " group by k.name" + " order by k.name";
        Query query = this.sessionFactory.getCurrentSession().createQuery(wSql);
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {
            int totalStaff;
            String bankName;
            double amount;
            for (Object[] o : wRetVal) {
                  totalStaff = ((Integer) o[2]);
                  bankName = ((String) o[1]);
                  amount = ((Double) o[0]);
                wBSS = new BankScheduleSummary(bankName, amount, totalStaff);
                wCol.add(wBSS);
            }
        }
        return wCol;
    }


    public List<GrossSummary> grossSummaryByLga(BusinessCertificate bc, int wRunMonth, int wRunYear)
            throws Exception {
        GrossSummary wBSS;
        List<GrossSummary> wCol = new ArrayList<>();

        String wSql = "select sum(i.totalPay), k.name, count(i.id)"
                + " from " + IppmsUtils.getPaycheckTableName(bc) + " i, " + IppmsUtils.getEmployeeTableName(bc) + " e, LGAInfo k"
                + " where i.runMonth = " + wRunMonth + " and i.runYear = " + wRunYear + ""
                + " and i.employee.id = e.id and e.lgaInfo.id = k.id "
                + " and i.netPay > 0"
                + " group by k.name" + " order by k.name";
        Query query = this.sessionFactory.getCurrentSession().createQuery(wSql);
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                Long totalStaff = ((Long) o[2]);
                String lgaName = ((String) o[1]);
                Double amount = ((Double) o[0]);
                wBSS = new GrossSummary(lgaName, amount, totalStaff);
                wCol.add(wBSS);
            }
        }
        return wCol;
    }

    public List<BankScheduleDetailed> bankScheduleDetailedMda(int pRunMonth, int pRunYear, BusinessCertificate bc)
            throws Exception {

        List<BankScheduleDetailed> wCol = new ArrayList<>();
        BankScheduleDetailed wBSD;

        String wSql = "select e.firstName, e.lastName, e.id, i.netPay, i.accountNumber,i.payPeriodEnd, b.name, e.initials"
                + " from " + IppmsUtils.getPaycheckTableName(bc) + " i, BankBranch b, Employee e"
                + " where i.runMonth = " + pRunMonth + " and i.runYear = " + pRunYear + ""
                + " and i.bankBranch.id = b.id" + " and e.id = i.employee.id"
                + " and i.netPay > 0" + " order by b.name, e.lastName, e.firstName";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wSql);
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                String firstName = ((String) o[0]);
                String lastName = ((String) o[1]);
                Long eID = ((Long) o[2]);
                Double totalPay = ((Double) o[3]);
                String accountNumber = ((String) o[4]);
                LocalDate period = ((LocalDate) o[5]);
                String bankBranch = ((String) o[6]);
                String initials = PayrollHRUtils.treatNull(((String) o[7]));

                String sName = PayrollHRUtils.createDisplayName(lastName, firstName, initials);

                wBSD = new BankScheduleDetailed(eID.toString(), sName, bankBranch, accountNumber, totalPay, period);
                wCol.add(wBSD);
            }
        }
        return wCol;
    }

    public List<EmployeePayBean> loadEmployeePayBeanByParentIdFromDateToDate(int pRunMonth, int pRunYear, BusinessCertificate bc) {
        ArrayList<Object[]> wRetVal;
        List wRetList = new ArrayList();

        String hqlQuery = "select p.taxesPaid,p.totalGarnishments,p.totalPay,p.netPay, e.lastName,e.firstName, e.initials, " +
                "e.employeeId,s.level,s.step,mda.name,p.totalDeductions,p.contributoryPension,si.name, coalesce(p.accountNumber,'N/A'), b.name," +
                "p.totalAllowance,p.monthlyBasic from " + IppmsUtils.getPaycheckTableName(bc) + " p, " + IppmsUtils.getEmployeeTableName(bc) + " e," +
                "BankBranch b, MdaInfo mda, MdaDeptMap m, SalaryInfo s, SchoolInfo si where e.id = p.employee.id and p.runMonth = :pRunMonth " +
                "and p.runYear = :pRunYear and b.id = p.bankBranch.id and p.mdaDeptMap.id = m.id and m.mdaInfo.id = mda.id and p.schoolInfo.id = si.id " +
                "and p.salaryInfo.id = s.id and p.businessClientId = :pBusClientIdVar and p.netPay > 0";

        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("pRunMonth", pRunMonth);
        query.setParameter("pRunYear", pRunYear);
        query.setParameter("pBusClientIdVar", bc.getBusinessClientInstId());

        wRetVal = (ArrayList) query.list();

        if (wRetVal.size() > 0) {
            EmployeePayBean p;
            int i;

            for (Object[] o : wRetVal) {
                i = 0;
                p = new EmployeePayBean();
                p.setTaxesPaid(((Double) o[i++]));
                p.setTotalGarnishments(((Double) o[i++]));
                p.setTotalPay(((Double) o[i++]));
                p.setNetPay(((Double) o[i++]));
                p.setEmployeeName(PayrollHRUtils.createDisplayName((String) o[i++], (String) o[i++], o[i++]));
                p.setEmployeeId((String) o[i++]);
                p.setLevelAndStepStr(PayrollUtils.makeLevelAndStep((Integer) o[i++], (Integer) o[i++]));
                p.setMda((String) o[i++]);
                p.setTotalDeductions(((Double) o[i++]));
                p.setContributoryPension(((Double) o[i++]));
                p.setSchoolName((String) o[i++]);
                p.setAccountNumber((String) o[i++]);
                p.setBranchName((String) o[i++]);
                p.setTotalAllowance(((Double) o[i++]));
                p.setMonthlyBasic((Double) o[i++]);

                wRetList.add(p);


            }

        }
//
        return wRetList;
    }


    public List<EmployeePayBean> loadEmployeePayBeanByLgaFromDateToDate(int pRunMonth, int pRunYear, BusinessCertificate bc,
                                                                        int fromLevel, int toLevel, String bank) throws IllegalAccessException, InstantiationException {
        ArrayList<Object[]> wRetVal;
        List wRetList = new ArrayList();
        HashMap<String, Double> map;
        HashMap<Long, HashMap<String, Double>> dedMap = paycheckService.loadPaycheckDeductionTypeByPaycheckId(pRunMonth, pRunYear, bc);

        String hqlQuery = "select p.id, mda.mdaType.mdaTypeCode, p.rent,p.transport,p.inducement,p.hazard,p.callDuty,p.otherAllowance," +
                "p.taxesPaid,p.unionDues,p.nhf,p.totalGarnishments,p.totalPay,p.netPay,e.id, " +
                " e.firstName, e.lastName, e.initials, e.employeeId, p.salaryInfo.id, r.name,e.employeeId," +
                "p.mdaDeptMap.id,mda.id,mda.name,mda.codeName," +
                "p.tws,p.principalAllowance, p.meal,p.utility,p.ruralPosting," +
                "p.journal,p.domesticServant,p.driversAllowance,p.adminAllowance,p.entertainment," +
                "p.academicAllowance,p.tss,p.arrears,p.otherArrears,p.salaryDifference," +
                "p.specialAllowance,p.contractAllowance,p.totalDeductions,p.furniture,p.developmentLevy, " +
                "p.contributoryPension,p.noOfDays,p.payByDaysInd,p.schoolInfo.id, p.accountNumber, bi.name," +
                "b.branchSortCode,p.totalAllowance,p.employeeType.id,p.monthlyBasic, p.employee, st.name, lg.name, d.name, p.bvnNo, p.pfaInfo, p.pensionPinCode, h.incrementalDate from " + IppmsUtils.getPaycheckTableName(bc) + " p, " + IppmsUtils.getEmployeeTableName(bc) + " e, " +
                "BankBranch b, BankInfo bi, MdaInfo mda, MdaDeptMap m, Department d, Rank r, SalaryInfo s, SalaryType st, LGAInfo lg, HiringInfo h where e.id = p.employee.id and p.runMonth = :pRunMonth " +
                "and p.runYear = :pRunYear and p.bankBranch.id = b.id and b.bankInfo.id = bi.id and p.salaryInfo.id = s.id and e.lgaInfo.id = lg.id and s.salaryType.id = st.id and p.mdaDeptMap.id = m.id and m.department.id = d.id and e.rank.id = r.id and m.mdaInfo.id = mda.id " +
                "and e.id = h.employee.id and p.netPay > 0 ";

        if ((fromLevel > 0) && (toLevel > 0)) {
            hqlQuery += "and s.level >= :fromLevelVar and s.level <= :toLevelVar ";
        }
        if (IppmsUtils.isNotNullOrEmpty(bank)) {
            hqlQuery += "and bi.name = :pBankVar ";
        }

        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("pRunMonth", pRunMonth);
        query.setParameter("pRunYear", pRunYear);

        if (IppmsUtils.isNotNullOrEmpty(bank))
            query.setParameter("pBankVar", bank);
        if ((fromLevel > 0) && (toLevel > 0)) {
            query.setParameter("fromLevelVar", fromLevel);
            query.setParameter("toLevelVar", toLevel);
        }

        wRetVal = (ArrayList) query.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                EmployeePayBean p = new EmployeePayBean();
                int i = 0;
                Object t;
                p.setId((Long) o[i++]);
                p.setObjectInd((int) o[i++]);
                p.setRent(((Double) o[i++]));
                p.setTransport(((Double) o[i++]));
                p.setInducement(((Double) o[i++]));
                p.setHazard(((Double) o[i++]));
                p.setCallDuty(((Double) o[i++]));
                p.setOtherAllowance(((Double) o[i++]));
                p.setTaxesPaid(((Double) o[i++]));
                p.setMonthlyTax(p.getTaxesPaid());
                p.setUnionDues(((Double) o[i++]));
                p.setNhf(((Double) o[i++]));
                p.setTotalGarnishments(((Double) o[i++]));
                p.setTotalPay(((Double) o[i++]));
                p.setNetPay(((Double) o[i++]));
                Long EmpId = (Long) o[i++];
                String EmpFirstName = (String) o[i++];
                String EmpLastName = (String) o[i++];
                String EmpInitials = (String) o[i++];
                Employee e = new Employee(EmpId, EmpFirstName, EmpLastName, EmpInitials);
                if (IppmsUtils.isNotNullOrEmpty(EmpInitials)) {
                    p.setEmployeeName(EmpFirstName + ", " + EmpLastName + ", " + EmpInitials.substring(0, 1).toUpperCase() + ".");
                } else {
                    p.setEmployeeName(EmpFirstName + ", " + EmpLastName);
                }
                p.setEmployeeId((String) o[i++]);
                SalaryInfo s = new SalaryInfo((Long) o[i++]);

                e.setSalaryInfo(s);
                p.setRankName((String) o[i++]);
                e.setEmployeeId((String) o[i++]);
                p.setMdaDeptMap(new MdaDeptMap((Long) o[i++]));
                p.getMdaDeptMap().setMdaInfo(new MdaInfo((Long) o[i++], (String) o[i++], (String) o[i++]));

                p.setTws(((Double) o[i++]));

                p.setPrincipalAllowance(((Double) o[i++]));
                p.setMeal(((Double) o[i++]));
                p.setUtility(((Double) o[i++]));
                p.setRuralPosting(((Double) o[i++]));
                p.setJournal(((Double) o[i++]));
                p.setDomesticServant(((Double) o[i++]));
                p.setDriversAllowance(((Double) o[i++]));
                p.setAdminAllowance(((Double) o[i++]));
                p.setEntertainment(((Double) o[i++]));
                p.setAcademicAllowance(((Double) o[i++]));
                p.setTss(((Double) o[i++]));
                p.setArrears(((Double) o[i++]));
                p.setOtherArrears(((Double) o[i++]));
                p.setSalaryDifference(((Double) o[i++]));
                p.setSpecialAllowance(((Double) o[i++]));
                p.setContractAllowance(((Double) o[i++]));
                p.setTotalDeductions(((Double) o[i++]));
                p.setFurniture(((Double) o[i++]));
                p.setDevelopmentLevy(((Double) o[i++]));
                p.setContributoryPension(((Double) o[i++]));
                p.setNoOfDays(((Integer) o[i++]).intValue());
                p.setPayByDaysInd(((Integer) o[i++]).intValue());
                Object wObj = o[i++];
                if (wObj != null) {
                    e.setSchoolInfo(new SchoolInfo((Long) wObj));
                    p.setSchoolName(e.getSchoolInfo().getName());
                } else {
                    e.setSchoolName("N/A");
                }
                System.out.println("school here is: " + p.getSchoolName());
                Object wObj2 = o[i++];
                if (wObj2 != null)
                    p.setAccountNumber((String) wObj2);
                else {
                    p.setAccountNumber("N/A");
                }
                p.setBranchName((String) o[i++]);

                p.setBranchSortCode((String) o[i++]);
                p.setTotalAllowance(((Double) o[i++]));
                p.setTotalAllowanceStr(String.valueOf(p.getTotalAllowance()));
                p.setEmployeeType(new EmployeeType((Long) o[i++]));
                p.setMonthlyBasic((Double) o[i++]);
                p.setEmployee((AbstractEmployeeEntity) o[i++]);
                p.setSalaryInfo(s);
                p.setSalaryTypeName((String) o[i++]);
                p.setLgaName((String) o[i++]);
                p.setDepartmentName((String) o[i++]);
                p.setBvnNo((String) o[i++]);
                p.setPfaInfo((PfaInfo) o[i++]);
                p.setPensionPinCode((String) o[i++]);
                p.setIncrementalDate((LocalDate) o[i++]);

                map = dedMap.get(EmpId);
                if (map != null)
                    p = setDeductionValues(map, p);


                wRetList.add(p);

            }

        }
//
        return wRetList;
    }

    private synchronized EmployeePayBean setDeductionValues(HashMap<String, Double> map, EmployeePayBean p) {

        Set<String> keys = map.keySet();

        for (String m : keys) {

            if (m.equalsIgnoreCase("MAHWUN")) {
                p.setMahwun(map.get(m));
            } else if (m.equalsIgnoreCase("NULGE")) {
                p.setNulge(map.get(m));
            } else if (m.equalsIgnoreCase("NACHP")) {
                p.setNachp(map.get(m));
            } else if (m.equalsIgnoreCase("NANNM")) {
                p.setNannm(map.get(m));
            } else if (m.equalsIgnoreCase("NHF")) {
                p.setNhf(map.get(m));
            } else if (m.startsWith("ADV")) {
                p.setAdv(map.get(m));
                p.setAdvName(m);
            } else if (m.startsWith("COOP")) {
                p.setCoop(map.get(m));
                p.setCoopName(m);
            } else if (m.startsWith("DEP")) {
                p.setDep(map.get(m));
                p.setDepName(m);
            }
        }
        return p;
    }


    public List<?> loadBankBranchesAsModelData() {
        String wHql = "select bb.branchSortCode,bb.name,b.id,b.name,b.sortCode " +
                "from BankBranch bb, BankInfo b " +
                "where b.id = bb.bankInfo.id ";
        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);

        List wRetList = new ArrayList();
        List<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        HashMap<Long, BankInfo> wBanks = new HashMap<Long, BankInfo>();
        for (Object[] o : wRetVal) {
            BankPVSummaryBean b = new BankPVSummaryBean();

            b.setBankBranchName((String) o[1]);
            b.setBankName((String) o[3]);
            b.setBankBranchSortCode((String) o[0]);
            b.setBankSortCode((String) o[4]);

            wRetList.add(b);
        }
        return wRetList;

    }


}
