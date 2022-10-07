package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.DataTableBean;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping({"/viewAllBanks.do"})
@SessionAttributes(types={PaginatedBean.class})
public class ViewExistingBanksFormController extends BaseController
{
   
  private final int pageLength = 20;

  private static final String VIEW_NAME = "bank/viewAllBanksForm";

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);

      BusinessCertificate bc = this.getBusinessCertificate(request);
	     
	   PaginationBean paginationBean = this.getPaginationInfo(request);

    List<BankInfo> empList = this.genericService.loadPaginatedObjects(
            BankInfo.class, new ArrayList<>(), (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

    List<BankInfo> bankList = this.genericService.loadAllObjectsWithoutRestrictions(BankInfo.class, "name");

    PredicateBuilder predicateBuilder = new PredicateBuilder();
    int wGLNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, BankInfo.class);

    PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements,
            paginationBean.getSortCriterion(), paginationBean.getSortOrder());




    wPELB.setShowRow(SHOW_ROW);
    model.addAttribute("roleBean", bc);
    model.addAttribute("bankBean", wPELB);
    model.addAttribute("bankList", bankList);
    return VIEW_NAME;
  }
  
  @RequestMapping(method={RequestMethod.GET},params={"bn","sc"})
  public String setupForm(@RequestParam("bn") String pBankName,
		  @RequestParam("sc") String pSortCode,Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);
	     
    PaginationBean paginationBean = this.getPaginationInfo(request);

    BusinessCertificate bc = this.getBusinessCertificate(request);
    
    String wBankName = null;
    if(!IppmsUtils.treatNull(pBankName).equals(EMPTY_STR)){
    	wBankName = IppmsUtils.treatNull(pBankName);
    }
    String wSortCode = null;
    
    if(!IppmsUtils.treatNull(pSortCode).equals(EMPTY_STR)){
    	wSortCode = IppmsUtils.treatNull(pSortCode);
    }

    List<BankInfo> empList = this.genericService.loadPaginatedObjects(BankInfo.class, Arrays.asList(CustomPredicate.procurePredicate("name", wBankName),
            CustomPredicate.procurePredicate("sortCode", wSortCode)),(paginationBean.getPageNumber() - 1) * this.pageLength,
            this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

    List<BankInfo> bankList = this.genericService.loadAllObjectsUsingRestrictions(BankInfo.class, Arrays.asList(CustomPredicate.procurePredicate("name", wBankName),
            CustomPredicate.procurePredicate("sortCode", wSortCode)),null);

      PredicateBuilder predicateBuilder = new PredicateBuilder();

      predicateBuilder.addPredicate(CustomPredicate.procurePredicate("name", wBankName));
      predicateBuilder.addPredicate(CustomPredicate.procurePredicate("sortCode", wSortCode));

    int wGLNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, BankInfo.class);

    PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements,
            paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    wPELB.setShowRow(SHOW_ROW);

    model.addAttribute("bankBean", wPELB);

    model.addAttribute("roleBean", bc);

    model.addAttribute("bankList", bankList);

    return VIEW_NAME;
  }
  
  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_update", required=false) String update, 
		  @RequestParam(value="_cancel", required=false) String cancel, 
		  @ModelAttribute("bankBean") PaginatedBean pEHB, 
		  BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);

	    if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
	    {
	      return CONFIG_HOME_URL;
	    }

	   
    return "redirect:viewAllBanks.do?bn="+IppmsUtils.treatNull(pEHB.getName())+"&sc="+IppmsUtils.treatNull(pEHB.getSortCode());
  }
}