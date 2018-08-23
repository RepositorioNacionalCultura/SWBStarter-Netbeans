<%--
    Document   : nav.jsp
    Created on : 22/08/2018, 15:49:04 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.Entry, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.model.WebSite"%>
<%
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    Entry entry = (Entry)request.getAttribute("entry");
    WebSite site = paramRequest.getWebPage().getWebSite();
    String userLang = paramRequest.getUser().getLanguage();
    String w = null != request.getParameter("word") ? request.getParameter("word") : "";
    Integer t = null != request.getAttribute("t") ? (Integer)request.getAttribute("t") : 0;
    Integer r = null != request.getParameter("r") ? Utils.toInt(request.getParameter("r")) : 0;
    String l = null != request.getParameter("leap") ? "&leap="+request.getParameter("leap") : "";
    String fs = null != request.getAttribute("filter") ? "&filter="+request.getAttribute("filter") : "";
    String f = null != request.getAttribute("sort") ? "&sort="+request.getAttribute("sort") : "";
    Integer iprev = r - 1;
    Integer inext = r + 1;
    Integer position = null != entry ? entry.getPosition() + 1 : 1;
    Integer objects = (null != entry && null != entry.getDigitalObject()) ?  entry.getDigitalObject().size() : 0;
%>
<div class="col-4">
    <%
	if (iprev > -1) {
    %>
            <a href="#" onclick="nextResult('/<%=userLang%>/<%=site.getId()%>/detalle?word=<%=w%>&r=<%=iprev%>&t=<%=t%><%=l%><%=fs%><%=f%>');"><%=paramRequest.getLocaleString("usrmsg_view_detail_prev_record")%></a>
    <%  } %>
</div>
<div class="col-4">
    <%=position%>/<%=objects%> Objetos
</div>
<div class="col-4">
    <%
	if (inext < t) {
    %>
            <a href="#" onclick="nextResult('/<%=userLang%>/<%=site.getId()%>/detalle?word=<%=w%>&r=<%=inext%>&t=<%=t%><%=l%><%=fs%><%=f%>');"><%=paramRequest.getLocaleString("usrmsg_view_detail_next_record")%> <span class="ion-chevron-right"></span></a>
    <%  } %>
</div>