<rich:page 
	xmlns:ui="http://java.sun.com/jsf/facelets"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core"
      xmlns:a4j="http://richfaces.org/a4j"
      xmlns:rich="http://richfaces.org/rich"
      markupType="xhtml"
      contentType="text/html"
      theme="simple"
      width="800"
      sidebarWidth="300"
      sidebarPosition="left"
      pageTitle="Plush Pay dot Com" >

		<f:facet name="header">
			<rich:layout>
				<rich:layoutPanel position="left">
						<br></br>
						<h:outputText value="PlushPay.com"/>				
				</rich:layoutPanel>
			
			</rich:layout>
		</f:facet>
		<f:facet name = "sidebar">
			<rich:layout>
				<rich:layoutPanel position="center" width="100%" >
				<br></br>
					<h:graphicImage url="/graphics/logo.png"></h:graphicImage>					
				</rich:layoutPanel>
			</rich:layout>
		</f:facet>
		<f:facet name="footer">
			<h:outputText value="Copyright &#169; Plush Pay 2010"/>	
		</f:facet>

		<!-- Main Page code goes here -->
		<rich:tabPanel switchType="client">
			<rich:tab label="Company Info">
				<ui:include src="/home/templates/companyInfo.xhtml"/>
			</rich:tab>
			
		</rich:tabPanel>
		
</rich:page>