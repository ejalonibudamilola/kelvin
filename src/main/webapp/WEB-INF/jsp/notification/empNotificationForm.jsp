<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> <c:out value="${roleBean.staffTypeName}"/> Notification Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon"  type="image/png"   href="<c:url value="/images/coatOfArms.png"/>">
</head>


<body class="main">

	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					


					<tr>
						<td colspan="2">
								<div class="title"><c:out value="${miniBean.abstractEmployeeEntity.displayName}" /> Basic Info </div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="miniBean">
				 
				<table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
					 
							<tr align="left">
								<td class="activeTH">Basic <c:out value="${roleBean.staffTypeName}"/> Information</td>
							</tr>
					 
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
							
							<tr>
									<td align="right" width="25%">
										<span class="required"><c:out value="${roleBean.mdaTitle}"/></span></td>
									<td width="25%" nowrap>
										<form:input path="abstractEmployeeEntity.currentMdaName" disabled="true" size="30"/>
									</td>
									
					         </tr>
                             <c:if test="${abstractEmployeeEntity.schoolEnabled}">
					              <c:if test="${ not roleBean.subeb and abstractEmployeeEntity.schoolStaff}">
						         	<tr>
						             	<td align="right"><span class="required">School</span></td>
					              		     <td align="left"> <c:out value="${abstractEmployeeEntity.schoolInfo.name}"/> </td>
					              <td align="left"></td>
						               	</tr>
						         </c:if>
						         </c:if>
					         <tr>
									<td align="right" width="25%">
										<span class="required">Pay Group</span></td>
									<td width="25%">
										<form:input path="abstractEmployeeEntity.salaryInfo.salaryScaleLevelAndStepStr" disabled="true" size="30"/>

									</td>
									
					         </tr>

					         <c:if test="${not roleBean.pensioner}">
					         <tr>
									<td align="right" width="25%">
										<span class="required">Basic Monthly Salary</span></td>
									<td width="25%">
									    <form:input path="abstractEmployeeEntity.salaryInfo.monthlyBasicSalaryStr" disabled="true"/>
										</td>
									
					         </tr>
					         <tr>
									<td align="right" width="25%">
										<span class="required">Monthly Gross</span></td>
									<td width="25%">
										 <form:input path="abstractEmployeeEntity.salaryInfo.totalMonthlySalaryStr" disabled="true"/>
									</td>
									
					         </tr>
					         <tr>
									<td align="right" width="25%">
										<span class="required">Annual Gross</span></td>
									<td width="25%">
										 <form:input path="abstractEmployeeEntity.salaryInfo.totalGrossPayStr" disabled="true"/></td>
									
					         </tr>
					         </c:if>

					         <c:if test="${roleBean.pensioner}">
					          <tr>
                                 <td align="right" width="25%">
                                   <span class="required">Yearly Pension Amount</span></td>
                                   <td width="25%">
                                  <form:input path="yearlyPensionStr" disabled="true"/>
                               </tr>
                               <tr>
                               <td align="right" width="25%"> <span class="required">Monthly Pension Amount</span></td>
                                   <td width="25%">  <form:input path="monthlyPensionStr" disabled="true"/>
                                   </td>
                                 </tr>

                                <tr>
                                	 <td align="right" width="25%"> <span class="required">Gratuity Amount</span></td>
                                	  <td width="25%"><form:input path="gratuityStr" disabled="true" /></td>
                                 </tr>
					         </c:if>
                                <tr>
                                	 <td align="right" width="25%"> <span class="required">Title</span></td>
                                	  <td width="25%"><form:input path="abstractEmployeeEntity.title.name" disabled="true" /></td>
                                 </tr>					         
                            <c:if test="${roleBean.localGovt}">
					          <tr>
                             	<td align=right><span class="required">File No.*</span></td>
                             		<td width="25%">
                             			<form:input path="abstractEmployeeEntity.fileNo" size="10" maxlength="10" disabled="true"/>
                             				</td>
                             </tr>
                             </c:if>
								<tr>
									<td align="right" width="25%">
										<span class="required">First Name*</span></td>
									<td width="25%">
										<form:input path="abstractEmployeeEntity.firstName" disabled="true"/>
									</td>
									
					            </tr>
					            <tr>
									<td align="right" width="25%"><span class="optional">Middle Name</span></td>
									<td width="25%">
										<form:input path="abstractEmployeeEntity.initials" size="25" maxlength="25" disabled="true"/>
									</td>
					            </tr>
					            <tr>
									<td align="right" width="25%"><span class="required">Last Name*</span></td>
									<td width="25%">
										<form:input path="abstractEmployeeEntity.lastName" disabled="true"/>
									</td>
									
					            </tr>
								<tr>
									<td align="right"><span class="required"><c:out value="${roleBean.staffTitle}"/>*</span></td>
									<td width="25%">
										<form:input path="abstractEmployeeEntity.employeeId" disabled="true"/>
									</td>
								
					            </tr>
                                <tr>
                             	 <td align="right" width="25%">
                             	  <span class="required">Residence ID</span></td>
                             	 <td width="25%">
                              <form:input path="abstractEmployeeEntity.residenceId" maxlength = "9" disabled="true"/>
                              </td>

                              </tr>
					            <tr>
									<td align="right" width="25%">
										<span class="required">Staff Rank*</span></td>
									<td width="25%">
										<form:input path="abstractEmployeeEntity.rank.name" size="32" disabled="true"/>
									</td>
									
					            </tr>
                                 <tr>
									<td align="right" width="25%">
										<span class="required"><c:out value="${roleBean.staffTypeName}"/> Type</span></td>
									<td width="25%">
										<form:input path="abstractEmployeeEntity.employeeType.name" size="32" disabled="true"/>
									</td>

					            </tr>
					            <tr>
					              <td align="right"><span class="required">Address</span></td>
					              <td width="25%"><form:input path="abstractEmployeeEntity.address1" size="40" disabled="true"/></td>

					            </tr>
                                  <tr>
                                   <td align=right width="25%"> <span class="required"> Residence City</span></td>
                                      <td width="25%"><form:input path="abstractEmployeeEntity.city.name" size="25" disabled="true"/></td>
                                  </tr>
                                 <tr>
					              <td align="right" width="25%"><span class="required">Residence State</span></td>
					              <td width="25%"><form:input path="abstractEmployeeEntity.city.state.name" size="25"  disabled="true"/></td>
					            </tr>
						         <tr>
					              <td align="right"><span class="required">State Of Origin</span></td>
					              <td width="25%"><form:input path="abstractEmployeeEntity.stateOfOrigin.name" size="25"  disabled="true"/></td>
					            </tr>
					            <tr>
					              <td align="right"><span class="required">LGA Of Origin</span></td>
					             <td width="25%"><form:input path="abstractEmployeeEntity.lgaInfo.name" size="25"  disabled="true"/></td>
					            </tr>
					            <tr>
					              <td align="right"><span class="required">Religion</span></td>
					               <td width="25%"><form:input path="abstractEmployeeEntity.religion.name" size="15"  disabled="true"/></td>
					            </tr>
					            
					            <tr>
					              <td align="right" width="25%"><span class="optional">GSM Number</span></td>
					               <td width="25%"><form:input path="abstractEmployeeEntity.gsmNumber" size="15"  disabled="true"/></td>
					              </tr>
					            <tr>
					              <td align="right" width="25%"><span class="optional">Email Address</span></td>
					               <td width="25%"><form:input path="abstractEmployeeEntity.email" size="40" maxlength="40" disabled="true"/></td>
					              
					             </tr>
							</table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
					        <input type="image" name="_cancel" value="cancel" title="Close" class="" src="images/close.png">
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

