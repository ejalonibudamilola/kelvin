<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ page buffer = "16kb" %>
<html>
    <head>
        <title>
            <c:out value="${roleBean.staffTypeName}"/> HR Report
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
           #yearOfSer thead tr th{
              font-size:8pt !important;
           }
    </style>
    <body class="main">
    
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
                                    View <c:out value="${roleBean.staffTypeName}" />s by<br>
                                    Year(s) of Service 
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
                                        <td class="reportFormControls">
                                            Between: <form:select path="fromYear">
                                            			<form:option value="0">&lt;Select&gt;</form:option>
														<c:forEach items="${yearsList}" var="yList">
														<form:option value="${yList.id}">${yList.id}</form:option>
													  </c:forEach>
													</form:select>
													&nbsp;&nbsp;&nbsp; 
											And: <form:select path="toYear">
														<form:option value="0">&lt;Select&gt;</form:option>
														<c:forEach items="${yearsList}" var="yList">
														<form:option value="${yList.id}">${yList.id}</form:option>
														</c:forEach>
													</form:select>
													&nbsp;&nbsp;&nbsp; Years of Service    
										</td>
                                       
                                    </tr>
                                    <tr>
                                        <td valign="top">
											<p class="label">Employees</p>
											<display:table name="dispBean" id="yearOfSer" class="table display" export="false" sort="page" defaultsort="1" requestURI="${appContext}/employeeYOSForm.do">
											<display:setProperty name="export.rtf.filename" value="EmployeesByYrsOfServiceList.rtf"/>
											<display:setProperty name="export.excel.filename" value="EmployeesByYrsOfServiceList.xls"/>
											<display:column property="employee.employeeId" title="${roleBean.staffTitle}" media="html" href="${appContext}/employeeOverviewForm.do" paramId="eid" paramProperty="employee.id"></display:column>
											<display:column property="employee.employeeId" title="${roleBean.staffTitle}" media="excel"/>
											<display:column property="employee.lastName" title="Surname"></display:column>
											<display:column property="employee.firstName" title="First Name"></display:column>											
											<display:column property="employee.initials" title="Mid. Name"></display:column>
											<display:column property="employee.assignedToObject" title="${roleBean.mdaTitle}"></display:column>
											<display:column property="employee.salaryTypeName" title="Pay Group"></display:column>
										    <display:column property="employee.salaryInfo.levelStepStr" title="Level & Step"></display:column>
										    <display:column property="hireDateStr" title="Date of Hire"></display:column>
										    <display:column property="noOfYearsInService" title="Yrs in Service"></display:column>
										    <display:setProperty name="paging.banner.placement" value="" />
										</display:table>
										</td>
                                    </tr>
                                    <tr>
                                        
                                    </tr>
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                        <input type="image" id ="updateReport" name="_updateReport" value="updateReport" alt="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                        <input type="image" name="_cancel" value="cancel" alt="Cancel" src="images/close.png">
                                        </td>
                                    </tr>
                                </table>
                                       <div class="reportBottomPrintLink">
                                                                                                				<a href="${appContext}/empByYearOfServiceReport.do?fy=${miniBean.fromYear}&ty=${miniBean.toYear}&rt=1">
                                                                                                				View By Years Of Service In Excel </a>&nbsp | &nbsp <a href="${appContext}/empByYearOfServiceReport.do?fy=${miniBean.fromYear}&ty=${miniBean.toYear}&rt=2">
                                                                                                                                                                                                                                                				View By <c:out value="${roleBean.mdaTitle}" /> in Excel </a><br />
                                                                                                				</div>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
                 
            </tr>
        </table>
        <div class="spin"></div>
        </form:form>
        <script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
        <script>
           $(function() {
              $("#yearOfSer").DataTable({
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
        </script>
    </body>
</html>
