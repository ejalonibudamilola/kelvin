<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Setup | Create Company Contribution  </title>
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
							<div class="title">Create Company Deduction</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainbody" id="mainbody">
							* = required<br/><br/>
							After clicking OK, go to the <c:out value="${roleBean.staffTypeName}"/>  Overview page to assign the deduction to appropriate <c:out value="${roleBean.staffTypeName}"/>&apos;.<br/><br>
							<form:form modelAttribute="compContributionInfo">
							 <div id="topOfPageBoxedErrorMessage" style="display:${compContributionInfo.displayErrors}">
								 <spring:hasBindErrors name="compContributionInfo">
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
									<td class="activeTH">Company Contribution</td>
								</tr>
								<tr>
									<td id="firstTD_editDeduction_form" class="activeTD">
										<table width="95%" border="0" cellspacing="0" cellpadding="2">
											<tr>
												<td width="25%" align=right nowrap valign="center"><span class="required">Category*</span></td>
												<td>
													<c:choose>
													<c:when test="${compContributionInfo.editMode}" > 
													<form:select path="compContCatRef" id="compContCat" disabled="true">
														<form:option value="0"> &lt; select a category &gt;</form:option>
														<c:forEach items="${contributionCategory}" var="contCat">
					                					<form:option value="${contCat.id}">${contCat.name}</form:option>
					               						</c:forEach>
													</form:select>
													&nbsp;
													</c:when>
													<c:otherwise>
													<form:select path="compContCatRef" id="compContCat" onchange="go(this)">
														<form:option value="0"> &lt; select a category &gt;</form:option>
														<c:forEach items="${contributionCategory}" var="contCat">
					                					<form:option value="${contCat.id}">${contCat.name}</form:option>
					               						</c:forEach>
													</form:select>
													&nbsp;
													</c:otherwise>
													</c:choose>
													
												</td>
											</tr>


											<tr>
												<td align=right nowrap valign="center">
													<span class="required">Type*</span>
												</td>
												<td>
												<c:choose>
												<c:when test="${compContributionInfo.editMode}" > 
												<form:select path="compContTypeRef" disabled="true">
												<form:option value="0"> &lt; Select a type &gt;</form:option>
												<c:forEach items="${compTypeList}" var="contType">
					                			<form:option value="${contType.id}">${contType.name}</form:option>
					               				</c:forEach>
												</form:select>
													&nbsp;
												</c:when>
												<c:otherwise>
												<form:select path="compContTypeRef">
												<form:option value="0"> &lt; Select a type &gt;</form:option>
												<c:forEach items="${compTypeList}" var="contType">
					                			<option value="${contType.id}">${contType.name}</option>
					               				</c:forEach>
												</form:select>
													&nbsp;
												</c:otherwise>
												</c:choose>
													
												</td>
											</tr>
											<tr>
												<td align=right valign="top">
													<span class="required">Description*</span>
												</td>
												<td nowrap>
													<form:input path="description" maxlength="36" />
														<span class="note">(appears on paycheck)</span><br>
														<div class="note">Vanguard 401K Plan or other Company only contribution</div>
												</td>

											</tr>
											<tr>
												<td align="right" nowrap valign="center">
													<span class="required">Deduct as</span>
												</td>
												<td>
												<form:select path="compContPayTypeRef">
												<form:option value="0">&lt; Select a type &gt;</form:option>
												<c:forEach items="${payTypeList}" var="paytype">
												<form:option value="${paytype.id}">${paytype.name}</form:option>
												</c:forEach>
												</form:select>
												</td>
											</tr>
											<tr>
												<td align=right nowrap valign="center">
													<span class="required">Value (optional)</span>
												</td>
												<td>
													<form:input path="contributionAmount" maxlength="10" size="8"/>
													&nbsp;
													
												</td>
											</tr>
											<tr>
												<td align=right nowrap valign="center">
													<span class="required">Annual max (optional) ₦</span>
												</td>
												<td>
													<form:input path="annualMax" size="8" maxlength="10" />
													&nbsp;
													
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
										<input type="image" name="submit" value="ok" alt="Ok" class="" src="images/ok_h.png">
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