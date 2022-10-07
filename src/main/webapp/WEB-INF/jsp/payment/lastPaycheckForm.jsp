<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title>Last Paycheck</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">

</head>

<body class="main">
<form:form modelAttribute="lastPaycheckBean">
<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
	<tr>
		<td>
			<table width="100%" align="center" border="0" cellpadding="0"
				cellspacing="0">
				
				<tr>
					<td colspan="2">
					<div class="title"><c:out value="${lastPaycheckBean.name}" /><br>
					Paycheck Details</div>
					</td>
				</tr>
				<tr>
					<td valign="top" class="mainBody" id="mainBody">
					<div id="topOfPageBoxedErrorMessage" style="display:${lastPaycheckBean.displayErrors}">
					<spring:hasBindErrors name="lastPaycheckBean">
						<ul>
							<c:forEach var="errMsgObj" items="${errors.allErrors}">
								<li>
								<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
								</li>
							</c:forEach>
						</ul>
					</spring:hasBindErrors>
					</div>
					<table>
						<tr>
							<td><form:select path="subLinkSelect">
								<form:option value="paystubsReportForm">Payroll Summary</form:option>
								<form:option value="employeeDetailsReport">Employee Details</form:option>
								<form:option value="deductionDetailsReport">Deduction Details</form:option>
							</form:select></td>
							<td>&nbsp;</td>
							<td><input type="image" name="_go" value="go" alt="Go" class="" src="images/go_h.png">
						</tr>
					</table>

					<table class="reportMain" cellpadding="0" cellspacing="0"
						width="100%">
						<tr>
							<td>&nbsp;</td>
							<!--<td class="reportFormControls">View the last paycheck that
							you created for: <form:select path="employeeId">
								<form:option value="0">&lt;make a selection&gt;</form:option>
								<c:forEach items="${lastPaycheckBean.employees}" var="emp">
									<form:option value="${emp.id}">${emp.name}</form:option>
								</c:forEach>
							</form:select> &nbsp;</td>
						--></tr>
						<c:if test="${lastPaycheckBean.hasError}">
							<tr>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td><b><c:out value="${lastPaycheckBean.errorMsg}"/></b></td>
								<td>&nbsp;&nbsp;<i><a href='${appContext}/preLastPaycheckForm.do' >[Select Another Employee]</a></i>
							</tr>
							<tr>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
						</c:if>
					<tr>
						<td>&nbsp;<td>
					<!--<td class="buttonRow" align="right"><input type="image"
						name="_updateReport" value="updateReport" alt="Update Report"
						class="updateReportSubmit" src="images/Update_Report_h.png">
					</td> -->
				</tr>
					</table>
					</td></tr></table></td></tr></table>
			</form:form>
		</body>

	</html>
	
