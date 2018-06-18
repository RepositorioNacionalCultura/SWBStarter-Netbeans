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
    int iDigit = 1;
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
    Entry entry = (Entry)request.getAttribute("entry");
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    WebSite site = paramRequest.getWebPage().getWebSite();
    if (null != entry) {
	if (null != entry.getDigitalObject()) {
            creators = entry.getCreator();
            titles = entry.getRecordtitle();
            digitalobjects = entry.getDigitalObject();
            books = null != digitalobjects ? digitalobjects.size() : 0;
            digital = books >= iDigit ? digitalobjects.get(iDigit-1) : new DigitalObject();
            if (null != digital.getUrl() && digital.getUrl().endsWith(".epub")) {
                scriptHeader.append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/style.css'/>");
		scriptHeader.append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/viewer-epub.css'/>");
                divVisor.append("<div id=\"main\">")
                    .append("   <div id=\"prev\" onclick=\"Book.prevPage();\" class=\"arrow\"><span class=\"ion-chevron-left\"></span></div>")
                    .append("   <div id=\"area\"></div>")
                    .append("   <div id=\"next\" onclick=\"Book.nextPage();\" class=\"arrow\"><span class=\"ion-chevron-right\"></span></div>")
                    .append("</div>");
                    scriptCallVisor.append("<script>")
                        .append("   Book.renderTo(\"area\");{")
                        .append("</script>");
                    type = entry.getResourcetype().size() > 0 ? entry.getResourcetype().get(0) : "";
                    creator = creators.size() > 0 ? creators.get(0) : "";
                    period = null != entry.getDatecreated() ? Utils.esDate(entry.getDatecreated().getValue()) : "";
                    if (!titles.isEmpty()) title = titles.get(0).getValue();
            }
	}
    }
    SWBResourceURL digitURL = paramRequest.getRenderUrl().setMode("DIGITAL");
    digitURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    String back = (String)request.getAttribute("back");
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
                            if (iDigit < books) {
                        %>
                                <a href="#" onclick="nextObj('<%=digitURL%>?id=', '<%=entry.getId()%>',<%=iDigit%>);"><%=paramRequest.getLocaleString("usrmsg_view_detail_next_object")%> <span class="ion-chevron-right"></span></a>
                        <%
                            }
                        %>
                    </div>
                </div>
            </div>
        </div>
        <%=divVisor%>
    </div>
</section>
<%=scriptCallVisor%>
<section id="detalleinfo">
    <div class="container">
        <div class="row">              
            <jsp:include page="../rack.jsp" flush="true"/>
            <jsp:include page="../techdata.jsp" flush="true"/>
            <div class="col-12 col-sm-6  col-md-3 col-lg-3 order-md-3 order-sm-3 order-3 clave">
                <div class="redes">
                    <a href="#" onclick="fbShare();"><span class="ion-social-facebook"></span></a>
                    <span class="ion-social-twitter"></span>
                </div>
                <div>
                    <p class="tit2"><%=paramRequest.getLocaleString("usrmsg_view_detail_key_words")%></p>
                    <p>
                        <%
                            int i = 0;
                            for (String key : entry.getKeywords()) {
                                i++;
                                out.println("<a href=\"#\">" + key + "</a>");
                                if (i < entry.getKeywords().size()) {
                                    out.println(" / ");
                                }
                            }
                        %>
                    </p>
                </div>
                <div class="">
                    <a href="<%=back%>">
                        <i aria-hidden="true" class="fa fa-long-arrow-left"></i> <%=paramRequest.getLocaleString("usrmsg_view_detail_back")%>
                    </a>
                </div>
            </div>
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