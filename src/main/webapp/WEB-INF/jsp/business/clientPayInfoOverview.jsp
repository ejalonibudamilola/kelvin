<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>
<head>
<title>Setup | Pay Setup</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
</head>

<body class="main">
<form:form modelAttribute="payPolicyBean">
	<table class="main" width="70%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
				
					<tr>
						<td colspan="2">
							<div class="title"><c:out value="${payPolicyBean.businessName}"/>'s Pay Policies</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainbody" id="mainbody">
							<table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
								<tr align="left">
									<th class="activeTH" align="left"><a name="PayCycles"></a>Pay Schedules</TH>
								</tr>
								<tr>
									<td class="activeTD">
										<table border="0" cellspacing="0" cellpadding="3">	
											<tr>
												<td>
													These are the <b>pay schedules</b> you have created.  			
													To add another schedule, click the Create button below.			
												</td>
											</tr>
											<tr>
												<td>
													<table width="100%" border="0" cellspacing="0" cellpadding="2">
														<tr>
															<td><b>Description</b></td>
															<td><b>Frequency</b></td>
															<td>&nbsp;</td>
														</tr>													
														
															<c:forEach items="${payPolicyBean.busPaySchedule}" var="sched">
															<tr>
															<td><c:out value="${sched.descr}"/> <c:if test="${sched.isDefault}"><b>(Default)</b></c:if></td>
															<td><c:out value="${sched.frequency}"/></td>														
															<td nowrap>
															<A HREF='${appContext}/editBusPayPeriodForm.do?bid=${sched.id}&ppid=${sched.payPeriodInstId}'>Edit</A>
															</td>	
															</tr>
															</c:forEach>												
																
													</table>
												</td>
											</tr>
											<tr>
												<td align=right>
												<A HREF='${appContext}/editBusPayPeriodForm.do?bid=${payPolicyBean.id}' id="create" onclick="" >
												<IMG SRC='images/create_h.png' ALT='Create' CLASS='' NAME='create' BORDER="0"></A>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainbody" id="mainbody">
							<table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left">
								<tr>
									<th class="activeTH" align="left" NOWRAP>
										<a name="PTOPolicies"></a>Vacation and Sick Leave Policies
									</TH>
								</TR>
								<tr>
									<td class="activeTD">
										<table border="0" cellspacing="0" cellpadding="5">	
											<tr>
												<td>These are the <b>vacation and sick leave policies</b> you have created.  	
												To add another policy, click the Create button below.		
												</td>
											</tr>
											<tr>
												<td>
													<table width="100%" border="0" cellspacing="0" cellpadding="2">
														<tr>
															<td><b>Description</b></td>
															<td><b>Category</b></td>
															<td><b>Rate</b></td>
															<td><b>Frequency</b></td>
															<td>&nbsp;</td>
														</tr>
														<c:forEach items="${payPolicyBean.busPayPolicyInfo}" var="policy">
														<tr>					 
															<td><c:out value="${policy.dispDescr}"/></td>
															<td><c:out value="${policy.payPolicyTypes.name}"/></td>													
															<td><c:out value="${policy.dispRate}"/></td>						
															<td><c:out value="${policy.hoursAccrue}"/></td>						
															<td nowrap>	
																<A HREF='${appContext}/busPayPolicyForm.do?bpid=${policy.id}'>Edit</A>
															</td>						
														</tr>
													</c:forEach>									
														
													</table>
												</td>
											</tr>
											<tr>
												<td align=right>
											<A HREF='${appContext}/createBusPayPolicyForm.do?bcid=${payPolicyBean.id}'>
											<IMG SRC='images/create_h.png' ALT='Create' CLASS='' NAME='createPTOPolicy' BORDER="0"/></A>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td>
										<br>
										<b>Next Step:</b>
										 To assign new pay types, policies, 
										 or schedules to employees you have already set up, go to the 
										 <a href='${appContext}/busEmpOverviewForm.do?bcid=${payPolicyBean.id}'>Client Employee List</a>. 
										 Click on an employee's name to edit their information.  
										 Each time you add an employee, you can assign policies that you've already created to that employee.
										<br><br><b>Go to</b>: 
										<A HREF='${appContext}/setUpNewEmployee.do' >Add an employee</A>
										</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
	</form:form>
</body>
</html>
