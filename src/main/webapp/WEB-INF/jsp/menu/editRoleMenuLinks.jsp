<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>

<html>
    <head>
        <title>${pageTitle}</title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css" />
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
        <link rel="stylesheet" href="${appContext}/dataTables/ext/scroller/css/scroller.dataTables.min.css" type="text/css">
    </head>

    <body class="main">
        <table class="main" width="74%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

            <%@ include file="/WEB-INF/jsp/headerFile.jsp"%>

            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

                        <tr>
                            <td>
                                <div class="title">${mainHeader}</div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                * = required<br /><br />

                                <form:form modelAttribute="roleMenuMasterBean">

                                    <spring:hasBindErrors name="roleMenuMasterBean">
                                        <div id="topOfPageBoxedErrorMessage" style="display: block;">
                                            <ul>
                                                <c:forEach var="errMsgObj" items="${errors.allErrors}">
                                                    <li><spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}" /></li>
                                                    </c:forEach>
                                            </ul>
                                        </div>
                                    </spring:hasBindErrors>

                                    <table class="hundredPercentWidth alignLeft" border="0" cellspacing="0" cellpadding="3">
                                        <!--<tr>
                                            <td class="tenPercentWidth alignLeft verticalAlignTop">
                                                <span><spring:message code="roleMenuLink.form.label.selectRole" /></span>
                                                <span class="required-asterik">*</span>
                                            </td>
                                            <td class="ninetyPercentWidth alignLeft verticalAlignTop">
                                                <form:select path="role.id" disabled="${roleMenuMasterBean.editMode}">
                                                    <form:option value="-1"><spring:message code="form.select.option.default"/></form:option>
                                                    <form:options items="${roleList}" itemLabel="name" itemValue="id"/>
                                                </form:select>
                                            </td>
                                        </tr>
                                        -->
                                        <tr>
                                        <td>
                                        <div class="panel panel-danger">
												  <div class="panel-heading">
												    <h3 class="panel-title">Role Configuration</h3>
												  </div>
												  <div class="panel-body">
												  	<table>
												  		<tr>
				                                            <td class="alignLeft verticalAlignTop">
				                                                <span><b><spring:message code="roleMenuLink.form.label.name" /></b></span>
				                                                <span class="required-asterik">*</span>
				                                            </td>
				                                            <td class="alignLeft verticalAlignTop">
				                                                <form:input path="role.name" size="70" maxlength="100"/>
				                                            </td>
				                                        </tr>
				                                        <tr>
				                                            <td class="alignLeft verticalAlignTop">
				                                                <span><b><spring:message code="roleMenuLink.form.label.desc" /></b></span>
				                                                <span class="required-asterik">*</span>
				                                            </td>
				                                            <td class="alignLeft verticalAlignTop">
				                                                <form:input path="role.description" size="70" maxlength="200"/>
				                                            </td>
				                                        </tr>
				                                        
				                                        <tr>
				                                            <td class="alignLeft verticalAlignTop">
				                                                <span><b><spring:message code="roleMenuLink.form.label.codeName" /></b></span>
				                                                <span class="required-asterik">*</span>
				                                            </td>
				                                            <td class="alignLeft verticalAlignTop">
				                                                <form:input path="role.displayName" size="50" maxlength="50"/>
				                                            </td>
				                                        </tr>
				                                        
				                                        <c:if test="${roleBean.superAdmin}">
															<tr>
																<td class="alignLeft verticalAlignTop"><b>SUPER ADMIN ACCESS</b></td>
																<td class="alignLeft verticalAlignTop"><form:checkbox path="role.adminUserFlag" /></td>
															</tr>
														</c:if>
												  	</table>
												  </div>
										</div>
										</td>
										</tr>
										
                                         
                                        
                                        <tr><td colspan="2">&nbsp;</td></tr>
                                        <tr>
                                            <td colspan="2">
                                            <div class="panel panel-danger">
												  <div class="panel-heading">
												    <h3 class="panel-title">Role Menu Links Configuration</h3>
												  </div>
												  <div class="panel-body">
												      <table id="menuTable" class="hundredPercentWidth table display"  cellspacing="0" cellpadding="0">
                                                    <thead>
                                                        <tr>
                                                            <th class="fivePercentWidth"><input type="checkbox" id="selectAll"/></th>
                                                            <th class="twentyPercentWidth"><spring:message code="roleMenuLink.form.label.name"/></th>
                                                            <th class="twentyPercentWidth"><spring:message code="roleMenuLink.form.label.category"/></th>
                                                            <th class="twentyPercentWidth"><spring:message code="roleMenuLink.form.label.parent"/></th>
                                                            <th class="thirtyFivePercentWidth"><spring:message code="roleMenuLink.form.label.desc"/></th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach items="${roleMenuMasterBean.roleMenuLinks}" var="menuLink" varStatus="currIndex">
                                                            <tr>
                                                                <td>
                                                                    <form:checkbox path="roleMenuLinks[${currIndex.index}].selected" cssClass="menuChkBx" 
                                                                    				id="_${menuLink.id}" onchange="checkParentMenuLink(this, '${menuLink.id}');"/>
                                                                    <input type="hidden" value="${menuLink.parentMenuLink.id}" class="menu-link-hidden-input-${menuLink.parentMenuLink.id}" id="${menuLink.id}"/>
                                                                </td>
                                                                <td><c:out value="${menuLink.name}" /></td>
                                                                <td><c:out value="${menuLink.menuLinkCategory.name}" /></td>
                                                                <td>
                                                                    <c:if test="${menuLink.hasParentLink}">
                                                                        <c:out value="${menuLink.parentMenuLink.name}" />
                                                                    </c:if>
                                                                </td>
                                                                <td><c:out value="${menuLink.description}" /></td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
												  </div>
												</div>
												
												
                                            
                                             
                                            </td>
                                        </tr>

                                        <tr>
                                            <td class="formButtonRow alignLeft">
                                                <input type="submit" name="_save" value="<spring:message code="form.button.save"/>" class="form-btn"/>
                                                <input type="submit" name="_cancel" value="<spring:message code="form.button.cancel"/>" class="form-btn"/>
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
                <%@ include file="/WEB-INF/jsp/footerFile.jsp"%>
            </tr>
        </table>

        <script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
        <script src="${appContext}/dataTables/ext/scroller/js/dataTables.scroller.min.js"></script>

        <script type="text/javascript">
            $(function () {
                $("#selectAll").change(function (e) {
                    $(".menuChkBx").prop("checked", (this.checked));
                });

                $("#menuTable").DataTable({
                    "order": [[1, "asc"]],
                    //can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
                    //also properties higher up, take precedence over those below
                    "columnDefs": [
                        {"targets": [0, 4], "orderable": false, "searchable": false}
                        //{"targets": [0, 1], "orderable" : false }
                    ],
                    "scroller": true,
                    "scrollY": 800
                });
            });

            function checkParentMenuLink(element, hiddenInputFieldId) {
    		    var parentMenuLinkId = jQuery("#"+hiddenInputFieldId).val();
    		 
    		  //check if any child link is checked, if not uncheck the parent link
    		  if( parentMenuLinkId != "" ){
    			  document.getElementById("_" + parentMenuLinkId).checked = false;
    		    var parentMenuLinks = jQuery( '.menu-link-hidden-input-'+parentMenuLinkId ).get();
    		    for( var i = 0; i < parentMenuLinks.length; i++ ) {
    		    	var currMenuLink = document.getElementById("_" + parentMenuLinks[i].id).checked;
    		    	if(currMenuLink){
    		    		document.getElementById("_" + parentMenuLinkId).checked = true;
    		    	}
    			}
    		  }

    		  //if current child link is checked, please ensure parent link is checked
    		  if( element.checked && parentMenuLinkId != "" ){
    			  document.getElementById("_" + parentMenuLinkId).checked = true;
    		  }
    		    
    		}
    		
        </script>
    </body>
</html>