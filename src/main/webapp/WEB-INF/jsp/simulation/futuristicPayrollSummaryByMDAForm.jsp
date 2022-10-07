<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title>Futuristic Payroll Breakdown by <c:out value="${roleBean.mdaTitle}"/></title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>

</head>

<body class="main">
<script type="text/javascript" src="scripts/jacs.js"></script>
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
					Futuristic Payroll Summary by <c:out value="${roleBean.mdaTitle}"/></div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainBody" id="mainBody">
				
				<span class="reportTopPrintLink">
				<a href="${appContext}/futurePayrollSummaryByMDAPExcel.do?pid=${miniBean.id}">
				View in Excel </a><br />
				</span>
				<div class="reportDescText">
					This report breaks down the Futuristic Payroll Simulation per <c:out value="${roleBean.mdaTitle}"/>
					<br />Comparing the result to the most recent Approved Payroll results.
				</div>
				<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
					<tr>
						<td>
				 			&nbsp;
				 			
				 		</td>
                 		
					</tr>
					
					<tr>
					
						<td class="reportFormControlsSpacing"></td>
					</tr>
					<tr>
						<td>
						<h3><c:out value="${roleBean.businessName}"/> Futuristic Payroll Simulation Report for <c:out value="${miniBean.monthAndYearStr}"/> </h3>
						<br />
						<table class="report" cellspacing="0" cellpadding="0">
							<tr class="reportOdd header">
								<td class="tableCell" align="center" valign="top">
								S/No.</td>
								<td class="tableCell" align="center" valign="top">
								<c:out value="${roleBean.mdaTitle}"/></td>
								<td class="tableCell" align="center" valign="top">
								Futuristic Staff Strength</td>
								<td class="tableCell" align="center" valign="top">
								Current Staff Strength</td>	
								<td class="tableCell" align="center" valign="top">
								No. Of Retirees</td>										
								<td class="tableCell" align="center" valign="top">
								 <c:out value="${miniBean.prevMonthAndYearStr}"/></td>
								<td class="tableCell" align="center" valign="top">
								<c:out value="${miniBean.monthAndYearStr}"/></td>
							</tr>
							<c:forEach items="${miniBean.wageSummaryBeanList}" var="wBean" varStatus="gridRow">
							<tr class="${wBean.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${wBean.assignedToObject}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.previousNoOfEmp}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.noOfEmp}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.empDiff}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.previousBalanceStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.currentBalanceStr}"/></td>
							</tr>
							</c:forEach>							
							<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">Sub-Total</td>
								<td class="tableCell" align="right"><c:out value="${miniBean.totalNoOfPrevMonthEmp}"/></td>
								<td class="tableCell" align="right"><c:out value="${miniBean.totalNoOfEmp}"/></td>
								<td class="tableCell" align="right"><c:out value="${miniBean.totalEmpDiff}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalPrevBalStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalCurrBalStr}"/></td>	
							</tr>
							<tr>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
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
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>								
							</tr>
							<c:forEach items="${miniBean.deductionList}" var="dList">
							<tr class="${dList.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${dList.serialNum}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${dList.name}"/></td>
								<td class="tableCell" align="right" valign="top">&nbsp;</td>
								<td class="tableCell" align="right" valign="top">&nbsp;</td>
								<td class="tableCell" align="right" valign="top">&nbsp;</td>
								<td class="tableCell" align="right" valign="top"><c:out value="${dList.previousBalanceStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${dList.currentBalanceStr}"/></td>
								
							</tr>
							</c:forEach>							
							<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">Sub-Totals</td>
								<td class="tableCell" align="right">&nbsp;</td>
								<td class="tableCell" align="right">&nbsp;</td>
								<td class="tableCell" align="right">&nbsp;</td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalPrevDedBalStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalDedBalStr}"/></td>
								
							</tr>
							<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">Grand Totals</td>
								<td class="tableCell" align="right"><c:out value="${miniBean.totalNoOfPrevMonthEmp}"/></td>
								<td class="tableCell" align="right"><c:out value="${miniBean.totalNoOfEmp}"/></td>
								<td class="tableCell" align="right"><c:out value="${miniBean.totalEmpDiff}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.grandPrevTotalStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.grandTotalStr}"/></td>	
							</tr>
						</table>
						</td>
					</tr>
					
					<tr>
                     <td class="buttonRow" align="right">
                       <input type="image" name="_cancel" value="cancel" title="Close Report View" class="" src="images/close.png">
                     </td>
                   </tr>
                  </table>
				<div class="reportBottomPrintLink">
				<a href="${appContext}/futurePayrollSummaryByMDAPExcel.do?pid=${miniBean.id}">
				View in Excel </a><br />
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
	
