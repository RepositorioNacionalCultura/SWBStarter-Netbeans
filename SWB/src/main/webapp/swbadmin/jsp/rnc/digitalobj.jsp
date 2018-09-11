<%--
    Document   : digitalobject.jsp
    Created on : 5/12/2017, 11:48:36 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, java.util.ArrayList, java.util.List, org.semanticwb.portal.api.SWBResourceURL"%>
<%
    int images = 0;
    String title = "";
    String creator = "";
    DigitalObject digital = null;
    StringBuilder divVisor = new StringBuilder();
    StringBuilder scriptHeader = new StringBuilder();
    StringBuilder scriptCallVisor = new StringBuilder();
    int iDigit = (Integer) request.getAttribute("iDigit");
    List<DigitalObject> digitalobjects = new ArrayList<>();
    Entry entry = (Entry) request.getAttribute("entry");
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    SWBResourceURL digitURL = paramRequest.getRenderUrl().setMode("DIGITAL");
    digitURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    WebSite site = paramRequest.getWebPage().getWebSite();
    String userLang = paramRequest.getUser().getLanguage();
    StringBuilder divNavigator = new StringBuilder("<div id=\"navigatorDiv\" class=\"explora1\"><div id=\"toolbarDiv\" class=\"exploratl\"></div></div>");
    if (null != entry) {
        if (null != entry.getDigitalObject()) {
            digitalobjects = entry.getDigitalObject();
            images = null != digitalobjects ? digitalobjects.size() : 0;
            digital = images > iDigit ? digitalobjects.get(iDigit) : new DigitalObject();
            title =  Utils.replaceSpecialChars(Utils.getTitle(entry.getRecordtitle(), 0));
            creator = Utils.replaceSpecialChars(Utils.getRowData(entry.getCreator(), 0, false));
            scriptHeader.append("<script src=\"/work/models/").append(site.getId()).append("/js/openseadragon.min.js\"></script>");
            scriptHeader.append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/openseadragon.css'/>");
            if (digital.getUrl().endsWith(".dzi")) {
                scriptHeader.append("<script src=\"/work/models/").append(site.getId()).append("/js/openseadragon.min.js\"></script>");
                scriptHeader.append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/openseadragon.css'/>");
                divVisor.append("<div id=\"pyramid\" class=\"openseadragon front-page\">");
                scriptCallVisor.append("<script type=\"text/javascript\">")
                    .append("var dz = OpenSeadragon({")
                    .append("	id:\"pyramid\",")
                    .append("	showNavigator: true,")
                    .append("	showHomeControl: false,")
                    .append("	navigatorId:   \"navigatorDiv\",")
                    .append("	prefixUrl:      \"/work/models/").append(site.getId()).append("/open/\",")
                    .append("	tileSources:   [")
                    .append("		\"").append(digital.getUrl()).append("\"")
                    .append("	]")
                    .append("});")
                    .append("dz.gestureSettingsMouse.scrollToZoom = false;")
                    .append("</script>");
            }else if (digital.getUrl().endsWith("view") || digital.getUrl().endsWith(".png") || digital.getUrl().endsWith(".jpg") || digital.getUrl().endsWith(".JPG")) {
		divVisor.append("<div id=\"pyramid\" class=\"openseadragon front-page\">");
		scriptCallVisor.append("<script type=\"text/javascript\">")
                    .append("var vw = OpenSeadragon({")
                    .append("	id:\"pyramid\",")
                    .append("	showHomeControl: false,")
                    .append("	toolbar:        \"toolbarDiv\",")
                    .append("	navigatorId:    \"navigatorDiv\",")
                    .append("	prefixUrl:      \"/work/models/").append(site.getId()).append("/open/\",")
                    .append("	showNavigator: true,")
                    .append("	defaultZoomLevel: 0.8,")
                    .append("	maxZoomLevel: 1.5,")
                    .append("	minZoomLevel: 0.4,")
                    .append("	tileSources:   {")
                    .append("       type: 'image',")
                    .append("       url: '").append(digital.getUrl()).append("'")
                    .append("	}")
                    .append("});")
                    .append("vw.gestureSettingsMouse.scrollToZoom = false;")
                    .append("</script>");
            }else {
                divNavigator = new StringBuilder();
                if (digital.getUrl().endsWith(".zip") || digital.getUrl().endsWith(".rtf") || digital.getUrl().endsWith(".docx") || digital.getUrl().endsWith(".avi")) divVisor.append("<a href='").append(digital.getUrl()).append("'><img src=\"").append(entry.getResourcethumbnail()).append("\"></a>");
		else divVisor.append("<img src=\"").append(digital.getUrl()).append("\">");
            }
        }
    }
%>
<div id="idetail" class="detalleimg">
    <jsp:include page="flow.jsp" flush="true"/>
    <div class="obranombre">
        <h3 class="oswB"><%=title%></h3>
        <p class="oswL"><%=creator%></p>
    </div>
    <div class="explora">
        <%=divNavigator%>
        <div class="explora2">
            <div class="explo1">
                &reg; <%=paramRequest.getLocaleString("usrmsg_view_detail_all_rights")%>
            </div>
            <div class="explo2 row">
                <div class="col-3">
                    <a href="#" onclick="fbShare();"><span class="ion-social-facebook"></span></a>
                </div>
                <div class="col-3">
                    <span class="ion-social-twitter"></span>
                </div>
                <div class="col-6">
                    <a href="#" onclick="loadDoc('/<%=userLang%>/<%=site.getId()%>/favorito?id=', '<%=entry.getId()%>');"><span class="ion-heart"></span></a> <%=entry.getResourcestats().getViews()%>
                </div>
            </div>
            <div class="explo3 row">
                <jsp:include page="nav.jsp" flush="true"/>
            </div>
        </div>
    </div>
    <%=scriptHeader%>
    <%=divVisor%>
    <%=scriptCallVisor%>
</div>