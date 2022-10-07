<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
	<head>
			<title>${pageTitle}</title>
			<link rel="stylesheet" href="styles/omg.css" type="text/css" />
			<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
			<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css" />
			<link rel="stylesheet" href="css/screen.css" type="text/css" />
			<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
			<script src="<c:url value="/scripts/jquery-1.8.3.min.js"/>"></script>
			<script src="<c:url value="/scripts/utility_script.js"/>"></script>
	</head>

<body class="main">
	<table class="main" width="74%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
	
		<%@ include file="/WEB-INF/jsp/headerFile.jsp"%>
		
		<tr>
			<td>
				<table class="alignLeft hundredPercentWidth" border="0" cellpadding="0" cellspacing="0">

					<tr>
						<td>
							<div class="title">${mainHeader}</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody hundredPercentWidth verticalTopAlign" id="mainbody">
							<div style="margin-bottom: 10px;">
								<a class="linkWithUnderline" href="<c:url value="/editMenuLink.do"/>"><spring:message code="menuLink.view.addNew"/></a>
								
								<span class="spacerFivePix"></span> 
								<a class="linkWithUnderline" href="<c:url value="/viewMenuLinkCategories.do"/>">
									<spring:message code="menuLink.view.viewMenuLinkCat"/>
								</a>
								
								<span class="spacerFivePix"></span> 
								<a class="linkWithUnderline" href="<c:url value="/viewRoleMenuLinks.do"/>">
									<spring:message code="menuLink.view.viewRoleMenuLink"/>
								</a>
							</div>
							
							<div>
								<display:table name="tableModel" requestURI="${appContext}/viewMenuLinks.do" class="displayTable ninetySevenPercentWidth" id="userTable">
								<display:setProperty name="paging.banner.placement" value="bottom" />
									<display:caption><spring:message code="menuLinkCat.view.table.caption"/></display:caption>
									

									    <thead id="sModel">
									      <tr>
									      <th><input type="text" size="2" name=serialNo class="search_init"/></th>
									       <th><input type="text" name="name" class="search_init"/></th>
										   <th><input type="text" name="parentMenuLink.name" class="search_init"/></th>
										   <th><input type="text" name="menuLinkCategory.name" class="search_init"/></th>
										   <th><input type="text" name="dashboardMenuLink" class="search_init"/></th>
										   <th><input type="text" name="description" class="search_init"/></th>
									      </tr>
									    </thead>

       
									<display:column property="serialNo" titleKey="table.serialNo" sortable="false" class="fivePercentWidth"/>
									
									<display:column property="name" titleKey="menuLink.view.table.name" href="${appContext}/editMenuLink.do" 
										class="fifteenPercentWidth" paramId="linkId" paramProperty="id" sortable="true"/>
										
									<display:column property="parentMenuLink.name" titleKey="menuLink.view.table.parent" 
										class="fifteenPercentWidth" sortable="true" />
									<display:column property="menuLinkCategory.name" titleKey="menuLink.view.table.category" 
										class="fifteenPercentWidth" sortable="true" />
										
									<display:column property="dashboardMenuLink" titleKey="menuLink.view.table.dbMenuLink" 
										sortable="true" class="tenPercentWidth"/>	
									
									<display:column property="description" titleKey="menuLink.view.table.desc" sortable="false" class="twentyPercentWidth"/>
									
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

	<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>

 <script type="text/javascript">
    var confirmed = false;
	    $(function () {
	        /* Search functionality */
	        var oTable = $('#userTable').dataTable({
	            "bPaginate":false,
	            "bLengthChange":false,
	            "bFilter":true,
	            "bSort":false,
	            "bInfo":false,
	            "bAutoWidth":false,
	            "bStateSave":false
	        });

	        
	        $("thead input").keyup(function () {
	            /* Filter on the column (the index) of this element */
	            oTable.fnFilter(this.value, $("thead input").index(this));
	        });

        	
	        $("thead input").focus(function () {
	            if (this.className == "search_init") {
	                this.className = "";
	                this.value = "";
	            }
	        });
	
	        $("thead input").blur(function (i) {
	            if (this.value == "") {
	                this.className = "search_init";
	                this.value = asInitVals[$("thead input").index(this)];
	            }
	        });
	    });
	</script>

</body>
</html>