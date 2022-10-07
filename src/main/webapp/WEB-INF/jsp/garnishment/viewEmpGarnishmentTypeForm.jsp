<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            Garnishment Types
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
        <link rel="stylesheet" href="styles/spinner.css" TYPE="text/css" />
    </head>
    <body class="main">
    <div class="loader"></div>
    <form:form modelAttribute="garnishTypeBean">
	<c:set value="${displayList}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    <c:out value="${roleBean.staffTypeName}"/> Loan Types<br>
                                   
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                <div id="topOfPageBoxedErrorMessage" style="display:${garnishTypeBean.displayErrors}">
									<spring:hasBindErrors name="garnishTypeBean">
										<ul>
											<c:forEach var="errMsgObj" items="${errors.allErrors}">
											<li>
												<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
											</li>
											</c:forEach>
										</ul>
									</spring:hasBindErrors>
								</div> 
								<!--<table>
								<tr align="left">
										<td class="activeTH" colspan="2">Filter By</td>
										
									</tr>
                                    
                                    <tr>
                                      
                                       <td class="activeTD">
                                             Loan Type Description :                                          
                                        </td>
                                        <td class="activeTD">
                                            <form:input path="name" size="20" maxlength="30"/> 
                                        </td>
                                    </tr>
                                    
                                    <tr>
                                       <td class="activeTD">
                                            Loan Type Name :                                          
                                        </td>
                                        <td class="activeTD">
                                            <form:input path="typeCode" size="7" maxlength="10"/>
                                        </td>
                                    </tr>
                                    <tr>
                                    	<td class="buttonRow" align="right">
                                    		 <input type="image" name="_update" value="update" title="Update Report" src="images/Update_Report_h.png">
                                        
                                    	</td>
                                    </tr>
								</table> -->
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <td class="">
                                            &nbsp;                                       
                                        </td>
                                       
                                    </tr>
                                    
                                    <tr>
                                        <td valign="top">
											<p class="label"><c:out value="${roleBean.staffTypeName}"/> Loan Types</p>
											<display:table name="dispBean" id="loanTbl" class="display table" export="" sort="page" defaultsort="1" requestURI="${appContext}/viewGarnishTypes.do">
											<display:setProperty name="export.excel.filename" value="EmployeeGarnishmentTypeList.xls"/>
											<display:column property="name" title="Name" media="html" href="${appContext}/editGarnishType.do" paramId="gtid" paramProperty="id"></display:column>
											<display:column property="name" title="Name" media="excel"/>
											<display:column property="description" title="Description"></display:column>
										    <display:column property="lastModBy.actualUserName" title="Last Modified By"></display:column>
										    <display:column property="lastModTsForDisplay" title="Last Modified Date"></display:column>
											<display:setProperty name="paging.banner.placement" value="bottom" />
										</display:table>
										</td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <input type="image" name="_cancel" value="cancel" title="Close" src="images/close.png">
                                        </td>
                                    </tr>
                                    <tr>
                                     	<td>
                                       		&nbsp;
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
                $("#loanTbl").DataTable({
                    "order" : [ [ 1, "asc" ] ]
                });
            });
            window.onload = function exampleFunction() {
               $(".loader").hide();
            }
        </script>
        </body>
</html>
