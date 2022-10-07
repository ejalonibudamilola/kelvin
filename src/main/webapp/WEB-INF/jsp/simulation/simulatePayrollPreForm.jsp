<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title>Simulate Payroll Preparation Form  </TITLE>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
</head>

<body class="main">
    <form:form modelAttribute="simulationBean">			
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					
			<tr>
				<td colspan="2">	
					<div class="title"> Customize Payroll Simulation - Step 1 of 3</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">
				
     				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
						<spring:hasBindErrors name="simulationBean">
         					<ul>
            					<c:forEach var="errMsgObj" items="${errors.allErrors}">
               						<li>
                  						<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               						</li>
            						</c:forEach>
         					</ul>
     					</spring:hasBindErrors>
					</div>
					
				<br/>
					<br/>
					     Simulate Payroll - Step 1. 
					<br/>
					<br/>
     				
     				<table border="0" cellspacing="0" cellpadding="0" width="90%"><tr><td>
					<table class="formtable" border="0" cellspacing="1" cellpadding="3" width="100%" align="left" >
						<tr align="left">
							<td class="activeTH">Payroll Simulator Prep</td>
						</tr>
						<tr>
							<td class="activeTD">
								<table width="95%" border="0" cellspacing="2" cellpadding="0">
									<tr>
										<td width="25%" align="left">Payroll Simulation Name<font color="red"><b>*</b></font></td>
										<td><form:input path="name" size="20" maxlength="50" title="Enter a unique name for this Payroll Simulation"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Simulaton Start Month :</td>
										<td width="25%" align="left" colspan="2">
										 <form:select path="startMonthInd" title="Select the month to start simulation (inclusive)">
												<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												  <c:forEach items="${simulationBean.baseMonthList}" var="mList">
													<form:option value="${mList.id}">${mList.name}</form:option>
												</c:forEach>	
										</form:select>
										</td>
									</tr>
									<tr>
										<td width="25%" align="left" valign="bottom" nowrap>Select No. of Months to Simulate for</td>
										<td width="1%" align="left" >
										<form:select path="noOfMonthsInd" title="Select the number of months to simulate for">
												<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<!--<form:option value="0">1 Month</form:option>
												--><form:option value="1">2 Months</form:option>
												<form:option value="2">3 Months</form:option>
												<form:option value="3">4 Months</form:option>
												<form:option value="4">5 Months</form:option>
												<form:option value="5">6 Months</form:option>
												<form:option value="6">7 Months</form:option>
												<form:option value="7">8 Months</form:option>
												<form:option value="8">9 Months</form:option>
												<form:option value="9">10 Months</form:option>
												<form:option value="10">11 Months</form:option>
												<form:option value="11">12 Months</form:option>
										</form:select>
										</td>
										<td width="19%" align="left">
										&nbsp;&nbsp;
										</td>
										<td width="25%" align="left">
											&nbsp;											
										</td>
									</tr>
									 <tr>
                  						<td><spring:bind path="includePromotionInd">
                 						    <input type="hidden" name="_<c:out value="${status.expression}"/>">
                 							<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if> title="Check this box if you want Promotions to be taken into consideration during simulation"/>
     										</spring:bind><font color="red"><b>Include Promotions</b></font>
     			  						 </td>
                  			
                  						<td nowrap>&nbsp; </td>
                  						<td><div class="note"></div></td>
               						 </tr>
									</table>
									</td>
									</tr>
									</table>
									</td>
									</tr>
									</table>
									</td>
									</tr>
									<tr>
											<td class="buttonRow" align="right" >
											 <c:choose>
				 								<c:when test="${simulationBean.warningIssued}">
				 									<input type="image" name="_delete" value="delete" title="Delete and Go to home page" class="" src="images/delete_h.png">
													<input type="image" name="_save" value="save" title="Save an Go to home page" class="" src="images/Save_h.png"><br>
				 	
				 								</c:when>
				 							<c:otherwise>
				 								<input type="image" name="_next" value="next" title="Next" class="" src="images/next_h.png">
												<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png"><br>
				 	
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
