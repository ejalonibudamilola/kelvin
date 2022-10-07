<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Setup | Create Deduction  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
 <link rel="stylesheet" href="<c:url value="/styles/epayroll.css"/>" type="text/css"/>

<script language="JavaScript">
function go(which,pid,ind) {
	n = which.value;
	   if (n > 0) {
		   if(ind == 0){
	    	var url = "${appContext}/empEditDeductionForm.do?eid="+pid+"&cid=" + n
	    	 
		   }else{
			   var url = "${appContext}/empEditDeductionForm.do?eid="+pid+"&cid="+ n+"&ind="+ind
		    	
		   }
		   location.href = url;
	  }
}
</script>
</head>

<body class="main">
<script type="text/JavaScript" src="scripts/jacs.js"></script>
	<table class="main" width="70%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
				
					<tr>
						<td colspan="2">
							<div class="title"><c:out value="${empDeductionInfo.displayPayrollMsg}"/><br>
							<c:out value="${empDeductionInfo.errorMsg}"/></div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainbody" id="mainbody">
							* = required<br/><br/>
							After clicking OK, go to the <c:out value="${roleBean.staffTypeName}"/> Overview page to see the deduction is added for <c:out value="${namedEntity.name}" />.<br/><br>
							<form:form modelAttribute="empDeductionInfo">
							<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="empDeductionInfo">
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
									<td class="activeTH"><c:out value="${empDeductionInfo.displayTitle}" /></td>
								</tr>
								<tr>
									<td id="firstTD_editDeduction_form" class="activeTD">
										<table width="95%" border="0" cellspacing="0" cellpadding="2">
											<tr>
												<td width="25%" align=right nowrap valign="middle"><span class="required">Category*</span></td>
												<td>
													<c:choose>
													<c:when test="${empDeductionInfo.lockCategory}" >
													<form:select path="empDeductCatRef" id="empDeductCat" disabled="true">
														<form:option value="0"> &lt; select a category &gt;</form:option>
														<c:forEach items="${deductionCategory}" var="dedCat">
					                					<form:option value="${dedCat.id}">${dedCat.name}</form:option>
					               						</c:forEach>
													</form:select>
													&nbsp;
													</c:when>
													<c:otherwise>
													<form:select path="empDeductCatRef" id="empDeductCat" onchange="go(this,${namedEntity.id},0);" >
														<form:option value="0"> &lt; select a category &gt;</form:option>
														<c:forEach items="${deductionCategory}" var="dedCat">
					                					<form:option value="${dedCat.id}">${dedCat.name}</form:option>
					               						</c:forEach>
													</form:select>
													&nbsp;
													</c:otherwise>
													</c:choose>
													
												</td>
											</tr>


											<tr>
												<td align=right nowrap valign="middle">
													<span class="required">Type*</span>
												</td>
												<td>
												<c:choose>
												<c:when test="${empDeductionInfo.lockDeductType}">
												<form:select path="empDeductTypeRef" disabled="true">
												<form:option value="-1"> &lt; select a type &gt;</form:option>
												<c:forEach items="${empTypeList}" var="dedType">
					                			<form:option value="${dedType.id}" title="${dedType.name}">${dedType.description}</form:option>
					               				</c:forEach>
												</form:select>
													&nbsp;
												</c:when>
												<c:otherwise>
												<form:select path="empDeductTypeRef" onchange="go(this,${namedEntity.id},1);">
												<form:option value="-1"> &lt; select a type &gt;</form:option>
												<c:forEach items="${empTypeList}" var="dedType" >
					                			<form:option value="${dedType.id}" title="${dedType.name}">${dedType.description}</form:option>
					               				</c:forEach>
												</form:select>
													&nbsp;
												</c:otherwise>
												</c:choose>
													
												</td>
											</tr>
											<tr>
												<td align="right" valign="top">
													<span class="required">Description*</span>
												</td>
												<td nowrap>
													<form:input path="description" size="30" maxlength="20" disabled="${empDeductionInfo.lockDescription}" />
														<span class="note">(appears on paycheck)</span><br>
														
												</td>

											</tr>
											<tr>
												<td align="right" nowrap valign="top">
													<span class="required">Deduct as</span>
												</td>
												<td>
												<c:choose>
													<c:when test="${empDeductionInfo.delete}"> 
														<form:select path="empDeductPayTypeRef" disabled="true" >
															<form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
															<c:forEach items="${payType}" var="paytypeList">
															<form:option value="${paytypeList.id}">${paytypeList.name}</form:option>
															</c:forEach>
														</form:select>
													</c:when>
													<c:otherwise>
														<form:select path="empDeductPayTypeRef" disabled="${empDeductionInfo.lockPayType}">
															<form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
															<c:forEach items="${payType}" var="paytypeList">
															<form:option value="${paytypeList.id}">${paytypeList.name}</form:option>
															</c:forEach>
														</form:select>
													</c:otherwise>
												</c:choose>
												</td>
											</tr>
											<tr>
												<td align="right" nowrap valign="top">
													<span>Value</span>
												</td>
												<td>
													<form:input path="amount" maxlength="10" size="8" disabled="${empDeductionInfo.lockAmount or empDeductionInfo.editDenied}"/>
													&nbsp;
													
												</td>
											</tr>
											<c:if test="${empDeductionInfo.showDateRows}">
											<tr>
								 					<td align="right" width="30%">Deduction Start Date*</td>
					              					 <td align="left"><form:input path="startDate"/><img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('startDate'),event);"></td>
					             					
											</tr>
						        			<tr>
								 					<td align="right" width="30%">Deduction End Date*</td>
					              					<td align="left"><form:input path="endDate"/><img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('endDate'),event);"></td>
					             					
											</tr> 
											</c:if>
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
								<c:choose>
									<c:when test="${empDeductionInfo.editDenied}">
										<tr>
											<td class="buttonRow" align="right" >
												
									       		 <input type="image" name="_cancel" value="cancel" title="Close" class="" src="images/close.png">
									       		
							 			</td>
							 				 
									
										</tr>
									</c:when>
									<c:otherwise>
										<tr>
											<td class="buttonRow" align="right" >
													<input type="image" name="submit" value="ok" title="Ok" class="" src="images/ok_h.png">
											        <input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
											        <c:if test="${empDeductionInfo.delete}"> 
									 					<input type="image" name="_delete" value="delete" title="Delete" class="" src="images/delete_h.png">
									 				</c:if>
									 		</td>
									 				 
											
										</tr>
									
									</c:otherwise>
								</c:choose>
								
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