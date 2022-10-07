<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="gnl" uri="/WEB-INF/tlds/copyright.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<link rel="stylesheet" href="css/bootstrap.min.css" type="text/css" />
<link rel="stylesheet" href="css/bootstrap-toggle.css" type="text/css" />
<link rel="stylesheet" href="font-awesome/css/font-awesome.min.css">
<link rel="stylesheet" href="css/multilevel.css" type="text/css" media="screen">

<c:set var="dbManager" value="${sessionScope.userDashboardMgr}" scope="page"/>
<c:set value="${sessionScope.epm_cert}" var="roleBean" scope="page"/>
<c:set value="${pageContext.request.contextPath}" var="appContext" scope="page" />
<c:set value="&#8358;" var="nairaHtml" scope="application" />

<script type="text/javascript">
	var APP_CONTEXT = '${pageContext.request.contextPath}'
</script>
<script type="text/javascript">
	var APP_CONTEXT = '${pageContext.request.contextPath}';

	//function displaying success/failure messages after certain actions
	function displayPageNotification(msgContent) {
		if (msgContent) {
			$(document).ready(function(){
				var notifDiv = $("#notification-msg");
				notifDiv.html(msgContent);
				notifDiv.hide();
				notifDiv.slideDown('slow');
			    setTimeout(function(){
			    	notifDiv.slideUp('slow', function(){
			             $(this).remove();
			        });
			    }, 10000);//10 seconds for message to disappear
			});
		}
	}
</script>
