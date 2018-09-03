<%-- 
    Document   : pagecollection.jsp
    Created on : 03/09/2018, 02:00:37 PM
    Author     : sergio.tellez
--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="mx.gob.cultura.portal.response.DigitalObject, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Identifier, java.util.List"%>
<%@ page import="org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceException, org.semanticwb.model.WebPage" %>
<%
	List<Entry> relevants = (List<Entry>) request.getAttribute("relevants");
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    WebPage detail = paramRequest.getWebPage().getWebSite().getWebPage("detalle");
    String uri = detail.getRealUrl(paramRequest.getUser().getLanguage());
	if (relevants != null && !relevants.isEmpty()) {
%>
		<div class="content container exhibiciones-main">
			<div class="row">
				<div class="col-12">
					<p><%=paramRequest.getLocaleString("usrmsg_view_relevants")%></p>
				</div>
			</div>
			<div class="row">
				<%
					for (Entry item : relevants) {
						String creator = !item.getCreator().isEmpty() ? item.getCreator().get(0) : "&nbsp;";
				%>	
						<div class="col-6 col-md-4 exhibi-pza">
							<div class="borde-CCC">
								<div class="exhibi-pza-img">
									<a href="<%=uri%>?id=<%=item.getId()%>">
										<img src="<%=item.getResourcethumbnail()%>"/>
									</a>
								</div>
								<p class="oswB rojo uppercase"><%=item.getRecordtitle().get(0).getValue()%></p>
								<p><%=creator%></p>
							</div>
						</div>
				<%
					}
				%>
			</div>
		</div>
<%
	}
%>