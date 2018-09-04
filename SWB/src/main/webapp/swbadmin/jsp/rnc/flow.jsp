<%--
    Document   : flow.jsp
    Created on : 21/08/2018, 16:24:47 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.Entry, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.model.WebSite"%>
<%
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    String back = (String)request.getAttribute("back");
    String w = null != request.getParameter("word") ? request.getParameter("word") : "";
    Integer t = null != request.getAttribute("t") ? (Integer)request.getAttribute("t") : 0;
    Integer r = null != request.getParameter("r") ? Utils.toInt(request.getParameter("r")) : 0;
    String l = null != request.getParameter("leap") ? "&leap="+request.getParameter("leap") : "";
    String fs = null != request.getAttribute("filter") ? "&filter="+request.getAttribute("filter") : "";
    String f = null != request.getAttribute("sort") ? "&sort="+request.getAttribute("sort") : "";
    
    Integer iprev = r - 1;
    Integer inext = r + 1;
    WebSite site = paramRequest.getWebPage().getWebSite();
    String userLang = paramRequest.getUser().getLanguage();
%>
<button class="btn btn-regresarDetalle" onclick="<%=back%>">
    <span class="ion-chevron-left"></span><%=paramRequest.getLocaleString("usrmsg_view_detail_back")%>
</button>
<div class="navegadetalle">
    <%
        if (iprev > -1) {
    %>
            <div class="navegadetalle-left">
		<a href="#"onclick="nextResult('/<%=userLang%>/<%=site.getId()%>/detalle?word=<%=w%>&r=<%=iprev%>&t=<%=t%><%=l%><%=fs%><%=f%>');"><span class="ion-chevron-left"></span></a>
            </div>
    <%  } if (inext < t) { %>
            <div class="navegadetalle-right">
		<a href="#" onclick="nextResult('/<%=userLang%>/<%=site.getId()%>/detalle?word=<%=w%>&r=<%=inext%>&t=<%=t%><%=l%><%=fs%><%=f%>');"><span class="ion-chevron-right"></span></a>
            </div>       
    <%  } %>
</div>