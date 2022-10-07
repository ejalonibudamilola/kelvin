/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.LocalDateType;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service("excelReportService")
@Repository
@Transactional(readOnly = true)
public class ExcelReportService {


    private final GenericService genericService;
    private final SessionFactory sessionFactory;

    @Autowired
    public ExcelReportService(GenericService genericService, SessionFactory sessionFactory) {
        this.genericService = genericService;
        this.sessionFactory = sessionFactory;
    }

    public List<HiringInfo> loadYOBDataForExport(
            LocalDate fDate, LocalDate tDate,  BusinessCertificate bc)
    {

        List<HiringInfo> wRetList = new ArrayList<>();

        ArrayList<Object[]> wRetVal;
        String hqlQuery  = "select e.employee_inst_id AS eID, e.employee_id AS empId,e.last_name AS lName,e.first_name AS fName, coalesce(e.initials,'') AS pInit,h.birth_date AS bDate,s.salary_level AS sLevel," +
                    "s.salary_step AS sStep,st.name AS payGrp, m.name AS MDA" +
                    " from "+ IppmsUtils.getEmployeePGTableName(bc)+" e,ippms_hire_info h, ippms_salary_info s, ippms_salary_type st, ippms_mda_info m," +
                    "ippms_mda_dept_map mdm where e."+IppmsUtils.getEmpPGJoinStr(bc)+" = h."+IppmsUtils.getEmpPGJoinStr(bc)+
                    " and e.salary_info_inst_id = s.salary_info_inst_id and s.salary_type_inst_id = st.salary_type_inst_id and e.mda_dept_map_inst_id = " +
                    "mdm.mda_dept_map_inst_id and mdm.mda_inst_id = m.mda_inst_id and e.business_client_inst_id =  "+bc.getBusinessClientInstId() +
                    "and h.birth_date >= :pStartDate and h.birth_date <= :pEndDate and h.terminate_inactive = 'N' ";


        NativeQuery wQuery = this.sessionFactory.getCurrentSession().createNativeQuery(hqlQuery)
                .addScalar("eID", StandardBasicTypes.LONG)
                .addScalar("empId", StandardBasicTypes.STRING)
                .addScalar("lName",StandardBasicTypes.STRING)
                .addScalar("fName",StandardBasicTypes.STRING)
                .addScalar("pInit",StandardBasicTypes.STRING)
                .addScalar("bDate", LocalDateType.INSTANCE)
                .addScalar("sLevel", StandardBasicTypes.INTEGER)
                .addScalar("sStep", StandardBasicTypes.INTEGER)
                .addScalar("payGrp", StandardBasicTypes.STRING)
                .addScalar(bc.getMdaTitle(), StandardBasicTypes.STRING);


            wQuery.setParameter("pStartDate", fDate);
            wQuery.setParameter("pEndDate", tDate);

        wRetVal = (ArrayList)wQuery.list();

        if (wRetVal.size() > 0)
        {
            HiringInfo h;
            int i = 0;
            for (Object[] o : wRetVal) {

                h  = new HiringInfo();
                h.setId((Long)o[i++]);
                h.setBvnNo((String)o[i++]); //Employee ID
                h.setName(PayrollHRUtils.createDisplayName((String)o[i++],(String)o[i++],(String)o[i++]));
                h.setBirthDate((LocalDate)o[i++]);
                h.setOldLevelAndStep(PayrollUtils.makeLevelAndStep((Integer)o[i++],(Integer)o[i++]));
                h.setPayPeriodName((String)o[i++]); //Salary Type Name
                h.setProposedMda((String)o[i++]); //MDA Name..
                wRetList.add(h);
                i = 0;
            }

        }

        return wRetList;
    }
}
