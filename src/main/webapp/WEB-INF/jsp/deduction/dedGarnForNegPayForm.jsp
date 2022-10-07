<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> <c:out value="${roleBean.staffTypeName}"/> Deductions, Special Allowances &amp; Loans Info  </TITLE>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<script language="JavaScript">
<!--


function go(which,destUrl) {
  n = which.value;
  if (n == -1) {
    location.href = destUrl;
  }else if(n > 0){
  	var url = destUrl+"&cid="+n+"&atn=s";
  	location.href = url;
  }
}

 //-->
</script>
</head>

<body class="main">
				
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					
			<tr>
				<td colspan="2">	
					<div style="color: #006400; font-size: 15px"><b style="font-size:15px">Staff Name :</b> <c:out value="${namedEntity.name}"/><br>
					     <b style="font-size:15px">Staff ID :</b> <c:out value="${namedEntity.staffId}"/><br>
					</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">
					Remove/Edit appropriate deduction(s) and/or Loan(s) for each <c:out value="${roleBean.staffTypeName}"/>.
     				
     						 
     							<c:if test="${dedConGarnBean.showNegativePayMsg and dedConGarnBean.netDifference lt 0}">
     								<br><b>Note</b>: <font color="red">Reduce Deductions or Loans by a minimum of <c:out value="${dedConGarnBean.negativeNetPayStr}"/></font>
     							</c:if>
     							<c:if test="${dedConGarnBean.showNegativePayMsg and dedConGarnBean.netDifference ge 0}">
     								<br><b>Note</b>: <font color="green">Deductions or Loans have been reduced. Estimated Net Pay <i> after payroll re-run**</i> : <c:out value="${dedConGarnBean.negativeNetPayStr}"/></font>
     							</c:if>
     						
     						
     					
     					
     				<form:form modelAttribute="dedConGarnBean">
     				<div id="topOfPageBoxedErrorMessage" style="display:${dedConGarnBean.displayErrors}">
						<spring:hasBindErrors name="dedConGarnBean">
         					<ul>
            					<c:forEach var="errMsgObj" items="${errors.allErrors}">
               						<li>
                  						<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               						</li>
            						</c:forEach>
         					</ul>
     					</spring:hasBindErrors>
					</div>
     				<table border="0" cellspacing="0" cellpadding="0" width="90%"><tr><td>
					<table class="formtable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
						<tr align="left">
							<td class="activeTH"><c:out value="${roleBean.staffTypeName}"/> Deductions</td>
						</tr>
						<tr>
							<td id="firsttd_editdeduction_form" class="activeTD">
								<table width="95%" border="0" cellspacing="0" cellpadding="2">
									<tr>
										<th width="25%" align="left">
											<b>Description</b>&nbsp;
										</th>
										<th width="20%" align="left">
											<span class="required head">Deduct as</span>
										</th>
										<th width="20%" align="left">
											<span class="optional head">Value</span> &nbsp;
										</th>
									</tr>
									
									<c:forEach items="${dedConGarnBean.empDeductInfo}" var="empDed">
									<tr>
										<c:choose>
											<c:when test="${empDed.lastEdited eq 1 }">
											  <td width="25%"><font color="red">${empDed.description}</font></td>
											</c:when>
											<c:otherwise>
												<td width="25%">${empDed.description}</td>
											</c:otherwise>
										</c:choose>
										<td>
											<input name="payType" value="${empDed.payTypes.name}" size="10" disabled="disabled"/>
										</td>
										<td width="20%">
											<input name="deductionRate" value="${empDed.deductionAmountStr}" size="10" disabled="disabled"/>
										</td>
										<td align="left">
											<a href='${appContext}/empEditDeductionForm.do?eid=${dedConGarnBean.id}&cid=${empDed.id}&npv=${dedConGarnBean.negPayId}' >Edit</a>
 										</td>
									</tr>
									</c:forEach>
									<c:if test="${dedConGarnBean.hiringInfo.pensionableEmployee and dedConGarnBean.showContributionRow}">
									<tr>
										<td width="25%">${dedConGarnBean.pensionName}</td>
										<td>
											<input name="contributionPayType" value="${dedConGarnBean.contributionPayType}" size="10" disabled="disabled" title="${dedConGarnBean.contributionAmountStr}"/>
										</td>
										<td width="20%">
											<input name="contributionAmount" value="${dedConGarnBean.contributionAmount}" size="10" disabled="disabled" "/>
										</td>
										
										<td align="left">
											<a href='${appContext}/editHireInfo.do?eid=${dedConGarnBean.hiringInfo.employee.id}' >Remove</a>&nbsp;
										</td>
									</tr>
									</c:if>
									
								</table>
								<!--
								<table width="95%" border="0" cellspacing="0" cellpadding="2">
									<tr>
										<td height="10" colspan="2"></td>
									</tr>
									<tr>
										<td width="25%">
											<span class="optional">Add deduction</span>
										</td>
										<td colspan="3">
											<select name="adddeduction" id="deduction" onChange="go(this,'${appContext}/empEditDeductionForm.do?eid=${dedConGarnBean.id}')" >
												<option value="0">Choose one...</option>
												<option value="-1">New deduction</option>
											</select>
											<br/>
										</td>
									</tr>
								</table>
								-->
							</td>
						</tr>

						<c:if test="${not roleBean.pensioner}">
						<tr>
							<td class="activeTH" align="left" colspan="20">Loans</td>
						</tr>
						<tr>
							<td class="activeTD">
								<table width="95%" border="0" cellspacing="0" cellpadding="2">
									<tr>
										<td width="25%"><b>Description</b></td>
										<td width="20%" align="left">
											<span class="optional head">Loan Amount</span>&nbsp;
										</td>
										<td><b>Current Balance</b></td>
										<td><b>Monthly Deduction</b></td>
										<td>&nbsp;</td>
									</tr>
									
									<c:forEach items="${dedConGarnBean.empGarnishInfo}" var="garnInfo">
									<tr>
									<c:choose>
											<c:when test="${garnInfo.lastEdited eq 1 }">
											  <td width="25%"><font color="red" title="${garnInfo.empGarnishmentType.name}">${garnInfo.description}&nbsp;</font></td>
											</c:when>
											<c:otherwise>
												<td width="25%" title="${garnInfo.empGarnishmentType.name}">${garnInfo.description}&nbsp;</td>
											</c:otherwise>
										</c:choose>
									<td width="20%">
										<input name="originalLoanAmount" value="${garnInfo.originalLoanAmountStr}" size="10" disabled="disabled"/>
									</td>
									<td width="20%">
										<input name="owedAmount" value="${garnInfo.owedAmountAsStr}" size="10" disabled="disabled"/>
									</td>
									
									<td width="20%">
										<input name="garnAmount" value="${garnInfo.garnishAmountAsStr}" size="10" disabled="disabled"/>
									</td>
									
									<td align="left">
											<a href='${appContext}/empGarnishForm.do?eid=${dedConGarnBean.id}&cid=${garnInfo.id}&atn=v' >View</a>&nbsp;
											<c:if test="${roleBean.superAdmin }"><span class="tabseparator">|</span>
											<a href='${appContext}/empGarnishForm.do?eid=${dedConGarnBean.id}&cid=${garnInfo.id}&atn=d' >Remove</a>&nbsp;<span class="tabseparator">|</span>
											<a href='${appContext}/empGarnishForm.do?eid=${dedConGarnBean.id}&cid=${garnInfo.id}&npv=${dedConGarnBean.negPayId}' >Edit</a></c:if>
									</td>  
									</tr>
									</c:forEach>
									<!--
									<tr>
										<td width="25%">
											<span class="optional">Add New Loan</span>
										</td>
										<td colspan="3">
											<select name="addgarnishment" id="addgarnishmentselect" onChange="go(this,'${appContext}/empGarnishForm.do?eid=${dedConGarnBean.id}')" >
												<option value=0>Choose one...</option>
												<option value="-1">New Loan</option>
											</select>
											<br/>
										</td>
									</tr>
									-->
								</table>
							</td>
						</tr>
						</c:if>
					</table>
				</td>
			</tr>
			<c:choose>
				<c:when test="${dedConGarnBean.mustDeletePaycheck}">
					<tr>
						<td class="buttonRow" align="right" >
							 <input type="image" name="_schedule" value="schedule" title="Schedule For Payroll Rerun" class="" src="images/deletePaycheck.png">
						</td>
					</tr>
				</c:when>
				<c:otherwise>
						<tr>
							<td class="buttonRow" align="right" >
								<input type="image" name="_cancel" value="cancel" title="Close Window" class="" src="images/close.png">
								&nbsp;<input type="image" name="_schedule" value="schedule" title="Schedule For Payroll Rerun" class="" src="images/deletePaycheck.png">
							</td>
						</tr>
				</c:otherwise>
			</c:choose>
			
		</table>
		</form:form>
		</table>
		
			</td>
		</tr>
		
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
		
	</table>

</body>
</html>
