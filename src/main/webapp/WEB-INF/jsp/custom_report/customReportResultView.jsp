<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Custom Report Result View Form  </title>
<link rel="stylesheet" href=<c:url value="styles/omg.css"/> type="text/css" />
<link rel="Stylesheet" href=<c:url value="styles/skye.css"/> type="text/css" media ="screen">
<script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
<script src="<c:url value="/scripts/utility_script.js"/>"></script>
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
</head>
 <style>
       #customReporttbl thead tr th{
          font-size:8pt !important;
       }
</style>

<body class="main">
	<table class="main" width="70%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

					<tr>
						<td colspan="2">
								<div class="title">Custom Report Query Results</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						 <br/><br/>
			<form:form modelAttribute="miniBean">
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
				<table border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >

					<tr>
						<td class="activeTD">

                            <table  id="customReporttbl" class="display table"  width="100%"  class="register4" id="">
                            <thead>
                                  <tr>
                                      <c:forEach items="${miniBean.headersList}" var="list">

                                            <th><c:out value="${list.headerName}"/></th>

                                        </c:forEach>
                                    </tr>
                                </thead>
                                <tbody>

                                       <c:forEach items="${miniBean.tableData}" var="dataMap">
                                        <tr>
                                            <c:forEach items="${miniBean.headersList}" var="dataEntries">
                                              <td>${dataMap[dataEntries.headerName]} </td>
                                            </c:forEach>
                                        </tr>
                                        </c:forEach>
                                </tbody>
                            </table>
						</td>
						<c:if test="${miniBean.showLink}">
                                        	    <div class="reportBottomPrintLink">

                                        	      <a id="pdfReportLink" href="${appContext}/genCustomRepXlsOrPdf.do?rt=1">
                                                     View Results in PDF&copy; </a><span>&nbsp;|&nbsp;</span>

                                        		    <a id="xlsReportLink" href="${appContext}/genCustomRepXlsOrPdf.do?rt=2">
                                        		     View Results in Excel&copy; </a><br />
                                        	     </div>
                                             </c:if>
                                             <p/>
					</tr>

					<tr>
						<td class="buttonRow" align="right" >
							  <input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/close.png">
                        </td>
					</tr>
				</table>

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
                            Please kindly wait while your custom report downloads.
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
	<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
            <script type="text/javascript">
               $(function() {
                  $("#customReporttbl").DataTable({
                    "pageLength" : 25
                  });
               });
                $("#pdfReportLink").click(function(e){

                   $('#reportModal').modal('show');
                 });
                   $("#xlsReportLink").click(function(e){

                    $('#reportModal').modal('show');
                  });
    </script>
</body>
</html>
