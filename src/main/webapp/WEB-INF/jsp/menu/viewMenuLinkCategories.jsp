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
	<table class="main" width="74%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
	
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
								<a class="linkWithUnderline" href="<c:url value="/editMenuLinkCategory.do"/>">
									<spring:message code="menuLinkCat.view.addNew"/>
								</a>
								
								<span class="spacerFivePix"></span> 
								<a class="linkWithUnderline" href="<c:url value="/viewMenuLinks.do"/>" title="View Existing Menus">
									<spring:message code="menuLinkCat.view.viewMenuLinks"/>
								</a>
								
								<span class="spacerFivePix"></span> 
								<a class="linkWithUnderline" href="<c:url value="/viewRoleMenuLinks.do"/>">
									<spring:message code="menuLinkCat.view.viewRoleMenuLinks"/>
								</a>
							</div>
							
							<div>
								<display:table name="tableModel" requestURI="${appContext}/viewMenuLinkCategories.do" 
									class="displayTable eightyPercentWidth">
									<display:setProperty name="paging.banner.placement" value="bottom" />
									<display:caption><spring:message code="menuLinkCat.view.table.caption"/></display:caption>
									
									<display:column property="serialNo" titleKey="table.serialNo" sortable="false" class="tenPercentWidth"/>
									
									<display:column property="name" titleKey="menuLinkCat.view.table.name" href="${appContext}/editMenuLinkCategory.do" 
										class="thirtyPercentWidth" paramId="catId" paramProperty="id" sortable="true"/>
									
									<display:column property="displayOnlyOnDbTabs" titleKey="menuLinkCat.view.table.dispOnDbTabsOnly" 
										sortable="true" class="tenPercentWidth"/>	
									<display:column property="description" titleKey="menuLinkCat.view.table.desc" sortable="false" />
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