<%-- 
    Document   : rows
    Created on : 31/01/2018, 04:52:37 PM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="org.bson.Document, mx.gob.cultura.portal.utils.Utils, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, java.util.List"%>
<%
    List<Document> references = (List<Document>) request.getAttribute("exhibitions");
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
%>
<div id="references">
    <div class="row">
        <%  if (null != paramRequest.getUser() && paramRequest.getUser().isSigned()) { %>
                <div class="col-6 col-md-4">
                    <div class="mosaico radius-overflow">
                        <a href="#" data-toggle="modal" data-target="#modalExh">
                            <span class="ion-ios-plus rojo"/>
			</a>
                    </div>
                    <div class="mosaico-txt ">
                        <p><span class="ion-locked rojo"/> <%=paramRequest.getLocaleString("usrmsg_view_create_exh")%></p>
			<p>%=paramRequest.getLocaleString("usrmsg_view_create_exh_desc")%></p>
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
				<img src="/work/models/<%=paramRequest.getWebPage().getWebSiteId()%>/img/empty.jpg"/>
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