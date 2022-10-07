package com.osm.gnl.ippms.ogsg.controllers.executive;


import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.simulation.*;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.WageBeanContainer;
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
@RequestMapping({"/allBusinessExecSummary.do"})
@SessionAttributes(types={WageBeanContainer.class})
public class ExecutivePayrollSummaryController extends BaseController {

    @Autowired
    private PaycheckService paycheckService;

    @Autowired
    private EmployeeService employeeService;


    private HashMap<Long, HashMap<Integer, SimulationMiniBean>> mdaMap;

    private List<SimulationMiniBean> deductionsTotalList;

    private List<SimulationMiniBean> subventionsTotalList;

    private List<SimulationMiniBean> contributionsTotalList;

    private List<SimulationMiniBean> totalsMap;

    private List<SimulationMiniBean> grandTotalsMap;

    private List<SimulationMiniBean> headerMap;
    private HashMap<Integer,SimulationMiniBean> payeeMap;
    private HashMap<Integer,SimulationMiniBean> nhfMap;
    private HashMap<Integer,SimulationMiniBean> unionMap;
    private HashMap<Integer,SimulationMiniBean> twsMap;
    private HashMap<Integer,SimulationMiniBean> ltgMap;

    //contributions Map
    private HashMap<Integer,SimulationMiniBean> contributoryMap;
    private HashMap<Integer,SimulationMiniBean> redemptionBondMap;

    //Subventions Map
    private HashMap<Integer,SimulationMiniBean> salariesOnGenMap;


    private HashMap<Integer,SimulationMiniBean> devLevyMap;

    //deductions
    private  final Integer PAYE = 1;
    private  final Integer NHF = 2;
    private  final Integer UNION = 3;
    private  final Integer TWS = 4;
    private  final Integer LTG = 5;
    private  final Integer DEVLEVY = 6;

    //contributions
    private final Integer ContributoryPensions = 1;
    private final Integer redemptionBond = 2;


    private String lastDisplayStyle;
    private int fStartMonth;

    private int totalNumOfMdas;

    private  int totalNumOfCont;

    private static final String VIEW_NAME = "report/payrollSummaryByClientForm";

    private void init(){

        this.deductionsTotalList = new ArrayList<SimulationMiniBean>();

        this.contributionsTotalList = new ArrayList<SimulationMiniBean>();

        this.grandTotalsMap = new ArrayList<SimulationMiniBean>();
        this.mdaMap = new HashMap<Long,HashMap<Integer,SimulationMiniBean>>();

        this.devLevyMap = new HashMap<Integer,SimulationMiniBean>();



        this.headerMap = new ArrayList<SimulationMiniBean>();


        this.totalsMap = new ArrayList<SimulationMiniBean>();
        this.contributoryMap = new HashMap<>();
        this.payeeMap = new HashMap<Integer,SimulationMiniBean>();
        this.nhfMap = new HashMap<Integer,SimulationMiniBean>();
        this.unionMap = new HashMap<Integer,SimulationMiniBean>();
        this.twsMap = new HashMap<Integer,SimulationMiniBean>();
        this.ltgMap = new HashMap<Integer,SimulationMiniBean>();
        this.devLevyMap = new HashMap<Integer,SimulationMiniBean>();
        this.redemptionBondMap = new HashMap<>();

        totalNumOfMdas = 0;

        totalNumOfCont = 0;

    }



    @RequestMapping(method={RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);

        init();

        BusinessCertificate bc = this.getBusinessCertificate(request);

        List<PayrollFlag> pfList =  this.genericService.loadAllObjectsWithoutRestrictions(PayrollFlag.class, "payPeriodStart");
        Comparator<PayrollFlag> comparator = Comparator.comparing(PayrollFlag::getId);

        Collections.sort(pfList, comparator.reversed());

        PayrollFlag pf = pfList.get(1);

        LocalDate endDate = null;
        LocalDate startDate = null;

        if (!pf.isNewEntity()) {

            endDate = pf.getPayPeriodEnd();
            startDate = pf.getPayPeriodStart();
        } else {
            ArrayList<LocalDate> list = PayrollBeanUtils.getDefaultCheckRegisterDates();

            endDate = list.get(1);
            startDate = list.get(0);
        }

        ArrayList<String> wGuideInfo = new ArrayList<String>();
        //First determine from when to when to load.....
            //Single Year...

            int wEndMonth = (endDate.getMonthValue());
                wGuideInfo.add(wEndMonth + ":" + endDate.getYear());

        String fDate = PayrollBeanUtils.getJavaDateAsString(startDate);
        String tDate = PayrollBeanUtils.getJavaDateAsString(endDate);



        return doWork(wGuideInfo, bc, model, request,  fDate, tDate);
    }


    @RequestMapping(method={RequestMethod.GET}, params={"fd", "td"})
    public String setupForm(@RequestParam("fd") String fromDate, @RequestParam("td") String toDate, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        init();

        BusinessCertificate bc = this.getBusinessCertificate(request);

        LocalDate date1 = PayrollBeanUtils.setDateFromString(fromDate);

        LocalDate date2 = PayrollBeanUtils.setDateFromString(toDate);

        int pStartDate = date1.getMonthValue();
        int pRunYear = date1.getYear();
        int pEndMonth = date2.getMonthValue();

        ArrayList<String> wGuideInfo = new ArrayList<String>();

        for (int wStart = pStartDate; wStart <= pEndMonth; wStart++) {
            wGuideInfo.add(wStart + ":" +pRunYear);
        }

        return doWork(wGuideInfo, bc, model, request, fromDate, toDate);
    }

    public String doWork(ArrayList<String> wGuideInfo, BusinessCertificate bc, Model model, HttpServletRequest request,
                         String fDate, String tDate) throws IllegalAccessException, InstantiationException {

        SimulationInfoContainer wSIC = new SimulationInfoContainer();

        Long pPid = 0L;
        pPid++;
        wSIC.setId(pPid);
        //Now iterate through our friends and make a container object.
        //Create the Header Beans...
        int wIndex = 1;

        for(String s : wGuideInfo){
            int wStartMonth = Integer.parseInt(s.substring(0,s.indexOf(":")));
            int wStartYear = Integer.parseInt(s.substring(s.indexOf(":")+ 1));

            SimulationMiniBean hb = new SimulationMiniBean();

            //Now load ALL SimulationInfo for this period...
            List<EmployeePayBean> wList = this.paycheckService.loadAllExecEmployeePayBeanByParentIdFromDateToDate(wStartMonth, wStartYear);


            PayrollRunMasterBean wPRMB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(
                    CustomPredicate.procurePredicate("runMonth", wStartMonth), CustomPredicate.procurePredicate("runYear", wStartYear)));

            createSimulationInfoSummaryBean(wList,wStartMonth,wStartYear,wIndex, wPRMB);

            hb.setIntegerId(new Integer(wIndex));
            hb.setName(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(wStartMonth, wStartYear));
            this.headerMap.add(hb);
            wIndex++;
        }


        //Now lets create the display beans....
        Collections.sort(headerMap);
        wSIC.setHeaderList(headerMap);

        //Now create mdapBeanList...
        List<SimulationMiniBean> wRetList = new ArrayList<SimulationMiniBean>();
        wRetList = getSimulationMiniBeanFromMap(this.mdaMap,wRetList,true);

        List<SimulationMiniBean> cRetList = new ArrayList<SimulationMiniBean>();
        List<SimulationInfoSummaryBean> cPayeeList = new ArrayList<SimulationInfoSummaryBean>();
        cRetList = getSimulationMiniBeanFromMap(this.mdaMap,cRetList,true);

        //Now before we sort make SimulationInfoSummaryBean....
        List<SimulationInfoSummaryBean> wMasterList = this.makeSimulationInfoSummaryBeanList(wRetList);

		/*for(SimulationInfoSummaryBean s : wMasterList){
			System.out.println(s.getAssignedToObject());
			for(SimulationMiniBean sb : s.getMiniBeanList()){
				System.out.println("ID = "+sb.getId()+": Parent Key = "+sb.getParentKey()+": Name = "+sb.getName()+": Value = "+sb.getCurrentValueStr());
			}
		}*/

        Collections.sort(wMasterList);

        wSIC.setSummaryBean(wMasterList);

        Collections.sort(wRetList);

        wSIC.setMdapList(wRetList);

        Collections.sort(this.totalsMap);

        wSIC.setMdapFooterList(this.totalsMap);

        Collections.sort(this.deductionsTotalList);

        wSIC.setDeductionsTotals(deductionsTotalList);
        wSIC.setContributionsTotals(contributionsTotalList);

        //Now Set Deductions....
        wRetList = null;
        wRetList = this.getDetailsFromMap(this.payeeMap, new ArrayList<SimulationMiniBean>(), true);

        List<SimulationInfoSummaryBean> wPayeeList = this.makeDeductionSimulationInfoSummaryBeanList(wRetList,new ArrayList<SimulationInfoSummaryBean>(),this.PAYE);

        wRetList = this.getDetailsFromMap(this.nhfMap,new ArrayList<SimulationMiniBean>(),true);

        wPayeeList = this.makeDeductionSimulationInfoSummaryBeanList(wRetList,wPayeeList,this.NHF);

        wRetList = this.getDetailsFromMap(this.unionMap,new ArrayList<SimulationMiniBean>(),true);

        wPayeeList = this.makeDeductionSimulationInfoSummaryBeanList(wRetList,wPayeeList,this.UNION);

        wRetList = this.getDetailsFromMap(this.twsMap,new ArrayList<SimulationMiniBean>(),true);

        wPayeeList = this.makeDeductionSimulationInfoSummaryBeanList(wRetList,wPayeeList,this.TWS);

        wRetList = this.getDetailsFromMap(this.ltgMap,new ArrayList<SimulationMiniBean>(),true);

        wPayeeList = this.makeDeductionSimulationInfoSummaryBeanList(wRetList,wPayeeList,this.LTG);

        wRetList = this.getDetailsFromMap(this.devLevyMap,new ArrayList<SimulationMiniBean>(),true);

        wPayeeList = this.makeDeductionSimulationInfoSummaryBeanList(wRetList,wPayeeList,this.DEVLEVY);

        Collections.sort(wPayeeList);

        wSIC.setDeductionsList(wPayeeList);

        //Now Set Contributions
        cRetList = this.getDetailsFromMap(this.contributoryMap, new ArrayList<SimulationMiniBean>(), true);

        cPayeeList = this.makeDeductionSimulationInfoSummaryBeanListCont(cRetList,new ArrayList<SimulationInfoSummaryBean>(),this.ContributoryPensions);

        cRetList = this.getDetailsFromMap(this.redemptionBondMap, new ArrayList<SimulationMiniBean>(), true);

        cPayeeList = this.makeDeductionSimulationInfoSummaryBeanListCont(cRetList, cPayeeList,this.redemptionBond);

        Collections.sort(cPayeeList);

        wSIC.setContributionsList(cPayeeList);


        Collections.sort(this.grandTotalsMap);
        wSIC.setFooterList(grandTotalsMap);
        wSIC.setFromDateStr(fDate);
        wSIC.setToDateStr(tDate);

        model.addAttribute("miniBean", wSIC);
        addRoleBeanToModel(model, request);


        return VIEW_NAME;
    }

    private List<SimulationInfoSummaryBean> makeDeductionSimulationInfoSummaryBeanListCont(
            List<SimulationMiniBean> pRetList, List<SimulationInfoSummaryBean> pPayeeList, Integer pTypeInd)
    {


        HashMap<Integer,SimulationInfoSummaryBean> wWorkMap = new HashMap<Integer,SimulationInfoSummaryBean>();

        for(SimulationMiniBean child : pRetList){

            SimulationInfoSummaryBean parent = wWorkMap.get(pTypeInd);
            if(null == parent){
                parent = new SimulationInfoSummaryBean();
                parent.setName(child.getName());
                parent.setId(Long.valueOf(pTypeInd));
                parent.setSerialNum(++this.totalNumOfCont);
                List<SimulationMiniBean> wChildList = new ArrayList<SimulationMiniBean>();
                wChildList.add(child);
                Collections.sort(wChildList);
                parent.setMiniBeanList(wChildList);
            }else{
                List<SimulationMiniBean> wChildList = parent.getMiniBeanList();
                wChildList.add(child);
                Collections.sort(wChildList);
                parent.setMiniBeanList(wChildList);
            }
            wWorkMap.put(pTypeInd, parent);
        }
        //Make Entry.Set list outa HashMap...
        List<SimulationInfoSummaryBean> wRetList = this.getEntryMapList(wWorkMap);
        pPayeeList.addAll(wRetList);

        return pPayeeList;
    }



    private List<SimulationInfoSummaryBean> makeDeductionSimulationInfoSummaryBeanList(
            List<SimulationMiniBean> pRetList, List<SimulationInfoSummaryBean> pPayeeList, Integer pTypeInd)
    {


        HashMap<Integer,SimulationInfoSummaryBean> wWorkMap = new HashMap<Integer,SimulationInfoSummaryBean>();

        for(SimulationMiniBean child : pRetList){

            SimulationInfoSummaryBean parent = wWorkMap.get(pTypeInd);
            if(null == parent){
                parent = new SimulationInfoSummaryBean();
                parent.setName(child.getName());
                parent.setId(Long.valueOf(pTypeInd));
                parent.setSerialNum(++this.totalNumOfMdas);
                List<SimulationMiniBean> wChildList = new ArrayList<SimulationMiniBean>();
                wChildList.add(child);
                Collections.sort(wChildList);
                parent.setMiniBeanList(wChildList);
            }else{
                List<SimulationMiniBean> wChildList = parent.getMiniBeanList();
                wChildList.add(child);
                Collections.sort(wChildList);
                parent.setMiniBeanList(wChildList);
            }
            wWorkMap.put(pTypeInd, parent);
        }
        //Make Entry.Set list outa HashMap...
        List<SimulationInfoSummaryBean> wRetList = this.getEntryMapList(wWorkMap);
        pPayeeList.addAll(wRetList);

        return pPayeeList;
    }

    //Now herein lies all the work..
    private List<SimulationInfoSummaryBean> makeSimulationInfoSummaryBeanList(
            List<SimulationMiniBean> pRetList)
    {
        List<SimulationInfoSummaryBean> wRetList = new ArrayList<SimulationInfoSummaryBean>();
        if(pRetList == null || pRetList.isEmpty())
            return wRetList;
        HashMap<Integer,SimulationInfoSummaryBean> wWorkMap = new HashMap<Integer,SimulationInfoSummaryBean>();

        for(SimulationMiniBean child : pRetList){

            Integer wKey = child.getParentKey().intValue();
            //System.out.println("child.getParentKey() = "+wKey);
            SimulationInfoSummaryBean parent = wWorkMap.get(wKey);
            if(null == parent){
                parent = new SimulationInfoSummaryBean();
                parent.setAssignedToObject(child.getName());
                parent.setId(child.getParentKey());
                List<SimulationMiniBean> wChildList = new ArrayList<SimulationMiniBean>();
                wChildList.add(child);
                Collections.sort(wChildList);
                parent.setMiniBeanList(wChildList);
            }else{
                List<SimulationMiniBean> wChildList = parent.getMiniBeanList();
                wChildList.add(child);
                Collections.sort(wChildList);
                parent.setAssignedToObject(child.getName());
                parent.setId(child.getParentKey());
                parent.setMiniBeanList(wChildList);
            }
            wWorkMap.put(wKey, parent);

        }
        //Make Entry.Set list outa HashMap...
        wRetList = this.getEntryMapList(wWorkMap);

        return wRetList;
    }

    private List<SimulationInfoSummaryBean> getEntryMapList(
            HashMap<Integer, SimulationInfoSummaryBean> pWorkMap)
    {
        List<SimulationInfoSummaryBean> wRetList = new ArrayList<SimulationInfoSummaryBean>();
        if(pWorkMap == null || pWorkMap.isEmpty())
            return wRetList;

        Set<Map.Entry<Integer,SimulationInfoSummaryBean>> set = pWorkMap.entrySet();
        Iterator<Map.Entry<Integer, SimulationInfoSummaryBean>> i = set.iterator();

        while(i.hasNext()){
            Map.Entry<Integer,SimulationInfoSummaryBean> me = i.next();
            me.getValue().setDisplayStyle("reportOdd");
            wRetList.add(me.getValue());
        }

        return wRetList;
    }


    private void createSimulationInfoSummaryBean(
            List<EmployeePayBean> pList, int pStartMonth, int pStartYear, int pIndex, PayrollRunMasterBean wPRMB) throws InstantiationException, IllegalAccessException {


        //Deductions
        SimulationMiniBean payeeBean = this.getSimulationMiniBean(pStartMonth,this.PAYE,pStartYear,pIndex);
        SimulationMiniBean nhfBean = this.getSimulationMiniBean(pStartMonth, this.NHF, pStartYear,pIndex);
        SimulationMiniBean unionBean = this.getSimulationMiniBean(pStartMonth, this.UNION, pStartYear,pIndex);
        SimulationMiniBean twsBean = this.getSimulationMiniBean(pStartMonth, this.TWS, pStartYear,pIndex);
        SimulationMiniBean ltgBean = this.getSimulationMiniBean(pStartMonth, this.LTG, pStartYear,pIndex);
        SimulationMiniBean devLevyBean = this.getSimulationMiniBean(pStartMonth, this.DEVLEVY, pStartYear,pIndex);

        SimulationMiniBean totalDeductions =  new SimulationMiniBean();
        totalDeductions.setIntegerId(pIndex);
        SimulationMiniBean wTotals = new SimulationMiniBean();
        wTotals.setIntegerId(pIndex);
        SimulationMiniBean totalGrossValue =  new SimulationMiniBean();
        totalGrossValue.setIntegerId(pIndex);

        //Contributions
        SimulationMiniBean contBean = this.getSimulationMiniBeanCont(pStartMonth,this.ContributoryPensions,pStartYear,pIndex, wPRMB);

        SimulationMiniBean redBean = this.getSimulationMiniBeanCont(pStartMonth,this.redemptionBond,pStartYear,pIndex, wPRMB);

        SimulationMiniBean totalContributions =  new SimulationMiniBean();
        totalContributions.setIntegerId(pIndex);
        SimulationMiniBean cTotals = new SimulationMiniBean();
        cTotals.setIntegerId(pIndex);
        SimulationMiniBean totalGrossValueCont =  new SimulationMiniBean();


        double wTotalPay = 0.0D;
        for(EmployeePayBean e : pList){

            Long wKey = e.getBusinessClientId();

            //deductions
            payeeBean.setCurrentValue(payeeBean.getCurrentValue() + e.getMonthlyTax());
            nhfBean.setCurrentValue(nhfBean.getCurrentValue() + e.getNhf());
            unionBean.setCurrentValue(unionBean.getCurrentValue() + e.getUnionDues());
            twsBean.setCurrentValue(twsBean.getCurrentValue() + e.getTws());
            ltgBean.setCurrentValue(ltgBean.getCurrentValue() + e.getLeaveTransportGrant());
            devLevyBean.setCurrentValue(devLevyBean.getCurrentValue() + e.getDevelopmentLevy());

            totalDeductions.setCurrentValue(totalDeductions.getCurrentValue() +  (e.getMonthlyTax() + e.getNhf() + e.getUnionDues() + e.getTws() + e.getDevelopmentLevy()));


            //contributions
            contBean.setCurrentValue(contBean.getCurrentValue() + e.getContributoryPension());
            wTotalPay += e.getTotalPay();
            redBean.setCurrentValue(wTotalPay * (wPRMB.getRbaPercentage() / 100.0D));



            SimulationMiniBean wWSB =  new SimulationMiniBean();
            //SimulationInfoSummaryBean wSISB = new SimulationInfoSummaryBean();

            //int _wKey = this.mapAgencyMap.get(wKey);
            if(this.mdaMap.containsKey(wKey)){
                HashMap<Integer,SimulationMiniBean> wAll = this.mdaMap.get(wKey);
                //Now get the SimulationInfoSummary for the Year/Month Simulation....
                if(wAll == null)
                    wAll = new HashMap<Integer,SimulationMiniBean>();

                if(wAll.containsKey(pIndex)){
                    wWSB = wAll.get(pIndex);
                    wWSB.setCurrentValue(wWSB.getCurrentValue() + e.getNetPay());
                    wWSB.setParentKey(wKey);
                    wAll.put(pIndex, wWSB);

                }else{

                    wWSB.setName(e.getBusinessClientName());
                    wWSB.setIntegerId(pIndex);
                    wWSB.setCurrentValue(e.getNetPay());
                    wWSB.setParentKey(wKey);
                    wAll.put(pIndex, wWSB);

                }
                this.mdaMap.put(wKey, wAll);


            }else{
                HashMap<Integer,SimulationMiniBean> wAll = new HashMap<Integer,SimulationMiniBean>();

                wWSB.setName(e.getBusinessClientName());
                wWSB.setParentKey(wKey);
                wWSB.setCurrentValue(e.getNetPay());
                wWSB.setIntegerId(pIndex);
                wAll.put(pIndex, wWSB);
                this.mdaMap.put(wKey, wAll);
            }

            //Now do totals for each Year:Month Combo..
            wTotals.setCurrentValue(wTotals.getCurrentValue() + e.getNetPay());
            totalGrossValue.setCurrentValue(totalGrossValue.getCurrentValue() + (e.getNetPay() + e.getNhf() + e.getTaxesPaid() + e.getTws() + e.getDevelopmentLevy() + e.getUnionDues()));
        }


        totalContributions.setCurrentValue(redBean.getCurrentValue() + contBean.getCurrentValue());


        //Now when all this has been done...
        this.deductionsTotalList.add(totalDeductions);
        this.grandTotalsMap.add(totalGrossValue);
        this.totalsMap.add(wTotals);
        this.payeeMap.put(pStartMonth,payeeBean);
        this.nhfMap.put(pStartMonth, nhfBean);
        this.unionMap.put(pStartMonth, unionBean);
        this.twsMap.put(pStartMonth, twsBean);
        this.ltgMap.put(pStartMonth, ltgBean);
        this.devLevyMap.put(pStartMonth, devLevyBean);

        //contributions
        this.contributionsTotalList.add(totalContributions);
//        this.totalsMap.add(cTotals);
        this.contributoryMap.put(pStartMonth,contBean);
        this.redemptionBondMap.put(pStartMonth, redBean);

    }

    private SimulationMiniBean getSimulationMiniBeanCont(int pStartMonth, int pIndicator, int pYearInd, int pIndex, PayrollRunMasterBean wPRMB) throws IllegalAccessException, InstantiationException {

        //Integer wKey = new Integer(pIndicator);


        SimulationMiniBean wRetVal = new SimulationMiniBean();
        switch(pIndicator){
            case 1://PAYE
                wRetVal = this.contributoryMap.get(pStartMonth);
                if(wRetVal == null){
                    wRetVal = new SimulationMiniBean();
                    wRetVal.setName("Contributory Pension");
                    wRetVal.setIntegerId(pStartMonth);
                    wRetVal.setYearInd(pYearInd);
                    wRetVal.setComparator(pIndex);
                    this.contributoryMap.put(pStartMonth, wRetVal);
                }
                break;
            case 2://PAYE
                wRetVal = this.redemptionBondMap.get(pStartMonth);
                if(wRetVal == null){
                    wRetVal = new SimulationMiniBean();
                    wRetVal.setName("Redemption Bond Account ( "+wPRMB.getRbaPercentageStr()+ " Gross Pay)");
                    wRetVal.setIntegerId(pStartMonth);
                    wRetVal.setYearInd(pYearInd);
                    wRetVal.setComparator(pIndex);
                    this.redemptionBondMap.put(pStartMonth, wRetVal);
                }
                break;

        }

        return wRetVal;
    }

    private SimulationMiniBean getSimulationMiniBean(int pStartMonth, int pIndicator, int pYearInd, int pIndex)
    {

        //Integer wKey = new Integer(pIndicator);

        SimulationMiniBean wRetVal = new SimulationMiniBean();
        switch(pIndicator){
            case 1://PAYE
                wRetVal = this.payeeMap.get(pStartMonth);
                if(wRetVal == null){
                    wRetVal = new SimulationMiniBean();
                    wRetVal.setName("P.A.Y.E");
                    wRetVal.setIntegerId(pStartMonth);
                    wRetVal.setYearInd(pYearInd);
                    wRetVal.setComparator(pIndex);
                    this.payeeMap.put(pStartMonth, wRetVal);
                }
                break;
            case 2: //NHF
                wRetVal = this.nhfMap.get(pStartMonth);
                if(wRetVal == null){
                    wRetVal = new SimulationMiniBean();
                    wRetVal.setName("N.H.F");
                    wRetVal.setIntegerId(pStartMonth);
                    wRetVal.setYearInd(pYearInd);
                    wRetVal.setComparator(pIndex);
                    this.nhfMap.put(pStartMonth, wRetVal);
                }
                break;
            case 3: //UNION
                wRetVal = this.unionMap.get(pStartMonth);
                if(wRetVal == null){
                    wRetVal = new SimulationMiniBean();
                    wRetVal.setName("Union Dues");
                    wRetVal.setIntegerId(pStartMonth);
                    wRetVal.setYearInd(pYearInd);
                    wRetVal.setComparator(pIndex);
                    this.unionMap.put(pStartMonth, wRetVal);
                }
                break;
            case 4://TWS
                wRetVal = this.twsMap.get(pStartMonth);
                if(wRetVal == null){
                    wRetVal = new SimulationMiniBean();
                    wRetVal.setName("Teachers Welfare Scheme");
                    wRetVal.setIntegerId(pStartMonth);
                    wRetVal.setYearInd(pYearInd);
                    wRetVal.setComparator(pIndex);
                    this.twsMap.put(pStartMonth, wRetVal);
                }
                break;
            case 5://LTG
                wRetVal = this.ltgMap.get(pStartMonth);
                if(wRetVal == null){
                    wRetVal = new SimulationMiniBean();
                    wRetVal.setName("Leave Transport Grant");
                    wRetVal.setIntegerId(pStartMonth);
                    wRetVal.setYearInd(pYearInd);
                    wRetVal.setComparator(pIndex);
                    this.ltgMap.put(pStartMonth, wRetVal);
                }
                break;
            case 6://DEVLEVY
                wRetVal = this.devLevyMap.get(pStartMonth);
                if(wRetVal == null){
                    wRetVal = new SimulationMiniBean();
                    wRetVal.setName("Development Levy");
                    wRetVal.setIntegerId(pStartMonth);
                    wRetVal.setYearInd(pYearInd);
                    wRetVal.setComparator(pIndex);
                    this.devLevyMap.put(pStartMonth, wRetVal);
                }
                break;

        }

        return wRetVal;
    }


    private List<SimulationMiniBean> getSimulationMiniBeanFromMap(
            HashMap<Long,HashMap<Integer,SimulationMiniBean>> pObjectMap, List<SimulationMiniBean> pRetList,boolean pSetDisplay) {


        Set<Long> wSet = pObjectMap.keySet();

        for(Long wInt : wSet){

            HashMap<Integer,SimulationMiniBean> wInnerMap = pObjectMap.get(wInt);
            pRetList = getDetailsFromMap(wInnerMap,pRetList,pSetDisplay);
        }



        return pRetList;
    }
    private List<SimulationMiniBean> getDetailsFromMap(
            HashMap<Integer, SimulationMiniBean> pObjectMap, List<SimulationMiniBean> pRetList,boolean pSetDisplay) {

        Set<Map.Entry<Integer,SimulationMiniBean>> set = pObjectMap.entrySet();
        Iterator<Map.Entry<Integer, SimulationMiniBean>> i = set.iterator();

        while(i.hasNext()){
            Map.Entry<Integer,SimulationMiniBean> me = i.next();

            if (pSetDisplay) {
                me.getValue().setDisplayStyle("reportOdd");
            }

            pRetList.add(me.getValue());



        }

        return pRetList;
    }

    @RequestMapping(method={RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep, @RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("miniBean") SimulationInfoContainer pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
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
