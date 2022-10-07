<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Employees to be promoted form  </TITLE>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
 <link rel="stylesheet" href="<c:url value="/styles/epayroll.css"/>" type="text/css"/>

</head>

<body class="main">

	<table class="main" width="70%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>

			<table  width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			
	<tr>
		<td colspan="2">
		<div class="title">
			Employees to be promoted</div>
		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">
		<form:form modelAttribute="miniBean" id="myForm">
			<table width="650" cellspacing="0" cellpadding="0">
				<tr>
					<td>
							<div id="topOfPageBoxedErrorMessage" style="display:${miniBean.displayErrors}">
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
					
					</td>
				</tr>
			</table>
			<div style="width: 690px; height: 300px; overflow: auto; padding: 5px">
				<table class="register3" cellspacing="1" cellpadding="3">
					<tr>
						<th width="10%">Employee ID</th>
						<th width="20%">Employee Name</th>
						<th width="10%">MDA&amp;P</th>
						<th width="20%">Last Promoted</th>
						<th width="20%">Next Promotion Due</th>
					</tr>
					<c:forEach items="${miniBean.promotionTracker}" var="pTracker" varStatus="gridRow">
					<tr class="${pTracker.displayStyle}">
						<td align="center">
						<spring:bind path="miniBean.promotionTracker[${gridRow.index}].promoteEmployeeRef">
  						<input type="hidden" name="_<c:out value="${status.expression}"/>">
                  		<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if>/>
     					</spring:bind>
						</td>
						<td width="8%">
						<div>
							<c:out value="${pTracker.employee.employeeId}" /></div>
						</td>
						<td width="8%">
						<div>
							<c:out value="${pTracker.employee.firstName}" />&nbsp;<c:out value="${pTracker.employee.lastName}" /> </div>
						</td>
						<td>
						<div>
							<c:out value="${pTracker.employee.assignedToObject}" />&nbsp;&nbsp;
						</div>
						</td>
						<td>
						<div>
							<c:out value="${pTracker.lastPromoDateStr}" />&nbsp;&nbsp;
						</div>
						</td>
						<td>
						<div>
							<c:out value="${pTracker.nextPromoDateStr}" />&nbsp;&nbsp;
						</div>
						</td>
						</tr>
					</c:forEach>
				</table>
				<table width="650" cellspacing="1" cellpadding="3">
				<tr>
					<td align="left">
					<input type="image" name="submit" value="ok" alt="Ok" class="" src="images/promote_h.png">&nbsp;
					<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
					</td>
				</tr>
				</table>
			</div>
			
			
		</form:form>
		<br/>
		<br/>
		</td>
		
		
		<!-- Here to put space beween this and any following divs -->
	</tr>
	</table>

</body>

</html>

