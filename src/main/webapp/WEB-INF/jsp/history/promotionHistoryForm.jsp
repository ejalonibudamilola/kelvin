<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
          <c:out value="${pageTitle}"/> 
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
    </head>
    <body class="main">
    <form:form modelAttribute="promoHist">
	<c:set value="${promoHist}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        
                        <tr>
                            <td>
                                <div class="title">
                                     <c:out value="${promoHist.name}"/> - <br>
                                       <c:out value="${pageSubTitle}"/>
                                    
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                  <div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
										<spring:hasBindErrors name="promoHist">
										<ul>
											<c:forEach var="errMsgObj" items="${errors.allErrors}">
											<li>
											<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
											</li>
											</c:forEach>
										</ul>
										</spring:hasBindErrors>
									</div>                            
                                <table width="50%" cellpadding="0" cellspacing="0" >
                                <tr align="left">
									<td class="activeTH"><c:out value="${promoHist.name}"/> -<c:out value="${pageSubTitle}"/> </td>
								</tr>
								<tr>
									<td class="activeTD">
									   <fieldset>
									   <legend><b><c:out value="${roleBean.staffTypeName}"/> Details</b></legend>
										<table width="100%" border="0" cellspacing="1" cellpadding="2">
											<tr>
												<td width="25%" align="left"><c:out value="${roleBean.staffTypeName}"/> Name :</td>
												<td width="25%" align="left"><c:out value="${promoHist.name}"/></td>
											</tr>
											<tr>
												<td width="25%" align="left"><c:out value="${roleBean.staffTitle}"/> :</td>
												<td width="25%" align="left"><c:out value="${promoHist.employeeId}"/></td>
											</tr>
											<tr>
												<td width="25%" align="left"><c:out value="${roleBean.mdaTitle}"/> :</td>
												<td width="25%" align="left"><c:out value="${promoHist.mode}"/></td>
											</tr>
											<c:if test="${promoHist.confirmation}">
											<tr>
												<td width="25%" align="left">School :</td>
												<td width="25%" align="left"><c:out value="${promoHist.displayTitle}"/></td>
											</tr>
											</c:if>
											<tr>
												<td width="25%" align="left">Date Of Birth :</td>
												<td width="25%" align="left"><c:out value="${promoHist.birthDate}"/></td>
											</tr>
											<tr>
												<td width="25%" align="left">Date of Hire :</td>
												<td width="25%" align="left"><c:out value="${promoHist.hireDate}"/></td>
											</tr>
											<tr>
												<td width="25%" align="left">Confirmation Date :</td>
												<td width="25%" align="left"><c:out value="${promoHist.confirmDate}"/></td>
											</tr>
											<tr>
											    <c:choose>
											    	<c:when test="${promoHist.terminatedEmployee}">
											    		<td width="25%" align="left">Last Level And Step :</td>
											    	</c:when>
											    	<c:otherwise>
											    		<td width="25%" align="left">Current Level And Step :</td>
											    	</c:otherwise>
											    </c:choose>
												
												<td width="25%" align="left"><c:out value="${promoHist.currentLevelAndStep}"/></td>
											</tr>
											<tr>
												<td width="25%" align="left">No of Years In Service :</td>
												<td width="25%" align="left"><c:out value="${promoHist.noOfYearsInService}"/></td>
											</tr>

											<c:if test="${promoHist.terminatedEmployee}">
											<tr>
												<td width="25%" align="left">Terminated Date :</td>
												<td width="25%" align="left"><c:out value="${promoHist.terminationDate}"/></td>
											</tr>
											<tr>
												<td width="25%" align="left">Termination Reason :</td>
												<td width="25%" align="left"><c:out value="${promoHist.terminationReason}"/></td>
											</tr>
											</c:if>
											
											</table>						
										</fieldset>
										
									</td>
								</tr>
                                    <tr>
                                        <td>
                                            
                                                    &nbsp;                                        
                                        </td>
                                       
                                    </tr>
                                    </table>
                                    <table>
                                    <c:if test="${promotion}">
                                    <tr>
                                        <td valign="top">
											<p class="label">Promotion/Demotion History</p>
											<display:table name="dispBean" class="register4" sort="page" defaultsort="1" requestURI="${appContext}/viewEmpPromoHistory.do">
 											<display:column property="promotionDateStr" title="Promotion Date" style="text-align:center;"></display:column>
 											<display:column property="mdaInfo.name" title="${roleBean.mdaTitle} at Promotion" style="text-align:right;"></display:column>
											<display:column property="oldSalaryInfo.salaryScaleLevelAndStepStr" title="From" style="text-align:center;"></display:column>
											<display:column property="salaryInfo.salaryScaleLevelAndStepStr" title="To" style="text-align:center;"></display:column>
											<display:column property="oldSalaryInfo.annualSalaryStrWivNaira" title="Old Gross" style="text-align:right;"></display:column>
											<display:column property="salaryInfo.annualSalaryStrWivNaira" title="New Gross" style="text-align:right;"></display:column>
											<display:column property="netIncreaseStr" title="Net Increase" style="text-align:right;"></display:column>
											<display:column property="user.actualUserName" title="Promoted By" style="text-align:center;"></display:column>
											<display:setProperty name="paging.banner.placement" value="bottom" />
										</display:table>
										</td>
                                    </tr>
                                    <tr>
                                     <td class="buttonRow" align="right">
                                           <input type="image" name="_cancel" value="cancel" title="Close" src="images/close.png">
                                       </td>
                                    </tr>
                                    <c:if test="${not promoHist.emptyList}">
                                        <tr>
                                            <td>
                                              <a href='${appContext}/singleEmployeePromoExcel.do?eid=${promoHist.id}'><i>Send to Microsoft&copy; Excel&copy;</i></a>
                                            </td>
                                        </tr>
                                     </c:if>
                                    </c:if>
                                    <c:if test="${transfer}">
                                    <tr>
                                        <td valign="top">
											<p class="label">Transfer History</p>
											<display:table name="dispBean" class="register3" sort="page" defaultsort="1" requestURI="${appContext}/viewEmpTransferHistory.do">
											<display:column property="oldMda" title="From ${roleBean.mdaTitle}" style="text-align:center;"></display:column>
											<display:column property="newMda" title="To ${roleBean.mdaTitle}" style="text-align:center;"></display:column>
											<display:column property="user.actualUserName" title="Transferred By" style="text-align:right;"></display:column>
											<display:column property="auditTimeStamp" title="Transfer Date" style="text-align:right;"></display:column>
											<display:setProperty name="paging.banner.placement" value="bottom" />
										</display:table>
										</td>
                                    </tr>
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                      	 	<input type="image" name="_cancel" value="cancel" title="Close" src="images/close.png">
                                        </td>
                                    </tr>
                                    <c:if test="${not promoHist.emptyList}">

                                    <tr>
                                     	<td>
                                          <a href='${appContext}/singleEmployeeTransferExcel.do?eid=${promoHist.id}'><i>Send to Microsoft&copy; Excel&copy;</i></a>                                          
                                        </td>
                                    </tr>
                                    </c:if>
                                    </c:if>
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
