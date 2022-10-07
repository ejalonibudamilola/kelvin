<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>

<html>
    <head>
        <title><c:out value="${pageTitle}" /></title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css" />
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">

        <script src="<c:url value="/scripts/jquery-1.8.3.min.js"/>"></script>
        <script src="<c:url value="/scripts/utility_script.js"/>"></script>
    </head>

    <body class="main">
        <table class="main" width="74%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

            <%@ include file="/WEB-INF/jsp/headerFile.jsp"%>

            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

                        <tr>
                            <td>
                                <div class="title"><c:out value="${mainHeader}"/></div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                <span class="required-asterik">*</span> = required<br /><br />

                                <form:form modelAttribute="menuLinkCategoryBean">

                                    <spring:hasBindErrors name="menuLinkCategoryBean">
                                        <div id="topOfPageBoxedErrorMessage" style="display: block;">
                                            <ul>
                                                <c:forEach var="errMsgObj" items="${errors.allErrors}">
                                                    <li><spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}" /></li>
                                                    </c:forEach>
                                            </ul>
                                        </div>
                                    </spring:hasBindErrors>

                                    <table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left">
                                        <tr>
                                            <td class="activeTH alignLeft"><c:out value="${mainHeader}"/></td>
                                        </tr>
                                        <tr>
                                            <td class="activeTD">
                                                <table class="hundredPercentWidth" border="0" cellspacing="0" cellpadding="2">
                                                    <tr>
                                                        <td class="thirtyPercentWidth alignRight verticalAlignTop">
                                                            <span class="form-label"><spring:message code="menuLinkCat.form.label.name"/></span>
                                                            <span class="required-asterik">*</span>
                                                        </td>
                                                        <td class="seventyPercentWidth alignLeft verticalAlignTop">
                                                            <form:input path="name" size="40"/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight verticalAlignTop">
                                                            <span class="form-label"><spring:message code="menuLinkCat.form.label.desc"/></span>
                                                            <span class="required-asterik">*</span>
                                                        </td>
                                                        <td class="alignLeft verticalAlignTop">
                                                            <form:textarea path="description" size="150" cols="40" rows="3"/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight verticalAlignTop">
                                                            <span class="form-label"><spring:message code="menuLinkCat.form.label.useWithDashboardTabsOnly"/></span>
                                                            
                                                        </td>
                                                        <td class="alignLeft verticalAlignTop">
                                                            <form:checkbox path="displayOnlyOnDbTabsBind"/>
                                                            <!--   <br/>
                                                            <span class="spacerTwentyPix"></span>-->
                                                            <span class="formInputDescText">
                                                                <spring:message code="menuLinkCat.form.label.useWithDashboardTabsOnly.desc"/>
                                                            </span>
                                                        </td>
                                                    </tr>
                                                </table>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="formButtonRow alignLeft">
                                                <input type="submit" name="_save" value="<spring:message code="form.button.save"/>" class="form-btn"/>
                                                <input type="submit" name="_cancel" value="<spring:message code="form.button.cancel"/>" class="form-btn"/>
                                            </td>
                                        </tr>
                                    </table>
                                </form:form>
                            </td>
                        </tr>

                    </table>
                </td>
            </tr>
            <tr>
                <%@ include file="/WEB-INF/jsp/footerFile.jsp"%>
            </tr>
        </table>

    </body>
</html>