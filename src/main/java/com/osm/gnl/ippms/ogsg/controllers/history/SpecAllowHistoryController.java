package com.osm.gnl.ippms.ogsg.controllers.history;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckSpecAllowEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.HistoryService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping({"/viewSpecialAllowanceHistory.do"})
public class SpecAllowHistoryController extends BaseController
{


  private final HistoryService historyService;
  private final String VIEW_NAME = "history/specialAllowanceHistoryForm";
  @Autowired
  public SpecAllowHistoryController(HistoryService historyService)
  {
    this.historyService = historyService;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"eid", "said"})
  public String setupForm(@RequestParam("eid") Long pEmpId, @RequestParam("said") Long pSpecAllowId, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);

    BusinessCertificate businessCertificate = getBusinessCertificate(request);

    double paidLoanAmount = 0.0D;

    PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate(businessCertificate.getEmployeeIdJoinStr(),pEmpId));
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("specialAllowanceInfo.id", pSpecAllowId));

    int wNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.makePaycheckSpecAllowClass(businessCertificate));

    List<AbstractPaycheckSpecAllowEntity> paycheckSpecialAllowances = null;
    PaginationBean paginationBean = getPaginationInfo(request);
    if (wNoOfElements > 0) {
      paycheckSpecialAllowances = this.historyService.loadEmpSpecialAllowancesByEmpIdAndId(businessCertificate,(paginationBean.getPageNumber() - 1) * 24, 24, paginationBean.getSortOrder(), paginationBean.getSortCriterion(), pEmpId, pSpecAllowId);

      for (AbstractPaycheckSpecAllowEntity p : paycheckSpecialAllowances) {
        paidLoanAmount += p.getAmount();
      }
      Collections.sort(paycheckSpecialAllowances);
    } else {
      paycheckSpecialAllowances = new ArrayList<>();
    }
    AbstractEmployeeEntity wEmp = IppmsUtils.loadEmployee(genericService,pEmpId,businessCertificate);
    PaginatedPaycheckGarnDedBeanHolder wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(paycheckSpecialAllowances, paginationBean.getPageNumber(), 24, wNoOfElements, paginationBean.getSortOrder(), paginationBean.getSortCriterion());
    wPGBDH.setDisplayTitle(wEmp.getEmployeeId());
    wPGBDH.setId(wEmp.getId());

    wPGBDH.setName(wEmp.getDisplayNameWivTitlePrefixed());
    wPGBDH.setMode(wEmp.getParentObjectName());

    AbstractSpecialAllowanceEntity wSAI = (AbstractSpecialAllowanceEntity) genericService.loadObjectById(IppmsUtils.getSpecialAllowanceInfoClass(businessCertificate),  pSpecAllowId);
    wPGBDH.setObjectId(wSAI.getId());
    wPGBDH.setObjectInd(2);
    wPGBDH.setGarnishmentName(wSAI.getSpecialAllowanceType().getName());

    wPGBDH.setPaidLoanAmount(paidLoanAmount);
    model.addAttribute("specAllowHist", wPGBDH);
    addRoleBeanToModel(model, request);

    return VIEW_NAME;
  }
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
                              @ModelAttribute("specAllowHist") PaginatedPaycheckGarnDedBeanHolder pHADB, BindingResult result, SessionStatus
                                      status, Model model, HttpServletRequest request) throws Exception {
    SessionManagerService.manageSession(request, model);


    return Navigator.getInstance(getSessionId(request)).getFromForm();
  }
}