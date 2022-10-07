<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> New Deduction Category Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon"
      type="image/png"
      href="<c:url value="/images/coatOfArms.png"/>">
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>
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
						<td>
								<div class="title">Create a New Deduction Category</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="deductionCategoryBean">
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="deductionCategoryBean">
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
						<td class="activeTH">New Deduction Category Information</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Deduction Category*</span></td>
										<td width="25%">
											<form:input path="name" size="20" maxlength="30"/>
									    </td>

								</tr>
								<tr>
									<td align="right" width="35%" nowrap>
										<span class="required">Description*</span></td>
									<td width="25%">
										<form:input path="description" size="30" maxlength="30"/>
									</td>
								</tr>
								<tr>
								<td align="right" width="35%" nowrap>
                                		 <span class="required" title="A statutory indicator when switched on means that <br> deduction category will be deducted monthly for all staff">Statutory Indicator*</span></td>
									<td width="25%">
                            	<form:input path="statutoryInd" id="statutoryBindInd"  style="display:none" />
                                     <label class="toggle">
                                     <input id="statutorySwitch" type="checkbox" onClick="toggleIt(this,'statutorySwitch','statutoryStatus','statutoryBindInd')">
                                       <span class="roundbutton"></span>
                                     </label>
                                    <span id="statutoryStatus">Off</span>
									</td>
								</tr>
								<tr>
								<td align="right" width="35%" nowrap>
                                		 <span class="required" title="A Ranged indicator when switched on means that <br> deduction type will be a Ranged Deduction Type">Ranged Indicator*</span></td>
									<td width="25%">
                            	<form:input path="rangedInd" id="rangedBindInd"  style="display:none" />
                                     <label class="toggle">
                                     <input id="rangedSwitch" type="checkbox" onClick="toggleIt(this,'rangedSwitch','rangedStatus','rangedBindInd')">
                                       <span class="roundbutton"></span>
                                     </label>
                                    <span id="rangedStatus">Off</span>
									</td>
								</tr>
								<tr>
								<td align="right" width="35%" nowrap>
                                		 <span class="required" title="An Apportioned indicator when switched on means that <br> Will have its value 'apportioned' in percentages against 2-3 entities">Apportionment Indicator*</span></td>
									<td width="25%">
                            	<form:input path="apportionedInd" id="apportionedBindInd"  style="display:none" />
                                     <label class="toggle">
                                     <input id="apportionedSwitch" type="checkbox" onClick="toggleIt(this,'apportionedSwitch','apportionedStatus','apportionedBindInd')">
                                       <span class="roundbutton"></span>
                                     </label>
                                    <span id="apportionedStatus">Off</span>
									</td>
								</tr>
					           </table>
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
							<c:choose>
								<c:when test ="${saved}">
									<input type="image" name="_cancel" value="cancel" title="Close" src="images/close.png">
								</c:when>
								<c:otherwise>
								<input type="image" name="submit" value="ok" alt="Ok" class="" src="images/ok_h.png">&nbsp;
								<input type="image" name="_cancel" value="cancel" title="Cancel" src="images/cancel_h.png">
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
<script language="JavaScript">
$(document).ready(function(){
   if($("#rangedBindInd").val() === "true"){
             $('input[name=rangedSwitch]').attr('checked', true);
                    $("#rangedStatus").html("Yes");
         }
         if($("#statutoryBindInd").val() === "true"){
                    $('input[name=statutorySwitch]').attr('checked', true);
                    $("#statutoryStatus").html("Yes");
                }
          if($("#apportionedBindInd").val() === "true"){
                             $('input[name=apportionedSwitch]').attr('checked', true);
                             $("#apportionedStatus").html("Yes");
                         }

    });
</script>
</body>
</html>