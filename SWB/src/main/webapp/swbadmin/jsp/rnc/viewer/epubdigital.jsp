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
    String userLang = paramRequest.getUser().getLanguage();
    if (null != entry) {
        if (null != entry.getDigitalObject()) {
            digitalobjects = entry.getDigitalObject();
            books = null != digitalobjects ? digitalobjects.size() : 0;
            digital = books >= iDigit ? digitalobjects.get(iDigit - 1) : new DigitalObject();
            url = null != digital.getUrl() ? digital.getUrl() : "";
            title =  Utils.replaceSpecialChars(Utils.getTitle(entry.getRecordtitle(), 0));
            creator = Utils.replaceSpecialChars(Utils.getRowData(entry.getCreator(), 0, false));
            if (null != url && url.endsWith(".epub")) {
                scriptHeader.append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/style.css'/>")
                    .append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/viewer-epub.css'/>");
                divVisor.append("<div id=\"main\">")
                    .append("   <div id=\"prev\" onclick=\"Book.prevPage();\" class=\"arrow\"><span class=\"ion-chevron-left\"></span></div>")
                    .append("   <div id=\"area\"></div>")
                    .append("   <div id=\"next\" onclick=\"Book.nextPage();\" class=\"arrow\"><span class=\"ion-chevron-right\"></span></div>")
                    .append("</div>");
                scriptCallVisor.append("<script>")
                    .append("	\"use strict\";")
                    .append("	var Book = ePub(\"").append(digital.getUrl()).append("\");")
                    .append("</script>")
                    .append("<script>")
                    .append("   Book.renderTo(\"area\");{")
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
                    <a href="#" onclick="loadDoc('/<%=userLang%>/<%=site.getId()%>/favorito?id=', <%=entry.getId()%>');"><span class="ion-heart"></span></a> <%=entry.getResourcestats().getViews()%>
                </div>
            </div>
            <div class="explo3 row">
                <jsp:include page="../nav.jsp" flush="true"/>
            </div>
        </div>
    </div>
    <%=scriptHeader%>
    <%=divVisor%>
    <%=scriptCallVisor%>