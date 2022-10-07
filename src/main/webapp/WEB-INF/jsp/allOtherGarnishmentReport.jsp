<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title>All Loans</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">


</head>
<body class="main">
    <form:form modelAttribute="garnishmentDetails">
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
	        <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
	        <%@ include file="/WEB-INF/jsp/modal.jsp" %>

	        <tr>
		        <td>
		            <table width="100%" >
			            <tr>
				            <td colspan="2">
                                <div class="title">
                                    Ogun State <c:out value="${roleBean.businessName}" />
                                    Loans for <c:out value="${garnishmentDetails.monthAndYearStr}"/>
                                </div>
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
                                                <form:option value="allOtherDeductionsReport">Deductions</form:option>
                                            </form:select>
                                        </td>
                                        <td>&nbsp;</td>
                                        <td>
                                            <input type="image" name="_go" value="go" title="Go" class="" src="images/go_h.png" >
                                        </td>
                                    </tr>
				                </table>
				
                                <!--<span class="reportTopPrintLink">
                                    <a href="${appContext}/allOtherGarnishmentDetailsExcel.do?sDate=${garnishmentDetails.fromDateStr}&eDate=${garnishmentDetails.toDateStr}&pid=${garnishmentDetails.id}">
                                    View in Excel </a>&nbsp;&nbsp;<a href="${appContext}/garnishmentWithDetailsExcel.do?sDate=${garnishmentDetails.fromDateStr}&eDate=${garnishmentDetails.toDateStr}&pid=${garnishmentDetails.id}&exp=all">
                                    View With Details in Excel</a></span>
                                -->
                                <div class="reportDescText">
                                    <p>
                                       This report shows the total for each payroll loan deduction
                                       during the date range you select.
                                    </p>
                                    <p>
                                      Click any total to see the details that make up the total.
                                    </p>
                                </div>
                                <table>
                                    <tr align="left">
                                        <td class="activeTH" colspan="2">Filter By</td>
                                    </tr>
                                    <tr>
                                       <td class="activeTD">
                                          Pay Period :&nbsp;
                                       </td>
                                       <td class="activeTD">
                                          Month&nbsp;
                                          <form:select path="runMonth">
                                             <form:option value="-1">Select Month</form:option>
                                             <c:forEach items="${monthList}" var="mList">
                                                <form:option value="${mList.id}">${mList.name}</form:option>
                                             </c:forEach>
                                          </form:select>
                                          Year&nbsp
                                          <form:select path="runYear">
                                             <form:option value="0">Select Year</form:option>
                                             <c:forEach items="${yearList}" var="yList">
                                                <form:option value="${yList.id}">${yList.name}</form:option>
                                             </c:forEach>
                                          </form:select>
                                       </td>
                                    </tr>
                                    <tr>
                                       <td class="activeTD">
                                           Loan Type :&nbsp;
                                       </td>
                                       <td class="activeTD">
                                          <form:select path="deductionId">
                                             <form:option value="0">All Garnishments</form:option>
                                             <c:forEach items="${garnishmentList}" var="garnishType">
                                                <form:option value="${garnishType.id}" title="${garnishType.name}">${garnishType.description}</form:option>
                                             </c:forEach>
                                          </form:select>
                                       </td>
                                    </tr>
                                    <tr>
                                       <td class="buttonRow" align="right">
                                           <input type="image" name="_updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                       </td>
                                    </tr>
                                </table>


                                <!--<table class="reportMain" cellpadding="0" cellspacing="0" width="70%">
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
                                            </form:select>&nbsp;
                                            <span>Garnishment: </span>
                                            <form:select path="deductionId">
                                                <form:option value="0">All Loans</form:option>
                                                <c:forEach items="${garnishmentList}" var="garnishments">
                                                   <form:option value="${garnishments.id}" title="${garnishments.name}">${garnishments.description}</form:option>
                                                </c:forEach>
                                            </form:select>&nbsp;
                                        </td>
                                    </tr>
                                </table>-->
                                <c:if test="${garnishmentDetails.noOfEmployees eq 0 }">
                                        <font color="red"><b>No Loan Deductions found for <c:out value="${garnishmentDetails.monthAndYearStr}"/></b></font>
                                </c:if>
                                <br/>
                                <table width="100%"  id ="garnReport" class="display table" cellspacing="0" cellpadding="0">
                                                <thead>
                                                    <tr class="">
                                                        <th>Garnishment Type</th>
                                                        <th style="text-align:right">Total Withheld</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach items="${garnishmentDetails.deductionMiniBean}" var="dedMiniBean">
                                                        <tr class="${dedMiniBean.displayStyle}">
                                                            <td><c:out value="${dedMiniBean.name}"/></td>
                                                            <td align="right">
                                                                <a href="${appContext}/otherGarnishmentDetailsReport.do?did=${dedMiniBean.deductionId}&rm=${garnishmentDetails.runMonth}&ry=${garnishmentDetails.runYear}&pid=${garnishmentDetails.id}">
                                                                    ₦<c:out value="${dedMiniBean.amountStr}"/>
                                                                </a>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                </table>

                                <c:if test="${garnishmentDetails.noOfEmployees gt 0 }">
                                        <tr>
                                            <td class="reportFormControlsSpacing"></td>
                                        </tr>
                                        <tr align="left">
                                            <td><b>No. of <c:out value="${roleBean.staffTypeName}"/> : <c:out value="${garnishmentDetails.noOfEmployees}"/></b></td>
                                        </tr>
                                        <tr align="right">
                                            <td nowrap>
                                                <b>Total Amount Deducted : <font color="green"><c:out value="${garnishmentDetails.totalCurrentDeductionStr}"></c:out></font></b><br>
                                                ( &nbsp;<font color="green"><c:out value="${garnishmentDetails.amountInWords}"></c:out></font> &nbsp;)
                                            </td>
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
                                    </tr>
                                </table>
                                <div id="excelReportLink" class="reportBottomPrintLink" style='display:none'>
                                    <a class="reportLink" href="${appContext}/allOtherGarnishmentDetailsExcel.do?rm=${garnishmentDetails.runMonth}&ry=${garnishmentDetails.runYear}&pid=${garnishmentDetails.id}&rt=1">
                                    Loan Summary Excel&copy; </a><span class="tabseparator">|</span>
                                    <a class="reportLink" href="${appContext}/garnishmentByMDAExcel.do?rm=${garnishmentDetails.runMonth}&ry=${garnishmentDetails.runYear}&rt=1">
                                    Group Loans By Type Excel</a>
                                    <span class="tabseparator">|</span>
                                    <a class="reportLink" href="${appContext}/groupLoansByMDAExcel.do?rm=${garnishmentDetails.runMonth}&ry=${garnishmentDetails.runYear}&mda=t&rt=1">
                                    Group Loans By MDAs Excel</a><c:if test="${roleBean.superAdmin}"> <span class="tabseparator">|</span>
                                        <a class="reportLink" href="${appContext}/allLoanByBankPdf.do?rm=${garnishmentDetails.runMonth}&ry=${garnishmentDetails.runYear}&rt=1">
                                    Loans By Bank Report</a>
                                    </c:if>
                                </div>

                                <div id="pdfReportLink" class="reportBottomPrintLink" style='display:none'>
                                    <a class="reportLink" href="${appContext}/allOtherGarnishmentDetailsExcel.do?rm=${garnishmentDetails.runMonth}&ry=${garnishmentDetails.runYear}&pid=${garnishmentDetails.id}&rt=2">
                                    Loan Summary PDF&copy; </a><span class="tabseparator">|</span>
                                    <a class="reportLink" href="${appContext}/groupLoansByMDAExcel.do?rm=${garnishmentDetails.runMonth}&ry=${garnishmentDetails.runYear}&mda=t&rt=2">
                                    Loan Report By MDA PDF&copy;</a><span class="tabseparator">|</span>
                                    <a class="reportLink" href="${appContext}/globalLoan.do?rm=${garnishmentDetails.runMonth}&ry=${garnishmentDetails.runYear}">
                                    Global Loan Report PDF&copy; </a><span class="tabseparator">|</span>

                                    <a class="reportLink" href="${appContext}/globalTSCLoan.do?rm=${garnishmentDetails.runMonth}&ry=${garnishmentDetails.runYear}">
                                    Global School Loan Report PDF&copy;</a><c:if test="${roleBean.superAdmin}">
                                        <span class="tabseparator">|</span>
                                        <a class="reportLink" href="${appContext}/allLoanByBankPdf.do?rm=${garnishmentDetails.runMonth}&ry=${garnishmentDetails.runYear}&rt=2">
                                    Loans By Bank Report</a>
                                    </c:if>
                                </div>
                            </td>
                        </tr>
                        <tr>
                           <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
                        </tr>
                    </table>
			    </td>
			</tr>
        </table>
	</form:form>
    <script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
    <script>
                     $(function() {
                        $("#garnReport").DataTable({
                           "order" : [ [ 0, "desc" ] ],
                           "columnDefs":[
                              {"targets": [0], "orderable" : false}
                           ]
                        });
                     });
                     $(".reportLink").click(function(e){
                        $('#reportModal').modal({
                           backdrop: 'static',
                           keyboard: false
                        });
                     });
    </script>
    </body>
</html>
