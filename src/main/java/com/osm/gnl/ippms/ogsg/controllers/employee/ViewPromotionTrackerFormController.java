package com.osm.gnl.ippms.ogsg.controllers.employee;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.PaginatedPromotionTrackerBean;
import com.osm.gnl.ippms.ogsg.domain.promotion.PromotionTracker;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * @author Damilola Ejalonibu
 */

@Controller
@RequestMapping({"/viewEmpDueForPromotion.do"})
@SessionAttributes(types={PaginatedPromotionTrackerBean.class})
public class ViewPromotionTrackerFormController extends BaseController
{ 
   
  private final int pageLength = 20;

  public ViewPromotionTrackerFormController()
  {}

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

    LocalDate wToday = LocalDate.now();
    LocalDate fDate = LocalDate.of(wToday.getYear(), wToday.getMonth(), 1);
    LocalDate tDate = LocalDate.of(wToday.getYear(), 12, 31);

    PaginationBean paginationBean = getPaginationInfo(request);

    List<PromotionTracker> empList = this.genericService.loadPaginatedObjects(PromotionTracker.class, Arrays.asList(
            CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
            CustomPredicate.procurePredicate("nextPromotionDate", fDate, Operation.GREATER_OR_EQUAL),
            CustomPredicate.procurePredicate("nextPromotionDate", tDate, Operation.LESS_OR_EQUAL)),
            (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());



    PredicateBuilder predicateBuilder = new PredicateBuilder();
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("nextPromotionDate",fDate));
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()));

//    int wGLNoOfElements = this.payrollService.getTotalNoOfEmpDueForPromotionByDates(bc.getBusinessClientInstId(), fDate, tDate);
    int wGLNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, PromotionTracker.class);


    PaginatedPromotionTrackerBean pCList = new PaginatedPromotionTrackerBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    pCList.setFromDate(fDate);
    pCList.setToDate(tDate);
    pCList.setShowRow(SHOW_ROW);

    pCList.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(fDate));
    pCList.setToDateStr(PayrollBeanUtils.getJavaDateAsString(tDate));

    model.addAttribute("miniBean", pCList);
    addRoleBeanToModel(model, request);
    return "promotion/promotionTrackerViewForm";
  }

  @RequestMapping(method={RequestMethod.GET}, params={"fd", "td"})
  public String setupForm(@RequestParam("fd") String pFd, @RequestParam("td") String pTd, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

    LocalDate fDate = PayrollBeanUtils.setDateFromString(pFd);
    LocalDate tDate = PayrollBeanUtils.setDateFromString(pTd);

    PaginationBean paginationBean = getPaginationInfo(request);

//    List<PromotionTracker> empList = this.payrollService.getEmpDueForPromotionByDates((pageNumber - 1) * this.pageLength, this.pageLength, sortOrder, sortCriterion, fDate.getTime(), tDate.getTime());

    List<PromotionTracker> empList = this.genericService.loadPaginatedObjects(PromotionTracker.class, Arrays.asList(
            CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
            CustomPredicate.procurePredicate("nextPromotionDate", fDate, Operation.GREATER_OR_EQUAL),
            CustomPredicate.procurePredicate("nextPromotionDate", tDate, Operation.LESS_OR_EQUAL)),
            (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

    PredicateBuilder predicateBuilder = new PredicateBuilder();
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("nextPromotionDate",fDate, Operation.GREATER_OR_EQUAL));
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("nextPromotionDate",fDate, Operation.LESS_OR_EQUAL));
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()));

    int wGLNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, PromotionTracker.class);

    PaginatedPromotionTrackerBean pCList = new PaginatedPromotionTrackerBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    pCList.setFromDate(fDate);
    pCList.setToDate(tDate);

    pCList.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(fDate));
    pCList.setToDateStr(PayrollBeanUtils.getJavaDateAsString(tDate));
    pCList.setShowRow(SHOW_ROW);

    model.addAttribute("miniBean", pCList);
    addRoleBeanToModel(model, request);
    return "promotion/promotionTrackerViewForm";
  }
  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep, @RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("miniBean") PaginatedPromotionTrackerBean pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

    

    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
      return REDIRECT_TO_DASHBOARD;
    }
     
    if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {
      if ((pLPB.getFromDate() == null) || (pLPB.getToDate() == null))
      {
        result.rejectValue("", "InvalidValue", "Please select valid Dates");
        addDisplayErrorsToModel(model, request);

        model.addAttribute("status", result);
        model.addAttribute("miniBean", pLPB);

        model.addAttribute("roleBean", bc);
        return "promotionTrackerViewForm";
      }
      LocalDate date1 = pLPB.getFromDate();
      LocalDate date2 = pLPB.getToDate();

      if (date1.isAfter(date2)) {
        result.rejectValue("", "InvalidValue", "'Between' Date can not be greater than 'And' Date ");
        addDisplayErrorsToModel(model, request);

        model.addAttribute("status", result);
        model.addAttribute("miniBean", pLPB);
        addRoleBeanToModel(model, request);

        return "promotion/promotionTrackerViewForm";
      }

      String sDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getFromDate());
      String eDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getToDate());
      return "redirect:viewEmpDueForPromotion.do?fd=" + sDate + "&td=" + eDate;
    }

    return "redirect:viewEmpDueForPromotion.do";
  }
}