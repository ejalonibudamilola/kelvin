<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            Loan History Report
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<link href="css/datatables.min.css" rel="stylesheet">
    </head>
    <body class="main">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
    <form:form modelAttribute="garnHist">
	<c:set value="${garnHist}" var="dispBean" scope="request"/>
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
                                <div class="title">
                                     <c:out value="${garnHist.name}"/> <br>
                                     <c:out value="${garnHist.garnishmentName}"/> History
                                </div>
							</td>
						</tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                  <div id="topOfPageBoxedErrorMessage" style="display:${garnHist.displayErrors}">
										<spring:hasBindErrors name="garnHist">
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
                                            <td class="activeTH"><c:out value="${garnHist.name}"/>&nbsp;- &nbsp;<c:out value="${garnHist.garnishmentName}"/> History </td>
                                        </tr>
                                        <tr>
                                            <td class="activeTD">
                                               <fieldset>
                                                    <legend><b>Employee Loan Summary</b></legend>
                                                    <table width="100%" border="0" cellspacing="1" cellpadding="2">
                                                        <tr>
                                                            <td> <b> <c:out value="${roleBean.staffTypeName}"/> Name :</b> <c:out value="${garnHist.name}"/></td>
                                                        </tr>
                                                        <tr>
                                                            <td> <b> <c:out value="${roleBean.staffTitle}"/> :</b> <c:out value="${garnHist.displayTitle}"/></td>
                                                        </tr>
                                                        <tr>
                                                            <td> <b> <c:out value="${roleBean.mdaTitle}"/> : </b> <c:out value="${garnHist.mode}"/></td>
                                                        </tr>
                                                        <tr>
                                                            <td> <b> Loan Name : </b> <c:out value="${garnHist.garnishmentName}"/></td>
                                                        </tr>
                                                        <c:if test="${garnHist.showTotalLoanTypeRow}">
                                                        <tr>
                                                            <td> <b> Total Loan Type Amount** : </b> <c:out value="${garnHist.originalLoanAmountStr}"/></td>
                                                        </tr>
                                                        </c:if>
                                                        <tr>
                                                            <td><b>Current Loan Amount :</b> <c:out value="${garnHist.currentOriginalLoanAmountStr}"/></td>
                                                        </tr>
                                                        <tr>
                                                            <td> <b>Total Payments Made :</b> <c:out value="${garnHist.paidLoanAmountStr}"/></td>
                                                        </tr>
                                                        <tr>
                                                            <td> <b>Outstanding Balance : </b><c:out value="${garnHist.loanDifferenceStr}"/></td>
                                                        </tr>

                                                    </table>
                                               </fieldset>
                                            </td>
                                        </tr>
                                        <c:choose>
                                            <c:when test="${garnHist.showTotalLoanTypeRow }">
                                                <tr>
                                                    <td>

                                                          <span class="note"><font color="maroon"><b>**The 'Total Loan Type Amount' </b></font> - <i>refers to ALL THE Totals of Loan(s) attributable to this Loan Type.<br/>
                                                          In essence all payments and loan balances ever inputted into the IPPMS for <c:out value="${garnHist.garnishmentName}"/> for this <c:out value="${roleBean.staffTypeName}"/>.</i></span>
                                                     <br> <br>
                                                    </td>
                                                </tr>
                                            </c:when>
                                            <c:otherwise>
                                                <tr>
                                                    <td>
                                                            &nbsp;
                                                    </td>
                                                </tr>
                                            </c:otherwise>
                                        </c:choose>
                                        <tr>
                                            <td>
                                                <table class="display table" id="garntbl" cellspacing="0" cellpadding="0">
                                                    <thead>
                                                        <tr >
                                                            <td >Pay Date</td>
                                                            <td >Pay Period</td>
                                                            <td>Starting Balance</td>
                                                            <td>Withheld Amount</td>
                                                            <td>End Balance</td>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach items="${garnHist.beanList}" var="dedMiniBean">
                                                                <tr>
                                                                    <td><c:out value="${dedMiniBean.payDateStr}"/></td>
                                                                    <td><c:out value="${dedMiniBean.payPeriodStr}"/></td>
                                                                    <td><c:out value="${dedMiniBean.startingLoanBalanceStr}"/></td>
                                                                    <td><c:out value="${dedMiniBean.amountStr}"/></td>
                                                                    <td><c:out value="${dedMiniBean.loanBalanceStr}"/></td>
                                                                </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                        <tr>
                                                <td>
                                                    &nbsp;
                                                </td>
                                        </tr>
                                        <tr>
                                            <td>
                                              <a href='${appContext}/empGarnDedExcel.do?eid=${garnHist.id}&oid=${garnHist.objectId}&oind=${garnHist.objectInd}'><i>Send to Microsoft&copy; Excel&copy;</i></a>
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
        <script src="scripts/datatables.min.js"></script>
        <script>
            $('#garntbl').DataTable( {

                 pageLength : 10,
                 lengthMenu: [[10, 20, 30, -1], [10, 20, 30, 'All Records']],
                 searching: true
            });

        </script>
    </body>
</html>
