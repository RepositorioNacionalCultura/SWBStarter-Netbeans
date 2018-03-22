<%-- 
    Document   : treecollection
    Created on : 24/01/2018, 01:24:24 PM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.resources.FavoritesMgr, org.semanticwb.portal.api.SWBResourceURL, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.model.WebSite, mx.gob.cultura.portal.response.Collection, java.util.List"%>
<%
    List<Collection> boards = (List<Collection>)request.getAttribute("mycollections");
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    SWBResourceURL saveURL = paramRequest.getActionUrl();
    saveURL.setAction(FavoritesMgr.ACTION_ADD_FAV);
    String entry = (String)request.getAttribute("entry");
    WebSite site = paramRequest.getWebPage().getWebSite();
%>

<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/css/bootstrap.min.css" integrity="sha384-PsH8R72JQ3SOdhVi3uxftmaW6Vc51MKb0q5P2rRUpPvrszuE4W1povHYgTpBfshb" crossorigin="anonymous">
<link rel="stylesheet" href=" http://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css">
<link rel="stylesheet" href="/work/models/<%=site.getId()%>/css/owl.carousel.css">
<link rel="stylesheet" href="/work/models/<%=site.getId()%>/css/owl.theme.green.css">
<link rel="stylesheet" href="/work/models/<%=site.getId()%>/css/cultura.css">
<link rel="stylesheet" href="/work/models/<%=site.getId()%>/css/offcanvas.css">
<link href="https://fonts.googleapis.com/css?family=Work+Sans:400,500,600,700" rel="stylesheet">
<link href="https://fonts.googleapis.com/css?family=Oswald:200,500,600" rel="stylesheet">
<script src="https://use.fontawesome.com/efa62eded2.js"></script>

<script type="text/javascript">
    function add() {
	if (validateNew()) {
            dojo.xhrPost({
		url: '<%=saveURL.toString()%>',
		form:'addFavForm',
		sync: true,
		timeout:180000,
		load: function(data) {
                    var res = dojo.fromJson(data);
                    if (null != res.idCollection) {
			$("#addCollection" ).dialog( "close" );
			jQuery("#dialog-msg-tree").text("Se guardó correctamente en su colección.");
			$("#dialog-success-tree" ).dialog( "open" );
                    }else {
			jQuery("#dialog-text-tree").text("Ya tiene una colección con éste nombre.");
			$("#dialog-message-tree" ).dialog( "open" );
                    }
		}
            });
	}
    }
    function save() {
	if (validateSel()) {
            document.forms.addFavForm.id.value = document.getElementsByName('idcollection')[0].value;
            dojo.xhrPost({
		url: '<%=saveURL.toString()%>',
		form:'addFavForm',
		sync: true,
		timeout:180000,
		load: function(data) {
                    var res = dojo.fromJson(data);
                    if (null != res.idCollection) {
			$("#addCollection" ).dialog( "close" );
			jQuery("#dialog-msg-tree").text("Se guardó correctamente en su colección.");
			$("#dialog-success-tree" ).dialog( "open" );
                    }
		}
            });
	}
    }
    function validateNew() {
	if (document.forms.addFavForm.title.value == '') {
            jQuery("#dialog-text-tree").text("Favor de proporcionar nombre de colección.");
            $("#dialog-message-tree" ).dialog( "open" );
            return false;
	}
	return true;
    }
    function validateSel() {
	if (document.getElementsByName('idcollection')[0].value == '') {
            jQuery("#dialog-text-tree").text("Favor de seleccionar colección.");
            $("#dialog-message-tree" ).dialog( "open" );
            document.getElementById("idcollection").focus();
            return false;
	}
	return true;
    }
</script>
<section id="treeboard" class="">
    <div class="container">
        <div class="row">
            <div class="col-xs-12 col-sm-12 col-md-2 col-lg-2 car-nav2">
                <div class="owl-nav col">
                    <h3 class="oswB rojo">Agregar a colección</h3>
                </div>
            </div>
        </div>
	<form id="addFavForm" action="<%=saveURL.toString() %>" method="post">
            <input type="hidden" id="id" name="id" value="">
            <input type="hidden" id="entry" name="entry" value="<%=entry%>">
        <%
            if (!boards.isEmpty()) {
        %>
		<div class="row">
                    <div class="owl-nav col">
                        <span class="oswB blanco">* Selecciona una colección: </span>
                            <select name="idcollection" id="idcollection">
				<option value=""></option>
        <%
                            for (Collection c :  boards) {
	%>
                                <option value="<%=c.getId()%>"><%=c.getTitle()%></option>
	<%
                            }
	%>
                            </select>
                    </div>
                </div>
	<%
            }
        %>
            <p>
		<span class="oswB rojo">* Nombre: </span><input type="text" name="title" maxlength="100" size="40" value=""/>
		<button type="button" onclick="add();" class="btn btn-sm rojo">Crear una colección nueva</button>
                <!--button type="button" onclick="window.close();" class="btn btn-sm rojo">Cerrar</button-->
                <button type="button" onclick="save();" class="btn btn-sm rojo" <% if (boards.isEmpty()) out.print("disabled='disabled'"); %>>Guardar</button>
            </p>
	</form>
    </div>
</section>