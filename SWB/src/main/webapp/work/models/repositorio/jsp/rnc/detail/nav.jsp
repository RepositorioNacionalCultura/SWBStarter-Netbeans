<%--
    Document   : nav.jsp
    Created on : 22/08/2018, 15:49:04 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.Entry, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL"%>
<%
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    SWBResourceURL digitURL = paramRequest.getRenderUrl().setMode("DIGITAL");
    digitURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    Entry entry = (Entry)request.getAttribute("entry");
    String w = null != request.getParameter("word") ? request.getParameter("word") : "";
    Integer t = null != request.getAttribute("t") ? (Integer)request.getAttribute("t") : 0;
    Integer r = null != request.getParameter("r") ? Utils.toInt(request.getParameter("r")) : 0;
    String l = null != request.getParameter("leap") ? "&leap="+request.getParameter("leap") : "";
    String fs = null != request.getAttribute("filter") ? "&filter="+request.getAttribute("filter") : "";
    String f = null != request.getAttribute("sort") ? "&sort="+request.getAttribute("sort") : "";
    Integer position = null != entry ? entry.getPosition() + 1 : 1;
    Integer objects = (null != entry && null != entry.getDigitalObject()) ?  entry.getDigitalObject().size() : 0;
    
    int iDigit = null != entry ? entry.getPosition() : 0;
    int iPrev = iDigit-1;
    int iNext = iDigit+1;
%>
<div class="col-1 col-md-4 explo-back">
    <%
	if (iPrev >= 0) {
    %>
            <a href="#" onclick="nextObj('<%=digitURL%>?id=', '<%=entry.getId()%>&word=<%=w%>&r=<%=r%>&t=<%=t%><%=l%><%=fs%><%=f%>', <%=iPrev%>);"><i class="fa fa-long-arrow-left" aria-hidden="true"></i></a>
    <%  } %>
</div>
<div class="col-2 col-md-4 explo-obje">
    <%=position%>/<%=objects%> <%=paramRequest.getLocaleString("usrmsg_view_detail_objects")%>
</div>
<div class="col-1 col-md-4 explo-forw">
    <%
	if (iNext < objects) {
    %>
            <a href="#" onclick="nextObj('<%=digitURL%>?id=', '<%=entry.getId()%>&word=<%=w%>&r=<%=r%>&t=<%=t%><%=l%><%=fs%><%=f%>', <%=iNext%>);"><i class="fa fa-long-arrow-right" aria-hidden="true"></i></a>
    <%  } %>
</div>