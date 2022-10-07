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
	<table class="main" width="74%" border="1" cellspacing="0" cellpadding="0" align="center">

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





												</table>
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

</body>
</html>