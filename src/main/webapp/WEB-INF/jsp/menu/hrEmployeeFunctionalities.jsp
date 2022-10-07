<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> HR <c:out value="${roleBean.staffTypeName}"/> Functionalities Main Page  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
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

	<table class="main" width="74%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">


<%@ include file="/WEB-INF/jsp/headerFile.jsp"%>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td colspan="2">
		<div class="title">
			HR <c:out value="${roleBean.staffTypeName}"/> Functionalities Overview</div>
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
							<c:if test="${not roleBean.pensioner}">
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/searchEmpForPromotion.do">Promote <c:out value="${roleBean.staffTypeName}"/></a><br />
								Promote an existing <c:out value="${roleBean.staffTypeName}"/>. <br />
								</td>
							</tr>
							</c:if>
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/searchEmpForSuspension.do">Suspend/Stop <c:out value="${roleBean.staffTypeName}"/> Salary</a><br />
								Temporarily Stop <c:out value="${roleBean.staffTypeName}"/> Salary.<br />
								</td>
							</tr>  
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/searchEmpForTermination.do">Terminate <c:out value="${roleBean.staffTypeName}"/></a><br />
								Terminate an existing <c:out value="${roleBean.staffTypeName}"/>.<br />
								</td>
							</tr> 
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/searchForEmpToReinstate.do">Reinstate <c:out value="${roleBean.staffTypeName}"/></a><br />
								Reinstate a Terminated <c:out value="${roleBean.staffTypeName}"/>.<br />
								</td>
							</tr>  
							<tr>
								<td class="infoTREven">
									<a href="${appContext}/searchEmpForSuspension.do?reab=y">Reabsorb <c:out value="${roleBean.staffTypeName}"/></a><br />
										Reabsorb an <c:out value="${roleBean.staffTypeName}"/>. <br />
								</td>
							</tr> 
							<c:if test="${not roleBean.pensioner}">
							<tr>
								<td class="infoTROdd">
								<a href="${appContext}/searchForEmpForm.do">Demotion/Pay Group Change</a><br />
								Demote or change the Pay Group of an <c:out value="${roleBean.staffTypeName}"/><br />
								</td>
							</tr>   
							</c:if>
							<tr>
								<td class="infoTREven">
								<a href="${appContext}/searchEmpForTransfer.do">Transfer <c:out value="${roleBean.staffTypeName}"/></a><br />
								Transfer <c:out value="${roleBean.staffTypeName}"/> between <c:out value="${roleBean.mdaTitle}"/><br />
								</td>
							</tr>     
							
						</table>
						</td>
					</tr>
				
					
				</table>
				</td>
		
				
			</tr>
		</table>
		</td>
		</tr>
		</table>
		</td>
		</tr>
		            
           <tr>				

			<%@ include file="/WEB-INF/jsp/footerFile.jsp"%>
</tr>
	</table>

</body>

</html>
