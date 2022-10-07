package com.osm.gnl.ippms.ogsg.controllers.contract;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.report.ContractHistory;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

 

@Controller
@RequestMapping({"/editContract.do"})
@SessionAttributes(types={ContractHistory.class})
public class EditEmployeeContractFormController extends BaseController
{
  
   private final String VIEW = "contract/editContractForm";
   public EditEmployeeContractFormController()
  {
    
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"cid"})
  public String setupForm(@RequestParam("cid") Long pContractId, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
	     
	  BusinessCertificate bc = this.getBusinessCertificate(request);
	   
    ContractHistory wCH = genericService.loadObjectById(ContractHistory.class,pContractId);

    if(!wCH.isExpired()){
       wCH.setCanEdit(true);
    }

    HiringInfo wHI = loadHiringInfoByEmpId(request,bc,wCH.getEmployee().getId());
     wCH.setHiringInfo(wHI);
    wCH.setExpiredBy(bc.getLoggedOnUserNames());
    addRoleBeanToModel(model, request);
    model.addAttribute("contractBean", wCH);
    return VIEW;
  }
  
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"cid","s"})
  public String setupForm(@RequestParam("cid") Long pContractId,
		  @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
	     
      BusinessCertificate bc = this.getBusinessCertificate(request);
	  
	  User l = genericService.loadObjectById(User.class,bc.getLoginId());
    
    ContractHistory wCH = genericService.loadObjectById(ContractHistory.class, (pContractId));

    HiringInfo wHI = loadHiringInfoByEmpId(request,bc,wCH.getEmployee().getId());
     wCH.setHiringInfo(wHI);
    wCH.setExpiredBy(l.getActualUserName());
    String msg = null;
    if(pSaved == 1){
      msg = "Extended";
    }else{
      msg = "Terminated";
    }
    model.addAttribute(IConstants.SAVED_MSG, "Contract for "+wCH.getEmployee().getDisplayName()+" "+msg+" Successfully.");
    addRoleBeanToModel(model, request);
    model.addAttribute("saved", true);
    model.addAttribute("contractBean", wCH);
    return VIEW;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @RequestParam(value="_terminate", required=false) String terminate,
                              @RequestParam(value="_extend", required=false) String extend, @RequestParam(value="_confirm", required=false) String confirm, @ModelAttribute("contractBean") ContractHistory pEHB,
                              BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);
	     
	  BusinessCertificate bc = this.getBusinessCertificate(request);
    
     

    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL))
    {
      return "redirect:viewContractEmployees.do";
    }

    if (isButtonTypeClick(request,REQUEST_PARAM_EXTEND))
    {
      String errMsg = validateForExtension(pEHB);
      if(!IppmsUtils.isNullOrEmpty(errMsg)) {
        result.rejectValue("", "Term.Warn", errMsg);
        addDisplayErrorsToModel(model, request);
        model.addAttribute("status", result);
        model.addAttribute("contractBean", pEHB);
        return VIEW;
      }

      pEHB.setTerminationWarning(true);
      pEHB.setEditMode(true);
      result.rejectValue("", "Term.Warn", "You have chosen to extend this Contract. Press the 'Confirm' Button to complete the Contract Extension. 'Cancel' Button to stop Contract Extension Process.");
      addDisplayErrorsToModel(model, request);
      model.addAttribute("status", result);
      model.addAttribute("contractBean", pEHB);
      return VIEW;
    }else{
      if (isButtonTypeClick(request,REQUEST_PARAM_TERMINATE))
      {
        pEHB.setTerminationWarning(true);
        result.rejectValue("", "Term.Warn", "You have chosen to terminate this Contract. Press the 'Confirm' Button to terminate. 'Cancel' Button to stop Contract Termination Process.");
        addDisplayErrorsToModel(model, request);
        model.addAttribute("status", result);
        model.addAttribute("contractBean", pEHB);
        return VIEW;
      }

    }
    int pointer = 0;
    if (isButtonTypeClick(request,REQUEST_PARAM_CONFIRM))
    {
      if(pEHB.isEditMode()){

        pEHB.setContractEndDay(pEHB.getContractExtEndDate().getDayOfMonth());
        pEHB.setContractEndMonth(pEHB.getContractExtEndDate().getMonthValue());
        pEHB.setContractEndYear(pEHB.getContractExtEndDate().getYear());
        pEHB.setContractEndDate(pEHB.getContractExtEndDate());

        pEHB.setLastModBy(new User(bc.getLoginId()));


        //Now persist this guy.
        this.genericService.storeObject(pEHB);

        pEHB.getHiringInfo().setContractEndDate(pEHB.getContractEndDate());
        pEHB.getHiringInfo().setLastModBy(new User(bc.getLoginId()));

        this.genericService.storeObject(pEHB.getHiringInfo());
        pointer = 1;
      }else{
        HiringInfo wHI = pEHB.getHiringInfo();
        wHI.setContractExpiredInd(1);
        wHI.setStaffInd(0);
        wHI.setContractEndDate(LocalDate.now());
        this.genericService.saveObject(wHI);
        pEHB.setExpiredInd(1);

        pEHB.setExpiredDate(LocalDate.now());
        pEHB.setLastModBy(new User(bc.getLoginId()));

        this.genericService.saveObject(pEHB);
        pointer = 2;
      }

    }

    return "redirect:editContract.do?cid="+pEHB.getId()+"&s="+pointer;
  }

  private String validateForExtension(ContractHistory pEHB) {
    String wRetVal = null;
    if( pEHB.getContractExtEndDate() == null) {
      wRetVal = "'Contract Extension End Date' is required for Contract Extensions.";
    }else {

      if(!pEHB.getContractExtEndDate().isAfter(pEHB.getContractEndDate())) {
        wRetVal = "'Contract Extension End Date' Must be after the Original Contract End Date.";
      }

    }
    return wRetVal;
  }

}