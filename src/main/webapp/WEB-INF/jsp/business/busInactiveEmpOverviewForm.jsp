<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
<head>
<title><c:out value="${roleBean.staffTypeName}"/> Overview</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
<link rel="stylesheet" href="css/screen.css" type="text/css"/>
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
</head>
 <style>
       #busInactiveTbl thead tr th{
         font-size:8pt !important;
       }
 </style>
<body class="main">
	<form:form modelAttribute="busEmpOVBean">
	<c:set value="${busEmpOVBean}" var="dispBean" scope="request"/>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
			<tr>
				<td colspan="2">
					<div class="title">Inactive <c:out value="${roleBean.staffTypeName}"/> List</div>
				</td>
			</tr>
			<tr>
				<td valign="top">
					<p class="label">Inactive Employees</p>
					
					<display:table name="dispBean" id="busInactiveTbl" class="display table" export="true" sort="page" defaultsort="1" requestURI="${appContext}/busInactiveEmpOverviewForm.do">
						<display:setProperty name="export.rtf.filename" value="InactiveEmployeesList.rtf"/>
						<display:setProperty name="export.excel.filename" value="InactiveEmployeesList.xls"/>
						<display:column property="employee.employeeId" title="${roleBean.staffTitle}" media="html"  href="${appContext}/employeeOverviewForm.do" paramId="eid" paramProperty="employee.id"></display:column>
						<display:column property="employee.employeeId" title="${roleBean.staffTitle}" media="excel"/>
						<display:column property="employee.lastName" title="Surname"></display:column>
						<display:column property="employee.firstName" title="Name"></display:column>						
						<display:column property="employee.initials" title="Mid Name"></display:column>
						<display:column property="employee.assignedToObject" title="${roleBean.mdaTitle}"></display:column>
						<display:column property="employee.salaryInfo.salaryType.name" title="Pay Group"></display:column>
						<display:column property="employee.salaryInfo.levelStepStr" title="Level & Step"></display:column>
						<display:column property="terminatedDateStr" title="Termination Date"></display:column>
						<display:column property="terminateReason.name" title="Reason"></display:column>
					</display:table>
					<table cellspacing="0" cellpadding="4" class="condensedInfoBox">
						
     					<tr class="infoFooter">
							<td colspan=4>
								&nbsp; &nbsp; 
								<c:if test="${busEmpOVBean.admin}">
								 <a href='${appContext}/setUpNewEmployee.do'>Add new <c:out value="${roleBean.staffTypeName}"/></a>
								</c:if>
								&nbsp; &nbsp; 
								<a href='${appContext}/busEmpOverviewForm.do'>Show Active <c:out value="${roleBean.staffTypeName}"/></a>
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
    <script type="text/javascript">
       $(function() {
            $("#busInactiveTbl").DataTable({
                 "order" : [ [ 1, "asc" ] ]
            });
       });
    </script>
</body>
</html>
