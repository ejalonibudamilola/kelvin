package com.osm.gnl.ippms.ogsg.controllers.audit;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.audit.domain.EmployeeAudit;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.AuditService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.pagination.beans.DataTableBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
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
import java.util.Collection;
import java.util.List;


@Controller
@RequestMapping({"/viewEmployeeLog.do"})
@SessionAttributes(types={DataTableBean.class})
public class EmployeeAuditLogsFormController extends BaseController
{
   
  private final int pageLength = 20;
  private final PaycheckService paycheckService;
  private final AuditService auditService;
  private final String VIEW = "audit/employeeAuditLogForm";
 
  
  @ModelAttribute("mdaList")
  protected List<MdaInfo> loadAllMdas(HttpServletRequest request){

	  return this.genericService.loadAllObjectsWithSingleCondition(MdaInfo.class,CustomPredicate.procurePredicate("businessClientId", getBusinessCertificate(request).getBusinessClientInstId()), "name");

  }
  @ModelAttribute("userList")
  protected List<User> populateUsersList(HttpServletRequest request) {

   return this.genericService.loadAllObjectsWithSingleCondition(User.class,CustomPredicate.procurePredicate("role.businessClient.id",getBusinessCertificate(request).getBusinessClientInstId()),"username");

  }
  @ModelAttribute("monthList")
  protected List<NamedEntity> getMonthsList(){
	  return PayrollBeanUtils.makeAllMonthList();
  }

//  @ModelAttribute("yearList")
//  protected Collection<NamedEntity> getYearList(HttpServletRequest request){
//	  return this.paycheckService.makePaycheckYearList(getBusinessCertificate(request));
//  }

 @ModelAttribute("schoolList")
  protected Collection<SchoolInfo> populateSchoolList(HttpServletRequest request) {
	 return this.genericService.loadAllObjectsWithSingleCondition(SchoolInfo.class,getBusinessClientIdPredicate(request), "name");

 }
  @Autowired
  public EmployeeAuditLogsFormController(PaycheckService paycheckService1, AuditService auditService1)
  {
  	this.paycheckService = paycheckService1;
  	this.auditService = auditService1;
  }
  

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);
	     
	    BusinessCertificate bc = this.getBusinessCertificate(request);

	  DataTableBean wBEOB = new DataTableBean(new ArrayList<EmployeeAudit>());
    wBEOB.setRunMonth(-1);
    wBEOB.setMdaInd(0L);
    wBEOB.setShowRow(HIDE_ROW);
    wBEOB.setId(0L);
    wBEOB.setRoleBean(bc);
	  return makeAndReturnView(model,getBusinessCertificate(request),wBEOB,null);
  }

  @RequestMapping(method={RequestMethod.GET}, params={"fd", "td", "uid","eid","mid","sid","pp"})
  public String setupForm(@RequestParam("fd") String pFd, 
		  @RequestParam("td") String pTd, 
		  @RequestParam("uid") Long pUid,
		  @RequestParam("eid") Long pEid,
		  @RequestParam("mid") Long pMdaId,
		  @RequestParam("sid") Long pSchoolId,
		  @RequestParam("pp") String pPayPeriod,
		  Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);
	     
	    BusinessCertificate bc = this.getBusinessCertificate(request);
    LocalDate fDate = null;
    LocalDate tDate = null;

    boolean useDates = (!StringUtils.trimToEmpty(pFd).equals(EMPTY_STR) && !StringUtils.trimToEmpty(pTd).equals(EMPTY_STR));

    if(useDates){
    	fDate = PayrollBeanUtils.getNextORPreviousDay(PayrollBeanUtils.getLocalDateFromString(pFd),false);
    	tDate =  PayrollBeanUtils.getNextORPreviousDay(PayrollBeanUtils.getLocalDateFromString(pTd),true);
	}
    
    boolean useUserId = (IppmsUtils.isNotNullAndGreaterThanZero(pUid));
    
    boolean useEmpId = (IppmsUtils.isNotNullAndGreaterThanZero(pEid));
    
//   PaginationBean paginationBean = getPaginationInfo(request);

    int wGLNoOfElements = 0;
    List<EmployeeAudit> empList = null;
    if(useDates){
    	empList = this.auditService.getEmployeeAuditLogByDateAndUserId(bc,
    		PayrollBeanUtils.getNextORPreviousDay(fDate, false), PayrollBeanUtils.getNextORPreviousDay(tDate, true), pUid,pEid, useUserId,useEmpId,pMdaId,pSchoolId,pPayPeriod);

    	wGLNoOfElements = this.auditService.getTotalNoOfEmployeeAuditLogByDateAndUserId(bc,
    			PayrollBeanUtils.getNextORPreviousDay(fDate, false), PayrollBeanUtils.getNextORPreviousDay(tDate, true), pUid,pEid, useUserId,useEmpId,pMdaId,pSchoolId,pPayPeriod);
    }else{
    	empList = this.auditService.getEmployeeAuditLogByDateAndUserId(bc,
        		null, null, pUid,pEid, useUserId,useEmpId,pMdaId,pSchoolId,pPayPeriod);
    	wGLNoOfElements = this.auditService.getTotalNoOfEmployeeAuditLogByDateAndUserId(bc, null, null, pUid, pEid, useUserId,useEmpId,pMdaId,pSchoolId,pPayPeriod);

    }
     
     

//    PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
	  DataTableBean wPELB = new DataTableBean(empList);

    wPELB.setShowRow(SHOW_ROW);
    if(useDates){
    	 wPELB.setFromDate(fDate);
         wPELB.setToDate(tDate);
         wPELB.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(fDate));
         wPELB.setToDateStr(PayrollBeanUtils.getJavaDateAsString(tDate));
    }
   
    wPELB.setId( pUid);
    if(useEmpId){
		AbstractEmployeeEntity abstractEmployeeEntity = IppmsUtils.loadEmployee(genericService,pEid,bc);
    	wPELB.setOgNumber(abstractEmployeeEntity.getEmployeeId());
    	wPELB.setEmpInstId(pEid);
    
    }
    if(empList != null && !empList.isEmpty())
    	wPELB.setShowLink(true);
    wPELB.setMdaInd(pMdaId);
    if(IppmsUtils.isNotNullAndGreaterThanZero(pSchoolId)){
    	
    	wPELB.setFilteredBySchool(true);
    } 
    if(pPayPeriod.length() > 1){
    	wPELB.setRunMonth(Integer.parseInt(pPayPeriod.substring(0,2)));
    	wPELB.setRunYear(Integer.parseInt(pPayPeriod.substring(2)));
    }else{
    	wPELB.setRunMonth(-1);
    	wPELB.setRunYear(0);
    }
    
    wPELB.setPayPeriodStr(pPayPeriod);
    wPELB.setSchoolId(pSchoolId);
	  return makeAndReturnView(model,bc,wPELB,null);

  }
  
  
  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep,
							  @RequestParam(value="_cancel", required=false) String cancel,
							  @ModelAttribute("miniBean") DataTableBean pLPB, BindingResult result,
							  SessionStatus status, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
	     
	    BusinessCertificate bc = this.getBusinessCertificate(request);
    
    if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
      return "redirect:auditPageHomeForm.do";
    }
    
    if (isButtonTypeClick(request, REQUEST_PARAM_UPDATE_REPORT)) {
    	boolean filterByEmployee = false;
    	Long empId = 0L;
        if(!StringUtils.trimToEmpty(pLPB.getOgNumber()).equals(EMPTY_STR)){

        		  pLPB.setOgNumber(IppmsUtils.treatOgNumber(bc,pLPB.getOgNumber()));

        		  AbstractEmployeeEntity wEmp = (AbstractEmployeeEntity) this.genericService.loadObjectUsingRestriction(
        		  		IppmsUtils.getEmployeeClass(bc), Arrays.asList(CustomPredicate.procurePredicate("employeeId", pLPB.getOgNumber())
						  , getBusinessClientIdPredicate(request)) );

          	  
        	  if( !wEmp.isNewEntity()){
        	  	    empId = wEmp.getId();
        			filterByEmployee = true;
        		  }else{

        			    result.rejectValue("", "InvalidValue", "No "+employeeText(request)+" found using "+pLPB.getOgNumber());
        		       addDisplayErrorsToModel(model, request);
				  return makeAndReturnView(model,bc,pLPB,result);
        		  }
        	  
          }
      
      boolean wUseDates = true;
      if ((pLPB.getFromDate() == null) && (pLPB.getToDate() == null))
      {
    	  if(!filterByEmployee && pLPB.getId() != null && pLPB.getId().intValue() == 0
    			  && (pLPB.getRunMonth() == -1 && pLPB.getRunYear() == 0)){
	        result.rejectValue("", "InvalidValue", "Please select valid Dates");
			  addDisplayErrorsToModel(model, request);
			  return makeAndReturnView(model,bc,pLPB,result);
    	  }else{
    		  wUseDates = false;
    	  }
      }
      String sDate = ""; 
      String eDate = "";
      if(wUseDates){

		      if ((pLPB.getFromDate() == null) || (pLPB.getToDate() == null))
		      {
		    	  
			        result.rejectValue("", "InvalidValue", "Please select valid Dates");
			        model.addAttribute(DISPLAY_ERRORS,BLOCK);
				  return makeAndReturnView(model,bc,pLPB,result);
		    	  
		      }

		      if (pLPB.getFromDate().isAfter(pLPB.getToDate())) {
		        result.rejectValue("", "InvalidValue", "'Between' Date can not be greater than 'And' Date ");
				  model.addAttribute(DISPLAY_ERRORS,BLOCK);
				  return makeAndReturnView(model,bc,pLPB,result);
		      }
		
		      if (pLPB.getFromDate().getYear() != pLPB.getToDate().getYear()) {
		        result.rejectValue("", "InvalidValue", "Audit Log dates must be in the same year");
				  model.addAttribute(DISPLAY_ERRORS,BLOCK);
				  return makeAndReturnView(model,bc,pLPB,result);
		      }
		
		       sDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getFromDate());
		       eDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getToDate());
		        

		    } 
      		//-- Here make sure the Pay Period is Makeable...
      		String wPayPeriod = "0";
      		
      		if(pLPB.getRunMonth() != -1 && pLPB.getRunYear() == 0){
      			result.rejectValue("", "InvalidValue", "Please select value for 'Year'");
				model.addAttribute(DISPLAY_ERRORS,BLOCK);
				return makeAndReturnView(model,bc,pLPB,result);
      		}else if(pLPB.getRunMonth() == -1 && pLPB.getRunYear() != 0){
      			result.rejectValue("", "InvalidValue", "Please select value for 'Month'");
				model.addAttribute(DISPLAY_ERRORS,BLOCK);
				return makeAndReturnView(model,bc,pLPB,result);
      		}else if(pLPB.getRunMonth() > 0 && pLPB.getRunYear() > 0){
      			
      			wPayPeriod = PayrollUtils.makeAuditPayPeriod(pLPB.getRunMonth(), pLPB.getRunYear());
      		}
      		if(pLPB.getSchoolId() == null)
      			pLPB.setSchoolId(0L);
      		
            if(sDate.equalsIgnoreCase(EMPTY_STR) || eDate.equalsIgnoreCase(EMPTY_STR)){
            	 return "redirect:viewEmployeeLog.do?fd=&td=&uid=" + pLPB.getId()+"&eid="+empId+"&mid="+pLPB.getMdaInd()+"&sid="+pLPB.getSchoolId()+"&pp="+wPayPeriod;
            }else{
            	return "redirect:viewEmployeeLog.do?fd=" + sDate + "&td=" + eDate + "&uid=" + pLPB.getId()+"&eid="+empId+"&mid="+pLPB.getMdaInd()+"&sid="+pLPB.getSchoolId()+"&pp="+wPayPeriod;
            }
           
    }
    return "redirect:viewEmployeeLog.do";
  }
	private String makeAndReturnView(Model model,BusinessCertificate bc,DataTableBean dtb, BindingResult result){

		model.addAttribute("displayList", dtb.getObjectList());
		model.addAttribute("roleBean", bc);
		if(result != null)
			model.addAttribute("status", result);
		model.addAttribute("miniBean", dtb);

		return this.VIEW;
	}
}