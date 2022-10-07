package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.StatisticsDetailsService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SimpleExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.controllers.statistics.PayrollStatisticsDataGen;
import com.osm.gnl.ippms.ogsg.domain.beans.MaterialityDisplayBean;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
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
public class PayrollStatisticsReportController extends BaseController {

    @Autowired
    private StatisticsDetailsService statisticsDetailsService;

    @RequestMapping({"/payrollStatExcelReport.do"})
    public void generateEmployeesOnPayGroupByLevelForm(@RequestParam("ind") int pStatCode, @RequestParam("rm") int rMonth, @RequestParam("ry") int rYear, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = getBusinessCertificate(request);

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        ReportGeneratorBean rt = new ReportGeneratorBean();

        MaterialityDisplayBean wMDB = PayrollStatisticsDataGen.generateModel(getBusinessCertificate(request),statisticsDetailsService, pStatCode, rMonth, rYear);


        List<NamedEntityBean> dataList = (List<NamedEntityBean>) wMDB.getObjectList();
        List<Map<String, Object>> hiringList = new ArrayList<>();
        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();

        switch(pStatCode){

            case 1 :
            case 5 ://No of Employees Paid.
            case 7 :
            case 8 :
            case 10 :
                for (NamedEntityBean data : dataList) {
                    Map<String, Object> newData = new HashMap<>();
                    newData.put(bc.getStaffTitle(), data.getMode());
                    newData.put(bc.getStaffTypeName(),data.getName());
                    newData.put("Level/Step",data.getSalaryInfo().getLevelStepStr());
                    newData.put("Total Pay", data.getTotalPay());
                    newData.put("Total Deductions", data.getTotalDeductions());
                    newData.put("Net Pay", data.getNetPay());
                    hiringList.add(newData);
                }


                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                tableHeaders.add(new ReportGeneratorBean("Level/Step", 0));
                tableHeaders.add(new ReportGeneratorBean("Total Pay", 2));
                tableHeaders.add(new ReportGeneratorBean("Total Deductions", 2));
                tableHeaders.add(new ReportGeneratorBean("Net Pay", 2));

                 break;
            case 2:
                //No Of Employees not paid by Birth Date....
                for (NamedEntityBean data : dataList) {
                    Map<String, Object> newData = new HashMap<>();
                    newData.put(bc.getStaffTitle(), data.getMode());
                    newData.put(bc.getStaffTypeName(),data.getName());
                    newData.put("Level/Step",data.getSalaryInfo().getLevelStepStr());
                    newData.put("Birth Date", data.getBirthDateStr());
                    newData.put("Exp. Date Of Retirement", data.getExpectedDateOfRetirementStr());
                    hiringList.add(newData);

                }
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                tableHeaders.add(new ReportGeneratorBean("Level/Step", 0));
                tableHeaders.add(new ReportGeneratorBean("Birth Date", 0));
                tableHeaders.add(new ReportGeneratorBean("Exp. Date Of Retirement", 0));

                break;
            case 3:
                //No Of Employees not paid by Hire Date.....
                for (NamedEntityBean data : dataList) {
                    Map<String, Object> newData = new HashMap<>();
                    newData.put(bc.getStaffTitle(), data.getMode());
                    newData.put(bc.getStaffTypeName(),data.getName());
                    newData.put("Level/Step",data.getSalaryInfo().getLevelStepStr());
                    newData.put("Hire Date", data.getHireDateStr());
                    newData.put("Exp. Date Of Retirement", data.getExpectedDateOfRetirementStr());
                    hiringList.add(newData);

                }
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                tableHeaders.add(new ReportGeneratorBean("Level/Step", 0));
                tableHeaders.add(new ReportGeneratorBean("Hire Date", 0));
                tableHeaders.add(new ReportGeneratorBean("Exp. Date Of Retirement", 0));

                break;
            case 4:
                //No of Employees not paid by Contract Date...
            case 6:
                //No of Employees not paid by Suspension
                for (NamedEntityBean data : dataList) {
                    Map<String, Object> newData = new HashMap<>();
                    newData.put(bc.getStaffTitle(), data.getMode());
                    newData.put(bc.getStaffTypeName(),data.getName());
                    newData.put("Level/Step",data.getSalaryInfo().getLevelStepStr());
                    hiringList.add(newData);

                }
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                tableHeaders.add(new ReportGeneratorBean("Level/Step", 0));

                break;
            case 9:

                for (NamedEntityBean data : dataList) {
                    Map<String, Object> newData = new HashMap<>();
                    newData.put(bc.getStaffTitle(), data.getMode());
                    newData.put(bc.getStaffTypeName(),data.getName());
                    newData.put("Level/Step", data.getSalaryInfo().getLevelStepStr());
                    newData.put("Spec Allowance",data.getSpecAllow());
                    newData.put("Total Pay", data.getTotalPay());
                    newData.put("Total Deductions", data.getTotalDeductions());
                    newData.put("Net Pay", data.getNetPay());
                    hiringList.add(newData);

                }
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                tableHeaders.add(new ReportGeneratorBean("Level/Step", 0));
                tableHeaders.add(new ReportGeneratorBean("Spec Allowance", 2));
                tableHeaders.add(new ReportGeneratorBean("Total Pay", 2));
                tableHeaders.add(new ReportGeneratorBean("Total Deductions", 2));
                tableHeaders.add(new ReportGeneratorBean("Net Pay", 2));

                break;
            case 11:

                for (NamedEntityBean data : dataList) {
                    Map<String, Object> newData = new HashMap<>();
                    newData.put(bc.getStaffTitle(), data.getMode());
                    newData.put(bc.getStaffTypeName(),data.getName());
                    newData.put("Level/Step", data.getSalaryInfo().getLevelStepStr());
                    newData.put("Exp. Date Of Retirement",data.getExpectedDateOfRetirementStr());
                    newData.put("Termination Date", data.getTerminationDateStr());
                    hiringList.add(newData);

                }
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                tableHeaders.add(new ReportGeneratorBean("Level/Step", 0));
                tableHeaders.add(new ReportGeneratorBean("Exp. Date Of Retirement", 0));
                tableHeaders.add(new ReportGeneratorBean("Termination Date", 0));

                break;
            case 12:

                for (NamedEntityBean data : dataList) {
                    Map<String, Object> newData = new HashMap<>();
                    newData.put(bc.getStaffTitle(), data.getMode());
                    newData.put(bc.getStaffTypeName(),data.getName());
                    newData.put("Level/Step", data.getSalaryInfo().getLevelStepStr());
                    newData.put("Hire Date",data.getHireDateStr());
                    newData.put("Birth Date", data.getBirthDateStr());
                    hiringList.add(newData);

                }
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                tableHeaders.add(new ReportGeneratorBean("Level/Step", 0));
                tableHeaders.add(new ReportGeneratorBean("Hire Date", 0));
                tableHeaders.add(new ReportGeneratorBean("Birth Date", 0));
                break;
            case 13:
                for (NamedEntityBean data : dataList) {
                    Map<String, Object> newData = new HashMap<>();
                    newData.put(bc.getStaffTitle(), data.getMode());
                    newData.put(bc.getStaffTypeName(),data.getName());
                    newData.put("Level/Step", data.getSalaryInfo().getLevelStepStr());
                    newData.put("Contract Start Date",data.getAllowanceStartDateStr());
                    newData.put("Contract End Date", data.getAllowanceEndDateStr());
                    hiringList.add(newData);

                }
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                tableHeaders.add(new ReportGeneratorBean("Level/Step", 0));
                tableHeaders.add(new ReportGeneratorBean("Contract Start Date", 0));
                tableHeaders.add(new ReportGeneratorBean("Contract End Date", 0));

                break;
            case 14:
                for (NamedEntityBean data : dataList) {
                    Map<String, Object> newData = new HashMap<>();
                    newData.put(bc.getStaffTitle(), data.getMode());
                    newData.put(bc.getStaffTypeName(),data.getName());
                    newData.put("Level/Step", data.getSalaryInfo().getLevelStepStr());
                    newData.put("Pension Start Date",data.getHireDateStr());
                    newData.put("I Am Alive Date", data.getExpectedDateOfRetirementStr());
                    newData.put("Annual Pension Amount",data.getAnnualPension());
                    newData.put("Monthly Pension Amount",data.getMonthlyPension());
                    hiringList.add(newData);

                }
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                tableHeaders.add(new ReportGeneratorBean("Level/Step", 0));
                tableHeaders.add(new ReportGeneratorBean("Pension Start Date", 0));
                tableHeaders.add(new ReportGeneratorBean("I Am Alive Date", 0));
                tableHeaders.add(new ReportGeneratorBean("Annual Pension Amount", 0));
                tableHeaders.add(new ReportGeneratorBean("Monthly Pension Amount", 0));

                break;
            default:

                break;

        }

        List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            hirMappedHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(wMDB.getName());

        rt.setBusinessCertificate(bc);
        rt.setGroupBy(null);
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(hiringList);
        rt.setTableHeaders(hirMappedHeaders);
        rt.setTableType(0);
        rt.setTotalInd(1);
        rt.setReportTitle(wMDB.getFileName());

        simpleExcelReportGenerator.getExcel(response, request, rt);

    }
}
