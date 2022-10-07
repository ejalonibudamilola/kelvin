package com.osm.gnl.ippms.ogsg.controllers.fileupload;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.FileUploadService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.domain.beans.FileParseBean;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import com.osm.gnl.ippms.ogsg.domain.promotion.StepIncrementTracker;
import com.osm.gnl.ippms.ogsg.engine.ParseExcelAndSchedulePayment;
import com.osm.gnl.ippms.ogsg.engine.ParseLeaveBonus;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.leavebonus.domain.LeaveBonusBean;
import com.osm.gnl.ippms.ogsg.leavebonus.domain.LeaveBonusErrorBean;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PassPhrase;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;


@Controller
@SessionAttributes(types = {FileParseBean.class})
public class FileUploadController extends BaseController {


    private final  FileUploadService fileUploadService;
    private volatile String uploadUniqueIdentifier;
    private final String VIEW = "fileupload/fileUploadForm";

    @Autowired
    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @RequestMapping({"/upload.do"})
    public String setupForm(@RequestParam("ot") int pObjectType, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        FileParseBean wFPB = new FileParseBean();
        wFPB.setObjectTypeInd(pObjectType);
        switch (pObjectType) {

            case 2:
                wFPB.setName("Loan");
                wFPB.setObjectTypeClass(EmpGarnishmentType.class);
                break;
            case 3:
                wFPB.setName("Deduction");
                wFPB.setObjectTypeClass(EmpDeductionType.class);
                break;
            case 4:
                wFPB.setName("Leave Bonus");
                wFPB.setObjectTypeClass(LeaveBonusBean.class);
                wFPB.setLeaveBonus(true);

                break;
            case 5:
                wFPB.setName("Special Allowance");
                wFPB.setObjectTypeClass(SpecialAllowanceType.class);
                break;
            case 6:
                List<SalaryType> salaryTypeList = this.genericService.loadAllObjectsUsingRestrictions(SalaryType.class,
                        Arrays.asList(getBusinessClientIdPredicate(request),CustomPredicate.procurePredicate("selectableInd",1)),"name");

                wFPB.setName("Salary Structure");
                wFPB.setObjectTypeClass(SalaryInfo.class);
                wFPB.setSalaryInfo(true);
                model.addAttribute("salaryTypeList",salaryTypeList);
                break;
            case 7:
                wFPB.setName("Step Increment");
                wFPB.setObjectTypeClass(StepIncrementTracker.class);
                wFPB.setStepIncrement(true);
                break;

        }
        wFPB.setPostActionUrl("uploadSubmit.do");
        model.addAttribute("fileUploadBean", wFPB);
        addRoleBeanToModel(model, request);
        return VIEW;
    }
    @RequestMapping({"/uploadLeaveBonusFile.do"})
    public String setupForm(Model model, HttpServletRequest request)
            throws HttpSessionRequiredException, EpmAuthenticationException
    {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        FileParseBean wFPB = new FileParseBean();

        wFPB.setName("Leave Bonus");
        wFPB.setObjectTypeClass(LeaveBonusBean.class);
        wFPB.setLeaveBonus(true);
        wFPB.setObjectTypeInd(4);

        //Find out whether we have error files..

        if(this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder().addPredicate(getBusinessClientIdPredicate(request)),LeaveBonusErrorBean.class) > 0)
            bc.setHasLeaveBonusErrorData(true);
        List<MdaInfo> mdaList = genericService.loadAllObjectsWithSingleCondition(MdaInfo.class,getBusinessClientIdPredicate(request),"name" );
        wFPB.setMdaInfoList(mdaList);
        model.addAttribute("fileUploadBean", wFPB);
        addRoleBeanToModel(model, request);
        return VIEW;
    }
    @RequestMapping({"/uploadSubmit.do"})
    public String upload(@RequestParam(value = "_cancel", required = false) String cancel, @RequestParam("file") MultipartFile multipartFile,
                         @ModelAttribute("fileUploadBean") FileParseBean pMB, BindingResult result, Model model, SessionStatus status, HttpServletRequest request)
            throws Exception {

        SessionManagerService.manageSession(request);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        if(isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
            return "redirect:massEntryDashboard.do";
        }
        if ((multipartFile == null) || (multipartFile.getName() == null)) {
            addDisplayErrorsToModel(model, request);
            result.rejectValue(null, "Invalid.File", "Please choose an Excel file to upload");

            model.addAttribute("status", result);
            model.addAttribute("fileUploadBean", pMB);
            addRoleBeanToModel(model, request);
            return VIEW;
        }
        PayrollRunMasterBean wPRB = IppmsUtilsExt.loadCurrentlyRunningPayroll(genericService, bc);
        if (!wPRB.isNewEntity() && wPRB.isRunning()) {
            result.rejectValue("", "No.Employees", "Payroll is currently being run by " + wPRB.getInitiator().getActualUserName() + ". " + pMB.getName() + ". can not be effected during a Payroll Run");
            addDisplayErrorsToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("fileUploadBean", pMB);
            addRoleBeanToModel(model, request);
            return VIEW;
        }

        if (!multipartFile.getOriginalFilename().endsWith(".xlsx")) {
            if(pMB.isSalaryInfo()){
                List<SalaryType> salaryTypeList = this.genericService.loadAllObjectsWithSingleCondition(SalaryType.class, getBusinessClientIdPredicate(request),"name");
                model.addAttribute("salaryTypeList",salaryTypeList);
            }
            addDisplayErrorsToModel(model, request);
            result.rejectValue(null, "Invalid.File", "Please only Microsoft Excel 2007+ (.xlsx) file can be processed");

            model.addAttribute("status", result);
            model.addAttribute("fileUploadBean", pMB);
            addRoleBeanToModel(model, request);
            return VIEW;
        }
        

        if(pMB.isLeaveBonus()){

            if(IppmsUtils.isNullOrLessThanOne(pMB.getMdaInstId())){
                addDisplayErrorsToModel(model, request);
                result.rejectValue(null, "Invalid.File", bc.getMdaTitle()+" is required");

                model.addAttribute("status", result);
                model.addAttribute("fileUploadBean", pMB);
                addRoleBeanToModel(model, request);
                return VIEW;
            }
        }
        else if(pMB.isSalaryInfo()){

            if(IppmsUtils.isNullOrLessThanOne(pMB.getSalaryTypeId())){
                addDisplayErrorsToModel(model, request);
                result.rejectValue(null, "Invalid.File", "Pay Group is required");
                List<SalaryType> salaryTypeList = this.genericService.loadAllObjectsWithSingleCondition(SalaryType.class, getBusinessClientIdPredicate(request),"name");

                model.addAttribute("salaryTypeList",salaryTypeList);
                model.addAttribute("status", result);
                model.addAttribute("fileUploadBean", pMB);
                addRoleBeanToModel(model, request);
                return VIEW;
            }

            int noOfSalInfo = this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder().addPredicate(Arrays.asList(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),
                    CustomPredicate.procurePredicate("salaryType.id",pMB.getSalaryTypeId()))),SalaryInfo.class);
            if(noOfSalInfo > 0){
                addDisplayErrorsToModel(model, request);
                result.rejectValue(null, "Invalid.File", "Salary Information exists for this Pay Group.");
                result.rejectValue(null, "Invalid.File", "This Module is ONLY for NEW PAY GROUP.");
                List<SalaryType> salaryTypeList = this.genericService.loadAllObjectsWithSingleCondition(SalaryType.class, getBusinessClientIdPredicate(request),"name");
                model.addAttribute("salaryTypeList",salaryTypeList);
                model.addAttribute("status", result);
                model.addAttribute("fileUploadBean", pMB);
                addRoleBeanToModel(model, request);
                return VIEW;
            }
        }
        PayrollFlag wPF = IppmsUtilsExt.getPayrollFlagForClient(genericService, bc);
        uploadUniqueIdentifier = PassPhrase.generateFileUploadId(bc.getUserName(), bc.getLogonUserRole());
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        InputStream is = multipartFile.getInputStream();
        XSSFWorkbook workbook = new XSSFWorkbook(is);
        is.close();
        if(pMB.isLeaveBonus()){
            ParseLeaveBonus peas = new ParseLeaveBonus(workbook, pMB, bc.getSessionId(),
                    fileUploadService.loadActiveEmployeesInMDAAsMap(bc,pMB.getMdaInstId()),uploadUniqueIdentifier, wPF.getApprovedMonthInd(),
                    wPF.getApprovedYearInd(),fileName,bc);

            Thread t = new Thread(peas);
            t.start();

            addSessionAttribute(request, "leaveBonusFileUpload", peas);

            return "redirect:uploadLeaveBonusStatus.do";
        } else {
            ParseExcelAndSchedulePayment peas;
            int year = wPF.getApprovedYearInd();
            if(wPF.getApprovedMonthInd() == 12)
                year += 1;

            if(pMB.isStepIncrement()){
                peas = new ParseExcelAndSchedulePayment(workbook, pMB,
                        fileUploadService.makeStepIncrementTrackerMap(bc,year), fileUploadService.makeLevelStepSalaryMapMap(bc), fileUploadService.makeAllowanceRuleMap(bc),
                        fileUploadService.makeIncrementApprovalList(bc,year),fileUploadService.makeEmployeeMap(bc, true),
                        uploadUniqueIdentifier, fileUploadService.makeEmployeeMap(bc, false), bc, fileUploadService.makeSuspendedMap(bc), fileName,loadConfigurationBean(request));
            }else{
                peas = new ParseExcelAndSchedulePayment(workbook, pMB, bc.getSessionId(),
                        fileUploadService.makeDedLoanSpecHashMap(pMB.getObjectTypeClass(), bc), fileUploadService.makeEmployeeMap(bc, true),
                        uploadUniqueIdentifier, wPF.getApprovedMonthInd(), wPF.getApprovedYearInd(), fileUploadService.makePayTypesAsMap(),
                        fileUploadService.makeEmployeeMap(bc, false), bc, fileUploadService.makeSuspendedMap(bc), fileName,loadConfigurationBean(request));
            }


            Thread t = new Thread(peas);
            t.start();
            addSessionAttribute(request, "fileUpload", peas);
        }


        return "redirect:uploadStatus.do";
    }


}