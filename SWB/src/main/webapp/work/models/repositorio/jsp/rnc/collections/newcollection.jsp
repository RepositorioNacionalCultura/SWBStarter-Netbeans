<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.resources.FavoritesMgr, org.semanticwb.portal.api.SWBResourceURL, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest"%>
<%
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    SWBResourceURL saveURL = paramRequest.getActionUrl();
    saveURL.setAction(FavoritesMgr.ACTION_ADD_FAV);
    String entry = (String) request.getAttribute("entry");
    WebSite site = paramRequest.getWebPage().getWebSite();
%>
<!-- MODAL -->
<!--div class="modal fade" id="modalExh" tabindex="-1" role="dialog" aria-labelledby="modalTitle" aria-hidden="true"-->
    <div class="modal-dialog modal-exh modal-2col" role="document">
        <div class="modal-content">
            <div class="row">
                <div class="col-4 col-sm-5 modal-col1">
                    <div class="modal-izq">
                        <img src="/work/models/<%=site.getId()%>/img/cabecera-colaborar.jpg">    
                    </div>
                </div>
                <div class="col-8 col-sm-7 modal-col2">
                    <div class="modal-header">
                        <h4 class="modal-title oswM rojo">CREAR NUEVA COLECCIÓN</h4>
                        <button type="button" class="close" data-dismiss="modal">
                            <span class="ion-ios-close-outline"></span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <form id="addCollForm" action="<%=saveURL.toString()%>" method="post">
                            <input type="hidden" id="entry" name="entry" value="<%=entry%>">
                            <div class="form-group">
                                <label for="crearNombre">Nombre</label>
                                <input type="text" name="title" maxlength="100" value="" id="crearNombre" class="form-control" placeholder="60" aria-label="Recipient's username" aria-describedby="basic-addon2"/><div id="dialog-msg-edit"></div>
                                <label for="crearDescr">Descripción (opcional)</label>
                                <textarea name="description" id="crearDescr" placeholder="250"></textarea>        
                                <label for="selprivado" class="selPrivado">
                                    <input name="status" value="" id="selprivado" type="checkbox" aria-label="Checkbox for following text input"/>
                                    <span class="ion-locked"> Privado</span>
                                </label>
                            </div>
                            <button type="button" onclick="add();" class="btn-cultura btn-rojo btn-mayus">Crear colección</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
<!--/div-->