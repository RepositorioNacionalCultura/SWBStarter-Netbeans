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
    String title = "";
    String holder = "";
    String period = "";
    String rights = "";
    String creator = "";
    Document desc = null;
    String generator = "";
    List<Title> titles = new ArrayList<>();
    List<String> holders = new ArrayList<>();
    Entry entry = (Entry)request.getAttribute("entry");
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    if (null != entry) {
        holders = entry.getHolder();
        titles = entry.getRecordtitle();
	StringBuilder builder = new StringBuilder();
        StringBuilder collection = new StringBuilder();
        for (String t : entry.getResourcetype()) {
            builder.append(t).append(", ");
        }
	if (builder.length() > 0) builder.deleteCharAt(builder.length() - 2);
        type =  builder.toString();
        if (null != entry.getGenerator()) {
            for (String t : entry.getGenerator()) {
                collection.append(t).append(", ");
            }
        }
	if (collection.length() > 0) collection.deleteCharAt(collection.length() - 2);
        generator = Utils.getRowData(entry.getGenerator(), 0, true);
	holder = holders.size() > 0 ? holders.get(0) : "";
	creator = Utils.getRowData(entry.getCreator(), 0, true);
        period = null != entry.getDatecreated() ? Utils.esDate(entry.getDatecreated().getValue()) : "";
	if (!titles.isEmpty()) title = titles.get(0).getValue();
        desc = Utils.getDescription(entry.getDescription());
        rights = Utils.getRights(entry);
    }
%>
<div class="col-12 col-sm-12 col-md-9 col-lg-9 order-md-2 order-sm-1 order-1 ficha ">
    <h3 class="oswM"><%=title%></h3>
    <%  if (null != desc) { %>
            <p id="shortdesc"><%=desc.get("short")%></p>
            <a name="showPage"></a>
            <p id="moredesc" style="display:none;"><%=desc.get("full")%></p>
            <hr>
    <% } %>
    <p class="vermas"><a href="#showPage" onclick="moreDesc()"><%=paramRequest.getLocaleString("usrmsg_view_detail_show_more")%> <span class="ion-plus-circled"></span></a></p>
    <div class="tabla">
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
                <td><%=holder%></td>
            </tr>
            <tr>
		<td><%=paramRequest.getLocaleString("usrmsg_view_detail_collection")%></td>
		<td><%=generator%></td>
            </tr>
            <tr>
                <td><%=paramRequest.getLocaleString("usrmsg_view_detail_rights")%></td>
		<td><%=rights%></td>
            </tr>
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
                if (null != entry.getLugar() && !entry.getLugar().isEmpty()) {
            %>
                    <tr>
                        <td><%=paramRequest.getLocaleString("usrmsg_view_detail_place")%></td>
                        <td><%=entry.getLugar()%></td>
                    </tr>
            <% }%>
        </table>
        <p class="vermas"><a href="#"><%=paramRequest.getLocaleString("usrmsg_view_detail_show_more")%> <span class="ion-plus-circled"></span></a></p>
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
</script>