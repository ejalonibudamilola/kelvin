package com.osm.gnl.ippms.ogsg.controllers.allowance;

import com.osm.gnl.ippms.ogsg.base.services.SimulationService;
import com.osm.gnl.ippms.ogsg.domain.allowance.LeaveTransportGrantHolder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.allowance.service.LeaveTransportGrantService;
import com.osm.gnl.ippms.ogsg.base.services.HRService;
import com.osm.gnl.ippms.ogsg.ltg.domain.LtgMasterBean;
import com.osm.gnl.ippms.ogsg.engine.CalculatePayPerABMPSimulator;
import com.osm.gnl.ippms.ogsg.engine.SimulatePayrollWithLtg;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.validators.allowance.LtgValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@SessionAttributes(types={LeaveTransportGrantHolder.class})
public class LeaveTransportGrantController extends BaseController {
    private final String VIEW = "LTG/casp/applyLTGToMBAPForm";

    @Autowired
    private HRService hrService;

    @Autowired
    private SimulationService simulationService;

    @Autowired
    private LtgValidator validator;

    public LeaveTransportGrantController() {}

    @RequestMapping(value = "/applyLTGToMABP.do", method = RequestMethod.GET)
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, InstantiationException, IllegalAccessException, EpmAuthenticationException {
        BusinessCertificate bc = this.getBusinessCertificate(request);
        LeaveTransportGrantHolder wLTGH = new LeaveTransportGrantHolder();
        SessionManagerService.manageSession(request, model);
        LeaveTransportGrantService.setupForm(model, genericService, wLTGH, bc);
        addSessionAttribute(request, LTG_ATTR_NAME, wLTGH);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }

    @RequestMapping(value = "/applyLTGToMABP.do", method=RequestMethod.GET, params={"spr"})
    public String setupForm(@RequestParam("spr") String pPid, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        LeaveTransportGrantHolder wLTGH = new LeaveTransportGrantHolder();
        LeaveTransportGrantService.setupForm(pPid, model, genericService, wLTGH,bc);
        addSessionAttribute(request, LTG_ATTR_NAME, wLTGH);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }

    @RequestMapping(value = "/applyLTGToMABP.do", method = RequestMethod.GET, params={"pid"})
    public String setupForm(@RequestParam("pid") Long pPid, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);

        LeaveTransportGrantHolder wLTGH = (LeaveTransportGrantHolder) getSessionAttribute(request, LTG_ATTR_NAME);

        if (wLTGH == null) {
            return "redirect:applyLTGToMABP.do";
        }

        LeaveTransportGrantService.setupForm(pPid, model, wLTGH, hrService, bc);
        addSessionAttribute(request, LTG_ATTR_NAME, wLTGH);
        return VIEW;
    }

    @RequestMapping(value = "/applyLTGToMABP.do", method=RequestMethod.GET, params={"eid", "act"})
    public String setupForm(@RequestParam("eid") Long pPid, @RequestParam("act") String pAct, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        LeaveTransportGrantHolder wLTGH = (LeaveTransportGrantHolder) getSessionAttribute(request, LTG_ATTR_NAME);
        if (wLTGH == null) {
            return "redirect:applyLTGToMABP.do";
        }

        wLTGH = LeaveTransportGrantService.setupForm(pPid, pAct, model, bc, wLTGH);

        addSessionAttribute(request, LTG_ATTR_NAME, wLTGH);
        return VIEW;
    }


    @RequestMapping(value = "/applyLTGToMABP.do", method=RequestMethod.POST)
    public String processSubmit(@RequestParam(value="_confirm", required=false) String confirm, @RequestParam(value="_add", required=false)
            String add, @RequestParam(value="_simulate", required=false) String simulate, @RequestParam(value="_save", required=false) String save,
                                @RequestParam(value="_close", required=false) String close, @ModelAttribute("ltgMiniBean") LeaveTransportGrantHolder pHMB,
                                BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
            throws Exception
    {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);



        if (isButtonTypeClick(request,  REQUEST_PARAM_CLOSE))
        {
            removeSessionAttribute(request, LTG_ATTR_NAME);
            return REDIRECT_TO_DASHBOARD;
        }



        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
        {
            if ((pHMB.isForPayrollRun()) || (pHMB.isForSimulation())) {
                removeSessionAttribute(request, LTG_ATTR_NAME);
                return "redirect:applyLTGToMABP.do";
            }
            return REDIRECT_TO_DASHBOARD;
        }


        if (isButtonTypeClick(request,  REQUEST_PARAM_ADD)) {
            addSessionAttribute(request, LTG_ATTR_NAME, pHMB);

            validator.validateForAdd(pHMB, result, bc);

            if(result.hasErrors()) {
                LeaveTransportGrantService.setModelData(model, result, pHMB, bc);
                return VIEW;
            }


            return "redirect:applyLTGToMABP.do?pid=" + pHMB.getMdaInfo().getId();
        }


        if (isButtonTypeClick(request, REQUEST_PARAM_SIMULATE))
        {

            LeaveTransportGrantService.
                    processButtonRequestParamSimulate(model, result, pHMB, bc, genericService, validator);
            if (!pHMB.isWarningIssued() || result.hasErrors()) {
                return VIEW;
            }

        }


        if (isButtonTypeClick(request, "_save"))
        {
            pHMB.setMessageString("Select Month to apply LTG");
            pHMB.setForPayrollRun(true);
            pHMB.setForSimulation(false);
            pHMB = LeaveTransportGrantService.setMonthList(pHMB, bc.getBusinessClientInstId(), genericService);
            pHMB.setShowMonthList(SHOW_ROW);
            pHMB.setShowNameRow(SHOW_ROW);

            if (!pHMB.isWarningIssued())
            {
                validator.validate(pHMB, result);
                if(result.hasErrors()) {
                    LeaveTransportGrantService.setModelData(model, result, pHMB, bc);
                    return VIEW;
                }

            }
        }



        if (isButtonTypeClick(request, REQUEST_PARAM_CONFIRM))
        {

            validator.validateForConfirm(pHMB, result);
            if(result.hasErrors()) {
                LeaveTransportGrantService.setModelData(model, result, pHMB, bc);
                return VIEW;
            }

            if (pHMB.isForSimulation())
            {
                LtgMasterBean wLMB = LeaveTransportGrantService.createLtgMasterBean(pHMB, bc, hrService);
                CalculatePayPerABMPSimulator wCalcPayPerEmp =
                        LeaveTransportGrantService.processGrantHolderOnSimulationConfirm(pHMB, wLMB, bc, hrService);

                SimulatePayrollWithLtg wCalcPay = new SimulatePayrollWithLtg(simulationService,this.hrService, wCalcPayPerEmp, wLMB.getId(), bc, wLMB.getSimulationMonth(), wLMB.getSimulationYear());

                addSessionAttribute(request, "simCalcPay", wCalcPay);
                addSessionAttribute(request, "currLtgId", wLMB.getId());


                Thread t = new Thread(wCalcPay);
                t.start();
//                return "redirect:displayStatus.do?ftr=future&ltg="+wLMB.getId();
                    return "redirect:displayLtgStatus.do";
            }if (pHMB.isForPayrollRun())
            {
                LtgMasterBean wLMB = hrService.loadLtgMasterBeanByMonthAndYear(pHMB.getMonthId(), Calendar.getInstance().get(1), false);

                if (!wLMB.isNewEntity()) {
                    addSessionAttribute(request, LTG_ATTR_NAME, pHMB);
                    return "redirect:existingLtgFound.do?lid=" + wLMB.getId();
                }

                LeaveTransportGrantService.processGrantHolderOnPayrollConfirm(pHMB, bc, hrService);
                removeSessionAttribute(request, LTG_ATTR_NAME);
            }


        }
        return REDIRECT_TO_DASHBOARD;
    }

     @RequestMapping(value="/existingLtgFound.do", method=RequestMethod.GET, params={"lid"})
    public String setupViewExistingLTGForm(@RequestParam("lid") Long pPid, Model model, HttpServletRequest request) throws Exception
    {
        SessionManagerService.manageSession(request, model);
        LeaveTransportGrantService.loadLTGFormData(pPid, model, request, genericService, getBusinessCertificate(request));
        return "existingLtgInstructionForm";
    }

    @RequestMapping(value="/existingLtgFound.do", method = RequestMethod.POST)
    public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
                                @RequestParam(value="_confirm", required=false) String confirm, @RequestParam(value="_replace", required=false) String replace, @ModelAttribute("miniBean") PaginatedBean pBE, BindingResult result, SessionStatus status, Model model, @RequestParam(value="_add", required=false) String add, HttpServletRequest request)
            throws Exception
    {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
        {
            return REDIRECT_TO_DASHBOARD;
        }

        if (isButtonTypeClick(request, REQUEST_PARAM_REPLACE)) {
            LeaveTransportGrantService.processExistingLTGAddParamButton(pBE, model, result);
            return "existingLtgInstructionForm";
        }

        if (isButtonTypeClick(request, REQUEST_PARAM_CONFIRM)) {
            LeaveTransportGrantHolder leaveTransportGrantHolder = (LeaveTransportGrantHolder) getSessionAttribute(request, LTG_ATTR_NAME);
            if (leaveTransportGrantHolder == null)
                return REDIRECT_TO_DASHBOARD;
            LeaveTransportGrantService.processExistingLTGConfirmParamButton(leaveTransportGrantHolder, pBE, bc, genericService);
        }

        removeSessionAttribute(request, LTG_ATTR_NAME);
        return "redirect:applyLTGToMABP.do";
    }
}
