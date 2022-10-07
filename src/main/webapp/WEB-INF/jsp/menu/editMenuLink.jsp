<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>

<spring:message code="form.select.option.default" var="defaultSelect"/>

<html>
    <head>
        <title><c:out value="${pageTitle}" /></title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css" />
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
	<link rel="stylesheet" href="${appContext}/dataTables/ext/scroller/css/scroller.dataTables.min.css" type="text/css">
    </head>

    <body class="main">
        <table class="main" width="74%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

            <%@ include file="/WEB-INF/jsp/headerFile.jsp"%>

            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

                        <tr>
                            <td>
                                <div class="title"><c:out value="${mainHeader}"/></div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                <span class="required-asterik">*</span> = required<br /><br />

                                <form:form modelAttribute="menuLinkBean">

                                    <spring:hasBindErrors name="menuLinkBean">
                                        <div id="topOfPageBoxedErrorMessage" style="display: block;">
                                            <ul>
                                                <c:forEach var="errMsgObj" items="${errors.allErrors}">
                                                    <li><spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}" /></li>
                                                    </c:forEach>
                                            </ul>
                                        </div>
                                    </spring:hasBindErrors>

                                    <table class="ninetyPercentWidth alignLeft" border="0" cellspacing="0" cellpadding="3">
                                        <tr>
                                            <td class="activeTH alignLeft"><c:out value="${mainHeader}"/></td>
                                        </tr>
                                        <tr>
                                            <td class="activeTD">
                                                <table class="hundredPercentWidth" border="0" cellspacing="0" cellpadding="2">
                                                    <tr>
                                                        <td class="thirtyPercentWidth alignRight verticalAlignTop">
                                                            <span class="form-label"><spring:message code="menuLink.form.label.name"/></span>
                                                            <span class="required-asterik">*</span>
                                                        </td>
                                                        <td class="seventyPercentWidth alignLeft verticalAlignTop">
                                                            <form:input path="name" size="40"/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight verticalAlignTop">
                                                            <span class="form-label"><spring:message code="menuLink.form.label.parent"/></span>
                                                        </td>
                                                        <td class="alignLeft verticalAlignTop">
                                                            <form:select path="parentMenuLink.id" id="parentMenu">
                                                                <form:option value="-1"><c:out value="${defaultSelect}" /></form:option>
                                                                <form:options items="${menuList}" itemLabel="name" itemValue="id" />
                                                            </form:select>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight verticalAlignTop">
                                                            <span class="form-label"><spring:message code="menuLink.form.label.category"/></span>
                                                            <span class="required-asterik">*</span>
                                                        </td>
                                                        <td class="alignLeft verticalAlignTop">
                                                            <form:select path="menuLinkCategory.id" id="category">
                                                                <form:option value="-1"><c:out value="${defaultSelect}" /></form:option>
                                                                <form:options items="${categoryList}" itemLabel="name" itemValue="id"/>
                                                            </form:select>

                                                            <span class="spacerTwentyPix"></span>
                                                            <span class="formInputDescText">
                                                                <spring:message code="menuLink.form.label.categoryDesc"/>
                                                            </span>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight verticalAlignTop">
                                                            <span class="form-label"><spring:message code="menuLink.form.label.url"/></span>
                                                        </td>
                                                        <td class="alignLeft verticalAlignTop">
                                                            <form:input path="linkUrl" size="80"/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight verticalAlignTop">
                                                            <span class="form-label"><spring:message code="menuLink.form.label.dbDisplay"/></span>
                                                        </td>
                                                        <td class="alignLeft verticalAlignTop">
                                                            <form:checkbox path="displayOnDashboardBind" />

                                                            <span class="spacerTwentyPix"></span>
                                                            <span class="formInputDescText">
                                                                <spring:message code="menuLink.form.label.dbDisplayDesc"/>
                                                            </span>
                                                        </td>
                                                    </tr>
                                                    <c:if test="${roleBean.sysUser}">
                                                    	<tr>
	                                                        <td class="alignRight verticalAlignTop">
	                                                            <span class="form-label"><spring:message code="menuLink.form.label.sysUser"/></span>
	                                                        </td>
	                                                        <td class="alignLeft verticalAlignTop">
	                                                            <form:checkbox path="systemUserBind" />
	                                                        </td>
	                                                    </tr>
	                                                    <c:if test="${not menuLinkBean.editMode}">
	                                                    <tr>
                                                         <td class="alignRight verticalAlignTop">
                                                         <span class="form-label"><spring:message code="menuLink.form.label.casp"/></span>
                                                         </td>
                                                         <td class="alignLeft verticalAlignTop">
                                                         <form:checkbox path="caspBind" />
                                                         </td>
                                                         </tr>
                                                         <tr>
                                                           <td class="alignRight verticalAlignTop">
                                                            <span class="form-label"><spring:message code="menuLink.form.label.subeb"/></span>
                                                           </td>
                                                           <td class="alignLeft verticalAlignTop">
                                                          <form:checkbox path="subebBind" />
                                                           </td>
                                                          </tr>
                                                          <tr>
                                                           <td class="alignRight verticalAlignTop">
                                                           <span class="form-label"><spring:message code="menuLink.form.label.localGovt"/></span>
                                                            </td>
                                                           <td class="alignLeft verticalAlignTop">
                                                           <form:checkbox path="lgBind" />
                                                           </td>
                                                            </tr>
                                                         <tr>
                                                           <td class="alignRight verticalAlignTop">
                                                           <span class="form-label"><spring:message code="menuLink.form.label.state.pension"/></span>
                                                            </td>
                                                           <td class="alignLeft verticalAlignTop">
                                                           <form:checkbox path="statePensionBind" />
                                                           </td>
                                                           </tr>
                                                            <tr>
                                                           <td class="alignRight verticalAlignTop">
                                                           <span class="form-label"><spring:message code="menuLink.form.label.lg.pension"/></span>
                                                            </td>
                                                           <td class="alignLeft verticalAlignTop">
                                                           <form:checkbox path="lgPensionBind" />
                                                           </td>
                                                           </tr>
                                                           <tr>
                                                           <td class="alignRight verticalAlignTop">
                                                           <span class="form-label"><spring:message code="menuLink.form.label.executive"/></span>
                                                            </td>
                                                           <td class="alignLeft verticalAlignTop">
                                                           <form:checkbox path="lgPensionBind" />
                                                           </td>
                                                           </tr>
                                                           </c:if>
                                                    </c:if>
                                                    
                                                    <tr>
	                                                        <td class="alignRight verticalAlignTop">
	                                                            <span class="form-label"><spring:message code="menuLink.form.label.hiddenLink"/></span>
	                                                        </td>
	                                                        <td class="alignLeft verticalAlignTop">
	                                                            <form:checkbox path="innerLinkBind" title="Select this box to indicate that this is an Inner Hidden Link not to be displayed on User Profile Configuration" />
	                                                        </td>
	                                                    </tr>
                                                    
                                                    <tr>
                                                        <td class="alignRight verticalAlignTop">
                                                            <span class="form-label"><spring:message code="menuLink.form.label.desc"/></span>
                                                            <span class="required-asterik">*</span>
                                                        </td>
                                                        <td class="alignLeft verticalAlignTop">
                                                            <form:textarea path="description" size="200" cols="60" rows="3"/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td colspan="2" style="padding-left: 30%;">
                                                            <br><br>
                                                            <form:checkbox path="dashboardMenuLink" id="dbMenuLinkChkBx"/>
                                                            <span class="form-label"><spring:message code="menuLink.form.label.dbLink"/></span>
                                                            
                                                            <!-- Should we hide this table and show only when clicked -->
                                                            <br><br>
                                                            <table id="menuCatTable" class="hundredPercentWidth" cellspacing="0" cellpadding="0">
                                                                <thead>
                                                                    <tr>
                                                                        <th class="tenPercentWidth"><input type="checkbox" id="selectAll"/></th>
                                                                        <th class="thirtyPercentWidth"><spring:message code="menuLink.form.label.category"/></th>
                                                                        <th><spring:message code="menuLink.form.label.desc"/></th>
                                                                    </tr>
                                                                </thead>
                                                                <tbody>
                                                                    <c:forEach items="${menuLinkBean.tabMenuCategories}" var="menuCat" varStatus="currIndexObj">
                                                                        <tr>
                                                                            <td>
                                                                                    <form:checkbox path="tabMenuCategories[${currIndexObj.index}].selected" 
                                                                                                   class="menuChkBx" disabled="${not menuLinkBean.dashboardMenuLink}"/>
                                                                            </td>
                                                                            <td><c:out value="${menuCat.name}" /></td>
                                                                            <td><c:out value="${menuCat.description}" /></td>
                                                                        </tr>
                                                                    </c:forEach>
                                                                </tbody>
                                                            </table>
                                                        </td>
                                                    </tr>
                                                </table>
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

        <script type="text/javascript">
            $(function () {
                $("#parentMenu").change(function (e) {
                    if (this.value === "-1") {
                        $('#category').prop("disabled", false);
                    } else {
                        var parentMenuId = this.value;
                        //use the id of the parent to get the category
                        $.ajax({
                            url: APP_CONTEXT + "/menu/getMenuCategoryByMenuId.do?menuId=" + parentMenuId,
                            type: 'GET',
                            dataType: 'json',
                            async: false,
                            statusCode: {
                                200: function (data, status, jqxhr) {
                                    if (data) {
                                        $('#category').val(data).prop("disabled", true);
                                    }
                                }
                            }
                        });
                    }
                });
                
                $("#dbMenuLinkChkBx").change(function (){
                    $(".menuChkBx").prop("disabled", !this.checked);
                });
                
                $("#selectAll").change(function (e) {
                    $(".menuChkBx").prop("checked", (this.checked));
                });
            });
        </script>
    </body>
</html>