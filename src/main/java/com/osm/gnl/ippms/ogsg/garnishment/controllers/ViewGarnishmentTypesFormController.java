package com.osm.gnl.ippms.ogsg.garnishment.controllers;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.pagination.beans.DataTableBean;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping({"/viewGarnishTypes.do"})
@SessionAttributes(types={PaginatedBean.class})
public class ViewGarnishmentTypesFormController extends BaseController
{
   
  private final int pageLength = 20;
  private final String VIEW_NAME = "garnishment/viewEmpGarnishmentTypeForm";


  public ViewGarnishmentTypesFormController() { }

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);

      List<?> empList = this.genericService.loadAllObjectsWithSingleCondition(EmpGarnishmentType.class, CustomPredicate.procurePredicate("businessClientId", this.getBusinessCertificate(request).getBusinessClientInstId()),"name");

    DataTableBean wPELB = new DataTableBean(empList);

    wPELB.setShowRow(SHOW_ROW);
    model.addAttribute("roleBean", super.getBusinessCertificate(request));
    model.addAttribute("garnishTypeBean", wPELB);
    model.addAttribute("displayList", wPELB.getObjectList());

    return VIEW_NAME;
  }
  @RequestMapping(method={RequestMethod.GET},params={"gn","tc"})
  public String setupForm(@RequestParam("gn") String pTypeName,
		  @RequestParam("tc") String pTypeCode,Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);


      PaginationBean paginationBean = this.getPaginationInfo(request);
    PredicateBuilder predicateBuilder = new PredicateBuilder();
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", this.getBusinessCertificate(request).getBusinessClientInstId()));
    String wTypeName = null;
    if(!IppmsUtils.treatNull(pTypeName).equals(EMPTY_STR)){
    	wTypeName = IppmsUtils.treatNull(pTypeName);
      predicateBuilder.addPredicate(CustomPredicate.procurePredicate("description", wTypeName, Operation.LIKE));

    }
    String wTypeCode = null;
    
    if(!IppmsUtils.treatNull(pTypeCode).equals(EMPTY_STR)){
    	wTypeCode = IppmsUtils.treatNull(pTypeCode);
      predicateBuilder.addPredicate(CustomPredicate.procurePredicate("name", wTypeCode, Operation.LIKE));
    }




    List<?> empList = this.genericService.loadPaginatedObjects(EmpGarnishmentType.class,predicateBuilder.getPredicates(),
            (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());



    int wGLNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder,EmpGarnishmentType.class);

    PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements,
            paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    wPELB.setShowRow(SHOW_ROW);
    model.addAttribute("roleBean", super.getBusinessCertificate(request));
    model.addAttribute("garnishTypeBean", wPELB);
    
     

    return VIEW_NAME;
  }
  
  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_update", required=false) String cancel, 
		  @ModelAttribute("garnishTypeBean") PaginatedBean pEHB, 
		  BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);
	     
	  
    return "redirect:viewGarnishTypes.do?gn="+IppmsUtils.treatNull(pEHB.getName())+"&tc="+IppmsUtils.treatNull(pEHB.getTypeCode());
  }
}