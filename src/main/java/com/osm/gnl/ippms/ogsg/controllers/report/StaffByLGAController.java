package com.osm.gnl.ippms.ogsg.controllers.report;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.location.domain.LGAInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping({"/selectLGAForm.do"})
@SessionAttributes(types={BusinessEmpOVBean.class})
public class StaffByLGAController extends BaseController
{
  
  private final int pageLength = 20;

  private static final String VIEW_NAME = "employee/viewEmpByLgaForm";

  public StaffByLGAController()
  {
   
  }
  
@ModelAttribute("LGAList")
  public Collection<LGAInfo> populateLocalGovt() {
	  
	Comparator<LGAInfo> comp = Comparator.comparing(LGAInfo::getName);
	List<LGAInfo> wList = this.genericService.loadControlEntity(LGAInfo.class);
	Collections.sort(wList, comp);
    return wList;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"lgi"})
  public String setupForm(@RequestParam("lgi") Long pLgid, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);
		
	  BusinessCertificate bc = this.getBusinessCertificate(request);

	  PaginationBean paginationBean = getPaginationInfo(request);


    PredicateBuilder predicateBuilder = new PredicateBuilder();

    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("statusIndicator", IConstants.OFF));
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("lgaInfo.id", pLgid));

    List<Employee> empList = (List<Employee>) this.genericService.getObjectsFromBuilder(predicateBuilder, IppmsUtils.getEmployeeClass(bc));

    int wGLNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getEmployeeClass(bc));

    BusinessEmpOVBean pCList = new BusinessEmpOVBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements,
            paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    pCList.setShowRow(SHOW_ROW);
    pCList.setId(pLgid);

    addRoleBeanToModel(model, request);
    model.addAttribute("miniBean", pCList);

    return VIEW_NAME;
  }
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep,
		  @RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("miniBean") BusinessEmpOVBean pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
		
	  BusinessCertificate bc = this.getBusinessCertificate(request);

     

    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
      return "redirect:hrEmployeeRelatedReports.do";
    }
     
    if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {
      if (IppmsUtils.isNullOrLessThanOne(pLPB.getId()))
      {
        result.rejectValue("id", "InvalidValue", "Please select a Local Govt. Area");
         
        addDisplayErrorsToModel(model, request);
        model.addAttribute("status", result);
        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", pLPB);

        return VIEW_NAME;
      }
      return "redirect:selectLGAForm.do?lgi=" + pLPB.getId();
    }

    return "redirect:selectLGAForm.do";
  }
}