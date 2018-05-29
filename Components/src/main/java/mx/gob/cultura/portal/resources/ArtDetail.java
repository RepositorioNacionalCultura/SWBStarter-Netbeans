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
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import mx.gob.cultura.portal.response.DigitalObject;

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
        }else if (MODE_VISOR.equals(mode)) {
            //MODE_VISOR
        }else
            super.processRequest(request, response, paramRequest);
    }
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws IOException {
        //Get baseURI from site properties first
        String baseUri = paramRequest.getWebPage().getWebSite().getModelProperty("search_endPoint");
        if (null == baseUri || baseUri.isEmpty())
            baseUri = SWBPlatform.getEnv("rnc/endpointURL", getResourceBase().getAttribute("url","http://localhost:8080")).trim();
        String uri = baseUri + "/api/v1/search?identifier=";
        String path = "/swbadmin/jsp/rnc/artdetail.jsp";
        try {
            if (null != request.getParameter(IDENTIFIER)) {
                uri += request.getParameter(IDENTIFIER);
                GetBICRequest req = new GetBICRequest(uri);
                Entry entry = req.makeRequest();
                if (null != entry) {
                    int position = Utils.toInt(request.getParameter(POSITION));
                    entry.setPosition(position);
                    path = getViewerPath(getMimeType(getDigitalObject(entry.getDigitalObject(), position)), SWBParamRequest.Mode_VIEW);
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
    
    public void doDigital(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws java.io.IOException {
        int iDigit = 0;
        String baseUri = paramRequest.getWebPage().getWebSite().getModelProperty("search_endPoint");
        if (null == baseUri || baseUri.isEmpty())
            baseUri = SWBPlatform.getEnv("rnc/endpointURL", getResourceBase().getAttribute("url", "http://localhost:8080")).trim();
        String uri = baseUri + "/api/v1/search?identifier=";
        String path = "/swbadmin/jsp/rnc/digitalobj.jsp";
        try {
            if (null != request.getParameter(IDENTIFIER)) {
                uri += request.getParameter(IDENTIFIER);
                if (null != request.getParameter(POSITION)) iDigit = Utils.toInt(request.getParameter(POSITION));
                GetBICRequest req = new GetBICRequest(uri);
                Entry entry = req.makeRequest();
                if (null != entry) {
                    entry.setPosition(iDigit);
                    int images = null != entry.getDigitalObject() ? entry.getDigitalObject().size() : 0;
                    path = getViewerPath(getMimeType(getDigitalObject(entry.getDigitalObject(), iDigit)), MODE_DIGITAL);
                    if (iDigit >= 0 && iDigit <= images) {
                        request.setAttribute("iDigit", iDigit);
                        request.setAttribute("digital", entry.getDigitalObject().get(iDigit));
                    }
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
    
    private DigitalObject getDigitalObject(List<DigitalObject> list, int position) {
        if (null == list || list.isEmpty() || position > list.size()) return null;
        if (position <=0) return list.get(0);
        return list.get(position);
    }
    
    private String getMimeType(DigitalObject digital) {
        if (null != digital && null != digital.getMediatype() && null != digital.getMediatype().getMime()) return digital.getMediatype().getMime();
        else return "";
    }
    
    private String getViewerPath(String mimeType, String mode) {
        String path = "/swbadmin/jsp/rnc/artdetail.jsp";
        if (null == mimeType || mimeType.isEmpty()) return path;
        if (mode.equalsIgnoreCase(MODE_DIGITAL)) {
            if (mimeType.equalsIgnoreCase("application/pdf"))
                path = "/swbadmin/jsp/rnc/viewer/pdfdigital.jsp";
            else if (mimeType.equalsIgnoreCase("application/octet-stream") || mimeType.equalsIgnoreCase("video/x-msvideo") || mimeType.equalsIgnoreCase("video/mpeg"))
                path = "/swbadmin/jsp/rnc/viewer/videodigital.jsp";
            else if (mimeType.equalsIgnoreCase("application/epub+zip"))
                path = "/swbadmin/jsp/rnc/viewer/epubdigital.jsp";
            else if (!mimeType.isEmpty() && mimeType.startsWith("audio"))
                path = "/swbadmin/jsp/rnc/viewer/audiodigital.jsp";
            else path = "/swbadmin/jsp/rnc/digitalobj.jsp";
        }else {
            if (mimeType.equalsIgnoreCase("application/pdf"))
                path = "/swbadmin/jsp/rnc/viewer/pdfdetail.jsp";
            else if (mimeType.equalsIgnoreCase("application/octet-stream") || mimeType.equalsIgnoreCase("video/x-msvideo") || mimeType.equalsIgnoreCase("video/mpeg"))
                path = "/swbadmin/jsp/rnc/viewer/videodetail.jsp";
            else if (mimeType.equalsIgnoreCase("application/epub+zip"))
                path = "/swbadmin/jsp/rnc/viewer/epubdetail.jsp";
            else if (!mimeType.isEmpty() && mimeType.startsWith("audio"))
                path = "/swbadmin/jsp/rnc/viewer/audiodetail.jsp";
        }
         return path;
    }
    
    /**private int hits(String baseUri, Entry entry) throws IOException {
        String uri = baseUri + "/api/v1/search/hits/" + entry.getId();
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.getOutputStream().close();
        return connection.getResponseCode();
    }**/
}
