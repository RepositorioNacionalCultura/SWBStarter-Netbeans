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
import org.semanticwb.portal.api.SWBResourceURL;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceModes;
import org.semanticwb.portal.api.SWBResourceException;

import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

import org.semanticwb.SWBException;
import mx.gob.cultura.portal.utils.Utils;
import mx.gob.cultura.portal.response.Aggregation;
import mx.gob.cultura.portal.response.CountName;
import mx.gob.cultura.portal.response.DateRange;
import mx.gob.cultura.portal.response.DigitalObject;
import mx.gob.cultura.portal.response.Document;
import mx.gob.cultura.portal.response.Entry;
import mx.gob.cultura.portal.request.ListBICRequest;

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
                setType(document.getRecords(),  paramRequest.getWebPage().getWebSite());
                request.setAttribute("aggs", getAggregation(document.getAggs()));
                //request.setAttribute("creators", getCreators(document.getRecords()));
                request.setAttribute(FULL_LIST, document.getRecords());
                request.setAttribute("NUM_RECORDS_TOTAL", document.getTotal());
                cassette(request, document.getTotal(), getPage(request));
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
                    cassette(request, document.getTotal(), 1);
                    setType(document.getRecords(),  paramRequest.getWebPage().getWebSite());
                    request.setAttribute(FULL_LIST, document.getRecords());
                    request.setAttribute("NUM_RECORDS_TOTAL", document.getTotal());
                }
                request.setAttribute("f", request.getParameter("sort"));
                request.setAttribute("word", request.getParameter("word"));
                init(request, response, paramRequest);
            }
            request.setAttribute("references", publicationList);
            request.setAttribute("paramRequest", paramRequest);
        }catch (IOException | SWBResourceException se) {
            LOG.error(se);
        }
        super.doSort(request, response, paramRequest);
    }

    @Override
    public void doPage(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws java.io.IOException {
        int pagenum = 0;
        Document document = null;
        String p = request.getParameter("p");
        List<Entry> publicationList = null;
        if (null != p)
            pagenum = Integer.parseInt(p);
        if (pagenum<=0) pagenum = 1;
        request.setAttribute(NUM_PAGE_LIST, pagenum);
        request.setAttribute("PAGE_NUM_ROW", PAGE_NUM_ROW);
        document = getReference(request, paramRequest.getWebPage().getWebSite());
        if (null != document) {
            publicationList = document.getRecords();
            setType(document.getRecords(),  paramRequest.getWebPage().getWebSite());
            request.setAttribute(FULL_LIST, publicationList);
            request.setAttribute("NUM_RECORDS_TOTAL", document.getTotal());
            page(pagenum, request);
            cassette(request, document.getTotal(), pagenum);
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

    private void cassette(HttpServletRequest request, int total, int pagenum) {
        int last = 0;
        int first = 0;
        first = (pagenum - 1) * PAGE_NUM_ROW + 1;
        last = first + PAGE_NUM_ROW - 1;
        if (last > total) last = total;
        request.setAttribute("LAST_RECORD", last);
        request.setAttribute("FIRST_RECORD", first);
        request.setAttribute("word", request.getParameter("word"));
    }
    
    private Document getReference(HttpServletRequest request, WebSite site) {
        Document document = null;
        String words = request.getParameter("word");
        String baseUri = site.getModelProperty("search_endPoint");
        if (null == baseUri || baseUri.isEmpty()) baseUri = SWBPlatform.getEnv("rnc/endpointURL", getResourceBase().getAttribute("endpointURL","http://localhost:8080")).trim();
        String uri = baseUri + "/api/v1/search?q=";
        try {
            uri += URLEncoder.encode(getParamSearch(words), StandardCharsets.UTF_8.name());
            uri += getFilters(request);
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
    
    private String getFilters(HttpServletRequest request) throws UnsupportedEncodingException {
        StringBuilder filters = new StringBuilder();
        filters.append(getFilter(request, "resourcetype"));
        filters.append(getFilter(request, "mediatype"));
        filters.append(getFilterDate(request, "datecreated"));
        filters.append(getFilter(request, "rights"));
        filters.append(getFilter(request, "lang"));
        filters.append(getFilter(request, "holder"));
        if (filters.length() > 0) {
            filters.deleteCharAt(0);
            filters.insert(0, "&filter=");
        }
        return filters.toString();
    }

    private String getFilter(HttpServletRequest request, String att) throws UnsupportedEncodingException {
        StringBuilder filter = new StringBuilder();
        if (null != request.getParameter(att) && !request.getParameter(att).isEmpty()) {
            String [] filters = request.getParameter(att).split("::");
            for (int i=0; i<filters.length; i++) {
                if (att.equalsIgnoreCase("mediatype")) filters[i] = this.getMediaType(filters[i]);
                filter.append(",").append(att).append(":").append(URLEncoder.encode(filters[i], StandardCharsets.UTF_8.name()));
            }
        }else return "";
        return filter.toString();
    }
    
    private String getFilterDate(HttpServletRequest request, String att) {
        StringBuilder filter = new StringBuilder();
        if (null != request.getParameter(att) && !request.getParameter(att).isEmpty()) {
            String [] filters = request.getParameter(att).split(",");
            if (filters.length == 2)
                filter.append(",").append("datestart:").append(filters[0]).append(",dateend:").append(filters[1]);
        }
        return filter.toString();
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
                    word = word.replace(","," ");
                    capitalize.append(word);
                    //break;
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
            aggregation.setRights(new ArrayList<>());
            aggregation.setHolders(new ArrayList<>());
            aggregation.setLanguages(new ArrayList<>());
            aggregation.setMediastype(new ArrayList<>());
            aggregation.setResourcetypes(new ArrayList<>());
            for (Aggregation a : aggs) {
                if (null !=  a.getDates()) aggregation.getDates().addAll(a.getDates());
                if (null !=  a.getRights()) aggregation.getRights().addAll(a.getRights());
                if (null !=  a.getHolders()) aggregation.getHolders().addAll(a.getHolders());
                if (null !=  a.getLanguages()) aggregation.getLanguages().addAll(a.getLanguages());
                if (null !=  a.getResourcetypes()) aggregation.getResourcetypes().addAll(a.getResourcetypes());
                if (null !=  a.getMediastype()) aggregation.getMediastype().addAll(getTypes(a.getMediastype()));
            }
            for (CountName date : aggregation.getDates()) {
                cal.setTime(Utils.convert(date.getName(), "yyyy-MM-dd'T'HH:mm:ss"));
                if (interval.getUpperLimit() < cal.get(Calendar.YEAR)) interval.setUpperLimit(cal.get(Calendar.YEAR));
                if (interval.getLowerLimit() > cal.get(Calendar.YEAR)) interval.setLowerLimit(cal.get(Calendar.YEAR));
            }
        }
        return aggregation;
    }
    
    private List<CountName> getTypes(List<CountName> media) {
        List<CountName> types = new ArrayList<>();
        CountName pdf = new CountName("PDF", 0);
        CountName zip = new CountName("ZIP", 0);
        CountName three = new CountName("3D", 0);
        CountName eBook = new CountName("EPUB", 0);
        CountName image = new CountName("Imagen", 0);
        CountName audio = new CountName("Audio", 0);
        CountName video = new CountName("Video", 0);
        for (CountName c : media) {
            if (c.getName().startsWith("image")) image.setCount(image.getCount() + c.getCount());
            if (c.getName().startsWith("audio")) audio.setCount(audio.getCount() + c.getCount());
            if (c.getName().startsWith("video") || c.getName().equalsIgnoreCase("application/octet-stream")) video.setCount(video.getCount() + c.getCount());
            if (c.getName().equalsIgnoreCase("application/pdf")) pdf.setCount(pdf.getCount() + c.getCount());
            if (c.getName().startsWith("model/x3d")) three.setCount(three.getCount() + c.getCount());
            if (c.getName().equalsIgnoreCase("application/zip")) zip.setCount(zip.getCount() + c.getCount());
            if (c.getName().equalsIgnoreCase("application/epub+zip")) eBook.setCount(eBook.getCount() + c.getCount());
        }
        if (image.getCount() > 0) types.add(image);
        if (audio.getCount() > 0) types.add(audio);
        if (video.getCount() > 0) types.add(video);
        if (pdf.getCount() > 0) types.add(pdf);
        if (eBook.getCount() > 0) types.add(eBook);
        if (three.getCount() > 0) types.add(three);
        if (zip.getCount() > 0) types.add(zip);
        return types;
    }
    
    private String getMediaType(String type) {
        String mediaType = null;
        switch(type) {
            case "Imagen" : mediaType = "image/jpeg"; break;
            case "Audio" : mediaType = "audio/x-wav"; break;
            case "Video" : mediaType = "application/octet-stream"; break;
            case "PDF" : mediaType = "application/pdf"; break;
            case "EPUB" : mediaType = "application/epub+zip"; break;
            case "3D" : mediaType = "model/x3d+binary"; break;
            case "ZIP" : mediaType = "application/zip"; break;
        }
        return mediaType;
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
    
    public static void setThumbnail(Entry e, WebSite site, int position) {
        if (null != e) {
            List<DigitalObject> list = e.getDigitalObject();
            if (null != list && !list.isEmpty()) {
                if (position < list.size()) {
                    DigitalObject dObj = list.get(position);
                    if (null != dObj && null != dObj.getMediatype() && null != dObj.getMediatype().getMime()) {
                        String type = dObj.getMediatype().getMime();
                        e.setType(type);
                        if (!existImg(e.getResourcethumbnail())) {
                            if (type.equalsIgnoreCase("application/octet-stream")) {
                                if (dObj.getUrl().endsWith(".zip"))
                                    e.setResourcethumbnail("/work/models/" + site.getId() + "/img/no-zip.png");
                                else if (dObj.getUrl().endsWith(".avi"))
                                    e.setResourcethumbnail("/work/models/" + site.getId() + "/img/no-video.png");
                            }else if (!type.isEmpty() && type.startsWith("video")) 
                                e.setResourcethumbnail("/work/models/" + site.getId() + "/img/no-video.png");
                            else if (type.equalsIgnoreCase("application/pdf"))
                                e.setResourcethumbnail("/work/models/" + site.getId() + "/img/no-pdf.png");
                            else if (type.equalsIgnoreCase("application/zip"))
                                e.setResourcethumbnail("/work/models/" + site.getId() + "/img/no-zip.png");
                            else if (!type.isEmpty() && type.startsWith("audio"))
                                e.setResourcethumbnail("/work/models/" + site.getId() + "/img/no-audio.png");
                            else if (type.equalsIgnoreCase("text/richtext") || (!type.isEmpty() && type.startsWith("application/vnd")))
                                e.setResourcethumbnail("/work/models/" + site.getId() + "/img/no-multimedia.png");
                            else if (type.equalsIgnoreCase("image/jpeg"))
                                e.setResourcethumbnail(dObj.getUrl());
                        }
                    }
                }
            }
        }
    }
    
    private void setType(List<Entry> references, WebSite site) {
        if (null != references && !references.isEmpty()) {
            for (Entry e : references) {
                setThumbnail(e, site, 0);
            }
        }
    }
    
    private static boolean existImg(String urlImg) {
        if (null == urlImg || urlImg.isEmpty() || !urlImg.startsWith("http")) return false;
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(urlImg).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }catch (IOException e) {
            return false;
        }
    }
}