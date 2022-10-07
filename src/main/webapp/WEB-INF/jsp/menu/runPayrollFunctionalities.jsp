<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title>Run Payroll Main Page </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">

<style type="text/css">
.style3 {
	text-decoration: underline;
	color: #0000FF;
}
</style>

</head>

<body class="main">
    <form:form modelAttribute="adminBean">
	<table class="main" width="74%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

	<%@ include file="/WEB-INF/jsp/headerFile.jsp"%>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
	<tr>
		<td colspan="2">
		<div class="title">
		 Run Payroll Overview</div>
		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">
		<table width="95%">
			<tr>
				<td valign="top">
					<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
							<tr>
								<td colspan="50" class="infoTH">Run Payroll</td>
							</tr>
							
							<c:if test="${adminBean.hasPendingLeaveBonus}">
								<tr>
									<td class="infoTROdd">
									<a href="${appContext}/viewPendingLeaveBonus.do">[ <c:out value="${adminBean.noOfPendingLeaveBonuses}"/> ]&nbsp;Pending Leave Bonus(es) found.</a><br />
									 View leave bonus(es) awaiting approval. <br />
									</td>
								</tr>
							
							</c:if>
                            <c:if test="${adminBean.hasPendingAllowanceApprovals}">
								<tr>
									<td class="infoTROdd">
									<a href="${appContext}/viewPendingAllowanceRule.do">[ <c:out value="${adminBean.noOfPendingAllowanceRules}"/> ]&nbsp;Pending PayGroup Allowance Rules found.</a><br />
									 View Pending PayGroup Allowance Rules awaiting approval. <br />
									</td>
								</tr>

							</c:if>
							
							<c:if test="${adminBean.hasPendingTransfers}">
								<tr>
									<td class="infoTROdd">
									<a href="${appContext}/viewPendingTransfers.do">[ <c:out value="${adminBean.noOfPendingTransfers}"/> ]&nbsp;Pending Transfer(s) found.</a><br />
									 View <c:out value="${roleBean.staffTypeName}"/> transfers awaiting approval. <br />
									</td>
								</tr>
							
							</c:if>
							
							<c:if test="${roleBean.hasPendingEmployeeApprovals}">
								<tr>
									<td class="infoTROdd">
									<a href="${appContext}/viewEmpForPayApproval.do">[ <c:out value="${roleBean.noOfPendingEmployeeApprovals}"/> ]&nbsp;Pending  <c:out value="${roleBean.staffTypeName}"/> Approval(s) found.</a><br />
									 View  <c:out value="${roleBean.staffTypeName}"/> awaiting approval for payrolling. <br />
									</td>
								</tr>
							
							</c:if>
							
							<c:if test="${adminBean.hasPendingNameConflicts}">
								<tr>
									<td class="infoTROdd">
									<a href="${appContext}/viewPendingConflicts.do">[ <c:out value="${adminBean.noOfPendingNameConflicts}"/> ]&nbsp;Name Conflict(s) found.</a><br />
									 View  <c:out value="${roleBean.staffTypeName}"/> with name conflicts during creation. <br />
									</td>
								</tr>
							
							</c:if>
							
							<c:if test="${adminBean.approvedPaychecks}">
								<c:choose>
									<c:when test="${roleBean.rerunPayrollExists}">
										<tr>
											<td class="infoTREven">
												<a href="${appContext}/rerunPayroll.do">Rerun <c:out value="${roleBean.noOfDeletedPaychecks}" /> Deleted Paycheck(s)</a><br />
													Rerun payroll for deleted and rescheduled pending paychecks.<br /></td>
											</tr>
									</c:when>
									<c:otherwise>
											<c:choose>
												<c:when test="${adminBean.noOfNegativePayRecords gt 0 }">
													<tr>
														<td class="infoTREven">
															<a href="${appContext}/viewNegativePay.do">Treat Negative Pay</a><br />
																Treat Negative Pay to enable Payroll Approval.<br />
														</td>
													</tr>
												
												</c:when>
												<c:otherwise>

													<tr>
														<td class="infoTREven">
															<a href="${appContext}/approvePaychecksForm.do">Approve Pending Paychecks</a><br />
																Approve Pending paychecks.<br />
														</td>
													</tr>
												</c:otherwise>
											</c:choose>
											
											 <tr>
												<td class="infoTREven">
														<a href="${appContext}/reRunAllPaychecks.do" title="Re-run all pending paychecks if values have changed that would affect payroll">Re-Run Pending Paychecks</a><br />
															Rerun ALL Pending paychecks.<br />
												</td>
											</tr>
											
											<tr>
												<td class="infoTREven"><a
													href="${appContext}/deletePendingPayroll.do">Delete Payroll Run</a><br />
													Delete this Payroll Run<br /></td>
											</tr>
									</c:otherwise>
								
								</c:choose>
							
							</c:if>						
							<c:if test="${not adminBean.approvedPaychecks}">
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/paydayForm.do">Create Paychecks 
								</a><br />
								Run Payroll for <c:out value="${roleBean.staffTypeName}"/>. </td>
							</tr>
							</c:if>							
						   
							
							
						</table>
				</td>
				<td width="5%">&nbsp;</td>
				<td width="45%" valign="top">
		
				</td>
			</tr>
			
		</table>
		
	</table>
</td>
</tr>
<tr>				

			<%@ include file="/WEB-INF/jsp/footerFile.jsp"%>
</tr>
</table>
</form:form>
</body>

</html>
