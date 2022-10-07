<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> <c:out value="${roleBean.staffTypeName}"/> Step Increment Approval Form  </title>
<link rel="stylesheet" href=<c:url value="styles/omg.css"/> type="text/css" />
<link rel="Stylesheet" href=<c:url value="styles/skye.css"/> type="text/css" media ="screen">
<script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
<script src="<c:url value="/scripts/utility_script.js"/>"></script>
</head>

<body class="main">
	<table class="main" width="70%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

					<tr>
						<td colspan="2">

								<div class="title">Approve Salary Structure</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
			<form:form modelAttribute="miniBean">
			<c:set value="${approveBean}" var="dispBean" scope="request"/>
			<c:if test="${approveBean.hasErrors}">
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
				</c:if>
				<table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
					<tr align="left">
						<td class="activeTH"><b>Salary Structure information</b></td>
					</tr>
					<tr>
						<td class="activeTD">
							<table width="100%" border="0" cellspacing="0" cellpadding="2">

						            <td align="right" width="25%"><b>Salary Type :</b></td>

									     <td width="25%">
						                   <c:out value="${miniBean.salaryType.description}"/>
									     </td>

						            </tr>
						          <tr>
						             <td align="right" width="25%"><b>Created By :</b></td>

									      <td width="25%">
						                   <c:out value="${miniBean.initiator.actualUserName}"/>
									      </td>

						    		 <td>&nbsp;</td>
						          </tr>
						          <tr>
						            <td align="right" width="25%"><b>Created Date :</b></td>

									       <td width="25%">
						                     <c:out value="${miniBean.initiatedDateStr}"/>
									       </td>

						          </tr>
                            <c:if test="${!approveBean.showLink}">
						          	<tr>

						               <td align="right" width="25%"><b>Memo*:</b></td>

									       <td width="25%">
						                     <form:textarea path="approvalMemo" rows="5" cols="35" />
									       </td>

						            </tr>
						            </c:if>



							</table>
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right">
						<c:choose>
						<c:when test="${approveBean.showLink}">
						<input type="image" name="_cancel" value="cancel" title="Close" class="" src="images/close.png">
						</c:when>
						<c:otherwise>
						<input type="image" name="_confirm" value="confirm" title="Confirm" src="images/confirm_h.png">&nbsp;
						<input type="image" name="_cancel" value="cancel" title="Cancel operation" class="" src="images/cancel_h.png">
						</c:otherwise>
						</c:choose>
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
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
</body>
</html>
