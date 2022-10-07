<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title>Payroll Statistics Details Form </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>

</head>

<body class="main">
<form:form modelAttribute="statBean">
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					<tr>
							<td>
								<div class="navTopBannerAndSignOffLink">
									<span class="navTopSignOff">
										&nbsp;&nbsp;&nbsp;<a href="javascript: self.close ()" title='Close Window' id="topNavSearchButton1">Close Window</a>
									</span>
								</div>
							</td>
						</tr>
                       
	<tr>
		<td colspan="2">
		<div class="title">
			Payroll Statistic : <br>
			<c:out value="${statBean.name}"/>
		</div>
		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">
		
			
		
		<table class="reportMain" cellpadding="0" cellspacing="0" width="90%">
			
			<tr>
				<td class="reportFormControlsSpacing">&nbsp;</td>
			</tr>
			<tr>
				<td>
				<c:if test="${statBean.objectInd eq 1}"> 
				<table class="report" cellspacing="0" cellpadding="0">
					
					<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="10%">S/No.</td>
						<td class="tableCell" valign="top" width="10%"><c:out value="${roleBean.staffTitle}"/></td>
						<td class="tableCell" valign="top" align="left" width="40%"><c:out value="${roleBean.staffTypeName}"/> Name</td>
						<td class="tableCell" valign="top" align="left" width="10%">Level/Step</td>
						<td class="tableCell" valign="top" align="right" width="10%">Total Pay</td>
						<td class="tableCell" valign="top" align="right" width="10%">Total Ded.</td>
						<td class="tableCell" valign="top" align="right" width="10%">Net Pay</td>
					</tr>
					</table>
					<div style="overflow:scroll;height:400px;width:100%;overflow:auto">
						<table class="report" cellspacing="0" cellpadding="0">
							<c:forEach items="${detailsBean}" var="innerBean" varStatus="gridRow">
							<tr class="reportOdd">
								<td class="tableCell" valign="top" width="10%"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.mode}"/></td>
								<td class="tableCell" align="left" valign="top" width="40%" ><c:out value="${innerBean.name}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.salaryInfo.levelStepStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.totalPayStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.totalDeductionsStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.netPayStr}"/></td>
							</tr>
							</c:forEach>
						</table>
						</div>
					
					</c:if>
					<c:if test="${statBean.objectInd eq 2}"> 
					<table class="report" cellspacing="0" cellpadding="0">
					<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="10%">S/No.</td>
						<td class="tableCell" valign="top" width="10%"><c:out value="${roleBean.staffTitle}"/></td>
						<td class="tableCell" valign="top" align="left" width="50%"><c:out value="${roleBean.staffTypeName}"/> Name</td>
						<td class="tableCell" valign="top" align="left" width="10%">Level/Step</td>
						<td class="tableCell" valign="top" align="right" width="10%">Birth Date</td>
						<td class="tableCell" valign="top" align="right" width="10%">Retire Date</td>
					</tr>
					</table>
					<div style="overflow:scroll;height:400px;width:100%;overflow:auto">
						<table class="report" cellspacing="0" cellpadding="0">
							<c:forEach items="${detailsBean}" var="innerBean" varStatus="gridRow">
							<tr class="reportEven">
								<td class="tableCell" valign="top" width="10%"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.mode}"/></td>
								<td class="tableCell" align="left" valign="top" width="50%" ><c:out value="${innerBean.name}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.salaryInfo.levelStepStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.birthDateStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.expectedDateOfRetirementStr}"/></td>
							</tr>
							</c:forEach>
						</table>
						</div>
					
					</c:if>
					<c:if test="${statBean.objectInd eq 3}"> 
					<table class="report" cellspacing="0" cellpadding="0">
						<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="10%">S/No.</td>
						<td class="tableCell" valign="top" width="10%"><c:out value="${roleBean.staffTitle}"/></td>
						<td class="tableCell" valign="top" align="left" width="50%"><c:out value="${roleBean.staffTypeName}"/> Name</td>
						<td class="tableCell" valign="top" align="left" width="10%">Level/Step</td>
						<td class="tableCell" valign="top" align="right" width="10%">Hire Date</td>
						<td class="tableCell" valign="top" align="right" width="10%">Retire Date</td>
					</tr>
					</table>
					<div style="overflow:scroll;height:280px;width:100%;overflow:auto">
						<table class="report" cellspacing="0" cellpadding="0">
							<c:forEach items="${detailsBean}" var="innerBean" varStatus="gridRow">
							<tr class="reportEven">
								<td class="tableCell" valign="top" width="10%"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.mode}"/></td>
								<td class="tableCell" align="left" valign="top" width="50%" ><c:out value="${innerBean.name}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.salaryInfo.levelStepStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.hireDateStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.expectedDateOfRetirementStr}"/></td>
							</tr>
							</c:forEach>
						</table>
						</div>
					
					</c:if>
					<c:if test="${statBean.objectInd eq 4}"> 
					<table class="report" cellspacing="0" cellpadding="0">
					<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="20%">S/No.</td>
						<td class="tableCell" valign="top" width="20%"><c:out value="${roleBean.staffTitle}"/></td>
						<td class="tableCell" valign="top" align="left" width="40%"><c:out value="${roleBean.staffTypeName}"/> Name</td>
						<td class="tableCell" valign="top" align="left" width="20%">Level/Step</td>
						 
					</tr>
					</table>
					<div style="overflow:scroll;height:280px;width:100%;overflow:auto">
						<table class="report" cellspacing="0" cellpadding="0">
							<c:forEach items="${detailsBean}" var="innerBean" varStatus="gridRow">
							<tr class="reportEven">
								<td class="tableCell" valign="top" width="20%"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top" width="20%" ><c:out value="${innerBean.mode}"/></td>
								<td class="tableCell" align="left" valign="top" width="40%" ><c:out value="${innerBean.name}"/></td>
								<td class="tableCell" align="left" valign="top" width="20%" ><c:out value="${innerBean.salaryInfo.levelStepStr}"/></td>
								 
							</tr>
							</c:forEach>
						</table>
						</div>
					
					</c:if>
					<c:if test="${statBean.objectInd eq 5}"> 
					<table class="report" cellspacing="0" cellpadding="0">
					<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="10%">S/No.</td>
						<td class="tableCell" valign="top" width="10%"><c:out value="${roleBean.staffTitle}"/></td>
						<td class="tableCell" valign="top" align="left" width="40%"><c:out value="${roleBean.staffTypeName}"/> Name</td>
						<td class="tableCell" valign="top" align="left" width="10%">Level/Step</td>
						<td class="tableCell" valign="top" align="right" width="10%">Gross Pay</td>
						<td class="tableCell" valign="top" align="right" width="10%">Total Ded.</td>
						<td class="tableCell" valign="top" align="right" width="10%">Net Pay</td>
					</tr>
					</table>
					<div style="overflow:scroll;height:280px;width:100%;overflow:auto">
						<table class="report" cellspacing="0" cellpadding="0">
							<c:forEach items="${detailsBean}" var="innerBean" varStatus="gridRow">
							<tr class="reportEven">
								<td class="tableCell" valign="top" width="10%"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.mode}"/></td>
								<td class="tableCell" align="left" valign="top" width="40%" ><c:out value="${innerBean.name}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.salaryInfo.levelStepStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.totalPayStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.totalDeductionsStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.netPayStr}"/></td>
							</tr>
							</c:forEach>
						</table>
						</div>
					
					</c:if>
					<c:if test="${statBean.objectInd eq 6}"> 
					<table class="report" cellspacing="0" cellpadding="0">
					<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="20%">S/No.</td>
						<td class="tableCell" valign="top" width="20%"><c:out value="${roleBean.staffTitle}"/></td>
						<td class="tableCell" valign="top" align="left" width="40%"><c:out value="${roleBean.staffTypeName}"/> Name</td>
						<td class="tableCell" valign="top" align="left" width="20%">Level/Step</td>
						 
					</tr>
					</table>
					<div style="overflow:scroll;height:280px;width:100%;overflow:auto">
						<table class="report" cellspacing="0" cellpadding="0">
							<c:forEach items="${detailsBean}" var="innerBean" varStatus="gridRow">
							<tr class="reportEven">
								<td class="tableCell" valign="top" width="20%"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top" width="20%" ><c:out value="${innerBean.mode}"/></td>
								<td class="tableCell" align="left" valign="top" width="40%" ><c:out value="${innerBean.name}"/></td>
								<td class="tableCell" align="left" valign="top" width="20%" ><c:out value="${innerBean.salaryInfo.levelStepStr}"/></td>
								 
							</tr>
							</c:forEach>
						</table>
						</div>
					
					</c:if>
					<c:if test="${statBean.objectInd eq 7}"> 
					<table class="report" cellspacing="0" cellpadding="0">
					<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="10%">S/No.</td>
						<td class="tableCell" valign="top" width="10%"><c:out value="${roleBean.staffTitle}"/></td>
						<td class="tableCell" valign="top" align="left" width="40%"><c:out value="${roleBean.staffTypeName}"/> Name</td>
						<td class="tableCell" valign="top" align="left" width="10%">Level/Step</td>
						<td class="tableCell" valign="top" align="right" width="10%">Total Pay</td>
						<td class="tableCell" valign="top" align="right" width="10%">Total Ded.</td>
						<td class="tableCell" valign="top" align="right" width="10%">Net Pay</td>
					</tr>
					</table>
					<div style="overflow:scroll;height:280px;width:100%;overflow:auto">
						<table class="report" cellspacing="0" cellpadding="0">
							<c:forEach items="${detailsBean}" var="innerBean" varStatus="gridRow">
							<tr class="reportEven">
								<td class="tableCell" valign="top" width="10%"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.mode}"/></td>
								<td class="tableCell" align="left" valign="top" width="40%" ><c:out value="${innerBean.name}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.salaryInfo.levelStepStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.totalPayStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.totalDeductionsStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><font color="red"><c:out value="${innerBean.netPayStr}"/></font></td>
							</tr>
							</c:forEach>
						</table>
						</div>
					
					</c:if>
					<c:if test="${statBean.objectInd eq 8}"> 
					<table class="report" cellspacing="0" cellpadding="0">
					<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="10%">S/No.</td>
						<td class="tableCell" valign="top" width="10%"><c:out value="${roleBean.staffTitle}"/></td>
						<td class="tableCell" valign="top" align="left" width="40%"><c:out value="${roleBean.staffTypeName}"/> Name</td>
						<td class="tableCell" valign="top" align="left" width="10%">Level/Step</td>
						<td class="tableCell" valign="top" align="right" width="10%">Total Pay</td>
						<td class="tableCell" valign="top" align="right" width="10%">Total Ded.</td>
						<td class="tableCell" valign="top" align="right" width="10%">Net Pay</td>
					</tr>
					</table>
					<div style="overflow:scroll;height:280px;width:100%;overflow:auto">
						<table class="report" cellspacing="0" cellpadding="0">
							<c:forEach items="${detailsBean}" var="innerBean" varStatus="gridRow">
							<tr class="reportEven">
								<td class="tableCell" valign="top" width="10%"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.mode}"/></td>
								<td class="tableCell" align="left" valign="top" width="40%" ><c:out value="${innerBean.name}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.salaryInfo.levelStepStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.totalPayStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.totalDeductionsStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.netPayStr}"/></td>
							</tr>
							</c:forEach>
						</table>
						</div>
					
					</c:if>
					<c:if test="${statBean.objectInd eq 9}">
					<table class="report" cellspacing="0" cellpadding="0"> 
					<tr class="reportOdd header">
						<td class="tableCell" valign="top" align="left" width="10%">S/No.</td>
						<td class="tableCell" valign="top" align="left" width="10%"><c:out value="${roleBean.staffTitle}"/></td>
						<td class="tableCell" valign="top" align="left" width="30%"><c:out value="${roleBean.staffTypeName}"/> Name</td>
						<td class="tableCell" valign="top" align="left" width="10%">Level/Step</td>
						<td class="tableCell" valign="top" align="right" width="10%">Spec Allow.</td>
						<td class="tableCell" valign="top" align="right" width="10%">Gross Pay</td>
						<td class="tableCell" valign="top" align="right" width="10%">Tot. Ded.</td>
						<td class="tableCell" valign="top" align="right" width="10%">Net Pay</td>
					</tr>
					</table>
					<div style="overflow:scroll;height:280px;width:100%;overflow:auto">
						<table class="report" cellspacing="0" cellpadding="0">
							<c:forEach items="${detailsBean}" var="innerBean" varStatus="gridRow">
							<tr class="reportEven">
								<td class="tableCell" valign="top" width="10%"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.mode}"/></td>
								<td class="tableCell" align="left" valign="top" width="30%" ><c:out value="${innerBean.name}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.salaryInfo.levelStepStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.specAllowStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.totalPayStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.totalDeductionsStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.netPayStr}"/></td>
							</tr>
							</c:forEach>
						</table>
						</div>
					
					</c:if>
					<c:if test="${statBean.objectInd eq 10}"> 
					<table class="report" cellspacing="0" cellpadding="0">
					<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="10%">S/No.</td>
						<td class="tableCell" valign="top" width="10%"><c:out value="${roleBean.staffTitle}"/></td>
						<td class="tableCell" valign="top" align="left" width="40%"><c:out value="${roleBean.staffTypeName}"/> Name</td>
						<td class="tableCell" valign="top" align="left" width="10%">Level/Step</td>
						<td class="tableCell" valign="top" align="right" width="10%">Gross Pay</td>
						<td class="tableCell" valign="top" align="right" width="10%">Total Deductions</td>
						<td class="tableCell" valign="top" align="right" width="10%">Net Pay</td>
					</tr>
					</table>
					<div style="overflow:scroll;height:280px;width:100%;overflow:auto">
						<table class="report" cellspacing="0" cellpadding="0">
							<c:forEach items="${detailsBean}" var="innerBean" varStatus="gridRow">
							<tr class="reportEven">
								<td class="tableCell" valign="top" width="10%"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.mode}"/></td>
								<td class="tableCell" align="left" valign="top" width="40%" ><c:out value="${innerBean.name}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.salaryInfo.levelStepStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.totalPayStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.totalDeductionsStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.netPayStr}"/></td>
							</tr>
							</c:forEach>
						</table>
						</div>
					
					</c:if>
					<c:if test="${statBean.objectInd eq 11}"> 
					<table class="report" cellspacing="0" cellpadding="0">
						<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="10%">S/No.</td>
						<td class="tableCell" valign="top" width="10%"><c:out value="${roleBean.staffTitle}"/></td>
						<td class="tableCell" valign="top" align="left" width="50%"><c:out value="${roleBean.staffTypeName}"/> Name</td>
						<td class="tableCell" valign="top" align="left" width="10%">Level/Step</td>
						<td class="tableCell" valign="top" align="right" width="10%">Exp.Retire Date</td>
						<td class="tableCell" valign="top" align="right" width="10%">Termination Date</td>
					</tr>
					</table>
					<div style="overflow:scroll;height:280px;width:100%;overflow:auto">
						<table class="report" cellspacing="0" cellpadding="0">
							<c:forEach items="${detailsBean}" var="innerBean" varStatus="gridRow">
							<tr class="reportEven">
								<td class="tableCell" valign="top" width="10%"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.mode}"/></td>
								<td class="tableCell" align="left" valign="top" width="50%" ><c:out value="${innerBean.name}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.salaryInfo.levelStepStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.expectedDateOfRetirementStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.terminationDateStr}"/></td>
							</tr>
							</c:forEach>
						</table>
						</div>
					
					</c:if>
					<c:if test="${statBean.objectInd eq 12}"> 
					<table class="report" cellspacing="0" cellpadding="0">
						<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="10%">S/No.</td>
						<td class="tableCell" valign="top" width="10%"><c:out value="${roleBean.staffTitle}"/></td>
						<td class="tableCell" valign="top" align="left" width="50%"><c:out value="${roleBean.staffTypeName}"/> Name</td>
						<td class="tableCell" valign="top" align="left" width="10%">Level/Step</td>
						<td class="tableCell" valign="top" align="right" width="10%">Hire Date</td>
						<td class="tableCell" valign="top" align="right" width="10%">Birth Date</td>
					</tr>
					</table>
					<div style="overflow:scroll;height:280px;width:100%;overflow:auto">
						<table class="report" cellspacing="0" cellpadding="0">
							<c:forEach items="${detailsBean}" var="innerBean" varStatus="gridRow">
							<tr class="reportEven">
								<td class="tableCell" valign="top" width="10%"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.mode}"/></td>
								<td class="tableCell" align="left" valign="top" width="50%" ><c:out value="${innerBean.name}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.salaryInfo.levelStepStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.hireDateStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.birthDateStr}"/></td>
							</tr>
							</c:forEach>
						</table>
						</div>
					
					</c:if>
					<c:if test="${statBean.objectInd eq 13}"> 
					<table class="report" cellspacing="0" cellpadding="0">
					<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="10%">S/No.</td>
						<td class="tableCell" valign="top" width="10%"><c:out value="${roleBean.staffTitle}"/></td>
						<td class="tableCell" valign="top" align="left" width="40%"><c:out value="${roleBean.staffTypeName}"/> Name</td>
						<td class="tableCell" valign="top" align="left" width="10%">Level/Step</td>
						<td class="tableCell" valign="top" align="right" width="10%">Contract Start Date</td>
						<td class="tableCell" valign="top" align="right" width="10%">Contract End Date</td>
						 
					</tr>
					</table>
					<div style="overflow:scroll;height:280px;width:100%;overflow:auto">
						<table class="report" cellspacing="0" cellpadding="0">
							<c:forEach items="${detailsBean}" var="innerBean" varStatus="gridRow">
							<tr class="reportEven">
								<td class="tableCell" valign="top" width="10%"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.mode}"/></td>
								<td class="tableCell" align="left" valign="top" width="40%" ><c:out value="${innerBean.name}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${innerBean.salaryInfo.levelStepStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.allowanceStartDateStr}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%" ><c:out value="${innerBean.allowanceEndDateStr}"/></td>
 							</tr>
							</c:forEach>
						</table>
						</div>
					
					</c:if>
				</table>
				<br/>
				<br/>
				
				</td>
			</tr>
 			
		</table>
		<br>
		
		</td>
		
	</tr>
	<tr>
				<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
			</tr>
	</table>
	
	
		
	</form:form>
</body>

</html>
