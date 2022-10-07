package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
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
import java.util.List;

@Controller
@RequestMapping({"/createSchool.do"})
@SessionAttributes(types={SchoolInfo.class})
public class CreateSchoolFormController extends BaseController{

  @Autowired
  private SchoolValidator validator;
	 
	
	@ModelAttribute("mdaInfoList")
	  protected List<MdaInfo> loadAllMdas(HttpServletRequest request){
      BusinessCertificate bc = super.getBusinessCertificate(request);
      return this.genericService.loadAllObjectsUsingRestrictions(MdaInfo.class,
                  Arrays.asList(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),
                          CustomPredicate.procurePredicate("schoolIndicator",IConstants.ON)),"name");
    }

@RequestMapping(method={RequestMethod.GET})
  public String setupSchoolForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	SessionManagerService.manageSession(request, model);
    BusinessCertificate bc = super.getBusinessCertificate(request);

    SchoolInfo wHCDD = new SchoolInfo();
    
    wHCDD.setBusinessClientInstId(bc.getBusinessClientInstId());
    model.addAttribute("school", wHCDD);
    model.addAttribute("roleBean", bc);
    return "createSchoolForm";
  }

@RequestMapping(method={RequestMethod.GET}, params = {"sid", "s"})
public String setupForm(@RequestParam("sid") Long sid, @RequestParam("s") String pSaved, Model model, HttpServletRequest request) throws Exception {
	SessionManagerService.manageSession(request, model);
    BusinessCertificate bc = super.getBusinessCertificate(request);
  SchoolInfo wHCDD =this.genericService.loadObjectUsingRestriction(SchoolInfo.class,
          Arrays.asList(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),
                  CustomPredicate.procurePredicate("id",sid)));
   
  wHCDD.setBusinessClientInstId(bc.getBusinessClientInstId());
 
  String actionCompleted = wHCDD.getName() + " created successfully.";
  
  model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
  model.addAttribute("saved", Boolean.valueOf(true));
  model.addAttribute("school", wHCDD);
  model.addAttribute("roleBean", bc);
  return "createSchoolForm";
}

  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("school") SchoolInfo pHCDD,
		  BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);

    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
      return "redirect:determineDashBoard.do";
    }

    validator.validate(pHCDD, result);
    if (result.hasErrors()) {
      ((SchoolInfo)result.getTarget()).setParentObjectType("block");
      model.addAttribute("status", result);
      model.addAttribute("school", pHCDD);
      model.addAttribute("roleBean", bc);
      return "createSchoolForm";
    }
    
  //  pHCDD.setRuralAllowance(Double.parseDouble(PayrollHRUtils.removeCommas(pHCDD.getRuralAllowanceStr())));
    
    pHCDD.setLastModBy(new User(bc.getLoginId()));
    pHCDD.setLastModTs(Timestamp.from(Instant.now()));
    //We would need the Object IND of the Parent Object

    MdaInfo wMdaInfo =this.genericService.loadObjectUsingRestriction(MdaInfo.class,
            Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                    CustomPredicate.procurePredicate("id",pHCDD.getMdaInfo().getId())));
    pHCDD.setObjectInd(wMdaInfo.getMdaType().getMdaTypeCode());
    pHCDD.setCreatedBy(new User(bc.getLoginId()));
    pHCDD.setBusinessClientId(bc.getBusinessClientInstId());
    this.genericService.saveObject(pHCDD);

    return "redirect:createSchool.do?sid="+pHCDD.getId()+"&s=1";
  }
}