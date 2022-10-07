package com.osm.gnl.ippms.ogsg.controllers.chart;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.audit.domain.EmployeeAudit;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.ChartService;
import com.osm.gnl.ippms.ogsg.base.services.ChartServiceExt;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.chart.ChartDTO;
import com.osm.gnl.ippms.ogsg.domain.chart.ChartMiniBean;
import com.osm.gnl.ippms.ogsg.domain.chart.SingleChartBean;
import com.osm.gnl.ippms.ogsg.domain.suspension.AbsorptionLog;
import com.osm.gnl.ippms.ogsg.domain.suspension.ReinstatementLog;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.MdaType;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

/**
 * @author Damilola Ejalonibu
 */

@Controller

public class ShowChartController extends BaseController {

    private final ChartService chartService;
    private final ChartServiceExt chartServiceExt;
    private final PaycheckService paycheckService;

    @Autowired
    public ShowChartController(ChartService chartService, ChartServiceExt chartServiceExt, PaycheckService paycheckService) {
        this.chartService = chartService;
        this.chartServiceExt = chartServiceExt;
        this.paycheckService = paycheckService;
    }

    @RequestMapping(value = "/showChartDashboard.do", method = RequestMethod.GET)
    public ModelAndView generateChartReport(@ModelAttribute("chartMiniBean") ChartMiniBean chartMiniBean,
                                            HttpServletRequest request, Model pModel) throws Exception{

        BusinessCertificate pBiz = super.getBusinessCertificate(request);

        SessionManagerService.manageSession(request, pModel);
        ModelAndView model = new ModelAndView();
        List<NamedEntity> getMonthList = PayrollBeanUtils.makeAllMonthList();
        List<NamedEntity> getYearList = this.paycheckService.makePaycheckYearList(getBusinessCertificate(request));

        String payPeriod,monthName,year;

        LocalDate payDate;

        Map<Long,BusinessClient> bizClientMap = genericService.loadAllObjectsAsMapWithoutRestrictions(BusinessClient.class,"id");

        if (IppmsUtils.isNotNullAndGreaterThanZero(chartMiniBean.getRunMonth()) && IppmsUtils.isNotNullAndGreaterThanZero(chartMiniBean.getRunYear())) {
            payPeriod = PayrollUtils.makeAuditPayPeriod(chartMiniBean.getRunMonth(), chartMiniBean.getRunYear());
//            Month getMonth = Month.of(chartMiniBean.getRunMonth());
//            monthName = String.valueOf(getMonth);
            monthName = Month.of(chartMiniBean.getRunMonth()).getDisplayName( TextStyle.FULL , Locale.US );
            year = String.valueOf(chartMiniBean.getRunYear());
            LocalDate pDate = LocalDate.of(chartMiniBean.getRunYear(), chartMiniBean.getRunMonth(), 1);
            payDate = pDate.withDayOfMonth(pDate.lengthOfMonth());

        } else {
            LocalDate localDate = LocalDate.now();
            payDate = localDate.withDayOfMonth(localDate.lengthOfMonth());
            payPeriod = PayrollUtils.makeAuditPayPeriod(localDate.getMonthValue(), localDate.getYear());
//            monthName = (String.valueOf(localDate.getMonth()));
            monthName = localDate.getMonth().getDisplayName(TextStyle.FULL , Locale.US );
            year = String.valueOf(localDate.getYear());
        }
        String month = payPeriod;
        int monthValue = Integer.valueOf(month.substring(0,2));
        int yearValue = Integer.valueOf(month.substring(2));

        List<BusinessClient> businessClient = this.genericService.loadAllObjectsUsingRestrictions(BusinessClient.class, Arrays.asList(CustomPredicate.procurePredicate("id",pBiz.getBusinessClientInstId(),Operation.NOT_EQUAL)),null);

        List<String> clients = new ArrayList<>();
        List<Long> clientsCode = new ArrayList<>();
        List<String> promotionCount = new ArrayList<>();
        List<String> newRecruitCount = new ArrayList<>();
        List<String> absorptionCount = new ArrayList<>();
        List<String> reinstatementCount = new ArrayList<>();
        List<String> payCheck = new ArrayList<>();
        List<Double> payCheckSum = new ArrayList<>();
        List<String> specAllow = new ArrayList<>();
        List<Double> specAllowSum = new ArrayList<>();


        for (BusinessClient b : businessClient) {
             clients.add(b.getChartName());
            clientsCode.add(b.getId());
        }

        //Promotion
        PredicateBuilder predicateBuilder;
        Class<?> clazz;
        for (Long i : clientsCode) {
            clazz = IppmsUtils.getPromotionAuditClassByBusinessClient(bizClientMap.get(i));
            if(clazz == null) continue;
            predicateBuilder = new PredicateBuilder();
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("auditPayPeriod", month));
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", i));

               promotionCount.add(String.valueOf(this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder,clazz)));
        }

        //New Staff
        for (Long i : clientsCode) {
            predicateBuilder = new PredicateBuilder();
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", i));
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("auditActionType", "I"));
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("auditPayPeriod", month));

            newRecruitCount.add(String.valueOf(this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, EmployeeAudit.class)));
        }

        //Mda Staffs by Bc

        List<String> staffCount = new ArrayList<>();
        for (Long i : clientsCode) {
            predicateBuilder = new PredicateBuilder();
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", i));
            staffCount.add(String.valueOf(this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getEmployeeClass(bizClientMap.get(i)))));
        }


        //Special Allowance
        double sumSpecAllowAmount = 0.00;
        for (Long i : clientsCode) {
            sumSpecAllowAmount = this.chartService.sumSpecAllowAmount(i, bizClientMap.get(i), payDate);
            specAllowSum.add(sumSpecAllowAmount);
        }


        double sumSpecAllow = 0.00;
        for (double j: specAllowSum) {
            if(IppmsUtils.isNotNullOrEmpty(specAllowSum))
            sumSpecAllow += j;
        }

        double percentageSpecAllow;
        for (double k: specAllowSum){
            percentageSpecAllow = Math.round (k*100)/sumSpecAllow;
//            DecimalFormat f = new DecimalFormat("##.0");
//            specAllow.add(f.format(percentageSpecAllow));
            specAllow.add(String.valueOf(percentageSpecAllow));
        }

        //Paycheck

        double sumPaycheckTotalPay;
        for (Long i : clientsCode) {
//            BusinessClient bc = this.genericService.loadObjectWithSingleCondition(BusinessClient.class,
//                    CustomPredicate.procurePredicate("id", i));
            sumPaycheckTotalPay = this.chartService.sumPaycheckTotalPay(i,bizClientMap.get(i), payDate);
            payCheckSum.add(sumPaycheckTotalPay);
        }

        double sum = 0.00;
        for (Double j: payCheckSum) {
            if(IppmsUtils.isNotNullOrEmpty(payCheckSum))
            sum += j;
        }

        double percentage;
        if(IppmsUtils.isNotNullOrEmpty(payCheckSum))
        for (double k: payCheckSum){
            percentage = Math.round (k*100)/sum;
//            DecimalFormat f = new DecimalFormat("##.0");
//            payCheck.add(f.format(percentage));
            payCheck.add(String.valueOf(payCheck));
        }

        //Absorption
        int sc;
        for (Long i : clientsCode) {
            predicateBuilder = new PredicateBuilder();
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", i));
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("auditPayPeriod", month));
             sc = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, AbsorptionLog.class);
            absorptionCount.add(String.valueOf(sc));
            sc = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, ReinstatementLog.class);
            reinstatementCount.add(String.valueOf(sc));
        }

        String card1Url = "/ogsg_ippms/showTotalPromotedStaffChartByMda.do";
        String card2Url = "/ogsg_ippms/showTotalNewStaffChartByMda.do";
        String card3Url = "/ogsg_ippms/showAllStaffChartByMda.do";
        String card4Url = "/ogsg_ippms/showSpecAllowByMda.do";
        String card5Url = "/ogsg_ippms/showPaycheckByMda.do";
        String card6Url = "/ogsg_ippms/showAbsorbedStaffChartByMda.do";
        String card7Url = "/ogsg_ippms/showReinstatedStaffChartByMda.do";

        model.addObject("monthList", getMonthList);
        model.addObject("yearList", getYearList);
        model.addObject("month", monthName);
        model.addObject("year", year);
        model.addObject("monthValue", monthValue);
        model.addObject("yearValue", yearValue);
        model.addObject("BarX1Axis", clients);
        model.addObject("BarX1AxisSize", clients.size());
        model.addObject("BarY1Axis", promotionCount);
        model.addObject("BarY1AxisSize", promotionCount.size());
        model.addObject("BarX2Axis", clients);
        model.addObject("BarX2AxisSize", clients.size());
        model.addObject("BarY2Axis", newRecruitCount);
        model.addObject("BarY2AxisSize", newRecruitCount.size());
        model.addObject("pieLabels1", clients);
        model.addObject("pieLabel1Size", clients.size());
        model.addObject("pieData1", staffCount);
        model.addObject("pieData1Size", staffCount.size());
        model.addObject("pieLabels2", clients);
        model.addObject("pieLabel2Size", clients.size());
        model.addObject("pieData2", specAllow);
        model.addObject("pieData2Size", specAllow.size());
        model.addObject("pieLabels3", clients);
        model.addObject("pieLabel3Size", clients.size());
        model.addObject("pieData3", payCheck);
        model.addObject("pieData3Size", payCheck.size());
        model.addObject("BarX3Axis", clients);
        model.addObject("BarX3AxisSize", clients.size());
        model.addObject("BarY3Axis", absorptionCount);
        model.addObject("BarY3AxisSize", absorptionCount.size());
        model.addObject("BarX4Axis", clients);
        model.addObject("BarX4AxisSize", clients.size());
        model.addObject("BarY4Axis", reinstatementCount);
        model.addObject("BarY4AxisSize", reinstatementCount.size());
        model.addObject("card1Url", card1Url);
        model.addObject("card2Url", card2Url);
        model.addObject("card3Url", card3Url);
        model.addObject("card4Url", card4Url);
        model.addObject("card5Url", card5Url);
        model.addObject("card6Url", card6Url);
        model.addObject("card7Url", card7Url);
        addRoleBeanToModel(pModel, request);
        model.setViewName("chart/chartView");
        return model;

    }

    @RequestMapping({"/showTotalPromotedStaffChartByMda.do"})
    public ModelAndView promotedStaffChart(@RequestParam(value = "label") String label,
                                           @RequestParam(value = "rm") int pRunMonth,
                                           @RequestParam(value = "ry") int pRunYear,
                                           Model pModel, HttpServletRequest pRequest) throws Exception {
        SessionManagerService.manageSession(pRequest, pModel);

        BusinessClient businessClient = this.genericService.loadObjectWithSingleCondition(BusinessClient.class,
                CustomPredicate.procurePredicate("chartName",label));

        List<NamedEntity> getMonthList = PayrollBeanUtils.makeAllMonthList();
        List<NamedEntity> getYearList = this.paycheckService.makePaycheckYearList(getBusinessCertificate(pRequest));

        ChartMiniBean cMB = new ChartMiniBean();

        cMB.setRunMonth(pRunMonth);
        cMB.setRunYear(pRunYear);

        String payPeriod = "";
        if (IppmsUtils.isNotNullAndGreaterThanZero(pRunMonth) && IppmsUtils.isNotNullAndGreaterThanZero(pRunYear)) {
            payPeriod = PayrollUtils.makeAuditPayPeriod(pRunMonth, pRunYear);
        } else {
            LocalDate localDate = LocalDate.now();
            payPeriod = PayrollUtils.makeAuditPayPeriod(localDate.getMonthValue(), localDate.getYear());
        }
        String month = payPeriod;

        String url = "/ogsg_ippms/showPromotionChartTable.do";
        int monthValue = Integer.parseInt(month.substring(0, 2));
        int year = Integer.parseInt(month.substring(2));
//        Month monthName = Month.of(monthValue);
        String monthName = Month.of(monthValue).getDisplayName( TextStyle.FULL , Locale.US );

        String labelForCD = "Promoted Staffs";
//        String chartTitle = "Promoted Staffs in "+label + " By MDAs ";
        String yLabelForCD = "Total Number of Promoted Staffs";

        String chartTitle="";

        if(label.equals("State CS")){
            chartTitle = "Promoted Staffs in "+label + " By MDAs (" +monthName+ ","+year+")";
        }
        else if(label.equals("Pensions")){
            chartTitle = "Promoted Staffs in "+label + " By TCO (" +monthName+ ","+year+")";
        }
        else if(label.equals("LG Pension")){
            chartTitle = "Promoted Staffs in "+label + " By TCO (" +monthName+ ","+year+")";
        }
        else if(label.equals("L.G")){
            chartTitle = "Promoted Staffs in "+label + " By Local Govt. (" +monthName+ ","+year+")";
        }
        else if (label.equals("SUBEB")){
            chartTitle = "Promoted Staffs in "+label + " By LGEA (" +monthName+ ","+year+")";
        }

        //long clientCode = businessClient.getId();

        List<MdaInfo> mdaCount = this.chartService.countPromotedStaffByMda(businessClient, month);

        List<String> mda = new ArrayList<>();
        List<String> totalStaffNumber = new ArrayList<>();

        for (MdaInfo mdaInfo : mdaCount) {
            String codeName = mdaInfo.getCodeName();
            mda.add(codeName);
            String codeNumber = String.valueOf(mdaInfo.getTotalNoOfEmployees());
            totalStaffNumber.add(codeNumber);
        }

        int mdaSize = mda.size();
        int totalStaffNumberSize = totalStaffNumber.size();


        //For Annual Salary Calculation and total staff promoted by month and year

        ChartDTO chartDTO = this.chartServiceExt.annualSalaryForPromotionAudit(businessClient, month);

        DecimalFormat df = new DecimalFormat("#,##0.00");
        String totalAnnualSalaryStr = df.format(chartDTO.getTotalAnnualSalary());


        LinkedHashMap<String, String> displayData = new LinkedHashMap<>();
        displayData.put("Table Title", "Details for all "+labelForCD +" in " +label);
        displayData.put("Total Number of Staff Promoted", String.valueOf(chartDTO.getNoOfYearsAtRetirement()));
        displayData.put("Annual Salary Implication", IConstants.naira + totalAnnualSalaryStr);
        displayData.put("Month and Year", (monthName) + ", " + (year));


        ChartController chartController = new ChartController();
        SingleChartBean singleChartBean = new SingleChartBean();
        singleChartBean.setBarXAxis(mda);
        singleChartBean.setBarXAxisSize(mdaSize);
        singleChartBean.setBarYAxis(totalStaffNumber);
        singleChartBean.setBarYAxisSize(totalStaffNumberSize);
        singleChartBean.setChartTitle(chartTitle);
        singleChartBean.setVerticalLabel(yLabelForCD);
        singleChartBean.setLabelForCD(labelForCD);
        singleChartBean.setUrl(url);
        singleChartBean.setMonthList(getMonthList);
        singleChartBean.setYearList(getYearList);
        singleChartBean.setRunMonth(monthValue);
        singleChartBean.setRunYear(year);
        return chartController.chartReport(singleChartBean, pRequest, pModel, displayData, cMB);
    }


    @RequestMapping({"/showTotalNewStaffChartByMda.do"})
    public ModelAndView newStaffChart(Model pModel, HttpServletRequest pRequest,
                                      @RequestParam(value = "label") String label,
                                      @RequestParam(value = "rm") int pRunMonth,
                                      @RequestParam(value = "ry") int pRunYear) throws Exception {

        SessionManagerService.manageSession(pRequest, pModel);

        BusinessClient businessClient = this.genericService.loadObjectWithSingleCondition(BusinessClient.class,
                CustomPredicate.procurePredicate("chartName",label));

        List<NamedEntity> getMonthList = PayrollBeanUtils.makeAllMonthList();
        List<NamedEntity> getYearList = this.paycheckService.makePaycheckYearList(getBusinessCertificate(pRequest));

        ChartMiniBean cMB = new ChartMiniBean();

        cMB.setRunMonth(pRunMonth);
        cMB.setRunYear(pRunYear);

        String payPeriod = "";
        if (IppmsUtils.isNotNullAndGreaterThanZero(pRunMonth) && IppmsUtils.isNotNullAndGreaterThanZero(pRunYear)) {
            payPeriod = PayrollUtils.makeAuditPayPeriod(pRunMonth, pRunYear);
        } else {
            LocalDate localDate = LocalDate.now();
            payPeriod = PayrollUtils.makeAuditPayPeriod(localDate.getMonthValue(), localDate.getYear());
        }
        String month = payPeriod;
        String url = "/ogsg_ippms/showNewStaffChartTable.do";
        int monthValue = Integer.parseInt(month.substring(0, 2));
//        Month monthName = Month.of(monthValue);
        String monthName = Month.of(monthValue).getDisplayName( TextStyle.FULL , Locale.US );
        int year = Integer.parseInt(month.substring(2));
        String labelForCD = "New Staffs";
//        String chartTitle = "New Staffs in "+label + " By MDAs";
        String yLabelForCD = "Total Number of New Staffs";

        String chartTitle="";

        if(label.equals("State CS")){
            chartTitle = "New Staffs in "+label + " By MDAs (" +monthName+ ","+year+")";
        }
        else if(label.equals("Pensions")){
            chartTitle = "New Staffs in "+label + " By TCO (" +monthName+ ","+year+")";
        }
        else if(label.equals("LG Pension")){
            chartTitle = "New Staffs in "+label + " By TCO (" +monthName+ ","+year+")";
        }
        else if(label.equals("L.G")){
            chartTitle = "New Staffs in "+label + " By Local Govt. (" +monthName+ ","+year+")";
        }
        else if (label.equals("SUBEB")){
            chartTitle = "New Staffs in "+label + " By LGEA (" +monthName+ ","+year+")";
        }

        long clientCode = businessClient.getId();

        List<MdaInfo> mdaCount = this.chartService.countNewStaffByMda(clientCode, month);

        List<String> mda = new ArrayList<>();
        List<String> totalStaffNumber = new ArrayList<>();

        for (MdaInfo mdaInfo : mdaCount) {
            String codeName = mdaInfo.getCodeName();
            mda.add(codeName);
            String codeNumber = String.valueOf(mdaInfo.getTotalNoOfEmployees());
            totalStaffNumber.add(codeNumber);
        }

        int mdaSize = mda.size();
        int totalStaffNumberSize = totalStaffNumber.size();


        ChartDTO chartDTO = this.chartServiceExt.annualSalaryForEmployeeAudit(clientCode, month);

        DecimalFormat df = new DecimalFormat("#,##0.00");
        String totalAnnualSalaryStr = df.format(chartDTO.getTotalAnnualSalary());


        LinkedHashMap<String, String> displayData = new LinkedHashMap<>();
        displayData.put("Table Title", "Details for all "+labelForCD +" in " +label);
        displayData.put("Total Number of New Staff", String.valueOf(chartDTO.getNoOfYearsAtRetirement()));
        displayData.put("Annual Salary Implication", IConstants.naira + totalAnnualSalaryStr);
        displayData.put("Month and Year", (monthName) + ", " + (year));


        ChartController chartController = new ChartController();
        SingleChartBean singleChartBean = new SingleChartBean();
        singleChartBean.setBarXAxis(mda);
        singleChartBean.setBarXAxisSize(mdaSize);
        singleChartBean.setBarYAxis(totalStaffNumber);
        singleChartBean.setBarYAxisSize(totalStaffNumberSize);
        singleChartBean.setChartTitle(chartTitle);
        singleChartBean.setVerticalLabel(yLabelForCD);
        singleChartBean.setLabelForCD(labelForCD);
        singleChartBean.setUrl(url);
        singleChartBean.setMonthList(getMonthList);
        singleChartBean.setYearList(getYearList);
        singleChartBean.setRunMonth(monthValue);
        singleChartBean.setRunYear(year);

        return chartController.chartReport(singleChartBean, pRequest, pModel, displayData, cMB);
    }


    @RequestMapping({"/showAbsorbedStaffChartByMda.do"})
    public ModelAndView absorbedChart(Model pModel, HttpServletRequest pRequest,
                                      @RequestParam(value = "label") String label,
                                      @RequestParam(value = "rm") int pRunMonth,
                                      @RequestParam(value = "ry") int pRunYear) throws Exception {

        SessionManagerService.manageSession(pRequest, pModel);

        BusinessClient businessClient = this.genericService.loadObjectWithSingleCondition(BusinessClient.class,
                CustomPredicate.procurePredicate("chartName",label));

        List<NamedEntity> getMonthList = PayrollBeanUtils.makeAllMonthList();
        List<NamedEntity> getYearList = this.paycheckService.makePaycheckYearList(getBusinessCertificate(pRequest));

        ChartMiniBean cMB = new ChartMiniBean();

        cMB.setRunMonth(pRunMonth);
        cMB.setRunYear(pRunYear);

        String payPeriod = "";
        if (IppmsUtils.isNotNullAndGreaterThanZero(pRunMonth) && IppmsUtils.isNotNullAndGreaterThanZero(pRunYear)) {
            payPeriod = PayrollUtils.makeAuditPayPeriod(pRunMonth, pRunYear);
        } else {
            LocalDate localDate = LocalDate.now();
            payPeriod = PayrollUtils.makeAuditPayPeriod(localDate.getMonthValue(), localDate.getYear());
        }
        String month = payPeriod;
        String url = "/ogsg_ippms/showAbsorbedChartTable.do";
        int monthValue = Integer.parseInt(month.substring(0, 2));
//        Month monthName = Month.of(monthValue);
        String monthName = Month.of(monthValue).getDisplayName( TextStyle.FULL , Locale.US );
        int year = Integer.parseInt(month.substring(2));
        String labelForCD = "Absorbed Staffs";
//        String chartTitle = "Absorbed Staffs in "+label+ "  By MDAs";
        String yLabelForCD = "Total Number of Absorbed Staffs";

        String chartTitle="";

        if(label.equals("State CS")){
            chartTitle = "Absorbed Staffs in "+label + " By MDAs (" +monthName+ ","+year+")";
        }
        else if(label.equals("Pensions")){
            chartTitle = "Absorbed Staffs in "+label + " By TCO (" +monthName+ ","+year+")";
        }
        else if(label.equals("LG Pension")){
            chartTitle = "Absorbed Staffs in "+label + " By TCO (" +monthName+ ","+year+")";
        }
        else if(label.equals("L.G")){
            chartTitle = "Absorbed Staffs in "+label + " By Local Govt. (" +monthName+ ","+year+")";
        }
        else if (label.equals("SUBEB")){
            chartTitle = "Absorbed Staffs in "+label + " By LGEA (" +monthName+ ","+year+")";
        }

        long clientCode = businessClient.getId();

        List<MdaInfo> mdaCount = this.chartService.countAbsorbedStaffByMda(clientCode, month);

        List<String> mda = new ArrayList<>();
        List<String> totalStaffNumber = new ArrayList<>();

        for (MdaInfo mdaInfo : mdaCount) {
            String codeName = mdaInfo.getCodeName();
            mda.add(codeName);
            String codeNumber = String.valueOf(mdaInfo.getTotalNoOfEmployees());
            totalStaffNumber.add(codeNumber);
        }

        int mdaSize = mda.size();
        int totalStaffNumberSize = totalStaffNumber.size();

        ChartDTO chartDTO = this.chartServiceExt.annualSalaryForAbsorptionLog(clientCode, month);

        DecimalFormat df = new DecimalFormat("#,##0.00");
        String totalAnnualSalaryStr = df.format(chartDTO.getTotalAnnualSalary());

        LinkedHashMap<String, String> displayData = new LinkedHashMap<>();
        displayData.put("Table Title", "Details for all "+labelForCD +" in " +label);
        displayData.put("Total Number of New Staff", String.valueOf(chartDTO.getNoOfYearsAtRetirement()));
        displayData.put("Annual Salary Implication", IConstants.naira + totalAnnualSalaryStr);
        System.out.println("Confirming "+chartDTO.getNoOfYearsAtRetirement()+" "+ df.format(chartDTO.getTotalAnnualSalary()));
        displayData.put("Month and Year", (monthName) + ", " + (year));


        ChartController chartController = new ChartController();
        SingleChartBean singleChartBean = new SingleChartBean();
        singleChartBean.setBarXAxis(mda);
        singleChartBean.setBarXAxisSize(mdaSize);
        singleChartBean.setBarYAxis(totalStaffNumber);
        singleChartBean.setBarYAxisSize(totalStaffNumberSize);
        singleChartBean.setChartTitle(chartTitle);
        singleChartBean.setVerticalLabel(yLabelForCD);
        singleChartBean.setLabelForCD(labelForCD);
        singleChartBean.setUrl(url);
        singleChartBean.setMonthList(getMonthList);
        singleChartBean.setYearList(getYearList);
        singleChartBean.setRunMonth(monthValue);
        singleChartBean.setRunYear(year);

        return chartController.chartReport(singleChartBean, pRequest, pModel, displayData, cMB);
    }

    @RequestMapping({"/showReinstatedStaffChartByMda.do"})
    public ModelAndView reinstatedChart(Model pModel, HttpServletRequest pRequest,
                                      @RequestParam(value = "label") String label,
                                      @RequestParam(value = "rm") int pRunMonth,
                                      @RequestParam(value = "ry") int pRunYear) throws Exception {

        SessionManagerService.manageSession(pRequest, pModel);

        BusinessClient businessClient = this.genericService.loadObjectWithSingleCondition(BusinessClient.class,
                CustomPredicate.procurePredicate("chartName",label));


        List<NamedEntity> getMonthList = PayrollBeanUtils.makeAllMonthList();
        List<NamedEntity> getYearList = this.paycheckService.makePaycheckYearList(getBusinessCertificate(pRequest));

        ChartMiniBean cMB = new ChartMiniBean();

        cMB.setRunMonth(pRunMonth);
        cMB.setRunYear(pRunYear);

        String payPeriod = "";
        if (IppmsUtils.isNotNullAndGreaterThanZero(pRunMonth) && IppmsUtils.isNotNullAndGreaterThanZero(pRunYear)) {
            payPeriod = PayrollUtils.makeAuditPayPeriod(pRunMonth, pRunYear);
        } else {
            LocalDate localDate = LocalDate.now();
            payPeriod = PayrollUtils.makeAuditPayPeriod(localDate.getMonthValue(), localDate.getYear());
        }
        String month = payPeriod;
        String url = "/ogsg_ippms/showReinstatedChartTable.do";
        int monthValue = Integer.parseInt(month.substring(0, 2));
//        Month monthName = Month.of(monthValue);
        String monthName = Month.of(monthValue).getDisplayName( TextStyle.FULL , Locale.US );
        int year = Integer.parseInt(month.substring(2));
        String labelForCD = "Reinstated Staffs";
//        String chartTitle = "Reinstated Staffs in " +label+" By MDAs";
        String yLabelForCD = "Total Number of Reinstated Staffs";

        String chartTitle="";

        if(label.equals("State CS")){
            chartTitle = "Reinstated Staffs in "+label + " By MDAs (" +monthName+ ","+year+")";
        }
        else if(label.equals("Pensions")){
            chartTitle = "Reinstated Staffs in "+label + " By TCO (" +monthName+ ","+year+")";
        }
        else if(label.equals("LG Pension")){
            chartTitle = "Reinstated Staffs in "+label + " By TCO (" +monthName+ ","+year+")";
        }
        else if(label.equals("L.G")){
            chartTitle = "Reinstated Staffs in "+label + " By Local Govt. (" +monthName+ ","+year+")";
        }
        else if (label.equals("SUBEB")){
            chartTitle = "Reinstated Staffs in "+label + " By LGEA (" +monthName+ ","+year+")";
        }

        long clientCode = businessClient.getId();

        List<MdaInfo> mdaCount = this.chartService.countReinstatedStaffByMda(clientCode, month);

        List<String> mda = new ArrayList<>();
        List<String> totalStaffNumber = new ArrayList<>();

        for (MdaInfo mdaInfo : mdaCount) {
            String codeName = mdaInfo.getCodeName();
            mda.add(codeName);
            String codeNumber = String.valueOf(mdaInfo.getTotalNoOfEmployees());
            totalStaffNumber.add(codeNumber);
        }

        int mdaSize = mda.size();
        int totalStaffNumberSize = totalStaffNumber.size();

        ChartDTO chartDTO = this.chartServiceExt.annualSalaryForReinstatedLog(clientCode, month);

        DecimalFormat df = new DecimalFormat("#,##0.00");
        String totalAnnualSalaryStr = df.format(chartDTO.getTotalAnnualSalary());

        LinkedHashMap<String, String> displayData = new LinkedHashMap<>();
        displayData.put("Table Title", "Details for all "+labelForCD +" in " +label);
        displayData.put("Total Number of New Staff", String.valueOf(chartDTO.getNoOfYearsAtRetirement()));
        displayData.put("Annual Salary Implication", IConstants.naira + totalAnnualSalaryStr);
        displayData.put("Month and Year", (monthName) + ", " + (year));


        ChartController chartController = new ChartController();
        SingleChartBean singleChartBean = new SingleChartBean();
        singleChartBean.setBarXAxis(mda);
        singleChartBean.setBarXAxisSize(mdaSize);
        singleChartBean.setBarYAxis(totalStaffNumber);
        singleChartBean.setBarYAxisSize(totalStaffNumberSize);
        singleChartBean.setChartTitle(chartTitle);
        singleChartBean.setVerticalLabel(yLabelForCD);
        singleChartBean.setLabelForCD(labelForCD);
        singleChartBean.setUrl(url);
        singleChartBean.setMonthList(getMonthList);
        singleChartBean.setYearList(getYearList);
        singleChartBean.setRunMonth(monthValue);
        singleChartBean.setRunYear(year);

        return chartController.chartReport(singleChartBean, pRequest, pModel, displayData, cMB);
    }

    @RequestMapping({"/showPaycheckByMda.do"})
    public ModelAndView payCheckByMda(Model pModel, HttpServletRequest pRequest,
                                        @RequestParam(value = "label") String label,
                                        @RequestParam(value = "rm") int pRunMonth,
                                        @RequestParam(value = "ry") int pRunYear) throws Exception {

        SessionManagerService.manageSession(pRequest, pModel);

        BusinessClient bc = this.genericService.loadObjectWithSingleCondition(BusinessClient.class,
                CustomPredicate.procurePredicate("chartName",label));


        List<NamedEntity> getMonthList = PayrollBeanUtils.makeAllMonthList();
        List<NamedEntity> getYearList = this.paycheckService.makePaycheckYearList(getBusinessCertificate(pRequest));

        ChartMiniBean cMB = new ChartMiniBean();

        cMB.setRunMonth(pRunMonth);
        cMB.setRunYear(pRunYear);

        LocalDate payCheckDate;

        if (IppmsUtils.isNotNullAndGreaterThanZero(pRunMonth) && IppmsUtils.isNotNullAndGreaterThanZero(pRunYear)) {
            LocalDate sDate = LocalDate.of(pRunYear, pRunMonth, 1);
            payCheckDate = sDate.withDayOfMonth(sDate.lengthOfMonth());

        } else {
            LocalDate localDate = LocalDate.now();
            payCheckDate = localDate.withDayOfMonth(localDate.lengthOfMonth());
        }

        int monthValue = payCheckDate.getMonthValue();
        int year = payCheckDate.getYear();

//        Month monthName = Month.of(pRunMonth);
        String monthName = Month.of(monthValue).getDisplayName( TextStyle.FULL , Locale.US );

        String url = "/ogsg_ippms/showPaycheckChartTable.do";

        String labelForCD = "paycheck Information";
//        String chartTitle = "Paycheck Information in "+label+ " By MDAs";
        String yLabelForCD = "Total Amount of Paycheck";

        String chartTitle="";

        if(label.equals("State CS")){
            chartTitle = "Promoted Staffs in "+label + " By MDAs (" +monthName+ ","+year+")";
        }
        else if(label.equals("Pensions")){
            chartTitle = "Promoted Staffs in "+label + " By TCO (" +monthName+ ","+year+")";
        }
        else if(label.equals("LG Pension")){
            chartTitle = "Promoted Staffs in "+label + " By TCO (" +monthName+ ","+year+")";
        }
        else if(label.equals("L.G")){
            chartTitle = "Promoted Staffs in "+label + " By Local Govt. (" +monthName+ ","+year+")";
        }
        else if (label.equals("SUBEB")){
            chartTitle = "Promoted Staffs in "+label + " By LGEA (" +monthName+ ","+year+")";
        }

        List<MdaInfo> payCheckByMda = this.chartService.sumTotalPayByMda(bc, payCheckDate);

        List<String> mdaName = new ArrayList<>();
        List<String> payCheckStr = new ArrayList<>();
        List <Double> payCheck = new ArrayList<>();

        for (MdaInfo mdaInfo : payCheckByMda) {
            String mda = mdaInfo.getCodeName();
            mdaName.add(mda);
            Double pay = mdaInfo.getTotalGrossPay();
            payCheck.add(pay);
            payCheckStr.add(String.valueOf(pay));
        }

        int mdaNameSize = mdaName.size();
        int payCheckStrSize = payCheckStr.size();

        Double sum = 0.0;
        for(Double i: payCheck){
            sum += i;
        }

        DecimalFormat df = new DecimalFormat("#,##0.00");
        String sumStr = df.format(sum);

        ChartController chartController = new ChartController();
        SingleChartBean singleChartBean = new SingleChartBean();
        singleChartBean.setBarXAxis(mdaName);
        singleChartBean.setBarXAxisSize(mdaNameSize);
        singleChartBean.setBarYAxis(payCheckStr);
        singleChartBean.setBarYAxisSize(payCheckStrSize);
        singleChartBean.setChartTitle(chartTitle);
        singleChartBean.setVerticalLabel(yLabelForCD);
        singleChartBean.setLabelForCD(labelForCD);
        singleChartBean.setUrl(url);
        singleChartBean.setMonthList(getMonthList);
        singleChartBean.setYearList(getYearList);
        singleChartBean.setRunMonth(monthValue);
        singleChartBean.setRunYear(year);


        LinkedHashMap<String, String> displayData = new LinkedHashMap<>();
        displayData.put("Table Title", "Details for all "+labelForCD +" in " +label);
        displayData.put("Total Salary Implication", IConstants.naira + sumStr);
        displayData.put("Month and Year", (monthName) + ", " + (pRunYear));


        return chartController.chartReport(singleChartBean, pRequest, pModel, displayData, cMB);

    }


    @RequestMapping({"/showSpecAllowByMda.do"})
    public ModelAndView specAllowByMda(Model pModel, HttpServletRequest pRequest,
                                      @RequestParam(value = "label") String label,
                                      @RequestParam(value = "rm") int pRunMonth,
                                      @RequestParam(value = "ry") int pRunYear) throws Exception {

        SessionManagerService.manageSession(pRequest, pModel);

        BusinessClient bc = this.genericService.loadObjectWithSingleCondition(BusinessClient.class,
                CustomPredicate.procurePredicate("chartName", label));

        List<NamedEntity> getMonthList = PayrollBeanUtils.makeAllMonthList();
        List<NamedEntity> getYearList = this.paycheckService.makePaycheckYearList(getBusinessCertificate(pRequest));

        ChartMiniBean cMB = new ChartMiniBean();

        cMB.setRunMonth(pRunMonth);
        cMB.setRunYear(pRunYear);

        LocalDate payCheckDate;

        if (IppmsUtils.isNotNullAndGreaterThanZero(pRunYear) && IppmsUtils.isNotNullAndGreaterThanZero(pRunYear)) {
            LocalDate sDate = LocalDate.of(pRunYear, pRunMonth, 1);
            payCheckDate = sDate.withDayOfMonth(sDate.lengthOfMonth());

        } else {
            LocalDate localDate = LocalDate.now();
            payCheckDate = localDate.withDayOfMonth(localDate.lengthOfMonth());
        }

        int monthValue = payCheckDate.getMonthValue();
        int year = payCheckDate.getYear();

//        Month monthName = Month.of(pRunMonth);
        String monthName = Month.of(monthValue).getDisplayName( TextStyle.FULL , Locale.US );

        String url = "/ogsg_ippms/showSpecAllowChartTable.do";

        String labelForCD = "Special Allowance Information";
//        String chartTitle = "Special Allowance in "+ " By MDAs";
        String yLabelForCD = "Total Amount of Special Allowance";

        String chartTitle="";

        if(label.equals("State CS")){
            chartTitle = "Promoted Staffs in "+label + " By MDAs (" +monthName+ ","+year+")";
        }
        else if(label.equals("Pensions")){
            chartTitle = "Promoted Staffs in "+label + " By TCO (" +monthName+ ","+year+")";
        }
        else if(label.equals("LG Pension")){
            chartTitle = "Promoted Staffs in "+label + " By TCO (" +monthName+ ","+year+")";
        }
        else if(label.equals("L.G")){
            chartTitle = "Promoted Staffs in "+label + " By Local Govt. (" +monthName+ ","+year+")";
        }
        else if (label.equals("SUBEB")){
            chartTitle = "Promoted Staffs in "+label + " By LGEA (" +monthName+ ","+year+")";
        }

        List<MdaInfo> specAllowByMda = this.chartService.sumSpecAllowByMda(bc, payCheckDate);

        List<String> mdaName = new ArrayList<>();
        List<String> specAllowStr = new ArrayList<>();
        List<Double> specAllow = new ArrayList<>();

        for (MdaInfo mdaInfo : specAllowByMda) {
            String mda = mdaInfo.getCodeName();
            mdaName.add(mda);
            Double pay = mdaInfo.getMonthlyBasic();
            specAllow.add(pay);
            specAllowStr.add(String.valueOf(pay));
        }

        int mdaNameSize = mdaName.size();
        int specAllowStrSize = specAllowStr.size();

        Double sum = 0.0;
        for (Double i : specAllow) {
            sum += i;
        }
        DecimalFormat df = new DecimalFormat("#,##0.00");
        String sumStr = df.format(sum);


        ChartController chartController = new ChartController();
        SingleChartBean singleChartBean = new SingleChartBean();
        singleChartBean.setBarXAxis(mdaName);
        singleChartBean.setBarXAxisSize(mdaNameSize);
        singleChartBean.setBarYAxis(specAllowStr);
        singleChartBean.setBarYAxisSize(specAllowStrSize);
        singleChartBean.setChartTitle(chartTitle);
        singleChartBean.setVerticalLabel(yLabelForCD);
        singleChartBean.setLabelForCD(labelForCD);
        singleChartBean.setUrl(url);
        singleChartBean.setMonthList(getMonthList);
        singleChartBean.setYearList(getYearList);
        singleChartBean.setRunMonth(monthValue);
        singleChartBean.setRunYear(year);


        LinkedHashMap<String, String> displayData = new LinkedHashMap<>();
        displayData.put("Table Title", "Details for all "+labelForCD +" in " +label);
        displayData.put("Total Salary Implication", IConstants.naira + sumStr);
        displayData.put("Month and Year", monthName + ", " + pRunYear);


        return chartController.chartReport(singleChartBean, pRequest, pModel, displayData, cMB);

    }

    @RequestMapping({"/showAllStaffChartByMda.do"})
    public ModelAndView TotalStaffChart(@RequestParam(value = "label") String label,
                                        @RequestParam(value = "rm") int pRunMonth,
                                        @RequestParam(value = "ry") int pRunYear,
                                        Model pModel, HttpServletRequest pRequest) throws Exception {

        SessionManagerService.manageSession(pRequest, pModel);

        BusinessClient businessClient = this.genericService.loadObjectWithSingleCondition(BusinessClient.class,
                CustomPredicate.procurePredicate("chartName",label));

        List<NamedEntity> getMonthList = PayrollBeanUtils.makeAllMonthList();
        List<NamedEntity> getYearList = this.paycheckService.makePaycheckYearList(getBusinessCertificate(pRequest));

        ChartMiniBean cMB = new ChartMiniBean();

        cMB.setRunMonth(pRunMonth);
        cMB.setRunYear(pRunYear);

        LocalDate payCheckDate;

        if (IppmsUtils.isNotNullAndGreaterThanZero(pRunYear) && IppmsUtils.isNotNullAndGreaterThanZero(pRunYear)) {
            LocalDate sDate = LocalDate.of(pRunYear, pRunMonth, 1);
            payCheckDate = sDate.withDayOfMonth(sDate.lengthOfMonth());

        } else {
            LocalDate localDate = LocalDate.now();
            payCheckDate = localDate.withDayOfMonth(localDate.lengthOfMonth());
        }

        int monthValue = payCheckDate.getMonthValue();
        int year = payCheckDate.getYear();

//        Month monthName = Month.of(pRunMonth);
        String monthName = Month.of(monthValue).getDisplayName( TextStyle.FULL , Locale.US );

        String labelForCD = "Total Staffs";
//        String chartTitle = "Total Staffs in " +label+ " By MDAs";
        String yLabelForCD = "Total Number of Staffs";
        String url = "/ogsg_ippms/showMdaChartTable.do";

        String chartTitle="";

        if(label.equals("State CS")){
            chartTitle = "Staffs in "+label + " By MDAs (" +monthName+ ","+year+")";
        }
        else if(label.equals("Pensions")){
            chartTitle = "Staffs in "+label + " By TCO (" +monthName+ ","+year+")";
        }
        else if(label.equals("LG Pension")){
            chartTitle = "Staffs in "+label + " By TCO (" +monthName+ ","+year+")";
        }
        else if(label.equals("L.G")){
            chartTitle = "Staffs in "+label + " By Local Govt. (" +monthName+ ","+year+")";
        }
        else if (label.equals("SUBEB")){
            chartTitle = "Staffs in "+label + " By LGEA (" +monthName+ ","+year+")";
        }

        long clientCode = businessClient.getId();

        List<MdaType> staffCount = this.chartService.countStaffByMda(clientCode);

        List<String> mdaTypeNameList = new ArrayList<>();
        List<String> mdaTypeCountList = new ArrayList<>();

        for(MdaType e: staffCount){
            String mdaTypeName = e.getName();
            long mdaTypeCount = e.getId();
            mdaTypeNameList.add(mdaTypeName);
            mdaTypeCountList.add(String.valueOf(mdaTypeCount));
        }

        ChartController chartController = new ChartController();
        SingleChartBean singleChartBean = new SingleChartBean();
        singleChartBean.setBarXAxis(mdaTypeNameList);
        singleChartBean.setBarXAxisSize(mdaTypeNameList.size());
        singleChartBean.setBarYAxis(mdaTypeCountList);
        singleChartBean.setBarYAxisSize(mdaTypeCountList.size());
        singleChartBean.setChartTitle(chartTitle);
        singleChartBean.setVerticalLabel(yLabelForCD);
        singleChartBean.setLabelForCD(labelForCD);
        singleChartBean.setUrl(url);
        singleChartBean.setMonthList(getMonthList);
        singleChartBean.setYearList(getYearList);
        singleChartBean.setRunMonth(monthValue);
        singleChartBean.setRunYear(year);

        LinkedHashMap<String, String> displayData = new LinkedHashMap<>();

        return chartController.chartReport(singleChartBean, pRequest, pModel, displayData, cMB);
    }
}