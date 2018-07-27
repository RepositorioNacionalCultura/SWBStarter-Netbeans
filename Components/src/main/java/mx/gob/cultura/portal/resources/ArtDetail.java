/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.resources;

import org.semanticwb.Logger;
import org.semanticwb.SWBUtils;
import org.semanticwb.SWBPlatform;

import mx.gob.cultura.portal.utils.Utils;
import mx.gob.cultura.portal.response.Entry;
import mx.gob.cultura.portal.request.GetBICRequest;
import org.semanticwb.portal.api.GenericAdmResource;

import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import mx.gob.cultura.portal.request.ListBICRequest;
import mx.gob.cultura.portal.response.DigitalObject;
import mx.gob.cultura.portal.response.Document;
import static mx.gob.cultura.portal.utils.Constants.WORD;
import static mx.gob.cultura.portal.utils.Constants.FILTER;
import static mx.gob.cultura.portal.utils.Constants.NUM_ROW;
import static mx.gob.cultura.portal.utils.Constants.NUM_RECORD;

/**
 *
 * @author sergio.tellez
 */
public class ArtDetail extends GenericAdmResource {

    private static final String POSITION = "n";
    private static final String IDENTIFIER = "id";
    private static final String MODE_VISOR = "VISOR";
    private static final String MODE_DIGITAL = "DIGITAL";
    private static final Logger LOG = SWBUtils.getLogger(ArtDetail.class);

    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        String mode = paramRequest.getMode();
        if (MODE_DIGITAL.equals(mode)) {
            doDigital(request, response, paramRequest);
        } else if (MODE_VISOR.equals(mode)) {
            //MODE_VISOR
        } else {
            super.processRequest(request, response, paramRequest);
        }
    }

    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws IOException {
        String path = "/swbadmin/jsp/rnc/preview.jsp";
        String baseUri = getBaseUri(paramRequest);
        String uri = getParamUri(baseUri, request);
        try {
            if (null != uri) {
                Entry entry = getEntry(request, uri);
                if (null != entry) {
                    int position = null != request.getParameter(POSITION) ? Utils.toInt(request.getParameter(POSITION)) : 0;
                    entry.setPosition(position);
                    DigitalObject ob = getDigitalObject(entry.getDigitalObject(), position);
                    if (ob.getMediatype().getMime().startsWith("audio"))
                        request.setAttribute("inext", iNext(entry.getDigitalObject(), position, "audio"));
                    SearchCulturalProperty.setThumbnail(entry, paramRequest.getWebPage().getWebSite(), position);
                    if (null != request.getParameter(POSITION))
                        path = getViewerPath(ob, SWBParamRequest.Mode_VIEW);
                    incHits(request, entry, baseUri, uri);
                }
                request.setAttribute("entry", entry);
                request.setAttribute("collection", explore(entry, baseUri));
            }
            setParams(request, paramRequest);
            RequestDispatcher rd = request.getRequestDispatcher(path);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.error(se);
        }
    }
    
    private Entry getEntry(HttpServletRequest request, String uri) {
        Entry entry = null;
        Document document = null;
        if (null != request.getParameter(IDENTIFIER)) {
            GetBICRequest req = new GetBICRequest(uri);
            entry = req.makeRequest();
        }else {
            ListBICRequest list = new ListBICRequest(uri);
            document = list.makeRequest();
            if (null != document && !document.getRecords().isEmpty())
                entry = document.getRecords().get(0);
        }
        return entry;
    }
    
    private String getParamUri(String base, HttpServletRequest request) {
        StringBuilder uri = new StringBuilder(base);
        uri.append("/api/v1/search?");
        if (null != request.getParameter(IDENTIFIER)) uri.append("identifier=").append(request.getParameter(IDENTIFIER));
        else {
            if (null != request.getParameter(WORD)) uri.append("q=").append(request.getParameter(WORD));
            if (null != request.getParameter(NUM_RECORD)) uri.append("&from=").append(request.getParameter(NUM_RECORD)).append("&size=1");
        }
        return uri.toString();
    }
    
    private String getBaseUri(SWBParamRequest paramRequest) {
        //Get baseURI from site properties first
        String baseUri = paramRequest.getWebPage().getWebSite().getModelProperty("search_endPoint");
        if (null == baseUri || baseUri.isEmpty())
            baseUri = SWBPlatform.getEnv("rnc/endpointURL", getResourceBase().getAttribute("url", "http://localhost:8080")).trim();
        return baseUri;
    }
    
    private void setParams(HttpServletRequest request, SWBParamRequest paramRequest) throws IOException {
        request.setAttribute("paramRequest", paramRequest);
        request.setAttribute("back", back(request, paramRequest));
        if (null != request.getParameter(WORD)) request.setAttribute(WORD, request.getParameter(WORD));
        if (null != request.getParameter(FILTER)) request.setAttribute(FILTER, request.getParameter(FILTER));
        if (null != request.getParameter(NUM_RECORD)) request.setAttribute(NUM_RECORD, request.getParameter(NUM_RECORD));
    }

    public void doDigital(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws java.io.IOException {
        int iDigit = 0;
        String baseUri = paramRequest.getWebPage().getWebSite().getModelProperty("search_endPoint");
        if (null == baseUri || baseUri.isEmpty()) {
            baseUri = SWBPlatform.getEnv("rnc/endpointURL", getResourceBase().getAttribute("url", "http://localhost:8080")).trim();
        }
        String uri = baseUri + "/api/v1/search?identifier=";
        String path = "/swbadmin/jsp/rnc/digitalobj.jsp";
        try {
            if (null != request.getParameter(IDENTIFIER)) {
                uri += request.getParameter(IDENTIFIER);
                if (null != request.getParameter(POSITION)) {
                    iDigit = Utils.toInt(request.getParameter(POSITION));
                }
                GetBICRequest req = new GetBICRequest(uri);
                Entry entry = req.makeRequest();
                if (null != entry) {
                    entry.setPosition(iDigit);
                    DigitalObject ob = getDigitalObject(entry.getDigitalObject(), iDigit);
                    SearchCulturalProperty.setThumbnail(entry, paramRequest.getWebPage().getWebSite(), iDigit);
                    int images = null != entry.getDigitalObject() ? entry.getDigitalObject().size() : 0;
                    path = getViewerPath(ob, MODE_DIGITAL);
                    if (iDigit >= 0 && iDigit <= images) {
                        request.setAttribute("iDigit", iDigit);
                        request.setAttribute("digital", entry.getDigitalObject().get(iDigit));
                    }
                    if (ob.getMediatype().getMime().startsWith("audio")) {
                        request.setAttribute("iprev", iPrev(entry.getDigitalObject(), iDigit, "audio"));
                        request.setAttribute("inext", iNext(entry.getDigitalObject(), iDigit, "audio"));
                    }
                    incHits(request, entry, baseUri, uri);
                }
                request.setAttribute("entry", entry);
            }
            request.setAttribute("paramRequest", paramRequest);
            RequestDispatcher rd = request.getRequestDispatcher(path);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.error(se);
        }
    }

    private List<Entry> explore(Entry entry, String endPoint) {
        List<Entry> bookCase = new ArrayList<>();
        if (null == entry) {
            return bookCase;
        }
        List<String> collection = entry.getCollection();
        if (null != collection && !collection.isEmpty()) {
            for (String rack : collection) {
                bookCase.addAll(bookCase(endPoint, rack));
                if (!bookCase.isEmpty() && bookCase.size() > NUM_ROW) {
                    break;
                }
            }
        }
        if (bookCase.size() >= NUM_ROW) {
            return bookCase.subList(0, NUM_ROW);
        } else {
            return bookCase;
        }
    }

    private List<Entry> bookCase(String endPoint, String rack) {
        Document document = null;
        String uri = endPoint + "/api/v1/search?q=";
        try {
            uri += URLEncoder.encode(rack, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException uex) {
            LOG.error(uex);
        }
        ListBICRequest req = new ListBICRequest(uri);
        try {
            document = req.makeRequest();
        } catch (Exception se) {
            LOG.error(se);
        }
        return null != document ? document.getRecords() : new ArrayList<>();
    }

    private String back(HttpServletRequest request, SWBParamRequest paramRequest) throws java.io.IOException {
        int p = ((null != request.getParameter("leap") ? Utils.toInt(request.getParameter("leap")) : 0) / 8) + 1;
        String back = (p > 1) ? "javascript:location.replace('/" + paramRequest.getUser().getLanguage() + "/" + paramRequest.getWebPage().getWebSiteId() + "/resultados?word=" + request.getParameter("word") + "&p=" + p + "');" : "javascript:history.go(-1)";
        return back;
    }

    private DigitalObject getDigitalObject(List<DigitalObject> list, int position) {
        if (null == list || list.isEmpty() || position > list.size()) return null;
        if (position <= 0) return list.get(0);
        return list.get(position);
    }
    
    private Integer iNext (List<DigitalObject> list, int position, String type) {
        Integer inext = 0;
        for (int i = position++; i < list.size(); i++) {
            DigitalObject o = list.get(i);
            if (!o.getMediatype().getMime().startsWith(type)) {
                inext = i;
                break;
            } 
        }
        return inext;
    }
    
    private Integer iPrev(List<DigitalObject> list, int position, String type) {
        Integer iprev = 0;
        if (position > list.size()+1 || position < 1) return iprev;
        for (int i = position--; i > 0; i--) {
            DigitalObject o = list.get(i);
            if (!o.getMediatype().getMime().startsWith(type)) {
                iprev = i;
                break;
            }
        }
        return iprev;
    }

    private String getMimeType(DigitalObject digital) {
        if (null != digital && null != digital.getMediatype() && null != digital.getMediatype().getMime()) {
            return digital.getMediatype().getMime();
        } else {
            return "";
        }
    }

    private String getViewerPath(DigitalObject digital, String mode) {
        String path = "/swbadmin/jsp/rnc/artdetail.jsp";
        if (null == digital) return path;
        String mimeType = getMimeType(digital);
        String url = null != digital.getUrl() ? digital.getUrl() : "";
        if (null == mimeType || mimeType.isEmpty()) {
            return path;
        }
        if (mode.equalsIgnoreCase(MODE_DIGITAL)) {
            if (mimeType.equalsIgnoreCase("application/pdf")) {
                path = "/swbadmin/jsp/rnc/viewer/pdfdigital.jsp";
            } else if (mimeType.equalsIgnoreCase("application/octet-stream")) {
                if (url.endsWith(".zip")) {
                    path = "/swbadmin/jsp/rnc/digitalobj.jsp";
                }else if (url.endsWith(".avi") || url.endsWith(".mp4") || url.endsWith(".MP4")) {
                    path = "/swbadmin/jsp/rnc/viewer/videodigital.jsp";
                }else if (url.endsWith(".epub")) {
                    path = "/swbadmin/jsp/rnc/viewer/epubdigital.jsp";
                }
            } else if (mimeType.equalsIgnoreCase("video/mp4") || mimeType.equalsIgnoreCase("video/quicktime") || mimeType.equalsIgnoreCase("video/x-msvideo") || mimeType.equalsIgnoreCase("video/mpeg")) {
                path = "/swbadmin/jsp/rnc/viewer/videodigital.jsp";
            } else if (mimeType.equalsIgnoreCase("application/epub+zip")) {
                path = "/swbadmin/jsp/rnc/viewer/epubdigital.jsp";
            } else if (!mimeType.isEmpty() && mimeType.startsWith("audio")) {
                path = "/swbadmin/jsp/rnc/viewer/audiodigital.jsp";
            } else {
                path = "/swbadmin/jsp/rnc/digitalobj.jsp";
            }
        } else {
            if (mimeType.equalsIgnoreCase("application/pdf")) {
                path = "/swbadmin/jsp/rnc/viewer/pdfdetail.jsp";
            } else if (mimeType.equalsIgnoreCase("application/octet-stream")) {
                if (url.endsWith(".zip")) {
                    path = "/swbadmin/jsp/rnc/artdetail.jsp";
                } else if (url.endsWith(".avi") || url.endsWith(".mp4") || url.endsWith(".MP4")) {
                    path = "/swbadmin/jsp/rnc/viewer/videodetail.jsp";
                }else if (url.endsWith(".epub")) {
                    path = "/swbadmin/jsp/rnc/viewer/epubdetail.jsp";
                }
            } else if (mimeType.equalsIgnoreCase("video/mp4") || mimeType.equalsIgnoreCase("video/quicktime") || mimeType.equalsIgnoreCase("video/x-msvideo") || mimeType.equalsIgnoreCase("video/mpeg")) {
                path = "/swbadmin/jsp/rnc/viewer/videodetail.jsp";
            } else if (mimeType.equalsIgnoreCase("application/epub+zip")) {
                path = "/swbadmin/jsp/rnc/viewer/epubdetail.jsp";
            } else if (!mimeType.isEmpty() && mimeType.startsWith("audio")) {
                path = "/swbadmin/jsp/rnc/viewer/audiodetail.jsp";
            }
        }
        return path;
    }

    private void incHits(HttpServletRequest request, Entry entry, String baseUri, String uri) {
        try {
            if (null != entry) {
                entry.setPosition(Utils.toInt(request.getParameter(POSITION)));
                uri = baseUri
                        + "/api/v1/search/hits/"
                        + entry.getId();
                URL url = new URL(uri);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.getOutputStream().close();
                connection.getResponseCode();
            }
        } catch (Exception se) {
            LOG.info(se.getMessage());
        }
    }
}
