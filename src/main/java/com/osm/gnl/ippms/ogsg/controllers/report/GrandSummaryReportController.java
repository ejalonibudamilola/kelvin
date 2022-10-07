package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.PaycheckDeductionService;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.GrandSummaryExcelGenerator;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.GrandSummaryPdfGenerator;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductGarnMiniBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductionDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckDeduction;
import com.osm.gnl.ippms.ogsg.payroll.utils.CurrencyWordGenerator;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class GrandSummaryReportController extends BaseController {

    @Autowired
    PaycheckService paycheckService;

    @Autowired
    PaycheckDeductionService paycheckDeductionService;

    @RequestMapping({"/grandSummaryReport.do"})
    public void setupForm(@RequestParam("rm") int pRunMonth,
                          @RequestParam("ry") int pRunYear,
                          @RequestParam("rt") int reportType,
                          Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        GrandSummaryExcelGenerator simpleExcelReportGeneratorClass = new GrandSummaryExcelGenerator();

        GrandSummaryPdfGenerator grandSummaryPdfGenerator = new GrandSummaryPdfGenerator();

        BusinessCertificate certificate = getBusinessCertificate(request);

        LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
        LocalDate eDate = PayrollBeanUtils.getEndOfMonth(sDate);
        String pSDate = PayrollBeanUtils.getJavaDateAsString(sDate);
        String pEDate = PayrollBeanUtils.getJavaDateAsString(eDate);

        DeductionDetailsBean deductDetails = new DeductionDetailsBean();
        HashMap<Long, DeductGarnMiniBean> deductionBean = new HashMap<Long, DeductGarnMiniBean>();

        deductDetails.setFromDate(sDate);
        deductDetails.setToDate(eDate);
        deductDetails.setRunMonth(pRunMonth);
        deductDetails.setRunYear(pRunYear);

        deductDetails.setCurrentDeduction("All Deductions");

        deductDetails.setFromDateStr(pSDate);
        deductDetails.setToDateStr(pEDate);

        LocalDate wSDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);


        List <PaycheckDeduction> paycheckDeductions = this.paycheckDeductionService.loadEmpDeductionsByPeriod(wSDate.getMonthValue(),wSDate.getYear(), certificate);

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
        deductDetails.setTotalCurrentDeduction(this.paycheckDeductionService.getTotalDeductions(null, wSDate.getMonthValue(), wSDate.getYear(), certificate));
        deductDetails.setNoOfEmployees(this.paycheckDeductionService.getNoOfEmployeeWithDeductions(null,wSDate.getMonthValue(), wSDate.getYear(), certificate));

        //List <EmpDeductionType>deductionListFiltered = this.genericService.loadAllObjectsWithSingleCondition(EmpDeductionType.class,getBusinessClientPredicate(request), "description");

        deductDetails.setDeductionMiniBean(EntityUtils.getDeductionList(deductionBean));
        deductDetails.setTotalGross(emp.getArrears()+ emp.getMonthlyBasic() + emp.getOtherArrears());
        deductDetails.setDeductionMiniBean(EntityUtils.setFormDisplayStyleOther(deductDetails.getDeductionMiniBean()));
        deductDetails.setNetPay(deductDetails.getTotalGross() - deductDetails.getTotalCurrentDeduction());
        deductDetails.setAmountInWords(CurrencyWordGenerator.getInstance().convertToWords(deductDetails.getNetPay()));
        List<Map<String, Object>> OtherDeductionList = new ArrayList<>();


        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(certificate.getBusinessName() + " Grand Summary");
        mainHeaders.add("For The Month Of : " + PayrollBeanUtils.getMonthNameFromInteger(pRunMonth)+", " +pRunYear);

        rt.setBusinessCertificate(certificate);
        rt.setGroupBy(null);
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(OtherDeductionList);
        rt.setEmployeePayBean(emp);
        rt.setDeductionDetailsBean(deductDetails);

        if(reportType == 1){
            rt.setReportTitle(certificate.getBusinessName() + " Grand Summary");
            simpleExcelReportGeneratorClass.getExcel(response, rt);
        }
        else{
            rt.setOutputInd(true);
            rt.setWatermark(certificate.getBusinessName()+" Grand Summary");
            rt.setReportTitle(certificate.getBusinessName() + "_Grand_Summary");
            grandSummaryPdfGenerator.getPdf(response, request, rt);
        }

    }
}
