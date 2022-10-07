package com.osm.gnl.ippms.ogsg.controllers.qualification;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.qualification.EducationQualificationType;
import com.osm.gnl.ippms.ogsg.domain.qualification.EducationSchoolType;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.validators.qualification.EduQualTypeValidator;
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
import java.util.List;

@Controller
@RequestMapping({"/addEditQualType.do"})
@SessionAttributes(types={EducationQualificationType.class})

public class CreateEditEducationalQualTypeController extends BaseController {

	  @Autowired 
	  private EduQualTypeValidator validator;
	  
	   private final String VIEW = "qual/createEditEduQualTypeForm";
	   
	    
	   @ModelAttribute("schoolTypeList")
		protected List<EducationSchoolType> getSchooTypes() {

			List<EducationSchoolType> wList = this.genericService.loadAllObjectsWithoutRestrictions(EducationSchoolType.class, "name");

			return wList;

		}

	  @RequestMapping(method={RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
		  SessionManagerService.manageSession(request, model);
		     
		  BusinessCertificate bc = this.getBusinessCertificate(request);


		  EducationQualificationType wEDT = new EducationQualificationType();
		  wEDT.setEducationSchoolType(new EducationSchoolType());
		  model.addAttribute("displayList", this.makePaginatedList(request, false));
		  model.addAttribute("displayTitle", "Create New Education Qualification Type");
	    model.addAttribute("miniBean", wEDT);
	    model.addAttribute("roleBean", bc);
	    return VIEW;
	  }

	  
	@RequestMapping(method={RequestMethod.GET},params={"bid"})
	  public String setupForm(@RequestParam("bid") Long pEduId,Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);
		     
		  BusinessCertificate bc = this.getBusinessCertificate(request);

		  EducationQualificationType wEDT = this.genericService.loadObjectWithSingleCondition(EducationQualificationType.class,
				  CustomPredicate.procurePredicate("id", pEduId));
		  wEDT.setEditMode(true);
		 
      
		  model.addAttribute("displayList", this.makePaginatedList(request, false));  
		model.addAttribute("displayTitle", "Edit Education Qualification Type");
	    model.addAttribute("miniBean", wEDT);
	    model.addAttribute("roleBean", bc);
	    return VIEW;
	  }

	
	@RequestMapping(method={RequestMethod.GET},params={"bid","s"})
	  public String setupForm(@RequestParam("bid") Long pObjectId,@RequestParam("s") int pSaved,Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);
		     
		  BusinessCertificate bc = this.getBusinessCertificate(request);


		  EducationQualificationType wEDT = this.genericService.loadObjectWithSingleCondition(EducationQualificationType.class,
				  CustomPredicate.procurePredicate("id", pObjectId));
	    
	     String wMessage = "";
	     
	     if(pSaved == 1) {
	    	 wMessage = "Education Qualification Type "+wEDT.getName()+" edited successfully.";
	     }else {
	    	 wMessage = "Education Qualification Type "+wEDT.getName()+" created successfully.";
	     }

	    model.addAttribute(IConstants.SAVED_MSG, wMessage);
	    wEDT = new EducationQualificationType();
	    wEDT.setEducationSchoolType(new EducationSchoolType());
	    model.addAttribute("displayList", this.makePaginatedList(request, true));
	    model.addAttribute("displayTitle", "Create/Edit Education Qualification School Type");
	    model.addAttribute("saved", true);
	    model.addAttribute("miniBean", wEDT);
	    model.addAttribute("roleBean", bc);
	    return VIEW;
	  }
	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("miniBean") EducationQualificationType pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
	  {
		  SessionManagerService.manageSession(request, model);
		     
		  BusinessCertificate bc = this.getBusinessCertificate(request);

	   

	    if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
	    {
	      return CONFIG_HOME_URL;
	    }

	    validator.validate(pEHB, result);
	    if (result.hasErrors())
	    {
	      model.addAttribute(DISPLAY_ERRORS, BLOCK);
	      model.addAttribute("status", result);
	      model.addAttribute("miniBean", pEHB);
	      model.addAttribute("roleBean", bc);
	      return VIEW;
	    }
      
	    pEHB.setLastModBy(new User(bc.getLoginId()));
	    pEHB.setLastModTs(Timestamp.from(Instant.now()));
	    int wEditType = 1;
	    if(!pEHB.isEditMode()) {
	    	wEditType = 2;
	    	pEHB.setCreatedBy(new User(bc.getLoginId()));
	    }
	   
	    this.genericService.saveObject(pEHB);

	    return "redirect:addEditQualType.do?bid="+pEHB.getId()+"&s="+wEditType;
	  }

	  private PaginatedPaycheckGarnDedBeanHolder makePaginatedList(HttpServletRequest request, boolean pSaved) {
			 
			PaginationBean paginationBean = this.getPaginationInfo(request);
			
			List<EducationQualificationType> wAllList;
			List<EducationQualificationType> wRetList = null;
			
			//Do we do the list upside down now?
			 
			if(pSaved) {
				wAllList = this.genericService.loadAllObjectsWithoutRestrictions(
						EducationQualificationType.class, "id");
			}else {
				wAllList = this.genericService.loadAllObjectsWithoutRestrictions(
						EducationQualificationType.class, "name");
			}
			 
				
			if(wAllList.size() > pageLength) {
				
				wRetList = (List<EducationQualificationType>) PayrollUtils.paginateList(paginationBean.getPageNumber(), pageLength, wAllList);
				 
			}else {
				 wRetList = wAllList;
			}

			PaginatedPaycheckGarnDedBeanHolder wPBO = new PaginatedPaycheckGarnDedBeanHolder(wRetList, paginationBean.getPageNumber(), pageLength, wAllList.size(), paginationBean.getSortOrder(), paginationBean.getSortCriterion());
			
			return wPBO;
		}
}
