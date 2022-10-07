<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            Errors Audit Log Report
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
    </head>
    <body class="main">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
    <style>
        .register1 {background: #9BA9B3; width:1200px; margin: 0 10px 10px 10px}
        .register1 th  {background: #EBF6FD; font-size:8pt}
        .register1 tr:nth-child(even) {background: #EBF6FD; font-size:8pt}
        .register1 tr:nth-child(odd) {background: white; font-size:8pt}
    </style>


    <form:form modelAttribute="miniBean">
	<c:set value="${miniBean}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    View Error Audit Logs<br>
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
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="90%">
                                <c:if test="${roleBean.privilegedUser}">
                                <tr>
                                  <td>
                                       <span class="">
                                           <a href='${appContext}/manual.do?deploy' title="Set Deploying Status"><i>Set Deploying Status</i></a>
                                       </span>
                                  </td>
                                  <td>
                                      <span class="reportTopPrintLinkExtended">
                                          <a href='${appContext}/createEditMiniSalary.do' title="Create Mini Salary Information"><i>Create Mini Salary</i></a>
                                         </span>
                                        </td>

                                     </tr>
                                     </c:if>
                                    <tr>
                                        <td class="reportFormControls">
                                            
                                                    <span class="optional"> Between </span> <form:input path="fromDate"/>
													<img src="images/calendar.png" id="fromDateId" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('fromDate'),event);"><span class="optional">&nbsp;And </span> <form:input path="toDate"/>
													<img src="images/calendar.png" id="toDateId" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('toDate'),event);">&nbsp;&nbsp; Select User 
                                                    <form:select path="id">
														<form:option value="0">&lt;Select&gt;</form:option>
														<c:forEach items="${userList}" var="uList">
														<form:option value="${uList.id}" title="${uList.role.businessClient.name}">${uList.firstName}&nbsp;${uList.lastName}</form:option>
														</c:forEach>
												</form:select>                       
                                        </td>
                                       
                                    </tr>
                                     <tr>
                                     	<td class="buttonRow" align="right">
                                        <input type="image" name="_updateReport" value="updateReport" alt="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                        <input type="image" name="_cancel" value="cancel" alt="Cancel" src="images/close.png">
                                        </td>
                                    </tr>
                                    <!-- <tr>
                                        <td valign="top">
											<p class="label">IPPMS Error Log Entries</p>
											<display:table name="dispBean" class="register3" export="false" sort="page" defaultsort="1" requestURI="${appContext}/viewErrorAuditLog.do">
                                            	<display:column property="user.actualUserName" title="Originator" media="html" href="${appContext}/viewErrorAuditLog.do" paramId="pid" paramProperty="id"  style="width:2%;white-space:nowrap;"></display:column>
                                            	<display:column property="user.actualUserName" title="Originator" media="excel"></display:column>
                                            	<display:column property="errorLogTime" title="Error Log Time" style="width:4%;white-space:nowrap;"></display:column>
                                            	<display:column property="errorCause" title="Error Cause" style="white-space:nowrap;"></display:column>
                                            	<display:column property="details" title="Treated" media="html" href="${appContext}/viewPayrollDetails.do" paramId="pid" paramProperty="id"></display:column>
                                            </display:table>
										</td>
                                    </tr>
                                    <tr>
                                        <td>&nbsp;</td>
                                        
                                    </tr> -->

                                </table>
                            </td>
                        </tr>
                        <c:if test="${size > 0}">
                        <tr>
                            <td>
                                <table width="100%" class="register1">
                                    <thead>
                                        <th>Originator</th>
                                        <th>Error Log Time</th>
                                        <th>Error Cause</th>
                                        <th></th>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${empList}" var="wBean" varStatus="gridRow">
                                            <tr>
                                                <td style="white-space:nowrap;"><a href="#" onClick="showModal(${wBean.id}); return false"><c:out value="${wBean.user.actualUserName}"/></a></td>
                                                <!--<td style="white-space:nowrap;"><a href="${appContext}/viewErrorAuditLog.do?pid=${wBean.id}" target="_blank" onclick="popup(this.href);return false;"><c:out value="${wBean.user.actualUserName}"/></a></td>-->
                                                <td style="white-space:nowrap;"><c:out value="${wBean.errorLogTime}"/></td>
                                                <td style="word-break:break-all;"><c:out value="${wBean.errorCause}"/></td>
                                                <td style="width:4%;">
                                                <!-- <a class="aTagButton" href="${appContext}/viewErrorAuditLog.do?eid=${wBean.id}&fd=${miniBean.fromDateStr}&td=${miniBean.toDateStr}&uid=${miniBean.id}">
                                                 Delete
                                                 </a>-->
                                                 <a href="${appContext}/viewErrorAuditLog.do?eid=${wBean.id}&fd=${miniBean.fromDateStr}&td=${miniBean.toDateStr}&uid=${miniBean.id}">
                                                   <img src="images/delete_h.png">
                                                 </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                        </c:if>
                    </table>
                     <div id="result1"></div>
                </td>
            </tr>
            <tr>
                <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
            </tr>
        </table>
        </form:form>


        <script>
           function popup(url,windowname){
                var width  = 1000;
                var height = 800;
                var left   = (screen.width  - width)/2;
                var top    = (screen.height - height)/2;
                var params = 'width='+width+', height='+height;
                params += ', top='+top+', left='+left;
                params += ', directories=no';
                params += ', location=no';
                params += ', menubar=no';
                params += ', resizable=no';
                params += ', scrollbars=yes';
                params += ', status=no';
                params += ', toolbar=no';
                newwin=window.open(url,windowname, params);
                if (window.focus) {newwin.focus()}
                return false;
           }

           function showModal(id){
                    var url="${appContext}/viewErrorAuditLog.do";
                    console.log("pid is "+id);
                    $.ajax({
                       type: "GET",
                       url: url,
                       data: {
                          pid: id
                       },
                       success: function (response) {
                          $('#result1').html(response);
                          $('#errorModal').modal('show');
                       },
                       error: function (e) {
                       }
                    });
           };
        </script>
    </body>
</html>
