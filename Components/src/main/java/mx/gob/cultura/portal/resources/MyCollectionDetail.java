/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.gob.cultura.portal.persist.CollectionMgr;
import mx.gob.cultura.portal.request.GetBICRequest;
import static mx.gob.cultura.portal.resources.MyCollections.IDENTIFIER;
import mx.gob.cultura.portal.response.Collection;
import mx.gob.cultura.portal.response.Entry;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION;
import static mx.gob.cultura.portal.utils.Constants.PARAM_REQUEST;
import org.semanticwb.SWBPlatform;
import org.semanticwb.model.User;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;

/**
 *
 * @author juan.fernandez
 */
public class MyCollectionDetail extends GenericResource{

    CollectionMgr mgr = CollectionMgr.getInstance();
    private static final Logger LOG = Logger.getLogger(MyCollectionDetail.class.getName());
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        User user = paramRequest.getUser();
        Collection collection = null;
        List<Collection> list = new ArrayList<>();
        List<Entry> favorites = new ArrayList<>();
        String path = "/swbadmin/jsp/rnc/collections/elements.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            if (null != user && user.isSigned() && null != request.getParameter(IDENTIFIER) /**&& !collectionList.isEmpty()**/) {
                collection = mgr.findById(request.getParameter(IDENTIFIER));
            }
            if (null != collection && null != collection.getElements()) {
                list.add(collection);
                setAuthors(paramRequest, list);
                for (String _id : collection.getElements()) {
                    Entry entry = getEntry(paramRequest, _id);
                    if (null != entry) {
                        favorites.add(entry);
                        SearchCulturalProperty.setThumbnail(entry, paramRequest.getWebPage().getWebSite(), 0);
                    }
                }
            }
            request.setAttribute("myelements", favorites);
            request.setAttribute(COLLECTION, collection);
            request.setAttribute(PARAM_REQUEST, paramRequest);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    private void setAuthors(SWBParamRequest paramRequest, List<Collection> list) {
         if (null == list || list.isEmpty()) return;
         for (Collection c : list) {
             if (null != c && null != c.getUserid()) {
                  User user = paramRequest.getWebPage().getWebSite().getUserRepository().getUser(c.getUserid());
                  c.setUserName(null != user && null != user.getFullName()? user.getFullName() : "");
             }
         }
     }
     
    protected Entry getEntry(SWBParamRequest paramRequest,String _id) {
        String baseUri = paramRequest.getWebPage().getWebSite().getModelProperty("search_endPoint");
        if (null == baseUri || baseUri.isEmpty()) {
            baseUri = SWBPlatform.getEnv("rnc/endpointURL", getResourceBase().getAttribute("url", "http://localhost:8080")).trim();
        }
        String uri = baseUri + "/api/v1/search?identifier=";
        uri += _id;
        GetBICRequest req = new GetBICRequest(uri);
        return req.makeRequest();
    }
    
}
