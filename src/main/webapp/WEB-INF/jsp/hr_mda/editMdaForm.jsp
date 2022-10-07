<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> <c:out value="${pageTitle}"/>&nbsp;<c:out value="${roleBean.mdaTitle}"/>  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
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
								<div class="title"><c:out value="${pageTitle}"/>&nbsp;<c:out value="${roleBean.mdaTitle}"/></div>
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
						<td class="activeTH"><c:out value="${roleBean.mdaTitle}"/> Information</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
                                <tr>
									<td align="right" width="35%" nowrap>
									  <span class="required"><c:out value="${roleBean.mdaTitle}"/> Type*</span>&nbsp;</td>
									<td>
										<form:select path="mdaType.id">
										<form:option value="0">&lt;Select&gt;</form:option>
										<c:forEach items="${mdaTypeList}" var="typeList">
						                <form:option value="${typeList.id}">${typeList.name}</form:option>
						                </c:forEach>
										</form:select>
									</td>
						        </tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required"><c:out value="${roleBean.mdaTitle}"/> Name*</span>&nbsp;
									</td>
									<td><form:input path="name" size="50" maxlength="200" /></td>								
								</tr>
                                <tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Description*</span>&nbsp;
									</td>
									<td><form:input path="description" size="50" maxlength="120" /></td>
								</tr>
								<tr>
									<td align="right" nowrap>
										<span class="required">Code Name*</span>&nbsp;
									</td>
									<td><form:input path="codeName" size="20" maxlength="20" /></td>								
								</tr>
								<tr>
									<td align="right" nowrap>
										<span class="required" title="Used to send bulk payslips to ${roleBean.staffTypeName} attached to this ${roleBean.mdaTitle}">E-Mail</span>&nbsp;
									</td>
									<td><form:input path="emailAddress" size="20" maxlength="40" /></td>
								</tr>
							 	<c:if test="${not roleBean.pensioner and not roleBean.localGovt}">
								<tr>
						             <td align="right" width="25%">School Indicator*</td>
						                <td><form:radiobutton path="schoolIndicator" value="1" title="Selecting this option means this ${roleBean.mdaTitle} has schools attached"/>Yes <form:radiobutton path="schoolIndicator" value="0" title="Selecting this option means this MDA can not have schools attached to it"/>No</td>
									 <td>&nbsp;</td>
						          </tr>
								<tr>
								</c:if>
								 <td>&nbsp;</td>
								</tr>
					        </table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
							<c:choose>
								<c:when test="${saved}">
											<input type="image" name="_cancel" value="cancel" title="Close Window" class="" src="images/close.png">
										</c:when>
								<c:otherwise>
									<input type="image" name="submit" value="ok" title="Ok" class="" src="images/ok_h.png">&nbsp;
									<input type="image" name="_cancel" value="cancel" title="Cancel" src="images/cancel_h.png">
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