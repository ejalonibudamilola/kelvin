
package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.audit.domain.AbstractPromotionAuditEntity;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.PromotionService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.control.entities.Rank;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.promotion.PromotionTracker;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;

public abstract class PromotionHelperService {

    public static Long applyPromotion(BusinessCertificate bc, Long pEmpId, Long pHiringId, Integer pNewLevel, PromotionService promotionService, PaycheckService pPaycheckService,
                                           Long pRankId, GenericService genericService, Long pOldSalId, Long pNewSalId,
                                      Long pMdaInfoId, @Nullable Long pSchoolId, PromotionTracker pPromotionTracker, double pAmount, String pRefNum, LocalDate pRefDate) throws Exception {
        LocalDate wNextPromotionDate = PayrollHRUtils.determineNextPromotionDate(LocalDate.now(), pNewLevel);
        if(bc.isLocalGovt())
             promotionService.promoteSingleEmployee(pEmpId, pNewSalId, bc, new Rank(pRankId));
        else{
            AbstractEmployeeEntity abstractEmployeeEntity = IppmsUtils.loadEmployee(genericService,pEmpId,bc);
            promotionService.promoteSingleEmployee(pEmpId, pNewSalId, bc, abstractEmployeeEntity.getRank());
        }



        AbstractPromotionAuditEntity wPA = IppmsUtils.makePromotionAuditObject(bc);
        wPA.setEmployee(new Employee(pEmpId));
        wPA.setOldSalaryInfo(new SalaryInfo(pOldSalId));
        wPA.setUser(new User(bc.getLoginId()));
        wPA.setLastModTs(LocalDate.now());
        wPA.setAuditTime(PayrollBeanUtils.getCurrentTime(false));
        wPA.setSalaryInfo(new SalaryInfo(pNewSalId));
        wPA.setMdaInfo(new MdaInfo(pMdaInfoId));//This is actually the MDA_INST_ID now. Please note..
        wPA.setPromotionDate(LocalDate.now());
        wPA.setUser(new User(bc.getLoginId()));
        wPA.setAuditPayPeriod(PayrollUtils.makePayPeriodForAuditLogs(genericService, bc));
        wPA.setBusinessClientId(bc.getBusinessClientInstId());
        if(IppmsUtils.isNotNullAndGreaterThanZero(pSchoolId)){
            wPA.setSchoolInfo(new SchoolInfo(pSchoolId));
        }
        genericService.storeObject(wPA);


        promotionService.updateHiringInfoPromotionDates(pHiringId,  wNextPromotionDate, bc);

        pPromotionTracker.setEmployee(new Employee(pEmpId));
        pPromotionTracker.setUser(new User(bc.getLoginId()));
        pPromotionTracker.setLastPromotionDate(LocalDate.now());
        pPromotionTracker.setNextPromotionDate(wNextPromotionDate);
        pPromotionTracker.setBusinessClientId(bc.getBusinessClientInstId());

        genericService.storeObject(pPromotionTracker);


        Long wArrearsAdded = 0L;
        if (pAmount > 0.0D)
        {
            SpecialAllowanceType wSAT =  genericService.loadObjectUsingRestriction(SpecialAllowanceType.class,
                    Arrays.asList(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()), CustomPredicate.procurePredicate("arrearsInd", 1)));
            //-- Now we need to Create a Salary Arrears Special Allowance...

            AbstractSpecialAllowanceEntity wSAI = (AbstractSpecialAllowanceEntity) genericService.loadObjectUsingRestriction(IppmsUtils.getSpecialAllowanceInfoClass(bc),
                    Arrays.asList(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),
                            CustomPredicate.procurePredicate("specialAllowanceType.id", wSAT.getId()), CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(),pEmpId)));
            LocalDate startDate,endDate;

            PayrollFlag pf = IppmsUtilsExt.getPayrollFlagForClient(genericService,bc);
            startDate = PayrollBeanUtils.makeNextPayPeriodStart(pf.getApprovedMonthInd(),pf.getApprovedYearInd());
            endDate = PayrollBeanUtils.getDateFromMonthAndYear(startDate.getMonthValue(),startDate.getYear(),true);
            if(wSAI.isNewEntity()){
                wSAI = IppmsUtils.makeSpecialAllowanceInfoObject(bc);
                wSAI.setSpecialAllowanceType(wSAT);
                wSAI.setEmployee(new Employee(pEmpId));
                wSAI.setPayTypes(wSAT.getPayTypes());
                wSAI.setCreatedBy(new User(bc.getLoginId()));
                wSAI.setName(wSAT.getName());
                wSAI.setDescription(wSAT.getDescription());

            }

            wSAI.setStartDate(startDate);
            wSAI.setEndDate(endDate);
            wSAI.setExpire(0);
            wSAI.setExpiredBy(null);
            wSAI.setAmount(pAmount);
            wSAI.setDescription(wSAT.getName());
            wSAI.setLastModBy(new User(bc.getLoginId()));
            wSAI.setLastModTs(Timestamp.from(Instant.now()));
            wSAI.setReferenceNumber(pRefNum);
            wSAI.setReferenceDate(pRefDate);
            wSAI.setBusinessClientId(bc.getBusinessClientInstId());

            wArrearsAdded = genericService.storeObject(wSAI);


        }
        LocalDate _wCal = pPaycheckService.getPendingPaycheckRunMonthAndYear(bc);
        if(_wCal != null){
            //This means we have a Pending Paycheck. Set for strictly RERUN
            RerunPayrollBean wRPB = genericService.loadObjectUsingRestriction(RerunPayrollBean.class, Arrays.asList(
                    CustomPredicate.procurePredicate("runMonth", _wCal.getMonthValue()),
                    CustomPredicate.procurePredicate("runYear", _wCal.getYear()),
                    CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId())));
            wRPB.setNoOfPromotions(wRPB.getNoOfPromotions() + 1);
            if(wRPB.isNewEntity()){
                wRPB.setRunMonth(_wCal.getMonthValue());
                wRPB.setRunYear(_wCal.getYear());
                wRPB.setRerunInd(1);
                wRPB.setBusinessClientId(bc.getBusinessClientInstId());
            }

            genericService.storeObject(wRPB);
        }
        return wArrearsAdded;
    }
}
