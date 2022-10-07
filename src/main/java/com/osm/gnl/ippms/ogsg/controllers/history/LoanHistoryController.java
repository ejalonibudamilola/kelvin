package com.osm.gnl.ippms.ogsg.controllers.history;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.LoanService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Controller
@RequestMapping({"/viewGarnishmentHistory.do"})
public class LoanHistoryController extends BaseController {

    @Autowired
    private LoanService loanService;

    private final String VIEW = "history/garnishmentHistoryForm";

    public LoanHistoryController() {
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"eid", "garnId"})
    public String setupForm(@RequestParam("eid") Long pEmpId, @RequestParam("garnId") Long pGarnId, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);
        PaginationBean paginationBean = getPaginationInfo(request);

        AbstractGarnishmentEntity wEmpGarnInfo = (AbstractGarnishmentEntity) genericService.loadObjectUsingRestriction(IppmsUtils.getGarnishmentInfoClass(bc),
                Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("id", pGarnId)));
        double paidLoanAmount = 0.0D;
        double originalLoanAmount = 0.0D;
        //double wAddAmount = 0.0D;
        boolean wShowTotalsRow = false;
        List<CustomPredicate> predicates = new ArrayList<>();
        predicates.add(CustomPredicate.procurePredicate("employee.id", pEmpId));
        predicates.add(CustomPredicate.procurePredicate("empGarnInfo.id", pGarnId));
        int wNoOfElements = this.genericService.getTotalPaginatedObjects(IppmsUtils.getPaycheckGarnishmentClass(bc), predicates).intValue();
        double currentLoanBalance = wEmpGarnInfo.getOwedAmount();
        originalLoanAmount = wEmpGarnInfo.getOriginalLoanAmount();
        //List<AbstractPaycheckGarnishmentEntity> paycheckGarnishments = (List<AbstractPaycheckGarnishmentEntity>) this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckGarnishmentClass(bc),
        //        predicates, null);
        /**
         * The Code Above throws ERROR: target lists can have at most 1664 entries and I have not been able to find out why.
         * The code below replaces it....
         */
        List<AbstractPaycheckGarnishmentEntity> paycheckGarnishments = this.loanService.loadAllPaychecksGarnishments(bc,pEmpId,pGarnId);
        double wTotalLoanPayments = this.loanService.getTotalGarnishments(bc, pGarnId, pEmpId);


        double wWorkingLoanAmount = wTotalLoanPayments + currentLoanBalance;
        double wTotalAmountLoaned = wWorkingLoanAmount;
        if (wNoOfElements > 0) {
            wShowTotalsRow = true;

            double wCurrentPayment = 0.0D;
            for (AbstractPaycheckGarnishmentEntity p : paycheckGarnishments) {
                if (wCurrentPayment != 0.0D && wCurrentPayment != p.getAmount()) {
                    p.setDisplayStyle("reportEvenHistory");
                    p.setTitleField("Loan Restructured");
                } else {
                    p.setTitleField("");
                }
                if (p.getStartingLoanBalance() <= 0.00D) {
                    p.setDeductionAmount(wWorkingLoanAmount);
                    p.setLoanBalance(wWorkingLoanAmount - p.getAmount());
                    wWorkingLoanAmount -= p.getAmount();
                } else {
                    p.setDeductionAmount(p.getStartingLoanBalance());
                    p.setLoanBalance(p.getAftGarnBal());
                }

                paidLoanAmount += p.getAmount();
                wCurrentPayment = p.getAmount();

            }

            Collections.sort(paycheckGarnishments, Comparator.comparing(AbstractPaycheckGarnishmentEntity::getRunYear).reversed().thenComparing(Comparator.comparing(AbstractPaycheckGarnishmentEntity::getRunMonth).reversed()));
        } else {
            paycheckGarnishments = new ArrayList<>();
        }
        AbstractEmployeeEntity wEmp = IppmsUtils.loadEmployee(genericService, pEmpId, bc);
        PaginatedPaycheckGarnDedBeanHolder wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(paycheckGarnishments, paginationBean.getPageNumber(), 24, wNoOfElements, paginationBean.getSortOrder(), paginationBean.getSortCriterion());
        wPGBDH.setDisplayTitle(wEmp.getEmployeeId());
        wPGBDH.setId(wEmp.getId());

        wPGBDH.setName(wEmp.getDisplayNameWivTitlePrefixed());
        wPGBDH.setMode(wEmp.getParentObjectName());
        wPGBDH.setObjectId(wEmpGarnInfo.getId());
        wPGBDH.setObjectInd(1);
        wPGBDH.setGarnishmentName(wEmpGarnInfo.getDescription());
        wPGBDH.setOriginalLoanAmount(wTotalAmountLoaned);
        wPGBDH.setCurrentLoanAmount(currentLoanBalance);
        wPGBDH.setCurrentOriginalLoanAmount(originalLoanAmount);
        wPGBDH.setPaidLoanAmount(paidLoanAmount);
        wPGBDH.setShowTotalLoanTypeRow(wShowTotalsRow);
        addRoleBeanToModel(model, request);
        model.addAttribute("garnHist", wPGBDH);

//        return "history/garnHistoryModal.jsp";

        return VIEW;
    }
}