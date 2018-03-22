<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.response.Collection, mx.gob.cultura.portal.response.DigitalObject, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Identifier, org.semanticwb.portal.api.SWBParamRequest"%>
<%@ page import="java.util.List" %>
<%
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    Collection c = (Collection)request.getAttribute("collection");
    List<Entry> itemsList = (List<Entry>)request.getAttribute("myelements");
%>
<a class="rojo" href="javascript:history.go(-1)"><i aria-hidden="true" class="fa fa-long-arrow-left"></i>Regresar</a>
<%
    if (!itemsList.isEmpty()) {
%>
        <section id="buscado" class="azul-bg">
            <div class="container">
                <div class="row">
                    <div class="col-xs-12 col-sm-12 col-md-2 col-lg-2 car-nav2">
                        <div class="owl-nav col">
                            <h3 class="oswB blanco"><%=c.getTitle() %></h3>
                            <div class="bus customPrevBtn">
                                <i class="fa fa-long-arrow-left" aria-hidden="true"></i>
                            </div>
                            <div class="bus customNextBtn">
                                <i class="fa fa-long-arrow-right" aria-hidden="true"></i>
                            </div>
                        </div>
                    </div>
                    <div class="col-xs-12 col-sm-12 col-md-10 col-lg-10 car-img2">
                        <div id="owl-2" class="card-deck owl-carousel owl-theme col bus">
                        <%
                            for (Entry item : itemsList) {
                        %>
                               <div class="item">
                                    <div class="card">
                                        <div class="card-header"><%=!item.getResourcetype().isEmpty() ? item.getResourcetype().get(0) : "&nbsp;" %><span class="ion-android-more-vertical"/></div>
                                    <%
                                        for (DigitalObject digObj : item.getDigitalobject()) {
                                            if (digObj.getMediatype().getMime().startsWith("image") && !digObj.getUrl().isEmpty()) {
                                    %>
                                                <div class="card-img-cont">
                                                    <img class="card-img-top" src="<%=digObj.getUrl()%>"/>
                                                </div>
                                    <%
                                                 break;
                                            }
                                        }
                                        int views = item.getResourcestats().getViews();
                                    %>
                                        <div class="card-body">
                                            <h4 class="card-title"><%=item.getRecordtitle().get(0).getValue()%></h4>
                                            <p class="card-text">&nbsp;<!--no hay descripcion--></p>
                                        </div>
                                        <div class="card-footer">
                                            <small class="text-muted">
                                                <div>
                                                    <span class="ion-eye"/> <%=views%>
                                                </div>
                                                <div><a class="rojo" href="/swb/<%=paramRequest.getWebPage().getWebSiteId()%>/detalle?id=<%=item.getId()%>">Ver más +</a></div>
                                            </small>
                                        </div>
                                    </div>
                                </div>
                        <%
                            }
                        %>
                        </div>
                    </div>
                </div>
            </div>
        </section>
<%
    }else out.print("<h3 class=\"oswB azul\">Agregue favoritos a su colección " + c.getTitle()+ "</h3>");
%>