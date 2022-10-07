package com.osm.gnl.ippms.ogsg.web.ui.controller;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.SelectedEntityPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.Role;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.menu.domain.MenuLink;
import com.osm.gnl.ippms.ogsg.menu.domain.RoleMenuMaster;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.web.ui.validators.RoleMenuLinkValidator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.apache.commons.collections4.ListUtils.select;


/**
 * Edit/Create {@link MenuLink MenuLinks} for a
 * particular {@link Role Role}.
 * @Author Mustola - To Allow Creation of new roles or editing existing roles as well
 * @see RoleMenuLinkValidator
 * @see RoleMenuMaster
 */
@Controller
@RequestMapping("/editRoleMenuLinks.do")
@SessionAttributes("roleMenuMasterBean")
public class EditRoleMenuLinkController extends BaseController {

    @Autowired
    private IMenuService menuService;


    @Autowired
    private RoleMenuLinkValidator validator;

    @ModelAttribute("roleList")
    public List<Role> getRoleList(HttpServletRequest request) throws Exception {
        int roleId = ServletRequestUtils.getIntParameter(request, "roleId", 0);
        if (roleId <= 0) {
            return this.menuService.getRolesWithoutMenuLinks(getBusinessCertificate(request));
        }

        return this.genericService.loadAllObjectsUsingRestrictions(Role.class, Arrays.asList(
                CustomPredicate.procurePredicate("adminAccessInd", IConstants.OFF),
                CustomPredicate.procurePredicate("businessClient.id", getBusinessCertificate(request).getBusinessClientInstId())), "name");

    }

    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(HttpServletRequest request, Model model,
                            @RequestParam(value = "roleId", required = false) Long roleId) throws HttpSessionRequiredException, EpmAuthenticationException {

        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);

        RoleMenuMaster roleMenuMaster = this.menuService.getRoleMenuMasterForRole(bc, roleId);
        Map<Long, MenuLink> linksMap = null;
        //If Not system administrator, currently logged in user should only have access to assign links that are configured on his profile
        if (!bc.isSysUser())
            linksMap = this.menuService.getAllMenuLinksAsMap(bc, true);
        else
            linksMap = this.menuService.getAllMenuLinksAsMap(bc, false);

        if (roleMenuMaster.isNotNewEntity()) {
            //Once we are editing we shouldn't be able to change roles
            roleMenuMaster.setEditMode(true);

            List<Long> roleMenuLinkIds = this.menuService.getMenuLinkIdsForRole(bc, roleId);

            if (!IppmsUtils.isNullOrEmpty(roleMenuLinkIds)) {
                for (Long menuId : roleMenuLinkIds) {
                    MenuLink menuLink = linksMap.get(menuId);

                    assert menuLink != null : "[MenuLink] not found for id --> " + menuId;

                    if (menuLink != null)
                        menuLink.setSelected(true);
                }
            }

            roleMenuMaster.getRole().setAdminUserFlag(roleMenuMaster.getRole().isAdminUser());
        }

        Collection<MenuLink> menuLinksCol = linksMap.values();
        menuLinksCol = menuService.treatMenuLinks(new ArrayList<>(menuLinksCol),bc);
       // Collection<MenuLink> menuLinksCol = menuService.treatMenuLinks((List<MenuLink>) linksMap.values(),bc);

        SortedSet<MenuLink> menuLinksSet = new TreeSet<MenuLink>(Comparator.comparing(MenuLink::getName));

        menuLinksSet.addAll(menuLinksCol);

        roleMenuMaster.setRoleMenuLinks(menuLinksSet);

        return prepareAndDisplayForm(model, roleMenuMaster,request);
    }

    
    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(
            @ModelAttribute("roleMenuMasterBean") RoleMenuMaster roleMenuMaster,
            BindingResult result,
            @RequestParam(value = "_cancel", required = false) String cancel,
            HttpServletRequest request,
            Model model,
            SessionStatus status,
            RedirectAttributes redirectAttr) throws HttpSessionRequiredException, EpmAuthenticationException {

        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);

        if (StringUtils.isBlank(cancel)) {
            validator.validate(roleMenuMaster, result);

            if (result.hasErrors()) {
                return prepareAndDisplayForm(model, roleMenuMaster, request);
            }

            //Persist EpmRole Object
            roleMenuMaster.getRole().setLastModBy(new User(bc.getLoginId()));
            roleMenuMaster.getRole().setLastModTs(Timestamp.from(Instant.now()));
            roleMenuMaster.getRole().setAdminAccessInd(roleMenuMaster.getRole().isAdminUserFlag() ? IConstants.ON : IConstants.OFF);

            if(roleMenuMaster.getRole().isNewEntity()){
                roleMenuMaster.getRole().setCreatedBy(new User(bc.getLoginId()));
                roleMenuMaster.getRole().setBusinessClient(new BusinessClient(bc.getBusinessClientInstId()));
            }

            this.menuService.persist(roleMenuMaster.getRole());

            //Get the selected menus
            roleMenuMaster.setRoleMenuLinks(new HashSet<MenuLink>(
                    select(roleMenuMaster.getRoleMenuLinks(), new SelectedEntityPredicate())));


            if (roleMenuMaster.isNewEntity()) {
                roleMenuMaster.setCreatedBy(bc.getUserName());
                roleMenuMaster.setTimeCreated(LocalDate.now());
            }

            roleMenuMaster.setLastModBy(bc.getUserName());
            roleMenuMaster.setLastModTs(LocalDate.now());
            roleMenuMaster.setDescription(roleMenuMaster.getRole().getDisplayName());

            this.menuService.persist(roleMenuMaster);

            addSaveMsgToFlashMap(redirectAttr, getText(roleMenuMaster.isEditMode() ? "roleMenuLink.form.success.edit" : "roleMenuLink.form.success.create"));
        }

        status.setComplete();
        return "redirect:/viewRoleMenuLinks.do";
    }


    private String prepareAndDisplayForm(Model model, RoleMenuMaster roleMenuMasterBean, HttpServletRequest request) {
        String placeholder = getText(roleMenuMasterBean.isEditMode() ? "text.edit" : "text.create");

        String mainHeader = roleMenuMasterBean.isEditMode() ?
                getText("roleMenuLink.mainHeader.edit", new Object[]{roleMenuMasterBean.getRole().getName()}) :
                getText("roleMenuLink.mainHeader.create");

        addPageTitle(model, getText("roleMenuLink.pageTitle", new Object[]{placeholder}));
        addMainHeader(model, mainHeader);
        addRoleBeanToModel(model,request);
        model.addAttribute("roleMenuMasterBean", roleMenuMasterBean);

        return "menu/editRoleMenuLinks";
    }
}
