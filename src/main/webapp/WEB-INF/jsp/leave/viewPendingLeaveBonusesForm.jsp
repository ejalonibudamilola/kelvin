<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            Leave Bonuses Awaiting Approval
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
         
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
    </head>
    <body class="main">
   
    <form:form modelAttribute="miniBean">
	<c:set value="${miniBean}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    Pending Employee Leave Bonus
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                <div id="topOfPageBoxedErrorMessage" style="display:${miniBean.displayErrors}">
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
                                <table class="reportMain" cellpadding="1" cellspacing="1" width="90%">
                                     <tr>
                                     	<td>&nbsp;</td>
                                    </tr>
                                    <tr>
                                        <td valign="top">
											<p class="label">Pending Leave Bonuses</p>
											<display:table name="dispBean" class="register3" export="true" sort="page" defaultsort="1" requestURI="${appContext}/viewPendingLeaveBonus.do">
											<display:setProperty name="export.rtf.filename" value="PendingLeaveBonusList.rtf"/>
											<display:setProperty name="export.excel.filename" value="PendingLeaveBonusList.xls"/>
											<display:column property="mdaName" title="MDA" media="html" href="${appContext}/approveLeaveBonus.do" paramId="pid" paramProperty="id"></display:column>
											<display:column property="mdaName" title="MDA" media="excel"></display:column>
											<display:column property="runMonthYearStr" title="Month, Year" media="excel"/>
											<display:column property="createdBy.actualUserName" title="Created By"></display:column>									
											<display:column property="totalNoOfEmp" title="No. of Staff"></display:column>
											<display:column property="totalLeaveBonusStr" title="Total Leave Bonus"></display:column>
										    <display:column property="details" title="" media="html" href="${appContext}/viewLeaveBonusDetails.do" paramId="pid" paramProperty="id"></display:column>
										    <display:column property="delete" title="" media="html" href="${appContext}/deleteLeaveBonus.do" paramId="pid" paramProperty="id"></display:column>
											<display:setProperty name="paging.banner.placement" value="bottom" />
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
