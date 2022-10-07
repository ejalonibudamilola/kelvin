<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title>Simulated Payroll Details Form  </TITLE>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
</head>

<body class="main">
    <form:form modelAttribute="simulationBean">			
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					
			<tr>
				<td colspan="2">	
					<div class="title"> Simulated Payroll Details</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">
				
     				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
						<spring:hasBindErrors name="simulationBean">
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
					     Simulated Payroll Details Form.
					<br/>
					<br/>
     				
     				<table border="0" cellspacing="0" cellpadding="0" width="90%"><tr><td>
					<table class="formtable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
						<tr align="left">
							<td class="activeTH">Simulated Payroll Details</td>
						</tr>
						<tr>
							<td class="activeTD">
							<fieldset>
							<legend><b>Simulation Details</b></legend>
								<table width="95%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td width="25%" align="left">Payroll Simulation Name<font color="red"><b>*</b></font></td>
										<td nowrap><c:out value="${simulationBean.name}"></c:out></td>
									</tr>
									<tr>
										<td width="25%" align="left">Simulaton Start Month &amp; Year :</td>
										<td width="25%" align="left" colspan="2">
										 <c:out value="${simulationBean.startMonthName}" />
										</td>
									</tr>
									<tr>
										<td width="25%" align="left" valign="bottom" nowrap>Simulate for</td>
										<td width="1%" align="left" >
										<c:out value="${simulationBean.simulationLength}"/>
										</td>
										<td width="19%" align="left">
										&nbsp;&nbsp;
										</td>
										<td width="25%" align="left">
											&nbsp;											
										</td>
									</tr>
									 <tr>
									 <td width="25%" align="left" valign="bottom" nowrap>&nbsp;</td>
                  						<td nowrap><spring:bind path="includePromotionInd">
                 						    <input type="hidden" name="_<c:out value="${status.expression}"/>">
                 							<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if> disabled="disabled"/>
     										</spring:bind>Include Promotions
     			  						 </td>
                  			
                  						<td nowrap>&nbsp; </td>
                  						<td><div class="note"></div></td>
               						 </tr>
               						 
               						 <c:if test="${simulationBean.deductDevLevyForBaseYear}">
               						 	<tr>
               						 	<td width="25%" align="left" valign="bottom" nowrap>&nbsp;</td>
                  						<td nowrap><spring:bind path="deductBaseYearDevLevy">
                 						    <input type="hidden" name="_<c:out value="${status.expression}"/>">
                 							<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if> disabled="disabled"/>
     										</spring:bind><c:out value="${simulationBean.deductBaseYearDevLevyStr}"/>
     			  						 </td>
                  			
                  						<td nowrap>&nbsp; </td>
                  						<td><div class="note"></div></td>
               						 </tr>
               						 
               						 </c:if>
               						 
               						 <c:if test="${simulationBean.deductDevLevyForSpillOverYear}">
               						 	<tr>
               						 	<td width="25%" align="left" valign="bottom" nowrap>&nbsp;</td>
                  						<td nowrap><spring:bind path="deductSpillOverYearDevLevy" >
                 						    <input type="hidden" name="_<c:out value="${status.expression}"/>">
                 							<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if> disabled="disabled" />
     										</spring:bind><c:out value="${simulationBean.deductDevLevyForSpillOverYearStr}"/>
     			  						 </td>
                  			
                  						<td nowrap>&nbsp; </td>
                  						<td><div class="note"></div></td>
               						 </tr>
               						
               						 </c:if>
               						 	
               						 <tr>
               						 	<td width="25%" align="left" valign="bottom" nowrap>&nbsp;</td>
                  						<td nowrap><spring:bind path="applyLtgInd">
                 						    <input type="hidden" name="_<c:out value="${status.expression}"/>">
                 							<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if> disabled="disabled"/>
     										</spring:bind>Apply Leave Transport Grant
     			  						 </td>
                  			
                  						<td nowrap>&nbsp; </td>
                  						<td><div class="note"></div></td>
               						 </tr>
               						 <c:if test="${simulationBean.canDoStepIncrement}">
	               						 <tr>
	               						 	<td width="25%" align="left" valign="bottom" nowrap>&nbsp;</td>
	                  						<td nowrap><spring:bind path="stepIncrementInd">
	                 						    <input type="hidden" name="_<c:out value="${status.expression}"/>">
	                 							<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}" >checked</c:if> disabled="disabled"/>
	     										</spring:bind>Apply Yearly Step Increment
	     			  						 </td>
	                  			
	                  						<td nowrap>&nbsp; </td>
	                  						<td><div class="note"></div></td>
	               						 </tr>
               						 	
               						 </c:if>
               						 
									</table>
									</fieldset>
									
									</td>
									</tr>
									</table>
									<c:if test="${simulationBean.hasDataForDisplay}">
											<table class="formtable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
												<tr align="left">
													<td class="activeTH">Details...</td>
												</tr>
											</table>
											<table class="activeTD" border="0" cellspacing="0" cellpadding="0" width="100%"><tr><td>
											<table class="report" cellspacing="0" cellpadding="0">
												<tr class="reportOdd header">
													<td class="tableCell" align="center" valign="top">
														S/No.</td>
													<td class="tableCell" align="center" valign="top">
													<c:out value="${roleBean.mdaTitle}"/></td>
													<td class="tableCell" align="center" valign="top">
													No. Of <c:out value="${roleBean.staffTypeName}"/></td>
													<td class="tableCell" align="center" valign="top">
													Total Basic Salary</td>								
													<td class="tableCell" align="center" valign="top">
													Total LTG Cost</td>
													<td class="tableCell" align="center" valign="top">
													Net Increase</td>
													<td class="tableCell" align="center" valign="top">
													Month &amp;Year to Apply</td>
																			
												</tr>
												<c:forEach items="${simulationBean.assignedMBAPList}" var="wBean" varStatus="gridRow">
													<tr class="${wBean.displayStyle}">
														<td class="tableCell" valign="top"><c:out value="${gridRow.index + 1}"/></td>
														<td class="tableCell" align="left" valign="top"><c:out value="${wBean.name}"/></td>
														<td class="tableCell" align="right" valign="top"><c:out value="${wBean.noOfEmp}"/></td>
														<td class="tableCell" align="right" valign="top"><c:out value="${wBean.basicSalaryStr}"/></td>
														<td class="tableCell" align="right" valign="top"><c:out value="${wBean.ltgCostStr}"/></td>
														<td class="tableCell" align="right" valign="top"><c:out value="${wBean.netIncreaseStr}"/></td>
														<td class="tableCell" align="right" valign="top"><c:out value="${wBean.monthYearDisplay}"/></td>
														
													</tr>
												</c:forEach>							
													<tr class="reportEven footer">
														<td class="tableCell" valign="top">&nbsp;</td>
														<td class="tableCell" align="center" valign="top">Totals</td>
														<td class="tableCell" align="right"><c:out value="${simulationBean.totalNoOfEmp}"/></td>
														<td class="tableCell" align="right" valign="top"><c:out value="${simulationBean.totalBasicSalaryStr}"/></td>
														<td class="tableCell" align="right" valign="top"><c:out value="${simulationBean.totalLtgCostStr}"/></td>
														<td class="tableCell" align="right" valign="top"><c:out value="${simulationBean.totalNetIncreaseStr}"/></td>
														<td class="tableCell" align="right" valign="top">&nbsp;</td>
														
														
													</tr>
							
							
										</table>
			
         							</table>
         							</c:if>
									</td>
									</tr>
									<c:choose>
										<c:when test="${simulationBean.cancelWarningIssued}">
											<tr>
												<td class="buttonRow" align="right" >
												&nbsp;&nbsp;<input type="image" name="_confirm" value="confirm" title="Confirm Payroll Simulation Deletion" class="" src="images/confirm_h.png">
													<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png"><br>
												</td>
				
											</tr>
										</c:when>
										<c:otherwise>
										
											<tr>
												<td class="buttonRow" align="right" >
												&nbsp;&nbsp;<input type="image" name="_delete" value="delete" title="Delete Payroll Simulation" class="" src="images/delete_h.png">
													<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png"><br>
												</td>
					
											</tr>
										</c:otherwise>
										</c:choose>
									
									
									</table>
									</td>
									</tr>
									</table>
									</td>
									</tr>
									
			
			<tr>
				<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
			</tr>
         </table>
        
		
		
</form:form>
</body>
</html>
