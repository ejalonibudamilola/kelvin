package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckSpecAllowEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.SpecAllowService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.GroupedDataExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SimpleExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductGarnMiniBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductionDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.pagination.beans.PayrollSummaryBean;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckSpecialAllowance;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpDeductMiniBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Controller
public class AllOtherSpecialAllowanceDetailsExcelForm extends BaseController
{


    private final SpecAllowService specAllowService;
    @Autowired
    public AllOtherSpecialAllowanceDetailsExcelForm(SpecAllowService specAllowService) {
        this.specAllowService = specAllowService;
    }


    @RequestMapping({"/allOtherSpecialAllowanceDetailsExcel.do"})
  public void setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear,
		  @RequestParam("pid") Long pTypeId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception
  {
	  SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

		SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

		ReportGeneratorBean rt = new ReportGeneratorBean();

	  LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
      LocalDate eDate = PayrollBeanUtils.getEndOfMonth(sDate);
		String pSDate = PayrollBeanUtils.getJavaDateAsString(sDate);
		String pEDate = PayrollBeanUtils.getJavaDateAsString(eDate);

      DeductionDetailsBean deductDetails = new DeductionDetailsBean();
      HashMap <Long,DeductGarnMiniBean>deductionBean = new HashMap<>();
      List <AbstractPaycheckSpecAllowEntity> paycheckSpecAllow = this.specAllowService.loadPaycheckSpecialAllowanceByParentIdAndPayPeriod(pTypeId, pRunMonth, pRunYear, bc);

      DeductGarnMiniBean d;
      for (AbstractPaycheckSpecAllowEntity p : paycheckSpecAllow)
      {
          if (deductionBean.containsKey(p.getSpecialAllowanceInfo().getSpecialAllowanceType().getId())) {
              d = deductionBean.get(p.getSpecialAllowanceInfo().getSpecialAllowanceType().getId());


          } else {
              d = new DeductGarnMiniBean();
              d.setDeductionId(p.getSpecialAllowanceInfo().getSpecialAllowanceType().getId());

          }
          d.setAmount(d.getAmount() + p.getAmount());
          d.setName(p.getSpecialAllowanceInfo().getSpecialAllowanceType().getName());
          d.setDescription(p.getSpecialAllowanceInfo().getDescription());

          deductionBean.put(p.getSpecialAllowanceInfo().getSpecialAllowanceType().getId(), d);
          deductDetails.setTotal(deductDetails.getTotal() + p.getAmount());
      }

      deductDetails.setDeductionMiniBean(EntityUtils.getDeductionList(deductionBean));





      List<Map<String, Object>> OtherSpecList = new ArrayList<>();
      for (DeductGarnMiniBean data : deductDetails.getDeductionMiniBean()) {
          Map<String, Object> newData = new HashMap<>();
          newData.put("Type", data.getDescription());
          newData.put("Amount Paid", data.getAmount());
          OtherSpecList.add(newData);
      }

      List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
      tableHeaders.add(new ReportGeneratorBean("Type", 0));
      tableHeaders.add(new ReportGeneratorBean("Amount Paid", 2));

      List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
      for(ReportGeneratorBean head : tableHeaders){
          Map<String, Object> mappedHeader = new HashMap<>();
          mappedHeader.put("headerName", head.getHeaderName());
          mappedHeader.put("totalInd", head.getTotalInd());
          OtherSpecHeaders.add(mappedHeader);
      }

      List<String> mainHeaders = new ArrayList<>();
      mainHeaders.add(bc.getBusinessName()+" Special Allowance Report");
      mainHeaders.add("Pay Period : "+pSDate +" - "+eDate);


      rt.setBusinessCertificate(bc);
      rt.setGroupBy(null);
      rt.setReportTitle("Special_Allowance_Report");
      rt.setMainHeaders(mainHeaders);
      rt.setSubGroupBy(null);
      rt.setTableData(OtherSpecList);
      rt.setTableHeaders(OtherSpecHeaders);
      rt.setTableType(0);
      rt.setTotalInd(1);


      simpleExcelReportGenerator.getExcel(response, request, rt);
  }

  @RequestMapping({"/specialAllowanceWithDetailsExcel.do"})
  public void setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear,
                                @RequestParam("pid") Long pTypeId, @RequestParam("exp") String pFactor, Model model,
                                HttpServletRequest request, HttpServletResponse response) throws Exception {
   
	  
	  LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
	    LocalDate eDate = PayrollBeanUtils.getEndOfMonth(sDate);

      BusinessCertificate bc = getBusinessCertificate(request);

		ReportGeneratorBean rt = new ReportGeneratorBean();

       GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();


    List <AbstractPaycheckSpecAllowEntity>paycheckDeductions;

    paycheckDeductions = this.specAllowService.loadPaycheckSpecialAllowanceByParentIdAndPayPeriod(pTypeId, pRunMonth, pRunYear, bc);

      List<Map<String, Object>> OtherSpecList = new ArrayList<>();
      HashMap<String, Integer> uniqueSet = new HashMap<>();
      HashMap<String, Integer> subUniqueSet = new HashMap<>();
      int i =1;
      for (AbstractPaycheckSpecAllowEntity data : paycheckDeductions) {
          Map<String, Object> newData = new HashMap<>();
          newData.put(bc.getStaffTitle(), data.getEmployeeId());
          newData.put(bc.getStaffTypeName(), data.getEmployeeName());
          newData.put("Amount Paid", data.getAmount());
          newData.put("Special Allowance Type", data.getSpecialAllowanceInfo().getDescription());
          OtherSpecList.add(newData);
          uniqueSet.put(data.getSpecialAllowanceInfo().getDescription(), i++);
      }

      List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
      tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
      tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
      tableHeaders.add(new ReportGeneratorBean("Amount Paid", 2));
      tableHeaders.add(new ReportGeneratorBean("Special Allowance Type", 3));

      List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
      for(ReportGeneratorBean head : tableHeaders){
          Map<String, Object> mappedHeader = new HashMap<>();
          mappedHeader.put("headerName", head.getHeaderName());
          mappedHeader.put("totalInd", head.getTotalInd());
          OtherSpecHeaders.add(mappedHeader);
      }

      List<String> mainHeaders = new ArrayList<>();
      mainHeaders.add("Special Allowance By Type");


      rt.setBusinessCertificate(bc);
      rt.setGroupBy("Special Allowance Type");
      rt.setReportTitle("Special_Allowances_By_Type");
      rt.setMainHeaders(mainHeaders);
      rt.setSubGroupBy(null);
      rt.setTableData(OtherSpecList);
      rt.setTableHeaders(OtherSpecHeaders);
      rt.setTableType(1);
      rt.setTotalInd(1);
      rt.setGroupedKeySet(uniqueSet.keySet());



      groupedDataExcelReportGenerator.getExcel(response, request, rt);
  }


  @RequestMapping({"/specialAllowanceByMDAExcel.do"})
  public void groupSpecialAllowanceByMDA(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear,
                                         Model model, HttpServletRequest request, HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = getBusinessCertificate(request);

	  ReportGeneratorBean rt = new ReportGeneratorBean();


      GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();
 


	    List<EmpDeductMiniBean> specAllowList = this.specAllowService.loadSpecAllowanceAllowanceMiniBeanByFromDateToDate(pRunMonth,pRunYear, bc);

      List<Map<String, Object>> OtherSpecList = new ArrayList<>();
      HashMap<String, Integer> uniqueSet = new HashMap<>();
      int i =1;
      for (EmpDeductMiniBean data : specAllowList) {
          Map<String, Object> newData = new HashMap<>();
          newData.put(bc.getStaffTypeName(), data.getName());
          newData.put(bc.getStaffTitle(), data.getEmployeeId());
          newData.put("Amount", data.getCurrentDeduction());
          newData.put(bc.getMdaTitle(), data.getMdaDeptMap().getMdaInfo().getName());
          OtherSpecList.add(newData);
          uniqueSet.put(data.getMdaDeptMap().getMdaInfo().getName(), i++);
      }

      List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
      tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
      tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
      tableHeaders.add(new ReportGeneratorBean("Amount", 2));
      tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

      List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
      for(ReportGeneratorBean head : tableHeaders){
          Map<String, Object> mappedHeader = new HashMap<>();
          mappedHeader.put("headerName", head.getHeaderName());
          mappedHeader.put("totalInd", head.getTotalInd());
          OtherSpecHeaders.add(mappedHeader);
      }

      List<String> mainHeaders = new ArrayList<>();
      mainHeaders.add("Special Allowance By "+bc.getMdaTitle());


      rt.setBusinessCertificate(bc);
      rt.setGroupBy(bc.getMdaTitle());
      rt.setReportTitle("Special Allowances By MDA");
      rt.setMainHeaders(mainHeaders);
      rt.setSubGroupBy(null);
      rt.setTableData(OtherSpecList);
      rt.setTableHeaders(OtherSpecHeaders);
      rt.setTableType(1);
      rt.setTotalInd(1);
      rt.setGroupedKeySet(uniqueSet.keySet());


      groupedDataExcelReportGenerator.getExcel(response, request, rt);

	  }

  @RequestMapping({"/singleSpecialAllowanceByMDAExcel.do"})
  public void singleSpecialAllowanceByMDA(@RequestParam("satid") Long pSatid, @RequestParam("rm") int pRunMonth,
		  @RequestParam("ry") int pRunYear,
		 Model model, HttpServletRequest request) throws Exception 
  {
	  SessionManagerService.manageSession(request, model);

	    BusinessCertificate bc = getBusinessCertificate(request);

	    PayrollSummaryBean p = new PayrollSummaryBean();
	    LocalDate fromDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);

	    List<EmpDeductMiniBean> empBeanList = this.specAllowService.loadSingleSpecAllowanceAllowanceByFromDateToDate(pSatid, fromDate, bc);


	  }

  @RequestMapping({"/specialAllowanceDetailsExcel.do"})
  public void setupForm(@RequestParam("did") Long dedId, @RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear,
		  @RequestParam("pid") Long pBusClientId, HttpServletResponse response, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

		BusinessCertificate bCert = this.getBusinessCertificate(request);

      ReportGeneratorBean rt = new ReportGeneratorBean();

	  LocalDate fromDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
	    LocalDate toDate = PayrollBeanUtils.getEndOfMonth(fromDate);



    List <AbstractPaycheckSpecAllowEntity>paycheckSpecialAllowance  = this.specAllowService.loadPaycheckSpecialAllowanceByParentIdAndPayPeriod(dedId, pRunMonth, pRunYear, bCert);
      Collections.sort(paycheckSpecialAllowance,Comparator.comparing(AbstractPaycheckSpecAllowEntity::getMdaName).thenComparing(AbstractPaycheckSpecAllowEntity::getEmployeeName));
      List<Map<String, Object>> OtherSpecList = new ArrayList<>();
      HashMap<String, Integer> uniqueSet = new HashMap<>();
      HashMap<String, Integer> subUniqueSet = new HashMap<>();
      int i =1;
      Map<String, Object> newData;
      for (AbstractPaycheckSpecAllowEntity data : paycheckSpecialAllowance) {
          newData = new HashMap<>();
          newData.put(bCert.getStaffTypeName(), data.getEmployeeName());
          newData.put(bCert.getStaffTitle(), data.getEmployeeId());
          newData.put("Amount", data.getAmount());
          newData.put(bCert.getMdaTitle(),data.getMdaName());
          newData.put("Special Allowance",data.getSpecialAllowanceInfo().getName());
          OtherSpecList.add(newData);
           uniqueSet.put(data.getMdaName(),i++);
      }

      List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
      tableHeaders.add(new ReportGeneratorBean(bCert.getStaffTypeName(), 0));
      tableHeaders.add(new ReportGeneratorBean(bCert.getStaffTitle(), 0));
      tableHeaders.add(new ReportGeneratorBean(bCert.getMdaTitle(), 0));
      tableHeaders.add(new ReportGeneratorBean("Amount", 2));
      tableHeaders.add(new ReportGeneratorBean("Special Allowance", 3));

      List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
      for(ReportGeneratorBean head : tableHeaders){
          Map<String, Object> mappedHeader = new HashMap<>();
          mappedHeader.put("headerName", head.getHeaderName());
          mappedHeader.put("totalInd", head.getTotalInd());
          OtherSpecHeaders.add(mappedHeader);
      }
      tableHeaders = new ArrayList<>();
      tableHeaders.add(new ReportGeneratorBean(bCert.getStaffTypeName(), 0));
      tableHeaders.add(new ReportGeneratorBean(bCert.getStaffTitle(), 0));
      tableHeaders.add(new ReportGeneratorBean("Special Allowance", 0));
      tableHeaders.add(new ReportGeneratorBean("Amount", 2));
      tableHeaders.add(new ReportGeneratorBean(bCert.getMdaTitle(), 3));

      List<Map<String, Object>> subDedHeaders = new ArrayList<>();
      for (ReportGeneratorBean head : tableHeaders) {
          Map<String, Object> mappedHeader = new HashMap<>();
          mappedHeader.put("headerName", head.getHeaderName());
          mappedHeader.put("totalInd", head.getTotalInd());
          subDedHeaders.add(mappedHeader);
      }
      List<String> mainHeaders = new ArrayList<>();
      mainHeaders.add("Single Special Allowance Report");


      rt.setBusinessCertificate(bCert);
      rt.setGroupBy("Special Allowance");
      rt.setReportTitle("Single_Special_Allowance_Report");
      rt.setMainHeaders(mainHeaders);
      rt.setSubGroupBy(bCert.getMdaTitle());
      rt.setUseTableSubData(true);
      rt.setTableData(OtherSpecList);
      rt.setTableHeaders(OtherSpecHeaders);
      rt.setTableSubData(OtherSpecList);
      rt.setTableSubHeaders(subDedHeaders);
      rt.setTableType(1);
      rt.setTotalInd(1);
      rt.setGroupedKeySet(uniqueSet.keySet());

      new GroupedDataExcelReportGenerator().getExcel(response, request, rt);
  }

   
}