<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Edit Subvention Form  </title>
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
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
				
					<tr>
						<td>
								<div class="title">Stop/Edit a Recurring Subvention</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="subventionBean">
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="subventionBean">
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
						<td class="activeTH">Subvention Information</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align=right width="35%">
										<span class="required">Created By :</span></td>
									<td width="25%">
										<c:out value="${subventionBean.createdBy.actualUserName}"/>
									</td>
								</tr>
								<tr>
									<td align=right width="35%" nowrap>
										<span class="required">Created On :</span></td>
									<td width="25%">
										<c:out value="${subventionBean.createdDateStr}"/>									
									</td>
								</tr>
								<c:if test="${subventionBean.expired}">
								<tr>
									<td align=right width="35%" nowrap>
										<span class="required">Expired on:</span></td>
									<td width="25%">
										<c:out value="${subventionBean.expirationDateStr}"/>
									</td>
									
								</tr>
								</c:if>
								<tr>
									<td align=right width="35%" nowrap>
										<span class="required">Subvention Name*</span></td>
									<td width="25%">
										<form:input path="name" size="50" maxlength="50" disabled="${subventionBean.expired or not subventionBean.canEdit}"/>
									</td>
								</tr>
								<tr>
									<td align=right width="35%" nowrap>
										<span class="required">Description </span></td>
									<td width="25%">
										<form:input path="description" size="50" maxlength="50" disabled="${subventionBean.expired or not subventionBean.canEdit}"/>										
									</td>
								</tr>
					            <tr>
									<td align=right width="35%" nowrap>
									<span class="required">Monthly Amount*</span></td>
									<td width="25%">
										₦<form:input path="amountStr" size="15" maxlength="15" disabled="${subventionBean.expired or not subventionBean.canEdit }"/>&nbsp;Commas and '.' accepted e.g 500,000.99
									</td>
									
					            </tr>
					            <c:if test="${roleBean.privilegedUser}">
                                <tr>
									<td align="right" width="35%" nowrap>
									<span class="required">Base Amount*</span></td>
									<td width="25%">
										<form:input path="baseAmountStr" size="5" maxlength="5" />
									</td>
					            </tr>
					          </c:if>
							       <c:if test="${ not subventionBean.expired}">

									 <td align=right width="35%" nowrap>
									   <span class="required">Expiration Date</span></td>
									   <td width="60%" nowrap>
										<form:input path="expirationDate" disabled="${not subventionBean.canEdit}"/>
										  <img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('expirationDate'),event);">
										  &nbsp;<font color="green"><b>Expires this subvention after this date. Can be left blank.</b></font></td>
						          	
					                  </tr>
							       </c:if>

					           </table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
							<c:choose>
							  <c:when test="${subventionBean.expired or not subventionBean.canEdit}">
								<input type="image" name="_cancel" value="cancel" title="Close" src="images/close.png">
							  </c:when>
							  <c:otherwise>
							  		<input type="image" name="submit" value="ok" title="Save Changes" class="" src="images/ok_h.png">&nbsp;
							  		<input type="image" name="_cancel" value="cancel" title="Cancel" src="images/cancel_h.png">
							  		<input type="image" name="_delete" value="delete" title="Delete" class="" src="images/delete_h.png">
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