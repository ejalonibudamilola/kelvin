<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title>Payroll Breakdown by <c:choose><c:when test="${roleBean.staffTypeName == 'Pensioner'}">TCOs</c:when> <c:otherwise> <c:out value="${roleBean.mdaTitle}" />s </c:otherwise></c:choose></title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>

</head>

<body class="main">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
<script type="text/javascript" src="scripts/helper.js"></script>
<form:form modelAttribute="miniBean">
<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
	<tr>
		<td>
		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

			<tr>
				<td colspan="2">
				<div class="title">
					Ogun State Government - <br>
					Payroll Summary by Organization</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainBody" id="mainBody">

				<span class="reportTopPrintLink">
				<a href="${appContext}/payrollSummaryByMDAPExcel.do?rm=1">
				View in Excel </a><br />
				</span>
				<div class="reportDescText">
					This report breaks down the Payroll per Organization
					<br />
				</div>
				<table class="reportMain" cellpadding="0" cellspacing="0" width="60%">

					 <tr>
                                                             <td class="reportFormControls">
                                                                         <span class="optional"> Start Date </span> <form:input path="fromDate" readonly="readonly"/>
                     													<img src="images/calendar.png" id="fromDateId" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('fromDate'),event);"><span class="optional">&nbsp;End Date </span> <form:input path="toDate" readonly="readonly"/>
                     													<img src="images/calendar.png" id="toDateId" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('toDate'),event);">&nbsp;&nbsp;
                                                             </td>
                     </tr>

					<tr>
                     <td class="buttonRow" align="right">
                       <input type="image" name="_updateReport" value="updateReport" alt="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                     </td>
                   </tr>
					<tr>

						<td class="reportFormControlsSpacing"></td>
					</tr>

					</table>
					<table>

					<tr>
						<td>
						<h3>Ogun State Executive Staff Salary Report for <c:out value="${miniBean.fromDateStr}"/> - <c:out value="${miniBean.toDateStr}"/></h3>
						<br />
						<div style="overflow-x:auto;">
						<table class="report" cellspacing="0" cellpadding="0">
						<c:forEach var="mStatus" items="${miniBean.beanList}" begin="0" end="0">
							<tr class="reportOdd header">
								<td class="tableCell" align="center" valign="top">
								S/No.</td>
								<c:forEach items="${mStatus.wageSummaryBeanList}" var="wBean">
								<td class="tableCell" align="center" valign="top">
								<c:out value="${wBean.assignedToObject}"/> Staff Strength</td>
								</c:forEach>
								<c:forEach items="${mStatus.wageSummaryBeanList}" var="wBean">
                                <td class="tableCell" align="center" valign="top">
                                <c:out value="${wBean.assignedToObject}"/></td>
                                </c:forEach>
								<c:forEach items="${mStatus.contributionList}" var="wCBean">
								<td class="tableCell" align="center" valign="top">
                                <c:out value="${wCBean.name}"/>
								</td>
								</c:forEach>
									<td class="tableCell" align="center" valign="top">
									Total Contributions
									</td>
								<c:forEach items="${mStatus.subventionList}" var="wSBean">
								<td class="tableCell" align="center" valign="top">
								<c:out value="${wSBean.name}"/>
								</td>
								</c:forEach>
								<td class="tableCell" align="center" valign="top">
									Total Subvention
								</td>
								<td class="tableCell" align="center" valign="top">
                                	Total Payments
                                </td>
                                <c:forEach items="${mStatus.deductionList}" var="dList">
                                <td class="tableCell" align="center" valign="top">
                                <c:out value="${dList.name}"/>
                                </td>
                                </c:forEach>
                                <td class="tableCell" align="center" valign="top">
                                 Total Deductions
                                </td>
                                <td class="tableCell" align="center" valign="top">
                                 Net Pay (Gross Pay - Total Deductions)
                                </td>
							</tr>
							</c:forEach>

                            <c:forEach var="mStatus" items="${miniBean.beanList}" varStatus="gridRow">
							<tr class="${wBean.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${mStatus.monthAndYearStr}"/></td>
							<c:forEach items="${mStatus.wageSummaryBeanList}" var="wBean">
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.noOfEmp}"/></td>
							</c:forEach>
							<c:forEach items="${mStatus.wageSummaryBeanList}" var="wBean">
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.currentBalanceStr}"/></td>
							</c:forEach>
							<c:forEach items="${mStatus.contributionList}" var="wCBean">
                            <td class="tableCell" align="right" valign="top">
                             <c:out value="${wCBean.currentBalanceStr}"/>
                            </td>
                            </c:forEach>
                            <td class="tableCell" align="right" valign="top">
                             <c:out value="${mStatus.totalCurrContStr}"/>
                             </td>
                             <c:forEach items="${mStatus.subventionList}" var="wSBean">
                             <td class="tableCell" align="right" valign="top">
                             <c:out value="${wSBean.currentBalanceStr}"/>
                             </td>
                             </c:forEach>
							 <td class="tableCell" align="right" valign="top">
                              <c:out value="${mStatus.totalSubBalStr}"/>
                             </td>
                             <td class="tableCell" align="right" valign="top">
                             <c:out value="${mStatus.totalCurrOutGoingStr}"/>
                             </td>
                             <c:forEach items="${mStatus.deductionList}" var="dList">
                              <td class="tableCell" align="right" valign="top">
                              <c:out value="${dList.currentBalanceStr}"/>
                              </td>
                             </c:forEach>
                             <td class="tableCell" align="right" valign="top">
                              <c:out value="${mStatus.totalDedBalStr}"/>
                              </td>
                              <td class="tableCell" align="right" valign="top">
                              <c:out value="${mStatus.grandTotalStr}"/>
                              </td>
							</tr>
							</c:forEach>

						</table>
						</div>
						<br>
						</td>

					</tr>


                  </table>

                   <table>
				<tr>
				<td class="activeTD"><form:radiobutton path="reportType" value="1"  onclick="if (this.checked) { document.getElementById('excelReportLink').style.display = 'none';document.getElementById('pdfReportLink').style.display = '';}"/>PDF Report Links <form:radiobutton path="reportType" value="2" onclick="if (this.checked) { document.getElementById('excelReportLink').style.display = '';document.getElementById('pdfReportLink').style.display = 'none';}"/> Excel Report Links</td>
				</tr>
				</table>

				<div id=excelReportLink class="reportBottomPrintLink" style='display:none'>
				<a href="${appContext}/payrollSummaryByMDAPExcel.do?rm=&ry=&rt=1">
				View in Excel </a><br />
				</div>

				<div id=pdfReportLink class="reportBottomPrintLink" style='display:none'>
				 	<a href="${appContext}/payrollSummaryByMDAPExcel.do?rm=&ry=&rt=2">
				View in Pdf </a>
				<br />
				</div>

				</td>
				<td valign="top" class="navSide"><br>
				<br>

				</td>
				</tr>
				</table>
				</td>

			</tr>
			<tr> <%@ include file="/WEB-INF/jsp/footerFile.jsp" %></tr>
			</table>
			</form:form>
		</body>

	</html>

