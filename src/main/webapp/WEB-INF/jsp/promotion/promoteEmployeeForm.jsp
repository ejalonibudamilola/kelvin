<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Promote <c:out value="${roleBean.staffTypeName}"/> Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="<c:url value="/styles/epayroll.css"/>" type="text/css"/>
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
<script src="<c:url value="/scripts/utility_script.js"/>"></script>
<script type="text/javascript">
<!--
function doShowHideList(box,idName) {

	  if (box.checked) {
		  
		  document.getElementById(idName).style.display = ''; 
		  
	  }else{
		  document.getElementById(idName).style.display = 'none'; 
	  }
}
//-->
</script>
</head>


<body class="main">
<script type="text/JavaScript" src="scripts/jacs.js"></script>
	<table class="main" width="70%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					


					<tr>
						<td colspan="2">
								<div class="title">Promote <c:out value="${miniBean.name}" /></div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="miniBean">
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
				    <c:if test="${saved}">
				    	<tr>
                           <td>
                           <span class="reportTopPrintLinkLeft">
                              	<a href='${appContext}/searchEmpForPromotion.do' title="Search for another ${roleBean.staffTypeName} to promote"><i>Search Again</i></a>
                              </span>                                 
                          </td>
                                       
                        </tr>
				    </c:if>
					<tr align="left">
						<td class="activeTH"><c:out value="${roleBean.staffTypeName}"/> Information</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align="right" width="25%">
										<span class="required"><c:out value="${roleBean.staffTypeName}"/> Name <b> : </b></span></td>
									<td width="35%">
										<c:out value="${miniBean.name}"/>
									</td>
									
					            </tr>
								<tr>
									<td align="right"><span class="required"><c:out value="${roleBean.staffTitle}"/></span><b> : </b></td>
									<td width="35%">
										<c:out value="${miniBean.employeeId}"/>
									</td>
								
					            </tr>
					            <tr>
									<td align="right" width="25%"><span class="optional"><c:out value="${roleBean.mdaTitle}"/></span><b> : </b></td>
									<td width="35%">
										<c:out value="${miniBean.assignedToObject}"/>
									</td>
					            </tr>
					            <tr>
									<td align="right" width="25%"><span class="required">Hire Date</span><b> : </b></td>
									<td width="35%">
										<c:out value="${miniBean.hireDate}"/>
									</td>
									
					            </tr>
								<tr>
									<td align="right" width="25%"><span class="required">Years of Service</span><b> : </b></td>
									<td width="35%">
										<c:out value="${miniBean.yearsOfService}"/>
									</td>									
					            </tr>
								<tr>
									<td align="right" width="25%"><span class="required">Current Level &amp; Step</span></td>
									<td width="35%">
										<c:out value="${miniBean.oldLevelAndStep}"/>
									</td>									
					            </tr>
					            <c:choose>
					            <c:when test="${roleBean.localGovt}">
                                 <tr>
                                  <td align="right" width="25%" title="You may choose to change the Rank within this Cadre as well">Current Rank* </td>
                                     <td width="35%" align="left">
                                      <form:select path="rankInstId" id="rank-control" cssClass="salaryTypeControls" onchange="loadSalaryTypeLevelAndStepByRankId(this);" disabled="${saved}">
                                         <form:option value="-1" >&lt;Select&gt;</form:option>
                                          <c:forEach items="${rankList}" var="rList">
                                           <form:option value="${rList.id}" title="${rList.description}">${rList.name}</form:option>
                                            </c:forEach>
                                           </form:select>
                                  </td>
                                </tr>
                                <c:if test="${not saved}">
					            <tr>
									<td align="right" width="25%">New Pay Group Level &amp; Step</td>
									<td width="35%" align="left">
										<form:select path="terminateReasonId" id="levelStep-lga-control" cssClass="salaryTypeControls" disabled="${saved}">
										<form:option value="0">&lt;Select&gt;</form:option>
										<c:forEach items="${salaryStructureList}" var="sInfoList">
						                <form:option value="${sInfoList.id}">${sInfoList.levelStepStr}</form:option>
						                </c:forEach>
										</form:select>
									</td>
						          </tr>
						          </c:if>
						       </c:when>
						       <c:otherwise>
                                <tr>
									<td align="right" width="25%">New Pay Group Level &amp; Step</td>
									<td width="35%" align="left">
										<form:select path="terminateReasonId" disabled="${saved}">
										<form:option value="0">&lt;Select&gt;</form:option>
										<c:forEach items="${salaryStructureList}" var="sInfoList">
						                <form:option value="${sInfoList.id}">${sInfoList.levelStepStr}</form:option>
						                </c:forEach>
										</form:select>
									</td>
						          </tr>
						       </c:otherwise>
						       </c:choose>

						          <tr> <td align="right" width="25%">&nbsp;</td>
                  						<td><spring:bind path="payArrearsInd">
                 						    <input type="hidden" name="_<c:out value="${status.expression}"/>">
                 							<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if> onClick="doShowHideList(this,'payArrearsAmount');doShowHideList(this,'payArrearsStartDate');doShowHideList(this,'payArrearsEndDate')" />
     										</spring:bind>Pay Arrears.
     			  						 </td>I Agree
                  						<td><div class="note"><font color="red"><i>Check this to add salary arrears</i></font></div></td>
               					 </tr>

               					 <c:if test="${miniBean.showConfirmationRow}">
 						          <tr> <td align="right" width="25%">&nbsp;</td>
                   						<td nowrap><spring:bind path="flagInd" >
                  						    <input type="hidden" name="_<c:out value="${status.expression}"/>">
                  							<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if> onClick="doShowHideList(this,'payArrearsAmount');doShowHideList(this,'payArrearsStartDate');doShowHideList(this,'payArrearsEndDate')" title="This Promotion has to be flagged as it is skipping one or more Level(s)"/>
      										</spring:bind>I Agree. &nbsp;&nbsp;<font color="red"><i>Check to accent to Flagged Promotion</i></font>
      			  						 </td>
                   						<td><div class="note"></div></td>
                				</c:if>
								<tr id="payArrearsAmount" style="${miniBean.showArrearsRow}">
									<td align="right" width="25%">Arrears Amount*</td>
									<td width="35%" align="left" colspan="2">
										 ₦<form:input path="amountStr" size="12" maxlength="12" />&nbsp;e.g 500,000.99
									</td>
						          </tr>
								<tr style="${miniBean.showForConfirm}">
					              <td align="right">Reference Number*</td>
					              <td width="35%"><form:input path="refNumber"/></td>
					            </tr>
					            <tr style="${miniBean.showForConfirm}">
					              <td align="right"><span class="required">Promotion Date*</span></td>
					              <td width="35%"><form:input path="refDate"/><img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('refDate'),event);"></td>
					              
					              <td></td>
					            </tr>
							</table>
							 <c:if test="${saved}">
						    	<tr>
		                           <td>
		                           <span class="reportBottomPrintLinkLeft">
		                              	<a href='${appContext}/searchEmpForPromotion.do' title="Search for another ${roleBean.staffTypeName} to promote"><i>Search Again</i></a>
		                              </span>                                 
		                          </td>
		                                       
		                        </tr>
						    </c:if>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
							<c:choose>
								<c:when test="${saved}">
									<input type="image" name="_cancel" value="cancel" title="Close Window" class="" src="images/close.png">
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${miniBean.warningIssued}">
											<input type="image" name="submit" value="ok" title="Confirm Promotion" class="" src="images/confirm_h.png">
										</c:when>
										<c:otherwise>
										  <input type="image" name="submit" value="ok" title="Promote ${roleBean.staffTypeName}" class="" src="images/ok_h.png">
										</c:otherwise>
									</c:choose>
									<input type="image" name="_cancel" value="cancel" title="Cancel Operation" class="" src="images/cancel_h.png">
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
</body>
</html>

