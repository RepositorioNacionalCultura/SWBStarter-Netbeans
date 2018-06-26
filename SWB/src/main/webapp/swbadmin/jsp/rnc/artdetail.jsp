<%--
    Document   : artdetail.jsp
    Created on : 5/12/2017, 11:48:36 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DateDocument, mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.resources.ArtDetail, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, java.util.ArrayList, java.util.List"%>
<script type="text/javascript" src="/swbadmin/js/rnc/detail.js"></script>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<%
    int iDigit = 1;
    int images = 0;
    String type = "";
    String title = "";
    String period = "";
    String creator = "";
    DigitalObject digital = null;
    List<Title> titles = new ArrayList<>();
    List<String> creators = new ArrayList<>();
    StringBuilder divVisor = new StringBuilder();
    StringBuilder scriptHeader = new StringBuilder();
    StringBuilder scriptCallVisor = new StringBuilder();
    List<DigitalObject> digitalobjects = new ArrayList<>();
    Entry entry = (Entry) request.getAttribute("entry");
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    WebSite site = paramRequest.getWebPage().getWebSite();
    if (null != entry) {
        if (null != entry.getDigitalObject()) {
            creators = entry.getCreator();
            titles = entry.getRecordtitle();
            digitalobjects = entry.getDigitalObject();
            images = null != digitalobjects ? digitalobjects.size() : 0;
            digital = images >= iDigit ? digitalobjects.get(iDigit - 1) : new DigitalObject();
            if (null == digital.getUrl()) digital.setUrl("");
            if (digital.getUrl().endsWith(".dzi")) {
                    scriptHeader.append("<script src=\"/work/models/").append(site.getId()).append("/js/openseadragon.min.js\"></script>");
                    scriptHeader.append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/style.css'/>");
                    divVisor.append("<div id=\"pyramid\" class=\"openseadragon front-page\">");
                    scriptCallVisor.append("<script type=\"text/javascript\">")
                            .append("OpenSeadragon({")
                            .append("	id:\"pyramid\",")
                            .append("	showHomeControl: false,")
                            .append("	prefixUrl:      \"/work/models/").append(site.getId()).append("/open/\",")
                            .append("	tileSources:   [")
                            .append("		\"https://openseadragon.github.io/example-images/highsmith/highsmith.dzi\"")
                            .append("	]")
                            .append("});")
                            .append("</script>");
            }else if (digital.getUrl().endsWith("view") || digital.getUrl().endsWith(".png") || digital.getUrl().endsWith(".jpg") || digital.getUrl().endsWith(".JPG")) {
                    scriptHeader.append("<script src=\"/work/models/").append(site.getId()).append("/js/openseadragon.min.js\"></script>");
                    scriptHeader.append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/style.css'/>");
                    divVisor.append("<div id=\"pyramid\" class=\"openseadragon front-page\">");
                    scriptCallVisor.append("<script type=\"text/javascript\">")
                            .append("OpenSeadragon({")
                            .append("	id:\"pyramid\",")
                            .append("	showHomeControl: false,")
                            .append("	prefixUrl:      \"/work/models/").append(site.getId()).append("/open/\",")
                            .append("	showNavigator: false,")
                            .append("	tileSources:   {")
                            .append("		type: 'image',")
                            .append("		url: '").append(digital.getUrl()).append("'")
                            .append("	}")
                            .append("});")
                            .append("</script>");
            } else {
                if (digital.getUrl().endsWith(".zip")) divVisor.append("<a href='").append(digital.getUrl()).append("'><img src=\"").append(entry.getResourcethumbnail()).append("\"></a>");
                else divVisor.append("<img src=\"").append(digital.getUrl()).append("\">");
            }
            type = entry.getResourcetype().size() > 0 ? entry.getResourcetype().get(0) : "";
            creator = creators.size() > 0 ? creators.get(0) : "";
            if (!titles.isEmpty()) title = titles.get(0).getValue();
            period = null != entry.getDatecreated() ? Utils.esDate(entry.getDatecreated().getValue()) : "";
        }
    }
    String back = (String)request.getAttribute("back");
    SWBResourceURL digitURL = paramRequest.getRenderUrl().setMode("DIGITAL");
    digitURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    //llamada a la generacion del script para compartir con Facebook: funcion fbShare()
    String scriptFB = Utils.getScriptFBShare(request);
%>
<%=scriptFB%>

<%=scriptHeader%>

<section id="detalle">
    <div id="idetail" class="detalleimg">
        <div class="obranombre">
            <h3 class="oswB"><%=title%></h3>
            <p class="oswL"><%=creator%></p>
        </div>
        <div class="explora">
            <div class="explora2">
                <div class="explo1">
                    © <%=paramRequest.getLocaleString("usrmsg_view_detail_all_rights")%>
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
                        <a href="#" onclick="loadDoc('/swb/<%=site.getId()%>/favorito?id=', '<%=entry.getId()%>');"><span class="ion-heart"></span></a> <%=entry.getResourcestats().getViews()%>
                    </div>
                </div>
                <div class="explo3 row">
                    <div class="col-6">
                        <%
                            if (iDigit > 1) {
                        %>
                                <span class="ion-chevron-left"></span> <%=paramRequest.getLocaleString("usrmsg_view_detail_prev_object")%>
                        <%
                            }
                        %>
                    </div>
                    <div class="col-6">
                        <%
                            if (iDigit < images) {
                        %>
                                <a href="#" onclick="nextObj('<%=digitURL%>?id=', '<%=entry.getId()%>', <%=iDigit%>);"><%=paramRequest.getLocaleString("usrmsg_view_detail_next_object")%> <span class="ion-chevron-right"></span></a>
                        <%
                            }
                        %>
                    </div>
                </div>
            </div>
        </div>
        <%=divVisor%>
        <%=scriptCallVisor%>
    </div>
</section>
<section id="detalleinfo">
    <div class="container">
        <div class="row">              
            <jsp:include page="rack.jsp" flush="true"/>
            <jsp:include page="techdata.jsp" flush="true"/>
            <jsp:include page="keywords.jsp" flush="true"/>
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
                    
<div class="modal fade" id="newCollection" tabindex="-1" role="dialog" aria-labelledby="modalTitle" aria-hidden="true"></div>