package com.osm.gnl.ippms.ogsg.controllers.contributions;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;


@Controller
@RequestMapping({"/createEditPfc.do"})
@SessionAttributes(types={PfcInfo.class})
public class CreateEditPFCFormController extends BaseController
{
  @Autowired
  private CreatePensionValidator validator;


  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);

    PfcInfo wPfcInfo = new PfcInfo();


    model.addAttribute("displayTitle", "Create New PFC");
    model.addAttribute("pfcBean", wPfcInfo);
    addRoleBeanToModel(model, request);

    return "createEditPfcForm";
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"oid"})
  public String setupForm(@RequestParam("oid") Long pOid, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

    PfcInfo wPfcInfo = this.genericService.loadObjectWithSingleCondition(PfcInfo.class,
            CustomPredicate.procurePredicate("id",pOid));
    wPfcInfo.setOldName(wPfcInfo.getName());
    wPfcInfo.setOldAddress(wPfcInfo.getAddress());


    model.addAttribute("displayTitle", "Edit PFC - " + wPfcInfo.getName());
    wPfcInfo.setEditMode(true);

    model.addAttribute("pfcBean", wPfcInfo);
    addRoleBeanToModel(model, request);
    return "createEditPfcForm";
  }
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"s", "oid"})
  public String setupForm(@RequestParam("s") int pSave, @RequestParam("oid") Long pObjectId, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

  PfcInfo wPfcInfo = this.genericService.loadObjectWithSingleCondition(PfcInfo.class,
            CustomPredicate.procurePredicate("id",pObjectId));
    model.addAttribute("saved", Boolean.valueOf(true));
    String wMessage = null;
    String titlePage = null;
    if (pSave == 1) {
      wMessage = "PFC - " + wPfcInfo.getName() + " Saved Successfully";
      titlePage = "Create PFC";
    }
    else {
      wMessage = "PFC - " + wPfcInfo.getName() + " Updated Successfully";
      titlePage = "Edit PFC - " + wPfcInfo.getName();
    }
    model.addAttribute(IConstants.SAVED_MSG, wMessage);

    model.addAttribute("pfcBean", wPfcInfo);
    addRoleBeanToModel(model, request);
    model.addAttribute("displayTitle", titlePage);
    return "createEditPfcForm";
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @RequestParam(value="_ok", required=false) String ok, @RequestParam(value="_confirm", required=false) String confirm, @ModelAttribute("pfcBean") PfcInfo pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);



    if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
    {
      return CONFIG_HOME_URL;
    }

    validator.validate(pEHB, result);
    if (result.hasErrors())
    {
      model.addAttribute(DISPLAY_ERRORS, BLOCK);
      model.addAttribute("status", result);
      model.addAttribute("pfcBean", pEHB);
      addRoleBeanToModel(model, request);
      if(pEHB.isEditMode())
        model.addAttribute("displayTitle", "Edit PFC - " + pEHB.getName());
      else
        model.addAttribute("displayTitle", "Create PFC");
      return "createEditPfcForm";
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
      model.addAttribute("pfcBean", pEHB);
      addRoleBeanToModel(model, request);
      if(pEHB.isEditMode())
        model.addAttribute("displayTitle", "Edit PFC - " + pEHB.getName());
      else
        model.addAttribute("displayTitle", "Create PFC");
      return "createEditPfcForm";
    }
    if (!pEHB.getGeneratedCaptcha().equalsIgnoreCase(pEHB.getEnteredCaptcha())) {
      pEHB.setGeneratedCaptcha(PassPhrase.generateCapcha(7));
      result.rejectValue("", "Warning.Value", "Entered Code does not match Generated Code. Please retry.");
      model.addAttribute(DISPLAY_ERRORS, BLOCK);
      model.addAttribute("status", result);
      model.addAttribute("pfaBean", pEHB);
      addRoleBeanToModel(model, request);
      if(pEHB.isEditMode())
        model.addAttribute("displayTitle", "Edit PFC - " + pEHB.getName());
      else
        model.addAttribute("displayTitle", "Create PFC");
      return "createEditPfcForm";
    }

    pEHB.setLastModBy(new User(bc.getLoginId()));
    pEHB.setLastModTs(Timestamp.from(Instant.now()));
    if (!pEHB.isEditMode())
    {
      pEHB.setCreatedBy(new User(bc.getLoginId()));
      pEHB.setPfcCode(this.genericService.loadMaxValueByClassAndIntColName(PfcInfo.class, "pfcCode") + 1);
    }
    this.genericService.saveObject(pEHB);

    if (pEHB.isEditMode()) {
      return "redirect:createEditPfc.do?s=2&oid=" + pEHB.getId();
    }
    return "redirect:createEditPfc.do?s=1&oid=" + pEHB.getId();
  }
}