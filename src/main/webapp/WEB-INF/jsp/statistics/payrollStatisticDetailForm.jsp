<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp"%>

<html>
	<head>
			<title>${pageTitle}</title>
			<link rel="stylesheet" href="styles/omg.css" type="text/css" />
			<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
			<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
	        <link rel="stylesheet" href="styles/spinner.css" TYPE="text/css" />
	</head>

<body class="main">
<div class="loader"></div>
	<table class="main" width="74%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
	
		<%@ include file="/WEB-INF/jsp/headerFile.jsp"%>
		
		<tr>
			<td>
				<table class="alignLeft hundredPercentWidth" border="0" cellpadding="0" cellspacing="0">

					<tr>
						<td>
							<div class="title">${mainHeader}</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody hundredPercentWidth verticalTopAlign" id="mainbody">
						
							
							
							<div>
							
							<div class="panel panel-danger">
												  <div class="panel-heading">
												    <h3 class="panel-title lead">
												    	<a class="linkWithUnderline" href="<c:url value="/payrollRunStatistics.do"/>"><spring:message code="stats.back"/></a>
													</h3>
												  </div>
								
												  <div class="panel-body">
													<table id="userTable" class="hundredPercentWidth table display" cellspacing="0" cellpadding="0">
													<c:if test="${statBean.objectInd eq 1 or statBean.objectInd eq 10 or statBean.objectInd eq 5 or statBean.objectInd eq 7 or statBean.objectInd eq 8}"> 
													<thead>
														<tr>
															<th class="fivePercentWidth"><spring:message code="table.serialNo" /></th>
															<th class="tenPercentWidth"><c:out value="${roleBean.staffTitle}"/></th>
															<th class="twentyPercentWidth"> <c:out value="${roleBean.staffTypeName}"/></th>
															<th class="tenPercentWidth"><spring:message code="table.levelStep" /></th>
															<th class="tenPercentWidth"><spring:message code="stats.totalPay" /></th>
															<th class="tenPercentWidth"><spring:message code="stats.totalDeductions" /></th>
															<th class="tenPercentWidth"><spring:message code="stats.netPay" /></th>
														</tr>
													</thead>
													<tbody>
														<c:forEach items="${statBean.objectList}" var="innerBean" varStatus="gridRow">
															<tr>
																<td>
																	<c:out value="${gridRow.index + 1}" />
																	 
																</td> 
																<td>
																	<a href="<c:url value="/paySlip.do?pid=${innerBean.id}&rm=${statBean.runMonth}&ry=${statBean.runYear}&s=${statBean.objectInd}"/>"><c:out value="${innerBean.mode}" /></a>
																	 
																</td>
																<td><c:out value="${innerBean.name}" /></td>
																<td><c:out value="${innerBean.salaryInfo.levelStepStr}" /></td>
																<td>
																	<c:out value="${innerBean.totalPayStr}" />
																</td>
																<td><c:out value="${innerBean.totalDeductionsStr}"/> </td>
																<td><c:out value="${innerBean.netPayStr}"/> </td>
															</tr>
														</c:forEach>
													</tbody>
													</c:if> 
													<c:if test="${statBean.objectInd eq 2 or statBean.objectInd eq 3}"> 
													<thead>
														<tr>
															<th class="fivePercentWidth"><spring:message code="table.serialNo" /></th>
															<th class="tenPercentWidth"><c:out value="${roleBean.staffTitle}"/></th>
															<th class="twentyPercentWidth"> <c:out value="${roleBean.staffTypeName}"/></th>
															<th class="tenPercentWidth"><spring:message code="table.levelStep" /></th>
															<c:if test="${statBean.objectInd eq 2}">
															<th class="tenPercentWidth"><spring:message code="stats.birthDate" /></th>
															</c:if>
															<c:if test="${statBean.objectInd eq 3}">
															<th class="tenPercentWidth"><spring:message code="stats.hireDate" /></th>
															</c:if>
															<th class="tenPercentWidth"><spring:message code="stats.retireDate" /></th>
 														</tr>
													</thead>
													<tbody>
														<c:forEach items="${statBean.objectList}" var="innerBean" varStatus="gridRow">
															<tr>
																<td>
																	<c:out value="${gridRow.index + 1}" />
																	 
																</td> 
																<td>
																	<a href="<c:url value="/paySlip.do?pid=${innerBean.id}&rm=${statBean.runMonth}&ry=${statBean.runYear}&s=${statBean.objectInd}"/>"><c:out value="${innerBean.mode}" /></a>
																	 
																</td>
																<td><c:out value="${innerBean.name}" /></td>
																<td><c:out value="${innerBean.salaryInfo.levelStepStr}" /></td>
																<c:if test="${statBean.objectInd eq 2}">
																<td>
																	<c:out value="${innerBean.birthDateStr}" />
																</td>
																</c:if>
																<c:if test="${statBean.objectInd eq 3}">
																<td>
																	<c:out value="${innerBean.hireDateStr}" />
																</td>
																</c:if>
																<td><c:out value="${innerBean.expectedDateOfRetirementStr}"/> </td>
																 
															</tr>
														</c:forEach>
													</tbody>
													</c:if> 
													<c:if test="${statBean.objectInd eq 4 or statBean.objectInd eq 6}"> 
													<thead>
														<tr>
															<th class="fivePercentWidth"><spring:message code="table.serialNo" /></th>
															<th class="tenPercentWidth"><spring:message code="table.empId" /></th>
															<th class="twentyPercentWidth"><spring:message code="table.staffName" /></th>
															<th class="tenPercentWidth"><spring:message code="table.levelStep" /></th>
															 
 														</tr>
													</thead>
													<tbody>
														<c:forEach items="${statBean.objectList}" var="innerBean" varStatus="gridRow">
															<tr>
																 <td>
																	<c:out value="${gridRow.index + 1}" />
																	 
																</td>
																<td>
																	<a href="<c:url value="/paySlip.do?pid=${innerBean.id}&rm=${statBean.runMonth}&ry=${statBean.runYear}&s=${statBean.objectInd}"/>"><c:out value="${innerBean.mode}" /></a>
																	 
																</td>
																<td><c:out value="${innerBean.name}" /></td>
																<td><c:out value="${innerBean.salaryInfo.levelStepStr}" /></td>
																 
															</tr>
														</c:forEach>
													</tbody>
													</c:if> 
													<c:if test="${statBean.objectInd eq 9}"> 
													<thead>
														<tr>
															<th class="fivePercentWidth"><spring:message code="table.serialNo" /></th>
															<th class="tenPercentWidth"> <c:out value="${roleBean.staffTitle}"/></th>
															<th class="twentyPercentWidth"> <c:out value="${roleBean.staffTypeName}"/></th>
															<th class="tenPercentWidth"><spring:message code="table.levelStep" /></th>
															<th class="tenPercentWidth"><spring:message code="stats.specAllow" /></th>
															<th class="tenPercentWidth"><spring:message code="stats.totalPay" /></th>
															<th class="tenPercentWidth"><spring:message code="stats.totalDeductions" /></th>
															<th class="tenPercentWidth"><spring:message code="stats.netPay" /></th>
															 
 														</tr>
													</thead>
													<tbody>
														<c:forEach items="${statBean.objectList}" var="innerBean" varStatus="gridRow">
															<tr>
																 <td>
																	<c:out value="${gridRow.index + 1}" />
																	 
																</td>
																<td>
																	<a href="<c:url value="/paySlip.do?pid=${innerBean.id}&rm=${statBean.runMonth}&ry=${statBean.runYear}&s=${statBean.objectInd}"/>"><c:out value="${innerBean.mode}" /></a>
																	 
																</td>
																<td><c:out value="${innerBean.name}" /></td>
																<td><c:out value="${innerBean.salaryInfo.levelStepStr}" /></td>
																<td><c:out value="${innerBean.specAllowStr}"/></td>
																<td>
																	<c:out value="${innerBean.totalPayStr}" />
																</td>
																<td><c:out value="${innerBean.totalDeductionsStr}"/> </td>
																<td><c:out value="${innerBean.netPayStr}"/> </td>
															</tr>
														</c:forEach>
													</tbody>
													</c:if> 
													<c:if test="${statBean.objectInd eq 11}"> 
													<thead>
														<tr>
															<th class="fivePercentWidth"><spring:message code="table.serialNo" /></th>
															<th class="tenPercentWidth"> <c:out value="${roleBean.staffTitle}"/></th>
															<th class="twentyPercentWidth"> <c:out value="${roleBean.staffTypeName}"/></th>
															<th class="tenPercentWidth"><spring:message code="table.levelStep" /></th>
														    <th class="tenPercentWidth"><spring:message code="stats.retireDate" /></th>
														     <th class="tenPercentWidth"><spring:message code="stats.termDate" /></th>
 														</tr>
													</thead>
													<tbody>
														<c:forEach items="${statBean.objectList}" var="innerBean" varStatus="gridRow">
															<tr>
																 <td>
																	<c:out value="${gridRow.index + 1}" />
																	 
																</td>
																<td>
																	<a href="<c:url value="/paySlip.do?pid=${innerBean.id}&rm=${statBean.runMonth}&ry=${statBean.runYear}&s=${statBean.objectInd}"/>"><c:out value="${innerBean.mode}" /></a>
																	 
																</td>
																<td><c:out value="${innerBean.name}" /></td>
																<td><c:out value="${innerBean.salaryInfo.levelStepStr}" /></td>
																 
																<td>
																	<c:out value="${innerBean.expectedDateOfRetirementStr}" />
																</td>
															 
																<td><c:out value="${innerBean.terminationDateStr}"/> </td>
																 
															</tr>
														</c:forEach>
													</tbody>
													</c:if> 
													<c:if test="${statBean.objectInd eq 12 or statBean.objectInd eq 13 or statBean.objectInd eq 14}">
													<thead>
														<tr>
															<th class="fivePercentWidth"><spring:message code="table.serialNo" /></th>
															<th class="tenPercentWidth"> <c:out value="${roleBean.staffTitle}"/></th>
															<th class="twentyPercentWidth"> <c:out value="${roleBean.staffTypeName}"/></th>
															<th class="tenPercentWidth"><spring:message code="table.levelStep" /></th>
															<c:if test="${statBean.objectInd eq 12}"> 
														    <th class="tenPercentWidth"><spring:message code="stats.hireDate" /></th>
														     <th class="tenPercentWidth"><spring:message code="stats.birthDate" /></th>
														     </c:if>
														     <c:if test="${statBean.objectInd eq 13}"> 
														       <th class="tenPercentWidth"><spring:message code="stats.contract.startDate" /></th>
														       <th class="tenPercentWidth"><spring:message code="stats.contract.endDate" /></th>
														     </c:if>
                                                             <c:if test="${statBean.objectInd eq 14}">
														       <th class="tenPercentWidth"><spring:message code="stats.pensionDate" /></th>
														       <th class="tenPercentWidth"><spring:message code="stats.yearlyPension" /></th>
														       <th class="tenPercentWidth"><spring:message code="stats.monthlyPension" /></th>
														     </c:if>
 														</tr>
													</thead>
													<tbody>
														<c:forEach items="${statBean.objectList}" var="innerBean" varStatus="gridRow">
															<tr>
																  <td>
																	<c:out value="${gridRow.index + 1}" />
																	 
																</td>
																<td>
																	<a href="<c:url value="/paySlip.do?pid=${innerBean.id}&rm=${statBean.runMonth}&ry=${statBean.runYear}&s=${statBean.objectInd}"/>"><c:out value="${innerBean.mode}" /></a>
																	 
																</td>
																<td><c:out value="${innerBean.name}" /></td>
																<td><c:out value="${innerBean.salaryInfo.levelStepStr}" /></td>
																 <c:if test="${statBean.objectInd eq 12}"> 
																<td>
																	<c:out value="${innerBean.hireDateStr}" />
																</td>
															 
																<td><c:out value="${innerBean.birthDateStr}"/> </td>
																</c:if>
																 <c:if test="${statBean.objectInd eq 13}"> 
																 <td>
																	<c:out value="${innerBean.allowanceStartDateStr}" />
																</td>
															 
																<td><c:out value="${innerBean.allowanceEndDateStr}"/> </td>
																</c:if>
                                                                 <c:if test="${statBean.objectInd eq 14}">
																 <td>
																	<c:out value="${innerBean.hireDateStr}" />
																</td>

																<td><c:out value="${innerBean.annualPensionStr}"/> </td>
																<td><c:out value="${innerBean.monthlyPensionStr}"/> </td>
																</c:if>
															</tr>
														</c:forEach>
													</tbody>
													</c:if> 
												</table>
												  </div>
												</div>
						
							</div>	
						</td>
					</tr>

				</table>
				 <div class="panel-heading">
												    <h3 class="panel-title lead">
												    	<a class="linkWithUnderline" href="<c:url value="/payrollStatExcelReport.do?ind=${statBean.objectInd}&rm=${statBean.runMonth}&ry=${statBean.runYear}"/>"><spring:message code="stats.excel"/></a>
													</h3>
				</div>
			</td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp"%>
		</tr>
	</table>
	<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
	<script type="text/javascript">
		$(function() {

			$("#userTable").DataTable({
				"pageLength": 50,
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
		window.onload = function exampleFunction() {
           $(".loader").hide();
        }
	</script>
</body>
</html>