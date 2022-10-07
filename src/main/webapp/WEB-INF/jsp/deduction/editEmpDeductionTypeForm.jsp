<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Edit Deduction Type Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.4.4.min.js"/>"></script>
</head>

<body class="main">

	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					<tr>
						<td>
								<div class="title">Edit Deduction Type</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="deductionTypeBean">
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="deductionTypeBean">
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
						<td class="activeTH">Edit <c:out value="${deductionTypeBean.name}"/> </td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Deduction Type Name*</span></td>
									<td width="25%">
										<form:input path="name" size="8" maxlength="12"/>
									</td>
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Description*</span></td>
									<td width="25%">
										<form:input path="description" size="30" maxlength="30"/>										
									</td>
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required" title="A Date Dependent Type of Deduction will insist on the selection of a start and an end date while adding this deduction to ${roleBean.staffTypeName}.">Date Dependent*</span></td>
									<td width="25%" nowrap>
									    <form:radiobutton path="dateDependent" value="0" />No <form:radiobutton path="dateDependent" value="1"/> Yes
										
									</td>
								</tr>

								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required" title="Indicate whether Taxable or non-Taxable (this impacts on Free Pay and Taxes if PITA is being applied)">Tax Deductible*</span></td>
									<td width="25%" nowrap>
									    <form:radiobutton path="taxable" value="N" title="Selecting this option will REDUCE taxes payable for each ${roleBean.staffTypeName} having this deduction"/>No <form:radiobutton path="taxable" value="Y" title="Selecting this option will INCREASE taxes payable for each employee having this deduction"/> Yes
										
									</td>
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required" title="Indicate whether editing of Deductions of this type should be restricted to super admin and admin roles">Restrict Editing*</span></td>
									<td width="25%" nowrap>
									    <form:radiobutton path="editRestrictionInd" value="0" title="Edit of Deductions of this type not restricted to super admin and admin roles"/>No <form:radiobutton path="editRestrictionInd" value="1" title="Only Super Admin and Administrator Role can edit Deductions of this type"/> Yes
										
									</td>
								</tr>
                                <tr>
									<td align="right" width="35%" nowrap>
										<span class="required" title="Indicate whether Deductions of this type should be available for Selection">Deactivate/Reactivate*</span></td>
									<td width="25%" nowrap>
									    <form:radiobutton path="displayableInd" value="0" title="Selecting this value allows this Deduction Type to be available for Selection"/>Active <form:radiobutton path="displayableInd" value="1" title="Selecting this value will 'deactivate' this Deduction Type - It will not be available for selection"/> Inactive

									</td>
								</tr>
								<c:if test="${roleBean.subeb}">
                                <tr>
									<td align="right" width="35%" nowrap>
										<span class="required" title="Indicates that Deductions of this type should be reported As 'Union Dues'">Union Dues Type*</span></td>
									<td width="25%" nowrap>
									    <form:radiobutton path="unionDueInd" value="0" title="Selecting this value allows this Deduction Type NOT to be reported as 'Union Dues'"/>No<form:radiobutton path="unionDueInd" value="1" title="Selecting this value allows this Deduction Type to be reported as 'Union Dues'"/> Yes
                                    </td>
								</tr>
								</c:if>
									<c:if test="${deductionTypeBean.showApportionsRows}">
									<fieldset>
                                    	<legend><b>Apportionment Details</b></legend>
									<tr>
										<td align="right" width="35%" nowrap>
											<span>Allotment 1</span>
										</td>

                                        <td width="25%">
											<form:input path="firstAllotment" maxlength="15" size="8"/>
                                            <form:input path="firstAllotAmt" maxlength="5" size="4"/>%
										</td>

									</tr>
									<tr>
										<td align="right" width="35%" nowrap>
											<span>Allotment 2</span>
										</td>

                                        <td width="25%">
											<form:input path="secAllotment" maxlength="15" size="8"/>
                                            <form:input path="secAllotAmt" maxlength="5" size="4"/>%
										</td>

									</tr>
									<tr>
										<td align="right" width="35%" nowrap>
											<span>Allotment 3</span>
										</td>

                                        <td width="25%">
											<form:input path="thirdAllotment" maxlength="15" size="8"/>
                                            <form:input path="thirdAllotAmt" maxlength="5" size="4"/>%
										</td>

									</tr>
									</fieldset>
									</c:if>
								<tr style="${deductionTypeBean.showForConfirm}">
									<td align="right" width="35%" nowrap>
										<span class="required">Deduct as*</span>
									</td>
									<td width="25%">
										<form:select path="empDeductPayTypeRef">
											<form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<c:forEach items="${payTypeList}" var="pList">
													<form:option value="${pList.id}">${pList.name}</form:option>
												</c:forEach>
											</form:select>
												
									</td>
									</tr>
									<tr style="${deductionTypeBean.showForConfirm}">
										<td align="right" width="35%" nowrap>
											<span>Value</span>
										</td>
										<td width="25%">
											<form:input path="amount" maxlength="10" size="8"/>
													&nbsp;
													
										</td>
									</tr>
									<tr style="${deductionTypeBean.showForConfirm}">
										<td align="right" width="35%" nowrap>
											<span class="required">Bank Name*</span>
										</td>
										<td width="25%">
											<form:select path="bankInstId" onchange='loadBankBranchesByBankId(this);'>
												<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<c:forEach items="${bankList}" var="bList">
												<form:option value="${bList.id}">${bList.name}</form:option>
												</c:forEach>
												</form:select>
										</td>
										</tr> 
										<tr style="${deductionTypeBean.showForConfirm}">
										<td align="right" width="35%" nowrap>
											<span class="required">Branch Name*</span>
										</td>
										<td width="25%">
											<form:select path="branchInstId" id="bank-branch-control" cssClass="branchControls">
												<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<c:forEach items="${bankBranchList}" var="bbList">
												<form:option value="${bbList.id}">${bbList.name}</form:option>
												</c:forEach>
												</form:select>
										</td>
										</tr> 
										<tr style="${deductionTypeBean.showForConfirm}">
												<td align="right" width="35%" nowrap>
													<span class="required">Account Number*</span>
												</td>
												<td width="25%">
													<form:input path="accountNumber" size="20" maxlength="20" />
												</td>
											</tr>
										<tr style="${deductionTypeBean.showForConfirm}">
												<td align="right" width="35%" nowrap>
													<span class="required">Confirm Account Number</span>
												</td>
												<td width="25%">
													<form:input path="confirmAccountNumber" size="20" maxlength="20" />
												</td>
										</tr>
										<c:if test="${deductionTypeBean.showUpdPaychecksLink}">
											<tr style="${deductionTypeBean.showForConfirm}">
												<td align="right" width="35%" nowrap>
													<span class="required">&nbsp;</span>
												</td>
												<td width="25%">
												<spring:bind path="updPendingPaychecks">
													<input type="hidden" name="_<c:out value="${status.expression}"/>">
													<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if> title="Checking this box will Update Account Number and Bank Information on Pending Paycheck Deductions."/>
													</spring:bind>
													<span class="required">Update Pending Paycheck Deductions</span>
												</td>
											</tr>
										</c:if>
					           </table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
						<c:choose>
							<c:when test="${saved}">
							<input type="image" name="_cancel" value="cancel" title="Close" src="images/close.png">
							</c:when>
							<c:otherwise>
								<input type="image" name="submit" value="ok" title="Ok" class="" src="images/ok_h.png">&nbsp;
								<input type="image" name="_cancel" value="cancel" title="Cancel" src="images/cancel_h.png">
							
							</c:otherwise>
						</c:choose>
							
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