<%--
    Document   : videodigital.jsp
    Created on : 29/05/2018, 11:48:36 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, java.util.ArrayList, java.util.List"%>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<%
    int vids = 0;
    String title = "";
    String creator = "";
    DigitalObject digital = null;
    List<Title> titles = new ArrayList<>();
    List<String> creators = new ArrayList<>();
    StringBuilder divVisor = new StringBuilder();
    int iDigit = (Integer)request.getAttribute("iDigit");
    int iPrev = iDigit-1;
    int iNext = iDigit+1;
    List<DigitalObject> digitalobjects = new ArrayList<>();
    Entry entry = (Entry)request.getAttribute("entry");
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    if (null != entry) {
	if (null != entry.getDigitalObject()) {
            creators = entry.getCreator();
            titles = entry.getRecordtitle();
            digitalobjects = entry.getDigitalObject();
            vids = null != digitalobjects ? digitalobjects.size() : 0;
            digital = vids > iDigit ? digitalobjects.get(iDigit) : new DigitalObject();
            creator = creators.size() > 0 ? creators.get(0) : "";
            if (!titles.isEmpty()) title = titles.get(0).getValue();
            if (null != digital.getUrl()) {
                String mime = null != digital.getMediatype() ? digital.getMediatype().getMime() : "";
                if (digital.getUrl().endsWith(".mp4")) mime = "video/mp4";
                divVisor.append("<video id=\"video\" width=\"97%\" poster=\"").append(entry.getResourcethumbnail()).append("\" controls controlsList=\"nodownload\">");
                divVisor.append("	<source src=\"").append(digital.getUrl()).append("\" type=\"").append(mime).append("\">");
		divVisor.append("	<p>Tu navegador no soporta video en HTML5</p>");
                divVisor.append("</video>");
		divVisor.append("<div id=\"video-controls\" class=\"row\">")
                .append("   <div class=\"col-11 col-md-7 video-players\">")
                .append("       <div class=\"col-12 video-botones\">");
		if (iPrev >= 0) {
                    String prev = "onclick=\"nextObj('"+entry.getId()+"',"+iPrev+");\"";
                    divVisor.append("           <button type=\"button\" id=\"prev\" class=\"prev\"").append(prev).append("><span class=\"ion-ios-skipbackward\"></span></button>");
                }
                divVisor.append("           <button type=\"button\" id=\"play-pause\" class=\"play\"><span class=\"ion-ios-play\"></span></button>");
		if (iNext < vids) {
                    String next = "onclick=\"nextObj('"+entry.getId()+"',"+iNext+");\"";
                    divVisor.append("           <button type=\"button\" id=\"next\" class=\"next\"").append(next).append("><span class=\"ion-ios-skipforward\"></span></button>");
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
                    <span class="ion-social-facebook"></span>
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
                </div>
                <div class="col-6">
                </div>
            </div>
        </div>
    </div>
    <%=divVisor%>
</div>