package com.osm.gnl.ippms.ogsg.controllers.leave;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.leavebonus.domain.LeaveBonusMasterBean;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;


@Controller
@RequestMapping({"/viewPendingLeaveBonus.do"})
@SessionAttributes(types={PaginatedBean.class})
public class ViewPendingLeaveBonusesFormController extends BaseController {

	  
	  private final int pageLength = 20;
	  private final String VIEW = "leave/viewPendingLeaveBonusesForm";


	  
	  @RequestMapping(method={RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request)
	    throws Exception
	  {
		  SessionManagerService.manageSession(request, model);
 
	    PaginationBean paginationBean = this.getPaginationInfo(request);

	    List<LeaveBonusMasterBean> empList = this.genericService.loadPaginatedObjects(LeaveBonusMasterBean.class,
				new ArrayList<>(), (paginationBean.getPageNumber() - 1) * this.pageLength,
	    		this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

		/*
		 * for(LeaveBonusMasterBean t : empList){
		 * t.setCreatedBy(this.payrollService.getUserNamesByUserName(t.getLastModBy()));
		 * }
		 */
		  PredicateBuilder predicateBuilder = new PredicateBuilder();
		  predicateBuilder.addPredicate(CustomPredicate.procurePredicate("approvedInd", IConstants.OFF));
		  predicateBuilder.addPredicate(CustomPredicate.procurePredicate("approvedDate", null));
		int wNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, LeaveBonusMasterBean.class);

	    PaginatedBean wPHDB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements,
				paginationBean.getSortCriterion(), paginationBean.getSortOrder());

	    Navigator.getInstance(getSessionId(request)).setFromForm("redirect:viewPendingLeaveBonus.do");
	    model.addAttribute("miniBean", wPHDB);
	    addRoleBeanToModel(model, request);

	    return VIEW;
	  }
	/*@RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET},params={"eid","ln","uid"})
	  public String setupForm(@RequestParam("eid") Integer pEmpId,
			  @RequestParam("ln") String pLastName,
			  @RequestParam("uid") Integer pUid,Model model, HttpServletRequest request)
	    throws Exception
	  {
	      SessionManagerService.manageSession(request);
		Object userId = super.getSessionId(request);
	    Object userId = session.getAttribute("user");
	    if (userId == null) {
	      return "redirect:sessionExpiredForm.do";
	    }
	    BusinessCertificate bc = this.payrollService.getBusinessCertificate(userId);

	    if (bc.isNew())
	    {
	      return "redirect:sessionExpiredForm.do";
	    }if (!bc.getSessionId().equals(userId))
	    {
	      return "redirect:sessionExpiredForm.do";
	    }

	    
	    int pageNumber = ServletRequestUtils.getIntParameter(request, "page", 1);
	    String sortOrder = ServletRequestUtils.getStringParameter(request, "dir", "asc");
	    String sortCriterion = ServletRequestUtils.getStringParameter(request, "sort", null);
	    
	    if(pEmpId.equals(new Integer(0)))
	    	pEmpId = null;
	    
	    if(pUid.equals(new Integer(0)))
	    	pUid = null;

	    //Now Get the list of Id's with the Last Name...
	    List<Integer> wEmpIds =  null;
	    if(!treatNull(pLastName).equals(EMPTY_STR)){
	    	wEmpIds = this.payrollService.loadEmployeeInstIdsByStringValue(pLastName );
		    if(wEmpIds.isEmpty() )
		    	wEmpIds =  null;
	    }
	    	
	    
	    List<TransferApproval> empList = this.payrollService.loadPendingTransferApprovals((pageNumber - 1) * this.pageLength, 
	    		this.pageLength, sortOrder, sortCriterion,pEmpId,wEmpIds,pUid);

	    for(TransferApproval t : empList){
	    	 
	    	t.setOldMda(t.getEmployee().getParentObjectName());
	    	 
	    	 t.setNewMda(PayrollUtils.getActualMdaClass(t.getMdaInstId(),t.getMdaTypeInd(),this.payrollService).getName());
	    }
	    int wNoOfElements = this.payrollService.getTotalNoOfActiveTransferApprovals(pEmpId,wEmpIds,pUid);

	    PaginatedBean wPHDB = new PaginatedBean(empList, pageNumber, this.pageLength, wNoOfElements, sortCriterion, sortOrder);
	    if(pUid != null && pUid > 0)
	       wPHDB.setId(pUid);
	    else
	    	wPHDB.setId(0);
	    if(!treatNull(pLastName).equals(EMPTY_STR)){
	    	wPHDB.setLastName(pLastName);
	    }
	    if(pEmpId != null && pEmpId > 0){
	    	wPHDB.setOgNumber((String) this.payrollService.loadStringByClassParameters("Employee e", "e.employeeId", "e.id", pEmpId, Integer.class));
	    }
	    model.addAttribute("miniBean", wPHDB);

	    return VIEW;
	  }*/
	  
	  @RequestMapping(method={RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
			  @RequestParam(value="_update", required=false) String pUpd,
			  @ModelAttribute("miniBean") PaginatedBean pLPB, BindingResult result, SessionStatus
			  status, Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);
 
	     
	    //if(cancel.equalsIgnoreCase(REQUEST_PARAM_CANCEL_VALUE)){
	    	return REDIRECT_TO_DASHBOARD;
	   // }
	     
     /* Integer empId = 0;
      String wLastName = "";
      Integer pUid = 0;
      if(!treatNull(pLPB.getOgNumber()).equals(EMPTY_STR)){
      	  if(!pLPB.getOgNumber().toUpperCase().startsWith("OG")){
      		  pLPB.setOgNumber("OG"+pLPB.getOgNumber());
      	  } 
      		  //Employee wEmp = (Employee) this.payrollService.loadObectByClassAndName(Employee.class, "employeeId", pLPB.getOgNumber().toUpperCase());
      	  empId  = (Integer) this.payrollService.loadStringByClassParameters("Employee e", "e.id", "e.employeeId",  pLPB.getOgNumber().toUpperCase(), String.class);
        	  
      	  if( empId == null || empId <= 0){
      			   
      			 
      		 
      			    result.rejectValue("", "InvalidValue", "No Employee found with OG Number "+pLPB.getOgNumber());
      		        ((PaginatedBean)result.getTarget()).setDisplayErrors("block");

      		        model.addAttribute("status", result);
      		        model.addAttribute("miniBean", pLPB);

      		        return VIEW; 
      		  }
      	     
        } 
        if(!treatNull(pLPB.getLastName()).equals(EMPTY_STR)){
      	  wLastName = pLPB.getLastName();	
         }
         if(pLPB.getId() > 0)
      	   pUid = pLPB.getId();
         
         Navigator.getInstance(userId).setFromForm("redirect:viewPendingTransfers.do?eid="+empId+"&ln="+wLastName+"&uid="+pUid);
         return "redirect:viewPendingTransfers.do?eid="+empId+"&ln="+wLastName+"&uid="+pUid;
	    */
	  }


}
