package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckDeductionEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.ContributoryPensionService;
import com.osm.gnl.ippms.ogsg.base.services.PensionService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.GroupedDataExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SimpleExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.PdfSimpleGeneratorClass;
import com.osm.gnl.ippms.ogsg.domain.beans.SalarySummaryBean;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PayrollSummaryBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpDeductMiniBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;


@Controller
public class ContributoryPensionReportExcel extends BaseController {

    @Autowired
    ContributoryPensionService contributoryPensionService;

    @Autowired
    PensionService pensionService;

    @RequestMapping({"/pensionReportByMdaExcel.do"})
    public void generateContributoryPensionByMDA(@RequestParam("sDate") String pSDate, @RequestParam("eDate") String pToDate, @RequestParam("pid") Long pPfaId,
                                                 @RequestParam("mdaid") Long pMdaId,
                                                 @RequestParam("eid") Long pEmpId, @RequestParam("rt") int reportType,
                                                 Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        PdfSimpleGeneratorClass pdfSimpleGeneratorClass = new PdfSimpleGeneratorClass();

        ReportGeneratorBean rt = new ReportGeneratorBean();

        BusinessCertificate bc = getBusinessCertificate(request);

        PayrollSummaryBean p = new PayrollSummaryBean();
        boolean wMustSum = false;

        LocalDate wSDate = PayrollBeanUtils.setDateFromString(pSDate);
        LocalDate wToDate = PayrollBeanUtils.setDateFromString(pToDate);


        List<EmpDeductMiniBean> empBeanList = this.contributoryPensionService.loadPensionContributions(0, 0,
                null, null, wSDate, wToDate, pPfaId, pMdaId, wMustSum, pEmpId, true, bc);

        Collections.sort(empBeanList, Comparator.comparing(EmpDeductMiniBean::getDeductionCode).thenComparing(EmpDeductMiniBean::getName));
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i=0;
        List<Map<String, Object>> contPenMapped = new ArrayList<>();
        for (EmpDeductMiniBean data : empBeanList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTitle(), data.getEmployeeId());
            newData.put(bc.getStaffTypeName(), data.getName());
            newData.put("P.F.A", data.getDeductionCode());
            newData.put("PIN CODE", data.getDetails());
            newData.put("Amount Deducted", data.getCurrentDeduction());
            newData.put(bc.getMdaTitle(), data.getMdaDeptMap().getMdaInfo().getName());
            uniqueSet.put(data.getMdaDeptMap().getMdaInfo().getName(), i++);
            contPenMapped.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean("P.F.A", 0));
        tableHeaders.add(new ReportGeneratorBean("PIN CODE", 0));
        tableHeaders.add(new ReportGeneratorBean("Amount Deducted", 2));
        tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherSpecHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add("Pension Contribution for "+wSDate.getMonth() +", "+wSDate.getYear());

        List<String> mainHeaders2 = new ArrayList<>();

        mainHeaders2.add("Pay Period: "+ wSDate.getMonth() +", "+wSDate.getYear());

        rt.setBusinessCertificate(bc);
        rt.setGroupBy(bc.getMdaTitle());
        rt.setReportTitle("Contributory Pension Report");
        rt.setMainHeaders(mainHeaders);
        rt.setMainHeaders2(mainHeaders2);
        rt.setSubGroupBy(null);
        rt.setWatermark(bc.getBusinessName()+" Contributory Pension");
        rt.setGroupedKeySet(uniqueSet.keySet());
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(OtherSpecHeaders);
        rt.setTableType(1);
        rt.setTotalInd(1);

        if(reportType == 1) {
            simpleExcelReportGenerator.getExcel(response, request, rt);
        }
        else{
            rt.setOutputInd(true);
            pdfSimpleGeneratorClass.getPdf(response, request, rt);
        }
    }

    @RequestMapping({"/pensionReportByPFAExcel.do"})
    public void generateContributoryPensionByPFA(@RequestParam("sDate") String pSDate, @RequestParam("eDate") String pToDate, @RequestParam("pid") Long pPfaId,
            @RequestParam("mdaid") Long pMdaId,
            @RequestParam("eid") Long pEmpId,
            Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = getBusinessCertificate(request);

        GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();

        ReportGeneratorBean rt = new ReportGeneratorBean();

        PayrollSummaryBean p = new PayrollSummaryBean();

        boolean wMustSum = false;


        LocalDate wSDate = PayrollBeanUtils.setDateFromString(pSDate);
        LocalDate wToDate = PayrollBeanUtils.setDateFromString(pToDate);
        boolean genMultiple = false;
        boolean restricted = false;
        List<EmpDeductMiniBean> empBeanList  = this.contributoryPensionService.loadPensionContributions(0, 0,
                null, null,wSDate, wToDate,pPfaId,pMdaId,wMustSum,pEmpId, true, bc);


        List<Map<String, Object>> contPenMapped = new ArrayList<>();
        List<Map<String, Object>> ungroupedData = new ArrayList<>();
        List<ReportGeneratorBean> restrictedTableHeaders = new ArrayList<>();
        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        List<ReportGeneratorBean> tableSubHeaders = new ArrayList<>();
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i = 1;
        p.setStartAndEndPeriod(pSDate+" - "+pToDate);
        if(IppmsUtils.isNotNullAndGreaterThanZero(pEmpId) || IppmsUtils.isNotNullAndGreaterThanZero(pPfaId)){
            restricted = true;
            for (EmpDeductMiniBean data : empBeanList) {
                Map<String, Object> newData = new HashMap<>();
                newData.put("Pay Period", p.getStartAndEndPeriod());
                newData.put("Amount Deducted", data.getCurrentDeduction());
                newData.put("Pfa Name", data.getDeductionCode());
                contPenMapped.add(newData);
                uniqueSet.put(data.getDeductionCode(), i++);
            }

        }

        else {
            genMultiple = true;
            //First we need to get the Ungrouped Data Set.
            //This is the PFAs and the Amounts deducted.
            HashMap<String, EmpDeductMiniBean> filterMap = new HashMap<>();
            EmpDeductMiniBean empDeductMiniBean;
            for (EmpDeductMiniBean e : empBeanList) {
                if (filterMap.containsKey(e.getDeductionCode())) {
                    empDeductMiniBean = filterMap.get(p.getDeductionCode());
                    empDeductMiniBean.setCurrentDeduction(empDeductMiniBean.getCurrentDeduction() + e.getCurrentDeduction());
                    filterMap.put(p.getDeductionCode(), empDeductMiniBean);
                } else {
                    filterMap.put(p.getDeductionCode(), e);
                }
            }
            ArrayList<EmpDeductMiniBean> ungroupedDataList = new ArrayList<>(filterMap.values());

            Collections.sort(empBeanList,Comparator.comparing(EmpDeductMiniBean::getName));
            Map<String, Object> newData;
            for (EmpDeductMiniBean data : empBeanList) {
                newData = new HashMap<>();
                newData.put(bc.getStaffTitle(), data.getEmployeeId());
                newData.put(bc.getStaffTypeName(), data.getName());
                newData.put(bc.getMdaTitle(), data.getMdaName());
                newData.put("Pin Code",data.getDetails());
                newData.put("Amount Deducted", data.getCurrentDeduction());
                newData.put("Pfa Name", data.getDeductionCode());
                contPenMapped.add(newData);
                uniqueSet.put(data.getDeductionCode(), i++);
            }
            Collections.sort(ungroupedDataList, Comparator.comparing(EmpDeductMiniBean::getDeductionCode));
            for (EmpDeductMiniBean data : ungroupedDataList) {
                 newData = new HashMap<>();
                newData.put("Pfa Name", data.getDeductionCode());
                newData.put("Amount Deducted", data.getCurrentDeduction());

                ungroupedData.add(newData);

            }

        }
        List<Map<String, Object>> otherSpecHeaders = new ArrayList<>();
        List<Map<String, Object>> subTableHeaders = new ArrayList<>();
        Map<String, Object> mappedHeader;
        if(restricted){

            restrictedTableHeaders.add(new ReportGeneratorBean("Pay Period", 0));
            restrictedTableHeaders.add(new ReportGeneratorBean("Amount Deducted", 2));
            restrictedTableHeaders.add(new ReportGeneratorBean("Pfa Name", 3));

            for(ReportGeneratorBean head : restrictedTableHeaders){
                mappedHeader = new HashMap<>();
                mappedHeader.put("headerName", head.getHeaderName());
                mappedHeader.put("totalInd", head.getTotalInd());
                otherSpecHeaders.add(mappedHeader);
            }
        }else{
            tableHeaders.add(new ReportGeneratorBean("Pfa Name", 0));
            tableHeaders.add(new ReportGeneratorBean("Amount Deducted", 2));

            for(ReportGeneratorBean head : tableHeaders){
                mappedHeader = new HashMap<>();
                mappedHeader.put("headerName", head.getHeaderName());
                mappedHeader.put("totalInd", head.getTotalInd());
                otherSpecHeaders.add(mappedHeader);
            }
            tableSubHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
            tableSubHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
            tableSubHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 0));
            tableSubHeaders.add(new ReportGeneratorBean("Pin Code", 0));
            tableSubHeaders.add(new ReportGeneratorBean("Amount Deducted", 2));
            tableSubHeaders.add(new ReportGeneratorBean("Pfa Name", 3));

            for(ReportGeneratorBean head : tableSubHeaders){
                mappedHeader = new HashMap<>();
                mappedHeader.put("headerName", head.getHeaderName());
                mappedHeader.put("totalInd", head.getTotalInd());
                subTableHeaders.add(mappedHeader);
            }

        }



        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add("Pension Contribution for : "+wSDate.getMonth().getDisplayName(TextStyle.FULL,Locale.UK) +", "+wSDate.getYear());

        rt.setBusinessCertificate(bc);
        rt.setReportTitle("Detailed Contributory Pension Report");
        rt.setMainHeaders(mainHeaders);
        rt.setGroupBy("Pfa Name");
        rt.setSubGroupBy(null);
        rt.setTableHeaders(otherSpecHeaders);
        if(restricted){
            rt.setTableType(0);
            rt.setTableData(contPenMapped);
        }else{
            rt.setTableType(1);
            rt.setTableData(ungroupedData);
            rt.setTableSubData(contPenMapped);
            rt.setUseTableSubData(true);
            rt.setGroupedKeySet(uniqueSet.keySet());
            rt.setTableSubHeaders(subTableHeaders);
            rt.setGroupedKeySet(uniqueSet.keySet());
        }

        rt.setTotalInd(1);
        groupedDataExcelReportGenerator.getExcel(response, request, rt);
    }

    @RequestMapping({"/pensionEntrantReportByTCOExcel.do"})
    public void generatePensionerByEntrant(Model model, HttpServletRequest request,
                                           @RequestParam("rm") int pRunMonth,
                                           @RequestParam("ry") int pRunYear,
                                           HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = getBusinessCertificate(request);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();


        List<AbstractPaycheckEntity> penList = this.pensionService.loadPensionersByEntrantMonthAndYear(bc, pRunMonth, pRunYear);

        Collections.sort(penList,Comparator.comparing(AbstractPaycheckEntity::getMda).thenComparing(AbstractPaycheckEntity::getEmployeeName));

        List<Map<String, Object>> penSummaryList = new ArrayList<>();
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i = 1;
        for(AbstractPaycheckEntity data : penList){
            Map<String, Object> newData = new HashMap<>();
            newData.put("Pensioner ID", data.getEmployeeId());
            newData.put("Pensioner Name", data.getEmployeeName());
            newData.put("Account Number", data.getAccountNumber());
            newData.put("Monthly Pension", data.getMonthlyPension());
            newData.put("Arrears", data.getArrears());
            newData.put("Deductions", data.getTotalDeductionsStr());
            newData.put("Net Pay", data.getNetPayStr());
            newData.put("TCO", data.getMda());
            penSummaryList.add(newData);
            uniqueSet.put(data.getMda(), i++);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean("Pensioner ID", 0));
        tableHeaders.add(new ReportGeneratorBean("Pensioner Name", 0));
        tableHeaders.add(new ReportGeneratorBean("Account Number", 0));
        tableHeaders.add(new ReportGeneratorBean("Monthly Pension", 2));
        tableHeaders.add(new ReportGeneratorBean("Arrears", 2));
        tableHeaders.add(new ReportGeneratorBean("Deductions", 2));
        tableHeaders.add(new ReportGeneratorBean("Net Pay", 2));
        tableHeaders.add(new ReportGeneratorBean("TCO", 3));


        List<Map<String, Object>> penMapHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            penMapHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add("Pensioner Entrant For "+ PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth,pRunYear));

        rt.setBusinessCertificate(bc);
        rt.setGroupBy("TCO");
        rt.setReportTitle("Pensioner Entrant By TCO");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(penSummaryList);
        rt.setTableHeaders(penMapHeaders);
        rt.setTableType(1);
        rt.setTotalInd(1);
        rt.setGroupedKeySet(uniqueSet.keySet());
        groupedDataExcelReportGenerator.getExcel(response, request, rt);

    }

}
