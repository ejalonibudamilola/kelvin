<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            Leave Bonuses Reports By Dates
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
                                    Leave Bonuses Report
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
								<table>
									<tr align="left">
										<td class="activeTH" colspan="2">Filter By</td>
										
									</tr>
                                    
                                    <tr>
                                      
                                       <td class="activeTD">
                                             Year :                                          
                                        </td>
                                        <td class="activeTD">
                                            <form:select path="runYear">
						                		<c:forEach items="${yearList}" var="yList">
						                			<form:option value="${yList.salaryInfoInstId}">${yList.name}</form:option>
						                		</c:forEach>
						               		</form:select>
                                        </td>
                                    </tr>
									<tr>
                                    	<td class="buttonRow" align="right">
                                    		 <input type="image" name="_update" value="update" title="Update Report" src="images/Update_Report_h.png">
                                        
                                    	</td>
                                    </tr>
								</table>                                   
                                <table class="reportMain" cellpadding="1" cellspacing="1" width="90%">
                                     <tr>
                                     	<td>&nbsp;</td>
                                    </tr>
                                    <c:choose>
                                    	<c:when test="${miniBean.runYear eq 0 }">
                                    	<tr>
                                        <td valign="top">
											<p class="label">Leave Bonuses</p>
											<display:table name="dispBean" class="register2" export="false" sort="page" defaultsort="1" requestURI="${appContext}/preLeaveBonusReportForm.do">
											<display:column property="runYear" title="Year" media="html" href="${appContext}/preLeaveBonusReportForm.do" paramId="yid" paramProperty="runYear"></display:column>
											<display:column property="runYear" title="Year" media="excel"></display:column>
											<display:column property="totalNoOfEmp" title="No. of Staff"></display:column>
											<display:column property="totalLeaveBonusStr" title="Total Leave Bonus"></display:column>
											<display:setProperty name="paging.banner.placement" value="bottom" />
										</display:table>
										</td>
                                        </tr>
                                    	</c:when>
                                    	<c:otherwise>
                                    	<tr>
                                        <td valign="top">
											<p class="label">Leave Bonuses by year</p>
											<display:table name="dispBean" class="register2" export="false" sort="page" defaultsort="1" requestURI="${appContext}/preLeaveBonusReportForm.do">
											<display:column property="mdaInfo.name" title="MDA"></display:column>
											<display:column property="runMonthYearStr" title="Month, Year" media="excel"/>
											<display:column property="totalNoOfEmp" title="No. of Staff"></display:column>
											<display:column property="totalLeaveBonusStr" title="Total Leave Bonus"></display:column>
										    <display:column property="details" title="" media="html" href="${appContext}/viewLeaveBonusDetailsByMDA.do" paramId="pid" paramProperty="mode"></display:column>
											<display:setProperty name="paging.banner.placement" value="bottom" />
										</display:table>
										</td>
                                    </tr>
                                    	
                                    	</c:otherwise>
                                    
                                    </c:choose>
                                    
                                    <tr>
                                        
                                    </tr>

                                    <tr>
                                     	<td class="buttonRow" align="right">
                                       <input type="image" name="_cancel" value="cancel" title="Close Form" src="images/close.png">
                                        </td>
                                    </tr>
                                </table>
                                 <div class="reportBottomPrintLink">
                                <c:if test="${miniBean.showLink}">
                                <div class="reportBottomPrintLink">
                                   <a href="${appContext}/leaveBonusReportExcel.do?pid=${miniBean.runYear}">
										   Export to Excel </a><br />
								    <a href="${appContext}/leaveBonusBarChartReport.do">Show Chart</a><br/>
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
    </body>
</html>
