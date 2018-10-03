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
import static mx.gob.cultura.portal.utils.Constants.COLLECTION_PRIVATE;

import org.semanticwb.SWBPlatform;
import org.semanticwb.SWBException;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;

import org.semanticwb.model.WebSite;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBResourceModes;
import org.semanticwb.portal.api.SWBResourceURL;

import static mx.gob.cultura.portal.utils.Constants.PARAM_REQUEST;

/**
 *
 * @author sergio.tellez
 */
public class RelevanceMgr extends GenericResource {
    
    private static final Logger LOG = Logger.getLogger(RelevanceMgr.class.getName());
    
    CollectionMgr mgr = CollectionMgr.getInstance();
    
    @Override
    public void doAdmin(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws IOException {
        StringBuilder ret = new StringBuilder();
        try {
            List<Collection> collectionList = mgr.collectionsByStatus(COLLECTION_PRIVATE);
            if ("add".equals(paramRequest.getAction()) || "edit".equals(paramRequest.getAction())) {
                SWBResourceURL url=paramRequest.getRenderUrl();
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
                    if (getResourceBase().getAttribute("collectionID","").equals(c.getId())) ret.append("selected='selected'");
                    ret.append(">").append(c.getTitle()).append("</option>");
                }
                ret.append("</select>");
                ret.append("				</td>");
                ret.append("			</tr> \n");
                ret.append("			<tr> \n");
                ret.append("				<td class=\"datos\">Ruta relativa del JSP: </td> \n");
                ret.append("				<td class=\"valores\"> \n");
                ret.append("					<input type=\"text\" name=\"jspresponse\" value=\"").append(getResourceBase().getAttribute("jspresponse","").trim()).append("\" size=\"50\">");
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
            }else if ("save".equals(paramRequest.getAction())) {
                getResourceBase().setAttribute("collectionID", request.getParameter("collectionID"));
                getResourceBase().setAttribute("jspresponse", request.getParameter("jspresponse"));
                getResourceBase().updateAttributesToDB();
                SWBResourceURL url=paramRequest.getRenderUrl();
                url.setMode(SWBResourceModes.Mode_ADMIN);
                url.setAction("resume");
                response.sendRedirect(url.toString());
            }else if ("resume".equals(paramRequest.getAction())) {
                SWBResourceURL url=paramRequest.getRenderUrl();
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
                    if (getResourceBase().getAttribute("collectionID","").equals(c.getId())) ret.append("selected='selected'");
                    ret.append(">").append(c.getTitle()).append("</option>");
                }
                ret.append("</select>");
                ret.append("                </td>");
                ret.append("		</tr> \n");
                ret.append("		<tr> \n");
                ret.append("				<td class=\"datos\">Ruta relativa del JSP: </td> \n");
                ret.append("				<td class=\"valores\"> \n").append(getResourceBase().getAttribute("jspresponse","").trim()).append("</td>");
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
        }catch (SWBException e) { LOG.info(e.getMessage());}
        response.getWriter().print(ret.toString());
    }
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        String path = "/swbadmin/jsp/rnc/collections/homecollection.jsp";
        if (!this.getResourceBase().getAttribute("jspresponse", "").isEmpty())
            path = this.getResourceBase().getAttribute("jspresponse");
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
        Entry e = null;
        GetBICRequest req = new GetBICRequest(uri);
        try {
            e = req.makeRequest();
        }catch (Exception se) {
            e = null;
            LOG.info(se.getMessage());
        }
        return e;
    }
}
