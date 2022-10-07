<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="dt"%>

<html>
<head>
<title>Approval</TITLE>
<link rel="stylesheet" href=<c:url value="styles/omg.css"/> type="text/css" />
<link rel="Stylesheet" href=<c:url value="styles/skye.css"/> type="text/css" media ="screen">
 <link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
 <script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
 <script src="<c:url value="/scripts/utility_script.js"/>"></script>
 <link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
</head>
<style>
    .tableDiv{
        max-width: 1200px;
        overflow-x: auto;
    }
    .fixedColumn{
        left: 0;
        position: sticky;
    }
    thead th{
        background-color: #f1f1f1;
    }
</style>
<body class="main">

<table class="main" width="70%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
	<tr>
		<td>

		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

			<tr>

				<td colspan="2">
				<c:choose>
					<c:when test="${approveBean.objectsExist}">
						<div class="title" >Approve Salary Structure</div>

					</c:when>
					<c:otherwise>
					<div class="title" >No Salary Structure found.</div>
					</c:otherwise>
				</c:choose>

				</td>

			</tr>

			<tr >
				<form:form modelAttribute="miniBean">

				<c:set value="${sBean}" var="dispBean" scope="request"/>
				<td valign="top" class="mainBody" id="mainBody">
				<c:if test="${approveBean.hasErrors}">
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
			</c:if>
				To see details
				of the each salary structure, click the Details link. After you click Approve,
				you can now assign the salary structure
				<p><br>
				</p>

					<!--- display the checks --->
							     <div class="tableDiv">
					<table border="0" width="80%" cellpadding="5">
						<tr>
							<td align="left">
							<table  width="90%" cellspacing="1" cellpadding="3">

							    <tr>
							    <table id="sTable" class="hundredPercentWidth table display" cellspacing="0" cellpadding="0">
                                                <thead>
												<tr><th class="tenPercentWidth fixedColumn">Level And Step</th>
													<th class="twentyPercentWidth">Basic Salary</th>
													<th class="twentyPercentWidth">Rent</th>
													<th class="twentyPercentWidth">Transport</th>
													<th class="twentyPercentWidth">Utility</th>
													<th class="twentyPercentWidth">Meal</th>
													<th class="twentyPercentWidth">Motor Vehicle</th>
													<th class="twentyPercentWidth">Exam Allowance</th>
													<th class="twentyPercentWidth">LCons. Allowance</th>
													<th class="twentyPercentWidth">Cons. Allowance</th>
													<th class="twentyPercentWidth">Outfit Allowance</th>
													<th class="twentyPercentWidth">Quarters Allowance</th>
													<th class="twentyPercentWidth">Spa Allowance</th>
													<th class="twentyPercentWidth">Responsibility Allowance</th>
													<th class="twentyPercentWidth">Security Allowance</th>
													<th class="twentyPercentWidth">Swes Allowance</th>
													<th class="twentyPercentWidth">Research Allowance</th>
													<th class="twentyPercentWidth">Totor Allowance</th>
													<th class="twentyPercentWidth">Medical Allowance</th>
													<th class="twentyPercentWidth">Sitting Allowance</th>
													<th class="twentyPercentWidth">Overtime Allowance</th>
													<th class="twentyPercentWidth">Tools Torch Light Allowance</th>
													<th class="twentyPercentWidth">Uniform Allowance</th>
													<th class="twentyPercentWidth">Teaching Allowance</th>
													<th class="twentyPercentWidth">Enhanced Health Allowance</th>
                                                    <th class="twentyPercentWidth">Special Health Allowance</th>
                                                    <th class="twentyPercentWidth">Shift Duty Allowance</th>
                                                    <th class="twentyPercentWidth">Special Allowance</th>
                                                    <th class="twentyPercentWidth">Admin Allowance</th>
                                                    <th class="twentyPercentWidth">Call Duty</th>
                                                    <th class="twentyPercentWidth">Domestic Servant</th>
 													<th class="twentyPercentWidth">Drivers Allowance</th>
 													<th class="twentyPercentWidth">Entertainment</th>
                                                    <th class="twentyPercentWidth">Furniture</th>
                                                    <th class="twentyPercentWidth">Hazard</th>
                                                    <th class="twentyPercentWidth">Inducement</th>
                                                    <th class="twentyPercentWidth">Journal</th>
                                                    <th class="twentyPercentWidth">Nurse Other Allowance</th>
                                                    <th class="twentyPercentWidth">Rural Posting</th>
												</tr>
											</thead>
                                            <tbody>
												<c:forEach items="${sBean}" var="innerBean" varStatus="gridRow">
													<tr>
														<td class="fixedColumn"><c:out value="${innerBean.levelAndStepAsStr}" /></td>
														<td><c:out value="${innerBean.monthlyBasicSalaryStr}" /></td>
														<td><c:out value="${innerBean.rentStr}" /></td>
														<td><c:out value="${innerBean.transportStr}" /></td>
														<td><c:out value="${innerBean.utilityStr}" /></td>
														<td><c:out value="${innerBean.mealStr}" /></td>
														<td><c:out value="${innerBean.motorVehicleStr}" /></td>
														<td><c:out value="${innerBean.examAllowanceStr}" /></td>
														<td><c:out value="${innerBean.lcosAllowanceStr}" /></td>
														<td><c:out value="${innerBean.consAllowanceStr}" /></td>
														<td><c:out value="${innerBean.outfitAllowanceStr}" /></td>
														<td><c:out value="${innerBean.quartersAllowanceStr}" /></td>
														<td><c:out value="${innerBean.spaAllowanceStr}" /></td>
														<td><c:out value="${innerBean.responsibilityAllowanceStr}" /></td>
														<td><c:out value="${innerBean.securityAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.swesAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.researchAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.totorAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.medicalAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.sittingAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.overtimeAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.toolsTorchLightAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.uniformAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.teachingAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.enhancedHealthAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.specialHealthAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.shiftDutyStr}" /></td>
													    <td><c:out value="${innerBean.specialistAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.adminAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.callDutyStr}" /></td>
													    <td><c:out value="${innerBean.domesticServantStr}" /></td>
													    <td><c:out value="${innerBean.driversAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.entertainmentStr}" /></td>
													    <td><c:out value="${innerBean.furnitureStr}" /></td>
													    <td><c:out value="${innerBean.hazardStr}" /></td>
													    <td><c:out value="${innerBean.inducementStr}" /></td>
													    <td><c:out value="${innerBean.journalStr}" /></td>
													    <td><c:out value="${innerBean.nurseOtherAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.ruralPostingStr}" /></td>
													</tr>
												</c:forEach>
											</tbody>

							    </table>
								</tr>
                                                <c:if test="${approveBean.showForConfirm}">
                        							<tr>
                                                <td width="25%">
                                                <p><b>Memo*:</b></p>
                                                		<form:textarea path="approvalMemo" rows="5" cols="35" />
                                                </td>
                                                </tr>
                                                </c:if>
							</table>
							    </div>
							</td>
						</tr>
						<!-- <tr>
							<td align="left">
							&nbsp;
							</td>
						</tr>-->

						<tr>
						    <td>
						        <c:if test="${approveBean.confirmation}">
                        						<div id="topOfPageBoxedErrorMessage" style="display:${miniBean.displayErrors}">
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


                        					            <tr>
                        									<td align="left" width="20%" nowrap>
                        									<span class="required">Generated Code*</span></td>
                        									<td width="60%">
                        										<form:input path="generatedCaptcha" size="8" maxlength="8" disabled="true" />
                        									</td>
                        					            </tr>
                        					            <tr>
                        									<td align="left" width="20%" nowrap>
                        									<span class="required">Enter Code Above*</span></td>
                        									<td width="60%">
                        										<form:input path="enteredCaptcha" size="8" maxlength="8" />&nbsp;<font color="green">case insensitive.</font>
                        									</td>
                        					            </tr>
                        					       </table>
                        	    </c:if>
                        	</td>
                        </tr>
                        <tr>
							<td class="buttonRow" align="right">
								<c:choose>
								<c:when test="${roleBean.superAdmin}">
								<c:choose>
								<c:when test="${approveBean.showForConfirm}">
								 <input type="image" name="_confirm" value="confirm" title="Confirm" src="images/confirm_h.png">&nbsp;
								</c:when>
								<c:otherwise>
								   <input type="image" name="_approve" value="approve" title="Approve Salary Structure" class='' src='images/approve.png' onclick="">
								   <input type="image" name="_reject" value="reject" title="Reject Salary Structure" class="" src="images/reject_h.png">
									 <input type="image" name="_cancel" value="cancel" title="Close" class='' src='images/close.png' onclick="">
								</c:otherwise>
								</c:choose>
								</c:when>
								<c:otherwise>
								  <input type="image" name="_cancel" value="cancel" title="Close" class='' src='images/close.png' onclick="">
								</c:otherwise>
							    </c:choose>
							</td>
						</tr>
					</table><p/>

				<br/>

				</td>


			</tr>
			<tr>
				<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
			</tr>
			</table>

			</td>
			</tr>


		</table>

				</form:form>
<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
	<script type="text/javascript">
		$(function() {

			$("#sTable").DataTable({
				"pageLength": 20,
				//can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
				//also properties higher up, take precedence over those below
				"columnDefs":[
					{"targets": 0, "orderable" : true},
					{"targets": 2, "searchable" : true },
					{"targets": 2, "orderable" : true }
					//{"targets": [0, 1], "orderable" : false }
				]
			});
		});
	</script>
</body>

</html>

