<%-- 
    Document   : share
    Created on : 26/09/2018, 12:53:04 PM
    Author     : sergio.tellez
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.response.Entry, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest"%>
<%
    Entry entry = (Entry) request.getAttribute("entry");
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    WebSite site = paramRequest.getWebPage().getWebSite();
    String userLang = paramRequest.getUser().getLanguage();
%>
<div class="col-4">
    <a href="#" onclick="fbShare();"><span class="ion-social-facebook"></span></a>
</div>
<div class="col-4">
    <span class="ion-social-twitter"></span>
</div>
<div class="col-4">
    <a href="#" onclick="loadDoc('/<%=userLang%>/<%=site.getId()%>/favorito?id=', '<%=entry.getId()%>');"><span class="ion-heart"></span></a> <%=entry.getResourcestats().getViews()%>
</div>