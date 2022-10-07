package com.osm.gnl.ippms.ogsg.controllers.allowance;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.allowance.service.AllowanceService;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.validators.allowance.SpecialAllowanceInfoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
@SessionAttributes("empSpecAllow")
public class AllowanceFormController extends BaseController {


    private final SpecialAllowanceInfoValidator validator;
    private final AllowanceService allowanceService;

    @Autowired
    public AllowanceFormController(SpecialAllowanceInfoValidator validator, AllowanceService allowanceService) {
        this.validator = validator;
        this.allowanceService = allowanceService;
    }


    @ModelAttribute("specAllowType")
    public List<SpecialAllowanceType> getSpecAllowType(HttpServletRequest request) {
        return allowanceService.getSpecAllowType(getBusinessCertificate(request).getBusinessClientInstId());
    }

    @RequestMapping(value = "/addSpecialAllowance.do", method = RequestMethod.GET, params = {"eid"})
    public String setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);
        NamedEntity ne = (NamedEntity) BaseController.getSessionAttribute(request, IConstants.NAMED_ENTITY);
        allowanceService.setupCreateForm(pEmpId, model, ne, getBusinessCertificate(request));
        addRoleBeanToModel(model, request);
        return "allowance/casp/createSpecAllowForm";
    }

    @RequestMapping(value = "/addSpecialAllowance.do", method = RequestMethod.POST)
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("empSpecAllow") AbstractSpecialAllowanceEntity pSpecAllowInfo, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return "redirect:dedGarnForm.do?eid=" + pSpecAllowInfo.getParentId() + "&pid="
                    + bc.getBusinessClientInstId() + "&oid=0&tid=" + SPEC_ALLOW_IND;
        }
        validator.validate(pSpecAllowInfo, result, bc);
        NamedEntity namedEntity = (NamedEntity) getSessionAttribute(request, IConstants.NAMED_ENTITY);
        if (result.hasErrors()) {

            allowanceService.setCreateAllowanceResult(pSpecAllowInfo, result, model, namedEntity);
            addRoleBeanToModel(model, request);
            addDisplayErrorsToModel(model, request);
            if (IppmsUtils.isNotNullAndGreaterThanZero(pSpecAllowInfo.getTypeInstId())) {
                SpecialAllowanceType specialAllowanceType = genericService.loadObjectById(SpecialAllowanceType.class, pSpecAllowInfo.getTypeInstId());
                model.addAttribute("payType", Arrays.asList(specialAllowanceType.getPayTypes()));
            }

            return "allowance/casp/createSpecAllowForm";
        }
        //Now Check if the Amount is > 2M - if it is, create an Approval For it.

        if (!pSpecAllowInfo.isWarningIssued()) {
            allowanceService.setCreateAllowanceWarning(pSpecAllowInfo, result, bc);
            allowanceService.setCreateAllowanceResult(pSpecAllowInfo, result, model, namedEntity);
            if (IppmsUtils.isNotNullAndGreaterThanZero(pSpecAllowInfo.getTypeInstId())) {
                SpecialAllowanceType specialAllowanceType = genericService.loadObjectById(SpecialAllowanceType.class, pSpecAllowInfo.getTypeInstId());
                model.addAttribute("payType", Arrays.asList(specialAllowanceType.getPayTypes()));
            }
            addRoleBeanToModel(model, request);
            addDisplayErrorsToModel(model, request);
            return "allowance/casp/createSpecAllowForm";
        }
        if (pSpecAllowInfo != null) {
            allowanceService.createAllowance(pSpecAllowInfo, bc);
        }
        return "redirect:dedGarnForm.do?eid=" + pSpecAllowInfo.getParentId() + "&pid="
                + bc.getBusinessClientInstId() + "&oid=" + pSpecAllowInfo.getId() + "&tid=" + SPEC_ALLOW_IND;
    }

    @RequestMapping(value = "/editSpecialAllowance.do", method = RequestMethod.GET, params = {"aid", "eid", "atn"})
    public String setupForm(@RequestParam("aid") Long pAllowanceId, @RequestParam("eid") Long pEmpId,
                            @RequestParam("atn") String pActn, Model model, HttpServletRequest request) throws Exception {
        BusinessCertificate bc = this.getBusinessCertificate(request);
        NamedEntity ne = (NamedEntity) getSessionAttribute(request, IConstants.NAMED_ENTITY);
        return allowanceService.prepareModelForEdit(model, pAllowanceId, pEmpId, ne, bc, pActn);
    }

    @RequestMapping(value = "/editSpecialAllowance.do", method = RequestMethod.GET, params = {"aid", "eid"})
    public String setupForm(@RequestParam("aid") Long pAllowanceId, @RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws Exception {
        BusinessCertificate bc = this.getBusinessCertificate(request);
        NamedEntity ne = (NamedEntity) getSessionAttribute(request, IConstants.NAMED_ENTITY);
        return allowanceService.prepareModelForEdit(model, pAllowanceId, pEmpId, ne, bc, null);
    }

    @RequestMapping(value = "/editSpecialAllowance.do", method = RequestMethod.POST)
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel, @RequestParam(value = "_delete", required = false) String delete,
                                @ModelAttribute("empSpecAllow") AbstractSpecialAllowanceEntity pSpecAllowInfo,
                                BindingResult result, SessionStatus status, Model model,
                                HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
            return "redirect:dedGarnForm.do?eid=" + pSpecAllowInfo.getParentId() + "&pid=" + bc.getBusinessClientInstId()
                    + "&oid=" + pSpecAllowInfo.getId() + "&tid=" + SPEC_ALLOW_IND;


        if (allowanceService.getNoOfPendingObjects(pSpecAllowInfo.getId(),
                pSpecAllowInfo.getParentId(), bc) > 0) {
            NamedEntity ne = (NamedEntity) getSessionAttribute(request, IConstants.NAMED_ENTITY);
            if ((ne.isNewEntity()) || (ne.getName() == null)) {
                ne.setName(((AbstractSpecialAllowanceEntity) result.getTarget()).getParentObject().getDisplayNameWivTitlePrefixed());
                ne.setId(((AbstractSpecialAllowanceEntity) result.getTarget()).getParentId());
                addSessionAttribute(request, IConstants.NAMED_ENTITY, ne);
            }
            allowanceService.setEditDeleteResult(pSpecAllowInfo, ne, result, model);
            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            return "allowance/casp/editSpecAllowForm";
        }
        if (isButtonTypeClick(request, REQUEST_PARAM_DELETE)) {
            pSpecAllowInfo.setHasPaycheckInfo(allowanceService.employeeHasSpecialAllowance(pSpecAllowInfo.getId(), pSpecAllowInfo.getParentId(), bc));
            allowanceService.saveOrDeletePaycheckInfo(pSpecAllowInfo, bc);
            return "redirect:dedGarnForm.do?eid=" + pSpecAllowInfo.getParentId() + "&pid=" + bc.getBusinessClientInstId()
                    + "&oid=" + pSpecAllowInfo.getId() + "&tid=" + SPEC_ALLOW_IND;
        }

        validator.validate(pSpecAllowInfo, result, bc);
        if (result.hasErrors()) {
            NamedEntity ne = (NamedEntity) getSessionAttribute(request, IConstants.NAMED_ENTITY);
            allowanceService.setEditErrorResult(pSpecAllowInfo, ne, model, bc, result);
            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            return "allowance/casp/editSpecAllowForm";
        }
        if (pSpecAllowInfo != null) {
            allowanceService.saveFormForEdit(pSpecAllowInfo, bc);
        }
        return "redirect:dedGarnForm.do?eid=" + +pSpecAllowInfo.getParentId() + "&pid=" + bc.getBusinessClientInstId()
                + "&oid=" + pSpecAllowInfo.getId() + "&tid=" + SPEC_ALLOW_IND;
    }
}
