<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>

<head>
<title>PFA List </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
<link rel="stylesheet" href="css/screen.css" type="text/css"/>
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
</head>

<body class="main">
<form:form modelAttribute="miniBean">
<%--<c:set value="${miniBean}" var="dispBean" scope="request"/>--%>
<c:set value="${displayList}" var="dispBean" scope="request"/>
<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
	<tr>
		<td>
			<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
				
				<tr>
					<td colspan="2">
					<div class="title">View Pension Fund Administrator List<br>
					</div>
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
					
					<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
						<!--<tr>
							<td class="reportFormControls"> &nbsp;</td>
						</tr>-->
					<tr>
					<display:table name="dispBean" id="pfaTable" class="table display" export="" sort="page" defaultsort="1" requestURI="${appContext}/listPfcPfa.do?pfa=t">
						<display:caption>Pension Fund Administrators</display:caption>
						<display:setProperty name="export.rtf.filename" value="pfaList.rtf"/>
						<display:setProperty name="export.excel.filename" value="pfaList.xls"/>
						<display:column property="name" title="PFA Name" media="html"  href="${appContext}/createEditPfa.do" paramId="oid" paramProperty="id"></display:column>
						<display:column property="name" title="PFA Name" media="excel"/>
						<display:column property="pfcInfo.name" title="PFC" ></display:column>
						<display:column property="address" title="Address" ></display:column>
						
						<display:setProperty name="paging.banner.placement" value="" />
					</display:table>
					</tr>
					<tr>
					
					<td class="buttonRow" align="right">
						<input type="image" name="_cancel" value="cancel" alt="Cancel" src="images/close.png">
					</td>
				</tr>
					</table>
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
        <script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
        <script type="text/javascript">
           $(function() {
              $("#pfaTable").DataTable({
                  "order" : [ [ 1, "asc" ] ]
              });
           });
        </script>
		</body>

	</html>
	
