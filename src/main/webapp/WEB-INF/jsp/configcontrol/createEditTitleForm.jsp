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
 
</head>

<body class="main">
<form:form modelAttribute="titleBean">
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
								 <spring:hasBindErrors name="titleBean">
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
										<span class="required">Title Name*</span>&nbsp;
									</td>
									<td align="left"><form:input path="name" size="25" maxlength="30"/></td>
								</tr>
                                <tr>
									<td align="right" nowrap>
										<span class="required">Title Description*</span>&nbsp;
									</td>
									<td align="left"> <form:input path="description" size="25" maxlength="30"/></td>
								</tr>								 


					        </table>							
						</td>
					</tr>
					
					</table>
					 
					<tr>
						<td class="buttonRow" align="right" >
						
						   <c:if test="${titleBean.editMode}">
							  &nbsp;<input type="image" name="submit" value="ok" title="Update" class="" src="images/update.png"> 
							</c:if>
							<c:if test="${not titleBean.editMode}">
							  &nbsp; <input type="image" name="submit" value="ok" title="Update" class="" src="images/create_h.png"> 
							</c:if>
							 &nbsp; <input type="image" name="_cancel" value="cancel" title="Close View" src="images/close.png">
						 
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
											<p class="label">Click to Edit Title</p>
											<display:table name="dispBean" id="cadretbl" class="display table" export="false" sort="page" defaultsort="1" requestURI="${appContext}/editTitle.do">
											<display:column property="name" title="Cadre Name" media="html" href="${appContext}/editTitle.do" paramId="cid" paramProperty="id"></display:column>
											<display:column property="description" title="Description"></display:column>
											<display:column property="lastModBy.actualUserName" title="Last Modified By"></display:column>
											<display:column property="lastModTs" title="Last Modified By"></display:column>
											<display:column property="modifierOrg" title="Modifier Org."></display:column>
											<display:setProperty name="paging.banner.placement" value="bottom" />
										</display:table>
										</td>
                                    </tr>
                                    
                                    </table>

                                       <p>
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

</body>
</html>