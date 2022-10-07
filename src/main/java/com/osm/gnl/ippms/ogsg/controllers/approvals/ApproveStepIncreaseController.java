/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.approvals;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.PromotionService;
import com.osm.gnl.ippms.ogsg.base.services.StoredProcedureService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.promotion.ManualPromotionBean;
import com.osm.gnl.ippms.ogsg.domain.promotion.StepIncreaseBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping({"/approveStepIncrease.do"})
@SessionAttributes(types={PaginatedBean.class})
public class ApproveStepIncreaseController extends BaseController
{


  private final StoredProcedureService storedProcedureService;
  private final PromotionService promotionService;

  private final int pageLength = 20;
  private final String VIEW_NAME = "promotion/approveStepIncreaseForm";

  @Autowired
  public ApproveStepIncreaseController(StoredProcedureService storedProcedureService, PromotionService promotionService) {
    this.storedProcedureService = storedProcedureService;
    this.promotionService = promotionService;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

    PaginationBean paginationBean = getPaginationInfo(request);

    List<CustomPredicate> predicates = Arrays.asList(getBusinessClientIdPredicate(request));
    List<StepIncreaseBean> wSIBList = this.genericService.loadPaginatedObjects(StepIncreaseBean.class,predicates,(paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

    int wNoOfElements = this.genericService.getTotalPaginatedObjects(StepIncreaseBean.class,predicates).intValue();

    PaginatedBean pPDMB = new PaginatedBean(wSIBList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(),paginationBean.getSortOrder());

    model.addAttribute("approveStepBean", pPDMB);
    model.addAttribute("roleBean", bc);
    return VIEW_NAME;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"s","act"})
  public synchronized String setupForm(@RequestParam("s") int pSaved,@RequestParam("act") String del, Model model, HttpServletRequest request)
          throws Exception
  {
    SessionManagerService.manageSession(request, model);
    String message;
     if(del.equalsIgnoreCase("d")){
       message = " Step Increment deleted successfully";
     }else{
       if(pSaved > 0)
        message = pSaved+" Step Increment performed successfully";
       else
         message = " No Step Increment performed";
     }
    PaginatedBean pPDMB = new PaginatedBean(new ArrayList<>(), 0, this.pageLength, 0, null,null);
     pPDMB.setConfirmation(true);
     addSaveMsgToModel(request,model,message);
    model.addAttribute("approveStepBean", pPDMB);
    model.addAttribute("roleBean", getBusinessCertificate(request));
    return VIEW_NAME;

  }
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"action"})
  public synchronized String setupForm(@RequestParam("action") String del, Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);

	  int wRetVal = this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder().addPredicate(getBusinessClientIdPredicate(request)),StepIncreaseBean.class);

     this.storedProcedureService.callStoredProcedure(IConstants.DEL_STEP_INC,getBusinessCertificate(request).getBusinessClientInstId());

     return "redirect:approveStepIncrease.do?s="+wRetVal+"&act=d";
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_close", required=false) String done, @ModelAttribute("approveStepBean") PaginatedBean ppDMB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

     

    if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
    {
      return REDIRECT_TO_DASHBOARD;
    }

	List<StepIncreaseBean> wSIBList = genericService.loadAllObjectsWithSingleCondition(StepIncreaseBean.class,getBusinessClientIdPredicate(request), null);
    int wRetVal = wSIBList.size();
    if(wRetVal > 0) {
      try {

        ManualPromotionBean wMPBean = new ManualPromotionBean();
        wMPBean.setLastPromoYear(PayrollBeanUtils.getCurrentYearAsString());
        wMPBean.setName(bc.getBusinessName() + " Yearly Step Increment");
        wMPBean.setLastModBy(new User(bc.getLoginId()));
        wMPBean.setLastModTs(Timestamp.from(Instant.now()));
        wMPBean.setRunMonth(LocalDate.now().getMonthValue());
        wMPBean.setRunYear(LocalDate.now().getYear());
        wMPBean.setCreatedBy(wMPBean.getLastModBy());
        wMPBean.setBusinessClientId(bc.getBusinessClientInstId());
        this.genericService.storeObject(wMPBean);
        this.promotionService.updateStaffSalarySteps(wSIBList, bc);
        this.storedProcedureService.callStoredProcedure(IConstants.DEL_STEP_INC,getBusinessCertificate(request).getBusinessClientInstId());
      } catch (Exception wEx) {
        wEx.printStackTrace();
      }

      return "redirect:approveStepIncrease.do?s=" + wRetVal + "&act=s";
    }
    return "redirect:approveStepIncrease.do?s=" + wRetVal + "&act=s";
  }
}