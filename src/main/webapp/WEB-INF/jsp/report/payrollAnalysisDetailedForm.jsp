<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
    <head>
        <title>
            Detailed Payroll Analysis Report
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
            <%@ include file="/WEB-INF/jsp/modal.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    <c:out value="${paystubSummary.companyName}"/><br>
                                    Detailed Payroll Analysis Report
                                    <c:if test="${paystubSummary.mdaInstId gt 0}">
                                     Displaying Results For <c:out value="${roleBean.mdaTitle}"/> :  <c:out value="${paystubSummary.mdaTitle}"/>
                                    </c:if>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                                              
                                <span class="reportTopPrintLinkExtended">
									<a href="${appContext}/payrollAnalysisByMdaExcel.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&mid=${paystubSummary.mdaInstId}&rt=1" target="">Detailed Payroll Analysis in Excel&copy;</a> <span class="tabseparator">|</span>
                                    <a href="${appContext}/payrollAnalysisExcel.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&mid=${paystubSummary.mdaInstId}&rt=1" target="">Payroll Analysis in Excel&copy;</a>&nbsp;
                                   
                               </span>
                                <table class="reportMain" cellpadding="2" cellspacing="2" width="30%">
                                    <tr>
				     <td class="activeTH"  colspan="2">Filter By</td>
				        <tr>
				           <td class="activeTD">
				              <span class="optional"> Month </span>&nbsp;
                                  <form:select path="runMonth">
	                               <form:option value="-1">Select Month</form:option>
							       <c:forEach items="${monthList}" var="mList">
							         <form:option value="${mList.id}">${mList.name}</form:option>
							      </c:forEach>
							      </form:select>
							      <span class="optional"> Year </span>
                                   <form:select path="runYear">
	                               <form:option value="0">Select Year</form:option>
							       <c:forEach items="${yearList}" var="yList">
							         <form:option value="${yList.id}">${yList.name}</form:option>
							      </c:forEach>
							      </form:select>
						   </td>

                           </tr>
                           <tr>
                              <td class="activeTD">
		                         <c:out value="${roleBean.mdaTitle}"/> :&nbsp;
                                <form:select path="mdaInstId">
								<form:option value="0">&lt;All <c:out value="${roleBean.mdaTitle}"/>&gt;</form:option>
								<c:forEach items="${mdaList}" var="mdas">
								<form:option value="${mdas.id}">${mdas.name}</form:option>
								</c:forEach>
								</form:select>
		                          </td>

							</tr>
                           <tr>
				                         <td class="buttonRow" align="right">
                                            <input type="image" name="_updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                            <input type="image" name="_close" value="cancel" title="Close View"  src="images/close.png">
				                            <br/>
				                         </td>
				                     </tr>
                                    <tr>
                                        <td class="reportFormControlsSpacing"></td>
                                    </tr>


                                    <tr style="${paystubSummary.showRow}">
                                    	
                                    		<display:table name="dispBean" id="row" class="register4" sort="page" defaultsort="1" requestURI="${appContext}/mdaPayrollAnalysisExcel.do">
												<display:caption>Detailed Payroll Analysis Report</display:caption>
												<display:column title="S/No."><c:out value="${row_rowNum}"/></display:column>
												<display:column property="employee.employeeId" title="${roleBean.staffTitle}"></display:column>
												<display:column property="name" title="${roleBean.staffTypeName} Name"></display:column>
												<display:column property="employee.salaryInfo.levelStepStr" title="Grade"></display:column>
												<c:choose>
												<c:when test="${roleBean.pensioner}">
                                                     <display:column property="monthlyPensionStr" title="Monthly Pension"></display:column>
                                                    <display:column property="totalPayStr" title="Gross Pay"></display:column>
                                                    <display:column property="allDedTotalStr" title="Tot. Ded."></display:column>
                                                     <display:column property="specialAllowanceStr" title="Tot. Allow."></display:column>
                                                      <display:column property="netPayStr" title="Payable Amount"></display:column>
                                                     <display:column property="mdaDeptMap.mdaInfo.name" title="TCO"></display:column>
												</c:when>
												<c:otherwise>
                                                    <display:column property="employee.salaryInfo.monthlyBasicSalaryStr" title="Basic"></display:column>
                                                    <display:column property="totalPayStr" title="Gross Pay"></display:column>
                                                    <display:column property="allDedTotalStr" title="Tot. Ded."></display:column>
                                                    <display:column property="freePayStr" title="Free pay"></display:column>
                                                    <display:column property="taxableIncomeStr" title="Taxable Income"></display:column>
                                                    <display:column property="monthlyReliefAmountStr" title="Relief"></display:column>
                                                    <display:column property="taxesPaidStr" title="PAYE"></display:column>
                                                    <display:column property="netPayStr" title="Net Pay"></display:column>
												</c:otherwise>
												</c:choose>

													<display:setProperty name="paging.banner.placement" value="bottom" />					
											</display:table>
                                    	
                                    </tr>

                                    <tr>
                                     	<td align="right">&nbsp;
                                        </td>
                                    </tr>
                                </table>
                                <table>

									<tr  style="${paystubSummary.showRow}">
									<td class="activeTD"><form:radiobutton path="reportType" value="1"  onclick="if (this.checked) { document.getElementById('excelReportLink').style.display = 'none';document.getElementById('pdfReportLink').style.display = '';}"/>PDF Report Links <form:radiobutton path="reportType" value="2" onclick="if (this.checked) { document.getElementById('excelReportLink').style.display = '';document.getElementById('pdfReportLink').style.display = 'none';}"/> Excel Report Links</td>
									</tr>

									</table>
									<br>
									<div id="excelReportLink" class="reportBottomPrintLink" style='display:none'>
										<a class="reportLink" href="${appContext}/payrollAnalysisByMdaExcel.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&mid=${paystubSummary.mdaInstId}&rt=1" target="">
										Payroll Analysis By <c:out value="${roleBean.mdaTitle}"/> Excel&copy; </a><span class="tabseparator">|</span>
										<a class="reportLink" href="${appContext}/payrollAnalysisExcel.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&mid=${paystubSummary.mdaInstId}&rt=1" target="">
										Payroll Analysis Excel&copy; </a>
										<c:if test="${roleBean.subeb or roleBean.civilService}">
                                            <span class="tabseparator">|</span>
                                            <a class="reportLink" href="${appContext}/SchoolPayrollAnalysisByMdaExcel.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&mid=0&&rt=1" target="">
                                            Payroll Analysis By Schools Excel&copy;</a>
                                            <span class="tabseparator">|</span>
                                            <a class="reportLink" href="${appContext}/TscPayrollAnalysisByMdaExcel.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}" target="">
                                            Schools Payroll Summary Excel&copy;</a>
										</c:if>
										<span class="tabseparator">|</span>
                                          <a class="reportLink" href="${appContext}/salarySummaryByOrg.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&rt=1">
                                          Salary Summary By <c:out value="${roleBean.mdaTitle}"/> Excel&copy;<br />
										
										
									</div>
									<div id="pdfReportLink" class="reportBottomPrintLink" style='display:none'>
										<a class="reportLink" href="${appContext}/payrollAnalysisByMdaExcel.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&mid=${paystubSummary.mdaInstId}&rt=2">
										Payroll Analysis By <c:out value="${roleBean.mdaTitle}"/> PDF&copy; </a><span class="tabseparator">|</span>
										<a class="reportLink" href="${appContext}/SchoolPayrollAnalysisByMdaExcel.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&mid=${paystubSummary.mdaInstId}&rt=2">
										Payroll Analysis By Schools PDF&copy;</a><span class="tabseparator">|</span>
										<a class="reportLink" href="${appContext}/payrollAnalysisExcel.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&mid=${paystubSummary.mdaInstId}&rt=2">
										Payroll Analysis For All <c:out value="${roleBean.staffTypeName}"/> PDF&copy;<span class="tabseparator">|</span>
										<a class="reportLink" href="${appContext}/salarySummaryByOrg.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&rt=2">
										Salary Summary By <c:out value="${roleBean.mdaTitle}"/> PDF&copy;<br />
										<br />
										
									</div><!--
                                <div class="reportBottomPrintLink">
                                    <a href="${appContext}/payrollAnalysisByMdaExcel.do?fd=${paystubSummary.fromDateAsString}&td=${paystubSummary.toDateAsString}" target="">Detailed Payroll Analysis in Excel&copy;</a> &nbsp;
                                    <a href="${appContext}/payrollAnalysis.do?fd=${paystubSummary.fromDateAsString}&td=${paystubSummary.toDateAsString}&bpv=t" target="">Payroll Analysis in Excel&copy;</a>&nbsp;
                                   </div><br>-->
                                
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
