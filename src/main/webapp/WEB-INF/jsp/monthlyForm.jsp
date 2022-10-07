<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Monthly Form  </title>
<link rel="stylesheet" href=<c:url value="styles/omg.css"/> type="text/css" />
 <link rel="stylesheet" href=<c:url value="styles/skye.css"/> type="text/css" /> 
 <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
</head>

<body class="main">

	<table class="main" width="70%" border="1"  bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
			<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
						<tr>
						<td colspan="2">
								<div class="title">Edit Pay Schedule for <c:out value="${namedEntity.name}" /></div>
						</td>
					</tr>
					<tr>
					<td valign="top">Edit your pay schedule, then click <b>OK</b>. <br>
							        * = Required<br/> <br/> 	
			<form:form modelAttribute="paySchedule">
			<div id="topOfPageBoxedErrorMessage" style="display:${paySchedule.displayErrors}">
								 <spring:hasBindErrors name="paySchedule">
         							 <ul>
            							<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            							</c:forEach>
         								</ul>
     							 </spring:hasBindErrors>
			</div>
				<table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
					<tr align="left">
						<td class="activeTH">Pay Period <b><c:out value="${payPeriod.name}" /></b></td>
					</tr>
					<tr>
						<td id="firsttd_editpay2_form" class="activeTD">
						<table border="0" cellspacing="0" cellpadding="4" width="100%">
						<tr>
							        <td width="25%" class="required">The first pay day each month is the* 
							        <form:select path="periodDayInstId">
							        <c:forEach items="${daysList}" var="payDays">
							        <form:option value="${payDays.id}" >${payDays.name}</form:option>
							        </c:forEach></form:select> </td>
							      </tr>
							      <tr>
							        <td nowrap align="left">This covers the pay period ending on*</td>
							      </tr>
							      <tr>
							        <td width="25%" class="required"><form:radiobutton path="refType1" value="0" /> 
							        <form:select path="workPeriodInstId">
							         <c:forEach items="${daysList}" var="payDays">
							        <form:option value="${payDays.id}" >${payDays.name}</form:option>
							        </c:forEach></form:select></td>
							        <td align="left">of the <form:select path="workPeriodEffective">
							         <c:forEach items="${relPeriod}" var="relevantPeriod">
							        <form:option value="${relevantPeriod.id}" >${relevantPeriod.name}</form:option>
							        </c:forEach></form:select>month.</td>
							      </tr>
							       <tr>
							        <td align="right" width="25%" class="required"><form:radiobutton path="refType1" value="1" /> <form:input path="workPeriodDays" size="2" maxlength="2" /> days before the payday.</td>
							      </tr>
							</table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
							<input type="image" name="submit" value="ok" title="Ok" class="" src="images/ok_h.png">
							<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
						</td>
					</tr>
				</table>
			</form:form>
			</td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
</body>
</html>
