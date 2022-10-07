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
<script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
<script src="<c:url value="/scripts/utility_script.js"/>"></script>
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
								<div class="title">Edit Next Of Kin information </div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="nokBean">
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
						<td class="activeTH">Next Of Kin Information</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
							<tr>
									<td align=right width="25%">
										<span class="required"><c:out value="${roleBean.staffTypeName}"/> Name</span></td>
									<td width="25%" nowrap>
										<c:out value="${nokBean.parentObject.displayName}"/>
									</td>
									
					         </tr>
					         <tr>
									<td align=right width="25%">
										<span class="required"><c:out value="${roleBean.staffTitle}"/></span></td>
									<td width="25%">
										<c:out value="${nokBean.parentObject.employeeId}"/>
									</td>
									
					         </tr>
					         
							
					             <tr>
					              <td align=right><span class="required">Relationship*</span></td>
					              <td align="left">   
					                <form:select path="relationshipType.id" >
					                <form:option value="-1" >&lt;Select Title&gt;</form:option>
					                <c:forEach items="${relationshipTypes}" var="relTypeList">
					                	<form:option value="${relTypeList.id}" >${relTypeList.name}</form:option>
					                </c:forEach>
					                </form:select>
					              </td>
					              <td align="left"></td>
					            </tr>
					            <tr>
									<td align=right width="25%"><span class="required">Last Name/Surname *</span></td>
									<td width="25%">
										<form:input path="lastName" size="25" maxlength="25" />
									</td>
									
					            </tr>
								<tr>
									<td align=right width="25%">
										<span class="required">First Name*</span></td>
									<td width="25%">
										<form:input path="firstName" size="25" maxlength="25" />
									</td>
									
					            </tr>
					            <tr>
									<td align=right width="25%"><span class="optional">Middle Name</span></td>
									<td width="25%">
										<form:input path="middleName" size="25" maxlength="25" />
									</td>
					            </tr>
					            
					           
								<tr>
									<td align=right><span class="required">Address*</span></td>
									<td width="25%">
										<form:input path="address" size="50" maxlength="120" />
									</td>
								
					            </tr>

					            <tr>
					              <td align=right><span class="required">City*</span></td>
					              <td align="left">   
					                <form:select path="city.id" >
					                <form:option value="-1" >&lt;Select Title&gt;</form:option>
					                <c:forEach items="${cities}" var="cityList">
					                	<form:option value="${cityList.id}" title="${cityList.state.name}">${cityList.name}</form:option>
					                </c:forEach>
					                </form:select>
					              </td>
					              <td align="left"></td>
					            </tr>
					           
					            <tr>
					              <td align=right><span class="required">Primary Phone Number*</span></td>
					              <td width="25%"><form:input path="gsmNumber" size="11" maxlength="11" /></td>
					              
					            </tr>
					            <tr>
					              <td align="right">Alternate Phone Number</td>
					              <td width="25%"><form:input path="gsmNumber2" size="11" maxlength="11" /></td>
					            </tr>
					             <tr>
					              <td align="right">Email Address</td>
					              <td width="25%"><form:input path="emailAddress" size="25" maxlength="35"/></td>
					            </tr>
							</table>							
						</td>
					</tr>
					
				</table>
				
			</td>
			</tr>
			<tr><td>
						   <c:choose>
							         	<c:when test="${saved}">
												<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/close.png">
							         	</c:when>
							         	<c:otherwise>
							         		<input type="image" name="submit" value="ok" alt="Ok" class="" src="images/ok_h.png">
											<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
							         	
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

