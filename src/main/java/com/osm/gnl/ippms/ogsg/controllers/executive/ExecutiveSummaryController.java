package com.osm.gnl.ippms.ogsg.controllers.executive;


import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.subvention.SubventionHistory;
import com.osm.gnl.ippms.ogsg.forensic.domain.Materiality;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.domain.payroll.DevelopmentLevy;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.report.beans.WageBeanContainer;
import com.osm.gnl.ippms.ogsg.report.beans.WageSummaryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping({"/allBusinessExecutiveSummary.do"})
@SessionAttributes(types={WageBeanContainer.class})
public class ExecutiveSummaryController extends BaseController {

    @Autowired
    private PaycheckService paycheckService;

    @Autowired
    private EmployeeService employeeService;


    private HashMap<Long, WageSummaryBean> mdasMap;

    private HashMap<Long, WageSummaryBean> subventionMap;

    private static final String VIEW_NAME = "report/payrollSummaryByClientForm";


    @ModelAttribute("monthList")
    protected List<NamedEntity> getMonthsList(){
        return PayrollBeanUtils.makeAllMonthList();
    }
    @ModelAttribute("yearList")
    protected Collection<NamedEntity> getYearList(HttpServletRequest request){

        return paycheckService.makePaycheckYearList(getBusinessCertificate(request));
    }

    private void init()
    {

        this.mdasMap = new HashMap<>();

        this.subventionMap = new HashMap<>();
    }


    @RequestMapping(method={RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

       List<PayrollFlag> pfList =  this.genericService.loadAllObjectsWithoutRestrictions(PayrollFlag.class, "payPeriodStart");
        Comparator<PayrollFlag> comparator = Comparator.comparing(PayrollFlag::getId);

        Collections.sort(pfList, comparator.reversed());

        PayrollFlag pf = pfList.get(1);

       // System.out.println("payroll Flag: "+pf.getPayPeriodStart());

        LocalDate endDate = null;

        if (!pf.isNewEntity()) {

            endDate = pf.getPayPeriodEnd();
        } else {
            ArrayList<LocalDate> list = PayrollBeanUtils.getDefaultCheckRegisterDates();

            endDate = list.get(1);
        }


        return prepareSubesequentMonths(endDate.getMonthValue(), endDate.getYear(), 1,endDate, endDate, model,request, 0);
    }


    @RequestMapping(method={RequestMethod.GET}, params={"fd", "td"})
    public String setupForm(@RequestParam("fd") String fromDate, @RequestParam("td") String toDate, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        LocalDate date1 = PayrollBeanUtils.setDateFromString(fromDate);

        LocalDate date2 = PayrollBeanUtils.setDateFromString(toDate);

        int pStartDate = date1.getMonthValue();
        int pRunYear = date1.getYear();
        int pEndMonth = date2.getMonthValue();
        int monthDiff =  pEndMonth - pStartDate;
        return prepareSubesequentMonths(pStartDate,pRunYear, monthDiff, date1, date2, model,request, 0);
    }
//    @RequestMapping(method={RequestMethod.GET}, params={"rm", "ry","sl"})
//    public String setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int  pRunYear,  @RequestParam("sl") int pShowDiffInd,Model model, HttpServletRequest request) throws Exception {
//        SessionManagerService.manageSession(request, model);
//        return doWork(pRunMonth,pRunYear,model,request, pShowDiffInd);
//    }


    private String doWork(int pRunMonth,int  pRunYear, int monthDiff, Model model, HttpServletRequest request, int pShowDiffInd) throws Exception {

            ArrayList<WageBeanContainer> wWBList = new ArrayList<>();

            BusinessCertificate bc = this.getBusinessCertificate(request);

            init();

            LocalDate fDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
            WageBeanContainer wBEOB = new WageBeanContainer();
            wBEOB.setShowDiffInd(pShowDiffInd);

            int noOfEmpWivNegPay = IppmsUtilsExt.countNoOfNegativePay(IppmsUtils.getPaycheckClass(bc), genericService, bc);

            if (noOfEmpWivNegPay > 0) {
                LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
                return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + _wCal.getMonthValue() + "&ry=" + _wCal.getYear();
            }

            LocalDate wPrevMonthStart = PayrollBeanUtils.getPreviousMonthDate(fDate, false);
            LocalDate wPrevMonthEnd = PayrollBeanUtils.getPreviousMonthDate(fDate, true);

            LocalDate fPrevDate;
            fPrevDate = wPrevMonthStart;

            List<EmployeePayBean> wEPBList = this.paycheckService.loadAllExecEmployeePayBeanByParentIdFromDateToDate(pRunMonth, pRunYear);
            List<WageSummaryBean> wRetList = new ArrayList<WageSummaryBean>();


            LocalDate wCheckDate;
            wCheckDate = wPrevMonthEnd;

        wBEOB.setShowUnionDues(PayrollBeanUtils.isUnionDuesDeducted(wCheckDate));


            wBEOB.setMonthAndYearStr(PayrollHRUtils.getMonthYearDateFormat().format(fDate));
            wBEOB.setPrevMonthAndYearStr(PayrollHRUtils.getMonthYearDateFormat().format(wPrevMonthStart));

            double wTotalCurrPay = 0.0D;
            double wTotalPrevPay = 0.0D;
            int serialNum = 0;
            WageSummaryBean wTotalDeductions = new WageSummaryBean();
            wTotalDeductions.setName("Other Deductions");
            WageSummaryBean wTotalLoans = new WageSummaryBean();
            wTotalLoans.setName("Total Loans");
            WageSummaryBean wPayee = new WageSummaryBean();
            wPayee.setName("Total Taxes");
            WageSummaryBean wPensions = new WageSummaryBean();
            wPensions.setName("Contributory Pensions (Employee)");
            WageSummaryBean wPensionsEmployer = new WageSummaryBean();
            wPensionsEmployer.setName("Contributory Pensions (Employer)");

            WageSummaryBean wRBA = new WageSummaryBean();

            PayrollRunMasterBean wPRMB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(
                    CustomPredicate.procurePredicate("runMonth", pRunMonth), CustomPredicate.procurePredicate("runYear", pRunYear)));

            if (wPRMB.getRbaPercentage() > 0)
                wRBA.setName("Redemption Bond Account ( " + wPRMB.getRbaPercentageStr() + " Gross Pay)");

            boolean wDevLevyDeducted = false;

            LocalDate wCal;
            wCal = wPrevMonthEnd;


            PayrollRunMasterBean wPRMBPrev = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(
                    CustomPredicate.procurePredicate("runMonth", wCal.getMonthValue()), CustomPredicate.procurePredicate("runYear", wCal.getYear())));

            DevelopmentLevy wDLI = this.genericService.loadObjectWithSingleCondition(DevelopmentLevy.class,
                    CustomPredicate.procurePredicate("year", wCal.getYear()));
            if (wDLI.isNewEntity()) {
                if (wCal.getYear() != fDate.getYear()) {
                    wDLI = this.genericService.loadObjectWithSingleCondition(DevelopmentLevy.class,
                            CustomPredicate.procurePredicate("year", wCal.getYear()));
                    if (!wDLI.isNewEntity()) {
                        if (wDLI.getMonth() == wCal.getMonthValue())
                            wDevLevyDeducted = true;
                    }
                }
            } else {
                if (wDLI.getMonth() == fDate.getMonthValue()) {
                    wDevLevyDeducted = true;
                }

                wDLI = this.genericService.loadObjectWithSingleCondition(DevelopmentLevy.class,
                        CustomPredicate.procurePredicate("year", wCal.getYear()));
                if (!wDLI.isNewEntity()) {
                    if (wDLI.getMonth() == wCal.getMonthValue()) {
                        wDevLevyDeducted = true;
                    }
                }
            }
            WageSummaryBean wDevLevy = null;
            if (wDevLevyDeducted) {
                wDevLevy = new WageSummaryBean();
                wDevLevy.setName("Development Levy");
            }
            wBEOB.setTotalNoOfEmp(wEPBList.size());
            HashMap<Long, Double> wDeductionMap = employeeService.loadAllBizClientDeductionsForActiveEmployees(fDate);
            for (EmployeePayBean e : wEPBList) {
                Long wKey = e.getBusinessClientId();

                wPayee.setCurrentBalance(wPayee.getCurrentBalance() + e.getTaxesPaid());

                double totalDeductions = 0.0D;
                if (wDeductionMap.containsKey(e.getEmployee().getId())) {
                    totalDeductions = wDeductionMap.get(e.getEmployee().getId()).doubleValue();
                    wTotalDeductions.setCurrentBalance(wTotalDeductions.getCurrentBalance() + totalDeductions);
                }

                wTotalLoans.setCurrentBalance(wTotalLoans.getCurrentBalance() + e.getTotalGarnishments());
                wPensions.setCurrentBalance(wPensions.getCurrentBalance() + e.getContributoryPension());
                wPensionsEmployer.setCurrentBalance(wPensionsEmployer.getCurrentBalance() + e.getContributoryPension());
                if ((wDevLevyDeducted) && (e.getDevelopmentLevy() > 0.0D)) {
                    wDevLevy.setCurrentBalance(wDevLevy.getCurrentBalance() + e.getDevelopmentLevy());
                }

                wBEOB.setTotalDedBal(wBEOB.getTotalDedBal() + totalDeductions + e.getTotalGarnishments() + e.getTaxesPaid() + e.getDevelopmentLevy() + e.getContributoryPension());
//            }
                wTotalCurrPay += e.getTotalPay();
                WageSummaryBean wWSB = null;


                if (this.mdasMap.containsKey(wKey)) {
                    wWSB = this.mdasMap.get(wKey);
                } else {
                    wWSB = new WageSummaryBean();
                    wWSB.setClientId(wKey);
                    wWSB.setObjectInd(1);
                    serialNum++;
                    wWSB.setSerialNum(serialNum);
                    wWSB.setAssignedToObject(e.getBusinessClientName());
                }

                wWSB.setCurrentBalance(wWSB.getCurrentBalance() + e.getTotalPay());
                wWSB.setNoOfEmp(wWSB.getNoOfEmp() + 1);
                this.mdasMap.put(wKey, wWSB);

            }

            wEPBList = null;
            List<EmployeePayBean> wEPBListOld = this.paycheckService.loadAllBizClientEmployeePayBeanByParentIdFromDateToDate(fPrevDate.getMonthValue(), fPrevDate.getYear());

            wBEOB.setTotalNoOfPrevMonthEmp(wEPBListOld.size());
            wDeductionMap = employeeService.loadAllBizClientDeductionsForActiveEmployees(wCal);
            for (EmployeePayBean e : wEPBListOld) {
                Long wKey = e.getBusinessClientId();

                wTotalPrevPay += e.getTotalPay();
                wPayee.setPreviousBalance(wPayee.getPreviousBalance() + e.getTaxesPaid());

                double totalDeductions = 0.0D;
                if (wDeductionMap.containsKey(e.getEmployee().getId())) {
                    totalDeductions = wDeductionMap.get(e.getEmployee().getId()).doubleValue();
                    wTotalDeductions.setPreviousBalance(wTotalDeductions.getPreviousBalance() + totalDeductions);
                }
                wTotalLoans.setPreviousBalance(wTotalLoans.getPreviousBalance() + e.getTotalGarnishments());
                wPensions.setPreviousBalance(wPensions.getPreviousBalance() + e.getContributoryPension());
                wPensionsEmployer.setPreviousBalance(wPensionsEmployer.getPreviousBalance() + e.getContributoryPension());

                if ((wDevLevyDeducted) && (e.getDevelopmentLevy() > 0.0D)) {
                    wDevLevy.setPreviousBalance(wDevLevy.getPreviousBalance() + e.getDevelopmentLevy());
                }
//            if(isPensioner()) {
//                wBEOB.setTotalPrevDedBal(wBEOB.getTotalPrevDedBal() + e.getTotalGarnishments() + totalDeductions + e.getTaxesPaid() + e.getDevelopmentLevy());
//            }
//            else{
                wBEOB.setTotalPrevDedBal(wBEOB.getTotalPrevDedBal() + e.getTotalGarnishments() + totalDeductions + e.getTaxesPaid() + e.getDevelopmentLevy() + e.getContributoryPension());
//            }
                WageSummaryBean wWSB = null;


                if (this.mdasMap.containsKey(wKey)) {
                    wWSB = this.mdasMap.get(wKey);
                } else {
                    wWSB = new WageSummaryBean();
                    wWSB.setObjectInd(1);
                    wWSB.setClientId(wKey);
                    serialNum++;
                    wWSB.setSerialNum(serialNum);
                    wWSB.setAssignedToObject(e.getBusinessClientName());
                }

                wWSB.setPreviousBalance(wWSB.getPreviousBalance() + e.getTotalPay());
                wWSB.setPreviousNoOfEmp(wWSB.getPreviousNoOfEmp() + 1);
                this.mdasMap.put(wKey, wWSB);
            }


            wEPBListOld = null;
            wRetList = getWageSummaryBeanFromMap(this.mdasMap, wRetList, true);

            wBEOB.setTotalCurrBal(wTotalCurrPay);
            wBEOB.setTotalPrevBal(wTotalPrevPay);


            List<WageSummaryBean> wContList = new ArrayList<WageSummaryBean>();
            serialNum++;
            wPensionsEmployer.setSerialNum(serialNum);
            wRBA.setCurrentBalance(wBEOB.getTotalCurrBal() * (wPRMB.getRbaPercentage() / 100.0D));
            wRBA.setPreviousBalance(wBEOB.getTotalPrevBal() * (wPRMBPrev.getRbaPercentage() / 100.0D));
            serialNum++;
            wRBA.setSerialNum(serialNum);
            wContList.add(wPensionsEmployer);
            wContList.add(wRBA);
            wBEOB.setTotalCurrCont(wPensionsEmployer.getCurrentBalance() + wRBA.getCurrentBalance());
            wBEOB.setTotalPrevCont(wPensionsEmployer.getPreviousBalance() + wRBA.getPreviousBalance());
            Collections.sort(wContList);
            wBEOB.setContributionList(wContList);

            LocalDate wEnd;
            wEnd = (wPrevMonthEnd);

            List<WageSummaryBean> wSubventionList = new ArrayList<WageSummaryBean>();
            List<SubventionHistory> wListCurrent = this.genericService.loadAllObjectsUsingRestrictions(SubventionHistory.class,
                    Arrays.asList(CustomPredicate.procurePredicate("runMonth", pRunMonth), CustomPredicate.procurePredicate("runYear", pRunYear)), null);
            for (SubventionHistory s : wListCurrent) {
                WageSummaryBean wWSB = new WageSummaryBean();

                wWSB.setSubvention(true);

                wWSB.setDisplayStyle("reportOdd");
                serialNum++;
                wWSB.setSerialNum(serialNum);
                wWSB.setName(s.getSubvention().getName());

                if (wBEOB.getTotalCurrBal() > 0.0D) {
                    wWSB.setCurrentBalance(s.getAmount());
                    wBEOB.setTotalSubBal(wBEOB.getTotalSubBal() + s.getAmount());
                } else {
                    wWSB.setCurrentBalance(0.0D);
                    wBEOB.setTotalSubBal(wBEOB.getTotalSubBal() + 0.0D);
                }

                this.subventionMap.put(s.getSubvention().getId(), wWSB);
            }
            wListCurrent = null;
//     List<SubventionHistory> wListPrev = this.payrollServiceExt.loadSubventionHistoryByDates(wEnd.get(2), wEnd.get(1));
            List<SubventionHistory> wListPrev = this.genericService.loadAllObjectsUsingRestrictions(SubventionHistory.class,
                    Arrays.asList(CustomPredicate.procurePredicate("runMonth", wEnd.getMonthValue()),
                            CustomPredicate.procurePredicate("runYear", wEnd.getYear())), null);

            if (wListPrev != null) {
                for (SubventionHistory s : wListPrev) {
                    WageSummaryBean wWSB = this.subventionMap.get(s.getSubvention().getId());

                    if (wWSB == null) {
                        wWSB = new WageSummaryBean();
                        wWSB.setDisplayStyle("reportOdd");
                        serialNum++;
                        wWSB.setSerialNum(serialNum);
                        wWSB.setName(s.getSubvention().getName());
                    }

                    if (!wWSB.isSubvention()) {
                        wWSB.setSubvention(true);
                    }

                    if (wBEOB.getTotalPrevBal() > 0.0D) {
                        wWSB.setPreviousBalance(s.getAmount());
                        wBEOB.setTotalPrevSubBal(wBEOB.getTotalPrevSubBal() + s.getAmount());
                    } else {
                        wBEOB.setTotalSubBal(wBEOB.getTotalSubBal() + 0.0D);
                    }

                    this.subventionMap.put(s.getSubvention().getId(), wWSB);
                }
            }

            wListPrev = null;
            wSubventionList = getWageSummaryBeanFromMap(this.subventionMap, wSubventionList, false);
            Collections.sort(wSubventionList);
            wBEOB.setSubventionList(wSubventionList);

            List<WageSummaryBean> wDedList = new ArrayList<WageSummaryBean>();
            serialNum++;
            wPayee.setSerialNum(serialNum);
            serialNum++;
            wTotalDeductions.setSerialNum(serialNum);

            serialNum++;
            wTotalLoans.setSerialNum(serialNum);
//        if (!(isPensioner())){
            serialNum++;
            wPensions.setSerialNum(serialNum);
//        }

            if (wDevLevyDeducted) {
                serialNum++;
                wDevLevy.setSerialNum(serialNum);
            }

            wDedList.add(wPayee);
            wDedList.add(wTotalDeductions);

            wDedList.add(wTotalLoans);
//        if (!(isPensioner())) {
            wDedList.add(wPensions);
//        }
            if (wDevLevyDeducted) {
                wDedList.add(wDevLevy);
            }

            Collections.sort(wDedList);
            wBEOB.setDeductionList(wDedList);

//       if(bc.isPensioner(){
//            wBEOB.setTotalCurrOutGoing(wBEOB.getTotalCurrBal());
//            wBEOB.setTotalPrevOutGoing(wBEOB.getTotalPrevBal());
//        }
//        else{
            wBEOB.setTotalCurrOutGoing(wBEOB.getTotalCurrBal() + wBEOB.getTotalSubBal() + wBEOB.getTotalCurrCont());
            wBEOB.setTotalPrevOutGoing(wBEOB.getTotalPrevBal() + wBEOB.getTotalPrevSubBal() + wBEOB.getTotalPrevCont());
//        }

            wBEOB.setGrandTotal(wBEOB.getTotalCurrBal() - wBEOB.getTotalDedBal());
            wBEOB.setGrandPrevTotal(wBEOB.getTotalPrevBal() - wBEOB.getTotalPrevDedBal());


            wRetList = setMaterialityLevelIndicator(request, wRetList, bc.getLoginId());
            Collections.sort(wRetList);
            wBEOB.setWageSummaryBeanList(wRetList);
            wBEOB.setRunMonth(pRunMonth);
            wBEOB.setRunYear(pRunYear);
        model.addAttribute("miniBean", wBEOB);
        addRoleBeanToModel(model, request);

        return VIEW_NAME;
    }



    private String prepareSubesequentMonths(int pRunMonth, int  pRunYear, int monthDiff, LocalDate sDate, LocalDate eDate, Model model, HttpServletRequest request, int pShowDiffInd) throws Exception {

        List<WageBeanContainer> wWBList = new ArrayList<>();
        WageBeanContainer gBean = new WageBeanContainer();
        int i, x = -1;
        for (i=0; i<monthDiff; i++) {
            x++;
            pRunMonth = pRunMonth + x;
            BusinessCertificate bc = this.getBusinessCertificate(request);

            init();

            LocalDate fDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
            WageBeanContainer wBEOB = new WageBeanContainer();
            wBEOB.setShowDiffInd(pShowDiffInd);

            int noOfEmpWivNegPay = IppmsUtilsExt.countNoOfNegativePay(IppmsUtils.getPaycheckClass(bc), genericService, bc);

            if (noOfEmpWivNegPay > 0) {
                LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
                return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + _wCal.getMonthValue() + "&ry=" + _wCal.getYear();
            }

            LocalDate wPrevMonthStart = PayrollBeanUtils.getPreviousMonthDate(fDate, false);
            LocalDate wPrevMonthEnd = PayrollBeanUtils.getPreviousMonthDate(fDate, true);

            LocalDate fPrevDate;
            fPrevDate = wPrevMonthStart;

            List<EmployeePayBean> wEPBList = this.paycheckService.loadAllExecEmployeePayBeanByParentIdFromDateToDate(pRunMonth, pRunYear);
           // System.out.println("all employee paybean size is: " + wEPBList.size());
            List<WageSummaryBean> wRetList = new ArrayList<WageSummaryBean>();


            LocalDate wCheckDate;
            wCheckDate = wPrevMonthEnd;

            wBEOB.setShowUnionDues(PayrollBeanUtils.isUnionDuesDeducted(wCheckDate));


            wBEOB.setMonthAndYearStr(PayrollHRUtils.getMonthYearDateFormat().format(fDate));
            wBEOB.setPrevMonthAndYearStr(PayrollHRUtils.getMonthYearDateFormat().format(wPrevMonthStart));

            double wTotalCurrPay = 0.0D;
            double wTotalPrevPay = 0.0D;
            int serialNum = 0;
            WageSummaryBean wTotalDeductions = new WageSummaryBean();
            wTotalDeductions.setName("Other Deductions");
            WageSummaryBean wTotalLoans = new WageSummaryBean();
            wTotalLoans.setName("Total Loans");
            WageSummaryBean wPayee = new WageSummaryBean();
            wPayee.setName("Total Taxes");
            WageSummaryBean wPensions = new WageSummaryBean();
            wPensions.setName("Contributory Pensions (Employee)");
            WageSummaryBean wPensionsEmployer = new WageSummaryBean();
            wPensionsEmployer.setName("Contributory Pensions (Employer)");

            WageSummaryBean wRBA = new WageSummaryBean();

            PayrollRunMasterBean wPRMB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(
                    CustomPredicate.procurePredicate("runMonth", pRunMonth), CustomPredicate.procurePredicate("runYear", pRunYear)));

            if (wPRMB.getRbaPercentage() > 0)
                wRBA.setName("Redemption Bond Account ( " + wPRMB.getRbaPercentageStr() + " Gross Pay)");

            boolean wDevLevyDeducted = false;

            LocalDate wCal;
            wCal = wPrevMonthEnd;


            PayrollRunMasterBean wPRMBPrev = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(
                    CustomPredicate.procurePredicate("runMonth", wCal.getMonthValue()), CustomPredicate.procurePredicate("runYear", wCal.getYear())));

            DevelopmentLevy wDLI = this.genericService.loadObjectWithSingleCondition(DevelopmentLevy.class,
                    CustomPredicate.procurePredicate("year", wCal.getYear()));
            if (wDLI.isNewEntity()) {
                if (wCal.getYear() != fDate.getYear()) {
                    wDLI = this.genericService.loadObjectWithSingleCondition(DevelopmentLevy.class,
                            CustomPredicate.procurePredicate("year", wCal.getYear()));
                    if (!wDLI.isNewEntity()) {
                        if (wDLI.getMonth() == wCal.getMonthValue())
                            wDevLevyDeducted = true;
                    }
                }
            } else {
                if (wDLI.getMonth() == fDate.getMonthValue()) {
                    wDevLevyDeducted = true;
                }

                wDLI = this.genericService.loadObjectWithSingleCondition(DevelopmentLevy.class,
                        CustomPredicate.procurePredicate("year", wCal.getYear()));
                if (!wDLI.isNewEntity()) {
                    if (wDLI.getMonth() == wCal.getMonthValue()) {
                        wDevLevyDeducted = true;
                    }
                }
            }
            WageSummaryBean wDevLevy = null;
            if (wDevLevyDeducted) {
                wDevLevy = new WageSummaryBean();
                wDevLevy.setName("Development Levy");
            }
            wBEOB.setTotalNoOfEmp(wEPBList.size());
            HashMap<Long, Double> wDeductionMap = employeeService.loadAllBizClientDeductionsForActiveEmployees(fDate);
            for (EmployeePayBean e : wEPBList) {
                Long wKey = e.getBusinessClientId();

                wPayee.setCurrentBalance(wPayee.getCurrentBalance() + e.getTaxesPaid());

                double totalDeductions = 0.0D;
                if (wDeductionMap.containsKey(e.getEmployee().getId())) {
                    totalDeductions = wDeductionMap.get(e.getEmployee().getId()).doubleValue();
                    wTotalDeductions.setCurrentBalance(wTotalDeductions.getCurrentBalance() + totalDeductions);
                }

                wTotalLoans.setCurrentBalance(wTotalLoans.getCurrentBalance() + e.getTotalGarnishments());
                wPensions.setCurrentBalance(wPensions.getCurrentBalance() + e.getContributoryPension());
                wPensionsEmployer.setCurrentBalance(wPensionsEmployer.getCurrentBalance() + e.getContributoryPension());
                if ((wDevLevyDeducted) && (e.getDevelopmentLevy() > 0.0D)) {
                    wDevLevy.setCurrentBalance(wDevLevy.getCurrentBalance() + e.getDevelopmentLevy());
                }

                wBEOB.setTotalDedBal(wBEOB.getTotalDedBal() + totalDeductions + e.getTotalGarnishments() + e.getTaxesPaid() + e.getDevelopmentLevy() + e.getContributoryPension());
//            }
                wTotalCurrPay += e.getTotalPay();
                WageSummaryBean wWSB = null;


                if (this.mdasMap.containsKey(wKey)) {
                    wWSB = this.mdasMap.get(wKey);
                } else {
                    wWSB = new WageSummaryBean();
                    wWSB.setClientId(wKey);
                    wWSB.setObjectInd(1);
                    serialNum++;
                    wWSB.setSerialNum(serialNum);
                    wWSB.setAssignedToObject(e.getBusinessClientName());
                }

                wWSB.setCurrentBalance(wWSB.getCurrentBalance() + e.getTotalPay());
                wWSB.setNoOfEmp(wWSB.getNoOfEmp() + 1);
                this.mdasMap.put(wKey, wWSB);

            }

            List<EmployeePayBean> wEPBListOld = this.paycheckService.loadAllBizClientEmployeePayBeanByParentIdFromDateToDate(fPrevDate.getMonthValue(), fPrevDate.getYear());

            wBEOB.setTotalNoOfPrevMonthEmp(wEPBListOld.size());
            wDeductionMap = employeeService.loadAllBizClientDeductionsForActiveEmployees(wCal);
            for (EmployeePayBean e : wEPBListOld) {
                Long wKey = e.getBusinessClientId();

                wTotalPrevPay += e.getTotalPay();
                wPayee.setPreviousBalance(wPayee.getPreviousBalance() + e.getTaxesPaid());

                double totalDeductions = 0.0D;
                if (wDeductionMap.containsKey(e.getEmployee().getId())) {
                    totalDeductions = wDeductionMap.get(e.getEmployee().getId()).doubleValue();
                    wTotalDeductions.setPreviousBalance(wTotalDeductions.getPreviousBalance() + totalDeductions);
                }
                wTotalLoans.setPreviousBalance(wTotalLoans.getPreviousBalance() + e.getTotalGarnishments());
                wPensions.setPreviousBalance(wPensions.getPreviousBalance() + e.getContributoryPension());
                wPensionsEmployer.setPreviousBalance(wPensionsEmployer.getPreviousBalance() + e.getContributoryPension());

                if ((wDevLevyDeducted) && (e.getDevelopmentLevy() > 0.0D)) {
                    wDevLevy.setPreviousBalance(wDevLevy.getPreviousBalance() + e.getDevelopmentLevy());
                }
//            if(isPensioner()) {
//                wBEOB.setTotalPrevDedBal(wBEOB.getTotalPrevDedBal() + e.getTotalGarnishments() + totalDeductions + e.getTaxesPaid() + e.getDevelopmentLevy());
//            }
//            else{
                wBEOB.setTotalPrevDedBal(wBEOB.getTotalPrevDedBal() + e.getTotalGarnishments() + totalDeductions + e.getTaxesPaid() + e.getDevelopmentLevy() + e.getContributoryPension());
//            }
                WageSummaryBean wWSB;


                if (this.mdasMap.containsKey(wKey)) {
                    wWSB = this.mdasMap.get(wKey);
                } else {
                    wWSB = new WageSummaryBean();
                    wWSB.setObjectInd(1);
                    wWSB.setClientId(wKey);
                    serialNum++;
                    wWSB.setSerialNum(serialNum);
                    wWSB.setAssignedToObject(e.getBusinessClientName());
                }

                wWSB.setPreviousBalance(wWSB.getPreviousBalance() + e.getTotalPay());
                wWSB.setPreviousNoOfEmp(wWSB.getPreviousNoOfEmp() + 1);
                this.mdasMap.put(wKey, wWSB);
            }


            wRetList = getWageSummaryBeanFromMap(this.mdasMap, wRetList, true);

            wBEOB.setTotalCurrBal(wTotalCurrPay);
            wBEOB.setTotalPrevBal(wTotalPrevPay);


            List<WageSummaryBean> wContList = new ArrayList<WageSummaryBean>();
            serialNum++;
            wPensionsEmployer.setSerialNum(serialNum);
            wRBA.setCurrentBalance(wBEOB.getTotalCurrBal() * (wPRMB.getRbaPercentage() / 100.0D));
            wRBA.setPreviousBalance(wBEOB.getTotalPrevBal() * (wPRMBPrev.getRbaPercentage() / 100.0D));
            serialNum++;
            wRBA.setSerialNum(serialNum);
            wContList.add(wPensionsEmployer);
            wContList.add(wRBA);
            wBEOB.setTotalCurrCont(wPensionsEmployer.getCurrentBalance() + wRBA.getCurrentBalance());
            wBEOB.setTotalPrevCont(wPensionsEmployer.getPreviousBalance() + wRBA.getPreviousBalance());
            Collections.sort(wContList);
            wBEOB.setContributionList(wContList);

            LocalDate wEnd;
            wEnd = (wPrevMonthEnd);

            List<WageSummaryBean> wSubventionList = new ArrayList<WageSummaryBean>();
            List<SubventionHistory> wListCurrent = this.genericService.loadAllObjectsUsingRestrictions(SubventionHistory.class,
                    Arrays.asList(CustomPredicate.procurePredicate("runMonth", pRunMonth), CustomPredicate.procurePredicate("runYear", pRunYear)), null);
            for (SubventionHistory s : wListCurrent) {
                WageSummaryBean wWSB = new WageSummaryBean();

                wWSB.setSubvention(true);

                wWSB.setDisplayStyle("reportOdd");
                serialNum++;
                wWSB.setSerialNum(serialNum);
                wWSB.setName(s.getSubvention().getName());

                if (wBEOB.getTotalCurrBal() > 0.0D) {
                    wWSB.setCurrentBalance(s.getAmount());
                    wBEOB.setTotalSubBal(wBEOB.getTotalSubBal() + s.getAmount());
                } else {
                    wWSB.setCurrentBalance(0.0D);
                    wBEOB.setTotalSubBal(wBEOB.getTotalSubBal() + 0.0D);
                }

                this.subventionMap.put(s.getSubvention().getId(), wWSB);
            }
            //     List<SubventionHistory> wListPrev = this.payrollServiceExt.loadSubventionHistoryByDates(wEnd.get(2), wEnd.get(1));
            List<SubventionHistory> wListPrev = this.genericService.loadAllObjectsUsingRestrictions(SubventionHistory.class,
                    Arrays.asList(CustomPredicate.procurePredicate("runMonth", wEnd.getMonthValue()),
                            CustomPredicate.procurePredicate("runYear", wEnd.getYear())), null);

            if (wListPrev != null) {
                for (SubventionHistory s : wListPrev) {
                    WageSummaryBean wWSB = this.subventionMap.get(s.getSubvention().getId());

                    if (wWSB == null) {
                        wWSB = new WageSummaryBean();
                        wWSB.setDisplayStyle("reportOdd");
                        serialNum++;
                        wWSB.setSerialNum(serialNum);
                        wWSB.setName(s.getSubvention().getName());
                    }

                    if (!wWSB.isSubvention()) {
                        wWSB.setSubvention(true);
                    }

                    if (wBEOB.getTotalPrevBal() > 0.0D) {
                        wWSB.setPreviousBalance(s.getAmount());
                        wBEOB.setTotalPrevSubBal(wBEOB.getTotalPrevSubBal() + s.getAmount());
                    } else {
                        wBEOB.setTotalSubBal(wBEOB.getTotalSubBal() + 0.0D);
                    }

                    this.subventionMap.put(s.getSubvention().getId(), wWSB);
                }
            }

            wListPrev = null;
            wSubventionList = getWageSummaryBeanFromMap(this.subventionMap, wSubventionList, false);
            Collections.sort(wSubventionList);
            wBEOB.setSubventionList(wSubventionList);

            List<WageSummaryBean> wDedList = new ArrayList<WageSummaryBean>();
            serialNum++;
            wPayee.setSerialNum(serialNum);
            serialNum++;
            wTotalDeductions.setSerialNum(serialNum);

            serialNum++;
            wTotalLoans.setSerialNum(serialNum);
//        if (!(isPensioner())){
            serialNum++;
            wPensions.setSerialNum(serialNum);
//        }

            if (wDevLevyDeducted) {
                serialNum++;
                wDevLevy.setSerialNum(serialNum);
            }

            wDedList.add(wPayee);
            wDedList.add(wTotalDeductions);

            wDedList.add(wTotalLoans);
//        if (!(isPensioner())) {
            wDedList.add(wPensions);
//        }
            if (wDevLevyDeducted) {
                wDedList.add(wDevLevy);
            }

            Collections.sort(wDedList);
            wBEOB.setDeductionList(wDedList);

//       if(bc.isPensioner(){
//            wBEOB.setTotalCurrOutGoing(wBEOB.getTotalCurrBal());
//            wBEOB.setTotalPrevOutGoing(wBEOB.getTotalPrevBal());
//        }
//        else{
            wBEOB.setTotalCurrOutGoing(wBEOB.getTotalCurrBal() + wBEOB.getTotalSubBal() + wBEOB.getTotalCurrCont());
            wBEOB.setTotalPrevOutGoing(wBEOB.getTotalPrevBal() + wBEOB.getTotalPrevSubBal() + wBEOB.getTotalPrevCont());
//        }

            wBEOB.setGrandTotal(wBEOB.getTotalCurrBal() - wBEOB.getTotalDedBal());
            wBEOB.setGrandPrevTotal(wBEOB.getTotalPrevBal() - wBEOB.getTotalPrevDedBal());


            wRetList = setMaterialityLevelIndicator(request, wRetList, bc.getLoginId());
            Collections.sort(wRetList);
            wBEOB.setWageSummaryBeanList(wRetList);
            wBEOB.setRunMonth(pRunMonth);
            wBEOB.setRunYear(pRunYear);
            wBEOB.setFromDate(LocalDate.now());
            wBEOB.setToDate(LocalDate.now());
            wWBList.add(wBEOB);
        }
        gBean.setFromDateStr(PayrollHRUtils.getFullDateFormat().format(sDate));
        gBean.setToDateStr(PayrollHRUtils.getFullDateFormat().format(eDate));
        gBean.setBeanList(wWBList);
        model.addAttribute("miniBean", gBean);
        addRoleBeanToModel(model, request);

        return VIEW_NAME;
    }


    private List<WageSummaryBean> setMaterialityLevelIndicator(HttpServletRequest request, List<WageSummaryBean> pRetList,  Long pLoginId)
            throws InstantiationException, IllegalAccessException  {

        BusinessCertificate bc = this.getBusinessCertificate(request);
//    Materiality wMateriality = (Materiality) payrollService.loadObjectByClassAndKeyValue(Materiality.class, "login.id",pLoginId);
        Materiality wMateriality = this.genericService.loadObjectUsingRestriction(Materiality.class, Arrays.asList(
                CustomPredicate.procurePredicate("user.id",pLoginId)));

        if (wMateriality.isNewEntity()) {
            return pRetList;
        }
        double wActMat = wMateriality.getValue();

        if (wActMat < 10.0D) {
            return pRetList;
        }
        List<WageSummaryBean> wRetList = new ArrayList<>();
        for (WageSummaryBean w : pRetList) {
            w.setMaterialityThreshold((w.getPreviousBalance() > 0.0D) && (w.getCurrentBalance() - w.getPreviousBalance() >= wActMat));

            wRetList.add(w);
        }

        return wRetList;
    }
    private List<WageSummaryBean> getWageSummaryBeanFromMap(HashMap<Long, WageSummaryBean> mdasMap2, List<WageSummaryBean> pRetList, boolean pSetDisplay)
    {
        Set<Map.Entry<Long,WageSummaryBean>> set = mdasMap2.entrySet();
        Iterator<Map.Entry<Long, WageSummaryBean>> i = set.iterator();

        while (i.hasNext()) {
            Map.Entry<Long,WageSummaryBean> me = i.next();

            if (pSetDisplay)
            {
                me.getValue().setDisplayStyle("reportOdd");
            }

            if (me.getValue().isSubvention()) {
                if ((me.getValue().getCurrentBalance() == 0.0D) && (me.getValue().getPreviousBalance() == 0.0D))
                    continue;
                pRetList.add(me.getValue());
            } else {
                pRetList.add(me.getValue());
            }

        }

        return pRetList;
    }
    @RequestMapping(method={RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep, @RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("miniBean") WageBeanContainer pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);


        if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
            return "redirect:reportsOverview.do";
        }


        if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {

            if (pLPB.getFromDate() == null)
            {
                result.rejectValue("", "InvalidValue", "Please select a Payroll 'From' Date");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("miniBean", pLPB);

                return VIEW_NAME;
            }
            if(pLPB.getToDate() == null)
            {
                result.rejectValue("", "InvalidValue", "Please select a Payroll 'To' Date");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("miniBean", pLPB);

                return VIEW_NAME;
            }
            if((pLPB.getFromDate() == null) && (pLPB.getToDate() == null)){
                result.rejectValue("", "InvalidValue", "Please select Payroll Dates");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("miniBean", pLPB);

                return VIEW_NAME;
            }
            LocalDate date1 = (pLPB.getFromDate());
            LocalDate date2 = (pLPB.getToDate());

            String fDate = PayrollBeanUtils.getJavaDateAsString(date1);
            String tDate = PayrollBeanUtils.getJavaDateAsString(date2);

            if (date1.isAfter(date2)) {
                result.rejectValue("", "InvalidValue", "'From' Date can not be greater than 'To' Date ");

                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);

                model.addAttribute("status", result);
                model.addAttribute("miniBean", pLPB);

                return VIEW_NAME;
            }
            if ((date1.isAfter(LocalDate.now())) || (date1.equals(LocalDate.now()))) {
                result.rejectValue("", "InvalidValue", "'From' Date must be in the Past");

                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);

                model.addAttribute("status", result);
                model.addAttribute("miniBean", pLPB);

                return VIEW_NAME;
            }
            if (date1.getYear()!= date2.getYear()) {
                result.rejectValue("", "InvalidValue", "'From' and 'To' Dates must be in the same year.");

                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);

                model.addAttribute("status", result);
                model.addAttribute("miniBean", pLPB);

                return VIEW_NAME;
            }

            return "redirect:allBusinessExecSummary.do?fd=" +fDate + "&td=" + tDate;
        }

        return "redirect:allBusinessExecSummary.do";
    }

}
