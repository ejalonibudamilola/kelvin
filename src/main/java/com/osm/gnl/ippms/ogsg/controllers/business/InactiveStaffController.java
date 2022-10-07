package com.osm.gnl.ippms.ogsg.controllers.business;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.TerminateReason;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBeanInactive;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
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
import java.util.List;


/**
 * @author Damilola Ejalonibu
 */

@Controller
@RequestMapping({"/inactiveEmpOverview.do"})
@SessionAttributes(types={BusinessEmpOVBeanInactive.class})
@SuppressWarnings({"unchecked", "rawtypes"})

public class InactiveStaffController extends BaseController{

  private final int pageLength = 20;
  private final String VIEW = "business/busInactiveEmpPopupOverviewForm";

  @Autowired
  PaycheckService paycheckService;

  @Autowired
  EmployeeService employeeService;

    @ModelAttribute("mdaList")
    protected List<MdaInfo> loadAllMdas(HttpServletRequest request){

        return this.genericService.loadAllObjectsWithSingleCondition(MdaInfo.class,CustomPredicate.procurePredicate("businessClientId", getBusinessCertificate(request).getBusinessClientInstId()), "name");

    }

  @ModelAttribute("userList")
  protected List<User> populateUserList() {
        return this.genericService.loadAllObjectsWithSingleCondition(User.class,
                CustomPredicate.procurePredicate("accountLocked", IConstants.OFF), "firstName");
  }

  @ModelAttribute("monthList")
  protected List<NamedEntity> getMonthsList(){
	  return PayrollBeanUtils.makeAllMonthList();
  }


    @ModelAttribute("yearList")
    protected List<NamedEntity> getYearList(HttpServletRequest request){
        return this.paycheckService.makePaycheckYearList(getBusinessCertificate(request));
    }

  @ModelAttribute("schoolList")
  protected List<SchoolInfo> populateSchoolList(HttpServletRequest request) {
      BusinessCertificate bc = super.getBusinessCertificate(request);
        return this.genericService.loadAllObjectsWithSingleCondition(SchoolInfo.class,
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), "name");
  }
  @ModelAttribute("termReasonList")
  protected List<TerminateReason> populateTermReasonList() {
	  return this.genericService.loadControlEntity(TerminateReason.class);
	  
  }
  @Autowired
  public InactiveStaffController()
  {}

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);


      PaginationBean paginationBean = getPaginationInfo(request);

    List empList = this.employeeService.getInActiveEmployees((paginationBean.getPageNumber() - 1) * this.pageLength,
            this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(), null, null, null, null,null,null,null,"0", bc,true);

    int wNoOfElements = this.employeeService.getTotalNoOfInActiveEmployees(null, null, null, false, false,null,null,null,null,"0", bc);

    BusinessEmpOVBeanInactive pList = new BusinessEmpOVBeanInactive(empList, paginationBean.getPageNumber(),
            this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
    pList.setShowingInactive(true);
    if(empList != null && !empList.isEmpty())
    	pList.setShowLink(true);
    pList.setId(new Long(0L));
    pList.setHidden("hidden");
    pList.setFromDateStr("");
    pList.setToDateStr("");
    pList.setTerminateReasonId(new Long(0L));
    pList.setSchoolInstId(new Long(0L));
    pList.setRunMonth(-1);
    pList.setMdaInstId(new Long(0L));
    pList.setShowRow(HIDE_ROW);
    pList.setPayPeriodStr("0");
    
    model.addAttribute("busEmpOVBean", pList);
    addRoleBeanToModel(model, request);
    return VIEW;
  }

  @RequestMapping(method={RequestMethod.GET}, params={"fd", "td", "tid","uid","eid","mid","sid","pp"})
  public String setupForm(
		  @RequestParam("fd") String pFromDate, 
		  @RequestParam("td") String pToDate, 
		  @RequestParam("tid") Long pTermId, 
		  @RequestParam("uid") Long pUid,
		  @RequestParam("eid") Long pEid,
		  @RequestParam("mid") Long pMdaId,
		  @RequestParam("sid") Long pSchoolId,
		  @RequestParam("pp") String pPayPeriod,
		  Model model, HttpServletRequest request)
    throws Exception
  {


	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);

      PaginationBean paginationBean = getPaginationInfo(request);


      LocalDate fDate = null;


      LocalDate tDate = null;
    
    
    
    boolean useDates = (pFromDate != null && !StringUtils.trimToEmpty(pFromDate).equals(EMPTY_STR) && pToDate != null && !StringUtils.trimToEmpty(pToDate).equals(EMPTY_STR));
     
    boolean useEmpId = IppmsUtils.isNotNullAndGreaterThanZero(pEid);

     if(useDates){
    	fDate = PayrollBeanUtils.setDateFromString(pFromDate);
    	tDate = PayrollBeanUtils.setDateFromString(pToDate);
     }
    boolean usingTermId = IppmsUtils.isNotNullAndGreaterThanZero(pTermId);

      
    List empList = this.employeeService.getInActiveEmployees((paginationBean.getPageNumber() - 1) * this.pageLength,
            this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(), fDate, tDate, pTermId, pUid,pEid,pMdaId,pSchoolId,pPayPeriod,bc,true);

    int wNoOfElements = this.employeeService.getTotalNoOfInActiveEmployees(fDate, tDate, pTermId, useDates, usingTermId,pUid,pEid,pMdaId,pSchoolId,pPayPeriod, bc);

    BusinessEmpOVBeanInactive pList = new BusinessEmpOVBeanInactive(empList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
    if(empList != null && !empList.isEmpty())
    	pList.setShowLink(true);
    pList.setId(pUid);
    pList.setHidden("hidden");

    if(useEmpId){
        String wEmployeeId = genericService.loadObjectById(Employee.class,  pEid).getEmployeeId();
        pList.setStaffId(wEmployeeId);
    	pList.setParentId(pEid);

    }

    if(useDates){
    	pList.setFromDate(fDate);
    	pList.setToDate(tDate);
    	pList.setFromDateStr(pFromDate);
    	pList.setToDateStr(pToDate);
     }
    
    pList.setTerminateReasonId(pTermId); 
    pList.setMdaInstId(pMdaId);
     
    if(IppmsUtils.isNotNullAndGreaterThanZero(pSchoolId))
    	pList.setFilteredBySchool(true);
     
    if(pPayPeriod.length() > 1){
    	pList.setRunMonth(Integer.parseInt(pPayPeriod.substring(0,2)));
    	pList.setRunYear(Integer.parseInt(pPayPeriod.substring(2)));
    }else{
    	pList.setRunMonth(-1);
    	pList.setRunYear(0);
    }
    
    pList.setPayPeriodStr(pPayPeriod);
    pList.setSchoolInstId(pSchoolId);
    model.addAttribute("busEmpOVBean", pList);
    addRoleBeanToModel(model, request);
    return VIEW;
  }

  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep, @RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("busEmpOVBean") BusinessEmpOVBeanInactive pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);

     

    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
      return REDIRECT_TO_DASHBOARD;
    }
     
    
    if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {
    	
    	boolean filterByEmployee = false;
        Long empId = 0L;
        if(!StringUtils.trimToEmpty(pLPB.getStaffId()).equals(EMPTY_STR)){
        	  if(!pLPB.getStaffId().toUpperCase().startsWith("OG")){
        		  pLPB.setStaffId("OG"+pLPB.getStaffId());
        	  } 
        		  //Employee wEmp = (Employee) this.payrollService.loadObectByClassAndName(Employee.class, "employeeId", pLPB.getOgNumber().toUpperCase());

            empId =  ((AbstractEmployeeEntity)this.genericService.loadObjectWithSingleCondition(IppmsUtils.getEmployeeClass(bc),CustomPredicate.procurePredicate( "employeeId", pLPB.getStaffId().toUpperCase()))).getId();

        	  if( empId != null && empId > 0){

        			filterByEmployee = !filterByEmployee;
        		  }else{
        			    result.rejectValue("", "InvalidValue", "No "+bc.getStaffTitle()+" found with "+bc.getStaffTitle()+" "+pLPB.getStaffId());
        		        addDisplayErrorsToModel(model, request);
        		        model.addAttribute("status", result);
        		        model.addAttribute("miniBean", pLPB);
                        addRoleBeanToModel(model, request);
        		        return VIEW; 
        		  }
        	  
          }
        boolean wUseDates = true;
      if ((pLPB.getFromDate() == null) || (pLPB.getToDate() == null))
      {
    	  if(!filterByEmployee && pLPB.getId() != null && pLPB.getId().intValue() == 0
    			  && (pLPB.getRunMonth() == -1 && pLPB.getRunYear() == 0) && pLPB.getTerminateReasonId() == 0){
	        result.rejectValue("", "InvalidValue", "Please select valid Dates");
	        addDisplayErrorsToModel(model, request);
	
	        model.addAttribute("status", result);
	        model.addAttribute("miniBean", pLPB);

	        addRoleBeanToModel(model, request);
	        return VIEW;
    	  }else{
    		  wUseDates = false;
    	  }
      }
      String sDate = ""; 
      String eDate = "";
      if(wUseDates){
    	  	  LocalDate date1 = null;
		      LocalDate date2 = null;
		      if ((pLPB.getFromDate() == null) || (pLPB.getToDate() == null))
		      {
		    	  
			        result.rejectValue("", "InvalidValue", "Please select valid Dates");
			        addDisplayErrorsToModel(model, request);
			
			        model.addAttribute("status", result);
			        model.addAttribute("miniBean", pLPB);

                    addRoleBeanToModel(model, request);
			        return VIEW;
		    	  
		      }
		      date1 = pLPB.getFromDate();
		      date2 = pLPB.getToDate();
		
		      if (date1.isAfter(date2)) {
		        result.rejectValue("", "InvalidValue", "'Between' Date can not be greater than 'And' Date ");
		        addDisplayErrorsToModel(model, request);
		
		        model.addAttribute("status", result);
		        model.addAttribute("miniBean", pLPB);

		        addRoleBeanToModel(model, request);
		        return VIEW;
		      }
		
		       
		
		       sDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getFromDate());
		       eDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getToDate());
		        

		    } 
      		//-- Here make sure the Pay Period is Makeable...
      		String wPayPeriod = "0";
      		
      		if(pLPB.getRunMonth() != -1 && pLPB.getRunYear() == 0){
      			result.rejectValue("", "InvalidValue", "Please select value for 'Year'");
                addDisplayErrorsToModel(model, request);
		
		        model.addAttribute("status", result);
		        model.addAttribute("miniBean", pLPB);

                addRoleBeanToModel(model, request);
		        return VIEW;
      		}else if(pLPB.getRunMonth() == -1 && pLPB.getRunYear() != 0){
      			result.rejectValue("", "InvalidValue", "Please select value for 'Month'");
                addDisplayErrorsToModel(model, request);
		
		        model.addAttribute("status", result);
		        model.addAttribute("miniBean", pLPB);
		
		        addRoleBeanToModel(model, request);
		        return VIEW;
      		}else if(pLPB.getRunMonth() > 0 && pLPB.getRunYear() > 0){
      			
      			wPayPeriod = PayrollUtils.makeAuditPayPeriod(pLPB.getRunMonth(), pLPB.getRunYear());
      		}
      		if(pLPB.getSchoolInstId() == null)
      			pLPB.setSchoolInstId(0L);
      		
            if(sDate.equalsIgnoreCase(EMPTY_STR) || eDate.equalsIgnoreCase(EMPTY_STR)){
            	 return "redirect:inactiveEmpOverview.do?fd=&td=&tid="+pLPB.getTerminateReasonId()+"&uid=" + pLPB.getId()+"&eid="+empId+"&mid="+pLPB.getMdaInstId()+"&sid="+pLPB.getSchoolInstId()+"&pp="+wPayPeriod;
            }else{
            	return "redirect:inactiveEmpOverview.do?fd=" + sDate + "&td=" + eDate+"&tid="+pLPB.getTerminateReasonId() + "&uid=" + pLPB.getId()+"&eid="+empId+"&mid="+pLPB.getMdaInstId()+"&sid="+pLPB.getSchoolInstId()+"&pp="+wPayPeriod;
            }
    }
    return "redirect:inactiveEmpOverview.do";
  }
}