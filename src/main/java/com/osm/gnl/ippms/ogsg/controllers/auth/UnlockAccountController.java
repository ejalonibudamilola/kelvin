package com.osm.gnl.ippms.ogsg.controllers.auth;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.audit.domain.LoginAudit;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

 
@Controller
@RequestMapping({"/unlockUserAccount.do"})
@SessionAttributes(types={User.class})
public class UnlockAccountController extends BaseController
{
  
  @Autowired
  private PasswordEncoder passwordEncoder;
  
    private final String VIEW = "user/unLockUserAccountForm";
  

  public UnlockAccountController()
  {}

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"lid"})
  public String setupForm(@RequestParam("lid") Long pLid, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);

      User user = genericService.loadObjectById(User.class,pLid);

    model.addAttribute("epmUser", user);
    model.addAttribute("roleBean", bc);
    return VIEW;
  }
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"lid","s"})
  public String setupForm(@RequestParam("lid") Long pLid,@RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);

      User user = genericService.loadObjectById(User.class,pLid);

      String actionCompleted = "Account for "+user.getActualUserName()+" Unlocked Successfully.";
      model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
      model.addAttribute("saved", true);
      model.addAttribute("epmUser", user);
      model.addAttribute("roleBean", bc);
    return VIEW;
  }
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("epmUser") User pLogin, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);

     

    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL))
    {
      return REDIRECT_TO_DASHBOARD;
    }

    User wLogin = genericService.loadObjectWithSingleCondition(User.class, CustomPredicate.procurePredicate("username", bc.getUserName()));

    if ((wLogin.getRole().getName().equalsIgnoreCase("SUPER_ADMIN")) &&
            (!bc.isSuperAdmin())) {
      result.rejectValue("id", "Invalid.Value", "Only a SUPER ADMIN can Unlock a SUPER ADMIN account");
      model.addAttribute(DISPLAY_ERRORS, BLOCK);
      model.addAttribute("status", result);
      model.addAttribute("empUser", pLogin);
      model.addAttribute("roleBean",bc);
      return VIEW;
    }

     
   // pLogin.setPassword(passwordEncoder.encode(pLogin.getUserName()));
    //pLogin.setChangePasswordInd(1);
    pLogin.setAccountLocked(0);
    pLogin.setAccountEnabled(0);
    pLogin.setDeactivatedInd(0);
    
    this.genericService.saveObject(pLogin);

    LoginAudit wLA = new LoginAudit();

      wLA.setLogin(wLogin);
      wLA.setFirstName(pLogin.getFirstName());
      wLA.setLastName(pLogin.getLastName());
      wLA.setUserName(pLogin.getUserName());
      wLA.setChangedBy(wLogin.getUserName());
      wLA.setDescription("Account Unlocked");
      wLA.setLastModTs(LocalDate.now());
      wLA.setBusinessClientId(bc.getBusinessClientInstId());
      wLA.setAuditTimeStamp(PayrollBeanUtils.getCurrentTime(false));
      wLA.setRemoteIpAddress(request.getRemoteAddr());
 
    this.genericService.saveObject(wLA);

    return "redirect:unlockUserAccount.do?lid="+pLogin.getId()+"&s=1";
  }
}