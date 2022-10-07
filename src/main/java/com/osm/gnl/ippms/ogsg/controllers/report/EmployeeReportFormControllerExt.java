package com.osm.gnl.ippms.ogsg.controllers.report;


import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.base.services.ExcelReportService;
import com.osm.gnl.ippms.ogsg.base.services.HRService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.TerminateReason;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.EmployeeReportService;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.GroupedDataExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SimpleExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.beans.LgaMiniBean;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.domain.report.ReportStatus;
import com.osm.gnl.ippms.ogsg.domain.transfer.TransferLog;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBeanInactive;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
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
public class EmployeeReportFormControllerExt extends BaseController {

    @Autowired
    private HRService hrService;

    @Autowired
    private EmployeeReportService employeeReportService;

    @Autowired
    private ExcelReportService excelReportService;

    @Autowired
    private EmployeeService employeeService;

    @RequestMapping({"/totalEmpByFaithExcel.do"})
    public void generateTotalEmpByReligionExcelReport(
            Model model,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ReportGeneratorBean rt = new ReportGeneratorBean();

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        List<LgaMiniBean> wDEList = this.hrService.getTotalNoOfEmpByReligion(bc);

        List<Map<String, Object>> contPenMapped = new ArrayList<>();
        for (LgaMiniBean data : wDEList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put("Religion", data.getName());
            newData.put("No. Of "+bc.getStaffTypeName(), data.getTotalElements());
            newData.put("Percentage", data.getPlaceHolder());
            contPenMapped.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean("Religion", 0));
        tableHeaders.add(new ReportGeneratorBean("No. Of "+bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean("Percentage", 0));

        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherSpecHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add("Total "+bc.getStaffTypeName()+" By Religion");


        rt.setBusinessCertificate(bc);
        rt.setGroupBy(null);
        rt.setReportTitle("TotalEmpByReligion");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(OtherSpecHeaders);
        rt.setTableType(0);
        rt.setTotalInd(0);


        simpleExcelReportGenerator.getExcel(response, request, rt);
    }


    @RequestMapping(value = {"/empReligionByMdaExcel.do"}, method = {RequestMethod.GET})
    public void generateTotalEmpLgaByMdaExcelReport(@RequestParam("rt") int reportType,
                                                   Model model,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) throws Exception {

        ReportGeneratorBean rt = new ReportGeneratorBean();

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

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
            newData.put("Religion", data.getReligion().getName());
            newData.put("Level & Step", data.getSalaryInfo().getLevelStepStr());
            newData.put("Pay Group", data.getSalaryTypeName());
            newData.put(bc.getMdaTitle(), data.getMdaDeptMap().getMdaInfo().getName());
            contPenMapped.add(newData);

        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        if(reportType == 1) {
            tableHeaders.add(new ReportGeneratorBean("Religion", 0));
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
            tableHeaders.add(new ReportGeneratorBean("Religion", 3));
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
            mainHeaders.add(bc.getStaffTypeName()+" Religion By "+bc.getMdaTitle());
        }
        else{
            mainHeaders.add(bc.getStaffTypeName()+" Details By Religion");
        }

        rt.setBusinessCertificate(bc);
        if(reportType == 1) {
            rt.setGroupBy(bc.getMdaTitle());
            rt.setReportTitle("EmpReligionByMDA");
        }
        else{
            rt.setGroupBy("Religion");
            rt.setReportTitle("EmpDetailsByReligion");
        }
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(OtherSpecHeaders);
        rt.setTableType(1);
        rt.setTotalInd(0);



        simpleExcelReportGenerator.getExcel(response, request, rt);
    }

    @RequestMapping(value = {"/empByExpYrOfBirthReport.do"}, method = {RequestMethod.GET})
    public void geerateEmpYOBExcelReport(@RequestParam("fd") String pFd, @RequestParam("td") String pTd, Model model,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) throws Exception {

        SimpleDateFormat formatter2 =new SimpleDateFormat("yyyy-mm-dd");

        Date dateFrom = formatter2.parse(pFd);
        Date dateTo = formatter2.parse(pTd);

        ReportGeneratorBean rt = new ReportGeneratorBean();


        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String strFDate= formatter.format(dateFrom);
        String strTDate= formatter.format(dateTo);

        LocalDate fDate = PayrollBeanUtils.setDateFromString(strFDate);
        LocalDate tDate = PayrollBeanUtils.setDateFromString(strTDate);

        fDate = LocalDate.of(fDate.getYear(), 1, 1);
        tDate = LocalDate.of(tDate.getYear(), 12, 31);


      /*  PredicateBuilder predicateBuilder = new PredicateBuilder();

        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("terminateInactive", "N"));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("birthDate", fDate, Operation.GREATER_OR_EQUAL));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("birthDate", tDate, Operation.LESS_OR_EQUAL));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));

     //   List<HiringInfo> empList = this.genericService.getObjectsFromBuilder(predicateBuilder, HiringInfo.class);

        List<HiringInfo> empList = this.genericService.loadAllObjectsUsingRestrictions(HiringInfo.class,
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("birthDate", fDate, Operation.GREATER_OR_EQUAL),
                        CustomPredicate.procurePredicate("birthDate", tDate, Operation.LESS_OR_EQUAL),
                        CustomPredicate.procurePredicate("terminateInactive", "N")),null);*/

        List<HiringInfo> empList = this.excelReportService.loadYOBDataForExport(fDate,tDate,bc);
        List<Map<String, Object>> contPenMapped = new ArrayList<>();
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i = 1;
        for (HiringInfo data : empList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTitle(), data.getBvnNo());
            newData.put(bc.getStaffTypeName(), data.getName());
            newData.put("Date Of Birth", data.getBirthDateStr());
            newData.put("Level & Step", data.getOldLevelAndStep());
            newData.put("Pay Group", data.getPayPeriodName());
            newData.put(bc.getMdaTitle(), data.getProposedMda());

            uniqueSet.put( data.getProposedMda(), i++);
            contPenMapped.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean("Date Of Birth", 0));
        tableHeaders.add(new ReportGeneratorBean("Level & Step", 0));
        tableHeaders.add(new ReportGeneratorBean("Pay Group", 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherSpecHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
            mainHeaders.add("Employees With Birth Dates between  "+strFDate +" and "+strTDate);

        rt.setBusinessCertificate(bc);
        rt.setGroupBy(bc.getMdaTitle());
        rt.setReportTitle("EmpDetailsByYOB");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(OtherSpecHeaders);
        rt.setTableType(1);
        rt.setTotalInd(0);
        rt.setGroupedKeySet(uniqueSet.keySet());



       /* CustomExcelReportGenerator wCP = new CustomExcelReportGenerator(rt, response, request,  contPenMapped.size());
        addSessionAttribute(request, "reportRun", wCP);
        addSessionAttribute(request, "url","selectYOBForm.do");
        Thread t = new Thread(wCP);
        t.start();*/
        new GroupedDataExcelReportGenerator().getExcel(response, request, rt);
        ReportStatus stat = new ReportStatus();
        stat.setFinished(true);
        addSessionAttribute(request, "reportStatus", stat);
    }

    @RequestMapping(value = {"/empByYearOfServiceReport.do"}, method = {RequestMethod.GET})
    public void generateEmpYOSExcelReport(@RequestParam("fy") int pFy, @RequestParam("ty") int pTy, @RequestParam("rt") int reportType, Model model,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {

        SessionManagerService.manageSession(request, model);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        boolean usingBaseYear = false;
        boolean usingToYear = false;
        BusinessCertificate bc = this.getBusinessCertificate(request);

        PaginationBean paginationBean = getPaginationInfo(request);

        LocalDate wBaseYear = null;
        LocalDate wToYearMin = null;
        LocalDate wBaseYearMax = null;
        LocalDate wToday = LocalDate.now();
        LocalDate wToYear = null;

        if (pTy > -1){
            usingToYear = true;
            int diff = wToday.getYear();
            int mainDiff = diff - pTy;
            wToYear = LocalDate.of(mainDiff, 12, 31);
            wToYearMin = LocalDate.of(mainDiff, 1, 1);
        }
        if (pFy > -1){
            usingBaseYear = true;
            int diff = wToday.getYear();
            int mainDiff = diff - pFy;
            wBaseYearMax = LocalDate.of(mainDiff, 12, 31);
            wBaseYear = LocalDate.of(mainDiff, 1, 1);
        }

        ArrayList<CustomPredicate> customPredicates = new ArrayList<>();
        customPredicates.add(getBusinessClientIdPredicate(request));
        customPredicates.add(CustomPredicate.procurePredicate("terminateInactive", "N"));
        if ((usingToYear) && (usingBaseYear)) {
            customPredicates.add(CustomPredicate.procurePredicate("hireDate", wToYearMin, Operation.GREATER_OR_EQUAL));
            customPredicates.add(CustomPredicate.procurePredicate("hireDate", wBaseYearMax, Operation.LESS_OR_EQUAL));
        }
        else if ((usingBaseYear) && (!usingToYear)){
            customPredicates.add(CustomPredicate.procurePredicate("hireDate", wBaseYear, Operation.GREATER_OR_EQUAL));
            customPredicates.add(CustomPredicate.procurePredicate("hireDate", wBaseYearMax, Operation.LESS_OR_EQUAL));
        }
        else if ((usingToYear) && (!usingBaseYear)){
            customPredicates.add(CustomPredicate.procurePredicate("hireDate", wToYearMin, Operation.GREATER_OR_EQUAL));
            customPredicates.add(CustomPredicate.procurePredicate("hireDate", wToYear, Operation.LESS_OR_EQUAL));
        }


        List<HiringInfo> empList = this.genericService.loadAllObjectsUsingRestrictions(HiringInfo.class, customPredicates, null);

        Collections.sort(empList);

        List<Map<String, Object>> contPenMapped = new ArrayList<>();
        for (HiringInfo data : empList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTitle(), data.getEmployee().getEmployeeId());
            newData.put(bc.getStaffTypeName(), data.getEmployee().getDisplayName());
            newData.put("Date Of Hire", data.getHireDateStr());
            if(reportType == 1){
                newData.put(bc.getMdaTitle(), data.getEmployee().getMdaDeptMap().getMdaInfo().getName());
            }
            else{
                newData.put("Years In Service", data.getNoOfYearsInService());
            }
            newData.put("Level & Step", data.getEmployee().getSalaryInfo().getLevelStepStr());
            newData.put("Pay Group", data.getEmployee().getSalaryTypeName());
            if(reportType == 1){
                newData.put("Years In Service", data.getNoOfYearsInService());
            }
            else {
                newData.put(bc.getMdaTitle(), data.getEmployee().getMdaDeptMap().getMdaInfo().getName());
            }
            contPenMapped.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean("Date Of Hire", 0));
        if(reportType == 1){
            tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 0));
        }
        else{
            tableHeaders.add(new ReportGeneratorBean("Years In Service", 0));
        }
        tableHeaders.add(new ReportGeneratorBean("Level & Step", 0));
        tableHeaders.add(new ReportGeneratorBean("Pay Group", 0));
        if(reportType == 1) {
            tableHeaders.add(new ReportGeneratorBean("Years In Service", 3));
        }
        else{
            tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));
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
            mainHeaders.add("Employees' Details By Year Of Service Between " + pFy + " - " + pTy + " Years");
        }
        else{
            mainHeaders.add("Employees' Details By "+bc.getMdaTitle()+" Between " + pFy + " - " + pTy + " Years");
        }

        rt.setBusinessCertificate(bc);
        if(reportType == 1) {
            rt.setGroupBy("Years In Service");
            rt.setReportTitle("EmpDetailsByYOS");
        }
        else{
            rt.setGroupBy(bc.getMdaTitle());
            rt.setReportTitle("EmpYoSDetailsByMda");
        }
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(OtherSpecHeaders);
        rt.setTableType(1);
        rt.setTotalInd(0);



        simpleExcelReportGenerator.getExcel(response, request, rt);
    }

    
    @RequestMapping({"/singleEmployeeTransferExcel.do"})
    public void setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request, HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException, IOException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = getBusinessCertificate(request);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        ArrayList<CustomPredicate> arrays = new ArrayList<>();

        arrays.add(getBusinessClientIdPredicate(request));
        if(bc.isPensioner()){
            arrays.add(CustomPredicate.procurePredicate("pensioner.id", pEmpId));
        }
        else{
            arrays.add(CustomPredicate.procurePredicate("employee.id", pEmpId));
        }

        List<TransferLog> wTransHist = this.genericService.loadAllObjectsUsingRestrictions(TransferLog.class, arrays, null);
        Collections.sort(wTransHist);



        HiringInfo wHireInfo = this.genericService.loadObjectUsingRestriction(HiringInfo.class,
                arrays);
        Employee wEmp = wHireInfo.getEmployee();
        BusinessEmpOVBeanInactive wPGBDH = new BusinessEmpOVBeanInactive(wTransHist);
        wPGBDH.setEmployeeId(wEmp.getEmployeeId());
        wPGBDH.setId(wEmp.getId());
        wPGBDH.setConfirmationDate(wHireInfo.getConfirmationDateAsStr());

        wPGBDH.setEmployeeName(wEmp.getDisplayNameWivTitlePrefixed());
        wPGBDH.setCurrentMda(wEmp.getMdaDeptMap().getMdaInfo().getName());
        wPGBDH.setCurrentLevelAndStep(wEmp.getSalaryInfo().getSalaryScaleLevelAndStepStr());

        wPGBDH.setBirthDate(wHireInfo.getBirthDate());
        wPGBDH.setHireDate(wHireInfo.getHireDate());
        wPGBDH.setCanEdit(wEmp.isSchoolStaff());
        if(wEmp.isSchoolStaff())
            wPGBDH.setDisplayErrors(wEmp.getSchoolName());
        if (wEmp.isTerminated()) {
            wPGBDH.setNoOfYearsInService(wHireInfo.getTerminateDate().getYear() - wHireInfo.getHireDate().getYear());
            wPGBDH.setTerminatedEmployee(true);
            wPGBDH.setTerminationReason(wHireInfo.getTerminateReason().getName());
            wPGBDH.setTerminationDate(PayrollHRUtils.getDisplayDateFormat().format(wHireInfo.getTerminateDate()));
        } else {
            wPGBDH.setNoOfYearsInService(LocalDate.now().getYear() - wHireInfo.getHireDate().getYear());
        }


        List<Map<String, Object>> contPenMapped = new ArrayList<>();
        for(TransferLog data : wTransHist) {
            Map<String, Object> newData = new HashMap<>();
            newData.put("Transfer Date", PayrollHRUtils.getDateFormat().format(data.getTransferDate()));
            newData.put("Transferred From", data.getOldMda());
            newData.put("Transferred To", data.getNewMda());
            newData.put("Transferred By", data.getUser().getActualUserName());
            contPenMapped.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean("Transfer Date", 0));
        tableHeaders.add(new ReportGeneratorBean("Transferred From", 0));
        tableHeaders.add(new ReportGeneratorBean("Transferred To", 0));
        tableHeaders.add(new ReportGeneratorBean("Transferred By", 0));

        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : tableHeaders) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherSpecHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getStaffTypeName()+" : "+wPGBDH.getEmployeeName());
        mainHeaders.add(bc.getStaffTitle()+" : "+wPGBDH.getEmployeeId());
        mainHeaders.add(bc.getMdaTitle()+" : "+wPGBDH.getCurrentMda());
        if(wPGBDH.isCanEdit()){
            mainHeaders.add("School : "+wPGBDH.getDisplayErrors());
        }
        mainHeaders.add("Date Of Birth : "+wPGBDH.getBirthDateStr());
        mainHeaders.add("Date Of Hire : "+wPGBDH.getHireDateStr());
        mainHeaders.add("Confirmation Date : "+wPGBDH.getConfirmationDate());
        if(wPGBDH.isTerminatedEmployee()) {
            mainHeaders.add("Last Level And Step : "+wPGBDH.getCurrentLevelAndStep());
        }else{
            mainHeaders.add("Current Level And Step : "+wPGBDH.getCurrentLevelAndStep());
        }
        mainHeaders.add("No Of Years In Service : "+wPGBDH.getNoOfYearsInService());
        mainHeaders.add("Last Level And Step : "+wPGBDH.getCurrentLevelAndStep());
        if(wPGBDH.isTerminatedEmployee()){
            mainHeaders.add("Termination Date : "+wPGBDH.getTerminationDate());
            mainHeaders.add("Termination Reason : "+wPGBDH.getTerminationReason());
        }

        rt.setBusinessCertificate(bc);
        rt.setGroupBy(null);
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(OtherSpecHeaders);
        rt.setReportTitle(wEmp.getLastName()+"_transfer_history");
        rt.setTableType(0);
        rt.setTotalInd(0);


        simpleExcelReportGenerator.getExcel(response, request, rt);
    }


    @RequestMapping({"/inactiveEmployeeList.do"})
    public void setupForm(@RequestParam("fd") String pFromDate,
                                  @RequestParam("td") String pToDate,
                                  @RequestParam("tid") Long pTid,
                                  @RequestParam("uid") Long  pUserId,
                                  @RequestParam("eid") Long  pEmpId,
                                  @RequestParam("mid") Long  pMdaId,
                                  @RequestParam("sid") Long  pSchoolId,
                                  @RequestParam("pp") String  pPayPeriod,
                                  Model model,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = getBusinessCertificate(request);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        LocalDate wStartDate = null;
        LocalDate wEndDate = null;


        boolean usingDates = false;
        boolean usingTermId = false;

        String EMPTY_STR = "";
        if ((!IppmsUtils.treatNull(pFromDate).equals(EMPTY_STR)) && (!IppmsUtils.treatNull(pToDate).equals(EMPTY_STR))) {
            wStartDate = PayrollBeanUtils.getNextORPreviousDay(PayrollBeanUtils.setDateFromString(pFromDate),false);
            wEndDate = PayrollBeanUtils.getNextORPreviousDay(PayrollBeanUtils.setDateFromString(pToDate),true);
            usingDates = !usingDates;
        }


        List<HiringInfo> empList = (List<HiringInfo>) this.employeeService.getInActiveEmployees(0,0,null,null,wStartDate, wEndDate, pTid, pUserId,pEmpId,pMdaId,pSchoolId,pPayPeriod, bc,false);

        List<AbstractSpecialAllowanceEntity> wAllowListToSplit = this.employeeReportService.loadTerminatedEmployeesSpecialAllowances(wStartDate ,wEndDate, pTid, usingDates, usingTermId,pEmpId,pUserId,pMdaId,pSchoolId,pPayPeriod, bc);

        HashMap<Long, List<AbstractSpecialAllowanceEntity>> wAllowanceMap =  breakUpAllowanceList(wAllowListToSplit);


        if(bc.isPensioner()){

            for (HiringInfo h : empList) {
                double yearlyGross = h.getYearlyPensionAmount();

                double monthlySalary = h.getMonthlyPensionAmount();

                double totalAllowance = 0.0D;

                List<AbstractSpecialAllowanceEntity> wList = wAllowanceMap.get(h.getEmployee().getId());

                if (wList == null || wList.isEmpty()) {
                    h.setTerminalGrossPay(yearlyGross);
                    continue;
                } else {
                    for (AbstractSpecialAllowanceEntity s : wList) {


                        double rate;
                        double allowanceAmount;
                        if (s.getPayTypes().getName().contains("%")) {
                            rate = s.getAmount();
                            if ((rate >= 100.0D) || (rate < 0.0D)) {
                                continue;
                            }

                            rate /= 100.0D;

                            allowanceAmount = monthlySalary * rate;

                        } else {
                            allowanceAmount = s.getAmount();


                        }
                        allowanceAmount = EntityUtils.convertDoubleToEpmStandard(allowanceAmount);
                        totalAllowance += allowanceAmount;

                    }
                    yearlyGross += totalAllowance;
                    h.setTerminalGrossPay(yearlyGross);
                }
            }
        }else {
            List<SalaryInfo> _wList = this.genericService.loadAllObjectsWithSingleCondition(SalaryInfo.class,
                    CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), null);
            Map<Long, SalaryInfo> wMap = breakSalaryInfo(_wList);
            for (HiringInfo h : empList) {

                List<AbstractSpecialAllowanceEntity> wList = wAllowanceMap.get(h.getEmployee().getId());
                SalaryInfo wSI = wMap.get(h.getEmployee().getSalaryInfo().getId());
                double yearlyGross = wSI.getAnnualSalary();

                double monthlySalary = wSI.getMonthlyGrossPay();
                double totalAllowance = 0.0D;
                if (wList == null || wList.isEmpty()) {
                    h.setTerminalGrossPay(yearlyGross);
                    continue;
                } else {
                    for (AbstractSpecialAllowanceEntity s : wList) {

                        if (s.getSpecialAllowanceType().isArrearsType()) {
                            continue;
                        }

                        double rate;
                        double allowanceAmount;
                        if (s.getPayTypes().getName().contains("%")) {
                            rate = s.getAmount();
                            if ((rate >= 100.0D) || (rate < 0.0D)) {
                                continue;
                            }

                            rate /= 100.0D;

                            allowanceAmount = monthlySalary * rate;

                        } else {
                            allowanceAmount = s.getAmount();


                        }
                        allowanceAmount = EntityUtils.convertDoubleToEpmStandard(allowanceAmount);
                        totalAllowance += allowanceAmount;

                    }
                    yearlyGross += totalAllowance;
                    h.setTerminalGrossPay(yearlyGross);
                }
            }
        }

        BusinessEmpOVBeanInactive pList = new BusinessEmpOVBeanInactive(empList);

        if (usingDates)
        {
            pList.setToDate(PayrollBeanUtils.setDateFromString(pToDate));
            pList.setFromDate(PayrollBeanUtils.setDateFromString(pFromDate));
            pList.setFromDateStr(PayrollBeanUtils.getDateAsString(PayrollBeanUtils.setDateFromString(pFromDate)));
            pList.setToDateStr(PayrollBeanUtils.getDateAsString(PayrollBeanUtils.setDateFromString(pToDate)));

        }
        else {
            pList.setFromDateStr(EMPTY_STR);
            pList.setToDateStr(EMPTY_STR);
        }
        if (usingTermId) {
            TerminateReason tR = this.genericService.loadObjectWithSingleCondition(TerminateReason.class, CustomPredicate.procurePredicate("id", pTid));
            pList.setTerminationReasonStr(tR.getDescription());
        }

        pList.setUsingDates(usingDates);
        pList.setUsingTermReason(usingTermId);
        pList.setTerminateReasonId(pTid);

        List<Map<String, Object>> contPenMapped = new ArrayList<>();
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i = 1;
        for (HiringInfo data : empList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTitle(), data.getEmployee().getEmployeeId());
            newData.put(bc.getStaffTypeName(), data.getEmployee().getDisplayName());
            newData.put("Date Of Birth", data.getBirthDateStr());
            newData.put("Date Of Hire", data.getHireDateStr());
            if(bc.isPensioner()){
                newData.put("Pension Start Date", data.getPensionStartDateStr());
                newData.put("Pension End Date", data.getPensionEndDateStr());
                newData.put("Years On Pension", data.getYearsOnPension());
                newData.put("Age At Termination", data.getAgeAtTermination());
            }else{
                newData.put("Total Years In Service", data.getNoOfYearsInService());
                newData.put("Age At Termination", data.getAgeAtTermination());
                newData.put("Last Pay Group", data.getAccountNumber());
                newData.put("Termination Date", data.getTerminatedDateStr());
            }

            newData.put("Monetary Implication", data.getTerminalGrossPay());
            newData.put("Termination Reason", data.getReligionStr());
            newData.put("Terminated By", data.getLgaName());
            newData.put("Last "+bc.getMdaTitle(), data.getEmployee().getMdaDeptMap().getMdaInfo().getName());
            uniqueSet.put(data.getEmployee().getMdaDeptMap().getMdaInfo().getName(), i++);
            contPenMapped.add(newData);
               }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean("Date Of Birth", 0));
        tableHeaders.add(new ReportGeneratorBean("Date Of Hire", 0));
        if(bc.isPensioner()){
            tableHeaders.add(new ReportGeneratorBean("Pension Start Date", 0));
            tableHeaders.add(new ReportGeneratorBean("Pension End Date", 0));
            tableHeaders.add(new ReportGeneratorBean("Years On Pension", 0));
            tableHeaders.add(new ReportGeneratorBean("Age At Termination", 0));
        }else{
            tableHeaders.add(new ReportGeneratorBean("Total Years In Service", 0));
            tableHeaders.add(new ReportGeneratorBean("Age At Termination", 0));
            tableHeaders.add(new ReportGeneratorBean("Last Pay Group", 0));
            tableHeaders.add(new ReportGeneratorBean("Termination Date", 0));

        }
         tableHeaders.add(new ReportGeneratorBean("Monetary Implication", 2));
         tableHeaders.add(new ReportGeneratorBean("Termination Reason", 0));
        tableHeaders.add(new ReportGeneratorBean("Terminated By", 0));
        tableHeaders.add(new ReportGeneratorBean("Last "+bc.getMdaTitle(), 3));

        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherSpecHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        if(pList.isUsingDates()) {
            mainHeaders.add("Inactive " + bc.getStaffTypeName() + " Between " + pList.getFromDateStr() + " - " + pList.getToDateStr());
        }
        if(pList.isUsingTermReason()) {
            mainHeaders.add("Reason: " + pList.getTerminationReasonStr());
        }

        rt.setBusinessCertificate(bc);
        rt.setGroupBy("Last "+bc.getMdaTitle());
        rt.setReportTitle("Inactive_"+bc.getStaffTypeName());
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(OtherSpecHeaders);
        rt.setGroupedKeySet(uniqueSet.keySet());
        rt.setTableType(1);
        rt.setTotalInd(1);



        new GroupedDataExcelReportGenerator().getExcel(response, request, rt);
        ReportStatus stat = new ReportStatus();
        stat.setFinished(true);
        addSessionAttribute(request, "reportStatus", stat);

    }

    private HashMap<Long, List<AbstractSpecialAllowanceEntity>> breakUpAllowanceList(List<AbstractSpecialAllowanceEntity> pAllowListToSplit)
    {
        HashMap<Long, List<AbstractSpecialAllowanceEntity>> wRetVal = new HashMap<>();
        if ((pAllowListToSplit == null) || (pAllowListToSplit.isEmpty()))
            return wRetVal;

        for (AbstractSpecialAllowanceEntity e : pAllowListToSplit)
        {


            if (e.isExpired() || e.getAmount() == 0) {
                continue;
            }
            if (wRetVal.containsKey(e.getParentId())) {
                wRetVal.get(e.getParentId()).add(e);
            } else {
                ArrayList<AbstractSpecialAllowanceEntity> wList = new ArrayList<>();
                wList.add(e);
                wRetVal.put(e.getParentId(), wList);
            }
        }
        return wRetVal;
    }

    private Map<Long, SalaryInfo> breakSalaryInfo(List<SalaryInfo> pList)
    {
        Map<Long, SalaryInfo> wMap = new HashMap<>();
        for (SalaryInfo s : pList) {

            wMap.put(s.getId(), s);
        }
        return wMap;
    }
}
