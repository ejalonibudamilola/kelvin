<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title>Move Employees Between Pay Groups.  </TITLE>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
</head>

<body class="main">
    <form:form modelAttribute="ltgMiniBean">			
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					
			<tr>
				<td colspan="2">	
					<div class="title"> Move Employees Across Pay Groups</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">
				
     				<div id="topOfPageBoxedErrorMessage" style="display:${mvEmpByPayTypeBean.displayErrors}">
						<spring:hasBindErrors name="mvEmpByPayTypeBean">
         					<ul>
            					<c:forEach var="errMsgObj" items="${errors.allErrors}">
               						<li>
                  						<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               						</li>
            						</c:forEach>
         					</ul>
     					</spring:hasBindErrors>
					</div>
					
				<br/>
					<br/>
					     Move Employees Across Pay Groups
					<br/>
					<br/>
     				
     				<table border="0" cellspacing="0" cellpadding="0" width="90%"><tr><td>
					<table class="formtable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
						<tr align="left">
							<td class="activeTH">Move Details</td>
						</tr>
						<tr>
							<td class="activeTD">
								<table width="95%" border="0" cellspacing="0" cellpadding="0">
									
									<tr>
											<td width="25%" align="left" valign="bottom" nowrap>From Pay Group*</td>
											<td width="1%" align="left" >
											<form:select path="fromPayGroupId" disabled="${mvEmpByPayTypeBean.showConfirmation}">
												<form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<c:forEach items="${salaryTypesList}" var="fromList">
														<form:option value="${fromList.id}">${fromList.name}</form:option>
												</c:forEach>		 
											</form:select>
											</td>
											
									</tr>
									<tr>
											<td width="25%" align="left" valign="bottom" nowrap>To Pay Group*</td>
											<td width="1%" align="left" >
											<form:select path="toPayGroupId" disabled="${mvEmpByPayTypeBean.showConfirmation}">
												<form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<c:forEach items="${salaryTypesList}" var="toList">
														<form:option value="${toList.id}">${toList.name}</form:option>
												</c:forEach>		 
											</form:select>
											</td>
											
									</tr>
									<tr>
										<td width="25%" align="left">Total No Assigned :</td>
										<td width="1%" align="left" colspan="2"><c:out value="${mvEmpByPayTypeBean.totalAssigned}"/></td>
									</tr>
									
									
									<tr>
										<td width="25%" align="left" valign="bottom" nowrap>Add Employee</td>
										<td width="1%" align="left" >
										 <form:input path="staffId" size="8" maxlength="10"/>
										</td>
										 
										<td width="19%" align="left">
										&nbsp;&nbsp;<input type="image" name="_add" value="add" title="Add Employee" class="" src="images/add.png" align="bottom">
										</td>
										<td width="25%" align="left">
											&nbsp;											
										</td>
									</tr>
									
									
									
									<tr>
										<td width="25%" align="left">&nbsp;</td>
										<td>&nbsp;</td>
									</tr>
									<tr>
										<td>&nbsp;</td>
									</tr>
									
									</table>						
								
							</td>
						</tr>
						
					</table>
					<c:if test="${mvEmpByPayTypeBean.showCloseButton}">
					<tr>
						<td class="buttonRow" align="right" >
							<input type="image" name="_close" value="close" title="Return to Admin Home Page" class="" src="images/close.png">
						</td>
					</tr>
				 </c:if>
				</td>
			</tr>
			</table>
			
		
		</table>
		<br>

						
	<c:if test="${mvEmpByPayTypeBean.hasDataForDisplay}">
	<table class="formtable" border="0" cellspacing="0" cellpadding="3" width="90%" align="left" >
						<tr align="left">
							<td class="activeTH">Details...</td>
						</tr>
	</table>
	<table class="activeTD" border="0" cellspacing="0" cellpadding="0" width="90%">
	<tr>
		<td>
		<table class="report" cellspacing="0" cellpadding="0">
							<tr class="reportOdd header">
							<td class="tableCell" align="center" valign="top">
								S/No.</td>
								<td class="tableCell" align="center" valign="top">
								<c:out value="${roleBean.staffTitle}"/></td>
								<td class="tableCell" align="center" valign="top">
								<c:out value="${roleBean.staffTypeName}"/> Name</td>
								<td class="tableCell" align="center" valign="top">
								Level/Step</td>
								<td class="tableCell" align="center" valign="top">
								Current Total Pay</td>
								<td class="tableCell" align="center" valign="top">
								Future Total Pay</td>
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>								
							</tr>
			</table>
							<div style="overflow:scroll;height:280px;width:100%;overflow:auto">
							<table class="report" cellspacing="0" cellpadding="0">
							<c:forEach items="${mvEmpByPayTypeBean.detailsList}" var="wBean" varStatus="gridRow">
							<tr class="${wBean.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${wBean.employeeId}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.displayName}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.salaryInfo.levelAndStepStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.salaryInfo.totalMonthlySalaryStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.deductionAmountStr}"/></td>
								<td class="tableCell" align="right" valign="top"><a href="${appContext}/moveEmployeesEnmass.do?eid=${wBean.id}&act=r" >Remove</a></td>
							    
							</tr>
							</c:forEach>
							</table>	
							</div>	
							<table>					
							<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">Totals</td>
								<td class="tableCell" align="right"><c:out value="${mvEmpByPayTypeBean.totalNoOfEmp}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${mvEmpByPayTypeBean.totalBasicSalaryStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${mvEmpByPayTypeBean.totalLtgCostStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${mvEmpByPayTypeBean.totalNetIncreaseStr}"/></td>
								<td class="tableCell" align="right" valign="top">&nbsp;</td>
								 
							</tr>
							
							
						</table>
			
         </table>
        
			<tr>
				<td class="buttonRow" align="right" >
				
					
					  <c:choose>
					  <c:when test="${mvEmpByPayTypeBean.showConfirmation}">
					  
						 <input type="image" name="_confirm" value="confirm" title="click to Confirm" class="" src="images/confirm_h.png">
						 <input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
					  
					  </c:when>
					  <c:otherwise>
					     
						 		<input type="image" name="_save" value="save" title="Save and Apply Pay Group Change" class="" src="images/Save_h.png">
					         	<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
					    
					  </c:otherwise>
					  </c:choose>
					
				</td>
			</tr>
		 </c:if>
		<br/>
		<br/>
		
		</td>
		</tr>
			<tr>
				<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
			</tr>
		</table>
		
</form:form>
</body>
</html>
