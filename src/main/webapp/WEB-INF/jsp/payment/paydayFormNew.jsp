<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <link rel="stylesheet" href="styles/omg.css" type="text/css" />
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="icon" type="image/png" href="/ogsg_ippms/images/coatOfArms.png">
        <title>Create Paycheck</title>
    </head>
    <style>
        .activeTD p{
            font-size:8pt;
        }
        .activeTD{
            padding:1%;
        }
    </style>
    <body>
    <form:form modelAttribute="payPeriods">
        <table class="main" width="75%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                        <tr>
                           <td>
                              <div class="title">Create Paychecks</div>
                           </td>
                        </tr>
                        <tr>
                            <td>
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
                                <table width="45%"  style="margin-left:1%; margin-top:1%">
                                   <tr>
                                    <td class="activeTH" colspan="2" style="padding:2%">Payroll Run Details</td>
                                   </tr>
                                   <tr>
                                    <td class="activeTD">Pay Period:</td>
                                    <td class="activeTD">${payPeriods.payPeriod}</td>
                                   </tr>
                                   <tr>
                                     <td class="activeTD">Pay Date:</td>
                                     <td class="activeTD">${payDay}</td>
                                   </tr>
                                   <tr>
                                     <td class="activeTD">No. of <c:out value="${roleBean.staffTypeName}"/>s:</td>
                                     <td class="activeTD">${payPeriods.toBeProcessedStaff}</td>
                                   </tr>
                                   <tr>
                                     <td class="activeTD">No. of <c:out value="${roleBean.mdaTitle}"/>s:</td>
                                     <td class="activeTD">${payPeriods.toBeProcessedMda}</td>
                                   </tr>
                                   <tr>
                                     <td class="activeTD">No. of Deductions:</td>
                                     <td class="activeTD">${payPeriods.toBeProcessedDeduction}</td>
                                   </tr>
                                   <tr>
                                     <td class="activeTD">No. of Special Allowances:</td>
                                     <td class="activeTD">${payPeriods.toBeProcessedSpecAllow}</td>
                                   </tr>
                                   <c:if test="${not roleBean.pensioner}">
                                      <tr>
                                         <td class="activeTD">No. of Loans:</td>
                                         <td class="activeTD">${payPeriods.toBeProcessedLoan}</td>
                                      </tr>
                                   </c:if>
                                   <tr>
                                     <td class="activeTD">Projected Gross Pay:</td>
                                     <td class="activeTD">${payPeriods.projectedGrossPayStr}</td>
                                   </tr>
                                   <tr>
                                     <td class="activeTD">Projected Net Pay:</td>
                                     <td class="activeTD">${payPeriods.projectedNetPayStr}</td>
                                   </tr>
                                   <tr>
                                     <td class="activeTD">Last Paycheck Gross Pay:</td>
                                     <td class="activeTD">${payPeriods.lastMonthGrossPayStr}</td>
                                   </tr>
                                   <c:if test="${payPeriods.deleteWarningIssued}">
                                     <tr>
                                        <td class="activeTD">Generated Captcha:</td>
                                        <td class="activeTD"><c:out value="${payPeriods.generatedCaptcha}"/></td>
                                     </tr>
                                     <tr>
                                        <td class="activeTD">Entered Captcha :</td>
                                        <td class="activeTD">
                                            <form:input path="enteredCaptcha" size="5" maxlength="6"/>
                                            &nbsp;<font color="green">Case Insensitive</font>
                                            <c:if test="${payPeriods.captchaError}">
                             						&nbsp;<font color="red"><b>Entered Captcha does not match!</b></font>
                             			    </c:if>
                                        </td>
                                     </tr>
                                   </c:if>
                                   <c:if test="${not roleBean.pensioner}">

                                          <tr>
                                             <td class="activeTH" colspan="2" style="padding:2%">Subventions</td>
                                          </tr>
                                           <c:forEach items="${payPeriods.subventionList}"  var="sList" varStatus="gridRow">
                                               <tr>
                                                  <td class="activeTD">
                                                     <b><c:out value="${sList.name}"/></b>
                                                  </td>
                                                  <td class="activeTD">
                                                     <spring:bind path="payPeriods.subventionList[${gridRow.index}].amountStr">
                                                     <input style="width:70%" type="text" name="<c:out value="${status.expression}"/>"
                                                     id="<c:out value="${status.expression}"/>"
                                                     value="<c:out value="${status.value}"/>" size="18" maxlength="15"/>
                                                     </spring:bind>
                                                  </td>
                                               </tr>
                                           </c:forEach>

		                           </c:if>
                                   <%--<tr align="left">
                                      <td class="activeTH" style="padding:2%; font-size:10pt">Payroll Run Details</td>
                                   </tr>
                                   <tr>
                                      <td class="activeTD" style="padding:2%">
                                         <p><b>Pay Period:</b> ${payPeriods.payPeriod}</p>
                                         <p><b>Pay Date:</b> ${payDay}</p>
                                         <p><b>Number of <c:out value="${roleBean.staffTypeName}"/> to be Processed:</b> ${payPeriods.toBeProcessedStaff}</p>
                                         <p><b>Number of <c:out value="${roleBean.mdaTitle}"/> to be Processed:</b> ${payPeriods.toBeProcessedMda}</p>
                                         <p><b>Number of Deductions to be processed:</b> ${payPeriods.toBeProcessedDeduction}</p>
                                         <p><b>Number of Special Allowance to be processed:</b> ${payPeriods.toBeProcessedSpecAllow} <p>
                                         <p><b>Number of Loans to be processed:</b> ${payPeriods.toBeProcessedLoan}<p>
                                         <p><b>Projected Gross Pay:</b> ${payPeriods.projectedGrossPayStr}<p>
                                         <p><b>Projected Net Pay:</b> ${payPeriods.projectedNetPayStr}<p>
                                         <p><b>Last Paycheck Gross Pay:</b> ${payPeriods.lastMonthGrossPayStr}<p>
                                         <c:if test="${payPeriods.deleteWarningIssued}">
                                            <p><b>Generated Captcha:</b> <c:out value="${payPeriods.generatedCaptcha}"/></p>
                                            <p>
                                                <b>Entered Captcha :</b>
                                                <form:input path="enteredCaptcha" size="5" maxlength="6"/>
                                                &nbsp;<font color="green">Case Insensitive</font>
                                                <c:if test="${payPeriods.captchaError}">
                             						&nbsp;<font color="red"><b>Entered Captcha does not match!</b></font>
                             					</c:if>
                                            </p>
                                         </c:if>
                                      </td>
                                   </tr> --%>
                                </table>
                            </td>
                        </tr>
                        <tr>
                           <td class="buttonRow" align="right">
                              <c:choose>
			                   	<c:when test="${payPeriods.deleteWarningIssued}">
			                   	    <input type="image" name="_confirm" value="confirm" title="Confirm Paycheck" src="images/confirm_h.png">&nbsp;
			                   	</c:when>
			                   	<c:otherwise>
                                    <input  style="margin-left:1%" type="image" name="_create" value="Create Check" alt="Create Paychecks" class='' src='images/Create_Paychecks_h.png' onclick="">
                                </c:otherwise>
                              </c:choose>
                              <input type="image" name="_cancel" value="cancel" alt="Cancel" src="images/close.png">
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
