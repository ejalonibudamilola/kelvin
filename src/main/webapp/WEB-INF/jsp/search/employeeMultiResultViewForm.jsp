<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>
    <head>
        <title>
            Multiple Results Page
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
		<script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
    </head>
    <style>
       #emp thead tr th{
          font-size:8pt !important;
       }
    </style>
    <body class="main">
   
    <form:form modelAttribute="miniBean">
	<c:set value="${miniBean.objectList}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    Multiple <c:out value="${roleBean.staffTypeName}"/> Found.<br>
                                    
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                                   
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">

                                    <tr>
                                        <td>
                                            <a href='${appContext}/${miniBean.searchAgainUrl}'><i>Search again</i></a>
                                        </td>
                                       
                                    </tr>
                                    <tr>
                                        <td>
                                            &nbsp;                                    
                                        </td>
                                       
                                    </tr>
                                    <tr>
                                        <td valign="top">
											<p class="label"> <c:out value="${miniBean.pageSize}" /> <c:out value="${roleBean.staffTypeName}"/>s found please select</p>
											<display:table name="dispBean" id="empTbl" class="display table"  export="false" sort="page" defaultsort="1" requestURI="${appContext}/viewMultiEmployeeResults.do">
											<display:setProperty name="export.rtf.filename" value="EmployeeSearchList.rtf"/>
											<display:setProperty name="export.excel.filename" value="EmployeeSearchList.xls"/>
											<display:column property="employeeId" title="${roleBean.staffTitle}" media="html" href="${appContext}/${miniBean.urlName}" paramId="eid" paramProperty="id"></display:column>
											<display:column property="employeeId" title="${roleBean.staffTitle}" media="excel"/>
											<display:column property="displayName" title="${roleBean.staffTypeName} Name"></display:column>
											<display:column property="currentMdaName" title="${roleBean.mdaTitle}"></display:column>
											<display:column property="salaryTypeName" title="Pay Group"></display:column>
										    <display:column property="salaryInfo.levelStepStr" title="Level & Step"></display:column>
										</display:table>
										</td>
                                    </tr>
                                    <tr>
                                        <td>&nbsp;</td>
                                    </tr>
                                    <tr>
                                     	<td>
                                          <a href='${appContext}/${miniBean.searchAgainUrl}'><i>Search again</i></a>
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
        <script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
        <script type="text/javascript">
           $(function() {
             $("#empTbl").DataTable({
                "order" : [ [ 1, "asc" ] ]
             });
           });
        </script>
    </body>
</html>
