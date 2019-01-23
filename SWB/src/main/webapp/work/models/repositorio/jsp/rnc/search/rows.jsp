<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DigitalObject,mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Identifier, mx.gob.cultura.portal.response.Title, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.model.WebSite, java.util.List, org.bson.Document"%>
<%
    String wxss = "";
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    WebSite site = paramRequest.getWebPage().getWebSite();
    String mode = "card-columns";
    String userLang = paramRequest.getUser().getLanguage();
    List<Entry> references = (List<Entry>)request.getAttribute("PAGE_LIST");
    if (null != request.getAttribute("mode")) mode = (String)request.getAttribute("mode");
    Integer first = (Integer)request.getAttribute("FIRST_RECORD");
    Integer t = (Integer)request.getAttribute("NUM_RECORDS_TOTAL");
    String word = null != request.getAttribute("word") ? Utils.suprXSS((String)request.getAttribute("word")) : "";
    if (null != request.getAttribute("theme")) wxss = Utils.suprXSS((String)request.getAttribute("theme"));
    else wxss = Utils.suprXSS(word);
    String uri = !word.isEmpty() ? "&word="+word+"&leap="+first : "";
    String fs = null != request.getAttribute("filters") ? "&fs="+request.getAttribute("filters") : "";
    String f = null != request.getAttribute("sort") ? "&sort="+request.getAttribute("sort") : "";
%>
<% if (!references.isEmpty()) {  %>
	<!--div id="references"-->
		<jsp:include page="filters.jsp" flush="true"/>
		<div class="ruta-resultado row" id="ruta-resultado">
                    <% if (null != wxss) { %>
			<p class="oswL"><%=Utils.decimalFormat("###,###", t)%> <%=paramRequest.getLocaleString("usrmsg_view_search_results")%> <%=paramRequest.getLocaleString("usrmsg_view_search_of")%> <span class="oswB rojo"><%=wxss%></span></p>
                    <% }else { out.println(paramRequest.getLocaleString("usrmsg_view_search_empty_criteria")); } %>
		</div>
		<div id="contenido">
                    <div id="resultados" class="<%=mode%>">
                    <%  
			for (Entry reference : references) {    
                            String title =  Utils.getTitle(reference.getRecordtitle(), 50);
                            Document desc = Utils.getDescription(reference.getDescription(), 240);
                            String holder = Utils.getRowHold(reference.getHolder(), 0, false);
                            String creator = Utils.getRowData(reference.getCreator(), 0, false);
                    %>
					<div class="pieza-res card">
						<a class="pieza-res-img" href="/<%=paramRequest.getUser().getLanguage()%>/<%=site.getId()%>/detalle?id=<%=reference.getId()%>&r=<%=reference.getPosition()%>&t=<%=t%><%=fs%><%=f%><%=uri%>">
							<img src="<%=reference.getResourcethumbnail()%>" />
						</a>
						<div class="pieza-res-inf">
							<p class="tit"><a href="/<%=paramRequest.getUser().getLanguage()%>/<%=site.getId()%>/detalle?id=<%=reference.getId()%>&r=<%=reference.getPosition()%>&t=<%=t%><%=fs%><%=f%><%=uri%>"><%=title%></a></p>
							<p class="autor"><a href="#"><%=creator%></a></p>
							<p class="desc"><% if (null != desc) desc.get("short");%></p>
							<p class="palabras">
							<%
								int i = 0;
                                for (String key : reference.getKeywords()) {
									if (i < reference.getKeywords().size()) key += ", ";
                                    out.println("<a href=\"/" + userLang + "/" + site.getId() + "/resultados?word=" + key + "\">" + key + "</a>");
                                    i++;
								}
                            %>
                            </p>
							<div>
								<p class="tipo"><a href="#"><%=holder%></a></p>
								<a href="#" class="pieza-res-like" onclick="loadDoc('/<%=userLang%>/<%=site.getId()%>/favorito?id=', '<%=reference.getId()%>');"><span class="ion-heart"></span></a>
							</div>
						</div>
					</div>
			<%
				}
			%>
			</div>
		<jsp:include page="pager.jsp" flush="true"/>
	<div>
<!--/div-->
<%
	}else if (null != word) { 
%>	
		<div class="ruta-resultado row" id="ruta-resultado">
			<div class="col-12 col-sm-8 col-md-8">
				<p class="oswL rojo"><%=paramRequest.getLocaleString("usrmsg_view_search_no_results")%> <span class="oswB rojo"><%=word%></span></p>
			</div>
			<div class="col-12 col-sm-4 col-md-4 ordenar"></div>
		</div>
		<div id="resultados" class="<%=mode%>"></div>
		<div class="container paginacion"></div>
<%
	}
%>