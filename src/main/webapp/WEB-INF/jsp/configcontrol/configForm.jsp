<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> <c:out value="${roleBean.businessName}"/> IPPMS Configuration Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
<link rel="stylesheet" href="css/bootstrap-datetimepicker.min.css" type="text/css"/>
<script type="text/javascript" src="scripts/jacs.js"></script>
<script language="javascript">
    <!--
	 function toggleIt(which,formSwitch,formStatus,fieldInd){

       var input = document.getElementById(formSwitch);



        var outputText = document.getElementById(formStatus);


            if(which.checked) {
                outputText.innerHTML = "Yes";
                $("#"+fieldInd).val("1");

            } else {
                outputText.innerHTML = "No";
                $("#"+fieldInd).val("0");
            }

        }
     // -->
    </script>

</head>
<body class="main">


	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

					<tr>
						<td>
								<div class="title">Edit IPPMS Configuration Parameters</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="configBean">
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="configBean">
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
						<td class="activeTH">Edit IPPMS Configuration </td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
							  <c:choose>
							  	<c:when test="${saved}">
							  	<c:if test="${not roleBean.pensioner}">
							     <tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Max Length Of Service*</span></td>
									<td width="25%">
										<form:input path="serviceLength" size="3" maxlength="2" disabled="true"/>Years
									</td>
								</tr>
                                <tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Retirement Age*</span></td>
									<td width="25%">
										<form:input path="ageAtRetirement" size="3" maxlength="2" title="This indicates the Age at which System will stop paying salary" disabled="true"/>Years
									</td>
								</tr>
	                            <tr>
    									<td align="right" width="35%" nowrap>
    										<span class="required">Estimated Tax Rate*</span></td>
    									<td width="55%">
    										<form:input path="taxRate" size="5" maxlength="2" title="This indicates the estimated tax percentage rate" disabled="true"/>Percent(%)
    									</td>
    								</tr>
                                    <tr>
    									<td align="right" width="35%" nowrap>
    										<span class="required">Max Loan/Wage Percentage*</span></td>
    									<td width="55%">
    										<form:input path="maxLoanPercentage" size="5" maxlength="2" title="This indicates the maximum percentage ALL Loans must be to Monthly Income" disabled="true"/>Percent(%)
    									</td>
    								</tr>
                                    <tr>
                                      <td align="right" width="35%" nowrap>
                                       <span class="required">Show Retirement Date on Pay Slip</span>
                                      </td>
                                      <td width="35%">
                                      <form:checkbox path="showRetirementDatePaySlipBind" title="Select this box to display Expected Retirement Date of {roleBean.staffTypeName}s on Pay Slips" disabled="true"/>
                                      </td>
                                    </tr>
								</c:if>
                                <tr>
                                  <td align="right" width="35%" nowrap>
                                  <span class="required">Mandate Residence ID</span>
                                   </td>
                                    <td width="35%">
                                    <form:checkbox path="reqResIdBind" title="Select this box makes 'Residence ID' a Required Field" disabled="true"/>
                                     </td>
                                </tr>
                                <tr>
                                    <td align="right" width="35%" nowrap>
                                    <span class="required">Mandate <c:out value="${roleBean.staffTypeName}"/> Email</span>
                                     </td>
                                      <td width="35%">
                                      <form:checkbox path="reqStaffEmailBind" title="Select this box makes 'Email' a Required Field" disabled="true"/>
                                       </td>
                                      </td>
                                  </tr>
                                 <tr>
                                   <td align="right" width="35%" nowrap>
                                   <span class="required">Mandate NIN</span>
                                    </td>
                                     <td width="35%">
                                     <form:checkbox path="reqNinBind" title="Select this box makes 'NIN' National Identification Number a Required Field - Note: If selected, ${roleBean.staffTypeName} without NIN will not get paid." disabled="true"/>
                                      </td>
                                     </td>
                                 </tr>
                                 <tr>
                                    <td align="right" width="35%" nowrap>
                                    <span class="required">Mandate BVN For Payment</span>
                                     </td>
                                      <td width="35%">
                                      <form:checkbox path="reqBvnBind" title="Select this box makes 'BVN' Bank Verification Number a requirement for Salary/Pension Generation - Note: If selected, ${roleBean.staffTypeName} without BVN will not get paid." disabled="true"/>
                                       </td>
                                      </td>
                                  </tr>
                                 <tr>
                                   <td align="right" width="35%" nowrap>
                                   <span class="required">Mandate Biometrics For Payment</span>
                                    </td>
                                     <td width="35%">
                                     <form:checkbox path="reqBioBind" title="Selection of this box makes 'Biometrics Verification' mandatory for Salary/Pension Payment. ${roleBean.staffTypeName} without 'Verified Biometric Information' will not get paid." disabled="true"/>
                                      </td>
                                     </td>
                                 </tr>
								<tr>
                                  <td align="right" width="35%" nowrap>
                                   <span class="required">Allow <c:out value="${roleBean.staffTypeName}"/> Creator To Approve</span>
                                  </td>
                                   <td width="35%">
                                   <form:checkbox path="empCreatorCanApproveBind" title="Select this box to Allow Creators of ${roleBean.staffTypeName}s approve them for Payroll" disabled="true"/>
                                   </td>
                                   </tr>
                                 <tr>
                                   <td align="right" width="35%" nowrap>
                                    <span class="required">Allow Step Incrementer To Approve</span>
                                   </td>
                                    <td width="35%">
                                    <form:checkbox path="stepIncrementerCanApproveBind" title="Select this box to Allow Creators of ${roleBean.staffTypeName}s Step Increment to Approve " disabled="true"/>
                                    </td>
                                 </tr>
                                <tr>
                                   <td align="right" width="35%" nowrap>
                                    <span class="required">Allow Salary Structure Creator To Approve</span>
                                   </td>
                                    <td width="35%">
                                    <form:checkbox path="payGroupCreatorCanApproveBind" title="Select this box to Allow Creators of Salary Structure to Approve" disabled="true"/>
                                    </td>
                                 </tr>
                                    <tr>
                                      <td align="right" width="35%" nowrap>
                                       <span class="required">Show Picture On Pay Slip</span>
                                      </td>
                                      <td width="35%">
                                      <form:checkbox path="showPiksoOnPaySlipBind" title="Select this box to display Picture of ${roleBean.staffTypeName} on Pay Slips" disabled="true"/>
                                      </td>
                                    </tr>
                                     <tr>
                                      <td align="right" width="35%" nowrap>
                                       <span class="required">Mandate OGS Email for IPPMS Users</span>
                                      </td>
                                      <td width="35%">
                                      <form:checkbox path="useOgsEmailBind"  disabled="true"/>
                                      </td>
                                    </tr>
                                     <tr>
                                      <td align="right" width="35%" nowrap>
                                       <span class="required">Show Bank Details On Pay Slip</span>
                                      </td>
                                      <td width="35%">
                                      <form:checkbox path="showBankInfoOnPaySlipBind" title="Select this box to display Bank Details of ${roleBean.staffTypeName} on Pay Slips" disabled="true"/>
                                      </td>
                                    </tr>
                                 <tr>
                                   <td align="right" width="35%" nowrap>
                                   <span class="required">Open Percentage Module</span>
                                    </td>
                                     <td width="35%">
                                     <form:checkbox path="globalPercentModOpenBind" title="Select this box opens the  'Global Percentage Module'." disabled="true"/>
                                      </td>
                                     </td>
                                 </tr>

								<c:if test="${roleBean.pensioner}">
                                 <tr>
	                                <td align="right" width="35%" nowrap>
	                                <span class="required">Use Am Alive Functionality</span>
	                                </td>
                                     <td width="35%">
                                      <form:checkbox path="iamAliveBind" title="Select this box to Allow Creators of ${roleBean.staffTypeName}s 'Am Alive' Requests to approve them for Payroll" disabled="true"/>
                                   </td>
	                              </tr>
                                	 <tr>
                                	 <td align="right" width="35%" nowrap>
                                	 <span class="required">I Am Alive Value*</span></td>
                                	 <td width="35%">
                                	   <form:input path="iamAlive" size="3" maxlength="2" disabled="true"/>Months
                                	 </td>
                                 </tr>
                                   <tr>
                                   <td align="right" width="35%" nowrap>
                                    <span class="required">Allow 'Am Alive Creator' To Approve</span>
                                   </td>
                                    <td width="35%">
                                    <form:checkbox path="amAliveCreatorCanApproveBind" title="Select this box to Allow Creators of ${roleBean.staffTypeName}s 'Am Alive' Requests to approve them for Payroll" disabled="true"/>
                                    </td>
                                    </tr>
 	                             <tr>
                                 	 <td align="right" width="35%" nowrap>
                                 	 <span class="required">I Am Alive Extension*</span></td>
                                 	 <td width="35%">
                                 	   <form:input path="iamAliveExt" size="1" maxlength="2" disabled="true"/>Years
                                 	 </td>
                                  </tr>
                                 </c:if>
                                 <tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Max Percentage For Special Allowance*</span></td>
									<td width="55%">
										<form:input path="maxSpecAllowValue" size="5" maxlength="2" title="This indicates the maximum allowed value for Percentage based Special Allowances" disabled="true"/>Percent(%)
									</td>
								</tr>
                                 <tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Max Percentage For Deductions*</span></td>
									<td width="55%">
										<form:input path="maxDeductionValue" size="5" maxlength="2" title="This indicates the maximum allowed value for Percentage based Deductions" disabled="true"/>Percent(%)
									</td>
								</tr>

								</c:when>
								<c:otherwise>
                                <c:if test="${not roleBean.pensioner}">
							     <tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Max Length Of Service*</span></td>
									<td width="25%">
										<form:input path="serviceLength" size="3" maxlength="2" disabled="${not roleBean.privilegedUser}"  />Years
									</td>
								</tr>

                                 <tr>
   									<td align="right" width="35%" nowrap>
   										<span class="required">Estimated Tax Rate*</span></td>
   									<td width="55%">
   										<form:input path="taxRate" size="5" maxlength="2" title="This indicates the estimated tax percentage rate"/>Percent(%)
   									</td>
   								</tr>
                                   <tr>
   									<td align="right" width="35%" nowrap>
   										<span class="required">Max Loan/Wage Percentage*</span></td>
   									<td width="55%">
   										<form:input path="maxLoanPercentage" size="5" maxlength="2" title="This indicates the maximum percentage ALL Loans must be to Monthly Income"/>Percent(%)
   									</td>
   								</tr>
                                <tr>
                                      <td align="right" width="35%" nowrap>
                                       <span class="required">Show Retirement Date on Pay Slip</span>
                                      </td>
                                      <td width="35%">
                                      <form:input path="showRetirementDatePaySlipBind" id="showRetirementDatePaySlipBindInd"  style="display:none"/>
                                         <label class="toggle">
                                           <input id="showRetirementDatePaySlipBindSwitch" name="showRetirementDatePaySlipBindSwitch" type="checkbox" onClick="toggleIt(this,'showRetirementDatePaySlipBindSwitch','showRetirementDateStatus','showRetirementDatePaySlipBindInd')" title="Select this box to display Expected Retirement Date of {roleBean.staffTypeName}s on Pay Slips">
                                           <span class="roundbutton"></span>
                                           </label>
                                           <span id="showRetirementDateStatus">No</span>
                                      </td>
                                    </tr>
                                <tr>
                                  <td align="right" width="35%" nowrap>
                                  <span class="required">Mandate Residence ID</span>
                                   </td>

                                    <td width="35%">
                                   <form:input path="reqResIdBind" id="reqResIdBindInd"  style="display:none"/>
                                      <label class="toggle">
                                       <input id="reqResIdBindSwitch" name="reqResIdBindSwitch" type="checkbox" onClick="toggleIt(this,'reqResIdBindSwitch','resIdStatus','reqResIdBindInd')" title="Select this box makes 'Residence ID' a Required Field">
                                         <span class="roundbutton"></span>
                                       </label>
                                      <span id="resIdStatus">No</span>
                                     </td>

                                </tr>

                                 <tr>
                                   <td align="right" width="35%" nowrap>
                                   <span class="required">Mandate <c:out value="${roleBean.staffTypeName}"/> Email</span>
                                    </td>

                                     <td width="35%">
                                        <form:input path="reqStaffEmailBind" id="reqStaffEmailBindInd" style="display:none"/>
                                         <label class="toggle">
                                            <input id="reqStaffEmailSwitch" name="reqStaffEmailSwitch" type="checkbox" onClick="toggleIt(this,'reqStaffEmailSwitch','reqStaffEmailStatus','reqStaffEmailBindInd')" title="Select this box makes ${roleBean.staffTypeName} Email a Required Field.">
                                             <span class="roundbutton"></span>
                                          </label>
                                          <span id="reqStaffEmailStatus">No</span>

                                     </td>
                                 </tr>
                                  <tr>
                                   <td align="right" width="35%" nowrap>
                                   <span class="required">Mandate NIN</span>
                                    </td>

                                     <td width="35%">
                                        <form:input path="reqNinBind" id="reqNinBindInd" style="display:none"/>
                                         <label class="toggle">
                                            <input id="reqNinBindSwitch" name="reqNinBindSwitch" type="checkbox" onClick="toggleIt(this,'reqNinBindSwitch','reqNinStatus','reqNinBindInd')" title="Select this box makes 'NIN' National Identification Number a Required Field - Note: If selected, ${roleBean.staffTypeName} without NIN will not get paid.">
                                             <span class="roundbutton"></span>
                                          </label>
                                          <span id="reqNinStatus">No</span>

                                     </td>
                                 </tr>
                                  <tr>
                                   <td align="right" width="35%" nowrap>
                                   <span class="required">Mandate Biometrics For Payment</span>
                                    </td>

                                     <td width="35%">
                                        <form:input path="reqBioBind" id="reqBioBindInd" style="display:none"/>
                                         <label class="toggle">
                                            <input id="reqBioBindSwitch" name="reqBioBindSwitch" type="checkbox" onClick="toggleIt(this,'reqBioBindSwitch','reqBioBindStatus','reqBioBindInd')" title="Select this box makes 'Biometrics Information Verification' required required for Salary/Pension Payment - Note: If selected, ${roleBean.staffTypeName} without their 'Biometrics Information Verified'' will not get paid.">
                                             <span class="roundbutton"></span>
                                          </label>
                                          <span id="reqBioBindStatus">No</span>

                                     </td>
                                 </tr>
                                <tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Retirement Age*</span></td>
									<td width="25%">
										<form:input path="ageAtRetirement" size="3" maxlength="2" title="This indicates the Age at which System will stop paying salary"  disabled="${not roleBean.privilegedUser}"/>Years
									</td>
								</tr>
								</c:if>
								<c:if test="${roleBean.pensioner}">
                                 <tr>
 	                                <td align="right" width="35%" nowrap>
 	                                <span class="required">Use Am Alive Functionality</span>
 	                                </td>

                                      <td width="35%">
                                         <form:input path="iamAliveBind" id="iAmAliveBindInd" style="display:none"/>
                                          <label class="toggle">
                                             <input id="iAmAliveBindSwitch" name="iAmAliveBindSwitch" type="checkbox"  onClick="toggleIt(this,'iAmAliveBindSwitch','iAmAliveStatus','iAmAliveBindInd')" title="Select this box to indicate that 'Am Alive' should not be considered when processing Pensioner Payroll." >
                                              <span class="roundbutton"></span>
                                           </label>
                                           <span id="iAmAliveStatus">No</span>

                                      </td>
 	                              </tr>
                                <tr>
                                  <td align="right" width="35%" nowrap>
                                  <span class="required"> Allow 'Am Alive Creator' To Approve</span>
                                   </td>

                                      <td width="35%">
                                         <form:input path="amAliveCreatorCanApproveBind" id="amAliveCreatorCanApproveBindInd" style="display:none"/>
                                          <label class="toggle">
                                             <input id="amAliveCreateSwitch" name="amAliveCreateSwitch" type="checkbox" onClick="toggleIt(this,'amAliveCreateSwitch','amAliveCreateStatus','amAliveCreatorCanApproveBindInd')" title="Select this box to Allow Creators of ${roleBean.staffTypeName}s to also approve them for Payroll.">
                                              <span class="roundbutton"></span>
                                           </label>
                                           <span id="amAliveCreateStatus">No</span>

                                      </td>
                                </tr>

                                 <tr>
                                	 <td align="right" width="35%" nowrap>
                                	 <span class="required">I Am Alive Value*</span></td>
                                	 <td width="35%">
                                	   <form:input path="iamAlive" size="3" maxlength="2" disabled="${not roleBean.privilegedUser}"/>Years
                                	 </td>
                                 </tr>

                                 <tr>
                                	 <td align="right" width="35%" nowrap title="This is the default value a Pensioner's 'I Am Alive' Date will be extended by">
                                	 <span class="required">I Am Alive Value Extension*</span></td>
                                	 <td width="35%">
                                	   <form:input path="iamAliveExt" size="1" maxlength="2" disabled="${not roleBean.privilegedUser}"/>Months
                                	 </td>
                                 </tr>


                                 </c:if>
                                <tr>
                                   <td align="right" width="35%" nowrap>
                                   <span class="required">Mandate BVN For Payment</span>
                                    </td>

                                     <td width="35%">
                                        <form:input path="reqBvnBind" id="reqBvnBindInd" style="display:none"/>
                                         <label class="toggle">
                                            <input id="reqBvnBindSwitch" name="reqBvnBindSwitch" type="checkbox" onClick="toggleIt(this,'reqBvnBindSwitch','reqBvnStatus','reqBvnBindInd')" title="Select this box makes ${roleBean.staffTypeName} 'BVN' Bank Verification Number Mandatory for Salary/Pension Generation - Note: If selected, ${roleBean.staffTypeName} without BVN will not get paid.">
                                             <span class="roundbutton"></span>
                                          </label>
                                          <span id="reqBvnStatus">No</span>

                                     </td>
                                 </tr>
                                <tr>
                                  <td align="right" width="35%" nowrap>
                                  <span class="required"><c:out value="${roleBean.staffTypeName}"/> Creator To Approve</span>
                                   </td>

                                      <td width="35%">
                                         <form:input path="empCreatorCanApproveBind" id="empCreatorCanApproveBindInd" style="display:none"/>
                                          <label class="toggle">
                                             <input id="empCreateSwitch" name="empCreateSwitch" type="checkbox" onClick="toggleIt(this,'empCreateSwitch','empCreateStatus','empCreatorCanApproveBindInd')" title="Select this box to Allow Creators of ${roleBean.staffTypeName}s to also approve them for Payroll.">
                                              <span class="roundbutton"></span>
                                           </label>
                                           <span id="empCreateStatus">No</span>

                                      </td>
                                </tr>
                                <tr>
                                  <td align="right" width="35%" nowrap>
                                  <span class="required"><c:out value="${roleBean.staffTypeName}"/> Step Increment Creator To Approve</span>
                                   </td>

                                      <td width="35%">
                                         <form:input path="stepIncrementerCanApproveBind" id="stepIncrementerCanApproveBindInd" style="display:none"/>
                                          <label class="toggle">
                                             <input id="stepIncrementSwitch" name="stepIncrementSwitch" type="checkbox" onClick="toggleIt(this,'stepIncrementSwitch','stepIncrementStatus','stepIncrementerCanApproveBindInd')" title="Select this box to Allow Creators of ${roleBean.staffTypeName}s Step Increment to also approve them.">
                                              <span class="roundbutton"></span>
                                           </label>
                                           <span id="stepIncrementStatus">No</span>

                                      </td>
                                </tr>
                                <tr>
                                  <td align="right" width="35%" nowrap>
                                  <span class="required">Salary Structure Creator To Approve</span>
                                   </td>

                                      <td width="35%">
                                         <form:input path="payGroupCreatorCanApproveBind" id="payGroupCreatorCanApproveBindInd" style="display:none"/>
                                          <label class="toggle">
                                             <input id="payGroupCreatorSwitch" name="payGroupCreatorSwitch" type="checkbox" onClick="toggleIt(this,'payGroupCreatorSwitch','payGroupCreatorStatus','payGroupCreatorCanApproveBindInd')" title="Select this box to Allow Creators of Salary Structure to also approve them.">
                                              <span class="roundbutton"></span>
                                           </label>
                                           <span id="payGroupCreatorStatus">No</span>

                                      </td>
                                </tr>
                                 <tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Max Percentage For Special Allowance*</span></td>
									<td width="55%">
										<form:input path="maxSpecAllowValue" size="2" maxlength="5" title="This indicates the maximum allowed value for Percentage based Special Allowances"/>Percent(%)
									</td>
								</tr>
                                 <tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Max Percentage For Deductions*</span></td>
									<td width="55%">
										<form:input path="maxDeductionValue" size="2" maxlength="5" title="This indicates the maximum allowed value for Percentage based Deductions"/>Percent(%)
									</td>
								</tr>
                                   <tr>
                                      <td align="right" width="35%" nowrap>
                                       <span class="required">Show Picture On Pay Slip</span>
                                      </td>

                                         <td width="35%">
                                          <form:input path="showPiksoOnPaySlipBind" id="showPiksoOnPaySlipBindInd" style="display:none"/>
                                           <label class="toggle">
                                              <input id="piksoSwitch" name="piksoSwitch" type="checkbox" onClick="toggleIt(this,'piksoSwitch','piksoStatus','showPiksoOnPaySlipBindInd')" title="Select this box to Allow Creators of ${roleBean.staffTypeName}s to also approve them for Payroll.">
                                               <span class="roundbutton"></span>
                                            </label>
                                            <span id="piksoStatus">No</span>

                                       </td>
                                    </tr>
                                     <tr>
                                      <td align="right" width="35%" nowrap>
                                       <span class="required">Show Bank Details On Pay Slip</span>
                                      </td>

                                         <td width="35%">
                                          <form:input path="showBankInfoOnPaySlipBind" id="bankOnPaySlipBindInd"  style="display:none"/>
                                           <label class="toggle">
                                              <input id="bankInfoSwitch" name="bankInfoSwitch" type="checkbox" onClick="toggleIt(this,'bankInfoSwitch','bankInfoStatus','bankOnPaySlipBindInd')" title="Select this box to display Bank Details of {roleBean.staffTypeName} on Pay Slips.">
                                               <span class="roundbutton"></span>
                                            </label>
                                            <span id="bankInfoStatus">No</span>

                                       </td>

                                    </tr>
                                    <tr>
                                      <td align="right" width="35%" nowrap>
                                       <span class="required">Open Percentage Module</span>
                                      </td>

                                         <td width="35%">
                                          <form:input path="globalPercentModOpenBind" id="globalPercentModOpenBindInd"  style="display:none"/>
                                           <label class="toggle">
                                              <input id="globalPercentModOpenBindSwitch" name="globalPercentModOpenBindSwitch" type="checkbox" onClick="toggleIt(this,'globalPercentModOpenBindSwitch','globalPercentModOpenBindStatus','globalPercentModOpenBindInd')" title="Select this box to 'Open Global Percentage Module'.">
                                               <span class="roundbutton"></span>
                                            </label>
                                            <span id="globalPercentModOpenBindStatus">No</span>

                                       </td>

                                    </tr>
                                    <tr>
                                      <td align="right" width="35%" nowrap>
                                       <span class="required">Mandate OGS Email for IPPMS Users</span>
                                      </td>

                                         <td width="35%">
                                          <form:input path="useOgsEmailBind" id="useOgsEmailBindInd"  style="display:none"/>
                                           <label class="toggle">
                                              <input id="useOgsEmailSwitch" name="useOgsEmailSwitch" type="checkbox" onClick="toggleIt(this,'useOgsEmailSwitch','useOgsEmailStatus','useOgsEmailBindInd')" title="Select this box to mandate use of Government Emails Only by users of the IPPMS." <c:if test="${not roleBean.privilegedUser}" >disabled="true"</c:if> >
                                               <span class="roundbutton"></span>
                                            </label>
                                            <span id="useOgsEmailStatus">No</span>

                                       </td>

                                    </tr>

								</c:otherwise>
								</c:choose>
								<!--
                                <tr>
                                   <td align="right" width="35%" nowrap>
                                      <span>Restrict Login Start Date</span>
                                   </td>
                                   <td width="55%">
                                      <form:input path="cutOffStartDate" title="Start Date"/>
                                      <img src="images/calendar.png" id="fromDateId" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('cutOffStartDate'),event);">&nbsp;&nbsp;
                                   </td>
                                </tr>
                                <tr>
                                   <td align="right" width="35%" nowrap>
                                      <span>Restrict Login End Date</span>
                                   </td>
                                   <td width="55%">
                                      <form:input path="cutOffEndDate" title="End Date"/>
                                      <img src="images/calendar.png" id="toDateId" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('cutOffEndDate'),event);">&nbsp;&nbsp;
                                   </td>
                                </tr>
                                -->
                                <tr>
                                    <td align="right" width="35%" nowrap>
                                        <span>Login Start Time</span>
                                    </td>
                                    <td width="55%">
                                       <div style="width:30%" class='input-group date' id='datetimepickerstart'>
                                         <form:input path="cutOffStartTime" style="height:25px" type='text' class="form-control" title="Time Users are allowed to Login into the IPPMS"/>
                                         <span class="input-group-addon">
                                             <span class="glyphicon glyphicon-time"></span>
                                         </span>
                                       </div>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="right" width="35%" nowrap>
                                        <span>Login End Time</span>
                                    </td>
                                    <td width="55%">
                                       <div style="width:30%" class='input-group date' id='datetimepickerend'>
                                         <form:input path="cutOffEndTime" style="height:25px" type='text' class="form-control" title="Time Users are no longer allowed to Login into the IPPMS"/>
                                         <span class="input-group-addon">
                                             <span class="glyphicon glyphicon-time"></span>
                                         </span>
                                       </div>
                                    </td>
                                </tr>
					           </table>
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
							 <c:choose>
							         	<c:when test="${saved}">
												<input type="image" name="_cancel" value="cancel" title="Close" class="" src="images/close.png">
							         	</c:when>
							         	<c:otherwise>
							         		<input type="image" name="submit" value="ok" title="Ok" class="" src="images/ok_h.png">&nbsp;
											<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">

							         	</c:otherwise>

							         </c:choose>
						</td>
					</tr>
				</table>
				</form:form>
			</td>
		</tr>

		</table>
		</td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
<script src="scripts/moment.min.js" type="text/javascript"></script>
<script src="scripts/bootstrap-datetimepicker.min.js" type="text/javascript"></script>
<script language="javascript">
 $(document).ready(function(){
        if($("#reqResIdBindInd").val() === "true"){
           $('input[name=reqResIdBindSwitch]').attr('checked', true);
                  $("#resIdStatus").html("Yes");
       }
         if($("#reqBioBindInd").val() === "true"){
                  $('input[name=reqBioBindSwitch]').attr('checked', true);
                         $("#reqBioBindStatus").html("Yes");
              }
       if($("#reqNinBindInd").val() === "true"){
                  $('input[name=reqNinBindSwitch]').attr('checked', true);
                  $("#reqNinStatus").html("Yes");
              }
        if($("#reqBvnBindInd").val() === "true"){
                         $('input[name=reqBvnBindSwitch]').attr('checked', true);
                         $("#reqBvnStatus").html("Yes");
         }

   if($("#reqStaffEmailBindInd").val() === "true"){
                         $('input[name=reqStaffEmailSwitch]').attr('checked', true);
                         $("#reqStaffEmailStatus").html("Yes");
         }
       if($("#iAmAliveBindInd").val() === "true"){
                  $('input[name=iAmAliveBindSwitch]').attr('checked', true);
                  $("#iAmAliveStatus").html("Yes");
              }
       if($("#empCreatorCanApproveBindInd").val() === "true"){
                  $('input[name=empCreateSwitch]').attr('checked', true);
                  $("#empCreateStatus").html("Yes");
              }
       if($("#amAliveCreatorCanApproveBindInd").val() === "true"){
                         $('input[name=amAliveCreateSwitch]').attr('checked', true);
                         $("#amAliveCreateStatus").html("Yes");
                     }
       if($("#stepIncrementerCanApproveBindInd").val() === "true"){
                         $('input[name=stepIncrementSwitch]').attr('checked', true);
                         $("#stepIncrementStatus").html("Yes");
                     }
       if($("#payGroupCreatorCanApproveBindInd").val() === "true"){
                         $('input[name=payGroupCreatorSwitch]').attr('checked', true);
                         $("#payGroupCreatorStatus").html("Yes");
                     }
       if($("#bankOnPaySlipBindInd").val() === "true"){
                  $('input[name=bankInfoSwitch]').attr('checked', true);
                  $("#bankInfoStatus").html("Yes");
              }
         if($("#globalPercentModOpenBindInd").val() === "true"){
                         $('input[name=globalPercentModOpenBindSwitch]').attr('checked', true);
                         $("#globalPercentModOpenBindStatus").html("Yes");
                     }
       if($("#showPiksoOnPaySlipBindInd").val() === "true"){
                         $('input[name=piksoSwitch]').attr('checked', true);
                         $("#piksoStatus").html("Yes");
              }
       if($("#showRetirementDatePaySlipBindInd").val() === "true"){
                                $('input[name=showRetirementDatePaySlipBindSwitch]').attr('checked', true);
                                $("#showRetirementDateStatus").html("Yes");

              }
   if($("#useOgsEmailBindInd").val() === "true"){
                                  $('input[name=useOgsEmailSwitch]').attr('checked', true);
                                  $("#useOgsEmailStatus").html("Yes");
                }
       });

      $(function () {
         $('#datetimepickerstart').datetimepicker({
              format: 'HH:mm'
          });
      });
      $(function () {
         $('#datetimepickerend').datetimepicker({
              format: 'HH:mm'
         });
      });
</script>
</body>

</html>