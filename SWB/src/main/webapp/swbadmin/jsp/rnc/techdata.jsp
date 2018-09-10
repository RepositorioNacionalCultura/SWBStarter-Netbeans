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
    //String type = "";
    String fdesc = "";
    String title = "";
    String holder = "";
    //String period = "";
    //String rights = "";
    //String creator = "";
    //String generator = "";
    List<Title> titles = new ArrayList<>();
    //List<String> holders = new ArrayList<>();
    Entry entry = (Entry)request.getAttribute("entry");
    Document desc = null;
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    if (null != entry) {
        //holders = entry.getHolder();
        titles = entry.getRecordtitle();
	StringBuilder builder = new StringBuilder();
        StringBuilder collection = new StringBuilder();
        for (String t : entry.getResourcetype()) {
            builder.append(t).append(", ");
        }
	if (builder.length() > 0) builder.deleteCharAt(builder.length() - 2);
        //type =  builder.toString();
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
    }
%>
<div class="col-12 col-sm-12 col-md-9 col-lg-9 order-md-2 order-sm-1 order-1 ficha ">
    <h3 class="oswM"><%=title%></h3>
    <%  if (null != desc) { %>
            <a name="showPage"></a>
            <p id="shortdesc"><%=desc.get("short")%></p>
            <p id="moredesc" style="display:none;"><%=fdesc%></p>
            <hr>
    <% } if (!fdesc.isEmpty()) { %>
	<p class="vermas"><a href="#showPage" onclick="moreDesc()"><%=paramRequest.getLocaleString("usrmsg_view_detail_show_more")%> <span class="ion-plus-circled"></span></a></p>
    <% } %>
    <div class="tabla">
        <table>
            <tr>
                <th colspan="2"><%=paramRequest.getLocaleString("usrmsg_view_detail_data_sheet")%></th>
            </tr>
            <%=Utils.getTechData("creator", holder, Utils.getRowData(entry.getCreator(), 0, true), paramRequest.getLocaleString("usrmsg_view_detail_artist"))%>
            <%=Utils.getTechData("datecreated", holder, null != entry.getDatecreated() ? Utils.esDate(entry.getDatecreated().getValue()) : "", paramRequest.getLocaleString("usrmsg_view_detail_date"))%>
            <%=Utils.getTechData("resourcetype", holder, Utils.getRowData(entry.getResourcetype(), 0, true), paramRequest.getLocaleString("usrmsg_view_detail_type_object"))%>
            <%=Utils.getTechData("oaiid/identifier", holder, entry.getIdentifiers(), paramRequest.getLocaleString("usrmsg_view_detail_identifier"))%>
            <%=Utils.getTechData("holder", holder, holder, paramRequest.getLocaleString("usrmsg_view_detail_institution"))%>
            <%=Utils.getTechData("reccollection", holder, Utils.getRowData(entry.getGenerator(), 0, true), paramRequest.getLocaleString("usrmsg_view_detail_collection"))%>
            <%=Utils.getTechData("rights.rightstitle", holder, entry.getRights().getRightstitle(), paramRequest.getLocaleString("usrmsg_view_detail_rights"))%>
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
                        <% if (entry.getLang().size() == 4) {%>
                        <td><%=entry.getLang().get(2)%>, <%=entry.getLang().get(3)%></td>
                        <% } else if (entry.getLang().size() == 3) {%>
                        <td><%=entry.getLang().get(1)%>, <%=entry.getLang().get(2)%></td>
                        <% } %>
                    </tr>
            <%
                }
            %>
        </table>
        <a name="complementary"></a>
        <p id="morecompl" style="display:none;">
            <table>
                <%
                    if (null != entry.getLugar() && !entry.getLugar().isEmpty()) {
                %>
                        <tr>
                            <td><%=paramRequest.getLocaleString("usrmsg_view_detail_place")%></td>
                            <td><%=entry.getLugar()%></td>
                        </tr>
                <% }%>
            </table>
        </p>
        <p class="vermas"><a href="#complementary"><%=paramRequest.getLocaleString("usrmsg_view_detail_show_more")%> <span class="ion-plus-circled"></span></a></p>
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