package com.osm.gnl.ippms.ogsg.controllers.auth;

import com.osm.gnl.ippms.ogsg.audit.domain.LoginAudit;
import com.osm.gnl.ippms.ogsg.auth.domain.IppmsEncoder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.BusinessCertificateCreator;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;

@Controller
@RequestMapping({"/loginForm.gnl"})
//@SessionAttributes(types={User.class})
@Slf4j
public class InitLoginForm extends BaseController
{

	  private final PasswordEncoder passwordEncoder;


	@Autowired
	public InitLoginForm(final PasswordEncoder passwordEncoder) {

		this.passwordEncoder = passwordEncoder;
	}

	
	@RequestMapping( method = RequestMethod.GET )
	  public String setupForm(Model model, HttpServletRequest pRequest) throws Exception {
		  //note that the user will always be redirected here 
		  //after login regardless of the requested URL. 
		  User wLogin = SessionManagerService.manageSession( pRequest, model );

		  //if the user's session doesn't have an id then create one
		  //else just proceed as necessary
		  if( !(super.hasSessionId( pRequest )) ){
			  super.createSessionId( wLogin, pRequest, this.passwordEncoder );
			 //save Login Audit Information
			  saveLoginAuditInfo( wLogin,pRequest );

		  }
        BusinessCertificate bc = (BusinessCertificate) pRequest.getSession().getAttribute(IppmsEncoder.getCertificateKey());
		  if(bc == null) {
			  bc = BusinessCertificateCreator.createBusinessCertificate(pRequest, wLogin, genericService);
			  pRequest.getSession().setAttribute(IppmsEncoder.getCertificateKey(),bc);
		  }

		//if user needs to change password
		  if( wLogin.isChgPassword() ) {
			  return CHANGE_PASSWORD_URL;
		  }

		  if(bc.isExecutive()){
			  return "redirect:/showChartDashboard.do";
		  }
		  
		  //determine user's dashboard
		  return DETERMINE_DASHBOARD_URL;
	  }

	  
	  //perform login audit
	  private void saveLoginAuditInfo( User login, HttpServletRequest request ) throws IllegalAccessException, InstantiationException {
		  	LoginAudit wLA = new LoginAudit();

		    wLA.setLogin(login);
		    wLA.setFirstName(login.getFirstName());
		    wLA.setLastName(login.getLastName());
		    wLA.setUserName(login.getUserName());
		    
		    wLA.setChangedBy(login.getUserName());
		    wLA.setDescription("Logon Successful");
		    wLA.setLastModTs(LocalDate.now());
		    wLA.setAuditTimeStamp(PayrollBeanUtils.getCurrentTime(false));
		    wLA.setBusinessClientId(login.getRole().getBusinessClient().getId());
		    wLA.setRemoteIpAddress(request.getRemoteAddr());

		    this.genericService.saveObject(wLA);

		    //we need to set the Last Login Date as well...
		  User _login = this.genericService.loadObjectById(User.class,login.getId());
		  _login.setLastLoginDate(Timestamp.from(Instant.now()));
		  this.genericService.saveObject(_login);
	  }
	  
	  

	
}