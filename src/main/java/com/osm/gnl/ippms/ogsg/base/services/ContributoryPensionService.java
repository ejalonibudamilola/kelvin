package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpDeductMiniBean;
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

@Service("contPenService")
@Repository
@Transactional(readOnly = true)
public class ContributoryPensionService {

    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public List<EmpDeductMiniBean> loadPensionContributions(int pStartRow,int pEndRow, String pSortOrder, String pSortCriterion,
                                                            LocalDate pStartDate, LocalDate pEndDate,
                                                            Long pPfaInstId, Long pMdaInstId, boolean pMustSum,Long pEmpId, boolean pForExcelExport, BusinessCertificate bc) {
        String wHql = "";
        boolean wUsePfaId = IppmsUtils.isNotNullAndGreaterThanZero(pPfaInstId);
        boolean wUseMdaId = IppmsUtils.isNotNullAndGreaterThanZero(pMdaInstId);
        boolean wUseEmpId = IppmsUtils.isNotNullAndGreaterThanZero(pEmpId);

        boolean wUseDates = true;

        int wRunMonth = 0;
        int wRunMonth2 = 0;
        int wRunYear = 0;
        int wRunYear2 = 0;

        if(wUseEmpId){
            if(pStartDate != null && pEndDate != null){
                wRunMonth = pStartDate.getMonthValue();
                wRunMonth2 = pEndDate.getMonthValue();
                wRunYear = pStartDate.getYear();
                wRunYear2 = pEndDate.getYear();
            }else{
                wUseDates = false;
            }
        }else{
            wRunMonth = pStartDate.getMonthValue();
            wRunMonth2 = pEndDate.getMonthValue();
            wRunYear = pStartDate.getYear();
            wRunYear2 = pEndDate.getYear();
        }

        if(pMustSum){

            if(!wUseMdaId){
                wHql = "select e.lastName,e.firstName, e.initials,e.employeeId, emp.pensionPinCode, emp.mdaDeptMap.id, "
                        + "m.id,m.name,m.codeName,p.name,sum(emp.contributoryPension),coalesce(emp.pfaInfo.id,1020),emp.payPeriodStart "
                        + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp, MdaInfo m, PfaInfo p where emp.employee.id = e.id and "
                        + "emp.pfaInfo.id = p.id and emp.mdaDeptMap.mdaInfo.id = m.id and emp.pfaInfo.id is not null  "
                        + "and ((emp.runMonth >= :pRunMonth and emp.runYear = :pRunYear) "
                        +"or (emp.runMonth <= :pRunMonth2 and emp.runYear = :pRunYear2) or  (emp.runMonth >= 0 and emp.runYear > :pRunYear and emp.runYear < :pRunYear2)) "
                        +"and emp.contributoryPension > 0 ";


                if(wUseEmpId){
                    if(!wUseDates){
                        wHql = "select e.lastName,e.firstName, e.initials,e.employeeId, emp.pensionPinCode, emp.mdaDeptMap.id, "
                                + "m.id,m.name,m.codeName,p.name,sum(emp.contributoryPension),coalesce(emp.pfaInfo.id,1020),emp.payPeriodStart "
                                + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,MdaInfo m, PfaInfo p where emp.employee.id = e.id and "
                                + "emp.pfaInfo.id = p.id and m.id = emp.mdaDeptMap.mdaInfo.id and emp.pfaInfo.id is not null  "
                                +"and emp.contributoryPension > 0 ";
                        if(pForExcelExport){
                            wHql = "select e.lastName,e.firstName, e.initials,e.employeeId, emp.pensionPinCode, emp.mdaDeptMap.id, "
                                    + "m.id,m.name,m.codeName,p.name, emp.contributoryPension,coalesce(emp.pfaInfo.id,1020),emp.payPeriodStart  "
                                    + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,MdaInfo m, PfaInfo p where emp.employee.id = e.id and "
                                    + "emp.pfaInfo.id = p.id and m.id = emp.mdaDeptMap.mdaInfo.id "
                                    +"and emp.contributoryPension > 0 ";
                        }
                    }
                    if(pForExcelExport){
                        wHql = "select e.lastName,e.firstName, e.initials,e.employeeId, emp.pensionPinCode, emp.mdaDeptMap.id, "
                                + "m.id,m.name,m.codeName,p.name,emp.contributoryPension,coalesce(emp.pfaInfo.id,1020),emp.payPeriodStart "
                                + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,MdaInfo m, PfaInfo p where emp.employee.id = e.id and "
                                + "emp.pfaInfo.id = p.id and m.id = emp.mdaDeptMap.mdaInfo.id "
                                + "and ((emp.runMonth >= :pRunMonth and emp.runYear = :pRunYear) "
                                +"or (emp.runMonth <= :pRunMonth2 and emp.runYear = :pRunYear2) or  (emp.runMonth >= 0 and emp.runYear > :pRunYear and emp.runYear < :pRunYear2)) "
                                +"and emp.contributoryPension > 0 ";
                    }
                    wHql += "and e.id = :pEmpIdVar ";

                }

                if(wUsePfaId)
                    wHql += "and p.id = :pPfaIdValue ";
            } else {

                if (wUseMdaId) {
                    wHql = "select e.lastName,e.firstName, e.initials,e.employeeId, emp.pensionPinCode, emp.mdaDeptMap.id, "
                            + "m.id,m.name, m.codeName,p.name, emp.contributoryPension ,coalesce(emp.pfaInfo.id,1020),emp.payPeriodStart "
                            + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,MdaInfo m, PfaInfo p  "
                            + "where emp.employee.id = e.id and m.id = emp.mdaDeptMap.mdaInfo.id  "
                            +" and m.id = :pMdaInstIdValue and emp.pfaInfo.id = p.id    "
                            + "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.contributoryPension > 0 ";

                    if(wUseEmpId){
                        if(!wUseDates){
                            wHql = "select e.lastName,e.firstName, e.initials,e.employeeId, emp.pensionPinCode, emp.mdaDeptMap.id, "
                                    + "m.id,m.name, m.codeName,p.name, emp.contributoryPension,coalesce(emp.pfaInfo.id,1020),emp.payPeriodStart  "
                                    + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,MdaInfo  m, PfaInfo p "
                                    + "where emp.employee.id = e.id and m.id = emp.mdaDeptMap.mdaInfo.id  "
                                    +"and m.id = :pMdaInstIdValue and  emp.pfaInfo.id = p.id   "
                                    + "and emp.contributoryPension > 0 ";
                        }
                        wHql += "and e.id = :pEmpIdVar ";
                    }
                    if(wUsePfaId)
                        wHql += "and p.id = :pPfaIdValue ";


                } else {
                    wHql = "select e.lastName,e.firstName, e.initials,e.employeeId, emp.pensionPinCode, emp.mdaDeptMap.id, "
                            + "m.id,m.name,m.codeName,p.name,sum(emp.contributoryPension),coalesce(emp.pfaInfo.id,1020),emp.payPeriodStart "
                            + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,MdaInfo m, PfaInfo p where emp.employee.id = e.id and "
                            + "m.id = emp.mdaDeptMap.mdaInfo.id and emp.pfaInfo.id = p.id   "
                            + "and ((emp.runMonth >= :pRunMonth and emp.runYear = :pRunYear) "
                            + "or (emp.runMonth <= :pRunMonth2 and emp.runYear = :pRunYear2) or  (emp.runMonth >= 0 and emp.runYear > :pRunYear and emp.runYear < :pRunYear2)) "
                            + "and emp.contributoryPension > 0 ";

                    if (wUseEmpId) {
                        if (!wUseDates) {
                            wHql = "select e.lastName,e.firstName, e.initials,e.employeeId, emp.pensionPinCode, emp.mdaDeptMap.id, "
                                    + "m.id,m.name,m.codeName,p.name,sum(emp.contributoryPension),coalesce(emp.pfaInfo.id,1020),emp.payPeriodStart "
                                    + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,MdaInfo m, PfaInfo p where emp.employee.id = e.id and "
                                    + "m.id = emp.mdaDeptMap.mdaInfo.id and emp.pfaInfo.id = p.id and h.pfaInfo.id is not null  "
                                    + "and emp.contributoryPension > 0 ";
                        }
                        if (pForExcelExport) {
                            wHql = "select e.lastName,e.firstName, e.initials,e.employeeId, emp.pensionPinCode, emp.mdaDeptMap.id, "
                                    + "m.id,m.name,m.codeName,p.name, emp.contributoryPension,coalesce(emp.pfaInfo.id,1020),emp.payPeriodStart "
                                    + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,MdaInfo m, PfaInfo p where emp.employee.id = e.id and "
                                    + "m.id = emp.mdaDeptMap.mdaInfo.id and emp.pfaInfo.id = p.id  "
                                    + "and emp.contributoryPension > 0 ";
                        }
                        wHql += "and e.id = :pEmpIdVar ";
                    }
                    if (wUsePfaId)
                        wHql += "and p.id = :pPfaIdValue ";
                }

            }
            if(!(wUseEmpId && pForExcelExport)){
                wHql += "group by e.lastName,e.firstName, e.initials,e.employeeId, emp.mdaDeptMap.id, m.id, m.name,m.codeName,p.name,coalesce(emp.pfaInfo.id,1020),emp.payPeriodStart";

            }


        }else{

            if(!wUseMdaId){
                wHql = "select e.lastName,e.firstName, e.initials,e.employeeId, emp.pensionPinCode, emp.mdaDeptMap.id, "
                        + "m.id, m.name,m.codeName,p.name, emp.contributoryPension,coalesce(emp.pfaInfo.id,1020),emp.payPeriodStart  "
                        + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,MdaInfo m, PfaInfo p where emp.employee.id = e.id and "
                        + "emp.pfaInfo.id = p.id  and emp.mdaDeptMap.mdaInfo.id = m.id  "
                        + "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.contributoryPension > 0 ";
                if(wUseEmpId){
                    if(!wUseDates){
                        wHql = "select e.lastName,e.firstName, e.initials,e.employeeId, emp.pensionPinCode, emp.mdaDeptMap.id, "
                                + "m.id, m.name,m.codeName,p.name, emp.contributoryPension ,coalesce(emp.pfaInfo.id,1020),emp.payPeriodStart "
                                + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,MdaInfo m, PfaInfo p where emp.employee.id = e.id and "
                                + "emp.pfaInfo.id = p.id and emp.mdaDeptMap.mdaInfo.id = m.id   "
                                + "and emp.contributoryPension > 0 ";
                    }
                    wHql += "and e.id = :pEmpIdVar ";
                }
                if(wUsePfaId)
                    wHql += "and p.id = :pPfaIdValue ";
            }else{


                if(wUseMdaId){ //This takes care of the situation where pObjectInd somehow is not in the range of 1-4...

                    wHql = "select e.lastName,e.firstName, e.initials,e.employeeId, emp.pensionPinCode, emp.mdaDeptMap.id, "
                            + "m.id,m.name, m.codeName,p.name, emp.contributoryPension ,coalesce(emp.pfaInfo.id,1020),emp.payPeriodStart "
                            + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,MdaInfo m, PfaInfo p  "
                            + "where emp.employee.id = e.id and m.id = emp.mdaDeptMap.mdaInfo.id  "
                            +" and m.id = :pMdaInstIdValue and emp.pfaInfo.id = p.id    "
                            + "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.contributoryPension > 0 ";

                    if(wUseEmpId){
                        if(!wUseDates){
                            wHql = "select e.lastName,e.firstName, e.initials,e.employeeId, emp.pensionPinCode, emp.mdaDeptMap.id, "
                                    + "m.id,m.name, m.codeName,p.name, emp.contributoryPension,coalesce(emp.pfaInfo.id,1020),emp.payPeriodStart  "
                                    + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,MdaInfo  m, PfaInfo p "
                                    + "where emp.employee.id = e.id and m.id = emp.mdaDeptMap.mdaInfo.id  "
                                    +"and m.id = :pMdaInstIdValue and  emp.pfaInfo.id = p.id   "
                                    + "and emp.contributoryPension > 0 ";
                        }
                        wHql += "and e.id = :pEmpIdVar ";
                    }
                    if(wUsePfaId)
                        wHql += "and p.id = :pPfaIdValue ";

                }else{
                    wHql = "select e.lastName,e.firstName, e.initials,e.employeeId, emp.pensionPinCode, emp.mdaDeptMap.id, "
                            + "m.id,m.name, m.codeName,p.name,p.name,emp.contributoryPension,coalesce(emp.pfaInfo.id,1020),emp.payPeriodStart "
                            + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,MdaInfo m, PfaInfo p where emp.employee.id = e.id and "
                            + "emp.pfaInfo.id = p.id and m.id = emp.mdaDeptMap.mdaInfo.id  "
                            + "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.contributoryPension > 0 ";

                    if(wUseEmpId){
                        if(!wUseDates){
                            wHql = "select e.lastName,e.firstName, e.initials,e.employeeId, emp.pensionPinCode, emp.mdaDeptMap.id, "
                                    + "m.id,m.name, m.codeName,p.name,emp.contributoryPension,coalesce(emp.pfaInfo.id,1020),emp.payPeriodStart "
                                    + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,MdaInfo m, PfaInfo p where emp.employee.id = e.id  "
                                    + "and emp.pfaInfo.id = p.id and emp.mdaDeptMap.mdaInfo.id = m.id "
                                    + "and emp.contributoryPension > 0 ";
                        }
                        wHql += "and e.id = :pEmpIdVar ";
                    }
                    if(wUsePfaId)
                        wHql += "and p.id = :pPfaIdValue ";
                }
            }


        }

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        if(wUseDates){
            wQuery.setParameter("pRunMonth", wRunMonth);
            wQuery.setParameter("pRunYear", wRunYear);
        }

        if(pMustSum){
            if(wUseDates){
                wQuery.setParameter("pRunMonth2", wRunMonth2);
                wQuery.setParameter("pRunYear2", wRunYear2);
            }

        }
        if(wUsePfaId)
            wQuery.setParameter("pPfaIdValue", pPfaInstId);
        if(wUseMdaId){

            wQuery.setParameter("pMdaInstIdValue", pMdaInstId);
        }

        if(wUseEmpId){
            wQuery.setParameter("pEmpIdVar", pEmpId);
        }
        if(!pForExcelExport){
            if (pStartRow > 0) {
                wQuery.setFirstResult(pStartRow);
                wQuery.setMaxResults(pEndRow);
            }
        }
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if (wRetVal.size() > 0) {
            List wRetList = new ArrayList();
            EmpDeductMiniBean p;
            int i = 0;
            for (Object[] o : wRetVal) {
                p = new EmpDeductMiniBean();
                p.setName(PayrollHRUtils.createDisplayName((String) o[i++],
                        (String) o[i++], StringUtils.trimToEmpty((String) o[i++])));
                p.setEmployeeId((String) o[i++]);
                p.setDetails((String) o[i++]);
                p.setMdaDeptMap(new MdaDeptMap((Long) o[i++]));
                p.getMdaDeptMap().setMdaInfo(new MdaInfo((Long) o[i++],(String) o[i++],(String) o[i++]));
                p.setDeductionCode((String) o[i++]);
                p.setCurrentDeduction(((Double) o[i++]));
                p.setPfaId((Long)o[i++]);
                if(wUseEmpId)
                    p.setPayPeriodStr(PayrollHRUtils.getMonthYearDateFormat().format((LocalDate)o[i++]));

                wRetList.add(p);
                i = 0;
            }

            return wRetList;
        }
        return new ArrayList();

    }

    public List<EmpDeductMiniBean> loadPensionContributionByRunMonthAndYear(int pStartRow, int pEndRow, String pSortOrder, String pSortCriterion,
                                                                            int pRunMonth, int pRunYear, BusinessCertificate bc){
        String wHql = "select e.firstName,e.lastName, e.initials,e.employeeId, emp.mdaDeptMap.id, mda.id, mda.name, " +
                "p.name,emp.contributoryPension " +
                "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,HiringInfo h, PfaInfo p, MdaDeptMap m, MdaInfo mda"
                + " where emp.employee.id = e.id and m.mdaInfo.id = mda.id and emp.mdaDeptMap.id = m.id and  " +
                " h.employee.id = e.id and h.pfaInfo.id = p.id and h.pfaInfo.id is not null and emp.contributoryPension > 0 " +
                "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);

        if (pStartRow > 0)
            wQuery.setFirstResult(pStartRow);
        wQuery.setMaxResults(pEndRow);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        if (wRetVal.size() > 0) {
            EmpDeductMiniBean p = null;
            int i = 0;
            List wRetList = new ArrayList();
            for (Object[] o : wRetVal) {
                p = new EmpDeductMiniBean();


                String pFirstName = (String)o[i++];
                String pLastName = (String)o[i++];
                String pInitials = StringUtils.trimToEmpty((String)o[i++]);

                p.setName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials));
                p.setEmployeeId((String)o[i++]);
                p.setMdaInstId((Long)o[i++]);
                p.setMdaDeptMap(new MdaDeptMap(p.getMdaInstId(),(Long)o[i++] ,(String)o[i++]));

                p.setDeductionCode((String)o[7]);
                p.setCurrentDeduction(((Double)o[8]));
                wRetList.add(p);
                i = 0;
            }

            return wRetList;
        }
        return new ArrayList();
    }

    public int getTotalSumPensionContributions(LocalDate pStartDate,
                                               LocalDate pEndDate, Long pPfaInstId, Long pMdaInstId,
                                               boolean pMustSum, Long pEmpId, BusinessCertificate bc) {

        String wHql = "";
        boolean wUsePfaId = pPfaInstId > 0;
        boolean wUseMdaId = pMdaInstId > 0;
        boolean wUseEmpId = pEmpId > 0;
        boolean wUseDates = true;

        int wRunMonth = 0;
        int wRunMonth2 = 0;
        int wRunYear = 0;
        int wRunYear2 = 0;

        if(wUseEmpId){
            if(pStartDate != null && pEndDate != null){
                wRunMonth = pStartDate.getMonthValue();
                wRunMonth2 = pEndDate.getMonthValue();
                wRunYear = pStartDate.getYear();
                wRunYear2 = pEndDate.getYear();
            }else{
                wUseDates = false;
            }
        }else{
            wRunMonth = pStartDate.getMonthValue();
            wRunMonth2 = pEndDate.getMonthValue();
            wRunYear = pStartDate.getYear();
            wRunYear2 = pEndDate.getYear();
        }
        if(pMustSum){

            if(!wUseMdaId){
                wHql = "select e.id,sum(emp.contributoryPension) "
                        + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,HiringInfo h, PfaInfo p where emp.employee.id = e.id and "
                        + "h.employee.id = e.id and h.pfaInfo.id = p.id and h.pfaInfo.id is not null  "
                        + "and ((emp.runMonth >= :pRunMonth and emp.runYear = :pRunYear) "
                        +"or (emp.runMonth <= :pRunMonth2 and emp.runYear = :pRunYear2) or  (emp.runMonth >= 0 and emp.runYear > :pRunYear and emp.runYear < :pRunYear2)) "
                        +"and emp.contributoryPension > 0 ";
                if(wUseEmpId){
                    if(!wUseDates){
                        wHql = "select e.id,sum(emp.contributoryPension) "
                                + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,HiringInfo h, PfaInfo p where emp.employee.id = e.id and "
                                + "h.employee.id = e.id and h.pfaInfo.id = p.id and h.pfaInfo.id is not null  "
                                +"and emp.contributoryPension > 0 ";
                    }
                    wHql += "and e.id = :pEmpIdVar ";
                }

                if(wUsePfaId)
                    wHql += "and p.id = :pPfaIdValue ";
            }else{

                if(wUseMdaId){ //This takes care of the situation where pObjectInd somehow is not in the range of 1-4...

                    wHql = "select e.id,sum(emp.contributoryPension) "
                            + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,HiringInfo h, PfaInfo p, MdaInfo m "
                            + "where emp.employee.id = e.id and  emp.mdaDeptMap.mdaInfo.id = m.id  "
                            +"and m.id = :pMdaInstIdValue and "
                            + "emp.pfaInfo.id = p.id "
                            + "and ((emp.runMonth >= :pRunMonth and emp.runYear = :pRunYear) "
                            +"or (emp.runMonth <= :pRunMonth2 and emp.runYear = :pRunYear2) or  (emp.runMonth >= 0 and emp.runYear > :pRunYear and emp.runYear < :pRunYear2)) "
                            +"and emp.contributoryPension > 0 ";
                    if(wUseEmpId){
                        if(!wUseDates){
                            wHql = "select e.id,sum(emp.contributoryPension) "
                                    + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp, PfaInfo p,  MdaInfo m "
                                    + "where emp.employee.id = e.id  and emp.mdaDeptMap.mdaInfo.id = m.id  "
                                    +"and m.id = :pMdaInstIdValue and "
                                    + "emp.pfaInfo.id = p.id and emp.contributoryPension > 0 ";
                        }
                        wHql += "and e.id = :pEmpIdVar ";
                    }

                    if(wUsePfaId)
                        wHql += "and p.id = :pPfaIdValue ";

                }else{
                    wHql = "select e.id,sum(emp.contributoryPension) "
                            + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,PfaInfo p where emp.employee.id = e.id and "
                            + "emp.pfaInfo.id = p.id "
                            + "and ((emp.runMonth >= :pRunMonth and emp.runYear = :pRunYear) "
                            +"or (emp.runMonth <= :pRunMonth2 and emp.runYear = :pRunYear2) or  (emp.runMonth >= 0 and emp.runYear > :pRunYear and emp.runYear < :pRunYear2)) "
                            +"and emp.contributoryPension > 0 ";

                    if(wUseEmpId){
                        if(!wUseDates){
                            wHql = "select e.id,sum(emp.contributoryPension) "
                                    + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,  PfaInfo p where emp.employee.id = e.id and "
                                    + "emp.pfaInfo.id = p.id and emp.contributoryPension > 0 ";
                        }
                        wHql += "and e.id = :pEmpIdVar ";
                    }

                    if(wUsePfaId)
                        wHql += "and p.id = :pPfaIdValue ";
                }
            }


            wHql += "group by e.id ";


        }else{


            if(!wUseMdaId){
                wHql = "select e.firstName,e.lastName, e.initials,e.employeeId, emp.mdaDeptMap.id, "
                        + "p.name, emp.contributoryPension  "
                        + "from Employee e, EmployeePayBean emp,PfaInfo p where emp.employee.id = e.id and "
                        + "emp.pfaInfo.id = p.id and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.contributoryPension > 0 ";

                if(wUseEmpId){
                    if(!wUseDates){
                        wHql = "select e.firstName,e.lastName, e.initials,e.employeeId, emp.mdaDeptMap.id, "
                                + "p.name, emp.contributoryPension  "
                                + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,PfaInfo p where emp.employee.id = e.id and "
                                + "emp.pfaInfo.id = p.id and emp.contributoryPension > 0 ";

                    }
                    wHql += "and e.id = :pEmpIdVar ";
                }


                if(wUsePfaId)
                    wHql += "and p.id = :pPfaIdValue ";
            }else{

                if(wUseMdaId){ //This takes care of the situation where pObjectInd somehow is not in the range of 1-4...

                    wHql = "select e.firstName,e.lastName, e.initials,e.employeeId, emp.mdaDeptMap.id, "
                            + "p.name, emp.contributoryPension  "
                            + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,HiringInfo h, PfaInfo p, MdaInfo m "
                            + "where emp.employee.id = e.id and  emp.mdaDeptMap.mdaInfo.id = m.id  "
                            +"and m.id = :pMdaInstIdValue and emp.pfaInfo.id = p.id and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.contributoryPension > 0 ";

                    if(wUseEmpId){
                        if(!wUseDates){
                            wHql = "select e.firstName,e.lastName, e.initials,e.employeeId, emp.mdaDeptMap.id, "
                                    + "p.name, emp.contributoryPension  "
                                    + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,HiringInfo h, PfaInfo p, MdaInfo m "
                                    + "where emp.employee.id = e.id and  emp.mdaDeptMap.mdaInfo.id = m.id  "
                                    +"and m.id = :pMdaInstIdValue and emp.pfaInfo.id = p.id "
                                    + "and emp.contributoryPension > 0 ";

                        }
                        wHql += "and e.id = :pEmpIdVar ";
                    }

                    if(wUsePfaId)
                        wHql += "and p.id = :pPfaIdValue ";

                }else{
                    wHql = "select e.firstName,e.lastName, e.initials,e.employeeId, emp.mdaDeptMap.id, "
                            + "p.name,emp.contributoryPension "
                            + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,  PfaInfo p where emp.employee.id = e.id and "
                            + "emp.pfaInfo.id = p.id   "
                            + "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.contributoryPension > 0 ";

                    if(wUseEmpId){
                        if(!wUseDates){
                            wHql = "select e.firstName,e.lastName, e.initials,e.employeeId, emp.mdaDeptMap.id, "
                                    + "p.name,emp.contributoryPension "
                                    + "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,  PfaInfo p where emp.employee.id = e.id and "
                                    + "emp.pfaInfo.id = p.id and emp.contributoryPension > 0 ";

                        }
                        wHql += "and e.id = :pEmpIdVar ";
                    }


                    if(wUsePfaId)
                        wHql += "and p.id = :pPfaIdValue ";
                }
            }


        }

        Query wQuery =this.sessionFactory.getCurrentSession().createQuery(wHql);
        if(wUseDates){
            wQuery.setParameter("pRunMonth", wRunMonth);
            wQuery.setParameter("pRunYear", wRunYear);
        }
        if(pMustSum){
            if(wUseDates){
                wQuery.setParameter("pRunMonth2", wRunMonth2);
                wQuery.setParameter("pRunYear2", wRunYear2);
            }
        }
        if(wUsePfaId)
            wQuery.setParameter("pPfaIdValue", pPfaInstId);
        if(wUseMdaId){

            wQuery.setParameter("pMdaInstIdValue", pMdaInstId);
        }
        if(wUseEmpId){
            wQuery.setParameter("pEmpIdVar", pEmpId);
        }

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if(wRetVal == null || wRetVal.isEmpty() || wRetVal.get(0) == null)
            return 0;
        else
            return wRetVal.size();
    }

    public List<EmpDeductMiniBean> loadTPSEmployeeByRunMonthAndYear(boolean forExcel, int pStartRow, int pEndRow, String pSortOrder, String pSortCriterion,
                                                                    int pRunMonth, int pRunYear, Long pPfaInstId, Long pMdaInstId, boolean pUseRule,
                                                                    boolean pIncludeTerminated, boolean pTps, BusinessCertificate bc) {

        String wHql = "";

        String wTpsPartForRule = " and h.expectedDateOfRetirement is not null "
                + "and (h.expectedDateOfRetirement <= :pTPSEndDate and h.hireDate <= :pTPSStartDate) ";

        String wCpsPartForRule = " and h.expectedDateOfRetirement is not null "
                + "and ( not (h.expectedDateOfRetirement <= :pTPSEndDate and h.hireDate <= :pTPSStartDate)) ";

        String wTpsPartNoRule = " and emp.contributoryPension = 0 ";
        String wCpsPartNoRule = " and emp.contributoryPension > 0 ";

        if (pMdaInstId > 0) {


            if (pUseRule) {
                wHql = "select emp.netPay,emp.suspendedInd,e.firstName,e.lastName, e.initials,e.employeeId, emp.mdaDeptMap.id, " +
                        "a.id,a.name,a.codeName, emp.contributoryPension , h.expectedDateOfRetirement, h.hireDate, h.birthDate,s.level,s.step,h.terminateDate,st.name " +
                        "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,HiringInfo h, SalaryInfo s, SalaryType st,MdaDeptMap adm, MdaInfo a"
                        + " where emp.employee.id = e.id and s.salaryType.id = st.id and " +
                        " h.employee.id = e.id " +
                        "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.salaryInfo.id = s.id";
                if (pTps) {
                    wHql += wTpsPartForRule;

                } else {
                    wHql += wCpsPartForRule;
                }
                wHql += "and adm.id = emp.mdaDeptMap.id and adm.mdaInfo.id = a.id and a.id = " + pMdaInstId + " ";

            } else {
                wHql = "select emp.netPay,emp.suspendedInd,e.firstName,e.lastName, e.initials,e.employeeId, emp.mdaDeptMap.id, " +
                        ".id,a.name,a.codeName, emp.contributoryPension , h.expectedDateOfRetirement, h.hireDate, h.birthDate,s.level,s.step,h.terminateDate,st.name " +
                        "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,HiringInfo h, SalaryInfo s,SalaryType st, MdaDeptMap adm, MdaInfo a "
                        + " where emp.employee.id = e.id and s.salaryType.id = st.id and  " +
                        " h.employee.id = e.id " +
                        "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.salaryInfo.id = s.id";

                if (pTps) {
                    wHql += wTpsPartNoRule;

                } else {
                    wHql += wCpsPartNoRule;
                }

                wHql += "and adm.id = emp.mdaDeptMap.id and adm.mdaInfo.id = a.id and a.id = " + pMdaInstId + " ";
            }


        } else {
            if (pUseRule) {
                wHql = "select emp.netPay,emp.suspendedInd,e.firstName,e.lastName, e.initials,e.employeeId, emp.mdaDeptMap.id, " +
                        "m.id,m.name,m.codeName, emp.contributoryPension , h.expectedDateOfRetirement, h.hireDate, h.birthDate,s.level,s.step,h.terminateDate,st.name " +
                        "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,HiringInfo h, SalaryInfo s,SalaryType st, MdaInfo m, MdaDeptMap mdm "
                        + " where emp.employee.id = e.id and s.salaryType.id = st.id and  " +
                        " h.employee.id = e.id and m.id = mdm.mdaInfo.id and emp.mdaDeptMap.id = mdm.id " +
                        "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.salaryInfo.id = s.id";
                if (pTps) {
                    wHql += wTpsPartForRule;

                } else {
                    wHql += wCpsPartForRule;
                }


            } else {
                wHql = "select emp.netPay,emp.suspendedInd,e.firstName,e.lastName, e.initials,e.employeeId, emp.mdaDeptMap.id, " +
                        "m.id,m.name,m.codeName, emp.contributoryPension , h.expectedDateOfRetirement, h.hireDate, h.birthDate,s.level,s.step,h.terminateDate,st.name " +
                        "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" emp,HiringInfo h, SalaryInfo s,SalaryType st,MdaInfo m, MdaDeptMap mdm "
                        + " where emp.employee.id = e.id and s.salaryType.id = st.id and  " +
                        " h.employee.id = e.id and m.id = mdm.mdaInfo.id and emp.mdaDeptMap.id = mdm.id " +
                        "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.salaryInfo.id = s.id ";
                if (pTps) {
                    wHql += wTpsPartNoRule;

                } else {
                    wHql += wCpsPartNoRule;
                }
            }


        }
        if (!pIncludeTerminated)
            wHql += " and h.terminateDate is null and emp.netPay > 0 ";
        if (pPfaInstId != null && pPfaInstId > 0)
            wHql += " and h.pfaInfo.id = " + pPfaInstId;

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);

        if (pUseRule) {
            wQuery.setParameter("pTPSEndDate", PayrollBeanUtils.setDateFromString(IConstants.TPS_EXP_RETIRE_DATE_STR));
            wQuery.setParameter("pTPSStartDate", PayrollBeanUtils.setDateFromString(IConstants.TPS_HIRE_DATE_STR));
        }

        if (!forExcel) {
            if (pStartRow > 0)
                wQuery.setFirstResult(pStartRow);
            wQuery.setMaxResults(pEndRow);
        }

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if (wRetVal.size() > 0) {
            List wRetList = new ArrayList();
            for (Object[] o : wRetVal) {
                EmpDeductMiniBean p = new EmpDeductMiniBean();
                int i = 0;
                double wNetPay = (Double) o[i++];
                int wSuspInd = (Integer) o[i++];
                String pFirstName = (String) o[i++];
                String pLastName = (String) o[i++];
                String pInitials = StringUtils.trimToEmpty((String) o[i++]);

                if (wNetPay == 0) {
                    if (wSuspInd == 1) {
                        p.setName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials) + "*");
                        p.setErrorRecord(true);
                    } else {
                        p.setName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials) + "**");
                        p.setErrorRecord(true);
                    }

                } else
                    p.setName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials));

                p.setEmployeeId((String) o[i++]);
                p.setMdaDeptMap(new MdaDeptMap((Long) o[i++]));
                p.getMdaDeptMap().setMdaInfo(new MdaInfo((Long) o[i++], (String) o[i++], (String) o[i++]));

                p.setCurrentDeduction(((Double) o[i++]));
                p.setExpDateOfRetirement((LocalDate) o[i++]);
                p.setHireDate((LocalDate) o[i++]);
                p.setBirthDate((LocalDate) o[i++]);
                p.setLevel((Integer) o[i++]);
                p.setStep((Integer) o[i++]);
                p.setTerminateDate((LocalDate) o[i++]);
                p.setSalaryInfoDesc((String) o[i++]);
                wRetList.add(p);
            }

            return wRetList;
        }
        return new ArrayList();

    }

    public int getTotalNoOfTPSEmployeesByRunMonthAndRunYear(int pRunMonth,
                                                            int pRunYear, Long pPfaInstId, Long pMdaInstId, boolean pUseRule,
                                                            boolean pIncludeTerminated, boolean pTps) {

        int wRetVal = 0;
        boolean useMda = IppmsUtils.isNotNullAndGreaterThanZero(pMdaInstId);

        List list = null;
        String wHql = "";
        String wTpsPartForRule = " and h.expectedDateOfRetirement is not null "
                + "and (h.expectedDateOfRetirement <= :pTPSEndDate and h.hireDate <= :pTPSStartDate) ";

        String wCpsPartForRule = " and h.expectedDateOfRetirement is not null "
                + "and ( not (h.expectedDateOfRetirement <= :pTPSEndDate and h.hireDate <= :pTPSStartDate)) ";

        String wTpsPartNoRule = " and emp.contributoryPension = 0 ";
        String wCpsPartNoRule = " and emp.contributoryPension > 0 ";
        if (useMda) {


            if (pUseRule) {
                wHql = "select count(e.id) from Employee e, EmployeePayBean emp,HiringInfo h, SalaryInfo s, MdaDeptMap adm, MdaInfo a"
                        + " where emp.employee.id = e.id and " +
                        " h.employee.id = e.id " +
                        "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.salaryInfo.id = s.id"
                        + " and h.expectedDateOfRetirement is not null ";
                if (pTps) {
                    wHql += wTpsPartForRule;

                } else {
                    wHql += wCpsPartForRule;
                }

                wHql += "and adm.id = emp.mdaDeptMap.id and adm.mdaInfo.id = a.id and a.id = " + pMdaInstId + " ";

            } else {
                wHql = "select count(e.id) from Employee e, EmployeePayBean emp,HiringInfo h, SalaryInfo s, MdaDeptMap adm, MdaInfo a "
                        + " where emp.employee.id = e.id and " +
                        " h.employee.id = e.id " +
                        "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.salaryInfo.id = s.id";
                if (pTps) {
                    wHql += wTpsPartNoRule;

                } else {
                    wHql += wCpsPartNoRule;
                }
                wHql += "and adm.id = emp.mdaDeptMap.id  and adm.mdaInfo.id = a.id and a.id = " + pMdaInstId + " ";
            }


        } else {
            if (pUseRule) {
                wHql = "select count(e.id) from Employee e, EmployeePayBean emp,HiringInfo h, SalaryInfo s "
                        + " where emp.employee.id = e.id and " +
                        " h.employee.id = e.id " +
                        "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.salaryInfo.id = s.id"
                        + " and h.expectedDateOfRetirement is not null ";
                if (pTps) {
                    wHql += wTpsPartForRule;

                } else {
                    wHql += wCpsPartForRule;
                }


            } else {
                wHql = "select count(e.id) from Employee e, EmployeePayBean emp,HiringInfo h, SalaryInfo s "
                        + " where emp.employee.id = e.id and " +
                        " h.employee.id = e.id " +
                        "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.salaryInfo.id = s.id";
                if (pTps) {
                    wHql += wTpsPartNoRule;

                } else {
                    wHql += wCpsPartNoRule;
                }

            }


        }
        if (!pIncludeTerminated) {
            wHql += " and h.terminateDate is null and emp.netPay > 0";
        }
        if (pPfaInstId > 0)
            wHql += " and h.pfaInfo.id = " + pPfaInstId;

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);
        if (pUseRule) {
            wQuery.setParameter("pTPSEndDate", PayrollBeanUtils.setDateFromString(IConstants.TPS_EXP_RETIRE_DATE_STR));
            wQuery.setParameter("pTPSStartDate", PayrollBeanUtils.setDateFromString(IConstants.TPS_HIRE_DATE_STR));
        }

        list = wQuery.list();

        if ((list == null) || (list.isEmpty())) {
            return 0;
        }
        String wStr = String.valueOf(list.get(0));
        wRetVal = Integer.parseInt(wStr);
        return wRetVal;

    }
}
