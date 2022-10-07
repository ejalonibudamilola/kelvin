<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
<head>
<title>Active <c:out value="${roleBean.staffTypeName}"/> By Remuneration</title>
 <link rel="stylesheet" href="styles/omg.css" type="text/css">
 <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
 <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
 <link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
 <link rel="stylesheet" href="css/screen.css" type="text/css"/>
 <link rel="stylesheet" href="styles/spinner.css" TYPE="text/css" />
 <link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
</head>

<body class="main">
<div class="loader"></div>
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
					<div class="title"><c:out value="${roleBean.staffTypeName}"/> General Information for<br>
					<c:out value="${miniBean.name}"/>
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
                                            Pay Period :&nbsp;                                   
                                        </td>
										<td class="activeTD">	 Month&nbsp;<form:select path="runMonth">
																<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
																<c:forEach items="${monthList}" var="mList">
																<form:option value="${mList.id}">${mList.name}</form:option>
																</c:forEach>
																</form:select>
											&nbsp;&nbsp;
												 Year&nbsp;<form:select path="runYear">
																<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
																<c:forEach items="${yearList}" var="yList">
																<form:option value="${yList.id}">${yList.id}</form:option>
																</c:forEach>
																</form:select>
												                        
                                        </td>
                                       
                                    </tr>
                                     <tr>
                                        <c:if test="${roleBean.pensioner}">
                                           <td class="activeTD">
                                                 Pension Start Date:&nbsp;
                                           </td>
                                        </c:if>
                                        <c:if test="${not roleBean.pensioner}">
                                            <td class="activeTD">
                                                 Service Entry Date:&nbsp;
                                           </td>
                                        </c:if>

                                        <td class="activeTD">
                                           Between :  &nbsp;<form:input path="allowanceStartDate"/>&nbsp;<img src="images/calendar.png" id="fromDateId" width="16" height="16" border="0" title="Pick from date" onclick="JACS.show(document.getElementById('allowanceStartDate'),event);">
                                        	&nbsp;And : &nbsp;<form:input path="allowanceEndDate"/>
													<img src="images/calendar.png" id="toDateId" width="16" height="16" border="0" title="Pick to date" onclick="JACS.show(document.getElementById('allowanceEndDate'),event);">
                                        
                                        </td>
                                       
                                        
                                    </tr>
                                    <tr>
                                       <td class="activeTD">
                                            Bank Type :&nbsp;                                   
                                        </td>
                                        <td class="activeTD">
                                            <form:select path="bankTypeInd">
														<form:option value="0">&lt;All Types&gt;</form:option>
														<form:option value="1" title="Deposit Money Banks i.e.,Polaris Bank,First Bank, GT Bank et al">D.M.B</form:option>
														<form:option value="2" title="Micro-Finance Banks">M.F.B</form:option>
												</form:select>       
                                        </td>
                                    </tr>
									<tr>
                                       <td class="activeTD">
                                            <c:out value="${roleBean.mdaTitle}"/> :&nbsp;
                                        </td>
                                        <td class="activeTD">
                                            <form:select path="mdaInstId">
														<form:option value="0">&lt;Ignore&gt;</form:option>
														<c:forEach items="${mdaList}" var="mList">
														<form:option value="${mList.id}">${mList.name}</form:option>
														</c:forEach>
												</form:select>       
                                        </td>
                                    </tr>
                                     
									<tr>
                                    	<td class="buttonRow" align="right">
 											<input type="image" name="_updateReport" id="updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                    	</td>
                                    </tr>
			          			</table>   
			          			<br/>
			          			<table>
			          			
								<tr>
									<td valign="top">
										<p><b><c:out value="${roleBean.staffTypeName}"/> General Information for <c:out value="${miniBean.name}"/></b></p>
										<p><b>Total <c:out value="${roleBean.staffTypeName}"/> : <c:out value="${statBean.objectInd}"/></b></p>
										<c:choose>
										 <c:when test="${roleBean.pensioner}">
										    <p><b>Total Monthly Pension Paid : <font color="green"><c:out value="${statBean.monthlyPensionStr}"/></font></b></p>
										 </c:when>
										 <c:otherwise>
										     <p><b>Total Monthly Basic Paid : <font color="green"><c:out value="${statBean.monthlyBasicStr}"/></font></b></p>
										 </c:otherwise>
                                        </c:choose>
										<p><b>Total Net Pay Paid       : <font color="green"><c:out value="${statBean.netPayStr}"/></font></b></p>
										<p><b>Total Taxes Paid         : <font color="green"><c:out value="${statBean.currentContributionStr}"/></font></b></p>
										<p><b>Total Gross Pay          : <font color="green"><c:out value="${statBean.yearToDateStr}"/></font></b></p>
								</td>
							</tr>
							</table>
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <td valign="top" class="mainBody" id="mainbody">
                               
					
					
					<display:table name="dispBean" id="renumerationTbl" class="display table" export="false" sort="page" defaultsort="1" requestURI="${appContext}/viewEmployeeRemunerationReport.do">
						<display:setProperty name="paging.banner.placement" value="bottom" />
						<display:column property="employeeId" title="${roleBean.staffTitle}"></display:column>
						<display:column property="displayName" title="${roleBean.staffTypeName} Name"></display:column>
						<display:column property="salaryInfo.levelStepStr" title="Level/Step"></display:column>
						<display:column property="hiringInfo.hireDateStr" title="Hired Date"></display:column>	
						<display:column property="hiringInfo.birthDateStr" title="Birth Date"></display:column>	
						<display:column property="mdaName" title="${roleBean.mdaTitle}"></display:column>
						<display:column property="hiringInfo.taxesPaidStr" title="Taxes Paid"></display:column>
						<display:column property="hiringInfo.terminalGrossPayStr" title="Gross Pay"></display:column>
						<display:column property="hiringInfo.terminatedDateStr" title="Termination Date"></display:column>	
						

						
					</display:table>
					
					
				</td>
			</tr>
			
			
		</table>
           <c:if test="${statBean.objectInd gt 0}">
           			<div class="reportBottomPrintLink">
				<a class="reportLink" href="${appContext}/employeeGeneralInfo.do?rm=${miniBean.runMonth}&ry=${miniBean.runYear}">
				View in Excel </a> <br /><!--&nbsp;|&nbsp; <a class="reportLink" href="${appContext}/employeeGeneralInfoPdf.do?rm=${miniBean.runMonth}&ry=${miniBean.runYear}">
                                           				View in Pdf </a><br />-->
				</div>
			</c:if>
			</td>
		</tr>
		<tr><td>&nbsp;</td></tr>
		<tr>
              <td align="left">
              &nbsp;&nbsp;&nbsp;<input type="image" name="_cancel" value="cancel" title="Cancel" src="images/close.png">
              </td>
        </tr>
		 </table>
                <!-- Modal -->
                <div class="modal fade" id="reportModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                  <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                      <div class="modal-header" style="background-color:green; color:white">
                        <h5 class="modal-title" id="exampleModalLongTitle" style="font-size:16px">Downloading Custom Report....</h5>
                      </div>
                      <div class="modal-body">
                        <p style="color: black; font-size:14px">
                            Please kindly wait while your report downloads.
                            Please close this PopUp when the download completes.
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
<div class="spin"></div>
 </form:form>

  <script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
  <script>
     $(function() {
        $("#renumerationTbl").DataTable({
               "order" : [ [ 1, "asc" ] ],
                //can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
                //also properties higher up, take precedence over those below
               "columnDefs":[
                  {"targets": [0], "orderable" : false}
               ]
        });
     });

     $("#updateReport").click(function(e){
        console.log("I am here");
        $(".spin").show();
     });

     window.onload = function exampleFunction() {
         $(".loader").hide();
     }

     $(".reportLink").click(function(e){
         $('#reportModal').modal({
            backdrop: 'static',
            keyboard: false
         })
     });
  </script>
	
</body>
</html>
