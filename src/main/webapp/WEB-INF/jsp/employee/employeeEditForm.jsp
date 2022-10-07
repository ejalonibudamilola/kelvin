<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> <c:out value="${roleBean.staffTypeName}"/> Edit Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon"  type="image/png"   href="<c:url value="/images/coatOfArms.png"/>">
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>
</head>


<body class="main">

	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					


					<tr>
						<td colspan="2">
								<div class="title">Edit <c:out value="${namedEntity.name}" /> Info </div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="employee">
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="employee">
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
					<c:choose>
						<c:when test="${employee.terminated}">
						   <tr align="left">
								<td class="activeTH">This <c:out value="${roleBean.staffTypeName}"/> is Terminated and can not be edited!</td>
							</tr>
						</c:when>
						<c:otherwise>
							<tr align="left">
								<td class="activeTH">Basic <c:out value="${roleBean.staffTypeName}"/> Information</td>
							</tr>
						</c:otherwise>
					</c:choose>
					
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
							
							<tr>
									<td align="right" width="25%">
										<span class="required"><c:out value="${roleBean.mdaTitle}"/></span></td>
									<td width="25%" nowrap>
										<form:input path="currentMdaName" disabled="true" size="30"/>
									</td>
									
					         </tr>
                             <c:if test="${employee.schoolEnabled}">
					              <c:if test="${ not roleBean.subeb and employee.schoolStaff}">
						         	<tr>
						             	<td align="right"><span class="required">School</span></td>
					              		     <td align="left"> <c:out value="${employee.schoolInfo.name}"/> </td>
					              <td align="left"></td>
						               	</tr>
						         </c:if>
						         </c:if>
					         <tr>
									<td align="right" width="25%">
										<span class="required">Pay Group</span></td>
									<td width="25%">
										<form:input path="salaryInfo.salaryScaleLevelAndStepStr" disabled="true" size="30"/>

									</td>
									
					         </tr>

					         <c:if test="${not roleBean.pensioner}">
					         <tr>
									<td align="right" width="25%">
										<span class="required">Basic Monthly Salary</span></td>
									<td width="25%">
									    <form:input path="salaryInfo.monthlyBasicSalaryStr" disabled="true"/>
										</td>
									
					         </tr>
					         <tr>
									<td align="right" width="25%">
										<span class="required">Monthly Gross</span></td>
									<td width="25%">
										 <form:input path="salaryInfo.totalMonthlySalaryStr" disabled="true"/>
									</td>
									
					         </tr>
					         <tr>
									<td align="right" width="25%">
										<span class="required">Annual Gross</span></td>
									<td width="25%">
										 <form:input path="salaryInfo.totalGrossPayStr" disabled="true"/></td>
									
					         </tr>
					         </c:if>

					         <c:if test="${roleBean.pensioner}">
					          <tr>
                                 <td align="right" width="25%">
                                   <span class="required">Yearly Pension Amount</span></td>
                                   <td width="25%">
                                  <form:input path="employee.hiringInfo.yearlyPensionAmountStr" disabled="true"/>
                               </tr>
                               <tr>
                               <td align="right" width="25%"> <span class="required">Monthly Pension Amount</span></td>
                                   <td width="25%">  <form:input path="employee.hiringInfo.monthlyPensionAmountStr" disabled="true"/>
                                   </td>
                                 </tr>

                                <tr>
                                	 <td align="right" width="25%"> <span class="required">Gratuity Amount</span></td>
                                	  <td width="25%"><form:input path="employee.hiringInfo.gratuityAmountStr" disabled="true" /></td>
                                 </tr>
					         </c:if>
								<tr>
					              <td align="right"><span class="required">Title*</span></td>
					              <td align="left">   
					                <form:select path="titleId" disabled="${employee.canEdit}">
					                <form:option value="-1" >&lt;Select Title&gt;</form:option>
					                <c:forEach items="${titleList}" var="tList">
					                	<form:option value="${tList.id}" >${tList.name}</form:option>
					                </c:forEach>
					                </form:select>
					              </td>
					              <td align="left"></td>
					            </tr>
                            <c:if test="${roleBean.localGovt}">
					          <tr>
                             	<td align=right><span class="required">File No.*</span></td>
                             		<td width="25%">
                             			<form:input path="fileNo" size="10" maxlength="10" disabled="${employee.canEdit}"/>
                             				</td>
                             </tr>
                             </c:if>
								<tr>
									<td align="right" width="25%">
										<span class="required">First Name*</span></td>
									<td width="25%">
										<form:input path="firstName" disabled="${employee.canEdit}"/>
									</td>
									
					            </tr>
					            <tr>
									<td align="right" width="25%"><span class="optional">Middle Name</span></td>
									<td width="25%">
										<form:input path="initials" size="25" maxlength="25" disabled="${employee.canEdit}"/>
									</td>
					            </tr>
					            <tr>
									<td align="right" width="25%"><span class="required">Last Name*</span></td>
									<td width="25%">
										<form:input path="lastName" disabled="${employee.canEdit}"/>
									</td>
									
					            </tr>
								<tr>
									<td align="right"><span class="required"><c:out value="${roleBean.staffTitle}"/>*</span></td>
									<td width="25%">
										<form:input path="employeeId" disabled="${employee.canEdit}"/>
									</td>
								
					            </tr>
                                <tr>
                             	 <td align="right" width="25%">
                             	  <span class="required">Residence ID</span></td>
                             	 <td width="25%">
                              <form:input path="residenceId" maxlength = "9" disabled="${openResidenceId}"/>
                              </td>

                              </tr>
					            <tr>
									<td align="right" width="25%">
										<span class="required">Staff Rank*</span></td>
									<td width="25%">
										<form:input path="rank.name" size="32" disabled="true"/>
									</td>
									
					            </tr>
					            <tr>
					              <td align="right"><span class="required"><c:out value="${roleBean.staffTypeName}"/> Type*</span></td>
					              <td align="left">   
					                <form:select path="employeeType.id" disabled="${employee.canEdit}">
					                <form:option value="-1" >&lt;Select Type&gt;</form:option>
					                <c:forEach items="${emptypes}" var="employeeType">
					                	<form:option value="${employeeType.id}" title="${employeeType.description}" >${employeeType.name}</form:option>
					                </c:forEach>
					                </form:select>
					              </td>
					              <td align="left"></td>
					            </tr>
					            <tr>
					              <td align="right"><span class="required">Address*</span></td>
					              <td width="25%"><form:input path="address1" size="40" maxlength="50" disabled="${employee.canEdit}"/></td>

					            </tr>


						         <tr>
                                   <td align=right width="25%"> <span class="required"> Residence City</span></td>
                                     <td width="25%"> <form:select path="cityId" onchange="loadStateByCity(this)" disabled="${employee.canEdit}">
                                       <form:option value="-1" >&lt;Select City&gt;</form:option>
                                          <c:forEach items="${cities}" var="cityList">
                                            <form:option value="${cityList.id}" title="${cityList.name}" >${cityList.name}</form:option>
                                           </c:forEach>
                                          </form:select>
                                      </td>

                                  </tr>
                                 <tr>
					              <td align="right"><span class="required">Residence State*</span></td>
					              <td>
					                <form:select path="stateInstId" id="state-control" cssClass="stateControls" disabled="${employee.canEdit}">
					                <form:option value="0">&lt;Select a state&gt;</form:option>
					                <c:forEach items="${states}" var="stateInfo">
					                	<form:option value="${stateInfo.id}" >${stateInfo.name}</form:option>
					                </c:forEach>
					                </form:select>
					              </td>
					              <td align="left"></td>
					            </tr>
						         <tr>
					              <td align="right"><span class="required">State Of Origin*</span></td>
					              <td>   
					                <form:select path="stateOfOriginId" onchange='loadLGAByStateId(this)' disabled="${employee.canEdit}">
					                <form:option value="0">&lt;Select a state&gt;</form:option>
					                <c:forEach items="${statesOfOriginList}" var="sorList">
					                	<form:option value="${sorList.id}" >${sorList.name}</form:option>
					                </c:forEach>
					                </form:select>
					              </td>
					              <td align="left"></td>
					            </tr>
					            <tr>
					              <td align="right"><span class="required">LGA Of Origin*</span></td>
					              <td align="left">   
					                <form:select path="lgaId" id="lga-control" cssClass="branchControls" disabled="${employee.canEdit}">
					                <form:option value="-1" >&lt;Select Type&gt;</form:option>
					                <c:forEach items="${LGAList}" var="lgaList">
					                	<form:option value="${lgaList.id}" >${lgaList.name}</form:option>
					                </c:forEach>
					                </form:select>
					              </td>
					              <td align="left"></td>
					            </tr>
					            <tr>
					              <td align="right"><span class="required">Religion*</span></td>
					              <td align="left">   
					                <form:select path="relId" disabled="${employee.canEdit}">
					                <form:option value="-1" >&lt;Select Type&gt;</form:option>
					                <c:forEach items="${religionList}" var="relList">
					                	<form:option value="${relList.id}">${relList.name}</form:option>
					                </c:forEach>
					                </form:select>
					              </td>
					              <td align="left"></td>
					            </tr>
					            
					            <tr>
					              <td align="right" width="25%"><span class="optional">GSM Number</span></td>
					               <td width="25%"><form:input path="gsmNumber" size="15" maxlength="13" disabled="${employee.canEdit}"/>
					               <span class="note"> (0##########)</span>
					               </td>
					              </tr>
					            <tr>
					              <td align="right" width="25%"><span class="optional">Email Address</span></td>
					               <td width="25%"><form:input path="email" size="40" maxlength="40" disabled="${employee.canEdit}"/></td>
					              
					             </tr>
							</table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
						
							         <c:choose>
                                    	 <c:when test="${saved or employee.canEdit}">
                                    	 <input type="image" name="_cancel" value="cancel" title="Close" class="" src="images/close.png">
                                    		 <c:if test="${employee.showContractLink}">
                                    			  <input type="image" name="_createContract" value="createContract" title="Create Contract" class="" src="images/create_contract.png">
                                              </c:if>
                                    	 </c:when>
                                    	 <c:otherwise>
                                    	 <input type="image" name="submit" value="ok" alt="Ok" class="" src="images/ok_h.png">
                                    	 <input type="image" name="_cancel" value="cancel" title="Close" class="" src="images/cancel_h.png">
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

