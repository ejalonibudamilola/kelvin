<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>

<html>
<head>
<title>Create/Edit School</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css"
	media="screen">
<link rel="icon" type="image/png"
	href="<c:url value="/images/coatOfArms.png"/>">
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="scripts/utility_script.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="scripts/jquery-1.4.4.min.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>
<script type="text/javascript">
	
</script>
</head>

<body class="main">
	<table class="main" width="70%" border="1" bordercolor="#33c0c8"
		cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp"%>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0"
					cellspacing="0">


					<tr>
						<td>
						    <c:choose>
                                <c:when test="${edit}">
							        <div class="title">View/Edit School</div>
							    </c:when>
							    <c:otherwise>
							        <div class="title">Create School</div>
							    </c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">* = required<br />
						<br /> <form:form modelAttribute="school">
								<div id="topOfPageBoxedErrorMessage"
									style="display:${school.parentObjectType}">
									<spring:hasBindErrors name="school">
										<ul>
											<c:forEach var="errMsgObj" items="${errors.allErrors}">
												<li><spring:message code="${errMsgObj.code}"
														text="${errMsgObj.defaultMessage}" /></li>
											</c:forEach>
										</ul>
									</spring:hasBindErrors>
								</div>
								<table class="formTable" border="0" cellspacing="0"	cellpadding="3" width="100%" align="left">
									<tr align="left">
										<td class="activeTH">School Information</td>
									</tr>
									<tr>
										<td class="activeTD">
											<table border="0" cellspacing="0" cellpadding="2">
												<c:choose>
													<c:when test="${edit}">
                                                                    <tr>
																		<td align=right nowrap><span class="required">
																				School to edit*</span>&nbsp;</td>
																		<td><form:select path="id">
																				<form:option value="-1"> &lt;Select&gt;</form:option>
																				<c:forEach items="${schoolList}" var="school">
																					<form:option value="${school.id}">${school.name}</form:option>
																				</c:forEach>
																			</form:select>
																		</td>
																		<c:choose>
                                                                        	<c:when test="${saved}">
                                                                        	</c:when>
                                                                        	<c:otherwise>
																		        <td><input type="image" name="_update"	value="update" title="Edit School" class="" src="images/go_h.png"></td>
																	        </c:otherwise>
																	    </c:choose>
																	</tr>
														
																	<tr>
																		<td align=right nowrap><span class="required">Parent
																				<c:out value="${roleBean.mdaTitle}"/>*</span>&nbsp;</td>
																		<td><form:input path="mdaInfo.name" size="60"
																				maxlength="300" disabled="true" /></td>
																	</tr>
															 
													</c:when>
													<c:otherwise>
														<tr>
															<td align=right><span class="required"><c:out value="${roleBean.mdaTitle}"/>*</span></td>
															<td align="left"><form:select path="mdaInfo.id">
																	<form:option value="-1"> &lt;Select&gt;</form:option>
																	<c:forEach items="${mdaInfoList}" var="mList">
																		<form:option value="${mList.id}">${mList.name}</form:option>
																	</c:forEach>>
					               	                               </form:select></td>
															<td align="left"></td>
														</tr>

													</c:otherwise>


												</c:choose>

												<tr>
													<td align=right nowrap><span class="required">School
															Name*</span>&nbsp;</td>
													<td><form:input path="name" size="60" maxlength="300" /></td>
												</tr>

												<tr>
													<td align=right nowrap><span class="required">Code
															Name*</span>&nbsp;</td>
													<td><form:input path="codeName" size="30"
															maxlength="20" /></td>
												</tr>

												<tr>
													<td align=right width="35%" nowrap><span
														class="required">Description*</span>&nbsp;</td>
													<td><form:input path="description" size="60"
															maxlength="300" /></td>
												</tr>


												<tr>

													<td>&nbsp;</td>
												</tr>
												<tr>
													<td>&nbsp;</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td class="buttonRow" align="right">
										    <c:choose>
												<c:when test="${saved}">
													<input type="image" name="_cancel" value="cancel"
														title="Close Window" class="" src="images/close.png">
												</c:when>
												<c:otherwise>
													<input type="image" name="submit" value="ok" alt="Ok"	class="" src="images/ok_h.png">&nbsp;
							                        <input type="image" name="_cancel" value="cancel" alt="Cancel"	src="images/cancel_h.png">
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
			<%@ include file="/WEB-INF/jsp/footerFile.jsp"%>
		</tr>
	</table>

</body>
</html>