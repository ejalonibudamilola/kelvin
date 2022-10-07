<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
           <c:out value="${miniBean.tpsOrCpsName}"/> <c:out value="${roleBean.staffTypeName}"/>
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
    </head>
    <body class="main">
    <form:form modelAttribute="miniBean">
	<c:set value="${miniBean}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
           <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                  <c:out value="${miniBean.tpsOrCpsName}"/> <c:out value="${roleBean.staffTypeName}"/><br>
                                    
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                  <div id="topOfPageBoxedErrorMessage" style="display:${miniBean.displayErrors}">
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
                                    	<td class="activeTD">
                                    	P.F.A&nbsp;&nbsp;<form:select path="id">
														<form:option value="0">&lt;Select PFA&gt;</form:option>
														<c:forEach items="${pfaList}" var="uList">
														<form:option value="${uList.id}">${uList.name}</form:option>
														</c:forEach>
												</form:select>
												
                                    	</td>
                                    </tr> 
                                    <tr>
                                    	<td class="activeTD">
                                    	 <c:out value="${roleBean.mdaTitle}"/>&nbsp;<form:select path="mdaInd">
														<form:option value="0">&lt;Select&gt;</form:option>
													       <c:forEach items="${mdaList}" var="mList">
													      <form:option value="${mList.id}" title="${mList.codeName}">${mList.name}</form:option>
													     </c:forEach>
												</form:select>     
												
                                    	</td>
                                    </tr>
                                    <tr>
										<td class="activeTD">&nbsp;&nbsp;&nbsp;&nbsp;
									    <form:radiobutton path="tpsOrCps" value="1" title="Selecting this option will generate report for ${roleBean.staffTypeName} on Transition Pension Scheme (TPS)" />TPS <c:out value="${roleBean.staffTypeName}"/> <form:radiobutton path="tpsOrCps" value="2" title="Selecting this option will generate report for ${roleBean.staffTypeName} on Contributory Pension Scheme (CPS)"/> CPS <c:out value="${roleBean.staffTypeName}"/>
			
									</td>
									</tr>
                                    <tr>
										<td class="activeTD">&nbsp;&nbsp;&nbsp;&nbsp;
										<spring:bind path="useTpsCpsRule">
										<input type="hidden" name="_<c:out value="${status.expression}"/>">
										<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if> title="Checking this box will filter ONLY by TPS/CPS Rule irrespective of Pension Contribution."/>
										</spring:bind>
										<span class="required">Use TPS/CPS Rule</span>
			
									</td>
									</tr>
									<tr>
										<td class="activeTD">&nbsp;&nbsp;&nbsp;&nbsp;
										<spring:bind path="includeTerminated">
										<input type="hidden" name="_<c:out value="${status.expression}"/>">
										<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if> title="Checking this box allows the inclusion of Terminated Staffs."/>
										</spring:bind>
										<span class="required">Include Terminated Staff</span>
			
									</td>
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
											<p class="label">  ${miniBean.listSize}  <c:out value="${miniBean.tpsOrCpsName}"/> <c:out value="${roleBean.staffTypeName}"/> found</p>
											<display:table name="dispBean" class="register2" export="false" sort="page" defaultsort="1" requestURI="${appContext}/tpsReport.do">
											<display:column property="employeeId" title="${roleBean.staffTitle}" />
											<display:column property="name" title="${roleBean.staffTypeName} Name"></display:column>
											<display:column property="mdaDeptMap.mdaInfo.name" title="${roleBean.mdaTitle}"></display:column>
											<display:column property="hireDateStr" title="Hire Date"></display:column>
											<display:column property="expDateOfRetirementStr" title="Expected Date of Retirement"></display:column>
											<display:setProperty name="paging.banner.placement" value="bottom" />
										</display:table>
										</td>
                                    </tr>
                                    <tr>
                                        
                                    </tr>
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                        <input type="image" name="_cancel" value="cancel" title="Cancel" src="images/close.png">
                                        </td>
                                    </tr>
                                </table>
                                <br>
                                <c:if test="${not miniBean.errorRecord}">
                                	[ * ] -- Denotes Suspended Employee<br>
                                	[ ** ] -- Denotes Terminated Employee
                                </c:if>
				<br>
				<table>
				<%-- <tr>
				<td class="activeTD"><form:radiobutton path="reportType" value="1"  onclick="if (this.checked) { document.getElementById('excelReportLink').style.display = 'none';document.getElementById('pdfReportLink').style.display = '';}"/>PDF Report Links <form:radiobutton path="reportType" value="2" onclick="if (this.checked) { document.getElementById('excelReportLink').style.display = '';document.getElementById('pdfReportLink').style.display = 'none';}"/> Excel Report Links</td>
				</tr> --%>
				</table>
				
				<br>
				<a href="${appContext}/tpsReportExcel.do?rm=${miniBean.runMonth}&ry=${miniBean.runYear}&pid=${miniBean.id}&mdaid=${miniBean.mdaInd}&utcr=${miniBean.useTpsCpsRule}&intemp=${miniBean.includeTerminated}&tpscps=${miniBean.tpsOrCps}">
					Export <c:out value="${miniBean.tpsOrCpsName}"/> Employee Report in Excel&copy; </a>
					<br />
				
				
				
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
