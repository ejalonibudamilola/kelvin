<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
<head>
<title> Department Details Form </TITLE>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="<c:url value="/styles/epayroll.css"/>" type="text/css"/>
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
<link rel="stylesheet" href="css/screen.css" type="text/css"/>
</head>

<body class="main">
    
	<table class="main" width="70%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>

			<table  width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			<tr>
						<td>
							<div class="navTopBannerAndSignOffLink">
								<span class="navTopSignOff">
									&nbsp;&nbsp;&nbsp;<a href="javascript: self.close ()" title='Close Window' id="topNavSearchButton1">Close Window</a>
								</span>
							</div>
						</td>
			</tr>
			
	<tr>
		<td colspan="2">
		<div class="title">
			Departments List</div>
		</td>
	</tr>
	<tr>
		<td valign="top">
		<form:form modelAttribute="miniBean">
		 <c:set value="${miniBean}" var="dispBean" scope="request"/>			
				<!--<table class="register3" cellspacing="1" cellpadding="3">
					<tr>
						<td>
								--><table width="95%" border="0" cellspacing="0" cellpadding="0">
									<display:table name="dispBean" class="register2" sort="page" defaultsort="1" requestURI="${appContext}/departmentOverview.do">
										<display:column property="department.name" title="Department" ></display:column>
										<display:column property="director" title="Director"></display:column>
										<display:column property="mdaInfo.name" title="Ministry"></display:column>
										<display:column property="mdaInfo.noOfDept" title="No. of Employees"></display:column>
									</display:table>
								</table><!--
						</td>
					</tr>
				</table>
			
		 --></form:form>
		</td>
			
	</tr>
	</table>

</body>

</html>

