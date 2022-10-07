package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.nextofkin.domain.RelationshipType;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
@Controller
@RequestMapping({"/viewRelationshipTypes.do"})
@SessionAttributes(types={PaginatedBean.class})
public class ViewRelationshipTypeFormController extends BaseController {

	  
	  private final int pageLength = 20;


	  
	@RequestMapping(method={RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
	  {
		  SessionManagerService.manageSession(request, model);

		PaginationBean paginationBean = this.getPaginationInfo(request);

//	    List<RelationshipType> empList =  this.genericService.loadPaginatedObjects(RelationshipType.class, new ArrayList<>(),
//				(paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(),
//				paginationBean.getSortCriterion());

	    List<RelationshipType> empList =  this.genericService.loadControlEntity(RelationshipType.class);
	    int wGLNoOfElements = this.genericService.getTotalNoOfModelObjectByClass(RelationshipType.class, "id", true);

	    PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements,
				paginationBean.getSortCriterion(), paginationBean.getSortOrder());

	    wPELB.setShowRow(SHOW_ROW);

	    model.addAttribute("relTypeBean", wPELB);
	    model.addAttribute("empList", empList);
	    addRoleBeanToModel(model, request);

	    //return "viewAllBanksForm";
	    
	    return "viewRelationTypesForm";
	  }

}
