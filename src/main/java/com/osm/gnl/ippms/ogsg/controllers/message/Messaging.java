package com.osm.gnl.ippms.ogsg.controllers.message;


import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.MessageService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.message.MessageObject;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Controller
@RequestMapping(value = "/messaging.do")
public class Messaging extends BaseController {

    @Autowired
    private MessageService messageService;

    public Messaging() {}

    @RequestMapping(method = RequestMethod.GET)
    public String messageReceived(HttpServletRequest request, Model model) throws EpmAuthenticationException, HttpSessionRequiredException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        User user = this.genericService.loadObjectById(User.class,bc.getLoginId());
        long userId = user.getId();
        //System.out.println("user id is "+userId);
        List<MessageObject> messageObject = this.genericService.loadAllObjectsWithSingleCondition(MessageObject.class, new CustomPredicate("recipientId",userId),null);

        Collections.sort(messageObject, Comparator.comparing(MessageObject::getId).reversed());
        long fid = 0;
        MessageObject messageBody;

        if(IppmsUtils.isNotNullOrEmpty(messageObject)){
            messageBody = messageObject.get(0);
            fid = messageBody.getId();
            
        }

        model.addAttribute("msg", messageObject);
        //model.addAttribute("body",body);
        model.addAttribute("fid",fid);
        addRoleBeanToModel(model,request);
        return "message/receivedMessages";
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"sent"})
    public String messageSent(HttpServletRequest request, Model model) throws EpmAuthenticationException, HttpSessionRequiredException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        User user = this.genericService.loadObjectById(User.class,bc.getLoginId());
        long userId = user.getId();
        List<MessageObject> messageObject = this.genericService.loadAllObjectsWithSingleCondition(MessageObject.class,new CustomPredicate("senderId",userId),null);
        Collections.sort(messageObject, Comparator.comparing(MessageObject::getId).reversed());
        String body = EMPTY_STR;
        MessageObject messageBody;

        if(IppmsUtils.isNotNullOrEmpty(messageObject)){
            messageBody = messageObject.get(0);
            body = messageBody.getMsgBody();
        }

        List<User> emp = this.messageService.loadUsers(false,bc.getLoginId());

        model.addAttribute("emp", emp);
        model.addAttribute("msg", messageObject);
        model.addAttribute("body",body);
        addRoleBeanToModel(model,request);
        return "message/sentMessages";
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"com"})
        public String composeMessage(@RequestParam("com") String c, HttpServletRequest request, Model model) throws EpmAuthenticationException, HttpSessionRequiredException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
       // List<User> emp = this.genericService.loadControlEntity(User.class);
        List<User> emp = this.messageService.loadUsers(false,bc.getLoginId());
        model.addAttribute("emp", emp);
        addRoleBeanToModel(model,request);
        return "message/composeMessage";
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"to", "subject", "body"})
    public RedirectView sendMessage(@RequestParam(value = "to") String to, @RequestParam(value = "subject") String subject,
                              @RequestParam(value = "body") String body, HttpServletRequest request,
                              Model model, RedirectAttributes redirectAttributes) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        RedirectView redirectView= new RedirectView("/messaging.do?sent",true);
        String sent;

        List<Long> recipient = new ArrayList<>();
        for(String s : to.split(","))
            recipient.add(Long.parseLong(s));
        //System.out.println("Recipient are "+recipient);

        PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate("accountExpired",0));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("accountLocked",0));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("accountEnabled",0));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("deactivatedInd",0));
        Map<Long,User> userMap = this.genericService.loadObjectAsMapWithConditions(User.class,predicateBuilder.getPredicates(),"id");
        MessageObject messageObject = null;
        List<MessageObject> listMessages = new ArrayList<>();
        for(Long id : recipient){
            User user = userMap.get(id);
            if(user != null) {
                messageObject = new MessageObject();
                messageObject.setSender(bc.getLoggedOnUserNames());
                messageObject.setSenderId(bc.getLoginId());
                messageObject.setRecipient(user.getActualUserName());
                messageObject.setRecipientId(id);
                messageObject.setSubject(subject);
                messageObject.setMsgBody(body);
                messageObject.setTimeSent(Timestamp.from(Instant.now()));
                listMessages.add(messageObject);
            }

        }
         this.genericService.storeObjectBatch(listMessages);

         sent = "Message sent successfully";

        redirectAttributes.addFlashAttribute("sent", sent);
        addRoleBeanToModel(model, request);
        return redirectView;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"rid"})
    @ResponseBody
    public List viewReceivedMessages(@RequestParam("rid") long mid, HttpServletRequest request, Model model) throws EpmAuthenticationException, HttpSessionRequiredException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);

        MessageObject msgObj = this.genericService.loadObjectById(MessageObject.class, mid);
        String body = EMPTY_STR;

        if(IppmsUtils.isNotNull(msgObj)){
            body = msgObj.getMsgBody();
        }

        int update = this.messageService.updateMessageStatus(IConstants.ON, mid);
        List<String> result = new ArrayList<>();

        if(update > 0){
            result.add(String.valueOf(mid));
            result.add(body);
        }
        return result;

    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"sid"})
    @ResponseBody
    public String viewSentMessages(@RequestParam("sid") long mid, HttpServletRequest request, Model model) throws EpmAuthenticationException, HttpSessionRequiredException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        MessageObject msgObj = this.genericService.loadObjectById(MessageObject.class, mid);
        String body = EMPTY_STR;

        if(IppmsUtils.isNotNull(msgObj)){
            body = msgObj.getMsgBody();
        }
        return body;
    }


    @RequestMapping(method = {RequestMethod.GET}, params = {"replyId"})
    public String replyMessage(@RequestParam("replyId") long replyId, HttpServletRequest request, Model model) throws EpmAuthenticationException, HttpSessionRequiredException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        MessageObject msg = this.genericService.loadObjectById(MessageObject.class, replyId);
        addRoleBeanToModel(model,request);
        model.addAttribute("msg",msg);
        model.addAttribute("sender", bc.getLoggedOnUserNames());
        List<User> emp = this.genericService.loadControlEntity(User.class);
//        List<User> emp = this.messageService.loadUsers(1005);
        model.addAttribute("emp", emp);
        return "message/replyMessage";
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"replyTo", "subject", "body"})
    public RedirectView sendReply(@RequestParam("replyTo") String to, @RequestParam("subject") String subject,
                                  @RequestParam("body") String body, HttpServletRequest request,
                                  Model model, RedirectAttributes redirectAttributes) throws EpmAuthenticationException, HttpSessionRequiredException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        RedirectView redirectView= new RedirectView("/messaging.do",true);
        String sent;
        List<Long> recipient = new ArrayList<>();
        for(String s : to.split(","))
            recipient.add(Long.parseLong(s));


        Long result=0L;
        if(IppmsUtils.isNotNullOrEmpty(body)){
            for(Long i:recipient){
                User user = this.genericService.loadObjectById(User.class,i);
                MessageObject messageObject = new MessageObject();
                messageObject.setSender(bc.getLoggedOnUserNames());
                messageObject.setSenderId(bc.getLoginId());
                messageObject.setRecipient(user.getActualUserName());
                messageObject.setRecipientId(i);
                messageObject.setSubject(subject);
                messageObject.setMsgBody(body);
                messageObject.setTimeSent(Timestamp.from(Instant.now()));
                result = this.genericService.storeObject(messageObject);
            }
        }

        if(result > 0){
            sent = "Message sent successfully";

        }
        else{
            sent = "Message not successfully sent, please try again";
        }
        redirectAttributes.addFlashAttribute("sent", sent);
        addRoleBeanToModel(model, request);
        return redirectView;
    }

    public static int countMessage(BusinessCertificate bc, GenericService genericService){
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("dataStatus", IConstants.OFF));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("recipientId", bc.getLoginId()));
        int countMessage = genericService.countObjectsUsingPredicateBuilder(predicateBuilder, MessageObject.class);
        return countMessage;
    }
}