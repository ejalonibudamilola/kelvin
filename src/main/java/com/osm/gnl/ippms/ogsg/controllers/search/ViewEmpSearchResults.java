/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.search;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.DataTableBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping({"/viewMultiEmployeeResults.do"})
@SessionAttributes(types={BusinessEmpOVBean.class})
public class ViewEmpSearchResults extends BaseController {


  private final String VIEW = "search/employeeMultiResultViewForm";

  public ViewEmpSearchResults() {

  }

  @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"eid","fn", "ln", "noe", "cn"})
  public String setupForm(@RequestParam("eid") String pEmpIdStr,@RequestParam("fn") String pFirstName, @RequestParam("ln") String pLastName, @RequestParam("noe") int pNoOfEmp,
                          @RequestParam("cn") int pCodeName, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
    SessionManagerService.manageSession(request, model);

    BusinessCertificate bc = this.getBusinessCertificate(request);

    PaginationBean paginationBean = getPaginationInfo(request);
    List<CustomPredicate> wList = new ArrayList<>();
    if (IppmsUtils.isNotNullOrEmpty(pFirstName))
      wList.add(CustomPredicate.procurePredicate("firstName", pFirstName, Operation.LIKE));
    if (IppmsUtils.isNotNullOrEmpty(pLastName))
      wList.add(CustomPredicate.procurePredicate("lastName", pLastName, Operation.LIKE));
    if(IppmsUtils.isNotNullOrEmpty(pEmpIdStr))
      wList.add(CustomPredicate.procurePredicate("employeeId", pEmpIdStr, Operation.LIKE));

    wList.add(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));

    List<AbstractEmployeeEntity> empList = (List<AbstractEmployeeEntity>) this.genericService.loadPaginatedObjects(IppmsUtils.getEmployeeClass(bc), wList, (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

    List<AbstractEmployeeEntity> empList2 = (List<AbstractEmployeeEntity>) this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getEmployeeClass(bc), wList, null);

    int wGLNoOfElements = pNoOfEmp;

    BusinessEmpOVBean wBEOB = new BusinessEmpOVBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    DataTableBean wPELB = new DataTableBean(empList2);

    wPELB.setPageSize(pNoOfEmp);

    wPELB.setReadOnly(false);
    switch (pCodeName) {
      case 1:
        wPELB.setUrlName("apportionPensioner.do");
        wPELB.setSearchAgainUrl("searchForApportionment.do");
        break;
      case 2:
        wPELB.setUrlName("employeeEnquiryForm.do");
        wPELB.setSearchAgainUrl("searchEmpForEnquiry.do");
        break;
      case 3:
        wPELB.setSearchAgainUrl("searchEmpForPromoHistory.do");
        wPELB.setUrlName("viewEmpPromoHistory.do");
        break;
      case 4:
        wPELB.setSearchAgainUrl("searchForEmpForm.do");
        wPELB.setUrlName("reassignEmpDeptForm.do");
        break;
      case 5:
        wPELB.setSearchAgainUrl("searchEmpForPromotion.do");
        wPELB.setUrlName("promoteEmployee.do");
        break;
      case 6:
        wPELB.setSearchAgainUrl("searchEmpForTransfer.do");
        wPELB.setUrlName("transferEmployee.do");
        break;
      case 7:
        wPELB.setSearchAgainUrl("preLastPaycheckForm.do?pf=t");
        wPELB.setUrlName("paySlip.do");
        break;
      case 8:
        wPELB.setSearchAgainUrl("preLastPaycheckForm.do?pf=t");
        wPELB.setUrlName("lastPaycheckForm.do");
        break;
      case 9:
        wPELB.setSearchAgainUrl("searchEmpForTransferHistory.do");
        wPELB.setUrlName("viewEmpTransferHistory.do");
        break;
      case 10:
        wPELB.setSearchAgainUrl("searchEmpForPRC.do");
        wPELB.setUrlName("generatePRC.do");
        break;
      case 11:
        wPELB.setSearchAgainUrl("searchPaySlipHistory.do");
        wPELB.setUrlName("viewPayslipHistory.do");
        break;
      case 12:
        wPELB.setSearchAgainUrl("searchEmpForEdit.do");
        wPELB.setUrlName("employeeOverviewForm.do");
        break;
      case 13:
        wPELB.setSearchAgainUrl("searchEmpForEnquiry.do");
        wPELB.setUrlName("employeeEnquiryForm.do");
        break;
      case 14:
        wPELB.setSearchAgainUrl("searchEmpForSuspension.do");
        wPELB.setUrlName("suspendEmployee.do");
        break;
      case 15:
        wPELB.setSearchAgainUrl("searchEmpForSuspension.do?reab=t");
        wPELB.setUrlName("reabsorbEmployee.do");
        break;
      case 16:
        wPELB.setSearchAgainUrl("searchForApportionment.do");
        wPELB.setUrlName("apportionPensioner.do");
        break;
      case 17:
        wPELB.setSearchAgainUrl("searchGratuityPensionCalc.do");
        wPELB.setUrlName("calcPensionAndGratuity.do");
        break;
      case 18:
        wPELB.setSearchAgainUrl("searchEmpStepIncrement.do");
        wPELB.setUrlName("stepIncrement.do");
        break;
      case 19:
        wPELB.setSearchAgainUrl("searchEmpForAllowanceRule.do");
        wPELB.setUrlName("createAllowanceRule.do");
        break;
      case 20:
        wPELB.setSearchAgainUrl("searchEmpForContract.do");
        wPELB.setUrlName("createEmployeeContract.do");
        break;

    }
    addRoleBeanToModel(model, request);
    model.addAttribute("miniBean", wPELB);

    return VIEW;
  }
}