/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.semanticwb.Logger;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.gob.cultura.portal.utils.EditorTemplate;

import org.semanticwb.SWBUtils;
import org.semanticwb.SWBPlatform;
import org.semanticwb.SWBException;
import org.semanticwb.model.WebPage;
import org.semanticwb.model.WebSite;
import org.semanticwb.model.Resource;
import org.semanticwb.model.Template;
import org.semanticwb.model.TemplateRef;
import org.semanticwb.model.ResourceType;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBActionResponse;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;

/**
 *
 * @author sergio.tellez
 */
public class ExhibitionResource extends GenericResource {
    
    public static final String ACTION_ADD_EXH = "ADD_EXH";
    
    /** Objeto utilizado para generacion de mensajes en el log. */
    private static final Logger LOGGER = SWBUtils.getLogger(ExhibitionResource.class);
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws IOException {
        String path = "/swbadmin/jsp/rnc/exhibitions/resource.jsp";
        try {
           List<EditorTemplate> tmpls = new ArrayList<>();
           tmpls.add(new EditorTemplate("1", "/work/models/repositorio/exhibition/clasic.html", "Clasic", "/work/models/repositorio/exhibition/clasic.jpg"));
           request.setAttribute("tmpls", tmpls);
	   request.setAttribute("paramRequest", paramRequest);
           RequestDispatcher rd = request.getRequestDispatcher(path);
           rd.include(request, response);
        } catch (ServletException ex) {
            LOGGER.error(ex);
        }
    }
    
    @Override
    public void processAction(HttpServletRequest request, SWBActionResponse response) throws SWBResourceException, IOException {
        String url = "/es/repositorio/home";
        try {
            WebPage wp = createWebPage(request, response);
            if (null != wp) {
                Resource html = createResource(response.getWebPage().getWebSite(), "HTMLCulture", "Editor HTML");
                Resource exhn = createResource(response.getWebPage().getWebSite(), "Exhibition", "Galería");
                if (null != html) {
                    html.setAttribute("template", request.getParameter("id"));
                    wp.addResource(html);
                }
                if (null != exhn) wp.addResource(exhn);
                url = wp.getUrl()+"?act=vEdit";
            }
        } catch (SWBException ex) {
            LOGGER.error(ex);
        }
        response.sendRedirect(url);
    }
    
    private WebPage createWebPage(HttpServletRequest request, SWBActionResponse response) throws SWBException {
        if (null == request.getParameter("title")) return null;
        WebPage wp = null;
        String title = request.getParameter("title");
        String descn = null != request.getParameter("description") ? request.getParameter("description") : "";
        String idTmpl = "21";
        String idnewwp = SWBPlatform.getIDGenerator().getID();
        try {
            wp = response.getWebPage().getWebSite().getWebPage(idnewwp);
            if (wp == null) {
                wp = response.getWebPage().getWebSite().createWebPage(idnewwp);
                wp.setTitle(title);
                wp.setSortName(title);
                wp.setDescription(descn);
                wp.setActive(Boolean.TRUE);
                wp.setParent(response.getWebPage());
                Template templateIndex = Template.ClassMgr.getTemplate(idTmpl, response.getWebPage().getWebSite());// se agrega plantilla para mostrar foro
                TemplateRef temrefindex = TemplateRef.ClassMgr.createTemplateRef(response.getWebPage().getWebSite());
                temrefindex.setActive(Boolean.TRUE);
                temrefindex.setTemplate(templateIndex);
                temrefindex.setInherit(TemplateRef.INHERIT_ACTUAL);
                temrefindex.setPriority(2);
                wp.addTemplateRef(temrefindex);
            }
        }catch (Exception e) {
            LOGGER.error(e);
        }
        return wp;
    }
    
    private Resource createResource(WebSite site, String resourceType, String resourceTitle) {
        Resource res = null;
        try {
            ResourceType resType = ResourceType.ClassMgr.getResourceType(resourceType, site);
            res = site.createResource();
            res.setResourceType(resType);
            res.setTitle(resourceTitle);
            res.setActive(Boolean.TRUE);
            res.updateAttributesToDB();
        }catch (SWBException e) {
            LOGGER.error(e);
        }
        return res;
    }
}
