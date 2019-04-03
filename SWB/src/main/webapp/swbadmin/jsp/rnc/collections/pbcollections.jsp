<%-- 
    Document   : pbcollections
    Created on : 27/06/2018, 15:16:49 PM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, mx.gob.cultura.portal.resources.MyCollections, org.semanticwb.model.WebSite, org.semanticwb.model.WebPage, mx.gob.cultura.portal.response.Collection, java.util.List, java.util.ArrayList"%>
<%
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    SWBResourceURL pageURL = paramRequest.getRenderUrl().setMode("PAGE");
    pageURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    WebSite site = paramRequest.getWebPage().getWebSite();
    WebPage wpdetail = site.getWebPage("Detalle_coleccion");
    String uels = wpdetail.getUrl();
    List<Collection> boards = null != request.getAttribute("PAGE_LIST") ? (List<Collection>)request.getAttribute("PAGE_LIST") : new ArrayList();
%>
<script type="text/javascript">
    function doPage(p,t) {
        dojo.xhrPost({
            url: '<%=pageURL%>?p='+p+'&ct='+t,
            load: function(data) {
                dojo.byId('references').innerHTML=data;
                location.href = '#showPage';
            }
        });
    }
</script>
<a name="showPage"></a>
<div class="container usrTit">
    <div class="row">
        <div>
            <h2 class="oswM nombre"><%=paramRequest.getLocaleString("usrmsg_view_collections")%></h2>
        </div>
    </div>
</div>

<div class="container">
    <div id="references">
        <div class="row mosaico-contenedor">
            <%
                if (!boards.isEmpty()) {
                    for (Collection c : boards) {
                        String username = null != c.getUserName() && !c.getUserName().trim().isEmpty() ? c.getUserName() : paramRequest.getLocaleString("usrmsg_view_collections_anonymous");
            %>
                        <div class="col-6 col-md-4">
			<% if (c.getCovers().isEmpty()) {%>
                            <div class="mosaico mosaico1 radius-overflow">
                                <a href="<%=uels%>?id=<%=c.getId()%>">
                                    <img src="/work/models/<%=site.getId()%>/img/empty.jpg">
				</a>
                            </div>
			<% } else if (c.getCovers().size() < 3) {%>
                            <div class="mosaico mosaico1 radius-overflow">
                                <a href="<%=uels%>?id=<%=c.getId()%>">
                                    <img src="<%=c.getCovers().get(0)%>">
				</a>
                            </div>
			<% } else {%>
                            <div class="mosaico mosaico3 radius-overflow">
                                <a href="<%=uels%>?id=<%=c.getId()%>">
                                    <div class="mosaico3a">
                                        <img src="<%=c.getCovers().get(0)%>">
                                    </div>
                                    <div class="mosaico3b">
                                        <div>
                                            <img src="<%=c.getCovers().get(1)%>">
					</div>
					<div>
                                            <img src="<%=c.getCovers().get(2)%>">
					</div>
                                    </div>
				</a>
                            </div>
			<% } %>
                        <div class="mosaico-txt ">
                            <p><%=c.getTitle()%></p>
                            <p><%=paramRequest.getLocaleString("usrmsg_view_collections_curate_by")%>: <%=username%></p>
                        </div>
                    </div>
            <%
                    }
            %>
            <%
                }
            %>
        </div>
        <jsp:include page="pager.jsp" flush="true"/>
    </div>
</div>