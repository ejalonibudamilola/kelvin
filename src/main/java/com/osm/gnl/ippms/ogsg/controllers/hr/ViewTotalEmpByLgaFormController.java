package com.osm.gnl.ippms.ogsg.controllers.hr;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping({"/empByLgaForm.do"})
@SessionAttributes(types={PaginatedBean.class})
public class ViewTotalEmpByLgaFormController extends BaseController
{
    @Autowired
    HRService hrService;

  
    private static final String VIEW_NAME = "employee/totalEmpByLgaForm";


  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest pRequest, HttpServletResponse pResponse) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(pRequest, model);

	  BusinessCertificate bc = this.getBusinessCertificate(pRequest);

	  PaginationBean paginationBean = getPaginationInfo(pRequest);

    List<LgaMiniBean> wNEList = this.hrService.getTotalNoOfEmpPerLGA( bc);

    PaginatedBean wPDE = new PaginatedBean(wNEList, paginationBean.getPageNumber(), wNEList.size(), wNEList.size(), paginationBean.getSortCriterion(),
            paginationBean.getSortOrder());

    wPDE.setPageSize(wNEList.size());

    addRoleBeanToModel(model, pRequest);
    model.addAttribute("miniBean", wPDE);

    return VIEW_NAME;
  }
}