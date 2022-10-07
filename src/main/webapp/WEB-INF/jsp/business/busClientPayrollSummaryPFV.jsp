<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>


<html>
		<head>
			<title>Business Client Payroll Summary</title>
			<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
			<META HTTP-EQUIV="Expires" CONTENT="-1">
			<META HTTP-EQUIV="Cache-Control" VALUE="no-cache, no-store, must-revalidate">
			<META HTTP-EQUIV="Cache-Control" VALUE="post-check=0, pre-check=0">
		
			

			<link rel="stylesheet" href="styles/omg.css" type="text/css">
       	 	<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        	<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
			
			
		</head>
<body>
	<form:form modelAttribute="clientPaystubBean">			
				<style> body { margin-left : 20px;}.report td {font-size: 9pt;}.report .header {background-color : #E2E2E2; font-weight : bold; color : #000000;}.report .footer {background-color : #E2E2E2; font-weight : bold; color : #000000;}table.report {width : auto;}table.reportMain {width : auto;}tr.reportEven {background-color : #F7F7F7;}td.reportFormControls {background-color : #E2E2E2; font-weight : bold; color : #000000;}h3 {margin-left: 10px;}</style>
				<div class="titlePrinterFriendly"><c:out value="${clientPaystubBean.businessName}"/><br>Payroll Summary</div>
		    	<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
					<tr>
						<td class="reportFormControls">
							<h3><c:out value="${clientPaystubBean.businessName}"/></h3>
						<p>
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
				<td class='tableCell' valign="top">Paid Status</td>
					<td class='tableCell' valign="top">Date</td>
					<td class='tableCell' valign="top">Name</td>
					<td class='tableCell' valign="top">Employee ID</td>
					<c:choose>
                    <c:when test="${clientPaystubBean.noFilter}">
                      <td class='tableCell' valign="top">Pay Method</td>
                    </c:when>
                    <c:when test="${clientPaystubBean.usingCashCard}">
                      <td class='tableCell' valign="top">Bank Name</td>
                      <td class='tableCell' valign="top">PAN</td>
                    </c:when>
                    <c:when test="${clientPaystubBean.usingDirectDeposit}">
                      <td class='tableCell' valign="top">Bank Name</td>
                      <td class='tableCell' valign="top">Sort Code</td>
                      <td class='tableCell' valign="top">Account Number</td>
                      <td class='tableCell' valign="top">Account Type</td>
                     </c:when>
                     <c:otherwise>
                     <td class='tableCell' valign="top">Pay Method</td>
                    </c:otherwise>
                    </c:choose>
					<td class='tableCell' valign="top">Net Amt</td>
					<td class='tableCell' valign="top">Taxes<br/>Withheld</td>
					<td class='tableCell' valign="top">Total<br/>Deductions</td>
					<td class='tableCell' valign="top">Company<br/>Contributions</td>
					<td class='tableCell' valign="top">Total<br/>Other Deductions</td>
					<td class='tableCell' valign="top">Total<br/>Pay</td>
					<td class='tableCell' valign="top">Total<br/>Cost</td>
				</tr>
				 <c:forEach items="${clientPaystubBean.payrollData}" var="pData">
                  <tr class="${pData.displayStyle}">
					<td class='tableCell' valign="top" nowrap>
                       <c:out value="${pData.payStatusStr}"/>
                    </td>
                    <td class='tableCell' valign="top">
                 	 <c:out value="${pData.payDate}" />
                     </td>
                     <td class='tableCell' valign="top">
                      <c:out value="${pData.employee.firstName}"/> &nbsp;<c:out value="${pData.employee.lastName}"/>
                     </td>
                     <td class='tableCell' align="right" valign="top">
                     	<c:out value="${pData.employee.employeeId}"/>
                     </td>
							<c:choose>
								<c:when test="${pBusinessData.noFilter}">
									<td class='tableCell' align="right" valign="top"><c:out
										value="${pData.paymentType}" /></td>
								</c:when>
								<c:when test="${payBusinessData.usingCashCard}">
									<td class='tableCell' align="right" valign="top"><c:out
										value="${pData.paymentMethodInfo.bankInfo.name}" /></td>
									<td class='tableCell' align="right" valign="top"><c:out
										value="${pData.paymentMethodInfo.cashCardNumber}" /></td>
								</c:when>
								<c:when test="${payBusinessData.usingDirectDeposit}">
									<td class='tableCell' align="right" valign="top"><c:out
										value="${pData.paymentMethodInfo.bankInfo.name}" /></td>
									<td class='tableCell' align="right" valign="top"><c:out
										value="${pData.paymentMethodInfo.bankInfo.sortCode}" /></td>
									<td class='tableCell' align="right" valign="top"><c:out
										value="${pData.paymentMethodInfo.accountNumber}" /></td>
									<td class='tableCell' align="right" valign="top"><c:out
										value="${pData.paymentMethodInfo.accountType}" /></td>
								</c:when>
								<c:otherwise>
									<td class='tableCell' align="right" valign="top"><c:out
										value="${pData.paymentType}" /></td>
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
								<td class='tableCell' valign="top">&nbsp;</td>
								<td class='tableCell' valign="top">&nbsp;</td>
							</c:when>
							<c:when test="${clientPaystubBean.usingCashCard}">
								<td class='tableCell' valign="top">&nbsp;</td>
								<td class='tableCell' valign="top">&nbsp;</td>
								<td class='tableCell' valign="top">&nbsp;</td>
							</c:when>
							<c:when test="${clientPaystubBean.usingDirectDeposit}">
								<td class='tableCell' valign="top">&nbsp;</td>
								<td class='tableCell' valign="top">&nbsp;</td>
								<td class='tableCell' valign="top">&nbsp;</td>
								<td class='tableCell' valign="top">&nbsp;</td>
							</c:when>
							<c:otherwise>
								<td class='tableCell' valign="top">&nbsp;</td>
								<td class='tableCell' valign="top">&nbsp;</td>
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
               </tr>
             </table>
			  <tr>
                  <td class='tableCell' valign="top">&nbsp;</td>
               </tr>
           
		</table>
	</form:form>
	</body>
 </html>
