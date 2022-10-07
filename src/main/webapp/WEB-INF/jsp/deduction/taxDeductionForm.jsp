<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
           <c:out value="${roleBean.staffTypeName}"/> Tax Deduction Page
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
       .taxDeduction thead tr th{
          font-size:8pt !important;
       }
    </style>
    <body class="main">
    <div class="loader"></div>
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
    <form:form modelAttribute="miniBean">
	<c:set value="${miniBean}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <%@ include file="/WEB-INF/jsp/modal.jsp" %>

            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    <c:out value="${roleBean.staffTypeName}"/> Tax Deductions<br>
                                    
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
                                           From :  &nbsp;<form:input path="fromDate"/>&nbsp;<img src="images/calendar.png" id="fromDateId" width="16" height="16" border="0" title="Pick from date" onclick="JACS.show(document.getElementById('fromDate'),event);">
                                        	&nbsp;To : &nbsp;<form:input path="toDate"/>
													<img src="images/calendar.png" id="toDateId" width="16" height="16" border="0" title="Pick to date" onclick="JACS.show(document.getElementById('toDate'),event);">
                                        
                                        </td>
                                       
                                        
                                    </tr>
                                    
                                     
                                    <tr>
                                       <td class="activeTD">
                                            M.D.A :&nbsp;                                   
                                        </td>
                                        <td class="activeTD">
                                            <form:select path="mdaInd">
														<form:option value="0">&lt;All MDAs&gt;</form:option>
														<c:forEach items="${agencyList}" var="uList">
														<form:option value="${uList.id}" title = "${uList.codeName}">${uList.name}</form:option>
														</c:forEach>
												</form:select>       
                                        </td>
                                    </tr>
                                     <tr>
                                      
                                        <td class="activeTD">
                                            <c:out value="${roleBean.staffTitle}"/> :&nbsp;
                                        </td>
                                        <td class="activeTD">
                                           	<form:input path="empId" maxlength="12" size="10" title="Enter ${roleBean.staffTypeName} ${roleBean.staffTitle} to filter by ${roleBean.staffTypeName}."/>
                                        </td>
                                       
                                        
                                    </tr>
									<tr>
                                    	<td class="buttonRow" align="right">
 											<input type="image" name="_updateReport" id="updateReport" value="updateReport" alt="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                    	</td>
                                    </tr>
								</table>                                                            
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    
                                    <tr>
                                    	<td>
                                    		<br>
                                    	</td>
                                    </tr>
                                    <tr>
 	                                  	<td class = "">           
                                    	</td>                                   	
                                    </tr>
                                    <c:choose>
                                    <c:when test="${miniBean.captchaError}">
                                    	  
	                                    <tr>
	                                        <td valign="top">
												<p class="label"><c:out value="${roleBean.staffTypeName}"/> Tax Deductions</p>
												<display:table name="dispBean"  class="display table taxDeduction" id="currentRowObject" export="false" sort="page" defaultsort="1" requestURI="${appContext}/taxReport.do">
												<display:column property="employeeId" title="${roleBean.staffTitle}" media="html" href="${appContext}/taxDeductionHistory.do" paramId="eid" paramProperty="yearsMonthsAndIdStr" ></display:column>
												<display:column property="name" title="Employee Name"></display:column>
											    <display:column property="payPeriodStr" title="PayPeriod"></display:column>
												<display:column property="currentDeductionStr" title="Amount Deducted"></display:column>
												<display:setProperty name="paging.banner.placement" value="" />
											</display:table>
											</td>
	                                    </tr>
                                    </c:when>
                                    <c:otherwise>
                                      
	                                    <tr>
	                                        <td valign="top">
												<p class="label"><c:out value="${roleBean.staffTypeName}"/> Tax Deductions</p>
												<display:table name="dispBean" class="display table taxDeduction" id="currentRowObject" export="false" sort="page" defaultsort="1" requestURI="${appContext}/taxReport.do">
												<display:column property="employeeId" title="${roleBean.staffTitle}" media="html" href="${appContext}/taxDeductionHistory.do" paramId="eid" paramProperty="yearsMonthsAndIdStr" ></display:column>
												<display:column property="name" title="Employee Name"></display:column>
												<display:column property="currentDeductionStr" title="Amount Deducted"></display:column>
												<display:column property="mdaName" title="${roleBean.mdaTitle}"></display:column>
												<display:setProperty name="paging.banner.placement" value="" />
											</display:table>
											</td>
	                                    </tr>
                                    </c:otherwise>
                                    </c:choose>
                                  
                                    <tr>
                                        
                                    </tr>
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                         <input type="image" name="_cancel" value="cancel" alt="Cancel" src="images/close.png">
                                        </td>
                                    </tr>
                                </table>
                                <br><!--
                                <a href="${appContext}/taxByAgency.do?sDate=${miniBean.fromDateStr}&eDate=${miniBean.toDateStr}&mid=${miniBean.mdaInd}&eid=${miniBean.id}">
								View in PDF </a>&nbsp;<span class="tabseparator">|</span>&nbsp;
                                --><a id="reportLink" href="${appContext}/taxByAgencyExcel.do?sDate=${miniBean.fromDateStr}&eDate=${miniBean.toDateStr}&mid=${miniBean.mdaInd}&eid=${miniBean.id}">
								View in Excel </a>
                                <br>
								<br>
                                
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
                $(".taxDeduction").DataTable({
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
