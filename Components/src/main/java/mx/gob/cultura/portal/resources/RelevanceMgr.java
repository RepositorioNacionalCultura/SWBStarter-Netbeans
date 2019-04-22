/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.resources;

import java.util.List;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
import static mx.gob.cultura.portal.utils.Constants.COUNT_BY_STAT;
import static mx.gob.cultura.portal.utils.Constants.COUNT_BY_USER;
import static mx.gob.cultura.portal.utils.Constants.FULL_LIST;
import static mx.gob.cultura.portal.utils.Constants.NUM_PAGE_JUMP;
import static mx.gob.cultura.portal.utils.Constants.NUM_PAGE_LIST;
import static mx.gob.cultura.portal.utils.Constants.NUM_RECORDS_TOTAL;
import static mx.gob.cultura.portal.utils.Constants.NUM_RECORDS_VISIBLE;
import static mx.gob.cultura.portal.utils.Constants.NUM_ROW;
import static mx.gob.cultura.portal.utils.Constants.PAGE_LIST;
import static mx.gob.cultura.portal.utils.Constants.PAGE_NUM_ROW;
import static mx.gob.cultura.portal.utils.Constants.PAGE_JUMP_SIZE;
import static mx.gob.cultura.portal.utils.Constants.PARAM_REQUEST;
import static mx.gob.cultura.portal.utils.Constants.STR_JUMP_SIZE;
import static mx.gob.cultura.portal.utils.Constants.TOTAL_PAGES;
import static mx.gob.cultura.portal.resources.MyCollections.IDENTIFIER;
import static mx.gob.cultura.portal.resources.MyCollections.MODE_VIEW_USR;

import org.semanticwb.SWBPlatform;
import org.semanticwb.SWBException;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;

import org.semanticwb.model.WebSite;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBResourceModes;
import org.semanticwb.portal.api.SWBResourceURL;

/**
 *
 * @author sergio.tellez
 */
public class RelevanceMgr extends GenericResource {

    private static final Logger LOG = Logger.getLogger(RelevanceMgr.class.getName());

    CollectionMgr mgr = CollectionMgr.getInstance();

    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        String mode = paramRequest.getMode();
        if (MODE_VIEW_USR.equals(mode)) {
            collectionById(request, response, paramRequest);
        } else {
            super.processRequest(request, response, paramRequest);
        }
    }

    @Override
    public void doAdmin(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws IOException {
        StringBuilder ret = new StringBuilder();
        try {
            //String siteid = paramRequest.getWebPage().getWebSiteId();
            List<Collection> collectionList = mgr.collectionsByStatus(null, COLLECTION_PRIVATE);
            if ("add".equals(paramRequest.getAction()) || "edit".equals(paramRequest.getAction())) {
                SWBResourceURL url = paramRequest.getRenderUrl();
                url.setMode(SWBResourceModes.Mode_ADMIN);
                url.setAction("save");
                ret.append("<form method=\"POST\" action=\"").append(url.toString()).append("\"> \n");
                ret.append("	<div class=swbform> \n");
                ret.append("		<table width=\"100%\"  border=\"0\" cellpadding=\"5\" cellspacing=\"0\"> \n");
                ret.append("			<tr> \n");
                ret.append("				<td class=\"datos\">*Colección: </td> \n");
                ret.append("				<td class=\"valores\"> \n");
                //ret.append("				<input type=\"text\" name=\"collectionID\" value=\"").append(getResourceBase().getAttribute("collectionID","").trim()).append("\" size=\"50\">");
                ret.append("<select name=\"collectionID\" id=\"collectionID\">");
                for (Collection c : collectionList) {
                    ret.append("<option value=\"").append(c.getId()).append("\"");
                    if (getResourceBase().getAttribute("collectionID", "").equals(c.getId())) {
                        ret.append("selected='selected'");
                    }
                    ret.append(">").append(c.getTitle()).append("</option>");
                }
                ret.append("</select>");
                ret.append("				</td>");
                ret.append("			</tr> \n");
                ret.append("			<tr> \n");
                ret.append("				<td class=\"datos\">Id del Usuario para mostrar sus colecciones: </td> \n");
                ret.append("				<td class=\"valores\"> \n");
                ret.append("					<input type=\"text\" name=\"userid\" value=\"").append(getResourceBase().getAttribute("userid", "").trim()).append("\" size=\"50\">");
                ret.append("				</td>");
                ret.append("			</tr> \n");
                ret.append("			<tr> \n");
                ret.append("				<td class=\"datos\">Ruta relativa del JSP: </td> \n");
                ret.append("				<td class=\"valores\"> \n");
                ret.append("					<input type=\"text\" name=\"jspresponse\" value=\"").append(getResourceBase().getAttribute("jspresponse", "").trim()).append("\" size=\"50\">");
                ret.append("				</td>");
                ret.append("			</tr> \n");
                ret.append("			<tr> \n");
                ret.append("				<td colspan=2 align=right> \n");
                ret.append("					<br><hr size=1 noshade> \n");
                ret.append("					<input type=submit name=btnSave value=\"Enviar\" class=boton> \n");
                ret.append("				</td> \n");
                ret.append("			</tr> \n");
                ret.append("		</table> \n");
                ret.append("	</div> \n");
                ret.append("</form> \n");
            } else if ("save".equals(paramRequest.getAction())) {
                getResourceBase().setAttribute("collectionID", request.getParameter("collectionID"));
                getResourceBase().setAttribute("userid", request.getParameter("userid"));
                getResourceBase().setAttribute("jspresponse", request.getParameter("jspresponse"));
                getResourceBase().updateAttributesToDB();
                SWBResourceURL url = paramRequest.getRenderUrl();
                url.setMode(SWBResourceModes.Mode_ADMIN);
                url.setAction("resume");
                response.sendRedirect(url.toString());
            } else if ("resume".equals(paramRequest.getAction())) {
                SWBResourceURL url = paramRequest.getRenderUrl();
                url.setMode(SWBResourceModes.Mode_ADMIN);
                url.setAction("edit");
                ret.append("<form method=\"POST\" action=\"").append(url.toString()).append("\"> \n");
                ret.append("    <div class=swbform> \n");
                ret.append("        <table width=\"100%\"  border=\"0\" cellpadding=\"5\" cellspacing=\"0\"> \n");
                ret.append("            <tr> \n");
                ret.append("                <td class=\"datos\">*Colección: </td> \n");
                ret.append("                <td class=\"valores\"> \n");
                //ret.append("              <input type=\"text\" name=\"collectionID\" value=\"").append(getResourceBase().getAttribute("collectionID","").trim()).append("\" size=\"50\">");
                ret.append("<select name=\"collectionID\" id=\"collectionID\" disabled=\"disabled\">");
                for (Collection c : collectionList) {
                    ret.append("<option value=\"").append(c.getId()).append("\"");
                    if (getResourceBase().getAttribute("collectionID", "").equals(c.getId())) {
                        ret.append("selected='selected'");
                    }
                    ret.append(">").append(c.getTitle()).append("</option>");
                }
                ret.append("</select>");
                ret.append("                </td>");
                ret.append("		</tr> \n");
                ret.append("			<tr> \n");
                ret.append("				<td class=\"datos\">Id del Usuario para mostrar sus colecciones: </td> \n");
                ret.append("				<td class=\"valores\"> \n");
                ret.append("					<input type=\"text\" name=\"userid\" value=\"").append(getResourceBase().getAttribute("userid", "").trim()).append("\" size=\"50\" disabled=\"disabled\">");
                ret.append("				</td>");
                ret.append("			</tr> \n");
                ret.append("		<tr> \n");
                ret.append("				<td class=\"datos\">Ruta relativa del JSP: </td> \n");
                ret.append("				<td class=\"valores\"> \n").append(getResourceBase().getAttribute("jspresponse", "").trim()).append("</td>");
                ret.append("			</tr> \n");
                ret.append("			<tr> \n");
                ret.append("				<td colspan=2 align=right> \n");
                ret.append("					<br><hr size=1 noshade> \n");
                ret.append("					<input type=submit name=btnSave value=\"Regresar\" class=boton> \n");
                ret.append("				</td> \n");
                ret.append("			</tr> \n");
                ret.append("		</table> \n");
                ret.append("	</div> \n");
                ret.append("</form> \n");
            }
        } catch (SWBException e) {
            LOG.info(e.getMessage());
        }
        response.getWriter().print(ret.toString());
    }

    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        Integer total = 0;
        response.setContentType("text/html; charset=UTF-8");
        //String siteid = paramRequest.getWebPage().getWebSite().getId();
        //String path = "/work/models/"+siteid+"/jsp/rnc/collections/homecollection.jsp";
        String path = "/swbadmin/jsp/rnc/collections/homecollection.jsp";
        if (!this.getResourceBase().getAttribute("userid", "").isEmpty()) {
            path = "/swbadmin/jsp/rnc/collections/listcollections.jsp";
            //path = "/work/models/"+paramRequest.getWebPage().getWebSite().getId()+"/jsp/rnc/collections/listcollections.jsp";
        }
        if (!this.getResourceBase().getAttribute("jspresponse", "").isEmpty()) {
            path = this.getResourceBase().getAttribute("jspresponse");
        }
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            int count = count(null, paramRequest.getUser().getId()).intValue();
            List<Entry> collectionList = null;
            List<Collection> collections = null;
            if (!this.getResourceBase().getAttribute("userid", "").isEmpty()) {
                collections = collectionByUserId(this.getResourceBase().getAttribute("userid", ""), paramRequest.getWebPage().getWebSite());
                setCovers(paramRequest, collections, 3);
                request.setAttribute(FULL_LIST, collections);
                request.setAttribute(PARAM_REQUEST, paramRequest);
                request.setAttribute("mycollections", collections);
                total = collections.size();
                request.setAttribute(NUM_RECORDS_TOTAL, total);
                request.setAttribute(COUNT_BY_STAT, total);
                request.setAttribute(COUNT_BY_USER, count);
                init(request);
                //request.setAttribute("collections", collections);
            } else {
                collectionList = collectionById(this.getResourceBase().getAttribute("collectionID", ""), paramRequest.getWebPage().getWebSite());
                request.setAttribute("relevants", collectionList);
            }
            request.setAttribute(PARAM_REQUEST, paramRequest);

            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }

    protected void init(HttpServletRequest request) throws SWBResourceException, java.io.IOException {
        int pagenum = 0;
        String p = request.getParameter("p");
        if (null != p) {
            pagenum = Integer.parseInt(p);
        }
        if (pagenum <= 0) {
            pagenum = 1;
        }
        request.setAttribute(NUM_PAGE_LIST, pagenum);
        request.setAttribute("PAGE_NUM_ROW", PAGE_NUM_ROW);
        page(pagenum, request);
    }

    protected void setCovers(SWBParamRequest paramRequest, List<Collection> list, int size) {
        String baseUri = paramRequest.getWebPage().getWebSite().getModelProperty("search_endPoint");
        if (null == baseUri || baseUri.isEmpty()) {
            baseUri = SWBPlatform.getEnv("rnc/endpointURL", getResourceBase().getAttribute("url", "http://localhost:8080")).trim();
        }
        for (Collection c : list) {
            c.setCovers(getCovers(paramRequest, c.getElements(), baseUri, size,c));
        }
    }

    private void page(int pagenum, HttpServletRequest request) {
        List<?> rows = (List<?>) request.getAttribute(FULL_LIST);
        Integer total = (Integer) request.getAttribute("NUM_RECORDS_TOTAL");
        if (null == rows) {
            rows = new ArrayList();
        }
        try {
            Integer totalPages = total / NUM_ROW;
            if (total % NUM_ROW != 0) {
                totalPages++;
            }
            request.setAttribute(TOTAL_PAGES, totalPages);
            Integer currentLeap = (pagenum - 1) / PAGE_JUMP_SIZE;
            request.setAttribute(NUM_PAGE_JUMP, currentLeap);
            request.setAttribute(STR_JUMP_SIZE, PAGE_JUMP_SIZE);
            ArrayList rowsPage = getRows(pagenum, rows);
            request.setAttribute(PAGE_LIST, rowsPage);
            request.setAttribute(NUM_RECORDS_TOTAL, total);
            request.setAttribute(NUM_RECORDS_VISIBLE, rowsPage.size());
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }
    }

    private ArrayList getRows(int page, List<?> rows) {
        int pageCount = 1;
        if (rows == null || rows.isEmpty()) {
            return new ArrayList();
        }
        Map<Integer, ArrayList<?>> pagesRows = new HashMap<>();
        ArrayList pageRows = new ArrayList();
        pagesRows.put(pageCount, pageRows);
        for (int i = 0; i < rows.size(); i++) {
            pageRows.add(rows.get(i));
            if (i + 1 < rows.size() && ((i + 1) % NUM_ROW) == 0) {
                pageCount++;
                pageRows = new ArrayList();
                pagesRows.put(pageCount, pageRows);
            }
        }
        ArrayList rowsPage = pagesRows.get(page);
        if (rowsPage == null) {
            rowsPage = new ArrayList();
        }
        return rowsPage;
    }

    private Long count(String siteid, String userid) {
        Long count = 0L;
        try {
            count = mgr.countByUser(siteid, userid);
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }
        return count;
    }

    private List<Collection> collectionByUserId(String id, WebSite site) throws SWBResourceException, IOException {
        @SuppressWarnings("UnusedAssignment")
        List<Collection> collections = null;
        if (id.isEmpty()) {
            return collections;
        }
        String base = site.getModelProperty("search_endPoint");
        if (null == base || base.isEmpty()) {
            base = SWBPlatform.getEnv("rnc/endpointURL", getResourceBase().getAttribute("endpointURL", "http://localhost:8080")).trim();
        }
        base += "/api/v1/search?identifier=";
        try {
            collections = mgr.collections(null, id);
        } catch (Exception se) {
            LOG.info(se.getMessage());
        }
        return collections;
    }

    private List<Entry> collectionById(String id, WebSite site) throws SWBResourceException, IOException {
        @SuppressWarnings("UnusedAssignment")
        Collection collection = null;
        List<Entry> favorites = new ArrayList<>();
        if (id.isEmpty()) {
            return favorites;
        }
        String base = site.getModelProperty("search_endPoint");
        if (null == base || base.isEmpty()) {
            base = SWBPlatform.getEnv("rnc/endpointURL", getResourceBase().getAttribute("endpointURL", "http://localhost:8080")).trim();
        }
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
        Entry e = null;
        GetBICRequest req = new GetBICRequest(uri);
        try {
            e = req.makeRequest();
            
        } catch (Exception se) {
            e = null;
            LOG.info(se.getMessage());
        }
        return e;
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

    public void collectionById(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {

        //User user = paramRequest.getUser();
        Collection collection = null;
        List<Entry> favorites = new ArrayList<>();
        String path = "/swbadmin/jsp/rnc/collections/pagecollection.jsp"; //elements.jsp
        //String path = "/work/models/"+paramRequest.getWebPage().getWebSite().getId()+"/jsp/rnc/collections/pagecollection.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            if (null != request.getParameter(IDENTIFIER) /**
                     * && !collectionList.isEmpty()*
                     */
                    ) { //null != user && user.isSigned() &&
                collection = mgr.findById(request.getParameter(IDENTIFIER));
            }
            if (null != collection && null != collection.getElements()) {
                for (String _id : collection.getElements()) {
                    Entry entry = getEntry(paramRequest, _id);
                    if (null != entry) {
                        favorites.add(entry);
                        SearchCulturalProperty.setThumbnail(entry, paramRequest.getWebPage().getWebSite(), 0);
                    }
                }
            }
            request.setAttribute("relevants", favorites);
            request.setAttribute(COLLECTION, collection);
            request.setAttribute(PARAM_REQUEST, paramRequest);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }

    private List<String> getCovers(SWBParamRequest paramRequest, List<String> elements, String baseUri, int size, Collection c) {
        Entry entry = null;
        List<String> covers = new ArrayList<>();
        if (elements.isEmpty()) {
            return covers;
        }
        Iterator it = elements.iterator();
        while (it.hasNext()) {
            String itnext = (String)it.next();
            String uri = baseUri + "/api/v1/search?identifier=" + itnext;
            GetBICRequest req = new GetBICRequest(uri);
            try {
                entry = req.makeRequest();
            } catch (Exception e) {
                entry = null;
                removeDeletedBicFromCollection(c,itnext);
                LOG.info(e.getMessage());
            }
            if (null != entry && null != entry.getResourcethumbnail() && entry.getResourcethumbnail().trim().length() > 0) {
                covers.add(entry.getResourcethumbnail());
            } else if(null != entry){
                SearchCulturalProperty.setThumbnail(entry, paramRequest.getWebPage().getWebSite(), size);
                covers.add(entry.getResourcethumbnail());
            }
            if (covers.size() >= size) {
                break;
            }
        }
        return covers;
    }
    
    private void removeDeletedBicFromCollection(Collection c, String bicId){
        c.getElements().remove(bicId);
        mgr.updateCollection(c);
    }
}
