<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            <c:out value="${roleBean.staffTypeName}"/>s Approved/Rejected View
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<link rel="stylesheet" href="css/jquery-ui.css" type="text/css"/>
		<link rel="stylesheet" href="styles/notifications.css" type="text/css"/>
 		<script src="scripts/jquery-3.4.1.min.js"></script>
        		<script src="scripts/jquery-ui.js"></script>
		<script src="scripts/mouseover_popup.js"></script>

     </head>
      <style>
            #empAppr thead tr th{
               font-size:8pt !important;
            }
      </style>
    <body class="main">
     <div class="loader"></div>

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
                                    Pending <c:out value="${roleBean.staffTypeName}"/> Am Alive Approval/Rejection
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                <div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
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
											<p class="label"><c:out value="${roleBean.staffTypeName}"/>(s) Approval/Rejection View</p>
											<display:table name="dispBean" class="register4" export="false" sort="page" defaultsort="1" id = "empAppr" requestURI="${appContext}/viewApproveRejectAmAlive.do">

                                            <display:column property="employeeId" title="${roleBean.staffTitle}" >
											<display:column property="displayName" title="${roleBean.staffTypeName}"></display:column>
											<display:column property="mdaName" title="${roleBean.mdaTitle}"></display:column>
										   <display:column property="createdBy" title="Created By"></display:column>
										    <display:column property="initiatedDateStr" title="Creation Date"></display:column>
										    <display:column property="approvalStatusStr" title="Status"></display:column>
											<display:setProperty name="paging.banner.placement" value="bottom" />
										</display:table>
										</td>
                                    </tr>
                                    <tr style = "${miniBean.showRow}">
                                    <td>
                                    <table>
                                    <c:if test="${miniBean.addWarningIssued}">
                                    <tr align = "left">
                                    	<td align=right width="25%"><b>Approval Memo*</b></td>

									       <td width="75%" align = "left">
						                     <form:textarea path="approvalMemo" rows="5" cols="35" disabled="${saved}"/>
									       </td>
                                    </tr>
                                    </c:if>
                                    </table>
                                    </td>
                                    </tr>
                                    <tr>

                                    </tr>
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                     	<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/close.png">
                                     	<c:if test="${roleBean.superAdmin}">
                                     	  <c:choose>
                                     	   <c:when test="${not miniBean.addWarningIssued}">
	                                          <input type="image" name="_approve" value="approve" title="Approve Selected ${roleBean.staffTypeName}" class="" src="images/approve.png">
                                              <input type="image" name="_reject" value="reject" title="Reject Selected ${roleBean.staffTypeName}" class="" src="images/reject_h.png">
	                                       </c:when>
	                                       <c:otherwise>
                                            <input type="image" name="_confirm" value="confirm" title="Confirm to 'Approve All Selected ${roleBean.staffTypeName}'" class="" src="images/confirm_h.png">
	                                       </c:otherwise>
	                                       </c:choose>
	                                     </c:if>

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
