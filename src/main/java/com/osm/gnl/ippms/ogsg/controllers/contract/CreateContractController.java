package com.osm.gnl.ippms.ogsg.controllers.contract;


import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.EmployeeType;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.report.ContractHistory;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.validators.contract.CreateEmployeeContractValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;

@Controller
@RequestMapping({"/createEmployeeContract.do"})
@SessionAttributes(types={ContractHistory.class})
public class CreateContractController extends BaseController
{
  

   private final CreateEmployeeContractValidator validator;
   private final String VIEW = "contract/createContractForm";

  @Autowired
  public CreateContractController(CreateEmployeeContractValidator validator)
  {
    this.validator = validator;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"eid"})
  public String setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
    BusinessCertificate bc = getBusinessCertificate(request);
    ContractHistory wCH = new ContractHistory();

    HiringInfo wHI = loadHiringInfoByEmpId(request,bc,pEmpId);
    EmployeeType employeeType;
    if(wHI.getTerminateReason() != null && !wHI.getTerminateReason().isNewEntity()) {
    	//Make Sure this EmployeeType is enabled for Contract.
    	 /**
         * Mustola -- Added New Configuration
         * If It is a terminated Employee...then Check if the Employee Type is of Contract
         * if not, set it to a contract type of Employee...
         * Jan 10th, 2020.
         */
     
         
        if(!wHI.getAbstractEmployeeEntity().getEmployeeType().isContractStaff()) {
          if(wHI.getTerminateReason().isNotReinstateable()){
            employeeType = this.genericService.loadObjectUsingRestriction(EmployeeType.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("contractStatusInd",1),
                    CustomPredicate.procurePredicate("renewableInd",0)));
          }else{
            employeeType = this.genericService.loadObjectUsingRestriction(EmployeeType.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("contractStatusInd",1),
                    CustomPredicate.procurePredicate("renewableInd",1)));
          }
        }else{
          //set As Contract Type....
          employeeType = this.genericService.loadObjectUsingRestriction(EmployeeType.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("contractStatusInd",1),
                  CustomPredicate.procurePredicate("renewableInd",1)));
        }
      wHI.getAbstractEmployeeEntity().setEmployeeType(employeeType);
      wHI.setStaffTypeChanged(true);
    }else{
      if(!wHI.getAbstractEmployeeEntity().getEmployeeType().isContractStaff()){
        employeeType = this.genericService.loadObjectUsingRestriction(EmployeeType.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("contractStatusInd",1),
                CustomPredicate.procurePredicate("renewableInd",1)));
        wHI.getAbstractEmployeeEntity().setEmployeeType(employeeType);
        wHI.setStaffTypeChanged(true);
      }
        
    }
    wCH.setHiringInfo(wHI);
    wCH.setName(wHI.getAbstractEmployeeEntity().getEmployeeId()+"/CONT/");
    wCH.setReferenceNumber("REF/IPPMS/"+bc.getUserName()+"/CNT/"+ PayrollBeanUtils.getMonthNameAndYearForExcelNaming(LocalDate.now().getMonthValue(), LocalDate.now().getYear(),true,false));
    addRoleBeanToModel(model, request);
    model.addAttribute("contractBean", wCH);
    return VIEW;
  }
   
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"eid", "mode"})
  public String setupForm(@RequestParam("eid") Long pEmpId, @RequestParam("mode") String pMode, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
	  SessionManagerService.manageSession(request, model);

    BusinessCertificate bc = getBusinessCertificate(request);
    
    ContractHistory wCH = new ContractHistory();

    HiringInfo wHI = loadHiringInfoByEmpId(request,bc,pEmpId);
    if(!wHI.getAbstractEmployeeEntity().getEmployeeType().isContractStaff()){
      EmployeeType employeeType = this.genericService.loadObjectUsingRestriction(EmployeeType.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("contractStatusInd", 1),
              CustomPredicate.procurePredicate("renewableInd", 1)));
      wHI.getAbstractEmployeeEntity().setEmployeeType(employeeType);
      wHI.setStaffTypeChanged(true);
    }
    wCH.setHiringInfo(wHI);
    wCH.setMode("create");
    wCH.setName(wHI.getAbstractEmployeeEntity().getEmployeeId()+"/CONT/");
    wCH.setReferenceNumber("REF/IPPMS/"+bc.getUserName()+"/CNT/"+ PayrollBeanUtils.getMonthNameAndYearForExcelNaming(LocalDate.now().getMonthValue(), LocalDate.now().getYear(),true,false));
    addRoleBeanToModel(model, request);
    model.addAttribute("contractBean", wCH);
    return VIEW;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"chid", "s"})
  public String setupForm(@RequestParam("chid") Long pContractId, @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

    BusinessCertificate bc = getBusinessCertificate(request);
    
    ContractHistory wCH = genericService.loadObjectById(ContractHistory.class,pContractId);

   
    wCH.setHiringInfo(loadHiringInfoByEmpId(request,bc,wCH.getEmployee().getId()));

    model.addAttribute(IConstants.SAVED_MSG, "Contract for "+wCH.getHiringInfo().getEmployee().getDisplayNameWivTitlePrefixed()+" [ "+wCH.getHiringInfo().getEmployee().getId()+" ] created successfully");
    model.addAttribute("saved", true);
    addRoleBeanToModel(model, request);
    model.addAttribute("contractBean", wCH);
    
    return "contract/createContractSuccessForm";
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @RequestParam(value="_ok", required=false) String ok, @ModelAttribute("contractBean") ContractHistory pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);
	     
	  BusinessCertificate bc = this.getBusinessCertificate(request);
    


    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL))
    {

      return REDIRECT_TO_DASHBOARD;
    }

    validator.validate(pEHB, result, bc);
    if (result.hasErrors())
    {
      addDisplayErrorsToModel(model, request);
      addRoleBeanToModel(model, request);
      model.addAttribute("status", result);
      model.addAttribute("contractBean", pEHB);
      return VIEW;
    }
   

    pEHB.setContractEndDay(pEHB.getContractEndDate().getDayOfMonth());
    pEHB.setContractEndMonth(pEHB.getContractEndDate().getMonthValue());
    pEHB.setContractEndYear(pEHB.getContractEndDate().getYear());

    pEHB.setContractStartDay(pEHB.getContractStartDate().getDayOfMonth());
    pEHB.setContractStartMonth(pEHB.getContractStartDate().getMonthValue());
    pEHB.setContractStartYear(pEHB.getContractStartDate().getYear());

    pEHB.setSalaryInfo(pEHB.getHiringInfo().getEmployee().getSalaryInfo());
    pEHB.setEmployee(pEHB.getHiringInfo().getEmployee());
    pEHB.setLastModBy(new User(bc.getLoginId()));
    pEHB.setCreatedBy(new User(bc.getLoginId()));
    pEHB.setBusinessClientId(bc.getBusinessClientInstId());
    pEHB.setLastModTs(Timestamp.from(Instant.now()));
    if(pEHB.getReferenceNumber().length() > 44)
      pEHB.setReferenceNumber(pEHB.getReferenceNumber().substring(0,43));

    this.genericService.saveObject(pEHB);
    if(pEHB.getHiringInfo().isStaffTypeChanged()){
        if(bc.isPensioner())
          this.genericService.storeObject(pEHB.getHiringInfo().getPensioner());
        else
          this.genericService.storeObject(pEHB.getHiringInfo().getEmployee());
    }
    pEHB.getHiringInfo().setContractStartDate(pEHB.getContractStartDate());
    pEHB.getHiringInfo().setContractEndDate(pEHB.getContractEndDate());
    pEHB.getHiringInfo().setStaffInd(1);
    pEHB.getHiringInfo().setContractExpiredInd(0);
    //pEHB.getHiringInfo().setContractId(pEHB.getId());
    this.genericService.storeObject(pEHB.getHiringInfo());


    return "redirect:createEmployeeContract.do?chid="+pEHB.getId()+"&s=1";
  }
}