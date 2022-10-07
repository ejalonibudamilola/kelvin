<%@ include file="/WEB-INF/jsp/includesForChart.jsp" %>
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
</tr>
