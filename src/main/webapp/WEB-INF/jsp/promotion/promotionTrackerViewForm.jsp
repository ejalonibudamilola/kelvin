<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
           Promotion Tracker Page
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
    </head>
    <style>
      #empDueForPromotion thead tr th{
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
                                    View <c:out value="${roleBean.staffTypeName}" />s due for Promotion<br>
                                    
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
                                            
                                                    <span class="optional"> Between </span> <form:input path="fromDate"/>
													<img src="images/calendar.png" id="fromDateId" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('fromDate'),event);"><span class="optional">&nbsp;And </span> <form:input path="toDate"/>
													<img src="images/calendar.png" id="toDateId" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('toDate'),event);">&nbsp;&nbsp;
                                                                                                
                                        </td>
                                       
                                    </tr>
                                    <tr>
                                        <td valign="top">
											<p class="label">Employees</p>
											<display:table name="dispBean" id="empDueForPromotion" class="display table" export="false" sort="page" defaultsort="1" requestURI="${appContext}/viewEmpDueForPromotion.do">
											<display:setProperty name="rtf.filename" value="EmployeesDueForPromotion.rtf"/>
											<display:setProperty name="excel.filename" value="EmployeesDueForPromotion.xls"/>
											<display:column property="employee.employeeId" title="${roleBean.staffTitle}" media="html" href="${appContext}/employeeOverviewForm.do" paramId="eid" paramProperty="employee.id"></display:column>
											<display:column property="employee.employeeId" title="${roleBean.staffTitle}" media="excel"/>
											<display:column property="employee.displayName" title="${roleBean.staffTypeName} Name"></display:column>
											<display:column property="employee.assignedToObject" title="${roleBean.mdaTitle}"></display:column>
											<display:column property="employee.salaryInfo.salaryScaleLevelAndStepStr" title="Pay Group"></display:column>
										    <display:column property="lastPromoDateStr" title="Promo. Last"></display:column>
											<display:column property="nextPromoDateStr" title="Promo. Due Date"></display:column>
										</display:table>
										</td>
                                    </tr>
                                     <tr>
                                                                            <td>
                                                                            <br><br>
                                    							            <div class="reportBottomPrintLink">
                                    											<a id="reportLink" href="${appContext}/employeesDueForPromotionExcel.do?fd=${miniBean.fromDateStr}&td=${miniBean.toDateStr}" title='Generate Excel Report'>
                                    											Generate Excel Report </a>
                                    											</div>
                                    											<br>
                                                                            </td>
                                                                        </tr>
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                        <input type="image" name="_updateReport" id="updateReport" value="updateReport" alt="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
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
        <script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
        <script>
                      $(function() {
                         $("#empDueForPromotion").DataTable({
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
