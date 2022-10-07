<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> New User Setup Form  </title>
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
								<div class="title">Edit Materiality Values</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="viewBean">
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="viewBean">
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
						<td class="activeTH">Materiality Information</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align="right" width="15%" nowrap>
									<span class="required">First Name </span></td>

									<td align="right" width="15%" nowrap>
                                    <span class="required">Last Name </span></td>
                                    <td align="right" width="35%" nowrap title="This value indicates the threshold value IPPMS uses to show variance regarding payments by ${roleBean.mdaTitle}">
                                     &nbsp;&nbsp;&nbsp;&nbsp;Materiality Value </td>

					            </tr>
					            <c:forEach items="${viewBean.objectList}" var="mats" varStatus="gridRow">
					             <tr>
					               <td align="right" width="15%" nowrap><c:out value="${mats.user.firstName}"/>
					               <td align="right" width="15%" nowrap><c:out value="${mats.user.lastName}"/>
					               <td align="right" width="35%" nowrap>
					                       <spring:bind path="viewBean.objectList[${gridRow.index}].valueAsStr" >
							                <input type="text" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>"  value="<c:out value="${status.value}"/>" size="10" maxlength="15"
							                title="Commas and '.' allowed. e.g., (50,000,000.00)"/>
							            </spring:bind>
                                  </c:forEach>

					            </table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
						  <c:choose>
						<c:when test= "${saved}">
						<input type="image" name="_cancel" value="cancel" title="Close" src="images/close.png">
						</c:when>
						<c:otherwise>
							<input type="image" name="submit" value="ok" title="Submit" class="" src="images/ok_h.png">&nbsp;
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