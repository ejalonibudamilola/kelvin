<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<tr>

		<td bgcolor = "#339900" align="center">
		    <img src=" <c:url value ="/images/${roleBean.clientLogo}"/>">
		</td>

		</tr>
		<tr>
			<td bgcolor = "#dedb6d"  align="center">
				<br><c:out value="${roleBean.clientDesc}"/><br>

			</td>
		</tr>
		<tr>
			<td>
				<table class="alignCenter hundredPercentWidth" border="0" cellpadding="0" cellspacing="0">
				      <%@ include file="/WEB-INF/jsp/menubar.jsp"%>
				      <tr>
				      <td>
				       <div id="parent-msg">
				       		<!-- Notification Message -->
							<div id="notification-msg" class="undisplay"></div>
				      </div>
				      </td>
				      </tr>
				     
				 </table>
				 
			</td>
		</tr>
