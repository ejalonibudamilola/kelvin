<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title>Payroll Run Statistical Summary</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>

</head>

<body class="main">
<form:form modelAttribute="statBean">
<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
	<tr>
		<td>
		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			
			<tr>
				<td colspan="2">
				<div class="title">
					<c:out value="${roleBean.businessName}"/> - <br>
					Payroll Run Statistics - <c:out value="${statBean.monthAndYearStr}"/></div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainBody" id="mainBody">
				
				<span class="reportTopPrintLink">
				<a href="${appContext}/payrollStatSummary.do?rm=${statBean.runMonth}&ry=${statBean.runYear}">
				View in Excel </a><br />
				</span>
				<div class="reportDescText">
					This report shows the statistics for a payroll run for a particular month and year.
					<br />
				</div>
				<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
					
					<tr>
								<td class="reportFormControls">
								Payroll Run Month&nbsp;<form:select path="runMonth">
												<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<c:forEach items="${monthList}" var="mList">
												<form:option value="${mList.id}">${mList.name}</form:option>
												</c:forEach>
												</form:select>
								&nbsp;&nbsp;Payroll Run Year&nbsp;<form:select path="runYear">
												<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<c:forEach items="${yearList}" var="yList">
												<form:option value="${yList.id}">${yList.id}</form:option>
												</c:forEach>
												</form:select>
								</td>
								
								
								
								
								
					</tr>
					<tr>
                     <td class="buttonRow" align="left">
                       <input type="image" name="_update" title="Update Report" src="images/Update_Report_h.png">
                     </td>
                    </tr>
					<tr>
					
						<td class="reportFormControlsSpacing"></td>
					</tr>
					<tr>
						<td>

						    <c:if test="${not no_recs}">
						        <h3><c:out value="${roleBean.businessName}"/> Payroll Run Statistic for <c:out value="${statBean.monthAndYearStr}"/> </h3>
                                <br />
                                <br />
                                        <table>
                                            <tr align="left">
                                                <td class="activeTH" colspan="2">Statistics Overview</td>
                                            </tr>

                                            <tr>
                                                <td class="activeTD">
                                                 <c:out value="${roleBean.staffTypeName}"/> Processed
                                                </td>
                                                <td class="activeTD">
                                                    <c:out value="${statBean.noOfEmployeesProcessed}"/>
                                                </td>
                                            </tr>
                                    
                                            <tr>
                                                <td class="activeTD">
                                                    <c:out value="${roleBean.staffTypeName}"/> Paid
                                                </td>
                                                <td class="activeTD">
                                                    <c:out value="${statBean.noOfEmployeesPaid}"/>
                                                </td>
                                            </tr>
                                            <tr >
                                               <td class="activeTD">
                                                    <c:out value="${roleBean.staffTypeName}"/> Not Paid
                                                </td>
                                                <td class="activeTD">
                                                    <c:out value="${statBean.noOfEmployeesNotPaid}"/>
                                                </td>
                                            </tr>
                                            <tr >
                                               <td class="activeTD">
                                                    Payroll Run By
                                               </td>
                                               <td class="activeTD">
                                                    <c:out value="${statMasterBean.initiator.actualUserName}"/>
                                               </td>
                                            </tr>
                                            <tr>
                                               <td class="activeTD">
                                                    Payroll Run Date
                                               </td>
                                               <td class="activeTD">
                                                    <c:out value="${statMasterBean.payrollRunStartDateStr}"/>
                                               </td>
                                            </tr>
                                            <tr>
                                               <td class="activeTD">
                                                    Payroll Run Duration
                                               </td>
                                               <td class="activeTD">
                                                    <c:out value="${statMasterBean.payrollRunTime}"/>
                                               </td>
                                            </tr>
                                            <c:if test="${statMasterBean.approved}">
                                                <tr>
                                                   <td class="activeTD">
                                                        Approved By
                                                   </td>
                                                   <td class="activeTD">
                                                      <c:out value="${statMasterBean.approver.actualUserName}"/>
                                                   </td>
                                                </tr>
	                                            <tr>
	                                                <td class="activeTD">
	                                                    Approval Date
	                                                </td>
	                                                <td class="activeTD">
	                                                    <c:out value="${statMasterBean.approvalTime}"/>
	                                                </td>
	                                            </tr>
                                            </c:if>

                                         <tr align="left">
                                           <td class="activeTH" colspan="2">Configuration Impact</td>
                                         </tr>

                                         <tr>

                                           <td class="activeTD">
                                             BVN Required
                                           </td>
                                           <td class="activeTD">
                                              <c:out value="${configImpact.usingBvn}"/>
                                           </td>
                                         </tr>

                                         <tr>
                                           <td class="activeTD">
                                             Biometrics Info Required
                                           </td>
                                           <td class="activeTD">
                                             <c:out value="${configImpact.usingBiometricInfo}"/>
                                           </td>
                                         </tr>
                                         <tr>
                                           <td class="activeTD">
                                             NIN Required
                                           </td>
                                           <td class="activeTD">
                                             <c:out value="${configImpact.usingNin}"/>
                                           </td>
                                         </tr>
                                         <tr>
                                           <td class="activeTD">
                                             Residence ID Required
                                           </td>
                                           <td class="activeTD">
                                             <c:out value="${configImpact.usingResidenceId}"/>
                                           </td>
                                         </tr>
                                         <c:choose>
                                            <c:when test="${roleBean.pensioner}">
                                               <tr>
                                                  <td class="activeTD">
                                                    Using 'I Am Alive'
                                                  </td>
                                                  <td class="activeTD">
                                                     <c:out value="${configImpact.usingAmAlive}"/>
                                                  </td>
                                               </tr>
                                               <c:if test="${configImpact.useIAmAlive}">
                                                 <tr>
                                                   <td class="activeTD">
                                                     Am Alive Extension
                                                   </td>
                                                   <td class="activeTD">
                                                     <c:out value="${configImpact.iamAliveExt}"/>
                                                   </td>
                                                 </tr>
                                               </c:if>
                                            </c:when>
                                            <c:otherwise>
                                               <tr>
                                                  <td class="activeTD">
                                                    Service Length
                                                  </td>
                                                  <td class="activeTD">
                                                     <c:out value="${configImpact.serviceLength}"/>
                                                  </td>
                                               </tr>

                                                 <tr>
                                                   <td class="activeTD">
                                                     Retirement Age
                                                   </td>
                                                   <td class="activeTD">
                                                     <c:out value="${configImpact.ageAtRetirement}"/> Years.
                                                   </td>
                                                 </tr>

                                            </c:otherwise>
                                         </c:choose>
                                     </table>
                                     <br/>
                            </c:if>
                        <c:choose>
                        	<c:when test="${not no_recs}">
						<table class="report" cellspacing="0" cellpadding="0">
							<tr class="reportOdd header">
								<td class="tableCell" align="center" valign="top">
								S/No.</td>
								<td class="tableCell" align="center" valign="top">
								Statistic</td>
								<td class="tableCell" align="center" valign="top">
								No Of <c:out value="${roleBean.staffTypeName}"/>s</td>
								<td class="tableCell" align="center" valign="top">
								% of Total <c:out value="${roleBean.staffTypeName}"/>s</td>
								<td class="tableCell" align="center" valign="top">
								Net Pay</td>		
								<td class="tableCell" align="center" valign="top">
								Gross Pay</td>						
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
													
							</tr>
							<tr class="reportOdd">
								<td class="tableCell" valign="top">1</td>
								<td class="tableCell" align="left" valign="top"><c:out value="${roleBean.staffTypeName}"/>s Paid</td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.noOfEmployeesPaid}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.percentageOfEmployees}"/></td>
								<td class="tableCell" align="right" valign="top">₦<c:out value="${statBean.netPaySumStr}"/></td>
								<td class="tableCell" align="right" valign="top">₦<c:out value="${statBean.totalPaySumStr}"/></td>
								<td class="tableCell" align="right" valign="top"><a href="${appContext}/showStatDetails.do?sc=1&rm=${statBean.runMonth}&ry=${statBean.runYear}" 
								  target="_blank" onclick="popup(this.href,${roleBean.staffTypeName} Paid);return false;">Details</a></td>
								 
							</tr>
							<c:if test="${statBean.employeesNotPaidByBirthDate gt 0 }">
							<tr class="reportOdd">
								<td class="tableCell" valign="top"><c:out value="${statBean.noOfEmpRetiredByBirthDate}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${roleBean.staffTypeName}"/>s Not Paid (Birth Date)</td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.employeesNotPaidByBirthDate}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.percentageOfEmpForBirthDate}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.zeroNaira}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.zeroNaira}"/></td>
								<td class="tableCell" align="right" valign="top"><a href="${appContext}/showStatDetails.do?sc=2&rm=${statBean.runMonth}&ry=${statBean.runYear}" 
								 target="_blank"onclick="popup(this.href,${roleBean.staffTypeName} Not Paid (Date of Birth));return false;">Details</a></td>
								 
							</tr>
							</c:if>	
							<c:if test="${statBean.employeesNotPaidByHireDate gt 0 }">
							<tr class="reportOdd">
								<td class="tableCell" valign="top"><c:out value="${statBean.noOfEmpRetiredByHireDate}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${roleBean.staffTypeName}"/>s Not Paid (Hire Date)</td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.employeesNotPaidByHireDate}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.percentageOfEmpForHireDate}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.zeroNaira}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.zeroNaira}"/></td>
								<td class="tableCell" align="right" valign="top"><a href="${appContext}/showStatDetails.do?sc=3&rm=${statBean.runMonth}&ry=${statBean.runYear}" 
								 target="_blank"onclick="popup(this.href,${roleBean.staffTypeName} Not Paid (Date of Hire));return false;">Details</a></td>
								 
							</tr>
							</c:if>		
							<c:if test="${statBean.employeesPaidByContract gt 0 }">
							<tr class="reportOdd">
								<td class="tableCell" valign="top"><c:out value="${statBean.noOfEmpPaidByContract}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${roleBean.staffTypeName}"/>s On Contract (Paid)</td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.employeesPaidByContract}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.percentageOfEmpPaidByContract}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.netPayContractStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.totalPayContractStr}"/></td>
								<td class="tableCell" align="right" valign="top"><a href="${appContext}/showStatDetails.do?sc=5&rm=${statBean.runMonth}&ry=${statBean.runYear}" 
								 target="_blank"onclick="popup(this.href,${roleBean.staffTypeName} Paid (Contracted Employees));return false;">Details</a></td>
								 
							</tr>
							</c:if>	
							<c:if test="${statBean.employeesNotPaidByContract gt 0 }">
							<tr class="reportOdd">
								<td class="tableCell" valign="top"><c:out value="${statBean.noOfEmpNotPaidByContract}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${roleBean.staffTypeName}"/>s On Contract (Not Paid)</td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.employeesNotPaidByContract}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.percentageOfEmpNotPaidByContract}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.zeroNaira}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.zeroNaira}"/></td>
								<td class="tableCell" align="right" valign="top"><a href="${appContext}/showStatDetails.do?sc=13&rm=${statBean.runMonth}&ry=${statBean.runYear}" 
								 target="_blank"onclick="popup(this.href,${roleBean.staffTypeName} Not Paid (Expired Contract));return false;">Details</a></td>
								 
							</tr>
							</c:if>
                            <c:if test="${statBean.noOfEmployeesPaidByDays gt 0 }">
							<tr class="reportOdd">
								<td class="tableCell" valign="top"><c:out value="${statBean.noOfEmpPaidByDays}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${roleBean.staffTypeName}"/>s Paid By Days</td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.noOfEmployeesPaidByDays}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.percentageOfEmpPaidByDays}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.netPayByDaysStr}"/></td>
								<td class="tableCell" align="right" valign="top">₦<c:out value="${statBean.totalPayByDaysStr}"/></td>
								<td class="tableCell" align="right" valign="top"><a href="${appContext}/showStatDetails.do?sc=8&rm=${statBean.runMonth}&ry=${statBean.runYear}"
								 target="_blank"onclick="popup(this.href,${roleBean.staffTypeName} Paid By Days Worked);return false;">Details</a></td>

							</tr>
							</c:if>
                            <c:if test="${statBean.employeesNotPaidByTermination gt 0 }">
							<tr class="reportOdd">
								<td class="tableCell" valign="top"><c:out value="${statBean.noOfEmpRetiredByTermination}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${roleBean.staffTypeName}"/>s Not Paid (Terminated)</td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.employeesNotPaidByTermination}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.percentageOfEmpRetiredByTermination}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.zeroNaira}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.zeroNaira}"/></td>
								<td class="tableCell" align="right" valign="top"><a href="${appContext}/showStatDetails.do?sc=11&rm=${statBean.runMonth}&ry=${statBean.runYear}"
								 target="_blank"onclick="popup(this.href,${roleBean.staffTypeName} Not Paid (Termination Candidates));return false;">Details</a></td>

							</tr>
							</c:if>
							<c:if test="${statBean.employeesNotPaidBySuspension gt 0 }">
							<tr class="reportOdd">
								<td class="tableCell" valign="top"><c:out value="${statBean.noOfEmpRetiredBySuspension}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${roleBean.staffTypeName}"/>s Not Paid (Suspended)</td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.employeesNotPaidBySuspension}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.percentageOfEmpForSuspension}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.zeroNaira}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.zeroNaira}"/></td>
								<td class="tableCell" align="right" valign="top"><a href="${appContext}/showStatDetails.do?sc=6&rm=${statBean.runMonth}&ry=${statBean.runYear}" 
								 target="_blank"onclick="popup(this.href,${roleBean.staffTypeName} Not Paid (Suspension));return false;">Details</a></td>
								 
							</tr>
							</c:if>

							<c:if test="${statBean.employeesNotPaidByApproval gt 0 }">
							<tr class="reportOdd">
								<td class="tableCell" valign="top"><c:out value="${statBean.noOfEmpNotPaidByApproval}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${roleBean.staffTypeName}"/>s Not Approved for Payroll</td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.employeesNotPaidByApproval}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.percentageOfEmpNotApproved}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.zeroNaira}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.zeroNaira}"/></td>
								<td class="tableCell" align="right" valign="top"><a href="${appContext}/showStatDetails.do?sc=12&rm=${statBean.runMonth}&ry=${statBean.runYear}" 
								 target="_blank"onclick="popup(this.href,${roleBean.staffTypeName} Not Paid (Not Approved for Payroll));return false;">Details</a></td>
								 
							</tr>
							</c:if>							
							
							<c:if test="${statBean.noOfEmployeesWithNegativePay gt 0 }">
							<tr class="reportOdd">
								<td class="tableCell" valign="top"><c:out value="${statBean.noOfEmpWithNegPay}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${roleBean.staffTypeName}"/>s With Negative Pay</td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.noOfEmployeesWithNegativePay}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.percentageOfEmpWithNegPay}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.negativePaySumStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.negativePayTotalSumStr}"/></td>
								<td class="tableCell" align="right" valign="top"><a href="${appContext}/showStatDetails.do?sc=7&rm=${statBean.runMonth}&ry=${statBean.runYear}" 
								 target="_blank"onclick="popup(this.href,${roleBean.staffTypeName} with Negative Pay);return false;">Details</a></td>
								 
							</tr>
							</c:if>	
							<c:if test="${statBean.employeesOnInterdiction gt 0 }">
							<tr class="reportOdd">
								<td class="tableCell" valign="top"><c:out value="${statBean.noOfEmpPaidByInterdiction}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${roleBean.staffTypeName}"/>s Paid by Interdiction</td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.employeesOnInterdiction}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.percentageOfEmpPaidByInterdiction}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.netPayInterdictionStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.totalPayInterdictionStr}"/></td>
								<td class="tableCell" align="right" valign="top"><a href="${appContext}/showStatDetails.do?sc=10&rm=${statBean.runMonth}&ry=${statBean.runYear}" 
								 target="_blank"onclick="popup(this.href,${roleBean.staffTypeName} Paid (Interdiction);return false;">Details</a></td>
								 
							</tr>
							</c:if>				

											
							<c:if test="${statBean.noOfEmpPaidSpecAllow gt 0 }">
							<tr class="reportOdd">
								<td class="tableCell" valign="top"><c:out value="${statBean.noOfEmpPaidSpecialAllowance}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${roleBean.staffTypeName}"/>s Paid Special Allowance</td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.noOfEmpPaidSpecAllow}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.percentageOfEmpPaidSpecAllow}"/></td>
								<td class="tableCell" align="right" valign="top">₦<c:out value="${statBean.specialAllowanceStr}"/></td>
								<td class="tableCell" align="right" valign="top">₦<c:out value="${statBean.totalPaySpecAllow}"/></td>
								<td class="tableCell" align="right" valign="top"><a href="${appContext}/showStatDetails.do?sc=9&rm=${statBean.runMonth}&ry=${statBean.runYear}" 
							    target="_blank"onclick="popup(this.href,${roleBean.staffTypeName} Paid Special Allowances);return false;">Details</a></td>
								 
							</tr>
							</c:if>					
                            <c:if test="${statBean.pensionersNotPaidByPensionCalc gt 0 }">
							<tr class="reportOdd">
								<td class="tableCell" valign="top"><c:out value="${statBean.noOfPenForRecalc}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${roleBean.staffTypeName}"/>s Awaiting Pension Recalculation</td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.pensionersNotPaidByPensionCalc}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${statBean.percentageOfPenAwaitingCalculation}"/></td>
								<td class="tableCell" align="right" valign="top">₦<c:out value="${statBean.zero}"/></td>
								<td class="tableCell" align="right" valign="top">₦<c:out value="${statBean.zero}"/></td>
								<td class="tableCell" align="right" valign="top"><a href="${appContext}/showStatDetails.do?sc=14&rm=${statBean.runMonth}&ry=${statBean.runYear}"
							    target="_blank"onclick="popup(this.href,${roleBean.staffTypeName} Not Paid (Awaiting Recalculation));return false;">Details</a></td>

							</tr>
							</c:if>
						</table>
						</c:when>
						<c:otherwise>
						
							<h3>No Payroll Data Exists for <c:out value="${statBean.monthAndYearStr}"/> </h3>
						
						</c:otherwise>
						</c:choose>
						</td>
					</tr>
					
					<tr>
                     <td class="buttonRow" align="right">
                       <input type="image" name="_close" title="Close Report" src="images/close.png">
                     </td>
                   </tr>
                  </table>
                  <c:if test="${not no_recs}">
				<div class="reportBottomPrintLink">
					<a href="${appContext}/payrollStatSummary.do?rm=${statBean.runMonth}&ry=${statBean.runYear}">
				View in Excel </a><br />
				</div>
				</c:if>
				
				</td>
				<td valign="top" class="navSide"><br>
				<br>
				
				</td>
				</tr>
				</table>
				</td>

			</tr>
			<tr> <%@ include file="/WEB-INF/jsp/footerFile.jsp" %></tr>
			</table>
			</form:form>
		</body>

	</html>
	
