<%-- 
    Document   : admExhibition
    Created on : 11/01/2018, 01:44:36 PM
    Author     : sergio.tellez
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, org.semanticwb.portal.api.SWBResourceModes, org.semanticwb.model.Resource"%>
<%
    Resource base = (Resource) request.getAttribute("base");
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    SWBResourceURL searchURL = paramRequest.getRenderUrl().setMode("SEARCH");
    searchURL.setCallMethod(SWBParamRequest.Call_DIRECT);

    SWBResourceURL saveURL = paramRequest.getRenderUrl().setMode("SAVE");
    saveURL.setCallMethod(SWBParamRequest.Call_DIRECT);

    SWBResourceURL pageURL = paramRequest.getRenderUrl().setMode("PAGE");
    pageURL.setCallMethod(SWBParamRequest.Call_DIRECT);

    SWBResourceURL sortURL = paramRequest.getRenderUrl().setMode("SORT");
    sortURL.setCallMethod(SWBParamRequest.Call_DIRECT);
%>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<script>
    function send() {
        var xhttp = new XMLHttpRequest();
        var sort = document.getElementById("sort").value;
        var type = document.getElementById("type").value;
        var criteria = document.getElementById("criteria").value.trim();
        var order = getRadio(document.getElementsByName("order"), 'des');
        //var title = document.getElementById("title").value.trim();
        var url = '<%=searchURL%>' + '?criteria=' + criteria + '&sort=' + sort + '&order=' + order + '&type=' + type;
        xhttp.onreadystatechange = function () {
            if (this.readyState == 4 && this.status == 200) {
                $(document).ready(function () {
                    var slider = new Slider('#ex1', {});
                    slider.on("slide", function (sliderValue) {
                        var str = sliderValue + "";
                        var res = str.split(',');
                        document.getElementById("ex1SliderVal").textContent = res[0];
                        document.getElementById("ex2SliderVal").textContent = res[1];
                    });
                });
                document.getElementById("filters").innerHTML = this.responseText;
            }
        };
        xhttp.open("POST", url, true);
        xhttp.send();
    }
    function isReady(criteria, sort, type) {
        if (criteria == '' || sort == '' || type == '')
            return false;
        return true;
    }
    function getRadio(radios, pred) {
        var order = pred;
        for (var i = 0; i < radios.length; i++) {
            if (radios[i].checked) {
                order = radios[i].value;
            }
        }
        return order;
    }
    function getEl(el, id) {
        var i;
        var url = '';
        for (i = 0; i < el.length; i++) {
            if (el[i].checked) {
                url = url + id + el[i].value;
            }
        }
        return url;
    }
    function doSave(word) {
        var fs = getEl(document.getElementsByName("favarts"), '&favarts=');
        var hs = getEl(document.getElementsByName("hiddenarts"), '&hiddenarts=');
        dojo.xhrPost({
            url: '<%=saveURL%>?criteria=' + word + fs + hs,
            load: function (data) {
                dojo.byId('filters').innerHTML = data;
            }
        });
    }
    function doPage(w, p, m, f) {
        var fs = getEl(document.getElementsByName("favarts"), '&favarts=');
        dojo.xhrPost({
            url: '<%=pageURL%>?p=' + p + '&m=' + m + '&sort=' + f + '&criteria=' + w + fs,
            load: function (data) {
                dojo.byId('references').innerHTML = data;
                location.href = '#showPage';
            }
        });
    }
    function sort(w, f) {
        doSort(w, f.value);
    }
    function doSort(w, f) {
        dojo.xhrPost({
            url: '<%=sortURL%>?criteria=' + w + '&sort=' + f,
            load: function (data) {
                dojo.byId('references').innerHTML = data;
                location.href = '#showPage';
            }
        });
    }
</script>
<div id="filters">
    <form id="criteriaForm" name="criteriaForm" action="<%=searchURL%>" method="post">
        <div class="swbform">
            <table width="100%"  border="0" cellpadding="5" cellspacing="0">
                <tr>
                    <td class="datos">Criterio de búsqueda: </td>
                    <td class="valores"><input type="text" name="criteria" id="criteria" size="40" value="<%=base.getAttribute("criteria", "").trim()%>"/></td>
                </tr>
                <tr>
                    <td class="datos">Ordenamiento: </td>
                    <td class="valores">
                        <select name="sort" id="sort">
                            <option value=""></option>
                            <option value="date" <% if (base.getAttribute("sort", "").equals("date")) {
                                                                   out.println("selected='selected'");
                                                               } %>>Fecha</option>
                            <option value="relv" <% if (base.getAttribute("sort", "").equals("relv")) {
                                                                   out.println("selected='selected'");
                                                               } %>>Relevancia</option>
                            <option value="stat" <% if (base.getAttribute("sort", "").equals("stat")) {
                                                                   out.println("selected='selected'");
                                                               } %>>Popularidad</option>
                        </select>
                        <input type="radio" name="order" value="des" <% if (base.getAttribute("order", "").equals("des")) {
                                                           out.println("checked");
                                                       } %>>Descendente
                        <input type="radio" name="order" value="asc" <% if (base.getAttribute("order", "").equals("asc")) {
                                                           out.println("checked");
                                                       }%>>Ascendente
                    </td>
                </tr>
                <!--tr>
                        <td class="datos">URL Endpoint: </td>
                        <td class="valores"><input type="text" name="endpointURL" size="40" value="<%=base.getAttribute("endpointURL", "").trim()%>"/></td>
                </tr-->
                <tr>
                    <td class="datos">Tipo de archivo: </td>
                    <td class="valores">
                        <select name="type" id="type">
                            <option value=""></option>
                            <option value="audio" <% if (base.getAttribute("type", "").equals("audio")) {
                                                                out.println("selected='selected'");
                                                            } %>>Audio</option>
                            <option value="image" <% if (base.getAttribute("type", "").equals("image")) {
                                                                out.println("selected='selected'");
                                                            } %>>Imagen</option>
                            <option value="video" <% if (base.getAttribute("type", "").equals("video")) {
                                                                out.println("selected='selected'");
                                                            }%>>Video</option>
                        </select>
                    </td>
                </tr>
                <!--tr>
                        <td class="datos">Título: </td>
                        <td class="valores"><input type="text" name="title" id="title" size="40" value="<%=base.getAttribute("title", "").trim()%>"/></td>
                </tr-->
                <tr>
                    <td></td>
                </tr>
                <tr>
                    <td colspan="2" align="right">
                        <button type="button" class="btn btn-negro" onclick="send();">Enviar</button>
                </tr>
            </table>
        </div>
    </form>	
</div>