/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.auth.services;

import com.osm.gnl.ippms.ogsg.auth.domain.Mailer;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.GeneratePayslipFile;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Calendar;

@Service("mailerService")
public class MailerServiceImpl implements MailerService{

    @Value("${spring.mail.username}")
    private String SENDER;

    private final JavaMailSender javaMailSender;
    private final GeneratePayslipFile generatePayslipFile;


    @Autowired
    public MailerServiceImpl(JavaMailSender javaMailSender, GeneratePayslipFile generatePayslipFile) {
        this.javaMailSender = javaMailSender;
        this.generatePayslipFile = generatePayslipFile;
    }



    @Override
    public boolean sendMailWithAttachments(Mailer mailer) {
        try{
            MimeMessage msg = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setFrom(SENDER);
            helper.setReplyTo(SENDER);
            msg.setSentDate(Calendar.getInstance().getTime());
            helper.setTo(mailer.getRecipient());

            if(IppmsUtils.isNotNullOrEmpty(mailer.getCopier() ))
                helper.setCc(mailer.getCopier());

            helper.setSubject("Payslip Notification");

            helper.setText("Find attached your requested Pay Slip", true);

            File file = this.generatePayslipFile.mailSinglePayslip(mailer.getId(),mailer.getRunMonth(), mailer.getRunYear(),mailer.getBusinessCertificate(),mailer.getHttpServletRequest());

            helper.addAttachment(file.getName(), file);

            javaMailSender.send(msg);
        }catch (Exception wEx){
            return false;
        }
        return true;
    }

    @Override
    public boolean sendMailForPasswordReset(Mailer mailer) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(mailer.getRecipient());
        msg.setFrom(SENDER);
        msg.setReplyTo(SENDER);
        msg.setSentDate(Calendar.getInstance().getTime());
        msg.setSubject(mailer.getSubject());
        msg.setText(mailer.getMessage());
        try{
            javaMailSender.send(msg);
        }catch (Exception wEx){
            return false;
        }
        return  true;
    }
}
