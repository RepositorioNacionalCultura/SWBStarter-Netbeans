<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.resources.ExhibitionResource, mx.gob.cultura.portal.utils.EditorTemplate, org.semanticwb.portal.api.SWBResourceURL, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, java.util.List"%>
<%
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    SWBResourceURL saveURL = paramRequest.getActionUrl();
    saveURL.setAction(ExhibitionResource.ACTION_ADD_EXH);
    String userlang = paramRequest.getUser().getLanguage();
    String siteid = paramRequest.getWebPage().getWebSiteId();
    List<EditorTemplate> tmpls = (List<EditorTemplate>)request.getAttribute("tmpls");
    SWBResourceURL delURL = paramRequest.getActionUrl();
    delURL.setAction(ExhibitionResource.ACTION_DEL_EXH);
%>
<script src="https://code.jquery.com/jquery-3.3.1.min.js" integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
<script>
    function add(tpl, index) {
	document.forms.addExForm.id.value = tpl;
	var choose = 'choosetpl';
        var i;
	for (i=1; i<4; i++) {
            var choosetpl = choose + i;
            if (i == index) {
                document.getElementById(choosetpl).className = "sel-temas sel-exh col-6 col-sm-4 selected";
            }else {
                document.getElementById(choosetpl).className = "sel-temas sel-exh col-6 col-sm-4";
            }
	}
	return false;
    }
    function del(uri) {
        var xhttp = new XMLHttpRequest();
	var url = '<%=delURL%>'+'?exh_del='+uri;
        xhttp.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 200) {
                location.href = '/<%=userlang%>/<%=siteid%>/exhibiciones';
            }
	};
        xhttp.open("POST", url, true);
	xhttp.send();
    }
    function validate() {
        if (document.forms.addExForm.title.value.trim() == '') {
            jQuery("#dialog-msg-edit").text("Favor de proporcionar nombre de exhibición.");
            return false;
	}
        if (document.forms.addExForm.id.value.trim() == '') {
            jQuery("#dialog-msg-tpl").text("Favor de seleccionar plantilla para exhibición.");
            return false;
	}
        return true;
    }
</script>

<!-- button type="button" class="" data-toggle="modal" data-target="#modalExh">Crear exhibición</button-->
<!-- MODAL -->
<div class="modal fade" id="modalExh" tabindex="-1" role="dialog" aria-labelledby="modalTitle" aria-hidden="true">
    <div class="modal-dialog modal-exh modal-2col" role="document">
        <div class="modal-content">
            <div class="row">
                <div class="col-4 col-sm-5 modal-col1">
                    <div class="modal-izq">
                        <img src="/work/models/<%=siteid%>/img/cabecera-colaborar.jpg">    
                    </div>
                </div>
                <div class="col-8 col-sm-7 modal-col2">
                    <div class="modal-header">
			<h4 class="modal-title oswM rojo"><%=paramRequest.getLocaleString("usrmsg_view_create_exh")%></h4>  
                        <button type="button" class="close" data-dismiss="modal">
                            <span class="ion-ios-close-outline"></span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <form id="addExForm" action="<%=saveURL.toString()%>" method="post" onsubmit="return validate()">
                            <input type="hidden" name="id" value=""/>
                            <div class="form-group">
                                <label for="crearNombre"><%=paramRequest.getLocaleString("usrmsg_view_create_exh_name")%></label><div id="dialog-msg-edit"></div>
                                <input type="text" name="title" id="title" class="form-control" placeholder="60" aria-label="Recipient's username" aria-describedby="basic-addon2">
                                <label for="crearDescr"><%=paramRequest.getLocaleString("usrmsg_view_create_exh_dialog_desc")%></label>
                                <textarea name="description" id="description" placeholder="250"></textarea>
                                <h5 class="modal-title "><div id="dialog-msg-tpl"><%=paramRequest.getLocaleString("usrmsg_view_create_exh_select_tpl")%></div></h5>
                                <div class="row">
                                    <%
                                        if (!tmpls.isEmpty()) {
                                            int i = 1;
                                            for (EditorTemplate t :  tmpls) {
                                    %>
                                                <div id="choosetpl<%=i%>" class="sel-temas sel-exh col-6 col-sm-4">
                                                    <button type="button" onclick="add(<%=t.getId()%>, <%=i%>);">
                                                        <span class="ion-android-checkbox-outline-blank"></span>
                                                        <span class="ion-android-checkbox-outline"></span>
                                                        <img src="<%=t.getPreview()%>">
                                                    </button>
                                                </div>
                                    <%
                                                i++;
                                            }
					}
                                    %>
                                </div>
                            </div>
                            <button type="submit" class="btn-cultura btn-rojo btn-mayus"><%=paramRequest.getLocaleString("usrmsg_view_create_exh")%></button>
                            <!--
                                <button type="submit" class="btn-cultura btn-blanco btn-mayus d-none d-lg-block"><span class="ion-trash-a"></span> Eliminar colección</button>
                                <button type="submit" class="btn-cultura btn-blanco btn-mayus d-block d-lg-none"> Eliminar</button>
                            -->
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>