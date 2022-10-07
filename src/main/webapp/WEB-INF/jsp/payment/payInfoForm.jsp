<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
	<head>
		<title><c:out value="${pageTitle}"/></title>
		<link rel="stylesheet" href=<c:url value="styles/omg.css"/> type="text/css" />
 		<link rel="stylesheet" href=<c:url value="styles/skye.css"/> type="text/css" /> 
 		<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<script language="javascript" src="scripts/helper.js"></script>

		<script>
			function showfields()
			{
				showmorepaytypes = document.forms[0].showmorepaytypes.value == 'true';
				
				setStyleByClass('TR','advancedpaytype','display', showmorepaytypes ? '' : 'none');
				
				document.getElementById('lesspaytypeslink').style.display = showmorepaytypes ? 'none' : '';
				
				document.getElementById('salaryfields').style.display='none';
				document.getElementById('hourlyfields').style.display='none';
				//document.getElementById('hourly2field').style.display='none';  // - yeah - another one
				document.getElementById('wagecheckbox15').disabled = false;
				document.getElementById('wagecheckbox8').disabled = false;
				document.getElementById('wagecheckbox9').disabled = false;
	
				if (document.getElementById('hourlyradio').checked)
				{
					//document.getElementById('hourly2field').style.display=''; // - isnt referececd in the page
					document.getElementById('hourlyfields').style.display='';
				}
				if (document.getElementById('salaryradio').checked)
				{
					document.getElementById('salaryfields').style.display='';
				}
				if (document.getElementById('commissiononlyradio').checked)
				{
					document.getElementById('wagecheckbox15').checked = true;
					document.getElementById('wagecheckbox15').disabled = true;
					document.getElementById('wagecheckbox8').checked = false;
					document.getElementById('wagecheckbox8').disabled = true;
					document.getElementById('wagecheckbox9').checked = false;
					document.getElementById('wagecheckbox9').disabled = true;
				}
				
				showrecurringheader = (document.forms[0].showrecurringheader.value == 'true') || showmorepaytypes;
				document.getElementById('recurringheader').style.display = showrecurringheader ? '' : 'none';
				
			}
			
			
		</script>
	</head>
<!--
<body marginheight="0" topmargin="0" vspace="0" marginwidth="0" leftmargin="0" hspace="0" style="margin:0">
-->
<body class="main">
<table class="main" width="70%" border="1"  bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">
  <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
				
  <tr>
    <td><div class="title"><p>Edit <c:out value="${namedEntity.name}" /> Pay Information</p>
    </div></td>
  </tr>
  <tr>
    <td valign="top" class="mainbody" id="mainbody">select the base pay information for this
    employee. then select any other pay types that apply to this employee.<br>
    click <b>show more pay types</b> to view additional options.<br>
    * = required<br/><br/> 
   <form:form modelAttribute="paymentInfo" onsubmit="return pc_form_editpay1_form_submit(this)">
      <input type="hidden" name="showmorepaytypes" value="false">
      <input type="hidden" name="wagecheckbox12" value="1">
      <input type="hidden" name="wagecheckbox13" value="1">
      <input type="hidden" name="showrecurringheader" value="false">
      <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
          <td>
           <div id="topOfPageBoxedErrorMessage" style="display:${paymentInfo.displayErrors}">
								 <spring:hasBindErrors name="paymentInfo">
         							 <ul>
            							<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            							</c:forEach>
         							</ul>
     							 </spring:hasBindErrors>
			</div>
          	<table class="formTable1" border="0" cellspacing="0" cellpadding="3" width="100%" align="left">
         
            <tr align="left">
              <td class="activeTH">pay information</td>
            </tr>
            <tr>
              <td id="firsttd_editpay1_form" class="activeTD"> <table width="100%">
                <tr>
                  <td nowrap><form:radiobutton path="salaryRef" tabindex="1" id="hourlyradio" value="0" onclick="showfields()" disabled="true"/>hourly</td>
                  <td rowspan="3"></td>
                  <td rowspan="3" align="left" width="100%" valign="top">
				   <table id="hourlyfields" style="display:none" width="100%">
                    <tr>
                      <td valign="middle" nowrap><span class="required">regular rate*</span></td>
                      <td align="left" valign="middle">₦</td>
                      <td align="left" width="100%"><form:input path="rate" size="8" tabindex="2" /> per hour </td>
                    </tr>
                  </table>
                  <table id="salaryfields" style="display:none" width="95%">
                    <tr>
                      <td nowrap>rate*&nbsp; ₦<form:input path="salary" size="10" tabindex="5" disabled="true"/>
                      per <form:select path="frequencyInstId">
                        <c:forEach items="${payFreq}" var="payFrequency">
						<form:option value="${payFrequency.id}">${payFrequency.name}</form:option>
						</c:forEach>
                      </form:select> <span class="note">(no commas)</span> </td>
                    </tr>
                    <tr>
                      <td nowrap>this rate represents* <form:input path="hoursWorkedPerDay" size="5" tabindex="7" maxlength="5" disabled="true"/> hours worked per day<br>
                      and* <form:input path="daysWorkedPerWeek" size="3" tabindex="8" maxlength="3" disabled="true"/> days worked per week. </td>
                    </tr>
                  </table>
                  </td>
                </tr>
                <tr>
                  <td nowrap><form:radiobutton path="salaryRef" tabindex="4" id="salaryradio" value="1" onclick="showfields()" />salary</td>
                </tr>
                <tr>
                  <td nowrap><form:radiobutton path="salaryRef" tabindex="9" id="commissiononlyradio" value="2" onclick="showfields()" disabled="true"/>commission only</td>
                </tr>
              </table>
              </td>
            </tr>
            <tr>
              <td class="activeTH" align="left">pay types</td>
            </tr>
            <tr>
              <td class="activeTD"><table>
                  <tr>
                  <td><spring:bind path="overtime">
                  <input type="hidden" name="_<c:out value="${status.expression}"/>">
                  <input type="checkbox" name="<c:out value="${status.expression}"/>" tabindex="10" id="wagecheckbox8" value="true" disabled="disabled" <c:if test="${status.value}">checked</c:if>/>
     				</spring:bind>
     			  </td>
                  <td width="30%"><span class="optional alt">Overtime pay</span></td>
                  <td nowrap>&nbsp; </td>
                  <td><div class="note"></div></td>
                </tr>
                <tr>
                  <td><spring:bind path="doubleOvertime">
                  <input type="hidden" name="_<c:out value="${status.expression}"/>">
                  <input type="checkbox" name="<c:out value="${status.expression}"/>" tabindex="11" id="wagecheckbox9" value="true" disabled="disabled" <c:if test="${status.value}">checked</c:if>/>
     				</spring:bind>
     			  </td>
                  <td width="30%" nowrap><span class="optional alt">double overtime pay</span></td>
                  <td nowrap>&nbsp; </td>
                  <td><div class="note"></div></td>
                </tr>
                <tr>
                  <td><spring:bind path="sickPay">
                  <input type="hidden" name="_<c:out value="${status.expression}"/>">
                  <input type="checkbox" name="<c:out value="${status.expression}"/>" tabindex="12" value="true" disabled="disabled" <c:if test="${status.value}">checked</c:if>/>
     				</spring:bind>
     			  </td>
                  <td width="30%"><span class="optional alt">sick pay</span></td>
                  <td nowrap>&nbsp; </td>
                  <td><div class="note"></div></td>
                </tr>
                <tr>
                  <td><spring:bind path="vacationPay">
                  <input type="hidden" name="_<c:out value="${status.expression}"/>">
                  <input type="checkbox" name="<c:out value="${status.expression}"/>" tabindex="13" value="true" disabled="disabled" <c:if test="${status.value}">checked</c:if>/>
     			  </spring:bind>
                  <td width="30%"><span class="optional alt">vacation pay</span></td>
                  <td nowrap>&nbsp; </td>
                  <td><div class="note"><p>can also be used for pto</p>
                  </div></td>
                </tr>
                <tr>
                  <td><spring:bind path="holidayPay">
                  <input type="hidden" name="_<c:out value="${status.expression}"/>">
                  <input type="checkbox" name="<c:out value="${status.expression}"/>" tabindex="14" id="wagecheckbox21" value="true" disabled="disabled" <c:if test="${status.value}">checked</c:if>/>
     			  </spring:bind>
                  <td width="30%"><span class="optional alt">holiday pay</span></td>
                  <td nowrap>&nbsp; </td>
                  <td><div class="note"></div></td>
                </tr>
                <tr>
                  <td><spring:bind path="bonus">
                  <input type="hidden" name="_<c:out value="${status.expression}"/>">
                  <input type="checkbox" name="<c:out value="${status.expression}"/>" tabindex="15" id="wagecheckbox14" value="true" disabled="disabled" <c:if test="${status.value}">checked</c:if>/>
     			  </spring:bind>
                  <td width="30%"><span class="optional alt">bonus</span></td>
                  <td nowrap>&nbsp; </td>
                  <td><div class="note"></div></td>
                </tr>
                <tr>
                  <td><spring:bind path="commission">
                  <input type="hidden" name="_<c:out value="${status.expression}"/>">
                  <input type="checkbox" name="<c:out value="${status.expression}"/>" tabindex="16" id="wagecheckbox15" value="true" disabled="disabled" <c:if test="${status.value}">checked</c:if>/>
     			  </spring:bind>
                  <td width="30%"><span class="optional alt">commission</span></td>
                  <td nowrap>&nbsp; </td>
                  <td><div class="note"></div></td>
                </tr>
                <tr id="recurringheader">
                  <td colspan="2"></td>
                  <td valign="middle"><span class="optional head">recurring<br/>amount (optional)</span> </td>
                  <td valign="middle"></td>
                </tr>
               <tr class="advancedpaytype">
                  <td><form:checkbox path="allowanceRef" tabindex="17" id="wagecheckbox16" value="${paymentInfo.allowanceRef}"/><c:if test="${paymentInfo.allowanceRef}">checked</c:if> </td>
                  <td width="30%"><span class="optional alt">allowance</span></td>
                  <td nowrap>₦<form:input path="allowance" size="8" tabindex="18" /> </td>
                  <td><div class="note"><p>taxable payments that are separate from regular wages.</p>
                  </div></td>
                </tr>
                <tr class="advancedpaytype">
                  <td><form:checkbox path="reimbursementRef" tabindex="20" id="wagecheckbox19" value="${paymentInfo.reimbursementRef}"/> <c:if test="${paymentInfo.reimbursementRef}">checked</c:if> </td>
                  <td width="30%"><span class="optional alt">reimbursement</span></td>
                  <td nowrap>₦<form:input path="reimbursement" size="8" tabindex="21" disabled="true"/> </td>
                  <td><div class="note"><p>nontaxable payments for general expenses, including limited or
                  irregular travel.</p>
                  </div></td>
                </tr>
                <tr class="advancedpaytype">
                  <td><form:checkbox path="perDiemRef" tabindex="23" id="wagecheckbox17" value="${paymentInfo.perDiemRef}" /><c:if test="${paymentInfo.perDiemRef}">checked</c:if> </td>
                  <td width="30%"><span class="optional alt">nontaxable per diem</span></td>
                  <td nowrap>₦<form:input path="perDiem" size="8" tabindex="24" disabled="true"/> </td>
                  <td><div class="note"><p>fixed and nontaxable daily allowance or payment to employees who
                  travel extensively.</p>
                  </div></td>
                </tr>
                <tr class="advancedpaytype">
                  <td><form:checkbox path="groupTermLifeInsuranceRef" tabindex="25" id="wagecheckbox22" value="${paymentInfo.groupTermLifeInsuranceRef}" /><c:if test="${paymentInfo.groupTermLifeInsuranceRef}">checked</c:if> </td>
                  <td width="30%"><span class="optional alt">group-term life insurance</span></td>
                  <td nowrap>₦<form:input path="groupTermLifeInsurance" size="8" tabindex="26" disabled="true"/> </td>
                  <td><div class="note"><p>coverage in excess of ₦50,000 per employee; subject to some
                  payroll taxes</p>
                  </div></td>
                </tr>
                <tr class="advancedpaytype">
                  <td><form:checkbox path="otherEarningsRef" tabindex="27" id="wagecheckbox30" value="${paymentInfo.otherEarningsRef}" /><c:if test="${paymentInfo.otherEarningsRef}">checked</c:if></td>
                  <td width="30%"><span class="optional alt">other earnings</span></td>
                  <td nowrap>₦<form:input path="otherEarnings" size="8" tabindex="28" disabled="true" /> </td>
                  <td><div class="note"><p>other earnings are taxable payments to an employee that are
                  separate from regular wages.</p>
                  </div></td>
                </tr>
                <tr class="advancedpaytype">
                  <td><form:checkbox path="otherEarnings2Ref" tabindex="29" id="wagecheckbox31" value="${paymentInfo.otherEarnings2Ref}" /><c:if test="${paymentInfo.otherEarnings2Ref}">checked</c:if> </td>
                  <td width="30%"><span class="optional alt">other earnings II</span></td>
                  <td nowrap>₦<form:input path="otherEarnings2" size="8" tabindex="30" disabled="true" /> </td>
                  <td><div class="note"></div></td>
                </tr>
 			  </table>
              <table id="lesspaytypeslink" style="display=none">
                <tr>
                  <td><a tabindex="49" href onclick="document.forms[0].showmorepaytypes.value = 'true'; showfields(); return false;">show
                  more pay types</a></td>
                </tr>
              </table>
              </td>
            </tr>
          </table>
          </td>
        </tr>
        <tr>
			<td class="buttonRow" align="right">
				<input type="image" name="submit" value="ok" alt="Ok" class="" src="images/ok_h.png">
				<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
			</td>
        </tr>
      </table>
    </form:form>
    <p> <script>showfields()</script></p></td>
  </tr>
</table>
</body>
</html>
