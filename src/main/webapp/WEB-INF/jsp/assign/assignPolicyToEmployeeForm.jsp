<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Assign Configured HR Policy To Employee  </TITLE>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">

</head>

<body class="main">
    <form:form modelAttribute="applyPolicyBean">			
	<table class="main" width="70%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
			<tr>
				<td colspan="2">	
					<div class="title">Assign Configured HR Policy To Employee </div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">
				<br/>
					<br/>
					Apply configured HR policies to this Employee. View and Inactivate Assigned Policy
					<br/>
					<br/>
     				
     				<div id="topOfPageBoxedErrorMessage" style="display:${applyPolicyBean.displayErrors}">
						<spring:hasBindErrors name="applyPolicyBean">
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
							<td class="activeTH">Apply pre-configured HR Policy to Employee</td>
						</tr>
						<tr>
							<td id="firsttd_editdeduction_form" class="activeTD">
								<table width="95%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td width="25%" align="left">Employee Name :</td>
										<td width="25%" align="left"><c:out value="${applyPolicyBean.employee.firstName}"/>&nbsp;<c:out value="${applyPolicyBean.employee.lastName}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Employee ID :</td>
										<td width="25%" align="left"><c:out value="${applyPolicyBean.employee.employeeId}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Employee Department:</td>
										<td width="25%" align="left"><c:out value="${applyPolicyBean.employee.deptName}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Current HR Status :</td>
										<td width="25%" align="left"><c:out value="${applyPolicyBean.hrStatus}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">
											&nbsp;
										</td>
									</tr>
									<tr>
										<td width="25%" align="left">
											&nbsp;
										</td>
									</tr>
									<tr>
										<td width="25%" align="left" nowrap>Select Policy</td>
										<td width="25%" align="left">
										<form:select path="currentPolicyId">
												<option value="0">&lt;&nbsp;Select&nbsp;&gt;</option>
													<c:forEach items="${policyList}" var="policies">
														<option value="${policies.id}">${policies.name}</option>
													</c:forEach>
														
										</form:select>
										&nbsp;&nbsp;<input type="image" name="submit" value="ok" title="Ok" class="" src="images/go_h.png">
										</td>
										<td width="25%" align="left">
											&nbsp;
										</td>
									</tr>
									<tr>
										<td>&nbsp;</td>
									</tr>
									
									</table>						
								
							</td>
						</tr>
						
					</table>
				</td>
			</tr>
			
		</table>
		
						
	
	<table border="0" cellspacing="0" cellpadding="0" width="90%"><tr><td>
			<table class="formtable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
						<tr align="left">
							<td class="activeTH">Policy Name</td>
							<td class="activeTH">Policy Description</td>
							<td class="activeTH">Policy Start Date</td>
							<td class="activeTH">Policy End Date</td>
						</tr>
						
						<tr>
							<td id="firsttd_editdeduction_form" class="activeTD">
								<table width="95%" border="0" cellspacing="0" cellpadding="0">
							
								<c:forEach items="${applyPolicyBean.assignedPolicyList}" var="assignedPolicies">
									<tr>
										
										<td>
											<c:out value="${assignedPolicies.name}"/>
										</td>
										<td>
											<c:out value="${assignedPolicies.description}"/>
										</td>
										<td>
											<c:out value="${assignedPolicies.startDate}"/>
										</td>
										<td>
											<c:out value="${assignedPolicies.endDate}"/>
										</td>
										<td align="left" nowrap>
											<a href="${appContext}/assignPolicyToEmpForm.do?pid=${applyPolicyBean.hrPolicyId}&did=${assignedDept.id}&atn=r" >Remove</a>&nbsp;
											
										</td>
										
									</tr>
								   </c:forEach>
									</table>
								</td>
								</tr>
								</table>
								</td>
								</tr>
			</table>
			<tr>
				<td class="buttonRow" align="right" >
					<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
				</td>
			</tr>
			<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
			</tr>
		</table>
	
</form:form>
</body>
</html>
