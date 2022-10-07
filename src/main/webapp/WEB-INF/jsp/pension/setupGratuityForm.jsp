<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<html>
<head>

<title>Setup Gratuity for Pension Payroll</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css">
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="<c:url value="/styles/epayroll.css"/>" type="text/css" />
<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>
<body class="main">
<script type="text/JavaScript" src="scripts/jacs.js"></script>
<table class="main" width="75%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
	<tr>
		<td>
		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

			<tr>
				<td colspan="2">
				<div class="title">Setup Gratuity Payment<br>

				</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainBody" id="mainbody">
				<p>* = Required</p>
				<form:form modelAttribute="gratBean">
					<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}"><spring:hasBindErrors name="gratBean">
						<ul>
							<c:forEach var="errMsgObj" items="${errors.allErrors}">
								<li><spring:message code="${errMsgObj.code}"
									text="${errMsgObj.defaultMessage}" />
								</li>
							</c:forEach>
						</ul>
					</spring:hasBindErrors></div>
					<table border="0" cellspacing="0" cellpadding="0" width="90%"
						id="pcform0">
						<tr>
							<td>
							<table class="formTable" border="0" cellspacing="0"
								cellpadding="2" width="100%" align="left">
								<tr align="left">
									<td class="activeTH">Setup Gratuity Payment</td>
								</tr>
								<tr>
									<td class="activeTD">
									<table width="95%" border="0" cellspacing="0" cellpadding="2">

										<c:forEach items="${gratBean.gratuityList}" var="giList" varStatus="gridRow">
											<tr>
												<td align="right" width="25%" nowrap><c:out value="${gridRow.index + 1}" />.&nbsp; <spring:bind path="gratBean.gratuityList[${gridRow.index}].payGratuity">
													<input type="hidden" name="_<c:out value="${status.expression}"/>">
													<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if> />
												</spring:bind> &nbsp; <b>Month and Year : </b>&nbsp;<c:out value="${giList.name}" /> &nbsp;<b>Percentage : &nbsp;</b>
												<spring:bind path="gratBean.gratuityList[${gridRow.index}].payPercentageStr">
													<input type="text" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" size="5" maxlength="5" />&#37;&nbsp;<span>e.g 50.99</span>
												</spring:bind></td>

											</tr>
										</c:forEach>

									</table>
									</td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td class="buttonRow" align="right"><c:if test="${gratBean.confirmation}">
								<input type="image" name="submit" value="ok" title="Confirm gratuity payment structure" class="" src="images/confirm_h.png">

							</c:if> <c:if test="${not gratBean.confirmation}">
								<input type="image" name="submit" value="ok" title="Setup gratuity payment structure" class="" src="images/continue_h.png">

							</c:if> <input type="image" name="_cancel" value="cancel" title="Cancel gratuity payment structure setup" class="" src="images/cancel_h.png"></td>
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
