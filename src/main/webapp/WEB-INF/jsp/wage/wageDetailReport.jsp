<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title>Tax and Wage Details</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>

</head>

<body class="main">
<script type="text/javascript" src="scripts/jacs.js"></script>
<form:form modelAttribute="taxWageBean">
<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
	<tr>
		<td>
		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			
			<tr>
				<td colspan="2">
				<div class="title">
					<c:out value="${taxWageBean.companyName}"/><br>
					Tax and Wage Details</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainBody" id="mainBody">
				
				<table>
					<tr>
						<td><form:select path="subLinkSelect">
							<form:option value="totalCostReportForm">Total Cost</form:option>
							<form:option value="retirementPlansReport">Retirement Plans</form:option>
							<form:option value="totalPayReportForm">Total Pay</form:option>
							<form:option value="taxWageSummaryForm">Tax & Wage Summary</form:option>
						</form:select></td>
						<td>&nbsp;</td>
						<td><input type="image" name="_go" value="go" alt="Go" class="" src="images/go_h.png">
					</tr>
				</table>
			
				<span class="reportTopPrintLink">
				<a href="${appContext}/wageDetailFormPFV.do?pfv=yes&sDate=${taxWageBean.fromDateStr}&eDate=${taxWageBean.toDateStr}&pid=${taxWageBean.id}&state=${taxWageBean.name}" target="_blank">
				Printer-Friendly Version </a>&nbsp;
				<a href="${appContext}/wageReportExcelSummary.do?sDate=${taxWageBean.fromDateStr}&eDate=${taxWageBean.toDateStr}&pid=${taxWageBean.id}&state=${taxWageBean.name}">
				View in Excel </a><sup>1</sup> <br />
				</span>
				<div class="reportDescText">
					Use this report to complete state quarterly wage reports by 
					employee. <br />
					<br />
				</div>
				<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
					<tr>
						<td class="reportFormControls">
				 		<span class="optional"> from </span> 
						 <form:input path="fromDate"/>
				 			<img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('fromDate'),event);"><span class="optional">&nbsp;to </span> <form:input path="toDate"/>
							<img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('toDate'),event);">&nbsp;&nbsp;
					   		&nbsp; 
                 		</td>
					</tr>
			
					<tr>
						<td class="reportFormControlsSpacing"></td>
					</tr>
					<tr>
						<td>
						<h3><c:out value="${taxWageBean.name}"/>&nbsp;Income Tax</h3>
						<br />
						<table class="report" cellspacing="0" cellpadding="0">
							<tr class="reportOdd header">
								<td class="tableCell" align="center" valign="top">
								Employee Name</td>
								<td class="tableCell" align="center" valign="top">
								Employee ID</td>
								<td class="tableCell" align="center" valign="top">
								Employee Address</td>
								<td class="tableCell" align="center" valign="top">
								Total Wages</td>
								<td class="tableCell" align="center" valign="top">
								Taxable Wages</td>
								<td class="tableCell" align="center" valign="top">
								Tax Amount</td>
							</tr>
							<c:forEach items="${taxWageBean.wageBean}" var="wBean">
							<tr class="${wBean.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${wBean.name}"/></td>
								<td class="tableCell" align="center" valign="top"><c:out value="${wBean.employeeId}"/></td>
								<td class="tableCell" valign="top"><c:out value="${wBean.address}"/></td>
								<td class="tableCell" align="right" valign="top">₦<c:out value="${wBean.totalWagesStr}"/></td>
								<td class="tableCell" align="right" valign="top">₦<c:out value="${wBean.taxableWages}"/></td>
								<td class="tableCell" align="right" valign="top">₦<c:out value="${wBean.taxAmountStr}"/></td>
							</tr>
							</c:forEach>							
							<tr class="reportEven footer">
								<td class="tableCell" colspan="3" valign="top">Totals</td>
								<td class="tableCell" align="right" valign="top">₦<c:out value="${taxWageBean.totalWagesStr}"/></td>
								<td class="tableCell" align="right" valign="top">₦<c:out value="${taxWageBean.totalTaxableWagesStr}"/></td>
								<td class="tableCell" align="right" valign="top">₦<c:out value="${taxWageBean.totalTaxAmountStr}"/></td>
								
							</tr>
						</table>
						</td>
					</tr>
					 <tr>
                                     	<td class="buttonRow" align="right">
                                        <input type="image" name="_updateReport" value="updateReport" alt="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                        </td>
                                    </tr>
				</table>
				<div class="reportBottomPrintLink">
					<a href="${appContext}/wageDetailFormPFV.do?pfv=yes&sDate=${taxWageBean.fromDateStr}&eDate=${taxWageBean.toDateStr}&pid=${taxWageBean.id}&state=${taxWageBean.name}" target="_blank">
				Printer-Friendly Version </a>&nbsp;
				<a href="${appContext}/wageReportExcelSummary.do?sDate=${taxWageBean.fromDateStr}&eDate=${taxWageBean.toDateStr}&pid=${taxWageBean.id}&state=${taxWageBean.name}">
				View in Excel </a><sup>1</sup> <br />
				</div>
				<br />
				<a href="${appContext}/taxWageSummaryForm.do?sDate=${taxWageBean.fromDateStr}&eDate=${taxWageBean.toDateStr}&pid=${taxWageBean.id}">
				Return to main Tax and Wage Summary </a><br />
				<br />
				</td>
				<td valign="top" class="navSide"><br>
				<br>
				<!-- Here to put space beween this and any following divs -->
				</td>
			</tr>
			</table>
			</form:form>
		</body>

	</html>
	
