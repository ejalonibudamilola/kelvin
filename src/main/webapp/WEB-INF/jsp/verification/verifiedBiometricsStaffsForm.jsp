<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
<head>
<title>Biometrics Verification Report Form</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" type="image/png"  href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
<link rel="stylesheet" href="css/screen.css" type="text/css"/>
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
<link rel="stylesheet" href="styles/spinner.css" TYPE="text/css" />

</head>

<style>
       #active thead tr{
          background-color: white;
       }

       #active thead tr th{
          font-size:8pt !important;
       }
</style>

<body class="main">
<div class="loader"></div>
	<form:form modelAttribute="busEmpOVBean">

	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">


			        <tr>
				        <td colspan="2">
					        <div class="title">Biometrics Verified <c:out value="${roleBean.staffTypeName}"/> List</div>

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
                                        <c:out value="${roleBean.mdaTitle}"/> :&nbsp;
                                    </td>
                                    <td class="activeTD">
                                        &nbsp; <form:select path="mdaInstId">
                                        <form:option value="0">All <c:out value="${roleBean.mdaTitle}"/>s</form:option>
                                            <c:forEach items="${mdaList}" var="moList">
                                                <form:option value="${moList.id}">${moList.name}</form:option>
                                            </c:forEach>
                                        </form:select>

                                    </td>
                                </tr>
                                <tr>
                                    <td class="activeTD">
                                        Verification Status :&nbsp;
                                    </td>
                                    <td class="activeTD">
                                        &nbsp; <form:radiobutton path="statusInd" value="0" />Verified <form:radiobutton path="statusInd" value="1"/> Unverified

                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
					<tr>
                        <td class="buttonRow" align="right">
                             <input type="image" name="_updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                        </td>
                    </tr>
			        <tr>
				        <td valign="top">
					       <p class="label"><c:out value="${busEmpOVBean.displayTitle}"/></p>
                           <div id="" style="margin:1% 1%" class="panel panel-danger">
                             <div class="panel-heading">
                                <p style="font-size:10pt" class="panel-title lead">
                                  <c:out value="${busEmpOVBean.displayTitle}"/>
                                </p>
                             </div>
                             <div class="panel-body">
                                <table id="active" class="hundredPercentWidth table display" cellspacing="0" cellpadding="0">
                                   <thead>
                                       <tr>
                                          <th><c:out value="${roleBean.staffTitle}"/></th>
                                          <th><c:out value="${roleBean.staffTypeName}"/> Name</th>
                                          <th><c:out value="${roleBean.mdaTitle}"/></th>
                                          <th>Pay Group</th>
                                          <c:if test="${ not busEmpOVBean.showingInactive}">
                                            <th>Verified By</th>
                                            <th>Verified Date</th>
                                          </c:if>
                                       </tr>
                                   </thead>
                                   <tbody>
                                       <c:forEach items="${busEmpOVBean.list}" var="empList" varStatus="currIndex">
                                           <tr>
                                               <td><c:out value="${empList.id}" /></td>
                                               <td><c:out value="${empList.displayName}" /></td>
                                               <td><c:out value="${empList.mdaName}" /></td>
                                               <td><c:out value="${empList.salaryTypeName}" /></td>
                                               <c:if test="${ not busEmpOVBean.showingInactive}">
                                                  <td><c:out value="${empList.lastModifier}" /></td>
                                                  <td><c:out value="${empList.lastModTs}" /></td>
                                               </c:if>
                                           </tr>
                                       </c:forEach>
                                   <tbody>
                                </table>
                             </div>
                           </div>

					       <%--  <display:table name="dispBean" id="active" class="display table"  sort="page" defaultsort="1" requestURI="${appContext}/biometricVerified.do">
                                <display:setProperty name="paging.banner.placement" value="" />
                                <display:column property="employeeId" title="${roleBean.staffTitle}" ></display:column>
                                <display:column property="displayName" title="${roleBean.staffTypeName} Name"></display:column>
                                <display:column property="mdaName" title="${roleBean.mdaTitle}"></display:column>
                                <display:column property="salaryTypeName" title="Pay Group"></display:column>

						            <c:if test="${ not busEmpOVBean.showingInactive}">
                                        <display:column property="lastModifier" title="Verified By"> </display:column>
                                        <display:column property="lastModTsForDisplay" title="Verified Date" ></display:column>
						            </c:if>

					       </display:table> --%>
					       <div class="reportBottomPrintLink">
                           	  <a id="reportLink" href="${appContext}/verifiedEmployeeList.do?mid=${busEmpOVBean.mdaInstId}&sind=${busEmpOVBean.statusInd}">View in Excel </a>
                              <br />
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

                    $('#reportModal').modal('show');
                 });


           </script>
</body>
</html>
