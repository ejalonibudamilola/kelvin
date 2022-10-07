<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Setup | Edit Paid Time Off Policy  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">

</head>

<body class="main">

	<table class="main" width="70%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>

				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
			<tr>
				<td colspan="2">
					<div class="title">Edit Vacation Pay Policy For <c:out value="${namedEntity.name}" /></div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">
					Make any changes below, then click <b>ok</b>.<br>
					 * = Required<br/><br/>
					<form:form modelAttribute="payPolicyInfo">
						<table class="formtable" border=0 cellspacing=0 cellpadding=3 width="100%" align="left">
									<tr align="left">
										<td class="activeTH">Vacation/Sick leave</td>
									</tr>
									<tr>
										<td id="firsttd_editptopolicy_form" class="activeTD">
										
											<table width="95%" border="0" cellspacing="0" cellpadding="2">
												<tr>
													<td  class="required" width="35%">
														<span class="required">Category*</span>
													</td>
													<td width="65">

														<c:out value="${payPolicyInfo.payPolicyType.name}"/>
													<!--<input type="hidden" name="ptocategory" value="2"/> -->
													</td>
												</tr>
												<tr>
													<td align=right nowrap width="35%"><span class="required">Hours are accrued *</span></td>
													<td nowrap>	
														<form:select path="hoursAccrueInstId">
															<c:forEach items="${hoursAccrue}" var="hrsAccrue">
															<form:option value="${hrsAccrue.id}">${hrsAccrue.name}</form:option>
															</c:forEach>
														</form:select>															
													</td>
												</tr>
												<tr>
													<td align=right nowrap width="35%"><span class="required">Employees earn *</span></td>
													<td nowrap>
														<form:input path="employeesEarn" size="5" maxlength="5"/>
															&nbsp;Hours 
															per year
													</td>
												</tr>
												<tr>
													<td  class="optional" nowrap width="35%"><span class="optional">Maximum available</span></td>
													<td>
														<form:input path="maximumHours" maxlength="5" size="5" onchange="checkmax();"/>
															&nbsp;Hours
														
													</td>
												</tr>
											</table>
										</td>
									</tr>
							
							<tr>
								<td class="buttonRow" align="right" >
								<input type="image" name="submit" value="ok" alt="Ok" class="" src="images/ok_h.png">
								<input type="image" name="_cancel" value="cancel" alt="Cancel" src="images/cancel_h.png">
								</td>
							</tr>
						</table>
				</form:form>
			</td>
		</tr>
		</table>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
</body>
</html>
