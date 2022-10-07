/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.base.dao.IPaycheckDao;
import com.osm.gnl.ippms.ogsg.control.entities.EmployeeType;
import com.osm.gnl.ippms.ogsg.control.entities.Rank;
import com.osm.gnl.ippms.ogsg.control.entities.Title;
import com.osm.gnl.ippms.ogsg.domain.beans.BankPVSummaryBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.location.domain.City;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckDeduction;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckGarnishment;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payslip.beans.MDAPPaySlipSummaryBean;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service("paycheckService")
@Repository
@Transactional(readOnly = true)
public class PaycheckService {


    private final GenericService genericService;

    private final SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    private final IPaycheckDao paycheckDao;

    @Autowired
    public PaycheckService(GenericService genericService, SessionFactory sessionFactory, final IPaycheckDao paycheckDao) {
        this.genericService = genericService;
        this.sessionFactory = sessionFactory;
        this.paycheckDao = paycheckDao;
    }

    public List<?> loadEmployeePayBeanByRunMonthAndRunYear(final BusinessCertificate b, final int runMonth, final int runYear){
        return this.paycheckDao.loadEmployeePayBeanByRunMonthAndRunYear(b,runMonth,runYear);
    }

    public Long getMaxPaycheckIdForEmployee(final BusinessCertificate businessCertificate,final Long pEmpId){
        return this.paycheckDao.getMaxPaycheckIdForEmployee(businessCertificate,pEmpId);
    }

    public LocalDate getPendingPaycheckRunMonthAndYear(BusinessCertificate businessCertificate) {

        return this.paycheckDao.getPendingPaycheckRunMonthAndYear(businessCertificate);
    }

    @Transactional()
    public void updPendPayDedValues(EmpDeductionType pEHB, int monthValue, int year, Object[] valueAsObjectArray, BusinessCertificate businessCertificate) {
        this.paycheckDao.updPendPayDedValues(pEHB,monthValue,year,valueAsObjectArray, businessCertificate);
    }

    public List<NamedEntity> makePaycheckYearList(BusinessCertificate businessCertificate) {return this.paycheckDao.makePaycheckYearList(businessCertificate); }

    @Transactional()
    public void updateMdaForPendingPaycheck(AbstractEmployeeEntity wEmp, LocalDate wCal, Long wSchoolInstId, BusinessCertificate bc) {
        this.paycheckDao.updateMdaForPendingPaychecks(wEmp,wCal,wSchoolInstId,bc);
    }

    public NamedEntityBean createPaySlipDisplayBean(HrMiniBean pHMB, BusinessCertificate bc) {
        NamedEntityBean n = new NamedEntityBean();

        String wSql = "select count(e.id),sum(e.totalPay), sum(e.netPay), sum(e.totalDeductions) from "+IppmsUtils.getPaycheckTableName(bc)+" e ";

        if(pHMB.isSalaryType()) {

            wSql += ", SalaryType st, SalaryInfo si where  st.id = si.salaryType.id and si.id = e.salaryInfo.id and st.id = :pOjectIdVar ";
            if(pHMB.getFromLevel() > 0 && pHMB.getToLevel() > 0) {
                wSql += "and si.level >= :pFromLevelVar and si.level <= :pToLevelVar ";
            }else if(pHMB.getFromLevel() > 0 && pHMB.getToLevel() == 0) {
                wSql += "and si.level >= :pFromLevelVar ";
            }
        }else if(pHMB.isMdaType()) {

            wSql += ", MdaInfo m, MdaDeptMap mdm where e.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = m.id and m.id = :pOjectIdVar ";
        }else {
            //Gotta be Employee...
//            wSql += "where e.employee.id = :pOjectIdVar ";
            wSql += "where e.ogNumber = :pOjectIdVar ";
        }
        n.setPaySlipObjTypeInd(pHMB.getMapId());

        wSql += "and e.runMonth = :pRunMonthVar and e.runYear = :pRunYearVar ";
        Query query = this.sessionFactory.getCurrentSession().createQuery(wSql);
        if(pHMB.isSalaryType()) {

            if(pHMB.getFromLevel() > 0  ) {
                query.setParameter("pFromLevelVar", pHMB.getFromLevel());
            }
            if(pHMB.getToLevel() > 0) {
                query.setParameter("pToLevelVar", pHMB.getToLevel());
            }
        }

        if(!pHMB.isNewEntity())
//            query.setParameter("pOjectIdVar", pHMB.getId());
            query.setParameter("pOjectIdVar", pHMB.getStaffId());

        else {
            if(pHMB.isSalaryType()){
                query.setParameter("pOjectIdVar", pHMB.getSalaryTypeId());
            }else if(pHMB.isMdaType()){
//                query.setParameter("pOjectIdVar", pHMB.getCurrentObjectId());
                query.setParameter("pOjectIdVar", pHMB.getMdaId());
            }
            else{
                //then it is employee
                query.setParameter("pOjectIdVar", pHMB.getStaffId());
            }

        }

        query.setParameter("pRunMonthVar", pHMB.getRunMonth());
        query.setParameter("pRunYearVar", pHMB.getRunYear());


        List<Object[]> wRetVal = (ArrayList)query.list();
        Object value = null;
        for (Object[] o : wRetVal) {
            n.setNoOfActiveEmployees(((Long)o[0]).intValue());
            value = o[1];
            if(value != null) {
                n.setTotalPay((Double) value);
                n.setTotalPayStr(PayrollHRUtils.getDecimalFormat().format(value));
            }
            else {
                n.setTotalPay(0.0D);
            }
            value = o[2];
            if(value != null) {
                n.setNetPay((Double) value);
                n.setNetPayStr(PayrollHRUtils.getDecimalFormat().format(value));
            }
            else {
                n.setNetPay(0.0D);
            }
            value = o[3];
            if(value != null) {
                n.setTotalDeductions((Double) value);
                n.setTotalDeductionsStr(PayrollHRUtils.getDecimalFormat().format(value));
            }
            else {
                n.setTotalDeductions(0.0D);
            }
        }

        return n;
    }

    public List<EmployeePayBean> loadEmployeePayBeanByParentIdFromDateToDate(Long pBusClientId, int pRunMonth, int pRunYear, BusinessCertificate bc, @Nullable Long pMdaId, boolean pMdaOnly) {
        ArrayList<Object[]> wRetVal;
        List wRetList = new ArrayList();


        String hqlQuery = "select p.id, mda.mdaType.mdaTypeCode, p.rent,p.transport,p.inducement,p.hazard,p.callDuty,p.otherAllowance," +
                "p.taxesPaid,p.unionDues,p.nhf,p.totalGarnishments,p.totalPay,p.netPay,e.id, " +
                " e.firstName, e.lastName, e.initials, s.id,e.rank,e.employeeId," +
                "p.mdaDeptMap.id,mda.id,mda.name,mda.codeName," +
                "p.tws,p.principalAllowance, p.meal,p.utility,p.ruralPosting," +
                "p.journal,p.domesticServant,p.driversAllowance,p.adminAllowance,p.entertainment," +
                "p.academicAllowance,p.tss,p.arrears,p.otherArrears,p.salaryDifference," +
                "p.specialAllowance,p.contractAllowance,p.totalDeductions,p.furniture,p.developmentLevy, " +
                "p.contributoryPension,p.noOfDays,p.payByDaysInd,p.schoolInfo.id,p.accountNumber,b.bankInfo.name," +
                "b.branchSortCode,p.totalAllowance,p.employeeType.id,p.monthlyBasic,p.unionDues,s.level,s.step,p.monthlyPension from "+ IppmsUtils.getPaycheckTableName(bc) +" p, "+IppmsUtils.getEmployeeTableName(bc)+" e," +
                "BankBranch b, MdaInfo mda, MdaDeptMap m , SalaryInfo s where e.id = p.employee.id and p.runMonth = :pRunMonth " +
                "and p.runYear = :pRunYear and b.id = p.bankBranch.id and p.mdaDeptMap.id = m.id and m.mdaInfo.id = mda.id " +
                "and p.businessClientId = :pBusClientIdVar and p.netPay > 0 and p.salaryInfo.id = s.id";
        if(pMdaOnly)
            hqlQuery += " and p.schoolInfo is null ";
        if(IppmsUtils.isNotNullAndGreaterThanZero(pMdaId))
            hqlQuery += " and mda.id = "+pMdaId;

        hqlQuery += " order by e.lastName,e.firstName,e.initials";

        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("pRunMonth", pRunMonth);
        query.setParameter("pRunYear", pRunYear);
        query.setParameter("pBusClientIdVar", pBusClientId);

        wRetVal = (ArrayList) query.list();

        if (wRetVal.size() > 0) {
            int i;
            EmployeePayBean p;
            Employee e;
            SalaryInfo s;
            Object wObj;
            for (Object[] o : wRetVal) {
                i = 0;
                p = new EmployeePayBean();
                p.setId((Long) o[i++]);
                p.setObjectInd((int)o[i++]);
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
                e = new Employee((Long) o[i++], (String) o[i++], (String) o[i++], o[i++]);
                s = new SalaryInfo((Long) o[i++]);
                e.setSalaryInfo(s);
                e.setRank((Rank) o[i++]);
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
                wObj = o[i++];
                if (wObj != null)
                    e.setSchoolInfo(new SchoolInfo((Long) wObj));
                else {
                    e.setSchoolInfo(new SchoolInfo());
                }
                p.setAccountNumber((String)  o[i++]);
                p.setBranchName((String) o[i++]);

                p.setBranchSortCode((String) o[i++]);
                p.setTotalAllowance(((Double) o[i++]));
                p.setEmployeeType(new EmployeeType((Long) o[i++]));
                p.setMonthlyBasic((Double) o[i++]);
                p.setUnionDues((Double)o[i++]);
                p.setSalaryInfo(s);
                p.setEmployee(e);
                p.setLevel((Integer)o[i++]);
                p.setStep((Integer)o[i++]);
                p.setMonthlyPension((Double)o[i++]);

                wRetList.add(p);

            }

        }

        return wRetList;

    }

    public List<EmployeePayBean> loadAllBizClientEmployeePayBeanByParentIdFromDateToDate(int pRunMonth, int pRunYear) {
        ArrayList<Object[]> wRetVal = new ArrayList<>();
        List wRetList = new ArrayList();


        String query = "SELECT * FROM (SELECT DISTINCT p.paychecks_inst_id as Id, p.rent as Rent,p.transport as Transport,p.inducement as Inducement,p.hazard as Hazard,p.call_duty as callDuty,p.other_allowance as OtherAllowance," +
                "p.taxes_paid as TaxesPaid,p.union_dues as UnionDues,p.nhf as NHF,p.total_garnishments as totalGarnishments,p.total_pay as totalPay,p.net_pay as netPay, e.employee_inst_id as EID, " +
                " e.first_name as firstName, e.last_name as lastName, e.initials as Initials, p.salary_info_inst_id as SID, e.employee_id as employeeId," +
                "p.mda_dept_map_inst_id as mdaDeptMap, mda.mda_inst_id as mdaId,mda.name as Name,mda.code_name as codeName," +
                "p.tws as TWS,p.principal_allowance as principalAllowance, p.meal as Meal,p.utility as Utility,p.rural_posting as RuralPosting," +
                "p.journal as Journal,p.domestic_servant as domesticServant,p.drivers_allowance as driversAllowance,p.admin_allowance as adminAllowance,p.entertainment as Entertainment," +
                "p.academic_allowance as academicAllowance,p.tss as TSS,p.arrears as Arrears,p.other_arrears as otherArrears,p.salary_difference as salaryDifference," +
                "p.special_allowance as specialAllowance,p.contract_allowance as contractAllowance,p.total_deductions as totalDeductions,p.furniture as Furniture,p.development_levy as developmentLevy, " +
                "p.contributory_pension as contributoryPension,p.no_of_days as noOfDays,p.pay_by_days_ind as payByDaysInd,p.school_inst_id as SchoolInfoId,p.account_number as accountNumber,b.branch_name as branchName," +
                "b.branch_id as branchSortCode,p.total_allowance as totalAllowance,p.employee_type_inst_id as employeeTypeId,p.monthly_basic as monthlyBasic, bc.name as ClientName, bc.business_client_inst_id as ClientId from ippms_paychecks_info p, ippms_employee e," +
                "ippms_bank_branches b, ippms_mda_info mda, ippms_mda_dept_map m, ippms_client bc where e.employee_inst_id = p.employee_inst_id and p.run_month = :pRunMonth " +
                "and p.run_year = :pRunYear and b.branch_inst_id = p.branch_inst_id and p.mda_dept_map_inst_id = m.mda_dept_map_inst_id and m.mda_inst_id = mda.mda_inst_id " +
                "and p.business_client_inst_id = bc.business_client_inst_id and p.net_pay > 0"
                + "UNION "
                + "SELECT DISTINCT p.paychecks_inst_id as Id, p.rent as Rent,p.transport as Transport,p.inducement as Inducement,p.hazard as Hazard,p.call_duty as callDuty,p.other_allowance as OtherAllowance," +
                "p.taxes_paid as TaxesPaid,p.union_dues as UnionDues,p.nhf as NHF,p.total_garnishments as totalGarnishments,p.total_pay as totalPay,p.net_pay as netPay, e.employee_inst_id as EID, " +
                " e.first_name as firstName, e.last_name as lastName, e.initials as Initials, p.salary_info_inst_id as SID, e.employee_id as employeeId," +
                "p.mda_dept_map_inst_id as mdaDeptMap, mda.mda_inst_id as mdaId,mda.name as Name,mda.code_name as codeName," +
                "p.tws as TWS,p.principal_allowance as principalAllowance, p.meal as Meal,p.utility as Utility,p.rural_posting as RuralPosting," +
                "p.journal as Journal,p.domestic_servant as domesticServant,p.drivers_allowance as driversAllowance,p.admin_allowance as adminAllowance,p.entertainment as Entertainment," +
                "p.academic_allowance as academicAllowance,p.tss as TSS,p.arrears as Arrears,p.other_arrears as otherArrears,p.salary_difference as salaryDifference," +
                "p.special_allowance as specialAllowance,p.contract_allowance as contractAllowance,p.total_deductions as totalDeductions,p.furniture as Furniture,p.development_levy as developmentLevy, " +
                "p.contributory_pension as contributoryPension,p.no_of_days as noOfDays,p.pay_by_days_ind as payByDaysInd,p.school_inst_id as SchoolInfoId,p.account_number as accountNumber,b.branch_name as branchName," +
                "b.branch_id as branchSortCode,p.total_allowance as totalAllowance,p.employee_type_inst_id as employeeTypeId,p.monthly_basic as monthlyBasic, bc.name as ClientName, bc.business_client_inst_id as ClientId from ippms_paychecks_lg_info p, ippms_employee e," +
                "ippms_bank_branches b, ippms_mda_info mda, ippms_mda_dept_map m, ippms_client bc where e.employee_inst_id = p.employee_inst_id and p.run_month = :pRunMonth " +
                "and p.run_year = :pRunYear and b.branch_inst_id = p.branch_inst_id and p.mda_dept_map_inst_id = m.mda_dept_map_inst_id and m.mda_inst_id = mda.mda_inst_id " +
                "and p.business_client_inst_id = bc.business_client_inst_id and p.net_pay > 0"
                + " UNION "
                + "SELECT DISTINCT p.paychecks_inst_id as Id, p.rent as Rent,p.transport as Transport,p.inducement as Inducement,p.hazard as Hazard,p.call_duty as callDuty,p.other_allowance as OtherAllowance," +
                "p.taxes_paid as TaxesPaid,p.union_dues as UnionDues,p.nhf as NHF,p.total_garnishments as totalGarnishments,p.total_pay as totalPay,p.net_pay as netPay, e.pensioner_inst_id as EID, " +
                " e.first_name as firstName, e.last_name as lastName, e.initials as Initials, p.salary_info_inst_id as SID, e.employee_id as employeeId," +
                "p.mda_dept_map_inst_id as mdaDeptMap, mda.mda_inst_id as mdaId,mda.name as Name,mda.code_name as codeName," +
                "p.tws as TWS,p.principal_allowance as principalAllowance, p.meal as Meal,p.utility as Utility,p.rural_posting as RuralPosting," +
                "p.journal as Journal,p.domestic_servant as domesticServant,p.drivers_allowance as driversAllowance,p.admin_allowance as adminAllowance,p.entertainment as Entertainment," +
                "p.academic_allowance as academicAllowance,p.tss as TSS,p.arrears as Arrears,p.other_arrears as otherArrears,p.salary_difference as salaryDifference," +
                "p.special_allowance as specialAllowance,p.contract_allowance as contractAllowance,p.total_deductions as totalDeductions,p.furniture as Furniture,p.development_levy as developmentLevy, " +
                "p.contributory_pension as contributoryPension,p.no_of_days as noOfDays,p.pay_by_days_ind as payByDaysInd,p.school_inst_id as SchoolInfoId,p.account_number as accountNumber,b.branch_name as branchName," +
                "b.branch_id as branchSortCode,p.total_allowance as totalAllowance,p.employee_type_inst_id as employeeTypeId,p.monthly_basic as monthlyBasic, bc.name as ClientName, bc.business_client_inst_id as ClientId from ippms_paychecks_blgp_info p, ippms_pensioner e," +
                "ippms_bank_branches b, ippms_mda_info mda, ippms_mda_dept_map m, ippms_client bc where e.pensioner_inst_id = p.pensioner_inst_id and p.run_month = :pRunMonth " +
                "and p.run_year = :pRunYear and b.branch_inst_id = p.branch_inst_id and p.mda_dept_map_inst_id = m.mda_dept_map_inst_id and m.mda_inst_id = mda.mda_inst_id " +
                "and p.business_client_inst_id = bc.business_client_inst_id and p.net_pay > 0"
                +" UNION "
                + "SELECT DISTINCT p.paychecks_inst_id as Id, p.rent as Rent,p.transport as Transport,p.inducement as Inducement,p.hazard as Hazard,p.call_duty as callDuty,p.other_allowance as OtherAllowance," +
                "p.taxes_paid as TaxesPaid,p.union_dues as UnionDues,p.nhf as NHF,p.total_garnishments as totalGarnishments,p.total_pay as totalPay,p.net_pay as netPay, e.employee_inst_id as EID, " +
                " e.first_name as firstName, e.last_name as lastName, e.initials as Initials, p.salary_info_inst_id as SID, e.employee_id as employeeId," +
                "p.mda_dept_map_inst_id as mdaDeptMap, mda.mda_inst_id as mdaId,mda.name as Name,mda.code_name as codeName," +
                "p.tws as TWS,p.principal_allowance as principalAllowance, p.meal as Meal,p.utility as Utility,p.rural_posting as RuralPosting," +
                "p.journal as Journal,p.domestic_servant as domesticServant,p.drivers_allowance as driversAllowance,p.admin_allowance as adminAllowance,p.entertainment as Entertainment," +
                "p.academic_allowance as academicAllowance,p.tss as TSS,p.arrears as Arrears,p.other_arrears as otherArrears,p.salary_difference as salaryDifference," +
                "p.special_allowance as specialAllowance,p.contract_allowance as contractAllowance,p.total_deductions as totalDeductions,p.furniture as Furniture,p.development_levy as developmentLevy, " +
                "p.contributory_pension as contributoryPension,p.no_of_days as noOfDays,p.pay_by_days_ind as payByDaysInd,p.school_inst_id as SchoolInfoId,p.account_number as accountNumber,b.branch_name as branchName," +
                "b.branch_id as branchSortCode,p.total_allowance as totalAllowance,p.employee_type_inst_id as employeeTypeId,p.monthly_basic as monthlyBasic, bc.name as ClientName, bc.business_client_inst_id as ClientId from ippms_paychecks_subeb_info p, ippms_employee e," +
                "ippms_bank_branches b, ippms_mda_info mda, ippms_mda_dept_map m, ippms_client bc where e.employee_inst_id = p.employee_inst_id and p.run_month = :pRunMonth " +
                "and p.run_year = :pRunYear and b.branch_inst_id = p.branch_inst_id and p.mda_dept_map_inst_id = m.mda_dept_map_inst_id and m.mda_inst_id = mda.mda_inst_id " +
                "and p.business_client_inst_id = bc.business_client_inst_id and p.net_pay > 0"
                +" UNION "
                + "SELECT DISTINCT p.paychecks_inst_id as Id, p.rent as Rent,p.transport as Transport,p.inducement as Inducement,p.hazard as Hazard,p.call_duty as callDuty,p.other_allowance as OtherAllowance," +
                "p.taxes_paid as TaxesPaid,p.union_dues as UnionDues,p.nhf as NHF,p.total_garnishments as totalGarnishments,p.total_pay as totalPay,p.net_pay as netPay, e.pensioner_inst_id as EID, " +
                " e.first_name as firstName, e.last_name as lastName, e.initials as Initials, p.salary_info_inst_id as SID, e.employee_id as employeeId," +
                "p.mda_dept_map_inst_id as mdaDeptMap, mda.mda_inst_id as mdaId,mda.name as Name,mda.code_name as codeName," +
                "p.tws as TWS,p.principal_allowance as principalAllowance, p.meal as Meal,p.utility as Utility,p.rural_posting as RuralPosting," +
                "p.journal as Journal,p.domestic_servant as domesticServant,p.drivers_allowance as driversAllowance,p.admin_allowance as adminAllowance,p.entertainment as Entertainment," +
                "p.academic_allowance as academicAllowance,p.tss as TSS,p.arrears as Arrears,p.other_arrears as otherArrears,p.salary_difference as salaryDifference," +
                "p.special_allowance as specialAllowance,p.contract_allowance as contractAllowance,p.total_deductions as totalDeductions,p.furniture as Furniture,p.development_levy as developmentLevy, " +
                "p.contributory_pension as contributoryPension,p.no_of_days as noOfDays,p.pay_by_days_ind as payByDaysInd,p.school_inst_id as SchoolInfoId,p.account_number as accountNumber,b.branch_name as branchName," +
                "b.branch_id as branchSortCode,p.total_allowance as totalAllowance,p.employee_type_inst_id as employeeTypeId,p.monthly_basic as monthlyBasic, bc.name as ClientName, bc.business_client_inst_id as ClientId from ippms_paychecks_pension_info p, ippms_pensioner e," +
                "ippms_bank_branches b, ippms_mda_info mda, ippms_mda_dept_map m, ippms_client bc where e.pensioner_inst_id = p.pensioner_inst_id and p.run_month = :pRunMonth " +
                "and p.run_year = :pRunYear and b.branch_inst_id = p.branch_inst_id and p.mda_dept_map_inst_id = m.mda_dept_map_inst_id and m.mda_inst_id = mda.mda_inst_id " +
                "and p.business_client_inst_id = bc.business_client_inst_id and p.net_pay > 0) AS foo";


        List<Object[]> rows = makeSQLQuery(query)
                .addScalar("Id", StandardBasicTypes.LONG)
                .addScalar("Rent", StandardBasicTypes.DOUBLE)
                .addScalar("Transport", StandardBasicTypes.DOUBLE)
                .addScalar("Inducement", StandardBasicTypes.DOUBLE)
                .addScalar("Hazard", StandardBasicTypes.DOUBLE)
                .addScalar("callDuty", StandardBasicTypes.DOUBLE)
                .addScalar("OtherAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("TaxesPaid", StandardBasicTypes.DOUBLE)
                .addScalar("UnionDues", StandardBasicTypes.DOUBLE)
                .addScalar("NHF", StandardBasicTypes.DOUBLE)
                .addScalar("totalGarnishments", StandardBasicTypes.DOUBLE)
                .addScalar("totalPay", StandardBasicTypes.DOUBLE)
                .addScalar("netPay", StandardBasicTypes.DOUBLE)
                .addScalar("EID", StandardBasicTypes.LONG)
                .addScalar("firstName", StandardBasicTypes.STRING)
                .addScalar("lastName", StandardBasicTypes.STRING)
                .addScalar("Initials", StandardBasicTypes.STRING)
                .addScalar("SID", StandardBasicTypes.LONG)
                .addScalar("employeeId", StandardBasicTypes.STRING)
                .addScalar("mdaDeptMap", StandardBasicTypes.LONG)
                .addScalar("mdaId", StandardBasicTypes.LONG)
                .addScalar("Name", StandardBasicTypes.STRING)
                .addScalar("codeName", StandardBasicTypes.STRING)
                .addScalar("TWS", StandardBasicTypes.DOUBLE)
                .addScalar("principalAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("Meal", StandardBasicTypes.DOUBLE)
                .addScalar("Utility", StandardBasicTypes.DOUBLE)
                .addScalar("RuralPosting", StandardBasicTypes.DOUBLE)
                .addScalar("Journal", StandardBasicTypes.DOUBLE)
                .addScalar("domesticServant", StandardBasicTypes.DOUBLE)
                .addScalar("driversAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("adminAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("Entertainment", StandardBasicTypes.DOUBLE)
                .addScalar("academicAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("TSS", StandardBasicTypes.DOUBLE)
                .addScalar("Arrears", StandardBasicTypes.DOUBLE)
                .addScalar("otherArrears", StandardBasicTypes.DOUBLE)
                .addScalar("salaryDifference", StandardBasicTypes.DOUBLE)
                .addScalar("specialAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("contractAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("totalDeductions", StandardBasicTypes.DOUBLE)
                .addScalar("Furniture", StandardBasicTypes.DOUBLE)
                .addScalar("developmentLevy", StandardBasicTypes.DOUBLE)
                .addScalar("contributoryPension", StandardBasicTypes.DOUBLE)
                .addScalar("noOfDays", StandardBasicTypes.INTEGER)
                .addScalar("payByDaysInd", StandardBasicTypes.INTEGER)
                .addScalar("SchoolInfoId", StandardBasicTypes.LONG)
                .addScalar("accountNumber", StandardBasicTypes.STRING)
                .addScalar("branchName", StandardBasicTypes.STRING)
                .addScalar("branchSortCode", StandardBasicTypes.STRING)
                .addScalar("totalAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("employeeTypeId", StandardBasicTypes.LONG)
                .addScalar("monthlyBasic", StandardBasicTypes.DOUBLE)
                .addScalar("clientName", StandardBasicTypes.STRING)
                .addScalar("clientId", StandardBasicTypes.LONG)
                .setParameter("pRunMonth", pRunMonth)
                .setParameter("pRunYear", pRunYear)
                .list();

        if (rows.size() > 0) {
            for (Object[] o : rows) {
                int i = 0;
                EmployeePayBean p = new EmployeePayBean();
                p.setId((Long) o[i++]);
//                p.setObjectInd((int)o[i++]);
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
                Employee e = new Employee((Long) o[i++], (String) o[i++], (String) o[i++], o[i++]);
                SalaryInfo s = new SalaryInfo((Long) o[i++]);
                e.setSalaryInfo(s);
//                e.setRank((Rank) o[i++]);
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
                if (wObj != null)
                    e.setSchoolInfo(new SchoolInfo((Long) wObj));
                else {
                    e.setSchoolInfo(new SchoolInfo());
                }
                wObj = o[i++];
                if (wObj != null)
                    p.setAccountNumber((String) wObj);
                else {
                    p.setAccountNumber("N/A");
                }
                p.setBranchName((String) o[i++]);

                p.setBranchSortCode((String) o[i++]);
                p.setTotalAllowance(((Double) o[i++]));
                p.setEmployeeType(new EmployeeType((Long) o[i++]));
                p.setMonthlyBasic((Double) o[i++]);
                p.setSalaryInfo(s);
                p.setEmployee(e);
                p.setBusinessClientName((String)o[i++]);
                p.setBusinessClientId((Long)o[i++]);

                wRetList.add(p);

            }

        }

        return wRetList;

    }
    public NativeQuery makeSQLQuery(String query)  {
        return this.genericService.getCurrentSession().createNativeQuery(query);
    }


    public List<EmployeePayBean> loadAllExecEmployeePayBeanByParentIdFromDateToDate(int pRunMonth, int pRunYear) {
        ArrayList<Object[]> wRetVal = new ArrayList<>();
        List wRetList = new ArrayList();

        String query = "SELECT * FROM (SELECT DISTINCT p.paychecks_inst_id as Id, p.rent as Rent,p.transport as Transport,p.inducement as Inducement,p.hazard as Hazard,p.call_duty as callDuty,p.other_allowance as OtherAllowance," +
                "p.taxes_paid as TaxesPaid,p.union_dues as UnionDues,p.nhf as NHF,p.total_garnishments as totalGarnishments,p.total_pay as totalPay,p.net_pay as netPay, e.employee_inst_id as EID, " +
                " e.first_name as firstName, e.last_name as lastName, e.initials as Initials, p.salary_info_inst_id as SID, e.employee_id as employeeId," +
                "p.mda_dept_map_inst_id as mdaDeptMap, mda.mda_inst_id as mdaId,mda.name as Name,mda.code_name as codeName," +
                "p.tws as TWS,p.principal_allowance as principalAllowance, p.meal as Meal,p.utility as Utility,p.rural_posting as RuralPosting," +
                "p.journal as Journal,p.domestic_servant as domesticServant,p.drivers_allowance as driversAllowance,p.admin_allowance as adminAllowance,p.entertainment as Entertainment," +
                "p.academic_allowance as academicAllowance,p.tss as TSS,p.arrears as Arrears,p.other_arrears as otherArrears,p.salary_difference as salaryDifference," +
                "p.special_allowance as specialAllowance,p.contract_allowance as contractAllowance,p.total_deductions as totalDeductions,p.furniture as Furniture,p.development_levy as developmentLevy, " +
                "p.contributory_pension as contributoryPension,p.no_of_days as noOfDays,p.pay_by_days_ind as payByDaysInd,p.school_inst_id as SchoolInfoId,p.account_number as accountNumber,b.branch_name as branchName," +
                "b.branch_id as branchSortCode,p.total_allowance as totalAllowance,p.employee_type_inst_id as employeeTypeId,p.monthly_basic as monthlyBasic, bc.name as ClientName, bc.business_client_inst_id as ClientId from ippms_paychecks_info p, ippms_employee e," +
                "ippms_bank_branches b, ippms_mda_info mda, ippms_mda_dept_map m, ippms_client bc where e.employee_inst_id = p.employee_inst_id and p.run_month = :pRunMonth " +
                "and p.run_year = :pRunYear and b.branch_inst_id = p.branch_inst_id and p.mda_dept_map_inst_id = m.mda_dept_map_inst_id and m.mda_inst_id = mda.mda_inst_id " +
                "and p.business_client_inst_id = bc.business_client_inst_id and p.net_pay > 0"
                + " UNION "
                + "SELECT DISTINCT p.paychecks_inst_id as Id, p.rent as Rent,p.transport as Transport,p.inducement as Inducement,p.hazard as Hazard,p.call_duty as callDuty,p.other_allowance as OtherAllowance," +
                "p.taxes_paid as TaxesPaid,p.union_dues as UnionDues,p.nhf as NHF,p.total_garnishments as totalGarnishments,p.total_pay as totalPay,p.net_pay as netPay, e.employee_inst_id as EID, " +
                " e.first_name as firstName, e.last_name as lastName, e.initials as Initials, p.salary_info_inst_id as SID, e.employee_id as employeeId," +
                "p.mda_dept_map_inst_id as mdaDeptMap, mda.mda_inst_id as mdaId,mda.name as Name,mda.code_name as codeName," +
                "p.tws as TWS,p.principal_allowance as principalAllowance, p.meal as Meal,p.utility as Utility,p.rural_posting as RuralPosting," +
                "p.journal as Journal,p.domestic_servant as domesticServant,p.drivers_allowance as driversAllowance,p.admin_allowance as adminAllowance,p.entertainment as Entertainment," +
                "p.academic_allowance as academicAllowance,p.tss as TSS,p.arrears as Arrears,p.other_arrears as otherArrears,p.salary_difference as salaryDifference," +
                "p.special_allowance as specialAllowance,p.contract_allowance as contractAllowance,p.total_deductions as totalDeductions,p.furniture as Furniture,p.development_levy as developmentLevy, " +
                "p.contributory_pension as contributoryPension,p.no_of_days as noOfDays,p.pay_by_days_ind as payByDaysInd,p.school_inst_id as SchoolInfoId,p.account_number as accountNumber,b.branch_name as branchName," +
                "b.branch_id as branchSortCode,p.total_allowance as totalAllowance,p.employee_type_inst_id as employeeTypeId,p.monthly_basic as monthlyBasic, bc.name as ClientName, bc.business_client_inst_id as ClientId from ippms_paychecks_lg_info p, ippms_employee e," +
                "ippms_bank_branches b, ippms_mda_info mda, ippms_mda_dept_map m, ippms_client bc where e.employee_inst_id = p.employee_inst_id and p.run_month = :pRunMonth " +
                "and p.run_year = :pRunYear and b.branch_inst_id = p.branch_inst_id and p.mda_dept_map_inst_id = m.mda_dept_map_inst_id and m.mda_inst_id = mda.mda_inst_id " +
                "and p.business_client_inst_id = bc.business_client_inst_id and p.net_pay > 0"
                + " UNION "
                + "SELECT DISTINCT p.paychecks_inst_id as Id, p.rent as Rent,p.transport as Transport,p.inducement as Inducement,p.hazard as Hazard,p.call_duty as callDuty,p.other_allowance as OtherAllowance," +
                "p.taxes_paid as TaxesPaid,p.union_dues as UnionDues,p.nhf as NHF,p.total_garnishments as totalGarnishments,p.total_pay as totalPay,p.net_pay as netPay, e.pensioner_inst_id as EID, " +
                " e.first_name as firstName, e.last_name as lastName, e.initials as Initials, p.salary_info_inst_id as SID, e.employee_id as employeeId," +
                "p.mda_dept_map_inst_id as mdaDeptMap, mda.mda_inst_id as mdaId,mda.name as Name,mda.code_name as codeName," +
                "p.tws as TWS,p.principal_allowance as principalAllowance, p.meal as Meal,p.utility as Utility,p.rural_posting as RuralPosting," +
                "p.journal as Journal,p.domestic_servant as domesticServant,p.drivers_allowance as driversAllowance,p.admin_allowance as adminAllowance,p.entertainment as Entertainment," +
                "p.academic_allowance as academicAllowance,p.tss as TSS,p.arrears as Arrears,p.other_arrears as otherArrears,p.salary_difference as salaryDifference," +
                "p.special_allowance as specialAllowance,p.contract_allowance as contractAllowance,p.total_deductions as totalDeductions,p.furniture as Furniture,p.development_levy as developmentLevy, " +
                "p.contributory_pension as contributoryPension,p.no_of_days as noOfDays,p.pay_by_days_ind as payByDaysInd,p.school_inst_id as SchoolInfoId,p.account_number as accountNumber,b.branch_name as branchName," +
                "b.branch_id as branchSortCode,p.total_allowance as totalAllowance,p.employee_type_inst_id as employeeTypeId,p.monthly_basic as monthlyBasic, bc.name as ClientName, bc.business_client_inst_id as ClientId from ippms_paychecks_blgp_info p, ippms_pensioner e," +
                "ippms_bank_branches b, ippms_mda_info mda, ippms_mda_dept_map m, ippms_client bc where e.pensioner_inst_id = p.pensioner_inst_id and p.run_month = :pRunMonth " +
                "and p.run_year = :pRunYear and b.branch_inst_id = p.branch_inst_id and p.mda_dept_map_inst_id = m.mda_dept_map_inst_id and m.mda_inst_id = mda.mda_inst_id " +
                "and p.business_client_inst_id = bc.business_client_inst_id and p.net_pay > 0"
                +" UNION "
                + "SELECT DISTINCT p.paychecks_inst_id as Id, p.rent as Rent,p.transport as Transport,p.inducement as Inducement,p.hazard as Hazard,p.call_duty as callDuty,p.other_allowance as OtherAllowance," +
                "p.taxes_paid as TaxesPaid,p.union_dues as UnionDues,p.nhf as NHF,p.total_garnishments as totalGarnishments,p.total_pay as totalPay,p.net_pay as netPay, e.employee_inst_id as EID, " +
                " e.first_name as firstName, e.last_name as lastName, e.initials as Initials, p.salary_info_inst_id as SID, e.employee_id as employeeId," +
                "p.mda_dept_map_inst_id as mdaDeptMap, mda.mda_inst_id as mdaId,mda.name as Name,mda.code_name as codeName," +
                "p.tws as TWS,p.principal_allowance as principalAllowance, p.meal as Meal,p.utility as Utility,p.rural_posting as RuralPosting," +
                "p.journal as Journal,p.domestic_servant as domesticServant,p.drivers_allowance as driversAllowance,p.admin_allowance as adminAllowance,p.entertainment as Entertainment," +
                "p.academic_allowance as academicAllowance,p.tss as TSS,p.arrears as Arrears,p.other_arrears as otherArrears,p.salary_difference as salaryDifference," +
                "p.special_allowance as specialAllowance,p.contract_allowance as contractAllowance,p.total_deductions as totalDeductions,p.furniture as Furniture,p.development_levy as developmentLevy, " +
                "p.contributory_pension as contributoryPension,p.no_of_days as noOfDays,p.pay_by_days_ind as payByDaysInd,p.school_inst_id as SchoolInfoId,p.account_number as accountNumber,b.branch_name as branchName," +
                "b.branch_id as branchSortCode,p.total_allowance as totalAllowance,p.employee_type_inst_id as employeeTypeId,p.monthly_basic as monthlyBasic, bc.name as ClientName, bc.business_client_inst_id as ClientId from ippms_paychecks_subeb_info p, ippms_employee e," +
                "ippms_bank_branches b, ippms_mda_info mda, ippms_mda_dept_map m, ippms_client bc where e.employee_inst_id = p.employee_inst_id and p.run_month = :pRunMonth " +
                "and p.run_year = :pRunYear and b.branch_inst_id = p.branch_inst_id and p.mda_dept_map_inst_id = m.mda_dept_map_inst_id and m.mda_inst_id = mda.mda_inst_id " +
                "and p.business_client_inst_id = bc.business_client_inst_id and p.net_pay > 0"
                +" UNION "
                + "SELECT DISTINCT p.paychecks_inst_id as Id, p.rent as Rent,p.transport as Transport,p.inducement as Inducement,p.hazard as Hazard,p.call_duty as callDuty,p.other_allowance as OtherAllowance," +
                "p.taxes_paid as TaxesPaid,p.union_dues as UnionDues,p.nhf as NHF,p.total_garnishments as totalGarnishments,p.total_pay as totalPay,p.net_pay as netPay, e.pensioner_inst_id as EID, " +
                " e.first_name as firstName, e.last_name as lastName, e.initials as Initials, p.salary_info_inst_id as SID, e.employee_id as employeeId," +
                "p.mda_dept_map_inst_id as mdaDeptMap, mda.mda_inst_id as mdaId,mda.name as Name,mda.code_name as codeName," +
                "p.tws as TWS,p.principal_allowance as principalAllowance, p.meal as Meal,p.utility as Utility,p.rural_posting as RuralPosting," +
                "p.journal as Journal,p.domestic_servant as domesticServant,p.drivers_allowance as driversAllowance,p.admin_allowance as adminAllowance,p.entertainment as Entertainment," +
                "p.academic_allowance as academicAllowance,p.tss as TSS,p.arrears as Arrears,p.other_arrears as otherArrears,p.salary_difference as salaryDifference," +
                "p.special_allowance as specialAllowance,p.contract_allowance as contractAllowance,p.total_deductions as totalDeductions,p.furniture as Furniture,p.development_levy as developmentLevy, " +
                "p.contributory_pension as contributoryPension,p.no_of_days as noOfDays,p.pay_by_days_ind as payByDaysInd,p.school_inst_id as SchoolInfoId,p.account_number as accountNumber,b.branch_name as branchName," +
                "b.branch_id as branchSortCode,p.total_allowance as totalAllowance,p.employee_type_inst_id as employeeTypeId,p.monthly_basic as monthlyBasic, bc.name as ClientName, bc.business_client_inst_id as ClientId from ippms_paychecks_pension_info p, ippms_pensioner e," +
                "ippms_bank_branches b, ippms_mda_info mda, ippms_mda_dept_map m, ippms_client bc where e.pensioner_inst_id = p.pensioner_inst_id and p.run_month = :pRunMonth " +
                "and p.run_year = :pRunYear and b.branch_inst_id = p.branch_inst_id and p.mda_dept_map_inst_id = m.mda_dept_map_inst_id and m.mda_inst_id = mda.mda_inst_id " +
                "and p.business_client_inst_id = bc.business_client_inst_id and p.net_pay > 0) AS foo";


        List<Object[]> rows = makeSQLQuery(query)
                .addScalar("Id", StandardBasicTypes.LONG)
                .addScalar("Rent", StandardBasicTypes.DOUBLE)
                .addScalar("Transport", StandardBasicTypes.DOUBLE)
                .addScalar("Inducement", StandardBasicTypes.DOUBLE)
                .addScalar("Hazard", StandardBasicTypes.DOUBLE)
                .addScalar("callDuty", StandardBasicTypes.DOUBLE)
                .addScalar("OtherAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("TaxesPaid", StandardBasicTypes.DOUBLE)
                .addScalar("UnionDues", StandardBasicTypes.DOUBLE)
                .addScalar("NHF", StandardBasicTypes.DOUBLE)
                .addScalar("totalGarnishments", StandardBasicTypes.DOUBLE)
                .addScalar("totalPay", StandardBasicTypes.DOUBLE)
                .addScalar("netPay", StandardBasicTypes.DOUBLE)
                .addScalar("EID", StandardBasicTypes.LONG)
                .addScalar("firstName", StandardBasicTypes.STRING)
                .addScalar("lastName", StandardBasicTypes.STRING)
                .addScalar("Initials", StandardBasicTypes.STRING)
                .addScalar("SID", StandardBasicTypes.LONG)
                .addScalar("employeeId", StandardBasicTypes.STRING)
                .addScalar("mdaDeptMap", StandardBasicTypes.LONG)
                .addScalar("mdaId", StandardBasicTypes.LONG)
                .addScalar("Name", StandardBasicTypes.STRING)
                .addScalar("codeName", StandardBasicTypes.STRING)
                .addScalar("TWS", StandardBasicTypes.DOUBLE)
                .addScalar("principalAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("Meal", StandardBasicTypes.DOUBLE)
                .addScalar("Utility", StandardBasicTypes.DOUBLE)
                .addScalar("RuralPosting", StandardBasicTypes.DOUBLE)
                .addScalar("Journal", StandardBasicTypes.DOUBLE)
                .addScalar("domesticServant", StandardBasicTypes.DOUBLE)
                .addScalar("driversAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("adminAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("Entertainment", StandardBasicTypes.DOUBLE)
                .addScalar("academicAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("TSS", StandardBasicTypes.DOUBLE)
                .addScalar("Arrears", StandardBasicTypes.DOUBLE)
                .addScalar("otherArrears", StandardBasicTypes.DOUBLE)
                .addScalar("salaryDifference", StandardBasicTypes.DOUBLE)
                .addScalar("specialAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("contractAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("totalDeductions", StandardBasicTypes.DOUBLE)
                .addScalar("Furniture", StandardBasicTypes.DOUBLE)
                .addScalar("developmentLevy", StandardBasicTypes.DOUBLE)
                .addScalar("contributoryPension", StandardBasicTypes.DOUBLE)
                .addScalar("noOfDays", StandardBasicTypes.INTEGER)
                .addScalar("payByDaysInd", StandardBasicTypes.INTEGER)
                .addScalar("SchoolInfoId", StandardBasicTypes.LONG)
                .addScalar("accountNumber", StandardBasicTypes.STRING)
                .addScalar("branchName", StandardBasicTypes.STRING)
                .addScalar("branchSortCode", StandardBasicTypes.STRING)
                .addScalar("totalAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("employeeTypeId", StandardBasicTypes.LONG)
                .addScalar("monthlyBasic", StandardBasicTypes.DOUBLE)
                .addScalar("clientName", StandardBasicTypes.STRING)
                .addScalar("clientId", StandardBasicTypes.LONG)
                .setParameter("pRunMonth", pRunMonth)
                .setParameter("pRunYear", pRunYear)
                .list();


        if (rows.size() > 0) {
            for (Object[] o : rows) {
                int i = 0;
                EmployeePayBean p = new EmployeePayBean();
                p.setId((Long) o[i++]);
//                p.setObjectInd((int)o[i++]);
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
                Employee e = new Employee((Long) o[i++], (String) o[i++], (String) o[i++], o[i++]);
                SalaryInfo s = new SalaryInfo((Long) o[i++]);
                e.setSalaryInfo(s);
//                e.setRank((Rank) o[i++]);
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
                if (wObj != null)
                    e.setSchoolInfo(new SchoolInfo((Long) wObj));
                else {
                    e.setSchoolInfo(new SchoolInfo());
                }
                wObj = o[i++];
                if (wObj != null)
                    p.setAccountNumber((String) wObj);
                else {
                    p.setAccountNumber("N/A");
                }
                p.setBranchName((String) o[i++]);

                p.setBranchSortCode((String) o[i++]);
                p.setTotalAllowance(((Double) o[i++]));
                p.setEmployeeType(new EmployeeType((Long) o[i++]));
                p.setMonthlyBasic((Double) o[i++]);
                p.setSalaryInfo(s);
                p.setEmployee(e);
                p.setBusinessClientName((String)o[i++]);
                p.setBusinessClientId((Long)o[i++]);


                wRetList.add(p);

            }

        }

        return wRetList;

    }

    public List<EmployeePayBean> loadOneExecEmployeePayBeanByParentIdFromDateToDate(int pRunMonth, int pRunYear, Long bId) {
        ArrayList<Object[]> wRetVal = new ArrayList<>();
        List wRetList = new ArrayList();
        String paycheck_table = null;
        String employee_table = null;
        String id = null;

        if(bId.equals(1000L)){
            paycheck_table = "ippms_paychecks_info";
            employee_table = "ippms_employee";
            id = "employee_inst_id";
        }else if(bId.equals(1003L)){
            paycheck_table = "ippms_paychecks_lg_info";
            employee_table = "ippms_employee";
            id = "employee_inst_id";
        }else if(bId.equals(1002L)){
            paycheck_table = "ippms_paychecks_blgp_info";
            employee_table = "ippms_pensioner";
            id = "pensioner_inst_id";
        }else if(bId.equals(1004L)){
            paycheck_table = "ippms_paychecks_subeb_info" ;
            employee_table = "ippms_employee";
            id = "employee_inst_id";
        }
        else if(bId.equals(1001L)){
            paycheck_table = "ippms_paychecks_pension_info";
            employee_table = "ippms_pensioner";
            id = "pensioner_inst_id";
        }

        String query = "SELECT DISTINCT p.paychecks_inst_id as Id, p.rent as Rent,p.transport as Transport,p.inducement as Inducement,p.hazard as Hazard,p.call_duty as callDuty,p.other_allowance as OtherAllowance," +
                "p.taxes_paid as TaxesPaid,p.union_dues as UnionDues,p.nhf as NHF,p.total_garnishments as totalGarnishments,p.total_pay as totalPay,p.net_pay as netPay, e.employee_inst_id as EID, " +
                " e.first_name as firstName, e.last_name as lastName, e.initials as Initials, p.salary_info_inst_id as SID, e.employee_id as employeeId," +
                "p.mda_dept_map_inst_id as mdaDeptMap, mda.mda_inst_id as mdaId,mda.name as Name,mda.code_name as codeName," +
                "p.tws as TWS,p.principal_allowance as principalAllowance, p.meal as Meal,p.utility as Utility,p.rural_posting as RuralPosting," +
                "p.journal as Journal,p.domestic_servant as domesticServant,p.drivers_allowance as driversAllowance,p.admin_allowance as adminAllowance,p.entertainment as Entertainment," +
                "p.academic_allowance as academicAllowance,p.tss as TSS,p.arrears as Arrears,p.other_arrears as otherArrears,p.salary_difference as salaryDifference," +
                "p.special_allowance as specialAllowance,p.contract_allowance as contractAllowance,p.total_deductions as totalDeductions,p.furniture as Furniture,p.development_levy as developmentLevy, " +
                "p.contributory_pension as contributoryPension,p.no_of_days as noOfDays,p.pay_by_days_ind as payByDaysInd,p.school_inst_id as SchoolInfoId,p.account_number as accountNumber,b.branch_name as branchName," +
                "b.branch_id as branchSortCode,p.total_allowance as totalAllowance,p.employee_type_inst_id as employeeTypeId,p.monthly_basic as monthlyBasic, bc.name as ClientName, bc.business_client_inst_id as ClientId from "+paycheck_table+" p, "+employee_table+" e," +
                "ippms_bank_branches b, ippms_mda_info mda, ippms_mda_dept_map m, ippms_client bc where e."+id+" = p."+id+" and p.run_month = :pRunMonth and bc.business_client_inst_id = :businessId " +
                "and p.run_year = :pRunYear and b.branch_inst_id = p.branch_inst_id and p.mda_dept_map_inst_id = m.mda_dept_map_inst_id and m.mda_inst_id = mda.mda_inst_id " +
                "and p.business_client_inst_id = bc.business_client_inst_id and p.net_pay > 0";


        List<Object[]> rows = makeSQLQuery(query)
                .addScalar("Id", StandardBasicTypes.LONG)
                .addScalar("Rent", StandardBasicTypes.DOUBLE)
                .addScalar("Transport", StandardBasicTypes.DOUBLE)
                .addScalar("Inducement", StandardBasicTypes.DOUBLE)
                .addScalar("Hazard", StandardBasicTypes.DOUBLE)
                .addScalar("callDuty", StandardBasicTypes.DOUBLE)
                .addScalar("OtherAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("TaxesPaid", StandardBasicTypes.DOUBLE)
                .addScalar("UnionDues", StandardBasicTypes.DOUBLE)
                .addScalar("NHF", StandardBasicTypes.DOUBLE)
                .addScalar("totalGarnishments", StandardBasicTypes.DOUBLE)
                .addScalar("totalPay", StandardBasicTypes.DOUBLE)
                .addScalar("netPay", StandardBasicTypes.DOUBLE)
                .addScalar("EID", StandardBasicTypes.LONG)
                .addScalar("firstName", StandardBasicTypes.STRING)
                .addScalar("lastName", StandardBasicTypes.STRING)
                .addScalar("Initials", StandardBasicTypes.STRING)
                .addScalar("SID", StandardBasicTypes.LONG)
                .addScalar("employeeId", StandardBasicTypes.STRING)
                .addScalar("mdaDeptMap", StandardBasicTypes.LONG)
                .addScalar("mdaId", StandardBasicTypes.LONG)
                .addScalar("Name", StandardBasicTypes.STRING)
                .addScalar("codeName", StandardBasicTypes.STRING)
                .addScalar("TWS", StandardBasicTypes.DOUBLE)
                .addScalar("principalAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("Meal", StandardBasicTypes.DOUBLE)
                .addScalar("Utility", StandardBasicTypes.DOUBLE)
                .addScalar("RuralPosting", StandardBasicTypes.DOUBLE)
                .addScalar("Journal", StandardBasicTypes.DOUBLE)
                .addScalar("domesticServant", StandardBasicTypes.DOUBLE)
                .addScalar("driversAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("adminAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("Entertainment", StandardBasicTypes.DOUBLE)
                .addScalar("academicAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("TSS", StandardBasicTypes.DOUBLE)
                .addScalar("Arrears", StandardBasicTypes.DOUBLE)
                .addScalar("otherArrears", StandardBasicTypes.DOUBLE)
                .addScalar("salaryDifference", StandardBasicTypes.DOUBLE)
                .addScalar("specialAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("contractAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("totalDeductions", StandardBasicTypes.DOUBLE)
                .addScalar("Furniture", StandardBasicTypes.DOUBLE)
                .addScalar("developmentLevy", StandardBasicTypes.DOUBLE)
                .addScalar("contributoryPension", StandardBasicTypes.DOUBLE)
                .addScalar("noOfDays", StandardBasicTypes.INTEGER)
                .addScalar("payByDaysInd", StandardBasicTypes.INTEGER)
                .addScalar("SchoolInfoId", StandardBasicTypes.LONG)
                .addScalar("accountNumber", StandardBasicTypes.STRING)
                .addScalar("branchName", StandardBasicTypes.STRING)
                .addScalar("branchSortCode", StandardBasicTypes.STRING)
                .addScalar("totalAllowance", StandardBasicTypes.DOUBLE)
                .addScalar("employeeTypeId", StandardBasicTypes.LONG)
                .addScalar("monthlyBasic", StandardBasicTypes.DOUBLE)
                .addScalar("clientName", StandardBasicTypes.STRING)
                .addScalar("clientId", StandardBasicTypes.LONG)
                .setParameter("pRunMonth", pRunMonth)
                .setParameter("pRunYear", pRunYear)
                .setParameter("businessId", bId)
                .list();


        if (rows.size() > 0) {
            for (Object[] o : rows) {
                int i = 0;
                EmployeePayBean p = new EmployeePayBean();
                p.setId((Long) o[i++]);
//                p.setObjectInd((int)o[i++]);
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
                Employee e = new Employee((Long) o[i++], (String) o[i++], (String) o[i++], o[i++]);
                SalaryInfo s = new SalaryInfo((Long) o[i++]);
                e.setSalaryInfo(s);
//                e.setRank((Rank) o[i++]);
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
                if (wObj != null)
                    e.setSchoolInfo(new SchoolInfo((Long) wObj));
                else {
                    e.setSchoolInfo(new SchoolInfo());
                }
                wObj = o[i++];
                if (wObj != null)
                    p.setAccountNumber((String) wObj);
                else {
                    p.setAccountNumber("N/A");
                }
                p.setBranchName((String) o[i++]);

                p.setBranchSortCode((String) o[i++]);
                p.setTotalAllowance(((Double) o[i++]));
                p.setEmployeeType(new EmployeeType((Long) o[i++]));
                p.setMonthlyBasic((Double) o[i++]);
                p.setSalaryInfo(s);
                p.setEmployee(e);
                p.setBusinessClientName((String)o[i++]);
                p.setBusinessClientId((Long)o[i++]);


                wRetList.add(p);

            }

        }

        return wRetList;

    }

    public NamedEntityBean loadEmployeePayBeanByParentIdFromDateToDateAndFilter(BusinessCertificate bc, int pRunMonth, int pRunYear, NamedEntityBean pNEB){


        List<EmployeePayBean> wRetList = new ArrayList<>();


        ArrayList<Object[]> wRetVal;


        String hqlQuery = "";


        hqlQuery = "select e.id,p.id, p.rent,p.transport,p.inducement,p.hazard,p.callDuty,p.otherAllowance,p.taxesPaid,p.unionDues," +
                "p.nhf,p.totalGarnishments,p.totalPay,p.netPay,p.status,p.payDate,p.payPeriodStart,p.payPeriodEnd,p.salaryInfo.id, " +
                "e.firstName, e.lastName, e.initials, e.rank,e.employeeId,e.address1,e.city.id, e.city.name,e.city.state.id,e.zipCode,t.id,t.name, m.name," +
                "p.contributoryPension,e.schoolInfo.id,p.totalAllowance,p.accountNumber,b.name " +
                " from "+IppmsUtils.getPaycheckTableName(bc)+" p, "+IppmsUtils.getEmployeeTableName(bc)+" e,MdaDeptMap a, MdaInfo m, Title t,BankBranch b, SalaryInfo s, SalaryType st " +
                "where e.title.id = t.id and e.id = p.employee.id and e.mdaDeptMap.id = a.id and b.id = p.bankBranch.id "+
                "and s.salaryType.id = st.id and s.id = p.salaryInfo.id " +
                "and a.mdaInfo.id = m.id and p.runMonth = :pRunMonth and p.runYear = :pRunYear ";

        if(pNEB.isSalaryType()) {
            hqlQuery += "and st.id = :pObjectIdVar ";
            if(pNEB.getFromLevel() > 0 && pNEB.getToLevel() > 0) {
                hqlQuery += "and s.level >= :pFromLevelVar and s.level <= :pToLevelVar ";
            }
            else if(pNEB.getFromLevel() > 0 && pNEB.getToLevel() <= 0) {
                hqlQuery += "and s.level >= :pFromLevelVar ";
            }
        }else if(pNEB.isMdaType()) {

            hqlQuery += "and m.id = :pObjectIdVar ";
        }else {
            //Gotta be Employee...
            hqlQuery += "and e.id = :pObjectIdVar ";
        }
        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);

        query.setParameter("pRunMonth", pRunMonth);
        query.setParameter("pRunYear", pRunYear);
        if(pNEB.isSalaryType()) {

            if(pNEB.getFromLevel() > 0  ) {
                query.setParameter("pFromLevelVar", pNEB.getFromLevel());
            }
            if(pNEB.getToLevel() > 0) {
                query.setParameter("pToLevelVar", pNEB.getToLevel());
            }
        }

        query.setParameter("pObjectIdVar", pNEB.getId());



        wRetVal = (ArrayList)query.list();

        if (wRetVal.size() > 0)
        {
            Long empId;
            int i = 0;
            for (Object[] o : wRetVal) {
                empId = (Long)o[i++];
                if(pNEB.getIdList().contains(empId)) {
                    continue; //filter
                    //Logic here is simple
                    //Employees can meet multiple criteria of printing PaySlips e.g exist in Same MDA and PayGroup Combo.
                }else {
                    pNEB.getIdList().add(empId);
                }
                EmployeePayBean p = new EmployeePayBean();
                p.setId((Long)o[i++]);
                p.setRent(((Double)o[i++]));
                p.setTransport(((Double)o[i++]));
                p.setInducement(((Double)o[i++]));
                p.setHazard(((Double)o[i++]));
                p.setCallDuty(((Double)o[i++]));
                p.setOtherAllowance(((Double)o[i++]));
                p.setTaxesPaid(((Double)o[i++]));
                p.setUnionDues(((Double)o[i++]));
                p.setNhf(((Double)o[i++]));
                p.setTotalGarnishments(((Double)o[i++]));
                p.setTotalPay(((Double)o[i++]));
                p.setNetPay(((Double)o[i++]));
                p.setStatus((String)o[i++]);
                p.setPayDate((LocalDate)o[i++]);
                p.setPayPeriodStart((LocalDate)o[i++]);
                p.setPayPeriodEnd((LocalDate)o[i++]);
                SalaryInfo s = new SalaryInfo((Long)o[i++]);
                Employee e = new Employee(empId, (String)o[i++], (String)o[i++],o[i++]);

                e.setSalaryInfo(s);

                e.setRank((Rank)o[i++]);
                e.setEmployeeId((String)o[i++]);
                e.setAddress1((String)o[i++]);
                e.setCity(new City((Long)o[i++],(String)o[i++]));
                e.setStateInstId((Long)o[i++]);
                Object obj = o[i++];
                if (null != obj)
                    e.setZipCode((String)obj);
                Title title = new Title((Long)o[i++], (String)o[i++]);
                e.setTitle(title);
                p.setEmployee(e);
                p.setMda((String)o[i++]);
                p.setContributoryPension(((Double)o[i++]));
                obj = o[i++];
                if (obj != null)
                    e.setSchoolInfo(new SchoolInfo((Long) obj));

                p.setTotalAllowance(((Double)o[i++]));
                p.setAccountNumber((String)o[i++]);
                p.setBranchName((String)o[i++]);


                p.setSalaryInfo(s);
                p.setEmployee(e);

                wRetList.add(p);
                i = 0;
            }

        }
        pNEB.setEmpPayBeanList(wRetList);
        return pNEB;

    }

  public BankPVSummaryBean loadEmployeePayBeanByFromDateToDateAndBank(LocalDate pSomeDate)
    {
        BankPVSummaryBean wRetMap = new BankPVSummaryBean();

        String wSql = "select e.employee.id,e.totalPay,e.totalDeductions,bb.id,bb.name," +
                "b.id, b.name from EmployeePayBean e, BankBranch bb, BankInfo b where " +
                "e.runMonth = :pRunMonth and e.runYear = :pRunYear and " +
                "bb.id = e.bankBranch.id and bb.bankInfo.id = b.id  and e.netPay > 0 order by e.id";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wSql);
        query.setParameter("pRunMonth", pSomeDate.getMonthValue());
        query.setParameter("pRunYear", pSomeDate.getYear());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)query.list();

        if (wRetVal.size() > 0)
        {
            HashMap wBranchMap = new HashMap();
            HashMap wBankInfoMap = new HashMap();

            for (Object[] o : wRetVal)
            {
                double wTotalPay = ((Double)o[1]);
                double wTotalDeduction = ((Double)o[2]);
                double wNetPay = wTotalPay - wTotalDeduction;
                Long wBranchId = (Long)o[3];
                Long wBankId = (Long)o[5];
                String wBranchName = (String)o[4];
                String wBankName = (String)o[6];

                wRetMap.setNetPay(wRetMap.getNetPay() + wNetPay);

                if (wBranchMap.containsKey(wBranchId)) {
                    BankBranch wBB = (BankBranch)wBranchMap.get(wBranchId);
                    wBB.setBankId(wBankId);
                    wBB.setTotalNetPay(wBB.getTotalNetPay() + wNetPay);
                    wBranchMap.put(wBranchId, wBB);
                }
                else {
                    BankBranch wBB = new BankBranch(wBranchId, wBranchName);
                    wBB.setBankId(wBankId);
                    wBB.setTotalNetPay(wBB.getTotalNetPay() + wNetPay);
                    wBranchMap.put(wBranchId, wBB);
                }

                if (wBankInfoMap.containsKey(wBankId)) {
                    BankInfo wBB = (BankInfo)wBankInfoMap.get(wBankId);
                    wBB.setTotalNetPay(wBB.getTotalNetPay() + wNetPay);
                    wBankInfoMap.put(wBankId, wBB);
                }
                else {
                    BankInfo wBB = new BankInfo(wBankId, wBankName);
                    wBB.setTotalNetPay(wBB.getTotalNetPay() + wNetPay);
                    wBankInfoMap.put(wBankId, wBB);
                }

            }

            wRetMap.setBankBranchMap(wBranchMap);
            wRetMap.setBankInfoMap(wBankInfoMap);
        }

        return wRetMap;
    }

    public List<SalaryInfo> loadSalaryInfoBySalaryTypeId(Long pSalTypeId)
    {
        String sql = "select distinct s.level,s.step"
                + " from SalaryInfo s, SalaryType st where s.salaryType.id = st.id and "
                + "st.id = :pSalTypeIdVar and st.deactivatedInd = 0";

        Query query = this.sessionFactory.getCurrentSession().createQuery(sql);

        query.setParameter("pSalTypeIdVar", pSalTypeId);

        ArrayList<Object[]> wRetVal = new ArrayList<>();
        ArrayList<SalaryInfo> wRetList = new ArrayList<>();

        wRetVal = (ArrayList<Object[]>)query.list();

        if ((wRetVal != null) && (wRetVal.size() > 0))
        {
            for (Object[] o : wRetVal) {
                SalaryInfo l = new SalaryInfo();

                l.setLevel((Integer)o[0]);
                l.setStep((Integer)o[1]);

                wRetList.add(l);
            }

        }

        return wRetList;
    }


    public List<NamedEntityBean> loadObjectIdAndNameByClassAndConditions(String pClassObject, int pObjType, String pAddendum, Long pBizId)
    {
        Objects.requireNonNull(pClassObject);

        String wHql = "select distinct id,name from " + pClassObject;

        if(!StringUtils.trimToEmpty(pAddendum).isEmpty()) {
            wHql += " "+pAddendum;
        }


        Query wQuery = this.sessionFactory.getCurrentSession().createQuery((wHql));
        wQuery.setParameter("pBizIdVar", pBizId);

        List<NamedEntityBean> wRetMap = new ArrayList<>();

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0))
        {
            NamedEntityBean n = null;
            for (Object[] o : wRetVal) {
                n = new NamedEntityBean();
                n.setId((Long)o[0]);
                n.setName((String)o[1]);
                n.setPaySlipObjTypeInd(pObjType);
                wRetMap.add(n);
            }
            Comparator<NamedEntityBean> c = Comparator.comparing(NamedEntityBean::getName);
            Collections.sort(wRetMap,c);
        }

        return wRetMap;
    }

    public AbstractPaycheckEntity loadLastNoneZeroPaycheckForEmployee(BusinessCertificate businessCertificate, Long employeeInstId) {
        AbstractPaycheckEntity wRetObj = IppmsUtils.makePaycheckObject(businessCertificate);
        try
        {
//            List<Long> results = sessionFactory.getCurrentSession().createCriteria(EmployeePayBean.class).setProjection(Projections.max("id"))
//                    .add(Restrictions.eq("employee.id", employeeInstId)).add(Restrictions.gt("netPay", Double.valueOf(0.0D))).list();
//
            Long pid = this.getMaxPaycheckIdForEmployee(businessCertificate, employeeInstId);

//            Long maxId =  pEmp.get(0);
            String sql = "select p.netPay,p.inducement,p.nhf,p.monthlyTax,p.id,p.payPeriodStart, p.runYear from "+IppmsUtils.getPaycheckTableName(businessCertificate)+" p where p.id = :pId";
            Query query = sessionFactory.getCurrentSession().createQuery(sql);
            query.setParameter("pId", pid);

            Object[] wRetVal = (Object[]) query.uniqueResult();

            wRetObj.setNetPay(((Double)wRetVal[0]).doubleValue());
            wRetObj.setInducement(((Double)wRetVal[1]).doubleValue());
            wRetObj.setNhf(((Double)wRetVal[2]).doubleValue());
            wRetObj.setMonthlyTax(((Double)wRetVal[3]).doubleValue());
            wRetObj.setId((Long)wRetVal[4]);
            wRetObj.setPayDate((LocalDate) wRetVal[5]);
            wRetObj.setRunYear((int)wRetVal[6]);
        }
        catch (Exception nPEx)
        {
            System.out.println(nPEx.getMessage());
            return wRetObj;
        }
        return wRetObj;
    }


    public List<AbstractPaycheckEntity> loadEmployeePayBeanSummaryByParentIdAndLastPayPeriod(Long pBusClientId, int pRunYear, int pStartRow, int pEndRow, BusinessCertificate bc)
    {
        String wSql = "select sum(p.taxesPaid), sum(p.taxableIncome), sum(p.totalPay), p.employee.id, e.employeeId ,e.firstName, e.lastName,"
                + " e.initials, m.name" +
                " from "+IppmsUtils.getPaycheckTableName(bc)+" p, "+IppmsUtils.getEmployeeTableName(bc)+" e, MdaDeptMap mdm, MdaInfo m" +
                " where p.runYear = :pRunYearVar and e.id = p.employee.id and mdm.id = p.mdaDeptMap.id and m.id = mdm.mdaInfo.id" +
                " group by p.employee.id, e.employeeId, e.firstName, e.lastName, e.initials, m.name " +
                " order by p.employee.id";

        Query query = sessionFactory.getCurrentSession().createQuery(wSql);
        query.setParameter("pRunYearVar", pRunYear);

//        if (pStartRow > 0)
//        { query.setFirstResult(pStartRow);
//            query.setMaxResults(pEndRow);}
//        else{
//            query.setMaxResults(pEndRow);
//        }


        ArrayList<Object[]> wRetVal = (ArrayList)query.list();
        List<AbstractPaycheckEntity> wRetList = new ArrayList<>();


        for (Object[] o : wRetVal) {
            Long wEmpId = (Long)o[3];
            EmployeePayBean p = new EmployeePayBean();
            p.setEmpInstId(wEmpId);
            p.setTaxPaidYTD((Double) o[0]);
            p.setTaxableIncomeYTD((Double) o[1]);
            p.setGrossPayYTD((Double) o[2]);
            p.setEmployeeId((String) o[4]);
            p.setEmployeeName(PayrollHRUtils.createDisplayName((String) o[6], (String) o[5], (String) o[7]));
            //p.setMdaDeptMap(new MdaDeptMap((Long)o[8]));
            p.setMda((String)o[8]);
            wRetList.add(p);
        }
        return wRetList;
    }

    public int countEmployeePayBeanSummaryByParentIdAndLastPayPeriod(Long pBusClientId, int pRunYear, BusinessCertificate bc)
    {
        int wRetVal = 0;
        String wSql = "select count(distinct p.employee.id)" +
                " from "+IppmsUtils.getPaycheckTableName(bc)+" p, "+IppmsUtils.getEmployeeTableName(bc)+" e" +
                " where p.runYear = :pRunYearVar and e.id = p.employee.id";

        Query query = sessionFactory.getCurrentSession().createQuery(wSql);
        query.setParameter("pRunYearVar", pRunYear);

        List results = query.list();
        wRetVal = Integer.parseInt(String.valueOf(results.get(0)));

        return wRetVal;
    }



    public List<MDAPPaySlipSummaryBean> getPayGroupPayslipSummaryByDates(int pRunMonth, int pRunYear, BusinessCertificate bc)
    {
        String hqlQuery = "select count(p.id),sum(p.netPay), st.name,st.id, p.status from " +
                " "+IppmsUtils.getPaycheckTableName(bc)+" p,SalaryInfo s, SalaryType st " +
                "where p.salaryInfo.id = s.id and s.salaryType.id = st.id " +
                "and p.runMonth = :pRunMonth " +
                "and p.runYear = :pRunYear " +
                "group by st.name, st.id, p.status order by st.name asc";


        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);


        query.setParameter("pRunMonth", pRunMonth);
        query.setParameter("pRunYear", pRunYear);

        List<Object[]> results = query.list();
        List<MDAPPaySlipSummaryBean> wRetList = new ArrayList<>();

        if ((results != null) && (results.size() > 0)) {
            for (Object[] o : results) {
                MDAPPaySlipSummaryBean m = new MDAPPaySlipSummaryBean();
                m.setNoOfEmployees(((Long)o[0]).intValue());
                m.setTotalPay(((Double)o[1]).doubleValue());
                m.setName((String)o[2]);
                m.setId((Long)o[3]);
                m.setCheckStatus((String)o[4]);

                wRetList.add(m);
            }
        }
        return wRetList;
    }

    public List<PaycheckGarnishment> loadPaycheckGarnishmentsByPaycheckId(Long pPaycheckId, BusinessCertificate bc)
    {
        List wRetList = new ArrayList();
        String wSql = "select p.amount, e.description from "+IppmsUtils.getPaycheckGarnishmentTableName(bc)+" p, "+IppmsUtils.getGarnishmentInfoTableName(bc)+" e,"
                + " "+IppmsUtils.getPaycheckTableName(bc)+" epb where p.employeePayBean.id = epb.id and p.empGarnInfo.id = e.id and epb.id = :pParentId";

        Query query = sessionFactory.getCurrentSession().createQuery(wSql);

        query.setParameter("pParentId", pPaycheckId);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)query.list();

        if (wRetVal.size() > 0)
        {
            for (Object[] o : wRetVal) {
                PaycheckGarnishment p = new PaycheckGarnishment();

                p.setAmount(((Double)o[0]));
                p.setName((String)o[1]);

                wRetList.add(p);
            }

        }

        return wRetList;
    }

    public List<PaycheckDeduction> loadPaycheckDeductionsByPaycheckId(Long pPaycheckId, BusinessCertificate bc)
    {
        List wRetList = new ArrayList();
        String wSql = "select p.amount, e.description "
                + "from "+IppmsUtils.getPaycheckDeductionTableName(bc)+" p, "+IppmsUtils.getDeductionInfoTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" epb "
                + "where p.employeePayBean.id = epb.id and p.empDedInfo.id = e.id and epb.id = :pParentId";

        Query query = sessionFactory.getCurrentSession().createQuery(wSql);

        query.setLong("pParentId", pPaycheckId);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)query.list();

        if (wRetVal.size() > 0)
        {
            for (Object[] o : wRetVal) {
                PaycheckDeduction p = new PaycheckDeduction();

                p.setAmount(((Double)o[0]));
                p.setName((String)o[1]);

                wRetList.add(p);
            }
        }
        return wRetList;
    }

    public HashMap<Long,HashMap<String,Double>> loadPaycheckDeductionTypeByPaycheckId(int pRunMonth, int pRunYear, BusinessCertificate bc)
    {

        HashMap<Long,HashMap<String,Double>> wRetMap = new HashMap<>();

        List wRetList = new ArrayList();
        String wSql = "select p.employee.id,et.name, p.amount "
                + "from "+IppmsUtils.getPaycheckDeductionTableName(bc)+" p, "+IppmsUtils.getDeductionInfoTableName(bc)+" e, EmpDeductionType et, "+IppmsUtils.getPaycheckTableName(bc)+" epb "
                + "where p.employeePayBean.id = epb.id and p.empDedInfo.id = e.id and e.empDeductionType.id = et.id and p.runMonth = epb.runMonth and p.runYear = epb.runYear" +
                " and p.runMonth = :pRunMonthVar and p.runYear = :pRunYearVar ";

        Query query = sessionFactory.getCurrentSession().createQuery(wSql);

        query.setParameter("pRunMonthVar", pRunMonth);
        query.setParameter("pRunYearVar", pRunYear);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)query.list();
        HashMap<String,Double> innerMap;
        if (wRetVal.size() > 0)
        {
            Long empId;
            String key;
            Double value;
            for (Object[] o : wRetVal) {
                int i = 0;
                empId =  (Long)o[i++];
                key =  (String)o[i++];
                value =  (Double)o[i++];

                if(wRetMap.containsKey(empId)){
                      innerMap = wRetMap.get(empId);
                }else{
                    innerMap = new HashMap<>();
                }
                innerMap.put(key,value);
                wRetMap.put(empId,innerMap);


            }
        }
        return wRetMap;
    }


}
