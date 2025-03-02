<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html 
      xmlns:ui="http://java.sun.com/jsf/facelets"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core"
      xmlns:a4j="http://richfaces.org/a4j"
      xmlns:rich="http://richfaces.org/rich">
	<head>
	<title>Trade Confirmation</title>
	</head>
	<body>
		<f:view>
			<ui:composition xmlns="http://www.w3.org/1999/xhtml"
		 	  xmlns:ui="http://java.sun.com/jsf/facelets">
			   	<ui:define name="header">
			    	<ui:include src="/trading_area/templates/header.xhtml"/>
			  	</ui:define>
			  	<ui:define name="body">
			  		<ui:include src="/trading_area/templates/trade_history.xhtml"/>
				</ui:define>
		 	</ui:composition>
		</f:view>
	</body>
</html>