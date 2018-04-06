<%-- 
    Document   : collecction
    Created on : 24/01/2018, 10:16:19 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, mx.gob.cultura.portal.resources.MyCollections, org.semanticwb.model.WebSite, mx.gob.cultura.portal.response.Collection, java.util.List"%>

<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<%
    String msg = "Agregar colección";
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    SWBResourceURL saveURL = paramRequest.getActionUrl();
    saveURL.setMode(SWBResourceURL.Mode_VIEW);
    saveURL.setAction(MyCollections.ACTION_ADD);
    Collection c = (Collection) request.getAttribute("collection");
    if (c == null) {
        c = new Collection("", false, "");
        msg = "No se encontró la colección solicitada";
    } else if (null != c.getId()) {
        msg = "Editar colección";
        saveURL.setAction(SWBResourceURL.Action_EDIT);
        saveURL.setParameter(MyCollections.IDENTIFIER, c.getId().toString());
    }
%>

<div class="modal-dialog modal-exh modal-2col" role="document">
    <div class="modal-content">
        <div class="row">
            <div class="col-4 col-sm-5 modal-col1">
                <div class="modal-izq">
                    <img src="/work/models/repositorio/img/cabecera-colaborar.jpg">    
                </div>
            </div>
            <div class="col-8 col-sm-7 modal-col2">
                <div class="modal-header">
                    <h4 class="modal-title oswM rojo">EDITAR COLECCIÓN</h4>
                    <button type="button" class="close" data-dismiss="modal">
                        <span class="ion-ios-close-outline"></span>
                    </button>
                </div>
                <div class="modal-body">
                    <form id="saveCollForm" action="<%=saveURL.toString()%>" method="post">
                        <div class="form-group">
                            <label for="crearNombre">Nombre</label>
                            <input type="text" name="title" maxlength="100" value="<%=c.getTitle()%>" id="crearNombre" class="form-control" placeholder="60" aria-label="Recipient's username" aria-describedby="basic-addon2"/><div id="dialog-msg-edit"></div>
                            <label for="crearDescr">Descripción (opcional)</label>
                            <textarea name="description" id="crearDescr" placeholder="250"><%=c.getDescription()%></textarea>        
                            <label for="selprivado" class="selPrivado">
                                <input name="status" <% if (c.getStatus()) { out.println(" checked"); }%> id="selprivado" type="checkbox" aria-label="Checkbox for following text input"/>
                                <% if (!c.getStatus()) { %><span class="ion-locked">Privado<% }else { %><span class="ion-unlocked">Público<% } %></span>
                            </label>
                        </div>
                        <button type="button" onclick="saveEdit('<%=saveURL.toString()%>');" class="btn-cultura btn-rojo btn-mayus">Editar colección</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>