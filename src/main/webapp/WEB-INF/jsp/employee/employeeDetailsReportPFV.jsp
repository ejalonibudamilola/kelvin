<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<HTML>
<head>
			<title>Employee Details</title>
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
<form:form modelAttribute="empDetails">
<DIV class=titlePrinterFriendly><c:out value="${empDetails.companyName}"/><BR>Employee Details</DIV>
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
    <TD class=tableCell vAlign=top>Personal Info</TD>
    <TD class=tableCell vAlign=top>Pay Info</TD>
    <TD class=tableCell vAlign=top>Tax Info</TD></TR>
    <c:forEach items="${employeeDetails}" var="empDet">
  <TR class=reportEven>
    <TD class="tableCell autoSplit compoundGroupVertical" vAlign=top><c:out value="${empDet.employee.firstName}"/>&nbsp;<c:out value="${empDet.employee.lastName}"/></TD>
    <TD class="tableCell autoSplit compoundGroupVertical" vAlign=top>Rate: 
      <c:out value="${empDet.payRate}"/></TD>
    <TD class="tableCell autoSplit compoundGroupVertical" vAlign=top>Employee ID: 
     <c:out value="${empDet.employee.employeeId}"/></TD></TR>
  <TR class=reportEven>
    <TD class="tableCell autoSplit indent compoundGroupVertical" 
      vAlign=top><c:out value="${empDet.employee.address1}"/></TD>
    <TD class="tableCell autoSplit compoundGroupVertical" vAlign=top>By: <c:out value="${empDet.payType}"/></TD>
     <TD class="tableCell autoSplit indent compoundGroupVertical" vAlign=top>&nbsp;</TD>
    </TR>
  <TR class=reportEven>
  <TD class="tableCell autoSplit compoundGroupVertical" vAlign=top><c:out value="${empDet.employee.city}"/>&nbsp;<c:out value="${empDet.employee.state}"/>&nbsp;<c:out value="${empDet.employee.zipCode}"/></TD>
    <TD class="tableCell autoSplit compoundGroupVertical" vAlign=top>Deductions/Other Deductions:</TD>
    <TD class="tableCell autoSplit compoundGroupVertical" vAlign=top><c:out value="${empDet.employee.state}"/>&nbsp;:<c:out value="${empDet.stateFilingInfo}"/> </TD>
    </TR>
  <TR class=reportEven>
    <TD class="tableCell autoSplit indent compoundGroupVertical" vAlign=top>&nbsp;</TD>
    <TD class="tableCell autoSplit compoundGroupVertical">
    	<c:choose>
			<c:when test="${empDet.hasDedGarns}">
				<c:forEach items="${empDetail.dedGarns}" var="dedGarn">
						<c:out value="${dedGarn.description}"/>&nbsp;:<c:if test="${dedGarn.usingCurrency}">₦</c:if><c:out value="${dedGarn.amountStr}"/><c:if test="${dedGarn.usingPercentage}">%</c:if><br/>
				</c:forEach>
			</c:when>
			<c:otherwise>
					<c:out value="${empDet.defaultVal}"/>
			</c:otherwise>
		</c:choose>
     
	</TD>
    <TD class="tableCell autoSplit compoundGroupVertical" vAlign=top>&nbsp;</TD></TR>
  <TR class=reportEven>
    <TD class="tableCell autoSplit indent compoundGroupVertical" vAlign=top>E-Mail : <c:out value="${empDet.employee.email}"/></TD>
    <TD class="tableCell autoSplit compoundGroupVertical" vAlign=top>&nbsp;</TD>
    <TD class="tableCell autoSplit compoundGroupVertical" vAlign=top>&nbsp;</TD></TR>
  <TR class=reportEven>
    <TD class="tableCell autoSplit indent compoundGroupVertical" 
      vAlign=top>Hired: <c:out value="${empDet.hireDate}"/></TD>
    <TD class="tableCell autoSplit compoundGroupVertical" vAlign=top>Company 
      Contributions:</TD>
    <TD class="tableCell autoSplit compoundGroupVertical" vAlign=top>&nbsp;</TD>
  </TR>
  <TR class=reportEven>
    <TD class="tableCell autoSplit indent compoundGroupVertical" 
      vAlign=top>Date of Birth: <c:out value="${empDet.dateOfBirth}"/></TD>
    <TD class="tableCell autoSplit compoundGroupVertical" vAlign=top>
    	<c:choose>
			<c:when test="${empDet.hasCompContribution}">
				<c:forEach items="${empDet.compContribution}" var="compCont">
					<c:out value="${compCont.description}"/>&nbsp;:<c:if test="${compCont.usingCurrency}">₦</c:if><c:out value="${compCont.amountStr}"/><c:if test="${compCont.usingPercentage}">%</c:if><br/>
				</c:forEach>
			</c:when>
			<c:otherwise>
					<c:out value="${empDet.defaultVal}"/>
			</c:otherwise>
		</c:choose>
      
	 </TD>
    <TD class="blankCell compoundGroupVertical" vAlign=top>&nbsp;</TD></TR>
  <TR class=reportEven>
    <TD class="blankCell compoundGroupVertical" vAlign=top>&nbsp;GSM : <c:out value="${empDet.employee.gsmNumber}"/></TD>
    <TD class="tableCell autoSplit compoundGroupVertical" vAlign=top>&nbsp;</TD>
    <TD class="blankCell compoundGroupVertical" vAlign=top>&nbsp;</TD></TR>
  <TR class=reportEven>
    <TD class=blankCell vAlign=top>&nbsp;</TD>
    <TD class="tableCell autoSplit" vAlign=top>Vac/Sick:&nbsp;<br/>
			<c:choose>
				<c:when test="${empDet.hasPolicyMessage}">
					<c:forEach items="${empDet.policyMessage}" varStatus="gridRow">
						<c:out value="${empDet.policyMessage[gridRow.index]}"/><br/>
					</c:forEach>
				</c:when>
				<c:otherwise>
						<c:out value="${empDet.defaultVal}"/>
				</c:otherwise>
			</c:choose>
    </TD>
    <TD class=blankCell vAlign=top>&nbsp;</TD>
    </TR>
    </c:forEach>
   </TBODY>
  </TABLE>
</form:form>
</BODY>
</HTML>