<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title>Simulation Main Page </title>
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
		 Simulation Overview</div>
		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">
		<table width="95%">
			<tr>
				<td valign="top">
					<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
							<tr>
								<td colspan="50" class="infoTH">Simulator</td>
							</tr>
							
							<tr>
								<td class="infoTROdd">
									<a href="${appContext}/applyLTGToMABP.do">Apply/Simulate Leave Transport Grant 
								</a><br />
								Simulate/Apply Yearly Leave Transport Grant by Local Government Education Authority. </td>
							</tr>
							<tr>
								<td class="infoTREven">
									<a href="${appContext}/prePayrollSimulator.do">Simulate Payroll 
								</a><br />
								Simulate Payroll for budgeting and planning. </td>
							</tr>
							<tr>
								<td class="infoTREven">
									<a href="${appContext}/futuristicPayrollSimulator.do">Futuristic Payroll Run
								</a><br />
								Run Payroll Simulation for a specific time in the future.</td>
							</tr>
						</table>
				</td>
				<td width="5%">&nbsp;</td>
				<td width="45%" valign="top">
		
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
