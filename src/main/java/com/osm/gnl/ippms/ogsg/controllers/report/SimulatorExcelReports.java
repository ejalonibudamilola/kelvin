package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.base.services.LeaveReportService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SimpleExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.ltg.domain.LtgMasterBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SimulatorExcelReports extends BaseController {

    @Autowired
    LeaveReportService leaveReportService;

    @RequestMapping({"/ltgSimulationReportExcel.do"})
    public void setupForm(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        PaginationBean paginationBean = getPaginationInfo(request);

        BusinessCertificate bc = getBusinessCertificate(request);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        List<LtgMasterBean> empList = this.leaveReportService.loadLtgMasterBeansForDisplay(
                (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(), bc);


        List<Map<String, Object>> contPenMapped = new ArrayList<>();
        for (LtgMasterBean data : empList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put("Name", data.getName());
            newData.put("Simulation Date", data.getSimulationMonthStr());
            newData.put("No. of MDAs", data.getNoOfMdasAffected());
            newData.put("Status", data.getSimulationStatus());
            newData.put("Created By", data.getCreatedBy());
            newData.put("Created Date", data.getCreatedDateStr());
            contPenMapped.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean("Name", 0));
        tableHeaders.add(new ReportGeneratorBean("Simulation Date", 0));
        tableHeaders.add(new ReportGeneratorBean("No. of MDAs", 1));
        tableHeaders.add(new ReportGeneratorBean("Status", 0));
        tableHeaders.add(new ReportGeneratorBean("Created By", 0));
        tableHeaders.add(new ReportGeneratorBean("Created Date", 0));

        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : tableHeaders) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherSpecHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add("LTG Simulation Report");

        rt.setBusinessCertificate(bc);
        rt.setGroupBy(null);
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(OtherSpecHeaders);
        rt.setReportTitle("LTG Simulation Report");
        rt.setTableType(0);
        rt.setTotalInd(1);


        simpleExcelReportGenerator.getExcel(response, request, rt);
    }
}
