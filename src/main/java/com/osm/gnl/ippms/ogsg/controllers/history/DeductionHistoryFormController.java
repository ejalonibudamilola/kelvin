package com.osm.gnl.ippms.ogsg.controllers.history;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckDeductionEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.DeductionService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
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
@RequestMapping({"/viewDeductionHistory.do"})
public class DeductionHistoryFormController extends BaseController
{

  @Autowired
  private DeductionService deductionService;

  private final String VIEW = "history/deductionHistoryForm";
  
  public DeductionHistoryFormController()
  {}

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"eid", "dedId"})
  public String setupForm(@RequestParam("eid") Long pEmpId, @RequestParam("dedId") Long pDedId, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);

    BusinessCertificate bc = getBusinessCertificate(request);
    PaginationBean paginationBean = getPaginationInfo(request);
    PredicateBuilder predicateBuilder = new PredicateBuilder();
    predicateBuilder.addPredicate(Arrays.asList(CustomPredicate.procurePredicate("employee.id", pEmpId), CustomPredicate.procurePredicate("empDedInfo.id", pDedId)));
    double paidLoanAmount = this.genericService.sumFieldUsingPredicateBuilder(IppmsUtils.getPaycheckDeductionClass(bc),predicateBuilder,Double.class,"amount",new ArrayList<>());

    int wNoOfElements = this.genericService.getTotalPaginatedObjects(IppmsUtils.getPaycheckDeductionClass(bc), predicateBuilder.getPredicates()).intValue();
    
    List<AbstractPaycheckDeductionEntity> wPayDed;
    if (wNoOfElements > 0) {
      /*wPayDed = (List<AbstractPaycheckDeductionEntity>)this.genericService.loadPaginatedObjectsByPredicates(IppmsUtils.getPaycheckDeductionClass(bc),predicateBuilder,(paginationBean.getPageNumber() - 1) * this.pageLength,
              this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());*/
      wPayDed = this.deductionService.getDeductionHistory(bc,pEmpId,pDedId);
      Collections.sort(wPayDed, Comparator.comparing(AbstractPaycheckDeductionEntity::getPayDate).reversed());
    } else {
      wPayDed = new ArrayList<>();
    }
    AbstractEmployeeEntity wEmp = IppmsUtils.loadEmployee(genericService,pEmpId,bc);
    PaginatedPaycheckGarnDedBeanHolder wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wPayDed, paginationBean.getPageNumber(), 24, wNoOfElements, paginationBean.getSortOrder(), paginationBean.getSortCriterion());
    wPGBDH.setDisplayTitle(wEmp.getEmployeeId());
    wPGBDH.setId(wEmp.getId());

    wPGBDH.setName(wEmp.getDisplayNameWivTitlePrefixed());
    wPGBDH.setMode(wEmp.getParentObjectName());

    AbstractDeductionEntity wEGI = (AbstractDeductionEntity) genericService.loadObjectById(IppmsUtils.getDeductionInfoClass(bc), pDedId);
    wPGBDH.setObjectId(wEGI.getId());
    wPGBDH.setObjectInd(2);
    wPGBDH.setGarnishmentName(wEGI.getDescription());

    wPGBDH.setPaidLoanAmount(paidLoanAmount);
    addRoleBeanToModel(model, request);
    model.addAttribute("garnHist", wPGBDH);

    return VIEW;
  }
}