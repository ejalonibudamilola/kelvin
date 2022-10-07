<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Single Special Allowance Detail </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
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
 params += ', resizable=yes';
 params += ', scrollbars=yes';
 params += ', status=no';
 params += ', toolbar=no';
 newwin=window.open(url,windowname, params);
 if (window.focus) {newwin.focus()}
 return false;
}
// -->
</script>
</head>

<body class="main">
<script type="text/javascript" src="scripts/jacs.js"></script>
<form:form modelAttribute="allowanceDetails">
	<table class="main" width="70%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
	<tr>
		<td colspan="2">
		<div class="title">
			<c:out value="${namedEntity.name}"/><br>
			Single Special Allowance</div>
		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">
		
			<!--<table>
				<tr>
					<td>
						<form:select path="subLinkSelect">
						<form:option value="paystubsReportForm">Payroll Summary</form:option>
						<form:option value="employeeDetailsReport">Employee Details</form:option>
						<form:option value="preLastPaycheckForm">Last Paycheck</form:option>
						</form:select> 
					</td>
					<td>&nbsp;</td>
					<td>
					<input type="image" name="_go" value="go" title="Go" class="" src="images/go_h.png" >
				</tr>
			</table>
		
		--><span class="reportTopPrintLink">
		<a href="${appContext}/specialAllowanceDetailsExcel.do?did=${allowanceDetails.deductionId}&rm=${allowanceDetails.runMonth}&ry=${allowanceDetails.runYear}&pid=${allowanceDetails.id}">
		View in Excel </a><br /></span>
		
		<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
			<tr>
				<td class="reportFormControls">
				  <span class="optional"> Month </span>
				 <form:select path="runMonth">
                               <form:option value="-1">Select Month</form:option>
						       <c:forEach items="${monthList}" var="mList">
						         <form:option value="${mList.id}">${mList.name}</form:option>
						      </c:forEach>
						      </form:select>
                              &nbsp;  <span class="optional"> Year </span> 
                 <form:select path="runYear">
                               <form:option value="0">Select Year</form:option>
						       <c:forEach items="${yearList}" var="yList">
						         <form:option value="${yList.id}">${yList.name}</form:option>
						      </c:forEach>
						      </form:select>
						      &nbsp;
						      
					Special Allowance: <form:select path="deductionId">
                               <form:option value="0">All Special Allowances</form:option>
						       <c:forEach items="${allowanceTypeList}" var="specAllType">
						      <form:option value="${specAllType.id}">${specAllType.name}</form:option>
						      </c:forEach>
						      </form:select>
                              &nbsp; 
                 </td>
			</tr>
			<tr>
                         <td class="buttonRow" align="right">
                             <input type="image" name="_updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                            <br/>
                         </td>
                     </tr>
			<!--<tr>
 	           <td class = "reportFormControls">           
                 <span class="optional"> Search </span><form:input path="empId" /> <input type = "image" name="_go" value="go" title= "Go" src="images/go_h.png" width="16" height="16" >
               </td>                                   	
            </tr>
			
			--><tr>
				<td class="reportFormControlsSpacing"><br><br></td>
			</tr>
			<tr>
				<td>
				<h3><c:out value="${allowanceDetails.currentDeduction}"/></h3>
				<br />
				<table class="report" cellspacing="0" cellpadding="0">
						<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="100px">Staff ID</td>
						<td class="tableCell" valign="top" width="250px">Employee Name</td>
						<td class="tableCell" width="250px" valign="top" align="center">Amount Paid</td>
						<!--<td class="tableCell" width="250px" valign="top" align="center">Loan Balance</td>						
					--></tr>
				</table>
				<div style="overflow:scroll;height:200px;width:100%;overflow:auto">
				<table class="report" cellspacing="0" cellpadding="0">
					<c:forEach items="${allowanceDetails.deductionMiniBean}" var="dedMiniBean">
					<tr class="${dedMiniBean.displayStyle}">
						<td class="tableCell" valign="top" width="100px"><a href='${appContext}/viewSpecialAllowanceHistory.do?eid=${dedMiniBean.id}&said=${dedMiniBean.deductionId}' target="_blank" onclick="popupWindow(this.href,'${dedMiniBean.name}'); return false;"><c:out value="${dedMiniBean.employeeId}"/></a></td>
						<td class="tableCell" valign="top" width="256px"><c:out value="${dedMiniBean.name}"/></td>
						<td class="tableCell" width="250px" align="center" valign="top"><c:out value="${dedMiniBean.amountStr}"/></td>
						</tr>
					</c:forEach>					
					
				</table>
				</div>
				<table class="report" cellspacing="0" cellpadding="0">
						<tr class="reportEven footer">
						<td class="tableCell" valign="top" width="100px">&nbsp;Total</td>
						<td class="tableCell" valign="top" width="250px"><c:out value="${allowanceDetails.pageSize}"/></td>
						<td class="tableCell" valign="top" align="center" width="250px">₦<c:out value="${allowanceDetails.totalStr}"/></td>
						</tr>
				</table>
				
				</td>
			</tr>
 			<tr>
                 <td class="buttonRow" align="right">
                  <input type="image" name="_cancel" value="cancel" title="Cancel" src="images/close.png">
                 
                 </td>
            </tr>
		</table>
		<div class="reportBottomPrintLink">
			<a href="${appContext}/specialAllowanceDetailsExcel.do?did=${allowanceDetails.deductionId}&rm=${allowanceDetails.runMonth}&ry=${allowanceDetails.runYear}&pid=${allowanceDetails.id}">
			View in Excel </a><span class="tabseparator"> <!-- |</span>
			<a href="${appContext}/singleSpecialAllowanceByMDAExcel.do?satid=${allowanceDetails.deductionId}&rm=${allowanceDetails.runMonth}&ry=${allowanceDetails.runYear}">
			View by MDA in Excel </a><br />		-->
		</div>
		<br>
		
		</td>
		<td valign="top" class="navSide"><br>
		<br>
		</td>
	</tr>
	</table>
	</td>
	</tr>
	<tr><%@ include file="/WEB-INF/jsp/footerFile.jsp" %></tr>
	</table>
	
	</form:form>
</body>

</html>
