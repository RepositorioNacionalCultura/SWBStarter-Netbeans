<%-- 
    Document   : rows
    Created on : 31/01/2018, 04:52:37 PM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, mx.gob.cultura.portal.resources.MyCollections, mx.gob.cultura.portal.response.Collection, java.util.List"%>
<%
    List<Collection> boards = (List<Collection>)request.getAttribute("PAGE_LIST");
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    SWBResourceURL uels = paramRequest.getRenderUrl().setMode(MyCollections.MODE_VIEW_USR);
    uels.setCallMethod(SWBParamRequest.Call_CONTENT);
    WebSite site = paramRequest.getWebPage().getWebSite();
%>

    <div class="row mosaico-contenedor">
        <%
            if (MyCollections.MODE_VIEW_MYALL.equalsIgnoreCase(paramRequest.getMode()) || SWBResourceURL.Mode_VIEW.equalsIgnoreCase(paramRequest.getMode())) {
	%>
                <div class="col-6 col-md-4">
                    <div class="mosaico radius-overflow">
                        <a href="#" data-toggle="modal" data-target="#modalExh">
                            <span class="ion-ios-plus rojo"></span>
                        </a>
                    </div>
                    <div class="mosaico-txt ">
                        <p><span class="ion-locked rojo"></span> Crear colección</p>
                        <p></p>
                    </div>
                </div>
                
	<%
            }
            if (!boards.isEmpty()) {
		for (Collection c : boards) {
	%>
                    <div class="col-6 col-md-4">
        <%          if (c.getCovers().isEmpty()) {	%>
                        <div class="mosaico mosaico1 radius-overflow">
                            <a href="<%=uels%>?id=<%=c.getId()%>">
                                <img src="/work/models/<%=site.getId()%>/img/empty.jpg">
                            </a>
                        </div>
        <%          }else if (c.getCovers().size() < 3) { %>
                        <div class="mosaico mosaico1 radius-overflow">
                            <a href="<%=uels%>?id=<%=c.getId()%>">
                                <img src="<%=c.getCovers().get(0)%>">
                            </a>
                        </div>
        <%          }else { %>
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
	<%          } %>
                    <div class="mosaico-txt ">
			<p>
        <%              if (!c.getStatus()) { %>
                            <span class="ion-locked rojo">
        <%              }else { %>
                            <span class="ion-unlocked rojo">
        <%              } %>
                            </span><%=c.getTitle()%><
                        </p>
			<p>Curada por: <%=c.getUserName()%></p>
                        <!--a href="#"><span class="ion-social-facebook"></span></a-->
                        <!--a href="#"><span class="ion-social-twitter"></span></a-->
                        <% if (null != paramRequest.getUser() && paramRequest.getUser().isSigned() && paramRequest.getUser().getId().equalsIgnoreCase(c.getUserid())) {%>
                            <a href="#" onclick="messageConfirm('¿Está usted seguro de eliminar la colección?', '<%=c.getId()%>');"><span class="ion-trash-a"></span></a>
                            <a href="#" onclick="editByForm('<%=c.getId()%>');"><span class="ion-edit"></span></a>
                        <% } %>
                    </div>
                </div>
	<%
                }
            }
        %>
    </div>
    <jsp:include page="pager.jsp" flush="true"/>