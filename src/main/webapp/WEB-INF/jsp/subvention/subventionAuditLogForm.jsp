<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            Subvention Audit Log Report
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
       #subventiontbl thead tr th{
          font-size:8pt !important;
       }
    </style>
    <body class="main">
    <script type="text/javascript" src="scripts/jacs.js"></script>
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
                                    View Subvention Audit Logs<br>
                                    
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                  <div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
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
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="80%">
                                    <tr>
                                        <td class="reportFormControls">
                                            
                                                    <span class="optional"> Between </span> <form:input path="fromDate"/>
													<img src="images/calendar.png" id="fromDateId" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('fromDate'),event);"><span class="optional">&nbsp;And </span> <form:input path="toDate"/>
													<img src="images/calendar.png" id="toDateId" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('toDate'),event);">&nbsp;&nbsp; Select User 
                                                    <form:select path="id">
														<form:option value="0">&lt;Select&gt;</form:option>
														<c:forEach items="${userList}" var="uList">
														<form:option value="${uList.id}">${uList.firstName}&nbsp;${uList.lastName}</form:option>
														</c:forEach>
												</form:select>                                            
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding-top:1%">
                                         <input type="image" name="_updateReport" id="updateReport" value="updateReport" alt="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                       </td>
                                    </tr>
                                    </table>
                                    <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <td valign="top">
											<p class="label">Subvention Changes</p>
											<display:table name="dispBean" id="subventiontbl" class="display table" export="false" sort="page" defaultsort="1" requestURI="${appContext}/viewSubventionLog.do">
											<display:setProperty name="export.rtf.filename" value="SubventionLogAuditLogs.rtf"/>
											<display:setProperty name="export.excel.filename" value="SubventionLogAuditLogs.xls"/>
											<display:column property="name" title="Subvention Name"></display:column>
											<display:column property="columnChanged" title="Column Changed"></display:column>
											<display:column property="oldValue" title="Old Value" />
											<display:column property="newValue" title="New Value"></display:column>
											<display:column property="changedBy" title="Changed By"></display:column>
											<display:column property="auditTime" title="Changed Date"></display:column>
										</display:table>
										</td>
                                    </tr>
                                    <tr>
                                        
                                    </tr>
                                    <tr>
                                     	<td class="buttonRow" align="right">

                                        <input type="image" name="_cancel" value="cancel" alt="Cancel" src="images/close.png">
                                        </td>
                                    </tr>
                                </table>
                                 <c:if test="${miniBean.showLink}">


                                                                                                   		<div class="reportBottomPrintLink">
                                                                											<a href="${appContext}/subventionAuditExcelReport.do?sDate=${miniBean.fromDateStr}&eDate=${miniBean.toDateStr}&uid=${miniBean.id}">
                                                                										View All Results in Excel&copy; </a><br />
                                                                			                        </div>
                                                                		                        </c:if>
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
        <script type="text/javascript">
           $(function() {
              $("#subventiontbl").DataTable({
                 "order" : [ [ 1, "asc" ] ]
              });

              $("#updateReport").click(function(e){
                 $(".spin").show();
              });
           });
        </script>
    </body>
</html>
