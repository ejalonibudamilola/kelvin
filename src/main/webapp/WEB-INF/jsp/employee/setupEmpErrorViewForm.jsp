<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            <c:out value="${roleBean.staffTypeName}"/> Setup Error View Form
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
    </head>
    <style>
       #conflictTbl thead tr th{
          font-size:8pt !important;
       }
    </style>
    <body class="main">
    <form:form modelAttribute="miniBean">
	<c:set value="${miniBean}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	

                        <tr>
                            <td colspan="2">
                                <div class="title">
                                     <c:out value="${roleBean.staffTypeName}"/> with conflicting names
                                    
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
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
								<table width="35%" border="0" cellspacing="1" cellpadding="2">
								    <tr align="left">
										<td class="activeTH" colspan="2"><c:out value="${roleBean.staffTypeName}"/> to be created</td>
										
									</tr>
                                   
                                    
                                    <tr>
									<td class="activeTD">
									   <fieldset>
									   <legend><b>Details</b></legend>
										<table width="100%" border="0" cellspacing="1" cellpadding="2">
											<tr>
												<td width="25%" align="right"><b><c:out value="${roleBean.staffTitle}"/> :</b></td>
												<td width="25%" align="left" colspan="2"><font color="green"><b><c:out value="${employee.employeeId}"/></b></font></td>
											</tr>
											<tr>
												<td width="25%" align="right"><b>Last Name :</b></td>
												<td width="25%" align="left" colspan="2"><font color="green"><b><c:out value="${employee.lastName}"/></b></font></td>
											</tr>
											<tr>
												<td width="25%" align="right"><b>First Name :</b></td>
												<td width="25%" align="left" colspan="2"><font color="green"><b><c:out value="${employee.firstName}"/></b></font></td>
											</tr>
											<tr>
												<td width="25%" align="right"><b>Middle Name :</b></td>
												<td width="25%" align="left" colspan="2"><font color="green"><b><c:out value="${employee.initials}"/></b></font></td>
											</tr>
											<tr>
												<td width="25%" align="right"><b><c:out value="${roleBean.mdaTitle}"/> :</b></td>
												<td width="25%" align="left" colspan="3" nowrap><font color="green"><b><c:out value="${employee.mdaName}"/></b></font></td>
											</tr>
											<c:if test="${employee.schoolExists}">
											<tr>
												<td width="25%" align="right"><b>School :</b></td>
												<td width="25%" align="left" colspan="3" nowrap><font color="green"><b><c:out value="${employee.schoolName}"/></b></font></td>
											</tr>
											</c:if>
											<tr>
												<td width="25%" align="right"><b>Pay Group :</b></td>
												<td width="25%" align="left" colspan="2" nowrap><font color="blue"><b><c:out value="${employee.payGroup}"/></b></font></td>
											</tr>
											<c:if test="${employee.approved}">
											<tr>
												<td width="25%" align="right"><b>Approved By :</b></td>
												<td width="25%" align="left" colspan="2"><c:out value="${employee.approvedBy}"/></td>
											</tr>
											
											<tr>
												<td width="25%" align="right"><b>Approval Date :</b></td>
												<td width="25%" align="left" colspan="2"><c:out value="${employee.approvedDateStr}"/></td>
											</tr>
											</c:if>
											<c:if test="${employee.rejected}">
											<tr>
												<td width="25%" align="right"><b>Rejected By :</b></td>
												<td width="25%" align="left" colspan="2"><c:out value="${employee.approvedBy}"/></td>
											</tr>
											<tr>
												<td width="25%" align="right"><b>Rejected Date :</b></td>
												<td width="25%" align="left" colspan="2"><c:out value="${employee.approvedDateStr}"/></td>
											</tr>
											</c:if>
											<c:if test="${miniBean.confirmation}">
												<tr>
													<td width="25%" align="left" nowrap><b>Generated Captcha :</b></td>
													<td width="25%" align="left" colspan="3" nowrap><c:out value="${miniBean.generatedCaptcha}"/></td>
												</tr>
												<tr>
													<td width="25%" align="left" nowrap><b>Entered Captcha :</b></td>
													<td width="25%" align="left" colspan="3" nowrap><form:input path="enteredCaptcha" size="7" maxlength="6"/></td>
												</tr>
											
											
											</c:if>
											</table>						
										</fieldset>
										
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
											<p class="label"><c:out value="${roleBean.staffTypeName}"/> with similar names</p>
											<display:table name="dispBean" id="conflictTbl" class="display table" export="false" sort="page" defaultsort="1" requestURI="${appContext}/${miniBean.pageUrl}">
											<display:column property="employeeId" title="${roleBean.staffTitle}" style="text-align:left;"></display:column>
											<display:column property="lastName" title="Last Name" style="text-align:left;"></display:column>
											<display:column property="firstName" title="First Name" style="text-align:left;"></display:column>
											<display:column property="initials" title="Middle Name" style="text-align:left;"></display:column>
											<display:column property="mdaName" title="${roleBean.mdaTitle}" style="text-align:left;"></display:column>
											<display:column property="businessClientName" title="Organization" style="text-align:left;"></display:column>
											<display:column property="payGroup" title="Pay Group" style="text-align:left;"></display:column>
											<display:column property="gsmNumber" title="Phone No." style="text-align:left;"></display:column>
											<display:setProperty name="paging.banner.placement" value="" />
										</display:table>
										</td>
                                    </tr>
                                   
                                    <tr>
                                     	<td>
                                      	 	&nbsp;
                                        </td>
                                    </tr>
                                
                                </table>
                                
                            </td>
                        </tr>
                        <tr>
							<td width="25%" align="left">
								<input type="image" name="_close" value="close" title="Close" class="" src="images/close.png">&nbsp;
								<c:choose>
									<c:when test="${miniBean.confirmation}">
									 	<input type="image" name="_confirm" value="confirm" title="Confirm" class="" src="images/confirm_h.png">&nbsp;
									</c:when>
									<c:otherwise>
											<c:if test="${ employee.rejectedOrApproved and roleBean.superAdmin}">
											<input type="image" name="_delete" value="delete" title="Delete" class="" src="images/delete_h.png">&nbsp;
											</c:if>
											<c:if test="${not employee.rejectedOrApproved and roleBean.superAdmin}">
											 <input type="image" name="_approve" value="approve" title="Approve" class="" src="images/approve.png">&nbsp;
										     <input type="image" name="_reject" value="reject" title="Reject" class="" src="images/reject_h.png">&nbsp;
											</c:if>
									</c:otherwise>
								</c:choose>
									
									
								
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
        <script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
        <script type="text/javascript">
           $(function() {
              $("#conflictTbl").DataTable({
                 "order" : [ [ 1, "asc" ] ]
              });
           });
        </script>
    </body>
</html>
