<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Delete Payroll Rerun Record Form  </title>
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
								<div class="title">Delete Payroll Rerun Record for: <c:out value="${payBean.hiringInfo.abstractEmployeeEntity.displayName}"/> [ <c:out value="${payBean.hiringInfo.abstractEmployeeEntity.employeeId}"/> ]<br>
									
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
						<td class="activeTH"><b><c:out value="${payBean.hiringInfo.abstractEmployeeEntity.displayName}"/>'s information</b></td>
					</tr>
					<tr>
						<td class="activeTD">
							 <fieldset>
							   <legend><b><c:out value="${roleBean.staffTypeName}"/> Details</b></legend>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
										<td width="25%" align="left"><c:out value="${roleBean.staffTypeName}"/> Name :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.hiringInfo.abstractEmployeeEntity.displayName}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left"><c:out value="${roleBean.staffTitle}"/> :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.hiringInfo.abstractEmployeeEntity.employeeId}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left"><c:out value="${roleBean.mdaTitle}"/> :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.hiringInfo.abstractEmployeeEntity.currentMdaName}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Date of Birth :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.hiringInfo.birthDateStr}"/></td>
									</tr>
									<c:choose>
									<c:when test="${roleBean.pensioner}">
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
										<td width="25%" align="left">Hire Date :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.hiringInfo.hireDateStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Total Years in Service :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.hiringInfo.noOfYearsInService}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Pay Group - Level &amp; Step :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.hiringInfo.abstractEmployeeEntity.gradeLevelAndStep}"/></td>
									</tr>
									</c:otherwise>
									</c:choose>

									
									</table>						
								</fieldset>
								<c:if test="${not roleBean.pensioner}">
								 <fieldset>
							   <legend><b>Pay Group Details</b></legend>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">

									<tr>
										<td width="25%" align="left">Monthly Basic Salary :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.hiringInfo.abstractEmployeeEntity.salaryInfo.monthlyBasicSalaryStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Pay Group Allowances :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${payBean.hiringInfo.abstractEmployeeEntity.salaryInfo.monthlyConsolidatedAllowanceStr}"/></td>
									</tr>
									
									
									</table>
													
								</fieldset>
								</c:if>


					</table>
					
					
					<br>
					<br>


			<br>
			<br>

			<tr class="buttonRow" align = "right">
                             <td >
                               <c:choose>
                               	<c:when test="${payBean.deleteWarningIssued}">
                               	 <input type="image" name="_confirm" value="confirm" title="Confirm Deletion" src="images/confirm_h.png">&nbsp;
                               	</c:when>
                               	<c:otherwise>
                               	 <input type="image" name="_delete" value="delete" title="Delete Record" src="images/delete_h.png">&nbsp;

                               	</c:otherwise>
                               </c:choose>

                                  <input type="image" name="_cancel" value="cancel" title="Close Report" src="images/close.png">
                              <br>
            				  <br>
                             </td>
                        </tr>


			</td>
			</tr>


		
		</table>
		<tr>

                                				<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
                                			</tr>
		
</form:form>
</body>
</html>
