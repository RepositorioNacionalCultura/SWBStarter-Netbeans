package mx.gob.cultura.portal.resources;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mx.gob.cultura.portal.response.Entry;
import mx.gob.cultura.portal.response.Collection;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION_PUBLIC;

import static mx.gob.cultura.portal.utils.Constants.FULL_LIST;
import static mx.gob.cultura.portal.utils.Constants.NUM_PAGE_LIST;
import static mx.gob.cultura.portal.utils.Constants.PARAM_REQUEST;
import static mx.gob.cultura.portal.utils.Constants.NUM_RECORDS_TOTAL;
import static mx.gob.cultura.portal.utils.Constants.NUM_ROW;
import static mx.gob.cultura.portal.utils.Constants.PAGE_NUM_ROW;

import org.semanticwb.model.User;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;

/**
 *
 * @author sergio.tellez
 */
public class InCollections extends MyCollections {
    
    public static final String MODE_VIEW_PBC = "VIEW_PBC";
    private static final Logger LOG = Logger.getLogger(InCollections.class.getName());
    
    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        String mode = paramRequest.getMode();
        if (MODE_VIEW_USR.equals(mode)) {
            collectionById(request, response, paramRequest);
        }else
            super.processRequest(request, response, paramRequest);
    }
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        String siteid = paramRequest.getWebPage().getWebSite().getId();
        String path = "/work/models/"+siteid+"/jsp/rnc/collections/pbcollections.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            Integer total = count(siteid, null, COLLECTION_PUBLIC).intValue();
            List<Collection> collectionList = collectionList(siteid, null, 1, NUM_ROW);
            setCovers(paramRequest, collectionList, 3);
            request.setAttribute(FULL_LIST, collectionList);
            request.setAttribute(PARAM_REQUEST, paramRequest);
            request.setAttribute(NUM_RECORDS_TOTAL, total);
            init(request);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    @Override
    protected List<Collection> collectionList(String siteid, User user, Integer from, Integer leap) {
        List<Collection> collection = new ArrayList<>();
        if (null != user && user.isSigned())
            collection = mgr.collectionsByUserLimit(siteid, user.getId(), from, leap);
        else {
            collection = mgr.collectionsByStatusLimit(siteid, COLLECTION_PUBLIC, from, leap);
        }
        return collection;
    }
    
    @Override
    public void doPage(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, java.io.IOException {
        int pagenum = 0;
        Integer total = 0;
        String p = request.getParameter("p");
        List<Collection> collectionList = null;
        if (null != p)  pagenum = Integer.parseInt(p);
        if (pagenum<=0) pagenum = 1;
        request.setAttribute(NUM_PAGE_LIST, pagenum);
        request.setAttribute(PAGE_NUM_ROW, NUM_ROW);
        String siteid = paramRequest.getWebPage().getWebSite().getId();
        total = count(siteid, null, COLLECTION_PUBLIC).intValue();
        collectionList = collectionList(siteid, null, pagenum, NUM_ROW);
        setCovers(paramRequest, collectionList, 3);
        request.setAttribute(FULL_LIST, collectionList);
        request.setAttribute(NUM_RECORDS_TOTAL, total);
        page(pagenum, request);
        String url = "/work/models/"+siteid+"/jsp/rnc/collections/rows.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(url);
        try {
            request.setAttribute(PARAM_REQUEST, paramRequest);
            rd.include(request, response);
        }catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    @Override
    public void collectionById(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        Collection collection = null;
        List<Entry> favorites = new ArrayList<>();
        //String path = "/swbadmin/jsp/rnc/collections/elements.jsp";
        String path = "/work/models/"+paramRequest.getWebPage().getWebSite().getId()+"/jsp/rnc/collections/elements.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            if (null != request.getParameter(IDENTIFIER))
                collection = mgr.findById(request.getParameter(IDENTIFIER));
            if (null != collection && null != collection.getElements()) {
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
    
    /**private List<Collection> collectionList(Boolean status) {
        List<Collection> collection = new ArrayList<>();
        try {
            collection = mgr.collectionsByStatus(status);
        }catch (Exception e) {LOG.info(e.getMessage());}
        return collection;
    }**/
}