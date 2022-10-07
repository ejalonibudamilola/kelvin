<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Bi Monthly Form  </title>
<link rel="stylesheet" href=<c:url value="styles/omg.css"/> type="text/css" />
<link rel="Stylesheet" href=<c:url value="styles/skye.css"/> type="text/css" media ="screen">

</head>

<body class="main">

	<table class="main" width="70%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
			<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					<tr>
						<td valign="top" class="mainbody" id="mainbody">
							* = Required<br/><br/>
			<form:form modelAttribute="busPaySchedule">
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="busPaySchedule">
         							 <ul>
            							<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            							</c:forEach>
         								</ul>
     							 </spring:hasBindErrors>
				</div>
				<table class="formTable1" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
					<tr>
					<td>&nbsp;</td>
					</tr>
					<tr align="left">
						<td class="activeTH"><c:out value="${busPaySchedule.mode}"/><b> Bi-Monthly Pay Schedule</b></td>
					</tr>
					<tr>
						<td class="activeTD">
							<table width="100%" border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td valign="top" class="ola">Edit your pay schedule, then click <b>OK</b>. <br>* = Required<br/> <br/> </td>
								</tr>
								<tr>
									<td class="ola">Pay Period <b>&nbsp;<c:out value="${payPeriod.name}" /></b></td>
								</tr>
								<tr>
									<td align="left" width="25%"><span class="required">The first pay day each month is the* 
									<select name="periodDayInstId" style="height : 22px; width : 62px;">
									<c:forEach items="${daysList}" var="payDays">
									<option value="${payDays.id}" >${payDays.name}</option>
									</c:forEach></select> </span></td>
								</tr>
								<tr>
									<td nowrap align="left"><span class="required">This covers the pay period ending on*</span></td>
								</tr>
								<tr>
									<td align="right"><form:radiobutton path="refType1" value="0" /> 
									<select name="workPeriodInstId" style="height : 22px; width : 120px;">
									<c:forEach items="${daysList}" var="payDays">
									<option value="${payDays.id}" >${payDays.name}</option>
									</c:forEach></select></td>
									<td align="left"><span class="required">of the </span><select name="workPeriodEffective" style="height : 22px; width : 62px;">
									<c:forEach items="${relPeriod}" var="relevantPeriod">
									<option value="${relevantPeriod.id}" >${relevantPeriod.name}</option>
									</c:forEach></select><span class="required">month.</span></td>
								</tr>
								<tr>
									<td align="right"><span class="required">
									<form:radiobutton path="refType1" value="1" />
									<form:input path="workPeriodDays" size="2" maxlength="2" /> days before the payday.
									</span>
									</td>
								</tr>
								<tr>
									<td nowrap align="left" nowrap colspan="2"><span class="required">The second payday each month is the* </span>
									<select name="periodDayInstId2" style="height : 22px; width : 62px;">
									<c:forEach items="${daysList}" var="payDays">
									<option value="${payDays.id}" >${payDays.name}</option>
									</c:forEach></select></td>
								</tr>
								<tr>
									<td nowrap align="left"><span class="required">This covers the pay period ending on*</span></td>
								</tr>
								<tr>
									<td align="right"><form:radiobutton path="refType2" value="0" />
									<select name="workPeriodInstId2" style="height : 22px; width : 120px;">
									<c:forEach items="${daysList}" var="payDays">
									<option value="${payDays.id}" >${payDays.name}</option>
									</c:forEach></select></td>
									<td align="left"><span class="required">of the </span>
									<select name="workPeriodEffective2" style="height : 22px; width : 62px;">
									<c:forEach items="${relPeriod}" var="relevantPeriod">
									<option value="${relevantPeriod.id}">${relevantPeriod.name}</option>
									</c:forEach></select><span class="required">month.</span></td>
								</tr>
								<tr>
									<td align="right">
									<span class="required">
									<form:radiobutton path="refType2" value="1" />
									<form:input path="workPeriodDays2" size="2" maxlength="2" /> days before the payday.
									</span>
									</td>
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
			</td>
			</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
</body>
</html>
