<%-- 
    Document   : listcollections
    Created on : 12-oct-2018, 17:37:52
    Author     : juan.fernandez
--%>
<%@page import="org.semanticwb.model.WebPage"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.semanticwb.model.User, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, mx.gob.cultura.portal.resources.MyCollections, org.semanticwb.model.WebSite, mx.gob.cultura.portal.response.Collection, java.util.List"%>
<%
    List<Collection> boards = (List<Collection>) request.getAttribute("FULL_LIST");
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    //Use in dialog
    SWBResourceURL saveURL = paramRequest.getActionUrl();
    saveURL.setMode(SWBResourceURL.Mode_VIEW);
    saveURL.setAction(MyCollections.ACTION_ADD);

    SWBResourceURL uedt = paramRequest.getRenderUrl().setMode(SWBResourceURL.Mode_EDIT);
    uedt.setCallMethod(SWBParamRequest.Call_DIRECT);

    SWBResourceURL pageURL = paramRequest.getRenderUrl();
    pageURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    pageURL.setAction("PAGE");

    SWBResourceURL uper = paramRequest.getActionUrl();
    SWBResourceURL udel = paramRequest.getActionUrl();
    uper.setAction(MyCollections.ACTION_STA);
    uper.setCallMethod(SWBParamRequest.Call_DIRECT);
    udel.setAction(SWBResourceURL.Action_REMOVE);

    SWBResourceURL uels = paramRequest.getRenderUrl().setMode(MyCollections.MODE_VIEW_USR);
    uels.setCallMethod(SWBParamRequest.Call_CONTENT);
    WebSite site = paramRequest.getWebPage().getWebSite();
//    WebPage wpdetail = site.getWebPage("Detalle_coleccion");
//    String uels = wpdetail.getUrl();

    SWBResourceURL wall = paramRequest.getRenderUrl().setMode(MyCollections.MODE_VIEW_MYALL);
    wall.setCallMethod(SWBParamRequest.Call_CONTENT);


    String userLang = paramRequest.getUser().getLanguage();

    Integer allc = (Integer) request.getAttribute("COUNT_BY_STAT");

    Integer cusr = (Integer) request.getAttribute("COUNT_BY_USER");

    SWBResourceURL uall = paramRequest.getRenderUrl().setMode(MyCollections.MODE_VIEW_ALL);
    uall.setCallMethod(SWBParamRequest.Call_CONTENT);

    String userName = "";
    String useridColls = paramRequest.getResourceBase().getAttribute("userid");
    User usr = site.getUserRepository().getUser(useridColls);
    if(null!=usr){
        userName = usr.getFullName();
    }
    

%>


<div class="container usrTit">
    <div class="row">
        <!--<img src="/work/models/<%//=site.getId()%>/img/agregado-07.jpg" class="circle">-->
        <div>
            <h2 class="oswM nombre"><%=userName%></h2>
        </div>
    </div>
</div>

<a name="showPage"></a>
<div class="container">
    <div id="references">
        <div class="row mosaico-contenedor">
            <%
                if (!boards.isEmpty()) {
                    for (Collection c : boards) {
            %>
            <div class="col-6 col-md-4">
                <%	if (c.getCovers().isEmpty()) {%>
                <div class="mosaico mosaico1 radius-overflow">
                    <a href="<%=uels%>?id=<%=c.getId()%>">
                        <img src="/work/models/<%=site.getId()%>/img/empty.jpg">
                    </a>
                </div>
                <%  } else if (c.getCovers().size() < 3) {%>
                <div class="mosaico mosaico1 radius-overflow">
                    <a href="<%=uels%>?id=<%=c.getId()%>">
                        <img src="<%=c.getCovers().get(0)%>">
                    </a>
                </div>
                <%  } else {%>
                <div class="mosaico mosaico3 radius-overflow">
                    <a href="<%=uels%>?id=<%=c.getId()%>">
                        <div class="mosaico3a">
                            <img src="<%=c.getCovers().get(0)%>">
                        </div>
                        <div class="mosaico3b">
                            <div>
                                <img src="<%=c.getCovers().get(1)%>">
                            </div>
                            <div>
                                <img src="<%=c.getCovers().get(2)%>">
                            </div>
                        </div>
                    </a>
                </div>
                <% }%>
                <div class="mosaico-txt ">
                    <p><%=c.getTitle()%></p>
                </div>
            </div>
            <%
                    }
                }
            %>
        </div>
    </div>
</div>

