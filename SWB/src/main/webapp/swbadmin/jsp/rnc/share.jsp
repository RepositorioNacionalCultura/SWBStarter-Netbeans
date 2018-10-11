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
<div class="explora2">
    <div class="row">
        <div class="col-3 col-md-12 explo-rese">
            &reg; <%=paramRequest.getLocaleString("usrmsg_view_detail_all_rights")%>
        </div>
        <div class="col-2 col-md-4 explo-face">
            <a href="#" onclick="fbShare();"><span class="ion-social-facebook"></span></a>
        </div>
        <div class="col-1 col-md-4 explo-twit">
            <a href="https://twitter.com/share?ref_src=twsrc%5Etfw" class="twitter-share-button" data-show-count="false"><span class="ion-social-twitter"></span></a>
            <!--<a href="#"><span class="ion-social-twitter"></span></a>-->
        </div>
        <div class="col-2 col-md-4 explo-like">
            <a href="#" onclick="loadDoc('/<%=userLang%>/<%=site.getId()%>/favorito?id=', '<%=entry.getId()%>');"><span class="ion-heart"></span> <%=entry.getResourcestats().getViews()%></a>
        </div>
        <jsp:include page="nav.jsp" flush="true"/>
    </div>
</div>