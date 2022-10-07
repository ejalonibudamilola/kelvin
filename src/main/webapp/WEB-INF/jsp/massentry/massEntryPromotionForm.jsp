<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            Mass Promotion Entry Form
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/spinner.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
		<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
		<script type="text/javascript" src="<c:url value="scripts/jquery-1.4.4.min.js"/>"></script>
		<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>
		<link rel="stylesheet" href="styles/jquery-ui-1.8.18.custom.css" type="text/css" media ="screen">
		<script type="text/javascript">
<!--
function doShowHideList(box,idName) {
     //alert("got into go fxn");
	  
	  //alert("selected value = "+n);
	  if (box.checked) {
		  
		  document.getElementById(idName).style.display = ''; 
		  
	  }else{
		  document.getElementById(idName).style.display = 'none'; 
	  }
}
//-->
</script>
    </head>
    <body class="main" onload="document.massEntryPromo.empId.focus()">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
    <form:form modelAttribute="massPromoBean" name="massEntryPromo">
	<c:set value="${massPromoBean}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	

                        <tr>
                            <td colspan="2">
                                <div class="title">
                                     Promote <c:out value="${roleBean.staffTypeName}"/>s En Masse
                                    
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                  <div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
										<spring:hasBindErrors name="massPromoBean">
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
                                         <input style="display:none" id="activeInd" value="<c:out value="${massPromoBean.activeInd}"/>"/>
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
                                        <td valign="top">
											<p class="label"><c:out value="${roleBean.staffTypeName}"/> To Mass Promote</p>
											<display:table name="dispBean" class="register3" sort="page" defaultsort="1" requestURI="${appContext}/massPromotions.do">
											<display:column property="employee.employeeId" title="${roleBean.staffTitle}" style="text-align:left;"></display:column>
											<display:column property="employee.displayName" title="${roleBean.staffTypeName} Name" style="text-align:left;"></display:column>
											<display:column property="employee.assignedToObject" title="${roleBean.mdaTitle}" style="text-align:left;"></display:column>
											<display:column property="employee.salaryInfo.salaryType.name" title="Pay Group" style="text-align:left;"></display:column>
											<display:column property="employee.salaryInfo.levelStepStr" title="Level & Step" style="text-align:left;"></display:column>
											<display:column property="remove" title="Remove" href="${appContext}/massPromotions.do" paramId="eid" paramProperty="id"></display:column>
											<display:setProperty name="paging.banner.placement" value="bottom" />
										</display:table>
										</td>
                                    </tr>
                                   
                                    <tr>
                                     	<td>
                                      	 	&nbsp;
                                        </td>
                                    </tr>
                                <c:if test="${not massPromoBean.emptyList}">
                                <tr align="left">
									<td class="activeTH">Promotion Details</td>
								</tr>
								<tr>
									<td class="activeTD">
									   <fieldset>
									   <legend><b>Select Promotion Details</b></legend>
										<table width="100%" border="0" cellspacing="1" cellpadding="2">
											<tr>
								              <td align=right><span class="required">Pay Group*</span></td>
								              <td align="left">   
								                <form:select path="salaryTypeId" disabled="true">
								                <form:option value="-1" >&lt;Select&gt;</form:option>
								                <c:forEach items="${salaryTypeList}" var="sTypeList">
								                	<form:option value="${sTypeList.id}" title="${sTypeList.description}">${sTypeList.name}</form:option>
								                </c:forEach>
								                </form:select>
								              </td>
								              <td align="left"></td>
								            </tr>
											<tr>
												 <td align=right><span class="required">Level &amp; Step*</span></td>
														<td align="left">   
														<form:select path="salaryStructureId">
															<form:option value="-1" >&lt;Select&gt;</form:option>
															<c:forEach items="${salaryStructureList}" var="sList">
															<form:option value="${sList.id}">${sList.levelStepStr}</form:option>
															</c:forEach>
														</form:select>
														</td>
												<td align="left"></td>
											</tr>
											<tr> <td align="left" width="25%">&nbsp;</td>
			                  						<td><spring:bind path="payArrearsInd">
			                 						    <input type="hidden" name="_<c:out value="${status.expression}"/>">
			                 							<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if> onClick="doShowHideList(this,'payArrearsAmount');doShowHideList(this,'payArrearsStartDate');doShowHideList(this,'payArrearsEndDate')" title="Check this box to pay Salary Arrears" />
			     										</spring:bind>Pay Arrears
			     			  						 </td>
			                  						<td>&nbsp;</td>
			               					 </tr>
											<tr id="payArrearsAmount" style="${massPromoBean.showArrearsRow}">
												<td align="right" width="25%">Arrears Amount*</td>
												<td width="25%" align="left" colspan="2">
													 <form:input path="amountStr" size="8" maxlength="10" />&nbsp;e.g 500,000.99
												</td>
									          </tr>
									        <tr id="payArrearsStartDate" style="${massPromoBean.showArrearsRow}">
								              <td align="right"><span class="required">Arrears Start Date*</span></td>
								              <td width="25%"><form:input path="startDate"/><img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('startDate'),event);"></td>
								              
								              <td></td>
								            </tr>
								            <tr id="payArrearsEndDate" style="${massPromoBean.showArrearsRow}">
								              <td align="right"><span class="required">Arrears End Date*</span></td>
								              <td width="25%"><form:input path="endDate"/><img src="images/calendar.png" width="16" height="16" border="0" title="Pick a date" onclick="JACS.show(document.getElementById('endDate'),event);"></td>
								              
								              <td></td>
								            </tr>
											<tr>
												<td width="25%" align="left">&nbsp;</td>
												<td width="25%" align="left" colspan="2">&nbsp;</td>
											</tr>
											<tr>
												<td width="25%" align="left">&nbsp;</td>
												<td width="25%" align="left" colspan="2">
												<input id="promote" type="image" name="_promote" value="promote" title="Promote All" class="" src="images/promote_h.png">&nbsp;
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
            $("#promote").click(function(e){
                $(".spin").show();
            });
         </script>
    </body>
</html>
