<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html 
      xmlns:ui="http://java.sun.com/jsf/facelets"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core"
      xmlns:a4j="http://richfaces.org/a4j"
      xmlns:rich="http://richfaces.org/rich">

<head>
	<title>Pay Yourself - Preferences</title>	
</head>
<body>

<f:view>
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
   xmlns:ui="http://java.sun.com/jsf/facelets">
   
  <ui:define name="header">
    <ui:include src="/user_area/templates/header.xhtml"/>
  </ui:define>

  <ui:define name="body">
  	<ui:include src="/user_area/templates/beneficiaries.xhtml"/>
  </ui:define>

  <ui:define name="addUsdBenificiaryModal">
  	<ui:include src="/user_area/templates/new_usd_beneficiary.xhtml"/>
  </ui:define>
 
   <ui:define name="updateUsdBenificiaryModal">
  	<ui:include src="/user_area/templates/update_usd_beneficiary.xhtml"/>
  </ui:define>
 
 
  <ui:define name="addAudBenificiaryModal">
  	<ui:include src="/user_area/templates/new_aud_beneficiary.xhtml"/>
  </ui:define>

  <ui:define name="updateAudBenificiaryModal">
  	<ui:include src="/user_area/templates/update_aud_beneficiary.xhtml"/>
  </ui:define>  
</ui:composition>
</f:view>

</body>
</html>