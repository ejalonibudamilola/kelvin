package com.osm.gnl.ippms.ogsg.controllers.report.service;

import com.osm.gnl.ippms.ogsg.domain.report.VariationReportBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Repository
@Transactional(readOnly = true)
@Slf4j
public class VariationService {

    private final SessionFactory sessionFactory;

    public VariationService(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    public List<VariationReportBean> loadNewEmployeesForMonthlyVariationReturnList(
            LocalDate startCal, boolean pSchoolsOnly, BusinessCertificate bc) throws Exception
    {


        String wPayPeriod = PayrollUtils.makeAuditPayPeriod(startCal.getMonthValue(), startCal.getYear());
        LocalDate currMonthStart = startCal;


        List<VariationReportBean> wRetList = new ArrayList<VariationReportBean>();

        String wHql = "select e.id, e.employeeId, e.firstName, e.lastName, e.initials, a.lastModTs," +
                "a.auditTimeStamp,s.salaryType.name, s.level, s.step, "
                + " m.name, a.user.firstName||' '||a.user.lastName "
                + " from "+ IppmsUtils.getEmployeeTableName(bc) +" e, SalaryInfo s, "+IppmsUtils.getEmployeeAuditTable(bc)+" a, MdaInfo m "
                + " where e.id = a.employee.id and a.salaryInfo.id = s.id and a.mdaInfo.id = m.id"
                + " and a.auditPayPeriod = :pPayPeriod and a.employee.id = e.id and a.auditActionType = :pNewEmpVar" +
                " and e.businessClientId = :pBizIdVar";



        wHql +=  " order by m.name, e.lastName, e.firstName,a.lastModTs";
        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pPayPeriod", wPayPeriod);
        query.setParameter("pNewEmpVar", "I");
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());


        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                VariationReportBean p = new VariationReportBean();
                p.setEmployeeInstId((Long) o[0]);
                p.setEmployeeId((String) o[1]);
                String pFirstName = ((String) o[2]);
                String pLastName = ((String) o[3]);
                String pInitials = null;
                if (o[4] != null) {
                    pInitials = ((String) o[4]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));
                p.setCreatedDateAndAuditTime(PayrollHRUtils.getFullDateFormat().format((LocalDate) o[5])+" "+ o[6]);


                p.setGradeStep(o[7] + ": "
                        + o[8] + "/ "
                        + PayrollUtils.formatStep((Integer) o[9]));


                p.setReportName(" New Staff Report");
                p.setPeriod(currMonthStart);


                p.setAgency(((String) o[10]).trim());
                p.setUserName((String) o[11]);

                wRetList.add(p);
            }
        }
        if (wRetList.size() > 0) {
            Collections.sort(wRetList, Comparator.comparing(VariationReportBean::getAgency));

        }
        return wRetList;
    }


    public List<VariationReportBean> loadPromotionLogForMonthlyVariationReturnList(
            LocalDate startCal, boolean pSchoolsOnly, BusinessCertificate bc) throws Exception
    {



        LocalDate currMonthStart = startCal;
        // Date currMonthEnd = endCal.getTime();


        List<VariationReportBean> wRetList = new ArrayList<VariationReportBean>();

        String wq = "select e.id, e.employeeId,s.level, s.step, s0.salaryType.name, s0.level, s0.step, "
                        + "e.firstName, e.lastName, e.initials, m.name,pa.lastModTs,pa.auditTime , pa.user.firstName||' '||pa.user.lastName "
                        + "from "+IppmsUtils.getEmployeeTableName(bc)+" e,SalaryInfo s,SalaryInfo s0,"+IppmsUtils.getPromotionAuditTable(bc)+" pa, User l, MdaInfo m"
                        + " where  e.id = pa.employee.id and pa.oldSalaryInfo.id = s.id and m.id = pa.mdaInfo.id"
                        + " and pa.salaryInfo.id = s0.id "
                        + " and pa.user.id = l.id and pa.auditPayPeriod = :pPayPeriodVar "
                        + " and e.businessClientId = pa.businessClientId and e.businessClientId = :pBizIdVar"
                        + " order by m.name,e.lastName ,e.firstName ";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wq);

        query.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(startCal.getMonthValue(), startCal.getYear()));
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();

        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                VariationReportBean p = new VariationReportBean();
                p.setEmployeeInstId((Long) o[0]);
                p.setEmployeeId((String) o[1]);
                p.setOldValue(o[2] + "/ "
                        + PayrollUtils.formatStep((Integer) o[3]));
                p.setPayGroup((String) o[4]);
                p.setNewValue(o[5] + "/ "
                        + PayrollUtils.formatStep((Integer) o[6]));

                String pFirstName = ((String) o[7]);
                String pLastName = ((String) o[8]);
                String pInitials = null;
                Object wObj = o[9];
                if (wObj != null) {
                    pInitials = ((String) wObj);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));


                p.setAgency((String) o[10]);
                p.setCreatedDateAndAuditTime(PayrollHRUtils.getFullDateFormat().format((LocalDate)o[11])+" "+ o[12]);
                p.setUserName((String)o[13]);

                p.setPeriod(currMonthStart);

                wRetList.add(p);
            }

            if (wRetList.size() > 0) {
                Collections.sort(wRetList,
                        Comparator.comparing(VariationReportBean::getAgency));
            }
        }
        return wRetList;
    }


    public List<VariationReportBean> loadDeductionLogForMonthlyVariationReturnList(
            LocalDate startCal, boolean pSchoolsOnly, BusinessCertificate bc) throws Exception
    {

        LocalDate currMonthStart = startCal;

        List<VariationReportBean> wRetList = new ArrayList<VariationReportBean>();

        String wHql = "select e.id, e.employeeId, e.firstName, e.lastName, e.initials, s.level, s.step, "
                + "p.lastModTs,p.auditTime, m.name, p.user.firstName||' '||p.user.lastName,p.oldValue,p.newValue,gt.description "
                + " from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getDeductionAuditTable(bc)+" p, SalaryInfo s, EmpDeductionType gt, MdaInfo m "
                + " where e.id = p.employee.id and p.salaryInfo.id = s.id and m.id = p.mdaInfo.id "
                + "and p.deductionType.id = gt.id  "
                + "and p.auditPayPeriod = :pPayPeriodVar and "
                + "e.businessClientId = p.businessClientId and e.businessClientId = :pBizIdVar"
                + " order by  m.name, e.lastName, e.firstName,p.lastModTs";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(startCal.getMonthValue(), startCal.getYear()));
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                VariationReportBean p = new VariationReportBean();
                p.setEmployeeInstId((Long) o[0]);
                p.setEmployeeId((String) o[1]);
                String pFirstName = ((String) o[2]);
                String pLastName = ((String) o[3]);
                String pInitials = null;
                if (o[4] != null) {
                    pInitials = ((String) o[4]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));
                p.setGradeStep(o[5] + "/ "
                        + PayrollUtils.formatStep((Integer) o[6]));
                p.setCreatedDateAndAuditTime(PayrollHRUtils.getFullDateFormat().format((LocalDate) o[7])+" "+ o[8]);
                p.setAgency(((String) o[9]).trim());
                p.setUserName((String) o[10]);
                p.setOldValue((String) o[11]);
                p.setNewValue((String)o[12]);
                p.setLoanCode((String)o[13]);
                p.setReportName(" Deductions");
                p.setPeriod(currMonthStart);


                wRetList.add(p);
            }
        }
        if (wRetList.size() > 0) {
            Collections.sort(wRetList, Comparator.comparing(VariationReportBean::getAgency));

        }
        return wRetList;
    }

    public List<VariationReportBean> loadReabsorptionLogForMothlyVariationReturnList(BusinessCertificate bc,
            LocalDate startCal, boolean pSchoolsOnly) throws Exception
    {

        LocalDate currMonthStart = startCal;

        HashMap<Long,Long> wFilterBean = this.makeAbsorptionFilterBeans(bc,startCal);

        List<VariationReportBean> wRetList = new ArrayList<VariationReportBean>();
        String wHql = "select r.id,e.id, e.employeeId,e.firstName,e.lastName,"
                + " e.initials, r.salaryInfo.salaryType.name, r.salaryInfo.level, r.salaryInfo.step,"
                + " r.absorptionDate, r.suspensionDate, r.mdaInfo.name, r.user.firstName||' '||r.user.lastName "
                + " from AbsorptionLog r, "+IppmsUtils.getEmployeeTableName(bc)+" e "
                + " where r.auditPayPeriod = :pPayPeriodVar and r."+bc.getEmployeeIdJoinStr()+" = e.id and r.businessClientId = e.businessClientId and r.businessClientId = :pBizIdVar"
                + " group by r.id, e.id, e.employeeId, e.firstName,e.lastName, e.initials, r.salaryInfo.salaryType.name, r.salaryInfo.level, r.salaryInfo.step,"
                + " r.absorptionDate, r.suspensionDate, r.mdaInfo.name, r.user.firstName||' '||r.user.lastName "
                + " order by r.mdaInfo.name, e.lastName, e.firstName";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(startCal.getMonthValue(), startCal.getYear()));
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                VariationReportBean p = new VariationReportBean();
                int i = 0;
                p.setId((Long)o[i++]);
                p.setEmployeeInstId((Long) o[i++]);
                p.setEmployeeId((String) o[i++]);
                String pFirstName = ((String) o[i++]);
                String pLastName = ((String) o[i++]);
                String pInitials = null;
                Object wInit = o[i++];
                if (wInit != null) {
                    pInitials = ((String) wInit);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));
                p.setPayGroup((String) o[i++]);
                p.setGradeStep(o[i++] + "/ "
                        + PayrollUtils.formatStep((Integer) o[i++]));

                p.setAbsorbedDate(PayrollHRUtils.getFullDateFormat().format((LocalDate) o[i++]));
                p.setSuspendedDate(PayrollHRUtils.getFullDateFormat().format((LocalDate) o[i++]));
                p.setAgency((String) o[i++]);
                p.setUserName((String) o[i++]);
                p.setReportName(" Reabsorption Report");
                p.setDateName("Reabsorbed Date");
                p.setOppositeDateName("Suspension Date");
                p.setPeriod(currMonthStart);
                if(wFilterBean.containsKey(p.getId()))
                    wRetList.add(p);
            }
        }
        if (wRetList.size() > 0) {
            Collections.sort(wRetList, Comparator.comparing(VariationReportBean::getAgency));

        }

        return wRetList;
    }

    private HashMap<Long, Long> makeAbsorptionFilterBeans(BusinessCertificate bc,
            LocalDate pStartCal) {

        HashMap<Long, Long> wRetList = new HashMap<>();
        String wHql = "select max(r.id),e.id"
                + " from AbsorptionLog r, "+IppmsUtils.getEmployeeTableName(bc)+" e "
                + " where r.auditPayPeriod = :pPayPeriodVar and r.businessClientId = e.businessClientId and r."+bc.getEmployeeIdJoinStr()+" = e.id and r.businessClientId = :pBizIdVar"
                + " group by e.id";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(pStartCal.getMonthValue(),pStartCal.getYear()));
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {

                wRetList.put((Long)o[0], (Long)o[0]);

            }
        }

        return wRetList;
    }

    public List<VariationReportBean> loadReinstatementLogForMonthlyVariationReturnList(
            LocalDate startCal, boolean pSchoolsOnly, BusinessCertificate bc) throws Exception
    {




        LocalDate currMonthStart = startCal;


        List<VariationReportBean> wRetList = new ArrayList<VariationReportBean>();
        String wHql = "select e.id, e.employeeId, e.firstName,e.lastName, e.initials, st.name, s.level, s.step, "
                + "r.reinstatementDate, r.terminationDate, m.name, l.username"
                + " from ReinstatementLog r, SalaryInfo s, SalaryType st, "+IppmsUtils.getEmployeeTableName(bc)+" e, User l, MdaInfo m "
                + " where r.auditPayPeriod = :pPayPeriodVar"
                + " and r."+bc.getEmployeeIdJoinStr()+" = e.id and m.id = r.mdaInfo.id"
                + " and s.salaryType.id = st.id"
                + " and r.user.id = l.id "
                + " and r.salaryInfo.id = s.id and r.businessClientId = e.businessClientId and r.businessClientId = :pBizIdVar"
                + " order by m.name,e.lastName,e.firstName";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(startCal.getMonthValue(), startCal.getYear()));
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());


        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                VariationReportBean p = new VariationReportBean();
                p.setEmployeeInstId((Long) o[0]);
                p.setEmployeeId((String) o[1]);
                String pFirstName = ((String) o[2]);
                String pLastName = ((String) o[3]);
                String pInitials = null;
                if (o[4] != null) {
                    pInitials = ((String) o[4]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));
                p.setPayGroup((String) o[5]);
                p.setGradeStep(o[6] + "/ "
                        + PayrollUtils.formatStep((Integer) o[7]));

                p.setAbsorbedDate(PayrollHRUtils.getFullDateFormat().format((LocalDate) o[8]));
                p.setSuspendedDate(PayrollHRUtils.getFullDateFormat().format((LocalDate) o[9]));
                p.setAgency(((String) o[10]).trim());
                p.setUserName((String) o[11]);
                p.setReportName(" Reinstatement Report");
                p.setDateName("Reinstated Date");
                p.setOppositeDateName("Termination Date");
                p.setPeriod(currMonthStart);
                wRetList.add(p);
            }
        }



        if (wRetList.size() > 0) {
            Collections.sort(wRetList, Comparator.comparing(VariationReportBean::getAgency));

        }

        return wRetList;
    }

    public List<VariationReportBean> loadAllowancePaidForMonthlyVariationReturnList(LocalDate startCal, boolean pSchoolsOnly,
                                                                                    BusinessCertificate bc) throws Exception
    {


        LocalDate currMonthStart = startCal;


        List<VariationReportBean> wRetList = new ArrayList<VariationReportBean>();
        String wHql = "select e.id, e.employeeId, e.firstName,e.lastName, e.initials, "
                + " sai.description,s.oldValue, s.newValue, m.name, s.user.firstName||' '||s.user.lastName,s.lastModTs,s.auditTimeStamp"
                + " from "+IppmsUtils.getSpecAllowAuditTable(bc)+" s, "+IppmsUtils.getSpecialAllowanceInfoTableName(bc)+" sai , "+IppmsUtils.getEmployeeTableName(bc)+" e, MdaInfo m "
                + " where s.auditPayPeriod = :pPayPeriodVar and s.specialAllowanceInfo.id = sai.id and m.id = s.mdaInfo.id"
                + " and s."+bc.getEmployeeIdJoinStr()+" = e.id and s.businessClientId = e.businessClientId and s.businessClientId = :pBizIdVar"
                + " order by m.name,e.lastName,e.firstName,s.lastModTs";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);

        query.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(startCal.getMonthValue(), startCal.getYear()));
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());


        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                VariationReportBean p = new VariationReportBean();
                p.setEmployeeInstId((Long) o[0]);
                p.setEmployeeId((String) o[1]);
                String pFirstName = ((String) o[2]);
                String pLastName = ((String) o[3]);
                String pInitials = null;
                if (o[4] != null) {
                    pInitials = ((String) o[4]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));
                p.setLoanCode((String) o[5]);
                p.setOldValue(((String)o[6]).trim());
                p.setNewValue(((String)o[7]).trim());

                p.setAgency(((String)o[8]).trim());
                p.setUserName((String) o[9]);
                p.setCreatedDateAndAuditTime(PayrollHRUtils.getFullDateFormat().format((LocalDate)o[10])+" "+ o[11]);
                p.setReportName(" Spec. Allow. Report");

                p.setPeriod(currMonthStart);
                wRetList.add(p);
            }
        }



        if (wRetList.size() > 0) {
            Collections.sort(wRetList, Comparator.comparing(VariationReportBean::getAgency));

        }
        return wRetList;
    }



    public List<VariationReportBean> loadTerminatedEmployeesForMonthlyVariationReturnList(
            LocalDate startCal, boolean pSchoolsOnly, BusinessCertificate bc) throws Exception
    {



        LocalDate currMonthStart = startCal;

        List<VariationReportBean> wRetList = new ArrayList<>();

        String wHql = "";
        if(bc.isPensioner()){

            wHql = "select max(t.id),e.id, e.employeeId, e.firstName, e.lastName, e.initials,t.terminationDate,t.auditTime , st.name, s.level, s.step,  "
                    + " m.name,tr.name,l.firstName||' '||l.lastName"
                    + " from TerminationLog t,Pensioner e, TerminateReason tr, SalaryInfo s,SalaryType st,User l, MdaInfo m "
                    + " where t.pensioner.id = e.id and t.salaryInfo.id = s.id and t.user.id = l.id "
                    + " and t.auditPayPeriod = :pPayPeriodVar and t.terminateReason.id = tr.id and s.salaryType.id = st.id and t.businessClientId = :pBizIdVar "
                    + " group by e.id, e.employeeId, e.firstName, e.lastName, e.initials,t.terminationDate,t.auditTime , st.name, s.level, s.step,"
                    + " m.name,tr.name,l.firstName||' '||l.lastName"
                    + " order by  m.name, e.lastName,e.firstName,t.terminationDate";
        }else {


            wHql = "select max(t.id),e.id, e.employeeId, e.firstName, e.lastName, e.initials,t.terminationDate,t.auditTime , st.name, s.level, s.step,  "
                    + " m.name,tr.name,l.firstName||' '||l.lastName"
                    + " from TerminationLog t,Employee e, TerminateReason tr, SalaryInfo s,SalaryType st,User l, MdaInfo m "
                    + " where t.employee.id = e.id and t.salaryInfo.id = s.id and t.user.id = l.id "
                    + " and t.auditPayPeriod = :pPayPeriodVar and t.terminateReason.id = tr.id and s.salaryType.id = st.id  and t.businessClientId = :pBizIdVar"
                    + " group by e.id, e.employeeId, e.firstName, e.lastName, e.initials,t.terminationDate,t.auditTime , st.name, s.level, s.step,"
                    + " m.name,tr.name,l.firstName||' '||l.lastName"
                    + " order by  m.name, e.lastName,e.firstName,t.terminationDate";
        }

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(startCal.getMonthValue(), startCal.getYear()));
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());


        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                VariationReportBean p = new VariationReportBean();
                p.setEmployeeInstId((Long) o[1]);
                p.setEmployeeId((String) o[2]);
                String pFirstName = ((String) o[3]);
                String pLastName = ((String) o[4]);
                String pInitials = null;
                if (o[5] != null) {
                    pInitials = ((String) o[5]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));
                p.setSuspendedDate(PayrollHRUtils.getFullDateFormat().format((LocalDate) o[6])+" "+ o[7]);
                p.setTerminateDate((LocalDate) o[6]);

                p.setPayGroup((String) o[8]);
                p.setGradeStep(o[9] + "/ "
                        + PayrollUtils.formatStep((Integer) o[10]));
                p.setAgency(((String) o[11]).trim());
                p.setTermReason((String) o[12]);
                p.setUserName((String)o[13]);
                p.setPeriod(currMonthStart);

                wRetList.add(p);
            }
        }

        //-- Now get the Absorption Log and Reinstatement Log for the month.
        //HashMap<Integer,Calendar> wAbLog = this.loadAbsorbedEmpByPayPeriod(PayrollUtils.makeAuditPayPeriod(startCal.get(Calendar.MONTH), startCal.get(Calendar.YEAR)));
        HashMap<Long,LocalDate> wReinstateLog = this.loadReinstatementsByPayPeriod(bc,PayrollUtils.makeAuditPayPeriod(startCal.getMonthValue(), startCal.getYear()));

        List<VariationReportBean> wActRetList = new ArrayList<VariationReportBean>();
        LocalDate wDate = null;
        for(VariationReportBean v : wRetList){
            if(wReinstateLog.containsKey(v.getEmployeeInstId())){
                //Find out if the date is before
                wDate = LocalDate.now();
                wDate = v.getTerminateDate();
                if(wDate.isAfter(wReinstateLog.get(v.getEmployeeInstId()))){
                    wActRetList.add(v);
                }else{
                    continue;
                }

            }else{
                wActRetList.add(v);
            }
        }

        if (wActRetList.size() > 0) {
            Collections.sort(wRetList, Comparator.comparing(VariationReportBean::getAgency));

        }

        return wActRetList;
    }

    private HashMap<Long, LocalDate> loadReinstatementsByPayPeriod(BusinessCertificate bc,
            String pPayPeriod) {

        String wHql = "select max(r.id),r."+bc.getEmployeeIdJoinStr()+",r.reinstatementDate from ReinstatementLog r " +
                "where r.auditPayPeriod = :pPayPeriodVar and r.businessClientId = :pBizIdVar " +
                "group by r."+bc.getEmployeeIdJoinStr()+", r.reinstatementDate ";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pPayPeriodVar", pPayPeriod);
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        HashMap<Long,LocalDate> wRetMap = new HashMap<Long,LocalDate>();

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {
            for(Object[] o : wRetVal){
                LocalDate wCal = LocalDate.now();
                wCal = (LocalDate)o[2];
                wRetMap.put((Long)o[1], wCal);
            }
        }

        return wRetMap;
    }

    public List<VariationReportBean> loadSuspensionLogForMonthlyVariationReturnList(
            LocalDate startCal, boolean pSchoolsOnly, BusinessCertificate bc) throws Exception
    {

        LocalDate currMonthStart = startCal;


        HashMap<Long,Long> wFilterBean = this.makeSuspensionFilterBeans(bc,startCal);
        //START!!!
        List<VariationReportBean> wRetList = new ArrayList<VariationReportBean>();
        String wHql = "select r.id,e.id, e.employeeId, e.firstName,e.lastName, e.initials, st.name," +
                " s.level, s.step, r.suspensionDate, l.firstName,l.lastName,"
                + " m.name, sc.name"
                + " from SuspensionLog r, SalaryInfo s, "+IppmsUtils.getEmployeeTableName(bc)+" e, SalaryType st, User l, MdaInfo m, SuspensionType sc "
                + " where r.auditPayPeriod = :pPayPeriodVar"
                + " and r."+bc.getEmployeeIdJoinStr()+"= e.id and m.id = r.mdaInfo.id and sc.id = r.suspensionType.id"
                + " and r.user.username = l.username"
                + " and s.salaryType.id = st.id and r.salaryInfo.id = s.id  and r.businessClientId = :pBizIdVar "
                + " order by m.name, e.lastName,e.firstName,r.suspensionDate";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(startCal.getMonthValue(),startCal.getYear()));
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());


        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                VariationReportBean p = new VariationReportBean();
                int i = 0;
                p.setId((Long)o[i++]);
                p.setEmployeeInstId((Long) o[i++]);
                p.setEmployeeId((String) o[i++]);
                String pFirstName = ((String) o[i++]);
                String pLastName = ((String) o[i++]);
                String pInitials = null;
                Object wObj = o[i++];
                if (wObj != null) {
                    pInitials = ((String) wObj);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));
                p.setPayGroup((String) o[i++]);
                p.setGradeStep(o[i++] + "/ "
                        + PayrollUtils.formatStep((Integer) o[i++]));
                LocalDate suspDate = (LocalDate)o[i++];
                p.setSuspendedDate(PayrollBeanUtils.getDateAsString(suspDate));
                p.setSuspensionDate(suspDate);
                p.setUserName(o[i++] +" "+ o[i++]);

                p.setAgency(((String) o[i++]).trim());
                p.setReason(((String) o[i++]).trim());
                p.setPeriod(currMonthStart);
                wRetList.add(p);
            }
        }

        List<VariationReportBean> wActRetList = new ArrayList<>();
        HashMap<Long,LocalDate> wReabList = this.loadAbsorbedEmpByPayPeriod(bc,PayrollUtils.makeAuditPayPeriod(startCal.getMonthValue(),startCal.getYear()));


        for (VariationReportBean p : wRetList) {
            if(!wFilterBean.containsKey(p.getId()))
                continue;
            if(wReabList.containsKey(p.getEmployeeInstId())){
                LocalDate wCal = LocalDate.now();
                wCal = p.getSuspensionDate();
                if(wReabList.get(p.getEmployeeInstId()).isAfter(wCal)){
                    continue;
                }
            }
            wActRetList.add(p);
        }
        if (wActRetList.size() > 0) {
            Collections.sort(wActRetList, Comparator.comparing(VariationReportBean::getAgency));

        }

        return wActRetList;
    }

    private HashMap<Long, LocalDate> loadAbsorbedEmpByPayPeriod(BusinessCertificate bc,
            String pPayPeriod) {

        String wHql = "select max(r.id),r."+bc.getEmployeeIdJoinStr()+",r.absorptionDate from AbsorptionLog r "
                + "where r.auditPayPeriod = :pPayPeriodVar and r.businessClientId = :pBizIdVar "
                + "group by r."+bc.getEmployeeIdJoinStr()+", r.absorptionDate ";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pPayPeriodVar", pPayPeriod);
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());


        HashMap<Long, LocalDate> wRetMap = new HashMap<Long, LocalDate>();

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                LocalDate wCal = LocalDate.now();
                wCal = ((LocalDate) o[2]);
                wRetMap.put((Long) o[1], wCal);
            }
        }

        return wRetMap;
    }

    private HashMap<Long, Long> makeSuspensionFilterBeans(BusinessCertificate bc,
            LocalDate pStartCal) {

        HashMap<Long, Long> wRetList = new HashMap<Long, Long>();
        String wHql = "select max(r.id),r."+bc.getEmployeeIdJoinStr()
                + " from SuspensionLog r "
                + " where r.auditPayPeriod = :pPayPeriodVar and r.businessClientId = :pBizIdVar "
                + " group by r."+bc.getEmployeeIdJoinStr();

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(pStartCal.getMonthValue(),pStartCal.getYear()));
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                VariationReportBean p = new VariationReportBean();
                p.setId((Long)o[0]);
                wRetList.put(p.getId(), p.getId());

            }
        }

        return wRetList;
    }


    public List<VariationReportBean> loadBankChangesVariationReturnList(
            LocalDate startCal, boolean pSchoolsOnly, BusinessCertificate bc) throws Exception
    {

        LocalDate currMonthStart = startCal;


        List<VariationReportBean> wRetList = new ArrayList<VariationReportBean>();

        String wHql = "select e.id, e.employeeId, e.firstName, e.lastName, e.initials, s.level, s.step, "
                + " p.lastModTs,p.auditTimeStamp, m.name, p.user.firstName||' '||p.user.lastName,p.oldValue,p.newValue"
                + " from "+IppmsUtils.getEmployeeTableName(bc)+" e, PaymentMethodInfoLog p, SalaryInfo s, MdaInfo m, MdaDeptMap md "
                + " where p."+bc.getEmployeeIdJoinStr()+" = e.id and e.mdaDeptMap.id = md.id and md.mdaInfo.id = m.id and "
                + " p.salaryInfo.id = s.id and e.businessClientId = :pBizIdVar "
                + " and p.auditPayPeriod = :pPayPeriodVar  and e.businessClientId = p.businessClientId "
                + " order by m.name, e.lastName, e.firstName,p.lastModTs";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(startCal.getMonthValue(), startCal.getYear()));
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                VariationReportBean p = new VariationReportBean();
                p.setEmployeeInstId((Long) o[0]);
                p.setEmployeeId((String) o[1]);
                String pFirstName = ((String) o[2]);
                String pLastName = ((String) o[3]);
                String pInitials = null;
                if (o[4] != null) {
                    pInitials = ((String) o[4]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));


                p.setGradeStep(o[5] + "/ "
                        + PayrollUtils.formatStep((Integer) o[6]));

                p.setCreatedDateAndAuditTime(PayrollHRUtils.getFullDateFormat().format((LocalDate) o[7])+" "+ o[8]);
                p.setAgency(((String) o[9]).trim());
                p.setUserName((String) o[10]);
                p.setOldValue(((String)o[11]).trim());
                p.setNewValue(((String)o[12]).trim());

                p.setReportName(" Account Changes");
                p.setPeriod(currMonthStart);


                wRetList.add(p);
            }
        }
        if (wRetList.size() > 0) {
            Collections.sort(wRetList, Comparator.comparing(VariationReportBean::getAgency));

        }
        return wRetList;
    }


    public List<VariationReportBean> loadTransferVariationReturnList(
            LocalDate startCal, boolean pSchoolsOnly, BusinessCertificate bc) throws Exception
    {

        LocalDate currMonthStart = startCal;

        List<VariationReportBean> wRetList = new ArrayList<>();

        String wHql = "select e.id, e.employeeId, e.firstName, e.lastName, e.initials, s.level, s.step, "
                + "p.transferDate,p.auditTime, m.name, p.user.firstName||' '||p.user.lastName,p.oldMda,p.newMda "
                + " from "+IppmsUtils.getEmployeeTableName(bc)+" e, TransferLog p, SalaryInfo s, MdaInfo m  "
                + " where e.id = p."+bc.getEmployeeIdJoinStr()+" and p.salaryInfo.id = s.id and m.id = p.mdaInfo.id "
                + "and p.auditPayPeriod = :pPayPeriodVar and p.businessClientId = :pBizIdVar "
                + " order by m.name, e.lastName, e.firstName,p.transferDate";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(startCal.getMonthValue(), startCal.getYear()));
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                VariationReportBean p = new VariationReportBean();
                p.setEmployeeInstId((Long) o[0]);
                p.setEmployeeId((String) o[1]);
                String pFirstName = ((String) o[2]);
                String pLastName = ((String) o[3]);
                String pInitials = null;
                if (o[4] != null) {
                    pInitials = ((String) o[4]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));
                p.setGradeStep(o[5] + "/ "
                        + PayrollUtils.formatStep((Integer) o[6]));
                p.setCreatedDateAndAuditTime(PayrollHRUtils.getFullDateFormat().format((LocalDate) o[7])+" "+ o[8]);

                p.setAgency(((String) o[9]).trim());
                p.setUserName((String) o[10]);
                p.setOldValue(((String)o[11]).trim());
                p.setNewValue(((String)o[12]).trim());

                p.setReportName(" Transfers");
                p.setPeriod(currMonthStart);

                wRetList.add(p);
            }
        }
        if (wRetList.size() > 0) {
            Collections.sort(wRetList, Comparator.comparing(VariationReportBean::getAgency));

        }
        return wRetList;
    }


    public List<VariationReportBean> loadPayGroupMonthlyVariationReturnList(
            LocalDate startCal, boolean pSchoolsOnly, BusinessCertificate bc) throws Exception
    {


        LocalDate currMonthStart = startCal;


        List<VariationReportBean> wRetList = new ArrayList<>();

        String wHql = "select e.id, e.employeeId, e.firstName, e.lastName, e.initials, p.lastModTs, "
                + "s.salaryType.name, s.level, s.step,p.refDate, m.name, p.user.firstName||' '||p.user.lastName,s0.salaryType.name, s0.level, s0.step "
                + " from "+IppmsUtils.getEmployeeTableName(bc)+" e, ReassignEmployeeLog p, SalaryInfo s,SalaryInfo s0, MdaInfo m "
                + " where e.id = p.employee.id  and p.oldSalaryInfo.id = s.id and p.salaryInfo.id = s0.id"
                + " and p.auditPayPeriod = :pPayPeriodVar and p.businessClientId = :pBizClientVar "
                + " and p.demotionInd = 0 and m.id = p.mdaInfo.id"
                + " order by m.name, e.lastName, e.firstName,p.refDate";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(startCal.getMonthValue(), startCal.getYear()));
        query.setParameter("pBizClientVar", bc.getBusinessClientInstId());



        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                VariationReportBean p = new VariationReportBean();
                p.setEmployeeInstId((Long) o[0]);
                p.setEmployeeId((String) o[1]);
                String pFirstName = ((String) o[2]);
                String pLastName = ((String) o[3]);
                String pInitials = null;
                if (o[4] != null) {
                    pInitials = ((String) o[4]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));
                p.setCreatedDateAndAuditTime(PayrollHRUtils.getFullDateFormat().format((LocalDate) o[5]));


                p.setOldValue(o[6] + " : "+ o[7] + "/ "
                        + PayrollUtils.formatStep((Integer) o[8]));


                p.setReportName(" PayGroup Report");
                p.setPeriod(currMonthStart);

                p.setCreatedDate((LocalDate) o[9]);
                p.setAgency(((String) o[10]).trim());
                p.setUserName((String) o[11]);
                p.setNewValue(o[12] + " : "+ o[13] + "/ "
                        + PayrollUtils.formatStep((Integer) o[14]));

                wRetList.add(p);
            }
        }

        return wRetList;
    }

    public List<VariationReportBean> loadLoansVariationReturnList(
            LocalDate startCal, boolean pSchoolsOnly, BusinessCertificate bc) throws Exception
    {

        LocalDate currMonthStart = startCal;

        List<VariationReportBean> wRetList = new ArrayList<VariationReportBean>();

        String wHql = "select e.id, e.employeeId, e.firstName, e.lastName, e.initials, s.level, s.step, "
                + "p.lastModTs,p.auditTimeStamp, m.name, p.user.firstName||' '||p.user.lastName,p.oldValue,p.newValue,gt.description,p.columnChanged "
                + " from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getGarnishAuditTableName(bc)+" p, SalaryInfo s, EmpGarnishmentType gt, MdaInfo m "
                + " where e.id = p.employee.id and p.salaryInfo.id = s.id  and p.mdaInfo.id = m.id "
                + "and p.garnishmentType.id = gt.id and p.businessClientId = e.businessClientId "
                + "and p.auditPayPeriod = :pPayPeriodVar and p.businessClientId = :pBizIdVar "
                + " order by m.name, e.lastName, e.firstName,p.lastModTs";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(startCal.getMonthValue(), startCal.getYear()));
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());


        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                VariationReportBean p = new VariationReportBean();
                p.setEmployeeInstId((Long) o[0]);
                p.setEmployeeId((String) o[1]);
                String pFirstName = ((String) o[2]);
                String pLastName = ((String) o[3]);
                String pInitials = null;
                if (o[4] != null) {
                    pInitials = ((String) o[4]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));
                p.setGradeStep(o[5] + "/ "
                        + PayrollUtils.formatStep((Integer) o[6]));
                p.setCreatedDateAndAuditTime(PayrollHRUtils.getFullDateFormat().format((LocalDate) o[7])+" "+ o[8]);
                p.setAgency(((String) o[9]).trim());
                p.setUserName((String) o[10]);
                p.setOldValue(((String)o[11]).trim());
                p.setNewValue(((String)o[12]).trim());
                p.setLoanCode((String)o[13]);
                p.setColumnChanged((String)o[14]);
                p.setReportName(" Loans");
                p.setPeriod(currMonthStart);

                wRetList.add(p);
            }
        }
        if (wRetList.size() > 0) {
            Collections.sort(wRetList, Comparator.comparing(VariationReportBean::getAgency));

        }
        return wRetList;
    }


    public List<VariationReportBean> loadEmployeeChangeVariationReturnList(
            Long bClientId, LocalDate startCal, boolean pSchoolsOnly,
            BusinessCertificate bc) throws Exception
    {

        LocalDate currMonthStart = startCal;


        List<VariationReportBean> wRetList = new ArrayList<VariationReportBean>();

        String wHql = "select e.id, e.employeeId, e.firstName, e.lastName, e.initials, s.level, s.step, "
                + " a.lastModTs,a.auditTimeStamp, m.name, a.user.firstName||' '||a.user.lastName,a.oldValue,a.newValue, a.columnChanged "
                + " from "+IppmsUtils.getEmployeeTableName(bc)+" e, SalaryInfo s, "+IppmsUtils.getEmployeeAuditTable(bc)+" a, MdaInfo m"
                + " where e.id = a.employee.id and a.salaryInfo.id = s.id and m.id = a.mdaInfo.id and a.businessClientId = e.businessClientId "
                + "and a.auditPayPeriod = :pPayPeriodVar and a.businessClientId = :pBizIdVar "
                + " and  a.auditActionType = :pUpdVar "
                + " order by m.name, e.lastName, e.firstName,a.lastModTs";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(startCal.getMonthValue(), startCal.getYear()));
        query.setParameter("pUpdVar", "U");
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                VariationReportBean p = new VariationReportBean();
                p.setEmployeeInstId((Long) o[0]);
                p.setEmployeeId((String) o[1]);
                String pFirstName = ((String) o[2]);
                String pLastName = ((String) o[3]);
                String pInitials = null;
                if (o[4] != null) {
                    pInitials = ((String) o[4]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));


                p.setGradeStep(o[5] + "/ "
                        + PayrollUtils.formatStep((Integer) o[6]));

                p.setCreatedDateAndAuditTime(PayrollHRUtils.getFullDateFormat().format((LocalDate) o[7])+" "+ o[8]);
                p.setAgency(((String) o[9]).trim());
                p.setUserName((String) o[10]);
                p.setOldValue(((String)o[11]).trim());
                p.setNewValue(((String)o[12]).trim());
                p.setColumnChanged((String)o[13]);
                p.setReportName(" Employee Changes");
                p.setPeriod(currMonthStart);



                wRetList.add(p);
            }
        }
        wRetList = this.loadHiringInfoChanges(  startCal,   pSchoolsOnly,wRetList, bc);
        if (wRetList.size() > 0) {
            Collections.sort(wRetList, Comparator.comparing(VariationReportBean::getAgency));

        }
        return wRetList;
    }

    private List<VariationReportBean> loadHiringInfoChanges(LocalDate pStartCal,
                                                            boolean pSchoolsOnly, List<VariationReportBean> pRetList,
                                                            BusinessCertificate bc) throws Exception
    {
        LocalDate currMonthStart = pStartCal;




        String wHql = "select e.id, e.employeeId, e.firstName, e.lastName, e.initials, s.level, s.step, "
                + " a.lastModTs,a.auditTimeStamp, m.name, a.user.firstName||' '||a.user.lastName,a.oldValue,a.newValue,a.columnChanged "
                + " from "+IppmsUtils.getEmployeeTableName(bc)+" e, SalaryInfo s, HiringInfoAudit a, HiringInfo h, MdaInfo m"
                + " where h.id = a.hireInfo.id and e.id = h."+bc.getEmployeeIdJoinStr()+" and a.salaryInfo.id = s.id and  a.businessClientId = e.businessClientId "
                + "and a.auditPayPeriod = :pPayPeriodVar and m.id = a.mdaInfo.id and a.businessClientId = :pBizIdVar"
                + " order by m.name, e.lastName, e.firstName,a.lastModTs";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(pStartCal.getMonthValue(), pStartCal.getYear()));
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                VariationReportBean p = new VariationReportBean();
                p.setEmployeeInstId((Long) o[0]);
                p.setEmployeeId((String) o[1]);
                String pFirstName = ((String) o[2]);
                String pLastName = ((String) o[3]);
                String pInitials = null;
                if (o[4] != null) {
                    pInitials = ((String) o[4]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));


                p.setGradeStep(o[5] + "/ "
                        + PayrollUtils.formatStep((Integer) o[6]));

                p.setCreatedDateAndAuditTime(PayrollHRUtils.getFullDateFormat().format((LocalDate) o[7])+" "+ o[8]);
                p.setAgency(((String) o[9]).trim());
                p.setUserName((String) o[10]);
                p.setOldValue(((String)o[11]).trim());
                p.setNewValue(((String)o[12]).trim());
                p.setColumnChanged((String)o[13]);
                p.setReportName(bc.getStaffTypeName()+" Changes");
                p.setPeriod(currMonthStart);



                pRetList.add(p);
            }
        }
        return pRetList;
    }


    public List<VariationReportBean> loadDemotionForMonthlyVariationReturnList(LocalDate startCal, boolean pSchoolsOnly,
                                                                               BusinessCertificate bc) throws Exception
    {


        LocalDate currMonthStart = startCal;

        List<VariationReportBean> wRetList = new ArrayList<VariationReportBean>();
        String wHql = "select e.id, e.employeeId, e.firstName, e.lastName, e.initials, "
                + "s.salaryType.name, s.level, s.step,p.refDate, m.name, p.user.firstName||' '||p.user.lastName,s0.salaryType.name, s0.level, s0.step "
                + " from "+IppmsUtils.getEmployeeTableName(bc)+" e, ReassignEmployeeLog p, SalaryInfo s,SalaryInfo s0, MdaInfo m "
                + " where e.id = p.employee.id  and p.oldSalaryInfo.id = s.id and p.salaryInfo.id = s0.id and m.id = p.mdaInfo.id"
                + " and p.auditPayPeriod = :pPayPeriodVar and p.businessClientId = :pBizIdVar "
                + " and p.demotionInd = 1 "
                + " order by  m.name, e.lastName, e.firstName,p.refDate";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(startCal.getMonthValue(), startCal.getYear()));
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());


        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                VariationReportBean p = new VariationReportBean();
                p.setEmployeeInstId((Long) o[0]);
                p.setEmployeeId((String) o[1]);
                String pFirstName = ((String) o[2]);
                String pLastName = ((String) o[3]);
                String pInitials = null;
                if (o[4] != null) {
                    pInitials = ((String) o[4]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));
                p.setCreatedDateAndAuditTime(PayrollHRUtils.getFullDateFormat().format((LocalDate) o[8]));


                p.setOldValue(o[5] + " : "+ o[6] + "/ "
                        + PayrollUtils.formatStep((Integer) o[7]));


                p.setReportName(" Demotions Report");
                p.setPeriod(currMonthStart);

                p.setCreatedDate((LocalDate) o[8]);
                p.setAgency(((String) o[9]).trim());
                p.setUserName((String) o[10]);
                p.setNewValue(o[11] + " : "+ o[12] + "/ "
                        + PayrollUtils.formatStep((Integer) o[13]));

                wRetList.add(p);
            }
        }
        if (wRetList.size() > 0) {
            Collections.sort(wRetList, Comparator.comparing(VariationReportBean::getAgency));

        }
        return wRetList;

    }



}
