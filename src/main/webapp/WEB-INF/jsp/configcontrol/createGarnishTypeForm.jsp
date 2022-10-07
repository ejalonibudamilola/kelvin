<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> New Loan Type Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.4.4.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>
</head>

<body class="main">

	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					<tr>
						<td>
								<div class="title">Create a New Loan Type</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="garnTypeBean">
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="garnTypeBean">
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
						<td class="activeTH">New Loan Type Information</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Loan Type Code*</span></td>
									<td width="25%">
										<form:input path="name" size="8" maxlength="7"/>
									</td>
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Description*</span></td>
									<td width="25%">
										<form:input path="description" size="30" maxlength="30"/>										
									</td>
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required" title="Indicate whether editing of this Loans of this type should be restricted to super admin and admin roles">Restrict Editing*</span></td>
									<td width="25%" nowrap>
									    <form:radiobutton path="editRestriction" value="0" title="Edit of Loans of this Type not restricted to super admin and admin roles only"/>No <form:radiobutton path="editRestriction" value="1" title="Only Super Admin and Administrator Role can edit Loans of this type"/> Yes
										
									</td>
								</tr>
								<tr>
										<td align="right" width="35%" nowrap>
											<span class="required">Bank Name*</span>
										</td>
										<td width="25%">
											<form:select path="bankInstId" onchange="loadBankBranchesByBankId(this);">
												<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<c:forEach items="${bankList}" var="bList">
												<form:option value="${bList.id}">${bList.name}</form:option>
												</c:forEach>
												</form:select>
										</td>
								</tr> 
								<tr>
										<td align="right" width="35%" nowrap>
											<span class="required">Branch Name*</span>
										</td>
										<td width="25%">
											<form:select path="branchInstId" id="bank-branch-control" cssClass="branchControls">
												<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<c:forEach items="${bankBranchList}" var="bbList">
												<form:option value="${bbList.id}">${bbList.name}</form:option>
												</c:forEach>
												</form:select>
										</td>
								</tr> 
								<tr>
												<td align="right" width="35%" nowrap>
													<span class="required">Account Number*</span>
												</td>
												<td width="25%">
													<form:input path="accountNumber" size="20" maxlength="20" />
												</td>
								</tr>
								<tr>
												<td align="right" width="35%" nowrap>
													<span class="required">Confirm Account Number</span>
												</td>
												<td width="25%">
													<form:input path="confirmAccountNumber" size="20" maxlength="20" />
												</td>
								</tr>
					           </table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
						
							<c:choose>
							<c:when test="${saved}">
								<input type="image" name="_cancel" value="cancel" title="Close" src="images/close.png">
							</c:when>
							<c:otherwise>
								<input type="image" name="submit" value="ok" alt="Ok" class="" src="images/ok_h.png">&nbsp;
								<input type="image" name="_cancel" value="cancel" title="Cancel" src="images/cancel_h.png">
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