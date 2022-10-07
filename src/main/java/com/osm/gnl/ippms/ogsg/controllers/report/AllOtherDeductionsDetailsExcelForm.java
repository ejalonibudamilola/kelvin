package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckDeductionEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.customizedpdfs.DoubleSubGroupedPdf;
import com.osm.gnl.ippms.ogsg.controllers.report.customizedpdfs.GroupedPdf;
import com.osm.gnl.ippms.ogsg.controllers.report.customizedpdfs.SubGroupedPdf;
import com.osm.gnl.ippms.ogsg.controllers.report.customizedpdfs.SubGroupedPdfExt;
import com.osm.gnl.ippms.ogsg.controllers.report.service.PaycheckDeductionService;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.GroupedDataExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.PdfSimpleGeneratorClass;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SimpleExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductGarnMiniBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductionDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.report.DeductionScheduleByTSC;
import com.osm.gnl.ippms.ogsg.domain.report.GlobalDeduction;
import com.osm.gnl.ippms.ogsg.domain.report.LoanSchedule;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PassPhrase;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpDeductMiniBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Controller
public class AllOtherDeductionsDetailsExcelForm extends BaseController {


    private final PaycheckDeductionService paycheckDeductionService;

    @Autowired
    public AllOtherDeductionsDetailsExcelForm(PaycheckDeductionService paycheckDeductionService) {
        this.paycheckDeductionService = paycheckDeductionService;
    }

    @RequestMapping({"/allOtherDeductionDetailsExcel.do"})
    public void setupForm(@RequestParam("rm") int pRunMonth,
                          @RequestParam("ry") int pRunYear, @RequestParam("pid") Long pBusClientId,
                          Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();

        BusinessCertificate certificate = getBusinessCertificate(request);

        LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
        LocalDate eDate = PayrollBeanUtils.getEndOfMonth(sDate);
        String pSDate = PayrollBeanUtils.getJavaDateAsString(sDate);
        String pEDate = PayrollBeanUtils.getJavaDateAsString(eDate);

        DeductionDetailsBean deductDetails = new DeductionDetailsBean();

        List<AbstractPaycheckDeductionEntity> paycheckDeductions;
        List<AbstractPaycheckDeductionEntity> ungroupedPaycheckDeductions;
        HashMap<String, AbstractPaycheckDeductionEntity> filterMap = new HashMap<>();
        deductDetails.setFromDate(sDate);
        deductDetails.setToDate(eDate);
        deductDetails.setRunMonth(pRunMonth);
        deductDetails.setRunYear(pRunYear);

        deductDetails.setCurrentDeduction("All Deductions");

        deductDetails.setId(pBusClientId);
        deductDetails.setFromDateStr(pSDate);
        deductDetails.setToDateStr(pEDate);
        BusinessClient bc = this.genericService.loadObjectById(BusinessClient.class, pBusClientId);
        deductDetails.setCompanyName(bc.getName());

        paycheckDeductions = this.paycheckDeductionService.loadEmpDeductionsByParentIdAndPayPeriod(null, sDate.getMonthValue(), sDate.getYear(), getBusinessCertificate(request));
        AbstractPaycheckDeductionEntity paycheckDeduction;
        for (AbstractPaycheckDeductionEntity p : paycheckDeductions) {
            if (filterMap.containsKey(p.getAbstractEmployeeEntity().getDisplayName())) {
                paycheckDeduction = filterMap.get(p.getAbstractEmployeeEntity().getDisplayName());
                paycheckDeduction.setAmount(paycheckDeduction.getAmount() + p.getAmount());
                filterMap.put(p.getAbstractEmployeeEntity().getDisplayName(), paycheckDeduction);
            } else {
                filterMap.put(p.getAbstractEmployeeEntity().getDisplayName(), p);
            }
        }
        ungroupedPaycheckDeductions = new ArrayList<>(filterMap.values());
        Collections.sort(ungroupedPaycheckDeductions, Comparator.comparing(AbstractPaycheckDeductionEntity::getEmployeeName));
        Collections.sort(paycheckDeductions, Comparator.comparing(AbstractPaycheckDeductionEntity::getEmployeeName));
        List<Map<String, Object>> otherDeductionList = new ArrayList<>();
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i = 1;
        Map<String, Object> newData;
        for (AbstractPaycheckDeductionEntity data : paycheckDeductions) {
            newData = new HashMap<>();
            newData.put(certificate.getStaffTypeName(), data.getAbstractEmployeeEntity().getDisplayName());
            newData.put("Total Withheld", data.getAmount());
            newData.put("Type", data.getEmpDedInfo().getEmpDeductionType().getDescription());
            otherDeductionList.add(newData);
            uniqueSet.put(data.getEmpDedInfo().getEmpDeductionType().getDescription(), i++);
        }
        i = 1;
        List<Map<String, Object>> mainDeductionList = new ArrayList<>();
        for (AbstractPaycheckDeductionEntity data : ungroupedPaycheckDeductions) {
            newData = new HashMap<>();
            newData.put(certificate.getStaffTypeName(), data.getAbstractEmployeeEntity().getDisplayName());
            newData.put("Total Withheld", data.getAmount());
            newData.put("Type", data.getEmpDedInfo().getEmpDeductionType().getDescription());
            mainDeductionList.add(newData);

        }
        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(certificate.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean("Total Withheld", 2));
        tableHeaders.add(new ReportGeneratorBean("Type", 3));

        List<Map<String, Object>> otherDeductionsHeaders = new ArrayList<>();
        Map<String, Object> mappedHeader;
        for (ReportGeneratorBean head : tableHeaders) {
             mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            otherDeductionsHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getName() + " Deduction Report");
        mainHeaders.add("Pay Period : " + PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth,pRunYear));

        rt.setBusinessCertificate(certificate);
        rt.setGroupBy("Type");
        rt.setReportTitle(bc.getName() + " Deduction Report");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setUseTableSubData(true);
        rt.setTableData(mainDeductionList);
        rt.setTableHeaders(otherDeductionsHeaders);
        rt.setTableSubData(otherDeductionList);
        rt.setTableSubHeaders(otherDeductionsHeaders);
        rt.setTableType(1);
        rt.setTotalInd(1);
        rt.setGroupedKeySet(uniqueSet.keySet());

        groupedDataExcelReportGenerator.getExcel(response, request, rt);
    }

    @RequestMapping({"/deductionWithDetailsExcel.do"})
    public void setupForm(@RequestParam("rm") int pRunMonth,
                          @RequestParam("ry") int pRunYear, @RequestParam("pid") Long pBusClientId,
                          @RequestParam("exp") String pFactor, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
        LocalDate eDate = PayrollBeanUtils.getEndOfMonth(sDate);
        String pSDate = PayrollBeanUtils.getJavaDateAsString(sDate);
        String pEDate = PayrollBeanUtils.getJavaDateAsString(eDate);
	    
   /* Calendar sDate = PayrollBeanUtils.setDateFromString(pSDate);
    Calendar eDate = PayrollBeanUtils.setDateFromString(pEDate);*/
        ReportGeneratorBean rt = new ReportGeneratorBean();

        GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();

        BusinessCertificate certificate = getBusinessCertificate(request);

        DeductionDetailsBean deductDetails = new DeductionDetailsBean();

        List<AbstractPaycheckDeductionEntity> paycheckDeductions;
        List<AbstractPaycheckDeductionEntity> ungroupedPaycheckDeductions;
        HashMap<String, AbstractPaycheckDeductionEntity> filterMap = new HashMap<>();
        deductDetails.setFromDate(sDate);
        deductDetails.setToDate(eDate);

        deductDetails.setCurrentDeduction("All Deductions");

        deductDetails.setId(pBusClientId);
        deductDetails.setFromDateStr(pSDate);
        deductDetails.setToDateStr(pEDate);
        BusinessClient bc = this.genericService.loadObjectById(BusinessClient.class, pBusClientId);
        deductDetails.setCompanyName(bc.getName());

        paycheckDeductions = this.paycheckDeductionService.loadEmpDeductionsByParentIdAndPayPeriod(null, sDate.getMonthValue(), sDate.getYear(), getBusinessCertificate(request));

        AbstractPaycheckDeductionEntity paycheckDeduction;
        for (AbstractPaycheckDeductionEntity p : paycheckDeductions) {
            if (filterMap.containsKey(p.getAbstractEmployeeEntity().getDisplayName())) {
                paycheckDeduction = filterMap.get(p.getAbstractEmployeeEntity().getDisplayName());
                paycheckDeduction.setAmount(paycheckDeduction.getAmount() + p.getAmount());
                filterMap.put(p.getAbstractEmployeeEntity().getDisplayName(), paycheckDeduction);
            } else {
                filterMap.put(p.getAbstractEmployeeEntity().getDisplayName(), p);
            }
        }
        ungroupedPaycheckDeductions = new ArrayList<>(filterMap.values());
        Collections.sort(ungroupedPaycheckDeductions, Comparator.comparing(AbstractPaycheckDeductionEntity::getEmployeeName));
        List<Map<String, Object>> OtherDeductionList = new ArrayList<>();
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i = 1;
        Collections.sort(paycheckDeductions, Comparator.comparing(AbstractPaycheckDeductionEntity::getEmployeeName));
        for (AbstractPaycheckDeductionEntity data : paycheckDeductions) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(certificate.getStaffTitle(), data.getAbstractEmployeeEntity().getEmployeeId());
            newData.put(certificate.getStaffTypeName(), data.getAbstractEmployeeEntity().getDisplayName());
            newData.put("Amount Withheld", data.getAmount());
            newData.put("Deduction Type", data.getEmpDedInfo().getEmpDeductionType().getDescription());
            OtherDeductionList.add(newData);
            uniqueSet.put(data.getEmpDedInfo().getEmpDeductionType().getDescription(), i++);
        }

        List<Map<String, Object>> mainDeductionList = new ArrayList<>();
        for (AbstractPaycheckDeductionEntity data : ungroupedPaycheckDeductions) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(certificate.getStaffTitle(), data.getAbstractEmployeeEntity().getEmployeeId());
            newData.put(certificate.getStaffTypeName(), data.getAbstractEmployeeEntity().getDisplayName());
            newData.put("Amount Withheld", data.getAmount());
            newData.put("Deduction Type", data.getEmpDedInfo().getEmpDeductionType().getDescription());
            mainDeductionList.add(newData);

        }
        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(certificate.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(certificate.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean("Amount Withheld", 2));
        tableHeaders.add(new ReportGeneratorBean("Deduction Type", 3));

        List<Map<String, Object>> otherDeductionsHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : tableHeaders) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            otherDeductionsHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getName() + " Detailed Deduction Report");
        mainHeaders.add("Pay Period : "+PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth,pRunYear));

        rt.setBusinessCertificate(certificate);
        rt.setGroupBy("Deduction Type");
        rt.setReportTitle(bc.getName() + " Detailed Deduction Report");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setUseTableSubData(true);
        rt.setTableData(mainDeductionList);
        rt.setTableHeaders(otherDeductionsHeaders);
        rt.setTableSubData(OtherDeductionList);
        rt.setTableSubHeaders(otherDeductionsHeaders);
        rt.setTableType(1);
        rt.setTotalInd(1);
        rt.setGroupedKeySet(uniqueSet.keySet());


        groupedDataExcelReportGenerator.getExcel(response, request, rt);
    }


    @RequestMapping({"/deductionByMdaWithDetailsExcel.do"})
    public void setupForm(@RequestParam("rm") int pRunMonth,
                          @RequestParam("ry") int pRunYear, Model model, HttpServletRequest request, HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = getBusinessCertificate(request);

        LocalDate fromDate = LocalDate.of(pRunYear, pRunMonth, 1);

        List<EmpDeductMiniBean> empBeanList = this.paycheckDeductionService.loadEmpDeductMiniBeanByFromDateToDate(fromDate, bc);


        List<Map<String, Object>> otherDeductionList = new ArrayList<>();
        List<Map<String, Object>> mainDeductionList = new ArrayList<>();
        List<EmpDeductMiniBean> ungroupedList;
        HashMap<String, EmpDeductMiniBean> filterMap = new HashMap<>();
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        EmpDeductMiniBean empDeductMiniBean;
        int i = 1;
        Collections.sort(empBeanList, Comparator.comparing(EmpDeductMiniBean::getName));
        for (EmpDeductMiniBean data : empBeanList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTypeName(), data.getName());
            newData.put(bc.getStaffTitle(), data.getEmployeeId());
            newData.put("Amount", data.getCurrentDeduction());
            newData.put("Deduction Type", data.getDeductionName());
            otherDeductionList.add(newData);
            uniqueSet.put(data.getDeductionName(), i++);
            if (filterMap.containsKey(data.getName())) {
                empDeductMiniBean = filterMap.get(data.getName());
                empDeductMiniBean.setCurrentDeduction(empDeductMiniBean.getCurrentDeduction() + data.getCurrentDeduction());
                filterMap.put(data.getName(), empDeductMiniBean);
            } else {
                filterMap.put(data.getName(), data);
            }
        }
        ungroupedList = new ArrayList<>(filterMap.values());
        Collections.sort(ungroupedList, Comparator.comparing(EmpDeductMiniBean::getName));
        for (EmpDeductMiniBean data : ungroupedList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTypeName(), data.getName());
            newData.put(bc.getStaffTitle(), data.getEmployeeId());
            newData.put("Amount", data.getCurrentDeduction());
            newData.put("Deduction Type", data.getDeductionName());
            mainDeductionList.add(newData);

        }
        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean("Amount", 2));
        tableHeaders.add(new ReportGeneratorBean("Deduction Type", 3));

        List<Map<String, Object>> otherDeductionsHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : tableHeaders) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            otherDeductionsHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add("Deductions For " + fromDate.getMonth().getDisplayName(TextStyle.FULL, Locale.UK) + ", " + fromDate.getYear());
        mainHeaders.add("Pay Period : "+ PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth,pRunYear));
        ReportGeneratorBean rt = new ReportGeneratorBean();
        rt.setBusinessCertificate(bc);
        rt.setGroupBy("Deduction Type");
        rt.setReportTitle(bc.getBusinessName() + " Deductions By Type");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setUseTableSubData(true);
        rt.setTableData(mainDeductionList);
        rt.setTableHeaders(otherDeductionsHeaders);
        rt.setTableSubData(otherDeductionList);
        rt.setTableSubHeaders(otherDeductionsHeaders);
        rt.setTableType(1);
        rt.setTotalInd(1);
        rt.setGroupedKeySet(uniqueSet.keySet());


        new GroupedDataExcelReportGenerator().getExcel(response, request, rt);
    }

    @RequestMapping({"/groupDeductionByMdaExcel.do"})
    public void setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, @RequestParam("mda") String pMda,
                          @RequestParam("rt") int reportType, Model model, HttpServletRequest request,
                          HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = getBusinessCertificate(request);

        LocalDate fromDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);

        List<EmpDeductMiniBean> empBeanList = this.paycheckDeductionService.loadEmpDeductMiniBeanByFromDateToDate(fromDate, bc);

        Collections.sort(empBeanList, Comparator.comparing(EmpDeductMiniBean::getMdaName).thenComparing(EmpDeductMiniBean::getDeductionName).thenComparing(EmpDeductMiniBean::getName));

        List<Map<String, Object>> otherDeductionList = new ArrayList<>();
        LinkedHashMap<String, Integer> uniqueSet = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> subUniqueSet = new LinkedHashMap<>();
         int i = 1;
        Map<String, Object> newData;
        for (EmpDeductMiniBean data : empBeanList) {
            newData = new HashMap<>();
            newData.put(bc.getStaffTypeName(), data.getName());
            newData.put(bc.getStaffTitle(), data.getEmployeeId());
            newData.put(bc.getStaffTypeName() + " Contribution", data.getCurrentDeduction());
            newData.put(bc.getMdaTitle(), data.getMdaName());
            newData.put("Deduction", data.getDeductionName());
            uniqueSet.put(data.getMdaName(), i++);
            subUniqueSet.put(data.getDeductionName(), i++);

            otherDeductionList.add(newData);

        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName() + " Contribution", 2));
        tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));
        tableHeaders.add(new ReportGeneratorBean("Deduction", 3));

        List<Map<String, Object>> otherDeductionsHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : tableHeaders) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            otherDeductionsHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getStaffTypeName() + " Deductions By " + bc.getMdaTitle() + " For " + fromDate.getMonth().getDisplayName(TextStyle.FULL, Locale.UK) + ", " + fromDate.getYear());

        List<String> mainHeaders2 = Arrays.asList("Pay Period : "+ PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth,pRunYear));

        ReportGeneratorBean rt = new ReportGeneratorBean();
        rt.setBusinessCertificate(bc);
        rt.setGroupBy(bc.getMdaTitle());
        rt.setReportTitle("Deductions_By_" + bc.getMdaTitle());
        rt.setMainHeaders(mainHeaders);
        rt.setMainHeaders2(mainHeaders2);
        rt.setSubGroupBy("Deduction");
        rt.setTableData(otherDeductionList);
        rt.setTableHeaders(otherDeductionsHeaders);
        rt.setTableType(2);
        rt.setTotalInd(1);
        rt.setGroupedKeySet(uniqueSet.keySet());
        rt.setSubGroupedKeySet(subUniqueSet.keySet());


        if (reportType == 1) {
            new GroupedDataExcelReportGenerator().getExcel(response, request, rt);
        } else {
            rt.setOutputInd(true);
            rt.setWatermark(bc.getBusinessName() + " Deductions By " + bc.getMdaTitle());
            new SubGroupedPdf().getPdf(response, request, rt);
        }

    }


    @RequestMapping({"/singleDeductionByMdaExcel.do"})
    public void setupForm(@RequestParam("did") Long dedTypeId, @RequestParam("rm") int pRunMonth,
                          @RequestParam("ry") int pRunYear, @RequestParam("rt") int reportType,
                          Model model, HttpServletRequest request,
                          HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = getBusinessCertificate(request);

        LocalDate fromDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);

        List<EmpDeductMiniBean> empBeanList = this.paycheckDeductionService.loadSingleEmpDeductMiniBeanByFromDateToDate(fromDate, bc, dedTypeId);

        Collections.sort(empBeanList, Comparator.comparing(EmpDeductMiniBean::getMdaName).thenComparing(EmpDeductMiniBean::getName));

        List<Map<String, Object>> otherDeductionList = new ArrayList<>();
        LinkedHashMap<String, Integer> uniqueSet = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> subUniqueSet = new LinkedHashMap<>();
         int i = 1;
        for (EmpDeductMiniBean data : empBeanList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTypeName(), data.getName());
            newData.put(bc.getStaffTitle(), data.getEmployeeId());
            newData.put(bc.getStaffTypeName() + " Contribution", data.getCurrentDeduction());
            newData.put(bc.getMdaTitle(), data.getMdaDeptMap().getMdaInfo().getName());
            newData.put("Deduction", data.getDeductionName());
            uniqueSet.put(data.getMdaDeptMap().getMdaInfo().getName(), i++);
            subUniqueSet.put(data.getDeductionName(), i++);

             otherDeductionList.add(newData);

        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName() + " Contribution", 2));
        tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));
        tableHeaders.add(new ReportGeneratorBean("Deduction", 3));

        List<Map<String, Object>> otherDeductionsHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : tableHeaders) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            otherDeductionsHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getStaffTypeName() + " Deductions By " + bc.getMdaTitle() + " For " + fromDate.getMonth().getDisplayName(TextStyle.FULL, Locale.UK) + ", " + fromDate.getYear());
        List<String> mainHeaders2 = Arrays.asList("Pay Period : "+ PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth,pRunYear));
        ReportGeneratorBean rt = new ReportGeneratorBean();
        rt.setBusinessCertificate(bc);
        rt.setGroupBy(bc.getMdaTitle());
        rt.setReportTitle("Deductions_By_" + bc.getMdaTitle());
        rt.setMainHeaders(mainHeaders);
        rt.setMainHeaders2(mainHeaders2);
        rt.setSubGroupBy("Deduction");
        rt.setTableData(otherDeductionList);
        rt.setTableHeaders(otherDeductionsHeaders);
        rt.setTableType(2);
        rt.setTotalInd(1);
        rt.setGroupedKeySet(uniqueSet.keySet());
        rt.setSubGroupedKeySet(subUniqueSet.keySet());


        if (reportType == 1) {
            new GroupedDataExcelReportGenerator().getExcel(response, request, rt);
        } else {
            rt.setOutputInd(true);
            rt.setWatermark(bc.getBusinessName() + " Deductions By " + bc.getMdaTitle());
            new SubGroupedPdf().getPdf(response, request, rt);
        }

    }


    @RequestMapping({"/allDeductionByBankExcel.do"})
    public void generateAllDedByBank(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear,
                                     @RequestParam("rt") int reportType, Model model, HttpServletRequest request,
                                     HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        LocalDate fromDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);

        List<EmpDeductMiniBean> empDedList = this.paycheckDeductionService.loadEmpDeductMiniBeanByPayPeriod(pRunMonth, pRunYear, bc);

        List<Map<String, Object>> otherDeductionList = new ArrayList<>();
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i = 1;
        Map<String, Object> newData;
        Collections.sort(empDedList, Comparator.comparing(EmpDeductMiniBean::getDeductionName).thenComparing(EmpDeductMiniBean::getBankBranchName));
        for (EmpDeductMiniBean data : empDedList) {
            newData = new HashMap<>();
            newData.put("Deduction Name", data.getDeductionName());
            newData.put("Deducted Amount", data.getDeductionAmount());
            newData.put("Account Number", data.getAccountNumber());
            newData.put("Bank Branch", data.getBankBranchName());
            newData.put("Bank Name", data.getBankName());
            otherDeductionList.add(newData);

            uniqueSet.put(data.getBankName(), i++);

        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean("Deduction Name", 0, 0));
        tableHeaders.add(new ReportGeneratorBean("Deducted Amount", 2, 2));
        tableHeaders.add(new ReportGeneratorBean("Account Number", 0, 0));
        tableHeaders.add(new ReportGeneratorBean("Bank Name", 0, 0));
        tableHeaders.add(new ReportGeneratorBean("Bank Branch", 3));

        List<Map<String, Object>> otherDeductionsHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : tableHeaders) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            otherDeductionsHeaders.add(mappedHeader);
        }

        tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean("Deduction Name", 0, 0));
        tableHeaders.add(new ReportGeneratorBean("Deducted Amount", 2, 2));
        tableHeaders.add(new ReportGeneratorBean("Account Number", 0, 0));
        tableHeaders.add(new ReportGeneratorBean("Bank Branch", 0, 0));
        tableHeaders.add(new ReportGeneratorBean("Bank Name", 3));
        List<Map<String, Object>> subDedHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : tableHeaders) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            subDedHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getStaffTypeName() + " Deductions For " + PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth,pRunYear));
        List<String> mainHeaders2 = Arrays.asList("Pay Period : "+ PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth,pRunYear));

        ReportGeneratorBean rt = new ReportGeneratorBean();

        rt.setBusinessCertificate(bc);
        rt.setGroupBy("Bank Name");
        rt.setReportTitle("Deductions By Bank Branches");
        rt.setMainHeaders(mainHeaders);
        rt.setMainHeaders2(mainHeaders2);
        rt.setSubGroupBy(null);
        rt.setUseTableSubData(true);
        rt.setTableData(otherDeductionList);
        rt.setTableHeaders(otherDeductionsHeaders);
        rt.setTableSubData(otherDeductionList);
        rt.setTableSubHeaders(subDedHeaders);
        rt.setTableType(1);
        rt.setTotalInd(1);
        rt.setGroupedKeySet(uniqueSet.keySet());


        if (reportType == 1) {
            new GroupedDataExcelReportGenerator().getExcel(response, request, rt);
        } else {
            rt.setWatermark(bc.getBusinessName() + " Deductions By Bank");
            rt.setOutputInd(true);
            new GroupedPdf().getPdf(response, request, rt);
        }
    }


    @RequestMapping(value = "/globalDeduction.do", method = RequestMethod.GET, params = {
            "rm", "ry"})
    public void globalDeduction(@RequestParam("rm") int pRunMonth,
                                @RequestParam("ry") int pRunYear, HttpServletRequest request,
                                HttpServletResponse response, ModelAndView mv) {

        LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);


        try {

            ReportGeneratorBean rt = new ReportGeneratorBean();
            BusinessCertificate bc = getBusinessCertificate(request);

            List<GlobalDeduction> jeds = this.paycheckDeductionService.globalDeduction(pRunMonth, pRunYear, bc);

            Collections.sort(jeds, Comparator.comparing(GlobalDeduction::getDeductionName).thenComparing(GlobalDeduction::getAgencyName).thenComparing(GlobalDeduction::getEmployeeName));
            int i = 0;
            LinkedHashMap<String, Integer> uniqueSet = new LinkedHashMap<>();
            LinkedHashMap<String, Integer> subUniqueSet = new LinkedHashMap<>();
            List<Map<String, Object>> otherDeductionList = new ArrayList<>();
            Map<String, Object> newData;
            for (GlobalDeduction data : jeds) {
                newData = new HashMap<>();
                newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                newData.put(bc.getStaffTitle(), data.getEmployeeNumber());
                newData.put(bc.getStaffTypeName() + " Contribution", data.getContributoryAmount());
                newData.put("Deduction Name", data.getDeductionName());
                newData.put(bc.getMdaTitle(), data.getAgencyName());
                uniqueSet.put(data.getDeductionName(), i++);
                subUniqueSet.put(data.getAgencyName(), i++);

                otherDeductionList.add(newData);

            }
                List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName() + " Contribution", 2));
                tableHeaders.add(new ReportGeneratorBean("Deduction Name", 3));
                tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                List<Map<String, Object>> otherDeductionsHeaders = new ArrayList<>();
                for (ReportGeneratorBean head : tableHeaders) {
                    Map<String, Object> mappedHeader = new HashMap<>();
                    mappedHeader.put("headerName", head.getHeaderName());
                    mappedHeader.put("totalInd", head.getTotalInd());
                    otherDeductionsHeaders.add(mappedHeader);
                }

                List<String> mainHeaders = new ArrayList<>();
                mainHeaders.add(bc.getStaffTypeName() + " Global Deductions");

                List<String> mainHeaders2 = Arrays.asList("Pay Period : "+ PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth,pRunYear));


                rt.setBusinessCertificate(bc);
                rt.setGroupBy("Deduction Name");
                rt.setReportTitle(bc.getBusinessName()+"_Global_Deductions_" + PayrollBeanUtils.getMonthNameAndYearForExcelNaming(pRunMonth, pRunYear, false, false)+ PassPhrase.generateFileNamingPseudo());
                rt.setMainHeaders(mainHeaders);
                rt.setMainHeaders2(mainHeaders2);
                rt.setSubGroupBy(bc.getMdaTitle());
                rt.setTableData(otherDeductionList);
                rt.setTableHeaders(otherDeductionsHeaders);
                rt.setTableType(2);
                rt.setWatermark(bc.getBusinessName() + " Global Deductions");
                rt.setTotalInd(1);
                rt.setGroupedKeySet(uniqueSet.keySet());
                rt.setSubGroupedKeySet(subUniqueSet.keySet());
                rt.setOutputInd(true);
//                rt.setNoWrap(true);
                new SubGroupedPdf().getPdf(response, request, rt);

            } catch(Exception ex){
                ex.printStackTrace();

            }
        }


        @RequestMapping(value = "/deductionByTSC.do", method = RequestMethod.GET, params = {
                "rm", "ry"})
        public void deductionBySchools ( @RequestParam("rm") int pRunMonth,
        @RequestParam("ry") int pRunYear, HttpServletRequest request,
                HttpServletResponse response, ModelAndView mv){
            try {
                LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(
                        pRunMonth, pRunYear);
                String pSDate = PayrollBeanUtils.getJavaDateAsString(sDate);

                PdfSimpleGeneratorClass pdfSimpleGeneratorClass = new PdfSimpleGeneratorClass();

                ReportGeneratorBean rt = new ReportGeneratorBean();

                BusinessCertificate bc = getBusinessCertificate(request);

                List<DeductionScheduleByTSC> jeds = this.paycheckDeductionService.deductionByTsc(pRunMonth, pRunYear, bc);
                Collections.sort(jeds,Comparator.comparing(DeductionScheduleByTSC::getEmployeeName));

                List<Map<String, Object>> otherDeductionList = new ArrayList<>();
                HashMap<String, Integer> uniqueSet = new HashMap<>();
                HashMap<String, Integer> subUniqueSet = new HashMap<>();
                HashMap<String, Integer> doubleSubGroupedUniqueSet = new HashMap<>();
                 int i = 1;
                for (DeductionScheduleByTSC data : jeds) {
                    Map<String, Object> newData = new HashMap<>();
                    newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                    newData.put(bc.getStaffTitle(), data.getEmployeeNumber());
                    newData.put(bc.getStaffTypeName() + " Contribution", data.getDeductionAmount());
                    newData.put("Agency", data.getAgencyName());
                    newData.put("Deduction", data.getDeductionName());
                    newData.put("School", data.getSchoolName());
//                    uniqueSet.put(data.getDeductionName(), i++);
//                    subUniqueSet.put(data.getSchoolName(), i++);
                    uniqueSet.put(data.getAgencyName(),  i++);
                    subUniqueSet.put(data.getSchoolName(), i++);
                    doubleSubGroupedUniqueSet.put(data.getDeductionName(), i++);
                    otherDeductionList.add(newData);
                }

                List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName() + " Contribution", 2));
                tableHeaders.add(new ReportGeneratorBean("Deduction", 3));
                tableHeaders.add(new ReportGeneratorBean("School", 3));
                tableHeaders.add(new ReportGeneratorBean("Agency", 3));

                List<Map<String, Object>> otherDeductionsHeaders = new ArrayList<>();
                for (ReportGeneratorBean head : tableHeaders) {
                    Map<String, Object> mappedHeader = new HashMap<>();
                    mappedHeader.put("headerName", head.getHeaderName());
                    mappedHeader.put("totalInd", head.getTotalInd());
                    otherDeductionsHeaders.add(mappedHeader);
                }

                List<String> mainHeaders = new ArrayList<>();
                mainHeaders.add(bc.getStaffTypeName() + " Deductions By Schools");
                List<String> mainHeaders2 = Arrays.asList("Pay Period : "+ PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth,pRunYear));

                rt.setBusinessCertificate(bc);
//                rt.setGroupBy("Deduction");
                rt.setGroupBy("Agency");
                rt.setReportTitle("deductions_by_schools");
                rt.setMainHeaders(mainHeaders);
                rt.setMainHeaders2(mainHeaders2);
                rt.setGroupedKeySet(uniqueSet.keySet());
                rt.setSubGroupedKeySet(subUniqueSet.keySet());
                rt.setDoubleSubGroupedKeySet(doubleSubGroupedUniqueSet.keySet());
                rt.setWatermark("Global Deductions By School");
                rt.setSubGroupBy("School");
                rt.setDoubleSubGroupBy("Deduction");
                rt.setTableData(otherDeductionList);
                rt.setTableHeaders(otherDeductionsHeaders);
//                rt.setTableType(2);
                rt.setTableType(3);
                rt.setTotalInd(1);
                rt.setOutputInd(true);


//                new SubGroupedPdf().getPdf(response, request, rt);

                new DoubleSubGroupedPdf().getPdf(response, request, rt);


            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }


        @RequestMapping(value = "/singleDeductionByTSC.do", method = RequestMethod.GET, params = {
                "rm", "ry", "did"})
        public void deductionBySchools ( @RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear,
        @RequestParam("did") Long dedTypeId, HttpServletRequest request,
                HttpServletResponse response, ModelAndView mv){
            try {
//            LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(
//                    pRunMonth, pRunYear);
//            String pSDate = PayrollBeanUtils.getJavaDateAsString(sDate);

//            PdfSimpleGeneratorClass pdfSimpleGeneratorClass = new PdfSimpleGeneratorClass();

                ReportGeneratorBean rt = new ReportGeneratorBean();

                BusinessCertificate bc = getBusinessCertificate(request);

                List<DeductionScheduleByTSC> jeds = this.paycheckDeductionService.singleDeductionByTsc(pRunMonth, pRunYear, bc, dedTypeId);


                List<Map<String, Object>> otherDeductionList = new ArrayList<>();
                HashMap<String, Integer> uniqueSet = new HashMap<>();
                HashMap<String, Integer> subUniqueSet = new HashMap<>();
                 int i = 1;
                for (DeductionScheduleByTSC data : jeds) {
                    Map<String, Object> newData = new HashMap<>();
                    newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                    newData.put(bc.getStaffTitle(), data.getEmployeeNumber());
                    newData.put(bc.getStaffTypeName() + " Contribution", data.getDeductionAmount());
                    newData.put("Deduction", data.getDeductionName());
                    newData.put("School", data.getSchoolName());
                    uniqueSet.put(data.getDeductionName(), i++);
                    subUniqueSet.put(data.getSchoolName(), i++);


                    otherDeductionList.add(newData);
                }

                List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName() + " Contribution", 2));
                tableHeaders.add(new ReportGeneratorBean("Deduction", 3));
                tableHeaders.add(new ReportGeneratorBean("School", 3));

                List<Map<String, Object>> otherDeductionsHeaders = new ArrayList<>();
                for (ReportGeneratorBean head : tableHeaders) {
                    Map<String, Object> mappedHeader = new HashMap<>();
                    mappedHeader.put("headerName", head.getHeaderName());
                    mappedHeader.put("totalInd", head.getTotalInd());
                    otherDeductionsHeaders.add(mappedHeader);
                }

                List<String> mainHeaders = new ArrayList<>();
                mainHeaders.add(bc.getStaffTypeName() + " Deductions By Schools");
                List<String> mainHeaders2 = Arrays.asList("Pay Period : "+ PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth,pRunYear));

                rt.setBusinessCertificate(bc);
                rt.setGroupBy("Deduction");
                rt.setReportTitle("deductions_by_schools");
                rt.setMainHeaders(mainHeaders);
                rt.setMainHeaders2(mainHeaders2);
                rt.setGroupedKeySet(uniqueSet.keySet());
                rt.setSubGroupedKeySet(subUniqueSet.keySet());
                rt.setWatermark("Global Deductions By School");
                rt.setSubGroupBy("School");
                rt.setTableData(otherDeductionList);
                rt.setTableHeaders(otherDeductionsHeaders);
                rt.setTableType(2);
                rt.setTotalInd(1);
                rt.setOutputInd(true);


                new SubGroupedPdf().getPdf(response, request, rt);


            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }


    @RequestMapping({"/otherDeductionDetailsExcel.do"})
    public void otherDeductionDetailsExcel (@RequestParam("did") Long dedId,@RequestParam("rm") int pRunMonth,
                                            @RequestParam("ry") int pRunYear, @RequestParam("pid") Long pBusClientId,
                                            @RequestParam("rt") int reportType, Model model, HttpServletRequest request, HttpServletResponse response) throws
            Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bCert = getBusinessCertificate(request);

        LocalDate fromDate  = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);


        GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();


        List<AbstractPaycheckDeductionEntity> paycheckDeductions;
        EmpDeductionType wEdt = this.genericService.loadObjectUsingRestriction(EmpDeductionType.class,
                Arrays.asList(CustomPredicate.procurePredicate("id", dedId), CustomPredicate.procurePredicate("businessClientId", bCert.getBusinessClientInstId())));

        paycheckDeductions = this.paycheckDeductionService.loadEmpDeductionsByParentIdAndPayPeriod(dedId, fromDate.getMonthValue(), fromDate.getYear(), bCert);

        LinkedHashMap<String, Integer> uniqueSet = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> subUniqueSet = new LinkedHashMap<>();

        List<Map<String, Object>> garnSummaryList = new ArrayList<>();
        Collections.sort(paycheckDeductions,Comparator.comparing(AbstractPaycheckDeductionEntity::getName).thenComparing(AbstractPaycheckDeductionEntity::getEmployeeName));
        int i = 0;
        for (AbstractPaycheckDeductionEntity data : paycheckDeductions) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bCert.getStaffTitle(), data.getAbstractEmployeeEntity().getEmployeeId());
            newData.put(bCert.getStaffTypeName(), data.getAbstractEmployeeEntity().getDisplayName());
            newData.put(bCert.getMdaTitle(), data.getName());
            newData.put("Amount", data.getAmount());
            newData.put("Deduction Name", data.getEmpDedInfo().getEmpDeductionType().getName());

            uniqueSet.put(data.getEmpDedInfo().getEmpDeductionType().getName(), i++);
            subUniqueSet.put(data.getName(), i++);
            garnSummaryList.add(newData);

        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bCert.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bCert.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean("Amount", 2));
        tableHeaders.add(new ReportGeneratorBean(bCert.getMdaTitle(), 3));
        tableHeaders.add(new ReportGeneratorBean("Deduction Name", 3));

        //tableHeaders.add(new ReportGeneratorBean("Account Number", 0));

        List<Map<String, Object>> garnMapHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : tableHeaders) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            garnMapHeaders.add(mappedHeader);
        }


//            List<String> mainHeaders = new ArrayList<>();
//            mainHeaders.add("Single Deductions");

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(" "+ wEdt.getDescription()+" Deduction");
        List<String> mainHeaders2 = new ArrayList<>();

        mainHeaders2.add("Pay Period: "+ PayrollHRUtils.getMonthYearDateFormat().format(PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear)));
        ReportGeneratorBean rt = new ReportGeneratorBean();
        rt.setBusinessCertificate(bCert);
        rt.setMainHeaders(mainHeaders);
        rt.setMainHeaders2(mainHeaders2);
        rt.setTableData(garnSummaryList);
        rt.setTableHeaders(garnMapHeaders);
        rt.setTotalInd(1);



        if (reportType == 1) {
            rt.setGroupedKeySet(subUniqueSet.keySet());
            rt.setGroupBy(bCert.getMdaTitle());
            rt.setSubGroupBy(null);
            rt.setTableType(1);
//                rt.setReportTitle("Single Deduction Report");
//                simpleExcelReportGenerator.getExcel(response, request, rt);
            rt.setReportTitle("Detailed_Deductions_For_"+wEdt.getName()+"_"+PayrollBeanUtils.getCurrentTime(true));
//            new SimpleExcelReportGenerator().getExcel(response, request, rt);
            groupedDataExcelReportGenerator.getExcel(response, request, rt);

        } else {
            rt.setGroupedKeySet(uniqueSet.keySet());
            rt.setSubGroupedKeySet(subUniqueSet.keySet());
            rt.setGroupBy("Deduction Name");
            rt.setSubGroupBy(bCert.getMdaTitle());
            rt.setTableType(2);
            rt.setOutputInd(true);
//                rt.setReportTitle("Single_Deduction_Report");
//                rt.setWatermark(bCert.getBusinessName() + " Deductions");
//                pdfSimpleGeneratorClass.getPdf(response, request, rt);
            rt.setReportTitle("Detailed_Deduction_Report_By_Deduction_Type");
            rt.setWatermark(bCert.getBusinessName()+" Deductions By "+bCert.getMdaTitle());
//            new PdfSimpleGeneratorClass().getPdf(response, request, rt);
            new SubGroupedPdf().getPdf(response, request, rt);
        }
    }



}