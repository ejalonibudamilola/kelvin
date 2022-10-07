package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.validators.configcontrol.CreateNewBankValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;

@Controller
@RequestMapping({"/editBank.do"})
@SessionAttributes(types={BankInfo.class})
public class EditBankFormController extends BaseController
{
  private static final String VIEW_NAME = "bank/editBankInfoForm";

  @Autowired
  CreateNewBankValidator createNewBankValidator;


  @RequestMapping(method={RequestMethod.GET}, params={"bid"})
  public String setupForm(@RequestParam("bid") Long pDtid, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
	     
	  

    BankInfo wP = this.genericService.loadObjectWithSingleCondition(BankInfo.class, CustomPredicate.procurePredicate("id", pDtid));

    if (wP.isNewEntity()) {
      return "redirect:addNewBank.do";
    }
    wP.setEditMode(true);
    wP.setMfbInd(wP.getMfbInd() + 1);
    addRoleBeanToModel(model,request);
    model.addAttribute("bankBean", wP);
    return VIEW_NAME;
  }

  @RequestMapping(method={RequestMethod.GET}, params={"bid","s"})
  public String setupForm(@RequestParam("bid") Long pDtid,
		  @RequestParam("s") int pSaved,Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
	     
    BusinessCertificate bc = this.getBusinessCertificate(request);

    BankInfo wP = this.genericService.loadObjectWithSingleCondition(BankInfo.class, CustomPredicate.procurePredicate("id", pDtid));

    if (wP.isNewEntity()) {
      return "redirect:addNewBank.do";
    }
    wP.setMfbInd(wP.getMfbInd() + 1);
    wP.setEditMode(true);
    model.addAttribute(IConstants.SAVED_MSG, "Bank "+wP.getName()+" updated successfully.");
    model.addAttribute("saved", true);
    model.addAttribute("bankBean", wP);
    model.addAttribute("roleBean", bc);
    return VIEW_NAME;
  }

  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("bankBean") BankInfo pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);
	     
	  BusinessCertificate bc = this.getBusinessCertificate(request);

    
    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL))
    {
      return "redirect:viewAllBanks.do";
    }

    createNewBankValidator.validate(pEHB, result);
    if (result.hasErrors())
    {
     // ((BankInfo)result.getTarget()).setDisplayErrors("block");
    	 model.addAttribute(DISPLAY_ERRORS, BLOCK);
      model.addAttribute("status", result);
      model.addAttribute("bankBean", pEHB);
      model.addAttribute("roleBean", bc);
      return "editBankInfoForm";
    }

    pEHB.setLastModBy(new User(bc.getLoginId()));
    pEHB.setLastModTs(Timestamp.from(Instant.now()));
    if(pEHB.getMfbInd() > 0)
    	pEHB.setMfbInd(pEHB.getMfbInd() - 1);
    this.genericService.saveObject(pEHB);

    return "redirect:editBank.do?bid="+pEHB.getId()+"&s=1";
  }
}