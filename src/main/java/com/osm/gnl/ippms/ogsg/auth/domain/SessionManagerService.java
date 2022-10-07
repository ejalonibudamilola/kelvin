/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.auth.domain;

import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;

import javax.servlet.http.HttpServletRequest;


/**
 * 
 * @author Mustola
 *
 */
public final class SessionManagerService {
	
	/**
     * Manages the session from the server side.
	 * @param - {@link HttpServletRequest}
     * @return - {@link User}
     */
    public static synchronized User manageSession(HttpServletRequest request)
            throws HttpSessionRequiredException, EpmAuthenticationException {
    	
        SecurityContext wSecContext = SecurityContextHolder.getContext();

        if (wSecContext == null) {
            throw new HttpSessionRequiredException("Session expired");
        }

        Object obj = wSecContext.getAuthentication().getPrincipal();

        if ((obj == null) ||!(obj instanceof User)) {
            throw new EpmAuthenticationException("Principal is unauthenticated");
        }

        return (User) obj;
    }
    
    
    /**
	 * 
	 * Manages the session from the server side.
	 * @param request - {@link HttpServletRequest}
	 * @param model - {@link Model}
	 * @return - {@link User}
	 * @throws HttpSessionRequiredException
	 * @throws EpmAuthenticationException
	 */
    public static synchronized User manageSession(HttpServletRequest request, Model model)
            throws HttpSessionRequiredException, EpmAuthenticationException {
    	
        SecurityContext wSecContext = SecurityContextHolder.getContext();

        if (wSecContext == null) {
        	
            throw new HttpSessionRequiredException("Session expired");
        }

        Object obj = wSecContext.getAuthentication().getPrincipal();

        if ((obj == null) ||!(obj instanceof User)) {
            throw new EpmAuthenticationException("Principal is unauthenticated");
        }
        

        BaseController.addFromFormToNavigator(request);
        
        //check if saved message on session, if so add it to request and remove it from session
        
          

        if(model != null)
        	BaseController.addSaveMsgOnSessionToModel(request, model);

        
        return (User) obj;
    }
    
}//end class
