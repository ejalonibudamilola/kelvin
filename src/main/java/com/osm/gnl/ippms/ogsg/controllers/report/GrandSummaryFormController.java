package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.PaycheckDeductionService;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductGarnMiniBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductionDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckDeduction;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRun;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping({ "/grandSummary.do" })
@SessionAttributes(types={DeductionDetailsBean.class})
public class GrandSummaryFormController extends BaseController {

    @Autowired
    PaycheckService paycheckService;

    @Autowired
    PaycheckDeductionService paycheckDeductionService;

    @ModelAttribute("monthList")
    public List<NamedEntity> getMonthList() {
        return PayrollBeanUtils.makeAllMonthList();
    }

    private static final String VIEW_NAME = "deduction/grandSummaryReport";

    @ModelAttribute("yearList")
    public List<NamedEntity> makeYearList(HttpServletRequest request) {

        return this.paycheckService.makePaycheckYearList(getBusinessCertificate(request));
    }



    @RequestMapping(method={RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        DeductionDetailsBean deductDetails = new DeductionDetailsBean();



//    PayrollRun pf = this.payrollService.getMostRecentPayrollRunByParentId(bc.getBusinessClientInstId());
        Long pfId = this.genericService.loadMaxValueByClassClientIdAndColumn(PayrollRun.class, "id",bc.getBusinessClientInstId(),"businessClientId");

        PayrollRun pf = this.genericService.loadObjectById(PayrollRun.class,pfId);

        LocalDate wSDate = LocalDate.now();
        HashMap<Long, DeductGarnMiniBean> deductionBean = new HashMap<Long,DeductGarnMiniBean> ();
        EmployeePayBean emp = new EmployeePayBean();
        if (!pf.isNewEntity()) {
            deductDetails.setFromDate(pf.getPayPeriodStart());
            deductDetails.setToDate(pf.getPayPeriodEnd());
            deductDetails.setCurrentDeduction("All Deductions");
            deductDetails.setId(bc.getBusinessClientInstId());
            deductDetails.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(pf.getPayPeriodStart()));
            deductDetails.setToDateStr(PayrollBeanUtils.getJavaDateAsString(pf.getPayPeriodEnd()));

            wSDate = pf.getPayPeriodStart();

            int noOfEmpWivNegPay = IppmsUtilsExt.countNoOfNegativePay(IppmsUtils.getPaycheckClass(bc),genericService,bc);

            if (noOfEmpWivNegPay > 0)
            {
                return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + wSDate.getMonthValue() + "&ry=" + wSDate.getYear();
            }
            List <PaycheckDeduction> paycheckDeductions = this.paycheckDeductionService.loadEmpDeductionsByPeriod(wSDate.getMonthValue(),wSDate.getYear(), bc);

            DeductGarnMiniBean d = null;
            for (PaycheckDeduction p : paycheckDeductions) {
                emp = new EmployeePayBean();
                if (deductionBean.containsKey(p.getEmpDedInfo().getEmpDeductionType().getId())) {
                    d = deductionBean.get(p.getEmpDedInfo().getEmpDeductionType().getId());
                } else {
                    d = new DeductGarnMiniBean();
                    d.setDeductionId(p.getEmpDedInfo().getEmpDeductionType().getId());

                }
                emp.setMonthlyBasic(d.getMonthlyBasic() + p.getEmployeePayBean().getMonthlyBasic());
                emp.setArrears(d.getArrears() + p.getEmployeePayBean().getArrears());
                emp.setOtherArrears(d.getOtherArrears() + p.getEmployeePayBean().getOtherArrears());
                d.setAmount(d.getAmount() + p.getAmount());
                d.setDescription(p.getEmpDedInfo().getDescription());
                d.setName(p.getEmpDedInfo().getEmpDeductionType().getDescription() + " [ " + p.getEmpDedInfo().getEmpDeductionType().getName() + " ]");
                deductionBean.put(p.getEmpDedInfo().getEmpDeductionType().getId(), d);
                deductDetails.setTotal(deductDetails.getTotal() + p.getAmount());
            }
            deductDetails.setRunMonth(wSDate.getMonthValue());
            deductDetails.setRunYear(wSDate.getYear());
        }else{
            deductDetails.setRunMonth(-1);
            deductDetails.setRunYear(0);
        }
        deductDetails.setMonthAndYearStr(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(wSDate.getMonthValue(), wSDate.getYear()));
        deductDetails.setTotalCurrentDeduction(this.paycheckDeductionService.getTotalDeductions(null, wSDate.getMonthValue(), wSDate.getYear(), bc));
        deductDetails.setNoOfEmployees(this.paycheckDeductionService.getNoOfEmployeeWithDeductions(null,wSDate.getMonthValue(), wSDate.getYear(), bc));

        List <EmpDeductionType>deductionListFiltered = this.genericService.loadAllObjectsWithSingleCondition(EmpDeductionType.class,getBusinessClientIdPredicate(request), "description");
        //Collections.sort(deductionListFiltered, Comparator.comparing(EmpDeductionType::getDescription));
        deductDetails.setDeductionMiniBean(EntityUtils.getDeductionList(deductionBean));
        deductDetails.setTotalGross(emp.getArrears()+ emp.getMonthlyBasic() + emp.getOtherArrears());
        deductDetails.setDeductionMiniBean(EntityUtils.setFormDisplayStyleOther(deductDetails.getDeductionMiniBean()));
        deductDetails.setNetPay(deductDetails.getTotalGross() - deductDetails.getTotalCurrentDeduction());
        deductDetails.setAmountInWords(CurrencyWordGenerator.getInstance().convertToWords(deductDetails.getNetPay()));
        model.addAttribute("deductionList", deductionListFiltered);
        model.addAttribute("deductionDetails", deductDetails);
        model.addAttribute("earningsDetails", emp);
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }


    @RequestMapping(method={RequestMethod.GET}, params={"rm", "ry"})
    public String setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear,
                            Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        DeductionDetailsBean deductDetails = new DeductionDetailsBean();
        HashMap <Long,DeductGarnMiniBean>deductionBean = new HashMap<>();

        BusinessCertificate bc = getBusinessCertificate(request);

        int noOfEmpWivNegPay = IppmsUtilsExt.countNoOfNegativePay(IppmsUtils.getPaycheckClass(bc),genericService,bc);

        if (noOfEmpWivNegPay > 0)
        {
            return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + pRunMonth + "&ry=" + pRunYear;
        }

        deductDetails.setRunMonth(pRunMonth);
        deductDetails.setRunYear(pRunYear);

        LocalDate wSDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);


        List <PaycheckDeduction> paycheckDeductions = this.paycheckDeductionService.loadEmpDeductionsByPeriod(wSDate.getMonthValue(),wSDate.getYear(), bc);

        DeductGarnMiniBean d = null;
        EmployeePayBean emp = new EmployeePayBean();
        for (PaycheckDeduction p : paycheckDeductions) {
            emp = new EmployeePayBean();
            if (deductionBean.containsKey(p.getEmpDedInfo().getEmpDeductionType().getId())) {
                d = deductionBean.get(p.getEmpDedInfo().getEmpDeductionType().getId());
            } else {
                d = new DeductGarnMiniBean();
                d.setDeductionId(p.getEmpDedInfo().getEmpDeductionType().getId());

            }
            emp.setMonthlyBasic(d.getMonthlyBasic() + p.getEmployeePayBean().getMonthlyBasic());
            emp.setArrears(d.getArrears() + p.getEmployeePayBean().getArrears());
            emp.setOtherArrears(d.getOtherArrears() + p.getEmployeePayBean().getOtherArrears());
            d.setAmount(d.getAmount() + p.getAmount());
            d.setDescription(p.getEmpDedInfo().getDescription());
            d.setName(p.getEmpDedInfo().getEmpDeductionType().getDescription() + " [ " + p.getEmpDedInfo().getEmpDeductionType().getName() + " ]");
            deductionBean.put(p.getEmpDedInfo().getEmpDeductionType().getId(), d);
            deductDetails.setTotal(deductDetails.getTotal() + p.getAmount());
        }
        deductDetails.setRunMonth(wSDate.getMonthValue());
        deductDetails.setRunYear(wSDate.getYear());

        deductDetails.setMonthAndYearStr(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(wSDate.getMonthValue(), wSDate.getYear()));
        deductDetails.setTotalCurrentDeduction(this.paycheckDeductionService.getTotalDeductions(null, wSDate.getMonthValue(), wSDate.getYear(), bc));
        deductDetails.setNoOfEmployees(this.paycheckDeductionService.getNoOfEmployeeWithDeductions(null,wSDate.getMonthValue(), wSDate.getYear(), bc));

    List <EmpDeductionType>deductionListFiltered = this.genericService.loadAllObjectsWithSingleCondition(EmpDeductionType.class,getBusinessClientIdPredicate(request), "description");
    //Collections.sort(deductionListFiltered, Comparator.comparing(EmpDeductionType::getDescription));
        deductDetails.setDeductionMiniBean(EntityUtils.getDeductionList(deductionBean));
        deductDetails.setTotalGross(emp.getArrears()+ emp.getMonthlyBasic() + emp.getOtherArrears());
        deductDetails.setDeductionMiniBean(EntityUtils.setFormDisplayStyleOther(deductDetails.getDeductionMiniBean()));
        deductDetails.setNetPay(deductDetails.getTotalGross() - deductDetails.getTotalCurrentDeduction());
        deductDetails.setAmountInWords(CurrencyWordGenerator.getInstance().convertToWords(deductDetails.getNetPay()));
        model.addAttribute("deductionList", deductionListFiltered);
        model.addAttribute("deductionDetails", deductDetails);
        model.addAttribute("earningsDetails", emp);
    addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }

    @RequestMapping(method={RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep,
                                @RequestParam(value="_cancel", required=false) String cancel,
                                @RequestParam(value="_go", required=false) String go, @ModelAttribute("deductionDetails") DeductionDetailsBean pDDB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
            throws Exception
    {
        SessionManagerService.manageSession(request, model);

        if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL))
        {

            return "redirect:reportsOverview.do";
        }


        if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {
            int rm = pDDB.getRunMonth();
            int ry = pDDB.getRunYear();

            return "redirect:grandSummary.do?rm=" + rm + "&ry=" + ry;
        }

        return "redirect:" + pDDB.getSubLinkSelect() + ".do";
    }


}
