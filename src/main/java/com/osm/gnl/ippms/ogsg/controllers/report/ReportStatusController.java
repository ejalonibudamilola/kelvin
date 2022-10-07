package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.control.entities.EmployeeType;
import com.osm.gnl.ippms.ogsg.control.entities.MaritalStatus;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.CustomExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.CustomPdfReportGenerator;
import com.osm.gnl.ippms.ogsg.customreports.utils.CustomReportGenHelper;
import com.osm.gnl.ippms.ogsg.domain.beans.ProgressBean;
import com.osm.gnl.ippms.ogsg.domain.customreports.CustomReportObjectAttr;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.location.domain.State;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping({"/reportDisplayStatus.do"})
public class ReportStatusController extends BaseController {

    private final String VIEW = "progress/progressReportForm";

    public ReportStatusController() {
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request, HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException, InterruptedException {
        SessionManagerService.manageSession(request, model);

        ProgressBean wPB = new ProgressBean();


        Object o = getSessionAttribute(request, "reportRun");
        if (o == null) {
            return "redirect:customReportGenerator.do";
        }
         if(o.getClass().isAssignableFrom(CustomExcelReportGenerator.class)){
            CustomExcelReportGenerator cERG = (CustomExcelReportGenerator) o;
            wPB.setDisplayMessage("Generating Excel Report....Please Wait");
            wPB.setPageTitle("Custom Excel Report");

            if(cERG.isFinished()){
                ((CustomExcelReportGenerator) o).stop(true);
                wPB.setDisplayMessage("Excel Generated Successfully");
                removeSessionAttribute(request, "reportRun");
                return "redirect:finalizeCustomReport.do";


//                addSessionAttribute(request,"currentWorkBook", cERG.getWorkbook());
//                addSessionAttribute(request,"title", cERG.getTitle());
//
//                ReportOutputUtil rOU = new ReportOutputUtil(cERG.getWorkbook(), cERG.getTitle(), request, response);
//                addSessionAttribute(request, "reportOutput", rOU);
//
//                Thread t = new Thread(rOU);
//                t.start();
//
////                return "redirect:reportRedirect.do";
//               // Thread.sleep(60000);
//                    if(rOU.isFinished()){
//                        //set indicator for completion
//                        ReportStatus stat = new ReportStatus();
//                        stat.setFinished(true);
//                        stat.setReportSize(cERG.getAllRecords());
//                        addSessionAttribute(request, "reportStatus", stat);
//                        return this.returnHome(request,model);
//                    }
            }
            wPB.setCurrentCount(cERG.getCurrentRecord());
            wPB.setTotalElements(cERG.getTotalRecords());
           // wPB.setPercentage(cERG.getPercentage());
          //  wPB.setTimeRemaining(cERG.getTimeToElapse());
        }
        else if(o.getClass().isAssignableFrom(CustomPdfReportGenerator.class)){
            CustomPdfReportGenerator cERG = (CustomPdfReportGenerator) o;
            wPB.setDisplayMessage("Generating Pdf Report....Please Wait");
            wPB.setPageTitle("Custom Pdf Report");

            if(cERG.isFinished()){
                ((CustomPdfReportGenerator) o).stop(true);
                wPB.setDisplayMessage("Pdf Generated Successfully");
                removeSessionAttribute(request, "reportRun");


                addSessionAttribute(request,"currentFileLocation", cERG.getFileLocation());
                addSessionAttribute(request,"title", cERG.getTitle());
                return "redirect:exportPdfToView.do";
            }
            wPB.setCurrentCount(cERG.getCurrentRecord());
            wPB.setTotalElements(cERG.getTotalRecords());
            wPB.setPercentage(cERG.getPercentage());
            wPB.setTimeRemaining(cERG.getTimeToElapse());
        }


        addRoleBeanToModel(model, request);
        model.addAttribute("progressBean", wPB);
        return VIEW;
    }

    private String returnHome(HttpServletRequest request, Model pModel) {

        BusinessCertificate bc = this.getBusinessCertificate(request);

        CustomPredicate predicates;
        if (bc.isPensioner()) {
            predicates = CustomPredicate.procurePredicate("pensionerRestricted", OFF);
        } else {
            predicates = CustomPredicate.procurePredicate("nonPensionerRestricted", OFF);

        }
        List<CustomReportObjectAttr> rList = this.genericService.loadAllObjectsWithSingleCondition(CustomReportObjectAttr.class, predicates, "prefDisplayName");
        rList = CustomReportGenHelper.treatList(rList, bc, true,false);

        List<BankInfo> banks = this.genericService.loadControlEntity(BankInfo.class);
        List<MdaInfo> mdaInfo = this.genericService.loadAllObjectsWithSingleCondition(MdaInfo.class, getBusinessClientIdPredicate(request), "name");
        List<State> state = this.genericService.loadControlEntity(State.class);
        List<MaritalStatus> marital = this.genericService.loadControlEntity(MaritalStatus.class);
        List<EmployeeType> empList = this.genericService.loadAllObjectsWithSingleCondition(EmployeeType.class, getBusinessClientIdPredicate(request), "name");
        List<EmpGarnishmentType> garnTypeList = this.genericService.loadAllObjectsWithSingleCondition(EmpGarnishmentType.class,getBusinessClientIdPredicate(request), "name");
        List<SalaryType> salaryTypeList = this.genericService.loadAllObjectsWithSingleCondition(SalaryType.class,getBusinessClientIdPredicate(request), "name");
        List<PfaInfo> pfaList = this.genericService.loadControlEntity(PfaInfo.class);
        pModel.addAttribute("bList", banks);
        pModel.addAttribute("mList", mdaInfo);
        pModel.addAttribute("stList", state);
        pModel.addAttribute("eList", empList);
        pModel.addAttribute("rList", rList);
        pModel.addAttribute("msList", marital);
        pModel.addAttribute("gList", garnTypeList);
        pModel.addAttribute("sList", salaryTypeList);
        pModel.addAttribute("pList", pfaList);

        addRoleBeanToModel(pModel, request);
        return "custom_report/customReportGenerator";
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        Object o = getSessionAttribute(request, "reportRun");
        if (o != null) {
            removeSessionAttribute(request,"reportRun");
        }
        if (!isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {


            return "redirect:customReportGenerator.do";
        }


        Thread.sleep(200L);
        return "redirect:customReportGenerator.do";


    }


}
