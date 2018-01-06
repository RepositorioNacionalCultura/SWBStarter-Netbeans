<%-- 
    Document   : zoomdetail.jsp
    Created on : 13/12/2017, 10:28:47 AM
    Author     : sergio.tellez
--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="mx.gob.cultura.portal.response.DigitalObject"%>
<%@page import="mx.gob.cultura.portal.response.Entry"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest, java.util.List"%>
<%
	int index = 0;
	List<DigitalObject> digitalobjects = null;
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    Entry entry = null != request.getAttribute("entry") ? (Entry)request.getAttribute("entry") : new Entry();
	entry.setDescription("El London Eye ('Ojo de Londres'), también conocido como Millennium Wheel ('Noria del milenio'), es una noria-mirador de 135 m situada sobre el extremo occidental de los Jubilee Gardens, en el South Bank del Támesis, distrito londinense de Lambeth, entre los puentes de Westminster y Hungerford. La noria está junto al County Hall y frente a las oficinas del Ministerio de Defensa.");
	
%>
<link rel='stylesheet' type='text/css' media='screen' href='/work/models/cultura/css/style.css'/>
<script src="/work/models/cultura/js/openseadragon.min.js"></script>
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
				<div class="carousel-inner">
					<div id="pyramid" class="openseadragon front-page">
					</div>
				</div>
			</div>
		</div>
	</div>
</main>
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
<script type="text/javascript">
	OpenSeadragon({
		id:            "pyramid",
        prefixUrl:     "/work/models/cultura/open/",
        showNavigator: false,
		tileSources: {
			type: 'legacy-image-pyramid',
			levels:[{
				url: '/work/models/cultura/open/london-landscape-small.jpg',
				height: 511,
				width:  1024
			},{
				url: '/work/models/cultura/open/london-landscape-mid.jpg',
				height: 1023,
				width:  2048
			},{
				url: '/work/models/cultura/open/london-landscape-large.jpg',
				height: 2976,
				width:  3968
			}]
		}
	});
</script>