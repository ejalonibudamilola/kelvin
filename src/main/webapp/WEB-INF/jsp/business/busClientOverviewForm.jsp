<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>
    <head>
        <title>
            Edit Company Account
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
    </head>
    <body class="main">
    <form:form modelAttribute="busClientBean">
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    Setup Overview
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainbody" id="mainbody">
                                To add, edit, or view information in a section, click the title of the section.
                                <table border="0" width="100%">
                                    <tr>
                                        <td></td>
                                    </tr>
                                </table>
                                <table width="100%" border="0" cellspacing="3" cellpadding="3">
                                    <tr>
                                        <td valign="top" width="33%">
                                            <h3>
                                                <a href='${appContext}/busClientContactEditForm.do?cid=${busClientBean.busClientContact.id}'>Contact Information</a>
                                            </h3>
                                            <table border="0" cellspacing="0" cellpadding="2">
                                                <tr>
                                                    <td align="left" valign="top">
                                                        <c:out value="${busClientBean.businessName}" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="left" valign="top">
                                                       <c:out value="${busClientBean.busClientContact.firstName}" />  <c:out value="${busClientBean.busClientContact.lastName}" />  
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="left" valign="top">
                                                        <c:out value="${busClientBean.busClientContact.email}" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="left" valign="top">
                                                       <c:out value="${busClientBean.busClientContact.businessPhone}" />
                                                    </td>
                                                </tr>
                                            </table>
                                            <h3>
                                                <a href='${appContext}/editBusAddrForm.do?cid=${busClientBean.addressInfo.id}'>Work Location</a>
                                            </h3>
                                            <table border="0" cellspacing="0" cellpadding="2">
                                                <tr>
                                                    <td align="left" valign="top">
                                                    <c:out value="${busClientBean.addressInfo.address1}" /> <c:out value="${busClientBean.addressInfo.address2}" /> <br/>
                                                      <c:out value="${busClientBean.addressInfo.city}" />,&nbsp; <c:out value="${busClientBean.addressInfo.state}" /> &nbsp;<c:out value="${busClientBean.addressInfo.zip}" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="left" valign="top"></td>
                                                </tr>
                                            </table>
                                            
                                            <table border="0" cellspacing="0" cellpadding="2"></table>
                                            <h3>
                                                <a href="${appContext}/busClientTaxDefnOverview.do">Tax Policy</a>
                                            </h3>
                                            <table border="1" cellspacing="0" cellpadding="2">
                                            	<c:choose>
                                            		<c:when test="${busClientBean.hasCustomTaxPolicy}">
                                            			<tr>
                                                			<th>Policy Name</th>
                                                			<th>Taxable %age</th>
                                                			<th>Rate</th>
                                                			<th>Tax Amount</th>
                                                		</tr>
                                            			<c:forEach items="${busClientBean.taxPolicies}" var="taxPolicy">
                                                		<tr>
                                                    		<td align="left">
                                                    		${taxPolicy.description}
                                                       		</td>
                                                       		<td align="center">
                                                    		${taxPolicy.taxablePercentageStr}
                                                       		</td>
                                                       		<td align="center">
                                                    		${taxPolicy.taxRateStr}
                                                       		</td>
                                                       		<td align="right">
                                                    		${taxPolicy.taxAmountStr}
                                                       		</td>
                                               			</tr>
                                               			</c:forEach>
                                                </c:when>
                                                <c:otherwise>
                                                		<tr>
                                                			<td align="left"><span class="label">Taxable Income</span></td>
                                                			<td align="left"><span class="label">Total Income</span></td>
                                                			<td align="left"><span class="label">Tax Rate</span></td>
                                                			<td align="left"><span class="label">Tax Due</span></td>
                                                		</tr>
                                               			 <c:forEach items="${busClientBean.defaultTaxRules}" var="taxRule">
                                                		<tr>
                                                    		<td align="left">
                                                       			<c:out value="${taxRule.taxableIncomeStr}"/>
                                                   			 </td>
                                                   			 <td align="left">
                                                       			<c:out value="${taxRule.cummulativeIncomeStr}"/>
                                                   			 </td>
                                                   			 <td align="left">
                                                       			<c:out value="${taxRule.taxRateStr}"/>
                                                   			 </td>
                                                   			 <td align="left">
                                                       			<c:out value="${taxRule.taxDueStr}"/>
                                                   			 </td>
                                               			</tr>
                                               			</c:forEach>
                                                </c:otherwise>
                                                </c:choose>
                                            </table>
                                           
                                        </td>
                                        <td valign="top" width="33%">
                                            <h3>
                                                <a href="${appContext}/clientPayInfoOverview.do">Pay Policies</a>
                                            </h3>
                                            <table border="0" cellspacing="0" cellpadding="2">
                                                <tr>
                                                    <td align="left" valign="top">
                                                        <span class="label">Schedules</span>:
                                                    </td>
                                                    <td align="left" valign="top">
                                                        <c:out value="${busClientBean.paySchedule}" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="left">
                                                        <span class="label">Vacation</span>:
                                                    </td>
                                                    <td align="left" valign="top">
                                                    	<table>
                                                    		<c:forEach items="${busClientBean.vacPolicyMessage}" varStatus="gridRow">
                                                    		<tr>
                                                    			<td>
                                                    				<c:out value="${busClientBean.vacPolicyMessage[gridRow.index]}" />
                                                    			</td>
                                                    		</tr>
                                                    		</c:forEach>
                              	                    	</table>
                                          	         </td>
                                                </tr>
                                                 <tr>
                                                    <td align="left">
                                                        <span class="label">Sick Policies</span>:
                                                    </td>
                                                    <td align="left" valign="top">
                                                    	<table>
                                                    		<c:forEach items="${busClientBean.sickPolicyMessage}" varStatus="gridRow">
                                                    		<tr>
                                                    			<td>
                                                    				<c:out value="${busClientBean.sickPolicyMessage[gridRow.index]}" />
                                                    			</td>
                                                    		</tr>
                                                    		</c:forEach>
                              	                    	</table>
                                          	         </td>
                                                </tr>
                                            </table>
                                            <!--
                                            <h3>
                                                <a href="${appContext}/clientTaxOverviewForm.do">Tax Setup</a>
                                            </h3>
                                            <table border="0" cellspacing="0" cellpadding="2">
                                                <tr>
                                                    <td align="left" valign="top">
                                                        <span class="label">Filing Name</span>:
                                                    </td>
                                                    <td align="left" valign="top">
                                                        <c:out value="${busClientBean.filingName}" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="left" valign="top">
                                                        <span class="label">FEIN</span>:
                                                    </td>
                                                    <td align="left" valign="top" nowrap>
                                                       <c:out value="${busClientBean.fein}" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="left" valign="top">
                                                        <span class="label">Registration/Incorporation No.</span>:
                                                    </td>
                                                    <td align="left" valign="top">
                                                       <c:out value="${busClientBean.withholdingId}" />
                                                    </td>
                                                </tr>
                                         
                                            </table>
                                            -->
                                            <h3>
                                                <a href='${appContext}/busContributionOverviewForm.do'>Company Contributions</a>
                                            </h3>
                                            <table border="0" cellspacing="0" cellpadding="2">
                                                <c:forEach items="${busClientBean.compContInfo}" var="compCont" >
                                                <tr>
                                                    <td align="left" valign="top">
                                                        <span> <c:out value="${compCont.description}" /></span>
                                                    </td>
                                                </tr>
                                                </c:forEach>
                                            </table>
                                            
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
