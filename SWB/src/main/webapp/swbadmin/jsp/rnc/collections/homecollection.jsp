<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="mx.gob.cultura.portal.response.DigitalObject, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Identifier, java.util.List"%>
<%@ page import="mx.gob.cultura.portal.utils.Utils, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceException, org.semanticwb.model.WebPage" %>
<%
    List<Entry> relevants = (List<Entry>) request.getAttribute("relevants");
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    WebPage detail = paramRequest.getWebPage().getWebSite().getWebPage("detalle");
    String uri = detail.getRealUrl(paramRequest.getUser().getLanguage());
    if (relevants != null && !relevants.isEmpty()) {
%>
<section id="recientes">
    <div class="rosa-bg">
        <h3 class="oswB vino"><span class="h3linea"></span><%=paramRequest.getLocaleString("usrmsg_view_relevants")%><span class="h3linea"></span></h3>
        <div class="container">
            <div class="row">
                <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 car-img3">
                    <div id="owl-3" class="card-deck owl-carousel owl-theme col rel">
                        <%
                            for (Entry item : relevants) {
                        %>
                                <div class="item">
                                    <div class="pieza">
                                        <div>
                                            <a href="<%=uri%>?id=<%=item.getId()%>">
                                                <img src="<%=item.getResourcethumbnail()%>"/>
                                            </a>
                                            <% String creator = null != item.getCreator() && !item.getCreator().isEmpty() ? item.getCreator().get(0) : "&nbsp;";%>
                                        </div>
                                        <p class="tit">
                                            <a href="<%=uri%>?id=<%=item.getId()%>"><%=Utils.getTitle(item.getRecordtitle(),0)%></a>
                                        </p>
                                        <p class="azul autor">
                                            <a href="#"><%=creator%></a>
                                        </p>
                                    </div>
                                </div>
                        <%
                            }
                        %>
                    </div>
                </div>
                <div class="owl-nav col">
                    <div class="rel customPrevBtn"><i class="fa fa-chevron-left" aria-hidden="true"></i></div>
                    <div class="rel customNextBtn"><i class="fa fa-chevron-right" aria-hidden="true"></i></div>
                </div>
            </div>
        </div>
    </div>
</section>
<%
    }
%>