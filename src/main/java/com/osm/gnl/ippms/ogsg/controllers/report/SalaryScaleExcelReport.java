package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaymentService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SimpleExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBeanInactive;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
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
public class SalaryScaleExcelReport extends BaseController {

    @Autowired
    PaymentService paymentService;

    @RequestMapping({"/salaryScaleByLevelExcel.do"})
    public void generateEmployeesOnSalaryScaleByLevelForm(@RequestParam("ssid") Long pSsc, @RequestParam("fl") int pFromLevel, @RequestParam("tl") int pToLevel, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = getBusinessCertificate(request);

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        List<HiringInfo> empList = this.paymentService.getActiveEmployeesOnPayScale(pSsc, pFromLevel, pToLevel, bc);

        ReportGeneratorBean rt = new ReportGeneratorBean();


        BusinessEmpOVBeanInactive pList = new BusinessEmpOVBeanInactive(empList);

        pList.setSalaryFromLevel(String.valueOf(pFromLevel));
        pList.setSalaryToLevel(String.valueOf(pToLevel));


        List<Map<String, Object>> contPenMapped = new ArrayList<>();
        for (HiringInfo data : empList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTitle(), data.getEmployee().getEmployeeId());
            newData.put(bc.getStaffTypeName(), data.getEmployee().getDisplayName());
            newData.put("Gender", data.getGenderStr());
            newData.put("Date Of Birth", data.getBirthDateStr());
            newData.put("Date Of Hire", data.getHireDateStr());
           if(bc.isPensioner()){
                newData.put("Years In Service", data.getNoOfYearsAtRetirement());
                newData.put("Age At Retirement", data.getAgeAtRetirement());
                newData.put("Retirement Date", data.getExpDateOfRetireStr());
            }
            else{
                newData.put("Years In Service (Expected)", data.getNoOfYearsAtRetirement());
                newData.put("Age At Retirement (Expected)", data.getAgeAtRetirement());
                newData.put("Retirement Due Date(Expected)", data.getExpDateOfRetireStr());
            }
            newData.put("Level/Step", data.getEmployee().getSalaryInfo().getLevelAndStepAsStr());
            newData.put(bc.getMdaTitle(), data.getEmployee().getMdaDeptMap().getMdaInfo().getName());
            contPenMapped.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean("Gender", 0));
        tableHeaders.add(new ReportGeneratorBean("Date Of Birth", 0));
        tableHeaders.add(new ReportGeneratorBean("Date Of Hire", 0));
       if(bc.isPensioner()){
            tableHeaders.add(new ReportGeneratorBean("Years In Service", 0));
            tableHeaders.add(new ReportGeneratorBean("Age At Retirement", 0));
            tableHeaders.add(new ReportGeneratorBean("Retirement Date", 0));
        }
        else {
            tableHeaders.add(new ReportGeneratorBean("Years In Service (Expected)", 0));
            tableHeaders.add(new ReportGeneratorBean("Age At Retirement (Expected)", 0));
            tableHeaders.add(new ReportGeneratorBean("Retirement Due Date(Expected)", 0));
        }
        tableHeaders.add(new ReportGeneratorBean("Level/Step", 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherSpecHeaders.add(mappedHeader);
        }



        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getStaffTypeName()+" Pay Code : Level & Step ");
        mainHeaders.add("From Level "+pList.getSalaryFromLevel()+ " - "+pList.getSalaryToLevel());

        rt.setBusinessCertificate(bc);
        rt.setGroupBy(bc.getMdaTitle());
       if(bc.isPensioner()){
            rt.setReportTitle("PensionersPayCodeLevelAndStep");
        }
        else{
            rt.setReportTitle("EmployeesPayCodeLevelAndStep");
        }
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(OtherSpecHeaders);
        rt.setTableType(1);
        rt.setTotalInd(0);


        simpleExcelReportGenerator.getExcel(response, request, rt);
    }
}
