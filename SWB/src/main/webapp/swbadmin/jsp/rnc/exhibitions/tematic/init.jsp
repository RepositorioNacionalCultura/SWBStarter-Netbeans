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
                <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.</p>
            </div>
            <div class="col-8 exhibi-rojo2">
                <img src="/work/models/repositorio/img/rojo-exhibiciones-03.png" class="img-responsive" />
            </div>
        </div>
        <a name="showPage"></a>
        <div id="references">
            <div class="row">
                <% if (null != paramRequest.getUser() && paramRequest.getUser().isSigned()) { %>
                        <div class="col-6 col-md-4">
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