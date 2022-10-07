<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Search <c:out value="${roleBean.staffTypeName}"/> Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
 <link rel="stylesheet" href="styles/jquery-ui-1.8.18.custom.css" type="text/css" media ="screen">
</head>


<body class="main">

	<table class="main" width="70%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					

					<tr>
						<td colspan="2">
								<div class="title"> <c:out value="${empMiniBean.displayTitle}"/> </div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="empMiniBean">
						<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="empMiniBean">
         							 <ul>
            							<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            							</c:forEach>
         								</ul>
     							 </spring:hasBindErrors>
						</div>
				<table border="0" cellspacing="0" cellpadding="3" width="95%" align="left" >
					<tr align="left">
						<td class="activeTH">Search Criteria</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2" width="100%" align="left">
								<tr>
									<td align=right width="10%"><span class="required">Employee ID*</span></td>
									<td align="left" width="90%">
									<form:input path="employeeId" id="empId" size="35"/>
										
										<!-- span to display the name of the employee with the selected id -->
										<span id="staff_name_display_span" style="padding-left: 30px; font-size: 11px; color: blue;">
										</span>
										<!-- container for the undisplayed employee names -->			
										<div id="employee_names_div" style="display: none;"></div>
									</td>
									
					            </tr>
                                <c:if test="${not (roleBean.civilService or roleBean.subeb)}">
                                                 <tr>
                                                     <td align=right width="10%" ><span class="required">Legacy ID&nbsp;</span></td>
                                                     <td align="left" width="90%">
                                                             <form:input path="legacyEmployeeId" id="legEmpId" size="35"/>

                                                             <!-- span to display the name of the employee with the selected id -->
                                                             <span id="staff_name_legacy_display_span" style="padding-left: 30px; font-size: 11px; color: blue;">
                                                             </span>
                                                             <!-- container for the undisplayed employee names -->
                                                             <div id="employee_leg_names_div" style="display: none;"></div>
                                                     </td>
                                                 </tr>

                                  </c:if>
								<tr>
									<td align="right">
										<span class="required">First Name</span></td>
									<td align="left">
										<form:input path="firstName" id="fName" size="25" maxlength="50"/>
										<!-- span to display the name of the employee with the selected id -->
										<span id="staff_name_display_span" style="padding-left: 30px; font-size: 11px; color: blue;">
										</span>
										
									</td>
					            </tr>
					            <tr>
									<td align="right"><span class="required">Surname</span></td>
									<td align="left">
										<form:input path="lastName" id="lName" size="25" maxlength="50"/>
										<!-- span to display the name of the employee with the selected id -->
										<span id="staff_name_display_span" style="padding-left: 30px; font-size: 11px; color: blue;">
										</span>
										
									</td>
					            </tr>
								
								
					           
							</table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
							<input type="image" name="submit" value="ok" title="Search" class="" src="images/search.png">
							<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
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
	
	<script type="text/javascript" src='<c:url value ="/scripts/employeeIdAutocomplete.js"/>'></script>
<script type="text/javascript" src='<c:url value ="/scripts/searchAutoComplete.js"/>'></script>
	
	
</body>
</html>

