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
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    SWBResourceURL saveURL = paramRequest.getActionUrl();
    saveURL.setMode(SWBResourceURL.Mode_VIEW);
    saveURL.setAction(MyCollections.ACTION_ADD);
    Collection c = (Collection)request.getAttribute("collection");
    if (c == null) {
        c = new Collection("", false, "");
        msg = "No se encontró la colección solicitada";
    }else if (null != c.getId()) {
	msg = "Editar colección";
	saveURL.setAction(SWBResourceURL.Action_EDIT);
        saveURL.setParameter(MyCollections.IDENTIFIER, c.getId().toString());
    }
%>

<div class="modal-dialog">
    <div class="modal-content">
	<div class="modal-header">
            <h4 class="modal-title"><%=msg%></h4>
            <button type="button" class="close" data-dismiss="modal">&times;</button>
	</div>
	<div class="modal-body">
            <p>
		<form id="saveCollForm" action="<%=saveURL.toString()%>" method="post">
                    <div class="col-xs-12 col-sm-12 col-md-10 col-lg-10 car-img2">
			<div class="card-body">
                            <span class="card-title">* Nombre: </span><input type="text" name="title" maxlength="100" size="40" value="<%=c.getTitle()%>"/><div id="dialog-msg-edit"></div>
			</div>
			<div class="card-body">
                            <span class="card-title">Descripción: </span><textarea name="description" rows="4" cols="40" maxlength="500" wrap="hard"><%=c.getDescription()%></textarea>
			</div>
			<div class="card-body">
                            <span class="card-title">Público: </span><input type="checkbox" name="status" <% if (c.getStatus()) out.println(" checked"); %> />
			</div>
                    </div>
		</form>
            </p>
	</div>
        <div class="modal-footer">
            <button type="button" class="btn btn-sm rojo" data-dismiss="modal">Cerrar</button>
            <button type="button" onclick="saveEdit('<%=saveURL.toString()%>');" class="btn btn-sm rojo">Guardar</button>
	</div>
    </div>
</div>