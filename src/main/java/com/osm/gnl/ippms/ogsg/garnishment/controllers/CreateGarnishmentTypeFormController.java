package com.osm.gnl.ippms.ogsg.garnishment.controllers;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.validators.garnishment.CreateGarnishTypeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping({"/createGarnishmentType.do"})
public class CreateGarnishmentTypeFormController extends BaseController
{
  
  @Autowired
  CreateGarnishTypeValidator createGarnishTypeValidator;

   private final String VIEW_NAME = "configcontrol/createGarnishTypeForm";
@ModelAttribute("bankList")
  public Collection<BankInfo> getBankInfo()
  {
    List<BankInfo> wBankList = this.genericService.loadControlEntity(BankInfo.class);
    Collections.sort(wBankList);
    return wBankList;
  }
  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);
	     
	   


    EmpGarnishmentType wEDT = new EmpGarnishmentType();
    model.addAttribute("roleBean", super.getBusinessCertificate(request));
    model.addAttribute("garnTypeBean", wEDT);
    return VIEW_NAME;
  }

  @RequestMapping(method={RequestMethod.GET}, params={"gtid", "s"})
  public String setupForm(@RequestParam("gtid") Long pDtid, @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
	     
	  EmpGarnishmentType wP = this.genericService.loadObjectById(EmpGarnishmentType.class,pDtid);

    wP.setBankInstId(wP.getBankInstId());
    wP.setBranchInstId(wP.getBranchInstId());
    String actionCompleted = "Loan Type " + wP.getDescription() + " created successfully!";
    model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
    model.addAttribute("saved", Boolean.valueOf(pSaved == 1));
    model.addAttribute("bankBranchList", this.genericService.loadAllObjectsWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate("bankInfo.id", wP.getBankInstId()),null));
    model.addAttribute("garnTypeBean", wP);
    model.addAttribute("roleBean", super.getBusinessCertificate(request));
    return VIEW_NAME;
  }

  @RequestMapping(method={RequestMethod.POST})
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
      model.addAttribute("bankBranchList", this.genericService.loadAllObjectsWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate("bankInfo.id", pEHB.getBankInstId()),null));
      model.addAttribute("garnTypeBean", pEHB);
      model.addAttribute("roleBean", super.getBusinessCertificate(request));
      return VIEW_NAME;
    }

    pEHB.setBankBranch(new BankBranch(pEHB.getBranchInstId()));
    pEHB.setLastModBy(new User(bc.getLoginId()));
    pEHB.setCreatedBy(new User(bc.getLoginId()));
    pEHB.setLastModTs(Timestamp.from(Instant.now()));
    pEHB.setBusinessClientId(bc.getBusinessClientInstId());

    Long _id = this.genericService.storeObject(pEHB);

    return "redirect:createGarnishmentType.do?gtid=" + _id + "&s=1";
  }
}