<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Leave Bonus Error Page </title>
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
			Leave Bonus<br>
			File Upload Errors
			 </div>
		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">
		<div id="topOfPageBoxedErrorMessage" style="display:${failedUploadBean.displayErrors}">
								 <spring:hasBindErrors name="failedUploadBean">
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
			
						<c:if test="${failedUploadBean.saveMode}">
						<tr>
						 <td>
						<table cellspacing="0" cellpadding="0" width="50%">
			             <tr>
									<td class="activeTH">Error File Save Details....</td>
								</tr>
								<tr>
									<td class="activeTD">
									   <fieldset>
									   <legend><b>Save Information</b></legend>
										<table width="100%" border="0" cellspacing="1" cellpadding="2">
											<tr>
												<td width="25%" align="left">Unique ID*</td>
												<td width="25%" align="left"><form:input path="name" size="40" maxlength="15"/></td>
											</tr>
											<tr>
												<td width="25%" align="left">Description*</td>
												<td width="25%" align="left"><form:textarea path="displayName" rows="5" cols="40"/></td>
											</tr>
										
											</table>						
										</fieldset>
										
									</td>
								</tr>
								</table>
								</td>
								</tr>
								</c:if>
			<tr>
				<td>
				<h3>MDA Uploaded For :&nbsp;<font color="green"><c:out value="${failedUploadBean.mode}"/></font></h3>
				<br />
				
				 
				<h4><c:out value="${failedUploadBean.name}"/> Errors</h4>
				<br />
				<table class="report" cellspacing="0" cellpadding="0">
						<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="5%" align="left">&nbsp;</td>
						<td class="tableCell" valign="top" width="9%" align="left"><c:out value="${roleBean.staffTitle}"/></td>
						<td class="tableCell" width="20%" valign="top" align="left">Name</td>
						<td class="tableCell" width="12%" valign="top" align="left">Amount</td>
						<td class="tableCell" width="8%" valign="top" align="left">LTG Year</td>
						<td class="tableCell" width="46%" valign="top" align="left">Errors</td>
						 					
					</tr>
				</table>
				<div style="overflow:scroll;height:400px;width:100%;overflow:auto">
				<table class="report" cellspacing="0" cellpadding="0">
					<c:forEach items="${failedUploadBean.errorList}" var="dedMiniBean" varStatus="gridRow">
					<tr class="${dedMiniBean.displayStyle}">
					    <td class="tableCell" valign="top" width="5%"><c:out value="${gridRow.index + 1}"/></td>
						<td class="tableCell" valign="top" width="9%" align="left"><c:out value="${dedMiniBean.staffId}"/></td>
						<td class="tableCell" valign="top" width="20%" align="left"><c:out value="${dedMiniBean.deductionCode}"/></td>
						<td class="tableCell" valign="top" width="12%" align="left"><c:out value="${dedMiniBean.deductionAmountStr}"/></td>
						<td class="tableCell" valign="top" width="8%" align="left"><c:out value="${dedMiniBean.ltgYear}"/></td>	
						<td class="tableCell" valign="top" width="46%" align="left" title="${dedMiniBean.titleField}"><c:out value="${dedMiniBean.errorMsg}"/></td>	 
					</tr>
					</c:forEach>					
					
				</table>
				</div>				 
				</td>
			</tr>
 			<tr>
                 <td class="buttonRow" align="right">
                 <input type="image" name="submit" value="ok" title="Save Error List" src="images/Save_h.png">&nbsp;
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
