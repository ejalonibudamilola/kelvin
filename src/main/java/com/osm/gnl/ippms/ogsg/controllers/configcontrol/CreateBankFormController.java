package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
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

 
@Controller
@RequestMapping({"/addNewBank.do"})
@SessionAttributes(types={BankInfo.class})
public class CreateBankFormController extends BaseController
{

  @Autowired
  CreateNewBankValidator createNewBankValidator;

  private final String VIEW_NAME = "bank/addNewBankForm";
  

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);
	     
	  BusinessCertificate bc = this.getBusinessCertificate(request);


    BankInfo wEDT = new BankInfo();
    model.addAttribute("roleBean", super.getBusinessCertificate(request));
    model.addAttribute("bankBean", wEDT);
    model.addAttribute("roleBean", bc);
    return VIEW_NAME;
  }

  @RequestMapping(method={RequestMethod.GET},params={"bid"})
  public String setupForm(@RequestParam("bid") Long pBankInstId,Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);
	     
	  BusinessCertificate bc = this.getBusinessCertificate(request);

    BankInfo wEDT = new BankInfo();
    model.addAttribute("roleBean", super.getBusinessCertificate(request));
    model.addAttribute("bankBean", wEDT);
    model.addAttribute("roleBean", bc);
    return VIEW_NAME;
  }

  @RequestMapping(method={RequestMethod.GET},params={"bid","s"})
  public String setupForm(@RequestParam("bid") Long pBankInstId,@RequestParam("s") int pSaved,Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
	     
	  BusinessCertificate bc = this.getBusinessCertificate(request);



    BankInfo wEDT = this.genericService.loadObjectWithSingleCondition(BankInfo.class,
            CustomPredicate.procurePredicate("id", pBankInstId));
    

    String msg = "Bank "+wEDT.getName()+" created successfully.";
    if(wEDT.isMicroFinanceBank())
      msg = "MFB "+wEDT.getName()+" created successfully.";

    wEDT.setMfbInd(wEDT.getMfbInd() + 1); //For Display ONLY....
    model.addAttribute(IConstants.SAVED_MSG,msg );
    model.addAttribute("saved", true);
    model.addAttribute("bankBean", wEDT);
    model.addAttribute("roleBean", bc);
    return VIEW_NAME;
  }
  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("bankBean") BankInfo pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);
	     
	  BusinessCertificate bc = this.getBusinessCertificate(request);

   

    if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
    {
      return CONFIG_HOME_URL;
    }

    createNewBankValidator.validate(pEHB, result);
    if (result.hasErrors())
    {
      model.addAttribute(DISPLAY_ERRORS, BLOCK);
      model.addAttribute("status", result);
      model.addAttribute("bankBean", pEHB);
      model.addAttribute("roleBean", bc);
      return VIEW_NAME;
    }
    pEHB.setCreatedBy(new User(bc.getLoginId()));
    pEHB.setLastModBy(new User(bc.getLoginId()));
    pEHB.setLastModTs(Timestamp.from(Instant.now()));
    pEHB.setMfbInd(pEHB.getMfbInd() - 1);
    this.genericService.saveObject(pEHB);

    return "redirect:addNewBank.do?bid="+pEHB.getId()+"&s=1";
  }
}