<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title>All Special Allowances</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>

</head>

<body class="main">
<form:form modelAttribute="allowanceDetails">
<script type="text/javascript" src="scripts/jacs.js"></script>
<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
	<%@ include file="/WEB-INF/jsp/modal.jsp" %>

	<tr>
		<td>
		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			
			<tr>
				<td colspan="2">
				<div class="title">
					Ogun State <c:out value="${roleBean.businessName}"/><br>
					Special Allowances</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainBody" id="mainBody">
				
				<table>
				<tr>
					<td>
						<form:select path="subLinkSelect">
						<form:option value="paystubsReportForm">Payroll Summary</form:option>
						<form:option value="viewWageSummary">Payroll Executive Summary</form:option>
						<form:option value="allOtherDeductionsReport">Deductions</form:option>
						</form:select> 
					</td>
					<td>&nbsp;</td>
					<td>
					<input type="image" name="_go" value="go" title="Go" class="" src="images/go_h.png" >
					</tr>
				</table>
				
				
				<span class="reportTopPrintLink">
					<a class ="reportLink" href="${appContext}/allOtherSpecialAllowanceDetailsExcel.do?rm=${allowanceDetails.runMonth}&ry=${allowanceDetails.runYear}&pid=${allowanceDetails.id}">
					View in Excel </a>&nbsp;<span class="tabseparator">|</span>&nbsp;<a class="reportLink" href="${appContext}/specialAllowanceWithDetailsExcel.do?rm=${allowanceDetails.runMonth}&ry=${allowanceDetails.runYear}&pid=${allowanceDetails.id}&exp=all">
					View With Details in Excel</a><br /></span>
				<div class="reportDescText">
					This report shows the total for each Special Allowance paid
					during the date range you select. </div>
				<br />
				Click any total to see the details that make up the total. <br />
				<br />
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
                              &nbsp;  Spec. Allow. Type: <form:select path="deductionId">
                               <form:option value="0">All Special Allowances</form:option>
						       <c:forEach items="${allowanceTypes}" var="allowTypes">
						      <form:option value="${allowTypes.id}">${allowTypes.name}</form:option>
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
					<tr>
						<td class="reportFormControlsSpacing"></td>
					</tr>
					<tr>
						<td class="reportFormControlsSpacing"><br/></td>
					</tr>
					<c:if test="${allowanceDetails.noOfEmployees eq 0 }">
					<br>
					<br>
						<font color="red"><b>No Special Allowance Payment found for <c:out value="${allowanceDetails.monthAndYearStr}"/></b></font>
					<br>
					<br>
					</c:if>
					<tr>
						<td>
						<table class="report" cellspacing="0" cellpadding="0">
							<tr class="reportOdd header">
								<td class="tableCell" valign="top">Special Allowance Type</td>
								<td class="tableCell" valign="top">Total Paid</td>
							</tr>
							<c:forEach items="${allowanceDetails.deductionMiniBean}" var="dedMiniBean">
							<tr class="${dedMiniBean.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${dedMiniBean.name}"/></td>
								<td class="tableCell" align="right" valign="top">
								<a href="${appContext}/otherSpecialAllowanceDetailsReport.do?did=${dedMiniBean.deductionId}&rm=${allowanceDetails.runMonth}&ry=${allowanceDetails.runYear}&pid=${allowanceDetails.id}">
								₦<c:out value="${dedMiniBean.amountStr}"/></a></td>
							</tr>
							</c:forEach>
						</table>
						</td>
					</tr>
					<c:if test="${allowanceDetails.noOfEmployees gt 0 }">
					<br/>
					<tr>
						<td class="reportFormControlsSpacing"><br/></td>
						
					</tr>
					<tr align="left">
						<td><b>No. of <c:out value="${roleBean.staffTypeName}"/> : <c:out value="${allowanceDetails.noOfEmployees}"></c:out></b></td>
					</tr>
					<tr align="right">
						<td nowrap><b>Total Amount Paid : <font color="green"><c:out value="${allowanceDetails.totalCurrentDeductionStr}"></c:out></font></b><br>
						 ( &nbsp;<font color="green"><c:out value="${allowanceDetails.amountInWords}"></c:out></font> &nbsp;)</td>
					</tr>
					<tr>
						<td class="reportFormControlsSpacing"></td>
					</tr>
					<br/>
					</c:if>
					<tr>
                         <td class="buttonRow" align="right">
                          
                         <input type="image" name="_cancel" value="cancel" title="Cancel" src="images/close.png">
				
                         </td>
                     </tr>
				</table>
				<div class="reportBottomPrintLink">
					<a class="reportLink" href="${appContext}/allOtherSpecialAllowanceDetailsExcel.do?rm=${allowanceDetails.runMonth}&ry=${allowanceDetails.runYear}&pid=${allowanceDetails.id}">
					View in Excel </a>&nbsp;<span class="tabseparator">|</span>&nbsp;<a class="reportLink" href="${appContext}/specialAllowanceWithDetailsExcel.do?rm=${allowanceDetails.runMonth}&ry=${allowanceDetails.runYear}&pid=${allowanceDetails.id}&exp=all">
					View With Details in Excel</a>&nbsp;<span class="tabseparator">|</span>&nbsp;<a class="reportLink" href="${appContext}/specialAllowanceByMDAExcel.do?rm=${allowanceDetails.runMonth}&ry=${allowanceDetails.runYear}">
					View By <c:out value="${roleBean.mdaTitle}"/>s in Excel</a><br />
					
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
			<tr>
				<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
			</tr>
			</table>
			
			</form:form>
		</body>
		<script>
		     $(".reportLink").click(function(e){
                $('#reportModal').modal({
                  backdrop: 'static',
                  keyboard: false
                });
             });
		</script>

	</html>
