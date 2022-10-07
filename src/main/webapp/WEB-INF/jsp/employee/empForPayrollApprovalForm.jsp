<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            <c:out value="${roleBean.staffTypeName}"/>s Awaiting Approval For Payroll
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<link rel="stylesheet" href="css/jquery-ui.css" type="text/css"/>
		<link rel="stylesheet" href="styles/notifications.css" type="text/css"/>
 		<script src="scripts/jquery-3.4.1.min.js"></script>
        		<script src="scripts/jquery-ui.js"></script>
		<script src="scripts/mouseover_popup.js"></script>
		<script language="JavaScript">
        	 		$(document).ready(function() {

        		    //bind a click event to the select all checkbox
        		    jQuery('#selectall').click(function(event) {  //on click
        		    	var checkboxes = [];
        		        var isChecked = this.checked; //this.checked ? true : false;
        		        var url = 'viewEmpForPayApproval.do?checkboxState='+(isChecked?"true":"false");

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
        		    	var url = 'viewEmpForPayApproval.do?checkboxState='+(this.checked?"true":"false") //use can use true or false also
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
     <div class="loader"></div>

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
                                    Pending <c:out value="${roleBean.staffTypeName}"/> Approval
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
                                                        <p class="label"><c:out value="${roleBean.staffTypeName}"/>(s) Awaiting Approval for Payroll Processing</p>
                                                        <display:table name="dispBean" class="register4" export="false" sort="page" defaultsort="1" id = "empAppr" requestURI="${appContext}/viewEmpForPayApproval.do">

                                                        <display:column style="width: 5%" title="<input type='checkbox'  name='selectall' id='selectall' />" media="html" >
                                                        <form:checkbox path="objectList[${empAppr_rowNum - 1}].rowSelected" id="${empAppr.id}" value="true" cssStyle="selectableCheckbox" cssClass="selectableCheckbox"/>
                                                        </display:column>

                                                        <display:column  title="${roleBean.staffTitle}" media="html" >
                                                            <c:choose>
                                                             <c:when test="${miniBean.captchaError}">
                                                                <a href="${appContext}${empAppr.enteredCaptcha}${empAppr.entityId}" class="popper" data-popbox="pop${empAppr.id}">
                                                                    ${empAppr.employeeId}
                                                                 </a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <a href="${appContext}/approveEmployeeForPayroll.do?aid=${empAppr.entityId}" class="popper" data-popbox="pop${empAppr.id}">
                                                                        ${empAppr.employeeId}
                                                                  </c:otherwise>
                                                                  </c:choose>

                                                            <div id="pop${empAppr.id}" class="popbox">
                                                                   <h2><c:out value="${roleBean.staffTypeName}"/> Information</h2>
                                                                   <table>
                                                                    <tr>
                                                                        <td><b>Last Name:</b></td>
                                                                        <td>${empAppr.lastName}
                                                                         </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td><b>First Name:</b></td>
                                                                        <td>${empAppr.firstName}
                                                                         </td>
                                                                    </tr>

                                                                    <tr>
                                                                        <td><b>Other Names:</b></td>
                                                                        <td>${empAppr.initials}
                                                                         </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td><b>Date Of Birth:</b></td>
                                                                        <td>${empAppr.birthDateStr}
                                                                         </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td><b>Bank:</b></td>
                                                                        <td>${empAppr.bankName}
                                                                         </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td><b>Account No:</b></td>
                                                                        <td>${empAppr.accountNo}
                                                                         </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td><b>BVN:</b></td>
                                                                        <td>${empAppr.bvnNo}
                                                                         </td>
                                                                    </tr>
                                                                    <c:choose>
                                                                        <c:when test="${roleBean.pensioner}">
                                                                         <tr>
                                                                            <td><b>Annual Pension:</b></td>
                                                                             <td>${empAppr.yearlySalary}</td>
                                                                          </tr>
                                                                         <tr>
                                                                            <td><b>Monthly Pension:</b></td>
                                                                             <td>${empAppr.monthlySalary }</td>
                                                                          </tr>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                        <tr>
                                                                             <td><b>Annual Gross:</b></td>
                                                                             <td>${empAppr.annualSalaryStrWivNaira}</td>
                                                                         </tr>
                                                                        </c:otherwise>
                                                                    </c:choose>

                                                                    <tr>
                                                                        <td><b><c:out value="${roleBean.staffTypeName}"/> Type:</b></td>
                                                                        <td>${empAppr.staffType}</td>
                                                                    </tr>


                                                                </table>
                                                            </div>
                                                        </display:column>
                                                        <display:column property="displayName" title="${roleBean.staffTypeName}"></display:column>
                                                        <display:column property="mdaName" title="${roleBean.mdaTitle}"></display:column>
                                                        <display:column property="payGroup" title="Pay Group"></display:column>
                                                       <display:column property="createdBy" title="Created By"></display:column>
                                                        <display:column property="initiatedDateStr" title="Creation Date"></display:column>
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
                                                    <td align=right width="25%"><b>Approval Memo*</b></td>

                                                       <td width="75%" align = "left">
                                                         <form:textarea path="approvalMemo" rows="5" cols="35" disabled="${saved}"/>
                                                       </td>
                                                </tr>
                                                </c:if>
                                                </table>
                                                </td>
                                                </tr>
                                                <tr>

                                                </tr>
                                                <tr>
                                                    <td class="buttonRow" align="right">
                                                    <input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/close.png">
                                                    <c:if test="${roleBean.superAdmin and miniBean.hasRecords }">
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
