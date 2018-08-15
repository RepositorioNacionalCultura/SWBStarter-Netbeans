<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.response.Entry"%>
<%@page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DigitalObject"%>
<%@page import="mx.gob.cultura.portal.response.Title,org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, java.util.List, java.util.ArrayList"%>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<style>
    a.ion {
        display: none;
    }
    p.tipo:hover + a {
        display: block;
    }
</style>
<%
    String wxss = "";
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    SWBResourceURL pageURL = paramRequest.getRenderUrl().setMode("PAGE");
    pageURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    SWBResourceURL pagesURL = paramRequest.getRenderUrl().setMode("PAGES");
    pagesURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    WebSite site = paramRequest.getWebPage().getWebSite();
    String word = (String) request.getAttribute("word");
    Integer t = (Integer) request.getAttribute("NUM_RECORDS_TOTAL");
    if (null != word) wxss = Utils.suprXSS(word);
    String userLang = paramRequest.getUser().getLanguage();
    String f = null != request.getAttribute("sort") ? "&sort="+request.getAttribute("sort") : "";
    List<Entry> references = null != request.getAttribute("PAGE_LIST") ? (List<Entry>) request.getAttribute("PAGE_LIST") : new ArrayList();
%>
<script type="text/javascript">
    function setList() { doPage(1, 'l', 'relvdes', ''); }
    function setGrid() { doPage(1, 'g', 'relvdes', ''); }
    function doPage(p, m, f, fs) {
        dojo.xhrPost({
            url: '<%=pageURL%>?p='+p+'&m='+m+'&sort='+f+'&word=<%=word%>'+fs,
            load: function(data) {
                dojo.byId('references').innerHTML=data;
		location.href = '#showPage';
            }
        });
    }
</script>
<div class="row resultadosbar">
    <div class="resultadosbar-cont">
        <div class="regresar">
            <button class="btn btn-rojo" onclick="javascript:history.go(-1)">
                <span class="ion-chevron-left"></span><%=paramRequest.getLocaleString("usrmsg_view_search_back")%>
            </button>
        </div>
        <div class="rutatop">
            <p class="">
                <a href="/<%=userLang%>/<%=site.getId()%>/home"><%=paramRequest.getWebPage().getWebSite().getHomePage().getDisplayName(userLang)%></a> / <%=paramRequest.getLocaleString("usrmsg_view_search_collection")%>
            </p>
        </div>
        <div class="ordenar">
            <a href="#" onclick="setGrid();"><i class="fa fa-th select" aria-hidden="true"></i></a>
            <a href="#" onclick="setList();"><i class="fa fa-th-list" aria-hidden="true"></i></a>
        </div>
    </div>
    <div class="resultadosbar-inf"></div>
</div>
<div class="row offcanvascont">
    <div class="offcanvas rojo-bg">
	<span onclick="openNav()" id="offcanvasAbre">
            <em class="fa fa-sliders" aria-hidden="true"></em> <%=paramRequest.getLocaleString("usrmsg_view_search_filters")%> <i class="ion-chevron-right " aria-hidden="true"></i>
        </span>
        <span onclick="closeNav()" id="offcanvasCierra">
            <em class="fa fa-sliders" aria-hidden="true"></em> <%=paramRequest.getLocaleString("usrmsg_view_search_filters")%> <i class="ion-close " aria-hidden="true"></i>
        </span>
    </div>
    <jsp:include page="filters.jsp" flush="true"/>
    <div class="ruta-resultado row">
        <% if (null != wxss) {%>
            <p class="oswL"><%=t%> <%=paramRequest.getLocaleString("usrmsg_view_search_results")%> <%=paramRequest.getLocaleString("usrmsg_view_search_of")%> <span class="oswB rojo"><%=wxss%></span></p>
        <% }else { out.println(paramRequest.getLocaleString("usrmsg_view_search_empty_criteria")); } %>
    </div>
    <div id="contenido">
	<a name="showPage"></a>
	<% if (!references.isEmpty()) {  %>
                <div id="references">
                    <div id="resultados" class="card-columns">
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
                                <a href="/<%=userLang%>/<%=site.getId()%>/detalle?id=<%=reference.getId()%>&word=<%=word%>&r=<%=reference.getPosition()%>&t=<%=t%><%=f%>">
                                    <img src="<%=reference.getResourcethumbnail()%>" />
                                </a>
                                <div>
                                    <p class="tit"><a href="/<%=userLang%>/<%=site.getId()%>/detalle?id=<%=reference.getId()%>&word=<%=word%>&r=<%=reference.getPosition()%>&t=<%=t%><%=f%>"><%=title%></a></p>
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
	<%
            }else{ 
	%>
                <div class="resultados-sin">
                    <p class="oswB rojo">
                    <%  if (null != word) { %>
                            <%=paramRequest.getLocaleString("usrmsg_view_search_no_results")%> <%=word%>
                    <%	}else { out.println(paramRequest.getLocaleString("usrmsg_view_search_empty_word")); }	%>
                    </p>
		</div>
	<%
            }
        %>
    </div>
</div>
<jsp:include page="addtree.jsp" flush="true"/>