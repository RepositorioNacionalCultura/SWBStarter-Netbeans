/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.repository.portal.resources;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.semanticwb.SWBUtils;
import org.semanticwb.portal.api.GenericAdmResource;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;

/**
 *
 * @author rene.jara
 */
public class SubscribeNewsletter extends GenericAdmResource{
    public static final Logger LOG = Logger.getLogger(SubscribeNewsletter.class.getName());
    public static final String ASYNC_SUBSCRIBE = "sub";

    
    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        String mode = paramRequest.getMode();
        if (ASYNC_SUBSCRIBE.equals(mode)) {
            doSub(request, response, paramRequest);
        } else {
            super.processRequest(request, response, paramRequest);
        }        
    }

    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        //User user = paramRequest.getUser();
        response.setContentType("text/html; charset=UTF-8");
        //String path = "/swbadmin/jsp/rnc/"+this.getClass().getSimpleName()+"/view.jsp";
        String path = "/work/models/" + paramRequest.getWebPage().getWebSite().getId() + "/jsp/rnc/" + this.getClass().getSimpleName()+"/view.jsp";
        RequestDispatcher dis = request.getRequestDispatcher(path);
        try {
            request.setAttribute("paramRequest", paramRequest);
            dis.include(request, response);
        } catch (ServletException se) {
            LOG.severe(se.getMessage());
        }        
    }
    public void doSub(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {    
        String email = request.getParameter("email");
      
        PrintWriter out = response.getWriter();
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Content-Type", "application/json");
        
//System.out.println("email:"+email);
        if(SWBUtils.EMAIL.isValidEmailAddress(email)){  
//System.out.println("intentandolo:");                        
            SWBUtils.EMAIL.sendBGEmail(email,this.getResourceBase().getAttribute("emailSubAck", "alta noticias"),this.getResourceBase().getAttribute("emailMsgAck", "recibiras las noticias"));
            SWBUtils.EMAIL.sendBGEmail(this.getResourceBase().getAttribute("email", "admnl@cultura.gob.mx"),this.getResourceBase().getAttribute("emailSubReq", "alta newsletter"),email+this.getResourceBase().getAttribute("emailMsgReq", " desea recibir el newsletter"));
            out.print("{\"subscribed\":true}");
//System.out.println("mandado:");        
        }else{ 
//System.out.println("formato invalido:");            
            out.print("{\"subscribed\":false}");
        }    
    }
  
}
