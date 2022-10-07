<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> View and Print Paystubs  </TITLE>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
</head>

<body class="main">
 <script type="text/JavaScript" src="scripts/jacs.js"></script>
	<table class="main" width="70%" border="1"  bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>

			<table  width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
				
	<tr>
		<td colspan="2">
		<div class="title">
			View and Print Paystubs</div>

		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">
		<p>To print all paystubs, click <b>View &amp; Print</b> to launch Adobe Reader 
		in a new window, then click the Print icon on the Reader toolbar. 
		</p>
		
		<style>
		#mainlist th, #mainlist td {
		border : solid 1px #3E7BA7;
		}
		table#mainlist {
		border-collapse : collapse;
		border-top: solid 2px #3E7BA7;
		border-bottom: solid 2px #3E7BA7;
		}
		</style>
		

		<form:form modelAttribute="payDayPrintBean" id="myForm2">
		<table width="650" cellspacing="0" cellpadding="0">
				<tr>
					<td>
							<div id="topOfPageBoxedErrorMessage" style="display:${payDayPrintBean.displayErrors}">
								 <spring:hasBindErrors name="payDayPrintBean">
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
								from&nbsp;<form:input path="startDate" readonly="readonly"/>
								<img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('startDate'),event);">
								</td>
								<td nowrap><span class="optional">&nbsp;to
								</span>&nbsp;								 
								<form:input path="endDate" readonly="readonly"/>
								<img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('endDate'),event);">
								</td>
								<td nowrap><span class="optional">&nbsp;<c:out value="${payDayPrintBean.name}"/>&nbsp;
								</span>&nbsp;<form:select path="objectId">
                               				<form:option value="0">&lt;Select&gt;</form:option>
						      				 <c:forEach items="${payDayPrintBean.objectList}" var="oList">
						     				 <form:option value="${oList.id}">${oList.name}</form:option>
						      				</c:forEach>
						     				 </form:select> 
						     				 </td>
								<td nowrap>&nbsp;&nbsp;&nbsp;
								<input type="image" name="_updateReport" value="updateReport" alt="Update Report" class="" src="images/Update_Report_h.png" onclick=""  />
								</td>
							</tr>
						</table>
					</div>
					</td>
				</tr>
			</table>
			<div style="width: 690px; height: 300px; overflow: auto; padding: 5px">
			<table cellspacing="0" cellpadding="0" border="0" width="500px">
				<tr>
					<td>

					<table id="mainlist" cellspacing="0" cellpadding="3">
						<tr class="reportHeaderRow">
							<th width="5%">Select</th>
							<th width="20%" align="center">Pay Date</th>
							<th width="5%" align="center">Staff ID</th>
							<th width="40" align="center">Staff Name</th>
							<th width="30%" align="center">Net Pay</th>

						</tr>
						<c:forEach items="${payDayPrintBean.employeePayBean}" var="payday" varStatus="gridRow">
						<tr class="${payday.displayStyle}">
							<td align="center">
							<spring:bind path="payDayPrintBean.employeePayBean[${gridRow.index}].payEmployee">
  								<input type="hidden" name="_<c:out value="${status.expression}"/>">
                  				<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if>/>
     						</spring:bind>
							</td>
							<td>
							<a href='${appContext}/paystubForm.do?pid=${payday.id}'>
							<c:out value="${payday.lastPayDate}" /></a>
							</td>
							<td align="center"><a href='${appContext}/employeeOverviewForm.do?eid=${payday.employee.id}'>
							<c:out value="${payday.employee.employeeId}" /></a>
							</td>
							<td><nobr><c:out value="${payday.employee.firstName}" />&nbsp;<c:out value="${payday.employee.lastName}" />&nbsp;&nbsp;
							</nobr></td>
							<td align="right"><c:out value="${payday.netPayStr}" /></td>

						</tr>
						</c:forEach>
					</table>
					<table width="100%" cellspacing="1" cellpadding="3">
						<tr>
							<td align="left">
								<input type="image" name="_print" value="print" alt="Print" class='' src='images/View_Print_n.png' onclick="document.getElementById('myForm2').target = '_blank';">
							</td>
						</tr>

					</table>
					</td>
				</tr>
			</table>
			</div>
			
		</form:form>
		<a href="${appContext}/selectPayslipMode.do">View more checks</a><br>
		</td>
		</tr>
		</table>
		</td>
		</tr>
	
	</table>

</body>

</html>

