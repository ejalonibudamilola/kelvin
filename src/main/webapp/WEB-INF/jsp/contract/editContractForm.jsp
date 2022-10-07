<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Edit <c:out value="${roleBean.staffTypeName}"/> Contract </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="styles/epayroll.css" type="text/css"/>

</head>


<body class="main">
<form:form modelAttribute="contractBean">
<script type="text/JavaScript" src="scripts/jacs.js"></script>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					

					<tr>
						<td colspan="2">
								<div class="title">
								<c:choose>
									<c:when test="${contractBean.expired}">
										<c:out value="${contractBean.employeeName}"/><br>
										Terminated Contract.
									</c:when>
									<c:otherwise>
										Terminate Contract for<br>
										<c:out value="${contractBean.employeeName}"/>
									
									</c:otherwise>
								</c:choose>
								</div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="contractBean">
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
						<td class="activeTH">Contract Information</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align="right" width="25%">
										<span class="required">Contract Name *</span></td>
									<td width="25%">
										<c:out value="${contractBean.name}"/>
									</td>
									
					            </tr>
						        <tr>
								  <td align="right"><span class="required">Start Date*</span></td>
					              <td width="25%"><c:out value="${contractBean.contractStartDateStr}"/></td>
					              
								</tr>
								<tr>
								  <td align="right">End Date*</td>
					              <td width="25%"><c:out value="${contractBean.contractEndDateStr}"/></td>
					             
								</tr>
						        <tr>
					              <td align="right">Reference Number*</td>
					              <td width="25%"><c:out value="${contractBean.referenceNumber}"/></td>
					            </tr>
					            <tr>
					              <td align="right"><span class="required">Reference Date*</span></td>
					              <td width="25%"><c:out value="${contractBean.referenceDateStr}"/></td>
					            </tr>
					            <c:if test="${contractBean.expired}">
					            	 <tr>
					              		<td align="right"><span class="required">Termination Date</span></td>
					              		<td width="25%"><c:out value="${contractBean.expirationDateStr}"/></td>
					            	 </tr>
					            
					            </c:if>
					            <c:if test="${contractBean.canEdit}">
					            	 <tr>
					              		<td align="right"><span class="required">New Contract End Date</span></td>
					              	    <td width="25%"><form:input path="contractExtEndDate"/><img src="images/calendar.png" width="16" height="16" border="0" title="Enter New Contract Extension End Date" onclick="JACS.show(document.getElementById('contractExtEndDate'),event);"></td>

					            	 </tr>
					            </c:if>

							</table>							
						</td>
					</tr>
							
						
					
				</table>
				
			    
				<c:choose>
				
								<c:when test="${contractBean.expired}">
								<tr>
									<td class="buttonRow" align="right" >
									<input type="image" name="_cancel" value="cancel" title="Close"  src="images/close.png">
								</td>
								</tr>
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${contractBean.terminationWarning}">
											<tr>
												<td class="buttonRow" align="right" >
													<input type="image" name="_confirm" value="confirm" title="${contractBean.showForConfirm}"  src="images/confirm_h.png">
													<input type="image" name="_cancel" value="cancel" title="Close Window" src="images/close.png">
											</td>
											</tr>
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${saved}">
													<tr>
														<td class="buttonRow" align="right" >
															<input type="image" name="_cancel" value="cancel" title="Close Window" src="images/close.png">
													</td>
													</tr>
												</c:when>
												<c:otherwise>
													<tr>
														<td class="buttonRow" align="right" >
														    <c:if test="${contractBean.canEdit}">
														    <input type="image" name="_extend" value="extend" title="Extend Contract" src="images/extend.png">
														    </c:if>
															<input type="image" name="_terminate" value="terminate" title="Terminate Contract" src="images/terminate_h.png">
															<input type="image" name="_cancel" value="cancel" title="Close Window" src="images/close.png">
													</td>
													</tr>
												</c:otherwise>
											</c:choose>
											
										
										</c:otherwise>
										
									</c:choose>
								
								
								</c:otherwise>
							 
				</c:choose>
					
			</td>
		</tr>
		
	</table>
	</td>
	</tr>
	<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
	</form:form>
</body>
</html>

