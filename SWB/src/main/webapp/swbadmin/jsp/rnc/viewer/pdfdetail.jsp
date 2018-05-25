<%--
    Document   : pdfdetail.jsp
    Created on : 22/05/2018, 11:48:36 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.response.DateDocument, mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.resources.ArtDetail, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, java.util.ArrayList, java.util.List"%>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<%
    int pdfs = 0;
    int iEntry = 0;
    int iDigit = 1;
    String type = "";
    String title = "";
    String period = "";
    String creator = "";
    DigitalObject digital = null;
    List<Title> titles = new ArrayList<>();
    List<String> creators = new ArrayList<>();
    DateDocument datestart = new DateDocument();
    StringBuilder divVisor = new StringBuilder();
    StringBuilder scriptHeader = new StringBuilder();
    StringBuilder scriptCallVisor = new StringBuilder();
    List<DigitalObject> digitalobjects = new ArrayList<>();
    Entry entry = (Entry)request.getAttribute("entry");
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    WebSite site = paramRequest.getWebPage().getWebSite();
    if (null != entry) {
	iEntry = entry.getPosition();
	if (null != entry.getDigitalObject()) {
            creators = entry.getCreator();
            titles = entry.getRecordtitle();
            digitalobjects = entry.getDigitalObject();
            pdfs = null != digitalobjects ? digitalobjects.size() : 0;
            digital = pdfs >= iDigit ? digitalobjects.get(iDigit-1) : new DigitalObject();
            if (null != digital.getUrl() && digital.getUrl().endsWith(".pdf")) {
		scriptHeader.append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/style.css'/>");
                scriptHeader.append("<link rel='stylesheet' type='text/css' media='screen' href='/work/models/").append(site.getId()).append("/css/viewer-pdf.css'/>");
		divVisor.append("<div id=\"pdfdetail\"></div>");
		scriptCallVisor.append("<script type=\"text/javascript\">")
                    .append("   $(document).ready(function() {")
                    .append("       PDFObject.embed(\"").append(digital.getUrl()).append("\", \"#pdfdetail\");")
                    .append("   });")
                    .append("</script>");
		type = entry.getResourcetype().size() > 0 ? entry.getResourcetype().get(0) : "";
		datestart = entry.getPeriodcreated().getDatestart();
		creator = creators.size() > 0 ? creators.get(0) : "";
		period = null != datestart ? datestart.getValue() : "";
		if (!titles.isEmpty()) title = titles.get(0).getValue();
            }
        }
    }
    Integer records = (Integer)session.getAttribute("NUM_RECORDS_TOTAL");
%>
<%=scriptHeader%>
<script>
    function add(id) {
        dojo.xhrPost({
            url: '/swb/<%=site.getId()%>/favorito?id='+id,
            load: function(data) {
                dojo.byId('addCollection').innerHTML=data;
                    $('#addCollection').modal('show');
            }
        });
    }
    function loadDoc(id) {
	var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 200) {
                jQuery("#addCollection-tree").html(this.responseText);
                $("#addCollection" ).dialog( "open" );
            }else if (this.readyState == 4 && this.status == 403) {
                jQuery("#dialog-message-tree").text("Regístrate o inicia sesión para crear tus colecciones.");
                $("#dialog-message-tree" ).dialog( "open" );
            }
        };
        xhttp.open("GET", "/swb/<%=site.getId()%>/favorito?id="+id, true);
        xhttp.send();
    }
    function dismiss() {
        $("#addCollection" ).dialog( "close" );
    }
    function addnew(uri) {
	dismiss();
	dojo.xhrPost({
            url: uri,
            load: function(data) {
                dojo.byId('newCollection').innerHTML=data;
		$('#newCollection').modal('show');
            }
        });
    }
</script>
<section id="detalle">
    <div id="idetail" class="detalleimg">
	<div class="obranombre">
            <h3 class="oswB"><%=title%></h3>
            <p class="oswL"><%=creator%></p>
        </div>
        <div class="explora">
            <div class="explora2">
		<div class="explo1">
                    © <%=paramRequest.getLocaleString("usrmsg_view_detail_all_rights")%>
                </div>
		<div class="explo2 row">
                    <div class="col-3">
                        <span class="ion-social-facebook"></span>
                    </div>
                    <div class="col-3">
                        <span class="ion-social-twitter"></span>
                    </div>
                    <div class="col-6">
                        <a href="#" onclick="loadDoc('<%=entry.getId()%>');"><span class="ion-heart"></span></a> 3,995
                    </div>
                </div>
		<div class="explo3 row">
                    <div class="col-6">
                        <%
                            if (iEntry > 1) {
			%>
                                <span class="ion-chevron-left"></span> <%=paramRequest.getLocaleString("usrmsg_view_detail_prev_object")%>
			<%
                            }
			%>
                    </div>
                    <div class="col-6">
                        <%
                            if (iEntry < records) {
			%>
                                <%=paramRequest.getLocaleString("usrmsg_view_detail_next_object")%> <span class="ion-chevron-right"></span>
			<%
                            }
			%>
                    </div>
                </div>
            </div>
	</div>
	<%=divVisor%>
    </div>
</section>
<section id="detalleinfo">
    <div class="container">
	<div class="row">              
            <div class="col-12 col-sm-6  col-md-3 col-lg-3 order-md-1 order-sm-2 order-2 mascoleccion">
                <div>
                    <p class="tit2"><%=paramRequest.getLocaleString("usrmsg_view_detail_more_collection")%></p>
			<div>
                            <img src="/work/models/repositorio/img/agregado-01.jpg" class="img-responsive">
                            <p><%=paramRequest.getLocaleString("usrmsg_view_detail_name_work")%></p>
                            <p>Autor Lorem Ipsum</p>
			</div>
			<div>
                            <img src="/work/models/repositorio/img/agregado-02.jpg" class="img-responsive">
                            <p><%=paramRequest.getLocaleString("usrmsg_view_detail_name_work")%></p>
                            <p>Autor Lorem Ipsum</p>
			</div>
                        <hr>
			<p class="vermas"><a href="#"><%=paramRequest.getLocaleString("usrmsg_view_detail_show_more")%> <span class="ion-plus-circled"></span></a></p>
                </div>
            </div>
            <div class="col-12 col-sm-12 col-md-6 col-lg-6 order-md-2 order-sm-1 order-1 ficha ">
		<h3 class="oswM"><%=title%></h3>
                <% if (null != entry && null != entry.getDescription() && !entry.getDescription().isEmpty()) { %>
                    <p><%=entry.getDescription().get(0)%></p>
                <% } %>
                <hr>
                <p class="vermas"><a href="#"><%=paramRequest.getLocaleString("usrmsg_view_detail_show_more")%> <span class="ion-plus-circled"></span></a></p>
                <table>
                    <tr>
                        <th colspan="2"><%=paramRequest.getLocaleString("usrmsg_view_detail_data_sheet")%></th>
                    </tr>
                    <tr>
			<td><%=paramRequest.getLocaleString("usrmsg_view_detail_artist")%></td>
                        <td><%=creator%></td>
                    </tr>
                    <tr>
			<td><%=paramRequest.getLocaleString("usrmsg_view_detail_date")%></td>
                        <td><%=period%></td>
                    </tr>
                    <tr>
                        <td><%=paramRequest.getLocaleString("usrmsg_view_detail_type_object")%></td>
                        <td><%=type%></td>
                    </tr>
                    <tr>
			<td><%=paramRequest.getLocaleString("usrmsg_view_detail_identifier")%></td>
                        <td><%=entry.getIdentifiers()%></td>
                    </tr>
                    <tr>
                        <td><%=paramRequest.getLocaleString("usrmsg_view_detail_institution")%></td>
                        <td>Lorem ipsum</td>
                    </tr>
                    <tr>
			<td><%=paramRequest.getLocaleString("usrmsg_view_detail_technique")%></td>
                        <td>Lorem ipsum</td>
                    </tr>
                </table>
                <p class="vermas"><a href="#"><%=paramRequest.getLocaleString("usrmsg_view_detail_show_more")%> <span class="ion-plus-circled"></span></a></p>
            </div>
            <div class="col-12 col-sm-6  col-md-3 col-lg-3 order-md-3 order-sm-3 order-3 clave">
		<div class="redes">
                    <span class="ion-social-facebook"></span>
                    <span class="ion-social-twitter"></span>
                </div>
                <div>
                    <p class="tit2"><%=paramRequest.getLocaleString("usrmsg_view_detail_key_words")%></p>
                    <p>
                        <% 
                            int i = 0;
                            for (String key :  entry.getKeywords()) {
                                i++;
                                out.println("<a href=\"#\">"+key+"</a>");
                                if (i < entry.getKeywords().size()) out.println(" / ");
                            }
                        %>
                    </p>
                </div>
            </div>
        </div>
    </div>
</section>
<%=scriptCallVisor%>
<div id="dialog-message-tree" title="error">
    <p>
        <div id="dialog-text-tree"></div>
    </p>
</div>

<div id="dialog-success-tree" title="éxito">
    <p>
        <span class="ui-icon ui-icon-circle-check" style="float:left; margin:0 7px 50px 0;"></span>
	<div id="dialog-msg-tree"></div>
    </p>
</div>

<div id="addCollection">
    <p>
        <div id="addCollection-tree"></div>
    </p>
</div>

<div class="modal fade" id="newCollection" tabindex="-1" role="dialog" aria-labelledby="modalTitle" aria-hidden="true"></div>