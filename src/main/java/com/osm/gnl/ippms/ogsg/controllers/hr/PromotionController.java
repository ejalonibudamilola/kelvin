package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.base.services.PromotionService;
import com.osm.gnl.ippms.ogsg.base.services.TransferService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.Rank;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.domain.promotion.FlaggedPromotions;
import com.osm.gnl.ippms.ogsg.domain.promotion.PromotionTracker;
import com.osm.gnl.ippms.ogsg.engine.PromotionHelperService;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.validators.hr.PromotionValidator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@Controller
@RequestMapping({"/promoteEmployee.do"})
@SessionAttributes(types={HrMiniBean.class})
public class PromotionController extends BaseController
{
  
  
  private final String VIEW_NAME = "promotion/promoteEmployeeForm";
  private final String ERROR_VIEW_NAME = "promotion/promoteEmployeeErrorForm";


  private final PromotionValidator promotionValidator;
  private final PromotionService promotionService;
  private final PayrollService payrollService;
  private final TransferService transferService;
  private final PaycheckService paycheckService;

  @Autowired
  public PromotionController(PromotionValidator promotionValidator, PromotionService promotionService, PayrollService payrollService, TransferService transferService, PaycheckService paycheckService)
  {
    this.promotionValidator = promotionValidator;
    this.promotionService = promotionService;
    this.payrollService = payrollService;
    this.transferService = transferService;
    this.paycheckService = paycheckService;
  }
  @RequestMapping(method={RequestMethod.GET}, params={"eid"})
  public String setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws Exception {   
	  
	  
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);

    if (pEmpId <= 0) {
      return REDIRECT_TO_DASHBOARD;
    }
    HrMiniBean empHrBean = new HrMiniBean();

    HiringInfo wHI = loadHiringInfoByEmpId(request,bc,pEmpId);


    if ((wHI == null) || (wHI.isNewEntity()))
    {

      AbstractEmployeeEntity wEmp = IppmsUtils.loadEmployee(genericService,pEmpId,bc);
      empHrBean.setName(wEmp.getDisplayName());
      empHrBean.setDisplayTitle("This "+bc.getStaffTypeName()+ " has no Hiring Information. "+bc.getStaffTypeName()+" can not be promoted.");
      addRoleBeanToModel(model, request);
      model.addAttribute("miniBean", empHrBean);
      return ERROR_VIEW_NAME;
    }
    if (wHI.isTerminated() || wHI.isSuspendedEmployee())
    {

      if(wHI.isTerminated()){
        empHrBean.setDisplayTitle("This "+bc.getStaffTypeName()+" was terminated on " + PayrollHRUtils.getDisplayDateFormat().format(wHI.getTerminateDate()) + ". Promotion denied!");

      }else if(wHI.isSuspendedEmployee()){
        empHrBean.setDisplayTitle("This "+bc.getStaffTypeName()+" was Suspended on " + PayrollHRUtils.getDisplayDateFormat().format(wHI.getSuspensionDate()) + ". Promotion denied!");

      }
      empHrBean.setName(wHI.getEmployee().getDisplayName());
      addRoleBeanToModel(model, request);
      model.addAttribute("miniBean", empHrBean);
      return ERROR_VIEW_NAME;
    }
    if(!wHI.getAbstractEmployeeEntity().isApprovedForPayrolling()){
      empHrBean.setName(wHI.getEmployee().getDisplayName());
      empHrBean.setDisplayTitle("This "+bc.getStaffTypeName()+" is not Approved For Payroll . Promotion denied!");
      addRoleBeanToModel(model, request);
      model.addAttribute("miniBean", empHrBean);
      return ERROR_VIEW_NAME;
    }
    PromotionTracker wPT = this.genericService.loadObjectUsingRestriction(PromotionTracker.class,Arrays.asList(CustomPredicate.procurePredicate("employee.id", wHI.getEmployee().getId()),
            getBusinessClientIdPredicate(request)));

    List<SalaryInfo> wSalaryInfo;
    if(bc.isLocalGovt()){
      wSalaryInfo = promotionService.loadSalaryInfoBySalaryScaleAndFilterLGA(wHI.getEmployee().getSalaryInfo().getSalaryType().getId(), wHI.getEmployee().getSalaryInfo().getStep(), wHI.getEmployee().getRank(), bc.getBusinessClientInstId());
    }else{

      wSalaryInfo = promotionService.loadSalaryInfoForPromotions(wHI.getAbstractEmployeeEntity().getSalaryInfo().getSalaryType().getId(),
              wHI.getAbstractEmployeeEntity().getSalaryInfo().getLevel(),wHI.getAbstractEmployeeEntity().getSalaryInfo().getStep(),bc.getBusinessClientInstId());

    }
    if (wSalaryInfo.isEmpty()) {
      empHrBean.setName(wHI.getEmployee().getDisplayNameWivTitlePrefixed());
      empHrBean.setDisplayTitle("Promotion denied! "+bc.getStaffTypeName()+" : "+wHI.getAbstractEmployeeEntity().getDisplayNameWivTitlePrefixed()+" can not be promoted past his current Level and Step.");
      addRoleBeanToModel(model, request);
      model.addAttribute("miniBean", empHrBean);
      return ERROR_VIEW_NAME;
    }else{
      Collections.sort(wSalaryInfo, Comparator.comparing(SalaryInfo::getLevel).thenComparing(SalaryInfo::getStep));
    }
    empHrBean.setShowForConfirm(HIDE_ROW);
    empHrBean.setShowArrearsRow(HIDE_ROW);
    empHrBean.setOldLevelAndStep(wHI.getAbstractEmployeeEntity().getSalaryInfo().getLevelStepStr());
    empHrBean.setOldSalary(wHI.getAbstractEmployeeEntity().getSalaryInfo().getAnnualSalary());
    empHrBean.setParentId(wHI.getAbstractEmployeeEntity().getId());
    empHrBean.setHiringInfoId(wHI.getId());
    empHrBean.setLevelStepList(wSalaryInfo);
    empHrBean = setRequiredValues(wHI, empHrBean);
    empHrBean.setPromoTracker(wPT);
    empHrBean.setWarningIssued(false);
    empHrBean.setRankInstId(wHI.getEmployee().getRank().getId());
    if(bc.isLocalGovt()){

      model.addAttribute("rankList", this.genericService.loadAllObjectsWithSingleCondition(Rank.class, CustomPredicate.procurePredicate("cadre.id",wHI.getEmployee().getRank().getCadre().getId()),"name"));
    }
    model.addAttribute("miniBean", empHrBean);
    model.addAttribute("salaryStructureList",wSalaryInfo);
    model.addAttribute("roleBean", bc);
    return VIEW_NAME; 
    
  }

  @RequestMapping(method={RequestMethod.GET}, params={"eid", "s","aa","fid"})
  public String setupForm(@RequestParam(value = "eid", required = false) Long pEmpId , @RequestParam("s") int pSaved,
		  @RequestParam("aa") Long pArrearsAdded,@RequestParam(value = "fid", required = false) Long pFlagId,Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);


    if (IppmsUtils.isNullOrLessThanOne(pEmpId)) {
      return REDIRECT_TO_DASHBOARD;
    }

    HrMiniBean empHrBean = new HrMiniBean();
    HiringInfo wHI = super.loadHiringInfoByEmpId(request,bc,pEmpId);
    List<SalaryInfo> wSalaryInfo;
    if(bc.isLocalGovt()){
      wSalaryInfo = promotionService.loadSalaryInfoBySalaryScaleAndFilterLGA(wHI.getEmployee().getSalaryInfo().getSalaryType().getId(), wHI.getEmployee().getSalaryInfo().getStep(), wHI.getEmployee().getRank(), bc.getBusinessClientInstId());
    }else{
      PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(getBusinessClientIdPredicate(request));
      predicateBuilder.addPredicate(CustomPredicate.procurePredicate("salaryType.businessClientId", bc.getBusinessClientInstId()));
      predicateBuilder.addPredicate(CustomPredicate.procurePredicate("level",wHI.getAbstractEmployeeEntity().getSalaryInfo().getLevel(), Operation.GREATER_OR_EQUAL));
      predicateBuilder.addPredicate(CustomPredicate.procurePredicate("salaryType.id", wHI.getAbstractEmployeeEntity().getSalaryInfo().getSalaryType().getId()));


      wSalaryInfo = this.genericService.getObjectsFromBuilder(predicateBuilder,SalaryInfo.class);

    }
    PromotionTracker wPT = new PromotionTracker();
    empHrBean.setShowForConfirm(HIDE_ROW);
    empHrBean.setParentId(wHI.getAbstractEmployeeEntity().getId());
    empHrBean.setHiringInfoId(wHI.getId());
    empHrBean.setLevelStepList(wSalaryInfo);

    empHrBean = setRequiredValues(wHI, empHrBean);
    empHrBean.setWarningIssued(false);
    String actionCompleted;
    if(IppmsUtils.isNotNullAndGreaterThanZero(pFlagId)){

      FlaggedPromotions flaggedPromotions = this.genericService.loadObjectById(FlaggedPromotions.class,pFlagId);
      wHI.getAbstractEmployeeEntity().setSalaryInfo(flaggedPromotions.getToSalaryInfo());

      empHrBean.setOldLevelAndStep(flaggedPromotions.getFromSalaryInfo().getSalaryTypeLevelAndStep());
      empHrBean.setOldSalary(flaggedPromotions.getFromSalaryInfo().getAnnualSalary());
      empHrBean.setPromoTracker(wPT);
      actionCompleted = bc.getStaffTypeName()+" " + empHrBean.getName() + " Flagged for Approval & Archiving Successfully..";
    }else{


      wPT = this.genericService.loadObjectUsingRestriction(PromotionTracker.class,Arrays.asList(CustomPredicate.procurePredicate("employee.id", pEmpId),
              getBusinessClientIdPredicate(request)));

      empHrBean.setPromoTracker(wPT);
      if(pArrearsAdded > 0){
        AbstractSpecialAllowanceEntity wSAI = (AbstractSpecialAllowanceEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getSpecialAllowanceInfoClass(bc), Arrays.asList(CustomPredicate.procurePredicate("id",pArrearsAdded),
                getBusinessClientIdPredicate(request)));
        empHrBean.setShowArrearsRow(SHOW_ROW);

        empHrBean.setAmountStr(PayrollHRUtils.getDecimalFormat().format(wSAI.getAmount()));
        empHrBean.setArrearsStartDate(wSAI.getStartDate());
        empHrBean.setArrearsEndDate(wSAI.getEndDate());
        actionCompleted = bc.getStaffTypeName()+" " + empHrBean.getName() + " Promoted and Salary Arrears added Successfully.";
      }else{
        empHrBean.setShowArrearsRow(HIDE_ROW);

        actionCompleted = bc.getStaffTypeName()+" " + empHrBean.getName() + " Promoted Successfully.";
      }
    }


    empHrBean.setOldLevelAndStep(wHI.getAbstractEmployeeEntity().getSalaryInfo().getLevelAndStepAsStr());
    empHrBean.setShowForConfirm(HIDE_ROW);
    model.addAttribute(SAVED_MSG, actionCompleted);
    model.addAttribute("saved", true);
    empHrBean.setRankInstId(wHI.getEmployee().getRank().getId());
    if(bc.isLocalGovt()){

      model.addAttribute("rankList", this.genericService.loadAllObjectsWithSingleCondition(Rank.class, CustomPredicate.procurePredicate("cadre.id",wHI.getEmployee().getRank().getCadre().getId()),"name"));
    }

    model.addAttribute("salaryStructureList",wSalaryInfo);

    model.addAttribute("miniBean", empHrBean);
    model.addAttribute("roleBean", bc);
    return VIEW_NAME;
  }
  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("miniBean") HrMiniBean pEHB, 
		  BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);

    

    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
      return REDIRECT_TO_DASHBOARD;
    }
  //Check if Payroll is running...
    PayrollRunMasterBean wPRB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("payrollStatus", ON)));

    if(!wPRB.isNewEntity() && wPRB.isRunning()) {
      result.rejectValue("", "No.Employees", "Payroll is currently being run by " + wPRB.getInitiator().getActualUserName() + ". Promotions can not be effected during a Payroll Run");
       model.addAttribute("status", result);
      addDisplayErrorsToModel(model, request);
      return prepareAndReturnView(pEHB, bc, model, false,false);

    }
    promotionValidator.validate(pEHB, result, bc,transferService);
    if (result.hasErrors()) {
      addDisplayErrorsToModel(model, request);
      model.addAttribute("status", result);
      return prepareAndReturnView(pEHB, bc, model, false,false);
    }
    double amount = 0.0D;
    if (Boolean.valueOf(pEHB.getPayArrearsInd()).booleanValue())
      amount = Double.parseDouble(PayrollHRUtils.removeCommas(pEHB.getAmountStr()));
    SalaryInfo wSI = this.genericService.loadObjectById(SalaryInfo.class, pEHB.getTerminateReasonId());
    SalaryInfo wOldSI = this.genericService.loadObjectById(SalaryInfo.class, pEHB.getOldSalaryInfoInstId());
    boolean spans2Levels = false;
    if(wSI.getLevel() - wOldSI.getLevel() > 1){
      if(wSI.getLevel() == 12 && wOldSI.getLevel() == 10)
        spans2Levels = false;
      else
        spans2Levels = true;
    }

    if( spans2Levels && !pEHB.isFlagWarningIssued()){
      pEHB.setFlagWarningIssued(true);
      result.rejectValue("", "warning", "This promotion spans more than 1 Level.");
      result.rejectValue("", "warning", "Promotion will not be effected immediately. It will be flagged for Approval and Archiving.");
      result.rejectValue("", "warning", "Please check the 'I Agree' check box to continue.");
      model.addAttribute("status", result);
      addDisplayErrorsToModel(model, request);

      return prepareAndReturnView(pEHB, bc, model, true,true);
    }else if(wSI.getLevel() - wOldSI.getLevel() > 1  && pEHB.isFlagWarningIssued() && !Boolean.valueOf(pEHB.getFlagInd()).booleanValue()){

        result.rejectValue("", "warning", "Please check the 'I Agree' check box to proceed.");
        model.addAttribute("status", result);
         addDisplayErrorsToModel(model, request);
        return prepareAndReturnView(pEHB, bc, model, true,true);

    }else {

      if (!pEHB.isWarningIssued()) {
        result.rejectValue("", "warning", "Please confirm this Promotion");
        model.addAttribute("status", result);
        addDisplayErrorsToModel(model, request);
        return prepareAndReturnView(pEHB, bc, model, false,true);


      }
    }
    if(wSI.getLevel() - wOldSI.getLevel() > 1){
      if(wSI.getLevel() == 12 && wOldSI.getLevel() == 10){
        //DO NOTHING.  Level 11 does not exist in the Civil Service.
      }else {
        //--Interject and Flag.
        FlaggedPromotions flaggedPromotions = new FlaggedPromotions();
        flaggedPromotions.setBusinessClientId(bc.getBusinessClientInstId());

        flaggedPromotions.setEmployee(new Employee(pEHB.getParentId()));
        flaggedPromotions.setFromSalaryInfo(wOldSI);
        flaggedPromotions.setToSalaryInfo(wSI);
        flaggedPromotions.setRunMonth(LocalDate.now().getMonthValue());
        flaggedPromotions.setRunYear(LocalDate.now().getYear());
        flaggedPromotions.setMdaInfo(new MdaInfo(pEHB.getMdaInstId()));
        flaggedPromotions.setPromotionDate(LocalDate.now());
        flaggedPromotions.setInitiator(new User(bc.getLoginId()));
        flaggedPromotions.setAuditPayPeriod(PayrollUtils.makePayPeriodForAuditLogs(genericService, bc));
        flaggedPromotions.setRefNumber(pEHB.getRefNumber());
        flaggedPromotions.setRefDate(pEHB.getRefDate());
        flaggedPromotions.setArrears(amount);
        flaggedPromotions.setHireInfoId(pEHB.getHiringInfoId());
        flaggedPromotions.setRejectionReason("Pending Approval");
        // Set Rank for Flagged Promotions as well.....
        if(IppmsUtils.isNotNullAndGreaterThanZero(pEHB.getRankInstId()))
           flaggedPromotions.setNewRank(new Rank(pEHB.getRankInstId()));
        else
           flaggedPromotions.setNewRank(new Rank(pEHB.getOldRankId()));


        return "redirect:promoteEmployee.do?eid=" + pEHB.getParentId() + "&s=1&aa=0&fid=" + genericService.storeObject(flaggedPromotions);
      }
    }


    Long wArrearsAdded = PromotionHelperService.applyPromotion(bc,pEHB.getParentId(),pEHB.getHiringInfoId(),wSI.getLevel(),promotionService,paycheckService,pEHB.getRankInstId(),
            genericService,wOldSI.getId(),wSI.getId(),pEHB.getMdaInstId(),pEHB.getSchoolInstId(),pEHB.getPromoTracker(),amount,pEHB.getRefNumber(),pEHB.getRefDate());


    return "redirect:promoteEmployee.do?eid=" + pEHB.getParentId() + "&s=1&aa="+wArrearsAdded+"&fid=0";
  }
  private String prepareAndReturnView(HrMiniBean pEHB, BusinessCertificate bc, Model model, boolean setFlagRow, boolean setConfirm) throws IllegalAccessException, InstantiationException {

    if (Boolean.valueOf(pEHB.getPayArrearsInd()).booleanValue()){
      pEHB.setShowArrearsRow(SHOW_ROW);
    }else{
      pEHB.setShowArrearsRow(HIDE_ROW);
    }

    if(setConfirm) {
      pEHB.setShowConfirmationRow(setFlagRow);
      pEHB.setShowForConfirm(IConstants.SHOW_ROW);
      pEHB.setWarningIssued(true);
      pEHB.setRefDate(LocalDate.now());
      pEHB.setRefNumber(bc.getUserName());

    }

    if(bc.isLocalGovt()){
      Rank rank = this.genericService.loadObjectById(Rank.class,pEHB.getRankInstId());
      model.addAttribute("rankList", this.genericService.loadAllObjectsWithSingleCondition(Rank.class,
              CustomPredicate.procurePredicate("cadre.id",rank.getCadre().getId()),"name"));
      pEHB.setLevelStepList(this.payrollService.loadSalaryInfoByRankInfo(rank,bc.getBusinessClientInstId(),false));

    }
    model.addAttribute("salaryStructureList",pEHB.getLevelStepList());
    model.addAttribute("miniBean", pEHB);
    model.addAttribute("roleBean", bc);
    return VIEW_NAME;
  }
  private HrMiniBean setRequiredValues(HiringInfo pWhi, HrMiniBean pEmpHrBean)
  {
	  pEmpHrBean.setName(pWhi.getAbstractEmployeeEntity().getDisplayNameWivTitlePrefixed());
     
      pEmpHrBean.setGradeLevelAndStep(pWhi.getAbstractEmployeeEntity().getSalaryInfo().getSalaryType().getName()+":" + pWhi.getAbstractEmployeeEntity().getSalaryInfo().getLevelAndStepAsStr());
    
    pEmpHrBean.setFirstName(pWhi.getAbstractEmployeeEntity().getFirstName());
    pEmpHrBean.setLastName(pWhi.getAbstractEmployeeEntity().getLastName());
    pEmpHrBean.setMiddleName(StringUtils.trimToEmpty(pWhi.getAbstractEmployeeEntity().getInitials()));

    pEmpHrBean.setHireDate(PayrollHRUtils.getFullDateFormat().format(pWhi.getHireDate()));

    int noOfYears = LocalDate.now().getYear() - pWhi.getHireDate().getYear();
    pEmpHrBean.setYearsOfService(String.valueOf(noOfYears));
    pEmpHrBean.setAssignedToObject(pWhi.getAbstractEmployeeEntity().getCurrentMdaName());
    pEmpHrBean.setEmployeeId(pWhi.getAbstractEmployeeEntity().getEmployeeId());
    pEmpHrBean.setSalaryScale(pWhi.getAbstractEmployeeEntity().getSalaryScale());
    pEmpHrBean.setId(pWhi.getId());
    pEmpHrBean.setParentId(pWhi.getAbstractEmployeeEntity().getId());
    pEmpHrBean.setMdaInstId(pWhi.getAbstractEmployeeEntity().getMdaDeptMap().getMdaInfo().getId());
    pEmpHrBean.setOldRankId(pWhi.getAbstractEmployeeEntity().getRank().getId());
    pEmpHrBean.setOldSalaryInfoInstId(pWhi.getAbstractEmployeeEntity().getSalaryInfo().getId());
    
    if(pWhi.getAbstractEmployeeEntity().isSchoolStaff()){
    	pEmpHrBean.setSchoolInstId(pWhi.getAbstractEmployeeEntity().getSchoolInfo().getId());
    	pEmpHrBean.setSchoolAssigned(true);
    }
    return pEmpHrBean;
  }
}