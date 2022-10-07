<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<HTML>
<head>
			<title>Total Pay</title>
			<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
			<META HTTP-EQUIV="Expires" CONTENT="-1">
			<META HTTP-EQUIV="Cache-Control" VALUE="no-cache, no-store, must-revalidate">
			<META HTTP-EQUIV="Cache-Control" VALUE="post-check=0, pre-check=0">
		
			

			<link rel="stylesheet" href="styles/omg.css" type="text/css">
       	 	<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        	<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
			
		</head>
<body>
<form:form modelAttribute="totalPayBean">
<DIV class=titlePrinterFriendly><c:out value="${totalPayBean.companyName}"/><BR>Total Pay</DIV>
<TABLE class=reportMain cellSpacing=0 cellPadding=0 width="100%">
  <TBODY>
  <TR>
    <TD class=reportFormControls>
      
      <H3><c:out value="${totalPayBean.payPeriod}"/>
      </H3><P>
      </P></TD></TR>
  <TR>
    <TD class=reportFormControlsSpacing></TD></TR>
  <TR>
    <TD>
      <STYLE>BODY {
	MARGIN-LEFT: 20px
}
.report TD {
	FONT-SIZE: 9pt
}
.report .header {
	FONT-WEIGHT: bold; COLOR: #000000; BACKGROUND-COLOR: #e2e2e2
}
.report .footer {
	FONT-WEIGHT: bold; COLOR: #000000; BACKGROUND-COLOR: #e2e2e2
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
	FONT-WEIGHT: bold; COLOR: #000000; BACKGROUND-COLOR: #e2e2e2
}
H3 {
	MARGIN-LEFT: 10px
}
</STYLE>

      <TABLE class=report cellSpacing=0 cellPadding=0>
        <TBODY>
        <TR class="reportOdd header">
          <TD class=tableCell vAlign=top>Name</TD>
          <TD class=tableCell vAlign=top>Employee ID</TD>
          <TD class=tableCell vAlign=top>Salary</TD>
          <TD class=tableCell vAlign=top>Total</TD></TR>
          <c:forEach items="${totalPayBean.payBean}" var="pBean">
					<tr class="${pBean.displayStyle}">
						<td class="tableCell" valign="top"><c:out value="${pBean.employeeName}"/></td>
						<td class="tableCell" align="right" valign="top"><c:out value="${pBean.employeeId}"/></td>
						<td class="tableCell" align="right" valign="top">₦<c:out value="${pBean.salaryStr}"/></td>
						<td class="tableCell" align="right" valign="top">₦<c:out value="${pBean.salaryTotalStr}"/></td>
					</tr>
					</c:forEach>
					<tr class="reportOdd header">
					<td>&nbsp;</td>
					<td class="tableCell total" align="right" valign="top">Totals</td>
					<td class="tableCell total" align="right" valign="top">₦<c:out value="${totalPayBean.totalPayStr}"/></td>
					<td class="tableCell total" align="right" valign="top">₦<c:out value="${totalPayBean.totalPayStr}"/></td>
					</tr>
        </TBODY></TABLE></TD></TR></TBODY></TABLE>
</form:form>
</BODY></HTML>
