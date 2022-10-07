<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Department Functionalities Main Page  </title>
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

	<table class="main" width="74%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">


<%@ include file="/WEB-INF/jsp/headerFile.jsp"%>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td colspan="2">
		<div class="title">
			Department Functionalities Overview</div>
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
								<td colspan="50" class="infoTH">Department Functionalities</td>
							</tr>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/createClientDeptForm.do">Create/Edit Department</a><br/>
								Create/Edit Department<br/>
								</td>
							</tr>
							<tr>
								<td class="infoTROdd">
									<a href="${appContext}/assignDeptToMin.do">Assign Departments</a><br />
									Assign departments to <c:out value="${roleBean.mdaTitle}"/>.<br />
								</td>
							</tr>
							
							<!-- 
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/employeeDetailsReport.do"> </a><br />
								Details of each employee's setup.<br />
								</td>
							</tr>
						   -->
						</table>
						</td>
					</tr>
				
					
				</table>
				</td>
			
				
			</tr>
		</table>
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
