/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.resources;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
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
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBActionResponse;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;

/**
 *
 * @author rene.jara
 */
public class MyAnnotations extends GenericResource{
    public static final Logger LOG = Logger.getLogger(MyAnnotations.class.getName());
    public static final String ASYNC_ADD = "add";
    public static final String ASYNC_EDIT = "edt";
    public static final String ASYNC_DELETE = "del";
    
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
        String oid=request.getParameter("id");  
        String id="";
        if (oid!=null&&!oid.isEmpty()){
            Entry entry = getEntry(oid);
            if (entry!=null&&entry.getIdentifier()!=null&&entry.getIdentifier().get(0)!=null){                
                id= entry.getIdentifier().get(0).getValue(); 
            }
        }
        User user = paramRequest.getUser();
        response.setContentType("text/html; charset=UTF-8");
       // String basePath = "/work/models/" + paramRequest.getWebPage().getWebSite().getId() + "/jsp/" + this.getClass().getSimpleName() + "/";
        String path = "/swbadmin/jsp/rnc/"+this.getClass().getSimpleName()+"/view.jsp";
        
        List<Annotation> annotationList;
        if(user==null){
            annotationList= AnnotationMgr.getInstance().findByTarget(id,null);
        }else{
            annotationList= AnnotationMgr.getInstance().findByTarget(id,user.getId());
        }    
        
        
        RequestDispatcher dis = request.getRequestDispatcher(path);
        try {
            request.setAttribute("paramRequest", paramRequest);
            request.setAttribute("annotations", annotationList);
            request.setAttribute("id", id);
            dis.include(request, response);
        } catch (ServletException se) {
            LOG.severe(se.getMessage());
        }        
    }
    public void doAdd(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        String target = request.getParameter("id");
        String bodyValue = request.getParameter("bodyValue");
        User user = paramRequest.getUser(); 

        UserRepository ur=paramRequest.getWebPage().getWebSite().getUserRepository();
        String userId = null;
        if (user!=null && user.isSigned()){
            userId = user.getId();
            if (target!=null&& !target.isEmpty()&&
                bodyValue!=null&& !bodyValue.isEmpty()){           
                Annotation annotation =new Annotation(bodyValue,target,user.getId());
                String annotationId=AnnotationMgr.getInstance().addAnnotation(annotation);
            }            
        }
        PrintWriter out = response.getWriter();
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Content-Type", "application/json");
        List<Annotation> annotationList = AnnotationMgr.getInstance().findByTarget(target,userId);
        StringBuilder sb = new StringBuilder("[");
        annotationList.forEach((Annotation a)->{
            sb.append("{\"id\":\"");
            sb.append(a.getId());
            sb.append("\",\"bodyValue\":\"");
            sb.append(a.getBodyValue().replace("\n","\\n"));
            sb.append("\",\"creatorName\":\"");
            String creatorName="";
            if(ur.getUser(a.getCreator())!=null){
                creatorName=ur.getUser(a.getCreator()).getFullName();
            }  
            sb.append(creatorName);
            sb.append("\"},");
        });
        if(',' == sb.charAt(sb.length()-1)){
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append("]");
        out.print(sb.toString());
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
