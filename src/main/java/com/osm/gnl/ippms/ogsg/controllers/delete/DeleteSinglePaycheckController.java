package com.osm.gnl.ippms.ogsg.controllers.delete;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRerun;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.negativepay.domain.NegativePayBean;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
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
import java.time.LocalDate;
import java.util.Arrays;



@Controller
@RequestMapping({"/deletePaycheck.do"})
@SessionAttributes({"payBean"})
public class DeleteSinglePaycheckController extends BaseController
{

    private final String VIEW = "payment/deletePaycheckForm";

  public DeleteSinglePaycheckController() { }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"pid"})
  public String setupForm(@RequestParam("pid") Long val, Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = getBusinessCertificate(request);
		 
	    AbstractPaycheckEntity epb = (AbstractPaycheckEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc), Arrays.asList(getBusinessClientIdPredicate(request),CustomPredicate.procurePredicate("id",val))  );
	    epb.setHiringInfo( this.genericService.loadObjectWithSingleCondition(HiringInfo.class, CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), epb.getParentObject().getId())));
		  NegativePayBean wNPB = this.genericService.loadObjectUsingRestriction(NegativePayBean.class,Arrays.asList(getBusinessClientIdPredicate(request),
          CustomPredicate.procurePredicate("paycheckId",epb.getId()), CustomPredicate.procurePredicate("runMonth",epb.getRunMonth()), CustomPredicate.procurePredicate("runYear",epb.getRunYear())));
          if(!wNPB.isNewEntity()){
        	  epb.setDeductionAmount(wNPB.getReductionAmount() + epb.getNetPay());
          }
          addRoleBeanToModel(model, request);
	    model.addAttribute("payBean", epb);
	    return VIEW;
  }


  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"pid", "eid","s"})
  public String setupForm(@RequestParam("pid") Long val, @RequestParam("eid") int eid,
		  @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

      BusinessCertificate bc = getBusinessCertificate(request);

    EmployeePayBean epb = new EmployeePayBean();
    epb.setHiringInfo(loadHiringInfoById(request,bc,val));
    epb.setAbstractEmployeeEntity(epb.getHiringInfo().getAbstractEmployeeEntity());
    String actionCompleted = "Paycheck for "+epb.getAbstractEmployeeEntity().getDisplayName()+" Deleted Successfully.";
    if(eid == 1){
    	actionCompleted = "Paycheck for "+epb.getAbstractEmployeeEntity().getDisplayName()+" Deleted and Rescheduled for Re-Run Successfully.";
    } 
    model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
    model.addAttribute("saved", pSaved==1);
      addRoleBeanToModel(model, request);
    model.addAttribute("payBean", epb);
    return VIEW;

  }
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_delete", required=false) String pDelete,
		  @RequestParam(value="_confirm", required=false) String pConfirm,
		  @RequestParam(value="_cancel", required=false) String pCancel,
		  @ModelAttribute("payBean") AbstractPaycheckEntity ppDMB,
		  BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
   
	  SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);
     
    Object userId = getSessionId(request);
    if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
    {
    	String wRetForm = Navigator.getInstance(userId).getFromForm();
    	if(wRetForm == null)
    		return "redirect:viewPayslipHistory.do?eid="+ppDMB.getParentObject().getId();
    	return wRetForm;
    }
     
    if(isButtonTypeClick(request, REQUEST_PARAM_DELETE)){
    	
    	ppDMB.setWarningIssued(true);
    	ppDMB.setGeneratedCaptcha(PassPhrase.generateCapcha(6));
    	 result.rejectValue("", "No.Employees", "Please confirm deletion of paycheck for "+ppDMB.getHiringInfo().getAbstractEmployeeEntity().getDisplayName());
    	 result.rejectValue("", "No.Employees", "For Pay Period "+ PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(ppDMB.getRunMonth(), ppDMB.getRunYear()));

    	 addDisplayErrorsToModel(model, request);
         model.addAttribute("pageErrors", result);
         model.addAttribute("payBean", ppDMB);
         model.addAttribute("roleBean", bc);
         return VIEW;
    	
    	
    }
     
    int eid = ppDMB.getReRunInd();
    Long wHid = ppDMB.getHiringInfo().getId();
    if (isButtonTypeClick(request, REQUEST_PARAM_CONFIRM))
    {
    	//First check that a rerun is set or not....
    	
    	switch(ppDMB.getReRunInd()){
    	case 0:
    		result.rejectValue("", "No.Employees", "Please indicate whether to Schedule for Rerun or not ");

            addDisplayErrorsToModel(model, request);
            model.addAttribute("pageErrors", result);
            model.addAttribute("payBean", ppDMB);
            model.addAttribute("roleBean", bc);
            return VIEW;
    	 
    	case 1:
    		//If it is Rerun...Make sure the captcha is Entered
    		if(!ppDMB.getGeneratedCaptcha().equalsIgnoreCase(ppDMB.getEnteredCaptcha())){
    			ppDMB.setCaptchaError(true);
    	            model.addAttribute("pageErrors", result);
    	            model.addAttribute("payBean", ppDMB);
    	            model.addAttribute("roleBean", bc);
    	            return VIEW;
    		}else{
    			//before Schedule for a re-run. Check if this Hiring Info exists...
    			
    			PayrollRerun wPR =   this.genericService.loadObjectWithSingleCondition(PayrollRerun.class, CustomPredicate.procurePredicate("hiringInfo.id", ppDMB.getHiringInfo().getId()));
    			if(wPR.isNewEntity()){
	    			wPR.setHiringInfo(new HiringInfo(ppDMB.getHiringInfo().getId()));
	    			wPR.setBusinessClientId(bc.getBusinessClientInstId());
	    			wPR.setRunMonth(ppDMB.getRunMonth());
	    			wPR.setRunYear(ppDMB.getRunYear());
	    			wPR.setLastModBy(bc.getLoginId());
	    			wPR.setLastModTs(LocalDate.now());
	    			wPR.setAuditTime(PayrollBeanUtils.getCurrentTime(false));
	    			 this.determinePayPeriod(ppDMB,bc);
	    			
	    			this.genericService.saveObject(wPR);
    			}
    			this.genericService.deleteObject(ppDMB);
    		}
    		break;
    	case 2:
    		//Check if Permanent Deletion Warning has been issued.
    		if(!ppDMB.isNoPaymentWarningIssued()){
    			ppDMB.setNoPaymentWarningIssued(true);
            	//ppDMB.setGeneratedCaptcha(PassPhrase.generateCapcha(6));
            	 result.rejectValue("", "No.Employees", "Please confirm deletion of paycheck for "+ppDMB.getHiringInfo().getAbstractEmployeeEntity().getDisplayName());
            	 result.rejectValue("", "No.Employees", "For Pay Period "+PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(ppDMB.getRunMonth(), ppDMB.getRunYear()));

            	 addDisplayErrorsToModel(model, request);
                 model.addAttribute("pageErrors", result);
                 model.addAttribute("payBean", ppDMB);
                 model.addAttribute("roleBean", bc);
                 return VIEW;

    		}
    		this.determinePayPeriod(ppDMB,bc);
    		this.genericService.deleteObject(ppDMB);
    		break;
    	}
       
      
    }
    
    return "redirect:deletePaycheck.do?pid="+wHid+"&eid="+eid+"&s=1";
  }
  private void determinePayPeriod(AbstractPaycheckEntity pE, BusinessCertificate businessCertificate) throws InstantiationException, IllegalAccessException
  {


    String payPeriod = PayrollBeanUtils.getDateAsString(pE.getPayPeriodStart()) + " - " + PayrollBeanUtils.getDateAsString(pE.getPayPeriodEnd());
    HiringInfo h = pE.getHiringInfo();
    if ((h == null) || (h.isNewEntity())) {
      h =   this.genericService.loadObjectWithSingleCondition(HiringInfo.class,CustomPredicate.procurePredicate(businessCertificate.getEmployeeIdJoinStr(),pE.getAbstractEmployeeEntity().getId()));
    }
    if ((h.getCurrentPayPeriod() != null) && (h.getCurrentPayPeriod().equalsIgnoreCase(payPeriod))) {
      if (h.getLastPayPeriod() != null) {
        h.setCurrentPayPeriod(h.getLastPayPeriod());
        if (h.getLastPayPeriodHolder() != null) {
          h.setLastPayPeriod(h.getLastPayPeriodHolder());
          h.setLastPayPeriodHolder(null);
        } else {
          h.setLastPayPeriod(null);
        }
      } else {
        h.setCurrentPayPeriod(null);
      }
    } else if ((h.getLastPayPeriod() != null) && (h.getLastPayPeriod().equalsIgnoreCase(payPeriod)))
    {
      h.setLastPayPeriod(h.getLastPayPeriodHolder());
      h.setLastPayPeriodHolder(null);
    }
    this.genericService.storeObject(h);
  }
}