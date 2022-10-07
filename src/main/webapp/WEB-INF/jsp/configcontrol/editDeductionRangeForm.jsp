<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Edit Deduction Range Form </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="styles/epayroll.css" type="text/css"/>

</head>

<body class="main">
<form:form modelAttribute="miniBean">
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
	<tr>
		<td colspan="2">
		<div class="title">
			Edit Deduction Range Form<br>
			 </div>
		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">
		
			
		
		<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
			
			<tr>
				<td class="reportFormControlsSpacing">&nbsp;</td>
			</tr>
			<tr>
				<td>
                    <table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align="right" width="35%">
										&nbsp;
									</td>
								</tr>

								<tr>
									<td align="right" nowrap>
										<span class="required">Department Name*</span>&nbsp;
									</td>
									<td><form:input path="name" size="50" maxlength="120"/></td>
								</tr>
								<tr>
									<td align="right" nowrap>
										<span class="required">Description*</span>&nbsp;
									</td>
									<td><form:input path="description" size="50" maxlength="120"/></td>
								</tr>
								 <tr>
                                 	 <td align="right" nowrap><span class="required">Default Department</span>&nbsp;</td>
                                 	 <td><form:checkbox path="defaultIndBind" /></td>
                                  </tr>
								 <c:if test="${clientDept.editMode}">

								 <tr>
								     <td align="right" nowrap><span class="required">Global Change*</span></td>
                  						<td><spring:bind path="globalChange" >
                 						    <input type="hidden" name="_<c:out value="${status.expression}"/>">
                 							<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if> title="If checked, Department Name and/or Description changes to this Department will be propagated to ALL MDA's having similar 'Department'." />
     										</spring:bind>
     			  						 </td>

               					 </tr>


								 </c:if>
					</table>


                    <h3><c:out value="${miniBean.name}"/></h3>
                    <br />

                    <table class="report" cellspacing="0" cellpadding="0">
                            <tr class="reportOdd header">
                            <td class="tableCell" valign="top" width="97px" align="left">Lower Bound</td>
                            <td class="tableCell" width="97px" valign="top" align="left">Upper Bound</td>
                            <td class="tableCell" width="97px" valign="top" align="left">Amount</td>
                        </tr>
                    </table>
				    <div style="overflow:scroll;height:400px;width:100%;overflow:auto">
                        <table class="report" cellspacing="0" cellpadding="0">
                            <c:forEach items="${miniBean.rangedDeductionDetailsList}" var="dedMiniBean" varStatus="gridRow">
                            <tr class="${dedMiniBean.displayStyle}">
                                <td class="tableCell" valign="top" width="97px" align="left">
                                    <spring:bind path="miniBean.rangedDeductionDetailsList[${gridRow.index}].lowerBoundAsStr">
                                    <input type="text" name="<c:out value="${status.expression}"/>"
                                        id="<c:out value="${status.expression}"/>"
                                        value="<c:out value="${status.value}"/>" size="6" maxlength="10"/>
                                    </spring:bind>
                                 </td>
                                <td class="tableCell" valign="top" width="97px" align="left">
                                    <spring:bind path="miniBean.rangedDeductionDetailsList[${gridRow.index}].upperBoundAsStr">
                                    <input type="text" name="<c:out value="${status.expression}"/>"
                                        id="<c:out value="${status.expression}"/>"
                                        value="<c:out value="${status.value}"/>" size="6" maxlength="10"/>
                                    </spring:bind>
                                 </td>
                                <td class="tableCell" valign="top" width="97px" align="left">
                                    <spring:bind path="miniBean.rangedDeductionDetailsList[${gridRow.index}].amountAsStr">
                                    <input type="text" name="<c:out value="${status.expression}"/>"
                                        id="<c:out value="${status.expression}"/>"
                                        value="<c:out value="${status.value}"/>" size="6" maxlength="10"/>
                                    </spring:bind>
                                 </td>
                            </tr>
                            </c:forEach>
                        </table>
				    </div>
				</td>
			</tr>
 			<tr>
                 <td class="buttonRow" align="right">
                  <c:choose>
                     <c:when test="${not miniBean.confirmation and not saved}">
                         <input type="image" name="_done" value="done" title="Save" src="images/done_h.png">
                        </c:when>
                        <c:otherwise>
                         <c:if test="${not miniBean.confirmation and not saved}">
                            <input type="image" name="_done" value="done" title="Save" src="images/done_h.png">
                         </c:if>
                          <c:if test="${miniBean.confirmation}">
                           <input type="image" name="_confirm" value="confirm" title="Confirm Configuration" src="images/confirm_h.png">
                           </c:if>
                           </c:otherwise>
                            </c:choose>
                            <input type="image" name="_cancel" value="cancel" title="Close" src="images/cancel_h.png">


                    </td>
            </tr>
		</table>
		
		<br>
		
		</td>
		<td valign="top" class="navSide"><br>
		<br>
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
