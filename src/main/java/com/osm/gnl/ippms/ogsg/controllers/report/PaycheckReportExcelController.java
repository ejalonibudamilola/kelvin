package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.audit.domain.AbstractPromotionAuditEntity;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaySlipService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.BusinessCertificateCreator;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.PayRecordExcelGenerator;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SimpleExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SinglePaySlipPdfGenerator;
import com.osm.gnl.ippms.ogsg.domain.beans.EmployeeHrBean;
import com.osm.gnl.ippms.ogsg.domain.beans.PRCDeductionsBeanHolder;
import com.osm.gnl.ippms.ogsg.domain.beans.PRCDisplayBeanHolder;
import com.osm.gnl.ippms.ogsg.domain.beans.PayRecordBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductionMiniBean;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBeanInactive;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.exception.NoBusinessCertificationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckDeduction;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckGarnishment;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmployeePayMiniBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller
public class PaycheckReportExcelController extends BaseController {


    private final PaycheckService paycheckService;
    private final PaySlipService paySlipService;


    @Autowired
    public PaycheckReportExcelController(PaycheckService paycheckService, PaySlipService paySlipService) {
        this.paycheckService = paycheckService;
        this.paySlipService = paySlipService;
    }

    @RequestMapping({"/employeesPaycheckHistoryExcel.do"})
    public void setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        ReportGeneratorBean rt = new ReportGeneratorBean();
        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();
        PaginationBean paginationBean = getPaginationInfo(request);
        int wNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder()
                .addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()))
                .addPredicate(CustomPredicate.procurePredicate("employee.id", pEmpId)), IppmsUtils.getPaycheckClass(bc));

        List<AbstractPaycheckEntity> wPromoHist = null;
        if (wNoOfElements > 0) {

            wPromoHist = (List<AbstractPaycheckEntity>) this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckClass(bc), Arrays.asList(
                    CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                    CustomPredicate.procurePredicate("employee.id", pEmpId)), "lastName");
            //Collections.sort(wPromoHist);
        } else {
            wPromoHist = new ArrayList<>();
        }

        HiringInfo wHireInfo = loadHiringInfoByEmpId(request, bc, pEmpId);

        PaginatedPaycheckGarnDedBeanHolder wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wPromoHist, paginationBean.getPageNumber(), pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        wPGBDH.setDisplayTitle(wHireInfo.getAbstractEmployeeEntity().getEmployeeId());
        wPGBDH.setId(wHireInfo.getAbstractEmployeeEntity().getId());
        wPGBDH.setEmployeeId(String.valueOf(pEmpId));
        wPGBDH.setName(wHireInfo.getAbstractEmployeeEntity().getTitle().getName() + " " + wHireInfo.getAbstractEmployeeEntity().getDisplayName());
        wPGBDH.setMode(wHireInfo.getAbstractEmployeeEntity().getParentObjectName());
        wPGBDH.setCurrentLevelAndStep(wHireInfo.getAbstractEmployeeEntity().getSalaryInfo().getSalaryType().getName() + " " + wHireInfo.getAbstractEmployeeEntity().getSalaryInfo().getLevelAndStepAsStr());


        List<Map<String, Object>> contPenMapped = new ArrayList<>();
        for (AbstractPaycheckEntity data : wPromoHist) {
            Map<String, Object> newData = new HashMap<>();
            newData.put("Pay Period", data.getPayPeriodStr());
            newData.put("Total Pay", data.getTotalPay());
            newData.put("Total Deduction", data.getTotalDeductions());
            newData.put("Total Garnishments", data.getTotalGarnishments());
            newData.put("Special Allowance", data.getSpecialAllowance());
            newData.put("Taxes Paid", data.getTaxesPaid());
            newData.put("Net Pay", data.getNetPay());
            contPenMapped.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean("Pay Period", 0));
        tableHeaders.add(new ReportGeneratorBean("Total Pay", 2));
        tableHeaders.add(new ReportGeneratorBean("Total Deduction", 2));
        tableHeaders.add(new ReportGeneratorBean("Total Garnishments", 2));
        tableHeaders.add(new ReportGeneratorBean("Special Allowance", 2));
        tableHeaders.add(new ReportGeneratorBean("Taxes Paid", 2));
        tableHeaders.add(new ReportGeneratorBean("Net Pay", 2));

        List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : tableHeaders) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherSpecHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add("Employee Paycheck History");
        mainHeaders.add("Employee - " + wPGBDH.getName());
        mainHeaders.add("Organization - " + wPGBDH.getMode());
        mainHeaders.add("Current Level And Step - " + wPGBDH.getCurrentLevelAndStep());
        mainHeaders.add("No Of Years In Service - " + wPGBDH.getNoOfYearsInService());

        rt.setBusinessCertificate(bc);
        rt.setGroupBy(null);
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(OtherSpecHeaders);
        rt.setReportTitle("Employee Paycheck History");
        rt.setTableType(0);
        rt.setTotalInd(1);


        simpleExcelReportGenerator.getExcel(response, request, rt);
    }


    @RequestMapping(value = "paySlipPdf.do", params={"rm","ry","empId"})
    public void PaySlipPdf(@RequestParam("rm") int runMonth, @RequestParam("ry")	int runYear, @RequestParam("empId") Long pEmpId,
                                   HttpServletRequest request, HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException
    {
        SessionManagerService.manageSession(request, null);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        IPaycheckGenerator paycheckGenerator = null;
        if(bc.isPensioner())
            paycheckGenerator = new PensionPaycheckGenerator();
        else
            paycheckGenerator = new PaycheckGenerator();
//        GenericService genericService = new GenericService();
        try
        {
            EmployeePayMiniBean empPayMiniBean = new EmployeePayMiniBean();
            empPayMiniBean.setParentInstId(pEmpId);
            empPayMiniBean.setAdmin(bc.isSuperAdmin());

            AbstractPaycheckEntity employeePayBean = (AbstractPaycheckEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc), Arrays.asList(
                    CustomPredicate.procurePredicate("employee.id", pEmpId), CustomPredicate.procurePredicate("runMonth", runMonth), CustomPredicate.procurePredicate("runYear",
                            runYear), getBusinessClientIdPredicate(request)));


            employeePayBean.setHiringInfo((this.genericService.loadObjectUsingRestriction(HiringInfo.class, Arrays.asList(
                    CustomPredicate.procurePredicate("employee.id", pEmpId), getBusinessClientIdPredicate(request)))));

            empPayMiniBean = (EmployeePayMiniBean) paycheckGenerator.generatePaySlipModel(empPayMiniBean, employeePayBean, this.genericService, bc,null,loadConfigurationBean(request), paySlipService);
            List<EmployeePayMiniBean> wList = new ArrayList<>();
            wList.add(empPayMiniBean);

            EmployeeHrBean eHRBean = new EmployeeHrBean();
            eHRBean.setParentObjectList(wList);

            //SinglePaySlipPdfGenerator singlePaySlipPdfGenerator = new SinglePaySlipPdfGenerator();

            rt.setReportTitle("single_paySlip");
            rt.setBusinessCertificate(bc);
            rt.setEmployeePayMiniBean(empPayMiniBean);
            rt.setWatermark(bc.getBusinessName()+ " PaySlip");
            rt.setOutputInd(true);
            SinglePaySlipPdfGenerator.getInstance().getPdf(response, rt,request);
        }

        catch (Exception wEx)
        {
            wEx.printStackTrace();

        }
    }



    @RequestMapping({"/payRecordCardExcel.do"})
    public void setupForm(@RequestParam("eid") Long pEmpId, Model pModel,HttpServletResponse response, HttpServletRequest pRequest) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException, IOException {
        SessionManagerService.manageSession(pRequest, pModel);

        PayRecordExcelGenerator payRecordExcelGenerator = new PayRecordExcelGenerator();

        ReportGeneratorBean rt = new ReportGeneratorBean();

        BusinessCertificate bc = getBusinessCertificate(pRequest);
        HiringInfo h = this.genericService.loadObjectWithSingleCondition(HiringInfo.class,CustomPredicate.procurePredicate("employee.id",pEmpId));

        PayRecordBean wPayRecordBean = new PayRecordBean();

        AbstractPaycheckEntity wLastPaycheck = this.paycheckService.loadLastNoneZeroPaycheckForEmployee(bc, pEmpId);

//        List<PRCDisplayBeanHolder> wLastPayYear = this.payrollServiceExt.loadEmployeePayBeanByYear(wLastPaycheck.getRunYear(), pEmpId);
        List<PRCDisplayBeanHolder> wLastPayYear = new ArrayList<PRCDisplayBeanHolder>();
        List<EmployeePayBean> wRetVal = (List<EmployeePayBean>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckClass(bc), Arrays.asList(
                CustomPredicate.procurePredicate("runYear", wLastPaycheck.getRunYear()), CustomPredicate.procurePredicate("employee.id", pEmpId)), "id");
        if (IppmsUtils.isNotNullOrEmpty(wRetVal)) {
        for(EmployeePayBean e : wRetVal) {
            List<PaycheckGarnishment> wList = this.paycheckService.loadPaycheckGarnishmentsByPaycheckId(e.getId(), bc);
            if (!wList.isEmpty()) {
                Collections.sort(wList);
                e.setGarnishList(wList);
            }
            List<PaycheckDeduction> wList2 = this.paycheckService.loadPaycheckDeductionsByPaycheckId(e.getId(), bc);
            if (!wList2.isEmpty()) {
                Collections.sort(wList2);
                e.setDeductionList(wList2);
            }
            PRCDisplayBeanHolder p = new PRCDisplayBeanHolder();
            p.setEmployeePayBean(e);
            p.setMonth(e.getRunMonth());
            p.setYear(e.getRunYear());
            wLastPayYear.add(p);
            }
        }

        wPayRecordBean.setUltimateBean(wLastPayYear);

        wPayRecordBean.setUltimateYear(wLastPaycheck.getRunYear());


        List<PaycheckGarnishment> wGarnishments =  (List<PaycheckGarnishment>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckGarnishmentClass(bc),
                Arrays.asList(CustomPredicate.procurePredicate("runYear", wLastPaycheck.getRunYear()), CustomPredicate.procurePredicate("employee.id", pEmpId)), "id");

        List<PaycheckDeduction> wDeductions = (List<PaycheckDeduction>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckDeductionClass(bc),
                Arrays.asList(CustomPredicate.procurePredicate("runYear", wLastPaycheck.getRunYear()), CustomPredicate.procurePredicate("id", pEmpId)), "id");

        wPayRecordBean.setUltimateDeductions(makeDeductions(wGarnishments, wDeductions));


        List<EmployeePayBean> wRetVal2 = (List<EmployeePayBean>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckClass(bc), Arrays.asList(
                CustomPredicate.procurePredicate("runMonth", wLastPaycheck.getRunYear() - 1), CustomPredicate.procurePredicate("employee.id", pEmpId)), "id");
        if (IppmsUtils.isNotNullOrEmpty(wRetVal2)) {
            for(EmployeePayBean e : wRetVal2) {
                List<PaycheckGarnishment> wList = this.paycheckService.loadPaycheckGarnishmentsByPaycheckId(e.getId(), bc);
                if (!wList.isEmpty()) {
                    Collections.sort(wList);
                    e.setGarnishList(wList);
                }
                List<PaycheckDeduction> wList2 = this.paycheckService.loadPaycheckDeductionsByPaycheckId(e.getId(), bc);
                if (!wList2.isEmpty()) {
                    Collections.sort(wList2);
                    e.setDeductionList(wList2);
                }
                PRCDisplayBeanHolder p = new PRCDisplayBeanHolder();
                p.setEmployeePayBean(e);
                p.setMonth(e.getRunMonth());
                p.setYear(e.getRunYear());
                wLastPayYear.add(p);
            }
        }

//        wGarnishments = this.paycheckService.loadEmployeeGarnishmentsByYear(pEmpId, wLastPaycheck.getRunYear() - 1);

        wGarnishments = (List<PaycheckGarnishment>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckGarnishmentClass(bc), Arrays.asList(
                CustomPredicate.procurePredicate("runYear", wLastPaycheck.getRunYear() - 1), CustomPredicate.procurePredicate("employee.id", pEmpId)), "id");

        wDeductions = (List<PaycheckDeduction>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckDeductionClass(bc),
                Arrays.asList(CustomPredicate.procurePredicate("runYear", wLastPaycheck.getRunYear()), CustomPredicate.procurePredicate("id", pEmpId)), "id");

        wPayRecordBean.setPenultimateDeductions(makeDeductions(wGarnishments, wDeductions));

        wPayRecordBean.setPenultimateBean(wLastPayYear);

        wPayRecordBean.setPenultimateYear(wLastPaycheck.getRunYear() - 1);

        List<EmployeePayBean> wRetVal3 = (List<EmployeePayBean>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckClass(bc), Arrays.asList(
                CustomPredicate.procurePredicate("runMonth", wLastPaycheck.getRunYear() - 2), CustomPredicate.procurePredicate("employee.id", pEmpId)), "id");
        if (IppmsUtils.isNotNullOrEmpty(wRetVal3)) {
            for(EmployeePayBean e : wRetVal3) {
                List<PaycheckGarnishment> wList = this.paycheckService.loadPaycheckGarnishmentsByPaycheckId(e.getId(), bc);
                if (!wList.isEmpty()) {
                    Collections.sort(wList);
                    e.setGarnishList(wList);
                }
                List<PaycheckDeduction> wList2 = this.paycheckService.loadPaycheckDeductionsByPaycheckId(e.getId(), bc);
                if (!wList2.isEmpty()) {
                    Collections.sort(wList2);
                    e.setDeductionList(wList2);
                }
                PRCDisplayBeanHolder p = new PRCDisplayBeanHolder();
                p.setEmployeePayBean(e);
                p.setMonth(e.getRunMonth());
                p.setYear(e.getRunYear());
                wLastPayYear.add(p);
            }
        }

        wPayRecordBean.setBaseYearBean(wLastPayYear);
        wPayRecordBean.setBaseYear(wLastPaycheck.getRunYear() - 2);


        wGarnishments = (List<PaycheckGarnishment>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckGarnishmentClass(bc), Arrays.asList(
                CustomPredicate.procurePredicate("runYear", wLastPaycheck.getRunYear() - 2), CustomPredicate.procurePredicate("employee.id", pEmpId)), "id");


        wDeductions = (List<PaycheckDeduction>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckDeductionClass(bc),
                Arrays.asList(CustomPredicate.procurePredicate("runYear", wLastPaycheck.getRunYear() - 2), CustomPredicate.procurePredicate("id", pEmpId)), "id");


        wPayRecordBean.setBaseYearDeductions(makeDeductions(wGarnishments, wDeductions));

        wPayRecordBean.setEmployeeName(PayrollHRUtils.createDisplayName(h.getEmployee().getLastName(), h.getEmployee().getFirstName(), h.getEmployee().getInitials()));
        wPayRecordBean.setEmployeeId(h.getEmployee().getEmployeeId());
        wPayRecordBean.setOrganization(h.getEmployee().getAssignedToObject());

        wPayRecordBean.setBirthDateStr(PayrollHRUtils.getDisplayDateFormat().format(h.getBirthDate()));
        wPayRecordBean.setHireDateStr(PayrollHRUtils.getDisplayDateFormat().format(h.getHireDate()));
        wPayRecordBean.setNoOfYearsInService(wLastPaycheck.getRunYear() - h.getHireDate().getYear());

        wPayRecordBean.setSalaryScaleLevelAndStep(h.getEmployee().getSalaryInfo().getSalaryType().getName() + " - " + h.getEmployee().getSalaryInfo().getLevelAndStepAsStr());


        //Send Report Date TO Excel Generator
        rt.setPayBean(wPayRecordBean);
        rt.setReportTitle("PayRecordExcel");
        rt.setBusinessCertificate(bc);
        payRecordExcelGenerator.getExcel(response, pRequest, rt);
    }

    private List<PRCDeductionsBeanHolder> makeDeductions(List<PaycheckGarnishment> pGarnishments, List<PaycheckDeduction> pDeductions)
    {
        ArrayList<PRCDeductionsBeanHolder> wRetList = new ArrayList<PRCDeductionsBeanHolder>();

        for (PaycheckGarnishment p : pGarnishments) {
            DeductionMiniBean d = new DeductionMiniBean();
            d.setName(p.getEmpGarnInfo().getDescription());
            d.setAmount(p.getAmount() * -1.0D);

            PRCDeductionsBeanHolder wP = new PRCDeductionsBeanHolder();
            wP.setId(p.getId());
            wP.setMonth(p.getRunMonth());
            wP.setYear(p.getRunYear());
            wP.setDeductionBean(d);

            wRetList.add(wP);
        }

        return wRetList;
    }

    @RequestMapping({"/singleEmployeePromoExcel.do"})
    public void generateTransferExcelHandler(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request, HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException, IOException, NoBusinessCertificationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = getBusinessCertificate(request);

         Long serviceId = pEmpId;
        Long bizId = bc.getBusinessClientInstId();
        BusinessCertificate parentCert = null;
        HiringInfo wHireInfo = loadHiringInfoByEmpId(request,bc,pEmpId);
        AbstractEmployeeEntity wEmp  = wHireInfo.getAbstractEmployeeEntity();
        if(wHireInfo.isPensionerType()){
            //--Look for if he has a Parent Business Client.
            if(wHireInfo.getPensioner().getParentBusinessClientId() != null){


                serviceId = wHireInfo.getPensioner().getEmployee().getId();
                bizId = wHireInfo.getPensioner().getParentBusinessClientId();
                parentCert = BusinessCertificateCreator.makeBusinessClient(genericService.loadObjectById(BusinessClient.class, bizId));

            }
        }
        ReportGeneratorBean rt = new ReportGeneratorBean();

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        List<AbstractPromotionAuditEntity> wPromoHist = (List<AbstractPromotionAuditEntity>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPromotionAuditClass(bc),
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bizId), CustomPredicate.procurePredicate("employee.id", serviceId)), null);

        Collections.sort(wPromoHist, Comparator.comparing(AbstractPromotionAuditEntity::getPromotionDate).reversed());


        BusinessEmpOVBeanInactive wPGBDH = new BusinessEmpOVBeanInactive(wPromoHist);
        wPGBDH.setEmployeeId(wEmp.getEmployeeId());
        wPGBDH.setId(wEmp.getId());
        wPGBDH.setConfirmationDate(wHireInfo.getConfirmationDateAsStr());

        wPGBDH.setEmployeeName(wEmp.getDisplayNameWivTitlePrefixed());
        wPGBDH.setCurrentMda(wEmp.getMdaDeptMap().getMdaInfo().getName());
        if(wHireInfo.isPensionerType())
            wPGBDH.setTotalMonthlyBasic(wHireInfo.getMonthlyPensionAmount());
        else
            wPGBDH.setCurrentLevelAndStep(wEmp.getSalaryInfo().getSalaryScaleLevelAndStepStr());

        wPGBDH.setBirthDate(wHireInfo.getBirthDate());
        wPGBDH.setHireDate(wHireInfo.getHireDate());
        wPGBDH.setCanEdit(wEmp.isSchoolStaff());
        if(wEmp.isSchoolStaff())
            wPGBDH.setDisplayErrors(wEmp.getSchoolName());
        if (wEmp.isTerminated()) {
            wPGBDH.setTerminatedEmployee(true);
            wPGBDH.setTerminationReason(wHireInfo.getTerminateReason().getName());
            if(wEmp.isPensioner()) {
                wPGBDH.setNoOfYearsInService(wHireInfo.getNoOfYearsInService());
                wPGBDH.setTerminationDate(PayrollHRUtils.getDisplayDateFormat().format(wHireInfo.getPensionEndDate()));
            }
            else{
                    wPGBDH.setNoOfYearsInService(wHireInfo.getNoOfYearsInService());
                    wPGBDH.setTerminationDate(PayrollHRUtils.getDisplayDateFormat().format(wHireInfo.getTerminateDate()));
                }
        } else {
            wPGBDH.setNoOfYearsInService(wHireInfo.getNoOfYearsInService());
        }
        wPGBDH.setForPromotion(true);

        String mdaTitle = bc.getMdaTitle();
        if(parentCert != null)
            mdaTitle = parentCert.getMdaTitle();
        List<Map<String, Object>> contPenMapped = new ArrayList<>();
        for(AbstractPromotionAuditEntity data : wPromoHist) {
            Map<String, Object> newData = new HashMap<>();
            newData.put("Promotion Date", data.getPromotionDateStr());
            newData.put("From", data.getOldSalaryInfo().getSalaryScaleLevelAndStepStr());
            newData.put("To", data.getSalaryInfo().getSalaryScaleLevelAndStepStr());
            newData.put("Old Gross", data.getOldSalaryInfo().getAnnualSalary());
            newData.put("New Gross", data.getSalaryInfo().getAnnualSalary());
            newData.put("Net Increase",data.getNetIncreaseStr());
            newData.put(mdaTitle+" At Promotion", data.getMdaInfo().getName());
            newData.put("Promoted By", data.getUser().getActualUserName());
            contPenMapped.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean("Promotion Date", 0));
        tableHeaders.add(new ReportGeneratorBean("From", 0));
        tableHeaders.add(new ReportGeneratorBean("To", 0));
        tableHeaders.add(new ReportGeneratorBean("Old Gross", 2));
        tableHeaders.add(new ReportGeneratorBean("New Gross", 2));
        tableHeaders.add(new ReportGeneratorBean("Net Increase", 2));

        tableHeaders.add(new ReportGeneratorBean(mdaTitle+" At Promotion", 0));
        tableHeaders.add(new ReportGeneratorBean("Promoted By", 0));

        List<Map<String, Object>> otherSpecialHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : tableHeaders) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            otherSpecialHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(wEmp.getLastName()+"_"+wEmp.getEmployeeId()+"_promotion_history");
        mainHeaders.add(bc.getStaffTypeName()+" Name: "+ wPGBDH.getEmployeeName());
        mainHeaders.add(bc.getStaffTitle()+": "+wPGBDH.getEmployeeId());
        mainHeaders.add(bc.getMdaTitle()+": "+wPGBDH.getCurrentMda());
        mainHeaders.add("Date Of Birth: "+ wPGBDH.getBirthDateStr());
        if(wHireInfo.isPensionerType()){
            mainHeaders.add("Pension Start Date: "+wHireInfo.getPensionStartDateStr());
            mainHeaders.add("Monthly Pension: "+wHireInfo.getMonthlyPensionAmountStr());
        }else{
            mainHeaders.add("Date Of Hire: "+wPGBDH.getHireDateStr());
            mainHeaders.add("Confirmation Date: "+wPGBDH.getConfirmationDate());
            mainHeaders.add("Pay Group: "+wPGBDH.getCurrentLevelAndStep());
        }

        if(wEmp.isTerminatedEmployee()){
            mainHeaders.add("Termination Date: "+wHireInfo.getTerminatedDateStr());
            mainHeaders.add("Termination Reason: "+wPGBDH.getTerminationReason());
        }else{
            mainHeaders.add("Expected Retirement Date: "+wHireInfo.getExpDateOfRetireStr());
        }
         mainHeaders.add("No Of Years In Service: " +wPGBDH.getNoOfYearsInService());



        rt.setBusinessCertificate(bc);
        rt.setGroupBy(null);
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(contPenMapped);
        rt.setTableHeaders(otherSpecialHeaders);
        rt.setReportTitle(wEmp.getLastName()+"_promotion_history");
        rt.setTableType(0);
        rt.setTotalInd(0);


        simpleExcelReportGenerator.getExcel(response, request, rt);
    }

}
