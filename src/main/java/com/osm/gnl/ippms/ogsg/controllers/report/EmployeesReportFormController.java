package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.*;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.EmployeeReportService;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.*;
import com.osm.gnl.ippms.ogsg.domain.beans.EmpContMiniBean;
import com.osm.gnl.ippms.ogsg.domain.beans.LgaMiniBean;
import com.osm.gnl.ippms.ogsg.domain.beans.MdaEmployeeMiniBean;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.promotion.PromotionTracker;
import com.osm.gnl.ippms.ogsg.domain.report.ContractHistory;
import com.osm.gnl.ippms.ogsg.domain.report.IndividualEmployeeBean;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBean;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBeanInactive;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PayrollSummaryBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpDeductMiniBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Controller
public class EmployeesReportFormController extends BaseController {

    @Autowired
    EmployeeReportService employeeReportService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    PayrollService payrollService;

    @Autowired
    private HrServiceHelper hrServiceHelper;
    
    @Autowired
    ContributoryPensionService contributoryPensionService;

    @Autowired
    HRService hrService;

    @RequestMapping({"/hiredEmployeeByDateExcel.do"})
    public void generateEmployeeHiredByForm(@RequestParam("fd") String pFromDate, @RequestParam("td") String pToDate, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        BusinessCertificate bc = getBusinessCertificate(request);

        LocalDate wFromDate;
        wFromDate = PayrollBeanUtils.setDateFromString(pFromDate);
        LocalDate wToDate = PayrollBeanUtils.setDateFromString(pToDate);

        List<MdaEmployeeMiniBean> empList = this.employeeReportService.getEmployeeAuditLogByDateAndCode(wFromDate, wToDate, "I", bc);


        List<Map<String, Object>> MdaEmpList = new ArrayList<>();
        for (MdaEmployeeMiniBean data : empList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTypeName(), PayrollHRUtils.createDisplayName(data.getLastName(), data.getFirstName(), data.getInitials()));
            newData.put(bc.getStaffTitle(), data.getEmployeeId());
            newData.put("Sex", data.getGenderCode());
            newData.put("PayCode: Level & Step", data.getSalaryScaleLevelAndStepStr());
            if(bc.isPensioner()) {
                newData.put("Pension Start Date", data.getPensionStartDate());
            }
            else{
                newData.put("Date Of Employment", data.getHireDateStr());
            }
            newData.put(bc.getMdaTitle(), data.getMda());
            newData.put("L.G.A", data.getLga());
            MdaEmpList.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean("Sex", 0));
        tableHeaders.add(new ReportGeneratorBean("PayCode: Level & Step", 0));
        if(bc.isPensioner()){
            tableHeaders.add(new ReportGeneratorBean("Pension Start Date", 0));
        }
        else{
            tableHeaders.add(new ReportGeneratorBean("Date Of Employment", 0));
        }
        tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean("L.G.A", 0));

        List<Map<String, Object>> EmpMappedHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            EmpMappedHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add("Newly Hired "+bc.getStaffTypeName()+"s");

        rt.setBusinessCertificate(bc);
        rt.setGroupBy(null);
        rt.setReportTitle("Newly Hired "+bc.getStaffTypeName()+"s");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(MdaEmpList);
        rt.setTableHeaders(EmpMappedHeaders);
        rt.setTableType(0);
        rt.setTotalInd(0);

        simpleExcelReportGenerator.getExcel(response, request, rt);

    }

    @RequestMapping({"/employeesDueForPromotionExcel.do"})
    public void employeesDueForPromotionExcel(@RequestParam("fd") String pFromDate, @RequestParam("td") String pToDate, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BusinessCertificate bc = getBusinessCertificate(request);

        LocalDate fDate = PayrollBeanUtils.setDateFromString(pFromDate);
        LocalDate tDate = PayrollBeanUtils.setDateFromString(pToDate);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        List<PromotionTracker> promEmpList = this.genericService.loadAllObjectsUsingRestrictions(PromotionTracker.class, Arrays.asList(
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("nextPromotionDate", fDate, Operation.GREATER_OR_EQUAL),
                CustomPredicate.procurePredicate("nextPromotionDate", tDate, Operation.LESS_OR_EQUAL)), "id");

        List<Map<String, Object>> promList = new ArrayList<>();
        for (PromotionTracker data : promEmpList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTitle(), data.getEmployee().getEmployeeId());
            newData.put(bc.getStaffTypeName(),data.getEmployee().getDisplayName());
            newData.put(bc.getMdaTitle(), data.getEmployee().getAssignedToObject());
            newData.put("Pay Group", data.getEmployee().getSalaryInfo().getSalaryScaleLevelAndStepStr());
            newData.put("Promo. Last", data.getLastPromoDateStr());
            newData.put("Promo. Due Date", data.getNextPromoDateStr());
            promList.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean("Pay Group", 0));
        tableHeaders.add(new ReportGeneratorBean("Promo. Last", 0));
        tableHeaders.add(new ReportGeneratorBean("Promo. Due Date", 0));

        List<Map<String, Object>> promMappedHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            promMappedHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getStaffTypeName()+" Due For Promotion");

        rt.setBusinessCertificate(bc);
        rt.setGroupBy(null);
        rt.setReportTitle(bc.getStaffTypeName()+" Due For Promotion Report");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(promList);
        rt.setTableHeaders(promMappedHeaders);
        rt.setTableType(0);
        rt.setTotalInd(0);

        simpleExcelReportGenerator.getExcel(response, request, rt);

    }

    @RequestMapping({"/retirementTrackerExcelList.do"})
    public void setupForm(@RequestParam("fd") String pFromDate, @RequestParam("td") String pToDate, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        SessionManagerService.manageSession(request, model);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();

        BusinessCertificate bc = getBusinessCertificate(request);

        LocalDate wStartDate = PayrollBeanUtils.setDateFromString(pFromDate);
        LocalDate wEndDate = PayrollBeanUtils.setDateFromString(pToDate);
        List<CustomPredicate> predicates = new ArrayList<>();
        predicates.add(getBusinessClientIdPredicate(request));
        predicates.add(CustomPredicate.procurePredicate("terminateInactive","N"));
        if(bc.isPensioner()){
           predicates.add( CustomPredicate.procurePredicate("amAliveDate", wStartDate, Operation.GREATER_OR_EQUAL));
            predicates.add(CustomPredicate.procurePredicate("amAliveDate", wEndDate, Operation.LESS_OR_EQUAL));
        }else{
            predicates.add(CustomPredicate.procurePredicate("expectedDateOfRetirement", wStartDate, Operation.GREATER_OR_EQUAL));
            predicates.add(CustomPredicate.procurePredicate("expectedDateOfRetirement", wEndDate, Operation.LESS_OR_EQUAL));
        }


        List<HiringInfo> hList = this.genericService.loadAllObjectsUsingRestrictions(HiringInfo.class,  predicates, "id");


        List<Map<String, Object>> hiringList = new ArrayList<>();
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i = 1;
        for (HiringInfo data : hList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTitle(), data.getAbstractEmployeeEntity().getEmployeeId());
            newData.put(bc.getStaffTypeName(),data.getAbstractEmployeeEntity().getDisplayName());
            newData.put("Gender", data.getGenderStr());
            newData.put("Date Of Birth", data.getBirthDateStr());
            newData.put("Date Of Hire", data.getHireDateStr());
            if(bc.isPensioner()){
                newData.put("Pension Start Date", data.getPensionStartDateStr());
                newData.put("Years On Pension ", data.getNoOfYearsAtRetirement());
                newData.put("Age By I Am Alive Date", data.getAgeAtRetirement());
                newData.put("Yearly Pension", data.getYearlyPensionStr());
                newData.put("Monthly Pension", data.getMonthlyPensionStr());
                newData.put("Gratuity Balance", data.getGratuityStr());
                newData.put("I Am Alive Due Date", data.getAmAliveDateStr());
            }else{
                newData.put("Confirmation Date", data.getConfirmationDateAsStr());
                newData.put("Years In Service (Expected)", data.getNoOfYearsAtRetirement());
                newData.put("Age At Retirement (Expected)", data.getAgeAtRetirement());
                newData.put("Salary Scale", data.getEmployee().getSalaryInfo().getSalaryType().getName());
                newData.put("Level & Step", data.getEmployee().getSalaryInfo().getLevelAndStepAsStr());
                newData.put("Retirement Due Date (Expected)", data.getExpDateOfRetireStr());
            }


            newData.put(bc.getMdaTitle(), data.getAbstractEmployeeEntity().getCurrentMdaName());
            uniqueSet.put(data.getAbstractEmployeeEntity().getCurrentMdaName(), i++);
            hiringList.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean("Gender", 0));
        tableHeaders.add(new ReportGeneratorBean("Date Of Birth", 0));
        tableHeaders.add(new ReportGeneratorBean("Date Of Hire", 0));
        if(bc.isPensioner()){
            tableHeaders.add(new ReportGeneratorBean("Pension Start Date", 0));
            tableHeaders.add(new ReportGeneratorBean("Years On Pension ", 0));
            tableHeaders.add(new ReportGeneratorBean("Age By I Am Alive Date", 0));
            tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 0));
            tableHeaders.add(new ReportGeneratorBean("Yearly Pension", 2));
            tableHeaders.add(new ReportGeneratorBean("Monthly Pension", 2));
            tableHeaders.add(new ReportGeneratorBean("Gratuity Balance", 2));
            tableHeaders.add(new ReportGeneratorBean("I Am Alive Due Date", 0));
        }else{
            tableHeaders.add(new ReportGeneratorBean("Confirmation Date", 0));
            tableHeaders.add(new ReportGeneratorBean("Years In Service (Expected)", 0));
            tableHeaders.add(new ReportGeneratorBean("Age At Retirement (Expected)", 0));
            tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 0));
            tableHeaders.add(new ReportGeneratorBean("Salary Scale", 0));
            tableHeaders.add(new ReportGeneratorBean("Level & Step", 0));
            tableHeaders.add(new ReportGeneratorBean("Retirement Due Date (Expected)", 0));
        }


        List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            hirMappedHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        if(bc.isPensioner()){
            mainHeaders.add("Pensioners I Am Alive Date Tracker Report");
            rt.setReportTitle("PensionersAmAliveBy_"+wStartDate+ "_"+wEndDate+"_"+PayrollBeanUtils.getCurrentTime(true));
        }else{
            mainHeaders.add(bc.getStaffTypeName()+" Retirement Tracker");
            rt.setReportTitle(bc.getReportStaffTypeName()+"Retiring_Between"+wStartDate+ "_"+wEndDate+"_"+PayrollBeanUtils.getCurrentTime(true));
        }


        rt.setBusinessCertificate(bc);
        rt.setGroupBy(bc.getMdaTitle());

        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(hiringList);
        rt.setTableHeaders(hirMappedHeaders);
        rt.setTableType(1);
        rt.setTotalInd(1);
        rt.setGroupedKeySet(uniqueSet.keySet());

        groupedDataExcelReportGenerator.getExcel(response, request, rt);


    }


    @RequestMapping(value = "/IndEmpPayroll.do", method = RequestMethod.GET, params = { "eid" })
    public void indEmpPayroll(@RequestParam("eid") Long eID,
                                      HttpServletRequest request, HttpServletResponse response) throws Exception {

            BusinessCertificate bc = getBusinessCertificate(request);

            IndividualEmployeeBean jeds = employeeReportService.indEmpPay(eID, bc);

        StaffPayrollInfoPdfGenerator staffPayrollInfoPdfGenerator = new StaffPayrollInfoPdfGenerator();

            ReportGeneratorBean rt = new ReportGeneratorBean();

            Map<String, Object> newData = new HashMap<>();
            newData.put("employeeID", jeds.getEmpNumber());
            newData.put("title", jeds.getTitle());
            newData.put("firstName", jeds.getEmpFirstName());
            newData.put("LastName", jeds.getEmpLastName());
            newData.put("initials", jeds.getInitials());
            newData.put("DateOfBirth", jeds.getDob());
            newData.put("Sex", jeds.getSex());
            newData.put("Address1", jeds.getAddress1());
            newData.put("State", jeds.getStateOfOrigin());
            newData.put("mobile", jeds.getMobile());
            newData.put("email", jeds.getEmail());
            newData.put("maritalStatus", jeds.getMarital());
            newData.put("LGA", jeds.getLgArea());
            newData.put("Religion", jeds.getReligion());
            newData.put("Nationality", jeds.getNationality());
            newData.put("GradeLevel", jeds.getGradeLevel());
            newData.put("EmploymentDate", jeds.getDateOfEmployment());
            newData.put("EmploymentStatus", jeds.getEmploymentStatus());
            newData.put("AnnualBasic", jeds.getAnnualBasic());
            newData.put("PaymentMethod", jeds.getPaymentMethod());
            newData.put("BankBranch", jeds.getBankBranch());
            newData.put("AccountNumber", jeds.getAccountNumber());
            newData.put("Agency", jeds.getAgency());
            newData.put("School", jeds.getSchool());
            newData.put("capturedMonth", jeds.getCapturedMonth());
            newData.put("StaffCategory", jeds.getStaffCategory());


        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getStaffTypeName()+" Payroll Information");

        rt.setBusinessCertificate(bc);
        rt.setGroupBy(bc.getMdaTitle());
        rt.setReportTitle(bc.getStaffTypeName()+" Payroll Information");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setSingleTableData(newData);
        rt.setTableType(1);
        rt.setTotalInd(0);
        rt.setWatermark(bc.getBusinessName()+" Staff Payroll");

        staffPayrollInfoPdfGenerator.generatePdf(response, request, rt);
    }
    @RequestMapping({"/verifiedEmployeeList.do"})
    public void verifiedEmployees(@RequestParam("mid") Long pMdaId,@RequestParam("sind") int pStatusInd,  Model model, HttpServletRequest request, HttpServletResponse response, Map pModel) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = getBusinessCertificate(request);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        boolean verified = pStatusInd == 0;

        List<Employee> empList = this.hrServiceHelper.loadBiometricVerifiedStaffs(bc,pMdaId,verified);

        List<Map<String, Object>> hiringList = new ArrayList<>();
        for (Employee data : empList) {

            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTitle(), data.getEmployeeId());
            newData.put(bc.getStaffTypeName(), data.getDisplayName());
            newData.put(bc.getMdaTitle(), data.getMdaName());
            newData.put("Pay Group", data.getSalaryTypeName());
            if(verified){
                newData.put("Verified By", data.getLastModifier()) ;
                newData.put("Verified Date", data.getLastModTsForDisplay()) ;

            }
            hiringList.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean("Pay Group", 0));
        if(verified){
            tableHeaders.add(new ReportGeneratorBean("Verified By", 0));
            tableHeaders.add(new ReportGeneratorBean("Verified Date", 0));
        }



        List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            hirMappedHeaders.add(mappedHeader);
        }


        List<String> mainHeaders = new ArrayList<>();
        if(verified) {
            mainHeaders.add(bc.getStaffTypeName() + "s  With Verified Biometric Data");
            rt.setReportTitle("biometricsVerified"+bc.getStaffTypeName());

        }else {
            mainHeaders.add(bc.getStaffTypeName() + "s  With No Biometric Data");
            rt.setReportTitle(bc.getStaffTypeName()+"sWithNoBiometricData");

        }
        rt.setBusinessCertificate(bc);
        rt.setGroupBy(null);

        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(hiringList);
        rt.setTableType(0);
        rt.setTotalInd(0);
        rt.setTableHeaders(hirMappedHeaders);

        simpleExcelReportGenerator.getExcel(response, request, rt);


    }
    @RequestMapping({"/activeEmployeeList.do"})
    public void setupForm(@RequestParam("rm") int pRunMonth,@RequestParam("ry") int pRunYear,  Model model, HttpServletRequest request, HttpServletResponse response, Map pModel) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = getBusinessCertificate(request);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();


        List<HiringInfo> empList = this.employeeReportService.getActiveEmployees(bc,pRunMonth,pRunYear);
        Collections.sort(empList,Comparator.comparing(HiringInfo::getProposedMda).thenComparing(HiringInfo::getName));

        List<Map<String, Object>> hiringList = new ArrayList<>();
        for (HiringInfo data : empList) {

            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTitle(), data.getBvnNo());
            newData.put(bc.getStaffTypeName(), data.getName());
            newData.put(bc.getMdaTitle(), data.getProposedMda());
            newData.put("Pay Group", data.getOldLevelAndStep());
            if(bc.isPensioner()){
               newData.put("Yearly Pension", data.getYearlyPensionStr()) ;
                newData.put("Monthly Pension", data.getMonthlyPensionStr()) ;
                newData.put("Service End Date", data.getTerminatedDateAsStr()) ;
                newData.put("Pension Start Date", data.getPensionStartDateStr()) ;
                newData.put("Years On Pension", data.getYearsOnPension()) ;
            }else{
                newData.put("Yearly Gross", data.getYearlyPensionStr()) ;
                newData.put("Monthly Gross", data.getMonthlyPensionStr()) ;
                newData.put("Birth Date", data.getBirthDateStr()) ;
                newData.put("Hire Date", data.getHireDateStr()) ;
                newData.put("Expected Retirement Date", data.getExpDateOfRetireStr()) ;
            }
            hiringList.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean("Pay Group", 0));
        if(bc.isPensioner()){
            tableHeaders.add(new ReportGeneratorBean("Yearly Pension", 2));
            tableHeaders.add(new ReportGeneratorBean("Monthly Pension", 2));
            tableHeaders.add(new ReportGeneratorBean("Service End Date", 0));
            tableHeaders.add(new ReportGeneratorBean("Pension Start Date", 0));
            tableHeaders.add(new ReportGeneratorBean("Years On Pension", 0));
        }else{
            tableHeaders.add(new ReportGeneratorBean("Yearly Gross", 2));
            tableHeaders.add(new ReportGeneratorBean("Monthly Gross", 2));
            tableHeaders.add(new ReportGeneratorBean("Birth Date", 0));
            tableHeaders.add(new ReportGeneratorBean("Hire Date", 0));
            tableHeaders.add(new ReportGeneratorBean("Expected Retirement Date", 0));
        }




        List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            hirMappedHeaders.add(mappedHeader);
        }


        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add("Active "+bc.getStaffTypeName()+"s");

        rt.setBusinessCertificate(bc);
        rt.setGroupBy(null);
        rt.setReportTitle("active_"+bc.getStaffTypeName()+"s");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(hiringList);
        rt.setTableType(0);
        rt.setTotalInd(0);
        rt.setTableHeaders(hirMappedHeaders);

        simpleExcelReportGenerator.getExcel(response, request, rt);
//        ReportStatus stat = new ReportStatus();
//        stat.setFinished(true);
//        addSessionAttribute(request, "reportStatus", stat);

    }
    @RequestMapping({"/nominalRollList.do"})
    public void nominalRoll(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = getBusinessCertificate(request);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();


        List<HiringInfo> empList = this.employeeReportService.makeNominalRole(bc,pRunMonth,pRunYear);
        Collections.sort(empList,Comparator.comparing(HiringInfo::getProposedMda).thenComparing(HiringInfo::getName));

        List<Map<String, Object>> hiringList = new ArrayList<>();
        for (HiringInfo data : empList) {

            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTitle(), data.getEmployeeId());
            newData.put(bc.getStaffTypeName(), data.getName());
            newData.put(bc.getMdaTitle(), data.getProposedMda());

            if(bc.isPensioner()){
                newData.put("Yearly Pension", data.getYearlyPensionStr()) ;
                newData.put("Monthly Pension", data.getMonthlyPensionStr()) ;
                newData.put("Service End Date", data.getTerminatedDateAsStr()) ;
                newData.put("Pension Start Date", data.getPensionStartDateStr()) ;
                newData.put("Years On Pension", data.getYearsOnPension()) ;
                newData.put("Am Alive Due Date", data.getExpDateOfRetireStr()) ;
            }else{
                newData.put("Pay Group", data.getSalaryTypeName()+" "+data.getLevelAndStepStr());
                newData.put("Yearly Gross", data.getYearlyPensionStr()) ;
                newData.put("Monthly Gross", data.getMonthlyPensionStr()) ;
                newData.put("Birth Date", data.getBirthDateStr()) ;
                newData.put("Hire Date", data.getHireDateStr()) ;
                newData.put("Expected Retirement Date", data.getExpDateOfRetireStr()) ;
            }
            hiringList.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 0));

        if(bc.isPensioner()){
            tableHeaders.add(new ReportGeneratorBean("Yearly Pension", 2));
            tableHeaders.add(new ReportGeneratorBean("Monthly Pension", 2));
            tableHeaders.add(new ReportGeneratorBean("Service End Date", 0));
            tableHeaders.add(new ReportGeneratorBean("Pension Start Date", 0));
            tableHeaders.add(new ReportGeneratorBean("Years On Pension", 0));
        }else{
            tableHeaders.add(new ReportGeneratorBean("Pay Group", 0));
            tableHeaders.add(new ReportGeneratorBean("Yearly Gross", 2));
            tableHeaders.add(new ReportGeneratorBean("Monthly Gross", 2));
            tableHeaders.add(new ReportGeneratorBean("Birth Date", 0));
            tableHeaders.add(new ReportGeneratorBean("Hire Date", 0));
            tableHeaders.add(new ReportGeneratorBean("Expected Retirement Date", 0));
        }




        List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            hirMappedHeaders.add(mappedHeader);
        }


        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getOrgNameForFileNaming(bc.getBusinessClientInstId())+" Nominal Roll ");

        rt.setBusinessCertificate(bc);
        rt.setGroupBy(null);
        rt.setReportTitle(bc.getOrgNameForFileNaming(bc.getBusinessClientInstId())+" Nominal Roll ");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(hiringList);
        rt.setTableType(0);
        rt.setTotalInd(0);
        rt.setTableHeaders(hirMappedHeaders);

        simpleExcelReportGenerator.getExcel(response, request, rt);

    }
    @RequestMapping({"/tpsReportExcel.do"})
    public void generateTPSEmployeeReport(@RequestParam(value="rm", required= false) int pRunMonth,
                                                  @RequestParam(value="ry", required= false) int pRunYear,
                                                  @RequestParam(value="pid", required= false) Long pPfaInstId,
                                                  @RequestParam(value="mdaid", required= false) Long pMdaId,
                                                  @RequestParam(value="utcr", required= false) boolean  pUseRule,
                                                  @RequestParam(value="intemp", required= false) boolean pIncludeTerminated,
                                                  @RequestParam(value="tpscps", required= false) int pTpsCps,
                                                  Model model, HttpServletRequest request, HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException, IOException {

        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();

        ReportGeneratorBean rt = new ReportGeneratorBean();
        List<EmpDeductMiniBean> tpsCpsList;

        boolean wTps = pTpsCps == IConstants.TPS_IND;

        tpsCpsList = this.contributoryPensionService.loadTPSEmployeeByRunMonthAndYear(true, 0,0, "", "",pRunMonth, pRunYear,pPfaInstId,pMdaId,pUseRule,pIncludeTerminated,wTps, bc);

        PayrollSummaryBean p = new PayrollSummaryBean();

        LocalDate fromDate = LocalDate.now();

        p.setPayDate(fromDate);

        p.setStartAndEndPeriod(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth, pRunYear));
        p.setGenerateBankPvs(false);

//        if(pPfaInstId != null && pPfaInstId > 0)
//            p.setFilterByPfa(true);
//        if(IppmsUtils.isNotNullAndGreaterThanZero(pMdaId))
//            p.setFilterByMda(true);


        List<Map<String, Object>> contPenMapped = new ArrayList<>();
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i = 1;
        for (EmpDeductMiniBean data : tpsCpsList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTitle(), data.getEmployeeId());
            newData.put(bc.getStaffTypeName(), data.getName());
            newData.put("Hire Date", data.getHireDateStr());
            newData.put("Birth Date", data.getBirthDateStr());
            newData.put("Retirement Date (Exp)", data.getExpDateOfRetirementStr());
            newData.put("Pay Group", data.getSalaryInfoDesc()+" "+data.getGradeLevelAndStep());
            newData.put("Contribution", data.getCurrentDeduction());
            newData.put("MDA", data.getMdaDeptMap().getMdaInfo().getName());
            contPenMapped.add(newData);
            uniqueSet.put(data.getMdaDeptMap().getMdaInfo().getName(), i++);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean("Hire Date", 0));
        tableHeaders.add(new ReportGeneratorBean("Birth Date", 0));
        tableHeaders.add(new ReportGeneratorBean("Retirement Date (Exp)", 0));
        tableHeaders.add(new ReportGeneratorBean("Pay Group", 0));
        tableHeaders.add(new ReportGeneratorBean("Contribution", 2));
        tableHeaders.add(new ReportGeneratorBean("MDA", 3));

        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherSpecHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();

        if(wTps){
            mainHeaders.add("Transitional Pension Scheme");
            rt.setReportTitle("Transitional Pension Scheme");
        }else{
            mainHeaders.add("Contributory Pension Scheme");
            rt.setReportTitle("Contributory Pension Scheme");
        }
        rt.setBusinessCertificate(bc);
        rt.setGroupBy("MDA");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(OtherSpecHeaders);
        rt.setTableType(1);
        rt.setTotalInd(1);
        rt.setGroupedKeySet(uniqueSet.keySet());


        groupedDataExcelReportGenerator.getExcel(response, request, rt);
    }

    @RequestMapping({"/employeeGeneralInfo.do"})
    public void generateEmployeeGeneralInfo(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear,
                                                    Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);


        BusinessEmpOVBean wConfigBean = new BusinessEmpOVBean();

        GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();

        ReportGeneratorBean rt = new ReportGeneratorBean();

        BusinessCertificate bc = getBusinessCertificate(request);

        Object _wConfigBean = getSessionAttribute(request, CONFIG_BEAN);
        if(_wConfigBean != null && _wConfigBean.getClass().isAssignableFrom(BusinessEmpOVBean.class)){
            wConfigBean = (BusinessEmpOVBean)_wConfigBean;
        }
        EmpContMiniBean wNoOfElements = this.employeeService.getTotalNumberOfEmployeesByRenumeration(
                pRunMonth, pRunYear, new BusinessEmpOVBean(), bc);
        List<Employee> empList = this.employeeService.loadActiveEmployeeByRenumeration(0, 0,
                null, null, pRunMonth, pRunYear,wConfigBean, bc,true);


        BusinessEmpOVBeanInactive pList = new BusinessEmpOVBeanInactive(empList);

        for(Employee e : empList){
            if(e.getTotalTaxesPaid() > 0 || e.getTotalGrossPay() > 0){
                pList.setTotalTaxesPaid(e.getTotalTaxesPaid());
                pList.setTotalGrossPay(e.getTotalGrossPay());
                pList.setTotalNetPay(e.getTotalNetPay());
                pList.setTotalMonthlyBasic(e.getTotalMonthlyBasic());
                break;
            }
        }
        pList.setBusEmpOvBean(wConfigBean);

        pList.setFromYear(pRunMonth);
        pList.setToYear(pRunYear);


        List<Map<String, Object>> contPenMapped = new ArrayList<>();
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        Collections.sort(empList,Comparator.comparing(AbstractEmployeeEntity::getDisplayName).thenComparing(AbstractEmployeeEntity::getMdaName).thenComparing(AbstractEmployeeEntity::getNetPay));
        int i = 1;
        for (Employee data : empList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTitle(), data.getEmployeeId());
            newData.put(bc.getStaffTypeName(), data.getDisplayName());
            newData.put("Level/Step", data.getSalaryInfo().getLevelAndStepAsStr());
            newData.put("Pay Group", data.getSalaryInfo().getPayGroupCode());
            newData.put("Gender", data.getHiringInfo().getGenderStr());
            newData.put("Phone No.", data.getGsmNumber());
            newData.put("Organization", data.getMdaName());
            newData.put("LG Area", data.getHiringInfo().getLgaName());
            if(!bc.isPensioner())
              newData.put("School", data.getSchoolName());
            newData.put("Religion", data.getHiringInfo().getReligionStr());
            newData.put("Date Of Hire", data.getHiringInfo().getHireDateStr());
            newData.put("Marital Status", data.getHiringInfo().getMaritalStatusStr());
            newData.put("Address", data.getAddress1());
            newData.put(bc.getStaffTypeName()+" Type", data.getEmployeeTypeStr());
            newData.put("Birth Date", data.getHiringInfo().getBirthDateStr());
            newData.put("Retirement Date", data.getHiringInfo().getTerminatedDateStr());
            newData.put("Monthly Basic", data.getSalaryInfo().getMonthlyBasicSalary());

            newData.put("Net Pay (Payable)", data.getNetPay());
            if(bc.isPensioner()){
                newData.put("Pension Start Date", data.getHiringInfo().getPensionStartDateStr());
                newData.put("Pension End Date", data.getHiringInfo().getPensionEndDateStr());
            }else{
                newData.put("Total Allowance", data.getSalaryInfo().getSpaAllowance());
                newData.put("Pension Contribution", data.getPensionContribution());
                newData.put("PFA", data.getHiringInfo().getPfaName());
                newData.put("Pension PIN", data.getHiringInfo().getPensionPinCode());
            }
            newData.put("Taxes Paid", data.getHiringInfo().getTaxesPaid());
            newData.put("Gross Pay", data.getHiringInfo().getTerminalGrossPay());
            newData.put("Bank", data.getBankName());
            newData.put("Bank Branch", data.getBranchName());
            newData.put("Account No.", data.getAccountNumber());
            newData.put(bc.getMdaTitle(), data.getMdaName());
            contPenMapped.add(newData);
            uniqueSet.put(data.getMdaName(), i++);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean("Level/Step", 0));
        tableHeaders.add(new ReportGeneratorBean("Pay Group", 0));
        tableHeaders.add(new ReportGeneratorBean("Gender", 0));
        tableHeaders.add(new ReportGeneratorBean("Phone No.", 0));
        tableHeaders.add(new ReportGeneratorBean("Organization", 0));
        tableHeaders.add(new ReportGeneratorBean("LG Area", 0));
        if(!bc.isPensioner())
           tableHeaders.add(new ReportGeneratorBean("School", 0));
        tableHeaders.add(new ReportGeneratorBean("Religion", 0));
        tableHeaders.add(new ReportGeneratorBean("Date Of Hire", 0));
        tableHeaders.add(new ReportGeneratorBean("Marital Status", 0));
        tableHeaders.add(new ReportGeneratorBean("Address", 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName()+" Type", 0));
        tableHeaders.add(new ReportGeneratorBean("Birth Date", 0));
        tableHeaders.add(new ReportGeneratorBean("Retirement Date", 0));
        tableHeaders.add(new ReportGeneratorBean("Monthly Basic", 2));
        tableHeaders.add(new ReportGeneratorBean("Net Pay (Payable)", 2));
        if(bc.isPensioner()){
            tableHeaders.add(new ReportGeneratorBean("Pension Start Date", 0));
            tableHeaders.add(new ReportGeneratorBean("Pension End Date", 0));
        }else{
            tableHeaders.add(new ReportGeneratorBean("Total Allowance", 2 ));
            tableHeaders.add(new ReportGeneratorBean("Pension Contribution", 2 ));
            tableHeaders.add(new ReportGeneratorBean("PFA", 0));
            tableHeaders.add(new ReportGeneratorBean("Pension PIN", 0));
        }
        tableHeaders.add(new ReportGeneratorBean("Taxes Paid", 2));
        tableHeaders.add(new ReportGeneratorBean("Gross Pay", 2));
        tableHeaders.add(new ReportGeneratorBean("Bank", 0));
        tableHeaders.add(new ReportGeneratorBean("Bank Branch", 0));
        tableHeaders.add(new ReportGeneratorBean("Account No.", 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherSpecHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getStaffTypeName()+" General Information for "+PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pList.getFromYear(), pList.getToYear()));
        mainHeaders.add("Total "+bc.getStaffTypeName()+" : "+empList.size());
        if(!bc.isPensioner())
            mainHeaders.add("Total Monthly Basic Paid : "+ wNoOfElements.getMonthlyBasicStr());
        mainHeaders.add("Total Net Pay Paid : "+wNoOfElements.getNetPayStr());
        if(!bc.isPensioner())
           mainHeaders.add("Total Taxes Paid : "+wNoOfElements.getCurrentContributionStr());
        mainHeaders.add("Total Gross Pay : "+wNoOfElements.getYearToDateStr());


        rt.setBusinessCertificate(bc);
        rt.setReportTitle(bc.getMdaTitle()+"_"+bc.getStaffTypeName()+"s_General_Info");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(OtherSpecHeaders);
        rt.setTotalInd(1);
        rt.setGroupBy(null);
        rt.setTableType(0);
        //rt.setGroupedKeySet(uniqueSet.keySet());

 /*
        int totalSize;
        if(IppmsUtils.isNotNull(rt.getGroupBy())) {
            totalSize = uniqueSet.size();
        }
        else{
            totalSize = contPenMapped.size();
        }

           NewSimpleExcelGenerator wCP = new NewSimpleExcelGenerator(rt, response, request, totalSize);
            addSessionAttribute(request, "myCalcPay", wCP);
            Thread t = new Thread(wCP);
            t.start();
            return "redirect:displayStatus.do";*/
        new SimpleExcelReportGenerator().getExcel(response, request, rt);
    }

//    @RequestMapping({"/employeeGeneralInfoPdf.do"})
//    public void generateEmployeeGeneralInfomationPdf(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear,
//                                                      Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        SessionManagerService.manageSession(request, model);
//
//
//        BusinessEmpOVBean wConfigBean = new BusinessEmpOVBean();
//
//        ReportGeneratorBean rt = new ReportGeneratorBean();
//
//        BusinessCertificate bc = getBusinessCertificate(request);
//
//        Object _wConfigBean = getSessionAttribute(request, CONFIG_BEAN);
//        if(_wConfigBean != null && _wConfigBean.getClass().isAssignableFrom(BusinessEmpOVBean.class)){
//            wConfigBean = (BusinessEmpOVBean)_wConfigBean;
//        }
//
//        List<Employee> empList = this.employeeService.loadActiveEmployeeByRenumeration(0, 0,
//                null, null, pRunMonth, pRunYear,wConfigBean, bc);
//
//        EmpContMiniBean wNoOfElements = this.employeeService.getTotalNumberOfEmployeesByRenumeration(
//                pRunMonth, pRunYear, new BusinessEmpOVBean(), bc);
//        //BusinessEmpOVBeanInactive pList = new BusinessEmpOVBeanInactive(empList);
//
//       /* for(Employee e : empList){
//            if(e.getTotalTaxesPaid() > 0 || e.getTotalGrossPay() > 0){
//                pList.setTotalTaxesPaid(e.getTotalTaxesPaid());
//                pList.setTotalGrossPay(e.getTotalGrossPay());
//                pList.setTotalNetPay(e.getTotalNetPay());
//                pList.setTotalMonthlyBasic(e.getTotalMonthlyBasic());
//                break;
//            }
//        }
//        pList.setBusEmpOvBean(wConfigBean);
//
//        pList.setFromYear(pRunMonth);
//        pList.setToYear(pRunYear)*/;
//
//
//        List<Map<String, Object>> contPenMapped = new ArrayList<>();
//        for (Employee data : empList) {
//            Map<String, Object> newData = new HashMap<>();
//            newData.put(bc.getStaffTitle(), data.getEmployeeId());
//            newData.put(bc.getStaffTypeName(), data.getDisplayName());
//            newData.put("Level/Step", data.getSalaryInfo().getLevelAndStepAsStr());
//            newData.put("Pay Group", data.getSalaryInfo().getPayGroupCode());
//            newData.put(bc.getMdaTitle(), data.getMdaName());
//            newData.put("Gender", data.getHiringInfo().getGenderStr());
//            newData.put("Phone No.", data.getGsmNumber());
//            newData.put("LG Area", data.getHiringInfo().getLgaName());
//            if(!bc.isPensioner())
//            newData.put("School", data.getSchoolName());
//            newData.put("Religion", data.getHiringInfo().getReligionStr());
//            newData.put("Date Of Hire", data.getHiringInfo().getHireDateStr());
//            newData.put("Marital Status", data.getHiringInfo().getMaritalStatusStr());
//            newData.put("Address", data.getAddress1());
//            newData.put(bc.getStaffTypeName()+" Type", data.getEmployeeTypeStr());
//            newData.put("Birth Date", data.getHiringInfo().getBirthDateStr());
//            newData.put("Retirement Date", data.getHiringInfo().getTerminatedDateStr());
//            newData.put("Monthly Basic", data.getSalaryInfo().getMonthlyBasicSalary());
//            newData.put("Net Pay (Payable)", data.getNetPay());
//            if(bc.isPensioner()){
//                newData.put("Pension Start Date", data.getHiringInfo().getPensionStartDateStr());
//                newData.put("Pension End Date", data.getHiringInfo().getPensionEndDateStr());
//            }else{
//                newData.put("Total Allowance", data.getSalaryInfo().getSpaAllowance());
//                newData.put("Pension Contribution", data.getPensionContribution());
//                newData.put("PFA", data.getHiringInfo().getPfaName());
//                newData.put("Pension PIN", data.getHiringInfo().getPensionPinCode());
//            }
//            newData.put("Taxes Paid", data.getHiringInfo().getTaxesPaid());
//            newData.put("Gross Pay", data.getHiringInfo().getTerminalGrossPay());
//            newData.put("Bank", data.getBankName());
//            newData.put("Bank Branch", data.getBranchName());
//            newData.put("Account No.", data.getAccountNumber());
//
//            contPenMapped.add(newData);
//
//        }
//
//        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
//        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
//        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
//        tableHeaders.add(new ReportGeneratorBean("Level/Step", 0));
//        tableHeaders.add(new ReportGeneratorBean("Pay Group", 0));
//        tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 0));
//        tableHeaders.add(new ReportGeneratorBean("Gender", 0));
//        tableHeaders.add(new ReportGeneratorBean("Phone No.", 0));
//        tableHeaders.add(new ReportGeneratorBean("Local Govt. Area", 0));
//        if(!bc.isPensioner())
//        tableHeaders.add(new ReportGeneratorBean("School", 0));
//        tableHeaders.add(new ReportGeneratorBean("Religion", 0));
//        tableHeaders.add(new ReportGeneratorBean("Date Of Hire", 0));
//        tableHeaders.add(new ReportGeneratorBean("Marital Status", 0));
//        tableHeaders.add(new ReportGeneratorBean("Address", 0));
//        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName()+" Type", 0));
//        tableHeaders.add(new ReportGeneratorBean("Birth Date", 0));
//        tableHeaders.add(new ReportGeneratorBean("Retirement Date", 0));
//        tableHeaders.add(new ReportGeneratorBean("Monthly Basic", 2));
//        tableHeaders.add(new ReportGeneratorBean("Net Pay (Payable)", 2));
//       if(bc.isPensioner()){
//            tableHeaders.add(new ReportGeneratorBean("Pension Start Date", 0));
//            tableHeaders.add(new ReportGeneratorBean("Pension End Date", 0));
//        }else{
//           tableHeaders.add(new ReportGeneratorBean("Total Allowance", 2 ));
//           tableHeaders.add(new ReportGeneratorBean("Pension Contribution", 2 ));
//           tableHeaders.add(new ReportGeneratorBean("PFA", 0));
//           tableHeaders.add(new ReportGeneratorBean("Pension PIN", 0));
//       }
//        tableHeaders.add(new ReportGeneratorBean("Taxes Paid", 2));
//        tableHeaders.add(new ReportGeneratorBean("Gross Pay", 2));
//        tableHeaders.add(new ReportGeneratorBean("Bank", 0));
//        tableHeaders.add(new ReportGeneratorBean("Bank Branch", 0));
//        tableHeaders.add(new ReportGeneratorBean("Account No.", 0));
//
//
//        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
//        for(ReportGeneratorBean head : tableHeaders){
//            Map<String, Object> mappedHeader = new HashMap<>();
//            mappedHeader.put("headerName", head.getHeaderName());
//            mappedHeader.put("totalInd", head.getTotalInd());
//            OtherSpecHeaders.add(mappedHeader);
//        }
//
//        List<String> mainHeaders = new ArrayList<>();
//        mainHeaders.add(bc.getStaffTypeName()+" General Information for "+PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth, pRunYear));
//         mainHeaders.add("Total "+bc.getStaffTypeName()+" : "+empList.size());
//        if(!bc.isPensioner())
//            mainHeaders.add("Total Monthly Basic Paid : "+ wNoOfElements.getMonthlyBasicStr());
//        mainHeaders.add("Total Net Pay Paid : "+wNoOfElements.getNetPayStr());
//        if(!bc.isPensioner())
//            mainHeaders.add("Total Taxes Paid : "+wNoOfElements.getCurrentContributionStr());
//        mainHeaders.add("Total Gross Pay : "+wNoOfElements.getYearToDateStr());
//
//
//        rt.setBusinessCertificate(bc);
//        rt.setReportTitle(bc.getMdaTitle()+"_"+bc.getStaffTypeName()+"s_General_Info");
//        rt.setMainHeaders(mainHeaders);
//        rt.setSubGroupBy(null);
//        rt.setTableData(contPenMapped);
//        rt.setTableHeaders(OtherSpecHeaders);
//        rt.setTotalInd(1);
//        rt.setGroupBy(null);
//        rt.setTableType(0);
//        rt.setOutputInd(true);
//
//        new PdfSimpleGeneratorClass().getPdf(response, request, rt);
//    }

    @RequestMapping({"/empBvnReport.do"})
    public void generateEmployeeBVNInfomation(
            @RequestParam("rm") int pRunMonth,
            @RequestParam("ry") int pRunYear,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ReportGeneratorBean rt = new ReportGeneratorBean();

        GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();

        SessionManagerService.manageSession(request, model);

        BusinessEmpOVBean wConfigBean = new BusinessEmpOVBean();

        BusinessCertificate bc = getBusinessCertificate(request);

        Object _wConfigBean = getSessionAttribute(request, BVN_CONFIG_BEAN);
        if(_wConfigBean != null && _wConfigBean.getClass().isAssignableFrom(BusinessEmpOVBean.class)){
            wConfigBean = (BusinessEmpOVBean)_wConfigBean;
        }

        List<Employee> empList = this.hrService.loadEmpBVNInformation(0, 0,null, null, pRunMonth, pRunYear, wConfigBean, true, bc);



        BusinessEmpOVBeanInactive pList = new BusinessEmpOVBeanInactive(empList);

        pList.setBusEmpOvBean(wConfigBean);

        pList.setFromYear(pRunMonth);
        pList.setToYear(pRunYear);

        pList.setConfigBean(wConfigBean);

        List<Map<String, Object>> contPenMapped = new ArrayList<>();
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i = 1;
        for (Employee data : empList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTitle(), data.getEmployeeId());
            newData.put(bc.getStaffTypeName(), data.getDisplayName());
            newData.put("Level/Step", data.getSalaryInfo().getLevelAndStepAsStr());
            newData.put("Phone No.", data.getGsmNumber());
            newData.put("Bank", data.getBusinessName());
            newData.put("Account No.", data.getAccountNumber());
            newData.put("B.V.N", data.getBvnNumber());
            newData.put(bc.getMdaTitle(), data.getMdaName());
            contPenMapped.add(newData);
            uniqueSet.put(data.getMdaName(), i++);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean("Level/Step", 0));
        tableHeaders.add(new ReportGeneratorBean("Phone No.", 0));
        tableHeaders.add(new ReportGeneratorBean("Bank", 0));
        tableHeaders.add(new ReportGeneratorBean("Account No.", 0));
        tableHeaders.add(new ReportGeneratorBean("B.V.N", 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherSpecHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getStaffTypeName()+" B.V.N Information for "+PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pList.getFromYear(), pList.getToYear()));


        rt.setBusinessCertificate(bc);
        rt.setGroupBy(bc.getMdaTitle());
        rt.setReportTitle(bc.getStaffTypeName()+"BVNInfo");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(OtherSpecHeaders);
        rt.setTableType(1);
        rt.setTotalInd(0);
        rt.setGroupedKeySet(uniqueSet.keySet());


        groupedDataExcelReportGenerator.getExcel(response, request, rt);
    }

    @RequestMapping(value = {"/empByExpYrOfRetireReport.do"}, method = {RequestMethod.GET})
    public void generateEmployeeYrOfRetirementInfomation(
            @RequestParam("fd") String pFd, @RequestParam("td") String pTd,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {


        SimpleDateFormat formatter2 =new SimpleDateFormat("yyyy-mm-dd");

        Date dateFrom = formatter2.parse(pFd);
        Date dateTo = formatter2.parse(pTd);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();

        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String strFDate= formatter.format(dateFrom);
        String strTDate= formatter.format(dateTo);

        LocalDate fDate = PayrollBeanUtils.setDateFromString(strFDate);
        LocalDate tDate = PayrollBeanUtils.setDateFromString(strTDate);

        fDate = LocalDate.of(fDate.getYear(), 1, 1);
        tDate = LocalDate.of(tDate.getYear(), 12, 31);


        List<HiringInfo> empList =  this.hrServiceHelper.getEmpByExpRetireDate(bc,fDate,tDate, false);

        List<Map<String, Object>> contPenMapped = new ArrayList<>();
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i = 1;
        for (HiringInfo data : empList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTitle(), data.getEmployee().getEmployeeId());
            newData.put(bc.getStaffTypeName(), data.getEmployee().getDisplayName());
            if(bc.isPensioner()){
                newData.put("Yearly Pension", data.getYearlyPensionAmount());
                newData.put("Monthly Pension", data.getMonthlyPensionAmountStr());
                newData.put("Pension Start Date", data.getPensionStartDateStr());
                newData.put("I Am Alive Due Date", data.getExpDateOfRetireStr());
            }
            else{
                newData.put("Pay Group", data.getEmployee().getSalaryTypeName());
                newData.put("Birth Date", data.getBirthDateStr());
                newData.put("Date Hired", data.getHireDateStr());
                newData.put("Retirement Date", data.getExpDateOfRetireStr());
            }
            newData.put(bc.getMdaTitle(), data.getEmployee().getMdaName());
            contPenMapped.add(newData);
            uniqueSet.put(data.getEmployee().getMdaName(), i++);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        if(bc.isPensioner()){
            tableHeaders.add(new ReportGeneratorBean("Yearly Pension", 2));
            tableHeaders.add(new ReportGeneratorBean("Monthly Pension", 2));
            tableHeaders.add(new ReportGeneratorBean("Pension Start Date", 0));
            tableHeaders.add(new ReportGeneratorBean("I Am Alive Due Date", 0));
        }
        else{
            tableHeaders.add(new ReportGeneratorBean("Pay Group", 0));
            tableHeaders.add(new ReportGeneratorBean("Birth Date", 0));
            tableHeaders.add(new ReportGeneratorBean("Date Hired", 0));
            tableHeaders.add(new ReportGeneratorBean("Retirement Date", 0));
        }
        tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherSpecHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getStaffTypeName()+" Expected Year Of Retirement");


        rt.setBusinessCertificate(bc);
        rt.setGroupBy(bc.getMdaTitle());
        rt.setReportTitle("StaffExpYrOfRetirementInfo");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(OtherSpecHeaders);
        rt.setTableType(1);
        rt.setTotalInd(0);
        rt.setGroupedKeySet(uniqueSet.keySet());


        groupedDataExcelReportGenerator.getExcel(response, request, rt);
    }


    @RequestMapping({"/totalEmpByLgaExcel.do"})
    public void geerateTotalEbpByLgaExcelReport(
            Model model,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ReportGeneratorBean rt = new ReportGeneratorBean();

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        List<LgaMiniBean> wNEList = this.hrService.getTotalNoOfEmpPerLGA( bc);

        List<Map<String, Object>> contPenMapped = new ArrayList<>();
       // HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i = 1;
        for (LgaMiniBean data : wNEList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put("Local Govt. Area", data.getName());
            newData.put("No. Of Employees", data.getTotalElements());
            newData.put("Percentage", data.getPlaceHolder());
            contPenMapped.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean("Local Govt. Area", 0));
        tableHeaders.add(new ReportGeneratorBean("No. Of Employees", 0));
        tableHeaders.add(new ReportGeneratorBean("Percentage", 0));

        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherSpecHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add("Total "+bc.getStaffTypeName()+" By LGA");


        rt.setBusinessCertificate(bc);
        rt.setGroupBy(null);
        rt.setReportTitle("TotalEmpByLGA");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(OtherSpecHeaders);
        rt.setTableType(0);
        rt.setTotalInd(0);


        simpleExcelReportGenerator.getExcel(response, request, rt);
    }


    @RequestMapping(value = {"/totalEmpLgaByMdaExcel.do"}, method = {RequestMethod.GET})
    public void geerateTotalEmpLgaByMdaExcelReport(@RequestParam("rt") int reportType,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ReportGeneratorBean rt = new ReportGeneratorBean();

        GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();

        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        PredicateBuilder predicateBuilder = new PredicateBuilder();

        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("statusIndicator", IConstants.OFF));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));

        List<Employee> empList = (List<Employee>) this.genericService.getObjectsFromBuilder(predicateBuilder, IppmsUtils.getEmployeeClass(bc));

        List<Map<String, Object>> contPenMapped = new ArrayList<>();
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i = 1;
        for (Employee data : empList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTitle(), data.getEmployeeId());
            newData.put(bc.getStaffTypeName(), data.getDisplayName());
            newData.put("Local Govt Area", data.getLgaInfo().getName());
            newData.put("Level & Step", data.getSalaryInfo().getLevelStepStr());
            newData.put("Pay Group", data.getSalaryTypeName());
            newData.put(bc.getMdaTitle(), data.getMdaDeptMap().getMdaInfo().getName());
            contPenMapped.add(newData);
            if(reportType == 1) {
                uniqueSet.put(data.getMdaDeptMap().getMdaInfo().getName(), i++);
            }else{
                uniqueSet.put(data.getLgaInfo().getName(), i++);
            }
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        if(reportType == 1) {
            tableHeaders.add(new ReportGeneratorBean("Local Govt Area", 0));
        }
        else{
            tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 0));
        }
        tableHeaders.add(new ReportGeneratorBean("Level & Step", 0));
        tableHeaders.add(new ReportGeneratorBean("Pay Group", 0));
        if(reportType == 1) {
            tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));
        }
        else{
            tableHeaders.add(new ReportGeneratorBean("Local Govt Area", 3));
        }

        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherSpecHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        if(reportType == 1) {
            mainHeaders.add("Employee LGA By "+bc.getMdaTitle());
        }
        else{
            mainHeaders.add("Employee Details By LGA");
        }

        rt.setBusinessCertificate(bc);
        if(reportType == 1) {
            rt.setGroupBy(bc.getMdaTitle());
            rt.setReportTitle("EmpLGAByMDA");
        }
        else{
            rt.setGroupBy("Local Govt Area");
            rt.setReportTitle("EmpDetailsByLGA");
        }
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(OtherSpecHeaders);
        rt.setTableType(1);
        rt.setTotalInd(0);
        rt.setGroupedKeySet(uniqueSet.keySet());


        groupedDataExcelReportGenerator.getExcel(response, request, rt);
    }

    @RequestMapping(value = {"/employeesOnContractExcel.do"}, method = {RequestMethod.GET})
    public void employeesOnContractExcel(@RequestParam("rm") int pRunMonth,
                                         @RequestParam("ry") int pRunYear,
                                         @RequestParam("eid") Long pEmpInstId,
                                         @RequestParam("ln") String pLastName,
                                         @RequestParam("toc") int pTypeOfContract,
                                                   Model model,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) throws Exception {

        ReportGeneratorBean rt = new ReportGeneratorBean();

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        List<ContractHistory> empList = this.payrollService.getActiveContracts(0, 0, null, null,pRunMonth, pRunYear, pEmpInstId,pLastName, pTypeOfContract, bc);

        List<Map<String, Object>> contPenMapped = new ArrayList<>();
        for (ContractHistory data : empList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTitle(), data.getOgNumber());
            newData.put(bc.getStaffTypeName(), data.getEmployeeName());
            newData.put(bc.getMdaTitle(), data.getMdaName());
            newData.put("Pay Group", data.getContractType());
            newData.put("Level & Step", data.getContractLength());
            newData.put("Contract Name", data.getName());
            newData.put("Contract Start Date", data.getContractStartDateStr());
            newData.put("Contract End Date", data.getContractEndDateStr());
            newData.put("Last Modified By", data.getChangedBy());
            newData.put("Last Modified Date", data.getLastModTs());
            newData.put("Expired Date", data.getExpiredDate());
            newData.put("Retirement Date", data.getTerminationDate());
            contPenMapped.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean("Pay Group", 0));
        tableHeaders.add(new ReportGeneratorBean("Level & Step", 0));
        tableHeaders.add(new ReportGeneratorBean("Contract Name", 0));
        tableHeaders.add(new ReportGeneratorBean("Contract Start Date", 0));
        tableHeaders.add(new ReportGeneratorBean("Contract End Date", 0));
        tableHeaders.add(new ReportGeneratorBean("Last Modified By", 0));
        tableHeaders.add(new ReportGeneratorBean("Last Modified Date", 0));
        tableHeaders.add(new ReportGeneratorBean("Expired Date", 0));
        tableHeaders.add(new ReportGeneratorBean("Retirement Date", 0));

        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherSpecHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
            mainHeaders.add("Contract History Report");

        rt.setBusinessCertificate(bc);
        rt.setGroupBy(null);
        rt.setReportTitle("contact_history_report");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(OtherSpecHeaders);
        rt.setTableType(0);
        rt.setTotalInd(0);


        simpleExcelReportGenerator.getExcel(response, request, rt);
    }

}
