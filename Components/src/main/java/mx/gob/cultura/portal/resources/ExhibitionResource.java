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

import org.semanticwb.SWBUtils;
import org.semanticwb.SWBPlatform;
import org.semanticwb.SWBException;
import org.semanticwb.model.WebPage;
import org.semanticwb.model.WebSite;
import org.semanticwb.model.Resource;
import org.semanticwb.model.Template;
import org.semanticwb.model.TemplateRef;
import org.semanticwb.model.ResourceType;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBActionResponse;
import org.semanticwb.portal.api.SWBResourceException;

import mx.gob.cultura.portal.utils.EditorTemplate;
/**
 *
 * @author sergio.tellez
 */
public class ExhibitionResource extends GenericResource {
    
    public static final String ACTION_ADD_EXH = "ADD_EXH";
    public static final String ACTION_DEL_EXH = "DEL_EXH";
    
    /** Objeto utilizado para generacion de mensajes en el log. */
    private static final Logger LOGGER = SWBUtils.getLogger(ExhibitionResource.class);
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws IOException {
        String path = "/swbadmin/jsp/rnc/exhibitions/resource.jsp";
        try {
           request.setAttribute("tmpls", editorTemplateList());
	   request.setAttribute("paramRequest", paramRequest);
           RequestDispatcher rd = request.getRequestDispatcher(path);
           rd.include(request, response);
        } catch (ServletException ex) {
            LOGGER.error(ex);
        }
    }
    
    @Override
    public void processAction(HttpServletRequest request, SWBActionResponse response) throws SWBResourceException, IOException {
        WebPage wp = null;
        String url = "/es/repositorio/home";
        try {
            if (ACTION_DEL_EXH.equals(response.getAction())) {
                String idnewwp = request.getParameter("exh_del");
                if (null != idnewwp) {
                    String [] pages = idnewwp.split("/");
                    wp = response.getWebPage().getWebSite().getWebPage(pages[pages.length - 1]);
                    if (null != wp) {
                        wp.setActive(false);
                        url = response.getWebPage().getUrl();
                    }
                }
            }else {
                wp = createWebPage(request, response);
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
        String idTmpl = null != request.getParameter("tpled") ? request.getParameter("tpled") : "21";
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
    
    public static List<EditorTemplate> editorTemplateList() {
        List<EditorTemplate> tmpls = new ArrayList<>();
        tmpls.add(new EditorTemplate("1", "models/repositorio/exhibition/", "plantilla1.html", "Plantilla 1", "/work/models/repositorio/exhibition/mini-plantilla-01.jpg"));
        tmpls.add(new EditorTemplate("2", "models/repositorio/exhibition/", "plantilla2.html", "Plantilla 2", "/work/models/repositorio/exhibition/mini-plantilla-02.jpg"));
        tmpls.add(new EditorTemplate("3", "models/repositorio/exhibition/", "plantilla3.html", "Plantilla 3", "/work/models/repositorio/exhibition/mini-plantilla-03.jpg"));
        return tmpls;
    }
}
