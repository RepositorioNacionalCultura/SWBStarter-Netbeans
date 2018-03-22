<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.response.DigitalObject, mx.gob.cultura.portal.response.ArtWork, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Identifier, mx.gob.cultura.portal.response.Title, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.model.Resource"%>
<%@ page import="java.util.List" %>
<%@ page import="org.semanticwb.model.WebSite" %>
<%
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    WebSite site = paramRequest.getWebPage().getWebSite();
	Resource base = (Resource)request.getAttribute("base");
    String mode = "row list";
    List<ArtWork> references = (List<ArtWork>)request.getAttribute("PAGE_LIST");
    if (null != request.getAttribute("mode"))
        mode = (String)request.getAttribute("mode");
%>
<% if (!references.isEmpty()) {  %>
<div id="references">
    <div id="recientes" class="<%=mode%>">
        <%  for (ArtWork art : references) {
			Entry reference = art.getEntry();
            Identifier identifier = new Identifier();
            DigitalObject digital = new DigitalObject();
            List<Title> titles = reference.getRecordtitle();
            List<String> creators = reference.getCreator();
            List<DigitalObject> digitalobject = reference.getDigitalobject();
            if (!digitalobject.isEmpty()) digital = digitalobject.get(0);
            String creator = creators.size() > 0 ? creators.get(0) : "";
        %>
        <div class="pieza">
            <div>
                <a href="/swb/<%=site.getId()%>/detalle?id=<%=reference.getId()%>">
                    <img src="<%=digital.getUrl()%>" />
                </a>
            </div>
            <p class="oswB azul tit"><a href="#"><%=titles.get(0).getValue()%></a></p>
            <p class="azul autor"><a href="#"><%=creator%></a></p>
			<p class="azul autor"><input type="checkbox" name="hiddenarts" value="<%=reference.getId()%>" <% if (art.isHidden()) out.print("checked"); %>>Ocultar
							<br/><input type="checkbox" name="favarts" value="<%=reference.getId()%>" <% if (art.isFavorite()) out.print("checked"); %>>Favorito</p>
        </div>
        <%
            }
        %>
    </div>
    <jsp:include page="admExPager.jsp" flush="true"/>
</div>
<%
}
%>