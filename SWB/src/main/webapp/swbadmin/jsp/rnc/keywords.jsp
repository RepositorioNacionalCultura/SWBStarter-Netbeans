<%--
    Document   :keywords.jsp
    Created on : 22/06/2018, 12:40:53 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.response.Entry, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.model.WebSite"%>
<%
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    Entry entry = (Entry) request.getAttribute("entry");
    String back = (String) request.getAttribute("back");
    WebSite site = paramRequest.getWebPage().getWebSite();
    String userLang = paramRequest.getUser().getLanguage();
%>
<div class="col-2 palabrastit">
	<span class="rojo"><%=paramRequest.getLocaleString("usrmsg_view_detail_words")%></span> <span class=""><%=paramRequest.getLocaleString("usrmsg_view_detail_key")%></span> <span class="ion-ios-arrow-thin-right"></span>
</div>
<div class="col-10 palabrascont">
    <% 
        for (String key :  entry.getKeywords()) {
            out.println("<a href=\"/"+userLang+"/"+site.getId()+"/resultados?word="+key+"\">"+key+"</a>");
        }
    %>
</div>
<div class="">
    <a href="<%=back%>">
        <i aria-hidden="true" class="fa fa-long-arrow-left"></i> <%=paramRequest.getLocaleString("usrmsg_view_detail_back")%>
    </a>
</div>