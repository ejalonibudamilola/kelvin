/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.domain.employee.BiometricInfo;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service("HrHelperService")
@Repository
@Transactional(readOnly = true)
public class HrServiceHelper {


    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }


    public List<HiringInfo> getEmpByExpRetireDate(BusinessCertificate bc, LocalDate pFromDate, LocalDate pToDate, boolean pForPayrollRun) {
        List<HiringInfo> wRetList = new ArrayList<>();


        String sql = "select e.id ,e.employeeId, e.lastName,e.firstName,e.initials,m.name,st.name,s.level,s.step,h.hireDate,h.birthDate";

        if(bc.isPensioner()) {
            sql += ",h.amAliveDate,h.yearlyPensionAmount,h.monthlyPensionAmount,h.id,h.pensionStartDate";

        }else{
            sql += ",h.expectedDateOfRetirement";
        }
        sql += " from "+ IppmsUtils.getEmployeeTableName(bc)+" e, HiringInfo h, SalaryInfo s, SalaryType st, MdaInfo m, MdaDeptMap mdm " +
                " where h."+bc.getEmployeeIdJoinStr()+" = e.id and e.salaryInfo.id = s.id and s.salaryType.id = st.id and m.id = mdm.mdaInfo.id and e.mdaDeptMap.id = mdm.id " +
                " and e.businessClientId = : pBizIdVar ";

        if(bc.isPensioner())
            sql += "and h.amAliveDate >= :pFromDateVar and h.amAliveDate <= :pToDateVar ";
        else
            sql += "and h.expectedDateOfRetirement >= :pFromDateVar and h.expectedDateOfRetirement <= :pToDateVar ";

        if(pForPayrollRun && bc.isPensioner())
            sql += "and h.monthlyPensionAmount > 0";

        sql += " order by e.lastName,e.firstName,e.initials";

        Query query = sessionFactory.getCurrentSession().createQuery(sql);

        query.setParameter("pFromDateVar", pFromDate);
        query.setParameter("pToDateVar", pToDate);
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)query.list();

        if (wRetVal.size() > 0)
        {
            int i = 0;
            for (Object[] o : wRetVal) {
                    Employee employee = new Employee<>();
                    employee.setId((Long)o[i++]);
                    employee.setEmployeeId((String)o[i++]);
                    employee.setLastName((String)o[i++]);
                    employee.setFirstName((String)o[i++]);
                    Object obj = o[i++];
                    if(obj != null)
                      employee.setInitials((String)obj);
                    employee.setMdaName((String)o[i++]);
                    employee.setSalaryTypeName(o[i++] +" : "+ PayrollUtils.makeLevelAndStep((Integer)o[i++],(Integer)o[i++]));
                    HiringInfo hiringInfo = new HiringInfo();
                    hiringInfo.setEmployee(employee);
                    hiringInfo.setHireDate((LocalDate)o[i++]);
                    hiringInfo.setBirthDate((LocalDate)o[i++]);
                    hiringInfo.setExpectedDateOfRetirement((LocalDate)o[i++]);
                    if(bc.isPensioner()) {
                        hiringInfo.setYearlyPensionAmount((Double) o[i++]);
                        hiringInfo.setMonthlyPensionAmount((Double) o[i++]);
                        hiringInfo.setId((Long)o[i++]);
                        hiringInfo.setPensionStartDate((LocalDate)o[i++]);
                        hiringInfo.setOldPensionAmount(hiringInfo.getMonthlyPensionAmount());
                    }
                wRetList.add(hiringInfo);
                i = 0;

            }
        }

        return  wRetList;

    }
    public List<BiometricInfo> loadEmp4BiometricVerification(BusinessCertificate bc, MdaInfo pMdaInfo) {
        List<BiometricInfo> wRetList = new ArrayList<>();


        String sql = "select e.id ,e.employeeId, e.lastName,e.firstName,e.legacyEmployeeId " +
                "from "+ IppmsUtils.getEmployeeTableName(bc)+" e,  MdaInfo m, MdaDeptMap mdm " +
                " where  m.id = mdm.mdaInfo.id and e.mdaDeptMap.id = mdm.id and m.id = :pMdaInfoId " +
                " and e.businessClientId = : pBizIdVar and e.biometricInfo.id is null and e.statusIndicator = 0";

        Query query = sessionFactory.getCurrentSession().createQuery(sql);

        query.setParameter("pMdaInfoId", pMdaInfo.getId());
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)query.list();

        if (wRetVal.size() > 0)
        {
            int i = 0;
            BiometricInfo employee;
            for (Object[] o : wRetVal) {
                employee = new BiometricInfo();
                employee.setParentId((Long)o[i++]);
                employee.setEmployeeId((String)o[i++]);
                employee.setLastName((String)o[i++]);
                employee.setFirstName((String)o[i++]);
                employee.setLegacyId((String)o[i++]);

                wRetList.add(employee);
                i = 0;

            }
        }

        return  wRetList;

    }
    public List<Employee> loadBiometricVerifiedStaffs(BusinessCertificate bc, Long pMdaId, boolean verified) {
        List<Employee> wRetList = new ArrayList<>();


        boolean useMda = IppmsUtils.isNotNullAndGreaterThanZero(pMdaId);

        String sql = "";
        if(verified){
            sql = "select e.id ,e.employeeId, e.lastName,e.firstName,e.initials, st.name,s.level,s.step,m.name,b.verifiedBy.firstName, b.verifiedBy.lastName,b.lastModTs " +
                    "from "+ IppmsUtils.getEmployeeTableName(bc)+" e,  MdaInfo m, MdaDeptMap mdm,SalaryInfo s, SalaryType st,BiometricInfo b " +
                    "where  m.id = mdm.mdaInfo.id and e.mdaDeptMap.id = mdm.id and b.id = e.biometricInfo.id and e.salaryInfo.id = s.id and s.salaryType.id = st.id " +
                    "and e.businessClientId = :pBizIdVar and e.biometricInfo.id is not null ";

        }else{
            sql = "select e.id ,e.employeeId, e.lastName,e.firstName,e.initials, st.name,s.level,s.step,m.name " +
                    "from "+ IppmsUtils.getEmployeeTableName(bc)+" e,  MdaInfo m, MdaDeptMap mdm,SalaryInfo s, SalaryType st  " +
                    "where  m.id = mdm.mdaInfo.id and e.mdaDeptMap.id = mdm.id and e.salaryInfo.id = s.id and s.salaryType.id = st.id " +
                    "and e.businessClientId = :pBizIdVar and e.biometricInfo.id is null ";
        }
        if(useMda)
            sql += "and m.id = :pMdaInfoId ";

        Query query = sessionFactory.getCurrentSession().createQuery(sql);

        if(useMda)
           query.setParameter("pMdaInfoId",pMdaId);
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)query.list();

        if (wRetVal.size() > 0)
        {
            int i = 0;
            Employee employee;
            for (Object[] o : wRetVal) {
                employee = new Employee();
                employee.setId((Long)o[i++]);
                employee.setEmployeeId((String)o[i++]);
                employee.setLastName((String)o[i++]);
                employee.setFirstName((String)o[i++]);
                employee.setInitials(IppmsUtils.treatNull(o[i++]));
                employee.setSalaryTypeName(o[i++] + " : "+o[i++] +"/"+ o[i++]);
                employee.setMdaName((String)o[i++]);
                if(verified){
                    employee.setLastModifier(o[i++] + " "+ o[i++]);
                    employee.setLastModTs((Timestamp)o[i++]);
                }
                wRetList.add(employee);
                i = 0;

            }
        }

        return  wRetList;

    }

    public int getNoOfRecords(BusinessCertificate businessCertificate, Long pMdaId) {
        boolean wUsingMda = IppmsUtils.isNotNull(pMdaId );
         String hqlQuery = "";

            hqlQuery = "select count(e.id) " +
                    "from "+IppmsUtils.getEmployeeTableName(businessCertificate)+" e, MdaDeptMap mdm, MdaInfo m " +
                    "where e.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = m.id and e.biometricInfo.id is null and e.statusIndicator = 0 and e.businessClientId = :pBizIdVar";
            if(wUsingMda)
                hqlQuery +=  " and m.id = :pMdaIdVar";


        Query query = this.sessionFactory.getCurrentSession() .createQuery(hqlQuery);
        query.setParameter("pBizIdVar", businessCertificate.getBusinessClientInstId());

        if(wUsingMda)
           query.setParameter("pMdaIdVar", pMdaId);
        int retVal = 0;

        List list = query.list();
        if ((list == null) || (list.isEmpty())) {
            return 0;
        }
        retVal = ((Long)list.get(0)).intValue();
        return retVal;

    }

}
