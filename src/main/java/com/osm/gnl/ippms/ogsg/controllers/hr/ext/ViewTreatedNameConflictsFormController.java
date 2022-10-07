package com.osm.gnl.ippms.ogsg.controllers.hr.ext;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.SetupEmployeeMaster;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.DataTableBean;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Damilola Ejalonibu
 */

@Controller
@RequestMapping({"/viewTreatedNameConflicts.do"})
@SessionAttributes(types={DataTableBean.class})
public class ViewTreatedNameConflictsFormController extends BaseController {

	
	  private final int pageLength = 20;
	  private final String VIEW = "conflict/viewTreatedNameConflictsForm";

	 
	  public ViewTreatedNameConflictsFormController() {}
	   
	  @RequestMapping(method={RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request) throws Exception{
		  SessionManagerService.manageSession(request, model);

//		  PaginationBean paginationBean = getPaginationInfo(request);

		  BusinessCertificate bc = super.getBusinessCertificate(request);
//		  PredicateBuilder predicateBuilder = new PredicateBuilder();
//		  predicateBuilder.addPredicate(getBusinessClientIdPredicate(request));

		  List<SetupEmployeeMaster> empList = this.genericService.loadAllObjectsWithSingleCondition(SetupEmployeeMaster.class, getBusinessClientIdPredicate(request),null);

//		  int wNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, SetupEmployeeMaster.class);

//		    PaginatedBean wPHDB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

		  DataTableBean wPHDB = new DataTableBean(empList);

		    wPHDB.setAddWarningIssued(true);
		    Navigator.getInstance(getSessionId(request)).setFromForm("redirect:viewTreatedNameConflicts.do");
		    wPHDB.setDisplayTitle(""+bc.getStaffTypeName()+" With Name Conflict");
//		  if(IppmsUtils.isNotNullAndGreaterThanZero(wNoOfElements)){
//		  	wPHDB.setShowLink(true);
//		  }
		  if (empList != null && empList.size() > 0)
			  wPHDB.setShowLink(true);
		  return makeAndReturnView(model,bc,wPHDB,null);
		  }
	
	   
	  @RequestMapping(method={RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
			  @ModelAttribute("miniBean") DataTableBean pLPB, BindingResult result, SessionStatus
			  status, Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);

	   
	    if(isButtonTypeClick(request,REQUEST_PARAM_CANCEL)){
	    	if(pLPB.isAddWarningIssued())
	    		return "redirect:auditPageHomeForm.do";
	    	else
	    		return REDIRECT_TO_DASHBOARD;
	    }
	     
    
       Navigator.getInstance(getSessionId(request)).setFromForm("redirect:viewTreatedNameConflicts.do");
       return "redirect:viewTreatedNameConflicts.do";
	    
	  }

	private String makeAndReturnView(Model model,BusinessCertificate bc,DataTableBean dtb, BindingResult result){

		model.addAttribute("displayList", dtb.getObjectList());
		model.addAttribute("roleBean", bc);
		if(result != null)
			model.addAttribute("status", result);
		model.addAttribute("miniBean", dtb);

		return this.VIEW;
	}



}
