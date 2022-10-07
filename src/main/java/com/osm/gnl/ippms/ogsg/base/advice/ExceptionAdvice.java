/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.advice;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.exception.NoBusinessCertificationException;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(Throwable.class)
    public String setupForm(Throwable exception, Model model, HttpServletRequest request)
            throws HttpSessionRequiredException, EpmAuthenticationException, NoBusinessCertificationException {

        SessionManagerService.manageSession(request, model);

        request.getSession().setAttribute(IConstants.IPPMS_EXCEPTION, exception);
        return "redirect:globalError.do";

    }
}

