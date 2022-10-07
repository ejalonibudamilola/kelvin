/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.audit.domain.LoginAudit;
import com.osm.gnl.ippms.ogsg.auth.domain.Mailer;
import com.osm.gnl.ippms.ogsg.auth.domain.PasswordResetBean;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.auth.services.MailerService;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
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
@RequestMapping({"/forgetPassword.gnl"})
@SessionAttributes(types={User.class})
public class ForgotPasswordController {


    private final PasswordEncoder passwordEncoder;
    private final MailerService mailerService;
    private final GenericService genericService;
    private final String VIEW = "user/forgotPasswordForm";

    @Autowired
    public ForgotPasswordController(PasswordEncoder passwordEncoder, MailerService mailerService, GenericService genericService) {
        this.passwordEncoder = passwordEncoder;
        this.mailerService = mailerService;
        this.genericService = genericService;
    }



    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String setupForm(@RequestParam(value = "lid", required = false) Long pLid, Model model, HttpServletRequest request) throws Exception {

        if(IppmsUtils.isNotNullAndGreaterThanZero(pLid)){
            User wLogin = genericService.loadObjectById(User.class, pLid);
            model.addAttribute("epmUser", wLogin);
            model.addAttribute(IConstants.SAVED_MSG, "Password for " + wLogin.getActualUserName() + " reset. New Credentials Email sent successfully");
            model.addAttribute("saved", true);
        }else{
            model.addAttribute("epmUser", new User());
        }

        return VIEW;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@ModelAttribute("epmUser") User pLogin, BindingResult result, SessionStatus status, Model model,
                                HttpServletRequest request) throws Exception {


        //--First get the User Name....
        User user = this.genericService.loadObjectWithSingleCondition(User.class, CustomPredicate.procurePredicate("username",pLogin.getUserName()));
        if(user.isNewEntity()){
            result.rejectValue("", "Invalid.True","No User with Username "+pLogin.getUsername()+" Found.");
            model.addAttribute(IConstants.DISPLAY_ERRORS, IConstants.BLOCK);
             model.addAttribute("epmUser", pLogin);
            model.addAttribute("status", result);
            return VIEW;
        }else if(IppmsUtils.isNullOrEmpty(user.getEmail())){
            result.rejectValue("", "Invalid.True","User "+pLogin.getUsername()+" Has no Email Address.");
            model.addAttribute(IConstants.DISPLAY_ERRORS, IConstants.BLOCK);
            model.addAttribute("epmUser", pLogin);
            model.addAttribute("status", result);
        }
        user.setChangePasswordInd(IConstants.ON);

        Mailer mailer = new Mailer();
        mailer.setAutoGenPassword(PassPhrase.generatePassword());
        mailer.setExpiration(Timestamp.valueOf(LocalDateTime.now().plus(Duration.of(7, ChronoUnit.MINUTES))));
        mailer.setSubject("IPPMS Account Password Reset.");
        mailer.setMessage("Dear " + user.getActualUserName() + ",\n here are your login credentials : Username : " + user.getUsername() + "  Password. " + mailer.getAutoGenPassword() + "\n  NOTE this password expires by " + PayrollUtils.formatTimeStamp(mailer.getExpiration()));
        mailer.setRecipient(user.getEmail());

        if (!mailerService.sendMailForPasswordReset(mailer)) {
            result.rejectValue("id", "Invalid.Value", "Password Reset Email Could not be sent to " + user.getEmail() + ". Password Reset Denied.");
            model.addAttribute(IConstants.DISPLAY_ERRORS, IConstants.BLOCK);
            model.addAttribute("status", result);
            model.addAttribute("empUser", pLogin);
            return VIEW;
        }

        PasswordResetBean passwordResetBean;

        if (user.getPasswordResetId() != null) {
            passwordResetBean = genericService.loadObjectById(PasswordResetBean.class, user.getId());
        } else {
            passwordResetBean = new PasswordResetBean();
        }
        passwordResetBean.setEmail(user.getEmail());
        passwordResetBean.setPassword(mailer.getAutoGenPassword());
        passwordResetBean.setResetBy(user);
        passwordResetBean.setExpirationDate(mailer.getExpiration());
        passwordResetBean.setUser(user);

        user.setPasswordResetId(genericService.storeObject(passwordResetBean));
        user.setPassword(passwordEncoder.encode(mailer.getAutoGenPassword()));
        this.genericService.storeObject(user);

        LoginAudit wLA = new LoginAudit();

        wLA.setLogin(user);
        wLA.setFirstName(user.getFirstName());
        wLA.setLastName(user.getLastName());
        wLA.setUserName(user.getUserName());
        wLA.setChangedBy(user.getUserName());
        wLA.setDescription("Forgot Password Reset Initiated.");
        wLA.setLastModTs(LocalDate.now());
        wLA.setBusinessClientId(user.getRole().getBusinessClient().getId());
        wLA.setAuditTimeStamp(PayrollBeanUtils.getCurrentTime(false));
        wLA.setRemoteIpAddress(request.getRemoteAddr());
        genericService.saveObject(wLA);


        return "redirect:forgetPassword.gnl?lid=" + user.getId();
    }
}
