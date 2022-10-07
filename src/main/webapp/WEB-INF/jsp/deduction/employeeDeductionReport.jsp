<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title><c:out value="${roleBean.staffTypeName}"/> Deductions</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
</head>

<body class="main">
<form:form modelAttribute="empDed">
<script type="text/javascript" src="scripts/jacs.js"></script>
<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
	<tr>
		<td>
		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			
			<tr>
				<td colspan="2">
				<div class="title">
					<c:out value="${empDed.companyName}"/><br>
					Employee Deductions for &nbsp;<c:out value="${empDed.employeeName}"/></div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainBody" id="mainBody">
				
				<table>
				<tr>
					<td>
						<form:select path="subLinkSelect">
						<form:option value="paystubsReportForm">Payroll Summary</form:option>
						<form:option value="allDeductionsReport">Deductions</form:option>
						<form:option value="preLastPaycheckForm">Last Paycheck</form:option>
						</form:select> 
					</td>
					<td>&nbsp;</td>
					<td>
					<input type="image" name="_go" value="go" title="Go" class="" src="images/go_h.png" />
					</td>
				</tr>
			</table>
				<span class="reportTopPrintLink">
					<a href="${appContext}/employeeDeductionReportPFV.do?eid=${empDed.employeeInstId}&sDate=${empDed.fromDateStr}&eDate=${empDed.toDateStr}&pid=${empDed.id}" target="_blank">
					Printer-Friendly Version </a>&nbsp;
					<a href="${appContext}/employeeDeductionReportExcel.do?xls=y&eid=${empDed.employeeInstId}&sDate=${empDed.fromDateStr}&eDate=${empDed.toDateStr}&pid=${empDed.id}&coy=${empDed.companyName}">
					View in Excel </a><sup>1</sup> <br />
				</span>
				<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
					<tr>
				<td class="reportFormControls">
				 <span class="optional"> from </span> 
				 <form:input path="fromDate"/>
				 	<img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('fromDate'),event);"><span class="optional">&nbsp;to </span> <form:input path="toDate"/>
					<img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('toDate'),event);">&nbsp;&nbsp;
					Employee: <form:select path="employeeInstId">
                              <c:forEach items="${employeeList}" var="empList">
						      <form:option value="${empList.id}">${empList.name}</form:option>
						      </c:forEach>
						      </form:select>
                              &nbsp; 
                 </td>
					</tr>
					<tr>
						<td class="reportFormControlsSpacing"></td>
					</tr>
					<tr>
						<td>
						<h3><c:out value="${empDed.employeeName}"/>&nbsp;-&nbsp;<c:out value="${empDed.description}"/></h3>
						<br />
						<table class="report" cellspacing="0" cellpadding="0">
							<tr class="reportOdd header">
								<td class="tableCell" valign="top">Date</td>
								<td class="tableCell" valign="top">Total Withheld</td>
							</tr>
							<c:forEach items="${empDed.empMiniBeanList}" var="deduction">
							<tr class="${deduction.displayStyle}">
								<td class="tableCell" valign="top">
								<a href="${appContext}/paystubForm.do?pid=${deduction.paycheckId}"><c:out value="${deduction.payDateStr}"/></a></td>
								<td class="tableCell" align="right" valign="top">
								₦<c:out value="${deduction.totalWithheldStr}"/></td>
							</tr>
							</c:forEach>
							<tr class="reportOdd footer">
								<td class="tableCell" valign="top">Total</td>
								<td class="tableCell" align="right" valign="top">
								₦<c:out value="${empDed.totalStr}"/></td>
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
					<a href="${appContext}/employeeDeductionReportPFV.do?eid=${empDed.employeeInstId}&sDate=${empDed.fromDateStr}&eDate=${empDed.toDateStr}&pid=${empDed.id}" target="_blank">
					Printer-Friendly Version </a>&nbsp;
					<a href="${appContext}/employeeDeductionReportExcel.do?xls=y&eid=${empDed.employeeInstId}&sDate=${empDed.fromDateStr}&eDate=${empDed.toDateStr}&pid=${empDed.id}&coy=${empDed.companyName}">
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
	

