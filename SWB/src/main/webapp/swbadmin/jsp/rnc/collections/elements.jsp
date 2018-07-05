<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, mx.gob.cultura.portal.resources.MyCollections, org.semanticwb.portal.api.SWBResourceURL, org.semanticwb.model.WebPage" %>
<%@ page import="mx.gob.cultura.portal.response.Title, mx.gob.cultura.portal.response.Collection, mx.gob.cultura.portal.response.DigitalObject, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Identifier, java.util.Date, org.bson.types.ObjectId"%>
<%
    Collection c = (Collection)request.getAttribute("collection");
    List<Entry> itemsList = (List<Entry>)request.getAttribute("myelements");
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    WebSite site = paramRequest.getWebPage().getWebSite();
    SWBResourceURL uedt = paramRequest.getRenderUrl().setMode(SWBResourceURL.Mode_EDIT);
    uedt.setCallMethod(SWBParamRequest.Call_DIRECT);
    SWBResourceURL uels = paramRequest.getRenderUrl().setMode(MyCollections.MODE_VIEW_USR);
    String _msg = null != request.getParameter("_msg") && "2".equals(request.getParameter("_msg")) ? "Se actualizó correctamente en su colección." : "";
    WebPage detail = site.getWebPage("detalle");
    String uri = detail.getRealUrl(paramRequest.getUser().getLanguage());
    SWBResourceURL delURL = paramRequest.getActionUrl();
    delURL.setMode(SWBResourceURL.Mode_VIEW);
    delURL.setAction(MyCollections.ACTION_DEL_FAV);
%>
<script>
    $(document).ready(function () {
        $("#alertSuccess").on('hidden.bs.modal', function () {
            window.location.replace('<%=uels%>?id=<%=c.getId()%>&_msg=2');
        });
    });
</script>
<script>
    function editByForm(id) {
	var xhttp = new XMLHttpRequest();
	var url = '<%=uedt%>'+'?id='+id;
        xhttp.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 200) {
                jQuery("#editCollection").html(this.responseText);
		$('#editCollection').modal('show');
            }
	};
        xhttp.open("POST", url, true);
	xhttp.send();
    }
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
			window.location.replace('<%=uels%>?id='+res.id+'&_msg=2');
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
    function del(id,entry) {
        var xhttp = new XMLHttpRequest();
	var url = '<%=delURL%>'+'?id='+id+'&entry='+entry;
        xhttp.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 200) {
                jQuery("#dialog-text").text("Se actualizó correctamente en su colección.");
		$('#alertSuccess').modal('show');
            }
	};
        xhttp.open("POST", url, true);
        xhttp.send();
    }
</script>
<div class="col-md-3"><a class=" oswL" href="javascript:history.go(-1)"><i aria-hidden="true" class="fa fa-long-arrow-left"></i> Regresar</a></div>
<div class="container coleccionSecc">
    <div class="row">
        <div class="col-9 col-sm-8 coleccionSecc-01">
            <div class="precontent">
                <h2 class="oswM rojo"><%=c.getTitle()%></h2>
                <div class="row perfilHead">
                    <img src="/work/models/repositorio/img/agregado-07.jpg" class="circle">
                    <p><%=paramRequest.getUser().getFullName()%>,&nbsp;&nbsp;<div id="fdate"></div></p>
                </div>
                <p><%=_msg%></p>
                <p><%=c.getDescription()%></p>
                <!--hr class="rojo">
                <div class="row redes">
                    <a href="#"><span class="ion-social-facebook"></span> Compartir</a>
                    <a href="#"><span class="ion-social-twitter"></span> Tweet</a>
                    <a href="#" class="rojo"><span class="ion-heart rojo"></span> Favoritos (356)</a>
                </div-->
            </div>
        </div>
        <div class="col-3 col-sm-4 coleccionSecc-02">
            <button type="button" onclick="editByForm('<%=c.getId()%>');" class="btn-cultura btn-blanco btn-mayus d-none d-md-block"><span class="ion-edit"></span> Editar colección</button>
            <button type="submit" class="btn-cultura btn-blanco btn-mayus d-block d-md-none"> <span class="ion-edit"></span> Editar</button>
        </div>
    </div>
</div>
<div class="container" style="padding:30px; text-align: center; background:#efefef">
    <div class="row offcanvascont">
	<div id="contenido">
            <%
                if (!itemsList.isEmpty()) {
            %>
                    <div id="references">
                        <div id="resultados" class="card-columns">
                            <%
                                for (Entry item : itemsList) {
                                    Title title = new Title();
                                    List<String> creators = item.getCreator();
                                    List<Title> titles = item.getRecordtitle();
                                    List<String> resourcetype = item.getResourcetype();
                                    if (!titles.isEmpty()) title = titles.get(0);
                                    String creator = creators.size() > 0 ? creators.get(0) : "";
                                    String type = resourcetype.size() > 0 ? resourcetype.get(0) : "";
                            %>
                                    <div class="pieza-res card">
                                        <a href="<%=uri%>?id=<%=item.getId()%>">
                                            <img src="<%=item.getResourcethumbnail()%>" />
                                        </a>
                                        <div>
                                            <p class="oswB azul tit"><a href="<%=uri%>?id=<%=item.getId()%>"><%=title.getValue()%></a></p>
                                            <p class="azul autor"><a href="#"><%=creator%></a></p>
                                            <p class="tipo"><%=type%></p>
                                            <%  if (null != paramRequest.getUser() && paramRequest.getUser().isSigned()) { %>
                                                    <a href="#" onclick="del('<%=c.getId()%>', '<%=item.getId()%>')">Quitar de la colección</a>
                                            <%  } %>
                                        </div>
                                    </div>
                            <%
				}
                            %>
			</div>
                    </div>
            <%
		}else out.print("<h3 class=\"oswB azul\">Agregue favoritos a su colección " + c.getTitle()+ "</h3>");
            %>
	</div>
    </div>
    <!--resultados -->
</div>
<div class="coleccionSecc-03 col-12 col-md-8 col-lg-6">
    <div class="agregarColecc ">
	<a href="#">
            <span class="ion-ios-plus"></span>
            <em class="oswM">Agregar  desde la colección</em>
            <span class="btn-cultura">Explorar <span class="ion-chevron-right"></span></span>
        </a>
	<div>
            <img src="/work/models/repositorio/img/cabecera-carranza.jpg">
        </div>
    </div>
</div>
<script>
    var d = new Date("<%=c.getDate()%>");
    var months = ["ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"];
    document.getElementById("fdate").innerHTML = d.getDay() + " DE " + months[d.getMonth()] + " DE " + d.getFullYear();
</script>

<div class="modal fade" id="editCollection" tabindex="-1" role="dialog" aria-labelledby="modalTitle" aria-hidden="true"></div>

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
                <button type="button" id="closeAlert" class="btn btn-sm rojo" data-dismiss="modal">Cerrar</button>
            </div>
        </div>
    </div>
</div>