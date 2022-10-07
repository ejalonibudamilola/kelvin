package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SimpleExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class suspendedEmployeesExcelReport extends BaseController {

    @Autowired
    EmployeeService employeeService;


    @RequestMapping({"/suspendedEmployeesReportExcel.do"})
    public void setupForm(@RequestParam("sd") String sDate, @RequestParam("ed") String eDate, Model model, HttpServletRequest request,
                          HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = getBusinessCertificate(request);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        LocalDate sd = PayrollBeanUtils.setDateFromString(sDate);

        LocalDate ed = PayrollBeanUtils.setDateFromString(eDate);

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        List<SuspensionLog> empList = this.employeeService.getAllSuspendedEmployeesForReport( sd, ed, 0L, false, bc);

        List<Map<String, Object>> OtherSpecList = new ArrayList<>();
        for (SuspensionLog data : empList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTypeName(), data.getEmployee().getDisplayName());
            newData.put(bc.getStaffTitle(), data.getEmployee().getEmployeeId());
            newData.put("Pay Group", data.getSalaryScale());
            newData.put("Level And Step", data.getLevelAndStep());
            newData.put("Suspension Date", data.getSuspensionDate());
            newData.put("Pay Percentage", data.getPayPercentageStr());
            newData.put("Reference Number", data.getReferenceNumber());
            newData.put("Reason", data.getSuspensionType().getName());
            OtherSpecList.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean("Pay Group", 0));
        tableHeaders.add(new ReportGeneratorBean("Level And Step", 0));
        tableHeaders.add(new ReportGeneratorBean("Suspension Date", 0));
        tableHeaders.add(new ReportGeneratorBean("Pay Percentage", 0));
        tableHeaders.add(new ReportGeneratorBean("Reference Number", 0));
        tableHeaders.add(new ReportGeneratorBean("Reason", 0));

        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherSpecHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add("Suspended Employees");


        rt.setBusinessCertificate(bc);
        rt.setGroupBy(null);
        rt.setReportTitle("Suspended_Employees_Report");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(OtherSpecList);
        rt.setTableHeaders(OtherSpecHeaders);
        rt.setTableType(0);
        rt.setTotalInd(0);


        simpleExcelReportGenerator.getExcel(response, request, rt);
    }
}
