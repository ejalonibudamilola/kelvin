package com.osm.gnl.ippms.ogsg.controllers.delete;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRerun;
import com.osm.gnl.ippms.ogsg.payroll.utils.PassPhrase;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;


@Controller
@RequestMapping({"/deleteRerunRecord.do"})
@SessionAttributes(types={PayrollRerun.class} )
public class DeleteSinglePayrollRerunFormController extends BaseController {


	  
	  private final String VIEW_NAME = "payroll/deletePayrollRerunForm";
	 
	  public DeleteSinglePayrollRerunFormController() {}

	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"rid"})
	  public String setupForm(@RequestParam("rid") Long val, Model model, HttpServletRequest request)
	    throws Exception
	  {
		  SessionManagerService.manageSession(request, model);
           BusinessCertificate bc = getBusinessCertificate(request);
		    PayrollRerun epb = this.genericService.loadObjectUsingRestriction(PayrollRerun.class, Arrays.asList(getBusinessClientIdPredicate(request),CustomPredicate.procurePredicate("id",  val)));
		  addRoleBeanToModel(model, request);
		    model.addAttribute("payBean", epb);
		    return VIEW_NAME;
	  }

	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"hid", "s"})
	  public String setupForm(@RequestParam("hid") Long val, @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);

		

	    HiringInfo wHI = loadHiringInfoById(request,getBusinessCertificate(request),val);
	    PayrollRerun epb = new PayrollRerun();
	    epb.setHiringInfo(wHI);
	    addRoleBeanToModel(model, request);
	    model.addAttribute("payBean", epb);
	    model.addAttribute(IConstants.SAVED_MSG, "Payroll Rerun for "+wHI.getAbstractEmployeeEntity().getDisplayName()+" deleted successfully");
	    model.addAttribute("saved", pSaved==1);
	    return VIEW_NAME;

	  }
	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_delete", required=false) String pDelete,
			  @RequestParam(value="_confirm", required=false) String pConfirm,
			  @RequestParam(value="_cancel", required=false) String pCancel,
			  @ModelAttribute("payBean") PayrollRerun ppDMB, 
			  BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
	   
		  SessionManagerService.manageSession(request, model);

			BusinessCertificate bc = this.getBusinessCertificate(request);
	    

	    if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
	    {
	      return "redirect:rerunPayroll.do";
	    }
	     
	    if(isButtonTypeClick(request, REQUEST_PARAM_DELETE)){
	    	
	    	ppDMB.setDeleteWarningIssued(true);
	    	ppDMB.setGeneratedCaptcha(PassPhrase.generateCapcha(6));
	    	 result.rejectValue("", "No.Employees", "Please confirm deletion of Payroll Rerun for "+ppDMB.getHiringInfo().getEmployee().getDisplayName());
	    	 result.rejectValue("", "No.Employees", "For Pay Period "+ PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(ppDMB.getRunMonth(), ppDMB.getRunYear()));
	    	 result.rejectValue("", "No.Employees", "NOTE! Deleting Payroll Rerun record will not allow "+bc.getStaffTypeName()+" to be paid for the current pay period");

	    	 model.addAttribute(DISPLAY_ERRORS, BLOCK);
	         model.addAttribute("pageErrors", result);
	         model.addAttribute("payBean", ppDMB);
	         model.addAttribute("roleBean", bc);
	         return VIEW_NAME;
	    	
	    	
	    }
	    

	    if (isButtonTypeClick(request, REQUEST_PARAM_CONFIRM))
	    {

	    	Long wHireInfoId = ppDMB.getHiringInfo().getId();
	    	this.genericService.deleteObject(ppDMB);
	    	return "redirect:deleteRerunRecord.do?hid="+wHireInfoId+"&s=1";
	       
	      
	    }
	    
	    return Navigator.getInstance(getSessionId(request)).getFromForm();
	  }
	 

}
