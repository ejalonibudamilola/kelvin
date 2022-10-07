<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Materiality View Form </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
</head>

<body class="main">
<script type="text/javascript" src="scripts/jacs.js"></script>
<form:form modelAttribute="miniBean">
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					<tr>
							<td>
								<div class="navTopBannerAndSignOffLink">
									<span class="navTopSignOff">
										&nbsp;&nbsp;&nbsp;<a href="javascript: self.close ()" title='Close Window' id="topNavSearchButton1">Close Window</a>
									</span>
								</div>
							</td>
						</tr>
                       
	<tr>
		<td colspan="2">
		<div class="title">
			Materiality View for<br>
			<c:out value="${miniBean.name}"/><br>
			Pay Period : <c:out value="${miniBean.payPeriodStr}"/>
		</div>
		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">
		
			
		Pay Differential : <c:out value ="${miniBean.amountStr}"/>
		<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
			
			<tr>
				<td class="reportFormControlsSpacing"></td>
			</tr>
			<tr>
				<td>
				
				<table class="report" cellspacing="0" cellpadding="0">
						<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="200px">Total Employees</td>
						<td class="tableCell" valign="top" align="center" width="300px">Action</td>
						<td class="tableCell" valign="top" align="center">Monetary Effect</td>
					</tr>
				</table>
				
				<table class="report" cellspacing="0" cellpadding="0">
					<c:if test="${miniBean.employeesCreated}"> 
					<tr class="reportEven">
						<td class="tableCell" valign="top" width="200px"><c:out value="${miniBean.noOfNewEmployees}"/></td>
						<td class="tableCell" align="center" valign="top" width="300px">Promoted Employees</td>
						<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.nuEmpNetEffectStr}"/></td>						
					</tr>
					</c:if>
					<c:if test="${miniBean.employeesReassigned}"> 
					<tr class="reportOdd">
						<td class="tableCell" valign="top" width="200px"><c:out value="${miniBean.noOfEmpReassigned}"/></td>
						<td class="tableCell" align="center" valign="top" width="300px">Employees Reassigned</td>
						<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.reassignedNetEffectStr}"/></td>						
					</tr>
					</c:if>
					<c:if test="${miniBean.employeesPromoted}"> 
					<tr class="reportEven">
						<td class="tableCell" valign="top" width="200px"><c:out value="${miniBean.noOfEmpPromoted}"/></td>
						<td class="tableCell" align="center" valign="top" width="300px">Promoted Employees</td>
						<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.promotedNetEffectStr}"/></td>						
					</tr>					
					</c:if>
					<c:if test="${miniBean.payeReduction}"> 
					<tr class="reportEven">
						<td class="tableCell" valign="top" width="200px"><c:out value="${miniBean.noOfEmpPayeInstruction}"/></td>
						<td class="tableCell" align="center" valign="top" width="300px">Taxes Reduced</td>
						<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.payeInstructionNetEffectStr}"/></td>						
					</tr>					
					</c:if>
					<c:if test="${miniBean.salaryIncrease}"> 
					<tr class="reportEven">
						<td class="tableCell" valign="top" width="200px"><c:out value="${miniBean.noOfEmpSalaryInstruction}"/></td>
						<td class="tableCell" align="center" valign="top" width="300px">Basic Salary Increased</td>
						<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.salaryInstructionNetEffectStr}"/></td>						
					</tr>					
					</c:if>
					<c:if test="${miniBean.thirteenthMonth}"> 
					<tr class="reportEven">
						<td class="tableCell" valign="top" width="200px"><c:out value="${miniBean.noOfEmpThirteenthMonth}"/></td>
						<td class="tableCell" align="center" valign="top" width="300px">Leave Transport Grant Payments</td>
						<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.thirteenMonthNetEffectStr}"/></td>						
					</tr>					
					</c:if>
					<c:if test="${miniBean.specialAllowances}"> 
					<tr class="reportEven">
						<td class="tableCell" valign="top" width="200px"><c:out value="${miniBean.noOfEmpWithSpecialAllowance}"/></td>
						<td class="tableCell" align="center" valign="top" width="300px">Special Allowance Payments</td>
						<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.specialAllowanceNetEffectStr}"/></td>						
					</tr>					
					</c:if>
					<c:if test="${miniBean.underPaymentAllowances}"> 
					<tr class="reportEven">
						<td class="tableCell" valign="top" width="200px"><c:out value="${miniBean.noOfEmpWithUnderpaymentAllowance}"/></td>
						<td class="tableCell" align="center" valign="top" width="300px">Under-Payment Allowance Payments</td>
						<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.underPaymentAllowanceNetEffectStr}"/></td>						
					</tr>					
					</c:if>
				</table>
				
				<table class="report" cellspacing="0" cellpadding="0">
						<tr class="reportEven footer">
						<td class="tableCell" valign="top" width="200px">Total</td>
						<td class="tableCell" valign="top" align="right"><c:out value="${miniBean.totalStr}"/></td>
					</tr>
				</table>
				<br/>
				<br/>
				<br/>
				<p>
				
				**Note - Materiality values do not take into consideration outgoing Employee Reassignements and<br>
				also no personal deductions like Owner Occupier and other such Employee specific deductions are<br>
				taken into consideration. The information on this page are provided as a guide to what might cause<br>
				material differences in payroll amount on a month-to-month basis. It is not accurate to the last Kobo.<br>
				For a comprehensive backend audit, please contact GNL Systems Nigeria Ltd at support@gnlsystems.com
				</td>
			</tr>
 			
		</table>
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
