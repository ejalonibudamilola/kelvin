<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title>Retirement Plans</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
</head>

<body class="main">
<script type="text/javascript" src="scripts/jacs.js"></script>
<form:form modelAttribute="retPlanBean">
<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
	<tr>
		<td>
		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			
			<tr>
				<td colspan="2">
				<div class="title">
					<c:out value="${retPlanBean.companyName}"/><br>
					Retirement Plans</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainBody" id="mainBody">
				
					<table>
					<tr>
					<td>
						<form:select path="subLinkSelect">
						<form:option value="taxWageSummaryForm">Tax & Wage Summary</form:option>
						<form:option value="totalPayReportForm">Total Pay</form:option>
						<form:option value="totalCostReportForm">Total Cost</form:option>
						</form:select> 
					</td>
					<td>&nbsp;</td>
					<td>
					<input type="image" name="_go" value="go" title="Go" class="" src="images/go_h.png" >
				</tr>
				</table>
					
				<span class="reportTopPrintLink">
				<a href="${appContext}/retirementPlansReportPFV.do?sDate=${retPlanBean.fromDateStr}&eDate=${retPlanBean.toDateStr}&pid=${retPlanBean.id}" target="_blank">
				Printer-Friendly Version </a>&nbsp;
				<a href="${appContext}/retirementPlansExcel.do?sDate=${retPlanBean.fromDateStr}&eDate=${retPlanBean.toDateStr}&pid=${retPlanBean.id}">
				View in Excel </a><sup>1</sup> <br />
				</span>
				<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
					<tr>
						<td class="reportFormControls">
				 		<span class="optional"> from </span> 
						 <form:input path="fromDate"/>
				 			<img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('fromDate'),event);"><span class="optional">&nbsp;to </span> <form:input path="toDate"/>
							<img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('toDate'),event);">&nbsp;&nbsp;
					   		&nbsp; 
                 		</td>
					</tr>
					<tr>
						<td class="reportFormControlsSpacing"></td>
					</tr>
					<tr>
						<td>
						<table class="report" cellspacing="0" cellpadding="0">
						<c:forEach items="${retPlanBean.retMainBean}" var="rMB">
							<tr class="reportOdd header">
								<td class="tableCell" colspan="4" valign="top"><c:out value="${rMB.name}"/></td>
							</tr>
							<tr class="reportEven header">
								<td class="tableCell indent" valign="top">Employee</td>
								<td class="tableCell" valign="top">Employee<br>
								Deduction</td>
								<td class="tableCell" valign="top">Company<br>
								Contribution</td>
								<td class="tableCell" valign="top">Plan Total</td>
							</tr>
							<c:choose>
							<c:when test="${rMB.hasValues}">
							<c:forEach items="${rMB.retMiniBean}" var="rMiniBean">
							<tr class="${rMiniBean.displayStyle}">
								<td class="tableCell indent" valign="top"><c:out value="${rMiniBean.employeeName}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${rMiniBean.empDedStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${rMiniBean.compContStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${rMiniBean.planTotalStr}"/></td>
							</tr>
							</c:forEach>
							<tr class="reportOdd footer">
								<td class="tableCell indent" valign="top">Total</td>
								<td class="tableCell" align="right" valign="top"><c:out value="${rMB.empDedTotalStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${rMB.compContTotalStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${rMB.planTotalStr}"/></td>
							</tr>
							<tr class="reportEven">
								<td class="blankCell" colspan="4" valign="top">&nbsp;</td>
							</tr>
							</c:when>
							<c:otherwise>
							<tr class="reportEven">
								<td class="tableCell indent" colspan="4" valign="top">
								This retirement plan was not used in the date range 
								specified.</td>
							</tr>
							<tr class="reportOdd">
								<td class="blankCell" colspan="4" valign="top">&nbsp;</td>
							</tr>
							</c:otherwise>
							</c:choose>
							</c:forEach>
							
						</table>
						</td>
					</tr>
					 <tr>
                                     	<td class="buttonRow" align="right">
                                        <input type="image" name="_updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                        </td>
                                    </tr>
				</table>
				<div class="reportBottomPrintLink">
					<a href="${appContext}/retirementPlansReportPFV.do?sDate=${retPlanBean.fromDateStr}&eDate=${retPlanBean.toDateStr}&pid=${retPlanBean.id}" target="_blank">
					Printer-Friendly Version </a>&nbsp;
					<a href="${appContext}/retirementPlansExcel.do?sDate=${retPlanBean.fromDateStr}&eDate=${retPlanBean.toDateStr}&pid=${retPlanBean.id}">
					View in Excel </a><sup>1</sup> <br />
					</div>
				<br>
				
				</td>
				<td valign="top" class="navSide"><br>
				<br>
				<!-- Here to put space beween this and any following divs -->
				</td>
			</tr>
			</table>
		</td>
		</tr>
		</table>
		</form:form>
		</body>

	</html>
	