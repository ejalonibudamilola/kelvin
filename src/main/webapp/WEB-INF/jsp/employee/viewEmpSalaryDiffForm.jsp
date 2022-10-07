<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            <c:out value="${roleBean.staffTypeName}" /> Salary Differences
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="styles/epayroll.css" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
    </head>
    <body class="main">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
    <form:form modelAttribute="miniBean">
	<c:set value="${miniBean.salaryDiffBeanList}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    View <c:out value="${roleBean.staffTypeName}" />s Salary Differences -
                                    By Pay Periods
                                    
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
                                    <tr>
                                        <td class="reportFormControls">
                                            
                                                    <span class="optional"> Between </span> <form:input path="fromDate"/>
													<img src="images/calendar.png" id="fromDateId" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('fromDate'),event);" title="Please Select Valid Start Month e.g (01-08-2020)"><span class="optional">&nbsp;And </span> <form:input path="toDate"/>
													<img src="images/calendar.png" id="toDateId" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('toDate'),event);" title="Please Select Valid End Month e.g (31-08-2020)">&nbsp;&nbsp;Filter By Amount Range <form:input path="startAmountStr" size="8" maxlength="8"/>&nbsp;-&nbsp;<form:input path="endAmountStr" size="8" maxlength="8"/></td>                                           
                                        
                                       
                                    </tr>
                                    <tr>
                                        <td valign="top">
											<p class="label">Employees with salary differences</p>
											<display:table name="dispBean" class="register3" export="false" sort="page" defaultsort="1" requestURI="${appContext}/employeeSalaryDiff.do">
											<display:column property="employeeId" title="Staff ID" media="html"  href="${appContext}/treatSalaryDiff.do" paramId="eid" paramProperty="currPaycheck"></display:column>
											<display:column property="employeeId" title="Staff ID" media="excel rtf"/>
											<display:column property="employeeName" title="Staff Name"></display:column>
											<display:column property="salaryScale" title="Pay Group"></display:column>
										    <display:column property="levelAndStep" title="Level & Step"></display:column>
										    <display:column property="currentSalaryStr" title="${miniBean.fromDateStr} Salary"></display:column>
										    <display:column property="lastSalaryStr" title="${miniBean.toDateStr} Salary"></display:column>
										    <display:column property="salaryDiffStr" title="Salary Difference"></display:column>

										</display:table>
										</td>
                                    </tr>
                                    <tr>
                                    	<td>
                                    	  &nbsp;
                                    	</td>
                                    </tr>
                                    <tr>
                                        
                                     	<td>
                                          <a href='${appContext}/exportEmpWithOpsUpsToExcel.do?cpp=${miniBean.fromDatePrintStr}&ppp=${miniBean.toDatePrintStr}&ph=t' title="View All Records in Excel"><i>Export to Microsoft Excel&copy;</i></a>                                          
                                        </td>
                                    </tr>
                                   
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                        <input type="image" name="_updateReport" value="updateReport" alt="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
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
    </body>
</html>
