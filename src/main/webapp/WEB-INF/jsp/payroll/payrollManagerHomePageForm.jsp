<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> ePayroll Manager Main Page  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">

<style type="text/css">
.style3 {
	text-decoration: underline;
	color: #0000FF;
}
</style>
<script type="text/javascript">
<!--
function popup(url,windowname) 
{
 var width  = 1000;
 var height = 800;
 var left   = (screen.width  - width)/2;
 var top    = (screen.height - height)/2;
 var params = 'width='+width+', height='+height;
 params += ', top='+top+', left='+left;
 params += ', directories=no';
 params += ', location=no';
 params += ', menubar=no';
 params += ', resizable=no';
 params += ', scrollbars=no';
 params += ', status=no';
 params += ', toolbar=no';
 newwin=window.open(url,windowname, params);
 if (window.focus) {newwin.focus()}
 return false;
}
// -->
</script>
</head>

<body class="main">
    <form:form modelAttribute="homePageBean">
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
	<tr>
		<td colspan="2">
		<div class="title">
			Home Page</div>
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
								<td colspan="50" class="infoTH">Ministry/Parastatal Overview</td>
							</tr>
							<tr>
								<td class="infoTROdd">
								<!--  
								<a href="${appContext}/ministryDetails.do">Ministries, Boards, Agencies, &amp;&nbsp;Parastatals List</a>&nbsp;<br />List of Ministries, Parastatals, Boards and Agencies <br /></td>
							    -->
							    <a href="${appContext}/mdapSummary.do">Ministries, Boards, Agencies, &amp;&nbsp;Parastatals List</a>&nbsp;<br />List of Ministries, Parastatals, Boards and Agencies <br /></td>
							</tr>
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/activeEmpOverview.do" target="_blank" onclick="popup(this.href, 'Employee Overview');return false;">Total Number of Active Employees</a>&nbsp;[<c:out value="${homePageBean.totalEmployee}"></c:out>]<br /><br /></td>
							</tr>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/inactiveEmpOverview.do" >Total Number of Inactive Employees</a>&nbsp;[<c:out value="${homePageBean.totalInactiveEmployee}"></c:out>]<br /><br /></td>
							</tr>
						    <tr>
								<td class="infoTREven">
								<a href="${appContext}/viewCreatedEmpForm.do">Employee(s) Hired this month</a>&nbsp;<br />View Employee(s) hired this month and (optionally) previous month(s)<br /></td>
							</tr>
						</table>
						</td>
					</tr>
					<tr>
						<td height="30">&nbsp;</td>
					</tr>
					
					<tr>
						<td height="30">&nbsp;</td>
					</tr>
					
					<tr>
						<td>
						<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
							<tr>
								<td colspan="50" class="infoTH">Run Payroll</td>
							</tr>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/paydayForm.do">Create Paychecks
								</a><br />
								Run Payroll for employees. </td>
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
						<td colspan="50" class="infoTH">Employee Functions</td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/searchEmpForEdit.do">View Employee</a><br />
						View an existing Employee. <br />
						</td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/viewEmpDueForRetirement.do">Retirement Tracker</a><br />
						View Employees due for retirement this Month.<br />
						</td>
					</tr>
                    
					<tr>
						<td class="infoTROdd">
						<a href="${appContext}/viewEmpDueForPromotion.do">Promotion Tracker</a><br />
						 View list of Employees due for promotion this Month. <br />
						</td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/searchEmpForPRC.do">Pay Record Card</a><br />
						 Generate Pay Record Card for an Employee. <br />
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
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
</table>
</form:form>
</body>

</html>
