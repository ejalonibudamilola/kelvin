<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> HR Module Main Page  </title>
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
</head>

<body class="main">

	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
	<tr>
		<td colspan="2">
		<div class="title">
			HR Functionalities Overview</div>
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
								<a href="${appContext}/createDeptForm.do">Create Department</a><br/>
								Create a new Department<br/>
								</td>
							</tr>
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/selectEditDeptForm.do">Edit Department</a><br />
								Edit an existing Department<br />
								</td>
							</tr>
							<tr>
								<td class="infoTROdd">
									<a href="${appContext}/selectMPBAIForm.do">Assign Departments</a><br />
									Assign departments to M.D.A's<br />
								</td>
							</tr>
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/searchForEmpForm.do">Demotion/Pay Group Change</a><br />
								Demote or change the Pay Group of a/an <c:out value="${roleBean.staffTypeName}"/><br />
								</td>
							</tr>
							<!-- 
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/employeeDetailsReport.do"> </a><br />
								Details of each <c:out value="${roleBean.staffTypeName}"/> setup.<br />
								</td>
							</tr>
						   -->
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
								<td colspan="50" class="infoTH">Employee Functions</td>
							</tr>
							 
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/searchEmpForTermination.do">Terminate Employee</a><br />
								Terminate an existing Employee.<br />
								</td>
							</tr> 
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/searchForEmpToReinstate.do">Reinstate Employee</a><br />
								Reinstate a Terminated Employee.<br />
								</td>
							</tr>  
							<tr>
								<td class="infoTROdd">
									<a href="${appContext}/searchEmpForSuspension.do?reab=y">Reabsorb Employee</a><br />
										Reabsorb an Employee back on Payroll. <br />
								</td>
							</tr>      
							
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/searchForEmpForm.do">Demotion/Pay Group Change</a><br />
								Demote or change the Pay Group of an Employee<br />
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
						<td colspan="50" class="infoTH">MDA/School Functionalities</td>
					</tr>
					<tr>
						<td class="infoTROdd">
						<a href="${appContext}/selectPABI.do?mode=1">Create MDA</a><br />
						Create a new MDA. </td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/createSchoolForm.do">Create School</a><br />
						Create a new School. </td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/selectPABI.do">Edit MDA</a><br />
						Edit an existing MDA. </td>
					</tr>
					
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/editSchool.do">Edit School</a><br />
						Edit a School. </td>
					</tr>
					<!--<tr>
						<td class="infoTREven">
						<a href="${appContext}/createMinistry.do">New Ministry</a><br />
						Create a new Ministry.<br />
						</td>
					</tr>
					><tr>
						<td class="infoTROdd">
						<a href="${appContext}/selectMinistry.do">Edit Ministry</a><br />
						Edit an existing Ministry. <br />
						</td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/createParastatal.do">New Parastatal</a><br />
						Create a new Parastatal. <br />
						</td>
					</tr>
					<tr>
						<td class="infoTROdd">
						<a href="${appContext}/createBoard.do">New Board</a><br />
						Create a new Board. </td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/createAgency.do">New Agency</a><br />
						Create a new Agency. </td>
					</tr>
					-->
					
					
                   
				</table>
				</td>
				
			</tr>
			<tr>
			
			
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

</body>

</html>
