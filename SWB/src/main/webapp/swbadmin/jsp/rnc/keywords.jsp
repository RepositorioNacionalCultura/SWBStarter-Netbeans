<%--
    Document   :keywords.jsp
    Created on : 22/06/2018, 12:40:53 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.response.Entry, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.model.WebSite"%>
<%
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    Entry entry = (Entry) request.getAttribute("entry");
    String back = (String) request.getAttribute("back");
    WebSite site = paramRequest.getWebPage().getWebSite();
    String userLang = paramRequest.getUser().getLanguage();
%>
<div class="col-12 col-sm-6  col-md-3 col-lg-3 order-md-3 order-sm-3 order-3 clave">
    <div class="redes">
        <a href="#" onclick="fbShare();"><span class="ion-social-facebook"></span></a>
        <span class="ion-social-twitter"></span>
    </div>
    <div>
        <p class="tit2"><%=paramRequest.getLocaleString("usrmsg_view_detail_key_words")%></p>
        <p>
            <%
                int i = 0;
                for (String key : entry.getKeywords()) {
                    i++;
                    out.println("<a href=\"/" + userLang + "/" + site.getId() + "/resultados?word=" + key + "\">" + key + "</a>");
                    if (i < entry.getKeywords().size()) {
                        out.println(" / ");
                    }
                }
            %>
        </p>
    </div>
    <div class="">
        <a href="<%=back%>">
            <i aria-hidden="true" class="fa fa-long-arrow-left"></i> <%=paramRequest.getLocaleString("usrmsg_view_detail_back")%>
        </a>
    </div>
</div>