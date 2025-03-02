<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="f"  uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h"  uri="http://java.sun.com/jsf/html"%>
<html>
<head>
<!-- link href="./css/body.css" rel="stylesheet" type="text/css"  -->

<title>Invalid Login</title>
</head>
<body>

<div style="margin-left: 255px; margin-top: 150px;">

<table width="400" border="0">
  <tr>
    <td colspan="3"><h1>Unable to Login</h1></td>
  </tr>
  <tr>
    <td></td>
    <td></td>
    <td> Wrong Username or Password</td>
  </tr>
  <tr>
    <td></td>
    <td><a href="<%request.getHeader("host");%>/PayYourself/">Login</a></td>
    <td></td>
    </tr>
  <tr>
    <td colspan="3"><div align="center">
    </div></td>
    </tr>
</table>

</div>
</body>
</html>