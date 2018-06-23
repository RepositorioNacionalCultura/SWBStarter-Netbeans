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
%>

<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
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
        $(".audio-bit").toggleClass("playbit");
    });
});
</script>

<section id="detalle">
	<div id="idetail" class="detallelist">

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
		
			<div class="" id="amplitude-player">
				<div class="row audiorow">
					<div class="col-12 col-sm-8" id="amplitude-left">
						
						<div id="meta-container"> <!-- FICHA -->
                            <div class="row">
                                <div class="bit1 col-3 col-md-4">
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit-100"></div>
                                </div>
                                <div class="bit2 col-9 col-md-8">
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit"></div>
                                    <div class="audio-bit-100"></div>
                                </div>
                                <div class="bit3 col-3 col-md-4">
                                    <div class="audio-bit2"></div>
                                    <div class="audio-bit-100"></div>
                                </div>
                                <div class="bit4 col-9 col-md-8">
                                    <span amplitude-song-info="name" amplitude-main-song-info="true" class="song-name oswM"></span>
								    <div class="song-artist-album">
								        <span amplitude-song-info="album" amplitude-main-song-info="true" class="oswM"></span>
									    <span amplitude-song-info="artist" amplitude-main-song-info="true"></span>
								    </div>
                                </div>
                            </div>
								
				        </div>
						
						<div id="player-left-bottom" class="barra-controles row"> <!-- BARRA BOTTOM -->
						    
						    <div class="col-12 col-md-8">
						    
						        <div id="control-container" class="col-12">
								
								<div id="central-control-container"> 
									<div id="central-controls">
										<div class="amplitude-prev" id="previous">
										    <span class="ion-ios-skipbackward"></span>
										</div>
										<div class="amplitude-play-pause" amplitude-main-play-pause="true" id="play-pause">
										    <span class="ion-ios-play"></span>
										    <span class="ion-ios-pause"></span>
										</div>
										<div class="amplitude-next" id="next">
										    <span class="ion-ios-skipforward"></span>
										</div>
									</div>
								</div> <!-- pre PLAY next -->

								
								
							</div> <!-- CONTROLES -->
						    
						    
							    <div id="time-container" class="col-12"> 
								<span class="current-time"> 
									<span class="amplitude-current-minutes oswM" amplitude-main-current-minutes="true"></span><b>:</b><span class="amplitude-current-seconds oswM" amplitude-main-current-seconds="true"></span>
								</span> <!-- 00 : 00-->
								<input type="range" class="amplitude-song-slider" amplitude-main-song-slider="true" step=".1"/> <!-- linea --->
								<span class="duration"> 
									<span class="amplitude-duration-minutes oswM" amplitude-main-duration-minutes="true"></span><b>:</b><span class="amplitude-duration-seconds oswM" amplitude-main-duration-seconds="true"></span>
								</span> <!-- 33 : 33-->
							</div> <!-- TIEMPO -->

							
							</div>
							
							<div id="volume-container" class="col-4">
								    <div class="volume-controls row">
								        <div class="amplitude-mute col-3 col-sm-3 col-md-2 amplitude-not-muted">
                                            <span class="ion-ios-volume-high"></span>
                                            <span class="ion-ios-volume-low"></span>
								        </div>
								        <div class="col-8 col-sm-9 col-md-10">
								            <input type="range" class="amplitude-volume-slider" />
								        </div>
								        
								    </div>
								</div> <!-- volumen btn -->
							
							<!--
							<div id="list-container" class="col-1">
								    <span class="ion-android-list"></span>
							</div>
							-->
							
						</div>
						
					</div>
					<div class="col-12 col-sm-4" id="amplitude-right">
						<div class="song amplitude-song-container amplitude-play-pause" amplitude-song-index="0">
						    <span class="song-number">1.</span>
							<div class="song-meta-data">
								<span class="song-title">Concerto D Major</span>
								<span class="song-artist">Haydn</span>
							</div>
							<span class="song-duration">3:30</span>
						</div>
						<div class="song amplitude-song-container amplitude-play-pause" amplitude-song-index="1">
						    <span class="song-number">2.</span>
							<div class="song-meta-data">
								<span class="song-title">Rococo Variations</span>
								<span class="song-artist">Tchaikovski</span>
							</div>
							<span class="song-duration">3:16</span>
						</div>
						<div class="song amplitude-song-container amplitude-play-pause" amplitude-song-index="2">
							<span class="song-number">3.</span>
							<div class="song-meta-data">
								<span class="song-title">Adagi</span>
								<span class="song-artist">Vivaldi</span>
							</div>
							<span class="song-duration">3:32</span>
						</div>
						<div class="song amplitude-song-container amplitude-play-pause" amplitude-song-index="3">
							<span class="song-number">4.</span>
                            <div class="song-meta-data">
								<span class="song-title">Nocturne</span>
								<span class="song-artist">Tchaikovsky</span>
							</div>
							<span class="song-duration">3:30</span>
						</div>
						<div class="song amplitude-song-container amplitude-play-pause" amplitude-song-index="4">
							<span class="song-number">5.</span>
                            <div class="song-meta-data">
								<span class="song-title">Adagio</span>
								<span class="song-artist">Haydn</span>
							</div>
							<span class="song-duration">5:12</span>
						</div>
						
					</div>
					
				</div>
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
				<h3 class="oswM">La niña</h3>
				
                <hr>
                <p class="vermas"><a href="#">Ver más <span class="ion-plus-circled"></span></a></p>
                <table>
					<tr>
						<th colspan="2">Ficha Técnica</th>
                    </tr>
                    <tr>
						<td>Artista</td>
						<td>Germán Gedovius</td>
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
<script type="text/javascript">
	Amplitude.init({
		"songs": [
			{
				"name": "Concerto D Major",
				"artist": "Haydn",
				"album": "Clasics",
				"url": "http://www.hochmuth.com/mp3/Haydn_Cello_Concerto_D-1.mp3",
				"cover_art_url": "/work/models/cultura/audio/img/waves.png"
			},
			{
				"name": "Rococo Variations",
				"artist": "Tchaikovski",
				"album": "Clasics",
				"url": "http://www.hochmuth.com/mp3/Tchaikovsky_Rococo_Var_orch.mp3",
				"cover_art_url": "/work/models/cultura/audio/img/waves.png"
			},
			{
				"name": "Adagio",
				"artist": "Vivaldi",
				"album": "Sonata",
				"url": "http://www.hochmuth.com/mp3/Vivaldi_Sonata_eminor_.mp3",
				"cover_art_url": "/work/models/cultura/audio/img/waves.png"
			},
			{
				"name": "Nocturne",
				"artist": "Tchaikovsky",
				"album": "Clasics",
				"url": "http://www.hochmuth.com/mp3/Tchaikovsky_Nocturne__orch.mp3",
				"cover_art_url": "/work/models/cultura/audio/img/waves.png"
			},
			{
				"name": "Adagio",
				"artist": "Haydn",
				"album": "Clasics",
				"url": "http://www.hochmuth.com/mp3/Haydn_Adagio.mp3",
				"cover_art_url": "/work/models/cultura/audio/img/waves.png"
			},
			{
				"name": "Concerto In D",
				"artist": "Boccherini",
				"album": "Key",
				"url": "http://www.hochmuth.com/mp3/Boccherini_Concerto_478-1.mp3",
				"cover_art_url": "/work/models/cultura/audio/img/waves.png"
			},
			{
				"name": "Prayer",
				"artist": "Bloch",
				"album": "Guidance",
				"url": "http://www.hochmuth.com/mp3/Bloch_Prayer.mp3",
				"cover_art_url": "/work/models/cultura/audio/img/waves.png"
			},
			{
				"name": "Variation",
				"artist": "Beethoven",
				"album": "Clasics",
				"url": "http://www.hochmuth.com/mp3/Beethoven_12_Variation.mp3",
				"cover_art_url": "/work/models/cultura/audio/img/waves.png"
			}
		]
	});
</script>