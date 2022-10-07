<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
<head>
<title> <c:out value="${roleBean.staffTypeName}"/> Contract Details  </TITLE>
 <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="styles/epayroll.css" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
</head>

<body class="main">
    <form:form modelAttribute="contractBean">	
   
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					
			<tr>
				<td colspan="2">	
					<div class="title"> Contract  for <c:out value="${contractBean.employeeName}"></c:out> </div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">
				
     				<div id="topOfPageBoxedErrorMessage" style="display:${contractBean.displayErrors}">
						<spring:hasBindErrors name="contractBean">
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
					    Contract Created Successfully.
					<br/>
					
     				
     			
					<table class="formtable" border="0" cellspacing="0" cellpadding="3" width="40%" align="left" >
						<tr align="left">
							<td class="activeTH">Employee Contract Details </td>
						</tr>
						<tr>
							<td class="activeTD">
							   <fieldset>
							   <legend><b>Employee Details</b></legend>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
										<td width="25%" align="left"><c:out value="${roleBean.staffTypeName}"/> Name :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${contractBean.employeeName}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left"><c:out value="${roleBean.staffTitle}"/> :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${contractBean.employee.employeeId}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left"><c:out value="${roleBean.mdaTitle}"/> :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${contractBean.employee.parentObjectName}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Date of Birth :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${contractBean.hiringInfo.birthDateStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Hire Date :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${contractBean.hiringInfo.hireDateStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Total Years in Service :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${contractBean.hiringInfo.noOfYearsInService}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Pay Group - Level &amp; Step :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${contractBean.employee.salaryInfo.salaryScaleLevelAndStepStr}"/></td>
									</tr>
									
									</table>						
								</fieldset>
								 <fieldset>
							   <legend><b>Contract Details</b></legend>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
										<td width="25%" align="left">Contract Name :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${contractBean.name}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Start Date :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${contractBean.contractStartDateStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">End Date :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${contractBean.contractEndDateStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Reference Number :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${contractBean.referenceNumber}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Reference Date :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${contractBean.referenceDateStr}"/></td>
									</tr>	
									</table>
													
								</fieldset>
								
								
							</td>
						</tr>
						
						
						 
					</table>
					
					
					<br>
					<br>
					
				</td>
			</tr>
			</table>
			<!--
				<div class="reportBottomPrintLink">
					<a href="${appContext}/contractDetailsExcel.do?chid=${contractBean.id}">Send To Microsoft Excel&copy;</a><br />			
				</div>

				-->
			
			<br>
			<br>
			<br>
			
			</td>
			</tr>
			<tr>
                 <td class="buttonRow" align="right">
                  <input type="image" name="_cancel" value="cancel" title="Close Report" src="images/close.png">
                  <br>
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
