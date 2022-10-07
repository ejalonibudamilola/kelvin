package com.osm.gnl.ippms.ogsg.controllers.audit;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.audit.domain.ReassignEmployeeLog;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.DataTableBean;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping({"/viewReassignLog.do"})
@SessionAttributes(types={DataTableBean.class})
public class ReassignmentAuditController extends BaseController
{ 
  
  private final int pageLength = 20;
  private final String VIEW_NAME = "audit/reassignAuditLogForm";
 
  public ReassignmentAuditController()
  {}
  
  @ModelAttribute("userList")
  public List<User> populateUsersList(HttpServletRequest request)
  {
    return this.genericService.loadAllObjectsWithSingleCondition(User.class,CustomPredicate.procurePredicate("role.businessClient.id",getBusinessCertificate(request).getBusinessClientInstId()),"username");
  }

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);


    DataTableBean wBEOB = new DataTableBean(new ArrayList<ReassignEmployeeLog>());

    wBEOB.setShowRow(HIDE_ROW);
    return makeAndReturnView(model,getBusinessCertificate(request),wBEOB,null);
  }

  @RequestMapping(method={RequestMethod.GET}, params={"fd", "td", "uid"})
  public String setupForm(@RequestParam("fd") String pFd, @RequestParam("td") String pTd, 
                          @RequestParam("uid") Long pUid, Model model, 
                          HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException{
	  SessionManagerService.manageSession(request, model);
	     
	    BusinessCertificate bc = this.getBusinessCertificate(request);
    LocalDate fDate = PayrollBeanUtils.setDateFromString(pFd);
    LocalDate tDate = PayrollBeanUtils.setDateFromString(pTd);

//    PaginationBean paginationBean = getPaginationInfo(request);
    List<CustomPredicate> predicates = new ArrayList<>();

    predicates.add(getBusinessClientIdPredicate(request));
     if(IppmsUtils.isNotNullAndGreaterThanZero(pUid))
       predicates.add(CustomPredicate.procurePredicate("user.id", pUid));
      else
       pUid = 0L;


   
    List<ReassignEmployeeLog> empList = null;

    empList = this.genericService.loadAllObjectsUsingRestrictions(ReassignEmployeeLog.class,predicates,null);
//    int wGLNoOfElements = this.genericService.getTotalPaginatedObjects(ReassignEmployeeLog.class,predicates).intValue();
//
//    PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    DataTableBean wPELB = new DataTableBean(empList);

    wPELB.setShowRow(SHOW_ROW);

    if(empList != null && empList.size() > 0){
      wPELB.setShowLink(true);
    }

    wPELB.setFromDate(fDate);
    wPELB.setToDate(tDate);
    wPELB.setId(pUid);
    return makeAndReturnView(model,bc,wPELB,null);
  }
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep,
                              @RequestParam(value="_cancel", required=false) String cancel,
                              @ModelAttribute("miniBean") DataTableBean pLPB,
                              BindingResult result, SessionStatus status, Model model,
                              HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
	     
    

    if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
      return "redirect:auditPageHomeForm.do";
    }
    
    if (isButtonTypeClick(request, REQUEST_PARAM_UPDATE_REPORT)) {
      if ((pLPB.getFromDate() == null) && (pLPB.getToDate() == null))
      {
        result.rejectValue("", "InvalidValue", "Please select valid Dates");
        addDisplayErrorsToModel(model,request);addRoleBeanToModel(model,request);
        addRoleBeanToModel(model,request);
        return makeAndReturnView(model,getBusinessCertificate(request),pLPB,result);
      }

      if (pLPB.getFromDate().isAfter(pLPB.getToDate())) {
        result.rejectValue("", "InvalidValue", "'Between' Date can not be greater than 'And' Date ");
         addDisplayErrorsToModel(model,request);
        addRoleBeanToModel(model,request);
        return makeAndReturnView(model,getBusinessCertificate(request),pLPB,result);
      }

      String sDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getFromDate());
      String eDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getToDate());
      return "redirect:viewReassignLog.do?fd=" + sDate + "&td=" + eDate + "&uid=" + pLPB.getId();
    }

    return "redirect:viewReassignLog.do";
  }
  private String makeAndReturnView(Model model,BusinessCertificate bc,DataTableBean dtb, BindingResult result){

    model.addAttribute("displayList", dtb.getObjectList());
    model.addAttribute("roleBean", bc);
    if(result != null)
      model.addAttribute("status", result);
    model.addAttribute("miniBean", dtb);

    return this.VIEW_NAME;
  }
}