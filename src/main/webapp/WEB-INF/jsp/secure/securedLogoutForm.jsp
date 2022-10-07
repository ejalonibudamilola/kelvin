<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Sign Off</title>
<style type="text/css">

body{
	font-family: Calibri, Candara, Segoe, "Segoe UI" , Optima, Arial, sans-serif;
	font-size:13px;
	background-image: url(images/rpt.png);
	background-repeat: repeat-x;
}
.wrapper{
	width:550px;
	height:280px;
	border: 2px solid #019340;
	margin-top: 155px;
	margin-right: auto;
	margin-bottom: auto;
	margin-left: auto;
	background-image: url(images/lgs.png);
	background-position: 15px;
	background-repeat: no-repeat;
	padding-top:10px;
	padding-right: 5px;
	padding-bottom: 5px;
	padding-left: 10px;
	background-color: #FFF;
}

.thankyou{
	height:70px;
	color:#FFF;
	float: right;
	width: 100%;
}

.type1{
	font-size:40px;
	font-weight:bold;
}

.textual{
	float: right;
	text-align:right;
	width:100%;
	height:80px;
}

a.orangeButton {
padding: 5px 10px;
border-radius: 10px;
text-align: center;
font-size: 12px;
line-height: 21px;
height: 25px;
margin-right:7px;
float:right;
}

a.orangeButton {
background: url(images/footbg.png) repeat-x bottom left;
color: #FFFFFF;
border: none;
}

.relogin{
	margin-top:10px;
	width:100%;
	float:right;
	height:105px;
}

.copyright{
	height:35px;
	width:100%;
	color:#000;
	text-align:center;
}
</style>
</head>

<body>

<div class="wrapper">
    <div class="thankyou">
    	<img src="images/coatOfArms.png" align="right"/>
    </div>
    
    <div class="textual">
    	<div>
        	<div class="type1">Thank You</div>
            <div>for using IPPMS.</div>
      </div>
  </div>
  
  
    
  <div class="relogin">
  	<a  title="Click here to re-login to your IPPMS" class="orangeButton leftmargin" href='<c:url value="/securedLoginForm"/>'>Click here to re-Login to IPPMS</a>
  </div>
  
  
  
  
  	<div class="copyright">
 <gnl:copyright startYear="2020" />
 	</div>
  
</div>
</body>
</html>
