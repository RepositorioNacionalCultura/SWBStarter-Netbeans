<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DigitalObject, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Identifier, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL"%>
<%@ page import="org.semanticwb.model.WebSite, java.util.List, org.semanticwb.model.Resource" %>
<%
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    
    SWBResourceURL pageURL = paramRequest.getRenderUrl().setMode("PAGE");
    pageURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    
    WebSite site = paramRequest.getWebPage().getWebSite();
    String userLang = paramRequest.getUser().getLanguage();
    List<Entry> references = (List<Entry>) request.getAttribute("references");
    String word = null != request.getAttribute("criteria") ? (String)request.getAttribute("criteria") : "";

    Integer last = (Integer) request.getAttribute("LAST_RECORD");
    Integer first = (Integer) request.getAttribute("FIRST_RECORD");
    Integer total = (Integer) request.getAttribute("NUM_RECORDS_TOTAL");
%>
<script type="text/javascript">
    function doPage(w, p, m, f) {
        var xhttp = new XMLHttpRequest();
        var url = '<%=pageURL%>?p=' + p + '&m=' + m + '&sort=' + f + '&criteria=' + w;
        xhttp.onreadystatechange = function () {
            if (this.readyState == 4 && this.status == 200) {
                document.getElementById("references").innerHTML = this.responseText;
                location.href = '#showPage';
            }
        };
        xhttp.open("POST", url, true);
        xhttp.send();
    }
</script>
<!--div class="row resultadosbar">
        <div class="col-md-3"><a class=" oswL" href="javascript:history.go(-1)"><i aria-hidden="true" class="fa fa-long-arrow-left"></i> Regresar</a></div>
        <div class="col-md-9">
                <p class="oswL">
<% if (!word.isEmpty()) {%>
<%=word%>
<% } else {
        out.println("Debe proporcionar un criterio de búsqueda");
    } %>
</p>
</div>
</div-->
<div class="row offcanvascont">
    <div id="contenido">
        <a name="showPage"></a>
        <% if (!references.isEmpty()) {%>
        <div id="references">
            <!--div class="ruta row">
                <div class="col-12 col-sm-8 col-md-8">
                    <p class="oswLc"><%=first%>-<%=last%> de <%=total%> resultados</p>
                </div>
                <div class="col-12 col-sm-4 col-md-4 ordenar">
                        <a href="#" onclick="setGrid();"><i class="fa fa-th select" aria-hidden="true"></i></a>
                        <a href="#" onclick="setList();"><i class="fa fa-th-list" aria-hidden="true"></i></a>
                </div>
            </div-->
                <div id="resultados" class="card-columns">
                    <%
                        for (Entry reference : references) {
                            reference.setPosition(0);
                            String title =  Utils.getTitle(reference.getRecordtitle(), 50);
                            String creator = Utils.getRowData(reference.getCreator(), 0, false);
                            String holder = Utils.getRowHold(reference.getHolder(), 0, false);
                    %>
                            <div class="pieza-res card">
                                <a class="pieza-res-img" href="/<%=userLang%>/<%=site.getId()%>/detalle?id=<%=reference.getId()%>&n=<%=reference.getPosition()%>">
                                    <img src="<%=reference.getResourcethumbnail()%>" />
                                </a>
                                <div class="pieza-res-inf">
                                    <p class="tit"><a href="#"><%=title%></a></p>
                                    <p class="autor"><a href="#"><%=creator%></a></p>
                                    <p class="tipo"><%=holder%></p>
                                </div>
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
        </div>