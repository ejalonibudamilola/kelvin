<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>

<HTML>
<head>
<title>Total Pay</title>
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Expires" CONTENT="-1">
<META HTTP-EQUIV="Cache-Control"
	VALUE="no-cache, no-store, must-revalidate">
<META HTTP-EQUIV="Cache-Control" VALUE="post-check=0, pre-check=0">



<link rel="stylesheet" href="styles/omg.css" type="text/css">
<link rel="stylesheet" href="styles/skye.css" type="text/css"
	media="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" 	type="text/css" />
</head>
<body>
<form:form modelAttribute="taxWageBean">
	<DIV class=titlePrinterFriendly><c:out value="${taxWageBean.companyName}"/><BR>
	Total Pay</DIV>
	<TABLE class=reportMain cellSpacing=0 cellPadding=0 width="100%">
		<TBODY>
			<TR>
				<TD class=reportFormControls>

				<H3><c:out value="${taxWageBean.payPeriod}" /></H3>
				<P></P>
				</TD>
			</TR>
			<TR>
				<TD class=reportFormControlsSpacing></TD>
			</TR>
			<TR>
				<TD>
				<STYLE>
BODY {
	MARGIN-LEFT: 20px
}

.report TD {
	FONT-SIZE: 9pt
}

.report .header {
	FONT-WEIGHT: bold;
	COLOR: #000000;
	BACKGROUND-COLOR: #e2e2e2
}

.report .footer {
	FONT-WEIGHT: bold;
	COLOR: #000000;
	BACKGROUND-COLOR: #e2e2e2
}

TABLE.report {
	WIDTH: auto
}

TABLE.reportMain {
	WIDTH: auto
}

TR.reportEven {
	BACKGROUND-COLOR: #f7f7f7
}

TD.reportFormControls {
	FONT-WEIGHT: bold;
	COLOR: #000000;
	BACKGROUND-COLOR: #e2e2e2
}

H3 {
	MARGIN-LEFT: 10px
}
</STYLE>


				<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
					<tr>
						<td>
						<h3><c:out value="${taxWageBean.name}"/>&nbsp; Income Tax</h3>
						<br />
						</td>
					</tr>
					<tr>
					<td>
					<table class=report cellspacing=0 cellpadding=0>
						<TBODY>
							<tr class='reportOdd header'>
								<td class='tableCell' align=center valign="top">Employee
								Name</td>
								<td class='tableCell' align=center valign="top">Employee
								ID</td>
								<td class='tableCell' align=center valign="top">Employee
								Address</td>
								<td class='tableCell' align=center valign="top">Total Wages</td>
								<td class='tableCell' align=center valign="top">Taxable
								Wages</td>
								<td class='tableCell' align=center valign="top">Tax Amount</td>
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
								<tr class='reportEven footer'>
								<td class="tableCell" colspan="3" valign="top">Totals</td>
								<td class="tableCell" align="right" valign="top">₦<c:out value="${taxWageBean.totalWagesStr}"/></td>
								<td class="tableCell" align="right" valign="top">₦<c:out value="${taxWageBean.totalTaxableWagesStr}"/></td>
								<td class="tableCell" align="right" valign="top">₦<c:out value="${taxWageBean.totalTaxAmountStr}"/></td>
								
							</tr>
					</table>
				
					</td>
					</tr>
				</table>

			</tr>


	</table>

</form:form>
</BODY>
</HTML>

