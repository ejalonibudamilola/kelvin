<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<html>
<head>
<title>Edit Name and address</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css"
	media="screen">
</head>

<body class="main">
<table class="main" width="70%" border="1" bordercolor="#33c0c8"
	cellspacing="0" cellpadding="0" align="center">
	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
	<tr>
		<td>
		<table width="100%" align="center" border="0" cellpadding="0"
			cellspacing="0">
			
			<tr>
				<td colspan="2">
				<div class="title">Edit Contact Information</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">* = required<br/>
				<br/>
				<form:form modelAttribute="businessClientContact">
					<div id="topOfPageBoxedErrorMessage" style="display:${businessClientContact.displayErrors}">
					<spring:hasBindErrors name="businessClientContact">
						<ul>
							<c:forEach var="errMsgObj" items="${errors.allErrors}">
								<li><spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}" /></li>
							</c:forEach>
						</ul>
					</spring:hasBindErrors></div>
					<table class="formTable" border="0" cellspacing="0" cellpadding="3"
						width="100%" align="left">
						<tr align="left">
							<td class="activeTH">Business Information</td>
						</tr>
						<tr>
							<td id="firstTD_contact_form" class="activeTD">
							<table width="95%" cellspacing="0" cellpadding="2">
								<tr>
									<td align="right" nowrap width="25%"><span
										class="required">Business Name</span></td>
									<td><form:input path="name" maxlength="100"
										disabled="true" /></td>
								</tr>
								<tr>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td class="activeTH" align="left" colspan="20">Primary
							Contact&nbsp;&nbsp;&nbsp;</td>
						</tr>
						<tr>
							<td class="activeTD">
							<table width="95%" border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align="right" nowrap><span class="required">First
									Name*</span></td>
									<td colspan="2"><form:input path="firstName"
										maxlength="40" /></td>
								</tr>
								<tr>
									<td align="right" nowrap><span class="optional">Middle
									Initial</span></td>
									<td colspan="2"><form:input path="initials" size="3"
										maxlength="3" /></td>
								</tr>
								<tr>
									<td align="right" nowrap><span class="required">Last
									Name*</span></td>
									<td colspan="2"><form:input path="lastName" maxlength="50" />
									</td>
								</tr>
								<tr>
									<td align="right" nowrap><span class="required">Email
									Address*</span></td>
									<td colspan="2" nowrap><form:input path="email"
										maxlength="100" /></td>
								</tr>
								<tr>
									<td align="right" nowrap><span class="required">Confirm
									Email Address*</span></td>
									<td colspan="2" nowrap><form:input path="confirmEmail"
										maxlength="80" /></td>
								</tr>
								<tr>
									<td align="right"><span class="required">Work
									Phone*</span></td>
									<td nowrap colspan="2"><form:input path="businessPhone"
										maxlength="14" /> <span class="note"> (xxx-xxx-xxxx)</span>&nbsp;&nbsp;
									<span class="optional">Ext</span>&nbsp; <form:input
										path="extension" size="5" maxlength="5" /></td>
								</tr>
								<tr>
									<td align="right"><span class="optional">Fax Number</span>
									</td>
									<td nowrap><form:input path="businessFax" maxlength="15" />
									<span class="note"> (xxx-xxx-xxxx)</SPAN></td>
								</tr>
								<tr>
									<td align="right"><span class="optional">Job Title</span>
									</td>
									<td><form:input path="jobTitle" maxlength="50" /></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td class="buttonRow" align="right"><input type="image"
								name="submit" value="ok" alt="Ok" class="" src="images/ok_h.png">
							<input type="image" name="_cancel" value="cancel" alt="Cancel"
								class="" src="images/cancel_h.png"></td>
						</tr>
					</table>
				</form:form></td>
			</tr>
		</table>
	<tr>
		<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
	</tr>
</table>

</body>
</html>
