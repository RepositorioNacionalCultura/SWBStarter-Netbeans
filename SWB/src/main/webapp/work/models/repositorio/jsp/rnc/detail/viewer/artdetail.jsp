<%--
    Document   : artdetail.jsp
    Created on : 5/12/2017, 11:48:36 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DateDocument, mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.resources.ArtDetail, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, 
         org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, java.util.ArrayList, java.util.List, org.w3c.dom.Element"%>
<%
    int iDigit = 0;
    int images = 0;
    String title = "";
    String creator = "";
    DigitalObject digital = null;
    StringBuilder divVisor = new StringBuilder();
    StringBuilder scriptHeader = new StringBuilder();
    StringBuilder workReference = new StringBuilder();
    StringBuilder scriptCallVisor = new StringBuilder();
    List<DigitalObject> digitalobjects = new ArrayList<>();
    Entry entry = (Entry) request.getAttribute("entry");
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    WebSite site = paramRequest.getWebPage().getWebSite();
    StringBuilder divNavigator = new StringBuilder("<div id=\"navigatorDiv\" class=\"explora1\"><div id=\"toolbarDiv\" class=\"exploratl\"></div></div>");
    if (null != entry) {
        iDigit = entry.getPosition();
        if (null != entry.getDigitalObject()) {
            digitalobjects = entry.getDigitalObject();
            images = null != digitalobjects ? digitalobjects.size() : 0;
            digital = images >= iDigit ? digitalobjects.get(iDigit) : new DigitalObject();
            if (null == digital.getUrl()) digital.setUrl("");
            title = Utils.getTitle(entry.getRecordtitle(), 0);
            creator = Utils.getRowData(entry.getCreator(), 0, true);
            workReference.append("<div class=\"obranombre\">")
                .append("	<h3 class=\"oswB\">").append(title).append("</h3>")
		.append("	<p class=\"oswL\">").append(creator).append("</p>")
                .append("</div>");
            if (digital.getUrl().endsWith("view") || digital.getUrl().endsWith(".png") || digital.getUrl().endsWith(".jpg") || digital.getUrl().endsWith(".JPG")) {
                scriptHeader.append("<script src=\"/work/models/").append(site.getId()).append("/js/openseadragon.min.js\"></script>");
                scriptHeader.append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/openseadragon.css'/>");
                divVisor.append("<div id=\"pyramid\" class=\"openseadragon front-page\">");
                scriptCallVisor.append("<script type=\"text/javascript\">")
                    .append("var vw = OpenSeadragon({")
                    .append("	id:\"pyramid\",")
                    .append("	toolbar:     \"toolbarDiv\",")
                    .append("	navigatorId: \"navigatorDiv\",")
                    .append("	showHomeControl: false,")
                    .append("	prefixUrl:      \"/work/models/").append(site.getId()).append("/open/\",")
                    .append("	showNavigator: true,")
                    .append("	immediateRender: true,")
                    .append("	defaultZoomLevel: 0.6,")
                    .append("	maxZoomLevel: 1.5,")
                    .append("	minZoomLevel: 0.4,")
                    .append("	tileSources:   {")
                    .append("       type: 'image',")
                    .append("       url: '").append(digital.getUrl()).append("'")
                    .append("	}")
                    .append("});")
                    .append("vw.gestureSettingsMouse.scrollToZoom = false;")
                    .append("</script>");
            }else if (digital.getUrl().endsWith(".zip") || digital.getUrl().endsWith(".rtf") || digital.getUrl().endsWith(".docx")) {
                divVisor.append("<a href='").append(digital.getUrl()).append("'><img src=\"").append(entry.getResourcethumbnail()).append("\"></a>");
            }else if (digital.getUrl().contains("amazonaws")) {
                divNavigator = new StringBuilder();
                workReference = new StringBuilder();
		workReference.append("<div class=\"scroll-gpixel\">")
                    .append("	<a href=\"#detalleinfo\">Ver ficha <span class=\"ion-chevron-down\"></span></a>")
                    .append("</div>");
                divVisor.append("<div id=\"gpxdetail\">")
                    .append("   <iframe class=\"sciframeclass\" src=\"").append(digital.getUrl()).append(" width=\"1440\" height=\"940\" scw=\"1440\" sch=\"940\"></iframe>")
                    .append("</div>");
            }else {
                int width = 1333;
		int height = 2000;
		String viewer = null != site.getModelProperty("host_media") ? site.getModelProperty("host_media") : "https://mexicana.cultura.gob.mx";
		String url = digital.getUrl().startsWith("/multimedia/") ? viewer+digital.getUrl()+"/" : digital.getUrl();
		String xmlpath = viewer+digital.getUrl()+"/ImageProperties.xml";
		Element ele = Utils.getOSDProps(xmlpath);
		if (null != ele) {
                    width = Utils.toInt(ele.getAttribute("WIDTH"));
                    height = Utils.toInt(ele.getAttribute("HEIGHT"));
		}
                scriptHeader.append("<script src=\"/work/models/").append(site.getId()).append("/js/openseadragon.min.js\"></script>");
                scriptHeader.append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/openseadragon.css'/>");
                divVisor.append("<div id=\"pyramid\" class=\"openseadragon front-page\">");
                scriptCallVisor.append("<script type=\"text/javascript\">")
                    .append("var dz = OpenSeadragon({")
                    .append("	id:\"pyramid\",")
                    .append("	toolbar:     \"toolbarDiv\",")
                    .append("	navigatorId:   \"navigatorDiv\",")
                    .append("	showHomeControl: false,")
                    .append("	prefixUrl:      \"/work/models/").append(site.getId()).append("/open/\",")
                    .append("	showNavigator: true,")
                    .append("	immediateRender: true,")
                    .append("	defaultZoomLevel: 1,")
                    //.append("	maxZoomLevel: 1.5,")
                    //.append("	minZoomLevel: 0.4,")
                    .append("	tileSources:   [{")
                    .append("		type:       	\"zoomifytileservice\",")
                    .append("		height:       	").append(height).append(",")
                    .append("		width:       	").append(width).append(",")
                    .append("		tilesUrl:       	\"").append(url).append("\"")
                    .append("	}]")
                    .append("});")
                    .append("dz.gestureSettingsMouse.scrollToZoom = false;")
                    .append("</script>");
            }
        }
    }
    SWBResourceURL digitURL = paramRequest.getRenderUrl().setMode("DIGITAL");
    digitURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    //llamada a la generacion del script para compartir con Facebook: funcion fbShare()
    String scriptFB = Utils.getScriptFBShare(request);
%>
<%=scriptFB%>

<section id="detalle" class="vis-osd">
    <div id="idetail" class="detalleimg">
        <jsp:include page="../flow.jsp" flush="true"/>
        <%=workReference%>
        <div class="explora">
            <%=divNavigator%>
            <jsp:include page="../share.jsp" flush="true"/>
        </div>
        <%=scriptHeader%>
        <%=divVisor%>
        <%=scriptCallVisor%>
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

<jsp:include page="../addtree.jsp" flush="true"/>