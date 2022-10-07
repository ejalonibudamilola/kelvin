package com.osm.gnl.ippms.ogsg.controllers.employee;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.DataTableBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping({"/busEmpOverviewForm.do"})
@SessionAttributes(types={BusinessEmpOVBean.class})
public class BusinessEmployeeOverviewForm extends BaseController{

 
  private final int pageLength = 20;

  private static final String VIEW_NAME = "business/busEmpOverviewForm";
  private static final String VIEW_NAME2 = "business/busEmpOverviewForm2";

  @Autowired
  private EmployeeService employeeService;
 
  public BusinessEmployeeOverviewForm() {}

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException{
    SessionManagerService.manageSession(request, model);

    BusinessCertificate bc = this.getBusinessCertificate(request);

    PredicateBuilder predicateBuilder = new PredicateBuilder();
    predicateBuilder.addPredicate(getBusinessClientIdPredicate(request));
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("statusIndicator", IConstants.OFF));


    PaginationBean paginationBean = getPaginationInfo(request);

    int wNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getEmployeeClass(bc));

    List<AbstractEmployeeEntity> empList=  this.employeeService.getTheFirst200Rows(bc,200);

    DataTableBean dtb = new DataTableBean(empList);

    BusinessEmpOVBean pList = new BusinessEmpOVBean(empList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
    pList.setShowingInactive(false);
    pList.setId(bc.getBusinessClientInstId());
    pList.setHidden("hidden");
    pList.setAdmin(bc.isSuperAdmin());

    Object userId = getSessionId(request);
    Navigator.getInstance(userId).setFromClass(getClass());

//    model.addAttribute("busEmpOVBean", pList);
    model.addAttribute("busEmpOVBean", dtb.getObjectList());
    Navigator.getInstance(userId).setFromClass(getClass());
    Navigator.getInstance(userId).setFromForm("redirect:busEmpOverviewForm.do");
    addRoleBeanToModel(model, request);
    return VIEW_NAME;
  }

  @RequestMapping(method={RequestMethod.GET}, params = {"all"})
  public String setupForm(Model model, HttpServletRequest request, @RequestParam("all") String all) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

    PredicateBuilder predicateBuilder = new PredicateBuilder();
    predicateBuilder.addPredicate(getBusinessClientIdPredicate(request));
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("statusIndicator", IConstants.OFF));


    PaginationBean paginationBean = getPaginationInfo(request);

    int wNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getEmployeeClass(bc));

    List<AbstractEmployeeEntity> empList=  this.employeeService.getTheFirst200Rows(bc,0);

    DataTableBean dtb = new DataTableBean(empList);

    BusinessEmpOVBean pList = new BusinessEmpOVBean(empList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
    pList.setShowingInactive(false);
    pList.setId(bc.getBusinessClientInstId());
    pList.setHidden("hidden");
    pList.setAdmin(bc.isSuperAdmin());

    Object userId = getSessionId(request);
    Navigator.getInstance(userId).setFromClass(getClass());

//    model.addAttribute("busEmpOVBean", pList);
    model.addAttribute("busEmpOVBean", dtb.getObjectList());
    Navigator.getInstance(userId).setFromClass(getClass());
    Navigator.getInstance(userId).setFromForm("redirect:busEmpOverviewForm.do");
    addRoleBeanToModel(model, request);
    return VIEW_NAME2;

//    return this.prepareAndReturnView(model,request,bc);
  }

  @RequestMapping(method={RequestMethod.GET}, params={"bcid"})
  public String setupForm(@RequestParam("bcid") Long bcid, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);
	  BusinessCertificate bc = this.getBusinessCertificate(request);
	  return this.prepareAndReturnView(model,request,bc);

  }
  private String prepareAndReturnView(Model model, HttpServletRequest request, BusinessCertificate bc){

    PredicateBuilder predicateBuilder = new PredicateBuilder();
    predicateBuilder.addPredicate(getBusinessClientIdPredicate(request));
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("statusIndicator", IConstants.OFF));

    List<CustomPredicate> predicates = new ArrayList<>();
    predicates.add(getBusinessClientIdPredicate(request));
    predicates.add(CustomPredicate.procurePredicate("statusIndicator", IConstants.OFF));


    PaginationBean paginationBean = getPaginationInfo(request);

    int wNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, Employee.class);

//    List<AbstractEmployeeEntity> empList = (List<AbstractEmployeeEntity>) this.genericService.loadPaginatedObjects(IppmsUtils.getEmployeeClass(bc),
//            predicateBuilder.getPredicates(), (paginationBean.getPageNumber() - 1) * this.pageLength,
//            this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());
//    Collections.sort(empList);


    List<AbstractEmployeeEntity> empList=  this.employeeService.getTheFirst200Rows(bc,0);

    DataTableBean dtb = new DataTableBean(empList);



    BusinessEmpOVBean pList = new BusinessEmpOVBean(empList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
    pList.setShowingInactive(false);
    pList.setId(bc.getBusinessClientInstId());
    pList.setHidden("hidden");
    pList.setAdmin(bc.isSuperAdmin());

    Object userId = getSessionId(request);
    Navigator.getInstance(userId).setFromClass(getClass());

//    model.addAttribute("busEmpOVBean", pList);
    model.addAttribute("busEmpOVBean", dtb.getObjectList());
    Navigator.getInstance(userId).setFromClass(getClass());
    Navigator.getInstance(userId).setFromForm("redirect:busEmpOverviewForm.do");
    addRoleBeanToModel(model, request);
    return VIEW_NAME;
  }
}