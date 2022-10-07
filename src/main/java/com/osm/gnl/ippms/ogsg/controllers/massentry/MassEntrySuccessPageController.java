package com.osm.gnl.ippms.ogsg.controllers.massentry;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.massentry.PromotionHistory;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping({"/displayMassEntryResult.do"})
@SessionAttributes(types = {PaginatedPaycheckGarnDedBeanHolder.class})
public class MassEntrySuccessPageController extends BaseController {

    private final String VIEW = "massentry/massEntrySuccessForm";

    @Autowired
    private IMenuService menuService;

    public MassEntrySuccessPageController(/*IPayroll pPayrollService*/) {
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"lid", "tn"})
    public String setupForm(@RequestParam("lid") Long pEmpId,
                            @RequestParam("tn") String pObjectAttrTypeName, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        PaginationBean paginationBean = getPaginationInfo(request);

        PaginatedPaycheckGarnDedBeanHolder wPGBDH = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, pObjectAttrTypeName);
        int runMonth = wPGBDH.getRunMonth();
        int runYear = wPGBDH.getRunYear();
        List<PromotionHistory> wPromoHist = new ArrayList<PromotionHistory>();
        if ((wPGBDH == null) || (wPGBDH.isNewEntity())) {

            wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wPromoHist, paginationBean.getPageNumber(), pageLength, 0, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
            wPGBDH.setDisplayErrors("No Records found to display.");
            wPGBDH.setId(bc.getLoginId());
        } else {

            int typeInd = wPGBDH.getTypeInd();
            double amt = 0.0D;
            int tenor = 0;
            String loanName = null;
            if (typeInd == 2) {
                amt = wPGBDH.getOriginalLoanAmount();
                tenor = wPGBDH.getLoanTerm();
                loanName = wPGBDH.getName();
            } else if (typeInd == 5) {
                amt = wPGBDH.getOriginalLoanAmount();
                loanName = wPGBDH.getName();
            }
            List<PromotionHistory> wNewList = (List<PromotionHistory>) wPGBDH.getSuccessList();
            if (typeInd == 4) {
                for (PromotionHistory p : wNewList)
                    p.getEmployee().setMdaName(wPGBDH.getOldMdaMap().get(p.getEmployee().getId()));
            }

            wNewList = setFormDisplayStyle(wNewList);

            wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wNewList, paginationBean.getPageNumber(), pageLength, wNewList.size(), paginationBean.getSortCriterion(), paginationBean.getSortOrder());

            wPGBDH.setEmptyList(wNewList.size() > 0);
            wPGBDH.setId(bc.getLoginId());
            wPGBDH.setTypeInd(typeInd);
            switch (typeInd) {
                case 1:
                    wPGBDH.setDisplayErrors(wNewList.size() + " Newly Promoted " + bc.getStaffTypeName() + "s");
                    wPGBDH.setDisplayTitle("Promotion Results");
                    break;
                case 2:
                    wPGBDH.setDisplayErrors(loanName + " Summary");
                    wPGBDH.setDisplayTitle("Loan Addition Results");
                    wPGBDH.setTerminationReason(" Loan Amount - " + IConstants.naira + "" + PayrollHRUtils.getDecimalFormat().format(amt));
                    wPGBDH.setStaffId("Tenor - " + tenor + " Months.");
                    wPGBDH.setShowArrearsRow("Monthly Payment - " + PayrollHRUtils.getDecimalFormat().format(EntityUtils.convertDoubleToEpmStandard(amt / new Double(tenor))));
                    wPGBDH.setGarnishmentName(bc.getStaffTypeName() + "s Affected - " + wNewList.size());
                    break;
                case 3:
                    wPGBDH.setDisplayErrors(wNewList.size() + " Newly Added Deductions.");
                    wPGBDH.setDisplayTitle("Deduction Addition Results");
                    break;
                case 4:
                    if (bc.isSuperAdmin()) {
                        wPGBDH.setDisplayErrors(wNewList.size() + " Newly Transferred " + bc.getStaffTypeName());
                        wPGBDH.setDisplayTitle("Staff Transfer Results");
                    } else {
                        wPGBDH.setDisplayErrors(wNewList.size() + " " + bc.getStaffTypeName() + "s  Scheduled for Transfer (Awaiting Approval).");
                        wPGBDH.setDisplayTitle(bc.getStaffTypeName() + "s Awaiting Transfer Results");
                    }
                    break;
                case 5:
                    wPGBDH.setDisplayErrors(loanName + " Summary.");
                    wPGBDH.setDisplayTitle("Special Allowance Addition Results");
                    wPGBDH.setTerminationReason("Allowance Amount - " + PayrollHRUtils.getDecimalFormat().format(amt));
                    wPGBDH.setGarnishmentName(bc.getStaffTypeName() + "s Affected - " + wNewList.size());
                    break;
                case 7:
                    wPGBDH.setDisplayErrors("Payslip Email Summary Summary.");
                    wPGBDH.setDisplayTitle("Email Sending Results");
                    wPGBDH.setTerminationReason("Month & Year - " + PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(runMonth, runYear));
                    wPGBDH.setGarnishmentName(bc.getStaffTypeName() + "'s Affected - " + wNewList.size());
                    wPGBDH.setRunMonth(runMonth);
                    wPGBDH.setRunYear(runYear);

                    break;
            }

        }
        wPGBDH = setCanDoOptions(bc,wPGBDH);
        wPGBDH.setMassAttrName(pObjectAttrTypeName);
        model.addAttribute("roleBean", bc);
        model.addAttribute("promoResults", wPGBDH);

        return VIEW;
    }


    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @RequestParam(value = "_go", required = false) String pGo,
                                @ModelAttribute("promoResults") PaginatedPaycheckGarnDedBeanHolder pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            removeSessionAttribute(request, pEHB.getMassAttrName());
            return "redirect:massEntryMainDashboard.do";
        }


        if (isButtonTypeClick(request, REQUEST_PARAM_GO)) {
            removeSessionAttribute(request, pEHB.getMassAttrName());
            return "redirect:" + pEHB.getSubLinkSelect() + ".do";
        }

        removeSessionAttribute(request, pEHB.getMassAttrName());
        return "redirect:massEntryMainDashboard.do";
    }
    private PaginatedPaycheckGarnDedBeanHolder setCanDoOptions(BusinessCertificate bc,PaginatedPaycheckGarnDedBeanHolder wPGBDH) {

        if (menuService.canUserAccessURL(bc, "/massDeductionEntry.do", "/massDeductionEntry.do"))
            wPGBDH.setCanDoDeduction(true);
        if (menuService.canUserAccessURL(bc, "/massEmailPayslips.do", "/massEmailPayslips.do"))
            wPGBDH.setCanEmailPayslips(true);
        if (menuService.canUserAccessURL(bc, "/massLoanEntry.do", "/massLoanEntry.do"))
             wPGBDH.setCanDoLoan(true);
        if (menuService.canUserAccessURL(bc, "/massPromotions.do", "/massPromotions.do"))
            wPGBDH.setCanDoPromotion(true);
        if (menuService.canUserAccessURL(bc, "/massSpecialAllowance.do", "/massSpecialAllowance.do"))
            wPGBDH.setCanDoSpecialAllow(true);
        if (menuService.canUserAccessURL(bc, "/massTransfer.do", "/massTransfer.do"))
            wPGBDH.setCanDoTransfer(true);

        return wPGBDH;
    }


    private List<PromotionHistory> setFormDisplayStyle(List<PromotionHistory> pEmpList) {
        int i = 1;
        for (PromotionHistory e : pEmpList) {
            if (i % 2 == 1)
                e.setDisplayStyle("reportEven");
            else {
                e.setDisplayStyle("reportOdd");
            }
            i++;
        }
        return pEmpList;
    }
}