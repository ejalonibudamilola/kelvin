<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> <c:out value="${pageTitle}" />  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
		<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
		<script type="text/javascript" src="<c:url value="scripts/jquery-1.4.4.min.js"/>"></script>
		<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>
</head>


<body class="main">

	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					

					<tr>
						<td colspan="2">
								<div class="title">Add New <c:out value="${pageTitle}"/> </div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						<font color="red"><b>* = required</b></font><br/><br/>
				<form:form modelAttribute="employee">
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="employee">
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
						<td class="activeTH"><c:out value="${mainHeader}" /></td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
                            <tr>
									<td align=right width="25%">
										<span class="required">NIN</span></td>
									<td width="25%">
										 <form:input path="nin"/>
									</td>

					         </tr>
                             <tr>
									<td align=right width="25%">
										<span class="required">Residence ID</span></td>
									<td width="25%">
										 <form:input path="residenceId"/>
									</td>

					         </tr>
							<tr>
									<td align=right width="25%">
										<span class="required"><c:out value="${roleBean.mdaTitle}"/></span></td>
									<td width="25%" nowrap>
										<form:input path="parentObjectName" disabled="true" size="30"/>
									</td>
									
					         </tr>

					         <tr>
									<td align=right width="25%">
										<span class="required">Pay Group</span></td>
									<td width="25%">
										<form:input path="salaryInfo.salaryScaleLevelAndStepStr" disabled="true" size="30"/>
									</td>
									
					         </tr>
					         <tr>
									<td align=right width="25%">
										<span class="required">Basic Monthly Salary</span></td>
									<td width="25%">
									    <form:input path="salaryInfo.monthlyBasicSalaryStr" disabled="true"/>
									</td>
									
					         </tr>
					         <tr>
									<td align=right width="25%">
										<span class="required">Monthly Gross</span></td>
									<td width="25%">
										 <form:input path="salaryInfo.totalMonthlySalaryStr" disabled="true"/>
									</td>
									
					         </tr>
					         <tr>
									<td align=right width="25%">
										<span class="required">Annual Gross</span></td>
									<td width="25%">
										 <form:input path="salaryInfo.totalGrossPayStr" disabled="true"/>
									</td>
									
					         </tr>
					        <c:if test="${roleBean.localGovt}">
					          <tr>
                             	<td align=right><span class="required">File No.*</span></td>
                             		<td width="25%">
                             			<form:input path="fileNo" size="10" maxlength="10" />
                             				</td>
                             </tr>
                             <tr>
                             	 <td align=right width="25%">
                             		 <span class="required">Cadre*</span></td>
                             	 <td width="25%">
                             		 <form:input path="rank.cadre.name" size="25" maxlength="25" disabled="true"/>
                             	 </td>

                              </tr>
                               </c:if>
                                <tr>
                                  <td align=right width="25%">
                                    <span class="required">Rank*</span></td>
                                   <td width="25%">
                                     <form:input path="rank.description" size="32" disabled="true"/> </td>
                                </tr>
					         <tr>
									<td align=right><span class="required"><c:out value="${roleBean.staffTitle}"/></span></td>
									<td width="25%">
										<form:input path="employeeId" disabled="true"/>
									</td>
								
					            </tr>
					         <tr>
									<td align=right width="25%"><span class="required">Staff Name*</span></td>
									<td width="35%">
										<form:input path="displayName" style="width:100%" disabled="true"/>
									</td>
									
					            </tr>
								 
								
							  <tr>
					              <td align=right><span class="required">Title*</span></td>
					              <td align="left">   
					                <form:select path="titleId">
					                <form:option value="-1" >&lt;Select Title&gt;</form:option>
					                <c:forEach items="${titleList}" var="tList">
					                	<form:option value="${tList.id}" >${tList.name}</form:option>
					                </c:forEach>
					                </form:select>
					              </td>
					              <td align="left"></td>
					            </tr>

					            <tr>
					              <td align=right><span class="required"><c:out value="${roleBean.staffTypeName}"/> Type*</span></td>
					              <td align="left">   
					                <form:select path="employeeType.id">
					                <form:option value="-1" >&lt;Select Type&gt;</form:option>
					                <c:forEach items="${empTypes}" var="employeeType">
					                	<form:option value="${employeeType.id}" title="${employeeType.description}" >${employeeType.name}</form:option>
					                </c:forEach>
					                </form:select>
					              </td>
					              <td align="left"></td>
					            </tr>

					            <c:if test="${employee.schoolEnabled}">
						         	<tr>
						             	<td align=right><span class="required">School</span></td>
					              		<td align="left">   
					                		<form:select path="schoolInstId">
					               			 	<form:option value="-1" >&lt;Select School&gt;</form:option>
					               				 <c:forEach items="${schoolList}" var="schools">
					                				<form:option value="${schools.id}" title="${schools.description}">${schools.name}</form:option>
					               				 </c:forEach>
					               			 </form:select>
					              </td>
					              <td align="left"></td>
						               	</tr>
						         
						         </c:if>
					            <tr>
					              <td align=right><span class="required">Address*</span></td>
					              <td width="25%"><form:input path="address1" size="40" maxlength="50"/></td>
					            </tr>
					            <!--
					            <tr>
					              <td align="right"></td>
					              <td width="25%"><form:input path="address2"/></td>
					            </tr>
					            -->
					            <tr>
                                  <td align=right width="25%"> <span class="required"> Residence City*</span></td>
                                    <td width="25%"> <form:select path="cityId" onchange="loadStateByCity(this)">
                                                      <form:option value="-1" >&lt;Select City&gt;</form:option>
                                                        <c:forEach items="${cities}" var="cityList">
                                                           <form:option value="${cityList.id}" title="${cityList.name}" >${cityList.name}</form:option>
                                                         </c:forEach>
                                                     </form:select>
                                      </td>

                                </tr>
					            <tr>
                                     <td align=right><span class="required">Residence State*</span></td>
                                	<td>
                                		<form:select path="stateInstId" id="state-control" cssClass="stateControls">
                                		    <form:option value="-1">&lt;Select a State&gt;</form:option>
                                				<c:forEach items="${statesList}" var="stateInfo">
                                					<form:option value="${stateInfo.id}" >${stateInfo.name}</form:option>
                                			    </c:forEach>
                                		</form:select>
                                	</td>
                                	<td align="left"></td>
                                </tr>
                                <tr>
                                      <td align=right><span class="required">State Of Origin*</span></td>
                                 	<td>
                                 		<form:select path="stateOfOriginId" onchange='loadLGAByStateId(this)'>
                                 		    <form:option value="-1">&lt;Select a State&gt;</form:option>
                                 				<c:forEach items="${statesOfOriginList}" var="sList">
                                 					<form:option value="${sList.id}" >${sList.name}</form:option>
                                 			    </c:forEach>
                                 		</form:select>
                                 	</td>
                                 	<td align="left"></td>
                                 </tr>
					            <tr>
					              <td align=right><span class="required">Local Govt. Area*</span></td>
					              <td align="left">   
					                <form:select path="lgaId" id="lga-control" cssClass="lgaControls">
					                <form:option value="-1" >&lt;Select LGA&gt;</form:option>
					                <c:forEach items="${LGAList}" var="lgaList">
					                	<form:option value="${lgaList.id}" >${lgaList.name}</form:option>
					                </c:forEach>
					                </form:select>
					              </td>
					              <td align="left"></td>
					            </tr>
					            <tr>
					              <td align=right><span class="required">Religion*</span></td>
					              <td align="left">   
					                <form:select path="relId">
					                <form:option value="-1" >&lt;Select Religion&gt;</form:option>
					                <c:forEach items="${religionList}" var="relList">
					                	<form:option value="${relList.id}" >${relList.name}</form:option>
					                </c:forEach>
					                </form:select>
					              </td>
					              <td align="left"></td>
					            </tr>
					            <c:if test="${roleBean.pensioner}">
                                <tr>
					              <td align=right><span class="required">Pensioner Entrant Status*</span></td>
					              <td align="left">
					                <form:select path="mapId">
					                <form:option value="0" >&lt;Select Status&gt;</form:option>
					                <c:forEach items="${entrantList}" var="elList">
					                	<form:option value="${elList.noOfEmp}" >${elList.name}</form:option>
					                </c:forEach>
					                </form:select>
					              </td>
					              <td align="left"></td>
					            </tr>
                                <tr>
					              <td align=right><span class="required">Entry Month*</span></td>
					              <td align="left">
					                <form:select path="objectInd">
					                <form:option value="0" >&lt;Select Month&gt;</form:option>
					                <c:forEach items="${entryMonthList}" var="mlList">
					                	<form:option value="${mlList.noOfEmp}" >${mlList.name}</form:option>
					                </c:forEach>
					                </form:select>
					              </td>
					              <td align="left"></td>
					            </tr>
					            </c:if>
					            <tr>
					              <td align=right width="25%"><span class="required">GSM Number</span></td>
					               <td width="25%"><form:input path="gsmNumber" size="15" maxlength="13" />
					               <span class="note"> (0##########)</span>
					               </td>
					              </tr>
					            <tr>
					              <td align=right width="25%"><span class="required">Email Address</span></td>
					               <td width="25%"><form:input path="email" size="40" maxlength="40"/></td>
					              
					             </tr>
							</table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
							<input type="image" name="submit" value="ok" title="Submit" class="" src="images/ok_h.png">
							<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
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

