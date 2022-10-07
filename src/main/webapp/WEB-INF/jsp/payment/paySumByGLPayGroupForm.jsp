<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title>Payroll Breakdown by GL/Pay Group</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
<script src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script src="<c:url value="scripts/utility_script.js"/>"></script>
<script src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>
</head>

<body class="main">
<form:form modelAttribute="miniBean">
<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
	<tr>
		<td>
		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			
			<tr>
				<td colspan="2">
				<div class="title">
					Ogun State Government - <br>
					Payroll Summary by Grade Level/Pay Group<br>
					<c:out value="${miniBean.payPeriodStr}"/>
					</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainBody" id="mainBody">
				
				<span class="reportTopPrintLink">
				<a href="<c:url value='/paySumByGLPayGroupExcel.do?rm=${miniBean.runMonth}&ry=${miniBean.runYear}'/>">
				View in Excel </a><br />
				</span>
				<div class="reportDescText">
					This report breaks down the Payroll Summary by Grade Level and Paygroup.
					<br />
				</div>
				<table cellpadding="1" cellspacing="2">
									<tr align="left">
											<td class="activeTH"><b>Filter Conditions </b></td>
									</tr>
									<tr>
                                        <td class="activeTD">
                                            
                                                   Month&nbsp;<form:select path="runMonth">
														<form:option value="-1">&lt;Select Month&gt;</form:option>
														<c:forEach items="${monthList}" var="mList">
														<form:option value="${mList.id}">${mList.name}</form:option>
														</c:forEach>
												</form:select> 
												 &nbsp;&nbsp;Year&nbsp;&nbsp;<form:select path="runYear">
														<form:option value="0">&lt;Select Year&gt;</form:option>
														<c:forEach items="${yearList}" var="yList">
														<form:option value="${yList.id}">${yList.name}</form:option>
														</c:forEach>
												</form:select>                                     
                                        </td>
                                        
                                    </tr> 
                                    <tr>
				                     <td class="buttonRow" align="right">
				                       <input type="image" name="_updateReport" value="updateReport" alt="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
				                     </td>
				                   </tr>
                                    </table>
				<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
					
					
					<tr>
					
						<td class="reportFormControlsSpacing"></td>
					</tr>
					<tr>
						<td>
						<h3>Ogun State Payroll Summary By Grade Level Pay Group for <c:out value="${miniBean.payPeriodStr}"/> </h3>
						<br />
						<table class="report" cellspacing="0" cellpadding="0">
							<tr class="reportOdd header">
								<td class="tableCell" align="center" valign="top">
								S/No.</td>
								<td class="tableCell" align="center" valign="top">
								Grade Level/Pay Group</td>
								<td class="tableCell" align="center" valign="top">
								<c:out value="${miniBean.payPeriodStr}"/>&nbsp;Staff Count</td>					
								<td class="tableCell" align="center" valign="top">
								Gross (₦)</td>
								<td class="tableCell" align="center" valign="top">
								Total Deductions (₦)</td>
								<td class="tableCell" align="center" valign="top">
								Net Pay (₦)</td>							
							</tr>
							<c:forEach items="${miniBean.namedEntityBeanList}" var="wBean" varStatus="gridRow">
							<tr class="${wBean.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="right" valign="top"><a href="<c:url value='/viewEmployeeTypeDetails.do?rm=${miniBean.runMonth}&ry=${miniBean.runYear}&tc=${wBean.typeOfEmpType} '/>" >
									<c:out value="${wBean.name}"/></a></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.noOfActiveEmployees}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.totalPayStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.totalDeductionsStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.netPayStr}"/></td>
								    
							</tr>
							</c:forEach>							
							<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">&nbsp;</td>
								<td class="tableCell" align="right"><c:out value="${miniBean.totalNoOfEmp}"/></td>
								<td class="tableCell" align="right"><c:out value="${miniBean.totalGrossSalaryStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalDeductionsAsStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalNetPayAsStr}"/></td>
								
							</tr>
							
						</table>
						<br>
						</td>
						 
					</tr>
					
                  </table>
                
				<div >
				<a href="<c:url value='/paySumByGLPayGroupExcel.do?rm=${miniBean.runMonth}&ry=${miniBean.runYear}'/>">
				View in Excel </a><br />
				</div>
				
				
				
				</td>
				<td valign="top" class="navSide"><br>
				<br>
				
				</td>
				</tr>
				</table>
				</td>

			</tr>
			<tr> <%@ include file="/WEB-INF/jsp/footerFile.jsp" %></tr>
			</table>
			</form:form>
		</body>

	</html>
	
