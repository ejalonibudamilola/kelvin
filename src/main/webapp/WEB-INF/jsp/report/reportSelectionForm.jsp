<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>

<html>
<head>
<title>${title} Report Page</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css"
	media="screen">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>"
	type="text/css" />
<script type="text/javascript"
	src='<c:url value="/scripts/jquery-1.7.1.min.js"/>'></script>

<script type="text/javascript">
	function handleAllSelection(allMdaTypeCheckbox, className) {
		var checkboxes = jQuery("." + className).get();
		var checkBoxChecked = allMdaTypeCheckbox.checked;

		for ( var i = 0; i < checkboxes.length; i++) {
			currentCheckBox = checkboxes[i];

			//if the check box is checked....funny english
			if (checkBoxChecked) {
				currentCheckBox.checked = true;
			} else {
				currentCheckBox.checked = false;
			}
		}

	}
</script>
</head>

<body class="main">
	<script type="text/javascript" src="scripts/jacs.js"></script>


	<table class="main" width="70%" border="1" bordercolor="#33c0c8"
		cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0"
					cellspacing="0">
					
					<tr>
						<td>
							<div class="title">
								Select Reports to generate <br>
							</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody"><span
							class="required">*</span> = required<br /> <br /> <form:form
								modelAttribute="profileBean">
								<div id="topOfPageBoxedErrorMessage"
									style="display:${profileBean.displayErrors}">
									<spring:hasBindErrors name="profileBean">
										<ul>
											<c:forEach var="errMsgObj" items="${errors.allErrors}">
												<li><spring:message code="${errMsgObj.code}"
														text="${errMsgObj.defaultMessage}" />
												</li>
											</c:forEach>
										</ul>
									</spring:hasBindErrors>
								</div>
								<table class="formTable" border="0" cellspacing="0"
									cellpadding="3" width="70%" align="left">
									<tr>
										<td class="reportFormControls">
											<table>
												<tr>
													<%-- <td valign="middle">
								
													 <span class="optional"> Month </span> 
													 <form:input path="fromDate"/>
													 	<img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('fromDate'),event);">&nbsp;&nbsp;									
									                              &nbsp;                              
									                   
									                              
									                            <!-- <input type="image" name="_update" value="update" title="Update Report" src="images/update.png">  -->
							                       </td> --%>
													<td align="left" valign="top"><b>Pay Period :</b>
														&nbsp;&nbsp;&nbsp;&nbsp; <form:select path="runMonth">
															<form:option value="-1">&lt;Select Month&gt;</form:option>
															<c:forEach items="${profileBean.monthList}" var="uList">
																<form:option value="${uList.id}">${uList.name}</form:option>
															</c:forEach>
														</form:select>&nbsp;&nbsp; <form:select path="runYear">
															<form:option value="0">&lt;Select Year&gt;</form:option>
															<c:forEach items="${profileBean.yearList}" var="yList">
																<form:option value="${yList}">${yList}</form:option>
															</c:forEach>
														</form:select>&nbsp;&nbsp;&nbsp;&nbsp;</td>
													<td valign="top"></td>
												</tr>
												<tr>
													<td align="left" width="10%" valign="top">Schools Only&nbsp;<form:checkbox  cssStyle="margin-left:1px;" path="schoolSelected" /> </td>
													<td align="left" valign="top" style="margin-right: 50px;" >
													</td>
												</tr>
											</table>
										</td>
									</tr>

									<tr>
										<td class="buttonRow" align="right">
											&nbsp;&nbsp;&nbsp;&nbsp; <input type="image" name="_update"
											value="update" title="Update Report" src="images/update.png">

										</td>


									</tr>


									<tr>
										<td>
											<table cellspacing="0" cellpadding="0" class="report"
												align="center">
												<tr class="reportOdd header">
													<td class="tableCell" align="center" valign="top"><b>&nbsp;Pay
															Period</b>
													</td>

													<td class="tableCell" align="center" valign="top"><b>&nbsp;&nbsp;Gross
															Pay</b>
													</td>

													<td class="tableCell" align="center" valign="top"><b>&nbsp;&nbsp;Net
															Pay</b>
													</td>
												</tr>

												<tr>
													<td class="tableCell" align="center" valign="top"><b><c:out
																value="${profileBean.currDateStr}" /> </b>
													</td>

													<td class="tableCell" align="right" valign="top"><c:out
															value="${profileBean.thisMonthGrossStr}" />
													</td>

													<td class="tableCell" align="right" valign="top"><c:out
															value="${profileBean.thisMonthNetStr}" />
													</td>
												</tr>

												<tr>
													<td class="tableCell" align="center" valign="top"><b><c:out
																value="${profileBean.prevDateStr}" /> </b>
													</td>

													<td class="tableCell" align="right" valign="top"><c:out
															value="${profileBean.prevMonthGrossStr}" />
													</td>

													<td class="tableCell" align="right" valign="top"><c:out
															value="${profileBean.prevMonthNetStr}" />
													</td>
												</tr>

												<tr>
													<td class="tableCell" align="center" valign="top"><b><c:out
																value="Difference" /> </b>
													</td>

													<td class="tableCell" align="right" valign="top"><c:out
															value="${profileBean.grossDifferenceStr}" />
													</td>

													<td class="tableCell" align="right" valign="top"><c:out
															value="${profileBean.netDifferenceStr}" />
													</td>
												</tr>

											</table>
										</td>
									</tr>

									<!-- <tr align="left">
									    <td class="activeTH"></td>
								      </tr> -->
														<%--  <tr>
									    <td class="activeTD">
									    <table border="0" cellspacing="0" cellpadding="2">
									    
									      <tr>
										      <td align="left" valign="top"><b>Select Report To Generate:</b> &nbsp;&nbsp;&nbsp;&nbsp;
										      									<form:select path="id">
																				<form:option value="0">&lt;Select Report&gt;</form:option>
																				<c:forEach items="${profileBean.reportsList}" var="uList">
																				<form:option value="${uList.id}">${uList.name}</form:option>
																				</c:forEach>
																				</form:select>&nbsp;&nbsp;&nbsp;&nbsp;                              
									                            <input type="image" name="_load" value="load" title="Load Report" src="images/load.png"> 
										      </td>
										      <td>
										      </td>
								          </tr>
								         
									      
									  </table></td>
								      </tr> --%>

									<tr>
										<td class="activeTD" align="left">
											<fieldset>
												<legend>Select Reports</legend>

												<b>Select All</b><input type="checkbox"
													name="<c:out value="${status.expression}"/>" value="true"
													onchange="handleAllSelection(this, 'check_all');" align="bottom"
													<c:if test="${status.value}">checked</c:if> />

												<div id="box">
													<ul>
														<c:forEach items="${profileBean.reportsList}" var="gList"
															varStatus="gridRow">
															<!--  Bind the .do's to some kind of bean -->
															<li style="list-style: none"><spring:bind
																	path="profileBean.reportsList[${gridRow.index}].checkBoxStatus">
																	<input type="hidden"
																		name="_<c:out value="${status.expression}"/>">
																	<input type="checkbox"
																		name="<c:out value="${status.expression}"/>"
																		class="check_all" value="true"
																		<c:if test="${status.value}">checked</c:if>
																		title="${gList.name}" />
																</spring:bind>&nbsp;<c:out value="${gList.name}" />
															</li>

														</c:forEach>
													</ul>
												</div>
											</fieldset></td>
									</tr>

									<tr>
										<td class="buttonRow" align="right">

											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type="image"
											name="_load" value="load" title="Load Report"
											src="images/load.png"> <input type="image"
											name="_cancel" value="cancel" title="Cancel"
											src="images/close.png">
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