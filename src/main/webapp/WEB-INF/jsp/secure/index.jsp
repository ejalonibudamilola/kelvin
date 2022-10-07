<%--
Views should be stored under the WEB-INF folder so that
they are not accessible except through controllers.

This JSP is here to provide a redirect to the dispatcher
servlet but should be one of the few JSPs' outside of the WEB-INF folder.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% response.sendRedirect("loginForm.do"); %>

