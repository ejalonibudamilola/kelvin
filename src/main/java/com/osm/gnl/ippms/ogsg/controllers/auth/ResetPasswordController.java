package com.osm.gnl.ippms.ogsg.controllers.auth;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.audit.domain.LoginAudit;
import com.osm.gnl.ippms.ogsg.auth.domain.Mailer;
import com.osm.gnl.ippms.ogsg.auth.domain.PasswordResetBean;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.auth.services.MailerService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PassPhrase;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
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
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Controller
@RequestMapping({"/resetUser.do"})
@SessionAttributes(types = {User.class})
public class ResetPasswordController extends BaseController {


    private final PasswordEncoder passwordEncoder;
    private final MailerService mailerService;
    private final String VIEW = "user/resetEpmUserPassword";

    @Autowired
    public ResetPasswordController(PasswordEncoder passwordEncoder, MailerService mailerService) {
        this.passwordEncoder = passwordEncoder;
        this.mailerService = mailerService;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"lid"})
    public String setupForm(@RequestParam("lid") Long pLid, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);


        User wLogin = genericService.loadObjectById(User.class, pLid);

        addRoleBeanToModel(model, request);
        model.addAttribute("epmUser", wLogin);

        return VIEW;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"lid", "s"})
    public String setupForm(@RequestParam("lid") Long pLid,
                            @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        User wLogin = genericService.loadObjectById(User.class, pLid);
        addRoleBeanToModel(model, request);
        model.addAttribute("epmUser", wLogin);
        model.addAttribute(IConstants.SAVED_MSG, "Password for " + wLogin.getActualUserName() + " reset successfully");
        model.addAttribute("saved", true);
        return VIEW;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel, @ModelAttribute("epmUser") User pLogin, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return REDIRECT_TO_DASHBOARD;
        }


        User wLogin = genericService.loadObjectWithSingleCondition(User.class, CustomPredicate.procurePredicate("username", bc.getUserName()));

        if ((wLogin.getRole().getName().equalsIgnoreCase("SUPER_ADMIN")) &&
                (!bc.isSuperAdmin())) {
            result.rejectValue("id", "Invalid.Value", "Only a SUPER ADMIN can Reset Password for a SUPER ADMIN account");
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
            model.addAttribute("status", result);
            model.addAttribute("empUser", pLogin);
            model.addAttribute("roleBean", bc);
            return VIEW;
        }
        User wLoggedInUser = genericService.loadObjectById(User.class, bc.getLoginId());
        if (wLogin.isPrivilegedUser() && !wLoggedInUser.isPrivilegedUser()) {
            result.rejectValue("id", "Invalid.Value", "This account is a Privileged Account. Only a Privileged User can Reset Password for another Privileged User.");
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
            model.addAttribute("status", result);
            model.addAttribute("empUser", pLogin);
            model.addAttribute("roleBean", bc);
            return VIEW;
        }

        pLogin.setChangePasswordInd(ON);
        // pLogin.setAccountLocked(0);
        // pLogin.setDeactivatedInd(0);

        Mailer mailer = new Mailer();
        mailer.setAutoGenPassword(PassPhrase.generatePassword());
        mailer.setExpiration(Timestamp.valueOf(LocalDateTime.now().plus(Duration.of(7, ChronoUnit.MINUTES))));
        mailer.setSubject("IPPMS Account Password Reset.");
        mailer.setMessage("Dear " + pLogin.getActualUserName() + ", here are your login credentials : Username : " + pLogin.getUsername() + "  Password. " + mailer.getAutoGenPassword() + "  NOTE this password expires by " + PayrollUtils.formatTimeStamp(mailer.getExpiration()));
        mailer.setRecipient(pLogin.getEmail());

        if (!mailerService.sendMailForPasswordReset(mailer)) {
            result.rejectValue("id", "Invalid.Value", "Password Reset Email Could not be sent to " + pLogin.getEmail() + ". Password Reset Denied.");
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
            model.addAttribute("status", result);
            model.addAttribute("empUser", pLogin);
            model.addAttribute("roleBean", bc);
            return VIEW;
        }

        PasswordResetBean passwordResetBean;

        if (pLogin.getPasswordResetId() != null) {
            passwordResetBean = genericService.loadObjectById(PasswordResetBean.class, pLogin.getId());
        } else {
            passwordResetBean = new PasswordResetBean();
        }
        passwordResetBean.setEmail(pLogin.getEmail());
        passwordResetBean.setPassword(mailer.getAutoGenPassword());
        passwordResetBean.setResetBy(wLoggedInUser);
        passwordResetBean.setExpirationDate(mailer.getExpiration());
        passwordResetBean.setUser(pLogin);

        pLogin.setPasswordResetId(genericService.storeObject(passwordResetBean));
        pLogin.setPassword(passwordEncoder.encode(mailer.getAutoGenPassword()));
        this.genericService.storeObject(pLogin);

        LoginAudit wLA = new LoginAudit();

        wLA.setLogin(wLogin);
        wLA.setFirstName(pLogin.getFirstName());
        wLA.setLastName(pLogin.getLastName());
        wLA.setUserName(pLogin.getUserName());
        wLA.setChangedBy(wLogin.getUserName());
        wLA.setDescription("Password Reset Initiated.");
        wLA.setLastModTs(LocalDate.now());
        wLA.setBusinessClientId(bc.getBusinessClientInstId());
        wLA.setAuditTimeStamp(PayrollBeanUtils.getCurrentTime(false));
        wLA.setRemoteIpAddress(request.getRemoteAddr());
        genericService.saveObject(wLA);

        if (wLogin.getId().equals(pLogin.getId())) {
            return "redirect:relogin.do";
        }

        return "redirect:resetUser.do?lid=" + pLogin.getId() + "&s=1";
    }
}