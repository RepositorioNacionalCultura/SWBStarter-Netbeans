/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.resources;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
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

import org.semanticwb.model.TemplateGroup;
import org.semanticwb.portal.api.SWBResourceURL;
import org.semanticwb.portal.api.SWBResourceModes;
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
           request.setAttribute("tmpls", editorTemplateList(paramRequest.getWebPage().getWebSite()));
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
    
    @Override
    public void doAdmin(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws IOException {
        StringBuilder ret = new StringBuilder();
        try {
            if ("add".equals(paramRequest.getAction()) || "edit".equals(paramRequest.getAction())) {
                SWBResourceURL url=paramRequest.getRenderUrl();
                url.setMode(SWBResourceModes.Mode_ADMIN);
                url.setAction("save");
                ret.append("<form method=\"POST\" action=\"").append(url.toString()).append("\"> \n");
                ret.append("	<div class=swbform> \n");
                ret.append("		<table width=\"100%\"  border=\"0\" cellpadding=\"5\" cellspacing=\"0\"> \n");
                
                ret.append("			<tr> \n");
                ret.append("				<td class=\"datos\">Identificador del grupo de plantillas base: </td> \n");
                ret.append("				<td class=\"valores\"> \n");
                ret.append("					<input type=\"text\" name=\"idGroupTemplate\" value=\"").append(getResourceBase().getAttribute("idGroupTemplate","").trim()).append("\" size=\"40\">");
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
                getResourceBase().setAttribute("idGroupTemplate", request.getParameter("idGroupTemplate"));
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
                ret.append("	<div class=swbform> \n");
                ret.append("		<table width=\"100%\"  border=\"0\" cellpadding=\"5\" cellspacing=\"0\"> \n");
                ret.append("			<tr> \n");
                ret.append("				<td class=\"datos\">Identificador del grupo de plantillas base: </td> \n");
                ret.append("				<td class=\"valores\"> \n");
                ret.append(                                 getResourceBase().getAttribute("idGroupTemplate","").trim());
                ret.append("				</td>");
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
        }catch (SWBException e) { LOGGER.info(e.getMessage());}
        response.getWriter().print(ret.toString());
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
    
    public List<EditorTemplate> editorTemplateList(WebSite site) {
        List<EditorTemplate> tmpls = new ArrayList<>();
        TemplateGroup exhibitions = TemplateGroup.ClassMgr.getTemplateGroup(getResourceBase().getAttribute("idGroupTemplate",""), site);
        Iterator it = exhibitions.listTemplates();
        while (it.hasNext()) {
            Template templateIndex = (Template)it.next();
            if (Objects.equals(templateIndex.isActive(), Boolean.TRUE)) {
                String url = templateIndex.getWorkPath().replaceFirst("/", "")+"/"+templateIndex.getActualVersion().getVersionNumber()+"/";
                EditorTemplate template = new EditorTemplate(templateIndex.getId(), null, null, templateIndex.getTitle(), "/work/models/repositorio/exhibition/mini-plantilla-01.jpg");
                template.setUrl(url);
                if (null != templateIndex.getDescription() && !templateIndex.getDescription().trim().isEmpty() &&
                    (templateIndex.getDescription().contains(".jpg") || templateIndex.getDescription().contains(".gif") || templateIndex.getDescription().contains(".png")))
                    template.setPreview("/work/"+url+"images/"+templateIndex.getDescription());
                template.setFileName(templateIndex.getFileName(templateIndex.getActualVersion().getVersionNumber()));
                tmpls.add(template);
            }
        }
        return tmpls;
    }
}
