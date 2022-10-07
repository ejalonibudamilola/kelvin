package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.auth.domain.IppmsEncoder;
import com.osm.gnl.ippms.ogsg.auth.domain.Mailer;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.services.MailerService;
import com.osm.gnl.ippms.ogsg.base.services.PaySlipService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.payroll.ApprovePaychecksRerunFormController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.report.ContractHistory;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PaycheckGenerator;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PensionPaycheckGenerator;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpDeductMiniBean;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpGarnMiniBean;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmployeePayMiniBean;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
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
import java.util.*;


@Controller
@RequestMapping({"/paySlip.do"})
@SessionAttributes(types = {EmployeePayMiniBean.class})
public class SingleEmployeePaySlipController extends BaseController {


    private final PaycheckService paycheckService;

    private final PaySlipService paySlipService;
    private final MailerService mailerService;

    private final String VIEW_NAME = "payment/paystubWithDatesForm";


    @ModelAttribute("monthList")
    protected List<NamedEntity> getMonthList() {
        return PayrollBeanUtils.makeAllMonthList();
    }

    @ModelAttribute("yearList")
    protected Collection<NamedEntity> getYearList(HttpServletRequest request) {
        return this.paycheckService.makePaycheckYearList(getBusinessCertificate(request));
    }
    @Autowired
    public SingleEmployeePaySlipController(PaycheckService paycheckService, PaySlipService paySlipService, MailerService mailerService) {
        this.paycheckService = paycheckService;
        this.paySlipService = paySlipService;
        this.mailerService = mailerService;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"eid"})
    public String setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        EmployeePayMiniBean empPayMiniBean = new EmployeePayMiniBean();

        Long pid = this.paycheckService.getMaxPaycheckIdForEmployee(bc, pEmpId);

        AbstractPaycheckEntity empPayBean = (AbstractPaycheckEntity) genericService.loadObjectById(IppmsUtils.getPaycheckClass(bc), pid);
        if(IppmsUtils.isNotNullOrEmpty(empPayBean.getParentObject().getEmail()))
            bc.setHasDefPaySched(true);
        empPayMiniBean.setParentInstId(pEmpId);
        empPayMiniBean.setEmployeeName(empPayBean.getParentObject().getDisplayNameWivTitlePrefixed());
        empPayMiniBean.setEmployeeId(empPayBean.getParentObject().getEmployeeId());
        empPayMiniBean.setRunMonth(LocalDate.now().getMonthValue());
        empPayMiniBean.setRunYear(LocalDate.now().getYear());
        empPayMiniBean.setAdmin(bc.isSuperAdmin());

        return this.generateAndReturnModel(empPayBean, model, request, bc, empPayMiniBean, 0, null);


    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"pid", "rm", "ry"})
    public String setupForm(@RequestParam("pid") Long pPId, @RequestParam("rm") int pRunMonth,
                            @RequestParam("ry") int pRunYear, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);


        EmployeePayMiniBean empPayMiniBean = new EmployeePayMiniBean();
        empPayMiniBean.setAdmin(bc.isSuperAdmin());

        AbstractPaycheckEntity empPayBean = (AbstractPaycheckEntity) genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc), Arrays.asList(  CustomPredicate.procurePredicate("employee.id", pPId),
                  CustomPredicate.procurePredicate("runMonth", pRunMonth),   CustomPredicate.procurePredicate("runYear", pRunYear)));
        empPayMiniBean.setParentInstId(empPayBean.getParentObject().getId());
        if(IppmsUtils.isNotNullOrEmpty(empPayBean.getParentObject().getEmail()))
            bc.setHasDefPaySched(true);
        return this.generateAndReturnModel(empPayBean, model, request, bc, empPayMiniBean, 0, null);


    }

    
    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"pid", "rm", "ry", "s"})
    public String setupForm(@RequestParam("pid") Long pPId, @RequestParam("rm") int pRunMonth,
                            @RequestParam("ry") int pRunYear, @RequestParam("s") int pObjInd, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        bc.setSemaphore(pObjInd);

        EmployeePayMiniBean empPayMiniBean = new EmployeePayMiniBean();
        empPayMiniBean.setAdmin(bc.isSuperAdmin());
        AbstractPaycheckEntity empPayBean = (AbstractPaycheckEntity) genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc), Arrays.asList(CustomPredicate.procurePredicate("employee.id", pPId),
                CustomPredicate.procurePredicate("runMonth", pRunMonth), CustomPredicate.procurePredicate("runYear", pRunYear)));
        empPayMiniBean.setParentInstId(empPayBean.getParentObject().getId());
        if(IppmsUtils.isNotNullOrEmpty(empPayBean.getParentObject().getEmail()))
            bc.setHasDefPaySched(true);
        return this.generateAndReturnModel(empPayBean, model, request, bc, empPayMiniBean, pObjInd, null);

    }
    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"pid", "rm", "ry", "em"})
    public String setupForm(@RequestParam("pid") Long pPId, @RequestParam("rm") int pRunMonth,
                            @RequestParam("ry") int pRunYear, @RequestParam("em") String pObjInd, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        int emailStatus = 0;
        if(pObjInd != null){
            if(pObjInd.equalsIgnoreCase("y"))
                emailStatus = 1;
            else
                emailStatus = 2;
        }

        EmployeePayMiniBean empPayMiniBean = new EmployeePayMiniBean();
        empPayMiniBean.setAdmin(bc.isSuperAdmin());
        AbstractPaycheckEntity empPayBean = (AbstractPaycheckEntity) genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc), Arrays.asList(CustomPredicate.procurePredicate("employee.id", pPId),
                CustomPredicate.procurePredicate("runMonth", pRunMonth), CustomPredicate.procurePredicate("runYear", pRunYear)));
        empPayMiniBean.setParentInstId(empPayBean.getParentObject().getId());
        if(IppmsUtils.isNotNullOrEmpty(empPayBean.getParentObject().getEmail()))
            bc.setHasDefPaySched(true);
        return this.generateAndReturnModel(empPayBean, model, request, bc, empPayMiniBean, emailStatus, pObjInd);

    }
    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @RequestParam(value = "_updateReport", required = false) String updRep,
                                @ModelAttribute("empPayMiniBean") EmployeePayMiniBean pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
            throws Exception {
        pLPB.setShowRow(IConstants.HIDE_ROW);
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            if(Navigator.getInstance(IppmsEncoder.getSessionKey()).getFromClass().isAssignableFrom(ApprovePaychecksRerunFormController.class)){
                return Navigator.getInstance(IppmsEncoder.getSessionKey()).getFromForm();
            }
            return "redirect:reportsOverview.do";
        }

        if (isButtonTypeClick(request, REQUEST_PARAM_UPDATE_REPORT)) {
            if (pLPB.getRunMonth() == -1) {
                result.rejectValue("", "InvalidValue", "Please select a value for 'Payslip Month'.");
                addDisplayErrorsToModel(model, request);

                model.addAttribute("status", result);
                model.addAttribute("instructionList", pLPB.getInstructionList());
                model.addAttribute("mandatoryList", pLPB.getMandList());
                model.addAttribute("deductList", pLPB.getDeductList());
                model.addAttribute("garnList", pLPB.getGarnList());
                model.addAttribute("empPayMiniBean", pLPB);
                model.addAttribute("roleBean", bc);

                return VIEW_NAME;
            } else if (pLPB.getRunYear() == 0) {
                result.rejectValue("", "InvalidValue", "Please select a value for 'Payslip Year'.");
                addDisplayErrorsToModel(model, request);

                model.addAttribute("status", result);
                model.addAttribute("instructionList", pLPB.getInstructionList());
                model.addAttribute("mandatoryList", pLPB.getMandList());
                model.addAttribute("deductList", pLPB.getDeductList());
                model.addAttribute("garnList", pLPB.getGarnList());
                model.addAttribute("empPayMiniBean", pLPB);
                model.addAttribute("roleBean", bc);

                return VIEW_NAME;
            }
            AbstractPaycheckEntity empPayBean = (AbstractPaycheckEntity) genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc), Arrays.asList(CustomPredicate.procurePredicate("employee.id", pLPB.getParentInstId()),
                    CustomPredicate.procurePredicate("runMonth", pLPB.getRunMonth()), CustomPredicate.procurePredicate("runYear", pLPB.getRunYear())));
            if (empPayBean == null || empPayBean.isNewEntity()) {

                result.rejectValue("", "InvalidValue", pLPB.getEmployeeName() + " [ " + pLPB.getEmployeeId() + " ]  was not paid through this IPPMS  for " + PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pLPB.getRunMonth(), pLPB.getRunYear()));
                addDisplayErrorsToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("instructionList", pLPB.getInstructionList());
                model.addAttribute("mandatoryList", pLPB.getMandList());
                model.addAttribute("deductList", pLPB.getDeductList());
                model.addAttribute("garnList", pLPB.getGarnList());
                model.addAttribute("empPayMiniBean", pLPB);
                model.addAttribute("roleBean", bc);

                return VIEW_NAME;
            }
            return "redirect:paySlip.do?pid=" + empPayBean.getParentObject().getId() + "&rm=" + pLPB.getRunMonth() + "&ry=" + pLPB.getRunYear();
        }
        if(this.isButtonTypeClick(request,REQUEST_PARAM_SEND_EMAIL)){

            AbstractEmployeeEntity emp = IppmsUtils.loadEmployee(genericService,pLPB.getParentInstId(),bc);

            if(!BaseValidator.isEmailValid(emp.getEmail())){
                result.rejectValue("", "InvalidValue", pLPB.getEmployeeName() + " [ " + pLPB.getEmployeeId() + " ] Does not have a valid Email Address according to IPPMS standards.");
                result.rejectValue("", "InvalidValue", "Invalid Email Address --> "+emp.getEmail());
                addDisplayErrorsToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("instructionList", pLPB.getInstructionList());
                model.addAttribute("mandatoryList", pLPB.getMandList());
                model.addAttribute("deductList", pLPB.getDeductList());
                model.addAttribute("garnList", pLPB.getGarnList());
                model.addAttribute("empPayMiniBean", pLPB);
                model.addAttribute("roleBean", bc);

                return VIEW_NAME;
            }

            Mailer mailer = new Mailer();

            mailer.setId(pLPB.getParentInstId());
            mailer.setRecipient(emp.getEmail());
            mailer.setRunMonth(pLPB.getRunMonth());
            mailer.setRunYear(pLPB.getRunYear());
            mailer.setBusinessCertificate(bc);
            mailer.setHttpServletRequest(request);

            boolean bool = mailerService.sendMailWithAttachments(mailer);
            if(bool)
                return "redirect:paySlip.do?pid=" +pLPB.getParentInstId() + "&rm=" + pLPB.getRunMonth() + "&ry=" + pLPB.getRunYear()+"&em=y";
            else
                return "redirect:paySlip.do?pid=" +pLPB.getParentInstId() + "&rm=" + pLPB.getRunMonth() + "&ry=" + pLPB.getRunYear()+"&em=n";

        }

        return "redirect:reportsOverview.do";
    }

    private String generateAndReturnModel(AbstractPaycheckEntity empPayBean, Model model, HttpServletRequest request, BusinessCertificate bc,
                                          EmployeePayMiniBean empPayMiniBean, int pObjInd, String emailSent) throws Exception {

        if (empPayBean.isNewEntity()) {
            empPayMiniBean.setShowRow(IConstants.SHOW_ROW);
            empPayMiniBean.setErrorMsg("No Paycheck Found for " + bc.getStaffTypeName());
            addRoleBeanToModel(model, request);
            model.addAttribute("instructionList", new ArrayList<NamedEntity>());
            model.addAttribute("mandatoryList", new ArrayList<EmpDeductMiniBean>());
            model.addAttribute("deductList",  new ArrayList<EmpDeductMiniBean>());
            model.addAttribute("garnList",  new ArrayList<EmpGarnMiniBean>());
            model.addAttribute("empPayMiniBean", empPayMiniBean);
        } else {
            empPayMiniBean.setShowRow(IConstants.HIDE_ROW);


            HiringInfo wHI = genericService.loadObjectUsingRestriction(HiringInfo.class, Arrays.asList(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), empPayBean.getParentObject().getId()), getBusinessClientIdPredicate(request)));
            LocalDate retireDate = PayrollBeanUtils.calculateExpDateOfRetirement(wHI.getBirthDate(), wHI.getHireDate(),loadConfigurationBean(request),bc);
            wHI.setExpectedDateOfRetirement(retireDate);
            empPayBean.setHiringInfo(wHI);

            //Look for a Suspension as at the time of Generating this paycheck...
            /*if (empPayBean.getSuspendedInd() == 1) {
                //LocalDate wStartDate = PayrollBeanUtils.getNextORPreviousDay(PayrollBeanUtils.getDateFromMonthAndYear(empPayBean.getRunMonth(), empPayBean.getRunYear(), false), false);
                LocalDate wEndDate = null;
                if(wHI.isSuspendedEmployee()){
                    wEndDate = wHI.getSuspensionDate();
                }else{
                    wEndDate = PayrollBeanUtils.getNextORPreviousDay(PayrollBeanUtils.getDateFromMonthAndYear(empPayBean.getRunMonth(), empPayBean.getRunYear(), true), true);
                }


                List<SuspensionLog> wSL = this.genericService.loadAllObjectsUsingRestrictions(SuspensionLog.class,
                        Arrays.asList(getBusinessClientPredicate(request), CustomPredicate.procurePredicate("suspensionDate", wEndDate, Operation.LESS_OR_EQUAL),
                                CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), empPayBean.getParentObject().getId())), null);
                if (wSL.size() > 1) {
                    Comparator<SuspensionLog> wComp = Comparator.comparing(SuspensionLog::getSuspensionDate).reversed();
                    Collections.sort(wSL, wComp);
                }
                empPayBean.setSuspensionLog(wSL.get(0));
            }*/
            if (empPayBean.getContractIndicator() == ON && empPayBean.getNetPay() < 1) {
                //Look for a Contract that ended before the beginning of that Month & Year...
                LocalDate wStartDate = PayrollBeanUtils.getDateFromMonthAndYear(empPayBean.getRunMonth(), empPayBean.getRunYear(), false);

                List<ContractHistory> wSL = this.genericService.loadAllObjectsUsingRestrictions(ContractHistory.class, Arrays.asList(CustomPredicate.procurePredicate("contractEndDate", wStartDate, Operation.LESS),
                        CustomPredicate.procurePredicate("employee.id", empPayBean.getParentObject().getId())), null);
                if (wSL.size() > 1) {
                    Comparator<ContractHistory> wComp = Comparator.comparing(ContractHistory::getContractEndDate).reversed();
                    Collections.sort(wSL, wComp);
                    empPayBean.getHiringInfo().setContractEndDate((wSL.get(0).getContractEndDate()));
                }else{
                    //why would an employee not have contract history???
                }

            }
            if (bc.isPensioner()) {
                model = (Model) new PensionPaycheckGenerator().generatePaySlipModel(empPayMiniBean, empPayBean, genericService, bc, model,loadConfigurationBean(request), paySlipService);
            } else {
                model = (Model) new PaycheckGenerator().generatePaySlipModel(empPayMiniBean, empPayBean, genericService, bc, model,loadConfigurationBean(request), paySlipService);

            }
        }
        model.addAttribute("_stats", false);
        if(emailSent != null) {
            if (pObjInd > 0) {
                String actionCompleted = "Email Sending Failed.";
                model.addAttribute("saved", true);
                if (pObjInd == 1)
                    actionCompleted = "Email Sent Successfully.";
                model.addAttribute(SAVED_MSG, actionCompleted);

            }
        }
        model.addAttribute("roleBean", bc);
        return VIEW_NAME;
    }
}