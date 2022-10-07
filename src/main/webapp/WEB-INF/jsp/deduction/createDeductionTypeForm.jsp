<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> New Deduction Type Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon"
      type="image/png"
      href="<c:url value="/images/coatOfArms.png"/>">
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>

<script language="JavaScript">
<!--
function go(which) {
 n = which.value;
   if (n != 0) {
    var url = "${appContext}/createDeductionType.do?cid="+n
    location.href = url;
  }
}

// -->
</script>


</head>

<body class="main">

	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					<tr>
						<td>
								<div class="title">Create a New Deduction Type</div>
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
						<td class="activeTH">New Deduction Type Information</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Deduction Category*</span></td>
										<td width="25%">
											<form:select path="empDeductCatRef" id="empDeductCat" onchange="go(this)" >
											  <form:option value="-1"> &lt; select a category &gt;</form:option>
											 	<c:forEach items="${deductionCategory}" var="dedCat">
					                					<form:option value="${dedCat.id}">${dedCat.name}</form:option>
					               				</c:forEach>
											</form:select>
									    </td>
								
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Deduction Type Code*</span></td>
									<td width="25%">
										<form:input path="name" size="10" maxlength="15"/>
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
										<span class="required" title="A Date Dependent Type of Deduction will insist on <br>the selection of a start and an end date while adding this deduction to an employee.">Date Dependent*</span></td>
									<td width="25%" nowrap>
									    <form:radiobutton path="dateDependent" value="0" />No <form:radiobutton path="dateDependent" value="1"/> Yes
										
									</td>
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required" title="Indicate whether Taxable or non-Taxable (this impacts on Free Pay and Taxes if PITA is being applied)">Tax Deductible*</span></td>
									<td width="25%" nowrap>
									    <form:radiobutton path="taxable" value="N" title="Selecting this option will REDUCE taxes payable for each employee having this deduction"/>No <form:radiobutton path="taxable" value="Y" title="Selecting this option will INCREASE taxes payable for each employee having this deduction"/> Yes
										
									</td>
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required" title="Indicate whether editing of Deductions of this type should be restricted to super admin and admin roles">Restrict Editing*</span></td>
									<td width="25%" nowrap>
									    <form:radiobutton path="editRestrictionInd" value="0" title="Edit of Deductions of this type not restricted to super admin and admin roles"/>No <form:radiobutton path="editRestrictionInd" value="1" title="Only Super Admin and Administrator Role can edit Deductions of this type"/> Yes
										
									</td>
								</tr>
                                <c:if test="${roleBean.subeb}">
                                <tr>
									<td align="right" width="35%" nowrap>
										<span class="required" title="Indicates that Deductions of this type should be reported As 'Union Dues'">Union Dues Type*</span></td>
									<td width="25%" nowrap>
									    <form:radiobutton path="unionDueInd" value="0" title="Selecting this value allows this Deduction Type NOT to be reported as 'Union Dues'"/>No <form:radiobutton path="unionDueInd" value="1" title="Selecting this value allows this Deduction Type to be reported as 'Union Dues'"/> Yes
								</tr>
								</c:if>
								<tr style="${deductionTypeBean.showForConfirm}">
									<td align="right" width="35%" nowrap>
										<span class="required">Deduct as*</span>
									</td>
									<td width="25%">
										<form:select path="empDeductPayTypeRef">
											<form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<c:forEach items="${payType}" var="paytypeList">
													<form:option value="${paytypeList.id}">${paytypeList.name}</form:option>
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
									<c:if test="${deductionTypeBean.showApportionsRows}">
									<fieldset>
                                    	<legend><b>Apportionment Details</b></legend>
                                    <tr>
                                    <td align="right" width="35%" nowrap></td>
                                    <td width="15%"><font color="grey">Name</font></td>
                                    </tr>
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
													<form:input path="accountNumber" size="10" maxlength="10" />
												</td>
											</tr>
										<tr style="${deductionTypeBean.showForConfirm}">
												<td align="right" width="35%" nowrap>
													<span class="required">Confirm Account Number</span>
												</td>
												<td width="25%">
													<form:input path="confirmAccountNumber" size="10" maxlength="10" />
												</td>
										</tr>
					           </table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
							<c:choose>
								<c:when test ="${saved}">
									<input type="image" name="_cancel" value="cancel" title="Close" src="images/close.png">
								</c:when>
								<c:otherwise>
								<input type="image" name="submit" value="ok" alt="Ok" class="" src="images/ok_h.png">&nbsp;
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