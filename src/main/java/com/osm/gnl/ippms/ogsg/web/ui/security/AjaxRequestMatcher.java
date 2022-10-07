package com.osm.gnl.ippms.ogsg.web.ui.security;

import com.osm.gnl.ippms.ogsg.web.ui.WebHelper;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * This is a <code>RequestMatcher</code> that is used to determine
 * if a request is AJAX based or not.
 * 
 * <p>This is useful for authentication during AJAX 
 * requests. A value should be put in the <b>Headers</b>
 * of the HTTP request, which will then be checked for.
 */
@Component
public class AjaxRequestMatcher implements RequestMatcher {

	@Override
	public boolean matches(HttpServletRequest request) {
		return WebHelper.isAjaxRequest(request);
	}
}
