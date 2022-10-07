<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Apply Configured HR Policy  </TITLE>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">

</head>

<body class="main">
    <form:form modelAttribute="applyPolicyBean">			
	<table class="main" width="70%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
			<tr>
				<td colspan="2">	
					<div class="title"> Apply Configured HR Policy </div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">
				<br/>
					<br/>
					Apply this configured HR policies to different departments, exclude or include employees from different departments as well.
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
							<td class="activeTH">Apply pre-configured HR Policy</td>
						</tr>
						<tr>
							<td id="firsttd_editdeduction_form" class="activeTD">
								<table width="95%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td width="25%" align="left">Policy Name :</td>
										<td width="25%" align="left"><c:out value="${applyPolicyBean.name}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Description :</td>
										<td width="25%" align="left"><c:out value="${applyPolicyBean.description}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Start Date :</td>
										<td width="25%" align="left"><c:out value="${applyPolicyBean.startDate}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">End Date :</td>
										<td width="25%" align="left"><c:out value="${applyPolicyBean.endDate}"/></td>
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
										<td width="25%" align="left" nowrap>Select Department</td>
										<td width="25%" align="left">
										<form:select path="currentDeptId">
												<option value="0">&lt;&nbsp;Select&nbsp;&gt;</option>
													<c:forEach items="${deptList}" var="dList">
														<option value="${dList.id}">${dList.name}</option>
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
							<td class="activeTH">Department Name</td>
							
						</tr>
						<tr>
							<td id="firsttd_editdeduction_form" class="activeTD">
								<table width="95%" border="0" cellspacing="0" cellpadding="0">
		
								<c:forEach items="${applyPolicyBean.assignedDeptList}" var="assignedDept">
									<tr>
										
										<td>
											<c:out value="${assignedDept.name}"/>
										</td>
										<td align="left" nowrap>
											<a href="${appContext}/assignPolicyToEmp.do?pid=${applyPolicyBean.hrPolicyId}&did=${assignedDept.id}&atn=e" >Exclude Employees</a>&nbsp;
											<a href="${appContext}/assignPolicyToEmp.do?pid=${applyPolicyBean.hrPolicyId}&did=${assignedDept.id}&atn=i" >Include Employees</a>
										</td>
										<c:if test="${assignedDept.hasEmployees}">										
										<td align="left" nowrap>
											<a href="${appContext}/policyEmpDetails.do?pid=${applyPolicyBean.hrPolicyId}&did=${assignedDept.id}" >Details....</a>&nbsp;
										<td>
										</c:if>
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
