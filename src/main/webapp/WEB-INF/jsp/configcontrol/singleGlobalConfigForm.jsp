<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> View Global Percentage Payment  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
 <link rel="stylesheet" href="css/screen.css" type="text/css"/>
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.4.4.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>

<script language="JavaScript">
    <!--
	 function toggleIt(which,formSwitch,formStatus,fieldInd){

       var input = document.getElementById(formSwitch);



        var outputText = document.getElementById(formStatus);


            if(which.checked) {
                outputText.innerHTML = "Apply";
                $("#"+fieldInd).val("1");

            } else {
                outputText.innerHTML = "Ignore";
                $("#"+fieldInd).val("0");
            }

        }
     // -->
    </script>
</head>


<body class="main">
<script type="text/JavaScript" src="scripts/jacs.js"></script>
<form:form modelAttribute="miniBean">
<c:set value="${displayList}" var="dispBean" scope="request"/>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

					<tr>
						<td colspan="2">
								<div class="title"> Configure Percentage Payment </div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						    * = required<br/><br/>
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
                            <table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
                                <tr align="left">
                                    <td class="activeTH">Percentage Payment Details</td>
                                </tr>
                                <tr>
                                    <td class="activeTD">
                                        <table>
                                            <tr>
                                             <td width="5%" align="left">Name*</td>
                                             <td> <form:input path="name" width="25%" size="10" maxlength="30" disabled="true" /> </td>
                                             </tr>
                                            <tr>
                                             <td width="5%" align="left">Start Date</td>
                                             <td width="25%"><c:out value="${miniBean.startDateStr}"/></td>
                                             </tr>

                                             <tr>
                                                <td width="5%" align="left">End Dates</td>
                                                <td width="25%"><c:out value="${miniBean.endDateStr}"/></td>
                                             </tr>

                                             <tr>
                                                <td width="5%" align="left" nowrap>Percentage Application Mode*</td>
                                                <td width="25%"><c:out value="${miniBean.applicationModeStr}"/></td>
                                             </tr>

                                             <c:if test="${miniBean.globalApply}">
                                                <tr>
                                                     <td width="5%" align="left" nowrap>Global Value</td>
                                                     <td>
                                                        <form:input path="globalPercentStr" size="4" maxlength="5" disabled="true"/>%
                                                     </td>
                                                </tr>
                                             </c:if>
                                             <c:if test="${miniBean.effectOnSuspension}">
                                                 <tr >
                                                  <td width="5%" title="${roleBean.staffTypeName} on Suspension that collect a percentage ">Effect On Suspensions*</td>
                                                  <td>

                                                             <form:input path="effectOnSuspensionBind" id="effectOnSuspensionBindInd"  style="display:none"/>
                                                             <label class="toggle">
                                                                <input id="effectOnSuspensionSwitch" name="effectOnSuspensionSwitch" type="checkbox" onClick="toggleIt(this,'effectOnSuspensionSwitch','effectOnSuspensionStatus','effectOnSuspensionBindInd')" title="Setting  to 'Apply' will apply Percentage, while setting to 'Ignore' will not apply Percentage ." disabled="true">
                                                                 <span class="roundbutton"></span>
                                                              </label>
                                                              <span id="effectOnSuspensionStatus">Ignore</span>
                                                    </td>

                                                </tr>
                                             </c:if>
                                             <c:if test="${miniBean.effectOnLoan}">
                                                <tr >
                                                  <td width="5%" title="${roleBean.staffTypeName} on Loan (Apply or Ignore) ">Effect On Loans*</td>
                                                  <td align="left">

                                                             <form:input path="loanEffectBind" id="loanEffectBindInd"  style="display:none"/>
                                                             <label class="toggle">
                                                                <input id="loanEffectSwitch" name="loanEffectSwitch" type="checkbox" onClick="toggleIt(this,'loanEffectSwitch','loanEffectStatus','loanEffectBindInd')" title="Setting  to 'Apply' will apply Percentage, while setting to 'Ignore' will not apply Percentage ." disabled="true">
                                                                 <span class="roundbutton"></span>
                                                              </label>
                                                              <span id="loanEffectStatus">Ignore</span>
                                                  </td>
                                                </tr>
                                             </c:if>
                                             <c:if test="${miniBean.effectOnDeduction}">
                                                <tr >
                                                  <td width="5%" title="${roleBean.staffTypeName} with Deductions (Apply or Ignore) ">Effect On Deductions*</td>
                                                  <td align="left">

                                                             <form:input path="deductionEffectInd" id="deductionEffectBindInd"  style="display:none"/>
                                                             <label class="toggle">
                                                                <input id="deductionEffectSwitch" name="deductionEffectSwitch" type="checkbox" onClick="toggleIt(this,'deductionEffectSwitch','deductionEffectStatus','deductionEffectBindInd')" title="Setting  to 'Apply' will apply Percentage, while setting to 'Ignore' will not apply Percentage ." disabled="true">
                                                                 <span class="roundbutton"></span>
                                                              </label>
                                                              <span id="deductionEffectStatus">Ignore</span>
                                                  </td>
                                                </tr>
                                             </c:if>
                                             <c:if test="${miniBean.effectOnSpecAllow}">
                                                <tr >
                                                  <td width="5%"  title="${roleBean.staffTypeName} with Special Allowances (Apply or Ignore) ">Effect On Special Allowance*</td>
                                                  <td align="left">

                                                             <form:input path="specAllowEffectInd" id="specAllowEffectBindInd"  style="display:none"/>
                                                             <label class="toggle">
                                                                <input id="specAllowEffectSwitch" name="specAllowEffectSwitch" type="checkbox" onClick="toggleIt(this,'specAllowEffectSwitch','specAllowEffectStatus','specAllowEffectBindInd')" title="Setting  to 'Apply' will apply Percentage, while setting to 'Ignore' will not apply Percentage ." disabled="true">
                                                                 <span class="roundbutton"></span>
                                                              </label>
                                                              <span id="specAllowEffectStatus">Ignore</span>
                                                  </td>
                                                </tr>
                                             </c:if>
                                             <c:if test="${miniBean.effectOnTerm}">
                                                <tr>
                                                  <td width="5%" title="For Terminations ( ${roleBean.staffTypeName} that will be paid by days )">Effect On Terminations*</td>
                                                  <td align="left">

                                                             <form:input path="effectOnTermBind" id="effectOnTermBindInd"  style="display:none"/>
                                                             <label class="toggle">
                                                                <input id="effectOnTermSwitch" name="effectOnTermSwitch" type="checkbox" onClick="toggleIt(this,'effectOnTermSwitch','effectOnTermStatus','effectOnTermBindInd')" title="Setting  to 'Apply' will apply Percentage, while setting to 'Ignore' will not apply Percentage ." disabled="true">
                                                                 <span class="roundbutton"></span>
                                                              </label>
                                                              <span id="effectOnTermStatus">Ignore</span>
                                                  </td>
                                                  <td align="left" title="The least number of days to apply Percentage">Least Number Of Days &nbsp; <c:out value="${miniBean.leastNoOfDays}"/> Days
                                                  </td>
                                                </tr>
                                             </c:if>

                                             <tr>
                                                        <td width="5%" >Created By :</td>
                                                        <td width="25%" colspan="2"><c:out value="${miniBean.creator.actualUserName}"/></td>
                                             </tr>
                                             <tr>
                                                        <td width="5%" align="left">Created Date :</td>
                                                        <td width="25%" align="left" colspan="2"><c:out value="${miniBean.createdDateStr}"/></td>
                                             </tr>
                                             <c:if test="${miniBean.approved}">
                                                    <tr>
                                                        <td width="5%" >Approved By :</td>
                                                        <td width="25%" colspan="2"><c:out value="${miniBean.approver.actualUserName}"/></td>
                                                    </tr>
                                                    <tr>
                                                        <td width="5%" align="left">Approval Date :</td>
                                                        <td width="25%" align="left" colspan="2"><c:out value="${miniBean.approvalDateStr}"/></td>
                                                    </tr>
                                             </c:if>
                                             <c:if test="${miniBean.rejected}">
                                                    <tr>
                                                        <td width="5%" align="left">Rejected By :</td>
                                                        <td width="25%" align="left" colspan="2"><c:out value="${miniBean.approver.actualUserName}"/></td>

                                                    </tr>
                                                    <tr>
                                                        <td width="5%" align="left">Rejected Date :</td>
                                                        <td width="25%" align="left" colspan="2"><c:out value="${miniBean.approvalDateStr}"/></td>
                                                    </tr>
                                             </c:if>
                                             <c:if test="${miniBean.deactivated}">
                                                    <tr>
                                                        <td width="5%" align="left">Deactivated By :</td>
                                                        <td width="25%" align="left" colspan="2"><c:out value="${miniBean.deactivatedBy.actualUserName}"/></td>

                                                    </tr>
                                                    <tr>
                                                        <td width="5%" align="left">Deactivation Date :</td>
                                                        <td width="25%" align="left" colspan="2"><c:out value="${miniBean.deactivationDateStr}"/></td>
                                                    </tr>
                                             </c:if>
                                        </table>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
			    <c:if test="${displayList.listSize gt 0}">
					<table>
					    <tr>
                           <td>
                              &nbsp;
                           </td>
                        </tr>
                        <tr>
                           <td >
											<p class="label">Pay Percentage Details</p>
											<display:table name="dispBean" class="register4" export="false" sort="page" defaultsort="1" requestURI="${appContext}/configureGlobalPercent.do">
                                                <display:column property="name" title="Name"></display:column>
                                                <display:column property="salaryTypeName" title="Pay Group"></display:column>
                                                <display:column property="fromLevel" title="From Level"></display:column>
                                                <display:column property="toLevel" title="To Level"></display:column>
                                                <display:column property="payPercentageStr" title="Application %age"></display:column>
                                                <display:column property="noOfStaffs" title="No of ${roleBean.staffTypeName}" ></display:column>
                                                <display:setProperty name="paging.banner.placement" value="bottom" />
										    </display:table>
						   </td>
                        </tr>
                    </table>
                    <tr>
                        <td>
                                         &nbsp; <input type="image" name="_cancel" value="cancel" title="Close" src="images/cancel_h.png">
                                         <p>
                        </td>
                    </tr>
                </c:if>
            </td>
        </tr>
        <tr>
        			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
       </tr>
	</table>
	<!-- <div id="pdfReportLink" class="reportBottomPrintLink">
        										<a href="${appContext}/percentPaymentFormExcelReport.do?">
                                                    Download Excel report </a>
        										<br />
    </div> -->

	</form:form>
	<Script language="JavaScript">
     $(document).ready(function(){
            if($("#effectOnSuspensionBindInd").val() === "true"){
               $('input[name=effectOnSuspensionSwitch]').attr('checked', true);
                      $("#effectOnSuspensionStatus").html("Apply");
           }
           if($("#loanEffectBindInd").val() === "true"){
                      $('input[name=loanEffectSwitch]').attr('checked', true);
                      $("#loadEffectStatus").html("Apply");
                  }
           if($("#effectOnTermBindInd").val() === "true"){
                      $('input[name=effectOnTermSwitch]').attr('checked', true);
                      $("#effectOnTermStatus").html("Apply");
                  }
           if($("#deductionEffectBindInd").val() === "true"){
                      $('input[name=deductionEffectSwitch]').attr('checked', true);
                      $("#deductionEffectStatus").html("Apply");
                  }
           if($("#specAllowEffectBindInd").val() === "true"){
                      $('input[name=specAllowEffectSwitch]').attr('checked', true);
                      $("#specAllowEffectStatus").html("Apply");
                  }

           });
    </Script>
</body>

</html>

