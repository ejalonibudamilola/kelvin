<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> View/Edit <c:out value="${roleBean.staffTypeName}"/> Biometric Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<script language="JavaScript">
    <!--
	 function toggleIt(which,formSwitch,formStatus,fieldInd){

       var input = document.getElementById(formSwitch);



        var outputText = document.getElementById(formStatus);


            if(which.checked) {
                outputText.innerHTML = "Yes";
                $("#"+fieldInd).val("1");

            } else {
                outputText.innerHTML = "No";
                $("#"+fieldInd).val("0");
            }

        }
     // -->
    </script>
</head>


<body class="main">

	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">



					<tr>
						<td colspan="2">
								<div class="title">View/Verify <c:out value="${roleBean.staffTypeName}"/> Biometric Information </div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
                <form:form modelAttribute="miniBean">
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
						<td class="activeTH">Verify <c:out value="${roleBean.staffTypeName}"/> Biometric Information</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table width="504" border="0" cellpadding="2" cellspacing="0">
                        <c:choose>
                        <c:when test="${miniBean.biometricDataExists eq false}">
								<tr>
									<td align=right><span class="required"><c:out value="${roleBean.staffTitle}"/>*</span></td>
									<td width="25%">
										<form:input path="employeeId" disabled="true" size="6"/>
									</td>
					            </tr>
					            <c:if test="${miniBean.hasLegacyId}">
								<tr>
									<td align=right><span class="required">Legacy ID</span></td>
									<td width="25%">
										<form:input path="legacyId" disabled="true" size="6"/>
									</td>
					            </tr>
					            </c:if>
					            <tr>
                                	 <td align=right><span class="required">Last Name/Surname</span></td>
                                	 <td width="25%">
                                	 <form:input path="lastName" disabled="true" size="15"/>
                                	 </td>
                                </tr>
					            <tr>
                                	 <td align=right><span class="required">First Name</span></td>
                                	 <td width="25%">
                                	 <form:input path="firstName" disabled="true" size="15"/>
                                	 </td>
                                </tr>
					            <tr>
                                	 <td align=right><span class="required">Initials/Other Names</span></td>
                                	 <td width="25%">
                                	 <form:input path="middleName" disabled="true" size="15"/>
                                	 </td>
                                </tr>
					    </c:when>
					    <c:otherwise>
					            <tr>
                                		 <td>
                                			<div style=" margin-left:55%">
                                				<div style="border: 2px solid black; padding:2%; width:165px; border-radius:7px">
                                				<c:choose>
                                				    <c:when test="${miniBean.hasPassport eq 1}">
                                					   <img  src="data:image/jpeg;base64,${miniBean.photoString}" width="150" height="200" >
                                					</c:when>
                                					<c:otherwise>
                                					     <img  src="${appContext}/images/avatar.png" width="150" height="200" >
                                					    <h3><b>Biometric Database did not return <c:out value="${roleBean.staffTypeName}"/> Picture</b></h3>
                                					</c:otherwise>
                                				</c:choose>

                                				</div>
                                				 <div style="margin-top:2%">
                                					    <h3><b><c:out value="${roleBean.staffTypeName}"/>:</b> ${miniBean.lastName} ${miniBean.firstName} ${miniBean.middleName} </h3>
                                					     <h3><b><c:out value="${roleBean.staffTitle}"/>:</b> ${miniBean.employeeId}</h3>
                                					     <c:choose>
                                					     <c:when test="${miniBean.hasSignature eq 1}">
                                					       <h3><b>Signature:</b> <img src="data:image/jpeg;base64,${miniBean.signatureString}" width="100" height="50" /></h3>
                                					     </c:when>
                                					     <c:otherwise>
                                                             <h3><b>Biometric Database did not return <c:out value="${roleBean.staffTypeName}"/> Signature</b></h3>
                                                          </c:otherwise>
                                                     </c:choose>
                                				</div>
                                			</div>
                                		</td>
                                </tr>
                                <tr>
                                 <td>
                                       <div style=" margin-left:45%">
                                          <c:choose>
                                          <c:when test="${miniBean.verifiedBy eq null}">
                                           <h3><b><font color="green">Biometric Information Verified Successfully</font></b></h3>
                                          </c:when>
                                          <c:otherwise>
                                              <h3><b><font color="green"><c:out value="${miniBean.verifiedByMsg}"/></font></b></h3>
                                               <h3><b><font color="green"><c:out value="${miniBean.verifiedDateMsg}"/></font></b></h3>
                                          </c:otherwise>
                                          </c:choose>
                                     </div>
                                </tr>
                                <c:if test="${miniBean.hasPassport eq 1}">
                                  <tr>
                                      <td>
                                          <div style=" margin-left:45%">Update Profile Picture:
                                              <form:input path="updPixBind" id="updPixBindInd"  style="display:none"/>
                                                <label class="toggle">
                                                  <input id="updPixBindSwitch" name="updPixBindSwitch" type="checkbox" onClick="toggleIt(this,'updPixBindSwitch','updPixBindStatus','updPixBindInd')" title="Select this box makes to update ${roleBean.staffTypeName} Profile Picture">
                                                   <span class="roundbutton"></span>
                                                 </label>
                                               <span id="updPixBindStatus">No</span>
                                          </div>
                                      </td>
                                  <tr>
                                </c:if>
                        </c:otherwise>
                        </c:choose>

								 </table>
					    </td>
								</tr>
						  </table>
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right">
						  <c:choose>
                             <c:when test="${miniBean.biometricDataExists eq false}">
						        <input type="image" name="_verify" value="verify" title="Verify" class="" src="images/verify.png">
						     </c:when>
						     <c:otherwise>
							     <input type="image" name="_update" value="update" title="Update" class="" src="images/update.png">
							</c:otherwise>

						   </c:choose>
                                <input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/close.png">
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
	<Script language="JavaScript">
     $(document).ready(function(){
            if($("#updPixBindInd").val() === "true"){
               $('input[name=updPixBindSwitch]').attr('checked', true);
                      $("#updPixBindStatus").html("Yes");
           }
           });
    </Script>
</body>
</html>

