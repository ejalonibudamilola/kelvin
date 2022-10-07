<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Edit <c:out value="${roleBean.staffTypeName}"/> Taxes  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<script type="text/javascript" src="scripts/jacs.js"></script>
</head>

<body class="main">

	<table class="main" width="70%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>

			<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			
			<tr>
				<td colspan="2">
						<div class="title">Edit <c:out value="${namedEntity.name}"/>'s Tax Info</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">
	If your employee is exempt from income tax withholding, choose "Do Not Withhold" as the filing status. <br>
	<br/> * = Required<br/><br/>
					<form:form modelAttribute="empTaxInfo">
					
					<table border=0 cellspacing=0 cellpadding=0 width="80%">
						<tr>
							<td>
								<table class="formtable"border=0 cellspacing=0 cellpadding=3 width=100% align="left" >
									<tr align="left">
										<td class="activeTH">Tax Withholding Information</td>
									</tr>
									<tr>
										<td id="firsttd_edittax_form" class="activeTD">
											<table width="95%" border="0" cellspacing="0" cellpadding="2">
												<tr> 
													<td align=right width="25%" nowrap><span class="required">Federal Filing Status*</span></td>
													<c:choose>
									  					<c:when test="${empTaxInfo.editMode}">
									  					<td colspan="2" nowrap>	<c:out value="${empTaxInfo.defaultFedFilingStatus}"/>
						               					</td>
									  					 </c:when>
									  					 <c:otherwise>
						            					<td colspan="2" nowrap>	
														<form:select path="fedFilingInd" id="fedfilingind">
															<form:option value="0">&lt; Please Select &gt;</form:option>
															<c:forEach items="${fedFilingStatus}" var="fedFiling">
															<form:option value="${fedFiling.id}">${fedFiling.name}</form:option>
															</c:forEach>
														</form:select>
														<span class="note">(Federal Tax filing status for <c:out value="${namedEntity.name}" />)</span>
														</td>
						               					</c:otherwise>
						               					</c:choose>
													
												</tr>
												<tr> 
													<td align=right width="25%"><span class="required">Allowances</span></td>
													<td colspan="2" nowrap> 
														<form:input path="fedAllowances" size="2" maxlength="3" readonly="true"/>
														<span class="note">(Total number of dependants)</span>
													</td>
												</tr>
												<tr>													
													<td align=right width="25%" nowrap><span class="optional">Additional Amount</span></td>
													<td colspan="2" nowrap>
														<form:input path="fedAddAmount" size="6" readonly="true"/>
														<span class="note">(Additional non-taxable amount)</span>
														
													</td>
												</tr>
												<tr>
													<td>
														<br>
													</td>
												</tr>
												<tr> 
												<!-- cross-state change -->
													<td align=right width="25%" nowrap>
														<span class="required"> State Filing Status*</span>
													</td>
													<td colspan="2"> 	
														<form:select path="stateFilingInd" id="statefilingind">
															<form:option value="0">&lt; Please Select &gt;</form:option>
															<c:forEach items="${stateFilingStatus}" var="stateFiling">
															<form:option value="${stateFiling.id}">${stateFiling.name}</form:option>
															</c:forEach>
															</form:select>
												</td>
												</tr>
												<tr>
													<td align=right width="25%" valign="top"><span class="required">Allowances</span></td>
													<td colspan="2"> 
														<form:input path="stateAllowances" size="2" maxlength="3"/>
														<span class="note">(Total number of dependants)</span>
														
													</td>
												</tr>
												<tr>
													<td align=right width="25%" nowrap><span class="optional">Additional Amount</span></td>
													<td colspan="2" nowrap>
														<form:input path="stateAddAmount" id="stateAddAmt" size="6"/>
														<span class="note"></span>
													</td>
												</tr>
											</table>
										</td>
									</tr>
									
								</table>	
							</td>
						</tr>
						<tr>
							<td class="buttonRow" align="right" >
								<input type="image" name="submit" value="ok" title="Ok" class="" src="images/ok_h.png">
								<input type="image" name="_cancel" value="cancel" title="Cancel" src="images/cancel_h.png">
							</td>
						</tr>
					</table>
				</form:form>
			</table>

			</td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
</body>
</html>
