/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service("paginationService")
@Repository
@Transactional(readOnly = true)
public class PaginationService {
    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public List<HiringInfo> getActiveEmployeesByObjectAndCode(BusinessCertificate businessCertificate,int pStartRow, int pEndRow, String sortOrder, String sortCriterion, Long pMid) {

        ArrayList<Object[]> wRetVal = new ArrayList<Object[]>();
        List<HiringInfo> wRetList = new ArrayList<HiringInfo>();

        String wSql = "";

        wSql = "select e.id,e.firstName,e.initials,e.lastName,s.level,s.step,sc.name,e.employeeId,h.birthDate, h.hireDate,h.expectedDateOfRetirement, h.pensionStartDate "
                + "from "+ IppmsUtils.getEmployeeTableName(businessCertificate) +" e, HiringInfo h, MdaDeptMap adm, MdaInfo a,SalaryInfo s, SalaryType sc "
                + "where e.id = h."+businessCertificate.getEmployeeIdJoinStr()+" and e.mdaDeptMap.id = adm.id and adm.mdaInfo.id = a.id and e.statusIndicator = 0 "
                + "and e.salaryInfo.id = s.id and s.salaryType.id = sc.id and a.id = :pObjId";



        Query query = sessionFactory.getCurrentSession().createQuery(wSql);

        query.setParameter("pObjId", pMid);

//        if (pStartRow > 0)
//            query.setFirstResult(pStartRow);
//        query.setMaxResults(pEndRow);

        wRetVal = (ArrayList<Object[]>)query.list();

        if (wRetVal.size() > 0)
        {
            for (Object[] o : wRetVal) {
                Employee s = new Employee();
                s.setId((Long)o[0]);
                s.setFirstName((String)o[1]);
                s.setInitials(IppmsUtils.treatNull((String)o[2]));
                s.setLastName((String)o[3]);
                String level = Integer.toString(((Integer)o[4]).intValue());
                String step = Integer.toString(((Integer)o[5]).intValue());
                if (step.length() < 2) {
                    step = "0" + step;
                }
                s.setLevelStepStr(level + "." + step);
                s.setSalaryScaleName((String)o[6]);
                s.setEmployeeId((String)o[7]);
                HiringInfo h = new HiringInfo();
                h.setBirthDate((LocalDate) o[8]);
                h.setHireDate((LocalDate) o[9]);
                h.setExpectedDateOfRetirement((LocalDate) o[10]);
                h.setPensionStartDate((LocalDate) o[11]);
                h.setEmployee(s);
                wRetList.add(h);
            }

        }

        return wRetList;

    }

    public int getTotalNoOfActiveEmployeesByObjectAndCode(BusinessCertificate businessCertificate, Long pMid) {

        String wSql = "select count(e.id) from "+IppmsUtils.getEmployeeTableName(businessCertificate)+" e, MdaDeptMap adm, MdaInfo a,SalaryInfo s, SalaryType sc "
                + "where e.mdaDeptMap.id = adm.id and adm.mdaInfo.id = a.id and e.statusIndicator = 0 "
                + "and e.salaryInfo.id = s.id and s.salaryType.id = sc.id and a.id = :pObjId";

        Query query = sessionFactory.getCurrentSession().createQuery(wSql);

        query.setParameter("pObjId", pMid);

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
    public int getTotalNoOfActiveEmployeesByGenderObjectAndCode(BusinessCertificate bc, String pGenderCode, Long pObjectId)
    {
        String wSql   = "select count(e.id) from "+IppmsUtils.getEmployeeTableName(bc)+" e, MdaDeptMap adm, MdaInfo a,HiringInfo h "
                + "where e.mdaDeptMap.id = adm.id and adm.mdaInfo.id = a.id and e.statusIndicator = 0 and h."+bc.getEmployeeIdJoinStr()+" = e.id "
                + "and a.id = :pObjId and h.gender = :pSex";



        Query query = this.sessionFactory.getCurrentSession().createQuery(wSql);

        query.setParameter("pObjId", pObjectId);
        query.setParameter("pSex", pGenderCode);

        List list = query.list();

        if (list == null || list.isEmpty() || list.size() == 0 )
            return 0;

        return ((Long)list.get(0)).intValue();
    }
}
