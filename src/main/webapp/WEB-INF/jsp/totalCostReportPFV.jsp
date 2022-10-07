<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<HTML>
<head>
			<title>Total Cost</title>
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
<form:form modelAttribute="totalCostBean">
<DIV class=titlePrinterFriendly><c:out value="${totalCostBean.companyName}"/><BR>Total Cost</DIV>
<TABLE class=reportMain cellSpacing=0 cellPadding=0 width="100%">
  <TBODY>
  <TR>
    <TD class=reportFormControls>
      
      <H3><c:out value="${totalCostBean.payPeriod}"/>
     </H3><P>  </P></TD></TR>
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

      <TABLE class=report cellSpacing=0 cellPadding=0 width="75%">
        <TBODY>
        <TR class="reportOdd header">
          <TD class=tableCell vAlign=top colspan="2">Total Pay</TD></TR>
        <TR class=reportEven>
          <TD class="tableCell indent" vAlign=top>Paycheck Wages</TD>
          <TD class=tableCell vAlign=top align=right>₦<c:out value="${totalCostBean.wagesTotalStr}"/></TD></TR>
        <TR class=reportOdd>
          <TD class="tableCell indent" vAlign=top>Reimbursements</TD>
          <TD class=tableCell vAlign=top align=right>₦<c:out value="${totalCostBean.reimbursementTotalStr}"/></TD></TR>
        <TR class=reportEven>
          <TD class="tableCell indent" vAlign=top>Total</TD>
          <TD class="tableCell total" vAlign=top align=right>₦<c:out value="${totalCostBean.totalPayStr}"/></TD></TR>
        <TR class="reportEven header">
          <TD class=tableCell vAlign=top colspan="2">Company Retirement/HSA
            Contributions</TD></TR>
		<c:forEach items="${totalCostBean.compContBean}" var="compCont">
        <TR class="${compCont.displayStyle}">
          <TD class="tableCell indent" vAlign=top><c:out value="${compCont.description}"/></TD>
          <TD class=tableCell vAlign=top align=right>₦<c:out value="${compCont.amountStr}"/></TD></TR>
          </c:forEach>
        <TR class=reportEven>       
          <TD class="tableCell indent" vAlign=top>Total</TD>
          <TD class="tableCell total" vAlign=top align=right>₦<c:out value="${totalCostBean.contTotalStr}"/></TD></TR>
       
        <TR class="reportOdd header">
          <TD class=tableCell vAlign=top>Total Cost</TD>
          <TD class=tableCell vAlign=top 
      align=right>₦<c:out value="${totalCostBean.totalCostStr}"/></TD></TR></TBODY></TABLE></TD></TR></TBODY></TABLE>
</form:form>
</BODY></HTML>
