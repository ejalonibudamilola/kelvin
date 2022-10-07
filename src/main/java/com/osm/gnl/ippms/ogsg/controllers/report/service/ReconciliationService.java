package com.osm.gnl.ippms.ogsg.controllers.report.service;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.audit.domain.HiringInfoAudit;
import com.osm.gnl.ippms.ogsg.audit.domain.PromotionAudit;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRun;
import com.osm.gnl.ippms.ogsg.domain.report.SummaryPage;
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

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@Service
@Repository
@Transactional(readOnly = true)
@Slf4j
public class ReconciliationService extends BaseController {

    private final SessionFactory sessionFactory;

    private HashMap<Long, String> termReasonMap;

    public ReconciliationService(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    public List<VariationReportBean> loadNewEmployees(LocalDate startCal,
                                                      BusinessCertificate bc) {


        LocalDate currMonthStart = startCal;

        List<VariationReportBean> wRetList = new ArrayList<>();

        String wHql = "select e.id, e.employeeId, e.firstName, e.lastName, e.initials, h.hireDate, p.totalPay, s.payGroupCode, s.level, s.step, "
                + "e.creationDate, m.name, a.user.firstName,a.user.lastName, s.salaryType.name,h.monthlyPensionAmount "
                + " from " + IppmsUtils.getEmployeeTableName(bc) + " e, HiringInfo h, " + IppmsUtils.getPaycheckTableName(bc) + " p, SalaryInfo s, " + IppmsUtils.getEmployeeAuditTable(bc) + " a, MdaInfo m"
                + " where e.id = p.employee.id and e.id = h." + bc.getEmployeeIdJoinStr() + " and p.salaryInfo.id = s.id "
                + "and h.hireDate is not null and m.id = a.mdaInfo.id "
                + "and a.auditPayPeriod = :pPayPeriodVar and e.id = h." + bc.getEmployeeIdJoinStr() + " and e.id = a.employee.id and p.employee.id = e.id "
                + "and a.auditActionType = 'I'"
                + " and p.runMonth = :pRunMonthVar and p.runYear = :pRunYearVar "
                + " and a.employee.id = e.id and a.auditActionType = :pAuditActionType and e.businessClientId = :pBizIdVar "
                + " order by e.lastName, e.firstName";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(startCal.getMonthValue(), startCal.getYear()));
        query.setParameter("pAuditActionType", "I");
        query.setParameter("pRunMonthVar", startCal.getMonthValue());
        query.setParameter("pRunYearVar", startCal.getYear());
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {
            VariationReportBean p;
            String pFirstName, pLastName, pInitials;
            for (Object[] o : wRetVal) {
                p = new VariationReportBean();
                p.setEmployeeInstId((Long) o[0]);
                p.setEmployeeId((String) o[1]);
                pFirstName = ((String) o[2]);
                pLastName = ((String) o[3]);
                pInitials = null;
                if (o[4] != null) {
                    pInitials = ((String) o[4]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));
                p.setChangedDate((LocalDate) o[5]);
                p.setCurrentPeriodGross((Double) o[6]);
                p.setCurrentPeriodGrossStr(PayrollHRUtils.getDecimalFormat()
                        .format(o[6]));
                p.setPayGroup((String) o[7]);
                p.setGradeStep(o[7] + ": "
                        + o[8] + "/ "
                        + PayrollUtils.formatStep((Integer) o[9]));
                p.setLastPeriodGross(0.0D);

                if (p.getCurrentPeriodGross() != null
                        && p.getLastPeriodGross() != null)
                    p.setGrossDifference(p.getCurrentPeriodGross()
                            - p.getLastPeriodGross());

                p.setReportName(" New Staff Report");
                p.setPeriod(currMonthStart);

                p.setCreatedDate(((Timestamp) o[10]).toLocalDateTime().toLocalDate());
                p.setAgency((String) o[11]);
                //p.setUserName((String) o[12]);

                p.setUserName(o[12] + " " + o[13]);
                if (bc.isPensioner())
                    p.setMonthlyPensionAmount((Double) o[15]);

                wRetList.add(p);
            }
        }

        return wRetList;
    }


    public List<VariationReportBean> loadTerminatedEmployees(LocalDate startCal, boolean prevMonth,
                                                             BusinessCertificate bc) {

        setTerminateReasonMap();


        LocalDate wPrevMonthStart = PayrollBeanUtils.getPreviousMonthDate(startCal, false);
        // Date wPrevMonthEnd = PayrollBeanUtils.getPreviousMonthDate(startCal, true);
        int prevRunMonth = wPrevMonthStart.getMonthValue();
        int prevRunYear = wPrevMonthStart.getYear();

        LocalDate currMonthStart = startCal;
        //   Date currMonthEnd = endCal.getTime();
        int runMonth = currMonthStart.getMonthValue();
        int runYear = currMonthStart.getYear();

        LocalDate currPayrollRunDate = currMonthStart;
        LocalDate prevPayrollRunDate = wPrevMonthStart;

        List<VariationReportBean> wRetList = new ArrayList<>();

        List<Long> wEmpIdList = new ArrayList<>();

        String wHql = "select e.id, e.employeeId, e.firstName, e.lastName, e.initials, h.terminateDate, p.totalPay, s.salaryType.name, s.level, s.step, "
                + "p.payByDaysInd, coalesce(h.terminateReason.id, null), user.firstName, user.lastName, m.name, p0.totalPay, h.expectedDateOfRetirement,e.lastModTs "
                + " from " + IppmsUtils.getEmployeeTableName(bc) + " e, HiringInfo h, " + IppmsUtils.getPaycheckTableName(bc) + " p, SalaryInfo s, " + IppmsUtils.getPaycheckTableName(bc) + " p0, User user, MdaInfo m,MdaDeptMap mdm "
                + " where e.id = p.employee.id and e.id = h." + bc.getEmployeeIdJoinStr() + " and p.salaryInfo.id = s.id and e.businessClientId = :pBizIdVar"
                + " and p.runMonth = :runMonth and p.runYear = :runYear"
                + " and h.lastModBy.username = user.username"
                + " and p0.runMonth = :prevRunMonth and p0.runYear = :prevRunYear and e.id = p0.employee.id"
                + " and p0.totalPay > 0 and m.id = mdm.mdaInfo.id and mdm.id = p.mdaDeptMap.id"
                + " and (p.totalPay = 0 or (p.terminatedInd = 1 and p0.salaryInfo.id = p.salaryInfo.id))"
                + " and e.id not in (select distinct(s." + bc.getEmployeeIdJoinStr() + ") from SuspensionLog s where s.suspensionDate >= :startDate and s.suspensionDate <= :endDate)"
                + " order by e.lastName, e.firstName";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("startDate", prevPayrollRunDate);
        query.setParameter("endDate", currPayrollRunDate);
        query.setParameter("runYear", runYear);
        query.setParameter("runMonth", runMonth);
        query.setParameter("prevRunYear", prevRunYear);
        query.setParameter("prevRunMonth", prevRunMonth);
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {
            VariationReportBean p;
            String pFirstName, pLastName, pInitials;
            for (Object[] o : wRetVal) {
                p = new VariationReportBean();
                p.setEmployeeInstId((Long) o[0]);
                p.setEmployeeId((String) o[1]);

                pFirstName = ((String) o[2]);
                pLastName = ((String) o[3]);
                pInitials = null;
                if (o[4] != null) {
                    pInitials = ((String) o[4]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials));

//                if (o[5] != null)
//                    p.setChangedDate((LocalDate) o[5]);
//                else
//                    p.setChangedDate((LocalDate) o[17]);

                Timestamp timestamp;
                LocalDate toLocalDate = null;
                if (o[5] != null) {
                    p.setChangedDate((LocalDate) o[5]);
                } else {
                    timestamp = (Timestamp) o[17];
                    toLocalDate = timestamp.toLocalDateTime().toLocalDate();
                    p.setChangedDate(toLocalDate);
                }

                p.setCurrentPeriodGross((Double) o[6]);
                p.setCurrentPeriodGrossStr(PayrollHRUtils.getDecimalFormat().format(o[6]));
                p.setPayGroup((String) o[7]);
                p.setGradeStep(p.getPayGroup() + ": " + o[8] + "/ " + o[9]);

                if (o[11] == null)
                    p.setTermReason("Auto Retirement");
                else
                    p.setTermReason(this.termReasonMap.get(o[11]));

                if (p.getTermReason().equalsIgnoreCase("Auto Retirement")) {
                    p.setUserName(p.getTermReason());
                } else {
                    p.setUserName(o[12] + " " + o[13]);
                }

                p.setAgency((String) o[14]);

                p.setLastPeriodGross((Double) o[15]);
                p.setGrossDifference(p.getCurrentPeriodGross() - p.getLastPeriodGross());

                p.setReportName(" Termination Report");
                p.setDateName("Terminated Date");
                p.setPeriod(currMonthStart);

                wRetList.add(p);
                wEmpIdList.add(p.getEmployeeInstId());
            }
        }
        return wRetList;
    }

    private void setTerminateReasonMap() {

        this.termReasonMap = new HashMap<>();

        Query query = this.sessionFactory.getCurrentSession().createQuery("select id, name from TerminateReason");

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                Long key = (Long) o[0];
                String value = (String) o[1];
                this.termReasonMap.put(key, value);
            }
        }
    }

    public List<VariationReportBean> loadSuspensionLog(LocalDate startCal,
                                                       BusinessCertificate bc) {

        // LocalDate wPrevMonthStart = PayrollBeanUtils.getPreviousMonthDate(startCal,false);

        LocalDate currMonthStart = startCal;
        int runMonth = currMonthStart.getMonthValue();
        int runYear = currMonthStart.getYear();


        // HashMap<Long, Long> wFilterBean = this.makeSuspensionFilterBeans(bc,startCal);

        List<VariationReportBean> wRetList = new ArrayList<>();
        String wHql = "select r.id,e.id, e.employeeId, e.firstName,e.lastName, e.initials," +
                " s.payGroupCode, s.level, s.step, p.totalPay, r.suspensionDate, r.user.username,"
                + " m.name, s.id"
                + " from SuspensionLog r, SalaryInfo s, " + IppmsUtils.getPaycheckTableName(bc) + " p, " + IppmsUtils.getEmployeeTableName(bc) + " e, MdaInfo m, User u "
                + " where r.auditPayPeriod = :pPayPeriodVar"
                + " and r." + bc.getEmployeeIdJoinStr() + " = p.employee.id and r.user.id = u.id"
                + " and p.employee.id = e.id and m.id = r.mdaInfo.id"
                + " and p.runMonth = :runMonth and p.runYear = :runYear "
                + " and p.salaryInfo.id = s.id and r.businessClientId = :pBizIdVar"
                + " order by e.lastName, e.firstName";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(runMonth, runYear));
        query.setParameter("runMonth", runMonth);
        query.setParameter("runYear", runYear);
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());


        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {
            VariationReportBean p;
            String pFirstName, pLastName, pInitials;
            for (Object[] o : wRetVal) {
                p = new VariationReportBean();
                int i = 0;
                p.setId((Long) o[i]);
                p.setEmployeeInstId((Long) o[1]);
                p.setEmployeeId((String) o[2]);
                pFirstName = ((String) o[3]);
                pLastName = ((String) o[4]);
                pInitials = null;
                if (o[5] != null) {
                    pInitials = ((String) o[5]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));
                p.setPayGroup((String) o[6]);
                p.setGradeStep(o[7] + "/ "
                        + PayrollUtils.formatStep((Integer) o[8]));
                p.setCurrentPeriodGross((Double) o[9]);
                p.setCurrentPeriodGrossStr(PayrollHRUtils.getDecimalFormat()
                        .format(o[9]));

                p.setLastPeriodGross(0.0D);

                if (p.getCurrentPeriodGross() != null
                        && p.getLastPeriodGross() != null)
                    p.setGrossDifference(p.getCurrentPeriodGross()
                            - p.getLastPeriodGross());

                p.setChangedDate((LocalDate) o[10]);
                p.setUserName((String) o[11]);

                p.setAgencyName((String) o[12]);
                p.setAgency(p.getAgencyName());
                p.setPrevSalInfoInstId((Long) o[13]);
                wRetList.add(p);
            }
        }
        return wRetList;
    }

    private HashMap<Long, Long> makeSuspensionFilterBeans(BusinessCertificate bc,
                                                          LocalDate pStartCal) {

        HashMap<Long, Long> wRetList = new HashMap<>();
        String wHql = "select max(r.id),r." + bc.getEmployeeIdJoinStr() + ""
                + " from SuspensionLog r"
                + " where r.auditPayPeriod = :pPayPeriodVar and r.businessClientId = :pBizIdVar"
                + " group by r." + bc.getEmployeeIdJoinStr();

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(pStartCal.getMonthValue(), pStartCal.getYear()));
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                VariationReportBean p = new VariationReportBean();
                p.setId((Long) o[0]);
                wRetList.put(p.getId(), p.getId());

            }
        }

        return wRetList;
    }

    public List<VariationReportBean> loadReinstatementLog(LocalDate startCal,
                                                          BusinessCertificate bc) {

        LocalDate wPrevMonthStart = PayrollBeanUtils.getPreviousMonthDate(startCal,
                false);
        // Date wPrevMonthEnd = PayrollBeanUtils.getPreviousMonthDate(endCal,
        // true);
        int prevRunMonth = wPrevMonthStart.getMonthValue();
        int prevRunYear = wPrevMonthStart.getYear();

        LocalDate currMonthStart = startCal;
        // Date currMonthEnd = endCal.getTime();
        int runMonth = currMonthStart.getMonthValue();
        int runYear = currMonthStart.getYear();

        LocalDate currPayrollRunDate = currMonthStart;
        LocalDate prevPayrollRunDate = wPrevMonthStart;

        List<VariationReportBean> wRetList = new ArrayList<>();

        String wHql = "select e.id, e.employeeId, e.firstName,e.lastName, e.initials, s.payGroupCode, s.level, s.step, p.totalPay, "
                + "r.reinstatementDate, r.terminationDate,m.name, r.user.firstName,r.user.lastName"
                + " from ReinstatementLog r, SalaryInfo s, User u, " + IppmsUtils.getPaycheckTableName(bc) + " p, " + IppmsUtils.getEmployeeTableName(bc) + " e, MdaInfo m "
                + " where r.reinstatementDate  >= :startDate and r.reinstatementDate <= :endDate"
                + " and r." + bc.getEmployeeIdJoinStr() + " = e.id and r.user.id = u.id"
                + " and p.employee.id = e.id"
                + " and r.businessClientId = :pBizIdVar"
                + " and p.runMonth = :runMonth and p.runYear = :runYear "
                + " and p.salaryInfo.id = s.id"
                + " and p.totalPay > 0"
                + " and (r.id, r." + bc.getEmployeeIdJoinStr() + ") in (select max(id), rl." + bc.getEmployeeIdJoinStr() + " from ReinstatementLog rl where rl.reinstatementDate >= :startDate and rl.reinstatementDate <= :endDate group by rl." + bc.getEmployeeIdJoinStr() + ")"
                + " and  r." + bc.getEmployeeIdJoinStr() + " not in (select distinct(employee.id) from " + IppmsUtils.getEmployeeAuditTable(bc) + " where auditPayPeriod = :pPayPeriod and auditActionType = 'I')"
                + " and  r." + bc.getEmployeeIdJoinStr() + " not in (select d.employee.id from " + IppmsUtils.getPaycheckTableName(bc) + " d where d.runMonth =:prevMonth and d.runYear =:prevYear and d.totalPay > 0 "
                + "and d.employee.id = p.employee.id and d.salaryInfo.id != p.salaryInfo.id )"
                + " order by e.lastName, e.firstName";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pPayPeriod", PayrollUtils.makeAuditPayPeriod(runMonth, runYear));
        wQuery.setParameter("startDate", prevPayrollRunDate);
        wQuery.setParameter("endDate", currPayrollRunDate);
        wQuery.setParameter("runMonth", runMonth);
        wQuery.setParameter("runYear", runYear);
        wQuery.setParameter("prevMonth", prevRunMonth);
        wQuery.setParameter("prevYear", prevRunYear);
        wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if (wRetVal.size() > 0) {
            VariationReportBean p;
            String pFirstName, pLastName, pInitials;
            for (Object[] o : wRetVal) {
                p = new VariationReportBean();
                p.setEmployeeInstId((Long) o[0]);
                p.setEmployeeId((String) o[1]);
                pFirstName = ((String) o[2]);
                pLastName = ((String) o[3]);
                pInitials = null;
                if (o[4] != null) {
                    pInitials = ((String) o[4]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));
                p.setPayGroup((String) o[5]);
                p.setGradeStep(o[6] + "/ "
                        + PayrollUtils.formatStep((Integer) o[7]));
                p.setCurrentPeriodGross((Double) o[8]);
                p.setCurrentPeriodGrossStr(PayrollHRUtils.getDecimalFormat()
                        .format(o[8]));
                p.setChangedDate((LocalDate) o[9]);
                p.setOpposingDate((LocalDate) o[10]);
                p.setAgency((String) o[11]);
                //Disable viewing of User Details on this report
                //p.setUserName((String) o[13]);
                p.setUserName(o[12] + " " + o[13]);
                wRetList.add(p);
            }
        }
        return wRetList;
    }

    public List<VariationReportBean> loadReabsorptionLog(LocalDate startCal,
                                                         BusinessCertificate bc) throws Exception {

        LocalDate wPrevMonthStart = PayrollBeanUtils.getPreviousMonthDate(startCal,
                false);

        LocalDate currMonthStart = startCal;
        int runMonth = currMonthStart.getMonthValue();
        int runYear = currMonthStart.getYear();

        LocalDate currPayrollRunDate = currMonthStart;
        LocalDate prevPayrollRunDate = wPrevMonthStart;
        HashMap<Long, Long> wFilterBean = this.makeAbsorptionFilterBeans(bc, startCal);

        List<VariationReportBean> wRetList = new ArrayList<>();
        List<VariationReportBean> wActRetList = new ArrayList<>();

        String wHql = "select r.id,r." + bc.getEmployeeIdJoinStr() + ", e.employeeId, e.firstName,e.lastName, e.initials, s.payGroupCode, s.level, s.step,"
                + " p.totalPay, r.absorptionDate, r.suspensionDate,m.name,  r.user.firstName,r.user.lastName, s.id"
                + " from AbsorptionLog r, SalaryInfo s, " + IppmsUtils.getPaycheckTableName(bc) + " p, " + IppmsUtils.getEmployeeTableName(bc) + " e,MdaInfo m, User u "
                + " where r.auditPayPeriod = :pPayPeriodVar"
                + " and r." + bc.getEmployeeIdJoinStr() + " = p.employee.id and r.businessClientId = :pBizIdVar"
                + " and m.id = r.mdaInfo.id and r.user.id = u.id"
                + " and p.employee.id = e.id"
                + " and p.runMonth = :runMonth and p.runYear = :runYear "
                + " and p.salaryInfo.id = s.id"
                + " and p.totalPay > 0"
                + " and p.payByDaysInd != 1"
                + " and e.id not in (select rl." + bc.getEmployeeIdJoinStr() + " from ReinstatementLog rl where rl.reinstatementDate >= :startDate and rl.reinstatementDate <= :endDate)"
                + " order by e.lastName, e.firstName";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        wQuery.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(runMonth, runYear));
        wQuery.setParameter("startDate", prevPayrollRunDate);
        wQuery.setParameter("endDate", currPayrollRunDate);
        wQuery.setParameter("runMonth", runMonth);
        wQuery.setParameter("runYear", runYear);
        wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if (wRetVal.size() > 0) {
            VariationReportBean p;
            String pFirstName, pLastName, pInitials;
            for (Object[] o : wRetVal) {
                p = new VariationReportBean();
                p.setId((Long) o[0]);
                p.setEmployeeInstId((Long) o[1]);
                p.setEmployeeId((String) o[2]);
                pFirstName = ((String) o[3]);
                pLastName = ((String) o[4]);
                pInitials = null;
                if (o[5] != null) {
                    pInitials = ((String) o[5]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));
                p.setPayGroup((String) o[6]);
                p.setGradeStep(o[7] + "/ "
                        + PayrollUtils.formatStep((Integer) o[8]));
                p.setCurrentPeriodGross((Double) o[9]);
                p.setCurrentPeriodGrossStr(PayrollHRUtils.getDecimalFormat()
                        .format(o[9]));
                p.setChangedDate((LocalDate) o[10]);
                p.setOpposingDate((LocalDate) o[11]);
                p.setAgency((String) o[12]);
                p.setUserName(o[13] + " " + o[14]);
                p.setSalaryInfoInstId((Long) o[15]);
                if (!wFilterBean.containsKey(p.getId())) continue;

                wRetList.add(p);
            }
        }
        return wRetList;
    }

    private HashMap<Long, Long> makeAbsorptionFilterBeans(BusinessCertificate bc,
                                                          LocalDate pStartCal) {

        HashMap<Long, Long> wRetList = new HashMap<>();
        String wHql = "select max(r.id),r." + bc.getEmployeeIdJoinStr()
                + " from AbsorptionLog r"
                + " where r.auditPayPeriod = :pPayPeriodVar and r.businessClientId = :pBizIdVar"
                + " group by r.employee.id";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(pStartCal.getMonthValue(), pStartCal.getYear()));
        wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {

                wRetList.put((Long) o[0], (Long) o[0]);

            }
        }

        return wRetList;
    }


    public List<VariationReportBean> loadReassignmentLog(LocalDate startCal,
                                                         BusinessCertificate bc) {


        LocalDate wPrevMonthStart = PayrollBeanUtils.getPreviousMonthDate(startCal,
                false);
        // Date wPrevMonthEnd = PayrollBeanUtils.getPreviousMonthDate(endCal,
        // true);
        int prevRunMonth = wPrevMonthStart.getMonthValue();
        int prevRunYear = wPrevMonthStart.getYear();

        LocalDate currMonthStart = startCal;
        // Date currMonthEnd = endCal.getTime();
        int runMonth = currMonthStart.getMonthValue();
        int runYear = currMonthStart.getYear();

        //Date currPayrollRunDate = getPayrollRunDateByPayPeriod(currMonthStart);
        //Date prevPayrollRunDate = getPayrollRunDateByPayPeriod(wPrevMonthStart);

        Double currPromotedStaffGrossSum = 0.0D;
        Double prevPromotedStaffGrossSum = 0.0D;

        List<VariationReportBean> wRetList = new ArrayList<>();

        Query wq = this.sessionFactory.getCurrentSession().createQuery("select p.employee.id, e.employeeId, s.level, s.step, s.payGroupCode, s0.level, s0.step, p0.totalPay, p.totalPay, "
                + "e.firstName, e.lastName, e.initials, s.salaryType.id, s0.salaryType.id, m.name, m0.name"
                + " from " + IppmsUtils.getPaycheckTableName(bc) + " p, " + IppmsUtils.getPaycheckTableName(bc) + " p0, " + IppmsUtils.getEmployeeTableName(bc) + " e,SalaryInfo s,SalaryInfo s0, MdaInfo m, MdaInfo m0 "
                + "where p.runMonth = "
                + runMonth
                + " and p.runYear = "
                + runYear
                + ""
                + " and p0.runMonth = "
                + prevRunMonth
                + " and p0.runYear = "
                + prevRunYear
                + " and p.totalPay > 0 "
                + " and e.businessClientId = " + bc.getBusinessClientInstId()
                + " and p.salaryInfo.id = s.id and p0.salaryInfo.id = s0.id and m.id = p.mdaDeptMap.mdaInfo.id and m0.id = p0.mdaDeptMap.mdaInfo.id "
                + " and p.salaryInfo.id != p0.salaryInfo.id "
                + " and (s.salaryType.id != s0.salaryType.id or "
                + " (s.salaryType.id = s0.salaryType.id and ( s.level < s0.level or (s.level = s0.level and s.step < s0.step))))"
                + "and p.employee.id = p0.employee.id and p.employee.id = e.id ");

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wq.list();

        if (wRetVal.size() > 0) {
            VariationReportBean p;
            String pFirstName, pLastName, pInitials;
            for (Object[] o : wRetVal) {
                p = new VariationReportBean();
                p.setEmployeeInstId((Long) o[0]);
                p.setEmployeeId((String) o[1]);
                p.setNewGradeStep(o[2] + "/ "
                        + o[3]);
                p.setPayGroup((String) o[4]);
                p.setOldGradeStep(o[5] + "/ "
                        + PayrollUtils.formatStep((Integer) o[6]));
                p.setLastPeriodGross((Double) o[7]);
                p.setCurrentPeriodGross((Double) o[8]);
                pFirstName = ((String) o[9]);
                pLastName = ((String) o[10]);
                pInitials = null;
                if (o[11] != null) {
                    pInitials = ((String) o[11]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));

                if (p.getCurrentPeriodGross() != null
                        && p.getLastPeriodGross() != null)
                    p.setGrossDifference(p.getCurrentPeriodGross()
                            - p.getLastPeriodGross());
                else
                    p.setGrossDifference(0.0D);

                currPromotedStaffGrossSum += p.getCurrentPeriodGross();
                prevPromotedStaffGrossSum += p.getLastPeriodGross();

                Double promotedStaffGrossDifference = currPromotedStaffGrossSum
                        - prevPromotedStaffGrossSum;

                p.setThisMonthGross(currPromotedStaffGrossSum);
                p.setPrevMonthGross(prevPromotedStaffGrossSum);

                p.setTotalGrossDifference(promotedStaffGrossDifference);

                String sql1 = "select p.refDate, s.salaryType.name, s.level, s.step, s1.salaryType.name, s1.level, s1.step, m.name,p.user.firstName,p.user.lastName"
                        + " from ReassignEmployeeLog p, SalaryInfo s, SalaryInfo s1, MdaInfo m where p.employee.id = :empId and p.businessClientId = :pBizIdVar" +
                        " and p.auditPayPeriod = :pPayPeriod and s.id = p.oldSalaryInfo.id and s1.id = p.salaryInfo.id and m.id = p.mdaInfo.id" +
                        "  order by p.id";
                Query wQ1 = this.sessionFactory.getCurrentSession().createQuery(sql1);
                wQ1.setParameter("pPayPeriod", PayrollUtils.makeAuditPayPeriod(runMonth, runYear));
                wQ1.setParameter("empId", p.getEmployeeInstId());
                wQ1.setParameter("pBizIdVar", bc.getBusinessClientInstId());
                if (wQ1.list().size() > 0) {
                    Object[] ret1 = (Object[]) wQ1.list().get(0);

                    //Date ret1 = (Date) (wQ1.list().get(0));
                    p.setChangedDate((LocalDate) ret1[0]);
                    p.setOldStep((Integer) ret1[3]);
                    p.setNewStep((Integer) ret1[6]);
                    p.setOldGradeStep(ret1[1] + "-" + ret1[2] + "/" + p.getFormatedOldStep());
                    p.setNewGradeStep(ret1[4] + "-" + ret1[5] + "/" + p.getFormatedOldStep());
                    p.setAgency((String) ret1[7]);
                    p.setOldAgency(p.getAgency());
                    p.setUserName(ret1[8] + " " + ret1[9]);

                }

                //if not in reassignment log, check promotion log
                if (p.getChangedDate() == null) {
                    String sql2 = "select p.lastModTs, s.salaryType.name, s.level, s.step, s1.salaryType.name, s1.level, s1.step, m.name,p.user.firstName, p.user.lastName"
                            + " from " + IppmsUtils.getPromotionAuditTable(bc) + " p, SalaryInfo s, SalaryInfo s1, MdaInfo m where p.employee.id = :empId and p.businessClientId = :pBizIdVar" +
                            " and p.auditPayPeriod = :pPayPeriod and s.id = p.oldSalaryInfo.id and s1.id = p.salaryInfo.id and m.id = p.mdaInfo.id" +
                            "  order by p.id";
                    Query wQ2 = this.sessionFactory.getCurrentSession().createQuery(sql2);
                    wQ2.setParameter("pPayPeriod", PayrollUtils.makeAuditPayPeriod(runMonth, runYear));
                    wQ2.setParameter("empId", p.getEmployeeInstId());
                    wQ2.setParameter("pBizIdVar", bc.getBusinessClientInstId());
                    List<Object[]> ret1 = wQ2.list();
                    for (Object[] o2 : ret1) {


                        //Date ret1 = (Date) (wQ1.list().get(0));
                        p.setChangedDate((LocalDate) o2[0]);
                        p.setOldStep((Integer) o2[3]);
                        p.setNewStep((Integer) o2[6]);
                        p.setOldGradeStep(o2[1] + "-" + o2[2] + "/" + p.getFormatedOldStep());
                        p.setNewGradeStep(o2[4] + "-" + o2[5] + "/" + p.getFormatedOldStep());
                        p.setAgency((String) o2[7]);
                        p.setOldAgency(p.getAgency());
                        p.setUserName(o2[8] + " " + o2[9]);

                    }
                }

                p.setPeriod(currMonthStart);
                p.setReportName("Reassignment Report");
                wRetList.add(p);
            }
        }
        return wRetList;
    }

    public List<VariationReportBean> loadPromotionLog(LocalDate startCal,
                                                      BusinessCertificate bc) {

        LocalDate wPrevMonthStart = PayrollBeanUtils.getPreviousMonthDate(startCal,
                false);

        int prevRunMonth = wPrevMonthStart.getMonthValue();
        int prevRunYear = wPrevMonthStart.getYear();

        LocalDate currMonthStart = startCal;

        int runMonth = currMonthStart.getMonthValue();
        int runYear = currMonthStart.getYear();

        Double currPromotedStaffGrossSum = 0.0D;
        Double prevPromotedStaffGrossSum = 0.0D;

        List<VariationReportBean> wRetList = new ArrayList<>();

        Query wq = this.sessionFactory.getCurrentSession().createQuery("select p.employee.id, e.employeeId, s.level, s.step, s.payGroupCode, s0.level, s0.step, p0.totalPay, p.totalPay, "
                + "e.firstName, e.lastName, e.initials, s.salaryType.id, s0.salaryType.id, m.name "
                + "from " + IppmsUtils.getPaycheckTableName(bc) + " p, " + IppmsUtils.getPaycheckTableName(bc) + " p0, " + IppmsUtils.getEmployeeTableName(bc) + " e,SalaryInfo s,SalaryInfo s0, MdaInfo m , MdaDeptMap mdm "
                + "where p.runMonth = "
                + runMonth
                + " and p.runYear = "
                + runYear
                + ""
                + " and p0.runMonth = "
                + prevRunMonth
                + " and p0.runYear = "
                + prevRunYear
                + " and p.salaryInfo.id = s.id and p0.salaryInfo.id = s0.id"
                + " and p.salaryInfo.id != p0.salaryInfo.id "
                + " and s.salaryType.id = s0.salaryType.id and (s.level > s0.level) "
                + "and p.employee.id = p0.employee.id and p.employee.id = e.id  and p.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = m.id and p0.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = m.id "
                + "and p.totalPay > 0 and p0.totalPay > 0 ");

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wq.list();

        if (wRetVal.size() > 0) {
            // System.out.println(wRetVal.size() + "retVal");
            HashMap<Long, LocalDate> promotionDateMap = this.makePromotionDateMap(bc, runMonth, runYear);

            VariationReportBean p;
            String pFirstName, pLastName, pInitials;
            for (Object[] o : wRetVal) {
                p = new VariationReportBean();
                p.setEmployeeInstId((Long) o[0]);
                p.setEmployeeId((String) o[1]);
                p.setNewGradeStep(o[2] + "/ "
                        + PayrollUtils.formatStep((Integer) o[3]));
                p.setPayGroup((String) o[4]);
                p.setOldGradeStep(o[5] + "/ "
                        + PayrollUtils.formatStep((Integer) o[6]));
                p.setLastPeriodGross((Double) o[7]);
                p.setCurrentPeriodGross((Double) o[8]);
                pFirstName = ((String) o[9]);
                pLastName = ((String) o[10]);
                pInitials = null;
                if (o[11] != null) {
                    pInitials = ((String) o[11]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));
                p.setAgency((String) o[14]);

                if (p.getCurrentPeriodGross() != null
                        && p.getLastPeriodGross() != null)
                    p.setGrossDifference(p.getCurrentPeriodGross()
                            - p.getLastPeriodGross());
                else
                    p.setGrossDifference(0.0D);

                currPromotedStaffGrossSum += p.getCurrentPeriodGross();
                prevPromotedStaffGrossSum += p.getLastPeriodGross();

                Double promotedStaffGrossDifference = currPromotedStaffGrossSum
                        - prevPromotedStaffGrossSum;

                p.setThisMonthGross(currPromotedStaffGrossSum);
                p.setPrevMonthGross(prevPromotedStaffGrossSum);

                p.setTotalGrossDifference(promotedStaffGrossDifference);


                p.setPeriod(currMonthStart);
                if (promotionDateMap.containsKey(p.getEmployeeInstId())) {
                    p.setChangedDate(promotionDateMap.get(p.getEmployeeInstId()));
                } else {
                    p.setChangedDate(startCal);
                }
                p.setUserName("N/A");
                wRetList.add(p);
            }
        }

        return wRetList;
    }

    private HashMap<Long, LocalDate> makePromotionDateMap(BusinessCertificate bc, int pRunMonth, int pRunYear) {
        HashMap<Long, LocalDate> retMap = new HashMap<>();
        String sql1 = "select p.employee.id,p.lastModTs from " + IppmsUtils.getPromotionAuditTable(bc) + " p where p.auditPayPeriod = :pPayPeriodVar order by p.lastModTs desc";
        Query wQ1 = this.sessionFactory.getCurrentSession().createQuery(sql1);

        wQ1.setParameter("pPayPeriodVar",
                PayrollUtils.makeAuditPayPeriod(pRunMonth, pRunYear));

        if (wQ1.list().size() > 0) {
            ArrayList<Object[]> retList = (ArrayList<Object[]>) wQ1
                    .list();
            LocalDate value;
            Long key;
            for (Object[] k : retList) {
                key = (Long) k[0];
                value = (LocalDate) k[1];
                if (!retMap.containsKey(key))
                    retMap.put(key, value);
            }
        }
        return retMap;

    }

    public List<VariationReportBean> loadStepIncrementLog(LocalDate startCal,
                                                          BusinessCertificate bc) throws Exception {


        LocalDate wPrevMonthStart = PayrollBeanUtils.getPreviousMonthDate(startCal, false);
        //  Date wPrevMonthEnd = PayrollBeanUtils.getPreviousMonthDate(endCal, true);
        int prevRunMonth = wPrevMonthStart.getMonthValue();
        int prevRunYear = wPrevMonthStart.getYear();

        LocalDate currMonthStart = startCal;
        //   Date currMonthEnd = endCal.getTime();
        int runMonth = currMonthStart.getMonthValue();
        int runYear = currMonthStart.getYear();

        LocalDate currPayrollRunDate = currMonthStart;
        LocalDate prevPayrollRunDate = wPrevMonthStart;

        Double currPromotedStaffGrossSum = 0.0D;
        Double prevPromotedStaffGrossSum = 0.0D;
        Double promotedStaffGrossDifference;
        HashMap<Long,PromotionAudit> wStepIncrementMap = this.getStepIncrementLog(bc,PayrollUtils.makeAuditPayPeriod(runMonth,runYear));
        List<VariationReportBean> wRetList = new ArrayList<>();



        Query wq = this.sessionFactory.getCurrentSession().createQuery("select p.employee.id, e.employeeId, s.level, s.step, s.salaryType.name, s0.level, s0.step, "
                + "p0.totalPay, p.totalPay, e.firstName, e.lastName, e.initials, s.salaryType.id, s0.salaryType.id, p.arrears, m.name "
                + "from " + IppmsUtils.getPaycheckTableName(bc) + " p, " + IppmsUtils.getPaycheckTableName(bc) + " p0, " + IppmsUtils.getEmployeeTableName(bc) + " e,SalaryInfo s,SalaryInfo s0, MdaInfo m , MdaDeptMap mdm, MdaInfo m0 "
                + "where p.runMonth = " + runMonth + " and p.runYear = " + runYear + ""
                + " and p0.runMonth = " + prevRunMonth + " and p0.runYear = " + prevRunYear + " and p.salaryInfo.id = s.id and p0.salaryInfo.id = s0.id"
                + " and p.salaryInfo.id != p0.salaryInfo.id and e.businessClientId = " + bc.getBusinessClientInstId()
                + " and s.salaryType.id = s0.salaryType.id and (s.level = s0.level and s.step > s0.step) "
                + "and p.employee.id = p0.employee.id and p.employee.id = e.id and mdm.id = p.mdaDeptMap.id and mdm.mdaInfo.id = m.id " +
                "and p0.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = m0.id "
                + "and p.totalPay > 0 and p0.totalPay > 0");

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wq.list();

        if (wRetVal.size() > 0) {
            VariationReportBean p;
            String pFirstName, pLastName, pInitials;
            Long eid;
            PromotionAudit promotionAudit;
            for (Object[] o : wRetVal) {
                eid = (Long)o[0];
                if(!wStepIncrementMap.containsKey(eid))
                    continue; //This is a Promotion Not Step Increment....

                p = new VariationReportBean();
                p.setEmployeeInstId(eid);
                p.setEmployeeId((String) o[1]);
                p.setPayGroup((String) o[4]);
                p.setNewGradeStep(o[2] + "/ " + o[3]);

                p.setOldGradeStep(o[5] + "/ " + o[6]);
                p.setLastPeriodGross((Double) o[7]);
                p.setCurrentPeriodGross((Double) o[8]);
                pFirstName = ((String) o[9]);
                pLastName = ((String) o[10]);
                pInitials = null;
                if (o[11] != null) {
                    pInitials = ((String) o[11]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials));

                p.setArrears((Double) o[14]);
                p.setAgency((String) o[15]);

                if (p.getCurrentPeriodGross() != null && p.getLastPeriodGross() != null)
                    p.setGrossDifference(p.getCurrentPeriodGross() - p.getLastPeriodGross());
                else
                    p.setGrossDifference(0.0D);

                currPromotedStaffGrossSum += p.getCurrentPeriodGross();
                prevPromotedStaffGrossSum += p.getLastPeriodGross();

                promotedStaffGrossDifference = currPromotedStaffGrossSum - prevPromotedStaffGrossSum;

                p.setThisMonthGross(currPromotedStaffGrossSum);
                p.setPrevMonthGross(prevPromotedStaffGrossSum);

                p.setTotalGrossDifference(promotedStaffGrossDifference);

                promotionAudit = wStepIncrementMap.get(eid);
                 p.setChangedDate(promotionAudit.getPromotionDate());
                 p.setUserName(promotionAudit.getUserName());

                p.setPeriod(currMonthStart);
                p.setReportName("STEP INCREMENT REPORT");

                wRetList.add(p);
            }
        }
        return wRetList;
    }


    public List<VariationReportBean> loadAllowancePaid(LocalDate startCal, LocalDate endCal, boolean decrease, Long busClientId,
                                                       BusinessCertificate bc) throws Exception {

        LocalDate wPrevMonthStart = PayrollBeanUtils.getPreviousMonthDate(startCal, false);
        //  Date wPrevMonthEnd = PayrollBeanUtils.getPreviousMonthDate(endCal, true);
        int prevRunMonth = wPrevMonthStart.getMonthValue();
        int prevRunYear = wPrevMonthStart.getYear();

        LocalDate currMonthStart = startCal;
        //   Date currMonthEnd = endCal.getTime();
        int runMonth = currMonthStart.getMonthValue();
        int runYear = currMonthStart.getYear();

        List<VariationReportBean> wRetList = new ArrayList<>();
        List<VariationReportBean> wActRetList = new ArrayList<>();

        //List<VariationReportBean> wFinalList = new ArrayList<VariationReportBean>();


        String hql = "select e.id, e.employeeId, e.firstName, e.lastName, e.initials, coalesce(p.specialAllowance, 0.00), coalesce(p1.specialAllowance, 0.00), "
                + "coalesce(p.totalPay, 0.00), coalesce(p1.totalPay, 0.00), coalesce(m.name, m1.name)"
                + " from " + IppmsUtils.getEmployeeTableName(bc) + " e, " + IppmsUtils.getPaycheckTableName(bc) + " p, " + IppmsUtils.getPaycheckTableName(bc) + " p1, MdaInfo m, MdaInfo m1 "
                + " where p.runMonth = :runMonth and p.runYear = :runYear and p1.runYear =:prevYear and p1.runMonth =:prevMonth "
                + " and p.employee.id = e.id and p.employee.id = p1.employee.id and e.businessClientId = :pBizIdVar and m.id = p.mdaDeptMap.mdaInfo.id and m1.id = p1.mdaDeptMap.mdaInfo.id and" +
                " p.specialAllowance <> p1.specialAllowance and p.specialAllowance > 0 and p1.specialAllowance > 0";


        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(hql);
        wQuery.setParameter("runMonth", runMonth);
        wQuery.setParameter("runYear", runYear);
        wQuery.setParameter("prevMonth", prevRunMonth);
        wQuery.setParameter("prevYear", prevRunYear);
        wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if (wRetVal.size() > 0) {
            VariationReportBean p;
            String pFirstName, pLastName, pInitials;
            for (Object[] o : wRetVal) {
                p = new VariationReportBean();
                p.setEmployeeInstId((Long) o[0]);
                p.setEmployeeId((String) o[1]);
                pFirstName = ((String) o[2]);
                pLastName = ((String) o[3]);
                pInitials = null;
                if (o[4] != null) {
                    pInitials = ((String) o[4]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials));
                p.setCurrentPeriodGross((Double) o[7]);

                p.setLastPeriodGross((Double) o[8]);
                p.setAgency((String) o[9]);
                p.setGrossDifference(p.getCurrentPeriodGross() - p.getLastPeriodGross());
                p.setPeriod(currMonthStart);

                if (decrease && p.getGrossDifference() < 0) {
                    wRetList.add(p);
                } else if (!decrease && p.getGrossDifference() > 0) {
                    wRetList.add(p);
                }


                //wRetList.add(p);
            }

            Collections.sort(wRetList, Comparator.comparing(VariationReportBean::getEmployeeName));
        }


       // LocalDate currPayrollRunDate = getPayrollRunDateByPayPeriod(currMonthStart, bc);
      //  LocalDate prevPayrollRunDate = getPayrollRunDateByPayPeriod(wPrevMonthStart, bc);

        HashMap<Long, HiringInfoAudit> wRetMap = new HashMap<>();
        String hQl = "select a.id, a.columnChanged, a.lastModTs, h." + bc.getEmployeeIdJoinStr() + ", h.id from HiringInfoAudit a, HiringInfo h where ( a.auditPayPeriod = :startDate or a.auditPayPeriod = :endDate) "
                + " and a.hireInfo.id = h.id and (a.columnChanged = 'Hiring Date' or a.columnChanged= 'Date of Birth') and a.businessClientId = :pBizIdVar ";
        Query wQ = this.sessionFactory.getCurrentSession().createQuery(hQl);
        wQ.setParameter("startDate", PayrollUtils.makeAuditPayPeriod(prevRunMonth,prevRunYear));
        wQ.setParameter("endDate", PayrollUtils.makeAuditPayPeriod(runMonth,runYear));
        wQ.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wList = (ArrayList<Object[]>) wQ.list();

        for (Object[] o : wList) {
            HiringInfoAudit wHireAudit = new HiringInfoAudit();
            wHireAudit.setId((Long) o[0]);
            wHireAudit.setColumnChanged((String) o[1]);
            wHireAudit.setLastModTs((LocalDate) o[2]);
            wHireAudit.setHireInfo(new HiringInfo((Long) o[4]));
            wHireAudit.getHireInfo().setEmployee((new Employee((Long) o[3])));
            //wRetList.add(wHireAudit);
            wRetMap.put(wHireAudit.getHireInfo().getEmployee().getId(), wHireAudit);

        }

        List<Long> wReabsorbedIds;
        String hQl1 = "select distinct e.id "
                + "from AbsorptionLog b, " + IppmsUtils.getEmployeeTableName(bc) + " e, HiringInfo h "
                + "where b.auditPayPeriod = :pPayPeriodVar "
                + "and b." + bc.getEmployeeIdJoinStr() + " = e.id "
                + "and h." + bc.getEmployeeIdJoinStr() + " = e.id and b.businessClientId = :pBizIdVar "
                + "and h.suspensionDate is null ";
        Query wQ1 = this.sessionFactory.getCurrentSession().createQuery(hQl1);
        wQ1.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(runMonth, runYear));
        wQ1.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        wReabsorbedIds = wQ1.list();

        List<Long> wSuspendedIds;
        String hQl2 = "select distinct e.id "
                + "from SuspensionLog b, " + IppmsUtils.getEmployeeTableName(bc) + " e, HiringInfo h "
                + "where b.auditPayPeriod = :pPayPeriodVar "
                + "and b." + bc.getEmployeeIdJoinStr() + " = e.id "
                + "and h." + bc.getEmployeeIdJoinStr() + " = e.id and b.businessClientId = :pBizIdVar "
                + "and h.suspensionDate is not null ";
        Query wQ2 = this.sessionFactory.getCurrentSession().createQuery(hQl2);
        wQ2.setParameter("pPayPeriodVar", PayrollUtils.makeAuditPayPeriod(runMonth, runYear));
        wQ2.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        wSuspendedIds = wQ2.list();


        for (VariationReportBean p : wRetList) {
            if (!wRetMap.containsKey(p.getEmployeeInstId()) && !wReabsorbedIds.contains(p.getEmployeeInstId()) && !wSuspendedIds.contains(p.getEmployeeInstId())) {
                wActRetList.add(p);
            }
        }


        return wRetList;

    }

    public SummaryPage reconciliationReport(LocalDate startCal, LocalDate endCal, BusinessCertificate bc)
            throws Exception {

        List<SummaryPage> wRetList = new ArrayList<>();
        SummaryPage wRetVal = new SummaryPage();

        LocalDate wPrevMonthStart = PayrollBeanUtils.getPreviousMonthDate(startCal,
                false);
        LocalDate wPrevMonthEnd = PayrollBeanUtils.getPreviousMonthDate(endCal, true);
        int prevRunMonth = wPrevMonthStart.getMonthValue();
        int prevRunYear = wPrevMonthStart.getYear();

        LocalDate currMonthStart = startCal;
        // Date currMonthEnd = endCal.getTime();
        int runMonth = currMonthStart.getMonthValue();
        int runYear = currMonthStart.getYear();

        LocalDate prevMonth;
        prevMonth = wPrevMonthStart;

        // LocalDate currPayrollRunDate = getPayrollRunDateByPayPeriod(currMonthStart, bc);
        // LocalDate prevPayrollRunDate = getPayrollRunDateByPayPeriod(wPrevMonthStart, bc);

        String currPayrollRunDate = PayrollUtils.makeAuditPayPeriod(runMonth, runYear);
        String prevPayrollRunDate = PayrollUtils.makeAuditPayPeriod(prevRunMonth, prevRunYear);

        Integer thisMonthStaffCount = this.countEmpForPayrollByMonthAndYear(
                runMonth, runYear, bc);
        Integer prevMonthStaffCount = this.countEmpForPayrollByMonthAndYear(
                prevRunMonth, prevRunYear, bc);
        Integer staffCountDifference = thisMonthStaffCount
                - prevMonthStaffCount;

        wRetVal.setCurrStaffCount(thisMonthStaffCount);
        wRetVal.setPrevStaffCount(prevMonthStaffCount);
        wRetVal.setStaffCountDifference(staffCountDifference);


        Double thisMonthGrossPay = (Double) this.runQuery(
                "select sum(p.totalPay) from " + IppmsUtils.getPaycheckTableName(bc) + " p where p.runMonth = "
                        + runMonth + " and " + " p.runYear = " + runYear, true);
        if (IppmsUtils.isNull(thisMonthGrossPay)) {
            thisMonthGrossPay = 0.0D;
        }

        Double prevMonthGrossPay = (Double) this.runQuery(
                "select sum(p.totalPay) from " + IppmsUtils.getPaycheckTableName(bc) + " p where p.runMonth = "
                        + prevRunMonth + " and " + " p.runYear = "
                        + prevRunYear, true);
        if (IppmsUtils.isNull(prevMonthGrossPay)) {
            prevMonthGrossPay = 0.0D;
        }
        // Double prevMonthGrossPay =
        // this.getGrossPaySumForRunMonthAndRunYear(prevRunMonth,
        // prevRunYear).doubleValue();

        Double grossPayDifference = thisMonthGrossPay - prevMonthGrossPay;

        wRetVal.setCurrGrossSum(thisMonthGrossPay);
        wRetVal.setPrevGrossSum(prevMonthGrossPay);
        wRetVal.setGrossSumDifference(grossPayDifference);

        /* Total Allowance */
        Double thisMonthTotalAllowance = (Double) this.runQuery(
                "select sum(p.totalAllowance) from " + IppmsUtils.getPaycheckTableName(bc) + " p where p.runMonth = "
                        + runMonth + " and " + " p.runYear = " + runYear, true);

        if (IppmsUtils.isNull(thisMonthTotalAllowance)) {
            thisMonthTotalAllowance = 0.0D;
        }

        Double prevMonthTotalAllowance = (Double) this.runQuery(
                "select sum(p.totalAllowance) from " + IppmsUtils.getPaycheckTableName(bc) + " p where p.runMonth = "
                        + prevRunMonth + " and " + " p.runYear = "
                        + prevRunYear, true);

        if (IppmsUtils.isNull(prevMonthTotalAllowance)) {
            prevMonthTotalAllowance = 0.0D;
        }

        Double totalAllowanceDifference = thisMonthTotalAllowance
                - prevMonthTotalAllowance;

        wRetVal.setCurrTotalAllowance(thisMonthTotalAllowance);
        wRetVal.setPrevTotalAllowance(prevMonthTotalAllowance);
        wRetVal.setTotalAllowanceDifference(totalAllowanceDifference);

        /* Basic Salary Sum */
        Double thisMonthBasicSal = 0.0D;
        Double prevMonthBasicSal = 0.0D;

        thisMonthBasicSal = thisMonthGrossPay - thisMonthTotalAllowance;
        prevMonthBasicSal = prevMonthGrossPay - prevMonthTotalAllowance;

        wRetVal.setCurrBasicSalary(thisMonthBasicSal);
        wRetVal.setPrevBasicSalary(prevMonthBasicSal);
        Double basicSalDifference = thisMonthBasicSal - prevMonthBasicSal;
        wRetVal.setBasicSalaryDifference(basicSalDifference);

        /* Tax */
        Double thisMonthTax = (Double) this.runQuery(
                "select sum(p.taxesPaid) from " + IppmsUtils.getPaycheckTableName(bc) + " p where p.runMonth = "
                        + runMonth + " and " + " p.runYear = " + runYear, true);
        if (IppmsUtils.isNull(thisMonthTax)) {
            thisMonthTax = 0.0D;
        }
        Double prevMonthTax = (Double) this.runQuery(
                "select sum(p.taxesPaid) from " + IppmsUtils.getPaycheckTableName(bc) + " p where p.runMonth = "
                        + prevRunMonth + " and " + " p.runYear = "
                        + prevRunYear, true);
        if (IppmsUtils.isNull(prevMonthTax)) {
            prevMonthTax = 0.0D;
        }
        Double taxDifference = thisMonthTax - prevMonthTax;

        wRetVal.setCurrTaxSum(thisMonthTax);
        wRetVal.setPrevTaxSum(prevMonthTax);
        wRetVal.setTaxSumDifference(taxDifference);

        /* Total Deductions */
        Double thisMonthTotalDeductions = (Double) this.runQuery(
                "select sum(p.totalDeductions) from " + IppmsUtils.getPaycheckTableName(bc) + " p where p.runMonth = "
                        + runMonth + " and " + " p.runYear = " + runYear, true);
        if (IppmsUtils.isNull(thisMonthTotalDeductions)) {
            thisMonthTotalDeductions = 0.0D;
        }
        Double prevMonthTotalDeductions = (Double) this.runQuery(
                "select sum(p.totalDeductions) from " + IppmsUtils.getPaycheckTableName(bc) + " p where p.runMonth = "
                        + prevRunMonth + " and " + " p.runYear = "
                        + prevRunYear, true);
        if (IppmsUtils.isNull(prevMonthTotalDeductions)) {
            prevMonthTotalDeductions = 0.0D;
        }
        Double totalDeductionDifference = thisMonthTotalDeductions
                - prevMonthTotalDeductions;

        wRetVal.setCurrTotalDeductions(thisMonthTotalDeductions);
        wRetVal.setPrevTotalDeductions(prevMonthTotalDeductions);
        wRetVal.setTotalDeductionsDifference(totalDeductionDifference);

        // wRetVal.setSalaryIncreaseDifference(0.0D);

        Double thisMonthOtherDeductions = thisMonthTotalDeductions
                - thisMonthTax;
        Double prevMonthOtherDeductions = prevMonthTotalDeductions
                - prevMonthTax;
        Double otherDeductionDifference = thisMonthOtherDeductions
                - prevMonthOtherDeductions;

        wRetVal.setCurrOtherDeductions(thisMonthOtherDeductions);
        wRetVal.setPrevOtherDeductions(prevMonthOtherDeductions);
        wRetVal.setOtherDeductionsDifference(otherDeductionDifference);

        /* Net Pay */
        Double thisMonthNetPay = (Double) this.runQuery(
                "select sum(p.netPay) from " + IppmsUtils.getPaycheckTableName(bc) + " p where p.runMonth = "
                        + runMonth + " and " + " p.runYear = " + runYear, true);
        if (IppmsUtils.isNull(thisMonthNetPay)) {
            thisMonthNetPay = 0.0D;
        }
        Double prevMonthNetPay = (Double) this.runQuery(
                "select sum(p.netPay) from " + IppmsUtils.getPaycheckTableName(bc) + " p where p.runMonth = "
                        + prevRunMonth + " and " + " p.runYear = "
                        + prevRunYear, true);
        if (IppmsUtils.isNull(prevMonthNetPay)) {
            prevMonthNetPay = 0.0D;
        }
        Double netPayDifference = thisMonthNetPay - prevMonthNetPay;

        wRetVal.setCurrNetPay(thisMonthNetPay);
        wRetVal.setPrevNetPay(prevMonthNetPay);
        wRetVal.setNetPayDifference(netPayDifference);

        List<VariationReportBean> wCurrMonthList = (List<VariationReportBean>) this
                .getEmployeePayBeanListByDate(runMonth, runYear, bc);
        List<VariationReportBean> wPrevMonthList = (List<VariationReportBean>) this
                .getEmployeePayBeanListByDate(prevRunMonth, prevRunYear, bc);

        HashMap<Long, VariationReportBean> wCurrPayMap = getVarRepBeanMap(wCurrMonthList);
        HashMap<Long, VariationReportBean> wPrevPayMap = getVarRepBeanMap(wPrevMonthList);

        //  HashMap<Long, HiringInfoAudit> wHireAuditMap = getHireInfoAuditByDate(bc,
        //        prevPayrollRunDate, currPayrollRunDate);

        List<Long> wSalDiffList = (List<Long>) this.sessionFactory.getCurrentSession().createQuery(
                "select p.employee.id from " + IppmsUtils.getPaycheckTableName(bc) + " p, " + IppmsUtils.getPaycheckTableName(bc) + " p0 where p.runMonth = "
                        + runMonth
                        + " and p.runYear = "
                        + runYear
                        + ""
                        + " and p0.runMonth = "
                        + prevRunMonth
                        + " and p0.runYear = "
                        + prevRunYear
                        + " and p.totalPay != p0.totalPay and p.employee.id = p0.employee.id ")
                .list();

        Double newStaffGrossSum = 0.0D;
        Double promotedStaffGrossDifference = 0.0D;
        Double reassignedStaffGrossDifference = 0.0D;
        Double reinstatedStaffGrossDifference = 0.0D;
        Double reabsorbedStaffGrossDifference = 0.0D;
        Double terminatedStaffGrossDifference = 0.0D;
        Double pMonthTerminatedStaffGrossDifference = 0.0D;
        Double suspendedStaffGrossDifference = 0.0D;
        Double stepIncrementImplications = 0.0D;

        Double specAllowDifference = 0.0D;
        Double specAllowDecrease = 0.0D;

        int newStaffCount = 0;
        int promotedEmpCount = 0;
        int reassignedEmpCount = 0;
        int reinstatedEmpCount = 0;
        int reabsorbedEmpCount = 0;
        int terminatedEmpCount = 0;
        int prevTerminatedEmpCount = 0;
        int suspendedEmpCount = 0;
        int specAllowDiffCount = 0;
        int stepIncrementCount = 0;
        int specAllowDecreaseCount = 0;

        ArrayList<Long> wReinstatedList = (ArrayList<Long>) this
                .getEmployeeListByDateValues(bc,
                        currPayrollRunDate, "ReinstatementLog",
                        "auditPayPeriod");
        ArrayList<Long> wReabsorbedList = (ArrayList<Long>) this
                .getEmployeeListByDateValues(bc,
                        currPayrollRunDate, "AbsorptionLog", "auditPayPeriod");
        ArrayList<Long> wSuspendedList = (ArrayList<Long>) this
                .getEmployeeListByDateValues(bc,
                        currPayrollRunDate, "SuspensionLog", "auditPayPeriod");

        ArrayList<Long> wNewEmpList = (ArrayList<Long>) this
                .getNewEmployeesByDateValues(bc,
                        currPayrollRunDate, IppmsUtils.getEmployeeAuditTable(bc), "auditPayPeriod");

        ArrayList<Long> wContractedEmpList = (ArrayList<Long>) this
                .getContractedEmployeesByDateValues(bc,
                        currPayrollRunDate, IppmsUtils.getEmployeeAuditTable(bc), "auditPayPeriod");

        // Load the DTO Objects for Current Month. It must have TotalPay,
        // SalaryInfoInstId, Employee ID and Names,
        // EmployeeInstId...VarRepMiniBean
        // Load the DTO Objects for Prev Month....
        // Iterate through Current Month....then create methods to determine
        // what should explain variance...Allah ba Musa!
        List<String> wPromotedEmpList = new ArrayList<>();
        List<String> wReinsList = new ArrayList<>();
        List<String> wSpecList = new ArrayList<>();
        List<String> wSpecDecList = new ArrayList<>();

        List<String> wReassignedList = new ArrayList<>();

        List<String> wReabsList = new ArrayList<>();
        List<String> wTermList = new ArrayList<>();
        List<String> wTermList1 = new ArrayList<>();
        List<String> wSuspList = new ArrayList<>();

        for (VariationReportBean p : wCurrMonthList) {

           /* if(p.getEmployeeId().equalsIgnoreCase("OG24583")){
                System.out.println("[ Debug Guy ]");
            }
*/
            VariationReportBean prevPayBean = new VariationReportBean();
            Long empId = p.getEmployeeInstId();
            String employeeId = p.getEmployeeId();
            if (wPrevPayMap.containsKey(empId)) {
                prevPayBean = wPrevPayMap.get(empId);

				/*if (p.getSalaryInfoInstId().intValue() != prevPayBean
						.getSalaryInfoInstId().intValue()) {
					if (p.getSalaryTypeId() != prevPayBean.getSalaryTypeId()) {
						wReassignedList.add(employeeId);
						wSalDiffList.remove(new Integer(empId));
						reassignedStaffGrossDifference += (p.getTotalPay() - prevPayBean
								.getTotalPay());
					} else {
						wPromotedEmpList.add(employeeId);
						wSalDiffList.remove(new Integer(empId));
						promotedStaffGrossDifference += (p.getTotalPay() - prevPayBean
								.getTotalPay());
					}

				}*/

                if (p.getSalaryInfoInstId().intValue() != prevPayBean.getSalaryInfoInstId().intValue()
                        && p.getTotalPay() > 0 && prevPayBean.getTotalPay() > 0) {
                    if (p.getSalaryTypeId() != prevPayBean.getSalaryTypeId()) {

                        wReassignedList.add(employeeId);
                        wSalDiffList.remove(empId);
                        reassignedStaffGrossDifference += (p.getTotalPay() - prevPayBean.getTotalPay());
                        reassignedEmpCount++;
                    } else {
                        if (p.getLevel() < prevPayBean.getLevel() || (p.getLevel() == prevPayBean.getLevel() && p.getStep() < prevPayBean.getStep())) {

                            wReassignedList.add(employeeId);
                            wSalDiffList.remove(empId);
                            reassignedStaffGrossDifference += (p.getTotalPay() - prevPayBean.getTotalPay());
                            reassignedEmpCount++;
                        } else if (p.getLevel() == prevPayBean.getLevel() && (p.getStep() - prevPayBean.getStep() > 0)) {
                            //wPromotedEmpList.add(employeeId);
                            wSalDiffList.remove(empId);
                            stepIncrementImplications += (p.getTotalPay() - prevPayBean.getTotalPay());
                            stepIncrementCount++;
                        } else {

                            wPromotedEmpList.add(employeeId);
                            wSalDiffList.remove(empId);
                            promotedStaffGrossDifference += (p.getTotalPay() - prevPayBean.getTotalPay());
                            promotedEmpCount++;
                        }
                    }


                }

                if (wSalDiffList.contains(empId)
                        && wReabsorbedList.contains(empId) && p.getTotalPay() > prevPayBean
                        .getTotalPay() && p.getPayByDaysInd() != 1) {
                    wSalDiffList.remove(empId);
                    wReabsList.add(employeeId);
                    reabsorbedStaffGrossDifference += (p.getTotalPay() - prevPayBean
                            .getTotalPay());
                    reabsorbedEmpCount++;
                }

                if (wSalDiffList.contains(empId)
                        && wSuspendedList.contains(empId) && p.getTotalPay() < prevPayBean
                        .getTotalPay()) {
                    wSalDiffList.remove(empId);
                    wSuspList.add(employeeId);
                    suspendedStaffGrossDifference += (prevPayBean.getTotalPay() - p
                            .getTotalPay());
                    suspendedEmpCount++;
                }
                if (wSalDiffList.contains(empId)
                        && wReinstatedList.contains(empId) && p.getTotalPay() > 0) {
                    wSalDiffList.remove(empId);
                    wReinsList.add(employeeId);
                    reinstatedStaffGrossDifference += (p.getTotalPay() - prevPayBean
                            .getTotalPay());
                    reinstatedEmpCount++;

                }

//                if (wSalDiffList.contains(empId)
//                        && (wReinstatedList.contains(empId) || wHireAuditMap
//                        .containsKey(empId) || wContractedEmpList.contains(empId)) && p.getTotalPay() > 0) {
//                    wSalDiffList.remove(empId);
//                    wReinsList.add(employeeId);
//                    reinstatedStaffGrossDifference += (p.getTotalPay() - prevPayBean
//                            .getTotalPay());
//                    reinstatedEmpCount++;
//                    //System.out.println(empId + " : " + employeeId + " :First Loop " + reinstatedStaffGrossDifference);
//
//                }

                if (wSalDiffList.contains(empId)
                        && checkIfTerminated(bc, currMonthStart, wPrevMonthEnd,
                        empId)) {
                    wSalDiffList.remove(empId);
                    wTermList.add(employeeId);
                    terminatedStaffGrossDifference += (prevPayBean
                            .getTotalPay() - p.getTotalPay());
                    terminatedEmpCount++;
                    //System.out.println(empId + " : " + employeeId + " :First Loop Term " + terminatedStaffGrossDifference + "count " + i++);

                }

                if (wSalDiffList.contains(empId) && p.getPayByDaysInd() == 1) {
                    wSalDiffList.remove(empId);
                    wTermList.add(prevPayBean.getEmployeeId() + "pbd");
                    terminatedStaffGrossDifference += (prevPayBean
                            .getTotalPay() - p.getTotalPay());
                    terminatedEmpCount++;
                    //System.out.println(empId + " : " + employeeId + " :Second Loop Term " + terminatedStaffGrossDifference + "count " + i++);

                }

                if (wSalDiffList.contains(empId)
                        && (p.getTotalPay() == 0 && prevPayBean.getTotalPay() > 0)) {
                    wSalDiffList.remove(empId);
                    wTermList.add(prevPayBean.getEmployeeId() + "--");
                    terminatedStaffGrossDifference += prevPayBean.getTotalPay();
                    terminatedEmpCount++;
                    //System.out.println(empId + " : " + employeeId + " :Third Loop Term " + terminatedStaffGrossDifference + "count " + i++);

                }

                if (wSalDiffList.contains(empId)
                        && (prevPayBean.getTotalPay() == 0 && p.getTotalPay() > 0)) {
                    wSalDiffList.remove(empId);
                    wReinsList.add(p.getEmployeeId());
                    reinstatedStaffGrossDifference += p.getTotalPay();
                    reinstatedEmpCount++;
                    //System.out.println(empId + " : " + employeeId + " :Second Loop " + reinstatedStaffGrossDifference);

                }

                if (wSalDiffList.contains(empId)
                        && p.getSpecialAllowanceAmount() > prevPayBean
                        .getSpecialAllowanceAmount()) {
                    wSalDiffList.remove(empId);
                    wSpecList.add(p.getEmployeeId());
                    specAllowDifference += (p.getTotalPay() - prevPayBean
                            .getTotalPay());
                    specAllowDiffCount++;
                }

                if (wSalDiffList.contains(empId)
                        && p.getSpecialAllowanceAmount() < prevPayBean
                        .getSpecialAllowanceAmount()) {
                    wSalDiffList.remove(empId);
                    wSpecDecList.add(p.getEmployeeId());
                    specAllowDecrease += (prevPayBean
                            .getTotalPay() - p.getTotalPay());
                    specAllowDecreaseCount++;
                    //System.out.println(p.getEmployeeId() + ",");
                }


                if (wSalDiffList.contains(empId) && prevPayBean.getPayByDaysInd() == 1) {

                    wSalDiffList.remove(empId);
                    wReinsList.add(prevPayBean.getEmployeeId() + "pbd");
                    reinstatedStaffGrossDifference += (p.getTotalPay() - prevPayBean.getTotalPay());
                    reinstatedEmpCount++;
                    //System.out.println(empId + " : " + employeeId + " :Third Loop " + reinstatedStaffGrossDifference);

                    //reinstatedEmpCount++;
                    //System.out.println(empId + " : " + employeeId + " :Third Loop " + hireInfoChangeGrossDifference);


                }

            }

            if (prevPayBean.isNewEntity() && wNewEmpList.contains(p.getEmployeeInstId())
					/*&& (p.getCreatedDate().after(prevPayrollRunDate) && p
							.getCreatedDate().before(currPayrollRunDate))*/) {
                newStaffGrossSum += p.getTotalPay();
                newStaffCount++;
            } else if (prevPayBean.isNewEntity()) {
                wReinsList.add(p.getEmployeeId() + ":new");
                reinstatedStaffGrossDifference += p.getTotalPay();
                reinstatedEmpCount++;
                //System.out.println(empId + " : " + employeeId + " :Fourth Loop " + reinstatedStaffGrossDifference);

            }
        }

        for (VariationReportBean p0 : wPrevMonthList) {

            if (!wCurrPayMap.containsKey(p0.getEmployeeInstId()) && p0.getTotalPay() > 0) {
                // report them differently....
                pMonthTerminatedStaffGrossDifference += p0.getTotalPay();
                wTermList1.add(p0.getEmployeeId() + "_term");
                prevTerminatedEmpCount++;
                //System.out.println(p0.getEmployeeInstId() + " : " + p0.getEmployeeId() + " :First Loop Prev Term " + pMonthTerminatedStaffGrossDifference + "count " + j++);

            }
        }

        wRetVal.setNewStaffGross(newStaffGrossSum);
        wRetVal.setPromotedStaffGrossDifference(promotedStaffGrossDifference);
        wRetVal.setReinstatedStaffGrossDifference(reinstatedStaffGrossDifference);
        wRetVal.setReassignedStaffGrossDifference(reassignedStaffGrossDifference);
        wRetVal.setTerminatedStaffGrossDifference(terminatedStaffGrossDifference);
        wRetVal.setPrevTerminatedStaffGrossDifference(pMonthTerminatedStaffGrossDifference);
        wRetVal.setSuspendedStaffGrossDifference(suspendedStaffGrossDifference);
        wRetVal.setReabsorbedStaffGrossDifference(reabsorbedStaffGrossDifference);
        wRetVal.setSpecAllowChange(specAllowDifference);
        wRetVal.setSpecAllowDecrease(specAllowDecrease);
        wRetVal.setStepIncrementGrossDifference(stepIncrementImplications);

        Double incrSubTotal = 0.0D;
        Double decrSubTotal = 0.0D;
        incrSubTotal = newStaffGrossSum + promotedStaffGrossDifference +
                reinstatedStaffGrossDifference + reassignedStaffGrossDifference +
                reabsorbedStaffGrossDifference + specAllowDifference + stepIncrementImplications;

        decrSubTotal = terminatedStaffGrossDifference + pMonthTerminatedStaffGrossDifference + suspendedStaffGrossDifference + specAllowDecrease;

        wRetVal.setIncrementSubTotal(incrSubTotal);
        wRetVal.setDecrementSubTotal(decrSubTotal);

        wRetVal.setNewStaffCount(newStaffCount);
        wRetVal.setPromotedEmpCount(promotedEmpCount);
        wRetVal.setReassignedEmpCount(reassignedEmpCount);
        wRetVal.setReinstatedEmpCount(reinstatedEmpCount);
        wRetVal.setReabsorbedEmpCount(reabsorbedEmpCount);
        wRetVal.setTerminatedEmpCount(terminatedEmpCount);
        wRetVal.setPrevTerminatedEmpCount(prevTerminatedEmpCount);
        wRetVal.setSuspendedEmpCount(suspendedEmpCount);
        wRetVal.setSpecAllowDiffCount(specAllowDiffCount);
        wRetVal.setStepIncrementCount(stepIncrementCount);
        wRetVal.setSpecAllowDecreaseCount(specAllowDecreaseCount);

        wRetVal.setPeriod(currMonthStart);

        wRetList.add(wRetVal);

        return wRetVal;
    }

    public Integer countEmpForPayrollByMonthAndYear(int pRunMonth, int pRunYear, BusinessCertificate bc) {
        String hql = "select count(p.employee.id) from " + IppmsUtils.getPaycheckTableName(bc) + " p where p.runMonth = :runMonth and p.runYear = :runYear and p.totalPay > 0";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("runMonth", pRunMonth);
        query.setParameter("runYear", pRunYear);

        Long retVal = (Long) query.uniqueResult();
        return retVal.intValue();
    }

    public Object runQuery(String hql, boolean unique) {
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        Object retVal;
        if (unique)
            retVal = query.uniqueResult();
        else
            retVal = query.list();
        return retVal;
    }

    public Object getEmployeePayBeanListByDate(int currRunMonth, int currRunYear, BusinessCertificate bc) {
        List<VariationReportBean> wRetList = new ArrayList<>();
        // int noOfDaysInMonth = PayrollBeanUtils.getNoOfDays(currRunMonth,
        // currRunYear);

        String hQl = "select e.id, e.employeeId, e.firstName, e.lastName,e.initials, p.totalPay, s.monthlyBasicSalary, "
                + "p.totalAllowance, p.salaryInfo.id, p.payPercentageInd,"
                + " p.payByDaysInd, h.terminateDate, h.suspensionDate, e.creationDate, p.noOfDays, p.payPercentage, "
                + "s.salaryType.id, s.level, s.step, p.specialAllowance  "
                + " from " + IppmsUtils.getPaycheckTableName(bc) + " p, " + IppmsUtils.getEmployeeTableName(bc) + " e, HiringInfo h, SalaryInfo s where p.runMonth = :runMonth and p.runYear = :runYear "
                + " and p.employee.id = e.id and e.id = h." + bc.getEmployeeIdJoinStr()
                + " and p.salaryInfo.id = s.id and e.businessClientId = :pBizIdVar ";
        Query wQ = this.sessionFactory.getCurrentSession().createQuery(hQl);
        wQ.setParameter("runMonth", currRunMonth);
        wQ.setParameter("runYear", currRunYear);
        wQ.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wList = (ArrayList<Object[]>) wQ.list();

        for (Object[] o : wList) {
            VariationReportBean vrp = new VariationReportBean();
            String initials = "";

            vrp.setEmployeeInstId((Long) o[0]);
            vrp.setId(vrp.getEmployeeInstId());
            vrp.setEmployeeId((String) o[1]);
            if (o[4] != null)
                initials = (String) o[4];
            vrp.setEmployeeName(o[3] + ", " + o[2] + " "
                    + initials);

            vrp.setTotalPay((Double) o[5]);
            // vrp.setBasicSalary((Double) o[6]);
            vrp.setTotalAllowance((Double) o[7]);
            vrp.setSalaryInfoInstId((Long) o[8]);
            vrp.setPayByDaysInd((Integer) o[10]);
            vrp.setPayPercentageInd((Integer) o[9]);
            vrp.setTerminateDate((LocalDate) o[11]);
            vrp.setSuspensionDate((LocalDate) o[12]);
//            vrp.setCreatedDate((LocalDate) o[13]);
            vrp.setSalaryTypeId((Long) o[16]);
            vrp.setLevel((Integer) o[17]);
            vrp.setStep((Integer) o[18]);

            vrp.setSpecialAllowanceAmount(((Double) o[19]).doubleValue());


            // vrp.setBasicSalary((Double) basicPay);
            vrp.setBasicSalary(vrp.getTotalPay() - vrp.getTotalAllowance());
            wRetList.add(vrp);
        }

        return wRetList;
    }

    public boolean checkIfTerminated(BusinessCertificate bc, LocalDate startDate, LocalDate endDate, Long empId) {
        boolean answer = false;
        String hQl = "";
        if (bc.isPensioner()) {
            hQl = "select e.pensioner.id from HiringInfo e where e.pensionEndDate is not null and e.businessClientId = :pBizIdVar "
                    + "and e.pensionEndDate > :startDate and e.pensionEndDate <= :endDate and e.pensioner.id = :empId and e.businessClientId = :pBizIdVar ";
        } else {
            hQl = "select e.employee.id from HiringInfo e where e.terminateDate is not null and e.businessClientId = :pBizIdVar "
                    + "and e.terminateDate > :startDate and e.terminateDate <= :endDate and e.employee.id = :empId";
        }

        Query wQ = this.sessionFactory.getCurrentSession().createQuery(hQl);
        wQ.setParameter("startDate", startDate);
        wQ.setParameter("endDate", endDate);
        wQ.setParameter("empId", empId);
        wQ.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        if (wQ.uniqueResult() != null)
            answer = true;
        return answer;
    }

    public List<Long> getEmployeeListByDateValues(BusinessCertificate bc, String pCurrentPayPeriod,
                                                  String tableName, String column) {

        List<Long> wRetList = new ArrayList<>();
        String hQl = "select distinct(e." + bc.getEmployeeIdJoinStr() + ") from " + tableName
                + " e where e.auditPayPeriod = :pPayPeriodVar and e.businessClientId = :pBizIdVar ";
        Query wQ = this.sessionFactory.getCurrentSession().createQuery(hQl);
        wQ.setParameter("pPayPeriodVar", pCurrentPayPeriod);
        wQ.setParameter("pBizIdVar", bc.getBusinessClientInstId());
        ArrayList<Long> wArr = (ArrayList<Long>) wQ.list();
        for (Long o : wArr) {
            wRetList.add(o);
        }
        return wRetList;

    }

    public List<Long> getNewEmployeesByDateValues(BusinessCertificate bc, String pCurrentPayPeriod,
                                                  String tableName, String column) {

        List<Long> wRetList = new ArrayList<>();
        String hQl = "select distinct(e." + bc.getEmployeeIdJoinStr() + ") from " + tableName
                + " e where e.auditPayPeriod = :pPayPeriodVar and auditActionType = 'I' and e.businessClientId = :pBizIdVar ";
        Query wQ = this.sessionFactory.getCurrentSession().createQuery(hQl);
        wQ.setParameter("pPayPeriodVar", pCurrentPayPeriod);
        wQ.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Long> wArr = (ArrayList<Long>) wQ.list();
        for (Long o : wArr) {
            wRetList.add(o);
        }
        return wRetList;
    }

    public List<Long> getContractedEmployeesByDateValues(BusinessCertificate bc, String pCurrentPayPeriod,
                                                         String tableName, String column) {

        List<Long> wRetList = new ArrayList<>();
        String hQl = "select distinct(e." + bc.getEmployeeIdJoinStr() + ") from " + tableName
                + " e where e.auditPayPeriod = :pPayPeriodVar and auditActionType = 'I' and e.businessClientId = :pBizIdVar ";
        Query wQ = this.sessionFactory.getCurrentSession().createQuery(hQl);
        wQ.setParameter("pPayPeriodVar", pCurrentPayPeriod);
        wQ.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Long> wArr = (ArrayList<Long>) wQ.list();
        for (Long o : wArr) {
            wRetList.add(o);
        }
        return wRetList;
    }

    public HashMap<Long, VariationReportBean> getVarRepBeanMap(
            List<VariationReportBean> payList) {
        HashMap<Long, VariationReportBean> payMap = new HashMap<>();
        for (VariationReportBean p : payList) {
            payMap.put(p.getEmployeeInstId(), p);
        }
        return payMap;
    }

    public HashMap<Long, PromotionAudit> getStepIncrementLog(BusinessCertificate bc, String pPayPeriod){
        HashMap<Long,PromotionAudit> wRetMap = new HashMap<>();
        String sql1 = "select p.promotionDate, user.firstName, user.lastName,p.employee.id from " + IppmsUtils.getPromotionAuditTable(bc) + " p, User user "
                + "where p.auditPayPeriod = :pAuditPeriod and p.user.id = user.id and p.businessClientId = :pBizIdVar and p.stepIncrementInd = 1 ";

        Query wQ1 = this.sessionFactory.getCurrentSession().createQuery(sql1);
        wQ1.setParameter("pAuditPeriod", pPayPeriod);
        wQ1.setParameter("pBizIdVar", bc.getBusinessClientInstId());
        ArrayList<Object[]> wList = (ArrayList<Object[]>) wQ1.list();
        PromotionAudit p;
        Long eid;
        for (Object[] o : wList) {
            p = new PromotionAudit();
            p.setPromotionDate((LocalDate) o[0]);
            p.setUserName(o[1] + " " + o[2]);
            eid = (Long)o[3];
            wRetMap.put(eid,p);
        }

        return wRetMap;

    }
    public HashMap<Long, HiringInfoAudit> getHireInfoAuditByDate(BusinessCertificate bc,
                                                                 String startDate, String endDate) {
        // List<HiringInfoAudit> wRetList = new ArrayList<HiringInfoAudit>();
        HashMap<Long, HiringInfoAudit> wRetMap = new HashMap<>();
        String hQl = "select a.id, a.columnChanged, a.lastModTs, h." + bc.getEmployeeIdJoinStr() + ", h.id " +
                "from HiringInfoAudit a, HiringInfo h where (a.auditPayPeriod = :startDate or a.auditPayPeriod = :endDate) "
                + " and a.hireInfo.id = h.id and (a.columnChanged = 'Hiring Date' or a.columnChanged= 'Date of Birth')  and a.businessClientId = :pBizIdVar ";
        Query wQ = this.sessionFactory.getCurrentSession().createQuery(hQl);
        wQ.setParameter("startDate", startDate);
        wQ.setParameter("endDate", endDate);
        wQ.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wList = (ArrayList<Object[]>) wQ.list();
        HiringInfoAudit wHireAudit;
        for (Object[] o : wList) {
            wHireAudit = new HiringInfoAudit();
            wHireAudit.setId((Long) o[0]);
            wHireAudit.setColumnChanged((String) o[1]);
            wHireAudit.setLastModTs((LocalDate) o[2]);
            wHireAudit.setHireInfo(new HiringInfo((Long) o[4]));
            wHireAudit.getHireInfo().setEmployee(new Employee((Long) o[3]));
            // wRetList.add(wHireAudit);
            wRetMap.put(wHireAudit.getHireInfo().getEmployee().getId(), wHireAudit);

        }

        return wRetMap;
    }

}
