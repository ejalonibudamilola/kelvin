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
<script type="text/javascript" src="scripts/jacs.js"></script>
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
			Reports Overview</div>
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
								<td class="infoTROdd">
								<a href="${appContext}/employeeSalaryDiff.do" title="View Overpayments & Underpayments">Employee 
								Overpayments/Underpayments </a><br />
								View Overpayments and Underpayments by dates.<br />
								</td>
							</tr>
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/viewContractEmployees.do" title="View Contract Employees">Contracted Employees
								</a><br />
								View all Employees that have been on contract.<br />
								</td>
							</tr>
							
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/viewSuspendedEmployees.do" title="View Employees on Suspension.">Suspended Employees </a>
								<br />
								View all Employees on Suspension or With Salary Stopped (Temporarily). <br />
							</td>
							</tr>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/allOtherDeductionsReport.do" title="View Deductions Details">Deductions
								</a><br />
								Deduction summary and details.<br />
								</td>
							</tr>
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/allOtherGarnishmentReport.do" title="View Employee Loans">Employee 
								Loans </a><br />
								Details of each employee's Loans.<br />
								</td>
							</tr>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/allOtherSpecialAllowanceReport.do" title="View Employee Special Allowances">Special Allowances  </a><br />
								Details of each employee paid a Special Allowance.<br />
								</td>
							</tr>
							
							 <tr>
								<td class="infoTREven">
								<a href="${appContext}/viewCreatedEmpForm.do">Employee(s) Hired this month</a>&nbsp;<br />View Employee(s) hired this month and (optionally) previous month(s)<br /></td>
							</tr>
							
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/activeEmpOverview.do" target="_blank" onclick="popup(this.href, 'Employee Overview');return false;">Total Number of Active Employees</a>&nbsp;[<c:out value="${homePageBean.totalEmployee}"></c:out>]<br /><br /></td>
							</tr>
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/inactiveEmpOverview.do" >Total Number of Inactive Employees</a>&nbsp;[<c:out value="${homePageBean.totalInactiveEmployee}"></c:out>]<br /><br /></td>
							</tr>
							 <tr>
						<td class="infoTROdd">
							    <a href="${appContext}/taxSummaryYTD.do" title="View & Print Tax YTD Summary Values ">Tax YTD Summary Report</a>&nbsp;<br />
							    View &amp; Print Tax YTD Summary<br /></td>
				    </tr>
						 <tr>
						<td class="infoTREven">
								
							    <a href="${appContext}/taxReport.do" title="View & Print Tax Deductions by Agency">Tax Deduction Report</a>&nbsp;<br />
							    View &amp; Print Tax Deductions by Agency<br /></td>
				    </tr>  
						</table>
						</td>
					</tr>
					<tr>
						<td height="30">&nbsp;</td>
					</tr>
					<tr>
						<td>
						<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
							<tr>
								<td colspan="50" class="infoTH">Paychecks and Pay 
								Stubs</td>
							</tr>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/selectPayslipMode.do" title="Print Paystubs">Check Register
								</a><br />
								Print or export checks, send pay stub emails to 
								employees. </td>
							</tr>
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/preLastPaycheckForm.do?pf=t" title="Print Pay Slip">Single Employee Pay Slip
								</a><br />
								Print an Employees Pay Slip</td>
							</tr>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/searchPaySlipHistory.do" title="View Employee Pay Slip History">Pay Slip History
								</a><br />
								View an Employee's Pay Slip History</td>
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
						<td colspan="50" class="infoTH">More Reports</td>
					</tr>
					<tr>
								<td class="infoTROdd">
								<a href="${appContext}/paystubsReportForm.do" title="View Summary for a Payroll Run">Payroll Summary
								</a><br />
								Summary information by paycheck.<br />
								</td>
							</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/viewWageSummary.do" title="Generate Payroll Executive Report">Payroll Summary by MDA
						</a><br />
						Payroll Executive Summary by MDA<br />
						</td>
					</tr>
					<tr>
						<td class="infoTROdd">
						<a href="${appContext}/wageCompExecSummary.do" title="Generate Comprehensive Executive Summary Report.">Comprehensive Executive Summary </a>
						<br />
						View comprehensive executive summary reports <br />
						</td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/enterVariableForCashBook.do" title="Generate Cashbook for salaries Report.">Generate Cashbook For Salaries</a>
						<br />
						Generate Cashbook for Salaries <br />
						</td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/mdaPayrollAnalysisExcel.do" title="Generate Detailed Payroll Analysis by MDA.">Detailed Payroll Analysis by MDA</a>
						<br />
						Generate Detailed Payroll Analysis by MDA <br />
						</td>
					</tr>
					<tr>
						<td class="infoTROdd">
						<a href="${appContext}/viewActiveSubventions.do" title="View all active Subventions currently affecting payroll.">Active Subventions </a>
						<br />
						View all active Subventions currently affecting payroll. <br />
						</td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/viewInactiveSubventions.do" title="View Subventions that have been inactivated">Inactive Subventions
						</a><br />
						View Subventions that have been inactivated. </td>
					</tr>
					<!--<tr>
						<td class="infoTROdd">
						<a href="${appContext}/variationReport.do" title="View Material changes (Variation Report) Carried Out This Month">Variation Report
						</a><br />
						View Variation Reports. </td>
					</tr>
					
					--><tr>
						<td class="infoTROdd">
						<a href="${appContext}/payDifference.do" title="Generate End Of Month Analysis for differences between two months pay">Monthly Reconcilation Report
						</a><br />
						Analyse Gross Pay Difference by month. </td>
					</tr>
					
					<tr>
						<td class="infoTROdd">
								
							    <a href="${appContext}/mdapSummary.do" title="MDA Summary">Ministries, Boards, Agencies, &amp;&nbsp;Parastatals List</a>&nbsp;<br />
							    List of Ministries, Parastatals, Boards and Agencies <br /></td>
						</tr>
					<tr>
						<td class="infoTROdd">
								
							    <a href="${appContext}/pensionReport.do" title="View & Print Contributory Pensions by Agency">Contributory Pension Report</a>&nbsp;<br />
							    View &amp; Print Contributory Pensions by Agency<br /></td>
				    </tr>
				    <tr>
								<td class="infoTREven">
								<a href="${appContext}/tpsReport.do" title="TPS Employees Report">TPS/CPS Employees</a><br />
						 			View Employees on the Transitional Pension Scheme. <br />
						 		</td>
							</tr>
				    
					
					<tr>
						<td height="30">&nbsp;</td>
					</tr>
					
					<tr>
						<td>
						<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
							<tr>
								<td colspan="50" class="infoTH">Other Reports</td>
							</tr>
							<c:if test="${adminBean.leaveBonusModuleOpen}">
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/preLeaveBonusReportForm.do" title="View & Generate Leave Bonus Reports">Leave Bonus Reports
								</a><br />
								View/Print Leave Bonuses reports </td>
							</tr>
							</c:if>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/monthlyVariation.do" title="Monthly Variation Report">Monthly Variation Report
								</a><br />
								View/Print Monthly Variation Report By MDAs </td>
							</tr>
							
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/prePayrollSimReportForm.do" title="View Payroll Simulation Results">Payroll Simulation Reports
								</a><br />
								View/Print Results of actual Payroll Simulations </td>
							</tr>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/preFutureSimReportForm.do" title="View Futuristic Simulation Results">Futuristic Payroll Simulation Reports
								</a><br />
								View/Print Results of Futuristic Payroll Simulations </td>
							</tr>
						</table>
						</td>						
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
   
</body>

</html>
