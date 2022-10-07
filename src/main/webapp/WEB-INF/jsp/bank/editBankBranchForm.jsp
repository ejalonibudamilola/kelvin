<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Edit Bank Branch Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
<script src="<c:url value="/scripts/utility_script.js"/>"></script>
<script language="javascript">
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
						<td>
								<div class="title">Edit Bank Branch Information</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="bankBranchBean">
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="bankBranchBean">
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
						<td class="activeTH">Edit <c:out value="${bankBranchBean.name}"/> </td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
							<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Parent Bank*</span></td>
									<td align="left" width="25%">
										<form:select path="bankInfo.id">
													<c:forEach items="${banksList}" var="banks">
														<form:option value="${banks.id}" title="${banks.sortCode}">${banks.name}</form:option>
													</c:forEach>
														
										</form:select>
									
									</td>
								
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Bank Branch Name*</span></td>
									<td align="left" width="25%">
										<form:input path="name" size="80" maxlength="99"/>
									</td>
								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Branch Sort Code*</span></td>
									<td align="left" width="25%">
										<form:input path="branchSortCode" size="10" maxlength="20"/>
									</td>
								</tr>
								<c:if test="${bankBranchBean.showOverride}">
                                 <tr>
                                   <td align="right" width="35%" nowrap>
                                     <span class="required">Designate As Bank</span>
                                    </td>

                                     <td width="35%">
                                        <form:input path="bankStatusDesignate" id="bankStatusDesignateBindInd" style="display:none"/>
                                         <label class="toggle" title="Switch to 'Yes' to make this Bank Branch act as a Bank for Report Generation.">
                                            <input id="bankStatusDesignateSwitch" name="bankStatusDesignateSwitch" type="checkbox" onClick="toggleIt(this,'bankStatusDesignateSwitch','bankStatusDesignateStatus','bankStatusDesignateBindInd')" >
                                             <span class="roundbutton"></span>
                                          </label>
                                          <span id="bankStatusDesignateStatus">No</span>

                                     </td>
                                 </tr>
                                 </c:if>
					           </table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
							 <c:choose>
							         	<c:when test="${saved}">
												<input type="image" name="_cancel" value="cancel" title="Close" class="" src="images/close.png">
							         	</c:when>
							         	<c:otherwise>
							         	    
							         		<input type="image" name="submit" value="ok" title="Ok" class="" src="images/ok_h.png">&nbsp;
											<c:if test="${bankBranchBean.canDelete and roleBean.superAdmin}">
												<input type="image" name="_delete" value="Delete" title="Delete bank branch" class="" src="images/delete_h.png">
												
											</c:if>
											<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
							         	
							         	</c:otherwise>
							         
							         </c:choose>
						</td>
					</tr>
				</table>
				</form:form>
			</td>
		</tr>
		
		</table>
		</td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
	<script language="javascript">
     $(document).ready(function(){
       if($("#bankStatusDesignateBindInd").val() === "true"){
                   $('input[name=bankStatusDesignateSwitch]').attr('checked', true);
                             $("#bankStatusDesignateStatus").html("Yes");
             }

      });
    </script>
</body>
</html>