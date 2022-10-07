<%@ taglib prefix="gnl" uri="/WEB-INF/tlds/copyright.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style>
    #logoff{
        display:none;
    }
</style>

<td colspan="" bgcolor="#33C0C8" align="center">
	<gnl:copyright startYear="2020" /> 
</td>

<script src="<c:url value="/scripts/jquery-1.12.0.min.js"/>"></script>
<script src="<c:url value="/scripts/jquery.jscroll.min.js"/>"></script>
<script src="<c:url value="/scripts/jquery-ui.js"/>"></script>
<script src="<c:url value="/scripts/jquery-migrate-1.3.0.min.js"/>"></script>
<script src="${appContext}/scripts/bootstrap.js"></script>
<script src="${appContext}/scripts/bootstrap-toggle.js"></script>
<script src="<c:url value="/scripts/utility_script.js"/>"></script>
<script src="${appContext}/scripts/additions.js"></script>
<script src="<c:url value="/scripts/jquery.userTimeout.js"/>"></script>

<script>
var LOGIN_URL=APP_CONTEXT+'/j_spring_security_check'

//add a header for AJAX requests so we can filter them out
$(document).ajaxSend(function(e,xhr,settings){
	xhr.withCredentials = true;
	xhr.setRequestHeader("ajax-request", "true");
});

$(document).ajaxError(function(e,jqXhr,settings){
	if(settings.url != LOGIN_URL && jqXhr.status=='401'){
		//redirect to login page.
		location.href = APP_CONTEXT + "/securedLoginForm.jsp";
	}
});

    $(document).userTimeout({
        session: 300000,
        force: 48000,
        logouturl: 'signOut.do?r=t',
        notify: true,
        timer: true,
        ui: 'auto',
        debug: false,
        modalTitle: 'IPPMS Session Timeout Warning',
        modalBody: 'You will be logged out due to inactivity. Click on the Stay Logged In button to stay logged on.',
	    modalStayLoggedBtn: 'Stay Logged In'
	})
</script>

<c:if test="${saved}">
	<script type="text/javascript">displayPageNotification('${savedMsg}');</script>
</c:if>
