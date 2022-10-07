package com.osm.gnl.ippms.ogsg.controllers.business;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.EmployeeReportService;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Damilola Ejalonibu
 */

@Controller
@RequestMapping({"/activeEmpOverview.do"})
@SessionAttributes(types={BusinessEmpOVBean.class})
public class BusinessEmployeePopupFormController extends BaseController
{


  @Autowired
  private EmployeeReportService employeeReportService;

  private final String VIEW_NAME = "business/busEmpOverviewPopupForm";


   private final PayrollService payrollService;
   private final PaycheckService paycheckService;

    @Autowired
  public BusinessEmployeePopupFormController(PayrollService payrollService, PaycheckService paycheckService) {
        this.payrollService = payrollService;
        this.paycheckService = paycheckService;
    }

    @ModelAttribute("monthList")
    protected ArrayList<NamedEntity> getMonthsList(){
        return PayrollBeanUtils.makeAllMonthList();
    }
    @ModelAttribute("yearList")
    protected List<NamedEntity> getYearList(HttpServletRequest request){
        return this.paycheckService.makePaycheckYearList(getBusinessCertificate(request));
    }


    @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);

    PaginationBean paginationBean = getPaginationInfo(request);


    List<HiringInfo> empList = employeeReportService.getActiveEmployees(getBusinessCertificate(request),0,0);

   BusinessEmpOVBean pList = new BusinessEmpOVBean(empList, paginationBean.getPageNumber(), this.pageLength, empList.size(), paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    pList.setId(bc.getBusinessClientInstId());


    pList.setRunMonth(-1);
    pList.setRunYear(0);

    Object userId = getSessionId(request);
    Navigator.getInstance(userId).setFromClass(getClass());

    model.addAttribute("busEmpOVBean", pList);
    Navigator.getInstance(userId).setFromClass(getClass());
    Navigator.getInstance(userId).setFromForm("redirect:activeEmpOverview.do");
    model.addAttribute("all","none");
    addRoleBeanToModel(model, request);
    return VIEW_NAME;
  }
    @RequestMapping(method={RequestMethod.GET},params = {"rm","ry"})
    public String setupForm(@RequestParam(value = "rm") int pRunMonth,
                            @RequestParam(value = "ry") int pRunYear,
                            Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
    {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        PaginationBean paginationBean = getPaginationInfo(request);


        List<HiringInfo> empList = employeeReportService.getActiveEmployees(getBusinessCertificate(request),pRunMonth,pRunYear);

        BusinessEmpOVBean pList = new BusinessEmpOVBean(empList, paginationBean.getPageNumber(), this.pageLength, empList.size(), paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        pList.setId(bc.getBusinessClientInstId());
        if(pRunYear == 0)
            pRunMonth = -1;

        pList.setRunMonth(pRunMonth);
        pList.setRunYear(pRunYear);

        Object userId = getSessionId(request);
        Navigator.getInstance(userId).setFromClass(getClass());

        model.addAttribute("busEmpOVBean", pList);
        Navigator.getInstance(userId).setFromClass(getClass());
        Navigator.getInstance(userId).setFromForm("redirect:activeEmpOverview.do");
        model.addAttribute("all","none");
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }
    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep,
                                @RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("busEmpOVBean")
                                        BusinessEmpOVBean pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
            throws Exception
    {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return REDIRECT_TO_DASHBOARD;
        }


         if (isButtonTypeClick(request, REQUEST_PARAM_UPDATE_REPORT)) {


            if(pLPB.getRunMonth() != -1 && pLPB.getRunYear() == 0){
                result.rejectValue("", "InvalidValue", "Please select value for 'Year'");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("busEmpOVBean", pLPB);

                return VIEW_NAME;
            }else if(pLPB.getRunMonth() == -1 && pLPB.getRunYear() != 0){
                result.rejectValue("", "InvalidValue", "Please select value for 'Month'");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("busEmpOVBean", pLPB);

            }

            return "redirect:activeEmpOverview.do?rm="+pLPB.getRunMonth()+"&ry="+pLPB.getRunYear();

        }
        return "redirect:activeEmpOverview.do";
    }

}