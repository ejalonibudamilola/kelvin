package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.Department;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.MdaType;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.hr.MDAValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping({"/editMda.do"})
@SessionAttributes(types = {MdaInfo.class})
public class EditMdaFormController extends BaseController {

    @Autowired
    private MDAValidator validator;

    private final String VIEW = "hr_mda/editMdaForm";

    
    @ModelAttribute("mdaTypeList")
    private List<MdaType> makeMdaList(HttpServletRequest request) {
        BusinessCertificate bc = super.getBusinessCertificate(request);

        return this.genericService.loadAllObjectsWithSingleCondition(MdaType.class,
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), "name");
    }


    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(@RequestParam(value = "mid", required = false) Long pMdaId, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);
        MdaInfo wMdaInfo = new MdaInfo();
        if (IppmsUtils.isNotNullAndGreaterThanZero(pMdaId)) {

            wMdaInfo = this.genericService.loadObjectUsingRestriction(MdaInfo.class,
                    Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                            CustomPredicate.procurePredicate("id", pMdaId)));
            wMdaInfo.setEditMode(true);
            addPageTitle(model, "Edit");
        } else {
            addPageTitle(model, "Create");
        }

        model.addAttribute("miniBean", wMdaInfo);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"pid", "s"})
    public String setupForm(@RequestParam("pid") Long pAid, @RequestParam("s") int pSaved, Model model,
                            HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        MdaInfo wMdaInfo = this.genericService.loadObjectUsingRestriction(MdaInfo.class,
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("id", pAid)));
        wMdaInfo.setEditMode(true);
        String wActionCompleted = "";
        if (pSaved == 0) {
            wActionCompleted = bc.getMdaTitle() + " " + wMdaInfo.getName() + " created successfully.";
            addPageTitle(model, "Create");
        } else {
            addPageTitle(model, "Edit");
            wActionCompleted = bc.getMdaTitle() + " " + wMdaInfo.getName() + " edited successfully.";
        }


        model.addAttribute(IConstants.SAVED_MSG, wActionCompleted);
        model.addAttribute("saved", true);
        model.addAttribute("miniBean", wMdaInfo);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("miniBean") MdaInfo pHCDD, BindingResult result, SessionStatus status, Model model,
                                HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return "redirect:selectPABI.do";
        }

        validator.validate(pHCDD, result, bc);
        if (result.hasErrors()) {
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
            model.addAttribute("status", result);
            model.addAttribute("miniBean", pHCDD);
            model.addAttribute("roleBean", bc);
            if(pHCDD.isEditMode())
                addPageTitle(model, "Edit");
            else
                addPageTitle(model, "Create");
            return VIEW;
        }
        int addendum = 0;

        if (!pHCDD.isEditMode()) {
            pHCDD.setCreatedBy(new User(bc.getLoginId()));
            pHCDD.setCreationDate(Timestamp.from(Instant.now()));

        } else {
            addendum = 1;
        }
        pHCDD.setBusinessClientId(bc.getBusinessClientInstId());
        pHCDD.setLastModBy(new User(bc.getLoginId()));
        pHCDD.setLastModTs(Timestamp.from(Instant.now()));

        this.genericService.saveObject(pHCDD);

        if(addendum == 0){
            //Map Default Department.
            Department department = this.genericService.loadObjectUsingRestriction(Department.class,Arrays.asList(getBusinessClientIdPredicate(request),CustomPredicate.procurePredicate("defaultInd",ON)));
            if(!department.isNewEntity()) {
                MdaDeptMap wADM = new MdaDeptMap(bc.getBusinessClientInstId(), pHCDD.getId(), department.getId());
                wADM.setLastModBy(new User(bc.getLoginId()));
                wADM.setCreatedBy(new User(bc.getLoginId()));
                wADM.setCreationDate(Timestamp.from(Instant.now()));
                wADM.setLastModTs(Timestamp.from(Instant.now()));
                wADM.setDeptDirector("TBD");
                this.genericService.storeObject(wADM);
            }
        }

        return "redirect:editMda.do?pid=" + pHCDD.getId() + "&s=" + addendum;
    }
}