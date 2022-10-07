<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>

<head>
<title><c:out value="${roleBean.staffTypeName}"/> By LGA </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
<link rel="stylesheet" href="css/screen.css" type="text/css"/>
</head>

<body class="main">
<form:form modelAttribute="miniBean">
<c:set value="${miniBean}" var="dispBean" scope="request"/>
<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
	<tr>
		<td>
			<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
				
				<tr>
					<td colspan="2">
					<div class="title">View <c:out value="${roleBean.staffTypeName}"/> By<br>
					Local Government Areas</div>
					</td>
				</tr>
				<tr>
					<td valign="top" class="mainBody" id="mainBody">
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
					
					<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
						<tr>
							<td class="reportFormControls">Select a Local Govt. Area
							   <form:select path="id">
								<form:option value="-1">&lt;make a selection&gt;</form:option>
								<c:forEach items="${LGAList}" var="lList">
									<form:option value="${lList.id}">${lList.name}</form:option>
								</c:forEach>
							</form:select> &nbsp;</td>
						</tr>
					<tr>
					<display:table name="dispBean" class="register3" export="false" sort="page" defaultsort="1" requestURI="${appContext}/selectLGAForm.do">
						<display:caption>Employees view by Local Government Area</display:caption>
						<display:setProperty name="export.rtf.filename" value="EmployeesByLGA.rtf"/>
						<display:setProperty name="export.excel.filename" value="EmployeesByLGA.xls"/>
						<display:column property="employeeId" title="${roleBean.staffTitle}" media="html" href="${appContext}/employeeOverviewForm.do" paramId="eid" paramProperty="id"></display:column>
						<display:column property="lastName" title="Surname" ></display:column>
						<display:column property="firstName" title="Name" ></display:column>						
						<display:column property="initials" title="Mid Name"></display:column>
						<display:column property="assignedToObject" title="${roleBean.mdaTitle}"></display:column>
						<display:column property="salaryTypeName" title="Pay Group"></display:column>
						<display:column property="salaryInfo.levelStepStr" title="Level & Step"></display:column>
						<display:column property="lgaInfo.name" title="LGA"></display:column>
						<display:setProperty name="paging.banner.placement" value="bottom" />
					</display:table>
					<tr>
					
					<td class="buttonRow" align="right">
					<input type="image" name="_updateReport" value="updateReport" alt="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
					<input type="image" name="_cancel" value="cancel" alt="Cancel" src="images/close.png">
					</td>
				</tr>
					</table>
					<a href="${appContext}/totalEmpLgaByMdaExcel.do?rt=1">View in Microsoft Excel By <c:out value="${roleBean.mdaTitle}"/> </a> &nbsp |  &nbsp <a href="${appContext}/totalEmpLgaByMdaExcel.do?rt=2"> View in Microsoft Excel By LGA </a> <br />
					</td>
					</td>
                        <tr>
                                                                                            			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
                                                                                    </tr>
					</tr>

					</table>

			</form:form>
		</body>

	</html>
	
