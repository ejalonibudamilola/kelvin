<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            <c:out value="${miniBean.displayTitle}"/>
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">

    </head>
    <style>
       #conflicttbl thead tr th{
          font-size:8pt !important;
       }
    </style>
    <body class="main">
   
    <form:form modelAttribute="miniBean">
	<c:set value="${displayList}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
           <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    <c:out value="${miniBean.displayTitle}"/>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                <div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
									<spring:hasBindErrors name="miniBean">
										<ul>
											<c:forEach var="errMsgObj" items="${errors.allErrors}">
											<li>
												<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
											</li>
											</c:forEach>
										</ul>
									</spring:hasBindErrors>
								</div> 
								             
                                <table class="reportMain" cellpadding="1" cellspacing="1" width="100%">
                                    
                                    <tr>
                                        <td valign="top">
											<p class="label">Treated Name Conflicts</p>
											<display:table name="dispBean" id="conflicttbl" class="display table" export="false" sort="page" defaultsort="1" requestURI="${appContext}/viewTreatedNameConflicts.do">
											<display:setProperty name="export.rtf.filename" value="TreatedEmployeesWithNameConflictList.rtf"/>
											<display:setProperty name="export.excel.filename" value="TreatedEmployeesWithNameConflictList.xls"/>
											<display:column property="employeeId" title="${roleBean.staffTitle}" media="html" href="${appContext}/setUpEmpErrorForm.do" paramId="mid" paramProperty="id"></display:column>
											<display:column property="employeeId" title="${roleBean.staffTitle}" media="excel"/>
											<display:column property="lastName" title="Last Name"></display:column>	
											<display:column property="firstName" title="First Name"></display:column>
											<display:column property="initials" title="Middle Name"></display:column>								
											<display:column property="mdaName" title="${roleBean.mdaTitle}"></display:column>
											<display:column property="payGroup" title="Pay Group"></display:column>		
										   <display:column property="lastModBy" title="Initated By"></display:column>	
										    <display:column property="initiatedDateStr" title="Created Date"></display:column>
										    <display:column property="approvedBy" title="Treated By"></display:column>
										    <display:column property="approvedDateStr" title="Treated Date"></display:column>
										    <display:column property="finalStatus" title="Status"></display:column>
											<display:setProperty name="paging.banner.placement" value="" />
										</display:table>
										</td>
                                    </tr>
                                    <tr>
                                        
                                    </tr>
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                       <input type="image" name="_cancel" value="cancel" title="Cancel" src="images/close.png">
                                        </td>
                                    </tr>
                                </table>
                                <c:if test="${miniBean.showLink}">


                                                                   		<div class="reportBottomPrintLink">
                                											<a href="${appContext}/viewTreatedNameConflictExcelReport.do">
                                										View All Results in Excel&copy; </a><br />
                                			                        </div>
                                		                        </c:if>
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
              $("#conflicttbl").DataTable({
                 "order" : [ [ 1, "asc" ] ]
              });
           });
        </script>
    </body>
</html>
