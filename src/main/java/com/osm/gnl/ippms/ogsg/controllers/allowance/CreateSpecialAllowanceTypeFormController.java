package com.osm.gnl.ippms.ogsg.controllers.allowance;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.validators.allowance.CreateSpecialAllowanceTypeValidator;
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
import java.util.List;


@Controller
@RequestMapping({"/createSpecAllowType.do"})
@SessionAttributes(types={SpecialAllowanceType.class})
public class CreateSpecialAllowanceTypeFormController extends BaseController
{


  @Autowired
  private CreateSpecialAllowanceTypeValidator createSpecialAllowanceTypeValidator;

  @ModelAttribute("payType")
  public List<PayTypes> getPayType() {
    return genericService.loadAllObjectsWithSingleCondition
            (PayTypes.class, CustomPredicate.procurePredicate("selectableInd", 0), null);
  }

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);
	     
	  
    SpecialAllowanceType wEDT = new SpecialAllowanceType();
    model.addAttribute("roleBean", super.getBusinessCertificate(request));
    model.addAttribute("specAllowTypeBean", wEDT);
    return "allowance/createSpecialAllowTypeForm";
  }

  @RequestMapping(method={RequestMethod.GET},params={"stid","s"})
  public String setupForm(@RequestParam("stid") Long pSpecTypeId,@RequestParam("s") int pSaved,Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
	     
	  

//    SpecialAllowanceType wEDT = new SpecialAllowanceType();
    
    SpecialAllowanceType wEDT = this.genericService.loadObjectById(SpecialAllowanceType.class, pSpecTypeId);
    wEDT.setPayTypeId(wEDT.getPayTypes().getId());
    String actionCompleted = "Special Allowance Type "+wEDT.getName()+" Created Successfully.";
    model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
    model.addAttribute("saved", true);
    model.addAttribute("specAllowTypeBean", wEDT);
    model.addAttribute("roleBean", super.getBusinessCertificate(request));
    return "allowance/createSpecialAllowTypeForm";
  }
  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("specAllowTypeBean") SpecialAllowanceType pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);
	     
	  BusinessCertificate bc = this.getBusinessCertificate(request);

    
    if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
    {
      return CONFIG_HOME_URL;
    }


    createSpecialAllowanceTypeValidator.validate(pEHB, result, bc);
    if (result.hasErrors())
    {
      model.addAttribute("status", result);
      model.addAttribute("specAllowTypeBean", pEHB);
      model.addAttribute(DISPLAY_ERRORS, BLOCK);
      model.addAttribute("roleBean", super.getBusinessCertificate(request));
      return "allowance/createSpecialAllowTypeForm";
    }
    pEHB.setCreatedBy(new User(bc.getLoginId()));
    pEHB.setBusinessClientId(bc.getBusinessClientInstId());
    pEHB.setPayTypes(new PayTypes(pEHB.getPayTypeId()));
    pEHB.setLastModBy(new User(bc.getLoginId()));
    pEHB.setLastModTs(Timestamp.from(Instant.now()));
    
    this.genericService.saveObject(pEHB);

    return "redirect:createSpecAllowType.do?stid="+pEHB.getId()+"&s=1";
  }
}