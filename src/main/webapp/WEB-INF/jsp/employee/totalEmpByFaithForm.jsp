<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
<head>
<title> <c:out value="${roleBean.staffTypeName}"/> View By Religion  </TITLE>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon"
      type="image/png"
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
<link rel="stylesheet" href="css/screen.css" type="text/css"/>
 <script type="text/JavaScript" src="scripts/jacs.js"></script>
</head>
<body class="main">
	<table class="main" width="70%" border="1" bordercolor="#A52A2A" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>

			<table  width="100%" align="center" border="0" cellpadding="0" cellspacing="0" >

	<tr>
		<td colspan="1">
		<div class="title">
			<c:out value="${roleBean.staffTypeName}"/> Distribution By Religion</div>
		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">

				<form:form modelAttribute="miniBean">
 					<c:set value="${miniBean}" var="dispBean" scope="request"/>
				<table class="register2" cellspacing="1" cellpadding="3">

				<display:table name="dispBean" class="register2" export="false" requestURI="${appContext}/empByRel.do" pagesize="${miniBean.pageSize}">
						<display:caption><c:out value="${roleBean.staffTypeName}"/> Totals By Religion</display:caption>
						<display:setProperty name="export.rtf.filename" value="TotalEmployeeByReligion.rtf"/>
						<display:setProperty name="export.excel.filename" value="TotalEmployeeByReligion.xls"/>
						<display:column property="name" title="Religion" media="html"  href="${appContext}/selectEmpReligionForm.do" paramId="relId" paramProperty="id"></display:column>
						<display:column property="name" title="Religion" media="excel"/>
						<display:column property="totalElements" title="No. Of Employees" ></display:column>
						<display:column property="placeHolder" title="Percentage" ></display:column>
				</display:table>
			</table>
		</form:form>
		<a href="${appContext}/totalEmpByFaithExcel.do">View in Microsoft Excel&copy; </a><br />
		<br/>
		<br/>
		</td>

		<tr>
        			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
        		</tr>
		<!-- Here to put space between this and any following divs -->
	</tr>
	</table>

</body>

</html>