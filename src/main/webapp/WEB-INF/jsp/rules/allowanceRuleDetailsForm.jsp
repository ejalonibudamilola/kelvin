<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Allowance Rule Details Form  </title>
<link rel="stylesheet" href=<c:url value="styles/omg.css"/> type="text/css" />
<link rel="Stylesheet" href=<c:url value="styles/skye.css"/> type="text/css" media ="screen">
<script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
<script src="<c:url value="/scripts/utility_script.js"/>"></script>
</head>

<body class="main">
	<table class="main" width="70%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

					<tr>
						<td colspan="2">
								<div class="title">PayGroup Allowance Rule Details for: <c:out value="${miniBean.allowanceRuleMaster.hiringInfo.employee.displayName}"/> [ <c:out value="${miniBean.allowanceRuleMaster.hiringInfo.employee.employeeId}"/> ]</div>
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
					<tr align="left">
						<td class="activeTH"><b><c:out value="${miniBean.allowanceRuleMaster.hiringInfo.employee.displayName}"/>'s Allowance Rule information</b></td>
					</tr>
					<tr>
						<td class="activeTD">
							<table width="100%" border="0" cellspacing="0" cellpadding="2">

						          <tr>
						            <td align="right" width="25%"><b>Allowance Rule Date :</b></td>

									       <td width="25%">
						                     <c:out value="${miniBean.initiatedDateStr}"/>
									       </td>

						          </tr>
						          <tr>
						            <td align="right" width="25%"><b>Rejected By :</b></td>

									       <td width="25%">
						                     <c:out value="${miniBean.approvedBy.actualUserName}"/>
									       </td>

						          </tr>
						          <tr>
						            <td align="right" width="25%"><b>Rejection Date :</b></td>

									       <td width="25%">
						                     <c:out value="${miniBean.rejectionDateStr}"/>
									       </td>

						          </tr>

						          <tr>
						               <td align="right" width="25%"><b>Rejection Reason* :</b></td>

									       <td width="25%">
						                     <form:textarea path="rejectionReason" rows="5" cols="35" disabled="true"/>
									       </td>

						          </tr>


							</table>
								</td>
         					</tr>
         					<tr>
         					<td class="activeTD">
                            <table width="100%"  class="register1" id="">

                                <thead>
                                    <tr>
                                       <th>Allowance Name</th>
                                       <th>Yearly Amount</th>
                                       <th>Monthly Amount</th>
                                       <th>Rule Monthly Amount</th>
                                       <th>Rule Yearly Amount</th>
                                       </c:if>
                                    </tr>
                                </thead>
                                <tbody>

                                       <c:forEach items="${miniBean.allowanceRuleMaster.allowanceRuleDetailsList}" var="myList" varStatus="gridRow">
                                        <tr>
                                            <td>${myList.beanFieldName}</td>
                                            <td>${myList.yearlyValueStr}</td>
                                            <td>${myList.monthlyValueStr}</td>
                                            <td>${myList.applyMonthlyValueStr}</td>
                                            <td>${myList.applyYearlyValueStr}</td>
                                        </tr>
                                        </c:forEach>
                                </tbody>
                            </table>
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
							 <c:choose>
								 <c:when test="${not saved}">
									 <input type="image" name="_delete" value="delete" title="Delete Transfer" class="" src="images/delete_h.png">
									  <input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
								 </c:when>
							     <c:otherwise>

									 <input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/close.png">

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
