<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            Transfer Audit Log Report
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css">
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css">
		<link rel="stylesheet" href="css/screen.css" type="text/css">
		<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
        <link rel="stylesheet" href="styles/spinner.css" TYPE="text/css" />
    </head>
    <style>
       #transfertbl thead tr th{
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
                                    View Transfer Audit Logs<br>
                                    
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
                                <table>
                                    <tr align="left">
										<td class="activeTH" colspan="2">Filter By</td>
										
									</tr>
                                     <tr>
                                      
                                        <td class="activeTD">
                                            Dates :&nbsp;                                   
                                        </td> <td class="activeTD">
                                           <form:input path="fromDate" title="Start Date"/>&nbsp;<img src="images/calendar.png" id="fromDateId" width="16" height="16" border="0" title="Pick from date" onclick="JACS.show(document.getElementById('fromDate'),event);">
                                        	&nbsp;- &nbsp;<form:input path="toDate" title="End Date"/>
													<img src="images/calendar.png" id="toDateId" width="16" height="16" border="0" title="Pick to date" onclick="JACS.show(document.getElementById('toDate'),event);">
                                        
                                        </td>
                                       
                                        
                                    </tr><tr>
                                           <td class="activeTD"> User :&nbsp; </td>
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
									    <td class="activeTD">
                                         <c:out value="${roleBean.staffTitle}" /> :&nbsp;
                                        </td>
                                      <td class="activeTD"> <form:input path="ogNumber" size="10" maxlength="20"/>
                                      </td>
                                    </tr>
                                    <tr>
                                    	<td class="buttonRow" align="right">
 											<input type="image" name="_updateReport" value="updateReport" id="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                    	</td>
                                    </tr>
                                    </table>
                                     <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <td valign="top">
											<p class="label"><c:out value="${roleBean.staffTypeName}" /> Transfers</p>
											<display:table name="dispBean" id="transfertbl" class="display table" export="false" sort="page" defaultsort="1" requestURI="${appContext}/viewTransferLog.do">
											<display:column property="employeeId" title="${roleBean.staffTitle}"></display:column>
											<display:column property="name" title="Name"></display:column>
										    <display:column property="oldMda" title="Transfered From"></display:column>
										    <display:column property="newMda" title="Tranfered To"></display:column>
										    <display:column property="transferDateStr" title="Transfered Date"></display:column>
										    <display:column property="user.actualUserName" title="Transfered By"></display:column>
										</display:table>
										</td>
                                    </tr>
                                    <tr>
                                        <td>&nbsp;</td>
                                        
                                    </tr>
                                    <c:if test="${miniBean.showLink}">
                                    <tr>
                                        
                                     	<td>
                                     	
                                        <a href='${appContext}/exportTransferLog.do?fd=${miniBean.fromDateStr}&td=${miniBean.toDateStr}&uid=${miniBean.id}&eid=${miniBean.empInstId}' title="View All Records in Excel"><i>Export to Microsoft Excel&copy;</i></a>                                          
                                      
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
        <script type="text/javascript">
           $(function() {
              $("#transfertbl").DataTable({
                 "order" : [ [ 1, "asc" ] ]
                 });
              });

              $("#updateReport").click(function(e){
                  $(".spin").show();
              });
        </script>
    </body>
</html>
