<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.response.Title, mx.gob.cultura.portal.resources.SearchCulturalProperty, mx.gob.cultura.portal.response.Entry"%>
<%@page import="org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest,org.semanticwb.portal.api.SWBResourceURL,java.util.ArrayList,java.util.List"%>
<%
    List<Title> titles = new ArrayList<>();
    List<Entry> c = (List<Entry>) request.getAttribute("collection");
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    WebSite site = paramRequest.getWebPage().getWebSite();
%>
<div class="col-12 col-sm-6  col-md-3 col-lg-3 order-md-1 order-sm-2 order-2 mascoleccion">
    <div>
        <p class="tit2"><%=paramRequest.getLocaleString("usrmsg_view_detail_more_collection")%></p>
        <% if (!c.isEmpty()) {
                for (Entry entry : c) {
                    String title = "";
                    titles = entry.getRecordtitle();
                    if (!titles.isEmpty()) title = titles.get(0).getValue();
                    SearchCulturalProperty.setThumbnail(entry, site, 0);
        %>
                    <div>
                        <img src=<%=entry.getResourcethumbnail()%> class="img-responsive">
                        <p><%=paramRequest.getLocaleString("usrmsg_view_detail_name_work")%></p>
                        <p><%=title%></p>
                    </div>
        <%
                }
            }
        %>
        <hr>
        <p class="vermas"><a href="#"><%=paramRequest.getLocaleString("usrmsg_view_detail_show_more")%> <span class="ion-plus-circled"></span></a></p>
    </div>
</div>