<%--
    Document   : epubdigital.jsp
    Created on : 06/06/2018, 13:04:48 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, java.util.ArrayList, java.util.List, org.semanticwb.portal.api.SWBResourceURL"%>
<%
    int books = 0;
    String url = "";
    String title = "";
    String creator = "";
    DigitalObject digital = null;
    StringBuilder divVisor = new StringBuilder();
    StringBuilder scriptHeader = new StringBuilder();
    StringBuilder scriptCallVisor = new StringBuilder();
    int iDigit = (Integer)request.getAttribute("iDigit");
    List<DigitalObject> digitalobjects = new ArrayList<>();
    Entry entry = (Entry)request.getAttribute("entry");
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    SWBResourceURL digitURL = paramRequest.getRenderUrl().setMode("DIGITAL");
    digitURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    WebSite site = paramRequest.getWebPage().getWebSite();
    String viewer = null != site.getModelProperty("host_media") ? site.getModelProperty("host_media") : "https://mexicana.cultura.gob.mx";
    if (null != entry) {
        if (null != entry.getDigitalObject()) {
            digitalobjects = entry.getDigitalObject();
            books = null != digitalobjects ? digitalobjects.size() : 0;
            digital = books > iDigit ? digitalobjects.get(iDigit) : new DigitalObject();
            url = null != digital.getUrl() && digital.getUrl().startsWith("/multimedia/") ? viewer+digital.getUrl() : digital.getUrl();
            title =  Utils.replaceSpecialChars(Utils.getTitle(entry.getRecordtitle(), 0));
            creator = Utils.replaceSpecialChars(Utils.getRowData(entry.getCreator(), 0, false));
            if (null != url && url.endsWith(".epub")) {
                divVisor.append("<div id=\"sidebar\">")
                    .append("	<div id=\"panels\">")
                    .append("       <a id=\"show-Toc\" class=\"show_view icon-list-1 active\" data-view=\"Toc\">TOC</a>")
                    .append("	</div>")
                    .append("	<div id=\"tocView\" class=\"view\">")
                    .append("	</div>")
                    .append("	<div id=\"searchView\" class=\"view\">")
                    .append("       <ul id=\"searchResults\"></ul>")
                    .append("	</div>")
                    .append("	<div id=\"bookmarksView\" class=\"view\">")
                    .append("       <ul id=\"bookmarks\"></ul>")
                    .append("	</div>")
                    .append("	<div id=\"notesView\" class=\"view\">")
                    .append("       <div id=\"new-note\">")
                    .append("		<textarea id=\"note-text\"></textarea>")
                    .append("		<button id=\"note-anchor\">Anchor</button>")
                    .append("       </div>")
                    .append("       <ol id=\"notes\"></ol>")
                    .append("	</div>")
                    .append("</div>")
                    .append("<div id=\"main\">")
                    .append("	<div id=\"titlebar\">")
                    .append("       <div id=\"opener\">")
                    .append("		<a id=\"slider\" class=\"icon-menu\">Menu</a>")
                    .append("       </div>")
                    .append("       <div id=\"metainfo\">")
                    .append("		<span id=\"book-title\"></span>")
                    .append("           <span id=\"title-seperator\">&nbsp;&nbsp;-&nbsp;&nbsp;</span>")
                    .append("		<span id=\"chapter-title\"></span>")
                    .append("       </div>")
                    .append("       <div id=\"title-controls\">")
                    .append("		<a id=\"fullscreen\" class=\"icon-resize-full\">Fullscreen</a>")
                    .append("       </div>")
                    .append("   </div>")
                    .append("	<div id=\"divider\"></div>")
                    .append("	<div id=\"prev\" class=\"arrow\"><span class=\"ion-android-arrow-dropleft-circle\"></span></div>")
                    .append("	<div id=\"viewer\"></div>")
                    .append("	<div id=\"next\" class=\"arrow\"><span class=\"ion-android-arrow-dropright-circle\"></span></div>")
                    .append("	<div id=\"loader\"><img src=\"/work/models/").append(site.getId()).append("/img/loader.gif\"></div>")
                    .append("</div>")
                    .append("<div class=\"modal md-effect-1\" id=\"settings-modal\">")
                    .append("	<div class=\"md-content\">")
                    .append("       <h3>Settings</h3>")
                    .append("       <div>")
                    .append("		<p>")
                    .append("               <input type=\"checkbox\" id=\"sidebarReflow\" name=\"sidebarReflow\">Reflow text when sidebars are open.")
                    .append("		</p>")
                    .append("       </div>")
                    .append("       <div class=\"closer icon-cancel-circled\"></div>")
                    .append("	</div>")
                    .append("</div>")
                    .append("<div class=\"overlay\"></div>");
                scriptCallVisor.append("<script>")
                    .append("	\"use strict\";")
                    .append("	document.onreadystatechange = function () {")
                    .append("       if (document.readyState == \"complete\") {")
                    .append("           window.reader = ePubReader(\"").append(url).append("\", {")
                    .append("               restore: true")
                    .append("           });")
                    .append("       }")
                    .append("   };")
                    .append("</script>");
            }
        }
    }
%>
    <jsp:include page="../flow.jsp" flush="true"/>
    <div class="obranombre">
        <h3 class="oswB"><%=title%></h3>
        <p class="oswL"><%=creator%></p>
        <a href="#detalleinfo"><%=paramRequest.getLocaleString("usrmsg_view_detail_see_data")%><span class="ion-chevron-down"></span></a>
    </div>
    <div class="explora">
        <jsp:include page="../share.jsp" flush="true"/>
    </div>
    <%=scriptHeader%>
    <%=divVisor%>
    <%=scriptCallVisor%>