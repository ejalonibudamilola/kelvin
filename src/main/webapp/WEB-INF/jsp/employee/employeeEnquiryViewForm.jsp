<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> <c:out value="${roleBean.staffTypeName}"/> Enquiry View Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">

      <link rel="stylesheet" href="<c:url value="/css/identityCard.css"/>" type="text/css" />
      <link rel="stylesheet" href="<c:url value="/css/jquery.contextmenu.css"/>" type="text/css" />

<script src="<c:url value="/scripts/jquery-1.3.2.min.js"/>"></script>    

</head>

<body class="main">
<form:form modelAttribute="employeeBean">
<!--  div for holding picture or avatar -->
<div id="fixedpikso">
	<c:choose>
		<c:when test="${empty photo }">
				<img src="<c:url value="/images/avatar.png"/>"  width="75" height="75"/>
		</c:when>
			<c:otherwise>
					<a href="#" >
					 <img src="<c:url value='/viewPassportImage'/>"  width="75" height="75"   class="img-zoom"/></a>
			</c:otherwise>
	</c:choose>		
</div>

	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
		<td>

		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			
			<tr>
				<td colspan="2">
					<div class="title"><c:out value="${roleBean.staffTypeName}"/>: &nbsp;<c:out value="${namedEntity.name}" /></div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">
					Review <c:out value="${roleBean.staffTypeName}"/> information.<br/><br/>
				   
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
                           <td>
                           <span class="reportTopPrintLinkExtended">
                              	<a href='${appContext}/searchEmpForEnquiry.do' title="Search for another ${roleBean.staffTypeName}"><i>New Search</i></a> </span>
                              	<span> <a href='${appContext}/IndEmpPayroll.do?eid=${employeeBean.employee.id}' title="View this ${roleBean.staffTypeName} Information in PDF"><i>View Biometric Information in PDF&copy; </i></a>
                              </span>                             
                          </td>
                                       
                        </tr>
						<tr>
							<td>
								<table class="formtable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left">
									<tr align="left">
										<c:if test="${employeeBean.hiringInfo.suspendedEmployee}">
										<td class="activeTH"><c:out value="${roleBean.staffTypeName}"/> Status : <img src="images/bulb_yellow.png" title="${roleBean.staffTypeName} is currently on suspension" border="0" /> <font color="orange">Suspended</font></td>
										</c:if>
										<c:if test="${employeeBean.employee.terminated}">
											<td class="activeTH"><c:out value="${roleBean.staffTypeName}"/> Status : <img src="images/bulb_red.png" title="${roleBean.staffTypeName} is currently terminated" border="0" /><font color="red">Terminated</font></td>
										</c:if>
										<c:if test="${not employeeBean.employee.terminated and not employeeBean.hiringInfo.suspendedEmployee }">
											<td class="activeTH"><c:out value="${roleBean.staffTypeName}"/> Status : <img src="images/bulb_green.png" title="Active ${roleBean.staffTypeName}" border="0" /><font color="green">Active</font></td>
										</c:if>
										
									</tr>
									<tr>
										<td id="firsttd_overview_form" class="activeTD">
											<table width="100%" border="0" cellspacing="0" cellpadding="0" class = "cmenu1" id="${employeeBean.employee.id}" title="Right Click to View Options">
												<tr>
													<td valign="top">
														<h2>
														Basic information
														</h2>
														<table border="0" cellspacing="0" cellpadding="2">
															<tr>
																<td valign="top"><b><c:out value="${roleBean.staffTitle}"/>:</b></td>
																<td valign="top"><c:out value="${employeeBean.employee.employeeId}"/></td>
															</tr>
                                                            <c:if test="${not (roleBean.civilService or roleBean.subeb)}">
                                                            <tr>
																<td valign="top"><b>Legacy ID:</b></td>
																<td valign="top"><c:out value="${employeeBean.employee.legacyEmployeeId}"/></td>
															</tr>
															</c:if>
                                                            <tr>
																<td valign="top" title="National Identification Number"><b>NIN:</b></td>
																<td valign="top" title="${employeeBean.employee.nin}"><c:out value="${employeeBean.employee.ninMask}"/></td>
															</tr>
                                                            <tr>
																<td valign="top"><b>Residence ID:</b></td>
																<td valign="top"><c:out value="${employeeBean.employee.residenceId}"/></td>
															</tr>
															<tr>
																<td valign="top"><b><c:out value="${roleBean.staffTypeName}"/> Type:</b></td>
																<td valign="top"><c:out value="${employeeBean.employeeType}"/></td>
															</tr>
															<tr>
																<td valign="top"><b>Name:</b></td>
																<td valign="top"><c:out value="${namedEntity.name}" /></td>
															</tr>
															<tr>
																<td valign="top"><b>Gender:</b></td>
																<td valign="top"><c:out value="${employeeBean.hiringInfo.genderStr}"/></td>
															</tr>
															<tr>
																<td valign="top"><b>Address:</b></td>
																<td valign="top"><c:out value="${employeeBean.employee.address1}" /></td>
															</tr>
															<tr>
																<td valign="top"><b>City, State:</b></td>
																<td valign="top"><c:out value="${employeeBean.employee.city.name}" />,<c:out value="${employeeBean.employee.city.state.name}" /></td>
															</tr>
															
															<tr>
																<td valign="top"><b>Local Govt. Area:</b></td>
																<td valign="top"><c:out value="${employeeBean.employee.lgaInfo.name}" /></td>
															</tr>
															<tr>
																<td valign="top"><b>E-Mail:</b></td>
																<td valign="top">
																	<c:out value="${employeeBean.employee.email}" />																	
																</td>
															</tr>
															<tr>
																<td valign="top"><b>Phone No:</b></td>
																<td valign="top">
																	<c:out value="${employeeBean.employee.gsmNumber}" />
																	
																</td>
															</tr>
															<tr>
																<td valign="top"><b>Next Of Kin:</b></td>
																<td valign="top">
																<c:choose>
																<c:when test="${employeeBean.hasNextOfKin}">
																	<a href='${appContext}/viewNextOfKin.do?eid=${employeeBean.employee.id}&dest=enq' title="${employeeBean.floatMessage}"><c:out value="${employeeBean.nextOfKinMessage}"/></a>
																	
																</c:when>
																<c:otherwise>
																	<c:out value="${employeeBean.nextOfKinMessage}"/>
																</c:otherwise>
																</c:choose>
																
																	
																</td>
															</tr>
														</table>
														<p>
														 <h2>Pay information</h2> 
														<table border="0" cellspacing="0" cellpadding="2">
															<tr>
                                                            	 <td valign="top"><b>Pay Group:</b></td>
                                                            	 <td valign="top">
                                                             <font color="blue"><b><c:out value="${employeeBean.employee.salaryInfo.salaryType.name}"/></b></font>:&nbsp;<c:out value="${employeeBean.employee.salaryInfo.levelStepStr}"/>
                                                             </td>
                                                             </tr>
                                                             <c:if test="${not roleBean.pensioner}">
                                                            	 <tr>
                                                            	 <td valign="top"><b>Annual Gross Salary:</b></td>
                                                            		 <td valign="top"><font color="green"><b>₦<c:out value="${employeeBean.employee.salaryInfo.annualSalaryStr}" /></b></font></td>
                                                            	 </tr>
                                                            	 <tr>
                                                                   <td valign="top"><b>Monthly Gross Salary:</b></td>
                                                                    <td valign="top"><font color="green"><b>₦<c:out value="${employeeBean.employee.salaryInfo.monthlyGrossSalaryStr}" /></b></font></td>
                                                                  </tr>
                                                            	  </c:if>
                                                            	   <c:if test="${roleBean.pensioner}">
                                                            		 <tr>
                                                                      <td valign="top"><b>Annual Pension Amount:</b></td>
                                                                       <td valign="top">₦<c:out value="${employeeBean.hiringInfo.yearlyPensionAmountStr}" /></td>
                                                                       </tr>
                                                            		 <tr>
                                                                       <td valign="top"><b>Monthly Pension Amount:</b></td>
                                                                       <td valign="top">₦<c:out value="${employeeBean.hiringInfo.monthlyPensionAmountStr}" /></td>
                                                                     </tr>
                                                            		 <c:if test="${employeeBean.hasGratuityPayments}">
                                                                       <tr>
                                                                         <td valign="top"><b>Total Gratuity:</b></td>
                                                                         <td valign="top">₦<c:out value="${employeeBean.gratuityInfo.gratuityAmountStr}" /></td>
                                                                       </tr>
                                                                        <tr>
                                                                         <td valign="top"><b>Gratuity Paid To Date:</b></td>
                                                                         <td valign="top">₦<c:out value="${employeeBean.gratuityInfo.difference}" />&nbsp;  [&nbsp;<a href='${appContext}/gratuityHistoryReport.do?pid=${employeeBean.employee.id}' target="_blank" onclick="popup(this.href, 'Gratuity Payments History');return false;" title="View Gratuity Payments"> History &nbsp;]</a>
                                                                         </td>
                                                                         </tr>
                                                                          <tr>
                                                                            <td valign="top"><b>Outstanding Amount:</b></td>
                                                                            <td valign="top">₦<c:out value="${employeeBean.gratuityInfo.outstandingAmountStr}" /></td>
                                                                           </tr>
                                                                         </c:if>
                                                                         </c:if>
                                                            			 <tr>
                                                            			  <td valign="top"><b>Pay method:</b></td>
                                                            			  <td valign="top">
                                                            			  <c:out value="${employeeBean.payMethodType}" />
                                                            			 </td>
                                                            		 </tr>

															<tr>
																<td valign="top"><b>Current Bank:</b></td>
																<td valign="top"><c:out value="${employeeBean.bankName}" /></td>
															</tr>
															<tr>
																<td valign="top"><b>Current Bank Branch:</b></td>
																<td valign="top"><c:out value="${employeeBean.bankBranchName}" /></td>
															</tr>
															<tr>
																<td valign="top"><b>Account Number:</b></td>
																<td valign="top"><c:out value="${employeeBean.accountNumber}" /></td>
															</tr>
															<tr>
                                                             <td valign="top"><b>BVN:</b></td>
                                                             <td valign="top" title="${employeeBean.bvnNo}"><c:out value="${employeeBean.bvnNoMask}" /></td>
                                                             </tr>
                                                            <tr>
																<td valign="top"><b>Payroll Approval Status:</b></td>
																<td><c:out value="${employeeBean.employee.approvalStatus}"/></td>
															</tr>
														</table>
														<p>
														<c:choose>
														<c:when test="${employeeBean.employee.terminated and not roleBean.superAdmin}">
														<h2>Deductions, Special Allowances &amp; Loans</h2> 
														</c:when>
														<c:otherwise>
														<h2><a href='${appContext}/viewDedGarnForm.do?eid=${employeeBean.employee.id}&pid=${employeeBean.id}' >View Deductions, Special Allowances &amp; Loans</a></h2>
														</c:otherwise>
														</c:choose>
														
														<table border="0" cellspacing="0" cellpadding="2">
															<tr>
																<td valign="top"><b>Deductions:</b></td>
																<td valign="top">
																	<c:out value="${employeeBean.empDed}" />
																</td>
															</tr>
															<tr>
																<td valign="top"><b>Special Allowances:</b></td>
																<td valign="top">
																	<c:out value="${employeeBean.specAllow}" />
																</td>
															</tr>
															<tr>
																<td valign="top"><b>Loans:</b></td>
																<td valign="top">
																	<c:out value="${employeeBean.garnishment}" />
																</td>
															</tr>
														</table>
													</td>
													<td>&nbsp;</td>
													<td valign="top">
														<h2>Hiring information</h2>
														<table border="0" cellspacing="0" cellpadding="2">
															<tr>
																<td valign="top"><b><c:out value="${roleBean.mdaTitle}"/>:</b></td>
																<td valign="top"><c:out value="${employeeBean.employee.currentMdaName}"/></td>
															</tr>
															<c:if test="${roleBean.localGovt}">

                                                            	 <tr>
                                                            	 <td valign="top"><b>Cadre:</b></td>
                                                            		 <td valign="top"><c:out value="${employeeBean.employee.rank.cadre.name}"/></td>
                                                            		 </tr>
                                                             </c:if>
                                                            <c:choose>
															<c:when test="${roleBean.subeb}">
                                                            <tr>
                                                            	 <td valign="top"><b>SUBEB Staff Designation:</b></td>
                                                            	 <td valign="top"><c:out value="${employeeBean.employee.rank.name}"/></td>
                                                             </tr>
                                                            <tr>
                                                            	 <td valign="top"><b>Staff Type:</b></td>
                                                            	 <td valign="top"><c:out value="${employeeBean.hiringInfo.employeeStaffType.name}"/></td>
                                                             </tr>
															</c:when>
															<c:otherwise>
                                                            <tr>
																<td valign="top"><b>Rank:</b></td>
																<td valign="top"><c:out value="${employeeBean.employee.rank.name}"/></td>
															</tr>
															</c:otherwise>
															</c:choose>
															<c:if test="${employeeBean.hasSchoolInformation}">
																<tr>
																	<td valign="top"><b>School:</b></td>
																	<td valign="top"><c:out value="${employeeBean.employee.schoolInfo.name}"/></td>
																</tr>
															</c:if>
															<tr>
																<td valign="top"><b>Marital Status:</b></td>
																<td valign="top"><c:out value="${employeeBean.hiringInfo.maritalStatus.name}"/></td>
															</tr>
															<tr>
																<td valign="top"><b>Birth date:</b></td>
																<td valign="top"><c:out value="${employeeBean.birthDate}"/></td>
															</tr>															
															<tr>
																<td valign="top"><b>Hire date:</b></td>
																<td valign="top"><c:out value="${employeeBean.hireDate}"/></td>
															</tr>
															<tr>
																<td valign="top"><b>Confirmation date:</b></td>
																<td valign="top"><c:out value="${employeeBean.hiringInfo.confirmDateStr}"/></td>
															</tr>
															 <c:if test="${roleBean.pensioner}">
                                                               <tr>
                                                                  <td valign="top"><b>Service Termination Date:</b></td>
                                                                   <td valign="top"><c:out value="${employeeBean.hiringInfo.terminatedDateStr}" /></td>
                                                               </tr>
                                                               <tr>
                                                                  <td valign="top"><b>Pension Start Date:</b></td>
                                                                   <td valign="top"><c:out value="${employeeBean.hiringInfo.pensionStartDateStr}" /></td>
                                                                </tr>
                                                               <tr>
                                                                 <td valign="top"><b>Monthly Pension Amount:</b></td>
                                                                   <td valign="top"><c:out value="${employeeBean.hiringInfo.monthlyPensionAmountStr}" /></td>
                                                                </tr>
                                                                 <c:choose>
                                                                   <c:when test="${employeeBean.hiringInfo.onApportionment}">
                                                                   <tr>
                                                                      <td valign="top"><b>Yearly Pension Amount:</b></td>
                                                                           <td valign="top"><c:out value="${employeeBean.hiringInfo.yearlyPensionAmountStr}" />
                                                                      </tr>
                                                                       <tr>
                                                                        <td valign="top"><b>Length Of Service In Ogun:</b></td>
                                                                        <td><c:out value="${employeeBean.hiringInfo.noOfYearsInOgun}" />&nbsp;years</td>
                                                                       </tr>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                       <tr>
                                                                          <td valign="top"><b>Yearly Pension Amount:</b></td>
                                                                          <td valign="top"><c:out value="${employeeBean.hiringInfo.yearlyPensionAmountStr}" />
                                                                        </tr>
                                                                         <tr>
                                                                          <td valign="top"><b>Length Of Service:</b></td>
                                                                            <td><c:out value="${employeeBean.hiringInfo.lengthOfService}" />&nbsp;years</td>
                                                                         </tr>
                                                                        </c:otherwise>
                                                                     </c:choose>
                                                              </c:if>
                                                           <c:if test="${not roleBean.pensioner}">
															<tr>
																<td valign="top"><b>Last Promotion Date:</b></td>
																<td valign="top"><c:out value="${employeeBean.hiringInfo.lastPromotionDateStr}"/>
																<c:if test="${employeeBean.hasPromotionHistory}">
																&nbsp;&nbsp;<a href='${appContext}/viewEmpPromoHistory.do?eid=${employeeBean.employee.id}' ><i>view history</i></a>
																	
																</c:if>
																</td>
															</tr>
															<tr>
																<td valign="top"><b>Next Promotion Date:</b></td>
																<td valign="top"><c:out value="${employeeBean.nextPromotionDateStr}"/></td>
															</tr>
															<tr>
																<td valign="top"><b>Exp. Retire Date:</b></td>
																<td valign="top"><c:out value="${employeeBean.hiringInfo.expDateOfRetireStr}"/></td>
															</tr>
															<c:if test="${employeeBean.hiringInfo.onContract}">
															<tr>
																<td valign="top"><b>Contract Type:</b></td>
																<td><c:out value="${employeeBean.contractType}"/></td>
															</tr>
															<tr>
																<td valign="top"><b>Contract Start Date:</b></td>
																<td><c:out value="${employeeBean.hiringInfo.contractStartDateStr}"/></td>
															</tr>
															<tr>
																<td valign="top"><b>Contract End Date:</b></td>
																<td><c:out value="${employeeBean.hiringInfo.contractEndDateStr}"/></td>
															</tr>
															</c:if>
														   </c:if>
														   <tr>
																<td valign="top"><b>Pension Fund Administrator(PFA):</b></td>
																<td><c:out value="${employeeBean.hiringInfo.pfaInfo.name}"/></td>
															</tr>
															<tr>
																<td valign="top"><b>Pension PIN Code:</b></td>
																<td><c:out value="${employeeBean.hiringInfo.pensionPinCode}"/></td>
															</tr>
                                                            <tr>
                                                              <td valign="top"><b>STIN:</b></td>
                                                              <td valign="top" title="${employeeBean.hiringInfo.tin}"><c:out value="${employeeBean.hiringInfo.tinMask}" /></td>
                                                              </tr>
															<c:if test="${employeeBean.employee.terminated}">
															<c:choose>
                                                            	 <c:when test="${roleBean.pensioner}">
                                                            	 <tr>
                                                                   <td valign="top"><b>Pension Terminated:</b></td>
                                                                        <td><c:out value="${employeeBean.hiringInfo.terminatedStr}" /></td>
                                                                  </tr>
                                                                    <tr>
                                                            	     <td valign="top"><b>Pension End Date:</b></td>
                                                            	     <td><c:out value="${employeeBean.hiringInfo.pensionEndDateStr}" /></td>
                                                            	  </tr>
                                                            		 </c:when>
                                                            		 <c:otherwise>
                                                            		 <tr>
                                                                        <td valign="top"><b>Terminated:</b></td>
                                                                         <td><c:out value="${employeeBean.hiringInfo.terminatedStr}"/></td>
                                                                      </tr>
                                                                       <tr>
                                                                         <td valign="top"><b>Termination Date:</b></td>
                                                                         <td><c:out value="${employeeBean.hiringInfo.terminatedDateStr}"/></td>
                                                                        </tr>
                                                            		 </c:otherwise>
                                                             </c:choose>
															<tr>
																<td valign="top"><b>Terminated By:</b></td>
																<td><font color="red"><c:out value="${employeeBean.terminatedBy}"/></font></td>
															</tr>
															<tr>
																<td valign="top"><b>Termination Reason:</b></td>
																<td><font color="red"><c:out value="${employeeBean.hiringInfo.terminateReason.name}"/></font></td>
															</tr>
															</c:if>
															<c:if test="${employeeBean.hiringInfo.suspendedEmployee}">
															
															<tr>
																<td valign="top"><b>Suspended by :</b></td>
																<td><font color="orange"><b><c:out value="${employeeBean.suspendedBy}"/></b></font></td>
															</tr>
															<tr>
																<td valign="top"><b>Suspension Start Date:</b></td>
																<td><font color="orange"><b><c:out value="${employeeBean.hiringInfo.suspensionDateStr}"/></b></font></td>
															</tr>
															<tr>
																<td valign="top"><b>Suspension Reason :</b></td>
																<td><font color="orange"><b><c:out value="${employeeBean.suspensionReasonStr}"/></b></font></td>
															</tr>
															</c:if>
														</table>
														<p>
														<c:choose>
															<c:when test="${employeeBean.hasPayInformation}">
																<h2><a href='${appContext}/paySlip.do?eid=${employeeBean.employee.id}' >Last Paycheck Information</a></h2>
 
																<table border="0" cellspacing="0" cellpadding="2">
																	<c:choose>
                                                                       <c:when test="${roleBean.pensioner}">
                                                                         <tr>
                                                                    		 <td valign="top"><b>Gross Pay:</b></td>
                                                                    		 <td valign="top"><c:out value="${employeeBean.lastPayCheck.totalPayStr}" /></td>
                                                                    	  </tr>
                                                                           <tr>
                                                                    	    <td valign="top"><b>Total Deductions:</b></td>
                                                                    		  <td valign="top"><c:out value="${employeeBean.lastPayCheck.totalDeductionsStr}" /></td>
                                                                    	   </tr>
                                                                    		 <tr>
                                                                    		  <td valign="top"><b>Net Pay:</b></td>
                                                                    			 <td valign="top"><c:out value="${employeeBean.lastPayCheck.netPayStr}" /></td>
                                                                    		 </tr>
                                                                    		 <c:if test="${employeeBean.lastPayCheck.gratuityPaymentMade}">
                                                                    			 <tr>
                                                                    			  <td valign="top"><b>Gratuity Paid:</b></td>
                                                                    			   <td valign="top"><c:out value="${employeeBean.lastPayCheck.amountStr}" /></td>
                                                                    			 </tr>
                                                                    		 </c:if>
                                                                        </c:when>
                                                                   <c:otherwise>
																	<tr>
																		<td valign="top"><b>Gross Pay:</b></td>
																		<td valign="top"><c:out value="${employeeBean.lastPayCheck.totalPayStr}"/></td>
																	</tr>
																	<tr>
																		<td valign="top" nowrap><b>Total Deductions:</b></td>
																		<td valign="top"><c:out value="${employeeBean.lastPayCheck.totalDeductionsStr}"/></td>
																	</tr>
																	<tr>
																		<td valign="top" nowrap><b>Net Pay:</b></td>
																		<td valign="top"><c:out value="${employeeBean.lastPayCheck.netPayStr}"/></td>
																	</tr>
																	<tr>
																		<td valign="top" nowrap><b>Total Allowances:</b></td>
																		<td valign="top">₦<c:out value="${employeeBean.lastPayCheck.totalAllowanceStr}"/></td>
																	</tr>
																	<tr>
																		<td valign="top" nowrap><b>Taxes Paid:</b></td>
																		<td valign="top"><c:out value="${employeeBean.lastPayCheck.taxesPaidStr}"/></td>
																	</tr>
																	<tr>
																		<td valign="top" nowrap><b>Pension Contribution:</b></td>
																		<td valign="top"><c:out value="${employeeBean.lastPayCheck.contributoryPensionStr}"/></td>
																	</tr>
                                                                    <tr>
																		<td valign="top" nowrap><b>Taxable Income:</b></td>
																		<td valign="top"><c:out value="${employeeBean.lastPayCheck.taxableIncomeStr}"/></td>
																	</tr>
																	<tr>
																		<td valign="top"><b>Free Pay:</b></td>
																		<td valign="top"><c:out value="${employeeBean.lastPayCheck.freePayStr}"/></td>
																	</tr>
																	 </c:otherwise>
                                                                  </c:choose>
																</table>
															</c:when>
															<c:otherwise>
																<h5><font color="grey"><b>Last Paycheck Information</b></font></h5>
 
																	<table border="0" cellspacing="0" cellpadding="2" >
																		<c:if test="${not roleBean.pensioner}">
                                                                         <tr>
																			<td valign="top" nowrap><b>Gross Pay:</b></td>
																			<td valign="top">₦0.0</td>
																		</tr>
																		<tr>
																			<td valign="top" nowrap><b>Total Deductions:</b></td>
																			<td valign="top">₦0.0</td>
																		</tr>
																		<tr>
																			<td valign="top" nowrap><b>Net Pay:</b></td>
																			<td valign="top">₦0.0</td>
																		</tr>
																		<tr>
																			<td valign="top" nowrap><b>Total Allowances:</b></td>
																			<td valign="top">₦0.0</td>
																		</tr>
																		<tr>
																			<td valign="top" nowrap><b>Taxes Paid:</b></td>
																			<td valign="top">₦0.0</td>
																		</tr>
																		<tr>
																			<td valign="top" nowrap><b>Pension Contribution:</b></td>
																			<td valign="top">₦0.0</td>
																		</tr>

																	</c:if>
                                                                     <c:if test="${roleBean.pensioner}">
                                                                       <tr>
                                                                    	 <td valign="top"><b>Gross Pay:</b></td>
                                                                    	 <td valign="top">₦0.00</td>
                                                                    	  </tr>
                                                                     <tr>
                                                                    	 <td valign="top"><b>Total Deductions:</b></td>
                                                                    	 <td valign="top">₦0.00</td>
                                                                    	  </tr>
                                                                    	 <tr>
                                                                    	  <td valign="top"><b>Net Pay:</b></td>
                                                                    	  <td valign="top">₦0.00</td>
                                                                    	 </tr>
                                                                     </c:if>
																	</table>
															
															</c:otherwise>
														</c:choose>
														
													</td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr style="display:${employeeBean.buttonRowInd}">
							<td class="buttonRow" align="right" >
								<input type="image" name="submit" value="ok" alt="Ok" class="" src="images/ok_h.png">
								<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</td>
	</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
</form:form>

<script src="<c:url value="/scripts/jquery.contextmenu.js"/>"></script>

<script>
  $(document).ready(function(){
    $('.img-zoom').hover(function() {
        $(this).addClass('transition');
        $(this).addClass('hoverZoom');
 
    }, function() {
        $(this).removeClass('transition');
        $(this).removeClass('hoverZoom');
    });
  });
</script>
<script type="text/javascript">
function OpenInNewTab(url) {
	  var win = window.open(url, '_blank');
	  win.focus();
	}
</script>
<script>
var menu1 = [
             {
                 '${roleBean.staffTypeName} Functions':{
	            	 className:'menu-custom-item', 
	            	 hoverClassName:'menu-custom-item-hover', 
            		 hoverItem:function(c) { $(this).addClass(c).find('div').html('${namedEntity.name}'); }, 
            		 hoverItemOut:function(c) { $(this).removeClass(c).find('div').html('${roleBean.staffTypeName} Functions'); },
            		 disabled:true,
            		 <c:choose>
						<c:when test="${not employeeBean.employee.terminated and not employeeBean.hiringInfo.suspendedEmployee }">
							title:'${roleBean.staffTypeName} Currently Active',
	         		 		icon:'images/bulb_green.png'
						</c:when>
						<c:when test="${employeeBean.employee.terminated}">
							title:'${roleBean.staffTypeName} Currently Terminated',
	         		 		icon:'images/bulb_red.png'
						</c:when>
						<c:otherwise>
							title:'${roleBean.staffTypeName} Currently Suspended',
	         		 		icon:'images/bulb_yellow.png'
						</c:otherwise>
					</c:choose>
            		 
                 }
             },
             $.contextMenu.separator,
             <c:forEach items="${contextMenuItems}" var="currObj">
             	{'${currObj.name}':{
                 	onclick:function(menuItem,menu) {
                 	OpenInNewTab('${currObj.goToLink}');
			  			//location.href = '${currObj.goToLink}'; 
			  		},
			  		hoverItem:function(c){$(this).addClass(c).find('div').css('background-image','url(${appContext}/images/list_greybullet.png)');},
           		 	hoverItemOut:function(c) { $(this).removeClass(c).find('div').css('background-image','') },
             	}
			  	},
             	$.contextMenu.separator,
             </c:forEach>
             
             ]; 

$(function() { 
	$('.cmenu1').contextMenu(menu1,
			{
				showTransition:'fadeIn',
				hideTransition:'fadeOut',
				useIframe:false,
				theme:'vista'
			}); 
	});

</script>

</body>
</html>
