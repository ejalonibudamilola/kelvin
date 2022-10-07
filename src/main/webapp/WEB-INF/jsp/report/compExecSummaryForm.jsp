<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title>Comprehensive Executive Summary Report</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>

</head>

<body class="main">
<script type="text/javascript" src="scripts/jacs.js"></script>
<form:form modelAttribute="miniBean">
<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
	<tr>
		<td>
		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			
			<tr>
				<td colspan="2">
				<div class="title">
					Ogun State Government - <br>
					Comprehensive Executive Summary Report</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainBody" id="mainBody">
				
				<span class="reportTopPrintLink">
				<a href="${appContext}/compExecSummaryReportExcel.do?sDate=${miniBean.fromDateStr}&eDate=${miniBean.toDateStr}&ph=0">
				View in Excel </a>&nbsp;
				<a href="${appContext}/compExecSummaryReportByMDAExcel.do?sDate=${miniBean.fromDateStr}&eDate=${miniBean.toDateStr}&ph=0">
				
				View by MDA in Excel </a>&nbsp;<br />
				</span>
				<div class="reportDescText">
					This report gives a comprehensive executive summary of Payroll Run
					<br />
				</div>
				<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
					<tr>
						<td class="reportFormControls">
				 		<span class="optional"> from </span> 
						 <form:input path="fromDate"/>
				 			<img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('fromDate'),event);"><span class="optional">&nbsp;to </span> <form:input path="toDate"/>
							<img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('toDate'),event);">&nbsp;&nbsp;
			   		</td>
                 		
					</tr>
					
					<tr>
					
						<td class="reportFormControlsSpacing"></td>
					</tr>
					<tr>
						<td>
						<h3>Ogun State Comprehensive Executive Payroll Summary Report for <c:out value="${miniBean.monthAndYearStr}"/> </h3>
						<br />
						<table class="report" cellspacing="0" cellpadding="0">
							<tr class="reportOdd header">
								<td class="tableCell" align="center" valign="top">
								S/No.</td>
								<td class="tableCell" align="center" valign="top">
								MDA &amp; P's</td>
								<td class="tableCell" align="center" valign="top">
								 No. Of Items</td>		
								<td class="tableCell" align="center" valign="top">
								No. Of Staff</td>						
								<td class="tableCell" align="center" valign="top">
								Basic Salary</td>
								<td class="tableCell" align="center" valign="top">
								Total Allowance</td>
								<td class="tableCell" align="center" valign="top">
								Gross Amount</td>
								<td class="tableCell" align="center" valign="top">
								PAYE</td>
								<td class="tableCell" align="center" valign="top">
								Other Deductions</td>
								<td class="tableCell" align="center" valign="top">
								Total Deductions</td>
								<td class="tableCell" align="center" valign="top">
								Net Pay</td>								
							</tr>
							<c:forEach items="${miniBean.wageSummaryBeanList}" var="wBean" varStatus="gridRow">
							<tr class="${wBean.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${wBean.assignedToObject}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.noOfItems}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.noOfEmp}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.basicSalaryStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.totalAllowanceStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.grossAmountStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.payeStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.otherDeductionsStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.totalDeductionsStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.netPayStr}"/></td>
								
							</tr>
							</c:forEach>							
							<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">Totals</td>
								<td class="tableCell" align="right"><c:out value="${miniBean.totalNoOfItems}"/></td>
								<td class="tableCell" align="right"><c:out value="${miniBean.totalNoOfStaffs}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalBasicSalaryStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalAllowancesStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalGrossAmountStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalPayeStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalOtherDeductionsStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalDeductionsStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalNetPayStr}"/></td>
								
							</tr>
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
				<a href="${appContext}/compExecSummaryReportExcel.do?sDate=${miniBean.fromDateStr}&eDate=${miniBean.toDateStr}&ph=0">
				
				View in Excel </a>&nbsp;
				<a href="${appContext}/compExecSummaryReportByMDAExcel.do?sDate=${miniBean.fromDateStr}&eDate=${miniBean.toDateStr}&ph=0">
				
				View by MDA in Excel </a>&nbsp;
				<a href="${appContext}/cashBookForSalaries.do?sDate=${miniBean.fromDateStr}&eDate=${miniBean.toDateStr}&ph=0"> Generate Cashbook for Salaries</a>
				<br />
				</div>
				
				</td>
				<td valign="top" class="navSide"><br>
				<br>
				
				</td>
				</tr>
				</table>
				</td>

			</tr>
			<tr> <%@ include file="/WEB-INF/jsp/footerFile.jsp" %></tr>
			</table>
			</form:form>
		</body>

	</html>
	
