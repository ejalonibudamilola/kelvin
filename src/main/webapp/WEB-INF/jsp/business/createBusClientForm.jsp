<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Business Client Setup Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">

</head>

<body class="main">
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td>
								<div class="title">Setup Client for ePayroll</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="busClient">
				<div id="topOfPageBoxedErrorMessage" style="display:${busClient.displayErrors}">
								 <spring:hasBindErrors name="busClient">
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
						<td class="activeTH">Basic Company Information</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align=left width="35%" nowrap>
										<span class="required">Company Name*</span></td>
									<td width="25%">
										<form:input path="name"/>
									</td>
								</tr>
								<tr>
									<td align=left width="35%" nowrap>
										<span class="required">Company Email*</span></td>
									<td width="25%">
										<form:input path="email" size="50" maxlength="50"/>										
									</td>
									<td nowrap>&nbsp;<span class="note">(This must be a valid email to send auto-generated password.)</span><td>
								</tr>
					            <tr>
									<td align=right width="35%" nowrap>
									<span class="required">User Name*</span></td>
									<td width="25%">
										<form:input path="userName" />
									</td>
					            </tr>
					            <tr>
									<td align=right width="35%" nowrap>
									<span class="required">Business ID*</span></td>
									<td width="25%">
										<form:input path="businessClientUID" />&nbsp;<input type="image" name="_create" value="create" title="Generate" src="images/create_h.png">
									</td>
									
					            </tr>
					           	<tr>
									<td align=right nowrap><span class="required">
									First Name*</span></td>
									<td width="25%">
										<form:input path="firstName"/>
									</td>
								</tr>
					            <tr>
					              <td align=right nowrap><span class="required">Last Name*</span></td>
					              <td width="25%">
										<form:input path="lastName"/>
								   </td>
					              <td align="left"></td>
					            </tr>
					            <tr>
					              <td align=right width="35%" nowrap><span class="required">Business State*</span></td>
					              <td>   
					                <form:select path="businessState">
					                <form:option value="none">&lt;Select a state&gt;</form:option>
					                <c:forEach items="${states}" var="stateInfo">
					                	<form:option value="${stateInfo.stateCode}" >${stateInfo.name}</form:option>
					                </c:forEach>
					                </form:select>
					              </td>
					              <td align="left"></td>
					            </tr>
					            </table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
							<input type="image" name="submit" value="ok" title="Ok" class="" src="images/ok_h.png">&nbsp;
							<input type="image" name="_cancel" value="cancel" title="Cancel" src="images/cancel_h.png">
						</td>
					</tr>
				</table>
				</form:form>
			</td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
</body>
</html>