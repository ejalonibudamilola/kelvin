package com.osm.gnl.ippms.ogsg.controllers.chart;


import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.ChartService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.chart.ChartDTO;
import com.osm.gnl.ippms.ogsg.domain.chart.ChartTableBean;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Damilola Ejalonibu
 */

@Controller
public class ShowChartTableController extends BaseController {

    @Autowired
    ChartService chartService;

    public ShowChartTableController() {

    }


    @RequestMapping(value={"/showPromotionChartTable.do"})
    public ModelAndView promotionChartTable(Model pModel, HttpServletRequest pRequest,
                                            @RequestParam(value="label") String label,
                                            @RequestParam(value = "rm") Integer pRunMonth,
                                            @RequestParam(value = "ry") Integer pRunYear) throws Exception {
        SessionManagerService.manageSession(pRequest, pModel);

        String payPeriod;
        if (IppmsUtils.isNotNullAndGreaterThanZero(pRunYear) && IppmsUtils.isNotNullAndGreaterThanZero(pRunYear)) {
            payPeriod = PayrollUtils.makeAuditPayPeriod(pRunMonth, pRunYear);
        } else {
            LocalDate localDate = LocalDate.now();
            payPeriod = PayrollUtils.makeAuditPayPeriod(localDate.getMonthValue(), localDate.getYear());
        }

        //System.out.println("Month in table is "+month);

        String title = "Details of Staffs Promoted in "+label;
        List<String> headers = new ArrayList<>();
        headers.add("Staff ID");
        headers.add("Name");
        headers.add("Phone Number");
        headers.add("From: Pay Group");
        headers.add("To Pay Group");


        //ChartController chartController = new ChartController();

        MdaInfo mdaInfo = this.genericService.loadObjectWithSingleCondition(MdaInfo.class,
                CustomPredicate.procurePredicate("codeName",label));

        List<ChartDTO> employees = chartService.loadPromotionDetailsForModalWindow(this.genericService.loadObjectById(BusinessClient.class,mdaInfo.getBusinessClientId()),mdaInfo,payPeriod);

        List<LinkedHashMap<String, Object>> chartData = new ArrayList<>();
        LinkedHashMap<String, Object> mappedData;
        for (ChartDTO data : employees) {
            mappedData = new LinkedHashMap<>();
            mappedData.put("Staff ID", data.getEmployeeId());
            mappedData.put("Name", data.getLastName() + " " +data.getFirstName() + " "+data.getInitials());
            mappedData.put("Phone Number",data.getGsmNumber());
            mappedData.put("From: Pay Group", data.getSalaryTypeName() + ":"+data.getLevel() + "/"+ data.getStep());
            mappedData.put("To: Pay Group", data.getSalaryTypeName2() + ":"+data.getLevel2() + "/"+ data.getStep2());
            chartData.add(mappedData);
        }

        ChartTableBean chartTableBean = new ChartTableBean();
        chartTableBean.setTitle(title);
        chartTableBean.setHeaders(headers);
        chartTableBean.setTableData(chartData);

        return new ChartController().chartTable(chartTableBean, pRequest, pModel);


    }

    @RequestMapping(value={"/showNewStaffChartTable.do"})
    public ModelAndView newStaffChartTable(Model pModel, HttpServletRequest pRequest,
                                           @RequestParam(value="label") String label,
                                           @RequestParam(value = "rm") Integer pRunMonth,
                                           @RequestParam(value = "ry") Integer pRunYear) throws Exception {
        SessionManagerService.manageSession(pRequest, pModel);

//        System.out.println("Run month and year is "+pRunMonth + " "+pRunYear);

        String payPeriod = "";
        if (IppmsUtils.isNotNullAndGreaterThanZero(pRunYear) && IppmsUtils.isNotNullAndGreaterThanZero(pRunYear)) {
            payPeriod = PayrollUtils.makeAuditPayPeriod(pRunMonth, pRunYear);
        } else {
            LocalDate localDate = LocalDate.now();
            payPeriod = PayrollUtils.makeAuditPayPeriod(localDate.getMonthValue(), localDate.getYear());
        }
        String month = payPeriod;

        String title = "Details of New Employee in "+label;
        List<String> headers = new ArrayList<>();
        headers.add("Staff ID");
        headers.add("Name");
        headers.add("Phone Number");
        headers.add("Pay Group");

        ChartController chartController = new ChartController();

        MdaInfo mdaInfo = this.genericService.loadObjectWithSingleCondition(MdaInfo.class,
                CustomPredicate.procurePredicate("codeName",label));

        List<ChartDTO> employees = chartService.loadNewEmployeeDetailsForModalWindow(mdaInfo,month);

        List<LinkedHashMap<String, Object>> chartData = new ArrayList<>();
        for (ChartDTO data : employees) {
            LinkedHashMap<String, Object> mappedData = new LinkedHashMap<>();
            mappedData.put("Staff ID", data.getEmployeeId());
            mappedData.put("Name", data.getLastName() + " "+ data.getFirstName() + " "+data.getInitials());
            mappedData.put("Phone Number",data.getGsmNumber());
            mappedData.put("Pay Group",data.getSalaryTypeName() + ":"+data.getLevel() + "/"+ data.getStep());
            chartData.add(mappedData);
        }

        //System.out.println("Chart Data is "+chartData);

        ChartTableBean chartTableBean = new ChartTableBean();
        chartTableBean.setTitle(title);
        chartTableBean.setHeaders(headers);
        chartTableBean.setTableData(chartData);

        return chartController.chartTable(chartTableBean, pRequest, pModel);
    }

    @RequestMapping(value={"/showMdaChartTable.do"})
    public ModelAndView generateMdaTypeTable(Model pModel, HttpServletRequest pRequest,
                                             @RequestParam(value="label") String label) throws Exception {
        SessionManagerService.manageSession(pRequest, pModel);
        ChartController chartController = new ChartController();

        List<Employee> employees = this.chartService.loadStaffDetailsForModalWindow(label);

        String title = "Details of Employee in "+label;
        List<String> headers = new ArrayList<>();
        headers.add("Staff ID");
        headers.add("Name");
        headers.add("Phone Number");
        headers.add("Organization Name");

        List<LinkedHashMap<String, Object>> chartData = new ArrayList<>();
        for (Employee data : employees) {
            LinkedHashMap<String, Object> mappedData = new LinkedHashMap<>();
            mappedData.put("Staff ID", data.getEmployeeId());
            mappedData.put("Name", data.getLastName() + " "+data.getFirstName()+ " "+data.getInitials());
            mappedData.put("Phone Number", data.getGsmNumber());
            mappedData.put("Organization Name", data.getMdaName());
            chartData.add(mappedData);
        }

        ChartTableBean chartTableBean = new ChartTableBean();
        chartTableBean.setTitle(title);
        chartTableBean.setHeaders(headers);
        chartTableBean.setTableData(chartData);

        return chartController.chartTable(chartTableBean, pRequest, pModel);

    }

    @RequestMapping(value={"/showAbsorbedChartTable.do"})
    public ModelAndView absorbedChartTable(Model pModel, HttpServletRequest pRequest,
                                           @RequestParam(value="label") String label,
                                           @RequestParam(value = "rm") Integer pRunMonth,
                                           @RequestParam(value = "ry") Integer pRunYear) throws Exception {
        SessionManagerService.manageSession(pRequest, pModel);

        String payPeriod = "";
        if (IppmsUtils.isNotNullAndGreaterThanZero(pRunYear) && IppmsUtils.isNotNullAndGreaterThanZero(pRunYear)) {
            payPeriod = PayrollUtils.makeAuditPayPeriod(pRunMonth, pRunYear);
        } else {
            LocalDate localDate = LocalDate.now();
            payPeriod = PayrollUtils.makeAuditPayPeriod(localDate.getMonthValue(), localDate.getYear());
        }
        String month = payPeriod;

        String title = "Details of Employees Absorbed in "+label;
        List<String> headers = new ArrayList<>();
        headers.add("Staff ID");
        headers.add("Name");
        headers.add("Phone Number");
        headers.add("Pay Group");
        headers.add("Suspension Date");
        headers.add("Absorption Date");

        ChartController chartController = new ChartController();

        MdaInfo mdaInfo = this.genericService.loadObjectWithSingleCondition(MdaInfo.class,
                CustomPredicate.procurePredicate("codeName",label));


        List<ChartDTO> employees = chartService.loadAbsorbedEmployeeDetailsForModalWindow(mdaInfo,month);

        List<LinkedHashMap<String, Object>> chartData = new ArrayList<>();
        for (ChartDTO data : employees) {
            LinkedHashMap<String, Object> mappedData = new LinkedHashMap<>();
            mappedData.put("Staff ID", data.getEmployeeId());
            mappedData.put("Name", data.getLastName() + " "+ data.getFirstName() + " "+data.getInitials());
            mappedData.put("Phone Number",data.getGsmNumber());
            mappedData.put("Pay Group",data.getSalaryTypeName() + ":"+data.getLevel() + "/"+ data.getStep());
            mappedData.put("Suspension Date", data.getSuspensionDateStr());
            mappedData.put("Absorption Date", data.getConfirmationDateAsStr());
            chartData.add(mappedData);
        }

        ChartTableBean chartTableBean = new ChartTableBean();
        chartTableBean.setTitle(title);
        chartTableBean.setHeaders(headers);
        chartTableBean.setTableData(chartData);

        return chartController.chartTable(chartTableBean, pRequest, pModel);
    }

    @RequestMapping(value={"/showReinstatedChartTable.do"})
    public ModelAndView reinstatedChartTable(Model pModel, HttpServletRequest pRequest,
                                             @RequestParam(value="label") String label,
                                             @RequestParam(value = "rm") Integer pRunMonth,
                                             @RequestParam(value = "ry") Integer pRunYear) throws Exception {
        SessionManagerService.manageSession(pRequest, pModel);

        String payPeriod = "";
        if (IppmsUtils.isNotNullAndGreaterThanZero(pRunYear) && IppmsUtils.isNotNullAndGreaterThanZero(pRunYear)) {
            payPeriod = PayrollUtils.makeAuditPayPeriod(pRunMonth, pRunYear);
        } else {
            LocalDate localDate = LocalDate.now();
            payPeriod = PayrollUtils.makeAuditPayPeriod(localDate.getMonthValue(), localDate.getYear());
        }
        String month = payPeriod;

        String title = "Details of Employees Reinstated in "+label;
        List<String> headers = new ArrayList<>();
        headers.add("Staff ID");
        headers.add("Name");
        headers.add("Phone Number");
        headers.add("Pay Group");
        headers.add("Terminated Date");
        headers.add("Reinstated Date");

        ChartController chartController = new ChartController();

        MdaInfo mdaInfo = this.genericService.loadObjectWithSingleCondition(MdaInfo.class,
                CustomPredicate.procurePredicate("codeName",label));

        List<ChartDTO> employees = chartService.loadReinstatedEmployeeDetailsForModalWindow(mdaInfo,month);


        List<LinkedHashMap<String, Object>> chartData = new ArrayList<>();
        for (ChartDTO data : employees) {
            LinkedHashMap<String, Object> mappedData = new LinkedHashMap<>();
            mappedData.put("Staff ID", data.getEmployeeId());
            mappedData.put("Name", data.getLastName() + " "+ data.getFirstName() + " "+data.getInitials());
            mappedData.put("Phone Number",data.getGsmNumber());
            mappedData.put("Pay Group",data.getSalaryTypeName() + ":"+data.getLevel() + "/"+ data.getStep());
            mappedData.put("Terminated Date", data.getTerminatedDate());
            mappedData.put("Reinstated Date", data.getConfirmationDateAsStr());
            chartData.add(mappedData);
        }

        ChartTableBean chartTableBean = new ChartTableBean();
        chartTableBean.setTitle(title);
        chartTableBean.setHeaders(headers);
        chartTableBean.setTableData(chartData);

        return chartController.chartTable(chartTableBean, pRequest, pModel);
    }

    @RequestMapping(value={"/showPaycheckChartTable.do"})
    public ModelAndView payCheckChartTable(Model pModel, HttpServletRequest pRequest,
                                             @RequestParam(value="label") String label,
                                             @RequestParam(value = "rm") Integer pRunMonth,
                                             @RequestParam(value = "ry") Integer pRunYear) throws Exception {
        SessionManagerService.manageSession(pRequest, pModel);

        LocalDate payCheckDate;

        if (IppmsUtils.isNotNullAndGreaterThanZero(pRunYear) && IppmsUtils.isNotNullAndGreaterThanZero(pRunYear)) {
            LocalDate sDate = LocalDate.of(pRunYear, pRunMonth, 1);
            payCheckDate = sDate.withDayOfMonth(sDate.lengthOfMonth());

        } else {
            LocalDate localDate = LocalDate.now();
            payCheckDate = localDate.withDayOfMonth(localDate.lengthOfMonth());
        }

        String title = "Details of Employees Paycheck in " + label;
        List<String> headers = new ArrayList<>();
        headers.add("Staff ID");
        headers.add("Name");
        headers.add("Phone Number");
        headers.add("Net Pay");
        headers.add("Gross Pay");

        ChartController chartController = new ChartController();

        MdaInfo mdaInfo = this.genericService.loadObjectWithSingleCondition(MdaInfo.class,
                CustomPredicate.procurePredicate("codeName", label));

        long mdaId = mdaInfo.getId();
        long clientCode = mdaInfo.getBusinessClientId();

        BusinessClient bc = this.genericService.loadObjectWithSingleCondition(BusinessClient.class,
                CustomPredicate.procurePredicate("id",clientCode));
//
//
//        List<AbstractPaycheckEntity> paycheckEntities = (List<AbstractPaycheckEntity>) this.genericService.loadAllObjectsUsingRestrictions(
//                IppmsUtils.getPaycheckClass(BusinessCertificateCreator.makeBusinessClient(bc)), Arrays.asList(CustomPredicate.procurePredicate("mdaDeptMap.mdaInfo.id", mdaId),
//                        CustomPredicate.procurePredicate("payDate", payCheckDate)), null);

        List<ChartDTO> paycheckEntities = this.chartService.getEmployeeTotalPayDetails(bc,payCheckDate,mdaId);

        List<LinkedHashMap<String, Object>> chartData = new ArrayList<>();
        for (ChartDTO data : paycheckEntities) {
            LinkedHashMap<String, Object> mappedData = new LinkedHashMap<>();
            mappedData.put("Staff ID", data.getEmployeeId());
            mappedData.put("Name", data.getLastName() + " "+ data.getFirstName() + " "+data.getInitials());
            mappedData.put("Phone Number", data.getGsmNumber());
            mappedData.put("Net Pay", data.getPay());
            mappedData.put("Total Pay", data.getSpecialPay());
            chartData.add(mappedData);
        }

        ChartTableBean chartTableBean = new ChartTableBean();
        chartTableBean.setTitle(title);
        chartTableBean.setHeaders(headers);
        chartTableBean.setTableData(chartData);

        return chartController.chartTable(chartTableBean, pRequest, pModel);
    }

    @RequestMapping(value={"/showSpecAllowChartTable.do"})
    public ModelAndView specAllowChartTable(Model pModel, HttpServletRequest pRequest,
                                           @RequestParam(value="label") String label,
                                           @RequestParam(value = "rm") Integer pRunMonth,
                                           @RequestParam(value = "ry") Integer pRunYear) throws Exception {
        SessionManagerService.manageSession(pRequest, pModel);

        LocalDate payCheckDate;

        if (IppmsUtils.isNotNullAndGreaterThanZero(pRunYear) && IppmsUtils.isNotNullAndGreaterThanZero(pRunYear)) {
            LocalDate sDate = LocalDate.of(pRunYear, pRunMonth, 1);
            payCheckDate = sDate.withDayOfMonth(sDate.lengthOfMonth());

        } else {
            LocalDate localDate = LocalDate.now();
            payCheckDate = localDate.withDayOfMonth(localDate.lengthOfMonth());
        }

        String title = "Details of Employees Special Allowance in " + label;
        List<String> headers = new ArrayList<>();
        headers.add("Staff ID");
        headers.add("Name");
        headers.add("Phone Number");
        headers.add("Total Special Allowance");
        headers.add("Monthly Pay");

        ChartController chartController = new ChartController();

        MdaInfo mdaInfo = this.genericService.loadObjectWithSingleCondition(MdaInfo.class,
                CustomPredicate.procurePredicate("codeName", label));

        long mdaId = mdaInfo.getId();
        long clientCode = mdaInfo.getBusinessClientId();

        BusinessClient bc = this.genericService.loadObjectWithSingleCondition(BusinessClient.class,
                CustomPredicate.procurePredicate("id",clientCode));

        List<ChartDTO> employeePayBean = this.chartService.getEmployeeSpecAllowDetails(bc,payCheckDate,mdaId);

        List<LinkedHashMap<String, Object>> chartData = new ArrayList<>();
        for (ChartDTO data : employeePayBean) {
            LinkedHashMap<String, Object> mappedData = new LinkedHashMap<>();
            mappedData.put("Staff ID", data.getEmployeeId());
            mappedData.put("Name", data.getLastName() + " "+ data.getFirstName() + " "+ data.getInitials());
            mappedData.put("Phone Number", data.getGsmNumber());
            mappedData.put("Total Special Allowance", data.getSpecialPay());
            mappedData.put("Monthly Pay", data.getPay());
            chartData.add(mappedData);
            //System.out.println("chart data is "+chartData);
        }

        ChartTableBean chartTableBean = new ChartTableBean();
        chartTableBean.setTitle(title);
        chartTableBean.setHeaders(headers);
        chartTableBean.setTableData(chartData);

        return chartController.chartTable(chartTableBean, pRequest, pModel);
    }
}
