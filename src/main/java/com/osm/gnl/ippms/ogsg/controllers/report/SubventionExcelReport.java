package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SimpleExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.domain.subvention.Subvention;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class SubventionExcelReport extends BaseController {

    @RequestMapping({"/inactiveSubventionReportExcel.do"})
    public void setupForm(Model model,
                          HttpServletRequest request, HttpServletResponse response) throws Exception {

        ReportGeneratorBean rt = new ReportGeneratorBean();

        BusinessCertificate bc = getBusinessCertificate(request);

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        List<Subvention> empList = this.genericService.loadAllObjectsUsingRestrictions(Subvention.class, Arrays.asList(
                getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("expire", ON)), "name");

        List<Map<String, Object>> subSummaryList = new ArrayList<>();
        for (Subvention data : empList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put("Name", data.getName());
            newData.put("Amount", data.getAmount());
            newData.put("Created Date", data.getCreatedDateStr());
            newData.put("Created By", data.getCreatedBy().getActualUserName());
            newData.put("Expiration Date", data.getExpirationDateStr());
            subSummaryList.add(newData);
        }

        List<ReportGeneratorBean> list = new ArrayList<>();
        list.add(new ReportGeneratorBean("Name", 0));
        list.add(new ReportGeneratorBean("Amount", 2));
        list.add(new ReportGeneratorBean("Created Date", 0));
        list.add(new ReportGeneratorBean("Created By", 0));
        list.add(new ReportGeneratorBean("Expiration Date", 0));

        List<Map<String, Object>> subHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : list){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            subHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getBusinessName()+" - Inactive Subvention Report");

            rt.setBusinessCertificate(bc);
            rt.setGroupBy(null);
            rt.setReportTitle("Inactive Subvention Report");
            rt.setMainHeaders(mainHeaders);
            rt.setSubGroupBy(null);
            rt.setTableData(subSummaryList);
            rt.setTableHeaders(subHeaders);
            rt.setTableType(0);
            rt.setTotalInd(1);
            simpleExcelReportGenerator.getExcel(response, request, rt);
        }


    @RequestMapping({"/activeSubventionReportExcel.do"})
    public void setupForm2(Model model,
                          HttpServletRequest request, HttpServletResponse response) throws Exception {

        ReportGeneratorBean rt = new ReportGeneratorBean();

        BusinessCertificate bc = getBusinessCertificate(request);

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        List<Subvention> empList = this.genericService.loadAllObjectsUsingRestrictions(Subvention.class, Arrays.asList(
                getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("expire", OFF)), "name");

        List<Map<String, Object>> subSummaryList = new ArrayList<>();
        for (Subvention data : empList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put("Name", data.getName());
            newData.put("Amount", data.getAmount());
            newData.put("Created Date", data.getCreatedDateStr());
            newData.put("Created By", data.getCreatedBy().getActualUserName());
            newData.put("Expiration Date", data.getExpirationDateStr());
            subSummaryList.add(newData);
        }

        List<ReportGeneratorBean> list = new ArrayList<>();
        list.add(new ReportGeneratorBean("Name", 0));
        list.add(new ReportGeneratorBean("Amount", 2));
        list.add(new ReportGeneratorBean("Created Date", 0));
        list.add(new ReportGeneratorBean("Created By", 0));
        list.add(new ReportGeneratorBean("Expiration Date", 0));

        List<Map<String, Object>> subHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : list){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            subHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getBusinessName()+" - Active Subvention Report");

        rt.setBusinessCertificate(bc);
        rt.setGroupBy(null);
        rt.setReportTitle("Active Subvention Report");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(subSummaryList);
        rt.setTableHeaders(subHeaders);
        rt.setTableType(0);
        rt.setTotalInd(1);
        simpleExcelReportGenerator.getExcel(response, request, rt);
    }

}
