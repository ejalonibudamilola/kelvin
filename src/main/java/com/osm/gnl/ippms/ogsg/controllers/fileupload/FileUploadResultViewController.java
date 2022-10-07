package com.osm.gnl.ippms.ogsg.controllers.fileupload;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.FileUploadService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.FileParseBean;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryTemp;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.engine.*;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;


@Controller
@RequestMapping({"/fileUploadReport.do"})
@SessionAttributes(types = {FileParseBean.class})
public class FileUploadResultViewController extends BaseController {


    private final PaycheckService paycheckService;
    private final FileUploadService fileUploadService;

    @Autowired
    public FileUploadResultViewController(PaycheckService paycheckService, FileUploadService fileUploadService) {
        this.paycheckService = paycheckService;
        this.fileUploadService = fileUploadService;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"uid"})
    public String setupForm(@RequestParam("uid") String pEmpId, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request);
        BusinessCertificate bc = super.getBusinessCertificate(request);
        Object wFPB = getSessionAttribute(request, pEmpId);
        if (wFPB == null)
            return "redirect:fileUploadFailed.do";

        FileParseBean _wFPB = (FileParseBean) wFPB;
        _wFPB.setDoNotShowButton(false);
        switch (_wFPB.getObjectTypeInd()) {
            case 3:
                _wFPB.setDisplayErrors("Deductions To Add");
                _wFPB.setDisplayTitle("Deduction Summary");
                _wFPB.setName("Deductions");
                 break;
            case 2:
                _wFPB.setDisplayErrors("Loans To Add.");
                _wFPB.setDisplayTitle("Loan Summary");
                _wFPB.setName("Loans");
                 break;
            case 5:
                //_wFPB.setSpecialAllowance(true);
                _wFPB.setName("Special Allowance");
                _wFPB.setDisplayErrors("Special Allowances To Add.");
                _wFPB.setDisplayTitle("Special Allowance Summary");
                break;
            case 6:
                _wFPB.setName("Salary Structure");
                _wFPB.setDisplayErrors("Salary Structure To Add.");
                _wFPB.setDisplayTitle("Salary Structure Summary");
                break;
            case 7:
                _wFPB.setName("Step Increment");
                _wFPB.setDisplayErrors("No of Step Increments.");
                _wFPB.setDisplayTitle("Step Increment Summary");
                break;
        }

        if (_wFPB.isHasSaveList()) {
            if(_wFPB.isSalaryInfo()) {
                Vector<SalaryTemp> vector = _wFPB.getPayGroupList();

                _wFPB.setPayGroupList(setSalInfoFormDisplayStyle(vector));
                _wFPB.setTotalNumberOfRecords(vector.size());
                _wFPB.setPayTypeName(this.genericService.loadObjectById(SalaryType.class, _wFPB.getSalaryTypeId()).getDescription());
                //addSessionAttribute(request, "salaryBean", _wFPB);
            } else
                _wFPB.setListToSave(setFormDisplayStyle(_wFPB.getListToSave()));
                _wFPB.setTotalNumberOfRecords(_wFPB.getListToSave().size());
        } else {
            _wFPB.setDoNotShowButton(true);
        }
        if (_wFPB.isHasErrorList()) {
            _wFPB.setTotalNumberOfRecords(_wFPB.getTotalNumberOfRecords() + _wFPB.getErrorList().size());
            _wFPB.setErrorList(setFormDisplayStyle(_wFPB.getErrorList()));
        }
        model.addAttribute("roleBean", bc);
        model.addAttribute("fileUploadResult", _wFPB);
        model.addAttribute("uid",pEmpId);

        return "fileupload/fileUploadResultForm";
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"uid", "s"})
    public String setupForm(@RequestParam("uid") String pEmpId, @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {

        SessionManagerService.manageSession(request);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        Object wFPB = getSessionAttribute(request, pEmpId);
        int objectTypeInd;
        if (wFPB == null)
            return "redirect:fileUploadFailed.do";

        objectTypeInd = ((FileParseBean) wFPB).getObjectTypeInd();

        switch (objectTypeInd) {
            case 3:
                ((FileParseBean) wFPB).setDisplayErrors("Deductions Added Successfully.");
                ((FileParseBean) wFPB).setDisplayTitle("Deduction Summary");
                break;
            case 2:
                ((FileParseBean) wFPB).setDisplayErrors("Loans Added Successfully.");
                ((FileParseBean) wFPB).setDisplayTitle("Loan Summary");
                break;
            case 5:
                ((FileParseBean) wFPB).setDisplayErrors("Special Added Successfully.");
                ((FileParseBean) wFPB).setDisplayTitle("Special Allowance Summary");
                break;
            case 6:

                ((FileParseBean) wFPB).setDisplayErrors("Salary Structure Added Successfully.");
                ((FileParseBean) wFPB).setDisplayTitle("Salary Structure Summary");
                break;
            case 7:

                ((FileParseBean) wFPB).setDisplayErrors("Step Incremented Successfully.");
                ((FileParseBean) wFPB).setDisplayTitle("Step Increment Summary");
                break;
        }

        model.addAttribute("saved", true);
        model.addAttribute("roleBean", bc);
        model.addAttribute("fileUploadResult", wFPB);
        model.addAttribute("uid",pEmpId);
        return "fileupload/fileUploadResultForm";
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @RequestParam(value = "_create", required = false) String pCreate, @RequestParam(value = "_edit", required = false) String pEdit,
                                @ModelAttribute("fileUploadResult") FileParseBean pEHB, BindingResult pResult, SessionStatus status, Model model, HttpServletRequest request)
            throws Exception {

        SessionManagerService.manageSession(request);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        if (super.isCancelRequest(request, cancel)) {
            removeSessionAttribute(request, pEHB.getUniqueUploadId());
            return "redirect:fileUploadDashboard.do";
        }


        if (isButtonTypeClick(request, REQUEST_PARAM_EDIT)) {
            return "redirect:fixFileUploadError.do?uid=" + pEHB.getUniqueUploadId();
        }
        Thread thread;

        if (isButtonTypeClick(request, REQUEST_PARAM_CREATE)) {

            HashMap<?, ?> wPayTypesList;
            addSessionAttribute(request, IConstants.FILE_UPLOAD_UUID, pEHB.getUniqueUploadId());
            LocalDate _wCal = paycheckService.getPendingPaycheckRunMonthAndYear(bc);
            PayrollFlag wPF = IppmsUtilsExt.getPayrollFlagForClient(genericService, bc);

            switch (pEHB.getObjectTypeInd()) {
                case 3:
                    wPayTypesList = this.fileUploadService.makePayTypeHashMapByClassName(bc, pEHB.getObjectTypeClass());

                    PersistFileUploadDeductions persistFileUploadDeductions = new PersistFileUploadDeductions(genericService, wPayTypesList, pEHB, bc, wPF, _wCal);
                    addSessionAttribute(request, IConstants.FU_OBJ_KEY, persistFileUploadDeductions);
                    addSessionAttribute(request, FILE_UPLOAD_OBJ_IND, pEHB.getObjectTypeInd());
                    thread = new Thread(persistFileUploadDeductions);
                    thread.start();


                    break;
                case 2:

                    PersistFileUploadLoans persistFileUploadLoans = new PersistFileUploadLoans(genericService, pEHB, bc, wPF, _wCal, IppmsUtilsExt.loadConfigurationBean(genericService,bc));
                    addSessionAttribute(request, IConstants.FU_OBJ_KEY, persistFileUploadLoans);
                    addSessionAttribute(request, FILE_UPLOAD_OBJ_IND, pEHB.getObjectTypeInd());
                    thread = new Thread(persistFileUploadLoans);
                    thread.start();


                    break;
                case 5:
                  //  wPayTypesList = this.fileUploadService.makePayTypeHashMapByClassName(bc, pEHB.getObjectTypeClass());
                    PersistFileUploadSpecAllow persistFileUploadSpecAllow = new PersistFileUploadSpecAllow(genericService, pEHB, bc, wPF, _wCal);
                    addSessionAttribute(request, IConstants.FU_OBJ_KEY, persistFileUploadSpecAllow);
                    addSessionAttribute(request, FILE_UPLOAD_OBJ_IND, pEHB.getObjectTypeInd());
                    thread = new Thread(persistFileUploadSpecAllow);
                    thread.start();


                    break;
                case 6:
                    //--Create the Master Bean for Salary Temp

                    PersistSalaryInfo persistSalaryInfo = new PersistSalaryInfo(genericService, pEHB, bc, wPF, _wCal);
                    addSessionAttribute(request, IConstants.FU_OBJ_KEY, persistSalaryInfo);
                    addSessionAttribute(request, FILE_UPLOAD_OBJ_IND, pEHB.getObjectTypeInd());
                    thread = new Thread(persistSalaryInfo);
                    thread.start();
                    break;
                case 7:
                    //--Create the Master Bean for Salary Temp

                    PerformStepIncrement performStepIncrement = new PerformStepIncrement(genericService, pEHB, bc, wPF, _wCal);
                    addSessionAttribute(request, IConstants.FU_OBJ_KEY, performStepIncrement);
                    addSessionAttribute(request, FILE_UPLOAD_OBJ_IND, pEHB.getObjectTypeInd());
                    thread = new Thread(performStepIncrement);
                    thread.start();
                    break;


            }


        }

        return "redirect:fileUploadSaveStatus.do";

    }


    private Vector<NamedEntity> setFormDisplayStyle(Vector<NamedEntity> pEmpList) {
        int i = 1;
        for (NamedEntity e : pEmpList) {
            if (i % 2 == 1)
                e.setDisplayStyle("reportEven");
            else {
                e.setDisplayStyle("reportOdd");
            }
            i++;
        }
        return pEmpList;
    }

    private Vector<SalaryTemp> setSalInfoFormDisplayStyle(Vector<SalaryTemp> pEmpList) {
        int i = 1;
        for (SalaryTemp e : pEmpList) {
            if (i % 2 == 1)
                e.setDisplayStyle("reportEven");
            else {
                e.setDisplayStyle("reportOdd");
            }
            i++;
        }
        Collections.sort(pEmpList, Comparator.comparing(SalaryTemp::getLevel).thenComparing(SalaryTemp::getStep));
        return pEmpList;
    }
}