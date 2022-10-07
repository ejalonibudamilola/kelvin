<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title>Payroll Simulation Result Form</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="styles/epayroll.css" type="text/css"/>
<script type="text/javascript">
<!--
function popup(url,windowname)
{
 var width  = 1000;
 var height = 800;
 var left   = (screen.width  - width)/2;
 var top    = (screen.height - height)/2;
 var params = 'width='+width+', height='+height;
 params += ', top='+top+', left='+left;
 params += ', directories=no';
 params += ', location=no';
 params += ', menubar=no';
 params += ', resizable=yes';
 params += ', scrollbars=yes';
 params += ', status=no';
 params += ', toolbar=no';
 newwin=window.open(url,windowname, params);
 if (window.focus) {newwin.focus()}
 return false;
}
// -->
</script>
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
					Payroll Simulation
					</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainBody" id="mainBody">

				<div class="reportDescText">
					This report shows payroll summary for each organization.
					<br />
				</div>
				<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">

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
					<tr>
						<td>
						<br />
						<table class="report" cellspacing="0" cellpadding="0">
							<tr class="reportOdd header">
								<td class="tableCell" align="center" valign="top">
								S/No.</td>
								<td class="tableCell" align="center" valign="top">
								Organisation</td>
								<c:forEach items="${miniBean.headerList}" var="hBean" varStatus="gridRow">
								<td class="tableCell" align="center" valign="top">
								 <c:out value="${hBean.name}"/>
								</td>
								</c:forEach>
							</tr>
							<c:forEach items="${miniBean.summaryBean}" var="wBean" varStatus="gridRow">
							<tr class="${wBean.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top"><a href="${appContext}/singleBusinessExecSummary.do?bid=${wBean.id}&fd=${miniBean.fromDateStr}&td=${miniBean.toDateStr}"><c:out value="${wBean.assignedToObject}"/></a></td>
								<c:forEach items="${wBean.miniBeanList}" var="mBean" varStatus="gridRow" >
									<td class="tableCell" align="right" valign="top"><c:out value="${mBean.currentValueStr}"/></td>
								</c:forEach>
							</tr>
							</c:forEach>
							<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">Sub-Totals</td>
								<c:forEach items="${miniBean.mdapFooterList}" var="footerList" varStatus="gridRow">
									<td class="tableCell" align="right"><c:out value="${footerList.currentValueStr}"/></td>
								</c:forEach>
							</tr>

							<tr class="reportEven footer">
                            	<td class="tableCell" align="center" valign="top">
                            	&nbsp;</td>
                            	<td class="tableCell" align="center" valign="top">
                            	Contributions</td>
                            	<c:forEach items="${miniBean.mdapFooterList}" var="footerList3" varStatus="gridRow">
                            	<td class="tableCell" align="right">&nbsp;</td>
                            	</c:forEach>
                            </tr>

							<c:forEach items="${miniBean.contributionsList}" var="cList"  varStatus="gridRow">
                            	<tr class="${cList.displayStyle}">
                            	<td class="tableCell" valign="top"><c:out value="${cList.serialNum}"/></td>
                            	<td class="tableCell" align="left" valign="top"><c:out value="${cList.name}"/></td>
                            	<c:forEach items="${cList.miniBeanList}" var="cMiniList">
                            	<td class="tableCell" align="right"><c:out value="${cMiniList.currentValueStr}"/></td>
                            	</c:forEach>
                            	</tr>
                            	</c:forEach>
                            	<tr class="reportEven footer">
                                	<td class="tableCell" valign="top">&nbsp;</td>
                                	<td class="tableCell" align="center" valign="top">Sub-Totals</td>
                                	<c:forEach items="${miniBean.contributionsTotals}" var="cTotals" varStatus="gridRow">
                                	<td class="tableCell" align="right"><c:out value="${cTotals.currentValueStr}"/></td>
                                	</c:forEach>
                                </tr>


							<tr class="reportEven footer">
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								<td class="tableCell" align="center" valign="top">
								Deductions</td>
								<c:forEach items="${miniBean.mdapFooterList}" var="footerList3" varStatus="gridRow">
									<td class="tableCell" align="right">&nbsp;</td>
								</c:forEach>
							</tr>
							<c:forEach items="${miniBean.deductionsList}" var="dList"  varStatus="gridRow">
							<tr class="${dList.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${dList.serialNum}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${dList.name}"/></td>
								<c:forEach items="${dList.miniBeanList}" var="dMiniList">
									<td class="tableCell" align="right"><c:out value="${dMiniList.currentValueStr}"/></td>
								</c:forEach>
							</tr>
							</c:forEach>
							<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">Sub-Totals</td>
								<c:forEach items="${miniBean.deductionsTotals}" var="sTotals" varStatus="gridRow">
									<td class="tableCell" align="right"><c:out value="${sTotals.currentValueStr}"/></td>
								</c:forEach>
							</tr>
							<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">Grand Totals</td>
								<c:forEach items="${miniBean.footerList}" var="gTotals" varStatus="gridRow">
									<td class="tableCell" align="right"><c:out value="${gTotals.currentValueStr}"/></td>
								</c:forEach>
							</tr>
						</table>
						</td>
					</tr>

					<tr>
                     <td class="buttonRow" align="right">
                      &nbsp;
                     </td>
                   </tr>
                  </table>
				<div class="reportBottomPrintLink">
				<a href="${appContext}/allBusinessExecSummaryReport.do?fd=${miniBean.fromDateStr}&td=${miniBean.toDateStr}&rt=1">
				View in Excel </a> &nbsp; &nbsp; | &nbsp; &nbsp; <a href="${appContext}/allBusinessExecSummaryReport.do?fd=${miniBean.fromDateStr}&td=${miniBean.toDateStr}&rt=2">
                                                   				View in Pdf </a><br />
				</div>

				</td>
				<td valign="top" class="navSide"><br>
				<br>

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
	
