<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
<head>
<title> Details...</title>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon"
      type="image/png"
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
<link rel="stylesheet" href="css/screen.css" type="text/css"/>
</head>

<body class="main">
    <form:form modelAttribute="miniBean">	
	<c:set value="${miniBean}" var="dispBean" scope="request"/>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		 <%@ include file="/WEB-INF/jsp/headerForModal.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td>
							<div class="navTopBannerAndSignOffLink">
								<span class="navTopSignOff">
									<a href="javascript: self.close ()" title='Close Window' id="topNavSearchButton1">Close Window</a>
								</span>
							</div>
						</td>
					</tr>
					
			        <tr>
				        <td colspan="2">
					        <div class="title"> <c:out value="${roleBean.staffTypeName}"/> list for <c:out value="${miniBean.objectName}" /> </div>
				        </td>
			        </tr>
			        <tr>
				        <td valign="top" class="mainbody" id="mainbody">
				
				<br/>
					<br/>
					     List of <c:out value="${roleBean.staffTypeName}"/>(s).
					<br/>
					<br/>
     				
	
	<table border="0" cellspacing="0" cellpadding="0" width="90%"><tr><td>
			<table class="formtable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
						<tr align="left">
							<td class="activeTH">Employees</td>
						</tr>
						<tr>
							<td class="activeTD">
								<table width="95%" border="0" cellspacing="0" cellpadding="0">
									<display:table name="dispBean" class="register2" export="true" sort="page" defaultsort="1" requestURI="${appContext}/ltgByMDAPEmpDetails.do">
									<display:column property="employeeId" title="${roleBean.staffTitle}" ></display:column>
									<display:column property="name" title="${roleBean.staffTypeName} Name" ></display:column>
									<display:column property="salaryScaleName" title="Pay Group"></display:column>
									<display:column property="salarylevelAndStepStr" title="Level & Step"></display:column>
									<display:column property="basicSalaryStr" title="Basic Salary" media="html"></display:column>
									<display:column property="basicSalaryStrSansNaira" title="Basic Salary" media="excel"></display:column>
									<display:column property="ltgCostStr" title="With LTG Increase" media="html"></display:column>
									<display:column property="ltgCostStrSansNaira" title="With LTG Increase" media="excel"></display:column>
									<display:column property="netIncreaseStr" title="Net Increase" media="html"></display:column>
									<display:column property="netIncreaseStrSansNaira" title="Net Increase" media="excel"></display:column>
									<display:setProperty name="paging.banner.placement" value="bottom" />
									</display:table>
								</table>
						    </td>
						</tr>
								
							
			</table>
			<!-- 
			<tr>
				<td class="buttonRow" align="right" >
					<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
				</td>
			</tr>
			 -->
			
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

</body>
</html>
