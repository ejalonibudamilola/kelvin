<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
<head>
<title>Active <c:out value="${roleBean.staffTypeName}"/></title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" type="image/png"  href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
<link rel="stylesheet" href="css/screen.css" type="text/css"/>
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
<link rel="stylesheet" href="styles/spinner.css" TYPE="text/css" />

</head>

<style>
       #active thead tr th{
          font-size:8pt !important;
       }
</style>

<body class="main">
<div class="loader"></div>
	<form:form modelAttribute="busEmpOVBean">
	<c:set value="${busEmpOVBean}" var="dispBean" scope="request"/>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					<!--<tr>
						<td>
							<div class="navTopBannerAndSignOffLink">
								<span class="navTopSignOff">
									&nbsp;&nbsp;&nbsp;<a href="javascript: self.close ()" title='Close Window' id="topNavSearchButton1">Close Window</a>
								</span>
							</div>
						</td>
					</tr>-->
					
			        <tr>
				        <td colspan="2">
					        <div class="title">Active <c:out value="${roleBean.staffTypeName}"/> List</div>

				        </td>
			        </tr>

                    <tr>
                        <td>
                            <table width="60%" border="0" cellpadding="0" cellspacing="0">
                                <tr align="left">
                                	<td class="activeTH" colspan="2">Filter By</td>
                                </tr>
                                <tr>
                                    <td class="activeTD">
                                        Active By :&nbsp;
                                    </td>
                                    <td class="activeTD">
                                         Month : &nbsp; <form:select path="runMonth">
                                        <form:option value="-1">Select Month</form:option>
                                            <c:forEach items="${monthList}" var="moList">
                                                <form:option value="${moList.id}">${moList.name}</form:option>
                                            </c:forEach>
                                        </form:select>
                                        &nbsp;  Year : &nbsp; <form:select path="runYear">
                                        <form:option value="0">Select Year</form:option>
                                            <c:forEach items="${yearList}" var="yList">
                                                <form:option value="${yList.id}">${yList.name}</form:option>
                                            </c:forEach>
                                        </form:select>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
					<tr>
                        <td class="buttonRow" align="right">
                             <input type="image" name="_updateReport" value="updateReport" alt="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                        </td>
                    </tr>
			        <tr>
				        <td valign="top">
					       <p class="label"><c:out value="${roleBean.staffTypeName}"/>s</p>

					       <display:table name="dispBean" id="active" class="display table"  sort="page" defaultsort="1" requestURI="${appContext}/activeEmpOverview.do">
                                <display:setProperty name="paging.banner.placement" value="" />
                                <display:column property="employeeId" title="${roleBean.staffTitle}" ></display:column>
                                <display:column property="name" title="${roleBean.staffTypeName} Name"></display:column>
                                <display:column property="proposedMda" title="${roleBean.mdaTitle}"></display:column>

						        <c:choose>
						            <c:when test="${roleBean.pensioner}">
                                        <display:column property="yearlyPensionStr" title="Yearly Pension" style="text-align:right;"></display:column>
                                        <display:column property="monthlyPensionStr" title="Monthly Pension" style="text-align:right;"></display:column>
                                        <display:column property="terminatedDateAsStr" title="Service End Date" style="text-align:right;"></display:column>
                                        <display:column property="pensionStartDateStr" title="Pension Start Date" style="text-align:right;"> </display:column>
                                        <display:column property="yearsOnPension" title="Years On Pension" style="text-align:right;"></display:column>
						            </c:when>
						            <c:otherwise>
						               <display:column property="tin" title="TIN" ></display:column>
						                <display:column property="oldLevelAndStep" title="Pay Group"></display:column>
                                        <display:column property="yearlyPensionStr" title="Yearly Gross" style="text-align:right;"></display:column>
                                        <display:column property="monthlyPensionStr" title="Monthly Gross" style="text-align:right;"></display:column>
                                        <display:column property="hireDateStr" title="Hired Date" style="text-align:right;"></display:column>
                                        <display:column property="birthDateStr" title="Birth Date" style="text-align:right;"></display:column>
                                        <display:column property="expDateOfRetireStr" title="Expected Retirement Date" style="text-align:right;"></display:column>
						            </c:otherwise>
						        </c:choose>
					       </display:table>
					       <div class="reportBottomPrintLink">
                           	  <a id="reportLink" href="${appContext}/activeEmployeeList.do?rm=${busEmpOVBean.runMonth}&ry=${busEmpOVBean.runYear}">View in Excel </a>
                           	  &nbsp;|&nbsp;<a href="${appContext}/nominalRollList.do?rm=${busEmpOVBean.runMonth}&ry=${busEmpOVBean.runYear}">
                              Nominal Roll </a><br />
                           </div>
				        </td>
			        </tr>
		        </table>
                <!-- Modal -->
                <div class="modal fade" id="reportModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                  <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                      <div class="modal-header" style="background-color:green; color:white">
                        <h5 class="modal-title" id="exampleModalLongTitle" style="font-size:16px">Downloading Report....</h5>
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
	</form:form>
	<div class="spin"></div>
	<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
    <script>

                        $(function() {
                            $("#active").DataTable({
                                        "order" : [ [ 1, "asc" ] ],
                                        //can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
                                        //also properties higher up, take precedence over those below
                                        "columnDefs":[
                                           {"targets": [0], "orderable" : false}
                                        ]
                            });
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

           </script>
</body>
</html>
