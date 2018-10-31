/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.resources;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import com.google.gson.Gson;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.gob.cultura.portal.persist.CollectionMgr;

import mx.gob.cultura.portal.response.Entry;
import mx.gob.cultura.portal.response.Collection;
import mx.gob.cultura.portal.request.GetBICRequest;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION_PRIVATE;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION_PUBLIC;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION_TYPE;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION_TYPE_ALL;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION_TYPE_OWN;
import static mx.gob.cultura.portal.utils.Constants.COUNT_BY_STAT;
import static mx.gob.cultura.portal.utils.Constants.COUNT_BY_USER;

import org.semanticwb.model.User;
import org.semanticwb.SWBPlatform;
import org.semanticwb.portal.api.SWBResourceURL;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBActionResponse;
import org.semanticwb.portal.api.SWBResourceException;
import static mx.gob.cultura.portal.utils.Constants.FULL_LIST;
import static mx.gob.cultura.portal.utils.Constants.MODE_PAGE;
import static mx.gob.cultura.portal.utils.Constants.NUM_PAGE_JUMP;
import static mx.gob.cultura.portal.utils.Constants.NUM_PAGE_LIST;
import static mx.gob.cultura.portal.utils.Constants.PARAM_REQUEST;

import static mx.gob.cultura.portal.utils.Constants.NUM_ROW;
import static mx.gob.cultura.portal.utils.Constants.NUM_RECORDS_TOTAL;
import static mx.gob.cultura.portal.utils.Constants.NUM_RECORDS_VISIBLE;

import static mx.gob.cultura.portal.utils.Constants.PAGE_LIST;
import static mx.gob.cultura.portal.utils.Constants.PAGE_NUM_ROW;
import static mx.gob.cultura.portal.utils.Constants.PAGE_JUMP_SIZE;

import static mx.gob.cultura.portal.utils.Constants.STR_JUMP_SIZE;
import static mx.gob.cultura.portal.utils.Constants.TOTAL_PAGES;
import mx.gob.cultura.portal.utils.Utils;

/**
 *
 * @author sergio.tellez
 */
public class MyCollections extends GenericResource {
    
    private static final String ENTRY = "entry";
    public static final String IDENTIFIER = "id";
    
    public static final String MODE_ADD = "ADD";
    public static final String MODE_EDIT = "EDIT";
    public static final String ACTION_STA = "STA";
    public static final String ACTION_ADD = "SAVE";
    public static final String MODE_RES_ADD = "RES_ADD";
    public static final String MODE_VIEW_USR = "VIEW_USR";
    public static final String MODE_VIEW_ALL = "VIEW_ALL";
    public static final String ACTION_DEL_FAV = "DEL_FAV";
    public static final String MODE_VIEW_MYALL = "VIEW_MYALL";
   
    private static final String COLLECTION_RENDER = "_collection";
    
    private static final Logger LOG = Logger.getLogger(MyCollections.class.getName());
    
    CollectionMgr mgr = CollectionMgr.getInstance();
    
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
        }else
            super.processRequest(request, response, paramRequest);
    }
    
    public void doViewAll(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        Integer total = 0;
        response.setContentType("text/html; charset=UTF-8");
        String path = "/swbadmin/jsp/rnc/collections/mycollections.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            int count = count(paramRequest.getUser().getId()).intValue();
            List<Collection> collectionList = collectionList(null);
            setAuthors(paramRequest, collectionList);
            setCovers(paramRequest, collectionList, 3);
            request.setAttribute(FULL_LIST, collectionList);
            request.setAttribute(PARAM_REQUEST, paramRequest);
            total = collectionList.size();
            request.setAttribute(NUM_RECORDS_TOTAL, total);
            request.setAttribute(COUNT_BY_STAT, total);
            request.setAttribute(COUNT_BY_USER, count);
            request.setAttribute(COLLECTION_TYPE, COLLECTION_TYPE_ALL);
            init(request);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public void doViewMyAll(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        String path = "/swbadmin/jsp/rnc/collections/mycollections.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            List<Collection> collectionList = collectionList(paramRequest.getUser());
            setAuthors(paramRequest, collectionList);
            setCovers(paramRequest, collectionList, 3);
            request.setAttribute(FULL_LIST, collectionList);
            request.setAttribute(PARAM_REQUEST, paramRequest);
            request.setAttribute("mycollections", collectionList);
            request.setAttribute(NUM_RECORDS_TOTAL, collectionList.size());
            request.setAttribute(COUNT_BY_STAT, collectionList(null).size());
            request.setAttribute(COUNT_BY_USER, collectionList.size());
            init(request);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        Integer total = 0;
        User user = paramRequest.getUser();
        response.setContentType("text/html; charset=UTF-8");
        String path = "/swbadmin/jsp/rnc/collections/init.jsp";
        try {
            request.setAttribute(PARAM_REQUEST, paramRequest);
            if (null != user && user.isSigned()) total = count(user.getId()).intValue();
            if (total > 0) {
                path = "/swbadmin/jsp/rnc/collections/mycollections.jsp";
                List<Collection> collectionList = collectionList(paramRequest.getUser());
                setAuthors(paramRequest, collectionList);
                setCovers(paramRequest, collectionList, 3);
                request.setAttribute(FULL_LIST, collectionList);
                request.setAttribute(PARAM_REQUEST, paramRequest);
                request.setAttribute(NUM_RECORDS_TOTAL, total);
                request.setAttribute(COUNT_BY_USER, total);
                init(request);
            }
            request.setAttribute(NUM_RECORDS_TOTAL, total);
            request.setAttribute(COUNT_BY_STAT, collectionList(null).size());
            RequestDispatcher rd = request.getRequestDispatcher(path);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public void doPage(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, java.io.IOException {
        int pagenum = 0;
        String p = request.getParameter("p");
        List<Collection> collectionList = null;
        if (null != p)  pagenum = Integer.parseInt(p);
        if (pagenum<=0) pagenum = 1;
        request.setAttribute(NUM_PAGE_LIST, pagenum);
        request.setAttribute(PAGE_NUM_ROW, NUM_ROW);
        if (Utils.toInt(request.getParameter(COLLECTION_TYPE)) == COLLECTION_TYPE_ALL) {
            collectionList = collectionList(null);
            request.setAttribute(COLLECTION_TYPE, COLLECTION_TYPE_ALL);
        } else {
            collectionList = collectionList(paramRequest.getUser());
            request.setAttribute(COLLECTION_TYPE, COLLECTION_TYPE_OWN);
        }
        setAuthors(paramRequest, collectionList);
        setCovers(paramRequest, collectionList, 3);
        request.setAttribute(FULL_LIST, collectionList);
        
        request.setAttribute(NUM_RECORDS_TOTAL, collectionList.size());
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
    
    public void collectionById(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
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
        if (null != request.getSession().getAttribute("mycollections"))
            collectionList = (List<Collection>)request.getSession().getAttribute("mycollections");
        if (SWBResourceURL.Action_REMOVE.equals(response.getAction())) {
            if (null != user && user.isSigned() && null != request.getParameter(IDENTIFIER)) {
                mgr.deleteCollection(request.getParameter(IDENTIFIER));
                collectionList = collectionList(user);
                Gson gson = new Gson();
                response.setRenderParameter(COLLECTION_RENDER, gson.toJson(new Collection("", false, "")));
                response.setMode(MODE_RES_ADD);
                response.setCallMethod(SWBParamRequest.Call_DIRECT);
                request.getSession().setAttribute("mycollections", collectionList);
            }
        }else if (SWBResourceURL.Action_EDIT.equals(response.getAction())) {
            Collection collection = setCollection(request);
            if (null != user && user.isSigned() && !collection.isEmpty() && null != request.getParameter(IDENTIFIER)) {
                collectionList = collectionList(user);
                if (!exist(collection.getTitle(), request.getParameter(IDENTIFIER))) {
                    for (Collection c : collectionList) {
                        if (c.getId().equals(request.getParameter(IDENTIFIER))) {
                            c.setUserid(user.getId());
                            c.setTitle(collection.getTitle());
                            c.setDescription(collection.getDescription());
                            c.setStatus(collection.getStatus());
                            Gson gson = new Gson();
                            response.setRenderParameter(COLLECTION_RENDER, gson.toJson(c));
                            update(c);
                            break;
                        }
                    }
                }else {
                    Gson gson = new Gson();
                    response.setRenderParameter(COLLECTION_RENDER, gson.toJson(collection));
                }
                response.setMode(MODE_RES_ADD);
                response.setCallMethod(SWBParamRequest.Call_DIRECT);
                request.getSession().setAttribute("mycollections", collectionList);
            }
        }else if (ACTION_STA.equals(response.getAction())) {
            if (null != request.getParameter(IDENTIFIER) && null != request.getSession().getAttribute("mycollections")) {
                //Integer id = Integer.valueOf(request.getParameter(IDENTIFIER));
                collectionList = (List<Collection>)request.getSession().getAttribute("mycollections");
                for (Collection c : collectionList) {
                    if (c.getId().equals(request.getParameter(IDENTIFIER))) {
                        c.setStatus(!c.getStatus());
                        break;
                    }
                }
            }
        }else if (ACTION_DEL_FAV.equals(response.getAction())) {
            if (null != request.getParameter(IDENTIFIER) && null != request.getParameter(ENTRY) /**&& null != request.getSession().getAttribute("mycollections")**/) {
                //Integer id = Integer.valueOf(request.getParameter(IDENTIFIER));
                //collectionList = (List<Collection>)request.getSession().getAttribute("mycollections");
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
            Collection c = setCollection(request);
            if (null != user && user.isSigned() && !c.isEmpty()) {
                //if (!exist(collectionList, c.getTitle(), null)) {
                if (!exist(c.getTitle(), null)) {
                    c.setUserid(user.getId());
                    String id = create(c);
                    if (null != id) c.setId(id);
                    collectionList.add(c);
                    request.getSession().setAttribute("mycollections", collectionList);
                }
                Gson gson = new Gson();
                response.setRenderParameter(COLLECTION_RENDER, gson.toJson(c));
                response.setMode(MODE_RES_ADD);
                response.setCallMethod(SWBParamRequest.Call_DIRECT);
            }
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
        if (elements.isEmpty()) return covers;
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
    
    private List<Collection> collectionList(User user) {
        List<Collection> collection = new ArrayList<>();
        if (null != user && user.isSigned())
            collection = mgr.collections(user.getId());
        else {
            collection = mgr.collectionsByStatus(COLLECTION_PUBLIC);
        }
        return collection;
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
    
    protected void init(HttpServletRequest request) throws SWBResourceException, java.io.IOException {
        int pagenum = 0;
        String p = request.getParameter("p");
        if (null != p) pagenum = Integer.parseInt(p);
        if (pagenum<=0) pagenum = 1;
        request.setAttribute(NUM_PAGE_LIST, pagenum);
        request.setAttribute("PAGE_NUM_ROW", PAGE_NUM_ROW);
        page(pagenum, request);
    }
    
    private void page(int pagenum, HttpServletRequest request) {
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
    }
    
    private String create(Collection c) {
        String id = mgr.insertCollection(c);
        return id;
    }
    
    private long update(Collection c) {
        return mgr.updateCollection(c);
    }
    
    private Long count(String userid) {
        Long count = 0L;
        try {
            count = mgr.countByUser(userid);
        }catch(Exception e) {
            LOG.info(e.getMessage());
        }
        return count; 
    }
    
    private Collection find(String _id) {
        return mgr.findById(_id);
    }
    
    private boolean exist(String title, String _id) {
        return mgr.exist(title, _id);
    }
    
     private Collection setCollection(HttpServletRequest request) {
        Collection collection = new Collection(request.getParameter("title").trim(), Utils.getStatus(request.getParameter("status")), request.getParameter("description").trim());
        return collection;
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
     
    protected void setCovers(SWBParamRequest paramRequest, List<Collection> list,  int size) {
        String baseUri = paramRequest.getWebPage().getWebSite().getModelProperty("search_endPoint");
        if (null == baseUri || baseUri.isEmpty())
            baseUri = SWBPlatform.getEnv("rnc/endpointURL", getResourceBase().getAttribute("url", "http://localhost:8080")).trim();
        for (Collection c : list) {
            c.setCovers(getCovers(paramRequest, c.getElements(), baseUri, size, c));
        }
    }
}
