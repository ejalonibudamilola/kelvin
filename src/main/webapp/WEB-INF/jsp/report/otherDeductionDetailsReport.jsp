<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
    <head>
        <title> Other Deductions </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css" />
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
        <link rel="icon" type="image/png" href="<c:url value="/images/coatOfArms.png"/>">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css" />" type="text/css"/>
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
                                        <c:out value="${deductionDetails.currentDeduction}"/> deduction<br>
                                        For <c:out value="${deductionDetails.monthAndYearStr}"/>
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
                                                    <form:option value="employeeDetailsReport"><c:out value="${roleBean.staffTypeName}"/> Details</form:option>
                                                    <form:option value="preLastPaycheckForm">Last Paycheck</form:option>
                                                </form:select>
                                            </td>
                                            <td>&nbsp;</td>
                                            <td>
                                                <input type="image" name="_go" value="go" title="Go" class="" src="images/go_h.png" >
                                            </td>
                                        </tr>
                                    </table>
                                    <span class="reportTopPrintLink">
                                        <a href="${appContext}/otherDeductionDetailsExcel.do?did=${deductionDetails.deductionId}&pfv=yes&rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&pid=${deductionDetails.id}">
                                            View in Excel
                                        </a><br />
                                    </span>
                                    <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                        <tr>
                                            <td class="reportFormControls">
                                                <span class="optional"> Month </span>
                                                <form:select path="runMonth">
                                                    <form:option value="-1">Select Month</form:option>
                                                    <c:forEach items="${monthList}" var="mList">
                                                        <form:option value="${mList.id}">${mList.name}</form:option>
                                                    </c:forEach>
                                                </form:select>&nbsp;
                                                <form:select path="runYear"> <span class="optional"> Year </span>
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
                                        <tr>
                                            <td class="buttonRow" align="right">
                                                <input type="image" name="_updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png"><br/>
                                                <br/>
                                                <br/>
                                            </td>
                                        </tr>
                                    <tr>
                                        <td>
                                            <table id="deductReport" class="display table" cellspacing="0" cellpadding="0">
                                                <thead>
                                                    <tr>
                                                        <th><c:out value="${roleBean.staffTitle}"/></th>
                                                        <th><c:out value="${roleBean.staffTypeName}"/> Name</th>
                                                        <th><c:out value="${roleBean.mdaTitle}"/></th>
                                                        <th align="right">Amount Withheld</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach items="${deductionDetails.deductionMiniBean}" var="dedMiniBean">
                                                        <tr class="${dedMiniBean.displayStyle}">
                                                            <td class="tableCell" valign="top"><a href='${appContext}/viewDeductionHistory.do?eid=${dedMiniBean.id}&dedId=${dedMiniBean.parentInstId}' target="_blank" onclick="popupWindow(this.href,'${dedMiniBean.name}'); return false;" target="_blank"><c:out value="${dedMiniBean.employeeId}"/></a></td>
                                                            <td class="tableCell" valign="top"><c:out value="${dedMiniBean.name}"/></td>
                                                            <td class="tableCell" valign="top"><c:out value="${dedMiniBean.mdaName}"/></td>
                                                            <td class="tableCell" align="right" valign="top"><c:out value="${dedMiniBean.amountStr}"/></td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                            <c:if test="${deductionDetails.noOfEmployees gt 0 }">
                                                <tr>
                                                    <td class="reportFormControlsSpacing"></td>
                                                </tr>
                                                <tr align="left">
                                                    <td><b>No. of <c:out value="${roleBean.staffTypeName}"/> : <c:out value="${deductionDetails.noOfEmployees}"/></b></td>
                                                </tr>
                                                <tr align="right">
                                                    <td nowrap>
                                                        <b>Total Amount Deducted : <font color="green"><c:out value="${deductionDetails.totalCurrentDeductionStr}"></c:out></font></b><br>
                                                        ( &nbsp;<font color="green"><c:out value="${deductionDetails.amountInWords}"></c:out></font> &nbsp;)
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td class="reportFormControlsSpacing"></td>
                                                </tr>
                                            </c:if>
                                            <!--<table class="report" cellspacing="0" cellpadding="0">
                                                <tr class="reportEven footer">
                                                    <td class="tableCell" valign="top" width="97px">Totals</td>
                                                    <td class="tableCell" valign="top" width="246px"><c:out value="${deductionDetails.noOfEmployees}"/></td>
                                                    <td class="tableCell" valign="top" align="right" width="250px">₦<c:out value="${deductionDetails.totalStr}"/></td>
                                                </tr>
                                            </table>-->
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="buttonRow" align="right">
                                            <input type="image" name="_cancel" value="cancel" title="Cancel" src="images/close.png">
                                            <br/>
                                            <br/>
                                        </td>
                                    </tr>
                                </td>
                            </tr>
                        </table>
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
                            <a class="reportLink" href="${appContext}/otherDeductionDetailsExcel.do?did=${deductionDetails.deductionId}&pfv=yes&rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&pid=${deductionDetails.id}&rt=1">
                                View in Excel
                            </a>
                            <span class="tabseparator">|</span>
                            <a href="${appContext}/singleDeductionByMdaExcel.do?did=${deductionDetails.deductionId}&rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&rt=1">
                                View Deduction By <c:out value="${roleBean.mdaTitle}"/> in Excel
                            </a><br />
                        </div>
                        <div id="pdfReportLink" class="reportBottomPrintLink" style='display:none'>
                                    <a class="reportLink" href="${appContext}/otherDeductionDetailsExcel.do?did=${deductionDetails.deductionId}&pfv=yes&rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&pid=${deductionDetails.id}&rt=2">
                                    View this Deduction in PDF&copy; </a><span class="tabseparator">|</span>
                                    <a class="reportLink" href="${appContext}/singleDeductionByMdaExcel.do?did=${deductionDetails.deductionId}&rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&rt=2">
                                    Deduction Report By <c:out value="${roleBean.mdaTitle}"/> PDF&copy;</a><span class="tabseparator">|</span>
                                    <a class="reportLink" href="${appContext}/singleDeductionByTSC.do?did=${deductionDetails.deductionId}&rm=${deductionDetails.runMonth}&ry=${deductionDetails.runYear}&rt=2">
                                    Deduction Report By Schools PDF&copy;</a>
                        </div>
                        <br>
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
              })
           });
        </script>
    </body>
</html>
