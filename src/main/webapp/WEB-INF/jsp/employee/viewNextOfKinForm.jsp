<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Next Of Kin Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
</head>


<body class="main">
<form:form modelAttribute="nokBean">
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					


					<tr>
						<td colspan="2">
								<div class="title">Next Of Kin information for <br>
								<c:out value="${employee.displayName}"/>&nbsp;<br><c:out value="${roleBean.staffTitle}"/>: &nbsp;[&nbsp;<c:out value="${employee.employeeId}"/>&nbsp;]</div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						<br/><br/>
				
				<table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
					<tr align="left">
						<td class="activeTH">Next Of Kin Information</td>
					</tr>
					<tr>
						<td class="activeTD">
							<fieldset>
								<legend>
									<b>Primary Next Of Kin</b>
								</legend>
							
							<table border="0" cellspacing="0" cellpadding="2">
							
							<tr>
									<td align=right width="25%"><span class="required">Relationship</span></td>
									<td width="25%">
										<form:input path="primaryNextOfKin.relationshipType.name" size="25" maxlength="25" disabled="true"/>
									</td>
									
					            </tr>
					            <tr>
									<td align=right width="25%"><span class="required">Last Name/Surname *</span></td>
									<td width="25%">
										<form:input path="primaryNextOfKin.lastName" size="25" maxlength="25" disabled="true"/>
									</td>
									
					            </tr>
								<tr>
									<td align=right width="25%">
										<span class="required">First Name*</span></td>
									<td width="25%">
										<form:input path="primaryNextOfKin.firstName" size="25" maxlength="25" disabled="true"/>
									</td>
									
					            </tr>
					            <tr>
									<td align=right width="25%"><span class="optional">Middle Name</span></td>
									<td width="25%">
										<form:input path="primaryNextOfKin.middleName" size="25" maxlength="25" disabled="true"/>
									</td>
					            </tr>
					            
					           
								<tr>
									<td align=right><span class="required">Address*</span></td>
									<td width="25%">
										<form:input path="primaryNextOfKin.address" size="50" maxlength="120" disabled="true"/>
									</td>
								
					            </tr>
					            <tr>
									<td align=right width="25%">
										<span class="required">City*</span></td>
									<td width="25%">
										<form:input path="primaryNextOfKin.city.name" size="15" maxlength="25" disabled="true"/>
									</td>
									
					            </tr>
					             <tr>
					              <td align=right><span class="required">State*</span></td>
					              <td width="25%"><form:input path="primaryNextOfKin.city.state.name" size="25" disabled="true"/></td>
					              
					            </tr>
					            
					           
					            <tr>
					              <td align=right><span class="required">Primary Phone Number*</span></td>
					              <td width="25%"><form:input path="primaryNextOfKin.gsmNumber" size="12" maxlength="13" disabled="true"/></td>
					              
					            </tr>
					            <tr>
					              <td align="right">Alternate Phone Number</td>
					              <td width="25%"><form:input path="primaryNextOfKin.gsmNumber2" size="12" maxlength="13" disabled="true"/></td>
					            </tr>
					             <tr>
					              <td align="right">Email Address</td>
					              <td width="25%"><form:input path="primaryNextOfKin.emailAddress" size="25" maxlength="50" disabled="true"/></td>
					            </tr>
					             <c:if test="${not nokBean.canNotEdit}">
					            <tr>
					              <td align="right">&nbsp;</td>
					              <td width="25%"><a href='${appContext}/editNextOfKin.do?nokId=${nokBean.primaryNextOfKin.id}&dest=${nokBean.name}'  title='Click To Edit'>Edit Next Of Kin</a></td>
					            </tr>
					            </c:if>
					            </table>
					            </fieldset>
					            
							
					<c:if test="${nokBean.hasSecondaryNextOfKin}">
					
					
					<tr>
					<tr align="left">
						<td class="activeTH">Secondary Next Of Kin Information</td>
					</tr>
					<tr>
						<td class="activeTD">
							<fieldset>
								<legend>
									<b>Secondary Next Of Kin</b>
								</legend>
							
							<table border="0" cellspacing="0" cellpadding="2">
							
					         
							
					            <tr>
									<td align=right width="25%"><span class="required">Relationship</span></td>
									<td width="25%">
										<form:input path="secondaryNextOfKin.relationshipType.name" size="25" maxlength="25" disabled="true"/>
									</td>
									
					            </tr>
					            <tr>
									<td align=right width="25%"><span class="required">Last Name/Surname *</span></td>
									<td width="25%">
										<form:input path="secondaryNextOfKin.lastName" size="25" maxlength="25" disabled="true"/>
									</td>
									
					            </tr>
								<tr>
									<td align=right width="25%">
										<span class="required">First Name*</span></td>
									<td width="25%">
										<form:input path="secondaryNextOfKin.firstName" size="25" maxlength="25" disabled="true"/>
									</td>
									
					            </tr>
					            <tr>
									<td align=right width="25%"><span class="optional">Middle Name</span></td>
									<td width="25%">
										<form:input path="secondaryNextOfKin.middleName" size="25" maxlength="25" disabled="true"/>
									</td>
					            </tr>
					            
					           
								<tr>
									<td align=right><span class="required">Address*</span></td>
									<td width="25%">
										<form:input path="secondaryNextOfKin.address" size="50" maxlength="120" disabled="true"/>
									</td>
								
					            </tr>
					            <tr>
									<td align=right width="25%">
										<span class="required">City*</span></td>
									<td width="25%">
										<form:input path="secondaryNextOfKin.city.name" size="15" maxlength="25" disabled="true"/>
									</td>
									
					            </tr>
					             <tr>
                                 <td align=right><span class="required">State*</span></td>
                                 <td width="25%"><form:input path="primaryNextOfKin.city.state.name" size="25" disabled="true"/></td>

                                 </tr>
					            <tr>
					              <td align=right><span class="required">Primary Phone Number*</span></td>
					              <td width="25%"><form:input path="secondaryNextOfKin.gsmNumber" size="12" maxlength="13" disabled="true"/></td>
					              
					            </tr>
					            <tr>
					              <td align="right">Alternate Phone Number</td>
					              <td width="25%"><form:input path="secondaryNextOfKin.gsmNumber2" size="12" maxlength="13" disabled="true"/></td>
					            </tr>
					             <tr>
					              <td align="right">Email Address</td>
					              <td width="25%"><form:input path="secondaryNextOfKin.emailAddress" size="25" maxlength="50" disabled="true"/></td>
					            </tr>
					            <c:if test="${not nokBean.canNotEdit}">
					            <tr>
					              <td align="right">&nbsp;</td>
					              <td width="25%"><a href='${appContext}/editNextOfKin.do?nokId=${nokBean.secondaryNextOfKin.id}&dest=${nokBean.name}'  title='Click To Edit'>Edit Next Of Kin</a></td>
					            </tr>
					            </c:if>
					            </table>
					            </fieldset>
					            </td>
					            </tr>
					      </c:if>
					      </td>
					      </tr>
					      </table>
					      </td>
					      </tr>
					      
					    <tr>
						<td class="buttonRow">
						   <c:choose>
						   <c:when test="${not nokBean.canNotEdit}">
						   		<c:if test="${not nokBean.hasSecondaryNextOfKin}">
									<input type="image" name="_add" value="add" alt="Add Next Of Kin" class="" src="images/add.png">&nbsp;
								</c:if>
								<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/close.png">
						   </c:when>
						   <c:otherwise>
						   		<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/close.png">
						   </c:otherwise>
						   </c:choose>
							
							
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

