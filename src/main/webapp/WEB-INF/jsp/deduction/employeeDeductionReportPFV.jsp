<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
<title><c:out value="${roleBean.staffTypeName}"/> Deductions</title>
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="-1">
<meta http-equiv="Cache-Control" value="no-cache, no-store, must-revalidate">
<meta http-equiv="Cache-Control" value="post-check=0, pre-check=0">

<link rel="stylesheet" href="styles/omg.css" type="text/css">
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css" />
<script language="JavaScript" src="scripts/input_validators.js"></script>
<script language="JavaScript" src="scripts/helper.js"></script>
</head>
<body>
<form:form modelAttribute="empDed">
<div class="titlePrinterFriendly"><c:out value="${empDed.companyName}"/><br>
<c:out value="${roleBean.staffTypeName}"/> Deductions for <c:out value="${empDed.employeeName}"/></div>


<table class="reportMain" width="100%" cellpadding="0" cellspacing="0">
	<tbody>
		<tr>
			<td class="reportFormControls">
			<h3><c:out value="${empDed.payPeriod}"/></h3>
			</td>
		</tr>
		<tr>
			<td class="reportFormControlsSpacing"></td>
		</tr>

		<tr>
			<td>

			<style>
body {
	margin-left: 20px;
}

.report td {
	font-size: 9pt;
}

.report .header {
	background-color: #E2E2E2;
	font-weight: bold;
	color: #000000;
}

.report .footer {
	background-color: #E2E2E2;
	font-weight: bold;
	color: #000000;
}

table.report {
	width: auto;
}

table.reportMain {
	width: auto;
}

tr.reportEven {
	background-color: #F7F7F7;
}

td.reportFormControls {
	background-color: #E2E2E2;
	font-weight: bold;
	color: #000000;
}

h3 {
	margin-left: 10px;
}
</style>
			<h3><c:out value="${empDed.employeeName}"/> - <c:out value="${empDed.description}"/></h3>
			<br>
			<table class="report" cellpadding="0" cellspacing="0">
				<tbody>
					<tr class="reportOdd header">
						<td class="tableCell" valign="top">Date</td>
						<td class="tableCell" valign="top">Total Withheld</td>
					</tr>
					<c:forEach items="${empDed.empMiniBeanList}" var="deduction">
							<tr class="${deduction.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${deduction.payDateStr}"/></td>
								<td class="tableCell" align="right" valign="top">₦<c:out value="${deduction.totalWithheldStr}"/></td>
							</tr>
							
					</c:forEach>
					<tr class="reportOdd footer">
						<td class="tableCell" valign="top">Total</td>
						<td class="tableCell" align="right" valign="top">₦<c:out value="${empDed.totalStr}"/></td>
					</tr>
				</tbody>
			</table>


			</td>
		</tr>
	</tbody>
</table>

<div class="reportBottomPrintLink"></div>
</form:form>
</body>
</html>