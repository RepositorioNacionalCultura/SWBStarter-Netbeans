<%--
    Document   : sketchdigital.jsp
    Created on : 29/05/2018, 11:48:36 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, java.util.ArrayList, java.util.List"%>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<%
    int books = 0;
    String title = "";
    String creator = "";
    DigitalObject digital = null;
    List<Title> titles = new ArrayList<>();
    List<String> creators = new ArrayList<>();
    StringBuilder divVisor = new StringBuilder();
    int iDigit = (Integer)request.getAttribute("iDigit");
    int iPrev = iDigit-1;
    int iNext = iDigit+1;
    List<DigitalObject> digitalobjects = new ArrayList<>();
    Entry entry = (Entry)request.getAttribute("entry");
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    if (null != entry) {
	if (null != entry.getDigitalObject()) {
            creators = entry.getCreator();
            titles = entry.getRecordtitle();
            digitalobjects = entry.getDigitalObject();
            books = null != digitalobjects ? digitalobjects.size() : 0;
            digital = books > iDigit ? digitalobjects.get(iDigit) : new DigitalObject();
            if (null != digital.getUrl())
                divVisor.append("<iframe width=\"1280\" height=\"860\" src=\"\" id=\"api-frame\" allowfullscreen mozallowfullscreen=\"true\" webkitallowfullscreen=\"true\"></iframe>");
        }
        creator = creators.size() > 0 ? creators.get(0) : "";
        if (!titles.isEmpty()) title = titles.get(0).getValue();
    }
%>
<div id="idetail" class="detalleimg">
    <div class="obranombre">
        <h3 class="oswB"><%=title%></h3>
        <p class="oswL"><%=creator%></p>
    </div>
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
                    <a href="#" onclick="loadDoc('<%=entry.getId()%>');"><span class="ion-heart"></span></a> <%=entry.getResourcestats().getViews()%>
                </div>
            </div>
            <div class="explo3 row">
                <div class="col-6">
                    <%
                        if (iPrev >= 0) {
                    %>
                            <a href="#" onclick="nextObj('<%=entry.getId()%>', <%=iPrev%>);"><span class="ion-chevron-left"></span> <%=paramRequest.getLocaleString("usrmsg_view_detail_prev_object")%></a>
                    <%					
                        }
                    %>
                </div>
                <div class="col-6">
                    <%
                        if (iNext < books) {
                    %>
                            <a href="#" onclick="nextObj('<%=entry.getId()%>', <%=iNext%>);"><%=paramRequest.getLocaleString("usrmsg_view_detail_next_object")%> <span class="ion-chevron-right"></span></a>
                    <%
                        }
                    %>
                </div>
            </div>
        </div>
    </div>
    <%=divVisor%>
</div>