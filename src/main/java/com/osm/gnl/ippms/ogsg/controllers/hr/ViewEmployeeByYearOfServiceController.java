package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.BaseEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBeanInactive;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping({"/employeeYOSForm.do"})
@SessionAttributes(types={BusinessEmpOVBeanInactive.class})
public class ViewEmployeeByYearOfServiceController extends BaseController
{
  
  private final int pageLength = 20;

  private static final String VIEW_NAME = "employee/viewEmpByYearOfServiceForm";
  

   
  public ViewEmployeeByYearOfServiceController()
  {}

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);
		
	  BusinessCertificate bc = this.getBusinessCertificate(request);


    List<BaseEntity> wRetList = new ArrayList<BaseEntity>();
    for (long i = 1; i < 36; i++)
    {
      BaseEntity b = new BaseEntity();
      b.setId(i);
      wRetList.add(b);
    }
    BusinessEmpOVBeanInactive wBEOB = new BusinessEmpOVBeanInactive(new ArrayList<>(), 1, this.pageLength, 0, null, "asc");

    wBEOB.setShowRow(HIDE_ROW);
    wBEOB.setYearsList(wRetList);
    addRoleBeanToModel(model, request);
    model.addAttribute("yearsList", wRetList);
    model.addAttribute("miniBean", wBEOB);

    return VIEW_NAME;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"fd", "td"})
  public String setupForm(@RequestParam("fd") int pFy, @RequestParam("td") int pTy, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);
      boolean usingBaseYear = false;
      boolean usingToYear = false;
	  BusinessCertificate bc = this.getBusinessCertificate(request);

	  PaginationBean paginationBean = getPaginationInfo(request);

	  LocalDate wBaseYear = null;
      LocalDate wToYearMin = null;
      LocalDate wBaseYearMax = null;
	  LocalDate wToday = LocalDate.now();
      LocalDate wToYear = null;

	  if (pTy > -1){
	    usingToYear = true;
	    int diff = wToday.getYear();
	    int mainDiff = diff - pTy;
	    wToYear = LocalDate.of(mainDiff, 12, 31);
	    wToYearMin = LocalDate.of(mainDiff, 1, 1);
      }
    if (pFy > -1){
      usingBaseYear = true;
      int diff = wToday.getYear();
      int mainDiff = diff - pFy;
      wBaseYearMax = LocalDate.of(mainDiff, 12, 31);
      wBaseYear = LocalDate.of(mainDiff, 1, 1);
    }

    ArrayList<CustomPredicate> customPredicates = new ArrayList<>();
    customPredicates.add(getBusinessClientIdPredicate(request));
    customPredicates.add(CustomPredicate.procurePredicate("terminateInactive", "N"));
    if ((usingToYear) && (usingBaseYear)) {
      customPredicates.add(CustomPredicate.procurePredicate("hireDate", wToYearMin, Operation.GREATER_OR_EQUAL));
      customPredicates.add(CustomPredicate.procurePredicate("hireDate", wBaseYearMax, Operation.LESS_OR_EQUAL));
    }
    else if ((usingBaseYear) && (!usingToYear)){
      customPredicates.add(CustomPredicate.procurePredicate("hireDate", wBaseYear, Operation.GREATER_OR_EQUAL));
      customPredicates.add(CustomPredicate.procurePredicate("hireDate", wBaseYearMax, Operation.LESS_OR_EQUAL));
    }
    else if ((usingToYear) && (!usingBaseYear)){
      customPredicates.add(CustomPredicate.procurePredicate("hireDate", wToYearMin, Operation.GREATER_OR_EQUAL));
      customPredicates.add(CustomPredicate.procurePredicate("hireDate", wToYear, Operation.LESS_OR_EQUAL));
    }

    List<HiringInfo> empList = this.genericService.loadAllObjectsUsingRestrictions(HiringInfo.class, customPredicates, null);
//    List<HiringInfo> empList = this.genericService.loadPaginatedObjects(HiringInfo.class, customPredicates, (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

    Collections.sort(empList);


    PredicateBuilder predicateBuilder = new PredicateBuilder();

    predicateBuilder.addPredicate(getBusinessClientIdPredicate(request));
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("terminateInactive", "N"));
    if ((usingToYear) && (usingBaseYear)) {
      predicateBuilder.addPredicate(CustomPredicate.procurePredicate("hireDate", wToYearMin, Operation.GREATER_OR_EQUAL));
      predicateBuilder.addPredicate(CustomPredicate.procurePredicate("hireDate", wBaseYearMax, Operation.LESS_OR_EQUAL));
    }
    else if ((usingBaseYear) && (!usingToYear)){
      predicateBuilder.addPredicate(CustomPredicate.procurePredicate("hireDate", wBaseYear, Operation.GREATER_OR_EQUAL));
      predicateBuilder.addPredicate(CustomPredicate.procurePredicate("hireDate", wBaseYearMax, Operation.LESS_OR_EQUAL));
  }
    else if ((usingToYear) && (!usingBaseYear)){
      predicateBuilder.addPredicate(CustomPredicate.procurePredicate("hireDate", wToYearMin, Operation.GREATER_OR_EQUAL));
      predicateBuilder.addPredicate(CustomPredicate.procurePredicate("hireDate", wToYear, Operation.LESS_OR_EQUAL));
    }

    int wGLNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, HiringInfo.class);

    BusinessEmpOVBeanInactive wBEOB = new BusinessEmpOVBeanInactive(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    wBEOB.setShowRow(SHOW_ROW);

    wBEOB.setFromYear(pFy);
    wBEOB.setToYear(pTy);

    List<BaseEntity> wRetList = new ArrayList<BaseEntity>();
    for (long i = 1; i < 36; i++)
    {
      BaseEntity b = new BaseEntity();
      b.setId(i);
      wRetList.add(b);
    }
    wBEOB.setYearsList(wRetList);
    addRoleBeanToModel(model, request);
    model.addAttribute("yearsList", wRetList);
    model.addAttribute("miniBean", wBEOB);

    return VIEW_NAME;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep, @RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("miniBean") BusinessEmpOVBeanInactive pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
		
	  BusinessCertificate bc = this.getBusinessCertificate(request);
 

    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
      return "redirect:hrRelatedReports.do";
    }
     
    
      if ((pLPB.getFromYear() == -1) && (pLPB.getToYear() == -1))
      {
        result.rejectValue("", "InvalidValue", "Please select at least 1 valid No. of Years!");
        ((BusinessEmpOVBeanInactive)result.getTarget()).setDisplayErrors("block");

        model.addAttribute("status", result);
        addRoleBeanToModel(model, request);
        model.addAttribute("yearsList", pLPB.getYearsList());
        model.addAttribute("miniBean", pLPB);

        return VIEW_NAME;
      }
      if ((pLPB.getFromYear() > -1) && (pLPB.getToYear() > -1) && 
        (pLPB.getFromYear() > pLPB.getToYear())) {
        result.rejectValue("", "InvalidValue", "'Between' No. of year can not be greater than 'And' No. of years");
        ((BusinessEmpOVBeanInactive)result.getTarget()).setDisplayErrors("block");

        model.addAttribute("status", result);
        model.addAttribute("yearsList", pLPB.getYearsList());
        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", pLPB);

        return VIEW_NAME;
      
      }
      return "redirect:employeeYOSForm.do?fd=" + pLPB.getFromYear() + "&td=" + pLPB.getToYear();
    

    
  }
}