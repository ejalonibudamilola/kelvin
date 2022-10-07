package com.osm.gnl.ippms.ogsg.controllers.auth;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.SelectedEntityPredicate;
import com.osm.gnl.ippms.ogsg.audit.domain.LoginAudit;
import com.osm.gnl.ippms.ogsg.auth.domain.*;
import com.osm.gnl.ippms.ogsg.auth.services.MailerService;
import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClientMap;
import com.osm.gnl.ippms.ogsg.menu.domain.MenuLink;
import com.osm.gnl.ippms.ogsg.menu.domain.UserMenu;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PassPhrase;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.validators.user.CreateNewUserValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Controller
@RequestMapping({"/createNewUser.do"})
@SessionAttributes(types={BusinessClient.class})
public class CreateUserController extends BaseController
{

    private final static String VIEW = "user/createNewUserForm";
	private static final String REQUEST_PARAM_CANCEL = "_cancel";

	private final IMenuService menuService;

	private final CreateNewUserValidator validator;

	private final PasswordEncoder passwordEncoder;
	private final MailerService mailerService;

	@Autowired
	public CreateUserController(IMenuService menuService, CreateNewUserValidator validator, PasswordEncoder passwordEncoder, MailerService mailerService){

		this.menuService = menuService;
		this.validator = validator;
		this.passwordEncoder = passwordEncoder;
		this.mailerService = mailerService;
	}
	

	@ModelAttribute("roles")
	public List<Role> getRoleList(HttpServletRequest request) {
		BusinessCertificate bcert = getBusinessCertificate(request);
		CustomPredicate c = CustomPredicate.procurePredicate("businessClient.id", bcert.getBusinessClientInstId());
		List<CustomPredicate> customPredicateList = new ArrayList<>();
		customPredicateList.add(c);
		if(!bcert.isSuperAdmin()){
			 customPredicateList.add(CustomPredicate.procurePredicate("adminAccessInd", 0));
		}

		return this.genericService.loadAllObjectsUsingRestrictions(Role.class,customPredicateList,"name");
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String setupForm(Model model, HttpServletRequest request, @RequestParam(value = "s", required = false)Boolean pSave)
			throws Exception {
		
		User wLogin = SessionManagerService.manageSession(request, model);
		BusinessCertificate bcert = super.getBusinessCertificate(wLogin, request);

//		BusinessClient bc = new BusinessClient(bcert.getBusinessClientInstId());\

		BusinessClient bc = new BusinessClient();

		Map<Long, MenuLink> linksMap = this.menuService.getAllMenuLinksAsMap(bcert, true);
		
		UserMenu userMenu = new UserMenu();

		Collection<MenuLink> menuLinksCol = linksMap.values();
		menuLinksCol = menuService.treatMenuLinks(new ArrayList<>(menuLinksCol),bcert);
		SortedSet<MenuLink> menuLinksSet = new TreeSet<>(Comparator.comparing(MenuLink::getName));
		
		menuLinksSet.addAll(menuLinksCol);
		
		userMenu.setMenuLinks(menuLinksSet);
		bc.setUserMenu(userMenu);
		
		if( (pSave != null) && pSave ) {
	    	super.handleGetAfterSaveOperation(request, "New user successfully created.", model);
	    }
		addRoleBeanToModel(model, request);
		return prepareAndDisplayForm(model, bc);


	}
 	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(@RequestParam(value = REQUEST_PARAM_CANCEL, required = false) String cancel,@ModelAttribute ("epmUser") BusinessClient pBc, BindingResult result, SessionStatus status, Model model,HttpServletRequest request) throws Exception {
	  	
		User wLoggedInUser = SessionManagerService.manageSession(request, model);
		BusinessCertificate bc = super.getBusinessCertificate(request);

		//if it's a cancel action
	    if( super.isCancelRequest( request, cancel ) ) {
	    	return DETERMINE_DASHBOARD_URL;
	    }

	    validator.validate(pBc, result, bc);
		if (result.hasErrors()) {
			addDisplayErrorsToModel(model, request);
			model.addAttribute("status", result);
			addRoleBeanToModel(model, request);
			return prepareAndDisplayForm(model, pBc);
		}
		
		//If all is honky dorry.
		//Create Login first.
		User login = new User();
		login.setFirstName(pBc.getFirstName());
		login.setUsername(pBc.getUserName());
		login.setLastName(pBc.getLastName());
		login.setLastModBy(new User(bc.getLoginId()));
		login.setCreatedBy(login.getLastModBy());
		login.setLastModTs(Timestamp.from(Instant.now()));
	    login.setRole( new Role( (new Long( pBc.getRoleId())) ) );
		login.setSystemUserInd(pBc.isSysUser() ? IConstants.ON : IConstants.OFF);
		login.setPasswordMonitorDate(LocalDate.now().plusDays(14L));
		login.setChangePasswordInd(ON);
		login.setEmail(pBc.getEmail());
		login.setAccountLocked( IConstants.OFF );
		login.setAccountEnabled( IConstants.OFF );
		login.setLastModTs(Timestamp.from(Instant.now()));

		Mailer mailer = new Mailer();
		mailer.setAutoGenPassword(PassPhrase.generatePassword());
		mailer.setExpiration(Timestamp.valueOf(LocalDateTime.now().plus(Duration.of(7, ChronoUnit.MINUTES))));
		mailer.setSubject("IPPMS Account Creation.");
		mailer.setMessage("Dear "+login.getActualUserName()+", here are your login credentials : Username : "+login.getUsername()+"  Password. "+mailer.getAutoGenPassword()+"  NOTE this password expires by "+ PayrollUtils.formatTimeStamp(mailer.getExpiration()));
		mailer.setRecipient(login.getEmail());

		if(!mailerService.sendMailForPasswordReset(mailer)){
			result.rejectValue("id", "Invalid.Value", "Account Provisioning Email Could not be sent to "+login.getEmail()+". User Account Creation Denied.");
			model.addAttribute(DISPLAY_ERRORS, BLOCK);
			model.addAttribute("status", result);
			addRoleBeanToModel(model,request);
			this.prepareAndDisplayForm(model,pBc);
		}

		login.setPassword(passwordEncoder.encode(mailer.getAutoGenPassword()));
		this.genericService.saveObject(login);
		PasswordResetBean passwordResetBean;

		if (login.getPasswordResetId() != null) {
			passwordResetBean = genericService.loadObjectById(PasswordResetBean.class, login.getId());
		} else {
			passwordResetBean = new PasswordResetBean();
		}
		passwordResetBean.setEmail(login.getEmail());
		passwordResetBean.setPassword(mailer.getAutoGenPassword());
		passwordResetBean.setResetBy(login);
		passwordResetBean.setExpirationDate(mailer.getExpiration());
		passwordResetBean.setUser(login);

		login.setPasswordResetId(genericService.storeObject(passwordResetBean));

		this.genericService.storeObject(login);
		
		//Now create the Mapping.
		BusinessClientMap wBCM = new BusinessClientMap();
		pBc.setId(bc.getBusinessClientInstId());
		wBCM.setBusinessClient(pBc);
		wBCM.setUser(login);
		
		this.genericService.saveObject(wBCM);
		
		//audit the action
	    auditNewUserCreation(wLoggedInUser, login,bc.getBusinessClientInstId());
	    
	  //Code for User Menu
		UserMenu userMenu = pBc.getUserMenu();
		
		//Get the selected menus
		Collection<MenuLink> selectedMenus = CollectionUtils.select(userMenu.getMenuLinks(), new SelectedEntityPredicate());
		Set<MenuLink> menus = new HashSet<>(selectedMenus.size());
		

		if (selectedMenus != null && !selectedMenus.isEmpty()) {
			for (MenuLink menuLink : selectedMenus) {
				
				//get all hidden menu links for the selected menu link
				if (!login.isSysUser()) {
					if (!menuLink.isSysUser()) {
						menus.add(menuLink);
						List<MenuLink> wHiddenLinks = this.menuService.getAllHiddenMenuLinksForParent(bc, menuLink.getId(), Boolean.TRUE);
						if(IppmsUtils.isNotNullOrEmpty(wHiddenLinks)){
							menus.addAll(wHiddenLinks);
						}
					}
				}
				else{
					menus.add(menuLink);
					List<MenuLink> wHiddenLinks = this.menuService.getAllHiddenMenuLinksForParent(bc, menuLink.getId(), Boolean.TRUE);
					if(IppmsUtils.isNotNullOrEmpty(wHiddenLinks)){
						menus.addAll(wHiddenLinks);
					}					
				}
				
			}
			
			userMenu.setMenuLinks(menus);

		}
		
		if (userMenu.isNewEntity()) {
			userMenu.setCreatedBy(wLoggedInUser.getUserName());
			userMenu.setTimeCreated(LocalDate.now());
			
			userMenu.setUser(login);
		}
		
		userMenu.setLastModBy(wLoggedInUser.getUserName());
		userMenu.setLastModTs(LocalDate.now());
		
		this.menuService.persist(userMenu);
		
	    super.putSaveOperationParamInSession(request);

	    //Before Redirection, send a mail to the User....
	    
	    //redirect with a flag to display success message
	    return "redirect:/createNewUser.do?s=true";
	  
	}
	
	
	private void auditNewUserCreation(User pCreator, User pCreated, Long businessClientId) {
	  	LoginAudit wLA = new LoginAudit();
		//user that performed the creation....
	    wLA.setLogin( pCreator );
	    wLA.setChangedBy( pCreator.getUsername() );
	    //user that was created.....
	    wLA.setFirstName( pCreated.getFirstName() );
	    wLA.setLastName( pCreated.getLastName() );
	    wLA.setUserName( pCreated.getUsername() );
	    wLA.setBusinessClientId(businessClientId);
	    wLA.setDescription("New User Created");
	    wLA.setAuditTimeStamp(PayrollBeanUtils.getCurrentTime(false));
	
	    this.genericService.saveObject(wLA);
	}
	
	
	private String prepareAndDisplayForm(Model model, BusinessClient bizClient) {
		
		addPageTitle(model, getText(bizClient.isEditMode() ? "userForm.edit.pageTitle" : "userForm.create.pageTitle"));
		addMainHeader(model, bizClient.isEditMode() ? 
				getText("userForm.edit.mainHeader", new Object[] {bizClient.getUserName()}) : getText("userForm.create.mainHeader"));
		
		model.addAttribute("_userId", "-1");
		
		model.addAttribute("epmUser", bizClient);
		
		
		return VIEW;
	}


}