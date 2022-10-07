/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.web.ui;

import com.osm.gnl.ippms.ogsg.auth.domain.IppmsEncoder;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;


public abstract class WebHelper {

    private static final String AJAX_REQUEST_HEADER = "ajax-request";

    private WebHelper() {}

    /**
     * Is this an AJAX based request? We check for a unique header value to determine.
     *
     * @param request
     * @return
     */
    public static boolean isAjaxRequest(final HttpServletRequest request) {
        return request.getHeader(AJAX_REQUEST_HEADER) != null ||
                "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    /**
     * Same as the method above but this time Spring MVC specific.
     *
     * @param request the generic Spring MVC {@link WebRequest}.
     * @return if this is an AJAX request
     */
    public static boolean isAjaxRequest(final WebRequest request) {
        return request.getHeader(AJAX_REQUEST_HEADER) != null ||
                "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    /**
     * Does the same thing as {@link #isNotAjaxRequest(HttpServletRequest)}
     * but is just Spring MVC specific
     *
     * @param request {@link WebRequest} request model
     * @return {@code true} if it is not and {@code false} otherwise
     */
    public static boolean isNotAjaxRequest(WebRequest request) {
        return !isAjaxRequest(request);
    }

    /**
     * Is this {@link HttpServletRequest} not an AJAX based request
     * <p>
     * Note: This method performs it's operation by checking for a
     * specific header in the request
     *
     * @param req The {@link HttpServletRequest} request
     * @return {@code true} if this request is not and {@code false} otherwise
     */
    public static boolean isNotAjaxRequest(HttpServletRequest req) {
        return !isAjaxRequest(req);
    }

    public static BusinessCertificate getBusinessCertificate(HttpServletRequest pRequest) throws Exception {
        BusinessCertificate businessCertificate = (BusinessCertificate) WebUtils.getSessionAttribute(pRequest, IppmsEncoder.getCertificateKey());
        return businessCertificate;
    }
}
