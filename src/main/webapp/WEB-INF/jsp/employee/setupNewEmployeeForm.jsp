<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Setup <c:out value="${roleBean.staffTypeName}"/> Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
<script src="<c:url value="/scripts/utility_script.js"/>"></script>
</head>


<body class="main">
<form:form modelAttribute="miniBean">


	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					


					<tr>
						<td colspan="2">
								<div class="title">Setup <c:out value="${roleBean.staffTypeName}"/> </div>
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
				<table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" >
					<tr align="left">
						<td class="activeTH">Setup <c:out value="${roleBean.staffTypeName}"/></td>
					</tr>
					<tr>
						<td class="activeTD">
							<table width="504" border="0" cellpadding="2" cellspacing="0">
							 <tr>
                                  <td width="273" align=right><span class="required">
                                   Hiring <c:out value="${roleBean.mdaTitle}"/>*</span></td>
                                   <td width="223" align="left">
                                   <form:select path="mdaId" onchange='loadDepartments(this)'>
                                      <form:option value="-1" >&lt;Select&gt;</form:option>
                                          <c:forEach items="${mdaList}" var="mList">
                                              <form:option value="${mList.id}">${mList.name}</form:option>
                                          </c:forEach>
                                      </form:select>
                                    </td>
                               </tr>
                               <tr>
                                  <td height="24" align=right><span class="required">Department*</span>

                                  </td>
                                    <td align="left">
                                       <form:select path="deptId" id="department-control" cssClass="branchControls">
                                          <form:option value="-1" >&lt;Select&gt;</form:option>
                                              <c:forEach items="${departmentList}" var="dList">
                                                   <form:option value="${dList.id}">${dList.name}</form:option>
                                               </c:forEach>
                                                </form:select>
                                     </td>
                                </tr>
                                <c:if test="${roleBean.pensioner}">
                                <tr>
                                  <td width="273" align=right><span class="required">
                                   Service Organization*</span></td>
                                   <td width="223" align="left">
                                   <form:select path="parentClientId" onchange='loadRanksByParentClientId(this)'>
                                      <form:option value="-1" >&lt;Select&gt;</form:option>
                                          <c:forEach items="${parentClientList}" var="mList">
                                              <form:option value="${mList.parentBusinessClient.id}">${mList.parentBusinessClient.name}</form:option>
                                          </c:forEach>
                                      </form:select>
                                    </td>
                               </tr>

                                </c:if>
								<c:choose>
								<c:when test="${roleBean.localGovt}">
								  <tr>
                                  	 <td height="24" align="right"><span class="required"><c:out value="${roleBean.staffTypeName}"/> Cadre*</span></td>
                                  		 <td align="left">
                                  			<form:select path="cadreInstId"  onchange="loadRanksByCadreId(this)">
                                  			   <form:option value="-1" >&lt;Select&gt;</form:option>
                                  				 <c:forEach items="${cadreList}" var="cList">
                                  					<form:option value="${cList.id}" title="${cList.description}">${cList.name}</form:option>
                                  				 </c:forEach>
                                  			</form:select>
                                         </td>
                                  </tr>
                                  <tr>
                                  	  <td height="24" align="right"><span class="required"><c:out value="${roleBean.staffTypeName}"/> Rank*</span></td>
                                  		<td align="left">
                                  			<form:select path="rankInstId" id="rank-control" cssClass="rankControls" onchange="loadSalaryTypeLevelAndStepByRankId(this)">
                                  				<form:option value="-1" >&lt;Select&gt;</form:option>
                                  				<c:forEach items="${rankList}" var="rList">
                                  				<form:option value="${rList.id}" title="${rList.description}">${rList.name}</form:option>
                                  				</c:forEach>
                                  			</form:select>
                                  		</td>
                                  </tr>
                                  <tr>
                                  		<td align=right><span class="required">Level and Step*</span></td>
                                  			<td align="left">
                                  			<form:select path="salaryStructureId" id="levelStep-lga-control" cssClass="salaryTypeControls">
                                  			<form:option value="-1" >&lt;Select&gt;</form:option>
                                  				<c:forEach items="${salaryStructureList}" var="sList">
                                  					<form:option value="${sList.id}">${sList.levelStepStr}</form:option>
                                  				</c:forEach>
                                  			</form:select>
                                  			</td>
                                  			<td align="left"></td>
                                  </tr>
                                  	<tr>
                                  		<td align=right><span class="required">File No.*</span></td>
                                  			<td width="25%">
                                  			<form:input path="fileNo"/>
                                  			</td>

                                  	</tr>
								</c:when>
								<c:otherwise>
                                   <tr>
                                       <td height="24" align="right"><span class="required">Rank*</span></td>
                                           <td align="left">
                                             <form:select path="rankInstId" id="pen-rank-control" onchange='loadSalaryTypeByRankId(this)'>
                                                <form:option value="-1" >&lt;Select&gt;</form:option>
                                                   <c:forEach items="${rankList}" var="rList">
                                                      <form:option value="${rList.id}" title="${rList.description}">${rList.name}</form:option>
                                                   </c:forEach>
                                              </form:select>
                                           </td>
                                   </tr>
                                   <tr>
                                    	<td height="24" align=right><span class="required">Pay Group*</span></td>
                                    	<td align="left">
                                    	 <form:select path="salaryTypeId" id="salary-type-control" cssClass="salaryTypeControls" onchange="loadSalaryLevelAndStepBySalaryTypeId(this);">
                                    		<form:option value="-1" >&lt;Select&gt;</form:option>
                                    			<c:forEach items="${salaryTypeList}" var="sTypeList">
                                    		        <form:option value="${sTypeList.id}" title="${sTypeList.description}">${sTypeList.name}</form:option>
                                    			</c:forEach>
                                    	</form:select>
                                    	</td>
                                   </tr>

                                   <tr>
                                       <td align=right><span class="required">Level and Step*</span></td>
                                        <td align="left">
                                          <form:select path="salaryStructureId" id="levelStep-control" cssClass="salaryTypeControls">
                                            <form:option value="-1" >&lt;Select&gt;</form:option>
                                               <c:forEach items="${salaryStructureList}" var="sList">
                                                  <form:option value="${sList.id}">${sList.levelStepStr}</form:option>
                                                </c:forEach>
                                          </form:select>
                                        </td>
                                         <td align="left"></td>
                                    </tr>
								</c:otherwise>

								</c:choose>

								<tr>
									<td align=right><span class="required"><c:out value="${roleBean.staffTitle}"/>*</span></td>
									<td width="25%">
										<form:input path="employeeId" readonly="true" />
									</td>
								
					            </tr>
                                <tr>
                                	 <td align=right width="25%">
                                     <span class="required"><c:out value="${miniBean.ninStr}"/></span></td>
                                		 <td width="25%">
                                	 <form:input path="nin" maxLength = "11"/>
                                	 </td>

                                 </tr>

                                 <tr>
                                	 <td align=right width="25%">
                                     <span class="required"><c:out value="${miniBean.residenceIdStr}"/></span></td>
                                		 <td width="25%">
                                	 <form:input path="residenceId" maxLength = "9"/>
                                	 </td>

                                 </tr>

								 <tr>
									<td align=right width="25%"><span class="required">Last Name*</span></td>
									<td width="25%">
										<form:input path="lastName" readonly="true" />
									</td>
									
					            </tr>
								<tr>
									<td align=right width="25%">
										<span class="required">First Name*</span></td>
									<td width="25%">
										<form:input path="firstName" readonly="true" />
									</td>
									
					            </tr>

					            <tr>
									<td align=right width="25%"><span class="optional">Middle Name</span></td>
									<td width="25%">
										<form:input path="middleName" readonly="true" />
									</td>
					            </tr>
					           
								
								 </table>					
					    </td>
								</tr>
						  </table>							
						</td>
					</tr>

					<tr>
						<td class="buttonRow" align="right">
						    <input type="image" name="submit" value="ok" title="Proceed" class="" src="images/ok_h.png">
							<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
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

