<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
           Retirement Tracker Page
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
       .empDueRetirement thead tr th{
          font-size:8pt !important;
       }
    </style>
    <body class="main">
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
                                   <c:choose>
                                    <c:when test="${roleBean.pensioner}">
                                       View Pensioner By 'I Am Alive' Due Dates<br>
                                    </c:when>
                                    <c:otherwise>
                                       View <c:out value="${roleBean.staffTypeName}"/>&apos;s due for Retirement<br>
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
                                     		<td class="buttonRow" align="right">
                                       		 	<input type="image" name="_updateReport" id="updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                        	</td>
                                   		 </tr>
                                    </table>
                                     <c:choose>
                                    <c:when test="${roleBean.pensioner}">
                                        <tr>
                                        <td valign="top">
											<p class="label"><c:out value="${roleBean.staffTypeName}"/>s</p>
											<display:table name="dispBean" class="display table empDueRetirement" export="false" sort="page" defaultsort="1" requestURI="${appContext}/viewEmpDueForRetirement.do">
											<display:setProperty name="export.excel.filename" value="PensionerByIamAlive.xls"/>
											<display:column property="abstractEmployeeEntity.employeeId" title="${roleBean.staffTitle}" media="html" href="${appContext}/employeeOverviewForm.do" paramId="eid" paramProperty="abstractEmployeeEntity.id"></display:column>
											<display:column property="abstractEmployeeEntity.employeeId" title="${roleBean.staffTitle}" media="excel"/>
											<display:column property="abstractEmployeeEntity.displayName" title="Name"></display:column>
											<display:column property="abstractEmployeeEntity.currentMdaName" title="${roleBean.mdaTitle}"></display:column>
											<display:column property="monthlyPensionStr" title="Monthly Pension"></display:column>
										    <display:column property="yearlyPensionStr" title="Yearly Pension"></display:column>
											<display:column property="gratuityStr" title="Gratuity Balance"></display:column>
											<display:column property="pensionStartDateStr" title="Pension Start Date"></display:column>
											<display:column property="expDateOfRetireStr" title="I Am Alive Due Date"></display:column>
											<display:setProperty name="paging.banner.placement" value="" />

										</display:table>
										</td>
                                    </tr>
                                    </c:when>
                                    <c:otherwise>


                                    <tr>
                                        <td valign="top">
											<p class="label"><c:out value="${roleBean.staffTypeName}"/>s</p>
											<display:table name="dispBean" class="table display empDueRetirement" export="false" sort="page" defaultsort="1" requestURI="${appContext}/viewEmpDueForRetirement.do">
											<display:setProperty name="export.rtf.filename" value="EmployeesDueForRetirement.rtf"/>
											<display:setProperty name="export.excel.filename" value="EmployeesDueForRetirement.xls"/>
											<display:column property="employee.employeeId" title="${roleBean.staffTitle}" media="html" href="${appContext}/employeeOverviewForm.do" paramId="eid" paramProperty="employee.id"></display:column>
											<display:column property="employee.employeeId" title="${roleBean.staffTitle}" media="excel"/>
											<display:column property="employee.firstName" title="Name"></display:column>
											<display:column property="employee.lastName" title="Last Name"></display:column>
											<display:column property="employee.assignedToObject" title="${roleBean.mdaTitle}"></display:column>
											<display:column property="employee.salaryInfo.salaryType.name" title="Pay Group"></display:column>
										    <display:column property="employee.salaryInfo.levelStepStr" title="Level & Step"></display:column>
										    <display:column property="hireDateStr" title="Date of Hire"></display:column>
											<display:column property="birthDateStr" title="Date of Birth"></display:column>
											<display:column property="expDateOfRetireStr" title="Retirement Date(Exp.)"></display:column>
											<display:setProperty name="paging.banner.placement" value="" />
						
										</display:table>
										</td>
                                    </tr>
                                      </c:otherwise>
                                  </c:choose>

                                    <tr>
                                     	<td class="buttonRow" align="right">
                                        <input type="image" name="_cancel" value="cancel" title="Cancel" src="images/close.png">
                                        </td>
                                    </tr>


                            </td>
                        </tr>
                    </table>
                     <div class="reportBottomPrintLink">
                    	 <a id="reportLink" href="${appContext}/retirementTrackerExcelList.do?fd=${miniBean.fromDateStr}&td=${miniBean.toDateStr}">View in Excel </a><br />
                    </div>
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
                         $(".empDueRetirement").DataTable({
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
                          $('#reportModal').modal({
                             backdrop: 'static',
                             keyboard: false
                          });
                       });
        </script>
    </body>
</html>
