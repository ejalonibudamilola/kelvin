package com.osm.gnl.ippms.ogsg.controllers.report;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.FuturePaycheckMaster;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping({"/preFutureSimReportForm.do"})
@SessionAttributes(types={PaginatedBean.class})
public class FuturisticSimulationResultController extends BaseController
{
  private static final String VIEW_NAME = "payment/viewFuturePaycheckMaster";
  private final int pageLength = 20;

  
@RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);

	 PaginationBean paginationBean = getPaginationInfo(request);

	  BusinessCertificate bc = this.getBusinessCertificate(request);


    List<FuturePaycheckMaster> empList = this.genericService.loadPaginatedObjects(FuturePaycheckMaster.class, new ArrayList<CustomPredicate>(), (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

    int wNoOfElements = this.genericService.getTotalNoOfModelObjectByClass(FuturePaycheckMaster.class, "id", true);

    PaginatedBean wPHDB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    model.addAttribute("miniBean", wPHDB);
    addRoleBeanToModel(model, request);

    return VIEW_NAME;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"fid"})
  public String setupForm(@RequestParam("fid") Long pFsid, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);

    FuturePaycheckMaster wDel = this.genericService.loadObjectById(FuturePaycheckMaster.class,pFsid);
    if ((wDel != null) && (!wDel.isNewEntity()))
      this.genericService.deleteObject(wDel);
    return "redirect:preFutureSimReportForm.do";
  }
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String go, @ModelAttribute("miniBean") PaginatedBean pDDB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  
	  SessionManagerService.manageSession(request, model);


    return "redirect:reportsOverview.do";
  }
}