package com.osm.gnl.ippms.ogsg.controllers.audit;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.audit.domain.AbstractGarnishmentAuditEntity;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
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
@RequestMapping({"/viewGarnishLog.do"})
@SessionAttributes(types = {DataTableBean.class})
public class GarnishmentAuditLogFormController extends BaseController
{
  
  
  private final int pageLength = 20;
  private final String VIEW_NAME = "audit/garnishmentAuditLogForm";
  
  public GarnishmentAuditLogFormController()
  {}
  
  @ModelAttribute("userList")
  public List<User> populateUsersList(HttpServletRequest request) {

      return this.genericService.loadAllObjectsWithSingleCondition(User.class,CustomPredicate.procurePredicate("role.businessClient.id",getBusinessCertificate(request).getBusinessClientInstId()),"username");
  }
  
  @ModelAttribute("garnTypeList")
  public List<EmpGarnishmentType> populateGarnTypeList(HttpServletRequest request) {
      return this.genericService.loadAllObjectsWithSingleCondition(EmpGarnishmentType.class,getBusinessClientIdPredicate(request), "description");
  }

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);

      DataTableBean wBEOB = new DataTableBean(new ArrayList<Object>());

    wBEOB.setShowRow(HIDE_ROW);
      return makeAndReturnView(model,getBusinessCertificate(request),wBEOB,null);
  }

  @RequestMapping(method={RequestMethod.GET}, params={"fd", "td", "uid","eid","gtid"})
  public String setupForm(@RequestParam("fd") String pFd, @RequestParam("td") String pTd, 
		  @RequestParam("uid") Long pUid,@RequestParam("eid") Long pEmpId,@RequestParam("gtid") Long pTypeId, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);
	     
	    BusinessCertificate bc = this.getBusinessCertificate(request);
    LocalDate fDate = null;
    LocalDate tDate = null;
    boolean usingDates = false;
    if( !IppmsUtils.treatNull(pFd).equals(EMPTY_STR)
    		&&  !IppmsUtils.treatNull(pFd).equals(EMPTY_STR)){
    	usingDates = true;
    	fDate = PayrollBeanUtils.setDateFromString(pFd);
    	tDate = PayrollBeanUtils.setDateFromString(pTd);
    }
    

//    PaginationBean paginationBean = getPaginationInfo(request);

    List<CustomPredicate> predicates = new ArrayList<>();

    predicates.add(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));

    if(IppmsUtils.isNotNullAndGreaterThanZero(pUid)){
        predicates.add(CustomPredicate.procurePredicate("user.id", pUid));
    }

    if(IppmsUtils.isNotNullAndGreaterThanZero(pTypeId)){
        predicates.add(CustomPredicate.procurePredicate("garnishmentType.id", pTypeId));
    }
    
   if(IppmsUtils.isNotNullAndGreaterThanZero(pEmpId)){
       predicates.add(CustomPredicate.procurePredicate("employee.id", pEmpId));
   }
      if( usingDates){

          predicates.add(CustomPredicate.procurePredicate("lastModTs", PayrollBeanUtils.getNextORPreviousDay(fDate,false), Operation.GREATER));
          predicates.add(CustomPredicate.procurePredicate("lastModTs", PayrollBeanUtils.getNextORPreviousDay(tDate,true), Operation.LESS));
      }
      List<?>  empList = this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getGarnishAuditClass(bc),predicates,null);

      DataTableBean wPELB = new DataTableBean(empList);
    wPELB.setShowRow(SHOW_ROW);
    
    wPELB.setShowLink(empList != null && !empList.isEmpty() && empList.size() > 0);
    if(usingDates){
    	wPELB.setFromDate(fDate);
        wPELB.setToDate(tDate);
        wPELB.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(wPELB.getFromDate()));
        wPELB.setToDateStr(PayrollBeanUtils.getJavaDateAsString(wPELB.getToDate()));
         
    }else{
    	wPELB.setNotUsingDates(true);
    }
    
    wPELB.setId(pUid);
    if(IppmsUtils.isNotNullAndGreaterThanZero(pEmpId)){
    	AbstractEmployeeEntity employee =  IppmsUtils.loadEmployee(genericService,pEmpId,bc);
    	wPELB.setOgNumber(employee.getEmployeeId());
    	wPELB.setEmpInstId(pEmpId);
    }
    wPELB.setDeductionTypeId(pTypeId);
      return makeAndReturnView(model,bc,wPELB,null);
  }
  @RequestMapping(method={RequestMethod.GET}, params={"uid","eid","gtid"})
  public String setupForm(@RequestParam("uid") Long pUid,@RequestParam("eid") Long pEmpId,@RequestParam("gtid") Long pTypeId, 
		  Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);
	     
	    BusinessCertificate bc = this.getBusinessCertificate(request);
//      PaginationBean paginationBean = getPaginationInfo(request);

      List<CustomPredicate> predicates = new ArrayList<>();

      predicates.add(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));

      if(IppmsUtils.isNotNullAndGreaterThanZero(pUid)){
          predicates.add(CustomPredicate.procurePredicate("user.id", pUid));
      }

      if(IppmsUtils.isNotNullAndGreaterThanZero(pTypeId)){
          predicates.add(CustomPredicate.procurePredicate("garnishmentType.id", pTypeId));
      }

      if(IppmsUtils.isNotNullAndGreaterThanZero(pEmpId)){
          predicates.add(CustomPredicate.procurePredicate("employee.id", pEmpId));
      }

    List<?> empList = null;
    int wGLNoOfElements = 0;


      empList = this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getGarnishAuditClass(bc),predicates,null);

      DataTableBean wPELB = new DataTableBean(empList);

    wPELB.setShowRow(SHOW_ROW);
    
    wPELB.setShowLink(empList != null && !empList.isEmpty() && empList.size() > 0);

      if(IppmsUtils.isNotNullAndGreaterThanZero(pEmpId)){
          AbstractEmployeeEntity employee =  IppmsUtils.loadEmployee(genericService,pEmpId,bc);
          wPELB.setOgNumber(employee.getEmployeeId());
          wPELB.setEmpInstId(pEmpId);
      }
    wPELB.setDeductionTypeId(pTypeId);
    wPELB.setId(pUid);
    return makeAndReturnView(model,bc,wPELB,null);
  }
  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep,
                              @RequestParam(value="_cancel", required=false) String cancel,
                              @ModelAttribute("miniBean") DataTableBean pLPB,
                              BindingResult result, SessionStatus status, Model model,
                              HttpServletRequest request) throws Exception {
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

          AbstractEmployeeEntity employee = (AbstractEmployeeEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getEmployeeClass(bc), Arrays.asList(CustomPredicate.procurePredicate("employeeId",pLPB.getOgNumber()),
                  getBusinessClientIdPredicate(request)));
	      if( !employee.isNewEntity()){
      			filterByEmployee = true;
      		  }else{
      			    result.rejectValue("", "InvalidValue", "No "+employeeText(request)+" found with "+bc.getStaffTitle()+": "+pLPB.getOgNumber());
      		         model.addAttribute(DISPLAY_ERRORS,BLOCK);
              return makeAndReturnView(model,bc,pLPB,result);
      		  }
      	  
        }
       // boolean wUseDates = true;
      if (pLPB.getFromDate() == null  ||  pLPB.getToDate() == null)
      {
    	     result.rejectValue("", "InvalidValue", "Please select valid Dates");
          addDisplayErrorsToModel(model, request);
          return makeAndReturnView(model,bc,pLPB,result);

      }
	      //if(wUseDates){


		
		      if (pLPB.getFromDate().isAfter(pLPB.getToDate())) {
		        result.rejectValue("", "InvalidValue", "'Between' Date can not be greater than 'And' Date ");
                  addDisplayErrorsToModel(model, request);
                  return makeAndReturnView(model,bc,pLPB,result);
		      }
		
		      if (pLPB.getFromDate().getYear() !=pLPB.getToDate().getYear()) {
		        result.rejectValue("", "InvalidValue", "Audit Log dates must be in the same year");
                  addDisplayErrorsToModel(model, request);
                  return makeAndReturnView(model,bc,pLPB,result);
		      }
		      //Also Make sure that we Audit Log Dates are in the same Month.
		      if(pLPB.getFromDate().getMonthValue() !=pLPB.getToDate().getMonthValue()
		    		  && !filterByEmployee){
		    	  result.rejectValue("", "InvalidValue", "Audit Log dates must exists in the same month of the same year if "+bc.getStaffTitle()+" is not used.");
		    	  addDisplayErrorsToModel(model, request);
                  return makeAndReturnView(model,bc,pLPB,result);
		      }
		      String sDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getFromDate());
		      String eDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getToDate());
		      return "redirect:viewGarnishLog.do?fd=" + sDate + "&td=" + eDate + "&uid=" + pLPB.getId()+"&eid="+empId+"&gtid="+pLPB.getDeductionTypeId();

    }
    return "redirect:viewGarnishLog.do";
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