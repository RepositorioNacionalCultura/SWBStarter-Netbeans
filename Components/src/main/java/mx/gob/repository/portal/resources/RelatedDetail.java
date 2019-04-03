/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.repository.portal.resources;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.net.URLEncoder;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.gob.cultura.portal.request.ListBICRequest;

import mx.gob.cultura.portal.response.Entry;
import mx.gob.cultura.portal.response.Title;

import org.semanticwb.SWBPlatform;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;

import org.semanticwb.Logger;
import org.semanticwb.SWBUtils;
import mx.gob.cultura.portal.utils.Utils;
import java.nio.charset.StandardCharsets;
import static mx.gob.cultura.portal.utils.Constants.NUM_REL;
import static mx.gob.cultura.portal.utils.Constants.IDENTIFIER;
import static mx.gob.repository.portal.resources.SearchCulturalProperty.getMapper;


/**
 *
 * @author sergio.tellez
 */
public class RelatedDetail extends GenericResource {
    
    private static final Logger LOG = SWBUtils.getLogger(RelatedDetail.class);
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        //String path = "/swbadmin/jsp/rnc/resources/related.jsp";
        String path = "/work/models/"+paramRequest.getWebPage().getWebSite().getId()+"/jsp/rnc/resources/related.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            StringBuilder base = new StringBuilder(endpoint(paramRequest));
            String version = null != paramRequest.getWebPage().getWebSite().getModelProperty("version_endPoint") ? paramRequest.getWebPage().getWebSite().getModelProperty("version_endPoint") : "v1";
            base.append("/api/").append(version).append("/search?");
            StringBuilder uri =  new StringBuilder(base);
            if (null != request.getParameter(IDENTIFIER)) uri.append("identifier=").append(request.getParameter(IDENTIFIER));
            Entry entry = ArtDetail.getEntry(request, uri.toString());
            request.setAttribute("related", getRelated(entry, base, paramRequest));
            rd.include(request, response);
        }catch (IOException | ServletException se) {
            LOG.info(se.getMessage());
        }
        
    }
    
    private List<Entry> getRelated(Entry entry, StringBuilder endpoint, SWBParamRequest paramRequest) {
        endpoint.append("q=");
        List<Entry> related = new ArrayList<>();
        if (null == entry) return new ArrayList<>();
        if (null != entry.getRecordtitle() && !entry.getRecordtitle().isEmpty())
            entry.getKeywords().add(0,Utils.getTitle(entry.getRecordtitle(), 0));
        for (String key : entry.getKeywords()) {
            StringBuilder search = new StringBuilder(endpoint.toString());
            if (null != key && !key.trim().isEmpty()) {
                try {
                    search.append(URLEncoder.encode(key.trim(), StandardCharsets.UTF_8.name()));
                    search.append("&from=0&size=").append(NUM_REL);
                } catch (UnsupportedEncodingException ex) {
                    LOG.info(ex.getMessage());
                }
                ListBICRequest req = new ListBICRequest(search.toString());
                try {
                    //document = req.makeRequest();
                    String json = req.doRequest();
                    Map mapper = getMapper(json);
                    List<Object> objs = (ArrayList)mapper.get("records");
                    if (null != objs) {
                        for (Object record : objs) {
                            Map map = (Map)record;
                            Entry rec = new Entry();
                            //rec.setResourcethumbnail("https://mexicana.cultura.gob.mx" + (String)map.get("resourcethumbnail"));
                            rec.setResourcethumbnail((String)map.get("resourcethumbnail"));
                            String recordtitle = null != map.get("recordtitle") ? (String)map.get("recordtitle") : "Sin Título";
                            rec.setId((String)map.get("_id"));
                            rec.getRecordtitle().add(new Title(recordtitle));
                            rec.setHolder((ArrayList)map.get("holder"));
                            rec.setCreator((ArrayList)map.get("author"));
                            if (!rec.getId().equalsIgnoreCase(entry.getId()) && !related.contains(rec)) {
                                related.add(rec);
                                SearchCulturalProperty.setThumbnail(rec, paramRequest.getWebPage().getWebSite(), 0);
                            }
                            if (related.size() >= NUM_REL) break;
                        }
                    }
                } catch (Exception se) {
                    LOG.error(se);
                }
            }
        }
        return related;
    }
    
    private String endpoint(SWBParamRequest paramRequest) {
        String baseUri = paramRequest.getWebPage().getWebSite().getModelProperty("search_endPoint");
        if (null == baseUri || baseUri.isEmpty())
            baseUri = SWBPlatform.getEnv("rnc/endpointURL", getResourceBase().getAttribute("url", "http://localhost:8080")).trim();
        return baseUri;
    }
    
}