package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollPayUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service("paymentService")
@Repository
@Transactional(readOnly = true)
@Slf4j
public class PaymentService {

    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }


    public List<Employee> getActiveEmployeesByPayGroup(int pStartRow,
                                                       int pEndRow, String pSortOrder, String pSortCriterion,
                                                       Long pSalaryTypeId, int pFromLevel, int pToLevel, BusinessCertificate bc)
    {
        List<Employee> wRetList = new ArrayList<Employee>();

        String wHql = "select e.id,e.employeeId,e.firstName,e.lastName,e.initials,e.mdaDeptMap.id," +
                "m.id,m.name,s.id,s.level,s.step,m.codeName from "+ IppmsUtils.getEmployeeTableName(bc)+" e, MdaInfo m, MdaDeptMap mda, " +
                "SalaryInfo s,SalaryType st where  "
                + " e.salaryInfo.id = s.id and s.salaryType.id = st.id and st.id = :pTypeId " +
                "and e.mdaDeptMap.id = mda.id and mda.mdaInfo.id = m.id and e.statusIndicator = 0";

        if(pFromLevel > 0 && pToLevel > 0)
            wHql += " and s.level >= :pFromLevel and s.level <= :pToLevel ";
        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        wQuery.setParameter("pTypeId",  pSalaryTypeId );
        if(pFromLevel > 0 && pToLevel > 0) {
            wQuery.setParameter("pFromLevel",  pFromLevel );
            wQuery.setParameter("pToLevel",  pToLevel );
        }
/*
        if (pStartRow > 0)
            wQuery.setFirstResult(pStartRow);
        wQuery.setMaxResults(pEndRow);*/

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0))
        {
            int i = 0;
            for (Object[] o : wRetVal) {

                Employee e = new Employee();

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

    public int getTotalNoOfEmployeesOnPayGroup(
            Long pSalaryTypeId, int pFromLevel, int pToLevel, BusinessCertificate bc)
    {
        int retVal = 0;

        String wHql = "select count(e.id) from "+IppmsUtils.getEmployeeTableName(bc)+" e, SalaryInfo s,SalaryType st " +
                "where  e.salaryInfo.id = s.id and s.salaryType.id = st.id and st.id = :pTypeId " +
                "and e.statusIndicator = 0 ";


        if(pFromLevel > 0 && pToLevel > 0)
            wHql += "and s.level >= :pFromLevel and s.level <= :pToLevel";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        wQuery.setParameter("pTypeId", pSalaryTypeId);
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


    public List<HiringInfo> getActiveEmployeesOnPayGroup(Long pTypeId,
                                                         int pFromLevel, int pToLevel, BusinessCertificate bc)
    {
        List<HiringInfo> wRetList = new ArrayList<HiringInfo>();

        String wHql = "select e.id,e.employeeId,e.firstName,e.lastName,e.initials," +
                "e.mdaDeptMap.id,m.id,m.name,m.codeName, " +
                "s.id,s.level,s.step,s.monthlyBasicSalary,h.id,h.birthDate,h.hireDate,h.gender," +
                "h.expectedDateOfRetirement from "+IppmsUtils.getEmployeeTableName(bc)+" e, SalaryInfo s,SalaryType st, " +
                "HiringInfo h, MdaInfo m where e.salaryInfo.id = s.id and h.employee.id = e.id " +
                "and s.salaryType.id = st.id and e.mdaDeptMap.mdaInfo.id = m.id and st.id = :pTypeId and e.statusIndicator = 0 " +
                "and s.level >= :pFromLevel and s.level <= :pToLevel ";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        wQuery.setParameter("pTypeId", pTypeId);
        wQuery.setParameter("pFromLevel", new Integer(pFromLevel).intValue());
        wQuery.setParameter("pToLevel", new Integer(pToLevel).intValue());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0))
        {
            Employee e = null;
            int i = 0;
            for (Object[] o : wRetVal) {
                e = new Employee();

                e.setId((Long)o[i++]);
                e.setEmployeeId((String)o[i++]);
                e.setFirstName((String)o[i++]);
                e.setLastName((String)o[i++]);
                e.setInitials(StringUtils.trimToEmpty((String)o[i++]));
                e.setMdaDeptMap(new MdaDeptMap((Long)o[i++]));
                e.getMdaDeptMap().setMdaInfo(new MdaInfo((Long)o[i++], (String)o[i++], (String)o[i++]));

                SalaryInfo s = new SalaryInfo((Long)o[i++]);
                s.setLevel(((Integer)o[i++]));
                s.setStep(((Integer)o[i++]));
                s.setMonthlyBasicSalary(PayrollPayUtils.convertDoubleToEpmStandard((Double)o[i++]));
                e.setSalaryInfo(s);
                HiringInfo h = new HiringInfo((Long)o[i++]);
                h.setBirthDate((LocalDate) o[i++]);
                h.setHireDate((LocalDate) o[i++]);
                h.setGender((String)o[i++]);
                h.setExpectedDateOfRetirement((LocalDate) o[i++]);
                h.setEmployee(e);
                wRetList.add(h);
                i = 0;
            }

        }

        return wRetList;
    }

    public List<HiringInfo> getActiveEmployeesOnPayScale(Long pSsc, int pFromLevel, int pToLevel, BusinessCertificate bc)
    {
        List<HiringInfo> wRetList = new ArrayList<>();

        String wHql = "select e.id,e.employeeId,e.firstName,e.lastName,e.initials,e.mdaDeptMap.id,"
                + "m.id,m.name,m.codeName, "
                + "s.id,s.level,s.step,s.monthlyBasicSalary,h.id,h.birthDate,h.hireDate,h.gender,"
                + "h.expectedDateOfRetirement from "+IppmsUtils.getEmployeeTableName(bc)+" e, SalaryInfo s,SalaryType sc, "
                + "HiringInfo h, MdaInfo m where  e.salaryInfo.id = s.id and e.mdaDeptMap.mdaInfo.id = m.id  and h.employee.id = e.id and s.salaryType.id = sc.id "
                + "and sc.id = :pScaleId and e.statusIndicator = 0  ";

        if((IppmsUtils.isNotNullAndGreaterThanZero(pFromLevel)) && (IppmsUtils.isNotNullAndGreaterThanZero(pToLevel))) {
            wHql += "and s.level >= :pFromLevel and s.level <= :pToLevel";
        }

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        wQuery.setParameter("pScaleId",pSsc);

        if((IppmsUtils.isNotNullAndGreaterThanZero(pFromLevel)) && (IppmsUtils.isNotNullAndGreaterThanZero(pToLevel))) {
            wQuery.setParameter("pFromLevel", new Integer(pFromLevel).intValue());
            wQuery.setParameter("pToLevel", new Integer(pToLevel).intValue());
        }

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0))
        {
            int i =0;
            for (Object[] o : wRetVal) {
                Employee e = new Employee();

                e.setId((Long)o[i++]);
                e.setEmployeeId((String)o[i++]);
                e.setFirstName((String)o[i++]);
                e.setLastName((String)o[i++]);
                e.setInitials(StringUtils.trimToEmpty((String)o[i++]));
                e.setMdaDeptMap(new MdaDeptMap((Long)o[i++]));
                e.getMdaDeptMap().setMdaInfo(new MdaInfo((Long)o[i++], (String)o[i++], (String)o[i++]));

                SalaryInfo s = new SalaryInfo((Long)o[i++]);
                s.setLevel(((Integer)o[i++]).intValue());
                s.setStep(((Integer)o[i++]).intValue());
                s.setMonthlyBasicSalary(PayrollPayUtils.convertDoubleToEpmStandard((Double)o[i++]));
                e.setSalaryInfo(s);
                HiringInfo h = new HiringInfo((Long)o[i++]);
                h.setBirthDate((LocalDate)o[i++]);
                h.setHireDate((LocalDate) o[i++]);
                h.setGender((String)o[i++]);
                h.setExpectedDateOfRetirement((LocalDate) o[i++]);
                h.setEmployee(e);
                wRetList.add(h);
                i = 0;
            }

        }

        return wRetList;
    }

}
