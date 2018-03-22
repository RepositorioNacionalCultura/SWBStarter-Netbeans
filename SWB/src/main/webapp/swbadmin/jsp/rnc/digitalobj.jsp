<%--
    Document   : artdetail.jsp
    Created on : 5/12/2017, 11:48:36 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, java.util.ArrayList, java.util.List"%>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<%
    int images = 0;
    String title = "";
    String creator = "";
    String url = "";
    DigitalObject digital = null;
    List<Title> titles = new ArrayList<>();
    List<String> creators = new ArrayList<>();
    int iDigit = (Integer)request.getAttribute("iDigit");
    int iPrev = iDigit-2;
    List<DigitalObject> digitalobjects = new ArrayList<>();
    Entry entry = (Entry)request.getAttribute("entry");
    if (null != entry) {
	if (null != entry.getDigitalobject()) {
            creators = entry.getCreator();
            titles = entry.getRecordtitle();
            digitalobjects = entry.getDigitalobject();
            images = null != digitalobjects ? digitalobjects.size() : 0;
            digital = images >= iDigit ? digitalobjects.get(iDigit-1) : new DigitalObject();
            url = digital.getUrl().replace("localhost", "129.144.24.140");
            creator = creators.size() > 0 ? creators.get(0) : "";
            if (!titles.isEmpty()) title = titles.get(0).getValue();
	}
    }
%>
<div id="idetail" class="detalleimg">
    <div class="obranombre">
	<h3 class="oswB"><%=title%></h3>
        <p class="oswL"><%=creator%></p>
    </div>
    <div class="explora">
	<div class="explora1">
            <img src="<%=url%>">
            <span class="ion-plus"></span>
            <span class="ion-minus"></span>
        </div>
        <div class="explora2">
            <p><a href="#">© Declaración de Derechos</a></p>
            <div>
                <% if (iPrev > -1) { %><a href="#" onclick="loadImg('<%=entry.getId()%>', '<%=iPrev%>');"><span class="ion-ios-arrow-left"></span></a><% } %> <%=iDigit%>/<%=images%> <% if (iDigit < images) { %><a href="#" onclick="loadImg('<%=entry.getId()%>', '<%=iDigit%>');"><span class="ion-ios-arrow-right"></span></a><% } %>
            </div>
            <span class="ion-grid"></span>
            <span class="ion-heart"><a href="#" onclick="loadDoc('<%=entry.getId()%>');"><i class="fa fa-heart" aria-hidden="true"></i></a></span>
            <span class="ion-android-share-alt"></span>
            <span class="ion-archive"></span>
        </div>
    </div>
    <img src="<%=url%>">
</div>