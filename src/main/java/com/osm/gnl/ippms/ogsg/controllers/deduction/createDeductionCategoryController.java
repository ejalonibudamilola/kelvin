package com.osm.gnl.ippms.ogsg.controllers.deduction;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionCategory;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.validators.deduction.CreateDeductionCategoryValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;

@Controller
@RequestMapping({ "/createDeductionCategory.do" })
@SessionAttributes(types = { EmpDeductionCategory.class })
public class createDeductionCategoryController extends BaseController {

    @Autowired
    CreateDeductionCategoryValidator createDeductionCategoryValidator;

    private final String VIEW_NAME = "deduction/createDeductionCategoryForm";

    @RequestMapping(method = { RequestMethod.GET })
    public String setupForm(Model model, HttpServletRequest request)
            throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        EmpDeductionCategory wEDC = new EmpDeductionCategory();

        model.addAttribute("deductionCategoryBean", wEDC);
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }


    @RequestMapping(method = { RequestMethod.GET }, params = { "dtid", "s" })
    public String setupForm(@RequestParam("dtid") Long pDtid, @RequestParam("s") int pSaved, Model model,
                            HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        EmpDeductionCategory wEDT = this.genericService.loadObjectById(EmpDeductionCategory.class, pDtid);

        String actionCompleted = "Deduction Category " + wEDT.getDescription() + " Created Successfully";

        model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
        model.addAttribute("saved", Boolean.valueOf(true));
        model.addAttribute("deductionCategoryBean", wEDT);
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }



    @RequestMapping(method = { RequestMethod.POST })
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("deductionCategoryBean") EmpDeductionCategory pEHB, BindingResult result, SessionStatus status,
                                Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);



        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return CONFIG_HOME_URL;
        }


        createDeductionCategoryValidator.validate(pEHB, result, bc);
        if (result.hasErrors()) {
            model.addAttribute("status", result);
            model.addAttribute("deductionTypeBean", pEHB);
            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            return VIEW_NAME;
        }

        pEHB.setLastModBy(new User(bc.getLoginId()));
        pEHB.setCreatedBy(new User(bc.getLoginId()));
        pEHB.setLastModTs(Timestamp.from(Instant.now()));
        pEHB.setBusinessClientId(bc.getBusinessClientInstId());
        this.genericService.saveObject(pEHB);

        return "redirect:createDeductionCategory.do?dtid=" + pEHB.getId() + "&s=1";

    }
}
