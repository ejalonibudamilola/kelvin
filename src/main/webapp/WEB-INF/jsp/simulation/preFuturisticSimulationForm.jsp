<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Simulate a Futuristic Payroll Run </title>
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
								<c:choose>
									<c:when test="${futureBean.canEdit}">
									  <div class="title">Delete <c:out value="${futureBean.name}"></c:out></div>
									</c:when>
									<c:otherwise>
									<div class="title">Run a Simulated Payroll in the future</div>
									</c:otherwise>
								</c:choose>
								
								
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="futureBean">
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="futureBean">
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
				   <c:choose>
				   	<c:when test="${futureBean.canEdit}">
				   	<tr align="left">
						<td class="activeTH">Delete Futuristic Payroll Simulation</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align="right" width="25%">
										<span class="required">Simulation Name *</span></td>
									<td width="25%">
										<form:input path="name" size="20" maxlength="15"/>
									</td>
									
					            </tr>
					            <tr>
					                <td align="right" valign="top">Date *</td>
									<td width="25%"><form:input path="createdDate"/>
					             	 <img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('createdDate'),event);">
					             	</td>
									 
								</tr>
								
							</table>							
						</td>
					</tr>
					
							<tr>
								<td class="buttonRow" align="right" >
									<input type="image" name="_delete" value="delete" alt="Delete" title="Delete Futuristic Payroll Run" src="images/delete_h.png">
									<input type="image" name="_cancel" value="cancel" alt="Cancel" title="Cancel" src="images/cancel_h.png">
								</td>
							</tr>
					
				   	</c:when>
				   	<c:otherwise>
				   	 <tr align="left">
						<td class="activeTH">Create a Futuristic Payroll Simulation</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align="right" width="25%">
										<span class="required">Simulation Name *</span></td>
									<td width="25%">
										<form:input path="name" size="25" maxlength="50"/>
									</td>
									
					            </tr>
					            <tr>
					                <td align="right" valign="top">Date *</td>
									<td width="25%"><form:input path="createdDate"/>
					             	 <img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('createdDate'),event);">
					             	</td>
									 
								</tr>
								
							</table>							
						</td>
					</tr>
					
							<tr>
								<td class="buttonRow" align="right" >
									<input type="image" name="_ok" value="ok" alt="Ok" title="Initiate Futuristic Payroll Run" src="images/ok_h.png">
									<input type="image" name="_cancel" value="cancel" alt="Cancel" title="Cancel" src="images/cancel_h.png">
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

