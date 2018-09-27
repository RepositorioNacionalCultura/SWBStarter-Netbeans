<%--
    Document   : pdfdetail.jsp
    Created on : 22/05/2018, 11:48:36 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DateDocument, mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.resources.ArtDetail, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, java.util.ArrayList, java.util.List"%>

<%
    int pdfs = 0;
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
    if (null != entry) {
        iDigit = entry.getPosition();
	if (null != entry.getDigitalObject()) {
            digitalobjects = entry.getDigitalObject();
            pdfs = null != digitalobjects ? digitalobjects.size() : 0;
            digital = pdfs >= iDigit ? digitalobjects.get(iDigit) : new DigitalObject();
            if (null != digital.getUrl() && (digital.getUrl().endsWith(".pdf") || digital.getUrl().endsWith("view"))) {
		scriptHeader.append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/style.css'/>");
                scriptHeader.append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/viewer-pdf.css'/>");
		if (Utils.getClientBrowser(request).contains("Firefox")) {
                    scriptCallVisor.append("<iframe src=\"").append(digital.getUrl()).append("\" width=\"1200px\" height=\"900px\"></iframe>");
		}else if (Utils.getClientBrowser(request).contains("edge")) {
                    scriptCallVisor.append("<script type=\"text/javascript\">")
                    .append("   $(document).ready(function() {")
                    .append("       PDFObject.embed(\"").append(digital.getUrl()).append("\", \"#pdfdetail\");")
                    .append("   });")
                    .append("</script>");
                    divVisor.append("<div id=\"pdfdetail\"></div>");
		}else {
                    String url = digital.getUrl().contains("/multimedia") ? digital.getUrl().replaceFirst("/multimedia", "..") : digital.getUrl();
                    scriptCallVisor.append("<iframe src=\"").append("/multimedia/ViewerJS/#").append(url).append("\" width=\"1200px\" height=\"900px\" allowfullscreen webkitallowfullscreen></iframe>");
                }
		title = Utils.getTitle(entry.getRecordtitle(), 0);
                creator = Utils.getRowData(entry.getCreator(), 0, false);
            }
        }
    }
    SWBResourceURL digitURL = paramRequest.getRenderUrl().setMode("DIGITAL");
    digitURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    
    String scriptFB = Utils.getScriptFBShare(request);
    String userLang = paramRequest.getUser().getLanguage();
%>
<%=scriptFB%>
<section id="detalle" class="vis-pdf">
    <div id="idetail" class="detalleimg">
        <jsp:include page="../flow.jsp" flush="true"/>
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
                    <jsp:include page="../share.jsp" flush="true"/>
                </div>
		<div class="explo3 row">
                    <jsp:include page="../nav.jsp" flush="true"/>
                </div>
            </div>
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