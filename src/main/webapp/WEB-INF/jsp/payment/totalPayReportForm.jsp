<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title>Total Pay</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
</head>

<body class="main">
<script type="text/javascript" src="scripts/jacs.js"></script>
<form:form modelAttribute="totalPayBean">
<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
	<tr>
		<td>
		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			
	<tr>
		<td colspan="2">
		<div class="title">
			<c:out value="${totalPayBean.companyName}"/><br>
			Total Pay</div>
		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">
		<table>
				<tr>
					<td>
						<form:select path="subLinkSelect">
						<form:option value="taxWageSummaryForm">Tax & Wage Summary</form:option>
						<form:option value="retirementPlansReport">Retirement Plans</form:option>
						<form:option value="totalCostReportForm">Total Cost</form:option>
						</form:select> 
					</td>
					<td>&nbsp;</td>
					<td>
					<input type="image" name="_go" alt="Go" class="" src="images/go_h.png" >
					</tr>
				</table>
			
		
		
		<span class="reportTopPrintLink">
		<a href="${appContext}/totalPayReportPFV.do?sDate=${totalPayBean.fromDateStr}&eDate=${totalPayBean.toDateStr}&pid=${totalPayBean.id}" target="_blank">
		Printer-Friendly Version </a>&nbsp;
		<a href="${appContext}/totalPayExcel.do?excel=yes&sDate=${totalPayBean.fromDateStr}&eDate=${totalPayBean.toDateStr}&pid=${totalPayBean.id}">
		View in Excel </a><br />
		</span>
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
				<table class="report" cellspacing="0" cellpadding="0">
					<tr class="reportOdd header">
						<td class="tableCell" valign="top">Name</td>
						<td class="tableCell" valign="top">Employee ID</td>
						<td class="tableCell" valign="top">Salary</td>
						<td class="tableCell" valign="top">Total</td>
					</tr>
					<c:forEach items="${totalPayBean.payBean}" var="pBean">
					<tr class="${pBean.displayStyle}">
						<td class="tableCell" valign="top"><c:out value="${pBean.employeeName}"/></td>
						<td class="tableCell" align="right" valign="top"><c:out value="${pBean.employeeId}"/></td>
						<td class="tableCell" align="right" valign="top">₦<c:out value="${pBean.salaryStr}"/></td>
						<td class="tableCell" align="right" valign="top">₦<c:out value="${pBean.salaryStr}"/></td>
					</tr>
					</c:forEach>
					<tr class="reportEven footer">
					<td class="tableCell" valign="top">&nbsp;</td>
					<td class="tableCell" valign="top">Total</td>
					<td class="tableCell" align="right" valign="top">₦<c:out value="${totalPayBean.totalPayStr}"/></td>
					<td class="tableCell" align="right" valign="top">₦<c:out value="${totalPayBean.totalPayStr}"/></td>
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
			<a href="${appContext}/totalPayReportPFV.do?sDate=${totalPayBean.fromDateStr}&eDate=${totalPayBean.toDateStr}&pid=${totalPayBean.id}" target="_blank">
			Printer-Friendly Version </a>&nbsp;
			<a href="${appContext}/totalPayExcel.do?excel=yes&sDate=${totalPayBean.fromDateStr}&eDate=${totalPayBean.toDateStr}&pid=${totalPayBean.id}">
			View in Excel </a><br />
		</div>
		<br>
		
		</td>
		<td valign="top" class="navSide"><br>
		<br>
		<!-- Here to put space beween this and any following divs --></td>
	</tr>
	</table>
</form:form>
</body>

</html>
