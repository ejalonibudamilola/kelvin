package com.osm.gnl.ippms.ogsg.controllers.deduction;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.IppmsEncoder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.negativepay.NegativePayViewFormController;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductContGarnishBean;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.negativepay.domain.NegativePayBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Controller
@RequestMapping({"/dedGarnForm.do"})
@SessionAttributes(types = {DeductContGarnishBean.class})
public class AddDedGarnForm extends BaseController {

     private final IMenuService menuService;
     @Autowired
     public AddDedGarnForm(IMenuService menuService) {
         this.menuService = menuService;
     }
     
     
     private final String VIEW = "deduction/dedGarnForm";
     private final String NEGATIVE_VIEW = "deduction/dedGarnForNegPayForm";

    @ModelAttribute("payTypeList")
    public List<PayTypes> getPayTypesList() {
        return genericService.loadAllObjectsWithSingleCondition(PayTypes.class, CustomPredicate.procurePredicate("selectableInd", OFF),"name");
    }
    private void init(BusinessCertificate bc,HttpServletRequest request) {

          if(menuService.canUserAccessURL(bc,"/searchEmpForEdit.do","/searchEmpForEdit.do")){
                  bc.setCanEditDeductions(true);
                  if(!bc.isPensioner())
                  bc.setCanEditLoans(true);
                  bc.setCanEditSpecAllow(true);
                  request.getSession().setAttribute(IppmsEncoder.getCertificateKey(),bc);
            }
    }
    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"eid"})
    public String setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        init(bc,request);
        NamedEntity ne = new NamedEntity();

        AbstractEmployeeEntity emp = IppmsUtils.loadEmployee(genericService,pEmpId,bc);
        ne.setName(emp.getDisplayName());
        ne.setId(emp.getId());

        //ne.setMode("e");
        ne.setStaffId(emp.getEmployeeId());

        addSessionAttribute(request, IConstants.NAMED_ENTITY, ne);

        DeductContGarnishBean dCGB = new DeductContGarnishBean();

        AbstractPaycheckEntity wEPB = (AbstractPaycheckEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc),
                Arrays.asList(CustomPredicate.procurePredicate("employee.id", emp.getId()),
                        CustomPredicate.procurePredicate("status", "P")));
        if (!wEPB.isNewEntity()) {

            if (wEPB.getNetPay() < 0) {
                dCGB = determineNegativePayStatus(dCGB, wEPB, this.getBusinessCertificate(request));
                dCGB.setShowNegativeNetPay(true);
            }
        }

        dCGB.setId(ne.getId());
        dCGB = buildBean(dCGB, dCGB.isShowNegativeNetPay(), 0L, 0L, bc,request);

        model.addAttribute("roleBean", bc);
        model.addAttribute("namedEntity", ne);
        model.addAttribute("dedConGarnBean", dCGB);

        if (dCGB.isShowNegativeNetPay()) {
            return NEGATIVE_VIEW;
        }
        return VIEW;
    }



    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"eid", "pid", "oid", "tid"})
    public String setupForm(@RequestParam("eid") Long pEmpId, @RequestParam("pid") Long pBid,
                            @RequestParam("oid") Long pObjectId, @RequestParam("tid") Long pTypeId,
                            Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        //Object userId = super.getSessionId(request);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        init(bc,request);
        DeductContGarnishBean dCGB = new DeductContGarnishBean();
        NamedEntity ne = (NamedEntity) getSessionAttribute(request, IConstants.NAMED_ENTITY);//this.payrollService.getNamedEntity(userId);

        AbstractEmployeeEntity emp = IppmsUtils.loadEmployee(genericService,pEmpId,bc);
        ne.setName(emp.getDisplayName());
        ne.setId(emp.getId());
        ne.setStaffId(emp.getEmployeeId());
        dCGB.setId(ne.getId());
        AbstractPaycheckEntity wEPB = (AbstractPaycheckEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc),
                Arrays.asList(CustomPredicate.procurePredicate("employee.id", emp.getId()),
                        CustomPredicate.procurePredicate("status", "P")));
        if (!wEPB.isNewEntity()) {

            if (wEPB.getNetPay() < 0) {
                dCGB = determineNegativePayStatus(dCGB, wEPB, this.getBusinessCertificate(request));
                dCGB.setShowNegativeNetPay(true);
            }
        }

        dCGB = buildBean(dCGB, dCGB.isShowNegativeNetPay(), pObjectId, pTypeId, bc,request);
        model.addAttribute("namedEntity", ne);

        model.addAttribute("dedConGarnBean", dCGB);
        model.addAttribute("roleBean", bc);
        if (dCGB.isShowNegativeNetPay()) {
            return NEGATIVE_VIEW;
        }
        return VIEW;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);


        BusinessCertificate bc = this.getBusinessCertificate(request);
        init(bc,request);
        NamedEntity ne = (NamedEntity) getSessionAttribute(request, IConstants.NAMED_ENTITY);

        DeductContGarnishBean dCGB = new DeductContGarnishBean();

        dCGB.setId(ne.getId());
        AbstractEmployeeEntity emp = IppmsUtils.loadEmployee(genericService,ne.getId(),bc);
        ne.setName(emp.getDisplayName());
        ne.setId(emp.getId());
        ne.setStaffId(emp.getEmployeeId());
        AbstractPaycheckEntity wEPB = (AbstractPaycheckEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc),
                Arrays.asList(CustomPredicate.procurePredicate("employee.id", emp.getId()),
                        CustomPredicate.procurePredicate("status", "P")));
        if (!wEPB.isNewEntity()) {
            if (wEPB.getNetPay() < 0) {
                dCGB = determineNegativePayStatus(dCGB, wEPB, this.getBusinessCertificate(request));
                dCGB.setShowNegativeNetPay(true);
            }
        }
        dCGB = buildBean(dCGB,dCGB.isShowNegativeNetPay(), 0L, 0L, bc,request);
        model.addAttribute("namedEntity", ne);

        model.addAttribute("dedConGarnBean", dCGB);
        model.addAttribute("roleBean", bc);
        if (dCGB.isShowNegativeNetPay()) {
            return NEGATIVE_VIEW;
        }
        return VIEW;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @RequestParam(value = "_schedule", required = false) String pSchedule,
                                @ModelAttribute("dedConGarnBean") DeductContGarnishBean dedGarnBean,
                                BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        Object userId = getSessionId(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL) && (dedGarnBean.isEditMode())) {
            if (Navigator.getInstance(userId).getFromClass().isAssignableFrom(NegativePayViewFormController.class)) {
                return Navigator.getInstance(userId).getFromForm() + "&eid=" + dedGarnBean.getId();
            }
            return "redirect:employeeOverviewForm.do";
        }


        if (isButtonTypeClick(request, REQUEST_PARAM_SCHED)) {
            AbstractPaycheckEntity epb = (AbstractPaycheckEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(this.getBusinessCertificate(request)),
                    Arrays.asList(CustomPredicate.procurePredicate("employee.id", dedGarnBean.getId()),
                            CustomPredicate.procurePredicate("status", "P")));
            if (!epb.isNewEntity()) {

                return "redirect:deletePaycheck.do?pid=" + epb.getId();
            }
            if (Navigator.getInstance(userId).getFromClass().isAssignableFrom(NegativePayViewFormController.class)) {
                return Navigator.getInstance(userId).getFromForm() + "&eid=" + dedGarnBean.getId();
            }
            return "redirect:employeeOverviewForm.do";
        }


        if (Navigator.getInstance(userId).getFromClass().isAssignableFrom(NegativePayViewFormController.class)) {
            return Navigator.getInstance(userId).getFromForm() + "&eid=" + dedGarnBean.getId();
        }
        return "redirect:employeeOverviewForm.do";
    }


    private DeductContGarnishBean buildBean(DeductContGarnishBean pDCGB,boolean forNegativePay,Long pObjectId, Long pTypeId, BusinessCertificate businessCertificate,
                                            HttpServletRequest request) throws Exception {


        List<AbstractDeductionEntity> empDedInfoList = (List<AbstractDeductionEntity>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getDeductionInfoClass(businessCertificate), Arrays.asList(getBusinessClientIdPredicate(request) ,CustomPredicate.procurePredicate(businessCertificate.getEmployeeIdJoinStr(), pDCGB.getId())), "description");

        if (!empDedInfoList.isEmpty()) {
            pDCGB = breakUpDeductionList(empDedInfoList, pDCGB, pObjectId, pTypeId);

        } else {
            pDCGB.setExpDedInfoList(new ArrayList<>());
            pDCGB.addEmpDeductionInfo(new ArrayList<>());
            pDCGB.setMode("c");
        }


        List<AbstractGarnishmentEntity> empGarnInfoList = (List<AbstractGarnishmentEntity>) this.genericService.loadAllObjectsWithSingleCondition(IppmsUtils.getGarnishmentInfoClass(businessCertificate), CustomPredicate.procurePredicate(businessCertificate.getEmployeeIdJoinStr(), pDCGB.getId()), "description");

        if (!empGarnInfoList.isEmpty()) {
            pDCGB = breakUpLoanList(empGarnInfoList, pDCGB, pObjectId, pTypeId);
        } else {
            pDCGB.setExpLoanList(new ArrayList<>());
            pDCGB.addEmpGarnishInfo(new ArrayList<>());
        }
        if(!forNegativePay) {
            List<AbstractSpecialAllowanceEntity> wSpecialAllowance = (List<AbstractSpecialAllowanceEntity>)this.genericService.loadAllObjectsWithSingleCondition(IppmsUtils.getSpecialAllowanceInfoClass(businessCertificate), CustomPredicate.procurePredicate(businessCertificate.getEmployeeIdJoinStr(), pDCGB.getId()), "description");

            if (wSpecialAllowance == null) {
                wSpecialAllowance = new ArrayList<>();
            }
            //-- Now check if there are expiredOnes....
            if (!wSpecialAllowance.isEmpty())
                pDCGB = breakUpSpecAllowList(pDCGB, wSpecialAllowance, pObjectId, pTypeId);
            else {
                pDCGB.addSpecialAllowanceInfo(wSpecialAllowance);
                pDCGB.setExpSpecAllowInfo(new ArrayList<>());
            }
        }
        HiringInfo wHInfo = this.genericService.loadObjectWithSingleCondition(HiringInfo.class, CustomPredicate.procurePredicate(businessCertificate.getEmployeeIdJoinStr(), pDCGB.getId()));

        //dCGB.addEmpGarnishInfo(empGarnInfoList);
        pDCGB.setHiringInfo(wHInfo);

        pDCGB.setShowContributionRow(false);
        if(!businessCertificate.isPensioner()) {
            if (wHInfo.isPensionableEmployee() &&
                    !wHInfo.isContractStaff()) {
                ConfigurationBean configurationBean = loadConfigurationBean(request);
                if (!PayrollBeanUtils.isTPSEmployee(wHInfo.getBirthDate(),
                        wHInfo.getHireDate(), wHInfo.getExpectedDateOfRetirement(),
                        PayrollBeanUtils.getLocalDateFromString(IConstants.TPS_HIRE_DATE_STR),
                        PayrollBeanUtils.getLocalDateFromString(IConstants.TPS_EXP_RETIRE_DATE_STR
                        ),configurationBean, businessCertificate)) {

                    {
                        double amount;
                        if
                        (wHInfo.getEmployee().getSalaryInfo().getSalaryType().isBasicRentTransportType()) {
                            pDCGB.setContributionPayType("% of B,R&T");
                            pDCGB.setContributionAmountStr("7.5% of (Basic + Rent + Transport)");
                              amount = (wHInfo.getEmployee().getSalaryInfo().getMonthlyBasicSalary() + wHInfo.getEmployee().getSalaryInfo().getRent() +
                                    wHInfo.getEmployee().getSalaryInfo().getTransport()) / 12.0D * 0.075D;

                        } else {
                            pDCGB.setContributionPayType("% of B,R&MV");
                            pDCGB.setContributionAmountStr("7.5% of (Basic + Rent + Motor Vehicle)");
                              amount = (wHInfo.getEmployee().getSalaryInfo().getMonthlyBasicSalary() + wHInfo.getEmployee().getSalaryInfo().getRent() +
                                    wHInfo.getEmployee().getSalaryInfo().getMotorVehicle()) / 12.0D * 0.075D;
                        }
                        pDCGB.setPensionName("Contributory Pension");
                        pDCGB.setContributionAmount(PayrollHRUtils.getDecimalFormat().format(amount)) ;
                        pDCGB.setShowContributionRow(true);
                    }
                }
            }
        }


        return pDCGB;
    }

    private DeductContGarnishBean breakUpLoanList(
            List<AbstractGarnishmentEntity> pEmpGarnInfoList, DeductContGarnishBean pDCGB, Long pObjectId, Long pTypeId) {

        List<AbstractGarnishmentEntity> wActiveList = new ArrayList<>();
        List<AbstractGarnishmentEntity> wInactiveList = new ArrayList<>();

        for (AbstractGarnishmentEntity s : pEmpGarnInfoList) {
            if (pTypeId == LOAN) {
                if (pObjectId > 0 && pObjectId.equals(s.getId())) {
                    s.setLastEdited(ON);
                    if (s.getAmount() == 0.0D && pDCGB.isShowNegativePayMsg()) {
                        if (!pDCGB.isMustDeletePaycheck())
                            pDCGB.setMustDeletePaycheck(true);
                    }
                }
            }
            if (s.getAmount() == 0.00D ||
                    s.getOwedAmount() == 0.00D ) {
                if(s.getEndDate() != null && s.getEndDate().isBefore(LocalDate.now())) {
                    s.setDescription(s.getDescription());
                    s.setName(s.getName());
                    wInactiveList.add(s);
                }else{
                    wActiveList.add(s);
                }
            } else {
                wActiveList.add(s);
            }
        }

        if (!wInactiveList.isEmpty()) {
            Collections.sort(wInactiveList);
            pDCGB.setHasExpLoan(true);
        }
        if (!wActiveList.isEmpty())
            Collections.sort(wActiveList);
        pDCGB.setExpLoanList(wInactiveList);
        pDCGB.addEmpGarnishInfo(wActiveList);
        return pDCGB;

    }

    private DeductContGarnishBean breakUpDeductionList(
            List<AbstractDeductionEntity> pEmpDedInfoList, DeductContGarnishBean pDCGB, Long pObjectId, Long pTypeId) {

        List<AbstractDeductionEntity> wActiveList = new ArrayList<>();
        List<AbstractDeductionEntity> wInactiveList = new ArrayList<>();

        for (AbstractDeductionEntity s : pEmpDedInfoList) {

            if (pTypeId == DEDUCTION) {
                if (pObjectId > 0 && pObjectId.equals(s.getId())) {
                    s.setLastEdited(ON);
                    //-- Now check if value is zero and there is negative pay.
                    if (s.getAmount() == 0.00D && pDCGB.isShowNegativePayMsg()) {
                        if (!pDCGB.isMustDeletePaycheck())
                            pDCGB.setMustDeletePaycheck(true);
                    }
                }
            }
            if (s.getAmount() == 0) {
                s.setDescription(s.getDescription());
                s.setName(s.getName());
                wInactiveList.add(s);
            } else {
                wActiveList.add(s);
            }
        }
        if (!wInactiveList.isEmpty()) {
            Collections.sort(wInactiveList);
            pDCGB.setHasExpDeductions(true);
        }
        if (!wActiveList.isEmpty())
            Collections.sort(wActiveList);
        pDCGB.setExpDedInfoList(wInactiveList);
        pDCGB.addEmpDeductionInfo(wActiveList);
        return pDCGB;

    }

    private DeductContGarnishBean breakUpSpecAllowList(DeductContGarnishBean pDCGB,
                                                       List<AbstractSpecialAllowanceEntity> pSpecialAllowance, Long pObjectId, Long pTypeId) {
        List<AbstractSpecialAllowanceEntity> wActiveList = new ArrayList<>();
        List<AbstractSpecialAllowanceEntity> wInactiveList = new ArrayList<>();

        for (AbstractSpecialAllowanceEntity s : pSpecialAllowance) {
            if (pTypeId == SPEC_ALLOW_IND) {
                if (pObjectId > 0 && pObjectId.equals(s.getId())) {
                    s.setLastEdited(ON);
                }
            }
            if (s.isExpired()) {
                s.setDescription(s.getDescription());
                wInactiveList.add(s);
            } else {
                wActiveList.add(s);
            }
        }
        if (!wInactiveList.isEmpty()) {
            Collections.sort(wInactiveList);
            pDCGB.setHasExpSpecAllow(true);
        }
        if (!wActiveList.isEmpty())
            Collections.sort(wActiveList);
        pDCGB.setExpSpecAllowInfo(wInactiveList);
        pDCGB.addSpecialAllowanceInfo(wActiveList);
        return pDCGB;
    }

    private DeductContGarnishBean determineNegativePayStatus(DeductContGarnishBean pDeductContGarnishBean, AbstractPaycheckEntity pEPB, BusinessCertificate pBc) throws IllegalAccessException, InstantiationException {

        NegativePayBean wNPB = this.genericService.loadObjectUsingRestriction(NegativePayBean.class, Arrays.asList(
                CustomPredicate.procurePredicate("paycheckId", pEPB.getId()),
                CustomPredicate.procurePredicate("runMonth", pEPB.getRunMonth()),
                CustomPredicate.procurePredicate("businessClientId", pBc.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("runYear", pEPB.getRunYear())));
        if (wNPB.isNewEntity()) {
            wNPB.setPaycheckId(pEPB.getId());
            wNPB.setTotalDeductions(pEPB.getTotalDeductions());
            wNPB.setNetPay(pEPB.getNetPay());
            wNPB.setTotalPay(pEPB.getTotalPay());
            wNPB.setRunMonth(pEPB.getRunMonth());
            wNPB.setRunYear(pEPB.getRunYear());
            wNPB.setLastModBy(pBc.getUserName());
            wNPB.setLastModTs(LocalDate.now());
            wNPB.setBusinessClientId(pBc.getBusinessClientInstId());
            this.genericService.saveObject(wNPB);
        }
        pDeductContGarnishBean.setNegPayId(wNPB.getId());
        double wDiff = pEPB.getNetPay() + wNPB.getReductionAmount();
        pDeductContGarnishBean.setShowNegativePayMsg(true);
        //This is used in the view...
        pDeductContGarnishBean.setNetDifference(wDiff);

        pDeductContGarnishBean.setNegativeNetPayStr(naira + PayrollHRUtils.getDecimalFormat().format(Math.abs(wDiff)));

        return pDeductContGarnishBean;
    }
}