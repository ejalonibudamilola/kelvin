<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
           <c:out value="${roleBean.staffTypeName}" /> Contribution Page
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
    </head>
    <body class="main">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
    <form:form modelAttribute="miniBean">
	<c:set value="${miniBean}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <%@ include file="/WEB-INF/jsp/modal.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    <c:out value="${roleBean.staffTypeName}" /> Monthly Pension Contributions<br>
                                    
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                  <div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
										<spring:hasBindErrors name="miniBean">
										<ul>
											<c:forEach var="errMsgObj" items="${errors.allErrors}">
											<li>
											<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
											</li>
											</c:forEach>
										</ul>
										</spring:hasBindErrors>
									</div> 
									<table cellpadding="1" cellspacing="2">
									<tr>
                                        <td class="activeTD">
                                            
                                                    <span class="optional"> From </span> <form:input path="fromDate"/>
													<img src="images/calendar.png" id="fromDateId" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('fromDate'),event);"><span class="optional">&nbsp;To </span> <form:input path="toDate"/>
													<img src="images/calendar.png" id="toDateId" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('toDate'),event);">&nbsp;&nbsp;
                                                                                                
                                        </td>
                                       
                                    </tr> 
                                    <tr>
                                    	<td class="activeTD">
                                    	Filter By P.F.A&nbsp;&nbsp;<form:select path="id">
														<form:option value="0">&lt;Select&gt;</form:option>
														<c:forEach items="${pfaList}" var="uList">
														<form:option value="${uList.id}">${uList.name}</form:option>
														</c:forEach>
												</form:select>
												
                                    	</td>
                                    </tr> 
                                    <tr>
                                    	<td class="activeTD">
                                    	 Filter By Organization &nbsp;<form:select path="mdaInd">
														<form:option value="0">Select Organization</form:option>
													       <c:forEach items="${mdaList}" var="mList">
													      <form:option value="${mList.id}" title="${mList.name}">${mList.codeName}</form:option>
													     </c:forEach>
												</form:select>     
												
                                    	</td>
                                    </tr> 
                                    <tr>
                                      
                                       <td class="activeTD">
                                             <c:out value="${roleBean.staffTitle}"/> :&nbsp;<form:input path="ogNumber" size="9" maxlength="10"/>
                                        </td>
                                        
                                    </tr>
                                    <tr>

                                        <%--<td class="activeTD">
                                              Pension Pin Code :&nbsp;<form:input path="pensionPinCode" size="12" maxlength="25"/>
                                         </td>--%>

                                     </tr>
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                        <input type="image" name="_updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                        </td>
                                    </tr> 
                                    </table>                        
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    
                                    <tr>
                                        <td valign="top">
											<p class="label"><c:out value="${roleBean.staffTypeName}" /> Contributory Pension</p>
											<display:table name="dispBean" class="register2" export="false" sort="page" defaultsort="1" requestURI="${appContext}/pensionReport.do">
											<display:column property="employeeId" title="${roleBean.staffTitle}" />
											<display:column property="name" title="${roleBean.staffTypeName} Name"></display:column>
											<display:column property="deductionCode" title="P.F.A"></display:column>
											<display:column property="currentDeductionStr" title="Amount Deducted"></display:column>
											<display:column property="mdaDeptMap.mdaInfo.name" title="${roleBean.mdaTitle}"></display:column>
										</display:table>
										</td>
                                    </tr>
                                    <tr>
                                        
                                    </tr>
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                        <input type="image" name="_cancel" value="cancel" title="Close View" src="images/close.png">
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
					<a href="${appContext}/pensionReportByMdaExcel.do?sDate=${miniBean.fromDateStr}&eDate=${miniBean.toDateStr}&pid=${miniBean.id}&mdaid=${miniBean.mdaInd}&eid=${miniBean.empInstId}&rt=1">
					Contribution Summary By MDA Excel&copy; </a><span class="tabseparator">|</span>
					<a class="reportLink" href="${appContext}/pensionReportByPFAExcel.do?sDate=${miniBean.fromDateStr}&eDate=${miniBean.toDateStr}&pid=${miniBean.id}&mdaid=${miniBean.mdaInd}&eid=${miniBean.empInstId}">
					Contribution Summary By PFA Excel&copy; </a>
					<br />
					
				</div>
				<div id="pdfReportLink" class="reportBottomPrintLink" style='display:none'>
					<a class="reportLink" href="${appContext}/pensionReportByMdaExcel.do?sDate=${miniBean.fromDateStr}&eDate=${miniBean.toDateStr}&pid=${miniBean.id}&mdaid=${miniBean.mdaInd}&eid=${miniBean.empInstId}&rt=2">
					Contribution Summary PDF&copy; </a><br />
				</div>
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
        <script>
        	$(".reportLink").click(function(e){
               $('#reportModal').modal({
                  backdrop: 'static',
                  keyboard: false
               });
            });
        </script>
    </body>
</html>
