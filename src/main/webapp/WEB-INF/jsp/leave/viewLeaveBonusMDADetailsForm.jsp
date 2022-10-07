<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title>Leave Bonus By MDA</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>

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
					Leave Bonus for <c:out value="${miniBean.name}"></c:out><br>
					Year : <c:out value="${miniBean.runYear}"></c:out></div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainBody" id="mainBody">
				
				<div class="reportDescText">
					This report shows the details of Leave Bonus paid to each employee
					within the MDA and YEAR you select. </div>
				<br />
				<br />
				Current MDA : <b><c:out value="${miniBean.name}"/></b><br/>
				Leave Bonus : <b><font color="green"><c:out value="${miniBean.totalLeaveBonusStr}"/></font></b><br/>
				No. of Emp  : <b><c:out value="${miniBean.totalNoOfEmp}"/></b>
				<br/>
				<br/>
				<br/>
				<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
					<tr>
						<td class="reportFormControls">
				
				 		M.D.A: <form:select path="mdaInfo.id">
                               <form:option value="0">Select MDA</form:option>
						       <c:forEach items="${mdaList}" var="mList">
						      <form:option value="${mList.id}" title="${mList.name}">${mList.codeName}</form:option>
						      </c:forEach>
						      </form:select>
                              &nbsp; 
                       Year: <form:select path="runYear">
						        <c:forEach items="${yearList}" var="yList">
						          <form:option value="${yList.salaryInfoInstId}">${yList.name}</form:option>
						        </c:forEach>
						      </form:select>
                        &nbsp; 
                 	</td>
					</tr>
					
					<tr>
						<td class="reportFormControlsSpacing"></td>
					</tr>
					<tr>
						<td>
						<table class="report" cellspacing="0" cellpadding="0">
							<tr class="reportOdd header">
								<td class="tableCell" valign="top" width="10%"><c:out value="${roleBean.staffTitle}"/></td>
								<td class="tableCell" valign="top" width="50%"><c:out value="${roleBean.staffTypeName}"/> Name</td>
								<td class="tableCell" valign="top" width="20%">Pay Group</td>
								<td class="tableCell" valign="top" width="10%">Level/Step</td>
								<td class="tableCell" valign="top" width="10%">Leave Bonus</td>
							</tr>
						</table>
						<div style="overflow:scroll;height:280px;width:100%;overflow:auto">
						<table class="report" cellspacing="0" cellpadding="0">
							<c:forEach items="${miniBean.leaveBonusList}" var="lbList">
							<tr class="${lbList.displayStyle}">
								<td class="tableCell" align="left" valign="top" width="10%"><c:out value="${lbList.employeeId}"/></td>
								<td class="tableCell" align="left" valign="top" width="50%" ><c:out value="${lbList.name}"/></td>
								<td class="tableCell" align="left" valign="top" width="20%"><c:out value="${lbList.payGroup}"/></td>
								<td class="tableCell" align="left" valign="top" width="10%" ><c:out value="${lbList.levelAndStep}"/></td>
								<td class="tableCell" align="right" valign="top" width="10%"><c:out value="${lbList.leaveBonusAmountStr}"/></td>
							</tr>
							</c:forEach>
						</table>
						</div>
						</td>
					</tr>
					<tr>
                         <td class="buttonRow" align="right">
                             <input type="image" name="_updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                         </td>
                     </tr>
				</table>
				<br>
				<br>
				<table>
				<tr>
				<td class="activeTD"><form:radiobutton path="reportType" value="1"  onclick="if (this.checked) { document.getElementById('excelReportLink').style.display = 'none';document.getElementById('pdfReportLink').style.display = '';}"/>PDF Report Links <form:radiobutton path="reportType" value="2" onclick="if (this.checked) { document.getElementById('excelReportLink').style.display = '';document.getElementById('pdfReportLink').style.display = 'none';}"/> Excel Report Links</td>
				</tr>
				</table>
				<br>
				
				<div id="excelReportLink" class="reportBottomPrintLink" style='display:none'>
					<a href="${appContext}/leaveBonusByMdaExcel.do?yr=${miniBean.runYear}&mdaInd=${miniBean.mdaInfo.id}">
					Export to Excel&copy; </a><br />
					
				</div>
				<div id="pdfReportLink" class="reportBottomPrintLink" style='display:none'>
					<a href="${appContext}/leaveBonusByMdaPDF.do?yr=${miniBean.runYear}&mdaInd=${miniBean.mdaInfo.id}">
					Export to PDF&copy; </a><br />
					
				</div>
				<br>
				</td>
				<td valign="top" class="navSide"><br>
				<br>
				<!-- Here to put space between this and any following divs -->
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

	</html>
