package com.osm.gnl.ippms.ogsg.controllers.hr;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.HRService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.LgaMiniBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"/empByRel.do"})
public class ViewTotalEmpByReligionFormController extends BaseController
{

  @Autowired
  HRService hrService;

  private static final String VIEW_NAME = "employee/totalEmpByFaithForm";


  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest pRequest) throws HttpSessionRequiredException, EpmAuthenticationException
  {
    SessionManagerService.manageSession(pRequest, model);

    BusinessCertificate bc = this.getBusinessCertificate(pRequest);

    PaginationBean paginationBean = getPaginationInfo(pRequest);

    List<LgaMiniBean> wDEList = this.hrService.getTotalNoOfEmpByReligion(bc);

    PaginatedBean wPDE = new PaginatedBean(wDEList, paginationBean.getPageNumber(), wDEList.size(), wDEList.size()
            , paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    wPDE.setPageSize(wDEList.size());

    addRoleBeanToModel(model, pRequest);
    model.addAttribute("miniBean", wPDE);

    return VIEW_NAME;
  }
}