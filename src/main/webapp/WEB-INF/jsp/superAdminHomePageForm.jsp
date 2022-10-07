<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title>Super Admin Main Page  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">

<style type="text/css">
.style3 {
	text-decoration: underline;
	color: #0000FF;
}
</style>

</head>

<body class="main">
    <form:form modelAttribute="adminBean">
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
	<tr>
		<td colspan="2">
		<div class="title">
		 Super Admin Home Page</div>
		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">
		<table width="95%">
			<tr>
				<td valign="top">
				<table border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td width="45%" valign="top">
						<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
							<tr>
								<td colspan="50" class="infoTH">Admin Tasks</td>
							</tr>
							<c:if test="${adminBean.hasPendingLeaveBonus}">
								<tr>
									<td class="infoTROdd">
									<a href="${appContext}/viewPendingLeaveBonus.do">[ <c:out value="${adminBean.noOfPendingLeaveBonuses}"/> ]&nbsp;Pending Leave Bonus(es) found.</a><br />
									 View leave bonus(es) awaiting approval. <br />
									</td>
								</tr>
							
							</c:if>
							<c:if test="${adminBean.hasPendingTransfers}">
								<tr>
									<td class="infoTROdd">
									<a href="${appContext}/viewPendingTransfers.do">[ <c:out value="${adminBean.noOfPendingTransfers}"/> ]&nbsp;Pending Transfer(s) found.</a><br />
									 View employee transfers awaiting approval. <br />
									</td>
								</tr>
							
							</c:if>
							<c:if test="${adminBean.hasPendingEmployeeApprovals}">
								<tr>
									<td class="infoTROdd">
									<a href="${appContext}/viewEmpForPayApproval.do">[ <c:out value="${adminBean.noOfPendingEmployeeApprovals}"/> ]&nbsp;Pending Employee Approval(s) found.</a><br />
									 View employee awaiting approval for payrolling. <br />
									</td>
								</tr>
							
							</c:if>
							<c:if test="${adminBean.hasPendingNameConflicts}">
								<tr>
									<td class="infoTROdd">
									<a href="${appContext}/viewPendingConflicts.do">[ <c:out value="${adminBean.noOfPendingNameConflicts}"/> ]&nbsp;Name Conflict(s) found.</a><br />
									 View Employees with name conflicts during creation. <br />
									</td>
								</tr>
							
							</c:if>
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/configurationHome.do">Configuration &amp; Control</a><br />
								 Application configuration and control main page. <br />
								</td>
							</tr>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/createNewUser.do">Create New User</a><br />
								Create a New user for ePayroll Application</td>							    
							</tr>
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/selectUserForRoleChange.do">Change User Role</a><br />
								Change the Role of a user for ePayroll Application</td>							    
							</tr>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/selectUserToDeactivate.do">Deactivate User</a><br />
								Deactivate an existing user of the ePayroll Application <br /></td>
							</tr>
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/selectUserForPasswordReset.do">Reset Password</a><br />
								Reset an existing user's password<br /></td>
							</tr>
							<tr>
						       <td class="infoTROdd">
						       <a href="${appContext}/createSubvention.do">Add a Subvention</a><br />
							    Create a Subvention. <br />
						       </td>
					        </tr>
					        <tr>
								<td class="infoTREven">
								<a href="${appContext}/searchForSubvention.do">Stop/Edit a Subvention</a><br />
								View/Edit/Delete/Expire a Subvention. <br />
								</td>
							</tr>
							<tr>
						 	 <td class="infoTROdd">
								<a href="${appContext}/setMateriality.do">Set/Reset Materiality</a><br />
								Set Materiality value for a User.<br /></td>
							</tr>
							
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/selectLockedAccount.do">Unlock Account</a><br />
								Unlock a locked account.<br /></td>
							</tr>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/auditPageHomeForm.do">Audit Logs</a><br />								
								View Audit Logs<br /></td>
							</tr>
							
							
							
						</table>
						</td>
					</tr>
					<tr>
						<td>
						<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
							<tr>
								<td colspan="50" class="infoTH">Run Payroll</td>
							</tr>
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
									</c:otherwise>
								
								</c:choose>
							
							</c:if>							
						   <c:if test="${not adminBean.approvedPaychecks}">
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/paydayForm.do">Create Paychecks 
								</a><br />
								Run Payroll for employees. </td>
							</tr>
							</c:if>
							
						</table>
						</td>
					</tr>
					
				</table>
				</td>
				<td width="5%">&nbsp;</td>
				<td width="45%" valign="top">
				<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
					<tr>
						<td colspan="50" class="infoTH">Employee Functions</td>
					</tr>
					<tr>
						<td class="infoTROdd">
						<a href="${appContext}/searchEmpForEnquiry.do">Employee Enquiry</a><br />
						 Enquiry View of an existing Employee. <br />
						</td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/searchEmpForEdit.do">Edit Employee</a><br />
						Edit an existing Employee. <br />
						</td>
					</tr>
					<tr>
						<td class="infoTROdd">
						<a href="${appContext}/setUpNewEmployee.do">Add Employee</a><br />
						Add a new Employee.<br />
						</td>
					</tr>
					
					<tr>
						<td class="infoTROdd">
						<a href="${appContext}/searchEmpForSuspension.do">Suspend/Stop Employee Salary</a><br />
						Temporarily Stop Employee Salary.<br />
						</td>
					</tr> 
					                   
					<tr>
						<td class="infoTROdd">
						<a href="${appContext}/searchEmpForPromotion.do">Promote Employee</a><br />
						Promote an existing Employee. <br />
						</td>
					</tr>
					<tr>
								<td class="infoTROdd">
									<a href="${appContext}/searchEmpForTransfer.do">Transfer Employee</a><br />
									Transfer an existing Employee between MDAs or Schools. <br />
									</td>
					</tr>
					<tr>
						  <td class="infoTREven">
								<a href="${appContext}/searchEmpForContract.do">Create Employee Contract</a><br />
								Put Employee(s) on Contract/Internship.<br />
						  </td>
					</tr>
					 
					<c:choose>
						<c:when test="${adminBean.runManualPromotion}">
							<tr>
							  <td class="infoTROdd">
									<a href="${appContext}/doManualStepPromotion.do">Run Yearly Step Promotion</a><br />
									Run Yearly Step Increases except at Bar.<br />
							  </td>
							</tr>
							<tr>
						  		<td class="infoTROdd">
								 	<br>
                  					 <br>
								</td>
							</tr>
							<tr>
						  		<td class="infoTROdd">
								 	<br>
                   					<br>
								 </td>
							</tr>
						 
						</c:when>
						<c:otherwise>
						<tr>
						  		<td class="infoTROdd">
								 	<br>
                  					 <br>
								</td>
							</tr>
						<tr>
						  		<td class="infoTROdd">
								 	<br>
                  					 <br>
								</td>
							</tr>
							<tr>
						  		<td class="infoTROdd">
								 	<br>
                   					<br>
								 </td>
							</tr>
						</c:otherwise>
					
					</c:choose>
					 
					<tr>
						<td>
						<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
							<tr>
								<td colspan="50" class="infoTH">Simulator</td>
							</tr>
							
							<tr>
								<td class="infoTROdd">
									<a href="${appContext}/applyLTGToMABP.do">Apply/Simulate Leave Transport Grant 
								</a><br />
								Simulate/Apply Yearly Leave Transport Grant by Ministry,Agency,Board or Parastatal. </td>
							</tr>
							<tr>
								<td class="infoTREven">
									<a href="${appContext}/prePayrollSimulator.do">Simulate Payroll 
								</a><br />
								Simulate Payroll for budgeting and planning. </td>
							</tr>
							<tr>
								<td class="infoTREven">
									<a href="${appContext}/futuristicPayrollSimulator.do">Futuristic Payroll Run
								</a><br />
								Run Payroll Simulation for a specific time in the future.</td>
							</tr>
						</table>
						</td>
					</tr>
									
				</table>
				</td>
			</tr>
			
		</table>
		
	</table>
</td>
</tr>
<tr>				
<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
</tr>
</table>
</form:form>
</body>

</html>
