<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> <c:out value="${roleBean.staffTypeName}"/> Step Increment Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon"
      type="image/png"
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="<c:url value="/styles/epayroll.css"/>" type="text/css"/>
<script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
<script src="<c:url value="/scripts/utility_script.js"/>"></script>
<script type="text/javascript">
<!--
function doShowHideList(box,idName) {
     //alert("got into go fxn");

	  //alert("selected value = "+n);
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
								<div class="title">Annual Increment for <c:out value="${miniBean.name}" /></div>
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
                              	<a href='${appContext}/searchEmpStepIncrement.do' title="Search for another ${roleBean.staffTypeName} to perform Step Increment"><i>Search Again</i></a>
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
										<span class="required"><b><c:out value="${roleBean.staffTypeName}"/> Name  : </b></span></td>
									<td width="35%">
										<b style="color:blue"><c:out value="${miniBean.name}"/></b>
									</td>

					            </tr>
								<tr>
									<td align="right"><span class="required"><b><c:out value="${roleBean.staffTitle}"/></span> : </b></td>
									<td width="35%">
										<b style="color:blue"><c:out value="${miniBean.employeeId}"/></b>
									</td>

					            </tr>
                                <tr>
									<td align="right"><span class="required"><b>Pay Group</span> : </b></td>
									<td width="35%">
										<b style="color:blue"><c:out value="${miniBean.gradeLevelAndStep}"/></b>
									</td>

					            </tr>
					            <tr>
									<td align="right" width="25%"><span class="optional"><b><c:out value="${roleBean.mdaTitle}"/></span> : </b></td>
									<td width="35%">
										<b><c:out value="${miniBean.assignedToObject}"/></b>
									</td>
					            </tr>
					            <tr>
									<td align="right" width="25%"><span class="required"><b> Hire Date</span>: </b></td>
									<td width="35%">
										<b><c:out value="${miniBean.hireDate}"/></b>
									</td>

					            </tr>
								<tr>
									<td align="right" width="25%"><span class="required"><b>Years of Service</span> : </b></td>
									<td width="35%">
										<b><c:out value="${miniBean.yearsOfService}"/></b>
									</td>
					            </tr>
								<tr>
									<td align="right" width="25%"><span class="required"><b>Current Level &amp; Step</span></b></td>
									<td width="35%">
										<b><c:out value="${miniBean.oldLevelAndStep}"/>&nbsp:&nbsp;<c:out value="${miniBean.oldSalaryStr}"/></b>
									</td>

					            </tr>
                                <tr>
									<td align="right" width="25%"><span class="required"><b>Step Increment Level &amp; Step</b></span></td>
									<td width="35%">
										<b><c:out value="${miniBean.newLevelAndStep}"/>&nbsp:&nbsp;<c:out value="${miniBean.newSalaryStr}"/></b>
									</td>

					            </tr>

								<tr style="${miniBean.showForConfirm}">
					              <td align="right">Reference Number*</td>
					              <td width="35%"><form:input path="refNumber"/></td>
					            </tr>
					            <tr style="${miniBean.showForConfirm}">
					              <td align="right"><span class="required">Annual Step Increment Date*</span></td>
					              <td width="35%"><form:input path="refDate"/><img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('refDate'),event);"></td>

					              <td></td>
					            </tr>
							</table>
							 <c:if test="${saved}">
						    	<tr>
		                           <td>
		                           <span class="reportBottomPrintLinkLeft">
                              	          <a href='${appContext}/searchEmpStepIncrement.do' title="Search for another ${roleBean.staffTypeName} to perform Step Increment"><i>Search Again</i></a>
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
											<input type="image" name="submit" value="ok" title="Confirm Step Increment" class="" src="images/confirm_h.png">
										</c:when>
										<c:otherwise>
										  <input type="image" name="submit" value="ok" title="Perform Step Increment" class="" src="images/ok_h.png">
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

