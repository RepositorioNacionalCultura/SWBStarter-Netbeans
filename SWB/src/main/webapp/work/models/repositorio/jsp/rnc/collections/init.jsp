<%-- 
    Document   : mycollections
    Created on : 28/03/2018, 07:58:29 PM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.semanticwb.SWBPortal, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, org.semanticwb.model.WebSite, mx.gob.cultura.portal.resources.MyCollections, mx.gob.cultura.portal.response.Collection, java.util.List"%>
<%
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    //Use in dialog
    SWBResourceURL saveURL = paramRequest.getActionUrl();
    saveURL.setMode(SWBResourceURL.Mode_VIEW);
    saveURL.setAction(MyCollections.ACTION_ADD);
    
    WebSite site = paramRequest.getWebPage().getWebSite();
    String userLang = paramRequest.getUser().getLanguage();
    
    SWBResourceURL wall = paramRequest.getRenderUrl().setMode(MyCollections.MODE_VIEW_MYALL);
    wall.setCallMethod(SWBParamRequest.Call_CONTENT);

    SWBResourceURL uall = paramRequest.getRenderUrl().setMode(MyCollections.MODE_VIEW_ALL);
    uall.setCallMethod(SWBParamRequest.Call_CONTENT);
    
    Integer size = null != request.getAttribute("NUM_RECORDS_TOTAL") ? (Integer)request.getAttribute("NUM_RECORDS_TOTAL") : 0;
    Integer allc = null != request.getAttribute("COUNT_BY_STAT") ? (Integer)request.getAttribute("COUNT_BY_STAT") : 0;
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
                        $('#modalExh').modal('hide');
                        jQuery("#dialog-text").text("Se guardó correctamente en su colección.");
                        $('#alertSuccess').modal('show');
                    } else {
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
</script>
<div class="container usrTit">
    <div class="row">
        <% if (null != paramRequest.getUser() && null != paramRequest.getUser().getPhoto()) { %>
        <img src="<%=SWBPortal.getWebWorkPath()+paramRequest.getUser().getPhoto()%>" class="circle">
        <% } else {%>
            <img src="/work/models/<%=site.getId()%>/img/agregado-07.jpg" class="circle">
        <% } %>
        <div>
            <h2 class="oswM nombre"><%=null!=paramRequest.getUser() ?paramRequest.getUser().getFullName():""%></h2>
            <!--<p class="subnombre">Lorem ipsum dolor sit amet, consecetur adipscing elit.</p>-->
            <button class="btn-cultura btn-blanco" onclick="javascript:location.replace('/<%=userLang%>/<%=site.getId()%>/Registro');">EDITAR PERFIL</button>
        </div>
    </div>
    <div class="buscacol">
        <button class="" type="submit"><span class="ion-search"></span></button>
        <input id="buscaColeccion" class="form-control" type="text" placeholder="BUSCA EN LAS COLECCIONES... " aria-label="Search">
    </div>
</div>
<div class="menuColecciones">
    <a href="<%=wall%>" class="selected">Mis colecciones (<%=size%>)</a>
    <a href="#" class="">Mis favoritos (0)</a>
    <!--a href="#" class="">Recomendados (20)</a-->
    <a href="<%=uall%>" class="">Todos (<%=allc%>)</a>
    <!--a href="#" class="">Temas (1)</a-->
</div>
<div class="container">
    <div class="row">
        <div class="col-12 col-sm-5 col-md-3">
            <div class="mosaico mosaico-crear radius-overflow">
                <a href="#" data-toggle="modal" data-target="#modalExh">
                    <span class="ion-ios-plus rojo"></span>
                </a>
            </div>
            <div class="mosaico-txt d-block d-sm-none" style="">
                <p>Crear colección</p>
                <!--<p>Lorem ipsum dolor</p>-->
            </div>
        </div>
        <div class="col-12 col-sm-7 col-md-9">
            <div class="contactabloque imgColabora radius-overflow margen0 sinsombra">
                <div class="contactabloque-in ">
                    <p class="oswM">Busca obras de tu interés<br> y agrégalas a tus colecciones</p>
                    <button class="btn-cultura btn-rojo" onclick="javascript:location.replace('/<%=userLang%>/<%=site.getId()%>/explorar');" type="button">EXPLORAR <span class="ion-chevron-right"></span></button>
                </div>
            </div>
        </div>
        <div class="col-12 mosaico-txt d-none d-sm-block">
            <p>Crear colección</p>
            <!--<p>Lorem ipsum dolor</p>-->
        </div>
    </div>
</div>
                
<!--<div class="container center titulo">
    <h3 class="oswB rojo"><span class="h3linea rojoborde"></span>RECOMENDADOS<span class="h3linea rojoborde"></span></h3>
</div>
<div class="container">
    <div class="row mosaico-contenedor">
        <div class="col-6 col-md-4">
            <div class="mosaico mosaico3 radius-overflow">
                <a href="#">
                    <div class="mosaico3a">
                        <img src="/work/models/<%=site.getId()%>/img/buscado-04.jpg">
                    </div>
                    <div class="mosaico3b">
                        <div>
                            <img src="/work/models/<%=site.getId()%>/img/buscado-05.jpg">
                        </div>
                        <div>
                            <img src="/work/models/<%=site.getId()%>/img/buscado-06.jpg">
                        </div>
                    </div>
                </a>
            </div>
            <div class="mosaico-txt ">
                <p><span class="ion-locked rojo"></span>Colores de M&eacute;xico</p>
                <p>Curada por: Leonor Rivas Mercado</p>
                <a href="#"><span class="ion-social-facebook"></span></a>
                <a href="#"><span class="ion-social-twitter"></span></a>
                <a href="#"><span class="ion-heart"></span></a>
            </div>
        </div>
        <div class="col-6 col-md-4">	
            <div class="mosaico mosaico1 radius-overflow">
                <a href="#">
                    <img src="/work/models/<%=site.getId()%>/img/agregado-03.jpg">
                </a>
            </div>
            <div class="mosaico-txt ">
                <p><span class="ion-locked rojo"></span> Tambores</p>
                <p>Curada por: Leonor Rivas Mercado</p>
                <a href="#"><span class="ion-social-facebook"></span></a>
                <a href="#"><span class="ion-social-twitter"></span></a>
                <a href="#"><span class="ion-heart"></span></a>
            </div>
        </div>
        <div class="col-6 col-md-4">
            <div class="mosaico mosaico3 radius-overflow">
                <a href="#">
                    <div class="mosaico3a">
                        <img src="/work/models/<%=site.getId()%>/img/agregado-04.jpg">
                    </div>
                    <div class="mosaico3b">
                        <div>
                            <img src="/work/models/<%=site.getId()%>/img/agregado-03.jpg">
                        </div>
                        <div>
                            <img src="/work/models/<%=site.getId()%>/img/agregado-07.jpg">
                        </div>
                    </div>
                </a>
            </div>
            <div class="mosaico-txt ">
                <p><span class="ion-locked rojo"></span>Los Muralistas mexicanos de inicios del siglo XX</p>
                <p>Curada por: Leonor Rivas Mercado</p>
                <a href="#"><span class="ion-social-facebook"></span></a>
                <a href="#"><span class="ion-social-twitter"></span></a>
                <a href="#"><span class="ion-heart rojo"></span></a>
            </div>
        </div>
        <div class="col-6 col-md-4">
            <div class="mosaico mosaico3 radius-overflow">
                <a href="#">
                    <div class="mosaico3a">
                        <img src="/work/models/<%=site.getId()%>/img/agregado-08.jpg">
                    </div>
                    <div class="mosaico3b">
                        <div>
                            <img src="/work/models/<%=site.getId()%>/img/agregado-02.jpg">
                        </div>
                        <div>
                            <img src="/work/models/<%=site.getId()%>/img/agregado-01.jpg">
                        </div>
                    </div>
                </a>
            </div>
            <div class="mosaico-txt ">
                <p><span class="ion-locked rojo"></span>Los Muralistas mexicanos de inicios del siglo XX</p>
                <p>Curada por: Leonor Rivas Mercado</p>
                <a href="#"><span class="ion-social-facebook"></span></a>
                <a href="#"><span class="ion-social-twitter"></span></a>
                <a href="#"><span class="ion-heart"></span></a>
            </div>
        </div>
        <div class="col-6 col-md-4">
            <div class="mosaico mosaico1 radius-overflow">
                <a href="#">
                    <img src="/work/models/<%=site.getId()%>/img/agregado-06.jpg">
                </a>
            </div>
            <div class="mosaico-txt ">
                <p><span class="ion-locked rojo"></span> Tambores</p>
                <p>Curada por: Leonor Rivas Mercado</p>
                <a href="#"><span class="ion-social-facebook"></span></a>
                <a href="#"><span class="ion-social-twitter"></span></a>
                <a href="#"><span class="ion-heart"></span></a>
            </div>
        </div>
        <div class="col-6 col-md-4">
            <div class="mosaico mosaico3 radius-overflow">
                <a href="#">
                    <div class="mosaico3a">
                        <img src="/work/models/<%=site.getId()%>/img/buscado-01.jpg">
                    </div>
                    <div class="mosaico3b">
                        <div>
                            <img src="/work/models/<%=site.getId()%>/img/buscado-02.jpg">
                        </div>
                        <div>
                            <img src="/work/models/<%=site.getId()%>/img/buscado-03.jpg">
                        </div>
                    </div>
                </a>
            </div>
            <div class="mosaico-txt ">
                <p><span class="ion-locked rojo"></span>Acuarelistas</p>
                <p>Curada por: Leonor Rivas Mercado</p>
                <a href="#"><span class="ion-social-facebook"></span></a>
                <a href="#"><span class="ion-social-twitter"></span></a>
                <a href="#"><span class="ion-heart rojo"></span></a>
            </div>
        </div>
    </div>
</div>-->
<!--<div class="container paginacion">
    <hr>
    <ul class="azul">
        <li><a href="#"><i class="ion-ios-arrow-back" aria-hidden="true"></i></a></li>
        <li><a href="#">1</a></li>
        <li><a href="#" class="select">2</a></li>
        <li><a href="#">3</a></li>
        <li><a href="#">4</a></li>
        <li><a href="#">5</a></li>
        <li><a href="#"><i class="ion-ios-arrow-forward" aria-hidden="true"></i></a></li>
    </ul>
</div>-->

<!-- MODAL -->
<div class="modal fade" id="modalExh" tabindex="-1" role="dialog" aria-labelledby="modalTitle" aria-hidden="true">
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
                            <div class="form-group">
                                <label for="crearNombre">Nombre</label>
                                <input type="text" name="title" maxlength="100" value="" id="crearNombre" class="form-control" placeholder="60" aria-label="Recipient's username" aria-describedby="basic-addon2"/><div id="dialog-msg"></div>
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