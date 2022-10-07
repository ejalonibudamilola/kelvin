<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> ePayroll Manager Audit Page  </title>
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
<form:form>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
	<tr>
		<td colspan="2">
		<div class="title">
			Audit Functionalities Overview</div>
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
								<td colspan="50" class="infoTH"><c:out value="${roleBean.staffTypeName}"/> Related Logs</td>
							</tr>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/viewEmployeeLog.do"><c:out value="${roleBean.staffTypeName}"/> Changes</a><br/>
								View changes made to <c:out value="${roleBean.staffTypeName}"/>(s) by a User<br/>
								</td>
							</tr>
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/viewHireInfoLog.do">Hiring Information Changes</a><br />
								View changes made to <c:out value="${roleBean.staffTypeName}"/>(s) hiring information by a User<br />
								</td>
							</tr>
							<tr>
								<td class="infoTROdd">
									<a href="${appContext}/viewPromotionLog.do">Manual Promotion Log</a><br />
									View <c:out value="${roleBean.staffTypeName}"/> promoted by a User.<br />
								</td>
							</tr>
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/viewReassignLog.do">Demotion/Pay Group Change Log</a><br />
								View Demotion/Pay Group Changes by a User<br />
								</td>
							</tr>
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/viewEmpReinstatementsLog.do">Reinstatement Log</a><br />
								View Reinstatement logs for a User<br />
								</td>
							</tr>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/viewTransferLog.do">Transfer Log</a><br />
								View Transfer logs for a User<br />
								</td>
							</tr>
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/viewGarnishLog.do"> Loan Log</a><br />
								View changes made to <c:out value="${roleBean.staffTypeName}"/> Loans.<br />
								</td>
							</tr>
							
						</table>
						</td>
					</tr>
					<tr>
						<td height="30">&nbsp;</td>
					</tr><!--
					
					<tr>
						<td height="30">&nbsp;</td>
					</tr>
					--><tr>
						<td>
						<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
							<tr>
								<td colspan="50" class="infoTH">Bank &amp; Account Logs</td>
							</tr>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/viewBankLog.do">Bank Logs
								</a><br />
								View Changes made to <c:out value="${roleBean.staffTypeName}"/> Bank information by a User. </td>
							</tr>
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/viewUserAccountLogs.do">Account Logs
								</a><br />
								View User Accounts audit trail. </td>
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
						<td colspan="50" class="infoTH">Deduction &amp; Allowance Logs</td>
					</tr>
					
					<tr>
								<td class="infoTREven">
								<a href="${appContext}/viewDeductionLog.do"> Deduction Log</a><br />
								View changes made to <c:out value="${roleBean.staffTypeName}"/> Deductions.<br />
								</td>
				    </tr>
					
					<tr>
								<td class="infoTROdd">
								<a href="${appContext}/viewSpecialAllowanceLog.do"> Special Allowance Log</a><br />
								View changes made to Special Allowances.<br />
								</td>
				    </tr>
					
				    <tr>
								<td>
								&nbsp;<br /><br />
								</td>
					</tr>
							
					<tr>
								<td>
								&nbsp;<br /><br />
								</td>
					</tr>
							
					<tr>
						<td height="30">&nbsp;</td>
					</tr>
					<tr>
						<td height="42">&nbsp;</td>
					</tr>
					<tr>
						<td>
						<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
							<tr>
								<td colspan="50" class="infoTH">Other Logs</td>
							</tr>
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/viewPendingConflicts.do?al=t"><c:out value="${roleBean.staffTypeName}"/> Name Conflict Logs</a><br />
								View <c:out value="${roleBean.staffTypeName}"/> with name conflicts during creation.<br />
								</td>
				   			 </tr>
							<tr>
								<td class="infoTREven">
									<a href="${appContext}/viewSubventionLog.do"> Subvention Log</a><br />
										View changes made to Subventions.<br />
								</td>
				    		</tr>
							
						</table>
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
