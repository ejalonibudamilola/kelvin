<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
<head>
<title>Inactive <c:out value="${roleBean.staffTypeName}"/></title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
<link rel="stylesheet" href="styles/epayroll.css" type="text/css"/>
<link rel="stylesheet" href="css/screen.css" type="text/css"/>
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
<link rel="stylesheet" href="styles/spinner.css" TYPE="text/css" />
</head>

<style>
       .inactive thead tr th{
          font-size:8pt !important;
       }
</style>

<body class="main">
<div class="loader"></div>
<script type="text/JavaScript" src="scripts/jacs.js"></script>
	<form:form modelAttribute="busEmpOVBean">
	<c:set value="${busEmpOVBean}" var="dispBean" scope="request"/>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
                    <tr>
                        <td colspan="2">
                            <div class="title">InActive <c:out value="${roleBean.staffTypeName}"/> List</div>
                        </td>
                    </tr>
			        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                <div id="topOfPageBoxedErrorMessage" style="display:${busEmpOVBean.displayErrors}">
									<spring:hasBindErrors name="busEmpOVBean">
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
                                            Date Range :&nbsp;
                                        </td>
                                        <td class="activeTD">
                                           <form:input path="fromDate"/>&nbsp;<img src="images/calendar.png" id="fromDateId" width="16" height="16" border="0" title="Pick from date" onclick="JACS.show(document.getElementById('fromDate'),event);">
                                        	&nbsp;To : &nbsp;<form:input path="toDate"/>
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
                                    		Termination Reason :&nbsp;
                                    	</td>
                                    	<td class="activeTD">
                                    		 <form:select path="terminateReasonId">
                              								   <form:option value="0">All Reason</form:option>
						       									<c:forEach items="${termReasonList}" var="termList">
						     									 <form:option value="${termList.id}">${termList.name}</form:option>
						     									 </c:forEach>
						     						 			</form:select>
                             						 &nbsp;             
                                    	</td>
                                    </tr>
                                    <tr>
                                       <td class="activeTD">
                                            <c:out value="${roleBean.mdaTitle}"/> :&nbsp;
                                        </td>
                                        <td class="activeTD">
                                            <form:select path="mdaInstId">
														<form:option value="0">&lt;Select&gt;</form:option>
														<c:forEach items="${mdaList}" var="mList">
														<form:option value="${mList.id}">${mList.name}</form:option>
														</c:forEach>
												</form:select>       
                                        </td>
                                    </tr>
                                    <c:if test="${not roleBean.pensioner and not roleBean.localGovt}">
                                    <tr>
                                       <td class="activeTD">
                                            School :&nbsp;                                   
                                        </td>
                                        <td class="activeTD">
                                            <form:select path="schoolInstId">
														<form:option value="0">&lt;Select&gt;</form:option>
														<c:forEach items="${schoolList}" var="sList">
														<form:option value="${sList.id}">${sList.name}</form:option>
														</c:forEach>
												</form:select>       
                                        </td>
                                    </tr>
                                    </c:if>
                                     <tr>
                                       <td class="activeTD">
                                            Pay Period :&nbsp;                                   
                                        </td>
                                        <td class="activeTD">
                                           <form:select path="runMonth">
														 <form:option value="-1">Select Month</form:option>
													       <c:forEach items="${monthList}" var="moList">
													         <form:option value="${moList.id}">${moList.name}</form:option>
													      </c:forEach>
													      </form:select>
													    &nbsp;  <form:select path="runYear">
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
                                           	<form:input path="staffId" maxlength="12" size="10" title="Enter ${roleBean.staffTypeName} ${roleBean.staffTitle} to filter by ${roleBean.staffTypeName}."/>

                                        </td>
                                       
                                        
                                    </tr>
									<tr>
                                    	<td class="buttonRow" align="right">
                                        <input type="image" name="_updateReport" id="updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                    	</td>
                                    </tr>
								</table>                                                    
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    <!--<tr>
                                        <td class="reportFormControls">
                                            
                                                    <span class="optional"> Between </span> <form:input path="fromDate"/>
													<img src="images/calendar.png" id="fromDateId" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('fromDate'),event);"><span class="optional">&nbsp;And </span> <form:input path="toDate"/>
													<img src="images/calendar.png" id="toDateId" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('toDate'),event);">&nbsp;&nbsp;
                                                    Termination Reason: <form:select path="terminateReasonId">
                              								   <form:option value="0">All Reason</form:option>
						       									<c:forEach items="${busEmpOVBean.termReasonList}" var="termList">
						     									 <form:option value="${termList.id}">${termList.name}</form:option>
						     									 </c:forEach>
						     						 			</form:select>
                             						 &nbsp;                                             
                                        </td>
                                    </tr>-->
									<tr>
										<td valign="top">
											<p class="label">Inactive <c:out value="${roleBean.staffTypeName}"/></p>
											
											<display:table name="dispBean" export="false" id="inactive" class="display table" sort="page" defaultsort="1" requestURI="${appContext}/inactiveEmpOverview.do">
											    <display:setProperty name="paging.banner.placement" value="" />
												<display:column property="employee.employeeId" title="${roleBean.staffTitle}" media="html" href="${appContext}/generatePRC.do" paramId="eid" paramProperty="employee.id"></display:column>
												<display:column property="employee.employeeId" title="${roleBean.staffTitle}" media="excel"></display:column>
												<display:column property="employee.displayName" title="${roleBean.staffTypeName} Name"></display:column>
												<display:column property="birthDateStr" title="Birth Date" ></display:column>						
												<display:column property="hireDateStr" title="Hired Date"></display:column>
												<display:column property="employee.currentMdaName" title="${roleBean.mdaTitle}"></display:column>
												<display:column property="accountNumber" title="Pay Group"  ></display:column>
												<c:choose>
												<c:when test="${roleBean.pensioner}">
												   <display:column property="pensionStartDateStr" title="Pension Start Date"></display:column>
                                                   <display:column property="pensionEndDateStr" title="Pension End Date"></display:column>
                                                    <display:column property="monthlyPensionStr" title="Monthly Pension"></display:column>
                                                </c:when>
												<c:otherwise>
												   <display:column property="terminatedDateStr" title="Termination Eff. Date"></display:column>
												</c:otherwise>
												</c:choose>

												<display:column property="auditTime" title="Termination Time"></display:column>
												<display:column property="religionStr" title="Reason"></display:column>
												<display:column property="lgaName" title="Terminated By"></display:column>
											</display:table>
										</td>
									</tr>
									<tr>
                                     	<td class="buttonRow" align="right">
                                        <input type="image" name="_cancel" value="cancel" title="Cancel" src="images/close.png">
                                        </td>
                                    </tr>
                                </table>
                                <c:if test="${busEmpOVBean.showLink}">
                                    <div class="reportBottomPrintLink">
                                            <a id="reportLink" href="${appContext}/inactiveEmployeeList.do?fd=${busEmpOVBean.fromDateStr}&td=${busEmpOVBean.toDateStr}&tid=${busEmpOVBean.terminateReasonId}&uid=${busEmpOVBean.id}&eid=${busEmpOVBean.parentId}&mid=${busEmpOVBean.mdaName}&sid=${busEmpOVBean.schoolInstId}&pp=${busEmpOVBean.payPeriodStr}">View in Excel </a><br />
                                    </div>
                                </c:if>

                                <!-- Modal -->
                                <div class="modal fade" id="reportModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                                  <div class="modal-dialog modal-dialog-centered" role="document">
                                    <div class="modal-content">
                                      <div class="modal-header" style="background-color:green; color:white">
                                        <h4 class="modal-title" id="exampleModalLongTitle" style="font-size:16px">Downloading Report....</h4>
                                      </div>
                                      <div class="modal-body">
                                        <p style="color: black; font-size:14px">
                                            Please kindly wait as the report is trying to download.
                                            After Download, you can use the Close button to close this modal
                                        </p>
                                      </div>
                                      <div class="modal-footer">
                                        <button style="background-color:red; color:white" type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                                      </div>
                                    </div>
                                  </div>
                                </div>
			                </td>
		            </tr>
		            <tr>
			            <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		            </tr>
	            </table>
	        </td>
	    </tr>
	</table>
	</form:form>
	<div class="spin"></div>
	<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
	<script>
                $(function() {
                    $("#inactive").DataTable({
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
                    })
                });


                /*$("#reportLink").click(function(e){
                      $(".spin").show();
                         checkReportStatus();
                });
                function checkReportStatus(){
                                       var url="${appContext}/reportRedirect.do";
                                       let id = 2;
                                       console.log("eid is "+id);
                                       $.ajax({
                                          type: "GET",
                                          url: url,
                                          success: function (response) {
                                             console.log("success");
                                             $(".l,spin").hide();
                                          },
                                          error: function (e) {
                                             alert('Error: ' + e);
                                          }
                                       });
                };*/
     </script>
</body>
</html>
