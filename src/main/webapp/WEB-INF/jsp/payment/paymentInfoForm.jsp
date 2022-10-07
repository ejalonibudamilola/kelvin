<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>

<html>
<head>
<title>Edit <c:out value="${roleBean.staffTypeName}"/> Payment Method</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
 <link rel="stylesheet" href="<c:url value="/styles/epayroll.css"/>" type="text/css"/>
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.4.4.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>

</head>

<body class="main">
<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
	<tr>
		<td>

		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			
			
			<tr>
				<td colspan="2">
				<div class="title">Edit <c:out value="${namedEntity.name}"/>'s Pay Method Info</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">* = Required<br />
				<br />
				<form:form modelAttribute="paymentMethodInfo">
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="paymentMethodInfo">
         							 <ul>
            							<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            							</c:forEach>
         								</ul>
     							 </spring:hasBindErrors>
							</div>
					<table border=0 cellspacing=0 cellpadding=0 width="75%">
						<tr>
							<td>
							<table class="formtable" border=0 cellspacing=0 cellpadding=3 width=100% align="left">
								<tr align="left">
									<td class="activeTH">Payment Method</td>
								</tr>
								<tr>
									<td id="firsttd_editpayment_form" class="activeTD">
									<table border="0" cellpadding="2" cellspacing="2">
										<tr>
											<td colspan="2"><span class="required alt">I'll pay <c:out value="${namedEntity.name}"/> by*</span></td>
										</tr>
										<tr>
											<td valign="top">
											<form:radiobutton path="paymentTypeRef" value="1" onclick="if (this.checked) { document.getElementById('hiderow').style.display = ''; document.getElementById('hiderow2').style.display = '';document.getElementById('hiderow3').style.display = 'none';document.getElementById('hiderow4').style.display = 'none';}"/></td>
											<td>Direct deposit to a Bank account**</td>
										</tr>
										<tr>
											<td valign="top">
											<form:radiobutton path="paymentTypeRef" disabled="true" value="0" onclick="if (this.checked) { document.getElementById('hiderow').style.display = 'none' ;document.getElementById('hiderow2').style.display = 'none'; document.getElementById('hiderow3').style.display = 'none';document.getElementById('hiderow4').style.display = 'none';}"/>
											</td>
											<td>Cheque<br>
											</td>
										</tr>
										<tr>
											<td valign="top">
											<form:radiobutton path="paymentTypeRef" disabled="true" value="2" onclick="if (this.checked) { document.getElementById('hiderow').style.display = 'none' ;document.getElementById('hiderow2').style.display = 'none'; document.getElementById('hiderow3').style.display = '';document.getElementById('hiderow4').style.display = '';}"/>
											</td>
											<td>Cash Card<br>
											</td>
										</tr>
										<tr>
											<td colspan="2">&nbsp;</td>
										</tr>
										<tr>
											<td colspan="2">Note: To direct deposit their pay, your <c:out value="${roleBean.staffTypeName}"/> must have a valid Bank account.</td>
										</tr>
									</table>
									</td>
								</tr>
								<tr id="hiderow3" style="display:${paymentMethodInfo.cashCardShowRow}">
										<td class="activeTH" align="left" colspan="20">Cash Card Information</td>
								</tr>
								<tr id="hiderow4" style="display:${paymentMethodInfo.cashCardShowRow}">
									<td class="activeTD">
									<table border="0" cellspacing="2" cellpadding="2">
									<tr>
					              			<td width="35%" align=right><span class="required">Bank Name*</span></td>
					              			<td>   
					                		<form:select path="cashCardBankId">
					                		<form:option value="0">&lt;Please select &gt;</form:option>
					                		<c:forEach items="${bankInfo}" var="banks">
					                		<form:option value="${banks.id}" >${banks.name}</form:option>
					                		</c:forEach>
					                		</form:select>
					             			 </td>
					              			
					            	</tr>		
									<tr>
									<td width="35%" align="right"><span class="required">Cash Card Number*</span></td>
									<td><form:input path="cashCardNumber" maxlength="19" /></td>
									</tr>
									<tr>
									<td width="35%" align="right"><span class="required">Expiration Date*</span></td>
									<td width="35%">
										<form:input path="expirationDate"/>
										<img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('expirationDate'),event);">
										</td>
						        
									</tr>
									</table>
									</td>
								</tr>
								
								<tr id="hiderow" style="display:${paymentMethodInfo.showRow}">
										<td class="activeTH" align="left" colspan="20">Direct deposit information</td>
								</tr>
								<tr id="hiderow2" style="display:${paymentMethodInfo.showRow}">
									<td class="activeTD">
									<table border="0" cellspacing="2" cellpadding="2">
										<tr>
											<td colspan="2">Select the <b>Bank</b>
											<br/>
											<br/><img src="images/theCheck.png" width="440" height="220" title="check with routing number highlighted" border="0" /> <br>
											Enter the <b>Account Number</b>, found on the bottom of the
											check (highlighted in ORANGE). <br/>
											&nbsp;&nbsp;Please omit any spaces, hyphens or other symbols.
											</td>
										</tr>
										<tr>
					              			<td width="35%" align="right"><span class="required">Bank Name*</span></td>
					              			<td>   
					                		<form:select path="directDepositBankId" onchange='loadBankBranchesByBankId(this)'>
					                		<form:option value="0">&lt;Please select &gt;</form:option>
					                		<c:forEach items="${bankInfo}" var="banks">
					                		<form:option value="${banks.id}" >${banks.name}</form:option>
					                		</c:forEach>
					                		</form:select>
					             			 </td>
					              			
					            		</tr>
					            		<tr>
					              			<td width="35%" align="right"><span class="required">Bank Branch*</span></td>
					              			<td>   
					                		<form:select path="bankBranches.id" id="bank-branch-control" cssClass="branchControls">
					                		<form:option value="0">&lt;Please select &gt;</form:option>
					                		<c:forEach items="${bankBranches}" var="branchList">
					                		<form:option value="${branchList.id}" >${branchList.name}</form:option>
					                		</c:forEach>
					                		</form:select>
					             			 </td>
					            		</tr>
					            		<tr>
											<td width="35%" align="right" title="Bank Verification Number"><span class="required">BVN*</span></td>
											<td><form:input path="bvnNo" maxlength="20" size="20" disabled="${paymentMethodInfo.terminated}"/></td>
										</tr>											
										<tr>
											<td width="35%" align="right"><span class="required">Account Number*</span></td>
											<td><form:input path="accountNumber" maxlength="20" size="20" disabled="${paymentMethodInfo.terminated}"/></td>
										</tr>
										<tr>
											<td width="35%" align="right"><span class="required">Confirm Account Number*</span></td>
											<td><form:input path="accountNumber2" maxlength="20" size="20" disabled="${paymentMethodInfo.terminated}"/></td>
										</tr>
										<tr>
											<td width="35%" align="right"><span class="required">Account Type*</span></td>
											<td><form:radiobutton path="accountType" value="C" disabled="${paymentMethodInfo.terminated}"/> Current Account &nbsp; 
											<form:radiobutton path="accountType" value="S" disabled="${paymentMethodInfo.terminated}"/>Savings Account
											</td>
										</tr>
										<c:if test="${paymentMethodInfo.needsOgNumber}">
                                         <tr>
                                        	 <td width="35%" align="right"><span class="required">Inheriting From*</span></td>
                                        	 <td><form:input path="inheritOgNumber" maxlength="20" size="20" disabled="${paymentMethodInfo.terminated}"/></td>
                                         </tr>
                                         </c:if>
                                         <c:if test="${paymentMethodInfo.ogNumberNotEmpty}">
                                        	 <tr>
                                        		 <td width="35%" align="right"><span class="required">Inheriting From*</span></td>
                                        		 <td><form:input path="inheritOgNumber" maxlength="11" size="9" disabled="true"/></td>
                                        	 </tr>
                                        	 <tr>
                                        		 <td width="35%" align="right"><span class="required">Created By*</span></td>
                                        		 <td><form:input path="shareAccountCreator.actualUserName" maxlength="20" size="20" disabled="true"/></td>
                                        	 </tr>
                                         </c:if>
									  </table>
									</td>
								</tr>
								
							</table>
							</td>
						</tr>
						<tr>
							<td class="buttonRow" align="right">
							<c:choose>
							  <c:when test="${paymentMethodInfo.terminated}">
								 <input type="image" name="_cancel" value="cancel" title="Cancel" src="images/cancel_h.png">
							  </c:when>
							  <c:otherwise>
							 <input type="image" name="submit" value="ok" title="Submit" class="" src="images/ok_h.png">&nbsp;
							  <input type="image" name="_cancel" value="cancel" title="Cancel" 	src="images/cancel_h.png">
						
							  </c:otherwise>
							</c:choose>
							</td>
							</tr>
					</table>
					<table border="0" cellpadding="2" cellspacing="2">
						<tr>
							<td class="footerText">Please note that a number of states
							have laws that require that paychecks be payable at full face
							value, without discount. For guidance on the applicability of
							these laws to your company and payroll service fees, please
							contact your legal advisor.</td>
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
