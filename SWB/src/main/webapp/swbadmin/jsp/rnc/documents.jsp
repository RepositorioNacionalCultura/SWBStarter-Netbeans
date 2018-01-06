<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.response.DigitalObject"%>
<%@page import="mx.gob.cultura.portal.response.Entry"%>
<%@page import="mx.gob.cultura.portal.response.Title,org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, java.util.List"%>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js"
        djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<%
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    SWBResourceURL pageURL = paramRequest.getRenderUrl().setMode("PAGE");
    pageURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    SWBResourceURL pagesURL = paramRequest.getRenderUrl().setMode("PAGES");
    pagesURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    List<Entry> references = (List<Entry>)session.getAttribute("PAGE_LIST");
    String word = (String)request.getAttribute("word");
    WebSite site = paramRequest.getWebPage().getWebSite();
%>
<script type="text/javascript">
    function setList() { doPage(1, 'l'); }
    function setGrid() { doPage(1, 'g'); }
    function doPage(p, m) {
        dojo.xhrPost({
            url: '<%=pageURL%>?p='+p+'&m='+m,
            load: function(data) {
                dojo.byId('references').innerHTML=data;
            }
        });
    }
</script>

<h2 class="oswM rojo titulo">Resultados de la búsqueda</h2>
<div class="row offcanvascont">
    <% if (!references.isEmpty()) {  %>
        <div class="offcanvas rojo-bg">
            <span id="offcanvasAbre" onclick="openNav()"> Filtros <i aria-hidden="true" class="ion-chevron-right "></i> </span> <span id="offcanvasCierra" onclick="closeNav()"> Filtros <i aria-hidden="true" class="ion-close "></i> </span>
        </div>
        <jsp:include page="filters.jsp" flush="true"/>
    <% } %>
    <div id="contenido">
        <div class="ruta oswL">
            <a class="rojo" href="javascript:history.go(-1)"><i aria-hidden="true" class="fa fa-long-arrow-left"></i> Regresar</a> | <a href="/swb/<%=site.getId()%>/home">Inicio</a> / Resultados de la b&uacute;squeda
        </div>
        <% if (!references.isEmpty()) {  %>
        <div class="ordenar">
            <div class="row">
                <div class="col-lg-6">&nbsp;</div>
                <div class="col-lg-3 oswL dropdown show">
                    <a class="dropdown-toggle" href="#" role="button" id="dropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        Ordenar por:
                    </a>
                    <div class="dropdown-menu" aria-labelledby="dropdownMenuLink">
                        <a class="dropdown-item" href="#" onclick="doSort(<% out.print("'"+word+"','datedes'"); %>)">Fecha</a>
                        <a class="dropdown-item" href="#" onclick="doSort(<% out.print("'"+word+"','relvdes'"); %>)">Relevancia</a>
                        <a class="dropdown-item" href="#" onclick="doSort(<% out.print("'"+word+"','statdes'"); %>)">Popularidad</a>
                    </div>
                </div>
                <div class="col-lg-3">
                    <a href="#" onclick="setGrid();"><i class="fa fa-th select" aria-hidden="true"></i></a>
                    <a href="#" onclick="setList();"><i class="fa fa-th-list" aria-hidden="true"></i></a>
                </div>
            </div>
        </div>
        <p class="float-left d-md-none">
            <button type="button" class="btn btn-sm rojo-bg" data-toggle="offcanvas">
                <span class="ion-chevron-left"> Ocultar filtros</span>
                <span class="ion-chevron-right"> Mostrar filtros</span>
            </button>
        </p>

        <div id="references">
            <div id="recientes" class="row">
                <%
                    for (Entry reference : references) {
                        String creator = "";
                        Title title = new Title();
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
                        <a href="/swb/<%=site.getId()%>/detalle?id=<%=reference.getId()%>">
                            <img src="<%=digital.getUrl()%>" />
                        </a>
                    </div>
                    <p class="oswB azul tit"><a href="#"><%=title.getValue()%></a></p>
                    <p class="azul autor"><a href="#"><%=creator%></a></p>
                </div>
                <%
                    }
                %>
            </div>
            <jsp:include page="pager.jsp" flush="true"/>
        </div>
        <%
            }else if (null != word) { out.println("No se encontraron resultados para la búsqueda " + word); }
            else { out.println("Debe proporcionar un criterio de búsqueda"); }
        %>
    </div>
</div>