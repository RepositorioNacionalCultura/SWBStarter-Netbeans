<%-- 
    Document   : init
    Created on : 2/07/2018, 11:43:22 AM
    Author     : sergio.tellez
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="org.bson.Document, mx.gob.cultura.portal.utils.Utils, org.semanticwb.model.Role, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, java.util.List"%>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<%
    List<Document> references = (List<Document>) request.getAttribute("FULL_LIST");
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    SWBResourceURL pageURL = paramRequest.getRenderUrl().setMode("PAGE");
    pageURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    boolean haveAccess = null != request.getAttribute("haveAccess") ? (Boolean)request.getAttribute("haveAccess") : false;
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
        <a name="showPage"></a>
        <div id="references">
            <div class="row">
                <% if (haveAccess) { %>
                        <div class="col-6 col-md-4 exhibi-pza">
                            <div class="mosaico radius-overflow">
                                <a href="#" data-toggle="modal" data-target="#modalExh">
                                    <span class="ion-ios-plus rojo"/>
                                </a>
                            </div>
                            <div class="mosaico-txt ">
                                <p><span class="ion-locked rojo"/> <%=paramRequest.getLocaleString("usrmsg_view_create_exh")%></p>
                                <p><%=paramRequest.getLocaleString("usrmsg_view_create_exh_desc")%></p>
                            </div>
                        </div>
                <%
                    }  
                    for (Document exhibition : references) {
                        List<String> posters = Utils.getElements(exhibition, "posters");
                        String portada = !posters.isEmpty() ? posters.get(0) : "";
                        for (String post : posters) {
                            if (post.contains("portada")) {
				portada = post;
				break;
                            }
			}
                %>	
                        <div class="col-6 col-md-4 exhibi-pza">
                            <div class="borde-CCC">
                                <div class="exhibi-pza-img">
                                    <a href="<%=exhibition.getString("url")%>" target="<%=exhibition.getString("target")%>" title="<%=exhibition.getString("title")%>">
                <%
                                if (posters.isEmpty()) {
                %>                            				
                                    <img src="/work/models/<%=paramRequest.getWebPage().getWebSiteId()%>/img/empty.jpg"/>
                <%
                                }else {
                %>
                                    <img src="<%=portada%>"/>
                <%
                                }
                %>
                                    </a>
                                </div>
                                <p class="oswB rojo uppercase"><%=exhibition.getString("title")%></p>
                                <p><%=exhibition.getString("author")%></p>
                                <% if (haveAccess) { %>
                                    <a href="#" onclick="del('<%=exhibition.getString("url")%>')"><span class="ion-trash-a"></span></a>
                                    <a href="<%=exhibition.getString("url")%>?act=vEdit"><span class="ion-edit"></span></a>
                                <% }else { %>
                                    <p><a href="<%=exhibition.getString("url")%>" class="vermas-1topexh uppercase"><%=paramRequest.getLocaleString("usrmsg_view_create_exh_visit")%></a></p>
                                <% } %>
                            </div>
                        </div>
                <%
                    }
                %>
            </div>
        </div>
	<!--jsp:include page="pager.jsp" flush="true"/-->
    </div>
</div>