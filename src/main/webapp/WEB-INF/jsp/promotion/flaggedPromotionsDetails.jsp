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
    </head>
    <body class="main">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
    <form:form modelAttribute="miniBean">
	<c:set value="${miniBean.variationList}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

                        <tr>
                            <td colspan="2">
                                <div class="title">
                                   Flagged Promotion List
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">

                               <br>
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">

                                    <tr>
                                        <td class=" "></td>
                                    </tr>

                                    <br />
                                    				<table class="report" cellspacing="0" cellpadding="0">
                                    						<tr class="reportOdd header">
                                    						<td class="tableCell" valign="top" width="27px">Staff Id</td>
                                    						<td class="tableCell" valign="top" width="56px">Staff Name</td>
                                    						<td class="tableCell" width="27px" valign="top" align="right">Promotion Date</td>
                                    						<td class="tableCell" width="27px" valign="top" align="right">Current Mda</td>
                                    						<td class="tableCell" width="27px" valign="top" align="right">Current Gross</td>
                                    						<td class="tableCell" width="27px" valign="top" align="right">Previous Gross</td>
                                    						<td class="tableCell" width="27px" valign="top" align="right">Current Annual Salary</td>
                                    						<td class="tableCell" width="27px" valign="top" align="right">Previous Annual Salary</td>
                                    						<td class="tableCell" width="27px" valign="top" align="right">Promoted By</td>
                                    					</tr>

                                    					<c:forEach items="${miniBean.variationList}" var="vList">
                                    					<c:forEach items="${vList}" var="vBean">
                                    					<tr class="${dedMiniBean.displayStyle}">
                                    						<td class="tableCell" valign="top" width="27px" align="right"><c:out value="${vBean.employeeId}"/></td>
                                    						<td class="tableCell" valign="top" width="56px" align="right"><c:out value="${vBean.employeeName}"/></td>
                                    						<td class="tableCell" valign="top" width="27px" align="right"><c:out value="${vBean.promotionDate}"/></td>
                                    						<td class="tableCell" valign="top" width="27px" align="right"><c:out value="${vBean.mda}"/></td>
                                    						<td class="tableCell" valign="top" width="27px" align="right"><c:out value="${vBean.thisMonthGrossStr}"/></td>
                                    						<td class="tableCell" valign="top" width="27px" align="right"><c:out value="${vBean.prevMonthGrossStr}"/></td>
                                    						<td class="tableCell" valign="top" width="27px" align="right"><c:out value="${vBean.basicSalaryStr}"/></td>
                                    						<td class="tableCell" valign="top" width="27px" align="right"><c:out value="${vBean.oldSalaryStr}"/></td>
                                    						<td class="tableCell" valign="top" width="27px" align="right"><c:out value="${vBean.promotedBy}"/></td>
                                    					</tr>
                                    					</c:forEach>
                                    					</c:forEach>

                                    				</table>


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
					<a href="${appContext}/bankSummaryReport.do?fd=${miniBean.fromDateStr}&td=${miniBean.toDateStr}&rt=1" target="_blank">
					Bank Summary Excel&copy; </a>


				</div>
				<div id="pdfReportLink" class="reportBottomPrintLink" style='display:none'>
					<a href="${appContext}/bankSummaryReport.do?fd=${miniBean.fromDateStr}&td=${miniBean.toDateStr}&rt=2" target="_blank">
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
    </body>
</html>
