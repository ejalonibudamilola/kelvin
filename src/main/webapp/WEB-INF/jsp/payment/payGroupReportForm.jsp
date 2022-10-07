<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
<head>
<title>Employee By Pay Group</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
<link rel="stylesheet" href="css/screen.css" type="text/css"/>
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
<link rel="stylesheet" href="styles/spinner.css" TYPE="text/css" />
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.4.4.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>
</head>

<style>
   #payGroup thead tr th{
      font-size:8pt !important;
   }
</style>

<body class="main">
    <div class="loader"></div>
	<form:form modelAttribute="miniBean">
	<c:set value="${miniBean}" var="dispBean" scope="request"/>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					
			<tr>
				<td colspan="2">
					<div class="title"><c:out value="${roleBean.staffTypeName}" /> By Pay Group List</div>
				</td>
			</tr>
			<tr>
					<td valign="top" class="mainBody" id="mainBody">
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
					
					<table class="reportMain" cellpadding="0" cellspacing="0" width="90%">
						<tr>
							<td class="reportFormControls"><font color='green'>Select a Pay Group
							   <form:select path="id">
								<form:option value="0">&lt;make a selection&gt;</form:option>
								<c:forEach items="${salaryTypeList}" var="ssList">
									<form:option value="${ssList.id}">${ssList.name}</form:option>
								</c:forEach>
							</form:select>&nbsp;&nbsp;From Level&nbsp;:
								<form:select path="fromLevel">
									 <form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
									 <c:forEach items="${fromLevelList}" var="fList">
									 <form:option value="${fList.id}">${fList.id}</form:option>
									 </c:forEach>
								</form:select>&nbsp;&nbsp;To Level&nbsp;:
								 <form:select path="toLevel">
									 <form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
									 <c:forEach items="${fromLevelList}" var="tList">
									 <form:option value="${tList.id}">${tList.id}</form:option>
									 </c:forEach>
								</form:select>
								</font>
							</td>
						</tr>
                        <tr style="${miniBean.showRow}">
                            <td style="padding-top:1%">
                                <div>
                                    <input type="image" name="_updateReport" value="updateReport" id="updateReport" alt="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                    <p style="text-align:center">Staffs on <font color="blue"><c:out value="${miniBean.salaryStructureName}"/></font> Pay Group</p>
                                </div>
                            </td>
                            <display:table name="dispBean" id="payGroup" class="display table" export="false" sort="page" defaultsort="1" requestURI="${appContext}/viewPayGroupEmployees.do">
                                <display:column property="employeeId" title="${roleBean.staffTitle}" media="html"  href="${appContext}/employeeOverviewForm.do" paramId="eid" paramProperty="id"></display:column>
                                <display:column property="lastName" title="Surname" ></display:column>
                                <display:column property="firstName" title="First Name" ></display:column>
                                <display:column property="initials" title="Middle Name"></display:column>
                                <display:column property="mdaDeptMap.mdaInfo.name" title="${roleBean.mdaTitle}"></display:column>
                                <display:column property="salaryInfo.levelStepStr" title="Level & Step"></display:column>
                                <display:setProperty name="paging.banner.placement" value="" />
                            </display:table>
                        </tr>
                        <tr style="${miniBean.showRow}">
                            <td>
                                <div class="reportBottomPrintLink">
                                    <a href="${appContext}/payGroupByLevelExcel.do?stid=${miniBean.id}&fl=${miniBean.fromLevel}&tl=${miniBean.toLevel}" title='View/Export to Microsoft Excel'>
                                        View in Microsoft Excel&copy;
                                    </a><br />
                                    <input style="padding-top:1%" type="image" name="_cancel" value="cancel" alt="Cancel" src="images/close.png">
                                </div>
                            </td>
                        </tr>
                        <c:if test = "${miniBean.showRow == 'display:none'}">
                            <tr>
                                <td class="buttonRow" align="right">
                                    <input type="image" name="_updateReport" value="updateReport" id="updateReport" alt="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                    <input type="image" name="_cancel" value="cancel" alt="Cancel" src="images/close.png">
                                </td>
                            </tr>
                        </c:if>
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
                   $("#payGroup").DataTable({
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
    </script>
</body>
</html>
