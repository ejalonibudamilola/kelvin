package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.base.services.PromotionService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.promotion.FlaggedPromotions;
import com.osm.gnl.ippms.ogsg.domain.report.VariationReportBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
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
@RequestMapping({"/flaggedPromotions.do"})
@SessionAttributes(types={WageBeanContainer.class})
public class FlaggedPromotionsFormController extends BaseController {

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private EmployeeService employeeService;


    private HashMap<Long, HashMap<Integer, WageSummaryBean>> mdaMap;

    private List<WageSummaryBean> annualSalaryTotalList;

    private List<WageSummaryBean> subventionsTotalList;

    private List<WageSummaryBean> contributionsTotalList;

    private List<WageSummaryBean> totalsMap;

    private List<WageSummaryBean> grandTotalsMap;

    private List<WageSummaryBean> headerMap;
    private HashMap<Integer,WageSummaryBean> annualSalaryMap;
    private HashMap<Integer,WageSummaryBean> consolidatedAllowanceMap;
    private HashMap<Integer,WageSummaryBean> monthlyGrossMap;




    private String lastDisplayStyle;
    private int fStartMonth;

    private int totalNumOfMdas;

    private  int totalNumOfCont;

    private static final String VIEW_NAME = "promotion/flaggedPromotionsForm";

    private void init(){

        this.annualSalaryTotalList = new ArrayList<>();

        this.contributionsTotalList = new ArrayList<>();

        this.grandTotalsMap = new ArrayList<>();

        this.mdaMap = new HashMap<>();

        this.headerMap = new ArrayList<>();


        this.totalsMap = new ArrayList<>();

        this.annualSalaryMap = new HashMap<>();
        this.consolidatedAllowanceMap = new HashMap<>();
        this.monthlyGrossMap = new HashMap<>();

        totalNumOfMdas = 0;

        totalNumOfCont = 0;
    }


    @RequestMapping(method={RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);

        init();

        BusinessCertificate bc = this.getBusinessCertificate(request);

        LocalDate startDate = null;
        LocalDate endDate = null;

        List<FlaggedPromotions> flaggedList = this.genericService.loadControlEntity(FlaggedPromotions.class);

        if(IppmsUtils.isNotNullOrEmpty(flaggedList)){
            FlaggedPromotions thisFlagged = flaggedList.get(0);
            startDate = PayrollBeanUtils.getDateFromMonthAndYear(thisFlagged.getRunMonth(), thisFlagged.getRunYear());
            endDate = PayrollBeanUtils.getDateFromMonthAndYear(12, thisFlagged.getRunYear());
        }
        else {
            ArrayList<LocalDate> list = PayrollBeanUtils.getDefaultCheckRegisterDates();

            endDate = list.get(1);
            startDate = list.get(0);
        }

        ArrayList<String> wGuideInfo = new ArrayList<>();
        //First determine from when to when to load.....
        //Single Year...

        int wEndMonth = endDate.getMonthValue();
        int wStartMonth = startDate.getMonthValue();

        for (int wStart = wStartMonth; wStart <= wEndMonth; wStart++)
            wGuideInfo.add(wStart + ":" + startDate.getYear());

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

        ArrayList<String> wGuideInfo = new ArrayList<>();

        for (int wStart = pStartDate; wStart <= pEndMonth; wStart++) {
            wGuideInfo.add(wStart + ":" +pRunYear);
        }

        return doWork(wGuideInfo, bc, model, request, fromDate, toDate);
    }

    public String doWork(ArrayList<String> wGuideInfo, BusinessCertificate bc, Model model, HttpServletRequest request,
                         String fDate, String tDate) throws IllegalAccessException, InstantiationException {

        WageBeanContainer wSIC = new WageBeanContainer();

        Long pPid = 0L;
        pPid++;
        wSIC.setId(pPid);
        //Now iterate through our friends and make a container object.
        //Create the Header Beans...
        int wIndex = 1;

        for(String s : wGuideInfo){
            int wStartMonth = Integer.parseInt(s.substring(0,s.indexOf(":")));
            int wStartYear = Integer.parseInt(s.substring(s.indexOf(":")+ 1));

            WageSummaryBean hb = new WageSummaryBean();


            //Now load ALL SimulationInfo for this period...
            List<VariationReportBean> wList = this.promotionService.loadFlaggedPromotionsForAllOrganization(wStartMonth, wStartYear);


            createSimulationInfoSummaryBean(wList,wStartMonth,wStartYear,wIndex);

            hb.setIntegerId(new Integer(wIndex));
            hb.setName(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(wStartMonth, wStartYear));
            this.headerMap.add(hb);
            wIndex++;
        }


        //Now lets create the display beans....
        Collections.sort(headerMap);
        wSIC.setHeaderList(headerMap);

        //Now create mdapBeanList...
        List<WageSummaryBean> wRetList = new ArrayList<>();
        wRetList = getSimulationMiniBeanFromMap(this.mdaMap,wRetList,true);

        List<WageSummaryBean> cRetList = new ArrayList<>();
        List<WageSummaryBean> cPayeeList = new ArrayList<>();
        cRetList = getSimulationMiniBeanFromMap(this.mdaMap,cRetList,true);

        //Now before we sort make SimulationInfoSummaryBean....
        List<WageSummaryBean> wMasterList = this.makeSimulationInfoSummaryBeanList(wRetList);

        Collections.sort(wMasterList);

        wSIC.setSummaryBean(wMasterList);

        Collections.sort(wRetList);

        wSIC.setMdapList(wRetList);

        Collections.sort(this.totalsMap);

        wSIC.setMdapFooterList(this.totalsMap);

        //Now Set Deductions....
        wRetList = null;


        Collections.sort(this.grandTotalsMap);
        wSIC.setFromDateStr(fDate);
        wSIC.setToDateStr(tDate);

        model.addAttribute("miniBean", wSIC);
        addRoleBeanToModel(model, request);


        return VIEW_NAME;
    }




    //Now herein lies all the work..
    private List<WageSummaryBean> makeSimulationInfoSummaryBeanList(
            List<WageSummaryBean> pRetList)
    {
        List<WageSummaryBean> wRetList = new ArrayList<>();
        if(pRetList == null || pRetList.isEmpty())
            return wRetList;
        HashMap<Integer,WageSummaryBean> wWorkMap = new HashMap<>();

        for(WageSummaryBean child : pRetList){

            Integer wKey = child.getParentKey().intValue();
            //System.out.println("child.getParentKey() = "+wKey);
            WageSummaryBean parent = wWorkMap.get(wKey);
            if(null == parent){
                parent = new WageSummaryBean();
                parent.setAssignedToObject(child.getName());
                parent.setId(child.getParentKey());
                List<WageSummaryBean> wChildList = new ArrayList<>();
                wChildList.add(child);
                Collections.sort(wChildList);
                parent.setMiniBeanList(wChildList);
            }else{
                List<WageSummaryBean> wChildList = parent.getMiniBeanList();
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

    private List<WageSummaryBean> getEntryMapList(
            HashMap<Integer, WageSummaryBean> pWorkMap)
    {
        List<WageSummaryBean> wRetList = new ArrayList<>();
        if(pWorkMap == null || pWorkMap.isEmpty())
            return wRetList;

        Set<Map.Entry<Integer,WageSummaryBean>> set = pWorkMap.entrySet();
        Iterator<Map.Entry<Integer, WageSummaryBean>> i = set.iterator();

        while(i.hasNext()){
            Map.Entry<Integer,WageSummaryBean> me = i.next();
            me.getValue().setDisplayStyle("reportOdd");
            wRetList.add(me.getValue());
        }

        return wRetList;
    }


    private void createSimulationInfoSummaryBean(
            List<VariationReportBean> pList, int pStartMonth, int pStartYear, int pIndex) throws InstantiationException, IllegalAccessException {


        WageSummaryBean wTotals = new WageSummaryBean();
        wTotals.setIntegerId(pIndex);


        Long i = 1L;
        for(VariationReportBean e : pList){

            Long wKey = e.getBusinessClientInstId();



            WageSummaryBean wWSB =  new WageSummaryBean();
            //SimulationInfoSummaryBean wSISB = new SimulationInfoSummaryBean();

            //int _wKey = this.mapAgencyMap.get(wKey);
            HashMap<Integer, WageSummaryBean> wAll;
            if(this.mdaMap.containsKey(wKey)){
                wAll = this.mdaMap.get(wKey);
                //Now get the SimulationInfoSummary for the Year/Month Simulation....
                if(wAll == null)
                    wAll = new HashMap<>();

                if(wAll.containsKey(pIndex)){
                    wWSB = wAll.get(pIndex);
                    wWSB.setCountValue(wWSB.getCountValue() + i);

                }else{

                    wWSB.setName(e.getBusinessName());
                    wWSB.setIntegerId(pIndex);
                    wWSB.setCountValue(i);

                }
                wWSB.setParentKey(wKey);
                wAll.put(pIndex, wWSB);


            }else{
                wAll = new HashMap<>();

                wWSB.setName(e.getBusinessName());
                wWSB.setParentKey(wKey);
                wWSB.setCountValue(i);
                wWSB.setIntegerId(pIndex);
                wAll.put(pIndex, wWSB);
            }
            this.mdaMap.put(wKey, wAll);

            //Now do totals for each Year:Month Combo..
            wTotals.setCountValue(wWSB.getCountValue());
     }



        //Now when all this has been done...
        this.totalsMap.add(wTotals);

        //contributions

    }




    private List<WageSummaryBean> getSimulationMiniBeanFromMap(
            HashMap<Long,HashMap<Integer,WageSummaryBean>> pObjectMap, List<WageSummaryBean> pRetList,boolean pSetDisplay) {


        Set<Long> wSet = pObjectMap.keySet();

        for(Long wInt : wSet){

            HashMap<Integer,WageSummaryBean> wInnerMap = pObjectMap.get(wInt);
            pRetList = getDetailsFromMap(wInnerMap,pRetList,pSetDisplay);
        }



        return pRetList;
    }
    private List<WageSummaryBean> getDetailsFromMap(
            HashMap<Integer, WageSummaryBean> pObjectMap, List<WageSummaryBean> pRetList,boolean pSetDisplay) {

        Set<Map.Entry<Integer,WageSummaryBean>> set = pObjectMap.entrySet();
        Iterator<Map.Entry<Integer, WageSummaryBean>> i = set.iterator();

        while(i.hasNext()){
            Map.Entry<Integer,WageSummaryBean> me = i.next();

            if (pSetDisplay) {
                me.getValue().setDisplayStyle("reportOdd");
            }

            pRetList.add(me.getValue());



        }

        return pRetList;
    }

    @RequestMapping(method={RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep, @RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("miniBean") WageBeanContainer pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);


        if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
            return "redirect:reportsOverview.do";
        }


        if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {

            if (pLPB.getFromDate() == null)
            {
                result.rejectValue("", "InvalidValue", "Please select a Promotion 'From' Date");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("miniBean", pLPB);

                return VIEW_NAME;
            }
            if(pLPB.getToDate() == null)
            {
                result.rejectValue("", "InvalidValue", "Please select a Promotion 'To' Date");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("miniBean", pLPB);

                return VIEW_NAME;
            }
            if((pLPB.getFromDate() == null) && (pLPB.getToDate() == null)){
                result.rejectValue("", "InvalidValue", "Please select Promotion Dates");
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

            return "redirect:flaggedPromotions.do?fd=" +fDate + "&td=" + tDate;
        }

        return "redirect:flaggedPromotions.do";
    }


}
