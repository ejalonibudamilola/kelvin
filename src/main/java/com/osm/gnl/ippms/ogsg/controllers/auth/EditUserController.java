package com.osm.gnl.ippms.ogsg.controllers.auth;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.SelectedEntityPredicate;
import com.osm.gnl.ippms.ogsg.audit.domain.LoginAudit;
import com.osm.gnl.ippms.ogsg.auth.domain.Role;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.menu.domain.MenuLink;
import com.osm.gnl.ippms.ogsg.menu.domain.UserMenu;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.validators.user.CreateNewUserValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;


@Controller
@RequestMapping({ "/editUserProfile.do" })
@SessionAttributes(types = { BusinessClient.class, Long.class })
public class EditUserController extends BaseController {

	private final String VIEW = "user/editUserForm";

	private final IMenuService menuService;

	private final CreateNewUserValidator validator;

	@Autowired
	public EditUserController(IMenuService menuService, CreateNewUserValidator validator) {
		this.menuService = menuService;
		this.validator = validator;
	}

	@ModelAttribute("roles")
	public Collection<Role> getRoles(HttpServletRequest request) {
		BusinessCertificate bc = getBusinessCertificate(request);
		return this.genericService.loadAllObjectsWithSingleCondition(Role.class, CustomPredicate.procurePredicate("businessClient.id", bc.getBusinessClientInstId()),"name");
	}

	@RequestMapping(method = RequestMethod.GET)
	public String setupForm(@RequestParam(value = "lid") Long pLid,
			@RequestParam(value = "s", required = false) Boolean pSave, Model model, HttpServletRequest request)
			throws Exception {
		SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

		User wLogin = this.genericService.loadObjectUsingRestriction(User.class, Arrays.asList(CustomPredicate.procurePredicate("role.businessClient.id", bc.getBusinessClientInstId()),
				CustomPredicate.procurePredicate("id",pLid)));

		wLogin.setOldRoleName(wLogin.getRole().getName());

		BusinessClient bClient = createBusinessClientFromLogin(bc, wLogin);

		bClient.setBusinessCertificate(bc);
		bClient.setId(bc.getBusinessClientInstId());

		Map<Long, MenuLink> linksMap = null;
		// If Not system administrator, currently logged in user should only have access
		// to assign links that are configured on his profile
		if (!bc.isSysUser())
			linksMap = this.menuService.getAllMenuLinksAsMap(bc, true);
		else
			linksMap = this.menuService.getAllMenuLinksAsMap(bc, false);

		UserMenu userMenu = this.menuService.getUserMenuForUser(bClient, pLid);

		wLogin.setEditMode(true);
		bClient.setEditMode(true);

		List<Long> userMenuLinkIds = this.menuService.getMenuLinkIdsForUser(bClient, pLid);
		if (!IppmsUtils.isNullOrEmpty(userMenuLinkIds)) {
			for (Long menuId : userMenuLinkIds) {
				MenuLink menuLink = linksMap.get(menuId);

				assert menuLink != null : "[MenuLink] not found for id --> " + menuId;

				if (menuLink != null)
					menuLink.setSelected(true);
				// else menu link is not assigned to current logged in user and should be denied
				// access to edit the designated user
				else {
					linksMap.remove(menuId);
					//delete from the User Menu Links....

				}
					//return PERMISSION_DENIED_URL;
			}
		}

		bClient.setUserName(wLogin.getUserName());
		bClient.setFirstName(wLogin.getFirstName());
		bClient.setLastName(wLogin.getLastName());
		bClient.setRoleId(wLogin.getRole().getId());

		// bc.setSysUser(wLogin.isSysUser());
		bClient.setSysUser(wLogin.isSysUser());
		bClient.setAllowWeekendLogin(wLogin.isAllowWeekendLogin());

		// Menus
		Collection<MenuLink> menuLinksCol = linksMap.values();
		menuLinksCol = menuService.treatMenuLinks(new ArrayList<>(menuLinksCol),bc);
		SortedSet<MenuLink> menuLinksSet = new TreeSet<MenuLink>(Comparator.comparing(MenuLink::getName));

		menuLinksSet.addAll(menuLinksCol);

		userMenu.setMenuLinks(menuLinksSet);
		bClient.setUserMenu(userMenu);

		bClient.setUser(wLogin);

		if (pSave != null && pSave.booleanValue()) {
			addSaveMsgToModel(request, model, "User: " + wLogin.getFirstName() + " " + wLogin.getLastName() + " ["
					+ wLogin.getUsername() + "] " + " Updated Successfully.");
		}

		return prepareAndDisplayForm(model, bClient,request);

	}


	@RequestMapping(method = { RequestMethod.POST })
	public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
			@ModelAttribute("epmUser") BusinessClient bClient, BindingResult result, SessionStatus status, Model model,
			HttpServletRequest request) throws Exception {
		User wLoggedInUser = SessionManagerService.manageSession(request, model);
		BusinessCertificate bc = this.getBusinessCertificate(request);

		if (super.isCancelRequest(request, cancel)) {
			return "redirect:viewUsers.do";
		}

		validator.validate(bClient, result, bc);

		if (result.hasErrors()) {
			bClient.setWarningIssued(false);
			return prepareAndDisplayForm(model, bClient, request);
		}

		// confirm from user
		if (!(bClient.isWarningIssued())) {
			bClient.setWarningIssued(true);
			result.rejectValue(null, null,
					"Are you sure about the changes to the user information? If you are click 'Save', else 'Cancel'.");
			return prepareAndDisplayForm(model, bClient, request);
		}

		// finalize the edit process...
		finalizeUserEdit(wLoggedInUser, bClient, bc,request);

		return "redirect:editUserProfile.do?lid=" + bClient.getUser().getId() + "&s=true";


	}

	private String prepareAndDisplayForm(Model model, BusinessClient bizClient, HttpServletRequest request) {

		addPageTitle(model, getText(bizClient.isEditMode() ? "userForm.edit.pageTitle" : "userForm.create.pageTitle"));
		addMainHeader(model,
				bizClient.isEditMode() ? getText("userForm.edit.mainHeader", new Object[] { bizClient.getUserName() })
						: getText("userForm.create.mainHeader"));

		model.addAttribute("_userId", bizClient.isEditMode() ? bizClient.getUser().getId().toString() : "-1");

		model.addAttribute("epmUser", bizClient);
        addRoleBeanToModel(model, request);
		return VIEW;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	private void finalizeUserEdit(User wLoggedInUser, BusinessClient bClient, BusinessCertificate bc,HttpServletRequest request) {


		User login = bClient.getUser();

		// AUDIT at this point...
		auditUserEdit(login, wLoggedInUser, "User Profile Edited", bc.getBusinessClientInstId(),request);

		login.setFirstName(bClient.getFirstName());
		login.setLastName(bClient.getLastName());
		login.setLastModBy(wLoggedInUser);
		login.setRole(new Role(bClient.getRoleId()));
		login.setSystemUserInd(bClient.isSysUser() ? ON : OFF);
		login.setAllowWeekendLoginInd(bClient.isAllowWeekendLogin() ? ON : OFF);
		if(bc.isPrivilegedUser())
		       login.setEmail(bClient.getEmail());

		this.genericService.saveObject(login);

		// Save all menu links assigned to User
		setMenuLinksForUser(bClient, bc, login);

	}

	private BusinessClient createBusinessClientFromLogin(BusinessCertificate bcert, User login) {

		BusinessClient bc = new BusinessClient();
		bc.setId(bcert.getBusinessClientInstId());
		bc.setFirstName(login.getFirstName());
		bc.setLastName(login.getLastName());
		bc.setUserName(login.getUserName());
		bc.setRoleId(login.getRole().getId());
		bc.setUser(login);
		bc.setEmail(login.getEmail());
		bc.setEditMode(true);

		//List<Role> wRoleList = this.payrollService.loadEpmRoles(bcert);
		//bc.setEpmRoleList(wRoleList);

		return bc;
	}

	private void auditUserEdit(User login, User editor, String editDescription, Long busClientId, HttpServletRequest request) {
		LoginAudit wLA = new LoginAudit();

		wLA.setLogin(editor);
		wLA.setFirstName(login.getFirstName());
		wLA.setLastName(login.getLastName());
		wLA.setUserName(login.getUserName());
		wLA.setChangedBy(editor.getUserName());
		wLA.setDescription(editDescription);
		wLA.setLastModTs(LocalDate.now());
		wLA.setAuditTimeStamp(PayrollBeanUtils.getCurrentTime());
		wLA.setBusinessClientId(busClientId);
		wLA.setRemoteIpAddress(request.getRemoteAddr());

		this.genericService.storeObject(wLA);
	}

	
	private void setMenuLinksForUser(BusinessClient bizClient, BusinessCertificate bc, User login) {
		// Code for User Menu
		UserMenu userMenu = bizClient.getUserMenu();

		// Get the selected menus
		Collection<MenuLink> selectedMenus = CollectionUtils.select(userMenu.getMenuLinks(),
				new SelectedEntityPredicate());
		Set<MenuLink> menus = new HashSet<>(selectedMenus.size());

		if (selectedMenus != null && !selectedMenus.isEmpty()) {
			for (MenuLink menuLink : selectedMenus) {

				// get all hidden menu links for the selected menu link
				if (!login.isSysUser()) {
					if (!menuLink.isSysUser()) {
						menus.add(menuLink);
						List<MenuLink> wHiddenLinks = this.menuService.getAllHiddenMenuLinksForParent(bc,
								menuLink.getId(), Boolean.TRUE);
						if (IppmsUtils.isNotNullOrEmpty(wHiddenLinks)) {
							menus.addAll(wHiddenLinks);
						}
					}
				} else {
					menus.add(menuLink);
					List<MenuLink> wHiddenLinks = this.menuService.getAllHiddenMenuLinksForParent(bc, menuLink.getId(),
							Boolean.TRUE);
					if (IppmsUtils.isNotNullOrEmpty(wHiddenLinks)) {
						menus.addAll(wHiddenLinks);
					}
				}

			}

			userMenu.setMenuLinks(menus);

		}


		if (userMenu.isNewEntity()) {
			userMenu.setCreatedBy(bc.getUserName());
			userMenu.setUser(login);
		}

		userMenu.setLastModBy(bc.getUserName());
		userMenu.setLastModTs(LocalDate.now());

		this.menuService.persist(userMenu);
	}

}