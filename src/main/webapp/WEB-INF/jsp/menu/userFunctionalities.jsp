<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title>User Functionalities Main Page </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">

<style type="text/css">
.style3 {
	text-decoration: underline;
	color: #0000FF;
}
</style>

</head>

<body class="main">
    <form:form modelAttribute="adminBean">
	<table class="main" width="74%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

	<%@ include file="/WEB-INF/jsp/headerFile.jsp"%>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
	<tr>
		<td colspan="2">
		<div class="title">
		 User Functionalities Overview</div>
		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">
		<table width="95%">
			<tr>
				<td valign="top">
				<table border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td width="45%" valign="top">
						<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
							<tr>
								<td colspan="50" class="infoTH">User Functionalities</td>
							</tr>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/createNewUser.do">Create New User</a><br />
								Create a New user for <c:out value="${roleBean.businessName}"/> I.P.P.M.S</td>
							</tr>
							
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/selectUserToDeactivate.do">Deactivate User</a><br />
								Deactivate an existing user of the <c:out value="${roleBean.businessName}"/> I.P.P.M.S <br /></td>
							</tr>
							
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/viewUsers.do">Edit Account</a><br />
								Edit User Account Details for <c:out value="${roleBean.businessName}"/> I.P.P.M.S<br />
								</td>
							</tr>
					
						</table>
						</td>
					</tr>
			
				</table>
				</td>
				<td width="5%">&nbsp;</td>
				<td width="45%" valign="top">
				<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
				<tr>
								<td colspan="50" class="infoTH">User Functionalities</td>
							</tr>
					<tr>
						<td class="infoTROdd">
						<a href="${appContext}/selectUserForPasswordReset.do">Reset Password</a><br />
								Reset an existing User Password for <c:out value="${roleBean.businessName}"/> I.P.P.M.S<br />
						</td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/selectLockedAccount.do">Unlock Account</a><br />
								Unlock a locked <c:out value="${roleBean.businessName}"/> I.P.P.M.S Account.<br />
						</td>
					</tr>
					
									
				</table>
				</td>
			</tr>
			
		</table>
		
	</table>
</td>
</tr>
<tr>				

			<%@ include file="/WEB-INF/jsp/footerFile.jsp"%>
</tr>
</table>
</form:form>
</body>

</html>
