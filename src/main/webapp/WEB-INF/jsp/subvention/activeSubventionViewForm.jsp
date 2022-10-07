<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
<head>
<title>Active Subventions</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
<link rel="stylesheet" href="css/screen.css" type="text/css"/>
<link rel="stylesheet" href="css/datatables.min.css" type="text/css">
</head>
<style>
    #activeSVF thead tr th{
        font-size:8pt !important;
    }
    #activeSVF_filter input{
        height:23px;
        font-size:10pt;
    }
</style>

<body class="main">
	<form:form modelAttribute="subventionBean">
	<c:set value="${subventionBean}" var="dispBean" scope="request"/>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
			<tr>
				<td colspan="2">
					<div class="title">Active Subventions List</div>
				</td>
			</tr>
			<tr>
				<td valign="top">
				    <div style="margin-left:1%; margin-right:1%">
					    <p class="label">Active Subventions</p>
                        <display:table name="dispBean" class="display table" id="activeSVF" export="false" sort="page" defaultsort="1" requestURI="${appContext}/searchForSubvention.do">
                            <display:setProperty name="export.rtf.filename" value="ActiveSubventionsList.rtf"/>
                            <display:setProperty name="export.excel.filename" value="ActiveSubventionsList.xls"/>
                            <display:column property="name" title="Name" media="html" href="${appContext}/editSubvention.do" paramId="sid" paramProperty="id"></display:column>
                            <display:column property="name" title="Name" media="excel"></display:column>
                            <display:column property="amountAsStr" title="Amount"></display:column>
                            <display:column property="createdDateStr" title="Created Date"></display:column>
                            <display:column property="expirationDateStr" title="Expiration Date"></display:column>
                            <display:column property="createdBy.actualUserName" title="Created By"></display:column>
                            <display:setProperty name="paging.banner.placement" value="" />
                        </display:table>
					</div>
					<table cellspacing="0" cellpadding="4" class="condensedInfoBox">
     					<tr class="infoFooter">
							<td colspan=4>
								&nbsp; &nbsp; 
								  <a href="${appContext}/activeSubventionReportExcel.do">Download Active Subventions in Excel</a> &nbsp; &nbsp; <span class="tabseparator">|</span> &nbsp; &nbsp;<a href='${appContext}/viewInactiveSubventions.do'>Show Inactive Subventions</a>
							</td>
						</tr>
					</table>
					
				</td>
			</tr>
		</table>

			</td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
	</form:form>
	<script src="scripts/datatables.min.js"></script>
    <script>
        $('#activeSVF').DataTable( {
           lengthMenu: [10, 20, 30, 'All Records'],
           searching: true
        });

    </script>
</body>
</html>
