package com.osm.gnl.ippms.ogsg.web.ui.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osm.gnl.ippms.ogsg.web.ui.WebHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This is used to customize the authentication success process.
 *
 * <p>We need this to handle AJAX based web requests.
 *
 */
@Component
public class AjaxAwareAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public AjaxAwareAuthenticationSuccessHandler() {
        super();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
            throws IOException, ServletException {


        //if we have ourselves an AJAX request...
        if (WebHelper.isAjaxRequest(request)) {
            //generate json model
        	new ObjectMapper().writer()
			.writeValue(response.getOutputStream(), new LoginStatus(auth.getName(), true));

            response.getOutputStream().flush();
            response.getOutputStream().close();
        } //delegate to super class 
        else {

            super.onAuthenticationSuccess(request, response, auth);
        }
    }
}
