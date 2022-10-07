<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Passport Overview  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="stylesheet" href="<c:url value="/css/identityCard.css"/>" type="text/css" />
<script type="text/javascript" src="scripts/jacs.js"></script>
<script src="<c:url value="/scripts/jquery-1.3.2.min.js"/>"></script>
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
 <style>
 .helper{
 	padding-right:10px;
 	font-weight: normal;
 	font-size: 11px;
 }    
 </style>
 
 <script>

function Validate()
	{
	var image =document.getElementById("file").value;
	if(image!=''){
	var checkimg = image.toLowerCase();
	if (!checkimg.match(/(\.jpg|\.png|\.JPG|\.PNG|\.jpeg|\.JPEG|\.bmp|\.BMP)$/)){
	alert("Please Select an Image.\nAcceptable File must end with .jpg,.png,.jpeg,.bmp");
	document.getElementById("file").focus();
	return false;
	}
      if (document.getElementById('file').files[0].size > 1048576) {
         alert("Picture must be 1MB or less in size");
         document.getElementById("file").focus();
         return false;
       }
	}
	return true;
}

</script>

<script>
  $(document).ready(function(){
    $('.img-zoom').hover(function() {
        $(this).addClass('transition');
        $(this).addClass('hoverZoom');
 
    }, function() {
        $(this).removeClass('transition');
        $(this).removeClass('hoverZoom');
    });
  });
</script>
</head>

<body class="main">

				
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
		<td>

		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			
			<tr>
				<td colspan="2">
					<div class="title"><c:out value="${roleBean.staffTypeName}"/>: &nbsp;<c:out value="${namedEntity.name} [ ${employeeBean.employee.employeeId} ]" />
					
					<span class="reportTopPrintLinkExtended helper">
                              	<a href='${appContext}/searchEmpForEdit.do' title="Search for another ${roleBean.staffTypeName}"><i>New Search</i></a>
                              </span>   
					</div>
					
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">
					<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="employeeBean">
         							 <ul>
            							<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            							</c:forEach>
         							 </ul>
     							 </spring:hasBindErrors>
								</div>
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
                           <td>
                           &nbsp;                              
                          </td>
                                       
                        </tr>
						<tr>
							<td>
								<table class="formtable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left">
									<tr align="left">
										<c:if test="${employeeBean.hiringInfo.suspendedEmployee}">
										<td class="activeTH"><c:out value="${roleBean.staffTypeName}"/> Status : <img src="images/bulb_yellow.png" title="${roleBean.staffTypeName} is currently on suspension" border="0" /> <font color="orange">Suspended</font></td>
										</c:if>
										<c:if test="${employeeBean.employee.terminated}">
											<td class="activeTH"><c:out value="${roleBean.staffTypeName}"/> Status : <img src="images/bulb_red.png" title="${roleBean.staffTypeName} is Terminated" border="0" /><font color="red">Terminated</font></td>
										</c:if>
										<c:if test="${not employeeBean.employee.terminated and not employeeBean.hiringInfo.suspendedEmployee }">
											<td class="activeTH"><c:out value="${roleBean.staffTypeName}"/> Status : <img src="images/bulb_green.png" title="Active ${roleBean.staffTypeName}" border="0" /><font color="green">Active</font></td>
										</c:if>
									</tr>
								</table>
							</td>
						</tr>
						
					</table>
				</td>
			</tr>
		</table>
	</td>
	
	</tr>
	
	<tr>
			<td align="left" width="100%" >
				
				<div id="mergeLeft">
								<div class="comment-wrap">
										<div class="comment-nav">Passport Upload Details</div>
										<div class="comment-item">
											Format: can be jpeg, png, gif or bitmap (.bmp) image
										</div>
										
										
									</div>
									<br>
									<div class="comment-wrap">
										<div class="comment-nav">Size</div>
										<div class="comment-item">
											Maximum Size for Upload: 1MB
										</div>
									</div>
							</div>
				
				
				<div id="identityCardView">
								<div class="base">
									<div class="topDisplay">PASSPORT OVERVIEW</div>
									<div class="holdAvatar">
										<c:choose>
												<c:when test="${empty photo }">
													<img src="<c:url value="/images/avatar.png"/>" />
												</c:when>
												<c:otherwise>
												 <a href="#" onmouseover="<c:url value='/viewPassportImage'/>">
												 <img src="<c:url value='/viewPassportImage'/>" /></a>
										</c:otherwise>
										</c:choose>		
									</div>
									<div class="width180 color autoMargin">${namedEntity.name}</div>
									<div class="width180 color autoMargin">[ ${employeeBean.employee.employeeId} ]</div>
									<div><hr noshade /></div>
									<div class="width180 autoPad autoMargin">[${employeeBean.employee.salaryScale} : ${employeeBean.employee.levelAndStep}]</div>
								</div>
							</div>
							
			
			
				<div id="mergeRight">
								<c:if test="${not empty photo }">
									<div class="comment-wrap">
										<div class="comment-nav">Passport Modification Details</div>
										<div class="comment-item">
										By: <strong style="text-transform: uppercase">${wModifiedBy}</strong><br>
									Date: &nbsp;<fmt:formatDate type="date" dateStyle="long"  value="${photo.lastModTs}" />
										</div>
									</div>
									<br />
									
								</c:if>
							</div>
											
				
				
			</td>
	</tr>

	
	
	<form:form modelAttribute="informationBean" enctype="multipart/form-data" onSubmit="return Validate();" method="post">
								<input type="hidden" name="employeeId" value="${employeeBean.employee.id}" />
								<c:choose>
								
										<c:when test="${empty photo }">
												<tr class="activeTH">
													<td align="center">
														
															Upload Passport: <input type="file" name="file" id="file" />
														
														
		 													<br /><br />
															<input type="image" name="_upload" value="Upload" title="Upload ${roleBean.staffTypeName} Passport" src="images/Upload.png">
															<input type="image" name="_close" value="close" title="Close" src="images/close.png">

														
													</td>
												</tr>
										</c:when>
										
									<c:otherwise>
										<tr class="activeTH">
											<td align="center">
													Edit Passport: <input type="file" name="file" id="file" />
													
													<input type="hidden" name="intent" value="update" />
 													<br />
 													<br />
 													<input type="image" name="_upload" value="update" title="Update ${roleBean.staffTypeName} Passport" src="images/update.png">
 													<input type="image" name="_close" value="close" title="Close" src="images/close.png">

											</td>
										</tr>
								
									</c:otherwise>	
								
								</c:choose>
									</form:form>
	
	
	
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
		
	</table>
</body>
</html>
