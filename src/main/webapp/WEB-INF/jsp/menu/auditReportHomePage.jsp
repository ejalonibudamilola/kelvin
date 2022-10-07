<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp"%>

<html>
	<head>
			<title>Audit Reports</title>
			<link rel="stylesheet" href="styles/omg.css" type="text/css" />
			<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">

	</head>

<body class="main">
	<table class="main" width="74%" border="1" cellspacing="0" cellpadding="0" align="center">
	
		<%@ include file="/WEB-INF/jsp/headerFile.jsp"%>
		
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

					<tr>
						<td>
							<div class="title">Audit Reports</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody hundredPercentWidth verticalTopAlign" id="mainbody">
						
							<div class="container">
							
								<div class="row">
								<div class="col-md-12">
									<div class="panel with-nav-tabs panel-warning" style=" height: auto; ">
										<div class="panel-heading">
											<ul class="nav nav-tabs">
													<c:forEach items="${_dbMenuCategories}" var="menuCat" varStatus="currStatus">
														<c:set value="${(currStatus.index + 1)}" var="currCount" />
														<c:choose>
															<c:when test="${currCount == 1}"><c:set value="active" var="li_Class"/></c:when>
															<c:otherwise><c:set value="" var="li_Class"/></c:otherwise>
														</c:choose>

														<li style="border-right: 2px solid #dad5cf" id="menu_tab_${menuCat.id}" class="${li_Class}" 
															onclick="handleTabClick(this);" tabplace="${currCount}">
															
															<a href="#tab${currCount}default" data-toggle="tab"> 
																<b style="font-size: 15px;"><u>${menuCat.name}</u></b>
															</a>
														</li>
													</c:forEach>
										     </ul>
										</div>
										
										<div class="panel-body" style ="margin-left:10px;margin-right:10px;">
											<div class="tab-content" align="center">
												<div disp=1008   class="tab-pane fade in active"  id="tab1default">
												   <ul class="nav nav-pills" role="tablist" style="margin : 1%;">
													     <c:forEach items="${_dbFirstTabLinks}" var="menuLink">
															   <li role="presentation" class="li_role">
																
																<div class="shadow" >
		                                                           <a class="fonttang" href="${appContext}${menuLink.linkUrl}">
		                                                           		<b><c:out value="${menuLink.name}" /></b>
		                                                           </a>
																   <br /> ${menuLink.description}.<br />
																</div>
															</li>
														 
														</c:forEach>
												   </ul>
												</div>
												
												
		                                        <c:forEach items="${_dbMenuCategories}" var="menuCat" varStatus="currStatus">
		                                        	<c:set value="${(currStatus.index + 1)}" var="currCount" />
		                                        	<c:if test="${currCount > 1}">
		                                        		<div   class="tab-pane fade"  id="tab${currCount}default">
														  <div class="shadow" style="display: none;">
														    <i style="color: black;"   class="fa fa-exclamation-circle fa-5x">No Tasks Available</i>
														  </div>
														  <ul class="nav nav-pills" style="margin : 2%;" role="tablist">
																	   
																		
													     </ul>
												    	</div>
		                                        	</c:if>
		                                        </c:forEach>
											</div>
										</div>
									</div>
								</div>
		
							</div>
							
							</div>
						</td>
					</tr>

				</table>
			</td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp"%>
		</tr>
	</table>

</body>
</html>