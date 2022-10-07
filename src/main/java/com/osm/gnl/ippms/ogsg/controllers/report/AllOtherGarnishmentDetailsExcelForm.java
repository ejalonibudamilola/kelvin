package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.LoanService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.customizedpdfs.*;
import com.osm.gnl.ippms.ogsg.controllers.report.service.GarnishmentService;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.GroupedDataExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.PdfSimpleGeneratorClass;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SimpleExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductGarnMiniBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductionDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductionMiniBean;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import com.osm.gnl.ippms.ogsg.domain.report.LoanSchedule;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.pagination.beans.PayrollSummaryBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpDeductMiniBean;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpGarnMiniBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


@SuppressWarnings("UnusedAssignment")
@Controller
public class AllOtherGarnishmentDetailsExcelForm extends BaseController
{

    /**
     * Kasumu Taiwo
     * 12-2020
     */


  private final GarnishmentService garnishmentService;
  private final  LoanService loanService;

  @Autowired
  public AllOtherGarnishmentDetailsExcelForm(GarnishmentService garnishmentService, LoanService loanService  )
  {

    this.garnishmentService = garnishmentService;
    this.loanService = loanService;

  }

  @RequestMapping({"/allOtherGarnishmentDetailsExcel.do"})
  public void setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear,
		  @RequestParam("pid") Long pBusClientId, @RequestParam("rt") int reportType,  Model model, HttpServletRequest request, HttpServletResponse response) throws Exception
  {
	    LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
	    LocalDate eDate = PayrollBeanUtils.getEndOfMonth(sDate);
		String pSDate = PayrollBeanUtils.getJavaDateAsString(sDate);
		String pEDate = PayrollBeanUtils.getJavaDateAsString(eDate);
    DeductionDetailsBean deductDetails = new DeductionDetailsBean();

    ReportGeneratorBean rt = new ReportGeneratorBean();

    SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

    BusinessCertificate certificate = getBusinessCertificate(request);

    List<AbstractPaycheckGarnishmentEntity> paycheckDeductions;

    deductDetails.setFromDate(sDate);
    deductDetails.setToDate(eDate);

    deductDetails.setCurrentDeduction("All Garnishments");

    deductDetails.setId(pBusClientId);
    deductDetails.setFromDateStr(pSDate);
    deductDetails.setToDateStr(pEDate);
    BusinessClient bc = this.genericService.loadObjectById(BusinessClient.class,pBusClientId);
    deductDetails.setCompanyName(bc.getName());

    paycheckDeductions = this.garnishmentService.loadEmpGarnishmentsByParentIdAndPayPeriod(null, pRunMonth, pRunYear, certificate);
    DeductGarnMiniBean d;
    List<DeductGarnMiniBean> GarnList = new ArrayList<>();
    HashMap<Long, DeductGarnMiniBean> uniqueSet = new HashMap<>();

    for (AbstractPaycheckGarnishmentEntity p : paycheckDeductions)
    {
      if (!uniqueSet.containsKey(p.getEmpGarnInfo().getEmpGarnishmentType().getId())) {
        d = new DeductGarnMiniBean();
      }
      else{
        d = uniqueSet.get(p.getEmpGarnInfo().getEmpGarnishmentType().getId());
      }



      d.setAmount(d.getAmount() + p.getAmount());
      d.setName(p.getEmpGarnInfo().getEmpGarnishmentType().getDescription());
      d.setDisplayName(p.getEmpGarnInfo().getEmpGarnishmentType().getName());
      d.setDeductionId(p.getEmpGarnInfo().getId());

      uniqueSet.put(p.getEmpGarnInfo().getEmpGarnishmentType().getId(), d);
    }

    for(Long x : uniqueSet.keySet()){
      GarnList.add(uniqueSet.get(x));
    }

    Collections.sort(GarnList,Comparator.comparing(DeductGarnMiniBean::getName));

    List<Map<String, Object>> garnSummaryList = new ArrayList<>();
    for (DeductGarnMiniBean data : GarnList) {
      Map<String, Object> newData = new HashMap<>();
      newData.put("Loan Name", data.getName());
      newData.put("Code", data.getDisplayName());
      newData.put("Loan Deduction", data.getAmount());
      garnSummaryList.add(newData);
    }

    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
    tableHeaders.add(new ReportGeneratorBean("Loan Name", 0));
    tableHeaders.add(new ReportGeneratorBean("Code", 0));
    tableHeaders.add(new ReportGeneratorBean("Loan Deduction", 2));

    List<Map<String, Object>> GarnMapHeaders = new ArrayList<>();
    for(ReportGeneratorBean head : tableHeaders){
      Map<String, Object> mappedHeader = new HashMap<>();
      mappedHeader.put("headerName", head.getHeaderName());
      mappedHeader.put("totalInd", head.getTotalInd());
      GarnMapHeaders.add(mappedHeader);
    }

    List<String> mainHeaders = new ArrayList<>();
    mainHeaders.add(bc.getName()+" Garnishment Report");
    mainHeaders.add("Pay Period: "+deductDetails.getFromDate() +" - "+deductDetails.getToDate());


    rt.setBusinessCertificate(certificate);
    rt.setGroupBy(null);
    rt.setReportTitle(bc.getName() + " Garnishment Report");
    rt.setMainHeaders(mainHeaders);
    rt.setSubGroupBy(null);
    rt.setTableData(garnSummaryList);
    rt.setTableHeaders(GarnMapHeaders);
    rt.setTableType(0);
    rt.setTotalInd(1);

    if (reportType == 1) {
      simpleExcelReportGenerator.getExcel(response, request, rt);
    }
    else {
      rt.setOutputInd(true);
      rt.setWatermark(bc.getName() + " Loan Summary");
      new UngroupedPdf().getPdf(response, request, rt);
    }
  }

  @RequestMapping({"/garnishmentWithDetailsExcel.do"})
  public void setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear,
		  @RequestParam("pid") Long pBusClientId, @RequestParam("exp") String pFactor, Model model, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
	    LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
	    LocalDate eDate = PayrollBeanUtils.getEndOfMonth(sDate);
		String pSDate = PayrollBeanUtils.getJavaDateAsString(sDate);
		String pEDate = PayrollBeanUtils.getJavaDateAsString(eDate);
		
    DeductionDetailsBean deductDetails = new DeductionDetailsBean();
    HashMap <Long,DeductGarnMiniBean>deductionBean = new HashMap<>();

    BusinessCertificate certificate = getBusinessCertificate(request);

    ReportGeneratorBean rt = new ReportGeneratorBean();

    GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();

    List <AbstractPaycheckGarnishmentEntity>paycheckDeductions;

    deductDetails.setFromDate(sDate);
    deductDetails.setToDate(eDate);

    deductDetails.setCurrentDeduction("All Garnishments");

    deductDetails.setId(pBusClientId);
    deductDetails.setFromDateStr(pSDate);
    deductDetails.setToDateStr(pEDate);
    BusinessClient bc = this.genericService.loadObjectById(BusinessClient.class,pBusClientId);
    deductDetails.setCompanyName(bc.getName());

    paycheckDeductions = this.garnishmentService.loadEmpGarnishmentsByParentIdAndPayPeriod(null, pRunMonth, pRunYear, getBusinessCertificate(request));
    DeductGarnMiniBean d;
    DeductionMiniBean dmb;

    List<DeductionMiniBean> garnBeanList = new ArrayList<>();
    for (AbstractPaycheckGarnishmentEntity p : paycheckDeductions)
    {
      if (deductionBean.containsKey(p.getEmpGarnInfo().getEmpGarnishmentType().getId())) {
         d = deductionBean.get(p.getEmpGarnInfo().getEmpGarnishmentType().getId());
      }
      else {
        d = new DeductGarnMiniBean();
        
        d.setDeductionId(p.getEmpGarnInfo().getId());
       
      }
      d.setAmount(d.getAmount() + p.getAmount());
      d.setName(p.getEmpGarnInfo().getEmpGarnishmentType().getDescription() + " [" + p.getEmpGarnInfo().getEmpGarnishmentType().getName() + " ]");
      dmb = new DeductionMiniBean();
      dmb.setName(PayrollHRUtils.createDisplayName(p.getEmployee().getLastName(), p.getEmployee().getFirstName(), p.getEmployee().getInitials()));
      dmb.setId(p.getEmployee().getId());
      dmb.setMode(p.getEmployee().getEmployeeId());
      dmb.setAmount(p.getAmount());
      dmb.setOwedAmount(p.getEmpGarnInfo().getOwedAmount());
      dmb.setMonthlyInterest(p.getInterestPaid());
      dmb.setGarnishmentType(p.getEmpGarnInfo().getEmpGarnishmentType().getName());

      garnBeanList.add(dmb);

      d.getEmployees().add(dmb);
      deductionBean.put(p.getEmpGarnInfo().getEmpGarnishmentType().getId(), d);
      deductDetails.setTotal(deductDetails.getTotal() + p.getAmount());

      d.setBalanceAmount(d.getBalanceAmount() + p.getEmpGarnInfo().getOwedAmount());
    }

    List<Map<String, Object>> garnSummaryList = new ArrayList<>();
    HashMap<String, Integer> uniqueSet = new HashMap<>();
    int i = 1;
    for (DeductionMiniBean data : garnBeanList) {
      Map<String, Object> newData = new HashMap<>();
      newData.put(certificate.getStaffTitle(), data.getMode());
      newData.put(certificate.getStaffTypeName(), data.getName());
      newData.put("Amount Withheld", data.getAmount());
      newData.put("Outstanding Balance", data.getOwedAmount());
      newData.put("Interest Paid", data.getMonthlyInterest());
      newData.put("Type", data.getGarnishmentType());
      garnSummaryList.add(newData);
      uniqueSet.put(data.getGarnishmentType(), i++);
    }

    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
    tableHeaders.add(new ReportGeneratorBean(certificate.getStaffTitle(), 0));
    tableHeaders.add(new ReportGeneratorBean(certificate.getStaffTypeName(), 0));
    tableHeaders.add(new ReportGeneratorBean("Amount Withheld", 2));
    tableHeaders.add(new ReportGeneratorBean("Outstanding Balance", 2));
    tableHeaders.add(new ReportGeneratorBean("Interest Paid", 2));
    tableHeaders.add(new ReportGeneratorBean("Type", 3));

    List<Map<String, Object>> GarnMapHeaders = new ArrayList<>();
    for(ReportGeneratorBean head : tableHeaders){
      Map<String, Object> mappedHeader = new HashMap<>();
      mappedHeader.put("headerName", head.getHeaderName());
      mappedHeader.put("totalInd", head.getTotalInd());
      GarnMapHeaders.add(mappedHeader);
    }

    List<String> mainHeaders = new ArrayList<>();
    mainHeaders.add("Ogun State Garnishment Report Summary");
    mainHeaders.add("Pay Period: "+deductDetails.getFromDate() +" - "+deductDetails.getToDate());

    rt.setBusinessCertificate(certificate);
    rt.setGroupBy("Type");
    rt.setReportTitle("Ogun State Garnishment Report Summary");
    rt.setMainHeaders(mainHeaders);
    rt.setSubGroupBy(null);
    rt.setTableData(garnSummaryList);
    rt.setTableHeaders(GarnMapHeaders);
    rt.setTableType(1);
    rt.setTotalInd(1);
    rt.setGroupedKeySet(uniqueSet.keySet());
    groupedDataExcelReportGenerator.getExcel(response, request, rt);

  }


    @RequestMapping({"/garnishmentByMDAExcel.do"})
    public void setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, Model model, HttpServletRequest request,
                                  HttpServletResponse response) throws EpmAuthenticationException, HttpSessionRequiredException, IOException {
        SessionManagerService.manageSession(request, model);

        PayrollSummaryBean p = new PayrollSummaryBean();
        LocalDate fromDate;

      LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
     /* LocalDate eDate = PayrollBeanUtils.getEndOfMonth(sDate);
      //String pSDate = PayrollBeanUtils.getJavaDateAsString(sDate);
     // String pEDate = PayrollBeanUtils.getJavaDateAsString(eDate);*/

        BusinessCertificate bc = getBusinessCertificate(request);

      ReportGeneratorBean rt = new ReportGeneratorBean();

      GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();


        List<EmpGarnMiniBean> empBeanList = this.garnishmentService.loadEmpGarnMiniBeanByFromDateToDate(sDate, bc);


        p.setPayDate(sDate);
        p.setRunMonth(sDate.getMonthValue());
        p.setRunYear(sDate.getYear());
        p.setEmpDeductionBeanList(empBeanList);

      List<Map<String, Object>> garnSummaryList = new ArrayList<>();
      HashMap<String, Integer> uniqueSet = new HashMap<>();
      int i = 1;
      for (EmpGarnMiniBean data : empBeanList) {
        Map<String, Object> newData = new HashMap<>();
        newData.put(bc.getStaffTypeName(), data.getName());
        newData.put(bc.getStaffTitle(), data.getEmployeeId());
        newData.put("Amount", data.getCurrentGarnishment());
        newData.put("Balance", data.getYearToDate());
        newData.put("Loan Start Date", data.getStartDate());
        newData.put("Expected End Date", data.getEndDate());
        newData.put("Garnishment", data.getGarnishmentName());
        garnSummaryList.add(newData);
        uniqueSet.put(data.getGarnishmentName(), i++);
      }

      List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
      tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
      tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
      tableHeaders.add(new ReportGeneratorBean("Amount", 2));
      tableHeaders.add(new ReportGeneratorBean("Balance", 2));
      tableHeaders.add(new ReportGeneratorBean("Loan Start Date", 0));
      tableHeaders.add(new ReportGeneratorBean("Expected End Date", 0));
      tableHeaders.add(new ReportGeneratorBean("Garnishment", 3));

      List<Map<String, Object>> garnMapHeaders = new ArrayList<>();
      for(ReportGeneratorBean head : tableHeaders){
        Map<String, Object> mappedHeader = new HashMap<>();
        mappedHeader.put("headerName", head.getHeaderName());
        mappedHeader.put("totalInd", head.getTotalInd());
        garnMapHeaders.add(mappedHeader);
      }

      List<String> mainHeaders = new ArrayList<>();
      mainHeaders.add("Loans For The Month - "+ PayrollHRUtils.getMonthYearDateFormat().format(p.getPayDate()));

      rt.setBusinessCertificate(bc);
      rt.setGroupBy("Garnishment");
      rt.setReportTitle("Loans By Garnishment Type");
      rt.setMainHeaders(mainHeaders);
      rt.setSubGroupBy(null);
      rt.setTableData(garnSummaryList);
      rt.setTableHeaders(garnMapHeaders);
      rt.setTableType(1);
      rt.setTotalInd(1);
      rt.setGroupedKeySet(uniqueSet.keySet());
      groupedDataExcelReportGenerator.getExcel(response, request, rt);

    }


  @RequestMapping({"/groupLoansByMDAExcel.do"})
  public void setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, @RequestParam("mda") String pMda,
                        @RequestParam("rt") int reportType,Model model, HttpServletRequest request,
                                HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException {
    SessionManagerService.manageSession(request, model);


//    PayrollSummaryBean p = new PayrollSummaryBean();

    LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
    LocalDate eDate = PayrollBeanUtils.getEndOfMonth(sDate);

    ReportGeneratorBean rt = new ReportGeneratorBean();

    GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();

    BusinessCertificate bc = getBusinessCertificate(request);


    List<EmpGarnMiniBean> empBeanList = this.garnishmentService.loadEmpGarnMiniBeanByFromDateToDate(sDate, bc);

    Collections.sort(empBeanList,Comparator.comparing(EmpGarnMiniBean::getName));

    List<Map<String, Object>> garnSummaryList = new ArrayList<>();
    LinkedHashMap<String, Integer> uniqueSet = new LinkedHashMap<>();
    LinkedHashMap<String, Integer> uniqueSubGroupedSet = new LinkedHashMap<>();

    Map<String, Object> newData;
    int i = 1;
    for (EmpGarnMiniBean data : empBeanList) {
      newData = new HashMap<>();
      newData.put(bc.getStaffTitle(), data.getEmployeeId());
      newData.put(bc.getStaffTypeName(), data.getName());
      newData.put("Loan Deduction", data.getCurrentGarnishment());
      newData.put("Loan Balance", data.getYearToDate());
      newData.put("Loan Name", data.getGarnishmentName());
      newData.put(bc.getMdaTitle(), data.getMdaName());
      uniqueSet.put(data.getGarnishmentName(), i++);
      uniqueSubGroupedSet.put(data.getMdaName(), i++);
      garnSummaryList.add(newData);
    }

    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
    tableHeaders.add(new ReportGeneratorBean("Loan Deduction", 2));
    tableHeaders.add(new ReportGeneratorBean("Loan Balance", 2));
    tableHeaders.add(new ReportGeneratorBean("Loan Name", 3));
    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

    List<Map<String, Object>> garnMapHeaders = new ArrayList<>();
    for(ReportGeneratorBean head : tableHeaders){
      Map<String, Object> mappedHeader = new HashMap<>();
      mappedHeader.put("headerName", head.getHeaderName());
      mappedHeader.put("totalInd", head.getTotalInd());
      garnMapHeaders.add(mappedHeader);
    }

    List<String> mainHeaders = new ArrayList<>();
    mainHeaders.add("Loans For The Month - "+ PayrollHRUtils.getMonthYearDateFormat().format(eDate));

    rt.setBusinessCertificate(bc);
    rt.setGroupBy("Loan Name");
    rt.setReportTitle("Loans By MDA");
    rt.setMainHeaders(mainHeaders);
    rt.setSubGroupBy(bc.getMdaTitle());
    rt.setTableData(garnSummaryList);
    rt.setTableHeaders(garnMapHeaders);
    rt.setWatermark(bc.getBusinessName()+" Loans By "+bc.getMdaTitle());
    rt.setTotalInd(1);
    rt.setGroupedKeySet(uniqueSet.keySet());
    rt.setSubGroupedKeySet(uniqueSubGroupedSet.keySet());

    if(reportType == 1) {
      rt.setTableType(1);
      groupedDataExcelReportGenerator.getExcel(response, request, rt);
    }
    else {
      rt.setTableType(2);
      rt.setOutputInd(true);
      rt.setWatermark(bc.getBusinessName()+" Loans By "+bc.getMdaTitle());
      new SubGroupedPdf().getPdf(response, request, rt);
    }
  }

  @RequestMapping({"/garnishmentDetailsExcel.do"})
  public void setupForm(@RequestParam("did") Long dedId,
		  @RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, @RequestParam("pid") Long pBusClientId,
                      @RequestParam("rt") int reportType,  Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

	  ReportGeneratorBean rt = new ReportGeneratorBean();

    EmpGarnishmentType d = this.genericService.loadObjectById(EmpGarnishmentType.class,dedId);
    List <AbstractPaycheckGarnishmentEntity>  paycheckDeductions = this.garnishmentService.loadEmpGarnishmentsByParentIdAndPayPeriod(dedId, pRunMonth, pRunYear, getBusinessCertificate(request));

    int i = 0;
    List<Map<String, Object>> garnSummaryList = new ArrayList<>();
    LinkedHashMap<String, Integer> uniqueSet = new LinkedHashMap<>();
    LinkedHashMap<String, Integer> subUniqueSet = new LinkedHashMap<>();
    LinkedHashMap<String,List<String>> mdaSchoolMap = new LinkedHashMap<>();
    List<Map<String, Object>> schoolList = new ArrayList<>();
    Map<String, Object> newData;
    List<String> aList;
    for (AbstractPaycheckGarnishmentEntity data : paycheckDeductions) {
      newData = new HashMap<>();
      newData.put(bc.getStaffTypeName(), data.getEmployee().getDisplayName());
      newData.put(bc.getStaffTitle(), data.getEmployee().getEmployeeId());
      newData.put("Amount Paid", data.getAmount());
      newData.put("Outstanding Balance", data.getEmpGarnInfo().getOwedAmount());
      newData.put("Loan Name", data.getEmpGarnInfo().getEmpGarnishmentType().getName());
      newData.put(bc.getMdaTitle(), data.getMdaName());
      newData.put("School",data.getSchoolName());

      uniqueSet.put(data.getEmpGarnInfo().getEmpGarnishmentType().getName(), i++);
      subUniqueSet.put(data.getMdaName(), i++);
      if(IppmsUtils.isNotNullOrEmpty(data.getSchoolName())){
        if(mdaSchoolMap.containsKey(data.getMdaName())){
          aList = mdaSchoolMap.get(data.getMdaName());
          if(!aList.contains(data.getSchoolName()))
            aList.add(data.getSchoolName());
        }else{
          aList = new ArrayList<>();
          aList.add(data.getSchoolName());
        }
        mdaSchoolMap.put(data.getMdaName(),aList);
        schoolList.add(newData);
      }else{
        garnSummaryList.add(newData);
      }


    }

    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
    tableHeaders.add(new ReportGeneratorBean("Amount Paid", 2));
    tableHeaders.add(new ReportGeneratorBean("Outstanding Balance", 2));
    tableHeaders.add(new ReportGeneratorBean("Loan Name", 3));
    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));
    tableHeaders.add(new ReportGeneratorBean("School", 3));

    List<Map<String, Object>> garnMapHeaders = new ArrayList<>();
    for(ReportGeneratorBean head : tableHeaders){
      Map<String, Object> mappedHeader = new HashMap<>();
      mappedHeader.put("headerName", head.getHeaderName());
      mappedHeader.put("totalInd", head.getTotalInd());
      garnMapHeaders.add(mappedHeader);
    }

    List<String> mainHeaders = new ArrayList<>();
    mainHeaders.add(" "+ d.getName()+" Loan");
    List<String> mainHeaders2 = new ArrayList<>();

    mainHeaders2.add("Pay Period: "+ PayrollHRUtils.getMonthYearDateFormat().format(PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear)));
    rt.setMdaSchoolMap(mdaSchoolMap);
    rt.setBusinessCertificate(bc);
    rt.setMainHeaders(mainHeaders);
    rt.setMainHeaders2(mainHeaders2);
    rt.setTableData(garnSummaryList);
    rt.setTableHeaders(garnMapHeaders);
    rt.setTableSubData(schoolList);
    rt.setTableSubHeaders(garnMapHeaders);
    rt.setTotalInd(1);
    if(IppmsUtils.isNull(d.getName()))
      d.setName("noType");

    if(reportType == 1) {
      rt.setGroupedKeySet(subUniqueSet.keySet());
      rt.setGroupBy(bc.getMdaTitle());
      rt.setSubGroupBy(null);
      rt.setTableType(1);
      rt.setReportTitle("Detailed_Loans_For_"+d.getName()+"_"+PayrollBeanUtils.getCurrentTime(true));
      new GroupedDataExcelReportGenerator().getExcel(response, request, rt);
    }
    else {
      rt.setTableType(3);
      rt.setGroupBy("Loan Name");
      rt.setSubGroupBy(bc.getMdaTitle());
      rt.setDoubleSubGroupBy("School");
      rt.setGroupedKeySet(uniqueSet.keySet());
      rt.setSubGroupedKeySet(subUniqueSet.keySet());
      rt.setOutputInd(true);
      rt.setUnUsedHeaders(3);
      rt.setReportTitle("Detailed_Loan_Report_By_Garnishment_Type");
      rt.setWatermark(bc.getBusinessName()+" Loans By "+bc.getMdaTitle());
      new SubGroupedPdfExt().getPdf(response, request, rt);
    }
  }


  @RequestMapping({"/singleLoanByMdaExcel.do"})
  public ModelAndView setupFormForSingleLoanByMda(@RequestParam("did") Long pGarnTypeId, @RequestParam("rm") int pRunMonth,@RequestParam("ry") int pRunYear, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

	    PayrollSummaryBean p = new PayrollSummaryBean();
	    LocalDate fromDate;

	    fromDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
	    
	    List<EmpGarnMiniBean> empBeanList = this.garnishmentService.loadEmpGarnMiniBeanByFromDateToDateAndType(fromDate,pGarnTypeId, getBusinessCertificate(request));
     
	    List<EmpGarnishmentType> wGarnList = new ArrayList<EmpGarnishmentType>();
	    
	    EmpGarnishmentType wEGT = this.genericService.loadObjectById(EmpGarnishmentType.class, pGarnTypeId);
	    
	    wGarnList.add(wEGT);
	    
	    
	    p.setPayDate(fromDate);

	    p.setDeductionList(wGarnList);
	    p.setEmpDeductionBeanList(empBeanList);

	    return new ModelAndView("singleLoanByMdas", "singleLoanByMdaBean", p);
  }


  @RequestMapping({"/loanBYTSC.do"})
  public void loanTSC(@RequestParam("rm") int wRunMonth,
                              @RequestParam("ry") int wRunYear, @RequestParam("rt") int reportType, HttpServletRequest request,
                              HttpServletResponse response, ModelAndView mv) throws Exception {
      BusinessCertificate bc = getBusinessCertificate(request);
      LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(
              wRunMonth, wRunYear);

      ReportGeneratorBean rt = new ReportGeneratorBean();

      GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();

      PdfSimpleGeneratorClass pdfSimpleGeneratorClass = new PdfSimpleGeneratorClass();

      String pSDate = PayrollBeanUtils.getJavaDateAsString(sDate);
      List<LoanSchedule> loanByTsc = this.garnishmentService.loanScheduleTSC(wRunMonth, wRunYear, bc);

    List<Map<String, Object>> garnSummaryList = new ArrayList<>();
    HashMap<String, Integer> uniqueSet = new HashMap<>();
    int i = 1;
    for (LoanSchedule data : loanByTsc) {
      Map<String, Object> newData = new HashMap<>();
      newData.put(bc.getStaffTypeName(), data.getEmployeeName());
      newData.put(bc.getStaffTitle(), data.getEmployeeNumber());
      newData.put("Amount", data.getLoanDeduction());
      newData.put("Balance", data.getLoanBalance());
      newData.put("Period", data.getPeriod());
      newData.put("School", data.getSchoolName());
      garnSummaryList.add(newData);
      uniqueSet.put(data.getSchoolName(), i++);
    }

    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
    tableHeaders.add(new ReportGeneratorBean("Amount", 2));
    tableHeaders.add(new ReportGeneratorBean("Balance", 2));
    tableHeaders.add(new ReportGeneratorBean("Period", 0));
    tableHeaders.add(new ReportGeneratorBean("School", 3));

    List<Map<String, Object>> GarnMapHeaders = new ArrayList<>();
    for(ReportGeneratorBean head : tableHeaders){
      Map<String, Object> mappedHeader = new HashMap<>();
      mappedHeader.put("headerName", head.getHeaderName());
      mappedHeader.put("totalInd", head.getTotalInd());
      GarnMapHeaders.add(mappedHeader);
    }

    List<String> mainHeaders = new ArrayList<>();
    mainHeaders.add("Loans For The Month - "+ pSDate);

    rt.setBusinessCertificate(bc);
    rt.setGroupBy("School");
    rt.setReportTitle("Loans By School");
    rt.setMainHeaders(mainHeaders);
    rt.setSubGroupBy(null);
    rt.setTableData(garnSummaryList);
    rt.setTableHeaders(GarnMapHeaders);
    rt.setTableType(1);
    rt.setTotalInd(1);
    rt.setGroupedKeySet(uniqueSet.keySet());

    if (reportType == 1) {
      groupedDataExcelReportGenerator.getExcel(response, request, rt);
    }
    else{
      rt.setWatermark(bc.getBusinessName()+" Loans By School");
      rt.setOutputInd(true);
      pdfSimpleGeneratorClass.getPdf(response, request, rt);
    }
  }

  @RequestMapping({"/globalLoan.do"})
  public void globalLoan(@RequestParam("rm") int wRunMonth,
                                 @RequestParam("ry") int wRunYear, HttpServletRequest request,
                                 HttpServletResponse response, ModelAndView mv) throws Exception {
      LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(
              wRunMonth, wRunYear);
      BusinessCertificate bc = getBusinessCertificate(request);
      String pSDate = PayrollBeanUtils.getJavaDateAsString(sDate);
      List<LoanSchedule> jeds = garnishmentService.loanScheduleTSC(wRunMonth, wRunYear, bc);


    ReportGeneratorBean rt = new ReportGeneratorBean();

    Collections.sort(jeds,Comparator.comparing(LoanSchedule::getEmployeeName));

    int i = 1;
    List<Map<String, Object>> garnSummaryList = new ArrayList<>();
    HashMap<String, Integer> uniqueSet = new HashMap<>();
    HashMap<String, Integer> subGroupedUniqueSet = new HashMap<>();
    HashMap<String, Integer> doubleSubGroupedUniqueSet = new HashMap<>();
    for (LoanSchedule data : jeds) {
      Map<String, Object> newData = new HashMap<>();
      newData.put(bc.getStaffTypeName(), data.getEmployeeName());
      newData.put(bc.getStaffTitle(), data.getEmployeeNumber());
      newData.put("Amount", data.getLoanDeduction());
      newData.put("Balance", data.getLoanBalance());
      newData.put("Loan", data.getLoanName());
      newData.put("School", data.getSchoolName());
      newData.put("Agency", data.getAgencyName());
      uniqueSet.put(data.getLoanName(),  i++);
      subGroupedUniqueSet.put(data.getAgencyName(), i++);
      doubleSubGroupedUniqueSet.put(data.getSchoolName(), i++);
      garnSummaryList.add(newData);
    }

    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
    tableHeaders.add(new ReportGeneratorBean("Amount", 2));
    tableHeaders.add(new ReportGeneratorBean("Balance", 2));
    tableHeaders.add(new ReportGeneratorBean("Loan", 3));
    tableHeaders.add(new ReportGeneratorBean("School", 3));
    tableHeaders.add(new ReportGeneratorBean("Agency", 3));

    List<Map<String, Object>> GarnMapHeaders = new ArrayList<>();
    for(ReportGeneratorBean head : tableHeaders){
      Map<String, Object> mappedHeader = new HashMap<>();
      mappedHeader.put("headerName", head.getHeaderName());
      mappedHeader.put("totalInd", head.getTotalInd());
      GarnMapHeaders.add(mappedHeader);
    }

    List<String> mainHeaders = new ArrayList<>();
    mainHeaders.add("Global Loans For "+ sDate.getMonth()+", "+sDate.getYear());

    rt.setBusinessCertificate(bc);
    rt.setGroupBy("Loan");
    rt.setReportTitle("Global Loans");
    rt.setMainHeaders(mainHeaders);
    rt.setSubGroupBy("Agency");
    rt.setDoubleSubGroupBy("School");
    rt.setTableData(garnSummaryList);
    rt.setTableHeaders(GarnMapHeaders);
    rt.setWatermark(bc.getBusinessName()+" Global Loans");
    rt.setTableType(3);
    rt.setTotalInd(1);
    rt.setOutputInd(true);
    rt.setGroupedKeySet(uniqueSet.keySet());
    rt.setSubGroupedKeySet(subGroupedUniqueSet.keySet());
    rt.setDoubleSubGroupedKeySet(doubleSubGroupedUniqueSet.keySet());

      new DoubleSubGroupedPdf().getPdf(response, request, rt);

  }

  @RequestMapping({"/globalTSCLoan.do"})
  public void globalLoanTSC(@RequestParam("rm") int wRunMonth,
                         @RequestParam("ry") int wRunYear, HttpServletRequest request,
                         HttpServletResponse response, ModelAndView mv) throws Exception {
    LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(
            wRunMonth, wRunYear);
    BusinessCertificate bc = getBusinessCertificate(request);
    String pSDate = PayrollBeanUtils.getJavaDateAsString(sDate);
    List<LoanSchedule> jeds = garnishmentService.loanScheduleTSC(wRunMonth, wRunYear, bc);


    ReportGeneratorBean rt = new ReportGeneratorBean();

    Collections.sort(jeds,Comparator.comparing(LoanSchedule::getEmployeeName));

    List<Map<String, Object>> garnSummaryList = new ArrayList<>();
    int i = 0;
    HashMap<String, Integer> uniqueSet = new HashMap<>();
    HashMap<String, Integer> subGroupedUniqueSet = new HashMap<>();
    HashMap<String, Integer> doubleSubGroupedUniqueSet = new HashMap<>();
    for (LoanSchedule data : jeds) {
      Map<String, Object> newData = new HashMap<>();
      newData.put(bc.getStaffTypeName(), data.getEmployeeName());
      newData.put(bc.getStaffTitle(), data.getEmployeeNumber());
      newData.put("Amount", data.getLoanDeduction());
      newData.put("Balance", data.getLoanBalance());
      newData.put("Agency", data.getAgencyName());
      newData.put("School", data.getSchoolName());
      newData.put("Loan", data.getLoanName());
      uniqueSet.put(data.getAgencyName(),  i++);
      subGroupedUniqueSet.put(data.getSchoolName(), i++);
      doubleSubGroupedUniqueSet.put(data.getLoanName(), i++);
      garnSummaryList.add(newData);
    }

    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
    tableHeaders.add(new ReportGeneratorBean("Amount", 2));
    tableHeaders.add(new ReportGeneratorBean("Balance", 2));
    tableHeaders.add(new ReportGeneratorBean("Agency", 3));
    tableHeaders.add(new ReportGeneratorBean("School", 3));
    tableHeaders.add(new ReportGeneratorBean("Loan", 3));

    List<Map<String, Object>> garnMapHeaders = new ArrayList<>();
    for(ReportGeneratorBean head : tableHeaders){
      Map<String, Object> mappedHeader = new HashMap<>();
      mappedHeader.put("headerName", head.getHeaderName());
      mappedHeader.put("totalInd", head.getTotalInd());
      garnMapHeaders.add(mappedHeader);
    }
    String addendum = "TSC Loans";
    List<String> mainHeaders = new ArrayList<>();
    if(bc.isSubeb()) {
      mainHeaders.add("Global School Loans For " + sDate.getMonth()+", "+sDate.getYear());
      addendum = "School Loans";
    }else {
      mainHeaders.add("Global TSC Loans For " +sDate.getMonth()+", "+sDate.getYear());
    }
    rt.setBusinessCertificate(bc);
    rt.setGroupBy("Agency");
    rt.setReportTitle("Global "+addendum);
    rt.setMainHeaders(mainHeaders);
    rt.setSubGroupBy("School");
    rt.setDoubleSubGroupBy("Loan");
    rt.setTableData(garnSummaryList);
    rt.setWatermark(bc.getBusinessName()+addendum);
    rt.setTableHeaders(garnMapHeaders);
    rt.setTableType(3);
    rt.setTotalInd(1);
    rt.setOutputInd(true);
    rt.setGroupedKeySet(uniqueSet.keySet());
    rt.setSubGroupedKeySet(subGroupedUniqueSet.keySet());
    rt.setDoubleSubGroupedKeySet(doubleSubGroupedUniqueSet.keySet());

    new DoubleSubGroupedPdf().getPdf(response, request, rt);

  }


  @RequestMapping({"/empGarnDedExcel.do"})
  public void setupForm(@RequestParam("eid") Long pEmpId, @RequestParam("oid") Long pOid, @RequestParam("oind") int pOind, Model model,
                        HttpServletRequest request, HttpServletResponse response) throws Exception {
    SessionManagerService.manageSession(request, model);

    ReportGeneratorBean rt = new ReportGeneratorBean();

    SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

    double paidLoanAmount = 0.0D;

    BusinessCertificate bc = getBusinessCertificate(request);

    boolean wShowTotalLoanTypeRow = false;

    AbstractGarnishmentEntity wEGI = (AbstractGarnishmentEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getGarnishmentInfoClass(bc),
            Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("id", pOid)));

    double currentLoanAmount = wEGI.getOwedAmount();
    double originalLoanAmount = wEGI.getOriginalLoanAmount();
    double wTotalLoanPayments = this.loanService.getTotalGarnishments(bc, pOid, pEmpId);
    List<CustomPredicate> predicates = new ArrayList<>();
    predicates.add(CustomPredicate.procurePredicate("employee.id", pEmpId));
    predicates.add(CustomPredicate.procurePredicate("empGarnInfo.id", pOid));
    List<AbstractPaycheckGarnishmentEntity> paycheckGarnishments = (List<AbstractPaycheckGarnishmentEntity>) this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckGarnishmentClass(bc),
            predicates, null);

    LocalDate wCal;

    double wWorkingLoanAmount = wTotalLoanPayments + currentLoanAmount;
    double wTotalAmountLoaned = wWorkingLoanAmount;
    if (!paycheckGarnishments.isEmpty()) {
      for (AbstractPaycheckGarnishmentEntity p : paycheckGarnishments)
      {
        if(p.getStartingLoanBalance() <= 0.00D){
          p.setDeductionAmount(wWorkingLoanAmount);
          p.setLoanBalance(wWorkingLoanAmount - p.getAmount());
          wWorkingLoanAmount -= p.getAmount();
        }else{
          p.setDeductionAmount(p.getStartingLoanBalance());
          p.setLoanBalance(p.getAftGarnBal());
        }

        wWorkingLoanAmount -= p.getAmount();
        paidLoanAmount += p.getAmount();
      }

      Collections.sort(paycheckGarnishments);
    }

    AbstractEmployeeEntity wEmp = IppmsUtils.loadEmployee(genericService, pEmpId, bc);
    PaginatedPaycheckGarnDedBeanHolder wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(paycheckGarnishments);
    wPGBDH.setDisplayTitle(wEmp.getEmployeeId());
    wPGBDH.setId(wEmp.getId());

    wPGBDH.setName(wEmp.getDisplayNameWivTitlePrefixed());
    wPGBDH.setMode(wEmp.getParentObjectName());


    wPGBDH.setObjectId(wEGI.getId());
    wPGBDH.setObjectInd(1);
    wPGBDH.setGarnishmentName(wEGI.getDescription());
    wPGBDH.setOriginalLoanAmount(wTotalAmountLoaned);
    wPGBDH.setCurrentLoanAmount(currentLoanAmount);
    wPGBDH.setCurrentOriginalLoanAmount(originalLoanAmount);
    wPGBDH.setShowTotalLoanTypeRow(wShowTotalLoanTypeRow);
    wPGBDH.setPaidLoanAmount(paidLoanAmount);


    List<Map<String, Object>> garnSummaryList = new ArrayList<>();
    for (AbstractPaycheckGarnishmentEntity data : paycheckGarnishments) {
      Map<String, Object> newData = new HashMap<>();
      newData.put("Pay Date", data.getPayDate());
      newData.put("Pay Period", data.getPayPeriodStr());
      newData.put("Starting Balance", data.getStartingLoanBalance());
      newData.put("Withheld Amount", data.getAmount());
      newData.put("End Balance", data.getLoanBalance());
      garnSummaryList.add(newData);
    }

    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
    tableHeaders.add(new ReportGeneratorBean("Pay Date", 0));
    tableHeaders.add(new ReportGeneratorBean("Pay Period", 0));
    tableHeaders.add(new ReportGeneratorBean("Starting Balance", 2));
    tableHeaders.add(new ReportGeneratorBean("Withheld Amount", 2));
    tableHeaders.add(new ReportGeneratorBean("End Balance", 2));

    List<Map<String, Object>> GarnMapHeaders = new ArrayList<>();
    for(ReportGeneratorBean head : tableHeaders){
      Map<String, Object> mappedHeader = new HashMap<>();
      mappedHeader.put("headerName", head.getHeaderName());
      mappedHeader.put("totalInd", head.getTotalInd());
      GarnMapHeaders.add(mappedHeader);
    }

    List<String> mainHeaders = new ArrayList<>();
    mainHeaders.add(bc.getStaffTypeName()+" Name: "+ wPGBDH.getName());
    mainHeaders.add(bc.getStaffTitle()+": "+ wPGBDH.getDisplayTitle());
    mainHeaders.add("Loan Name: "+ wPGBDH.getGarnishmentName());
    mainHeaders.add("Current Loan Amount: "+ wPGBDH.getCurrentOriginalLoanAmountStr());
    mainHeaders.add("Total Payments Made: "+ wPGBDH.getPaidLoanAmountStr());
    mainHeaders.add("Outstanding Balance: "+ wPGBDH.getLoanDifferenceStr());

    rt.setBusinessCertificate(bc);
    rt.setGroupBy(null);
    rt.setReportTitle("single_garnishment_history");
    rt.setMainHeaders(mainHeaders);
    rt.setSubGroupBy(null);
    rt.setTableData(garnSummaryList);
    rt.setTableHeaders(GarnMapHeaders);
    rt.setTableType(0);
    rt.setTotalInd(0);

    simpleExcelReportGenerator.getExcel(response, request, rt);
  }

  @RequestMapping({ "/allLoanByBankPdf.do" })
  public void LoanSummaryByBankBranch(
          @RequestParam("rm") int pRunMonth,
          @RequestParam("ry") int pRunYear, @RequestParam("rt") int reportType, HttpServletRequest request,
          HttpServletResponse response, ModelAndView mv) throws Exception
  {
    BusinessCertificate bc = getBusinessCertificate(request);

    ReportGeneratorBean rt = new ReportGeneratorBean();

    List<EmpDeductMiniBean> wRetList = this.garnishmentService
            .loadEmpDeductMiniBeanForLoanByPayPeriodList(pRunMonth,
                    pRunYear, bc);

    Collections.sort(wRetList,Comparator.comparing(EmpDeductMiniBean::getDeductionName));

    List<Map<String, Object>> garnSummaryList = new ArrayList<>();
    HashMap<String, Integer> uniqueSet = new HashMap<>();
    int i = 1;
    EmpDeductMiniBean temp = new EmpDeductMiniBean();
    for (EmpDeductMiniBean data : wRetList) {
      Map<String, Object> newData = new HashMap<>();
      newData.put("Loan Name", data.getDeductionName());
      newData.put("Deducted Amount", data.getDeductionAmount());
      newData.put("Bank Name",data.getBankName());
      newData.put("Account Number", data.getAccountNumber());
      newData.put("Bank Branch", data.getBankBranchName());
      temp.setPeriod(data.getPeriod());
      garnSummaryList.add(newData);
      uniqueSet.put(data.getBankBranchName(), i++);
    }

    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
    tableHeaders.add(new ReportGeneratorBean("Loan Name", 0));
    tableHeaders.add(new ReportGeneratorBean("Deducted Amount", 2));
    tableHeaders.add(new ReportGeneratorBean("Bank Name", 0));
    tableHeaders.add(new ReportGeneratorBean("Account Number", 0));
    tableHeaders.add(new ReportGeneratorBean("Bank Branch", 3));

    List<Map<String, Object>> garnMapHeaders = new ArrayList<>();
    for(ReportGeneratorBean head : tableHeaders){
      Map<String, Object> mappedHeader = new HashMap<>();
      mappedHeader.put("headerName", head.getHeaderName());
      mappedHeader.put("totalInd", head.getTotalInd());
      garnMapHeaders.add(mappedHeader);
    }

    List<String> mainHeaders = new ArrayList<>();
    mainHeaders.add("Loan Report By Bank");

    rt.setBusinessCertificate(bc);
    rt.setGroupBy("Bank Branch");
    rt.setReportTitle("loan_report_by_bank");
    rt.setMainHeaders(mainHeaders);
    rt.setSubGroupBy(null);
    rt.setTableData(garnSummaryList);
    rt.setTableHeaders(garnMapHeaders);
    rt.setWatermark(bc.getBusinessName()+" Loans By Bank");
    rt.setTableType(1);
    rt.setTotalInd(1);

    if(reportType == 1) {
      rt.setGroupedKeySet(uniqueSet.keySet());
      new GroupedDataExcelReportGenerator().getExcel(response, request, rt);
    }
    else{
      rt.setOutputInd(true);
      new GroupedPdf().getPdf(response, request, rt);
    }


  }





}