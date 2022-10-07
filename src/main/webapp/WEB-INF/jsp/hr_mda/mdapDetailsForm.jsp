<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>

<head>
<title><c:out value="${roleBean.mdaTitle}"/> Summary</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="styles/epayroll.css" type="text/css"/>
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
					<c:out value="${roleBean.businessName}"/> - <br>
					Active <c:out value="${roleBean.mdaTitle}"/>s</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainBody" id="mainBody">
				
				
				<div class="reportDescText">
					<c:out value="${roleBean.mdaTitle}"/> Overview
					<br />
				</div>
				<br/>
				<span class="reportTopPrintLinkExtended">
								<a href="${appContext}/mdapSummaryDetailsExcel.do" title='View/Export to Microsoft Excel'>
				View in Excel </a><br />
                </span>
				<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
					
					<tr>
					
						<td class="reportFormControlsSpacing"></td>
					</tr>
					<tr>
						<td>
						<h3><c:out value="${roleBean.mdaTitle}"/> Summary </h3>
						<br />
						<table class="report" cellspacing="0" cellpadding="0">
							<tr class="reportOdd header">
								<td class="tableCell" align="center" valign="top">
								S/No.</td>
								<td class="tableCell" align="center" valign="top">
								<c:out value="${roleBean.mdaTitle}"/></td>
								<td class="tableCell" align="center" valign="top">
								Code Name</td>
								<c:if test="${roleBean.civilService}">
								<td class="tableCell" align="center" valign="top">
								Type</td>
								</c:if>
								<td class="tableCell" align="center" valign="top">
								Staff Strength</td>
								<td class="tableCell" align="center" valign="top">
								Female Staff</td>
								<td class="tableCell" align="center" valign="top">
								Male Staff</td>	
								<td class="tableCell" align="center" valign="top">
								Female Staff &nbsp;(&#37;)</td>
								<td class="tableCell" align="center" valign="top">
								Male Staff&nbsp;(&#37;)</td>								
							</tr>
							<c:forEach items="${miniBean.mpbaMiniBeanList}" var="wBean" varStatus="gridRow">
							<tr class="${wBean.displayStyle}">
								<td class="tableCell" valign="top"><c:out value="${gridRow.index + 1}"/></td>
								<td class="tableCell" align="left" valign="top"><a href='${appContext}/empByMDAPDetails.do?oid=${wBean.id}' title='View ${roleBean.staffTypeName} in ${wBean.name}' target="_blank" onclick="popup(this.href, 'Employees');return false;"><c:out value="${wBean.name}"/></a></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.codeName}"/></td>
								<c:if test="${roleBean.civilService}">
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.type}"/></td>
								</c:if>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.staffStrength}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.noOfFemales}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.noOfMales}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.femalePercentageStr}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${wBean.malePercentageStr}"/></td>
							</tr>
							</c:forEach>							
							<tr class="reportEven footer">
								<td class="tableCell" valign="top">&nbsp;</td>
								<td class="tableCell" align="center" valign="top">Totals</td>
								<td class="tableCell" align="right">&nbsp;</td>
								<c:if test="${roleBean.civilService}">
								  <td class="tableCell" align="right">&nbsp;</td>
								</c:if>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalStaffStrength}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalNoOfFemales}"/></td>
								<td class="tableCell" align="right" valign="top"><c:out value="${miniBean.totalNoOfMales}"/></td>
								<td class="tableCell" align="right"><c:out value="${miniBean.totalFemalePercentage}"/></td>
								<td class="tableCell" align="right"><c:out value="${miniBean.totalMalePercentage}"/></td>
							</tr>
						</table>
						</td>
					</tr>
					
					
                  </table>
				<div class="reportBottomPrintLink">
				<a href="${appContext}/mdapSummaryDetailsExcel.do" title='View/Export to Microsoft Excel'>
				View in Excel </a><br/>
				</div>
				
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

	</html>
	
