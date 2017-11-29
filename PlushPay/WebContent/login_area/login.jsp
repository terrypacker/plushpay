<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j"%>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<title>Login to Pay Yourself</title>
<style type="text/css">
html,body
	{ margin: 15px; margin-top: 5px;
	  background-color: white; 
		color: #000000; 
		font-family: Tahoma, Verdana;
		font-size: 11px; 
		min-width: 760px;
		height: 100%;
	}
		
a, a:link, a:visited, a:active
	{ color: #0000aa; text-decoration: none; }
a:hover
	{ color: #ff0000; }
	
/*Set width of any input fields to 100px */
input {
	width: 100px;
}

h1{ 
	background-color: #bf8b1a; 
	color: #FFFFFF; 
	margin: 0; 
	padding: 3px 3px 3px 10px;
	font-size: 13px; 
}



h2{ 
	border-bottom: 1px solid #d0d0d0; 
	border-top: 1px solid #d0d0d0; 
	border-left:1px solid #d0d0d0; 
	border-right:1px solid #d0d0d0; 
	background-color: #bf8b1a; 
	color: #000000; 
	margin: 0 0 0 0; 
	padding: 3px; 
	font-size: 12px; 
}
	


h3 {
	background-color: #bf8b1a; 
	color: #000000; 
	margin: 0 0 5px 0; 
	padding: 3px; 
	font-size: 11px; 
}

h1 a, h1 a:link, h1 a:visited,
h2 a, h2 a:link, h2 a:visited,
h3 a, h3 a:link, h3 a:visited
	{ color: black; }
hr
	{ color: #bf8b1a; height : 1px; }

table{
		width: 10%;
		background-color: #bf8b1a;
		border-style: solid;
		border-color: #1a73bf;
		border-width: 2px 2px 2px 2px;
}

	 	
</style>
</head>
<body>

<div style="margin-left: 255px; margin-top: 150px;">

<form method="POST" action="../j_security_check">

<table width="400">
 	<tr>
 	<td colspan="3"><img src="/PayYourself/graphics/payYourself.gif"></img></td>
 	</tr>
  <tr>
    <td colspan="3"><h2>Login To Pay Yourself </h2></td>
    </tr>
  <tr>
    <td>UserName:</td>
    <td><input type="text" name="j_username" tabindex="1"></td>
    <td rowspan="2"><input name="submit" type="submit" value="Login" tabindex="3"></td>
  </tr>
  <tr>
    <td>Password:</td>
    <td><input type="password" name="j_password" tabindex="2"></td>
    </tr>
  <tr>
    <td colspan="3"><div align="center">
    </div></td>
    </tr>
</table>
</form>
</div>

</body>
</html>
