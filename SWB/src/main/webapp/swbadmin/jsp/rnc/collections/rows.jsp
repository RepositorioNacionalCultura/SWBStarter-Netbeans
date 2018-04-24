<%-- 
    Document   : rows
    Created on : 31/01/2018, 04:52:37 PM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, mx.gob.cultura.portal.resources.MyCollections, mx.gob.cultura.portal.response.Collection, java.util.List"%>
<%
    List<Collection> boards = (List<Collection>) request.getAttribute("mycollections");
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    SWBResourceURL uper = paramRequest.getActionUrl();
    SWBResourceURL udel = paramRequest.getActionUrl();
    SWBResourceURL uedt = paramRequest.getActionUrl();
    uper.setAction(MyCollections.ACTION_STA);
    udel.setAction(SWBResourceURL.Action_REMOVE);
    uedt.setMode(SWBResourceURL.Mode_EDIT);
%>
<div id="references">
    <%
        for (Collection c : boards) {
    %>
    <div class="item">
        <div class="card">
            <div class="card-header">
                <%=c.getTitle()%><span class="ion-android-more-vertical"/>
            </div>
            <div class="card-body">
                <p class="card-text"><%=c.getDescription()%></p>
            </div>
            <div class="card-footer">
                <small class="text-muted">
                    <div>
                        <a href="<%=uper%>?id=<%=c.getId()%>"><span class="ion-eye"/>
                            <%
                                if (c.getStatus()) {
                                    out.print("Publica");
                                } else {
                                    out.print("Privada");
                                }
                            %>
                        </a>
                    </div>
                    <div><a class="rojo" href="<%=udel%>?id=<%=c.getId()%>"> eliminar</a></div>
                    <div><a class="rojo" href="<%=uedt%>?id=<%=c.getId()%>"> editar row</a></div>
                </small>
            </div>
        </div>
    </div>
    <%
        }
    %>
</div>