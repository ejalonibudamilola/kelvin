package com.osm.gnl.ippms.ogsg.controllers.simulation;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.SimulationService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.simulation.FuturePaycheckBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.FuturePaycheckMaster;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
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


@Controller
@RequestMapping("/showFuturePayrollResult.do")
@SessionAttributes(types = WageBeanContainer.class)
public class FuturisticWageSummaryReportController extends BaseController {


    private static final String REQUEST_CANCEL = "_cancel";

    @Autowired
    private SimulationService simulationService;
    //Lists and Maps to help with Organization

    private HashMap<Long, String> mdasList;


    private HashMap<Long, WageSummaryBean> garnMap;
    private HashMap<Long, WageSummaryBean> mdasWageBeanMap;


    //HashMaps to hold the Map ID and the Actual Org Id.
    private HashMap<Long, Long> mdaDeptMap;


    public FuturisticWageSummaryReportController() {
    }

    //Initialize all Maps...
    private void init() {


        this.mdasList = new HashMap<Long, String>();


        this.mdaDeptMap = new HashMap<Long, Long>();

        this.mdasWageBeanMap = new HashMap<Long, WageSummaryBean>();

        this.garnMap = new HashMap<Long, WageSummaryBean>();


    }

    private void setControlMaps(HttpServletRequest request) {


        this.mdasList = makeMdaInfoHashMapFromList(getBusinessCertificate(request));


    }


    private HashMap<Long, String> makeMdaInfoHashMapFromList(BusinessCertificate businessCertificate) {

        List<MdaDeptMap> pList = genericService.loadAllObjectsWithSingleCondition(MdaDeptMap.class, CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()), null);
        HashMap<Long, String> wRetMap = new HashMap<Long, String>();

        for (MdaDeptMap p : pList) {
            this.mdaDeptMap.put(p.getId(), p.getMdaInfo().getId());
            if (wRetMap.containsKey(p.getMdaInfo().getId()))
                continue;
            wRetMap.put(p.getMdaInfo().getId(), p.getMdaInfo().getName());
        }

        return wRetMap;
    }


    private HashMap<Long, String> makeMdaInfoHashMapFromList(
            List<MdaDeptMap> pList) {
        HashMap<Long, String> wRetMap = new HashMap<Long, String>();

        for (MdaDeptMap p : pList) {
            this.mdaDeptMap.put(p.getId(), p.getMdaInfo().getId());
            if (wRetMap.containsKey(p.getMdaInfo().getId()))
                continue;
            wRetMap.put(p.getMdaInfo().getId(), p.getMdaInfo().getName());
        }

        return wRetMap;
    }


    
    @RequestMapping(method = RequestMethod.GET, params = {"pid"})
    public String setupForm(@RequestParam("pid") Long pPid, Model model, HttpServletRequest request) throws Exception {

        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        WageBeanContainer wBEOB = new WageBeanContainer();

        FuturePaycheckMaster wFPM = this.genericService.loadObjectById(FuturePaycheckMaster.class, pPid);

        wBEOB.setId(wFPM.getId());
        PayrollFlag pf = IppmsUtilsExt.getPayrollFlagForClient(genericService, bc);

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
		/*if(PayrollBeanUtils.isUnionDuesDeducted(tDate)){
			wBEOB.setShowUnionDues(true);
		}else{
			wBEOB.setShowUnionDues(false);
		}*/
        wBEOB.setShowUnionDues(false);
        //Set this so we have a handle to the begin dates as string.
        wBEOB.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(startDate));
        wBEOB.setToDateStr(PayrollBeanUtils.getJavaDateAsString(endDate));

        //Date wPrevMonthStart = PayrollBeanUtils.getPreviousMonthDate(fDate,false);
        //Date wPrevMonthEnd =  PayrollBeanUtils.getPreviousMonthDate(tDate,true);
        init();
        //Set control Maps.
        setControlMaps(request);

        List<EmployeePayBean> wEPBList = this.simulationService.loadEmployeePayBeanByParentIdFromDateToDate(bc, fDate);

        List<WageSummaryBean> wRetList = new ArrayList<WageSummaryBean>();
        LocalDate wCal = LocalDate.of(wFPM.getSimulationYear(), wFPM.getSimulationMonth(), 1);
        wBEOB.setMonthAndYearStr(PayrollHRUtils.getMonthYearDateFormat().format(wCal));

        wBEOB.setPrevMonthAndYearStr(PayrollHRUtils.getMonthYearDateFormat().format(fDate));

        double wTotalCurrPay = 0.0;
        double wTotalPrevPay = 0.0;
        int serialNum = 0;
        WageSummaryBean deductionBean = new WageSummaryBean();
        deductionBean.setName("Deductions");
        WageSummaryBean loans = new WageSummaryBean();
        loans.setName("Loans");
        WageSummaryBean wPayee = new WageSummaryBean();
        wPayee.setName("Taxes");
		/*WageSummaryBean wTws = new WageSummaryBean();
		wPayee.setName("TWS");*/


        wBEOB.setTotalNoOfEmp(wEPBList.size());
        for (EmployeePayBean e : wEPBList) {

            Long wKey = e.getMdaDeptMap().getId();

            loans.setCurrentBalance(loans.getCurrentBalance() + e.getTotalGarnishments());
            deductionBean.setCurrentBalance(deductionBean.getCurrentBalance() + e.getTotalDeductions());
            wPayee.setCurrentBalance(wPayee.getCurrentBalance() + e.getTaxesPaid());

            wBEOB.setTotalDedBal(wBEOB.getTotalDedBal() + e.getTotalGarnishments() + e.getTotalDeductions() + e.getTaxesPaid());
            wTotalCurrPay += e.getNetPay();
            WageSummaryBean wWSB = null;


            Long _wKey = this.mdaDeptMap.get(wKey);
            if (this.mdasWageBeanMap.containsKey(_wKey)) {
                wWSB = this.mdasWageBeanMap.get(_wKey);

            } else {
                wWSB = new WageSummaryBean();
                wWSB.setMdaDeptMapId(_wKey);
                wWSB.setObjectInd(e.getObjectInd());
                wWSB.setSerialNum(++serialNum);
                wWSB.setAssignedToObject(this.mdasList.get(_wKey));

            }
            wWSB.setCurrentBalance(wWSB.getCurrentBalance() + e.getNetPay());
            wWSB.setNoOfEmp(wWSB.getNoOfEmp() + 1);
            this.mdasWageBeanMap.put(_wKey, wWSB);


        }
        wEPBList = null;
        List<FuturePaycheckBean> wEPBListOld = genericService.loadAllObjectsWithSingleCondition(FuturePaycheckBean.class, CustomPredicate.procurePredicate("futurePaycheckMaster.id", pPid), null);

        for (FuturePaycheckBean e : wEPBListOld) {
            if (e.getNetPay() == 0)
                continue;

            Long wKey = e.getMdaInfo().getId();
            //int wCode = 0;//This points to whether it is M,B,A or P...
            wTotalPrevPay += e.getNetPay();
            loans.setPreviousBalance(loans.getPreviousBalance() + e.getTotalGarnishments());
            deductionBean.setPreviousBalance(deductionBean.getPreviousBalance() + e.getTotalDeductions());
            wPayee.setPreviousBalance(wPayee.getPreviousBalance() + e.getTaxesPaid());

            wBEOB.setTotalPrevDedBal(wBEOB.getTotalPrevDedBal() + e.getTotalGarnishments() + e.getTotalDeductions() + e.getTaxesPaid());
            wBEOB.setTotalNoOfPrevMonthEmp(wBEOB.getTotalNoOfPrevMonthEmp() + 1);
            WageSummaryBean wWSB = null;


            if (this.mdasWageBeanMap.containsKey(wKey)) {
                wWSB = this.mdasWageBeanMap.get(wKey);

            } else {
                wWSB = new WageSummaryBean();
                wWSB.setMdaDeptMapId(wKey);
                wWSB.setAssignedToObject(this.mdasList.get(wKey));

            }
            wWSB.setPreviousBalance(wWSB.getPreviousBalance() + e.getNetPay());
            wWSB.setPreviousNoOfEmp(wWSB.getPreviousNoOfEmp() + 1);
            this.mdasWageBeanMap.put(wKey, wWSB);


        }

        wRetList = getWageSummaryBeanFromMap(this.mdasWageBeanMap, wRetList, true);


        wBEOB.setTotalCurrBal(wTotalCurrPay);
        wBEOB.setTotalPrevBal(wTotalPrevPay);

        List<WageSummaryBean> wDedList = new ArrayList<WageSummaryBean>();
        wPayee.setSerialNum(++serialNum);
        deductionBean.setSerialNum(++serialNum);
        loans.setSerialNum(++serialNum);
        wDedList = getWageSummaryBeanFromMap(this.garnMap, wDedList, false);
        wDedList.add(wPayee);
        wDedList.add(deductionBean);

        wDedList.add(loans);

        Collections.sort(wDedList);
        wBEOB.setDeductionList(wDedList);

        wBEOB.setGrandTotal(wBEOB.getTotalCurrBal() + wBEOB.getTotalDedBal() + wBEOB.getTotalSubBal());
        wBEOB.setGrandPrevTotal(wBEOB.getTotalPrevBal() + wBEOB.getTotalPrevDedBal() + wBEOB.getTotalPrevSubBal());
        wBEOB.setFromDate(fDate);
        wBEOB.setToDate(tDate);

        Collections.sort(wRetList);
        wBEOB.setWageSummaryBeanList(wRetList);
        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", wBEOB);


        return "simulation/futuristicPayrollSummaryByMDAForm";

    }


    private List<WageSummaryBean> getWageSummaryBeanFromMap(
            HashMap<Long, WageSummaryBean> pObjectMap, List<WageSummaryBean> pRetList, boolean pSetDisplay) {


        Set<Entry<Long, WageSummaryBean>> set = pObjectMap.entrySet();
        Iterator<Entry<Long, WageSummaryBean>> i = set.iterator();

        while (i.hasNext()) {
            Entry<Long, WageSummaryBean> me = i.next();

            if (pSetDisplay) {

                me.getValue().setDisplayStyle("reportOdd");

            }
            pRetList.add(me.getValue());

        }

        return pRetList;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(@RequestParam(value = REQUEST_CANCEL, required = false) String cancel, @ModelAttribute("miniBean") WageBeanContainer pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);


        return REDIRECT_FUT_SIM_DASHBOARD;

    }


}
