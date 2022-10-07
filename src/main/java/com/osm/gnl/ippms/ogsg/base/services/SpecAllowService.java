/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckSpecAllowEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckSpecialAllowance;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpDeductMiniBean;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.*;

@Service("specAllowService")
@Repository
@Transactional
public class SpecAllowService {

    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public List<AbstractSpecialAllowanceEntity> loadToBePaidEmployeeSpecialAllowances(BusinessCertificate bc, LocalDate pStartDate, LocalDate pEndDate, boolean pRerun) {

        List wRetList = new ArrayList();

        ArrayList<Object[]> wRetVal;

        String hqlQuery = "";
                if(bc.isPensioner()){
                    if(!pRerun){
                        hqlQuery =  "select e.id,e.amount,k.id,l.id,e.startDate,e.endDate,p.id,p.name,p.percentageInd," +
                                "l.taxExemptInd,l.name,l.arrearsInd from "+IppmsUtils.getSpecialAllowanceInfoTableName(bc)+" e,HiringInfo h,Pensioner k," +
                                "SpecialAllowanceType l,PayTypes p, BusinessClient bc where e.pensioner.id = k.id and k.id = h.pensioner.id " +
                                "and l.id = e.specialAllowanceType.id and p.id = e.payTypes.id and bc.id = k.businessClientId and k.businessClientId = h.businessClientId " +
                                "and (h.terminateInactive = 'N' or (h.terminateDate >= :pStartDate and h.terminateDate <= :pEndDate)) " +
                                "and e.expire = 0 and bc.id = :pBizIdVar";
                    }else{
                        hqlQuery =  "select e.id,e.amount,k.id,l.id,e.startDate,e.endDate,p.id,p.name,p.percentageInd," +
                                "l.taxExemptInd,l.name,l.arrearsInd from "+IppmsUtils.getSpecialAllowanceInfoTableName(bc)+" e,HiringInfo h,Pensioner k," +
                                "SpecialAllowanceType l,PayTypes p, BusinessClient bc, PayrollRerun pr where e.pensioner.id = k.id and k.id = h.pensioner.id " +
                                "and l.id = e.specialAllowanceType.id and p.id = e.payTypes.id and bc.id = k.businessClientId and k.businessClientId = h.businessClientId " +
                                "and (h.terminateInactive = 'N' or (h.terminateDate >= :pStartDate and h.terminateDate <= :pEndDate)) " +
                                "and e.expire = 0 and bc.id = :pBizIdVar and h.id = pr.hiringInfo.id";
                    }

                }else{
                    if(!pRerun){
                        hqlQuery = "select e.id,e.amount,k.id,l.id,e.startDate,e.endDate,p.id,p.name,p.percentageInd," +
                                "l.taxExemptInd,l.name,l.arrearsInd from  "+IppmsUtils.getSpecialAllowanceInfoTableName(bc)+" e,HiringInfo h,Employee k," +
                                "SpecialAllowanceType l,PayTypes p, BusinessClient bc where e.employee.id = k.id and k.id = h.employee.id " +
                                "and l.id = e.specialAllowanceType.id and p.id = e.payTypes.id and bc.id = k.businessClientId and k.businessClientId = h.businessClientId " +
                                "and (h.terminateInactive = 'N' or (h.terminateDate >= :pStartDate and h.terminateDate <= :pEndDate)) " +
                                "and e.expire = 0 and bc.id = :pBizIdVar";
                    }else{
                        hqlQuery = "select e.id,e.amount,k.id,l.id,e.startDate,e.endDate,p.id,p.name,p.percentageInd," +
                                "l.taxExemptInd,l.name,l.arrearsInd from "+IppmsUtils.getSpecialAllowanceInfoTableName(bc)+" e,HiringInfo h,Employee k," +
                                "SpecialAllowanceType l,PayTypes p, BusinessClient bc, PayrollRerun pr where e.employee.id = k.id and k.id = h.employee.id " +
                                "and l.id = e.specialAllowanceType.id and p.id = e.payTypes.id and bc.id = k.businessClientId and k.businessClientId = h.businessClientId " +
                                "and (h.terminateInactive = 'N' or (h.terminateDate >= :pStartDate and h.terminateDate <= :pEndDate)) " +
                                "and e.expire = 0 and bc.id = :pBizIdVar and h.id = pr.hiringInfo.id";
                    }

                }



        Query query =sessionFactory.getCurrentSession().createQuery(hqlQuery);

        query.setParameter("pStartDate", pStartDate);
        query.setParameter("pEndDate", pEndDate);
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());
        wRetVal = (ArrayList)query.list();
        PayTypes p;

        if (wRetVal.size() > 0)
        {
            AbstractSpecialAllowanceEntity e;
            SpecialAllowanceType et;
            for (Object[] o : wRetVal) {
                 e = IppmsUtils.makeSpecialAllowanceInfoObject(bc);
                e.setId((Long)o[0]);
                e.setAmount(((Double)o[1]));
                e.makeParentObject((Long)o[2]);
                et = new SpecialAllowanceType((Long)o[3]);
                e.setStartDate((LocalDate) o[4]);
                e.setEndDate((LocalDate)o[5]);
                et.setTaxExemptInd(((Integer)o[9]) );
                e.setSpecialAllowanceType(et);


                p = new PayTypes((Long)o[6], (String)o[7], (Integer)o[8]);

                e.setPayTypes(p);
                e.setName((String)o[10]);
                e.getSpecialAllowanceType().setArrearsInd(11);
                wRetList.add(e);
            }

        }

        return wRetList;

    }

    public List<AbstractPaycheckSpecAllowEntity> loadPaycheckSpecialAllowanceByParentIdAndPayPeriod(
            Long pSpecAllowTypeId, int pRunMonth, int pRunYear, BusinessCertificate bc) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<AbstractPaycheckSpecAllowEntity> wRetList = new ArrayList<>();
        String wHql = "select p.amount,e.firstName,e.lastName, e.initials,e.employeeId, e.id, edi.id,edt.id, edt.name, edt.description, emp.id,mda.name,coalesce(emp.schoolInfo.id,0) "
                + "from "+IppmsUtils.getPaycheckSpecAllowTableName(bc)+" p, "+ IppmsUtils.getEmployeeTableName(bc) +" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,"
                + " "+IppmsUtils.getSpecialAllowanceInfoTableName(bc)+" edi, SpecialAllowanceType edt, MdaDeptMap mdm, MdaInfo mda where p.employeePayBean.id = emp.id and "
                + "emp.employee.id = e.id and edi.specialAllowanceType.id = edt.id and edi.id = p.specialAllowanceInfo.id and mdm.mdaInfo.id = mda.id "
                + "and emp.mdaDeptMap.id = mdm.id and p.runMonth = :pRunMonth and p.runYear = :pRunYear and p.amount > 0";


        if(pSpecAllowTypeId != null && pSpecAllowTypeId > 0)
            wHql += " and edt.id = :pSpecAllowTypeId ";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);
        if(pSpecAllowTypeId != null && pSpecAllowTypeId > 0)
            wQuery.setParameter("pSpecAllowTypeId", pSpecAllowTypeId);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if (wRetVal.size() > 0) {
            Map<Long,SchoolInfo> schoolInfoMap = this.genericService.loadObjectAsMapWithConditions(SchoolInfo.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId())),"id");
            AbstractSpecialAllowanceEntity specAllow;
            AbstractPaycheckSpecAllowEntity p;
            String pFirstName,pLastName,wName,wDesc,pInitials = null;
            Long schoolId;
            for (Object[] o : wRetVal) {
                 p =   IppmsUtils.makePaycheckSpecAllowObject(bc);
                p.setAmount(((Double) o[0]));

                  pFirstName = (String) o[1];
                  pLastName = (String) o[2];

                if (o[4] != null) {
                    pInitials = (String) o[3];
                }

                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));
                p.setEmployeeId((String) o[4]);
                p.setEmployeeInstId((Long)o[5]);
                specAllow= IppmsUtils.makeSpecialAllowanceInfoObject(bc);
                specAllow.setId((Long) o[6]);
                p.setSpecialAllowanceInfo(specAllow);
                p.getSpecialAllowanceInfo().setSpecialAllowanceType(new SpecialAllowanceType((Long) o[7]));
                  wName = (String)o[8];
                 wDesc = (String)o[9];
                p.getSpecialAllowanceInfo().setName(wName);
                p.getSpecialAllowanceInfo().setDescription( wDesc+" [ "+wName+" ]");
                p.setParentObjectInstId((Long)o[10]);
                p.setMdaName((String)o[11]);
                schoolId = (Long)o[12];
                if(schoolId > 0)
                    p.setMdaName(schoolInfoMap.get(schoolId).getName());

                wRetList.add(p);
            }

            return wRetList;
        }
        return wRetList;
    }


    public int getNoOfEmployeesWithSpecialAllowance(Long pTypeId, int pRunMonth, int pRunYear, BusinessCertificate bc){
        boolean wUseTypeId = (pTypeId != null && pTypeId > 0);
        String wHqlStr = "";

        if(wUseTypeId){
            wHqlStr = "select count(p.id) from "+IppmsUtils.getPaycheckSpecAllowTableName(bc)+" p,"+IppmsUtils.getSpecialAllowanceInfoTableName(bc)+" s," +
                    "SpecialAllowanceType st  where p.runMonth = :pRunMonthVar" +
                    " and p.runYear = :pRunYearVar and s.specialAllowanceType.id = st.id and" +
                    " p.specialAllowanceInfo.id = s.id and st.id = :pTypeIdVar ";
        }else{
            wHqlStr = "select count(p.id) from "+IppmsUtils.getPaycheckSpecAllowTableName(bc)+" p  where p.runMonth = :pRunMonthVar" +
                    " and p.runYear = :pRunYearVar ";
        }

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHqlStr);
        wQuery.setParameter("pRunMonthVar", pRunMonth);
        wQuery.setParameter("pRunYearVar", pRunYear);

        if(wUseTypeId)
            wQuery.setParameter("pTypeIdVar", pTypeId);


        List list = wQuery.list();

        if (list == null || list.size() == 0 || (list.size() == 1 && ((Long)list.get(0)).intValue() == 0))
            return 0;

        return ((Long)list.get(0)).intValue();

    }

    public double getTotalSpecialAllowancePaid(Long pSpecAllowId,int pRunMonth, int pRunYear, BusinessCertificate bc){
        String wHql = "select coalesce(sum(p.amount),0)  " +
                "from "+IppmsUtils.getPaycheckSpecAllowTableName(bc)+" p, "+IppmsUtils.getSpecialAllowanceInfoTableName(bc)+" edi,SpecialAllowanceType edt " +
                "  where p.runMonth = :pRunMonthVal and p.runYear = :pRunYearVal" +
                " and p.specialAllowanceInfo.id = edi.id" +
                " and edi.specialAllowanceType.id = edt.id ";

        if(pSpecAllowId != null && pSpecAllowId > 0){
            wHql += " and edt.id = :pGarnTypeIdVal";
        }

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonthVal", pRunMonth);
        wQuery.setParameter("pRunYearVal", pRunYear);
        if(pSpecAllowId != null && pSpecAllowId > 0){
            wQuery.setParameter("pGarnTypeIdVal", pSpecAllowId);
        }


        List  list = wQuery.list();

        if (list != null && !list.isEmpty() && list.get(0) != null ) {
            return (Double) list.get(0);
        }else{
            return 0.0D;
        }


    }

    public List<NamedEntity> loadSpecAllowTypeByPeriodAndFilter(int pRunMonth, int pRunYear, BusinessCertificate bc)
    {

        String wHqlStr = "select distinct st.id,st.name,st.description from SpecialAllowanceType st, "+IppmsUtils.getSpecialAllowanceInfoTableName(bc)+" si," +
                " "+IppmsUtils.getPaycheckSpecAllowTableName(bc)+" p where st.id = si.specialAllowanceType.id and si.id = p.specialAllowanceInfo.id" +
                " and p.runMonth = :pRunMonthVar and p.runYear = :pRunYearVar ";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHqlStr);

        wQuery.setParameter("pRunMonthVar", pRunMonth);
        wQuery.setParameter("pRunYearVar", pRunYear);

        List<Object[]> wRetList = wQuery.list();
        List<NamedEntity> wRetVal = new ArrayList<NamedEntity>();
        for(Object[] o : wRetList){
            NamedEntity wNEB = new NamedEntity();
            wNEB.setId((Long)o[0]);
            wNEB.setName(o[2] +" [ "+ o[1] +" ]");
            wRetVal.add(wNEB);
        }
        Comparator<NamedEntity> wComp = Comparator.comparing(NamedEntity::getName);
        Collections.sort(wRetVal,wComp);
        return wRetVal;
    }


    public List<EmpDeductMiniBean> loadSpecAllowanceAllowanceMiniBeanByFromDateToDate(
            int pRunMonth, int pRunYear, BusinessCertificate bc)
    {
        String wHql = "select p.amount,e.firstName,e.lastName, e.initials,e.employeeId, emp.mdaDeptMap.id, mda.id, mda.name,mda.codeName,edt.id, edt.name " +
                "from "+IppmsUtils.getPaycheckSpecAllowTableName(bc)+" p, "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,MdaDeptMap mdm, MdaInfo mda, " +
                ""+IppmsUtils.getSpecialAllowanceInfoTableName(bc)+" edi, SpecialAllowanceType edt where p.employeePayBean.id = emp.id and " +
                "emp.employee.id = e.id and edi.specialAllowanceType.id = edt.id and edi.id = p.specialAllowanceInfo.id " +
                "and p.runMonth = :pRunMonth and p.runYear = :pRunYear and emp.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = mda.id";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        if (wRetVal.size() > 0) {
            List wRetList = new ArrayList();
            for (Object[] o : wRetVal) {
                EmpDeductMiniBean p = new EmpDeductMiniBean();
                p.setCurrentDeduction(((Double)o[0]));

                String pFirstName = (String)o[1];
                String pLastName = (String)o[2];
                String pInitials = null;
                if (o[4] != null)
                {
                    pInitials = (String)o[3];
                }

                p.setName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials));
                p.setEmployeeId((String)o[4]);
                p.setMdaDeptMap(new MdaDeptMap((Long)o[5]));
                p.getMdaDeptMap().setMdaInfo(new MdaInfo((Long)o[6],(String)o[7],(String)o[8]));

                p.setId((Long)o[9]);
                p.setMode((String)o[10]); //Allowance Type Name
                wRetList.add(p);
            }

            return wRetList;
        }
        return new ArrayList();
    }


    public List<EmpDeductMiniBean> loadSingleSpecAllowanceAllowanceByFromDateToDate(Long pSpecialAllowanceType,
                                                                                    LocalDate pFromDate, BusinessCertificate bc)
    {
        String wHql = "select p.amount,e.firstName,e.lastName, e.initials,e.employeeId, emp.mdaDeptMap.id, mda.id,mda.name,mda.codeName,edt.id " +
                "from "+IppmsUtils.getPaycheckSpecAllowTableName(bc)+" p, "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,MdaDeptMap mdm, MdaInfo mda," +
                ""+IppmsUtils.getSpecialAllowanceInfoTableName(bc)+" edi, SpecialAllowanceType edt where p.employeePayBean.id = emp.id and " +
                "emp.employee.id = e.id and edi.specialAllowanceType.id = edt.id and edi.id = p.specialAllowanceInfo.id " +
                "and p.runMonth = :pRunMonth and p.runYear = :pRunYear and edt.id = :pSaid "
                + " and mdm.id = emp.mdaDeptMap.id and mdm.mdaInfo.id = mda.id";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonth", pFromDate.getMonthValue());
        wQuery.setParameter("pRunYear", pFromDate.getYear());
        wQuery.setParameter("pSaid", pSpecialAllowanceType);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        if (wRetVal.size() > 0) {
            List wRetList = new ArrayList();
            for (Object[] o : wRetVal) {
                EmpDeductMiniBean p = new EmpDeductMiniBean();
                p.setCurrentDeduction(((Double)o[0]));

                String pFirstName = (String)o[1];
                String pLastName = (String)o[2];
                String pInitials = null;
                if (o[4] != null)
                {
                    pInitials = (String)o[3];
                }

                p.setName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials));
                p.setEmployeeId((String)o[4]);
                p.setMdaDeptMap(new MdaDeptMap((Long)o[5]));
                p.getMdaDeptMap().setMdaInfo(new MdaInfo((Long)o[6],(String)o[7],(String)o[8] ));
                p.setId((Long)o[9]);
                wRetList.add(p);
            }

            return wRetList;
        }
        return new ArrayList();
    }

    public int getNoOfPendingSpecAllowances(Long pSpecAllowId,Long pEmpId,BusinessCertificate bc){

        String wSql = "select count(e.id) from "+ IppmsUtils.getPaycheckTableName(bc)+ " e, "+IppmsUtils.getPaycheckSpecAllowTableName(bc)+" d where"
                + " e.status ='P' and e.id = d.employeePayBean.id and d.specialAllowanceInfo.id = :pChildObjVar"
                + " and d.employee.id = :pEmpIdVar ";
        Query<Long> query = genericService.getCurrentSession().createQuery(wSql);

        query.setParameter("pChildObjVar", pSpecAllowId);
        query.setParameter("pEmpIdVar", pEmpId);
        Long retVal = query.uniqueResult();
        if(retVal == null)
            retVal = 0L;
        return retVal.intValue();
    }

    public int getTotalNoOfToBePaidSpecAllow (BusinessCertificate bc, LocalDate pStartDate, LocalDate pEndDate){

        int wRetVal = 0;

        String hqlQuery = "";
        if(bc.isPensioner()){
            hqlQuery = "select count(e.id) from "+IppmsUtils.getSpecialAllowanceInfoTableName(bc)+" e,HiringInfo h,Pensioner k," +
                    "SpecialAllowanceType l,PayTypes p, BusinessClient bc where e.pensioner.id = k.id and k.id = h.pensioner.id " +
                    "and l.id = e.specialAllowanceType.id and p.id = e.payTypes.id and bc.id = k.businessClientId and k.businessClientId = h.businessClientId " +
                    "and (h.terminateInactive = 'N' or (h.terminateDate >= :pStartDate and h.terminateDate <= :pEndDate)) " +
                    "and e.expire = 0 and bc.id = :pBizIdVar";
        }
        else{
            hqlQuery = "select count(e.id) from "+IppmsUtils.getSpecialAllowanceInfoTableName(bc)+" e,HiringInfo h,Employee k," +
                    "SpecialAllowanceType l,PayTypes p, BusinessClient bc where e.employee.id = k.id and k.id = h.employee.id " +
                    "and l.id = e.specialAllowanceType.id and p.id = e.payTypes.id and bc.id = k.businessClientId and k.businessClientId = h.businessClientId " +
                    "and (h.terminateInactive = 'N' or (h.terminateDate >= :pStartDate and h.terminateDate <= :pEndDate)) " +
                    "and e.expire = 0 and bc.id = :pBizIdVar";
        }



        Query query =sessionFactory.getCurrentSession().createQuery(hqlQuery);

        query.setParameter("pStartDate", pStartDate);
        query.setParameter("pEndDate", pEndDate);
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());
        List<Long> results = query.list();
        if ((results != null) && (!results.isEmpty())) {
            wRetVal = results.get(0).intValue();
        }

        return wRetVal;
    }

}
