package com.osm.gnl.ippms.ogsg.controllers.allowance;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.ltg.domain.AbmpBean;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping({"/ltgByMDAPEmpDetails.do"})
@SessionAttributes(types={PaginatedBean.class})
public class ViewEmployeesByMBAPAndLTGFormController extends BaseController{

  @Autowired
  EmployeeService employeeService;
   
  private final String VIEW = "LTG/casp/employeeAssignedToMDAModal";
  public ViewEmployeesByMBAPAndLTGFormController()
  {}

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"eid"})
  public String setupForm(@RequestParam("eid") Long pEid, Model model, HttpServletRequest request)
    throws Exception  {
	  SessionManagerService.manageSession(request, model);

    BusinessCertificate bc = this.getBusinessCertificate(request);

    PaginationBean paginationBean = getPaginationInfo(request);

    MdaInfo mdaInfo = this.genericService.loadObjectUsingRestriction(MdaInfo.class, Arrays.asList(
            CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), CustomPredicate.procurePredicate("id",pEid)));

    String wObjectName = mdaInfo.getName();

    List<AbmpBean> wABList = this.employeeService.getActiveEmployeesForLTGByMDAP((paginationBean.getPageNumber() - 1) * IConstants.pageLength, IConstants.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(), pEid,bc);

    int wNoOfElements = this.employeeService.getTotalNoOfActiveEmployeesForLTGByMDAP(pEid,bc);

    PaginatedBean wPHDB = new PaginatedBean(wABList, paginationBean.getPageNumber(), IConstants.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    wPHDB.setObjectName(wObjectName);
    wPHDB.setNoOfEmployees(wNoOfElements);

    model.addAttribute("miniBean", wPHDB);
    model.addAttribute("ltgBean", wABList);
    addRoleBeanToModel(model, request);

    return VIEW;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
                              @ModelAttribute("miniBean") HrMiniBean pHADB, BindingResult result,
                              SessionStatus status, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
	 
    return "redirect:determineDashBoard.do";
  }
}