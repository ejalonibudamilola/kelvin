package com.osm.gnl.ippms.ogsg.controllers.qualification;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.qualification.EducationSchool;
import com.osm.gnl.ippms.ogsg.domain.qualification.EducationSchoolType;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.location.domain.State;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import com.osm.gnl.ippms.ogsg.validators.qualification.EduSchoolValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;


@Controller
@RequestMapping({"/addNewEduSchool.do"})
@SessionAttributes(types={EducationSchool.class})

public class CreateEditEducationSchoolController extends BaseController {
	
	@Autowired
	private EduSchoolValidator validator;

	private final String VIEW = "qual/createEditEducationSchoolForm";

 
 
	@ModelAttribute("schoolTypeList")
	protected List<EducationSchoolType> getSchooTypes() {

		List<EducationSchoolType> wList = this.genericService.loadAllObjectsWithoutRestrictions(EducationSchoolType.class, "name");
		return wList;

	}
	 
	 @ModelAttribute("stateList")
	   public Collection<State> populateStates() {
	     return this.genericService.loadControlEntity(State.class);
	   }
	 @ModelAttribute("intList")
	   protected List<NamedEntityBean> getHigherInstituteIndList(){
		   List<NamedEntityBean> wList = new ArrayList<NamedEntityBean>();
		   NamedEntityBean n = new NamedEntityBean();
	 	    n.setReportType(0);
	 	    n.setName("Local");
	 	    wList.add(n);
	 	    n = new NamedEntityBean();
	 	    n.setReportType(1);
	 	    n.setName("International");
	 	     wList.add(n);
	 	    Comparator<NamedEntityBean> wComp = Comparator.comparing(NamedEntityBean::getName);
	 	    Collections.sort(wList,wComp);
	 	   return wList;
	 	   
	   }
	public CreateEditEducationSchoolController() {
	}

	@RequestMapping(method = {RequestMethod.GET})
	public String setupForm(Model model, HttpServletRequest request)
			throws HttpSessionRequiredException, EpmAuthenticationException {
		SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

		EducationSchool wEDT = new EducationSchool();
		wEDT.setEducationSchoolType(new EducationSchoolType());
		wEDT.setStateInfo(new State());
        wEDT.setLocalInd(-1);
		model.addAttribute("displayList", this.makePaginatedList(request, false));
		model.addAttribute("displayTitle", "Create New Educational School");
		model.addAttribute("miniBean", wEDT);
		model.addAttribute("roleBean", bc);
		return VIEW;
	}

	@RequestMapping(method = {RequestMethod.GET}, params = { "bid" })
	public String setupForm(@RequestParam("bid") Long pEduId, Model model, HttpServletRequest request)
			throws Exception {
		SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

		EducationSchool wEDT = this.genericService.loadObjectById(EducationSchool.class,pEduId);
		wEDT.setEditMode(true);
		
		model.addAttribute("displayList", this.makePaginatedList(request, false)); 
		model.addAttribute("displayTitle", "Edit Educational School");
		model.addAttribute("miniBean", wEDT);
		model.addAttribute("roleBean", bc);
		return VIEW;
	}

 	

	@RequestMapping(method = {RequestMethod.GET}, params = { "bid", "s" })
	public String setupForm(@RequestParam("bid") Long pObjectId, @RequestParam("s") int pSaved, Model model,
			HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

		EducationSchool wEDT = this.genericService.loadObjectWithSingleCondition(EducationSchool.class,  CustomPredicate.procurePredicate("id", pObjectId));

		String wMessage = "";

		if (pSaved == 1) {
			wMessage = "Educational School " + wEDT.getName() + " edited successfully.";
		} else {
			wMessage = "Educational School " + wEDT.getName() + " created successfully.";
		}

		model.addAttribute(IConstants.SAVED_MSG, wMessage);
		wEDT = new EducationSchool();
		wEDT.setEducationSchoolType(new EducationSchoolType());
		wEDT.setStateInfo(new State());
        wEDT.setLocalInd(-1);
		model.addAttribute("displayList", this.makePaginatedList(request, true));
		model.addAttribute("displayTitle", "Create/Edit Educational School");
		model.addAttribute("saved", true);
		model.addAttribute("miniBean", wEDT);
		model.addAttribute("roleBean", bc);
		return VIEW;
	}

	@RequestMapping(method = {RequestMethod.POST })
	public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
			@ModelAttribute("miniBean") EducationSchool pEHB, BindingResult result, SessionStatus status,
			Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

		if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
			return CONFIG_HOME_URL;
		}

		validator.validate(pEHB, result);
		if (result.hasErrors()) {
			model.addAttribute(DISPLAY_ERRORS, BLOCK);
			model.addAttribute("status", result);
			model.addAttribute("miniBean", pEHB);
			model.addAttribute("roleBean", bc);
			return VIEW;
		}

		pEHB.setLastModBy(new User(bc.getLoginId()));
		pEHB.setLastModTs(Timestamp.from(Instant.now()));
		int wEditType = 1;
		if (!pEHB.isEditMode()) {
			wEditType = 2;
			pEHB.setCreatedBy(new User(bc.getLoginId()));
		}

		this.genericService.saveObject(pEHB);

		return "redirect:addNewEduSchool.do?bid=" + pEHB.getId() + "&s=" + wEditType;
	}

	private PaginatedPaycheckGarnDedBeanHolder makePaginatedList(HttpServletRequest request, boolean pSaved) {
		 BusinessCertificate bc = this.getBusinessCertificate(request);
		 PaginationBean paginationBean = this.getPaginationInfo(request);
		
		List<EducationSchool> wAllList;
		List<EducationSchool> wRetList = null;
		
		//Do we do the list upside down now?
		 
		if(pSaved) {
			wAllList = this.genericService.loadAllObjectsWithoutRestrictions(EducationSchool.class, "id");
		}else {
			wAllList = this.genericService.loadAllObjectsWithoutRestrictions(EducationSchool.class, "name");
		}
		 
			
		if(wAllList.size() > 10) {

					wRetList = (List<EducationSchool>) PayrollUtils.paginateList(paginationBean.getPageNumber(), 10, wAllList);
			 
		}else {
			 wRetList = wAllList;
		}

		PaginatedPaycheckGarnDedBeanHolder wPBO = new PaginatedPaycheckGarnDedBeanHolder(wRetList, paginationBean.getPageNumber(), 10, wAllList.size(), paginationBean.getSortOrder(), paginationBean.getSortCriterion());
		
		return wPBO;
	}
}
