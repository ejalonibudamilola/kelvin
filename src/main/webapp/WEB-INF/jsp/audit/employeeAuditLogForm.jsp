<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            <c:out value="${roleBean.staffTypeName}"/> Log Report
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
       #emptbl thead tr th{
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
                                    View <c:out value="${roleBean.staffTypeName}"/>s Logs<br>
                                    
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
                                        </td>
                                        <td class="activeTD">
                                           <form:input path="fromDate" title="Start Date"/>&nbsp;<img src="images/calendar.png" id="fromDateId" width="16" height="16" border="0" title="Pick from date" onclick="JACS.show(document.getElementById('fromDate'),event);">
                                        	&nbsp;- &nbsp;<form:input path="toDate" title="End Date"/>
													<img src="images/calendar.png" id="toDateId" width="16" height="16" border="0" title="Pick to date" onclick="JACS.show(document.getElementById('toDate'),event);">
                                        
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
                                       <td class="activeTD">
                                            <c:out value="${roleBean.mdaTitle}"/> :&nbsp;
                                        </td>
                                        <td class="activeTD">
                                            <form:select path="mdaInd">
														<form:option value="0">&lt;Select&gt;</form:option>
														<c:forEach items="${mdaList}" var="mList">
														<form:option value="${mList.id}">${mList.name}</form:option>
														</c:forEach>
												</form:select>       
                                        </td>
                                    </tr>
                                    <tr>
                                       <td class="activeTD">
                                            School :&nbsp;                                   
                                        </td>
                                        <td class="activeTD">
                                            <form:select path="schoolId">
														<form:option value="0">&lt;Select&gt;</form:option>
														<c:forEach items="${schoolList}" var="sList">
														<form:option value="${sList.id}">${sList.name}</form:option>
														</c:forEach>
												</form:select>       
                                        </td>
                                    </tr>
                                     <tr>
                                       <td class="activeTD">
                                            Pay Period :&nbsp;                                   
                                        </td>
                                        <td class="activeTD">
                                              <form:select path="runMonth" title="Month">
														 <form:option value="-1">Select Month</form:option>
													       <c:forEach items="${monthList}" var="moList">
													         <form:option value="${moList.id}">${moList.name}</form:option>
													      </c:forEach>
													      </form:select>
													    &nbsp; -  &nbsp; <form:select path="runYear" title="Year">
															<form:option value="0">Select Year</form:option>
														       <c:forEach items="${yearList}" var="yList">
														         <form:option value="${yList.id}">${yList.name}</form:option>
														      </c:forEach>
														      </form:select>
                                        </td>
                                    </tr>
                                    <tr>
                                      
                                        <td class="activeTD">
                                            <c:out value="${roleBean.staffTitle}"/> :&nbsp;
                                        </td>
                                        <td class="activeTD">
                                           	<form:input path="ogNumber" maxlength="12" size="10" title="Enter ${roleBean.staffTypeName} ${roleBean.staffTitle} to filter by ${roleBean.staffTypeName}."/>

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
											<p class="label"><c:out value="${roleBean.staffTypeName}"/> Changes</p>
											<display:table name="dispBean" id="emptbl" class="display table" export="false" sort="page" defaultsort="1" requestURI="${appContext}/viewEmployeeLog.do">
											<display:column property="placeHolder" title="${roleBean.staffTitle}" media="html" ></display:column>
											<display:column property="placeHolder" title="${roleBean.staffTitle}" media="excel"/>
											<display:column property="name" title="Name"></display:column>
											<c:if test="${not miniBean.filteredBySchool}">
													<display:column property="mdaName" title="${roleBean.mdaTitle}"></display:column>
												</c:if>
											<display:column property="oldValue" title="Old Value"></display:column>
										    <display:column property="newValue" title="New Value"></display:column>
										    <display:column property="columnChanged" title="Column Name"></display:column>
										    <display:column property="changedBy" title="Changed By"></display:column>
										    <display:column property="auditTime" title="Changed Date"></display:column>
						                    <display:setProperty name="paging.banner.placement" value="" />
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
											<a href="${appContext}/employeeAuditExcel.do?sDate=${miniBean.fromDateStr}&eDate=${miniBean.toDateStr}&uid=${miniBean.id}&eid=${miniBean.empInstId}&mid=${miniBean.mdaInd}&sid=${miniBean.schoolId}&pp=${miniBean.payPeriodStr}">
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
              $("#emptbl").DataTable({
                 "order" : [ [ 1, "asc" ] ]
              });
              $("#updateReport").click(function(e){
                 $(".spin").show();
              });
           });
        </script>
    </body>
</html>
