<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title>Cashbook Executive Summary Report</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>

</head>

<body class="main">
<script type="text/javascript" src="scripts/jacs.js"></script>
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
					Cashbook Executive Summary Report</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainBody" id="mainBody">
				
				<span class="reportTopPrintLink">
				<a href="${appContext}/cashBookExcelReport.do?${miniBean.excelUrl}">
				View in Excel </a>&nbsp;
				<a href="${appContext}/enterVariableForCashBook.do"> New Cashbook</a><br />
				</span>
				<div class="reportDescText">
					This report gives a Summary of a Cashbook for a Payroll Run
					<br />
				</div>
				<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
					<tr>
						<td>
				 		&nbsp;</td>
                 		
					</tr>
					
					<tr>
					
						<td class="reportFormControlsSpacing"></td>
					</tr>
					<tr>
						<td>
						<h3>Ogun State Cashbook Summary Report for <c:out value="${miniBean.monthAndYearStr}"/> </h3>
						<br />
						<table class="report" cellspacing="0" cellpadding="0">
							<tr class="reportOdd header">
								<td class="tableCell" align="center" valign="top">
								S/No.</td>
								<td class="tableCell" align="center" valign="top">
								MDA &amp; P's</td>
								<td class="tableCell" align="center" valign="top">
								 Basic</td>		
								<td class="tableCell" align="center" valign="top">
								Rent</td>						
								<td class="tableCell" align="center" valign="top">
								Transport</td>
								<td class="tableCell" align="center" valign="top">
								Meal</td>
								<td class="tableCell" align="center" valign="top">
								Utility</td>
								<td class="tableCell" align="center" valign="top">
								Inducement</td>
								<td class="tableCell" align="center" valign="top">
								Domestic Serv.</td>
								<td class="tableCell" align="center" valign="top">
								Ent. Allow.</td>
								<td class="tableCell" align="center" valign="top">
								Gross Salary.</td>
								<td class="tableCell" align="center" valign="top">
								Union Dues</td>
								<td class="tableCell" align="center" valign="top">
								NHF</td>	
								<td class="tableCell" align="center" valign="top">
								PAYE</td>	
								<td class="tableCell" align="center" valign="top">
								Tot. Deductions</td>	
								<td class="tableCell" align="center" valign="top">
								Net Pay</td>									
							</tr>
							<c:forEach items="${miniBean.wageSummaryBeanList}" var="wBean" varStatus="gridRow">
							<tr class="${wBean.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${wBean.assignedToObject}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.basicSalaryAsStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.rentStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.transportStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.mealStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.utilityStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.inducementStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.domesticServantStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.entertainmentAllowanceStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.grossAmountAsStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.unionDuesStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.nhfStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.payeAsStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.totalDeductionsAsStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.netPayAsStr}"/></td>
								
							</tr>
							</c:forEach>							
							<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">Totals</td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalBasicSalaryAsStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalRentStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalTransportStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalMealStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalUtilityStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalInducementStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalDomServStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalEntAllowStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalGrossSalaryStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalUnionDuesStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalNhfStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalPAYEStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalDeductionsAsStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalNetPayAsStr}"/></td>
								
							</tr>
							</table>
						</td>
					</tr>
					
                  </table>
				<div class="reportBottomPrintLink">
				<a href="${appContext}/cashBookExcelReport.do?${miniBean.excelUrl}">
				
				View in Excel </a>&nbsp;
				<a href="${appContext}/enterVariableForCashBook.do"> New Cashbook</a>
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
	
