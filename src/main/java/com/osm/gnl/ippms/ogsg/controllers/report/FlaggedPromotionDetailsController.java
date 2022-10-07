package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.base.services.PromotionService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.report.VariationReportBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationInfoContainer;
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
@RequestMapping({"/flaggedPromotionDetails.do"})
@SessionAttributes(types={WageBeanContainer.class})
public class FlaggedPromotionDetailsController extends BaseController {

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

    private static final String VIEW_NAME = "promotion/flaggedPromotionsDetails";

    private void init(){

        this.annualSalaryTotalList = new ArrayList<WageSummaryBean>();

        this.contributionsTotalList = new ArrayList<WageSummaryBean>();

        this.grandTotalsMap = new ArrayList<WageSummaryBean>();

        this.mdaMap = new HashMap<Long,HashMap<Integer,WageSummaryBean>>();

        this.headerMap = new ArrayList<WageSummaryBean>();


        this.totalsMap = new ArrayList<WageSummaryBean>();

        this.annualSalaryMap = new HashMap<Integer,WageSummaryBean>();
        this.consolidatedAllowanceMap = new HashMap<Integer,WageSummaryBean>();
        this.monthlyGrossMap = new HashMap<Integer,WageSummaryBean>();

        totalNumOfMdas = 0;

        totalNumOfCont = 0;
    }



    @RequestMapping(method={RequestMethod.GET}, params={"bid", "mid", "fd", "td"})
    public String setupForm(@RequestParam("bid")Long bId, @RequestParam("mid")Long mId, @RequestParam("fd") String fromDate, @RequestParam("td") String toDate, Model model, HttpServletRequest request) throws Exception {
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

        return doWork(wGuideInfo, bc, model, request, fromDate, toDate, mId, bId);
    }

    public String doWork(ArrayList<String> wGuideInfo, BusinessCertificate bc, Model model, HttpServletRequest request,
                         String fDate, String tDate, Long mId, Long bId) throws IllegalAccessException, InstantiationException {

        WageBeanContainer wSIC = new WageBeanContainer();

        Long pPid = 0L;
        pPid++;
        wSIC.setId(pPid);
        //Now iterate through our friends and make a container object.
        //Create the Header Beans...
        int wIndex = 1;
        List<List<VariationReportBean>> wLists = new ArrayList<>();
        for(String s : wGuideInfo){
            int wStartMonth = Integer.parseInt(s.substring(0,s.indexOf(":")));
            int wStartYear = Integer.parseInt(s.substring(s.indexOf(":")+ 1));

            WageSummaryBean hb = new WageSummaryBean();


            //Now load ALL SimulationInfo for this period...
            List<VariationReportBean> wList = this.promotionService.loadFlaggedPromotionsByOrganization(wStartMonth, wStartYear, bc, bId, mId);

            if(IppmsUtils.isNotNullOrEmpty(wList))
            wLists.add(wList);
        }


        wSIC.setVariationList(wLists);



        wSIC.setFromDateStr(fDate);
        wSIC.setToDateStr(tDate);

        model.addAttribute("miniBean", wSIC);
        addRoleBeanToModel(model, request);


        return VIEW_NAME;
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
