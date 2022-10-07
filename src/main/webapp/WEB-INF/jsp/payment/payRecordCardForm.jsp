<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
<head>
<title> <c:out value="${roleBean.staffTypeName}"/> Pay Record Card   </TITLE>
 <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="styles/epayroll.css" type="text/css"/>
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
					<div class="title"> Pay Record Card for <c:out value="${miniBean.employeeName}"></c:out> </div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">
				
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
					
				<br/>
					<br/>
					     Pay Record Card Information
					<br/>
					
     				
     			
					<table class="formtable" border="0" cellspacing="0" cellpadding="3" width="40%" align="left" >
						<tr align="left">
							<td class="activeTH">Employee Information </td>
						</tr>
						<tr>
							<td class="activeTD">
							   <fieldset>
							   <legend><b><c:out value="${roleBean.staffTypeName}"/> Details</b></legend>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
										<td width="25%" align="left"><c:out value="${roleBean.staffTypeName}" /> Name :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${miniBean.employeeName}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left"><c:out value="${roleBean.staffTitle}" /> :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${miniBean.empId}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left"><c:out value="${roleBean.mdaTitle}"/> :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${miniBean.organization}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Date of Birth :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${miniBean.birthDateStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Hire Date :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${miniBean.hireDateStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Total Years in Service :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${miniBean.noOfYearsInService}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Last Pay Group - Level &amp; Step :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${miniBean.salaryScaleLevelAndStep}"/></td>
									</tr>
									
									</table>						
								</fieldset>
								
							</td>
						</tr>
						<br>
					</table>
					
					<br>
					<table class="report" cellspacing="0" cellpadding="0">
					<br>
				<tr>
                     <td valign="top">
						<p class="label">Paycheck Details</p>
						<display:table name="dispBean" id="row" class="register4" export="false" sort="page" defaultsort="1" requestURI="${appContext}/generatePRC.do">
							<display:column title="S/No."><c:out value="${row_rowNum}"/></display:column>
							<display:column property="payPeriodStr" title="Pay Period"></display:column>
							<display:column property="salaryInfo.salaryScaleLevelAndStepStr" title="Grade"></display:column>
							<display:column property="salaryInfo.monthlyBasicSalaryStrSansNaira" title="Basic Salary"></display:column>
							<display:column property="rentStr" title="Rent"></display:column>
							<display:column property="transportStr" title="Transport"></display:column>
							<display:column property="mealStr" title="Meal"></display:column>
							<display:column property="utilityStr" title="Academic"></display:column>
							<display:column property="taxesPaidStrSansNaira" title="PAYE"></display:column>
							<display:column property="totalDedStr" title="Other Ded."></display:column>
							<display:column property="netPayStrSansNaira" title="Net Pay"></display:column>	
							<display:setProperty name="paging.banner.placement" value="bottom" />
						
					</display:table>
					</td>
                               </tr>
			 
					</table>
					<br>
					<br>
					<br>
					<br>
					<br>
					<br>
					
				</td>
			</tr>
			</table>
			<c:if test="${miniBean.canSendToExcel}">
				<div class="reportBottomPrintLink">
					<a href="${appContext}/payRecordCardExcel.do?eid=${miniBean.id}">Send To Microsoft Excel&copy;</a><br />			
				</div>
			</c:if>
			<br>
			</td>
			</tr>
			<tr>
				<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
			</tr>
		
		</table>
		
		
</form:form>
</body>
</html>
