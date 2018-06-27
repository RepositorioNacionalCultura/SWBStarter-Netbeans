<%-- 
    Document   : mycollections
    Created on : 24/01/2018, 05:36:23 PM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, mx.gob.cultura.portal.resources.MyCollections, org.semanticwb.model.WebSite, mx.gob.cultura.portal.response.Collection, java.util.List"%>
<%
    List<Collection> boards = (List<Collection>) request.getAttribute("PAGE_LIST");
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
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

    SWBResourceURL wall = paramRequest.getRenderUrl().setMode(MyCollections.MODE_VIEW_ALL);
    wall.setCallMethod(SWBParamRequest.Call_CONTENT);

    WebSite site = paramRequest.getWebPage().getWebSite();

    //Integer size = null != boards ? boards.size() : 0;
    Integer size = (Integer) request.getAttribute("NUM_RECORDS_TOTAL");
%>
<script>
    $(document).ready(function () {
        $("#alertSuccess").on('hidden.bs.modal', function () {
            window.location.replace('<%=wall%>');
        });
    });
</script>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<script type="text/javascript">
    function addByForm() {
        //Do not use in dialog
        document.getElementById("addCollForm").action = '<%=saveURL.toString()%>';
        document.getElementById("addCollForm").submit();
    }
    function add(url) {
        //Do not use in dialog
        var leftPosition = (screen.width) ? (screen.width - 480) / 3 : 0;
        var topPosition = (screen.height) ? (screen.height - 441) / 3 : 0;
        popCln = window.open(
                url, 'popCln', 'height=441,width=480,left=' + leftPosition + ',top=' + topPosition + ',resizable=no,scrollbars=no,toolbar=no,menubar=no,location=no,directories=no,status=no')
    }
    function editByForm(id) {
        //document.getElementById("addCollForm").action = '<%=uedt.toString()%>?id='+id;
        //document.getElementById("addCollForm").submit();
        dojo.xhrPost({
            url: '<%=uedt.toString()%>?id=' + id,
            load: function (data) {
                dojo.byId('editCollection').innerHTML = data;
                $('#editCollection').modal('show');
            }
        });
    }
    function save() {
        if (validateData()) {
            dojo.xhrPost({
                url: '<%=saveURL.toString()%>',
                form: 'addCollForm',
                sync: true,
                timeout: 180000,
                load: function (data) {
                    var res = dojo.fromJson(data);
                    if (null != res.id) {
                        //alert('Se guardó correctamente su colección');
                        $('#modalExh').modal('hide');
                        jQuery("#dialog-text").text("Se guardó correctamente en su colección.");
                        $('#alertSuccess').modal('show');
                        //window.location.replace('/swb/<%=site.getId()%>/colecciones');
                    } else {
                        //alert('Ya tiene una colección con éste nombre');
                        jQuery("#dialog-msg").text("Ya tiene una colección con éste nombre.");
                        //$("#dialog-message" ).dialog( "open" );
                    }
                }
            });
        }
    }
    function validateData() {
        if (document.forms.addCollForm.title.value === '') {
            //alert("Favor de proporcionar nombre de colección.");	
            jQuery("#dialog-msg").text("Favor de proporcionar nombre de colección.");
            //$("#dialog-message" ).dialog( "open" );
            return false;
        }
        return true;
    }
    function edit(id) {
        var leftPosition = (screen.width) ? (screen.width - 480) / 3 : 0;
        var topPosition = (screen.height) ? (screen.height - 441) / 3 : 0;
        var url = '<%=uedt.toString()%>?id=' + id;
        popCln = window.open(
                url, 'popCln', 'height=441,width=480,left=' + leftPosition + ',top=' + topPosition + ',resizable=no,scrollbars=no,toolbar=no,menubar=no,location=no,directories=no,status=no')
    }
    function del(id) {
        document.getElementById("addCollForm").action = '<%=udel.toString()%>?id=' + id;
        document.getElementById("addCollForm").submit();
    }
    function changeStatus(id) {
        dojo.xhrPost({
            url: '<%=uper%>?id=' + id,
            load: function (data) {
                dojo.byId('references').innerHTML = data;
                location.href = '#showPage';
            }
        });
    }
    function doPage(p) {
        dojo.xhrPost({
            url: '<%=pageURL%>?p=' + p,
            load: function (data) {
                dojo.byId('references').innerHTML = data;
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
                form: 'saveCollForm',
                sync: true,
                timeout: 180000,
                load: function (data) {
                    var res = dojo.fromJson(data);
                    if (null != res.id) {
                        //alert('Se guardó correctamente en su colección');
                        $('#editCollection').modal('hide');
                        jQuery("#dialog-text").text("Se actualizó correctamente en su colección.");
                        $('#alertSuccess').modal('show');
                        //window.location.replace('/swb/<%=site.getId()%>/colecciones');
                    } else {
                        //alert('Ya tiene una colección con éste nombre');
                        jQuery("#dialog-msg-edit").text("Ya tiene una colección con éste nombre.");
                        //$("#dialog-message" ).dialog( "open" );
                    }
                }
            });
        }
    }
    function validateEdit() {
        if (document.forms.saveCollForm.title.value === '') {
            //alert("Favor de proporcionar nombre de colección.");	
            jQuery("#dialog-msg-edit").text("Favor de proporcionar nombre de colección.");
            //$("#dialog-message" ).dialog( "open" );
            return false;
        }
        return true;
    }
</script>
<div class="container usrTit">
    <div class="row">
        <img src="/work/models/<%=site.getId()%>/img/agregado-07.jpg" class="circle">
        <div>
            <h2 class="oswM nombre"><%=paramRequest.getUser().getFullName()%></h2>
            <p class="subnombre">Lorem ipsum dolor sit amet, consecetur adipscing elit.</p>
            <button class="btn-cultura btn-blanco">EDITAR PERFIL</button>
        </div>
    </div>
    <div class="buscacol">
        <button class="" type="submit"><span class="ion-search"></span></button>
        <input id="buscaColeccion" class="form-control" type="text" placeholder="BUSCA EN LAS COLECCIONES... " aria-label="Search">
    </div>
</div>
<div class="menuColecciones">
    <a href="'<%=wall%>" class="selected">Mis colecciones (<%=size%>)</a>
    <a href="#" class="">Mis favoritos (0)</a>
    <a href="#" class="">Recomendados (20)</a>
    <a href="#" class="">Todos (50)</a>
    <a href="#" class="">Temas (1)</a>
</div>
<a name="showPage"></a>
<div id="references">
    <div class="container">
        <div class="row mosaico-contenedor">
            <div class="col-6 col-md-4">
                <div class="mosaico radius-overflow">
                    <a href="#" data-toggle="modal" data-target="#modalExh">
                        <span class="ion-ios-plus rojo"></span>
                    </a>
                </div>
                <div class="mosaico-txt ">
                    <p><span class="ion-locked rojo"></span> Crear colección</p>
                    <p>Lorem ipsum dolor sit</p>
                </div>
            </div>
            <%
                if (!boards.isEmpty()) {
                    for (Collection c : boards) {
            %>
                        <div class="col-6 col-md-4">
                            <%	if (c.getCovers().isEmpty()) {	%>
                                    <div class="mosaico mosaico1 radius-overflow">
					<a href="<%=uels%>?id=<%=c.getId()%>">
                                            <img src="/work/models/<%=site.getId()%>/img/empty.jpg">
					</a>
                                    </div>
                            <%  }else if (c.getCovers().size() < 3) { %>
                                    <div class="mosaico mosaico1 radius-overflow">
					<a href="<%=uels%>?id=<%=c.getId()%>">
                                            <img src="<%=c.getCovers().get(0)%>">
					</a>
                                    </div>
                            <%  }else { %>
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
                            <% } %>
                            <div class="mosaico-txt ">
                                <p>
                                    <% if (!c.getStatus()) { %><span class="ion-locked rojo"><% } else { %><span class="ion-unlocked rojo"><% }%>
                                        </span><%=c.getTitle()%></p>
                                <p>Curada por: <%=paramRequest.getUser().getFullName()%></p>
                                <a href="#"><span class="ion-social-facebook"></span></a>
                                <a href="#"><span class="ion-social-twitter"></span></a>
                                <a href="#" onclick="editByForm('<%=c.getId()%>');"><span class="ion-edit"></span></a>
                            </div>
                        </div>
                <%
                    }
                %>
                    <jsp:include page="pager.jsp" flush="true"/>
            <%
                }
            %>
        </div>
    </div>
</div>
<!-- MODAL -->
<div class="modal fade" id="modalExh" tabindex="-1" role="dialog" aria-labelledby="modalTitle" aria-hidden="true">
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
                        <h4 class="modal-title oswM rojo">CREAR NUEVA COLECCIÓN</h4>
                        <button type="button" class="close" data-dismiss="modal">
                            <span class="ion-ios-close-outline"></span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <form id="addCollForm" action="<%=saveURL.toString()%>" method="post">
                            <div class="form-group">
                                <label for="crearNombre">Nombre</label>
                                <input type="text" name="title" maxlength="100" value="" id="crearNombre" class="form-control" placeholder="60" aria-label="Recipient's username" aria-describedby="basic-addon2"/>
                                <label for="crearDescr">Descripción (opcional)</label>
                                <textarea name="description" id="crearDescr" placeholder="250"></textarea>        
                                <label for="selprivado" class="selPrivado">
                                    <input name="status" value="" id="selprivado" type="checkbox" aria-label="Checkbox for following text input"/>
                                    <span class="ion-locked"> Privado</span>
                                </label>
                            </div>
                            <button type="button" onclick="save();" class="btn-cultura btn-rojo btn-mayus">Crear colección</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
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
                <button type="button" id="closeAlert" class="btn btn-sm rojo" data-dismiss="modal">Cerrar</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="editCollection" tabindex="-1" role="dialog" aria-labelledby="modalTitle" aria-hidden="true"></div>