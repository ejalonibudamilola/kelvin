package com.osm.gnl.ippms.ogsg.controllers.report.ext;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SimpleExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.domain.beans.FileParseBean;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class FileUploadReportGenerator extends BaseController {

    @RequestMapping({"/exportFileUploadResultExcel.do"})
    public void setupForm(@RequestParam("uid") String pFileUploadId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        Object wFPB = getSessionAttribute(request, pFileUploadId);
        FileParseBean _wFPB = (FileParseBean) wFPB;

        ReportGeneratorBean rt = makeReportGeneratorBean(_wFPB, bc);

        new SimpleExcelReportGenerator().getExcel(response, request, rt);

    }

    private ReportGeneratorBean makeReportGeneratorBean(FileParseBean fileParseBean, BusinessCertificate bc) {
        ReportGeneratorBean rt = new ReportGeneratorBean();
        Map<String, Object> newData;
        List<Map<String, Object>> newCompExecSummary = new ArrayList<>();
        List<ReportGeneratorBean> headerList = new ArrayList<>();
        Collections.sort(fileParseBean.getErrorList(), Comparator.comparing(NamedEntity::getName));
        if (fileParseBean.isSalaryInfo()) {
            for (NamedEntity data : fileParseBean.getErrorList()) {

                newData = new HashMap<>();
                newData.put("Grade Level", data.getName());
                newData.put(bc.getStaffTitle(), data.getStaffId());
                newData.put("Error Details", data.getDisplayErrors());

                newCompExecSummary.add(newData);
            }

            headerList.add(new ReportGeneratorBean("Grade Level", 0));
            headerList.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
            headerList.add(new ReportGeneratorBean("Error Details", 0));

        } else if (fileParseBean.isBankBranch()) {
            for (NamedEntity data : fileParseBean.getErrorList()) {

                newData = new HashMap<>();
                newData.put("Grade Level", data.getName());
                newData.put(bc.getStaffTitle(), data.getStaffId());
                newData.put("Error Details", data.getDisplayErrors());

                newCompExecSummary.add(newData);
            }

            headerList.add(new ReportGeneratorBean("Grade Level", 0));
            headerList.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
            headerList.add(new ReportGeneratorBean("Error Details", 0));
        } else {

            if (fileParseBean.isStepIncrement()) {
                for (NamedEntity data : fileParseBean.getErrorList()) {

                    newData = new HashMap<>();
                    newData.put(bc.getStaffTitle(), data.getStaffId());
                    newData.put(bc.getStaffTypeName(), data.getName());
                    newData.put("Pay Group", data.getPayTypeName());
                    newData.put("Current Level/Step", data.getLevelAndStep());
                    newData.put("Step Increment Level/Step", data.getObjectCode());
                    newData.put("Error Details", data.getDisplayErrors());

                    newCompExecSummary.add(newData);
                }

                headerList.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                headerList.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                headerList.add(new ReportGeneratorBean("Pay Group", 0));
                headerList.add(new ReportGeneratorBean("Current Level/Step", 0));
                headerList.add(new ReportGeneratorBean("Step Increment Level/Step", 0));
                headerList.add(new ReportGeneratorBean("Error Details", 0));
            } else if (fileParseBean.isLeaveBonus()) {
                for (NamedEntity data : fileParseBean.getErrorList()) {

                    newData = new HashMap<>();
                    newData.put(bc.getStaffTitle(), data.getStaffId());
                    newData.put(bc.getStaffTypeName(), data.getName());
                    newData.put(bc.getMdaTitle(), data.getEnteredCaptcha());
                    newData.put("Amount", data.getAllowanceAmount());
                    newData.put("Year", data.getRunYear());
                    newData.put("Error Details", data.getDisplayErrors());

                    newCompExecSummary.add(newData);
                }

                headerList.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                headerList.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                headerList.add(new ReportGeneratorBean(bc.getMdaTitle(), 0));
                headerList.add(new ReportGeneratorBean("Amount", 0));
                headerList.add(new ReportGeneratorBean("Year", 0));
                headerList.add(new ReportGeneratorBean("Error Details", 0));
            } else if (fileParseBean.isDeduction()) {
                for (NamedEntity data : fileParseBean.getErrorList()) {

                    newData = new HashMap<>();
                    newData.put(bc.getStaffTitle(), data.getStaffId());
                    newData.put(bc.getStaffTypeName(), data.getName());
                    newData.put("Deduction Code", data.getObjectCode());
                    newData.put("Deduction Amount", data.getDeductionAmount());
                    newData.put("Error Details", data.getDisplayErrors());

                    newCompExecSummary.add(newData);
                }

                headerList.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                headerList.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                headerList.add(new ReportGeneratorBean("Deduction Code", 0));
                headerList.add(new ReportGeneratorBean("Deduction Amount", 0));
                headerList.add(new ReportGeneratorBean("Error Details", 0));
            } else if (fileParseBean.isSpecialAllowance()) {
                for (NamedEntity data : fileParseBean.getErrorList()) {

                    newData = new HashMap<>();
                    newData.put(bc.getStaffTitle(), data.getStaffId());
                    newData.put(bc.getStaffTypeName(), data.getName());
                    newData.put("Special Allowance", data.getObjectCode());
                    newData.put("Allowance Amount", data.getAllowanceAmount());
                    newData.put("Start Date", data.getAllowanceStartDate());
                    newData.put("End Date", data.getAllowanceEndDate());
                    newData.put("Error Details", data.getDisplayErrors());

                    newCompExecSummary.add(newData);
                }

                headerList.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                headerList.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                headerList.add(new ReportGeneratorBean("Special Allowance", 0));
                headerList.add(new ReportGeneratorBean("Allowance Amount", 0));
                headerList.add(new ReportGeneratorBean("Start Date", 0));
                headerList.add(new ReportGeneratorBean("End Date", 0));
                headerList.add(new ReportGeneratorBean("Error Details", 0));
            } else if (fileParseBean.isLoan()) {
                for (NamedEntity data : fileParseBean.getErrorList()) {

                    newData = new HashMap<>();
                    newData.put(bc.getStaffTitle(), data.getStaffId());
                    newData.put(bc.getStaffTypeName(), data.getName());
                    newData.put("Loan Code", data.getObjectCode());
                    newData.put("Loan Amount", data.getLoanBalance());
                    newData.put("Tenor", data.getTenor());
                    newData.put("Error Details", data.getDisplayErrors());

                    newCompExecSummary.add(newData);
                }

                headerList.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                headerList.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                headerList.add(new ReportGeneratorBean("Loan Code", 0));
                headerList.add(new ReportGeneratorBean("Loan Amount", 0));
                headerList.add(new ReportGeneratorBean("Tenor", 0));
                headerList.add(new ReportGeneratorBean("Error Details", 0));
            }
        }
        List<Map<String, Object>> mappedHeadersList = new ArrayList<>();
        for (ReportGeneratorBean head : headerList) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            mappedHeadersList.add(mappedHeader);
        }

        //adding report headers
        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(fileParseBean.getName() + " File Upload Error Report");

        //setting report bean
        rt.setTableData(newCompExecSummary);
        rt.setTableHeaders(mappedHeadersList);
        rt.setBusinessCertificate(bc);
        rt.setMainHeaders(mainHeaders);
        rt.setTableType(0);
        rt.setGroupBy(null);
        rt.setSubGroupBy(null);
        rt.setReportTitle(fileParseBean.getName() + " Error Report");
        rt.setTotalInd(0);
        rt.setGroupedKeySet(null);


        return rt;
    }
}
