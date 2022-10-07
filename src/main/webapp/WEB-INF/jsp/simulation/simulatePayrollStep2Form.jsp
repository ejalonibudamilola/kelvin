<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title>Simulate Payroll Step II Form  </TITLE>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">


<script type="text/javascript">
<!--
function doShowHideList(box,idName) {
     //alert("got into go fxn");
	  
	  //alert("selected value = "+n);
	  if (box.checked) {
		  
		  document.getElementById(idName).style.display = ''; 
		  
	  }else{
		  document.getElementById(idName).style.display = 'none'; 
	  }
}
//-->
</script>

</head>

<body class="main">
    <form:form modelAttribute="simulationBean2">			
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					
			<tr>
				<td colspan="2">	
					<div class="title"> Customize Payroll Simulation - Step 2 of 3</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">
				
     				<div id="topOfPageBoxedErrorMessage" style="display:${simulationBean2.displayErrors}">
						<spring:hasBindErrors name="simulationBean2">
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
					     Simulate Payroll - Step 2.
					<br/>
					<br/>
     				
     				<table border="0" cellspacing="0" cellpadding="0" width="90%"><tr><td>
					<table class="formtable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
						<tr align="left">
							<td class="activeTH">Payroll Simulator Prep - Step 2</td>
						</tr>
						<tr>
							<td class="activeTD">
								<table width="95%" border="0" cellspacing="0" cellpadding="1">
									<tr>
										<td width="25%" align="left">Payroll Simulation Name<font color="red"><b>*</b></font></td>
										<td nowrap><c:out value="${simulationBean2.name}"></c:out></td>
									</tr>
									<tr>
										<td width="25%" align="left">Simulaton Start Month &amp; Year :</td>
										<td width="25%" align="left" colspan="2">
										 <c:out value="${simulationBean2.startMonthName}" />
										</td>
									</tr>
									<tr>
										<td width="25%" align="left" valign="bottom" nowrap>Simulate for</td>
										<td width="3%" align="left" >
										<c:out value="${simulationBean2.simulationLength}"/>
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
                 							<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if> disabled="disabled"/>
     										</spring:bind>Include Promotions
     			  						 </td>
                  			
                  						<td nowrap>&nbsp; </td>
                  						<td><div class="note"></div></td>
               						 </tr>
               						 
               						 <c:if test="${simulationBean2.canDeductBaseYearDevLevy}">
               						 	<tr>
                  						<td><spring:bind path="deductBaseYearDevLevy">
                 						    <input type="hidden" name="_<c:out value="${status.expression}"/>">
                 							<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if> onClick="doShowHideList(this,'baseYearList')"/>
     										</spring:bind>Deduct Current Year Development Levy.
     			  						 </td>
                  			
                  						<td nowrap>&nbsp; </td>
                  						<td><div class="note"></div></td>
               						 </tr>
               						 <tr id="baseYearList" style="display:none">
										<td width="25%" align="left">Select Month to deduct Dev. Levy :</td>
										<td width="25%" align="left" colspan="2">
										 <form:select path="baseYearDevLevyInd" title="Select the month to deduct Development Levy">
												<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												  <c:forEach items="${simulationBean2.baseMonthList}" var="mList">
													<form:option value="${mList.id}">${mList.name}</form:option>
												</c:forEach>	
										</form:select>
										</td>
									</tr>
               						 </c:if>
               						 
               						 <c:if test="${simulationBean2.canDeductSpillOverYearDevLevy}">
               						 	<tr>
                  						<td><spring:bind path="deductSpillOverYearDevLevy" >
                 						    <input type="hidden" name="_<c:out value="${status.expression}"/>">
                 							<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if> onClick="doShowHideList(this,'spillYearList')"/>
     										</spring:bind>Deduct Development Levy Next Year
     			  						 </td>
                  			
                  						<td nowrap>&nbsp; </td>
                  						<td><div class="note"></div></td>
               						 </tr>
               						 <tr id="spillYearList" style="display:none">
										<td width="25%" align="left">Select Month to deduct Dev. Levy :</td>
										<td width="25%" align="left" colspan="2">
										 <form:select path="spillYearDevLevyInd" title="Select the month to deduct Development Levy">
												<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												  <c:forEach items="${simulationBean2.spillOverMonthList}" var="sList">
													<form:option value="${sList.id}">${sList.name}</form:option>
												</c:forEach>	
										</form:select>
										</td>
									</tr>
               						 </c:if>
               						 	
               						 <tr>
                  						<td><spring:bind path="applyLtgInd">
                 						    <input type="hidden" name="_<c:out value="${status.expression}"/>">
                 							<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if> title="leave checked (Ticked) to apply Leave Transport Grant"/>
     										</spring:bind>Apply Leave Transport Grant
     			  						 </td>
                  			
                  						<td nowrap>&nbsp; </td>
                  						<td><div class="note"></div></td>
               						 </tr>
               						 <c:if test="${simulationBean2.canDoStepIncrement}">
	               						 <tr>
	                  						<td><spring:bind path="stepIncrementInd">
	                 						    <input type="hidden" name="_<c:out value="${status.expression}"/>">
	                 							<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}" >checked</c:if> title="leave checked (Ticked) to apply Yearly Step Increase"/>
	     										</spring:bind>Apply Yearly Step Increment
	     			  						 </td>
	                  			
	                  						<td nowrap>&nbsp; </td>
	                  						<td><div class="note"></div></td>
	               						 </tr>
               						 	
               						 </c:if>
									</table>
									</td>
									</tr>
									</table>
									
									</td>
									</tr>
									<tr>
										<td class="buttonRow" align="right" >
											<input type="image" name="_next" value="next" title="Next" class="" src="images/next_h.png">
											<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png"><br>
									</td>
										
									</tr>
									</table>
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
