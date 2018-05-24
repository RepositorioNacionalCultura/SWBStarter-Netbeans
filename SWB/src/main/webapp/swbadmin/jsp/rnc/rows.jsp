<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.response.DigitalObject,mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest"%>
<%@ page import="java.util.List" %>
<%
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    WebSite site = paramRequest.getWebPage().getWebSite();
    String mode = "card-columns";
    List<Entry> references = (List<Entry>)session.getAttribute("PAGE_LIST");
    if (null != request.getAttribute("mode"))
        mode = (String)request.getAttribute("mode");
    Integer first = (Integer)session.getAttribute("FIRST_RECORD");
    Integer last = (Integer)session.getAttribute("LAST_RECORD");
    Integer total = (Integer)session.getAttribute("NUM_RECORDS_TOTAL");
    if (null == first) first = 1;
    if (!references.isEmpty()) {
%>
        <div class="ruta row">
            <div class="col-12 col-sm-8 col-md-8">
                <p class="oswLc"><%=first%>-<%=last%> <%=paramRequest.getLocaleString("usrmsg_view_search_of")%> <%=total%> <%=paramRequest.getLocaleString("usrmsg_view_search_results")%></p>
            </div>
            <div class="col-12 col-sm-4 col-md-4 ordenar">
                <a href="#" onclick="setGrid();"><i class="fa fa-th select" aria-hidden="true"></i></a>
                <a href="#" onclick="setList();"><i class="fa fa-th-list" aria-hidden="true"></i></a>
            </div>
        </div>
        <div id="resultados" class="<%=mode%>">
        <%
            int position = first;
            for (Object o : references) {
                Entry reference = (Entry)o;
                Title title = new Title();
                reference.setPosition(position);
                DigitalObject digital = new DigitalObject();
                List<String> creators = reference.getCreator();
                List<Title> titles = reference.getRecordtitle();
                List<String> resourcetype = reference.getResourcetype();
                List<DigitalObject> digitalobject = reference.getDigitalObject();
                if (!titles.isEmpty()) title = titles.get(0);
                if (!digitalobject.isEmpty()) digital = digitalobject.get(0);
                String type = resourcetype.size() > 0 ? resourcetype.get(0) : "";
                String creator = creators.size() > 0 && null != creators.get(0) ? creators.get(0) : "";
		if (type.equalsIgnoreCase("gramatica") || type.equalsIgnoreCase("receta") || type.equalsIgnoreCase("video")) digital.setUrl("/work/models/"+site.getId()+"/img/icono-video.jpg");
		else if (type.equalsIgnoreCase("otro") || type.equalsIgnoreCase("thesis") || type.equalsIgnoreCase("book")) digital.setUrl("/work/models/"+site.getId()+"/img/icono-pdf.png");
                else if (type.equalsIgnoreCase("cantos")) digital.setUrl("/work/models/"+site.getId()+"/img/icono-audio.jpg");
        %>
                <div class="pieza-res card">
                    <a href="/swb/<%=site.getId()%>/detalle?id=<%=reference.getId()%>">
                        <img src="<%=digital.getUrl()%>" />
                    </a>
                    <div>
                        <p class="oswB azul tit"><a href="#"><%=title.getValue()%></a></p>
                        <p class="azul autor"><a href="#"><%=creator%></a></p>
                        <p class="tipo"><%=type%></p>
                    </div>
                </div>
                <%
                position++;
            }
        %>
        </div>
        <jsp:include page="pager.jsp" flush="true"/>
<%
    }
%>