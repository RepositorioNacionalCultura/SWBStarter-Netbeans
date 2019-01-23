<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="mx.gob.cultura.portal.response.DigitalObject, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Identifier, java.util.List"%>
<%@ page import="org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceException, org.semanticwb.model.WebPage" %>
<%
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    List<Entry> itemsList = (List<Entry>) request.getAttribute("recentlyAddedList");
    WebPage detailPage = paramRequest.getWebPage().getWebSite().getWebPage("detalle");
    String detailPath = detailPage.getRealUrl(paramRequest.getUser().getLanguage());
    
    if (itemsList != null && !itemsList.isEmpty()) {
        String lbl_sectionTitle;
        try {
            lbl_sectionTitle = paramRequest.getLocaleString("lbl_sectionTitle");
        } catch (SWBResourceException swbe) {
            lbl_sectionTitle = "AGREGADOS RECIENTEMENTE";
        }

%>
<section id="recientes">
    <div class="rosa-bg">
        <h3 class="oswB vino"><span class="h3linea"></span><%=lbl_sectionTitle%><span class="h3linea"></span></h3>
        <div class="container">
            <div class="row">
                <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 car-img3">
                    <div id="owl-3" class="card-deck owl-carousel owl-theme col rel">
                        <%
                            for (Entry item : itemsList) {
                        %>
                        <div class="item">
                            <div class="pieza">
                                <div>
                                    <%
                                        for (DigitalObject digObj : item.getDigitalObject()) {
                                            if (digObj.getMediatype().getMime().startsWith("image") && !digObj.getUrl().isEmpty()) {
                                    %>
                                    <a href="<%=detailPath%>?id=<%=item.getId()%>">
                                        <img src="<%=digObj.getUrl()%>"/>
                                    </a>
                                    <%
                                                break;
                                            }
                                        }
                                        String creator = !item.getCreator().isEmpty() ? item.getCreator().get(0) : "&nbsp;";
                                    %>
                                </div>
                                <p class="tit">
                                    <a href="/swb/<%=paramRequest.getWebPage().getWebSiteId()%>/detalle?id=<%=item.getId()%>"><%=item.getRecordtitle().get(0).getValue()%></a>
                                </p>
                                <p class="azul autor">
                                    <a href="/swb/<%=paramRequest.getWebPage().getWebSiteId()%>/detalle?id=<%=item.getId()%>"><%=creator%></a>
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