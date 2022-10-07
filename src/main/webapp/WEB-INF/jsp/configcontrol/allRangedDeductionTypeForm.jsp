<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp"%>

<html>
	<head>
			<title>${pageTitle}</title>
			<link rel="stylesheet" href="styles/omg.css" type="text/css" />
			<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
			<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
	</head>
<body class="main">
	<table class="main" width="74%" bordercolor="#33c0c8" border="1" cellspacing="0" cellpadding="0" align="center">

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
							                     <c:if test="${canCreate}">
												  <div class="panel-heading">
												    <h3 class="panel-title lead">
												    	<%--<a class="linkWithUnderline" href="<c:url value="/addRangedDeduction.do"/>"><spring:message code="pay.percent.view.addNew"/></a>--%>
												    	<a class="linkWithUnderline" href="<c:url value="/addRangedDeduction.do"/>">Add New Ranged Deduction</a>
													</h3>
												  </div>
												  </c:if>
												  <div class="panel-body">
															<table id="userTable" class="display table" cellspacing="0" cellpadding="0">
													<thead>
														<tr>
															<th class="twentyPercentWidth"><spring:message code="pay.percent.name" /></th>
															<th class="twentyPercentWidth"><spring:message code="pay.percent.created-by" /></th>
															<th class="twentyPercentWidth"><spring:message code="pay.percent.created.date" /></th>
															<th class="tenPercentWidth"><spring:message code="mdas.view.table.header.lastModBy" /></th>

														</tr>
													</thead>
													<tbody>
														<c:forEach items="${miniBean}" var="perConfig" varStatus="currIndex">
															<tr>
																<td>
																	<a href="${appContext}/editRangedDeduction.do?oid=${perConfig.id}">
																		<c:out value="${perConfig.name}" />
																	</a>
																</td>
																<td>
																	<c:out value="${perConfig.createdBy.actualUserName}" />
																</td>
																<td>
																	<c:out value="${perConfig.createdDateStrForDisplay}" />
																</td>
																<td>
																	<c:out value="${perConfig.lastModBy.actualUserName}" />
																</td>


															</tr>
														</c:forEach>
													</tbody>
												</table>
												  </div>
												</div>

							</div>
						</td>
					</tr>

				</table>
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
				"order" : [ [ 1, "asc" ] ],
				//can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
				//also properties higher up, take precedence over those below
				"columnDefs":[
					{"targets": 0, "orderable" : false},
					{"targets": 4, "searchable" : false }
					//{"targets": [0, 1], "orderable" : false }
				]
			});
		});
	</script>
</body>
</html>