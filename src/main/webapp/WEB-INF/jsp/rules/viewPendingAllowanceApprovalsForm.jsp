<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            PayGroup Allowance Rules Awaiting Approval
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="icon"type="image/png" href="<c:url value="/images/coatOfArms.png"/>">
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>

		<link rel="stylesheet" href="styles/notifications.css" type="text/css"/>
		<script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
		<script type="text/javascript"  src="<c:url value="scripts/mouseover_popup.js"/>"></script>

    </head>
    <body class="main">

    <form:form modelAttribute="miniBean">
	<c:set value="${miniBean}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    Pending <c:out value="${roleBean.staffTypeName}"/> PayGroup Allowance Rules
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
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
								<table>
									<tr align="left">
										<td class="activeTH" colspan="2">Filter By</td>

									</tr>
                                    <tr>
                                       <td class="activeTD">
                                            Initiator
                                        </td>
                                        <td class="activeTD">
                                       <form:select path="id">
														<form:option value="0">&lt;Select&gt;</form:option>
														<c:forEach items="${userList}" var="uList">
														<form:option value="${uList.id}">${uList.firstName}&nbsp;${uList.lastName}</form:option>
														</c:forEach>
												</form:select>
                                         </td>
                                    </tr>
                                    <tr>

                                       <td class="activeTD">
                                            <c:out value="${roleBean.staffTitle}"/> :
                                        </td>
                                        <td class="activeTD">
                                            <form:input path="ogNumber" size="9" maxlength="10"/>
                                        </td>
                                    </tr>

                                    <tr>
                                       <td class="activeTD">
                                            Last Name :
                                        </td>
                                        <td class="activeTD">
                                            <form:input path="lastName" size="15" maxlength="20"/>
                                        </td>
                                    </tr>
                                    <c:if test="${miniBean.objectsExist}">
									<tr>
                                    	<td class="buttonRow" align="right">
                                    		 <input type="image" name="_update" value="update" title="Update Report" src="images/Update_Report_h.png">

                                    	</td>
                                     </tr>
                                     </c:if>
								</table>
                                <table class="reportMain" cellpadding="1" cellspacing="1" width="90%">

                                    <tr>
                                        <td valign="top">
											<p class="label"><c:out value="${roleBean.staffTypeName}"/> Pending Allowance Rules</p>
											<display:table name="dispBean" class="register3" export="false" sort="page" defaultsort="1" id = "allowRule" requestURI="${appContext}/viewPendingTransfers.do">

                                              <c:if test="${miniBean.editMode}">
                                             <display:column title="">
												<spring:bind path="miniBean.objectList[${allowRule_rowNum - 1}].rowSelected">
													<input type="hidden" name="_<c:out value="${status.expression}"/>">
                  									<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" class = "check_all" <c:if test="${status.value}">checked</c:if>/>
												</spring:bind>
											</display:column>
											</c:if>

											<display:setProperty name="export.excel.filename" value="PendingPayGroupAllowanceList.xls"/>

											  <display:column title="${roleBean.staffTitle}" media="html" >
											  <c:choose>
											  <c:when test="${miniBean.editMode}">
											   <a href="${appContext}/approveAllowanceRule.do?arid=${allowRule.id}" class="popper" data-popbox="pop${allowRule.id}">
                              			         	${allowRule.allowanceRuleMaster.hiringInfo.employee.employeeId}
                              			        </a>
                              			       </c:when>
                              			       <c:otherwise>
                              			           <c:out value="${allowRule.allowanceRuleMaster.hiringInfo.employee.employeeId}"/>
                              			       </c:otherwise>
                              			       </c:choose>
												<div id="pop${allowRule.id}" class="popbox">
												       <h2><c:out value="${roleBean.staffTypeName}"/> Information</h2>
												       <table>
												    	<tr>
												    		<td><b>Title:</b></td>
												    		<td>${allowRule.allowanceRuleMaster.hiringInfo.employee.title.name}
												    		 </td>
												    	</tr>
												    	<tr>
												    		<td><b><c:out value="${roleBean.staffTypeName}"/> Name:</b></td>
												    		<td>${allowRule.allowanceRuleMaster.hiringInfo.employee.displayName}
												    		 </td>
												    	</tr>

												    	<tr>
												    		<td><b>Current <c:out value="${roleBean.mdaTitle}"/>:</b></td>
												    		<td>${allowRule.allowanceRuleMaster.hiringInfo.employee.currentMdaName}</td>
												    	</tr>


												    	<tr>
												    		<td><b>Current Salary Designation:</b></td>
												    		<td>${allowRule.allowanceRuleMaster.hiringInfo.employee.salaryInfo.salaryScaleLevelAndStepStr}</td>
												    	</tr>
												    	<tr>
												    		<td><b>Initiated By:</b></td>
												    		<td>${allowRule.initiator.actualUserName}</td>
												    	</tr>
												    	<tr>
												    		<td><b>Initiated Date:</b></td>
												    		<td>${allowRule.initiatedDateStr}</td>
												    	</tr>

												    	<tr>
												    		<td><b>Approval Status:</b></td>
												    		<td>${allowRule.approvalStatusStr}</td>
												    	</tr>

												    	<tr>
												    		<td><b>Approved By:</b></td>
												    		<td>${allowRule.approver.actualUserName}</td>
												    	</tr>

												    	<tr>
												    		<td><b>Approval Date:</b></td>
												    		<td>${allowRule.rejectionDateStr}</td>
												    	</tr>

												    	<tr>
												    		<td><b>Approval Memo:</b></td>
												    		<td>${allowRule.rejectionReason}</td>
												    	</tr>


												    </table>
	                              				</div>
											</display:column>
											<display:column property="allowanceRuleMaster.hiringInfo.employee.employeeId" title="${roleBean.staffTitle}" media="excel"/>
											<display:column property="allowanceRuleMaster.hiringInfo.employee.displayName" title="${roleBean.staffTypeName} Name"></display:column>
											<display:column property="allowanceRuleMaster.hiringInfo.employee.currentMdaName" title="${roleBean.mdaTitle}"></display:column>
											<display:column property="allowanceRuleMaster.hiringInfo.employee.salaryInfo.salaryScaleLevelAndStepStr" title="Pay Group"></display:column>
										   <display:column property="initiator.actualUserName" title="Initiated By"></display:column>

										    <display:column property="initiatedDateStr" title="Transfer Date (DD/MM/YYYY)"></display:column>

											<display:setProperty name="paging.banner.placement" value="bottom" />
										</display:table>
										</td>
                                    </tr>
                                    <tr style = "${miniBean.showRow}">
                                    <td>
                                    <table>
                                    <c:if test="${miniBean.addWarningIssued}">
                                       <tr align = "left">
                                    	<td align=right width="25%"><b><c:out value="${miniBean.memoType}"/> Memo*</b></td>

									       <td width="75%" align = "left">
						                     <form:textarea path="approvalMemo" rows="5" cols="35" disabled="${saved}"/>
									       </td>
                                    </tr>
                                    </c:if>
                                    </table>
                                    </td>
                                    </tr>
                                    <tr>

                                    </tr>
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                     	<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/close.png">
                                     	<c:if test="${roleBean.superAdmin}">
                                     	<c:if test="${not miniBean.showLink}">
                                     	  <c:choose>
                                     	   <c:when test="${not miniBean.addWarningIssued}">
                                     	      <c:if test="${miniBean.objectsExist}">
                                                  <input type="image" name="_approve" value="approve" title="Approve Selected Allowance Rules" class="" src="images/approve.png">
                                                  <input type="image" name="_reject" value="reject" title="Reject Selected Allowance Rules" class="" src="images/reject_h.png">
	                                          </c:if>
	                                       </c:when>
	                                       <c:otherwise>
                                              <c:choose>
                                                 <c:when test="${not miniBean.rejection}">
                                                   <c:if test="${miniBean.objectsExist}">
                                                    <input type="image" name="_confirm" value="confirm" title="Confirm to 'Approve All Selected Allowance Rules'" class="" src="images/confirm_h.png">
                                                   </c:if>
                                                 </c:when>
                                                  <c:otherwise>
                                                  <c:if test="${miniBean.objectsExist}">
                                                    <input type="image" name="_reject" value="reject" title="Reject Selected Allowance Rules" class="" src="images/reject_h.png">
                                                  </c:if>
                                                  </c:otherwise>
                                                  </c:choose>
	                                       </c:otherwise>
	                                       </c:choose>
	                                     </c:if>
                                          </c:if>
                                        </td>
                                    </tr>

                                </table>

                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
            </tr>
        </table>
        </form:form>
    </body>
</html>
