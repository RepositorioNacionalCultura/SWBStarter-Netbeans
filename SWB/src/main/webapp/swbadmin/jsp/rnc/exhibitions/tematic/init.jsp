<%-- 
    Document   : init
    Created on : 2/07/2018, 11:43:22 AM
    Author     : sergio.tellez
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="org.bson.Document, mx.gob.cultura.portal.utils.Utils, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, java.util.List"%>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<%
    List<Document> references = (List<Document>) request.getAttribute("PAGE_LIST");
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    SWBResourceURL pageURL = paramRequest.getRenderUrl();
    pageURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    pageURL.setAction("PAGE");
%>
<script>
    function doPage(p) {
        dojo.xhrPost({
            url: '<%=pageURL%>?p='+p,
            load: function(data) {
                dojo.byId('references').innerHTML=data;
                location.href = '#showPage';
            }
        });
    }
</script>
    <div class="content container exhibiciones-main">
	<div class="row">
            <div class="col-4 exhibi-rojo1">
                <img src="/work/models/repositorio/img/rojo-exhibiciones-01.png" class="img-responsive" />
            </div>
            <div class="col-8">
                <p>¡Conoce las exposiciones que curadores especializados han creado para ti!</p>
                <p>Estas exposiciones te permitirán conocer los fondos de Memoria Digital Mexicana  dentro de un panorama más amplio de contenidos digitales, múltiples y especializados que se interrelacionan uno a uno.</p>
                <p>Con las exposiciones se pretende extender el conocimiento de todo el contenido arqueológico, histórico, antropológico, artístico, audiovisual y sonoro que se puede explorar dentro de esta plataforma, fomentando así diversos procesos creativos que permitan la interacción con los objetos digitales a partir de una selección específica.</p>
            </div>
            <div class="col-8 exhibi-rojo2">
                <img src="/work/models/repositorio/img/rojo-exhibiciones-03.png" class="img-responsive" />
            </div>
        </div>
        <a name="showPage"></a>
        <div id="references">
            <div class="row">
                <% if (null != paramRequest.getUser() && paramRequest.getUser().isSigned()) { %>
                        <div class="col-6 col-md-4 exhibi-pza">
                            <div class="mosaico radius-overflow">
                                <a href="#" data-toggle="modal" data-target="#modalExh">
                                    <span class="ion-ios-plus rojo"/>
                                </a>
                            </div>
                            <div class="mosaico-txt ">
                                <p><span class="ion-locked rojo"/> Crear exhibición</p>
                                <p>Dentro de las exhibiciones se encuentran las colecciones de información altamente especializadas</p>
                            </div>
                        </div>
                <%
                    }  
                    for (Document exhibition : references) {
                        List<String> posters = Utils.getElements(exhibition, "posters");
                %>	
                        <div class="col-6 col-md-4 exhibi-pza">
                            <div class="borde-CCC">
                                <div class="exhibi-pza-img">
                                    <a href="<%=exhibition.getString("url")%>" target="<%=exhibition.getString("target")%>" title="<%=exhibition.getString("title")%>">
                <%
                                if (posters.isEmpty()) {
                %>                            				
                                        <img src="/work/models/repositorio/img/empty.jpg"/>
                <%
                                }else {
                %>
                                        <img src="<%=posters.get(0)%>"/>
                <%
                                }
                %>
                                    </a>
                                </div>
                                <p class="oswB rojo uppercase"><%=exhibition.getString("title")%></p>
                                <p><%=exhibition.getString("author")%></p>
                                <%  if (null != paramRequest.getUser() && paramRequest.getUser().isSigned()) { %>
                                        <a href="#" onclick="del('<%=exhibition.getString("url")%>')">Eliminar</a>
                                        <a href="<%=exhibition.getString("url")%>?act=vEdit"><span class="ion-edit"></span></a>
                                <% }else { %>
                                        <p><a href="<%=exhibition.getString("url")%>" class="vermas-1topexh uppercase">Visitar exhibición</a></p>
                                <% } %>
                            </div>
                        </div>
                <%
                    }
                %>
            </div>
        </div>
	<jsp:include page="pager.jsp" flush="true"/>
    </div>
</div>