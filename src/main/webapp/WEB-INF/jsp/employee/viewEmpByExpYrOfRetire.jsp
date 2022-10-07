<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            <c:out value="${roleBean.staffTypeName}"/> Expected End Of Service
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
        #expRetirement thead tr th{
            font-size:8pt !important;
        }
    </style>
    <body class="main">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
    <form:form modelAttribute="miniBean">
	<c:set value="${miniBean.employeeList}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
          <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    View <c:out value="${roleBean.staffTypeName}"/> by<br>
                                    <c:choose>
                                    <c:when test="${roleBean.pensioner}">
                                      &apos;I Am Alive&apos; Due Date
                                    </c:when>
                                    <c:otherwise>
                                       Expected Year of Retirement/End Of Service
                                    </c:otherwise>
                                    </c:choose>
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
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="30%">
                                    <tr>
                                        <td class="reportFormControls">
                                            
                                                    <span class="optional"> From </span> <form:input path="fromDate"/>
													<img src="images/calendar.png" id="fromDateId" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('fromDate'),event);"><span class="optional">&nbsp;To </span> <form:input path="toDate"/>
													<img src="images/calendar.png" id="toDateId" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('toDate'),event);">
                                                                                                
                                        </td>
                                       
                                    </tr>
                               </table>
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <td valign="top">
											<p class="label"><c:out value="${roleBean.staffTypeName}"/> by Expected Year of Retirement</p>
											<display:table name="dispBean" id="expRetirement" class="table display" export="false" sort="page" defaultsort="1" requestURI="${appContext}/selectYOR.do">

											<display:column property="employee.employeeId" title="${roleBean.staffTitle}" media="html" href="${appContext}/employeeOverviewForm.do" paramId="eid" paramProperty="employee.id"></display:column>
											<display:column property="employee.employeeId" title="${roleBean.staffTitle}" media="excel"/>
											<display:column property="employee.displayName" title="${roleBean.staffTypeName} Name"></display:column>
											 <display:column property="employee.mdaName" title="${roleBean.mdaTitle}"></display:column>

											 <c:choose>
											    <c:when test = "${roleBean.pensioner}">
											     <display:column property="birthDateStr" title="Birth Date"></display:column>
											    <display:column property="yearlyPensionAmountStr" title="Yearly Pension"></display:column>
											    <display:column property="monthlyPensionAmountStr" title="Monthly Pension"></display:column>
											    <display:column property="pensionStartDateStr" title="Pension Start Date"></display:column>
											    <display:column property="expDateOfRetireStr" title="I Am Alive Due Date"></display:column>
											    </c:when>
											    <c:otherwise>
											    <display:column property="employee.salaryTypeName" title="Pay Group"></display:column>
											    <display:column property="birthDateStr" title="Birth Date"></display:column>
											     <display:column property="hireDateStr" title="Date Hired"></display:column>
											     <display:column property="expDateOfRetireStr" title="Retirement Date"></display:column>
                                               </c:otherwise>
											 </c:choose>



										</display:table>
										</td>
                                    </tr>
                                    <tr>
                                        
                                    </tr>
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                        <input type="image" name="_updateReport" id="updateReport" value="updateReport" alt="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                        <input type="image" name="_cancel" value="cancel" alt="Cancel" src="images/close.png">
                                        </td>
                                    </tr>
                                </table>
                                <div class="reportBottomPrintLink">
                                				<a href="${appContext}/empByExpYrOfRetireReport.do?fd=${miniBean.fromDate}&td=${miniBean.toDate}">
                                				View in Excel </a><br />
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
                               $("#expRetirement").DataTable({
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
