<%--
    Document   :keywords.jsp
    Created on : 22/06/2018, 12:40:53 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.Entry, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.model.WebSite"%>
<%
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    Entry entry = (Entry) request.getAttribute("entry");
    String back = (String) request.getAttribute("back");
    WebSite site = paramRequest.getWebPage().getWebSite();
    String userLang = paramRequest.getUser().getLanguage();
    String w = null != request.getParameter("w") ? request.getParameter("w") : "";
    Integer t = null != request.getAttribute("t") ? (Integer) request.getAttribute("t") : 0;
    Integer r = null != request.getParameter("r") ? Utils.toInt(request.getParameter("r")) : 0;
    String l = null != request.getParameter("leap") ? "&leap="+request.getParameter("leap") : "";
    String fs = null != request.getAttribute("filter") ? "&filter=" + request.getAttribute("filter") : "";
    Integer iprev = r - 1;
    Integer inext = r + 1;
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
    <%	if (iprev > -1) { %>
            <span class="ion-chevron-left"></span><a href="#" onclick="nextResult('/<%=userLang%>/<%=site.getId()%>/detalle?w=<%=w%>&r=<%=iprev%>&t=<%=t%><%=l%><%=fs%>');"><%=paramRequest.getLocaleString("usrmsg_view_detail_prev_record")%></a>
    <% }if (inext < t) { %>
            <a href="#" onclick="nextResult('/<%=userLang%>/<%=site.getId()%>/detalle?w=<%=w%>&r=<%=inext%>&t=<%=t%><%=l%><%=fs%>');"><%=paramRequest.getLocaleString("usrmsg_view_detail_next_record")%> <span class="ion-chevron-right"></span></a>
    <% } %>
</div>