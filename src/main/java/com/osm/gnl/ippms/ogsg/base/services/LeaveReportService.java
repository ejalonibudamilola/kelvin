package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.leavebonus.domain.LeaveBonusBean;
import com.osm.gnl.ippms.ogsg.leavebonus.domain.LeaveBonusMasterBean;
import com.osm.gnl.ippms.ogsg.ltg.domain.AbmpBean;
import com.osm.gnl.ippms.ogsg.ltg.domain.LtgMasterBean;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service("leaveService")
@Repository
@Transactional(readOnly = true)
public class LeaveReportService {
    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public List<LeaveBonusBean> loadLeaveBonusDetailsByMdaAndYear(
            Long pMdaInstId,  int pYear)
    {

        String wHqlStr = "select e.lastName,e.firstName,e.initials,e.employeeId,h.birthDate,h.hireDate,l.leaveBonusAmount" +
                ",st.name,s.level,s.step,l.leaveBonusMasterBean.id from "
                + "LeaveBonusBean l, Employee e, LeaveBonusMasterBean lmb,HiringInfo h,SalaryInfo s,SalaryType st "
                + "where l.employee.id = e.id and l.leaveBonusMasterBean.id = lmb.id and e.id = h.employee.id "
                + "and lmb.mdaInfo.id = :pMdaId "
                + " and lmb.runYear = :pRunYear and s.id = l.salaryInfo.id and s.salaryType.id = st.id ";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHqlStr);
        wQuery.setParameter("pMdaId", pMdaInstId);
        wQuery.setParameter("pRunYear", pYear);
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        List<LeaveBonusBean> wRetMap = new ArrayList<LeaveBonusBean>();
        if ((wRetVal != null) && (wRetVal.size() > 0)) {
            for (Object[] o : wRetVal) {
                String wLastName = (String)o[0];
                String wFirstName = (String)o[1];
                String wInitials = IppmsUtils.treatNull((String) o[2]);
                LeaveBonusBean l = new LeaveBonusBean();
                l.setEmployee(new Employee(wLastName,wFirstName,wInitials));
                l.getEmployee().setEmployeeId((String)o[3]);
                l.setDateOfBirth((LocalDate)o[4]);
                l.setDateOfHire((LocalDate)o[5]);
                l.setLeaveBonusAmount((Double)o[6]);
                l.setPayGroup((String)o[7]);
                int wLevel = (Integer)o[8];
                int wStep = (Integer)o[9];
                if(wStep < 10){
                    l.setLevelAndStep(wLevel +"/0"+ wStep);
                }else{
                    l.setLevelAndStep(wLevel +"/"+ wStep);
                }
                l.setLeaveBonusMasterBean(new LeaveBonusMasterBean((Long)o[10]));
                wRetMap.add(l);

            }
        }

        return wRetMap;
    }

    public List<LeaveBonusBean> loadLeaveBonusByParentId(Long pId)
    {
        String wHqlStr = "select l.leaveBonusAmount,e.employeeId,e.firstName,e.lastName" +
                ",e.initials,s.level,s.step,st.name from " +
                "LeaveBonusBean l, Employee e, SalaryInfo s, SalaryType st " +
                "where l.employee.id = e.id and l.salaryInfo.id = s.id " +
                "and s.salaryType.id = st.id and l.leaveBonusMasterBean.id = :pParentId " +
                "order by e.lastName,e.firstName";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHqlStr);
        wQuery.setParameter("pParentId", pId);
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        List<LeaveBonusBean> wRetMap = new ArrayList<LeaveBonusBean>();
        if ((wRetVal != null) && (wRetVal.size() > 0))
        {
            for (Object[] o : wRetVal) {
                LeaveBonusBean n = new LeaveBonusBean();
                n.setLeaveBonusAmount((Double)o[0]);
                n.setEmployeeId((String)o[1]);
                String firstName = (String)o[2];
                String lastName = (String)o[3];
                Object initials = o[4];
                if(initials == null){
                    n.setName(PayrollHRUtils.createDisplayName(lastName, firstName, "")) ;
                }else{
                    n.setName(PayrollHRUtils.createDisplayName(lastName, firstName, (String)initials)) ;
                }
                int level = (Integer)o[5];
                int step = (Integer)o[6];
                if(step < 10){
                    n.setLevelAndStep(level +".0"+ step);
                }else{
                    n.setLevelAndStep(level +"."+ step);
                }
                n.setPayGroup((String)o[7]);
                wRetMap.add(n);
            }
        }

        return wRetMap;
    }

    public List<LeaveBonusMasterBean> loadLeaveBonusMasterBeansForExcelDisplay(
            int pYear)
    {
        String wHqlStr = "";
//        List<LeaveBonusMasterBean> wRetList = new ArrayList<LeaveBonusMasterBean>();

        if(pYear > 1){
            wHqlStr = "select l.id,l.mdaInfo.id,l.mdaInfo.name, l.mdaInfo.codeName,l.runMonth,l.lastModBy,l.createdDate," +
                    "l.totalAmountPaid,l.totalNoOfEmp,l.approvedInd,l.approvedBy.id, l.approvedBy.userName,l.approvedBy.firstName"
                    + ",l.approvedBy.lastName from LeaveBonusMasterBean l " +
                    "where l.runYear = :pYearValue ";
        }else{
            wHqlStr = "select runYear,sum(totalAmountPaid),sum(totalNoOfEmp),approvedInd from LeaveBonusMasterBean " +
                    " group by run_year,approvedInd " ;

        }

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHqlStr);
        if(pYear > 1)
            wQuery.setParameter("pYearValue", pYear);



        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        List<LeaveBonusMasterBean> wRetMap = new ArrayList<LeaveBonusMasterBean>();
        if ((wRetVal != null) && (wRetVal.size() > 0))
        {
            int i = 0;
            for (Object[] o : wRetVal) {
                LeaveBonusMasterBean n = new LeaveBonusMasterBean();

                if(pYear > 1){

                    n.setId((Long)o[i++]);
                    n.setMdaInfo(new MdaInfo((Long)o[i++], (String)o[i++] ,(String)o[i++]));
                    n.setRunMonth((Integer)o[i++]);
                    n.setLastModBy((String)o[i++]);
                    n.setCreatedDate((LocalDate)o[i++]);
                    n.setTotalAmountPaid((Double)o[i++]);
                    n.setTotalNoOfEmp((Integer)o[i++]);
                    n.setApprovedInd((Integer)o[i++]);
                    n.setApprovedBy(new User((Long)o[i++],(String)o[i++],(String)o[i++],(String)o[i++]));

                    n.setMode(n.getMdaInfo().getId() +":"+ pYear);

                }else{
                    n.setRunYear((Integer)o[i++]);
                    n.setTotalAmountPaid((Double)o[i++]);
                    n.setTotalNoOfEmp(((Long)o[i++]).intValue());
                    n.setApprovedInd((Integer)o[i++]);
                    n.setId(new Long(String.valueOf(n.getRunYear())));
                }
                i = 0;
                wRetMap.add(n);
            }
        }
        return wRetMap;
    }

    public List<LtgMasterBean> loadLtgMasterBeansForDisplay(int pStartRow, int pEndRow, String pSortOrder, String pSortCriterion, BusinessCertificate bc)
    {
        String wStr = "select m.id,m.name,m.simulationMonth,m.simulationYear,m.applicationIndicator,m.lastModTs,l.firstName,"
                + " l.lastName, count(d.id) from LtgMasterBean m, AbmpBean d, User l "
                + "where m.id = d.ltgMasterBean.id and m.lastModBy = l.username and m.businessClientId = :lBizId group by m.id, m.name,"
                + " m.simulationMonth, m.simulationYear, m.applicationIndicator,m.lastModTs, l.firstName, l.lastName";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wStr);
        wQuery.setParameter("lBizId", bc.getBusinessClientInstId());

        if (pStartRow > 0)
            wQuery.setFirstResult(pStartRow);
        wQuery.setMaxResults(pEndRow);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        List<LtgMasterBean> wRetMap = new ArrayList<LtgMasterBean>();
        if ((wRetVal != null) && (wRetVal.size() > 0))
        {
            for (Object[] o : wRetVal) {
                LtgMasterBean p = new LtgMasterBean();
                p.setId((Long)o[0]);
                p.setName((String)o[1]);
                p.setSimulationMonth(((Integer)o[2]));
                p.setSimulationYear(((Integer)o[3]));
                p.setApplicationIndicator(((Integer)o[4]));
                LocalDate wDate = (LocalDate) o[5];
                p.setCreatedDateStr(PayrollHRUtils.getDisplayDateFormat().format(wDate));
                p.setSimulationMonthStr(PayrollBeanUtils.getMonthNameFromInteger(p.getSimulationMonth()) + ", " + p.getSimulationYear());
                String pFirstName = (String)o[6];
                String pLastName = (String)o[7];
                p.setCreatedBy(pFirstName + " " + pLastName);
                p.setNoOfMdasAffected(((Long)o[8]).intValue());

                wRetMap.add(p);
            }
        }

        return wRetMap;
    }
    @Transactional()
    public void storeLtgDetails(List<AbmpBean> pAssignedList, String pUserName, Long pParentInstId)
    {
        for (AbmpBean a : pAssignedList)
        {
            a.setId(null);
            a.setLastModBy(pUserName);
            a.setLastModTs(LocalDate.now());
            a.setLtgMasterBean(new LtgMasterBean(pParentInstId));

            genericService.saveObject(a);
        }
    }
}
