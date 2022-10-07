<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="dt"%>


<html>
    <head>
        <title>
            Payroll Rerun Form
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="styles/epayroll.css" type="text/css"/>
        <link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
        <link rel="stylesheet" href="css/screen.css" type="text/css"/>
    </head>
     
 
    <body class="main">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
    <form:form modelAttribute="payPeriods">
	<c:set value="${payPeriods}" var="dispBean" scope="request"/>
	
           <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    
                        <tr>
                            <td>
                                <div class="title">
                                    Rerun Payroll For Deleted Paychecks
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                * = Required<br>
                                <br>
                               
							<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="payPeriods">
         							 <ul>
            							<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            							</c:forEach>
         							</ul>
     							 </spring:hasBindErrors>
							</div>
                                   <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <td>
                                                <table border="0" cellspacing="0" cellpadding="3" width="100%" align="left">
                                                    <tr>
                                                        <td class="activeTH">
                                                           <c:out value="${payPeriods.payPeriodName}" /> 
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="activeTD">
                                                            
                                                            <table width="100%" cellspacing="0" border="0" cellpadding="0">
                                                               <tr>
                                                                    <td nowrap><span class="required">Pay Period*</span>&nbsp;
                                                                                	<form:select path="payPeriod" id="newPayPeriod" onchange="go(this)" disabled="true">
                                                                                		 <c:forEach items="${payPeriodsList}" var="period">
						               													 <form:option value="${period}">${period}</form:option>
						                												</c:forEach>
                                                                                	</form:select>
                                                                     </td>
                                                                     <td nowrap>
                                                                             	<span class="required">Pay Date*</span>&nbsp;
                                                                                  <form:input path="payDate"/>
																				  <img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('payDate'),event);">
                                                                                </td>
                                                                </tr>
                                                                                                                               
                                                                <tr>
                                                                   <td>&nbsp; </td>                                                                  
                                                                </tr>
                                                                <tr> 
                                                                     
																      <dt:table name="dispBean" class="register3" sort="page" defaultsort="1" requestURI="${appContext}/paydayForm.do">
																		<dt:column property="abstractEmployeeEntity.employeeId" title="${roleBean.staffTitle}" media="html" href="${appContext}/deleteRerunRecord.do" paramId="rid" paramProperty="payrollRerunInstId"></dt:column>
											                            <dt:column property="abstractEmployeeEntity.employeeId" title="${roleBean.staffTitle}" media="excel"/>
																		<dt:column property="abstractEmployeeEntity.displayName" title="${roleBean.staffTypeName} Name"  ></dt:column>
																		<dt:column property="abstractEmployeeEntity.salaryInfo.salaryScaleLevelAndStepStr" title="Pay Group" ></dt:column>
																		<c:choose>
																		<c:when test="${roleBean.pensioner}">
																		<dt:column property="hiringInfo.monthlyPensionStr" title="Monthly Pension" ></dt:column>
																		</c:when>
																		<c:otherwise>
																		<dt:column property="abstractEmployeeEntity.salaryInfo.totalMonthlySalaryStr" title="Monthly Gross" ></dt:column>
																		</c:otherwise>
																		</c:choose>

																		<dt:column property="abstractEmployeeEntity.assignedToObject" title="${roleBean.mdaTitle}" ></dt:column>
																		
																		<dt:setProperty name="paging.banner.placement" value="bottom" />
																	 </dt:table>
																	
                                                                    
                                                                </tr>
                                                                
                                                     
                                                                                                                 
                                                            </table>
                                                            
                                                            
                                                            
                                                        </td>
                                                    </tr>
                                                </table>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="buttonRow" align="right">
                                                <input type="image" name="submit" value="Create Check" title="Rerun Payroll" class='' src='images/Create_Paychecks_h.png' onclick="">
                                            </td>
                                        </tr>
                                    </table>
                                   <table width="90%" cellpadding="0" cellspacing="0">
                                    <tr>
                                        <td>
                                            <ul>
                                                <li>
                                                	Rerun payroll for deleted Paychecks and then commit(ADD) them to existing payroll run for <c:out value="${roleBean.staffTypeName}"/> to get paid.
                                                </li>
                                            </ul>
                                        </td>
                                    </tr>
                                </table>
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
