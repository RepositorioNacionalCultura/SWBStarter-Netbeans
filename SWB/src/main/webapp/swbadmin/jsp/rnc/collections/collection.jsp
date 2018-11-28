<%-- 
    Document   : collecction
    Created on : 24/01/2018, 10:16:19 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, mx.gob.cultura.portal.resources.MyCollections, org.semanticwb.model.WebSite, mx.gob.cultura.portal.response.Collection, java.util.List"%>

<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<%
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    SWBResourceURL saveURL = paramRequest.getActionUrl();
    saveURL.setMode(SWBResourceURL.Mode_VIEW);
    saveURL.setAction(MyCollections.ACTION_ADD);
    Collection c = (Collection) request.getAttribute("collection");
    String msg = paramRequest.getLocaleString("usrmsg_view_collections_add");
    if (c == null) {
        c = new Collection("", false, "");
        msg = paramRequest.getLocaleString("usrmsg_view_collections_not_exist");
    } else if (null != c.getId()) {
        msg = paramRequest.getLocaleString("usrmsg_view_collections_edit");
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
                    <h4 class="modal-title oswM rojo"><%=paramRequest.getLocaleString("usrmsg_view_collections_edit").toUpperCase()%></h4>
                    <button type="button" class="close" data-dismiss="modal">
                        <span class="ion-ios-close-outline"></span>
                    </button>
                </div>
                <div class="modal-body">
                    <form id="saveCollForm" action="<%=saveURL.toString()%>" method="post">
                        <div class="form-group">
                            <label for="crearNombre"><%=paramRequest.getLocaleString("usrmsg_view_collections_name")%></label>
                            <input type="text" name="title" maxlength="100" value="<%=c.getTitle()%>" id="crearNombre" class="form-control" placeholder="60" aria-label="Recipient's username" aria-describedby="basic-addon2"/><div id="dialog-msg-edit"></div>
                            <label for="crearDescr"><%=paramRequest.getLocaleString("usrmsg_view_collections_desc_opt")%></label>
                            <textarea name="description" id="crearDescr" placeholder="250"><%=c.getDescription()%></textarea>        
                            <label for="selprivado" class="selPrivado">
                                <input name="status" value="<% out.println(c.getStatus()); %>" id="selprivado" type="checkbox" checked aria-label="Checkbox for following text input"/>
                                <% if (!c.getStatus()) { %><span class="ion-locked"><%=paramRequest.getLocaleString("usrmsg_view_collections_private")%><% }else { %><span class="ion-unlocked"><%=paramRequest.getLocaleString("usrmsg_view_collections_public")%><% } %></span>
                            </label>
                        </div>
                        <button type="button" onclick="saveEdit('<%=saveURL.toString()%>');" class="btn-cultura btn-rojo btn-mayus"><%=paramRequest.getLocaleString("usrmsg_view_collections_save")%></button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>