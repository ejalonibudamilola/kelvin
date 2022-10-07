<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Alter Redemption Bond Account Form  </title>
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
								<div class="title">Alter RBA Value</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="rbaBean">
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="rbaBean">
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
						<td class="activeTH">Edit RBA (Redemption Bond Account) </td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
							  <c:choose>
							  	<c:when test="${ not saved}">
							   <tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Old Value*</span></td>
									<td width="25%">
										<form:input path="rbaPercentageStr" size="5" maxlength="5" disabled="true"/>
									</td>
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">New Value*</span></td>
									<td width="25%">
										<form:input path="name" size="5" maxlength="5"/>%&nbsp;<font color="green"><i>&nbsp;e.g, 2.5 </i></font> 
									</td>
								</tr>
								</c:when>
								<c:otherwise>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Old Value*</span></td>
									<td width="25%">
										<form:input path="name" size="5" maxlength="5" disabled="true"/>
									</td>
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">New Value*</span></td>
									<td width="25%">
										<form:input path="rbaPercentageStr" size="5" maxlength="5" disabled="true"/>
									</td>
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
												<input type="image" name="_cancel" value="cancel" title="Close" class="" src="images/close.png">
							         	</c:when>
							         	<c:otherwise>
							         		<input type="image" name="submit" value="ok" title="Ok" class="" src="images/ok_h.png">&nbsp;
											<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
							         	
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