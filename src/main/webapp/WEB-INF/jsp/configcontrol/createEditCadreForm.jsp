<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> <c:out value="${displayTitle}"/>  </title>
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

<body class="main">
<style>
    #cadretbl thead tr th{
        font-size:8pt !important;
    }
</style>
<form:form modelAttribute="cadreBean">
<c:set value="${displayList}" var="dispBean" scope="request"/>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					<tr>
						<td>
								<div class="title"><c:out value="${displayTitle}"/></div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						<font color="red"><b>* = required</b></font><br/><br/>
				
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="cadreBean">
         							 <ul>
            							<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            							</c:forEach>
         								</ul>
     							 </spring:hasBindErrors>
				</div>
				<table border="0" cellspacing="0" cellpadding="3" width="60%" align="left" >
					<tr align="left">
						<td class="activeTH"><c:out value="${displayTitle}"/></td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="1" cellpadding="2">

								<tr>
									<td align="right" nowrap>
										<span class="required">Cadre Name*</span>&nbsp;
									</td>
									<td align="left"><form:input path="name" size="25" maxlength="30"/></td>
								</tr>
                                <tr>
									<td align="right" nowrap>
										<span class="required">Cadre Description*</span>&nbsp;
									</td>
									<td align="left"> <form:input path="description" size="25" maxlength="30"/></td>
								</tr>								 
								 <tr>
									<td align="right" nowrap>
										<span class="required">Pay Group</span>&nbsp;
									</td>
									<td align="left"><form:select path="salaryType.id">
										<form:option value="-1">&lt;Select&gt;</form:option>
										<c:forEach items="${salaryTypeList}" var="hList">
						                <form:option value="${hList.id}" title="${hList.description}">${hList.name}</form:option>
						                </c:forEach>
										</form:select>
										</td>
								</tr>

                                <tr>
                                      <td align="right"  nowrap>
                                       <span class="required" title="">Default Cadre*</span></td>
                                     <td align="left" nowrap>

                                      <form:input path="defaultIndBind" id="showDefaultIndBind"  style="display:none"/>
                                         <label class="toggle">
                                           <input id="showDefaultIndBindSwitch" name="showDefaultIndBindSwitch" type="checkbox" onClick="toggleIt(this,'showDefaultIndBindSwitch','showDefaultIndBindStatus','showDefaultIndBind')" title="Select this box to designates this Cadre as 'Default'">
                                           <span class="roundbutton"></span>
                                           </label>
                                           <span id="showDefaultIndBindStatus">No</span>
                                      </td>
                                    </tr>
								<c:if test="${cadreBean.editMode}">

                                <tr>
                                      <td align="right"  nowrap>
                                       <span class="required" title="">Status*</span></td>
                                     <td align="left" nowrap>

                                      <form:input path="selectableBind" id="showSelectableIndBind"  style="display:none"/>
                                         <label class="toggle">
                                           <input id="showSelectableIndBindSwitch" name="showSelectableIndBindSwitch" type="checkbox" onClick="toggleIt(this,'showSelectableIndBindSwitch','showSelectableIndBindStatus','showSelectableIndBind')" title="On = 'Active', Off = 'Inactive' - Active means Cadre will be available for selection. Inactive is the converse.">
                                           <span class="roundbutton"></span>
                                           </label>
                                           <span id="showSelectableIndBindStatus">Inactive</span>
                                      </td>
                                    </tr>
								</c:if>

					        </table>							
						</td>
					</tr>
					
					</table>
					 
					<tr>
						<td class="buttonRow" align="right" >
						
						   <c:if test="${cadreBean.editMode}">
							  &nbsp;<input type="image" name="submit" value="ok" title="Update" class="" src="images/update.png"> 
							</c:if>
							<c:if test="${not cadreBean.editMode}">
							  &nbsp; <input type="image" name="submit" value="ok" title="Update" class="" src="images/create_h.png"> 
							</c:if>
						 
						</td>
					</tr>
					 </table>

					<table width="100%">
					 <tr>
                                        <td>
                                            &nbsp;                                    
                                        </td>
                                       
                                    </tr>
                                    <tr>
                                        <td valign="top">
											<p class="label">Click to Edit Rank</p>
											<display:table name="dispBean" id="cadretbl" class="display table" export="false" sort="page" defaultsort="1" requestURI="${appContext}/editCadre.do">
											<display:column property="name" title="Cadre Name" media="html" href="${appContext}/editCadre.do" paramId="cid" paramProperty="id"></display:column>
											<display:column property="description" title="Description"></display:column>
											<display:column property="payGroupName" title="Pay Group"></display:column>
											<display:column property="createdBy.actualUserName" title="Creator"></display:column>
											<display:column property="creationDate" title="Created Date"></display:column>
											<%--<display:column property="lastModBy.actualUserName" title="Last Modified By"></display:column>--%>
											<display:column property="lastModTs" title="Last Modified"></display:column>
											 <display:setProperty name="paging.banner.placement" value="bottom" />								    
										</display:table>
										</td>
                                    </tr>
                                    </table>
                                        &nbsp; <input type="image" name="_cancel" value="cancel" title="Close View" src="images/close.png">
                                      </td>
                                    </tr>
                                    
                                  
				<tr>
			      <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		     </tr>
			 
			</table>
	 </form:form>

	<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
	<script type="text/javascript">
                 var check = "${ce}";
                 console.log("ce is "+check);
                 if(check==='0'){
                     console.log("first if");
                     $(function() {
                         $("#cadretbl").DataTable({
                             "order" : [ [ 1, "asc" ] ],
                             //can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
                             //also properties higher up, take precedence over those below
                             "columnDefs":[
                                 {"targets": [0], "orderable" : false}
                             ]
                         });
          		    });
                 }
                 else if(check==='1'){
                     console.log("second if");
                     $(function() {
                         $("#cadretbl").DataTable({
                             "order" : [ [ 6, "desc" ] ],
                             //can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
                             //also properties higher up, take precedence over those below
                             "columnDefs":[
                                 {"targets": [0], "orderable" : false}
                             ]
                         });
          		    });
                 }
                else{
                     console.log("third if");
                     $(function() {
                           $("#cadretbl").DataTable({
                               "order" : [ [ 1, "asc" ] ],
                               //can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
                               //also properties higher up, take precedence over those below
                               "columnDefs":[
                                   {"targets": [0], "orderable" : false}
                               ]
                           });
            		    });
                }
    </script>
    <Script language="JavaScript">
     $(document).ready(function(){
            if($("#showDefaultIndBind").val() === "true"){
               $('input[name=showDefaultIndBindSwitch]').attr('checked', true);
                      $("#showDefaultIndBindStatus").html("Yes");
           }
          if($("#showSelectableIndBind").val() === "true"){
               $('input[name=showSelectableIndBindSwitch]').attr('checked', true);
                      $("#showSelectableIndBindStatus").html("Active");
           }
           });
    </Script>
</body>
</html>