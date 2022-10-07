<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> <c:out value="${roleBean.staffTypeName}"/> Reabsorption Form  </title>
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
								<div class="title">Reabsorb <c:out value="${roleBean.staffTypeName}"/> - <c:out value="${employee.name}" /></div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="employee">
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="employee">
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
						<td class="activeTH"><c:out value="${roleBean.staffTypeName}"/> Information</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align="left" width="25%">
										<span class="required"><c:out value="${roleBean.staffTypeName}"/> Name</span></td>
									<td width="35%">
										<c:out value="${employee.name}"/>
									</td>
									
					            </tr>
								<tr>
									<td align="left" width="25%"><span class="required"><c:out value="${roleBean.staffTitle}"/></span></td>
									<td width="35%">
										<c:out value="${employee.employeeId}"/>
									</td>
								
					            </tr>
					            <tr>
									<td align="left" width="25%"><span class="optional"><c:out value="${roleBean.mdaTitle}"/></span></td>
									<td width="35%">
										<c:out value="${employee.assignedToObject}"/>
									</td>
					            </tr>
					           	<tr>
									<td align="left" width="25%"><span class="required">Hire Date</span></td>
									<td width="35%">
										<c:out value="${employee.hireDate}"/>
									</td>
									
					            </tr>
								<tr>
									<td align="left" width="25%"><span class="required">Years of Service</span></td>
									<td width="35%">
										<c:out value="${employee.yearsOfService}"/>
									</td>									
					            </tr>
								<tr>
									<td align="left" width="25%"><span class="required"><c:out value="${employee.salaryScale}"/>&nbsp;Level &amp; Step</span></td>
									<td width="35%">
										<c:out value="${employee.levelAndStepStr}"/>
									</td>									
					            </tr>
					            <c:if test="${employee.logPresent}">
								<tr>
									<td align="left" width="25%"><span class="required">Suspension Start Date</span></td>
									<td width="35%">
										<c:out value="${employee.suspensionLog.suspensionDateStr}"/>
									</td>
					            </tr>
								<tr>
									<td align="left" width="25%"><span class="required">Suspended By</span></td>
									<td width="35%">
										<c:out value="${employee.suspensionLog.user.actualUserName}"/>
									</td>
					            </tr>
								<tr>
									<td align="left" width="25%"><span class="required">Suspension Reason</span></td>
									<td width="35%">
										<c:out value="${employee.suspensionLog.suspensionType.name}"/>
									</td>
					            </tr>
					            </c:if>
								<tr>
									<td align="left" width="25%">Reason*</td>
									<td width="35%" align="left">
										<form:select path="terminateReasonId">
										<form:option value="0">&lt;Select&gt;</form:option>
										<c:forEach items="${absorptionReason}" var="sReason">
						                <form:option value="${sReason.id}">${sReason.name}</form:option>
						                </c:forEach>
										</form:select>
									</td>
						        </tr>
						<!--    <tr>
					               <td align="left" valign="top" width="25%">Arrears*</td>
									<td width="35%" align="left">
									  <form:radiobutton path="payArrearsInd" value="0" onclick="if (this.checked) { document.getElementById('hiderow').style.display = 'none'; document.getElementById('hiderow2').style.display = 'none'; document.getElementById('hiderow3').style.display = 'none';}"/>Do No Pay Arrears<br>
									  <form:radiobutton path="payArrearsInd" value="1" onclick="if (this.checked) { document.getElementById('hiderow').style.display = 'none'; document.getElementById('hiderow2').style.display = ''; document.getElementById('hiderow3').style.display = '';}"/>Pay Full Arrears<br>
									  <form:radiobutton path="payArrearsInd" value="2" onclick="if (this.checked) { document.getElementById('hiderow').style.display = ''; document.getElementById('hiderow2').style.display = '';document.getElementById('hiderow3').style.display = '';}" />Pay a Percentage of Arrears</td>
								</tr> --!>
								<tr id="hiderow" style="${employee.hideRow1}">
									<td align="left" width="25%"><span class="required">Enter Percentage Value</span></td>
									<td width="35%">
										<form:input path="arrearsPercentageStr" size="7" maxlength="5" />&#37;&nbsp;e.g 50.4
									</td>	
					            </tr>
					            <tr id="hiderow2" style="${employee.hideRow2}">
								  <td align=left width="25%"><span class="required">Arrears Start Date*</span></td>
					              <td width="35%"><form:input path="arrearsStartDate"/>
					              <img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('arrearsStartDate'),event);">
					              </td>
					              <td><div class="note">Date to start arrears payment calculation</div></td>
								</tr>
								<tr id="hiderow3" style="${employee.hideRow3}">
								  <td align="left" width="25%">Arrears End Date*</td>
					              <td width="35%"><form:input path="arrearsEndDate"/><img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('arrearsEndDate'),event);"></td>
					             <td><div class="note">Date to end arrears payment calculation</div></td>
								</tr>
								<c:if test="${employee.warningIssued}">
						        <tr>
					              <td align="left" width="25%">Reference Number*</td>
					              <td width="35%"><form:input path="refNumber" size="25" maxlength="50"/></td>
					            </tr>
					            <tr>
					              <td align=left width="25%"><span class="required">Effective Date*</span></td>
					              <td width="35%"><form:input path="refDate"/><img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('refDate'),event);"></td>
					              
					              <td nowrap><div><span class="note">Date Absorption becomes active.</span></div></td>
					            </tr>
					           </c:if>
							</table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
						  <c:choose>
						  	<c:when test="${saved}">
						  		<input type="image" name="_cancel" value="cancel" title="Close" src="images/close.png">
						  		
						  	</c:when>
						  	<c:otherwise>
						  		<input type="image" name="_absorb" value="absorb" title="Reabsorb ${roleBean.staffTypeName}" src="images/absorb.png">
							  <input type="image" name="_cancel" value="cancel" title="Cancel Operation" src="images/cancel_h.png">
						
						  	
						  	</c:otherwise>
						  
						  </c:choose>
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

