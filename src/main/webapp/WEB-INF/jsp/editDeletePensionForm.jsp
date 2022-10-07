<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Edit/Deactivate N.H.F Instruction </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="styles/epayroll.css" type="text/css"/>

</head>


<body class="main">
<script type="text/JavaScript" src="scripts/jacs.js"></script>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					

					<tr>
						<td colspan="2">
								<div class="title">Edit/Deactivate N.H.F Instruction</div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="pensionBean">
				<div id="topOfPageBoxedErrorMessage" style="display:${pensionBean.displayErrors}">
								 <spring:hasBindErrors name="pensionBean">
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
						<td class="activeTH">Edit/Deactivate N.H.F Instruction</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align="left" width="25%">
										<span class="required">Alteration Name *</span></td>
									<td width="25%">
										<form:input path="alterationName" size="25" maxlength="50"/>
									</td>
									
					            </tr>
					            <tr>
					                <td align="left" valign="top">Alteration Direction*</td>
									<td width="25%" align="left">
									  <form:radiobutton path="alterDir" value="1" />Reduce N.H.F amount<br>
									  <form:radiobutton path="alterDir" value="0" />Increase N.H.F amount<br>
									 </td>
									 
								</tr>
						        <tr>
								  <td align=left><span class="required">Start Date*</span></td>
					              <td width="25%"><c:out value="${pensionBean.startDateStr}"/></td>
								</tr>
								<tr>
								  <td width="25%">End Date*</td>
					              <td width="25%"><form:input path="endDate"/><img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('endDate'),event);"></td>
					             <td><div class="note">Only the Month and Year is considered</div></td>
								</tr>
								
						         <tr>
					                <td align="left" valign="top">Alteration Percentage*</td>
									<td width="25%" align="left">
									  <form:radiobutton path="alterInd" value="0" onclick="if (this.checked) { document.getElementById('hiderow').style.display = '';}"/>Percentage of N.H.F<br>
									  <form:radiobutton path="alterInd" value="1" onclick="if (this.checked) { document.getElementById('hiderow').style.display = '';}"/>Percentage of Monthly Basic<br></td>
								</tr>
								<tr>
									<td align="left" width="25%"><span class="required">Alteration Percentage Value</span></td>
									<td width="25%">
										<form:input path="alterationPercentageStr" size="7" maxlength="5" />&#37;&nbsp;e.g 50.4
									</td>	
					            </tr>
						        <tr>
					              <td align="left">Reference Number*</td>
					              <td width="25%"><form:input path="refNumber" size="25" maxlength="50"/></td>
					            </tr>
					            <tr>
					              <td align=left><span class="required">Reference Date*</span></td>
					              <td width="25%"><form:input path="refDate" readonly="readonly"/><img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('refDate'),event);"></td>
					            </tr>
							</table>							
						</td>
					</tr>
					<c:choose>
						<c:when test="${pensionBean.showForConfirmation}">
							<tr>
								<td class="buttonRow" align="right" >
									<input type="image" name="_confirm" value="confirm" alt="Confirm" title="Confirm N.H.F Alteration" src="images/confirm_h.png">
									<input type="image" name="_cancel" value="cancel" alt="Cancel" title="Cancel" src="images/cancel_h.png">
								</td>
							</tr>
						
						</c:when>
						<c:otherwise>
							<tr>
								<td class="buttonRow" align="right" >
									<input type="image" name="_ok" value="ok" alt="Ok" title="Change N.H.F Instruction" src="images/ok_h.png">
									<input type="image" name="_cancel" value="cancel" alt="Cancel" title="Cancel" src="images/cancel_h.png">
									<input type="image" name="_deactivate" value="deactivate" title="Deactivate P.A.Y.E Instruction" src="images/deactivate.png">
								</td>
							</tr>
						
						</c:otherwise>
					</c:choose>
					
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

