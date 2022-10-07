<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> <c:out value="${pageTitle}" />  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
<link rel="stylesheet" href="css/screen.css" type="text/css"/>
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">

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
<style>
    #paytbl thead tr th{
        font-size:8pt !important;
    }
</style>
<body class="main" id="result">
<form:form modelAttribute="miniBean">
<c:set value="${displayList}" var="dispBean" scope="request"/>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

					<tr>
						<td>
								<div class="title"><c:out value="${mainHeader}" /></div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						    * = required<br/><br/>

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
				            <table   border="0" cellspacing="0" cellpadding="3" width="60%" align="left" >
					            <tr align="left">
						            <td class="activeTH"><c:out value="${tableHeader}"/> </td>
					            </tr>
					            <tr>
						            <td class="activeTD">
							            <table border="0" cellspacing="0" cellpadding="2">
								            <tr>
									            <td align="right" width="35%" nowrap>
										            <span class="required">Pay Group Type Name*</span>
										        </td>
									            <td width="25%">
										            <form:input path="name" size="25" maxlength="32"/>
									            </td>
								            </tr>
								            <tr>
									            <td align="right" width="35%" nowrap>
										            <span class="required">Description*</span>
										        </td>
									            <td width="25%">
										            <form:input path="description" size="30" maxlength="70"/>
									            </td>
								            </tr>
								            <tr>
									            <td align="right" width="35%" nowrap>
										            <span class="required">Pay Group Code*</span>
										        </td>
									            <td width="25%">
										            <form:input path="payGroupCode" size="4" maxlength="6"/>
									            </td>
								            </tr>
                                            <tr>
                                               <td align="right" width="35%" nowrap>
                                                   <span class="required">Exempt From Pension Contribution</span>
                                               </td>
                                               <td width="35%">
                                                  <form:input path="pensionExemptIndBind" id="pensionExemptIndBindInd"  style="display:none"/>
                                                  <label class="toggle">
                                                     <input id="pensionExemptIndBindSwitch" name="pensionExemptIndBindSwitch" type="checkbox" onClick="toggleIt(this,'pensionExemptIndBindSwitch','pensionExemptStatus','pensionExemptIndBindInd')" title="Checking this box Exempts ${roleBean.staffTypeName}s on this Pay Group from Pension Contribution">
                                                     <span class="roundbutton"></span>
                                                  </label>
                                                  <span id="pensionExemptStatus">No</span>
                                               </td>
                                            </tr>
                                            <tr>
                                               <td align="right" width="35%" nowrap>
                                                   <span class="required">Consolidated</span>
                                               </td>
                                               <td width="35%">
                                                  <form:input path="consolidatedBind" id="consolidatedBindInd"  style="display:none"/>
                                                  <label class="toggle">
                                                     <input id="consolidatedBindSwitch" name="consolidatedBindSwitch" type="checkbox" onClick="toggleIt(this,'consolidatedBindSwitch','consolidatedStatus','consolidatedBindInd')" title="A 'Consolidated' Pay Group means the total values will be used during Pension Calculation.">
                                                     <span class="roundbutton"></span>
                                                  </label>
                                                  <span id="consolidatedStatus">No</span>
                                               </td>
                                            </tr>
                                            <tr>
                                                <td>
                                                    <c:if test="${miniBean.editMode}">
                                                       <tr>
                                                          <td align="right" width="35%" nowrap>
                                                            <span class="required">Active</span>
                                                          </td>
                                                          <td width="35%">
                                                             <form:input path="selectableBind" id="selectableBindInd"  style="display:none"/>
                                                             <label class="toggle">
                                                               <input id="selectableBindSwitch" name="selectableBindSwitch" type="checkbox" onClick="toggleIt(this,'selectableBindSwitch','selectableStatus','selectableBindInd')" title="If set to false (Off) the Pay Group will not be available for selection or assignment to ${roleBean.staffTypeName}s.">
                                                               <span class="roundbutton"></span>
                                                             </label>
                                                             <span id="selectableStatus">No</span>
                                                          </td>
                                                       </tr>
								                    </c:if>
								                </td>
								            </tr>
								            <c:if test="${miniBean.editMode}">
								            <tr>
								                <td style="display:none" id="stid" align="right" width="25%"><form:input path="id" id="ftid"/></td>
								                <td align="right" width="5%"></td>
								                <td id="vst" align="center" width="25%"><a href="${appContext}/salaryStructureDetails.do?sid=${miniBean.id}">View Salary Structure</a>
								                <span class="tabseparator">|</span><a href="${appContext}/editSalaryStructure.do?sid=${miniBean.id}">Edit Salary Structure</a></td>
								            </tr>
								            </c:if>
					                    </table>
						            </td>
					            </tr>

					        </table>
					    </td>
					</tr>
					<tr>
						<td class="buttonRow" align="right">
						   <c:if test="${miniBean.editMode}">
							  &nbsp;<input type="image" name="submit" value="ok" title="Update" class="" src="images/update.png">
						   </c:if>
						   <c:if test="${not miniBean.editMode}">
							  &nbsp; <input type="image" name="submit" value="ok" title="Update" class="" src="images/create_h.png">
						   </c:if>
						</td>
					</tr>
			    </table>
                <table>
                        <tr>
                            <td>
                               &nbsp;
                            </td>
                        </tr>
                        <tr width="95%">
                           <td valign="top">
                              <p class="label">Click to Edit Pay Group</p>
                              <display:table name="dispBean" id="paytbl" class="display table" export="false" sort="page" defaultsort="1" requestURI="${appContext}/createSalaryType.do">
                            	    <display:column property="name" title="Pay Group Name" media="html" href="${appContext}/createSalaryType.do" paramId="bid" paramProperty="id"></display:column>
                            		<display:column property="contributesToPensionStr" title="Contributes To Pension?"></display:column>
                            		<display:column property="consolidatedStr" title="Consolidated?"></display:column>
                            		<display:column property="selectableStr" title="Selectable?"></display:column>
                            		<display:column property="createdBy.actualUserName" title="Created By"></display:column>
                            		<display:column property="createdDateStrForDisplay" title="Created Date"></display:column>
                            		<display:column property="lastModBy.actualUserName" title="Last Modified By"></display:column>
                            		<display:column property="lastModTsForDisplay" title="Last Modified Time"></display:column>
                            		<%--
                            		<c:if test="${miniBean.showEditDetailsRow}">
                            		  <display:column property="viewDetails" title="" media="html" href="${appContext}/editSingleSalaryType.do" paramId="bid" paramProperty="id" target="_blank" rel="noopener"></display:column>
                                    </c:if>
                                    --%>
                            		<display:setProperty name="paging.banner.placement" value="top" />
                              </display:table>
                           </td>
                        </tr>
                        <tr>
                           <td>
                              &nbsp; <input type="image" name="_cancel" value="cancel" title="Close Report" src="images/close.png">
                           </td>
                        </tr>
                </table>
            </td>
        </tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
</form:form>
<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
<script language="javascript">
             $(document).ready(function(){
                    if($("#consolidatedBindInd").val() === "true"){
                       $('input[name=consolidatedBindSwitch]').attr('checked', true);
                              $("#consolidatedStatus").html("Yes");
                   }
                   if($("#selectableBindInd").val() === "true"){
                              $('input[name=selectableBindSwitch]').attr('checked', true);
                              $("#selectableStatus").html("Yes");
                   }
                   if($("#pensionExemptIndBind").val() === "true"){
                               $('input[name=pensionExemptIndBindSwitch]').attr('checked', true);
                               $("#pensionExemptStatus").html("Yes");
                    }
             });
            var check = "${ce}";
            if(check==='0'){
                $(function() {
                    $("#paytbl").DataTable({
                        "order" : [ [ 1, "asc" ] ]
                    });
     		    });
            }
            else if(check==='1'){
                $(function() {
                    $("#paytbl").DataTable({
                        "order" : [ [ 6, "desc" ] ]
                    });
     		    });
            }
           else{
                $(function() {
                      $("#paytbl").DataTable({
                          "order" : [ [ 1, "asc" ] ]
                      });
       		    });
           }

           var stid = document.getElementById ( "ftid" ).value;
           console.log("stid is "+stid);
           $('#vst').click(function(e){
              var url = "${appContext}/viewSalaryType.do";
              console.log("url is "+url);
              $.ajax({
                       type: "GET",
                       success: function (response) {
                           // do something ...
                           window.location.href ="${appContext}/viewSalaryType.do?stid="+stid;
                        },
                       error: function (e) {

                       }
              });
           });
</script>
</body>
</html>