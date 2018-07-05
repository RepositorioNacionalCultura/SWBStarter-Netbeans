<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.response.Aggregation"%>
<%@page import="mx.gob.cultura.portal.response.CountName"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest,org.semanticwb.portal.api.SWBResourceURL,java.util.ArrayList, java.util.List"%>
<%
    boolean showFilters = false;
    List<CountName> dates = new ArrayList<>();
    List<CountName> rights = new ArrayList<>();
    List<CountName> holders = new ArrayList<>();
    List<CountName> languages = new ArrayList<>();
    List<CountName> mediastype = new ArrayList<>();
    List<CountName> resourcetypes = new ArrayList<>();
    String word = (String)request.getAttribute("word");
    Aggregation aggs = (Aggregation)request.getAttribute("aggs");
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    SWBResourceURL pageURL = paramRequest.getRenderUrl().setMode("SORT");
    pageURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    if (null != aggs) {
        showFilters = true;
	if (null !=  aggs.getDates()) dates = aggs.getDates();
	if (null !=  aggs.getRights()) rights = aggs.getRights();
        if (null !=  aggs.getHolders()) holders = aggs.getHolders();
        if (null !=  aggs.getLanguages()) languages = aggs.getLanguages();
	if (null !=  aggs.getMediastype()) mediastype = aggs.getMediastype();
        if (null !=  aggs.getResourcetypes()) resourcetypes = aggs.getResourcetypes();
    }
%>
<script type="text/javascript">
    function sort(f) {
        doSort('<%=word%>',f.value);
    }
    function reset() {
        var inputElements = document.getElementsByClassName('form-check-input');
	for (i=0; i<inputElements.length; i++) {
            inputElements[i].checked = false;
	}
    }
    function filter() {
	var filters = '&';
	var rights = '&rights=';
        var holders = 'holders=';
	var dates = '&datecreated=';
        var mediatype = '&mediatype=';
	var languages = '&languages=';
        var resourcetype='resourcetype=';
	var inputElements = document.getElementsByClassName('form-check-input');
        for (i=0; i<inputElements.length; i++) {
            if (inputElements[i].checked) {
                if (inputElements[i].name == 'resourcetype') {
                    resourcetype += '::'+inputElements[i].value;
		}else if (inputElements[i].name == 'mediatype') {
                    mediatype += '::'+inputElements[i].value;
                }else if (inputElements[i].name == 'rights') {
                    rights += '::'+inputElements[i].value;
		}else if (inputElements[i].name == 'languages') {
                    languages += '::'+inputElements[i].value;
                }else if (inputElements[i].name == 'holders') {
                    holders += '::'+inputElements[i].value;
                }
            }
	}
	dates+=document.getElementById("bx1").value+","+document.getElementById("bx2").value;
	if (languages.length > 11) {languages = languages.replace("=::","=");}else {languages=''}
	if (rights.length > 8) {rights = rights.replace("=::","=");}else {rights=''}
        if (holders.length > 8) {holders = holders.replace("=::","=");}else {holders=''}
	if (mediatype.length > 11) {mediatype = mediatype.replace("=::","=");}else {mediatype=''}
        if (resourcetype.length > 13) {resourcetype = resourcetype.replace("=::","=");}else {resourcetype=''}
	filters += resourcetype + mediatype + rights + languages + holders + dates;
        doSort('<%=word%>'+filters,'relvdes');
    }
    function doSort(w, f) {
        dojo.xhrPost({
            url: '<%=pageURL%>?word='+w+'&sort='+f,
            load: function(data) {
                dojo.byId('references').innerHTML=data;
            }
        });
    }
    function validate(val, min, max) {
    }
</script>
<div id="sidebar">
    <div id="accordion" role="tablist">
    <%  if (null != resourcetypes && !resourcetypes.isEmpty()) { %>
        <div class="card card-temas">
            <div class="" role="tab" id="heading1">
                <a data-toggle="collapse" href="#collapse1" aria-expanded="true" aria-controls="collapse1" class="btnUpDown collapsed"><%=paramRequest.getLocaleString("usrmsg_view_search_themes")%> <span class="mas ion-plus"></span><span class="menos ion-minus"></span></a>
            </div>
            <div id="collapse1" class="collapse show" role="tabpanel" aria-labelledby="heading1" data-parent="#accordion">
                <ul>
                    <li>
                        <ul>
                            <%
				int i = 0;
                                for (CountName r : resourcetypes) {
                            %>
                                    <li><label class="form-check-label"><input class="form-check-input" type="checkbox" onclick="filter()" name="resourcetype" value="<%=r.getName()%>"><span><%=r.getName()%></span><span> <%=r.getCount()%></span></label></li>
                            <%
                                    if (i>3) break; else i++; 
				}
				if (i<resourcetypes.size()) {
                            %>
                                    <div class="collapse" id="vermas">
                            <%
                                    int j=0;
                                    for (CountName r : resourcetypes) {
                                        if (j<i) {j++; continue;} 
					else { 
                            %>
                                            <li><label class="form-check-label"><input class="form-check-input" type="checkbox" onclick="filter()" name="resourcetype" value="<%=r.getName()%>"><span><%=r.getName()%></span><span> <%=r.getCount()%></span></label></li>
                            <%			
                                        }
                                    }   
                            %>
                                    </div>
                            <% } %>
                        </ul>
                    </li>
                </ul>
		<p class="vermas vermas-filtros">
                    <button class="btn-vermas" type="button" data-toggle="collapse" data-target="#vermas" aria-expanded="false" aria-controls="vermas">
                        <span class="ion-plus-circled"></span>
                        <%=paramRequest.getLocaleString("usrmsg_view_search_show_more")%>
                    </button>
		</p>
            </div>
        </div>
    <% } %>

    <%  if (null != mediastype && !mediastype.isEmpty()) { %>
        <div class="card card-media">
            <div class="" role="tab" id="heading2">
                <a data-toggle="collapse" href="#collapse2" aria-expanded="true" aria-controls="collapseOne" class="btnUpDown collapsed"><%=paramRequest.getLocaleString("usrmsg_view_search_media")%> <span class="mas ion-plus"></span><span class="menos ion-minus"></span></a>
            </div>
            <div id="collapse2" class="collapse show" role="tabpanel" aria-labelledby="heading2" data-parent="#accordion">
                <ul>
                    <li>
                        <ul>
                            <%
                                for (CountName r : mediastype) {
                            %>
                                    <li><label class="form-check-label"><input class="form-check-input" type="checkbox" onclick="filter()" name="mediatype" value="<%=r.getName()%>"><span><%=r.getName()%></span><span> <%=r.getCount()%></span></label></li>
                            <%
				}
                            %>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
	<% } %>

	<%	if (!dates.isEmpty()) { %>
        <div class="card card-fecha">
            <div class="" role="tab" id="heading3">
                <a data-toggle="collapse" href="#collapse3" aria-expanded="true" aria-controls="collapse3" class="btnUpDown collapsed"><%=paramRequest.getLocaleString("usrmsg_view_search_date")%> <span class="mas ion-plus"></span><span class="menos ion-minus"></span></a>
            </div>
            <div id="collapse3" class="collapse show" role="tabpanel" aria-labelledby="heading3" data-parent="#accordion">
                <div class="slider">  
                    <p class="oswM">[ <input id="bx1" type="text" class="sliderinput oswB rojo" onchange="validate(this.value, <%=aggs.getInterval().getLowerLimit() %>, <%=aggs.getInterval().getUpperLimit()%>);" value="<%=aggs.getInterval().getLowerLimit() %>" /> - <input id="bx2" type="text" class="sliderinput oswB rojo" onchange="filter();" value="<%=aggs.getInterval().getUpperLimit()%>" /> ]</p>               
                    <input id="ex1" data-slider-id='ex1Slider' type="text" data-slider-min="<%=aggs.getInterval().getLowerLimit() %>" data-slider-max="<%=aggs.getInterval().getUpperLimit() %>" data-slider-step="1" data-slider-value="[<%=aggs.getInterval().getLowerLimit() %>,<%=aggs.getInterval().getUpperLimit() %>]"/>
                    <div class="d-flex">
			<div class="p-2" id="ex1SliderVal"><%=aggs.getInterval().getLowerLimit() %></div>
                        <div class="ml-auto p-2" id="ex2SliderVal"><%=aggs.getInterval().getUpperLimit() %></div>
                    </div>
                </div>
            </div>
        </div>		
	<% } %>

	<%  if (null != rights && !rights.isEmpty()) { %>
        <div class="card">
            <div class="" role="tab" id="heading4">
                <a data-toggle="collapse" href="#collapse4" aria-expanded="true" aria-controls="collapse4" class="btnUpDown collapsed"><%=paramRequest.getLocaleString("usrmsg_view_search_use")%> <span class="mas ion-plus"></span><span class="menos ion-minus"></span></a>
            </div>
            <div id="collapse4" class="collapse" role="tabpanel" aria-labelledby="heading4" data-parent="#accordion">
                <ul>
                    <li>
                        <ul>
                            <%
                                for (CountName r : rights) {
                            %>
                                    <li><label class="form-check-label"><input class="form-check-input" type="checkbox" onclick="filter()" name="rights" value="<%=r.getName()%>"><span><%=r.getName()%></span><span><%=r.getCount()%></span></label></li>
                            <%
				}
                            %>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
	<% } %>

	<%  if (null != languages && !languages.isEmpty()) { %>
        <div class="card">
            <div class="" role="tab" id="heading5">
                <a data-toggle="collapse" href="#collapse5" aria-expanded="true" aria-controls="collapse5" class="btnUpDown collapsed"><%=paramRequest.getLocaleString("usrmsg_view_search_languages")%> <span class="mas ion-plus"></span><span class="menos ion-minus"></span></a>
            </div>
            <div id="collapse5" class="collapse" role="tabpanel" aria-labelledby="heading5" data-parent="#accordion">
                <ul>
                    <li>
                        <ul>
                            <%
                                for (CountName r : languages) {
                                    if (null != r.getName() && !r.getName().equalsIgnoreCase("es")) {
                            %>
					<li><label class="form-check-label"><input class="form-check-input" type="checkbox" onclick="filter()" name="languages" value="<%=r.getName()%>"><span><%=r.getName()%></span><span> <%=r.getCount()%></span></label></li>
                            <%
                                    }
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
            <div class="" role="tab" id="heading6">
                <a data-toggle="collapse" href="#collapse6" aria-expanded="true" aria-controls="collapse6" class="btnUpDown collapsed"><%=paramRequest.getLocaleString("usrmsg_view_search_holder")%> <span class="mas ion-plus"></span><span class="menos ion-minus"></span></a>
            </div>
            <div id="collapse6" class="collapse" role="tabpanel" aria-labelledby="heading5" data-parent="#accordion">
                <ul>
                    <%
                        for (CountName r : holders) {
                    %>
                            <li><label class="form-check-label"><input class="form-check-input" type="checkbox" onclick="filter()" name="holders" value="<%=r.getName()%>"><span><%=r.getName()%></span><span> <%=r.getCount()%></span></label></li>
                    <%
			}
                    %>
                </ul>
            </div>
        </div>
	<% } %>
	<%
            if (showFilters) {
        %>
		<div class="card cardfecha">
                    <div class="form-group">
			<label for="selfecha"><%=paramRequest.getLocaleString("usrmsg_view_search_order_by")%>:</label>
                        <select class="form-control" id="selfecha" onchange="sort(this)">
                            <option value="datedes"><%=paramRequest.getLocaleString("usrmsg_view_search_date")%></option>
                            <option value="relvdes"><%=paramRequest.getLocaleString("usrmsg_view_search_relevance")%></option>
                            <option value="statdes"><%=paramRequest.getLocaleString("usrmsg_view_search_popularity")%></option>
			</select>   
                    </div>
		</div>
		<button type="button" onclick="reset();" class="btn-cultura btn-negro"><%=paramRequest.getLocaleString("usrmsg_view_search_delete_filters")%></button>
    <%  } %>
    </div>
</div>