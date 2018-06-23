<%-- 
    Document   : zoomdetail.jsp
    Created on : 13/12/2017, 10:28:47 AM
    Author     : sergio.tellez
--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="org.semanticwb.portal.api.SWBResourceURL"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest"%>
<%@page import="java.util.List, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.DigitalObject, mx.gob.cultura.portal.response.Rights"%>
<%
	int index = 0;
	List<DigitalObject> digitalobjects = null;
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    Entry entry = null != request.getAttribute("entry") ? (Entry)request.getAttribute("entry") : new Entry();
	entry.setDescription("El London Eye ('Ojo de Londres'), también conocido como Millennium Wheel ('Noria del milenio'), es una noria-mirador de 135 m situada sobre el extremo occidental de los Jubilee Gardens, en el South Bank del Támesis, distrito londinense de Lambeth, entre los puentes de Westminster y Hungerford. La noria está junto al County Hall y frente a las oficinas del Ministerio de Defensa.");
	
%>
<!-- Plyr core script -->
<script src="https://cdn.plyr.io/2.0.18/plyr.js"></script>
<script>plyr.setup();</script>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>

<script type="text/javascript" src="/work/models/repositorio/js/viewer-video.js"></script>
<link rel='stylesheet' type='text/css' media='screen' href='/work/models/repositorio/css/viewer-video.css'/>

<script>
	function add(id) {
		dojo.xhrPost({
            url: '/swb/cultura/favorito?id='+id,
            load: function(data) {
                dojo.byId('addCollection').innerHTML=data;
				$('#addCollection').modal('show');
            }
        });
	}
	function addPop(id) {
		var leftPosition = (screen.width) ? (screen.width-990)/3 : 0;
		var topPosition = (screen.height) ? (screen.height-150)/3 : 0;
		var url = '/swb/cultura/favorito?id='+id;
		popCln = window.open(
		url,'popCln','height=220,width=990,left='+leftPosition+',top='+topPosition+',resizable=no,scrollbars=no,toolbar=no,menubar=no,location=no,directories=no,status=no')
	}
	function loadDoc(id) {
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			jQuery("#addCollection-tree").html(this.responseText);
			$("#addCollection" ).dialog( "open" );
		}else if (this.readyState == 4 && this.status == 403) {
			jQuery("#dialog-message-tree").text("Regístrate o inicia sesión para crear tus colecciones.");
			$("#dialog-message-tree" ).dialog( "open" );
		}
	  };
	  xhttp.open("GET", "/swb/cultura/favorito?id="+id, true);
	  xhttp.send();
	}
	function loadImg(iEntry, iDigit) {
		dojo.xhrPost({
			url: '/es/cultura/detalle/_rid/64/_mto/3/_mod/DIGITAL?id='+iEntry+'&n='+iDigit,
            load: function(data) {
                dojo.byId('idetail').innerHTML=data;
            }
        });
	}
</script>

<script>
$(document).ready(function(){
    $("#play-pause").click(function(){
        $(".obranombre").toggleClass("opaco");
    });
});
</script>
  

<section id="detalle">
	<div id="idetail" class="detalleimg">

		 <div class="obranombre">
             <h3 class="oswB">Título de Video</h3>
             <p class="oswL">Nombre del Autor</p>
         </div>
		 <div class="explora">
			<div class="explora2">
				<div class="explo1">
					© Derechos Reservados
                </div>
				<div class="explo2 row">
					<div class="col-3">
						<span class="ion-social-facebook"></span>
                    </div>
                    <div class="col-3">
                        <span class="ion-social-twitter"></span>
                    </div>
                    <div class="col-6">
                        <a href="#" onclick="loadDoc('yR3UAWIBUTE03-mlNHOA');"><span class="ion-heart"></span></a> 3,995
                    </div>
                </div>
				<div class="explo3 row">
					<div class="col-6">
						<span class="ion-chevron-left"></span> Objeto anterior
					</div>
                    <div class="col-6">
						Siguiente objeto <span class="ion-chevron-right"></span>
                    </div>
                </div>
			</div>
		 </div>
        
		 <video id="video" width="97%" poster="/work/models/repositorio/img/video.jpg" controls controlsList="nodownload">
			<source src="https://cdn.plyr.io/static/demo/View_From_A_Blue_Moon_Trailer-HD.mp4" type="video/mp4">
			<p>Tu navegador no soporta video en HTML5</p>
		</video>
		<div id="video-controls">
			<button type="button" id="play-pause" class="play"><span class="ion-ios-play"></span></button>
			<input type="range" id="seek-bar" value="0">
			<button type="button" id="mute"><span class="ion-ios-volume-high"></span></button>
			<input type="range" id="volume-bar" min="0" max="1" step="0.1" value="1">
			<button type="button" id="full-screen"><span class="ion-android-expand"></span></button>
		</div>
		
	</div>
</section>
<section id="detalleinfo">
	<div class="container">
		<div class="row">              
			<div class="col-12 col-sm-6  col-md-3 col-lg-3 order-md-1 order-sm-2 order-2 mascoleccion">
				<div>
					<p class="tit2">Más de la colección</p>
					<div>
						<img src="/work/models/cultura/img/agregado-01.jpg" class="img-responsive">
						<p>Nombre de la obra</p>
						<p>Autor Lorem Ipsum</p>
					</div>
					<div>
						<img src="/work/models/cultura/img/agregado-02.jpg" class="img-responsive">
						<p>Nombre de la obra</p>
						<p>Autor Lorem Ipsum</p>
					</div>
					<hr>
					<p class="vermas"><a href="#">Ver más <span class="ion-plus-circled"></span></a></p>
				</div>
            </div>
            <div class="col-12 col-sm-12 col-md-6 col-lg-6 order-md-2 order-sm-1 order-1 ficha ">
				<h3 class="oswM">La chica de la Ventana</h3>
				
                <hr>
                <p class="vermas"><a href="#">Ver más <span class="ion-plus-circled"></span></a></p>
                <table>
					<tr>
						<th colspan="2">Ficha Técnica</th>
                    </tr>
                    <tr>
						<td>Artista</td>
						<td>Abraham Ángel</td>
                    </tr>
                    <tr>
						<td>Fecha</td>
                        <td></td>
                    </tr>
                    <tr>
						<td>Tipo de objeto</td>
                        <td>Óleo sobre cartón</td>
                    </tr>
                    <tr>
						<td>Identificador</td>
                        <td>10243</td>
                    </tr>
                    <tr>
						<td>Institución</td>
                        <td>Lorem ipsum</td>
                    </tr>
                    <tr>
						<td>Técnica</td>
                        <td>Lorem ipsum</td>
                    </tr>
                </table>
                <p class="vermas"><a href="#">Ves más <span class="ion-plus-circled"></span></a></p>
            </div>
            <div class="col-12 col-sm-6  col-md-3 col-lg-3 order-md-3 order-sm-3 order-3 clave">
				<div class="redes">
					<span class="ion-social-facebook"></span>
                    <span class="ion-social-twitter"></span>
                </div>
                <div>
					<p class="tit2">Palabras clave</p>
                    <p><a href="#">lorem</a> / <a href="#">ipsum</a> / <a href="#">dolor</a> / <a href="#">sit</a> / <a href="#">amet</a> / <a href="#">consectetuer</a> / <a href="#">adioiscing</a> / <a href="#">elit</a></p>
                </div>
            </div>
        </div>
    </div>
</section>  
    
