package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.nextofkin.domain.RelationshipType;
import com.osm.gnl.ippms.ogsg.validators.configcontrol.CreateNewRelationTypeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;

@Controller
@RequestMapping({"/editRelationType.do"})
@SessionAttributes(types={RelationshipType.class})
public class EditRelationshipTypeController extends BaseController {

	  private static final String VIEW_NAME = "configcontrol/editRelationTypeForm";

	  @Autowired
	  CreateNewRelationTypeValidator createNewRelationTypeValidator;

	  @RequestMapping(method={RequestMethod.GET}, params={"rtid"})
	  public String setupForm(@RequestParam("rtid") Long pRelTypeId, Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);
		     
		  BusinessCertificate bc = this.getBusinessCertificate(request);

	    RelationshipType wP = this.genericService.loadObjectWithSingleCondition(RelationshipType.class,
				CustomPredicate.procurePredicate("id", pRelTypeId));

	    if (wP.isNewEntity()) {
	      return "redirect:addNewRelationshipType.do";
	    }
	    wP.setEditMode(true);
	    model.addAttribute("relTypeBean", wP);
	    model.addAttribute("roleBean", bc);
	    return VIEW_NAME;
	  }

	  @RequestMapping(method={RequestMethod.GET}, params={"rtid","s"})
	  public String setupForm(@RequestParam("rtid") Long pRelTypeId, 
			  @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);
		     
		  BusinessCertificate bc = this.getBusinessCertificate(request);

	    RelationshipType wP = this.genericService.loadObjectWithSingleCondition(RelationshipType.class,
				CustomPredicate.procurePredicate("id", pRelTypeId));

	    model.addAttribute(IConstants.SAVED_MSG, "Relationship Type "+wP.getName()+" updated successfully.");
	    model.addAttribute("relTypeBean", wP);
	    model.addAttribute("saved", true);
	    model.addAttribute("roleBean", bc);
	    return VIEW_NAME;
	  }

	  @RequestMapping(method={RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, 
			  @ModelAttribute("relTypeBean") RelationshipType pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
	  {
		  SessionManagerService.manageSession(request, model);
		     
		  BusinessCertificate bc = this.getBusinessCertificate(request);

	     
	    if ( isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
	    {
	      return "redirect:viewRelationshipTypes.do";
	    }

	    createNewRelationTypeValidator.validateForEdit(pEHB, result);
	    if (result.hasErrors())
	    {
	      //((RelationshipType)result.getTarget()).setDisplayErrors("block");
	      model.addAttribute(DISPLAY_ERRORS, BLOCK);
	      model.addAttribute("status", result);
	      model.addAttribute("relTypeBean", pEHB);
	      model.addAttribute("roleBean", bc);
	      return VIEW_NAME;
	    }

	    pEHB.setLastModBy(new User(bc.getLoginId()));
	    pEHB.setLastModTs(Timestamp.from(Instant.now()));
	    this.genericService.saveObject(pEHB);

	    return "redirect:editRelationType.do?rtid="+pEHB.getId()+"&s=1";
	  }

}
