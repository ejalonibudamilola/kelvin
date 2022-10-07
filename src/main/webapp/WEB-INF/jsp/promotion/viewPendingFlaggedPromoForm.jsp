<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            Flagged Promotions
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">

		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<script src="scripts/jquery-3.4.1.min.js"></script>
        <script src="scripts/jquery-ui.js"></script>
        <script src="scripts/mouseover_popup.js"></script>
        <link rel="stylesheet" href="styles/notifications.css" type="text/css"/>
        <script language="JavaScript">
        	 		$(document).ready(function() {

        		    //bind a click event to the select all checkbox
        		    jQuery('#selectall').click(function(event) {  //on click
        		    	var checkboxes = [];
        		        var isChecked = this.checked; //this.checked ? true : false;
        		        var url = 'viewPendingFlaggedPromo.do?checkboxState='+(isChecked?"true":"false");

        		    	jQuery('.selectableCheckbox').each(function(index, element) { //loop through each checkbox
        	                this.checked = isChecked;
        	                checkboxes.push(this.id);
        	                url+='&reqList='+this.id;
        	            });

        		    	if(checkboxes.length > 0) {
        		    		sendValueToServer(url);
        		    	}
        		    });


        		    jQuery(".selectableCheckbox").click(function(e ){
        		    	var url = 'viewPendingFlaggedPromo.do?checkboxState='+(this.checked?"true":"false") //use can use true or false also
        				+"&reqList="+this.id;

        		    	sendValueToServer(url);
        		    });

        		    function sendValueToServer(pUrl) {
        		    	jQuery.ajax({
        	    			url: pUrl,
        	    			type: "POST"
        	    		});
        		    }
        		});
        	</script>
    </head>
    <style>
       #empAppr thead tr th{
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
                                    Pending <c:out value="${roleBean.staffTypeName}"/> Flagged Promotion Approval
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
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="90%">
                                    <tr>
                                        <td>

                                                &nbsp;
                                        </td>

                                    </tr>
                                    <tr>
                                        <td valign="top">
											<p class="label"><c:out value="${roleBean.staffTypeName}"/>(s) Flagged Promotions</p>
											<display:table name="dispBean" class="register4" export="false" sort="page" defaultsort="1" id = "empAppr" requestURI="${appContext}/viewPendingFlaggedPromo.do">


                                            <display:column style="width: 5%" title="<input type='checkbox'  name='selectall' id='selectall' />" media="html" >
											   <form:checkbox path="objectList[${empAppr_rowNum - 1}].rowSelected" id="${empAppr.id}" value="true" cssStyle="selectableCheckbox" cssClass="selectableCheckbox"/>
											</display:column>
                                            <display:column title="${roleBean.staffTitle}" media="html" >
                                            <a href="${appContext}/approveFlaggedPromotion.do?aid=${empAppr.id}" class="popper" data-popbox="pop${empAppr.id}">
                              			         	${empAppr.employee.employeeId}
                              			        </a>
												<div id="pop${empAppr.id}" class="popbox">
												       <h2><c:out value="${roleBean.staffTypeName}"/> Information</h2>
												       <table>
												    	<tr>
												    		<td><b>Surname:</b></td>
												    		<td>${empAppr.employee.lastName}
												    		 </td>
												    	</tr>
												    	<tr>
												    		<td><b>First Name:</b></td>
												    		<td>${empAppr.employee.firstName}
												    		 </td>
												    	</tr>

												    	<tr>
												    		<td><b>Other Names:</b></td>
												    		<td>${empAppr.employee.initials}
												    		 </td>
												    	</tr>
												    	<tr>
												    		<td><b><c:out value="${roleBean.mdaTitle}"/>:</b></td>
												    		<td>${empAppr.mdaInfo.name}
												    		 </td>
												    	</tr>
												    	<tr>
												    		<td><b>Initiated By:</b></td>
												    		<td>${empAppr.initiator.actualUserName}
												    		 </td>
												    	</tr>
												    	<tr>
												    		<td><b>Promotion Date:</b></td>
												    		<td>${empAppr.initiatedDateStr}
												    		 </td>
												    	</tr>

												    </table>
	                              				</div>
											</display:column>
											<display:column property="employee.displayName" title="${roleBean.staffTypeName}"></display:column>
											<display:column property="employee.currentMdaName" title="${roleBean.mdaTitle}"></display:column>
											<display:column property="fromSalaryInfo.salaryScaleLevelAndStepStr" title="From Pay Group"></display:column>
										   <display:column property="toSalaryInfo.salaryScaleLevelAndStepStr" title="To Pay Group"></display:column>
										    <display:column property="initiator.actualUserName" title="Initiator"></display:column>
										    <display:column property="initiatedDateStr" title="Initiated Date"></display:column>
										    <display:column property="approvalStatusStr" title="Status"></display:column>
											<display:setProperty name="paging.banner.placement" value="bottom" />
										</display:table>
										</td>
                                    </tr>
                                    <tr style = "${miniBean.showRow}">
                                        <td>
                                            <table>
                                                <c:if test="${miniBean.addWarningIssued}">
                                                    <tr align = "left">
                                    	                <td align=right width="25%"><b>Approval/Rejection Memo*</b></td>
									                    <td width="75%" align = "left">
						                                    <form:textarea path="approvalMemo" rows="5" cols="35" style="width: 243px; height: 54px;" disabled="${saved}"/>
						                                    &nbsp;<span class="mote"><font color="green"><b>at least 8 characters in length.</b></font></span>
									                    </td>
                                                    </tr>
                                                </c:if>
                                            </table>
                                        </td>
                                    </tr>
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                     	<c:choose>
                                     	<c:when test="{saved}">
                                     	    <input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/close.png">
                                     	</c:when>
                                     	<c:otherwise>
                                     	<c:if test="${roleBean.superAdmin}">
                                     	  <c:choose>
                                     	   <c:when test="${not miniBean.addWarningIssued}">
	                                          <input type="image" name="_approve" value="approve" title="Approve Selected ${roleBean.staffTypeName}" class="" src="images/approve.png">
                                              <input type="image" name="_reject" value="reject" title="Reject Selected ${roleBean.staffTypeName}" class="" src="images/reject_h.png">
	                                       </c:when>
	                                       <c:otherwise>
                                            <input type="image" name="_confirm" value="confirm" title="Confirm to 'Approve All Selected ${roleBean.staffTypeName}'" class="" src="images/confirm_h.png">
	                                       </c:otherwise>
	                                       </c:choose>
	                                     </c:if>
	                                       <input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/close.png">
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
