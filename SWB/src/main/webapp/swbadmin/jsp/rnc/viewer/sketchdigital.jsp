<%--
    Document   : sketchdigital.jsp
    Created on : 29/05/2018, 11:48:36 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, java.util.ArrayList, java.util.List, org.semanticwb.portal.api.SWBResourceURL"%>

<%
    int books = 0;
    String title = "";
    String creator = "";
    DigitalObject digital = null;
    StringBuilder divVisor = new StringBuilder();
    StringBuilder scriptCallVisor = new StringBuilder();
    int iDigit = (Integer)request.getAttribute("iDigit");
    List<DigitalObject> digitalobjects = new ArrayList<>();
    Entry entry = (Entry)request.getAttribute("entry");
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    SWBResourceURL digitURL = paramRequest.getRenderUrl().setMode("DIGITAL");
    digitURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    if (null != entry) {
	if (null != entry.getDigitalObject()) {
            digitalobjects = entry.getDigitalObject();
            books = null != digitalobjects ? digitalobjects.size() : 0;
            digital = books > iDigit ? digitalobjects.get(iDigit) : new DigitalObject();
            if (null != digital.getMediatype() && null != digital.getMediatype().getName() && !digital.getMediatype().getName().trim().isEmpty()) {
                divVisor.append("<iframe width=\"1280\" height=\"860\" src=\"\" id=\"api-frame\" allowfullscreen mozallowfullscreen=\"true\" webkitallowfullscreen=\"true\"></iframe>");
                scriptCallVisor.append("<style type=\"text/css\">")
                    .append("	.explora, .obranombre { bottom:60px}")
                    .append("	iframe#api-frame { height:100%; width:100%; border:none}")
                    .append("</style>")
                    .append("<script type=\"text/javascript\">")
                    .append("   var iframe = document.getElementById( 'api-frame' );")
                    .append("	var urlid = '").append(digital.getMediatype().getName()).append("';")
                    .append("	var client = new Sketchfab( iframe );")
                    .append("	client.init( urlid, {")
                    .append("       success: function onSuccess( api ) {")
                    .append("		api.start();")
                    .append("		api.addEventListener( 'viewerready', function() {")
                    .append("               console.log( 'Viewer is ready' );")
                    .append("           });")
                    .append("       },")
                    .append("       error: function onError() {")
                    .append("           console.log( 'Viewer error' );")
                    .append("       }")
                    .append("	});")
                    .append("</script>");
            }
        }
        title =  Utils.replaceSpecialChars(Utils.getTitle(entry.getRecordtitle(), 0));
        creator = Utils.replaceSpecialChars(Utils.getRowData(entry.getCreator(), 0, false));
    }
%>
    <jsp:include page="../flow.jsp" flush="true"/>
    <div class="obranombre">
        <h3 class="oswB"><%=title%></h3>
        <p class="oswL"><%=creator%></p>
    </div>
    <div class="explora">
        <jsp:include page="../share.jsp" flush="true"/>
    </div>
    <%=divVisor%>
    <%=scriptCallVisor%>