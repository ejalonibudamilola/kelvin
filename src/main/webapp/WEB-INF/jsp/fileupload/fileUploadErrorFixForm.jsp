<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> File Upload Error Page </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="styles/epayroll.css" type="text/css"/>

</head>

<body class="main">
<form:form modelAttribute="failedUploadBean">
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
	<tr>
		<td colspan="2">
		<div class="title">
			<c:out value="${failedUploadBean.displayTitle}"/><br>
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
				<h3><c:out value="${failedUploadBean.displayErrors}"/></h3>
				<br />
				
				<c:if test="${failedUploadBean.deduction}">
				<h4><c:out value="${failedUploadBean.name}"/></h4>
				<br />
				<table class="report" cellspacing="0" cellpadding="0">
						<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="97px" align="left"><c:out value="${roleBean.staffTypeName}"/></td>
						<td class="tableCell" width="97px" valign="top" align="left">Deduction Code</td>
						<td class="tableCell" width="97px" valign="top" align="left">Deduction Amount</td>
						
											
					</tr>
				</table>
				<div style="overflow:scroll;height:400px;width:100%;overflow:auto">
				<table class="report" cellspacing="0" cellpadding="0">
					<c:forEach items="${failedUploadBean.errorList}" var="dedMiniBean" varStatus="gridRow">
					<tr class="${dedMiniBean.displayStyle}">
						<td class="tableCell" valign="top" width="97px" align="left">
							<spring:bind path="failedUploadBean.errorList[${gridRow.index}].staffId">
							<input type="text" name="<c:out value="${status.expression}"/>"
    							id="<c:out value="${status.expression}"/>"
   								value="<c:out value="${status.value}"/>" size="6" maxlength="8"/>
							</spring:bind>
						 </td>
						<td class="tableCell" valign="top" width="97px" align="left">
							<spring:bind path="failedUploadBean.errorList[${gridRow.index}].objectCode">
							<input type="text" name="<c:out value="${status.expression}"/>"
    							id="<c:out value="${status.expression}"/>"
   								value="<c:out value="${status.value}"/>" size="6" maxlength="8"/>
							</spring:bind>
						 </td>
						<td class="tableCell" valign="top" width="97px" align="left">
							<spring:bind path="failedUploadBean.errorList[${gridRow.index}].deductionAmount">
							<input type="text" name="<c:out value="${status.expression}"/>"
    							id="<c:out value="${status.expression}"/>"
   								value="<c:out value="${status.value}"/>" size="6" maxlength="8"/>
							</spring:bind>
						 </td>
					</tr>
					</c:forEach>					
					
				</table>
				</div>
				</c:if>
				 
				<c:if test="${failedUploadBean.loan}">
				<h4><c:out value="${failedUploadBean.name}"/></h4>
				<br />
				<table class="report" cellspacing="0" cellpadding="0">
						<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="97px" align="left"><c:out value="${roleBean.staffTypeName}"/></td>
						<td class="tableCell" width="97px" valign="top" align="left">Loan Code</td>
						<td class="tableCell" width="97px" valign="top" align="left">Loan Amount</td>
						<td class="tableCell" width="97px" valign="top" align="left">Tenor</td>						
					</tr>
				</table>
				<div style="overflow:scroll;height:400px;width:100%;overflow:auto">
				<table class="report" cellspacing="0" cellpadding="0">
					<c:forEach items="${failedUploadBean.errorList}" var="dedMiniBean" varStatus="gridRow">
					<tr class="${dedMiniBean.displayStyle}">
						<td class="tableCell" valign="top" width="97px" align="left">
							<spring:bind path="failedUploadBean.errorList[${gridRow.index}].staffId">
							<input type="text" name="<c:out value="${status.expression}"/>"
    							id="<c:out value="${status.expression}"/>"
   								value="<c:out value="${status.value}"/>" size="6" maxlength="8"/>
							</spring:bind>
						 </td>
						<td class="tableCell" valign="top" width="97px" align="left">
							<spring:bind path="failedUploadBean.errorList[${gridRow.index}].objectCode">
							<input type="text" name="<c:out value="${status.expression}"/>"
    							id="<c:out value="${status.expression}"/>"
   								value="<c:out value="${status.value}"/>" size="6" maxlength="8"/>
							</spring:bind>
						 </td>
						<td class="tableCell" valign="top" width="97px" align="left">
							<spring:bind path="failedUploadBean.errorList[${gridRow.index}].loanBalance">
							<input type="text" name="<c:out value="${status.expression}"/>"
    							id="<c:out value="${status.expression}"/>"
   								value="<c:out value="${status.value}"/>" size="6" maxlength="8"/>
							</spring:bind>
						 </td>
						 <td class="tableCell" valign="top" width="97px" align="left">
							<spring:bind path="failedUploadBean.errorList[${gridRow.index}].tenor">
							<input type="text" name="<c:out value="${status.expression}"/>"
    							id="<c:out value="${status.expression}"/>"
   								value="<c:out value="${status.value}"/>" size="3" maxlength="2"/>
							</spring:bind>
						 </td>
					</tr>
					</c:forEach>					
					
				</table>
				</div>
				</c:if>
				 
				
				 
				<c:if test="${failedUploadBean.specialAllowance}">
				<h4><c:out value="${failedUploadBean.name}"/></h4>
				<br />
				<table class="report" cellspacing="0" cellpadding="0">
						<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="97px" align="left"><c:out value="${roleBean.staffTypeName}"/></td>
						<td class="tableCell" width="97px" valign="top" align="left">Special Allowance</td>
						<td class="tableCell" width="97px" valign="top" align="left">Start Month</td>
						<td class="tableCell" width="97px" valign="top" align="left">Start Year</td> 
						<td class="tableCell" width="97px" valign="top" align="left">End Month</td>		
						<td class="tableCell" width="97px" valign="top" align="left">End Year</td>					
					</tr>
				</table>
				<div style="overflow:scroll;height:400px;width:100%;overflow:auto">
				<table class="report" cellspacing="0" cellpadding="0">
					<c:forEach items="${failedUploadBean.errorList}" var="dedMiniBean" varStatus="gridRow">
					<tr class="${dedMiniBean.displayStyle}">
						<td class="tableCell" valign="top" width="97px" align="left">
							<spring:bind path="failedUploadBean.errorList[${gridRow.index}].staffId">
							<input type="text" name="<c:out value="${status.expression}"/>"
    							id="<c:out value="${status.expression}"/>"
   								value="<c:out value="${status.value}"/>" size="6" maxlength="8"/>
							</spring:bind>
						 </td>
						<td class="tableCell" valign="top" width="97px" align="left">
							<spring:bind path="failedUploadBean.errorList[${gridRow.index}].objectCode">
							<input type="text" name="<c:out value="${status.expression}"/>"
    							id="<c:out value="${status.expression}"/>"
   								value="<c:out value="${status.value}"/>" size="6" maxlength="8"/>
							</spring:bind>
						 </td>
						<td class="tableCell" valign="top" width="97px" align="left">
							<spring:bind path="failedUploadBean.errorList[${gridRow.index}].allowanceAmount">
							<input type="text" name="<c:out value="${status.expression}"/>"
    							id="<c:out value="${status.expression}"/>"
   								value="<c:out value="${status.value}"/>" size="6" maxlength="8"/>
							</spring:bind>
						 </td>
						 <td class="tableCell" valign="top" width="97px" align="left">
							<spring:bind path="failedUploadBean.errorList[${gridRow.index}].startMonthStr">
							<input type="text" name="<c:out value="${status.expression}"/>"
    							id="<c:out value="${status.expression}"/>"
   								value="<c:out value="${status.value}"/>" size="8" maxlength="8"/>
							</spring:bind>&nbsp;FEB or FEBRUARY
						 </td>
						 <td class="tableCell" valign="top" width="97px" align="left">
							<spring:bind path="failedUploadBean.errorList[${gridRow.index}].startYearStr">
							<input type="text" name="<c:out value="${status.expression}"/>"
    							id="<c:out value="${status.expression}"/>"
   								value="<c:out value="${status.value}"/>" size="4" maxlength="4"/>
							</spring:bind>&nbsp;i.e.;2021
						 </td>
						 <td class="tableCell" valign="top" width="97px" align="left">
							<spring:bind path="failedUploadBean.errorList[${gridRow.index}].endMonthStr">
							<input type="text" name="<c:out value="${status.expression}"/>"
    							id="<c:out value="${status.expression}"/>"
   								value="<c:out value="${status.value}"/>" size="8" maxlength="8"/>
							</spring:bind>&nbsp;JAN or JANUARY
						 </td>
						 <td class="tableCell" valign="top" width="97px" align="left">
							<spring:bind path="failedUploadBean.errorList[${gridRow.index}].endYearStr">
							<input type="text" name="<c:out value="${status.expression}"/>"
    							id="<c:out value="${status.expression}"/>"
   								value="<c:out value="${status.value}"/>" size="4" maxlength="4"/>
							</spring:bind>&nbsp;e.g 2021
						 </td>
					</tr>
					</c:forEach>					
					
				</table>
				</div>
				</c:if>
				
				
				</td>
			</tr>
 			<tr>
                 <td class="buttonRow" align="right">
                 <input type="image" name="_done" value="done" title="Save and Continue" src="images/done_h.png">&nbsp;
                 <input type="image" name="_cancel" value="cancel" title="Close Report" src="images/close.png">
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
