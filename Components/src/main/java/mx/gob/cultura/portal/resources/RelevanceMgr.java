/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.resources;

import java.util.List;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.gob.cultura.portal.persist.CollectionMgr;

import mx.gob.cultura.portal.response.Entry;
import mx.gob.cultura.portal.response.Collection;
import mx.gob.cultura.portal.request.GetBICRequest;

import org.semanticwb.SWBPlatform;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.GenericAdmResource;
import org.semanticwb.portal.api.SWBResourceException;
import static mx.gob.cultura.portal.utils.Constants.PARAM_REQUEST;

import org.semanticwb.model.WebSite;

/**
 *
 * @author sergio.tellez
 */
public class RelevanceMgr extends GenericAdmResource {
    
    private static final Logger LOG = Logger.getLogger(RelevanceMgr.class.getName());
    
    CollectionMgr mgr = CollectionMgr.getInstance();
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        String path = "/swbadmin/jsp/rnc/collections/homecollection.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            List<Entry> collectionList = collectionById(this.getResourceBase().getAttribute("collectionID", ""), paramRequest.getWebPage().getWebSite());
            request.setAttribute(PARAM_REQUEST, paramRequest);
            request.setAttribute("relevants", collectionList);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    private List<Entry> collectionById(String id, WebSite site) throws SWBResourceException, IOException {
        @SuppressWarnings("UnusedAssignment")
        Collection collection = null;
        List<Entry> favorites = new ArrayList<>();
        if (id.isEmpty()) return favorites;
        String base = site.getModelProperty("search_endPoint");
        if (null == base || base.isEmpty())
            base = SWBPlatform.getEnv("rnc/endpointURL", getResourceBase().getAttribute("endpointURL","http://localhost:8080")).trim();
        base += "/api/v1/search?identifier=";
        try {
            collection = mgr.findById(id);
            if (null != collection && null != collection.getElements()) {
                for (String _id : collection.getElements()) {
                    String uri = base + _id; 
                    Entry entry = getEntry(uri);
                    if (null != entry) {
                        favorites.add(entry);
                        SearchCulturalProperty.setThumbnail(entry, site, 0);
                    }
                }
            }
        } catch (Exception se) {
            LOG.info(se.getMessage());
        }
        return favorites;
    }
    
    private Entry getEntry(String uri) {
        GetBICRequest req = new GetBICRequest(uri);
        return req.makeRequest();
    }
}
