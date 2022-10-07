<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title>Create Pay Group Form  </TITLE>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
</head>

<body class="main">
    <form:form modelAttribute="dao">			
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					
			<tr>
				<td colspan="2">	
					<div class="title"> Create Pay Group - Step 1 of 3</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">
				
     				<div id="topOfPageBoxedErrorMessage" style="display:${dao.displayErrors}">
						<spring:hasBindErrors name="dao">
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
					     Create Pay Group - Step 1. 
					<br/>
					<br/>
     				
     				<table border="0" cellspacing="0" cellpadding="0" width="90%"><tr><td>
					<table class="formtable" border="0" cellspacing="1" cellpadding="3" width="100%" align="left" >
						<tr align="left">
							<td class="activeTH">Create Pay Group</td>
						</tr>
						<tr>
							<td class="activeTD">
								<table width="95%" border="0" cellspacing="2" cellpadding="0">
									<tr>
										<td width="25%" align="left">Pay Group Name<font color="red"><b>*</b></font></td>
										<td><form:input path="name" size="22" maxlength="30" title="Enter Pay Group Name"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Pay Group Description<font color="red"><b>*</b></font></td>
										<td><form:input path="description" size="30" maxlength="40" title="Enter Pay Group Description"/></td>
									</tr>
									<tr>
										<td width="25%" align="left" valign="bottom" nowrap>Pension Rule*</td>
										<td width="1%" align="left" >
										<form:select path="pensionRuleCode" title="Select Pension Calculation Rule">
												<form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<form:option value="1" title="7.5% of (Basic + Rent + Transport)">Basic,Rent & Transport</form:option>
												<form:option value="2" title="7.5% of (Basic + Rent + Motor Vehicle)">Basic,Rent & Motor Vehicle</form:option>
												
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
							             <td align="right" width="25%">Consolidated Pay Group?*</td>
							                <td><form:radiobutton path="consolidatedInd" value="1" />Yes <form:radiobutton path="consolidatedInd" value="0"/> No</td>
										 <td>&nbsp;</td>
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
											  
				 								<input type="image" name="_next" value="next" title="Next" class="" src="images/next_h.png">
												<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png"><br>
				 	 
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
