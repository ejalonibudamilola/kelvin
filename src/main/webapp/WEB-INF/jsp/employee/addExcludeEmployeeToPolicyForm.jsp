<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Exclude/Include Employee To Assigned HR Policy  </TITLE>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">

</head>

<body class="main">

	<form:form modelAttribute="addXEmpBean">			
	<table class="main" width="70%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
			
			<tr>
				<td colspan="2">	
					<div class="title"> <c:out value="${addXEmpBean.displayTitle}"></c:out> </div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">
					<br/>
					Use this form to include or exclude a specific employee into/from the application of an HR Policy.<br/>
					
					
     				<div id="topOfPageBoxedErrorMessage" style="display:${addXEmpBean.displayErrors}">
						<spring:hasBindErrors name="addXEmpBean">
         					<ul>
            					<c:forEach var="errMsgObj" items="${errors.allErrors}">
               						<li>
                  						<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               						</li>
       						  </c:forEach>
         					</ul>
     					</spring:hasBindErrors>
					</div>
     				<table border="0" cellspacing="0" cellpadding="0" width="90%"><tr><td>
					<table class="formtable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
						<tr align="left">
							<td class="activeTH"><c:out value="${addXEmpBean.displayTitle}"/></td>
						</tr>
						<tr>
							<td id="firsttd_editdeduction_form" class="activeTD">
								<table width="95%" border="0" cellspacing="0" cellpadding="2">
									<tr>
										<td width="25%" align="left">Policy Name :</td>
										<td><c:out value="${addXEmpBean.name}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Department Name  :</td>
										<td><c:out value="${addXEmpBean.deptName}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Select Employee:</td>
										<td align="left">
										<form:select path="employeeId">
												<option value="0">&lt;&nbsp;Select&nbsp;&gt;</option>
													<c:forEach items="${empList}" var="eList">
														<option value="${eList.id}">${eList.firstName}&nbsp;${eList.lastName}</option>
													</c:forEach>
										</form:select>&nbsp;<input type="image" name="submit" value="ok" alt="Ok" class="" src="images/ok_h.png">								  
										</td>
									</tr>
									<tr>
										<td>&nbsp;</td>
									</tr>
									
							  </table>
					  </table>
								
							</td>
						</tr>
						
					</table>
			  </td>
			</tr>
			<tr>
				<td class="buttonRow" align="right">					
					<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
				</td>
			</tr>
			<tr>
				<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
			</tr>
		</table>
		
		
		
		</table>
	</form:form>
</body>
</html>
