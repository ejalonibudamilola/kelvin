package com.osm.gnl.ippms.ogsg.controllers.report;


import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.BankService;
import com.osm.gnl.ippms.ogsg.domain.report.GrossSummary;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRun;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping({"/grossSummary.do"})
@SessionAttributes(types={PaginatedPaycheckGarnDedBeanHolder.class})
public class GrossSummaryFormController extends BaseController {

    @Autowired
    PaycheckService paycheckService;

    @Autowired
    BankService bankService;

    private final String VIEW_NAME = "payment/grossSummaryForm";

    @ModelAttribute("monthList")
    public List<NamedEntity> getMonthList() {
        return PayrollBeanUtils.makeAllMonthList();
    }

    @ModelAttribute("yearList")
    public List<NamedEntity> makeYearList(HttpServletRequest request) {

        return this.paycheckService.makePaycheckYearList(getBusinessCertificate(request));
    }

    @RequestMapping(method={RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);


        List<?> empBeanList = new ArrayList<>();

        Long pfId = this.genericService.loadMaxValueByClassClientIdAndColumn(PayrollRun.class, "id",bc.getBusinessClientInstId(),"businessClientId");

        PayrollRun pf = this.genericService.loadObjectById(PayrollRun.class,pfId);

        PaginatedPaycheckGarnDedBeanHolder p = null;
        if ((pf == null) || (pf.isNewEntity())) {
            p = new PaginatedPaycheckGarnDedBeanHolder();
            p.setShowRow(HIDE_ROW);
        }
        else {
            LocalDate fDate;
            fDate = pf.getPayPeriodStart();


            p = this.makePaginatedList(p, fDate.getMonthValue(), fDate.getYear(), request, false);
            p.setShowRow(SHOW_ROW);
            p.setRunMonth(fDate.getMonthValue());
            p.setRunYear(fDate.getYear());
        }
        p.setCompanyName(bc.getBusinessName());
        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", p);
        model.addAttribute("displayList", p);
        return VIEW_NAME;
    }

    private PaginatedPaycheckGarnDedBeanHolder makePaginatedList(PaginatedPaycheckGarnDedBeanHolder pHMB , int pRunMonth, int pRunYear, HttpServletRequest request, boolean errorRec ) throws Exception {

        PaginationBean paginationBean = getPaginationInfo(request);

        BusinessCertificate bc = getBusinessCertificate(request);

//        List<GlobalPercentConfigDetails> wAllList = pHMB.getConfigDetailsList();
        List<GrossSummary> wAllList = bankService.grossSummaryByLga(bc, pRunMonth, pRunYear);
        if(wAllList == null){
            wAllList = new ArrayList<>();
        }
        //Make a new NamedEntity....

        List<GrossSummary> wRetList = null;


        if(wAllList.size() > 10) {

            wRetList = (List<GrossSummary>) PayrollUtils.paginateList(paginationBean.getPageNumber(), 10, wAllList);

        }else {
            wRetList = wAllList;
        }

        PaginatedPaycheckGarnDedBeanHolder wPBO = new PaginatedPaycheckGarnDedBeanHolder(wRetList, paginationBean.getPageNumber(), 10, wAllList.size(), paginationBean.getSortOrder(), paginationBean.getSortCriterion());

        wPBO.setSomeObject(pHMB);


        return wPBO;
    }

    @RequestMapping(method={RequestMethod.GET}, params={"rm", "ry"})
    public String setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        PaginatedPaycheckGarnDedBeanHolder p = new PaginatedPaycheckGarnDedBeanHolder();

        p = this.makePaginatedList(p, pRunMonth, pRunYear, request, false);

        if(p.isEmptyList()){
            p.setShowRow(HIDE_ROW);
        }
        p.setShowRow(SHOW_ROW);
        p.setRunMonth(pRunMonth);
        p.setRunYear(pRunYear);
        p.setCompanyName(bc.getBusinessName());

        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", p);
        model.addAttribute("displayList", p);
        return VIEW_NAME;
    }


    @RequestMapping(method={RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep,
                                @RequestParam(value="_close", required=false) String close,
                                @ModelAttribute("miniBean") PaginatedPaycheckGarnDedBeanHolder ppDMB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
            throws Exception
    {
        SessionManagerService.manageSession(request, model);

        if (isButtonTypeClick(request, REQUEST_PARAM_CLOSE))
        {
            return "redirect:reportsOverview.do";
        }

        return "redirect:grossSummary.do?rm=" + ppDMB.getRunMonth() + "&ry=" + ppDMB.getRunYear();

    }


}
