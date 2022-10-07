<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Add Bank Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
<script src="<c:url value="/scripts/utility_script.js"/>"></script>
</head>

<body class="main">

	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					
					<tr>
						<td>
								<div class="title">Add a New (Online) Bank</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="bankBean">
				<div id="topOfPageBoxedErrorMessage" style="display:${bankBean.displayErrors}">
								 <spring:hasBindErrors name="bankBean">
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
						<td class="activeTH">New Bank Information</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Bank Name*</span></td>
									<td width="25%">
										<form:input path="name" size="50" maxlength="20"/>
									</td>
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Sort Code*</span></td>
									<td width="25%">
										<form:input path="sortCode" size="6" maxlength="5"/>
									</td>
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Description*</span></td>
									<td width="25%">
										<form:input path="description" size="50" maxlength="50"/>										
									</td>
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
									
										<span class="required">Type*</span></td>
									<td width="25%">
                                            <form:select path="mfbInd">
														<form:option value="0">&lt;Select&gt;</form:option>
														<form:option value="1" title="Deposit Money Banks i.e.,Union Bank,First Bank, GT Bank et al">Deposit Money Bank</form:option>
														<form:option value="2" title="Micro-Finance Banks">Micro Finance Bank</form:option>
												</form:select>       
                                        </td>
								</tr>
					           </table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
						  <c:choose>
							         	<c:when test="${saved}">
												<input type="image" name="_cancel" value="cancel" title="Close" class="" src="images/close.png">
							         	</c:when>
							         	<c:otherwise>
							         		<input type="image" name="submit" value="ok" title="Ok" class="" src="images/ok_h.png">&nbsp;
											<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
							         	
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