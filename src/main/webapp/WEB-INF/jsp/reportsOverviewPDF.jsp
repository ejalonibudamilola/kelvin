<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Reports  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<script type="text/javascript" src="scripts/jacs.js">
</script>
</head>

<body class="main">

	<table class="main" width="70%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
	<tr>
		<td colspan="2">
		<div class="title">
			PDF Reports Overview</div>
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
								<td colspan="50" class="infoTH">Employee Reports</td>
							</tr>
							
							<tr>
						<td class="infoTREven">
						<a href="${appContext}/bankScheduleSummary.do" title="Print Bank Schedule Summary">Bank Schedule Summary
						</a><br />
						Print Bank Schedule Summary. </td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/pdfReportForm.do" title="Global Deduction Report">Global Deduction Report
						</a><br />
						Global Deduction Report. </td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/deductionByAgency.do" title="Deduction By Agency Report">Deduction By Agency Report
						</a><br />
						Deduction By Agency Report. </td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/loanByAgency.do" title="Loan Schedule By Agency Report">Loan Schedule By Agency Report
						</a><br />
						Loan Schedule By Agency Report. </td>
					</tr>
					<c:choose>
					 <c:when test ="${roleBean.subeb}">
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/loanBYTSC.do" title="Loan Schedule By Schools">Print Loan Schedule By Schools
						</a><br />
						Print Loan Schedule By Schools. </td>
					</tr>
					</c:when>
					<c:otherwise>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/loanBYTSC.do" title="Loan Schedule By TSC">Print Loan Schedule By TSC
						</a><br />
						Print Loan Schedule By TSC. </td>
					</tr>

					</c:otherwise>
					</c:choose>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/globalLoan.do" title="Global Loan Report">Global Loan Report
						</a><br />
						Global Loan Report. </td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/acctGenLoan.do" title="Accountant General Loan Report">Accountant General Loan Report
						</a><br />
						Accountant General Loan Report. </td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/bankDetailed.do" title="Bank Schedule Detailed Report">Bank Schedule Detailed Report
						</a><br />
						Bank Schedule Detailed Report. </td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/payrollMDA.do" title="Payroll By Agency Report">Payroll By Agency Report
						</a><br />
						Payroll By Agency Report. </td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/payrollSchool.do" title="Payroll BySchool Report">Payroll By School Report
						</a><br />
						Payroll By School Report. </td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/payrollSchool.do" title="Payroll BySchool Report">Payroll By School Report
						</a><br />
						Payroll By School Report. </td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/IndEmpPayroll.do" title="Individual Staff Payroll Info">Individual Staff Payroll Info
						</a><br />
						Individual Staff Payroll Info. </td>
					</tr>	
							
						</table>
						</td>
					</tr>
					<tr>
						<td height="30">&nbsp;</td>
					</tr>
					
					
				</table>		
										
			</tr>
			
		</table>
		
	</table>
   </td>
   </tr>
       <tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
   </table>
   <script type="text/javascript">
   			function monthPrompt()
   			{
   	   			var month = prompt("Choose month!!","Sep");
   			}
   </script>
</body>

</html>
