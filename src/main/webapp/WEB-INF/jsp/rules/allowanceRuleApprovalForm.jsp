 <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> PayGroup Allowance Approval Form  </title>
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
								<div class="title">Approve PayGroup Allowance Rule for:
								<c:out value="${miniBean.allowanceRuleMaster.hiringInfo.abstractEmployeeEntity.displayName}"/>
								[ <c:out value="${miniBean.allowanceRuleMaster.hiringInfo.abstractEmployeeEntity.employeeId}"/> ]</div>
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
						<td class="activeTH"><b><c:out value="${miniBean.allowanceRuleMaster.hiringInfo.abstractEmployeeEntity.displayName}"/>'s Pay Group Rule information</b></td>
					</tr>
					<tr>
						<td class="activeTD">
							<table width="100%" border="0" cellspacing="0" cellpadding="2">
								<tr>
						            <td align="right" width="25%"><b><c:out value="${roleBean.staffTitle}"/></b></td>
						              	<td><c:out value="${miniBean.allowanceRuleMaster.hiringInfo.abstractEmployeeEntity.employeeId}"/>
						               </td>
								</tr>
						          <tr>
						            <td align="right" width="25%"><b><c:out value="${roleBean.mdaTitle}"/></b></td>

									     <td width="25%">
						                   <c:out value="${miniBean.mdaDeptMap.mdaInfo.name}"/>
									     </td>

						            </tr>
						          <tr>
						             <td align="right" width="25%"><b>Initiated By :&nbsp;</b></td>

									      <td width="25%">
						                   <c:out value="${miniBean.initiator.actualUserName}"/>
									      </td>

						    		 <td>&nbsp;</td>
						          </tr>
						          <tr>
						            <td align="right" width="25%"><b>Initiated Date :&nbsp;</b></td>

									       <td width="25%">
						                     <c:out value="${miniBean.initiatedDateStr}"/>
									       </td>

						          </tr>
                                   <c:if test="${miniBean.approval}">
						          	<tr>
						               <td align="right" width="25%">Approval Memo* :&nbsp;</td>

									       <td width="25%">
						                     <form:textarea path="approvalMemo" rows="5" cols="35" />
									       </td>

						          </tr>

						          </c:if>
						          <c:if test="${miniBean.rejection}">
						          	<tr>
						               <td align="right" width="25%">Rejection Reason* :&nbsp;</td>

									       <td width="25%">
						                     <form:textarea path="rejectionReason" rows="5" cols="35" />
									       </td>

						          </tr>

						          </c:if>

							</table>
						</td>
						</tr>
					<tr>
                    	 <td class="activeTD">
                            <table width="100%"  class="register1" id="">

                                <thead>
                                    <tr>
                                       <th>Allowance Name</th>
                                       <th>PayGroup Yearly Amount</th>
                                       <th>PayGroup Monthly Amount</th>
                                       <th>Rule Monthly Amount</th>
                                       <th>Rule Yearly Amount</th>

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
								 <c:if test="${miniBean.approval}">
									  <input type="image" name="_approve" value="approve" title="Approve Transfer" class="" src="images/approve.png">
									  <input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
								 </c:if>
							</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
								 <c:if test="${miniBean.rejection}">
									 <input type="image" name="_reject" value="reject" title="Reject Allowance Rule" class="" src="images/reject_h.png">
									  <input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
								 </c:if>
							     <c:if test="${miniBean.approval eq false && miniBean.rejection eq false}">
							         <c:choose>
							         	<c:when test="${saved}">
												<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/close.png">
							         	</c:when>
							         	<c:otherwise>
							         		<input type="image" name="_approve" value="approve" title="Approve Allowance Rule" class="" src="images/approve.png">
											<input type="image" name="_reject" value="reject" title="Reject Allowance Rule" class="" src="images/reject_h.png">
							         	    <input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
							         	</c:otherwise>

							         </c:choose>
							    	 </c:if>


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
