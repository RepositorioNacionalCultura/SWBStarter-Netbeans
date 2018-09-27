<%--
    Document   : sketchdetail.jsp
    Created on : 22/05/2018, 11:48:36 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DateDocument, mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.resources.ArtDetail, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, java.util.ArrayList, java.util.List"%>

<%
    int books = 0;
    int iDigit = 0;
    String title = "";
    String creator = "";
    DigitalObject digital = null;
    StringBuilder divVisor = new StringBuilder();
    StringBuilder scriptHeader = new StringBuilder();
    StringBuilder scriptCallVisor = new StringBuilder();
    List<DigitalObject> digitalobjects = new ArrayList<>();
    Entry entry = (Entry)request.getAttribute("entry");
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    WebSite site = paramRequest.getWebPage().getWebSite();
    if (null != entry) {
        iDigit = entry.getPosition();
        if (null != entry.getDigitalObject()) {
            digitalobjects = entry.getDigitalObject();
            books = null != digitalobjects ? digitalobjects.size() : 0;
            digital = books >= iDigit ? digitalobjects.get(iDigit) : new DigitalObject();
            if (null != digital.getUrl()) {
                scriptHeader.append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/style.css'/>");
                scriptHeader.append("<link rel='stylesheet' type='text/css' href='https://cdn.jsdelivr.net/npm/mediaelement@4.2.7/build/mediaelementplayer.min.css'/>");
                divVisor.append("<iframe width=\"1280\" height=\"860\" src=\"\" id=\"api-frame\" allowfullscreen mozallowfullscreen=\"true\" webkitallowfullscreen=\"true\"></iframe>");
                scriptCallVisor.append("<style type=\"text/css\">")
                    .append("	.explora, .obranombre { bottom:60px}")
                    .append("	iframe#api-frame { height:100%; width:100%; border:none}")
                    .append("</style>")
                    .append("<script type=\"text/javascript\">")
                    .append("   var iframe = document.getElementById( 'api-frame' );")
                    .append("	var urlid = '9e898967d93e406e8a1aecf878caec21';")
                    .append("	var client = new Sketchfab( iframe );")
                    .append("	client.init( urlid, {")
                    .append("       success: function onSuccess( api ) {")
                    .append("		api.start();")
                    .append("		api.addEventListener( 'viewerready', function() {")
                    .append("               console.log( 'Viewer is ready' );")
                    .append("           });")
                    .append("       },")
                    .append("       error: function onError() {")
                    .append("           console.log( 'Viewer error' );")
                    .append("       }")
                    .append("	});")
                    .append("</script>");
            }
            title = Utils.getTitle(entry.getRecordtitle(), 0);
            creator = Utils.getRowData(entry.getCreator(), 0, false);
        }
    }
    SWBResourceURL digitURL = paramRequest.getRenderUrl().setMode("DIGITAL");
    digitURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    
    String scriptFB = Utils.getScriptFBShare(request);
    String userLang = paramRequest.getUser().getLanguage();
%>
<%=scriptFB%>
<section id="detalle" class="vis-sketch">
    <div id="idetail" class="detalleimg">
        <jsp:include page="../flow.jsp" flush="true"/>
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
                    <jsp:include page="../share.jsp" flush="true"/>
                </div>
                <div class="explo3 row">
                    <jsp:include page="../nav.jsp" flush="true"/>
                </div>
            </div>
        </div>
        <%=scriptHeader%>
        <%=divVisor%>
    </div>
</section>
<section id="detalleinfo">
    <div class="container">
        <div class="row">              
            <jsp:include page="../rack.jsp" flush="true"/>
            <jsp:include page="../techdata.jsp" flush="true"/>
        </div>
    </div>
</section>
<section id="palabras">
    <div class="container">
        <div class="row">
            <jsp:include page="../keywords.jsp" flush="true"/>
        </div>
    </div>
</section>
<%=scriptCallVisor%>
<jsp:include page="addtree.jsp" flush="true"/>