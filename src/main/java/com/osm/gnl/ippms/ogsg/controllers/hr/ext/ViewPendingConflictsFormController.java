package com.osm.gnl.ippms.ogsg.controllers.hr.ext;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.SetupEmployeeMaster;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.DataTableBean;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @author Damilola Ejalonibu
 */

@Controller
@RequestMapping({"/viewPendingConflicts.do"})
@SessionAttributes({"miniBean"})
public class ViewPendingConflictsFormController extends BaseController {


	  private final int pageLength = 20;
	  private final String VIEW = "conflict/viewPendingConflictsForm";

	public ViewPendingConflictsFormController() {}

	  @RequestMapping(method={RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request)
	    throws Exception
	  {
		  SessionManagerService.manageSession(request, model);

		  BusinessCertificate bc = super.getBusinessCertificate(request);

	    List<SetupEmployeeMaster> empList2 = this.genericService.loadAllObjectsUsingRestrictions(SetupEmployeeMaster.class,
				Arrays.asList(CustomPredicate.procurePredicate("approvedDate", null, Operation.IS_NULL),
						CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())),null);

	    DataTableBean wPHDB = new DataTableBean(empList2);
	    Navigator.getInstance(getSessionId(request)).setFromForm("redirect:viewPendingConflicts.do");
	    wPHDB.setDisplayTitle("Pending "+ bc.getStaffTypeName()+" With Name Conflict");
	    model.addAttribute("miniBean", wPHDB);
	    addRoleBeanToModel(model, request);
	    return VIEW;
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
	     
      
         Navigator.getInstance(getSessionId(request)).setFromForm("redirect:viewPendingConflicts.do");
         return "redirect:viewPendingConflicts.do";
	    
	  }


}
