<%--
    Document   : techdata.jsp
    Created on : 15/06/2018, 14:15:08 AM
    Author     : sergio.tellez
--%>
<%@page import="java.util.Arrays"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map, mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DateDocument, mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.resources.ArtDetail, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, 
    org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, java.util.ArrayList, java.util.List, org.bson.Document"%>
<%
    String fdesc = "";
    String title = "";
    String holder = "";
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
        desc = Utils.getDescription(entry.getDescription(), 0);
	fdesc = (null != desc && null !=desc.get("full")) ? (String)desc.get("full") : "";
        holder = Utils.getRowData(entry.getHolder(), 0, false);
    }
    Map mapper = (Map)request.getAttribute("mapper");
%>
<div id="tchdta" class="col-12 col-sm-12 col-md-9 col-lg-9 order-md-2 order-sm-1 order-1 ficha ">
    <h3 class="oswM"><%=title%></h3>
    <% if (null != desc && !holder.contains("Museo Nacional de Antropolog")) { %>
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
            <%=Utils.getTechData(mapper, userLang, uri.toString())%>
            
            <%
                if (null != entry.getLang() && entry.getLang().size() > 2) {
            %>
                    <tr>
                        <td><%=paramRequest.getLocaleString("usrmsg_view_detail_lang")%></td>
                        <td><%=Utils.concatFilter(userLang, site.getId(), "lang", entry.getLang())%></td>
                    </tr>
            <%
                }
            %>
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