<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title>Tax and Wage Summary</title>
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
			Tax and Wage Summary</div>
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
						</form:select></td>
						<td>&nbsp;</td>
						<td><input type="image" name="_go" value="go" alt="Go"
							class="" src="images/go_h.png">
					</tr>
				</table>
				<span class="reportTopPrintLink">
		<a href="${appContext}/taxWageSummaryPFV.do?pfv=yes&sDate=${taxWageBean.fromDateStr}&eDate=${taxWageBean.toDateStr}&pid=${taxWageBean.id}&cn=${taxWageBean.companyName}" target="_blank">
		Printer-Friendly Version </a>&nbsp;
		<a href="${appContext}/taxWageExcelSummary.do?sDate=${taxWageBean.fromDateStr}&eDate=${taxWageBean.toDateStr}&pid=${taxWageBean.id}&cn=${taxWageBean.companyName}">
		View in Excel </a><sup>1</sup> <br />
		 </span>
		<div class="reportDescText">
			This report shows	total subject wages, and taxable wages by Employee State. Click on the tax name to see subject 
			wages by employee. <br />
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
				<table class="report" cellspacing="0" cellpadding="0">
					<tr class="reportOdd header">
						<td class="tableCell" valign="top">State Income Tax</td>
						<td class="tableCell" align="center" valign="top">Total 
						Wages</td>
						<td class="tableCell" align="center" valign="top">Taxable 
						Wages</td>
						<td class="tableCell" align="center" valign="top">Tax Amount</td>
					</tr>
					<c:forEach items="${taxWageBean.wageBean}" var="wBean">
					<tr class="${wBean.displayStyle}">
						<td class="tableCell" valign="top">
						<a href="wageDetailReport.do?state=${wBean.state}&sDate=${taxWageBean.fromDateStr}&eDate=${taxWageBean.toDateStr}&pid=${taxWageBean.id}">
						<c:out value="${wBean.state}"/>&nbsp;Income Tax</a></td>
						<td class="tableCell" align="right" valign="top">₦<c:out value="${wBean.totalWagesStr}"/></td>
						<td class="tableCell" align="right" valign="top">₦<c:out value="${wBean.taxableWagesStr}"/></td>
						<td class="tableCell" align="right" valign="top">₦<c:out value="${wBean.taxAmountStr}"/></td>
					</tr>
					<tr class="reportOdd footer">
						<td class="tableCell" valign="top">Total</td>
						<td class="tableCell" align="right" colspan="4" valign="top">
						₦<c:out value="${wBean.totalStr}"/></td>
					</tr>
					<tr class="reportEven">
						<td class="blankCell" colspan="5" valign="top">&nbsp;</td>
					</tr>					
					</c:forEach>
					
					
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
		<a href="${appContext}/taxWageSummaryPFV.do?pfv=yes&sDate=${taxWageBean.fromDateStr}&eDate=${taxWageBean.toDateStr}&pid=${taxWageBean.id}&cn=${taxWageBean.companyName}" target="_blank">
		Printer-Friendly Version </a>&nbsp;
		<a href="${appContext}/taxWageExcelSummary.do?sDate=${taxWageBean.fromDateStr}&eDate=${taxWageBean.toDateStr}&pid=${taxWageBean.id}&cn=${taxWageBean.companyName}">
		View in Excel </a><sup>1</sup> <br />
			
		</div>
		
		
		<td valign="top" class="navSide"><br>
		<br>
		<!-- Here to put space beween this and any following divs --></td>
	</tr>
	</table>
</form:form>
</body>

</html>
