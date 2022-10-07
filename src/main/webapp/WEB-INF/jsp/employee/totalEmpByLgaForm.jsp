<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
<head>
<title> <c:out value="${roleBean.staffTypeName}"/> View By LGA  </TITLE>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon"  type="image/png"  href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
<link rel="stylesheet" href="css/screen.css" type="text/css"/>
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
<link rel="stylesheet" href="styles/spinner.css" TYPE="text/css" />
</head>

<style>
   #totalEmp thead tr th{
      font-size:8pt !important;
   }
</style>
<body class="main">

	<table class="main" width="70%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>

			<table  width="100%" align="center" border="0" cellpadding="0" cellspacing="0" height="550px">
			
	<tr>
		<td colspan="2">
		<div class="title">
			Total <c:out value="${roleBean.staffTypeName}"/>s By Local Government Area</div>
		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">
		
			<table width="650" cellspacing="0" cellpadding="0">
				<tr>
					<td>
							&nbsp;
					</td>
				</tr>
			</table>
			   <form:form modelAttribute="miniBean">
 					<c:set value="${miniBean}" var="dispBean" scope="request"/>
				    <table class="register" cellspacing="1" cellpadding="3">
				
                        <display:table name="dispBean" id="totalEmp" class="display table" export="false" requestURI="${appContext}/empByLgaForm.do" pagesize="${miniBean.pageSize}">
                                <display:caption><c:out value="${roleBean.staffTypeName}"/> Totals By Local Goverment Area</display:caption>
                                <display:setProperty name="export.rtf.filename" value="TotalEmployeeByLGA.rtf"/>
                                <display:setProperty name="export.excel.filename" value="TotalEmployeeByLGA.xls"/>
                                <display:column property="name" title="Local Government Area" media="html"  href="${appContext}/selectLGAForm.do" paramId="lgi" paramProperty="id"></display:column>
                                <display:column property="name" title="Local Government Area" media="excel"/>
                                <display:column property="totalElements" title="No. Of ${roleBean.staffTypeName}s" ></display:column>
                                <display:column property="placeHolder" title="Percentage" ></display:column>
                                <display:setProperty name="paging.banner.placement" value="" />
                        </display:table>
			        </table>
		       ,</form:form>
<a href="${appContext}/totalEmpByLgaExcel.do">View in Microsoft Excel&copy; </a><br />

		<br/>
		<br/>
		</td>

		<tr>
                                    			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
        </tr>


	</tr>
	</table>

</body>

<div class="spin"></div>
    <script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
    <script>
                $(function() {
                   $("#totalEmp").DataTable({
                      "order" : [ [ 1, "asc" ] ],
                      //can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
                      //also properties higher up, take precedence over those below
                      "columnDefs":[
                         {"targets": [0], "orderable" : false}
                      ]
                   });
                });

                $("#updateReport").click(function(e){
                   $(".spin").show();
                });
    </script>

</html>

