<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title>Payroll Breakdown by <c:choose><c:when test="${roleBean.pensioner}">TCOs</c:when> <c:otherwise> <c:out value="${roleBean.mdaTitle}" />s </c:otherwise></c:choose></title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>

</head>

<body class="main">
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
					<c:out value="${roleBean.businessName}"/> - <br>
					Payroll Summary by <c:out value="${roleBean.mdaTitle}" />&lsquo;s</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainBody" id="mainBody">

				<span class="reportTopPrintLink">
				<a href="${appContext}/payrollSummaryByMDAPExcel.do?rm=${miniBean.runMonth}&ry=${miniBean.runYear}&rt=1">
				View in Excel </a><br />
				</span>
				<div class="reportDescText">
					This report breaks down the Payroll per <c:out value="${roleBean.mdaTitle}" />&lsquo;s
					<br />
				</div>
				<table class="reportMain" cellpadding="0" cellspacing="0" width="60%">
					<tr>
						<td class="reportFormControls">
				 		<span class="optional"> Payroll Month </span> 
						 &nbsp;<form:select path="runMonth">
												<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<c:forEach items="${monthList}" var="mList">
												<form:option value="${mList.id}">${mList.name}</form:option>
												</c:forEach>
												</form:select>&nbsp;&nbsp;<span class="optional"> Payroll Year </span>
												&nbsp;<form:select path="runYear">
												<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<c:forEach items="${yearList}" var="yList">
												<form:option value="${yList.id}">${yList.id}</form:option>
												</c:forEach>
												</form:select>
			   		</td>
                 		
					</tr>
					 
					<tr>
					     <td>
						 <form:radiobutton path="showDiffInd" value="1" onclick="go(this,'${appContext}/viewWageSummary.do?rm=${miniBean.runMonth}&ry=${miniBean.runYear}&sl=1')"/>Show Differentials<br>
					     <form:radiobutton path="showDiffInd" value="0" onclick="go(this,'${appContext}/viewWageSummary.do?rm=${miniBean.runMonth}&ry=${miniBean.runYear}&sl=0')"/>Hide Differentials<br>
					     </td>		  
					</tr>
					<tr>
                     <td class="buttonRow" align="right">
                       <input type="image" name="_updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                     </td>
                   </tr>
					<tr>
					
						<td class="reportFormControlsSpacing"></td>
					</tr>
				 
					</table>
					<table>
					
					<tr>
						<td>
						<h3><c:out value="${roleBean.businessName}"/> Executive Staff Salary Report for <c:out value="${miniBean.monthAndYearStr}"/> </h3>
						<br />
						<table class="report" cellspacing="0" cellpadding="0">
							<tr class="reportOdd header">
								<td class="tableCell" align="center" valign="top">
								S/No.</td>
								<td class="tableCell" align="center" valign="top">
								Organisation</td>
								<td class="tableCell" align="center" valign="top">
								<c:out value="${miniBean.monthAndYearStr}"/>&nbsp;Staff Strength</td>		
								<td class="tableCell" align="center" valign="top">
								<c:out value="${miniBean.prevMonthAndYearStr}"/>&nbsp;Staff Strength</td>	
								<c:if test="${miniBean.showDiff}">
									<td class="tableCell" align="center" valign="top">
										<c:out value="Staff Diff."/>
									</td>	
								</c:if>				
								<td class="tableCell" align="center" valign="top">
								<c:out value="${miniBean.monthAndYearStr}"/></td>
								<td class="tableCell" align="center" valign="top">
								<c:out value="${miniBean.prevMonthAndYearStr}"/></td>	
								<c:if test="${miniBean.showDiff}">
									<td class="tableCell" align="center" valign="top">
										<c:out value="Pay Diff."/>
									</td>	
								</c:if>
							</tr>
							<c:forEach items="${miniBean.wageSummaryBeanList}" var="wBean" varStatus="gridRow">
							<tr class="${wBean.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${wBean.assignedToObject}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.noOfEmp}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.previousNoOfEmp}"/></td>
								<c:if test="${miniBean.showDiff}">
									<td class="tableCell" align="right" valign="top"><c:out value="${wBean.empDiff}"/></td>
								</c:if>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.currentBalanceStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.previousBalanceStr}"/></td>
									<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.netDifferenceStr}"/></td>
								</c:if>
								<c:if test="${wBean.materialityThreshold}">
								    &nbsp;&nbsp;<td><a href="${appContext}/explainMateriality.do?rm=${miniBean.runMonth}&ry=${miniBean.runYear}&mid=${wBean.mdaDeptMapId}&pd=${wBean.netDifference}" onclick="popupWindow('${appContext}/explainMateriality.do?rm=${miniBean.runMonth}&ry=${miniBean.runYear}&mid=${wBean.mdaDeptMapId}&pd=${wBean.netDifference}','Materiality');return false" target="_blank">
									<img src="images/questionmark.png" border="0" title="View probable cause of differential" onmouseover="style.cursor='pointer'"></a> </td>
								</c:if>
								
							</tr>
							   
							</c:forEach>							
							<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">Gross Pay</td>
								<td class="tableCell" align="right"><c:out value="${miniBean.totalNoOfEmp}"/></td>
								<td class="tableCell" align="right"><c:out value="${miniBean.totalNoOfPrevMonthEmp}"/></td>
								<c:if test="${miniBean.showDiff}">
									<td class="tableCell" align="right"><c:out value="${miniBean.totalEmpDiff}"/></td>
								</c:if>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalCurrBalStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalPrevBalStr}"/></td>
								<c:if test="${miniBean.showDiff}">
									<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalCurrBalDiffStr}"/></td>
								</c:if>
								
							</tr>
							<tr>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="right" valign="top">&nbsp;</td>
							</c:if>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="right" valign="top">&nbsp;</td>
							</c:if>
							</tr>

                        <c:if test ="${roleBean.staffTypeName != 'Pensioner'}">
							<tr class="reportEven footer">
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								<td class="tableCell" align="center" valign="top">
								Contributions</td>								
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								</c:if>
								<td class="tableCell" align="center" valign="top">
								<td class="tableCell" align="center" valign="top">
								<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								</c:if>							
							</tr>
							<c:forEach items="${miniBean.contributionList}" var="wCBean">
							<tr class="${wCBean.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${wCBean.serialNum}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${wCBean.name}"/></td>
								<td class="tableCell" align="center" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">&nbsp;</td>
								<c:if test="${miniBean.showDiff}">
								  <td class="tableCell" align="center" valign="top">&nbsp;</td>
								</c:if>
								<td class="tableCell" align="right" valign="top"><c:out value="${wCBean.currentBalanceStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wCBean.previousBalanceStr}"/></td>
								<c:if test="${miniBean.showDiff}">
								    <td class="tableCell" align="right" valign="top"><c:out value="${wCBean.netDifferenceStr}"/></td>
								</c:if>
							</tr>
							</c:forEach>							
							<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">Total Contributions</td>
								<td class="tableCell" align="right">&nbsp;</td>
								<td class="tableCell" align="right">&nbsp;</td>
								<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="center" valign="top">&nbsp;</td>
								</c:if>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalCurrContStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalPrevContStr}"/></td>
								<c:if test="${miniBean.showDiff}">
								   <td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalCurrDiffStr}"/></td>
								</c:if>
							</tr>
							<tr class="reportEven footer">
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								<td class="tableCell" align="center" valign="top">
								Recurrent Subventions</td>								
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								</c:if>
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>	
								<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								</c:if>							
							</tr>
							<c:forEach items="${miniBean.subventionList}" var="wSBean">
							<tr class="${wSBean.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${wSBean.serialNum}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${wSBean.name}"/></td>
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								</c:if>
								<td class="tableCell" align="right" valign="top"><c:out value="${wSBean.currentBalanceStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wSBean.previousBalanceStr}"/></td>
								<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="right" valign="top"><c:out value="${wSBean.netDifferenceStr}"/></td>
								</c:if>
							</tr>
							</c:forEach>							
							<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">Total Subvention</td>
								<td class="tableCell" align="right">&nbsp;</td>
								<td class="tableCell" align="right">&nbsp;</td>
								<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								</c:if>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalSubBalStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalPrevSubBalStr}"/></td>
								<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalSubBalDiffStr}"/></td>
								</c:if>
							</tr>

							</c:if>

							<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">Total Payments</td>
								<td class="tableCell" align="right">&nbsp;</td>
								<td class="tableCell" align="right">&nbsp;</td>
								<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="right" valign="top">&nbsp;</td>
								</c:if>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalCurrOutGoingStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalPrevOutGoingStr}"/></td>
								<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalCurrDiffStr}"/></td>
								</c:if>
							</tr>

							<tr>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="right" valign="top">&nbsp;</td>
							</c:if>							
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<c:if test="${miniBean.showDiff}">
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							</c:if>
							</tr>
							<tr class="reportEven footer">
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								<td class="tableCell" align="center" valign="top">
								Deductions</td>								
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="right" valign="top">&nbsp;</td>
								</c:if>
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>	
								<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="right" valign="top">&nbsp;</td>
								</c:if>							
							</tr>
							<c:forEach items="${miniBean.deductionList}" var="dList">
							<tr class="${dList.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${dList.serialNum}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${dList.name}"/></td>
								<td class="tableCell" align="right" valign="top">&nbsp;</td>
								<td class="tableCell" align="right" valign="top">&nbsp;</td>
								<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="right" valign="top">&nbsp;</td>
								</c:if>
								<td class="tableCell" align="right" valign="top"><c:out value="${dList.currentBalanceStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${dList.previousBalanceStr}"/></td>
								<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="right" valign="top"><c:out value="${dList.netDifferenceStr}"/></td>
								</c:if>
							</tr>
							</c:forEach>							
							<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">Total Deductions</td>
								<td class="tableCell" align="right">&nbsp;</td>
								<td class="tableCell" align="right">&nbsp;</td>
								<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="right" valign="top">&nbsp;</td>
								</c:if>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalDedBalStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalPrevDedBalStr}"/></td>
								<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalDedDiffStr}"/></td>
								</c:if>
							</tr>
							<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">Net Pay (Gross Pay - Total Deductions)</td>
								<td class="tableCell" align="right"><c:out value="${miniBean.totalNoOfEmp}"/></td>
								<td class="tableCell" align="right"><c:out value="${miniBean.totalNoOfPrevMonthEmp}"/></td>
								<c:if test="${miniBean.showDiff}">
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalEmpDiff}"/></td>
								</c:if>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.grandTotalStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.grandPrevTotalStr}"/></td>
								<c:if test="${miniBean.showDiff}">
								     <td class="tableCell" align="right" valign="top"><c:out value="${miniBean.grandTotalDiffStr}"/></td>
								</c:if>
							</tr>
						</table>
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
				<a href="${appContext}/payrollSummaryByMDAPExcel.do?rm=${miniBean.runMonth}&ry=${miniBean.runYear}&rt=1">
				View in Excel </a><br />
				</div>
				
				<div id=pdfReportLink class="reportBottomPrintLink" style='display:none'>
				 	<a href="${appContext}/payrollSummaryByMDAPExcel.do?rm=${miniBean.runMonth}&ry=${miniBean.runYear}&rt=2">
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
			<tr> <%@ include file="/WEB-INF/jsp/footerFile.jsp" %></tr>
			</table>
			</form:form>
		</body>

	</html>
	
