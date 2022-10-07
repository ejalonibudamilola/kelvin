package com.osm.gnl.ippms.ogsg.controllers.allowance.service;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.SpecAllowService;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class AllowanceService {

    @Autowired
    private GenericService genericService;

    @Autowired
    private SpecAllowService specAllowService;

    public AllowanceService(){}

    public  List<SpecialAllowanceType> getSpecAllowType( Long pBizClientId) {
        return genericService.loadAllObjectsWithSingleCondition(SpecialAllowanceType.class, CustomPredicate.procurePredicate("businessClientId",pBizClientId),"description");
    }

    public   List<PayTypes> loadSelectablePayTypes() {
        return genericService.loadAllObjectsWithSingleCondition
            (PayTypes.class, CustomPredicate.procurePredicate("selectableInd", 0), null);
    }

    public void setupCreateForm(Long pEmpId, Model model, NamedEntity ne, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {
        AbstractEmployeeEntity emp = (AbstractEmployeeEntity) genericService.loadObjectById(IppmsUtils.getEmployeeClass(bc), pEmpId);
        if ((ne.isNewEntity()) || (ne.getName() == null)) {

            ne.setName(emp.getDisplayNameWivTitlePrefixed() + " [ " + emp.getEmployeeId() + " ]");
            ne.setId(emp.getId());
        }

        model.addAttribute("namedEntity", ne);
        AbstractSpecialAllowanceEntity specialAllowInfo = IppmsUtils.makeSpecialAllowanceInfoObject(bc);
        specialAllowInfo.setParentObject(emp);
        specialAllowInfo.makeParentObject(emp.getId());
        model.addAttribute("empSpecAllow", specialAllowInfo);
    }

    public   void setCreateAllowanceResult(AbstractSpecialAllowanceEntity pSpecAllowInfo, BindingResult result,
                                           Model model, NamedEntity namedEntity) throws IllegalAccessException, InstantiationException {

        model.addAttribute("namedEntity",
                namedEntity);
        model.addAttribute("pageErrors", result);
        if(pSpecAllowInfo.getPayTypes() != null && !pSpecAllowInfo.getPayTypes().isNewEntity() )
            model.addAttribute("payTypes", Arrays.asList(this.genericService.loadObjectById(PayTypes.class, pSpecAllowInfo.getPayTypes().getId())));

        model.addAttribute("empSpecAllow", pSpecAllowInfo);
        model.addAttribute("save", true);
    }

    public  void setCreateAllowanceWarning(AbstractSpecialAllowanceEntity pSpecAllowInfo, BindingResult result,
                                                 BusinessCertificate bc) {
        result.rejectValue("", "Warning", "Please confirm values before saving this Special Allowance.");
        pSpecAllowInfo.setWarningIssued(true);
        pSpecAllowInfo.setReferenceDate(LocalDate.now());
        pSpecAllowInfo.setReferenceNumber(bc.getUserName());
    }

    public void createAllowance(AbstractSpecialAllowanceEntity pSpecAllowInfo,
         BusinessCertificate bc) {
        pSpecAllowInfo.setAmount(Double.parseDouble(PayrollHRUtils.removeCommas(pSpecAllowInfo.getAmountStr())));
        pSpecAllowInfo.setPayTypes(new PayTypes(pSpecAllowInfo.getPayTypeInstId()));
        pSpecAllowInfo.setName(pSpecAllowInfo.getSpecialAllowanceType().getDescription());
        pSpecAllowInfo.setDescription(pSpecAllowInfo.getSpecialAllowanceType().getName());
        pSpecAllowInfo.setLastModBy(new User(bc.getLoginId()));
        pSpecAllowInfo.setLastModTs(Timestamp.valueOf(LocalDateTime.now()));
        pSpecAllowInfo.setCreatedBy(pSpecAllowInfo.getLastModBy());
        pSpecAllowInfo.setBusinessClientId( bc.getBusinessClientInstId());
        pSpecAllowInfo.makeParentObject(pSpecAllowInfo.getParentId());
//        if(bc.isPensioner())
//            pSpecAllowInfo.setPensioner(new Pensioner(pSpecAllowInfo.getParentId()));
//        else
//            pSpecAllowInfo.setEmployee(new Employee(pSpecAllowInfo.getParentId()));
        genericService.saveObject(pSpecAllowInfo);
    }

    public String prepareModelForEdit(Model pModel, Long pAllowanceId, Long pEmpId, NamedEntity ne, BusinessCertificate bc, String pActn) throws Exception  {

        AbstractSpecialAllowanceEntity wSAI = (AbstractSpecialAllowanceEntity) genericService.loadObjectById(IppmsUtils.getSpecialAllowanceInfoClass(bc), pAllowanceId);
        if ( wSAI.isNewEntity() ) {
            return "redirect:addSpecialAllowance.do?eid=" + pEmpId;
        }


        wSAI.setAmountStr(PayrollHRUtils.getDecimalFormat().format(wSAI.getAmount()));
        if ((ne.isNewEntity()) || (ne.getName() == null)) {
            AbstractEmployeeEntity emp = (AbstractEmployeeEntity) genericService.loadObjectById(IppmsUtils.getEmployeeClass(bc), pEmpId);
            ne.setName(emp.getDisplayNameWivTitlePrefixed() + " [ " + emp.getEmployeeId() + " ]");
            ne.setId(emp.getId());
        }
        wSAI.setPayTypeInstId(wSAI.getPayTypes().getId());
        wSAI.setTypeInstId(wSAI.getSpecialAllowanceType().getId());
        if(!wSAI.getSpecialAllowanceType().isCanEdit()
                && !(bc.isCanEditSpecAllow())){
            wSAI.setCanEdit(false);
        }else wSAI.setCanEdit(!wSAI.isExpired());
        if(IppmsUtils.isNotNullOrEmpty(pActn))
         wSAI.setDeleteMode(true);

        pModel.addAttribute("namedEntity", ne);
        pModel.addAttribute("payType", Arrays.asList(wSAI.getPayTypes()));
        pModel.addAttribute("empSpecAllow", wSAI);
        pModel.addAttribute("roleBean", bc);

        return "allowance/casp/editSpecAllowForm";
    }

    public int getNoOfPendingObjects(Long pDependentObjectId, Long pEmployeeInstId, BusinessCertificate businessCertificate) {
        if(pDependentObjectId == null) return 0;
        return specAllowService.getNoOfPendingSpecAllowances(pDependentObjectId,pEmployeeInstId,businessCertificate);
    }

    public void setEditDeleteResult(AbstractSpecialAllowanceEntity pSpecAllowInfo, NamedEntity ne,
                                           BindingResult result, Model model) {
        result.rejectValue("", "Invalid.Value", "Special Allowance can not be edited,deleted or set to zero (0)");
        result.rejectValue("", "Invalid.Value", "Reason - Pending Paychecks exists.");

        model.addAttribute("namedEntity", ne);
        model.addAttribute("pageErrors", result);
        model.addAttribute("empSpecAllow", pSpecAllowInfo);
    }

    public  void setEditErrorResult(AbstractSpecialAllowanceEntity pSpecAllowInfo, NamedEntity ne, Model model,BusinessCertificate businessCertificate,
                                          BindingResult result) throws IllegalAccessException, InstantiationException {

        if ((ne.isNewEntity()) || (ne.getName() == null)) {
            AbstractEmployeeEntity emp = (AbstractEmployeeEntity) genericService.loadObjectById(IppmsUtils.getEmployeeClass(businessCertificate), pSpecAllowInfo.getParentId());
            ne.setName(emp.getDisplayNameWivTitlePrefixed());
            ne.setMode(emp.getEmployeeId());
            ne.setId(emp.getId());
        }
        model.addAttribute("namedEntity", ne);
        model.addAttribute("pageErrors", result);
        model.addAttribute("empSpecAllow", pSpecAllowInfo);
    }

    public  void saveFormForEdit(AbstractSpecialAllowanceEntity pSpecAllowInfo, BusinessCertificate bc ) throws IllegalAccessException, InstantiationException {
        pSpecAllowInfo.setAmount(Double.parseDouble(PayrollHRUtils.removeCommas(pSpecAllowInfo.getAmountStr())));

        pSpecAllowInfo.setLastModBy(new User(bc.getLoginId()));
        pSpecAllowInfo.setLastModTs(Timestamp.valueOf(LocalDateTime.now()));
        if(pSpecAllowInfo.getPayTypes() == null || pSpecAllowInfo.getPayTypes().isNewEntity())
            pSpecAllowInfo.setPayTypes(new PayTypes(pSpecAllowInfo.getPayTypeInstId()));

        if (pSpecAllowInfo.getAmount() == 0.0D) {
            pSpecAllowInfo.setEndDate(LocalDate.now());
            pSpecAllowInfo.setExpire(1);
            pSpecAllowInfo.setExpiredBy(genericService.loadObjectById(User.class, bc.getLoginId()));
        }
        genericService.saveObject(pSpecAllowInfo);
    }

    public boolean employeeHasSpecialAllowance(Long specAllowInfoId, Long employeeId,  BusinessCertificate bc) {
        PredicateBuilder predicateBuilder = new PredicateBuilder().
                addPredicate(CustomPredicate.procurePredicate("specialAllowanceInfo.id", specAllowInfoId)).
                addPredicate(CustomPredicate.procurePredicate("employee.id", employeeId));

        return genericService.countObjectsUsingPredicateBuilder(predicateBuilder,IppmsUtils.makePaycheckSpecAllowClass(bc)) > 0;
    }

    public  void saveOrDeletePaycheckInfo(AbstractSpecialAllowanceEntity pSpecAllowInfo, BusinessCertificate bc){
        if (pSpecAllowInfo.isHasPaycheckInfo()) {
            pSpecAllowInfo.setAmount(0.0D);
            pSpecAllowInfo.setEndDate(LocalDate.now());
            pSpecAllowInfo.setLastModBy(new User(bc.getLoginId()));
            pSpecAllowInfo.setLastModTs(Timestamp.valueOf(LocalDateTime.now()));
            pSpecAllowInfo.setExpire(1);
            pSpecAllowInfo.setExpiredBy(new User(bc.getLoginId()));
            genericService.saveObject(pSpecAllowInfo);
        } else {
            genericService.deleteObject(pSpecAllowInfo);
        }
    }

}
