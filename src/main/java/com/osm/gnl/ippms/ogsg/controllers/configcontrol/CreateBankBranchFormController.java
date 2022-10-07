package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.validators.configcontrol.CreateNewBankValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping({"/addNewBankBranch.do"})
@SessionAttributes(types={BankBranch.class})
public class CreateBankBranchFormController extends BaseController {

	  
	  @Autowired
	  CreateNewBankValidator createNewBankValidator;

	  private static final String VIEW_NAME = "bank/addNewBankBranchForm";


	  @ModelAttribute("banksList")
	  public List<BankInfo> generateBanksList(){
		  
		List<BankInfo> wBankInfoList = this.genericService.loadAllObjectsWithoutRestrictions(BankInfo.class, "name");
		 Collections.sort(wBankInfoList);
		  return wBankInfoList;
	  }
	  
	  @RequestMapping(method={RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
		  SessionManagerService.manageSession(request, model);
		     
		  BusinessCertificate bc = this.getBusinessCertificate(request);


	    BankBranch wEDT = new BankBranch();

	    model.addAttribute("bankBranchBean", wEDT);
	    model.addAttribute("roleBean", bc);
	    return VIEW_NAME;
	  }

	 /* @RequestMapping(method={RequestMethod.GET},params={"bid"})
	  public String setupForm(@RequestParam("bid") Long pBankInstId,Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
		  SessionManagerService.manageSession(request, model);
		     
		  BusinessCertificate bc = this.getBusinessCertificate(request);


	    BankBranch wEDT = new BankBranch();

	    model.addAttribute("bankBranchBean", wEDT);
	    model.addAttribute("roleBean", bc);
	    return VIEW_NAME;
	  }
*/
	  @RequestMapping(method={RequestMethod.GET},params={"bbid","s"})
	  public String setupForm(@RequestParam("bbid") Long pBankBranchInstId,@RequestParam("s") int pSaved,Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);
		     
		  BusinessCertificate bc = this.getBusinessCertificate(request);

	    BankBranch wEDT = this.genericService.loadObjectWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate("id", pBankBranchInstId));

	    model.addAttribute(IConstants.SAVED_MSG, ""+wEDT.getName()+" branch of "+wEDT.getBankInfo().getName()+" created successfully.");
	    model.addAttribute("saved", true);
	    model.addAttribute("bankBranchBean", wEDT);
	    model.addAttribute("roleBean", bc);
	    return VIEW_NAME;
	  }
	  @RequestMapping(method={RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
			  @ModelAttribute("bankBranchBean") BankBranch pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
	  {
		  SessionManagerService.manageSession(request, model);
		     
		  BusinessCertificate bc = this.getBusinessCertificate(request);

	    
	    if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
	    {
	      return CONFIG_HOME_URL;
	    }

	    createNewBankValidator.validateBankBranch(pEHB, result);
	    if (result.hasErrors())
	    {
	      model.addAttribute(DISPLAY_ERRORS, BLOCK);
	      model.addAttribute("status", result);
	      model.addAttribute("bankBranchBean", pEHB);
	      model.addAttribute("roleBean", bc);
	      return VIEW_NAME;
	    }
	    pEHB.setCreatedBy(new User(bc.getLoginId()));
	    pEHB.setLastModBy(new User(bc.getLoginId()));
	    pEHB.setLastModTs(Timestamp.from(Instant.now()));
	    genericService.saveObject(pEHB);

	    return "redirect:addNewBankBranch.do?bbid="+pEHB.getId()+"&s=1";
	  }

}
