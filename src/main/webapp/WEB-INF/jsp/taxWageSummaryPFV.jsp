<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<HTML>
<head>
			<title>Tax And Wages Summary</title>
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
<form:form modelAttribute="taxWageBean">
<DIV class=titlePrinterFriendly><c:out value="${taxWageBean.companyName}"/><BR>Tax and Wage 
Summary</DIV>
<TABLE class=reportMain cellSpacing=0 cellPadding=0 width="100%">
  <TBODY>
  <TR>
    <TD class=reportFormControls>
      
      <H3><c:out value="${taxWageBean.payPeriod}"/>
     </H3> <P>

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
          <TD class=tableCell vAlign=top>State Income Tax</TD>
          <TD class=tableCell vAlign=top align=middle>Total Wages</TD>
          <TD class=tableCell vAlign=top align=middle>Taxable Wages</TD>
          <TD class=tableCell vAlign=top align=middle>Tax Amount</TD></TR>
      	<c:forEach items="${taxWageBean.wageBean}" var="wBean">
        <TR class="${wageBean.displayStyle}">
          <TD class=tableCell vAlign=top><c:out value="${wBean.state}"/>&nbsp; Income Tax</TD>
          <TD class=tableCell vAlign=top align=middle>₦<c:out value="${wBean.totalWagesStr}"/></TD>
          <TD class=tableCell vAlign=top align=middle>₦<c:out value="${wBean.taxableWagesStr}"/></TD>
          <TD class=tableCell vAlign=top align=middle>₦<c:out value="${wBean.taxAmountStr}"/></TD>
          <TR class="reportOdd footer">
          <TD class=tableCell vAlign=top>Total</TD>
          <TD class=tableCell vAlign=top align=right colSpan=4>₦<c:out value="${wBean.totalStr}"/></TD></TR>
        <TR class=reportEven>
          <TD class=blankCell vAlign=top colSpan=5>&nbsp;</TD></TR>
         </c:forEach>
        
        
       </TBODY></TABLE></TD></TR></TBODY></TABLE>
</form:form>
</BODY></HTML>
