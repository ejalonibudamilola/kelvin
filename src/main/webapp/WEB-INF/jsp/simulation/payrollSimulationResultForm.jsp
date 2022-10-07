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
				<div class="reportTopPrintLink">
				<a href="${appContext}/payrollSimulationExcel.do?pid=${miniBean.id}">
				View in Excel </a> &nbsp;&nbsp;<a href="${appContext}/viewPayrollDetails.do?pid=${miniBean.id}&pop=y" target="_blank" onclick="popup(this.href, '${miniBean.name}');return false;">View Details</a>
				<br />
				</div>
		
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
						<h3>Simulated Payroll Result.</h3>
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
								<td class="tableCell" align="left" valign="top"><c:out value="${wBean.assignedToObject}"/></td>
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
							<!--<tr>
							<td class="tableCell" align="right" valign="top">&nbsp;</td>
							<c:forEach items="${miniBean.mdapFooterList}" var="footerList2" varStatus="gridRow">
									<td class="tableCell" align="right">&nbsp;</td>
								</c:forEach>
							</tr>
														
							--><tr class="reportEven footer">
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
				<a href="${appContext}/payrollSimulationExcel.do?pid=${miniBean.id}">
				View in Excel </a> &nbsp;&nbsp;<a href="${appContext}/viewPayrollDetails.do?pid=${miniBean.id}&pop=y" target="_blank" onclick="popup(this.href, '${miniBean.name}');return false;">View Details</a>
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
			<tr>
			 <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
			 </tr>
			 
			</table>
			</form:form>
		</body>

	</html>
	
