<%--
    Document   : techdata.jsp
    Created on : 15/06/2018, 14:15:08 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DateDocument, mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.resources.ArtDetail, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, 
    org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, java.util.ArrayList, java.util.List, org.bson.Document"%>
<%
    String type = "";
    String fdesc = "";
    String title = "";
    String place = "";
    String holder = "";
    Document desc = null;
    String rights = "";
    String urlholder = "";
    List<Title> titles = new ArrayList<>();
    Entry entry = (Entry)request.getAttribute("entry");
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    WebSite site = paramRequest.getWebPage().getWebSite();
    String userLang = paramRequest.getUser().getLanguage();
    if (null != entry) {
        //holders = entry.getHolder();
        titles = entry.getRecordtitle();
	StringBuilder builder = new StringBuilder();
        StringBuilder collection = new StringBuilder();
        for (String t : entry.getResourcetype()) {
            builder.append("<a href=\"/").append(userLang).append("/").append(site.getId()).append("/resultados?word=").append(t).append("\">").append(t).append("</a>").append(", ");
        }
	if (builder.length() > 0) builder.deleteCharAt(builder.length() - 2);
        type =  builder.toString();
        if (null != entry.getGenerator()) {
            for (String t : entry.getGenerator()) {
                collection.append(t).append(", ");
            }
        }
	if (collection.length() > 0) collection.deleteCharAt(collection.length() - 2);
        //generator = Utils.getRowData(entry.getGenerator(), 0, true);
	//creator = Utils.getRowData(entry.getCreator(), 0, true);
        //period = null != entry.getDatecreated() ? Utils.esDate(entry.getDatecreated().getValue()) : "";
	if (!titles.isEmpty()) title = titles.get(0).getValue();
        //rights = Utils.getRights(entry);
        title = Utils.getTitle(titles, 0);
        desc = Utils.getDescription(entry.getDescription());
        fdesc = (null != desc && null != desc.get("full")) ? (String)desc.get("full") : "";
        
        holder = Utils.getRowData(entry.getHolder(), 0, false);
        String holdernote = (null != entry.getHoldernote() && !entry.getHoldernote().isEmpty()) ? (" " + entry.getHoldernote()) : "";
        urlholder = !holder.isEmpty() ? "<a href=\"/" + userLang + "/" + site.getId() + "/resultados?word="+ holder + "\">"+holder+holdernote+"</a>" : "";
        String rht = Utils.getRights(entry);
        rights = !rht.isEmpty() ? "<a href=\"/" + userLang + "/" + site.getId() + "/resultados?word="+ rht + "\">"+rht+"</a>" : "";
        String state = (null != entry.getState() && !entry.getState().isEmpty()) ? (" " + entry.getState()) : "";
        place = (null != entry.getLugar()) ? "<a href=\"/" + userLang + "/" + site.getId() + "/resultados?word="+ entry.getLugar() + "\">"+entry.getLugar()+state+"</a>" : "";
    }
%>
<div class="col-12 col-sm-12 col-md-9 col-lg-9 order-md-2 order-sm-1 order-1 ficha ">
    <h3 class="oswM"><%=title%></h3>
    <%  if (null != desc) { %>
            <a name="showPage"></a>
            <!--p id="shortdesc"><%=desc.get("short")%></p-->
            <p id="moredesc" style="display:block;"><%=fdesc%></p>
            <!--hr-->
    <% } if (false && !fdesc.isEmpty()) { %>
	<p class="vermas"><a href="#showPage" onclick="moreDesc()"><%=paramRequest.getLocaleString("usrmsg_view_detail_show_more")%> <span class="ion-plus-circled"></span></a></p>
    <% } %>
    <div class="tabla">
        <table>
            <tr>
                <th colspan="2"><%=paramRequest.getLocaleString("usrmsg_view_detail_data_sheet")%></th>
            </tr>
            <%=Utils.getTechData("creator", holder, Utils.getRowData(entry.getCreator(), 0, true), paramRequest.getLocaleString("usrmsg_view_detail_artist"), true)%>
            <%=Utils.getTechData("datecreated", holder, null != entry.getDatecreated() ? Utils.esDate(entry.getDatecreated().getValue()) : "", paramRequest.getLocaleString("usrmsg_view_detail_date"), true)%>
            <%=Utils.getTechData("resourcetype", holder, type, paramRequest.getLocaleString("usrmsg_view_detail_type_object"), true)%>
            <%=Utils.getTechData("oaiid/identifier", holder, entry.getIdentifiers(), paramRequest.getLocaleString("usrmsg_view_detail_identifier"), true)%>
            <%=Utils.getTechData("holder", holder, urlholder, paramRequest.getLocaleString("usrmsg_view_detail_institution"), true)%>
            <%=Utils.getTechData("reccollection", holder, Utils.getRowData(entry.getGenerator(), 0, true), paramRequest.getLocaleString("usrmsg_view_detail_collection"), true)%>
            <%=Utils.getTechData("rights.rightstitle", holder, rights, paramRequest.getLocaleString("usrmsg_view_detail_rights"), true)%>
            <%
                if (null != entry.getDigitalObject() && !entry.getDigitalObject().isEmpty() && null != entry.getRights() && null != entry.getRights().getDescription()) {
                    String url = "";
                    if (entry.getDigitalObject().get(0).getRights().getUrl().startsWith("http")) url = "<a href='" + entry.getDigitalObject().get(0).getRights().getUrl() + "'>";
            %>
                        <tr>
                            <td><%=paramRequest.getLocaleString("usrmsg_view_detail_use_statement")%></td>
                            <td><%=url%><%=entry.getRights().getDescription()%></a></td>
                        </tr>
            <%
                }
                if (null != entry.getLang() && entry.getLang().size() > 2) {
            %>
                    <tr>
                        <td><%=paramRequest.getLocaleString("usrmsg_view_detail_lang")%></td>
                        <% if (entry.getLang().size() == 4) {
                            out.println("<td><a href=\"/" + userLang + "/" + site.getId() + "/resultados?word="+ entry.getLang().get(2) + "\">"+entry.getLang().get(2)+"</a>, ");
                            out.println("<a href=\"/" + userLang + "/" + site.getId() + "/resultados?word="+ entry.getLang().get(3) + "\">"+entry.getLang().get(3)+"</a></td>");
                        %>
                        <% } else if (entry.getLang().size() == 3) {%>
                        <td><%=entry.getLang().get(1)%>, <%=entry.getLang().get(2)%></td>
                        <% } %>
                    </tr>
            <%
                }
            %>
            <%=Utils.getTechData("lugar", holder, entry.getLugar(), paramRequest.getLocaleString("usrmsg_view_detail_place"), false)%>
            <%=Utils.getTechData("reference", holder, entry.getReference(), paramRequest.getLocaleString("usrmsg_view_detail_reference"), false)%>
            <%=Utils.getTechData("reccollection", holder, Utils.getRowData(entry.getReccollection(), 0, true), paramRequest.getLocaleString("usrmsg_view_detail_collection"), false)%>
            <%=Utils.getTechData("dimension", holder, entry.getDimension(), paramRequest.getLocaleString("usrmsg_view_detail_dimension"), false)%>
            <%=Utils.getTechData("unidad", holder, entry.getUnidad(), paramRequest.getLocaleString("usrmsg_view_detail_unit"), false)%>
            <%=Utils.getTechData("serie", holder, Utils.getRowData(entry.getSerie(), 0, true), paramRequest.getLocaleString("usrmsg_view_detail_serie"), false)%>
            <%=Utils.getTechData("chapter", holder, entry.getChapter(), paramRequest.getLocaleString("usrmsg_view_detail_chapter"), false)%>
            <%=Utils.getTechData("credits", holder, Utils.getRowData(entry.getCredits(), 0, true), paramRequest.getLocaleString("usrmsg_view_detail_credits"), false)%>
            <%=Utils.getTechData("availableformats", holder, entry.getAvailableformats(), paramRequest.getLocaleString("usrmsg_view_detail_availableformats"), false)%>
            <%=Utils.getTechData("documentalfund", holder, entry.getDocumentalfund(), paramRequest.getLocaleString("usrmsg_view_detail_documentalfund"), false)%>
            <%=Utils.getTechData("episode", holder, entry.getEpisode(), paramRequest.getLocaleString("usrmsg_view_detail_episode"), false)%>
            <%=Utils.getTechData("direction", holder, entry.getDirection(), paramRequest.getLocaleString("usrmsg_view_detail_direction"), false)%>
            <%=Utils.getTechData("production", holder, entry.getProduction(), paramRequest.getLocaleString("usrmsg_view_detail_production"), false)%>
            <%=Utils.getTechData("music", holder, entry.getMusic(), paramRequest.getLocaleString("usrmsg_view_detail_music"), false)%>
            <%=Utils.getTechData("libreto", holder, entry.getLibreto(), paramRequest.getLocaleString("usrmsg_view_detail_libretto"), false)%>
            <%=Utils.getTechData("musicaldirection", holder, entry.getMusicaldirection(), paramRequest.getLocaleString("usrmsg_view_detail_music_direction"), false)%>
            <%=Utils.getTechData("number", holder, entry.getNumber(), paramRequest.getLocaleString("usrmsg_view_detail_number"), false)%>
            <%=Utils.getTechData("subtile", holder, entry.getSubtile(), paramRequest.getLocaleString("usrmsg_view_detail_subtile"), false)%>
            <%=Utils.getTechData("editorial", holder, entry.getEditorial(), paramRequest.getLocaleString("usrmsg_view_detail_editorial"), false)%>
            <%=Utils.getTechData("lugar+state", holder, place, paramRequest.getLocaleString("usrmsg_view_detail_place"), false)%>
            <%=Utils.getTechData("dimension+tipo_de_dimension", holder, Utils.c(entry.getDimension()) + " " + Utils.c(entry.getTipo_de_dimension()), paramRequest.getLocaleString("usrmsg_view_detail_dimension"), false)%>
            <%=Utils.getTechData("unidad+tipo_de_unidad", holder, Utils.c(entry.getUnidad()) + " " + Utils.c(entry.getTipo_de_unidad()), paramRequest.getLocaleString("usrmsg_view_detail_unit"), false)%>
            <%=Utils.getTechData("dimension+unidad", holder, Utils.concatLink(userLang, site.getId(), entry.getDimension(), entry.getUnidad()), paramRequest.getLocaleString("usrmsg_view_detail_dimension"), false)%>
            <%=Utils.getTechData("director", holder, entry.getDirector(), paramRequest.getLocaleString("usrmsg_view_detail_director"), false)%>
            <%=Utils.getTechData("producer", holder, entry.getProducer(), paramRequest.getLocaleString("usrmsg_view_detail_producer"), false)%>
            <%=Utils.getTechData("screenplay", holder, entry.getScreenplay(), paramRequest.getLocaleString("usrmsg_view_detail_screenplay"), false)%>
            <%=Utils.getTechData("distribution", holder, entry.getDistribution(), paramRequest.getLocaleString("usrmsg_view_detail_distribution"), false)%>
            <%=Utils.getTechData("press", holder, entry.getPress(), paramRequest.getLocaleString("usrmsg_view_detail_press"), false)%>
            <%=Utils.getTechData("period", holder, entry.getPeriod(), paramRequest.getLocaleString("usrmsg_view_detail_period"), false)%>
            <%=Utils.getTechData("techmaterial", holder, entry.getTechmaterial(), paramRequest.getLocaleString("usrmsg_view_detail_techmaterial"), false)%>
            <%=Utils.getTechData("hiperonimo", holder, entry.getHiperonimo(), paramRequest.getLocaleString("usrmsg_view_detail_hiperonimo"), false)%>
            <%=Utils.getTechData("curaduria", holder, entry.getCuraduria(), paramRequest.getLocaleString("usrmsg_view_detail_curaduria"), false)%>
            <%=Utils.getTechData("inscripcionobra", holder, entry.getInscripcionobra(), paramRequest.getLocaleString("usrmsg_view_detail_inscripcionobra"), false)%>
            <%=Utils.getTechData("cultura", holder, entry.getCultura(), paramRequest.getLocaleString("usrmsg_view_detail_cultura"), false)%>
            <%=Utils.getTechData("origin", holder, entry.getOrigin(), paramRequest.getLocaleString("usrmsg_view_detail_origin"), false)%>
            <%=Utils.getTechData("acervo", holder, entry.getAcervo(), paramRequest.getLocaleString("usrmsg_view_detail_acervo"), false)%>
            <%=Utils.getTechData("discipline", holder, entry.getDiscipline(), paramRequest.getLocaleString("usrmsg_view_detail_discipline"), false)%>
            <%=Utils.getTechData("creator+creatorgroup+dimension", holder, Utils.getRowData(entry.getCreatorgroup(), 0, true), paramRequest.getLocaleString("usrmsg_view_detail_creator_group"), false)%>
            <%=Utils.getTechData("material", holder, entry.getMaterial(), paramRequest.getLocaleString("usrmsg_view_detail_material"), false)%>
            <%=Utils.getTechData("collectionnote", holder, entry.getCollectionnote(), paramRequest.getLocaleString("usrmsg_view_detail_collection_note"), false)%>
        </table>
        <table class="collapse" id="vermas-ficha">
        </table>
        <!--p class="vermas-filtros">
            <button class="btn-vermas" type="button" data-toggle="collapse" data-target="#vermas-ficha" aria-expanded="false" aria-controls="vermas">
                <span class="ion-plus-circled"> <%=paramRequest.getLocaleString("usrmsg_view_detail_show_more")%></span>
		<span class="ion-minus-circled"> <%=paramRequest.getLocaleString("usrmsg_view_detail_show_less")%></span>
            </button>
        </p-->
    </div>
</div>
<script>
    function moreDesc() {
        var x = document.getElementById("moredesc");
	var s = document.getElementById("shortdesc");
        if (x.style.display === "none") {
            x.style.display = "block";
            s.style.display = "none";
	} else {
            x.style.display = "none";
            s.style.display = "block";
	}
    }
    function moreCompl() {
        var y = document.getElementById("morecompl");
        if (y.style.display === "none") {
            y.style.display = "block";
        }else {
            y.style.display = "none";
        }
    }
</script>