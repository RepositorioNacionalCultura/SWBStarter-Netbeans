package mx.gob.cultura.portal.resources;

import com.hp.hpl.jena.ontology.OntModel;
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
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBActionResponse;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;

/**
 * Realiza la visualizacion y cambios en la informacion del perfil de usuarios
 * @author jose.jimenez
 */
public class UserRegistry extends GenericResource {
    
    
    private static final Logger LOG = SWBUtils.getLogger(UserRegistry.class);
    
    public static final String REGISTER_ACTION = "creating";
    
    public static final String REGISTER_MODE = "confirmRegistry";
    
    private String confirmationActionUrl = null;

    private static final String DATACRED_URI =
            "http://www.semanticwebbuilder.org/swb4/ontology#plainCredential";
    
    public void doView(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramsRequest) throws IOException {
        
        String url = "/swbadmin/jsp/rnc/userRegistry.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(url);
        try {
            request.setAttribute("paramsRequest", paramsRequest);
            rd.include(request, response);
        } catch(ServletException se) {
            UserRegistry.LOG.info(se.getMessage());
        }
        if (null == this.confirmationActionUrl) {
            String serverUrl = request.getRequestURL().substring(0, request.getRequestURL().indexOf("/", 8));
            this.confirmationActionUrl = serverUrl + paramsRequest.getActionUrl().setAction("confirming").toString();
        }
    }

    @Override
    public void processAction(HttpServletRequest request, SWBActionResponse response)
            throws SWBResourceException, IOException {
        
        String action = response.getAction();
        String alert = null;
        UserRepository userRepo = response.getWebPage().getWebSite().getUserRepository();
        String nextMode = null;
        System.out.println("Referer: " + request.getHeader("Referer"));
        
        if (UserRegistry.REGISTER_ACTION.equals(action)) {
            User created = this.createProfile(request, userRepo);
            if (null == created) {
                response.setRenderParameter("condition",
                        (String) request.getAttribute("condition"));
                System.out.println("USUARIO NO CREADO");
            } else {
                this.sendConfirmationEmail(created);
                System.out.println("Se creo, el usuario  >>>");
            }
            nextMode = UserRegistry.REGISTER_MODE;
        } else if ("confirming".equals(action)) {
            User user = this.activateUser(request, userRepo);
            StringBuilder credential = new StringBuilder(16);
            
            if (null != user) {
                if (SWBPlatform.getSecValues().isMultiple()) {
                    String login = user.getLogin();
                     Iterator<SWBSessionObject> llist =SWBPortal.getUserMgr().listSessionObjects();
                     while(llist.hasNext()) {
                         SWBSessionObject so = llist.next();
                         Iterator<Principal> lpri = so.getSubjectByUserRep(userRepo.getId()).getPrincipals().iterator();
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
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            
            }
            
            if (null != alert) {
                response.setRenderParameter("condition", alert);
            }
//            if (user.isSigned()) {
                nextMode = "homeRedirect";
//            }
        }
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
            UserRegistry.LOG.error("Redirecting user", e);
        }
    }
    
    private void confirmRegistry(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        
        try {
            response.setContentType("Text/html");
            PrintWriter out = response.getWriter();
            if (request.getParameter("condition") == null || request.getParameter("condition").isEmpty()) {
                out.print("msgSent");
            } else {
                out.print(request.getParameter("condition"));
            }
            out.flush();
        } catch (IOException ioe) {
            UserRegistry.LOG.error("Confirming registry", ioe);
        }
    }
    
    private User createProfile(HttpServletRequest request, UserRepository userRepo) {
        
        User newUser = null;
        String name = request.getParameter("usrname");
        String lastName = request.getParameter("usrLastName");
        String email = request.getParameter("email");
        String password = request.getParameter("pass2");
        String passwordConfirm = request.getParameter("passConf");
        String termsPrivacy = request.getParameter("termsPrivacy");
        String condition = null;
        //boolean profCreated = true;
        
        if (null != password && null != email && !password.isEmpty() &&
                password.equals(passwordConfirm) && !email.isEmpty() && termsPrivacy.equals("true")) {
            
            User user = userRepo.getUserByLogin(email);
            
            if (null == user) {
                try {
                    String encryptdPwd;
                    encryptdPwd = SWBUtils.CryptoWrapper.passwordDigest(password);
                    newUser = userRepo.createUser();
                    newUser.setLogin(email);
                    newUser.setPassword(password);  //encryptdPwd
                    newUser.setLanguage("es");
                    //newUser.setFirstName(name);
                    //newUser.setLastName(lastName);
                    newUser.setEmail(email);
                    System.out.println("Contraseña para nuevo usuario: " + password);
                    //newUser.setActive(true);
                    SemanticObject obj = newUser.getSemanticObject();
                    OntModel ont = SWBPlatform.getSemanticMgr().getSchema().getRDFOntModel();
                    obj.getRDFResource().addLiteral(ont.createDatatypeProperty(
                        UserRegistry.DATACRED_URI), password);
                } catch (Exception e) {
                    condition = "msg_encryptingError";
                }
            } else {
                condition = "msg_userExists";
            }
        } else if (null != password && null != passwordConfirm && !password.equals(passwordConfirm)) {
            condition = "msg_pwdMismatch";
        }
        if (null != newUser || null != condition) {
            request.setAttribute("condition", condition);
        }
        return newUser;
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
            UserRegistry.LOG.error("Error al encriptar parametro en correo");
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
            UserRegistry.LOG.error("Enviando el correo de confirmacion del registro");
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
    private User activateUser(HttpServletRequest request, UserRepository userRepo) {
        
        String eMail = request.getParameter("account");
        String hash = request.getParameter("thru");
        User fulanito = null;
        
        if (null != eMail && null != hash && !eMail.isEmpty() && !hash.isEmpty()) {
            fulanito = userRepo.getUserByLogin(eMail);
            if (null != fulanito) {
                String legitHash = null;
                try {
                    legitHash = SWBUtils.CryptoWrapper.passwordDigest(fulanito.getEmail() +
                            Long.toString(fulanito.getCreated().getTime()));
                } catch(Exception e) {
                    UserRegistry.LOG.error("Error al encriptar parametro para activar usuario");
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
