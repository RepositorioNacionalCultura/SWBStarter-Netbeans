<%-- 
    Document   : treecollection
    Created on : 24/01/2018, 01:24:24 PM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.resources.FavoritesMgr, org.semanticwb.portal.api.SWBResourceURL, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.model.WebSite, mx.gob.cultura.portal.response.Collection, java.util.List"%>
<%
    List<Collection> boards = (List<Collection>) request.getAttribute("mycollections");
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    SWBResourceURL saveURL = paramRequest.getActionUrl();
    saveURL.setAction(FavoritesMgr.ACTION_ADD_FAV);
    String lang = paramRequest.getUser().getLanguage();
    String entry = (String) request.getAttribute("entry");
    WebSite site = paramRequest.getWebPage().getWebSite();
    SWBResourceURL uedt = paramRequest.getRenderUrl().setMode(FavoritesMgr.MODE_NEW_COLN);
    uedt.setCallMethod(SWBParamRequest.Call_CONTENT);
%>

<script type="text/javascript">
    function validateSel() {
        if (document.getElementsByName('id').value == '') {
            jQuery("#dialog-text-tree").text("Favor de seleccionar colección.");
            $("#dialog-message-tree").dialog("open");
            return false;
        }
        return true;
    }
    function save(id) {
        if (validateSel()) {
            document.forms.addFavForm.id.value = id;
            dojo.xhrPost({
                url: '<%=saveURL.toString()%>',
                form: 'addFavForm',
                sync: true,
                timeout: 180000,
                load: function (data) {
                    var res = dojo.fromJson(data);
                    if (null != res.idCollection) {
                        $("#addCollection").dialog("close");
                        jQuery("#dialog-go-text").text("Se guardó correctamente en su colección.");
                        go2Collection(res.idCollection, '/<%=lang%>/<%=site.getId()%>/miscolecciones/_rid/112/_mod/VIEW_USR');
                    }
                }
            });
        }
    }
    function add() {
        if (validateNew()) {
            dojo.xhrPost({
                url: '<%=saveURL.toString()%>',
                form: 'addCollForm',
                sync: true,
                timeout: 180000,
                load: function (data) {
                    var res = dojo.fromJson(data);
                    if (null != res.idCollection) {
                        $("#newCollection").modal('hide');
                        jQuery("#dialog-msg-tree").text("Se guardó correctamente en su colección.");
                        $("#dialog-success-tree").dialog("open");
                    } else {
                        jQuery("#dialog-msg-edit").text("Ya tiene una colección con éste nombre.");
                        //$("#dialog-message-tree" ).dialog( "open" );
                    }
                }
            });
        }
    }
    function validateNew() {
        if (document.forms.addCollForm.title.value == '') {
            jQuery("#dialog-msg-edit").text("Favor de proporcionar nombre de colección.");
            return false;
        }
        return true;
    }
</script>
<!--div class="modal fade" id="modalExh" tabindex="-1" role="dialog" aria-labelledby="modalTitle" aria-hidden="true"-->
    <!--div class="modal-dialog modal-exh modal-2col" role="document"-->
        <div class="modal-content">
            <div class="row">
                <div class="col-4 col-sm-5 modal-col1">
                    <div class="modal-izq">
                        <img src="/work/models/<%=site.getId()%>/img/cabecera-colaborar.jpg">    
                    </div>
                </div>
                <div class="col-8 col-sm-7 modal-col2 agregarColecc-container">
                    <div class="modal-header">
                        <h4 class="modal-title oswM rojo">AGREGAR A COLECCIÓN</h4>            
                        <button type="button" class="close" data-dismiss="modal" onclick="dismiss();">
                            <span class="ion-ios-close-outline"></span>
                        </button>
                    </div>
                    <form id="addFavForm" action="<%=saveURL.toString()%>" method="post">
                        <input type="hidden" id="id" name="id" value="">
                        <input type="hidden" id="entry" name="entry" value="<%=entry%>">
                        <div class="modal-body">
                            <div class="agregarColecc agregarColecc-nueva">
                                <button type="button" onclick="addnew('<%=uedt%>?entry=<%=entry%>');" class="rojo">
                                    <span class="ion-ios-plus rojo"></span>
                                    <em class="rojo">Crear nueva colección</em>
                                </button>
                            </div>
                            <%
                                if (!boards.isEmpty()) {
                                    for (Collection c : boards) {
                            %>
                            <div class="agregarColecc">
                                <button type="button" onclick="save('<%=c.getId()%>');">
                                    <span class="ion-locked"></span>
                                    <em><%=c.getTitle()%></em>
                                    <span class="ion-ios-plus"></span>
                                </button>
                                <div>
                                    <%
                                        if (c.getCovers().isEmpty()) {
                                    %>
                                    <img src="/work/models/<%=site.getId()%>/img/empty.jpg">
                                    <%
                                    } else {
                                    %>
                                    <img src="<%=c.getCovers().get(0)%>">
                                    <%
                                        }
                                    %>

                                </div>
                            </div>
                            <%
                                    }
                                }
                            %>
                        </div>
                    </form>
                </div>          
            </div>
        </div>
    <!--/div-->
<!--/div-->

<div id="dialog-go-tree" title="Éxito">
    <p>
        <div id="dialog-go-text"></div>
    </p>
</div>