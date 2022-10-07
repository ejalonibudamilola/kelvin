<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            View All Relationship Types
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
    </head>
    <body class="main">
  
    <form:form modelAttribute="relTypeBean">
	<c:set value="${relTypeBean}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
           <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    Relationship Type List<br>
                                   
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                <div id="topOfPageBoxedErrorMessage" style="display:${relTypeBean.displayErrors}">
									<spring:hasBindErrors name="relTypeBean">
										<ul>
											<c:forEach var="errMsgObj" items="${errors.allErrors}">
											<li>
												<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
											</li>
											</c:forEach>
										</ul>
									</spring:hasBindErrors>
								</div>                              
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <td class="">
                                            &nbsp;                                       
                                        </td>
                                       
                                    </tr>
                                    <tr>
                                        <td valign="top">
											<p class="label">All Relationship Types</p>
											<display:table name="dispBean" class="register2" export="true" sort="page" defaultsort="1" requestURI="${appContext}/viewRelationshipTypes.do">
											
											<display:column property="name" title="Name" media="html" href="${appContext}/editRelationType.do" paramId="rtid" paramProperty="id"></display:column>
											<display:column property="name" title="Name" media="excel"></display:column>
										    <display:column property="lastModBy" title="Last Modified By"></display:column>
										    <display:column property="lastModTs" title="Last Modified Date"></display:column>
											<display:setProperty name="paging.banner.placement" value="bottom" />
										</display:table>
										</td>
                                    </tr>
                                    <tr>
                                        
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
    </body>
</html>
