package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRun;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.pagination.beans.PayrollSummaryBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;


/**
 * @author Damilola Ejalonibu
 */


@Controller
@RequestMapping({"/mdaPayrollAnalysisExcel.do"})
@SessionAttributes(types = {PayrollSummaryBean.class})
public class DetailedPayrollAnalysisReportController extends BaseController {

    private final int pageLength = 20;
    private final PaycheckService paycheckService;
    private final String VIEW = "report/payrollAnalysisDetailedForm";

    @Autowired
    public DetailedPayrollAnalysisReportController(PaycheckService paycheckService) {
        this.paycheckService = paycheckService;
    }


    @ModelAttribute("monthList")
    private List<NamedEntity> getMonthsList() {
        return PayrollBeanUtils.makeAllMonthList();
    }

    @ModelAttribute("yearList")
    private List<NamedEntity> getYearList(HttpServletRequest request) {
        return paycheckService.makePaycheckYearList(getBusinessCertificate(request));
    }

    @ModelAttribute("mdaList")
    private List<MdaInfo> getMdaList(HttpServletRequest request) {
        return this.genericService.loadAllObjectsWithSingleCondition(MdaInfo.class, getBusinessClientIdPredicate(request), "name");
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        Long pfId = this.genericService.loadMaxValueByClassClientIdAndColumn(PayrollRunMasterBean.class, "id", bc.getBusinessClientInstId(), "businessClientId");

        PayrollRunMasterBean pf = this.genericService.loadObjectById(PayrollRunMasterBean.class, pfId);
        return "redirect:mdaPayrollAnalysisExcel.do?rm=" + pf.getRunMonth() + "&ry=" + pf.getRunYear()+"&mid=0";
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"rm", "ry"})
    public String setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);
        return "redirect:mdaPayrollAnalysisExcel.do?rm=" +pRunMonth + "&ry=" +pRunYear+"&mid=0";
    }
    @RequestMapping(method = {RequestMethod.GET}, params = {"rm", "ry", "mid"})
    public String setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, @RequestParam("mid") Long pMdaId, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);


        int noOfEmpWivNegPay = IppmsUtilsExt.countNoOfNegativePay(IppmsUtils.getPaycheckClass(bc), genericService, bc);

        if (noOfEmpWivNegPay > 0) {
            return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + sDate.getMonthValue() + "&ry=" + sDate.getYear();
        }
        PaginationBean paginationBean = getPaginationInfo(request);
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runMonth", sDate.getMonthValue()));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runYear", sDate.getYear()));
        if(IppmsUtils.isNotNullAndGreaterThanZero(pMdaId))
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("mdaDeptMap.mdaInfo.id",pMdaId));

        List<?> empBeanList = this.genericService.loadPaginatedObjects(IppmsUtils.getPaycheckClass(bc), predicateBuilder.getPredicates(),
                (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());


        int wNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder,
                IppmsUtils.getPaycheckClass(bc));

        PayrollSummaryBean pBSB = new PayrollSummaryBean((List<AbstractPaycheckEntity>) empBeanList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        pBSB.setShowUnionDues(PayrollBeanUtils.isUnionDuesDeducted(sDate));
        pBSB.setShowRow(SHOW_ROW);
        pBSB.setEmployeeId(0L);
        pBSB.setId(bc.getBusinessClientInstId());
        pBSB.setCompanyName(bc.getBusinessName());
        pBSB.setRunMonth(pRunMonth);
        pBSB.setRunYear(pRunYear);
        if(IppmsUtils.isNotNullAndGreaterThanZero(pMdaId)) {
            pBSB.setMdaInstId(pMdaId);
            pBSB.setMdaName(this.genericService.loadObjectById(MdaInfo.class,pMdaId).getName());
        }else
            pBSB.setMdaInstId(0L);
        pBSB.setFromDateAsString(PayrollBeanUtils.getJavaDateAsString(sDate));
        pBSB.setToDateAsString(PayrollBeanUtils.getJavaDateAsString(sDate));
        pBSB.setFromDate(sDate);
        pBSB.setToDate(sDate);
        addRoleBeanToModel(model, request);
        model.addAttribute("paystubSummary", pBSB);

        return VIEW;
    }
    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_updateReport", required = false) String updRep, @ModelAttribute("paystubSummary") PayrollSummaryBean ppDMB,
                                BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);


        if (isButtonTypeClick(request, REQUEST_PARAM_CLOSE)) {
            return REDIRECT_TO_DASHBOARD;
        }
            if ((ppDMB.getRunMonth() == -1) || (ppDMB.getRunYear() == 0)) {
                return "redirect:mdaPayrollAnalysisExcel.do";
            }
            Long pMdaId = 0L;
            if(IppmsUtils.isNotNullAndGreaterThanZero(ppDMB.getMdaInstId()))
                pMdaId = ppDMB.getMdaInstId();

            return "redirect:mdaPayrollAnalysisExcel.do?rm=" + ppDMB.getRunMonth() + "&ry=" + ppDMB.getRunYear()+"&mid="+pMdaId;

    }
}