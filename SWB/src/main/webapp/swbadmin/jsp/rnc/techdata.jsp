<%--
    Document   : techdata.jsp
    Created on : 15/06/2018, 14:15:08 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DateDocument, mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.resources.ArtDetail, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, java.util.ArrayList, java.util.List"%>
<%
    String type = "";
    String title = "";
    String holder = "";
    String period = "";
    String creator = "";
    List<Title> titles = new ArrayList<>();
    List<String> holders = new ArrayList<>();
    List<String> creators = new ArrayList<>();
    Entry entry = (Entry)request.getAttribute("entry");
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    if (null != entry) {
        holders = entry.getHolder();
	creators = entry.getCreator();
        titles = entry.getRecordtitle();
	StringBuilder builder = new StringBuilder();
        for (String t : entry.getResourcetype()) {
            builder.append(t).append(", ");
        }
	if (builder.length() > 0) builder.deleteCharAt(builder.length() - 2);
        type =  builder.toString();
	holder = holders.size() > 0 ? holders.get(0) : "";
	creator = creators.size() > 0 ? creators.get(0) : "";
        period = null != entry.getDatecreated() ? Utils.esDate(entry.getDatecreated().getValue()) : "";
	if (!titles.isEmpty()) title = titles.get(0).getValue();
    }
%>
<div class="col-12 col-sm-12 col-md-6 col-lg-6 order-md-2 order-sm-1 order-1 ficha ">
	<h3 class="oswM"><%=title%></h3>
	<% if (null != entry && null != entry.getDescription() && !entry.getDescription().isEmpty()) { %>
			<p><%=entry.getDescription().get(0)%></p>
	<% } %>
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
            <td><%=holder%></td>
        </tr>
		<%
			if (null != entry.getLang() && entry.getLang().size() > 2) {
		%>
				<tr>
					<td><%=paramRequest.getLocaleString("usrmsg_view_detail_lang")%></td>
					<% if (entry.getLang().size() == 4) { %>
							<td><%=entry.getLang().get(2)%>, <%=entry.getLang().get(3)%></td>
					<% }else if (entry.getLang().size() == 3) { %>
							<td><%=entry.getLang().get(1)%>, <%=entry.getLang().get(2)%></td>
					<% } %>
				</tr>
		<% } %>
        <tr>
			<td><%=paramRequest.getLocaleString("usrmsg_view_detail_technique")%></td>
			<td>Lorem ipsum</td>
        </tr>
    </table>
    <p class="vermas"><a href="#"><%=paramRequest.getLocaleString("usrmsg_view_detail_show_more")%> <span class="ion-plus-circled"></span></a></p>
</div>