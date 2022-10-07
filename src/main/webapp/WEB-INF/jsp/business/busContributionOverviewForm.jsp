<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<html>
<head>
<title>Company Contributions Overview</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css"
	media="screen">
</head>

<body class="main">
<table class="main" width="70%" border="1" bordercolor="#33c0c8"
	cellspacing="0" cellpadding="0" align="center">
	
	<tr>
		<td>
		<table width="100%" align="center" border="0" cellpadding="0"
			cellspacing="0">
			
			<tr>
				<td colspan="2">
				<div class="title">View Company Contributions</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">
				<table class="formTable" border="0" cellspacing="0" cellpadding="3"
					width="100%" align="left">
					<tr align="left">
						<th class="activeTH">Company Contributions</TH>
					</tr>
					<tr>
						<td class="activeTD">
						<table border="0" cellspacing="0" cellpadding="3">
							<tr>
								<td>These are your company's paycheck contributions. To add
								another company contribution, please follow the "Add new
								contribution" link below.</td>
							</tr>
							<tr>
								<td>
								<table width="100%" border="0" cellspacing="0" cellpadding="2">
									<tr>
										<td><b>Description</b></td>
										<td><b>Type</b></td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
									</tr>
									<c:forEach items="${compContList}" var="coyCompList">
										<tr>
											<td width="25%">${coyCompList.description}</td>
											<td width="20%">${coyCompList.companyContributionType.name}</td>
											<td align="right"><a href='${appContext}/busContributionForm.do?cid=${coyCompList.id}&atn=e'>Edit</a>
											</td>
										</tr>
									</c:forEach>
									
								</table>
								</td>
							</tr>
							<tr>
								<td>
								<table width="40%" border="0" cellspacing="0" cellpadding="5"
									align="right">
									<tr>
										<td align=right colspan=1><a href='${appContext}/busContributionForm.do'
											id="SubmitDeductionInfo" onclick="">Add new contribution</a>
										</td>
									</tr>
								</table>
								</td>
							</tr>
						</table>
						</td>
					</tr>
					<tr>
						<td><br />
						<br />
						These company contributions are available for your whole company,
						and you can assign them to individual employees. Other Deductions,
						such as child support and tax levies, apply to individual
						employees. <br>
						<br>
						To enter a deduction, company contribution or an other deduction for an
						individual employee, go to the <a href="${appContext}/busEmpOverviewForm.do">Employee
						Overview</a>. Click an employee's name, then click <b>Deductions,
						Contributions & Other Deductions</b>.</td>
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
</body>
</html>
