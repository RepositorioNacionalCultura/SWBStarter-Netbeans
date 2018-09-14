<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.response.Title, mx.gob.cultura.portal.response.Entry"%>
<%@page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DigitalObject"%>
<%@page import="org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, java.util.List, java.util.ArrayList, org.bson.Document"%>
<script type="text/javascript" src="/swbadmin/js/rnc/detail.js"></script>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<%
    String th = "";
    String wxss = "";
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    SWBResourceURL pageURL = paramRequest.getRenderUrl().setMode("PAGE");
    pageURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    SWBResourceURL pagesURL = paramRequest.getRenderUrl().setMode("PAGES");
    pagesURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    WebSite site = paramRequest.getWebPage().getWebSite();
    String word = (String) request.getAttribute("word");
    Integer t = (Integer) request.getAttribute("NUM_RECORDS_TOTAL");
    if (null != request.getAttribute("theme")) {
	th = (String)request.getAttribute("theme");
	wxss = Utils.suprXSS(th);
        th = "&theme="+th;
    }else if (null != word) wxss = Utils.suprXSS(word);
    String userLang = paramRequest.getUser().getLanguage();
    String f = null != request.getAttribute("sort") ? "&sort="+request.getAttribute("sort") : "";
    List<Entry> references = null != request.getAttribute("PAGE_LIST") ? (List<Entry>) request.getAttribute("PAGE_LIST") : new ArrayList();
%>
<script type="text/javascript">
    function setList() { doPage(1, 'l', 'relvdes', ''); }
    function setGrid() { doPage(1, 'g', 'relvdes', ''); }
    function doPage(p, m, f, fs) {
        dojo.xhrPost({
            url: '<%=pageURL%>?p='+p+'&m='+m+'&sort='+f+'&word=<%=word%>'+fs+'<%=th%>',
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
            <i class="fa fa-th select" aria-hidden="true" id="vistaMosaico" onclick="vistaMosaico();"></i>
            <i class="fa fa-th-list " aria-hidden="true" id="vistaLista" onclick="vistaLista();"></i>
        </div>
    </div>
    <div class="resultadosbar-inf"></div>
</div>
<div class="row offcanvascont">
    <div class="offcanvas rojo-bg" id="offcanvas">
	<span onclick="openNav()" id="offcanvasAbre">
            <em class="fa fa-sliders" aria-hidden="true"></em> <%=paramRequest.getLocaleString("usrmsg_view_search_filters")%> <i class="ion-chevron-right " aria-hidden="true"></i>
        </span>
        <span onclick="closeNav()" id="offcanvasCierra">
            <em class="fa fa-sliders" aria-hidden="true"></em> <%=paramRequest.getLocaleString("usrmsg_view_search_filters")%> <i class="ion-close " aria-hidden="true"></i>
        </span>
    </div>
    <jsp:include page="filters.jsp" flush="true"/>
    <a name="showPage"></a>
    <div id="references">
        <div class="ruta-resultado row" id="ruta-resultado">
            <% if (null != wxss && !wxss.isEmpty()) {%>
                <p class="oswL"><%=Utils.decimalFormat("###,###", t)%> <%=paramRequest.getLocaleString("usrmsg_view_search_results")%> <%=paramRequest.getLocaleString("usrmsg_view_search_of")%> <span class="oswB rojo"><%=wxss%></span></p>
            <% } %>
        </div>
        <div id="contenido">
            <% if (!references.isEmpty()) {  %>
                <div id="resultados" class="card-columns">
                    <%
                        for (Entry reference : references) {
                            String title =  Utils.getTitle(reference.getRecordtitle(), 50);
                            Document desc = Utils.getDescription(reference.getDescription());
                            String holder = Utils.getRowData(reference.getHolder(), 0, false);
                            String creator = Utils.getRowData(reference.getCreator(), 0, false);
                    %>
                            <div class="pieza-res card">
                                <a class="pieza-res-img" href="/<%=userLang%>/<%=site.getId()%>/detalle?id=<%=reference.getId()%>&word=<%=word%>&r=<%=reference.getPosition()%>&t=<%=t%><%=f%>">
                                    <img src="<%=reference.getResourcethumbnail()%>" />
                                </a>
                                <div class="pieza-res-inf">
                                    <p class="tit"><a href="/<%=userLang%>/<%=site.getId()%>/detalle?id=<%=reference.getId()%>&word=<%=word%>&r=<%=reference.getPosition()%>&t=<%=t%><%=f%>"><%=title%></a></p>
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
                <jsp:include page="footer.jsp" flush="true"/>
        
	<%
            }else{ 
	%>
                <div class="resultados-sin">
                    <p class="oswB rojo">
                    <%  if (null != word) { %>
                            <%=paramRequest.getLocaleString("usrmsg_view_search_no_results")%> <%=wxss%>
                    <%	}else { out.println(paramRequest.getLocaleString("usrmsg_view_search_empty_word")); }	%>
                    </p>
		</div>
	<%
            }
        %>
        </div>
    </div>
</div>
<jsp:include page="addtree.jsp" flush="true"/>