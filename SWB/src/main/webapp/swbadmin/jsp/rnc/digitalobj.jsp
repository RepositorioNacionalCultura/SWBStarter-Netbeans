<%--
    Document   : digitalobject.jsp
    Created on : 5/12/2017, 11:48:36 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, java.util.ArrayList, java.util.List, org.semanticwb.portal.api.SWBResourceURL"%>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<%
    int images = 0;
    String url = "";
    String title = "";
    String creator = "";
    DigitalObject digital = null;
    List<Title> titles = new ArrayList<>();
    List<String> creators = new ArrayList<>();
    StringBuilder divVisor = new StringBuilder();
    StringBuilder scriptCallVisor = new StringBuilder();
    int iDigit = (Integer) request.getAttribute("iDigit");
    int iPrev = iDigit - 1;
    int iNext = iDigit + 1;
    List<DigitalObject> digitalobjects = new ArrayList<>();
    Entry entry = (Entry) request.getAttribute("entry");
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    SWBResourceURL digitURL = paramRequest.getRenderUrl().setMode("DIGITAL");
    digitURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    WebSite site = paramRequest.getWebPage().getWebSite();
    if (null != entry) {
        if (null != entry.getDigitalObject()) {
            creators = entry.getCreator();
            titles = entry.getRecordtitle();
            digitalobjects = entry.getDigitalObject();
            images = null != digitalobjects ? digitalobjects.size() : 0;
            digital = images > iDigit ? digitalobjects.get(iDigit) : new DigitalObject();
            url = null != digital.getUrl() ? digital.getUrl() : "";
            creator = creators.size() > 0 ? creators.get(0) : "";
            if (!titles.isEmpty()) title = titles.get(0).getValue();
            if (!url.isEmpty() && url.endsWith(".dzi"))
		divVisor.append("<div id=\"pyramid\" class=\"openseadragon front-page\">");
            else if (digital.getUrl().endsWith("view") || digital.getUrl().endsWith(".png") || digital.getUrl().endsWith(".jpg") || digital.getUrl().endsWith(".JPG")) {
		divVisor.append("<div id=\"pyramid\" class=\"openseadragon front-page\">");
		scriptCallVisor.append("<script type=\"text/javascript\">")
                    .append("OpenSeadragon({")
                    .append("	id:\"pyramid\",")
                    .append("	showHomeControl: false,")
                    .append("	prefixUrl:      \"/work/models/").append(site.getId()).append("/open/\",")
                    .append("	showNavigator: true,")
                    .append("	panHorizontal: false,")
                    .append("	defaultZoomLevel: 1,")
                    .append("	minZoomLevel: 1,")
                    .append("	maxZoomLevel: 2,")
                    .append("	visibilityRatio: 1,")
                    .append("	tileSources:   {")
                    .append("		type: 'image',")
                    .append("		url: '").append(digital.getUrl()).append("'")
                    .append("	}")
                    .append("});")
                    .append("</script>");
            }else {
                if (digital.getUrl().endsWith(".zip") || digital.getUrl().startsWith("application/vnd")) divVisor.append("<a href='").append(digital.getUrl()).append("'><img src=\"").append(entry.getResourcethumbnail()).append("\"></a>");
		else divVisor.append("<img src=\"").append(digital.getUrl()).append("\">");
            }
        }
    }
%>
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
                    <a href="#" onclick="fbShare();"><span class="ion-social-facebook"></span></a>
                </div>
                <div class="col-3">
                    <span class="ion-social-twitter"></span>
                </div>
                <div class="col-6">
                    <a href="#" onclick="loadDoc('<%=entry.getId()%>');"><span class="ion-heart"></span></a> <%=entry.getResourcestats().getViews()%>
                </div>
            </div>
            <div class="explo3 row">
                <div class="col-6">
                    <%
                        if (iPrev >= 0) {
                    %>
                            <a href="#" onclick="nextObj('<%=digitURL%>?id=', '<%=entry.getId()%>', <%=iPrev%>);"><span class="ion-chevron-left"></span> <%=paramRequest.getLocaleString("usrmsg_view_detail_prev_object")%></a>
                    <%
                        }
                    %>
                </div>
                <div class="col-6">
                    <%
                        if (iNext < images) {
                    %>
                            <a href="#" onclick="nextObj('<%=digitURL%>?id=', '<%=entry.getId()%>', <%=iNext%>);"><%=paramRequest.getLocaleString("usrmsg_view_detail_next_object")%> <span class="ion-chevron-right"></span></a>
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