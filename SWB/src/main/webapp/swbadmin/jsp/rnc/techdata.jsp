<%--
    Document   : techdata.jsp
    Created on : 15/06/2018, 14:15:08 AM
    Author     : sergio.tellez
--%>
<%@page import="java.util.Arrays"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DateDocument, mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.resources.ArtDetail, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, 
    org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, java.util.ArrayList, java.util.List, org.bson.Document"%>
<%
    String fdesc = "";
    String title = "";
    String place = "";
    String holder = "";
    String rights = "";
    String subtile = "";
    String urlholder = "";
    Document desc = null;
    List<Title> titles = new ArrayList<>();
    Entry entry = (Entry)request.getAttribute("entry");
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    WebSite site = paramRequest.getWebPage().getWebSite();
    String userLang = paramRequest.getUser().getLanguage();
    StringBuilder uri = new StringBuilder("<a href=\"/").append(userLang).append("/").append(site.getId()).append("/resultados?word=*&theme=");
    if (null != entry) {
        titles = entry.getRecordtitle();
        StringBuilder collection = new StringBuilder();
        if (null != entry.getGenerator()) {
            for (String t : entry.getGenerator()) {
                collection.append(t).append(", ");
            }
        }
	if (collection.length() > 0) collection.deleteCharAt(collection.length() - 2);
	if (!titles.isEmpty()) title = titles.get(0).getValue();
        title = Utils.getTitle(titles, 0);
        subtile = " " + (Utils.c(entry.getNumber()) + " " + Utils.c(entry.getSubtile())).trim();
        desc = Utils.getDescription(entry.getDescription(), 0);
	fdesc = (null != desc && null !=desc.get("full")) ? (String)desc.get("full") : "";
        holder = Utils.getRowData(entry.getHolder(), 0, false);
        String holdernote = (null != entry.getHoldernote() && !entry.getHoldernote().isEmpty()) ? (" " + entry.getHoldernote()) : "";
        urlholder = !holder.isEmpty() ? uri + holder + "&filter=holder:" + holder + "\">"+holder+holdernote+"</a>" : "";
        rights = Utils.concatFilter(userLang, site.getId(), "rights", Arrays.asList(Utils.getRights(entry)));
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
            <%=Utils.getTechData("recordtitle", holder, title+subtile, paramRequest.getLocaleString("usrmsg_view_detail_title"), true, true)%>
            <%=Utils.getTechData("creator", holder, Utils.getCreator(entry.getCreator()), paramRequest.getLocaleString("usrmsg_view_detail_artist"), true, true)%>
            <%=Utils.getTechData("datecreated", holder, null != entry.getDatecreated() ? entry.getDatecreated().getValue() : "", paramRequest.getLocaleString("usrmsg_view_detail_date"), true, true)%>
            <%=Utils.getTechData("resourcetype", holder, Utils.concatFilter(userLang, site.getId(), "resourcetype", Utils.getList(entry.getBictype())), paramRequest.getLocaleString("usrmsg_view_detail_type_object"), false, false)%>
            <%=Utils.getTechData("resourcetype", holder, Utils.concatFilter(userLang, site.getId(), "resourcetype", entry.getResourcetype()), paramRequest.getLocaleString("usrmsg_view_detail_type_object"), true, true)%>
            <%=Utils.getTechData("holder", holder, urlholder, paramRequest.getLocaleString("usrmsg_view_detail_institution"), true, true)%>
            <%=Utils.getTechData("publisher", holder, entry.getPublisher(), paramRequest.getLocaleString("usrmsg_view_detail_publisher"), false, true)%>
            <%=Utils.getTechData("invited", holder, entry.getInvited(), paramRequest.getLocaleString("usrmsg_view_detail_invited"), false, true)%>
            <%=Utils.getTechData("theme", holder, entry.getTheme(), paramRequest.getLocaleString("usrmsg_view_detail_theme"), false, true)%>
            <%=Utils.getTechData("synopsis", holder, entry.getSynopsis(), paramRequest.getLocaleString("usrmsg_view_detail_synopsis"), false, true)%>
            <% if (null !=  entry.getGenerator() && !entry.getGenerator().isEmpty()) { out.println(Utils.getTechData("generator", holder, Utils.concatLink(userLang, site.getId(), true, entry.getGenerator().toArray(new String[0])), paramRequest.getLocaleString("usrmsg_view_detail_collection"), true, true)); } %>
            <%=Utils.getTechData("rights.rightstitle", holder, rights, paramRequest.getLocaleString("usrmsg_view_detail_rights"), true, true)%>
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
                        <td><%=Utils.concatFilter(userLang, site.getId(), "lang", entry.getLang())%></td>
                    </tr>
            <%
                }
            %>
            <%=Utils.getTechData("resourcetype", holder, Utils.concatFilter(userLang, site.getId(), "resourcetype", entry.getResourcetype()), paramRequest.getLocaleString("usrmsg_view_detail_type_object"), false, true)%>
            <%=Utils.getTechData("lugar", holder, entry.getLugar(), paramRequest.getLocaleString("usrmsg_view_detail_place"), false, true)%>
            <%=Utils.getTechData("reference", holder, entry.getReference(), paramRequest.getLocaleString("usrmsg_view_detail_reference"), false, true)%>
            <%=Utils.getTechData("reccollection", holder, Utils.getRowData(entry.getReccollection(), 0, true), paramRequest.getLocaleString("usrmsg_view_detail_collection"), false, true)%>
            <%=Utils.getTechData("dimension", holder, entry.getDimension(), paramRequest.getLocaleString("usrmsg_view_detail_dimension"), false, true)%>
            <%=Utils.getTechData("unidad", holder, entry.getUnidad(), paramRequest.getLocaleString("usrmsg_view_detail_unit"), false, true)%>
            <%=Utils.getTechData("serie", holder, Utils.getRowData(entry.getSerie(), 0, true), paramRequest.getLocaleString("usrmsg_view_detail_serie"), false, true)%>
            <%=Utils.getTechData("chapter", holder, entry.getChapter(), paramRequest.getLocaleString("usrmsg_view_detail_chapter"), false, true)%>
            <%=Utils.getTechData("credits", holder, Utils.getRowData(entry.getCredits(), 0, true), paramRequest.getLocaleString("usrmsg_view_detail_credits"), false, true)%>
            <%=Utils.getTechData("format", holder, Utils.getFormat(entry), paramRequest.getLocaleString("usrmsg_view_detail_availableformats"), false, true)%>
            <%=Utils.getTechData("availableformats", holder, entry.getAvailableformats(), paramRequest.getLocaleString("usrmsg_view_detail_availableformats"), false, true)%>
            <%=Utils.getTechData("documentalfund", holder, entry.getDocumentalfund(), paramRequest.getLocaleString("usrmsg_view_detail_documentalfund"), false, true)%>
            <%=Utils.getTechData("episode", holder, entry.getEpisode(), paramRequest.getLocaleString("usrmsg_view_detail_episode"), false, true)%>
            <%=Utils.getTechData("direction", holder, entry.getDirection(), paramRequest.getLocaleString("usrmsg_view_detail_direction"), false, true)%>
            <%=Utils.getTechData("production", holder, entry.getProduction(), paramRequest.getLocaleString("usrmsg_view_detail_production"), false, true)%>
            <%=Utils.getTechData("music", holder, entry.getMusic(), paramRequest.getLocaleString("usrmsg_view_detail_music"), false, true)%>
            <%=Utils.getTechData("libreto", holder, entry.getLibreto(), paramRequest.getLocaleString("usrmsg_view_detail_libretto"), false, true)%>
            <%=Utils.getTechData("musicaldirection", holder, entry.getMusicaldirection(), paramRequest.getLocaleString("usrmsg_view_detail_music_direction"), false, true)%>
            <%=Utils.getTechData("subtile", holder, entry.getSubtile(), paramRequest.getLocaleString("usrmsg_view_detail_subtile"), false, true)%>
            <%=Utils.getTechData("editorial", holder, entry.getEditorial(), paramRequest.getLocaleString("usrmsg_view_detail_editorial"), false, true)%>
            <%=Utils.getTechData("lugar+state", holder, place, paramRequest.getLocaleString("usrmsg_view_detail_place"), false, true)%>
            <%=Utils.getTechData("dimension+tipo_de_dimension", holder, Utils.c(entry.getDimension()) + " " + Utils.c(entry.getTipo_de_dimension()), paramRequest.getLocaleString("usrmsg_view_detail_dimension"), false, true)%>
            <%=Utils.getTechData("unidad+tipo_de_unidad", holder, Utils.c(entry.getUnidad()) + " " + Utils.c(entry.getTipo_de_unidad()), paramRequest.getLocaleString("usrmsg_view_detail_unit"), false, true)%>
            <%=Utils.getTechData("dimension+unidad", holder, Utils.concatLink(userLang, site.getId(), false, entry.getDimension(), entry.getUnidad()), paramRequest.getLocaleString("usrmsg_view_detail_dimension"), false, true)%>
            <%=Utils.getTechData("director", holder, entry.getDirector(), paramRequest.getLocaleString("usrmsg_view_detail_director"), false, true)%>
            <%=Utils.getTechData("producer", holder, entry.getProducer(), paramRequest.getLocaleString("usrmsg_view_detail_producer"), false, true)%>
            <%=Utils.getTechData("screenplay", holder, entry.getScreenplay(), paramRequest.getLocaleString("usrmsg_view_detail_screenplay"), false, true)%>
            <%=Utils.getTechData("distribution", holder, entry.getDistribution(), paramRequest.getLocaleString("usrmsg_view_detail_distribution"), false, true)%>
            <%=Utils.getTechData("clasification", holder, entry.getClasification(), paramRequest.getLocaleString("usrmsg_view_detail_clasification"), false, true)%>
            <%=Utils.getTechData("characters", holder, entry.getCharacters(), paramRequest.getLocaleString("usrmsg_view_detail_characters"), false, true)%>
            <%=Utils.getTechData("press", holder, entry.getPress(), paramRequest.getLocaleString("usrmsg_view_detail_press"), false, true)%>
            <%=Utils.getTechData("category", holder, entry.getCategory(), paramRequest.getLocaleString("usrmsg_view_detail_category"), false, true)%>
            <%=Utils.getTechData("subcategory", holder, entry.getSubcategory(), paramRequest.getLocaleString("usrmsg_view_detail_subcategory"), false, true)%>
            <%=Utils.getTechData("period", holder, entry.getPeriod(), paramRequest.getLocaleString("usrmsg_view_detail_period"), false, true)%>
            <%=Utils.getTechData("technique", holder, entry.getTechnique(), paramRequest.getLocaleString("usrmsg_view_detail_techmaterial"), false, true)%>
            <%=Utils.getTechData("techmaterial", holder, entry.getTechmaterial(), paramRequest.getLocaleString("usrmsg_view_detail_techmaterial"), false, true)%>
            <%=Utils.getTechData("catalog", holder, entry.getCatalog(), paramRequest.getLocaleString("usrmsg_view_detail_catalog"), false, true)%>
            <%=Utils.getTechData("media", holder, Utils.concatFilter(userLang, site.getId(), "rightsmedia", Utils.getMedia(entry.getRights())), paramRequest.getLocaleString("usrmsg_view_detail_media"), false, true)%>
            <%=Utils.getTechData("hiperonimo", holder, entry.getHiperonimo(), paramRequest.getLocaleString("usrmsg_view_detail_hiperonimo"), false, true)%>
            <%=Utils.getTechData("curaduria", holder, entry.getCuraduria(), paramRequest.getLocaleString("usrmsg_view_detail_curaduria"), false, true)%>
            <%=Utils.getTechData("inscripcionobra", holder, entry.getInscripcionobra(), paramRequest.getLocaleString("usrmsg_view_detail_inscripcionobra"), false, true)%>
            <%=Utils.getTechData("cultura", holder, entry.getCultura(), paramRequest.getLocaleString("usrmsg_view_detail_cultura"), false, true)%>
            <%=Utils.getTechData("origin", holder, entry.getOrigin(), paramRequest.getLocaleString("usrmsg_view_detail_origin"), false, true)%>
            <%=Utils.getTechData("culturalregion", holder, entry.getCulturalregion(), paramRequest.getLocaleString("usrmsg_view_detail_cultural_region"), false, true)%>
            <%=Utils.getTechData("acervo", holder, entry.getAcervo(), paramRequest.getLocaleString("usrmsg_view_detail_acervo"), false, true)%>
            <%=Utils.getTechData("discipline", holder, entry.getDiscipline(), paramRequest.getLocaleString("usrmsg_view_detail_discipline"), false, true)%>
            <%=Utils.getTechData("creatorgroup", holder, Utils.getRowData(entry.getCreatorgroup(), 0, true), paramRequest.getLocaleString("usrmsg_view_detail_creator_group"), false, true)%>
            <%=Utils.getTechData("creator+creatorgroup+dimension", holder, Utils.getRowData(entry.getCreatorgroup(), 0, true), paramRequest.getLocaleString("usrmsg_view_detail_creator_group"), false, true)%>
            <%=Utils.getTechData("material", holder, entry.getMaterial(), paramRequest.getLocaleString("usrmsg_view_detail_material"), false, true)%>
            <%=Utils.getTechData("gprlang", holder, entry.getGprlang(), paramRequest.getLocaleString("usrmsg_view_detail_gprlang"), false, true)%>
            <%=Utils.getTechData("genre", holder, entry.getGenre(), paramRequest.getLocaleString("usrmsg_view_detail_genre"), false, true)%>
            <%=Utils.getTechData("movs", holder, entry.getMovs(), paramRequest.getLocaleString("usrmsg_view_detail_movs"), false, true)%>
            <%=Utils.getTechData("acts", holder, entry.getActs(), paramRequest.getLocaleString("usrmsg_view_detail_acts"), false, true)%>
            <%=Utils.getTechData("clase", holder, entry.getClase(), paramRequest.getLocaleString("usrmsg_view_detail_clase"), false, true)%>
            <%=Utils.getTechData("observations", holder, entry.getObservations(), paramRequest.getLocaleString("usrmsg_view_detail_observations"), false, true)%>
            <%=Utils.getTechData("biccustodyentity", holder, entry.getBiccustodyentity(), paramRequest.getLocaleString("usrmsg_view_detail_biccustodyentity"), false, true)%>
            
            <%=Utils.getTechData("collectionnote", holder, entry.getCollectionnote(), paramRequest.getLocaleString("usrmsg_view_detail_collection_note"), false, true)%>
            <%=Utils.getTechData("oaiid/identifier", holder, entry.getIdentifiers(), paramRequest.getLocaleString("usrmsg_view_detail_identifier"), true, true)%>
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