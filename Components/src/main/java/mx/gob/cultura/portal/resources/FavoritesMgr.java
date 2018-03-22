/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.resources;

import com.google.gson.Gson;
import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.gob.cultura.portal.response.Favorite;
import mx.gob.cultura.portal.response.Collection;

import org.semanticwb.model.User;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBActionResponse;
import org.semanticwb.portal.api.SWBResourceException;

/**
 *
 * @author sergio.tellez
 */
public class FavoritesMgr extends GenericResource {
    
    private static final String ENTRY = "entry";
    private static final String IDENTIFIER = "id";
    
    public static final String MODE_RES_ADD = "RES_ADD";
    public static final String MODE_TREE_FAV = "TREE_FAV";

    public static final String ACTION_ADD_FAV = "ADD_FAV";
    private static final Logger LOG = Logger.getLogger(FavoritesMgr.class.getName());
    
    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        String mode = paramRequest.getMode();
        if (MODE_RES_ADD.equals(mode)) {
            redirectJsonResponse(request, response, paramRequest);
        }else
            super.processRequest(request, response, paramRequest);
    }
    
    @Override
    public void processAction(HttpServletRequest request, SWBActionResponse response) throws SWBResourceException, IOException {
        Collection c = null;
        User user = response.getUser();
        request.setCharacterEncoding("UTF-8");
        Favorite fav = new Favorite(null, null);
        List<Collection> collectionList = new ArrayList<>();
        if (null != request.getSession().getAttribute("mycollections"))
            collectionList = (List<Collection>)request.getSession().getAttribute("mycollections");
        if (null != user && user.isSigned() && ACTION_ADD_FAV.equals(response.getAction())) {
            if ((null == request.getParameter(IDENTIFIER) || request.getParameter(IDENTIFIER).isEmpty()) && null != request.getParameter("title")) {
                String title = request.getParameter("title").trim();
                if (!exist(collectionList, title)) {
                    c = new Collection(title, null != request.getParameter("status"), "");
                    c.setUserid(user.getId());
                    c.setId(collectionList.size()+1);
                    collectionList.add(c);
                    request.getSession().setAttribute("mycollections", collectionList);
                }
            }else if (!collectionList.isEmpty()) {
                Integer id = Integer.valueOf(request.getParameter(IDENTIFIER));
                for (Collection cl : collectionList) {
                    if (cl.getId().compareTo(id) == 0) {
                        c = cl;
                        break;
                    }
                }
            }
            if (null != request.getParameter(ENTRY) && null != c) {
                fav = new Favorite(request.getParameter(ENTRY), c.getId());
                if (!c.getElements().contains(fav.getId()))
                    c.getElements().add(fav.getId());
            }
            Gson gson = new Gson();
            response.setRenderParameter("_fav", gson.toJson(fav));
            response.setMode(MODE_RES_ADD);
            response.setCallMethod(SWBParamRequest.Call_DIRECT);
        }else
            super.processAction(request, response);
    }
    
    public void redirectJsonResponse(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws java.io.IOException {
        String json = request.getParameter("_fav");
        response.setContentType("application/json");
	response.setHeader("Cache-Control", "no-cache");
	PrintWriter pw = response.getWriter();
	pw.write(json);
	pw.flush();
	pw.close();
	response.flushBuffer();
    }
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        User user = paramRequest.getUser();
        response.setContentType("text/html; charset=UTF-8");
        List<Collection> collectionList = new ArrayList<>();
        String path = "/swbadmin/jsp/rnc/collections/treecollection.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        if (null != user && user.isSigned() && null != request.getSession().getAttribute("mycollections"))
            collectionList = (List<Collection>)request.getSession().getAttribute("mycollections");
        try {
            request.setAttribute("paramRequest", paramRequest);
            request.setAttribute("mycollections", collectionList);
            request.setAttribute("entry", request.getParameter(IDENTIFIER));
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public static boolean exist(List<Collection> collectionList, String title) {
        if (collectionList.isEmpty()) return false;
        for (Collection c : collectionList) {
            if (c.getTitle().equalsIgnoreCase(title)) return true;
        }
        return false;
    }
}
