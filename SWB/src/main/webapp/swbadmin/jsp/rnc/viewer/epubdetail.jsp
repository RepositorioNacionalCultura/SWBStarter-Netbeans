<%--
    Document   : epubdetail.jsp
    Created on : 22/05/2018, 11:48:36 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DateDocument, mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.resources.ArtDetail, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, java.util.ArrayList, java.util.List"%>
<script type="text/javascript" src="/swbadmin/js/rnc/detail.js"></script>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
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
    String userLang = paramRequest.getUser().getLanguage();
    if (null != entry) {
        iDigit = entry.getPosition();
	if (null != entry.getDigitalObject()) {
            digitalobjects = entry.getDigitalObject();
            books = null != digitalobjects ? digitalobjects.size() : 0;
            digital = books >= iDigit ? digitalobjects.get(iDigit) : new DigitalObject();
            if (null != digital.getUrl() && digital.getUrl().endsWith(".epub")) {
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
                    .append("	<div id=\"loader\"><img src=\"/work/models/repositorio/img/loader.gif\"></div>")
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
                    .append("           window.reader = ePubReader(\"").append(digital.getUrl()).append("\", {")
                    .append("               restore: true")
                    .append("           });")
                    .append("       }")
                    .append("   };")
                    .append("</script>");
                    title = Utils.getTitle(entry.getRecordtitle(), 0);
                    creator = Utils.getRowData(entry.getCreator(), 0, false);
                }
	}
    }
    SWBResourceURL digitURL = paramRequest.getRenderUrl().setMode("DIGITAL");
    digitURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    String scriptFB = Utils.getScriptFBShare(request);
%>
<%=scriptFB%>
<section id="detalle" class="vis-book">
    <jsp:include page="../flow.jsp" flush="true"/>
    <div id="idetail" class="detalleimg">
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

<div id="addCollection">
    <p>
        <div id="addCollection-tree"></div>
    </p>
</div>

<div class="modal fade" id="newCollection" tabindex="-1" role="dialog" aria-labelledby="modalTitle" aria-hidden="true"></div>