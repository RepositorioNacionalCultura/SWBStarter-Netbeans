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

import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mx.gob.cultura.portal.response.Entry;
import mx.gob.cultura.portal.response.Collection;
import mx.gob.cultura.portal.request.GetBICRequest;
import org.semanticwb.SWBPlatform;

import org.semanticwb.model.User;
import org.semanticwb.portal.api.SWBResourceURL;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBActionResponse;
import org.semanticwb.portal.api.SWBResourceException;

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
    public static final String MODE_PAGE = "PAGE";
    public static final String ACTION_ADD = "SAVE";
    public static final String MODE_RES_ADD = "RES_ADD";
    public static final String MODE_VIEW_USR = "VIEW_USR";
    public static final String ACTION_DEL_FAV = "DEL_FAV";
    
    //private static final int SEGMENT = 8;
    private static final int PAGE_NUM_ROW = 8;
    private static final int PAGE_JUMP_SIZE = 5;
    private static final String PAGE_LIST = "PAGE_LIST";
    private static final String FULL_LIST = "FULL_LIST";
    private static final String TOTAL_PAGES = "TOTAL_PAGES";
    private static final String NUM_PAGE_JUMP = "NUM_PAGE_JUMP";
    private static final String NUM_PAGE_LIST = "NUM_PAGE_LIST";
    
    private static final Logger LOG = Logger.getLogger(MyCollections.class.getName());
    
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
            redirectJsonResponse(request, response, paramRequest);
        }else
            super.processRequest(request, response, paramRequest);
    }
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        User user = paramRequest.getUser();
        response.setContentType("text/html; charset=UTF-8");
        List<Collection> collectionList = new ArrayList<>();
        String path = "/swbadmin/jsp/rnc/collections/mycollections.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        if (null != user && user.isSigned() && null != request.getSession().getAttribute("mycollections"))
            collectionList = (List<Collection>)request.getSession().getAttribute("mycollections");
        try {
            request.setAttribute(FULL_LIST, collectionList);
            request.setAttribute("paramRequest", paramRequest);
            request.setAttribute("mycollections", collectionList);
            request.setAttribute("NUM_RECORDS_TOTAL", collectionList.size());
            init(request);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public void collectionById(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        User user = paramRequest.getUser();
        Collection collection = null;
        List<String> elements = new ArrayList<>();
        List<Entry> favorites = new ArrayList<>();
        List<Collection> collectionList = new ArrayList<>();
        String path = "/swbadmin/jsp/rnc/collections/elements.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        if (null != user && user.isSigned() && null != request.getSession().getAttribute("mycollections"))
            collectionList = (List<Collection>)request.getSession().getAttribute("mycollections");
        try {
            if (null != request.getParameter(IDENTIFIER) && !collectionList.isEmpty()) {
                Integer id = Integer.valueOf(request.getParameter(IDENTIFIER));
                for (Collection c : collectionList) {
                    if (c.getId().compareTo(id) == 0) {
                        collection = c;
                        elements = c.getElements();
                        break;
                    }
                }
            }
            for (String _id : elements) {
                Entry entry = getEntry(_id);
                if (null != entry) favorites.add(entry);
            }
            request.setAttribute("myelements", favorites);
            request.setAttribute("collection", collection);
            request.setAttribute("paramRequest", paramRequest);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    @Override
    public void doEdit(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        String path = "/swbadmin/jsp/rnc/collections/collection.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        if (null != request.getParameter(IDENTIFIER) && null != request.getSession().getAttribute("mycollections")) {
            Integer id = Integer.valueOf(request.getParameter(IDENTIFIER));
            List<Collection> collectionList = (List<Collection>)request.getSession().getAttribute("mycollections");
            for (Collection c : collectionList) {
                if (c.getId().compareTo(id) == 0) {
                    request.setAttribute("collection", c);
                    break;
                }
            }
        }
        try {
            request.setAttribute("paramRequest", paramRequest);
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
            if (null != user && user.isSigned() && null != request.getParameter(IDENTIFIER) && null != request.getSession().getAttribute("mycollections")) {
                Integer id = Integer.valueOf(request.getParameter(IDENTIFIER));
                collectionList = (List<Collection>)request.getSession().getAttribute("mycollections");
                Collection tmp = null;
                for (Collection c : collectionList) {
                    if (c.getId().compareTo(id) == 0) {
                        tmp = c;
                        break;
                    }
                }
                if (null != tmp) collectionList.remove(tmp);
            }
        }else if (SWBResourceURL.Action_EDIT.equals(response.getAction())) {
            if (null != user && user.isSigned() && null != request.getParameter("title") && null != request.getSession().getAttribute("mycollections")) {
                String title = request.getParameter("title").trim();
                String description = null != request.getParameter("description") ? request.getParameter("description").trim() : "";
                Integer id = Integer.valueOf(request.getParameter(IDENTIFIER));
                collectionList = (List<Collection>)request.getSession().getAttribute("mycollections");
                if (!exist(collectionList, title, id)) {
                    for (Collection c : collectionList) {
                        if (c.getId().compareTo(id) == 0) {
                            c.setTitle(title);
                            c.setDescription(description);
                            c.setStatus(null != request.getParameter("status"));
                            Gson gson = new Gson();
                            response.setRenderParameter("_collection", gson.toJson(c));
                            break;
                        }
                    }
                }else {
                    Gson gson = new Gson();
                    response.setRenderParameter("_collection", gson.toJson(new Collection(null, null != request.getParameter("status"), null)));
                }
                response.setMode(MODE_RES_ADD);
                response.setCallMethod(SWBParamRequest.Call_DIRECT);
                request.getSession().setAttribute("mycollections", collectionList);
            }
        }else if (ACTION_STA.equals(response.getAction())) {
            if (null != request.getParameter(IDENTIFIER) && null != request.getSession().getAttribute("mycollections")) {
                Integer id = Integer.valueOf(request.getParameter(IDENTIFIER));
                collectionList = (List<Collection>)request.getSession().getAttribute("mycollections");
                for (Collection c : collectionList) {
                    if (c.getId().compareTo(id) == 0) {
                        c.setStatus(!c.getStatus());
                        break;
                    }
                }
            }
        }else if (ACTION_DEL_FAV.equals(response.getAction())) {
            if (null != request.getParameter(IDENTIFIER) && null != request.getParameter(ENTRY) && null != request.getSession().getAttribute("mycollections")) {
                Integer id = Integer.valueOf(request.getParameter(IDENTIFIER));
                collectionList = (List<Collection>)request.getSession().getAttribute("mycollections");
                for (Collection c : collectionList) {
                    if (c.getId().compareTo(id) == 0 && !c.getElements().isEmpty() && !request.getParameter(ENTRY).trim().isEmpty() && c.getElements().contains(request.getParameter(ENTRY))) {
                        c.getElements().remove(request.getParameter(ENTRY));
                        break;
                    }
                }
            }
        }else if (ACTION_ADD.equals(response.getAction())) {
            if (null != user && user.isSigned() && null != request.getParameter("title")) {
                String title = request.getParameter("title").trim();
                Collection c = new Collection(null, null != request.getParameter("status"), null);
                if (!exist(collectionList, title, 0)) {
                    String description = null != request.getParameter("description") ? request.getParameter("description").trim() : "";
                    c = new Collection(title, null != request.getParameter("status"), description);
                    c.setUserid(user.getId());
                    c.setId(collectionList.size()+1);
                    collectionList.add(c);
                    request.getSession().setAttribute("mycollections", collectionList);
                }
                Gson gson = new Gson();
                response.setRenderParameter("_collection", gson.toJson(c));
                response.setMode(MODE_RES_ADD);
                response.setCallMethod(SWBParamRequest.Call_DIRECT);
            }
        }else
            super.processAction(request, response);
    }
    
    public void redirectJsonResponse(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws java.io.IOException {
        String json = request.getParameter("_collection");
        response.setContentType("application/json");
	response.setHeader("Cache-Control", "no-cache");
	PrintWriter pw = response.getWriter();
	pw.write(json);
	pw.flush();
	pw.close();
	response.flushBuffer();
    }
    
    private boolean exist(List<Collection> collectionList, String title, int id) {
        if (collectionList.isEmpty()) return false;
        for (Collection c : collectionList) {
            if (c.getTitle().equalsIgnoreCase(title)) {
                if (id == 0) return true;
                else if (c.getId() != id) return true;
            }
        }
        return false;
    }
    
    public void doPage(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, java.io.IOException {
        int pagenum = 0;
        String p = request.getParameter("p");
        if (null != p)
            pagenum = Integer.parseInt(p);
        if (pagenum<=0) pagenum = 1;
        request.setAttribute(NUM_PAGE_LIST, pagenum);
        request.setAttribute("PAGE_NUM_ROW", PAGE_NUM_ROW);
        page(pagenum, request);
        String url = "/swbadmin/jsp/rnc/collections/rows.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(url);
        try {
            request.setAttribute("paramRequest", paramRequest);
            rd.include(request, response);
        }catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public void doAdd(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws java.io.IOException {
        String path = "/swbadmin/jsp/rnc/collections/collection.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            request.setAttribute("paramRequest", paramRequest);
            request.setAttribute("collection", new Collection("", false, ""));
            rd.include(request, response);
        }catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    private Entry getEntry(String _id) {
        String uri = SWBPlatform.getEnv("rnc/endpointURL",getResourceBase().getAttribute("endpointURL","http://localhost:8080")).trim() + "/api/v1/search?identifier=";
        uri += _id;
        GetBICRequest req = new GetBICRequest(uri);
        Entry entry = req.makeRequest();
        return entry;
    }
    
    private void init(HttpServletRequest request) throws SWBResourceException, java.io.IOException {
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
            request.setAttribute(TOTAL_PAGES, totalPages);
            Integer currentLeap = (pagenum-1)/PAGE_JUMP_SIZE;
            request.setAttribute(NUM_PAGE_JUMP, currentLeap);
            request.setAttribute("PAGE_JUMP_SIZE", PAGE_JUMP_SIZE);
            ArrayList rowsPage = getRows(pagenum, rows);
            request.setAttribute(PAGE_LIST, rowsPage);
            request.setAttribute("NUM_RECORDS_TOTAL", rows.size());
            request.setAttribute("NUM_RECORDS_VISIBLE", rowsPage.size());
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
            if (i+1 < rows.size() && ((i+1) % PAGE_NUM_ROW) == 0) {
                pageCount++;
                pageRows = new ArrayList();
                pagesRows.put(pageCount, pageRows);
            }
        }
        ArrayList rowsPage = pagesRows.get(page);
        if (rowsPage==null) rowsPage = new ArrayList();
        return rowsPage;
    }
}
