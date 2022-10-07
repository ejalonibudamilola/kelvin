<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>


<html>
    <head>
        <title>
            Payroll Summary
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
		<link rel="stylesheet" href="styles/spinner.css" TYPE="text/css" />
    </head>
    <body class="main">
    <div class="loader"></div>
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
                                    Payroll Summary
                                </div>
                            </td>
                        </tr>
                        <tr>
                        	<td>
                        	<br>
                        	<br>

						<table>
                        <tr align="left">
                        <td class="activeTH" colspan="2">Filter By</td>

                        </tr>

                         <tr>

                         <td class="activeTD">
                           Run Month&nbsp;
                         </td>
                         <td class="activeTD">
                             <form:select path="runMonth">
                             <form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
                             <c:forEach items="${monthList}" var="mList">
                             <form:option value="${mList.id}">${mList.name}</form:option>
                             </c:forEach>
                             </form:select>
                         </td>
                         </tr>

                         <tr>
                         <td class="activeTD">
                             Run Year&nbsp;
                         </td>
                         <td class="activeTD">
                             <form:select path="runYear">
                             <form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
                             <c:forEach items="${yearList}" var="yList">
                             <form:option value="${yList.id}">${yList.id}</form:option>
                             </c:forEach>
                             </form:select>
                         </td>
                         </tr>


                         <tr>
                         <td class="activeTD">
                          Start GL&nbsp;
                         </td>
                         <td class="activeTD">
                          <form:select path="fromLevel">
                          <form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
                          <form:option value="1">1</form:option>
                          <form:option value="2">2</form:option>
                          <form:option value="3">3</form:option>
                          <form:option value="4">4</form:option>
                          <form:option value="5">5</form:option>
                          <form:option value="6">6</form:option>
                          <form:option value="7">7</form:option>
                          <form:option value="8">8</form:option>
                          <form:option value="9">9</form:option>
                          <form:option value="10">10</form:option>
                          <form:option value="11">11</form:option>
                          <form:option value="12">12</form:option>
                          <form:option value="13">13</form:option>
                          <form:option value="14">14</form:option>
                          <form:option value="15">15</form:option>
                          <form:option value="16">16</form:option>
                          <form:option value="17">17</form:option>
                          </form:select>
                          </td>
                          </tr>


                          <tr>
                         <td class="activeTD">
                          End GL&nbsp;
                         </td>
                         <td class="activeTD">
                          <form:select path="toLevel">
                          <form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
                          <form:option value="1">1</form:option>
                          <form:option value="2">2</form:option>
                          <form:option value="3">3</form:option>
                          <form:option value="4">4</form:option>
                          <form:option value="5">5</form:option>
                          <form:option value="6">6</form:option>
                          <form:option value="7">7</form:option>
                          <form:option value="8">8</form:option>
                          <form:option value="9">9</form:option>
                          <form:option value="10">10</form:option>
                          <form:option value="11">11</form:option>
                          <form:option value="12">12</form:option>
                          <form:option value="13">13</form:option>
                          <form:option value="14">14</form:option>
                          <form:option value="15">15</form:option>
                          <form:option value="16">16</form:option>
                          <form:option value="17">17</form:option>
                          </form:select>
                          </td>
                          </tr>

                         <tr>
                         <td class="activeTD">
                             Bank List&nbsp;
                         </td>
                         <td class="activeTD">
                             <form:select path="bankName">
                             <form:option value="">&lt;&nbsp;Select&nbsp;&gt;</form:option>
                             <c:forEach items="${bankList}" var="bList">
                             <form:option value="${bList.name}">${bList.name}</form:option>
                             </c:forEach>
                             </form:select>
                         </td>
                         </tr>

                         </table>
						 
                        	</td>
                       
                        </tr>
                        <tr>
                                     	<td class="buttonRow" align="right">
                                        &nbsp;&nbsp;&nbsp;<input type="image" name="_updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                        </td>
                                    </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                             
                               <br>
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">

                                    <tr style="${paystubSummary.showRow}">

                                    			 <display:table name="dispBean" id="paytblo" class="display table" sort="page" defaultsort="1" requestURI="${appContext}/paystubsReportForm.do">
												    <display:caption><c:out value="${paystubSummary.companyName}"/> Payroll Summary</display:caption>
													<display:column title="S/No."></display:column>
													<display:column property="employee.employeeId" title="${roleBean.staffTitle}"></display:column>
                                                    <display:column property="employee.displayName" title="${roleBean.staffTypeName} Name"></display:column>

                                                     <c:if test="${not roleBean.pensioner}">
                                                              <display:column property="salaryInfo.name" title="Pay Group"></display:column>
                                                              <display:column property="monthlyBasicStr" title="Monthly Basic"></display:column>
                                                               <display:column property="taxableIncomeStr" title="Taxable Income"></display:column>
                                                               <display:column property="monthlyReliefAmountStr" title="Relief"></display:column>
                                                               <display:column property="taxesPaidStr" title="Taxes Paid"></display:column>
                                                                <display:column property="totalPayStr" title="Gross Pay"></display:column>
                                                                 <display:column property="allDedTotalStr" title="Tot. Ded."></display:column>
                                                     </c:if>


                                                     <c:if test="${roleBean.pensioner}">
                                                      <display:column property="monthlyPensionStr" title="Monthly Pension"></display:column>
                                                      <display:column property="totalPayStr" title="Gross Pay"></display:column>
                                                        <display:column property="allDedTotalStr" title="Tot. Ded."></display:column>
                                                       <display:column property="specialAllowanceStr" title="Arrears"></display:column>
                                                     </c:if>
                                                     <display:column property="netPayStr" title="Net Pay"></display:column>

                                                     <display:setProperty name="paging.banner.placement" value="" />
                                                 </display:table>




                                    </tr>

                                </table>

                                <table style="margin-left:10px">
                                    <tr>
                                        <td class="activeTD"><form:radiobutton path="reportType" value="1"  onclick="if (this.checked) { document.getElementById('excelReportLink').style.display = 'none';document.getElementById('pdfReportLink').style.display = '';}"/>PDF Report Links <form:radiobutton path="reportType" value="2" onclick="if (this.checked) { document.getElementById('excelReportLink').style.display = '';document.getElementById('pdfReportLink').style.display = 'none';}"/> Excel Report Links</td>
                                    </tr>
                                </table>
                                <br>
                                <div id="excelReportLink" class="reportBottomPrintLink" style='display:none'>
                                    <a class="reportLink" href="${appContext}/OgunStateBankDetailExcel.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&bpv=t&rt=1&sGL=${paystubSummary.fromLevel}&eGL=${paystubSummary.toLevel}&bank=${paystubSummary.bankName}">
                                    Bank Schedule Summary Excel&copy; </a><span class="tabseparator">|</span>
                                    <!--<a class="reportLink" href="${appContext}/PayrollSummaryByBankBranchExcel.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&bid=1&rt=1" target="_blank">
                                    Bank Schedule Detailed Excel&copy;</a>-->
                                    <a class="reportLink" href="${appContext}/bankDetailedMda.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&rt=1&sGL=${paystubSummary.fromLevel}&eGL=${paystubSummary.toLevel}&bank=${paystubSummary.bankName}&fb=0">
                                                        Bank Schedule Detailed Excel&copy;</a>
                                    <c:choose>
                                        <c:when test="${roleBean.pensioner}">
                                            <span class="tabseparator">|</span>

                                             <a class="reportLink" href="${appContext}/bankDetailedMda.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&rt=1&sGL=${paystubSummary.fromLevel}&eGL=${paystubSummary.toLevel}&bank=${paystubSummary.bankName}&fb=1">
                                                Vouchers By Bank Excel&copy;</a>
                                        </c:when>
                                        <c:otherwise>

                                            <span class="tabseparator">|</span>
                                            <a class="reportLink" href="${appContext}/SchoolPayrollAnalysisByMdaExcel.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&sGL=${paystubSummary.fromLevel}&eGL=${paystubSummary.toLevel}&bank=${paystubSummary.bankName}">
                                            Detailed Schools Bank Schedule Excel&copy; </a>
                                            <span class="tabseparator">|</span>
                                            <a class="reportLink" href="${appContext}/payrollSummaryByLgaExcel.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&sGL=${paystubSummary.fromLevel}&eGL=${paystubSummary.toLevel}&bank=${paystubSummary.bankName}">
                                                Detailed Payroll By LGA Excel&copy;
                                            </a>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div id="pdfReportLink" class="reportBottomPrintLink" style='display:none'>
                                    <!--<a class="reportLink" href="${appContext}/BankScheduleSummary.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&rt=2">
                                    Bank Schedule Summary PDF&copy; </a>-->
                                    <a class="reportLink" href="${appContext}/OgunStateBankDetailExcel.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&bpv=t&rt=2&sGL=${paystubSummary.fromLevel}&eGL=${paystubSummary.toLevel}&bank=${paystubSummary.bankName}" target="_blank">
                                         Bank Schedule Summary PDF&copy; </a>
                                    <span class="tabseparator">|</span>
                                    <c:choose>
                                        <c:when test="${roleBean.pensioner}">
                                            <a class="reportLink" href="${appContext}/bankDetailedMda.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&rt=2&sGL=${paystubSummary.fromLevel}&eGL=${paystubSummary.toLevel}&bank=${paystubSummary.bankName}&fb=0">
                                             Vouchers By Bank Detailed PDF&copy;</a>

                                            </a><span class="tabseparator">|</span>
                                             <a class="reportLink" href="${appContext}/bankDetailedMda.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&rt=2&sGL=${paystubSummary.fromLevel}&eGL=${paystubSummary.toLevel}&bank=${paystubSummary.bankName}&fb=1">
                                            Vouchers By Bank PDF&copy;</a>
                                        </c:when>
                                        <c:otherwise>
                                          <a class="reportLink" href="${appContext}/bankDetailedMda.do?rm=${paystubSummary.runMonth}&ry=${paystubSummary.runYear}&rt=2&sGL=${paystubSummary.fromLevel}&eGL=${paystubSummary.toLevel}&bank=${paystubSummary.bankName}&fb=0">
                                              Bank Schedule Detailed PDF&copy;</a>
                                        </c:otherwise>
                                    </c:choose>
                                    <!--<span class="tabseparator">|</span>
                                    <a class="reportLink" href="${appContext}/bankDetailedTSC.do?sDate=${paystubSummary.fromDateAsString}">
                                    Detailed Schools Bank Schedule PDF&copy;</a>
                                    --><br />
                                </div>
				                <input style="padding-left:13px" type="image" name="_close" value="close" title="Close Window" class="" src="images/close.png">
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

         <script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
          <script type="text/javascript">
             		$(function() {

             			$("#paytblw").DataTable({
             			    "rowCallback": function (nRow, aData, iDisplayIndex) {
                                 var oSettings = this.fnSettings();
                                 $("td:first", nRow).html(oSettings._iDisplayStart+iDisplayIndex +1);
                                 return nRow;
                            },
             				"order" : [ [ 1, "asc" ] ],
             				//can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
             				//also properties higher up, take precedence over those below
             				"columnDefs":[
             					{"targets": 0, "orderable" : false},
             					{"targets": 5, "searchable" : false }
             					//{"targets": [0, 1], "orderable" : false }
             				]
             			});
             		});

                    $(function() {
             			$("#paytblo").DataTable({
             			    "rowCallback": function (nRow, aData, iDisplayIndex) {
                                 var oSettings = this.fnSettings();
                                 $("td:first", nRow).html(oSettings._iDisplayStart+iDisplayIndex +1);
                                 return nRow;
                            },
             				"order" : [ [ 1, "asc" ] ],
             				//can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
             				//also properties higher up, take precedence over those below
             				"columnDefs":[
             					{"targets": 0, "orderable" : false},
             					{"targets": 5, "searchable" : false }
             					//{"targets": [0, 1], "orderable" : false }
             				]
             			});
             		});

          window.onload = function exampleFunction() {
              $(".loader").hide();
          }

          $(".reportLink").click(function(e){
             $('#reportModal').modal({
                 backdrop: 'static',
                 keyboard: false
             });
          });
         </script>
    </body>
</html>
