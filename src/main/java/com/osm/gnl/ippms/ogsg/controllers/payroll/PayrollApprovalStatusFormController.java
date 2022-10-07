package com.osm.gnl.ippms.ogsg.controllers.payroll;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.ProgressBean;
import com.osm.gnl.ippms.ogsg.engine.ApprovePendingPayroll;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;


@Controller
@RequestMapping({"/displayApprovePayroll.do"})
public class PayrollApprovalStatusFormController extends BaseController {
	
	
	  public PayrollApprovalStatusFormController()
	  {}

	  @RequestMapping(method={RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
		  SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);
	    ProgressBean wPB = new ProgressBean();
	    Object o = getSessionAttribute(request, "aprvPay");
	    if (o == null) {
	      return "redirect:viewWageSummary.do";
	    }
	    ApprovePendingPayroll wCP = (ApprovePendingPayroll)o;
	     if (wCP.isFinished()) {
	    	      removeSessionAttribute(request, "aprvPay");
	    	      do{
	    	    	 try {
						Thread.sleep(20);
					} catch (InterruptedException wEx) {
						
						wEx.printStackTrace();
					} 
	    	      }while(this.getTotalNoOfPendingPayChecks(bc) > 0);
	    	      return "redirect:viewWageSummary.do";
	     }
	    	    wPB.setCurrentCount(wCP.getCurrentRecord());
	    	    wPB.setTotalElements(wCP.getTotalRecords());
	     addRoleBeanToModel(model, request);
	    model.addAttribute("progressBean", wPB);
	    return "approvalProgressBarForm";
	  }


	 
	private synchronized int getTotalNoOfPendingPayChecks(BusinessCertificate businessCertificate){

	  	return this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder().addPredicate(Arrays.asList(CustomPredicate.procurePredicate("status","P"))),
				IppmsUtils.getPaycheckClass(businessCertificate));

	}

}
