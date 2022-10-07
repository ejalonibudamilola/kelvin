<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<HTML>
<head>
			<title>Retirement Plans</title>
			<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
			<META HTTP-EQUIV="Expires" CONTENT="-1">
			<META HTTP-EQUIV="Cache-Control" VALUE="no-cache, no-store, must-revalidate">
			<META HTTP-EQUIV="Cache-Control" VALUE="post-check=0, pre-check=0">
		
			

			<link rel="stylesheet" href="styles/omg.css" type="text/css">
       	 	<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        	<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
			<script language="JavaScript" src="scripts/input_validators.js"></script>
			<script language="JavaScript" src="scripts/helper.js"></script>
			
		</head>
<body>
<form:form modelAttribute="retPlanBean">		
<DIV class=titlePrinterFriendly><c:out value="${retPlanBean.companyName}"/><BR>Retirement Plans</DIV>
<TABLE class=reportMain cellSpacing=0 cellPadding=0 width="100%">
  <TBODY>
  <TR>
    <TD class=reportFormControls>
        <H3><c:out value="${retPlanBean.payPeriod}"/>
      </H3><P>
      </P></TD></TR>
  <TR>
    <TD class=reportFormControlsSpacing></TD></TR>
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
							</c:forEach></TR></TBODY></TABLE></TD></TR></TBODY></TABLE>
<DIV class=reportBottomPrintLink></DIV>
</form:form>
</BODY>
</HTML>
