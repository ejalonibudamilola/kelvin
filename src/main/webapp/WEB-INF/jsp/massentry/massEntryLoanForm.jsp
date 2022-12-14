<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            Mass Loan Entry Form
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/spinner.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<link rel="stylesheet" href="styles/jquery-ui-1.8.18.custom.css" type="text/css" media ="screen">
    </head>
    <body class="main" onload="document.massEntryLoan.empId.focus()">
    <form:form modelAttribute="massLoanBean"  name="massEntryLoan">
	<c:set value="${massLoanBean}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	

                        <tr>
                            <td colspan="2">
                                <div class="title">
                                     Create <c:out value="${roleBean.staffTypeName}"/> Loan En Masse
                                    
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                  <div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
										<spring:hasBindErrors name="massLoanBean">
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
                                         <input style="display:none" id="activeInd" value="<c:out value="${massLoanBean.activeInd}"/>"/>
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
											<p class="label"><c:out value="${roleBean.staffTypeName}"/> To Add Loan</p>
											<display:table name="dispBean" class="register3" sort="page" defaultsort="1" requestURI="${appContext}/massLoanEntry.do">
											<display:column property="employee.employeeId" title="${roleBean.staffTitle}" style="text-align:left;"></display:column>
										    <display:column property="employee.displayName" title="Name" style="text-align:left;"></display:column>
                                        	 <display:column property="employee.assignedToObject" title="${roleBean.mdaTitle}" style="text-align:left;"></display:column>
                                        	 <display:column property="employee.salaryTypeName" title="Pay Group" style="text-align:left;"></display:column>
                                        	 <display:column property="employee.salaryInfo.levelStepStr" title="Level & Step" style="text-align:left;"></display:column>
                                        	 <display:column property="remove" title="Remove" href="${appContext}/massLoanEntry.do" paramId="eid" paramProperty="id"></display:column>
											<display:setProperty name="paging.banner.placement" value="bottom" />
										</display:table>
										</td>
                                    </tr>
                                   
                                    <tr>
                                     	<td>
                                      	 	&nbsp;
                                        </td>
                                    </tr>
                                <c:if test="${not massLoanBean.emptyList}">
                                <tr align="left">
									<td class="activeTH">Loan Details</td>
								</tr>
								<tr>
									<td class="activeTD">
									   <fieldset>
									   <legend><b>Enter Loan Details</b></legend>
										<table width="100%" border="0" cellspacing="1" cellpadding="2">
											<tr>
								              <td align=right><span class="required">Select Loan Type*</span></td>
								              <td align="left">   
								                <form:select path="salaryTypeId">
								                <form:option value="-1" >&lt;Select&gt;</form:option>
								                <c:forEach items="${LoanTypeList}" var="gtypes">
                                                       <form:option value="${gtypes.id}">${gtypes.description}</form:option>
                                                </c:forEach>
								                </form:select>
								              </td>
								              <td align="left"></td>
								            </tr>
								            <tr>
												 <td align=right><span class="required">Loan Amount* ???</span></td>
												<td align="left"><form:input path="owedAmountStr" size="10" maxlength="15"/></td>
												<td align="left"></td>
											</tr>
											<tr>
												 <td align=right><span class="required">Loan Tenor*</span></td>
														<td align="left">   
														<form:select path="loanTerm">
                                                              <form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
                                                                   <c:forEach items="${loanTermList}" var="loanTerms">
                                                                       <form:option value="${loanTerms.id}">${loanTerms.name}</form:option>
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
												<input id="addLoan" type="image" name="_addloan" value="addloan" title="Add Loan" class="" src="images/addLoan.png">&nbsp;
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
								     <ul>
                                   	     <li>
                                   		    <b><font color="grey"><c:out value="${roleBean.staffTypeName}"/>s having the selected Loan will have the value overridden.</font></b>
                                   		  </li>
                                   	 </ul>

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
            $("#addLoan").click(function(e){
                $(".spin").show();
            });
         </script>

    </body>
</html>
