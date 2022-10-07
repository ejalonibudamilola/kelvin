<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>

<html>
<head>
<title>Auto Create Pensioner</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="stylesheet" href="styles/epayroll.css" type="text/css"/>
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>
</head>


<body class="main">

	<script type="text/javascript" src="scripts/jacs.js"></script>
	<form:form modelAttribute="employeeBean">

		<table class="main" width="80%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

			<%@ include file="/WEB-INF/jsp/headerFile.jsp"%>
			<tr>
				<td>

					<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

						<tr>
							<td colspan="2">
								<div class="title">
									Auto Create New Pensioner: &nbsp;
									<c:out value="${employeeBean.name}" />
								</div>
							</td>
						</tr>
						<tr>
							<td valign="top" class="mainbody" id="mainbody">
								<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
									<spring:hasBindErrors name="employeeBean">
										<ul>
											<c:forEach var="errMsgObj" items="${errors.allErrors}">
												<li><spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}" /></li>
											</c:forEach>
										</ul>
									</spring:hasBindErrors>
								</div>

								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td>&nbsp;</td>

									</tr>
									<tr>
										<td>
											<table class="formtable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left">
												<tr align="left">
													<td class="activeTH">Pensioner Payroll Bio-Data</td>
												</tr>
												<tr>
													<td id="firsttd_overview_form" class="activeTD">
														<table width="100%" border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td valign="bottom">
																	<h4>
																		<font color="brown"><u>Personal Information</u></font>
																	</h4>
																	<table border="0" cellspacing="0" cellpadding="2">
																		<tr>
																			<td valign="middle"><b>Active ID:</b></td>
																			<td valign="bottom"><form:input path="activeEmployeeId" size="8" maxlength="12" readonly="true" /></td>
																		</tr>
																		<tr>
																			<td valign="middle"><b>Pensioner ID:</b></td>
																			<td valign="bottom"><form:input path="pensioner.employeeId" size="8" maxlength="12" /></td>
																		</tr>
                                                                        <tr>
																			<td valign="middle"><b>NIN:</b></td>
																			<td valign="bottom"><form:input path="pensioner.nin" size="8" maxlength="11" disabled="${employeeBean.lockNin}" /></td>
																		</tr>
                                                                        <tr>
																			<td valign="middle"><b>Residence ID:</b></td>
																			<td valign="bottom"><form:input path="pensioner.residenceId" size="8" maxlength="11" disabled="${employeeBean.lockResId}" /></td>
																		</tr>
																		<tr>
                                                                        	 <td valign="middle"><b>Pensioner Type:</b></td>
                                                                        	 <td valign="bottom"><form:select path="pensionerTypeId">
                                                                        	 <form:option value="0">&lt;Select&gt;</form:option>
                                                                        	    <c:forEach items="${employeeTypeList}" var="empTypeList">
                                                                        	      <form:option value="${empTypeList.id}">${empTypeList.name}</form:option>
                                                                        	     </c:forEach>
                                                                        	 </form:select></td>
                                                                       </tr>
																		<tr>
																			<td valign="middle"><b>Pensioner Title:</b></td>
																			<td valign="bottom"><form:select path="titleId">
																					<form:option value="0">&lt;Select&gt;</form:option>
																					<c:forEach items="${employeeBean.epmTitleList}" var="titleList">
																						<form:option value="${titleList.id}">${titleList.name}</form:option>
																					</c:forEach>
																				</form:select></td>
																		</tr>
                                                                        <tr>
																			<td valign="middle"><b>Last Name (Surname):</b></td>
																			<td valign="bottom"><form:input path="pensioner.lastName" size="15"  maxlength="25" /></td>
																		</tr>
																		<tr>
																			<td valign="middle"><b>First Name:</b></td>
																			<td valign="middle"><form:input path="pensioner.firstName" size="15"  maxlength="25" /></td>
																		</tr>

																		<tr>
																			<td valign="middle"><b>Initials:</b></td>
																			<td valign="bottom"><form:input path="pensioner.initials" size="15"  maxlength="25" /></td>
																		</tr>
																		<tr>
																			<td valign="middle"><b>Gender:</b></td>
																			<td valign="bottom"><form:input path="hiringInfo.genderStr" size="5" readonly = "true" /></td>
																		</tr>
																		<tr>
																			<td valign="middle"><b>Address:</b></td>
																			<td valign="bottom"><form:input path="pensioner.address1" size="25" maxlength="50" /></td>
																		</tr>
                                                                        <tr>
                                                                           <td valign="middle"><b>City:</b></td>
                                                                        	<td valign="bottom">
                                                                              <form:select path="cityId"  onchange="loadStateByCity(this)" >
                                                                                 <form:option value="-1" >&lt;Select Type&gt;</form:option>
                                                                                   <c:forEach items="${employeeBean.cityList}" var="cities">
                                                                                     <form:option value="${cities.id}" title="${cities.name}" >${cities.name}</form:option>
                                                                                   </c:forEach>
                                                                                   </form:select>
                                                                              </td>
                                                                        </tr>
																		<tr>
																			<td valign="middle"><b>State Of Origin:</b></td>
																			<td valign="bottom"><form:select path="stateId" id="state-control" cssClass="stateControls" onchange='loadLGAByStateId(this)'>
                                                                                  <form:option value="-1">&lt;Select a state&gt;</form:option>
                                                                                   <c:forEach items="${employeeBean.stateInfoList}" var="stateInfo">
                                                                                      <form:option value="${stateInfo.id}" >${stateInfo.name}</form:option>
                                                                                    </c:forEach>
                                                                              </form:select></td>
																		</tr>

																		<tr>
																			<td valign="middle"><b>Local Govt. Area:</b></td>
																			<td valign="bottom"><form:select path="lgaId" id="lga-control" cssClass="lgaControls">
																					<form:option value="0">&lt;Select Type&gt;</form:option>
																					<c:forEach items="${employeeBean.lgaInfoList}"
																						var="lgaList">
																						<form:option value="${lgaList.id}">${lgaList.name}</form:option>
																					</c:forEach>
																				</form:select></td>
																		</tr>
																	 
																		 
																		<tr>
																			<td valign="middle"><b>Religion:</b></td>
																			<td valign="bottom"><form:select path="pensioner.religion.id">
																					<form:option value="0">&lt;Select&gt;</form:option>
																					<c:forEach items="${religionList}" var="relList">
																						<form:option value="${relList.id}">${relList.name}</form:option>
																					</c:forEach>
																				</form:select></td>
																		</tr>
																		<tr>
																			<td valign="middle"><b>E-Mail:</b></td>

																			<td valign="bottom"><form:input path="pensioner.email" size="15" /></td>


																		</tr>
																		<tr>
																			<td valign="middle"><b>Phone No:</b></td>
																			<td valign="bottom"><form:input path="pensioner.gsmNumber" size="8" maxlength="14"/></td>

																		</tr>

																	</table>

																	<h4>
																		<font color="brown"><u>Payment Information</u></font>
																	</h4>
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
                                                                            <td valign="middle" nowrap><b>Retirement Pay Group :&nbsp;</b></td>
																			<td valign="bottom"><form:select path="salaryTypeId" disabled = "true">
																					<form:option value="0">&lt;Select&gt;</form:option>
																					<c:forEach items="${employeeBean.salaryTypeList}" var="salTypeList">
																						<form:option value="${salTypeList.id}">${salTypeList.name}</form:option>
																					</c:forEach>
																				</form:select>
																		 </td>
																		</tr>
																		<tr>

																			<td valign="middle" nowrap><b>Retirement Level/Step :&nbsp;</b></td>
																			<td valign="bottom"><form:select path="levelAndStepInd" disabled = "true">
																					<form:option value="0">&lt;Select&gt;</form:option>
																					<c:forEach items="${employeeBean.levelAndStepList}" var="levelList">
																						<form:option value="${levelList.id}">${levelList.levelStepStr}</form:option>
																					</c:forEach>
																				</form:select></td>
																		</tr>
																		<tr>
																			<td valign="middle"><b>Retiring Bank : &nbsp;</b></td>
																			<td valign="bottom"><form:select path="bankId" onchange='loadBankBranchesByBankId(this);'>
																					<form:option value="0">&lt;Select&gt;</form:option>
																					<c:forEach items="${employeeBean.bankList}" var="bList">
																						<form:option value="${bList.id}">${bList.name}</form:option>
																					</c:forEach>
																				</form:select></td>
																		</tr>
																		<tr>
																			<td valign="middle" nowrap><b> Bank Branch :&nbsp;</b></td>
																			<td valign="bottom"><form:select path="bankBranchId" id="bank-branch-control" cssClass="branchControls">
																					<form:option value="0">&lt;Select&gt;</form:option>
																					<c:forEach items="${employeeBean.bankBranchList}" var="bbList">
																						<form:option value="${bbList.id}">${bbList.name}</form:option>
																					</c:forEach>
																				</form:select></td>
																		</tr>
																		<tr>
																			<td valign="middle" nowrap><b> Account No :&nbsp;</b></td>

																			<td valign="bottom"><form:input path="paymentMethodInfo.accountNumber" size = "10"/></td>
																		</tr>
																		<tr>
                                                                        	 <td valign="middle" nowrap><b> BVN :&nbsp;</b></td>
                                                                             <td valign="bottom"><form:input path="paymentMethodInfo.bvnNo" size = "10"/></td>
                                                                          </tr>

																	</table> <br>

																	<h4>
																		<font color="brown"><u>Calculated Pension &amp; Gratuity</u></font>
																	</h4>


																	<table border="0" cellspacing="0" cellpadding="2">
	                                                                    <tr>
																			<td valign="middle"><b>Total Emoluments:</b></td>
																			<td valign="bottom"><form:input path="totalEmolumentsStr" style="text-align:right;" size="6" readonly="true" /></td>
																		</tr>
																		<tr>
																			<td valign="middle"><b>Annual Pension Amount:</b></td>
																			<td valign="bottom"><form:input path="hiringInfo.yearlyPensionAmountStr" style="text-align:right;" size="6" readonly="true" /></td>
																		</tr>
																		<tr>
																			<td valign="middle"><b>Monthly Pension Amount:</b></td>
																			<td valign="bottom"><form:input path="hiringInfo.monthlyPensionAmountStr" style="text-align:right;" size="6" readonly="true" /></td>
																		</tr>
																		<tr>
																			<td valign="middle"><b>Gratuity Amount:</b></td>
																			<td valign="bottom"><form:input path="hiringInfo.gratuityAmountStr" style="text-align:right;" size="6" readonly="true" /></td>

																		</tr>
																		<tr>
																			<td valign="middle"><b>Total Length Of Service (Months):</b></td>
																			<td valign="bottom"><form:input path="totalLengthOfService" style="text-align:right;" size="2" readonly="true" /></td>
																		</tr>
                                                                        <tr>
																			<td valign="middle"><b>Total Length Of Service (Years):</b></td>
																			<td valign="bottom"><form:input path="totalLengthOfServiceInYears" style="text-align:right;" size="2" readonly="true" /></td>
																		</tr>
                                                                         <tr>
																			 <td valign="middle"><b>Apply Generated Pension</b></td>
																			 <td valign="bottom">
																			  <form:radiobutton path="useDefPension" value="1" />No <form:radiobutton path="useDefPension" value="2" />Yes
																					</td>
																		 </tr>
                                                                          <tr>
																			 <td valign="middle"><b>Apply Generated Gratuity</b></td>
																			 <td valign="bottom">
																			  <form:radiobutton path="useDefGratuity" value="1" />No <form:radiobutton path="useDefGratuity" value="2" />Yes
																					</td>
																		 </tr>
																	</table>
																</td>
																<td>&nbsp;</td>
																<td valign="top">
																	<h4>
																		<font color="brown"><u>Hiring Information</u></font>
																	</h4>
																	<table border="0" cellspacing="0" cellpadding="2">

																		<tr>
																			<td valign="middle"><b><c:out value="${roleBean.mdaTitle}"/>:</b></td>
																			<td valign="bottom"><form:select path="mdaId">
																					<form:option value="0">&lt;Select&gt;</form:option>
																					<c:forEach items="${mdaList}" var="tcList">
																						<form:option value="${tcList.id}">${tcList.name}</form:option>
																					</c:forEach>
																				</form:select></td>
																		</tr>
																		<tr>
																			<td valign="middle"><b>Marital Status:</b></td>
																			<td valign="bottom"><form:select path="maritalStatusId">
																					<form:option value="0">&lt;Select&gt;</form:option>
																					<c:forEach
																						items="${maritalStatusList}" var="msList">
																						<form:option value="${msList.id}">${msList.name}</form:option>
																					</c:forEach>
																				</form:select></td>
																		</tr>
																		<tr>
																			<td valign="middle"><b>Birth date:</b></td>
																			<td valign="bottom"><form:input path="hiringInfo.birthDateStr" size = "5" readonly="true" /></td>
																		</tr>
																		<tr>
																			<td valign="middle"><b>Hire date:</b></td>
																			<td valign="bottom"><form:input path="hiringInfo.hireDateStr" size = "5" readonly="true" /></td>
																		</tr>

																		<tr>
																			<td valign="middle"><b>Confirmation date:</b></td>
																			<td valign="bottom"><form:input path="hiringInfo.confirmDateStr" size = "5" readonly="true" /></td>
																		</tr>
																		<tr>
																			<td valign="middle"><b>Last Promotion Date:</b></td>
																			<td valign="bottom"><form:input path="hiringInfo.lastPromotionDateStr" size = "5" readonly="true"/></td>
																		</tr>
                                                                        <tr>
																			<td valign="middle" nowrap><b>Service Termination Date:</b></td>
																			<td valign="bottom"><form:input path="hiringInfo.terminatedDateStr" size = "5" readonly="true" /></td>
																		</tr>
																		<tr>
																			<td valign="middle"><b>Pension Start Date:</b></td>
																			<td valign="bottom"><form:input path="hiringInfo.pensionStartDate" size = "5"/> <img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('hiringInfo.pensionStartDate'),event);"></td>
																		</tr>

                                                                        <tr>
					                                                          <td valign="middle" nowrap><b>Pensioner Entrant Status:</b></td>
					                                                           <td valign="bottom"> <form:select path="mapId">
					                                                             <form:option value="0" >&lt;Select Status&gt;</form:option>
					                                                             <c:forEach items="${entrantList}" var="elList">
					                	                                        <form:option value="${elList.noOfEmp}" >${elList.name}</form:option>
					                                                             </c:forEach>
					                                                             </form:select>
					                                                             </td>

					                                                            </tr>
                                                                             <tr>
					                                                              <td valign="middle" nowrap><b>Entry Month:</b></td>
					                                                                 <td valign="bottom">
					                                                                  <form:select path="objectInd">
					                                                                   <form:option value="0" >&lt;Select Month&gt;</form:option>
					                                                                      <c:forEach items="${entryMonthList}" var="mlList">
					                                                                    	<form:option value="${mlList.noOfEmp}" >${mlList.name}</form:option>
					                                                                      </c:forEach>
					                                                                    </form:select>
					                                                               </td>

					                                                         </tr>

																		<tr>
																			<td valign="middle" nowrap><b>Pension Fund Administrator(PFA):</b></td>
																			<td><form:select path="pfaId">
																					<form:option value="0">&lt;Select&gt;</form:option>
																					<c:forEach items="${employeeBean.pfaInfoList}" var="pfaList">
																						<form:option value="${pfaList.id}">${pfaList.name}</form:option>
																					</c:forEach>
																				</form:select></td>
																		</tr>
																		<tr>
																			<td valign="middle"><b>Pension PIN Code:</b></td>
																			<td><form:input path="hiringInfo.pensionPinCode" size = "10"/></td>
																		</tr>


																	</table>
																	<br/>
																	<br/>
																	<h4>
																		<font color="brown"><u>Next Of Kin Information</u></font>
																	</h4>
																	<table border="0" cellspacing="0" cellpadding="2">
																		<c:choose>
																			<c:when test="${employeeBean.hasNextOfKin}">
																				<tr>
																					<td valign="middle"><b>Relationship:</b></td>
																					<td valign="bottom"><form:select path="relationTypeId">
																							<form:option value="0">&lt;Select&gt;</form:option>
																							<c:forEach items="${employeeBean.relationshipTypeList}" var="rtList">
																								<form:option value="${rtList.id}">${rtList.name}</form:option>
																							</c:forEach>
																						</form:select></td>
																				</tr>
																				<tr>
																					<td valign="middle"><b>Last Name/Surname:</b></td>
																					<td valign="bottom"><form:input path="employeeBean.nextOfKin.lastName" size="25" maxlength="25" /></td>
																				</tr>
																				<tr>
																					<td valign="middle"><b>First Name:</b></td>
																					<td valign="bottom"><form:input path="employeeBean.nextOfKin.firstName" size="25" maxlength="25" /></td>
																				</tr>
																				<tr>
																					<td valign="middle"><b>Middle Name:</b></td>
																					<td valign="bottom"><form:input path="employeeBean.nextOfKin.middleName" size="25" maxlength="25" /></td>
																				</tr>
																				<tr>
																					<td valign="middle"><b>Address:</b></td>
																					<td valign="bottom"><form:input path="employeeBean.nextOfKin.address" size="50" maxlength="120" /></td>
																				</tr>
                                                                                <tr>
                                                                                    <td valign="middle"><b>City:</b></td>
                                                                                        <td valign="bottom">
                                                                                        <form:select path="nokCityId"  onchange='loadNOKStateByCityId(this)'>
                                                                                         <form:option value="-1" >&lt;Select Type&gt;</form:option>
                                                                                         <c:forEach items="${employeeBean.cityList}" var="cities">
                                                                                         <form:option value="${cities.id}" title="${cities.name}" >${rankType.name}</form:option>
                                                                                    </c:forEach>
                                                                                    </form:select>
                                                                                  </td>
                                                                                  </tr>
																				<tr>
                                                                                	 <td valign="middle"><b>State Of Origin:</b></td>
                                                                                	 <td valign="bottom"><form:select path="nokStateId" id="nok-state-control">
                                                                                      <form:option value="-1">&lt;Select a state&gt;</form:option>
                                                                                       <c:forEach items="${employeeBean.stateInfoList}" var="stateInfo">
                                                                                          <form:option value="${stateInfo.id}" >${stateInfo.name}</form:option>
                                                                                        </c:forEach>
                                                                                        </form:select></td>
                                                                                 </tr>

																				<tr>
																					<td valign="middle"><b>Primary Phone Number:</b></td>
																					<td valign="bottom"><form:input path="employeeBean.nextOfKin.gsmNumber" size="12" maxlength="13" /></td>
																				</tr>
																				<tr>
																					<td valign="middle"><b>Alternate Phone Number:</b></td>
																					<td valign="bottom"><form:input path="employeeBean.nextOfKin.gsmNumber2" size="12" maxlength="13" /></td>
																				</tr>
																				<tr>
																					<td valign="middle"><b>Email Address:</b></td>
																					<td valign="bottom"><form:input path="employeeBean.nextOfKin.emailAddress" size="25" maxlength="25" /></td>
																				</tr>
																			</c:when>
																			<c:otherwise>
																				<tr>
																					<td valign="middle"><b>&nbsp;</b></td>
																					<td valign="bottom"><c:out value="${employeeBean.noPensionerMsg}" />&nbsp;
																						<form:radiobutton path="createLaterInd" value="0" />No
																						<form:radiobutton path="createLaterInd" value="1" />Yes
																					</td>
																				</tr>
																			</c:otherwise>
																		</c:choose>

																	</table>
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td class="buttonRow" align="right"><input type="image" name="submit" value="ok" title="Proceed" class="" src="images/create_h.png"> 
										<input type="image" name="_cancel" value="cancel" title="Cancel Operation" class="" src="images/cancel_h.png"></td>
									</tr>
								</table>
							</td>
						</tr>
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
