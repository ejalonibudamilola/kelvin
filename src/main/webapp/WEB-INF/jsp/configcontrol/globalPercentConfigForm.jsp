<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Configure Global Percentage Payment  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
<link rel="stylesheet" href="<c:url value="/styles/epayroll.css"/>" type="text/css"/>
<link rel="stylesheet" href="css/screen.css" type="text/css"/>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>

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

    <style>
    .globalTbl thead{
        background-color: #EBF6FD;
    }
    .globalTbl tbody{
        background-color : white
    }
    </style>
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
				<table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" style="margin-bottom:1%">
					<tr align="left">
						<td class="activeTH">Configuration Details</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="0">
							    <tr>
                            	 <td style="padding-top:5px" width="5%" align="right">Name* : </td>
                            	  <td style="padding-top:5px" width="25%"> <form:input path="name" size="20" maxlength="30" /> </td>
                            	  </tr>

								<tr>
								 <td style="padding-top:5px" width="5%" align="right">Start Date* : </td>

								<td style="padding-top:5px" width="25%"><form:input path="startDate"/>
                                    <img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('startDate'),event);">
                                  </td>
                                </tr>
                                <tr>
                                <td style="padding-top:5px" width="5%" align="right">End Dates* : </td>
								<td style="padding-top:5px" width="25%"><form:input path="endDate" align = "left"/>
                                   <img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('endDate'),event);">
                                 </td>

					           </tr>

					            <tr>

					                <td width="15%" align="right"  nowrap>Percentage Application Mode* : </td>
									<td width="25%" align="left">
									<span id="globalSpan"><form:radiobutton path="applicationMode" value="2" id="globalRadio" onclick="if (this.checked) { document.getElementById('hiderow2').style.display = '';document.getElementById('hiderow').style.display = 'none';document.getElementById('hiderow1').style.display = 'none'; document.getElementById('hiderow3').style.display = 'none'}"/>Global Value</span><br>
									<span id="paySpan"><form:radiobutton path="applicationMode" value="1" id="payRadio" onclick="if (this.checked) { document.getElementById('hiderow').style.display = ''; document.getElementById('hiderow1').style.display = '';document.getElementById('hiderow2').style.display = 'none'; document.getElementById('hiderow3').style.display = ''}"/>Pay Group</span><br>


									  </td>

					            </tr>
					            <c:if test="${not saved}">
					             <tr  id="hiderow" style="${miniBean.hideRow}">
					              <td width="15%" align="right">Pay Group* : </td>
					              <td align="left">
					              	<form:select path="salaryTypeId"  onchange="loadGradeLevels(this);">
										<form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<c:forEach items="${payGroupList}" var="pList">
												<form:option value="${pList.id}">${pList.name}</form:option>
												</c:forEach>
										</form:select>
					                </td>

					            </tr>
					            <tr  id="hiderow1" style="${miniBean.hideRow}">
					              <td style="padding-top:5px" width="15%" align="right">Level &amp; Step* : </td>
					              <td style="padding-top:5px" align="left">

										<form:select path="fromLevel" id="grade-level-control" cssClass="branchControls">
											 <form:option value="0">&lt;From Level&gt;</form:option>
											 <c:forEach items="${miniBean.fromLevelList}" var="fList">
											 <form:option value="${fList.level}">${fList.level}</form:option>
											 </c:forEach>
										</form:select>
										 <form:select path="toLevel" id="grade-to-level-control" cssClass="branchControls">
											 <form:option value="0">&lt;To Level&gt;</form:option>
											 <c:forEach items="${miniBean.toLevelList}" var="tList">
											 <form:option value="${tList.level}">${tList.level}</form:option>
											 </c:forEach>
										</form:select>
					                </td>
					            </tr>
					            <tr id="hiderow3" style="${miniBean.hideRow}">
					                <td style="padding-top:5px" width="15%" align="right">Pay Percentage Value :</td>
                                    <td style="padding-top:5px" align="left">
                                        <form:input path="percentageStr" size="4" maxlength="5" title="Enter Percentage to apply. Note value must not be greater than 100. Do not add '%'"/>%
                                     </td>
					            <tr>
					            </tr>
								 <tr  id="hiderow2" style="${miniBean.hideRow1}">
					              <td width="5%" align="right" title="Applies entered percentage to all ${roleBean.staffTypeName}">Global Value*</td>
					              <td align="left">
                                        <form:input path="globalPercentStr" size="4" maxlength="5" title="Enter Percentage to apply. Note value must not be greater than 100. Do not add '%'"/>%
					                </td>

					            </tr>
					            <c:if test="${miniBean.confirmation}">
					            <c:if test="${miniBean.fiftyPercentExists}">
								 <tr >
					              <td width="5%" valign="top" align="right" title="${roleBean.staffTypeName} on Suspension that collect a percentage ">Effect On Suspensions*</td>
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
								 <tr >
					              <td width="5%" valign="top" align="right" title="${roleBean.staffTypeName} on Loan (Apply or Ignore) ">Effect On Loans*</td>
					              <td align="left">

                                             <form:input path="loanEffectBind" id="loanEffectBindInd"  style="display:none"/>
                                             <label class="toggle">
                                                <input id="loanEffectSwitch" name="loanEffectSwitch" type="checkbox" onClick="toggleIt(this,'loanEffectSwitch','loanEffectStatus','loanEffectBindInd')" title="Setting  to 'Apply' will apply Percentage, while setting to 'Ignore' will not apply Percentage ." disabled="${saved}">
                                                 <span class="roundbutton"></span>
                                              </label>
                                              <span id="loanEffectStatus">Ignore</span>
					                </td>

					            </tr>
								 <tr >
					              <td width="5%" valign="top" align="right" title="${roleBean.staffTypeName} with Deductions (Apply or Ignore) ">Effect On Deductions*</td>
					              <td align="left">

                                             <form:input path="deductionEffectInd" id="deductionEffectBindInd"  style="display:none"/>
                                             <label class="toggle">
                                                <input id="deductionEffectSwitch" name="deductionEffectSwitch" type="checkbox" onClick="toggleIt(this,'deductionEffectSwitch','deductionEffectStatus','deductionEffectBindInd')" title="Setting  to 'Apply' will apply Percentage, while setting to 'Ignore' will not apply Percentage ." disabled="${saved}">
                                                 <span class="roundbutton"></span>
                                              </label>
                                              <span id="deductionEffectStatus">Ignore</span>
					                </td>

					            </tr>
								 <tr >
					              <td width="5%" valign="top" align="right" title="${roleBean.staffTypeName} with Special Allowances (Apply or Ignore) ">Effect On Special Allowance*</td>
					              <td align="left">

                                             <form:input path="specAllowEffectInd" id="specAllowEffectBindInd"  style="display:none"/>
                                             <label class="toggle">
                                                <input id="specAllowEffectSwitch" name="specAllowEffectSwitch" type="checkbox" onClick="toggleIt(this,'specAllowEffectSwitch','specAllowEffectStatus','specAllowEffectBindInd')" title="Setting  to 'Apply' will apply Percentage, while setting to 'Ignore' will not apply Percentage ." disabled="${saved}">
                                                 <span class="roundbutton"></span>
                                              </label>
                                              <span id="specAllowEffectStatus">Ignore</span>
					                </td>

					            </tr>
   								 <tr>
   					              <td width="5%" valign="top" align="right" title="For Terminations ( ${roleBean.staffTypeName} that will be paid by days )">Effect On Terminations*</td>
   					              <td align="left">

                                             <form:input path="effectOnTermBind" id="effectOnTermBindInd"  style="display:none"/>
                                             <label class="toggle">
                                                <input id="effectOnTermSwitch" name="effectOnTermSwitch" type="checkbox" onClick="toggleIt(this,'effectOnTermSwitch','effectOnTermStatus','effectOnTermBindInd')" title="Setting  to 'Apply' will apply Percentage, while setting to 'Ignore' will not apply Percentage .">
                                                 <span class="roundbutton"></span>
                                              </label>
                                              <span id="effectOnTermStatus">Ignore</span>
   					                </td>
   					              </tr>
   					              <tr>
                                    <td width="5%" align="right" title="The least number of days to apply Percentage">Least Number Of Days</td>
                                    <td>
                                       <form:select path="leastNoOfDays" disabled="${saved}">
                                           	 <form:option value="0">&lt;Select&gt;</form:option>
                                           	 <c:forEach items="${noOfDaysList}" var="nList">
                                           	 <form:option value="${nList.currentOtherId}">${nList.currentOtherId}</form:option>
                                           	 </c:forEach>
                                       </form:select>
                                    </td>
   					              </tr>
                                </c:if>
                                </c:if>
								<td colspan="2" align="center" >
								<br>
                                <c:if test ="${action !='_done' && action != '_confirm' && action != '_close' && action !='' && action == '_add'}">
								   <input type="image" name="_add" value="add" title="Add Global Percent Configuration detail" src="images/add.png">
                                </c:if>

								</td>
							</tr>


							</table>

					       <br/>


				</table>
				<br/>

			<c:choose>
			    <c:when test="${not miniBean.globalApply}">
			        <c:if test="${displayList.listSize gt 0}">
                        <table>
					        <tr>
                                <td>
                                   &nbsp;
                                </td>

                            </tr>
                            <tr>
                               <td valign="top">
										<p class="label">Pay Percentage Details</p>
										<display:table name="dispBean" class="register4" export="false" sort="page" defaultsort="1" requestURI="${appContext}/configureGlobalPercent.do">
											<display:column property="salaryTypeName" title="Pay Group"></display:column>
											<display:column property="fromLevel" title="From Level"></display:column>
											<display:column property="toLevel" title="To Level"></display:column>
											<display:column property="payPercentageStr" title="Application %age"></display:column>
											<display:column property="noOfStaffs" title="No of ${roleBean.staffTypeName}" ></display:column>
											<c:if test="${not saved}">
											  <display:column property="remove" title="Remove" href="${appContext}/configureGlobalPercent.do" paramId="iid" paramProperty="entryIndex"></display:column>
											</c:if>
											 <display:setProperty name="paging.banner.placement" value="bottom" />
										</display:table>
							   </td>
                            </tr>
                        </table>
			        </c:if>
			    </c:when>
			    <c:otherwise>
			        <c:if test="${displayErrors !='block'}">
			            <table width="80%" class="register4 globalTbl">
			                <thead>
			                    <th>Name</th>
			                    <th>Start Date</th>
			                    <th>End Date</th>
			                    <th>Global Percentage Value</th>
			                </thead>
			                <tbody>
                                <td><c:out value="${miniBean.name}"/></td>
                                <td><c:out value="${miniBean.startDate}"/></td>
                                <td><c:out value="${miniBean.endDate}"/></td>
                                <td><c:out value="${miniBean.globalPercentStr}"/></td>
			                </tbody>
			            </table>
                    </c:if>
			    </c:otherwise>
			</c:choose>
                                    <tr><td>
                                    <c:choose>
                                    <c:when test="${action == ''}">
                                      &nbsp; <input id="addClick" type="image" name="_add" value="add" title="Add Ranged Deduction detail" src="images/add.png">
                                      &nbsp; <input type="image" name="_cancel" value="cancel" title="Close" src="images/cancel_h.png">
                                    </c:when>
                                    <c:otherwise>
                                      <c:if test="${not miniBean.confirmation && action != '_close'}">
                                         &nbsp; <input type="image" name="_done" value="done" title="Save" src="images/done_h.png">
                                         &nbsp; <input type="image" name="_cancel" value="cancel" title="Close" src="images/cancel_h.png">
                                      </c:if>
                                      <c:if test="${miniBean.confirmation && action != '_close'}">
                                         &nbsp; <input type="image" name="_confirm" value="confirm" title="Confirm Configuration" src="images/confirm_h.png">
                                         &nbsp; <input type="image" name="_cancel" value="cancel" title="Close" src="images/cancel_h.png">
                                      </c:if>
                                      <c:if test="${action == '_close'}">
                                         &nbsp; <input type="image" name="_cancel" value="cancel" title="Close" src="images/close.png">
                                      </c:if>
                                    </c:otherwise>
                                    </c:choose>

                                       <p>
                                      </td>
                                    </tr>

		</table>
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
	<script language="javaScript">
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

        $("#addClick").click(function(e){
           var isGlobal = $('#globalRadio').prop('checked');
           var isPay = $('#payRadio').prop('checked');
           console.log("is global is "+isGlobal);
           console.log("is pay is "+isPay);


           if( $('#globalRadio').is(':checked') ){
               $('#paySpan').css("display", "none");
               $('#globalSpan').css("display", "block");
           }
           else if( $('#payRadio').is(':checked') ){
               $('#globalSpan').css("display", "none");
               $('#paySpan').css("display", "block");
           }
           else{
               $('#globalSpan').css("display", "block");
               $('#paySpan').css("display", "block");
           }
        });



    </script>
</body>

</html>

