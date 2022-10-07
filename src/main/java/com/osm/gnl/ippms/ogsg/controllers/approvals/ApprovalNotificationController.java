package com.osm.gnl.ippms.ogsg.controllers.approvals;


import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.StoredProcedureService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.notifications.NotificationObject;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import com.osm.gnl.ippms.ogsg.validators.approval.CreateApprovalUpdateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping({"/approvalUpdates.do"})
@SessionAttributes(types = {NotificationObject.class})
public class ApprovalNotificationController extends BaseController{

    private final String VIEW_NAME = "approval/approvalUpdatesForm";

    private final StoredProcedureService storedProcedureService;
    private final CreateApprovalUpdateValidator createApprovalUpdateValidator;

    @Autowired
    public ApprovalNotificationController(StoredProcedureService storedProcedureService, CreateApprovalUpdateValidator createApprovalUpdateValidator) {
        this.storedProcedureService = storedProcedureService;

        this.createApprovalUpdateValidator = createApprovalUpdateValidator;
    }

    @ModelAttribute("filterList")
    public List<NamedEntity> makeDropDownList(){
        List<NamedEntity> wList = new ArrayList<>();
        wList.add(new NamedEntity(1,"Open Tickets"));
        wList.add(new NamedEntity(2,"Closed Tickets"));
        wList.add(new NamedEntity(3,"All Tickets"));
        return wList;

    }
    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest pRequest) throws Exception {


        SessionManagerService.manageSession(pRequest, model);

        List<CustomPredicate> predicates = new ArrayList<>();
        predicates.add(getBusinessClientIdPredicate(pRequest));
        predicates.add(CustomPredicate.procurePredicate("ticketOpen",IConstants.OFF));
        predicates.add(CustomPredicate.procurePredicate("responseInd",OFF));
        //predicates.add(CustomPredicate.procurePredicate("initiator.id", bc.getLoginId()));
        //predicates.add(CustomPredicate.procurePredicate("approver.id", bc.getLoginId(),Operation.EQUALS,ConjunctionType.OR));

        List<NotificationObject> newNotificationList = this.genericService.loadAllObjectsUsingRestrictions(NotificationObject.class,
                predicates, "ticketTime");
        //--For Some Odd Reason, We have notification objects that do not have ticket id's, if we have this kind of scenarios
        //-- Set those types to
        newNotificationList = checkForNullTicketIds(newNotificationList);
        newNotificationList.sort(Comparator.comparing(NotificationObject ::getTicketTime).reversed());
        Navigator.getInstance(getSessionId(pRequest)).setFromForm("redirect:approvalUpdates.do");
        NotificationObject notificationObject = new NotificationObject();
        notificationObject.setObjectList(newNotificationList);
        model.addAttribute("openTicketsExists",newNotificationList.size() > 0);
        model.addAttribute("sBean", notificationObject.getObjectList().get(0));
        model.addAttribute("miniBean", notificationObject);
        addPageTitle(model,"Open Tickets");
        addRoleBeanToModel(model, pRequest);

        return VIEW_NAME;
    }

    private List<NotificationObject> checkForNullTicketIds(List<NotificationObject> newNotificationList) {
        List<NotificationObject> wList = newNotificationList.stream().filter(NotificationObject::isTicketIdNull).collect(Collectors.toList());
        for(NotificationObject n : wList){
            n.setTicketId(PayrollBeanUtils.getCurrentDateTimeAsLong(n.getSender()));
            this.genericService.storeObject(n);
        }


        return newNotificationList;
    }

    @RequestMapping(method={RequestMethod.GET}, params={"tid"})
    public String setupForm2(@RequestParam("tid") Long tId, Model model, HttpServletRequest request)
            throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);
         List<NotificationObject> notificationObject = this.genericService.loadAllObjectsWithSingleCondition(NotificationObject.class,
                CustomPredicate.procurePredicate("ticketId",tId), "id");
        model.addAttribute("miniBean", notificationObject);
        model.addAttribute("sBean", notificationObject.get(0));
        addRoleBeanToModel(model, request);
        return "approval/approvalLogForm";

    }

    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_reply", required=false) String reply,
                                @RequestParam(value="_closeOpenTickets", required=false) String closeOpenTickets,
                                @ModelAttribute("sBean") NotificationObject pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
    {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        NotificationObject nObj = new NotificationObject();
        if(isButtonTypeClick(request,IConstants.REQUEST_PARAM_CLOSE_ALL_TICKETS)){
            storedProcedureService.callStoredProcedure(CLOSE_OPEN_TICKETS,bc.getBusinessClientInstId());
            return REDIRECT_TO_DASHBOARD;
        }
        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
        {
            return "redirect:approvalUpdates.do";
        }

        if(isButtonTypeClick(request, REQUEST_PARAM_CLOSE)){
            if(!bc.isSuperAdmin())
            if(!pEHB.getInitiator().getId().equals(bc.getLoginId())){
                result.rejectValue("response","Bad Operation"," Only Originators of Request Approval Tickets can close them.");
                addDisplayErrorsToModel(model, request);
                List<NotificationObject> notificationObject = this.genericService.loadAllObjectsWithSingleCondition(NotificationObject.class, CustomPredicate.procurePredicate("ticketId",pEHB.getTicketId()), "");
                pEHB.setObjectList(notificationObject);
                model.addAttribute("miniBean", notificationObject);
                model.addAttribute("status", result);
                model.addAttribute("sBean", pEHB);
                addRoleBeanToModel(model, request);
                return "approval/approvalLogForm";
            }

            List<NotificationObject> nObjects = this.genericService.loadAllObjectsWithSingleCondition(NotificationObject.class, CustomPredicate.procurePredicate(
                    "ticketId", pEHB.getTicketId()), "");


            for(NotificationObject n : nObjects){
                n.setTicketOpen(IConstants.ON);
                this.genericService.saveOrUpdate(n);
            }
            return "redirect:approvalUpdates.do";
        }


        createApprovalUpdateValidator.validate(pEHB, result, bc);
        if (result.hasErrors())
        {
            // ((RelationshipType)result.getTarget()).setDisplayErrors("block");
            addDisplayErrorsToModel(model, request);
            List<NotificationObject> notificationObject = this.genericService.loadAllObjectsWithSingleCondition(NotificationObject.class, CustomPredicate.procurePredicate("ticketId",pEHB.getTicketId()), "");
            pEHB.setObjectList(notificationObject);
            model.addAttribute("miniBean", notificationObject);
            model.addAttribute("status", result);
            model.addAttribute("sBean", pEHB);
            addRoleBeanToModel(model, request);
            return "approval/approvalLogForm";
        }
        nObj.setInitiator(pEHB.getInitiator());
        nObj.setApprover(pEHB.getApprover());
        nObj.setApprovalStatusInd(pEHB.getApprovalStatusInd());
        nObj.setApprovedDate(pEHB.getApprovedDate());
        nObj.setUrl(pEHB.getUrl());
        nObj.setSubject(pEHB.getSubject());
        nObj.setTicketId(pEHB.getTicketId());
        nObj.setSenderName(bc.getLoggedOnUserNames());
        nObj.setBusinessClientId(bc.getBusinessClientInstId());
        nObj.setApprovalMemo(pEHB.getResponse());
        nObj.setLastModTs(LocalDate.now());
        nObj.setResponseInd(1);
        nObj.setEmployeeId(pEHB.getEmployeeId());
        nObj.setEntityName(pEHB.getEntityName());
        nObj.setEntityId(pEHB.getEntityId());
        this.genericService.saveObject(nObj);

        return "redirect:approvalUpdates.do?tid="+pEHB.getTicketId();
    }

//    @RequestMapping(method = {RequestMethod.GET}, params = {"op","s"})
//    @ResponseBody
//    public List reloadTable(@RequestParam("optid") Integer optionType, @RequestParam("s") String str, HttpServletRequest request, Model model, BusinessCertificate bc) throws EpmAuthenticationException, HttpSessionRequiredException, IllegalAccessException, InstantiationException {
//        SessionManagerService.manageSession(request, model);
//
//        List<NotificationObject>  approverList = new ArrayList<>();
//
//
//        List<CustomPredicate> predicates = new ArrayList<>();
//        predicates.add(getBusinessClientIdPredicate(request));
//
//        switch (optionType){
//            case 1:
//                predicates.add(CustomPredicate.procurePredicate("ticketOpen",IConstants.OFF));
//                break;
//            case 2:
//                predicates.add(CustomPredicate.procurePredicate("ticketOpen",IConstants.ON));
//                break;
//            case 3:
//                //do nothing
//                break;
//        }
//        predicates.add(CustomPredicate.procurePredicate("initiator.id", bc.getLoginId()));
//        predicates.add(CustomPredicate.procurePredicate("approver.id", bc.getLoginId(),Operation.EQUALS,ConjunctionType.OR));
//
//        List<NotificationObject> newNotificationList = this.genericService.loadAllObjectsUsingRestrictions(NotificationObject.class,
//                predicates, "ticketTime");
//
//        for(NotificationObject notObj: newNotificationList){
//            NotificationObject nt = new NotificationObject();
//            nt.setApprovalMemo(notObj.getApprovalMemo());
//            nt.setSubject(notObj.getSubject());
//            nt.setApprovalStatusStr(notObj.getApprovalStatusStr());
//            nt.setApprovalDateStr(notObj.getApprovalDateStr());
//            nt.setApprovedBy(notObj.getApprovedBy());
//            nt.setTicketOpen(notObj.getTicketOpen());
//        }
//
//        newNotificationList.sort(Comparator.comparing(NotificationObject ::getTicketTime).reversed());
//        approverList.add((NotificationObject) newNotificationList);
//
//        return approverList;
//
//    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"op","s"})
    public String reloadTable(@RequestParam("op") Integer optionType, @RequestParam("s") String str, HttpServletRequest request, Model model, BusinessCertificate bc) throws EpmAuthenticationException, HttpSessionRequiredException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);

        List<CustomPredicate> predicates = new ArrayList<>();
        predicates.add(getBusinessClientIdPredicate(request));
        String pageTitle="";

        switch (optionType){
            case 1:
                predicates.add(CustomPredicate.procurePredicate("ticketOpen",IConstants.OFF));
                predicates.add(CustomPredicate.procurePredicate("responseInd",OFF));
                pageTitle = "Open Tickets";
                break;
            case 2:
                predicates.add(CustomPredicate.procurePredicate("ticketOpen",IConstants.ON));
                pageTitle = "Closed Tickets";
                break;
            case 3:
                //do nothing
                pageTitle = "All Tickets";
                break;
        }

//        predicates.add(CustomPredicate.procurePredicate("initiator.id", bc.getLoginId()));
//        predicates.add(CustomPredicate.procurePredicate("approver.id", bc.getLoginId(),Operation.EQUALS,ConjunctionType.OR));

        List<NotificationObject> newNotificationList = this.genericService.loadAllObjectsUsingRestrictions(NotificationObject.class,
                predicates, "ticketTime");

        NotificationObject notificationObject = new NotificationObject();
        notificationObject.setFilterInd(optionType);
        notificationObject.setObjectList(newNotificationList);

        model.addAttribute("miniBean", notificationObject);
        addPageTitle(model,pageTitle);
        addRoleBeanToModel(model, request);

        return VIEW_NAME;

    }

}
