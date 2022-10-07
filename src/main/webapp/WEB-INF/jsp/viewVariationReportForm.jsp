<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            Variation Report For <c:out value="${miniBean.monthAndYearStr}"/>
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="styles/epayroll.css" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
    </head>
    <body class="main">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
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
                                <c:choose>
                                	<c:when test="${miniBean.individual}">
                                		  View Variation Report For  - <br>
                                   			 <c:out value="${miniBean.employeeName}"/> during <c:out value="${miniBean.monthAndYearStr}"/>
                                    
                                	</c:when>
                                	<c:otherwise>
                                			  View All Variation Report For - <br>
                                   				<c:out value="${miniBean.monthAndYearStr}"/>
                                    
                                	
                                	</c:otherwise>
                                </c:choose>
                                  
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
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="90%">
                                    <tr>
                                        <td class="reportFormControls">
                                            Run Month:<form:select path="monthInd">
												<form:option value="0">&lt;make a selection&gt;</form:option>
												<c:forEach items="${monthList}" var="mList">
													<form:option value="${mList.id}">${mList.name}</form:option>
												</c:forEach>
											</form:select> &nbsp;Run Year 
											<c:out value="${miniBean.yearStr}"/>&nbsp;Filter By MDA
											<form:select path="mdaInd">
												<form:option value="0">&lt;Select MDA&gt;</form:option>
												<c:forEach items="${mdaList}" var="mdas">
													<form:option value="${mdas.generatedCaptcha}">${mdas.name}</form:option>
												</c:forEach>
											</form:select>
                                            
                                          </td>        
                                       
                                    </tr>
                                    <tr>
                                        <td valign="top">
											<p class="label">Variation Report For  <c:out value="${miniBean.monthAndYearStr}"/></p>
											<display:table name="dispBean" class="register3" export="false" sort="page" defaultsort="1" requestURI="${appContext}/variationReport.do">
											<display:column property="employee.employeeId" title="${roleBean.staffTitle}" media="html"  href="${appContext}/variationReport.do" paramId="eid" paramProperty="employee.id"></display:column>
											<display:column property="employee.employeeId" title="${roleBean.staffTitle}" media="excel"/>
											<display:column property="employee.displayName" title="${roleBean.staffTypeName} Name"></display:column>
											<display:column property="employee.salaryInfo.salaryScaleLevelAndStepStr" title="Pay Group : Level/Step"></display:column>
										    <display:column property="employee.assignedToObject" title="MDA"></display:column>
										    <display:column property="description" title="Change Description"></display:column>
										    <display:column property="oldValue" title="Old Value"></display:column>
										    <display:column property="newValue" title="New Value"></display:column>
										    <display:column property="login.actualUserName" title="Changed By"></display:column>
										    <display:column property="auditTime" title="Changed Date"></display:column>
											
										</display:table>
										</td>
                                    </tr>
                                    <tr>
                                    	<td>
                                    	  &nbsp;
                                    	</td>
                                    </tr>
                                    <tr>
                                        
                                     	<td>
                                     	<c:choose>
                                     	<c:when test="${miniBean.individual}">
                                          <a href='${appContext}/exportSingleVariationReport.do?rm=${miniBean.monthInd}&ry=${miniBean.yearStr}&eid=${miniBean.employeeId}' title="View All Records in Excel"><i>Export to Microsoft Excel&copy;</i></a>                                          
                                        </c:when>
                                        <c:otherwise>
                                        	<a href='${appContext}/exportVariationReport.do?rm=${miniBean.monthInd}&ry=${miniBean.yearStr}&mda=${miniBean.mdaInd}' title="View All Records in Excel"><i>Export to Microsoft Excel&copy;</i></a>                                          
                                        </c:otherwise>
                                        </c:choose>
                                        </td>
                                        
                                    </tr>
                                   
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                        <input type="image" name="_updateReport" value="updateReport" alt="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                        <input type="image" name="_cancel" value="cancel" alt="Cancel" src="images/close.png">
                                        </td>
                                    </tr>
                                </table>
                                
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
