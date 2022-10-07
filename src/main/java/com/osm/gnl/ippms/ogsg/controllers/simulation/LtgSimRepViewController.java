/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.simulation;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.LeaveReportService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.ApplicationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.ltg.domain.LtgMasterBean;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
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
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping({"/viewLtgSimulations.do"})
@SessionAttributes(types={PaginatedBean.class})
public class LtgSimRepViewController extends BaseController
{

  @Autowired
  LeaveReportService leaveReportService;
  
  private final int pageLength = 20;

  private static final String VIEW_NAME = "LTG/casp/viewLtgSimulationForm";

  
  public LtgSimRepViewController() {}

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);
      PaginationBean paginationBean = getPaginationInfo(request);

    List<LtgMasterBean> empList = this.leaveReportService.loadLtgMasterBeansForDisplay(
            (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(), bc);


    int wNoOfElements = this.genericService.getTotalNoOfModelObjectByClass(LtgMasterBean.class, "id", true);

    PaginatedBean wPHDB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    model.addAttribute("miniBean", wPHDB);
    addRoleBeanToModel(model, request);

    return VIEW_NAME;
  }
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"lid"})
  public String setupForm(@RequestParam("lid") Long pPid, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

    LtgMasterBean wLMB = this.genericService.loadObjectUsingRestriction(LtgMasterBean.class, Arrays.asList(
            CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), CustomPredicate.procurePredicate("id", pPid)));


    if (wLMB.isNewEntity()) {
      throw new ApplicationException("Invalid Parameter passed for LTG Simulation. Please contact your Payroll Administrator.");
    }
    this.genericService.deleteObject(wLMB);

    return "redirect:viewLtgSimulations.do";
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("miniBean") PaginatedBean pHADB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);

    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
      return "redirect:reportsOverview.do";
    }

    return "redirect:viewLtgSimulations.do";
  }
}