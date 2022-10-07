<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>
<head>
<title>Edit Work Location</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
</head>

<body class="main">
	<table class="main" width="70%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					<tr>
						<td colspan="2">
							<div class="title">Edit Work Location</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainbody" id="mainbody">
							* = Required<br/><br/>
							<form:form modelAttribute="addressInfo">
							<div id="topOfPageBoxedErrorMessage" style="display:${addressInfo.displayErrors}">
								 <spring:hasBindErrors name="addressInfo">
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
								<tr align="left"><td class="activeTH">Work Location Address</td></tr>
								<tr><td id="firstTD_signup_form" class="activeTD">
									<table width="95%" border="0" cellspacing="0" cellpadding="2">
										<tr>
											<td align=right NOWRAP><span class="required">Address*</span></td>
											<td colspan="3"><form:input path="address1" maxlength="100"/>
											</td>
										</tr>
										<tr> 
											<td><br></td>
											<td colspan="3"><form:input path="address2" maxlength="100"/>
											</td>
										</tr>
										<tr> 
											<td align=right><span class="required">City*</span></td>
											<td colspan="3"><form:input path="city" maxlength="50"/>
											</td>
										</tr>
										<tr>
											<td align=right><span class="required">State*</span></td> 
											<td>
											<form:select path="state">
											 <form:option value="none">&lt;Select a state&gt;</form:option>
					               				 <c:forEach items="${states}" var="stateInfo">
					                			<form:option value="${stateInfo.stateCode}" >${stateInfo.name}</form:option>
					                			</c:forEach>
					               			 </form:select>
					               			</td>
					               			 
					               	    </tr>
										<tr> 
											<td align="right"><span class="required">Zip</span></td>
											<td colspan="3"><form:input path="zip" maxlength="10"/>
												<span class="note"> (xxxxx or xxxxx-xxxx)</SPAN>
											</td>
										</tr>
											
									</table>
								</td></tr>
								<tr>
									<td class="buttonRow" align="right" >
										<input type="image" name="submit" value="ok" alt="Ok" class="" src="images/ok_h.png">
										<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
									</td>
								</tr>
							</table>
							</form:form>
						</td>
					</tr>
					</table>
					
					<tr>
						<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
					</tr>
				</table>
	</body>
</html>
