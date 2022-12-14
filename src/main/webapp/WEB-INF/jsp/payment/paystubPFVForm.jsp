<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Pay Stub Printer Friendly Version </TITLE>
			<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
			<META HTTP-EQUIV="Expires" CONTENT="-1">
			<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache, no-store, must-revalidate">
			<META HTTP-EQUIV="Cache-Control" CONTENT="post-check=0, pre-check=0">
		
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<style>
TABLE.paycheck {
	margin : 3px;
	border: 1px solid #8080a0;
	background-color : white;
}
TABLE.checkPart {
	margin : 0px;
	border: medium double #DDEAE2;
	background-color : #f8fffc;
}
TABLE.detailsPart {
	border-collapse: collapse;
	margin : 0px;
	border: 1px solid #a0a0a0;
	background-color : white;
}
TABLE.detailsWages {
	border-collapse: collapse;
	margin : 0px;
	margin-bottom : 3px;
	border: 1px solid #a0a0a0;
	background-color : white;
}
TH.detailsPart {
	align: left;
	font-size: 8pt;
	font-weight: normal;
	background-color: #e0e0e0;
}
TH.detailsPartLeft {
	text-align: left;
	font-size: 8pt;
	font-weight: normal;
	background-color: #e0e0e0;
}
TH.detailsPartRight {
	text-align: right;
	font-size: 8pt;
	font-weight: normal;
	background-color: #e0e0e0;
}
TH.detailsPartCenter {
	text-align: center;
	font-size: 8pt;
	font-weight: normal;
	background-color: #e0e0e0;
}
.checksmall {
	font-size= 8pt;
}
.checkCoAddr {
	font-size= 7pt;
	padding-left: 1cm;
}
</style>

</head>

<body class="main">

	<table class="main" width="50%" border="0"  bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		

		<tr>
			<td>

			<table  width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
				
                  <tr>
		<td colspan="2">
		<div class="title">
			<c:out value="${empPayMiniBean.employeeName}" />'s Pay Slip </div>
		</td>
	</tr>
	<tr align="center">
		<td valign="top" class="mainBody" id="mainBody">
		<!--- links to "operate" on this paystub --->
		
		<table width="95%">
			<tr>
				<td colspan="2">

				<p>
				<table width="100%">
					<tr>
						<td align="right">Paystub for period: <c:out value="${empPayMiniBean.payPeriod}" />
						</td>
					</tr>
				</table>
				<table width="100%" class="paycheck">
					<tr>
						<td colspan="3" align="right"></td>
					</tr>
					<tr>
						<td colspan="3">
						<table width="100%" class="checkPart">
							<tr>
								<td>
								<table width="100%">
									
									<tr>
									   
										<td nowrap><c:out value="${empPayMiniBean.employerName}" /><br>
										<c:out value="${empPayMiniBean.employerAddress}" /><br>
										<c:out value="${empPayMiniBean.employerCityStateZip}" /> </td>
										<td width="75%" align="center">
										<span style="text-transform: uppercase; font-size: 11pt; color: #bbc8c0">
										<img src="images/coatOfArms.png" width="50px" height="50px">
										<br><b><c:out value="${empPayMiniBean.mda}" /></b><br>
										<b><c:out value="${empPayMiniBean.paycheckStatus}" /></b> </span></td>
										<td nowrap align="right">Date: <c:out value="${empPayMiniBean.payDate}" />
										</td>
									</tr>
									<tr>
										<td>&nbsp;</td>
									</tr>
									<tr>
										<td valign="top" colspan="2"><c:out value="${empPayMiniBean.employeeName}" /><br>
										<c:out value="${empPayMiniBean.employeeId}"/><br>
										<c:out value="${empPayMiniBean.employeeAddress}" /><br>
										<c:out value="${empPayMiniBean.employeeCityStateZip}" /> </td>
										<td nowrap align="right" valign="top">Net 
										Pay:<u> ???<c:out value="${empPayMiniBean.currentNetPayStr}" />
										</u></td>
									</tr>
									<tr>
										<td class="checksmall" colspan="3" align="right">
										<c:out value="${empPayMiniBean.bankBranchName}"/> 
										</td>
									</tr>
									<tr>
										<td class="checksmall" colspan="3" align="right">
										<c:out value="${empPayMiniBean.accountNumber}"/> <br>
										</td>
									</tr>
								</table>
								</td>
							</tr>
						</table>
						</td>
					</tr>
					<tr>
						<td valign="top">
						<table class="detailsWages" width="100%">
							<tr>
								<th class="detailsPart"><b>PAY</b></th>
								<th class="detailsPartLeft" >Level/Step</th>
                                <c:if test="${roleBean.pensioner}">
								  <th class="detailsPartRight" >Monthly Pension</th>
								 </c:if>
								 <c:if test="${not roleBean.pensioner}">
								     <th class="detailsPartRight" >Basic Salary</th>
								 </c:if>
								<th class="detailsPartRight" >Gross Pay</th>
								<th class="detailsPartRight">YTD</th>
							</tr>
							<tr>
								<td nowrap><c:out value="${empPayMiniBean.payType}" /></td>
								<td>
								<div align="right">
									<c:out value="${empPayMiniBean.levelAndStep}" /></div>
								</td>
								<td>
								<div align="right">
									<c:out value="${empPayMiniBean.basicSalaryStr}" /></div>
								</td>
								<td>
								<div align="right">
								&nbsp;???<c:out value="${empPayMiniBean.currentTotalPayStr}" />
								</div>
								</td>
								<td>
								<div align="right">
								&nbsp;???<c:out value="${empPayMiniBean.salaryYTDStr}" />
								</div>
								</td>
							</tr>
						</table>
						<table class="detailsPart" width="100%">
							<tr>
								<th class="detailsPart" align="left"><b>Allowances</b></th>
								<th class="detailsPartRight" >Current</th>
								<th class="detailsPartRight" >YTD</th>
							</tr>
							<c:forEach items="${mandatoryList}" var="manList">
							<tr>
								<td><c:out value="${manList.name}" />&nbsp; </td>
								<td>
								<div align="right">
									???<c:out value="${manList.currentDeductionStr}" /></div>
								</td>
								<td>
								<div align="right">
									???<c:out value="${manList.yearToDateStr}" /></div>
								</td>
							</tr>
							</c:forEach>
							<!--
							<c:forEach items="${allowanceList}" var="aList">
							<tr>
								<td><c:out value="${aList.name}" />&nbsp; </td>
								<td>
								<div align="right">
									???<c:out value="${aList.currentDeductionStr}" /></div>
								</td>
								<td>
								<div align="right">
									???<c:out value="${aList.yearToDateStr}" /></div>
								</td>
							</tr>
							</c:forEach>
							-->
							<tr bgcolor="#CCCCCC">
								<td><b>Total</b></td>
								<td>
								<div align="right">
									<b>???<c:out value="${empPayMiniBean.currentAllowanceTotalStr}" /></b></div>
								</td>
								<td>
								<div align="right">
									<b>???<c:out value="${empPayMiniBean.allowanceTotalStr}" /></b></div>
								</td>
							</tr>
							
							</table>
						</td>
						<td></td>
						<td valign="top">
						<table class="detailsPart" width="100%">
							<tr>
								<th class="detailsPart"><b>TAXES WITHHELD</b></th>
								<th class="detailsPartCenter" >Current</th>
								<th class="detailsPartRight">YTD</th>
							</tr>
							 <tr>
								<td><c:out value="${empPayMiniBean.employeeState}" /> Income Tax </td>
								<td>
								<div align="center">
								&nbsp;???<c:out value="${empPayMiniBean.currentTaxesPaidStr}" /></div>
								</td>
								<td>
								<div align="right">
								&nbsp;???<c:out value="${empPayMiniBean.taxesPaidYTDStr}" /></div>
								</td>
							</tr>
						</table>
						</td>
					</tr>
					<tr>
						<td colspan="2"></td>
						<td valign="top">
						<table class="detailsPart" width="100%">
							<tr>
								<th class="detailsPart" align="left"><b>SUMMARY</b></th>
								<th class="detailsPartRight" align="right">Current</th>
								<th class="detailsPartRight" align="right">YTD</th>
							</tr>
							<tr>
								<td>Total Pay</td>
								<td>
								<div align="right">
									???<c:out value="${empPayMiniBean.currentTotalPayStr}" /></div>
								</td>
								<td>
								<div align="right">
									???<c:out value="${empPayMiniBean.salaryYTDStr}" /></div>
								</td>
							</tr>
							<tr>
								<td>Deductions</td>
								<td>
								<p align="right">???<c:out value="${empPayMiniBean.currentGarnTotalStr}" /></p>
								</td>
								<td>
								<p align="right">???<c:out value="${empPayMiniBean.garnishmentTotalStr}" /></p>
								</td>
							</tr>
							<tr>
								<td>P.A.Y.E</td>
								<td>
								<div align="right">
									???<c:out value="${empPayMiniBean.currentTaxesPaidStr}" /></div>
								</td>
								<td>
								<div align="right">
									???<c:out value="${empPayMiniBean.taxesPaidYTDStr}" /></div>
								</td>
							</tr>
						</table>
						<table width="100%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="right">Net This Check:&nbsp; </td>
								<td width="8%" align="right">???<c:out value="${empPayMiniBean.currentNetPayStr}" /></td>
							</tr>
						</table>
						</td>
					</tr>
					
				</table>
				<p/>
				</td>
			</tr>
			<tr>
				<td>
				<table width="100%">
					<tr>
						<td valign="top" width="50%">
						<table class="detailsPart" width="100%">
							<tr bgcolor="#CCCCCC">
								<th class="detailsPart" align="left"><b>Deductions</b></th>
								<th class="detailsPartRight" align="right">Current</th>
								<th class="detailsPartRight" align="right">YTD</th>
							</tr>
							<c:forEach items="${deductList}" var="dedList">
							<tr>
								<td><c:out value="${dedList.name}" /></td>
								<td align="right">
								<div>
								&nbsp;???<c:out value="${dedList.currentDeductionStr}" />
								</div>
								</td>
								<td>
								<div align="right">
								&nbsp;???<c:out value="${dedList.yearToDateStr}" />
								</div>
								</td>
							</tr>
							</c:forEach>
							<c:forEach items="${pensionList}" var="penList">
							<tr>
								<td><c:out value="${penList.name}" /></td>
								<td align="right">
								<div>
								&nbsp;???<c:out value="${penList.currentDeductionStr}" />
								</div>
								</td>
								<td>
								<div align="right">
								&nbsp;???<c:out value="${penList.yearToDateStr}" />
								</div>
								</td>
							</tr>
							</c:forEach>
							<c:forEach items="${garnList}" var="garnish">
							<tr>
								<td><c:out value="${garnish.name}" />&nbsp; </td>
								<td>
								<div align="right">
									???<c:out value="${garnish.currentGarnishmentStr}" /></div>
								</td>
								<td>
								<div align="right">
									???<c:out value="${garnish.yearToDateStr}" /></div>
								</td>
							</tr>
							</c:forEach>
							<tr bgcolor="#CCCCCC">
								<td><b>Total</b></td>
								<td>
								<div align="right">
									<b>???<c:out value="${empPayMiniBean.currentGarnTotalStr}" /></b></div>
								</td>
								<td>
								<div align="right">
									<b>???<c:out value="${empPayMiniBean.garnishmentTotalStr}" /></b></div>
								</td>
							</tr>
						</table>
						</td>
						
						<td valign="top" width="50%">
						
						<table border="0" width="100%">
							<tr align="right">
									<td align="right">&nbsp;</td>
							</tr>
							<c:forEach items="${empPayMiniBean.instructionList}" var="inst">
								<tr align="right">
									<td align="right">**<font color="red"><i><c:out value="${inst.name}" /></i></font></td>
									
								</tr>
							</c:forEach>
							
						</table>
						 
						</td>
						
					</tr>
					
				</table>
				</td>
			</tr>
			<!-- 
			<tr>
				<td>
				<table>
					<tr>
						<td valign="top">
						<table class="detailsPart">
							<tr bgcolor="#CCCCCC">
								<th class="detailsPart" align="left"><b>Other Deductions</b></th>
								<th class="detailsPart" width="80">Current</th>
								<th class="detailsPart" width="80">YTD</th>
							</tr>
							<c:forEach items="${garnList}" var="garnish">
							<tr>
								<td><c:out value="${garnish.name}" />&nbsp; </td>
								<td>
								<div align="right">
									???<c:out value="${garnish.currentGarnishmentStr}" /></div>
								</td>
								<td>
								<div align="right">
									???<c:out value="${garnish.yearToDateStr}" /></div>
								</td>
							</tr>
							</c:forEach>
							<tr bgcolor="#CCCCCC">
								<td>Total</td>
								<td>
								<div align="right">
									???<c:out value="${empPayMiniBean.currentGarnTotalStr}" /></div>
								</td>
								<td>
								<div align="right">
									???<c:out value="${empPayMiniBean.garnishmentTotalStr}" /></div>
								</td>
							</tr>
						</table>
						</td>
						<td valign="top">
						
						<table class="detailsPart">
							<tr bgcolor="#CCCCCC">
								<th class="detailsPart" align="left"><b>COMPANY 
								CONTRIBUTIONS</b></th>
								<th class="detailsPart" width="80">Current</th>
								<th class="detailsPart" width="80">YTD</th>
							</tr>
							<c:forEach items="${contList}" var="contribution">
							<tr>
								<td><c:out value="${contribution.name}" /></td>
								<td align="right">
								<div>
								&nbsp;???<c:out value="${contribution.currentContributionStr}" />
								</div>
								</td>
								<td>
								<div align="right">
								&nbsp;???<c:out value="${contribution.yearToDateStr}" />
								</div>
								</td>
							</tr>
							</c:forEach>
							<tr bgcolor="#CCCCCC">
								<td>Total</td>
								<td>
								<div align="right">
									???<c:out value="${empPayMiniBean.currentContTotalStr}" /></div>
								</td>
								<td>
								<div align="right">
									???<c:out value="${empPayMiniBean.contributionTotalStr}" /></div>
								</td>
							</tr>
						</table>
						 
						</td>
					</tr>
				</table>
				</td>
			</tr>
			-->
			
		</table>
		</td>
		</tr>
		</table>
		</td>
		
	</tr>
	<tr><%@ include file="/WEB-INF/jsp/footerFile.jsp" %></tr>
	</table>

</body>

</html>
		
