<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>
    <head>
        <meta name="generator" content="HTML Tidy for Windows (vers 18 June 2008), see www.w3.org">
        <title>
            Setup | Create Loan
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
    </head>
    <body class="main">
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                  
                                    <c:out value="${empGarnishInfo.displayName}"/><br>
                                    Employee : <c:out value="${namedEntity.name}"></c:out><br/>
                                     
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                <p>
                                    * = Required
                                </p>
                                <form:form modelAttribute="empGarnishInfo">
                                <div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="empGarnishInfo">
         							 <ul>
            							<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            							</c:forEach>
         							 </ul>
     							 </spring:hasBindErrors>
								</div>
                                    <table border="0" cellspacing="0" cellpadding="0" width="90%" id="pcform0">
                                        <tr>
                                            <td>
                                                <table class="formTable" border="0" cellspacing="0" cellpadding="2" width="100%" align="left">
                                                    
                                                    <tr align="left">
                                                        <td class="activeTH">
                                                        	<c:if test="${not empGarnishInfo.hasNegPay}">
                                                            <c:out value="${namedEntity.name}" />
                                                            </c:if>
                                                            <c:if test="${empGarnishInfo.hasNegPay}">
                                                            <c:out value="${empGarnishInfo.displayTitle}" />
                                                            </c:if>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td id="firstTD_editGarnishment_form" class="activeTD">
                                                           <table width="95%" border="0" cellspacing="0" cellpadding="2">
                                                                <tr>
                                                                    <td align="right" width="25%" nowrap>
                                                                        <span class="required"> Type*&nbsp;</span>
                                                                    </td>
                                                                    
                                                                    <c:choose>
																		<c:when test="${empGarnishInfo.delete or empGarnishInfo.viewOnlyMode}">
																		<td align="left" nowrap>
                                                                        <form:select path="empGarnishmentType.id" disabled="true">
                                                                            <form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
                                                                             <c:forEach items="${garnishType}" var="gtypes">
                                                                             <form:option value="${gtypes.id}">${gtypes.description}</form:option>
                                                                             </c:forEach>
                                                                        </form:select>
                                                                        </td>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                        <td align="left" nowrap>
                                                                        <form:select path="empGarnishmentType.id" disabled="${empGarnishInfo.edit}">
                                                                            <form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
                                                                             <c:forEach items="${garnishType}" var="gtypes">
                                                                             <form:option value="${gtypes.id}">${gtypes.description}</form:option>
                                                                             </c:forEach>
                                                                        </form:select>
                                                                        </td>
                                                                        </c:otherwise>
                                                                        </c:choose>
                                                                  
                                                                </tr><!--
                                                                <tr>
                                                                <td align="right" width="30%" nowrap><span class="required">Loan Type Name*&nbsp;</span></td>
                                                                <td align="left"><form:input path="description" size="20" maxlength="15"/></td>
                                                                                               	
                                                               </tr>
                                                               -->
                                                               <c:if test="${empGarnishInfo.delete or empGarnishInfo.edit}">
                                                               <tr>
                                                                <td align="right" width="30%" nowrap><span class="required">Original Amount ₦</span></td>
                                                                <td align="left"><form:input path="originalLoanAmountStr" disabled="true" size="10" maxlength="15"/></td>                                 	
                                                               </tr>
                                                               <tr>
                                                                <td align="right" width="30%" nowrap><span class="required">Remaining Balance* ₦</span></td>
                                                                <td align="left"><form:input path="owedAmountStr" size="10" maxlength="15"/></td>                                 	
                                                               </tr>
                                                               
                                                               <tr>
                                                                <td align="right" width="30%" nowrap><span class="required">Monthly Payment ₦</span></td>
                                                                <td align="left"><form:input path="garnishAmountStr" size="10" maxlength="15" disabled="true"/></td>                                 	
                                                               </tr>
                                                               </c:if>
                                                               <c:if test="${ not (empGarnishInfo.delete or empGarnishInfo.edit)}">
                                                               
                                                               <tr>
                                                                <td align="right" width="30%" nowrap><span class="required">Loan Amount* ₦</span></td>
                                                                <td align="left"><form:input path="owedAmountStr" size="10" maxlength="15"/></td>                                 	
                                                               </tr>
                                                               
                                                               </c:if>
                                                              
                                                                <tr>
                                                                
                                                                	<td align="right" width="30%" nowrap>
                                                                	 <c:if test="${ not (empGarnishInfo.delete or empGarnishInfo.edit)}">
                                                                	  <span class="required">Loan Term*&nbsp;</span>
                                                                	 </c:if>
                                                                	  <c:if test="${empGarnishInfo.delete or empGarnishInfo.edit}">
                                                                	 	<span class="required">Remaining Loan Term*&nbsp;</span>
                                                                	 </c:if>
                                                                		
                                                                	</td>
                                                                	<td align="left" nowrap>
                                                                	<c:choose>
																		<c:when test="${empGarnishInfo.delete}">
                                                                		<form:select path="loanTerm" disabled="true">
                                                                		 <form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
                                                                             <c:forEach items="${LoanTermList}" var="loanTerms">
                                                                             <form:option value="${loanTerms.currentOtherId}">${loanTerms.name}</form:option>
                                                                             </c:forEach>
                                                                		</form:select>
                                                                		</c:when>
                                                                		<c:otherwise>
                                                                		   <c:choose>
                                                                		   
                                                                		   <c:when test="${empGarnishInfo.edit}">
                                                                		   		<form:select path="newLoanTerm">
			                                                                		 	<form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
			                                                                             <c:forEach items="${loanTermList}" var="loanTerms">
			                                                                             <form:option value="${loanTerms.currentOtherId}">${loanTerms.name}</form:option>
			                                                                             </c:forEach>
			                                                                		</form:select>&nbsp;Original Loan Term&nbsp;
			                                                                		<form:select path="loanTerm" disabled="true">
		                                                                		 	<form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
			                                                                             <c:forEach items="${loanTermList}" var="loanTerms">
			                                                                             <form:option value="${loanTerms.currentOtherId}">${loanTerms.name}</form:option>
			                                                                             </c:forEach>
		                                                                			</form:select>&nbsp;Current Loan Term&nbsp;
			                                                                		<form:select path="currentLoanTerm" disabled="true">
		                                                                		 	<form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
			                                                                             <c:forEach items="${loanTermList}" var="loanTerms">
			                                                                             <form:option value="${loanTerms.currentOtherId}">${loanTerms.name}</form:option>
			                                                                             </c:forEach>
		                                                                			</form:select>
		                                                                				
                                                                		   </c:when>
                                                                		   <c:otherwise>
                                                                		   			<form:select path="loanTerm">
			                                                                		 	<form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
			                                                                             <c:forEach items="${loanTermList}" var="loanTerms">
			                                                                             <form:option value="${loanTerms.currentOtherId}">${loanTerms.name}</form:option>
			                                                                             </c:forEach>
			                                                                		</form:select>
                                                                		   </c:otherwise>
                                                                		  </c:choose>                                                                		 
                                                                		</c:otherwise>
                                                                		</c:choose>
                                                                	</td>
                                                              		                              	
                                                               </tr>
                                                               </table>
                                                        </td>
                                                    </tr>
                                                </table>
                                            </td>
                                        </tr>
                                        <tr>
						<td class="buttonRow" align="right" >
							<c:choose>
								<c:when test="${empGarnishInfo.delete}">
									<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
							 		<input type="image" name="_delete" value="delete" title="Delete" class="" src="images/delete_h.png">
							 	</c:when>
							 	<c:otherwise>
							 		<c:if test="${not empGarnishInfo.viewOnlyMode}">
							 			<c:if test="${empGarnishInfo.confirmation}">
							 				<input type="image" name="submit" value="ok" title="Ok" class="" src="images/confirm_h.png">
							 			</c:if>
										<c:if test="${not empGarnishInfo.confirmation}">
							 				<input type="image" name="submit" value="ok" title="Ok" class="" src="images/ok_h.png">
							 			</c:if>
									</c:if>
									<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</table>
				</form:form>
				</table>
			</td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
</body>
</html>
