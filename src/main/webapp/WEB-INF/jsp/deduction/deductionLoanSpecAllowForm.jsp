<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> <c:out value="${roleBean.staffTypeName}"/> Deductions, Special Allowances &amp; Loans View  </TITLE>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
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
                    <div class="title">Staff Name :<c:out value="${namedEntity.name}"/><br>
					Staff ID : <c:out value="${namedEntity.staffId}"/><br>
					View : Deductions, Special Allowances and Loans</div>				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">
					View all active deduction(s), special allowance(s) and/or Loan(s) for each <c:out value="${namedEntity.name}"/>.
					<br><b>Note</b>: For recurring deductions, the naira amount or percent of basic pay to be withheld from each paycheck is displayed.
     				<form:form modelAttribute="dedConGarnBean">
     				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
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
							<td class="activeTH">Active <c:out value="${roleBean.staffTypeName}"/> Deductions</td>
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
										<!-- 
										<th width="20%" align="left">
											<span class="head optional">Annual max</span>&nbsp;
										</th>
										-->
									</tr>
									
									<c:forEach items="${dedConGarnBean.empDeductInfo}" var="empDed">
									<tr>
										<td width="25%" title="${empDed.empDeductionType.name}">${empDed.description}</td>
										<td>
											<input name="payType" value="${empDed.payTypes.name}" size="10" disabled="disabled"/>
										</td>
										<td width="20%">
											<input name="deductionRate" value="${empDed.deductionAmountStr}" size="10" disabled="disabled"/>
										</td>
										
										<td width="20%">
											&nbsp;
											
										</td>
										<td width="20%">
											&nbsp;
											
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
										
										<td width="20%">
											&nbsp;
										</td>
										
									</tr>
									</c:if>
									
								</table>
								
							</td>
						</tr>
						 
						<tr>
							<td class="activeTH" align="left" colspan="20">
								Active Special Allowances&nbsp;&nbsp;&nbsp;
							</td>
						</tr>
						<tr>
							<td id="firsttd_editdeduction_form" class="activeTD">
								<table width="95%" border="0" cellspacing="0" cellpadding="2">
									<tr>
										<td width="25%" align="left">
											<b>Description</b>&nbsp;
										</td>
										<td width="20%" align="left">
											<span class="required head">Apply as</span>
										</td>
										<td width="20%" align="left">
											<span class="optional head">Value</span>&nbsp;
										</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
									</tr>
									
									
									<c:forEach items="${dedConGarnBean.specialAllowanceInfo}" var="specAllow">
									<tr>
										<td width="25%" title="${specAllow.specialAllowanceType.description}">${specAllow.name}</td>
										<td>
											<input name="payTypeL" value="${specAllow.payTypes.name}" size="10" disabled="disabled"/>
										</td>
										<td width="20%">
											<input name="compContRate" value="${specAllow.amountAsStr}" size="10" disabled="disabled"/>
										</td>
										<!--  <td width="20%">							
										    &nbsp;
										</td>
										
										<td align="left">
											<a href='${appContext}/viewSpecialAllowanceHistory.do?aid=${specAllow.id}&eid=${dedConGarnBean.id}' >View History</a>
										</td>
										-->
									</tr>
									</c:forEach>
									
								</table>
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
										<td width="20%"><b>Type</b></td>
										<td width="20%" align="left">
											<span class="optional head">Value</span>&nbsp;
										</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
									</tr>
									
									<c:forEach items="${dedConGarnBean.empGarnishInfo}" var="garnInfo">
									<tr>
									<td width="25%" >${garnInfo.description}&nbsp;</td>
									<td width="2%" nowrap>${garnInfo.empGarnishmentType.name}</td>
									<td width="20%">
										<input name="garnAmount" value="${garnInfo.amount}" size="6" disabled="disabled"/>
									</td>
									
									<td align="left">
											<a href='${appContext}/viewGarnishmentHistory.do?eid=${dedConGarnBean.id}&garnId=${garnInfo.id}' onclick="popupWindow('${appContext}/viewGarnishmentHistory.do?eid=${dedConGarnBean.id}&garnId=${garnInfo.id}','View Loan');return false" target="_blank">View History</a>
											
									</td>
									</tr>
									</c:forEach>
								</table>
							</td>
						</tr>
						</c:if>
					</table>
				</td>
			</tr>
			<tr>
				<td class="buttonRow" align="right" >
					<input type="image" name="_cancel" value="cancel" title="Close View" class="" src="images/close.png">
				</td>
			</tr>
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
