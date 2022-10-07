<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            User Account Audit Log Report
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
        #auditTbl thead tr th{
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
                                    View User Account Audit Logs<br>
                                    
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
                                            Dates :&nbsp;                                   
                                           </td> 
                                           
                                             <td class="activeTD">
                                             	<form:input path="fromDate" title="Start Date"/>
													<img src="images/calendar.png" id="fromDateId" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('fromDate'),event);">&nbsp;-&nbsp; 
													<form:input path="toDate" title="End Date"/>
													<img src="images/calendar.png" id="toDateId" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('toDate'),event);">
													
											</td>
											</tr>
											<tr>
		                                       <td class="activeTD">
		                                            User :&nbsp;                                   
		                                        </td>
		                                        <td class="activeTD">
                                                    <form:select path="id">
														<form:option value="0">&lt;Select&gt;</form:option>
														<c:forEach items="${userList}" var="uList">
														<form:option value="${uList.id}">${uList.firstName}&nbsp;${uList.lastName}</form:option>
														</c:forEach>
												</form:select>                                            
                                            </td>
                                       
                                         </tr>
                                         <tr>
                                     		<td class="buttonRow" align="right">
                                       		 	<input type="image" name="_updateReport" id="updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                        	</td>
                                   		 </tr>
                                   </table>
                                   <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <td valign="top">
											<p class="label">User Account Changes</p>
											<display:table name="dispBean" id="auditTbl" class="display table" export="false" sort="page" defaultsort="1" requestURI="${appContext}/viewUserAccountLogs.do">
											<display:setProperty name="export.rtf.filename" value="UserAccountAuditLogReport.rtf"/>
											<display:setProperty name="export.excel.filename" value="UserAccountAuditLogReport.xls"/>
											<display:column property="firstName" title="First Name"></display:column>
											<display:column property="lastName" title="Last Name"></display:column>
											<display:column property="userName" title="User Name"></display:column>
											<display:column property="description" title="Change Description"></display:column>
										    <display:column property="changedBy" title="Changed By"></display:column>
											<display:column property="auditTime" title="Changed Date"></display:column>
											<display:column property="remoteIpAddress" title="Initiating IP Address"></display:column>
											<display:setProperty name="paging.banner.placement" value="bottom" />
										</display:table>
										</td>
                                    </tr>
                                    <tr>
                                        
                                    </tr>
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                        <input type="image" name="_cancel" value="cancel" title="Close Report" src="images/close.png">
                                        </td>
                                    </tr>
                                </table>
                                 <c:if test="${miniBean.showLink}">
                                	                                <div class="reportBottomPrintLink">
                                											<a id="reportLink" href="#">
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
        <script>
            $(function() {
               $("#auditTbl").DataTable({
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
            $("#reportLink").click(function(e){
                        console.log("Hitting this");
                        $(".spin").show();
                        $.ajax({
                           type: "GET",
                           success: function (response) {
                              // do something ...
                              window.location.href ="${appContext}/userAccountLogsReport.do?uid=${miniBean.id}&sDate=${miniBean.fromDateStr}&eDate=${miniBean.toDateStr}";
                              console.log("Response here is " + response);
                              $(".spin").hide();
                           },
                           error: function (e) {
                              alert('Error: ' + e);
                           }
                        });
            });
        </script>
    </body>
</html>
