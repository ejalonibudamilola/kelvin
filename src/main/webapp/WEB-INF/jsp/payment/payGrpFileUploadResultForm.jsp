<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Pay Group File Upload Summary Page </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="styles/epayroll.css" type="text/css"/>

</head>

<body class="main">
<form:form modelAttribute="miniBean">
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
	<tr>
		<td colspan="2">
		<div class="title">
			<c:out value="${miniBean.displayTitle}"/><br>
			 </div>
		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">
		
			
		
		<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
			
			<tr>
				<td class="reportFormControlsSpacing">&nbsp;</td>
			</tr>
			<tr>
				<td>
				<c:choose>
					<c:when test="${saved}">
						<h3><c:out value="${miniBean.displayErrors}"/>&nbsp;<font color="red"><c:out value="${miniBean.successfulRecords}"/></font>&nbsp;Total Record(s) saved</h3>
					
					</c:when>
					<c:otherwise>
						<h3><c:out value="${miniBean.displayErrors}"/>&nbsp;<font color="red"><c:out value="${miniBean.totalNumberOfRecords}"/></font>&nbsp;Total Record(s) Parsed</h3>
					
					</c:otherwise>
				</c:choose>
				
				<br />
				
				<c:if test="${miniBean.hasPayGrpSaveList}"> 
				<h4><c:out value="${miniBean.salaryType.name}"/>&nbsp;<font color="red"><c:out value="${miniBean.successfulRecords}"/></font>&nbsp;Record(s)</h4>
				<br />
				<table class="report" cellspacing="0" cellpadding="0">
						<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="97px" align="left">Pay Group Name</td>
						<td class="tableCell" valign="top" width="97px" align="left">Level/Step</td>
						<td class="tableCell" valign="top" width="100px" align="left">Monthly Basic</td>
						<td class="tableCell" valign="top" width="100px" align="left">Allowances</td> 	
						<td class="tableCell" valign="top" width="97px" align="left">Details</td>
					</tr>
				</table>
				<div style="overflow:scroll;height:400px;width:100%;overflow:auto">
				<table class="report" cellspacing="0" cellpadding="0">
					<c:forEach items="${miniBean.payGroupSaveList}" var="dedMiniBean">
					<tr class="${dedMiniBean.displayStyle}">
						<td class="tableCell" valign="top" width="97px" align="left"><c:out value="${miniBean.salaryType.name}"/></td>
						<td class="tableCell" valign="top" width="246px" align="left"><c:out value="${dedMiniBean.levelStepStr}"/></td>
						<td class="tableCell" valign="top" width="125px" align="left"><c:out value="${dedMiniBean.monthlyBasicSalaryStr}"/></td>
						<td class="tableCell" valign="top" width="125px" align="left"><c:out value="${dedMiniBean.monthlyConsolidatedAllowanceStr}"/></td>
						<td class="tableCell" valign="top" width="150px" align="left">&nbsp; </td>
					</tr>
					</c:forEach>					
					
				</table>
				</div>
				</c:if>
				<c:if test="${miniBean.hasPayGrpErrorList}">
			    <br>
				<h4><font color="red"><c:out value="${miniBean.failedRecords}"/></font>&nbsp;Error Record(s)</h4>
				<table class="report" cellspacing="0" cellpadding="0">
						<tr class="reportOdd header">
					     
							<td class="tableCell" valign="top" width="97px" align="left">Pay Group Name</td>
							<td class="tableCell" width="125px" valign="top" align="left">Error Details</td>
						 
						 				
					</tr>
				</table>
				<div style="overflow:scroll;height:80px;width:100%;overflow:auto">
				<table class="report" cellspacing="0" cellpadding="0">
					<c:forEach items="${miniBean.payGroupErrorList}" var="dedMiniBean">
					<tr class="${dedMiniBean.displayStyle}">
						<c:if test="${miniBean.deduction}">
							<td class="tableCell" valign="top" width="97px" align="left"><c:out value="${miniBean.salaryType.name}"/></td>
							<td class="tableCell" valign="top" width="125px" align="left"><c:out value="${dedMiniBean.displayErrors}"/></td>
						</c:if>
						 
					</c:forEach>					
					
				</table>
				</div>
				</c:if>
				</td>
			</tr>
 			<tr>
                 <td class="buttonRow" align="right">
                 <c:choose>
                 	<c:when test="${saved}">
                 		<input type="image" name="_cancel" value="cancel" title="Close Report" src="images/close.png">
                 	</c:when>
                 	<c:otherwise>
                 	
                 		<input type="image" name="_create" value="create" title="Create Pay Group Details" src="images/create_h.png">&nbsp;
                 	
                 	
                 	<input type="image" name="_cancel" value="cancel" title="Cancel File Upload" src="images/cancel_h.png">
                 	
                 	</c:otherwise>
                 
                 </c:choose>
                  	
                 </td>
            </tr>
		</table>
		
		<br>
		
		</td>
		<td valign="top" class="navSide"><br>
		<br>
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
</body>

</html>
