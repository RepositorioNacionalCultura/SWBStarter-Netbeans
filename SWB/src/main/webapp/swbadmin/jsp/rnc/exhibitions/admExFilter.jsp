<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.utils.ArtWork"%>
<%@page import="mx.gob.cultura.portal.response.DigitalObject"%>
<%@page import="mx.gob.cultura.portal.response.Title,org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, org.semanticwb.portal.api.SWBResourceModes, org.semanticwb.model.Resource, java.util.List"%>
<%
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    SWBResourceURL pageURL = paramRequest.getRenderUrl();
    pageURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    pageURL.setMode(SWBResourceModes.Mode_ADMIN);
    pageURL.setAction("PAGE");

    String word = (String) request.getAttribute("criteria");
    List<ArtWork> references = (List<ArtWork>) request.getAttribute("PAGE_LIST");
    WebSite site = paramRequest.getWebPage().getWebSite();
    Integer last = (Integer) request.getAttribute("LAST_RECORD");
    Integer first = (Integer) request.getAttribute("FIRST_RECORD");
    Integer total = (Integer) request.getAttribute("NUM_RECORDS_TOTAL");
%>
<!--div class="row resultadosbar">
        <div class="col-md-3"><a class=" oswL" href="javascript:history.go(-1)"><i aria-hidden="true" class="fa fa-long-arrow-left"></i> Regresar</a></div>
        <div class="col-md-9">
                <p class=" oswL">
<% if (null != word) {%>
        Seleccione los elementos de la exhibición: <%= word%>
<% } %>
</p>
</div>
</div-->
<form id="admExForm" action="" method="post">
    <!--div class="row offcanvascont"-->
    <% if (!references.isEmpty()) {  %>
    <!--div class="offcanvas rojo-bg">
            <span onclick="openNav()" id="offcanvasAbre">
                    <em class="fa fa-sliders" aria-hidden="true"></em> Filtros <i class="ion-chevron-right " aria-hidden="true"></i>
            </span>
            <span onclick="closeNav()" id="offcanvasCierra">
                    <em class="fa fa-sliders" aria-hidden="true"></em> Filtros <i class="ion-close " aria-hidden="true"></i>
            </span>
    </div-->
    <!--jsp:include page="admExFilters.jsp" flush="true"/-->
    <% } %>
    <!--div id="contenido"-->
    <a name="showPage"></a>
    <% if (!references.isEmpty()) {%>
    <div id="references">
        <div class="ruta row">
            <div class="col-12 col-sm-8 col-md-8">
                <p class="oswLc"><%=first%>-<%=last%> de <%=total%> resultados</p>
            </div>
            <div class="col-12 col-sm-4 col-md-4 ordenar">
                <!--a href="#" onclick="setGrid();"><i class="fa fa-th select" aria-hidden="true"></i></a>
                <a href="#" onclick="setList();"><i class="fa fa-th-list" aria-hidden="true"></i></a-->
            </div>
        </div>
        <div id="resultados" class="card-columns">
            <%
                for (ArtWork art : references) {
                    String creator = "";
                    Title title = new Title();
                    Entry reference = art.getEntry();
                    DigitalObject digital = new DigitalObject();
                    List<Title> titles = reference.getRecordtitle();
                    List<String> creators = reference.getCreator();
                    List<DigitalObject> digitalobject = reference.getDigitalobject();
                    if (!digitalobject.isEmpty()) {
                        digital = digitalobject.get(0);
                    }
                    if (!titles.isEmpty()) {
                        title = titles.get(0);
                    }
                    if (!creators.isEmpty()) {
                        creator = creators.get(0);
                    }
            %>
            <div class="pieza-res card">
                <a href="/swb/<%=site.getId()%>/detalle?id=<%=reference.getId()%>" target="blank">
                    <img src="<%=digital.getUrl()%>" />
                </a>
                <div>
                    <p class="oswB azul tit"><a href="#"><%=title.getValue()%></a></p>
                    <p class="azul autor"><a href="#"><%=creator%></a></p>
                    <p class="tipo"><input type="checkbox" name="hiddenarts" value="<%=reference.getId()%>" <% if (art.isHidden()) {
                                                                                        out.print("checked");
                                                                                    }%>>Ocultar
                        <br/><input type="checkbox" name="favarts" value="<%=reference.getId()%>" <% if (art.isFavorite()) {
                                                                                                out.print("checked");
                                                                                            } %>>Favorito</p>
                </div>
            </div>
            <%
                }
            %>
        </div>
        <jsp:include page="admExPager.jsp" flush="true"/>
    </div>
    <%
        } else if (null != word) {
            out.println("No se encontraron resultados para la búsqueda " + word);
        } else {
            out.println("Debe proporcionar un criterio de búsqueda");
        }
    %>
    <!--/div-->
    <!--/div-->
</form>