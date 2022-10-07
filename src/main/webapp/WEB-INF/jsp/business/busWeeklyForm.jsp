<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Weekly Form  </title>
<link rel="stylesheet" href=<c:url value="styles/omg.css"/> type="text/css" />
 <link rel="stylesheet" href=<c:url value="styles/skye.css"/> type="text/css" /> 
 <link rel="stylesheet" href=<c:url value="styles/epayroll.css"/> type="text/css"/>

</head>

<body class="main">
<script type="text/JavaScript" src="scripts/jacs.js"></script>
	<table class="main" width="70%" border="1"  bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
			<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						Edit your pay schedule, then click <b>OK</b>.<br>
						* = required<br/><br/>
			<form:form modelAttribute="busPaySchedule">
				<table class="formTable1" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
					<tr>
					<td>&nbsp;</td>
					</tr>
					<tr align="left">
						<td class="activeTH"><c:out value="${busPaySchedule.mode}"/> Weekly Pay Schedule<b/></td>
					</tr>
					<tr>
						<td class="activeTD">
							<table width="100%" border="0" cellspacing="0" cellpadding="2">
									<tr>
										<td>Edit pay schedule, then click <b>OK</b>. <br>
										* = Required<br />
										<br />
										</td>
									</tr>
								      <tr>
										<td align="left" width="25%">Pay Period <b><c:out value="${payPeriod.name}" /></b></td>
										<td>&nbsp;</td>
									</tr>
									<tr>
										<td nowrap><b>Pay Date</b></td>
										<td colspan="2" nowrap>My next pay day is* 
										<form:input path="payStartDate"/><img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('payStartDate'),event);"> &nbsp;(dd-mm-yyyy)</td>
									</tr>
									<tr>
										<td><br>
										</td>
										<td colspan="2" NOWRAP>Last Pay day is*
										<form:input path="lastPayDate"/><img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('lastPayDate'),event);"> &nbsp;(dd-mm-yyyy)</td>
									</tr>
									<tr>
									<th class="helplink"></th>
									<td colspan="1">
									<spring:bind path="useAsDefault">
									<input type="hidden" name="_<c:out value="${status.expression}"/>">
									<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if>/>
									</spring:bind>
										<span class="optional alt">Use this schedule as the default for employees I add</span>

									</td>
								</tr>
							</table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
							<input type="image" name="submit" value="ok" alt="Ok" class="" src="images/ok_h.png">
							<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
						</td>
					</tr>
				</table>
			</form:form>
			</td>
			</tr>
			</table>
			
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
</body>
</html>
