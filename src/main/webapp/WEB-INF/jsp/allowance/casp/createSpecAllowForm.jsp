<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>
    <head>

        <title>
            Setup | Create Special Allowance
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="/styles/epayroll.css"/>" type="text/css"/>
        <script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>

    </head>
    <body class="main">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    Create Special Allowance for<br>
                                    <c:out value="${namedEntity.name}"/>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                <p>
                                    * = Required
                                </p>
                                <form:form modelAttribute="empSpecAllow">
                                <div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="empSpecAllow">
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
                                                            Create Special Allowance for <c:out value="${namedEntity.name}" />
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td id="firstTD_editGarnishment_form" class="activeTD">
                                                           <table width="95%" border="0" cellspacing="0" cellpadding="2">
                                                                <tr>
                                                                    <td align="right" width="25%" nowrap>
                                                                        <span class="required"> Type*&nbsp;</span>
                                                                    </td>
                                                                    
                                            
                                                                        <td align="left" nowrap>
                                                                        <form:select path="typeInstId"  onchange="loadPayTypes(this)">
                                                                            <form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
                                                                             <c:forEach items="${specAllowType}" var="gtypes">
                                                                             <form:option value="${gtypes.id}">${gtypes.description}</form:option>
                                                                             </c:forEach>
                                                                        </form:select>
                                                                        </td>
                                                                  
                                                                </tr>
                                                               <tr>
                                                                <td align="right" width="30%" nowrap><span class="required">Apply As*&nbsp;</span></td>
                                                                <td align="left" nowrap>
                                                                        <form:select path="payTypeInstId" id="pay-type-control" cssClass="payTypeControls">
                                                                            <form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
                                                                             <c:forEach items="${payType}" var="ptypes">
                                                                             <form:option value="${ptypes.id}">${ptypes.name}</form:option>
                                                                             </c:forEach>
                                                                        </form:select>
                                                                </td>
                                                                                               	
                                                               </tr><!--
                                                               <tr>
                                                                <td align="right" width="30%" nowrap>Allowance Name*</td>
                                                                <td align="left"><form:input path="description" size="10" maxlength="15"/></td>   
                                                                </tr>
                                                               --><tr>
                                                                <td align="right" width="30%" nowrap>Allowance Value*&nbsp;</td>
                                                                <td align="left"><form:input path="amountStr" size="8" maxlength="10"/></td>
                                                                </tr>
                                                                <tr>
								 									 <td align="right" width="30%">Allowance Start Date*</td>
					              									 <td align="left"><form:input path="startDate"/><img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('startDate'),event);"></td>
					             									<td><div class="note" nowrap><font color="green"><b>Date to start paying this allowance</b></font></div></td>
																</tr>
						        								<tr>
								 									 <td align="right" width="30%">Allowance End Date*</td>
					              									 <td align="left"><form:input path="endDate"/><img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('endDate'),event);"></td>
					             									<td><div class="note" nowrap><font color="green"><b>Date to stop paying this allowance</b></font></div></td>
																</tr>  
																<c:if test="${empSpecAllow.warningIssued}">
																<tr>
								 									 <td align="right" width="30%">Reference Number</td>
					              									 <td align="left"><form:input path="referenceNumber"/></td>
					             									
																</tr> 
																<tr>
								 									 <td align="right" width="30%">Reference Date*</td>
					              									 <td align="left"><form:input path="referenceDate"/><img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('referenceDate'),event);"></td>
																</tr>  
																</c:if>                                                
                                                            </table>
                                                        </td>
                                                    </tr>
                                                </table>
                                            </td>
                                        </tr>
                                        <tr>
											<td class="buttonRow" align="right" >
												<input type="image" name="submit" value="ok" title="Ok" class="" src="images/ok_h.png">
												<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
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
