<%-- 
    Document   : videodetail.jsp
    Created on : 22/05/2018, 11:48:36 AM
    Author     : sergio.tellez
--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="mx.gob.cultura.portal.utils.Utils, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBResourceURL, org.semanticwb.portal.api.SWBParamRequest"%>
<%@ page import="java.util.List, java.util.ArrayList, mx.gob.cultura.portal.response.Title, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.DateDocument, mx.gob.cultura.portal.response.DigitalObject"%>
<script type="text/javascript" src="/swbadmin/js/rnc/detail.js"></script>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<%
    int vids = 0;
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
    SWBResourceURL digitURL = paramRequest.getRenderUrl().setMode("DIGITAL");
    digitURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    if (null != entry) {
	if (null != entry.getDigitalObject()) {
            creators = entry.getCreator();
            titles = entry.getRecordtitle();
            digitalobjects = entry.getDigitalObject();
            vids = null != digitalobjects ? digitalobjects.size() : 0;
            digital = vids >= iDigit ? digitalobjects.get(iDigit-1) : new DigitalObject();
            if (null != digital.getUrl()) {
		String mime = null != digital.getMediatype() ? digital.getMediatype().getMime() : "";
		if (digital.getUrl().endsWith(".mp4")) mime = "video/mp4";
                scriptHeader.append("<script src=\"https://cdn.plyr.io/2.0.18/plyr.js\"></script>");
		scriptHeader.append("<script>plyr.setup();</script>");
                scriptHeader.append("<script type=\"text/javascript\" src=\"/work/models/").append(site.getId()).append("/js/viewer-video.js\"></script>");
                scriptHeader.append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/viewer-video-2.css'/>");
		divVisor.append("<video id=\"video\" width=\"97%\" poster=\"").append(entry.getResourcethumbnail()).append("\" controls controlsList=\"nodownload\">");
                divVisor.append("	<source src=\"").append(digital.getUrl()).append("\" type=\"").append(mime).append("\">");
		divVisor.append("	<p>Tu navegador no soporta video en HTML5</p>");
                divVisor.append("</video>");
		divVisor.append("<div id=\"video-controls\" class=\"row\">")
                .append("   <div class=\"col-11 col-md-7 video-players\">")
                .append("       <div class=\"col-12 video-botones\">");
		if (iDigit > 1)
                    divVisor.append("       <button type=\"button\" id=\"prev\" class=\"prev\"><span class=\"ion-ios-skipbackward\"></span></button>");
                divVisor.append("       <button type=\"button\" id=\"play-pause\" class=\"play\"><span class=\"ion-ios-play\"></span></button>");
		if (iDigit < vids) {
                    String next = "onclick=\"nextObj('"+digitURL+"?id=',"+entry.getId()+"',"+iDigit+");\"";
                    divVisor.append("       <button type=\"button\" id=\"next\" class=\"next\"").append(next).append("><span class=\"ion-ios-skipforward\"></span></button>");
		}
                divVisor.append("       </div>")
                .append("       <div class=\"col-12 video-range\">")
                .append("           <span class=\"video-time oswM\">0:00:00</span>")
                .append("               <input type=\"range\" id=\"seek-bar\" value=\"0\">")
                .append("           <span class=\"video-duration oswM\">0:00:00</span>")
                .append("       </div>")
                .append("   </div>")
                
                .append("   <div class=\"col-4 video-volumen\">")
                .append("       <div class=\"row\">")
                .append("           <div class=\"col-3 col-sm-3 col-md-2 video-volbtn\">")
                .append("               <button type=\"button\" id=\"mute\"><span class=\"ion-ios-volume-high\"></span></button>")
                .append("           </div>")
                .append("           <div class=\"col-8 col-sm-9 col-md-10 video-volrange\">")
                .append("               <input type=\"range\" id=\"volume-bar\" min=\"0\" max=\"1\" step=\"0.1\" value=\"1\">")
                .append("           </div>")
                .append("       </div>")
                .append("   </div>")
                .append("   <div class=\"col-1 video-screen\">")
                .append("       <button type=\"button\" id=\"full-screen\"><span class=\"ion-android-expand\"></span></button>")
                .append("   </div>")
                .append("</div>");
                scriptCallVisor.append("<script>")
                    .append("   $(document).ready(function() {")
                    .append("       $(\"#play-pause\").click(function() {")
                    .append("		$(\".obranombre\").toggleClass(\"opaco\");")
                    .append("       });")
                    .append("	});")
                    .append("</script>");
		type = entry.getResourcetype().size() > 0 ? entry.getResourcetype().get(0) : "";
                creator = creators.size() > 0 ? creators.get(0) : "";
                if (!titles.isEmpty()) title = titles.get(0).getValue();
                period = null != entry.getDatecreated() ? Utils.esDate(entry.getDatecreated().getValue()) : "";
            }
        }
    }
    String scriptFB = Utils.getScriptFBShare(request);
%>
<%=scriptFB%>
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
                    </div>
                    <div class="col-6">
                    </div>
                </div>
            </div>
        </div>
        <%=scriptHeader%>
        <%=scriptCallVisor%>
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