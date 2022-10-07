<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp"%>

<html>
	<head>
			<title>${pageTitle}</title>
			<link rel="stylesheet" href="styles/omg.css" type="text/css" />
			<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
			<link href="css/datatables.min.css" rel="stylesheet">
	</head>

<body class="main">
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
												    	<a class="linkWithUnderline" href="<c:url value="/editMda.do"/>"><spring:message code="mdas.view.addNew"/></a>
													</h3>
												  </div>
												  <div class="panel-body">
															<table id="mdaTable" class="hundredPercentWidth table display" cellspacing="0" cellpadding="0">
													<thead>
														<tr>
															<th class="twentyPercentWidth"><c:out value="${roleBean.mdaTitle}" /></th>
															<th class="twentyPercentWidth"><spring:message code="mdas.view.table.header.codeName" /></th>
															<th class="tenPercentWidth"><spring:message code="mdas.view.table.header.deactivated" /></th>
															<th class="tenPercentWidth"><spring:message code="mdas.view.table.header.lastModBy" /></th>
															<th class="twentyPercentWidth"><spring:message code="mdas.view.table.header.lastModTs" /></th>
														</tr>
													</thead>
													<tbody>
														<c:forEach items="${mdas}" var="mda" varStatus="currIndex">
															<tr>
																<td>
																	<a href="${appContext}/editMda.do?mid=${mda.id}">
																		<c:out value="${mda.name}" />
																	</a>
																</td>
																<td>
																	<c:out value="${mda.codeName}" />
																</td>
																<td>
																	<c:out value="${mda.deactivatedIndicator eq 1 ? 'Inactive' : 'Active'}" />
																</td>	
																<td><c:out value="${mda.lastModBy.actualUserName}" /></td>
																
																<td><fmt:formatDate value="${mda.lastModTs}" pattern="MMM dd, yyyy"/> </td>
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
	<script src="scripts/datatables.min.js"></script>
	<script type="text/javascript">
		$(function() {
			$("#mdaTable").DataTable({

			});
		});
	</script>
</body>
</html>