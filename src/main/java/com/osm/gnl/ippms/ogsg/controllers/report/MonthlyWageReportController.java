package com.osm.gnl.ippms.ogsg.controllers.report;

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
import java.util.Map.Entry;

/**
 * @author Damilola Ejalonibu
 */


@Controller
@RequestMapping({"/viewWageSummary.do"})
@SessionAttributes(types = {WageBeanContainer.class})
public class MonthlyWageReportController extends BaseController {


    private final PaycheckService paycheckService;
    private final EmployeeService employeeService;

    private final String VIEW_NAME = "report/payrollSummaryByMDAPForm";
    private HashMap<Long, WageSummaryBean> mdasMap;
    private HashMap<Long, WageSummaryBean> subventionMap;

    @Autowired
    public MonthlyWageReportController(PaycheckService paycheckService, EmployeeService employeeService) {
        this.paycheckService = paycheckService;
        this.employeeService = employeeService;
    }


    @ModelAttribute("monthList")
    protected List<NamedEntity> getMonthsList() {
        return PayrollBeanUtils.makeAllMonthList();
    }

    @ModelAttribute("yearList")
    protected Collection<NamedEntity> getYearList(HttpServletRequest request) {

        return paycheckService.makePaycheckYearList(getBusinessCertificate(request));
    }

    private void init() {

        this.mdasMap = new HashMap<>();

        this.subventionMap = new HashMap<>();
    }


    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        PayrollFlag pf = null;

        LocalDate endDate = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);

        if (endDate == null) {

            pf = this.genericService.loadObjectWithSingleCondition(PayrollFlag.class,getBusinessClientIdPredicate(request));
            if (!pf.isNewEntity()) {

                endDate = pf.getPayPeriodEnd();
            } else {
                ArrayList<LocalDate> list = PayrollBeanUtils.getDefaultCheckRegisterDates();

                endDate = list.get(1);

            }
        }


        return doWork(endDate.getMonthValue(), endDate.getYear(), model, request, 0);
    }


    @RequestMapping(method = {RequestMethod.GET}, params = {"rm", "ry"})
    public String setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        return doWork(pRunMonth, pRunYear, model, request, 0);
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"rm", "ry", "sl"})
    public String setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, @RequestParam("sl") int pShowDiffInd, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        return doWork(pRunMonth, pRunYear, model, request, pShowDiffInd);
    }


    private String doWork(int pRunMonth, int pRunYear, Model model, HttpServletRequest request, int pShowDiffInd) throws Exception {


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



        List<WageSummaryBean> wRetList = new ArrayList<>();


        LocalDate wCheckDate;
        wCheckDate = wPrevMonthEnd;

        wBEOB.setShowUnionDues(PayrollBeanUtils.isUnionDuesDeducted(wCheckDate));


        wBEOB.setMonthAndYearStr(PayrollHRUtils.getMonthYearDateFormat().format(fDate));
        wBEOB.setPrevMonthAndYearStr(PayrollHRUtils.getMonthYearDateFormat().format(wPrevMonthStart));

        double wTotalCurrPay = 0.0D;
        double wTotalPrevPay = 0.0D;
        int serialNum = 0;
        WageSummaryBean wTotalDeductions = new WageSummaryBean("Other Deductions");
        WageSummaryBean wTotalLoans = new WageSummaryBean("Total Loans");
        WageSummaryBean wPayee = new WageSummaryBean("Total Taxes");
        WageSummaryBean wPensions = new WageSummaryBean("Contributory Pensions ("+ bc.getStaffTypeName() +" )");
        WageSummaryBean wPensionsEmployer = new WageSummaryBean("Contributory Pensions (Employer)");
        WageSummaryBean wUnionDues = null;
        if(bc.isSubeb() || bc.isPensioner()){
            wUnionDues = new WageSummaryBean("Union Dues");
        }

        WageSummaryBean wRBA = new WageSummaryBean();

        PayrollRunMasterBean wPRMB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("runMonth", pRunMonth), CustomPredicate.procurePredicate("runYear", pRunYear)));

        if (wPRMB.getRbaPercentage() > 0)
            wRBA.setName("Redemption Bond Account ( " + wPRMB.getRbaPercentageStr() + " Gross Pay)");

        boolean wDevLevyDeducted = false;

        LocalDate wCal = wPrevMonthEnd;


        PayrollRunMasterBean wPRMBPrev = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
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
        List<EmployeePayBean> wEPBList = this.paycheckService.loadEmployeePayBeanByParentIdFromDateToDate(bc.getBusinessClientInstId(), pRunMonth, pRunYear, bc,null,false);
        wBEOB.setTotalNoOfEmp(wEPBList.size());
        HashMap<Long, Double> wDeductionMap = employeeService.loadDeductionsForActiveEmployees(fDate, bc);
        Long wKey;
        double totalDeductions;
        for (EmployeePayBean e : wEPBList) {
            wKey = e.getMdaDeptMap().getMdaInfo().getId();

            if (wDeductionMap.containsKey(e.getEmployee().getId())) {
                totalDeductions = wDeductionMap.get(e.getEmployee().getId()).doubleValue();
                if(bc.isSubeb()) {
                    wUnionDues.setCurrentBalance(wUnionDues.getCurrentBalance() + e.getUnionDues());
                    totalDeductions -= e.getUnionDues();
                }
                wTotalDeductions.setCurrentBalance(wTotalDeductions.getCurrentBalance() + totalDeductions);
            }
           if(!bc.isPensioner()) {
               wPayee.setCurrentBalance(wPayee.getCurrentBalance() + e.getTaxesPaid());
               wTotalLoans.setCurrentBalance(wTotalLoans.getCurrentBalance() + e.getTotalGarnishments());
               wPensions.setCurrentBalance(wPensions.getCurrentBalance() + e.getContributoryPension());
               wPensionsEmployer.setCurrentBalance(wPensionsEmployer.getCurrentBalance() + e.getContributoryPension());
               if ((wDevLevyDeducted) && (e.getDevelopmentLevy() > 0.0D)) {
                   wDevLevy.setCurrentBalance(wDevLevy.getCurrentBalance() + e.getDevelopmentLevy());
               }
           }else{
               wUnionDues.setCurrentBalance(wUnionDues.getCurrentBalance() + e.getUnionDues());
           }
            wBEOB.setTotalDedBal(wBEOB.getTotalDedBal() + e.getTotalDeductions());

            wTotalCurrPay += e.getTotalPay();
            WageSummaryBean wWSB;


            if (this.mdasMap.containsKey(wKey)) {
                wWSB = this.mdasMap.get(wKey);
            } else {
                wWSB = new WageSummaryBean();
                wWSB.setMdaDeptMapId(wKey);
                wWSB.setObjectInd(1);
                serialNum++;
                wWSB.setSerialNum(serialNum);
                wWSB.setAssignedToObject(e.getMdaDeptMap().getMdaInfo().getName());
                wWSB.setId(e.getMdaDeptMap().getMdaInfo().getId());

            }

            wWSB.setCurrentBalance(wWSB.getCurrentBalance() + e.getTotalPay());
            wWSB.setNoOfEmp(wWSB.getNoOfEmp() + 1);
            this.mdasMap.put(wKey, wWSB);

        }

        List<EmployeePayBean> wEPBListOld = this.paycheckService.loadEmployeePayBeanByParentIdFromDateToDate(bc.getBusinessClientInstId(), fPrevDate.getMonthValue(), fPrevDate.getYear(), bc,null,false);

        wBEOB.setTotalNoOfPrevMonthEmp(wEPBListOld.size());
        wDeductionMap = employeeService.loadDeductionsForActiveEmployees(wCal, bc);
        for (EmployeePayBean e : wEPBListOld) {
             wKey = e.getMdaDeptMap().getMdaInfo().getId();

            wTotalPrevPay += e.getTotalPay();

            if (wDeductionMap.containsKey(e.getEmployee().getId())) {
                totalDeductions = wDeductionMap.get(e.getEmployee().getId()).doubleValue();
                if(bc.isSubeb()) {
                    wUnionDues.setPreviousBalance(wUnionDues.getPreviousBalance() + e.getUnionDues());;
                    totalDeductions -= e.getUnionDues();
                }
                wTotalDeductions.setPreviousBalance(wTotalDeductions.getPreviousBalance() + totalDeductions);
            }
            if(!bc.isPensioner()) {
                wPayee.setPreviousBalance(wPayee.getPreviousBalance() + e.getTaxesPaid());
                wTotalLoans.setPreviousBalance(wTotalLoans.getPreviousBalance() + e.getTotalGarnishments());
                wPensions.setPreviousBalance(wPensions.getPreviousBalance() + e.getContributoryPension());
                wPensionsEmployer.setPreviousBalance(wPensionsEmployer.getPreviousBalance() + e.getContributoryPension());

                if ((wDevLevyDeducted) && (e.getDevelopmentLevy() > 0.0D)) {
                    wDevLevy.setPreviousBalance(wDevLevy.getPreviousBalance() + e.getDevelopmentLevy());
                }
            }else{
                wUnionDues.setPreviousBalance(wUnionDues.getPreviousBalance() + e.getUnionDues());
            }
            wBEOB.setTotalPrevDedBal(wBEOB.getTotalPrevDedBal() + e.getTotalDeductions());

            WageSummaryBean wWSB;


            if (this.mdasMap.containsKey(wKey)) {
                wWSB = this.mdasMap.get(wKey);
            } else {
                wWSB = new WageSummaryBean();
                wWSB.setObjectInd(1);
                wWSB.setMdaDeptMapId(wKey);
                serialNum++;
                wWSB.setSerialNum(serialNum);
                wWSB.setAssignedToObject(e.getMdaDeptMap().getMdaInfo().getName());
            }

            wWSB.setPreviousBalance(wWSB.getPreviousBalance() + e.getTotalPay());
            wWSB.setPreviousNoOfEmp(wWSB.getPreviousNoOfEmp() + 1);
          //  wWSB.setNetDifference(wWSB.getCurrentBalance() - wWSB.getPreviousBalance());
            this.mdasMap.put(wKey, wWSB);
        }


        wRetList = getWageSummaryBeanFromMap(this.mdasMap, wRetList, true);


        Collections.sort(wRetList, Comparator.comparing(WageSummaryBean::getAssignedToObject));



        wBEOB.setTotalCurrBal(wTotalCurrPay);
        wBEOB.setTotalPrevBal(wTotalPrevPay);


        List<WageSummaryBean> wContList = new ArrayList<>();
        serialNum++;
        if(!bc.isPensioner()){
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
        }


        LocalDate wEnd;
        wEnd = (wPrevMonthEnd);

        List<WageSummaryBean> wSubventionList = new ArrayList<>();
        List<SubventionHistory> wListCurrent = this.genericService.loadAllObjectsUsingRestrictions(SubventionHistory.class,
                Arrays.asList(CustomPredicate.procurePredicate("subvention.businessClientId", bc.getBusinessClientInstId()),CustomPredicate.procurePredicate("runMonth", pRunMonth), CustomPredicate.procurePredicate("runYear", pRunYear)), null);
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
        List<SubventionHistory> wListPrev = this.genericService.loadAllObjectsUsingRestrictions(SubventionHistory.class,
                Arrays.asList(CustomPredicate.procurePredicate("runMonth", wEnd.getMonthValue()),CustomPredicate.procurePredicate("subvention.businessClientId", bc.getBusinessClientInstId()),
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
                //wBEOB.setNetDifference(wBEOB.getCurrentBalance() - wBEOB.getPreviousBalance());
                this.subventionMap.put(s.getSubvention().getId(), wWSB);
            }
        }

        wSubventionList = getWageSummaryBeanFromMap(this.subventionMap, wSubventionList, false);
        Collections.sort(wSubventionList);
        wBEOB.setSubventionList(wSubventionList);

        List<WageSummaryBean> wDedList = new ArrayList<>();

        if(!bc.isPensioner()){
            wPayee.setSerialNum(++serialNum);
            wTotalDeductions.setSerialNum(++serialNum);
            if(bc.isSubeb())
                wUnionDues.setSerialNum(++serialNum);
            wTotalLoans.setSerialNum(++serialNum);
            wPensions.setSerialNum(++serialNum);


            if (wDevLevyDeducted)
                wDevLevy.setSerialNum(++serialNum);


            wDedList.add(wPayee);
            wDedList.add(wTotalDeductions);
            if(bc.isSubeb())
                wDedList.add(wUnionDues);

            wDedList.add(wTotalLoans);
            if (!(bc.isPensioner()))
                wDedList.add(wPensions);

            if (wDevLevyDeducted)
                wDedList.add(wDevLevy);


        }else{
            wTotalDeductions.setSerialNum(++serialNum);
            wDedList.add(wTotalDeductions);
            wUnionDues.setSerialNum(++serialNum);
            wDedList.add(wUnionDues);
        }


        Collections.sort(wDedList);
        wBEOB.setDeductionList(wDedList);


       wBEOB.setTotalCurrOutGoing(wBEOB.getTotalCurrBal() + wBEOB.getTotalSubBal() + wBEOB.getTotalCurrCont());
       wBEOB.setTotalPrevOutGoing(wBEOB.getTotalPrevBal() + wBEOB.getTotalPrevSubBal() + wBEOB.getTotalPrevCont());

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

    private List<WageSummaryBean> setMaterialityLevelIndicator(HttpServletRequest request, List<WageSummaryBean> pRetList, Long pLoginId)
            throws InstantiationException, IllegalAccessException {

        BusinessCertificate bc = this.getBusinessCertificate(request);
        Materiality wMateriality = this.genericService.loadObjectUsingRestriction(Materiality.class, Arrays.asList(
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("user.id", pLoginId)));

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

    private List<WageSummaryBean> getWageSummaryBeanFromMap(HashMap<Long, WageSummaryBean> mdasMap2, List<WageSummaryBean> pRetList, boolean pSetDisplay) {
        Set<Entry<Long, WageSummaryBean>> set = mdasMap2.entrySet();
        Iterator<Entry<Long, WageSummaryBean>> i = set.iterator();

        while (i.hasNext()) {
            Entry<Long, WageSummaryBean> me = i.next();

            if (pSetDisplay) {
                me.getValue().setDisplayStyle("reportOdd");
            }

            if (me.getValue().isSubvention()) {
                if ((me.getValue().getCurrentBalance() == 0.0D) && (me.getValue().getPreviousBalance() == 0.0D))
                    continue;
                else
                    pRetList.add(me.getValue());
            } else {
                pRetList.add(me.getValue());
            }

        }

        return pRetList;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_updateReport", required = false) String updRep, @RequestParam(value = "_cancel", required = false) String cancel, @ModelAttribute("miniBean") WageBeanContainer pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return "redirect:reportsOverview.do";
        }


        if (isButtonTypeClick(request, REQUEST_PARAM_UPDATE_REPORT)) {
            if (pLPB.getRunMonth() == -1) {
                result.rejectValue("", "InvalidValue", "Please select a Payroll Month");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("miniBean", pLPB);

                return VIEW_NAME;
            }
            if (pLPB.getRunYear() == -1) {
                result.rejectValue("", "InvalidValue", "Please select a Payroll Year");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("miniBean", pLPB);

                return VIEW_NAME;
            }
            return "redirect:viewWageSummary.do?rm=" + pLPB.getRunMonth() + "&ry=" + pLPB.getRunYear() + "&sl=" + pLPB.getShowDiffInd();
        }

        return "redirect:viewWageSummary.do";
    }
}