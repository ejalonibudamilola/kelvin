package com.osm.gnl.ippms.ogsg.controllers.user;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.auth.repository.IUserRepository;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.BusinessPtoMiniBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping({"/selectUserToDeactivate.do"})
@SessionAttributes(types={BusinessPtoMiniBean.class})
public class SelectUserForDeactivationController extends BaseController {

  @Autowired
  private IUserRepository userRepository;

  //  private final String VIEW = "user/selectUserForDeactivationForm";
  private final String VIEW = "user/selectUserForm";

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);

    List<User> wLL = userRepository.loadActiveLoginUsingFilters(bc);

    BusinessPtoMiniBean wBPMB = new BusinessPtoMiniBean();
    wBPMB.setLoginList(wLL);

    model.addAttribute("miniBean", wBPMB);
    model.addAttribute("roleBean",bc);
    model.addAttribute("title", "Select User to Deactivate");
    model.addAttribute("url", "selectUserToDeactivate.do?uid");
    model.addAttribute("confirmBox", "Are you sure you want to deactivate this user");

    return VIEW;
  }

  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_done", required=false) String done, @ModelAttribute("miniBean") BusinessPtoMiniBean pBPMB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);

     
    if (isButtonTypeClick(request, REQUEST_PARAM_DONE) || isButtonTypeClick(request,REQUEST_PARAM_CANCEL))
    {
      return REDIRECT_TO_DASHBOARD;
    }

    if (IppmsUtils.isNullOrLessThanOne(pBPMB.getId())) {
      result.rejectValue("id", "Required.Value", "Please select a User to deactivate account");
      model.addAttribute(DISPLAY_ERRORS, BLOCK);
      model.addAttribute("status", result);
      model.addAttribute("miniBean", pBPMB);
      model.addAttribute("roleBean",bc);
      return VIEW;
    }
    User wLogin = this.genericService.loadObjectUsingRestriction(User.class,
            Arrays.asList(CustomPredicate.procurePredicate("role.businessClient.id", bc.getBusinessClientInstId()),
                    CustomPredicate.procurePredicate("id",pBPMB.getId())));
    if ((wLogin.getRole().getName().equalsIgnoreCase("SUPER_ADMIN")) &&
      (!bc.isSuperAdmin())) {
      result.rejectValue("id", "Invalid.Value", "Only a SUPER ADMIN can Deactivate a SUPER ADMIN account");
      model.addAttribute(DISPLAY_ERRORS, BLOCK);
      model.addAttribute("status", result);
      model.addAttribute("miniBean", pBPMB);
      model.addAttribute("roleBean",bc);
      return VIEW;
    }

    return "redirect:deactivateUser.do?lid=" + pBPMB.getId();
  }


  @RequestMapping(method = {RequestMethod.GET}, params = {"uid"})
  public String setupForm(@RequestParam(value = "uid") String pUid, Model model, HttpServletRequest request) throws Exception {
    SessionManagerService.manageSession(request, model);


    return "redirect:deactivateUser.do?lid=" + pUid;

  }
}