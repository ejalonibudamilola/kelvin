<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="dt"%>


<html>
    <head>
        <title>
            Pay
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="styles/epayroll.css" type="text/css"/>
        <link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
        <link rel="stylesheet" href="css/screen.css" type="text/css"/>
    </head>
     
   <script language="JavaScript">
<!--


function go(which) {
  n = which.value;
  if (n != 0) {
    var url = "${appContext}/paydayForm.do?npp=" + n
    location.href = url;
  }
}

// -->
</script>
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
                                                <c:choose>
                                                    <c:when test="${payPeriods.reRun}">
                                                	    Re-Run All Pending Paychecks
                                                	</c:when>
                                                	<c:otherwise>
                                                	    Create Paychecks
                                                	</c:otherwise>
                                                	</c:choose>
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
                                                                     
																      <dt:table name="dispBean" class="register4" sort="page" defaultsort="1" requestURI="${appContext}/paydayForm.do">
																		<dt:column property="employee.employeeId" title="${roleBean.staffTitle}" ></dt:column>
																		<dt:column property="employee.displayName" title="${roleBean.staffTypeName} Name"  ></dt:column>
 																		<dt:column property="employee.salaryInfo.salaryScaleLevelAndStepStr" title="Pay Group" ></dt:column>
																		<c:choose>
																		<c:when test="${roleBean.pensioner}">
																		    <dt:column property="hiringInfo.monthlyPensionStr" title="Monthly Pension" ></dt:column>
																		</c:when>
																		<c:otherwise>
																		    <dt:column property="employee.salaryInfo.monthlySalaryStr" title="Basic Salary" > </dt:column>
																		    <dt:column property="employee.salaryInfo.monthlyGrossSalaryStr" title="Gross Pay" ></dt:column>
																		</c:otherwise>
																		</c:choose>

																		<dt:column property="employee.currentMdaName" title="${roleBean.mdaTitle}" ></dt:column>
																		<dt:setProperty name="paging.banner.placement" value="bottom" />
																	 </dt:table>
																	
                                                                    
                                                                </tr>
                                                                <c:if test="${roleBean.civilService}">
                                                                <tr>
                                                                 <td class="activeTD">
                                                                 <fieldset>
									   								<legend><b>Subvention(s)</b></legend>
	                                                                <table width="30%" border="0" cellspacing="0" cellpadding="0">
			                                                            <c:forEach items="${payPeriods.subventionList}"  var="sList" varStatus="gridRow">
			                                                                <tr>
			                  														<td>
			                  															<b><c:out value="${sList.name}"/></b>
			                  														</td>
																					<td>
																						<spring:bind path="payPeriods.subventionList[${gridRow.index}].subventionAmountStr">
																						<input type="text" name="<c:out value="${status.expression}"/>"
															    							id="<c:out value="${status.expression}"/>"
															   								value="<c:out value="${status.value}"/>" size="18" maxlength="15"/>
																						</spring:bind>
																					 </td>
															               	</tr>
			               													 </c:forEach>
			                                                            
			                                                            </table>
			                                                            </fieldset>
			                                                            
		                                                            </td>
		                                                            </tr>
                                                                 <tr style="display:none">
                  														<td><spring:bind path="deductDevelopmentLevy">
                 															 <input type="hidden" name="_<c:out value="${status.expression}"/>">
                 															 <input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if>/>
     																		</spring:bind><font color="red"><b>Deduct Development Levy</b></font>
     			  														</td>
                  			
                  														<td nowrap>&nbsp; </td>
                  														<td><div class="note"></div></td>
               													 </tr>
                                                               </c:if>
                                                            </table>
                                                            
                                                            
                                                            
                                                        </td>
                                                    </tr>
                                                </table>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="buttonRow" align="right">
                                                <input type="image" name="submit" value="Create Check" alt="Create Paychecks" class='' src='images/Create_Paychecks_h.png' onclick="">
                                            </td>
                                        </tr>
                                    </table>
                                   <table width="90%" cellpadding="0" cellspacing="0">
                                    <br/>
                                    <tr>
                                        <td>
                                            <ul>
                                                <li>
                                                    <c:choose>
                                                    <c:when test="${payPeriods.reRun}">
                                                	    Re-Run All Pending Paychecks and then Approve them for <c:out value="${roleBean.staffTypeName}"/> to get paid.
                                                	</c:when>
                                                	<c:otherwise>
                                                	    Create Paychecks and then Approve them for <c:out value="${roleBean.staffTypeName}"/> to get paid.
                                                	</c:otherwise>
                                                	</c:choose>
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
