<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
    <head>
        <title>
           <c:out value="${paystubSummary.companyName}"/> Employee Details
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
    </head>
    <body class="main">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
    <form:form modelAttribute="paystubSummary">
	<c:set value="${paystubSummary}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    <c:out value="${paystubSummary.companyName}"/><br>
                                    Employee Details for Period <br>
                                    <c:out value="${paystubSummary.name}"/>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                                              
                                <span>
									<a href="${appContext}/${paystubSummary.urlName}">&lt;&lt;-- Go Back</a>                                    
                               </span>
                               <p/>
                               <div class="reportDescText">
									This report displays the <c:out value="${roleBean.staffTypeName}"/> Payroll Summary of a particular  Grade Level and Paygroup.
									<br />
								</div>
								<p/>
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
                                            
                                                   <c:out value="${roleBean.mdaTitle}"/>&nbsp;<form:select path="mdaInstId">
														<form:option value="0">&lt;Select&gt;</form:option>
														<c:forEach items="${paystubSummary.mdaList}" var="desigList">
														<form:option value="${desigList.id}">${desigList.name}</form:option>
														</c:forEach>
												</form:select> 
												&nbsp;&nbsp;Pay Group&nbsp;&nbsp;<form:select path="salaryTypeInstId">
														<form:option value="0">&lt;Select Pay Group&gt;</form:option>
														<c:forEach items="${paystubSummary.salaryTypeList}" var="pgList">
														<form:option value="${pgList.id}">${pgList.name}</form:option>
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
										<td >
											&nbsp;         
			                              </td>
                                       
                                    </tr>
                                    
                                    <tr>
                                        <td class="reportFormControlsSpacing"></td>
                                    </tr>
                                    <tr>
                                    	
                                    		<display:table name="dispBean" id="row" class="register3" sort="page" defaultsort="1" requestURI="${appContext}/viewEmployeeTypeDetails.do">
												<display:caption><c:out value="${paystubSummary.companyName}"/> Staff Details</display:caption>
												<display:column title="S/No."><c:out value="${row_rowNum}"/></display:column>
												<display:column property="employee.employeeId" title="${roleBean.staffTitle}"></display:column>
												<display:column property="name" title="${roleBean.staffTypeName} Name"></display:column>
												<display:column property="salaryInfo.levelStepStr" title="Grade"></display:column>
												<display:column property="totalPayStr" title="Gross Pay"></display:column>
												<display:column property="allDedTotalStr" title="Tot. Ded."></display:column>
												<display:column property="taxesPaidStr" title="PAYE"></display:column>
												<display:column property="netPayStr" title="Net Pay"></display:column>	
													<display:setProperty name="paging.banner.placement" value="bottom" />					
											</display:table>
                                    	
                                    </tr>
                                    <tr>
                                     	<td align="right">&nbsp;
                                        </td>
                                    </tr>
                                    <tr>
				                     <td class="buttonRow" align="right">
				                       <input type="image" name="_cancel" value="cancel" title="Close Report" src="images/close.png">
				                     </td>
				                   </tr>
                                </table>
                                
                                <div class="reportBottomPrintLink">
                                    <a href="${appContext}/payrollSumDetailsByGLandPayGroupExcel.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&tc=${paystubSummary.typeCode}&mda=${paystubSummary.mdaCode}&stid=${paystubSummary.salaryTypeInstId}" target="_blank">View in Excel&copy;</a>                                    
								</div><br> 
                                
                            </td>
                            
                        </tr>
                        
                        <tr>
               				 <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
           				 </tr>
                    </table>
                </td>
            </tr>
            
        </table>
        </form:form>
    </body>
</html>
