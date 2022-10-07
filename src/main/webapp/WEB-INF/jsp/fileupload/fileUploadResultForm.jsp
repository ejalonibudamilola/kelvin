<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>

<html>
<head>
<title>File Upload Summary Page</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
 
<link rel="stylesheet" href="${appContext}/dataTables/css/fixedColumns.dataTables.min.css" type="text/css">
</head>

<style>
    .tableDiv{
        max-width: 1200px;
        overflow-x: auto;
    }

    thead th{
        background-color: #f1f1f1;
    }
    .tableDiv2{
            max-width: 1100px;
            overflow-x: auto;
        }

    thead th{
            background-color: #f1f1f1;
      }
.hline-bottom {
    padding-bottom: 10px;
    border-bottom: 2px solid #000; /* whichever color you prefer */
}

</style>

<body class="main">

	<form:form modelAttribute="fileUploadResult">
		<table class="main" width="70%" border="1" bordercolor="#33c0c8"
			cellspacing="0" cellpadding="0" align="center">

			<%@ include file="/WEB-INF/jsp/headerFile.jsp"%>

			<tr>
				<td>
					<table width="100%" align="center" border="0" cellpadding="0"
						cellspacing="0">

						<tr>
							<td colspan="2">
								<div class="title">
									<c:out value="${fileUploadResult.displayTitle}" />
								</div>
							</td>
						</tr>
						<tr>
							<td valign="top" class="mainBody hundredPercentWidth verticalTopAlign" id="mainbody">



								<table class="reportMain" cellpadding="0" cellspacing="0"
									width="100%">

									<tr>
										<td><c:choose>
												<c:when test="${saved}">
													<h3>
														<c:out value="${fileUploadResult.displayErrors}" />
														&nbsp;<font color="green"><b><c:out
																value="${fileUploadResult.successfulRecords}" /></b></font>&nbsp;Total Record(s) saved
													</h3>

												</c:when>
												<c:otherwise>
													<h4>
														<font color="blue"><b><c:out value="${fileUploadResult.successfulRecords}" /></b></font>&nbsp; <c:out value="${fileUploadResult.displayErrors}" /><p/>
														<font color="blue"><b><c:out value="${fileUploadResult.totalNumberOfRecords}" /></b></font>&nbsp; Records Parsed.

														<br/>
													</h4>

												</c:otherwise>
											</c:choose> </td>
									</tr>
								</table>
								<div class="panel-body">
								    <div class="tableDiv">
									<table id="userTable" class="hundredPercentWidth table display" cellspacing="0" cellpadding="0">
                                        <c:if test="${fileUploadResult.salaryInfo}">
											<h2 style="font-size: 15px">
											Salary Type: <c:out value="${fileUploadResult.payTypeName}" />
											</h2>
											<thead>
												<tr><th class="tenPercentWidth">Level And Step</th>
													<th class="twentyPercentWidth">Basic Salary</th>
													<th class="twentyPercentWidth">Monthly Gross</th>
													<th class="twentyPercentWidth">Annual Gross</th>
													<th class="twentyPercentWidth">Rent</th>
													<th class="twentyPercentWidth">Transport</th>
													<th class="twentyPercentWidth">Utility</th>
													<th class="twentyPercentWidth">Meal</th>
													<th class="twentyPercentWidth">Motor Vehicle</th>
													<th class="twentyPercentWidth">Exam Allowance</th>
													<th class="twentyPercentWidth">LCons. Allowance</th>
													<th class="twentyPercentWidth">Cons. Allowance</th>
													<th class="twentyPercentWidth">Outfit Allowance</th>
													<th class="twentyPercentWidth">Quarters Allowance</th>
													<th class="twentyPercentWidth">Spa Allowance</th>
													<th class="twentyPercentWidth">Responsibility Allowance</th>
													<th class="twentyPercentWidth">Security Allowance</th>
													<th class="twentyPercentWidth">Swes Allowance</th>
													<th class="twentyPercentWidth">Research Allowance</th>
													<th class="twentyPercentWidth">Totor Allowance</th>
													<th class="twentyPercentWidth">Medical Allowance</th>
													<th class="twentyPercentWidth">Sitting Allowance</th>
													<th class="twentyPercentWidth">Overtime Allowance</th>
													<th class="twentyPercentWidth">Tools Torch Light Allowance</th>
													<th class="twentyPercentWidth">Uniform Allowance</th>
													<th class="twentyPercentWidth">Teaching Allowance</th>
													<th class="twentyPercentWidth">Enhanced Health Allowance</th>
                                                    <th class="twentyPercentWidth">Special Health Allowance</th>
                                                    <th class="twentyPercentWidth">Shift Duty Allowance</th>
                                                    <th class="twentyPercentWidth">Special Allowance</th>
                                                    <th class="twentyPercentWidth">Admin Allowance</th>
                                                    <th class="twentyPercentWidth">Call Duty</th>
                                                    <th class="twentyPercentWidth">Domestic Servant</th>
 													<th class="twentyPercentWidth">Drivers Allowance</th>
 													<th class="twentyPercentWidth">Entertainment</th>
                                                    <th class="twentyPercentWidth">Furniture</th>
                                                    <th class="twentyPercentWidth">Hazard</th>
                                                    <th class="twentyPercentWidth">Inducement</th>
                                                    <th class="twentyPercentWidth">Journal</th>
                                                    <th class="twentyPercentWidth">Nurse Other Allowance</th>
                                                    <th class="twentyPercentWidth">Rural Posting</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach items="${fileUploadResult.payGroupList}" var="innerBean" varStatus="gridRow">
													<tr>
														<td class=""><c:out value="${innerBean.levelAndStepAsStr}" /></td>
														<td class=""><c:out value="${innerBean.monthlyBasicSalaryStr}" /></td>
														<td class=""><c:out value="${innerBean.monthlyGrossPayWivNairaStr}" /></td>
														<td><c:out value="${innerBean.annualSalaryStrWivNaira}" /></td>
														<td><c:out value="${innerBean.rentStr}" /></td>
														<td><c:out value="${innerBean.transportStr}" /></td>
														<td><c:out value="${innerBean.utilityStr}" /></td>
														<td><c:out value="${innerBean.mealStr}" /></td>
														<td><c:out value="${innerBean.motorVehicleStr}" /></td>
														<td><c:out value="${innerBean.examAllowanceStr}" /></td>
														<td><c:out value="${innerBean.lcosAllowanceStr}" /></td>
														<td><c:out value="${innerBean.consAllowanceStr}" /></td>
														<td><c:out value="${innerBean.outfitAllowanceStr}" /></td>
														<td><c:out value="${innerBean.quartersAllowanceStr}" /></td>
														<td><c:out value="${innerBean.spaAllowanceStr}" /></td>
														<td><c:out value="${innerBean.responsibilityAllowanceStr}" /></td>
														<td><c:out value="${innerBean.securityAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.swesAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.researchAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.totorAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.medicalAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.sittingAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.overtimeAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.toolsTorchLightAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.uniformAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.teachingAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.enhancedHealthAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.specialHealthAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.shiftDutyStr}" /></td>
													    <td><c:out value="${innerBean.specialistAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.adminAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.callDutyStr}" /></td>
													    <td><c:out value="${innerBean.domesticServantStr}" /></td>
													    <td><c:out value="${innerBean.driversAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.entertainmentStr}" /></td>
													    <td><c:out value="${innerBean.furnitureStr}" /></td>
													    <td><c:out value="${innerBean.hazardStr}" /></td>
													    <td><c:out value="${innerBean.inducementStr}" /></td>
													    <td><c:out value="${innerBean.journalStr}" /></td>
													    <td><c:out value="${innerBean.nurseOtherAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.ruralPostingStr}" /></td>
													</tr>
												</c:forEach>
											</tbody>
										</c:if>
                                        <c:if test="${fileUploadResult.stepIncrement}">
										
											<br />
											<thead>
												<tr>
													<th class="fivePercentWidth"><spring:message
															code="table.serialNo" /></th>
													<th class="tenPercentWidth"><c:out value="${roleBean.staffTitle}"/></th>
													<th class="twentyPercentWidth"><c:out value="${roleBean.staffTypeName}"/></th>
													<th class="twentyPercentWidth"><c:out value="${roleBean.mdaTitle}"/></th>
													<th class="tenPercentWidth">Pay Group</th>
													<th class="tenPercentWidth">From Level/Step</th>
													<th class="tenPercentWidth">To Level Step </th>

												</tr>
											</thead>
											<tbody>
												<c:forEach items="${fileUploadResult.listToSave}" var="innerBean"
													varStatus="gridRow">
													<tr>
														<td><c:out value="${gridRow.index + 1}" /></td>
														<td><c:out value="${innerBean.staffId}" /></td>
														<td><c:out value="${innerBean.name}" /></td>
														<td><c:out value="${innerBean.organization}" /></td>
														<td><c:out value="${innerBean.payTypeName}" /></td>
														<td><c:out value="${innerBean.deductionAmountStr}" /></td>
														<td><c:out value="${innerBean.objectCode}" /></td>
													</tr>
												</c:forEach>
											</tbody>
										</c:if>
										<c:if test="${fileUploadResult.deduction}">
											<br />
											<thead>
												<tr>
													<th class="fivePercentWidth"><spring:message
															code="table.serialNo" /></th>
													<th class="tenPercentWidth"><c:out value="${roleBean.staffTitle}"/></th>
													<th class="twentyPercentWidth"><c:out value="${roleBean.staffTypeName}"/></th>
													<th class="twentyPercentWidth"><c:out value="${roleBean.mdaTitle}"/></th>
													<th class="tenPercentWidth">Pay Group</th>
													<th class="tenPercentWidth"><c:out value="${fileUploadResult.name}" />&nbsp;<spring:message
															code="table.code" /></th>
													<th class="tenPercentWidth"><c:out
														value="${fileUploadResult.name}" />&nbsp;<spring:message
															code="table.amount" /></th>
													<th class="fifteenPercentWidth"><c:out
														value="${fileUploadResult.name}" />&nbsp;<spring:message
															code="table.period" /></th>

												</tr>
											</thead>
											<tbody>
												<c:forEach items="${fileUploadResult.listToSave}" var="innerBean"
													varStatus="gridRow">
													<tr>
														<td><c:out value="${gridRow.index + 1}" /></td>
														<td><c:out value="${innerBean.staffId}" /></td>
														<td><c:out value="${innerBean.name}" /></td>
														<td><c:out value="${innerBean.organization}" /></td>
														<td><c:out value="${innerBean.payTypeName}" /></td>
														<td><c:out value="${innerBean.objectCode}" /></td>
														<td><c:out value="${innerBean.deductionAmountStr}" /></td>
														<td><c:out value="${innerBean.startAndEndPeriod}" /></td>
													</tr>
												</c:forEach>
											</tbody>
										</c:if>
										<c:if test="${fileUploadResult.loan}">
											 
											<br />
											<thead>
												<tr>
													<th class="fivePercentWidth"><spring:message code="table.serialNo" /></th>
													<th class="tenPercentWidth"><c:out value="${roleBean.staffTitle}"/></th>
													<th class="twentyPercentWidth"><c:out value="${roleBean.staffTypeName}"/></th>
													<th class="tenPercentWidth"><c:out value="${fileUploadResult.name}" />&nbsp;<spring:message code="table.code" /></th>
													<th class="tenPercentWidth"><c:out value="${fileUploadResult.name}" />&nbsp;<spring:message code="table.amount" /></th>
													<th class="tenPercentWidth"><c:out value="${fileUploadResult.name}" />&nbsp;<spring:message code="table.period" /></th>

												</tr>
											</thead>
											<tbody>
												<c:forEach items="${fileUploadResult.listToSave}" var="innerBean" varStatus="gridRow">
													<tr>
														<td><c:out value="${gridRow.index + 1}" /></td>
														<td><c:out value="${innerBean.staffId}" /></td>
														<td><c:out value="${innerBean.name}" /></td>
														<td><c:out value="${innerBean.objectCode}" /></td>
														<td><c:out value="${innerBean.loanBalance}" /></td>
														<td><c:out value="${innerBean.tenor}" /></td>
													</tr>
												</c:forEach>
											</tbody>
										</c:if>
										 
										<c:if test="${fileUploadResult.leaveBonus}">
											 
											<br />
											<thead>
												<tr>
													<th class="fivePercentWidth"><spring:message
															code="table.serialNo" /></th>
													<th class="tenPercentWidth"><spring:message
															code="table.empId" /></th>
													<th class="twentyPercentWidth"><spring:message
															code="table.staffName" /></th>
													<th class="tenPercentWidth"><spring:message
															code="table.amount" /></th>
													 
												</tr>
											</thead>
											<tbody>
												<c:forEach items="${fileUploadResult.listToSave}" var="innerBean"
													varStatus="gridRow">
													<tr>
														<td><c:out value="${gridRow.index + 1}" /></td>
														<td><c:out value="${innerBean.staffId}" /></td>
														<td><c:out value="${innerBean.name}" /></td>
														<td><c:out value="${innerBean.deductionAmount}" /></td>
														<td><c:out value="${innerBean.reportType}" /></td>
													 
													</tr>
												</c:forEach>
											</tbody>
											 
										</c:if>

										<c:if test="${fileUploadResult.specialAllowance}">
										 
											<br />
											<thead>
												<tr>
													<th class="fivePercentWidth"><spring:message
															code="table.serialNo" /></th>
													<th class="fivePercentWidth"><spring:message
															code="table.empId" /></th>
													<th class="twentyPercentWidth"><spring:message
															code="table.staffName" /></th>
													<th class="fivePercentWidth"><spring:message
															code="stats.specAllow" /></th>
													<th class="fivePercentWidth"><spring:message
															code="table.amount" /></th>
													<th class="fivePercentWidth"><spring:message
															code="table.startDate" /></th>
													<th class="fivePercentWidth"><spring:message
															code="table.endDate" /></th>

												</tr>
											</thead>
											 <tbody>
												<c:forEach items="${fileUploadResult.listToSave}" var="innerBean"
													varStatus="gridRow">
													<tr>
														<td><c:out value="${gridRow.index + 1}" /></td>
														<td><c:out value="${innerBean.staffId}" /></td>
														<td><c:out value="${innerBean.name}" /></td>
														<td><c:out value="${innerBean.objectCode}" /></td>
														<td><c:out value="${innerBean.allowanceAmount}" /></td>
														<td><c:out value="${innerBean.startDateStr}" /></td>
														<td><c:out value="${innerBean.endDateStr}" /></td>
													 
													</tr>
												</c:forEach>
											</tbody>
											 
										</c:if>
										<c:if test="${fileUploadResult.bankBranch}">
											 
											<br />
											<thead>
												<tr>
													<th class="fivePercentWidth"><spring:message
															code="table.serialNo" /></th>
													<th class="fivePercentWidth"><spring:message
															code="table.empId" /></th>
													<th class="twentyPercentWidth"><spring:message
															code="table.staffName" /></th>
													<th class="fivePercentWidth"><spring:message
															code="table.bankSortCode" /></th>
													<th class="fivePercentWidth"><spring:message
															code="table.branchSortCode" /></th>
													<th class="fivePercentWidth"><spring:message
															code="table.accountNo" /></th>
												 
												</tr>
											</thead>
											  <tbody>
												<c:forEach items="${fileUploadResult.listToSave}" var="innerBean"
													varStatus="gridRow">
													<tr>
														<td><c:out value="${gridRow.index + 1}" /></td>
														<td><c:out value="${innerBean.staffId}" /></td>
														<td><c:out value="${innerBean.name}" /></td>
														<td><c:out value="${innerBean.bankSortCode}" /></td>
														<td><c:out value="${innerBean.bankBranchSortCode}" /></td>
														<td><c:out value="${innerBean.mode}" /></td>
													 
													</tr>
												</c:forEach>
											</tbody>
											 
											 
										    </c:if>
										    </table>

										 </div>
										 <div>
                                              <c:choose>
													<c:when test="${saved}">
														<input type="image" name="_cancel" value="cancel" title="Close Report" src="images/close.png">
													</c:when>
													<c:otherwise>
                                                        <c:if test="${fileUploadResult.salaryInfo and (not fileUploadResult.hasErrorList)}">
															<input type="image" name="_create" value="create" title="Add Salary Structure Values" src="images/add_pay_group.png">&nbsp;
                 	                                    </c:if>
                                                        <c:if test="${fileUploadResult.stepIncrement and (not fileUploadResult.doNotShowButton)}">
															<input type="image" name="_create" value="create" title="Do Step Increment" src="images/create_h.png">&nbsp;
                 	                                    </c:if>
														<c:if test="${fileUploadResult.deduction and (not fileUploadResult.doNotShowButton)}">
															<input type="image" name="_create" value="create" title="Add/Edit/Stop Deductions" src="images/addDeduction.png">&nbsp;
                 	                                    </c:if>
														<c:if test="${fileUploadResult.loan and (not fileUploadResult.doNotShowButton)}">
															<input type="image" name="_create" value="create" title="Add/Edit/Stop Loans" src="images/addLoan.png">&nbsp;
                 	                                    </c:if>
														<c:if test="${fileUploadResult.specialAllowance and (not fileUploadResult.doNotShowButton)}">
															<input type="image" name="_create" value="create" title="Add/Edit/Stop Special Allowances" src="images/addSpecAllow.png">&nbsp;
                 	                                    </c:if>
														<c:if test="${fileUploadResult.leaveBonus}">
															<input type="image" name="_create" value="create" title="Add Leave Bonus" src="images/addLeaveBonus.png">&nbsp;
                 	                                    </c:if>

														<c:if test="${fileUploadResult.bankBranch}">
															<input type="image" name="_create" value="create" title="Update Bank Details" src="images/updateBankInfo.png">&nbsp;
                 	                                    </c:if>
                                                        <c:if test="${not fileUploadResult.hasErrorList}">
                                                            <input type="image" name="_cancel" value="cancel" title="Cancel Fileupload" src="images/cancel_h.png">
                 	                                    </c:if>
                 	                            </c:otherwise>
                 	                           </c:choose>
										 </div>
										 </div>

										 
						     <c:if test="${fileUploadResult.hasErrorList}">
                                   <div class="block_1 hline-bottom"></div>
                                 <h3>

 									 <font color="red"><b><c:out value="${fileUploadResult.failedRecords}" /></b></font> &nbsp; Error Records Detected.

 									 <br/>
 								 </h3>
                                 <div class="tableDiv2">

									<table id="userTable2" class="hundredPercentWidth table display" cellspacing="0" cellpadding="0">
                                        <c:if test="${fileUploadResult.salaryInfo}">
											<h2 style="font-size: 15px">
											Salary Type: <c:out value="${fileUploadResult.payTypeName}" />
											</h2>
											<thead>
												<tr><th class="fivePercentWidth">Grade Level</th>
													<th class="fivePercentWidth">Step</th>
													<th class="twentyPercentWidth">Error Details</th>
													 
												</tr>
											</thead>
											<tbody>
												<c:forEach items="${fileUploadResult.errorList}" var="innerBean" varStatus="gridRow">
													<tr>
														<td class=""><c:out value="${innerBean.name}" /></td>
														<td class=""><c:out value="${innerBean.staffId}" /></td>
														<td class=""><c:out value="${innerBean.displayErrors}" /></td>
													</tr>
												</c:forEach>
											</tbody>
										</c:if>
                                        <c:if test="${fileUploadResult.stepIncrement}">
										
											<br />
											<thead>
												<tr>
													<th class="fivePercentWidth"><spring:message
															code="table.serialNo" /></th>
													<th class="tenPercentWidth"><c:out value="${roleBean.staffTitle}"/></th>
													<th class="twentyPercentWidth"><c:out value="${roleBean.staffTypeName}"/></th>
													<th class="tenPercentWidth">Pay Group</th>
													<th class="tenPercentWidth">Current Level/Step</th>
													<th class="tenPercentWidth">Step Increment Level/Step </th>
													<th class="twentyPercentWidth">Error Details </th>
												</tr>
											</thead>
											<tbody>
												<c:forEach items="${fileUploadResult.errorList}" var="innerBean"
													varStatus="gridRow">
													<tr>
														<td><c:out value="${gridRow.index + 1}" /></td>
														<td><c:out value="${innerBean.staffId}" /></td>
														<td><c:out value="${innerBean.name}" /></td>
														<td><c:out value="${innerBean.payTypeName}" /></td>
														<td><c:out value="${innerBean.levelAndStep}" /></td>
														<td><c:out value="${innerBean.objectCode}" /></td>
														<td><c:out value="${innerBean.displayErrors}" /></td>													
													</tr>
												</c:forEach>
											</tbody>
										</c:if>
										<c:if test="${fileUploadResult.deduction}">
											
											<br />
											<thead>
												<tr>
													<th class="fivePercentWidth"><spring:message
															code="table.serialNo" /></th>
													<th class="fivePercentWidth"><c:out value="${roleBean.staffTitle}"/></th>
													<th class="twentyPercentWidth"><c:out value="${roleBean.staffTypeName}"/></th>
													<th class="fivePercentWidth">Deduction Code</th>
													<th class="fivePercentWidth">Deduction Amount</th>
													<th class="twentyPercentWidth">Error Details</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach items="${fileUploadResult.errorList}" var="innerBean"
													varStatus="gridRow">
													<tr>
														<td><c:out value="${gridRow.index + 1}" /></td>
														<td><c:out value="${innerBean.staffId}" /></td>
														<td><c:out value="${innerBean.name}" /></td>
														<td><c:out value="${innerBean.objectCode}" /></td>
														<td><c:out value="${innerBean.deductionAmount}" /></td>
														<td><c:out value="${innerBean.displayErrors}" /></td>
													 </tr>
												</c:forEach>
											</tbody>
										</c:if>
										<c:if test="${fileUploadResult.loan}">
											 
											<br />
											<thead>
												<tr>
													<th class="fivePercentWidth"><spring:message code="table.serialNo" /></th>
													<th class="tenPercentWidth"><c:out value="${roleBean.staffTitle}"/></th>
													<th class="twentyPercentWidth"><c:out value="${roleBean.staffTypeName}"/></th>
													<th class="tenPercentWidth">Loan Code</th>
													<th class="tenPercentWidth">Loan Amount</th>
													<th class="fivePercentWidth">Tenor</th>
													<th class="twentyPercentWidth">Error Details</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach items="${fileUploadResult.errorList}" var="innerBean" varStatus="gridRow">
													<tr>
														<td><c:out value="${gridRow.index + 1}" /></td>
														<td><c:out value="${innerBean.staffId}" /></td>
														<td><c:out value="${innerBean.name}" /></td>
														<td><c:out value="${innerBean.objectCode}" /></td>
														<td><c:out value="${innerBean.loanBalance}" /></td>
														<td><c:out value="${innerBean.tenor}" /></td>
														<td><c:out value="${innerBean.displayErrors}" /></td>
													</tr>
												</c:forEach>
											</tbody>
										</c:if>
										 
										<c:if test="${fileUploadResult.leaveBonus}">
											 
											<br />
											<thead>
												<tr>
													<th class="fivePercentWidth"><spring:message
															code="table.serialNo" /></th>
													<th class="tenPercentWidth"><c:out value="${roleBean.staffTitle}"/></th>
													<th class="twentyPercentWidth"><c:out value="${roleBean.staffTypeName}"/></th>
													<th class="tenPercentWidth"><c:out value="${roleBean.mdaTitle}"/></th>
													<th class="tenPercentWidth">Amount</th>
													<th class="tenPercentWidth">Year</th>
													<th class="tenPercentWidth">Error Details</th>
                                                </tr>
											</thead>
											<tbody>
												<c:forEach items="${fileUploadResult.errorList}" var="innerBean"
													varStatus="gridRow">
													<tr>
														<td><c:out value="${gridRow.index + 1}" /></td>
														<td><c:out value="${innerBean.staffId}" /></td>
														<td><c:out value="${innerBean.name}" /></td>
														<td><c:out value="${innerBean.objectCode}" /></td>
														<td><c:out value="${innerBean.enteredCaptcha}" /></td>
														<td><c:out value="${innerBean.startDateStr}" /></td>
														<td><c:out value="${innerBean.displayErrors}" /></td>
													</tr>
												</c:forEach>
											</tbody>
											 
										</c:if>

										<c:if test="${fileUploadResult.specialAllowance}">
										 
											<br />
											<thead>
												<tr>
													<th class="fivePercentWidth"><spring:message code="table.serialNo" /></th>
													<th class="fivePercentWidth"><c:out value="${roleBean.staffTitle}"/></th>
													<th class="twentyPercentWidth">Special Allow.</th>
													<th class="fivePercentWidth"><spring:message code="table.amount" /></th>
													<th class="fivePercentWidth"><spring:message code="table.startDate" /></th>
													<th class="fivePercentWidth"><spring:message code="table.endDate" /></th>
													<th class="twentyPercentWidth">Error Details</th>
                                                 </tr>

											</thead>
											 <tbody>
												<c:forEach items="${fileUploadResult.errorList}" var="innerBean"
													varStatus="gridRow">
													<tr>
														<td><c:out value="${gridRow.index + 1}" /></td>
														<td><c:out value="${innerBean.staffId}" /></td>
														<td><c:out value="${innerBean.name}" /></td>
														<td><c:out value="${innerBean.objectCode}" /></td>
														<td><c:out value="${innerBean.allowanceAmount}" /></td>
														<td><c:out value="${innerBean.allowanceStartDateStr}" /></td>
														<td><c:out value="${innerBean.allowanceEndDateStr}" /></td>
														<td><c:out value="${innerBean.displayErrors}" /></td>
 													</tr>
												</c:forEach>
											</tbody>
											 
										</c:if>

									</table>

									 <div>
                                    	 <a class="reportLink" href="${appContext}/exportFileUploadResultExcel.do?uid=${uid}"> Export Errors to Microsoft Excel&copy; </a>
                                     </div>
                                     <br/>
                                     <br/>
									 <div>
									    <c:if test="${not saved}">
										   <input type="image" name="_edit" value="edit" title="Try and fix errors" src="images/fixError.png">&nbsp;
										</c:if>
										<input type="image" name="_cancel" value="cancel" title="Cancel Fileupload" src="images/cancel_h.png">
									 </div>
									 <br/>
									 <br/>

                                  </c:if>
						          

								 <tr>
                                       <%@ include file="/WEB-INF/jsp/footerFile.jsp"%>
                                  </tr>
								</table>

							</td>

						</tr>

					</table>
		 
			
	 
	</form:form>
	<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/dataTables/js/dataTables.fixedColumns.min.js"/>"></script>
	<script type="text/javascript">

      $(document).ready(function() {
        var table = $('#userTable').DataTable( {
            "order": [],

            scrollX:        true,
            scrollCollapse: true,
            paging:         true,
            fixedColumns:   {
                leftColumns: 3
          },

        });
       var table = $('#userTable2').DataTable( {
                      "order": [],
					  scrollX:        true,
            scrollCollapse: true,
            paging:         true,
            fixedColumns:   {
                leftColumns: 2
          },

         });
      });

	</script>
</body>

</html>
