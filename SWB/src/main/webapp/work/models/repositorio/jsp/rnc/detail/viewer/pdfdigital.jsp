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
    String viewer = null != site.getModelProperty("pdf_pathViewer") ? site.getModelProperty("pdf_pathViewer")+"/multimedia/" : "https://mexicana.cultura.gob.mx"+"/multimedia/";
    if (null != entry) {
        if (null != entry.getDigitalObject()) {
            digitalobjects = entry.getDigitalObject();
            pdfs = null != digitalobjects ? digitalobjects.size() : 0;
            digital = pdfs > iDigit ? digitalobjects.get(iDigit) : new DigitalObject();
            title =  Utils.replaceSpecialChars(Utils.getTitle(entry.getRecordtitle(), 0));
            creator = Utils.replaceSpecialChars(Utils.getRowData(entry.getCreator(), 0, false));
            scriptHeader.append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/style.css'/>");
            scriptHeader.append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/viewer-pdf.css'/>");
            String url = digital.getUrl().contains(viewer) ? digital.getUrl().replace(viewer, "../") : digital.getUrl();
            if (url.contains("../")) {
                scriptHeader = new StringBuilder();
                scriptCallVisor.append("<iframe src=\"").append(viewer).append("ViewerJS/#").append(url).append("\" width=\"1200px\" height=\"900px\" allowfullscreen webkitallowfullscreen></iframe>");
            }else if (url.startsWith("/multimedia/")) {
		scriptHeader = new StringBuilder();
		scriptCallVisor.append("<iframe src=\"").append(viewer).append("ViewerJS/#").append(url.replace("/multimedia/","../")).append("\" width=\"1200px\" height=\"900px\" allowfullscreen webkitallowfullscreen></iframe>");
            }else if (Utils.getClientBrowser(request).contains("Firefox") || Utils.getClientBrowser(request).contains("Safari")) {
                scriptCallVisor.append("<iframe src=\"").append(url).append("\" width=\"1200px\" height=\"900px\"></iframe>");
            }else {
                scriptCallVisor.append("<script type=\"text/javascript\">")
                    .append("   $(document).ready(function() {")
                    .append("       PDFObject.embed(\"").append(url).append("\", \"#pdfdetail\");")
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
        <jsp:include page="../share.jsp" flush="true"/>
    </div>
    <%=scriptHeader%>
    <%=divVisor%>
    <%=scriptCallVisor%>
<!--/div-->