<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> <c:out value="${roleBean.staffTypeName}"/> Related Reports Page  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
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
			<c:out value="${roleBean.staffTypeName}"/> Related Reports</div>
		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">
		<table width="95%">
			<tr>

				<td valign="top">
				
				
				<table border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td width="80%" valign="top">
						
							<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
												<tr>
								<td colspan="50" class="infoTH"><c:out value="${roleBean.staffTypeName}"/> Related Reports</td>
							</tr>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/viewSalaryStructure.do"><c:out value="${roleBean.staffTypeName}"/> By Salary Scale</a><br/>
								View <c:out value="${roleBean.staffTypeName}"/> on Pay Group<br/>
								</td>
							</tr>
							
							<tr>
								<td class="infoTREven">
									<a href="${appContext}/empByLgaForm.do"><c:out value="${roleBean.staffTypeName}"/> By Local Govt. Area</a><br />
									View <c:out value="${roleBean.staffTypeName}"/> by their Local Government Areas.<br />
								</td>
							</tr>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/empByRel.do"><c:out value="${roleBean.staffTypeName}"/> By Religion</a><br />
								View <c:out value="${roleBean.staffTypeName}"/> By Religion<br />
								</td>
							</tr>
							
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/viewPayGroupEmployees.do"><c:out value="${roleBean.staffTypeName}"/> By Designation</a><br/>
								View <c:out value="${roleBean.staffTypeName}"/> by Designation<br/>
								</td>
							</tr>
							
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/viewEmployeeRemunerationReport.do"><c:out value="${roleBean.staffTypeName}"/> General Information Report</a><br/>
								View <c:out value="${roleBean.staffTypeName}"/> General Information Report<br/>
								</td>
							</tr>
					
						</table>
						
						</td></tr></table>
			
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
