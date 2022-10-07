package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PaymentMethodInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping({"/mergeBankBranch.do"})
@SessionAttributes(types={BankBranch.class})
public class MergeBankBranchFormController extends BaseController {

	  @Autowired
	  PayrollService payrollService;

	  private static final String VIEW_NAME = "bank/mergeBankBranchForm";

	  @ModelAttribute("banksList")
	  public List<BankInfo> generateBanksList(){
		  
		List<BankInfo> wBankInfoList =  this.genericService.loadControlEntity(BankInfo.class);
		  return wBankInfoList;
	  }


	  @RequestMapping(method={RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);

		  BusinessCertificate bc = this.getBusinessCertificate(request);

	    BankBranch wP = new BankBranch();

	    model.addAttribute("roleBean", bc);
	    model.addAttribute("bankBranchBean", wP);
	    return VIEW_NAME;
	  }

	  @RequestMapping(method={RequestMethod.GET}, params={"obid","mbid","s"})
	  public String setupForm(@RequestParam("obid") Long pBankBranchId,
			  @RequestParam("mbid") Long pMergedBankBranch,
			  @RequestParam("s") int pSaved,Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);
		     
		  BusinessCertificate bc = this.getBusinessCertificate(request);

	    BankBranch wP = new BankBranch();
	    
 	    BankBranch wBB = this.genericService.loadObjectWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate("id", pBankBranchId));

	    String actionCompleted = wBB.getName()+" branch of "+wBB.getBankInfo().getName()+" merged to "+wBB.getBankInfo().getName()+" successfully.";
	    model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
	    model.addAttribute("saved", true);
	    model.addAttribute("roleBean", bc);
	    model.addAttribute("bankBranchBean", wP);
	    return VIEW_NAME;
	  }
	 

	  @RequestMapping(method={RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
			  @RequestParam(value="_merge", required=false) String merge,
			  @RequestParam(value="_confirm", required=false) String confirm,
			  @ModelAttribute("bankBranchBean") BankBranch pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
	  {
		  SessionManagerService.manageSession(request, model);
		     
		  BusinessCertificate bc = this.getBusinessCertificate(request);

	   
	    if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
	    {
	    	return CONFIG_HOME_URL;
	    }
	    
	     

	    if (isButtonTypeClick(request, REQUEST_PARAM_MERGE))
	    {
	    	
	    	//First find the information to display...
	    	if(IppmsUtils.isNullOrLessThanOne(pEHB.getFromBranchInstId())){
	    		result.rejectValue("", "DEF.EDIT.VOID", "Please select a value for 'From Bank Branch'.");
	    		model.addAttribute(DISPLAY_ERRORS, BLOCK);
	    		model.addAttribute("status", result);
				model.addAttribute("roleBean", bc);
				model.addAttribute("bankBranchBean", pEHB);
	    	      return VIEW_NAME;
	    	}
	    	if(IppmsUtils.isNullOrLessThanOne(pEHB.getToBranchInstId())){
	    		result.rejectValue("", "DEF.EDIT.VOID", "Please select a value for 'To Bank Branch'.");
	    		model.addAttribute(DISPLAY_ERRORS, BLOCK);
	    		model.addAttribute("status", result);
				model.addAttribute("roleBean", bc);
				model.addAttribute("bankBranchBean", pEHB);
	    	      return VIEW_NAME;
	    	}
	    	//-- If we get here...get the information needed.
	    	BankBranch wBB = this.genericService.loadObjectWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate("id", pEHB.getFromBranchInstId()));
	    	if(wBB.isNewEntity()){
	    		//Should never happen....but humans!
	    		result.rejectValue("", "DEF.EDIT.VOID", "Unknown  'From Bank Branch'.");
	    		model.addAttribute(DISPLAY_ERRORS, BLOCK);
	    		model.addAttribute("status", result);
				model.addAttribute("roleBean", bc);
				model.addAttribute("bankBranchBean", pEHB);
	    	      return VIEW_NAME;
	    	} 
	    	BankBranch _wBB = this.genericService.loadObjectWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate("id", pEHB.getToBranchInstId()));
	    	if(_wBB.isNewEntity()){
	    		//Should never happen....but humans!
	    		result.rejectValue("", "DEF.EDIT.VOID", "Unknown  'To Bank Branch'.");
	    		model.addAttribute(DISPLAY_ERRORS, BLOCK);
	    		model.addAttribute("status", result);
				model.addAttribute("roleBean", bc);
				model.addAttribute("bankBranchBean", pEHB);
	    	      return VIEW_NAME;
	    	}
	    	if(_wBB.getId().equals(4708L) || wBB.getId().equals(4708L)){
	    		result.rejectValue("", "DEF.EDIT.VOID", "Default Bank Branch CAN NOT BE MERGED.");
	    		model.addAttribute(DISPLAY_ERRORS, BLOCK);
	    		model.addAttribute("status", result);
	    		model.addAttribute("roleBean", bc);
	    		model.addAttribute("bankBranchList", this.genericService.loadAllObjectsWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate(
	    	      		"bankInfo.id", pEHB.getBankId()), "name"));
	    		model.addAttribute("bankBranchBean", pEHB);
	    	      return VIEW_NAME;
	    	}
	    	//--Now make sure the 2 arent the same...
	    	if(_wBB.getId().equals(wBB.getId())){
	    		result.rejectValue("", "DEF.EDIT.VOID", "From and To Bank Branches are the same branch!. Merging not allowed.");
	    		model.addAttribute(DISPLAY_ERRORS, BLOCK);
	    		model.addAttribute("status", result);
	    		model.addAttribute("roleBean", bc);
	    		model.addAttribute("bankBranchList", this.genericService.loadAllObjectsWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate(
	    	      		"bankInfo.id", pEHB.getBankId()), "name"));
	    		model.addAttribute("bankBranchBean", pEHB);
	    	      return VIEW_NAME;
	    	}

			PredicateBuilder predicateBuilder =  new PredicateBuilder();
	    	predicateBuilder.addPredicate(CustomPredicate.procurePredicate("bankBranches.id", wBB.getId()));

	    	//-- Now get the no of Employees in this Branch...
	    	int wNoOfFromBranchAccounts = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, PaymentMethodInfo.class);
	    	 model.addAttribute("confirmation", true); 
	    	 model.addAttribute("displayPayrollMsg",wNoOfFromBranchAccounts+" Active "+bc.getStaffTypeName()+" accounts will be moved from <br> "+wBB.getName()
	    			+"<br> to "+_wBB.getName());
	    	result.rejectValue("", "DEF.EDIT.VOID", "Please confirm the moving of ALL Active Accounts from "+wBB.getName());
	    	result.rejectValue("", "DEF.EDIT.VOID", "to  "+_wBB.getName());

			model.addAttribute("roleBean", bc);
			model.addAttribute("status", result);
			model.addAttribute("bankBranchList", this.genericService.loadAllObjectsWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate("bankInfo.id", pEHB.getBankId()), "name"));
			model.addAttribute("bankBranchBean", pEHB);
			return VIEW_NAME;
	    	
	    	//return "redirect:editBankBranch.do?bbn="+branchName+"&s=2&bn="+bankName;
	    }

	    this.payrollService.updatePaymentMethodInfoUsingHql(pEHB.getFromBranchInstId(), pEHB.getToBranchInstId(), bc.getLoginId());
        

	    return "redirect:mergeBankBranch.do?obid="+pEHB.getFromBranchInstId()+"&mbid="+pEHB.getToBranchInstId()+"&s=1";
	  }


}
