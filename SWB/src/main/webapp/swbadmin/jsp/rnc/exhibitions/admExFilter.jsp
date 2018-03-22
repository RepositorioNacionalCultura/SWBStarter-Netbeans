<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.ArtWork"%>
<%@page import="mx.gob.cultura.portal.response.DigitalObject"%>
<%@page import="mx.gob.cultura.portal.response.Title,org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, org.semanticwb.portal.api.SWBResourceModes, org.semanticwb.model.Resource, java.util.List"%>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<%
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    SWBResourceURL pageURL = paramRequest.getRenderUrl();
    pageURL.setCallMethod(SWBParamRequest.Call_DIRECT);
	pageURL.setMode(SWBResourceModes.Mode_ADMIN);
    pageURL.setAction("PAGE");

    SWBResourceURL saveURL = paramRequest.getRenderUrl();
    saveURL.setMode(SWBResourceModes.Mode_ADMIN);
	saveURL.setAction("SAVE");
    String word = (String)request.getAttribute("criteria");
    List<ArtWork> references = (List<ArtWork>)request.getAttribute("PAGE_LIST");

    WebSite site = paramRequest.getWebPage().getWebSite();
	Resource base = (Resource)request.getAttribute("base");
%>
<!-- Required meta tags -->
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<!-- Bootstrap CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/css/bootstrap.min.css" integrity="sha384-PsH8R72JQ3SOdhVi3uxftmaW6Vc51MKb0q5P2rRUpPvrszuE4W1povHYgTpBfshb" crossorigin="anonymous">
<link rel="stylesheet" href=" http://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css">
<link href="https://fonts.googleapis.com/css?family=Work+Sans:400,500,600,700" rel="stylesheet">
<link href="https://fonts.googleapis.com/css?family=Oswald:200,500,600" rel="stylesheet">
<script src="https://use.fontawesome.com/efa62eded2.js"></script>
<link rel="stylesheet" href="/work/models/<%=site.getId()%>/css/owl.carousel.css">
<link rel="stylesheet" href="/work/models/<%=site.getId()%>/css/owl.theme.green.css">
<link rel="stylesheet" href="/work/models/<%=site.getId()%>/css/cultura.css">
<link rel="stylesheet" href="/work/models/<%=site.getId()%>/css/offcanvas.css">
<script type="text/javascript">
	function getEl(el) {
		var i;
		var url = '';
		for (i = 0; i < el.length; i++) {
			if (el[i].checked) {
				url = url + '&favarts=' + el[i].value;
			}
		}
		return url;
    }
    function doPage(p, m, f) {
		var fs = getEl(document.getElementsByName("favarts"));
        dojo.xhrPost({
            url: '<%=pageURL%>?p='+p+'&m='+m+'&sort='+f+'&criteria=<%=word%>'+fs,
            load: function(data) {
                dojo.byId('references').innerHTML=data;
				location.href = '#showPage';
            }
        });
    }
</script>
<div class="ruta oswL">
	<a name="showPage"></a>
    <a class="rojo" href="javascript:history.go(-1)"><i aria-hidden="true" class="fa fa-long-arrow-left"></i> Regresar</a>
	<h2 class="oswM rojo titulo">Seleccione los elementos de la exhibición: <%= word%></h2>
</div>
<main role="main" class="container-fluid resultados">
	<form id="admExForm" action="<%=saveURL.toString() %>" method="post">
		<div class="row offcanvascont">
			<% if (!references.isEmpty()) {  %>
				<div class="offcanvas rojo-bg">
					<span id="offcanvasAbre" onclick="openNav()"> Filtros <i aria-hidden="true" class="ion-chevron-right "></i> </span> <span id="offcanvasCierra" onclick="closeNav()"> Filtros <i aria-hidden="true" class="ion-close "></i> </span>
				</div>
				<jsp:include page="admExFilters.jsp" flush="true"/>
			<% } %>
			<div id="contenido">
				<% if (!references.isEmpty()) {  %>
				<div id="references">
					<div id="recientes" class="row lista">
						<%
							for (ArtWork art : references) {
								String creator = "";
								Title title = new Title();
								Entry reference = art.getEntry();
								DigitalObject digital = new DigitalObject();
								List<Title> titles = reference.getRecordtitle();
								List<String> creators = reference.getCreator();
								List<DigitalObject> digitalobject = reference.getDigitalobject();
								if (!digitalobject.isEmpty()) digital = digitalobject.get(0);
								if (!titles.isEmpty()) title = titles.get(0);
								if (!creators.isEmpty()) creator = creators.get(0);
						%>
						<div class="pieza">
							<div>
								<a href="/swb/<%=site.getId()%>/detalle?id=<%=reference.getId()%>" target="blank">
									<img src="<%=digital.getUrl()%>" />
								</a>
							</div>
							<p class="oswB azul tit"><a href="#"><%=title.getValue()%></a></p>
							<p class="azul autor"><a href="#"><%=creator%></a></p>
							<p class="azul autor"><input type="checkbox" name="hiddenarts" value="<%=reference.getId()%>" <% if (art.isHidden()) out.print("checked"); %>>Ocultar
											<br/><input type="checkbox" name="favarts" value="<%=reference.getId()%>" <% if (art.isFavorite()) out.print("checked"); %>>Favorito</p>
						</div>
						<%
							}
						%>
					</div>
					<jsp:include page="admExPager.jsp" flush="true"/>
				</div>
				<%
					}else if (null != word) { out.println("No se encontraron resultados para la búsqueda " +word); }
					else { out.println("Debe proporcionar un criterio de búsqueda"); }
				%>
			</div>
		</div>
	</form>
</main>