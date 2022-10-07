<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title>Ogun State  I.P.P.M.S Login  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
</head>


<body class="main" onload="document.formLogin.uName.focus()">

	<table class="main" width="70%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		<tr>

			<td bgcolor = "#339900" align="center"><img src="images/clientLogo.png"></td>

		</tr>
		<tr>

			<td bgcolor = "#dedb6d"  align="center"><br>Welcome to Ogun State  IPPMS - Integrated Payroll &amp; Personnel Management System Login<br></td>

		</tr>
		<tr>
			<td>
				<table  border="0" cellspacing="0" cellpadding="0" align="center">
					<form:form modelAttribute="login" name="formLogin">
					<div id="topOfPageBoxedErrorMessage" style="display:${login.displayErrors}">
								 <spring:hasBindErrors name="login">
         							 <ul>
            							<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            							</c:forEach>
         								</ul>
     							 </spring:hasBindErrors>
					</div>
					<br/>
						<tr>
								<td align="right">Username: </td>
								<td><form:input path="username" id="uName" disabled="${login.chgPassword}" autocomplete="off"/></td>
							
						</tr>
						<tr>
							<c:choose>
							<c:when test="${login.chgPassword}">
							<td></td>
							</c:when>
							<c:otherwise>
								<td align="right">Password:</td>
								<td><form:password path="password" disabled="${login.locked}"/><c:if test="${login.notLocked}">&nbsp;<font color="red"><i>case sensitive</i></font></c:if></td>
							</c:otherwise>
							</c:choose>
							
							
						</tr>
						<tr id="hiderow" style="display:${login.showRow}">
							<td align="right">New Password:</td>
							<td><form:password path="newPassword" />&nbsp;<font color="red"><i>case sensitive</i></font></td>
						</tr>
						<tr id="hiderow2" style="display:${login.showRow}">
							<td align="right">Confirm Password:</td>
							<td><form:password path="confirmNewPassword" />&nbsp;<font color="red"><i>case sensitive</i></font></td>
						</tr>
						<br>
						<tr>
							<td>&nbsp;</td>
						</tr>
						<tr>
							
							<td></td>
							<c:choose>
							<c:when test="${login.chgPassword}">
								<td><input class="b" type="submit"  value="Continue"></td>	
							</c:when>
							<c:otherwise>
							<c:choose>
							 <c:when test="${login.locked}">
								<td><input class="b" type="submit"  value="Login" disabled="true"></td>
							 </c:when>
							 <c:otherwise>
							    <td><input class="b" type="submit"  value="Login"></td>
							 </c:otherwise>
							 </c:choose>	
							</c:otherwise>
							</c:choose>						
						</tr>
						<tr>
							<td></td>
						</tr>
					<p>
					
					</form:form>
				</table>
			  </td>
			  </tr>
				<tr>
				
					<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
				</tr>
		
	</table>
</body>
</html>