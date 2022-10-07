package com.osm.gnl.ippms.ogsg.controllers.report.service;

import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.domain.beans.MdaEmployeeMiniBean;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.domain.report.IndividualEmployeeBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.type.LocalDateType;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Repository
@Transactional(readOnly = true)
@Slf4j
public class EmployeeReportService {

    private final SessionFactory sessionFactory;

    public EmployeeReportService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<MdaEmployeeMiniBean> getEmployeeAuditLogByDateAndCode(LocalDate pFromDate, LocalDate pToDate, String pInsertCode, BusinessCertificate bc) {
        String wHql = "select e.lastName,e.employeeId, e.firstName, e.initials, h.gender, h.birthDate,h.hireDate, "
                + "m.id,m.name, l.name,s.level,s.step,st.name, h.pensionStartDate"
                + " from " + IppmsUtils.getEmployeeTableName(bc) + " e,HiringInfo h,SalaryInfo s, SalaryType st,LGAInfo l, MdaInfo m where h. " + bc.getEmployeeIdJoinStr() + " = e.id"
                + " and m.id = e.mdaDeptMap.mdaInfo.id and e.creationDate >= :startDate and e.creationDate <= :endDate and e.salaryInfo.id = s.id "
                + "and s.salaryType.id = st.id and e.lgaInfo.id = l.id and e.businessClientId = " + bc.getBusinessClientInstId() + " order by e.lastName ";

        List<MdaEmployeeMiniBean> wRetList = new ArrayList<>();

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("startDate", Timestamp.valueOf(LocalDateTime.of(pFromDate, LocalTime.MIN)));
        query.setParameter("endDate", Timestamp.valueOf(LocalDateTime.of(pToDate, LocalTime.MAX)));
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();

        if (wRetVal.size() > 0) {
            MdaEmployeeMiniBean s = null;
            int i = 0;
            for (Object[] o : wRetVal) {

                s = new MdaEmployeeMiniBean();

                s.setLastName((String) o[i++]);
                s.setEmployeeId((String) o[i++]);
                s.setFirstName((String) o[i++]);
                s.setInitials((String) o[i++]);
                s.setGenderCode((String) o[i++]);
                s.setBirthDateStr(String.valueOf(o[i++]));
                s.setHireDateStr(String.valueOf(o[i++]));
                s.setMdaInstId((Long) o[i++]);
                s.setMda((String) o[i++]);
                s.setLga((String) o[i++]);
                s.setSalaryLevel(((Integer) o[i++]));
                s.setSalaryStep(((Integer) o[i++]));
                s.setSalaryScale((String) o[i++]);
                s.setPensionStartDate((LocalDate) o[i++]);
                wRetList.add(s);
                i = 0;
            }
        }

        return wRetList;
    }


    public IndividualEmployeeBean indEmpPay(Long eID, BusinessCertificate bc) throws Exception {
        List<IndividualEmployeeBean> wCol = new ArrayList<>();
        Session session = sessionFactory.openSession();
        //EntityManager em = session.getEntityManagerFactory().createEntityManager();
        IndividualEmployeeBean wIEB = null;

        String sql1 = "select t.name as tName, e.lastName, e.firstName, e.initials,e.employeeId, h.gender, e.address1,"
                + " c.fullName as cFullName,e.country,e.gsmNumber,e.email,"
                + " s.level, s.step, r.name as rName, si.fullName  as sFullName,h.hireDate, m.name as mName,l.name,"
                + " s.monthlyBasicSalary, pm.accountNumber, pt.name, b.name,"
                + " et.name, g.name as gName,e.statusIndicator, h.birthDate, ss.name as ssName, h.confirmDate"
                + " from " + IppmsUtils.getEmployeeTableName(bc) + " e, SalaryInfo s, Title t, PaymentMethodInfo pm,"
                + " PaymentMethodTypes pt, Religion r, MaritalStatus m, "
                + " State si, HiringInfo h, LGAInfo l, BankBranch b,"
                + " EmployeeType et, MdaInfo g, MdaDeptMap gm, SalaryType ss, City c"
                + " where e.id ='"
                + eID
                + "'"
                + " and e.salaryInfo.id = s.id"
                + " and e.title.id = t.id"
                + " and pm.employee.id = e.id"
                + " and pm.paymentMethodTypes.id = pt.id"
                + " and e.religion.id = r.id"
//                        + " and e.state_code = si.state_code"
                + " and e.id = h.employee.id"
                + " and h.maritalStatus.id = m.id"
                + " and e.lgaInfo.id = l.id"
                + " and pm.bankBranches.id = b.id"
                + " and e.employeeType.id = et.id"
                + " and e.mdaDeptMap.id = gm.id"
                + " and gm.mdaInfo.id = g.id"
                + " and s.salaryType.id = ss.id"
                + " and e.city.id = c.id"
                + " and c.state.id = si.id";

//                    NativeQuery query = session.createSQLQuery(sql1);
        Query query = this.sessionFactory.getCurrentSession().createQuery(sql1);
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {
            int i = 0;
            for (Object[] o : wRetVal) {
                wIEB = new IndividualEmployeeBean();
                wIEB.setTitle((String) o[i++]);
                wIEB.setEmpLastName((String) o[i++]);
                wIEB.setEmpFirstName((String) o[i++]);
                wIEB.setInitials(PayrollHRUtils.treatNull((String) o[i++]));
                wIEB.setEmpNumber((String) o[i++]);
                if (o[i++].equals("M"))
                    wIEB.setSex("Male");
                else
                    wIEB.setSex("Female");
                wIEB.setAddress1(o[i++] + " " + o[i++]);
                wIEB.setNationality((String) o[i++]);
                wIEB.setMobile((String) o[i++]);
                wIEB.setEmail((String) o[i++]);
                wIEB.setGradeLevel(o[i++] + "/" + o[i++]);
                wIEB.setReligion((String) o[i++]);
                wIEB.setStateOfOrigin((String) o[i++]);
                wIEB.setDateOfEmployment((LocalDate) o[i++]);
                wIEB.setMarital((String) o[i++]);
                wIEB.setLgArea((String) o[i++]);
                wIEB.setAnnualBasic(PayrollHRUtils.getDecimalFormat().format(
                        o[i++]));
                wIEB.setAccountNumber((String) o[i++]);
                wIEB.setPaymentMethod((String) o[i++]);
                wIEB.setBankBranch((String) o[i++]);
                wIEB.setStaffCategory((String) o[i++]);
                wIEB.setAgency((String) o[i++]);
                if ((int) (o[i++]) == 0)
                    wIEB.setEmploymentStatus("ACTIVE");
                else
                    wIEB.setEmploymentStatus("TERMINATED");
                // wIEB.setSchool(rs1.getString(26));
                wIEB.setDob((LocalDate) o[i++]);
                wIEB.setJobtitle((String) o[i++]);
                wIEB.setCapturedMonth((LocalDate) o[i++]);
                wCol.add(wIEB);
            }
        }
        return wIEB;
    }


    public List<AbstractSpecialAllowanceEntity> loadTerminatedEmployeesSpecialAllowances(
            LocalDate pStartDate, LocalDate pEndDate, Long pTerminateReasonInstId,
            boolean pUsingDates, boolean pUsingTermId, Long pEmpId, Long pUserId,
            Long pMdaId, Long pSchoolId, String pPayPeriod, BusinessCertificate bc) {
        List wRetList = new ArrayList();

        boolean wUseMda = IppmsUtils.isNotNullAndGreaterThanZero(pMdaId);

        boolean wUsePayPeriod = pPayPeriod.length() > 1;

        ArrayList<Object[]> wRetVal = new ArrayList<>();
        String hqlQuery = "";

        if (bc.isPensioner()) {
            hqlQuery = "select e.spec_allow_info_inst_id AS saID,e.amount AS saAMT,k.pensioner_inst_id AS empID,l.spec_allow_inst_id AS satID,e.start_date AS saSD" +
                    ",e.end_date AS saED,p.pay_types_inst_id AS ptID,p.name AS payType,l.taxable AS satTIND," +
                    "l.name AS satNAME, l.arrears_ind AS arrIND from ippms_spec_allow_info e,ippms_hire_info h, ippms_pensioner k,ippms_spec_allow_type l," +
                    "ippms_pay_types p,ippms_termination_audit t where k.business_client_inst_id = :pBizIdVar and e.pensioner_inst_id = k.pensioner_inst_id " +
                    "and l.spec_allow_inst_id = e.spec_allow_type_inst_id and p.pay_types_inst_id = e.pay_types_inst_id and t.pensioner_inst_id = k.pensioner_inst_id " +
                    "and t.termination_date = h.pension_end_date and t.term_reason_inst_id = h.term_reason_inst_id " +
                    "and e.expired = 1 and h.terminate_inactive = 'Y' and h.pension_end_date is not null ";
        } else {
            hqlQuery = "select e.spec_allow_info_inst_id AS saID,e.amount AS saAMT,k.employee_inst_id AS empID,l.spec_allow_inst_id AS satID,e.start_date AS saSD" +
                    ",e.end_date AS saED,p.pay_types_inst_id AS ptID,p.name AS payType,l.taxable AS satTIND," +
                    "l.name AS satNAME, l.arrears_ind AS arrIND from ippms_spec_allow_info e,ippms_hire_info h, ippms_employee k,ippms_spec_allow_type l," +
                    "ippms_pay_types p,ippms_termination_audit t where k.business_client_inst_id = :pBizIdVar and e.employee_inst_id = k.employee_inst_id " +
                    "and l.spec_allow_inst_id = e.spec_allow_type_inst_id and p.pay_types_inst_id = e.pay_types_inst_id and t.employee_inst_id = k.employee_inst_id " +
                    "and t.termination_date = h.terminate_date and t.term_reason_inst_id = h.term_reason_inst_id " +
                    "and e.expired = 1 and h.terminate_inactive = 'Y' and h.terminate_date is not null ";
        }


        NativeQuery wQuery = null;
        if (pUsingDates) {
            if (bc.isPensioner())
                hqlQuery += "and h.pension_end_date >  :pStartDate and h.pension_end_date <  :pEndDate ";
            else
                hqlQuery += "and h.terminate_date >  :pStartDate and h.terminate_date <  :pEndDate ";

        }
        if (pUsingTermId) {
            hqlQuery += "and h.term_reason_inst_id = :pTermReasonId ";


        }
        if (pEmpId != null && pEmpId > 0) {
            if (bc.isPensioner())
                hqlQuery += "and k.pensioner_inst_id = :pEmpIdVar ";
            else
                hqlQuery += "and k.employee_inst_id = :pEmpIdVar ";

        }
        if (pUserId != null && pUserId > 0) {
            hqlQuery += "and t.user_inst_id = :pLoginIdVar ";


        }
        if (IppmsUtils.isNotNullAndGreaterThanZero(pSchoolId)) {
            hqlQuery += "and t.school_inst_id = :pSchoolInstIdVar ";

        }
        if (wUseMda) {
            hqlQuery += "and t.mda_inst_id = :pMdaInstIdVar ";

        }
        if (wUsePayPeriod) {
            hqlQuery += "and t.pay_period = :pPayPeriodVar ";

        }

        wQuery = this.sessionFactory.getCurrentSession().createNativeQuery(hqlQuery)
                .addScalar("saID", StandardBasicTypes.LONG)
                .addScalar("saAMT", StandardBasicTypes.DOUBLE)
                .addScalar("empID", StandardBasicTypes.LONG)
                .addScalar("satID", StandardBasicTypes.LONG)
                .addScalar("saSD", LocalDateType.INSTANCE)
                .addScalar("saED", LocalDateType.INSTANCE)
                .addScalar("ptID", StandardBasicTypes.LONG)
                .addScalar("payType", StandardBasicTypes.STRING)
                .addScalar("satTIND", StandardBasicTypes.INTEGER)
                .addScalar("satNAME", StandardBasicTypes.STRING)
                .addScalar("arrIND", StandardBasicTypes.INTEGER);

        if (pUsingDates) {
            wQuery.setParameter("pStartDate", pStartDate);
            wQuery.setParameter("pEndDate", pEndDate);
        }
        if (wUseMda) {
            wQuery.setParameter("pMdaInstIdVar", pMdaId);

        }
        if (IppmsUtils.isNotNullAndGreaterThanZero(pSchoolId)) {
            wQuery.setParameter("pSchoolInstIdVar", pSchoolId);
        }
        if (wUsePayPeriod) {

            wQuery.setParameter("pPayPeriodVar", pPayPeriod);
        }
        if (IppmsUtils.isNotNullAndGreaterThanZero(pUserId))
            wQuery.setParameter("pLoginIdVar", pUserId);

        if (IppmsUtils.isNotNullAndGreaterThanZero(pEmpId))
            wQuery.setParameter("pEmpIdVar", pEmpId);
        if (pUsingTermId)
            wQuery.setParameter("pTermReasonId", pTerminateReasonInstId);

        wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        wRetVal = (ArrayList) wQuery.list();

        if (wRetVal.size() > 0) {
            AbstractSpecialAllowanceEntity e;
            for (Object[] o : wRetVal) {
                int i = 0;
                e = IppmsUtils.makeSpecialAllowanceInfoObject(bc);
                e.setId((Long) o[i++]);
                e.setAmount(((Double) o[i++]));
                e.setEmployee(new Employee((Long) o[i++]));

                SpecialAllowanceType et = new SpecialAllowanceType((Long) o[i++]);
                e.setStartDate((LocalDate) o[i++]);
                e.setEndDate((LocalDate) o[i++]);
                et.setTaxExemptInd(((Integer) o[i++]));


                PayTypes p = new PayTypes((Long) o[i++], (String) o[i++]);

                e.setPayTypes(p);
                e.setName((String) o[i++]);
                et.setArrearsInd((Integer) o[i++]);
                e.setSpecialAllowanceType(et);
                wRetList.add(e);
            }

        }

        return wRetList;
    }


    public List<HiringInfo> getActiveEmployees(BusinessCertificate bc, int pRunMonth, int pRunYear) {
        List<HiringInfo> wHireInfoList = new ArrayList<>();

        String wHql;
        boolean useDates = true;
        if (pRunYear == 0)
            useDates = false;
        if (bc.isPensioner()) {
            if (!useDates)
                wHql = "select e.id,e.employeeId,e.lastName, e.firstName, e.initials," +
                        "h.terminateDate,h.pensionStartDate,h.pensionEndDate,h.yearlyPensionAmount,h.monthlyPensionAmount," +
                        "m.name,st.name,s.level,s.step,coalesce(h.tin,'Not Supplied')" +
                        " from Pensioner e,HiringInfo h,SalaryInfo s, MdaInfo m, SalaryType st " +
                        "where h.pensioner.id = e.id and s.id = e.salaryInfo.id and st.id = s.salaryType.id and h.pensionEndDate is null and e.statusIndicator = 0 " +
                        "and m.id = e.mdaDeptMap.mdaInfo.id and e.businessClientId = " + bc.getBusinessClientInstId();
            else
                wHql = "select e.id,e.employeeId,e.lastName, e.firstName, e.initials," +
                        "h.terminateDate,h.pensionStartDate,h.pensionEndDate,h.yearlyPensionAmount,h.monthlyPensionAmount," +
                        "m.name,st.name,s.level,s.step,coalesce(h.tin,'Not Supplied')" +
                        " from Pensioner e,HiringInfo h,SalaryInfo s, MdaInfo m, SalaryType st, " + IppmsUtils.getPaycheckTableName(bc) + " p " +
                        "where p.employee.id = e.id and h.pensioner.id = e.id and s.id = e.salaryInfo.id and st.id = s.salaryType.id and h.pensionEndDate is null and e.statusIndicator = 0 " +
                        "and m.id = e.mdaDeptMap.mdaInfo.id and e.businessClientId = " + bc.getBusinessClientInstId() + " and p.runMonth = :pRunMonthVar and p.runYear = :pRunYearVar ";
        } else {
            if (!useDates)
                wHql = "select e.id,e.employeeId,e.lastName, e.firstName, e.initials," +
                        "h.birthDate,h.hireDate,h.expectedDateOfRetirement,ms.annualGross,ms.monthlyGross, " +
                        "m.name,st.name,s.level,s.step,coalesce(h.tin,'Not Supplied')" +
                        " from Employee e,MiniSalaryInfoDao ms,HiringInfo h,SalaryInfo s, SalaryType st, MdaInfo m" +
                        " where h.employee.id = e.id and e.salaryInfo.id = ms.salaryInfoId and st.id = s.salaryType.id and e.salaryInfo.id = s.id " +
                        " and m.id = e.mdaDeptMap.mdaInfo.id and e.businessClientId = " + bc.getBusinessClientInstId() + " " +
                        "and h.terminateDate is null and e.statusIndicator = 0 ";
            else
                wHql = "select e.id,e.employeeId,e.lastName, e.firstName, e.initials," +
                        "h.birthDate,h.hireDate,h.expectedDateOfRetirement,ms.annualGross,ms.monthlyGross, " +
                        "m.name,st.name,s.level,s.step,coalesce(h.tin,'Not Supplied')" +
                        " from Employee e,MiniSalaryInfoDao ms,HiringInfo h,SalaryInfo s, SalaryType st, MdaInfo m, " + IppmsUtils.getPaycheckTableName(bc) + " p " +
                        " where p.employee.id = e.id and h.employee.id = e.id and e.salaryInfo.id = ms.salaryInfoId and st.id = s.salaryType.id and e.salaryInfo.id = s.id " +
                        " and m.id = e.mdaDeptMap.mdaInfo.id and e.businessClientId = " + bc.getBusinessClientInstId() + " and p.runMonth = :pRunMonthVar and p.runYear = :pRunYearVar  " +
                        "and h.terminateDate is null and e.statusIndicator = 0 ";
        }

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);

        if (useDates) {
            query.setParameter("pRunMonthVar", pRunMonth);
            query.setParameter("pRunYearVar", pRunYear);
        }

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();

        if (wRetVal.size() > 0) {
            HiringInfo s;
            int i = 0;
            for (Object[] o : wRetVal) {

                s = new HiringInfo();
                s.setId((Long) o[i++]);
                s.setEmployeeId((String) o[i++]);
                s.setName(PayrollHRUtils.createDisplayName((String) o[i++], (String) o[i++], o[i++]));
                if (bc.isPensioner()) {
                    s.setTerminateDate((LocalDate) o[i++]);
                    s.setPensionStartDate((LocalDate) o[i++]);
                    s.setPensionEndDate((LocalDate) o[i++]);
                } else {
                    s.setBirthDate((LocalDate) o[i++]);
                    s.setHireDate((LocalDate) o[i++]);
                    s.setExpectedDateOfRetirement((LocalDate) o[i++]);

                }
                s.setYearlyPensionAmount((Double) o[i++]);
                s.setMonthlyPensionAmount((Double) o[i++]);
                s.setProposedMda((String) o[i++]);
                s.setOldLevelAndStep(o[i++] + " : " + PayrollUtils.makeLevelAndStep((Integer) o[i++], (Integer) o[i++]));
                s.setTin((String)o[i++]);
                wHireInfoList.add(s);
                i = 0;
            }
        }


        return wHireInfoList;
    }

    public List<HiringInfo> makeNominalRole(BusinessCertificate bc, int pMonth, int pYear) {
        HashMap<Long, Long> wIDFilterMap = new HashMap<>();
        List<HiringInfo> results = new ArrayList<>();

        boolean useDates = true;
        if (pYear == 0)
            useDates = false;
        String hqlQuery;

        if (useDates) {
            if (bc.isPensioner()) {
                hqlQuery = "select h.id,e.employeeId,e.lastName,e.firstName,e.initials, h.gender,h.birthDate,h.pensionStartDate,h.confirmDate,h.terminateDate,h.amAliveDate,h.staffInd,h.yearlyPensionAmount,h.monthlyPensionAmount, " +
                        " m.id, m.name, coalesce(h.tin,'Not Supplied') from Pensioner e , " + IppmsUtils.getPaycheckTableName(bc) + " epb, MdaDeptMap mdm, MdaInfo m,"
                        + ", HiringInfo h where epb.employee.id = e.id and h.employee.id = e.id and epb.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = m.id  " +
                        "and epb.runMonth = :pRunMonthVar and epb.runYear = :pRunYearVar and ( epb.netPay > 0 or (epb.netPay = 0 and epb.suspendedInd = 1)) and e.businessClientId = :pBizIdVar ";

            } else {
                hqlQuery = "select h.id,e.employeeId,e.lastName,e.firstName,e.initials, h.gender,h.birthDate,h.hireDate,h.confirmDate,h.terminateDate,h.expectedDateOfRetirement,h.staffInd,ms.annualGross,ms.monthlyGross,s.level,s.step,st.name, " +
                        " m.id, m.name,coalesce(h.tin,'Not Supplied') from Employee e,MiniSalaryInfoDao ms, SalaryInfo s, SalaryType st, " + IppmsUtils.getPaycheckTableName(bc) + " epb, MdaDeptMap mdm, MdaInfo m"
                        + ", HiringInfo h where epb.employee.id = e.id and h.employee.id = e.id and epb.salaryInfo.id = s.id and s.salaryType.id = st.id and epb.salaryInfo.id = ms.salaryInfoId " +
                        "and epb.runMonth = :pRunMonthVar and epb.runYear = :pRunYearVar and ( epb.netPay > 0 or (epb.netPay = 0 and epb.suspendedInd = 1)) and e.businessClientId = :pBizIdVar ";

            }

        } else {
            if (bc.isPensioner()) {
                hqlQuery = "select h.id,e.employeeId,e.lastName,e.firstName,e.initials, h.gender,h.birthDate,h.pensionStartDate,h.confirmDate,h.terminateDate,h.amAliveDate,h.staffInd," +
                        "h.yearlyPensionAmount,h.monthlyPensionAmount,  m.id, m.name,coalesce(h.tin,'Not Supplied') " +
                        "from Pensioner e,  MdaDeptMap mdm, MdaInfo m, HiringInfo h where h.employee.id = e.id and e.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = m.id  and e.businessClientId = :pBizIdVar";

            } else {
                hqlQuery = "select h.id,e.employeeId,e.lastName,e.firstName,e.initials, h.gender,h.birthDate,h.hireDate,h.confirmDate,h.terminateDate,h.expectedDateOfRetirement,h.staffInd,ms.annualGross,ms.monthlyGross, "
                        + "s.level,s.step,st.name, m.id, m.name,coalesce(h.tin,'Not Supplied') "
                        + "from Employee e, SalaryInfo s, SalaryType st, HiringInfo h ,MdaDeptMap mdm, MdaInfo m,MiniSalaryInfoDao ms " +
                        "where h.employee.id = e.id and e.salaryInfo.id = s.id and s.salaryType.id = st.id and s.id = ms.salaryInfoId and e.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = m.id and e.businessClientId = :pBizIdVar";
            }
        }

        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);

        if (useDates) {
            query.setParameter("pRunMonthVar", pMonth);
            query.setParameter("pRunYearVar", pYear);
        }
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());


        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            HiringInfo h;
            int i = 0;

            for (Object[] o : wRetVal) {
                h = new HiringInfo((Long) o[i++]);

                h.setEmployeeId((String) o[i++]);
                h.setName(
                        PayrollHRUtils.createDisplayName((String) o[i++], (String) o[i++], IppmsUtils.treatNull((String) o[i++])));

                h.setGender((String) o[i++]);
                h.setBirthDate((LocalDate) o[i++]);
                if (bc.isPensioner())
                    h.setPensionStartDate((LocalDate) o[i++]);
                else
                    h.setHireDate((LocalDate) o[i++]);
                h.setConfirmDate((LocalDate) o[i++]);
                h.setTerminateDate((LocalDate) o[i++]);
                h.setExpectedDateOfRetirement((LocalDate) o[i++]);
                h.setStaffInd((Integer) o[i++]);

                h.setYearlyPensionAmount((Double) o[i++]);
                h.setMonthlyPensionAmount((Double) o[i++]);
                if (!bc.isPensioner()) {
                    h.setLevelAndStepStr(PayrollUtils.makeLevelAndStep((Integer) o[i++], (Integer) o[i++]));
                    h.setSalaryTypeName((String) o[i++]);
                }

                h.setMdaId((Long) o[i++]);
                h.setProposedMda((String) o[i++]);
                h.setTin((String)o[i++]);
                if (useDates)
                    wIDFilterMap.put(h.getId(), h.getId());
                results.add(h);

                i = 0;
            }

        }

        /*
         * }
         */

        // now add the retirees
        if (useDates)
            results = addRetireesForNominalRole(bc, results, pMonth, pYear, wIDFilterMap);
        return results;

    }

    private List<HiringInfo> addRetireesForNominalRole(BusinessCertificate bc, List<HiringInfo> mainList, int pMonth, int pYear, HashMap<Long, Long> pWIDFilterMap) {


        if (pMonth == 1) {
            pMonth = 12;
            pYear -= 1;
        } else {
            pMonth -= 1;

        }

        LocalDate endDate = LocalDate.of(pYear, pMonth, 1);

        LocalDate _endDate = LocalDate.of(pYear, pMonth, endDate.lengthOfMonth());

        LocalDate startDate = LocalDate.of(pYear,1,1);

        String hqlQuery;

        if (bc.isPensioner()) {
            hqlQuery = "select h.id,e.employeeId,e.lastName,e.firstName,e.initials, h.gender,h.birthDate,h.pensionStartDate, h.pensionEndDate,h.confirmDate,h.terminateDate,h.amAliveDate,h.staffInd,h.yearlyPensionAmount,h.monthlyPensionAmount, " +
                    " m.id, m.name,coalesce(h.tin,'Not Supplied') from Pensioner e , MdaDeptMap mdm, MdaInfo m,"
                    + ", HiringInfo h where h.employee.id = e.id and e.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = m.id  " +
                    "and h.pensionEndDate is not null and h.pensionEndDate >= :pStartDate and h.pensionEndDate <= :pTermDate and e.businessClientId = :pBizIdVar ";
        } else {
            hqlQuery = "select h.id,e.employeeId,e.lastName,e.firstName,e.initials, h.gender,h.birthDate,h.hireDate,h.confirmDate,h.terminateDate,h.expectedDateOfRetirement,h.staffInd,ms.annualGross,ms.monthlyGross,s.level,s.step,st.name, " +
                    " m.id, m.name,coalesce(h.tin,'Not Supplied') from Employee e, SalaryInfo s, SalaryType st, MdaDeptMap mdm, MdaInfo m,MiniSalaryInfoDao ms"
                    + ", HiringInfo h where h.employee.id = e.id and e.salaryInfo.id = ms.salaryInfoId and ms.salaryInfoId = s.id and s.salaryType.id = st.id  " +
                    "and h.terminateDate is not null and h.terminateDate >= :pStartDate and h.terminateDate <= :pTermDate and e.businessClientId = :pBizIdVar ";
        }

        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);

        query.setParameter("pStartDate", startDate);

        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());
        query.setParameter("pTermDate", _endDate);


        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            HiringInfo h;
            int i;

            for (Object[] o : wRetVal) {
                i = 0;
                h = new HiringInfo((Long) o[i++]);

                if (pWIDFilterMap.containsKey(h.getId())) {

                    continue;
                }
                h.setEmployeeId((String) o[i++]);
                h.setName(PayrollHRUtils.createDisplayName((String) o[i++], (String) o[i++], IppmsUtils.treatNull((String) o[i++])));

                h.setGender((String) o[i++]);
                h.setBirthDate((LocalDate) o[i++]);
                if (bc.isPensioner()) {
                    h.setPensionStartDate((LocalDate) o[i++]);
                    h.setPensionEndDate((LocalDate)o[i++]);
                }else {
                    h.setHireDate((LocalDate) o[i++]);
                }
                h.setConfirmDate((LocalDate) o[i++]);
                h.setTerminateDate((LocalDate) o[i++]);
                h.setExpectedDateOfRetirement((LocalDate) o[i++]);
                h.setStaffInd((Integer) o[i++]);
                h.setYearlyPensionAmount((Double) o[i++]);
                h.setMonthlyPensionAmount((Double) o[i++]);
                if (!bc.isPensioner()) {
                   h.setLevelAndStepStr(PayrollUtils.makeLevelAndStep((Integer) o[i++], (Integer) o[i++]));
                   h.setSalaryTypeName((String) o[i++]);

                }

                h.setMdaId((Long) o[i++]);
                h.setProposedMda((String) o[i++]);
                h.setTin((String)o[i++]);
                mainList.add(h);

            }

        }


        return mainList;
    }

}

