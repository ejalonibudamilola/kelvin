<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Termination Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="<c:url value="/styles/epayroll.css"/>" type="text/css"/>
<script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
<script src="<c:url value="/scripts/utility_script.js"/>"></script>
</head>


<body class="main">
<script type="text/JavaScript" src="scripts/jacs.js"></script>
	<table class="main" width="70%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					

					<tr>
						<td colspan="2">
								<div class="title">Terminate <c:out value="${miniBean.employee.displayNameWivTitlePrefixed}" /></div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="miniBean">
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="miniBean">
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
									<td width="25%">
										<c:out value="${miniBean.abstractEmployeeEntity.displayName}"/>
									</td>
									
					            </tr>
								<tr>
									<td align="left"><span class="required"><c:out value="${roleBean.staffTitle}"/></span></td>
									<td width="25%">
										<c:out value="${miniBean.abstractEmployeeEntity.employeeId}"/>
									</td>
								
					            </tr>
					            <tr>
									<td align="left" width="25%"><span class="optional"><c:out value="${roleBean.mdaTitle}"/></span></td>
									<td width="25%">
										<c:out value="${miniBean.abstractEmployeeEntity.mdaDeptMap.mdaInfo.name}"/>
									</td>
					            </tr>
					           	<tr>
									<td align="left" width="25%"><span class="required">Hire Date</span></td>
									<td width="25%">
										<c:out value="${miniBean.hireDateStr}"/>
									</td>
									
					            </tr>
								<tr>
									<td align="left" width="25%"><span class="required">Years of Service</span></td>
									<td width="25%">
										<c:out value="${miniBean.yearsOfService}"/>
									</td>									
					            </tr>
								<tr>
									<td align="left" width="25%"><span class="required"><c:out value="${miniBean.abstractEmployeeEntity.salaryTypeName}"/>&nbsp;Level &amp; Step</span></td>
									<td width="25%">
										<c:out value="${miniBean.abstractEmployeeEntity.gradeLevelAndStep}"/>
									</td>									
					            </tr>
								<tr>
									<td align="left" width="25%">Termination Reason</td>
									<td width="25%" align="left">
										<form:select path="terminateReason.id" disabled="${saved}">
										<form:option value="0">&lt;Select&gt;</form:option>
										<c:forEach items="${termReason}" var="tReason">
						                <form:option value="${tReason.id}">${tReason.name}</form:option>
						                </c:forEach>
										</form:select>
									</td>
						          </tr>
						          <c:choose>
						          	<c:when test="${saved}">
						          		<tr>
						            		<td align="left" width="25%">Termination Effective Date*</td>
						           	 		<td width="25%" align="left"><c:out value="${miniBean.terminateDate}"/>
								  		</tr>
						          	
						          	</c:when>
						          	<c:otherwise>
						          		<tr>
						            		<td align="left" width="25%">Termination Effective Date*</td>
						           	 		<td width="25%" align="left"><form:input path="terminateDate"/>
										 	<img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('terminateDate'),event);"></td>
								 		 </tr>
						          	</c:otherwise>
						          	</c:choose>
						         
							</table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
							 <c:choose>
							         	<c:when test="${saved}">
												<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/close.png">
							         	</c:when>
							         	<c:otherwise>
							         		<input type="image" name="submit" value="ok" alt="Ok" class="" src="images/terminate_h.png">
											<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
							         	
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

