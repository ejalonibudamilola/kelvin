<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Create/Edit Educational School  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
 <link rel="stylesheet" href="css/screen.css" type="text/css"/>
</head>

<body class="main">
<form:form modelAttribute="miniBean">
<c:set value="${displayList}" var="dispBean" scope="request"/>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					<tr>
						<td>
								<div class="title">${displayTitle}</div>
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
				<table border="0" cellspacing="0" cellpadding="3" width="60%" align="left" >
					<tr align="left">
						<td class="activeTH">Educational School</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
							
							
								 <tr>
									<td align="right" width="35%" nowrap>
										<span class="required">School Type*</span>&nbsp;
									</td>
									<td><form:select path="educationSchoolType.id">
										<form:option value="-1">&lt;Select&gt;</form:option>
										<c:forEach items="${schoolTypeList}" var="hList">
						                <form:option value="${hList.id}" title="${hList.classification}">${hList.name}</form:option>
						                </c:forEach>
										</form:select>
										</td>
								</tr>
								 <tr>
									<td align="right" width="35%" nowrap>
										<span class="required">School State*</span>&nbsp;
									</td>
									<td><form:select path="stateInfo.id">
										<form:option value="-1">&lt;Select&gt;</form:option>
										<c:forEach items="${stateList}" var="sList">
						                <form:option value="${sList.id}" title="${sList.name}">${sList.name}</form:option>
						                </c:forEach>
										</form:select>
										</td>
								</tr>
								<tr>
									<td align="right" nowrap>
										<span class="required">School Name*</span>&nbsp;
									</td>
									<td><form:input path="name" size="50" maxlength="80"/></td>								
								</tr>
								 <tr>
									<td align="right" nowrap>
										<span class="required">Code Name*</span>&nbsp;
									</td>
									<td><form:input path="codeName" size="5" maxlength="10" title="This field is not designated to be Unique. However it is advised to be User-Friendly for reporting purposes only."/></td>								
								</tr>
								 <tr>
									<td align="right" nowrap>
										<span class="required">Address*</span>&nbsp;
									</td>
									<td><form:input path="address" size="30" maxlength="120"/></td>								
								</tr> 
								 <tr>
									<td align="right" width="35%" nowrap>
										<span class="required">International/Local*</span>&nbsp;
									</td>
									<td><form:select path="localInd">
										<form:option value="-1">&lt;Select&gt;</form:option>
										<c:forEach items="${intList}" var="iList">
						                <form:option value="${iList.reportType}">${iList.name}</form:option>
						                </c:forEach>
										</form:select>
										</td>
								</tr>
					        </table>							
						</td>
					</tr>
					
					</table>
					 
					<tr>
						<td class="buttonRow" align="right" >
						
						   <c:if test="${miniBean.editMode}">
							  &nbsp;<input type="image" name="submit" value="ok" title="Update" class="" src="images/update.png"> 
							</c:if>
							<c:if test="${not miniBean.editMode}">
							  &nbsp; <input type="image" name="submit" value="ok" title="Update" class="" src="images/create_h.png"> 
							</c:if>
							
						 
						</td>
					</tr>
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
											<p class="label">Click to Edit School</p>
											<display:table name="dispBean" class="register3" export="false" sort="page" defaultsort="1" requestURI="${appContext}/addNewEduSchool.do">
											<display:column property="name" title="School Name" media="html" href="${appContext}/addNewEduSchool.do" paramId="bid" paramProperty="id"></display:column>
											<display:column property="codeName" title="School Code"></display:column>	
											<display:column property="educationSchoolType.name" title="School Type"></display:column>	
											<display:column property="stateInfo.name" title="State"></display:column>	
											<display:column property="address" title="Location"></display:column>									
											<display:column property="createdBy" title="Creator"></display:column>
											<display:column property="timeCreated" title="Created Date"></display:column>
											 <display:setProperty name="paging.banner.placement" value="bottom" />								    
										</display:table>
										</td>
                                    </tr>
                                    


                                          &nbsp; <input type="image" name="_cancel" value="cancel" title="Close Report" src="images/close.png">
                                       <p>
                                      </td>
                                    </tr>
                     </table>
                     </c:if>
				<tr>
			      <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		     </tr>
			 
			</table>	
		 
 
	
	 </form:form>
</body>
</html>