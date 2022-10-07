<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Delete Payroll Run Form  </title>
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
								<div class="title">Delete Payroll Run<br>
									
								</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
			
			<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
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
						<td class="activeTH"><b><c:out value="${payBean.runMonthYearStr}"/> Payroll information</b></td>
					</tr>
					<tr>
						<td class="activeTD">
							 <fieldset>
							   <legend><b> <c:out value="${roleBean.staffTypeName}"/> Details</b></legend>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
										<td width="25%" align="left">No. of <c:out value="${roleBean.staffTypeName}"/>s Processed :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.totalEmpProcessed}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">No. of <c:out value="${roleBean.staffTypeName}"/>s Paid :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.totalEmpPaid}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">No. of <c:out value="${roleBean.staffTypeName}"/>s Retired :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.totalEmpRetired}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">No. of <c:out value="${roleBean.staffTypeName}"/>s Not Paid :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.totalEmpNotPaid}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">No. of <c:out value="${roleBean.staffTypeName}"/>s With Negative pay :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.totalEmpWithNegPay}"/></td>
									</tr>
									
									</table>						
								</fieldset>
								 <fieldset>
							   <legend><b>Pay Details</b></legend>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
										<td width="25%" align="left">Total Gross Pay :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.totalPayStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Total Deductions :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.totalDeductionsStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Total Special Allowances :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.specialAllowanceStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Taxes Paid :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.monthlyTaxStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Total Loan Deductions :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.totalGarnishmentsStr}"/></td>
									</tr>		
									<tr>
										<td width="25%" align="left">Net Pay :</td>
										 <td width="25%" align="left" colspan="2"><font color="green"><c:out value="${payBean.netPayStr}"/></font></td>
										
									</tr>
									
									<c:if test="${payBean.deleteWarningIssued}">
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
									</c:if>
									</table>
													
								</fieldset>
								
							</td>
						</tr>
						
						<tr>
			                 <td class="buttonRow" align="right">
			                   <c:choose>
			                   	<c:when test="${payBean.deleteWarningIssued}">
			                   	 <input type="image" name="_confirm" value="confirm" title="Confirm Payroll Deletion" src="images/confirm_h.png">&nbsp;
			                   	</c:when>
			                   	<c:otherwise>
			                   	<c:if test="${not saved and not payBean.deleteWarningIssued}">
			                   	 <input type="image" name="_delete" value="delete" title="Delete Payroll" src="images/delete_h.png">&nbsp;
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
