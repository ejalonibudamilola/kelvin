<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Setup | Create Employee Contribution  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<script language="JavaScript">
<!--


function go(which) {
  n = which.value;
  if (n != 0) {
    var url = "${appContext}/busContributionForm.do?cid=" + n
    location.href = url;
  }
}

// -->
</script>
</head>

<body class="main">
	<table class="main" width="70%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					<tr>
						<td colspan="2">
							<div class="title">Company Contribution for&nbsp;<c:out value="${namedEntity.name}" /></div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainbody" id="mainbody">
							* = required<br/><br/>
							After clicking OK, go to the Employee Overview page to assign the deduction to appropriate employees.<br/><br>
							<form:form modelAttribute="empCompCont">
							 <div id="topOfPageBoxedErrorMessage" style="display:${empCompCont.displayErrors}">
								 <spring:hasBindErrors name="empCompCont">
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
									<td class="activeTH">Employee Company Contribution</td>
								</tr>
								<tr>
									<td id="firstTD_editDeduction_form" class="activeTD">
										<table width="95%" border="0" cellspacing="0" cellpadding="2">
											<tr>
												<td width="25%" align=right nowrap valign="center"><span class="required">Contribution Type</span></td>
												<td>
													<form:input path="category" disabled="true"/>													
												</td>
											</tr>
											<tr>
												<td align="right" nowrap valign="center">
													<span class="required">Provider</span>
												</td>
												<td>
													<form:input path="provider" disabled="true"/>
												</td>
											</tr>
											<tr>
												<td align="right" nowrap valign="center">
													<span class="required">Contribute as</span>
												</td>
												<td>
												<form:select path="payTypesRef" disabled="true">
												<form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<c:forEach items="${payTypeList}" var="paytype">
												<form:option value="${paytype.id}">${paytype.name}</form:option>
												</c:forEach>
												</form:select>
												</td>
											</tr>
											<tr>
												<td align="right" nowrap valign="center">
													<span class="required">Value (optional)</span>
												</td>
												<td>
													<form:input path="contributionAmount" maxlength="10" size="8"/>													
												</td>
											</tr>
											<tr>
												<td colspan="2" align="right">
													<table>
														<tr>
															<td colspan="1">
													
																&nbsp;
													
															</td>
															<td align="right" colspan="1">
															</td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td class="buttonRow" align="right" >
									    <!-- 
										<input type="image" name="submit" value="ok" alt="Ok" class="" src="images/ok_h.png">
										 -->
										<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
									</td>
								</tr>
							</table>
							</form:form>
						</td>
					</tr>
					</table>
					
					<tr>
						<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
					</tr>
				</table>
	
</body>
</html>