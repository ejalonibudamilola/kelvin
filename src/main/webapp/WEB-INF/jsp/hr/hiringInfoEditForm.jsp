<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Hiring Form  </title>
<link rel="stylesheet" href="<c:url value="styles/omg.css"/>" type="text/css" />
<link rel="Stylesheet" href="<c:url value="styles/skye.css"/>" type="text/css" media ="screen">
<link rel="stylesheet" href="<c:url value="/styles/epayroll.css"/>" type="text/css"/>
<script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
<script src="<c:url value="/scripts/utility_script.js"/>"></script>
</head>

<body class="main">
<script type="text/JavaScript" src="scripts/jacs.js"></script>
	<table class="main" width="70%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					<tr>
						<td colspan="2">
								<div class="title">Edit Hiring Information for: <c:out value="${namedEntity.name}"/></div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
			<form:form modelAttribute="hiringInfo">
			<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="hiringInfo">
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
						<td class="activeTH"><b><c:out value="${namedEntity.name}"/>'s hiring information</b></td>
					</tr>
					<tr>
						<td class="activeTD">
							<table width="100%" border="0" cellspacing="0" cellpadding="2">
								<tr>
						            <td align="right" width="25%">Pay Schedule*</td>
						              	<td align="left" width="75%"><c:out value="${hiringInfo.payPeriodName}"/>
						               </td>
								</tr>
						          <tr>
						            <td align="right" width="25%">Birth Date*</td>
						            <c:choose>
									   <c:when test="${hiringInfo.canEdit}">
									     <td align="left" width="75%">
						                   <form:input path="birthDate"/>
										  <img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('birthDate'),event);"></td>
									    </c:when>
									    <c:otherwise>
									     <td align="left" width="75%">
						                   <c:out value="${hiringInfo.birthDateStr}"/>	
									     </td>									    
									     </c:otherwise>
									</c:choose> 
						            </tr>
						          <tr>
						             <td align="right" width="25%">Gender*</td>
						              <c:choose>
									   <c:when test="${hiringInfo.canEdit}">
									   <td align="left" width="75%"><form:radiobutton path="gender" value="M" />Male <form:radiobutton path="gender" value="F"/> Female</td>
									    </c:when>
									    <c:otherwise>
									      <td align="left" width="75%">
						                   <c:out value="${hiringInfo.genderStr}"/>	
									      </td>
									     </c:otherwise>
									     </c:choose>
						    		 <td>&nbsp;</td>
						          </tr>
						          <tr>
						            <td align="right" width="25%">Hire Date*</td>
						            <c:choose>
									   <c:when test="${hiringInfo.canEdit}">
									   	 <td align="left" width="75%"><form:input path="hireDate"/>
										 <img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('hireDate'),event);"></td>
									    </c:when>
									    <c:otherwise>
									       <td align="left" width="75%">
						                     <c:out value="${hiringInfo.hireDateStr}"/>	
									       </td>
										</c:otherwise>
										</c:choose>
						          </tr>
						          <tr>
						            <td align="right" width="25%">Confirmation Date</td>
						            <c:choose>
									   <c:when test="${hiringInfo.canEdit}">
									   	 <td align="left" width="75%"><form:input path="confirmDate"/>
										 <img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('confirmDate'),event);"></td>
									    </c:when>
									    <c:otherwise>
									       <td align="left" width="75%">
						                     <c:out value="${hiringInfo.confirmDateStr}"/>	
									       </td>
										</c:otherwise>
										</c:choose>
						          </tr>
                                    <c:if test="${roleBean.subeb}">
								    <tr>
                                  	 <td align=right><span class="required">SUBEB Staff Type*</span></td>
                                  		 <td align="left">
                                  		  <form:select path="staffTypeId">
                                  		  <form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
                                  		  <c:forEach items="${subTypeList}" var="empSubtype">
                                  			 <form:option value="${empSubtype.id}" title="${empSubtype.description}" >${empSubtype.name}</form:option>
                                  		 </c:forEach>
                                  		 </form:select>
                                  			 </td>
                                  			 <td align="left"></td>
                                  	 </tr>
                                  	 <tr>
                                  	  <td align=right><span class="required">SUBEB Staff Designation*</span></td>
                                  	  <td align="left">
                                  	 <form:select path="staffDesignationId">
                                  	 <form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
                                  	 <c:forEach items="${designationList}" var="empDesignation">
                                  	      <form:option value="${empDesignation.id}" title="${empDesignation.description}" >${empDesignation.name}</form:option>
                                  	 </c:forEach>
                                  	  </form:select>
                                  	  </td>
                                  	 </tr>
                                  	 </c:if>
						          <c:if test="${roleBean.pensioner}" >
						          <tr>
                                  	<td align="right" width="25%">Service Termination Date</td>
                                  		<c:choose>
                                  			 <c:when test="${hiringInfo.canEdit}">
                                  				 <td align="left" width="75%"><form:input path="terminateDate"/>
                                  					<img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('terminateDate'),event);"></td>
                                  				  </c:when>
                                  					 <c:otherwise>
                                  						<td align="left" width="75%">
                                  						    <c:out value="${hiringInfo.terminateDateStr}"/>
                                  						 </td>
                                  					 </c:otherwise>
                                  		</c:choose>
                                  </tr>
                                  <tr>
                                      <td align="right" width="25%">Pension Start Date</td>
                                           <c:choose>
                                              <c:when test="${hiringInfo.canEdit}">
                                                 <td align="left" width="75%"><form:input path="pensionStartDate" disabled="${hiringInfo.politicalOfficeHolderType}"/>
                                                     <img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('pensionStartDate'),event);"></td>
                                               </c:when>
                                               <c:otherwise>
                                                  <td align="left" width="75%">
                                                      <c:out value="${hiringInfo.pensionStartDateStr}"/>
                                                  </td>
                                               </c:otherwise>
                                               </c:choose>
                                    </tr>
                                  </c:if>
						          <tr>
						            <td align="right" width="25%">Marital Status</td>
						            <c:choose>
									   <c:when test="${hiringInfo.canEdit}">
									   	 <td align="left" width="75%">
						            		<form:select path="maritalStatus.id">
						            		<form:option value="0">&lt;Select&gt;</form:option>
						                	<c:forEach items="${maritalStatusList}" var="sList">
						               		 <form:option value="${sList.id}">${sList.name}</form:option>
						                	</c:forEach>
						               		</form:select>
						               		</td>
						               	</c:when>
									    <c:otherwise>
									       <td align="left" width="75%">
						                     <c:out value="${hiringInfo.maritalStatus.name}"/>	
									       </td>
										</c:otherwise>
										</c:choose>
						          </tr>
						         
						          	<c:if test="${hiringInfo.contractStaff}">
						          		
						           		 <c:choose>
						           			 <c:when test="${hiringInfo.canEdit}">
						           			 <tr>
						            			<td align="right" width="25%">Contract Start Date:</td>
						           				 <td align="left" width="75%"><form:input path="contractStartDate"/>
										 		<img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('contractStartDate'),event);"></td>
								  			</tr>
								  			<tr>
						            		<td align="right" width="25%">Contract End Date:</td>
						            		 <td align="left" width="75%"> <form:input path="contractEndDate"/>
										 		<img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('contractEndDate'),event);"></td>
								  			</tr>
						          		  </c:when>
						            	<c:otherwise>
						            		<tr>
						            			<td align="right" width="25%">Contract Start Date:</td>
						            			 <td width="75%" align="left">
												<c:out value="${hiringInfo.contractStartDateStr}"/></td>
										     </tr>
										     <tr>
										     <td align="right" width="25%">Contract End Date:</td>
										        <td width="75%" align="left">
												<c:out value="${hiringInfo.contractEndDateStr}"/></td>
												</tr>
						           		 </c:otherwise>
						         		 </c:choose>
						     		</c:if>

						     		 <c:if test="${hiringInfo.nonContractStaff and not roleBean.pensioner}">
						     		   <tr>
							            	<td align="right" width="25%">Pensionable <c:out value="${roleBean.staffTypeName}"/>:</td>
							           		 <td align="left" width="75%">
						            		<form:select path="pensionableInd" disabled="${hiringInfo.politicalOfficeHolderType}">
						            		<form:option value="-1">&lt;Select&gt;</form:option>
						                	<c:forEach items="${respList}" var="rAllow">
						               		 <form:option value="${rAllow.id}">${rAllow.name}</form:option>
						                	</c:forEach>
						               		</form:select>
						               		</td>
						               		</tr>
						          
							          
						     		<tr>
						            <td align="right" width="25%" nowrap>Pension Fund Administrator(PFA)</td>
						            <c:choose>
									   <c:when test="${hiringInfo.canEdit}">
									   	 <td align="left" width="75%">
						            		<form:select path="pfaInfo.id" disabled="${hiringInfo.politicalOfficeHolderType}">
						            		<form:option value="-1">&lt;Select&gt;</form:option>
						                	<c:forEach items="${pfaList}" var="pList">
						               		 <form:option value="${pList.id}">${pList.name}</form:option>
						                	</c:forEach>
						               		</form:select>
						               		</td>
						               	</c:when>
									    <c:otherwise>
									       <td align="left" width="75%">
						                     <c:out value="${hiringInfo.pfaInfo.name}"/>	
									       </td>
										</c:otherwise>
										</c:choose>
						          </tr>
						          <tr>
						            <td align="right" width="25%">Pension PIN Code</td>
						            <c:choose>
									   <c:when test="${hiringInfo.canEdit}">
									   	 <td align="left" width="75%">
						            		 <form:input path="pensionPinCode" size="20" maxlength="15" disabled="${hiringInfo.politicalOfficeHolderType}"/>
						            		
						               		</td>
						               	</c:when>
									    <c:otherwise>
									       <td align="left" width="75%">
						                     <c:out value="${hiringInfo.pensionPinCode}"/>	
									       </td>
										</c:otherwise>
										</c:choose>
						          </tr>
						     		</c:if>
						     		<c:if test="${not roleBean.pensioner}">
							        <c:choose>
							        	 <c:when test="${hiringInfo.canEdit}">
							            <tr>
							            <td align="right" width="25%">Last Promotion Date:</td>
							            <td align="left" width="75%"><form:input path="lastPromotionDate"/>
											 <img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('lastPromotionDate'),event);"></td>
									  
							         	</c:when>
							            <c:otherwise>
							             <tr>
							            <td align="right" width="25%">Last Promotion Date:</td>
							             <td align="left" width="75%">
											<c:out value="${hiringInfo.lastPromotionDateStr}"/></td>
							            </c:otherwise>
							            </c:choose>
                                    <tr>
						             <td align="right" width="25%">STIN</td>
						               	 <td><form:input path="tin" size="8" maxlength="14" /></td>
									 <td>&nbsp;</td>
						          </tr>
							        </c:if>

						          <tr style="${hiringInfo.showTerminated}">
						           <td align="right" width="25%">Terminated/Inactive:</td>
						           <td align="left" width="75%"><c:out value="${hiringInfo.terminatedStr}"/></td>
						    	  </tr>
						           <tr style="${hiringInfo.showTerminated}">
						            <td align="right" width="25%">Terminated On:</td>
						            <td width="75%" align="left">
										<c:out value="${hiringInfo.terminatedDateStr}"/>
									</tr>
									<tr style="${hiringInfo.showTerminated}">
									<td align="right" width="25%">Reason:</td>
									<td width="75%" align="left">
										<c:out value="${hiringInfo.terminateReason.name}"/>
									</td>
						          </tr>

						          <c:if test="${roleBean.pensioner}">
						          <tr>
                                  	 <td align="right" width="25%">Annual Pension Amount:</td>
                                  	   <td><form:input path="yearlyPensionAmountStr" size="20" maxlength="15" disabled="true" /></td>
                                  	 </tr>
                                   <tr>
                                  		 <td align="right" width="25%">Monthly Pension Amount:</td>
                                  	 <td><form:input path="monthlyPensionAmountStr" size="20" maxlength="15" disabled="true" /></td>
                                   </tr>
                                  	 <c:if test="${hiringInfo.hasGratuityInfo}">
                                  		 <tr>
                                  			 <td align="right" width="25%">Gratuity:</td>
                                  				 <td><form:input path="gratuityAmountStr" size="20" maxlength="15" disabled="true" /></td>
                                  		 </tr>
                                  		 <tr>
                                  			 <td align="right" width="25%">Gratuity Paid To Date:</td>
                                  				 <td><form:input path="monthlyGratuityAmountAsStr" size="20" maxlength="15" disabled="true" /></td>
                                  		 </tr>

                                  	 </c:if>

                                  	</c:if>


							</table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
							 <c:choose>
								 <c:when test="${hiringInfo.terminated}">
									  <input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
								 </c:when>
							     <c:otherwise>
							         <c:choose>
							         	<c:when test="${saved}">
												<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/close.png">
							         	</c:when>
							         	<c:otherwise>
							         		<input type="image" name="submit" value="ok" alt="Ok" class="" src="images/ok_h.png">
											<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
							         	
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
