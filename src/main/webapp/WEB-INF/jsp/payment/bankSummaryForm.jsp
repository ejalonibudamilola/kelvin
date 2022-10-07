<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
    <head>
        <title>
            Bank Summary
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
        <link rel="stylesheet" href="styles/spinner.css" TYPE="text/css" />
    </head>

    <style>
        #bankSummary thead tr th{
            font-size:8pt !important;
        }
    </style>

    <body class="main">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
    <form:form modelAttribute="miniBean">
	<c:set value="${displayList}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    <c:out value="${miniBean.companyName}"/><br>
                                    Bank Summary
                                </div>
                            </td>
                        </tr>
                        <tr>
                        	<td>
                        	<br>
                        	<br>
                        	<table>
							<tr>
								<td class="reportFormControls">
								Month&nbsp;<form:select path="runMonth">
												<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<c:forEach items="${monthList}" var="mList">
												<form:option value="${mList.id}">${mList.name}</form:option>
												</c:forEach>
												</form:select>
								</td>
								<td class="reportFormControls">
								Year&nbsp;<form:select path="runYear">
												<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<c:forEach items="${yearList}" var="yList">
												<form:option value="${yList.id}">${yList.id}</form:option>
												</c:forEach>
												</form:select>
								</td>


							</tr>
						</table>

                        	</td>

                        </tr>
                        <tr>
                                     	<td class="buttonRow" align="right">
                                        &nbsp;&nbsp;&nbsp;<input type="image" id="updateReport" name="_updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                        </td>
                                    </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">

                               <br>
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    <!--<tr>
                                        <td class="reportFormControls">
                                            <div class="registerDatePicker"></div>
                                        </td>
                                    </tr>-->
                                    <tr>
                                        <td class=" "></td>
                                    </tr>

                                    <tr style="${miniBean.showRow}">
                                        <td>
                                            <p><c:out value="${miniBean.companyName}"/> Payroll Summary</p>
                                        </td>
                                    	<display:table name="dispBean" id="bankSummary" class="display table" sort="page" defaultsort="1" requestURI="${appContext}/bankSummary.do">
										    <display:column property="bankName" title="Bank Name"></display:column>
											<display:column property="payableAmountStr" title="Amount"></display:column>
											<display:column property="totalStaff" title="Total Staff"></display:column>
											<display:setProperty name="paging.banner.placement" value="" />
										</display:table>
                                    </tr>
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                        <input type="image" name="_close" value="close" title="Close Window" class="" src="images/close.png">
                                        </td>
                                    </tr>
                                </table><br>
				<br>
				<table>
				<tr>
				<td class="activeTD"><form:radiobutton path="reportType" value="1"  onclick="if (this.checked) { document.getElementById('excelReportLink').style.display = 'none';document.getElementById('pdfReportLink').style.display = '';}"/>PDF Report Links <form:radiobutton path="reportType" value="2" onclick="if (this.checked) { document.getElementById('excelReportLink').style.display = '';document.getElementById('pdfReportLink').style.display = 'none';}"/> Excel Report Links</td>
				</tr>
				</table>
				<br>
				<div id="excelReportLink" class="reportBottomPrintLink" style='display:none'>
					<a href="${appContext}/bankSummaryReport.do?rm=${miniBean.runMonth}&ry=${miniBean.runYear}&rt=1" target="_blank">
					Bank Summary Excel&copy; </a>


				</div>
				<div id="pdfReportLink" class="reportBottomPrintLink" style='display:none'>
					<a href="${appContext}/bankSummaryReport.do?rm=${miniBean.runMonth}&ry=${miniBean.runYear}&rt=2" target="_blank">
                    					Bank Summary PDF&copy; </a>

				</div>
				<br>


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
        <script type="text/javascript">

            		$("#updateReport").click(function(e){
                      $(".spin").show();
                    });


                    $(function() {

            			$("#bankSummary").DataTable({
            				"order" : [ [ 0, "asc" ] ]
            			});
            		});
        </script>
    </body>
</html>
