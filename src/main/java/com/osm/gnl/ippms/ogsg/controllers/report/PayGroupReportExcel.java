package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaymentService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SimpleExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBeanInactive;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.utils.annotation.AnnotationProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class PayGroupReportExcel extends BaseController {


    private final PaymentService paymentService;
    @Autowired
    public PayGroupReportExcel(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @RequestMapping({"/payGroupByLevelExcel.do"})
    public void generateEmployeesOnPayGroupByLevelForm(@RequestParam("stid") Long pTypeId, @RequestParam("fl") int pFromLevel, @RequestParam("tl") int pToLevel, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = getBusinessCertificate(request);

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        ReportGeneratorBean rt = new ReportGeneratorBean();

        List<HiringInfo> empList = this.paymentService.getActiveEmployeesOnPayGroup(pTypeId, pFromLevel, pToLevel, bc);


        BusinessEmpOVBeanInactive pList = new BusinessEmpOVBeanInactive(empList);
        SalaryType wSS =  this.genericService.loadObjectUsingRestriction(SalaryType.class, Arrays.asList(
                CustomPredicate.procurePredicate("id",pTypeId), CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));


        pList.setDisplayErrors(wSS.getName());
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
            if(bc.getStaffTypeName().equals("Pensioner")){
                newData.put("Years In Service", data.getNoOfYearsAtRetirement());
                newData.put("Age At Retirement", data.getAgeAtRetirement());
                newData.put("Retirement Date", data.getExpDateOfRetireStr());
            }
            else{
                newData.put("Years In Service (Expected)", data.getNoOfYearsAtRetirement());
                newData.put("Age At Retirement (Expected)", data.getAgeAtRetirement());
                newData.put("Retirement Due Date (Expected)", data.getExpDateOfRetireStr());
            }
            newData.put("Level & Step", data.getEmployee().getSalaryInfo().getLevelAndStepAsStr());
            newData.put(bc.getMdaTitle(), data.getEmployee().getMdaDeptMap().getMdaInfo().getName());
            contPenMapped.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean("Gender", 0));
        tableHeaders.add(new ReportGeneratorBean("Date Of Birth", 0));
        tableHeaders.add(new ReportGeneratorBean("Date Of Hire", 0));
        if(bc.getStaffTypeName().equals("Pensioner")){
            tableHeaders.add(new ReportGeneratorBean("Years In Service", 0));
            tableHeaders.add(new ReportGeneratorBean("Age At Retirement", 0));
            tableHeaders.add(new ReportGeneratorBean("Retirement Date", 0));
        }
        else{
            tableHeaders.add(new ReportGeneratorBean("Years In Service (Expected)", 0));
            tableHeaders.add(new ReportGeneratorBean("Age At Retirement (Expected)", 0));
            tableHeaders.add(new ReportGeneratorBean("Retirement Due Date (Expected)", 0));
        }
        tableHeaders.add(new ReportGeneratorBean("Level & Step", 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : tableHeaders) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherSpecHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
            mainHeaders.add(bc.getStaffTypeName()+" Pay By Group Report");

        rt.setBusinessCertificate(bc);
        rt.setGroupBy(bc.getMdaTitle());
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(OtherSpecHeaders);
        if(bc.getStaffTypeName().equals("Pensioner")) {
            rt.setReportTitle("PensionerPayGroupReport");
        }
        else{
            rt.setReportTitle("EmployeePayGroupReport");
        }
        rt.setTableType(1);
        rt.setTotalInd(0);


        simpleExcelReportGenerator.getExcel(response, request, rt);
//        return new ModelAndView("employeeBySalaryScaleExcelView", "empByPayCodeBean", pList);
    }
    @RequestMapping({"/salaryStructureExcel.do"})
    public void generateSalaryStructureList(@RequestParam("sid") Long pTypeId, @RequestParam("rvalue") int val1, @RequestParam("dvalue") int val2, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = getBusinessCertificate(request);

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        ReportGeneratorBean rt = new ReportGeneratorBean();

        List<CustomPredicate> predicates = new ArrayList<>();
        predicates.add(CustomPredicate.procurePredicate("salaryType.id", pTypeId));
        predicates.add(getBusinessClientIdPredicate(request));
        List<SalaryInfo> empList = this.genericService.loadAllObjectsUsingRestrictions(SalaryInfo.class,predicates,null );


        List<Map<String, Object>> contPenMapped = new ArrayList<>();


         Object value;
         List<String> keySet;
         String salaryTypeName = null;
         List<String> _keySet = null;
         Collections.sort(empList,Comparator.comparing(SalaryInfo::getLevel).thenComparing(SalaryInfo::getStep));
         int i = 0;
        for (SalaryInfo data : empList) {

            LinkedHashMap<String, Object> newData = new LinkedHashMap<>();


            newData.put("Level & Step", data.getLevelAndStepAsStr());
            value = data.getAnnualSalary();

            newData.put("Annual Gross",treatAsTextOrNumber(value,val2));
            value = data.getMonthlySalary();
            newData.put("Monthly Gross",treatAsTextOrNumber(value,val2));

            newData.put("Basic Salary", setReportValueType(data.getMonthlyBasicSalary(), val1,val2));
            Map<String, Object> _newData = AnnotationProcessor.getSalaryAllowanceAnnotations(data, Double.class);
            keySet = new ArrayList<>(_newData.keySet());
            Collections.sort(keySet);
            if(i == 0){
                salaryTypeName = data.getSalaryType().getName();
                _keySet = keySet;
                i += 1;
            }
            for(String string : keySet){
                value = _newData.get(string);
                newData.put(string, setReportValueType(value, val1,val2));
            }
            contPenMapped.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean("Level & Step", 0));
        tableHeaders.add(new ReportGeneratorBean("Annual Gross", 0));
        tableHeaders.add(new ReportGeneratorBean("Monthly Gross", 0));
        tableHeaders.add(new ReportGeneratorBean("Basic Salary", 0));
        for(String string : _keySet){
            tableHeaders.add(new ReportGeneratorBean(string, 0));
        }

        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : tableHeaders) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherSpecHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getOrgName(bc.getBusinessClientInstId()));
        mainHeaders.add("Salary Structure - "+" "+salaryTypeName);
        if(val1 == 1)
            mainHeaders.add("Monthly Values");
        else
            mainHeaders.add("Yearly Values");


        rt.setBusinessCertificate(bc);
        rt.setGroupBy(null);
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(OtherSpecHeaders);

            rt.setReportTitle(salaryTypeName);

        rt.setTableType(0);
        rt.setTotalInd(0);


        simpleExcelReportGenerator.getExcel(response, request, rt);
    }
}
