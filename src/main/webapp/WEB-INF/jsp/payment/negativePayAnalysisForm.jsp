<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
    <head>
        <title>
            Negative Pay Report
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
    </head>
    <body class="main">
    
    <form:form modelAttribute="paystubSummary">
	<c:set value="${paystubSummary}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    <c:out value="${paystubSummary.companyName}"/><br>
                                    Negative Pay Report for <c:out value="${paystubSummary.fromDateAsString}"/>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                <c:if test="${paystubSummary.hasErrors}">
						<div id="topOfPageBoxedErrorMessage" style="display:${paystubSummary.displayErrors}">
								 <spring:hasBindErrors name="paystubSummary">
         							 <ul>
            							<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            							</c:forEach>
         								</ul>
     							 </spring:hasBindErrors>
						</div>
						</c:if>                              
                                <span class="reportTopPrintLinkExtended">
									<a href="${appContext}/negativePayExcelReport.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}" target="_blank">View in Excel&copy;</a> &nbsp;                                   
                               </span>
                               <br>
                               <br>
                               <table>
									<tr align="left">
										<td class="activeTH" colspan="2">Filter By</td>
										
									</tr>
                                    
                                    <tr>
                                      
                                       <td class="activeTD">
                                             <c:out value="${roleBean.staffTitle}"/> :
                                        </td>
                                        <td class="activeTD">
                                            <form:input path="ogNumber" size="9" maxlength="10"/> 
                                        </td>
                                    </tr>
                                    
                                    <tr>
                                       <td class="activeTD">
                                            Last Name :                                          
                                        </td>
                                        <td class="activeTD">
                                            <form:input path="lastName" size="15" maxlength="20"/>
                                        </td>
                                    </tr>
									<tr>
                                    	<td class="buttonRow" align="right">
                                    		 <input type="image" name="_update" value="update" title="Search" src="images/search.png">
                                        
                                    	</td>
                                    </tr>
								</table>    
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <td>
                                           <span class="note">  ** in front of the name denotes a treated Negative Pay Staff.</span>             
                                        </td>
                                       
                                    </tr>
                                    <tr>
                                        <td class="reportFormControlsSpacing"></td>
                                    </tr>
                                    <tr>
                                    	
                                    		<display:table name="dispBean" id="row" class="registerMid" sort="page" defaultsort="1" requestURI="${appContext}/viewNegativePay.do">
												<display:caption>Employees With Negative Pay For <c:out value="${paystubSummary.fromDateAsString}"/></display:caption>
												<display:column title="S/No."><c:out value="${row_rowNum}"/></display:column>
												<display:column property="abstractEmployeeEntity.employeeId" title="${roleBean.staffTitle}" media="html" href="${appContext}/dedGarnForm.do" paramId="eid" paramProperty="abstractEmployeeEntity.id"></display:column>
												<display:column property="abstractEmployeeEntity.displayNameWivAsterix" title="${roleBean.staffTypeName} Name"></display:column>
												<display:column property="salaryInfo.levelStepStr" title="Pay Group"></display:column>
												<display:column property="mda" title="${roleBean.mdaTitle}"></display:column>
												<display:column property="totalPayStr" title="Gross Pay"></display:column>
												<display:column property="allDedTotalStr" title="Tot. Ded."></display:column>
 												<display:column property="taxesPaidStr" title="PAYE"></display:column>
												<display:column property="netPayStr" title="Net Pay"></display:column>	
												<display:column property="deductionAmountStr" title="Reduce Amt"> </display:column>	
												<display:setProperty name="paging.banner.placement" value="bottom" />					
											</display:table>
                                    	
                                    </tr>
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                        	<input type="image" name="_cancel" value="cancel" title="Close Report" src="images/close.png">
                                        </td>
                                    </tr>
                                </table>
                                <c:if test="${paystubSummary.hasHiddenRecords}">
                                	<div class="reportBottomPrintLink">
                                    	<a href="${appContext}/viewNegativePay.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}">Show Treated Records</a> &nbsp;                                   
									</div><br>
                                </c:if>
                                
                                <div class="reportBottomPrintLink">
                                    <a href="${appContext}/negativePayExcelReport.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}" target="_blank">View in Excel&copy;</a> &nbsp;                                   
								</div><br>
                                
                            </td>
                        </tr>
                        <tr>
               				 <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
           				 </tr>
                    </table>
                </td>
            </tr>
            
        </table>
        </form:form>
    </body>
</html>
