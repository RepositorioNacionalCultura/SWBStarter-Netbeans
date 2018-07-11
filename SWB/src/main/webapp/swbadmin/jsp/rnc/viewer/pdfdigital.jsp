<%--
    Document   : pdfdigital.jsp
    Created on : 29/05/2018, 11:48:36 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, java.util.ArrayList, java.util.List, org.semanticwb.portal.api.SWBResourceURL"%>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<%
    int pdfs = 0;
    String title = "";
    String creator = "";
    DigitalObject digital = null;
    List<Title> titles = new ArrayList<>();
    List<String> creators = new ArrayList<>();
    StringBuilder divVisor = new StringBuilder();
    int iDigit = (Integer) request.getAttribute("iDigit");
    int iPrev = iDigit - 1;
    int iNext = iDigit + 1;
    StringBuilder scriptHeader = new StringBuilder();
    StringBuilder scriptCallVisor = new StringBuilder();
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
            pdfs = null != digitalobjects ? digitalobjects.size() : 0;
            digital = pdfs > iDigit ? digitalobjects.get(iDigit) : new DigitalObject();
            creator = creators.size() > 0 ? creators.get(0) : "";
            if (!titles.isEmpty()) title = titles.get(0).getValue();
            scriptHeader.append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/style.css'/>");
            scriptHeader.append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/viewer-pdf.css'/>");
            divVisor.append("<div id=\"pdfdetail\"></div>");
            scriptCallVisor.append("<script type=\"text/javascript\">")
                .append("   $(document).ready(function() {")
                .append("       PDFObject.embed(\"").append(digital.getUrl()).append("\", \"#pdfdetail\");")
                .append("   });")
                .append("</script>");
        }
    }
%>
<!--div id="idetail" class="detalleimg"-->
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
                        if (iNext < pdfs) {
                    %>
                            <a href="#" onclick="nextObj('<%=digitURL%>?id=', '<%=entry.getId()%>', <%=iNext%>);"><%=paramRequest.getLocaleString("usrmsg_view_detail_next_object")%> <span class="ion-chevron-right"></span></a>
                    <%
                        }
                    %>
                </div>
            </div>
        </div>
    </div>
    <%=scriptHeader%>
    <%=divVisor%>
    <%=scriptCallVisor%>
<!--/div-->