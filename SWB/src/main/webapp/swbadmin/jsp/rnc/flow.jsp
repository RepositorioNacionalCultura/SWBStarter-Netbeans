<%--
    Document   : flow.jsp
    Created on : 21/08/2018, 16:24:47 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.Entry, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL"%>
<%
    int iPrev = 0;
    int iNext = 0;
    int images = 0;
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    SWBResourceURL digitURL = paramRequest.getRenderUrl().setMode("DIGITAL");
    digitURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    String back = (String)request.getAttribute("back");
    Entry entry = (Entry)request.getAttribute("entry");
    if (null != entry) {
        int iDigit = entry.getPosition();
	iPrev = iDigit-1;
        iNext = iDigit+1;
	if (null != entry.getDigitalObject())
            images = null != entry.getDigitalObject() ? entry.getDigitalObject().size() : 0;
    }
    String w = null != request.getParameter("word") ? request.getParameter("word") : "";
    Integer t = null != request.getAttribute("t") ? (Integer)request.getAttribute("t") : 0;
    Integer r = null != request.getParameter("r") ? Utils.toInt(request.getParameter("r")) : 0;
    String l = null != request.getParameter("leap") ? "&leap="+request.getParameter("leap") : "";
    String fs = null != request.getAttribute("filter") ? "&filter="+request.getAttribute("filter") : "";
    String f = null != request.getAttribute("sort") ? "&sort="+request.getAttribute("sort") : "";
%>
<button class="btn btn-regresarDetalle" onclick="<%=back%>">
    <span class="ion-chevron-left"></span><%=paramRequest.getLocaleString("usrmsg_view_detail_back")%>
</button>
<div class="navegadetalle">
    <%
        if (iPrev >= 0) {
    %>
            <div class="navegadetalle-left">
		<a href="#" onclick="nextObj('<%=digitURL%>?id=', '<%=entry.getId()%>&word=<%=w%>&r=<%=r%>&t=<%=t%><%=l%><%=fs%><%=f%>', <%=iPrev%>);"><span class="ion-chevron-left"></span></a>
            </div>
    <%  } if (iNext < images) { %>
            <div class="navegadetalle-right">
		<a href="#" onclick="nextObj('<%=digitURL%>?id=', '<%=entry.getId()%>&word=<%=w%>&r=<%=r%>&t=<%=t%><%=l%><%=fs%><%=f%>', <%=iNext%>);"><span class="ion-chevron-right"></span></a>
            </div>       
    <%  } %>
</div>