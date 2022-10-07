package com.osm.gnl.ippms.ogsg.controllers.subvention;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.subvention.Subvention;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import com.osm.gnl.ippms.ogsg.validators.subvention.SubventionValidator;
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
import java.time.LocalDate;

@Controller
@SessionAttributes(types={Subvention.class})
public class SubventionFormController extends BaseController{

  @Autowired
  private SubventionValidator validator;

   private final String VIEW_CREATE = "subvention/createSubventionForm";
   private final String VIEW_EDIT = "subvention/editSubventionForm";

  @RequestMapping(value = "/createSubvention.do", method = RequestMethod.GET)
  public String setupForm(Model model, HttpServletRequest request)throws HttpSessionRequiredException, EpmAuthenticationException
  {
      SessionManagerService.manageSession(request, model);
		Object userId = getSessionId(request);

    Subvention wS = new Subvention();

    model.addAttribute("subventionBean", wS);
    addRoleBeanToModel(model, request);

    Navigator.getInstance(userId).setFromForm(VIEW_CREATE);
    return VIEW_CREATE;
  }
  @RequestMapping(value = "/editSubvention.do",method={RequestMethod.GET}, params={"sid"})
  public String setupForm(@RequestParam("sid") Long pId, Model model, HttpServletRequest request) throws Exception
  {
    SessionManagerService.manageSession(request, model);
    Object userId = getSessionId(request);
    BusinessCertificate bcert = this.getBusinessCertificate(request);

    Subvention wS = this.genericService.loadObjectById(Subvention.class,pId);
    if ((wS != null) && (!wS.isNewEntity())) {
      wS.setAmountStr(PayrollHRUtils.getDecimalFormat().format(wS.getAmount()));
    }
    wS.setEditMode(true);
    if(bcert.isSuperAdmin())
      wS.setCanEdit(true);

    addRoleBeanToModel(model, request);
    model.addAttribute("subventionBean", wS);

    Navigator.getInstance(userId).setFromForm(VIEW_EDIT);
    return VIEW_EDIT;
  }
  @RequestMapping(value = {"/createSubvention.do","/editSubvention.do"},method={RequestMethod.GET}, params={"sid", "s"})
  public String setupForm(@RequestParam("sid") Long pSid, @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
      SessionManagerService.manageSession(request, model);
		Object userId = getSessionId(request);

    Subvention wS = this.genericService.loadObjectById(Subvention.class, pSid);

    wS.setAmountStr(PayrollHRUtils.getDecimalFormat().format(wS.getAmount()));
    String actionCompleted = wS.getName() + " created successfully.";
    if(pSaved == 2){
      actionCompleted = wS.getName() + " edited successfully.";
    }else if(pSaved == 3){
      actionCompleted = wS.getName()+" Deleted or Expired Successfully";
    }

    addRoleBeanToModel(model, request);
    model.addAttribute("saved", true);
    model.addAttribute(SAVED_MSG, actionCompleted);
    model.addAttribute("subventionBean", wS);
    return Navigator.getInstance(userId).getFromForm();
  }

  @RequestMapping(value = {"/createSubvention.do","/editSubvention.do"},method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_delete", required=false) String delete, @RequestParam(value="_cancel", required=false) String cancel,
                              @ModelAttribute("subventionBean") Subvention pSubventionBean, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
      SessionManagerService.manageSession(request, model);
    Object userId = getSessionId(request);
		BusinessCertificate bc = super.getBusinessCertificate(request);
   

    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
      return REDIRECT_TO_DASHBOARD;
    }

    int sValue = 1;
    if(pSubventionBean.isEditMode())
      sValue = 2;
    if (isButtonTypeClick(request,REQUEST_PARAM_DELETE)) {
      pSubventionBean.setLastModBy(new User(bc.getLoginId()));
      pSubventionBean.setLastModTs(Timestamp.from(Instant.now()));
      pSubventionBean.setExpire(1);
      pSubventionBean.setExpirationDate(LocalDate.now());
      Long id = this.genericService.storeObject(pSubventionBean);
      return "redirect:editSubvention.do?sid=" + id + "&s=3";

    }
    validator.validate(pSubventionBean, result);
    if (result.hasErrors()) {
        addDisplayErrorsToModel(model, request);
        addRoleBeanToModel(model, request);
        model.addAttribute("status", result);
        model.addAttribute("subventionBean", pSubventionBean);
        if(pSubventionBean.isEditMode())
          return VIEW_EDIT;
      return VIEW_CREATE;
    }
    try
    {
      pSubventionBean.setAmount(Double.parseDouble(PayrollHRUtils.removeCommas(pSubventionBean.getAmountStr())));
    } catch (Exception ex) {
      result.rejectValue("amountStr", "Invalid.Value", "Invalid value entered for Amount");
      addDisplayErrorsToModel(model, request);
      addRoleBeanToModel(model, request);
      model.addAttribute("status", result);
      model.addAttribute("subventionBean", pSubventionBean);
      return Navigator.getInstance(userId).getFromForm();
    }
    if(pSubventionBean.isNewEntity())
     pSubventionBean.setCreatedBy(new User(bc.getLoginId()));
    pSubventionBean.setBusinessClientId(bc.getBusinessClientInstId());
    pSubventionBean.setLastModBy(new User(bc.getLoginId()));
    pSubventionBean.setLastModTs(Timestamp.from(Instant.now()));

    pSubventionBean.setExpire(0);
    Long id = this.genericService.storeObject(pSubventionBean);

    if(sValue == 2)
      return "redirect:createSubvention.do?sid=" + id + "&s="+sValue;
    return "redirect:editSubvention.do?sid=" + id + "&s="+sValue;
  }
}