<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DigitalObject,mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Identifier, mx.gob.cultura.portal.response.Title, org.semanticwb.portal.api.SWBParamRequest"%>
<%@ page import="java.util.List" %>
<%@ page import="org.semanticwb.model.WebSite" %>
<%
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    WebSite site = paramRequest.getWebPage().getWebSite();
    String mode = "card-columns";
    List<Entry> references = (List<Entry>)request.getAttribute("PAGE_LIST");
    if (null != request.getAttribute("mode")) mode = (String)request.getAttribute("mode");
    Integer last = (Integer)request.getAttribute("LAST_RECORD");
    Integer first = (Integer)request.getAttribute("FIRST_RECORD");  
    Integer total = (Integer)request.getAttribute("NUM_RECORDS_TOTAL");
    String word = null != request.getAttribute("word") ? Utils.suprXSS((String)request.getAttribute("word")) : "";
    String uri = !word.isEmpty() ? "&word="+word+"&leap="+first : "";
%>
<% if (!references.isEmpty()) {  %>
    <div id="references">
        <div class="ruta-resultado row">
            <div class="col-12 col-sm-8 col-md-8">
		<% if (null != word) { %>
                    <p class="oswL rojo"><%=first%>-<%=last%> <%=paramRequest.getLocaleString("usrmsg_view_search_of")%> <%=total%> <%=paramRequest.getLocaleString("usrmsg_view_search_results")%> <%=paramRequest.getLocaleString("usrmsg_view_search_of")%> <span class="oswB rojo"><%=word%></span></p>
		<% }else { out.println(paramRequest.getLocaleString("usrmsg_view_search_empty_criteria")); } %>
            </div>
            <div class="col-12 col-sm-4 col-md-4 ordenar">
                <a href="#" onclick="setGrid();"><i class="fa fa-th select" aria-hidden="true"></i></a>
                <a href="#" onclick="setList();"><i class="fa fa-th-list" aria-hidden="true"></i></a>
            </div>
        </div>
        <div id="resultados" class="<%=mode%>">
        <%  
            for (Entry reference : references) {
                String holder = "";
                Title title = new Title();
                List<String> holders = reference.getHolder();
		List<Title> titles = reference.getRecordtitle();
                if (!titles.isEmpty()) title = titles.get(0);
                List<String> creators = reference.getCreator();
		List<String> resourcetype = reference.getResourcetype();
                holder = null != holders && holders.size() > 0 ? holders.get(0) : "";
                String resource = resourcetype.size() > 0 ? resourcetype.get(0) : "";
		String creator = creators.size() > 0 && null != creators.get(0) ? creators.get(0) : "";
        %>
                <div class="pieza-res card">
                    <a href="/<%=paramRequest.getUser().getLanguage()%>/<%=site.getId()%>/detalle?id=<%=reference.getId()%><%=uri%>">
                        <img src="<%=reference.getResourcethumbnail()%>" />
                    </a>
                    <div>
                        <p class="tit"><a href="/<%=paramRequest.getUser().getLanguage()%>/<%=site.getId()%>/detalle?id=<%=reference.getId()%><%=uri%>"><%=title.getValue()%></a></p>
                        <p class="autor"><a href="#"><%=creator%></a><br/><i><%=holder%></i></p>
                        <p class="tipo"><%=resource%></p>
                    </div>
                </div>
        <%
            }
        %>
    </div>
    <jsp:include page="pager.jsp" flush="true"/>
</div>
<%
    }
%>