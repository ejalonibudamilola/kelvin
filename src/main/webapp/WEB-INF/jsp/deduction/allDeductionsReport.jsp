<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title>Deductions</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>

</head>

<body class="main">
<form:form modelAttribute="deductionDetails">
<script type="text/javascript" src="scripts/jacs.js"></script>
<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
	<tr>
		<td>
		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			
			<tr>
				<td colspan="2">
				<div class="title">
					<c:out value="${namedEntity.name}"></c:out><br>
					Deductions</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainBody" id="mainBody">
				
				<table>
				<tr>
					<td>
						<form:select path="subLinkSelect">
						<form:option value="paystubsReportForm">Payroll Summary</form:option>
						<form:option value="employeeDetailsReport">Employee Details</form:option>
						<form:option value="preLastPaycheckForm">Last Paycheck</form:option>
						</form:select> 
					</td>
					<td>&nbsp;</td>
					<td>
					<input type="image" name="_go" value="go" title="Go" class="" src="images/go_h.png" >
					</tr>
				</table>
				
				<span class="reportTopPrintLink">
					<a href="${appContext}/deductionsReportPFV.do?did=${deductionDetails.deductionId}&pfv=yes&sDate=${deductionDetails.fromDateStr}&eDate=${deductionDetails.toDateStr}&pid=${deductionDetails.id}" target="_blank">
					Printer-Friendly Version </a>&nbsp;
					<a href="${appContext}/allDeductionDetailsExcel.do?sDate=${deductionDetails.fromDateStr}&eDate=${deductionDetails.toDateStr}&pid=${deductionDetails.id}">
					View in Excel </a><sup>1</sup> <br /></span>
				<div class="reportDescText">
					This report shows the total for each payroll deduction
					during the date range you select. </div>
				<br />
				Click any total to see the details that make up the total. <br />
				<br />
				<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
					<tr>
						<td class="reportFormControls">
				 <span class="optional"> from </span> 
				 <form:input path="fromDate"/>
				 	<img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('fromDate'),event);"><span class="optional">&nbsp;to </span> <form:input path="toDate"/>
					<img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('toDate'),event);">&nbsp;&nbsp;
					Deduction: <form:select path="deductionId">
                               <form:option value="0">All Deductions</form:option>
						       <c:forEach items="${deductionList}" var="deduction">
						      <form:option value="${deduction.id}" title="${deduction.name}">${deduction.description}</form:option>
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
						<table class="report" cellspacing="0" cellpadding="0">
							<tr class="reportOdd header">
								<td class="tableCell" valign="top">Description</td>
								<td class="tableCell" valign="top">Type</td>
								<td class="tableCell" valign="top">Total Withheld</td>
							</tr>
						</table>
							<div style="overflow:scroll;height:80px;width:100%;overflow:auto">
							<table class="report" cellspacing="0" cellpadding="0">
							<c:forEach items="${deductionDetails.deductionMiniBean}" var="dedMiniBean">
							<tr class="${dedMiniBean.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${dedMiniBean.type}"/></td>
								<td class="tableCell" valign="top"><c:out value="${dedMiniBean.description}"/></td>
								<td class="tableCell" align="right" valign="top">
								<a href="${appContext}/deductionDetailsReport.do?did=${dedMiniBean.deductionId}&sDate=${deductionDetails.fromDateStr}&eDate=${deductionDetails.toDateStr}&pid=${deductionDetails.id}">
								₦<c:out value="${dedMiniBean.amountStr}"/></a></td>
							</tr>
							</c:forEach>
							</table>
							</div>
						
						</td>
					</tr>
					<tr>
                         <td class="buttonRow" align="right">
                             <input type="image" name="_updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                         </td>
                     </tr>
				</table>
				<div class="reportBottomPrintLink">
					<a href="${appContext}/deductionsReportPFV.do?did=${deductionDetails.deductionId}&sDate=${deductionDetails.fromDateStr}&eDate=${deductionDetails.toDateStr}&pid=${deductionDetails.id}" target="_blank">
					Printer-Friendly Version </a>&nbsp;
					<a href="${appContext}/allDeductionDetailsExcel.do?sDate=${deductionDetails.fromDateStr}&eDate=${deductionDetails.toDateStr}&pid=${deductionDetails.id}">
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
			<tr>
                <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
            </tr>
			</table>
			
			</form:form>
		</body>

	</html>
