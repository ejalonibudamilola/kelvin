package com.osm.gnl.ippms.ogsg.controllers.audit;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.AuditService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.DataTableBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping({"/viewSpecialAllowanceLog.do"})
@SessionAttributes(types={DataTableBean.class})
public class SpecialAllowanceAuditLogFormController extends BaseController
{
  
  private final int pageLength = 20;

  private final AuditService auditService;
  
  private final String VIEW_NAME = "audit/specialAllowanceAuditForm";
  

  @Autowired
  public SpecialAllowanceAuditLogFormController(AuditService auditService)
  {
      this.auditService = auditService;
  
  }
  @ModelAttribute("userList")
  public List<User> populateUsersList(HttpServletRequest request) {
      return this.genericService.loadAllObjectsWithSingleCondition(User.class,CustomPredicate.procurePredicate("role.businessClient.id",getBusinessCertificate(request).getBusinessClientInstId()),"username");
  }
  
  @ModelAttribute("specAllowTypeList")
  public List<SpecialAllowanceType> populateSpecialAllowTypeList(HttpServletRequest request) {
      return genericService.loadAllObjectsWithSingleCondition(SpecialAllowanceType.class,getBusinessClientIdPredicate(request),"name");
  }
  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);

      DataTableBean wBEOB = new DataTableBean(new ArrayList<Object>());

    wBEOB.setShowRow(HIDE_ROW);
      return makeAndReturnView(model,getBusinessCertificate(request),wBEOB,null);
  }

  @RequestMapping(method={RequestMethod.GET}, params={"fd", "td", "uid","eid","stid"})
  public String setupForm(@RequestParam("fd") String pFd, @RequestParam("td") String pTd, 
		  @RequestParam("uid") Long pUid,@RequestParam("eid") Long pEmpId, 
		  @RequestParam("stid") Long pTypeId, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = getBusinessCertificate(request);

      LocalDate fDate = PayrollBeanUtils.setDateFromString(pFd);
      LocalDate tDate = PayrollBeanUtils.setDateFromString(pTd);

//    PaginationBean paginationBean = getPaginationInfo(request);

    List<?> empList = this.auditService.getSpecAuditLogInfoByClassNameDateAndUserId(bc,
    		PayrollBeanUtils.getNextORPreviousDay(fDate, false),PayrollBeanUtils.getNextORPreviousDay(tDate, true), pUid, pTypeId, pEmpId);

//    int wGLNoOfElements = this.auditService.getSpecTotalNoOfAuditLogInfoByClassNameDateAndUserId(bc,PayrollBeanUtils.getNextORPreviousDay(fDate, false),PayrollBeanUtils.getNextORPreviousDay(tDate, true), pUid,
//    		pTypeId,pEmpId);
//
//    PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

      DataTableBean wPELB = new DataTableBean(empList);

    wPELB.setShowRow(SHOW_ROW);

    wPELB.setFromDate(fDate);
    wPELB.setToDate(tDate);
    wPELB.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(wPELB.getFromDate()));
    wPELB.setToDateStr(PayrollBeanUtils.getJavaDateAsString(wPELB.getToDate()));
    wPELB.setId(pUid);
    if(IppmsUtils.isNotNullAndGreaterThanZero(pTypeId)){
    	wPELB.setDeductionTypeId(pTypeId);
    }
    if(IppmsUtils.isNotNullAndGreaterThanZero(pEmpId)){
        AbstractEmployeeEntity entity = IppmsUtils.loadEmployee(genericService,pEmpId,bc);
    	wPELB.setOgNumber(entity.getEmployeeId());
    	wPELB.setEmpInstId(pEmpId);
    }
    if(empList.size() > 0)
    	wPELB.setShowLink(true);

      return makeAndReturnView(model,bc,wPELB,null);
  }
  
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"uid","eid","stid"})
  public String setupForm(@RequestParam("uid") Long pUid,@RequestParam("eid") Long pEmpId, 
		  @RequestParam("stid") Long pTypeId, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = getBusinessCertificate(request);

      PaginationBean paginationBean = getPaginationInfo(request);


    List<?> empList = this.auditService.getSpecAuditLogInfoByClassNameDateAndUserId(bc, null, null, pUid, pTypeId, pEmpId);

//    int wGLNoOfElements = this.auditService.getSpecTotalNoOfAuditLogInfoByClassNameDateAndUserId(bc,null,null, pUid, pTypeId,pEmpId);
//
//    PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

      DataTableBean wPELB = new DataTableBean(empList);
    wPELB.setShowRow(SHOW_ROW);

    
    wPELB.setId(pUid);
    if(IppmsUtils.isNotNullAndGreaterThanZero(pTypeId)){
    	wPELB.setDeductionTypeId(pTypeId);
    }
    if(IppmsUtils.isNotNullAndGreaterThanZero(pEmpId)){
        AbstractEmployeeEntity entity = IppmsUtils.loadEmployee(genericService,pEmpId,bc);
    	wPELB.setOgNumber(entity.getEmployeeId());
    	wPELB.setEmpInstId(pEmpId);
    	 
    }
    if(empList.size() > 0)
    	wPELB.setShowLink(true);
    
    wPELB.setNotUsingDates(true);
      return makeAndReturnView(model,bc,wPELB,null);
  }
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep,
                              @RequestParam(value="_cancel", required=false) String cancel,
                              @ModelAttribute("miniBean") DataTableBean pLPB, BindingResult result,
                              SessionStatus status, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
	  BusinessCertificate bc = getBusinessCertificate(request);
   

    if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
      return "redirect:auditPageHomeForm.do";
    }
    

    if (isButtonTypeClick(request, REQUEST_PARAM_UPDATE_REPORT)) {
    	
    	boolean filterByEmployee = false;
    	Long empId = 0L;
        if(!StringUtils.trimToEmpty(pLPB.getOgNumber()).equals(EMPTY_STR)){

      		  pLPB.setOgNumber(IppmsUtils.treatOgNumber(bc,pLPB.getOgNumber()));

            AbstractEmployeeEntity wEmp = this.genericService.loadObjectUsingRestriction(Employee.class, Arrays.asList(CustomPredicate.procurePredicate("employeeId",pLPB.getOgNumber()),
                    getBusinessClientIdPredicate(request)));
            if(IppmsUtils.isNotNull(wEmp)) {
                empId = wEmp.getId();
            }
      	    if(IppmsUtils.isNotNullAndGreaterThanZero(empId)){
      			 
      			filterByEmployee = !filterByEmployee;
      		  }else{
      			    result.rejectValue("", "InvalidValue", "No "+employeeText(request)+" found with using "+pLPB.getOgNumber());
      		        addDisplayErrorsToModel(model, request);
                return makeAndReturnView(model,bc,pLPB,result);
      		  }
      	  
        }
        boolean wUseDates = true;
      if ((pLPB.getFromDate() == null) && (pLPB.getToDate() == null))
      {
    	  if(!filterByEmployee){
	        result.rejectValue("", "InvalidValue", "Please select valid Dates");
              addDisplayErrorsToModel(model, request);
              return makeAndReturnView(model,bc,pLPB,result);
    	  }else{
    		  wUseDates = false;
    	  }
      }
      if(wUseDates){

	      if (pLPB.getFromDate() == null || pLPB.getToDate() == null)
	      {
	    	   
		        result.rejectValue("", "InvalidValue", "Please select valid Dates");
              addDisplayErrorsToModel(model, request);
              return makeAndReturnView(model,bc,pLPB,result);
	       } 

	      if (pLPB.getFromDate().isAfter(pLPB.getToDate())) {
	        result.rejectValue("", "InvalidValue", "'Between' Date can not be greater than 'And' Date ");
              addDisplayErrorsToModel(model, request);
              return makeAndReturnView(model,bc,pLPB,result);
	      }
	
	      String sDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getFromDate());
	      String eDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getToDate());
      
      return "redirect:viewSpecialAllowanceLog.do?fd=" + sDate + "&td=" + eDate + "&uid=" + pLPB.getId()+"&eid="+empId+"&stid="+pLPB.getDeductionTypeId();
    }else{
        return "redirect:viewSpecialAllowanceLog.do?uid=" + pLPB.getId()+"&eid="+empId+"&stid="+pLPB.getDeductionTypeId();

    }

    }
    return "redirect:viewSpecialAllowanceLog.do";
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