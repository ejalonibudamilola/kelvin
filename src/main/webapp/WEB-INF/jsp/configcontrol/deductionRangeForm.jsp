<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Configure Ranged Deductions  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
 <link rel="stylesheet" href="css/screen.css" type="text/css"/>
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.4.4.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>

</head>


<body class="main">
<script type="text/JavaScript" src="scripts/jacs.js"></script>
<form:form modelAttribute="miniBean">
<c:set value="${displayList}" var="dispBean" scope="request"/>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

					<tr>
						<td colspan="2">
								<div class="title"> Configure Ranged Deduction</div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>

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
						<td class="activeTH">Add Ranged Deduction Values</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="0">
							    <tr>
                            	 <td width="25%" valign="middle" align="right">Name*</td>
                            	 &nbsp;  <td> <form:input path="name" size="15" maxlength="20" disabled="${miniBean.expired or saved}"/> </td>
                            	</tr>
							    <tr>
                            	 <td width="25%" valign="middle">Lower Bound Value*</td>
                            	 &nbsp;  <td> <form:input path="lowerBoundValue" size="7" maxlength="10" /> </td>
                                </tr>
							    <tr>
                            	 <td width="25%" valign="middle">Upper Bound Value*</td>
                            	 &nbsp;  <td> <form:input path="upperBoundValue" size="7" maxlength="10" /> </td>
                                </tr>
							    <tr>
                            	 <td width="25%" valign="middle">Deduction Amount*</td>
                            	 &nbsp;  <td> <form:input path="amountStr" size="7" maxlength="10" /> </td>
                                </tr>
								<td colspan="2" align="left" >
								<br>
								<c:if test ="${action != ''}">
								    <input type="image" name="_add" value="add" title="Add Ranged Deduction detail" src="images/add.png">
                                </c:if>
                                </td>
							</tr>


							</table>

					       <br/>


				</table>

			<c:if test="${displayList.listSize gt 0}">
					<table>
					 <tr>
                                        <td>
                                            &nbsp;
                                        </td>

                                    </tr>
                                    <tr>
                                        <td valign="top">
											<p class="label">Ranged Deduction Details</p>
											<display:table name="dispBean" class="register3" export="false" sort="page" defaultsort="1" requestURI="${appContext}/addRangedDeduction.do">
											<display:column property="lowerBoundStr" title="Lower Bound"></display:column>
											<display:column property="upperBoundStr" title="Upper Bound"></display:column>
											<display:column property="amountStr" title="Deduction Amount"></display:column>
											<c:if test="${not saved}">
                                            	 <display:column property="remove" title="Remove" href="${appContext}/addRangedDeduction.do" paramId="iid" paramProperty="entryIndex"></display:column>
                                             </c:if>
											 <display:setProperty name="paging.banner.placement" value="bottom" />
										</display:table>
										</td>
                                    </tr>

                                    </table>
                                    </c:if>
                                    <tr><td>
                                    <c:choose>
                                        <c:when test="${miniBean.firstTimePay}">
                                             <input type="image" name="_add" value="add" title="Add Ranged Deduction detail" src="images/add.png">
                                        </c:when>
                                        <c:otherwise>
                                          <c:if test="${not miniBean.confirmation and not saved}">
                                             <input type="image" name="_done" value="done" title="Save" src="images/done_h.png">
                                          </c:if>
                                          <c:if test="${miniBean.confirmation}">
                                              <input type="image" name="_confirm" value="confirm" title="Confirm Configuration" src="images/confirm_h.png">
                                           </c:if>
                                        </c:otherwise>
                                    </c:choose>

                                    <%--<c:choose>
                                    <c:when test="${not miniBean.confirmation and not saved}">
                                      <!--<input type="image" name="_done" value="done" title="Save" src="images/done_h.png">-->
                                      <input type="image" name="_add" value="add" title="Add Ranged Deduction detail" src="images/add.png">
                                    </c:when>
                                    <c:otherwise>
                                      <c:if test="${not miniBean.confirmation and saved}">
                                         <input type="image" name="_done" value="done" title="Save" src="images/done_h.png">
                                      </c:if>
                                      <c:if test="${miniBean.confirmation}">
                                          <input type="image" name="_confirm" value="confirm" title="Confirm Configuration" src="images/confirm_h.png">
                                       </c:if>
                                    </c:otherwise>
                                    </c:choose>--%>

                                       <input type="image" name="_cancel" value="cancel" title="Close" src="images/cancel_h.png">
                                       <p>
                                      </td>
                                    </tr>

		</table>
		<!-- <div id="pdfReportLink" class="reportBottomPrintLink">
        										<a href="${appContext}/percentPaymentFormExcelReport.do?">
                                                    Download Excel report </a>
        										<br />

        									</div> -->
		</td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
	</form:form>

</body>

</html>

