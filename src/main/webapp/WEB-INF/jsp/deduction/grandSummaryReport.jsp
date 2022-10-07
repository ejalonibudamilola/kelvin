<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title>Grand Summary</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>

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
					<c:out value="${roleBean.businessName}"/><br>
					Grand Summary for <c:out value="${deductionDetails.monthAndYearStr}"/></div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainBody" id="mainBody">


                <div class="reportDescText">
					This report shows the total for each deduction and earnings
					during the date range you select. </div>
				<br />
				Click any total to see the details that make up the total. <br />
				<br />
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
                              &nbsp; <span class="optional"> Year </span>
                 <form:select path="runYear">
                               <form:option value="0">Select Year</form:option>
						       <c:forEach items="${yearList}" var="yList">
						         <form:option value="${yList.id}">${yList.name}</form:option>
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
                    						<table class="report" cellspacing="0" cellpadding="0">
                    							<tr class="reportOdd header">
                    								<td class="tableCell" valign="top" width="250px">Earnings</td>
                    								<td class="tableCell" valign="top" width="100px">Total Amount</td>
                    							</tr>
                    						</table>
                    						<div style="overflow:scroll;height:120px;width:100%;overflow:auto">
                    						<table class="report" cellspacing="0" cellpadding="0">
                    							<tr class="${dedMiniBean.displayStyle}">
                    								<td class="tableCell" valign="top" width="250px">Monthly Pay</td>
                    								<td class="tableCell" align="right" valign="top" width="100px" >
                    								<a href="${appContext}/otherDeductionDetailsReport.do?did=${dedMiniBean.deductionId}&rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&pid=${deductionDetails.id}">
                    								₦<c:out value="${earningsDetails.monthlyBasicStr}"/></a></td>
                    							</tr>
                    							<tr class="${dedMiniBean.displayStyle}">
                                                   <td class="tableCell" valign="top" width="250px">Arrears</td>
                                                   <td class="tableCell" align="right" valign="top" width="100px" >
                                                   <a href="${appContext}/otherDeductionDetailsReport.do?did=${dedMiniBean.deductionId}&rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&pid=${deductionDetails.id}">
                                                   ₦<c:out value="${earningsDetails.arrearsStr}"/></a></td>
                                                </tr>
                                                <tr class="${dedMiniBean.displayStyle}">
                                                   <td class="tableCell" valign="top" width="250px">Other Arrears</td>
                                                   <td class="tableCell" align="right" valign="top" width="100px" >
                                                   <a href="${appContext}/otherDeductionDetailsReport.do?did=${dedMiniBean.deductionId}&rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&pid=${deductionDetails.id}">
                                                   ₦<c:out value="${earningsDetails.otherArrearsStr}"/></a></td>
                                                </tr>
                                                 <tr class="${dedMiniBean.displayStyle}">
                                                    <td class="tableCell" valign="top" width="250px"><b>Total Gross Pay</b></td>
                                                    <td class="tableCell" align="right" valign="top" width="100px" >
                                                    <a href="${appContext}/otherDeductionDetailsReport.do?did=${dedMiniBean.deductionId}&rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&pid=${deductionDetails.id}">
                                                    ₦<c:out value="${deductionDetails.totalGrossStr}"/></a></td>
                                                 </tr>
                    						</table>
                    						</div>
                    						</td>
                    					</tr>


					<c:if test="${deductionDetails.noOfEmployees eq 0 }">
						<font color="red"><b>No Deductions found for <c:out value="${deductionDetails.monthAndYearStr}"/></b></font>
					</c:if>
					<tr>
						<td>
						<table class="report" cellspacing="0" cellpadding="0">
							<tr class="reportOdd header">
								<td class="tableCell" valign="top" width="250px">Less Following Deductions</td>
								<td class="tableCell" valign="top" width="100px">Total Withheld</td>
							</tr>
						</table>
						<div style="overflow:scroll;height:160px;width:100%;overflow:auto">
						<table class="report" cellspacing="0" cellpadding="0">
							<c:forEach items="${deductionDetails.deductionMiniBean}" var="dedMiniBean">
							<tr class="${dedMiniBean.displayStyle}">
								<td class="tableCell" valign="top" width="250px"><c:out value="${dedMiniBean.name}"/></td>
								<td class="tableCell" align="right" valign="top" width="100px" >
								<a href="${appContext}/otherDeductionDetailsReport.do?did=${dedMiniBean.deductionId}&rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&pid=${deductionDetails.id}">
								₦<c:out value="${dedMiniBean.amountStr}"/></a></td>
							</tr>
							</c:forEach>
							<tr>
							<td class="tableCell" valign="top" width="250px"><b>Total Deduction</b></td>
                            	<td class="tableCell" align="right" valign="top" width="100px" >
                            	₦<c:out value="${deductionDetails.totalCurrentDeductionStr}"/></td>
							</tr>
						</table>
						</div>
						</td>
					</tr>
					<c:if test="${deductionDetails.noOfEmployees gt 0 }">
					<br/>
					<tr>
						<td class="reportFormControlsSpacing"><br/></td>

					</tr>
					<tr align="left">
						<td><b>No. of Employees : <c:out value="${deductionDetails.noOfEmployees}"></c:out></b></td>
					</tr>
					<tr align="right">
						<td><b>Net Pay : <font color="green"><c:out value="${deductionDetails.netPayStr}"></c:out></font></b><br>
						 ( &nbsp;<font color="green"><c:out value="${deductionDetails.amountInWords}"></c:out></font> &nbsp;)</td>
					</tr>
					<tr>
						<td class="reportFormControlsSpacing"></td>
					</tr>
					<br/>
					</c:if>
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
				<td class="activeTD"><form:radiobutton path="reportType" value="1"  onclick="if (this.checked) { document.getElementById('excelReportLink').style.display = 'none';document.getElementById('pdfReportLink').style.display = '';}"/>PDF Report Links <form:radiobutton path="reportType" value="2" onclick="if (this.checked) { document.getElementById('excelReportLink').style.display = '';document.getElementById('pdfReportLink').style.display = 'none';}"/> Excel Report Links</td>
				</tr>
				</table>
				<br>

				<div id="excelReportLink" class="reportBottomPrintLink" style='display:none'>
					<a href="${appContext}/grandSummaryReport.do?rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&rt=1">
					Grand Summary Excel&copy; </a>
					</div>


				<div id="pdfReportLink" class="reportBottomPrintLink" style='display:none'>
					<a href="${appContext}/grandSummaryReport.do?rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&rt=2">
					Grand Summary PDF&copy; </a>
				</div>
				<br>
				</td>
				<td valign="top" class="navSide"><br>
				<br>
				<!-- Here to put space beween this and any following divs -->
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
