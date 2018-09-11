<%--
    Document   : pdfdigital.jsp
    Created on : 29/05/2018, 11:48:36 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, java.util.ArrayList, java.util.List, org.semanticwb.portal.api.SWBResourceURL"%>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<%
    int pdfs = 0;
    String title = "";
    String creator = "";
    DigitalObject digital = null;
    StringBuilder divVisor = new StringBuilder();
    int iDigit = (Integer) request.getAttribute("iDigit");
    StringBuilder scriptHeader = new StringBuilder();
    StringBuilder scriptCallVisor = new StringBuilder();
    List<DigitalObject> digitalobjects = new ArrayList<>();
    Entry entry = (Entry) request.getAttribute("entry");
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    SWBResourceURL digitURL = paramRequest.getRenderUrl().setMode("DIGITAL");
    digitURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    WebSite site = paramRequest.getWebPage().getWebSite();
    String userLang = paramRequest.getUser().getLanguage();
    if (null != entry) {
        if (null != entry.getDigitalObject()) {
            digitalobjects = entry.getDigitalObject();
            pdfs = null != digitalobjects ? digitalobjects.size() : 0;
            digital = pdfs > iDigit ? digitalobjects.get(iDigit) : new DigitalObject();
            title =  Utils.replaceSpecialChars(Utils.getTitle(entry.getRecordtitle(), 0));
            creator = Utils.replaceSpecialChars(Utils.getRowData(entry.getCreator(), 0, false));
            scriptHeader.append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/style.css'/>");
            scriptHeader.append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/viewer-pdf.css'/>");
            if (Utils.getClientBrowser(request).contains("Firefox") || Utils.getClientBrowser(request).contains("Safari")) {
		scriptCallVisor.append("<iframe src=\"").append(digital.getUrl()).append("\" width=\"1200px\" height=\"900px\"></iframe>");
            }else {
		scriptCallVisor.append("<script type=\"text/javascript\">")
                    .append("   $(document).ready(function() {")
                    .append("       PDFObject.embed(\"").append(digital.getUrl()).append("\", \"#pdfdetail\");")
                    .append("   });")
                    .append("</script>");
                divVisor.append("<div id=\"pdfdetail\"></div>");
            }
        }
    }
%>
<!--div id="idetail" class="detalleimg"-->
    <jsp:include page="../flow.jsp" flush="true"/>
    <div class="obranombre">
        <h3 class="oswB"><%=title%></h3>
        <p class="oswL"><%=creator%></p>
    </div>
    <div class="explora">
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
<!--/div-->