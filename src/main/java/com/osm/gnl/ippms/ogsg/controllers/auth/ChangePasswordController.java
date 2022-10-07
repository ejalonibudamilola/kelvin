package com.osm.gnl.ippms.ogsg.controllers.auth;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.validators.auth.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * {@link Controller} for handling change password requests.
 * @author ola Mustapha
 */
@Controller
@RequestMapping( "/changePassword.do" )
@SessionAttributes( types = { User.class } )
public class ChangePasswordController extends BaseController {

	private static final String VIEW = "user/passwordChangeForm";
	
	@Autowired
    private UserValidator validator;
	
	@RequestMapping( method = RequestMethod.GET )
	public String setupForm(HttpServletRequest pRequest, Model pModel) throws Exception {
		User wLogin = SessionManagerService.manageSession(pRequest, pModel);
		addRoleBeanToModel(pModel,pRequest);
		pModel.addAttribute( "loginBean", wLogin );
		
		return VIEW;
	}
	
	@RequestMapping( method = RequestMethod.POST )
	public String processSubmit( 
			@ModelAttribute("loginBean")User pLogin, BindingResult pResult,
			HttpServletRequest pRequest, Model pModel, SessionStatus pStatus
		) throws Exception {
		
		SessionManagerService.manageSession(pRequest, pModel);


		validator.validatePasswordChange( pLogin, pResult);
		
		if( pResult.hasErrors() ) {
			addRoleBeanToModel(pModel,pRequest);
			return VIEW;

		}
		if(!pLogin.isLogoutMessageShown()){
			pLogin.setLogoutMessageShown(true);
			pResult.rejectValue("newPassword","Warning","Password Change Successful");
			pResult.rejectValue("newPassword","Warning","Click on the Link below to Access the Login Page.");
			pLogin.setChangePasswordInd( IConstants.OFF );
			pLogin.setPasswordResetId(null);
			pLogin.setLastModBy(new User(this.getBusinessCertificate(pRequest).getLoginId()));
			pLogin.setLastModTs(Timestamp.from(Instant.now()));

			this.genericService.saveOrUpdate( pLogin );
			addRoleBeanToModel(pModel,pRequest);
			pModel.addAttribute("caseShow",pLogin.isLogoutMessageShown());
			return VIEW;
//			return "user/passwordChangeSuccess";
		}
		//pLogin.setPasswordMonitorDate(Lo);

		
//		return DETERMINE_DASHBOARD_URL;
		return SIGN_OUT_URL;
	}
	
	
	
}//end class
