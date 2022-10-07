<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Merge Bank Branch Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
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
								<div class="title">Merge Bank Branches</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="bankBranchBean">
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="bankBranchBean">
         							 <ul>
            							<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            							</c:forEach>
         								</ul>
     							 </spring:hasBindErrors>
				</div>
				<div id="topOfPageBoxedErrorMessage" style="display:${displayPayrollMsgBlock}">
							<spring:hasBindErrors name="bankBranchBean">
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
						<td class="activeTH">Merge Bank Branches </td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
							<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Parent Bank*</span></td>
									<td align="left" width="25%">
										<form:select path="bankId" onchange='loadBankBranchesByBankId(this);loadBankBranchByBankId(this);'>
													<form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
													<c:forEach items="${banksList}" var="banks">
														<form:option value="${banks.id}" title="${banks.sortCode}">${banks.name}</form:option>
													</c:forEach>
														
										</form:select>
									
									</td>
								
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">From Bank Branch *</span></td>
									<td align="left" width="25%">
										<form:select path="fromBranchInstId" id="bank-branch-control" cssClass="branchControls">
												<form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<c:forEach items="${bankBranchList}" var="fromList">
												<form:option value="${fromList.id}">${fromList.name}</form:option>
												</c:forEach>
												</form:select>
									</td>
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">To Bank Branch *</span></td>
									<td align="left" width="25%">
										<form:select path="toBranchInstId" id="bank-branch2-control" cssClass="branchControls">
												<form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<c:forEach items="${bankBranchList}" var="toList">
												<form:option value="${toList.id}">${toList.name}</form:option>
												</c:forEach>
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
							         	    <c:if test="${not confirmation }">
							         		<input type="image" name="_merge" value="merge" title="Merge Bank Branches" class="" src="images/merge_b_h.png">&nbsp;
											</c:if>
											 <c:if test="${confirmation }">
							         		<input type="image" name="_confirm" value="confirm" title="Confirm Bank Branch Merger" class="" src="images/confirm_h.png">&nbsp;
											</c:if>
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