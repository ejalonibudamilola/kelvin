package com.osm.gnl.ippms.ogsg.controllers.fileupload;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.FileUploadService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.FileParseBean;
import com.osm.gnl.ippms.ogsg.engine.ProcessLeaveBonus;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.leavebonus.domain.LeaveBonusBean;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

@Controller
@RequestMapping({"/leaveBonusfileUploadReport.do"})
@SessionAttributes(types={FileParseBean.class})
public class LeaveBonusFileUploadReportFormController extends BaseController {

	  private final String VIEW = "fileupload/fileUploadResultForm";

	  @Autowired
	  private FileUploadService fileUploadService;

	  public LeaveBonusFileUploadReportFormController()
	  {

	  }

	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"uid"})
	  public String setupForm(@RequestParam("uid") String pEmpId, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
	  {
		  SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

	    Object wFPB = getSessionAttribute(request, pEmpId);
	    if ((wFPB == null) || (!wFPB.getClass().isAssignableFrom(FileParseBean.class))) {
	      return "redirect:fileUploadFailed.do";
	    }
	    FileParseBean _wFPB = (FileParseBean)wFPB;

	   
	    	_wFPB.setLeaveBonus(true);
	        _wFPB.setName("Leave Bonus Allowance");
	        _wFPB.setDisplayErrors("Leave Bonus To Add.");
	        _wFPB.setDisplayTitle("Leave Bonus Summary");
	       
	   

	    if (_wFPB.isHasSaveList()) {
	      _wFPB.setListToSave(setFormDisplayStyle(_wFPB.getListToSave()));
	    }
	    if (_wFPB.isHasErrorList()) {
	    	addSessionAttribute(request, pEmpId, _wFPB);
	    	return "redirect:displayLeaveBonusError.do?el="+pEmpId;
	     // _wFPB.setErrorList(setFormDisplayStyle(_wFPB.getErrorList()));
	    }
	    model.addAttribute("roleBean", bc);
	    model.addAttribute("fileUploadResult", _wFPB);

	    return VIEW;
	  }

	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"uid", "s"})
	  public String setupForm(@RequestParam("uid") String pEmpId, 
			  @RequestParam("s") int pSaved, Model model, 
			  HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
	  {
		  SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

	    Object wFPB = getSessionAttribute(request, pEmpId);
	    
	    if ((wFPB == null) || (!wFPB.getClass().isAssignableFrom(FileParseBean.class))) {
	      return "redirect:fileUploadFailed.do";
	    }

	   
	   
	      ((FileParseBean)wFPB).setDisplayErrors("Leave Bonus Scheduled Successfully.");
	      ((FileParseBean)wFPB).setDisplayTitle("Leave Bonus Summary");
	     
	   
	    

	    model.addAttribute("saved", Boolean.valueOf(pSaved == 1));
	    model.addAttribute("roleBean", bc);
	    model.addAttribute("fileUploadResult", wFPB);

	    return VIEW;
	  }

	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
			  @RequestParam(value="_create", required=false) String pCreate, 
			  @RequestParam(value="_edit", required=false) String pEdit, 
			  @ModelAttribute("fileUploadResult") FileParseBean pEHB, 
			  BindingResult result, SessionStatus status, Model model, 
			  HttpServletRequest request)
	    throws Exception
	  {
		  SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);



	    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
	      removeSessionAttribute(request, pEHB.getUniqueUploadId());
	      return "redirect:fileUploadDashboard.do";
	    }


	    if (isButtonTypeClick(request,REQUEST_PARAM_EDIT))
	    {
	      return "redirect:fileUploadDashboard.do";
	    }

	    if (isButtonTypeClick(request,REQUEST_PARAM_CREATE))
	    {
	          
	    	//Throw this into another Thread.......
	    	  int wObjType = pEHB.getObjectTypeInd();
	    	  Long wMdaInstId = pEHB.getMdaInstId();
	    	  int wYear = pEHB.getListToSave().get(0).getReportType();
	    	  MdaInfo wMdaName = genericService.loadObjectById(MdaInfo.class,wMdaInstId);
	     	  //First we need to Load Employees that were added to Payroll that Year....
	    	  List<Long> wMapIds = this.fileUploadService.loadMappedIdsByInnerClassId(wMdaInstId);
	    	  LocalDate wStartDate = LocalDate.of(wYear - 1,12,31);
			  LocalDate wEndDate = LocalDate.of(wYear,1,1);
	    	  HashMap<Long, LeaveBonusBean> wPromoList = this.fileUploadService.loadPromotedEmployeeByDates(bc,wStartDate,wEndDate);
	    	  HashMap<Long,LeaveBonusBean> wNewEmployees = this.fileUploadService.getCreatedEmployeeByMdaAndYear(wMapIds,wYear);
	          //Load Leave Bonuses for this Year
			HashMap<Long, Long> wPendingMap = this.fileUploadService.loadLeaveBonusByMdaAndYear(wMdaInstId,wYear);

	    	  ProcessLeaveBonus wPLB = new ProcessLeaveBonus(wYear,wPromoList,wNewEmployees,pEHB.getListToSave(),wMapIds,
	    			  wObjType,this.genericService,bc.getUserName(),wMdaInstId,wPendingMap, getSession(request),wMdaName.getName(), bc);
	    	  //BigDecimal wTotalLeaveBonus = new BigDecimal(((_wNewEmployees.getTotalBasicSalary() + _wOldEmployees.getTotalBasicSalary()) * .1) *.25).setScale(2, RoundingMode.FLOOR);
	    	  
	    	     addSessionAttribute(request, "processLB", wPLB);
	    	    
	    	    Thread t = new Thread(wPLB);
	    	    t.start();

	    	    return "redirect:displayLeaveBonusStatus.do";

	    }

	    return "redirect:leaveBonusfileUploadReport.do?uid=" + pEHB.getUniqueUploadId() + "&s=1";
	  }

	/*  private InnerNamedEntity filterEmployees(
			HashMap<Integer, NamedEntity> pNewEmployees, int pYear,HashMap<Integer,SalaryInfo> pSalMap)
	{
		  InnerNamedEntity wRetMap  = new InnerNamedEntity();
		//We need to filter these employees by certain rules....
		//1. By Suspension...
		//2. By Termination
		//3. By Approval For Payroll...
		   
		   for(Integer i : pNewEmployees.keySet()){
			   
			   NamedEntity n = pNewEmployees.get(i);
			   if(!n.isApprovedForPayroll()){
				   n.setErrorField("Employee not approved for Payroll");
				   
			   }else if(n.getSuspendedInd() == 1){
				   n.setErrorField("Employee is Suspended");
				    
			   }else if(n.getLastLtgPaid() != null){
				   //Now find out if it is this Year...
				   Calendar wCal = new GregorianCalendar();
				   wCal.setTime(n.getLastLtgPaid());
				   if(wCal.get(Calendar.YEAR) == pYear){
					   n.setErrorField("Employee already paid Leave Bonus for "+pYear);
					    
				   }
			   }else{
				 //Now find out if the Employee is Retiring before July of the current year...
				   Calendar wRetDate = new GregorianCalendar();
				   if(n.getDateTerminated() != null){
					   wRetDate.setTime(n.getDateTerminated());
				   }else{
					   if(n.getAllowanceEndDate() != null)
					       wRetDate.setTime(n.getAllowanceEndDate());
					   else //hopes this does not happen often....
						   wRetDate.setTime(PayrollBeanUtils.calculateExpDateOfRetirement(n.getDateOfBirth(), n.getDateOfHire()));
						   
				   }
				   if(wRetDate.get(Calendar.YEAR) == pYear){
					   //Now if the Month is before June...
					   if(wRetDate.get(Calendar.MONTH) < Calendar.JUNE){
						   n.setErrorField("Employee already paid Leave Bonus for "+pYear);
						    
					   }
				   }
			   }
			   if(n.getErrorField() != null){
				   wRetMap.getErrorRecords().add(n);
			   }else{
				   wRetMap.getCleanRecords().add(n);
				   SalaryInfo wSalInfo = pSalMap.get(n.getSalaryInfoInstId());
				   wRetMap.setTotalBasicSalary(wRetMap.getTotalBasicSalary() + wSalInfo.getMonthlyBasicSalary());
			   }
		   }
		return wRetMap;
	}
*/
	private Vector<NamedEntity> setFormDisplayStyle(Vector<NamedEntity> pEmpList)
	  {
	    int i = 1;
	    for (NamedEntity e : pEmpList) {
	      if (i % 2 == 1)
	        e.setDisplayStyle("reportEven");
	      else {
	        e.setDisplayStyle("reportOdd");
	      }
	      i++;
	    }
	    return pEmpList;
	  }
	
	 /* private class InnerNamedEntity{
		  private List<NamedEntity> errorRecords;
		  private List<NamedEntity> cleanRecords;
		  private double totalBasicSalary;
		  
		
		public void setTotalBasicSalary(double totalBasicSalary)
		{
			this.totalBasicSalary = totalBasicSalary;
		}
		public double getTotalBasicSalary()
		{
			return totalBasicSalary;
		}
		
		public List<NamedEntity> getErrorRecords()
		{
			if(this.errorRecords == null)
				errorRecords = new ArrayList<NamedEntity>();
			return errorRecords;
		}
		
		public List<NamedEntity> getCleanRecords()
		{
			if(this.cleanRecords == null)
				cleanRecords = new ArrayList<NamedEntity>();
			return cleanRecords;
		}
	  }*/

}
