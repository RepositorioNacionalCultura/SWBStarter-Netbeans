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
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mx.gob.cultura.portal.utils.ArtWork;
import mx.gob.cultura.portal.response.Entry;
import mx.gob.cultura.portal.response.Document;
import mx.gob.cultura.portal.request.ListBICRequest;

import mx.gob.cultura.portal.utils.Utils;
import mx.gob.cultura.portal.response.CountName;
import mx.gob.cultura.portal.response.DateRange;
import mx.gob.cultura.portal.response.Aggregation;

import org.semanticwb.SWBPlatform;
import org.semanticwb.SWBException;
import org.semanticwb.model.WebSite;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBResourceException;
import static mx.gob.cultura.portal.resources.SearchCulturalProperty.getCapitalizeName;
/**
 *
 * @author sergio.tellez
 */
public class Exhibition extends GenericResource {
    
    private static final int SEGMENT = 8;
    private static final int PAGE_NUM_ROW = 8;
    private static final int PAGE_JUMP_SIZE = 5;
    
    private static final String PAGE = "PAGE";
    private static final String SAVE = "SAVE";
    private static final String SORT = "SORT";
    private static final String SEARCH = "SEARCH";
    private static final String PAGE_LIST = "PAGE_LIST";
    private static final String FULL_LIST = "FULL_LIST";
    private static final String NUM_PAGE_LIST = "NUM_PAGE_LIST";
    
    public static final String ACTION = "act";
    public static final String MODE_VIEW_EDIT = "vEdit";
    
    private static final Logger LOG = Logger.getLogger(ArtDetail.class.getName());
    
    public void doViewFix(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws IOException {
        String path = "/swbadmin/jsp/rnc/exhibitions/admExhibition.jsp";
        try {
            request.setAttribute("base", getResourceBase());
	    request.setAttribute("paramRequest", paramRequest);
            /**if ("SEARCH".equals(paramRequest.getAction())) {
                doAdminFilter(request, response, paramRequest);
            }else if ("PAGE".equals(paramRequest.getAction())) {
                doAdminPage(request, response, paramRequest);
            }/**else if ("SAVE".equals(paramRequest.getAction())) {
                doAdminSave(request, response, paramRequest);
            }else {**/
            RequestDispatcher rd = request.getRequestDispatcher(path);
            rd.include(request, response);
            //}
        } catch (ServletException ex) {
            Logger.getLogger(Exhibition.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        String mode = paramRequest.getMode();
        if (null != request.getParameter(ACTION) && MODE_VIEW_EDIT.equals(request.getParameter(ACTION))) {
            doViewFix(request, response, paramRequest);
        }else if (SEARCH.equals(mode)) {
            doAdminFilter(request, response, paramRequest);
        }else if (SAVE.equals(mode)) {
            doAdminSave(request, response, paramRequest);
        }else if (PAGE.equals(mode)) {
            doAdminPage(request, response, paramRequest);
        }else if (SORT.equals(mode)) {
            doAdminSort(request, response, paramRequest);
        }else
            super.processRequest(request, response, paramRequest);
    }
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, java.io.IOException {
    	request.setCharacterEncoding("UTF-8");
        Document document = null;
        List<Entry> publicationList = new ArrayList<>();
    	String url = "/swbadmin/jsp/rnc/exhibitions/exhibitions.jsp";
    	RequestDispatcher rd = request.getRequestDispatcher(url);
    	try {
            if (null != getResourceBase().getAttribute("criteria")) {
    		document = getReference(request);
                List<String> favs = getElements("favorites");
                List<String> hds = getElements("hiddenarts");
                if (null != document) {
                    for (Entry e : document.getRecords()) {
                        if (!hds.contains(e.getId())) {
                            if (favs.contains(e.getId())) publicationList.add(0, e);
                            else publicationList.add(e);
                        }
                    }
                    request.setAttribute(FULL_LIST, publicationList);
                    request.setAttribute("NUM_RECORDS_TOTAL", document.getTotal());
                    request.setAttribute("criteria", getResourceBase().getAttribute("criteria"));
                }
    	    }
            init(request);
            request.setAttribute("base", getResourceBase());
            request.setAttribute("references", publicationList);
	    request.setAttribute("paramRequest", paramRequest);
	    rd.include(request, response);
	}catch (ServletException se) {
            LOG.log(Level.SEVERE, se.getMessage());
	}
    }
    
    public void doAdminFilter(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws IOException {
    	String path = "/swbadmin/jsp/rnc/exhibitions/admExFilter.jsp";
    	try {
            if (null != request.getParameter("criteria") && !request.getParameter("criteria").isEmpty()) {
                getResourceBase().setAttribute("sort", request.getParameter("sort"));
                getResourceBase().setAttribute("type", request.getParameter("type"));
                getResourceBase().setAttribute("title", request.getParameter("title"));
                getResourceBase().setAttribute("order", request.getParameter("order"));
                getResourceBase().setAttribute("criteria", request.getParameter("criteria"));
                //getResourceBase().setAttribute("endpointURL", request.getParameter("endpointURL"));
                getResourceBase().updateAttributesToDB();
                Document document = getReference(request);//getReference(request, paramRequest.getWebPage().getWebSite());
                if (null != document) {
                    List<ArtWork> arts = getArts(request, document);
                    //request.setAttribute("aggs", document.getAggs());
                    request.setAttribute("aggs", getAggregation(document.getAggs()));
                    request.setAttribute("creators", getCreators(document.getRecords()));
                    request.setAttribute(FULL_LIST, arts);
                    request.setAttribute("NUM_RECORDS_TOTAL", document.getTotal());
                    request.setAttribute("criteria", request.getParameter("criteria"));
                }
                init(request);
            }
            request.setAttribute("f", request.getParameter("sort"));
            request.setAttribute("paramRequest", paramRequest);
            RequestDispatcher rd = request.getRequestDispatcher(path);
	    rd.include(request, response);
	} catch (ServletException | SWBResourceException ex) {
            Logger.getLogger(Exhibition.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SWBException ex) {
            Logger.getLogger(Exhibition.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    
    public void doAdminSave(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws IOException {
    	try {
            configPage(request);
            List<ArtWork> arts = (List<ArtWork>)request.getSession().getAttribute("arts");
            if (null != arts) {
                StringBuilder favorites = new StringBuilder();
                for (ArtWork art : arts) {
                    if (art.isFavorite())
                        favorites.append(",").append(art.getEntry().getId());
                }
                if (favorites.length() > 1)
                    getResourceBase().setAttribute("favorites", favorites.substring(1, favorites.length()));
            }
            if (null != request.getParameterValues("hiddenarts")) {
                getResourceBase().setAttribute("hiddenarts", getChekedEl(request, "hiddenarts"));
            }
            getResourceBase().updateAttributesToDB();
            doAdminResume(request, response, paramRequest);
	} catch (IOException | SWBException ex) {
            Logger.getLogger(Exhibition.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void doAdminResume(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws IOException {
        String path = "/swbadmin/jsp/rnc/exhibitions/admExResume.jsp";
        try {
            request.setAttribute("base", getResourceBase());
	    request.setAttribute("paramRequest", paramRequest);
            RequestDispatcher rd = request.getRequestDispatcher(path);
            rd.include(request, response);
        } catch (ServletException ex) {
            Logger.getLogger(Exhibition.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void doAdminPage(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws java.io.IOException {
        response.setContentType("text/html; charset=UTF-8");
        int pagenum = 0;
        Document document = null;
        String p = request.getParameter("p");
        if (null != p)
            pagenum = Integer.parseInt(p);
        if (pagenum<=0) pagenum = 1;
        configPage(request);
        request.setAttribute(NUM_PAGE_LIST, pagenum);
        request.setAttribute("PAGE_NUM_ROW", PAGE_NUM_ROW);
        document = getReference(request, paramRequest.getWebPage().getWebSite());
        if (null != document) {
            List<ArtWork> arts = getArts(request, document);
            request.setAttribute("NUM_RECORDS_TOTAL", document.getTotal());
            request.setAttribute(FULL_LIST, arts);
            page(pagenum, request);
        }
        request.setAttribute("criteria", request.getParameter("criteria"));
        String url = "/swbadmin/jsp/rnc/exhibitions/admExRows.jsp";
        request.setAttribute("mode", "card-columns");
        RequestDispatcher rd = request.getRequestDispatcher(url);
        try {
            request.setAttribute("paramRequest", paramRequest);
            rd.include(request, response);
        }catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public void doAdminSort(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, java.io.IOException {
        List<Entry> publicationList = new ArrayList<>();
        try {
            if (null != request.getParameter("criteria") && !request.getParameter("criteria").isEmpty()) {
                Document document = getReference(request, paramRequest.getWebPage().getWebSite());
                if (null != document) {
                    List<ArtWork> arts = getArts(request, document);
                    publicationList = document.getRecords();
                    request.setAttribute("NUM_RECORDS_TOTAL", document.getTotal());
                    request.setAttribute(FULL_LIST, arts);
                }
                request.setAttribute("f", request.getParameter("sort"));
                request.setAttribute("criteria", request.getParameter("criteria"));
                init(request);
            }
            request.setAttribute("references", publicationList);
            request.setAttribute("paramRequest", paramRequest);
            String url = "/swbadmin/jsp/rnc/exhibitions/admExRows.jsp";
            request.setAttribute("mode", "card-columns");
            RequestDispatcher rd = request.getRequestDispatcher(url);
            rd.include(request, response);
        }catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    private void configPage(HttpServletRequest request) {
        List<String> elements = new ArrayList<>();
        if (null != request.getParameterValues("favarts") && null != request.getSession().getAttribute("arts")) {
            String [] elms = request.getParameterValues("favarts");
            List<ArtWork> prev = (List<ArtWork>)request.getSession().getAttribute("arts");
            for (int i=0; i<elms.length; i++) {
                elements.add(elms[i]);
            }
            if (null != prev && !elements.isEmpty()) {
                for (ArtWork art : prev) {
                    if (elements.contains(art.getEntry().getId()))
                        art.setFavorite(Boolean.TRUE);
                }
            }
        }
    }
    
    private String getChekedEl(HttpServletRequest request, String attr) {
        StringBuilder elements = new StringBuilder();
        String [] elms = request.getParameterValues(attr);
        for (int i=0; i<elms.length; i++) {
            elements.append(",").append(elms[i]);
        }
        if (elements.length() > 1)
            return elements.substring(1, elements.length());
        return elements.toString();
    }
    
    private List<ArtWork> getArts(HttpServletRequest request, Document document) {
        List<ArtWork> prev = new ArrayList<>();
        List<ArtWork> arts = new ArrayList<>();
        if (null != request.getSession().getAttribute("arts"))
            prev = (List<ArtWork>)request.getSession().getAttribute("arts");
        if (null != document.getRecords()) {
            List<String> favs = getElements("favorites");
            List<String> hds = getElements("hiddenarts");
            for (Entry entry : document.getRecords()) {
                ArtWork art = new ArtWork(entry);
                art.setHidden(hds.contains(entry.getId()));
                art.setFavorite(favs.contains(entry.getId()));
                arts.add(art);
                if (!prev.contains(art))
                    prev.add(art);
            }
        }
        request.getSession().setAttribute("arts", prev);
        return arts;
    }
    
    private List<String> getElements(String attr) {
        List<String> elements = new ArrayList<>();
        if (null != getResourceBase().getAttribute(attr) && !getResourceBase().getAttribute(attr).isEmpty()) {
            if (getResourceBase().getAttribute(attr).lastIndexOf(",") == -1) {
                elements.add(getResourceBase().getAttribute(attr));                
            } else {
                String[] el = getResourceBase().getAttribute(attr).split(",");
                for (int i = 0; i < el.length; i++) {
                    if (!elements.contains(el[i])) {
                        elements.add(el[i]);
                    }                    
                }
            }
        }
        return elements;
    }
    
    private void init(HttpServletRequest request) {
        int pagenum = 0;
        String p = request.getParameter("p");
        if (null != p) pagenum = Integer.parseInt(p);
        if (pagenum<=0) pagenum = 1;
        request.setAttribute("NUM_PAGE_LIST", pagenum);
        request.setAttribute("PAGE_NUM_ROW", PAGE_NUM_ROW);
        page(pagenum, request);
    }
    
    private void page(int pagenum, HttpServletRequest request) {
        List<?> rows = (List<?>)request.getAttribute(FULL_LIST);
        Integer total = (Integer)request.getAttribute("NUM_RECORDS_TOTAL");
        if (null==total) total = 0;
        if (null==rows) rows = new ArrayList();
        try {
            Integer totalPages = total/PAGE_NUM_ROW;
            if (total%PAGE_NUM_ROW != 0)
                totalPages ++;
            request.setAttribute("TOTAL_PAGES", totalPages);
            Integer currentLeap = (pagenum-1)/PAGE_JUMP_SIZE;
            request.setAttribute("NUM_PAGE_JUMP", currentLeap);
            request.setAttribute("PAGE_JUMP_SIZE", PAGE_JUMP_SIZE);
            request.setAttribute(PAGE_LIST, rows);
            request.setAttribute("NUM_RECORDS_VISIBLE", rows.size());
            
            int last = 0;
            int first = 0;
            first = (pagenum-1)*PAGE_NUM_ROW+1;
            last = first+PAGE_NUM_ROW-1;
            if (last>total) last = total;
            request.setAttribute("FIRST_RECORD", first);
            request.setAttribute("LAST_RECORD", last);
            
        }catch(Exception e) {
            LOG.info(e.getMessage());
        }
    }
    
    private Document getReference(HttpServletRequest request, WebSite site) {
        Document document = null;
        String words = request.getParameter("criteria");
        String base = site.getModelProperty("search_endPoint");
        if (null == base || base.isEmpty()) {
            base = SWBPlatform.getEnv("rnc/endpointURL",getResourceBase().getAttribute("endpointURL","http://localhost:8080")).trim() + "/api/v1/search?q=";
        }
        String uri = base + getParamSearch(words);
        uri += getRange(request);
        if (null != request.getParameter("sort")) {
            String sorted = request.getParameter("sort") + getResourceBase().getAttribute("order");
            if (sorted.equalsIgnoreCase("datedes")) uri += "&sort=-datecreated.value";
            if (sorted.equalsIgnoreCase("dateasc")) uri += "&sort=datecreated.value";
            if (sorted.equalsIgnoreCase("statdes")) uri += "&sort=-resourcestats.views";
            if (sorted.equalsIgnoreCase("statasc")) uri += "&sort=resourcestats.views";
        }
	ListBICRequest req = new ListBICRequest(uri);
        try {
            document = req.makeRequest();
        }catch (Exception se) {
            LOG.log(Level.SEVERE, se.getMessage());
        }
        return document;
    }
    
    private Document getReference(HttpServletRequest request) {
        Document document = null;
        String words = getResourceBase().getAttribute("criteria");
    	String uri = SWBPlatform.getEnv("rnc/endpointURL",getResourceBase().getAttribute("endpointURL","http://localhost:8080")).trim() + "/api/v1/search?q=";
    	uri += getParamSearch(words);
        if (null != getResourceBase().getAttribute("sort") && null != getResourceBase().getAttribute("order")) {
            String sorted = getResourceBase().getAttribute("sort") + getResourceBase().getAttribute("order");
            System.out.println("sorted: " + sorted);
            if (sorted.equalsIgnoreCase("datedes")) uri += "&sort=-datecreated.value";
            if (sorted.equalsIgnoreCase("dateasc")) uri += "&sort=datecreated.value";
            if (sorted.equalsIgnoreCase("statdes")) uri += "&sort=-resourcestats.views";
            if (sorted.equalsIgnoreCase("statasc")) uri += "&sort=resourcestats.views";
        }
        uri += getRange(request);
	ListBICRequest req = new ListBICRequest(uri);
        try {
            document = req.makeRequest();
        }catch (Exception se) {
            LOG.log(Level.SEVERE, se.getMessage());
        }
        return document;
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
}