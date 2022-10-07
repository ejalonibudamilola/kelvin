<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title>Mass Entry Dashboard  </title>
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
	<table class="main" width="74%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">


<%@ include file="/WEB-INF/jsp/headerFile.jsp"%>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td colspan="2">
		<div class="title">
			Mass Entry Dashboard</div>
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
								<td colspan="50" class="infoTH">Mass Entry</td>
							</tr>
							<c:if test="${adminBean.canDoDeductions}">
							 <tr>
								<td class="infoTREven">
								<a href="${appContext}/massDeductionEntry.do">Deductions</a><br />
								 Mass Assign Deductions to <c:out value="${roleBean.staffTypeName}"/>&apos;s<br />
								</td>
							</tr>
							</c:if>
							<c:if test="${adminBean.canDoLoans}">
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/massLoanEntry.do">Loans/Garnishments</a><br />
								Mass Assign Loans to <c:out value="${roleBean.staffTypeName}"/>&apos;s<br />
								</td>
							</tr>
							</c:if>
							<c:if test="${adminBean.canDoPromotions}">
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/massPromotions.do">Promotions</a><br />
								 Mass Promote <c:out value="${roleBean.staffTypeName}"/>&apos;s to the same Pay Group, Level &amp; Step<br />
								</td>
							</tr>
							</c:if>
							<c:if test="${adminBean.canDoSpecAllow}">
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/massSpecialAllowance.do">Special Allowances</a><br />
								Mass Assign Special Allowances to <c:out value="${roleBean.staffTypeName}"/>&apos;s<br />
								</td>
							</tr>
							</c:if>
		                   <c:if test="${adminBean.canDoTransfers}">
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/massTransfer.do">Transfer</a><br />
								 Mass Transfer <c:out value="${roleBean.staffTypeName}"/>&apos;s <br />
								</td>
							</tr>
							</c:if>
							<c:if test="${adminBean.canDoEmail}">
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/massEmailPayslips.do">Email Payslips</a><br />
								Mass Email  <c:out value="${roleBean.staffTypeName}"/> Payslips <br />
								</td>
							</tr>
                            </c:if>
						</table>
						</td>
					</tr>
					<tr>
						<td>

						</td>
					</tr>
					
				</table>
				</td>
				<td width="5%">&nbsp;</td>
				<td width="45%" valign="top">
				
				</td>
			</tr>
			
		</table>
		
	</table>
</td>
</tr>
            
           <tr>				

			<%@ include file="/WEB-INF/jsp/footerFile.jsp"%>
</tr>
</table>
</form:form>
</body>

</html>
