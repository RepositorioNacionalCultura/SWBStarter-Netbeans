/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.resources;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import org.bson.Document;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import com.google.gson.Gson;
import org.semanticwb.Logger;
import org.semanticwb.SWBUtils;

import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.gob.cultura.portal.persist.CollectionMgr;
import mx.gob.cultura.portal.persist.UserCollectionMgr;

import mx.gob.cultura.portal.response.Entry;
import mx.gob.cultura.portal.response.Collection;
import mx.gob.cultura.portal.request.GetBICRequest;
import mx.gob.cultura.portal.response.UserCollection;
import static mx.gob.cultura.portal.utils.Constants.COUNT_BY_REST;
import static mx.gob.cultura.portal.utils.Constants.COUNT_BY_STAT;
import static mx.gob.cultura.portal.utils.Constants.COUNT_BY_USER;
import static mx.gob.cultura.portal.utils.Constants.COUNT_BY_FAVS;
import static mx.gob.cultura.portal.utils.Constants.COUNT_BY_THMS;
import static mx.gob.cultura.portal.utils.Constants.COUNT_BY_ADVC;

import static mx.gob.cultura.portal.utils.Constants.COLLECTION;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION_PUBLIC;

import static mx.gob.cultura.portal.utils.Constants.COLLECTION_TYPE;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION_TYPE_ADV;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION_TYPE_ALL;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION_TYPE_FAV;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION_TYPE_FND;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION_TYPE_OWN;

import static mx.gob.cultura.portal.utils.Constants.ADVICE_BY_ACVTVY;
import static mx.gob.cultura.portal.utils.Constants.ADVICE_BY_THEMES;

import org.semanticwb.model.User;
import org.semanticwb.SWBPlatform;
import org.semanticwb.portal.api.SWBResourceURL;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBActionResponse;
import org.semanticwb.portal.api.SWBResourceException;

import mx.gob.cultura.portal.utils.Utils;
import static mx.gob.cultura.portal.utils.Constants.FULL_LIST;
import static mx.gob.cultura.portal.utils.Constants.MAX_WORDS;
import static mx.gob.cultura.portal.utils.Constants.MIN_FAVS;
import static mx.gob.cultura.portal.utils.Constants.MODE_PAGE;
import static mx.gob.cultura.portal.utils.Constants.NUM_PAGE_JUMP;
import static mx.gob.cultura.portal.utils.Constants.NUM_PAGE_LIST;
import static mx.gob.cultura.portal.utils.Constants.PARAM_REQUEST;

import static mx.gob.cultura.portal.utils.Constants.NUM_ROW;
import static mx.gob.cultura.portal.utils.Constants.NUM_RECORDS_TOTAL;
import static mx.gob.cultura.portal.utils.Constants.NUM_RECORDS_VISIBLE;

import static mx.gob.cultura.portal.utils.Constants.THEME;
import static mx.gob.cultura.portal.utils.Constants.PAGE_LIST;
import static mx.gob.cultura.portal.utils.Constants.TOTAL_PAGES;
import static mx.gob.cultura.portal.utils.Constants.PAGE_NUM_ROW;
import static mx.gob.cultura.portal.utils.Constants.PAGE_JUMP_SIZE;
import org.semanticwb.portal.api.GenericAdmResource;

/**
 *
 * @author sergio.tellez
 */
public class MyCollections extends GenericAdmResource {
    
    public static final String ENTRY = "entry";
    public static final String IDENTIFIER = "id";
    
    public static final String MODE_ADD = "ADD";
    public static final String MODE_EDIT = "EDIT";
    public static final String ACTION_STA = "STA";
    public static final String ACTION_ADD = "SAVE";
    public static final String MODE_RES_ADD = "RES_ADD";
    public static final String MODE_VIEW_USR = "VIEW_USR";
    public static final String MODE_VIEW_ALL = "VIEW_ALL";
    public static final String ACTION_DEL_FAV = "DEL_FAV";
    public static final String ACTION_ADD_CLF = "ADD_CLF";
    public static final String MODE_VIEW_FIND = "VIEW_FIND";
    public static final String MODE_VIEW_MYALL = "VIEW_MYALL";
    public static final String MODE_VIEW_MYFAV = "VIEW_MYFAV";
    public static final String MODE_VIEW_THEME = "VIEW_THEME";
    public static final String MODE_VIEW_SUGST = "VIEW_SUGST";
    
    public static final String COLLECTION_RENDER = "_collection";
    
    private static final Logger LOG = SWBUtils.getLogger(MyCollections.class);
    
    private static final Map<String, List<Collection>> props = new HashMap<>();
    
    CollectionMgr mgr = CollectionMgr.getInstance();
    UserCollectionMgr umr = UserCollectionMgr.getInstance();
    
    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        String mode = paramRequest.getMode();
        if (MODE_ADD.equals(mode)) {
            doAdd(request, response, paramRequest);
        }else if (MODE_EDIT.equals(mode)) {
            doEdit(request, response, paramRequest);
        }else if (MODE_PAGE.equals(mode)) {
            doPage(request, response, paramRequest);
        }else if (MODE_VIEW_USR.equals(mode)) {
            collectionById(request, response, paramRequest);
        }else if (MODE_RES_ADD.equals(mode)) {
            redirectJsonResponse(request, response);
        }else if (MODE_VIEW_ALL.equals(mode)) {
            doViewAll(request, response, paramRequest);
        }else if (MODE_VIEW_MYALL.equals(mode)) {
            doViewMyAll(request, response, paramRequest);
        }else if (MODE_VIEW_FIND.equals(mode)) {
            doSearch(request, response, paramRequest);
        }else if (MODE_VIEW_MYFAV.equals(mode)) {
            doViewMyFav(request, response, paramRequest);
        }else if (MODE_VIEW_THEME.equals(mode)) {
            doViewTheme(request, response, paramRequest);
        }else if (MODE_VIEW_SUGST.equals(mode)) {
            doViewAdvise(request, response, paramRequest);
        }else
            super.processRequest(request, response, paramRequest);
    }
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        Integer favs = 0;
        Integer total = 0;
        User user = paramRequest.getUser();
        response.setContentType("text/html; charset=UTF-8");
        //String siteid = paramRequest.getWebPage().getWebSite().getId();
        String path = "/swbadmin/jsp/rnc/collections/init.jsp";
        try {
            request.setAttribute(PARAM_REQUEST, paramRequest);
            if (null != user && user.isSigned()) {
                total = count(null, user.getId(), null).intValue();
                try {
                    favs = umr.countByUser(null, paramRequest.getUser().getId()).intValue();
                }catch (com.mongodb.MongoTimeoutException toe) {
                    LOG.info(toe.getMessage());
                    favs = 0;
                }
            }
            if (total > 0) {
                path = "/swbadmin/jsp/rnc/collections/mycollections.jsp";
                List<Collection> collectionList = collectionList(null, paramRequest.getUser(), 1, NUM_ROW);
                setAuthors(paramRequest, collectionList);
                setCovers(paramRequest, collectionList, 3);
                request.setAttribute(FULL_LIST, collectionList);
                request.setAttribute(PARAM_REQUEST, paramRequest);
                request.setAttribute(NUM_RECORDS_TOTAL, total);
                request.setAttribute(COUNT_BY_USER, total);
                request.setAttribute(COUNT_BY_THMS, themes(paramRequest.getUser()));
                request.setAttribute(COUNT_BY_ADVC, countAdvices(paramRequest));
                init(request);
            }
            request.setAttribute(NUM_RECORDS_TOTAL, total);
            request.setAttribute(COUNT_BY_FAVS, favs);
            request.setAttribute(COUNT_BY_STAT, count(null, null, COLLECTION_PUBLIC).intValue());
            RequestDispatcher rd = request.getRequestDispatcher(path);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public void doViewMyAll(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        Integer favs = 0;
        Integer total = 0;
        User user = paramRequest.getUser();
        response.setContentType("text/html; charset=UTF-8");
        //String siteid = paramRequest.getWebPage().getWebSite().getId();
        //String path = "/work/models/"+siteid+"/jsp/rnc/collections/mycollections.jsp";
        String path = "/swbadmin/jsp/rnc/collections/mycollections.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            if (null != user && user.isSigned()) {
                total = count(null, user.getId(), null).intValue();
                favs = umr.countByUser(null, paramRequest.getUser().getId()).intValue();
            }
            List<Collection> collectionList = collectionList(null, paramRequest.getUser(), 1, NUM_ROW);
            setAuthors(paramRequest, collectionList);
            setCovers(paramRequest, collectionList, 3);
            request.setAttribute(FULL_LIST, collectionList);
            request.setAttribute(PARAM_REQUEST, paramRequest);
            //request.setAttribute("mycollections", collectionList);
            request.setAttribute(NUM_RECORDS_TOTAL, total);
            request.setAttribute(COUNT_BY_FAVS, favs);
            request.setAttribute(COUNT_BY_USER, total);
            request.setAttribute(COUNT_BY_THMS, themes(paramRequest.getUser()));
            request.setAttribute(COUNT_BY_STAT, count(null, null, COLLECTION_PUBLIC).intValue());
            request.setAttribute(COUNT_BY_ADVC, countAdvices(paramRequest));
            init(request);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public void doViewAll(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        Integer favs = 0;
        Integer total = 0;
        response.setContentType("text/html; charset=UTF-8");
        //String siteid = paramRequest.getWebPage().getWebSite().getId();
        //String path = "/work/models/"+siteid+"/jsp/rnc/collections/mycollections.jsp";
        String path = "/swbadmin/jsp/rnc/collections/mycollections.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            total = count(null, null, COLLECTION_PUBLIC).intValue();
            favs = umr.countByUser(null, paramRequest.getUser().getId()).intValue();
            int count = count(null, paramRequest.getUser().getId(), null).intValue();
            List<Collection> collectionList = collectionList(null, null, 1, NUM_ROW);
            setAuthors(paramRequest, collectionList);
            setCovers(paramRequest, collectionList, 3);
            request.setAttribute(FULL_LIST, collectionList);
            request.setAttribute(PARAM_REQUEST, paramRequest);
            request.setAttribute(NUM_RECORDS_TOTAL, total);
            request.setAttribute(COUNT_BY_STAT, total);
            request.setAttribute(COUNT_BY_USER, count);
            request.setAttribute(COUNT_BY_FAVS, favs);
            request.setAttribute(COUNT_BY_THMS, themes(paramRequest.getUser()));
            request.setAttribute(COUNT_BY_ADVC, countAdvices(paramRequest));
            request.setAttribute(COLLECTION_TYPE, COLLECTION_TYPE_ALL);
            init(request);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public void doPage(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, java.io.IOException {
        int pagenum = 0;
        Integer total = 0;
        String p = request.getParameter("p");
        List<Collection> collectionList = null;
        if (null != p)  pagenum = Integer.parseInt(p);
        if (pagenum<=0) pagenum = 1;
        request.setAttribute(NUM_PAGE_LIST, pagenum);
        request.setAttribute(PAGE_NUM_ROW, NUM_ROW);
        String siteid = null;//paramRequest.getWebPage().getWebSite().getId();
        if (Utils.toInt(request.getParameter(COLLECTION_TYPE)) == COLLECTION_TYPE_ALL) {
            total = count(siteid, null, COLLECTION_PUBLIC).intValue();
            collectionList = collectionList(siteid, null, pagenum, NUM_ROW);
            request.setAttribute(COLLECTION_TYPE, COLLECTION_TYPE_ALL);
        } else if (Utils.toInt(request.getParameter(COLLECTION_TYPE)) == COLLECTION_TYPE_FND) {
            Document reference = getReference(request, paramRequest, pagenum);
            total = (Integer)reference.get("records");
            collectionList = (List<Collection>)reference.get("references");
            request.setAttribute(COLLECTION_TYPE, COLLECTION_TYPE_FND);
        } else if (Utils.toInt(request.getParameter(COLLECTION_TYPE)) == COLLECTION_TYPE_FAV) {
            total = umr.countByUser(siteid, paramRequest.getUser().getId()).intValue();
            collectionList = mgr.favorites(siteid, paramRequest.getUser().getId(), pagenum, NUM_ROW);
            request.setAttribute(COLLECTION_TYPE, COLLECTION_TYPE_FAV);
        } else if (Utils.toInt(request.getParameter(COLLECTION_TYPE)) == COLLECTION_TYPE_ADV) {
            Document reference = getAdvise(paramRequest, pagenum);
            total = (Integer)reference.get("records");
            collectionList = (List<Collection>)reference.get("references");
            request.setAttribute(COLLECTION_TYPE, COLLECTION_TYPE_ADV);
        }  else {
            total = count(siteid, paramRequest.getUser().getId(), null).intValue();
            collectionList = collectionList(siteid, paramRequest.getUser(), pagenum, NUM_ROW);
            request.setAttribute(COLLECTION_TYPE, COLLECTION_TYPE_OWN);
        }
        setAuthors(paramRequest, collectionList);
        setCovers(paramRequest, collectionList, 3);
        request.setAttribute(FULL_LIST, collectionList);
        request.setAttribute(NUM_RECORDS_TOTAL, total);
        page(pagenum, request);
        String url = "/swbadmin/jsp/rnc/collections/rows.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(url);
        try {
            request.setAttribute(PARAM_REQUEST, paramRequest);
            rd.include(request, response);
        }catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public void doSearch(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        //String siteid = paramRequest.getWebPage().getWebSite().getId();
        String path = "/swbadmin/jsp/rnc/collections/mycollections.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            Document reference = getReference(request, paramRequest, 1);
            Integer results = (Integer)reference.get("records");
            List<Collection> collectionList = (List<Collection>)reference.get("references");
            request.setAttribute(FULL_LIST, collectionList);
            request.setAttribute(PARAM_REQUEST, paramRequest);
            request.setAttribute(NUM_RECORDS_TOTAL, results);
            request.setAttribute(COUNT_BY_REST, results);
            request.setAttribute(COUNT_BY_THMS, themes(paramRequest.getUser()));
            request.setAttribute(COUNT_BY_STAT, count(null, null, COLLECTION_PUBLIC).intValue());
            request.setAttribute(COUNT_BY_ADVC, countAdvices(paramRequest));
            request.setAttribute(COUNT_BY_USER, count(null, paramRequest.getUser().getId(), null).intValue());
            request.setAttribute(COUNT_BY_FAVS, umr.countByUser(null, paramRequest.getUser().getId()).intValue());
            request.setAttribute(COLLECTION_TYPE, COLLECTION_TYPE_FND);
            init(request);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public void doViewAdvise(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        String siteid = null;//paramRequest.getWebPage().getWebSite().getId();
        String path = "/swbadmin/jsp/rnc/collections/mycollections.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            Document reference = getAdvise(paramRequest, 1);
            Integer results = (Integer)reference.get("records");
            List<Collection> collectionList = (List<Collection>)reference.get("references");
            request.setAttribute(FULL_LIST, collectionList);
            request.setAttribute(PARAM_REQUEST, paramRequest);
            request.setAttribute(NUM_RECORDS_TOTAL, results);
            request.setAttribute(COUNT_BY_ADVC, results);
            request.setAttribute(COUNT_BY_THMS, themes(paramRequest.getUser()));
            request.setAttribute(COUNT_BY_STAT, count(siteid, null, COLLECTION_PUBLIC).intValue());
            request.setAttribute(COUNT_BY_USER, count(siteid, paramRequest.getUser().getId(), null).intValue());
            request.setAttribute(COUNT_BY_FAVS, umr.countByUser(siteid, paramRequest.getUser().getId()).intValue());
            request.setAttribute(COLLECTION_TYPE, COLLECTION_TYPE_ADV);
            init(request);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public void doViewMyFav(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        Integer total = 0;
        response.setContentType("text/html; charset=UTF-8");
        String siteid = null;//paramRequest.getWebPage().getWebSite().getId();
         String path = "/swbadmin/jsp/rnc/collections/mycollections.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            total = umr.countByUser(siteid, paramRequest.getUser().getId()).intValue();
            List<Collection> collectionList = mgr.favorites(siteid, paramRequest.getUser().getId(), 1, NUM_ROW);
            setAuthors(paramRequest, collectionList);
            setCovers(paramRequest, collectionList, 3);
            request.setAttribute(FULL_LIST, collectionList);
            request.setAttribute(PARAM_REQUEST, paramRequest);
            request.setAttribute(NUM_RECORDS_TOTAL, total);
            request.setAttribute(COUNT_BY_FAVS, total);
            request.setAttribute(COUNT_BY_THMS, themes(paramRequest.getUser()));
            request.setAttribute(COUNT_BY_STAT, count(siteid, null, COLLECTION_PUBLIC).intValue());
            request.setAttribute(COUNT_BY_USER, count(siteid, paramRequest.getUser().getId(), null).intValue());
            request.setAttribute(COUNT_BY_ADVC, countAdvices(paramRequest));
            request.setAttribute(COLLECTION_TYPE, COLLECTION_TYPE_FAV);
            init(request);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public void doViewTheme(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        String siteid = null;//paramRequest.getWebPage().getWebSite().getId();
        String path = "/swbadmin/jsp/rnc/collections/mycollections.jsp";
        //String path = "/work/models/"+siteid+"/jsp/rnc/collections/mycollections.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        List<Collection> collectionList = getThemes(paramRequest);
        String uels = "/"+paramRequest.getUser().getLanguage()+"/"+paramRequest.getWebPage().getWebSiteId()+"/resultados?word=*";
        try {
            request.setAttribute("uels", uels);
            request.setAttribute(NUM_RECORDS_TOTAL, 0);
            request.setAttribute(FULL_LIST, collectionList);
            request.setAttribute(PARAM_REQUEST, paramRequest);
            request.setAttribute(COUNT_BY_THMS, themes(paramRequest.getUser()));
            request.setAttribute(COUNT_BY_STAT, count(siteid, null, COLLECTION_PUBLIC).intValue());
            request.setAttribute(COUNT_BY_ADVC, countAdvices(paramRequest));
            request.setAttribute(COUNT_BY_USER, count(siteid, paramRequest.getUser().getId(), null).intValue());
            request.setAttribute(COUNT_BY_FAVS, countByUser(siteid, paramRequest.getUser().getId()).intValue());
            init(request);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public void collectionById(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        User user = paramRequest.getUser();
        Collection collection = null;
        List<Collection> list = new ArrayList<>();
        List<Entry> favorites = new ArrayList<>();
        String path = "/swbadmin/jsp/rnc/collections/elements.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            if (null != user && user.isSigned() && null != request.getParameter(IDENTIFIER)) {
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
        String path = "/swbadmin/jsp/rnc/collections/collection.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        if (null != request.getParameter(IDENTIFIER) /**&& null != request.getSession().getAttribute("mycollections")**/) {
            //Integer id = Integer.valueOf(request.getParameter(IDENTIFIER));
            //List<Collection> collectionList = (List<Collection>)request.getSession().getAttribute("mycollections")
            //for (Collection c : collectionList) {
                //if (c.getId().equals(request.getParameter(IDENTIFIER))) {
            Collection c = find(request.getParameter(IDENTIFIER));
            if (null != c) request.setAttribute(COLLECTION, c);
                    //break;
                //}
            //}
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
        String siteid = null;//response.getWebPage().getWebSiteId();
        /**if (null != request.getSession().getAttribute("mycollections"))
            collectionList = (List<Collection>)request.getSession().getAttribute("mycollections");**/
        if (SWBResourceURL.Action_REMOVE.equals(response.getAction())) {
            if (null != user && user.isSigned() && null != request.getParameter(IDENTIFIER)) {
                mgr.deleteCollection(request.getParameter(IDENTIFIER));
                collectionList = collectionList(siteid, user);
                Gson gson = new Gson();
                response.setRenderParameter(COLLECTION_RENDER, gson.toJson(new Collection("", false, "")));
                response.setMode(MODE_RES_ADD);
                response.setCallMethod(SWBParamRequest.Call_DIRECT);
                //request.getSession().setAttribute("mycollections", collectionList);
            }
        }else if (SWBResourceURL.Action_EDIT.equals(response.getAction())) {
            Collection collection = setCollection(request, false);
            if (null != user && user.isSigned() && !collection.isEmpty() && null != request.getParameter(IDENTIFIER)) {
                collectionList = collectionList(siteid, user);
                if (!exist(siteid, collection.getTitle(), request.getParameter(IDENTIFIER))) {
                    for (Collection c : collectionList) {
                        if (c.getId().equals(request.getParameter(IDENTIFIER))) {
                            c.setUserid(user.getId());
                            c.setTitle(collection.getTitle());
                            c.setDescription(collection.getDescription());
                            c.setSiteid(response.getWebPage().getWebSiteId());
                            c.setUserName(getAuthor(user.getId(), null, response));
                            c.setStatus(Utils.getStatus(request.getParameter("status"), c.getStatus()));
                            Gson gson = new Gson();
                            update(c);
                            response.setRenderParameter(COLLECTION_RENDER, gson.toJson(c));
                            break;
                        }
                    }
                }else {
                    Gson gson = new Gson();
                    response.setRenderParameter(COLLECTION_RENDER, gson.toJson(collection));
                }
                response.setMode(MODE_RES_ADD);
                response.setCallMethod(SWBParamRequest.Call_DIRECT);
                //request.getSession().setAttribute("mycollections", collectionList);
            }
        }else if (ACTION_STA.equals(response.getAction())) {
            Collection c = new Collection("", false, "");
            int idTh = Utils.toInt(request.getParameter(IDENTIFIER))-1;
            if (idTh >= 0) {
                String themes = getThemes(request.getParameter(IDENTIFIER), null != user.getProperty(THEME) && !user.getProperty(THEME).isEmpty() ? user.getProperty(THEME) : "00000000");
                user.setProperty(THEME, themes);
            }
            List<Collection> list = getThemes(null);
            if (idTh < list.size())
                c = list.get(idTh);
            Gson gson = new Gson();
            response.setRenderParameter(COLLECTION_RENDER, gson.toJson(c));
            response.setMode(MODE_RES_ADD);
            response.setCallMethod(SWBParamRequest.Call_DIRECT);
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
        }else if (ACTION_ADD.equals(response.getAction())) {
            Collection c = setCollection(request, false);
            if (null != user && user.isSigned() && !c.isEmpty()) {
                if (!exist(siteid, c.getTitle(), null)) {
                    c.setSiteid(siteid);
                    c.setUserid(user.getId());
                    c.setUserName(getAuthor(user.getId(), null, response));
                    String id = create(c);
                    if (null != id) c.setId(id);
                    collectionList.add(c);
                    //request.getSession().setAttribute("mycollections", collectionList);
                }
                Gson gson = new Gson();
                response.setRenderParameter(COLLECTION_RENDER, gson.toJson(c));
                response.setMode(MODE_RES_ADD);
                response.setCallMethod(SWBParamRequest.Call_DIRECT);
            }
        }else if (ACTION_ADD_CLF.equals(response.getAction()) && null != request.getParameter(IDENTIFIER) && !request.getParameter(IDENTIFIER).trim().isEmpty()) {
            Gson gson = new Gson();
            Collection c = find(request.getParameter(IDENTIFIER));
            if (!umr.exist(user.getId(), request.getParameter(IDENTIFIER))) {
                UserCollection uc = new UserCollection(siteid, user.getId(), request.getParameter(IDENTIFIER));
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
    
    public void doAdd(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws java.io.IOException {
        String path = "/swbadmin/jsp/rnc/collections/collection.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            request.setAttribute(PARAM_REQUEST, paramRequest);
            request.setAttribute(COLLECTION, new Collection("", false, ""));
            rd.include(request, response);
        }catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public static List<String> getCovers(SWBParamRequest paramRequest, List<String> elements, String baseUri, int size) {
        Entry entry = null;
        List<String> covers = new ArrayList<>();
        if (null == elements || elements.isEmpty()) return covers;
        Iterator it = elements.iterator();
        while (it.hasNext()) {
            String uri = baseUri + "/api/v1/search?identifier=" + it.next();
            GetBICRequest req = new GetBICRequest(uri);
            try {
                entry = req.makeRequest();
            }catch (Exception e) {
                entry = null;
                LOG.info(e.getMessage());
            }
            if (null != entry) {
                if (null != entry.getResourcethumbnail() && !entry.getResourcethumbnail().trim().isEmpty())
                    covers.add(entry.getResourcethumbnail());
                else {
                    SearchCulturalProperty.setThumbnail(entry, paramRequest.getWebPage().getWebSite(), size);
                    covers.add(entry.getResourcethumbnail());
                }
            }
            if (covers.size() >= size) break;
        }
        return covers;
    }
    
    public static String getAuthor(String userid, SWBParamRequest paramRequest, SWBActionResponse response) {
        User user = null;
        if (null != paramRequest)
            user = paramRequest.getWebPage().getWebSite().getUserRepository().getUser(userid);
        else if (null != response)
            user = response.getWebPage().getWebSite().getUserRepository().getUser(userid);
        return null != user && null != user.getFullName() ? user.getFullName() : "";
    }
    
    protected Long count(String siteid, String userid, Boolean status) {
        Long count = 0L;
        try {
            if (null != userid) count = mgr.countByUser(siteid, userid);
            else count = mgr.countAllByStatus(siteid, status);
        }catch(Exception e) {
            LOG.info(e.getMessage());
        }
        return count; 
    }
    
    protected List<Collection> collectionList(String siteid, User user, Integer from, Integer leap) {
        List<Collection> collection = new ArrayList<>();
        if (null != user && user.isSigned())
            collection = mgr.collectionsByUserLimit(siteid, user.getId(), from, leap);
        else {
            collection = mgr.collectionsByStatusLimit(siteid, COLLECTION_PUBLIC, from, leap);
        }
        return collection;
    }
    
    private Document getReference(HttpServletRequest request, SWBParamRequest paramRequest, int page) {
        Integer records = 0;
        Document reference = new Document();
        List<Collection> collectionList = new ArrayList<>();
        try {
            String criteria = null != request.getParameter("criteria") ? request.getParameter("criteria").trim() : "";
            request.setAttribute("criteria", criteria);
            if (!criteria.isEmpty()) records = mgr.countByCriteria(criteria, COLLECTION_PUBLIC).intValue();
            if (records > 0) {
                collectionList = mgr.findByCriteria(criteria, COLLECTION_PUBLIC, page, NUM_ROW);
                setAuthors(paramRequest, collectionList);
                setCovers(paramRequest, collectionList, 3);
            }
            reference.append("references", collectionList).append("records", records);
        }catch (Exception se) {
            LOG.info(se.getMessage());
        }
        return reference;
    }
    
    private Document getAdvise(SWBParamRequest paramRequest, int page) {
        Integer records = 0;
        String criteria = "";
        Document reference = new Document();
        List<Collection> collectionList = new ArrayList<>();
        try {
            int type = getAdviceType(paramRequest);
            if (type == ADVICE_BY_THEMES)
                criteria = getThemeCriteria(paramRequest);
            else if (type == ADVICE_BY_ACVTVY) criteria = getAcvtyCriteria(paramRequest);
            else return reference;
            records = countByAdvice(criteria, COLLECTION_PUBLIC);
            if (records > 0)
                collectionList = findByCriteria(paramRequest, criteria, page);
            reference.append("references", collectionList).append("records", records);
        }catch (Exception se) {
            LOG.info(se.getMessage());
        }
        return reference;
    }
    
    private Integer countAdvices(SWBParamRequest paramRequest) {
        String criteria = null;
        int type = getAdviceType(paramRequest);
        if (type == -1) return 0;
        if (type == ADVICE_BY_THEMES)
            criteria = getThemeCriteria(paramRequest);
        else if (type == ADVICE_BY_ACVTVY) criteria = getAcvtyCriteria(paramRequest);
        return countByAdvice(criteria, COLLECTION_PUBLIC);
    }
    
    private int getAdviceType(SWBParamRequest paramRequest) {
        int MIN_ACT_FAV = Utils.toInt(paramRequest.getResourceBase().getAttribute("minfavs", MIN_FAVS));
        int favs = umr.countByUser(paramRequest.getWebPage().getWebSiteId(), paramRequest.getUser().getId()).intValue();
        if (favs > MIN_ACT_FAV) return ADVICE_BY_ACVTVY;
        if (null != paramRequest.getUser().getProperty(THEME) && !paramRequest.getUser().getProperty(THEME).isEmpty() 
                && paramRequest.getUser().getProperty(THEME).contains("1"))
            return ADVICE_BY_THEMES;
        return -1;
    }
    
    private Integer countByAdvice(String criteria, Boolean status) {
        if (null != criteria && !criteria.isEmpty()) return mgr.countByCriteria(criteria, status).intValue();
        return 0;
    }
    
    private String getAcvtyCriteria(SWBParamRequest paramRequest) {
        List<String> users = new ArrayList<>();
        StringBuilder authors = new StringBuilder();
        StringBuilder criteria = new StringBuilder();
        List<Collection> list = mgr.favorites(paramRequest.getWebPage().getWebSiteId(), paramRequest.getUser().getId(), 1, NUM_ROW);
        for (Collection c : list) {
            if (null != c.getTitle() && !c.getTitle().trim().isEmpty()) criteria.append(" ").append(c.getTitle());
            if (null != c.getUserName() && !c.getUserName().trim().isEmpty() && !users.contains(c.getUserName())) {
                users.add(c.getUserName());
                authors.append(" ").append(c.getUserName());
            }
        }
        criteria.append(authors);
        return criteria.toString().trim();
    }
    
    private String getThemeCriteria(SWBParamRequest paramRequest) {
        int cwords = 0;
        Collection c = null;
        String criteria = "";
        List<Collection> themes = getThemes(paramRequest);
        if (null == paramRequest.getUser().getProperty(THEME)) return "";
        String ths = paramRequest.getUser().getProperty(THEME);
        int MAX_WORDS_THM = Utils.toInt(paramRequest.getResourceBase().getAttribute("maxwords", MAX_WORDS));
        for (int i=0; i<ths.length(); i++) {
            if (ths.substring(i, i+1).equalsIgnoreCase("1") && cwords<MAX_WORDS_THM) {
                c = themes.get(i);
                for (String item : c.getElements()) {
                    if (cwords<MAX_WORDS_THM) {
                        cwords++;
                        criteria += " " + item;
                    }
                }
            }
        }
        criteria = criteria.trim();
        return criteria;
    }
    
    private String getThemes(String id, String theme) {
        String tmp = "";
        int position = Utils.toInt(id)-1;
        if (position >= 0 && position < 8) {
            for (int i=0; i<theme.length(); i++) {
                String c = theme.substring(i, i+1);
                if (position == i) {
                    if (c.equalsIgnoreCase("0")) tmp += "1";
                    else tmp += "0";
                }else {
                    tmp += c;
                }
            }
        }
        return tmp;
    }
    
    private Integer themes(User user) {
        int count = 0;
        int fromIndex = 0;
        if (null == user.getProperty(THEME)) return 0;
        String themes = user.getProperty(THEME);
        while (fromIndex < themes.length()) {
            fromIndex = themes.indexOf("1", fromIndex);
            if (fromIndex == -1) break;
            fromIndex++;
            count++;
        }
        return count;
    }
    
    private List<Collection> findByCriteria(SWBParamRequest paramRequest, String criteria, int page) {
        List<Collection> collectionList = new ArrayList<>();
        if (null != criteria && !criteria.trim().isEmpty()) {
            collectionList = mgr.findByCriteria(criteria, COLLECTION_PUBLIC, page, NUM_ROW);
            setAuthors(paramRequest, collectionList);
            setCovers(paramRequest, collectionList, 3);
        }
        return collectionList;
    }
    
    private List<String> getCovers(SWBParamRequest paramRequest, List<String> elements, String baseUri, int size, Collection c) {
        Entry entry = null;
        List<String> covers = new ArrayList<>();
        if (elements.isEmpty()) return covers;
        Iterator it = elements.iterator();
        while (it.hasNext()) {
            String itnext = (String)it.next();
            String uri = baseUri + "/api/v1/search?identifier=" + itnext;
            GetBICRequest req = new GetBICRequest(uri);
            try {
                entry = req.makeRequest();
            }catch (Exception e) {
                entry = null;
                removeDeletedBicFromCollection(c,itnext);
                LOG.info(e.getMessage());
            }
            if (null != entry) {
                if (null != entry.getResourcethumbnail() && !entry.getResourcethumbnail().trim().isEmpty())
                    covers.add(entry.getResourcethumbnail());
                else {
                    SearchCulturalProperty.setThumbnail(entry, paramRequest.getWebPage().getWebSite(), size);
                    covers.add(entry.getResourcethumbnail());
                }
            }
            if (covers.size() >= size) break;
        }
        return covers;
    }
    
    private void removeDeletedBicFromCollection(Collection c, String bicId){
        c.getElements().remove(bicId);
        mgr.updateCollection(c);
    }
    
    private List<Collection> collectionList(String siteid, User user) {
        List<Collection> collection = new ArrayList<>();
        if (null != user && user.isSigned())
            collection = mgr.collections(siteid, user.getId());
        else {
            collection = mgr.collectionsByStatus(siteid, COLLECTION_PUBLIC);
        }
        return collection;
    }
    
    protected Entry getEntry(SWBParamRequest paramRequest, String _id) {
        String baseUri = paramRequest.getWebPage().getWebSite().getModelProperty("search_endPoint");
        if (null == baseUri || baseUri.isEmpty()) {
            baseUri = SWBPlatform.getEnv("rnc/endpointURL", getResourceBase().getAttribute("url", "http://localhost:8080")).trim();
        }
        String uri = baseUri + "/api/v1/search?identifier=";
        uri += _id;
        GetBICRequest req = new GetBICRequest(uri);
        return req.makeRequest();
    }
    
    protected void init(HttpServletRequest request) throws SWBResourceException, java.io.IOException {
        int pagenum = 0;
        String p = request.getParameter("p");
        if (null != p) pagenum = Integer.parseInt(p);
        if (pagenum<=0) pagenum = 1;
        request.setAttribute(NUM_PAGE_LIST, pagenum);
        request.setAttribute("PAGE_NUM_ROW", PAGE_NUM_ROW);
        page(pagenum, request);
    }
    
    protected void page(int pagenum, HttpServletRequest request) {
        ArrayList<?> rows = (ArrayList<?>)request.getAttribute(FULL_LIST);
        Integer total = (Integer)request.getAttribute(NUM_RECORDS_TOTAL);
        if (null==total) total = 0;
        if (null==rows) rows = new ArrayList();
        try {
            Integer totalPages = total/NUM_ROW;
            if (total%NUM_ROW != 0)
                totalPages ++;
            request.setAttribute(TOTAL_PAGES, totalPages);
            Integer currentLeap = (pagenum-1)/PAGE_JUMP_SIZE;
            request.setAttribute(NUM_PAGE_JUMP, currentLeap);
            request.setAttribute("PAGE_JUMP_SIZE", PAGE_JUMP_SIZE);
            request.setAttribute(PAGE_LIST, rows);
            request.setAttribute(NUM_RECORDS_VISIBLE, rows.size());
        }catch(Exception e) {
            LOG.info(e.getMessage());
        }
    }
    
    /**private void page(int pagenum, HttpServletRequest request) {
        List<?> rows = (List<?>)request.getAttribute(FULL_LIST);
        Integer total = (Integer)request.getAttribute("NUM_RECORDS_TOTAL");
        if (null==rows) rows = new ArrayList();
        try {
            Integer totalPages = total/NUM_ROW;
            if (total%NUM_ROW != 0)
                totalPages ++;
            request.setAttribute(TOTAL_PAGES, totalPages);
            Integer currentLeap = (pagenum-1)/PAGE_JUMP_SIZE;
            request.setAttribute(NUM_PAGE_JUMP, currentLeap);
            request.setAttribute(STR_JUMP_SIZE, PAGE_JUMP_SIZE);
            ArrayList rowsPage = getRows(pagenum, rows);
            request.setAttribute(PAGE_LIST, rowsPage);
            request.setAttribute(NUM_RECORDS_TOTAL, total);
            request.setAttribute(NUM_RECORDS_VISIBLE, rowsPage.size());
        }catch(Exception e) {
            LOG.info(e.getMessage());
        }
    }
    
    private ArrayList getRows(int page, List<?> rows) {
        int pageCount = 1;
        if (rows==null || rows.isEmpty()) return new ArrayList();
        Map<Integer, ArrayList<?>> pagesRows = new HashMap<>();
        ArrayList pageRows = new ArrayList();
        pagesRows.put(pageCount, pageRows);
        for (int i=0; i<rows.size(); i++) {
            pageRows.add(rows.get(i));
            if (i+1 < rows.size() && ((i+1) % NUM_ROW) == 0) {
                pageCount++;
                pageRows = new ArrayList();
                pagesRows.put(pageCount, pageRows);
            }
        }
        ArrayList rowsPage = pagesRows.get(page);
        if (rowsPage==null) rowsPage = new ArrayList();
        return rowsPage;
    }**/
    
    private String create(Collection c) {
        String id = mgr.insertCollection(c);
        return id;
    }
    
    private long update(Collection c) {
        return mgr.updateCollection(c);
    }
    
    private Long countByUser(String siteid, String userid) {
        Long count = 0L;
        try {
            if (null != userid) count = umr.countByUser(siteid, userid);
        }catch(Exception e) {
            LOG.info(e.getMessage());
        }
        return count; 
    }
    
    private Collection find(String _id) {
        return mgr.findById(_id);
    }
    
    private boolean exist(String siteid, String title, String _id) {
        return mgr.exist(siteid, title, _id);
    }
    
    private Collection setCollection(HttpServletRequest request, boolean prevst) {
        Collection collection = new Collection(request.getParameter("title").trim(), Utils.getStatus(request.getParameter("status"), prevst), request.getParameter("description").trim());
        return collection;
    }
     
    private void setAuthors(SWBParamRequest paramRequest, List<Collection> list) {
        if (null == list || list.isEmpty()) return;
        for (Collection c : list) {
            if (null != c && null != c.getUserid()) {
                c.setUserName(getAuthor(c.getUserid(), paramRequest, null));
            }
        }
    }
     
    protected void setCovers(SWBParamRequest paramRequest, List<Collection> list,  int size) {
        String baseUri = paramRequest.getWebPage().getWebSite().getModelProperty("search_endPoint");
        if (null == baseUri || baseUri.isEmpty())
            baseUri = SWBPlatform.getEnv("rnc/endpointURL", getResourceBase().getAttribute("url", "http://localhost:8080")).trim();
        for (Collection c : list) {
            c.setCovers(getCovers(paramRequest, c.getElements(), baseUri, size, c));
        }
    }
    
    private List<Collection> getThemes(SWBParamRequest paramRequest) {
        if (null == paramRequest || props.containsKey("themes")) return props.get("themes");
        Collection c = null;
        List<String> covers = null;
        List<Collection> themes = new ArrayList<>();
        String memorysound = "radio::radiofónico::grabación::sonido::concierto::opera::partitura::cantos::entrevista::educación::fonoteca::audio";
        String memoryvisual = "foto::fotografía::fotomontaje::televisión::documental::cartel::dibujo::mapa::video::programa::pintura::obra::gráfica::imagen::cine::cinematografía::digital::mediateca::visual";
        String release = "conferencia::foro::presentación::diálogo::seminario::entrevista::curso::coloquio::artículo::revista::capítulo::libro::conversación::guía::turística::turismo::cuaderno::estudio::expediente::manual::proyecto::restauración::museográfico::reporte::prácticas::catálogo::digital::interactivo::mediateca::divulgación";
        String art = "danza::busto::cartel::escultura::estampa::foto::fotografía::impreso::manuscrito::libro::matriz::ornamento::pintura::relieve::alcancía::anfora::capítulo::bastón::batea::baúl::bolsa::botellón::candelero::cartel::cinturón::falda::florero::fuete::huarache::jarra::jícara::juguete::lazo::mantel::mecapal::olla::paragüero::plato::platón::rebozo::sarape::sillón::sombrero::sopera::taza::tinaja::trastero::utensilio::vasija::animación::documento::grabación::partitura::presentación::programa::tesis::video::pintura::caballete::mural::moderna::novohispana::elemento::arquitectónico::numismática::pieza::arqueológica::exvoto::dibujo::plumario::cristalería::enconchados::vidrio::instrumento::obra::gráfica::escultórica::arte::INBA::mediateca::virreinato::estampa";
        String documents ="libro::revista::manuscrito::borrador::capítulo::documento::tesis::narrativa::cuaderno::catálogo::exposición::reseña::artículo::editorial::plano::códice::mapa::biblioteca::publicaciones::literatura::mediateca::texto";
        String history = "curso::coloquio::foto::fotografía::radio::conferencia::entrevista::exposición::foro::premiación::presentación::seminario::archivo::histórico::coro::acuerdo::armas::bandera::cerámica::códice::contenedor::arquitectónico::escultura::heráldica::herramienta::indumentaria::instrumento::libro::litografía::mapa::mapoteca::maqueta::máscara::miniatura::mobiliario::numismática::objeto::cotidiano::etnográfico::funerario::litúrgico::ornamento::arqueológica::pintura::mural::plano::resto::geológico::óseo::textil::vehículo::vidrio::arma::equipo::indumentaria::joyería::juguete::miniatura::mobiliario::técnica::utensilio::vasija::costa::maya::occidente::aceitera::árbol::banca::barreta::barril::baúl::bieldo::boletero::bolsa::cámara::campana::candado::carrete::coche::cofre::compás::corneta::cucharilla::cucharón::escatillón::escudo::extintor::farola::granada::hielera::inventario::lámpara::lechera::llave::locomotora::machuelo::manómetro::microscopio::modelo::nivel::parihuela::pizarrón::placa::porta::reloj::resonador::sello::señal::silbato::taladro::telegráfono::tenaza::teodolito::velocímetro::verificadora::voltímetro::estudios::históricos::revoluciones::México::historia::mediateca::ferrocarriles";
        String anthropology = "ceremonia::etnografía::cuestionario::elicitación::léxico::palabras::transcripción::gramática::pintura::mural::prehispánica::armas::cerámica::contenedor::arquitectónico::escultura::heráldica::herramienta::indumentaria::instrumento::maqueta::mobiliario::numismática::objeto::cotidiano::ornamento::pieza::arqueológica::óseo::vegetal::textil::fósil::geológico::costa::maya::occidente::lenguas::indígenas::INALI::antropología";
	String archeology = "ceremonia::pieza::arqueología::arqueológica::fósil::geológico::óseo::herramienta::arqueología::lenguas::indígenas::INALI::antropología";
        try {
            c = new Collection(paramRequest.getLocaleString("usrmsg_view_collections_memory_sound"), false, "");
            c.setId("1");
            covers = new ArrayList<>();
            covers.add(0, "/work/models/"+paramRequest.getWebPage().getWebSiteId()+"/img/temas/ms.jpg");
            c.setCovers(covers);
            themes.add(c);
            c.setElements(getElements(memorysound));
            c = new Collection(paramRequest.getLocaleString("usrmsg_view_collections_memory_visual"), false, "");
            c.setId("2");
            covers = new ArrayList<>();
            covers.add(0, "/work/models/"+paramRequest.getWebPage().getWebSiteId()+"/img/temas/visual.jpg");
            c.setCovers(covers);
            themes.add(c);
            c.setElements(getElements(memoryvisual));
            c = new Collection(paramRequest.getLocaleString("usrmsg_view_collections_divulgation"), false, "");
            c.setId("3");
            covers = new ArrayList<>();
            covers.add(0, "/work/models/"+paramRequest.getWebPage().getWebSiteId()+"/img/temas/divulgacion.jpg");
            c.setCovers(covers);
            themes.add(c);
            c.setElements(getElements(release));
            c = new Collection(paramRequest.getLocaleString("usrmsg_view_collections_art"), false, "");
            c.setId("4");
            covers = new ArrayList<>();
            covers.add(0, "/work/models/"+paramRequest.getWebPage().getWebSiteId()+"/img/temas/arte2.jpg");
            c.setCovers(covers);
            themes.add(c);
            c.setElements(getElements(art));
            c = new Collection(paramRequest.getLocaleString("usrmsg_view_collections_documents"), false, "");
            c.setId("5");
            covers = new ArrayList<>();
            covers.add(0, "/work/models/"+paramRequest.getWebPage().getWebSiteId()+"/img/temas/libros3.jpg");
            c.setCovers(covers);
            themes.add(c);
            c.setElements(getElements(documents));
            c = new Collection(paramRequest.getLocaleString("usrmsg_view_collections_history"), false, "");
            c.setId("6");
            covers = new ArrayList<>();
            covers.add(0, "/work/models/"+paramRequest.getWebPage().getWebSiteId()+"/img/temas/historia2.jpg");
            c.setCovers(covers);
            themes.add(c);
            c.setElements(getElements(history));
            c = new Collection(paramRequest.getLocaleString("usrmsg_view_collections_anthropology"), false, "");
            c.setId("7");
            covers = new ArrayList<>();
            covers.add(0, "/work/models/"+paramRequest.getWebPage().getWebSiteId()+"/img/temas/antropologia2.jpg");
            c.setCovers(covers);
            themes.add(c);
            c.setElements(getElements(anthropology));
            c = new Collection(paramRequest.getLocaleString("usrmsg_view_collections_archeology"), false, "");
            c.setId("8");
            covers = new ArrayList<>();
            covers.add(0, "/work/models/"+paramRequest.getWebPage().getWebSiteId()+"/img/temas/arqueologia.jpg");
            c.setCovers(covers);
            themes.add(c);
            c.setElements(getElements(archeology));
        } catch (SWBResourceException ex) {
            LOG.info(ex.getMessage());
        }
        props.put("themes", themes);
        return themes;
    }
    
    private List<String> getElements(String themes) {
        List<String> elements = new ArrayList<>();
        if (null == themes || themes.trim().isEmpty()) return elements;
        String [] theme = themes.split("::");
        for (int i=0; i<theme.length; i++) {
            elements.add(theme[i]);
        }
        return elements;
    }
}
