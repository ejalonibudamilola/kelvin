<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<tr>
	<td>
		<div>
			<nav class="navbar navbar-default">
			
				<div class="container-fluid">
				
					<div class="navbar-header">
						<button type="button" class="navbar-toggle collapsed"data-toggle="collapse" data-target="#navbar" aria-expanded="false"
							aria-controls="navbar">
							<span class="sr-only">Toggle navigation</span> <span
								class="icon-bar"></span> <span class="icon-bar"></span> <span
								class="icon-bar"></span>
						</button>
					</div>
					
					<div id="navbar" class="navbar-collapse collapse mainHeaderNav">
					
						<ul class="nav navbar-nav" style="margin-left: -30px;">
							<li class="active" style="background-color: #FFFFFF">
								<a href="${appContext}/determineDashBoard.do" title="Application Main Page">
									<span class="fa fa-home fa-2x"></span>
								</a>
							</li>

							<c:if test="${not empty _userMenuCategories}">
								 
								
								
								<c:forEach items="${_userMenuCategories}" var="menuCat">
								
									<li class="dropdown" id="menu_cat_${menuCat.id}" onclick="createRootMenuLinks(this);" work="" ondblclick="goToLink(this)">
										<a id="headerAnchor_${menuCat.id}" role="button" data-toggle="dropdown" class="dropdown-toggle" data-target="#">
											<b style="text-color: black; font-size:11px;">${menuCat.name}</b> 
											<span class="caret"></span>
										</a>
										
										<ul class="dropdown-menu " role="menu" aria-labelledby="dropdownMenu"></ul>
									</li>
								</c:forEach>
							</c:if>
							<li class="dropdown">
							    <a href="${pageContext.request.contextPath}/manual.do">
							        <b style="text-color: black; font-size:11px;">Help</b>
							    </a>
							</li>
						</ul>
						
						<div class = "pull-right" style="margin-right: -40px;" >
							<ul class="nav pull-right">
								<li class="dropdown active">
									
									<a href="#" class="dropdown-toggle"
										data-toggle="dropdown"><i style="color: black;"   class="fa fa-user fa-2x"></i>&nbsp;
											<!--<b class="caret"></b>
									-->
									</a>
										<ul class="dropdown-menu pull-right">
											<li class = "center"><b><i><font color="green"><%=session.getAttribute("userName")%></font></i></b></li>
		                                    <li class="divider"></li>
											<%-- <li><a  href="${pageContext.request.contextPath}/signOut.do"><i
													class="fa fa-mail"></i><b>Messages</b></a></li>
											<li class="divider"></li> --%>
											<li><a  href="${pageContext.request.contextPath}/signOut.do"><i style="color: red;"
													class="fa fa-sign-out fa-1x "></i>&nbsp;<b>SignOut</b></a></li>
										</ul>
									</li>
							</ul>
						</div>
						
						<div class = "pull-right" >
						
							<ul class="nav pull-right">
							
								<li class="dropdown active">
									<a href="#" class="dropdown-toggle"
										data-toggle="dropdown"><i style="color: black;" class="fa fa-globe fa-2x"></i>
										<c:if test="${not empty _notificationBeans}">
											<span class = "noti_bubble" style = "top: 6px; right: 6px;">
												<c:out value="${fn:length(_notificationBeans)}" /></span>
										</c:if>
									</a>
									
									
									<ul class="dropdown-menu pull-right">
	                                    <li class="divider"></li>
	                                    <c:choose>
		                                    <c:when test="${not empty _notificationBeans}">
			                                    <c:forEach items="${_notificationBeans}" var="notifBean">
													<li><a  href="${pageContext.request.contextPath}${notifBean.name}"><i style="color: red;"
														class="fa fa-caret-right fa-1x "></i>&nbsp;<b>${notifBean.description}</b>
														<span class = "noti_bubble_disp">${notifBean.id}</span>
														</a></li>
												</c:forEach>
		                                    </c:when>
		                                    <c:otherwise>
		                                    	<li><i style="color: red;"
														class="fa fa-1x "></i>&nbsp;<b>No Notifications</b></li>
		                                    </c:otherwise>
	                                    </c:choose>
									</ul>
								</li>
							</ul>
						</div>

                        <div class = "pull-right">
							<ul class="nav pull-right">
								<li class="dropdown active">
									<a href="#" class="dropdown-toggle"	data-toggle="dropdown"><i style="color: black;" class="fa fa-envelope fa-2x"></i>
										    <c:if test="${messageCount != 0}">
                                        		<span class = "noti_bubble" style = "top: 6px; right: 6px;">
                                        			<c:out value="${messageCount}" />
                                        		</span>
                                        	</c:if>
									</a>

									<ul class="dropdown-menu pull-right">
									        <li class="divider"></li>
									        <li class = "center"><b><i><font color="green"><a href="${pageContext.request.contextPath}/messaging.do?com">New Message</a></font></i></b></li>
											<li class="divider"></li>
											<li class = "center">
											    <b><i><font color="green">
											        <a href="${pageContext.request.contextPath}/messaging.do">Received Messages
											            <c:if test="${messageCount != 0}">
											                <i style="color: red;" class="fa fa-caret-right fa-1x "></i>&nbsp;
                                                            <span class = "noti_bubble_disp">${messageCount}</span>
                                                        </c:if>
											        </a></font>
											    </i></b>
											</li>
											<li class="divider"></li>
											<li class = "center"><b><i><font color="green"><a href="${pageContext.request.contextPath}/messaging.do?sent">Sent Messages</a></font></i></b></li>
									        <li class="divider"></li>
									</ul>
								</li>
							</ul>
						</div>
					</div>
					<!--/.nav-collapse -->
                    <div>
                      <p id="showDeployStatus" style="color:red; font-size:14px; display:none">Application going down in a minute for upgrade, please check back</p>
                      <!--<marquee id="showDeployStatus" loop="infinite" style="color:red; font-size:14px; display:none">Application going down in a minute for upgrade, please check back</marquee>-->
					</div>
				</div>
				<!--/.container-fluid -->
			</nav>
		</div>
	</td>
</tr>
