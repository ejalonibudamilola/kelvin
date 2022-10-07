package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.base.services.HRService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.SchoolService;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.*;
import com.osm.gnl.ippms.ogsg.domain.beans.MPBAMiniBean;
import com.osm.gnl.ippms.ogsg.domain.hr.MDAPMiniBeanHolder;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.DevelopmentLevy;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.FuturePaycheckMaster;
import com.osm.gnl.ippms.ogsg.domain.subvention.SubventionHistory;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.MdaType;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.pagination.beans.PayrollSummaryBean;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import com.osm.gnl.ippms.ogsg.report.beans.WageBeanContainer;
import com.osm.gnl.ippms.ogsg.report.beans.WageSummaryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.*;

@Controller
public class PayrollManagerReportSummaryFormExt extends BaseController {

    /**
     * Kasumu Taiwo
     * 12-2020
     */

    // @Autowired
    //BankService bankService;

    private final SchoolService schoolService;
    private final PaycheckService paycheckService;
    private final EmployeeService employeeService;
    private final PayrollService payrollService;
    private final HRService hrService;

    @Autowired
    public PayrollManagerReportSummaryFormExt(SchoolService schoolService, PaycheckService paycheckService, EmployeeService employeeService, PayrollService payrollService, HRService hrService) {
        this.schoolService = schoolService;
        this.paycheckService = paycheckService;
        this.employeeService = employeeService;
        this.payrollService = payrollService;
        this.hrService = hrService;
    }

    private HashMap<Long, WageSummaryBean> ministryMap;
    private HashMap<Long, WageSummaryBean> subventionMap;
    private HashMap<Long, SalaryInfo> salaryInfoMap;
    private HashMap<Long, MdaInfo> mapAgencyMap;

    private void init() {
        this.mapAgencyMap = new HashMap<>();
        this.subventionMap = new HashMap<>();
        this.ministryMap = new HashMap<>();
        this.salaryInfoMap = new HashMap<>();
    }


    private void makeAgencyHashMapFromList(List<MdaDeptMap> pList) {
        for (MdaDeptMap a : pList)
            this.mapAgencyMap.put(a.getId(), a.getMdaInfo());
    }


    @RequestMapping({"/compExecSummaryReportExcel.do"})
    public void setupForm(@RequestParam("sDate") String fD, @RequestParam("eDate") String tD, @RequestParam("ph") int pInt, Model model, HttpServletResponse response, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        LocalDate fDate = PayrollBeanUtils.setDateFromString(fD);
        LocalDate tDate = PayrollBeanUtils.setDateFromString(tD);
        init();
        setControlMaps(request);

        WageBeanContainer wBEOB = new WageBeanContainer();

        wBEOB.setMonthAndYearStr(PayrollHRUtils.getMonthYearDateFormat().format(fDate));
        List<WageSummaryBean> wRetList = new ArrayList<>();
        HashMap<Integer, WageSummaryBean> wWorkBean = new HashMap<>();

        List<EmployeePayBean> wEPBList = this.paycheckService.loadEmployeePayBeanByParentIdFromDateToDate(bc.getBusinessClientInstId(), fDate.getMonthValue(), tDate.getYear(), bc, null, false);
        wBEOB.setTotalNoOfEmp(wEPBList.size());
        WageSummaryBean wMinistryBean = new WageSummaryBean();
        wMinistryBean.setAssignedToObject("Ministries");
        WageSummaryBean wAgencyBean = new WageSummaryBean();
        wAgencyBean.setAssignedToObject("Agencies");
        WageSummaryBean wParastatals = new WageSummaryBean();
        wParastatals.setAssignedToObject("Parastatals");
        WageSummaryBean wBoardBean = new WageSummaryBean();
        wBoardBean.setAssignedToObject("Boards");
        WageSummaryBean wLGEABean = new WageSummaryBean();
        wLGEABean.setAssignedToObject("TCO");
        WageSummaryBean wTCOBean = new WageSummaryBean();
        wTCOBean.setAssignedToObject("TCO");
        wWorkBean.put(1, wAgencyBean);
        wWorkBean.put(2, wBoardBean);
        wWorkBean.put(3, wMinistryBean);
        wWorkBean.put(4, wParastatals);
        wWorkBean.put(5, wLGEABean);
        wWorkBean.put(6, wTCOBean);

        wBEOB.setTotalNoOfEmp(wEPBList.size());
        HashMap<Long, Long> wAgencyMap = new HashMap<>();
        WageSummaryBean wWSB = null;
        for (EmployeePayBean e : wEPBList) {


            MdaInfo wAgency = this.mapAgencyMap.get(e.getMdaDeptMap().getId());

            switch (wAgency.getMdaType().getMdaTypeCode()) {
                case 1: //Agency
                    wAgencyBean = this.processWageBean(wAgencyBean, wAgency.getId(), wAgencyMap, e);
                    break;
                case 2://Board
                    wBoardBean = this.processWageBean(wBoardBean, wAgency.getId(), wAgencyMap, e);
                    break;

                case 3: // Ministry
                    wMinistryBean = this.processWageBean(wMinistryBean, wAgency.getId(), wAgencyMap, e);
                    break;
                case 4:
                    wParastatals = this.processWageBean(wParastatals, wAgency.getId(), wAgencyMap, e);
                    break;
            }

        }

        wRetList.add(wAgencyBean);
        wRetList.add(wBoardBean);
        wRetList.add(wParastatals);
        wRetList.add(wMinistryBean);

        //translating data to meet report generator requirement
        List<Map<String, Object>> newCompExecSummary = new ArrayList<>();
        for (WageSummaryBean data : wRetList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put("Department", data.getAssignedToObject());
            newData.put("No. Of Items", data.getNoOfItems());
            newData.put("No. Of Staff", data.getNoOfEmp());
            newData.put("Basic Salary", data.getBasicSalary());
            newData.put("Total Allowance", data.getTotalAllowance());
            newData.put("Gross Amount", data.getGrossAmount());
            newData.put("PAYE", data.getPaye());
            newData.put("Other Deductions", data.getOtherDeductions());
            newData.put("Total Deduction", data.getTotalDeductions());
            newData.put("Net Pay", data.getNetPay());
            newCompExecSummary.add(newData);
        }

        //adding table headers
        List<ReportGeneratorBean> headerList = new ArrayList<>();
        headerList.add(new ReportGeneratorBean("Department", 0));
        headerList.add(new ReportGeneratorBean("No. Of Items", 1));
        headerList.add(new ReportGeneratorBean("No. Of Staff", 1));
        headerList.add(new ReportGeneratorBean("Basic Salary", 2));
        headerList.add(new ReportGeneratorBean("Total Allowance", 2));
        headerList.add(new ReportGeneratorBean("Gross Amount", 2));
        headerList.add(new ReportGeneratorBean("PAYE", 2));
        headerList.add(new ReportGeneratorBean("Other Deductions", 2));
        headerList.add(new ReportGeneratorBean("Total Deduction", 2));
        headerList.add(new ReportGeneratorBean("Net Pay", 2));

        //translating table headers to meet report generator requirement
        List<Map<String, Object>> mappedHeadersList = new ArrayList<>();
        for (ReportGeneratorBean head : headerList) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            mappedHeadersList.add(mappedHeader);
        }

        //adding report headers
        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add("Ogun State Comprehensive Executive Payroll Report For: " + wBEOB.getMonthAndYearStr());

        //setting report bean
        ReportGeneratorBean rt = new ReportGeneratorBean();
        rt.setTableData(newCompExecSummary);
        rt.setTableHeaders(mappedHeadersList);
        rt.setBusinessCertificate(bc);
        rt.setMainHeaders(mainHeaders);
        rt.setTableType(0);
        rt.setGroupBy(null);
        rt.setSubGroupBy(null);
        rt.setReportTitle("ComprehensiveExecSummaryReport");
        rt.setTotalInd(1);

        simpleExcelReportGenerator.getExcel(response, request, rt);
    }


    private WageSummaryBean processWageBean(WageSummaryBean pSummaryBean, Long wAgencyId, HashMap<Long, Long> wAgencyMap, EmployeePayBean e) {

        if (!wAgencyMap.containsKey(wAgencyId)) {

            pSummaryBean.setNoOfItems(pSummaryBean.getNoOfItems() + 1);
            wAgencyMap.put(wAgencyId, wAgencyId);
        }
        pSummaryBean.setNoOfEmp(pSummaryBean.getNoOfEmp() + 1);
        SalaryInfo s = this.salaryInfoMap.get(e.getSalaryInfo().getId());
        pSummaryBean.setBasicSalary(pSummaryBean.getBasicSalary() + EntityUtils.convertDoubleToEpmStandard(s.getMonthlyBasicSalary() / 12.0D));

        double otherAllowance = e.getPrincipalAllowance() + e.getOtherArrears() + e.getArrears() + e.getSpecialAllowance();

        double wSalaryDiffAsDeduction = 0.0D;
        if (e.getSalaryDifference() > 0.0D)
            otherAllowance += e.getSalaryDifference();
        else {
            wSalaryDiffAsDeduction = e.getSalaryDifference() * -1.0D;
        }
        pSummaryBean.setTotalAllowance(pSummaryBean.getTotalAllowance() + otherAllowance + e.getLeaveTransportGrant() + EntityUtils.convertDoubleToEpmStandard(s.getConsolidatedAllowance() / 12.0D));
        pSummaryBean.setGrossAmount(pSummaryBean.getGrossAmount() + (s.getMonthlyBasicSalary() / 12.0D + otherAllowance + e.getLeaveTransportGrant() + EntityUtils.convertDoubleToEpmStandard(s.getConsolidatedAllowance() / 12.0D)));
        pSummaryBean.setPaye(pSummaryBean.getPaye() + e.getTaxesPaid());
        double otherDeductions = e.getNhf() + e.getUnionDues() + e.getTws() + e.getTotalDeductions() + e.getTotalGarnishments() + wSalaryDiffAsDeduction + e.getDevelopmentLevy();
        pSummaryBean.setOtherDeductions(pSummaryBean.getOtherDeductions() + otherDeductions);
        pSummaryBean.setTotalDeductions(pSummaryBean.getTotalDeductions() + (e.getTaxesPaid() + otherDeductions));
        pSummaryBean.setNetPay(pSummaryBean.getNetPay() + e.getNetPay());


        return pSummaryBean;
    }


    @RequestMapping({"/compExecSummaryReportByMDAExcel.do"})
    public void generateCompExecSummaryExcelByMda(@RequestParam("sDate") String fD, @RequestParam("eDate") String tD, @RequestParam("ph") int pInt,
                                                  Model model, HttpServletRequest request, HttpServletResponse response) throws EpmAuthenticationException, HttpSessionRequiredException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException {

        SessionManagerService.manageSession(request, model);

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        BusinessCertificate bc = super.getBusinessCertificate(request);
        init();

        LocalDate fDate = PayrollBeanUtils.setDateFromString(fD);
        LocalDate tDate = PayrollBeanUtils.setDateFromString(tD);

        setControlMaps(request);

        WageBeanContainer wBEOB = new WageBeanContainer();

        wBEOB.setMonthAndYearStr(PayrollHRUtils.getMonthYearDateFormat().format(fDate));
        List<WageSummaryBean> wRetList = new ArrayList<>();

        List<EmployeePayBean> wEPBList = this.paycheckService.loadEmployeePayBeanByParentIdFromDateToDate(bc.getBusinessClientInstId(), fDate.getMonthValue(), tDate.getYear(), bc, null, false);
        wBEOB.setTotalNoOfEmp(wEPBList.size());

        HashMap<Long, WageSummaryBean> wAgencyMap = new HashMap<>();


        for (EmployeePayBean e : wEPBList) {
            WageSummaryBean wWSB = wAgencyMap.get(e.getMdaDeptMap().getMdaInfo().getId());
            if (wWSB == null) {
                wWSB = new WageSummaryBean();

                wWSB.setAssignedToObject(e.getMdaDeptMap().getMdaInfo().getName().toUpperCase());
            }

            wWSB.setNoOfItems(wWSB.getNoOfItems() + 1);
            SalaryInfo s = this.salaryInfoMap.get(e.getSalaryInfo().getId());
            wWSB.setBasicSalary(wWSB.getBasicSalary() + EntityUtils.convertDoubleToEpmStandard(s.getMonthlyBasicSalary() / 12.0D));
            double otherAllowance = e.getPrincipalAllowance() + e.getOtherArrears() + e.getArrears() + e.getSpecialAllowance();
            double wSalaryDiffAsDeduction = 0.0D;
            if (e.getSalaryDifference() > 0.0D)
                otherAllowance += e.getSalaryDifference();
            else {
                wSalaryDiffAsDeduction = e.getSalaryDifference() * -1.0D;
            }
            wWSB.setTotalAllowance(wWSB.getTotalAllowance() + otherAllowance + e.getLeaveTransportGrant() + EntityUtils.convertDoubleToEpmStandard(s.getConsolidatedAllowance() / 12.0D));
            wWSB.setGrossAmount(wWSB.getGrossAmount() + (EntityUtils.convertDoubleToEpmStandard(s.getMonthlyBasicSalary() / 12.0D) + otherAllowance + e.getLeaveTransportGrant() + EntityUtils.convertDoubleToEpmStandard(s.getConsolidatedAllowance() / 12.0D)));
            wWSB.setPaye(wWSB.getPaye() + e.getTaxesPaid());
            double otherDeductions = e.getNhf() + e.getUnionDues() + e.getTws() + e.getTotalDeductions() + e.getTotalGarnishments() + wSalaryDiffAsDeduction + e.getDevelopmentLevy();
            wWSB.setOtherDeductions(wWSB.getOtherDeductions() + otherDeductions);
            wWSB.setTotalDeductions(wWSB.getTotalDeductions() + (e.getTaxesPaid() + otherDeductions));
            wWSB.setNetPay(wWSB.getNetPay() + e.getNetPay());
            wAgencyMap.put(e.getMdaDeptMap().getMdaInfo().getId(), wWSB);


        }

        wRetList = getWageSummaryBeanFromMap(wAgencyMap, wRetList, true);

        List<Map<String, Object>> newCompExecSummary = new ArrayList<>();
        for (WageSummaryBean data : wRetList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getMdaTitle(), data.getAssignedToObject());
            newData.put("No. Of Staff", data.getNoOfItems());
            newData.put("Basic Salary", data.getBasicSalary());
            newData.put("Total Allowance", data.getTotalAllowance());
            newData.put("Gross Amount", data.getGrossAmount());
            newData.put("PAYE", data.getPaye());
            newData.put("Other Deductions", data.getOtherDeductions());
            newData.put("Total Deduction", data.getTotalDeductions());
            newData.put("Net Pay", data.getNetPay());
            newCompExecSummary.add(newData);
        }

        //adding table headers
        List<ReportGeneratorBean> headerList = new ArrayList<>();
        headerList.add(new ReportGeneratorBean(bc.getMdaTitle(), 0));
        headerList.add(new ReportGeneratorBean("No. Of Staff", 1));
        headerList.add(new ReportGeneratorBean("Basic Salary", 2));
        headerList.add(new ReportGeneratorBean("Total Allowance", 2));
        headerList.add(new ReportGeneratorBean("Gross Amount", 2));
        headerList.add(new ReportGeneratorBean("PAYE", 2));
        headerList.add(new ReportGeneratorBean("Other Deductions", 2));
        headerList.add(new ReportGeneratorBean("Total Deduction", 2));
        headerList.add(new ReportGeneratorBean("Net Pay", 2));

        //translating table headers to meet report generator requirement
        List<Map<String, Object>> mappedHeadersList = new ArrayList<>();
        for (ReportGeneratorBean head : headerList) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            mappedHeadersList.add(mappedHeader);
        }

        //adding report headers
        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add("Ogun State Comprehensive Executive Payroll Report For: " + wBEOB.getMonthAndYearStr());

        //setting report bean
        ReportGeneratorBean rt = new ReportGeneratorBean();
        rt.setTableData(newCompExecSummary);
        rt.setTableHeaders(mappedHeadersList);
        rt.setBusinessCertificate(bc);
        rt.setMainHeaders(mainHeaders);
        rt.setTableType(0);
        rt.setGroupBy(null);
        rt.setSubGroupBy(null);
        rt.setReportTitle("ComprehensiveExecSummaryReport");
        rt.setTotalInd(1);

        simpleExcelReportGenerator.getExcel(response, request, rt);
    }


    private void setControlMaps(HttpServletRequest request) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        BusinessCertificate bc = super.getBusinessCertificate(request);


//	List<MdaDeptMap> wAgencyList = (List<MdaDeptMap>) this.payrollService.loadAllByHqlStr("from MdaDeptMap");
        List<MdaDeptMap> wAgencyList = this.genericService.loadAllObjectsWithSingleCondition(MdaDeptMap.class,
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), null);
        makeAgencyHashMapFromList(wAgencyList);

//    this.salaryInfoMap = this.payrollServiceExt.loadSalaryInfoAsMap();
        this.salaryInfoMap = this.genericService.loadObjectAsMapWithConditions(SalaryInfo.class, Arrays.asList(
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())), "id");
    }


    @RequestMapping({"/payrollSummaryByMDAPExcel.do"})
    public void generateExecutivePayrollSummary(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear,
                                                @RequestParam("rt") int ReportType, Model model,
                                                HttpServletResponse response, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        LocalDate fDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
        //Calendar tDate = PayrollBeanUtils.setDateFromString(tD);

        LocalDate wPrevMonthStart = PayrollBeanUtils.getPreviousMonthDate(fDate, false);
        LocalDate wPrevMonthEnd = PayrollBeanUtils.getPreviousMonthDate(fDate, true);
        init();
        setControlMaps(request);

        WageBeanContainer wBEOB = new WageBeanContainer();

        LocalDate wCheckDate;
        wCheckDate = wPrevMonthEnd;

        //wBEOB.setShowUnionDues(PayrollBeanUtils.isUnionDuesDeducted(wCheckDate));

        wBEOB.setMonthAndYearStr(PayrollHRUtils.getMiniMonthYearDateFormat().format(fDate));
        wBEOB.setPrevMonthAndYearStr(PayrollHRUtils.getMiniMonthYearDateFormat().format(wPrevMonthStart));

        double wTotalCurrPay = 0.0D;
        double wTotalPrevPay = 0.0D;
        int serialNum = 0;
        WageSummaryBean wTotalDeductions = new WageSummaryBean("Other Deductions");
        WageSummaryBean wTotalLoans = new WageSummaryBean("Total Loans");
        WageSummaryBean wPayee = new WageSummaryBean("Total Taxes");
        WageSummaryBean wPensions = new WageSummaryBean("Contributory Pensions (" + bc.getStaffTypeName() + " )");
        WageSummaryBean wPensionsEmployer = new WageSummaryBean("Contributory Pensions (Employer)");
        WageSummaryBean wUnionDues = null;
        if (bc.isSubeb() || bc.isPensioner()) {
            wUnionDues = new WageSummaryBean("Union Dues");
        }

        WageSummaryBean wRBA = new WageSummaryBean();

        PayrollRunMasterBean wPRMB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(getBusinessClientIdPredicate(request),
                CustomPredicate.procurePredicate("runMonth", pRunMonth), CustomPredicate.procurePredicate("runYear", pRunYear)));
        if (wPRMB.getRbaPercentage() > 0)
            wRBA.setName("Redemption Bond Account ( " + wPRMB.getRbaPercentageStr() + " Gross Pay)");


        boolean wDevLevyDeducted = false;

        LocalDate wCal = wPrevMonthEnd;

        PayrollRunMasterBean wPRMBPrev = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(getBusinessClientIdPredicate(request),
                CustomPredicate.procurePredicate("runMonth", wCal.getMonthValue()), CustomPredicate.procurePredicate("runYear", wCal.getYear())));


        DevelopmentLevy wDLI = this.genericService.loadObjectUsingRestriction(DevelopmentLevy.class, Arrays.asList(CustomPredicate.procurePredicate("year", fDate.getYear()), getBusinessClientIdPredicate(request)));

        if (wDLI.isNewEntity()) {
            if (wCal.getYear() != fDate.getYear()) {
                wDLI = this.genericService.loadObjectUsingRestriction(DevelopmentLevy.class, Arrays.asList(CustomPredicate.procurePredicate("year", wCal.getYear()), getBusinessClientIdPredicate(request)));
                if (!wDLI.isNewEntity()) {
                    if (wDLI.getMonth() == wCal.getMonthValue())
                        wDevLevyDeducted = true;
                }
            }
        } else {
            if (wDLI.getMonth() == fDate.getMonthValue()) {
                wDevLevyDeducted = true;
            }

            wDLI = this.genericService.loadObjectUsingRestriction(DevelopmentLevy.class, Arrays.asList(CustomPredicate.procurePredicate("year", wCal.getYear()), getBusinessClientIdPredicate(request)));
            if (!wDLI.isNewEntity()) {
                if (wDLI.getMonth() == wCal.getMonthValue()) {
                    wDevLevyDeducted = true;
                }
            }
        }
        WageSummaryBean wDevLevy = null;
        if (wDevLevyDeducted) {
            wDevLevy = new WageSummaryBean();
            wDevLevy.setName("Development Levy");
        }
        List<EmployeePayBean> wEPBList = this.paycheckService.loadEmployeePayBeanByParentIdFromDateToDate(bc.getBusinessClientInstId(), fDate.getMonthValue(), fDate.getYear(), bc, null, false);
        wBEOB.setTotalNoOfEmp(wEPBList.size());
        HashMap<Long, Double> wDeductionMap = this.employeeService.loadDeductionsForActiveEmployees(fDate, bc);
        double totalDeductions;
         for (EmployeePayBean e : wEPBList) {


            if (wDeductionMap.containsKey(e.getEmployee().getId())) {
                totalDeductions = wDeductionMap.get(e.getEmployee().getId()).doubleValue();
                if (bc.isSubeb()) {
                    wUnionDues.setCurrentBalance(wUnionDues.getCurrentBalance() + e.getUnionDues());
                    totalDeductions -= e.getUnionDues();
                }
                wTotalDeductions.setCurrentBalance(wTotalDeductions.getCurrentBalance() + totalDeductions);
            }
            if(!bc.isPensioner()){
                wPayee.setCurrentBalance(wPayee.getCurrentBalance() + e.getTaxesPaid());
                wTotalLoans.setCurrentBalance(wTotalLoans.getCurrentBalance() + e.getTotalGarnishments());
                wPensions.setCurrentBalance(wPensions.getCurrentBalance() + e.getContributoryPension());
                wPensionsEmployer.setCurrentBalance(wPensionsEmployer.getCurrentBalance() + e.getContributoryPension());
                if ((wDevLevyDeducted) && (e.getDevelopmentLevy() > 0.0D)) {
                    wDevLevy.setCurrentBalance(wDevLevy.getCurrentBalance() + e.getDevelopmentLevy());
                }
            }else{
                wUnionDues.setCurrentBalance(wUnionDues.getCurrentBalance() + e.getUnionDues());
            }

            wBEOB.setTotalDedBal(wBEOB.getTotalDedBal() + e.getTotalDeductions());

            wTotalCurrPay += e.getTotalPay();
            WageSummaryBean wWSB;


            if (this.ministryMap.containsKey(e.getMdaDeptMap().getMdaInfo().getId())) {
                wWSB = this.ministryMap.get(e.getMdaDeptMap().getMdaInfo().getId());
            } else {
                wWSB = new WageSummaryBean();
                wWSB.setAssignedToObject(e.getMdaDeptMap().getMdaInfo().getName().toUpperCase());
            }

            wWSB.setCurrentBalance(wWSB.getCurrentBalance() + e.getTotalPay());
            wWSB.setNoOfEmp(wWSB.getNoOfEmp() + 1);
            this.ministryMap.put(e.getMdaDeptMap().getMdaInfo().getId(), wWSB);


        }

        List<EmployeePayBean> wEPBListOld = this.paycheckService.loadEmployeePayBeanByParentIdFromDateToDate(bc.getBusinessClientInstId(), wCal.getMonthValue(), wCal.getYear(), bc, null, false);
        wBEOB.setTotalNoOfPrevMonthEmp(wEPBListOld.size());
        wDeductionMap = this.employeeService.loadDeductionsForActiveEmployees(wCheckDate, bc);

        for (EmployeePayBean e : wEPBListOld) {
            wTotalPrevPay += e.getTotalPay();
            if (wDeductionMap.containsKey(e.getEmployee().getId())) {
                totalDeductions = wDeductionMap.get(e.getEmployee().getId()).doubleValue();
                if (bc.isSubeb()) {
                    wUnionDues.setPreviousBalance(wUnionDues.getPreviousBalance() + e.getUnionDues());

                    totalDeductions -= e.getUnionDues();
                }
                wTotalDeductions.setPreviousBalance(wTotalDeductions.getPreviousBalance() + totalDeductions);
            }
            if(!bc.isPensioner()){
                wPayee.setPreviousBalance(wPayee.getPreviousBalance() + e.getTaxesPaid());
                wTotalLoans.setPreviousBalance(wTotalLoans.getPreviousBalance() + e.getTotalGarnishments());
                wPensions.setPreviousBalance(wPensions.getPreviousBalance() + e.getContributoryPension());
                wPensionsEmployer.setPreviousBalance(wPensionsEmployer.getPreviousBalance() + e.getContributoryPension());
                if ((wDevLevyDeducted) && (e.getDevelopmentLevy() > 0.0D)) {
                    wDevLevy.setPreviousBalance(wDevLevy.getPreviousBalance() + e.getDevelopmentLevy());
                }
            }else{
                wUnionDues.setPreviousBalance(wUnionDues.getPreviousBalance() + e.getUnionDues());
            }

            wBEOB.setTotalPrevDedBal(wBEOB.getTotalPrevDedBal() + e.getTotalDeductions());

            WageSummaryBean wWSB;


            if (this.ministryMap.containsKey(e.getMdaDeptMap().getMdaInfo().getId())) {
                wWSB = this.ministryMap.get(e.getMdaDeptMap().getMdaInfo().getId());

            } else {
                wWSB = new WageSummaryBean();
                wWSB.setAssignedToObject(e.getMdaDeptMap().getMdaInfo().getName().toUpperCase());
            }
            serialNum++;
            wWSB.setSerialNum(serialNum);


            wWSB.setPreviousNoOfEmp(wWSB.getPreviousNoOfEmp() + 1);
            wWSB.setPreviousBalance(wWSB.getPreviousBalance() + e.getTotalPay());
            this.ministryMap.put(e.getMdaDeptMap().getMdaInfo().getId(), wWSB);


        }

        List<WageSummaryBean> wRetList = getWageSummaryBeanFromMap(this.ministryMap, new ArrayList<>(), true);

        Collections.sort(wRetList, Comparator.comparing(WageSummaryBean::getAssignedToObject));
        wBEOB.setTotalCurrBal(wTotalCurrPay);
        wBEOB.setTotalPrevBal(wTotalPrevPay);

        LocalDate wEnd;

        wEnd = wPrevMonthEnd;

        List<SubventionHistory> wSubHistoryList = this.genericService.loadAllObjectsUsingRestrictions(SubventionHistory.class, Arrays.asList(CustomPredicate.procurePredicate("subvention.businessClientId", bc.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("runMonth", fDate.getMonthValue()), CustomPredicate.procurePredicate("runYear", fDate.getYear())), "id");

        for (SubventionHistory s : wSubHistoryList) {
            WageSummaryBean wWSB = new WageSummaryBean();
            wWSB.setSubvention(true);
            wWSB.setDisplayStyle("reportOdd");
            serialNum++;
            wWSB.setSerialNum(serialNum);
            wWSB.setName(s.getSubvention().getName().toUpperCase());
            wWSB.setCurrentBalance(s.getAmount());
            wBEOB.setTotalSubBal(wBEOB.getTotalSubBal() + s.getAmount());

            this.subventionMap.put(s.getSubvention().getId(), wWSB);
        }
        wSubHistoryList = this.genericService.loadAllObjectsUsingRestrictions(SubventionHistory.class, Arrays.asList(CustomPredicate.procurePredicate("subvention.businessClientId", bc.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("runMonth", wEnd.getMonthValue()), CustomPredicate.procurePredicate("runYear", wEnd.getYear())), "id");
        if (wSubHistoryList != null) {
            for (SubventionHistory s : wSubHistoryList) {
                WageSummaryBean wWSB = this.subventionMap.get(s.getSubvention().getId());

                if (wWSB == null) {
                    wWSB = new WageSummaryBean();
                    wWSB.setDisplayStyle("reportOdd");
                    serialNum++;
                    wWSB.setSerialNum(serialNum);
                    wWSB.setName(s.getSubvention().getName().toUpperCase());
                }

                if (!wWSB.isSubvention()) {
                    wWSB.setSubvention(true);
                }

                if (wBEOB.getTotalPrevBal() > 0.0D) {
                    wWSB.setPreviousBalance(s.getAmount());
                    wBEOB.setTotalPrevSubBal(wBEOB.getTotalPrevSubBal() + s.getAmount());
                } else {
                    wBEOB.setTotalPrevSubBal(wBEOB.getTotalPrevSubBal() + 0.0D);
                }

                this.subventionMap.put(s.getSubvention().getId(), wWSB);
            }

        }


            List<WageSummaryBean> wSubventionList = new ArrayList<>();
            try {
                wSubventionList = getWageSummaryBeanFromMap(this.subventionMap, wSubventionList, false);
            } catch (NullPointerException e) {
                this.subventionMap = new HashMap<>();
            }
        if (!(bc.isPensioner())) {
            Collections.sort(wSubventionList);
            wBEOB.setSubventionList(wSubventionList);

            List<WageSummaryBean> wContList = new ArrayList<>();
            serialNum++;
            wPensionsEmployer.setSerialNum(serialNum);
            wRBA.setCurrentBalance(wBEOB.getTotalCurrBal() * (wPRMB.getRbaPercentage() / 100.0D));
            wRBA.setPreviousBalance(wBEOB.getTotalPrevBal() * (wPRMBPrev.getRbaPercentage() / 100.0D));
            serialNum++;
            wRBA.setSerialNum(serialNum);
            wContList.add(wPensionsEmployer);
            wContList.add(wRBA);
            wBEOB.setTotalCurrCont(wPensionsEmployer.getCurrentBalance() + wRBA.getCurrentBalance());
            wBEOB.setTotalPrevCont(wPensionsEmployer.getPreviousBalance() + wRBA.getPreviousBalance());
            Collections.sort(wContList);
            wBEOB.setContributionList(wContList);
        }

        List<WageSummaryBean> wDedList = new ArrayList<>();
        if(!bc.isPensioner()){
            wPayee.setSerialNum(++serialNum);
            wTotalDeductions.setSerialNum(++serialNum);
            if (bc.isSubeb())
                wUnionDues.setSerialNum(++serialNum);
            wTotalLoans.setSerialNum(++serialNum);
            wPensions.setSerialNum(++serialNum);
            if (wDevLevyDeducted)
                wDevLevy.setSerialNum(++serialNum);
            wDedList.add(wPayee);
            wDedList.add(wTotalDeductions);

            wDedList.add(wTotalLoans);
            wDedList.add(wPensions);

            if (wDevLevyDeducted) {
                wDedList.add(wDevLevy);
            }
            if (bc.isSubeb())
                wDedList.add(wUnionDues);

        }else{
            wTotalDeductions.setSerialNum(++serialNum);
            wDedList.add(wTotalDeductions);
            wUnionDues.setSerialNum(++serialNum);
            wDedList.add(wUnionDues);
        }

        Collections.sort(wDedList);
        wBEOB.setDeductionList(wDedList);

        wBEOB.setGrandTotal(wBEOB.getTotalCurrBal() - wBEOB.getTotalDedBal());
        wBEOB.setGrandPrevTotal(wBEOB.getTotalPrevBal() - wBEOB.getTotalPrevDedBal());

        wBEOB.setTotalCurrOutGoing(wBEOB.getTotalCurrBal() + wBEOB.getTotalSubBal() + wBEOB.getTotalCurrCont());
        wBEOB.setTotalPrevOutGoing(wBEOB.getTotalPrevBal() + wBEOB.getTotalPrevSubBal() + wBEOB.getTotalPrevCont());

        wBEOB.setRunMonth(pRunMonth);
        wBEOB.setRunYear(pRunYear);
        Collections.sort(wRetList);
        wBEOB.setWageSummaryBeanList(wRetList);


        if (ReportType == 1) {
            new ExecutiveSummaryExcelGenerator().generateExcel(wBEOB, "Ogun " + bc.getBusinessName() + " Executive " + bc.getStaffTypeName() + " Salary Summary By " + bc.getMdaTitle(), response, request, getBusinessCertificate(request));
        } else if (ReportType == 2) {
            new ExecutiveSummaryPdfGenerator().generatePdf(wBEOB, "Ogun " + bc.getBusinessName() + " Executive " + bc.getStaffTypeName() + " Salary Summary By " + bc.getMdaTitle(), response, request, getBusinessCertificate(request));
        }

    }

    private List<WageSummaryBean> getWageSummaryBeanFromMap(HashMap<Long, WageSummaryBean> pObjectMap, List<WageSummaryBean> pRetList, boolean pSetDisplay) {
        Set<Map.Entry<Long, WageSummaryBean>> set = pObjectMap.entrySet();
        Iterator<Map.Entry<Long, WageSummaryBean>> i = set.iterator();

        while (i.hasNext()) {
            Map.Entry<Long, WageSummaryBean> me = i.next();

            if (pSetDisplay) {
                me.getValue().setDisplayStyle("reportOdd");
            }
            if (me.getValue().isSubvention()) {
                if (me.getValue().getCurrentBalance() == 0 && me.getValue().getPreviousBalance() == 0)
                    continue;

            }
            pRetList.add(me.getValue());


        }

        return pRetList;
    }


    @RequestMapping({"/payrollAnalysisByMdaExcel.do"})
    public void setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, @RequestParam("mid") Long pMdaId, @RequestParam("rt") int reportType, Model model,
                          HttpServletRequest request, HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        PayrollSummaryBean p = new PayrollSummaryBean();
        LocalDate fromDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
        LocalDate toDate = LocalDate.of(fromDate.getYear(), fromDate.getMonthValue(), fromDate.lengthOfMonth());

        HashMap<Long, SalaryInfo> wSIMap;
        List<SalaryInfo> wList = this.schoolService.loadBasicSalaryInfo();
        List<SchoolInfo> wSchoolList = this.genericService.loadControlEntity(SchoolInfo.class);
        wSIMap = setSalaryInfoHashMap(wList);
        HashMap<Long, SchoolInfo> _wSIMap = setSchoolInfoHashMap(wSchoolList);
        p.setSalaryInfoMap(wSIMap);

        p.setSchoolInfoMap(_wSIMap);


        List<EmployeePayBean> empBeanList = this.paycheckService.loadEmployeePayBeanByParentIdFromDateToDate(bc.getBusinessClientInstId(), pRunMonth, pRunYear, bc, pMdaId, true);
        GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();

        PdfSimpleGeneratorClass pdfSimpleGeneratorClass = new PdfSimpleGeneratorClass();

        ReportGeneratorBean rt = new ReportGeneratorBean();
        p.setAllowanceStartDateStr(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(toDate.getMonthValue(), toDate.getYear()));
        p.setPayDate(toDate);

        List<Map<String, Object>> newCompExecSummary = new ArrayList<>();
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i = 1;
        SalaryInfo wSI;
        Map<String, Object> newData;
        for (EmployeePayBean data : empBeanList) {

             newData = new HashMap<>();
             wSI = p.getSalaryInfoMap().get(data.getSalaryInfo().getId());

            if(bc.isPensioner()){
                newData.put(bc.getStaffTitle(), data.getEmployee().getEmployeeId());
                newData.put(bc.getStaffTypeName(), data.getEmployee().getDisplayNameWivShortInitials());
                newData.put("Account No.", data.getAccountNumber());
                newData.put("Bank", data.getBranchName());
                newData.put("Gross Pay", data.getTotalPay());
                newData.put("Monthly Pension", data.getMonthlyPension());
                newData.put("Arrears", data.getSpecialAllowance());
                newData.put("Total Deductions", data.getAllDedTotal());
                newData.put("Payable Amount", data.getNetPay());
                newData.put(bc.getMdaTitle(), data.getMdaDeptMap().getMdaInfo().getName());
            }else{
                newData.put(bc.getStaffTypeName(), data.getEmployee().getDisplayNameWivShortInitials());
                newData.put(bc.getStaffTitle(), data.getEmployee().getEmployeeId());
                newData.put("Grade/Step", wSI.getLevelAndStepAsStr());
                newData.put("Basic", data.getMonthlyBasic());
                newData.put("Total Allowance", data.getTotalAllowance());
                newData.put("Gross Pay", data.getTotalPay());
                newData.put("Tax Paid", data.getTaxesPaid());
                newData.put("Pension (" + bc.getStaffTypeName() + ")", data.getContributoryPension());
                newData.put("Total Loan Deduction", data.getTotalGarnishments());
                newData.put("Total Deductions", data.getAllDedTotal());
                newData.put("Bank", data.getBranchName());
                newData.put("Payable Amount", data.getNetPay());
                newData.put("Account No.", data.getAccountNumber());
                newData.put(bc.getMdaTitle(), data.getMdaDeptMap().getMdaInfo().getName());
            }


            newCompExecSummary.add(newData);
            uniqueSet.put(data.getMdaDeptMap().getMdaInfo().getName(), i++);
        }

        //adding table headers
        List<ReportGeneratorBean> headerList = new ArrayList<>();
        if(bc.isPensioner()){
            headerList.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
            headerList.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
            headerList.add(new ReportGeneratorBean("Account No.", 0));
            headerList.add(new ReportGeneratorBean("Bank", 0));
            headerList.add(new ReportGeneratorBean("Gross Pay", 2));
            headerList.add(new ReportGeneratorBean("Monthly Pension", 2));
            headerList.add(new ReportGeneratorBean("Arrears", 2));
            headerList.add(new ReportGeneratorBean("Total Deductions", 2));
            headerList.add(new ReportGeneratorBean("Payable Amount", 2));
        }else {
            headerList.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
            headerList.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
            headerList.add(new ReportGeneratorBean("Grade/Step", 0));
            headerList.add(new ReportGeneratorBean("Basic", 2));
            headerList.add(new ReportGeneratorBean("Total Allowance", 2));
            headerList.add(new ReportGeneratorBean("Gross Pay", 2));
            headerList.add(new ReportGeneratorBean("Tax Paid", 2));
            headerList.add(new ReportGeneratorBean("Pension (" + bc.getStaffTypeName() + ")", 2));
            headerList.add(new ReportGeneratorBean("Total Loan Deduction", 2));
            headerList.add(new ReportGeneratorBean("Total Deductions", 2));
            headerList.add(new ReportGeneratorBean("Bank", 0));
            headerList.add(new ReportGeneratorBean("Payable Amount", 2));
            headerList.add(new ReportGeneratorBean("Account No.", 0));

        }
        headerList.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));
        //translating table headers to meet report generator requirement
        List<Map<String, Object>> mappedHeadersList = new ArrayList<>();
        for (ReportGeneratorBean head : headerList) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            mappedHeadersList.add(mappedHeader);
        }

        //adding report headers
        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getBusinessName() + "_" + PayrollBeanUtils.getMonthNameAndYearForExcelNaming(pRunMonth, pRunYear, false, true) + " PAYROLL ANALYSIS - DETAILED");
//        mainHeaders.add("Pay Period: " + p.getAllowanceStartDateStr());

        List<String> mainHeaders2 = new ArrayList<>();
        mainHeaders2.add("Pay Period: " + p.getAllowanceStartDateStr());

        //setting report bean
        rt.setTableData(newCompExecSummary);
        rt.setTableHeaders(mappedHeadersList);
        rt.setBusinessCertificate(bc);
        rt.setMainHeaders(mainHeaders);
        rt.setMainHeaders2(mainHeaders2);
        rt.setTableType(1);
        rt.setGroupBy(bc.getMdaTitle());
        rt.setSubGroupBy(null);
        rt.setReportTitle(PayrollBeanUtils.getMonthNameAndYearForExcelNaming(pRunMonth, pRunYear, false, true) + " PAYROLL ANALYSIS - DETAILED BY " + bc.getMdaTitle());
        rt.setTotalInd(1);
        rt.setGroupedKeySet(uniqueSet.keySet());

        if (reportType == 1) {
            groupedDataExcelReportGenerator.getExcel(response, request, rt);
        } else {
            rt.setOutputInd(true);
            pdfSimpleGeneratorClass.getPdf(response, request, rt);
        }
    }

    private HashMap<Long, SalaryInfo> setSalaryInfoHashMap(List<SalaryInfo> list) {
        HashMap<Long, SalaryInfo> wRetMap = new HashMap<>();
        for (SalaryInfo s : list) {
            wRetMap.put(s.getId(), s);
        }
        return wRetMap;
    }

    private HashMap<Long, SchoolInfo> setSchoolInfoHashMap(List<SchoolInfo> pSchoolList) {
        HashMap<Long, SchoolInfo> wRetMap = new HashMap<>();
        for (SchoolInfo s : pSchoolList) {
            wRetMap.put(s.getId(), s);
        }
        return wRetMap;
    }

    @RequestMapping({"/payrollAnalysisExcel.do"})
    public void payrollSetupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, @RequestParam("mid") Long pMdaId, @RequestParam("rt") int reportType, Model model,
                                 HttpServletRequest request, HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        PdfSimpleGeneratorClass pdfSimpleGeneratorClass = new PdfSimpleGeneratorClass();

        ReportGeneratorBean rt = new ReportGeneratorBean();

        PayrollSummaryBean p = new PayrollSummaryBean();
        LocalDate fromDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
        LocalDate toDate = LocalDate.now();
        LocalDate.of(fromDate.getYear(), fromDate.getMonthValue(), fromDate.lengthOfMonth());

        List<EmployeePayBean> empBeanList;

        empBeanList = this.paycheckService.loadEmployeePayBeanByParentIdFromDateToDate(bc.getBusinessClientInstId(), pRunMonth, pRunYear, bc, pMdaId, false);


        HashMap<Long, SalaryInfo> wSIMap = new HashMap<>();
        List<SalaryInfo> wList = this.schoolService.loadBasicSalaryInfo();
        /*List<SchoolInfo> wSchoolList = (List<SchoolInfo>) this.payrollService.loadAllByHqlStr("from SchoolInfo");
         */
        //  HashMap<Integer,SchoolInfo> _wSIMap = setSchoolInfoHashMap(wSchoolList);
        wSIMap = setSalaryInfoHashMap(wList);
        p.setSalaryInfoMap(wSIMap);

        // p.setSchoolInfoMap(_wSIMap);

        p.setAllowanceStartDateStr(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(toDate.getMonthValue(), toDate.getYear()));
        p.setPayDate(toDate);

        List<Map<String, Object>> newCompExecSummary = new ArrayList<>();

        Map<String, Object> newData;
        SalaryInfo wSI;
        for (EmployeePayBean data : empBeanList) {

            newData = new HashMap<>();
            wSI = p.getSalaryInfoMap().get(data.getSalaryInfo().getId());
            newData.put(bc.getStaffTypeName(), data.getEmployee().getFirstName() + ", " + data.getEmployee().getLastName() + ", " + data.getEmployee().getInitials());
            newData.put(bc.getStaffTitle(), data.getEmployee().getEmployeeId());
            newData.put("Grade/Step", wSI.getLevelAndStepAsStr());
            newData.put("Basic", data.getMonthlyBasic());
            newData.put("Total Allowance", data.getTotalAllowance());
            newData.put("Gross Pay", data.getTotalPay());
            newData.put("Tax Paid", data.getTaxesPaid());
            newData.put("Pension (" + bc.getStaffTypeName() + ")", data.getContributoryPension());
            newData.put("Total Loan Deduction", data.getTotalGarnishments());
            newData.put("Total Deduction", data.getAllDedTotal());
            newData.put("Account No", data.getAccountNumber());
            newData.put("Bank", data.getBranchName());
            newData.put("Payable Amount", data.getNetPay());
            newData.put(bc.getMdaTitle(), data.getMdaDeptMap().getMdaInfo().getName());
            newCompExecSummary.add(newData);
        }

        //adding table headers
        List<ReportGeneratorBean> headerList = new ArrayList<>();
        headerList.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        headerList.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        headerList.add(new ReportGeneratorBean("Grade/Step", 0));
        headerList.add(new ReportGeneratorBean("Basic", 2));
        headerList.add(new ReportGeneratorBean("Total Allowance", 2));
        headerList.add(new ReportGeneratorBean("Gross Pay", 2));
        headerList.add(new ReportGeneratorBean("Tax Paid", 2));
        headerList.add(new ReportGeneratorBean("Pension (" + bc.getStaffTypeName() + ")", 2));
        headerList.add(new ReportGeneratorBean("Total Loan Deduction", 2));
        headerList.add(new ReportGeneratorBean("Total Deduction", 2));
        headerList.add(new ReportGeneratorBean("Account No", 0));
        headerList.add(new ReportGeneratorBean("Bank", 0));
        headerList.add(new ReportGeneratorBean("Payable Amount", 2));
        headerList.add(new ReportGeneratorBean(bc.getMdaTitle(), 0));

        //translating table headers to meet report generator requirement
        List<Map<String, Object>> mappedHeadersList = new ArrayList<>();
        for (ReportGeneratorBean head : headerList) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            mappedHeadersList.add(mappedHeader);
        }

        //adding report headers
        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(PayrollBeanUtils.getMonthNameAndYearForExcelNaming(pRunMonth, pRunYear, false, true) + " PAYROLL ANALYSIS - DETAILED");
//        mainHeaders.add("Pay Period: " + p.getAllowanceStartDateStr());

        List<String> mainHeaders2 = new ArrayList<>();
        mainHeaders2.add("Pay Period: " + p.getAllowanceStartDateStr());

        //setting report bean
        rt.setTableData(newCompExecSummary);
        rt.setTableHeaders(mappedHeadersList);
        rt.setBusinessCertificate(bc);
        rt.setMainHeaders(mainHeaders);
        rt.setMainHeaders2(mainHeaders2);
        rt.setTableType(0);
        rt.setGroupBy(null);
        rt.setSubGroupBy(null);
        rt.setReportTitle(PayrollBeanUtils.getMonthNameAndYearForExcelNaming(pRunMonth, pRunYear, false, true) + " PAYROLL ANALYSIS - DETAILED");
        rt.setTotalInd(1);

        if (reportType == 1) {
            simpleExcelReportGenerator.getExcel(response, request, rt);
        } else {
            rt.setOutputInd(true);
            pdfSimpleGeneratorClass.getPdf(response, request, rt);
        }
    }


    @RequestMapping({"/mdapSummaryDetailsExcel.do"})
    public void setupForm(Model pModel, HttpServletRequest pRequest, HttpServletRequest request,
                          HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException {
        SessionManagerService.manageSession(request, pModel);


        MDAPMiniBeanHolder wMBH = new MDAPMiniBeanHolder();

        BusinessCertificate bc = getBusinessCertificate(request);

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        ReportGeneratorBean rt = new ReportGeneratorBean();

        List<MdaType> wList = this.genericService.loadAllObjectsWithSingleCondition(MdaType.class, getBusinessClientIdPredicate(pRequest), null);
        List<MPBAMiniBean> wStoreList = new ArrayList<>();

        if (wList.size() == 1) {
            List<MdaInfo> wMdaInfoList = this.genericService.loadAllObjectsWithSingleCondition(MdaInfo.class, getBusinessClientIdPredicate(pRequest), null);
            for (MdaInfo m : wMdaInfoList) {
                List<MPBAMiniBean> wAgeFemaleList = this.hrService.getMDAPDetailsByGenderAndCode("F", wList.get(0).getMdaTypeCode(), bc, m);
                List<MPBAMiniBean> wAgeMaleList = this.hrService.getMDAPDetailsByGenderAndCode("M", wList.get(0).getMdaTypeCode(), bc, m);

                wStoreList.addAll(getDisplayBeanList(wAgeFemaleList, wAgeMaleList));
            }
        } else {
            for (MdaType mdaType : wList) {
                List<MPBAMiniBean> wAgeFemaleList = this.hrService.getMDAPDetailsByGenderAndCode("F", mdaType.getMdaTypeCode(), bc, null);
                List<MPBAMiniBean> wAgeMaleList = this.hrService.getMDAPDetailsByGenderAndCode("M", mdaType.getMdaTypeCode(), bc, null);

                wStoreList.addAll(getDisplayBeanList(wAgeFemaleList, wAgeMaleList));
            }
        }


        wMBH = setTotals(wMBH, wStoreList);

        List<MPBAMiniBean> pList = wMBH.getMpbaMiniBeanList();

        List<Map<String, Object>> bankSummaryList = new ArrayList<>();
        Double Basic = 0.00;
        for (MPBAMiniBean data : pList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getMdaTitle(), data.getName());
            newData.put("Code Name", data.getCodeName());
            newData.put("Head/Permanent Secretary", data.getHead());
            if (bc.isCivilService())
                newData.put("Parent Ministry", data.getParentObjectName());
            newData.put("Type", data.getType());
            newData.put("Staff Strength", data.getStaffStrength());
            newData.put("Female Staff", data.getNoOfFemales());
            newData.put("Male Staff", data.getNoOfMales());
            newData.put("Female Staff (%)", data.getFemalePercentageStr());
            newData.put("Male Staff (%)", data.getMalePercentageStr());
            bankSummaryList.add(newData);
        }

        List<ReportGeneratorBean> list = new ArrayList<>();
        list.add(new ReportGeneratorBean(bc.getMdaTitle(), 0));
        list.add(new ReportGeneratorBean("Code Name", 0));
        list.add(new ReportGeneratorBean("Head/Permanent Secretary", 0));
        if (bc.isCivilService())
            list.add(new ReportGeneratorBean("Parent Ministry", 0));
        list.add(new ReportGeneratorBean("Type", 0));
        list.add(new ReportGeneratorBean("Staff Strength", 1));
        list.add(new ReportGeneratorBean("Female Staff", 1));
        list.add(new ReportGeneratorBean("Male Staff", 1));
        list.add(new ReportGeneratorBean("Female Staff (%)", 0));
        list.add(new ReportGeneratorBean("Male Staff (%)", 0));


        List<Map<String, Object>> BankHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : list) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            BankHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        if (bc.isCivilService())
            mainHeaders.add("List of Ministries, Departments, Agencies in Ogun State.");
        else
            mainHeaders.add("List of " + wList.get(0).getName() + "'s in Ogun State.");

        rt.setBusinessCertificate(bc);
        rt.setGroupBy(null);
        rt.setReportTitle(bc.getMdaTitle() + " " + bc.getStaffTypeName() + " Summary");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(bankSummaryList);
        rt.setTableHeaders(BankHeaders);
        rt.setTableType(0);
        rt.setTotalInd(1);
        simpleExcelReportGenerator.getExcel(response, request, rt);
    }

    private MDAPMiniBeanHolder setTotals(MDAPMiniBeanHolder pMBH,
                                         List<MPBAMiniBean> pStoreList) {
        Collections.sort(pStoreList);
        Double tM = 0.0D;
        Double tF = 0.0D;
        for (MPBAMiniBean m : pStoreList) {
            pMBH.setTotalNoOfFemales(pMBH.getTotalNoOfFemales() + m.getNoOfFemales());
            pMBH.setTotalNoOfMales(pMBH.getTotalNoOfMales() + m.getNoOfMales());
            pMBH.setTotalStaffStrength(pMBH.getTotalStaffStrength() + m.getStaffStrength());
            tM = tM + m.getMalePercentage();
            tF = tF + m.getFemalePercentage();
            pMBH.setTotalMalePercentage(tM);
            pMBH.setTotalFemalePercentage(tF);
        }
        pMBH.setMpbaMiniBeanList(pStoreList);
        return pMBH;
    }

    private List<MPBAMiniBean> getDisplayBeanList(
            List<MPBAMiniBean> pAgeFemaleList, List<MPBAMiniBean> pAgeMaleList) {

        HashMap<Long, MPBAMiniBean> wFillMap = new HashMap<>();

        for (MPBAMiniBean m : pAgeFemaleList) {

            if (wFillMap.containsKey(m.getId())) {
                MPBAMiniBean n = wFillMap.get(m.getId());
                n.setNoOfFemales(n.getNoOfFemales() + m.getNoOfFemales());
                n.setFemalePercentage(m.getFemalePercentage());
                n.setStaffStrength(n.getStaffStrength() + m.getNoOfFemales());
                wFillMap.put(n.getId(), n);
            } else {
                //This should typically not happen often.
                m.setStaffStrength(m.getStaffStrength() + m.getNoOfFemales());
                wFillMap.put(m.getId(), m);
            }

        }
        for (MPBAMiniBean m : pAgeMaleList) {

            if (wFillMap.containsKey(m.getId())) {
                MPBAMiniBean n = wFillMap.get(m.getId());
                n.setNoOfMales(m.getNoOfMales());
                n.setMalePercentage(m.getMalePercentage());
                n.setStaffStrength(n.getStaffStrength() + m.getNoOfMales());
                wFillMap.put(n.getId(), n);
            } else {
                //This should typically not happen often.
                m.setStaffStrength(m.getStaffStrength() + m.getNoOfMales());
                wFillMap.put(m.getId(), m);
            }

        }
        //Now create a list and return the list...

        return createListFromHashMap(wFillMap);
    }

    private List<MPBAMiniBean> createListFromHashMap(HashMap<Long, MPBAMiniBean> pFillMap) {
        List<MPBAMiniBean> wRetList = new ArrayList<>();
        Set<Map.Entry<Long, MPBAMiniBean>> set = pFillMap.entrySet();
        Iterator<Map.Entry<Long, MPBAMiniBean>> i = set.iterator();

        while (i.hasNext()) {
            Map.Entry<Long, MPBAMiniBean> me = i.next();
            me.getValue().setDisplayStyle("reportOdd");
            wRetList.add(me.getValue());
        }

        return wRetList;
    }


    @RequestMapping({"/paySumByGLPayGroupExcel.do"})
    public void setupForm2(@RequestParam("rm") int pRunMonth,
                           @RequestParam("ry") int pRunYear,
                           Model model, HttpServletRequest request, HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();

        WageBeanContainer wBEOB = new WageBeanContainer();
        wBEOB.setRunMonth(pRunMonth);
        wBEOB.setRunYear(pRunYear);


        List<NamedEntityBean> wEPBList = this.payrollService.loadPayrollSummaryByRunMonthAndYear(bc, pRunMonth, pRunYear);


        wBEOB.setTotalNoOfEmp(wEPBList.size());
        HashMap<String, NamedEntityBean> wModelBean = new HashMap<>();
        Integer OTHER_ID = 20;

        for (NamedEntityBean e : wEPBList) {
            e = PayrollUtils.makeCode(e, OTHER_ID);
            if (e.getCurrentOtherId() != null && e.getCurrentOtherId() > 0) {
                OTHER_ID = e.getCurrentOtherId();
            }
            NamedEntityBean wNEB = wModelBean.get(e.getName());
            if (wNEB == null) {
                wNEB = new NamedEntityBean();
                wNEB.setTypeOfEmpType(e.getTypeOfEmpType());
            }
            wNEB.setTotalDeductions(wNEB.getTotalDeductions() + e.getTotalDeductions());
            wNEB.setTotalPay(wNEB.getTotalPay() + e.getTotalPay());
            wNEB.setNetPay(wNEB.getNetPay() + e.getNetPay());
            wNEB.setName(e.getName());
            wNEB.setId(e.getId());
            wNEB.setNoOfActiveEmployees(wNEB.getNoOfActiveEmployees() + e.getNoOfActiveEmployees());
            wModelBean.put(e.getName(), wNEB);
            wBEOB.setTotalDeductions(wBEOB.getTotalDeductions() + e.getTotalDeductions());
            wBEOB.setTotalGrossSalary(wBEOB.getTotalGrossSalary() + e.getTotalPay());
            wBEOB.setTotalNetPay(wBEOB.getTotalNetPay() + e.getNetPay());
            wBEOB.setTotalNoOfEmp(wBEOB.getTotalNoOfEmp() + e.getNoOfActiveEmployees());

        }
        wBEOB.setPayPeriodStr(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth, pRunYear));

        wBEOB.setNamedEntityBeanList(PayrollBeanUtils.getListFromMap(wModelBean, true));

        //Now get the Details for each Row....


        List<EmployeePayBean> empB = this.payrollService.loadEmployeePayBeanByParentIdAndLastPayPeriod(pRunMonth, pRunYear, 0, null, 0, 0L, 0, bc);


        List<Map<String, Object>> contPenMapped = new ArrayList<>();
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i = 1;
        for (EmployeePayBean data : empB) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTypeName(), data.getEmployee().getDisplayName());
            newData.put(bc.getStaffTitle(), data.getEmployee().getEmployeeId());
            newData.put(bc.getMdaTitle(), data.getMda());
            newData.put("Grade/Step", data.getSalaryInfo().getLevelAndStepAsStr());
            newData.put("Total Allowance", data.getTotalAllowance());
            newData.put("Gross Pay", data.getGrossPay());
            newData.put("Tax Paid", data.getTaxesPaid());
            newData.put("Pension (" + bc.getStaffTypeName() + ")", data.getContributoryPension());
            newData.put("Total Loan Deduction", data.getTotalGarnishments());
            newData.put("Total Deduction", data.getTotalDeductions());
            newData.put("Account Number", data.getAccountNumber());
            newData.put("Bank", data.getBranchName());
            newData.put("Payable Amount", data.getNetPay());
            newData.put(bc.getStaffTypeName() + " Type", data.getEmployeeTypeName());
            contPenMapped.add(newData);
            uniqueSet.put(data.getEmployeeTypeName(), i++);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean("Grade/Step", 0));
        tableHeaders.add(new ReportGeneratorBean("Total Allowance", 2));
        tableHeaders.add(new ReportGeneratorBean("Gross Pay", 2));
        tableHeaders.add(new ReportGeneratorBean("Tax Paid", 2));
        tableHeaders.add(new ReportGeneratorBean("Pension (" + bc.getStaffTypeName() + ")", 2));
        tableHeaders.add(new ReportGeneratorBean("Total Loan Deduction", 2));
        tableHeaders.add(new ReportGeneratorBean("Total Deduction", 2));
        tableHeaders.add(new ReportGeneratorBean("Account Number", 0));
        tableHeaders.add(new ReportGeneratorBean("Bank", 0));
        tableHeaders.add(new ReportGeneratorBean("Payable Amount", 2));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName() + " Type", 3));

        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : tableHeaders) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherSpecHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add("Payroll Summary By Grade Level and Pay Group");


        rt.setBusinessCertificate(bc);
        rt.setGroupBy(bc.getStaffTypeName() + " Type");
        rt.setReportTitle("Payroll Summary By Grade Level and Pay Group");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(OtherSpecHeaders);
        rt.setTableType(1);
        rt.setTotalInd(1);
        rt.setGroupedKeySet(uniqueSet.keySet());


        groupedDataExcelReportGenerator.getExcel(response, request, rt);

    }


    @RequestMapping({"/futureSimulationExcelReport.do"})
    public void futurePayrol(Model model, HttpServletRequest request, HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        List<FuturePaycheckMaster> empList = this.genericService.loadAllObjectsWithoutRestrictions(FuturePaycheckMaster.class, "lastModTs");

        List<Map<String, Object>> contPenMapped = new ArrayList<>();
        for (FuturePaycheckMaster data : empList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put("Simulation Name", data.getName());
            newData.put("Simulation Period", data.getSimulationMonthStr());
            newData.put("Created By", data.getLastModBy());
            newData.put("Created Date", data.getCreatedDateStr());
            contPenMapped.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean("Simulation Name", 0));
        tableHeaders.add(new ReportGeneratorBean("Simulation Period", 0));
        tableHeaders.add(new ReportGeneratorBean("Created By", 0));
        tableHeaders.add(new ReportGeneratorBean("Created Date", 0));

        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : tableHeaders) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherSpecHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add("Futuristic Payroll Simulation");


        rt.setBusinessCertificate(bc);
        rt.setGroupBy(null);
        rt.setReportTitle("FuturisticPayrollSimulation");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(OtherSpecHeaders);
        rt.setTableType(0);
        rt.setTotalInd(0);


        simpleExcelReportGenerator.getExcel(response, request, rt);

    }

}