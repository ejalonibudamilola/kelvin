package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.validators.hr.SchoolValidator;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping({"/editSchool.do"})
@SessionAttributes(types={SchoolInfo.class})
public class EditSchoolFormController extends BaseController{
	
	private final SchoolValidator validator;
	private final String VIEW = "schools/createSchoolForm";
	@Autowired
	public EditSchoolFormController(SchoolValidator validator) {
		this.validator = validator;
	}

	@ModelAttribute("schoolList")
	  public Collection<SchoolInfo> getSchoolList(HttpServletRequest request){
		  BusinessCertificate bc = super.getBusinessCertificate(request);
		  List<SchoolInfo> wRetList = this.genericService.loadAllObjectsWithSingleCondition(SchoolInfo.class,
				  CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),"name");
		  Collections.sort(wRetList);
		  return wRetList;
	  }
	  @RequestMapping(method={RequestMethod.GET})
	  public String setupForm(Model pModel, HttpServletRequest pRequest) throws HttpSessionRequiredException, EpmAuthenticationException
	  {
		  BusinessCertificate bc = super.getBusinessCertificate(pRequest);
		  SessionManagerService.manageSession(pRequest, pModel);

		    SchoolInfo wHCDD = new SchoolInfo();
		    
		    pModel.addAttribute("school", wHCDD);
		    pModel.addAttribute("edit", Boolean.valueOf(true));
		  	pModel.addAttribute("roleBean", bc);
		    return VIEW;
	  }
	  
	  @RequestMapping(method={RequestMethod.GET}, params = {"sid", "s"})
	  public String setupForm(@RequestParam("sid") Long sid, @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

	    SchoolInfo wHCDD = this.genericService.loadObjectUsingRestriction(SchoolInfo.class,
				Arrays.asList(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),
						CustomPredicate.procurePredicate("id",sid)));
	    int pOid = wHCDD.getObjectInd();
	 //   int pPid = wHCDD.getParentInstId();
	    
	    
	    wHCDD.setObjectInd(pOid);
	    wHCDD.setBusinessClientInstId(bc.getBusinessClientInstId());

	    String actionCompleted = wHCDD.getName() + " edited successfully.";
	    
	    model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
	    model.addAttribute("message", actionCompleted);
	    model.addAttribute("saved", Boolean.valueOf(true));
	    model.addAttribute("school", wHCDD);
	    model.addAttribute("edit", Boolean.valueOf(true));
	    model.addAttribute("roleBean", bc);
	    return VIEW;
	  }
	  
	  @RequestMapping(method={RequestMethod.GET}, params = {"sid"})
	  public String setupForm(@RequestParam("sid") Long sid, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
		  SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

	    SchoolInfo wHCDD = this.genericService.loadObjectUsingRestriction(SchoolInfo.class,
				Arrays.asList(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),
						CustomPredicate.procurePredicate("id",sid)));

	    wHCDD.setBusinessClientInstId(bc.getBusinessClientInstId());
	    wHCDD.setEditMode(Boolean.valueOf(true));
	    
	  
	    model.addAttribute("school", wHCDD);
	    model.addAttribute("edit", Boolean.valueOf(true));
	    model.addAttribute("roleBean", bc);
	    return VIEW;
	  }
	  
	  @RequestMapping(method={RequestMethod.POST})
	    public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, 
	    		@RequestParam(value="_update", required=false) String update,
	    		@ModelAttribute("school") SchoolInfo pHCDD, BindingResult result, SessionStatus status, 
	    		Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);
  

	      if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
	        return "redirect:lgeaSchoolsFunctionalities.do";
	      }
	      
	      if (isButtonTypeClick(request, REQUEST_PARAM_UPD))
	      {
		      return "redirect:editSchool.do?sid="+pHCDD.getId();
	      }
		        
	      validator.validate(pHCDD, result);
	      if (result.hasErrors()) {
	      	model.addAttribute(DISPLAY_ERRORS, BLOCK);
	        model.addAttribute("status", result);
	        model.addAttribute("school", pHCDD);
	        model.addAttribute("edit", Boolean.valueOf(true));
	        model.addAttribute("roleBean", bc);
	        return VIEW;
	      }
	      pHCDD.setLastModBy(new User(bc.getLoginId()));
	      pHCDD.setLastModTs(Timestamp.from(Instant.now()));

	      return "redirect:editSchool.do?sid="+this.genericService.storeObject(pHCDD)+"&s=1";
	    }
}
