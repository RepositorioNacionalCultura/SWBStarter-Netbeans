/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.resources;

import java.net.SocketException;
import org.semanticwb.SWBUtils;
import org.semanticwb.model.Role;
import org.semanticwb.model.WebSite;
import org.semanticwb.model.SWBContext;
import org.semanticwb.base.SWBAppObject;

import org.semanticwb.model.User;
import org.semanticwb.model.UserRepository;

import org.semanticwb.platform.SemanticObject;
import org.semanticwb.platform.SemanticObserver;
import org.semanticwb.platform.SemanticProperty;

/**
 *
 * @author rene.jara
 */
public class AnnotatorObserver implements SWBAppObject {

    private static final org.semanticwb.Logger LOG = SWBUtils.getLogger(AnnotatorObserver.class);
    
    @Override
    public void init() {
        User.sclass.registerObserver(new SemanticObserver() {
            @Override
            public void notify(SemanticObject obj, Object prop, String lang, String action) {

                if (SemanticObject.ACT_ADD.equals(action) && obj.getGenericInstance() instanceof User && prop instanceof SemanticProperty 
                        && "hasRole".equals(((SemanticProperty)prop).getName())) {
                    
                    User user = (User)obj.getGenericInstance();
                    String siteid = null != user.getUserRepository() ? user.getUserRepository().getParentWebSite().getId() : "repositorio";
                    WebSite wsite = SWBContext.getWebSite(siteid);
                    UserRepository urep = wsite.getUserRepository();
                    String roleId = wsite.getModelProperty("annotator_role_id");
                    String body = wsite.getModelProperty("annotator_mail_msg");
                    String subject = wsite.getModelProperty("annotator_mail_sub");
                    if (roleId == null) roleId = "Anotador";
                    if (subject == null) subject = "Bienvenid@ como anotador";
                    if (body == null) body = "Estimad@ <br> {username} <br> has sido aceptad@ como anotador del Repositotio Digital del Patrimonio Cultural Nacional";
                        
                    Role role = urep.getRole(roleId);
                    body = body.replace("{username}", user.getFullName());
                    if (user.hasRole(role)) {
                        try {
                            SWBUtils.EMAIL.sendBGEmail(user.getEmail(), subject, body);
                        } catch (SocketException ex) {
                            LOG.error("Error al enviar correo de aceptacion como anotador", ex);
                        }
                    }
                }
            }
        }); 
    }

    @Override
    public void destroy() {
        //System.out.println("destroy");
    }

    @Override
    public void refresh() {
        //System.out.println("refresh");
    }
    
}
