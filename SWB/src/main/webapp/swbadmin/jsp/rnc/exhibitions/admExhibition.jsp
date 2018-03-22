<%-- 
    Document   : admExhibition
    Created on : 11/01/2018, 01:44:36 PM
    Author     : sergio.tellez
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, org.semanticwb.portal.api.SWBResourceModes, org.semanticwb.model.Resource"%>
<%
    Resource base = (Resource)request.getAttribute("base");
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    SWBResourceURL url=paramRequest.getRenderUrl();
    url.setMode(SWBResourceModes.Mode_ADMIN);
    url.setAction("SEARCH");
%>
<form action="<%=url.toString() %>" method="post">
    <div class="swbform">
        <table width="100%"  border="0" cellpadding="5" cellspacing="0">
            <tr>
                <td class="datos">Criterio de búsqueda: </td>
                <td class="valores"><input type="text" name="criteria" size="40" value="<%=base.getAttribute("criteria","").trim()%>"/></td>
            </tr>
			<tr>
                <td class="datos">Ordenamiento: </td>
                <td class="valores">
                       <select name="sort" id="sort">
                           <option value=""></option>
                           <option value="date" <% if (base.getAttribute("sort","").equals("date")) out.println("selected='selected'"); %>>Fecha</option>
                           <option value="relv" <% if (base.getAttribute("sort","").equals("relv")) out.println("selected='selected'"); %>>Relevancia</option>
                           <option value="stat" <% if (base.getAttribute("sort","").equals("stat")) out.println("selected='selected'"); %>>Popularidad</option>
                       </select>
					   <input type="radio" name="order" value="des" <% if (base.getAttribute("order","").equals("des")) out.println("checked"); %>>Descendente
					   <input type="radio" name="order" value="asc" <% if (base.getAttribute("order","").equals("asc")) out.println("checked"); %>>Ascendente
                </td>
            </tr>
			<tr>
                <td class="datos">URL Endpoint: </td>
                <td class="valores"><input type="text" name="endpointURL" size="40" value="<%=base.getAttribute("endpointURL","").trim()%>"/></td>
            </tr>
			<tr>
                <td class="datos">Tipo de archivo: </td>
                <td class="valores">
					 <select name="type" id="type">
						<option value=""></option>
                        <option value="audio" <% if (base.getAttribute("type","").equals("audio")) out.println("selected='selected'"); %>>Audio</option>
                        <option value="image" <% if (base.getAttribute("type","").equals("image")) out.println("selected='selected'"); %>>Imagen</option>
                      </select>
				</td>
            </tr>
			<tr>
                <td class="datos">Título: </td>
                <td class="valores"><input type="text" name="title" size="40" value="<%=base.getAttribute("title","").trim()%>"/></td>
            </tr>
            <tr>
                <td></td>
            </tr>
            <tr>
                <td colspan="2" align="right">
                <br><hr size="1" noshade>
                <input type="submit" name="btnSave" value="Enviar" class="boton"/></td>
            </tr>
        </table>
    </div>
</form>