package com.osm.gnl.ippms.ogsg.controllers.subvention;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.subvention.Subvention;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @author Damilola Ejalonibu
 */
@Controller
@RequestMapping({"/viewInactiveSubventions.do"})
@SessionAttributes(types = {PaginatedBean.class})
public class ViewInactiveSubventionsFormController extends BaseController {

  public ViewInactiveSubventionsFormController() {
  }

  @ModelAttribute("userList")
    public List<User> populateUsersList(HttpServletRequest request) {
//     return this.payrollService.loadActiveLogin();
        BusinessCertificate bc = super.getBusinessCertificate(request);
        return this.genericService.loadAllObjectsUsingRestrictions(User.class, Arrays.asList(
                CustomPredicate.procurePredicate("role.businessClient.id", bc.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("accountLocked", IConstants.OFF)), "firstName");
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);


        PaginationBean paginationBean = getPaginationInfo(request);


        List<Subvention> empList = this.genericService.loadPaginatedObjects(Subvention.class, Arrays.asList(
                getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("expire", ON)), (paginationBean.getPageNumber() - 1) * IConstants.pageLength,
                IConstants.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

        PredicateBuilder predicateBuilder = new PredicateBuilder()
                .addPredicate(getBusinessClientIdPredicate(request))
                .addPredicate(CustomPredicate.procurePredicate("expire", ON));


        int wGLNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(
                predicateBuilder, Subvention.class);

        PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), IConstants.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        wPELB.setShowRow(SHOW_ROW);

        model.addAttribute("miniBean", wPELB);
        model.addAttribute("roleBean", bc);
        return "subvention/inactiveSubventionForm";
    }
}