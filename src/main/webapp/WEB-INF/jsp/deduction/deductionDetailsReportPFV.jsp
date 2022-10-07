<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>

<HTML>
<head>
<title>Deduction Details</title>
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Expires" CONTENT="-1">
<META HTTP-EQUIV="Cache-Control"
	VALUE="no-cache, no-store, must-revalidate">
<META HTTP-EQUIV="Cache-Control" VALUE="post-check=0, pre-check=0">



<link rel="stylesheet" href="styles/omg.css" type="text/css">
<link rel="stylesheet" href="styles/skye.css" type="text/css"
	media="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>"
	type="text/css" />
<script language="JavaScript" src="scripts/input_validators.js"></script>
<script language="JavaScript" src="scripts/helper.js"></script>

</head>
<body>
<form:form modelAttribute="deductionDetails">

	<div class="titlePrinterFriendly"><c:out value="${deductionDetails.companyName}" /><br>
	Deductions</div>



	<table class="reportMain" width="100%" cellpadding="0" cellspacing="0">
		<tbody>
			<tr>
				<td class="reportFormControls">

				<H3><c:out value="${deductionDetails.payPeriod}" /></H3>
				<P></P>
				</TD>
			</TR>

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
				<h3><c:out value="${deductionDetails.currentDeduction}" /></h3>
				<br>
				<table class="report" cellpadding="0" cellspacing="0">
					<tbody>
						<tr class="reportOdd header">
							<td class="tableCell" valign="top"><c:out value="${roleBean.staffTypeName}"/> Name</td>
							<td class="tableCell" valign="top">Total Withheld</td>
						</tr>
						<c:forEach items="${deductionDetails.deductionMiniBean}" var="dedMiniBean">
							<tr class="${dedMiniBean.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${dedMiniBean.name}" /></td>
								<td class="tableCell" valign="top" align="right">₦<c:out value="${dedMiniBean.amountStr}" /></td>
							</tr>
						</c:forEach>

						<tr class="reportEven footer">
							<td class="tableCell" valign="top">Total</td>
							<td class="tableCell" valign="top" align="right">₦<c:out value="${deductionDetails.totalStr}" /></td>
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