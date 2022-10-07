package com.osm.gnl.ippms.ogsg.controllers.hr.ext;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.domain.approval.TransferApproval;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping({"/viewRejectedTransfers.do"})
@SessionAttributes(types={PaginatedBean.class})
public class ViewRejectedTransferRequests extends BaseController {


	  private final int pageLength = 20;

	 
	  public ViewRejectedTransferRequests() {}

	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request)
	    throws Exception
	  {
		  SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

	     PaginationBean paginationBean = getPaginationInfo(request);
		  List<CustomPredicate> predicates = new ArrayList<>();
		  predicates.addAll(Arrays.asList(getBusinessClientIdPredicate(request),CustomPredicate.procurePredicate("approvalStatusInd",2), CustomPredicate.procurePredicate("initiator.id",bc.getLoginId())));
	    List<TransferApproval> empList = this.genericService.loadPaginatedObjects(TransferApproval.class,predicates,(paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder()
				, paginationBean.getSortCriterion());

	    for(TransferApproval t : empList){
	    	 
	    	t.setOldMda(t.getParentObject().getCurrentMdaName());
	    	 
	    	 if(!t.getSchoolInfo().isNewEntity()) {
		 	    	t.setNewMda(t.getSchoolInfo().getName());
		 	    }else
		 	    	t.setNewMda(t.getMdaDeptMap().getMdaInfo().getName()); 
	    }
	    int wNoOfElements = this.genericService.getTotalPaginatedObjects(TransferApproval.class,predicates).intValue();

	    PaginatedBean wPHDB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

	   addRoleBeanToModel(model, request);
	    model.addAttribute("miniBean", wPHDB);

	    return "transfer/viewRejectedTransfersForm";
	  }

	@RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
			  @RequestParam(value="_delete", required=false) String delete,
			  @ModelAttribute("miniBean") PaginatedBean pHADB, BindingResult result, SessionStatus
			  status, Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);
 
	    
	    if(isButtonTypeClick(request,REQUEST_PARAM_DELETE)){
	    	for(TransferApproval t : (List<TransferApproval>) pHADB.getObjectList()){
	    		t.setApprovalStatusInd(3);
	    		t.setDeleteDate(LocalDate.now());
	    		t.setAuditTime(PayrollBeanUtils.getCurrentTime(false));
	    		this.genericService.storeObject(t);
	    	}
	    }

	    //if(Navigator.getInstance(getSessionId(request)).getFromForm() != null)
	          return Navigator.getInstance(getSessionId(request)).getFromForm();
	  //  else
	    //	return REDIRECT_TO_DASHBOARD;
	  }


}
