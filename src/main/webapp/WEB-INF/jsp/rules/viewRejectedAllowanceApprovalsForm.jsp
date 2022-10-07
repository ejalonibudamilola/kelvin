<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            Rejected PayGroup Allowance Rules
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">

		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
    </head>
    <body class="main">

    <form:form modelAttribute="miniBean">
	<c:set value="${miniBean}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    Rejected <c:out value="${roleBean.staffTypeName}"/> PayGroup Allowance Rules
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                <div id="topOfPageBoxedErrorMessage" style="display:${miniBean.displayErrors}">
									<spring:hasBindErrors name="miniBean">
										<ul>
											<c:forEach var="errMsgObj" items="${errors.allErrors}">
											<li>
												<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
											</li>
											</c:forEach>
										</ul>
									</spring:hasBindErrors>
								</div>
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="90%">
                                    <tr>
                                        <td>

                                                &nbsp;
                                        </td>

                                    </tr>
                                    <tr>
                                        <td valign="top">
											<p class="label">Rejected <c:out value="${roleBean.staffTypeName}"/> PayGroup Allowance Rules</p>
											<display:table name="dispBean" class="register3" export="true" sort="page" defaultsort="1" requestURI="${appContext}/viewRejectedAllowanceRules.do">
											<display:setProperty name="export.excel.filename" value="${roleBean.staffTypeName}RejectedAllowanceRuleList.xls"/>
											<display:column property="allowanceRuleMaster.hireInfo.employee.employeeId" title="${roleBean.staffTitle}" media="html" href="${appContext}/allowanceRuleDetails.do" paramId="tid" paramProperty="id"></display:column>
											<display:column property="employee.employeeId" title="${roleBean.staffTitle}" media="excel"/>
											<display:column property="allowanceRuleMaster.hireInfo.employee.displayName" title="${roleBean.staffTypeName} Name"></display:column>
											<display:column property="allowanceRuleMaster.hireInfo.employee.salaryInfo.gradeLevelAndStep" title="Pay Group"></display:column>
											<display:column property="allowanceRuleMaster.mdaDeptMap.mdaInfo.name" title="${roleBean.mdaTitle}"></display:column>
										   <display:column property="approver.actualUserName" title="Rejected By"></display:column>

										    <display:column property="rejectionDateStr" title="Rejection Date (DD/MM/YYYY)"></display:column>

											<display:setProperty name="paging.banner.placement" value="bottom" />
										</display:table>
										</td>
                                    </tr>
                                    <tr>

                                    </tr>
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                        <input type="image" name="_cancel" value="cancel" title="Close" src="images/close.png">
                                        </td>
                                    </tr>
                                </table>

                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
            </tr>
        </table>
        </form:form>
    </body>
</html>
