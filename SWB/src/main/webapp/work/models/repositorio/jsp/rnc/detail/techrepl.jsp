<%--
    Document   : techrepl.jsp
    Created on : 29/10/2018, 12:33:40 PM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map, mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DateDocument, mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.resources.ArtDetail, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, java.util.ArrayList, java.util.List, java.util.Arrays, org.bson.Document"%>
<%
    String place = "";
    String fdesc = "";
    String title = "";
    String holder = "";
    String rights = "";
    String subtile = "";
    String urlholder = "";
    Document desc = null;
    boolean notNull = true;
    Entry entry = (Entry)request.getAttribute("entry");
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    WebSite site = paramRequest.getWebPage().getWebSite();
    String userLang = paramRequest.getUser().getLanguage();
    StringBuilder uri = new StringBuilder("<a href=\"/").append(userLang).append("/").append(site.getId()).append("/resultados?word=*&theme=");
    if (null != entry) {
        StringBuilder collection = new StringBuilder();
        if (null != entry.getGenerator()) {
            for (String t : entry.getGenerator()) {
                collection.append(t).append(", ");
            }
        }
        if (collection.length() > 0) collection.deleteCharAt(collection.length() - 2);
        title = Utils.getTitle(entry.getRecordtitle(), 0);
        subtile = " " + (Utils.c(entry.getNumber()) + " " + Utils.c(entry.getSubtile())).trim();
        desc = Utils.getDescription(entry.getDescription(), 0);
        fdesc = (null != desc && null !=desc.get("full")) ? Utils.replaceSpecialChars((String)desc.get("full")) : "";
        holder = Utils.replaceSpecialChars(Utils.getRowData(entry.getHolder(), 0, false));
        String holdernote = (null != entry.getHoldernote() && !entry.getHoldernote().isEmpty()) ? (", " + uri + Utils.replaceSpecialChars(entry.getHoldernote()) + "&filter=holder:" + Utils.replaceSpecialChars(entry.getHoldernote()) + "\">"+Utils.replaceSpecialChars(entry.getHoldernote())+"</a>") : "";
        urlholder = !holder.isEmpty() ? uri + holder + "&filter=holder:" + holder + "\">"+holder+"</a>"+holdernote : "";
        rights = Utils.concatFilter(userLang, site.getId(), "rights", Arrays.asList(Utils.getRights(entry)));
        String state = (null != entry.getState() && !entry.getState().isEmpty()) ? (" " + entry.getState()) : "";
        place = (null != entry.getLugar()) ? "<a href=\"/" + userLang + "/" + site.getId() + "/resultados?word="+ entry.getLugar() + "\">"+entry.getLugar()+state+"</a>" : "";
    }
    Map mapper = (Map)request.getAttribute("mapper");
%>
<a name="showPage"></a>
<h3 class="oswM"><%=Utils.replaceSpecialChars(title)%></h3>
<%  if (null != desc) { %>
        <p id="moredesc" style="display:block;"><%=fdesc%></p>
<%  }
    if (false && !fdesc.isEmpty()) { %>
        <p class="vermas"><a href="#showPage" onclick="moreDesc()"><%=paramRequest.getLocaleString("usrmsg_view_detail_show_more")%> <span class="ion-plus-circled"></span></a></p>
<%  } %>
<div class="tabla">
    <table>
        <tr>
            <th colspan="2"><%=Utils.replaceSpecialChars(paramRequest.getLocaleString("usrmsg_view_detail_data_sheet"))%></th>
        </tr>
        <%=Utils.getTechData(mapper, userLang, uri.toString())%>
        <%
            if (null != entry.getLang() && entry.getLang().size() > 0) {
                List<String> langs = new ArrayList<>();
                for (String ln : entry.getLang()) {
                    langs.add(Utils.replaceSpecialChars(ln));
                }
	%>
                <tr>
                    <td><%=paramRequest.getLocaleString("usrmsg_view_detail_lang")%></td>
                    <td><%=Utils.concatFilter(userLang, site.getId(), "lang", langs)%></td>
                </tr>
        <% } %>
	<%=Utils.getTechRepl("reccollection", holder, Utils.getRowData(entry.getReccollection(), 0, true), paramRequest.getLocaleString("usrmsg_view_detail_collection"), false, notNull)%>
        <%=Utils.getTechRepl("dimension", holder, entry.getDimension(), paramRequest.getLocaleString("usrmsg_view_detail_dimension"), false, notNull)%>
        <%=Utils.getTechRepl("invited", holder, entry.getInvited(), paramRequest.getLocaleString("usrmsg_view_detail_invited"), false, notNull)%>
        <%=Utils.getTechRepl("theme", holder, entry.getTheme(), paramRequest.getLocaleString("usrmsg_view_detail_theme"), false, notNull)%>
        <%=Utils.getTechRepl("lugar", holder, entry.getLugar(), paramRequest.getLocaleString("usrmsg_view_detail_place"), false, notNull)%>
        <%=Utils.getTechRepl("lugar+state", holder, place, paramRequest.getLocaleString("usrmsg_view_detail_place"), false, notNull)%>
        <%=Utils.getTechRepl("characters", holder, entry.getCharacters(), paramRequest.getLocaleString("usrmsg_view_detail_characters"), false, notNull)%>
        <%=Utils.getTechRepl("synopsis", holder, entry.getSynopsis(), paramRequest.getLocaleString("usrmsg_view_detail_synopsis"), false, notNull)%>
        <%=Utils.getTechRepl("technique", holder, entry.getTechnique(), paramRequest.getLocaleString("usrmsg_view_detail_techmaterial"), false, notNull)%>
        <%=Utils.getTechData("media", holder, Utils.concatReplace(userLang, site.getId(), "rightsmedia", Utils.getMedia(entry.getRights()), "rightsmedia:3D", "rightsmedia:3d"), paramRequest.getLocaleString("usrmsg_view_detail_media"), true, notNull)%>
        <%=Utils.getTechData("format", holder, Utils.getFormat(entry), paramRequest.getLocaleString("usrmsg_view_detail_format"), true, notNull)%>
        <%=Utils.getTechData("catalog", holder, entry.getCatalog(), paramRequest.getLocaleString("usrmsg_view_detail_catalog"), false, notNull)%>
        <%=Utils.getTechData("reference", holder, entry.getReference(), paramRequest.getLocaleString("usrmsg_view_detail_reference"), false, notNull)%>
        <%=Utils.getTechRepl("serie", holder, Utils.getRowData(entry.getSerie(), 0, true), paramRequest.getLocaleString("usrmsg_view_detail_serie"), false, notNull)%>
        <%=Utils.getTechRepl("credits", holder, Utils.getRowData(entry.getCredits(), 0, true), paramRequest.getLocaleString("usrmsg_view_detail_credits"), false, notNull)%>
        <%=Utils.getTechData("documentalfund", holder, entry.getDocumentalfund(), paramRequest.getLocaleString("usrmsg_view_detail_documentalfund"), false, notNull)%>
        <%=Utils.getTechData("episode", holder, entry.getEpisode(), paramRequest.getLocaleString("usrmsg_view_detail_episode"), false, notNull)%>
        <%=Utils.getTechData("direction", holder, entry.getDirection(), paramRequest.getLocaleString("usrmsg_view_detail_direction"), false, notNull)%>
        <%=Utils.getTechData("production", holder, entry.getProduction(), paramRequest.getLocaleString("usrmsg_view_detail_production"), false, notNull)%>
        <%=Utils.getTechData("music", holder, entry.getMusic(), paramRequest.getLocaleString("usrmsg_view_detail_music"), false, notNull)%>
        <%=Utils.getTechData("libreto", holder, entry.getLibreto(), paramRequest.getLocaleString("usrmsg_view_detail_libretto"), false, notNull)%>
        <%=Utils.getTechData("musicaldirection", holder, entry.getMusicaldirection(), paramRequest.getLocaleString("usrmsg_view_detail_music_direction"), false, notNull)%>
        <%=Utils.getTechData("subtile", holder, entry.getSubtile(), paramRequest.getLocaleString("usrmsg_view_detail_subtile"), false, notNull)%>
        <%=Utils.getTechData("editorial", holder, entry.getEditorial(), paramRequest.getLocaleString("usrmsg_view_detail_editorial"), false, notNull)%>
        <%=Utils.getTechData("director", holder, entry.getDirector(), paramRequest.getLocaleString("usrmsg_view_detail_director"), false, notNull)%>
        <%=Utils.getTechData("producer", holder, entry.getProducer(), paramRequest.getLocaleString("usrmsg_view_detail_producer"), false, notNull)%>
        <%=Utils.getTechData("screenplay", holder, entry.getScreenplay(), paramRequest.getLocaleString("usrmsg_view_detail_screenplay"), false, notNull)%>
        <%=Utils.getTechData("distribution", holder, entry.getDistribution(), paramRequest.getLocaleString("usrmsg_view_detail_distribution"), false, notNull)%>
        <%=Utils.getTechData("clasification", holder, entry.getClasification(), paramRequest.getLocaleString("usrmsg_view_detail_clasification"), false, notNull)%>
        <%=Utils.getTechData("category", holder, entry.getCategory(), paramRequest.getLocaleString("usrmsg_view_detail_category"), false, notNull)%>
        <%=Utils.getTechData("subcategory", holder, entry.getSubcategory(), paramRequest.getLocaleString("usrmsg_view_detail_subcategory"), false, notNull)%>
        <%=Utils.getTechData("press", holder, entry.getPress(), paramRequest.getLocaleString("usrmsg_view_detail_press"), false, notNull)%>
        <%=Utils.getTechData("period", holder, entry.getPeriod(), paramRequest.getLocaleString("usrmsg_view_detail_period"), false, notNull)%>
        <%=Utils.getTechData("availableformats", holder, entry.getAvailableformats(), paramRequest.getLocaleString("usrmsg_view_detail_availableformats"), false, notNull)%>
        <%=Utils.getTechData("hiperonimo", holder, entry.getHiperonimo(), paramRequest.getLocaleString("usrmsg_view_detail_hiperonimo"), false, notNull)%>
        <%=Utils.getTechData("curaduria", holder, entry.getCuraduria(), paramRequest.getLocaleString("usrmsg_view_detail_curaduria"), false, notNull)%>
        <%=Utils.getTechData("inscripcionobra", holder, entry.getInscripcionobra(), paramRequest.getLocaleString("usrmsg_view_detail_inscripcionobra"), false, notNull)%>
        <%=Utils.getTechData("cultura", holder, entry.getCultura(), paramRequest.getLocaleString("usrmsg_view_detail_cultura"), false, notNull)%>
        <%=Utils.getTechData("origin", holder, entry.getOrigin(), paramRequest.getLocaleString("usrmsg_view_detail_origin"), false, notNull)%>
        <%=Utils.getTechData("culturalregion", holder, entry.getCulturalregion(), paramRequest.getLocaleString("usrmsg_view_detail_cultural_region"), false, notNull)%>
        <%=Utils.getTechData("acervo", holder, entry.getAcervo(), paramRequest.getLocaleString("usrmsg_view_detail_acervo"), false, notNull)%>
        <%=Utils.getTechData("creator+creatorgroup+dimension", holder, Utils.getRowData(entry.getCreatorgroup(), 0, true), paramRequest.getLocaleString("usrmsg_view_detail_creator_group"), false, notNull)%>
        <%=Utils.getTechData("gprlang", holder, entry.getGprlang(), paramRequest.getLocaleString("usrmsg_view_detail_gprlang"), false, notNull)%>
        <%=Utils.getTechData("genre", holder, entry.getGenre(), paramRequest.getLocaleString("usrmsg_view_detail_genre"), false, notNull)%>
        <%=Utils.getTechData("movs", holder, entry.getMovs(), paramRequest.getLocaleString("usrmsg_view_detail_movs"), false, notNull)%>
        <%=Utils.getTechData("acts", holder, entry.getActs(), paramRequest.getLocaleString("usrmsg_view_detail_acts"), false, notNull)%>
        <%=Utils.getTechData("clase", holder, entry.getClase(), paramRequest.getLocaleString("usrmsg_view_detail_clase"), false, notNull)%>
        <%=Utils.getTechData("observations", holder, entry.getObservations(), paramRequest.getLocaleString("usrmsg_view_detail_observations"), false, notNull)%>
        <%=Utils.getTechData("biccustodyentity", holder, entry.getBiccustodyentity(), paramRequest.getLocaleString("usrmsg_view_detail_biccustodyentity"), false, notNull)%>
        <%=Utils.getTechData("rights.rightstitle", holder, rights, paramRequest.getLocaleString("usrmsg_view_detail_rights"), true, notNull)%>
        <%
            if (null != entry.getDigitalObject() && !entry.getDigitalObject().isEmpty() && null != entry.getRights() && null != entry.getRights().getDescription()) {
                String url = "";
		if (entry.getDigitalObject().get(0).getRights().getUrl().startsWith("http")) url = "<a href='" + entry.getDigitalObject().get(0).getRights().getUrl() + "'>";
	%>
                <tr>
                    <td><%=Utils.replaceSpecialChars(paramRequest.getLocaleString("usrmsg_view_detail_use_statement"))%></td>
                    <td><%=url%><%=Utils.replaceSpecialChars(entry.getRights().getDescription())%></a></td>
                </tr>
	<%
            }
	%>
        <%=Utils.getTechData("oaiid/identifier", holder, entry.getIdentifiers(), paramRequest.getLocaleString("usrmsg_view_detail_identifier"), true, notNull)%>
    </table>
</div>