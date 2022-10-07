 <%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>
<head>
<title> Edit Employee Sick and Vacation  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<script type="text/javascript" src="scripts/jacs.js"></script>
<script language="javascript">
	function textValue(setField,setvalue) {
	 //alert("This is the value "+setvalue);
	 document.getElementById(setField).value = setvalue;
  }
</script>
</head>

<body class="main">

	<table class="main" width="70%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			
					<tr>
						<td colspan="2">
								<div class="title">Edit <c:out value="${namedEntity.name}" /> Sick/Vacation Info</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainbody" id="mainbody">
						* = required<br/><br/>
					<form:form modelAttribute="businessPayPolicyInfo">
					<table border=0 cellspacing=0 cellpadding=0 width="*"><tr><td>
					<table class="formtable" border=0 cellspacing=0 cellpadding=3 width=100% align="left" >
					<tr align="left">
					<td class="activeTH">Vacation and Sick Leave</td>
					</tr>
					<tr>
					<td id="firsttd_editpay2_form" class="activeTD">
									<table border=0 cellspacing="0" cellpadding="4" width="100%">
										<tr>
											<td>&nbsp;</td>
											<td><br><b>Policy</b></td>
											<td>&nbsp;</td>
											<td><b>Current Balance</b><br><b>(Hours)</b>&nbsp;</td>
										</tr>
										<tr>
											<td align="right" nowrap><span class="required">Sick *</span></td>
											<td nowrap>
											<c:choose>
											<c:when test="${businessPayPolicyInfo.hasCustomSickPayPolicy}">
												<form:input path="sickDisplayMessage" id="customSick" size="68" disabled="true"/>
													&nbsp;&nbsp;&nbsp;
											<a href='${appContext}/payPolicyEditForm.do?id=${customSickPayPolicy.id}&ppt=s' >Edit policy</a>
											</c:when>
						                    <c:otherwise>
						                    	<form:select path="sickPayCurrentBalance" id="sick" onchange="textValue('availSick',this.options[this.selectedIndex].value)">
						                    	<form:option value="0">No Sick Policy</form:option>
						                    	<c:forEach items="${sickPolicies}" var="sickPolicy">
						                    	<form:option value="${sickPolicy.currentBalance}">${sickPolicy.displayMessage}</form:option>
						                    	</c:forEach>
						                    	</form:select>
						                    		&nbsp;&nbsp;&nbsp;
											<!-- <a href='${appContext}/payPolicyEditForm.do?id=${sickPolicy.id}&ppt=s' >Edit policy</a> -->
											</c:otherwise>
						                    </c:choose>
											</td>
											<td>&nbsp;&nbsp;&nbsp;</td>
											<td>
												<form:input path="availableSick" id="availSick" size="5" disabled="true"/>
											</td>
											
										</tr>
										<tr>
											<td align="right" nowrap><span class="required">Vacation *</span></td>
											<td nowrap>
											<c:choose>
											<c:when test="${businessPayPolicyInfo.hasCustomVacationPayPolicy}">
												<form:input path="vacDisplayMessage" id="customVacation" size="68" disabled="true"/>
												&nbsp;&nbsp;&nbsp;
												<a href='${appContext}/payPolicyEditForm.do?id=${customVacPayPolicy.id}&ppt=v' >Edit policy</a>
						                    </c:when>
						                    <c:otherwise>
											<form:select path="vacationPayCurrentBalance" id="vacation" onchange="textValue('availVac',this.options[this.selectedIndex].value)">
						                    <form:option value="0">No Vacation Policy</form:option>
						                    <c:forEach items="${vacationPolicies}" var="vacPolicy">
						                    <form:option value="${vacPolicy.currentBalance}">${vacPolicy.displayMessage}</form:option>
						                    </c:forEach>
						                    </form:select>
						                    &nbsp;&nbsp;&nbsp;
												<!--<a href='${appContext}/payPolicyEditForm.do?id=${vacPolicy.id}&ppt=v' >Edit policy</a>-->
						                    </c:otherwise>
						                    </c:choose>		
											</td>
											<td>&nbsp;&nbsp;&nbsp;</td>
											<td>
												<form:input path="availableVacation" id="availVac" size="5" disabled="true"/>
											</td>
										</tr>
									</table>
								</td></tr>
					</table>
					</td></tr>
					<tr>
				<td class="buttonRow" align="right" >
					<input type="image" name="submit" value="ok" alt="Ok" class="" src="images/ok_h.png">
					<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
				</td>
			</tr>
		</table>
		</form:form>
		</table>
			</td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
</body>
</html>
