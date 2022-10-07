<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title><c:out value="${roleBean.staffTypeName}"/> Functions Main Page </title>
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
		 <c:out value="${roleBean.staffTypeName}"/> Functions Overview</div>
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
								<td colspan="50" class="infoTH"><c:out value="${roleBean.staffTypeName}"/> Functions</td>
							</tr>
							<tr>
						<td class="infoTROdd">
						<a href="${appContext}/searchEmpForEnquiry.do"><c:out value="${roleBean.staffTypeName}"/> Enquiry</a><br />
						 Enquiry View of an existing <c:out value="${roleBean.staffTypeName}"/>. <br />
						</td>
					</tr>
					<tr>
						<td class="infoTREven">
						<a href="${appContext}/searchEmpForEdit.do">Edit <c:out value="${roleBean.staffTypeName}"/></a><br />
						Edit an existing <c:out value="${roleBean.staffTypeName}"/>. <br />
						</td>
					</tr>
					<tr>
						<td class="infoTROdd">
						<c:choose>
						<c:when test="${roleBean.pensioner}">
                            <a href="${appContext}/searchPensionerToCreate.do">Add <c:out value="${roleBean.staffTypeName}"/></a><br />
						</c:when>
						<c:otherwise>
                        <a href="${appContext}/createNewEmployee.do">Add <c:out value="${roleBean.staffTypeName}"/></a><br />
						</c:otherwise>
						</c:choose>
						 Add a new <c:out value="${roleBean.staffTypeName}"/>.<br />
						</td>
					</tr>
					
					
					<tr>
								<td class="infoTROdd">
									<a href="${appContext}/searchEmpForTransfer.do">Transfer <c:out value="${roleBean.staffTypeName}"/></a><br />
									Transfer an existing <c:out value="${roleBean.staffTypeName}"/> between <c:out value="${roleBean.mdaTitle}"/>
									<c:if test="${not roleBean.pensioner and not roleBean.localGovt}"> or Schools  </c:if>. <br />
									</td>
					</tr>
					
					<tr>
						  <td class="infoTREven">
								<a href="${appContext}/busEmpOverviewForm.do">View All <c:out value="${roleBean.staffTypeName}"/></a><br />
								View all <c:out value="${roleBean.staffTypeName}"/><br /></td>
					</tr>
					 <c:if test="${roleBean.pensioner}">
                     <tr>
                    		 <td class="infoTROdd">
                    		 <a href="${appContext}/viewEmpDueForRetirement.do"><c:out value="${roleBean.staffTypeName}"/>s Due for 'I am Alive' Retirement</a><br />
                    	 View <c:out value="${roleBean.staffTypeName}"/>s due for 'I am Alive' retirement this month.<br /></td>
                     </tr>
                     </c:if>
						</table>
						</td>
					</tr>
			
				</table>
				</td>
				<c:if test="${not roleBean.pensioner}">
				<td width="5%">&nbsp;</td>
				<td width="45%" valign="top">
				<table width="100%" cellpadding="0" cellspacing="0" border="0" class="infoBox" id="pcbox0">
				<tr>
								<td colspan="50" class="infoTH"><c:out value="${roleBean.staffTypeName}"/> Functions</td>
							</tr>

							<tr>
								<td class="infoTREven">
								<a href="${appContext}/searchEmpForPromoHistory.do"><c:out value="${roleBean.staffTypeName}"/> Promotion History</a><br />
								View <c:out value="${roleBean.staffTypeName}"/> Promotion History<br />
								</td>
							</tr>   	
							
				<tr>
						  <td class="infoTREven">
								<a href="${appContext}/searchEmpForContract.do">Create <c:out value="${roleBean.staffTypeName}"/> Contract</a><br />
								Put <c:out value="${roleBean.staffTypeName}"/>(s) on Contract/Internship.<br />
						  </td>
					</tr>
					 

						<c:if test="${adminBean.runManualPromotion}">
						<tr>
						  <td class="infoTROdd">
								<a href="${appContext}/doManualStepPromotion.do">Run Yearly Step Promotion</a><br />
								Run Yearly Step Increases except at Bar.<br />
						  </td>
						</tr>
						</c:if>

				    
					<c:if test="${adminBean.empToBePromoted}">
						<tr>
						  <td class="infoTREven">
								<a href="${appContext}/viewEmpDueForPromotion.do"><c:out value="${roleBean.staffTypeName}"/>s Due for Promotion</a><br />
								View <c:out value="${roleBean.staffTypeName}"/>s due for promotion this month.<br /></td>
						</tr>
					</c:if>	

					  	<tr>
						  <td class="infoTREven">
								<a href="${appContext}/viewEmpDueForRetirement.do"><c:out value="${roleBean.staffTypeName}"/>s Due for Retirement</a><br />
								View <c:out value="${roleBean.staffTypeName}"/>s due for retirement this month.<br /></td>
					    </tr>


									
				</table>
				</td>
				</c:if>
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
