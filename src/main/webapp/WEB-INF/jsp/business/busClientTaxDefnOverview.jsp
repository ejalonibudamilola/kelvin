<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<html>
<head>
<title>Client Tax Definition Overview</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css"
	media="screen">
</head>

<body class="main">
<form:form modelAttribute="clientTaxPolicy">
<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
	<tr>
		<td>
		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			
			<tr>
				<td colspan="2">
				<div class="title">View Tax Policies</div>
				</td>
			</tr>
			 <tr>
				<td>&nbsp;</td>
			 </tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">
				<table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left">
					<tr align="left">
						<th class="activeTH">Company Defined Tax Policies</th>
					</tr>
					<tr>
						<td class="activeTD">
						<table border="0" cellspacing="0" cellpadding="3">
							<tr>
								<td>These are your company defined Tax Policies. To add
								another tax policy, please follow the "Define New Tax Policy" link below.</td>
							</tr>
							<tr>
								<td>
								<table width="100%" border="0" cellspacing="0" cellpadding="2">
									<tr>
										<td><b>Description</b></td>
										<td><b>Range</b></td>
										<td><b>Taxable %age</b></td>
										<td><b>Tax Rate</b></td>
										<td><b>Tax Amount</b></td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
									</tr>
									<c:forEach items="${clientTaxPolicy}" var="taxPolicy">
										<tr>
											<td width="25%">${taxPolicy.description}</td>
											<td width="25%">${taxPolicy.lowerBoundStr}&nbsp;-&nbsp;${taxPolicy.upperBoundStr}</td>
											<td width="25%" align="center">${taxPolicy.taxablePercentageStr}</td>
											<td width="25%" align="left">${taxPolicy.taxRateStr}</td>
											<td width="25%" align="left">${taxPolicy.taxAmountStr}</td>
											<c:choose>
												<c:when test="${taxPolicy.active}">
													<td align="right"><a href='${appContext}/busClientTaxDefinitions.do?oid=${taxPolicy.id}'>Edit</a></td>
												</c:when>
												<c:otherwise>
													<td align="right"><h5>(Expired)</h5></td>
												</c:otherwise>
											</c:choose>
										</tr>
									</c:forEach>
									
								</table>
								</td>
							</tr>
							<tr>
								<td>
								<table width="40%" border="0" cellspacing="0" cellpadding="5" align="right">
									<tr>
										<td align=right colspan=1><a href='${appContext}/busClientTaxDefinitions.do' id="SubmitTaxPolicy" onclick="">Define New Tax Policy</a>
										</td>
									</tr>
								</table>
								</td>
							</tr>
						</table>
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
</body>
</html>
