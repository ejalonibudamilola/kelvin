<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
<head>
<title><c:out value="${roleBean.staffTypeName}"/> Overview</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
<link rel="stylesheet" href="css/screen.css" type="text/css"/>
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
<link rel="stylesheet" href="styles/spinner.css" TYPE="text/css" />
</head>

<style>
    #employeeTable thead tr th{
        font-size:8pt !important;
    }
</style>

<body id="result" class="main">
	<div class="loader"></div>
	<form:form modelAttribute="busEmpOVBean">
	<c:set value="${busEmpOVBean}" var="dispBean" scope="request"/>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
			<tr>
				<td colspan="2">
					<div class="title">
					    <c:out value="${roleBean.staffTypeName}" /> List
					</div>
				</td>
			</tr>
			<tr>
				<td valign="top">
				    <div style="margin: 0 1%">
                        <p class="label"><c:out value="${roleBean.staffTypeName}"/></p>
                        <display:table name="dispBean" class="display table" id="employeeTable" export="false" sort="page" defaultsort="1" requestURI="${appContext}/busEmpOverviewForm.do">
                            <display:setProperty name="export.rtf.filename" value="EmployeesList.rtf"/>
                            <display:setProperty name="export.excel.filename" value="EmployeesList.xls"/>
                            <display:column property="employeeId" title="${roleBean.staffTitle}" media="html"  href="${appContext}/employeeOverviewForm.do" paramId="eid" paramProperty="id"></display:column>
                            <display:column  property="employeeId" title="${roleBean.staffTitle}" media="excel"/>
                            <display:column property="displayNameWivTitleNamePrefixed" title="${roleBean.staffTypeName} Name"  ></display:column>
                            <display:column property="currentMdaName" title="${roleBean.mdaTitle}"></display:column>
                            <%--<display:column property="salaryInfo.salaryType.name" title="Pay Group"  ></display:column>--%>
                            <display:column property="payEmployee" title="Pay Group"  ></display:column>
                            <%--<display:column property="salaryInfo.levelStepStr" title="Level & Step" ></display:column>--%>
                            <display:column property="levelAndStep" title="Level & Step" ></display:column>
                            <display:setProperty name="paging.banner.placement" value="" />
                        </display:table>
					</div>
					<table cellspacing="0" cellpadding="4" class="condensedInfoBox">
						
     					<tr class="infoFooter">
							<td colspan=4>
								<a href='${appContext}/busInactiveEmpOverviewForm.do'>Show Terminated <c:out value="${roleBean.staffTypeName}"/>s</a>
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
         window.onload = function exampleFunction() {
                 $(".loader").hide();
         }


         $(function() {

    		$("#employeeTable").DataTable({
    				"order" : [ [ 1, "asc" ] ],
    				//can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
    				//also properties higher up, take precedence over those below
    				"columnDefs":[
    					{"targets": 0, "orderable" : false},
    					{"targets": 4, "searchable" : false }
    					//{"targets": [0, 1], "orderable" : false }
    				]
    		});
    	});
    </script>
</body>
</html>
