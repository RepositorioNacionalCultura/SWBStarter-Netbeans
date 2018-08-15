<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DigitalObject,mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Identifier, mx.gob.cultura.portal.response.Title, org.semanticwb.portal.api.SWBParamRequest"%>
<%@ page import="java.util.List" %>
<%@ page import="org.semanticwb.model.WebSite" %>
<style>
    a.ion {
        display: none;
    }
    p.tipo:hover + a {
        display: block;
    }
</style>
<%
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    WebSite site = paramRequest.getWebPage().getWebSite();
    String mode = "card-columns";
    String userLang = paramRequest.getUser().getLanguage();
    List<Entry> references = (List<Entry>)request.getAttribute("PAGE_LIST");
    if (null != request.getAttribute("mode")) mode = (String)request.getAttribute("mode");
    Integer first = (Integer)request.getAttribute("FIRST_RECORD");
    String word = null != request.getAttribute("word") ? Utils.suprXSS((String)request.getAttribute("word")) : "";
    String uri = !word.isEmpty() ? "&word="+word+"&leap="+first : "";
    String fs = null != request.getAttribute("filters") ? "&fs="+request.getAttribute("filters") : "";
    String f = null != request.getAttribute("sort") ? "&sort="+request.getAttribute("sort") : "";
    String wxss = Utils.suprXSS(word);
%>
<% if (!references.isEmpty()) {  %>
    <div id="references">
        <div class="ruta-resultado row">
            <% if (null != wxss) { %>
		<p class="oswL"><%=t%> <%=paramRequest.getLocaleString("usrmsg_view_search_results")%> <%=paramRequest.getLocaleString("usrmsg_view_search_of")%> <span class="oswB rojo"><%=wxss%></span></p>
            <% }else { out.println(paramRequest.getLocaleString("usrmsg_view_search_empty_criteria")); } %>
	</div>
        <div id="contenido">
            <div id="resultados" class="<%=mode%>">
            <%  
                for (Entry reference : references) {
                    String holder = "";
                    List<String> holders = reference.getHolder();
                    List<String> creators = reference.getCreator();
                    String title =  Utils.getTitle(reference.getRecordtitle(), 50);
                    holder = null != holders && holders.size() > 0 ? holders.get(0) : "";
                    String creator = creators.size() > 0 && null != creators.get(0) ? creators.get(0) : "";
            %>
                    <div class="pieza-res card">
                        <a href="/<%=paramRequest.getUser().getLanguage()%>/<%=site.getId()%>/detalle?id=<%=reference.getId()%>&r=<%=reference.getPosition()%><%=fs%><%=f%><%=uri%>">
                            <img src="<%=reference.getResourcethumbnail()%>" />
                        </a>
                        <div>
                            <p class="tit"><a href="/<%=paramRequest.getUser().getLanguage()%>/<%=site.getId()%>/detalle?id=<%=reference.getId()%>&r=<%=reference.getPosition()%><%=fs%><%=f%><%=uri%>"><%=title%></a></p>
                            <p class="autor"><a href="#"><%=creator%></a></p>
                            <p class="tipo"><i><%=holder%></i></p>
                            <a class="ion" href="#" onclick="loadDoc('/<%=userLang%>/<%=site.getId()%>/favorito?id=', '<%=reference.getId()%>');"><span class="ion-heart"></span></a>
                        </div>
                    </div>
            <%
                }
            %>
            </div>
            <jsp:include page="pager.jsp" flush="true"/>
            <jsp:include page="footer.jsp" flush="true"/>
        </div>
    </div>
<%
    }else if (null != word) { 
%>	
	<div id="references">
            <div class="ruta-resultado row">
                <div class="col-12 col-sm-8 col-md-8">
                    <p class="oswL rojo"><%=paramRequest.getLocaleString("usrmsg_view_search_no_results")%> <span class="oswB rojo"><%=word%></span></p>
		</div>
                <div class="col-12 col-sm-4 col-md-4 ordenar"></div>
            </div>
            <div id="resultados" class="<%=mode%>"></div>
            <div class="container paginacion"></div>
	</div>
<%
    }
%>