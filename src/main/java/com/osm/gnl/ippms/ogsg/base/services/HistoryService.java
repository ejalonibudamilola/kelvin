/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckSpecAllowEntity;
import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.approval.AbstractApprovalEntity;
import com.osm.gnl.ippms.ogsg.domain.approval.EmployeeApproval;
import com.osm.gnl.ippms.ogsg.domain.approval.AmAliveApproval;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckSpecialAllowance;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service("historyService")
@Repository
@Transactional(readOnly = true)
public class HistoryService {


    private final GenericService genericService;
    private final SessionFactory sessionFactory;
    private final IMenuService menuService;

    @Autowired
    public HistoryService(GenericService genericService, SessionFactory sessionFactory, IMenuService menuService) {
        this.genericService = genericService;
        this.sessionFactory = sessionFactory;
        this.menuService = menuService;
    }


    public List<AbstractPaycheckSpecAllowEntity> loadEmpSpecialAllowancesByEmpIdAndId(BusinessCertificate bc,int pStartRow, int pEndRow, String sortOrder, String sortCriterion, Long pEmpId, Long pSpecialAllowanceId) {

        String wHql = "select  p.amount,p.runMonth,p.runYear,p.payDate,p.employeePayBean.id,p.payPeriodEnd  "
                + "from "+ IppmsUtils.getPaycheckSpecAllowTableName(bc) +" p where p.employee.id = :pEid and p.specialAllowanceInfo.id = :pGid order by p.employeePayBean.id ";

        Query wQuery = sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pEid", pEmpId);
        wQuery.setParameter("pGid", pSpecialAllowanceId);
        if (pStartRow > 0) {
            wQuery.setFirstResult(pStartRow);
            wQuery.setMaxResults(pEndRow);
        } else {
            wQuery.setMaxResults(pEndRow);
        }

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        if (wRetVal.size() > 0) {
            List wRetList = new ArrayList();
            for (Object[] o : wRetVal) {
                AbstractPaycheckSpecAllowEntity p = new PaycheckSpecialAllowance();
                p.setAmount(((Double)o[0]));
                p.setRunMonth(((Integer)o[1]).intValue());
                p.setRunYear(((Integer)o[2]).intValue());
                p.setPayDate((LocalDate) o[3]);
                p.setParentObjectInstId((Long)o[4]);
                p.setPayPeriodEnd((LocalDate) o[5]);
                if (p.getPayPeriodEnd() != null)
                    p.setPayPeriodStr(PayrollBeanUtils.makePayPeriod(p.getPayPeriodEnd()));
                wRetList.add(p);
            }

            return wRetList;
        }
        return new ArrayList();

    }

    public EmployeeApproval getEmpApprovalPopupValues(EmployeeApproval employeeApproval, BusinessCertificate bc) {

        String wHql = "select b.name,p.accountNumber,coalesce(p.bvnNo,'Not Supplied'),h.birthDate,h.yearlyPensionAmount,h.monthlyPensionAmount  "
                + "from  PaymentMethodInfo p, BankInfo b, HiringInfo h, BankBranch bb where " +
                " p."+bc.getEmployeeIdJoinStr()+" = :pEid and p.bankBranches.id = bb.id and bb.bankInfo.id = b.id " +
                "and p."+bc.getEmployeeIdJoinStr()+" = h."+bc.getEmployeeIdJoinStr()+" and p.businessClientId = h.businessClientId" +
                " and h.businessClientId = :pBizIdVar ";

        Query wQuery = sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pEid", employeeApproval.getChildObject().getId());
        wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());
         ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        if (wRetVal.size() > 0) {
            int i = 0;
            for (Object[] o : wRetVal) {
                employeeApproval.setBankName((String)o[i++]);
                employeeApproval.setAccountNo((String)o[i++]);
                employeeApproval.setBvnNo((String)o[i++]);
                employeeApproval.setBirthDateStr(PayrollHRUtils.getFullDateFormat().format((LocalDate)o[i++]));
                if(bc.isPensioner()){
                    employeeApproval.setYearlySalary(IConstants.naira+PayrollHRUtils.getDecimalFormat().format(o[i++]));
                    employeeApproval.setMonthlySalary(IConstants.naira+PayrollHRUtils.getDecimalFormat().format(o[i++]));

                }
                i = 0;

            }


        }

        return employeeApproval;
    }

    public List<EmployeeApproval> loadPendingEmpApprovals(BusinessCertificate bc, int startRow, int pEndRow, String sortOrder, String sortCriterion) {
        List<EmployeeApproval> wRetList = new ArrayList<>();

        String wHql = "select ea.id,e.id,e.employeeId,e.firstName,e.lastName, e.initials,b.name,p.accountNumber,coalesce(p.bvnNo,'Not Supplied')," +
                "h.birthDate,h.yearlyPensionAmount,h.monthlyPensionAmount ,m.annualGross,st.name,s.level,s.step,u.firstName, u.lastName,u.id,mda.name,ea.lastModTs "
                + "from  EmployeeApproval ea, "+IppmsUtils.getEmployeeTableName(bc)+" e,PaymentMethodInfo p, BankInfo b, HiringInfo h, BankBranch bb,SalaryInfo s, SalaryType st,MiniSalaryInfoDao m," +
                "MdaInfo mda, User u where ea." +bc.getEmployeeIdJoinStr()+" = e.id and e.salaryInfo.id = s.id and s.id = m.salaryInfoId and s.salaryType.id = st.id" +
                " and mda.id = e.mdaDeptMap.mdaInfo.id and u.id = ea.initiator.id and h." +bc.getEmployeeIdJoinStr()+" = e.id and b.id = bb.bankInfo.id and p.bankBranches.id = bb.id "+
                "and p."+bc.getEmployeeIdJoinStr()+" = e.id and ea.businessClientId = :pBizIdVar and ea.approvalStatusInd = 0";

        Query wQuery = sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        if(startRow > 0)
            wQuery.setFirstResult(startRow);
        wQuery.setMaxResults(pEndRow);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        if (wRetVal.size() > 0) {
            int i = 0;
            EmployeeApproval employeeApproval;
            for (Object[] o : wRetVal) {
                employeeApproval = new EmployeeApproval();
                employeeApproval.setId((Long)o[i++]);
                employeeApproval.setEntityId((Long)o[i++]);
                employeeApproval.setEmployeeId((String)o[i++]);
                employeeApproval.setFirstName((String)o[i++]);
                employeeApproval.setLastName((String)o[i++]);
                employeeApproval.setInitials(IppmsUtils.treatNull((String) o[i++]));
                employeeApproval.setBankName((String)o[i++]);
                employeeApproval.setAccountNo((String)o[i++]);
                employeeApproval.setBvnNo((String)o[i++]);
                employeeApproval.setBirthDateStr(PayrollHRUtils.getFullDateFormat().format((LocalDate)o[i++]));
                employeeApproval.setYearlySalary(IConstants.naira+PayrollHRUtils.getDecimalFormat().format(o[i++]));
                employeeApproval.setMonthlySalary(IConstants.naira+PayrollHRUtils.getDecimalFormat().format(o[i++]));
                employeeApproval.setAnnualSalaryStrWivNaira(IConstants.naira+PayrollHRUtils.getDecimalFormat().format(o[i++]));
                employeeApproval.setPayGroup(o[i++]+" : "+ PayrollUtils.makeLevelAndStep((Integer)o[i++],(Integer)o[i++]));
                employeeApproval.setCreatedBy(o[i++] + " "+o[i++]);
                employeeApproval.setInitiatorId((Long)o[i++]);
                employeeApproval.setMdaName((String)o[i++]);
                employeeApproval.setInitiatedDateStr(PayrollHRUtils.getFullDateFormat().format((LocalDate)o[i++]));
                employeeApproval.setDisplayName(PayrollHRUtils.createDisplayName(employeeApproval.getLastName(),employeeApproval.getFirstName(),employeeApproval.getInitials()));
                wRetList.add(employeeApproval);
                i = 0;

            }
        }

        return wRetList;
    }
    public List<AmAliveApproval> loadPendingAmAliveApprovals(BusinessCertificate bc, int startRow, int pEndRow, Long pTicketId) {
        List<AmAliveApproval> wRetList = new ArrayList<>();
        String wHql;
        if(IppmsUtils.isNotNullAndGreaterThanZero(pTicketId)){
            wHql = "select ea.id,e.employeeId,e.firstName,e.lastName, e.initials,b.name,p.accountNumber,coalesce(p.bvnNo,'Not Supplied')," +
                    "h.birthDate,h.yearlyPensionAmount,h.monthlyPensionAmount ,m.annualGross,st.name,s.level,s.step,u.firstName, u.lastName,mda.name,ea.initiatedDate,ea.approvedDate, u2.firstName, u2.lastName "
                    + "from  ApproveAmAlive ea, "+IppmsUtils.getEmployeeTableName(bc)+" e,PaymentMethodInfo p, BankInfo b, HiringInfo h, BankBranch bb,SalaryInfo s, SalaryType st,MiniSalaryInfoDao m," +
                    "MdaInfo mda, User u, User u2 where ea.hiringInfo.id = h.id and e.salaryInfo.id = s.id and s.id = m.salaryInfoId and s.salaryType.id = st.id" +
                    " and mda.id = e.mdaDeptMap.mdaInfo.id and u.id = ea.initiator.id and u2.id = ea.approver.id and h." +bc.getEmployeeIdJoinStr()+" = e.id and b.id = bb.bankInfo.id and p.bankBranches.id = bb.id "+
                    "and p."+bc.getEmployeeIdJoinStr()+" = e.id and ea.businessClientId = :pBizIdVar ";
        }else{
            wHql = "select ea.id,e.employeeId,e.firstName,e.lastName, e.initials,b.name,p.accountNumber,coalesce(p.bvnNo,'Not Supplied')," +
                    "h.birthDate,h.yearlyPensionAmount,h.monthlyPensionAmount ,m.annualGross,st.name,s.level,s.step,u.firstName, u.lastName,mda.name,ea.initiatedDate "
                    + "from  ApproveAmAlive ea, "+IppmsUtils.getEmployeeTableName(bc)+" e,PaymentMethodInfo p, BankInfo b, HiringInfo h, BankBranch bb,SalaryInfo s, SalaryType st,MiniSalaryInfoDao m," +
                    "MdaInfo mda, User u where ea.hiringInfo.id = h.id and e.salaryInfo.id = s.id and s.id = m.salaryInfoId and s.salaryType.id = st.id" +
                    " and mda.id = e.mdaDeptMap.mdaInfo.id and u.id = ea.initiator.id and h." +bc.getEmployeeIdJoinStr()+" = e.id and b.id = bb.bankInfo.id and p.bankBranches.id = bb.id "+
                    "and p."+bc.getEmployeeIdJoinStr()+" = e.id and ea.businessClientId = :pBizIdVar and ea.approvalStatusInd = 0";
        }


        Query wQuery = sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        if(startRow > 0)
            wQuery.setFirstResult(startRow);
        wQuery.setMaxResults(pEndRow);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        if (wRetVal.size() > 0) {
            int i;
            AmAliveApproval employeeApproval;
            for (Object[] o : wRetVal) {
                i = 0;
                employeeApproval = new AmAliveApproval();
                employeeApproval.setId((Long)o[i++]);
                employeeApproval.setEmployeeId((String)o[i++]);
                employeeApproval.setFirstName((String)o[i++]);
                employeeApproval.setLastName((String)o[i++]);
                employeeApproval.setInitials(IppmsUtils.treatNull((String) o[i++]));
                employeeApproval.setBankName((String)o[i++]);
                employeeApproval.setAccountNo((String)o[i++]);
                employeeApproval.setBvnNo((String)o[i++]);
                employeeApproval.setBirthDateStr(PayrollHRUtils.getFullDateFormat().format((LocalDate)o[i++]));
                employeeApproval.setYearlySalary(IConstants.naira+PayrollHRUtils.getDecimalFormat().format(o[i++]));
                employeeApproval.setMonthlySalary(IConstants.naira+PayrollHRUtils.getDecimalFormat().format(o[i++]));
                employeeApproval.setAnnualSalaryStrWivNaira(IConstants.naira+PayrollHRUtils.getDecimalFormat().format(o[i++]));
                employeeApproval.setPayGroup(o[i++]+" : "+ PayrollUtils.makeLevelAndStep((Integer)o[i++],(Integer)o[i++]));
                employeeApproval.setCreatedBy(o[i++] + " "+o[i++]);
                employeeApproval.setMdaName((String)o[i++]);
                employeeApproval.setInitiatedDateStr(PayrollHRUtils.getFullDateFormat().format((LocalDate)o[i++]));
                employeeApproval.setDisplayName(PayrollHRUtils.createDisplayName(employeeApproval.getLastName(),employeeApproval.getFirstName(),employeeApproval.getInitials()));
                if(IppmsUtils.isNotNullAndGreaterThanZero(pTicketId)){
                    employeeApproval.setApprovedDate((LocalDate)o[i++]);
                    employeeApproval.setApprovedBy(o[i++] + " "+o[i++]);
                }
                wRetList.add(employeeApproval);


            }
        }

        return wRetList;
    }
    @Transactional
    public void saveApproval(AbstractApprovalEntity employeeApproval, BusinessCertificate businessCertificate) {
        String tableName;
        tableName = "EmployeeApproval";

       if(businessCertificate.isPensioner())
            if(employeeApproval.getClass().isInstance(AmAliveApproval.class))
               tableName = "AmAliveApproval";

        String hqlQuery = "update "+tableName+"  set approvalStatusInd = :pStatus, approver.id = :pModifiedBy, approvedDate = :pLastModTs, approvalMemo = :pMemo" +
                "  where id = :pIdVal";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("pStatus", employeeApproval.getApprovalStatusInd());
        query.setParameter("pModifiedBy", employeeApproval.getApprover().getId());
        query.setParameter("pLastModTs", LocalDate.now());
        query.setParameter("pMemo", employeeApproval.getApprovalMemo());
        query.setParameter("pIdVal", employeeApproval.getId());

        query.executeUpdate();
    }

    public List<EmployeeApproval> loadSkeletalPendingEmpApprovals(BusinessCertificate bc, int startRow, int pEndRow, String url) {
        List<EmployeeApproval> wRetList = new ArrayList<>();



        String wHql = "select ea.id,e.id,e.employeeId,e.firstName,e.lastName, e.initials,u.firstName, u.lastName,u.id,ea.lastModTs "
                + "from  EmployeeApproval ea, "+IppmsUtils.getEmployeeTableName(bc)+" e,User u where ea." +bc.getEmployeeIdJoinStr()+" = e.id and u.id = ea.initiator.id  "+
                "and ea.businessClientId = :pBizIdVar and ea.approvalStatusInd = 0";

        Query wQuery = sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        if(startRow > 0)
            wQuery.setFirstResult(startRow);
        wQuery.setMaxResults(pEndRow);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        if (wRetVal.size() > 0) {
            int i = 0;
            EmployeeApproval employeeApproval;
            for (Object[] o : wRetVal) {
                employeeApproval = new EmployeeApproval();
                employeeApproval.setId((Long)o[i++]);
                employeeApproval.setEntityId((Long)o[i++]);
                employeeApproval.setEmployeeId((String)o[i++]);
                employeeApproval.setFirstName((String)o[i++]);
                employeeApproval.setLastName((String)o[i++]);
                employeeApproval.setInitials(IppmsUtils.treatNull((String) o[i++]));
                employeeApproval.setCreatedBy(o[i++] + " "+o[i++]);
                employeeApproval.setInitiatorId((Long)o[i++]);
                employeeApproval.setInitiatedDateStr(PayrollHRUtils.getFullDateFormat().format((LocalDate)o[i++]));
                employeeApproval.setDisplayName(PayrollHRUtils.createDisplayName(employeeApproval.getLastName(),employeeApproval.getFirstName(),employeeApproval.getInitials()));
                employeeApproval.setEnteredCaptcha(url);
                wRetList.add(employeeApproval);
                i = 0;

            }
        }

        return wRetList;
    }
}
