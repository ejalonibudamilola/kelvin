/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.transfer;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.domain.approval.TransferApproval;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Arrays;


@Controller
@RequestMapping({"/transferDetails.do"})
@SessionAttributes(types={TransferApproval.class})
public class TransferDetailsFormController extends BaseController {
   
	   private final String VIEW = "transfer/transferDetailsForm";
	   
	  public TransferDetailsFormController( )
	  {
	    
	  }
	  @RequestMapping(method={RequestMethod.GET}, params={"tid"})
	  public String setupForm(@RequestParam("tid") Long pTaid, Model model, HttpServletRequest request) throws Exception { 
		  
		  
		  SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);


	    //Load this TransferApproval...
	    TransferApproval wTA = genericService.loadObjectById(TransferApproval.class, pTaid);
	    
	    wTA.setOldMda(wTA.getParentObject().getParentObjectName());
	   	 
	    if(wTA.getSchoolInfo().isNewEntity()) {
	    	wTA.setNewMda(wTA.getSchoolInfo().getName());
	    }else
	    	wTA.setNewMda(wTA.getMdaDeptMap().getMdaInfo().getName());


	    model.addAttribute("miniBean", wTA);
	    model.addAttribute("roleBean", bc);
	    return VIEW; 
	   
	  }
	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"tid","s"})
	  public String setupForm(@RequestParam("tid") Long pTaid,
			  @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {  
		  
		  SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);


	    //Load this TransferApproval...
	    TransferApproval wTA = genericService.loadObjectById(TransferApproval.class, pTaid);
	   
	   	wTA.setOldMda(wTA.getParentObject().getParentObjectName());
	   	 
	    if(wTA.getSchoolInfo().isNewEntity()) {
	    	wTA.setNewMda(wTA.getSchoolInfo().getName());
	    }else
	    	wTA.setNewMda(wTA.getMdaDeptMap().getMdaInfo().getName());
       
      	model.addAttribute(IConstants.SAVED_MSG, "Transfer Request Deleted Successfully.");
      
	    
	    model.addAttribute("saved", true);
	    model.addAttribute("miniBean", wTA);
	    model.addAttribute("roleBean", bc);
	    
	    return VIEW; 
	   
	  }

	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, 
			  @RequestParam(value="_delete", required=false) String delete,  @ModelAttribute("miniBean") TransferApproval pEHB, BindingResult result,
			  SessionStatus status, Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

	    

	    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
			PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(getBusinessClientIdPredicate(request));
			predicateBuilder.addPredicate(Arrays.asList(CustomPredicate.procurePredicate("approvalStatusInd",2, Operation.GREATER_OR_EQUAL), CustomPredicate.procurePredicate("initiator.id",bc.getLoginId())));
	    	 if(genericService.countObjectsUsingPredicateBuilder(predicateBuilder, TransferApproval.class) == 0)
	    	    return Navigator.getInstance(getSessionId(request)).getFromForm();

	    	 return "redirect:viewRejectedTransfers.do";
	    }else if(isButtonTypeClick(request,REQUEST_PARAM_DELETE)) {
			//Otherwise Delete it....

			pEHB.setApprovalStatusInd(3);
			pEHB.setDeleteDate(LocalDate.now());
			pEHB.setApprovalAuditTime(PayrollBeanUtils.getCurrentTime(false));

			this.genericService.saveObject(pEHB);

			return "redirect:transferDetails.do?tid=" + pEHB.getId() + "&s=2";

		}
		  return "redirect:viewRejectedTransfers.do";
	  }

	  

}
