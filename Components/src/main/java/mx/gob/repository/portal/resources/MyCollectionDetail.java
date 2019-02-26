/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.repository.portal.resources;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.gob.cultura.portal.persist.CollectionMgr;
import mx.gob.cultura.portal.persist.UserCollectionMgr;
import mx.gob.cultura.portal.request.GetBICRequest;
import static mx.gob.cultura.portal.resources.MyCollections.ACTION_ADD_CLF;
import static mx.gob.cultura.portal.resources.MyCollections.ACTION_DEL_FAV;
import static mx.gob.cultura.portal.resources.MyCollections.COLLECTION_RENDER;
import static mx.gob.cultura.portal.resources.MyCollections.ENTRY;

import mx.gob.cultura.portal.response.Collection;
import mx.gob.cultura.portal.response.Entry;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION;
import static mx.gob.cultura.portal.utils.Constants.PARAM_REQUEST;
import org.semanticwb.SWBPlatform;
import org.semanticwb.model.User;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;
import static mx.gob.cultura.portal.resources.MyCollections.IDENTIFIER;
import static mx.gob.cultura.portal.resources.MyCollections.MODE_EDIT;
import static mx.gob.cultura.portal.resources.MyCollections.MODE_RES_ADD;
import mx.gob.cultura.portal.response.UserCollection;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION_PUBLIC;
import mx.gob.cultura.portal.utils.Utils;
import org.semanticwb.portal.api.SWBActionResponse;
import org.semanticwb.portal.api.SWBResourceURL;
/**
 *
 * @author juan.fernandez
 */
public class MyCollectionDetail extends GenericResource{

    CollectionMgr mgr = CollectionMgr.getInstance();
    UserCollectionMgr umr = UserCollectionMgr.getInstance();
    private static final Logger LOG = Logger.getLogger(MyCollectionDetail.class.getName());
    
    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        String mode = paramRequest.getMode();
        if (MODE_EDIT.equals(mode)) {
            doEdit(request, response, paramRequest);
        }else if (MODE_RES_ADD.equals(mode)) {
            redirectJsonResponse(request, response);
        }else
            super.processRequest(request, response, paramRequest);
    }
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        //User user = paramRequest.getUser();
        Collection collection = null;
        List<Collection> list = new ArrayList<>();
        List<Entry> favorites = new ArrayList<>();
        //String path = "/swbadmin/jsp/rnc/collections/elements.jsp";
        String path = "/work/models/"+paramRequest.getWebPage().getWebSite().getId()+"/jsp/rnc/collections/elements.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            if (null != request.getParameter(IDENTIFIER) ) {  /** null != user && user.isSigned() &&  && !collectionList.isEmpty()**/
                collection = mgr.findById(request.getParameter(IDENTIFIER));
            }
            if (null != collection && null != collection.getElements()) {
                list.add(collection);
                setAuthors(paramRequest, list);
                collection.setFavorites(umr.countByCollection(collection.getId()).intValue());
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
    
    @Override
    public void doEdit(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        //String path = "/swbadmin/jsp/rnc/collections/collection.jsp";
        String path = "/work/models/"+paramRequest.getWebPage().getWebSite().getId()+"/jsp/rnc/collections/collection.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        if (null != request.getParameter(IDENTIFIER)) {
            Collection c = mgr.findById(request.getParameter(IDENTIFIER));
            if (null != c) request.setAttribute(COLLECTION, c);
        }
        try {
            request.setAttribute(PARAM_REQUEST, paramRequest);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    @Override
    public void processAction(HttpServletRequest request, SWBActionResponse response) throws SWBResourceException, IOException {
        User user = response.getUser();
        request.setCharacterEncoding("UTF-8");
        List<Collection> collectionList = new ArrayList<>();
        String siteid = response.getWebPage().getWebSiteId();
        if (SWBResourceURL.Action_EDIT.equals(response.getAction())) {
            Collection collection = setCollection(request, false);
            if (null != user && user.isSigned() && !collection.isEmpty() && null != request.getParameter(IDENTIFIER)) {
                collectionList = collectionList(siteid, user);
                if (!mgr.exist(collection.getTitle(), request.getParameter(IDENTIFIER))) {
                    for (Collection c : collectionList) {
                        if (c.getId().equals(request.getParameter(IDENTIFIER))) {
                            c.setUserid(user.getId());
                            c.setTitle(collection.getTitle());
                            c.setDescription(collection.getDescription());
                            c.setUserName(getAuthor(user.getId(), null, response));
                            c.setStatus(Utils.getStatus(request.getParameter("status"), c.getStatus()));
                            Gson gson = new Gson();
                            response.setRenderParameter(COLLECTION_RENDER, gson.toJson(c));
                            mgr.updateCollection(c);
                            break;
                        }
                    }
                }else {
                    Gson gson = new Gson();
                    response.setRenderParameter(COLLECTION_RENDER, gson.toJson(collection));
                }
                response.setMode(MODE_RES_ADD);
                response.setCallMethod(SWBParamRequest.Call_DIRECT);
            }
        }else if (ACTION_DEL_FAV.equals(response.getAction())) {
            if (null != request.getParameter(IDENTIFIER) && null != request.getParameter(ENTRY)) {
                Collection c = mgr.findById(request.getParameter(IDENTIFIER));
                if (null != c && !c.getElements().isEmpty() && !request.getParameter(ENTRY).trim().isEmpty() && c.getElements().contains(request.getParameter(ENTRY))) {
                    c.getElements().remove(request.getParameter(ENTRY));
                    mgr.updateCollection(c);
                }
                Gson gson = new Gson();
                response.setRenderParameter(COLLECTION_RENDER, gson.toJson(c));
                response.setMode(MODE_RES_ADD);
                response.setCallMethod(SWBParamRequest.Call_DIRECT);
            }
        }else if (ACTION_ADD_CLF.equals(response.getAction()) && null != request.getParameter(IDENTIFIER) && !request.getParameter(IDENTIFIER).trim().isEmpty()) {
            Gson gson = new Gson();
            Collection c = mgr.findById(request.getParameter(IDENTIFIER));
            if (null != user && null != user.getId()) {
                if (!umr.exist(user.getId(), request.getParameter(IDENTIFIER))) {
                    UserCollection uc = new UserCollection(user.getId(), request.getParameter(IDENTIFIER));
                    String _id = umr.insertUserCollection(uc);
                    if (null != _id) {
                        c.setId(_id);
                        c.setFavorites(umr.countByCollection(request.getParameter(IDENTIFIER)).intValue());
                    }
                }else {
                    UserCollection uc = umr.findUserCollection(user.getId(), request.getParameter(IDENTIFIER));
                    if (null != uc) {
                        Long result = umr.deleteUserCollection(uc.getId());
                        if (result > 0) c.setFavorites(umr.countByCollection(request.getParameter(IDENTIFIER)).intValue());
                    }
                }
            }else {
                c.setId(null);
                c.setUserid(null);
            }
            response.setRenderParameter(COLLECTION_RENDER, gson.toJson(c));
            response.setMode(MODE_RES_ADD);
            response.setCallMethod(SWBParamRequest.Call_DIRECT);
        }else
            super.processAction(request, response);
    }
    
    public void redirectJsonResponse(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
        String json = request.getParameter(COLLECTION_RENDER);
        response.setContentType("application/json");
	response.setHeader("Cache-Control", "no-cache");
	PrintWriter pw = response.getWriter();
	pw.write(json);
	pw.flush();
	pw.close();
	response.flushBuffer();
    }
    
    private Collection setCollection(HttpServletRequest request, boolean prevst) {
        Collection collection = new Collection(request.getParameter("title").trim(), Utils.getStatus(request.getParameter("status"), prevst), request.getParameter("description").trim());
        return collection;
    }
    
    private List<Collection> collectionList(String siteid, User user) {
        List<Collection> collection = new ArrayList<>();
        if (null != user && user.isSigned())
            collection = mgr.collections(user.getId());
        else {
            collection = mgr.collectionsByStatus(COLLECTION_PUBLIC);
        }
        return collection;
    }
    
    private String getAuthor(String userid, SWBParamRequest paramRequest, SWBActionResponse response) {
        User user = null;
        if (null != paramRequest)
            user = paramRequest.getWebPage().getWebSite().getUserRepository().getUser(userid);
        else if (null != response)
            user = response.getWebPage().getWebSite().getUserRepository().getUser(userid);
        return null != user && null != user.getFullName() ? user.getFullName() : "";
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
