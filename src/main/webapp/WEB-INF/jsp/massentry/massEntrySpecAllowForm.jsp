<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            Mass Special Allowance Entry Form
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/spinner.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<link rel="stylesheet" href="styles/jquery-ui-1.8.18.custom.css" type="text/css" media ="screen">
    </head>
    <body class="main" onload="document.massEntrySpecAllow.empId.focus()">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
    <form:form modelAttribute="massSpecAllowBean" name="massEntrySpecAllow">
	<c:set value="${beanList}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                     Create <c:out value="${roleBean.staffTypeName}"/> Special Allowances En Masse
                                    
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                  <div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
										<spring:hasBindErrors name="massSpecAllowBean">
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
								<tr align="left">
										<td class="activeTH" colspan="2">Search By <c:out value="${roleBean.staffTitle}"/></td>
										
									</tr>
                                   
                                    
                                    <tr>
                                      
                                       <td class="activeTD">
                                              <c:out value="${roleBean.staffTitle}"/> :
                                        </td>
                                        <td class="activeTD">
                                        <form:input path="staffId" size="8" maxlength="10" id="empId"/>
                                         <!-- span to display the name of the employee with the selected id -->
                                          <span id="staff_name_display_span" style="padding-left: 30px; font-size: 11px; color: blue;">
                                           </span>
                                           <!-- container for the undisplayed employee names -->
                                         <div id="employee_names_div" style="display: none;"></div>
                                         <input style="display:none" id="activeInd" value="<c:out value="${massSpecAllowBean.activeInd}"/>"/>
                                         </td>
                                    </tr>
                                    
                                    
									<tr>
                                    	<td class="buttonRow" align="right">
                                    		 <input type="image" name="_search" value="search" title="Find/Add ${roleBean.staffTitle}" class="" src="images/add.png">&nbsp;
												<c:if test="${saved}"><input type="image" name="_cancel" value="cancel" title="Close" class="" src="images/close.png"></c:if>
                                        
                                    	</td>
                                    </tr>
								</table>                                 
                                <table class="reportMain" cellpadding="0" cellspacing="0" >
                                
                                    <tr>
                                        <td>
                                                    &nbsp;                                        
                                        </td>
                                       
                                    </tr>
                                   <tr>
                                    <td>
                                        <c:forEach items="${massSpecAllowBean.beanList}" var="list" varStatus="gridRow">
                                            <p>Hi
                                                <c:out value="${list.parentObject.employeeId}" />
                                                <c:out value="${list.parentObject.displayName}" />
                                            </p>
                                        </c:forEach>
                                    </td>
                                   </tr>
                                    <tr>
                                        <td valign="top">
											<p class="label"><c:out value="${roleBean.staffTypeName}"/> To Add Special Allowance</p>
											<display:table name="dispBean" class="register3" sort="page" defaultsort="1" requestURI="${appContext}/massSpecialAllowance.do">
                                            <display:column property="parentObject.employeeId" title="${roleBean.staffTitle}" style="text-align:left;"></display:column>
											<display:column property="parentObject.displayName" title="Name" style="text-align:left;"></display:column>
											<display:column property="parentObject.assignedToObject" title="${roleBean.mdaTitle}" style="text-align:left;"></display:column>
											<display:column property="parentObject.salaryTypeName" title="Pay Group" style="text-align:left;"></display:column>
											<display:column property="parentObject.salaryInfo.levelStepStr" title="Level & Step" style="text-align:left;"></display:column>
											<display:column property="remove" title="Remove" href="${appContext}/massSpecialAllowance.do" paramId="eid" paramProperty="id"></display:column>
											<display:setProperty name="paging.banner.placement" value="bottom" />
										</display:table>
										</td>
                                    </tr>
                                   
                                    <tr>
                                     	<td>
                                      	 	&nbsp;
                                        </td>
                                    </tr>
                                <c:if test="${not massSpecAllowBean.emptyList}">
                                <tr align="left">
									<td class="activeTH">Special Allowance Details</td>
								</tr>
								<tr>
									<td class="activeTD">
									   <fieldset>
									   <legend><b>Enter Special Allowance Details</b></legend>
										<table width="100%" border="0" cellspacing="1" cellpadding="2">
											<tr>
								              <td align=right><span class="required">Select Special Allowance Type*</span></td>
								              <td align="left">   
								                <form:select path="salaryTypeId">
								                <form:option value="-1" >&lt;Select&gt;</form:option>
								                <c:forEach items="${specAllowList}" var="gtypes">
                                                       <form:option value="${gtypes.id}">${gtypes.description}</form:option>
                                                </c:forEach>
								                </form:select>
								              </td>
								              <td align="left"></td>
								            </tr>
								            <tr>
								              <td align=right><span class="required">Select Pay Type*</span></td>
								              <td align="left">   
								                <form:select path="payTypeInstId">
								                <form:option value="-1" >&lt;Select&gt;</form:option>
								                <c:forEach items="${payType}" var="ptypes">
                                                       <form:option value="${ptypes.id}">${ptypes.name}</form:option>
                                                </c:forEach>
								                </form:select>
								              </td>
								              <td align="left"></td>
								            </tr>
								            <tr>
												 <td align=right><span class="required">Amount*</span></td>
												<td align="left"><form:input path="owedAmountStr" size="8" maxlength="10"/></td>
												<td align="left"></td>
											</tr>
											<tr>
												<td align=right><span class="required">Allowance Start Date*</span></td>
												<td align="left"><form:input path="startDate"/><img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('startDate'),event);"></td>
												<td align="left"></td>
											</tr>
											<tr>
												<td align=right><span class="required">Allowance End Date*</span></td>
												<td align="left"><form:input path="endDate"/><img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('endDate'),event);"></td>
												<td align="left"></td>
											</tr>
											<tr>
												<td width="25%" align="left">&nbsp;</td>
												<td width="25%" align="left" colspan="2">
												<input id="addSpecAllow" type="image" name="_addSpecAllow" value="addSpecAllow" title="Add Special Allowance" class="" src="images/addSpecAllow.png">&nbsp;
												<input type="image" name="_cancel" value="cancel" title="Cancel Operation" class="" src="images/cancel_h.png">&nbsp;</td>
											</tr>
											
											</table>						
										</fieldset>
										
									</td>
								</tr>
								</c:if>
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
        <div class="spin"></div>
          <script type="text/javascript" src='<c:url value ="/scripts/employeeIdAutocomplete.js"/>'></script>
          <script type="text/javascript" src='<c:url value ="/scripts/searchAutoComplete.js"/>'></script>
          <script>
            $("#addSpecAllow").click(function(e){
                $(".spin").show();
            });
          </script>
    </body>
</html>
