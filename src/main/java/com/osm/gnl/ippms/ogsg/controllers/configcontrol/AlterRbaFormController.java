package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RbaConfigBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.validators.configcontrol.CreateNewBankValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;

@Controller
@RequestMapping({"/alterRba.do"})
@SessionAttributes(types={RbaConfigBean.class})
public class AlterRbaFormController extends BaseController {


	  private final String VIEW_NAME = "configcontrol/alterRbaForm";

	   @Autowired
	   CreateNewBankValidator createNewBankValidator;

	   public AlterRbaFormController(){}

	  @RequestMapping(method={RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);
		  BusinessCertificate bc = this.getBusinessCertificate(request);


		  RbaConfigBean wP = this.genericService.loadObjectWithSingleCondition(RbaConfigBean.class, CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));
		  if(wP.isNewEntity())
		  	wP.setBusinessClientId(bc.getBusinessClientInstId());
		model.addAttribute("roleBean", bc);
	    model.addAttribute("rbaBean", wP);
	    return VIEW_NAME;
	  }

	  @RequestMapping(method={RequestMethod.GET}, params={"bid","s","ov"})
	  public String setupForm(@RequestParam("bid") Long pDtid,
			  @RequestParam("s") int pSaved,@RequestParam("ov") String pOldValue,Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);

		  BusinessCertificate bc = this.getBusinessCertificate(request);
		  

		  RbaConfigBean wP = this.genericService.loadObjectUsingRestriction(RbaConfigBean.class,
				  Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
						  CustomPredicate.procurePredicate("id", pDtid)));

		  wP.setName(pOldValue);
	     
	    model.addAttribute(IConstants.SAVED_MSG, "Redemption Bond Account changed from  "+pOldValue+" to "+wP.getRbaPercentageStr()+" successfully.");
	    model.addAttribute("saved", true);
		  model.addAttribute("roleBean", bc);
	    model.addAttribute("rbaBean", wP);
	    return VIEW_NAME;
	  }

	  @RequestMapping(method={RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, 
			  @ModelAttribute("rbaBean") RbaConfigBean pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
	  {
		  SessionManagerService.manageSession(request, model);
		     
		  BusinessCertificate bc = this.getBusinessCertificate(request);

	    

	    if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
	    {
	      return CONFIG_HOME_URL;
	    }

	    
	    createNewBankValidator.validateRba(pEHB, result, bc);
	    if (result.hasErrors())
	    {
	      model.addAttribute(DISPLAY_ERRORS, BLOCK);
	      model.addAttribute("status", result);
	      model.addAttribute("rbaBean", pEHB);
	      model.addAttribute("roleBean", bc);
	      return VIEW_NAME;
	    }
        
	    String oldValue = String.valueOf(pEHB.getRbaPercentage());
	    pEHB.setLastModBy(new User(bc.getLoginId()));
	    pEHB.setLastModTs(Timestamp.from(Instant.now()));
	    pEHB.setRbaPercentage(Double.parseDouble(PayrollHRUtils.removeCommas(pEHB.getName())));

	    this.genericService.saveObject(pEHB);

	    return "redirect:alterRba.do?bid="+pEHB.getId()+"&s=1&ov="+oldValue;
	  }

}
