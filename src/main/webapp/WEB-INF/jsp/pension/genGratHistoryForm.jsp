<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
<head>
<title>
            Gratuity Payments History Report
        </title>

<link rel="stylesheet" href="styles/omg.css" type="text/css">
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css" />
<link rel="stylesheet" href="css/screen.css" type="text/css" />
</head>
<body class="main">

<form:form modelAttribute="gratPayHistBean">
	<c:set value="${gratPayHistBean}" var="dispBean" scope="request" />
	<table class="main" width="75%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
			<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

				<tr>
					<td colspan="2">
					<div class="title">Year To Date Gratuity Report
					<br>
					Gratuity Payment History</div>
					</td>
				</tr>
				<tr>
					<td valign="top" class="mainBody" id="mainbody">
					<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}"><spring:hasBindErrors
						name="gratPayHistBean">
						<ul>
							<c:forEach var="errMsgObj" items="${errors.allErrors}">
								<li><spring:message code="${errMsgObj.code}"
									text="${errMsgObj.defaultMessage}" /></li>
							</c:forEach>
						</ul>
					</spring:hasBindErrors></div>
					<table class="reportMain" cellpadding="0" cellspacing="0"> <tr align="left">
							<td class="activeTH"> Gratuity Payments</td>
						</tr>

						<tr>
							<td>&nbsp;</td>

						</tr>
						<tr>
							<td valign="top">
							<p class="label">Gratuity History</p>
							<display:table name="dispBean" class="register3" sort="page" defaultsort="1" requestURI="${appContext}/gratuityHistoryReport.do">
								<display:column property="name" title="${roleBean.mdaTitle}" style="text-align:center;"></display:column>
								<display:column property="totalNoOfEmployees" title="No Of Staff" style="text-align:center;"></display:column>
								<display:column property="totalGrossPayStr" title="Amount Paid" style="text-align:center;"></display:column>
                                <display:setProperty name="paging.banner.placement" value="bottom" />
							</display:table></td>
						</tr>
						<!--<tr>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td><a href='${appContext}/staffGratuityExcel.do?ry=${gratPayHistBean.id}'><i>Send to Microsoft&copy; Excel&copy;</i></a></td>
						</tr>-->
					</table>

					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp"%>
		</tr>
	</table>
</form:form>
</body>
</html>
