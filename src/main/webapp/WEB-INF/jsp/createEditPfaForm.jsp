<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Create/Edit PFA Form  </title>
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
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					<tr>
						<td>
								<div class="title"><c:out value="${displayTitle}"/></div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="pfaBean">
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="pfaBean">
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
						<td class="activeTH">PFA Information</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">PFA Name*</span></td>
									<td width="25%">
										<form:input path="name" size="50" maxlength="50"/>
									</td>
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">PFA Code*</span></td>
									<td width="25%">
										<form:input path="pfaCode" size="50" maxlength="40"/>
									</td>
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">PFA ID*</span></td>
									<td width="25%">
										<form:input path="pfaId" size="10" maxlength="4"/>
									</td>
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Address </span></td>
									<td width="25%">
										<form:textarea path="address" rows="5" cols="30"/>										
									</td>
								</tr>
								<tr>
									<td align="right" width="35%">PFC*</td>
									<td width="25%" align="left">
										<form:select path="pfcInfo.id">
										<form:option value="0">&lt;Select&gt;</form:option>
										<c:forEach items="${pfcInfoList}" var="pfcList">
						                <form:option value="${pfcList.id}">${pfcList.name}</form:option>
						                </c:forEach>
										</form:select>
									</td>
						        </tr>
								<c:if test="${confirmation}">
					            <tr>
									<td align="right" width="35%" nowrap>
									<span class="required">Generated Code*</span></td>
									<td width="25%">
										<form:input path="generatedCaptcha" size="15" maxlength="15" disabled="true" />
									</td>									
					            </tr>
					            <tr>
									<td align="right" width="35%" nowrap>
									<span class="required">Enter Code Above*</span></td>
									<td width="25%">
										<form:input path="enteredCaptcha" size="15" maxlength="15" />&nbsp;<font color="green">case insensitive.</font>
									</td>									
					            </tr>
					            </c:if>
					            
					           </table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
						<c:choose>
						<c:when test="${confirmation}">
							<input type="image" name="submit" value="ok" alt="Ok" class="" src="images/confirm_h.png">&nbsp;
							<input type="image" name="_cancel" value="cancel" alt="Cancel" src="images/cancel_h.png">
						</c:when>
						<c:otherwise>
							<c:choose>
						    	<c:when test="${saved}">
						    	
						  		<input type="image" name="_cancel" value="cancel" alt="Cancel" src="images/close.png">
						    	
						    	</c:when>
						    	<c:otherwise>
						    	<input type="image" name="_ok" value="ok" alt="Ok" class="" src="images/ok_h.png">&nbsp;
						  		<input type="image" name="_cancel" value="cancel" alt="Cancel" src="images/cancel_h.png">
						    	
						    	</c:otherwise>
						    
						    </c:choose>
						
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