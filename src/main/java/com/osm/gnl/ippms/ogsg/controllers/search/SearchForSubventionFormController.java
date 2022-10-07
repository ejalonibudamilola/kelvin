/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.search;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.subvention.Subvention;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping({"/searchForSubvention.do"})
@SessionAttributes(types={PaginatedBean.class})
public class SearchForSubventionFormController extends BaseController{

  public SearchForSubventionFormController() {
  }

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {

	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);

      PaginationBean paginationBean = getPaginationInfo(request);

    List empList = this.genericService.loadPaginatedObjects(Subvention.class, Arrays.asList(
              getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("expire",IConstants.OFF)),(paginationBean.getPageNumber() - 1) * this.pageLength,
              this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());


    PredicateBuilder predicateBuilder = new PredicateBuilder()
            .addPredicate(getBusinessClientIdPredicate(request))
            .addPredicate(CustomPredicate.procurePredicate("expire", OFF));

    int wGLNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(
            predicateBuilder, Subvention.class);

      PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    wPELB.setShowRow(SHOW_ROW);

    model.addAttribute("subventionBean", wPELB);
    addRoleBeanToModel(model, request);
    return "subvention/activeSubventionViewForm";
  }
}