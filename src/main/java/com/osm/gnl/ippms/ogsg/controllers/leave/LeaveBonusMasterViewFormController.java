package com.osm.gnl.ippms.ogsg.controllers.leave;

import com.osm.gnl.ippms.ogsg.abstractentities.NamedEntityLong;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.HRService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.leavebonus.domain.LeaveBonusMasterBean;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping({"/preLeaveBonusReportForm.do"})
@SessionAttributes(types={PaginatedBean.class})
public class LeaveBonusMasterViewFormController extends BaseController {

	 @Autowired
	 HRService hrService;

	  private final int pageLength = 20;
	  private final String VIEW = "leave/leaveBonusMasterByDateForm";
	   

	  
	  public LeaveBonusMasterViewFormController( ) {
	     
	  }
	  @ModelAttribute("yearList")
	  public List<NamedEntityLong> populateYearList(){
		  return this.hrService.makeYearListByOrmObject("LeaveBonusMasterBean");
	  }
	  
	  @RequestMapping(method={RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request) throws Exception{
		  SessionManagerService.manageSession(request, model);


		  PaginationBean paginationBean = getPaginationInfo(request);

	    List<LeaveBonusMasterBean> empList = this.hrService.loadLeaveBonusMasterBeansForDisplay((paginationBean.getPageNumber() - 1) * this.pageLength,
	    		this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(),0);


//	    int wNoOfElements = this.payrollService.getTotalNoOfLeaveBonusMasterBeanByYear(0);
		int wNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder()
				.addPredicate(CustomPredicate.procurePredicate("runYear", IConstants.OFF)),LeaveBonusMasterBean.class);
	    PaginatedBean wPHDB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

	    wPHDB.setRunYear(0);
	    wPHDB.setShowLink(wNoOfElements > 0);
	    addRoleBeanToModel(model, request);
	    model.addAttribute("miniBean", wPHDB);

	    return VIEW;
	  }
	  @RequestMapping(method={RequestMethod.GET}, params={"yid"})
	  public String setupForm(@RequestParam("yid") int pYearid, Model model, HttpServletRequest request)
	  throws Exception {
		  SessionManagerService.manageSession(request, model);

		  PaginationBean paginationBean = getPaginationInfo(request);

		    List<LeaveBonusMasterBean> empList = this.hrService.loadLeaveBonusMasterBeansForDisplay((paginationBean.getPageNumber() - 1) * this.pageLength,
		    		this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(),pYearid);


//		    int wNoOfElements = this.payrollService.getTotalNoOfLeaveBonusMasterBeanByYear(pYearid);
		  int wNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder()
				  .addPredicate(CustomPredicate.procurePredicate("runYear", pYearid)),LeaveBonusMasterBean.class);

		    PaginatedBean wPHDB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
		    wPHDB.setShowLink(wNoOfElements > 0);
		    wPHDB.setRunYear(pYearid);
		  	addRoleBeanToModel(model, request);
		    model.addAttribute("miniBean", wPHDB);

		    return VIEW;
		  }

	  @RequestMapping(method={RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
			  @RequestParam(value="_update", required=false) String pUpdate, 
			  @ModelAttribute("miniBean") PaginatedBean pHADB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
	  {
		  SessionManagerService.manageSession(request, model);
 
	    

	    if(isButtonTypeClick(request,REQUEST_PARAM_CANCEL))
	      return "redirect:reportsOverview.do";
	    
	     
	    
	    	return "redirect:preLeaveBonusReportForm.do?yid="+pHADB.getRunYear();
	    
	     
	  }

}
