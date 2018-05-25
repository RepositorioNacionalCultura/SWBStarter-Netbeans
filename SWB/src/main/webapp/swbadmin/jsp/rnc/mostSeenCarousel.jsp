<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.response.DigitalObject, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Identifier, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.model.WebPage"%>
<%@ page import="java.util.List, org.semanticwb.portal.api.SWBResourceException" %>
<%
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    WebPage detailPage = paramRequest.getWebPage().getWebSite().getWebPage("detalle");
    String detailPath = detailPage.getRealUrl(paramRequest.getUser().getLanguage());
    List<Entry> itemsList = (List<Entry>) request.getAttribute("mostSeenList");
    String sectionTitle;
    try {
        sectionTitle = paramRequest.getLocaleString("lbl_sectionTitle");
    } catch (SWBResourceException swbe) {
        sectionTitle = "Habráse visto!!";
        swbe.printStackTrace(System.err);
    }
    String lbl_seeMore = paramRequest.getLocaleString("lbl_seeMore");
%>
<section id="buscado">
    <div>
        <h3 class="oswB vino"><span class="h3linea"></span><%=sectionTitle%><span class="h3linea"></span></h3>
        <div class="container">
            <div class="row">
                <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 car-img2">
                    <div id="owl-2" class="card-deck owl-carousel owl-theme col bus">
                    <%
                    for (Entry item : itemsList) {
                        %>
                        <div class="item">
                            <div class="card">
                                <div class="card-header">
                                    <%=!item.getResourcetype().isEmpty() ? item.getResourcetype().get(0) : "&nbsp;" %>
                                </div>
                                <%
                                    for (DigitalObject digObj : item.getDigitalObject()) {
                                        if (digObj.getMediatype().getMime().startsWith("image") && !digObj.getUrl().isEmpty()) {
                                %>
                                <div class="card-img-cont">
                                    <img class="card-img-top" src="<%=digObj.getUrl()%>"/>
                                </div>
                                <%
                                            break;
                                        }
                                    }
                                    String authorName = "&nbsp;";
                                    int views = item.getResourcestats().getViews();
                                    if (!item.getCreator().isEmpty()) {
                                        authorName = item.getCreator().get(0);
                                    }
                                %>
                                <div class="card-body">
                                    <p class="tit"><%=item.getRecordtitle().get(0).getValue()%></p>
                                    <p class="card-text"><%=authorName%></p>
                                </div>
                                <div class="card-footer">
                                    <small class="text-muted">
                                        <div>
                                            <span class="ion-eye"/> <%=views%>
                                        </div>
                                        <div>
                                            <a class="rojo" href="<%=detailPath%>?id=<%=item.getId()%>"><%=lbl_seeMore%></a>
                                        </div>
                                    </small>
                                </div>
                            </div>
                        </div>
<%
                    }
%>
                    </div>
                </div>
                <div class="owl-nav col">
                    <div class="bus customPrevBtn"><i class="fa fa-chevron-left" aria-hidden="true"></i></div>
                    <div class="bus customNextBtn"><i class="fa fa-chevron-right" aria-hidden="true"></i></div>
                </div>
            </div>
        </div>
    </div>
</section>