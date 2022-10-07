<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Enter Cashbook Variables Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
</head>


<body class="main">

	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					


					<tr>
						<td colspan="2">
								<div class="title"> Enter Cashbook Generation Values </div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="empMiniBean">
						<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="empMiniBean">
         							 <ul>
            							<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            							</c:forEach>
         								</ul>
     							 </spring:hasBindErrors>
						</div>
				<table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
					<tr align="left">
						<td class="activeTH">Search Criteria</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
										<td width="35%" align="right">Cashbook Month :</td>
										<td width="25%" align="left" colspan="2">
										 <form:select path="monthInd" title="Select the month to generate Cashbook">
												<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												  <c:forEach items="${monthList}" var="mList">
													<form:option value="${mList.id}">${mList.name}</form:option>
												</c:forEach>	
										</form:select>
										</td>
								</tr>
								<tr>
										<td width="35%" align="right">Cashbook Year :</td>
										<td width="25%" align="left" colspan="2">
										 <form:select path="yearInd" title="Select the Year to generate Cashbook">
												<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												  <c:forEach items="${yearList}" var="years">
													<form:option value="${years.id}">${years.name}</form:option>
												</c:forEach>	
										</form:select>
										</td>
								</tr>
					            <tr>
									<td align="right" width="35%" nowrap>
									<span class="required">Public Office Holders Emoluments*</span></td>
									<td width="25%">
										<form:input path="amountStr" size="15" maxlength="15" />&nbsp;e.g 500,000.99 can be 0.00
									</td>									
					            </tr>
					            <tr>
									<td align="right" width="35%" nowrap>
									<span class="required">Public Office Holders PAYE*</span></td>
									<td width="25%">
										<form:input path="payeStr" size="15" maxlength="15" />&nbsp;e.g 500,000.99 can be 0.00
									</td>									
					            </tr>
					            <c:if test="${empMiniBean.showConfirmationRow}">
					            <tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Generated Code*</span></td>
									<td width="25%">
										<form:input path="name" size="50" maxlength="50" disabled="true"/>
									</td>
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Enter Code Above* </span></td>
									<td width="25%">
										<form:input path="description" size="50" maxlength="50"/>&nbsp;<font color="red"><i>Case Insensitive</i></font>										
									</td>
								</tr>
								</c:if>
					           </table>							
						</td>
					</tr>
					<tr>
						
						<td class="buttonRow" align="right" >
						<c:choose>
							<c:when test="${empMiniBean.showConfirmationRow}">
								<input type="image" name="submit" value="ok" title="Ok" class="" src="images/confirm_h.png">
							</c:when>
							<c:otherwise>
							<input type="image" name="submit" value="ok" title="Ok" class="" src="images/ok_h.png">
							</c:otherwise>
							
						</c:choose>
							
							<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
						</td>
					</tr>
				</table>
				</form:form>
			

			</td>
		</tr>
		
		</table>
		</td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
</body>
</html>

