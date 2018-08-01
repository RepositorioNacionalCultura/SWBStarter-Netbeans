<%--
    Document   : preview.jsp
    Created on : 5/12/2017, 11:48:36 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.utils.Utils, mx.gob.cultura.portal.response.DateDocument, mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.resources.ArtDetail, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, java.util.ArrayList, java.util.List"%>
<script type="text/javascript" src="/swbadmin/js/rnc/detail.js"></script>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<%
    int iDigit = 1;
    int images = 0;
    String title = "";
    String creator = "";
    DigitalObject digital = null;
    List<Title> titles = new ArrayList<>();
    List<String> creators = new ArrayList<>();
	StringBuilder divVisor = new StringBuilder();
	StringBuilder scriptHeader = new StringBuilder();
	StringBuilder scriptCallVisor = new StringBuilder();
    List<DigitalObject> digitalobjects = new ArrayList<>();
    Entry entry = (Entry)request.getAttribute("entry");
	Integer r = null != request.getParameter("r") ? Utils.toInt(request.getParameter("r")) : 0;
	String w = null != request.getParameter("word") ? request.getParameter("word") : "";
	Integer iprev = r - 1;
	Integer inext = r + 1;
	SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
	WebSite site = paramRequest.getWebPage().getWebSite();
	String userLang = paramRequest.getUser().getLanguage();
	SWBResourceURL digitURL = paramRequest.getRenderUrl().setMode("DIGITAL");
    digitURL.setCallMethod(SWBParamRequest.Call_DIRECT);
	Integer t = null != request.getAttribute("t") ? (Integer)request.getAttribute("t") : 0;
	String fs = null != request.getAttribute("filter") ? "&filter="+request.getAttribute("filter") : "";
    if (null != entry) {
		if (null != entry.getDigitalObject()) {
			int n = 0;
			creators = entry.getCreator();
			titles = entry.getRecordtitle();
			digitalobjects = entry.getDigitalObject();
			creator = creators.size() > 0 ? creators.get(0) : "";
			if (!titles.isEmpty()) title = titles.get(0).getValue();
			images = null != digitalobjects ? digitalobjects.size() : 0;
			digital = images >= iDigit ? digitalobjects.get(iDigit-1) : new DigitalObject();
			if (null == digital.getUrl()) digital.setUrl("");
			divVisor.append("<div class=\"tablaVisualizaCont\">")
				.append("	<div class=\"container\">")
				.append("		<h3 class=\"oswM\">").append(title).append("</h3>")
				.append("		<p class=\"oswL\">").append(creator).append("</p>")
				.append("		<div class=\"tablaVisualizaTab\">")
				.append("			<table>")
				.append("				<tr>")
				.append("					<th style=\"text-align:left; width:60%;\">")
				.append("						Nombre")
				.append("					</th>")
				.append("					<th style=\"text-align:left;\">")
				.append("						Formato")
				.append("					</th>")
				.append("					<th style=\"text-align:left;\">")
				.append("						Enlace")
				.append("					</th>")
				.append("				</tr>");
			for (DigitalObject ob : digitalobjects)	{
				String action = ob.getMediatype().getMime().startsWith("audio") ? "Escuchar" : "Ver";
				divVisor.append("		<tr>")
				.append("					<td style=\"text-align:left; width:60%;\">")
				.append(ob.getMediatype().getName())
				.append("					</td>")
				.append("					<td style=\"text-align:left;\">")
				.append(ob.getMediatype().getMime())
				.append("					</td>")
				.append("					<td style=\"text-align:left;\">");
				if (ob.getUrl().endsWith(".avi") || ob.getUrl().endsWith(".zip") || ob.getUrl().endsWith(".rtf") || ob.getUrl().endsWith(".docx") || ob.getUrl().endsWith(".aiff")) {
					action = "Descargar";
					divVisor.append("<a href='").append(ob.getUrl()).append("'>").append(action).append("</a>");
				}else {
					divVisor.append("						<a href=\"/").append(userLang).append("/")
					.append(site.getId()).append("/").append("detalle?id=").append(entry.getId()).append("&n=").append(n).append("\">").append(action).append("</a>");
				}
				divVisor.append("					</td>")
				.append("				</tr>");
				n++;
			}
			divVisor.append("			</table>")
				.append("		</div>")
				.append("	</div>")
				.append("</div>");
			type = entry.getResourcetype().size() > 0 ? entry.getResourcetype().get(0) : "";
			period = null != entry.getDatecreated() ? Utils.esDate(entry.getDatecreated().getValue()) : "";
		}
    }
	String back = (String)request.getAttribute("back");
	String scriptFB = Utils.getScriptFBShare(request);
%>
<section id="detalle">
	<div id="idetail" class="detalleimg">
		 <div class="explora">
			<div class="explora2">
				<div class="explo1">
					© <%=paramRequest.getLocaleString("usrmsg_view_detail_all_rights")%>
                </div>
				<div class="explo2 row">
					<div class="col-3">
						<a href="#" onclick="fbShare();"><span class="ion-social-facebook"></span></a>
                    </div>
                    <div class="col-3">
                        <span class="ion-social-twitter"></span>
                    </div>
                    <div class="col-6">
                        <a href="#" onclick="loadDoc('/swb/<%=site.getId()%>/favorito?id=', '<%=entry.getId()%>');"><span class="ion-heart"></span></a> <%=entry.getResourcestats().getViews()%>
                    </div>
                </div>
				<div class="explo3 row">
					<div class="col-6">
						<%
							if (iprev > -1) {
						%>
								<span class="ion-chevron-left"></span><a href="#" onclick="nextResult('/<%=userLang%>/<%=site.getId()%>/detalle?word=<%=w%>&r=<%=iprev%>&t=<%=t%><%=fs%>');"><%=paramRequest.getLocaleString("usrmsg_view_detail_prev_object")%></a>
						<%
							}
						%>
					</div>
                    <div class="col-6">
						<%
							if (inext < t) {
						%>
								<a href="#" onclick="nextResult('/<%=userLang%>/<%=site.getId()%>/detalle?word=<%=w%>&r=<%=inext%>&t=<%=t%><%=fs%>');"><%=paramRequest.getLocaleString("usrmsg_view_detail_next_object")%> <span class="ion-chevron-right"></span></a>
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
			<jsp:include page="rack.jsp" flush="true"/>
            <jsp:include page="techdata.jsp" flush="true"/>
        </div>
    </div>
</section>
<section id="palabras">
	<div class="container">
		<div class="row">
			<jsp:include page="keywords.jsp" flush="true"/>
		</div>
    </div>
</section>
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