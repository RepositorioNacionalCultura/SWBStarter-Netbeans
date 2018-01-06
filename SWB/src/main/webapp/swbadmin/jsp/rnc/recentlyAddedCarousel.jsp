<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="mx.gob.cultura.portal.response.DigitalObject, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Identifier, java.util.List"%>
<%@ page import="org.semanticwb.portal.api.SWBParamRequest" %>
<%
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    List<Entry> itemsList = (List<Entry>) request.getAttribute("recentlyAddedList");
    if (itemsList != null && !itemsList.isEmpty()) {
%>

<section id="recientes">
    <div class="container">
        <div class="col">
            <h3 class="oswB rojo">Agregados recientemente</h3>
            <div class="col-xs-12 col-sm-12 col-md-1 col-lg-1 car-nav">
                <div class="owl-nav col">
                    <div class="rel customPrevBtn"><i class="fa fa-long-arrow-left" aria-hidden="true"></i></div>
                    <div class="rel customNextBtn"><i class="fa fa-long-arrow-right" aria-hidden="true"></i></div>
                </div>
            </div>
        </div>
    </div>
    <div class="gris-bg">
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
                                        for (DigitalObject digObj : item.getDigitalobject()) {
                                            if (digObj.getMediatype().getMime().startsWith("image") && !digObj.getUrl().isEmpty()) {
                                    %>
                                    <a href="/swb/<%=paramRequest.getWebPage().getWebSiteId()%>/detalle?id=<%=item.getId()%>">
                                        <img src="<%=digObj.getUrl()%>"/>
                                    </a>
                                    <%
                                                break;
                                            }
                                        }
                                        String creator = !item.getCreator().isEmpty() ? item.getCreator().get(0) : "&nbsp;";
                                    %>
                                </div>
                                <p class="oswB azul tit ">
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
            </div>
        </div>
    </div>
</section>

<%
    }
%>