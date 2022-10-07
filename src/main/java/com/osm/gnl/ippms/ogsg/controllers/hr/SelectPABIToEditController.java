package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
@RequestMapping({"/selectPABI.do"})
@SessionAttributes(types={HrMiniBean.class})
public class SelectPABIToEditController extends BaseController
{


  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);

      List<MdaInfo> wMdas = this.genericService.loadAllObjectsWithSingleCondition(MdaInfo.class,
              CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),"name");
        addDisplayErrorsToModel(model, request);
        addRoleBeanToModel(model, request);
		model.addAttribute("mdas", wMdas);
		
		addPageTitle(model, getText("mdas.view.pageTitle", new Object[] { bc.getMdaTitle()+"s" }));
		addMainHeader(model, getText("mdas.view.mainHeader", new Object[] { bc.getMdaTitle() }));
		
		return "hr_mda/viewMdas";
  }

  
  
}