<%-- 
    Document   : index
    Created on : 13/05/2018, 03:03:03 PM
    Author     : sergio.tellez
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="org.bson.Document, mx.gob.cultura.portal.utils.Utils, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, org.semanticwb.portal.api.SWBResourceModes, java.util.List"%>
<%
	List<Document> references = (List<Document>) request.getAttribute("exhibitions");
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
%>
<a name="showPage"></a>
<div id="references">
    <div class="container">          
        <div class="row mosaico-contenedor">
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
					<div class="col-6 col-md-4">
			<%
                        if (posters.isEmpty()) {
			%>
                            <div class="mosaico mosaico1 radius-overflow">
                                <a href="<%=exhibition.getString("url")%>" target="<%=exhibition.getString("target")%>" title="<%=exhibition.getString("title")%>">
                                    <img src="/work/models/repositorio/img/empty.jpg"/>
                                </a>
                            </div>
                <%
                        }else if (posters.size() == 1 || posters.size() == 2) {
                %>
                            <div class="mosaico mosaico1 radius-overflow">
                                <a href="<%=exhibition.getString("url")%>" target="<%=exhibition.getString("target")%>" title="<%=exhibition.getString("title")%>">
                                    <img src="<%=posters.get(0)%>"/>
                                </a>
                            </div>
                <%
                        }else if (posters.size() == 3) {
                %>
                            <div class="mosaico mosaico3 radius-overflow">
                                <a href="<%=exhibition.getString("url")%>" target="<%=exhibition.getString("target")%>" title="<%=exhibition.getString("title")%>">
                                    <div class="mosaico3a">
                                        <img src="<%=posters.get(0)%>"/>
                                    </div>
                                    <div class="mosaico3b">
                                        <div>
                                            <img src="<%=posters.get(1)%>"/>
                                        </div>
                                        <div>
                                            <img src="<%=posters.get(2)%>"/>
                                        </div>
                                    </div>
                                </a>
                            </div>
                <%
                        }
                %>
                        <div class="mosaico-txt">
                            <p><%=exhibition.getString("title")%></p>
                            <a href="#"><span class="ion-social-facebook"/></a>
                            <a href="#"><span class="ion-social-twitter"/></a>
							<% if (null != paramRequest.getUser() && paramRequest.getUser().isSigned()) { %>
							       <a href="#" onclick="del('<%=exhibition.getString("url")%>')">Eliminar</a>
								   <a href="<%=exhibition.getString("url")%>?act=vEdit"><span class="ion-edit"></span></a>
							<% } %>
                        </div>
            </div>
            <%
                }
            %>
        </div>
    </div>
</div>