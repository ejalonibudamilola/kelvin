<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Inactivate/Deactivate User Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>
</head>

<body class="main">
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
				  
					<tr>
						<td>
								<div class="title">Inactivate Existing User</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="epmUser">
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="epmUser">
         							 <ul>
            							<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            							</c:forEach>
         								</ul>
     							 </spring:hasBindErrors>
				</div>
				<table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
					<tr align="left">
						<td class="activeTH">User Information</td>
					</tr>
					<tr>
						<td class="activeTD" align="center">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align=right width="35%" nowrap>
									<span class="required"><b>User Name :</b></span></td>
									<td width="25%">
										&nbsp;<c:out value="${epmUser.userName}" />
									</td>
					            </tr>
					           	<tr>
									<td align=right nowrap><span class="required">
									<b>First Name :</b></span></td>
									<td width="25%">
										&nbsp;<c:out value="${epmUser.firstName}" />
									</td>
								</tr>
					            <tr>
					              <td align=right nowrap><span class="required"><b>Last Name :</b></span></td>
					              <td width="25%">
										&nbsp;<c:out value="${epmUser.lastName}" />
								   </td>
					              <td align="left"></td>
					            </tr>
					            <tr>
					              <td align=right width="35%" nowrap><span class="required"><b>Role :</b></span></td>
					              <td>   
					                &nbsp;<c:out value="${epmUser.role.name}" />
					              </td>
					            </tr>
					            </table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
						<c:choose>
							         	<c:when test="${saved}">
												<input type="image" name="_cancel" value="cancel" title="Close Window" class="" src="images/close.png">
							         	</c:when>
							         	<c:otherwise>
							         		<input type="image" name="submit" value="ok" title="Deactivate User ${epmUser.actualUserName}" class="" src="images/deactivate.png">&nbsp;
											<input type="image" name="_cancel" value="cancel" title="Cancel Deactivation" src="images/cancel_h.png">

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