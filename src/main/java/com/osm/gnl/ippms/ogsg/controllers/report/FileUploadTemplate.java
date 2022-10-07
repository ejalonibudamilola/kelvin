package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.BankService;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.FileUploadTemplateExcelGenerator;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.GroupedDataExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.domain.beans.BankPVSummaryBean;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBeanInactive;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
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
public class FileUploadTemplate extends BaseController {

    @Autowired
    BankService bankService;

    @RequestMapping({"/downloadObjectTemplate.do"})
    public void generateDeductionTemplate(@RequestParam("ot") int pObjectType, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        BusinessCertificate bc = getBusinessCertificate(request);

        FileUploadTemplateExcelGenerator fileUploadTemplateExcelGenerator = new FileUploadTemplateExcelGenerator();

        //BusinessEmpOVBeanInactive pList = new BusinessEmpOVBeanInactive();


        List<Map<String, Object>> hiringList = new ArrayList<>();
        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        List<String> mainHeaders = new ArrayList<>();
        Map<String, Object> newData = new HashMap<>();


        switch(pObjectType) {
            case 3:
                newData.put(bc.getStaffTitle(), "OG123456");
                newData.put("Deduction Code", "TestDed");
                newData.put("Amount", "99999.99");
                newData.put("Start Date", "01-Mar-2021");
                newData.put("End Date", "01-Mar-2021");
                hiringList.add(newData);

                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                tableHeaders.add(new ReportGeneratorBean("Deduction Code", 0));
                tableHeaders.add(new ReportGeneratorBean("Amount", 0));
                tableHeaders.add(new ReportGeneratorBean("Start Date", 0));
                tableHeaders.add(new ReportGeneratorBean("End Date", 0));

                mainHeaders.add("Deduction Template");

                rt.setReportTitle("Deduction Template");
                break;

            case 0:
                newData.put("LEVEL", "");
                newData.put("STEP", "");
                newData.put("BASIC_SALARY", "");
                newData.put("RENT", "");
                newData.put("TRANSPORT", "");
                newData.put("MEAL", "");
                newData.put("UTILITY", "");
                newData.put("INDUCEMENT", "");
                newData.put("FURNITURE", "");
                newData.put("RURAL_POSTING", "");
                newData.put("HAZARD", "");
                newData.put("CALL_DUTY", "");
                newData.put("JOURNAL", "");
                newData.put("DOMESTIC_SERVANT", "");
                newData.put("ADMIN_ALLOWANCE", "");
                newData.put("ENTERTAINMENT", "");
                newData.put("PERSONAL_ASSISTANT", "");
                newData.put("MOTOR_VEHICLE", "");
                newData.put("EXAM_ALLOWANCE", "");
                newData.put("CONS_ALLOWANCE", "");
                newData.put("OUTFIT_ALLOWANCE", "");
                newData.put("QUARTERS_ALLOWANCE", "");
                newData.put("SPA_ALLOWANCE", "");
                newData.put("RESPONSIBILITY_ALLOWANCE", "");
                newData.put("RESEARCH_ALLOWANCE", "");
                newData.put("TOTO_ALLOWANCE", "");
                newData.put("MEDICAL_ALLOWANCE", "");
                newData.put("SITTING_ALLOWANCE", "");
                newData.put("SECURITY_ALLOWANCE", "");
                newData.put("UNIFORM_ALLOWANCE", "");
                newData.put("TEACHING_ALLOWANCE", "");
                newData.put("ENHANCED_ALLOWANCE", "");
                newData.put("SPECIAL_HEALTH_ALLOWANCE", "");
                newData.put("SHIFT_DUTY", "");
                newData.put("SPECIALIST_ALLOWANCE", "");
                newData.put("SECURITY_ALLOWANCE", "");
                hiringList.add(newData);

                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                tableHeaders.add(new ReportGeneratorBean("LEVEL", 0));
                tableHeaders.add(new ReportGeneratorBean("STEP", 0));
                tableHeaders.add(new ReportGeneratorBean("BASIC_SALARY", 0));
                tableHeaders.add(new ReportGeneratorBean("RENT", 0));
                tableHeaders.add(new ReportGeneratorBean("TRANSPORT", 0));
                tableHeaders.add(new ReportGeneratorBean("MEAL", 0));
                tableHeaders.add(new ReportGeneratorBean("UTILITY", 0));
                tableHeaders.add(new ReportGeneratorBean("INDUCEMENT", 0));
                tableHeaders.add(new ReportGeneratorBean("FURNITURE", 0));
                tableHeaders.add(new ReportGeneratorBean("RURAL_POSTING", 0));
                tableHeaders.add(new ReportGeneratorBean("HAZARD", 0));
                tableHeaders.add(new ReportGeneratorBean("CALL_DUTY", 0));
                tableHeaders.add(new ReportGeneratorBean("JOURNAL", 0));
                tableHeaders.add(new ReportGeneratorBean("DOMESTIC_SERVANT", 0));
                tableHeaders.add(new ReportGeneratorBean("ADMIN_ALLOWANCE", 0));
                tableHeaders.add(new ReportGeneratorBean("ENTERTAINMENT", 0));
                tableHeaders.add(new ReportGeneratorBean("PERSONAL_ASSISTANT", 0));
                tableHeaders.add(new ReportGeneratorBean("MOTOR_VEHICLE", 0));
                tableHeaders.add(new ReportGeneratorBean("EXAM_ALLOWANCE", 0));
                tableHeaders.add(new ReportGeneratorBean("CONS_ALLOWANCE", 0));
                tableHeaders.add(new ReportGeneratorBean("OUTFIT_ALLOWANCE", 0));
                tableHeaders.add(new ReportGeneratorBean("QUARTERS_ALLOWANCE", 0));
                tableHeaders.add(new ReportGeneratorBean("SPA_ALLOWANCE", 0));
                tableHeaders.add(new ReportGeneratorBean("RESPONSIBILITY_ALLOWANCE", 0));
                tableHeaders.add(new ReportGeneratorBean("RESEARCH_ALLOWANCE", 0));
                tableHeaders.add(new ReportGeneratorBean("TOTO_ALLOWANCE", 0));
                tableHeaders.add(new ReportGeneratorBean("MEDICAL_ALLOWANCE", 0));
                tableHeaders.add(new ReportGeneratorBean("SITTING_ALLOWANCE", 0));
                tableHeaders.add(new ReportGeneratorBean("SECURITY_ALLOWANCE", 0));
                tableHeaders.add(new ReportGeneratorBean("UNIFORM_ALLOWANCE", 0));
                tableHeaders.add(new ReportGeneratorBean("TEACHING_ALLOWANCE", 0));
                tableHeaders.add(new ReportGeneratorBean("ENHANCED_ALLOWANCE", 0));
                tableHeaders.add(new ReportGeneratorBean("SPECIAL_HEALTH_ALLOWANCE", 0));
                tableHeaders.add(new ReportGeneratorBean("SHIFT_DUTY", 0));
                tableHeaders.add(new ReportGeneratorBean("SPECIALIST_ALLOWANCE", 0));
                tableHeaders.add(new ReportGeneratorBean("SECURITY_ALLOWANCE", 0));

                mainHeaders.add("Pay Group Template");

                rt.setReportTitle("Pay Group Template");

                break;

            case 1:
                newData.put(bc.getStaffTitle(), "OG12345");
                newData.put("Bank Sort Code", "076");
                newData.put("Bank Branch Sort Code", "07608678");
                newData.put("Account No.", "1234567890");
                hiringList.add(newData);

                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                tableHeaders.add(new ReportGeneratorBean("Bank Sort Code", 0));
                tableHeaders.add(new ReportGeneratorBean("Bank Branch Sort Code", 0));
                tableHeaders.add(new ReportGeneratorBean("Account No.", 0));

                mainHeaders.add("Bank Information Template");

                rt.setReportTitle("Bank Information Template");

                break;

            case 2:
                newData.put(bc.getStaffTitle(), "OG12345");
                newData.put("Loan Code", "LOAN CODE");
                newData.put("Balance", "99999.99");
                newData.put("Tenor", "24");
                hiringList.add(newData);

                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                tableHeaders.add(new ReportGeneratorBean("Loan Code", 0));
                tableHeaders.add(new ReportGeneratorBean("Balance", 0));
                tableHeaders.add(new ReportGeneratorBean("Tenor", 0));

                mainHeaders.add("Loan Template");

                rt.setReportTitle("Loan Template");

                break;


            case 4:
                newData.put(bc.getStaffTitle(), "OG12345");
                newData.put(bc.getStaffTypeName(), "John Doe");
                newData.put("Amount", "99999.99");
                newData.put("Year", "2021");
                hiringList.add(newData);

                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                tableHeaders.add(new ReportGeneratorBean("Amount", 0));
                tableHeaders.add(new ReportGeneratorBean("Year", 0));

                mainHeaders.add("Leave Bonus Template");

                rt.setReportTitle("Leave Bonus Template");

                break;

            case 5:
                newData.put(bc.getStaffTitle(), "OG12345");
                newData.put("Allowance Code", "SPEC ALLOW CODE");
                newData.put("Amount", "99999.99");
                newData.put("Start Date", "01-Mar-2021");
                newData.put("End Date", "01-Mar-2021");
                hiringList.add(newData);

                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                tableHeaders.add(new ReportGeneratorBean("Allowance Code", 0));
                tableHeaders.add(new ReportGeneratorBean("Amount", 0));
                tableHeaders.add(new ReportGeneratorBean("Start Date", 0));
                tableHeaders.add(new ReportGeneratorBean("End Date", 0));

                mainHeaders.add("Spec Allow. Template");

                rt.setReportTitle("Spec Allow. Template");

                break;

            case 6:
                newData.put("Grade Level", "");
                newData.put("Step", "");
                newData.put("Basic Salary", "");
                List<String> fields = AnnotationProcessor.getFieldsForTemplate(new SalaryInfo(), null);
                Collections.sort(fields);
                for(String s : fields)
                    newData.put(s,"");

                hiringList.add(newData);

                tableHeaders.add(new ReportGeneratorBean("Grade Level", 0));
                tableHeaders.add(new ReportGeneratorBean("Step", 0));
                tableHeaders.add(new ReportGeneratorBean("Basic Salary", 0));
                for(String s : fields)
                    tableHeaders.add(new ReportGeneratorBean(s, 0));

                mainHeaders.add("Salary Structure Template");

                rt.setReportTitle("Salary Structure Template");

                break;
            case 7:
                newData.put(bc.getStaffTitle(), "");


                hiringList.add(newData);

                tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));

                mainHeaders.add("Step Increment Template");

                rt.setReportTitle("Step Increment Template");

                break;
        }

            List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
            for(ReportGeneratorBean head : tableHeaders){
                Map<String, Object> mappedHeader = new HashMap<>();
                mappedHeader.put("headerName", head.getHeaderName());
                mappedHeader.put("totalInd", head.getTotalInd());
                hirMappedHeaders.add(mappedHeader);
            }


        rt.setBusinessCertificate(bc);
        rt.setGroupBy(null);
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(hiringList);
        rt.setTableHeaders(hirMappedHeaders);
        rt.setTableType(0);
        rt.setTotalInd(0);

        fileUploadTemplateExcelGenerator.getExcel(response, request, rt);

    }


    @RequestMapping({"/downloadTypeTemplate.do"})
    public void generateTypeTemplate(@RequestParam("ot") int pObjectType,Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();

        ReportGeneratorBean rt = new ReportGeneratorBean();


        //Build the Model Data Here...
        List<?> wModelData = null;
        switch(pObjectType){
            case 1://Bank Details
                wModelData = this.bankService.loadBankBranchesAsModelData();
                break;
            case 2://Loan
                //wModelData = this.payrollService.loadControlEntity("EmpGarnishmentType t",false);
                wModelData =  this.genericService.loadAllObjectsWithSingleCondition(EmpGarnishmentType.class, CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), "name");
                break;
            case 3://Deduction
                //wModelData = this.payrollService.loadControlEntity("EmpDeductionType t",false);
                wModelData = this.genericService.loadAllObjectsUsingRestrictions(EmpDeductionType.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), CustomPredicate.procurePredicate("displayableInd",0)),"name");
                break;
            case 4://Leave Bonus
                wModelData = this.genericService.loadAllObjectsWithSingleCondition(MdaInfo.class,CustomPredicate.procurePredicate("businessClientId", getBusinessCertificate(request).getBusinessClientInstId()), "name");

                break;
            case 5://Spec Allow
                //wModelData = this.payrollService.loadControlEntity("SpecialAllowanceType t",false);
                wModelData = this.genericService.loadControlEntity(SpecialAllowanceType.class);
                break;
        }
        BusinessEmpOVBeanInactive pList = new BusinessEmpOVBeanInactive(wModelData);
        pList.setId(Long.valueOf(String.valueOf(pObjectType)));


        List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
        List<Map<String, Object>> hiringList = new ArrayList<>();
        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        List<String> mainHeaders = new ArrayList<>();
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i = 1;

        switch(pObjectType){
            case 1://Bank Details
                for (BankPVSummaryBean data : (List<BankPVSummaryBean>)pList.getList()) {
                    Map<String, Object> newData = new HashMap<>();
                    newData.put("Bank Sort Code", data.getBankSortCode());
                    newData.put("Bank Branch Name", data.getBankBranchName());
                    newData.put("Branch Sort Code", data.getBankBranchSortCode());
                    newData.put("Bank Name", data.getBankName());
                    hiringList.add(newData);
                    uniqueSet.put(data.getBankName(), i++);
                }

                tableHeaders.add(new ReportGeneratorBean("Bank Sort Code", 0));
                tableHeaders.add(new ReportGeneratorBean("Bank Branch Name", 0));
                tableHeaders.add(new ReportGeneratorBean("Bank Name", 3));


                mainHeaders.add("Bank And Branches Model Data");

                rt.setReportTitle("Bank And Branches Model Data");
                break;
            case 2://Loan
                for (EmpGarnishmentType data : (List<EmpGarnishmentType>)pList.getList()) {
                    Map<String, Object> newData = new HashMap<>();
                    newData.put("Name", data.getName());
                    newData.put("Description", data.getDescription());
                    newData.put("Type Code", data.getName());
                    hiringList.add(newData);
                }

                tableHeaders.add(new ReportGeneratorBean("Name", 0));
                tableHeaders.add(new ReportGeneratorBean("Description", 0));
                tableHeaders.add(new ReportGeneratorBean("Type Code", 0));


                mainHeaders.add("Loan Model Data");

                rt.setReportTitle("Loan Model Data");
                break;
            case 3://Deduction
                for (EmpDeductionType data : (List<EmpDeductionType>)pList.getList()) {
                    Map<String, Object> newData = new HashMap<>();
                    newData.put("Name", data.getName());
                    newData.put("Description", data.getDescription());
                    newData.put("Type Code", data.getName());
                    hiringList.add(newData);
                }

                tableHeaders.add(new ReportGeneratorBean("Name", 0));
                tableHeaders.add(new ReportGeneratorBean("Description", 0));
                tableHeaders.add(new ReportGeneratorBean("Type Code", 0));


                mainHeaders.add("Deduction Model Data");

                rt.setReportTitle("Deduction Model Data");
                break;
            case 4://Leave Bonus
                for (MdaInfo data : (List<MdaInfo>)pList.getList()) {
                    Map<String, Object> newData = new HashMap<>();
                    newData.put("Name", data.getName());
                    newData.put("Description", data.getDescription());
                    hiringList.add(newData);
                }

                tableHeaders.add(new ReportGeneratorBean("Name", 0));
                tableHeaders.add(new ReportGeneratorBean("Description", 0));


                mainHeaders.add("M.D.A List (Leave Bonus)");

                rt.setReportTitle("M.D.A List (Leave Bonus)");
                break;
            case 5://Spec Allow
                for (SpecialAllowanceType data : (List<SpecialAllowanceType>)pList.getList()) {
                    Map<String, Object> newData = new HashMap<>();
                    newData.put("Name", data.getName());
                    newData.put("Description", data.getDescription());
                    newData.put("Type Code", data.getName());
                    hiringList.add(newData);
                }

                tableHeaders.add(new ReportGeneratorBean("Name", 0));
                tableHeaders.add(new ReportGeneratorBean("Description", 0));
                tableHeaders.add(new ReportGeneratorBean("Type Code", 0));


                mainHeaders.add("Spec. Allow. Model Data");

                rt.setReportTitle("Spec. Allow. Model Data");
                break;
        }


        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            hirMappedHeaders.add(mappedHeader);
        }

        if(pObjectType == 1){
            rt.setBusinessCertificate(bc);
            rt.setGroupBy("Bank Name");
            rt.setMainHeaders(mainHeaders);
            rt.setSubGroupBy(null);
            rt.setTableData(hiringList);
            rt.setTableHeaders(hirMappedHeaders);
            rt.setTableType(1);
            rt.setTotalInd(0);
            rt.setGroupedKeySet(uniqueSet.keySet());

            groupedDataExcelReportGenerator.getExcel(response, request, rt);
        }
        else {
            rt.setBusinessCertificate(bc);
            rt.setGroupBy(null);
            rt.setMainHeaders(mainHeaders);
            rt.setSubGroupBy(null);
            rt.setTableData(hiringList);
            rt.setTableHeaders(hirMappedHeaders);
            rt.setTableType(0);
            rt.setTotalInd(0);

            groupedDataExcelReportGenerator.getExcel(response, request, rt);
        }
    }
}
