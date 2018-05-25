/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.resources;

import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.ArrayList;
import org.semanticwb.Logger;
import org.semanticwb.SWBUtils;
import org.semanticwb.SWBPlatform;
import org.semanticwb.model.WebSite;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceModes;
import org.semanticwb.portal.api.SWBResourceURL;
import org.semanticwb.portal.api.SWBResourceException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


import mx.gob.cultura.portal.utils.Utils;
import mx.gob.cultura.portal.request.ListBICRequest;
import mx.gob.cultura.portal.response.Aggregation;
import mx.gob.cultura.portal.response.CountName;
import mx.gob.cultura.portal.response.DateRange;
import mx.gob.cultura.portal.response.DigitalObject;
import mx.gob.cultura.portal.response.Document;
import mx.gob.cultura.portal.response.Entry;
import org.semanticwb.SWBException;

/**
 *
 * @author sergio.tellez
 */
public class SearchCulturalProperty extends PagerAction {

    private static final int SEGMENT = 8;
    private static final Logger LOG = SWBUtils.getLogger(SearchCulturalProperty.class);

    @Override
    public void doAdmin(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws IOException {
        StringBuilder ret = new StringBuilder();
        try {
            if ("add".equals(paramRequest.getAction()) || "edit".equals(paramRequest.getAction())) {
                SWBResourceURL url=paramRequest.getRenderUrl();
                url.setMode(SWBResourceModes.Mode_ADMIN);
                url.setAction("save");
                ret.append("<form method=\"POST\" action=\"").append(url.toString()).append("\"> \n");
                ret.append("	<div class=swbform> \n");
                ret.append("		<table width=\"100%\"  border=\"0\" cellpadding=\"5\" cellspacing=\"0\"> \n");
                ret.append("			<tr> \n");
                ret.append("				<td class=\"datos\">URL Endpoint: </td> \n");
                ret.append("				<td class=\"valores\"> \n");
                ret.append("					<input type=\"text\" name=\"endpointURL\" value=\"").append(getResourceBase().getAttribute("endpointURL","").trim()).append("\" size=\"40\">");
                ret.append("				</td>");
                ret.append("			</tr> \n");
                ret.append("			<tr> \n");
                ret.append("				<td class=\"datos\">Resultados por página: </td> \n");
                ret.append("				<td class=\"valores\"> \n");
                ret.append("					<input type=\"text\" name=\"resultsPage\" value=\"").append(getResourceBase().getAttribute("resultsPage","").trim()).append("\" size=\"40\">");
                ret.append("				</td>");
                ret.append("			</tr> \n");
                ret.append("			<tr> \n");
                ret.append("				<td colspan=2 align=right> \n");
                ret.append("					<br><hr size=1 noshade> \n");
                ret.append("					<input type=submit name=btnSave value=\"Enviar\" class=boton> \n");
                ret.append("				</td> \n");
                ret.append("			</tr> \n");
                ret.append("		</table> \n");
                ret.append("	</div> \n");
                ret.append("</form> \n");
            }else if ("save".equals(paramRequest.getAction())) {
                getResourceBase().setAttribute("endpointURL", request.getParameter("endpointURL"));
                getResourceBase().setAttribute("resultsPage", request.getParameter("resultsPage"));
                getResourceBase().updateAttributesToDB();
                SWBResourceURL url=paramRequest.getRenderUrl();
                url.setMode(SWBResourceModes.Mode_ADMIN);
                url.setAction("resume");
                response.sendRedirect(url.toString());
            }else if ("resume".equals(paramRequest.getAction())) {
                SWBResourceURL url=paramRequest.getRenderUrl();
                url.setMode(SWBResourceModes.Mode_ADMIN);
                url.setAction("edit");
                ret.append("<form method=\"POST\" action=\"").append(url.toString()).append("\"> \n");
                ret.append("	<div class=swbform> \n");
                ret.append("		<table width=\"100%\"  border=\"0\" cellpadding=\"5\" cellspacing=\"0\"> \n");
                ret.append("			<tr> \n");
                ret.append("				<td class=\"datos\">URL Endpoint: </td> \n");
                ret.append("				<td class=\"valores\"> \n").append(getResourceBase().getAttribute("endpointURL","")).append("</td>");
                ret.append("			</tr> \n");
                ret.append("			<tr> \n");
                ret.append("				<td class=\"datos\">Resultados por página: </td> \n");
                ret.append("				<td class=\"valores\"> \n").append(getResourceBase().getAttribute("resultsPage","").trim()).append("</td>");
                ret.append("			</tr> \n");
                ret.append("			<tr> \n");
                ret.append("				<td colspan=2 align=right> \n");
                ret.append("					<br><hr size=1 noshade> \n");
                ret.append("					<input type=submit name=btnSave value=\"Regresar\" class=boton> \n");
                ret.append("				</td> \n");
                ret.append("			</tr> \n");
                ret.append("		</table> \n");
                ret.append("	</div> \n");
                ret.append("</form> \n");
            }
        }catch (SWBException e) { LOG.info(e.getMessage());}
        response.getWriter().print(ret.toString());
    }

    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, java.io.IOException {
        //response.setCharacterEncoding("UTF-8");
        List<Entry> publicationList = new ArrayList<>();
        String url = "/swbadmin/jsp/rnc/documents.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(url);
        String q = request.getParameter("word");

        if (null != q && !q.isEmpty()) {
            Document document = getReference(request, paramRequest.getWebPage().getWebSite());
            if (null != document) {
                publicationList = document.getRecords();
                setType(document.getRecords());
                request.setAttribute("aggs", getAggregation(document.getAggs()));
                request.setAttribute("creators", getCreators(document.getRecords()));
                request.getSession().setAttribute(FULL_LIST, document.getRecords());
                request.getSession().setAttribute("NUM_RECORDS_TOTAL", document.getTotal());
            }
            request.setAttribute("word", q);
            init(request, response, paramRequest);
        }

        try {
            request.setAttribute("references", publicationList);
            request.setAttribute("paramRequest", paramRequest);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.error(se);
        }
    }

    @Override
    public void doSort(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, java.io.IOException {
        Document document = null;
        List<Entry> publicationList = new ArrayList<>();
        try {
            if (null != request.getParameter("word") && !request.getParameter("word").isEmpty()) {
                document = getReference(request, paramRequest.getWebPage().getWebSite());
                if (null != document) {
                    publicationList = document.getRecords();
                    setType(document.getRecords());
                    request.getSession().setAttribute(FULL_LIST, document.getRecords());
                }
                request.setAttribute("f", request.getParameter("sort"));
                request.setAttribute("word", request.getParameter("word"));
                init(request, response, paramRequest);
            }
            request.setAttribute("references", publicationList);
            request.setAttribute("paramRequest", paramRequest);
        }catch (Exception se) {
            LOG.error(se);
        }
        super.doSort(request, response, paramRequest);
    }

    @Override
    public void doPage(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws java.io.IOException {
        int pagenum = 0;
        Document document = null;
        String p = request.getParameter("p");
        HttpSession session = request.getSession();
        List<Entry> publicationList = null;
        if (null != p)
            pagenum = Integer.parseInt(p);
        if (pagenum<=0) pagenum = 1;
        session.setAttribute(NUM_PAGE_LIST, pagenum);
        session.setAttribute("PAGE_NUM_ROW", PAGE_NUM_ROW);
        document = getReference(request, paramRequest.getWebPage().getWebSite());
        if (null != document) {
            publicationList = document.getRecords();
            setType(document.getRecords());
            request.getSession().setAttribute(FULL_LIST, publicationList);
            page(pagenum, session);
        }
        request.setAttribute("word", request.getParameter("word"));
        String url = "/swbadmin/jsp/rnc/rows.jsp";
        if (null != request.getParameter("m") && "l".equalsIgnoreCase(request.getParameter("m")))
            request.setAttribute("mode", "row lista");
        else request.setAttribute("mode", "card-columns");
        request.setAttribute("m",request.getParameter("m"));
        RequestDispatcher rd = request.getRequestDispatcher(url);
        try {
            request.setAttribute("paramRequest", paramRequest);
            rd.include(request, response);
        }catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }

    private Document getReference(HttpServletRequest request, WebSite site) {
        Document document = null;
        String words = request.getParameter("word");

        //Get baseURI from site properties first
        String baseUri = site.getModelProperty("search_endPoint");
        if (null == baseUri || baseUri.isEmpty()) {
            baseUri = SWBPlatform.getEnv("rnc/endpointURL",
                    getResourceBase().getAttribute("endpointURL",
                            "http://localhost:8080")).trim();
        }

        String uri = baseUri + "/api/v1/search?q=";
        try {
            uri += URLEncoder.encode(getParamSearch(words), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException uex) {
            LOG.error(uex);
        }

        uri += getRange(request);
        if (null != request.getParameter("sort")) {
            if (request.getParameter("sort").equalsIgnoreCase("datedes")) uri += "&sort=-datecreated.value";
            if (request.getParameter("sort").equalsIgnoreCase("dateasc")) uri += "&sort=datecreated.value";
            if (request.getParameter("sort").equalsIgnoreCase("statdes")) uri += "&sort=-resourcestats.views";
            if (request.getParameter("sort").equalsIgnoreCase("statasc")) uri += "&sort=resourcestats.views";
        }
        ListBICRequest req = new ListBICRequest(uri);
        try {
            document = req.makeRequest();
        }catch (Exception se) {
            LOG.error(se);
        }
        return document;
    }

    private List<String> getCreators(List<Entry> records) {
        List<String> creators = new ArrayList<>();
        if (null != records) {
            for (Entry entry : records) {
                for (String author : entry.getCreator()) {
                    author = getCapitalizeName(author);
                    if (!creators.contains(author))
                        creators.add(author);
                }
            }
        }
        return creators;
    }

    public static String getCapitalizeName(String name) {
        if (null == name) return "";
        name = name.trim();
        if (name.isEmpty() || name.length() < 2) return name;
        name = name.toLowerCase();
        if (name.contains(" ")) {
            String[] words = name.split(" ");
            StringBuilder capitalize = new StringBuilder();
            for (int i=0; i<words.length; i++) {
                String word = words[i].trim();
                if (word.length() > 2) word = Character.toUpperCase(word.charAt(0)) + word.substring(1);
                if (!word.contains(","))
                    capitalize.append(word).append(" ");
                else {
                    word = word.replace(",","");
                    capitalize.append(word);
                    break;
                }
            }
            name = capitalize.toString().trim();
        }else
            name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        return name;
    }

    private Aggregation getAggregation(List<Aggregation> aggs) {
        DateRange interval = new DateRange();
        Calendar cal = Calendar.getInstance();
        Aggregation aggregation = new Aggregation();
        interval.setUpperLimit(0);
        cal.setTime(new Date());
        interval.setLowerLimit(cal.get(Calendar.YEAR));
        aggregation.setInterval(interval);
        if (null != aggs && !aggs.isEmpty()) {
            aggregation.setDates(new ArrayList<>());
            aggregation.setHolders(new ArrayList<>());
            aggregation.setResourcetypes(new ArrayList<>());
            for (Aggregation a : aggs) {
                if (null !=  a.getDates()) aggregation.getDates().addAll(a.getDates());
                if (null !=  a.getHolders()) aggregation.getHolders().addAll(a.getHolders());
                if (null !=  a.getResourcetypes()) aggregation.getResourcetypes().addAll(a.getResourcetypes());
            }
            for (CountName date : aggregation.getDates()) {
                cal.setTime(Utils.convert(date.getName()));
                if (interval.getUpperLimit() < cal.get(Calendar.YEAR)) interval.setUpperLimit(cal.get(Calendar.YEAR));
                if (interval.getLowerLimit() > cal.get(Calendar.YEAR)) interval.setLowerLimit(cal.get(Calendar.YEAR));
            }
        }
        return aggregation;
    }

    private String getRange(HttpServletRequest request) {
        int s = 0;
        StringBuilder range = new StringBuilder("&from=");
        String start = request.getParameter("p");
        if (null != start) {
            try {
                s = Integer.parseInt(start);
                if (s > 1) s = (s-1)*SEGMENT;
            }catch (NumberFormatException e) { }
        }
        range.append(String.valueOf(s));
        range.append("&size=").append(SEGMENT);
        return range.toString();
    }

    private String getParamSearch(String words) {
        StringBuilder parameters = new StringBuilder();
        String fix = words.replaceAll(",", " ").replaceAll(" ,", " ").replaceAll(", ", " ");
        String[] search = fix.split(" ");
        if (search.length > 0) {
            for (int i=0; i<search.length; i++) {
                parameters.append("%2B");
                String param  = search[i].trim();
                parameters.append(param);
            }
            if (parameters.length() > 1)
                parameters.delete(0, 3);
            return parameters.toString();
        }else
            return words;
    }
    
    private void setType(List<Entry> references) {
        if (null != references && !references.isEmpty()) {
            for (Entry e : references) {
                List<DigitalObject> list = e.getDigitalObject();
                if (null != list && !list.isEmpty()) {
                    DigitalObject dObj = list.get(0);
                    if (null != dObj && null != dObj.getMediatype() && null != dObj.getMediatype().getMime())
                        e.setType(dObj.getMediatype().getMime());
                }
            }
        }
    }
}