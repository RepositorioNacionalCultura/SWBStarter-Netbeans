<%--
    Document   : artdetail.jsp
    Created on : 5/12/2017, 11:48:36 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.response.DateDocument, mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, java.util.ArrayList, java.util.List"%>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<%
    int iEntry = 0;
    int iDigit = 1;
    int images = 0;
    String type = "";
    String title = "";
    String period = "";
    String creator = "";
    DigitalObject digital = null;
    List<Title> titles = new ArrayList<>();
    List<String> creators = new ArrayList<>();
    DateDocument datestart = new DateDocument();
    List<DigitalObject> digitalobjects = new ArrayList<>();
    Entry entry = (Entry)request.getAttribute("entry");
    if (null != entry) {
	iEntry = entry.getPosition();
        if (null != entry.getDigitalobject()) {
            creators = entry.getCreator();
            titles = entry.getRecordtitle();
            digitalobjects = entry.getDigitalobject();
            images = null != digitalobjects ? digitalobjects.size() : 0;
            digital = images >= iDigit ? digitalobjects.get(iDigit-1) : new DigitalObject();
            type = entry.getResourcetype().size() > 0 ? entry.getResourcetype().get(0) : "";
            datestart = entry.getPeriodcreated().getDatestart();
            creator = creators.size() > 0 ? creators.get(0) : "";
            period = null != datestart ? datestart.getValue() : "";
            if (!titles.isEmpty()) title = titles.get(0).getValue();
	}
    }
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    WebSite site = paramRequest.getWebPage().getWebSite();
    SWBResourceURL digitURL = paramRequest.getRenderUrl().setMode("DIGITAL");
    digitURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    Integer records = null != session.getAttribute("NUM_RECORDS_TOTAL") ? (Integer)session.getAttribute("NUM_RECORDS_TOTAL") : 0;
    
    //llamada a la generacion del script para compartir con Facebook: funcion fbShare()
    String scriptFB = mx.gob.cultura.portal.resources.Utilities.getScriptFBShare(request);
%>
<%=scriptFB%>
<script>
    function add(id) {
        //var leftPosition = (screen.width) ? (screen.width-990)/3 : 0;
	//var topPosition = (screen.height) ? (screen.height-150)/3 : 0;
	//var url = '/swb/<%=site.getId()%>/favorito?id='+id;
	//popCln = window.open(
	//url,'popCln','height=220,width=990,left='+leftPosition+',top='+topPosition+',resizable=no,scrollbars=no,toolbar=no,menubar=no,location=no,directories=no,status=no')
	//call dojo ajax into a div
	dojo.xhrPost({
            url: '/swb/<%=site.getId()%>/favorito?id='+id,
            load: function(data) {
                dojo.byId('addCollection').innerHTML=data;
                $('#addCollection').modal('show');
            }
        });
    }
    function addPop(id) {
        var leftPosition = (screen.width) ? (screen.width-990)/3 : 0;
        var topPosition = (screen.height) ? (screen.height-150)/3 : 0;
        var url = '/swb/<%=site.getId()%>/favorito?id='+id;
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
	xhttp.open("GET", "/swb/<%=site.getId()%>/favorito?id="+id, true);
	xhttp.send();
    }
    function loadImg(iEntry, iDigit) {
	dojo.xhrPost({
            url: '<%=digitURL%>?id='+iEntry+'&n='+iDigit,
            load: function(data) {
                dojo.byId('idetail').innerHTML=data;
            }
        });
    }
</script>
<section id="detalle">
    <div id="idetail" class="detalleimg">
	<div class="obranombre">
            <h3 class="oswB"><%=title%></h3>
            <p class="oswL"><%=creator%></p>
        </div>
	<div class="explora">
            <div class="explora2">
		<div class="explo1">
                    © Derechos Reservados
                </div>
		<div class="explo2 row">
                    <div class="col-3">
                    <!-- llamada a funcion para compartir -->
                    <a href="#" onclick="fbShare();"><span class="ion-social-facebook"></span></a>
                    </div>
                    <div class="col-3">
                        <span class="ion-social-twitter"></span>
                    </div>
                    <div class="col-6">
                        <a href="#" onclick="loadDoc('<%=entry.getId()%>');"><span class="ion-heart"></span></a> 3,995
                    </div>
                </div>
		<div class="explo3 row">
                    <div class="col-6">
                        <%
                            if (iEntry > 1) {
                        %>
                                <span class="ion-chevron-left"></span> Objeto anterior
			<%
                            }
			%>
                    </div>
                    <div class="col-6">
                        <%
                            if (iEntry < records) {
			%>
                                Siguiente objeto <span class="ion-chevron-right"></span>
			<%
                            }
			%>
                    </div>
                </div>
            </div>
	</div>
	<img src="<%=digital.getUrl()%>">
    </div>
</section>
<section id="detalleinfo">
    <div class="container">
	<div class="row">              
            <div class="col-12 col-sm-6  col-md-3 col-lg-3 order-md-1 order-sm-2 order-2 mascoleccion">
		<div>
                    <p class="tit2">Más de la colección</p>
                    <div>
			<img src="/work/models/repositorio/img/agregado-01.jpg" class="img-responsive">
			<p>Nombre de la obra</p>
			<p>Autor Lorem Ipsum</p>
                    </div>
                    <div>
			<img src="/work/models/repositorio/img/agregado-02.jpg" class="img-responsive">
			<p>Nombre de la obra</p>
			<p>Autor Lorem Ipsum</p>
                    </div>
                    <hr>
                    <p class="vermas"><a href="#">Ver más <span class="ion-plus-circled"></span></a></p>
		</div>
            </div>
            <div class="col-12 col-sm-12 col-md-6 col-lg-6 order-md-2 order-sm-1 order-1 ficha ">
		<h3 class="oswM"><%=title%></h3>
		<% if (null != entry && null != entry.getDescription() && !entry.getDescription().isEmpty()) { %>
                        <p><%=entry.getDescription()%></p>
                <% } %>
                <hr>
                <p class="vermas"><a href="#">Ver más <span class="ion-plus-circled"></span></a></p>
                <table>
                    <tr>
                        <th colspan="2">Ficha Técnica</th>
                    </tr>
                    <tr>
			<td>Artista</td>
                        <td><%=creator%></td>
                    </tr>
                    <tr>
			<td>Fecha</td>
                        <td><%=period%></td>
                    </tr>
                    <tr>
			<td>Tipo de objeto</td>
                        <td><%=type%></td>
                    </tr>
                    <tr>
			<td>Identificador</td>
                        <td><%=entry.getIdentifiers()%></td>
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
                    <!-- llamada a funcion para compartir -->
                    <a href="#" onclick="fbShare();"><span class="ion-social-facebook"></span></a>
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

<div id="dialog-message-tree" title="error">
    <p>
        <div id="dialog-text-tree"></div>
    </p>
</div>

<div id="dialog-success-tree" title="éxito">
    <p>
	<span class="ui-icon ui-icon-circle-check" style="float:left; margin:0 7px 50px 0;"></span>
	<div id="dialog-msg-tree"></div>
    </p>
</div>

<div id="addCollection" title="Agregar a colección">
    <p>
	<div id="addCollection-tree"></div>
    </p>
</div>