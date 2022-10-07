<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Configuration and Control  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">

<style type="text/css">
.style3 {
	text-decoration: underline;
	color: #0000FF;
}
</style>

</head>

<body class="main">
    <form:form modelAttribute="adminBean">
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
	<tr>
		<td colspan="2">
		<div class="title">
			Configuration and Control</div>
		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">
		<table width="95%">
			<tr>
				<td valign="top">
				<table border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td width="45%" valign="top">
						<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
							<tr>
								<td colspan="50" class="infoTH">Variable Configurations</td>
							</tr>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/alterRba.do">Alter RBA Value</a><br />
								Alter the value of Redemption Bond Account for Ogun State IPPMS</td>
							</tr>
							 
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/createSalaryType.do">Create New Pay Group</a><br />
								Create a new Pay Group for civil servants in Ogun State <br /></td>
							</tr>
							 <!--
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/alterPensions.do">Increase/Decrease Pensions</a><br />
								Increase/Decrease Pensions Paid by civil servants in Ogun<br /></td>
							</tr>
						  -->
						</table>
						</td>
					</tr>
					<tr>
						<td>
						<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
							
							<tr>
						<td colspan="50" class="infoTH">Model Configurations</td>
						</tr>
						<tr>
							<td class="infoTREven">
							<a href="${appContext}/addNewBank.do">Add New Bank</a><br />
								  Add a new Bank that can support ePayment. <br />
							</td>
						</tr>
						<tr>
							<td class="infoTROdd">
							<a href="${appContext}/viewAllBanks.do">Edit Bank</a><br />
							 Edit an existing Bank object.<br />
							</td>
						</tr>
						<tr>
							<td class="infoTREven">
							<a href="${appContext}/addNewBankBranch.do">Add New Bank Branch</a><br />
								  Add a new Bank Branch that can support ePayment. <br />
							</td>
						</tr>
						<tr>
							<td class="infoTROdd">
							<a href="${appContext}/viewAllBankBranches.do">Edit Bank Branch</a><br />
							 Edit an existing Bank Branch.<br />
							</td>
						</tr>
						<tr>
							<td class="infoTREven">
							<a href="${appContext}/mergeBankBranch.do">Merge Bank Branches</a><br />
								  Merge bank branches of the same bank . <br />
							</td>
						</tr>
						<!--<tr>
							<td class="infoTREven">
							<a href="${appContext}/addRespAllowance.do">Add Responsibility Allowance</a><br />
								  Add new Responsibility Allowance. <br />
							</td>
						</tr>
						<tr>
							<td class="infoTROdd">
							<a href="${appContext}/viewAllRespAllowance.do">Edit Responsibility Allowance</a><br />
							 Edit an existing Responsibility Allowance.<br />
							</td>
						</tr>
						--><tr>
							<td class="infoTROdd">
							<a href="${appContext}/addNewRelationshipType.do">Add Relationship Type</a><br />
								  Add new Relationship Type. <br />
							</td>
						</tr>
						<tr>
							<td class="infoTREven">
							<a href="${appContext}/viewRelationshipTypes.do">Edit Relationship Type</a><br />
							 Edit an existing Relationship Type.<br />
							</td>
						</tr>
						</table>
						</td>
					</tr>
					
				</table>
				</td>
				<td width="5%">&nbsp;</td>
				<td width="45%" valign="top">
				<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
					<tr>
								<td colspan="50" class="infoTH">Deductions/Loans Configuration</td>
							</tr>
							<tr>
									<td class="infoTREven">
									<a href="${appContext}/createDeductionType.do" title="Create a new Deduction Type.">New Deduction Type</a><br />
									Create a new Deduction Type.<br /></td>
							</tr>
								
							<tr>
								<td class="infoTREven">
								   <a href="${appContext}/viewDeductionTypes.do" title="Edit an existing Deduction Type">Edit Deduction Type 
										</a><br />
								  Edit an existing Deduction Type</td>
							</tr>
							<tr>
								<td class="infoTROdd">
								 <a href="${appContext}/createGarnishmentType.do" title="Create a new Garnishment Type.">New Loan Type </a><br />
								  Create a new Loan Type. </td>
							</tr>
							
							<tr>
								<td class="infoTREven">
								   <a href="${appContext}/viewGarnishTypes.do" title="Edit an existing Garnishment Type">Edit Loan Type 
										</a><br />
								  Edit an existing Loan Type</td>
							</tr>
							<tr>
								<td class="infoTROdd">
								 <a href="${appContext}/createSpecAllowType.do" title="Create a new Special Allowance Type.">New Special Allowance Type </a><br />
								  Create a new Special Allowance Type. </td>
							</tr>
							
							<tr>
								<td class="infoTREven">
								   <a href="${appContext}/viewSpecAllowTypes.do" title="Edit an existing Special Allowance Type">Edit Special Allowance Type 
										</a><br />
								  Edit an existing Special Allowance Type</td>
							</tr>
					<!--  
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/addState.do">Add New State</a><br />
						 Add a new State to the IPPMS application.<br />
						</td>
					</tr>  
					
					<tr>
						<td class="infoTROdd">
						<a href="${appContext}/editState.do">Edit State</a><br />
						 Edit an existing State in the IPPMS application.<br />
						</td>
					</tr>
					<tr>
						<td class="infoTREven">
							<a href="${appContext}/addLga.do">Add New L.G.A</a><br />
								Add a new Local Government Area to the IPPMS application. <br />
						</td>
					</tr>                         
					<tr>
						<td class="infoTROdd">
						<a href="${appContext}/searchEmpForPromotion.do">Edit L.G.A</a><br />
						Edit an existing Local Government Area in the IPPMS application. <br />
						</td>
					</tr>
					-->
					<tr>
						  <td class="infoTROdd">
								&nbsp;<br>
								&nbsp;<br>
						  </td>
						</tr>	
					<tr>
						<td>
						<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
							<tr>
								<td colspan="50" class="infoTH">Pension Model Configurations</td>
							</tr>
							
							<tr>
								<td class="infoTROdd">
									<a href="${appContext}/createEditPfc.do">Create a new PFC 
								</a><br />
								Create a new PFC. </td>
							</tr>
							<tr>
								<td class="infoTREven">
									<a href="${appContext}/listPfcPfa.do">Edit PFC
								</a><br />
								Edit an existing PFC. </td>
							</tr>
							<tr>
								<td class="infoTROdd">
									<a href="${appContext}/createEditPfa.do">Create a new PFA
								</a><br />
								Create a new PFA. </td>
							</tr>
							<tr>
								<td class="infoTREven">
									<a href="${appContext}/listPfcPfa.do?pfa=t">Edit PFA
								</a><br />
								Edit an existing PFA. </td>
							</tr>
							
						</table>
						</td>
					</tr>
									
				</table>
				</td>
			</tr>
			
		</table>
		
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
