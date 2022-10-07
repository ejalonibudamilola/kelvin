package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.StoredProcedureService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.allowance.CreateSpecialAllowanceTypeValidator;
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
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping({"/editSpecAllowType.do"})
@SessionAttributes(types={SpecialAllowanceType.class})
public class EditSpecialAllowanceTypeController extends BaseController
{

  private static final String VIEW_NAME = "allowance/editSpecialAllowanceTypeForm";

  private final StoredProcedureService storedProcedureService;
  private final CreateSpecialAllowanceTypeValidator createSpecialAllowanceTypeValidator;

  @ModelAttribute("payTypeList")
  public Collection<PayTypes> getPayTypesList() {
    return genericService.loadAllObjectsWithSingleCondition
            (PayTypes.class, CustomPredicate.procurePredicate("selectableInd", 0), "name");
  }


  @Autowired
  public EditSpecialAllowanceTypeController(StoredProcedureService storedProcedureService, CreateSpecialAllowanceTypeValidator createSpecialAllowanceTypeValidator) {
    this.storedProcedureService = storedProcedureService;
    this.createSpecialAllowanceTypeValidator = createSpecialAllowanceTypeValidator;
  }

  @RequestMapping(method={RequestMethod.GET}, params={"stid"})
  public String setupForm(@RequestParam("stid") Long pStid, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
	     
	  BusinessCertificate bc = this.getBusinessCertificate(request);


    SpecialAllowanceType wP = this.genericService.loadObjectUsingRestriction(SpecialAllowanceType.class, Arrays.asList(
            CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), CustomPredicate.procurePredicate("id",pStid)));

    if (wP.isNewEntity()) {
      return "redirect:createSpecAllowType.do";
    }
    wP.setOldName(wP.getName());
    wP.setPayTypeId(wP.getPayTypes().getId());
    wP.setOldPayTypeId(wP.getPayTypeId());
    model.addAttribute("specAllowTypeBean", wP);
    model.addAttribute("roleBean", bc);
    return VIEW_NAME;
  }

  @RequestMapping(method={RequestMethod.GET}, params={"stid","s"})
  public String setupForm(@RequestParam("stid") Long pStid,@RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
	     
	  BusinessCertificate bc = this.getBusinessCertificate(request);

    SpecialAllowanceType wP = this.genericService.loadObjectUsingRestriction(SpecialAllowanceType.class, Arrays.asList(
            CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), CustomPredicate.procurePredicate("id",pStid)));

    if (wP.isNewEntity()) {
      return "redirect:createSpecAllowType.do";
    }
    wP.setPayTypeId(wP.getPayTypes().getId());
    String actionCompleted = "Special Allowance Type "+wP.getName()+" Updated Successfully.";
    model.addAttribute("specAllowTypeBean", wP);
    model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
    model.addAttribute("saved", true);
    model.addAttribute("roleBean", bc);
    return VIEW_NAME;
  }

  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
		  @ModelAttribute("specAllowTypeBean") SpecialAllowanceType pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);
	     
	  BusinessCertificate bc = this.getBusinessCertificate(request);

    

    if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
    {
      return "redirect:viewSpecAllowTypes.do";
    }

    createSpecialAllowanceTypeValidator.validate(pEHB, result, bc);
    if (result.hasErrors())
    {
      model.addAttribute(DISPLAY_ERRORS, BLOCK);
      model.addAttribute("status", result);
      model.addAttribute("specAllowTypeBean", pEHB);
      model.addAttribute("roleBean", bc);
      return VIEW_NAME;
    }
    boolean updateInfoTable = false;
    if(!pEHB.getPayTypeId().equals(pEHB.getOldPayTypeId()))
      updateInfoTable = true;
    pEHB.setPayTypes(new PayTypes(pEHB.getPayTypeId()));
    pEHB.setLastModBy(new User(bc.getLoginId()));
    pEHB.setLastModTs(Timestamp.from(Instant.now()));

    this.genericService.saveObject(pEHB);
    if(updateInfoTable){
      List<AbstractSpecialAllowanceEntity> abstractSpecialAllowanceEntityList = (List<AbstractSpecialAllowanceEntity>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getSpecialAllowanceInfoClass(bc)
      ,Arrays.asList(CustomPredicate.procurePredicate("specialAllowanceType.id", pEHB.getId()), getBusinessClientIdPredicate(request)),null);
      for(AbstractSpecialAllowanceEntity a : abstractSpecialAllowanceEntityList){
         a.setPayTypes(pEHB.getPayTypes());
         a.setLastModBy(pEHB.getLastModBy());
         a.setLastModTs(pEHB.getLastModTs());
         this.genericService.storeObject(a);
      }


    }
      // storedProcedureService.callStoredProcedure(IConstants.UPD_SPEC_ALLOW_PAY_TYPES, pEHB.getBusinessClientId(),pEHB.getId(),pEHB.getPayTypes().getId(),bc.getLoginId());

    return "redirect:editSpecAllowType.do?stid="+pEHB.getId()+"&s=1";
  }
}