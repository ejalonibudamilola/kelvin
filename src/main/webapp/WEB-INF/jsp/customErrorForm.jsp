
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>


<html lang="en">
<head>
<title>Exception | Uncaught Exception Page</title>
<style type="text/css">

body {
	color: #000;
	font-size: 13px;
	line-height: 21px;
	font-family: 'proxima-nova', Helvetica, sans-serif;
	font-weight: 300;
}


.container {
	margin-left: auto;
	margin-right: auto;
	width: 960px;
}
.container:before,
.container:after {
	content: '.';
	display: block;
	overflow: hidden;
	visibility: hidden;
	font-size: 0;
	line-height: 0;
	width: 0;
	height: 0;
}
.container:after {
	clear: both;
}


header  {
	width:520px;
	height:346px;
	margin-right:auto;
	margin-left:auto;
	margin-top:30px;
	
}


#header_1{
	height:70px;
	color:#FFF;
	width: 100%;
	padding:5px;
	text-align: center;
}




/* !MAIN */
#main {
	background-color: #fff;
	background-image: url(images/OSXedit.png);
	background-repeat: no-repeat;
	height: 630px;
	width: 755px;
	margin-right: auto;
	margin-left: auto;
}

#form{
	width:480px;
	margin-left:auto;
	margin-right:auto;
	height: 180px;
}
#frm2{
	width:480px;
	margin-left:auto;
	margin-right:auto;
	height: 180px;
}
/* the error **/

.error {
	border: 1px solid;
	background-repeat: no-repeat;
	background-position: 10px center;
	width:425px;
	margin-top: 5px;
	margin-right: auto;
	margin-bottom: 5px;
	margin-left: auto;
	padding-top: 10px;
	padding-right: 10px;
	padding-bottom: 10px;
	padding-left: 40px;
	font-size: 12px;
	text-align: center;
}

.error {
    color: #D8000C;
    background-color: #FFBABA;
    background-image: url('images/e_error.png');
}


.btn {
	display: inline-block;
	padding: 6px 12px;
	font-size: 13px;
	font-weight: normal;
	line-height: 21px;
	text-align: center;
	white-space: nowrap;
	vertical-align: middle;
	cursor: pointer;
	-webkit-user-select: none;
	user-select: none;
	background-image: none;
	border: 1px solid transparent;
	border-radius: 4px;
}

.btn-primary {
	color: #fff;
	background-color: #428bca;
	border-color: #357ebd;
}

#buttons{
	text-align:center;
	margin-left:auto;
	margin-right:auto;
	width:200px;
	margin-top: 12px;
}

textarea{
	resize: none;
}
</style>
</head>
<body>
	<form:form modelAttribute="elbean">
  <article id="main" class="container" role="main">
  
    <header>
     	<div id="header_1"><img src="images/Ogun.png" align="center"/></div>
        <div id="error" class="error">Oops!!! This is embarrassing...<br/>
		Please Contact your GNL Systems Support Team. <br/>
		The errant code will be logged on exiting this page.</div>
        
        
    </header>

		<div id="buttons">

        <input type="image" name="submit" value="ok" title="Save Error Log" class="" src="images/ok_h.png">
		
							
       </div>

 <section>

    </section>
    
  </article>

  </form:form>
</body>
</html>