<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Approve Leave Bonus Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
<script src="<c:url value="/scripts/utility_script.js"/>"></script>
</head>

<body class="main">

	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					<tr>
						<td>
								<div class="title">Approve Leave Bonus</div>
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
						<td class="activeTH">Leave Bonus Details</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">M.D.A</span></td>
									<td width="25%">
										<form:input path="mdaInfo.name" size="50" maxlength="20" disabled="true"/>
									</td>
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Month and Year</span></td>
									<td width="25%">
										<form:input path="runMonthYearStr" size="10" maxlength="20" disabled="true"/>
									</td>
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Created By</span></td>
									<td width="25%">
										<form:input path="createdBy.actualUserName" size="50" maxlength="50" disabled="true"/>										
									</td>
								</tr>
									<c:choose>
										<c:when test="${not miniBean.approved}">
										<tr>
											<td align="right" width="35%" nowrap>
												<span class="required">Total To Pay</span></td>
											<td width="25%">
												<form:input path="totalLeaveBonusStr" size="15" maxlength="15" disabled="true"/>										
											</td>
											</tr>
										</c:when>
										<c:otherwise>
										 <tr>
											<td align="right" width="35%" nowrap>
												<span class="required">Total Paid</span></td>
											<td width="25%">
												<form:input path="totalLeaveBonusStr" size="15" maxlength="15" disabled="true"/>										
											</td>
										 </tr>
										 <tr>
											<td align="right" width="35%" nowrap>
												<span class="required">Approved By</span></td>
											<td width="25%">
												<form:input path="approvedBy.actualUserName" size="15" maxlength="15" disabled="true"/>										
											</td>
										 </tr>
										 <tr>
											<td align="right" width="35%" nowrap>
												<span class="required">Approved Date</span></td>
											<td width="25%">
												<form:input path="approvedDateStr" size="15" maxlength="15" disabled="true"/>										
											</td>
										 </tr>
										</c:otherwise>
										
										
									</c:choose>
									
								
								 <tr>
									<td align="right" width="35%" nowrap>
										<span class="required">No. of Employees</span></td>
									<td width="25%">
										<form:input path="totalNoOfEmp" size="5" maxlength="5" disabled="true"/>										
									</td>
								</tr>
								<c:if test="${miniBean.approving and roleBean.canApproveLeaveBonus}">
								 <tr>
									<td align="left" width="35%" nowrap>
									<span class="required">Generated Code*</span></td>
									<td width="25%">
										<form:input path="generatedCaptcha" size="8" maxlength="8" disabled="true" />
									</td>									
					            </tr>
					            <tr>
									<td align="left" width="35%" nowrap>
									<span class="required">Enter Code Above*</span></td>
									<td width="25%">
										<form:input path="enteredCaptcha" size="8" maxlength="8" />&nbsp;<font color="green">case insensitive.</font>
									</td>									
					            </tr>
					            </c:if>
					           </table>		
					           
					          <br> 					
						</td>
						
					</tr>
					
                                    <tr>
                                    	<td><b>Leave Bonus Details</b></td>
                                    </tr>
	                                    <tr>
											<td>
											<table class="report" cellspacing="0" cellpadding="0">
												<tr class="reportOdd header">
													<td class="tableCell" valign="top" width="10%">Staff ID</td>
													<td class="tableCell" valign="top" width="30%">Employee Name</td>
													<td class="tableCell" valign="top" width="30%">Pay Group Used</td>
													<td class="tableCell" valign="top" width="15%">Level&amp;Step</td>
													<td class="tableCell" valign="top" width="15%">Leave Bonus</td>
													
												</tr>
											</table>
												<div style="overflow:scroll;height:300px;width:100%;overflow:auto">
												<table class="report" cellspacing="0" cellpadding="0">
												<c:forEach items="${miniBean.leaveBonusList}" var="lbList">
												<tr>
													<td class="tableCell" valign="top" width="10%"><c:out value="${lbList.employeeId}"/></td>
													<td class="tableCell" valign="top" width="30%"><c:out value="${lbList.name}"/></td>
													<td class="tableCell" valign="top" width="30%"><c:out value="${lbList.payGroup}"/></td>
													<td class="tableCell" valign="top" width="15%"><c:out value="${lbList.levelAndStep}"/></td>
													<td class="tableCell" valign="top" width="15%"><c:out value="${lbList.leaveBonusAmountStr}"/></td>
													 
													
												</tr>
												</c:forEach>
												
												</table>
												</div>
												<br>
												
											</td>
										</tr>
					<tr>
						<td class="buttonRow" align="right" >
						           <c:choose>
							         	<c:when test="${saved}">
												<input type="image" name="_cancel" value="cancel" title="Close" class="" src="images/close.png">
							         	</c:when>
							         	<c:otherwise>
							         	   <c:if test="${roleBean.superAdmin and roleBean.canApproveLeaveBonus}">
							         			<input type="image" name="submit" value="approve" title="Approve Leave Bonus" class="" src="images/approve.png">&nbsp;
										  </c:if>
											<input type="image" name="_cancel" value="cancel" title="Close" class="" src="images/close.png">
							         	
							         	</c:otherwise>
							         
							         </c:choose>
							         <br>
						
						<div class="reportBottomPrintLink">
											<a href="${appContext}/leaveBonusExcel.do?pid=${miniBean.id}">
										Export to Excel&copy; </a><br />			
			                        </div>
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