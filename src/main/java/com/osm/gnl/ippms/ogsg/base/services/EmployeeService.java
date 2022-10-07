/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.beans.EmpContMiniBean;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.ltg.domain.AbmpBean;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.report.beans.EmployeeMiniBean;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionType;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.type.LocalDateType;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils.treatNull;

@Service("employeeService")
@Repository
@Transactional(readOnly = true)
public class EmployeeService {
    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;


    public List<EmployeeMiniBean> findSimilarlyNamedEmps(AbstractEmployeeEntity pEmp, BusinessCertificate businessCertificate) {

        String wHqlStr = "";
        boolean usingInitials = false;
        if(treatNull(pEmp.getInitials()).equalsIgnoreCase(IConstants.EMPTY_STR) || treatNull(pEmp.getInitials()).length() < 3){
            wHqlStr = "select distinct e.id,e.employeeId,e.lastName,e.firstName,e.initials,e.gsmNumber,bc.name, bc.id from "+ IppmsUtils.getEmployeeTableName(businessCertificate)+ " e, BusinessClient bc " +
                    " where (upper(e.lastName) like :pLastNameVar OR upper(e.lastName) like :pFirstNameVar" +
                    " OR upper(e.firstName) like :pFirstNameVar OR upper(e.firstName) like :pLastNameVar )";

        }else{
            usingInitials = true;

            wHqlStr = "select distinct e.id,e.employeeId,e.lastName,e.firstName,e.initials,e.gsmNumber,bc.name,bc.id from "+ IppmsUtils.getEmployeeTableName(businessCertificate)+ " e , BusinessClient bc " +
                    " where (upper(e.lastName) like :pFirstNameVar OR upper(e.firstName) like :pLastNameVar OR upper(e.initials) like :pInitialsVar" +
                    " OR upper(e.lastName) like :pInitialsVar OR upper(e.firstName) like :pFirstNameVar OR upper(e.initials) like :pLastNameVar"+
                    " OR upper(e.lastName) like :pLastNameVar OR upper(e.firstName) like :pFirstNameVar OR upper(e.initials) like :pInitialsVar"+
                    " OR upper(e.lastName) like :pFirstNameVar OR upper(e.firstName) like :pInitialsVar OR upper(e.initials) like :pLastNameVar"+
                    " OR upper(e.lastName) like :pInitialsVar OR upper(e.firstName) like :pLastNameVar OR upper(e.initials) like :pFirstNameVar"+
                    " OR upper(e.lastName) like :pLastNameVar OR upper(e.firstName) like :pInitialsVar OR upper(e.initials) like :pFirstNameVar ) ";
        }
        wHqlStr += " AND e.businessClientId = :pBizIdVar ";
        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHqlStr);

        wQuery.setParameter("pLastNameVar", "%"+pEmp.getLastName().toUpperCase()+"%");
        wQuery.setParameter("pFirstNameVar", "%"+pEmp.getFirstName().toUpperCase()+"%");

        if(usingInitials){

            wQuery.setParameter("pInitialsVar", "%"+pEmp.getInitials().toUpperCase()+"%");

        }
        wQuery.setParameter("pBizIdVar", businessCertificate.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        List<EmployeeMiniBean> wRetMap = new ArrayList<EmployeeMiniBean>();
        if ((wRetVal != null) && (wRetVal.size() > 0))
        {
            for (Object[] o : wRetVal) {
                EmployeeMiniBean n = new EmployeeMiniBean();
                n.setId((Long)o[0]);
                n.setEmployeeId((String)o[1]);
                n.setLastName((String)o[2]);
                n.setFirstName((String)o[3]);
                Object wOb = o[4];
                if(wOb != null)
                    n.setInitials((String)wOb);
                wOb = o[5];
                if(wOb != null)
                    n.setAccountNumber((String)wOb);
                n.setName((String)o[6]);
                n.setPaycheckId((Long)o[7]);
                wRetMap.add(n);
            }
        }

        return wRetMap;

    }

    public HashMap<Long, Double> loadDeductionsForActiveEmployees(LocalDate pStartDate, BusinessCertificate bc)
    {
        HashMap wRetList = new HashMap();

        String hqlQuery = "select p.employee.id,sum(p.amount) from "+IppmsUtils.getPaycheckDeductionTableName(bc)+" p, "+IppmsUtils.getPaycheckTableName(bc)+" e "
                + "where p.employeePayBean.id = e.id and p.employee.id = e.employee.id and e.netPay > 0 "
                + "and p.runMonth = :pRunMonth and p.runYear = :pRunYear group by p.employee.id";

        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("pRunMonth", pStartDate.getMonthValue());
        query.setParameter("pRunYear", pStartDate.getYear());

        ArrayList<Object[]>  wRetVal = (ArrayList<Object[]> )query.list();

        if (wRetVal.size() > 0)
         for (Object[] o : wRetVal)
                 wRetList.put(o[0], o[1]);

        return wRetList;
    }


    public NativeQuery makeSQLQuery(String query)  {
        return this.genericService.getCurrentSession().createNativeQuery(query);
    }

    public HashMap<Long, Double> loadAllBizClientDeductionsForActiveEmployees(LocalDate pStartDate)
    {
        HashMap wRetList = new HashMap();

        ArrayList<Object[]> wRetVal = new ArrayList<Object[]>();

        String query = "SELECT * FROM (select DISTINCT p.employee_inst_id as Id,sum(p.amount) as Amount from ippms_paycheck_deductions p, ippms_paychecks_info e "
                + "where p.paycheck_inst_id = e.paychecks_inst_id and p.employee_inst_id = e.employee_inst_id and e.net_pay > 0 "
                + "and p.run_month = :pRunMonth and p.run_year = :pRunYear group by p.employee_inst_id "+
                " UNION "
                +"select DISTINCT p.employee_inst_id as Id,sum(p.amount) as Amount from ippms_paycheck_deductions_lg p, ippms_paychecks_lg_info e "
                +"where p.paycheck_inst_id = e.paychecks_inst_id and p.employee_inst_id = e.employee_inst_id and e.net_pay > 0 "
                +"and p.run_month = :pRunMonth and p.run_year = :pRunYear group by p.employee_inst_id"
                +" UNION "
                +"select DISTINCT p.employee_inst_id as Id,sum(p.amount) as Amount from ippms_paycheck_deductions_blgp p, ippms_paychecks_blgp_info e "
                +"where p.paycheck_inst_id = e.paychecks_inst_id and p.employee_inst_id = e.pensioner_inst_id and e.net_pay > 0 "
                +"and p.run_month = :pRunMonth and p.run_year = :pRunYear group by p.employee_inst_id"
                +" UNION "
                +"select DISTINCT p.employee_inst_id as Id,sum(p.amount) as Amount from ippms_paycheck_deductions_subeb p, ippms_paychecks_subeb_info e "
                +"where p.paycheck_inst_id = e.paychecks_inst_id and p.employee_inst_id = e.employee_inst_id and e.net_pay > 0 "
                +"and p.run_month = :pRunMonth and p.run_year = :pRunYear group by p.employee_inst_id"
                +" UNION "
                +"select DISTINCT p.employee_inst_id as Id,sum(p.amount) as Amount from ippms_paycheck_deductions_pension p, ippms_paychecks_pension_info e "
                +"where p.paycheck_inst_id = e.paychecks_inst_id and p.employee_inst_id = e.pensioner_inst_id and e.net_pay > 0 "
                +"and p.run_month = :pRunMonth and p.run_year = :pRunYear group by p.employee_inst_id) AS foo";


        List<Object[]> rows = makeSQLQuery(query)
                .addScalar("Id", StandardBasicTypes.LONG)
                .addScalar("Amount", StandardBasicTypes.DOUBLE)
                .setParameter("pRunMonth",pStartDate.getMonthValue())
                .setParameter("pRunYear", pStartDate.getYear())
                .list();

        if (rows.size() > 0)
        {
            for (Object[] o : rows) {
                SuspensionLog e = new SuspensionLog();
                e.setId((Long)o[0]);
                e.setPayPercentage(((Double)o[1]));
                wRetList.put(e.getId(), Double.valueOf(e.getPayPercentage()));
            }

        }

        return wRetList;
    }

    public List<EmployeePayBean> getPayrollRunYears(BusinessCertificate bc)
    {
        String wHql = "select distinct(p.runYear) from "+IppmsUtils.getPaycheckTableName(bc)+" p where p.status = 'A'";
        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        ArrayList<Integer> wRetList = (ArrayList<Integer>)wQuery.list();
        List<EmployeePayBean> wRetVal = new ArrayList<EmployeePayBean>();
        int count;
        if (wRetList.size() > 0) {
            count = 0;
            for (Integer o : wRetList) {
                EmployeePayBean e = new EmployeePayBean();
                e.setRunYear(o.intValue());
                count++; e.setId(new Long(count));
                wRetVal.add(e);
            }
        }
        return wRetVal;
    }

    public List<Integer> getAllRunYears(BusinessCertificate bc) {
        List<Integer> thisLIst = new ArrayList<>();
        String sql = "select distinct(p.runYear) from "+IppmsUtils.getPaycheckTableName(bc)+" p order by p.runYear";
        Query wQ = this.sessionFactory.getCurrentSession().createQuery(sql);
        if (IppmsUtils.isNotNullOrEmpty((ArrayList<Integer>) wQ.list())) {
            thisLIst =  (ArrayList<Integer>) wQ.list();
        } else {
           // System.out.println("List is empty");
        }
        return  thisLIst;
    }


    public List<?> getInActiveEmployees(int pStartRow, int pEndRow,
                                        String pSortOrder, String pSortCriterion, LocalDate pFDate,
                                        LocalDate pTDate, Long pTermId,  Long pUid, Long pEid, Long pMdaId,
                                        Long pSchoolId, String pPayPeriod, BusinessCertificate bc, boolean paginate)
    {

        boolean wUseMda = IppmsUtils.isNotNullAndGreaterThanZero(pMdaId);



        boolean wUseSchool = IppmsUtils.isNotNullAndGreaterThanZero(pSchoolId);


        boolean wUsePayPeriod = pPayPeriod.length() > 1;
        boolean pUseUserId = IppmsUtils.isNotNullAndGreaterThanZero(pUid);
        boolean pUseEmpId = IppmsUtils.isNotNullAndGreaterThanZero(pEid);
        boolean pUsingTermId = IppmsUtils.isNotNullAndGreaterThanZero(pTermId);
        String wHql = "";
       /*
          String wHql = "select e.id,e.employeeId,e.lastName, e.firstName, e.initials," +
                "h.birthDate,h.hireDate,t.terminationDate,t.lastModTs,t.auditTime," +
                "m.id,m.name,m.codeName,coalesce(t.schoolInfo.id,0),l.firstName,l.lastName,tr.name,s.salaryType.name,s.level,s.step " +
                " from "+IppmsUtils.getEmployeeTableName(bc)+" e,TerminationLog t,User l,HiringInfo h,TerminateReason tr,SalaryInfo s,MdaInfo m " +
                "where t."+bc.getEmployeeIdJoinStr()+" = e.id and t.user.id = l.id and t.mdaInfo.id = m.id " +
                "and h."+bc.getEmployeeIdJoinStr()+" = e.id and tr.id = t.terminateReason.id " +
                "and s.id = t.salaryInfo.id and e.statusIndicator = 1 and e.businessClientId = :pBizIdVar ";
        */
      if(bc.isPensioner()){
          wHql = "select e.pensioner_inst_id As empId,e.employee_Id As staffId,e.last_Name As empLN, e.first_Name As empFN,e.initials As empIN,h.birth_date As empBD,h.hire_date As empHD," +
                  "h.terminate_date empTD,t.last_mod_ts termLMT," +
                  "t.audit_time As empAT,m.mda_inst_id As mdaID, m.name As mda, m.code_name as mdaCode, coalesce(t.school_inst_id,0) As schID, s.salary_info_inst_id As sId, l.first_name As userFN,l.last_name As userLN," +
                  "tr.reason As termReason,st.name As salTypeName,s.salary_level As sLevel,s.salary_step As sStep,h.monthly_pension As penAmt, h.pension_start_date AS penSD,h.pension_end_date AS penED " +
                  "from ippms_pensioner e,ippms_termination_audit t,ippms_user l,ippms_hire_info h," +
                  "ippms_terminate_reason tr,ippms_salary_info s,ippms_salary_type st, ippms_mda_info m  " +
                  "where t.mda_inst_id = m.mda_inst_id and s.salary_type_inst_id = st.salary_type_inst_id and " +
                  "t.pensioner_inst_id = e.pensioner_inst_id and t.user_inst_id = l.user_id and h.pensioner_inst_id = e.pensioner_inst_id " +
                  "and tr.term_reason_inst_id = t.term_reason_inst_id and s.salary_info_inst_id = t.salary_info_inst_id and e.status_Ind = 1 and " +
                  "e.business_client_inst_id = "+bc.getBusinessClientInstId() ;
      }else{
          wHql = "select e.employee_inst_id As empId,e.employee_Id As staffId,e.last_Name As empLN, e.first_Name As empFN,e.initials As empIN,h.birth_date As empBD,h.hire_date As empHD," +
                  "t.termination_date empTD,t.last_mod_ts termLMT," +
                  "t.audit_time As empAT,m.mda_inst_id As mdaID, m.name As mda, m.code_name as mdaCode, coalesce(t.school_inst_id,0) As schID,  s.salary_info_inst_id As sId, l.first_name As userFN,l.last_name As userLN," +
                  "tr.reason As termReason,st.name As salTypeName,s.salary_level As sLevel,s.salary_step As sStep " +
                  "from ippms_employee e,ippms_termination_audit t,ippms_user l,ippms_hire_info h," +
                  "ippms_terminate_reason tr,ippms_salary_info s,ippms_salary_type st, ippms_mda_info m  " +
                  "where t.mda_inst_id = m.mda_inst_id and s.salary_type_inst_id = st.salary_type_inst_id and " +
                  "t.employee_inst_id = e.employee_inst_id and t.user_inst_id = l.user_id and h.employee_inst_id = e.employee_inst_id " +
                  "and tr.term_reason_inst_id = t.term_reason_inst_id and s.salary_info_inst_id = t.salary_info_inst_id and e.status_Ind = 1 and " +
                  "e.business_client_inst_id = "+bc.getBusinessClientInstId() ;
      }


        if(bc.isPensioner())
            wHql += " and h.pension_end_date is not null";
        else
            wHql += " and h.terminate_date is not null";

        if(pFDate != null && pTDate != null){
            if(bc.isPensioner())
                wHql += " and h.pension_end_date >= :pStartDate and h.pension_end_date <= :pEndDate ";
            else
                wHql += " and h.terminate_date >= :pStartDate and h.terminate_date <= :pEndDate ";
        }
        if(wUseMda){
            wHql += " and m.mda_inst_id = :pMdaInstIdVar ";
        }
        if(wUseSchool){
            wHql += " and t.school_inst_id = :pSchoolInstIdVar ";
        }
        if(wUsePayPeriod){
            wHql += " and t.pay_period = :pPayPeriodVar ";
        }
        if (pUseUserId)
            wHql += " and l.user_id = :pUserIdVar ";
        if(pUseEmpId){
            if(bc.isPensioner())
                wHql += " and e.pensioner_inst_id = :pEmpIdVar ";
            else
                wHql += " and e.employee_inst_id = :pEmpIdVar ";
        }

        if(pUsingTermId)
            wHql += " and tr.term_reason_inst_id = :pTermIdVar ";


        NativeQuery wQuery = this.sessionFactory.getCurrentSession().createNativeQuery(wHql)
                .addScalar("empId", StandardBasicTypes.LONG)
                .addScalar("staffId",StandardBasicTypes.STRING)
                .addScalar("empLN",StandardBasicTypes.STRING)
                .addScalar("empFN",StandardBasicTypes.STRING)
                .addScalar("empIN",StandardBasicTypes.STRING)
                .addScalar("empBD", LocalDateType.INSTANCE)
                .addScalar("empHD",LocalDateType.INSTANCE)
                .addScalar("empTD",LocalDateType.INSTANCE)
                .addScalar("termLMT",LocalDateType.INSTANCE)
                .addScalar("empAT",StandardBasicTypes.STRING)
                .addScalar("mdaID",StandardBasicTypes.LONG)
                .addScalar("mda",StandardBasicTypes.STRING)
                .addScalar("mdaCode",StandardBasicTypes.STRING)
                .addScalar("schID",StandardBasicTypes.LONG)
                .addScalar("sId",StandardBasicTypes.LONG)
                .addScalar("userFN",StandardBasicTypes.STRING)
                .addScalar("userLN",StandardBasicTypes.STRING)
                .addScalar("termReason",StandardBasicTypes.STRING)
                .addScalar("salTypeName",StandardBasicTypes.STRING)
                .addScalar("sLevel",StandardBasicTypes.INTEGER)
                .addScalar("sStep",StandardBasicTypes.INTEGER);
        if(bc.isPensioner()) {
            wQuery.addScalar("penAmt", StandardBasicTypes.DOUBLE);
            wQuery.addScalar("penSD", LocalDateType.INSTANCE);
            wQuery.addScalar("penED", LocalDateType.INSTANCE);
        }

        if(pFDate != null && pTDate != null){
            wQuery.setParameter("pStartDate", pFDate);
            wQuery.setParameter("pEndDate", pTDate);
        }
        if(wUseMda){
            wQuery.setParameter("pMdaInstIdVar",pMdaId);

        }
        if(wUseSchool){
            wQuery.setParameter("pSchoolInstIdVar",pSchoolId);
        }
        if(wUsePayPeriod){

            wQuery.setParameter("pPayPeriodVar",pPayPeriod);
        }
        if (pUseUserId)
            wQuery.setParameter("pUserIdVar",pUid);

        if(pUseEmpId)
            wQuery.setParameter("pEmpIdVar",pEid);
        if(pUsingTermId)
            wQuery.setParameter("pTermIdVar",pTermId);

        //wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());
//        if(paginate) {
//            if (pStartRow > 0)
//                wQuery.setFirstResult(pStartRow);
//            wQuery.setMaxResults(pEndRow);
//        }


        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        List<HiringInfo> wRetList = new ArrayList<>(wRetVal.size());
        if (wRetVal.size() > 0)
        {
            HiringInfo s = null;
            Employee e = null;
            int i = 0;
            for (Object[] o : wRetVal) {
                s = new HiringInfo();
                e = new Employee((Long)o[i++]);
                e.setEmployeeId((String)o[i++]);
                e.setLastName((String)o[i++]);
                e.setFirstName((String)o[i++]);
                e.setInitials(StringUtils.trimToEmpty((String)o[i++]));
                s.setBirthDate((LocalDate)o[i++]);
                s.setHireDate((LocalDate)o[i++]);
                s.setTerminateDate((LocalDate)o[i++]);
                s.setLastModTs(Timestamp.valueOf(((LocalDate) o[i++]).atTime(LocalTime.MIDNIGHT)));
                s.setAuditTime((String)o[i++]);
                e.setMdaDeptMap(new MdaDeptMap());
                e.getMdaDeptMap().setMdaInfo(new MdaInfo((Long)o[i++], (String)o[i++], (String)o[i++]));
                e.setMdaInstId(e.getMdaDeptMap().getMdaInfo().getId());
                e.setSchoolInfo(new SchoolInfo((Long)o[i++]));
                e.setSalaryInfo(new SalaryInfo((Long)o[i++]));
                s.setLgaName(o[i++] +" "+ o[i++]);
                s.setReligionStr((String)o[i++]);
                s.setAccountNumber(o[i++]+":"+ o[i++] +"/"+  o[i++]);
                if(bc.isPensioner()) {
                    s.setMonthlyPensionAmount((Double) o[i++]);
                    s.setPensionStartDate((LocalDate)o[i++]);
                    s.setPensionEndDate((LocalDate)o[i++]);
                }
                s.setEmployee(e);
                wRetList.add(s);
                i = 0;
            }
        }

        return wRetList;

    }

    public int getTotalNoOfInActiveEmployees(LocalDate pTime, LocalDate pTime2,
                                             Long pTermId, boolean pUseDates, boolean pUsingTermId, Long pUid,
                                             Long pEid, Long pMdaId, Long pSchoolId, String pPayPeriod,
                                             BusinessCertificate bc)
    {

        int wRetVal = 0;
        boolean wUsePayPeriod = pPayPeriod.length() > 1;
        boolean pUseUserId = IppmsUtils.isNotNullAndGreaterThanZero(pUid) ;
        boolean pUseEmpId = IppmsUtils.isNotNullAndGreaterThanZero(pEid);
        boolean wUseSchool = IppmsUtils.isNotNullAndGreaterThanZero(pSchoolId) ;
        boolean wUseMda = IppmsUtils.isNotNullAndGreaterThanZero(pMdaId);

        String  wHql = "";
        if(bc.isPensioner())
        wHql = "select count(e.pensioner_inst_id) As empId from ippms_pensioner e,ippms_termination_audit t,ippms_user l,ippms_hire_info h," +
                "ippms_terminate_reason tr,ippms_salary_info s,ippms_salary_type st, ippms_mda_info m  " +
                "where t.mda_inst_id = m.mda_inst_id and s.salary_type_inst_id = st.salary_type_inst_id and " +
                "t.pensioner_inst_id = e.pensioner_inst_id and t.user_inst_id = l.user_id and h.pensioner_inst_id = e.pensioner_inst_id " +
                "and tr.term_reason_inst_id = t.term_reason_inst_id and s.salary_info_inst_id = t.salary_info_inst_id and e.status_Ind = 1 and " +
                "e.business_client_inst_id = "+bc.getBusinessClientInstId() ;
        else
           wHql = "select count(e.employee_inst_id) As empId from ippms_employee e,ippms_termination_audit t,ippms_user l,ippms_hire_info h," +
                "ippms_terminate_reason tr,ippms_salary_info s,ippms_salary_type st, ippms_mda_info m  " +
                "where t.mda_inst_id = m.mda_inst_id and s.salary_type_inst_id = st.salary_type_inst_id and " +
                "t.employee_inst_id = e.employee_inst_id and t.user_inst_id = l.user_id and h.employee_inst_id = e.employee_inst_id " +
                "and tr.term_reason_inst_id = t.term_reason_inst_id and s.salary_info_inst_id = t.salary_info_inst_id and e.status_Ind = 1 and " +
                "e.business_client_inst_id = "+bc.getBusinessClientInstId() ;


        if(bc.isPensioner())
            wHql += "and h.pension_end_date is not null ";
        else
            wHql += "and h.terminate_date is not null ";

        if(pTime != null && pTime2 != null){
            if(bc.isPensioner())
                wHql += "and h.pension_end_date >= :pStartDate and h.pension_end_date <= :pEndDate ";
            else
                wHql += "and h.terminate_date >= :pStartDate and h.terminate_date <= :pEndDate ";
        }
        if (wUseMda) {
            wHql += "and m.mda_inst_id = :pMdaInstIdVar ";
        }
        if (wUseSchool) {
            wHql += "and t.school_inst_id = :pSchoolInstIdVar ";
        }
        if (wUsePayPeriod) {
            wHql += "and t.pay_period = :pPayPeriodVar ";
        }
        if (pUseUserId)
            wHql += "and l.user_id = :pUserIdVar ";
        if (pUseEmpId){
            if(bc.isPensioner())
                wHql += "and e.pensioner_inst_id = :pEmpIdVar ";
            else
                wHql += " and e.employee_inst_id = :pEmpIdVar ";
        }

        if (pUsingTermId)
            wHql += "and tr.id = :pTermIdVar ";


        NativeQuery wQuery = this.sessionFactory.getCurrentSession().createNativeQuery(wHql).addScalar("empId",StandardBasicTypes.LONG);

        if (pTime != null && pTime2 != null) {
            wQuery.setParameter("pStartDate", pTime);
            wQuery.setParameter("pEndDate", pTime2);
        }
        if (wUseMda) {
            wQuery.setParameter("pMdaInstIdVar", pMdaId);

        }
        if (wUseSchool) {
            wQuery.setParameter("pSchoolInstIdVar", pSchoolId);
        }
        if (wUsePayPeriod) {

            wQuery.setParameter("pPayPeriodVar", pPayPeriod);
        }
        if (pUseUserId)
            wQuery.setParameter("pUserIdVar", pUid);

        if (pUseEmpId)
            wQuery.setParameter("pEmpIdVar", pEid);
        if (pUsingTermId)
            wQuery.setParameter("pTermIdVar", pTermId);

       // wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        List<Long> results = wQuery.list();
        if ((results != null) && (!results.isEmpty())) {
            wRetVal = results.get(0).intValue();
        }
        return wRetVal;
    }

    public List<SuspensionLog> getSuspendedEmployees(int pStartRow, int pEndRow, String pSortOrder, String pSortCriterion,
                                                     LocalDate pFromDate, LocalDate pToDate, Long pTypeId, boolean pUseCode, BusinessCertificate bc)
    {
        String sql = "";

        if (pUseCode)
        {
            sql = "select s.id,s.suspensionDate,s.referenceNumber,st.id, st.name,si.level, si.step,ss.name,e.id,e.lastName,e.firstName,e.initials,s.payPercentage " +
                    "from SuspensionLog s, SalaryInfo si, SalaryType ss, "+IppmsUtils.getEmployeeTableName(bc)+" e, HiringInfo h, SuspensionType st " +
                    "where s."+bc.getEmployeeIdJoinStr()+" = e.id and e.id = h."+bc.getEmployeeIdJoinStr()+" and st.id = s.suspensionType.id and s.salaryInfo.id = si.id and si.salaryType.id = ss.id and h.suspended > 0 " +
                    "and e.statusIndicator = 0 and s.suspensionDate >= :pStartDate and s.suspensionDate <= :pEndDate and st.id = :pId and h.businessClientId = :pBizClientIdVar ";
        }
        else
        {
            sql = "select s.id,s.suspensionDate,s.referenceNumber,st.id, st.name,si.level, si.step,ss.name,e.id,e.employeeId,e.lastName,e.firstName,e.initials,s.payPercentage " +
                    "from SuspensionLog s, SalaryInfo si, SalaryType ss, "+IppmsUtils.getEmployeeTableName(bc)+" e, HiringInfo h, SuspensionType st " +
                    "where s."+bc.getEmployeeIdJoinStr()+" = e.id and e.id = h."+bc.getEmployeeIdJoinStr()+" and st.id = s.suspensionType.id and s.salaryInfo.id = si.id and si.salaryType.id = ss.id and h.suspended > 0 " +
                    "and e.statusIndicator = 0 and s.suspensionDate >= :pStartDate and s.suspensionDate <= :pEndDate  and h.businessClientId = :pBizClientIdVar ";
        }

        Query query = this.sessionFactory.getCurrentSession().createQuery(sql);

        query.setParameter("pStartDate", pFromDate);
        query.setParameter("pEndDate", pToDate);
        query.setParameter("pBizClientIdVar", bc.getBusinessClientInstId());
        if(pUseCode)
            query.setParameter("pId", pTypeId);


        List<Object[]> wRetVal = (ArrayList<Object[]>)query.list();
        List<SuspensionLog> wRetList = new ArrayList<>();
        if ((wRetVal != null) && (wRetVal.size() > 0))
        {
            SuspensionLog s = null;
            int i = 0;
            for (Object[] o : wRetVal) {
                s= new SuspensionLog();
                s.setId((Long)o[i++]);
                s.setSuspensionDate((LocalDate)o[i++]);
                s.setReferenceNumber((String)o[i++]);
                s.setSuspensionType(new SuspensionType((Long)o[i++], (String)o[i++]));

                s.setSalaryLevel(((Integer)o[i++]));
                s.setSalaryStep(((Integer)o[i++]));
                s.setSalaryScale((String)o[i++]);
                s.setEmployee(new Employee((Long)o[i++],(String)o[i++], (String)o[i++], (String)o[i++], (String)o[i++]));
                Object wObj = o[i++];
                if (wObj != null)
                    s.setPayPercentage(((Double)wObj));
                else {
                    s.setPayPercentage(0.0D);
                }

                wRetList.add(s);
                i = 0;
            }

        }

        return wRetList;
    }

    public List<SuspensionLog> getAllSuspendedEmployeesForReport(LocalDate pFromDate, LocalDate pToDate, Long pTypeId, boolean pUseCode, BusinessCertificate bc)
    {
        String sql = "";

        if (pUseCode)
        {
            sql = "select s.id,s.suspensionDate,s.referenceNumber,st.id, st.name,si.level, si.step,ss.name,e.id,e.lastName,e.firstName,e.initials,s.payPercentage " +
                    "from SuspensionLog s, SalaryInfo si, SalaryType ss, "+IppmsUtils.getEmployeeTableName(bc)+" e, HiringInfo h, SuspensionType st " +
                    "where s."+bc.getEmployeeIdJoinStr()+" = e.id and e.id = h."+bc.getEmployeeIdJoinStr()+" and st.id = s.suspensionType.id and s.salaryInfo.id = si.id and si.salaryType.id = ss.id and h.suspended > 0 " +
                    "and e.statusIndicator = 0 and s.suspensionDate >= :pStartDate and s.suspensionDate <= :pEndDate and st.id = :pId and h.businessClientId = :pBizClientIdVar ";
        }
        else
        {
            sql = "select s.id,s.suspensionDate,s.referenceNumber,st.id, st.name,si.level, si.step,ss.name,e.id,e.employeeId,e.lastName,e.firstName,e.initials,s.payPercentage " +
                    "from SuspensionLog s, SalaryInfo si, SalaryType ss, "+IppmsUtils.getEmployeeTableName(bc)+" e, HiringInfo h, SuspensionType st " +
                    "where s."+bc.getEmployeeIdJoinStr()+" = e.id and e.id = h."+bc.getEmployeeIdJoinStr()+" and st.id = s.suspensionType.id and s.salaryInfo.id = si.id and si.salaryType.id = ss.id and h.suspended > 0 " +
                    "and e.statusIndicator = 0 and s.suspensionDate >= :pStartDate and s.suspensionDate <= :pEndDate  and h.businessClientId = :pBizClientIdVar ";
        }

        Query query = this.sessionFactory.getCurrentSession().createQuery(sql);

        query.setParameter("pStartDate", pFromDate);
        query.setParameter("pEndDate", pToDate);
        query.setParameter("pBizClientIdVar", bc.getBusinessClientInstId());
        if(pUseCode)
            query.setParameter("pId", pTypeId);


        List<Object[]> wRetVal = (ArrayList<Object[]>)query.list();
        List<SuspensionLog> wRetList = new ArrayList<>();
        if ((wRetVal != null) && (wRetVal.size() > 0))
        {
            SuspensionLog s;
            int i = 0;
            for (Object[] o : wRetVal) {
                s= new SuspensionLog();
                s.setId((Long)o[i++]);
                s.setSuspensionDate((LocalDate)o[i++]);
                s.setReferenceNumber((String)o[i++]);
                s.setSuspensionType(new SuspensionType((Long)o[i++], (String)o[i++]));

                s.setSalaryLevel(((Integer)o[i++]));
                s.setSalaryStep(((Integer)o[i++]));
                s.setSalaryScale((String)o[i++]);
                s.setEmployee(new Employee((Long)o[i++],(String)o[i++], (String)o[i++], (String)o[i++], (String)o[i++]));
                Object wObj = o[i++];
                if (wObj != null)
                    s.setPayPercentage(((Double)wObj));
                else {
                    s.setPayPercentage(0.0D);
                }

                wRetList.add(s);
                i = 0;
            }

        }

        return wRetList;
    }

    public int getTotalNumberOfSuspendedEmployees(BusinessCertificate bc,
                                                  LocalDate pFromDate, LocalDate pToDate, Long pSuspensionId, boolean pUseUserId)
    {
        String sql = "";

        if (pUseUserId)
        {
            sql = "select count(s.id) from SuspensionLog s, SalaryInfo si, SalaryType ss, "+IppmsUtils.getEmployeeTableName(bc)+" e, "
                    + "HiringInfo h where s."+bc.getEmployeeIdJoinStr()+" = e.id and e.id = h."+bc.getEmployeeIdJoinStr()+" and s.salaryInfo.id = si.id and si.salaryType.id = ss.id "
                    + "and h.suspended > 0 and e.statusIndicator = 0 and s.suspensionDate >= :pStartDate and s.suspensionDate <= :pEndDate and h.businessClientId = :pBizClientVar ";
        }
        else
        {
            sql = "select count(s.id) from SuspensionLog s, SalaryInfo si, SalaryType ss, "+IppmsUtils.getEmployeeTableName(bc)+" e, HiringInfo h "
                    + "where s."+bc.getEmployeeIdJoinStr()+" = e.id and e.id = h."+bc.getEmployeeIdJoinStr()+" " +
                    "and s.salaryInfo.id = si.id and si.salaryType.id = ss.id and h.suspended > 0 and e.statusIndicator = 0 and s.suspensionDate >= :pStartDate and s.suspensionDate <= :pEndDate" +
                    " and h.businessClientId = :pBizClientVar ";
        }

        Query query = this.sessionFactory.getCurrentSession().createQuery(sql);

        query.setParameter("pStartDate", pFromDate);
        query.setParameter("pEndDate", pToDate);
        query.setParameter("pBizClientVar", bc.getBusinessClientInstId());

        List results = query.list();

        if ((results == null) || (results.isEmpty())) {
            return 0;
        }
        return ((Long)results.get(0)).intValue();
    }

    public List<AbstractEmployeeEntity> getActiveEmployeesByPayGroup(int pStartRow,
                                                       int pEndRow, String pSortOrder, String pSortCriterion,
                                                       Long pSalaryTypeId, int pFromLevel, int pToLevel, BusinessCertificate bc)
    {
        List<AbstractEmployeeEntity> wRetList = new ArrayList<>();

        String wHql = "select e.id,e.employeeId,e.firstName,e.lastName,e.initials,e.mdaDeptMap.id," +
                "m.id,m.name,s.id,s.level,s.step,m.codeName from "+IppmsUtils.getEmployeeTableName(bc)+" e, MdaInfo m, MdaDeptMap mda, " +
                "SalaryInfo s,SalaryType st where  "
                + " e.salaryInfo.id = s.id and s.salaryType.id = st.id and st.id = :pTypeId " +
                "and e.mdaDeptMap.id = mda.id and mda.mdaInfo.id = m.id and e.statusIndicator = 0 and e.businessClientId = :pBizIdVar";

        if(pFromLevel > 0 && pToLevel > 0)
            wHql += " and s.level >= :pFromLevel and s.level <= :pToLevel ";
        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        wQuery.setParameter("pTypeId",  pSalaryTypeId );
        wQuery.setParameter("pBizIdVar",  bc.getBusinessClientInstId() );
        if(pFromLevel > 0 && pToLevel > 0) {
            wQuery.setParameter("pFromLevel",  pFromLevel );
            wQuery.setParameter("pToLevel",  pToLevel );
        }

//        if (pStartRow > 0)
//            wQuery.setFirstResult(pStartRow);
//        wQuery.setMaxResults(pEndRow);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0))
        {
            int i = 0;
            for (Object[] o : wRetVal) {

                AbstractEmployeeEntity e = new Employee();

                e.setId((Long)o[i++]);
                e.setEmployeeId((String)o[i++]);
                e.setFirstName((String)o[i++]);
                e.setLastName((String)o[i++]);

                e.setInitials(StringUtils.trimToEmpty((String) o[i++]));
                e.setMdaDeptMap(new MdaDeptMap((Long)o[i++],(Long)o[i++], (String)o[i++]));
                SalaryInfo s = new SalaryInfo((Long)o[i++]);
                s.setLevel(((Integer)o[i++]).intValue());
                s.setStep(((Integer)o[i++]).intValue());
                e.getMdaDeptMap().getMdaInfo().setCodeName((String)o[i++]);
                e.setSalaryInfo(s);

                wRetList.add(e);
                i = 0;
            }

        }

        return wRetList;
    }

    public int getTotalNoOfEmployeesOnPayGroup(BusinessCertificate bc,
            Long pSalaryTypeId, int pFromLevel, int pToLevel)
    {
        int retVal = 0;

        String wHql = "select count(e.id) from "+IppmsUtils.getEmployeeTableName(bc)+" e, SalaryInfo s,SalaryType st " +
                "where  e.salaryInfo.id = s.id and s.salaryType.id = st.id and st.id = :pTypeId " +
                "and e.statusIndicator = 0 and e.businessClientId = :pBizIdVar";


        if(pFromLevel > 0 && pToLevel > 0)
            wHql += " and s.level >= :pFromLevel and s.level <= :pToLevel";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        wQuery.setParameter("pTypeId", pSalaryTypeId);
        wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());
        if(pFromLevel > 0 && pToLevel > 0) {
            wQuery.setParameter("pFromLevel", new Integer(pFromLevel));
            wQuery.setParameter("pToLevel", new Integer(pToLevel));
        }

        List list = wQuery.list();

        if (list == null) {
            return retVal;
        }
        return ((Long)list.get(0)).intValue();
    }

    public List<Employee> loadActiveEmployeeByRenumeration(int pStartRow,
                                                           int pEndRow, String pSortOrder, String pSortCriterion,
                                                           int pApprovedMonthInd, int pApprovedYearInd, BusinessEmpOVBean pBEBean,
                                                           BusinessCertificate bc, boolean pForExport) throws Exception {


        LocalDate wFrom = LocalDate.now();
        LocalDate wTo = LocalDate.now();



        Long wMdaInd = 0L;
        boolean wFilterByBank = pBEBean.getBankTypeInd() > 0;
        boolean wFilterByHireDate = pBEBean.getAllowanceStartDate() != null;
        boolean wFilterByMda = IppmsUtils.isNotNullAndGreaterThanZero(pBEBean.getMdaInstId());
        LocalDate wLastApproved = LocalDate.now();
        LocalDate.of(pApprovedYearInd, pApprovedMonthInd, 1);

        HashMap<Long,SchoolInfo> schoolMap = this.genericService.loadObjectAsMapWithConditions(SchoolInfo.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId())),"id");

        String hqlQuery = "select e.id,e.employeeId, e.firstName, e.lastName, e.initials" +
                ",p.mdaDeptMap.id,m.id,m.name,e.gsmNumber,h.gender " +
                ",l.name,r.name,h.hireDate,ms.name, p.taxesPaid, p.totalPay,e.address1 " +
                ",et.name,h.birthDate,h.expectedDateOfRetirement,h.contractEndDate,h.terminateDate, h.pensionEndDate,s.level,s.step, p.monthlyBasic, p.totalPay, p.totalDeductions, p.accountNumber, bi.name, bb.name, s.salaryType.name," +
                " coalesce(e.schoolInfo.id,0),p.contributoryPension, pfa.name,p.pensionPinCode,p.totalAllowance" +
                " from " + IppmsUtils.getEmployeeTableName(bc) + " e, EmployeeType et,HiringInfo h, " + IppmsUtils.getPaycheckTableName(bc) + " p," +
                "Religion r, LGAInfo l,MaritalStatus ms, SalaryInfo s, MdaInfo m,MdaDeptMap mdm,PfaInfo pfa," +
                " BankInfo bi, BankBranch bb " +
                " where e.id = p.employee.id and e.id = h."+bc.getEmployeeIdJoinStr()+" and e.religion.id = r.id and s.id = p.salaryInfo.id and m.id = mdm.mdaInfo.id and p.mdaDeptMap.id = mdm.id " +
                "and e.lgaInfo.id = l.id and e.employeeType.id = et.id and h.maritalStatus.id = ms.id and" +
                " p.bankBranch.id = bb.id and bb.bankInfo.id = bi.id and pfa.id = p.pfaInfo.id and" +
                " p.runMonth = :pRunMonthVal and p.runYear = :pRunYearVal and p.netPay > 0 and e.businessClientId = :pBizIdVar ";

        if(bc.isPensioner())
            hqlQuery = "select e.id,e.employeeId, e.firstName, e.lastName, e.initials" +
                    ",p.mdaDeptMap.id,m.id,m.name,e.gsmNumber,h.gender " +
                    ",l.name,r.name,h.hireDate,ms.name, p.taxesPaid, p.totalPay,e.address1 " +
                    ",et.name,h.birthDate,h.expectedDateOfRetirement,h.contractEndDate,h.terminateDate, h.pensionEndDate,s.level,s.step, p.monthlyBasic, p.totalPay, p.totalDeductions, p.accountNumber, bi.name, bb.name, s.salaryType.name" +
                    " " +
                    " from " + IppmsUtils.getEmployeeTableName(bc) + " e, EmployeeType et,HiringInfo h, " + IppmsUtils.getPaycheckTableName(bc) + " p," +
                    "Religion r, LGAInfo l,MaritalStatus ms, SalaryInfo s, MdaInfo m,MdaDeptMap mdm," +
                    " BankInfo bi, BankBranch bb " +
                    " where e.id = p.employee.id and e.id = h."+bc.getEmployeeIdJoinStr()+" and e.religion.id = r.id and s.id = p.salaryInfo.id and m.id = mdm.mdaInfo.id and p.mdaDeptMap.id = mdm.id " +
                    "and e.lgaInfo.id = l.id and e.employeeType.id = et.id and h.maritalStatus.id = ms.id and" +
                    " p.bankBranch.id = bb.id and bb.bankInfo.id = bi.id and " +
                    " p.runMonth = :pRunMonthVal and p.runYear = :pRunYearVal and p.netPay > 0 and e.businessClientId = :pBizIdVar ";
        List<Employee> wRetList = new ArrayList<>();
        if (wFilterByBank) {
            if (pBEBean.getBankTypeInd() == 1) {
                hqlQuery += "and bi.mfbInd = 0";
            } else if (pBEBean.getBankTypeInd() == 2) {
                hqlQuery += "and bi.mfbInd = 1";
            }
        }
        if (wFilterByHireDate) {
            wFrom = PayrollBeanUtils.getNextORPreviousDay(PayrollBeanUtils.setDateFromString(pBEBean.getStartDateStr()), false);
            wTo = LocalDate.now();
        }
            if (pBEBean.getAllowanceEndDate() != null) {
                wTo = pBEBean.getAllowanceEndDate();
                if(bc.isPensioner()){
                    hqlQuery += " and h.pensionStartDate > :pHireDateVar and h.pensionStartDate < :pHireDateVar2";
                }else{
                    hqlQuery += " and h.hireDate > :pHireDateVar and h.hireDate < :pHireDateVar2";
                }

            }
            if (wFilterByMda) {


                hqlQuery += " and m.id = " + wMdaInd;

            }
            hqlQuery += " order by e.lastName,e.firstName,e.initials asc ";


            Query wQuery = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
            wQuery.setParameter("pRunMonthVal", pApprovedMonthInd);
            wQuery.setParameter("pRunYearVal", pApprovedYearInd);
            if (wFilterByHireDate) {
                wQuery.setParameter("pHireDateVar", wFrom);
                wQuery.setParameter("pHireDateVar2", wTo);
            }
//            if(pStartRow != 0 && pEndRow != 0) {
//                if (IppmsUtils.isNotNullAndGreaterThanZero(pStartRow))
//                    wQuery.setFirstResult(pStartRow);
//                wQuery.setMaxResults(pEndRow);
//            }
        if(!pForExport) {
            if (pStartRow > 0)
                wQuery.setFirstResult(pStartRow);
            wQuery.setMaxResults(pEndRow);
        }

            wQuery.setParameter("pBizIdVar",bc.getBusinessClientInstId());
            List<Object[]> results = wQuery.list();

            if ((results != null) && (results.size() > 0)) {
                double taxesTotal = 0.0D;
                double grossPayTotal = 0.0D;

                Object schoolId;
                Employee e;
                HiringInfo h;
                SalaryInfo s;
                Double totalPay,totalDeductions,netPay;
                LocalDate wGc;
                for (Object[] o : results) {
                    int i = 0;
                      e = new Employee();
                      h = new HiringInfo();
                    h.setId((Long) o[i++]);
                    e.setEmployeeId((String) o[i++]);
                    e.setFirstName((String) o[i++]);
                    e.setLastName((String) o[i++]);

                    e.setInitials(StringUtils.trimToEmpty((String) o[i++]));


                    e.setMdaDeptMap(new MdaDeptMap((Long) o[i++]));
                    e.getMdaDeptMap().setMdaInfo(new MdaInfo((Long) o[i++]));
                    e.setMdaName((String) o[i++]);


                    e.setGsmNumber((String) o[i++]);
                    h.setGender((String) o[i++]);
                    h.setLgaName((String) o[i++]);
                    h.setReligionStr((String) o[i++]);
                    h.setHireDate((LocalDate) o[i++]);
                    h.setMaritalStatusStr((String) o[i++]);
                    h.setTaxesPaid((Double) o[i++]);
                    h.setTerminalGrossPay((Double) o[i++]);
                    e.setAddress1((String) o[i++]);
                    e.setEmployeeTypeStr((String) o[i++]);
                    h.setBirthDate((LocalDate) o[i++]);
                    h.setExpectedDateOfRetirement((LocalDate) o[i++]);
                    h.setContractEndDate((LocalDate) o[i++]);
                    h.setTerminateDate((LocalDate) o[i++]);
                    h.setPensionEndDate((LocalDate) o[i++]);
                      s = new SalaryInfo();
                    s.setLevel((Integer) o[i++]);
                    s.setStep((Integer) o[i++]);
                    s.setMonthlyBasicSalary((Double)o[i++]);
                      totalPay = (Double)o[i++];
                      totalDeductions = (Double)o[i++];
                    //-- Now Determine if Contract Employee is Active.....
                    if (h.getContractEndDate() != null) {

                        wGc = h.getContractEndDate();
                        if (wGc.compareTo(wLastApproved) < 0) {
                            continue;
                        } else {
                            //replace Terminated Date
                            h.setTerminateDate(h.getContractEndDate());
                        }
                    } else {
                        if (h.getTerminateDate() == null) {
                            h.setTerminateDate(h.getExpectedDateOfRetirement());
                        }
                    }

                    taxesTotal += h.getTaxesPaid();
                    grossPayTotal += h.getTerminalGrossPay();


                    e.setAccountNumber((String)o[i++]);
                    e.setBankName((String)o[i++]);
                    e.setBranchName((String)o[i++]);
                    s.setPayGroupCode((String)o[i++]);
                    if(!bc.isPensioner()) {
                        try {
                            schoolId = o[i++];
                            if ((Long) schoolId != 0) {
                                e.setSchoolName(schoolMap.get(schoolId).getName());
                            } else {
                                e.setSchoolName("N/A");
                            }
                        } catch (Exception wEx) {
                            wEx.printStackTrace();
                        }
                        e.setPensionContribution((Double)o[i++]);
                        h.setPfaName((String)o[i++]);
                        h.setPensionPinCode((String)o[i++]);
                        s.setSpaAllowance((Double)o[i++]);


                    }
                    e.setHiringInfo(h);
                   /* String schoolName = (String)o[i++];
                    if(IppmsUtils.isNotNullOrEmpty(schoolName)){
                        e.setSchoolName(schoolName);
                    }
                    else{
                        e.setSchoolName("N/A");
                    }*/
                     netPay = totalPay - totalDeductions;
                    e.setNetPay(netPay);
                    e.setSalaryInfo(s);
                    wRetList.add(e);
                    //i = 0;
                }
                wRetList.get(0).setTotalTaxesPaid(taxesTotal);
                wRetList.get(0).setTotalGrossPay(grossPayTotal);

            }


        return wRetList;
    }

    public EmpContMiniBean getTotalNumberOfEmployeesByRenumeration(int pApprovedMonthInd, int pApprovedYearInd, BusinessEmpOVBean pBEBean,
    BusinessCertificate bc)
    {


        LocalDate wFrom = null;
        LocalDate wTo = null;

        boolean wRecordB4Sept2019 = PayrollHRUtils.isRecordBeforeSept2019(pApprovedMonthInd, pApprovedYearInd);
        boolean wFilterByBank = pBEBean.getBankTypeInd() > 0;
        boolean wFilterByHireDate = pBEBean.getAllowanceStartDate() != null;
        boolean wFilterByMda = IppmsUtils.isNotNullAndGreaterThanZero(pBEBean.getMdaInstId());
        LocalDate wLastApproved = LocalDate.now();
        LocalDate.of(pApprovedYearInd, pApprovedMonthInd, 1);
        int wNoOfDaysInMonth = PayrollBeanUtils.getNoOfDays(pApprovedMonthInd, pApprovedYearInd);
        String hqlQuery = "select e.employeeId,h.contractEndDate,p.taxesPaid,p.totalPay,p.netPay,p.noOfDays,p.payPercentage,s.monthlyBasicSalary,"
                + " m.id,m.name,p.monthlyBasic,h.monthlyPensionAmount" +
                " from "+IppmsUtils.getEmployeeTableName(bc)+" e, EmployeeType et,HiringInfo h, "+IppmsUtils.getPaycheckTableName(bc)+" p,Religion r, LGAInfo l,MaritalStatus ms, SalaryInfo s, " +
                "BankInfo bi, BankBranch bb, MdaDeptMap mda, MdaInfo m " +
                " where e.id = p.employee.id and e.id = h."+bc.getEmployeeIdJoinStr()+" and e.religion.id = r.id and p.salaryInfo.id = s.id "
                + " and mda.id = p.mdaDeptMap.id and mda.mdaInfo.id = m.id " +
                "and e.lgaInfo.id = l.id and e.employeeType.id = et.id and h.maritalStatus.id = ms.id and" +
                " p.bankBranch.id = bb.id and bb.bankInfo.id = bi.id and" +
                " p.runMonth = :pRunMonthVal and p.runYear = :pRunYearVal and  p.netPay > 0 and e.businessClientId = :pBizIdVar ";

        if(wFilterByBank){
            if(pBEBean.getBankTypeInd() == 1){
                hqlQuery += "and bi.mfbInd = 0 ";
            }else if(pBEBean.getBankTypeInd() == 2){
                hqlQuery += "and bi.mfbInd = 1 ";
            }
        }
        if(wFilterByHireDate){
            wFrom = PayrollBeanUtils.getNextORPreviousDay(PayrollBeanUtils.setDateFromString(pBEBean.getStartDateStr()), false);
            wTo = LocalDate.now();
            if(pBEBean.getAllowanceEndDate() != null){
                wTo = pBEBean.getAllowanceEndDate();
            }
            hqlQuery += " and h.hireDate > :pHireDateVar and h.hireDate < :pHireDateVar2";
        }
        if(wFilterByMda){

            hqlQuery += " and m.id = "+pBEBean.getMdaInstId();

        }
        hqlQuery += " order by e.lastName,e.firstName,e.initials asc ";
        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        wQuery.setParameter("pRunMonthVal", pApprovedMonthInd);
        wQuery.setParameter("pRunYearVal", pApprovedYearInd);
        if(wFilterByHireDate){
            wQuery.setParameter("pHireDateVar", wFrom);
            wQuery.setParameter("pHireDateVar2", wTo);
        }
        wQuery.setParameter("pBizIdVar",bc.getBusinessClientInstId());
        List<Object[]> results = wQuery.list();
        EmpContMiniBean wRetList = new EmpContMiniBean();
        double monthlyPension = 0.0D;
        if ((results != null) && (results.size() > 0)) {


            for (Object[] o : results) {


                wRetList.setObjectInd(wRetList.getObjectInd() + 1);

                /*
                 * if(wRetList.isNew()){ wRetList.setObjectInd(1); }else
                 * wRetList.setObjectInd(wRetList.getObjectInd() + 1);
                 */

                wRetList.setCurrentContribution(wRetList.getCurrentContribution() + (Double)o[2]);
                wRetList.setYearToDate(wRetList.getYearToDate() + (Double)o[3]);
                wRetList.setNetPay(wRetList.getNetPay() + (Double)o[4]);
                int wNoOfDays = (Integer)o[5];
                double wPayPercentage = (Double)o[6];
                double monthlyBasic = PayrollPayUtils.convertDoubleToEpmStandard((Double)o[7] / 12.0D);
                double paycheckMonthlyBasic = (Double)o[10];
                monthlyPension = (Double)o[11];
                if(wRecordB4Sept2019) {
                    monthlyBasic = paycheckMonthlyBasic;
                }else {


                    if (wNoOfDays > 0) {

                        monthlyBasic = PayrollPayUtils.convertDoubleToEpmStandard((monthlyBasic / new Double(wNoOfDaysInMonth)) * wNoOfDays);



                    }
                    else if (wPayPercentage > 0.0D) {

                        monthlyBasic = PayrollPayUtils.convertDoubleToEpmStandard(monthlyBasic * wPayPercentage);


                    }
                }
                wRetList.setMonthlyBasic(wRetList.getMonthlyBasic() + monthlyBasic);
                wRetList.setMdaInstId((Long)o[8]);
                wRetList.setMode((String)o[9]);
                wRetList.setMonthlyPension(wRetList.getMonthlyPension() + monthlyPension);
            }
        }
        return wRetList;
    }

    public List<AbmpBean> getActiveEmployeesForLTGByMDAP(int pStartRow, int pEndRow, String pSortOrder, String pSortCriterion, Long pEid, BusinessCertificate bc)
    {
        String wHql = "";

        ArrayList<Object[]> wRetVal;
        List<AbmpBean> wRetList = new ArrayList<>();


        wHql = "select e.employeeId,e.firstName,e.lastName,s.level,s.step,sc.name,s.monthlyBasicSalary,(s.monthlyBasicSalary * 1.2) from "
                + ""+IppmsUtils.getEmployeeTableName(bc)+" e ,SalaryInfo s, SalaryType sc, MdaDeptMap adm, MdaInfo a "
                + "where e.mdaDeptMap.id = adm.id and adm.mdaInfo.id = a.id and e.salaryInfo.id = s.id "
                + "and s.salaryType.id = sc.id and e.statusIndicator = 0 and a.id = :pIdValue and e.businessClientId = :pBizIdVar order by e.lastName, e.firstName";


        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pIdValue", pEid);
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());
        if (pStartRow > 0)
            query.setFirstResult(pStartRow);
        query.setMaxResults(pEndRow);

        wRetVal = (ArrayList<Object[]>)query.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                AbmpBean a = new AbmpBean();
                a.setEmployeeId((String)o[0]);
                a.setName(o[1] + " " + o[2]);
                a.setSalaryLevel(((Integer)o[3]));
                a.setSalaryStep(((Integer)o[4]));
                a.setSalaryScaleName((String)o[5]);
                a.setBasicSalary(((Double)o[6]));
                a.setLtgCost(((Double)o[7]));

                wRetList.add(a);
            }

        }

        return wRetList;
    }

    public int getTotalNoOfActiveEmployeesForLTGByMDAP(Long pEid, BusinessCertificate bc)
    {
        String wHql   = "select count(e.id) from "+IppmsUtils.getEmployeeTableName(bc)+" e ,"
                + " MdaDeptMap adm, MdaInfo a where e.mdaDeptMap.id = adm.id and adm.mdaInfo.id = a.id "
                + "and e.statusIndicator = 0 and a.id = :pIdValue and  e.businessClientId = :pBizIdVar is not null ";


        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pIdValue", pEid);
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());
        List list = query.list();

        if (list == null)
            return 0;
        if (list.size() == 0)
            return 0;
        if ((list.size() == 1) && (((Long)list.get(0)).intValue() == 0)) {
            return 0;
        }
        return ((Long)list.get(0)).intValue();
    }

    public List<AbstractEmployeeEntity> getTheFirst200Rows(BusinessCertificate bc, Integer maxResult){
        String wHql = "";

        ArrayList<Object[]> wRetVal;
        List<AbstractEmployeeEntity> wRetList = new ArrayList<>();


        wHql = "select e.id,e.employeeId,e.firstName,e.lastName,e.initials,t.name,m.mdaInfo.name,s.salaryType.name," +
                "s.level,s.step from "
                + ""+IppmsUtils.getEmployeeTableName(bc)+" e ,SalaryInfo s, MdaDeptMap m, MdaInfo a, Title t "
                + "where e.mdaDeptMap.id = m.id and m.mdaInfo.id = a.id and e.salaryInfo.id = s.id and t.id = e.title.id "
                + "and e.statusIndicator = 0 and e.businessClientId = :bIdValue order by e.id desc";


        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("bIdValue", bc.getBusinessClientInstId());
        if(maxResult>0){
            query.setMaxResults(maxResult);
        }

        wRetVal = (ArrayList<Object[]>)query.list();

        if (wRetVal.size() > 0) {
            AbstractEmployeeEntity a;
            for (Object[] o : wRetVal) {
                a = IppmsUtils.makeEmployeeObject(bc);
                a.setId((Long)o[0]);
                a.setEmployeeId((String)o[1]);
                a.setFirstName((String)o[2]);
                a.setLastName((String)o[3]);
                a.setInitials((String)o[4]);
                a.setDisplayTitle((String)o[5]);
//                a.setDisplayNameWivTitlePrefixed(o[5] + "." + " "+o[3]+ " " + o[2] + " " + o[4]);

                a.setCurrentMdaName((String)o[6]);
                a.setPayEmployee((String)o[7]);
                a.setLevelAndStep(o[8] + "/" + o[9]);


                wRetList.add(a);
            }

        }

        return wRetList;

    }

}