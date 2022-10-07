<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
  <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<html>
<head>

<title>Setup | Gratuity and Pension</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css">
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="<c:url value="/styles/epayroll.css"/>" type="text/css" />
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
<script src="<c:url value="/scripts/utility_script.js"/>"></script>
</head>
<body class="main">
	<script type="text/JavaScript" src="scripts/jacs.js"></script>
	<table class="main" width="75%" border="1" bordercolor="#33c0c8"
		cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">


					<tr>
						<td colspan="2">
							<div class="title">
								Calculate Gratuity and Monthly Pension for<br> Pensioner :
								<c:out value="${gratBean.hiringInfo.pensioner.displayName}" />
								<br> Pensioner ID :
								<c:out value="${gratBean.hiringInfo.pensioner.employeeId}" />
							</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
							<p>* = Required</p> <form:form modelAttribute="gratBean">
								<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
									<spring:hasBindErrors name="gratBean">
										<ul>
											<c:forEach var="errMsgObj" items="${errors.allErrors}">
												<li><spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}" /></li>
											</c:forEach>
										</ul>
									</spring:hasBindErrors>
								</div>
								<table border="0" cellspacing="0" cellpadding="0" width="90%" id="pcform0">
									<tr>
										<td>
											<table class="formTable" border="0" cellspacing="0"
												cellpadding="2" width="100%" align="left">
												<tr align="left">
													<td class="activeTH">Calculate Gratuity and Pension
														for <c:out
															value="${gratBean.hiringInfo.employee.displayName}" />
													</td>
												</tr>
												<tr>
													<td class="activeTD">
														<table width="95%" border="0" cellspacing="0"
															cellpadding="2">
															<tr>
																<td align="right" width="25%" nowrap><span
																	class="required"><b>Date of Hire : </b></span></td>


																<td align="left" nowrap><form:input
																		path="hiringInfo.hireDateStr" title="Day ${gratBean.hiringInfo.pensioner.displayName} was absorbed into the Service"
																		disabled="true" /></td>

															</tr>
															<tr>
																<td align="right" width="25%" nowrap><span
																	class="required"><b>Service Termination Date
																			: </b></span></td>
                                                                        <td align="left" nowrap><form:input
																		path="hiringInfo.terminatedDateStr"
																		title="Day ${gratBean.hiringInfo.pensioner.displayName} service ended." disabled="true" /></td>

															</tr>
                                                          
                                                            <tr>
																<td align="right" width="25%" nowrap><span
																	class="required"><b>Pay Group : </b></span></td>
																<td align="left" nowrap><form:select path="salaryTypeId"  disabled="true">
																		<form:option value="-1">&lt;Select&gt;</form:option>
																		<c:forEach items="${salaryTypes}" var="pList">
																			<form:option value="${pList.id}">${pList.name}</form:option>
																		</c:forEach>
																	</form:select></td>

															</tr>
                                                              <tr>
																<td align="right" width="25%" nowrap><span
																	class="required"><b>Level &amp; Step : </b></span></td>
																<td align="left" nowrap><form:select path="salaryInfoId"  disabled="true">
																		<form:option value="-1">&lt;Select&gt;</form:option>
																		<c:forEach items="${levelAndSteps}" var="lList">
																			<form:option value="${lList.id}">${lList.levelStepStr}</form:option>
																		</c:forEach>
																	</form:select></td>

															</tr>
 															<!--
															<tr>
																<td align="right" width="25%" nowrap><span
																	class="required"><b>Level &amp; Step :</b></span></td>
																<td align="left" nowrap><form:select path="salaryInfoId">
																		<form:option value="-1">&lt;Select&gt;</form:option>
																		<c:forEach items="${gratBean.levelAndStepList}" var="pList">
																			<form:option value="${pList.id}">${pList.name}</form:option>
																		</c:forEach>
																	</form:select></td>

															</tr>
															<tr>
																<td align="right" width="25%" nowrap><span
																	class="required"><b>Level &amp; Step : </b></span></td>
                                                                    <td align="left" nowrap><form:select path="salaryInfoId" id="levelStep-control" cssClass="branchControls" disabled="${gratBean.confirmation}">
																		<form:option value="0">&lt;Select&gt;</form:option>
																		<c:forEach items="${levelAndStepList}" var="levelList">
																			<form:option value="${levelList.id}">${levelList.name}</form:option>
																		</c:forEach>
																	</form:select></td>

															</tr>
															-->
															<tr>
																<td align="right" width="25%" nowrap><span
																	class="required"><b>Total Emoluments : </b></span></td>
                                                                	<td align="left" nowrap><form:input path="totalPayStr" title="If a value is entered here, IT WILL OVERRIDE the values from Pay Group." disabled="${gratBean.lockAll}" /></td>

															</tr>
															<tr>
																<td width="25%" align="right" nowrap>&nbsp;</td>

																<td nowrap>
																	 <form:checkbox path="consolidatedIndBind" title="Checking this box will indicate that the Total Emoluments field (if populated) should be used 100% when calculating Pensions."/>
                                                                     Consolidated Salary (e.g HoS, AG,Perm Sec, Judges,Professors e.t.c)</td>
                                                            </tr>
															<tr>
																<td align="right" width="25%" nowrap><span class="required"><b>Total Length Of Service : </b></span></td>
                                                                <td align="left" nowrap><form:input path="hiringInfo.lengthOfService" size = "3" disabled="true" /></td>

															</tr>

															<c:if test="${gratBean.recalculation}">
																<tr>
																	<td align="right" width="25%"><b>Calculate Gratuity*</b></td>
																	<td><form:radiobutton path="calculateGratuityInd" value="0" disabled="${gratBean.lockAll}" />No <form:radiobutton path="calculateGratuityInd" value="1" disabled="${gratBean.lockAll}" /> Yes</td>

																</tr>
																<tr>
																	<td align="right" width="25%"><b>Calculate Pension*</b></td>
																	<td><form:radiobutton path="calculatePensionInd" value="0" disabled="${gratBean.lockAll}" />No <form:radiobutton path="calculatePensionInd" value="1" disabled="${gratBean.lockAll}" /> Yes</td>

																</tr>
															</c:if>
															<c:if test="${gratBean.confirmation}">
                                                            	<tr>
																	<td align="right" width="30%"><b><c:if test="${gratBean.recalculation}"> Recalculated </c:if> Yearly Pension : </b></td>
																	<td align="left"><form:input path="hiringInfo.yearlyPensionAmountStr" disabled="true" /></td>

																</tr>
																<tr>
																	<td align="right" width="30%"><b><c:if test="${gratBean.recalculation}"> Recalculated </c:if>Monthly Pension : </b></td>
																	<td align="left"><form:input path="hiringInfo.monthlyPensionAmountStr" disabled="true" /></td>

																</tr>
																<c:if test="${gratBean.calculateGratuity}">
																	<tr>
																		<td align="right" width="30%"><b><c:if test="${gratBean.recalculation}">Recalculated </c:if>Gratuity : </b></td>
																		<td align="left"><form:input path="hiringInfo.gratuityAmountStr" disabled="true" /></td>

																	</tr>
																	<tr>
																		<td valign="top"><b>Gratuity Effective Date:</b></td>
																		<td valign="top"><form:input path="gratuityEffectiveDate" /> <img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('gratuityEffectiveDate'),event);"></td>
																	</tr>
																</c:if>
															</c:if>
														</table>
													</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td class="buttonRow" align="right"><c:if test="${gratBean.confirmation}">
												<input type="image" name="submit" value="ok" title="Confirm" class="" src="images/confirm_h.png">
                                                </c:if> <c:if test="${not gratBean.confirmation}">
												    <input type="image" name="submit" value="ok" title="Save" class="" src="images/Save_h.png">
                                                </c:if> <input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png"></td>
									</tr>
								</table>
							</form:form>
				</table>
			</td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp"%>
		</tr>
	</table>
</body>
</html>
