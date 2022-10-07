<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Demotion/Pay Group Change Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
 <link rel="stylesheet" href="styles/epayroll.css" type="text/css"/>
 <script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
 <script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
<script src="<c:url value="/scripts/utility_script.js"/>"></script>
 
</head>


<body class="main">
<script type="text/JavaScript" src="scripts/jacs.js"></script>
<form:form modelAttribute="miniBean">
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					

					<tr>
						<td colspan="2">
								 <div class="title">Demote or Change Pay Group for :
								 <br><c:out value="${miniBean.employee.displayNameWivTitlePrefixed }"/>
								  </div>
						 
						 
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
                                    <td class="activeTH">Demotion/Pay Group Change Module</td>
                                </tr>
                                <tr>
                                    <td class="activeTD">
                                        <table border="0" cellspacing="0" cellpadding="2">

                                            <tr>
                                                <td align=right><span class="required"><b><c:out value="${roleBean.staffTitle}"/> :</b></span></td>
                                                <td width="25%">
                                                    <b><font color="blue"><c:out value="${miniBean.employee.employeeId}"/></font></b>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td align=right width="25%">
                                                    <span class="required"><b><c:out value="${roleBean.staffTypeName}"/> Name :</b></span></td>
                                                <td width="25%">
                                                    <b><font color="blue"><c:out value="${miniBean.employee.displayNameWivTitlePrefixed}"/></font></b>
                                                </td>

                                            </tr>

                                             <tr>
                                                <td align=right width="25%"><span class="required"><b>Current <c:out value="${roleBean.staffTypeName}"/> :</b></span></td>
                                                <td width="25%" nowrap>
                                                    <c:out value="${miniBean.employee.currentMdaName}"/>
                                                </td>
                                            </tr>
                                            <c:if test="${miniBean.employee.schoolStaff}">
                                                 <tr>
                                                    <td align=right width="25%"><span class="required"><b>Current School :</b></span></td>
                                                    <td width="25%" nowrap>
                                                        <c:out value="${miniBean.employee.schoolName}"/>
                                                    </td>
                                                </tr>
                                            </c:if>
                                            <c:choose>
                                                <c:when test="${saved}">

                                                   <c:if test="${roleBean.localGovt}">
                                                   <tr>
                                                     <td align=right width="25%"><span class="required"><b>Old Cadre :</b></span></td>
                                                      <td width="25%"> <c:out value="${logBean.oldRank.cadre.name}"/> </td>
                                                    </tr>
                                                   <tr>
                                                         <td align=right width="25%"><span class="required"><b>Old Rank :</b></span></td>
                                                         <td width="25%"> <c:out value="${logBean.oldRank.name}"/> </td>
                                                   </tr>
                                                   </c:if>

                                                   <tr>
                                                     <td align=right width="25%"><span class="required"><b>Old Pay Group :</b></span></td>
                                                        <td width="25%">
                                                            <c:out value="${logBean.oldSalaryInfo.salaryType.name}"/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td align=right width="25%"><span class="required"><b>Old Level/Step :</b></span></td>
                                                        <td width="25%">
                                                            <c:out value="${logBean.oldSalaryInfo.levelStepStr}"/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                     <td align=right width="25%"><span class="required"><b>New Pay Group :</b></span></td>
                                                        <td width="25%">
                                                            <c:out value="${logBean.salaryInfo.salaryType.name}"/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td align=right width="25%"><span class="required"><b>New Level/Step :</b></span></td>
                                                        <td width="25%">
                                                            <c:out value="${logBean.salaryInfo.levelStepStr}"/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td align=right width="25%"><span class="required"><b>Reference Number :</b></span></td>
                                                        <td width="25%">
                                                            <c:out value="${logBean.deptRefNumber}"/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td align=right width="25%"><span class="required"><b>Reference Date :</b></span></td>
                                                        <td width="25%">
                                                            <c:out value="${logBean.refDateStr}"/>
                                                        </td>
                                                    </tr>
                                                     <c:if test="${roleBean.localGovt}">
                                                     <tr>
                                                        <td align=right width="25%"><span class="required"><b>New Cadre :</b></span></td>
                                                        <td width="25%"> <c:out value="${miniBean.employee.rank.cadre.name}"/> </td>
                                                      </tr>
                                                      <tr>
                                                        <td align=right width="25%"><span class="required"><b>New Rank :</b></span></td>
                                                         <td width="25%"> <c:out value="${miniBean.employee.rank.name}"/> </td>
                                                       </tr>
                                                      </c:if>
                                                </c:when>
                                                <c:otherwise>
                                                   <c:if test="${roleBean.localGovt}">
                                                      <tr>
                                                         <td align=right width="25%"><span class="required"><b>Current Cadre :</b></span></td>
                                                         <td width="25%"> <c:out value="${miniBean.employee.rank.cadre.name}"/> </td>
                                                      </tr>
                                                      <tr>
                                                         <td align=right width="25%"><span class="required"><b>Current Rank :</b></span></td>
                                                         <td width="25%"> <c:out value="${miniBean.employee.rank.name}"/> </td>
                                                      </tr>
                                                   </c:if>
                                                    <tr>
                                                        <td align=right width="25%"><span class="required"><b>Current Pay Group :</b></span></td>
                                                        <td width="25%">
                                                            <c:out value="${miniBean.employee.salaryTypeName}"/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td align=right width="25%"><span class="required"><b>Current Level/Step :</b></span></td>
                                                        <td width="25%">
                                                            <c:out value="${miniBean.employee.salaryInfo.levelStepStr}"/>
                                                        </td>
                                                    </tr>

                                                    <c:choose>
                                                    <c:when test= "${roleBean.localGovt}">
                                                    <tr>
                                                     <td align=right width="25%"><span class="required"><b>New Pay Group :</b></span></td>
                                                     <td align="left">  <form:select path="salaryTypeId"  onchange="loadCadreBySalaryTypeId(this);">
                                                     <form:option value="-1" >&lt;Select&gt;</form:option>
                                                     <c:forEach items="${salaryTypeList}" var="stList">
                                                         <form:option value="${stList.id}" title="${stList.description}">${stList.name}</form:option>
                                                     </c:forEach>
                                                     </form:select>
                                                     </td>
                                                    </tr>
                                                    <tr>
                                                     <td align=right width="25%"><span class="required"><b>New Cadre* :</b></span></td>
                                                     <td align="left">  <form:select path="cadreInstId" id="cadre-control" cssClass="salaryTypeControls" onchange="loadRanksByCadreId(this);">
                                                     <form:option value="-1" >&lt;Select&gt;</form:option>
                                                     <c:forEach items="${cadreList}" var="cList">
                                                         <form:option value="${cList.id}" title="${cList.description}">${cList.name}</form:option>
                                                     </c:forEach>
                                                     </form:select>
                                                     </td>
                                                    </tr>
                                                     <tr>
                                                      <td align=right><span class="required"><b>New Rank* :</b></span></td>
                                                         <td align="left">  <form:select path="rankInstId" id="rank-control" cssClass="salaryTypeControls" onchange="loadSalaryTypeLevelAndStepByRankId(this);">
                                                             <form:option value="-1" >&lt;Select&gt;</form:option>
                                                             <c:forEach items="${rankList}" var="rList">
                                                             <form:option value="${rList.id}" title="${rList.description}">${rList.name}</form:option>
                                                             </c:forEach>
                                                         </form:select>
                                                         </td>
                                                      </tr>
                                                      <tr>
                                                          <td align=right><span class="required"><b>New Level/Step* :</b></span></td>
                                                         <td align="left"> <form:select path="salaryStructureId" id="levelStep-lga-control" cssClass="salaryTypeControls">
                                                             <form:option value="-1" >&lt;Select&gt;</form:option>
                                                             <c:forEach items="${salaryStructureList}" var="sList">
                                                                 <form:option value="${sList.id}">${sList.levelStepStr}</form:option>
                                                             </c:forEach>
                                                             </form:select>
                                                         </td>
                                                    </tr>
                                                    </c:when>
                                                    <c:otherwise>
                                                    <tr>
                                                      <td align=right><span class="required"><b>New Pay Group* :</b></span></td>
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
                                                         <td align=right><span class="required"><b>New Level/Step* :</b></span></td>
                                                                <td align="left">
                                                                <form:select path="salaryStructureId" id="levelStep-control" cssClass="salaryTypeControls">
                                                                    <form:option value="-1" >&lt;Select&gt;</form:option>
                                                                    <c:forEach items="${salaryStructureList}" var="sList">
                                                                        <form:option value="${sList.id}">${sList.levelStepStr}</form:option>
                                                                    </c:forEach>
                                                                </form:select>
                                                                </td>

                                                    </tr>
                                                    </c:otherwise>
                                                    </c:choose>
                                                </c:otherwise>
                                            </c:choose>
                                            <c:if test="${miniBean.confirm}">
                                               <tr >
                                                  <td align="right"><b>Reference Number*</b></td>
                                                  <td width="25%"><form:input path="refNumber" size="25" maxlength="50"/></td>
                                                </tr>
                                                <tr >
                                                  <td align=right><span class="required"><b>Reference Date*</b></span></td>
                                                  <td width="25%"><form:input path="refDate"/><img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('refDate'),event);"></td>

                                                  <td></td>
                                                </tr>
                                             </c:if>
                                        </table>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="buttonRow" align="right">

                                        <c:choose>
                                        <c:when test="${miniBean.confirm}">
                                            <input type="image" name="submit" value="confirm" title="Confirm" class="" src="images/confirm_h.png">
                                            <input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
                                        </c:when>
                                        <c:otherwise>
                                            <c:if test="${not saved}">
                                                <input type="image" name="submit" value="ok" title="Ok" class="" src="images/ok_h.png">
                                                <input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
                                            </c:if>
                                            <c:if test = "${saved}">
                                                <input type="image" name="_close" value="close" title="Close" title="Close" class="" src="images/close.png">
                                            </c:if>
                                        </c:otherwise>
                                        </c:choose>
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

