/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.resources;

import java.net.SocketException;
import org.semanticwb.SWBUtils;
import org.semanticwb.base.SWBAppObject;
import org.semanticwb.model.Role;
import org.semanticwb.model.SWBContext;
import org.semanticwb.model.User;
import org.semanticwb.model.UserRepository;
import org.semanticwb.model.WebSite;
import org.semanticwb.platform.SemanticObject;
import org.semanticwb.platform.SemanticObserver;
import org.semanticwb.platform.SemanticProperty;

/**
 *
 * @author rene.jara
 */
public class AnnotatorObserver implements SWBAppObject{

    private static final org.semanticwb.Logger LOG = SWBUtils.getLogger(AnnotatorObserver.class);
    
    @Override
    public void init() {
        User.sclass.registerObserver(new SemanticObserver() 
        {
            @Override
            public void notify(SemanticObject obj, Object prop, String lang, String action)
            {  
                //System.out.println("notify:obj:"+obj+" prop:"+prop+" lang:"+lang+" action:"+action);
                //notify:obj:http://user.repositorio.swb#swb_User:13 prop:http://www.semanticwebbuilder.org/swb4/ontology#hasRole action:ADD
                
                if(SemanticObject.ACT_ADD.equals(action) && 
                        obj.getGenericInstance() instanceof User && 
                        prop instanceof SemanticProperty && 
                        "hasRole".equals(((SemanticProperty)prop).getName())){
                    
                    
                    User user=(User)obj.getGenericInstance();
                    WebSite wsite=SWBContext.getWebSite("repositorio");
                    UserRepository urep=wsite.getUserRepository();
                    String roleId=wsite.getModelProperty("annotator_role_id");
                    if(roleId==null){
                        roleId="Anotador";
                    }
                    Role role=urep.getRole(roleId);
                    if(user.hasRole(role)){
                        try {
                            SWBUtils.EMAIL.sendBGEmail(user.getEmail(), "Bienvenid@ como anotador", "Estimad@ \n "+user.getFullName()+"\n has sido aceptad@ como anotador del Repositotio Digital del Patrimonio Cultural Nacional");
                        } catch (SocketException ex) {
                            LOG.error("Error al enviar correo de aceptacion como anotador", ex);
                        }
                                
                    }
                }
/*                
                
                if(obj.getGenericInstance() instanceof User){
                    System.out.println("Obvio!!!");
                    User user=(User)obj.getGenericInstance();
                    System.out.println(user.getFullName());
                }
                SemanticProperty swb_hasRole=SWBPlatform.getSemanticMgr().getVocabulary().getSemanticProperty("http://www.semanticwebbuilder.org/swb4/ontology#hasRole");
                if(prop instanceof SemanticProperty){
                    System.out.println("Obvio!!!");
                    SemanticProperty semanticProperty=(SemanticProperty)prop;
                    System.out.println(semanticProperty.getName());
                    System.out.println(semanticProperty.getSemanticObject());                    
                    for(Statement st:semanticProperty.getRDFProperty().listProperties().toList()){
                        System.out.println(st);
                        //System.out.println(st.getLiteral());                                       
                        //System.out.println(st.getString());                    
                    }
                    System.out.println();
                }                
                if("ADD".equals(action)){
                    System.out.println(obj.getClass());
                    System.out.println(prop.getClass());
                }
*/
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
