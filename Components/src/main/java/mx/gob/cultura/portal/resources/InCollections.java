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
import static mx.gob.cultura.portal.utils.Constants.PARAM_REQUEST;
import static mx.gob.cultura.portal.utils.Constants.NUM_RECORDS_TOTAL;

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
        String path = "/swbadmin/jsp/rnc/collections/pbcollections.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            List<Collection> collectionList = collectionList(COLLECTION_PUBLIC);
            setCovers(paramRequest, collectionList, 3);
            request.setAttribute(FULL_LIST, collectionList);
            request.setAttribute(PARAM_REQUEST, paramRequest);
            request.setAttribute(NUM_RECORDS_TOTAL, collectionList.size());
            init(request);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    @Override
    public void collectionById(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        Collection collection = null;
        List<Entry> favorites = new ArrayList<>();
        String path = "/swbadmin/jsp/rnc/collections/elements.jsp";
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
    
    private List<Collection> collectionList(Boolean status) {
        List<Collection> collection = new ArrayList<>();
        try {
            collection = mgr.collectionsByStatus(status);
        }catch (Exception e) {LOG.info(e.getMessage());}
        return collection;
    }
}