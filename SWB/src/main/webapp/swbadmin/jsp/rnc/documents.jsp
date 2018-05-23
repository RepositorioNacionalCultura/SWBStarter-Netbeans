<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.response.Entry"%>
<%@page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DigitalObject"%>
<%@page import="mx.gob.cultura.portal.response.Title,org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, java.util.List, java.util.ArrayList"%>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<%
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    SWBResourceURL pageURL = paramRequest.getRenderUrl().setMode("PAGE");
    pageURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    SWBResourceURL pagesURL = paramRequest.getRenderUrl().setMode("PAGES");
    pagesURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    WebSite site = paramRequest.getWebPage().getWebSite();
    String word = (String)request.getAttribute("word");
    Integer last = (Integer)session.getAttribute("LAST_RECORD");
    Integer first = (Integer)session.getAttribute("FIRST_RECORD");
    Integer total = (Integer)session.getAttribute("NUM_RECORDS_TOTAL");
    if (null != word) word = Utils.suprXSS(word);
    String userLang = paramRequest.getUser().getLanguage();
    List<Entry> references = null != session.getAttribute("PAGE_LIST") ? (List<Entry>)session.getAttribute("PAGE_LIST") : new ArrayList<>();
%>
<script type="text/javascript">
    function setList() { doPage(1, 'l', 'relvdes'); }
    function setGrid() { doPage(1, 'g', 'relvdes'); }
    function doPage(p, m, f) {
        dojo.xhrPost({
            url: '<%=pageURL%>?p='+p+'&m='+m+'&sort='+f+'&word=<%=word%>',
            load: function(data) {
                dojo.byId('references').innerHTML=data;
                    location.href = '#showPage';
            }
        });
    }
</script>
<div class="row resultadosbar">
    <div class="col-md-3"><a class=" oswL" href="javascript:history.go(-1)"><i aria-hidden="true" class="fa fa-long-arrow-left"></i> <%=paramRequest.getLocaleString("usrmsg_view_search_back")%></a></div>
    <div class="col-md-9">
        <p class=" oswL">
            <% if (null != word) { %>
                    <%=paramRequest.getLocaleString("usrmsg_view_search_collection")%> / <%=paramRequest.getLocaleString("usrmsg_view_search_results_of")%>: <%=word%>
            <% }else { out.println(paramRequest.getLocaleString("usrmsg_view_search_empty_criteria")); } %>
	</p>
    </div>
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
    <div id="contenido">
	<a name="showPage"></a>
	<% if (!references.isEmpty()) {  %>
                <div id="references">
                    <div class="ruta row">
			<div class="col-12 col-sm-8 col-md-8">
                            <p class="oswLc"><%=first%>-<%=last%> <%=paramRequest.getLocaleString("usrmsg_view_search_of")%> <%=total%> <%=paramRequest.getLocaleString("usrmsg_view_search_results")%></p>
			</div>
                        <div class="col-12 col-sm-4 col-md-4 ordenar">
                            <a href="#" onclick="setGrid();"><i class="fa fa-th select" aria-hidden="true"></i></a>
                            <a href="#" onclick="setList();"><i class="fa fa-th-list" aria-hidden="true"></i></a>
			</div>
                    </div>
                    <div id="resultados" class="card-columns">
                    <%
                        int position = first;
                        for (Entry reference : references) {
                            Title title = new Title();
                            reference.setPosition(position);
                            DigitalObject digital = new DigitalObject();
                            List<String> creators = reference.getCreator();
                            List<Title> titles = reference.getRecordtitle();
                            List<String> resourcetype = reference.getResourcetype();
                            List<DigitalObject> digitalobject = reference.getDigitalObject();
                            if (!titles.isEmpty()) title = titles.get(0);
                            String creator = creators.size() > 0 ? creators.get(0) : "";
                            String type = resourcetype.size() > 0 ? resourcetype.get(0) : "";
                            if (!digitalobject.isEmpty()) digital = digitalobject.get(0);
                            //String titleValue =  (null != title.getValue() && title.getValue().size() > 0) ? title.getValue().get(0) : "";
                    %>
                            <div class="pieza-res card">
                                <a href="/<%=userLang%>/<%=site.getId()%>/detalle?id=<%=reference.getId()%>&n=<%=reference.getPosition()%>">
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
		</div>
	<%
            }else if (null != word) { out.println(paramRequest.getLocaleString("usrmsg_view_search_no_results") + word); }
        %>
    </div>
</div>