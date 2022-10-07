package com.osm.gnl.ippms.ogsg.controllers.qualification;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.qualification.EducationSchoolType;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import com.osm.gnl.ippms.ogsg.validators.qualification.EduSchoolTypeValidator;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;




@Controller
@RequestMapping({"/addNewEduSchoolType.do"})
@SessionAttributes(types={EducationSchoolType.class})

public class CreateEducationSchoolTypeController extends BaseController {

	  @Autowired 
	  private EduSchoolTypeValidator validator;
	  
	   private final String VIEW = "qual/createEditEduSchoolTypeForm";
	   
	    
	   @ModelAttribute("higherList")
	   protected List<NamedEntityBean> getHigherInstituteIndList(){
		   List<NamedEntityBean> wList = new ArrayList<NamedEntityBean>();
		   NamedEntityBean n = new NamedEntityBean();
	 	    n.setReportType(0);
	 	    n.setName("Primary");
	 	    wList.add(n);
	 	    n = new NamedEntityBean();
	 	    n.setReportType(1);
	 	    n.setName("Secondary");
	 	     wList.add(n);
	 	    n = new NamedEntityBean();
	 	    n.setReportType(2);
	 	    n.setName("Tertiary");
	 	     wList.add(n);
	 	    Comparator<NamedEntityBean> wComp = Comparator.comparing(NamedEntityBean::getReportType);
	 	    Collections.sort(wList,wComp);
	 	   return wList;
	 	   
	   }
	  public CreateEducationSchoolTypeController( )
	  {
	     
	  }

	  @RequestMapping(method={RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
		  SessionManagerService.manageSession(request, model);
		     
		  BusinessCertificate bc = this.getBusinessCertificate(request);


		  EducationSchoolType wEDT = new EducationSchoolType();
		  wEDT.setHigherInstitutionInd(-1);
		  wEDT.setEducationSchoolTypeList(this.genericService.loadControlEntity(EducationSchoolType.class));
		  model.addAttribute("displayTitle", "Create New Education Qualification School Type");
	    model.addAttribute("miniBean", wEDT);
	    model.addAttribute("roleBean", bc);
	    return VIEW;
	  }

	  
	@RequestMapping(method={RequestMethod.GET},params={"bid"})
	  public String setupForm(@RequestParam("bid") Long pEduId,Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);
		     
		  BusinessCertificate bc = this.getBusinessCertificate(request);

		  EducationSchoolType wEDT = this.genericService.loadObjectWithSingleCondition(EducationSchoolType.class,
				  CustomPredicate.procurePredicate("id", pEduId));
		  wEDT.setEditMode(true);
		 
		  wEDT.setEducationSchoolTypeList(this.genericService.loadControlEntity(EducationSchoolType.class));
        
		model.addAttribute("displayTitle", "Edit Education Qualification School Type");
	    model.addAttribute("miniBean", wEDT);
	    model.addAttribute("roleBean", bc);
	    return VIEW;
	  }

	
	@RequestMapping(method={RequestMethod.GET},params={"bid","s"})
	  public String setupForm(@RequestParam("bid") Long pObjectId,@RequestParam("s") int pSaved,Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);
		     
		  BusinessCertificate bc = this.getBusinessCertificate(request);


		  EducationSchoolType wEDT = this.genericService.loadObjectWithSingleCondition(EducationSchoolType.class,
				  CustomPredicate.procurePredicate("id", pObjectId ));
	    
	     String wMessage = "";
	     
	     if(pSaved == 1) {
	    	 wMessage = "Education School Type "+wEDT.getName()+" edited successfully.";
	     }else {
	    	 wMessage = "Education School Type "+wEDT.getName()+" created successfully.";
	     }

	    model.addAttribute(IConstants.SAVED_MSG, wMessage);
	    wEDT = new EducationSchoolType();
	    wEDT.setEducationSchoolTypeList(this.genericService.loadControlEntity(EducationSchoolType.class));
	    model.addAttribute("displayTitle", "Create/Edit Education Qualification School Type");
	    model.addAttribute("saved", true);
	    model.addAttribute("miniBean", wEDT);
	    model.addAttribute("roleBean", bc);
	    return VIEW;
	  }
	  @RequestMapping(method={RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("miniBean") EducationSchoolType pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
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

	    return "redirect:addNewEduSchoolType.do?bid="+pEHB.getId()+"&s="+wEditType;
	  }

}
