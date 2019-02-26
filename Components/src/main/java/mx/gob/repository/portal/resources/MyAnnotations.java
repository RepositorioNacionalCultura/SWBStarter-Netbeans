/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.repository.portal.resources;

import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.gob.cultura.portal.persist.AnnotationMgr;
import mx.gob.cultura.portal.request.GetBICRequest;
import mx.gob.cultura.portal.response.Annotation;
import mx.gob.cultura.portal.response.Entry;
import org.semanticwb.SWBPlatform;
import org.semanticwb.model.User;
import org.semanticwb.model.UserRepository;
import org.semanticwb.portal.api.GenericAdmResource;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;

/**
 *
 * @author rene.jara
 */
public class MyAnnotations extends GenericAdmResource{
    public static final Logger LOG = Logger.getLogger(MyAnnotations.class.getName());
    public static final String ASYNC_ADD = "add";
    public static final String ASYNC_EDIT = "edt";
    public static final String ASYNC_DELETE = "del";
    
    private UserRepository userRepository;
    
    @Override
    public void init() throws SWBResourceException {
        super.init(); 
        userRepository=getResourceBase().getWebSite().getUserRepository();        
    }
    
    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        String mode = paramRequest.getMode();
        if (ASYNC_ADD.equals(mode)) {
            doAdd(request, response, paramRequest);
        } else {
            super.processRequest(request, response, paramRequest);
        }        
    }

    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        String id="";
        String oid=request.getParameter("id");  
        boolean isAnnotator= false;
        if (null != oid && !oid.isEmpty()) {
            Entry entry = getEntry(oid);
            if (null != entry && null != entry.getIdentifier() && !entry.getIdentifier().isEmpty()) id= entry.getIdentifier().get(0).getValue(); 
        }
        User user = paramRequest.getUser();
        if (null != user  && user.isSigned()) isAnnotator = user.hasRole(userRepository.getRole(this.getResourceBase().getAttribute("AnnRol", "Anotador")));          
        response.setContentType("text/html; charset=UTF-8");
        String site = paramRequest.getWebPage().getWebSite().getId();
        String path = "/work/models/" + site + "/jsp/rnc/" + this.getClass().getSimpleName()+"/view.jsp";
        List<Annotation> annotationList;
        if (user == null) {
            annotationList = AnnotationMgr.getInstance().findByTarget(id, null); //orderby modified direction 1 by default
            //annotationList= AnnotationMgr.getInstance().findByTarget(id,null,"created",0); //orderby created direction 0
        }else {
            annotationList = AnnotationMgr.getInstance().findByTarget(id, user.getId());
        }
        RequestDispatcher dis = request.getRequestDispatcher(path);
        try {
            request.setAttribute("paramRequest", paramRequest);
            request.setAttribute("annotations", annotationList);
            request.setAttribute("id", id);
            request.setAttribute("isAnnotator", isAnnotator);
            dis.include(request, response);
        } catch (ServletException se) {
            LOG.severe(se.getMessage());
        }        
    }
    public void doAdd(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        Annotation annotation = null;
        User user = paramRequest.getUser();
        String target = request.getParameter("id");
        String bodyValue = request.getParameter("bodyValue"); 
        UserRepository ur = paramRequest.getWebPage().getWebSite().getUserRepository();
        if (null != user && user.isSigned()) {
            if (null != target && !target.isEmpty()&& null != bodyValue && !bodyValue.isEmpty()) {           
                annotation = new Annotation(bodyValue,target,user.getId());
                annotation = AnnotationMgr.getInstance().addAnnotation(annotation);
            }            
        }
        PrintWriter out = response.getWriter();
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Content-Type", "application/json");
        if (annotation == null) {
            response.sendError(123, "No se pudo agregar");
        }else{
            //List<Annotation> annotationList = AnnotationMgr.getInstance().findByTarget(target,userId);
            StringBuilder sb = new StringBuilder("{");
            //annotationList.forEach((Annotation a)->{
            sb.append("\"id\":\"");
            sb.append(annotation.getId());
            sb.append("\",\"bodyValue\":\"");
            sb.append(annotation.getBodyValue().replace("\n","\\n"));
            sb.append("\",\"creatorName\":\"");
            String creatorName="";
            if (null != ur.getUser(annotation.getCreator())) creatorName=ur.getUser(annotation.getCreator()).getFullName();
            sb.append(creatorName);
            sb.append("\"}");
            out.print(sb.toString());            
        }    
    }
    
    private Entry getEntry(String id) throws IOException {
        String baseUri = getResourceBase().getWebSite().getModelProperty("search_endPoint");
        if (null == baseUri || baseUri.isEmpty()) {
            baseUri = SWBPlatform.getEnv("rnc/endpointURL").trim();
        }
        String uri = baseUri + "/api/v1/search?identifier="+id;
        GetBICRequest req = new GetBICRequest(uri);
        Entry entry = req.makeRequest();         
        return entry;
    }    
}
