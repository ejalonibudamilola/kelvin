package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping({"/viewAllBankBranches.do"})
@SessionAttributes(types={PaginatedBean.class})
public class ViewExistingBankBranchesFormController extends BaseController {


    private final int pageLength = 20;

    private static final String VIEW_NAME = "bank/viewBankBranchesForm";


    @ModelAttribute("banksList")
    public List<BankInfo> generateBanksList(){

        List<BankInfo> wBankInfoList = this.genericService.loadControlEntity(BankInfo.class);
        return wBankInfoList;
    }

    @RequestMapping(method={RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
    {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        PaginationBean paginationBean = this.getPaginationInfo(request);

        List<BankBranch> empList = new ArrayList<>();

        int wGLNoOfElements = 0;

        PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements,
                paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        wPELB.setShowRow(SHOW_ROW);

        model.addAttribute("bankBranchBean", wPELB);

        model.addAttribute("roleBean", bc);

        return VIEW_NAME;
    }

    @RequestMapping(method={RequestMethod.GET},params={"bid","bn","sc"})
    public String setupForm(@RequestParam("bid") Long pBankId,@RequestParam("bn") String pBankBranchName,
                            @RequestParam("sc") String pBranchSortCode,Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
    {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        PaginationBean paginationBean = this.getPaginationInfo(request);
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        if(IppmsUtils.isNotNullAndGreaterThanZero(pBankId))
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("bankInfo.id", pBankId));



        String wBankName = null;
        if(!IppmsUtils.treatNull(pBankBranchName).equals(EMPTY_STR)){
            wBankName = IppmsUtils.treatNull(pBankBranchName);
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("name", wBankName));
        }
        String wSortCode = null;

        if(!IppmsUtils.treatNull(pBranchSortCode).equals(EMPTY_STR)){
            wSortCode = IppmsUtils.treatNull(pBranchSortCode);
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("branchSortCode", wSortCode));
        }
        List<BankBranch> empList = this.genericService.loadPaginatedObjects(BankBranch.class, predicateBuilder.getPredicates(), (paginationBean.getPageNumber() - 1) * this.pageLength,
                this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

        List<BankBranch> bankBranchList = this.genericService.loadAllObjectsUsingRestrictions(BankBranch.class, predicateBuilder.getPredicates(),null);

        int wGLNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, BankBranch.class);

        PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements,
                paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        wPELB.setBankId(pBankId);
        if(wBankName != null)
            wPELB.setName(wBankName);

        if(wSortCode != null)
            wPELB.setSortCode(wSortCode);

        wPELB.setShowRow(SHOW_ROW);

        model.addAttribute("bankBranchBean", wPELB);
        model.addAttribute("roleBean", bc);
        model.addAttribute("bankBranchList", bankBranchList);

        return VIEW_NAME;
    }

    @RequestMapping(method={RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_update", required=false) String cancel,
                                @ModelAttribute("bankBranchBean") PaginatedBean pEHB,
                                BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
    {

        return "redirect:viewAllBankBranches.do?bid="+pEHB.getBankId()+"&bn="+IppmsUtils.treatNull(pEHB.getName())+"&sc="+IppmsUtils.treatNull(pEHB.getSortCode());
    }

}