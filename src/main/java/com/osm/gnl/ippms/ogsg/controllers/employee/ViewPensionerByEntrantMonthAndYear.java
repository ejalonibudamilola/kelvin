package com.osm.gnl.ippms.ogsg.controllers.employee;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PensionService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.DataTableBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;



@Controller
@RequestMapping({"/viewPensionerbyEntrant.do"})
@SessionAttributes(types={DataTableBean.class})
public class ViewPensionerByEntrantMonthAndYear extends BaseController
{


    private final int pageLength = 80;
    private final PensionService pensionService;

    private final String VIEW = "employee/viewPenByEntryDate";



    public ViewPensionerByEntrantMonthAndYear(PensionService pensionService)
    {
        this.pensionService = pensionService;
    }

    @ModelAttribute("monthList")
    public List<NamedEntity> getMonthList() {
        return PayrollBeanUtils.makeAllMonthList();
    }

    @ModelAttribute("yearList")
    public Collection<NamedEntity> makeYearList(HttpServletRequest request) {
        BusinessCertificate bc = getBusinessCertificate(request);
        return this.pensionService.makeYearList(bc);
    }

    @RequestMapping(method={RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        PayrollFlag wPf = this.genericService.loadObjectWithSingleCondition(PayrollFlag.class,getBusinessClientIdPredicate(request));


        List<AbstractPaycheckEntity> penList = this.pensionService.loadPensionersByEntrantMonthAndYear(bc, wPf.getApprovedMonthInd(), wPf.getApprovedYearInd());


        DataTableBean wPELB = new DataTableBean(penList);

        wPELB.setRunMonth(wPf.getApprovedMonthInd());
        wPELB.setRunYear(wPf.getApprovedYearInd());


        wPELB.setShowRow(SHOW_ROW);

        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", wPELB);
        model.addAttribute("listSize", penList.size());
        return VIEW;
    }

    @RequestMapping(method={RequestMethod.GET},params={"rm","ry"})
    public String setupForm(@RequestParam("rm") int pRunMonth,
                            @RequestParam("ry") int pRunYear, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
    {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        List<AbstractPaycheckEntity> penList = this.pensionService.loadPensionersByEntrantMonthAndYear(bc, pRunMonth, pRunYear);


        DataTableBean wPELB = new DataTableBean(penList);

        wPELB.setShowRow(SHOW_ROW);


        wPELB.setRunMonth(pRunMonth);
        wPELB.setRunYear(pRunYear);

        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", wPELB);
        model.addAttribute("listSize", penList.size());

        return VIEW;
    }
    @RequestMapping(method={RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep,
                                @RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("miniBean") DataTableBean pLPB,
                                BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);



        if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
            return "redirect:reportsOverview.do";
        }

        if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {
            if ((pLPB.getRunMonth() == -1) && (pLPB.getRunYear() == 0)){
                result.rejectValue("", "InvalidValue", "Please select valid Month and Year");
                addDisplayErrorsToModel(model, request);

                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("miniBean", pLPB);

                return VIEW;
            }
            else  if ((pLPB.getRunMonth() == -1) && (pLPB.getRunYear() != 0)){
                result.rejectValue("", "InvalidValue", "Please select valid Month and Year");
                addDisplayErrorsToModel(model, request);

                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("miniBean", pLPB);

                return VIEW;
            }
            else  if ((pLPB.getRunMonth() != -1) && (pLPB.getRunYear() == 0)){
                result.rejectValue("", "InvalidValue", "Please select valid Month and Year");
                addDisplayErrorsToModel(model, request);

                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("miniBean", pLPB);

                return VIEW;
            }
            else if(pLPB.getRunMonth() > 0 && pLPB.getRunYear() > 0){
                return "redirect:viewPensionerbyEntrant.do?rm=" + pLPB.getRunMonth() + "&ry=" + pLPB.getRunYear();
            }


        }

        return "redirect:viewPensionerbyEntrant.do";
    }
}