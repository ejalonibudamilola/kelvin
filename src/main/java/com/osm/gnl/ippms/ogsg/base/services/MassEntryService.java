package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Service("massEntryService")
@Repository
@Transactional(readOnly = true)
public class MassEntryService {


    private final GenericService genericService;

    private final SessionFactory sessionFactory;
    @Autowired
    public MassEntryService(GenericService genericService, SessionFactory sessionFactory){
        this.genericService = genericService;
        this.sessionFactory = sessionFactory;
    }

    public HashMap<Long, String> loadStepIncrementMapForMassPromotion(BusinessCertificate bc) {
        HashMap<Long, String> wRetMap = new HashMap<>();

        String wHql = "SELECT e.id,e.firstName, e.lastName, e.initials, fs.salaryType.name,fs.level,fs.step, ts.salaryType.name, ts.level,ts.step "
                + " FROM StepIncrementApproval f, Employee e, SalaryInfo fs, SalaryInfo ts WHERE "
                + " f.parentId = e.id  and e.salaryInfo.id = fs.id and f.salaryInfo.id = ts.id "
                + "  and f.approvalStatusInd = 0 and f.businessClientId = :pBizIdVar";


        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        String value;
        Long key;
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                key = ((Long) o[0]);
                String pFirstName = ((String) o[1]);
                String pLastName = ((String) o[2]);
                String pInitials = null;
                if (o[3] != null) {
                    pInitials = ((String) o[3]);
                }
                value = PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials);
                value += " has a pending Step Increment Approval from " + o[4] + ":" + PayrollUtils.makeLevelAndStep((Integer) o[5], (Integer) o[6]) + "\n to" +
                        "" + o[7] + ":" + PayrollUtils.makeLevelAndStep((Integer) o[8], (Integer) o[9]);
                wRetMap.put(key, value);
            }
        }

        return wRetMap;
    }

    public HashMap<Long, String> loadFlaggedPromotionMapForMassPromotion(BusinessCertificate bc) {
        HashMap<Long, String> wRetMap = new HashMap<>();

        String wHql = "SELECT e.id,e.firstName, e.lastName, e.initials, fs.salaryType.name,fs.level,fs.step, ts.salaryType.name, ts.level,ts.step "
                + " FROM FlaggedPromotions f, Employee e, SalaryInfo fs, SalaryInfo ts   WHERE "
                + " f.employee.id = e.id  and f.fromSalaryInfo.id = fs.id and f.toSalaryInfo.id = ts.id "
                + "  and f.statusInd = 0 and f.businessClientId = :pBizIdVar";


        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        String value;
        Long key;
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                key = ((Long) o[0]);
                String pFirstName = ((String) o[1]);
                String pLastName = ((String) o[2]);
                String pInitials = null;
                if (o[3] != null) {
                    pInitials = ((String) o[3]);
                }
                value = PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials);
                value += " has a pending Flagged Promotion from " + o[4] + ":" + PayrollUtils.makeLevelAndStep((Integer) o[5], (Integer) o[6]) + "\n to " +
                        "" + o[7] + ":" + PayrollUtils.makeLevelAndStep((Integer) o[8], (Integer) o[9]);
                wRetMap.put(key, value);
            }
        }

        return wRetMap;
    }
    public List<Employee> loadActiveEmployeesForMassPromotion(BusinessCertificate bc,Long pSsc) {
        List<Employee> wRetList = new ArrayList<Employee>();

        String wHql = "";
        if(bc.isSubeb() || bc.isCivilService()){
            wHql = "select e.id,e.employeeId,e.firstName,e.lastName,e.initials,m.name, "
                    + "s.id,s.level,s.step,e.schoolInfo.id, e.rank.id,m.id from Employee e, SalaryInfo s,SalaryType sc, MdaDeptMap mdm, MdaInfo m" +
                    " where  e.salaryInfo.id = s.id and s.salaryType.id = sc.id and e.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = m.id "
                    + "and e.statusIndicator = 0 and s.id = :pScaleId and e.businessClientId = :pBizIdVar"
                    + " order by e.lastName,e.firstName,e.initials";
        }else if(bc.isLocalGovt()){
            wHql = "select e.id,e.employeeId,e.firstName,e.lastName,e.initials,m.name, "
                    + "s.id,s.level,s.step ,e.rank.id,m.id from Employee e, SalaryInfo s,SalaryType sc, MdaDeptMap mdm, MdaInfo m" +
                    " where  e.salaryInfo.id = s.id and s.salaryType.id = sc.id and e.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = m.id "
                    + "and e.statusIndicator = 0 and s.id = :pScaleId and e.businessClientId = :pBizIdVar"
                    + " order by e.lastName,e.firstName,e.initials";
        }


        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        wQuery.setParameter("pScaleId",  pSsc);
        wQuery.setParameter("pBizIdVar",  bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {
            int i = 0;
            int _i = 1;
            Object init;
            Object schoolId;
            SalaryInfo s;
            Employee e;
            for (Object[] o : wRetVal) {
                e = new Employee();

                e.setId((Long) o[i++]);
                e.setEmployeeId((String) o[i++]);
                e.setFirstName((String) o[i++]);
                e.setLastName((String) o[i++]);
                init = o[i++];
                if(init != null)
                  e.setInitials((String) init);

                e.setMdaName((String)o[i++]);

                s = new SalaryInfo((Long) o[i++]);
                s.setLevel(((Integer) o[i++]).intValue());
                s.setStep(((Integer) o[i++]).intValue());
                if(bc.isCivilService() || bc.isSubeb()){
                    schoolId = o[i++];
                    if(schoolId == null)
                        e.setSchoolInstId(0L);
                    else
                        e.setSchoolInstId((Long) schoolId);
                }

                e.setBiometricId((Long)o[i++]);
                e.setMdaInstId((Long)o[i++]);
                e.setSalaryInfo(s);
                if (_i % 2 == 1)
                    e.setDisplayStyle("reportEven");
                else {
                    e.setDisplayStyle("reportOdd");
                }
                _i++;
                i = 0;
                wRetList.add(e);
            }

        }

        return wRetList;
    }
    public List<SalaryInfo> loadSalaryInfoBySalaryTypeLevelAndStep(SalaryInfo pSalaryInfo, boolean pLimitSteps, boolean isForDemotion) {
        List <SalaryInfo>wRetList = new ArrayList<SalaryInfo>();
        String sql = "select s.id, s.level,s.step from SalaryInfo s "
                + "where s.salaryType.id = :pTypeId ";

        if(pLimitSteps) {
            if(isForDemotion) {
                sql += "and ((s.level = :pLevel and s.step < :pStep) or s.level < :pLevel)";
            }else {
                sql += "and ((s.level >= :pLevel and s.step > :pStep) or s.level > :pLevel)";
            }

        }
        sql += "order by s.level,s.step";
        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameter("pTypeId", pSalaryInfo.getSalaryType().getId());

        if(pLimitSteps) {

            wQuery.setParameter("pLevel", pSalaryInfo.getLevel());
            wQuery.setParameter("pStep", pSalaryInfo.getStep());
        }

        List<Object[]> results = wQuery.list();

        if ((results != null) && (results.size() > 0)) {
            for (Object[] o : results) {
                SalaryInfo m = new SalaryInfo();
                m.setId((Long)o[0]);
                m.setLevel((Integer)o[1]);
                m.setStep((Integer)o[2]);
                wRetList.add(m);
            }
        }
        return wRetList;

    }

    public HashMap<Long, Long> makeDedGarnSpecMap(Class<?> clazz, String pTypeString, Long pTypeInd, List<Long> pEmployeeIdList, BusinessCertificate businessCertificate) {

        Criteria wCrit = this.sessionFactory.getCurrentSession().createCriteria(clazz)
                .add(Restrictions.in(businessCertificate.getEmployeeIdJoinStr(),pEmployeeIdList));

        wCrit.add(Restrictions.eq(pTypeString, pTypeInd));

        HashMap<Long,Long> wRetMap = new HashMap<>();
        List<?> wRetVal;
        try{
            wRetVal = wCrit.list();

            if(wRetVal != null && !wRetVal.isEmpty()){
                for(Object o : wRetVal){
                    if(AbstractDeductionEntity.class.isAssignableFrom(clazz)){
                        wRetMap.put(((AbstractDeductionEntity)o).getEmployee().getId(),((AbstractDeductionEntity)o).getId());
                    }else if(AbstractGarnishmentEntity.class.isAssignableFrom(clazz)) {
                        wRetMap.put(((AbstractGarnishmentEntity)o).getParentId(),((AbstractGarnishmentEntity)o).getId());
                    }else if(AbstractSpecialAllowanceEntity.class.isAssignableFrom(clazz)) {

                        wRetMap.put(((AbstractSpecialAllowanceEntity)o).getEmployee().getId(),((AbstractSpecialAllowanceEntity)o).getId());
                    }else {
                        continue;
                    }


                }
            }
        }catch(Exception wEx){
            wEx.printStackTrace();
        }

        return wRetMap;
    }

    @Transactional
    public void promoteMassEmployee(Long pSalaryStructureId, List<Long> pEmployeeInstId,
                                    List<?> pSAIList)
    {
        String wHql = "update Employee e set e.salaryInfo.id = :pSalaryInfo where e.id in (:pIds)";
        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pSalaryInfo", pSalaryStructureId);
        query.setParameterList("pIds",   (pEmployeeInstId.toArray()));
        query.executeUpdate();
        if(pSAIList != null && !pSAIList.isEmpty())
            this.genericService.storeObjectBatch(pSAIList);
    }

    @Transactional
    public void updateHiringInfoPromotionDates(Collection<NamedEntity> pNEList) {

        Query query = null;
        for(NamedEntity n : pNEList){
            query = this.sessionFactory.getCurrentSession().createQuery(n.getName());
            query.setParameter("pId", n.getId());
            query.setParameter("pLPD",  n.getAllowanceStartDate());
            query.setParameter("pNPD",  n.getAllowanceEndDate());
            query.setParameter("pLMB", n.getParentInstId());
            query.setParameter("pLMTS", Timestamp.from(Instant.now()));

            query.executeUpdate();
        }
    }

    @Transactional
    public void transferMassEmployee(Collection<Long> pEmpIdList, Long pMdaDeptMapId, Long pSchoolId, Long pInitiator)
    {
        String sql = "";
        if(null != pSchoolId ) {
            sql = "update Employee  e set e.mdaDeptMap.id = :pMapId,  e.schoolInfo.id = :pSchoolIdVar, e.lastModBy.id = :pLMB, e.lastModTs = :pLMTS where e.id in (:pIds)";

        }else {
            sql = "update Employee  e set e.mdaDeptMap.id = :pMapId, e.schoolInfo.id = null, e.lastModBy.id = :pLMB, e.lastModTs = :pLMTS  where e.id in (:pIds)";

        }

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameter("pMapId", pMdaDeptMapId);
        wQuery.setParameter("pLMB", pInitiator);
        wQuery.setParameter("pLMTS", Timestamp.from(Instant.now()));
        wQuery.setParameterList("pIds", pEmpIdList.toArray());

        if(null != pSchoolId)
            wQuery.setParameter("pSchoolIdVar", pSchoolId);
        wQuery.executeUpdate();
    }


}
