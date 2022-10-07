<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title>Mass Entry &amp; File Upload Dashboard  </title>
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
    <form:form modelAttribute="adminBean">
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
	<tr>
		<td colspan="2">
		<div class="title">
			Mass Entry &amp; File Upload Dashboard</div>
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
								<td colspan="50" class="infoTH">Mass Entry</td>
							</tr>
							 <tr>
								<td class="infoTREven">
								<a href="${appContext}/massDeductionEntry.do">Deductions</a><br />
								 Mass Assign Deductions to <c:out value="${roleBean.staffTypeName}"/><br />
								</td>
							</tr>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/massLoanEntry.do">Loans/Garnishments</a><br />
								Mass Assign Loans to <c:out value="${roleBean.staffTypeName}"/><br />
								</td>
							</tr>
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/massPromotions.do">Promotions</a><br />
								 Mass Promote <c:out value="${roleBean.staffTypeName}"/> to the same Pay Group, Level &amp; Step<br />
								</td>
							</tr>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/massSpecialAllowance.do">Special Allowances</a><br />
								Mass Assign Special Allowances to <c:out value="${roleBean.staffTypeName}"/><br />
								</td>
							</tr>
		                  
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/massTransfer.do">Transfer</a><br />
								 Mass Assign <c:out value="${roleBean.mdaTitle}"/> to <c:out value="${roleBean.staffTypeName}"/> <br />
								</td>
							</tr>
							
						</table>
						</td>
					</tr>
					<tr>
						<td>
						<!--<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
							<tr>
								<td colspan="50" class="infoTH">Promotions</td>
							</tr>
							<tr>
								<td class="infoTROdd">
									<a href="${appContext}/searchEmpForPromotion.do">Promote <c:out value="${roleBean.staffTypeName}"/></a><br />
									Promote an existing <c:out value="${roleBean.staffTypeName}"/>. <br />
									</td>
							</tr>
							<tr>
								<td class="infoTROdd">
									<a href="${appContext}/searchEmpForTransfer.do">Transfer <c:out value="${roleBean.staffTypeName}"/></a><br />
									Transfer an existing <c:out value="${roleBean.staffTypeName}"/> between <c:out value="${roleBean.mdaTitle}"/> or Schools. <br />
									</td>
							</tr>
							
						</table>
						-->&nbsp;
						</td>
					</tr>
					
				</table>
				</td>
				<td width="5%">&nbsp;</td>
				<td width="45%" valign="top">
				<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
					<tr>
						<td colspan="50" class="infoTH">File Upload</td>
					</tr>
					<tr>
						<td class="infoTROdd">
							<a href="${appContext}/upload.do?ot=${roleBean.deductionObjectType}" title="Upload Deductions for ${roleBean.staffTypeName}">Upload Deductions
								</a><br />
								Upload Deductions for <c:out value="${roleBean.staffTypeName}"/> using Microsoft&copy;&nbsp;Excel (.xls only).<br />
						</td>
					</tr>
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/upload.do?ot=${roleBean.loanObjectType}" title="Upload Loans for ${roleBean.staffTypeName}">Upload Loans/Garnishments
								</a><br />
								Upload Loans/Garnishments for <c:out value="${roleBean.staffTypeName}"/> using Microsoft&copy;&nbsp;Excel (.xls only).<br />
								</td>
							</tr>
							<c:if test="${roleBean.superAdmin}">
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/upload.do?ot=${roleBean.specAllowObjectType}" title="Upload ${roleBean.staffTypeName} Special Allowances">Upload Special Allowances  </a><br />
								Upload Special Allowances for <c:out value="${roleBean.staffTypeName}"/> using Microsoft&copy;&nbsp;Excel (.xls only).<br />
								</td>
							</tr>	
							

							</c:if>

							
					<tr>
						<td>
						<!--<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
							<tr>
								<td colspan="50" class="infoTH"><c:out value="${roleBean.staffTypeName}"/> Reports</td>
							</tr>
							
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/employeeSalaryDiff.do" title="View Overpayments & Underpayments"><c:out value="${roleBean.staffTypeName}"/>
								Overpayments/Underpayments </a><br />
								View Overpayments and Underpayments by dates.<br />
								</td>
							</tr>
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/viewContractEmployees.do" title="View Contract Employees">Contracted <c:out value="${roleBean.staffTypeName}"/>s
								</a><br />
								View all <c:out value="${roleBean.staffTypeName}"/>s that have been on contract.<br />
								</td>
							</tr>
							
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/viewSuspendedEmployees.do" title="View Employees on Suspension.">Suspended <c:out value="${roleBean.staffTypeName}"/>s </a>
								<br />
								View all <c:out value="${roleBean.staffTypeName}"/>s on Suspension or With Salary Stopped (Temporarily). <br />
							</td>
							</tr>
						</table>
						-->&nbsp;</td>
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
