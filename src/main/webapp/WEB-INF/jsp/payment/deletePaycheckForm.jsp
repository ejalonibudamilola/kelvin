<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Delete Single Paycheck Form  </title>
<link rel="stylesheet" href=<c:url value="styles/omg.css"/> type="text/css" />
<link rel="Stylesheet" href=<c:url value="styles/skye.css"/> type="text/css" media ="screen">
<script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
<script src="<c:url value="/scripts/utility_script.js"/>"></script>
</head>

<body class="main">
<form:form modelAttribute="payBean">
	<table class="main" width="70%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					
					<tr>
						<td colspan="2">
								<div class="title">Delete Paycheck for: <c:out value="${payBean.employee.displayName}"/> [ <c:out value="${payBean.employee.employeeId}"/> ]<br>
									
								</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
			
			<div id="topOfPageBoxedErrorMessage" style="display:${payBean.displayErrors}">
								 <spring:hasBindErrors name="payBean">
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
						<td class="activeTH"><b><c:out value="${payBean.employee.displayName}"/>'s Paycheck information</b></td>
					</tr>
					<tr>
						<td class="activeTD">
							 <fieldset>
							   <legend><b><c:out value="${roleBean.staffTypeName}"/> Details</b></legend>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
										<td width="25%" align="left"><c:out value="${roleBean.staffTypeName}"/> Name :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.employee.displayName}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left"><c:out value="${roleBean.staffTitle}"/> :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.employee.employeeId}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left"><c:out value="${roleBean.mdaTitle}"/> :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.employee.parentObjectName}"/></td>
									</tr>
									<c:choose>

									<c:when test="${roleBean.pensioner}">
	                                <tr>
										<td width="25%" align="left">Date of Birth :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.hiringInfo.birthDateStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Pension Start Date :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.hiringInfo.pensionStartDateStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Total Years As Pensioner :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.hiringInfo.yearsOnPension}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Monthly Pension :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.hiringInfo.monthlyPensionStr}"/></td>
									</tr>
									</c:when>
									<c:otherwise>


									<tr>
										<td width="25%" align="left">Date of Birth :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.hiringInfo.birthDateStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Hire Date :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.hiringInfo.hireDateStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Total Years in Service :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.hiringInfo.noOfYearsInService}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Pay Group - Level &amp; Step :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.employee.salaryInfo.salaryScaleLevelAndStepStr}"/></td>
									</tr>
									</c:otherwise>
                                  </c:choose>
									</table>						
								</fieldset>
								 <fieldset>
							   <legend><b>Pay Details</b></legend>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
										<td width="25%" align="left">Gross Pay :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.totalPayStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Total Deductions :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.totalDeductionsStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Net Pay :</td>
										<c:if test="${payBean.netPay ge 0 }">
											<td width="25%" align="left" colspan="2"><font color="green"><c:out value="${payBean.netPayStr}"/></font></td>
										</c:if>
										<c:if test="${payBean.netPay lt 0 }">
											<td width="25%" align="left" colspan="2"><font color="red"><c:out value="${payBean.netPayStr}"/></font></td>
										</c:if>
									</tr>
									<c:if test="${payBean.deductionAmount gt 0 }">
									<tr>
										<td width="25%" align="left"><b>Projected Net Pay :</b></td>
										 
										<td width="25%" align="left" colspan="2"><font color="green"><c:out value="${payBean.deductionAmountStr}"/><i>&nbsp;&nbsp;**After deletion and re-run</i></font>
										</td>
									 
										
									</tr>
									</c:if>
									<tr>
										<td width="25%" align="left">Taxes Paid :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.monthlyTaxStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Total Special Allowances :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.specialAllowanceStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Total Loan Deductions :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.totalGarnishmentsStr}"/></td>
									</tr>		
									</table>
													
								</fieldset>
								<c:if test="${payBean.warningIssued}">
								 <fieldset>
							   <legend><b>Re-Run Indicator</b></legend>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
										<td width="25%" align="left">Schedule for Rerun :</td>
										
										<td width="25%" align="left" colspan="2"><form:radiobutton path="reRunInd" value="1" />Yes <form:radiobutton path="reRunInd" value="2"/>No </td>
										
									</tr>
									<tr>
										<td width="25%" align="left">Generated Captcha :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.generatedCaptcha}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Entered Captcha :</td>
										<td width="25%" align="left" colspan="2"><form:input path="enteredCaptcha" size="5" maxlength="6"/>
										&nbsp;<font color="green">Case Insensitive</font></td>
										<c:if test="${payBean.captchaError}">
										  &nbsp;<font color="red"><b>Entered Captcha does not match!</b></font>
										</c:if>
									</tr>
									
									</table>
													
								</fieldset>
								</c:if>
							</td>
						</tr>
						
						<tr>
			                 <td class="buttonRow" align="right">
			                   <c:choose>
			                   	<c:when test="${payBean.warningIssued}">
			                   	 <input type="image" name="_confirm" value="confirm" title="Confirm Paycheck Deletion" src="images/confirm_h.png">&nbsp;
			                   	</c:when>
			                   	<c:otherwise>
			                   	<c:if test="${not saved and not payBean.warningIssued}">
			                   	 <input type="image" name="_delete" value="delete" title="Delete Paycheck" src="images/delete_h.png">&nbsp;
			                   	</c:if>
			                   	</c:otherwise>
			                   </c:choose>
			                     
			                      <input type="image" name="_cancel" value="cancel" title="Close Report" src="images/close.png">
			                  <br>
							  <br>
			                 </td>
           				 </tr>
            
						 
					</table>
					
					
					<br>
					<br>
					
				</td>
			</tr>
			</table>
			
				
			<br>
			<br>
			<br>
			
			</td>
			</tr>
			
			<tr>
			
				<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
			</tr>
		
		</table>
		
		
</form:form>
</body>
</html>
