<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title>Simulated Payroll Result Form</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="styles/epayroll.css" type="text/css"/>

</head>

<body class="main">

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
					<c:out value="${miniBean.name}"/>&nbsp; - Simulated for <c:out value="${miniBean.monthAndYearStr}"/></div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainBody" id="mainBody">
				
				<span class="reportTopPrintLink">
				<a href="${appContext}/simulatedLTGPayrollExcel.do?lid=${miniBean.id}">
				View in Excel </a><br />
				</span>
				<div class="reportDescText">
					This report shows a payroll simulation.
					<br />
				</div>
				<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
					
					
					<tr>
					
						<td class="reportFormControlsSpacing"></td>
					</tr>
					<tr>
						<td>
						<h3>Simulated LTG Effect for <c:out value="${miniBean.monthAndYearStr}"/> </h3>
						<br />
						<table class="report" cellspacing="0" cellpadding="0">
							<tr class="reportOdd header">
								<td class="tableCell" align="center" valign="top">
								S/No.</td>
								<td class="tableCell" align="center" valign="top">
								Organisation</td>
								<td class="tableCell" align="center" valign="top">
								 &nbsp;Staff Strength
								</td>				
								<td class="tableCell" align="center" valign="top">
								 Simulated Results (LTG Inclusive)</td>
								<td class="tableCell" align="center" valign="top">
								 Actual Results (LTG Not Inclusive)</td>								
							</tr>
							<c:forEach items="${miniBean.wageSummaryBeanList}" var="wBean" varStatus="gridRow">
							<tr class="${wBean.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${wBean.assignedToObject}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.noOfEmp}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.currentBalanceStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.previousBalanceStr}"/></td>
								<c:if test="${wBean.materialityThreshold}">
								    <td>&nbsp;&nbsp;
									<img src="images/tick.png" border="0" title="LTG Applied to ${wBean.assignedToObject}" onmouseover="style.cursor='pointer'"></td>
								</c:if>
							</tr>
							</c:forEach>							
							<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">Sub-Totals</td>
								<td class="tableCell" align="right"><c:out value="${miniBean.totalNoOfEmp}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalCurrBalStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalPrevBalStr}"/></td>
								
							</tr>
							<tr>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							</tr>
														
							<!--<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">Sub-Totals</td>
								<td class="tableCell" align="right">&nbsp;</td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalSubBalStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalPrevSubBalStr}"/></td>
							</tr>
							<tr>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							</tr>
							--><tr class="reportEven footer">
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
							</tr>
							<c:forEach items="${miniBean.deductionList}" var="dList">
							<tr class="${dList.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${dList.serialNum}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${dList.name}"/></td>
								<td class="tableCell" align="right" valign="top">&nbsp;</td>
								<td class="tableCell" align="right" valign="top"><c:out value="${dList.currentBalanceStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${dList.previousBalanceStr}"/></td>
							</tr>
							</c:forEach>							
							<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">Sub-Totals</td>
								<td class="tableCell" align="right">&nbsp;</td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalDedBalStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalPrevDedBalStr}"/></td>
							</tr>
							<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">Grand Totals</td>
								<td class="tableCell" align="right"><c:out value="${miniBean.totalNoOfEmp}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.grandTotalStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.grandPrevTotalStr}"/></td>
							</tr>
						</table>
						</td>
					</tr>
					
					<tr>
                     <td class="buttonRow" align="right">
                       <input type="image" name="_close" value="close" title="Close" class="updateReportSubmit" src="images/close.png">
                     </td>
                   </tr>
                  </table>
				<div class="reportBottomPrintLink">
				<a href="${appContext}/simulatedLTGPayrollExcel.do?lid=${miniBean.id}">
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
	
