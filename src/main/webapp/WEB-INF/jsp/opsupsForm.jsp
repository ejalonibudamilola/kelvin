<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Over Payment/Under Payment Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
</head>


<body class="main">
<form:form modelAttribute="oupsBean">
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					

					<tr>
						<td colspan="2">
								<div class="title"><c:out value="${oupsBean.opsUpsTypeStr}" />  Reconciliation Form</div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				
				<div id="topOfPageBoxedErrorMessage" style="display:${oupsBean.displayErrors}">
								 <spring:hasBindErrors name="oupsBean">
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
						<td class="activeTH"><c:out value="${oupsBean.employeeName}"/>&nbsp;Salary Difference</td>
					</tr>
					<tr>
						<td class="activeTD">
							<fieldset>
							<legend> Employee Details</legend>
							<table border="0" cellspacing="0" cellpadding="2">
							<tr>
									<td align=right width="25%">
										<span class="required"><b>Employee ID :</b></span></td>
									<td width="25%">
										<c:out value="${oupsBean.employee.employeeId}"/>
									</td>
									
					         </tr>
							<tr>
									<td align=right width="25%">
										<span class="required"><b>Names :</b></span></td>
									<td width="25%">
										<c:out value="${oupsBean.employee.displayNameWivTitlePrefixed}"/>
									</td>
									
					         </tr>
							 
					         
					         
					          <tr>
									<td align=right width="25%">
										<span class="required"><b>Current Level &amp; Step :</b></span></td>
									<td width="25%">
										<c:out value="${oupsBean.employee.salaryInfo.salaryScaleLevelAndStepStr}"/>
									</td>
									
					         </tr>
					          
							 <tr>
							 
									<td align=right width="25%">
										<span class="required"><b>Status :</b></span></td>
										<c:choose>
										<c:when test="${oupsBean.pendingMode}">
											<td width="25%">
												<font color="red"><b></b><c:out value="${oupsBean.status}"/><b></b></font>
											</td>
										</c:when>
										<c:otherwise>
										   <c:choose>
										   
										  	<c:when test="${oupsBean.applied}">
												<td width="25%">
													<font color="green"><b></b><c:out value="${oupsBean.status}"/><b></b></font>
												</td>
											</c:when>
											<c:otherwise>
												<td width="25%">
													<font color="blue"><b></b><c:out value="${oupsBean.status}"/><b></b></font>
												</td>
											</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
									<td width="25%">
										
									</td>
									
					         </tr>
					        
							 </table>
							 </fieldset>						
							<fieldset>
							<legend><c:out value="${oupsBean.opupStr}"/>&nbsp; Details</legend>
							<table border="0" cellspacing="0" cellpadding="2">
							<tr>
									<td align=right width="45%">
										<span class="required"><b><c:out value="${oupsBean.currentMonthStr}"/>&nbsp; Net Pay :</b></span></td>
									<td width="25%">
										<c:out value="${oupsBean.currPaycheck.netPayStr}"/>
									</td>
									
					         </tr>
							 <tr>
									<td align=right width="25%">
										<span class="required"><b><c:out value="${oupsBean.currentMonthStr}"/>&nbsp; Standard Deductions :</b></span></td>
									<td width="25%">
										<c:out value="${oupsBean.currPaycheck.totalDeductionsStr}"/>
									</td>
									
					         </tr>
							 <tr>
									<td align=right width="25%">
										<span class="required"><b><c:out value="${oupsBean.currentMonthStr}"/>&nbsp; P.A.Y.E (Taxes) :</b></span></td>
									<td width="25%">
										<c:out value="${oupsBean.currPaycheck.taxesPaidStr}"/>
									</td>
									
					         </tr>
					         <tr>
									<td align=right width="45%">
										<span class="required"><b><c:out value="${oupsBean.previousMonthStr}"/>&nbsp; Net Pay :</b></span></td>
									<td width="25%">
										<c:out value="${oupsBean.prevPaycheck.netPayStr}"/>
									</td>
									
					         </tr>
							 <tr>
									<td align=right width="25%">
										<span class="required"><b><c:out value="${oupsBean.previousMonthStr}"/>&nbsp; Standard Deductions :</b></span></td>
									<td width="25%">
										<c:out value="${oupsBean.prevPaycheck.totalDeductionsStr}"/>
									</td>
									
					         </tr>
							 <tr>
									<td align=right width="25%">
										<span class="required"><b><c:out value="${oupsBean.previousMonthStr}"/>&nbsp; P.A.Y.E (Taxes) :</b></span></td>
									<td width="25%">
										<c:out value="${oupsBean.prevPaycheck.taxesPaidStr}"/>
									</td>
									
					         </tr>
					         <tr>
									<td align=right width="25%">
										<span class="required"><b><c:out value="${oupsBean.opupStr}"/>&nbsp; Amount :</b></span></td>
									<c:choose>
										<c:when test="${oupsBean.overPayment}">
											<td width="25%">
												<font color="red"><c:out value="${oupsBean.salaryDiffStr}"/></font>
											</td>
										</c:when>
										<c:otherwise>
											<td width="25%">
												<c:out value="${oupsBean.salaryDiffStr}"/>
											</td>
										</c:otherwise>
									</c:choose>
									
									
					         </tr>
							 </table>
							 </fieldset>
							 <c:if test="${oupsBean.viewOnly}">
							<fieldset>
							<legend> Status</legend>
							<table border="0" cellspacing="0" cellpadding="2">
							<tr>
									<td align=right width="25%">
										<span class="required"><b>Current Status :</b></span></td>
									<td width="25%">
										<c:out value="${oupsBean.status}"/>
									</td>
									
					         </tr>
							<tr>
									<td align=right width="25%">
										<span class="required"><b>Applied Period :</b></span></td>
									<td width="25%">
										<c:out value="${oupsBean.applicationMonthYearStr}"/>
									</td>
									
					         </tr>
							
							 </table>
							 </fieldset>
							 </c:if>		
						</td>
						</tr>
							</table>							
						</td>
					</tr>
					<c:if test="${oupsBean.exists}">
					 <c:choose>
						   	<c:when test="${oupsBean.viewOnly}">
						   		<tr>
									<td class="buttonRow" align="right" >
										<input type="image" name="_cancel" value="cancel" title="Close" class="" src="images/close.png">
									</td>
								</tr>
						   	
						   	</c:when>
						   	<c:otherwise>
						   	 <c:choose>
						   	    <c:when test="${oupsBean.deleteMode}">
							      <tr>
									<td class="buttonRow" align="right" >
									<input type="image" name="_delete" value="delete" title="Delete ${oupsBean.opupStr} Instruction" class="" src="images/delete_h.png">
										
										<input type="image" name="_cancel" value="cancel" title="Cancel deletion" class="" src="images/close.png">
									</td>
								</tr>
						      </c:when>
						      <c:otherwise>
						   		<tr>
									<td class="buttonRow" align="right" >
										<input type="image" name="_ok" value="ok" title="Confirm deletion of ${oupsBean.opupStr} Instruction" class="" src="images/ok_h.png">
										<input type="image" name="_cancel" value="cancel" title="Close" class="" src="images/close.png">
									</td>
								</tr>
								</c:otherwise>
								</c:choose>
							</c:otherwise>
						   </c:choose>
					
					</c:if>
					<c:if test="${!oupsBean.exists}">
						<c:choose>
						      <c:when test="${oupsBean.deleteMode}">
							      <tr>
									<td class="buttonRow" align="right" >
										<input type="image" name="_ok" value="ok" title="Delete ${oupsBean.opupStr} Instruction" class="" src="images/ok_h.png">
										<input type="image" name="_cancel" value="cancel" title="Cancel deletion" class="" src="images/close.png">
									</td>
								</tr>
						      </c:when>
						      <c:otherwise>
						      <tr>
									<td class="buttonRow" align="right" >
										<input type="image" name="_apply" value="apply" title="Create ${oupsBean.opupStr} Instruction" class="" src="images/go_h.png">
										<input type="image" name="_cancel" value="cancel" title="Cancel " class="" src="images/cancel_h.png">
									</td>
								</tr>
						      
						      </c:otherwise>
						   </c:choose>
					
					</c:if>
					
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

