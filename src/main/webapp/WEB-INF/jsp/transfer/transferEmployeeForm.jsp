<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Transfer <c:out value="${roleBean.staffTypeName}"/> Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="stylesheet" href="<c:url value="/styles/epayroll.css"/>" type="text/css"/>
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>
</head>


<body class="main">
<script type="text/JavaScript" src="scripts/jacs.js"></script>
	<table class="main" width="70%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					

					<tr>
						<td colspan="2">
								<div class="title">Transfer <c:out value="${miniBean.abstractEmployeeEntity.displayNameWivTitlePrefixed}" />&nbsp;[ <c:out value="${miniBean.abstractEmployeeEntity.employeeId}" /> ]</div>
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
						<td class="activeTH"><c:out value="${roleBean.staffTypeName}"/> Information</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align="left" width="25%">
										<span class="required"><c:out value="${roleBean.staffTypeName}"/> Name</span></td>
									<td width="25%">
										<c:out value="${miniBean.abstractEmployeeEntity.displayNameWivTitlePrefixed}"/>
									</td>
									
					            </tr>
								<tr>
									<td align="left"><span class="required"><c:out value="${roleBean.staffTitle}"/></span></td>
									<td width="25%">
										<c:out value="${miniBean.abstractEmployeeEntity.employeeId}"/>
									</td>
								
					            </tr>
					            <tr>
									<td align="left" width="25%"><span class="optional"><c:out value="${roleBean.mdaTitle}"/> (Current)</span></td>
									<td width="25%">
										<c:out value="${miniBean.abstractEmployeeEntity.mdaDeptMap.mdaInfo.name}"/>
									</td>
					            </tr>
					            <c:if test="${saved and not miniBean.abstractEmployeeEntity.schoolEnabled}">
					            	<tr>
					            	<c:choose>
					              	<c:when test="${needsApproval}">
					              		<td align="left" width="25%"><span class="optional"><c:out value="${roleBean.mdaTitle}"/> (Proposed)</span></td>
										</c:when>
					              	<c:otherwise>
					              		<td align="left" width="25%"><span class="optional"><c:out value="${roleBean.mdaTitle}"/> (Transferred From)</span></td>
									</c:otherwise>
					              </c:choose>
					            	<td width="25%">
											<c:out value="${miniBean.proposedMda}"/>
										</td>
					              	 </tr>
					            </c:if>
					            
					            <c:if test="${miniBean.abstractEmployeeEntity.schoolEnabled}">
					             <tr>
									<td align="left" width="25%"><span class="optional">School (Current)</span></td>
									<td width="25%">
										<c:out value="${miniBean.abstractEmployeeEntity.schoolInfo.name}"/>
									</td>
					             </tr>
					             
					              <c:if test="${saved}">
					              <tr>
					              <c:choose>
					              	<c:when test="${needsApproval}">
					              		<td align="left" width="25%"><span class="optional">School (Proposed)</span></td>
										</c:when>
					              	<c:otherwise>
					              		<td align="left" width="25%"><span class="optional">School (Transferred From)</span></td>
									</c:otherwise>
					              </c:choose>
					             	 <td width="25%">
											<c:out value="${miniBean.proposedMda}"/>
										</td>
					              	 </tr>
					              </c:if>
					            </c:if>

					             <c:choose>
					             <c:when test="${roleBean.pensioner}">
                                 <tr>
									<td align="left" width="25%"><span class="required">Pension Start Date</span></td>
									<td width="25%">
										<c:out value="${miniBean.pensionStartDateStr}"/>
									</td>

					            </tr>
								<tr>
									<td align="left" width="25%"><span class="required">Years On Pension</span></td>
									<td width="25%">
										<c:out value="${miniBean.yearsOnPension}"/>
									</td>									
					            </tr>

								<tr>
									<td align="left" width="25%"><span class="required">Monthly Pension</span></td>
									<td width="25%">
										<c:out value="${miniBean.monthlyPensionStr}"/>
									</td>									
					            </tr>
					            </c:when>
					            <c:otherwise>
                                <tr>
									<td align="left" width="25%"><span class="required">Hire Date</span></td>
									<td width="25%">
										<c:out value="${miniBean.hireDateStr}"/>
									</td>

					            </tr>
                                <tr>
									<td align="left" width="25%"><span class="required">Years in Service</span></td>
									<td width="25%">
										<c:out value="${miniBean.yearsOfService}"/>
									</td>
					            </tr>

								<tr>
									<td align="left" width="25%"><span class="required">Current Level &amp; Step</span></td>
									<td width="25%">
										<c:out value="${miniBean.abstractEmployeeEntity.gradeLevelAndStep}"/>
									</td>
					            </tr>
					            </c:otherwise>
					            </c:choose>
					            
					            <c:if test="${not saved}">
					             
							         <tr>
						              		<td width="35%" align="left"><span class="required"><c:out value="${roleBean.mdaTitle}"/> (New)*</span></td>
						              			<td>   
						                		<form:select path="mdaId" onchange='loadSchoolsByMdaId(this); loadDepartments(this)'>
						                		<form:option value="0">&lt;Please select &gt;</form:option>
						                		<c:forEach items="${mdaList}" var="mList">
						                		<form:option value="${mList.id}" >${mList.name}</form:option>
						                		</c:forEach>
						                		</form:select>
						             		 </td>
						              			
						            </tr>
						         <tr>
					              <td height="35%" align="left"><span class="required">Department*</span></td>
					              <td>   
					                <form:select path="departmentId" id="department-control" cssClass="branchControls">
					                <form:option value="-1" >&lt;Select&gt;</form:option>
					                <c:forEach items="${departmentList}" var="dList">
					                	<form:option value="${dList.id}">${dList.name}</form:option>
					                </c:forEach>
				                  </form:select>
					              </td>
					            </tr>
					            		
								  <tr style="${miniBean.rowVisibility}" id="school-control">
					              <td height="35%" align="left"><span class="required">School*</span></td>
					              <td>   
					                <form:select path="schoolId" id="mda-school-control" cssClass="branch-controls">
					                <form:option value="-1" >&lt;Select&gt;</form:option>
					                <c:forEach items="${schoolList}" var="sList">
					                	<form:option value="${sList.id}">${sList.name}</form:option>
					                </c:forEach>
				                  </form:select>
					              </td>
					            </tr>
					            <c:if test="${not roleBean.pensioner and not roleBean.localGovt}">
					            <tr style="${miniBean.rowVisibility}" id="school-ind-control" >	
					            <td align="left" width="35%">School Transfer*</td>
							     <td nowrap><form:radiobutton path="schoolTransfer" value="1" 
							                title="${roleBean.staffTypeName} transfer is to a School" />
							                Yes
							                <form:radiobutton path="schoolTransfer" value="2" title="${roleBean.staffTypeName} Transfer is to ${roleBean.mdaTitle}"/> No</td>
								</tr>
								</c:if>
					            <tr>
					              <td width="35%" align="left"><span class="required">Transfer Date*</span></td>
					              <td><form:input path="transferDate"/><img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('transferDate'),event);"></td>
					              
					              <td></td>
					            </tr>
					            
					            <c:if test="${miniBean.showOverride}">
							          <td align="left" width="35%"><b>Effect on Pending Payslip*</b></td>
							                <td nowrap><form:radiobutton path="overrideInd" value="1" title="If 'Apply Immediately' option is selected, new MDA/School WILL BE reflected on Pending Paychecks if they exist" />Apply Immediately <form:radiobutton path="overrideInd" value="2" title="If 'Ignore Pending Payslips' option is selected, new MDA/School will not be reflected on Pending Paychecks if they exist"/> Ignore Pending Payslips</td>
										
							          
						          </c:if>
						        </c:if>
							</table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
						<c:choose>
							<c:when test="${saved}">
								<input type="image" name="_cancel" value="cancel" title="Close" class="" src="images/close.png">
							</c:when>
							<c:otherwise>
								<input type="image" name="submit" value="ok" title="Perform Transfer" class="" src="images/ok_h.png">
								<input type="image" name="_cancel" value="cancel" title="Cancel Transfer" class="" src="images/cancel_h.png">
							
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

