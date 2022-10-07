<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
        <link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
        <link rel="stylesheet" href="<c:url value="/styles/epayroll.css"/>" type="text/css"/>
        <link rel="stylesheet" href="css/screen.css" type="text/css"/>
        <link rel="stylesheet" href="css/jquery-ui.css">
        <link rel="stylesheet" href="dataTables/css/jquery.dataTables.min.css" type="text/css">

        <title>Pay Group Allowance Rule Form</title>
    </head>
    <style>
        .btn-success{
            background-color:#326032 !important;
        }
        .btn-sm{
            font-size:10px !important;
            line-height:1 !important;
        }
        .toggle.btn-sm {
            min-height: 22px !important;
        }
        .register1 {background: #9BA9B3; width:900px; margin: 0 10px 10px 10px}
        .register1 thead th  {background: #EBF6FD; font-size:8pt; padding:10px; font-weight:bold}
        .register1 tr:nth-child(even) {background: #EBF6FD; font-size:8pt}
        .register1 tr:nth-child(odd) {background: white; font-size:8pt}
        .tableDiv{
            margin-top:2%;
           /* overflow-y: auto;
            overflow-x: hidden;
            */
            height: 500px;
        }
        .tableDiv thead th{
            position: sticky;
            top: 0;
            z-index:1;
        }

        .tableDiv tbody td{
            z-index:0;
        }

    </style>
    <body class="main">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
        <form:form modelAttribute="miniBean">
        <c:set value="${miniBean}" var="dispBean" scope="request"/>
        <table class="main" width="75%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
               <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
               <tr>
                  <td>
                  <br/>
                     <div style="color:red; padding: 4px 10px" class="title"><c:out value = "${pageTitle}"/></div>

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

                  <div class="tableDiv">
				<table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
					<tr align="left">
						<td class="activeTH"><c:out value="${roleBean.staffTypeName}"/> Information</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align="left" width="25%">
										<span class="required"><c:out value="${roleBean.staffTypeName}"/> Name</span></td>
									<td width="35%">
										<c:out value="${miniBean.name}"/>
									</td>

					            </tr>
								<tr>
									<td align="left" width="25%"><span class="required"><c:out value="${roleBean.staffTitle}"/></span></td>
									<td width="35%">
										<c:out value="${miniBean.employeeId}"/>
									</td>

					            </tr>
					            <tr>
									<td align="left" width="25%"><span class="optional"><c:out value="${roleBean.mdaTitle}"/></span></td>
									<td width="35%">
										<c:out value="${miniBean.assignedToObject}"/>
									</td>
					            </tr>
					           	<tr>
									<td align="left" width="25%"><span class="required">Hire Date</span></td>
									<td width="35%">
										<c:out value="${miniBean.hireDate}"/>
									</td>

					            </tr>
								<tr>
									<td align="left" width="25%"><span class="required">Years of Service</span></td>
									<td width="35%">
										<c:out value="${miniBean.yearsOfService}"/>
									</td>
					            </tr>
								<tr>
									<td align="left" width="25%"><span class="required">Pay Group</span></td>
									<td width="35%">
										<c:out value="${miniBean.gradeLevelAndStep}"/>
									</td>
					            </tr>
					            <c:choose>
					            <c:when test="${miniBean.forSuccessDisplay}">
                                      <tr>
                                        <td align="left" width="25%">Rule Effective Period</td>
                                        <td width="15%"><c:out value="${miniBean.endDateString}"/></td>
                                      </tr>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td align="left" width="25%">Start Date*</td>
                                        <td width="15%"><form:input path="arrearsStartDate" /> <img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date"onclick="JACS.show(document.getElementById('arrearsStartDate'),event);"></td>
                                      </tr>
                                      <tr>
                                         <td align="left" width="25%">End Date*</td>
                                         <td width="15%"><form:input path="arrearsEndDate" /> <img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('arrearsEndDate'),event);"></td>
                                      </tr>
                                  </c:otherwise>
                                  </c:choose>
                                    <c:if test="${miniBean.confirmation}">
									<tr>
										<td width="25%" align="left">Generated Captcha :</td>
										<td width="25%" align="left" colspan="2"><c:out value="${miniBean.generatedCaptcha}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Entered Captcha :</td>
										<td width="25%" align="left" colspan="2"><form:input path="enteredCaptcha" size="5" maxlength="6"/>
										&nbsp;<font color="green">Case Insensitive</font></td>
										<c:if test="${miniBean.captchaError}">
										  &nbsp;<font color="red"><b>Entered Captcha does not match!</b></font>
										</c:if>
									</tr>
									</c:if>
					 </table>
					 </td>
					 </tr>
					 <tr>
                     	<td class="activeTD">
					 <c:if test="${miniBean.showDetails}">
                    <table width="90%"  class="register1" id="">
                        <c:choose>
                            <c:when test="${saved}">
                                <thead>
                                    <tr>
                                       <th>Allowance Name</th>
                                       <th>Yearly Amount</th>
                                       <th>Monthly Amount</th>
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
                            </c:when>
                            <c:otherwise>
                                <thead>
                                     <tr>
                                        <th>Allowance Name</th>
                                        <th>Yearly Amount</th>
                                        <th>Monthly Amount</th>
                                        <th>Rule Monthly Amount</th>
                                     </tr>
                                 </thead>
                                 <tbody>
                                   <c:forEach items="${miniBean.allowanceRuleMaster.allowanceRuleDetailsList}" var="myList" varStatus="gridRow">
                                    <tr>
                                         <td>${myList.beanFieldName}</td>
                                         <td>${myList.yearlyValueStr}</td>
                                         <td>${myList.monthlyValueStr}</td>
                                         <td>
                                            <spring:bind path="miniBean.allowanceRuleMaster.allowanceRuleDetailsList[${gridRow.index}].amountStr">
                                               <input type="text" name="${status.expression}" value="${status.value}"/>
                                            </spring:bind>
                                        </td>
                                    </tr>
                                    </c:forEach>
                               </tbody>
                            </c:otherwise>
                            </c:choose>

                    </table>
                    </c:if>
                    </table>
                    </div>
                    <tr>
                     <td class="buttonRow">
                    <c:choose>

                            <c:when test="${miniBean.confirmation}">
                                <input type="image" name="_confirm" value="confirm" title="Confirm" src="images/confirm_h.png">
                                <input type="image" name="_cancel" value="cancel" title="Close" src="images/close.png">
                            </c:when>
                            <c:otherwise>
                               <c:choose>
                                <c:when test="${not saved}">
                                   <c:if test="${miniBean.editMode}">
                                    <input type="image" name="_updateReport" value="update" title="Update" src="images/update.png">
                                    </c:if>
                                    <c:if test="${not miniBean.editMode}">
                                      <input type="image" name="_updateReport" value="update" title="Update" src="images/create_h.png">
                                    </c:if>
                                    <c:if test="${miniBean.deletable}">
                                        <input type="image" name="_delete" value="delete" title="Expire" src="images/delete_h.png">
                                    </c:if>
                                     <input type="image" name="_cancel" value="cancel" title="Close" src="images/close.png">
                                </c:when>
                                <c:otherwise>
                                      <input type="image" name="_cancel" value="cancel" title="Close" src="images/close.png">
                                </c:otherwise>
                                </c:choose>
                    </c:otherwise>


                    </c:choose>

                  </td>
               </tr>


        <tr>
            <%@ include file="/WEB-INF/jsp/footerFile.jsp"%>
        </tr>
        </form:form>
    </body>