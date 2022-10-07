<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<html>
<head>

<title>Apportionment Form</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css">
<link rel="stylesheet" href="styles/skye.css" type="text/css"
	media="screen">
<link rel="stylesheet" href="<c:url value="/styles/epayroll.css"/>"
	type="text/css" />


<body class="main">
	<script type="text/JavaScript" src="scripts/jacs.js"></script>
	<table class="main" width="75%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

					<tr>
						<td colspan="2">
							<div class="title">
								Create Apportionment for<br> Pensioner :
								<c:out
									value="${apportionmentBean.hiringInfo.pensioner.displayName}" />
								<br> Pensioner ID :
								<c:out
									value="${apportionmentBean.hiringInfo.pensioner.employeeId}" />
							</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
							<p>* = Required</p> <form:form modelAttribute="apportionmentBean">
								<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
									<spring:hasBindErrors name="apportionmentBean">
										<ul>
											<c:forEach var="errMsgObj" items="${errors.allErrors}">
												<li><spring:message code="${errMsgObj.code}"
														text="${errMsgObj.defaultMessage}" /></li>
											</c:forEach>
										</ul>
									</spring:hasBindErrors>
								</div>
								<table border="0" cellspacing="0" cellpadding="0" width="90%" id="pcform0">
									<tr>
										<td>
											<table class="formTable" border="0" cellspacing="0" cellpadding="2" width="100%" align="left">
												<tr align="left">
													<td class="activeTH">Apportion Gratuity and Pension
														for <c:out
															value="${apportionmentBean.hiringInfo.pensioner.displayName}" />
													</td>
												</tr>
												<tr>
													<td class="activeTD">
														<table width="95%" border="0" cellspacing="0" cellpadding="2">
															<tr>
																<td align="right" width="25%" nowrap><span
																	class="required"><b>Date of Hire : </b></span></td>


																<td align="left" nowrap><form:input
																		path="hiringInfo.hireDateStr"
																		title="Day ${apportionmentBean.hiringInfo.employee.displayName} was absorbed into the Civil Service" disabled="true" /></td>

															</tr>
															<tr>
																<td align="right" width="25%" nowrap><span
																	class="required"><b>Service Termination Date
																			: </b></span></td>


																<td align="left" nowrap><form:input path="hiringInfo.terminatedDateStr" title="Day ${apportionmentBean.hiringInfo.employee.displayName} service ended." disabled="true" /></td>

															</tr>
															<tr>
																<td align="right" width="25%" nowrap><span
																	class="required"><b>Total Length Of Service
																			: </b></span></td>


																<td align="left" nowrap><form:input
																		path="hiringInfo.lengthOfService" disabled="true" /></td>

															</tr>


															<tr>
																<td align="right" width="25%" nowrap><span
																	class="required"><b>Total Emoluments* : </b></span></td>


																<td align="left" nowrap><form:input
																		path="totalPayStr" /></td>

															</tr>

															<tr>
																<td align="right" width="25%" nowrap><span class="required"><b>Length Of Service In Ogun (In Months)* : </b></span></td>
                                                                <td align="left" nowrap><form:input
																		path="totalLengthOfServiceStr" size="4" maxlength="4" title="Enter Total Month Value. i.e 2.7 years = 31 Months" />

																</td>

															</tr>

															<tr>
																<td align="right" width="25%"><b>Calculate Gratuity*</b></td>
																<td><form:radiobutton path="calculateGratuityInd" value="0" />No <form:radiobutton path="calculateGratuityInd" value="1" /> Yes</td>

															</tr>
															<tr>
																<td align="right" width="25%"><b>Calculate Pension*</b></td>
																<td><form:radiobutton path="calculatePensionInd" value="0" />No <form:radiobutton path="calculatePensionInd" value="1" /> Yes</td>

															</tr>

															<c:if test="${apportionmentBean.confirmation}">


																<tr>
																	<td align="right" width="30%"><b><c:if test="${apportionmentBean.recalculation}"> Recalculated </c:if>
																			Yearly Pension : </b></td>
																	<td align="left"><form:input path="hiringInfo.yearlyPensionAmountStr" disabled="true" /></td>

																</tr>
																<tr>
																	<td align="right" width="30%"><b><c:if test="${apportionmentBean.recalculation}"> Recalculated </c:if>Monthly Pension : </b></td>
																	<td align="left"><form:input path="hiringInfo.monthlyPensionAmountStr" disabled="true" /></td>

																</tr>
																<c:if test="${apportionmentBean.calculateGratuity}">
																	<tr>
																		<td align="right" width="30%"><b><c:if test="${apportionmentBean.recalculation}">Recalculated </c:if>Gratuity : </b></td>
																		<td align="left"><form:input
																				path="hiringInfo.gratuityAmountStr" disabled="true" /></td>

																	</tr>
																	<tr>
																		<td align="right" width="30%"><b>Gratuity Effective Date:</b></td>
																		<td align="left"><form:input path="gratuityEffectiveDate" /> <img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('gratuityEffectiveDate'),event);"></td>
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
										<td class="buttonRow" align="right"><c:if test="${apportionmentBean.confirmation}">
												<input type="image" name="submit" value="ok" title="Confirm" class="" src="images/confirm_h.png">

											</c:if> <c:if test="${not apportionmentBean.confirmation}">
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
