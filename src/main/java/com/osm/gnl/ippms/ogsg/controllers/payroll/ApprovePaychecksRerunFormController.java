package com.osm.gnl.ippms.ogsg.controllers.payroll;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.IppmsEncoder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.StoredProcedureService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payment.beans.PayPeriodDaysMiniBean;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRerun;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping({"/approveRerunPaychecks.do"})
@SessionAttributes(types={PayPeriodDaysMiniBean.class})
public class ApprovePaychecksRerunFormController extends BaseController {

 	  private final StoredProcedureService storedProcedureService;
	  private final int pageLength = 20;
	  
	  private final String VIEW = "payment/approveRerunPaychecksForm";

	  @Autowired
	  public ApprovePaychecksRerunFormController(StoredProcedureService storedProcedureService) {

		  this.storedProcedureService = storedProcedureService;
	  }

	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
		  SessionManagerService.manageSession(request, model);

		  BusinessCertificate bc = this.getBusinessCertificate(request);

		  PaginationBean paginationBean = getPaginationInfo(request);
		  List<AbstractPaycheckEntity> empList = (List<AbstractPaycheckEntity>)this.genericService.loadPaginatedObjects
				  (IppmsUtils.getPaycheckClass(bc), Arrays.asList(CustomPredicate.procurePredicate("status","P"), getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("reRunInd", ON)),(paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

		  int wNoOfElements = this.genericService.getTotalPaginatedObjects(IppmsUtils.getPaycheckClass(bc),Arrays.asList(CustomPredicate.procurePredicate("status","P"), getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("reRunInd", ON))).intValue();

		  PayPeriodDaysMiniBean pPDMB = new PayPeriodDaysMiniBean(empList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

	    pPDMB.setAdmin(bc.isSuperAdmin());

	    pPDMB.setParentInstId(bc.getBusinessClientInstId());
		  bc.setRerunPayrollExists(this.genericService.isObjectExisting(PayrollRerun.class, Arrays.asList(getBusinessClientIdPredicate(request))));
	    model.addAttribute("approveBean", pPDMB);
	    model.addAttribute("roleBean", bc);
		  Navigator.getInstance(IppmsEncoder.getSessionKey()).setFromForm("redirect:approveRerunPaychecks.do");
		  Navigator.getInstance(IppmsEncoder.getSessionKey()).setFromClass(ApprovePaychecksRerunFormController.class);
	    return VIEW;
	  }

	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET},params={"pid"})
	  public String setupForm(@RequestParam("pid") Long pBid,Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
	  {
		  SessionManagerService.manageSession(request, model);

	    this.storedProcedureService.callStoredProcedure(IConstants.DEL_SING_RERUN_PAYCHECKS,getBusinessCertificate(request).getBusinessClientInstId());
	    return "redirect:approvePaychecksForm.do";
	  }

	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
			  @ModelAttribute("approveBean") PayPeriodDaysMiniBean ppDMB, 
			  BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);



	   if(isButtonTypeClick(request,REQUEST_PARAM_CANCEL)){
	    	
	    	 return REDIRECT_TO_DASHBOARD;
	    }
	      BusinessCertificate businessCertificate = getBusinessCertificate(request);
	    //First Update the Values to Zero and then Delete the PayrollReruns
    	this.storedProcedureService.callStoredProcedure(DEL_PAYROLL_RERUN_PROC,businessCertificate.getBusinessClientInstId());
    	this.storedProcedureService.callStoredProcedure(UPD_RERUN_IND_PROC,businessCertificate.getBusinessClientInstId());
	  
	    return "redirect:approvePaychecksForm.do";
	  }


}
