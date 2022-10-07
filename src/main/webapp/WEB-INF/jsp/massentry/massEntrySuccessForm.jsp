<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Mass Entry Results Page </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
 

</head>

<body class="main">
 
<form:form modelAttribute="promoResults">
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
	<tr>
		<td colspan="2">
		<div class="title">
			<c:out value="${promoResults.displayTitle}"/><br>
			 </div>
		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">
		
			<table>
				<tr>
					<td>
						<form:select path="subLinkSelect">
						<c:if test="${not promoResults.deduction and promoResults.canDoDeduction}">
							<form:option value="massDeductions">Add Deduction En Masse</form:option>
						</c:if>
						<c:if test="${not promoResults.loan and promoResults.canDoLoan}">
							<form:option value="massLoans">Add Loans En Masse</form:option>
						</c:if>
						<c:if test="${not promoResults.promotion and promoResults.canDoPromotion}">
							<form:option value="massPromotions">Promote En Masse</form:option>
						</c:if>
						<c:if test="${not promoResults.specialAllowance and promoResults.canDoSpecialAllow}">
							<form:option value="massSpecialAllowance">Add Special Allowance En Masse</form:option>
						</c:if>
						<c:if test="${not promoResults.transfer and promoResults.canDoTransfer}">
							<form:option value="massTransfer">Transfer En Masse</form:option>
						</c:if>
						<c:if test="${not promoResults.emailPayslips and promoResults.canEmailPayslips}">
                            <form:option value="massEmailPayslips">Email Payslips En Masse</form:option>
                         </c:if>
						</form:select> 
					</td>
					<td>&nbsp;</td>
					<td>
					<input type="image" name="_go" value="go" title="Go" class="" src="images/go_h.png" >
				</tr>
			</table>
		
		<table class="alignLeft hundredPercentWidth" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top" class="mainBody hundredPercentWidth verticalTopAlign" id="mainbody">
					 
						<tr>
							<td>
							<h4><font color="green"><b><c:out value="${promoResults.displayErrors}"/></b></font></h4>
							<c:if test="${promoResults.loan}">
							<h4><font color="green"><b><c:out value="${promoResults.terminationReason}"/></b></font></h4>
							<h4><font color="green"><b><c:out value="${promoResults.showArrearsRow}"/></b></font></h4>
                             <h4><font color="green"><b><c:out value="${promoResults.staffId}"/></b></font></h4>
                             <h4><font color="green"><b><c:out value="${promoResults.garnishmentName}"/></b></font></h4>
                           	</c:if>
                           	<c:if test="${promoResults.specialAllowance}">
                           	   <h4><font color="green"><b><c:out value="${promoResults.displayErrors}"/></b></font></h4>
                           	   <h4><font color="green"><b><c:out value="${promoResults.terminationReason}"/></b></font></h4>
                               <h4><font color="green"><b><c:out value="${promoResults.garnishmentName}"/></b></font></h4>
                           	</c:if>
							<br />
								<div class="panel-body">
										<table id="userTable" class="hundredPercentWidth table display" cellspacing="0" cellpadding="0">
													<c:if test="${promoResults.deduction  or promoResults.loan or promoResults.specialAllowance}"> 
													<h4><c:out value="${promoResults.name}"/></h4>
													<br />
													<thead>
														<tr>
															<th class="fivePercentWidth"><spring:message code="table.serialNo" /></th>
															<th class="tenPercentWidth"><spring:message code="table.empId" /></th>
															<th class="twentyPercentWidth"><spring:message code="table.staffName" /></th>
															<th class="twentyPercentWidth"><c:out value="${roleBean.mdaTitle}"/></th>
															<th class="tenPercentWidth"><spring:message code="table.levelStep" /></th>

														</tr>
													</thead>
													<tbody>
														<c:forEach items="${promoResults.beanList}" var="innerBean" varStatus="gridRow">
															<tr>
																<td>
																	<c:out value="${gridRow.index + 1}" />
																	 
																</td> 
																<td>
																	<a href="<c:url value="/employeeEnquiryForm.do?eid=${innerBean.employee.id}"/>"><c:out value="${innerBean.employee.employeeId}" /></a>
																	 
																</td>
																<td><c:out value="${innerBean.employee.displayName}" /></td>
																<td><c:out value="${innerBean.employee.currentMdaName}" /></td>
																<td><c:out value="${innerBean.employee.gradeLevelAndStep}" /></td>

															</tr>
														</c:forEach>
													</tbody>
													</c:if> 
													 <c:if test="${promoResults.promotion}"> 
													<h4><c:out value="${promoResults.name}"/></h4>
													<br />
													<thead>
														<tr>
															<th class="fivePercentWidth"><spring:message code="table.serialNo" /></th>
															<th class="tenPercentWidth"><spring:message code="table.empId" /></th>
															<th class="twentyPercentWidth"><spring:message code="table.staffName" /></th>
															<th class="twentyPercentWidth"><spring:message code="table.oldPayGroup" /></th>
															<th class="tenPercentWidth"><spring:message code="table.newPayGroup" /></th>
														    <th class="tenPercentWidth"><spring:message code="table.salar" /></th>
														</tr>
													</thead>
													<tbody>
														<c:forEach items="${promoResults.beanList}" var="innerBean" varStatus="gridRow">
															<tr>
																<td>
																	<c:out value="${gridRow.index + 1}" />
																	 
																</td> 
																<td>
																	<a href="<c:url value="/viewEmpPromoHistory.do?eid=${innerBean.employee.id}"/>"><c:out value="${innerBean.employee.employeeId}" /></a>
																	 
																</td>
																<td><c:out value="${innerBean.employee.displayName}" /></td>
																<td><c:out value="${innerBean.oldSalaryLevelAndStep}" /></td>
																<td><c:out value="${innerBean.newSalaryLevelAndStep}" /></td>
																<td><c:out value="${innerBean.amountStr}" /></td>
																 
															</tr>
														</c:forEach>
													</tbody>
													</c:if> 
				                                 <c:if test="${promoResults.transfer}"> 
													<h4><c:out value="${promoResults.name}"/></h4>
													<br />
													<thead>
														<tr>
															<th class="fivePercentWidth"><spring:message code="table.serialNo" /></th>
															<th class="tenPercentWidth"><spring:message code="table.empId" /></th>
															<th class="tenPercentWidth"><spring:message code="table.staffName" /></th>
															<th class="tenPercentWidth"><spring:message code="table.levelStep" /></th>
															<th class="tenPercentWidth"><spring:message code="table.fromMda" /></th>
															<th class="tenPercentWidth"><spring:message code="table.toMda" /></th>
														  
														</tr>
													</thead>
													<tbody>
														<c:forEach items="${promoResults.beanList}" var="innerBean" varStatus="gridRow">
															<tr>
																<td>
																	<c:out value="${gridRow.index + 1}" />
																	 
																</td> 
																<td>
																	<a href="<c:url value="/viewEmpPromoHistory.do?eid=${innerBean.employee.id}"/>"><c:out value="${innerBean.employee.employeeId}" /></a>
																	 
																</td>
																<td><c:out value="${innerBean.employee.displayName}" /></td>
																<td><c:out value="${innerBean.employee.gradeLevelAndStep}" /></td>
																<td><c:out value="${innerBean.employee.mdaName}" /></td>
																<td><c:out value="${innerBean.transferTo}" /></td>
															 
																 
															</tr>
														</c:forEach>
													</tbody>
													</c:if> 
				                                    <c:if test="${promoResults.emailPayslips}">
                 													<h4><c:out value="${promoResults.name}"/></h4>
                 													<br />
                 													<thead>
                 														<tr>
                 															<th class="fivePercentWidth"><spring:message code="table.serialNo" /></th>
                 															<th class="tenPercentWidth"><spring:message code="table.empId" /></th>
                 															<th class="tenPercentWidth"><spring:message code="table.staffName" /></th>
                 															<th class="tenPercentWidth"><spring:message code="table.levelStep" /></th>
                 														    <th class="tenPercentWidth"><c:out value="${roleBean.mdaTitle}" /></th>

                 														</tr>
                 													</thead>
                 													<tbody>
                 														<c:forEach items="${promoResults.beanList}" var="innerBean" varStatus="gridRow">
                 															<tr>
                 																<td>
                 																	<c:out value="${gridRow.index + 1}" />

                 																</td>
                 																<td>
                 																	<a href="<c:url value="/viewPayslipHistory.do?eid=${innerBean.id}"/>"><c:out value="${innerBean.employee.employeeId}" /></a>

                 																</td>
                 																<td><c:out value="${innerBean.employee.displayName}" /></td>
                 																<td><c:out value="${innerBean.employee.gradeLevelAndStep}" /></td>
                 																<td><c:out value="${innerBean.employee.assignedToObject}" /></td>



                 															</tr>
                 														</c:forEach>
                 													</tbody>
                 													</c:if>
				 
				 
				 
				 </table>
				 </div>
				 
				</td>
			</tr>
			
 			<tr>
                 <td class="buttonRow" align="right">
                  <input type="image" name="_cancel" value="cancel" title="Close Report" src="images/close.png">
                 </td>
            </tr>
            
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
	<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
	<script type="text/javascript">
		$(function() {

			$("#userTable").DataTable({
				"pageLength": 10,
				"order" : [ [ 2, "asc" ] ],
				//can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
				//also properties higher up, take precedence over those below
				"columnDefs":[
					{"targets": 0, "orderable" : false},
					{"targets": 5, "searchable" : false }
					//{"targets": [0, 1], "orderable" : false }
				]
			});
		});
	</script>
</body>

</html>
