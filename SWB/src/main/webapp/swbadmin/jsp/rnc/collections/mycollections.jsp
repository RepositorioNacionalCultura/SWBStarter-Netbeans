<%-- 
    Document   : mycollections
    Created on : 24/01/2018, 05:36:23 PM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, mx.gob.cultura.portal.resources.MyCollections, org.semanticwb.model.WebSite, mx.gob.cultura.portal.response.Collection, java.util.List"%>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>

<%
    List<Collection> boards = (List<Collection>)request.getAttribute("PAGE_LIST");
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    //Use in dialog
    SWBResourceURL saveURL = paramRequest.getActionUrl();
    saveURL.setMode(SWBResourceURL.Mode_VIEW);
    saveURL.setAction(MyCollections.ACTION_ADD);

    SWBResourceURL uedt = paramRequest.getRenderUrl().setMode(SWBResourceURL.Mode_EDIT);
    uedt.setCallMethod(SWBParamRequest.Call_DIRECT);

    SWBResourceURL pageURL = paramRequest.getRenderUrl();
    pageURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    pageURL.setAction("PAGE");

    SWBResourceURL uper = paramRequest.getActionUrl();
    SWBResourceURL udel = paramRequest.getActionUrl();
    uper.setAction(MyCollections.ACTION_STA);
    uper.setCallMethod(SWBParamRequest.Call_DIRECT);
    udel.setAction(SWBResourceURL.Action_REMOVE);

    SWBResourceURL uels = paramRequest.getRenderUrl().setMode(MyCollections.MODE_VIEW_USR);
    uels.setCallMethod(SWBParamRequest.Call_CONTENT);

    WebSite site = paramRequest.getWebPage().getWebSite();
%>

<script type="text/javascript">
    function editByForm(id) {
	dojo.xhrPost({
            url: '<%=uedt.toString()%>?id='+id,
            load: function(data) {
                dojo.byId('editCollection').innerHTML=data;
		$('#editCollection').modal('show');
            }
        });
    }
    function save() {
	if (validateData()) {
            dojo.xhrPost({
		url: '<%=saveURL.toString()%>',
                form:'addCollForm',
		sync: true,
		timeout:180000,
		load: function(data) {
                    var res = dojo.fromJson(data);
			if (null != res.id) {
                            $('#addCollection').modal('hide');
                            jQuery("#dialog-text").text("Se guardó correctamente en su colección.");
                            $('#alertSuccess').modal('show');
			}else {
                            jQuery("#dialog-msg").text("Ya tiene una colección con éste nombre.");
                        }
		}
            });
	}
    }
    function validateData() {
	if (document.forms.addCollForm.title.value === '') {
            jQuery("#dialog-msg").text("Favor de proporcionar nombre de colección.");
            return false;
	}
	return true;
    }
    function del(id) {
	document.getElementById("addCollForm").action = '<%=udel.toString()%>?id='+id;
	document.getElementById("addCollForm").submit();
    }
    function changeStatus(id) {
        dojo.xhrPost({
            url: '<%=uper%>?id='+id,
            load: function(data) {
                dojo.byId('references').innerHTML=data;
		location.href = '#showPage';
            }
        });
    }
    function doPage(p) {
        dojo.xhrPost({
            url: '<%=pageURL%>?p='+p,
            load: function(data) {
                dojo.byId('references').innerHTML=data;
		location.href = '#showPage';
            }
        });
    }
</script>
<script type="text/javascript">
    function saveEdit(uri) {
	if (validateEdit()) {
            dojo.xhrPost({
		url: uri,
		form:'saveCollForm',
		sync: true,
		timeout:180000,
		load: function(data) {
                    var res = dojo.fromJson(data);
                    if (null != res.id) {
                        $('#editCollection').modal('hide');
			jQuery("#dialog-text").text("Se actualizó correctamente en su colección.");
			$('#alertSuccess').modal('show');
                    }else {
			jQuery("#dialog-msg-edit").text("Ya tiene una colección con éste nombre.");
                    }
		}
            });
        }
    }
    function validateEdit() {
	if (document.forms.saveCollForm.title.value === '') {
            jQuery("#dialog-msg-edit").text("Favor de proporcionar nombre de colección.");
            return false;
	}
	return true;
    }
</script>
<a name="showPage"></a>
<div id="references">
    <p>
	<button type="button" class="btn btn-sm rojo" data-toggle="modal" data-target="#addCollection">Crear colección</button>
    </p>
    <%
        if (!boards.isEmpty()) {
            for (Collection c : boards) {
    %>
                <div class="item">
                    <div class="card">
			<div class="card-header">
                            <a href="<%=uels%>?id=<%=c.getId()%>"><%=c.getTitle()%></a><span class="ion-android-more-vertical"/>
			</div>
                        <div class="card-body">
                            <p class="card-text"><%=c.getDescription()%></p>
			</div>
			<div class="card-footer">
                            <small class="text-muted">
				<div>
                                    <a href="#" onclick="changeStatus(<%=c.getId()%>);"><span class="ion-eye"/>
                                    <%
					if (c.getStatus()) out.print("Publica"); else out.print("Privada");
                                    %>
                                    </a>
				</div>
				<div><a class="rojo" href="#" onclick="del(<%=c.getId()%>);"> eliminar</a></div>
				<div><a class="rojo" href="#" onclick="editByForm(<%=c.getId()%>);"> editar</a></div>
                            </small>
			</div>
                    </div>
		</div>
    <%
            }
    %>
            <jsp:include page="pager.jsp" flush="true"/>
    <%  } %>
</div>
<div class="modal fade" id="addCollection" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
		<h4 class="modal-title">Agregar colección</h4>
		<button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
		<p>
                    <form id="addCollForm" action="<%=saveURL.toString()%>" method="post">
			<div class="col-xs-12 col-sm-12 col-md-10 col-lg-10 car-img2">
                            <div class="card-body">
				<span class="card-title">* Nombre: </span><input type="text" name="title" maxlength="100" size="40" value=""/><div id="dialog-msg"></div>
                            </div>
                            <div class="card-body">
				<span class="card-title">Descripción: </span><textarea name="description" rows="4" cols="40" maxlength="500" wrap="hard"></textarea>
                            </div>
                            <div class="card-body">
				<span class="card-title">Público: </span><input type="checkbox" name="status" value=""/>
                            </div>
			</div>
                    </form>
		</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm rojo" data-dismiss="modal">Cerrar</button>
		<button type="button" onclick="save();" class="btn btn-sm rojo">Guardar</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="alertSuccess" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
		<h4 class="modal-title">Éxito</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
		<div id="dialog-text"></div>
            </div>
            <div class="modal-footer">
		<button type="button" class="btn btn-sm rojo" data-dismiss="modal">Cerrar</button>
            </div>
	</div>
    </div>
</div>

<div class="modal fade" id="editCollection" role="dialog"></div>