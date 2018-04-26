<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.response.Aggregation"%>
<%@page import="mx.gob.cultura.portal.response.CountName"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest,org.semanticwb.portal.api.SWBResourceURL,java.util.ArrayList, java.util.List"%>
<%
    boolean showFilters = false;
    List<CountName> dates = new ArrayList<>();
    List<CountName> holders = new ArrayList<>();
    List<CountName> resourcetypes = new ArrayList<>();
    String word = (String) request.getAttribute("criteria");
    Aggregation aggs = (Aggregation) request.getAttribute("aggs");
    List<String> creators = (List<String>) request.getAttribute("creators");
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");

    if (null != aggs && null != creators && !creators.isEmpty()) {
        showFilters = true;
        if (null != aggs.getDates()) {
            dates = aggs.getDates();
        }
        if (null != aggs.getHolders()) {
            holders = aggs.getHolders();
        }
        if (null != aggs.getResourcetypes()) {
            resourcetypes = aggs.getResourcetypes();
        }
    }
%>
<div id="sidebar">
    <div id="accordion" role="tablist">
        <%	if (null != creators && !creators.isEmpty()) { %>
        <div class="card">
            <div class="" role="tab" id="headingOne">
                <a data-toggle="collapse" href="#collapse1" aria-expanded="true" aria-controls="collapseOne" class="btnUpDown collapsed">Autor <span class="mas">+</span><span class="menos">-</span></a>
            </div>
            <div id="collapse1" class="collapse" role="tabpanel" aria-labelledby="headingOne" data-parent="#accordion">
                <ul>
                    <li>
                        <label class="form-check-label"><input class="form-check-input" type="checkbox" value="">Todos</label>
                        <ul>
                            <%
                                for (String r : creators) {
                            %>
                            <li><label class="form-check-label"><input class="form-check-input" type="checkbox" value=""><%=r%></label></li>
                                    <%
                                        }
                                    %>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
        <% } %>
        <%	if (!dates.isEmpty()) {%>
        <div class="card">
            <div class="" role="tab" id="headingOne">
                <a data-toggle="collapse" href="#collapse2" aria-expanded="true" aria-controls="collapseOne" class="btnUpDown collapsed">Fecha <span class="mas">+</span><span class="menos">-</span></a>
            </div>
            <div id="collapse2" class="collapse" role="tabpanel" aria-labelledby="headingOne" data-parent="#accordion">
                <div class="slider">  
                    <p class="oswM">[<%=aggs.getInterval().getLowerLimit()%> - <%=aggs.getInterval().getUpperLimit()%>]</p>               
                    <input id="ex1" data-slider-id='ex1Slider' type="text" data-slider-min="<%=aggs.getInterval().getLowerLimit()%>" data-slider-max="<%=aggs.getInterval().getUpperLimit()%>" data-slider-step="1" data-slider-value="[<%=aggs.getInterval().getLowerLimit()%>,<%=aggs.getInterval().getUpperLimit()%>]"/>
                    <div class="d-flex">
                        <div class="p-2" id="ex1SliderVal"><%=aggs.getInterval().getLowerLimit()%></div>
                        <div class="ml-auto p-2" id="ex2SliderVal"><%=aggs.getInterval().getUpperLimit()%></div>
                    </div>
                </div>
            </div>
        </div>		
        <% } %>
        <% if (!holders.isEmpty()) { %>
        <div class="card">
            <div class="" role="tab" id="headingOne">
                <a data-toggle="collapse" href="#collapse3" aria-expanded="true" aria-controls="collapseOne" class="btnUpDown collapsed">Derechos <span class="mas">+</span><span class="menos">-</span></a>
            </div>
            <div id="collapse3" class="collapse" role="tabpanel" aria-labelledby="headingOne" data-parent="#accordion">
                <ul>
                    <li>
                        <label class="form-check-label"><input class="form-check-input" type="checkbox" value="">Todos</label>
                        <ul>
                            <li><label class="form-check-label"><input class="form-check-input" type="checkbox" value="">Sin derechos</label></li>
                            <li><label class="form-check-label"><input class="form-check-input" type="checkbox" value="">Derechos reservados</label></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
        <% } %>
        <%
            if (showFilters) {
        %>
        <div class="card cardfecha">
            <div class="form-group">
                <label for="selfecha">Ordenar por:</label>
                <select class="form-control" id="selfecha" onchange="sort('<%=word%>', this)">
                    <option value="datedes">Fecha</option>
                    <option value="relvdes">Relevancia</option>
                    <option value="statdes">Popularidad</option>
                </select>
            </div>
        </div>
        <button type="button" class="btn btn-negro">Aplicar filtros</button>
        <button type="button" class="btn btn-negro" onclick="doSave('<%=word%>');">Guardar cambios</button>
        <% } else { %>
        <input type="submit" value="Guardar cambios" class="btn btn-negro"></input>
        <% }%>
    </div>
</div>