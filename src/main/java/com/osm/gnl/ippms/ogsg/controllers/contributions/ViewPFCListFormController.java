package com.osm.gnl.ippms.ogsg.controllers.contributions;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfcInfo;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.pagination.beans.DataTableBean;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;


@Controller
@RequestMapping({"/listPfcPfa.do"})
@SessionAttributes(types={PaginatedBean.class})
//@SessionAttributes(types={DataTableBean.class})
public class ViewPFCListFormController extends BaseController{

  public ViewPFCListFormController() {
  }

  private final int pageLength = 20;

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);

    PaginationBean paginationBean = this.getPaginationInfo(request);

//    List<?> wRetList = (List<?>)this.payrollService.loadPaginatedObjectByClass(
////    (pageNumber - 1) * this.pageLength, this.pageLength, sortOrder, sortCriterion, PfcInfo.class);
//    List<?> wRetList = this.genericService.loadPaginatedObjects(PfcInfo.class,
//            new ArrayList<CustomPredicate>(), (paginationBean.getPageNumber()- 1) * this.pageLength,
//            this.pageLength,paginationBean.getSortOrder(),paginationBean.getSortCriterion());
//
//    int wGLNoOfElements = this.genericService.getTotalNoOfModelObjectByClass(PfcInfo.class, "id", true);
//
//
//    PaginatedBean wBEOB = new PaginatedBean(wRetList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    List<?> wRetList2 = this.genericService.loadAllObjectsWithoutRestrictions(PfcInfo.class,null);
    DataTableBean wPELB = new DataTableBean(wRetList2);
//    model.addAttribute("miniBean", wBEOB);
    model.addAttribute("displayList", wPELB.getObjectList());
    addRoleBeanToModel(model, request);

    return "viewPfcListForm";
  }

  @RequestMapping(method={RequestMethod.GET}, params={"pfa"})
  public String setupForm(@RequestParam("pfa") String pPfaInd, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);
//    PaginationBean paginationBean = this.getPaginationInfo(request);

//    List<?> wRetList = this.genericService.loadPaginatedObjects(PfaInfo.class,
//            new ArrayList<CustomPredicate>(), (paginationBean.getPageNumber()- 1) * this.pageLength,
//            this.pageLength,paginationBean.getSortOrder(),paginationBean.getSortCriterion());
//
//     int wGLNoOfElements = this.genericService.getTotalNoOfModelObjectByClass(PfaInfo.class,"id", true);
//    PaginatedBean wBEOB = new PaginatedBean(wRetList, paginationBean.getPageNumber(),
//            this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    List<?> wRetList2 = this.genericService.loadAllObjectsWithoutRestrictions(PfaInfo.class,null);
    DataTableBean wPELB = new DataTableBean(wRetList2);

//    model.addAttribute("miniBean", wBEOB);
    model.addAttribute("miniBean", wPELB);
    model.addAttribute("displayList", wPELB.getObjectList());
    addRoleBeanToModel(model, request);

    return "viewPfaListForm";
  }
  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(SessionStatus status, Model model, HttpServletRequest request) throws Exception {
      SessionManagerService.manageSession(request, model);
	

    return CONFIG_HOME_URL;
  }
}