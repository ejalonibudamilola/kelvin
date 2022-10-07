<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>


<html>
		<head>
			<title>Pay Record Card</title>
			<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
			<META HTTP-EQUIV="Expires" CONTENT="-1">
			<META HTTP-EQUIV="Cache-Control" VALUE="no-cache, no-store, must-revalidate">
			<META HTTP-EQUIV="Cache-Control" VALUE="post-check=0, pre-check=0">
		
			

			<link rel="stylesheet" href="styles/omg.css" type="text/css">
       	 	<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        	<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
			
			
		</head>
<body>
	<form:form modelAttribute="miniBean">			
				<style> body { margin-left : 20px;}.report td {font-size: 9pt;}.report .header {background-color : #E2E2E2; font-weight : bold; color : #000000;}.report .footer {background-color : #E2E2E2; font-weight : bold; color : #000000;}table.report {width : auto;}table.reportMain {width : auto;}tr.reportEven {background-color : #F7F7F7;}td.reportFormControls {background-color : #E2E2E2; font-weight : bold; color : #000000;}h3 {margin-left: 10px;}</style>
				<div class="titlePrinterFriendly"><c:out value="${miniBean.employeeName}"/><br>Pay Record Card</div>
		    	<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
					
				<tr>
					<td class="reportFormControlsSpacing"></td>
				</tr>
				
				<tr>
				<td valign="top" class="mainbody" id="mainbody">
				
     				
				<br/>
					
					<table class="formtable" border="0" cellspacing="0" cellpadding="3" width="40%" align="left" >
						<tr align="left">
							<td class="activeTH">Employee Information </td>
						</tr>
						<tr>
							<td class="activeTD">
							   <fieldset>
							   <legend><b>Employee Details</b></legend>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
										<td width="25%" align="left">Employee Name :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${miniBean.employeeName}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Employee/Staff ID :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${miniBean.empId}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Organization :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${miniBean.organization}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Date of Birth :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${miniBean.birthDateStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Hire Date :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${miniBean.hireDateStr}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Pay Group - Level &amp; Step :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${miniBean.salaryScaleLevelAndStep}"/></td>
									</tr>
									
									</table>						
								</fieldset>
								
							</td>
						</tr>
						
					</table>
					<table><tr><td>&nbsp;</td></tr>
					</table>
					<br>
					<br>
					<table class="report" cellspacing="0" cellpadding="0">
					<c:choose>
					  <c:when test="${miniBean.firstMonthBean.gradeLevel}">
						<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="5%">&nbsp;</td>
						<td class="tableCell" valign="top" align="center" width="5%">Basic</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Rent</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Transport</td>
						<td class="tableCell" valign="top" align="center" width="5%">Meal</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Utility</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Furniture</td>
						<td class="tableCell" valign="top" align="center" width="5%">Domestic Servant</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Entertainment</td>
						<td class="tableCell"  valign="top" align="center" width="5%">TSS</td>
						<td class="tableCell" valign="top" align="center" width="5%">Principal Allowance</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Union Dues</td>
						<td class="tableCell"  valign="top" align="center" width="5%">N.H.F</td>
						<td class="tableCell"  valign="top" align="center" width="5%">P.A.Y.E</td>
						<td class="tableCell"  valign="top" align="center" width="5%">T.W.S</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Other Deductions</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Contributions</td>		
						<td class="tableCell"  valign="top" align="center" width="5%">Net Pay</td>					
					   </tr>
				       <tr>
				       <td class="tableCell" valign="top"  width="5%" nowrap><c:out value="${miniBean.firstMonthBean.monthAndYearStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.firstMonthBean.basicStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.rentStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.transportStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.firstMonthBean.mealStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.utilityStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.furnitureStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.firstMonthBean.domesticServantStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.entertainmentStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.tssStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.firstMonthBean.principalAllowanceStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.unionDuesStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.nhfStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.payeStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.twsStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.otherDeductionsStr}"/></td>		
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.otherContributionsStr}"/></td>	
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.netPayStr}"/></td>					
					   </tr>
					  
				     </c:when>
					 <c:otherwise>
					   <tr class="reportOdd header">
					    
						<td class="tableCell" valign="top" width="5%">&nbsp;</td>
						<td class="tableCell" valign="top" align="center" width="5%">Basic</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Rent</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Transport</td>
						<td class="tableCell" valign="top" align="center" width="5%">Meal</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Utility</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Furniture</td>
						<td class="tableCell" valign="top" align="center" width="5%">Domestic Servant</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Entertainment</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Hazard</td>
						<td class="tableCell" valign="top" align="center" width="5%">Call Duty</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Journal</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Academic Allowance</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Rural Posting</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Union Dues</td>
						<td class="tableCell"  valign="top" align="center" width="5%">N.H.F</td>
						<td class="tableCell"  valign="top" align="center" width="5%">P.A.Y.E</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Other Deductions</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Contributions</td>		
						<td class="tableCell"  valign="top" align="center" width="5%">Net Pay</td>					
					   </tr>
				    	<tr>
						<td class="tableCell" valign="top"  width="5%" nowrap><c:out value="${miniBean.firstMonthBean.monthAndYearStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.firstMonthBean.basicStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.rentStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.transportStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.firstMonthBean.mealStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.utilityStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.furnitureStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.firstMonthBean.domesticServantStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.entertainmentStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.hazardStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.firstMonthBean.callDutyStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.firstMonthBean.journalStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.firstMonthBean.academicAllowanceStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.firstMonthBean.ruralPostingStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.unionDuesStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.nhfStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.payeStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.otherDeductionsStr}"/></td>	
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.otherContributionsStr}"/></td>		
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.firstMonthBean.netPayStr}"/></td>					
					   
					   </tr>
					   
					   </c:otherwise>
					</c:choose>
					 
					</table>
					<br>
					<!-- Second -->
					<table class="report" cellspacing="0" cellpadding="0">
					<c:choose>
					  <c:when test="${miniBean.secondMonthBean.gradeLevel}">
						<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="5%">&nbsp;</td>
						<td class="tableCell" valign="top" align="center" width="5%">Basic</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Rent</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Transport</td>
						<td class="tableCell" valign="top" align="center" width="5%">Meal</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Utility</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Furniture</td>
						<td class="tableCell" valign="top" align="center" width="5%">Domestic Servant</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Entertainment</td>
						<td class="tableCell"  valign="top" align="center" width="5%">TSS</td>
						<td class="tableCell" valign="top" align="center" width="5%">Principal Allowance</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Union Dues</td>
						<td class="tableCell"  valign="top" align="center" width="5%">N.H.F</td>
						<td class="tableCell"  valign="top" align="center" width="5%">P.A.Y.E</td>
						<td class="tableCell"  valign="top" align="center" width="5%">T.W.S</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Other Deductions</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Contributions</td>		
						<td class="tableCell"  valign="top" align="center" width="5%">Net Pay</td>					
					   </tr>
				       <tr>
				       <td class="tableCell" valign="top"  width="5%" nowrap><c:out value="${miniBean.secondMonthBean.monthAndYearStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.secondMonthBean.basicStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.rentStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.transportStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.secondMonthBean.mealStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.utilityStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.furnitureStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.secondMonthBean.domesticServantStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.entertainmentStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.tssStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.secondMonthBean.principalAllowanceStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.unionDuesStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.nhfStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.payeStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.twsStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.otherDeductionsStr}"/></td>		
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.otherContributionsStr}"/></td>	
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.netPayStr}"/></td>					
					   </tr>
					  
				     </c:when>
					 <c:otherwise>
					   <tr class="reportOdd header">
					    
						<td class="tableCell" valign="top" width="5%">&nbsp;</td>
						<td class="tableCell" valign="top" align="center" width="5%">Basic</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Rent</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Transport</td>
						<td class="tableCell" valign="top" align="center" width="5%">Meal</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Utility</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Furniture</td>
						<td class="tableCell" valign="top" align="center" width="5%">Domestic Servant</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Entertainment</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Hazard</td>
						<td class="tableCell" valign="top" align="center" width="5%">Call Duty</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Journal</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Academic Allowance</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Rural Posting</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Union Dues</td>
						<td class="tableCell"  valign="top" align="center" width="5%">N.H.F</td>
						<td class="tableCell"  valign="top" align="center" width="5%">P.A.Y.E</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Other Deductions</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Contributions</td>		
						<td class="tableCell"  valign="top" align="center" width="5%">Net Pay</td>					
					   </tr>
				    	<tr>
						<td class="tableCell" valign="top"  width="5%" nowrap><c:out value="${miniBean.secondMonthBean.monthAndYearStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.secondMonthBean.basicStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.rentStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.transportStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.secondMonthBean.mealStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.utilityStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.furnitureStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.secondMonthBean.domesticServantStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.entertainmentStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.hazardStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.secondMonthBean.callDutyStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.secondMonthBean.journalStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.secondMonthBean.academicAllowanceStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.secondMonthBean.ruralPostingStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.unionDuesStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.nhfStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.payeStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.otherDeductionsStr}"/></td>	
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.otherContributionsStr}"/></td>		
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.secondMonthBean.netPayStr}"/></td>					
					   </tr>
					   
					   </c:otherwise>
					</c:choose>
					 
					</table>
					<br>
					<!--  Third -->
					<table class="report" cellspacing="0" cellpadding="0">
					<c:choose>
					  <c:when test="${miniBean.thirdMonthBean.gradeLevel}">
						<tr class="reportOdd header">
						<td class="tableCell" valign="top" width="5%">&nbsp;</td>
						<td class="tableCell" valign="top" align="center" width="5%">Basic</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Rent</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Transport</td>
						<td class="tableCell" valign="top" align="center" width="5%">Meal</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Utility</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Furniture</td>
						<td class="tableCell" valign="top" align="center" width="5%">Domestic Servant</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Entertainment</td>
						<td class="tableCell"  valign="top" align="center" width="5%">TSS</td>
						<td class="tableCell" valign="top" align="center" width="5%">Principal Allowance</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Union Dues</td>
						<td class="tableCell"  valign="top" align="center" width="5%">N.H.F</td>
						<td class="tableCell"  valign="top" align="center" width="5%">P.A.Y.E</td>
						<td class="tableCell"  valign="top" align="center" width="5%">T.W.S</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Other Deductions</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Contributions</td>		
						<td class="tableCell"  valign="top" align="center" width="5%">Net Pay</td>					
					   </tr>
				       <tr>
				       <td class="tableCell" valign="top"  width="5%" nowrap><c:out value="${miniBean.thirdMonthBean.monthAndYearStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.basicStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.rentStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.transportStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.mealStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.utilityStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.furnitureStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.domesticServantStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.entertainmentStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.tssStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.principalAllowanceStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.unionDuesStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.nhfStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.payeStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.twsStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.otherDeductionsStr}"/></td>		
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.otherContributionsStr}"/></td>	
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.netPayStr}"/></td>					
					   </tr>
					  
				     </c:when>
					 <c:otherwise>
					   <tr class="reportOdd header">
					    
						<td class="tableCell" valign="top" width="5%">&nbsp;</td>
						<td class="tableCell" valign="top" align="center" width="5%">Basic</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Rent</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Transport</td>
						<td class="tableCell" valign="top" align="center" width="5%">Meal</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Utility</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Furniture</td>
						<td class="tableCell" valign="top" align="center" width="5%">Domestic Servant</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Entertainment</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Hazard</td>
						<td class="tableCell" valign="top" align="center" width="5%">Call Duty</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Journal</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Academic Allowance</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Rural Posting</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Union Dues</td>
						<td class="tableCell"  valign="top" align="center" width="5%">N.H.F</td>
						<td class="tableCell"  valign="top" align="center" width="5%">P.A.Y.E</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Other Deductions</td>
						<td class="tableCell"  valign="top" align="center" width="5%">Contributions</td>		
						<td class="tableCell"  valign="top" align="center" width="5%">Net Pay</td>					
					   </tr>
				    	<tr>
						<td class="tableCell" valign="top"  width="5%" nowrap><c:out value="${miniBean.thirdMonthBean.monthAndYearStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.basicStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.rentStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.transportStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.mealStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.utilityStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.furnitureStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.domesticServantStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.entertainmentStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.hazardStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.callDutyStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.journalStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.academicAllowanceStr}"/></td>
						<td class="tableCell" valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.ruralPostingStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.unionDuesStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.nhfStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.payeStr}"/></td>
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.otherDeductionsStr}"/></td>	
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.otherContributionsStr}"/></td>		
						<td class="tableCell"  valign="top" align="center"><c:out value="${miniBean.thirdMonthBean.netPayStr}"/></td>					
					   </tr>
					   
					   </c:otherwise>
					</c:choose>
					 
					</table>
					<br>
					<br>
					<br>
					<br>
					
					<br>
					<br>
					
				</td>
			</tr>
			<tr>
				<td>
				<div class="reportBottomPrintLink">
							<a href="javascript:window.print()">
								<img src="images/printer_h.png" border="0" alt="Print" title="Print Pay Record Card"></a><br />			
					</div>
				</td>
			</tr>
			
			</table>
			
		
</form:form>
</body>
</html>
