package com.osm.gnl.ippms.ogsg.controllers.search;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.validators.search.SearchValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @author Damilola Ejalonibu
 */

@Controller
@RequestMapping({"/searchEmpForPRC.do"})
@SessionAttributes(types={HrMiniBean.class})
public class SearchForEmpForPRCFormController extends BaseController{

  @Autowired
  private SearchValidator searchValidator;
  public SearchForEmpForPRCFormController()
  {
   
  }
  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);


    HrMiniBean empHrBean = new HrMiniBean();

    empHrBean.setDisplayTitle("Search for Employee to create a Pay Record Card.");

    model.addAttribute("empMiniBean", empHrBean);
    addRoleBeanToModel(model, request);
    return "search/searchEmployeeForEditForm"; }

  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("empMiniBean") HrMiniBean pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
    throws Exception {
    SessionManagerService.manageSession(request, model);

    BusinessCertificate bc = this.getBusinessCertificate(request);


    if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
      return REDIRECT_TO_DASHBOARD;
    }

    searchValidator.validate(pEHB, result);
    if (result.hasErrors()) {
      addDisplayErrorsToModel(model, request);
      addRoleBeanToModel(model, request);
      model.addAttribute("status", result);
      model.addAttribute("empMiniBean", pEHB);
      return "search/searchEmployeeForEditForm";
    }

    boolean useFirstName = false;
    boolean useLastName = false;
    boolean useEmployeeId = false;
    if (!PayrollHRUtils.treatNull(pEHB.getEmployeeId()).equals("")) {
      pEHB.setEmployeeId(PayrollHRUtils.treatSqlStringParam(pEHB.getEmployeeId(), false));

      useEmployeeId = true;
    } else {
      if (!PayrollHRUtils.treatNull(pEHB.getFirstName()).equals("")) {
        pEHB.setFirstName(PayrollHRUtils.treatSqlStringParam(pEHB.getFirstName(), false));
        useFirstName = true;
      }
      if (!PayrollHRUtils.treatNull(pEHB.getLastName()).equals("")) {
        pEHB.setLastName(PayrollHRUtils.treatSqlStringParam(pEHB.getLastName(), false));
        useLastName = true;
      }

    }

//    List<?> emp = this.hrService.findHrEmployee(bc.getBusinessClientInstId(), pEHB, useFirstName, useLastName, useEmployeeId);
    CustomPredicate cp = null;

    if(useEmployeeId){
      cp = CustomPredicate.procurePredicate("employeeId", pEHB.getEmployeeId(), Operation.LIKE);
    }
    else{
      if (useFirstName){
        cp = CustomPredicate.procurePredicate("firstName", pEHB.getFirstName(), Operation.LIKE);
      }
      if(useLastName){
        cp = CustomPredicate.procurePredicate("firstName", pEHB.getLastName(), Operation.LIKE);
      }
    }
    List<?> emp = this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getEmployeeClass(bc), Arrays.asList(
            CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),cp), null);
//    System.out.println("emp is "+emp);

    if (emp.size() < 1)
    {
      result.rejectValue("", "search.no_values", "No Employee(s) found! Please retry.");

      addDisplayErrorsToModel(model, request);

      model.addAttribute("status", result);
      model.addAttribute("empMiniBean", pEHB);
      addRoleBeanToModel(model, request);
      return "search/searchEmployeeForEditForm";
    }

    if (emp.size() == 1)
    {
      Employee e = (Employee)emp.get(0);

      return "redirect:generatePRC.do?eid=" + e.getId();
    }
    return "redirect:multiEmpResultViewForm.do?fn=" + IppmsUtils.treatNull(pEHB.getFirstName()) + "&ln=" + IppmsUtils.treatNull(pEHB.getLastName()) + "&noe=" + emp.size() +  "&cn=10";
  }
}