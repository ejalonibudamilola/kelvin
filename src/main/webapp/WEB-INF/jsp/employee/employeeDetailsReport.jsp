<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Employee Details </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
</head>

<body class="main">
<script type="text/javascript" src="scripts/jacs.js"></script>
<form:form modelAttribute="empDetails">
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
		<tr>
		<td colspan="2">
		<div class="title">
			<c:out value="${empDetails.companyName}"/><br>
			Employee Details</div>
		</td>
	</tr>
	
	<tr>
		<td valign="top" class="mainBody" id="mainBody">
			<div id="topOfPageBoxedErrorMessage" style="display:${empDetails.displayErrors}">
					<spring:hasBindErrors name="empDetails">
						<ul>
							<c:forEach var="errMsgObj" items="${errors.allErrors}">
								<li><spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}" /></li>
							</c:forEach>
						</ul>
					</spring:hasBindErrors>
			</div>
			<table>
				<tr>
					<td>
						<form:select path="subLinkSelect">
						<form:option value="paystubsReportForm">Payroll Summary</form:option>
						<form:option value="allOtherDeductionsReport">Deductions</form:option>
						<form:option value="preLastPaycheckForm">Last Paycheck</form:option>
						</form:select> 
					</td>
					<td>&nbsp;</td>
					<td>
					<input type="image" name="_go" value="go" alt="Go" class="" src="images/go_h.png" />
					</td>
				</tr>
			</table>
			
		
		
		<span class="reportTopPrintLink">
		<a href="${appContext}/employeeDetailsReportPFV.do?eid=${empDetails.id}" target="_blank">
		Printer-Friendly Version </a>&nbsp;
		<a href="${appContext}/employeeDetailsReport.do">
		Select another employee </a>&nbsp;
		</span>
		<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
			<!--
			<tr>
				<td class="reportFormControls">				
					Employee: <form:select path="id">
					<form:option value="0">All Employees</form:option>
					<c:forEach items="${employees}" var="emp">
					<form:option value="${emp.id}">${emp.firstName}&nbsp;${emp.lastName}</form:option>
					</c:forEach>
					</form:select>&nbsp;&nbsp;
					<input type="image"	name="_updateReport" value="updateReport" alt="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
				</td>
			</tr>
			-->
			<tr>
				<td class="reportFormControlsSpacing"></td>
			</tr>
			<tr>
				<td>
				<table class="report" cellspacing="0" cellpadding="0">
					<tr class="reportOdd header">
						<td class="tableCell" valign="top">Personal Info</td>
						<td class="tableCell" valign="top">Pay Info</td>
						<td class="tableCell" valign="top">Tax Info</td>
					</tr>
					<c:forEach items="${employeeDetails}" var="empDetail">
					<tr class="reportEven">
						<td class="tableCell autoSplit compoundGroupVertical" valign="top">
						<a href="${appContext}/editEmployeeForm.do?oid=${empDetail.employee.id}"><c:out value="${empDetail.employee.firstName}"/>&nbsp;<c:out value="${empDetail.employee.lastName}"/></a></td>
						<td class="tableCell autoSplit compoundGroupVertical" valign="top">
						Rate:₦<c:out value="${empDetail.payRate}"/></td>
						<td class="tableCell autoSplit compoundGroupVertical" valign="top">
						Employee ID: <c:out value="${empDetail.employee.employeeId}"/></td>
						
					</tr>
					<tr class="reportEven">
						<td class="tableCell autoSplit indent compoundGroupVertical" valign="top">
						<c:out value="${empDetail.employee.address1}"/></td>
						<td class="tableCell autoSplit compoundGroupVertical" valign="top">
						By: <c:out value="${empDetail.payType}"/></td>
						<td class="tableCell autoSplit indent compoundGroupVertical" valign="top">
						Pay Scale: <c:out value="${empDetail.employee.salaryScale}"/> - <c:out value="${empDetail.employee.levelAndStep}"/></td>
						<!-- <td class="tableCell autoSplit indent compoundGroupVertical" valign="top">&nbsp;</td>-->
					</tr>
					<tr class="reportEven">
						<td class="tableCell autoSplit indent compoundGroupVertical" valign="top">
						<c:out value="${empDetail.employee.city}"/>&nbsp;<c:out value="${empDetail.employee.state}"/>&nbsp;<c:out value="${empDetail.employee.zipCode}"/></td>
						<td class="tableCell autoSplit compoundGroupVertical" valign="top">
						Deductions/Other Deductions:</td>
						<td class="tableCell autoSplit compoundGroupVertical" valign="top">
						<c:out value="${empDetail.employee.state}"/>&nbsp;:<c:out value="${empDetail.stateFilingInfo}"/> </td>
					</tr>
					<tr class="reportEven">
						<td class="tableCell autoSplit indent compoundGroupVertical" valign="top">&nbsp;</td>
						<td class="tableCell autoSplit compoundGroupVertical" valign="top"> 
							<c:choose>
								<c:when test="${empDetail.hasDedGarns}">
									<c:forEach items="${empDetail.dedGarns}" var="dedGarn">
							 			<c:out value="${dedGarn.description}"/>&nbsp;:<c:if test="${dedGarn.usingCurrency}">₦</c:if><c:out value="${dedGarn.amountStr}"/><c:if test="${dedGarn.usingPercentage}">%</c:if><br/>
									</c:forEach>
								</c:when>
								<c:otherwise>
										<c:out value="${empDetail.defaultVal}"/>
								</c:otherwise>
							</c:choose>
						</td>						
						<td class="tableCell autoSplit compoundGroupVertical" valign="top">&nbsp;</td>
					</tr>
					<tr class="reportEven">
						<td class="tableCell autoSplit indent compoundGroupVertical" valign="top">
						E-Mail : <c:out value="${empDetail.employee.email}"/></td>
						<td class="tableCell autoSplit compoundGroupVertical" valign="top">&nbsp;</td>
						<td class="tableCell autoSplit compoundGroupVertical" valign="top">&nbsp;</td>
					</tr>
					<tr class="reportEven">
						<td class="tableCell autoSplit indent compoundGroupVertical" valign="top">
						Hired: <c:out value="${empDetail.hireDate}"/></td>
						<td class="tableCell autoSplit compoundGroupVertical" valign="top">
						Company Contributions:</td>
						<td class="tableCell autoSplit compoundGroupVertical" valign="top">&nbsp;</td>
					</tr>
					<tr class="reportEven">
						<td class="tableCell autoSplit indent compoundGroupVertical" valign="top">
						Date of Birth: <c:out value="${empDetail.dateOfBirth}"/></td>
						<td class="tableCell autoSplit compoundGroupVertical" valign="top">				
							<c:choose>
								<c:when test="${empDetail.hasCompContribution}">
									<c:forEach items="${empDetail.compContribution}" var="compCont">
							 		 	<c:out value="${compCont.description}"/>&nbsp;:<c:if test="${compCont.usingCurrency}">₦</c:if><c:out value="${compCont.amountStr}"/><c:if test="${compCont.usingPercentage}">%</c:if><br/>
									</c:forEach>
								</c:when>
								<c:otherwise>
										<c:out value="${empDetail.defaultVal}"/>
								</c:otherwise>
							</c:choose>
						</td>
						<td class="blankCell compoundGroupVertical" valign="top">&nbsp;</td>
					</tr>
					<tr class="reportEven">
						<td class="blankCell compoundGroupVertical" valign="top">&nbsp;
						GSM : <c:out value="${empDetail.employee.gsmNumber}"/></td>
						<td class="tableCell autoSplit compoundGroupVertical" valign="top">&nbsp;</td>
						<td class="blankCell compoundGroupVertical" valign="top">&nbsp;</td>
					</tr>
					<tr class="reportEven">
						<td class="blankCell" valign="top">&nbsp;</td>
						<td class="tableCell autoSplit" valign="top">
							Vac/Sick:&nbsp;<br/>
								<c:choose>
								<c:when test="${empDetail.hasPolicyMessage}">
									<c:forEach items="${empDetail.policyMessage}" varStatus="gridRow">
										<c:out value="${empDetail.policyMessage[gridRow.index]}"/><br/>
									</c:forEach>
								</c:when>
								<c:otherwise>
										<c:out value="${empDetail.defaultVal}"/>
								</c:otherwise>
							</c:choose>
						</td>
						<td class="blankCell" valign="top">&nbsp;</td>
					</tr>
					</c:forEach>
				</table>
				</td>
			</tr>
						<!--<tr>
							<td class="buttonRow" align="right"><input type="image"
								name="_updateReport" value="updateReport" alt="Update Report"
								class="updateReportSubmit" src="images/Update_Report_h.png">
							</td>
						</tr>
					--></table>
			<!--
			<c:if test="${empDetails.showLink}">
			<c:choose>
				<c:when test="${empDetails.showingInactive}">
					<a href='${appContext}/employeeDetailsReport.do'>Hide inactive employees</a>
				</c:when>
				<c:otherwise>
					<a href='${appContext}/employeeDetailsReport.do?filter=a'>Show inactive employees</a>
				</c:otherwise>
			</c:choose>
			</c:if>
		  -->
		<div class="reportBottomPrintLink">
			<a href="${appContext}/employeeDetailsReportPFV.do?eid=${empDetails.id}" target="_blank">
		Printer-Friendly Version </a>&nbsp;
		<a href="${appContext}/employeeDetailsReport.do">Select another employee </a>&nbsp;
		</div>
		<br>
		</td>
		<td valign="top" class="navSide"><br>
		<br>
		<!-- Here to put space beween this and any following divs --></td>
	</tr>
	</table>
</td>
</tr>
</table>
</form:form>
</body>

</html>
