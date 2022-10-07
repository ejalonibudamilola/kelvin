<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Check Register  </TITLE>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">

</head>
<body class="main">
	<table class="main" width="70%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>

			<table  width="100%" align="center" border="0" cellpadding="0" cellspacing="0" height="550px">
			
	<tr>
		<td colspan="2">
		<div class="title">
			<c:if test="${checkBean.payGroup}">
				Check Register By Pay Group
			</c:if>
			<c:if test="${not checkBean.payGroup}">
				Check Register for <c:out value="${namedEntity.name}"/>
			</c:if>
			</div>
		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">
		<form:form modelAttribute="checkBean" id="myForm">
			<table width="650" cellspacing="0" cellpadding="0">
				<tr>
					<td>
							<div id="topOfPageBoxedErrorMessage" style="display:${checkBean.displayErrors}">
								 <spring:hasBindErrors name="checkBean">
         							 <ul>
            							<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            							</c:forEach>
         								</ul>
     							 </spring:hasBindErrors>
							</div>
					<div class="registerDatePicker">
						<table>
							<tr>
								<td nowrap>
								Payroll Run Month&nbsp;<form:select path="runMonth">
												<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<c:forEach items="${monthList}" var="mList">
												<form:option value="${mList.id}">${mList.name}</form:option>
												</c:forEach>
												</form:select>
								</td>
								<td nowrap>
								Payroll Run Year&nbsp;<form:select path="runYear">
												<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<c:forEach items="${yearList}" var="yList">
												<form:option value="${yList.id}">${yList.id}</form:option>
												</c:forEach>
												</form:select>
								</td>
								
								
							</tr>
						</table>
					</div>
					</td>
				</tr>
			</table>
			
				<table class="register3" cellspacing="1" cellpadding="3">
					<tr class="registerEvenLine header">
						<td class="tableCell" width="101px"><b>Select</b></td>
						<td class="tableCell" width="340px"><b>Name</b></td>
						<td class="tableCell" width="150px"><b>No Of Employees</b></td>
						<td class="tableCell" width="150px"><b>Total Pay</b></td>
						<td class="tableCell" width="150px"><b>Status</b></td>
					</tr>
					</table>
					<div style="overflow:scroll;height:280px;width:100%;overflow:auto">
				<table class="report" cellspacing="0" cellpadding="0">
					<c:forEach items="${checkBean.mdapPaySlips}" var="checks" varStatus="gridRow">
					<tr class="${checks.displayStyle}">
						<td class="tableCell" align="center" width="104px" >
						<spring:bind path="checkBean.mdapPaySlips[${gridRow.index}].printChecks">
  							<input type="hidden" name="_<c:out value="${status.expression}"/>">
                  			<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if>/>
     					</spring:bind>
						</td>
						<td class="tableCell" valign="top" width="335px">
							<c:out value="${checks.name}" />
						</td>
						<td class="tableCell" valign="top" width="150px">
							<c:out value="${checks.noOfEmployees}" />
						</td>
						<td class="tableCell" valign="top" width="149px">
							<c:out value="${checks.totalPayStr}" />
						</td>
						<td class="tableCell" valign="top" width="150px">
							<c:out value="${checks.checkStatus}" />
						</td>
						</tr>
					</c:forEach>
				</table>
				</div>
				
				
				<table width="650px" cellspacing="1" cellpadding="3">
				<tr>
					<td align="left">
					<input type="image" name="_print" value="print" title="Print" class='' src='images/View_Print_n.png'>
					&nbsp; <input type="image" name="_updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
					&nbsp;<input type="image" name="_cancel" value="cancel" title="Cancel operation" src="images/cancel_h.png">
					
								
					</td>
				</tr>
				</table>
					
		</form:form>
		<br/>
		<br/>
		</td>
		
		
		<!-- <input type="image" name="_print" value="print" title="Print" class='' src='images/View_Print_n.png' onclick="document.getElementById('myForm').target = '_blank';"> -->
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

