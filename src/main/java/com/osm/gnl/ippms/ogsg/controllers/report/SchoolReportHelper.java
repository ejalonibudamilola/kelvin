/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.controllers.report.customizedpdfs.SubGroupedPdf;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.GroupedDataExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.PdfSimpleGeneratorClass;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public abstract class SchoolReportHelper {


    public static void prepareReport(PayrollService payrollService, BusinessCertificate bc, int pRunMonth, int pRunYear, int reportType, HttpServletRequest request, int fromLevel,
                                     int toLevel, String bank, HttpServletResponse response) throws IOException {



        List<EmployeePayBean> empBeanList = payrollService.loadEmployeePayBeanByParentIdGlAndBank( pRunMonth, pRunYear, fromLevel, toLevel, bank, bc,true);


        List<Map<String, Object>> bankSummaryList = new ArrayList<>();

         Map<String, Object> newData;
        int i = 1;
         Collections.sort(empBeanList,Comparator.comparing(EmployeePayBean::getMda).thenComparing(EmployeePayBean::getSchoolName).thenComparing(EmployeePayBean::getFullName));
        LinkedHashMap<String, Integer> uniqueSet = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> subUniqueSet = new LinkedHashMap<>();
        for (EmployeePayBean data : empBeanList) {
            newData = new HashMap<>();
            newData.put(bc.getStaffTypeName(), data.getEmployee().getDisplayName());
            newData.put(bc.getStaffTitle(), data.getEmployee().getEmployeeId());
            newData.put("Grade/Step", data.getSalaryInfo().getLevelStepStr());
            newData.put("Basic", data.getMonthlyBasic());
            newData.put("Total Allowance", data.getTotalAllowance());
            newData.put("Gross Pay", data.getTotalPay());
            newData.put("Tax Paid", data.getTaxesPaid());
            newData.put("Pension ("+bc.getStaffTypeName()+")", data.getContributoryPension());
            newData.put("Total Loan Deduction", data.getTotalGarnishments());
            newData.put("Total Deduction", data.getTotalDeductions());
            newData.put("Bank", data.getBankName());
            newData.put("Payable Amount", data.getNetPay());
            newData.put("Account No.", data.getAccountNumber());
            newData.put("School", data.getSchoolName());
            newData.put(bc.getMdaTitle(),data.getMda());
            bankSummaryList.add(newData);
            subUniqueSet.put(data.getSchoolName(), i++);
            uniqueSet.put(data.getMda(),i++);
        }

        List<ReportGeneratorBean> list = new ArrayList<>();
        list.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        list.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        list.add(new ReportGeneratorBean("Grade/Step", 0));
        list.add(new ReportGeneratorBean("Basic", 2));
        list.add(new ReportGeneratorBean("Total Allowance", 2));
        list.add(new ReportGeneratorBean("Gross Pay", 2));
        list.add(new ReportGeneratorBean("Tax Paid", 2));
        list.add(new ReportGeneratorBean("Pension ("+bc.getStaffTypeName()+")", 2));
        list.add(new ReportGeneratorBean("Total Loan Deduction", 2));
        list.add(new ReportGeneratorBean("Total Deduction", 2));
        list.add(new ReportGeneratorBean("Bank", 0));
        list.add(new ReportGeneratorBean("Payable Amount", 2));
        list.add(new ReportGeneratorBean("Account No.", 0));
        list.add(new ReportGeneratorBean("School", 3));
        list.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));


        List<Map<String, Object>> BankHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : list){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            BankHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        String addendum = " PAYROLL ANALYSIS (TSC) - DETAILED";
        if(bc.isSubeb()){
            addendum = " PAYROLL ANALYSIS (SCHOOLS) - DETAILED";
        }
        List<String> mainHeaders2 = new ArrayList<>();
        LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);

        mainHeaders2.add("Pay Period: "+sDate.getMonth()+", "+sDate.getYear());

        mainHeaders.add(PayrollBeanUtils.getMonthNameAndYearForExcelNaming(pRunMonth,pRunYear,false, true)+addendum);
        ReportGeneratorBean rt = new ReportGeneratorBean();

        rt.setBusinessCertificate(bc);
        rt.setGroupBy(bc.getMdaTitle());
        rt.setReportTitle(bc.getBusinessName()+"_PayrollAnalysisBySchool"+PayrollBeanUtils.getMonthNameAndYearForExcelNaming(pRunMonth,pRunYear,false,true)+addendum);
        rt.setMainHeaders(mainHeaders);
        rt.setMainHeaders2(mainHeaders2);
        rt.setSubGroupBy("School");
        rt.setTableData(bankSummaryList);
        rt.setTableHeaders(BankHeaders);
        rt.setTableType(2);
        rt.setTotalInd(1);
        rt.setGroupedKeySet(uniqueSet.keySet());
        rt.setSubGroupedKeySet(subUniqueSet.keySet());

        if(reportType == 1) {
            new GroupedDataExcelReportGenerator().getExcel(response, request, rt);
        }
        else{
            rt.setCheckRotate(true);
            rt.setWatermark(bc.getBusinessName()+" Payroll Analysis By School");
            rt.setOutputInd(true);
            new SubGroupedPdf().getPdf(response, request, rt);
        }

    }
}
