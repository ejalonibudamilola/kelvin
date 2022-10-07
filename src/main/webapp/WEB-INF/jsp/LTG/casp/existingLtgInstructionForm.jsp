<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
<head>
<title> Existing LTG Instruction Form</title>
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
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					
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
	<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="agency">
         							 <ul>
            							<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            							</c:forEach>
         								</ul>
     							 </spring:hasBindErrors>
				</div>
			<table class="formtable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
						<tr align="left">
							<td class="activeTH"><c:out value="${roleBean.staffTypeName}"/>s</td>
							
						</tr>
						<tr>
							<td class="activeTD">You have Leave Transport Grant
									Instruction that has not been applied.<br>
									You can either <b>Add&nbsp;</b> or <b>Replace</b> the existing
									Leave Transport Grant Instruction<br>
									using the appropriate buttons or cancel the whole process.<br>
									<br>
									Note** Cancelling the whole process will leave the Old LTG
									Instruction intact,<font color="red"><b><i>but
									the new one will be lost!</i></b></font><br>
									&nbsp;&nbsp;<b>Adding</b> to the existing LTG Instruction will
									add the Old LTG instruction to the new one<br>
									while <b>Replacing</b> will delete the old LTG Instruction and
									replace it with the most recent one.
							 </td>
							</tr>
							<tr class="activeTD">
							 <td>&nbsp;<br></td>
							</tr>
							<tr class="activeTD">
							 	<td>Created By:</td>
							 	<td><c:out value="${miniBean.createdBy}"/></td>
							</tr>
							<tr class="activeTD">
							 	<td>Created Date:</td>
							 	<td><c:out value="${miniBean.createdDateStr}"/></td>
							</tr>
							<tr class="activeTD">
							 	<td>Total Basic Salary:</td>
							 	<td><c:out value="${miniBean.totalBasicSalaryStr}"/></td>
							</tr>
							<tr class="activeTD">
							 	<td>Total Basic Salary (including LTG):</td>
							 	<td><c:out value="${miniBean.totBasicSalaryPlusLtgStr}"/></td>
							</tr>
							<tr class="activeTD">
							 	<td>Net Increase:</td>
							 	<td><c:out value="${miniBean.netIncreaseStr}"/></td>
							</tr>
							<tr>
							<td class="activeTD">
								<table width="95%" border="0" cellspacing="0" cellpadding="0">
									<display:table name="dispBean" class="register3" export="true" sort="page" defaultsort="1" requestURI="${appContext}/existingLtgFound.do">
									<display:column property="name" title="Name" ></display:column>
									<display:column property="noOfEmp" title="No. Of ${roleBean.staffTypeName}s"></display:column>
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

			<tr>
				<td class="buttonRow" align="right" >
					<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
					<input type="image" name="_add" value="cancel" title="Add" class="" src="images/add.png">
					<input type="image" name="_replace" value="cancel" title="Replace" class="" src="images/replace_h.png">
				</td>
			</tr>

			<tr>
				<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
			</tr>
		</table>
	
</form:form>
</body>
</html>
