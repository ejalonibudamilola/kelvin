<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp"%>

<html>
	<head>
			<title>${pageTitle}</title>
			<link rel="stylesheet" href="styles/omg.css" type="text/css" />
			<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
			<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css" />
			<link rel="stylesheet" href="css/screen.css" type="text/css" />
	</head>

<body class="main">
	<table class="main" width="74%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">
	
		<%@ include file="/WEB-INF/jsp/headerFile.jsp"%>
		
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

					<tr>
						<td>
							<div class="title">${mainHeader}</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody hundredPercentWidth verticalTopAlign" id="mainbody">
							<div style="margin-bottom: 10px;">
								<a class="linkWithUnderline" href="<c:url value="/editRoleMenuLinks.do"/>">
									<spring:message code="roleMenuLink.view.addNew"/>
								</a>
								<c:if test="${tableModel.canViewMenuLinks}">
								<span class="spacerFivePix"></span> 
								<a class="linkWithUnderline" href="<c:url value="/viewMenuLinks.do"/>">
									<spring:message code="menuLinkCat.view.viewMenuLinks"/>
								</a>
								</c:if>
								<c:if test="${tableModel.canViewMenuLinkCategories}">
								<span class="spacerFivePix"></span> 
								<a class="linkWithUnderline" href="<c:url value="/viewMenuLinkCategories.do"/>">
									<spring:message code="menuLink.view.viewMenuLinkCat"/>
								</a>
								</c:if>
							</div>
							
							<div>
								<display:table name="tableModel" requestURI="${appContext}/viewRoleMenuLinks.do" 
									class="displayTable eightyPercentWidth">
									<display:setProperty name="paging.banner.placement" value="bottom" />
									<display:caption><spring:message code="roleMenuLink.view.table.caption"/></display:caption>
									
									<display:column property="serialNo" titleKey="table.serialNo" sortable="false" class="tenPercentWidth"/>
									
									<display:column property="role.name" titleKey="roleMenuLink.view.table.role" 
										href="${appContext}/editRoleMenuLinks.do" 
										class="thirtyPercentWidth" paramId="roleId" paramProperty="role.id" sortable="true"/>
										
									<display:column property="description" titleKey="roleMenuLink.view.table.desc" sortable="false" />
								</display:table>
							</div>	
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