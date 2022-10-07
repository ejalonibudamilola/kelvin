<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Other Deductions </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon"
      type="image/png"
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css" />" type="text/css"/>

</head>

<body class="main">
<form:form modelAttribute="deductionDetails">
<script type="text/javascript" src="scripts/jacs.js"></script>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

	<tr>
		<td colspan="2">
		<div class="title">
		<c:out value="${deductionDetails.currentDeduction}"/> deduction<br>
			For <c:out value="${deductionDetails.monthAndYearStr}"/></div>
		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">


		<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
			<tr>
				<td class="reportFormControls">
				<span class="optional"> Month </span>
				 <form:select path="runMonth">
                               <form:option value="-1">Select Month</form:option>
						       <c:forEach items="${monthList}" var="mList">
						         <form:option value="${mList.id}">${mList.name}</form:option>
						      </c:forEach>
						      </form:select>
                              &nbsp;
                 <form:select path="runYear"> <span class="optional"> Year </span>
                               <form:option value="0">Select Year</form:option>
						       <c:forEach items="${yearList}" var="yList">
						         <form:option value="${yList.id}">${yList.name}</form:option>
						      </c:forEach>
						      </form:select>
                              &nbsp;
                              Deduction: <form:select path="deductionId">
                               <form:option value="0">All Deductions</form:option>
						       <c:forEach items="${deductionList}" var="deduction">
						      <form:option value="${deduction.id}" title="${deduction.name}">${deduction.description}</form:option>
						      </c:forEach>
						      </form:select>
                              &nbsp;
                 </td>
			</tr>
			<tr>
                         <td class="buttonRow" align="right">
                             <input type="image" name="_updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                            <br/>
                         </td>
                     </tr>
					<tr>
						<td class="reportFormControlsSpacing"><br/></td>
					</tr>
					<tr>
						<td class="reportFormControlsSpacing"><br/></td>
					</tr>
			<tr>
				<td>

				<br />
				<table class="report" cellspacing="0" cellpadding="0">
						<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="97px"><c:out value="${roleBean.staffTitle}"/></td>
						<td class="tableCell" valign="top" width="166px"><c:out value="${roleBean.staffTypeName}"/> Name</td>
						<c:forEach items="${deductionDetails.deductionMiniBean}" var="dedMiniBean" begin="0" end="0">
                        <td class="tableCell" valign="top" width="97px"><c:out value="${dedMiniBean.firstAllotment}" />(<c:out value="${dedMiniBean.firstAllotPercent}" />%)</td>
                        <td class="tableCell" valign="top" width="97px"><c:out value="${dedMiniBean.secondAllotment}" />(<c:out value="${dedMiniBean.secondAllotPercent}" />%)</td>
                        <td class="tableCell" valign="top" width="97px"><c:out value="${dedMiniBean.thirdAllotment}" />(<c:out value="${dedMiniBean.thirdAllotPercent}" />%)</td>
						<td class="tableCell" width="150px" valign="top" align="right">Amount Deducted</td>
                        </c:forEach>
					</tr>
				</table>
				<div style="overflow:scroll;height:400px;width:100%;overflow:auto">
				<table class="report" cellspacing="0" cellpadding="0">
					<c:forEach items="${deductionDetails.deductionMiniBean}" var="dedMiniBean">
					<tr class="${dedMiniBean.displayStyle}">
						<td class="tableCell" valign="top" width="97px"><a href='${appContext}/viewDeductionHistory.do?eid=${dedMiniBean.id}&dedId=${dedMiniBean.parentInstId}' target="_blank" onclick="popupWindow(this.href,'${dedMiniBean.name}'); return false;" target="_blank"><c:out value="${dedMiniBean.employeeId}"/></a></td>
						<td class="tableCell" valign="top" width="166px"><c:out value="${dedMiniBean.name}"/></td>
						<td class="tableCell" valign="top" width="97px"><c:out value="${dedMiniBean.firstAllotAmt}"/></td>
						<td class="tableCell" valign="top" width="97px"><c:out value="${dedMiniBean.secondAllotAmt}"/></td>
						<td class="tableCell" valign="top" width="97px"><c:out value="${dedMiniBean.thirdAllotmentAmt}"/></td>
						<td class="tableCell" width="150px" align="right" valign="top"><c:out value="${dedMiniBean.amountStr}"/></td>

					</tr>
					</c:forEach>

				</table>
				</div>
				<table class="report" cellspacing="0" cellpadding="0">
						<tr class="reportEven footer">
						<td class="tableCell" valign="top" width="97px">Totals</td>
						<td class="tableCell" valign="top" width="166px"><c:out value="${deductionDetails.noOfEmployees}"/></td>
						<td class="tableCell" valign="top" width="97px"><c:out value="${deductionDetails.firstAllotAmtTotal}" /></td>
						<td class="tableCell" valign="top" width="97px"><c:out value="${deductionDetails.secondAllotAmtTotal}" /></td>
						<td class="tableCell" valign="top" width="97px"><c:out value="${deductionDetails.thirdAllotAmtTotal}" /></td>
						<td class="tableCell" valign="top" align="right" width="150px">₦<c:out value="${deductionDetails.totalStr}"/></td>
					</tr>
				</table>

				</td>
			</tr>
 			<tr>
                 <td class="buttonRow" align="right">
                  <input type="image" name="_cancel" value="cancel" title="Cancel" src="images/close.png">

                 </td>
            </tr>
		</table>
		<br>
				<br>
				<table>
				<tr>
				<td class="activeTD">
				<form:radiobutton path="reportType" value="1"  onclick="if (this.checked) { document.getElementById('excelReportLink').style.display = 'none';document.getElementById('pdfReportLink').style.display = '';}"/>PDF Report Links
				<form:radiobutton path="reportType" value="2" onclick="if (this.checked) { document.getElementById('excelReportLink').style.display = '';document.getElementById('pdfReportLink').style.display = 'none';}"/> Excel Report Links
				</td>
				</tr>
				</table>
				<br>
		<div id="excelReportLink" class="reportBottomPrintLink" style='display:none'>
			<a href="${appContext}/apportionedDeductionReport.do?did=${deductionDetails.deductionId}&rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&pid=${deductionDetails.id}&rt=1">
			View in Excel </a>

		</div>
		<div id="pdfReportLink" class="reportBottomPrintLink" style='display:none'>
					<a href="${appContext}/apportionedDeductionReport.do?did=${deductionDetails.deductionId}&rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&pid=${deductionDetails.id}&rt=2">
					View this Deduction in PDF&copy; </a>
				</div>

		<br>

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
