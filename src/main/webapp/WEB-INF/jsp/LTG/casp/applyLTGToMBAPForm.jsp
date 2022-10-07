<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title>Apply Leave Transport Grant  </TITLE>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link href="css/datatables.min.css" rel="stylesheet">

<script type="text/javascript">
<!--
function popup(url,windowname) 
{
 var width  = 1000;
 var height = 800;
 var left   = (screen.width  - width)/2;
 var top    = (screen.height - height)/2;
 var params = 'width='+width+', height='+height;
 params += ', top='+top+', left='+left;
 params += ', directories=no';
 params += ', location=no';
 params += ', menubar=no';
 params += ', resizable=no';
 params += ', scrollbars=yes';
 params += ', status=no';
 params += ', toolbar=no';
 newwin=window.open(url,windowname, params);
 if (window.focus) {newwin.focus()}
 return false;
}

// -->
</script>
<script type="text/javascript">
<!--
function go(which) {
     //alert("got into go fxn");
	  n = which.value;
	  //alert("selected value = "+n);
	  if (n != 0) {
	    var url = "${appContext}/applyLTGToMABP.do?pid="+n;
	  	location.href = url;
	  }else{
		  document.getElementById('hiderow').style.display = ''; 
	}
}
//-->
</script>


</head>

<body class="main">
    <form:form modelAttribute="ltgMiniBean">			
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
			<tr>
				<td colspan="2">	
					<div class="title"> Choose MDA <br> to apply Leave Transport Grant</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">
				
     				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
						<spring:hasBindErrors name="ltgMiniBean">
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
					     Leave Transport Grant (LTG) Allowance 
					<br/>
					<br/>
     				
     				<table border="0" cellspacing="0" cellpadding="0" width="90%"><tr><td>
					<table class="formtable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
						<tr align="left">
							<td class="activeTH">Choose <c:out value="${roleBean.mdaTitle}"/></td>
						</tr>
						<tr>
							<td class="activeTD">
								<table width="95%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td width="25%" align="left">Current Selection :</td>
										<td width="25%" align="left"><c:out value="${ltgMiniBean.name}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Total No Assigned :</td>
										<td width="25%" align="left"><c:out value="${ltgMiniBean.totalAssigned}"/></td>
									</tr>
									
									<tr id="hiderow" style="${ltgMiniBean.showSelectionList}">
										<td width="25%" align="left" valign="bottom" nowrap>Select <c:out value="${roleBean.mdaTitle}"/> To Pay LTG</td>
										<td width="1%" align="left" >
										<form:select path="mdaInfo.id" title="Select ${roleBean.mdaTitle} to pay Leave Transport Grant">
											<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
													<c:forEach items="${ltgMiniBean.unAssignedObjects}" var="dList">
														<form:option value="${dList.id}">${dList.name}</form:option>
											</c:forEach>	
										</form:select>
										</td>
										<td width="19%" align="left">
										&nbsp;&nbsp;<input type="image" name="_add" value="add" title="Add ${ltgMiniBean.name}" class="" src="images/add.png" align="bottom">
										</td>
										<td width="25%" align="left">
											&nbsp;											
										</td>
									</tr>
									<tr id="hiderow" style="${ltgMiniBean.showMonthList}">
										<td width="25%" align="left"><c:out value="${ltgMiniBean.messageString}"/><font color="red"><b>*</b></font></td>
										<td><form:select path="monthId" title="Select a month">
										    <form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option> 
										    <c:forEach items="${ltgMiniBean.monthList}" var="mList">
												<form:option value="${mList.runMonth}">${mList.name}</form:option>
											</c:forEach>	
										</form:select>
										&nbsp;<b><c:out value="${ltgMiniBean.runYear}"/></b>	</td>
										  
									</tr>
									<tr id="hiderow2" style="${ltgMiniBean.showNameRow}">
										<td width="25%" align="left">LTG Instruction Name<font color="red"><b>*</b></font></td>
										<td><form:input path="ltgInstructionName" size="20" maxlength="100" title="Enter a unique name for this LTG Instruction"/></td>
									</tr>
									<tr>
										<td width="25%" align="left" colspan="3">
											&nbsp;											
										</td>
									</tr>
									
									<tr>
										<td width="25%" align="left">&nbsp;</td>
										<td>&nbsp;</td>
									</tr>
									<tr>
										<td>&nbsp;</td>
									</tr>
									
									</table>						
								
							</td>
						</tr>
						
					</table>
					<c:if test="${ltgMiniBean.showCloseButton}">
					<tr>
						<td class="buttonRow" align="right" >
							<input type="image" name="_close" value="close" title="Return to Admin Home Page" class="" src="images/close.png">
						</td>
					</tr>
				 </c:if>
				</td>
			</tr>
			</table>
			
		
		</table>
		<br>

						
	<c:if test="${ltgMiniBean.hasDataForDisplay}">
	<table class="formtable" border="0" cellspacing="0" cellpadding="3" width="90%" align="left" >
						<tr align="left">
							<td class="activeTH">Details...</td>
						</tr>
	</table>
	<table class="activeTD" border="0" cellspacing="0" cellpadding="0" width="90%"><tr><td>
		<table class="report" cellspacing="0" cellpadding="0">
							<tr class="reportOdd header">
							<td class="tableCell" align="center" valign="top">
								S/No.</td>
								<td class="tableCell" align="center" valign="top">
								<c:out value="${roleBean.mdaTitle}"/></td>
								<td class="tableCell" align="center" valign="top">
								No. Of <c:out value="${roleBean.staffTypeName}"/></td>
								<td class="tableCell" align="center" valign="top">
								Total Basic Salary</td>								
								<td class="tableCell" align="center" valign="top">
								Total LTG Cost</td>
								<td class="tableCell" align="center" valign="top">
								Net Increase</td>
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>
								<td class="tableCell" align="center" valign="top">
								&nbsp;</td>								
							</tr>
							<c:forEach items="${ltgMiniBean.assignedMBAPList}" var="wBean" varStatus="gridRow">
							<tr class="${wBean.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top"><c:out value="${wBean.name}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.noOfEmp}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.basicSalaryStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.ltgCostStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.netIncreaseStr}"/></td>
								<td class="tableCell" align="right" valign="top"><a href="${appContext}/applyLTGToMABP.do?eid=${wBean.id}&act=r" >Remove</a></td>
								<!-- <td class="tableCell" align="right" valign="top"><a href="${appContext}/ltgByMDAPEmpDetails.do?eid=${wBean.id}" target="_blank" onclick="popup(this.href, '${wBean.name}');return false;">Details....</a>&nbsp;</td> -->
							    <td class="tableCell" align="right" valign="top"><a href="#" onClick="showLtgModal(${wBean.id}); return false">Details....</a>&nbsp;</td>
							</tr>
							</c:forEach>							
							<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">Totals</td>
								<td class="tableCell" align="right"><c:out value="${ltgMiniBean.totalNoOfEmp}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${ltgMiniBean.totalBasicSalaryStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${ltgMiniBean.totalLtgCostStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${ltgMiniBean.totalNetIncreaseStr}"/></td>
								<td class="tableCell" align="right" valign="top">&nbsp;</td>
								<td class="tableCell" align="right" valign="top">&nbsp;</td>
							</tr>
							
							
						</table>
			
         </table>
        
			<tr>
				<td class="buttonRow" align="right" >
				
					
					  <c:choose>
					  <c:when test="${ltgMiniBean.showConfirmation}">
					  
						 <input type="image" name="_confirm" value="confirm" title="click to Confirm" class="" src="images/confirm_h.png">
						 <input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
					  
					  </c:when>
					  <c:otherwise>
					     
					 	 		<input type="image" name="_simulate" value="simulate" title="Simulate Payroll Run" class="" src="images/Simulate_h.png">
						 		<input type="image" name="_save" value="save" title="Save and apply to actual payroll run" class="" src="images/Save_h.png">
					         	<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
					    
					  </c:otherwise>
					  </c:choose>
					
				</td>
			</tr>
		 </c:if>
		<br/>
		<br/>
		<div id="result1"></div>
		</td>
		</tr>
			<tr>
				<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
			</tr>
		</table>
		
</form:form>
<script src="scripts/datatables.min.js"></script>
<script>
    function showLtgModal(id){
                        var url="${appContext}/ltgByMDAPEmpDetails.do";
                        console.log("eid is "+id);
                        $.ajax({
                           type: "GET",
                           url: url,
                           data: {
                              eid: id
                           },
                           success: function (response) {
                              console.log("success");
                              $('#result1').html(response);
                              $('#employeeToMDAModal').modal('show');
                           },
                           error: function (e) {
                              alert('Error: ' + e);
                           }
                        });
    };
</script>

</body>
</html>
