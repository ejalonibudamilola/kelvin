<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
    <head>
        <title>
            Business Client Payroll Summary
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
    </head>
    <body class="main">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
    <form:form modelAttribute="clientPaystubBean">
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    <c:out value="${clientPaystubBean.businessName}"/><br>
                                    Payroll Summary
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                                              
                                <span class="reportTopPrintLink"><a href="${appContext}/busClientPayrollSummaryPFV.do?fd=${clientPaystubBean.fromDateAsStr}&td=${clientPaystubBean.toDateAsStr}&bid=${clientPaystubBean.id}&pt=${clientPaystubBean.payMethodTypes}&pfv=t" target="_blank">Printer-Friendly Version</a> &nbsp; 
                                <a href="${appContext}/busClientPayrollSummaryXls.do?fd=${clientPaystubBean.fromDateAsStr}&td=${clientPaystubBean.toDateAsStr}&bid=${clientPaystubBean.id}&pt=${clientPaystubBean.payMethodTypes}" target="_blank">View in Excel</a> &nbsp;<br>
                                <a href="${appContext}/createBusClientPayrollForm.do">Select another client</a>
                               <!-- Trouble viewing or printing? <a href="" onclick=""><img src="images/questionmark.png" border="0"></a> --></span>
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <td class="reportFormControls">
                                            
                                                    <span class="optional"> <c:out value="${clientPaystubBean.businessName}"/>&nbsp;Payroll as at :&nbsp;<c:out value="${clientPaystubBean.payrollDate}"/> </span>  
                                                    &nbsp;&nbsp;Pay Method:
                                                    <form:select path="payMethodTypes">
                                                    <form:option value="0">All Types</form:option>
						                			<c:forEach items="${payMethodTypes}" var="pMT">
						                			<form:option value="${pMT.id}">${pMT.name}</form:option>
						                			</c:forEach>
						               				</form:select> 
                                            
                                        </td>
                                       
                                    </tr>
                                    <tr>
                                        <td class="reportFormControlsSpacing"></td>
                                    </tr>
                                    
                                    <tr>
                                    	<td>
                                    		<table class="report" cellspacing="0" cellpadding="0">
                                    		 <tr class='reportEven header'>
                                    		 	<td class='tableCell' valign="top">
                                                        Business Name :&nbsp;<c:out value="${clientPaystubBean.businessName}"/>
                                                </td>
                                             	<td class='tableCell' valign="top">
                                                        Map ID :&nbsp;<c:out value="${clientPaystubBean.mapId}"/>
                                                </td>
				                             </tr>
                                              
                                    		</table>
                                    	</td>
                                    </tr>
                                    <tr>
                                    	
                                        <td>
                                            <table class="report" cellspacing="0" cellpadding="0">
                                                <tr class='reportOdd header'>
                                                	<td class='tableCell' valign="top">
                                                        &nbsp;
                                                    </td>
                                                    <td class='tableCell' valign="top">
                                                        Pay Date
                                                    </td>
                                                    <td class='tableCell' valign="top">
                                                        Name
                                                    </td>
                                                    <td class='tableCell' valign="top">
                                                        Employee ID
                                                    </td>
                                                    <c:choose>
                                                    <c:when test="${clientPaystubBean.noFilter}">
                                                    <td class='tableCell' valign="top">
                                                        Pay Method
                                                    </td>
                                                    </c:when>
                                                    <c:when test="${clientPaystubBean.usingCashCard}">
                                                    	<td class='tableCell' valign="top">
                                                       	 Bank Name
                                                    	</td>
                                                    	<td class='tableCell' valign="top">
                                                       	 PAN
                                                    	</td>
                                                    </c:when>
                                                     <c:when test="${clientPaystubBean.usingDirectDeposit}">
                                                    	<td class='tableCell' valign="top">
                                                       	 Bank Name
                                                    	</td>
                                                    	<td class='tableCell' valign="top">
                                                       	 Sort Code
                                                    	</td>
                                                    	<td class='tableCell' valign="top">
                                                       	 Account Number
                                                    	</td>
                                                    	<td class='tableCell' valign="top">
                                                       	 Account Type
                                                    	</td>
                                                    </c:when>
                                                    <c:otherwise>
                                                    	<td class='tableCell' valign="top">
                                                       	 Pay Method
                                                    	</td>
                                                    </c:otherwise>
                                                    </c:choose>
                                                    <td class='tableCell' valign="top">
                                                        Net Amt
                                                    </td>
                                                    <td class='tableCell' valign="top">
                                                        Taxes<br>
                                                        Withheld
                                                    </td>
                                                    <td class='tableCell' valign="top">
                                                        Total<br>
                                                        Deductions
                                                    </td>
                                                    <td class='tableCell' valign="top">
                                                        Company<br>
                                                        Contributions
                                                    </td>
                                                    <td class='tableCell' valign="top">
                                                        Total<br>
                                                        Other Deductions
                                                    </td>
                                                    <td class='tableCell' valign="top">
                                                        Total<br>
                                                        Pay
                                                    </td>
                                                    <td class='tableCell' valign="top">
                                                        Total<br>
                                                        Cost
                                                    </td>
                                                  
                                                </tr>
                                                <c:forEach items="${clientPaystubBean.payrollData}" var="pData">
                                                
                                                <tr class="${pData.displayStyle}">
                                                	                                                    
                                                    <td class='tableCell' valign="top" nowrap>
                                                        <c:out value="${pData.payStatusStr}"/>
                                                    </td>
                                                    <td class='tableCell' valign="top" nowrap>
                                                        <c:out value="${pData.payDate}"/>
                                                    </td>
                                                    <td class='tableCell' valign="top" nowrap>
                                                        <c:out value="${pData.employee.firstName}"/> &nbsp;<c:out value="${pData.employee.lastName}"/>
                                                    </td>
                                                    <td class='tableCell' align="right" valign="top">
                                                        <c:out value="${pData.employee.employeeId}"/>
                                                    </td>
                                                    <c:choose>
                                                    <c:when test="${clientPaystubBean.noFilter}">
                                                    <td class='tableCell' align="right" valign="top">
                                                        <c:out value="${pData.paymentType}"/>
                                                    </td>
                                                    </c:when>
                                                    <c:when test="${clientPaystubBean.usingCashCard}">
                                                    	<td class='tableCell' align="right" valign="top">
                                                       	 <c:out value="${pData.paymentMethodInfo.bankInfo.name}"/>
                                                    	</td>
                                                    	<td class='tableCell' align="right" valign="top">
                                                       	 <c:out value="${pData.paymentMethodInfo.cashCardNumber}"/>
                                                    	</td>
                                                    </c:when>
                                                     <c:when test="${clientPaystubBean.usingDirectDeposit}">
                                                    	<td class='tableCell' align="right" valign="top">
                                                       	 <c:out value="${pData.paymentMethodInfo.bankInfo.name}"/>
                                                    	</td>
                                                    	<td class='tableCell' align="right" valign="top">
                                                       	 <c:out value="${pData.paymentMethodInfo.bankInfo.sortCode}"/>
                                                    	</td>
                                                    	<td class='tableCell' align="right" valign="top">
                                                       	 <c:out value="${pData.paymentMethodInfo.accountNumber}"/>
                                                    	</td>
                                                    	<td class='tableCell' align="right" valign="top">
                                                       	 <c:out value="${pData.paymentMethodInfo.accountType}"/>
                                                    	</td>
                                                    </c:when>
                                                    <c:otherwise>
                                                    	<td class='tableCell' align="right" valign="top">
                                                       	 <c:out value="${pData.paymentType}"/>
                                                    	</td>
                                                    </c:otherwise>
                                                    </c:choose>
                                                    <td class='tableCell' align="right" valign="top">
                                                       ₦<c:out value="${pData.netPayStr}"/>
                                                    </td>
                                                    <td class='tableCell' align="right" valign="top">
                                                       ₦<c:out value="${pData.taxesPaidStr}"/>
                                                    </td>
                                                    <td class='tableCell' align="right" valign="top">
                                                         ₦<c:out value="${pData.totalDeductionsStr}"/>
                                                    </td>
                                                    <td class='tableCell' align="right" valign="top">
                                                         ₦<c:out value="${pData.totalContributionsStr}"/>
                                                    </td>
                                                    <td class='tableCell' align="right" valign="top">
                                                       ₦<c:out value="${pData.totalGarnishmentsStr}"/>
                                                    </td>
                                                    <td class='tableCell' align="right" valign="top">
                                                         ₦<c:out value="${pData.totalPayStr}"/>
                                                    </td>
                                                    <td class='tableCell total' align="right" valign="top">
                                                         ₦<c:out value="${pData.totalCostStr}"/>
                                                    </td>
                                                    
                                                </tr>
                                                </c:forEach>
                                                <tr class='reportOdd footer'>
                                                    <td class='tableCell' valign="top">
                                                        &nbsp;
                                                    </td>
                                                    <td class='tableCell' valign="top">
                                                         &nbsp;
                                                    </td>
                                                    
                                                    <c:choose>
                                                    <c:when test="${clientPaystubBean.noFilter}">
                                                    <td class='tableCell' valign="top">
                                                        &nbsp;
                                                    </td>
                                                     <td class='tableCell' valign="top">
                                                        &nbsp;
                                                    </td>
                                                    </c:when>
                                                    <c:when test="${clientPaystubBean.usingCashCard}">
                                                    	<td class='tableCell' valign="top">
                                                       	 &nbsp;
                                                    	</td>
                                                    	 <td class='tableCell' valign="top">
                                                        &nbsp;
                                                    	</td>
                                                    	<td class='tableCell' valign="top">
                                                        &nbsp;
                                                    	</td>
                                                    </c:when>
                                                     <c:when test="${clientPaystubBean.usingDirectDeposit}">
                                                    	<td class='tableCell' valign="top">
                                                       	&nbsp;
                                                    	</td>
                                                    	<td class='tableCell' valign="top">
                                                       	 &nbsp;
                                                    	</td>
                                                    	<td class='tableCell' valign="top">
                                                       	 &nbsp;
                                                    	</td>
                                                    	<td class='tableCell' valign="top">
                                                       	 &nbsp;
                                                    	</td>
                                                    	 <td class='tableCell' valign="top">
                                                        &nbsp;
                                                    	</td>
                                                    </c:when>
                                                    <c:otherwise>
                                                    	<td class='tableCell' valign="top">
                                                       	 &nbsp;
                                                    	</td>
                                                    	 <td class='tableCell' valign="top">
                                                        &nbsp;
                                                    </td>
                                                    </c:otherwise>
                                                    </c:choose>
                                                    <td class='tableCell' align="right" valign="top">
                                                        Totals
                                                    </td>
                                                    <td class='tableCell' align="right" valign="top">
                                                        ₦<c:out value="${clientPaystubBean.totalNetAmountStr}"/>
                                                    </td>
                                                    <td class='tableCell' align="right" valign="top">
                                                         ₦<c:out value="${clientPaystubBean.totalTaxesWithheldStr}"/>
                                                    </td>
                                                    <td class='tableCell' align="right" valign="top">
                                                         ₦<c:out value="${clientPaystubBean.totalDeductionsStr}"/>
                                                    </td>
                                                    <td class='tableCell' align="right" valign="top">
                                                        ₦<c:out value="${clientPaystubBean.totalCompanyContributionsStr}"/>
                                                    </td>
                                                    <td class='tableCell' align="right" valign="top">
                                                        ₦<c:out value="${clientPaystubBean.totalGarnishmentsStr}"/>
                                                    </td>
                                                    <td class='tableCell' align="right" valign="top">
                                                        ₦<c:out value="${clientPaystubBean.totalPayStr}"/>
                                                    </td>
                                                    <td class='tableCell' align="right" valign="top">
                                                         ₦<c:out value="${clientPaystubBean.totalCostStr}"/>
                                                    </td>
                                                    <!--
                                                    <td class='tableCell' valign="top">
                                                        &nbsp;
                                                    </td>
                                                --></tr>
                                                
                                            </table>
                                        </td>
                                    </tr>
                                    <tr>
                                    	<td class='tableCell' valign="top">
                                    	 &nbsp;
                                    	</td>
                                    </tr>
                                    
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                        <input type="image" name="_updateReport" value="updateReport" alt="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                        <input type="image" name="_pay" value="pay" alt="Set status to paid" class="" src="images/Set_Pay_Status_h.png">
                                        </td>
                                    </tr>
                                    <tr>
                                    <td>&nbsp;</td>
                                    </tr>
                                    <tr>
                                    <td>&nbsp;</td>
                                    </tr>
                                    <tr>
                                    <td></td>
                                    </tr>
                                </table>
                                <div class="reportBottomPrintLink">
                                    <a href="${appContext}/busClientPayrollSummaryPFV.do?fd=${clientPaystubBean.fromDateAsStr}&td=${clientPaystubBean.toDateAsStr}&bid=${clientPaystubBean.id}&pt=${clientPaystubBean.payMethodTypes}&pfv=t" target="_blank">Printer-Friendly Version</a> &nbsp; 
                                	<a href="${appContext}/busClientPayrollSummaryXls.do?fd=${clientPaystubBean.fromDateAsStr}&td=${clientPaystubBean.toDateAsStr}&bid=${clientPaystubBean.id}&pt=${clientPaystubBean.payMethodTypes}" target="_blank">View in Excel</a> &nbsp;
                                    <a href="${appContext}/createBusClientPayrollForm.do">Select another client</a><br>
                                    <!--Trouble viewing or printing? <a href="" onclick=""><img src="images/questionmark.png" border="0"></a>-->
                                </div><br>
                                <!--
                                <a href="">Print or export checks</a><br>
                                <a href="">Send pay stub emails to employees</a><br>
                               
                                <br>
                                <table>
                                    <tr>
                                        <td align='right' class='fn' valign='top'>
                                            <sup>1</sup>
                                        </td>
                                        <td class='fn'>
                                            Requires Excel version 2002 or later. What if I have an older version? <a href="" onclick=""><img src="images/questionmark.png" border="0"></a>
                                        </td>
                                    </tr>
                                </table>
                                 -->
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
