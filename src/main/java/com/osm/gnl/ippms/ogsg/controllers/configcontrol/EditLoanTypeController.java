package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.validators.garnishment.CreateGarnishTypeValidator;
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
@RequestMapping({"/editGarnishType.do"})
@SessionAttributes(types={EmpGarnishmentType.class})
public class EditLoanTypeController extends BaseController
{
  
  @Autowired
  private CreateGarnishTypeValidator createGarnishTypeValidator;

  
@ModelAttribute("bankList")
  public Collection<BankInfo> getBankInfo()
  {
    List<BankInfo> wBankList = this.genericService.loadControlEntity(BankInfo.class);
    return wBankList;
  }
  @RequestMapping(method={RequestMethod.GET}, params={"gtid"})
  public String setupForm(@RequestParam("gtid") Long pDtid, Model model, HttpServletRequest request) throws Exception {   
	  
	  SessionManagerService.manageSession(request, model);

    BusinessCertificate bc = this.getBusinessCertificate(request);


    EmpGarnishmentType wP = this.genericService.loadObjectUsingRestriction(EmpGarnishmentType.class,Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
    CustomPredicate.procurePredicate("id", pDtid)));

    if (wP.isNewEntity()) {
      return "redirect:createGarnishmentType.do";
    }
    wP.setBankInstId(wP.getBankBranch().getBankInfo().getId());
    wP.setBranchInstId(wP.getBankBranch().getId());
    model.addAttribute("roleBean", bc);
    model.addAttribute("bankBranchList", this.genericService.loadAllObjectsWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate("bankInfo.id", wP.getBankInstId()), "name"));
    model.addAttribute("garnTypeBean", wP);
    return "editEmpGarnishmentTypeForm"; }

  @RequestMapping(method={RequestMethod.GET}, params={"gtid", "s"})
  public String setupForm(@RequestParam("gtid") Long pDtid, @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);

    BusinessCertificate bc = this.getBusinessCertificate(request);

    EmpGarnishmentType wP = this.genericService.loadObjectUsingRestriction(EmpGarnishmentType.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
            CustomPredicate.procurePredicate("id", pDtid)));

    wP.setBankInstId(wP.getBankBranch().getBankInfo().getId());
    wP.setBranchInstId(wP.getBankBranch().getId());
    String actionCompleted = "Loan Type " + wP.getDescription() + " updated Successfully!";
    model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
    model.addAttribute("saved", Boolean.valueOf(pSaved == 1));
    model.addAttribute("roleBean", bc);
    model.addAttribute("bankBranchList", this.genericService.loadAllObjectsWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate("bankInfo.id", wP.getBankInstId()), "name"));
    model.addAttribute("garnTypeBean", wP);
    return "editEmpGarnishmentTypeForm";
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("garnTypeBean") EmpGarnishmentType pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);
	     
	  BusinessCertificate bc = this.getBusinessCertificate(request);

    

    if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
    {
      return CONFIG_HOME_URL;
    }

    createGarnishTypeValidator.validate(pEHB, result);
    if (result.hasErrors())
    {
      model.addAttribute(DISPLAY_ERRORS, BLOCK);
      model.addAttribute("status", result);
      model.addAttribute("roleBean", bc);
      model.addAttribute("bankBranchList", this.genericService.loadAllObjectsWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate("bankInfo.id", pEHB.getBankInstId()), "name"));
      model.addAttribute("garnTypeBean", pEHB);
      return "editEmpGarnishmentTypeForm";
    }

    pEHB.setBankBranch(new BankBranch(pEHB.getBranchInstId()));
    pEHB.setLastModBy(new User(bc.getLoginId()));
    pEHB.setLastModTs(Timestamp.from(Instant.now()));

    this.genericService.saveObject(pEHB);

    return "redirect:editGarnishType.do?gtid=" + pEHB.getId() + "&s=1";
  }
}