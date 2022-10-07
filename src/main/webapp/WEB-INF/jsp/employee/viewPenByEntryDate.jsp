<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            View <c:out value="${roleBean.staffTypeName}" />s hiring by month.
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
        <link rel="stylesheet" href="styles/spinner.css" TYPE="text/css" />
    </head>
    <style>
           #penByEntrantDate thead tr th{
              font-size:8pt !important;
           }
    </style>
    <body class="main">
    <div class="loader"></div>
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
    <form:form modelAttribute="miniBean">
	<c:set value="${miniBean.objectList}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <%@ include file="/WEB-INF/jsp/modal.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    View <c:out value="${roleBean.staffTypeName}" />s by Entry Date
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                <div id="topOfPageBoxedErrorMessage" style="display:${miniBean.displayErrors}">
									<spring:hasBindErrors name="miniBean">
										<ul>
											<c:forEach var="errMsgObj" items="${errors.allErrors}">
											<li>
												<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
											</li>
											</c:forEach>
										</ul>
									</spring:hasBindErrors>
								</div>

						        <table>
                                	<tr align="left">
                                             <td class="activeTH" colspan="2">Filter By</td>
                                	</tr>
                                	<tr>
                                          <td class="activeTD">
                                               Select Month :&nbsp;
                                          </td>
                                          <td class="activeTD">
                                               <form:select path="runMonth">
                                                  <form:option value="-1">Select Month</form:option>
                                               	  <c:forEach items="${monthList}" var="mList">
                                               		 <form:option value="${mList.id}">${mList.name}</form:option>
                                               	  </c:forEach>
                                               </form:select>
                                		  </td>
                                    </tr>
                                    <tr>
                                        <td class="activeTD">
                                            Select Year :&nbsp;
                                        </td>
                                        <td class="activeTD">
                                            <form:select path="runYear">
                                               <form:option value="0">Select Year</form:option>
                                                  <c:forEach items="${yearList}" var="yList">
                                                 	<form:option value="${yList.id}">${yList.name}</form:option>
                                                  </c:forEach>
                                            </form:select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <br>
                                            <input type="image" name="_updateReport" id="updateReport" value="updateReport" alt="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                        </td>
                                    </tr>
                                </table>
                                <br><br>
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <td valign="top">
											<p class="label">Pensioners by Creation Date</p>
											<display:table name="dispBean" id="penByEntrantDate" class="display table" export="false" sort="page" defaultsort="1" requestURI="${appContext}/viewCreatedEmpForm.do">
											<display:setProperty name="export.rtf.filename" value="PensionersByEntrantMonthAndYear.rtf"/>
											<display:setProperty name="export.excel.filename" value="PensionersByEntrantMonthAndYear.xls"/>
											<display:column property="employeeId" title="${roleBean.staffTitle}" media="html" href="${appContext}/employeeOverviewForm.do" paramId="eid" paramProperty="id"></display:column>
											<display:column property="employeeId" title="${roleBean.staffTitle}" media="excel"/>
											<display:column property="employeeName" title="Pensioner Name"></display:column>
											<display:column property="accountNumber" title="Account Number"></display:column>
											<display:column property="mda" title="TCO"></display:column>
											<display:column property="monthlyPensionStr" title="Monthly Pension"></display:column>
											<display:column property="arrearsStrSansNaira" title="Arrears"></display:column>
										    <display:column property="totalDeductionsStr" title="Deductions"></display:column>
										    <display:column property="netPayStr" title="Net Pay"></display:column>
											<display:setProperty name="paging.banner.placement" value="" />
										</display:table>
										</td>
                                    </tr>
					                <c:if test="${listSize gt 0}">
                                        <tr>
                                            <td>
                                            <br><br>
                                            <div class="reportBottomPrintLink">
                                                <!-- <a href="${appContext}/biometricTemplateExcel.do?fd=${miniBean.fromDateStr}&td=${miniBean.toDateStr}" title='Generate Biometric Template Report'>
                                                Generate Biometric File Upload </a><span class="tabseparator">|</span> -->
                                                <a id="reportLink" href="${appContext}/pensionEntrantReportByTCOExcel.do?rm=${miniBean.runMonth}&ry=${miniBean.runYear}" title='Export Report To Microsoft Excel'>
                                                Generate Report In Excel </a><br />
                                                </div>
                                                <br>
                                            </td>
                                        </tr>
                                    </c:if>
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                        <input type="image" name="_cancel" value="cancel" alt="Cancel" src="images/close.png">
                                        </td>
                                    </tr>
                                </table>

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
        <div class="spin"></div>
        <script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
        <script>
                    $(function() {
                        $("#penByEntrantDate").DataTable({
                                    "order" : [ [ 1, "asc" ] ],
                                    //can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
                                    //also properties higher up, take precedence over those below
                                    "columnDefs":[
                                       {"targets": [0], "orderable" : false}
                                    ]
                        });
                    });
                    $("#updateReport").click(function(e){
                        $(".spin").show();
                    });

                    window.onload = function exampleFunction() {
                        $(".loader").hide();
                    }
                    $("#reportLink").click(function(e){
                       $('#reportModal').modal({
                          backdrop: 'static',
                          keyboard: false
                       });
                    });
        </script>
    </body>
</html>
