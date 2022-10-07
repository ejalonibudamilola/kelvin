<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title>  <c:out value="${pageTitle}"/> </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
 <link rel="stylesheet" href="css/screen.css" type="text/css"/>
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.4.4.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>

<script>
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
  <script type="text/javaScript" src="scripts/jacs.js"></script>
</head>


<body class="main">
<form:form modelAttribute="miniBean">
<c:set value="${displayList}" var="dispBean" scope="request"/>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

					<tr>
						<td colspan="2">
								<div class="title"> Approve Configured Percentage Payment </div>
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
						    <c:if test="${miniBean.showErrorRow}">
						        <div>
                                     <ul>
                                        <li>
                                          	<c:out value="${miniBean.mode}"/>
                                        </li>
                                     </ul>
                                </div>
                            </c:if>
                            <c:if test="${empty miniBean.mode}">
                                <table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
                                    <tr align="left">
                                        <td class="activeTH">Percentage Payment Details</td>
                                    </tr>
                                    <tr>
                                        <td class="activeTD">
                                            <table border="0" cellspacing="0" cellpadding="0">
                                                <tr>
                                                    <td style="padding-bottom:3px" width="5%" valign="top">Name*</td>
                                                    <td style="padding-bottom:3px"> <form:input path="name" size="10" maxlength="30" disabled="${miniBean.deactivation}" readonly="true"/></td>
                                                </tr>
                                                <tr>
                                                    <td style="padding-bottom:3px" width="5%" valign="top">Start Date</td>
                                                    <td style="padding-bottom:3px" width="25%"><form:input path="startDate" disabled="${miniBean.deactivation}" readonly="true"/>
                                                        <!--<img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('startDate'),event);">-->
                                                    </td>
                                                     <!--<td width="25%">End Dates<form:input path="endDate" disabled="${miniBean.deactivation}"/>
                                                        <img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('endDate'),event);">
                                                    </td>-->
                                                </tr>
                                                <tr>
                                                    <td style="padding-bottom:3px" width="5%" valign="top">End Dates</td>
                                                    <td style="padding-bottom:3px" width="25%"><form:input path="endDate" disabled="${miniBean.deactivation}" readonly="true"/>
                                                        <!--<img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('startDate'),event);">-->
                                                    </td>
                                                <tr>
                                                    <td style="padding-bottom:4px" width="5%" valign="top" nowrap>Percentage Application Mode*</td>
                                                    <td style="padding-bottom:3px"  width="25%" align="left"><c:out value="${miniBean.applicationModeStr}"/></td>
                                                </tr>
                                                <c:if test="${miniBean.globalApply}">
                                                    <tr>
                                                         <td style="padding-bottom:3px" width="5%" valign="top" nowrap>Global Value*</td>
                                                         <td style="padding-bottom:3px"  align="left">
                                                            <form:input path="globalPercentStr" size="4" maxlength="5" disabled="true"/>%
                                                         </td>
                                                    </tr>
                                                </c:if>
                                                <c:if test="${miniBean.effectOnSuspension}">
                                                     <tr>
                                                        <td width="5%" valign="top" title="${roleBean.staffTypeName} on Suspension that collect a percentage ">Effect On Suspensions*</td>
                                                        <td align="left">
                                                           <form:input path="effectOnSuspensionBind" id="effectOnSuspensionBindInd"  style="display:none"/>
                                                           <label class="toggle">
                                                              <input id="effectOnSuspensionSwitch" name="effectOnSuspensionSwitch" type="checkbox" onClick="toggleIt(this,'effectOnSuspensionSwitch','effectOnSuspensionStatus','effectOnSuspensionBindInd')" title="Setting  to 'Apply' will apply Percentage, while setting to 'Ignore' will not apply Percentage ." disabled="${saved}">
                                                              <span class="roundbutton"></span>
                                                           </label>
                                                           <span id="effectOnSuspensionStatus">Ignore</span>
                                                        </td>
                                                     </tr>
                                                </c:if>
                                                <c:if test="${miniBean.effectOnLoan}">
                                                    <tr>
                                                        <td width="5%" valign="top" title="${roleBean.staffTypeName} on Loan (Apply or Ignore) ">Effect On Loans*</td>
                                                        <td align="left">
                                                            <form:input path="loanEffectBind" id="loanEffectBindInd"  style="display:none"/>
                                                            <label class="toggle">
                                                                <input id="loanEffectSwitch" name="loanEffectSwitch" type="checkbox" onClick="toggleIt(this,'loanEffectSwitch','loanEffectStatus','loanEffectBindInd')" title="Setting  to 'Apply' will apply Percentage, while setting to 'Ignore' will not apply Percentage ." disabled="${saved}">
                                                                <span class="roundbutton"></span>
                                                            </label>
                                                            <span id="loanEffectStatus">Ignore</span>
                                                        </td>
                                                    </tr>
                                                </c:if>
                                                <c:if test="${miniBean.effectOnDeduction}">
                                                    <tr>
                                                        <td width="5%" valign="top" title="${roleBean.staffTypeName} with Deductions (Apply or Ignore) ">Effect On Deductions*</td>
                                                        <td align="left">
                                                            <form:input path="deductionEffectInd" id="deductionEffectBindInd"  style="display:none"/>
                                                            <label class="toggle">
                                                                <input id="deductionEffectSwitch" name="deductionEffectSwitch" type="checkbox" onClick="toggleIt(this,'deductionEffectSwitch','deductionEffectStatus','deductionEffectBindInd')" title="Setting  to 'Apply' will apply Percentage, while setting to 'Ignore' will not apply Percentage ." disabled="${saved}">
                                                                <span class="roundbutton"></span>
                                                            </label>
                                                            <span id="deductionEffectStatus">Ignore</span>
                                                        </td>
                                                    </tr>
                                                </c:if>
                                                <c:if test="${miniBean.effectOnSpecAllow}">
                                                    <tr>
                                                        <td width="5%" valign="top" title="${roleBean.staffTypeName} with Special Allowances (Apply or Ignore) ">Effect On Special Allowance*</td>
                                                        <td align="left">
                                                            <form:input path="specAllowEffectInd" id="specAllowEffectBindInd"  style="display:none"/>
                                                            <label class="toggle">
                                                                <input id="specAllowEffectSwitch" name="specAllowEffectSwitch" type="checkbox" onClick="toggleIt(this,'specAllowEffectSwitch','specAllowEffectStatus','specAllowEffectBindInd')" title="Setting  to 'Apply' will apply Percentage, while setting to 'Ignore' will not apply Percentage ." disabled="${saved}">
                                                                <span class="roundbutton"></span>
                                                            </label>
                                                            <span id="specAllowEffectStatus">Ignore</span>
                                                        </td>
                                                    </tr>
                                                </c:if>
                                                <c:if test="${miniBean.effectOnTerm}">
                                                    <tr>
                                                        <td width="5%" valign="top" title="For Terminations ( ${roleBean.staffTypeName} that will be paid by days )">Effect On Terminations*</td>
                                                        <td align="left">
                                                            <form:input path="effectOnTermBind" id="effectOnTermBindInd"  style="display:none"/>
                                                            <label class="toggle">
                                                                <input id="effectOnTermSwitch" name="effectOnTermSwitch" type="checkbox" onClick="toggleIt(this,'effectOnTermSwitch','effectOnTermStatus','effectOnTermBindInd')" title="Setting  to 'Apply' will apply Percentage, while setting to 'Ignore' will not apply Percentage .">
                                                                <span class="roundbutton"></span>
                                                            </label>
                                                            <span id="effectOnTermStatus">Ignore</span>
                                                        </td>
                                                        <td align="left" title="The least number of days to apply Percentage">Least Number Of Days &nbsp; <c:out value="${miniBean.leastNoOfDays}"/> Days</td>
                                                    </tr>
                                                </c:if>
                                                <c:if test="${miniBean.confirmation}">
                                                    <tr>
                                                        <td width="5%" valign="top">Generated Captcha :</td>
                                                        <td width="5%" valign="top" colspan="2"><c:out value="${miniBean.generatedCaptcha}"/></td>
                                                    </tr>
                                                    <tr>
                                                        <td width="25%" align="left">Entered Captcha :</td>
                                                        <td width="25%" align="left" colspan="2"><form:input path="enteredCaptcha" size="5" maxlength="6"/>
                                                        &nbsp;<font color="green">Case Insensitive</font></td>
                                                        <c:if test="${miniBean.captchaError}">
                                                          &nbsp;<font color="red"><b>Entered Captcha does not match!</b></font>
                                                        </c:if>
                                                    </tr>
                                                </c:if>
                                            </table>
                                        </td>
                                    </tr>
                                </table>
					            <br/>

					        </td>
					        </tr>
				        </table>

                        <c:if test="${displayList.listSize gt 0}">
                            <table>
                                <tr>
                                    <td>&nbsp;</td>

                                </tr>
                                <tr>
                                                <td valign="top">
                                                    <p class="label">Pay Percentage Details</p>
                                                        <display:table name="dispBean" class="register4" export="false" sort="page" defaultsort="1" requestURI="${appContext}/configureGlobalPercent.do">
                                                        <%--<display:column property="name" title="Name"></display:column>--%>
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
                        </c:if>
                        <c:choose>
                            <c:when test="${not miniBean.confirmation}">
                                <c:if test="${miniBean.deactivation}">
                                    <input type="image" name="_deactivate" value="deactivate" title="Deactivate" src="images/deactivate.png">
                                </c:if>
                                <c:if test="${not saved}">
                                  &nbsp; <input type="image" name="_approve" value="approve" title="Approve" src="images/approve.png">
                                  &nbsp; <input type="image" name="_reject" value="reject" title="Reject" src="images/reject_h.png">
                                </c:if>
                                <c:if test="${saved}">
                                    <input type="image" name="_cancel" value="cancel" title="Close" src="images/close.png">
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <input type="image" name="_confirm" value="confirm" title="Confirm Approval" src="images/confirm_h.png">
                                <input type="image" name="_cancel" value="cancel" title="Close" src="images/cancel_h.png">
                            </c:otherwise>
                        </c:choose>
            </c:if>
               <%-- <c:choose>
                    <c:when test="${not saved}">
                       <c:choose>
                          <c:when test="${miniBean.deactivation}">
                              &nbsp; <input type="image" name="_deactivate" value="deactivate" title="Deactivate" src="images/deactivate.png">
                          </c:when>
                          <c:otherwise>
                             &nbsp; <input type="image" name="_approve" value="approve" title="Approve" src="images/approve.png">
                             &nbsp; <input type="image" name="_reject" value="reject" title="Reject" src="images/reject_h.png">
                          </c:otherwise>
                       </c:choose>
                    </c:when>
                    <c:otherwise>
                       <c:if test="${not miniBean.confirmation}">
                            &nbsp; <input type="image" name="_cancel" value="cancel" title="Close" src="images/cancel_h.png">
                       </c:if>
                       <c:if test="${miniBean.confirmation}">
                          &nbsp; <input type="image" name="_confirm" value="confirm" title="Confirm Approval" src="images/confirm_h.png">
                       </c:if>
                    </c:otherwise>
               </c:choose>--%>
		        <!-- <div id="pdfReportLink" class="reportBottomPrintLink">
        										<a href="${appContext}/percentPaymentFormExcelReport.do?">
                                                    Download Excel report </a>
        										<br />
        	    </div> -->
		    </td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
</form:form>
	<script>
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
    </script>
</body>

</html>

