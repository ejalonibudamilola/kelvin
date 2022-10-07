<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title>Other Deductions</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">


</head>

<body class="main">
<form:form modelAttribute="deductionDetails">
<script type="text/javascript" src="scripts/jacs.js"></script>
<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
    <%@ include file="/WEB-INF/jsp/modal.jsp" %>

	<tr>
		<td>
		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			
			<tr>
				<td colspan="2">
				<div class="title">
					<c:out value="${roleBean.businessName}"/>
					Deductions for <c:out value="${deductionDetails.monthAndYearStr}"/></div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainBody" id="mainBody">
				
				<table>
				<tr>
					<td>
						<form:select path="subLinkSelect">
						<form:option value="paystubsReportForm">Payroll Summary</form:option>
						<form:option value="viewWageSummary">Payroll Executive Summary</form:option>
						<form:option value="allOtherGarnishmentReport">Loans</form:option>
						
						</form:select> 
					</td>
					<td>&nbsp;</td>
					<td>
					<input type="image" name="_go" value="go" title="Go" class="" src="images/go_h.png" >
					</tr>
				</table>
				
				
				<!--<span class="reportTopPrintLink">
					<a href="${appContext}/allOtherDeductionDetailsExcel.do?sDate=${deductionDetails.fromDateStr}&eDate=${deductionDetails.toDateStr}&pid=${deductionDetails.id}">
					View in Excel </a>&nbsp;&nbsp;<a href="${appContext}/deductionWithDetailsExcel.do?sDate=${deductionDetails.fromDateStr}&eDate=${deductionDetails.toDateStr}&pid=${deductionDetails.id}&exp=all">
					View With Details in Excel</a></span>
				-->
				<div class="reportDescText">
					<p>
					    This report shows the total for each payroll deduction
					    during the date range you select.
					</p>
					<p>
					    Click any total to see the details that make up the total.
					</p>
				</div>
				<br/>
				<table class="reportMain" cellpadding="0" cellspacing="0" width="70%">
					<tr>
						<td class="reportFormControls">
				            <span class="optional"> Month </span>
				            <form:select path="runMonth">
                               <form:option value="-1">Select Month</form:option>
						       <c:forEach items="${monthList}" var="mList">
						         <form:option value="${mList.id}">${mList.name}</form:option>
						      </c:forEach>
						    </form:select>&nbsp;
						    <span class="optional"> Year </span>
                            <form:select path="runYear">
                               <form:option value="0">Select Year</form:option>
						       <c:forEach items="${yearList}" var="yList">
						         <form:option value="${yList.id}">${yList.name}</form:option>
						      </c:forEach>
						    </form:select>&nbsp;Deduction:
						    <form:select path="deductionId">
                               <form:option value="0">All Deductions</form:option>
						       <c:forEach items="${deductionList}" var="deduction">
						      <form:option value="${deduction.id}" title="${deduction.name}">${deduction.description}</form:option>
						      </c:forEach>
						    </form:select>&nbsp;
                 	    </td>
					</tr>
				</table>
					
				<tr>
                         <td class="buttonRow" align="right">
                             <input type="image" name="_updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                            <br/>
                            <br/>
                         </td>
                </tr>

			    <c:if test="${deductionDetails.noOfEmployees eq 0 }">

						<font color="red"><b>No Deductions found for <c:out value="${deductionDetails.monthAndYearStr}"/></b></font>

				</c:if>

				<tr>
						<td>
                            <table width="100%" id="deductReport" class="display table" cellspacing="0" cellpadding="0">
                                <thead>
                                   <tr class="">
                                      <th>Other Deduction Type</th>
                                      <th style="text-align:right">Total Withheld</th>
                                   </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${deductionDetails.deductionMiniBean}" var="dedMiniBean">
                                        <tr class="${dedMiniBean.displayStyle}">
                                            <td ><c:out value="${dedMiniBean.name}"/></td>
                                            <td align="right" valign="top" >
                                                <c:choose>
                                                    <c:when test="${dedMiniBean.apportionedType eq true}">
                                                        <a href="${appContext}/apportionedDeductionDetails.do?did=${dedMiniBean.deductionId}&rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&pid=${deductionDetails.id}">
                                                           ₦<c:out value="${dedMiniBean.amountStr}"/>
                                                        </a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a href="${appContext}/otherDeductionDetailsReport.do?did=${dedMiniBean.deductionId}&rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&pid=${deductionDetails.id}">
                                                            ₦<c:out value="${dedMiniBean.amountStr}"/>
                                                        </a>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
						</td>
				</tr>
				<c:if test="${deductionDetails.noOfEmployees gt 0 }">


					<tr align="left">
						<td><br/><b>No. of <c:out value="${roleBean.staffTypeName}"/> : <c:out value="${deductionDetails.noOfEmployees}"></c:out></b><br/><br/></td>
					</tr>

					<tr align="right">
						<td><b>Total Amount Deducted : <font color="green"><c:out value="${deductionDetails.totalCurrentDeductionStr}"></c:out></font></b><br/>
						 ( &nbsp;<font color="green"><c:out value="${deductionDetails.amountInWords}"></c:out></font> &nbsp;)</td>
					</tr>
					<tr>
						<td class="reportFormControlsSpacing"></td>
					</tr>
				</c:if>
				<tr>
                         <td class="buttonRow" align="right">
                        		  <input type="image" name="_cancel" value="cancel" title="Cancel" src="images/close.png">
				
                         </td>
                </tr>

				<table>
				    <tr>
				        <td class="activeTD"><form:radiobutton path="reportType" value="1"  onclick="if (this.checked) { document.getElementById('excelReportLink').style.display = 'none';document.getElementById('pdfReportLink').style.display = '';}"/>PDF Report Links <form:radiobutton path="reportType" value="2" onclick="if (this.checked) { document.getElementById('excelReportLink').style.display = '';document.getElementById('pdfReportLink').style.display = 'none';}"/> Excel Report Links</td>
				        <br><br>
				    </tr>
				</table>
				
				<div id="excelReportLink" class="reportBottomPrintLink" style='display:none'>
					<a class="reportLink" href="${appContext}/allOtherDeductionDetailsExcel.do?rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&pid=${deductionDetails.id}">
					Deduction Summary Excel&copy; </a><span class="tabseparator">|
					</span><a class="reportLink" href="${appContext}/deductionWithDetailsExcel.do?rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&pid=${deductionDetails.id}&exp=all">
					View With Details in Excel&copy;</a>
					<span class="tabseparator">|</span><a class="reportLink" href="${appContext}/deductionByMdaWithDetailsExcel.do?rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}">
					View By Deduction Type Report</a>
					<span class="tabseparator">|</span><a class="reportLink" href="${appContext}/groupDeductionByMdaExcel.do?rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&mda=t&rt=1">
					Group Deductions By <c:out value="${roleBean.mdaTitle}"/> Report</a><c:if test="${roleBean.superAdmin}"> <span class="tabseparator">|</span>
						<a class="reportLink" href="${appContext}/allDeductionByBankExcel.do?rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&mda=t&rt=1">
					Deductions By Bank Report</a>
					</c:if>
					<!--<span class="tabseparator">|</span><a href="${appContext}/allDeductionByMDAExcel.do?rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&mda=t">
					Deductions (ALL) By <c:out value="${roleBean.mdaTitle}"/> Report</a>

				--></div>
				<div id="pdfReportLink" class="reportBottomPrintLink" style='display:none'>
					<a class="reportLink" href="${appContext}/globalDeduction.do?rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}">
					Global Deduction PDF&copy; </a><span class="tabseparator">|</span>
					<a class="reportLink" href="${appContext}/groupDeductionByMdaExcel.do?rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&mda=t&rt=2">
					Deduction Report By <c:out value="${roleBean.mdaTitle}"/> PDF&copy;</a>
					<c:if test="${(roleBean.civilService or roleBean.subeb)}"><span class="tabseparator">|</span>
					<a class="reportLink" href="${appContext}/deductionByTSC.do?rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}">
					Deduction Report By Schools PDF&copy;</a>
					</c:if>
					<span class="tabseparator">|</span>
						<a class="reportLink" href="${appContext}/allDeductionByBankExcel.do?rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&mda=t&rt=2">
					Deductions By Bank Report PDF&copy;</a>
				</div>
				</td>
			</tr>

			<tr>
				<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
			</tr>
			</table>
			
			</form:form>
			<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
	        <script>
                $(function() {
                   $("#deductReport").DataTable({
                      "order" : [ [ 0, "asc" ] ],
                      "columnDefs":[
                              {"targets": [0], "orderable" : false}
                      ]
                   });
                });
                $(".reportLink").click(function(e){
                     $('#reportModal').modal({
                        backdrop: 'static',
                        keyboard: false
                     })
                });
	        </script>
		</body>

	</html>
