package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.SimulationService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.PayrollSimulationExcelGenerator;
import com.osm.gnl.ippms.ogsg.domain.simulation.*;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class PayrollSimulationExcelReport extends BaseController {

    @Autowired
    SimulationService simulationService;

    @SuppressWarnings("unused")
    private String lastDisplayStyle;
    private int fStartMonth;

    private HashMap<Long, HashMap<Long, SimulationMiniBean>> ministryMap;

    private List<SimulationMiniBean> deductionsTotalList;

    private List<SimulationMiniBean> totalsMap;

    private List<SimulationMiniBean> grandTotalsMap;

    private List<SimulationMiniBean> headerMap;
    private HashMap<Long,SimulationMiniBean> payeeMap;
    private HashMap<Long,SimulationMiniBean> nhfMap;
    private HashMap<Long,SimulationMiniBean> unionMap;
    private HashMap<Long,SimulationMiniBean> twsMap;
    private HashMap<Long,SimulationMiniBean> ltgMap;
    private HashMap<Long, SimulationMiniBean> devLevyMap;

    private  final long PAYE = 1;
    private  final long NHF = 2;
    private  final long UNION = 3;
    private  final long TWS = 4;
    private  final long LTG = 5;
    private  final long DEVLEVY = 6;

    private int totalNumOfMdas;

    @RequestMapping("/payrollSimulationExcel.do")
    public void setupForm(@RequestParam("pid")Long pPid, Model model, HttpServletRequest request,
                          HttpServletResponse response) throws Exception {

        PayrollSimulationExcelGenerator payrollSimulationExcelGenerator = new PayrollSimulationExcelGenerator();

        SessionManagerService.manageSession(request, model);

        init();

        BusinessCertificate bc = getBusinessCertificate(request);

        //First load the Master Bean to act as guide...
        PayrollSimulationMasterBean wPSMB = this.genericService.loadObjectUsingRestriction(PayrollSimulationMasterBean.class,
                Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("id", pPid)));


        this.fStartMonth = wPSMB.getSimulationStartMonth();

        //this.spillover = wPSMB.isCrossesOver();

        ArrayList<String> wGuideInfo = new ArrayList<String>();
        ArrayList<String> wGuideInfo2 = new ArrayList<String>();
        //First determine from when to when to load.....
        if(wPSMB.isCrossesOver()){
            //This means we need to cater for 2 years....
            int wStartMonth = wPSMB.getSimulationStartMonth();
            int wStartYear = wPSMB.getSimulationStartYear();

            //Now add the rest of the current year...
            for(int wStart = wPSMB.getSimulationStartMonth(); wStart < 12; wStart++){
                wGuideInfo.add(wStartMonth +":"+ wStartYear);
            }
            //Now we need to find out the start and end of the spillover...
            int wEndMonth = (wPSMB.getSimulationStartMonth() + wPSMB.getSimulationPeriodInd()) - 12;
            for(int wStart = 0; wStart < wEndMonth; wStart++){
                wGuideInfo2.add(wStart +":"+ (wStartYear + 1));
            }

        }else{
            //Single Year...
            int wStartMonth = wPSMB.getSimulationStartMonth();
            int wEndMonth = (wPSMB.getSimulationStartMonth() + wPSMB.getSimulationPeriodInd());
            for(int wStart = wStartMonth; wStart <= wEndMonth; wStart++){
                wGuideInfo.add(wStart +":"+wPSMB.getSimulationStartYear());
            }

        }

        SimulationInfoContainer wSIC = new SimulationInfoContainer();

        wSIC.setId(pPid);

        //Now iterate through our friends and make a container object.
        //Create the Header Beans...
        long wIndex = 1;

        for(String s : wGuideInfo){
            int wStartMonth = Integer.parseInt(s.substring(0,s.indexOf(":")));
            int wStartYear = Integer.parseInt(s.substring(s.indexOf(":")+ 1));

            SimulationMiniBean hb = new SimulationMiniBean();

            //Now load ALL SimulationInfo for this period...
            List<SimulationInfo> wList = this.simulationService.loadAllSimulatedPayrollByParentIdAndMonthAndYear(wPSMB.getId(),wStartMonth,wStartYear, bc);
            createSimulationInfoSummaryBean(wList,wStartMonth,wStartYear,wIndex);
            hb.setId(wIndex);
            hb.setName(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(wStartMonth, wStartYear));
            this.headerMap.add(hb);
            wIndex++;
        }

        for(String s : wGuideInfo2){
            int wStartMonth = Integer.parseInt(s.substring(0,s.indexOf(":")));
            int wStartYear = Integer.parseInt(s.substring(s.indexOf(":")+ 1));
            SimulationMiniBean hb = new SimulationMiniBean();
            //Now load ALL SimulationInfo for this period...
            List<SimulationInfo> wList = this.simulationService.loadAllSimulatedPayrollByParentIdAndMonthAndYear(wPSMB.getId(),wStartMonth,wStartYear, bc);
            createSimulationInfoSummaryBean(wList,wStartMonth,wStartYear,wIndex);
            hb.setId(wIndex);
            hb.setName(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(wStartMonth, wStartYear));
            this.headerMap.add(hb);
            wIndex++;
        }


        //Now lets create the display beans....
        Collections.sort(headerMap);
        wSIC.setHeaderList(headerMap);

        //Now create mdapBeanList...
        List<SimulationMiniBean> wRetList = new ArrayList<SimulationMiniBean>();

        wRetList = getSimulationMiniBeanFromMap(this.ministryMap,wRetList,true);

        //Now before we sort make SimulationInfoSummaryBean....
        List<SimulationInfoSummaryBean> wMasterList = this.makeSimulationInfoSummaryBeanList(wRetList);

		/*for(SimulationInfoSummaryBean s : wMasterList){
			System.out.println(s.getAssignedToObject());
			for(SimulationMiniBean sb : s.getMiniBeanList()){
				System.out.println("ID = "+sb.getId()+": Parent Key = "+sb.getParentKey()+": Name = "+sb.getName()+": Value = "+sb.getCurrentValueStr());
			}
		}*/

        Collections.sort(wMasterList);
        //int serialNum = wMasterList.size();
        wSIC.setSummaryBean(wMasterList);

        Collections.sort(wRetList);

        wSIC.setMdapList(wRetList);

        Collections.sort(this.totalsMap);

        wSIC.setMdapFooterList(this.totalsMap);

        Collections.sort(this.deductionsTotalList);

        wSIC.setDeductionsTotals(deductionsTotalList);

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

        wSIC.setName(wPSMB.getName());

        Collections.sort(this.grandTotalsMap);
        wSIC.setFooterList(grandTotalsMap);


        payrollSimulationExcelGenerator.generateExcel(wSIC, "Payroll_Simulation_Report", response, request, bc);
    }

    private void init(){

        this.deductionsTotalList = new ArrayList<SimulationMiniBean>();


        this.grandTotalsMap = new ArrayList<SimulationMiniBean>();
        this.ministryMap = new HashMap<Long,HashMap<Long,SimulationMiniBean>>();

        this.devLevyMap = new HashMap<Long,SimulationMiniBean>();


        //new HashMap<Integer,SimulationMiniBean>();
        //new HashMap<Integer,SimulationMiniBean>();
        this.headerMap = new ArrayList<SimulationMiniBean>();
        //new ArrayList<SimulationMiniBean>();

        this.totalsMap = new ArrayList<SimulationMiniBean>();

        this.payeeMap = new HashMap<Long,SimulationMiniBean>();
        this.nhfMap = new HashMap<Long,SimulationMiniBean>();
        this.unionMap = new HashMap<Long,SimulationMiniBean>();
        this.twsMap = new HashMap<Long,SimulationMiniBean>();
        this.ltgMap = new HashMap<Long,SimulationMiniBean>();
        this.devLevyMap = new HashMap<Long,SimulationMiniBean>();

    }

    private void createSimulationInfoSummaryBean(
            List<SimulationInfo> pList, int pStartMonth, int pStartYear, long wIndex)
    {



        SimulationMiniBean payeeBean = this.getSimulationMiniBean(pStartMonth,this.PAYE,pStartYear);
        SimulationMiniBean nhfBean = this.getSimulationMiniBean(pStartMonth, this.NHF, pStartYear);
        SimulationMiniBean unionBean = this.getSimulationMiniBean(pStartMonth, this.UNION, pStartYear);
        SimulationMiniBean twsBean = this.getSimulationMiniBean(pStartMonth, this.TWS, pStartYear);
        SimulationMiniBean ltgBean = this.getSimulationMiniBean(pStartMonth, this.LTG, pStartYear);
        SimulationMiniBean devLevyBean = this.getSimulationMiniBean(pStartMonth, this.DEVLEVY, pStartYear);

        SimulationMiniBean totalDeductions =  new SimulationMiniBean();
        totalDeductions.setId(wIndex);
        SimulationMiniBean wTotals = new SimulationMiniBean();
        wTotals.setId(wIndex);
        SimulationMiniBean totalGrossValue =  new SimulationMiniBean();

        totalGrossValue.setId(wIndex);

        for(SimulationInfo e : pList){

            Long wKey = e.getMdaDeptMap().getMdaInfo().getId();

            payeeBean.setCurrentValue(payeeBean.getCurrentValue() + e.getMonthlyTax());
            nhfBean.setCurrentValue(nhfBean.getCurrentValue() + e.getNhf());
            unionBean.setCurrentValue(unionBean.getCurrentValue() + e.getUnionDues());
            twsBean.setCurrentValue(twsBean.getCurrentValue() + e.getTws());
            ltgBean.setCurrentValue(ltgBean.getCurrentValue() + e.getLeaveTransportGrant());
            devLevyBean.setCurrentValue(devLevyBean.getCurrentValue() + e.getDevelopmentLevy());

            totalDeductions.setCurrentValue(totalDeductions.getCurrentValue() +  (e.getMonthlyTax() + e.getNhf() + e.getUnionDues() + e.getTws() + e.getDevelopmentLevy()));

            SimulationMiniBean wWSB =  new SimulationMiniBean();
            //SimulationInfoSummaryBean wSISB = new SimulationInfoSummaryBean();

            //int _wKey = this.mapAgencyMap.get(wKey);
            if(this.ministryMap.containsKey(wKey)){
                HashMap<Long,SimulationMiniBean> wAll = this.ministryMap.get(wKey);
                //Now get the SimulationInfoSummary for the Year/Month Simulation....
                if(wAll == null)
                    wAll = new HashMap<Long,SimulationMiniBean>();

                if(wAll.containsKey(wIndex)){
                    wWSB = wAll.get(wIndex);
                    wWSB.setCurrentValue(wWSB.getCurrentValue() + e.getNetPay());
                    wWSB.setParentKey(wKey);
                    wAll.put(wIndex, wWSB);

                }else{

                    wWSB.setName(e.getMdaDeptMap().getMdaInfo().getName());
                    wWSB.setId(wIndex);
                    wWSB.setCurrentValue(e.getNetPay());
                    wWSB.setParentKey(wKey);
                    wAll.put(wIndex, wWSB);

                }
                this.ministryMap.put(wKey, wAll);


            }else{
                HashMap<Long,SimulationMiniBean> wAll = new HashMap<Long,SimulationMiniBean>();

                wWSB.setName(e.getMdaDeptMap().getMdaInfo().getName());
                wWSB.setParentKey(wKey);
                wWSB.setCurrentValue(e.getNetPay());
                wWSB.setId(wIndex);
                wAll.put(wIndex, wWSB);
                this.ministryMap.put(wKey, wAll);
            }



            //Now do totals for each Year:Month Combo..
            wTotals.setCurrentValue(wTotals.getCurrentValue() + e.getNetPay());
            totalGrossValue.setCurrentValue(totalGrossValue.getCurrentValue() + (e.getNetPay() + e.getNhf() + e.getMonthlyTax() + e.getTws() + e.getDevelopmentLevy() + e.getUnionDues()));
        }

        //Now when all this has been done...
        this.deductionsTotalList.add(totalDeductions);
        this.grandTotalsMap.add(totalGrossValue);
        this.totalsMap.add(wTotals);
        this.payeeMap.put((long) pStartMonth,payeeBean);
        this.nhfMap.put((long) pStartMonth, nhfBean);
        this.unionMap.put((long) pStartMonth, unionBean);
        //this.twsMap.put(pIndex, twsBean);
        this.ltgMap.put((long) pStartMonth, ltgBean);
        this.devLevyMap.put((long) pStartMonth, devLevyBean);

    }

    private List<SimulationMiniBean> getSimulationMiniBeanFromMap(
            HashMap<Long, HashMap<Long, SimulationMiniBean>> ministryMap2, List<SimulationMiniBean> pRetList,boolean pSetDisplay) {


        Set<Long> wSet = ministryMap2.keySet();

        for(Long wInt : wSet){

            HashMap<Long,SimulationMiniBean> wInnerMap = ministryMap2.get(wInt);
            pRetList = getDetailsFromMap(wInnerMap,pRetList,pSetDisplay);
        }



        return pRetList;
    }

    private List<SimulationMiniBean> getDetailsFromMap(
            HashMap<Long, SimulationMiniBean> wInnerMap, List<SimulationMiniBean> pRetList,boolean pSetDisplay) {

        Set<Map.Entry<Long,SimulationMiniBean>> set = wInnerMap.entrySet();
        Iterator<Map.Entry<Long, SimulationMiniBean>> i = set.iterator();

        while(i.hasNext()){
            Map.Entry<Long,SimulationMiniBean> me = i.next();

            if (pSetDisplay) {
                me.getValue().setDisplayStyle("reportOdd");
            }

            pRetList.add(me.getValue());



        }

        return pRetList;
    }

    private SimulationMiniBean getSimulationMiniBean(int pStartMonth, Long pIndex, int pYearInd)
    {



        SimulationMiniBean wRetVal = new SimulationMiniBean();
        long startMonth = pStartMonth;
        switch(pIndex.intValue()){
            case 1://PAYE
                wRetVal = this.payeeMap.get(startMonth);
                if(wRetVal == null){
                    wRetVal = new SimulationMiniBean();
                    if(pStartMonth == this.fStartMonth){
                        wRetVal.setName("P.A.Y.E");

                    }
                    wRetVal.setId(startMonth);
                    wRetVal.setYearInd(pYearInd);
                    this.payeeMap.put(startMonth, wRetVal);
                }
                break;
            case 2: //NHF
                wRetVal = this.nhfMap.get(startMonth);
                if(wRetVal == null){
                    wRetVal = new SimulationMiniBean();
                    if(pStartMonth == this.fStartMonth){
                        wRetVal.setName("N.H.F");
                    }
                    wRetVal.setId(startMonth);
                    wRetVal.setYearInd(pYearInd);
                    this.nhfMap.put(startMonth, wRetVal);
                }
                break;
            case 3: //UNION
                wRetVal = this.unionMap.get(startMonth);
                if(wRetVal == null){
                    wRetVal = new SimulationMiniBean();
                    if(pStartMonth == this.fStartMonth){
                        wRetVal.setName("Union Dues");
                    }
                    wRetVal.setId(startMonth);
                    wRetVal.setYearInd(pYearInd);
                    this.unionMap.put(startMonth, wRetVal);
                }
                break;
            case 4://TWS
                wRetVal = this.twsMap.get(startMonth);
                if(wRetVal == null){
                    wRetVal = new SimulationMiniBean();
                    if(pStartMonth == this.fStartMonth){
                        wRetVal.setName("Teachers Welfare Scheme");
                    }
                    wRetVal.setId(startMonth);
                    wRetVal.setYearInd(pYearInd);
                    this.twsMap.put(startMonth, wRetVal);
                }
                break;
            case 5://LTG
                wRetVal = this.ltgMap.get(startMonth);
                if(wRetVal == null){
                    wRetVal = new SimulationMiniBean();
                    if(pStartMonth == this.fStartMonth){
                        wRetVal.setName("Leave Transport Grant");
                    }
                    wRetVal.setId(startMonth);
                    wRetVal.setYearInd(pYearInd);
                    this.ltgMap.put(startMonth, wRetVal);
                }
                break;
            case 6://DEVLEVY
                wRetVal = this.devLevyMap.get(startMonth);
                if(wRetVal == null){
                    wRetVal = new SimulationMiniBean();
                    if(pStartMonth == this.fStartMonth){
                        wRetVal.setName("Development Levy");
                    }
                    wRetVal.setId(startMonth);
                    wRetVal.setYearInd(pYearInd);
                    this.devLevyMap.put(startMonth, wRetVal);
                }
                break;

        }

        return wRetVal;
    }

    private List<SimulationInfoSummaryBean> makeDeductionSimulationInfoSummaryBeanList(
            List<SimulationMiniBean> pRetList, List<SimulationInfoSummaryBean> pPayeeList, long pAYE2)
    {


        HashMap<Long,SimulationInfoSummaryBean> wWorkMap = new HashMap<Long,SimulationInfoSummaryBean>();

        for(SimulationMiniBean child : pRetList){

            SimulationInfoSummaryBean parent = wWorkMap.get(pAYE2);
            if(null == parent){
                parent = new SimulationInfoSummaryBean();
                parent.setName(child.getName());
                parent.setId(pAYE2);
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
            wWorkMap.put(pAYE2, parent);
        }
        //Make Entry.Set list outa HashMap...
        List<SimulationInfoSummaryBean> wRetList = this.getEntryMapList(wWorkMap);
        pPayeeList.addAll(wRetList);

        return pPayeeList;
    }

    private List<SimulationInfoSummaryBean> getEntryMapList(
            HashMap<Long, SimulationInfoSummaryBean> wWorkMap)
    {
        List<SimulationInfoSummaryBean> wRetList = new ArrayList<SimulationInfoSummaryBean>();
        if(wWorkMap == null || wWorkMap.isEmpty())
            return wRetList;

        Set<Map.Entry<Long,SimulationInfoSummaryBean>> set = wWorkMap.entrySet();
        Iterator<Map.Entry<Long, SimulationInfoSummaryBean>> i = set.iterator();

        while(i.hasNext()){
            Map.Entry<Long,SimulationInfoSummaryBean> me = i.next();
            me.getValue().setDisplayStyle("reportOdd");
            wRetList.add(me.getValue());
        }

        return wRetList;
    }

    private List<SimulationInfoSummaryBean> makeSimulationInfoSummaryBeanList(
            List<SimulationMiniBean> pRetList)
    {
        List<SimulationInfoSummaryBean> wRetList = new ArrayList<SimulationInfoSummaryBean>();
        if(pRetList == null || pRetList.isEmpty())
            return wRetList;
        HashMap<Long, SimulationInfoSummaryBean> wWorkMap = new HashMap<Long,SimulationInfoSummaryBean>();

        for(SimulationMiniBean child : pRetList){

            long wKey = child.getParentKey();
            SimulationInfoSummaryBean parent = wWorkMap.get(wKey);
            if(null == parent){
                parent = new SimulationInfoSummaryBean();
                parent.setAssignedToObject(child.getName());
                parent.setId(wKey);
                List<SimulationMiniBean> wChildList = new ArrayList<SimulationMiniBean>();
                wChildList.add(child);
                Collections.sort(wChildList);
                parent.setMiniBeanList(wChildList);
            }else{
                List<SimulationMiniBean> wChildList = parent.getMiniBeanList();
                wChildList.add(child);
                Collections.sort(wChildList);
                parent.setAssignedToObject(child.getName());
                parent.setId(wKey);
                parent.setMiniBeanList(wChildList);
            }
            wWorkMap.put(wKey, parent);

        }
        //Make Entry.Set list outa HashMap...
        wRetList = this.getEntryMapList(wWorkMap);

        return wRetList;
    }
}
