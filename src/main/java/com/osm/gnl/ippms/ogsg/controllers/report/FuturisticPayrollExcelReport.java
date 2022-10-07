package com.osm.gnl.ippms.ogsg.controllers.report;


import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.SimulationService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.FuturePayrollExcelGenerator;
import com.osm.gnl.ippms.ogsg.domain.simulation.FuturePaycheckBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.FuturePaycheckMaster;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.WageBeanContainer;
import com.osm.gnl.ippms.ogsg.report.beans.WageSummaryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.*;

@Controller
public class FuturisticPayrollExcelReport extends BaseController {

    private HashMap<Long, WageSummaryBean> ministryMap;

    private HashMap<Long, WageSummaryBean> garnMap;

    private HashMap<Long, WageSummaryBean> subventionMap;
    private HashMap<Long, SalaryInfo> salaryInfoMap;

    @Autowired
    SimulationService simulationService;

    private String lastDisplayStyle;

    @RequestMapping({"/futurePayrollSummaryByMDAPExcel.do"})
    public void setupForm(@RequestParam("pid") Long pPid, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        FuturePayrollExcelGenerator futurePayrollExcelGenerator = new FuturePayrollExcelGenerator();

        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);
        init();
        WageBeanContainer wBEOB = new WageBeanContainer();
        FuturePaycheckMaster wFPM = this.genericService.loadObjectUsingRestriction(FuturePaycheckMaster.class,
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), CustomPredicate.procurePredicate("id", pPid)));

        wBEOB.setName(wFPM.getName());

       PayrollFlag pf = IppmsUtilsExt.getPayrollFlagForClient(genericService,bc);

        LocalDate startDate = null;
        LocalDate endDate = null;

        if (!pf.isNewEntity()) {
            startDate = pf.getPayPeriodStart();
            endDate = pf.getPayPeriodEnd();
        } else {
            ArrayList<LocalDate> list = PayrollBeanUtils.getDefaultCheckRegisterDates();
            startDate = list.get(0);
            endDate = list.get(1);
        }
        LocalDate fDate = PayrollBeanUtils.setDateFromString(PayrollBeanUtils.getJavaDateAsString(startDate));
        LocalDate tDate = PayrollBeanUtils.setDateFromString(PayrollBeanUtils.getJavaDateAsString(endDate));

        wBEOB.setShowUnionDues(PayrollBeanUtils.isUnionDuesDeducted(tDate));

        setControlMaps(bc);


        LocalDate wCal = LocalDate.of(wFPM.getSimulationYear(), wFPM.getSimulationMonth(), 1);

        wBEOB.setMonthAndYearStr(PayrollHRUtils.getMonthYearDateFormat().format(wCal));

        //setting previous month
        int pMonth = wFPM.getSimulationMonth() - 1;
        if(pMonth == 0){
            pMonth = 12;
        }
        LocalDate pCal = LocalDate.of(wFPM.getSimulationYear(), pMonth, 1);
        wBEOB.setPrevMonthAndYearStr(PayrollHRUtils.getMonthYearDateFormat().format(pCal));

        double wTotalCurrPay = 0.0D;
        double wTotalPrevPay = 0.0D;
        int serialNum = 0;
        WageSummaryBean wNHFBean = new WageSummaryBean();
        wNHFBean.setName("N.H.F Deductions");
        WageSummaryBean wUnionDues = new WageSummaryBean();
        wUnionDues.setName("Pooled Union Dues");
        WageSummaryBean wPayee = new WageSummaryBean();
        wPayee.setName("PAYE");

        List<EmployeePayBean> wEPBList = this.simulationService.loadEmployeePayBeanByParentIdFromDateToDate(bc, fDate);
        wBEOB.setTotalNoOfEmp(wEPBList.size());
        for (EmployeePayBean e : wEPBList)
        {
            wUnionDues.setCurrentBalance(wUnionDues.getCurrentBalance() + e.getUnionDues());
            wNHFBean.setCurrentBalance(wNHFBean.getCurrentBalance() + e.getNhf());
            wPayee.setCurrentBalance(wPayee.getCurrentBalance() + e.getTaxesPaid());

            wBEOB.setTotalDedBal(wBEOB.getTotalDedBal() + e.getUnionDues() + e.getNhf() + e.getTaxesPaid());
            wTotalCurrPay += e.getNetPay();
            WageSummaryBean wWSB = null;







            if (this.ministryMap.containsKey(e.getMdaDeptMap().getMdaInfo().getId())) {
                wWSB = this.ministryMap.get(e.getMdaDeptMap().getMdaInfo().getId());
            }
            else {
                wWSB = new WageSummaryBean();

                serialNum++; wWSB.setSerialNum(serialNum);
                wWSB.setAssignedToObject(e.getMdaDeptMap().getMdaInfo().getName());
            }

            wWSB.setCurrentBalance(wWSB.getCurrentBalance() + e.getNetPay());
            wWSB.setNoOfEmp(wWSB.getNoOfEmp() + 1);
            this.ministryMap.put(e.getMdaDeptMap().getMdaInfo().getId(), wWSB);

        }

        wEPBList = null;
        List<FuturePaycheckBean> wEPBListOld = this.genericService.loadAllObjectsUsingRestrictions(FuturePaycheckBean.class,
                Arrays.asList(CustomPredicate.procurePredicate("futurePaycheckMaster.businessClientId", bc.getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("futurePaycheckMaster.id", pPid)), null);


        for (FuturePaycheckBean e : wEPBListOld)
        {
            if (e.getNetPay() == 0.0D)
                continue;
            Long wKey = e.getMdaInfo().getId();

            wTotalPrevPay += e.getNetPay();
            wUnionDues.setPreviousBalance(wUnionDues.getPreviousBalance() + e.getUnionDues());
            wNHFBean.setPreviousBalance(wNHFBean.getPreviousBalance() + e.getNhf());
            wPayee.setPreviousBalance(wPayee.getPreviousBalance() + e.getTaxesPaid());
            wBEOB.setTotalPrevDedBal(wBEOB.getTotalPrevDedBal() + e.getUnionDues() + e.getNhf() + e.getTaxesPaid());
            wBEOB.setTotalNoOfPrevMonthEmp(wBEOB.getTotalNoOfPrevMonthEmp() + 1);
            WageSummaryBean wWSB = null;

            //-- All the WageSummaryBean should exist prior....

            wWSB = this.ministryMap.get(wKey);

            if(wWSB == null)
                throw new Exception("Unknown MDA - ID "+wKey.toString()+" "+this.getClass().getName());
            wWSB.setPreviousNoOfEmp(wWSB.getPreviousNoOfEmp() + 1);
            wWSB.setPreviousBalance(wWSB.getPreviousBalance() + e.getNetPay());

            this.ministryMap.put(wKey, wWSB);
        }



        wEPBListOld = null;
        List<WageSummaryBean> wRetList = new ArrayList<WageSummaryBean>();

        wRetList = getWageSummaryBeanFromMap(this.ministryMap, wRetList, true);


        wBEOB.setTotalCurrBal(wTotalCurrPay);
        wBEOB.setTotalPrevBal(wTotalPrevPay);

        List<WageSummaryBean> wDedList = new ArrayList<WageSummaryBean>();
        serialNum++; wPayee.setSerialNum(serialNum);
        serialNum++; wNHFBean.setSerialNum(serialNum);
        if (wBEOB.isShowUnionDues()) {
            serialNum++; wUnionDues.setSerialNum(serialNum);
        }

        wDedList = getWageSummaryBeanFromMap(this.garnMap, wDedList, false);
        wDedList.add(wPayee);
        wDedList.add(wNHFBean);
        if (wBEOB.isShowUnionDues()) {
            wDedList.add(wUnionDues);
        }
        Collections.sort(wDedList);
        wBEOB.setDeductionList(wDedList);

        wBEOB.setGrandTotal(wBEOB.getTotalCurrBal() + wBEOB.getTotalDedBal() + wBEOB.getTotalSubBal());
        wBEOB.setGrandPrevTotal(wBEOB.getTotalPrevBal() + wBEOB.getTotalPrevDedBal() + wBEOB.getTotalPrevSubBal());
        wBEOB.setFromDate(fDate);
        wBEOB.setToDate(tDate);
        Collections.sort(wRetList);
        wBEOB.setWageSummaryBeanList(wRetList);

        futurePayrollExcelGenerator.generateExcel(wBEOB, "futuristic_payroll_simulation", response, bc);
//        return new ModelAndView("mdapFutureExcelSummary", (String) super.getSessionId(request), wBEOB);
    }

    private void init()
    {
        this.ministryMap = new HashMap<Long,WageSummaryBean>();

        this.garnMap = new HashMap<Long,WageSummaryBean>();
        this.subventionMap = new HashMap<Long,WageSummaryBean>();

        this.salaryInfoMap = new HashMap<Long, SalaryInfo>();
    }

    private void setControlMaps(BusinessCertificate bc) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.salaryInfoMap = this.genericService.loadObjectAsMapWithConditions(SalaryInfo.class, Arrays.asList(
                CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId())), "id");

    }

    private List<WageSummaryBean> getWageSummaryBeanFromMap(
            HashMap<Long, WageSummaryBean> mdaMap2, List<WageSummaryBean> pRetList,boolean pSetDisplay) {


        Set<Map.Entry<Long,WageSummaryBean>> set = mdaMap2.entrySet();
        Iterator<Map.Entry<Long, WageSummaryBean>> i = set.iterator();

        while(i.hasNext()){
            Map.Entry<Long,WageSummaryBean> me = i.next();

            if (pSetDisplay) {
                //if ((wSize % 2) == 1) {
                me.getValue().setDisplayStyle("reportEven");
                lastDisplayStyle = "reportEven";
            } else {
                me.getValue().setDisplayStyle("reportOdd");
                lastDisplayStyle = "reportOdd";
            }

            pRetList.add(me.getValue());
            // wSize++;
        }

        return pRetList;
    }



}
