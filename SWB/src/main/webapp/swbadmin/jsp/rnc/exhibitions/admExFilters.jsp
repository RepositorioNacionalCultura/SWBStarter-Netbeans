<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.response.Aggregation"%>
<%@page import="mx.gob.cultura.portal.response.CountName"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest,org.semanticwb.portal.api.SWBResourceURL,java.util.ArrayList, java.util.List"%>
<%
    boolean showFilters = false;
    List<CountName> dates = new ArrayList<>();
    List<CountName> holders = new ArrayList<>();
    List<CountName> resourcetypes = new ArrayList<>();
    String word = (String)request.getAttribute("criteria");
    List<Aggregation> aggs = (List<Aggregation>)request.getAttribute("aggs");
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    SWBResourceURL pageURL = paramRequest.getRenderUrl().setMode("SORT");
    pageURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    if (null != aggs && null != word) {
        showFilters = true;
        for (Aggregation a : aggs) {
            if (null !=  a.getHolders()) holders.addAll(a.getHolders());
            if (null !=  a.getDates()) dates.addAll(a.getDates());
            if (null !=  a.getResourcetypes()) resourcetypes.addAll(a.getResourcetypes());
        }
    }
%>
<script type="text/javascript">
    function doSort(w, f) {
        dojo.xhrPost({
            url: '<%=pageURL%>?criteria='+w+'&sort='+f,
            load: function(data) {
                dojo.byId('references').innerHTML=data;
            }
        });
    }
</script>
<div class="" id="sidebar">
    <div id="accordion" role="tablist">
        <% if (!resourcetypes.isEmpty()) { %>
        <div class="card">
            <div class="" role="tab" id="headingOne">
                <a data-toggle="collapse" href="#collapse1" aria-expanded="true" aria-controls="collapseOne">Tipos de objeto <span>+</span></a>
            </div>
            <div id="collapse1" class="collapse show" role="tabpanel" aria-labelledby="headingOne" data-parent="#accordion">
                <ul>
                    <li>
                        <label class="form-check-label"><input class="form-check-input" type="checkbox" value="">Todos</label>
                        <ul>
                            <%
                                for (CountName r : resourcetypes) {
                                    %>
										<li><label class="form-check-label"><input class="form-check-input" type="checkbox" value=""><%=r.getCapitalizeName()%></label></li>
                                    <%
                                }
                            %>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
        <% } %>
        <% if (!holders.isEmpty()) { %>
        <div class="card">
            <div class="" role="tab" id="headingThree">
                <a data-toggle="collapse" href="#collapse3" aria-expanded="true" aria-controls="collapseOne">Institución <span>+</span></a>
            </div>
            <div id="collapse3" class="collapse show" role="tabpanel" aria-labelledby="headingThree" data-parent="#accordion">
                <ul>
                    <li>
                        <label class="form-check-label"><input class="form-check-input" type="checkbox" value="">Todas</label>
                        <ul>
                            <%
                                for (CountName h : holders) {
                                    %>
                                    <li><label class="form-check-label"><input class="form-check-input" type="checkbox" value=""><%=h.getName()%></label></li>
                                    <%
                                }
                            %>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
        <% } %>
        <% if (!dates.isEmpty()) { %>
        <div class="card">
            <div class="" role="tab" id="headingFour">
                <a data-toggle="collapse" href="#collapse4" aria-expanded="true" aria-controls="collapseOne">Fecha <span>+</span></a>
            </div>
            <div id="collapse4" class="collapse show" role="tabpanel" aria-labelledby="headingFour" data-parent="#accordion">
                <ul>
                    <li>
                        <% if (dates.size() > 1) { %>
                            <label class="form-check-label"><input class="form-check-input" type="checkbox" value="">Todas</label>
                        <% } %>
                        <ul>
                            <%
                                for (CountName d : dates) {
                                    %>
										<li><label class="form-check-label"><input class="form-check-input" type="checkbox" value=""><%=d.getName().substring(0,4)%></label></li>
                                    <%
                                }
                            %>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
        <%
        }
			if (showFilters) {
        %>
				<button type="button" class="">Aplicar filtros</button>
        <% } %>
		<input type="submit" value="Guardar cambios" class=""></input>
    </div>
</div>