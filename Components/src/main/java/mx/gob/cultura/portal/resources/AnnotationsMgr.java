/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.resources;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.gob.cultura.portal.persist.AnnotationMgr;
import mx.gob.cultura.portal.request.GetBICRequest;
import mx.gob.cultura.portal.request.ListBICRequest;
import mx.gob.cultura.portal.response.Annotation;
import mx.gob.cultura.portal.response.Document;
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
public class AnnotationsMgr extends GenericAdmResource{
    public static final Logger LOG = Logger.getLogger(AnnotationsMgr.class.getName());
    
    public static final String ASYNC_ACCEPT = "acp";
    public static final String ASYNC_REJECT = "rjc";
    public static final String ASYNC_MODIFY = "edt";
    public static final String ASYNC_DELETE = "del";    
    public static final String ASYNC_LIST = "list"; 
    public static final String ORDER_DATE = "date"; 
    public static final String FILTER_ACCEPTED = "acp"; 
    public static final String FILTER_REJECTED = "rjc"; 
    public static final int RECORDS_PER_PAGE= 4; 

    private UserRepository userRepository;
    public static final String DATE_PATTERN="yyyy-MM-dd";
    
    @Override
    public void init() throws SWBResourceException {
        super.init(); 
        userRepository=getResourceBase().getWebSite().getUserRepository();        
    }

    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        String mode = paramRequest.getMode();        
        switch (mode) {
            /*case ASYNC_LIST:
                doList(request, response, paramRequest);
                break;*/
            case ASYNC_ACCEPT:
                doAccept(request, response, paramRequest);
                break;
            case ASYNC_REJECT:
                doReject(request, response, paramRequest);
                break;
            case ASYNC_MODIFY:
                doModify(request, response, paramRequest);
                break;
            case ASYNC_DELETE:
                doDelete(request, response, paramRequest);
                break;
            default:             
                super.processRequest(request, response, paramRequest);
                break;
        }                     
    }    


    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        User user = paramRequest.getUser();
        boolean isAdmin = false;
        int currentPage = 1;
        int totalPages = 1;
        String order = "";
        String filter = "target";
        String o = request.getParameter("o");
        //String f = request.getParameter("f");
        try{
            currentPage=Integer.parseInt(request.getParameter("p"));
        }catch (NumberFormatException ignore){        
        }
        if (o!=null){
            switch(o){
                case ORDER_DATE:
                    order="modified";
                    break;
                default:
                    order="target";
            }   
        }
        
        response.setContentType("text/html; charset=UTF-8");
        // String basePath = "/work/models/" + paramRequest.getWebPage().getWebSite().getId() + "/jsp/" + this.getClass().getSimpleName() + "/";
        String path = "/swbadmin/jsp/rnc/"+this.getClass().getSimpleName()+"/view.jsp";
        
        List<Annotation> annotationList = new ArrayList<>();
        if (user.isSigned()){
            isAdmin=user.hasRole(userRepository.getRole(this.getResourceBase().getAttribute("AdmRol", "")));
            if(isAdmin){                
                annotationList= AnnotationMgr.getInstance().findByPaged(null,null,RECORDS_PER_PAGE, currentPage,order,1);
                totalPages = AnnotationMgr.getInstance().countPages(null, null,RECORDS_PER_PAGE);
            }else{            
                annotationList= AnnotationMgr.getInstance().findByPaged(null,user.getId(),RECORDS_PER_PAGE, currentPage,order,1);
                totalPages = AnnotationMgr.getInstance().countPages(null, user.getId(),RECORDS_PER_PAGE);
            }            
        }    
       
        RequestDispatcher dis = request.getRequestDispatcher(path);
        try {
            request.setAttribute("paramRequest", paramRequest);
            request.setAttribute("annotations", annotationsToList(annotationList,user.getId()));
            request.setAttribute("isAdmin", isAdmin);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("order", order);
            request.setAttribute("filter", filter);
            dis.include(request, response);
        } catch (ServletException se) {
            LOG.severe(se.getMessage());
        }        
    }

    /*public void doList(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException{
        
System.out.println("********************doList");       
        int currentPage = 0;
        
        try {
            currentPage=Integer.parseInt(request.getParameter("cp"));
        } catch (Exception ignore) {
        }
        
System.out.println("currentPage:"+currentPage);
        boolean isAdmin = false;
        User user = paramRequest.getUser(); 
        
System.out.println("user:"+user);  
        
        List<Annotation> annotationList = new ArrayList<>();
        if (user.isSigned()){
            isAdmin=user.hasRole(userRepository.getRole(this.getResourceBase().getAttribute("AdmRol", "")));
            if(isAdmin){
                annotationList= AnnotationMgr.getInstance().findByPaged(null,null,RECORDS_PER_PAGE, currentPage ,"target",1);
            }else{            
                annotationList= AnnotationMgr.getInstance().findByPaged(null,user.getId(),RECORDS_PER_PAGE, currentPage,"target",1);
            }            
        }  

        PrintWriter out = response.getWriter();
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Content-Type", "application/json");
        //String str = annotationsToJson(annotationList, ur).toString();
        //String str = annotationsToJsonString(annotationList,user.getId());
//System.out.println(str);        
//        out.print(str);
            
    }*/
    
    public void doAccept(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException{
        String id = request.getParameter("id");
        User user = paramRequest.getUser();         
        if(user.hasRole(userRepository.getRole(this.getResourceBase().getAttribute("AdmRol", "")))){
            Annotation a = AnnotationMgr.getInstance().acceptAnnotation(id, user.getId());        
            PrintWriter out = response.getWriter();
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Content-Type", "application/json");
            String s=mapToStringBuilder(annotationToMap(a,user.getId())).toString();
            out.print(s);
        }            
    }
    
    public void doReject(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        String id = request.getParameter("id");
        User user = paramRequest.getUser(); 
        if(user.hasRole(userRepository.getRole(this.getResourceBase().getAttribute("AdmRol", "")))){                        
            Annotation a = AnnotationMgr.getInstance().rejectAnnotation(id);

            PrintWriter out = response.getWriter();
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Content-Type", "application/json");

            out.print(mapToStringBuilder(annotationToMap(a,user.getId())).toString());
        }    
    }
    
    public void doModify(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        String id = request.getParameter("id");
        String bodyValue = request.getParameter("bodyValue");
        User user = paramRequest.getUser(); 
        Annotation a = AnnotationMgr.getInstance().updateAnnotation(id,bodyValue);
      
        PrintWriter out = response.getWriter();
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Content-Type", "application/json");

        out.print(mapToStringBuilder(annotationToMap(a,user.getId())).toString());
    }
    
    public void doDelete(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        String id = request.getParameter("id");
        User user = paramRequest.getUser(); 
        // @todo validar usuario
        AnnotationMgr.getInstance().deleteAnnotation(id);
      
        PrintWriter out = response.getWriter();
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Content-Type", "application/json");

        StringBuilder sb = new StringBuilder();
        sb.append("{\"deleted\":true}");
        out.print(sb.toString());
    } 
    /*private String annotationsToJsonString(List<Annotation> annotationList, String userId){
        
        StringBuilder sb = new StringBuilder("[");
        if(annotationList!=null){                        
            annotationList.forEach((Annotation a)->{
                sb.append(annotationToJsonStringBuilder(a,userId));
                sb.append(",");
            });
            if(',' == sb.charAt(sb.length()-1)){
                sb.deleteCharAt(sb.length()-1);
        }       
    }
        sb.append("]");
System.out.println(sb);        
        return sb.toString();
    }*/
   
   /* public StringBuilder annotationToJsonStringBuilder(Annotation annotation, String userId) {
        SimpleDateFormat sdf= new SimpleDateFormat(DATE_PATTERN);
        StringBuilder sb=new StringBuilder("{");
        if (annotation.getId() != null) {
            sb.append("\"id\":\"").append(annotation.getId()).append("\",");
        }
        if (annotation.getBodyValue() != null) {
            sb.append("\"bodyValue\":\"").append(annotation.getBodyValue().replace("\n","\\n")).append("\",");
        }
        if (annotation.getTarget() != null) {
            sb.append("\"target\":\"").append(annotation.getTarget()).append("\",");
        }
        if (annotation.getCreator() != null && userRepository.getUser(annotation.getCreator())!=null){
            sb.append("\"creator\":\"").append(userRepository.getUser(annotation.getCreator()).getFullName()).append("\",");
        }
        if (annotation.getCreated() != null) {
            sb.append("\"created\":\"").append(sdf.format(annotation.getCreated())).append("\",");
        } 
        if (annotation.getModerator()!= null) {            
            sb.append("\"moderator\":\"").append(annotation.getModerator()).append("\",");
        }      
        if (annotation.getModified()!= null) {
            sb.append("\"modified\":\"").append(annotation.getModified()).append("\",");
        }            
        if (!annotation.isModerated()) { // es modificable
        sb.append("\"isMod\":\"").append(!annotation.isModerated()).append("\",");
        } 
        if (annotation.getCreator().equals(userId)) { 
        sb.append("\"isOwn\":\"").append(true).append("\",");
        }
        if(',' == sb.charAt(sb.length()-1)){
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append("}");
        return sb;
    }*/

    private Entry getEntry(String id) throws IOException {
        String baseUri = getResourceBase().getWebSite().getModelProperty("search_endPoint");
        Entry entry =new Entry();
        if (null == baseUri || baseUri.isEmpty()) {
            baseUri = SWBPlatform.getEnv("rnc/endpointURL").trim();
        }
        String uri = baseUri + "/api/v1/search?q="+id+"&attr=oaiid";
        //String uri = baseUri + "/api/v1/search?identifier="+id;
        //GetBICRequest req = new GetBICRequest(uri);
        //Entry entry = req.makeRequest();       
        ListBICRequest req = new ListBICRequest(uri);        
        Document document = req.makeRequest();
        if(document!=null&&document.getRecords()!=null&&document.getRecords().get(0)!=null){
            entry=document.getRecords().get(0);
        }         
        return entry;
    }
    
    private List<Map<String,String>> annotationsToList(List<Annotation> annotations, String userId){
        List<Map<String,String>> list = new ArrayList<>();
        if(annotations!=null){                        
            annotations.forEach((Annotation a)->{
                list.add(annotationToMap(a, userId));
            });
        }       
        return list;
    } 
    
    private Map<String,String> annotationToMap(Annotation annotation, String userId){
        Map<String,String> map = new HashMap<>();
        SimpleDateFormat sdf= new SimpleDateFormat(DATE_PATTERN);
        if (annotation.getId() != null) {
            map.put("id",annotation.getId());
        }
        if (annotation.getBodyValue() != null) {
            map.put("bodyValue",annotation.getBodyValue());
        }
        if (annotation.getTarget() != null) {
            map.put("target",annotation.getTarget());
        }
        if (annotation.getCreator() != null && userRepository.getUser(annotation.getCreator())!=null){
            map.put("creator",userRepository.getUser(annotation.getCreator()).getFullName());
        }
        if (annotation.getCreated() != null) {
            map.put("created",sdf.format(annotation.getCreated()));
        } 
        if (annotation.getModerator()!= null) {            
            map.put("moderator",annotation.getModerator());
        }      
        if (annotation.getModified()!= null) {
            map.put("modified",annotation.getModified()+"");
        }            
        if (!annotation.isModerated()) { // es modificable
            map.put("isMod",!annotation.isModerated()+"");
        } 
        if (annotation.getCreator().equals(userId)) { 
            map.put("isOwn","true");
        }
        try {
            Entry entry = getEntry(annotation.getTarget());
            if (entry!=null){                
                if(entry.getId()!=null){
                    map.put("oid",entry.getId());
                }    
                if(entry.getRecordtitle()!=null){
                    map.put("bicTitle",entry.getRecordtitle().get(0).getValue());            
                }    
                if(entry.getCreator()!=null){
                    map.put("bicCreator",entry.getCreator().get(0));
                }    
            }
        } catch (IOException ex) {
            LOG.severe(ex.getMessage());
        }
        return map;
    } 
    
    private StringBuilder mapToStringBuilder(Map<String,String> map){
        StringBuilder sb=new StringBuilder("{");
        map.forEach((k,v)->{        
            sb.append("\"");
            sb.append(k);
            sb.append("\":\"");
            sb.append(v.replace("\n","\\n"));
            sb.append("\",");            
        });        
        if(',' == sb.charAt(sb.length()-1)){
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append("}");
        return sb;
    }
    
    /*
    private JsonArray annotationsToJson(List<Annotation> annotationList, UserRepository ur){
        JsonArrayBuilder json = Json.createArrayBuilder();
        if(annotationList!=null){                        
            annotationList.forEach((Annotation a)->{
                //json.add(annotationToJson(a, ur));
            });
        }       
        return json.build();
    }
    /*
    public JsonObject annotationToJson(Annotation annotation, UserRepository ur) {
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        JsonObjectBuilder json = Json.createObjectBuilder();
        if (annotation.getId() != null) {
            json.add("id",annotation.getId());
        }
        if (annotation.getBodyValue() != null) {
            json.add("bodyValue",annotation.getBodyValue());
        }
        if (annotation.getTarget() != null) {
            json.add("target",annotation.getTarget());
        }
        if (annotation.getCreator() != null && ur.getUser(annotation.getCreator())!=null){
            json.add("creator",ur.getUser(annotation.getCreator()).getFullName());
        }
        if (annotation.getCreated() != null) {
            json.add("created",sdf.format(annotation.getCreated()));
        } 
        return json.build();
    }
    */
}
