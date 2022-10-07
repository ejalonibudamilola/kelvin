<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            View All Banks
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
    </head>
    <body class="main">
    <form:form modelAttribute="bankBean">
	<c:set value="${bankList}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    Banks List<br>
                                   
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                <div id="topOfPageBoxedErrorMessage" style="display:${bankBean.displayErrors}">
									<spring:hasBindErrors name="bankBean">
										<ul>
											<c:forEach var="errMsgObj" items="${errors.allErrors}">
											<li>
												<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
											</li>
											</c:forEach>
										</ul>
									</spring:hasBindErrors>
								</div>  
								<table>
								<tr align="left">
										<td class="activeTH" colspan="2">Filter By</td>
										
									</tr>
                                    
                                    <tr>
                                      
                                       <td class="activeTD">
                                             Bank Name :                                          
                                        </td>
                                        <td class="activeTD">
                                            <form:input path="name" size="20" maxlength="30"/> 
                                        </td>
                                    </tr>
                                    
                                    <tr>
                                       <td class="activeTD">
                                            Sort Code :                                          
                                        </td>
                                        <td class="activeTD">
                                            <form:input path="sortCode" size="5" maxlength="10"/>
                                        </td>
                                    </tr>
									<tr>
                                    	<td class="buttonRow" align="right">
                                    		 <input type="image" name="_update" value="update" title="Update Report" src="images/Update_Report_h.png">
                                        
                                    	</td>
                                    </tr>
								</table>                            
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <td class="">
                                            &nbsp;                                       
                                        </td>
                                       
                                    </tr>
                                    
									
								 
                                    <tr>
                                        <td valign="top">
											<p class="label">All Banks List</p>
											<display:table name="dispBean" id="banktbl" class="display table" export="" sort="page" defaultsort="1" requestURI="${appContext}/viewAllBanks.do">
											<display:setProperty name="export.rtf.filename" value="AllBanksList.rtf"/>
											<display:setProperty name="export.excel.filename" value="AllBanksList.xls"/>
											<display:column property="name" title="Name" media="html" href="${appContext}/editBank.do" paramId="bid" paramProperty="id"></display:column>
											<display:column property="name" title="Name" media="excel"></display:column>
											<display:column property="sortCode" title="Sort Code"></display:column>										
											<display:column property="description" title="Description"></display:column>
										    <display:column property="lastModBy.actualUserName" title="Last Modified By"></display:column>
										    <display:column property="lastModTs" title="Last Modified Date"></display:column>
											<display:setProperty name="paging.banner.placement" value="" />
										</display:table>
										</td>
                                    </tr>
                                    <tr>
                                        
                                    </tr>
                                    <tr>
										<td class="buttonRow" align="right" >
										   
											<input type="image" name="_cancel" value="cancel" title="Close" class="" src="images/close.png">
											     
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
                $("#banktbl").DataTable({
                    "order" : [ [ 1, "asc" ] ]
                });
            });
        </script>
    </body>
</html>
