package com.osm.gnl.ippms.ogsg.controllers.contributions;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfcInfo;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PassPhrase;
import com.osm.gnl.ippms.ogsg.validators.contributions.CreatePensionValidator;
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
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping({"/createEditPfa.do"})
@SessionAttributes(types={PfaInfo.class})
public class CreateEditPFAFormController extends BaseController{
  @Autowired
  private CreatePensionValidator validator;

  @ModelAttribute("pfcInfoList")
  protected List<?> getPfcList(HttpServletRequest request)
  {
    return this.genericService.loadControlEntity(PfcInfo.class);

  }

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);
	     
	  BusinessCertificate bc = this.getBusinessCertificate(request);
    
    PfaInfo wPfaInfo = new PfaInfo();
    PfcInfo wPfcInfo = new PfcInfo();
    wPfaInfo.setPfcInfo(wPfcInfo);
    
    model.addAttribute("displayTitle", "Create PFA");
    model.addAttribute("pfaBean", wPfaInfo);
    model.addAttribute("roleBean", bc);
    return "createEditPfaForm";
  }

  @RequestMapping(method={RequestMethod.GET}, params={"oid"})
  public String setupForm(@RequestParam("oid") Long pOid, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
	     
	  BusinessCertificate bc = this.getBusinessCertificate(request);

    PfaInfo wPfaInfo = this.genericService.loadObjectById(PfaInfo.class, pOid);

    wPfaInfo.setOldName(wPfaInfo.getName());
    wPfaInfo.setOldAddress(wPfaInfo.getAddress());
    

    model.addAttribute("displayTitle","Edit PFA - " + wPfaInfo.getName());

    wPfaInfo.setEditMode(true);

    model.addAttribute("pfaBean", wPfaInfo);
    model.addAttribute("roleBean", bc);
    return "createEditPfaForm";
  }
  @RequestMapping(method={RequestMethod.GET}, params={"s", "oid"})
  public String setupForm(@RequestParam("s") int pSave, @RequestParam("oid") Long pObjectId, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
	     
	  BusinessCertificate bc = this.getBusinessCertificate(request);
    
     PfaInfo wPfcInfo = this.genericService.loadObjectById(PfaInfo.class, pObjectId);


    model.addAttribute("saved", Boolean.valueOf(true));
    String wMessage = null;
    String titlePage = null;
    if (pSave == 1) {
      wMessage = "PFA - " + wPfcInfo.getName() + " Saved Successfully";
      titlePage = "Create PFA";
    }
    else {
      wMessage = "PFA - " + wPfcInfo.getName() + " Updated Successfully";
      titlePage = "Edit PFA - " + wPfcInfo.getName();
    }
    model.addAttribute(IConstants.SAVED_MSG, wMessage);

    model.addAttribute("displayTitle", titlePage);
    model.addAttribute("pfaBean", wPfcInfo);
    model.addAttribute("roleBean", bc);
    return "createEditPfaForm";
  }

  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @RequestParam(value="_ok", required=false) String ok,
		  @RequestParam(value="_confirm", required=false) String confirm, 
		  @ModelAttribute("pfaBean") PfaInfo pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);
	     
	  BusinessCertificate bc = this.getBusinessCertificate(request);
    
     

    if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
    {
      return CONFIG_HOME_URL;
    }

    validator.validateForPFA(pEHB, result);
    if (result.hasErrors())
    {
      model.addAttribute(DISPLAY_ERRORS, BLOCK);
      model.addAttribute("status", result);
      model.addAttribute("pfcBean", pEHB);
      addRoleBeanToModel(model, request);
      if(pEHB.isEditMode())
        model.addAttribute("displayTitle", "Edit PFA - " + pEHB.getName());
      else
        model.addAttribute("displayTitle", "Create PFA");
      return "createEditPfaForm";
    }
    
    if (isButtonTypeClick(request, REQUEST_PARAM_OK)) {
      boolean valueChanged = false;
      if (pEHB.isEditMode())
      {
        if ((pEHB.getName().equalsIgnoreCase(pEHB.getOldName())) || (pEHB.getAddress().equalsIgnoreCase(pEHB.getOldAddress()))) {
          valueChanged = !valueChanged;
        }
      }

      if ((valueChanged) || (!pEHB.isEditMode())) {
        model.addAttribute("confirmation", true);
        result.rejectValue("", "Warning.Value", "Please Enter the Generated Code and click 'Confim' Button to confirm.");
      } else {
        return CONFIG_HOME_URL;
      }

      pEHB.setGeneratedCaptcha(PassPhrase.generateCapcha(7));
      model.addAttribute(DISPLAY_ERRORS, BLOCK);
      model.addAttribute("status", result);
      model.addAttribute("pfaBean", pEHB);
      addRoleBeanToModel(model, request);
      if(pEHB.isEditMode())
        model.addAttribute("displayTitle", "Edit PFA - " + pEHB.getName());
      else
        model.addAttribute("displayTitle", "Create PFA");
      return "createEditPfaForm";
    }

    if (!pEHB.getGeneratedCaptcha().equalsIgnoreCase(pEHB.getEnteredCaptcha())) {
      pEHB.setGeneratedCaptcha(PassPhrase.generateCapcha(7));
      result.rejectValue("", "Warning.Value", "Entered Code does not match Generated Code. Please retry.");
      model.addAttribute(DISPLAY_ERRORS, BLOCK);
      model.addAttribute("status", result);
      model.addAttribute("pfaBean", pEHB);
      addRoleBeanToModel(model, request);
      if(pEHB.isEditMode())
        model.addAttribute("displayTitle", "Edit PFA - " + pEHB.getName());
      else
        model.addAttribute("displayTitle", "Create PFA");
      return "createEditPfaForm";
    }
    pEHB.setLastModBy(new User(bc.getLoginId()));
    pEHB.setCreatedBy(new User(bc.getLoginId()));
    pEHB.setLastModTs(Timestamp.from(Instant.now()));

    this.genericService.saveObject(pEHB);

    if (pEHB.isEditMode()) {
      return "redirect:createEditPfa.do?s=2&oid=" + pEHB.getId();
    }
    return "redirect:createEditPfa.do?s=1&oid=" + pEHB.getId();
  }
}