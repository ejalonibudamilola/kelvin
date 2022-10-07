package com.osm.gnl.ippms.ogsg.controllers;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.audit.domain.LoginAudit;
import com.osm.gnl.ippms.ogsg.auth.domain.IppmsEncoder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.auth.repository.IAccountDao;
import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.base.services.*;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.approval.EmployeeApproval;
import com.osm.gnl.ippms.ogsg.domain.approval.TransferApproval;
import com.osm.gnl.ippms.ogsg.domain.beans.AdminBean;
import com.osm.gnl.ippms.ogsg.domain.beans.HomePageBean;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRerun;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.exception.NoBusinessCertificationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;


@Controller
@SessionAttributes({"roleBean"})
public class IppmsMultiFormController extends BaseController {

    private final PromotionService promotionService;
    private final HRService hrService;
    private final IMenuService menuService;
    private final IAccountDao accountDao;
    private final TransferService transferService;
    private final NegPayService negPayService;
    private final PaycheckService paycheckService;
    private final PayrollService payrollService;

    @Autowired
    public IppmsMultiFormController(PromotionService promotionService, HRService hrService, IMenuService menuService, IAccountDao accountDao, TransferService transferService, NegPayService negPayService, PaycheckService paycheckService, PayrollService payrollService) {
        this.promotionService = promotionService;
        this.hrService = hrService;
        this.menuService = menuService;
        this.accountDao = accountDao;
        this.transferService = transferService;
        this.negPayService = negPayService;
        this.paycheckService = paycheckService;
        this.payrollService = payrollService;
    }



    /**
     * Custom handler for the welcome view.
     * <p>
     * Note that this handler relies on the RequestToViewNameTranslator to
     * determine the logical view name based on the request URL: "/welcome.do"
     * -&gt; "welcome".
     */
    @RequestMapping("/welcome.do")
    public void welcomeHandler() {
    }

    @RequestMapping("/sessionXFormMAV.do")
    public ModelAndView setupForm(Model model) {
        return new ModelAndView("sessionExpiredFormMAV");

    }

    @RequestMapping("/signOut.do")
    public String signOutHandler(@RequestParam(value="r" ,required = false) String r,HttpServletRequest request, SessionStatus status) throws IllegalAccessException, InstantiationException {
        Object sessionId = getSessionAttribute(request, IppmsEncoder.getSessionKey());

        if (sessionId != null) {
            BusinessCertificate bc = super.getBusinessCertificate(request);
            //Remove it from the session.
            //this.payrollService.invalidateSessionInternal(sessionId,bc.getUserName());
            Navigator.invalidateInstance(sessionId);
            getSession(request).invalidate();
            User wLogin = this.genericService.loadObjectById(User.class, bc.getLoginId());
            if(r != null)
               auditAction(wLogin, "Session Timeout", request);
            else
                auditAction(wLogin, "Successful Logout",request);

        }
        //System.gc();
        SecurityContextHolder.clearContext();
        status.setComplete();
        //redirect to spring security log out form
        return SIGN_OUT_URL;
    }


    private void auditAction(User pLogin, String pActionDescription, HttpServletRequest request) {
        LoginAudit wLA = new LoginAudit();

        wLA.setLogin(pLogin);
        wLA.setFirstName(pLogin.getFirstName());
        wLA.setLastName(pLogin.getLastName());
        wLA.setUserName(pLogin.getUserName());

        wLA.setChangedBy(pLogin.getUserName());
        wLA.setDescription(pActionDescription);
        wLA.setLastModTs(LocalDate.now());
        wLA.setBusinessClientId(pLogin.getRole().getBusinessClient().getId());
        wLA.setAuditTimeStamp(PayrollBeanUtils.getCurrentTime(false));
        wLA.setRemoteIpAddress(request.getRemoteAddr());

        this.genericService.storeObject(wLA);
    }

    @RequestMapping("/relogin.do")
    public String reloginHandler(HttpServletRequest request, SessionStatus status, Model model) throws HttpSessionRequiredException, EpmAuthenticationException {
        User wLogin = SessionManagerService.manageSession(request, model);
        Object userId = getSessionId(request);

        Navigator.invalidateInstance(userId);

        auditAction(wLogin, "Successful Logout", request);
        //spring security specific logout
        super.logoutUser(request);

        return "redirect:/";
    }

    @RequestMapping("/epmErrorPage.do")
    public String errorPageFormHandler(HttpServletRequest request, Model model) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        return "errorPageForm";
    }

    @RequestMapping("/sessionExpiredForm.do")
    public String sessionExpiredFormHandler(HttpServletRequest request, Model model) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        return "sessionExpiredForm";
    }

    @RequestMapping("/sessionExpiredPopUpForm.do")
    public String sessionExpiredPopUpFormHandler(HttpServletRequest request, Model model) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        return "sessionExpiredPopUpForm";
    }

   /* @RequestMapping("/successForm.do")
    public String successFormHandler(HttpServletRequest request, Model model) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        return "successForm";
    }*/

    @RequestMapping("/notAllowedForm.do")
    public String notAllowedFormHandler(HttpServletRequest request, Model model) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        return "accessDeniedForm";
    }

    @RequestMapping("/comingSoonForm.do")
    public String comingSoonFormHandler(HttpServletRequest request, Model model) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        return "comingSoonForm";
    }


   /* @RequestMapping({"/determineDashBoard.do"})
    public String determineDashBoardFormHandler(Model model,
                                                HttpServletRequest request,
                                                @RequestParam(value = "s", required = false) boolean pSave)
            throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);

        final String roleDisplayName = accountDao.getUserRoleDisplayName(bc.getLoginId());

        addMainHeader(model, roleDisplayName);
        addPageTitle(model, roleDisplayName);

        return "menu/userDashboard";
    }*/

    @RequestMapping({"/enquiryUserHomePage.do"})
    public String enquiryHomeHandler(Model model, HttpServletRequest request) throws Exception {

        User wLogin = SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(wLogin, request);

        model.addAttribute("roleBean", bc);
        return "enquiryUserHomePageForm";
    }

    @RequestMapping({"/superUserPage.do"})
    public String superAdminHomePageHandler(Model model, HttpServletRequest request) throws Exception {
        User wLogin = SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(wLogin, request);

        model.addAttribute("roleBean", bc);
        return "superUserHomePageForm";
    }

    @RequestMapping({"/midUserHomePage.do"})
    public String midUserHomePageHandler(Model model, HttpServletRequest request) throws Exception {
        User wLogin = SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(wLogin, request);

        model.addAttribute("roleBean", bc);
        return "midUserHomePageForm";
    }

    @RequestMapping({"/prePayManagerHomeForm.do"})
    public String prePageHomeHandler(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, NoBusinessCertificationException {
        SessionManagerService.manageSession(request, model);
        addRoleBeanToModel(model, request);
        return REDIRECT_TO_DASHBOARD;

    }


    @RequestMapping("/reportsOverview.do")
    public String reportsOverviewHandler(Model model, HttpServletRequest request) throws NoBusinessCertificationException, HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);

        addRoleBeanToModel(model, request);
        return "menu/multiLinkDashboard";
    }

    //	@RequestMapping("/actSuccessForm.do")
//	public String activitySuccessFormController(Model model,HttpServletRequest request) throws Exception{
//		User wLogin = SessionManagerService.manageSession(request, model);
//		BusinessCertificate bc = super.getBusinessCertificate(wLogin, request);
//
//		HomePageBean wHPB = new HomePageBean();
//        wHPB.setAdmin(bc.isSuperAdmin());
//        model.addAttribute("roleBean", bc);
//		model.addAttribute("homePageBean", wHPB);
//		return "activitySuccessPage";
//	}
    @RequestMapping("/hrReportsOverview.do")
    public String hrReportsOverviewHandler(HttpServletRequest request, Model model) throws Exception {
        SessionManagerService.manageSession(request, model);
        addRoleBeanToModel(model, request);
        return "hrReportsOverviewForm";
    }

    @RequestMapping("/adminHomeForm.do")
    public String adminHomeHandler(Model model, HttpServletRequest request) throws Exception {
        User wLogin = SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(wLogin, request);

        //Are there approved Paychecks?
        AdminBean adminBean = new AdminBean();
        if (bc.isSuperAdmin())
            adminBean.setSuperAdmin(true);

        adminBean.setApprovedPaychecks(IppmsUtils.isPendingPaychecksExisting(genericService, bc));

        adminBean.setEmpToBePromoted(promotionService.getToBePromotedEmpCount(bc.getBusinessClientInstId()) > 0);

        adminBean.setEmpToBeRetired(hrService.getToBeTerminatedEmpCount(bc) > 0);


        adminBean.setRunManualPromotion(true);
        model.addAttribute("roleBean", bc);
        model.addAttribute("adminBean", adminBean);
        return "adminHomePageForm";
    }

    @RequestMapping("/hrAdminHomeForm.do")
    public String hrModuleOverviewHandler(Model model, HttpServletRequest request) throws Exception {
        User wLogin = SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(wLogin, request);

        HomePageBean wHPB = new HomePageBean();
        wHPB.setAdmin(bc.isSuperAdmin());
        
        /*if ((!bc.isAdmin()) && (!bc.isSuperAdmin())) {
  	      return DETERMINE_DASHBOARD_URL;
  	    }*/
        model.addAttribute("roleBean", bc);
        model.addAttribute("homePageBean", wHPB);
        return "hrModuleOverviewForm";
    }

    @RequestMapping("/auditPageHomeForm.do")
    public String auditPageHomeFormController(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, NoBusinessCertificationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);
		
		/*if (!bc.isSuperAdmin()) {
		      return REDIRECT_TO_DASHBOARD;
		    }*/
        //model.addAttribute("roleBean", bc);
        //return "auditPageForm";
        model.addAttribute("roleBean", bc);
        return "menu/multiLinkDashboard";
    }

    @RequestMapping("/resolvePendingPaychecksForm.do")
    public String unapprovedPaychecksHandler(Model model, HttpServletRequest request) throws Exception {
        User wLogin = SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(wLogin, request);

        NamedEntity ne = new NamedEntity();
        ne.setName(bc.getBusinessName());
        model.addAttribute("namedEntity", ne);
        model.addAttribute("roleBean", bc);
        return "payment/resolvePendingPaychecksForm";
    }

    @RequestMapping("/existingLtgDetailsNotif.do")
    public String existingLtgDetailsNotifHandler(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, NoBusinessCertificationException {
        User wLogin = SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(wLogin, request);

        NamedEntity ne = new NamedEntity();
        ne.setName(bc.getBusinessName());
        model.addAttribute("namedEntity", ne);
        model.addAttribute("roleBean", bc);
        return "existingLtgDetailsNotifForm";
    }

    @RequestMapping("/resolvePendingStepIncrement.do")
    public String resolvePendingStepIncreaseFormHandler(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, NoBusinessCertificationException {
        User wLogin = SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(wLogin, request);

        NamedEntity ne = new NamedEntity();
        ne.setName(bc.getBusinessName());
        model.addAttribute("namedEntity", ne);
        model.addAttribute("roleBean", bc);
        return "resolvePendingStepIncrementForm";
    }

    @RequestMapping("/busPaySchedException.do")
    public String busPaySchedExceptionFormHandler() {
        return "busPaySchedException";
    }

    @RequestMapping("/busPayPolicyException.do")
    public String busPayPolicyExceptionFormHandler(HttpServletRequest request, Model model) throws HttpSessionRequiredException, EpmAuthenticationException, NoBusinessCertificationException {
        addRoleBeanToModel(model, request);
        return "busPayPolicyException";
    }

    @RequestMapping("/existingBasicSalaryAlterBean.do")
    public String existingBasicSalaryInstructionHandler(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, NoBusinessCertificationException {
        addRoleBeanToModel(model, request);
        return "resolveExistingBasicSalaryForm";
    }

    @RequestMapping("/configurationHome.do")
    public String configAndControlHomeFormController(Model model,
                                                     HttpServletRequest request) throws HttpSessionRequiredException,
            EpmAuthenticationException, NoBusinessCertificationException {


        SessionManagerService.manageSession(request, model);
        addRoleBeanToModel(model,request);
        return "menu/configAndControlHomePage";


    }

    @RequestMapping("/existingPayeeAlterBean.do")
    public String existingPayeInstructionHandler(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, NoBusinessCertificationException {
        User wLogin = SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = super.getBusinessCertificate(wLogin, request);

        NamedEntity ne = new NamedEntity();
        ne.setName(bc.getBusinessName());
        addRoleBeanToModel(model, request);
        model.addAttribute("namedEntity", ne);
        return "resolveExistingPayeInstructionForm";
    }

    @RequestMapping("/existingPensionAlterBean.do")
    public String existingPensionInstructionHandler(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, NoBusinessCertificationException {
        User wLogin = SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = super.getBusinessCertificate(wLogin, request);

        NamedEntity ne = new NamedEntity();
        ne.setName(bc.getBusinessName());
        addRoleBeanToModel(model, request);
        model.addAttribute("namedEntity", ne);
        return "resolveExistingPensionForm";
    }


    @RequestMapping({"/negativePayWarningForm.do"})
    public String negativePayWarningFormHandler(@RequestParam("noe") int pNoOfEmp, @RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, NoBusinessCertificationException {

        User wLogin = SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = super.getBusinessCertificate(wLogin, request);

        NamedEntity ne = new NamedEntity();
        ne.setName(bc.getBusinessName());

        ne.setReportType(pNoOfEmp);
        ne.setRunMonth(pRunMonth);
        ne.setRunYear(pRunYear);
        model.addAttribute("namedEntity", ne);
        model.addAttribute("roleBean", bc);
        return "resolveNegativeNetPayForm";

    }

    @RequestMapping({"/massEntryDashboard.do"})
    public String massEntryDashboardHandler(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        bc.setDeductionObjectType(3);
        bc.setLoanObjectType(2);
        bc.setSpecAllowObjectType(5);
        bc.setBankBranchObjectType(1);

        model.addAttribute("roleBean", bc);
        model.addAttribute("adminBean", new AdminBean());
        return "massentry/massEntryAndFileUploadDashboard";
    }

    @RequestMapping({"/fileUploadFailed.do"})
    public String fileUploadFailedHandler(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        model.addAttribute("roleBean", bc);
        return "fileupload/failedFileUploadForm";
    }

    @RequestMapping({"/pendingTransferApprovals.do"})
    public String resolvePendingTransferFormHandler(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);
        Navigator.getInstance(bc.getSessionId()).setFromForm("redirect:paydayForm.do");
        model.addAttribute("roleBean", bc);
        return "transfer/pendingTransferApprovalForm";
    }

    @RequestMapping({"/pendingAllowanceRuleApprovals.do"})
    public String resolvePendingAllowanceRuleFormHandler(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);
        Navigator.getInstance(bc.getSessionId()).setFromForm("redirect:paydayForm.do");
        model.addAttribute("roleBean", bc);
        return "rules/pendingAllowanceApprovalForm";
    }
    @RequestMapping({"/pendingPayrollApprovals.do"})
    public String resolvePendingPayrollApprovalFormHandler(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);
        //--check if pending Employee Approvals actually exists...
        Navigator.getInstance(bc.getSessionId()).setFromForm("redirect:paydayForm.do");
        model.addAttribute("roleBean", bc);
        return "payroll/pendingPayrollApprovalForm";
    }

    @RequestMapping({"/userFunctionalities.do"})
    public String userFunctionalitesController(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        this.getBusinessCertificate(request);
	    /*if (!bc.isSuperAdmin()) {
	      return "redirect:determineDashBoard.do";
	    }*/
        addRoleBeanToModel(model, request);
        return "menu/userFunctionalities";
    }

    @RequestMapping({"/simulatorFunctionalities.do"})
    public String simulatorController(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        this.getBusinessCertificate(request);
        addRoleBeanToModel(model, request);
        return "menu/simulatorFunctionalities";
    }

    @RequestMapping({"/runPayrollFunctionalities.do"})
    public String runPayrollController(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);


        bc.setNoOfDeletedPaychecks(0);
        bc.setNoOfPendingEmployeeApprovals(0);
        bc.setNoOfPendingTransfers(0);
        bc.setHasPendingEmployeeApprovals(false);
        bc.setHasPendingTransfers(false);
        bc.setRerunPayrollExists(false);
        model.addAttribute("roleBean", bc);
        AdminBean adminBean = new AdminBean();
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(getBusinessClientIdPredicate(request)).addPredicate(CustomPredicate.procurePredicate("approvalStatusInd", IConstants.OFF));
        if (adminBean.isLeaveBonusModuleOpen()) {
            adminBean.setNoOfPendingLeaveBonuses(this.hrService.getNoOfLeaveBonusForApproval(bc));
            adminBean.setHasPendingLeaveBonus(adminBean.getNoOfPendingLeaveBonuses() > 0);
        }
        adminBean.setLeaveBonusModuleOpen(false);
        adminBean.setNoOfPendingTransfers(this.transferService.getTotalNoOfActiveTransferApprovals(bc.getBusinessClientInstId()));
        adminBean.setHasPendingTransfers(adminBean.getNoOfPendingTransfers() > 0);

        adminBean.setNoOfPendingAllowanceRules(this.transferService.getTotalNoOfActiveAllowanceApprovals(bc.getBusinessClientInstId()));
        adminBean.setHasPendingAllowanceApprovals(adminBean.getNoOfPendingAllowanceRules() > 0);

        adminBean.setNoOfPendingEmployeeApprovals(genericService.countObjectsUsingPredicateBuilder(predicateBuilder, EmployeeApproval.class));

        adminBean.setHasPendingEmployeeApprovals(adminBean.getNoOfPendingEmployeeApprovals() > 0);
        if(!bc.isPensioner())
           adminBean.setNoOfPendingNameConflicts(this.hrService.getNoOfPendingNameConflicts(bc.getBusinessClientInstId()));
        adminBean.setHasPendingNameConflicts(adminBean.getNoOfPendingNameConflicts() > 0);

        adminBean.setApprovedPaychecks(IppmsUtils.isNotNull(paycheckService.getPendingPaycheckRunMonthAndYear(bc)));

        if (adminBean.isApprovedPaychecks()) {
            adminBean.setCanRerunPayroll(true);
        }
        if(!bc.isPensioner()){
            adminBean.setEmpToBePromoted(promotionService.getToBePromotedEmpCount(bc.getBusinessClientInstId()) > 0);
        }

        adminBean.setEmpToBeRetired(hrService.getToBeTerminatedEmpCount(bc) > 0);

        adminBean.setRunManualPromotion(true);

        //Make sure the rerun payroll indicators are false in case of deletion of rerun payroll records
        bc.setRerunPayrollExists(false);

        if (this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder()
                .addPredicate(getBusinessClientIdPredicate(request)), PayrollRerun.class) > 0) {
            bc.setNoOfDeletedPaychecks(this.genericService.getTotalNoOfModelObjectByClass(PayrollRerun.class, "id", true));

            bc.setRerunPayrollExists(true);

        }

        if (bc.isSuperAdmin()) {
            adminBean.setSuperAdmin(true);
            //Check for Manual Step Increment ability...
            adminBean.setRunManualPromotion(true);

            adminBean.setSuperAdmin(true);

            bc.setNoOfPendingEmployeeApprovals(adminBean.getNoOfPendingEmployeeApprovals());
            if (adminBean.isApprovedPaychecks()) {
                LocalDate wRunDetails = paycheckService.getPendingPaycheckRunMonthAndYear(bc);
                if (IppmsUtils.isNotNull(wRunDetails)) {
                    int wNoOfElements = this.negPayService.getNoOfNegativePaychecksByRunMonthAndYear(bc, wRunDetails.getMonthValue(), wRunDetails.getYear(), null, null, false);
                    adminBean.setNoOfNegativePayRecords(wNoOfElements);
                }
            }

            int wNoOfActTransfers = genericService.countObjectsUsingPredicateBuilder(predicateBuilder, TransferApproval.class);

            bc.setHasPendingTransfers(wNoOfActTransfers > 0);
            bc.setNoOfPendingTransfers(wNoOfActTransfers);
            adminBean.setHasPendingTransfers(wNoOfActTransfers > 0);
            adminBean.setNoOfPendingTransfers(wNoOfActTransfers);


            model.addAttribute("adminBean", adminBean);


        }

        addRoleBeanToModel(model, request);
        if(!adminBean.isApprovedPaychecks() && !bc.isHasPendingEmployeeApprovals() && !bc.isHasPendingTransfers() && !bc.isRerunPayrollExists() && !adminBean.isHasPendingTransfers() && !adminBean.isHasPendingAllowanceApprovals())
            return "redirect:paydayForm.do";

        return "menu/runPayrollFunctionalities";
    }


    @RequestMapping({"/subventionFuntionalities.do"})
    public String subventionController(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        this.getBusinessCertificate(request);
        addRoleBeanToModel(model, request);
        return "menu/subventionFuntionalities";
    }


    @RequestMapping({"/employeeFuntionalities.do"})
    public String employeeFunctionsController(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        AdminBean adminBean = new AdminBean();

        if (this.promotionService.getToBePromotedEmpCount(bc.getBusinessClientInstId()) > 0)
            adminBean.setEmpToBePromoted(true);
        else if (this.hrService.getToBeTerminatedEmpCount(bc) > 0) {
            adminBean.setEmpToBeRetired(true);
        }


        if (bc.isSuperAdmin()) {
            adminBean.setSuperAdmin(true);

            //Check for Manual Step Increment ability...
            adminBean.setRunManualPromotion(true);
            adminBean.setSuperAdmin(true);


            model.addAttribute("adminBean", adminBean);


        }

        addRoleBeanToModel(model, request);

        return "menu/employeeFuntionalities";
    }


    @RequestMapping({"/hrEmployeeFunctionalities.do"})
    public String hrEmployeeFunctionalitiesController(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        HomePageBean wHPB = new HomePageBean();
        wHPB.setAdmin((bc.isSuperAdmin()));
        addRoleBeanToModel(model, request);
        model.addAttribute("homePageBean", wHPB);

        Navigator.getInstance(getSessionId(request)).setFromForm("redirect:hrEmployeeFunctionalities.do");

         return "menu/hrEmployeeFunctionalities";
    }


    @RequestMapping({"/lgeaSchoolsFunctionalities.do"})
    public String lgeaSchoolsFunctionlitesController(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        HomePageBean wHPB = new HomePageBean();
        wHPB.setAdmin((bc.isSuperAdmin()));

        model.addAttribute("homePageBean", wHPB);

        Navigator.getInstance(getSessionId(request)).setFromForm("redirect:lgeaSchoolsFunctionalities.do");
        if(bc.isSubeb() || bc.isCivilService())
            addPageTitle(model,bc.getMdaTitle()+"/School");
        else
            addPageTitle(model,bc.getMdaTitle());

        addRoleBeanToModel(model, request);
        return "menu/lgeaSchoolsFunctionalities";
    }


    @RequestMapping({"/departmentFunctionalities.do"})
    public String departmentFunctionalitiesController(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        HomePageBean wHPB = new HomePageBean();
        wHPB.setAdmin((bc.isSuperAdmin()));

        model.addAttribute("homePageBean", wHPB);

        Navigator.getInstance(getSessionId(request)).setFromForm("redirect:departmentFunctionalities.do");
        addRoleBeanToModel(model, request);
        return "menu/departmentFunctionalities";
    }


    @RequestMapping({"/hrEmployeeRelatedReports.do"})
    public String hrEmployeeRelatedReportsController(HttpServletRequest request, Model model) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        this.getBusinessCertificate(request);
        addRoleBeanToModel(model, request);
        return "menu/hrEmployeeRelatedReportsOverview";
    }

    @RequestMapping({"/hrRelatedReports.do"})
    public String hrRelatedReportsController(HttpServletRequest request, Model model) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        this.getBusinessCertificate(request);
        addRoleBeanToModel(model, request);
        return "menu/hrRelatedReportsOverview";
    }


    @RequestMapping({"/fileUploadDashboard.do"})
    public String fileUploadDashboardHandler(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {

        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        bc.setDeductionObjectType(3);
        bc.setLoanObjectType(2);
        bc.setSpecAllowObjectType(5);
        bc.setBankBranchObjectType(1);


        model.addAttribute("roleBean", bc);
        model.addAttribute("adminBean", new AdminBean());
        return "menu/fileUploadDashboard";

    }


    @RequestMapping({"/massEntryMainDashboard.do"})
    public String massEntryMainDashboardHandler(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {

        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        bc.setDeductionObjectType(3);
        bc.setLoanObjectType(2);
        bc.setSpecAllowObjectType(5);
        AdminBean adminBean = new AdminBean();
        if (menuService.canUserAccessURL(bc, "/massDeductionEntry.do", "/massDeductionEntry.do"))
            adminBean.setCanDoDeductions(true);
        if (menuService.canUserAccessURL(bc, "/massEmailPayslips.do", "/massEmailPayslips.do"))
            adminBean.setCanDoEmail(true);
        if (menuService.canUserAccessURL(bc, "/massLoanEntry.do", "/massLoanEntry.do"))
            adminBean.setCanDoLoans(true);
        if (menuService.canUserAccessURL(bc, "/massPromotions.do", "/massPromotions.do"))
            adminBean.setCanDoPromotions(true);
        if (menuService.canUserAccessURL(bc, "/massSpecialAllowance.do", "/massSpecialAllowance.do"))
            adminBean.setCanDoSpecAllow(true);
        if (menuService.canUserAccessURL(bc, "/massTransfer.do", "/massTransfer.do"))
            adminBean.setCanDoTransfers(true);

        model.addAttribute("roleBean", bc);
        model.addAttribute("adminBean", adminBean);
        return "menu/massEntryMainDashboard";

    }

    @RequestMapping({"/permissionDenied.do"})
    public String permissionDeniedHandler(Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        addRoleBeanToModel(model, request);
        return "permissionDeniedForm";
    }

}