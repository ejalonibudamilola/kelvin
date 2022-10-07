<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            Single <c:out value="${roleBean.staffTypeName}"/> Paycheck History Report Page
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
    </head>
    <body class="main">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
    <form:form modelAttribute="paycheckHistory">
	<c:set value="${paycheckHistory}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
           <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                     <c:out value="${paycheckHistory.name}"/> <br>
                                      Paycheck History
                                    
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                  <div id="topOfPageBoxedErrorMessage" style="display:${paycheckHistory.displayErrors}">
										<spring:hasBindErrors name="paycheckHistory">
										<ul>
											<c:forEach var="errMsgObj" items="${errors.allErrors}">
											<li>
											<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
											</li>
											</c:forEach>
										</ul>
										</spring:hasBindErrors>
									</div>
									
									<table width="50%">
									<tr>
                                        <td>
                                            <a href='${appContext}/searchPaySlipHistory.do'><i>Search again</i></a>                                    
                                        </td>
                                       
                                    </tr>
                                    <tr>
                                        <td>
                                            &nbsp;                                    
                                        </td>
                                       
                                    </tr>
									<tr align="left">
										<td class="activeTH"><c:out value="${paycheckHistory.name}"/> - Paycheck History </td>
									</tr>
								<tr>
									<td class="activeTD">
									   <fieldset>
									   <legend><b><c:out value="${roleBean.staffTypeName}"/> Details</b></legend>
										<table width="100%" border="0" cellspacing="1" cellpadding="2">
											<tr>
												<td width="25%" align="left"><c:out value="${roleBean.staffTypeName}"/> Name :</td>
												<td width="25%" align="left" colspan="2"><c:out value="${paycheckHistory.name}"/></td>
											</tr>
											<tr>
												<td width="25%" align="left"><c:out value="${roleBean.staffTitle}"/> :</td>
												<td width="25%" align="left" colspan="2"><c:out value="${paycheckHistory.displayTitle}"/></td>
											</tr>
											<tr>
												<td width="25%" align="left"><c:out value="${roleBean.mdaTitle}"/> :</td>
												<td width="25%" align="left" colspan="2"><c:out value="${paycheckHistory.mode}"/></td>
											</tr>
											<tr>
												<td width="25%" align="left">Date Of Birth :</td>
												<td width="25%" align="left" colspan="2"><c:out value="${paycheckHistory.birthDate}"/></td>
											</tr>
											<tr>
												<td width="25%" align="left">Date of Hire :</td>
												<td width="25%" align="left" colspan="2"><c:out value="${paycheckHistory.hireDate}"/></td>
											</tr>
											<tr>
												<td width="25%" align="left">Confirmation Date :</td>
												<td width="25%" align="left" colspan="2"><c:out value="${paycheckHistory.confirmDate}"/></td>
											</tr>
											<tr>
											    <c:choose>
											    	<c:when test="${paycheckHistory.terminatedEmployee}">
											    		<td width="25%" align="left">Last Level And Step :</td>
											    	</c:when>
											    	<c:otherwise>
											    		<td width="25%" align="left">Current Level And Step :</td>
											    	</c:otherwise>
											    </c:choose>
												
												<td width="25%" align="left" colspan="2"><c:out value="${paycheckHistory.currentLevelAndStep}"/></td>
											</tr>
											<tr>
												<td width="25%" align="left">No of Years In Service :</td>
												<td width="25%" align="left" colspan="2"><c:out value="${paycheckHistory.noOfYearsInService}"/></td>
											</tr>
											<c:if test="${paycheckHistory.terminatedEmployee}">
											<tr>
												<td width="25%" align="left">Terminated Date :</td>
												<td width="25%" align="left" colspan="2"><c:out value="${paycheckHistory.terminationDate}"/></td>
											</tr>
											<tr>
												<td width="25%" align="left">Termination Reason :</td>
												<td width="25%" align="left" colspan="2"><c:out value="${paycheckHistory.terminationReason}"/></td>
											</tr>
											</c:if>
											
											</table>						
										</fieldset>
										
									</td>
								</tr>
									
									</table>                            
                                <table class="reportMain" cellpadding="0" cellspacing="0" >
                                
                                    <tr>
                                        <tr>
                                                                                                                  <td>
                                                                                                                  <br><br>
                                                                          							            <div class="reportBottomPrintLink">
                                                                          											<a href="${appContext}/employeesPaycheckHistoryExcel.do?eid=${paycheckHistory.employeeId}" title='Generate Excel Report'>
                                                                          											Generate Excel Report </a>
                                                                          											</div>
                                                                          											<br>
                                                                                                                  </td>
                                                                                                              </tr>
                                       
                                    </tr>
                                    <tr>
                                        <td valign="top">
											<p class="label">Paycheck History</p>
											<display:table name="dispBean" class="register3" sort="page" defaultsort="1" requestURI="${appContext}/viewPayslipHistory.do">
											<display:column property="payPeriodStr" title="Pay Period" style="text-align:center;"  href="${appContext}/paySlipView.do" paramId="psid" paramProperty="id"></display:column>
											<display:column property="totalPayStr" title="Total Pay" style="text-align:center;"></display:column>
											<display:column property="totalDeductionsStr" title="Total Deductions" style="text-align:center;"></display:column>
											<display:column property="totalGarnishmentsStr" title="Total Loans" style="text-align:center;"></display:column>
											<display:column property="specialAllowanceStr" title="Spec. Allow." style="text-align:center;"></display:column>
											<display:column property="taxesPaidStr" title="Taxes Paid" style="text-align:center;"></display:column>
											<display:column property="netPayStr" title="Net Pay" style="text-align:center;"></display:column>
											<display:setProperty name="paging.banner.placement" value="bottom" />
										</display:table>
										</td>
                                    </tr>
                                    <tr>
                                     	<td>
                                      	 	&nbsp;
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <a href='${appContext}/searchPaySlipHistory.do'><i>Search again</i></a>
                                        </td>
                                       
                                    </tr>
                                    <tr>
                                        <td>
                                            &nbsp;                                    
                                        </td>
                                       
                                    </tr></table>
                                
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
