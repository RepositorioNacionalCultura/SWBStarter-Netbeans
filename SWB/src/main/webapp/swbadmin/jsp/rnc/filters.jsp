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
    String word = (String) request.getAttribute("word");
    Aggregation aggs = (Aggregation) request.getAttribute("aggs");
    SWBParamRequest paramRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    SWBResourceURL pageURL = paramRequest.getRenderUrl().setMode("SORT");
    pageURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    if (null != aggs) {
        showFilters = true;
        if (null != aggs.getDates()) {
            dates = aggs.getDates();
        }
        if (null != aggs.getRights()) {
            rights = aggs.getRights();
        }
        if (null != aggs.getHolders()) {
            holders = aggs.getHolders();
        }
        if (null != aggs.getLanguages()) {
            languages = aggs.getLanguages();
        }
        if (null != aggs.getMediastype()) {
            mediastype = aggs.getMediastype();
        }
        if (null != aggs.getResourcetypes()) {
            resourcetypes = aggs.getResourcetypes();
        }
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
        var lang = 'lang=';
        var dates = 'dates=';
        var holder = 'holder=';
        var rights = 'rights=';
        var mediatype = 'mediatype=';
        var resourcetype='resourcetype=';
        var inputElements = document.getElementsByClassName('form-check-input');
        for (i=0; i<inputElements.length; i++) {
            if (inputElements[i].checked) {
                if (inputElements[i].name == 'resourcetype') {
                    resourcetype += ','+inputElements[i].value;
                }else if (inputElements[i].name == 'mediatype') {
                    mediatype += ','+inputElements[i].value;
                }else if (inputElements[i].name == 'rights') {
                    rights += ','+inputElements[i].value;
                }else if (inputElements[i].name == 'lang') {
                    lang += ','+inputElements[i].value;
                }else if (inputElements[i].name == 'holder') {
                    holder += ','+inputElements[i].value;
                }   
            }
        }
        dates+=document.getElementById("ex1").value;
        if (lang.length > 5) {lang = lang.replace("=,","=");}else {lang=''}
        if (rights.length > 7) {rights = rights.replace("=,","=");}else {rights=''}
        if (holder.length > 7) {holder = holder.replace("=,","=");}else {holder=''}
        if (mediatype.length > 10) {mediatype = mediatype.replace("=,","=");}else {mediatype=''}
        if (resourcetype.length > 13) {resourcetype = resourcetype.replace("=,","=");}else {resourcetype=''}
        filters += resourcetype + mediatype + rights + lang + holder + dates;
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
</script>
<div id="sidebar">
    <div id="accordion" role="tablist">
    <%	if (null != resourcetypes && !resourcetypes.isEmpty()) { %>
        <div class="card">
            <div class="" role="tab" id="headingOne">
                <a data-toggle="collapse" href="#collapse1" aria-expanded="true" aria-controls="collapseOne" class="btnUpDown collapsed"><%=paramRequest.getLocaleString("usrmsg_view_search_themes")%> <span class="mas ion-plus"></span><span class="menos ion-minus"></span></a>
            </div>
            <div id="collapse1" class="collapse" role="tabpanel" aria-labelledby="headingOne" data-parent="#accordion">
                <ul>
                    <li>
                        <ul>
                            <%
                                for (CountName r : resourcetypes) {
                            %>
                                    <li><label class="form-check-label"><input class="form-check-input" type="checkbox" onclick="filter()" name="resourcetype" value="<%=r.getName()%>"><%=r.getName()%> <%=r.getCount()%></label></li>
                            <%
				}
                            %>
                        </ul>
                    </li>
                </ul>
		<p class=""><a href="#"><span class="ion-plus-circled"></span> <%=paramRequest.getLocaleString("usrmsg_view_search_show_more")%></a></p>
            </div>
        </div>
	<% } %>

	<%  if (null != mediastype && !mediastype.isEmpty()) { %>
        <div class="card">
            <div class="" role="tab" id="headingOne">
                <a data-toggle="collapse" href="#collapse2" aria-expanded="true" aria-controls="collapseOne" class="btnUpDown collapsed"><%=paramRequest.getLocaleString("usrmsg_view_search_media")%> <span class="mas ion-plus"></span><span class="menos ion-minus"></span></a>
            </div>
            <div id="collapse2" class="collapse" role="tabpanel" aria-labelledby="headingOne" data-parent="#accordion">
                <ul>
                    <li>
                        <ul>
                            <%
                                for (CountName r : mediastype) {
                            %>
                                    <li><label class="form-check-label"><input class="form-check-input" type="checkbox" onclick="filter()" name="mediatype" value="<%=r.getName()%>"><%=r.getName()%> <%=r.getCount()%></label></li>
                            <%
				}
                            %>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
	<% } %>

	<%  if (!dates.isEmpty()) { %>
        <div class="card">
            <div class="" role="tab" id="headingOne">
                <a data-toggle="collapse" href="#collapse3" aria-expanded="true" aria-controls="collapseOne" class="btnUpDown collapsed"><%=paramRequest.getLocaleString("usrmsg_view_search_date")%> <span class="mas ion-plus"></span><span class="menos ion-minus"></span></a>
            </div>
            <div id="collapse3" class="collapse" role="tabpanel" aria-labelledby="headingOne" data-parent="#accordion">
                <div class="slider">  
                    <p class="oswM">[<%=aggs.getInterval().getLowerLimit() %> - <%=aggs.getInterval().getUpperLimit() %>]</p>               
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
            <div class="" role="tab" id="headingOne">
                <a data-toggle="collapse" href="#collapse4" aria-expanded="true" aria-controls="collapseOne" class="btnUpDown collapsed"><%=paramRequest.getLocaleString("usrmsg_view_search_rights")%> <span class="mas ion-plus"></span><span class="menos ion-minus"></span></a>
            </div>
            <div id="collapse4" class="collapse" role="tabpanel" aria-labelledby="headingOne" data-parent="#accordion">
                <ul>
                    <li>
                        <ul>
                            <%
                                for (CountName r : rights) {
                            %>
                                    <li><label class="form-check-label"><input class="form-check-input" type="checkbox" onclick="filter()" name="rights" value="<%=r.getName()%>"><%=r.getName()%> <%=r.getCount()%></label></li>
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
            <div class="" role="tab" id="headingOne">
                <a data-toggle="collapse" href="#collapse5" aria-expanded="true" aria-controls="collapseOne" class="btnUpDown collapsed"><%=paramRequest.getLocaleString("usrmsg_view_search_languages")%> <span class="mas ion-plus"></span><span class="menos ion-minus"></span></a>
            </div>
            <div id="collapse5" class="collapse" role="tabpanel" aria-labelledby="headingOne" data-parent="#accordion">
                <ul>
                    <li>
                        <ul>
                            <%
                                for (CountName r : languages) {
                                    if (null != r.getName() && !r.getName().equalsIgnoreCase("es")) {
                            %>
                                        <li><label class="form-check-label"><input class="form-check-input" type="checkbox" onclick="filter()" name="rights" value="<%=r.getName()%>"><%=r.getName()%> <%=r.getCount()%></label></li>
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
            <div class="" role="tab" id="headingOne">
                <a data-toggle="collapse" href="#collapse6" aria-expanded="true" aria-controls="collapseOne" class="btnUpDown collapsed"><%=paramRequest.getLocaleString("usrmsg_view_search_holder")%> <span class="mas ion-plus"></span><span class="menos ion-minus"></span></a>
            </div>
            <div id="collapse6" class="collapse" role="tabpanel" aria-labelledby="headingOne" data-parent="#accordion">
                <ul>
                    <%
			for (CountName r : holders) {
                    %>
                            <li><label class="form-check-label"><input class="form-check-input" type="checkbox" onclick="filter()" name="holder" value="<%=r.getName()%>"><%=r.getName()%> <%=r.getCount()%></label></li>
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