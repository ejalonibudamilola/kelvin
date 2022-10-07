package com.osm.gnl.ippms.ogsg.controllers.business;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBeanInactive;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping({"/busInactiveEmpOverviewForm.do"})
@SessionAttributes(types={BusinessEmpOVBeanInactive.class})
public class BusinessInactiveEmployeeOverviewForm extends BaseController
{
  @Autowired
  EmployeeService employeeService;
   
  private final int pageLength = 20;

  private static final String VIEW_NAME = "business/busInactiveEmpOverviewForm";

  
  public BusinessInactiveEmployeeOverviewForm() { }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

		PaginationBean paginationBean = getPaginationInfo(request);

      LocalDate wToday = LocalDate.now();
      LocalDate wYearStart = LocalDate.now();

    LocalDate.of(wToday.getYear(), 1, 1);

    List<HiringInfo> empList = (List<HiringInfo>) this.employeeService.getInActiveEmployees((paginationBean.getPageNumber() - 1) * this.pageLength,
            this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(), wToday, wYearStart, 0L,  null, null, null, null, "0", bc,true);

    int wNoOfElements = this.employeeService.getTotalNoOfInActiveEmployees(null, null, 0L, false, false, null, null, null, null, "0", bc);

    BusinessEmpOVBeanInactive pList = new BusinessEmpOVBeanInactive(empList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
    pList.setShowingInactive(true);
    pList.setId(bc.getBusinessClientInstId());
    pList.setHidden("hidden");
    pList.setAdmin(bc.isSuperAdmin());

    Object userId = getSessionId(request);
    Navigator.getInstance(userId).setFromClass(getClass());

    model.addAttribute("busEmpOVBean", pList);
    Navigator.getInstance(userId).setFromClass(getClass());
    Navigator.getInstance(userId).setFromForm("redirect:busInactiveEmpOverviewForm.do");
    addRoleBeanToModel(model, request);

    return VIEW_NAME;
  }
}