<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Step Increment Notification Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon"
      type="image/png"
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="<c:url value="/styles/epayroll.css"/>" type="text/css"/>
<script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
<script src="<c:url value="/scripts/utility_script.js"/>"></script>

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

				<table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >

					<tr align="left">
						<td class="activeTH"><c:out value="${roleBean.staffTypeName}"/> Step Increment Information</td>
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
										<c:out value="${miniBean.oldLevelAndStep}"/>&nbsp:&nbsp;<c:out value="${miniBean.oldSalaryStr}"/>
									</td>

					            </tr>
                                <tr>
									<td align="right" width="25%"><span class="required">Step Increment Level &amp; Step</span></td>
									<td width="35%">
										<c:out value="${miniBean.newLevelAndStep}"/>&nbsp:&nbsp;<c:out value="${miniBean.newSalaryStr}"/>
									</td>

					            </tr>


							</table>

						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
	                             <c:if test="${canApprove}">
									 <input type="image" name="_approve" value="approve" title="Approve/Reject Step Increment" class="" src="images/approve.png">
								 </c:if>
									<input type="image" name="_cancel" value="cancel" title="Close Window" class="" src="images/close.png">

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

