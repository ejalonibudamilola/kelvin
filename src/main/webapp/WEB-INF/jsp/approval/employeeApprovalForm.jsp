<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> <c:out value="${pageTitle}"/> </title>
<link rel="stylesheet" href=<c:url value="styles/omg.css"/> type="text/css" />
<link rel="Stylesheet" href=<c:url value="styles/skye.css"/> type="text/css" media ="screen">
<script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
<script src="<c:url value="/scripts/utility_script.js"/>"></script>
</head>

<body class="main">
	<table class="main" width="70%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					<tr>
						<td colspan="2">
						        
								<div class="title"> <c:out value="${mainHeader}"/></div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
			<form:form modelAttribute="miniBean">
			<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="miniBean">
         							 <ul>
            							<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            							</c:forEach>
         							 </ul>
     							 </spring:hasBindErrors>
					</div>
				<table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
					<tr align="left">
						<td class="activeTH"><b><c:out value="${tableHeader}"/></b></td>
					</tr>
					<tr>
						<td class="activeTD">
							<table width="100%" border="0" cellspacing="0" cellpadding="2">
							   <c:choose>
							    <c:when test="${hasChildObject}">
                                <tr>
						            <td align="right" width="25%"><b><c:out value="${roleBean.staffTypeName}"/> Name :</b></td>
						              	<td><c:out value="${miniBean.childObject.displayName}"/>
						               </td>
								</tr>
						          <tr>
						            <td align="right" width="25%"><b><c:out value="${roleBean.staffTitle}"/> :</b></td>

									     <td width="25%">
						                   <c:out value="${miniBean.childObject.employeeId}"/>
									     </td>

						            </tr>
						            <tr>
						            <td align="right" width="25%"><b><c:out value="${roleBean.mdaTitle}"/> :</b></td>

									     <td width="25%">
						                   <c:out value="${miniBean.childObject.parentObjectName}"/>
									     </td>

						            </tr>
						            <tr>
						            <td align="right" width="25%"><b>Pay Group :</b></td>

									     <td width="25%">
						                   <c:out value="${miniBean.childObject.salaryInfo.salaryScaleLevelAndStepStr}"/>
									     </td>

						            </tr>


							    </c:when>
							    <c:otherwise>
                                <tr>
						            <td align="right" width="25%"><b><c:out value="${roleBean.staffTypeName}"/> Name :</b></td>
						              	<td><c:out value="${miniBean.hiringInfo.pensioner.displayName}"/>
						               </td>
								</tr>
						          <tr>
						            <td align="right" width="25%"><b><c:out value="${roleBean.staffTitle}"/> :</b></td>

									     <td width="25%">
						                   <c:out value="${miniBean.hiringInfo.pensioner.employeeId}"/>
									     </td>

						            </tr>
						            <tr>
						            <td align="right" width="25%"><b><c:out value="${roleBean.mdaTitle}"/> :</b></td>

									     <td width="25%">
						                   <c:out value="${miniBean.hiringInfo.pensioner.parentObjectName}"/>
									     </td>

						            </tr>
                                    <tr>
						            <td align="right" width="25%"><b>Pension Start Date :</b></td>

									     <td width="25%">
						                   <c:out value="${miniBean.hiringInfo.pensionStartDateStr}"/>
									     </td>

						            </tr>
						            <tr>
						            <td align="right" width="25%"><b>Monthly Pension :</b></td>

									     <td width="25%">
						                   <c:out value="${miniBean.hiringInfo.monthlyPensionStr}"/>
									     </td>

						            </tr>
						            <tr>
						            <td align="right" width="25%"><b>No of Years on Pension :</b></td>

									     <td width="25%">
						                   <c:out value="${miniBean.hiringInfo.yearsOnPension}"/>
									     </td>

						            </tr>
							    </c:otherwise>
							    </c:choose>

						          <tr>
						             <td align="right" width="25%"><b>Created By :</b></td>
						             
									      <td width="25%">
						                   <c:out value="${miniBean.initiator.actualUserName}"/>
									      </td>
									    
						    		 <td>&nbsp;</td>
						          </tr>
						          <tr>
						            <td align="right" width="25%"><b>Created Date :</b></td>
						             
									       <td width="25%">
						                     <c:out value="${miniBean.initiatedDateStr}"/>	
									       </td>
										 
						          </tr>
						          
						          	<tr>
						          	
						               <td align="right" width="25%"><b>Memo*:</b></td>
						             
									       <td width="25%">
						                     <form:textarea path="approvalMemo" rows="5" cols="35" />
									       </td>
										 
						            </tr>
						     <c:if test="${miniBean.confirmed}">
                            <tr>
						    <td>

                        						<div id="confirmPageBoxedMessage" style="display:${miniBean.displayErrors}">
                        								 <spring:hasBindErrors name="miniBean">
                                 							 <ul>
                                    							<c:forEach var="errMsgObj" items="${errors.allErrors}">
                                       								<li>
                                          							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
                                       								</li>
                                    							</c:forEach>
                                 								</ul>
                             							 </spring:hasBindErrors>
                        						</div>
                        						<table>
                        					            <tr>
                        									<td align="left" width="20%" nowrap>
                        									<span class="required">Generated Code*</span></td>
                        									<td width="60%">
                        										<form:input path="generatedCaptcha" size="8" maxlength="8" disabled="true" />
                        									</td>
                        					            </tr>
                        					            <tr>
                        									<td align="left" width="20%" nowrap>
                        									<span class="required">Enter Code Above*</span></td>
                        									<td width="60%">
                        										<form:input path="enteredCaptcha" size="8" maxlength="8" />&nbsp;<font color="green">case insensitive.</font>
                        									</td>
                        					            </tr>
                        					       </table>

                        	</td>
                        </tr>
						   </c:if>
						          
							</table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
						  <c:choose>
						   <c:when test="${miniBean.confirmed}">
						     <input type="image" name="_confirm" value="confirm" title="Confirm" class="" src="images/confirm_h.png">
						     <input type="image" name="_cancel" value="cancel" title="Close" class="" src="images/close.png">
						   </c:when>
                           <c:otherwise>
							 <c:choose>
								 <c:when test="${saved or not roleBean.superAdmin}">
								 		<input type="image" name="_cancel" value="cancel" title="Close" class="" src="images/close.png">
								 
									  </c:when>
							     <c:otherwise>
							         <c:choose>
							         	<c:when test="${miniBean.approved}">
							         		<input type="image" name="_reject" value="reject" title="Reject" class="" src="images/reject_h.png">
									  		<input type="image" name="_cancel" value="cancel" title="Cancel operation" class="" src="images/cancel_h.png">
								
							         	</c:when>
							         	<c:otherwise>
							         		<input type="image" name="_approve" value="approve" title="Approve" class="" src="images/approve.png">
											<input type="image" name="_reject" value="reject" title="Reject" class="" src="images/reject_h.png">
							         	    <input type="image" name="_cancel" value="cancel" title="Cancel operation" class="" src="images/cancel_h.png">
							         	</c:otherwise>
							         
							         </c:choose>
							    	 
							     </c:otherwise>
							 </c:choose>
						  </c:otherwise>
						  </c:choose>
							</td>
					</tr>
				</table>
				</form:form>
				</td>
				</tr>
				</table>
			</td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
</body>
</html>
