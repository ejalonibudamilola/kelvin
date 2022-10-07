package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.NegPayService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.SpecAllowService;
import com.osm.gnl.ippms.ogsg.base.services.StatisticsService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SimpleExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.domain.beans.MaterialityDisplayBean;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PayrollManagerReportSummaryForm extends BaseController {

    /**
     * Damilola Ejalonibu
     * 02-2021
     */

    @Autowired
    PaycheckService paycheckService;

    @Autowired
    StatisticsService statisticsService;

    @Autowired
    NegPayService negPayService;

    @Autowired
    SpecAllowService specAllowService;

    @RequestMapping({"/payrollStatSummary.do"})
    public void generatePayrollStatisticsSummary(@RequestParam("rm") int  pRunMonth, @RequestParam("ry") int pRunYear,
                                                 Model model, HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        MaterialityDisplayBean wMDB = new MaterialityDisplayBean();

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        ReportGeneratorBean rt = new ReportGeneratorBean();

        BusinessCertificate bc = super.getBusinessCertificate(request);

        int wTotalNoProcessed = IppmsUtilsExt.countNoOfPayChecks(genericService,bc,pRunMonth,pRunYear,false);
        wMDB.setNoOfEmployeesProcessed(wTotalNoProcessed);

        //Net Pay for Active Employees...
        NamedEntityBean wNamedEntity = this.statisticsService.loadPaycheckSummaryInfoByMonthAndYear(bc, pRunMonth,pRunYear);


        wMDB.setNetPaySum(wNamedEntity.getNetPay());
        wMDB.setTotalPaySum(wNamedEntity.getTotalPay());
        wMDB.setTotalDeductionSum(wNamedEntity.getTotalDeductions());
        wMDB.setNoOfEmployeesPaid(wNamedEntity.getNoOfActiveEmployees());

        Map<String, Object> newData;
        List<Map<String, Object>> listNewData = new ArrayList<>();

        //Employee Paid

        newData = new HashMap<>();
        newData.put("Statistic", ""+bc.getStaffTypeName()+"'s Paid");
        newData.put("No Of "+bc.getStaffTypeName()+"s", wMDB.getNoOfEmployeesPaid());
        newData.put("Percentage of Total "+bc.getStaffTypeName()+"s", wMDB.getPercentageOfEmployees());
        newData.put("Amount", wMDB.getNetPaySum());
        newData.put("Percentage Of Gross", wMDB.getTotalPayPercentageOfGross());
        listNewData.add(newData);



        //--- Employees NOT Paid.
        int wSerialNum = 0;
        wMDB = this.statisticsService.loadEmployeesNotPaidByMonthAndYear(bc, pRunMonth,pRunYear, wMDB);

       // wMDB.setEmployeesNotPaidByBirthDate(wNamedEntity.getRetiredByBirthDate());
        if(bc.isPensioner()){
            if(wMDB.getPensionersNotPaidByPensionCalc() > 0) {
                wMDB.setNoOfPenForRecalc(++wSerialNum);

                newData = new HashMap<>();
                newData.put("Statistic", ""+bc.getStaffTypeName()+"'s Not Paid (Awaiting Calculation)");
                newData.put("No Of "+bc.getStaffTypeName()+"s", wMDB.getPensionersNotPaidByPensionCalc());
                newData.put("Percentage of Total "+bc.getStaffTypeName()+"s", wMDB.getPercentageOfPenAwaitingCalculation());
                newData.put("Amount", wMDB.getZero());
                newData.put("Percentage Of Gross", wMDB.getZeroPercent());
                listNewData.add(newData);
            }
        }else {
            if (wMDB.getEmployeesNotPaidByBirthDate() > 0) {
                wMDB.setNoOfEmpRetiredByBirthDate(++wSerialNum);

                newData = new HashMap<>();
                newData.put("Statistic", "" + bc.getStaffTypeName() + "'s Not Paid (Birth Date)");
                newData.put("No Of " + bc.getStaffTypeName() + "s", wMDB.getEmployeesNotPaidByBirthDate());
                newData.put("Percentage of Total " + bc.getStaffTypeName() + "s", wMDB.getPercentageOfEmpForBirthDate());
                newData.put("Amount", wMDB.getZero());
                newData.put("Percentage Of Gross", wMDB.getZeroPercent());
                listNewData.add(newData);
            }
            //    wMDB.setEmployeesNotPaidByHireDate(wNamedEntity.getRetiredByHireDate());


            if (wMDB.getEmployeesNotPaidByHireDate() > 0) {
                wMDB.setNoOfEmpRetiredByHireDate(++wSerialNum);

                newData = new HashMap<>();
                newData.put("Statistic", "" + bc.getStaffTypeName() + "'s Not Paid (Hire Date)");
                newData.put("No Of " + bc.getStaffTypeName() + "s", wMDB.getEmployeesNotPaidByHireDate());
                newData.put("Percentage of Total " + bc.getStaffTypeName() + "s", wMDB.getPercentageOfEmpForHireDate());
                newData.put("Amount", wMDB.getZero());
                newData.put("Percentage Of Gross", wMDB.getZeroPercent());
                listNewData.add(newData);
            }

            wNamedEntity = this.statisticsService.loadEmployeesPaidByContract(bc, pRunMonth, pRunYear, false);
            wMDB.setEmployeesPaidByContract(wNamedEntity.getNoOfActiveEmployees());
            wMDB.setNetPayContract(wNamedEntity.getNetPay());
            wMDB.setTotalPayContract(wNamedEntity.getTotalPay());


            if (wMDB.getEmployeesPaidByContract() > 0) {
                wMDB.setNoOfEmpRetiredByContract(++wSerialNum);
                //System.out.println("wSerialNum in paid contract in report is " +wSerialNum);
                newData = new HashMap<>();
                newData.put("Statistic", "" + bc.getStaffTypeName() + "'s Paid (Contract Date)");
                newData.put("No Of " + bc.getStaffTypeName() + "s", wMDB.getEmployeesPaidByContract());
                newData.put("Percentage of Total " + bc.getStaffTypeName() + "s", wMDB.getPercentageOfEmpForContract());
                newData.put("Amount", wMDB.getNetPayContract());
                newData.put("Percentage Of Gross", wMDB.getContractEmpPercentageOfGross());
                listNewData.add(newData);
            }
        }
        wMDB.setEmployeesNotPaidBySuspension(this.statisticsService.loadEmpNotPaidDueToReason(bc, pRunMonth, pRunYear, true , false, false));

        //System.out.println("Employee not paid by suspension is "+wMDB.getEmployeesNotPaidBySuspension());

        if(wMDB.getEmployeesNotPaidBySuspension() > 0) {

            wMDB.setNoOfEmpRetiredBySuspension(++wSerialNum);

            newData = new HashMap<>();
            newData.put("Statistic", ""+bc.getStaffTypeName()+"'s Not Paid (Suspension)");
            newData.put("No Of "+bc.getStaffTypeName()+"s", wMDB.getEmployeesNotPaidBySuspension());
            newData.put("Percentage of Total "+bc.getStaffTypeName()+"s", wMDB.getPercentageOfEmpForSuspension());
            newData.put("Amount", wMDB.getZero());
            newData.put("Percentage Of Gross", wMDB.getZeroPercent());
            listNewData.add(newData);
        }

        wMDB.setEmployeesNotPaidByTermination(this.statisticsService.loadEmpNotPaidDueToReason(bc, pRunMonth, pRunYear, false, true, false ));
        //System.out.println("Employee not paid by termination is "+wMDB.getEmployeesNotPaidByTermination());

        if(wMDB.getEmployeesNotPaidByTermination() > 0) {
            wMDB.setNoOfEmpRetiredByTermination(++wSerialNum);

            newData = new HashMap<>();
            newData.put("Statistic", ""+bc.getStaffTypeName()+"'s Not Paid (Terminated)");
            newData.put("No Of "+bc.getStaffTypeName()+"s", wMDB.getEmployeesNotPaidByTermination());
            newData.put("Percentage of Total "+bc.getStaffTypeName()+"s", wMDB.getPercentageOfEmpRetiredByTermination());
            newData.put("Amount", wMDB.getZero());
            newData.put("Percentage Of Gross", wMDB.getZeroPercent());
            listNewData.add(newData);
        }

        wMDB.setEmployeesNotPaidByApproval(this.statisticsService.loadEmpNotPaidDueToReason(bc, pRunMonth, pRunYear, false, false, true));

        //System.out.println("Employee not paid by approval is "+wMDB.getEmployeesNotPaidByApproval());


        if(wMDB.getEmployeesNotPaidByApproval() > 0) {
            wMDB.setNoOfEmpNotPaidByApproval(++wSerialNum);

           wMDB.setEmployeesOnInterdiction(wNamedEntity.getNoOfActiveEmployees());
            newData = new HashMap<>();
            newData.put("Statistic", ""+bc.getStaffTypeName()+"'s Not Approved For Payroll");
            newData.put("No Of "+bc.getStaffTypeName()+"s", wMDB.getEmployeesNotPaidByApproval());
            newData.put("Percentage of Total "+bc.getStaffTypeName()+"s", wMDB.getPercentageOfEmpNotApproved());
            newData.put("Amount", wMDB.getZero());
            newData.put("Percentage Of Gross", wMDB.getZeroPercent());
            listNewData.add(newData);
        }

        wNamedEntity = this.statisticsService.loadNoOfEmployeesPaidByInterdiction(bc, pRunMonth, pRunYear);


        if(wMDB.getEmployeesOnInterdiction() > 0){
            wMDB.setNoOfEmpPaidByInterdiction(++wSerialNum);
            wMDB.setNetPayInterdiction(wNamedEntity.getNetPay());
            wMDB.setTotalPayInterdiction(wNamedEntity.getTotalPay());

            newData = new HashMap<>();
            newData.put("Statistic", ""+bc.getStaffTypeName()+"'s On Interdiction");
            newData.put("No Of "+bc.getStaffTypeName()+"s", wMDB.getEmployeesOnInterdiction());
            newData.put("Percentage of Total "+bc.getStaffTypeName()+"s", wMDB.getPercentageOfEmpPaidByInterdiction());
            newData.put("Amount", wMDB.getZero());
            newData.put("Percentage Of Gross", wMDB.getZeroPercent());
            listNewData.add(newData);
        }


        wNamedEntity = this.statisticsService.loadEmployeesWithNegativeNetPay(bc, pRunMonth,pRunYear);

        wMDB.setNoOfEmployeesWithNegativePay(wNamedEntity.getNoOfEmployeesWithNegPay());
        wMDB.setNegativePaySum(wNamedEntity.getNegativePay());


       // System.out.println("Employee not paid by suspension 2 is "+wMDB.getEmployeesNotPaidBySuspension());
        if(wMDB.getEmployeesNotPaidBySuspension() > 0) {
            wMDB.setNoOfEmpWithNegPay(++wSerialNum);

            newData = new HashMap<>();
            newData.put("Statistic", ""+bc.getStaffTypeName()+"'s With Negative Pay");
            newData.put("No Of "+bc.getStaffTypeName()+"s", wMDB.getNoOfEmployeesWithNegativePay());
            newData.put("Percentage of Total "+bc.getStaffTypeName()+"s", wMDB.getPercentageOfEmpWithNegPay());
            newData.put("Amount", wMDB.getNegativePaySum());
            newData.put("Percentage Of Gross", wMDB.getZeroPercent());
            listNewData.add(newData);
        }

        //Now Get the Number of Employees Paid By Days...

        wNamedEntity = this.statisticsService.loadEmployeesPaidByDays(bc, pRunMonth, pRunYear);

        wMDB.setNetPayByDays(wNamedEntity.getNetPayByDays());
        wMDB.setNoOfEmployeesPaidByDays(wNamedEntity.getNoOfEmployeesPaidByDays());

        //System.out.println("Employee paid by days "+wMDB.getNoOfEmployeesPaidByDays());

        if(wMDB.getNoOfEmployeesPaidByDays() > 0) {
            wMDB.setNoOfEmpPaidByDays(++wSerialNum);

            newData = new HashMap<>();
            newData.put("Statistic", ""+bc.getStaffTypeName()+"'s Paid By Days");
            newData.put("No Of "+bc.getStaffTypeName()+"s", wMDB.getNoOfEmployeesPaidByDays());
            newData.put("Percentage of Total "+bc.getStaffTypeName()+"s", wMDB.getPercentageOfEmpPaidByDays());
            newData.put("Amount", wMDB.getZero());
            newData.put("Percentage Of Gross", wMDB.getZeroPercent());
            listNewData.add(newData);
        }

        //Now Get the Number of Employees Paid Special Allowances...

        wNamedEntity = this.statisticsService.loadEmployeesPaidSpecialAllowancesByMonthAndYear(bc, pRunMonth, pRunYear);

        wMDB.setSpecialAllowance(wNamedEntity.getNetPayByDays());
        wMDB.setNoOfEmpPaidSpecAllow(wNamedEntity.getNoOfEmployeesPaidByDays());

        //System.out.println("Employee paid special allowance "+wMDB.getNoOfEmpPaidSpecAllow());

        if(wMDB.getNoOfEmpPaidSpecAllow() > 0) {
            wMDB.setNoOfEmpPaidSpecialAllowance(++wSerialNum);

            newData = new HashMap<>();
            newData.put("Statistic", ""+bc.getStaffTypeName()+"'s Paid Special Allowance");
            newData.put("No Of "+bc.getStaffTypeName()+"s", wMDB.getNoOfEmpPaidSpecAllow());
            newData.put("Percentage of Total "+bc.getStaffTypeName()+"s", wMDB.getPercentageOfEmpPaidSpecAllow());
            newData.put("Amount", wMDB.getSpecialAllowance());
            newData.put("Percentage Of Gross", wMDB.getSpecAllowPercentageOfGross());
            listNewData.add(newData);
        }

        wMDB.setRunMonth(pRunMonth);
        wMDB.setRunYear(pRunYear);
        


        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean("Statistic", 0));
        tableHeaders.add(new ReportGeneratorBean("No Of "+bc.getStaffTypeName()+"s", 0));
        tableHeaders.add(new ReportGeneratorBean("Percentage of Total "+bc.getStaffTypeName()+"s", 0));
        tableHeaders.add(new ReportGeneratorBean("Amount", 2));
        tableHeaders.add(new ReportGeneratorBean("Percentage Of Gross", 0));

        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherSpecHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add("Payroll Run Statistics Report");


        rt.setBusinessCertificate(bc);
        rt.setGroupBy(null);
        rt.setReportTitle("Payroll Run Statistics Report");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(listNewData);
        rt.setTableHeaders(OtherSpecHeaders);
        rt.setTableType(0);
        rt.setTotalInd(1);


        simpleExcelReportGenerator.getExcel(response, request, rt);

//        return new ModelAndView("payrollStatReportExcelSummary", (String) super.getSessionId(request), wMDB);
    }
}
