<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            Tax Deduction History Report
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
    </head>
    <body class="main">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
    <form:form modelAttribute="taxDedHist">
	<c:set value="${taxDedHist}" var="dispBean" scope="request"/>
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
                                     <c:out value="${taxDedHist.name}"/> <br>
                                     <c:out value="${taxDedHist.deductionName}"/> History
                                    
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                  <div id="topOfPageBoxedErrorMessage" style="display:${taxDedHist.displayErrors}">
										<spring:hasBindErrors name="taxDedHist">
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
									<td class="activeTH"><c:out value="${taxDedHist.name}"/>&nbsp;- &nbsp;<c:out value="${taxDedHist.deductionName}"/> History </td>
								</tr>
								<tr>
									<td class="activeTD">
									   <fieldset>
									   <legend><b>Employee Tax Deduction History</b></legend>
										<table width="100%" border="0" cellspacing="1" cellpadding="2">
											<tr>
												<td width="25%" align="left">Employee Name :</td>
												<td width="25%" align="left" colspan="2"><c:out value="${taxDedHist.name}"/></td>
											</tr>
											<tr>
												<td width="25%" align="left">Employee/Staff ID :</td>
												<td width="25%" align="left" colspan="2"><c:out value="${taxDedHist.employeeId}"/></td>
											</tr>
											<tr>
												<td width="25%" align="left">Organization :</td>
												<td width="25%" align="left" colspan="2"><c:out value="${taxDedHist.mode}"/></td>
											</tr>
											
											<tr>
												<td width="25%" align="left">Total Monies Paid :</td>
												<td width="25%" align="left" colspan="2"><c:out value="${taxDedHist.taxPaidStr}"/></td>
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
											<p class="label">${taxDedHist.deductionName} - History</p>
											<display:table name="dispBean" class="register2" sort="page" defaultsort="1" requestURI="${appContext}/taxDeductionHistory.do">
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
                                    <tr>
                                    <td class="buttonRow" align="right">
                                        <input type="image" name="_cancel" value="cancel" alt="Cancel" src="images/close.png">
                                        </td>
                                    </tr>
                                    
                                    <tr>
                                     	<td>
                                     	<br>
                                          <a href='${appContext}/empTaxDed.do?eid=${taxDedHist.id}&sDate=${taxDedHist.fromDateStr}&eDate=${taxDedHist.toDateStr}'><i>Send to PDF&copy;</i></a>                                          
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
