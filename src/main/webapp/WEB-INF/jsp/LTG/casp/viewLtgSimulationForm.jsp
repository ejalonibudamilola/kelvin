k,<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            LTG Simulations
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
    </head>
    <body class="main">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
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
                                    View L.T.G Simulations<br>
                                  
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
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                    	<td>&nbsp;</td>
                                        <!--<td class="reportFormControls">
                                            
                                                    <span class="optional"> From Date </span> <form:input path="fromDate"/>
													<img src="images/calendar.png" id="fromDateId" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('fromDate'),event);"><span class="optional">&nbsp;To Date </span> <form:input path="toDate"/>
													<img src="images/calendar.png" id="toDateId" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('toDate'),event);">
                                                                                                
                                        </td>
                                       
                                    --></tr>
                                    <tr>
                                        <td valign="top">
											<p class="label">Leave Transport Grant Simulations</p>
											<display:table name="dispBean" class="register3" export="false" sort="page" defaultsort="1" requestURI="${appContext}/viewLtgSimulations.do">
											<display:setProperty name="export.rtf.filename" value="ltgSimulationsByDate.rtf"/>
											<display:setProperty name="export.excel.filename" value="ltgSimulationsByDate.xls"/>
											<display:column property="name" title="Name" media="html" href="${appContext}/viewSimulatedPayroll.do" paramId="lid" paramProperty="id"></display:column>
											<display:column property="name" title="Name" media="excel"/>
											<display:column property="simulationMonthStr" title="Simulation Date"></display:column>
											<display:column property="noOfMdasAffected" title="No. of MDAs"></display:column>
											<display:column property="simulationStatus" title="Status"></display:column>
											<display:column property="createdBy" title="Created By"></display:column>											
											<display:column property="createdDateStr" title="Created Date"></display:column>
											<display:column property="details" title="Details" media="html" href="${appContext}/viewLtgDetails.do" paramId="lid" paramProperty="id"></display:column>
											<display:column property="delete" title="Delete" media="html" href="${appContext}/viewLtgSimulations.do" paramId="lid" paramProperty="id"></display:column>
						
										</display:table>
										</td>
                                    </tr>
                                    <tr>

                                                                             	<td>
                                                                                	<a href='${appContext}/ltgSimulationReportExcel.do' title="View All Records in Excel"><i>Export to Microsoft Excel&copy;</i></a>

                                                                                </td>
                                    </tr>
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                        <!--<input type="image" name="_updateReport" value="updateReport" alt="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                        --><input type="image" name="_cancel" value="cancel" alt="Cancel" src="images/close.png">
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
    </body>
</html>
