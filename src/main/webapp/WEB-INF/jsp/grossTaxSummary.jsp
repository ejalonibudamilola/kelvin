<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
    <head>
        <title>
            Detailed Payroll Analysis Report
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
        <link rel="stylesheet" href="styles/spinner.css" TYPE="text/css" />
    </head>
     <style>
           .grossTax thead tr th{
              font-size:8pt !important;
           }
     </style>
    <body class="main">
    <div class="loader"></div>
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
    <form:form modelAttribute="paystubSummary">
	<c:set value="${paystubSummary}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <%@ include file="/WEB-INF/jsp/modal.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    <c:out value="${paystubSummary.companyName}"/><br>
                                    Tax YTD Summary Report
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                                              
                              
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
										<td class="reportFormControls">
										Payroll Run Year&nbsp;<form:select path="runYear">
														<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
														<c:forEach items="${yearList}" var="yList">
														<form:option value="${yList.id}">${yList.id}</form:option>
														</c:forEach>
														</form:select>
										</td>
										
								</tr>
								<tr>
			                     <td class="buttonRow" align="left">
			                       <input style="margin-bottom:3%" type="image" name="_update" id="updateReport" value="update" title="Update Report" src="images/Update_Report_h.png">
			                     </td>
			                    </tr>
                                    <tr>
                                        <td class="reportFormControlsSpacing"></td>
                                    </tr>
                                    <tr style="${paystubSummary.showRow}">
                                    	
                                    		<display:table name="dispBean" id="row" class="display table grossTax" sort="page" defaultsort="1" requestURI="${appContext}/taxSummaryYTD.do">
												<display:caption>Detailed Tax YTD Report</display:caption>
												<display:column title="S/No."><c:out value="${row_rowNum}"/></display:column>
												<display:column property="employeeName" title="${roleBean.staffTypeName} Name"></display:column>
												<display:column property="employeeId" title="${roleBean.staffTitle}"></display:column>
												<display:column property="mda" title="${roleBean.mdaTitle}"></display:column>
												<display:column property="grossPayYTDStr" title="Gross Pay YTD"></display:column>																							
												<display:column property="taxableIncomeYTDStr" title="Taxable Income YTD"></display:column>																					
												<display:column property="taxPaidYTDStr" title="PAYE YTD"></display:column>
												<display:setProperty name="paging.banner.placement" value="" />
											</display:table>
                                    	
                                    </tr>
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                        <input type="image" name="_cancel" value="cancel" title="Close Report" class=""" src="images/close.png">
                                        </td>
                                    </tr>
                                </table>
                               
									<br>
									
									<div class="reportBottomPrintLink">
										<a class="reportLink" href="${appContext}/taxYTDSummaryExcel.do?ry=${paystubSummary.runYear}&rt=2">
										Tax Summary PDF&copy; </a><span class="tabseparator">|</span>
										<a class="reportLink" href="${appContext}/taxYTDSummaryExcel.do?ry=${paystubSummary.runYear}&rt=1">
										Tax Summary Excel&copy; </a>
										<br />
										
									</div><!--
                                <div class="reportBottomPrintLink">
                                    <a href="${appContext}/payrollAnalysisByMdaExcel.do?fd=${paystubSummary.fromDateAsString}&td=${paystubSummary.toDateAsString}" target="_blank">Detailed Payroll Analysis in Excel&copy;</a> &nbsp; 
                                    <a href="${appContext}/payrollAnalysis.do?fd=${paystubSummary.fromDateAsString}&td=${paystubSummary.toDateAsString}&bpv=t" target="_blank">Payroll Analysis in Excel&copy;</a>&nbsp;
                                   </div><br>-->
                                
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
        <div class="spin"></div>
        <script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
        <script>
                    $(function() {
                        $(".grossTax").DataTable({
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
                    $(".reportLink").click(function(e){
                       $('#reportModal').modal({
                          backdrop: 'static',
                          keyboard: false
                       });
                    });
        </script>
    </body>
</html>
