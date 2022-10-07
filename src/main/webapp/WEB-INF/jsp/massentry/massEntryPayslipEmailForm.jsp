<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            Mass Email Payslip Form
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<link rel="stylesheet" href="styles/jquery-ui-1.8.18.custom.css" type="text/css" media ="screen">
    </head>
    <body class="main" onload="document.massEntryEmail.empId.focus()">
    <form:form modelAttribute="massEmailPayslipBean" name="massEntryEmail">
	<c:set value="${massEmailPayslipBean}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	

                        <tr>
                            <td colspan="2">
                                <div class="title">
                                     Email <c:out value="${roleBean.staffTypeName}"/> Payslips En Masse
                                    
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                  <div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
										<spring:hasBindErrors name="massEmailPayslipBean">
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
                                          <input style="display:none" id="activeInd" value="<c:out value="${massEmailPayslipBean.activeInd}"/>"/>
                                        </td>
                                    </tr>
                                    
                                    
									<tr>
                                    	<td class="buttonRow" align="right">
                                    		 <input type="image" name="_search" value="search" title="Find/Add ${roleBean.staffTitle}" class="" src="images/add.png">&nbsp;
												<c:if test="${saved}"><input type="image" name="_cancel" value="cancel" title="Close" class="" src="images/close.png"></c:if></td>
											
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
											<p class="label">Email <c:out value="${roleBean.staffTypeName}"/> Payslips</p>
											<display:table name="dispBean" class="register2" sort="page" defaultsort="1" requestURI="${appContext}/massEmailPayslips.do">
											<display:column property="employee.employeeId" title="${roleBean.staffTitle}" style="text-align:left;"></display:column>
											<display:column property="employee.displayName" title="Name" style="text-align:left;"></display:column>
											<display:column property="employee.assignedToObject" title="${roleBean.mdaTitle}" style="text-align:left;"></display:column>
											<display:column property="employee.salaryTypeName" title="Pay Group" style="text-align:left;"></display:column>
											<display:column property="employee.salaryInfo.levelStepStr" title="Level & Step" style="text-align:left;"></display:column>
											<display:column property="remove" title="Remove" href="${appContext}/massEmailPayslips.do" paramId="eid" paramProperty="id"></display:column>
											<display:setProperty name="paging.banner.placement" value="bottom" />
										</display:table>
										</td>
                                    </tr>
                                   
                                    <tr>
                                     	<td>
                                      	 	&nbsp;
                                        </td>
                                    </tr>
                                <c:if test="${not massEmailPayslipBean.emptyList}">
                                <tr align="left">
									<td class="activeTH">Payslip Month & Year </td>
								</tr>
								<tr>
									<td class="activeTD">
									   <fieldset>
									   <legend><b>Enter Payslip Generation Month & Year</b></legend>
										<table width="100%" border="0" cellspacing="1" cellpadding="2">
											<tr>
								              <td align=right><span class="required">Select Month*</span></td>
								              <td align="left">
								                <form:select path="runMonth">
								                <form:option value="-1" >&lt;Select&gt;</form:option>
								                <c:forEach items="${monthList}" var="months">
                                                       <form:option value="${months.id}">${months.name}</form:option>
                                                </c:forEach>
								                </form:select>
								              </td>
								              <td align="left"></td>
								            </tr>
                                            <tr>
								              <td align=right><span class="required">Select Year*</span></td>
								              <td align="left">
								                <form:select path="runYear">
								                <form:option value="0" >&lt;Select&gt;</form:option>
								                <c:forEach items="${yearList}" var="years">
                                                       <form:option value="${years.id}">${years.name}</form:option>
                                                </c:forEach>
								                </form:select>
								              </td>
								              <td align="left"></td>
								            </tr>
											<tr>
												<td width="25%" align="left">&nbsp;</td>
												<td width="25%" align="left" colspan="2">&nbsp;</td>
											</tr>
											<tr>
												<td width="25%" align="left">&nbsp;</td>
												<td width="25%" align="left" colspan="2">

												<input type="image" name="_sendEmail" value="sendEmail" title="Send Email" class="" src="images/Send_Email_n.png">&nbsp;
												<input type="image" name="_cancel" value="cancel" title="Cancel Operation" class="" src="images/cancel_h.png">&nbsp;</td>
											</tr>

											</table>
										</fieldset>

									</td>
								</tr>
								<p></p>
								<tr>
								   <td>
								   <p></p>

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
        <script type="text/javascript" src='<c:url value ="/scripts/employeeIdAutocomplete.js"/>'></script>
        <script type="text/javascript" src='<c:url value ="/scripts/searchAutoComplete.js"/>'></script>

    </body>
</html>
