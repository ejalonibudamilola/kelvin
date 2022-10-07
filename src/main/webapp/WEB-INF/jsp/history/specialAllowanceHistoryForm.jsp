<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            Special Allowance History Report
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
    </head>
    <body class="main">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
    <form:form modelAttribute="specAllowHist">
	<c:set value="${specAllowHist}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
           <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	<tr>
							<td>
								<div class="navTopBannerAndSignOffLink">
									<span class="navTopSignOff">
										&nbsp;&nbsp;&nbsp;<a href="javascript: self.close ()" title='Close Window' id="topNavSearchButton1">Close Window</a>
									</span>
								</div>
							</td>
						</tr>
                        <tr>
                            <td colspan="2">
                               
                                        &nbsp;
                              </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                     <c:out value="${specAllowHist.name}"/> <br>
                                     <c:out value="${specAllowHist.garnishmentName}"/> History
                                    
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                  <div id="topOfPageBoxedErrorMessage" style="display:${specAllowHist.displayErrors}">
										<spring:hasBindErrors name="specAllowHist">
										<ul>
											<c:forEach var="errMsgObj" items="${errors.allErrors}">
											<li>
											<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
											</li>
											</c:forEach>
										</ul>
										</spring:hasBindErrors>
									</div>                            
                                <table class="reportMain" cellpadding="0" cellspacing="0" >
                                <tr align="left">
									<td class="activeTH"><c:out value="${specAllowHist.name}"/>&nbsp;- &nbsp;<c:out value="${specAllowHist.garnishmentName}"/> History </td>
								</tr>
								<tr>
									<td class="activeTD">
									   <fieldset>
									   <legend><b>Employee Special Allowance Summary</b></legend>
										<table width="100%" border="0" cellspacing="1" cellpadding="2">
											<tr>
												<td width="25%" align="left">Employee Name :</td>
												<td width="25%" align="left" colspan="2"><c:out value="${specAllowHist.name}"/></td>
											</tr>
											<tr>
												<td width="25%" align="left">Employee/Staff ID :</td>
												<td width="25%" align="left" colspan="2"><c:out value="${specAllowHist.displayTitle}"/></td>
											</tr>
											<tr>
												<td width="25%" align="left">Organization :</td>
												<td width="25%" align="left" colspan="2"><c:out value="${specAllowHist.mode}"/></td>
											</tr>
											<tr>
												<td width="25%" align="left">Special Allowance Name :</td>
												<td width="25%" align="left" colspan="2"><c:out value="${specAllowHist.garnishmentName}"/></td>
											</tr>
											<tr>
												<td width="25%" align="left">Total Monies Paid :</td>
												<td width="25%" align="left" colspan="2"><c:out value="${specAllowHist.paidLoanAmountStr}"/></td>
											</tr>
											
											</table>						
										</fieldset>
										
									</td>
								</tr>
                                    <tr>
                                        <td>
                                            
                                                    &nbsp;                                        
                                        </td>
                                       
                                    </tr>
                                    <tr>
                                        <td valign="top">
											<p class="label">${specAllowHist.garnishmentName} - History</p>
											<display:table name="dispBean" class="register2" sort="page" defaultsort="1" requestURI="${appContext}/viewSpecialAllowanceHistory.do">
											<display:column property="payPeriodStr" title="Pay Period" style="text-align:center;"></display:column>
											<display:column property="payDateStr" title="Pay Date" style="text-align:center;"></display:column>
											<display:column property="amountStr" title="Amount Paid" style="text-align:center;"></display:column>
											<display:setProperty name="paging.banner.placement" value="bottom" />
										</display:table>
										</td>
                                    </tr>
                                    <tr>
                                     	<td>
                                      	 	&nbsp;
                                        </td>
                                    </tr>
                                    <c:if test="${not specAllowHist.emptyList}">
                                    <tr>
                                     	<td>
                                          <a href='${appContext}/empSpecAllowExcel.do?eid=${specAllowHist.id}&oid=${specAllowHist.objectId}&ph=y'><i>Send to Microsoft&copy; Excel&copy;</i></a>                                          
                                        </td>
                                    </tr>
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
