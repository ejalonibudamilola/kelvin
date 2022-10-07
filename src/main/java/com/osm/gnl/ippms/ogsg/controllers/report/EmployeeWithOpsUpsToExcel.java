package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.SalaryService;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.GroupedDataExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SimpleExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.domain.report.SalaryDifferenceBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
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
public class EmployeeWithOpsUpsToExcel extends BaseController {

    @Autowired
    SalaryService salaryService;

    @RequestMapping({"/exportEmpWithOpsUpsToExcel.do"})
    public void setupForm(@RequestParam("cpp") String pCurrentPayPeriod, @RequestParam("ppp") String pPrevPayPeriod, @RequestParam("ph") String pPlaceHolder, Model pModel, HttpServletRequest pRequest,
                          HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException {
        SessionManagerService.manageSession(pRequest, pModel);

        BusinessCertificate bc = getBusinessCertificate(pRequest);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();

        LocalDate fDate = PayrollBeanUtils.setDateFromString(pCurrentPayPeriod);
        LocalDate tDate = PayrollBeanUtils.setDateFromString(pPrevPayPeriod);

        LocalDate wPrevMonthStart = PayrollBeanUtils.getPreviousMonthDate(fDate, false);
        LocalDate wPrevMonthEnd = PayrollBeanUtils.getPreviousMonthDate(tDate, true);

        List<SalaryDifferenceBean> empList = this.salaryService.getEmployeesWithSalaryDiffByDates(fDate, tDate, wPrevMonthStart, wPrevMonthEnd, bc);

        String currentDate = PayrollHRUtils.getMonthYearDateFormat().format(fDate);
        String PrevDate = PayrollHRUtils.getMonthYearDateFormat().format(wPrevMonthStart);
        List<Map<String, Object>> EmpOpsUpsList = new ArrayList<>();
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i = 1;
        for (SalaryDifferenceBean data : empList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTitle(), data.getEmployeeId());
            newData.put(bc.getStaffTypeName(), data.getEmployeeName());
            newData.put("Salary Scale", data.getSalaryScale());
            newData.put("Level & Step", data.getLevelAndStep());
            newData.put(currentDate + " Net Salary", data.getCurrentSalary());
            newData.put(PrevDate +" Net Salary", data.getLastSalary());
            newData.put("Salary Difference", data.getSalaryDiff());
            newData.put("Type", data.getFreshOpsUpsStr());
            newData.put("Mda", data.getMdaInfo().getName());
            EmpOpsUpsList.add(newData);
            uniqueSet.put(data.getMdaInfo().getName(), i++);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean("Salary Scale", 0));
        tableHeaders.add(new ReportGeneratorBean("Level & Step", 0));
        tableHeaders.add(new ReportGeneratorBean(currentDate + " Net Salary", 2));
        tableHeaders.add(new ReportGeneratorBean(PrevDate + " Net Salary", 2));
        tableHeaders.add(new ReportGeneratorBean("Salary Difference", 2));
        tableHeaders.add(new ReportGeneratorBean("Type", 0));
        tableHeaders.add(new ReportGeneratorBean("Mda", 3));

        List<Map<String, Object>> EmpOpsUpsMappedHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            EmpOpsUpsMappedHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add("Oyo State Government - Over-Payment And Under-Payment List for :  " + currentDate+ " and " + PrevDate);

        rt.setBusinessCertificate(bc);
        rt.setGroupBy("Mda");
        rt.setReportTitle("Over-Payment And Under-Payment List By MDA");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(EmpOpsUpsList);
        rt.setTableHeaders(EmpOpsUpsMappedHeaders);
        rt.setTableType(1);
        rt.setTotalInd(1);
        rt.setGroupedKeySet(uniqueSet.keySet());


        groupedDataExcelReportGenerator.getExcel(response, pRequest, rt);
    }
}
