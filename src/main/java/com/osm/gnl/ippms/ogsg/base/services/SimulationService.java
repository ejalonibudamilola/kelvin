/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.base.dao.ISimulationDao;
import com.osm.gnl.ippms.ogsg.control.entities.EmployeeType;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.simulation.PayrollSimulationMasterBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationInfo;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationPaycheckBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.ltg.domain.LtgMasterBean;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.report.beans.RetMainBean;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service("simulationService")
@Repository
@Transactional(readOnly = true)
public class SimulationService {

    @Autowired
    private ISimulationDao simulationDao;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private GenericService genericService;

    public SimulationService() {

    }

    public RetMainBean populateAverages(final BusinessCertificate bcert, int pStartMonth, int pStartYear, int pEndMonth, int pEndYear, int pNoofMonths){
        return simulationDao.populateAverages(bcert, pStartMonth, pStartYear, pEndMonth, pEndYear, pNoofMonths);
    }

    public HashMap<Long, Long> getObjectsToApplyLtg(Long pLtgMasterInstId) {
        HashMap<Long, Long> wRetList = new HashMap<>();
        String wSql = "select a.mdaInfo.id,a.mdaInfo.name from AbmpBean a, MdaType m  where a.ltgMasterBean.id = :pPid "
                + "and a.mdaInfo.mdaType.id = m.id and a != null ";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wSql);

        query.setParameter("pPid", pLtgMasterInstId);

        ArrayList<Object[]> wRetVal = new ArrayList<>();

        wRetVal = (ArrayList)query.list();

        if ((wRetVal != null) && (wRetVal.size() > 0))
        {
            for (Object[] o : wRetVal) {
                if(!wRetList.containsKey(o[0])) {
                    wRetList.put((Long)o[0], (Long)o[0]);
                }
            }
        }
        return wRetList;
    }
    public List<SimulationPaycheckBean> getPayableEmployeesByDesignation(Long pMdaTypeId, Long pLtgMasterBeanId, BusinessCertificate bc, LocalDate pStartDate, LocalDate pEndDate)
    {
        ArrayList<Object[]> wRetVal = new ArrayList<>();
        List<SimulationPaycheckBean> wRetList = new ArrayList<>();
        String wSql = "";
        if(bc.isPensioner()){
            wSql = "select  h.birthDate,h.hireDate, e.id, e.salaryInfo.id,h.ltgLastPaid," +
                    " h.suspended,h.contractEndDate,h.pensionEndDate,h.expectedDateOfRetirement,h.pensionableInd,et.id,et.politicalInd,e.payApprInstId,"
                    + "a.id,a.mdaType.mdaTypeCode,h.monthlyPensionAmount from HiringInfo h, Pensioner e, " +
                    "MdaInfo a,MdaDeptMap adm, EmployeeType et,PaymentMethodInfo p " +
                    "where e.id = h.pensioner.id and (h.terminateInactive = 'N' or h.staffInd = 1 or " +
                    "(h.pensionEndDate >= :pBeginDate and h.pensionEndDate <= :pEndDate)) " +
                    "and e.mdaDeptMap.id = adm.id and adm.mdaInfo.id = a.id and e.employeeType.id =  et.id " +
                    "and p.pensioner.id = e.id  and e.businessClientId = :pBusId  "
                    + " and a.mdaType.id = :pMdaTypeIdVar ";
        }else{
            wSql = "select  h.birthDate,h.hireDate, e.id, e.salaryInfo.id,h.ltgLastPaid," +
                    " h.suspended,h.contractEndDate,h.terminateDate,h.expectedDateOfRetirement,h.pensionableInd,et.id,et.politicalInd,e.payApprInstId,"
                    + "a.id,a.mdaType.mdaTypeCode from HiringInfo h, Employee e, " +
                    "MdaInfo a,MdaDeptMap adm, EmployeeType et,PaymentMethodInfo p " +
                    "where e.id = h.employee.id and (h.terminateInactive = 'N' or h.staffInd = 1 or " +
                    "(h.terminateDate >= :pBeginDate and h.terminateDate <= :pEndDate)) " +
                    "and e.mdaDeptMap.id = adm.id and adm.mdaInfo.id = a.id and e.employeeType.id =  et.id " +
                    "and p.employee.id = e.id  and e.businessClientId = :pBusId  "
                    + " and a.mdaType.id = :pMdaTypeIdVar ";
        }




        Query query = this.sessionFactory.getCurrentSession().createQuery(wSql);

        query.setParameter("pMdaTypeIdVar", pMdaTypeId);
        query.setParameter("pBeginDate", pStartDate);
        query.setParameter("pEndDate", pEndDate);
        query.setParameter("pBusId",bc.getBusinessClientInstId() );

        wRetVal = (ArrayList<Object[]>)query.list();

        if (wRetVal.size() > 0)
        {
            int i = 0;
            for (Object[] o : wRetVal) {
                SimulationPaycheckBean s = new SimulationPaycheckBean();
                s.setBusinessClientId(bc.getBusinessClientInstId());
                s.setBirthDate((LocalDate) o[i++]);
                s.setHireDate((LocalDate) o[i++]);
                s.setEmployee(new Employee((Long)o[i++]));
                s.setSalaryInfoId((Long)o[i++]);
                Object wObj = o[i++];
                if(wObj != null)
                    s.setLtgLastPaid((LocalDate) wObj);
                else
                    s.setLtgLastPaid(null);
                s.setSuspended((Integer) o[i++] == 1);
                wObj = o[i++];
                if(wObj != null)
                    s.setContractEndDate((LocalDate) wObj);
                else
                    s.setContractEndDate(null);
                wObj = o[i++];
                if(wObj != null)
                    s.setTerminateDate((LocalDate) wObj);
                else
                    s.setTerminateDate(null);

                wObj = o[i++];
                if(wObj != null)
                    s.setExpectedDateOfRetirement((LocalDate) wObj);
                else
                    s.setExpectedDateOfRetirement(null);
                s.setPensionableInd((Integer)o[i++]);
                EmployeeType wEt = new EmployeeType((Long)o[i++],(Integer)o[i++],0,0);
                s.setPoliticalOfficeHolder(wEt.isPoliticalOfficeHolderType());
                s.setApprovedForPayroll(o[i++] != null);
                s.setMdaInfo(new MdaInfo((Long)o[i++]));

                s.setObjectInd((Integer)o[i++]);
                s.setLtgMasterBean(new LtgMasterBean(pLtgMasterBeanId));
                if(bc.isPensioner()){
                    s.setPensionerType(true);
                    s.setMonthlyPensionAmount((Double)o[i++]);
                }
                wRetList.add(s);
                i= 0;
            }

        }

        return wRetList;
    }

    public List<HiringInfo> loadPayableActiveHiringInfoByBusinessId(BusinessCertificate bc, MdaInfo mdaInfo) {

        ArrayList<Object[]> wRetVal = new ArrayList<>();
        List<HiringInfo> wRetList = new ArrayList<>();
        boolean setRest = false;

        String hqlQuery  ="select h.id, h.birthDate,h.hireDate,h.lastPayPeriod,h.currentPayPeriod,h.lastPayDate,e.id,"
                + " e.salaryInfo.id, e.firstName, e.lastName,h.contractStartDate,h.contractEndDate,h.contractExpiredInd,"
                + "e.mdaDeptMap.id,m.id,m.name,h.monthlyPensionAmount,h.suspended,h.staffInd,h.pensionableInd from HiringInfo h, MdaInfo m, MdaDeptMap mdm, "
                + ""+ IppmsUtils.getEmployeeTableName(bc) +" e where e.id = h."+bc.getEmployeeIdJoinStr()+" and h.terminateInactive = 'N' and m.id = mdm.mdaInfo.id and mdm.id = e.mdaDeptMap.id and "
                + "h is not null";

        if(mdaInfo != null && !mdaInfo.isNewEntity()) {
            hqlQuery += " and m.id = :pMdaInfoId";
            setRest = true;
        }

        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        if(mdaInfo != null && !mdaInfo.isNewEntity())
            query.setParameter("pMdaInfoId", mdaInfo.getId());
        wRetVal = (ArrayList)query.list();

        if (wRetVal.size() > 0)
        {
            for (Object[] o : wRetVal) {
                HiringInfo h = new HiringInfo();
                h.setId((Long)o[0]);
                h.setBusinessClientId(bc.getBusinessClientInstId());
                h.setBirthDate((LocalDate) o[1]);
                h.setHireDate((LocalDate)o[2]);
                h.setLastPayPeriod((String)o[3]);
                h.setCurrentPayPeriod((String)o[4]);
                h.setLastPayDate((LocalDate)o[5]);
                if (o[10] != null) {
                    h.setContractStaff(true);
                    h.setContractStartDate((LocalDate)o[10]);
                }

                if (o[11] != null) {
                    h.setContractEndDate((LocalDate)o[11]);
                }

                h.setContractExpiredInd(((Integer)o[12]));
                Employee e = new Employee((Long)o[6]);
                e.setFirstName((String)o[8]);
                e.setLastName((String)o[9]);
                e.setSalaryInfo(new SalaryInfo((Long)o[7]));
                e.setMdaDeptMap(new MdaDeptMap((Long)o[13]));
                e.getMdaDeptMap().setMdaInfo(new MdaInfo((Long)o[14], (String)o[15]));
                if(bc.isPensioner())
                    h.setMonthlyPensionAmount((Double)o[16]);
                if(setRest){
                    h.setSuspended((Integer)o[17]);
                    h.setStaffInd(((Integer)o[18]));
                    h.setPensionableInd(((Integer)o[19]));

                }
                h.setEmployee(e);
                wRetList.add(h);
            }

        }

        return wRetList;
    }

    public List<EmployeePayBean> loadEmployeePayBeanByParentIdFromDateToDate(BusinessCertificate bc, LocalDate fDate) {

        ArrayList<Object[]> wRetVal = new ArrayList<>();
        List wRetList = new ArrayList();

        String hqlQuery = "select p.taxesPaid,p.unionDues,p.nhf,p.totalPay,p.netPay,p.mdaDeptMap.id,mda.id,mda.name,mda.codeName" +
               " from "+IppmsUtils.getPaycheckTableName(bc)+" p, "+IppmsUtils.getEmployeeTableName(bc)+" e,MdaInfo mda, MdaDeptMap m "
                + "where e.id = p.employee.id  and p.runMonth = :pRunMonth " +
                "and p.runYear = :pRunYear and p.mdaDeptMap.id = m.id and m.mdaInfo.id = mda.id " +
                "and p.businessClientId = :pBusClientIdVar and p.netPay > 0";

        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("pRunMonth", fDate.getMonthValue());
        query.setParameter("pRunYear", fDate.getYear());
        query.setParameter("pBusClientIdVar", bc.getBusinessClientInstId());

        wRetVal = (ArrayList)query.list();

        if (wRetVal.size() > 0)
        {
            int i = 0;
            for (Object[] o : wRetVal) {

                EmployeePayBean p = new EmployeePayBean();

                p.setTaxesPaid(((Double)o[i++]));
                p.setMonthlyTax(p.getTaxesPaid());
                p.setUnionDues(((Double)o[i++]));
                p.setNhf(((Double)o[i++]));
                p.setTotalPay(((Double)o[i++]));
                p.setNetPay(((Double)o[i++]));

                p.setMdaDeptMap(new MdaDeptMap((Long)o[i++]));
                p.getMdaDeptMap().setMdaInfo(new MdaInfo((Long)o[i++],(String)o[i++], (String)o[i++]));


                wRetList.add(p);
                i = 0;

            }

        }

        return wRetList;

    }

    public List<SalaryInfo> loadSalaryInfoBySalaryScaleAndFilter(Long id, int level, int step, double monthlyBasicSalary) {
        List wRetList = new ArrayList();

        Criteria crit = sessionFactory.getCurrentSession().createCriteria(SalaryInfo.class);
        crit.add(Restrictions.eq("salaryType.id", id));
        crit.add(Restrictions.ge("level", Integer.valueOf(level)));
        crit.add(Restrictions.le("step", Integer.valueOf(step)));
        crit.add(Restrictions.gt("monthlyBasicSalary", Double.valueOf(monthlyBasicSalary)));
        crit.addOrder(Order.asc("level"));
        crit.addOrder(Order.asc("monthlyBasicSalary"));
        crit.setMaxResults(1);
        crit.setFetchSize(1);

        wRetList = crit.list();
        if (wRetList == null) {
            wRetList = new ArrayList();
        }

        return wRetList;
    }

    public List<SimulationInfo> loadAllSimulatedPayrollByParentIdAndMonthAndYear(Long pParentId, int pStartMonth, int pStartYear, BusinessCertificate bc)
    {
        String sql = "select s.id,s.payrollSimulationMasterBean.id,s.mdaDeptMap.id,m.id,m.name, m.codeName,s.netPay,s.nhf,s.monthlyTax,"
                + "s.developmentLevy,s.tws,s.unionDues,s.leaveTransportGrant from SimulationInfo s, MdaDeptMap mdm, MdaInfo m "
                + "where mdm.id = s.mdaDeptMap.id and mdm.mdaInfo.id = m.id and s.payrollSimulationMasterBean.businessClientId = :pBizId and "
                + "s.payrollSimulationMasterBean.id = :pValue1 and s.runMonth = :pValue2 and s.runYear = :pValue3 and s is not null";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameter("pValue1", pParentId);
        wQuery.setParameter("pValue2", pStartMonth);
        wQuery.setParameter("pValue3", pStartYear);
        wQuery.setParameter("pBizId", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal;
        ArrayList<SimulationInfo> wRetList = new ArrayList<>();

        wRetVal = (ArrayList<Object[]>)wQuery.list();

        if (wRetVal.size() > 0)
        {
            int i = 0;
            SimulationInfo s = null;
            for (Object[] o : wRetVal) {
                s = new SimulationInfo();
                s.setId((Long)o[i++]);
                s.setPayrollSimulationMasterBean(new PayrollSimulationMasterBean((Long)o[i++]));
                s.setMdaDeptMap(new MdaDeptMap((Long)o[i++]));
                s.getMdaDeptMap().setMdaInfo(new MdaInfo((Long)o[i++], (String)o[i++], (String)o[i++]));
                s.setNetPay(((Double)o[i++]));
                s.setNhf(((Double)o[i++]));
                s.setMonthlyTax(((Double)o[i++]));
                s.setDevelopmentLevy(((Double)o[i++]));
                s.setTws(((Double)o[i++]));
                s.setUnionDues(((Double)o[i++]));
                s.setLeaveTransportGrant(((Double)o[i++]));

                wRetList.add(s);
                i = 0;
            }

        }

        return wRetList;
    }
}
