package com.osm.gnl.ippms.ogsg.web.ui.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osm.gnl.ippms.ogsg.web.ui.WebHelper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AjaxAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
		    throws IOException, ServletException {
		
		if (WebHelper.isAjaxRequest(request)) {
			//generate json model
			new ObjectMapper().writer()
					.writeValue(response.getOutputStream(), new LoginStatus(null, false));
			
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} else {
			super.onAuthenticationFailure(request, response, authException);
		}
	}

	
}
