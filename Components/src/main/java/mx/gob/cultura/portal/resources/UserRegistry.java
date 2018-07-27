package mx.gob.cultura.portal.resources;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.Iterator;
import org.semanticwb.Logger;
import javax.security.auth.Subject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.semanticwb.SWBPlatform;
import org.semanticwb.SWBPortal;
import org.semanticwb.SWBUtils;
import org.semanticwb.model.User;
import org.semanticwb.model.UserRepository;
import org.semanticwb.model.WebPage;
import org.semanticwb.platform.SemanticObject;
import org.semanticwb.portal.SWBSessionObject;
import org.semanticwb.portal.api.GenericAdmResource;
import org.semanticwb.portal.api.SWBActionResponse;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;
import org.semanticwb.portal.api.SWBResourceURL;
import org.semanticwb.portal.api.SWBResourceURLImp;

/**
 * Realiza la visualizacion y cambios en la informacion del perfil de usuarios
 * @author jose.jimenez / rene.jara
 */
public class UserRegistry extends GenericAdmResource {
    
    
    private static final Logger LOG = SWBUtils.getLogger(UserRegistry.class);
    
    public static final String ACTION_REGISTER = "creating";
    public static final String REGISTER_ACTION = "creating";//legacy
        
    public static final String REGISTER_MODE = "confirmRegistry";
    
    public static final String ACTION_BE_ANNOTATOR = "beannotator";
    
    public static final String ACTION_CONFIRMING = "confirming";
    
    private String confirmationActionUrl = null;

    private static final String DATACRED_URI =
            "http://www.semanticwebbuilder.org/swb4/ontology#plainCredential";
    public static final String ORGANITATION_NAME_URI =
            "http://www.semanticwebbuilder.org/swb4/ontology#organitationName";
    public static final String POSITION_TITLE_URI =
            "http://www.semanticwebbuilder.org/swb4/ontology#positionTitle";
    public static final String TELEPHONE_NUMBER_URI =
            "http://www.semanticwebbuilder.org/swb4/ontology#telephoneNumber";
    public static final String AREA_INTEREST_URI =
            "http://www.semanticwebbuilder.org/swb4/ontology#areaInterest";
    
    private UserRepository userRepository;
      
    @Override
    public void init() throws SWBResourceException {
        super.init(); 
        userRepository=getResourceBase().getWebSite().getUserRepository();   
    }
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramsRequest) throws IOException {
        
        //String url = "/swbadmin/jsp/rnc/userRegistry.jsp";
        String url = "/swbadmin/jsp/rnc/"+this.getClass().getSimpleName()+"/view.jsp";
        User user = paramsRequest.getUser();
        boolean isAnnotator= false;
        if (user!=null && user.isSigned()){
            isAnnotator=user.hasRole(userRepository.getRole(this.getResourceBase().getAttribute("AnnRol", "")));          
        }
        RequestDispatcher rd = request.getRequestDispatcher(url);
        try {
            request.setAttribute("paramsRequest", paramsRequest);
            request.setAttribute("isAnnotator", isAnnotator);
            rd.include(request, response);
        } catch(ServletException se) {
            LOG.error(se.getMessage());
        }
/*        if (null == this.confirmationActionUrl) {
            String serverUrl = request.getScheme() + "://" + request.getServerName() + ((request.getServerPort() != 80)? (":" + request.getServerPort()) : "");
            this.confirmationActionUrl = serverUrl + paramsRequest.getActionUrl().setAction("confirming").toString();
        }*/
    }

    @Override
    public void processAction(HttpServletRequest request, SWBActionResponse response)
            throws SWBResourceException, IOException {

        String action = response.getAction();
//System.out.println("-----------------------------------------ProcessAction:"+action);        
        String alert = null;
        String nextMode = null;
        System.out.println("Referer: " + request.getHeader("Referer"));
        
        if (ACTION_REGISTER.equals(action)) {
            User created = this.createProfile(request);
            if (null == created) {
                response.setRenderParameter("condition",
                        (String) request.getAttribute("condition"));
                System.out.println("USUARIO NO CREADO");
            } else {
                if (null == this.confirmationActionUrl) {
                    String serverUrl = request.getScheme() + "://" + request.getServerName() + ((request.getServerPort() != 80)? (":" + request.getServerPort()) : "");
                    SWBResourceURLImp urlAcc = new SWBResourceURLImp(request, this.getResourceBase(), response.getWebPage(), SWBResourceURL.UrlType_ACTION);
                    this.confirmationActionUrl = serverUrl + urlAcc.setAction("confirming").toString();
                }               
                this.sendConfirmationEmail(created);
                System.out.println("Se creo, el usuario  >>>");
                System.out.println(request.getParameter(ACTION_BE_ANNOTATOR));
                if (request.getParameter(ACTION_BE_ANNOTATOR)!=null){
                    this.sendBeAnnotatorEmail(created);
                }
            }
            nextMode = UserRegistry.REGISTER_MODE;
        }else if (response.Action_EDIT.equals(action)) {
            User updated = this.updateProfile(request, response.getUser());
            if (null == updated) {
                response.setRenderParameter("condition",
                        (String) request.getAttribute("condition"));
                System.out.println("USUARIO NO actualizado");
            } else {             
                if (request.getParameter(ACTION_BE_ANNOTATOR)!=null){
                    this.sendBeAnnotatorEmail(updated);
                }
            }
            nextMode = response.Mode_VIEW;
        }else if (ACTION_CONFIRMING.equals(action)) {
            User user = this.activateUser(request);
            StringBuilder credential = new StringBuilder(16);
            
            if (null != user) {
                if (SWBPlatform.getSecValues().isMultiple()) {
                    String login = user.getLogin();
                     Iterator<SWBSessionObject> llist =SWBPortal.getUserMgr().listSessionObjects();
                     while(llist.hasNext()) {
                         SWBSessionObject so = llist.next();
                         Iterator<Principal> lpri = so.getSubjectByUserRep(userRepository.getId()).getPrincipals().iterator();
                         if (lpri.hasNext() && login.equalsIgnoreCase(((User)lpri.next()).getLogin())) {
                             alert = "msg_alreadyLoggedin";
                         }
                     }
                }
                
                try {
                    OntModel ont = SWBPlatform.getSemanticMgr().getSchema().getRDFOntModel();
                    credential.append(user.getSemanticObject().getRDFResource()
                        .getProperty(ont.createDatatypeProperty(
                                UserRegistry.DATACRED_URI)).getString());

                    user.checkCredential(credential.toString().toCharArray());
                    Subject subject = SWBPortal.getUserMgr().getSubject(request,
                                        response.getWebPage().getWebSiteId());
                    subject.getPrincipals().clear();
                    subject.getPrincipals().add(user);
                    if (null == user.getLanguage()) {
                        user.setLanguage("es");   //forzar lenguaje si no se dio de alta.
                    }
                    user.getSemanticObject().getRDFResource().
                            removeAll(ont.createDatatypeProperty(UserRegistry.DATACRED_URI));
                } catch (Exception ex) {
                    LOG.error("Actualizando usuario",ex);
                }
            
            }
            
            if (null != alert) {
                response.setRenderParameter("condition", alert);
            }
//            if (user.isSigned()) {
                nextMode = "homeRedirect";
//            }
        } 
//System.out.println("termino");        
        response.setMode(nextMode);
    }

    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        
        if (paramRequest.getMode().equals("homeRedirect")) {
            redirect2Home(request, response, paramRequest);
        } else if (paramRequest.getMode().equals("confirmRegistry")) {
            confirmRegistry(request, response, paramRequest);
        } else {
            super.processRequest(request, response, paramRequest);
        }
    }
    
    private void redirect2Home(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        
        try {
            WebPage home = WebPage.ClassMgr.getWebPage("home", paramRequest.getWebPage().getWebSite());
            String url = home.getRealUrl(paramRequest.getUser().getLanguage());
            response.setContentType("Text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><head><meta http-equiv=\"Refresh\" CONTENT=\"0; URL=" +
                    url + "\" /><script>alert(\"" + paramRequest.getLocaleString("msg_welcome") +
                    "\");window.location='" + url +
                    "';</script></head></html>");
            out.flush();
        } catch (IOException e) {
            LOG.error("Redirecting user", e);
        }
    }
    
    private void confirmRegistry(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramsRequest) throws SWBResourceException, IOException {
        
        try {
            response.setContentType("Text/html");
            PrintWriter out = response.getWriter();
            if (request.getParameter("condition") == null || request.getParameter("condition").isEmpty()) {
                out.print(paramsRequest.getLocaleString("msgSent"));
            } else {
                out.print(paramsRequest.getLocaleString(request.getParameter("condition")));
            }
            out.flush();
        } catch (IOException ioe) {
            LOG.error("Confirming registry", ioe);
        }
    }
    private User createProfile(HttpServletRequest request) {
//System.out.println("createProfile");        
        User newUser = null;
        String firstName = request.getParameter("usrFirstName");
        String lastName = request.getParameter("usrLastName");
        String email = request.getParameter("email");
        String password = request.getParameter("pass2");        
        String passwordConfirm = request.getParameter("passConf");
        String termsPrivacy = request.getParameter("termsPrivacy");
        String condition = null;
        //anotador
        String organ = request.getParameter("usrOrg");
        String title = request.getParameter("usrTitle");    
        String inter = request.getParameter("usrInter");
        String tel = request.getParameter("usrTel");  
        //boolean profCreated = true;
//System.out.println("1");          
        if (null != password && null != email && !password.isEmpty() &&
                password.equals(passwordConfirm) && !email.isEmpty() &&
                null!=termsPrivacy && termsPrivacy.equals("true")) {
            
            User user = userRepository.getUserByLogin(email);
//System.out.println("2");              
            if (null == user) {
                //try {
//System.out.println("3");                      
                    //String encryptdPwd;
                    //encryptdPwd = SWBUtils.CryptoWrapper.passwordDigest(password);
                    newUser = userRepository.createUser();
                    newUser.setLogin(email);
                    newUser.setPassword(password);  //encryptdPwd
                    newUser.setLanguage("es");
                    if (firstName!=null){
                        newUser.setFirstName(firstName);
                    }    
                    if (lastName!=null){
                        newUser.setLastName(lastName);
                    }
                    newUser.setEmail(email);
//System.out.println("4");                      
                    System.out.println("Contraseña para nuevo usuario: " + password);
                    //newUser.setActive(true);
                    SemanticObject obj = newUser.getSemanticObject();
                    OntModel ont = SWBPlatform.getSemanticMgr().getSchema().getRDFOntModel();
                    obj.getRDFResource().addLiteral(ont.createDatatypeProperty(
                        UserRegistry.DATACRED_URI), password);
                    if(organ!=null){
                        obj.getRDFResource().addLiteral(ont.createDatatypeProperty(
                            UserRegistry.ORGANITATION_NAME_URI),organ);
                    }
                    if(title!=null){
                        obj.getRDFResource().addLiteral(ont.createDatatypeProperty(
                            UserRegistry.POSITION_TITLE_URI),title);
                    }
                    if(inter!=null){
                        obj.getRDFResource().addLiteral(ont.createDatatypeProperty(
                            UserRegistry.AREA_INTEREST_URI),inter);
                    }
                    if(tel!=null){
                        obj.getRDFResource().addLiteral(ont.createDatatypeProperty(
                            UserRegistry.TELEPHONE_NUMBER_URI),tel);
                    }
//System.out.println("4");                      
                //} catch (Exception e) {
//System.out.println("5");                      
                //    condition = "msg_encryptingError";
                //}
            } else {
//System.out.println("5");                  
                condition = "msgUserExists";
            }
        } else if (null != password && null != passwordConfirm && !password.equals(passwordConfirm)) {
//System.out.println("6");              
            condition = "msg_pwdMismatch";
        }
        if (null != newUser || null != condition) {
//System.out.println("7");              
            request.setAttribute("condition", condition);
        }
//System.out.println("8 condition:"+condition);
        return newUser;
    }
    
    private User updateProfile(HttpServletRequest request,User user) {
//System.out.println("updateProfile");        
        
        String firstName = request.getParameter("usrFirstName");
        String lastName = request.getParameter("usrLastName");
        String email = request.getParameter("email");
        String password = request.getParameter("pass2");        
        String passwordConfirm = request.getParameter("passConf");
        //String termsPrivacy = request.getParameter("termsPrivacy");
        String condition = null;
        //anotador
        String organ = request.getParameter("usrOrg");
        String title = request.getParameter("usrTitle");    
        String inter = request.getParameter("usrInter");
        String tel = request.getParameter("usrTel");  
        //boolean profCreated = true;
//System.out.println("1"+firstName+","+lastName+","+email+","+password+","+passwordConfirm+","+organ+","+title+","+inter+","+tel);  
        
        if (null != email &&!email.isEmpty() &&
                null != firstName &&!firstName.isEmpty() &&
                null != lastName &&!lastName.isEmpty()){
                
                /*&& null != password 
                null != password && null != email && !password.isEmpty() &&
                password.equals(passwordConfirm) &&  && termsPrivacy.equals("true")) {*/
            
//System.out.println("2");              
//System.out.println("3");                      
                    //String encryptdPwd;
                    //encryptdPwd = SWBUtils.CryptoWrapper.passwordDigest(password);
                    //user.setLogin(email);
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setEmail(email);
                     //encryptdPwd
                    //user.setLanguage("es");
                    
//System.out.println("4");                      
                    
                    //newUser.setActive(true);
                    SemanticObject obj = user.getSemanticObject();
                    OntModel ont = SWBPlatform.getSemanticMgr().getSchema().getRDFOntModel();
                    
                    if (null != password &&  !password.isEmpty() && password.equals(passwordConfirm)){
                        user.setPassword(password);
                        System.out.println("Contraseña para nuevo usuario: " + password);
                        
                        Property prprt=ont.createDatatypeProperty(UserRegistry.DATACRED_URI);
                        user.getSemanticObject().getRDFResource().removeAll(prprt);                        
                        obj.getRDFResource().addLiteral(prprt, password);
                    }    
                    if(organ!=null){
                        Property prprt=ont.createDatatypeProperty(UserRegistry.ORGANITATION_NAME_URI);
                        user.getSemanticObject().getRDFResource().removeAll(prprt);                        
                        obj.getRDFResource().addLiteral(prprt, organ);
                    }
                    if(title!=null){
                        Property prprt=ont.createDatatypeProperty(UserRegistry.POSITION_TITLE_URI);
                        user.getSemanticObject().getRDFResource().removeAll(prprt);                        
                        obj.getRDFResource().addLiteral(prprt,title);
                    }
                    if(inter!=null){
                        Property prprt=ont.createDatatypeProperty(UserRegistry.AREA_INTEREST_URI);
                        user.getSemanticObject().getRDFResource().removeAll(prprt);                        
                        obj.getRDFResource().addLiteral(prprt, inter);
                    }
                    if(tel!=null){
                        Property prprt=ont.createDatatypeProperty(UserRegistry.TELEPHONE_NUMBER_URI);
                        user.getSemanticObject().getRDFResource().removeAll(prprt);                        
                        obj.getRDFResource().addLiteral(prprt,tel );
                    }                     
//System.out.println("5");                      
        } else {
            condition = "msg_missingData";
        }
        if (null != user || null != condition) {
//System.out.println("7");              
            request.setAttribute("condition", condition);
        }
//System.out.println("8 condition:"+condition);
        return user;
    }
    
    private void sendConfirmationEmail(User user) {
        
        StringBuilder body = new StringBuilder(256);
        StringBuilder linkUrl = new StringBuilder(128);
        boolean noProblem = false;
        
        body.append("Estimad@ ");
        body.append(user.getName());
        body.append("\nPara completar el registro de usuario en el Repositotio Digital del Patrimonio Cultural Nacional, ");
        body.append("es necesario que confirmes tu cuenta de correo, haciendo clic en la siguiente liga: \n");
        body.append("<a href=\"");
        
        linkUrl.append(this.confirmationActionUrl);
        linkUrl.append("?account=");
        try {
            linkUrl.append(URLEncoder.encode(user.getEmail(), "UTF-8"));
            linkUrl.append("&thru=");
            linkUrl.append(
                    URLEncoder.encode(SWBUtils.CryptoWrapper.passwordDigest(user.getEmail() +
                    Long.toString(user.getCreated().getTime())), "UTF-8"));
            noProblem = true;
        } catch(Exception e) {
            LOG.error("Error al encriptar parametro en correo");
        }
        
        System.out.println("confirmation mail:\n" + linkUrl);
        body.append(linkUrl);
        body.append("\">Confirmar registro</a>\n");
        body.append("Si no funciona el v&iacute;nculo anterior, por favor ");
        body.append("copia la siguiente ruta en tu navegador para confirmar tu registro:\n\n");
        body.append(linkUrl);
        body.append("\n\n¡Por tu atenci&oacute;n, muchas gracias!");
        //body.append("");
        
        try {
            if (noProblem) {
                SWBUtils.EMAIL.sendBGEmail(user.getEmail(),
                        "Confirmacion de registro de usuario",
                        body.toString());
            }
        } catch (SocketException se) {
            LOG.error("Enviando el correo de confirmacion del registro");
        }
    }
    private void sendBeAnnotatorEmail(User user) {
System.out.println("sendBeAnnotatorEmail");        
        StringBuilder body = new StringBuilder(256);
        StringBuilder linkUrl = new StringBuilder(128);
        //boolean noProblem = false;
        
        OntModel ont = SWBPlatform.getSemanticMgr().getSchema().getRDFOntModel();
        body.append("El usuario ");
        body.append(user.getName());
        body.append("\n");
        body.append(user.getSemanticObject().getRDFResource().getProperty(ont.createDatatypeProperty(UserRegistry.POSITION_TITLE_URI)).getString());
        body.append("\n");
        body.append(user.getSemanticObject().getRDFResource().getProperty(ont.createDatatypeProperty(UserRegistry.ORGANITATION_NAME_URI)).getString());
        body.append("\n desea ser anotador");
        
        try {
        //    if (noProblem) {
                SWBUtils.EMAIL.sendBGEmail(this.getResourceBase().getAttribute("email", "admnl@cultura.gob.mx"),
                        "Usuario desea ser anotador",
                        body.toString());
        //    }
        } catch (SocketException se) {
            LOG.error("Usuario desea ser anotador");
        }
    }
    /**
     * Activa el registro del usuario, siempre que la cuenta de correo proporcionada
     * corresponda con el dato de validacion contenido en el hash recibido
     * @param request la peticion del usuario para validar su cuenta de correo y 
     *        activar el registro del usuario
     * @param userRepo el repositorio de usuarios en que se encuentra el registro del usuario
     * @return true, siempre que se verifique que la cuenta de correo indicada esta 
     *         asociada a un usuario registrado, y que la peticion contiene datos validos.
     */
    private User activateUser(HttpServletRequest request) {
        
        String eMail = request.getParameter("account");
        String hash = request.getParameter("thru");
        User fulanito = null;
        
        if (null != eMail && null != hash && !eMail.isEmpty() && !hash.isEmpty()) {
            fulanito = userRepository.getUserByLogin(eMail);
            if (null != fulanito) {
                String legitHash = null;
                try {
                    legitHash = SWBUtils.CryptoWrapper.passwordDigest(fulanito.getEmail() +
                            Long.toString(fulanito.getCreated().getTime()));
                } catch(Exception e) {
                    LOG.error("Error al encriptar parametro para activar usuario");
                }
                if (hash.equals(legitHash)) {
                    fulanito.setActive(true);
                } else {
                    fulanito = null;
                }
            }
        }
        return fulanito;
    }
}
