<%--
    Document   : artdetail.jsp
    Created on : 5/12/2017, 11:48:36 AM
    Author     : sergio.tellez
--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="org.semanticwb.portal.api.SWBResourceURL"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest"%>
<%@page import="java.util.List, java.util.ArrayList, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.DigitalObject, mx.gob.cultura.portal.response.Rights,  mx.gob.cultura.portal.response.Title, mx.gob.cultura.portal.response.DateDocument"%>
<%
    int index = 0;
    String title = "";
    String period = "";
    String creator = "";
    List<Title> titles = new ArrayList<>();
    List<String> creators = new ArrayList<>();
    DateDocument datestart = new DateDocument();
    List<DigitalObject> digitalobjects = new ArrayList<>();
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    Entry entry = (Entry)request.getAttribute("entry");
    if (null != entry && null != entry.getDigitalobject()) {
	creators = entry.getCreator();
	titles = entry.getRecordtitle();
	digitalobjects = entry.getDigitalobject();
	datestart = entry.getPeriodcreated().getDatestart();
	creator = creators.size() > 0 ? creators.get(0) : "";
	period = null != datestart ? datestart.getValue() : "";
	if (!titles.isEmpty()) title = titles.get(0).getValue();
    }
%>
<main role="main" class="container-fluid detalle" id="detalle">
    <div class="row row-offcanvas row-offcanvas-right" id="detallecont">
        <div class="col-12 col-md-9" id="contenido">
            <p class="float-right d-md-none">
                <button type="button" class="btn btn-sm" data-toggle="offcanvas">
                    <span class="ion-chevron-left"> Mostrar ficha </span>
                    <span class="ion-chevron-right"> Ocultar ficha </span>
                </button>
            </p>
            <div id="carouselExampleIndicators" class="carousel slide" data-ride="carousel">
                <ol class="carousel-indicators">
					<% 
						int i = 0;
						for (DigitalObject r : digitalobjects) {
							if (r.getMediatype().getMime().equals("image/jpeg")) {
									if (i == 0) {
							%>
										<li data-target="#carouselExampleIndicators" data-slide-to="0" class="active"></li>
							<%		
									}else {
							%>
										<li data-target="#carouselExampleIndicators" data-slide-to="<% out.println(""+i); %>"></li>
							<% 
									}
									i++;
							 }
						}
					%>
                </ol>
                <div class="carousel-inner">
					<% 
						for (DigitalObject r : digitalobjects) {
							if (r.getMediatype().getMime().equals("image/jpeg")) {
										if (index == 0) {
								%>
											<div class="carousel-item active">
								<%		
										}else {
								%>	
											<div class="carousel-item">
								<%	
										} 
								%>
												<img src="<%=r.getUrl()%>" alt="siguiente"></img>
											</div>
								<% 
										index++;
							}
						}
					%>    
                </div>
				<%
					if (index > 1) {
				%>
						<a class="carousel-control-prev" href="#carouselExampleIndicators" role="button" data-slide="prev">
							<i class="fa fa-long-arrow-left" aria-hidden="true"></i>
							<span class="sr-only">anterior</span>
						</a>
						<a class="carousel-control-next" href="#carouselExampleIndicators" role="button" data-slide="next">
							<i class="fa fa-long-arrow-right" aria-hidden="true"></i>
							<span class="sr-only">siguiente</span>
						</a>
				<%
					}
				%>
            </div>
        </div>
        <div class="col-6 col-md-3 sidebar-offcanvas" id="sidebar">
            <div>
                <p>Ficha t�cnica</p>
                <hr>
                <ul>
					<% if (null != entry.getIdentifier()) { %>
						<li><strong>Tipo de objeto</strong> <%=entry.getIdentifier().get(0).getType()%></li>
						<li><strong>Autor</strong> <%=creator%></li>
						<li><strong>T�tulo</strong> <%=titles.get(0).getValue()%></li>
						<li><strong>Fecha de creaci�n</strong> <%=entry.getDatecreated().getValue()%></li>
						<li><strong>Identificador</strong> <%=entry.getIdentifiers()%></li>
					<% 
						}else {
							out.println("<li><strong>No se encontr� informaci�n del objeto</strong></li>");
						}
					%>
                </ul>
            </div>
            <div class="vermas">
                <a href="#">Ver m�s <span class="ion-android-add-circle"></span></a>
            </div>
        </div>
    </div>
    <div id="detallesube" class="row detalle-acciones ">
        <div class="col-xs-12 col-sm-7 col-md-6 col-lg-6 col-xl-6 offset-sm-0 offset-md-0 offset-lg-0 offset-xl-1">
			<a href="#detallesube" id="subir"><i class="fa fa-long-arrow-down rojo-bg" aria-hidden="true"></i></a>
            <p class="oswB"><%=title%></p>
            <p><%=period%></p>
        </div>
        <div class="col-xs-12 col-sm-2 col-md-3 col-lg-3 col-xl-2">
            <a href="#"><i class="fa fa-search-plus" aria-hidden="true"></i></a>
            <a href="#"><i class="fa fa-heart" aria-hidden="true"></i></a>
            <a href="#"><i class="fa fa-share-alt" aria-hidden="true"></i></a>
            <a href="#"><i class="fa fa-download" aria-hidden="true"></i></a>
        </div>
        <div class="col-xs-12 col-sm-2 col-md-3 col-lg-3 col-xl-3 rojo">
            <p>�Puedo usarlo?</p>
        </div>
    </div>
</main>
<% if (null != entry && null != entry.getDescription() && !entry.getDescription().isEmpty()) { %>
	<section id="detalleinfo">
		<a></a>
		<div class="cointainer-fluid">
			<div class="row detalleinfo azul-bg">
				<p class="col-md-10 offset-md-1">
					<%=entry.getDescription()%>
				</p>
			</div>
		</div>
	</section>
<% } %>